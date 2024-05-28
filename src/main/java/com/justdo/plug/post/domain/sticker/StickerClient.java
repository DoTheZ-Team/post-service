package com.justdo.plug.post.domain.sticker;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "sticker-service", url ="${application.config.sticker-url}")
public interface StickerClient {

    @GetMapping("/poststickers")
    PostStickerDTO.PostStickerUrlItems getStickers(@RequestParam("postId") Long postId);

}