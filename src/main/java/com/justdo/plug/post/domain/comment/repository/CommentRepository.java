package com.justdo.plug.post.domain.comment.repository;

import com.justdo.plug.post.domain.comment.Comment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Slice<Comment> findAllByPostIdAndParentCommentIsNull(Long postId, PageRequest pageRequest);

    Slice<Comment> findAllByParentCommentId(Long parentId, PageRequest pageRequest);
}
