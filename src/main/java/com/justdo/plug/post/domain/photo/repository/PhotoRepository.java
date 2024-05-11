package com.justdo.plug.post.domain.photo.repository;

import com.justdo.plug.post.domain.photo.Photo;
import com.justdo.plug.post.domain.posthashtag.PostHashtag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Optional<Photo> findFirstByPostId(Long postId);
}
