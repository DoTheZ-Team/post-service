package com.justdo.plug.post.domain.likes.dto;

import com.justdo.plug.post.domain.likes.Likes;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LikesResponse {

    private Long likeId;
    private LocalDateTime createdAt;

    public static LikesResponse toLikeResponse(Likes likes) {

        return LikesResponse.builder()
                .likeId(likes.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
