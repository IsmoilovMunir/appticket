package com.surnekev.ticketing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadService {

    private final Path uploadDirectory;
    private final String baseUrl;

    public FileUploadService(
            @Value("${file.upload.directory:uploads}") String uploadDir,
            @Value("${file.upload.base-url:/uploads}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDirectory);
            log.info("File upload directory initialized: {}", this.uploadDirectory);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    public String uploadFile(MultipartFile file, String subdirectory) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Создаем поддиректорию если нужно
        Path targetDirectory = subdirectory != null 
            ? uploadDirectory.resolve(subdirectory)
            : uploadDirectory;
        Files.createDirectories(targetDirectory);

        // Генерируем уникальное имя файла
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        // Сохраняем файл
        Path targetPath = targetDirectory.resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Возвращаем URL
        String url = subdirectory != null 
            ? baseUrl + "/" + subdirectory + "/" + filename
            : baseUrl + "/" + filename;
        
        log.info("File uploaded: {} -> {}", originalFilename, url);
        return url;
    }
}
