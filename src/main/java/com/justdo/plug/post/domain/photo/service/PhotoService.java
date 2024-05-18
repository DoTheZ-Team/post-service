package com.justdo.plug.post.domain.photo.service;

import com.justdo.plug.post.domain.photo.Photo;
import com.justdo.plug.post.domain.photo.repository.PhotoRepository;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.post.dto.PostUpdateDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;

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
    public void updatePhotoUrls(Post post, Long postId, PostUpdateDto updateDto) {
        // 기존의 photos 삭제
        photoRepository.deleteByPostId(postId);

        // 새로운 photoUrl 리스트를 기반으로 Photo 엔티티 생성
        List<Photo> newPhotos = updateDto.getPhotoUrls().stream()
                .map(url -> new Photo(url, post))
                .collect(Collectors.toList());

        // 새로운 photos 저장
        photoRepository.saveAll(newPhotos);
    }


}
