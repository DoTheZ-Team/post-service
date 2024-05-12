package com.justdo.plug.post.elastic;

import com.justdo.plug.post.domain.post.Post;
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

    @Field(type = FieldType.Long, index = false)
    private Long postId;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String preview;

    @Field(type = FieldType.Long, index = false)
    private Long memberId;

    @Field(type = FieldType.Long, index = false)
    private Long blogId;

    public static PostDocument toDocument(Post post) {

        return PostDocument.builder()
            .postId(post.getId())
            .title(post.getTitle())
            .preview(post.getPreview())
            .memberId(post.getMemberId())
            .blogId(post.getBlogId())
            .build();
    }
}
