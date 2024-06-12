package com.justdo.plug.post.domain.post.dto;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.sticker.PostStickerDTO;
import com.justdo.plug.post.domain.sticker.PostStickerResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

        @Schema(description = "사용자 nickname")
        private String nickname;

        @Schema(description = "사용자 profile URL")
        private String profile;

        @Schema(description = "포스트 ID")
        private Long postId;

        @Schema(description = "포스트 제목")
        private String title;

        @Schema(description = "포스트 내용")
        private List<Object> content;

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

        @Schema(description = "포스트의 해시태그 값")
        private List<String> postHashtags;

        @Schema(description = "포스트의 카테고리 값")
        private String categoryName;

        @Schema(description = "포스트의 이미지 경로")
        private List<String> photoUrls;

        @Schema(description = "포스트의 스티커 정보")
        private List<PostStickerResponseDTO.PostStickerItem> postStickerItems;

    }

    // SUB: 게시글 반환 함수
    public static PostResponse.PostDetail toPostDetail(Post post, boolean isLike,
            boolean isSubscribe, List<String> postHashtags, List<String> photoUrls, List<PostStickerResponseDTO.PostStickerItem> postStickerItems, String nickname, String profile) {

        List<Object> contentList = new Gson().fromJson(post.getContent(),
                new TypeToken<List<Object>>() {
                }.getType());

        return PostDetail.builder()
                .nickname(nickname)
                .profile(profile)
                .postId(post.getId())
                .title(post.getTitle())
                .content(contentList)
                .likeCount(post.getLikeCount())
                .temporaryState(post.isTemporaryState())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .memberId(post.getMemberId())
                .blogId(post.getBlogId())
                .isLike(isLike)
                .isSubscribe(isSubscribe)
                .postHashtags(postHashtags)
                .categoryName(post.getCategoryName())
                .photoUrls(photoUrls)
                .postStickerItems(postStickerItems)
                .build();
    }

    @Schema(description = "포스트 작성 후, 응답 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostProc {

        @Schema(description = "포스트가 작성된 블로그 ID", example = "1")
        private Long blogId;

        @Schema(description = "포스트 ID", example = "2")
        private Long postId;
    }

    public static PostProc toPostProc(Post post) {

        return PostProc.builder()
                .blogId(post.getBlogId())
                .postId(post.getId())
                .build();
    }
}
