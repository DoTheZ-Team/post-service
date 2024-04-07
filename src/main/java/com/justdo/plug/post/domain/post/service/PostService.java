package com.justdo.plug.post.domain.post.service;

import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.post.dto.PostRequestDto;
import com.justdo.plug.post.domain.post.dto.PostResponseDto;
import com.justdo.plug.post.domain.post.repository.PostRepository;
import com.justdo.plug.post.global.exception.ApiException;
import com.justdo.plug.post.global.response.code.status.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    // BLOG001: 블로그 리스트 조회
    public List<Post> getAllPosts() {

        return postRepository.findAll();

    }

    // BLOG002: 블로그 상세 페이지 조회
    public PostResponseDto getPostById(long post_id) throws JSONException {
        Post post = postRepository.findById(post_id)
                .orElseThrow(() -> new ApiException(ErrorStatus._POST_NOT_FOUND));


        return PostResponseDto.createFromPost(post);
    }

    // BLOG003: 블로그 작성
    public Post save(PostRequestDto requestDto) {
       
            Post post = requestDto.toEntity();
            return postRepository.save(post);
    }

}
