package com.justdo.plug.post.domain.photo.service;

import com.justdo.plug.post.domain.photo.Photo;
import com.justdo.plug.post.domain.photo.repository.PhotoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;

    public void createPhoto(String photoUrl, Long postId){
        Photo photo = new Photo();
        photo.setPostId(postId);
        photo.setPhotoUrl(photoUrl);
        save(photo);
    }

    public void save(Photo photo){
        photoRepository.save(photo);
    }
}
