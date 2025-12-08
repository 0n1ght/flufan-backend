package com.flufan.controller;

import com.flufan.service.FileStorageService;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileStorageService storage;

    public FileController(FileStorageService storage) {
        this.storage = storage;
    }

    @PostMapping("/{folder}")
    public ResponseEntity<String> upload(
            @PathVariable String folder,
            @RequestParam("file") MultipartFile file) throws IOException {

        String filename = storage.save(file, folder);
        return ResponseEntity.ok(filename);
    }

    @GetMapping("/{folder}/{filename}")
    public ResponseEntity<Resource> get(
            @PathVariable String folder,
            @PathVariable String filename) throws IOException {

        Path path = storage.load(folder, filename);
        if (!Files.exists(path)) return ResponseEntity.notFound().build();

        Resource resource = new UrlResource(path.toUri());
        String contentType = Files.probeContentType(path);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType == null ? "application/octet-stream" : contentType))
                .body(resource);
    }

    @DeleteMapping("/{folder}/{filename}")
    public ResponseEntity<Void> delete(
            @PathVariable String folder,
            @PathVariable String filename) throws IOException {

        boolean deleted = storage.delete(folder, filename);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    //TODO: update pfp
    // sprawdza dane zdjecia, parametry itp. jezeli nie jest odpowiedzie -> zwraca wyjatek
    // jezeli jest juz zdjecie pod sciezka -> <nickname_zalogowanego_uzytkownika>_pfp.png/jpg to je usowa
    // zapisuej zdjecie pod sciezka <nickname_zalogowanego_uzytkownika>_pfp.png/jpg

    //TODO: delete pfp
    // usowa zdjecie pod sciezka <nickname_zalogowanego_uzytkownika>_pfp.png/jpg

    //TODO: add photo/video to message

    //TODO: remove photo/video from message

    //TODO: WARNING: jezeli uzytkownik zmieni nick, nazwa pliku z jego pfp i inne pliki tez powinny sie zaktualizowac
}
