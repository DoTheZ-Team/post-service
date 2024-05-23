package com.justdo.plug.post.domain.post.repository;

import com.justdo.plug.post.domain.post.Post;
import feign.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByBlogId(Long blogId);

    List<Post> findTop4ByBlogIdOrderByCreatedAtDesc(Long blogId);

    Page<Post> findAllByBlogId(Long blogId, PageRequest pageRequest);

    List<Post> findByMemberId(Long memberId);

    Optional<Post> findByEsId(String esId);

    void deleteByEsId(String esId);


    @Query("SELECT p FROM Post p WHERE p.blogId in :blogIdList")
    Slice<Post> findByBlogIdList(List<Long> blogIdList, PageRequest pageRequest);

    @Query("SELECT p FROM Post p WHERE p.memberId in :memberIdList")
    Slice<Post> findByMemberIdList(List<Long> memberIdList, PageRequest pageRequest);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    void increaseLikeCount(@Param("postId") Long postId);

}
