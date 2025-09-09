package org.example.server.controller;

import org.example.server.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@RequestMapping("files")
@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            return fileService.saveImage(file);
        } catch (IOException e) {
            return "图片上传失败: " + e.getMessage();
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public void getImage(@PathVariable Long id, HttpServletResponse response) {
        try {
            byte[] imageData = fileService.getImage(id);
            if (imageData != null) {
                response.setContentType(MediaType.IMAGE_PNG_VALUE);
                OutputStream os = response.getOutputStream();
                os.write(imageData);
                os.flush();
                os.close();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IOException e) {
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (Exception ex) {
                // 忽略异常
            }
        }
    }
}