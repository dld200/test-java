package org.example.server.controller;

import org.example.common.domain.TestCase;
import org.example.common.domain.TestCaseRun;
import org.example.server.dto.BaseReq;
import org.example.server.service.TestCaseService;
import org.example.server.service.TestStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
public class TestCaseController {

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestStepService testStepService;

    @PostMapping("testcases")
    public Page<TestCase> listTestCases(@RequestBody BaseReq req) {
        return testCaseService.listTestCases(req);
    }

    @PostMapping("testcases/save")
    public TestCase saveTestCase(@RequestBody TestCase testCase) {
        return testCaseService.save(testCase);
    }

    @GetMapping("testcases/{id}")
    public TestCase getTestCase(@PathVariable Long id) {
        return testCaseService.findById(id);
    }

    @DeleteMapping("testcases/{id}")
    public boolean deleteTestCase(@PathVariable Long id) {
        testCaseService.deleteById(id);
        return true;
    }

    @PostMapping("testcases/{id}/run")
    public TestCaseRun runTestCase(@PathVariable Long id, @RequestBody List<Map<String, Object>> params) {
        Map<String, Object> map = params.stream()
                .collect(Collectors.toMap(m -> m.get("key").toString(),
                        m -> m.get("value")));
        Long deviceId = map.get("testDeviceId") == null? null: Long.parseLong(map.get("testDeviceId").toString());
        return testCaseService.execute(id, map, deviceId);
    }

    @PostMapping("runs")
    public Page<TestCaseRun> listTestCaseRuns(@RequestBody BaseReq req) {
        return testCaseService.listTestCaseRuns(req);
    }

    @GetMapping("runs/{id}")
    public TestCaseRun getTestCaseRun(@PathVariable Long id) {
        return testCaseService.getTestRun(id);
    }
}