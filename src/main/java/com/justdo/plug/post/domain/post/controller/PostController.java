package com.justdo.plug.post.domain.post.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.justdo.plug.post.domain.category.service.CategoryService;
import com.justdo.plug.post.domain.likes.service.LikesService;
import com.justdo.plug.post.domain.photo.service.PhotoService;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.post.dto.PostRequestDto;
import com.justdo.plug.post.domain.post.dto.PostResponse;
import com.justdo.plug.post.domain.post.dto.PostResponse.PostDetail;
import com.justdo.plug.post.domain.post.dto.PostUpdateDto;
import com.justdo.plug.post.domain.post.dto.PreviewResponse;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.BlogPostItem;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.PostItem;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.PostItemSlice;
import com.justdo.plug.post.domain.post.dto.SearchResponse;
import com.justdo.plug.post.domain.post.service.PostService;
import com.justdo.plug.post.domain.posthashtag.service.PostHashtagService;
import com.justdo.plug.post.global.response.ApiResponse;
import com.justdo.plug.post.global.utils.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Post 게시글 관련 API입니다.")
@RestController
@RequestMapping("/posts/")
@RequiredArgsConstructor
public class PostController {

    private final PostHashtagService postHashtagService;
    private final PostService postService;
    private final CategoryService categoryService;
    private final PhotoService photoService;
    private final JwtProvider jwtProvider;
    private final LikesService likesService;


    // BLOG001: 게시글 리스트 조회 요청
    @GetMapping("all")
    @Operation(summary = "모든 게시글 리스트 조회 요청", description = "데이터 베이스 내에 있는 모든 게시글 리스트를 조회 합니다")
    public ApiResponse<List<Post>> ViewList() {

        return ApiResponse.onSuccess(postService.getAllPosts());

    }

    // BLOG002: 게시글 상세페이지 조회 요청
    @GetMapping("{postId}")
    @Operation(summary = "특정 게시글 상세 페이지 조회 요청", description = "특정 게시글의 상세 페이지를 요청합니다(제목, 글, 좋아요 갯수, 현재 사용자가 좋아요 했는지 여부 등)")
    @Parameter(name = "postId", description = "포스트의 id, Path Variable 입니다", required = true, in = ParameterIn.PATH)
    public ApiResponse<PostResponse.PostDetailResult> ViewPage(HttpServletRequest request,
            @PathVariable Long postId) throws JSONException {

        Long memberId = jwtProvider.getUserIdFromToken(request);
        boolean isLike = likesService.isLike(memberId, postId);
        PostDetail postDetail = postService.getPostById(postId, memberId, isLike);

        return ApiResponse.onSuccess(PostResponse.toPostDetailResult(memberId, postDetail));

    }

    // BLOG003: 게시글 작성 요청
    @PostMapping("{blogId}")
    @Operation(summary = "게시글 작성 요청", description = "해당(본인) 블로그에 게시글을 작성합니다")
    @Parameter(name = "blogId", description = "사용자 블로그의 id, Path Variable 입니다", required = true, in = ParameterIn.PATH)
    public ApiResponse<String> PostBlog(HttpServletRequest request,
            @RequestBody PostRequestDto requestDto, @PathVariable Long blogId)
            throws JsonProcessingException {

        Long memberId = jwtProvider.getUserIdFromToken(request);

        // 1. Post 저장
        Post post = postService.save(requestDto, blogId, memberId);

        // 2. Post_Hashtag 저장
        postHashtagService.createHashtag(requestDto.getHashtags(), post);

        // 3. Category 저장
        categoryService.createCategory(requestDto.getCategoryName(), post);

        // 4. Photo 저장
        photoService.createPhoto(requestDto.getPhotoUrls(), post);

        // 5. Recommend Service 로 해시태그 보내주기
        postHashtagService.sendNewHashtags(memberId, requestDto.getHashtags(), request);

        return ApiResponse.onSuccess("게시글이 성공적으로 업로드 되었습니다");
    }

    // BLOG004: 게시글 수정 요청
    @PatchMapping("{postId}")
    @Operation(summary = "특정게시글 수정 요청", description = "해당 게시글을 수정합니다")
    @Parameter(name = "postId", description = "포스트의 id, Path Variable 입니다", required = true, in = ParameterIn.PATH)
    public ApiResponse<String> EditBlog(HttpServletRequest request, @PathVariable Long postId,
            @RequestBody PostUpdateDto updateDto)
            throws JsonProcessingException {

        // 전체 해시태그 Recommend Service 로 보내주기
        Long memberId = jwtProvider.getUserIdFromToken(request);
        postHashtagService.sendAllHashtags(memberId, request);

        return ApiResponse.onSuccess(postService.UpdatePost(postId, updateDto));
    }

