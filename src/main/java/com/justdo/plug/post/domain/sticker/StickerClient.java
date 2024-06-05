package com.justdo.plug.post.domain.sticker;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "sticker-service", url ="${application.config.sticker-url}")
public interface StickerClient {

    @GetMapping("/poststickers")
    PostStickerDTO.PostStickerUrlItems getStickersByPostId(@RequestParam("postId") Long postId);
    @PostMapping("/post-list")
    void savePostStickers(@RequestBody List<PostStickerDTO.PostStickerItem> postStickerItemList);
}