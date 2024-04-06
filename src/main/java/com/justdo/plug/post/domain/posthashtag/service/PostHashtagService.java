package com.justdo.plug.post.domain.posthashtag.service;

import com.justdo.plug.post.domain.hashtag.service.HashtagService;
import com.justdo.plug.post.domain.posthashtag.PostHashtag;
import com.justdo.plug.post.domain.posthashtag.repository.PostHashtagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostHashtagService {
    private final PostHashtagRepository postHashtagRepository;
    private final HashtagService hashtagService;
    public void createHashtag(List<String> hashtags, Long post_id){

        for (String hashtag : hashtags) {

            // 해시태그 이름으로 해시태그 ID를 가져오는 메서드
            Long hashtag_id = hashtagService.getHashtagIdByName(hashtag);

            // Post_Hashtag 엔티티 생성
            PostHashtag postHashtag = new PostHashtag();
            postHashtag.setPostId(post_id);
            postHashtag.setHashtagId(hashtag_id);

            // Post_Hashtag 엔티티 저장
            save(postHashtag);
        }

    }
    public void save(PostHashtag postHashtag) {
        postHashtagRepository.save(postHashtag);
    }
}
