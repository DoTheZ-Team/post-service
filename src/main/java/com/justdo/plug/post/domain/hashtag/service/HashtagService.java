package com.justdo.plug.post.domain.hashtag.service;

import com.justdo.plug.post.domain.hashtag.Hashtag;
import com.justdo.plug.post.domain.hashtag.repository.HashtagRepository;
import com.justdo.plug.post.global.exception.ApiException;
import com.justdo.plug.post.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    public Hashtag getHashtagIdByName(String hashtagName) {
        Hashtag hashtag = hashtagRepository.findByName(hashtagName);

        return (hashtag == null) ? createNewHashtag(hashtagName) : hashtag;
    }

    @Transactional
    public Hashtag createNewHashtag(String hashtagName) {
        Hashtag newHashtag = Hashtag.builder()
                .name(hashtagName)
                .build();

        return hashtagRepository.save(newHashtag);
    }

    public String getHashtagNameById(Long hashtagId) {
        Hashtag hashtag = hashtagRepository.findById(hashtagId)
                .orElseThrow(() -> new ApiException(ErrorStatus._HASHTAG_NOT_FOUND));
        return hashtag.getName();
    }

}
