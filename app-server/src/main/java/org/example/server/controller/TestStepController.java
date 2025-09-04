package org.example.server.controller;

import org.example.common.domain.TestStep;
import org.example.server.service.TestStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-steps")
public class TestStepController {

    @Autowired
    private TestStepService testStepService;

    /**
     * 创建或更新TestStep
     *
     * @param testStep TestStep对象
     * @return 保存后的TestStep对象
     */
    @PostMapping
    public TestStep saveTestStep(@RequestBody TestStep testStep) {
        return testStepService.save(testStep);
    }

    /**
     * 根据ID删除TestStep
     *
     * @param id TestStep的ID
     * @return 删除是否成功
     */
    @DeleteMapping("/{id}")
    public boolean deleteTestStep(@PathVariable Long id) {
        testStepService.deleteById(id);
        return true;
    }

    /**
     * 根据ID获取TestStep
     *
     * @param id TestStep的ID
     * @return TestStep对象
     */
    @GetMapping("/{id}")
    public TestStep getTestStep(@PathVariable Long id) {
        return testStepService.findById(id);
    }

}