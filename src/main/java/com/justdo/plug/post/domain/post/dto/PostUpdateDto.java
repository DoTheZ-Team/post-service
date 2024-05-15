package com.justdo.plug.post.domain.post.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostUpdateDto {
    private String title;
    private String content;
    private List<String> hashtags;
    private String categoryName;
    private List<String> photoUrl;

}
