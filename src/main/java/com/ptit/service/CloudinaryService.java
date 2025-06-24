package com.ptit.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface CloudinaryService {
    Map<String, Object> uploadFile(MultipartFile file, String folder);
    String getImageUrl(String publicId);
    void deleteFile(String publicId);
}
