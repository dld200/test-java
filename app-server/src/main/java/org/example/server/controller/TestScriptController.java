package org.example.server.controller;

import org.example.common.dto.Result;
import org.example.server.dto.DebugReq;
import org.example.server.service.TestScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("tests")
@RestController
public class TestScriptController {

    @Autowired
    private TestScriptService testCaseService;

    @GetMapping("/")
    public String hello() {
        return "Hello, Spring Boot!";
    }

    @PostMapping("debug")
    public Result<String> debug(@RequestBody DebugReq req) {
        return Result.success(testCaseService.debug(req));
    }
}