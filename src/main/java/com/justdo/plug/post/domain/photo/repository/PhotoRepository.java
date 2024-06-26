package com.justdo.plug.post.domain.photo.repository;

import com.justdo.plug.post.domain.photo.Photo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Optional<Photo> findFirstByPostId(Long postId);

    List<Photo> findTop5ByPostId(Long postId);

    List<Photo> findAllByPostId(Long postId);

    void deleteByPostId(Long postId);
}