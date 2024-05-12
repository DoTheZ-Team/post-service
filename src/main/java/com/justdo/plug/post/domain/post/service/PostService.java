package com.justdo.plug.post.domain.post.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justdo.plug.post.domain.hashtag.service.HashtagService;
import com.justdo.plug.post.domain.photo.service.PhotoService;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.post.dto.PostRequestDto;
import com.justdo.plug.post.domain.post.dto.PostResponseDto;
import com.justdo.plug.post.domain.post.dto.PostSearchDTO;
import com.justdo.plug.post.domain.post.dto.PreviewResponse;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.PostItem;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.PostItemSlice;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.StoryItem;
import com.justdo.plug.post.domain.post.repository.PostRepository;
import com.justdo.plug.post.domain.posthashtag.PostHashtag;
import com.justdo.plug.post.domain.posthashtag.service.PostHashtagService;
import com.justdo.plug.post.elastic.PostDocument;
import com.justdo.plug.post.elastic.PostElasticsearchRepository;
import com.justdo.plug.post.global.exception.ApiException;
import com.justdo.plug.post.global.response.code.status.ErrorStatus;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostHashtagService postHashtagService;
    private final HashtagService hashtagService;
    private final PostElasticsearchRepository postElasticsearchRepository;

    private final PhotoService photoService;


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
    @Transactional
    public Post save(PostRequestDto requestDto) throws JsonProcessingException {
        Post post = requestDto.toEntity();

        Post save = postRepository.save(post);
        savePostIndex(post);

        System.out.println(post.getEsId());
        return save;
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
                String hashtagName = hashtagService.getHashtagNameById(
                    postHashtag.getHashtag().getId());
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

    /**
     * 게시글의 preview 값 저장
     */
    public String parseContent(String content) throws JsonProcessingException {

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

    public PostItemSlice findPreviewList(List<Long> blogIdList, int page) {

        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("createdAt"));
        Slice<Post> posts = postRepository.findByBlogIdList(blogIdList, pageRequest);

        List<String> photoUrls = photoService.findPhotoUrlsByPosts(posts.getContent());

        return PreviewResponse.toPostItemSlice(posts, photoUrls);
    }

    public PostItemSlice findPreviewsByMember(List<Long> memberIdList, int page) {

        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("createdAt"));
        Slice<Post> posts = postRepository.findByMemberIdList(memberIdList, pageRequest);

        List<String> photoUrls = photoService.findPhotoUrlsByPosts(posts.getContent());

        return PreviewResponse.toPostItemSlice(posts, photoUrls);
    }

    /**
     * Elastic Search를 통한 Post 검색 (title, content, hashtag)
     */
    public List<PostSearchDTO> searchPost(String keyword) {

        // Elasticsearch URL
        String searchUrl =
            url + "/post/_search?q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);

        // Request Header
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(searchUrl))
            .setHeader("Authorization", "ApiKey " + apiKey)
            .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build();

        // Search Result
        List<PostSearchDTO> postSearchDTOList = new ArrayList<>();
        List<Long> postSearchDTOListPostId = new ArrayList<>();
        List<Long> postSearchDTOListBlogId = new ArrayList<>();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println(responseBody);

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
                postSearchDTOListPostId.add(postSearchDTO.getPostId());
                postSearchDTOListBlogId.add(postSearchDTO.getBlogId());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        List<Long> newPostSearchDTOListBlogId = postSearchDTOListBlogId.stream().distinct()
            .collect(Collectors.toList());
        List<Long> newPostSearchDTOListPostId = postSearchDTOListPostId.stream().distinct()
            .collect(Collectors.toList());

        System.out.println("postSearchDTOListBlogId = " + newPostSearchDTOListBlogId);
        System.out.println("postSearchDTOListPostId = " + newPostSearchDTOListPostId);

        return postSearchDTOList;
    }

    /**
     * Elastic Search Post Document 생성
     */
    @Transactional
    public void savePostIndex(Post post) {
        PostDocument postDocument = PostDocument.toDocument(post);
        PostDocument document = postElasticsearchRepository.save(postDocument);
        post.setEsId(document.getId());
    }
  
    public String deletePost(String id){

        // MySQL
        // EsId 값으로 Post를 찾기
        /*
        Post post = postRepository.findByEsId(id)
                .orElseThrow(() -> new ApiException(ErrorStatus._POST_NOT_FOUND));

        Long postId = post.getId();
        List<PostHashtag> postHashtags = postHashtagRepository.findByPostId(postId);
        postHashtagRepository.deleteAll(postHashtags);

        List<Photo> photos = photoRepository.findByPostId(postId);
        photoRepository.deleteAll(photos);

        // 찾은 Post 삭제
        postRepository.delete(post);

         */


        // Elasticsearch
        String deleteUrl = url + "/post/_doc/" + URLEncoder.encode(id, StandardCharsets.UTF_8);;

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create(deleteUrl))
                .header("Authorization", "ApiKey " + apiKey)
                .DELETE()
                .build();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

            if (deleteResponse.statusCode() == 200) {
               return "게시글이 삭제되었습니다.";
            } else {
                return "게시글 삭제도중 오류가 발생하였습니다: " + deleteResponse.statusCode();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return e.toString();
        }

    }

    /**
     * 최신 postItem 4개 조회
     */
    public List<PostItem> getPostItemList(List<Post> posts) {

        List<String> photoUrls = photoService.findPhotoUrlsByPosts(posts);

        return PreviewResponse.toPostItemList(posts, photoUrls);
    }

    /**
     * 최신 post 4개 조회
     */
    public List<Post> getRecent4Post(Long blogId) {

        return postRepository.findTop4ByBlogIdOrderByCreatedAtDesc(blogId);
    }

    /**
     * Post Paging
     */
    public StoryItem findStories(Long blogId, int page) {

        PageRequest pageRequest = PageRequest.of(page, 7, Sort.by("id").descending());

        Page<Post> posts = postRepository.findAllByBlogId(blogId, pageRequest);

        List<String> photoUrlList = posts.stream()
            .map(post -> photoService.findPhotoByPostId(post.getId()))
            .toList();

        return PreviewResponse.toStoryItem(posts, photoUrlList);
    }
}
