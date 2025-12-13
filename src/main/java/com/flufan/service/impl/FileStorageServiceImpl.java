package com.flufan.service.impl;

import com.flufan.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path storageRoot = Paths.get("storage");

    public FileStorageServiceImpl() throws IOException {
        Files.createDirectories(storageRoot);
    }

    @Override
    public String save(MultipartFile file, String name, String folder) throws IOException {
        Path dir = storageRoot.resolve(folder);
        Files.createDirectories(dir);

        String original = Objects.requireNonNull(file.getOriginalFilename());
        String ext = original.contains(".")
                ? original.substring(original.lastIndexOf("."))
                : "";

        String filename = name + ext;
        Path target = dir.resolve(filename);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    @Override
    public Path load(String folder, String filename) {
        return storageRoot.resolve(folder).resolve(filename);
    }

    @Override
    public boolean delete(String folder, String prefix) throws IOException {
        Path dir = storageRoot.resolve(folder);
        if (!Files.exists(dir)) return false;

        try (var stream = Files.list(dir)) {
            for (Path p : stream.toList()) {
                if (p.getFileName().toString().startsWith(prefix)) {
                    Files.deleteIfExists(p);
                    return true;
                }
            }
        }
        return false;
    }
}
