package com.justdo.plug.post.domain.post;

import com.justdo.plug.post.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.lang.annotation.Documented;


@EqualsAndHashCode(callSuper = true)
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Data
@Document(indexName = "postindex")
public class Post extends BaseTimeEntity {


    @Id
    @GeneratedValue
    @Column(name = "postId")
    private Long id;

    @Column(nullable = false)
    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Field(type = FieldType.Text, name = "content")
    private String content;

    @Column(nullable = false)
    @Field(type = FieldType.Integer, name = "like_count")
    private int like_count;

    @Column(nullable = false)
    @Field(type = FieldType.Boolean, name = "temporaryState")
    private boolean temporaryState;

    @Column(nullable = false)
    @Field(type = FieldType.Boolean, name = "state")
    private boolean state;

    @Column(nullable = false, updatable = false)
    @Field(type = FieldType.Long, name = "blogId")
    private Long blogId;

    @Column(nullable = false, updatable = false)
    @Field(type = FieldType.Long, name = "memberId")
    private Long memberId;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Field(type = FieldType.Text, name = "preview")
    private String preview;

}
