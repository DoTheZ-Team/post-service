package com.justdo.plug.post.domain.comment.dto;

import com.justdo.plug.post.domain.comment.Comment;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentResponse {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentProc {
        private Long commentId;
        private LocalDateTime createdAt;
    }

    public static CommentProc toCommentProc(Comment comment) {

        return CommentProc.builder()
                .commentId(comment.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

}
