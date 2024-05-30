package com.justdo.plug.post.domain.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", url = "${application.config.auths-url}")
public interface AuthClient {

    @GetMapping("blogs/{memberId}")
    String getMemberName(@PathVariable Long memberId);


}
