package com.justdo.plug.post.domain.posthashtag;

import com.justdo.plug.post.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostHashtag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long post_hashtag_id;

    @Column(nullable = false)
    private Long post_id;

    @Column(nullable = false)
    private Long hashtag_id;

    public void setPostId(Long post_id) {
        this.post_id = post_id;
    }

    public void setHashtagId(Long hashtag_id) {
        this.hashtag_id = hashtag_id;
    }
}
