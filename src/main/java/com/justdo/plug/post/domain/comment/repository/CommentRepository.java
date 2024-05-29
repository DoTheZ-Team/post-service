package com.justdo.plug.post.domain.comment.repository;

import com.justdo.plug.post.domain.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByPostIdAndParentCommentIsNull(Long postId, PageRequest pageRequest);

    Page<Comment> findAllByParentCommentId(Long parentId, PageRequest pageRequest);

    void deleteByPostId(Long postId);
}
