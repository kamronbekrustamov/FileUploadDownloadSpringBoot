package com.kamronbek.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileService {

    private final Path fileStorageLocation;

    public FileService(@Value("${file.storage.location:temp}") String fileStorageLocation) {
        this.fileStorageLocation = Paths.get(fileStorageLocation).toAbsolutePath().normalize();
        try {
            if (!Files.exists(this.fileStorageLocation)) {
                Files.createDirectories(this.fileStorageLocation);
            }
        } catch (IOException io) {
            throw new RuntimeException("Directory cannot be created");
        }
    }

    public Resource getFile(String fileName) {
        Path filePath = Paths.get(fileStorageLocation + "").toAbsolutePath().resolve(fileName);
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException io) {
            throw new RuntimeException("File cannot be read");
        }

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("File is not readable");
        }
    }

    public String saveFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path filePath = Paths.get(fileStorageLocation + "", fileName);
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException io) {
            throw new RuntimeException("FIle cannot be copied");
        }
        return fileName;
    }
}
