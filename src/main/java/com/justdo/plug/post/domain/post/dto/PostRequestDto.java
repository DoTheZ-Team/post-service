package com.justdo.plug.post.domain.post.dto;

import com.justdo.plug.post.domain.post.Post;
import lombok.*;

import java.util.List;

@Data
@Builder
public class PostRequestDto {
    private String title;
    private String content;
    private int like_count;
    private boolean temporary_state;
    private boolean state;
    private long member_id;
    private long blog_id;
    private List<String> hashtags;
    private String name;
    private String photo_url;


    public Post toEntity(){
        return Post.builder()
                .title(title)
                .content(content)
                .temporary_state(temporary_state)
                .state(state)
                .member_id(member_id)
                .blog_id(blog_id)
                .build();
    }
}
