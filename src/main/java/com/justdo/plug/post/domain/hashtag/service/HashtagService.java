package com.justdo.plug.post.domain.hashtag.service;

import com.justdo.plug.post.domain.hashtag.Hashtag;
import com.justdo.plug.post.domain.hashtag.repository.HashtagRepository;
import com.justdo.plug.post.global.exception.ApiException;
import com.justdo.plug.post.global.response.code.status.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;

    public Long getHashtagIdByName(String hashtagName) {
        Hashtag hashtag = hashtagRepository.findByName(hashtagName);
        // 만약 사용하려는 해시태그가 없다면 새로운 해시태그 생성 후 사용
        hashtag = (hashtag == null) ? createNewHashtag(hashtagName) : hashtag;
        return hashtag.getId();
    }

    private Hashtag createNewHashtag(String hashtagName) {
        Hashtag newHashtag = new Hashtag();
        newHashtag.setName(hashtagName);

        return hashtagRepository.save(newHashtag);
    }

    public String getHashtagNameById(Long hashtagId) {
        Hashtag hashtag = hashtagRepository.findById(hashtagId)
                .orElseThrow(() -> new ApiException(ErrorStatus._HASHTAG_NOT_FOUND));
        return hashtag.getName();
    }
}
