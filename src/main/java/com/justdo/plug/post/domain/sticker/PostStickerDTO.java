package com.justdo.plug.post.domain.sticker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class PostStickerDTO {
    @Schema(description = "스티커 포스트 정보 DTO")
    @Getter
    public static class PostStickerItem {
        @JsonIgnore
        @Schema(description = "포스트-스티커의 id", example = "1")
        private Long postStickerId;

        @JsonIgnore
        @Schema(description = "포스트의 id", example = "2")
        private Long postId;

        @Schema(description = "스티커의 id", example = "3")
        private Long stickerId;

        @Schema(description = "스티커의 url", example = "https://glue-bucket-sticker.s3.ap-northeast-2.amazonaws.com/54728a63-ff4b-4b3e-aea8-18036491b97c.png")
        private String url;

        @Schema(description = "스티커의 x 위치", example = "100")
        @JsonProperty(value = "xLocation")
        private int xlocation;

        @Schema(description = "스티커의 y 위치", example = "100")
        @JsonProperty(value = "yLocation")
        private int ylocation;

        @Schema(description = "스티커의 scaleX", example = "100")
        private double scaleX;

        @Schema(description = "스티커의 scaleY", example = "100")
        private double scaleY;

        @Schema(description = "스티커의 rotation", example = "100")
        private double rotation;

        public void setPostId(Long postId) {
            this.postId = postId;
        }
    }


}