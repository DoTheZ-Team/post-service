package com.justdo.plug.post.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "검색 응답 DTO")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchDTO {

    @Schema(description = "Post 아이디")
    private Long postId;

    @Schema(description = "Post 제목 ")
    private String title;

    @Schema(description = "Post Preview : 포스트 글 부분")
    private String preview;

    @Schema(description = "Post가 작성된 Blog 아이디")
    private Long blogId;

    @Schema(description = "작성자 Member Id")
    private Long memberId;

    @Schema(description = "ES 관련 데이터")
    private String _class;

}
