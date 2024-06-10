package com.justdo.plug.post.domain.photo.service;

import com.justdo.plug.post.domain.photo.Photo;
import com.justdo.plug.post.domain.photo.repository.PhotoRepository;
import com.justdo.plug.post.domain.post.Post;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;

    @Transactional
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

    public List<String> findTop5PhotoByPostId(Long postId) {

        List<String> photoList = photoRepository.findTop5ByPostId(postId)
                .stream()
                .map(Photo::getPhotoUrl)
                .toList();

        return photoList.isEmpty() ? null : photoList;
    }

    public List<List<String>> findTop5PhotoUrlsByPosts(List<Post> posts) {

        return posts.stream()
                .map(post -> findTop5PhotoByPostId(post.getId()))
                .toList();
    }

    public List<String> findPhotoUrlsByPostId(Long postId) {

        List<Photo> photos = photoRepository.findAllByPostId(postId);
        List<String> photoUrls = new ArrayList<>();

        for (Photo photo : photos) {
            String photoUrl = photo.getPhotoUrl();
            photoUrls.add(photoUrl);
        }

        return photoUrls;
    }

    @Transactional
    public void updatePhotoUrls(Post post, Long postId, List<String> photoUrls) {

        photoRepository.deleteByPostId(postId);

        List<Photo> newPhotos = photoUrls.stream()
                .map(url -> new Photo(url, post))
                .collect(Collectors.toList());

        photoRepository.saveAll(newPhotos);
    }


}
