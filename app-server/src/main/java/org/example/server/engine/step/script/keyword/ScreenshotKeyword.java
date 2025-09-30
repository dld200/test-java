package org.example.server.engine.step.script.keyword;

import lombok.extern.slf4j.Slf4j;
import org.example.mobile.automation.Automation;
import org.example.server.engine.MobileContext;
import org.example.server.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class ScreenshotKeyword implements Keyword {

    @Autowired
    private FileService fileService;

    @Override
    public String getName() {
        return "screenshot";
    }

    @Override
    public Object execute(MobileContext context, Object... args) {
        String name = "screenshot";
        if (args.length > 0) {
            name = args[0].toString();
        }
        String fileName = "/Users/snap/workspace/app-agent/uploads/" + name;
        context.getAutomation().screenshot(fileName);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }
        try {
            String uuid = fileService.saveFile(new File(fileName));
            log.info("<img src='http://127.0.0.1:8080/api/files/{}'/>", uuid);
            return uuid;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}