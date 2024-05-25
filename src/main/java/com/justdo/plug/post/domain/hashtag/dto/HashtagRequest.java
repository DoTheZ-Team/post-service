package com.justdo.plug.post.domain.hashtag.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class HashtagRequest {
    private Long memberId;
    private List<String> hashtags;

}
