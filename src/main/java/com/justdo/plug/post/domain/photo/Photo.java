package com.justdo.plug.post.domain.photo;

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

public class Photo extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photo_id;

    @Column(nullable = false)
    private String photo_url;

    @Column(nullable = false)
    private Long post_id;

}
