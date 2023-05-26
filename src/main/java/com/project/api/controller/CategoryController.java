package com.project.api.controller;

import com.project.api.dao.CategoryRepository;
import com.project.api.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.NoResultException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000",allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("api/category")
public class CategoryController {

    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping // Get Category List
    public ResponseEntity<List<Category>> getAllCategory() {
        List<Category> listCategory = categoryRepository.findAll();
        return new ResponseEntity<>(listCategory, HttpStatus.OK);
    }

    @GetMapping("/{categoryId}") // Get Category ID
    public ResponseEntity<Category> getCategoryById(@PathVariable("categoryId") Long categoryId) {
        Category category = categoryRepository.findById(categoryId).get();

        if (category == null) {
            throw new NoResultException("This category not exists");
        }

        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PostMapping("/add") // Insert/Add Category
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category existCategory = categoryRepository.findByCategoryName(category.getCategoryName());

        if (existCategory == null) {
            Category newCategory = categoryRepository.save(category);
            return new ResponseEntity<>(newCategory, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, "Category already exits");
        }
    }

    @PutMapping("/update/{categoryId}") // Edit Category
    public ResponseEntity<Category> updateCategory(@RequestBody Category category,
                                                   @PathVariable("categoryId") Long categoryId) {
        Category existCategory = categoryRepository.findById(categoryId).get();
        existCategory.setCategoryName(category.getCategoryName());

        Category updateCategory = categoryRepository.save(existCategory);
        return new ResponseEntity<>(updateCategory, HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}") // Delete Category
    public void deleteCategory(@PathVariable("categoryId") Long categoryId) {
        Category existCategory = categoryRepository.findById(categoryId).get();
        categoryRepository.delete(existCategory);
    }
}
