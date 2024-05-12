package com.justdo.plug.post.domain.post.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justdo.plug.post.domain.blog.BlogClient;
import com.justdo.plug.post.domain.hashtag.service.HashtagService;
import com.justdo.plug.post.domain.photo.service.PhotoService;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.post.dto.PostRequestDto;
import com.justdo.plug.post.domain.post.dto.PostResponseDto;
import com.justdo.plug.post.domain.post.dto.PostUpdateDto;
import com.justdo.plug.post.domain.post.dto.PreviewResponse;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.PostItem;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.PostItemSlice;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.StoryItem;
import com.justdo.plug.post.domain.post.dto.SearchResponse;
import com.justdo.plug.post.domain.post.dto.SearchResponse.BlogInfoItem;
import com.justdo.plug.post.domain.post.dto.SearchResponse.PostSearch;
import com.justdo.plug.post.domain.post.dto.SearchResponse.PostSearchItem;
import com.justdo.plug.post.domain.post.dto.SearchResponse.SearchInfo;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final BlogClient blogClient;


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
    public Post save(PostRequestDto requestDto, Long blogId) throws JsonProcessingException {

        String preview = parseContent(requestDto.getContent());

        Post post = requestDto.toEntity(requestDto, preview, blogId);
        Post save = postRepository.save(post);

        String esId = savePostIndex(save);
        post.setEsId(esId);

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
    public SearchInfo searchPost(String keyword, Pageable pageable) {

        try {
            // paging
            int size = pageable.getPageSize();
            int from = pageable.getPageNumber() * size;

            // Elasticsearch URL
            String searchUrl =
                url + "/post/_search?q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8)
                    + "&from=" + from + "&size=" + size + "&sort=postId:desc";

            // Request Header
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(searchUrl))
                .setHeader("Authorization", "ApiKey " + apiKey)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

            // Search Result
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            System.out.println(responseBody);

            // Result Parsing
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // 검색된 데이터 총 개수
            int totalValue = rootNode.path("hits").path("total").path("value").asInt();
            System.out.println("totalValue = " + totalValue);

            // search 결과 리스트 반환
            List<SearchResponse.PostSearch> searchResponseList = new ArrayList<>();
            List<Long> postIdList = new ArrayList<>(); // Photo 조회
            List<Long> blogIdList = new ArrayList<>(); // Blog 조회
            JsonNode hitsNode = rootNode.path("hits").path("hits");

            for (JsonNode hit : hitsNode) {
                JsonNode sourceNode = hit.path("_source");

                // PostSearchDTO로 매핑
                PostSearch postSearch = objectMapper.treeToValue(sourceNode,
                    PostSearch.class);
                searchResponseList.add(postSearch);
                postIdList.add(postSearch.getPostId());
                blogIdList.add(postSearch.getBlogId());
            }
            List<Long> distinctBlogId = blogIdList.stream().distinct()
                .toList();
            List<Long> distinctPostId = postIdList.stream().distinct()
                .toList();

            List<String> photoUrls = postIdList.stream()
                .map(photoService::findPhotoByPostId)
                .toList();

            BlogInfoItem blogInfoItem = blogClient.findBlogInfoItem(distinctBlogId,
                pageable.getPageNumber());

            System.out.println("postSearchDTOListBlogId = " + distinctBlogId);
            System.out.println("postSearchDTOListPostId = " + distinctPostId);

            PostSearchItem postSearchItem = SearchResponse.toPostSearchItem(searchResponseList,
                photoUrls,
                pageable, totalValue);

            return SearchResponse.toSearchInfo(postSearchItem, blogInfoItem);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Elastic Search Post Document 생성
     */
    @Transactional
    public String savePostIndex(Post post) {
        PostDocument postDocument = PostDocument.toDocument(post);
        PostDocument document = postElasticsearchRepository.save(postDocument);
        return document.getId();
    }

    public String deletePost(String id) {

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
        String deleteUrl = url + "/post/_doc/" + URLEncoder.encode(id, StandardCharsets.UTF_8);

        HttpRequest deleteRequest = HttpRequest.newBuilder()
            .uri(URI.create(deleteUrl))
            .header("Authorization", "ApiKey " + apiKey)
            .DELETE()
            .build();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> deleteResponse = client.send(deleteRequest,
                HttpResponse.BodyHandlers.ofString());

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

    public String UpdatePost(String id, PostUpdateDto updateDto) throws JsonProcessingException {

        String content = updateDto.getContent();
        String preview = parseContent(content);

        /*
        Post post = postRepository.findByEsId(id)
                .orElseThrow(() -> new ApiException(ErrorStatus._POST_NOT_FOUND));
        post.setContent(updateDto.getContent());
        post.setPreview(preview);
        post.setTitle(updateDto.getTitle());
         */

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody;
        try {
            Map<String, Object> docFields = new HashMap<>();
            docFields.put("title", updateDto.getTitle());
            docFields.put("content", updateDto.getContent());
            docFields.put("hashtags", updateDto.getHashtags());
            docFields.put("categoryName", updateDto.getCategoryName());
            docFields.put("photoUrl", updateDto.getPhotoUrl());
            docFields.put("preview", preview);

            // "doc" 필드 아래에 updateDto 객체를 넣어서 JSON 문자열로 변환
            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("doc", docFields);
            jsonBody = objectMapper.writeValueAsString(requestBodyMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "게시글 수정 중 오류 발생: " + e.getMessage();
        }

        // Elasticsearch
        String updateURL = url + "/post/_update/" + URLEncoder.encode(id, StandardCharsets.UTF_8);

        HttpRequest updateRequest = HttpRequest.newBuilder()
            .uri(URI.create(updateURL))
            .header("Authorization", "ApiKey " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> updateResponse = client.send(updateRequest,
                HttpResponse.BodyHandlers.ofString());

            if (updateResponse.statusCode() == 200) {
                return "게시글이 수정되었습니다.";
            } else {
                return "게시글 수정도중 오류가 발생하였습니다: " + updateResponse;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            e.toString();

            return e.toString();
        }

    }
}