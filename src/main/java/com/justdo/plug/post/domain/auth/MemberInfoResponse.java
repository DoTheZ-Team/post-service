package com.justdo.plug.post.domain.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberInfoResponse {

    @Schema(description = "유저 이메일", example = "ht0729@gachon.ac.kr")
    private String email;
    @Schema(description = "유저 닉네임", example = "정성실")
    private String nickname;

}