package com.flufan.modules.user.service.impl;

import com.flufan.modules.user.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;

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

        String filename = name + ".png";
        Path target = dir.resolve(filename);

        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new IllegalArgumentException("Not an image");
        }

        ImageIO.write(image, "png", target.toFile());
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
