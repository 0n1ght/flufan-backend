package com.flufan.service.impl;

import com.flufan.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

    public String save(MultipartFile file, String folder) throws IOException {
        Path dir = storageRoot.resolve(folder);
        Files.createDirectories(dir);

        String name = System.currentTimeMillis() + "_" + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path target = dir.resolve(name);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return name;
    }

    public Path load(String folder, String filename) {
        return storageRoot.resolve(folder).resolve(filename);
    }

    public boolean delete(String folder, String filename) throws IOException {
        Path path = load(folder, filename);
        return Files.deleteIfExists(path);
    }
}
