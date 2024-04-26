package com.justdo.plug.post.domain.hashtag.service;

import com.justdo.plug.post.domain.hashtag.Hashtag;
import com.justdo.plug.post.domain.hashtag.repository.HashtagRepository;
import com.justdo.plug.post.global.exception.ApiException;
import com.justdo.plug.post.global.response.code.status.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;

    public Long getHashtagIdByName(String hashtagName) {
        Hashtag hashtag = hashtagRepository.findByName(hashtagName);
        if (hashtag == null) {
            // 만약 해당하는 이름의 해시태그가 없는 경우 새로운 해시태그를 생성
            hashtag = createNewHashtag(hashtagName);
        }
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
