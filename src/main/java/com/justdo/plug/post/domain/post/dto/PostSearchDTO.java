package com.justdo.plug.post.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostSearchDTO {

    private Long postId;
    private String title;
    private String preview;
    private Long blogId;
    private Long memberId;
    private String _class;

}
