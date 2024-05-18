package com.justdo.plug.post.domain.blog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class BlogDto {

    @Schema(description = "댓글의 블로그 정보 Request DTO - Open Feign으로부터 받음")
    @Getter
    public static class BlogInfo {

        @Schema(description = "블로그 프로필")
        private String profile;

        @Schema(description = "블로그 제목")
        private String title;
    }

}
