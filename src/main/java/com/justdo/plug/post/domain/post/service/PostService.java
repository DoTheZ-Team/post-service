package com.justdo.plug.post.domain.post.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.justdo.plug.post.domain.hashtag.service.HashtagService;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.post.dto.PostRequestDto;
import com.justdo.plug.post.domain.post.dto.PostResponseDto;
import com.justdo.plug.post.domain.post.repository.PostRepository;
import com.justdo.plug.post.domain.posthashtag.PostHashtag;
import com.justdo.plug.post.domain.posthashtag.repository.PostHashtagRepository;
import com.justdo.plug.post.global.exception.ApiException;
import com.justdo.plug.post.global.response.code.status.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final HashtagService hashtagService;


    // BLOG001: 게시글 리스트 조회
    public List<Post> getAllPosts() {

        return postRepository.findAll();

    }

    // BLOG002: 게시글 상세 페이지 조회
    public PostResponseDto getPostById(Long postId) throws JSONException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(ErrorStatus._POST_NOT_FOUND));

        return PostResponseDto.createFromPost(post);
    }

    // BLOG003: 블로그 작성
    public Post save(PostRequestDto requestDto) {
       
            Post post = requestDto.toEntity();
            return postRepository.save(post);
    }

    // BLOG006: 블로그 게시글 리스트 조회
    public List<Post> getBlogPosts(Long blogId){
        List<Post> posts = postRepository.findByBlogId(blogId);
        if(posts.isEmpty()) {
            throw new ApiException(ErrorStatus._BLOG_NOT_FOUND);
        }
        return posts;
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
            postHashtags = postHashtagRepository.findByPostId(postId);
            for (PostHashtag postHashtag : postHashtags) {
                // 아이디에서 해시태그 명으로 변경 후 리스트에 저장
                String hashtagName = hashtagService.getHashtagNameById(postHashtag.getHashtagId());
                hashtagNames.add(hashtagName);
            }
        }


        return hashtagNames;
    }

    // BlOG008: 게시글의 글만 조회하기
    public String getPreviewPost(Long postId) throws JsonProcessingException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(ErrorStatus._POST_NOT_FOUND));

        String preview = post.getContent();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonArray = mapper.readTree(preview);

        StringBuilder extractedTexts = new StringBuilder();
        for (JsonNode node : jsonArray) {
            JsonNode contentArray = node.path("content");
            if (contentArray.isArray()) {
                for (JsonNode contentObj : contentArray) {
                    if (contentObj.isObject()) {
                        String text = contentObj.path("text").asText();
                        extractedTexts.append(text).append(" ");
                    }
                }
            }
        }

        return extractedTexts.toString().trim();
    }


}
