package com.justdo.plug.post.domain.posthashtag.repository;

import com.justdo.plug.post.domain.posthashtag.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long>{

}
