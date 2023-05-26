package com.project.api.controller;

import com.project.api.dao.CategoryRepository;
import com.project.api.dao.ProductRepository;
import com.project.api.dto.ProductDto;
import com.project.api.dto.ProductRequest;
import com.project.api.entity.Category;
import com.project.api.entity.Product;
// import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@CrossOrigin(origins = "http://localhost:3000",allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private FileController fileController;

    @Autowired
    public ProductController(ProductRepository productRepository,
                             CategoryRepository categoryRepository,
                             FileController fileController) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileController = fileController;
    }

    @GetMapping // Get Product full list
    public ResponseEntity<List<ProductDto>> getAllProduct() {
        List<Product> listProducts = productRepository.findAll();

        List<ProductDto> listProductDto = listProducts.stream()
                .map(product -> new ProductDto(product))
                .collect(Collectors.toList());
        return new ResponseEntity<>(listProductDto, HttpStatus.OK);
    }

    @GetMapping("/{productId}") // Get Product by id
    public ResponseEntity<ProductDto> getProductById(@PathVariable("productId") Long productId) {
        Product product = productRepository.findById(productId).get();
        ProductDto productDto = new ProductDto(product);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    @GetMapping("/byCategory={categoryId}")
    public ResponseEntity<List<ProductDto>> getProductByCategoryId(@PathVariable("categoryId") Long categoryId) {
        List<Product> listProducts  = productRepository.findByCategoryId(categoryId);
        List<ProductDto> listProductDto = listProducts.stream()
                .map(product -> new ProductDto(product))
                .collect(Collectors.toList());
        return new ResponseEntity<>(listProductDto, HttpStatus.OK);
    }

    @PostMapping("/add") // Insert/Add Product
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest product) {
        Long categoryId = product.getCategoryId();
        Category category = categoryRepository.findById(categoryId).get();

        Product existProduct = productRepository.findByName(product.getName());

        if (existProduct != null) {
            existProduct.setUnitsInStock(existProduct.getUnitsInStock() + product.getUnitsInStock());
            existProduct.setPrice(product.getPrice());
            existProduct.setImageUrl(product.getImageUrl());
            existProduct.setCategory(category);

            Product updatedProduct = productRepository.save(existProduct);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } else {
            Product newProduct = new Product();
            newProduct.setName(product.getName());
            newProduct.setDescription(product.getDescription());
            newProduct.setPrice(product.getPrice());
            newProduct.setImageUrl(product.getImageUrl());
            newProduct.setUnitsInStock(product.getUnitsInStock());
            newProduct.setCategory(category);

            Product savedProduct = productRepository.save(newProduct);
            return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
        }
    }

    @PutMapping("/update/{productId}") // Edit Product
    public ResponseEntity<Product> updateProduct(@RequestBody ProductRequest product,
                                                 @PathVariable("productId") Long productId) {
        Product existProduct = productRepository.findById(productId).get();

        if (existProduct != null) {
            existProduct.setName(product.getName());
            existProduct.setPrice(product.getPrice());
            existProduct.setDescription(product.getDescription());
            existProduct.setUnitsInStock(product.getUnitsInStock());
            existProduct.setLastUpdated(new Date());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not exist");
        }

        Product updateProduct = productRepository.save(existProduct);
        return new ResponseEntity<>(updateProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{productId}") // Delete Product
    public void deleteProduct(@PathVariable("productId") Long productId) {
        Product existProduct = productRepository.findById(productId).get();
        productRepository.delete(existProduct);
    }
}

