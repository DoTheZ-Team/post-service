package com.justdo.plug.post.domain.category.service;

import com.justdo.plug.post.domain.category.Category;
import com.justdo.plug.post.domain.category.repository.CategoryRepository;
import com.justdo.plug.post.domain.post.Post;
import com.justdo.plug.post.global.exception.ApiException;
import com.justdo.plug.post.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public void createCategory(String name, Post post) {

        categoryRepository.save(new Category(name, post));
    }

    public String getHashtags(Long postId) {
        Category category = categoryRepository.findByPostId(postId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CATEGORY_NOT_FOUND));

        return category.getName();
    }

}
