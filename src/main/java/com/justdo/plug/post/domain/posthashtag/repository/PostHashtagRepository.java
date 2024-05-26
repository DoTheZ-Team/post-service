package com.justdo.plug.post.domain.posthashtag.repository;

import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.posthashtag.PostHashtag;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long>{
    List<PostHashtag> findByPostId(Long postId);

    @Query("SELECT ph FROM PostHashtag ph WHERE ph.post IN :postList")
    List<PostHashtag> findByPostList(List<Post> postList);

    @Transactional
    void deleteByPost(Post post);

}
