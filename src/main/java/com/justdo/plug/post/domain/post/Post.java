package com.justdo.plug.post.domain.post;

import com.justdo.plug.post.domain.common.BaseTimeEntity;
import com.justdo.plug.post.domain.post.dto.PostUpdateDto;
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
    @Column(name = "post_id")
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
    private Long blogId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String preview;

    @Column
    private String esId;

    @Column
    private String categoryName;

    // UPDATE METHOD
    public void changeEsId(String esId) {
        this.esId = esId;
    }

    public void increaseLike() {
        this.likeCount++;
    }

    public void decreaseLike() {
        this.likeCount--;
    }

    public void changePost(PostUpdateDto request, String preview) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.preview = preview;
        this.categoryName = request.getCategoryName();
    }
}
