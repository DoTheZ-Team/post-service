package com.justdo.plug.post.domain.comment.dto;

import com.justdo.plug.post.domain.blog.BlogDto.BlogInfo;
import com.justdo.plug.post.domain.comment.Comment;
import com.justdo.plug.post.global.utils.DateParser;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

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

    @Schema(description = "댓글 목록 응답 DTO")
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class CommentResult {

        @Schema(description = "댓글 데이터 목록")
        List<CommentItem> commentItems;

        @Schema(description = "페이징된 리스트의 항목 개수")
        private Integer listSize;

        @Schema(description = "총 페이징 수")
        private Integer totalPage;

        @Schema(description = "전체 데이터의 개수")
        private Long totalElements;

        @Schema(description = "첫 페이지의 여부")
        private Boolean isFirst;

        @Schema(description = "마지막 페이지의 여부")
        private Boolean isLast;
    }

    public static CommentResult toCommentResult(Page<Comment> commentPage,
            List<BlogInfo> blogInfoList) {

        List<Comment> commentList = commentPage.getContent();
        List<CommentItem> commentItems = IntStream.range(0, commentList.size())
                .mapToObj(idx -> toCommentItem(commentList.get(idx), blogInfoList.get(idx)))
                .toList();

        return CommentResult.builder()
                .commentItems(commentItems)
                .listSize(commentList.size())
                .totalPage(commentPage.getTotalPages())
                .totalElements(commentPage.getTotalElements())
                .isFirst(commentPage.isFirst())
                .isLast(commentPage.isLast())
                .build();
    }

    @Schema(description = "댓글 데이터 DTO")
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class CommentItem {

        @Schema(description = "작성자 아이디")
        private Long memberId;

        @Schema(description = "댓글 아이디")
        private Long commentId;

        @Schema(description = "댓글 내용")
        private String content;

        @Schema(description = "작성한 블로그 프로필")
        private String blogProfile;

        @Schema(description = "작성한 블로그 제목")
        private String blogTitle;

        @Schema(description = "댓글이 작성된 시간")
        private String createdAt;

        @Schema(description = "부모 댓글 아이디 / 부모 댓글인 경우 0, 대댓글인 경우 부모 댓글의 Id")
        private Long parentCommentId;

        @Schema(description = "자식 댓글 존재 여부")
        private Boolean hasChildComment;
    }

    public static CommentItem toCommentItem(Comment comment, BlogInfo blogInfo) {

        return CommentItem.builder()
                .memberId(comment.getMemberId())
                .commentId(comment.getId())
                .content(comment.getContent())
                .blogProfile(blogInfo.getProfile())
                .blogTitle(blogInfo.getTitle())
                .createdAt(DateParser.dateTimeParse(LocalDateTime.now()))
                .parentCommentId(
                        comment.getParentComment() != null ? comment.getParentComment().getId() : 0)
                .hasChildComment(!comment.getChildren().isEmpty())
                .build();
    }

}
