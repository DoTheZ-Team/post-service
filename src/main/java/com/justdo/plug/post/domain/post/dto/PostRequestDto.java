package com.justdo.plug.post.domain.post.dto;

import com.justdo.plug.post.domain.post.Post;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostRequestDto {

    private String title;
    private String content;
    private boolean temporaryState;
    private List<String> hashtags;
    private String categoryName;
    private List<String> photoUrls;
    private Long memberId; // TODO : JWT 토큰으로 처리할 것

    public Post toEntity(PostRequestDto postRequestDto, String preview, Long blogId) {
        return Post.builder()
            .title(postRequestDto.getTitle())
            .content(postRequestDto.getContent())
            .temporaryState(postRequestDto.temporaryState)
            .memberId(postRequestDto.getMemberId())
            .blogId(blogId)
            .preview(preview)
            .build();
    }
}
