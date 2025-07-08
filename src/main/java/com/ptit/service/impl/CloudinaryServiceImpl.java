package com.ptit.service.impl;

import com.cloudinary.Cloudinary;
import com.ptit.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;    
    
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> uploadFile(MultipartFile file, String folder) {
        try {
            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("folder", folder);
            uploadParams.put("resource_type", "image");
            uploadParams.put("use_filename", true);
            uploadParams.put("unique_filename", false);
            
            return cloudinary.uploader().upload(file.getBytes(), uploadParams);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }

    @Override
    public String getImageUrl(String publicId) {
        return cloudinary.url()
                .secure(true)
                .format("auto")
                .generate(publicId);
    }

    @Override
    public void deleteFile(String publicId) {
        try {
            Map<String, Object> deleteParams = new HashMap<>();
            deleteParams.put("resource_type", "image");
            cloudinary.uploader().destroy(publicId, deleteParams);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file from Cloudinary", e);
        }
    }
}
