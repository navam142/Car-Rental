package com.example.carrental.service;

import com.cloudinary.Cloudinary;
import com.example.carrental.exceptions.ImageUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final Cloudinary cloudinary;

    public String uploadProductImage(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of("folder", "carrental/vehicles")
            );
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new ImageUploadException("Failed to upload image to Cloudinary");
        }
    }
}
