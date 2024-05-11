package com.justdo.plug.post.domain.post;

import com.justdo.plug.post.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Data
public class Post extends BaseTimeEntity {


    @Id
    @GeneratedValue
    @Column(name = "postId")
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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String preview;

    @Column(nullable = false, updatable = false)
    private String esId;

}
