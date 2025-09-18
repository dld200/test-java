package org.example.server.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${server.upload-dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        String uuid = UUID.randomUUID().toString();
        Path filePath = Paths.get(uploadDir, uuid, file.getOriginalFilename());
        Files.write(filePath, file.getBytes());
        return uuid;
    }

    public String saveFile(File file) throws IOException {
        String uuid = UUID.randomUUID().toString();
        Path filePath = Paths.get(uploadDir, uuid, file.getName());
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, Files.readAllBytes(file.toPath()));
        return uuid;
    }

    public byte[] getImage(String uuid) throws IOException {
        Path imagePath = Paths.get(uploadDir, uuid);
        //读取下层的文件
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(imagePath)) {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {
                    System.out.println("找到文件: " + filePath.getFileName());
                    // 读取内容（比如二进制图像、文本等）
                    byte[] bytes = Files.readAllBytes(filePath);
                    return bytes;
                }
            }
        }
        return null;
    }
}