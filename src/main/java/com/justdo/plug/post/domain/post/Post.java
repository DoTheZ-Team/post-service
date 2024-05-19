package com.justdo.plug.post.domain.post;

import com.justdo.plug.post.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postId")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean temporaryState = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean state = true;

    @Column(nullable = false)
    private Long blogId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String preview;

    @Column
    private String esId;

    // 필드 값 변경을 위한 메서드
    public void changeEsId(String esId) {
        this.esId = esId;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeTitle(String title) {
        this.title = title;
    }


    public void changePreview(String preview) {
        this.preview = preview;
    }
}
