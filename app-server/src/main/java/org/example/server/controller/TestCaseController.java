package org.example.server.controller;

import org.example.common.domain.TestCase;
import org.example.common.domain.TestStep;
import org.example.common.dto.Result;
import org.example.server.dto.DebugReq;
import org.example.server.service.TestCaseService;
import org.example.server.service.TestStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-cases")
public class TestCaseController {

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestStepService testStepService;

    /**
     * 创建或更新TestCase
     *
     * @param testCase TestCase对象
     * @return 保存后的TestCase对象
     */
    @PostMapping
    public TestCase save(@RequestBody TestCase testCase) {
        return testCaseService.save(testCase);
    }

    /**
     * 根据ID删除TestCase
     *
     * @param id TestCase的ID
     * @return 删除是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        testCaseService.deleteById(id);
        return true;
    }

    /**
     * 根据ID获取TestCase
     *
     * @param id TestCase的ID
     * @return TestCase对象
     */
    @GetMapping("/{id}")
    public TestCase getById(@PathVariable Long id) {
        return testCaseService.findById(id);
    }

    /**
     * 获取所有TestCase
     *
     * @return TestCase列表
     */
    @GetMapping
    public List<TestCase> getAll() {
        return testCaseService.findAll();
    }

    /**
     * 根据TestCase ID获取关联的TestSteps
     *
     * @param id TestCase的ID
     * @return TestStep列表
     */
    @GetMapping("/{id}/steps")
    public List<TestStep> getTestStepsByTestCaseId(@PathVariable Long id) {
        return testStepService.findByTestCaseId(id);
    }

    @PostMapping("{id}/run")
    public Result<String> run(@PathVariable Long id) {
        return Result.success(testCaseService.execute(id));
    }
}