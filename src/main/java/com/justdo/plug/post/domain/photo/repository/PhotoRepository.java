package com.justdo.plug.post.domain.photo.repository;
import com.justdo.plug.post.domain.photo.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long>{

}
