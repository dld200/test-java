package org.example.server.controller;

import org.example.common.Result;
import org.example.common.domain.TestCase;
import org.example.server.service.TestCaseService;
import org.example.server.service.TestStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @PostMapping("{testCaseId}/run")
    public Result<String> runTestCase(@PathVariable Long testCaseId, @RequestBody List<Map<String, Object>> params) {
        Map<String, Object> map = params.stream()
                .collect(Collectors.toMap(m -> m.get("key").toString(),
                        m -> m.get("value")));
        return Result.success(testCaseService.execute(testCaseId, map, (Long) map.get("testDeviceId")));
    }
}