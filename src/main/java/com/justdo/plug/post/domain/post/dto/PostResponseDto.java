package com.justdo.plug.post.domain.post.dto;
import com.justdo.plug.post.domain.post.Post;
import lombok.*;
import org.json.JSONArray;

import java.time.LocalDateTime;
import java.util.List;

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

    // SUB: 게시글 반환 함수
    public static PostResponseDto createFromPost(Post post) {
        String JsonContent = post.getContent();
        JSONArray jsonArray = new JSONArray(JsonContent);
        List<Object> list = jsonArray.toList();
        Object[] array = list.toArray();

        return PostResponseDto.builder()
                .post_id(post.getId())
                .title(post.getTitle())
                .content(array)
                .like_count(post.getLike_count())
                .temporary_state(post.isTemporary_state())
                .state(post.isState())
                .created_at(post.getCreatedAt())
                .updated_at(post.getUpdatedAt())
                .member_id(post.getMember_id())
                .blog_id(post.getBlog_id())
                .build();
    }
}
