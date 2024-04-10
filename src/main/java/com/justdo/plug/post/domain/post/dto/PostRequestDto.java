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
    private boolean temporaryState;
    private boolean state;
    private long memberId;
    private long blogId;
    private List<String> hashtags;
    private String name;
    private String photo_url;


    public Post toEntity(){
        return Post.builder()
                .title(title)
                .content(content)
                .temporaryState(temporaryState)
                .state(state)
                .memberId(memberId)
                .blogId(blogId)
                .build();
    }
}
