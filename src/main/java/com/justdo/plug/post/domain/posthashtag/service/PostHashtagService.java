package com.justdo.plug.post.domain.posthashtag.service;

import com.justdo.plug.post.domain.hashtag.Hashtag;
import com.justdo.plug.post.domain.hashtag.service.HashtagService;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.post.dto.HashtagRequestDTO;
import com.justdo.plug.post.domain.post.repository.PostRepository;
import com.justdo.plug.post.domain.posthashtag.PostHashtag;
import com.justdo.plug.post.domain.posthashtag.repository.PostHashtagRepository;
import com.justdo.plug.post.global.exception.ApiException;
import com.justdo.plug.post.global.response.code.status.ErrorStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostHashtagService {

    private final PostHashtagRepository postHashtagRepository;
    private final HashtagService hashtagService;
    private final PostRepository postRepository;

    /**
     * Hashtag 생성
     */
    @Transactional
    public void createHashtag(List<String> hashtags, Post post) {

        System.out.println(hashtags);
        Optional.ofNullable(hashtags)
            .ifPresent(list -> list.forEach(hashtag -> {

                // 해시태그 이름으로 해시태그 ID를 가져오는 메서드
                Hashtag findHashtag = hashtagService.getHashtagIdByName(hashtag);

                // Post_Hashtag 엔티티 생성
                PostHashtag postHashtag = new PostHashtag(post, findHashtag);

                // Post_Hashtag 엔티티 저장
                save(postHashtag);
            }));
    }

    @Transactional
    public void changeHashtag(List<String> newHashtags, Post post) {
        postHashtagRepository.deleteByPost(post);

        newHashtags.forEach(hashtagName -> {

            Hashtag hashtag = hashtagService.getHashtagIdByName(hashtagName);

            PostHashtag postHashtag = new PostHashtag(post, hashtag);

            postHashtagRepository.save(postHashtag);
        });
    }

    // BLOG009: 블로그 아이디로 해시태그 추출하기
    public List<String> getHashtagsBlog(Long blogId) {

        // 블로그 아이디에 해당하는 포스트를 가져온다.
        List<Post> blogPosts = postRepository.findByBlogId(blogId);

        // 블로그 아이디에 해당하는 포스트가 없는 경우
        if (blogPosts.isEmpty()) {
            throw new ApiException(ErrorStatus._NO_HASHTAGS);
        }

        // 블로그 아이디에 해당하는 포스트의 아이디만 추출하여 반환
        List<Long> postIds = blogPosts.stream()
            .map(Post::getId)
            .toList();

        List<String> hashtagNames = new ArrayList<>();

        // 각 포스트별로 해당하는 해시태그 아이디를 추출하여 저장
        for (Long postId : postIds) {
            List<PostHashtag> postHashtags;
            postHashtags = getPostHashtags(postId);

            for (PostHashtag postHashtag : postHashtags) {
                // 아이디에서 해시태그 명으로 변경 후 리스트에 저장
                String hashtagName = hashtagService.getHashtagNameById(
                    postHashtag.getHashtag().getId());
                hashtagNames.add(hashtagName);
            }
        }

        return hashtagNames;
    }

    // BLOG007: 해시태그 값 추출
    public List<String> getHashtags(Long memberId) {
        // 멤버 아이디에 해당하는 포스트를 가져온다.
        List<Post> memberPosts = postRepository.findByMemberId(memberId);

        // 멤버 아이디에 해당하는 포스트가 없는 경우
        if (memberPosts.isEmpty()) {
            throw new ApiException(ErrorStatus._NO_HASHTAGS);
        }

        // 멤버 아이디에 해당하는 포스트의 아이디만 추출하여 반환
        List<Long> postIds = memberPosts.stream()
            .map(Post::getId)
            .toList();

        List<String> hashtagNames = new ArrayList<>();

        // 각 포스트별로 해당하는 해시태그 아이디를 추출하여 저장
        for (Long postId : postIds) {
            List<PostHashtag> postHashtags;
            postHashtags = getPostHashtags(postId);

            for (PostHashtag postHashtag : postHashtags) {
                // 아이디에서 해시태그 명으로 변경 후 리스트에 저장
                String hashtagName = hashtagService.getHashtagNameById(
                    postHashtag.getHashtag().getId());
                hashtagNames.add(hashtagName);
            }
        }

        return hashtagNames;
    }

    public List<PostHashtag> getPostHashtags(Long postId) {
        return postHashtagRepository.findByPostId(postId);
    }

    public void save(PostHashtag postHashtag) {
        postHashtagRepository.save(postHashtag);
    }

    /**
     * 최신 Post의 hashtag 조회
     */
    public List<String> getHashtagNamesByPost(List<Post> posts) {

        List<PostHashtag> postHashtags = postHashtagRepository.findByPostList(posts);

        return postHashtags.stream()
            .map(ph -> hashtagService.getHashtagNameById(ph.getHashtag().getId()))
            .distinct()
            .toList();
    }

    public void deletePostHashtags(Long postId) {
        List<PostHashtag> postHashtags = postHashtagRepository.findByPostId(postId);
        postHashtagRepository.deleteAll(postHashtags);
    }

    public void sendNewHashtags(Long memberId, List<String> hashtags){
        HashtagRequestDTO hashtagRequestdto = new HashtagRequestDTO();

        hashtagRequestdto.setHashtags(hashtags);
        hashtagRequestdto.setMemberId(memberId);

        //TODO: Recommend Service 로 POST API 요청하기

    }
}
