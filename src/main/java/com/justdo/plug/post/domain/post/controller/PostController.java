package com.justdo.plug.post.domain.post.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.justdo.plug.post.domain.category.service.CategoryService;
import com.justdo.plug.post.domain.hashtag.service.HashtagService;
import com.justdo.plug.post.domain.photo.service.PhotoService;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.post.dto.PostRequestDto;
import com.justdo.plug.post.domain.post.dto.PostResponseDto;
import com.justdo.plug.post.domain.post.service.PostService;
import com.justdo.plug.post.domain.posthashtag.service.PostHashtagService;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/posts/")
@RequiredArgsConstructor
/*BLOG API - POSTS*/
public class PostController {

    private final HashtagService hashtagService;
    private final PostHashtagService postHashtagService;
    private final PostService postService;
    private final CategoryService categoryService;
    private final PhotoService photoService;

    // BLOG001: 게시글 리스트 조회 요청
    @GetMapping()
    public List<Post> ViewList(){

        return postService.getAllPosts();

    }

    // BLOG002: 게시글 상세페이지 조회 요청
    @GetMapping("{postId}")
    public PostResponseDto ViewPage(@PathVariable Long postId) throws JSONException {

        return postService.getPostById(postId);

    }

    // BLOG003: 게시글 작성 요청
    @PostMapping("{blogId}")
    public String PostBlog(@RequestBody PostRequestDto RequestDto, @PathVariable Long blogId) {

            // 1. Post 저장
            RequestDto.setBlogId(blogId);
            Post post = postService.save(RequestDto);

            // Post 에서 post_id 받아오기
            Long postId = post.getId();

            // 2. Post_Hashtag 저장
            List<String> hashtags = RequestDto.getHashtags();
            postHashtagService.createHashtag(hashtags, postId);

            // 3. Category 저장
            String name = RequestDto.getName(); // '카테고리 명'저장
            categoryService.createCategory(name, postId);

            // 4. Photo 저장
            String photoUrl = RequestDto.getPhotoUrl();
            photoService.createPhoto(photoUrl, postId);

            return "게시글이 성공적으로 업로드 되었습니다";
    }

    // BLOG004: 게시글 수정 요청
    @PatchMapping("{postId}")
    public PostRequestDto EditBlog(@PathVariable Long postId){
        /*service*/
        return null;
    }

    // BLOG005: 게시글 삭제 요청
    @DeleteMapping("{postId}")
    public PostRequestDto DeleteBlog(@PathVariable Long postId){
        /*service*/
        return null;
    }

    // BLOG006: 블로그 게시글 리스트 조회 요청
    @GetMapping("blog/{blogId}")
    public List<Post> ViewBlogList(@PathVariable Long blogId){

        return postService.getBlogPosts(blogId);
    }

    // BlOG007: 특정 멤버가 사용한 HASHTAG 값 조회
    @GetMapping("member/{memberId}")
    public List<String> ViewHashtags(@PathVariable Long memberId){

        return postService.getHashtags(memberId);

    }

    // BlOG008: 게시글의 글만 조회하기
    @GetMapping("preview/{postId}")
    public String PreviewPost(@PathVariable Long postId) throws JsonProcessingException {

        return postService.getPreviewPost(postId);

    }

}
