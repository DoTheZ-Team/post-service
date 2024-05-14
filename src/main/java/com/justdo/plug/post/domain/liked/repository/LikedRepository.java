package com.justdo.plug.post.domain.liked.repository;

import com.justdo.plug.post.domain.liked.Liked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikedRepository extends JpaRepository<Liked, Long> {
    Liked findByPostIdAndMemberId(Long postId, Long memberId);
}