    // BLOG005: 게시글 삭제 요청
    @DeleteMapping("{postId}")
    @Operation(summary = "특정게시글 삭제 요청", description = "해당 게시글을 삭제합니다")
    @Parameter(name = "postId", description = "포스트의 id, Path Variable 입니다", required = true, in = ParameterIn.PATH)
    public ApiResponse<String> deletePost(HttpServletRequest request, @PathVariable Long postId) {

        Long memberId = jwtProvider.getUserIdFromToken(request);
        postHashtagService.sendAllHashtags(memberId, request);

        return ApiResponse.onSuccess(postService.deletePost(postId));
    }

    // BlOG007: 특정 멤버가 사용한 HASHTAG 값 조회
    // Recommend service에서 사용예정이라 따로 JWT 파싱 진행 안함
    @GetMapping("memberId/{memberId}")
    @Operation(summary = "특정 멤버가 사용한 HASHTAG 값 조회", description = "Open Feign을 통해 사용되는 API입니다.")
    public List<String> ViewHashtags(@PathVariable Long memberId) {

        return postHashtagService.getHashtags(memberId);
    }

    // BlOG008: 게시글의 글만 조회하기
    @GetMapping("previews/{postId}")
    @Operation(summary = "특정게시글의 미리보기(글만) 요청", description = "글 내의 JS 값들을 제외한 글만 조회하는 API입니다")
    @Parameter(name = "postId", description = "포스트의 id, Path Variable 입니다", required = true, in = ParameterIn.PATH)
    public ApiResponse<String> PreviewPost(@PathVariable Long postId)
            throws JsonProcessingException {

        return ApiResponse.onSuccess(postService.getPreviewPost(postId));

    }

    @Operation(summary = "내가 구독하는 블로그 (구독 페이지) - Open Feign을 통해 사용되는 API입니다.", description = "Post 미리보기에서 내가 구독한 사용자 Post 조회 요청")
    @PostMapping("previews/subscriptions")
    public PostItemSlice findPreviewByBlogIds(@RequestBody List<Long> blogIdList,
            @RequestParam int page) {

        return postService.findPreviewList(blogIdList, page);
    }

    @PostMapping("previews/subscribers")
    @Operation(summary = "나를 구독하는 블로그 (구독 페이지) - Open Feign을 통해 사용되는 API입니다.", description = "Post 미리보기에서 나를 구독한 사용자 포스트 조회 요청")
    public PostItemSlice findPreviewByMemberIds(@RequestBody List<Long> memberIdList,
            @RequestParam int page) {

        return postService.findPreviewsByMember(memberIdList, page);

    }

    @Operation(summary = "내 블로그 - Open Feign을 통해 사용되는 API입니다.", description = "최신 Post 4개 조회 요청")
    @Parameter(name = "blogId", description = "블로그의 Id, Path Variable입니다.", required = true, in = ParameterIn.PATH)
    @GetMapping("blogs/{blogId}")
    public BlogPostItem findBlogPosts(@PathVariable Long blogId) {

        List<Post> recent4Post = postService.getRecent4Post(blogId);

        List<PostItem> postItemList = postService.getPostItemList(recent4Post);
        List<String> hashtagNames = postHashtagService.getHashtagNamesByPost(recent4Post);

        return PreviewResponse.toBlogPostItem(postItemList, hashtagNames);
    }

    @Operation(summary = "내 블로그 전체글 - 블로그의 포스트를 페이징 조회합니다", description = "Post를 최신순 7개씩 반환합니다.")
    @Parameters({
            @Parameter(name = "blogId", description = "블로그의 Id, Path Variable입니다.", required = true, in = ParameterIn.PATH),
            @Parameter(name = "page", description = "페이지 번호(page), Query String입니다.", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "size", description = "페이지 크기(size), Query String입니다.", in = ParameterIn.QUERY)
    })
    @GetMapping("blogs/stories/{blogId}")
    public ApiResponse<PreviewResponse.StoryItem> getStories(@PathVariable Long blogId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "7") int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return ApiResponse.onSuccess(postService.findStories(blogId, pageRequest));
    }

    // BLOG009: 블로그 아이디로 해시태그 추출하기
    @GetMapping("blogId/{blogId}")
    @Operation(summary = "블로그 아이디로 해시태그 추출하기", description = "Open Feign을 통해 사용되는 API입니다.")
    public List<String> ViewHashtagsBlog(@PathVariable Long blogId) {
        return postHashtagService.getHashtagsBlog(blogId);

    }

    @Operation(summary = "검색 페이지 - Post 검색을 요청합니다.", description = "Post의 title, content, hashtagName을 기반으로 검색을 진행합니다.")
    @Parameters({
            @Parameter(name = "keyword", description = "keyword는 검색어이며, QueryString 입니다.", required = true, example = "예시", in = ParameterIn.QUERY),
            @Parameter(name = "page", description = "page : 페이지 번호, Query String입니다.", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "size", description = "size : 페이지 크기, Query String입니다.", in = ParameterIn.QUERY)
    })
    @GetMapping("search")
    public ApiResponse<SearchResponse.SearchInfo> searchElastic(@RequestParam String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        return ApiResponse.onSuccess(postService.searchPost(keyword, pageRequest));
    }

}