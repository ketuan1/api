package com.project.api.controller;


import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.xml.ws.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("api/file")
@CrossOrigin("http://localhost:3000")
public class FileController {

    @GetMapping(value = "/image/{filename}", produces = MediaType.IMAGE_JPEG_VALUE) // Display Image
    @ResponseBody
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) throws IOException {
        Path file = Paths.get("upload/image").resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() && resource.isReadable()) {
            return new ResponseEntity<>(FileUtils.readFileToByteArray(resource.getFile()), HttpStatus.OK);
        } else {
            throw new RuntimeException("Can't find image");
        }
    }

//    @PostMapping("/upload") // Upload Image
//    public ResponseEntity<String> uploadFile(@RequestParam("imageUrl") MultipartFile file) {
//        String message;
//
//        try {
//            Files.copy(file.getInputStream(), Paths.get("upload/image").resolve(file.getOriginalFilename()));
//
//            message = "Upload successfully: " + file.getOriginalFilename();
//            return new ResponseEntity<>(message, HttpStatus.OK);
//        } catch (Exception e) {
//            message = "Upload failed: " + file.getOriginalFilename();
//            return new ResponseEntity<>(message, HttpStatus.EXPECTATION_FAILED);
//        }
//    }

    public String uploadFile(MultipartFile file) {
        try {
            Path uploadDir = Paths.get("upload/image");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/upload/image")
                    .path(file.getOriginalFilename())
                    .toUriString();
            return imageUrl;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }

    }

    @DeleteMapping(value = "/image/{filename}") // Delete Image
    public ResponseEntity<Boolean> deleteFile(@PathVariable String filename) throws IOException {
        Path file = Paths.get("upload/image").resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() && resource.isReadable()) {
            return new ResponseEntity<>(FileUtils.deleteQuietly(resource.getFile()), HttpStatus.OK);
        } else {
            throw new RuntimeException("Not file delete");
        }
    }
}
