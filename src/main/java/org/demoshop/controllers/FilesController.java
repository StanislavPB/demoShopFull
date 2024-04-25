package org.demoshop.controllers;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.demoshop.dto.StandardResponseDto;
import org.demoshop.services.FilesService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequiredArgsConstructor
public class FilesController {

    private final AmazonS3 amazonS3;
    private final FilesService filesService;

    @PostMapping("/api/files")
    public StandardResponseDto upload(@RequestParam("file") MultipartFile file) {
        return filesService.upload(file);
    }
}
