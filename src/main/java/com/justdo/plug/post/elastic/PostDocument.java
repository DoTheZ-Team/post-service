package com.justdo.plug.post.elastic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "post")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long, index = false, docValues = false)
    private String postId;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String preview;

    @Field(type = FieldType.Long, index = false, docValues = false)
    private Long memberId;

    @Field(type = FieldType.Long, index = false, docValues = false)
    private Long blogId;

}
