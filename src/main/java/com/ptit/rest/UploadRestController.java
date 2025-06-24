package com.ptit.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ptit.service.CloudinaryService;
import com.ptit.service.UploadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

@CrossOrigin("*")
@RestController
public class UploadRestController {
    
    @Autowired
    CloudinaryService cloudinaryService;
    
    @Autowired
    UploadService uploadService;

    // Test endpoint
    @PostMapping("/rest/upload/test")
    public ResponseEntity<?> testUpload() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("message", "Upload service is working");
            node.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(node);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/rest/upload/{folder}")
    public JsonNode upload(@RequestParam("file") MultipartFile file, @PathVariable("folder") String folder) {
        try {
            System.out.println("Uploading file: " + file.getOriginalFilename() + " to folder: " + folder);
            
            // Validate file
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }
            
            if (!file.getContentType().startsWith("image/")) {
                throw new RuntimeException("File must be an image");
            }
            
            // Upload to Cloudinary
            Map<String, Object> result = cloudinaryService.uploadFile(file, folder);
            
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            
            // Return Cloudinary URL and public_id
            node.put("url", result.get("secure_url").toString());
            node.put("publicId", result.get("public_id").toString());
            node.put("name", result.get("original_filename").toString());
            node.put("size", file.getSize());
            
            System.out.println("Upload successful. URL: " + result.get("secure_url").toString());
            
            return node;
        } catch (Exception e) {
            System.err.println("Cloudinary upload failed: " + e.getMessage());
            e.printStackTrace();
              // Fallback to local storage if Cloudinary fails
            try {
                File savedFile = uploadService.save(file, folder);
                
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();
                node.put("name", savedFile.getName());
                node.put("size", savedFile.length());
                node.put("url", "/assets/" + folder + "/" + savedFile.getName());
                node.put("error", "Cloudinary failed, using local storage");
                
                System.out.println("Fallback to local storage. URL: " + "/assets/" + folder + "/" + savedFile.getName());
                
                return node;
            } catch (Exception fallbackError) {
                System.err.println("Local storage fallback also failed: " + fallbackError.getMessage());
                fallbackError.printStackTrace();
                
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode errorNode = mapper.createObjectNode();
                errorNode.put("error", "Upload failed completely: " + e.getMessage());
                errorNode.put("fallbackError", fallbackError.getMessage());
                
                return errorNode;
            }
        }
    }
}
