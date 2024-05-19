package com.justdo.plug.post.domain.category.repository;

import com.justdo.plug.post.domain.category.Category;
import com.justdo.plug.post.domain.post.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>{
    Optional<Category> findByPostId(Long postId);

    @Transactional
    void deleteByPost(Post post);
}
