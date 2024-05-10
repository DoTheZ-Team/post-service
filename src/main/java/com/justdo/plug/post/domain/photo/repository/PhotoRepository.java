package com.justdo.plug.post.domain.photo.repository;
import com.justdo.plug.post.domain.photo.Photo;
import com.justdo.plug.post.domain.posthashtag.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long>{

    List<Photo> findByPostId(Long postId);
}
