package org.example.server.service;

import jakarta.annotation.PostConstruct;
import org.example.common.domain.PageModel;
import org.example.server.dto.ModelReq;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${image.upload.dir:/Users/snap/workspace/app-agent/uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        // 确保上传目录存在
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public String saveImage(MultipartFile file) throws IOException {
        Path filePath = Paths.get(uploadDir, UUID.randomUUID().toString(), file.getOriginalFilename());
        Files.write(filePath, file.getBytes());
        return filePath.toString();
    }

    public String saveFile(File file) throws IOException {
        //保存到uuid目录下
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

    /**
     * 将PageModel转换为ModelReq
     *
     * @param pageModel PageModel对象
     * @return ModelReq对象
     */
    private ModelReq convertToReq(PageModel pageModel) {
        ModelReq req = new ModelReq();
        req.setName(pageModel.getName());
        req.setXml(pageModel.getXml());
        req.setJson(pageModel.getJson());
        req.setHtml(pageModel.getHtml());
        req.setScreenshot(pageModel.getScreenshot());
        req.setSummary(pageModel.getSummary());
        return req;
    }
}