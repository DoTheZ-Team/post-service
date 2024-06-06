package com.justdo.plug.post.domain.likes.service;

import com.justdo.plug.post.domain.likes.Likes;
import com.justdo.plug.post.domain.likes.dto.LikesResponse;
import com.justdo.plug.post.domain.likes.repository.LikesRepository;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikesService {
    private final LikesRepository likeRepository;
    private final PostService postService;

    public LikesResponse postLike(Long postId, Long memberId) {

        return likeRepository.findByPostIdAndMemberId(postId, memberId)
                .map(this::deleteLike)
                .orElseGet(()-> createLike(postService.getPost(postId), memberId));
    }

    @Transactional(readOnly = true)
    public boolean isLike(Long memberId, Long postId) {

        return likeRepository.findByPostIdAndMemberId(postId, memberId).isPresent();
    }

    private LikesResponse deleteLike(Likes likes) {
        likeRepository.delete(likes);
        likes.getPost().decreaseLike();

        return LikesResponse.toLikeResponse(likes);
    }

    private LikesResponse createLike(Post post, Long memberId) {

        Likes newLike = LikesResponse.toEntity(post, memberId);
        likeRepository.save(newLike);
        post.increaseLike();

        return LikesResponse.toLikeResponse(newLike);
    }

}
