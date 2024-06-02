package com.justdo.plug.post.domain.recommend;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name ="recommend-service", url = "${application.config.recommend-url}")
public interface RecommendClient {

    @PostMapping("/hashtags")
    void sendNewHashtags(@RequestBody Map<String, Object> docFields, @RequestHeader("Authorization") String token);

    @PostMapping("/editings")
    void sendAllHashtags(@RequestBody Map<String, Object> docFields, @RequestHeader("Authorization") String token);


}
