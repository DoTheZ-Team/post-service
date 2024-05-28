package com.justdo.plug.post.domain.sticker;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

public class PostStickerDTO {
    @Schema(description = "스티커 포스트 정보 DTO")
    @Getter
    public static class PostStickerItem {

        @Schema(description = "포스트-스티커의 id")
        @JsonProperty("postStickerId")
        private Long postStickerId;

        @Schema(description = "포스트의 id")
        @JsonProperty("postId")
        private Long postId;

        @Schema(description = "스티커의 id")
        @JsonProperty("stickerId")
        private Long stickerId;

        @Schema(description = "스티커의 x_location")
        @JsonProperty("xLocation")
        private int xLocation;

        @Schema(description = "스티커의 y_location")
        @JsonProperty("yLocation")
        private int yLocation;

        @Schema(description = "스티커의 width")
        @JsonProperty("width")
        private int width;

        @Schema(description = "스티커의 height")
        @JsonProperty("height")
        private int height;

        @Schema(description = "스티커의 angle")
        @JsonProperty("angle")
        private int angle;
    }

    @Schema(description = "포스트에 저장된 스티커 리스트 정보 DTO")
    @Getter
    public static class PostStickerItems {
        private Long postId;
        private List<PostStickerItem> postStickerItems;
    }

    @Schema(description = "url포함 스티커 포스트 정보 DTO")
    @Getter
    public static class PostStickerUrlItem {

        @Schema(description = "스티커 item")
        private PostStickerItem postStickerItem;

        @Schema(description = "스티커의 url")
        private String url;

    }

    @Schema(description = "url 포함 스티커 포스트 정보 DTO")
    @Getter
    public static class PostStickerUrlItems {

        @Schema(description = "url포함 포스트-스티커 리스트")
        @JsonProperty("postStickerId")
        private List<PostStickerUrlItem> postStickerUrlItems;
    }
}