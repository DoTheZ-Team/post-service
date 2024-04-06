package com.justdo.plug.post.domain.post.dto;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponseDto {
    private Long post_id;
    private String title;
    private Object[] content;
    private int like_count;
    private boolean temporary_state;
    private boolean state;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private Long member_id;
    private Long blog_id;
}
