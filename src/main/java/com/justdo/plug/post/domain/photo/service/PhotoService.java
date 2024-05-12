package com.justdo.plug.post.domain.photo.service;

import com.justdo.plug.post.domain.hashtag.Hashtag;
import com.justdo.plug.post.domain.photo.Photo;
import com.justdo.plug.post.domain.photo.repository.PhotoRepository;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.domain.posthashtag.PostHashtag;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    /*
    public void createPhoto(List<String> photoUrl, Post post) {
        photoRepository.save(new Photo(photoUrl, post));
    }
     */

    @Transactional
    public void createPhoto(List<String> photoUrl, Post post) {

        for (String photoUrls : photoUrl) {
            // 해시태그 이름으로 해시태그 ID를 가져오는 메서드
            photoRepository.save(new Photo(photoUrls, post));
        }
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
