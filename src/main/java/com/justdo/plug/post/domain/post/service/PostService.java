package com.justdo.plug.post.domain.post.service;

import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.post.dto.PostRequestDto;
import com.justdo.plug.post.domain.post.dto.PostResponseDto;
import com.justdo.plug.post.domain.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    // BLOG002: 블로그 상세 페이지 조회
    public PostResponseDto getPostById(long post_id) throws JSONException {
        Post post = postRepository.findById(post_id)
                .orElseThrow(() -> new RuntimeException("해당 id의 게시글을 찾을 수 없습니다: " + post_id));

        return createPostResponseDto(post);
    }

    // BLOG003: 블로그 작성
    public Post save(PostRequestDto requestDto) {
       
            Post post = requestDto.toEntity();
            return postRepository.save(post);
    }

    // SUB: 게시글 반환 함수
    private PostResponseDto createPostResponseDto(Post post) throws JSONException {
        String JsonContent = post.getContent();
        JSONArray jsonArray = new JSONArray(JsonContent);
        List<Object> list = jsonArray.toList();
        Object[] array = list.toArray();

        return PostResponseDto.builder()
                .post_id(post.getId())
                .title(post.getTitle())
                .content(array)
                .temporary_state(post.isTemporary_state())
                .state(post.isState())
                .created_at(post.getCreatedAt())
                .updated_at(post.getUpdatedAt())
                .member_id(post.getMember_id())
                .blog_id(post.getBlog_id())
                .build();
    }


}
