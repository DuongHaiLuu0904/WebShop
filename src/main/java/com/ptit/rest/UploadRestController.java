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
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PostMapping("/rest/upload/{folder}")
    public JsonNode upload(@RequestParam("file") MultipartFile file, @PathVariable("folder") String folder) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }
            
            if (!file.getContentType().startsWith("image/")) {
                throw new RuntimeException("File must be an image");
            }
            
            Map<String, Object> result = cloudinaryService.uploadFile(file, folder);
            
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            
            node.put("url", result.get("secure_url").toString());
            node.put("publicId", result.get("public_id").toString());
            node.put("name", result.get("original_filename").toString());
            node.put("size", file.getSize());
            
            return node;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                File savedFile = uploadService.save(file, folder);
                
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();
                node.put("name", savedFile.getName());
                node.put("size", savedFile.length());
                node.put("url", "/assets/" + folder + "/" + savedFile.getName());
                node.put("error", "Cloudinary failed, using local storage");
                
                return node;
            } catch (Exception fallbackError) {
                fallbackError.printStackTrace();
                
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode errorNode = mapper.createObjectNode();
                errorNode.put("error", "Upload failed completely: " + e.getMessage());
                errorNode.put("fallbackError", fallbackError.getMessage());
                
                return errorNode;
            }
        }
    }

    @PostMapping("/rest/upload/delete")
    public ResponseEntity<?> deleteImageByPost(@RequestParam("publicId") String publicId) {
        try {
            if (publicId == null || publicId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Public ID is required");
            }
            
            if (publicId.equals("user_dlgoyb") || publicId.equals("cloud-upload_c6zitf") || 
                publicId.toLowerCase().contains("default")) {
                System.out.println("SKIPPED: Not deleting default image: " + publicId);
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();
                node.put("message", "Default image not deleted");
                node.put("publicId", publicId);
                node.put("skipped", true);
                return ResponseEntity.ok(node);
            }
            
            cloudinaryService.deleteFile(publicId);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("message", "Image deleted successfully");
            node.put("publicId", publicId);
            node.put("deleted", true);
            
            return ResponseEntity.ok(node);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image: " + e.getMessage());
        }
    }

    @DeleteMapping("/rest/upload/delete/{publicId:.+}")
    public ResponseEntity<?> deleteImage(@PathVariable("publicId") String publicId) {
        try {
            try {
                publicId = java.net.URLDecoder.decode(publicId, "UTF-8");
            } catch (Exception decodeError) {
                System.out.println("No URL decoding needed, using original: " + publicId);
            }
            
            if (publicId == null || publicId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Public ID is required");
            }
            
            if (publicId.equals("user_dlgoyb") || publicId.equals("cloud-upload_c6zitf") || publicId.toLowerCase().contains("default")) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();
                node.put("message", "Default image not deleted");
                node.put("publicId", publicId);
                node.put("skipped", true);
                return ResponseEntity.ok(node);
            }
            
            cloudinaryService.deleteFile(publicId);
            
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("message", "Image deleted successfully");
            node.put("publicId", publicId);
            node.put("deleted", true);
            
            return ResponseEntity.ok(node);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image: " + e.getMessage());
        }
    }
}
