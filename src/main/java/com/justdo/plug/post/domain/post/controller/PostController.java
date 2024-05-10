package com.justdo.plug.post.domain.post.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.justdo.plug.post.domain.category.service.CategoryService;
import com.justdo.plug.post.domain.photo.service.PhotoService;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.post.dto.PostRequestDto;
import com.justdo.plug.post.domain.post.dto.PostResponseDto;
import com.justdo.plug.post.domain.post.dto.PostSearchDTO;
import com.justdo.plug.post.domain.post.dto.PreviewResponse;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.BlogPostItem;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.PostItem;
import com.justdo.plug.post.domain.post.dto.PreviewResponse.PostItemList;
import com.justdo.plug.post.domain.post.service.PostService;
import com.justdo.plug.post.domain.posthashtag.service.PostHashtagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Post API")
@RestController
@RequestMapping("/posts/")
@RequiredArgsConstructor
public class PostController {

    private final PostHashtagService postHashtagService;
    private final PostService postService;
    private final CategoryService categoryService;
    private final PhotoService photoService;


    // BLOG001: 게시글 리스트 조회 요청
    @GetMapping
    public List<Post> ViewList() {

        return postService.getAllPosts();

    }

    // BLOG002: 게시글 상세페이지 조회 요청
    @GetMapping("{postId}")
    public PostResponseDto ViewPage(@PathVariable Long postId) throws JSONException {

        return postService.getPostById(postId);

    }

    // BLOG003: 게시글 작성 요청
    @PostMapping("{blogId}")
    public String PostBlog(@RequestBody PostRequestDto requestDto, @PathVariable Long blogId)
        throws JsonProcessingException {

        // 1. Post 저장
        requestDto.setBlogId(blogId);

        // 1-2. Preview 저장
        String content = requestDto.getContent();
        String preview = postService.parseContent(content);
        requestDto.setPreview(preview);
        Post post = postService.save(requestDto);

        // Post 에서 post_id 받아오기
        Long postId = post.getId();

        // 2. Post_Hashtag 저장
        List<String> hashtags = requestDto.getHashtags();
        postHashtagService.createHashtag(hashtags, post);

        // 3. Category 저장
        String name = requestDto.getName(); // '카테고리 명'저장
        categoryService.createCategory(name, postId);

        // 4. Photo 저장
        String photoUrl = requestDto.getPhotoUrl();
        photoService.createPhoto(photoUrl, post);

        return "게시글이 성공적으로 업로드 되었습니다";
    }

    // BLOG004: 게시글 수정 요청
    @PatchMapping("{postId}")
    public PostRequestDto EditBlog(@PathVariable Long postId) {
        /*service*/
        return null;
    }

    // BLOG005: 게시글 삭제 요청
    @DeleteMapping("{postId}")
    public PostRequestDto DeleteBlog(@PathVariable Long postId) {
        /*service*/
        return null;
    }

    // BlOG007: 특정 멤버가 사용한 HASHTAG 값 조회
    @GetMapping("memberId/{memberId}")
    public List<String> ViewHashtags(@PathVariable Long memberId) {

        return postHashtagService.getHashtags(memberId);

    }

    // BlOG008: 게시글의 글만 조회하기
    @GetMapping("preview/{postId}")
    public String PreviewPost(@PathVariable Long postId) throws JsonProcessingException {

        return postService.getPreviewPost(postId);

    }

    @Operation(summary = "Post 미리보기에서 내가 구독한 사용자 포스트 조회 요청 (구독 페이지)", description = "Open Feign을 통해 사용되는 API입니다.")
    @PostMapping("preview")
    public PostItemList findPreviewByBlogIds(@RequestBody List<Long> blogIdList,
        @RequestParam int page) {

        return postService.findPreviewList(blogIdList, page);
    }

    @PostMapping("preview/subscribers")
    @Operation(summary = "Post 미리보기에서 나를 구독한 사용자 포스트 조회 요청 (구독 페이지)", description = "Open Feign을 통해 사용되는 API입니다.")
    public PostItemList findPreviewByMemberIds(@RequestBody List<Long> memberIdList,
        @RequestParam int page) {

        return postService.findPreviewsByMember(memberIdList, page);
    }

    /**
     * 포스트 목록 조회 (개인 블로그 조회 페이지) -> open feign
     */
    @Operation(summary = "최신 Post 4개 조회 요청", description = "Open Feign을 통해 사용되는 API입니다.")
    @Parameter(name = "blogId", description = "블로그의 Id, Path Variable입니다.", required = true, in = ParameterIn.PATH)
    @GetMapping("blogs/{blogId}")
    public BlogPostItem findBlogPosts(@PathVariable Long blogId) {

        List<Post> recent4Post = postService.getRecent4Post(blogId);

        List<PostItem> postItemList = postService.getPostItemList(recent4Post);
        List<String> hashtagNames = postHashtagService.getHashtagNamesByPost(recent4Post);

        return PreviewResponse.toBlogPostItem(postItemList, hashtagNames);
    }

    // BLOG009: 블로그 아이디로 해시태그 추출하기
    @GetMapping("blogId/{blogId}")
    public List<String> ViewHashtagsBlog(@PathVariable Long blogId) {
        return postHashtagService.getHashtagsBlog(blogId);

    }

    // kylo ES
    @Operation(summary = "Post 검색 요청", description = "Post의 title, content, hashtagName을 기반으로 검색을 진행합니다.")
    @Parameter(name = "keyword", description = "keyword는 검색어이며, QueryString 입니다.", required = true, example = "종강", in = ParameterIn.QUERY)
    @GetMapping("search")
    public List<PostSearchDTO> searchElastic(@RequestParam String keyword) {

        return postService.searchPost(keyword);
    }


}
