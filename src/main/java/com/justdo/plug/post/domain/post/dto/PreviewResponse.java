package com.justdo.plug.post.domain.post.dto;

import com.justdo.plug.post.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

public class PreviewResponse {

    @Schema(description = "Post 미리보기 응답 DTO")
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PostItem {

        @Schema(description = "Blog 아이디")
        private Long blogId;

        @Schema(description = "Post 아이디")
        private Long postId;

        @Schema(description = "Post 제목")
        private String title;

        @Schema(description = "Post 글 미리보기")
        private String preview;

        @Schema(description = "Post의 추가된 사진 경로")
        private String photoUrl;
    }

    public static PostItem toPostItem(Post post, String photoUrl) {
        return PostItem.builder()
                .blogId(post.getBlogId())
                .postId(post.getId())
                .title(post.getTitle())
                .preview(post.getPreview())
                .photoUrl(photoUrl)
                .build();
    }

    public static List<PostItem> toPostItemList(List<Post> posts, List<String> photoUrls) {

        return IntStream.range(0, posts.size())
                .mapToObj(idx -> {
                    return toPostItem(posts.get(idx), photoUrls.get(idx));
                })
                .toList();
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PostItemSlice {

        private List<PostItem> postItems;

        @Schema(description = "다음 페이지의 여부")
        private Boolean hasNext;

        @Schema(description = "첫 페이지의 여부")
        private Boolean isFirst;

        @Schema(description = "마지막 페이지의 여부")
        private Boolean isLast;
    }

    public static PostItemSlice toPostItemSlice(Slice<Post> posts, List<String> photoUrls) {

        List<Post> postList = posts.getContent();

        List<PostItem> postItems = IntStream.range(0, postList.size())
                .mapToObj(idx -> {
                    return toPostItem(postList.get(idx), photoUrls.get(idx));
                })
                .toList();

        return PostItemSlice.builder()
                .postItems(postItems)
                .hasNext(posts.hasNext())
                .isFirst(posts.isFirst())
                .isLast(posts.isLast())
                .build();

    }

    @Schema(description = "내 블로그 전체글 응답 DTO")
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StoryItem {

        private List<PostItem> postItems;

        @Schema(description = "페이징 된 리스트의 항목 개수")
        private Integer listSize;

        @Schema(description = "총 페이징 수")
        private Integer totalPage;

        @Schema(description = "전체 데이터의 개수")
        private Long totalElements;

        @Schema(description = "첫 페이지의 여부")
        private Boolean isFirst;

        @Schema(description = "마지막 페이지의 여부")
        private Boolean isLast;
    }

    public static StoryItem toStoryItem(Page<Post> posts, List<String> photoUrls) {

        List<Post> postList = posts.getContent();

        List<PostItem> postItems = IntStream.range(0, postList.size())
                .mapToObj(idx -> {
                    return toPostItem(postList.get(idx), photoUrls.get(idx));
                })
                .toList();

        return StoryItem.builder()
                .postItems(postItems)
                .listSize(postItems.size())
                .totalPage(posts.getTotalPages())
                .totalElements(posts.getTotalElements())
                .isFirst(posts.isFirst())
                .isLast(posts.isLast())
                .build();
    }

    @Schema(description = "내 블로그의 Post, Hashtag 정보 응답 DTO")
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BlogPostItem {

        private List<PostItem> postItems;

        @Schema(description = "Post에 작성된 해시태그 목록")
        private List<String> hashtagNames;
    }

    public static BlogPostItem toBlogPostItem(List<PostItem> postItems, List<String> hashtagNames) {

        return BlogPostItem.builder()
                .postItems(postItems)
                .hashtagNames(hashtagNames)
                .build();
    }
}
