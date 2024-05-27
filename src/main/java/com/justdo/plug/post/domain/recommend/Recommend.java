package com.justdo.plug.post.domain.recommend;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name ="recommend-service", url = "${application.config.recommend-url}")
public interface Recommend {


}
