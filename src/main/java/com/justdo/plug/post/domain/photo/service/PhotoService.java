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

    public void createPhoto(String photo_url, Long post_id){
        Photo photo = new Photo();
        photo.setPost_id(post_id);
        photo.setPhoto_url(photo_url);
        save(photo);
    }

    public void save(Photo photo){
        photoRepository.save(photo);
    }
}
