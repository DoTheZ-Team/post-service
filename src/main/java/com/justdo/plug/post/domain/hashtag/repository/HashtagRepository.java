package com.justdo.plug.post.domain.hashtag.repository;

import com.justdo.plug.post.domain.hashtag.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Hashtag findByName(String hashtagName);

}
