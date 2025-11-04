package me.asreal.markgenius.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    String uploadFile(MultipartFile file);

    void createFolder(String folderName);

    boolean removeFile(String imageUrl);

}
