package com.justdo.plug.post.domain.post.controller;

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

    // BLOG001: 블로그 리스트 조회 요청
    @GetMapping()
    public List<Post> ViewList(){

        return postService.getAllPosts();

    }

    // BLOG002: 블로그 상세페이지 조회 요청
    @GetMapping("{post_id}")
    public PostResponseDto ViewPage(@PathVariable long post_id) throws JSONException {

        return postService.getPostById(post_id);

    }

    // BLOG003: 블로그 작성 요청
    @PostMapping("{blog_id}")
    public String PostBlog(@RequestBody PostRequestDto RequestDto, @PathVariable long blog_id) {

            // 1. Post 저장
            RequestDto.setBlog_id(blog_id);
            Post post = postService.save(RequestDto);

            // Post 에서 post_id 받아오기
            Long post_id = post.getId();

            // 2. Post_Hashtag 저장
            List<String> hashtags = RequestDto.getHashtags();
            postHashtagService.createHashtag(hashtags, post_id);

            // 3. Category 저장
            String name = RequestDto.getName(); // '카테고리 명'저장
            categoryService.createCategory(name, post_id);

            // 4. Photo 저장
            String photo_url = RequestDto.getPhoto_url();
            photoService.createPhoto(photo_url, post_id);

            return "게시글이 성공적으로 업로드 되었습니다";
    }

    // BLOG004: 블로그 수정 요청
    @PatchMapping("{post_id}")
    public PostRequestDto EditBlog(@PathVariable long post_id){
        /*service*/
        return null;
    }

    // BLOG005: 블로그 삭제 요청
    @DeleteMapping("{post_id}")
    public PostRequestDto DeleteBlog(@PathVariable long post_id){
        /*service*/
        return null;
    }

}
