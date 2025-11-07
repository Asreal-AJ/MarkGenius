package me.asreal.markgenius.service.impl;

import lombok.RequiredArgsConstructor;
import me.asreal.markgenius.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${aws.bucket.name}")
    private String bucketName;
    private final S3Client s3Client;

    @Override
    public String uploadFile(MultipartFile file) {
        var filenameExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        var key = UUID.randomUUID() + "." + filenameExtension;
        //Handle exceptions
        try {
            var putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl("public-read")
                    .contentType(file.getContentType())
                    .build();
            var response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            //Check if the response is successful
            if (response.sdkHttpResponse().isSuccessful()) {
                return "https://" + bucketName + ".amazonaws.com/" + key;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while uploading the file");
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while uploading the file");
        }
    }

    @Override
    public void createFolder(String folderName) {
        //Handle exceptions
        var request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(folderName)
                .build();
        var response = s3Client.putObject(request, RequestBody.empty());
        //Check if the response is successful
        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while creating folder");
        }
    }

    @Override
    public boolean removeFile(String imageUrl) {
        var fileName =  imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        var deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
        return true;

    }

}
