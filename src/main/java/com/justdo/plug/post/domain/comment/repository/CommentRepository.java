package com.justdo.plug.post.domain.comment.repository;

import com.justdo.plug.post.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
