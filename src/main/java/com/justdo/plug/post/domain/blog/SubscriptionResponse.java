package com.justdo.plug.post.domain.blog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SubscriptionResponse {

    @Schema(description = "구독 확인 여부 및 프로필 DTO")
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class SubscribedProfile {

        private boolean isSubscribed;

        private String profile;
    }
}
