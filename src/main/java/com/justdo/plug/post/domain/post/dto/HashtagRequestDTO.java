package com.justdo.plug.post.domain.post.dto;

import lombok.Data;
import lombok.Setter;

import java.util.List;

@Setter
@Data
public class HashtagRequestDTO {

    private Long memberId;
    private List<String> hashtags;

}
