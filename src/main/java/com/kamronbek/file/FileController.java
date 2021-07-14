package com.kamronbek.file;


import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/file")
@AllArgsConstructor
public class FileController {

    public final FileService fileService;

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) throws IOException {
        Resource file = fileService.getFile(fileName);
        String mimeType = Files.probeContentType(file.getFile().toPath());
        return ResponseEntity.ok().contentType(MediaType.valueOf(mimeType)).body(file);
    }

    @PostMapping
    public Map<String, String> saveFile(@RequestParam MultipartFile file) {
        String fileName = fileService.saveFile(file);
        String url = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/file/").path(fileName).toUriString();

        Map<String, String> response = new HashMap<>();
        response.put("fileURL", url);
        response.put("fileName", fileName);
        return response;
    }

}