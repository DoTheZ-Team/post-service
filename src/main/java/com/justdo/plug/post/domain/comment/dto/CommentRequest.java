package com.justdo.plug.post.domain.comment.dto;

import com.justdo.plug.post.domain.comment.Comment;
import com.justdo.plug.post.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class CommentRequest {

    @Schema(description = "댓글 작성 DTO")
    @Getter
    public static class PostComment {

        @Schema(description = "댓글 내용")
        private String content;

        @Schema(description = "부모 댓글 id / 부모 댓글인 경우, parentId 빼고 보내기!")
        private Long parentId;
    }

    public static Comment toEntity(Long memberId, Post post, String content,
            Comment parentComment) {

        return Comment.builder()
                .memberId(memberId)
                .content(content)
                .post(post)
                .parentComment(parentComment)
                .build();
    }
}
