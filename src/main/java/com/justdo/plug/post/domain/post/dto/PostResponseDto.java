package com.justdo.plug.post.domain.post.dto;
import com.justdo.plug.post.domain.post.Post;
import lombok.*;
import org.json.JSONArray;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostResponseDto {
    private Long postId;
    private String title;
    private Object[] content;
    private int like_count;
    private boolean temporaryState;
    private boolean state;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private Long memberId;
    private Long blogId;

    // SUB: 게시글 반환 함수
    public static PostResponseDto createFromPost(Post post) {
        String JsonContent = post.getContent();
        JSONArray jsonArray = new JSONArray(JsonContent);
        List<Object> list = jsonArray.toList();
        Object[] array = list.toArray();

        return PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(array)
                .like_count(post.getLike_count())
                .temporaryState(post.isTemporaryState())
                .state(post.isState())
                .created_at(post.getCreatedAt())
                .updated_at(post.getUpdatedAt())
                .memberId(post.getMemberId())
                .blogId(post.getBlogId())
                .build();
    }
}