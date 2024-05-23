package com.justdo.plug.post.domain.likes.repository;

import com.justdo.plug.post.domain.likes.Likes;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByPostIdAndMemberId(Long postId, Long memberId);

    @Transactional
    void deleteByPostId(Long postId);
}
