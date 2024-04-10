package com.justdo.plug.post.domain.category.service;

import com.justdo.plug.post.domain.category.Category;
import com.justdo.plug.post.domain.category.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor

public class CategoryService {
    private final CategoryRepository categoryRepository;


    public void createCategory(String name, Long postId){

        Category category = Category.builder().name(name).postId(postId).build();
        save(category);
    }
    public void save(Category category){
        categoryRepository.save(category);
    }
}