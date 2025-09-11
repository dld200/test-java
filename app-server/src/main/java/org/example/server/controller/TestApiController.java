package org.example.server.controller;

import org.example.common.domain.TestCase;
import org.example.common.dto.Result;
import org.example.server.service.TestCaseService;
import org.example.server.service.TestStepService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-cases")
public class TestApiController {

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestStepService testStepService;

    @PostMapping
    public TestCase saveTestCase(@RequestBody TestCase testCase) {
        return testCaseService.save(testCase);
    }

    @DeleteMapping("/{id}")
    public boolean deleteTestCase(@PathVariable Long id) {
        testCaseService.deleteById(id);
        return true;
    }

    @GetMapping("/{id}")
    public TestCase getTestCase(@PathVariable Long id) {
        return testCaseService.findById(id);
    }

    @GetMapping
    public List<TestCase> listTestCases() {
        return testCaseService.findAll();
    }

    @PostMapping("{id}/run")
    public Result<String> runTestCase(@PathVariable Long id) {
        return Result.success(testCaseService.execute(id));
    }
}