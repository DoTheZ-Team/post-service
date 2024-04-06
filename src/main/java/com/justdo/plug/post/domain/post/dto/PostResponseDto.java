package com.justdo.plug.post.domain.post.dto;
import lombok.*;

import java.time.LocalDateTime;

@Data
public class PostResponseDto {
    private Long post_id;
    private String title;
    private String content;
    private int like_count;
    private boolean temporary_state;
    private boolean state;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private long member_id;
    private long blog_id;
}
