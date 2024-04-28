package com.justdo.plug.post.domain.post.dto;

import com.justdo.plug.post.domain.post.Post;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

public class PreviewResponse {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PostItem {

        private Long postId;
        private String title;
        private String preview;
        private String photo;
    }

    public static PostItem toPostItem(Post post) {
        return PostItem.builder()
            .postId(post.getId())
            .title(post.getTitle())
            .preview(post.getPreview()) // TODO : 포스트의 Photo 추가
            .build();
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PostItemList {

        private List<PostItem> postItems;
        private boolean hasNext;
        private boolean hasFirst;
        private boolean hasLast;
    }

    public static PostItemList toPostItemList(Slice<Post> posts) {

        List<PostItem> postItems = posts.stream()
            .map(PreviewResponse::toPostItem)
            .toList();

        return PostItemList.builder()
            .postItems(postItems)
            .hasNext(posts.hasNext())
            .hasFirst(posts.isFirst())
            .hasLast(posts.isLast())
            .build();

    }
}
