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
    private int likeCount;
    private boolean temporaryState;
    private boolean state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long memberId;
    private Long blogId;
    private boolean isLike;
    private boolean isSubscribe;

    // SUB: 게시글 반환 함수
    public static PostResponseDto createFromPost(Post post, boolean isLike, boolean isSubscribe) {
        String JsonContent = post.getContent();
        JSONArray jsonArray = new JSONArray(JsonContent);
        List<Object> list = jsonArray.toList();
        Object[] array = list.toArray();

        return PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(array)
                .likeCount(post.getLikeCount())
                .temporaryState(post.isTemporaryState())
                .state(post.isState())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .memberId(post.getMemberId())
                .blogId(post.getBlogId())
                .isLike(isLike)
                .isSubscribe(isSubscribe)
                .build();

    }

}
