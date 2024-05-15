package com.justdo.plug.post.domain.likes.service;

import com.justdo.plug.post.domain.likes.Likes;
import com.justdo.plug.post.domain.likes.repository.LikesRepository;
import com.justdo.plug.post.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikesService {
    private final LikesRepository likeRepository;
    private final PostService postService;

    // 좋아요 등록
    public String postLike(Long postId, Long memberId) {
        try {

            Likes likes = Likes.builder()
                    .postId(postId)
                    .memberId(memberId)
                    .build();

            likeRepository.save(likes);
            postService.getLiked(postId); // 포스트에 좋아요 추가

            return "게시물 좋아요가 성공적으로 등록되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "게시물 좋아요 등록 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    // 좋아요 취소
    public String postLikeCancel(Long postId, Long memberId) {
        Likes likepost = likeRepository.findByPostIdAndMemberId(postId, memberId);

        try {
            likeRepository.delete(likepost);
            return "게시물 좋아요가 취소되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "게시물 좋아요 취소 중 오류가 발생했습니다: " + e.getMessage();
        }

    }
}
