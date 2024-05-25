package com.justdo.plug.post.domain.post.dto;

import com.justdo.plug.post.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONArray;

public class PostResponse {

    @Schema(description = "로그인 사용자 정보 & 포스트 상세 정보 응답 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostDetailResult {

        private Long loginMemberId;
        private PostDetail postDetail;
    }

    public static PostDetailResult toPostDetailResult(Long loginMemberId, PostDetail postDetail) {

        return PostDetailResult.builder()
                .loginMemberId(loginMemberId)
                .postDetail(postDetail)
                .build();
    }

    @Schema(description = "포스트 상세 정보 응답 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostDetail {

        @Schema(description = "포스트 ID")
        private Long postId;

        @Schema(description = "포스트 제목")
        private String title;

        @Schema(description = "포스트 내용")
        private Object[] content;

        @Schema(description = "포스트의 좋아요 개수")
        private int likeCount;

        @Schema(description = "포스트의 임시 저장 여부")
        private Boolean temporaryState;

        @Schema(description = "포스트가 작성된 시간")
        private LocalDateTime createdAt;

        @Schema(description = "포스트가 수정된 시간")
        private LocalDateTime updatedAt;

        @Schema(description = "포스트 작성자 ID")
        private Long memberId;

        @Schema(description = "포스트가 작성된 블로그 ID")
        private Long blogId;

        @Schema(description = "로그인 사용자에 따른 포스트 좋아요 여부")
        private Boolean isLike;

        @Schema(description = "로그인 사용자에 따른 포스트 구독 여부 ")
        private Boolean isSubscribe;
    }

    // SUB: 게시글 반환 함수
    public static PostResponse.PostDetail toPostDetail(Post post, boolean isLike,
            boolean isSubscribe) {

        String JsonContent = post.getContent();
        JSONArray jsonArray = new JSONArray(JsonContent);
        List<Object> list = jsonArray.toList();
        Object[] array = list.toArray();

        return PostResponse.PostDetail.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(array)
                .likeCount(post.getLikeCount())
                .temporaryState(post.isTemporaryState())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .memberId(post.getMemberId())
                .blogId(post.getBlogId())
                .isLike(isLike)
                .isSubscribe(isSubscribe)
                .build();

    }

}
