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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${image.upload.dir:/tmp/images}")
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
        Path filePath = Paths.get(uploadDir, UUID.randomUUID().toString(), file.getName());
        Files.write(filePath, Files.readAllBytes(file.toPath()));
        return filePath.toString();
    }

    public byte[] getImage(Long id) throws IOException {
        // 首先尝试按图片文件名查找
        Path imagePath = Paths.get(uploadDir, id.toString());
        if (Files.exists(imagePath)) {
            return Files.readAllBytes(imagePath);
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