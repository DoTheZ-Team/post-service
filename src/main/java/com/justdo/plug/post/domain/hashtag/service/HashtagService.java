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
        Hashtag hashtag = Optional.ofNullable(hashtagRepository.findByName(hashtagName))
                .orElseThrow(() -> new ApiException(ErrorStatus._HASHTAG_NOT_FOUND));

        return hashtag.getId();
    }
}
