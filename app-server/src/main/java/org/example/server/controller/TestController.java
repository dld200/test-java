package org.example.server.controller;

import org.example.common.dto.Result;
import org.example.server.dto.DebugReq;
import org.example.server.service.TestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("test")
@RestController
public class TestController {

    @Autowired
    private TestCaseService testCaseService;

    @GetMapping("/")
    public String hello() {
        return "Hello, Spring Boot!";
    }

    @PostMapping("debug")
    public Result<String> debug( @RequestBody DebugReq req) {
        return Result.success(testCaseService.debug(req));
    }
}