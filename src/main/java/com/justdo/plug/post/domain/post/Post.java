package com.justdo.plug.post.domain.post;

import com.justdo.plug.post.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseTimeEntity {


    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int like_count;

    @Column(nullable = false)
    private boolean temporaryState;

    @Column(nullable = false)
    private boolean state;

    @Column(nullable = false, updatable = false)
    private Long blogId;

    @Column(nullable = false, updatable = false)
    private Long memberId;

}
