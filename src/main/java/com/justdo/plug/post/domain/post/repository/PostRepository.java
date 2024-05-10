package com.justdo.plug.post.domain.post.repository;

import com.justdo.plug.post.domain.post.Post;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByBlogId(Long blogId);

    List<Post> findTop4ByBlogIdOrderByCreatedAtDesc(Long blogId);

    List<Post> findByMemberId(Long memberId);

    @Query("SELECT p FROM Post p WHERE p.blogId in :blogIdList")
    Slice<Post> findByBlogIdList(List<Long> blogIdList, PageRequest pageRequest);

    @Query("SELECT p FROM Post p WHERE p.memberId in :memberIdList")
    Slice<Post> findByMemberIdList(List<Long> memberIdList, PageRequest pageRequest);
}
