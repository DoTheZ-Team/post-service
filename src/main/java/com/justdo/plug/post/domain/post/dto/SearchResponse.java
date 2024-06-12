package com.justdo.plug.post.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;


public class SearchResponse {

    @Schema(description = "Post 검색 응답 DTO")
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostSearch {

        @Schema(description = "Post 아이디")
        private Long postId;

        @Schema(description = "Post 제목 ")
        private String title;

        @Schema(description = "Post Preview : 포스트 글 부분")
        private String preview;

        @Schema(description = "작성자 Member Id")
        private Long memberId;

        @Schema(description = "Post가 작성된 Blog 아이디")
        private Long blogId;

    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PostInfo {

        private Long blogId;
        private Long postId;
        private String title;
        private String preview;
        private String photoUrl;
    }

    public static PostInfo toPostInfo(PostSearch postSearch, String photoUrl) {

        return PostInfo.builder()
            .blogId(postSearch.getBlogId())
            .postId(postSearch.getPostId())
            .title(postSearch.getTitle())
            .preview(postSearch.getPreview())
            .photoUrl(photoUrl)
            .build();
    }

    @Schema(description = "Post 검색 응답 목록 DTO")
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PostSearchItem {

        @Schema(description = "Post 검색 목록")
        List<PostInfo> postSearchList;

        @Schema(description = "페이징된 리스트의 항목 개수")
        private Integer listSize;

        @Schema(description = "총 페이징 수")
        private Integer totalPage;

        @Schema(description = "전체 데이터의 개수")
        private Integer totalElements;

        @Schema(description = "추가 목록이 있는 지의 여부")
        private Boolean hasNext;

        @Schema(description = "첫 페이지의 여부")
        private Boolean isFirst;

        @Schema(description = "마지막 페이지의 여부")
        private Boolean isLast;
    }

    public static PostSearchItem toPostSearchItem(List<PostSearch> postSearchList,
        List<String> photoUrlList,
        Pageable pageable, int totalValue) {

        List<PostInfo> postInfoList = IntStream.range(0, postSearchList.size())
            .mapToObj(idx -> toPostInfo(postSearchList.get(idx), photoUrlList.get(idx)))
            .toList();

        boolean isFirst = pageable.getPageNumber() == 0;
        boolean isLast = (pageable.getPageNumber() + 1) * pageable.getPageSize() >= totalValue;
        boolean hasNext = !isLast;

        return PostSearchItem.builder()
                .postSearchList(postInfoList)
                .listSize(postInfoList.size())
                .totalPage((int) (Math.ceil((double) totalValue / pageable.getPageSize())))
                .totalElements(totalValue)
                .isFirst(isFirst)
                .isLast(isLast)
                .hasNext(hasNext)
                .build();

        

    }

    /**
     * Blog Search Result
     */
    @Schema(description = "블로그 검색 응답 DTO")
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class BlogInfo {
        @Schema(description = "블로그 아이디", example = "1")
        private Long id;

        @Schema(description = "블로그 제목")
        private String title;

        @Schema(description = "블로그 설명")
        private String description;

        @Schema(description = "블로그 프로필")
        private String profile;

        @Schema(description = "블로그 배경 사진")
        private String background;
    }

    @Schema(description = "블로그 검색 응답 목록 DTO")
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class BlogInfoItem {

        @Schema(description = "블로그 정보 목록")
        private List<BlogInfo> blogInfoList;

        @Schema(description = "페이징된 리스트의 항목 개수")
        private Integer listSize;

        @Schema(description = "총 페이징 수")
        private Integer totalPage;

        @Schema(description = "전체 데이터의 개수")
        private Long totalElements;

        @Schema(description = "추가 목록이 있는 지의 여부")
        private Boolean hasNext;

        @Schema(description = "첫 페이지의 여부")
        private Boolean isFirst;

        @Schema(description = "마지막 페이지의 여부")
        private Boolean isLast;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class SearchInfo {

        private PostSearchItem postSearchItem;
        private BlogInfoItem blogInfoItem;
    }

    public static SearchInfo toSearchInfo(PostSearchItem postSearchItem,
        BlogInfoItem blogInfoItem) {

        return SearchInfo.builder()
            .postSearchItem(postSearchItem)
            .blogInfoItem(blogInfoItem)
            .build();
    }

}
