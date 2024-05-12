package com.justdo.plug.post.domain.category.service;

import com.justdo.plug.post.domain.category.Category;
import com.justdo.plug.post.domain.category.repository.CategoryRepository;
import com.justdo.plug.post.domain.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public void createCategory(String name, Post post) {

        save(new Category(name, post));
    }

    public void save(Category category) {
        categoryRepository.save(category);
    }
}
