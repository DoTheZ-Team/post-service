package com.justdo.plug.post.domain.photo.service;

import com.justdo.plug.post.domain.photo.Photo;
import com.justdo.plug.post.domain.photo.repository.PhotoRepository;
import com.justdo.plug.post.domain.post.Post;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;

    public void createPhoto(List<String> photoUrls, Post post) {

        List<Photo> photoList = photoUrls.stream()
            .map(photo -> new Photo(photo, post))
            .toList();

        photoRepository.saveAll(photoList);
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
}
