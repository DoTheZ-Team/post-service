package com.justdo.plug.post.domain.blog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class SubscriptionRequest {

    @Schema(description = "로그인한 사용자의 정보와 블로그의 정보 DTO")
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginSubscription {

        @Schema(description = "로그인한 사용자의 아이디")
        private Long memberId;

        @Schema(description = "포스트가 작성된 블로그의 아이디")
        private Long blogId;
    }

}
