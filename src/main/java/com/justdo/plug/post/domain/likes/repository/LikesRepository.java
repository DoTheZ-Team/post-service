package com.justdo.plug.post.domain.likes.repository;

import com.justdo.plug.post.domain.likes.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    Likes findByPostIdAndMemberId(Long postId, Long memberId);
}
