package com.justdo.plug.post.domain.post.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class PostUpdateDto {

    private String title;
    private String content;
    private List<String> hashtags;
    private String categoryName;
    private List<String> photoUrls;

}
