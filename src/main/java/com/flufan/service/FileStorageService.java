package com.flufan.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {
    String save(MultipartFile file, String name, String folder) throws IOException;
    Path load(String folder, String filename);
    boolean delete(String folder, String filename) throws IOException;
}
