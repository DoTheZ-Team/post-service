package com.justdo.plug.post.domain.post.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justdo.plug.post.domain.hashtag.service.HashtagService;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.post.dto.PostRequestDto;
import com.justdo.plug.post.domain.post.dto.PostResponseDto;
import com.justdo.plug.post.domain.post.dto.PostSearchDTO;
import com.justdo.plug.post.domain.post.dto.PreviewResponse;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.PostItemList;
import com.justdo.plug.post.domain.post.repository.PostRepository;
import com.justdo.plug.post.domain.posthashtag.PostHashtag;
import com.justdo.plug.post.domain.posthashtag.service.PostHashtagService;
import com.justdo.plug.post.elastic.PostDocument;
import com.justdo.plug.post.elastic.PostElasticsearchRepository;
import com.justdo.plug.post.global.exception.ApiException;
import com.justdo.plug.post.global.response.code.status.ErrorStatus;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostHashtagService postHashtagService;
    private final HashtagService hashtagService;
    private final PostElasticsearchRepository postElasticsearchRepository;

    @Value("${spring.elasticsearch.uris}")
    private String url;

    @Value("${elasticsearch.api-key}")
    private String apiKey;

    // BLOG001: 게시글 리스트 조회
    public List<Post> getAllPosts() {

        //return postRepository.findAll();
        return null;

    }

    // BLOG002: 게시글 상세 페이지 조회
    public PostResponseDto getPostById(Long postId) throws JSONException {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ApiException(ErrorStatus._POST_NOT_FOUND));

        return PostResponseDto.createFromPost(post);
    }

    // BLOG003: 블로그 작성
    public Post save(PostRequestDto requestDto) throws JsonProcessingException {
        Post post = requestDto.toEntity();

        String url = "https://e69e033e6b5f461db0d97431ac9ce409.es.us-east-1.aws.elastic.cloud:443/post/_doc";
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(requestDto.getContent());
        jsonString = jsonString.substring(1, jsonString.length() - 1);
        System.out.println(jsonString);
        List<String> hashtags = requestDto.getHashtags();
        StringBuilder hashtagsJson = new StringBuilder("[");

        for (int i = 0; i < hashtags.size(); i++) {
            String hashtag = "\"" + hashtags.get(i) + "\"";
            hashtagsJson.append(hashtag);
            if (i < hashtags.size() - 1) {
                hashtagsJson.append(",");
            }
        }
        hashtagsJson.append("]");

        String jsonBody = "{\n" +
            "    \"title\": \"" + requestDto.getTitle() + "\",\n" +
            "    \"content\": \"" + jsonString + "\",\n" +
            "    \"memberId\": " + requestDto.getMemberId() + ",\n" +
            "    \"hashtags\": " + hashtagsJson + ",\n" +
            "    \"name\": \"" + requestDto.getName() + "\",\n" +
            "    \"photo_url\": \"" + requestDto.getPhotoUrl() + "\"\n" +
            "}";

        System.out.println(jsonBody);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .setHeader("Authorization",
                "ApiKey alI1LVVJOEI3eGJfdmZvUkMxQWQ6MHp2MHJXQ0VSMk85bXdNVGlrLWgxZw==")
            .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

            String ResponseBody = response.body();
            System.out.println(ResponseBody);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(ResponseBody);

            String id = jsonNode.get("_id").asText();
            post.setEsId(id);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(post.getEsId());
        return postRepository.save(post);
    }

    // BLOG006: 블로그 게시글 리스트 조회
    public List<Post> getBlogPosts(Long blogId) {
        List<Post> posts = postRepository.findByBlogId(blogId);
        if (posts.isEmpty()) {
            throw new ApiException(ErrorStatus._BLOG_NOT_FOUND);
        }
        return posts;
    }

    // BLOG007: 해시태그 값 추출
    public List<String> getHashtags(Long memberId) {
        // 멤버 아이디에 해당하는 포스트를 가져온다.
        List<Post> memberPosts = postRepository.findByMemberId(memberId);

        // 멤버 아이디에 해당하는 포스트가 없는 경우
        if (memberPosts.isEmpty()) {
            throw new ApiException(ErrorStatus._NO_HASHTAGS);
        }

        // 멤버 아이디에 해당하는 포스트의 아이디만 추출하여 반환
        List<Long> postIds = memberPosts.stream()
            .map(Post::getId)
            .toList();

        List<String> hashtagNames = new ArrayList<>();

        // 각 포스트별로 해당하는 해시태그 아이디를 추출하여 저장
        for (Long postId : postIds) {
            List<PostHashtag> postHashtags;
            postHashtags = postHashtagService.getPostHashtags(postId);

            for (PostHashtag postHashtag : postHashtags) {
                // 아이디에서 해시태그 명으로 변경 후 리스트에 저장
                String hashtagName = hashtagService.getHashtagNameById(postHashtag.getHashtagId());
                hashtagNames.add(hashtagName);
            }
        }

        return hashtagNames;
    }

    // BlOG008: 게시글의 글만 조회하기
    public String getPreviewPost(Long postId) throws JsonProcessingException {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ApiException(ErrorStatus._POST_NOT_FOUND));

        String preview = post.getContent();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonArray = mapper.readTree(preview);

        StringBuilder extractedTexts = new StringBuilder();
        for (JsonNode node : jsonArray) {
            JsonNode contentArray = node.path("content");
            if (contentArray.isArray()) {
                for (JsonNode contentObj : contentArray) {
                    if (contentObj.isObject()) {
                        String text = contentObj.path("text").asText();
                        extractedTexts.append(text).append(" ");
                    }
                }
            }
        }

        return extractedTexts.toString().trim();
    }

    // 게시글의 preview 값 저장
    public String savePreviewPost(String content) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonArray = mapper.readTree(content);

        StringBuilder extractedTexts = new StringBuilder();
        for (JsonNode node : jsonArray) {
            JsonNode contentArray = node.path("content");
            if (contentArray.isArray()) {
                for (JsonNode contentObj : contentArray) {
                    if (contentObj.isObject()) {
                        String text = contentObj.path("text").asText();
                        extractedTexts.append(text).append(" ");
                    }
                }
            }
        }

        return extractedTexts.toString().trim();
    }

    public PostItemList findPreviewList(List<Long> blogIdList, int page) {

        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("createdAt"));
        Slice<Post> posts = postRepository.findByBlogIdList(blogIdList, pageRequest);

        return PreviewResponse.toPostItemList(posts);
    }

    public PostItemList findPreviewsByMember(List<Long> memberIdList, int page) {

        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("createdAt"));
        Slice<Post> posts = postRepository.findByMemberIdList(memberIdList, pageRequest);

        return PreviewResponse.toPostItemList(posts);
    }

    /**
     * ElasticSearch를 통한 Post 검색
     */
    public List<PostSearchDTO> searchPost(String q) {

        // Elasticsearch URL
        String searchUrl = url + "/post/_search?q=" + q;

        // Request Header
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(searchUrl))
            .setHeader("Authorization", "ApiKey " + apiKey)
            .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build();

        // Search Result
        List<PostSearchDTO> postSearchDTOList = new ArrayList<>();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // search 결과 리스트 반환
            JsonNode hitsNode = jsonNode.path("hits").path("hits");

            for (JsonNode hit : hitsNode) {
                JsonNode sourceNode = hit.path("_source");

                // PostSearchDTO로 매핑
                PostSearchDTO postSearchDTO = objectMapper.treeToValue(sourceNode,
                    PostSearchDTO.class);
                postSearchDTOList.add(postSearchDTO);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return postSearchDTOList;
    }

    public void savePostIndex() {

        Post post = Post.builder()
            .title("test")
            .preview("preview")
            .blogId(1L)
            .memberId(2L)
            .build();

        PostDocument postDocument = PostDocument.toDocument(post);
        postElasticsearchRepository.save(postDocument);
    }


}
