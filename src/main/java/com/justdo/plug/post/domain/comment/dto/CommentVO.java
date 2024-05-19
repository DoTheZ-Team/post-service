package com.justdo.plug.post.domain.comment.dto;

public record CommentVO(Long memberId, String content, Long parentId, Long postId) {

    public static CommentVO of(Long memberId, CommentRequest.PostComment post, Long postId) {
        return new CommentVO(memberId, post.getContent(), post.getParentId(), postId);
    }
}
