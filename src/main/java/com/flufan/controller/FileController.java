package com.flufan.controller;

import com.flufan.service.FileStorageService;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService storage;

    public FileController(FileStorageService storage) {
        this.storage = storage;
    }

    @GetMapping("/profile-pictures/{username}")
    public ResponseEntity<Resource> get(@PathVariable String username) throws IOException {

        Path path = storage.load("profile-pictures", username+"_pfp.png");
        if (!Files.exists(path)) return ResponseEntity.notFound().build();

        Resource resource = new UrlResource(path.toUri());
        String contentType = Files.probeContentType(path);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType == null ? "application/octet-stream" : contentType))
                .body(resource);
    }
    //TODO: formaty do wiadomosci: mp3, mp4, png, jpg, jpeg
    //obrazy: .jpg, .jpeg, .png, .gif
    //NeetoChat Help Center
    //+1
    //dokumenty: .doc, .docx, .xls, .xlsx, .pdf, .txt, .rtf, .csv, ewentualnie też .html
    //NeetoChat Help Center
    //+1
    //archiwa: .zip
    //NeetoChat Help Center
    //+1
    //filmy/wideo: np. .mp4, .mov, .avi, .mkv i inne formaty wideo obsługiwane przez Messenger
}
