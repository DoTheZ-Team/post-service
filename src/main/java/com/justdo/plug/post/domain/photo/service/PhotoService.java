package com.justdo.plug.post.domain.photo.service;

import com.justdo.plug.post.domain.photo.Photo;
import com.justdo.plug.post.domain.photo.repository.PhotoRepository;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.post.dto.PostRequestDto;
import com.justdo.plug.post.domain.post.dto.PostUpdateDto;
import com.justdo.plug.post.domain.post.repository.PostRepository;
import com.justdo.plug.post.global.exception.ApiException;
import com.justdo.plug.post.global.response.code.status.ErrorStatus;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final PostRepository postRepository;

    public void createPhoto(List<String> photoUrls, Post post) {

        Optional.ofNullable(photoUrls)
            .ifPresent(list -> list.forEach(photoUrl -> {
                photoRepository.save(new Photo(photoUrl, post));
            }));
    }

    public String findPhotoByPostId(Long postId) {

        return photoRepository.findFirstByPostId(postId)
            .map(Photo::getPhotoUrl)
            .orElse(null);

    }

    public List<String> findPhotoUrlsByPosts(List<Post> posts) {

        return posts.stream()
            .map(post -> findPhotoByPostId(post.getId()))
            .toList();
    }

    @Transactional
    public void updatePhotoUrls(Long postId, PostUpdateDto updateDto) {
        // 기존의 photos 삭제
        photoRepository.deleteByPostId(postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(ErrorStatus._POST_NOT_FOUND));

        // 새로운 photoUrl 리스트를 기반으로 Photo 엔티티 생성
        List<Photo> newPhotos = updateDto.getPhotoUrls().stream()
                .map(url -> new Photo(url, post))
                .collect(Collectors.toList());

        // 새로운 photos 저장
        photoRepository.saveAll(newPhotos);
    }


}
