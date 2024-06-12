package com.justdo.plug.post.domain.post.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justdo.plug.post.domain.auth.AuthClient;
import com.justdo.plug.post.domain.blog.BlogClient;
import com.justdo.plug.post.domain.blog.SubscriptionRequest;
import com.justdo.plug.post.domain.blog.SubscriptionResponse;
import com.justdo.plug.post.domain.comment.repository.CommentRepository;
import com.justdo.plug.post.domain.likes.repository.LikesRepository;
import com.justdo.plug.post.domain.photo.Photo;
import com.justdo.plug.post.domain.photo.repository.PhotoRepository;
import com.justdo.plug.post.domain.photo.service.PhotoService;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.post.dto.PostRequestDto;
import com.justdo.plug.post.domain.post.dto.PostResponse;
import com.justdo.plug.post.domain.post.dto.PostUpdateDto;
import com.justdo.plug.post.domain.post.dto.PreviewResponse;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.PostItemBy5Photo;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.PostItemSlice;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.StoryItem;
import com.justdo.plug.post.domain.post.dto.SearchResponse;
import com.justdo.plug.post.domain.post.dto.SearchResponse.BlogInfoItem;
import com.justdo.plug.post.domain.post.dto.SearchResponse.PostSearch;
import com.justdo.plug.post.domain.post.dto.SearchResponse.PostSearchItem;
import com.justdo.plug.post.domain.post.dto.SearchResponse.SearchInfo;
import com.justdo.plug.post.domain.post.repository.PostRepository;
import com.justdo.plug.post.domain.posthashtag.service.PostHashtagService;
import com.justdo.plug.post.domain.sticker.PostStickerDTO;
import com.justdo.plug.post.domain.sticker.PostStickerResponseDTO;
import com.justdo.plug.post.domain.sticker.StickerClient;
import com.justdo.plug.post.elastic.PostDocument;
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
    private final PhotoService photoService;
    private final BlogClient blogClient;
    private final AuthClient authClient;
    private final StickerClient stickerClient;
    private final PhotoRepository photoRepository;
    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${spring.elasticsearch.uris}")
    private String url;

    @Value("${elasticsearch.api-key}")
    private String apiKey;

    // BLOG002: 게시글 상세 페이지 조회
    public PostResponse.PostDetail getPostById(Post post, Long memberId, boolean isLike) {

        Long postId = post.getId();
        List<String> postHashtags = postHashtagService.getPostHashtagNames(postId);
        List<String> photoUrls = photoService.findPhotoUrlsByPostId(postId);


        boolean isSubscribe;
        SubscriptionRequest.LoginSubscription loginSubscription = SubscriptionRequest.toLoginSubscription(
                memberId, post.getBlogId());
      
        SubscriptionResponse.SubscribedProfile subscribedProfile = blogClient.checkSubscribeById(loginSubscription);

        if (memberId == null) {
            isSubscribe = false;
        } else {
            isSubscribe = subscribedProfile.isSubscribed();

        }

        
        String profile = subscribedProfile.getProfile();

        String nickname = authClient.getMemberName(memberId);

        List<PostStickerResponseDTO.PostStickerItem> postStickerItems = stickerClient.getStickersByPostId(
                postId);

        return PostResponse.toPostDetail(post, isLike, isSubscribe, postHashtags, photoUrls,
                postStickerItems, nickname, profile);

    }

    // BLOG003: 블로그 작성
    @Transactional
    public Post save(PostRequestDto requestDto, Long blogId, Long memberId)
            throws JsonProcessingException {

        String preview = parseContent(requestDto.getContent());

        Post post = requestDto.toEntity(requestDto, preview, blogId, memberId);
        Post save = postRepository.save(post);

        String esId = savePostIndex(save);
        post.changeEsId(esId);

        return save;
    }

    // 게시글의 preview 값 저장
    public String parseContent(String content) throws JsonProcessingException {
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

    // ElasticSearch로 Post 검색 (title, content, hashtag)
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

            // Result Parsing
            JsonNode rootNode = mapper.readTree(responseBody);

            // 검색된 데이터 총 개수
            int totalValue = rootNode.path("hits").path("total").path("value").asInt();

            // search 결과 리스트 반환
            List<PostSearch> searchResponseList = new ArrayList<>();
            List<Long> postIdList = new ArrayList<>(); // Photo 조회
            List<Long> blogIdList = new ArrayList<>(); // Blog 조회
            JsonNode hitsNode = rootNode.path("hits").path("hits");

            for (JsonNode hit : hitsNode) {
                JsonNode sourceNode = hit.path("_source");

                // PostSearchDTO로 매핑
                PostSearch postSearch = mapper.treeToValue(sourceNode,
                        PostSearch.class);
                searchResponseList.add(postSearch);
                postIdList.add(postSearch.getPostId());
                blogIdList.add(postSearch.getBlogId());
            }
            List<Long> distinctBlogId = blogIdList.stream().distinct()
                    .toList();

            List<String> photoUrls = postIdList.stream()
                    .map(photoService::findPhotoByPostId)
                    .toList();

            BlogInfoItem blogInfoItem = blogClient.findBlogInfoItem(distinctBlogId,
                    pageable.getPageNumber());

            PostSearchItem postSearchItem = SearchResponse.toPostSearchItem(searchResponseList,
                    photoUrls,
                    pageable, totalValue);

            return SearchResponse.toSearchInfo(postSearchItem, blogInfoItem);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Transactional
    public String savePostIndex(Post post) throws JsonProcessingException {

        PostDocument postDocument = PostDocument.toDocument(post);
        String jsonDocument = mapper.writeValueAsString(postDocument);

        String createURL = url + "/post/_doc";
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(createURL))
                .header("Authorization", "ApiKey " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonDocument))
                .build();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> createResponse = client.send(createRequest,
                    HttpResponse.BodyHandlers.ofString());

            if (createResponse.statusCode() == 200 || createResponse.statusCode() == 201) {
                JsonNode jsonResponse = mapper.readTree(createResponse.body());
                return jsonResponse.get("_id").asText();
            } else {
                return "게시글 수정도중 오류가 발생하였습니다: " + createResponse;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @Transactional
    public String deletePost(Long postId) {

        Post post = getPost(postId);

        String esId = post.getEsId();
        postHashtagService.deletePostHashtags(postId);

        List<Photo> photos = photoRepository.findAllByPostId(postId);
        if (photos != null && !photos.isEmpty()) {
            photoRepository.deleteAll(photos);
        }

        likesRepository.deleteByPostId(postId);
        commentRepository.deleteByPostId(postId);
        postRepository.deleteByEsId(esId);

        // Elasticsearch
        String deleteUrl = url + "/post/_doc/" + URLEncoder.encode(esId, StandardCharsets.UTF_8);

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create(deleteUrl))
                .header("Authorization", "ApiKey " + apiKey)
                .DELETE()
                .build();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> deleteResponse = client.send(deleteRequest,
                    HttpResponse.BodyHandlers.ofString());

            return "게시글이 삭제되었습니다.";

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return e.toString();
        }

    }

    // 최신 postItem 4개 조회
    public List<PostItemBy5Photo> getPostItemList(List<Post> posts) {

        List<List<String>> photoUrls = photoService.findTop5PhotoUrlsByPosts(posts);

        return PreviewResponse.toPostItemBy5Photo(posts, photoUrls);
    }

    // 최신 post 4개 조회
    public List<Post> getRecent4Post(Long blogId) {

        return postRepository.findTop4ByBlogIdOrderByCreatedAtDesc(blogId);
    }

    // Post Paging
    public StoryItem findStories(Long blogId, PageRequest pageRequest) {

        Page<Post> posts = postRepository.findAllByBlogId(blogId, pageRequest);

        List<String> photoUrlList = posts.stream()
                .map(post -> photoService.findPhotoByPostId(post.getId()))
                .toList();

        return PreviewResponse.toStoryItem(posts, photoUrlList);
    }

    @Transactional
    public String UpdatePost(Long postId, PostUpdateDto updateDto) throws JsonProcessingException {

        Post post = getPost(postId);
        String esId = post.getEsId();

        // 해시태그 변경
        postHashtagService.changeHashtag(updateDto.getHashtags(), post);

        // 이미지 경로 변경
        photoService.updatePhotoUrls(post, postId, updateDto.getPhotoUrls());

        // POST 변경
        String preview = parseContent(updateDto.getContent());
        post.changePost(updateDto, preview);

        String jsonBody;
        try {
            Map<String, Object> docFields = new HashMap<>();
            docFields.put("title", updateDto.getTitle());
            docFields.put("content", updateDto.getContent());
            docFields.put("hashtags", updateDto.getHashtags());
            docFields.put("categoryName", updateDto.getCategoryName());
            docFields.put("preview", preview);

            // "doc" 필드 아래에 updateDto 객체를 넣어서 JSON 문자열로 변환
            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("doc", docFields);
            jsonBody = mapper.writeValueAsString(requestBodyMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "게시글 수정 중 오류 발생: " + e.getMessage();
        }

        // Elasticsearch
        String updateURL = url + "/post/_update/" + URLEncoder.encode(esId, StandardCharsets.UTF_8);

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

    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new ApiException(ErrorStatus._POST_NOT_FOUND)
        );
    }

    public void sendSticker(List<PostStickerDTO.PostStickerItem> postStickerItemList) {
        stickerClient.savePostStickers(postStickerItemList);
    }

    public Long getBlogIdByMemberId(Long memberId) {
        return blogClient.getBlogId(memberId);
    }

}