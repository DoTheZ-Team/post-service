package com.justdo.plug.post.domain.post.repository;

import com.justdo.plug.post.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.blog_id = :blog_id")
    List<Post> findByBlogId(Long blog_id);


}
