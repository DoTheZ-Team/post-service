package com.justdo.plug.post.domain.blog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class SubscriptionResponse {

    @Schema(description = "구독 확인 여부 및 프로필 DTO")
    @Getter
    public static class SubscribedProfile {

        private boolean isSubscribed;

        private String profile;
    }
}
