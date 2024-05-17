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
    private Long memberId;

    public Post toEntity(PostRequestDto postRequestDto, String preview, Long blogId, Long memberId) {
        return Post.builder()
            .title(postRequestDto.getTitle())
            .content(postRequestDto.getContent())
            .temporaryState(postRequestDto.temporaryState)
            .memberId(postRequestDto.getMemberId())
            .blogId(blogId)
            .preview(preview).memberId(memberId)
            .build();
    }

}
