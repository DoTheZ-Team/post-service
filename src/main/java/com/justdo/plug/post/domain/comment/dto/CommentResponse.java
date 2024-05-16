package com.justdo.plug.post.domain.comment.dto;

import com.justdo.plug.post.domain.comment.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentResponse {

    @Schema(description = "댓글 작성/수정/삭제 처리 응답 DTO")
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentProc {

        @Schema(description = "댓글 아이디")
        private Long commentId;

        @Schema(description = "요청 처리 응답 시간")
        private LocalDateTime createdAt;
    }

    public static CommentProc toCommentProc(Comment comment) {

        return CommentProc.builder()
                .commentId(comment.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

}
