package com.justdo.plug.post.domain.blog;

import com.justdo.plug.post.domain.blog.BlogDto.BlogInfo;
import com.justdo.plug.post.domain.post.dto.SearchResponse.BlogInfoItem;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "blog-service", url = "${application.config.blogs-url}")
public interface BlogClient {

    @PostMapping("/search")
    BlogInfoItem findBlogInfoItem(@RequestBody List<Long> blogIdList,
        @RequestParam(value = "page", defaultValue = "0") int page);

    @PostMapping("/comments")
    List<BlogInfo> findBlogInfoToComment(@RequestBody List<Long> memberIdList);

    @PostMapping("/subscriptions")
    SubscriptionResponse.SubscribedProfile checkSubscribeById(@RequestBody SubscriptionRequest.LoginSubscription loginSubscription);

    @GetMapping("members/{memberId}")
    Long getBlogId(@PathVariable Long memberId);
}
