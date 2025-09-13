package org.example.server.service;

import lombok.extern.slf4j.Slf4j;
import org.example.common.domain.TestCase;
import org.example.common.domain.TestStep;
import org.example.server.dao.TestCaseDao;
import org.example.server.engine.ExecuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TestCaseService {
    
    @Autowired
    private TestCaseDao testCaseDao;
    
    @Autowired
    private TestStepService testStepService;

    @Autowired
    private ExecuteService executeService;

    /**
     * 保存TestCase信息
     * @param testCase TestCase对象
     * @return 保存后的TestCase对象
     */
    public TestCase save(TestCase testCase) {
        // 保存TestCase
        TestCase savedTestCase = testCaseDao.save(testCase);
        
        // 保存关联的TestSteps
        List<TestStep> testSteps = testCase.getSteps();
        if (testSteps != null) {
            for (TestStep testStep : testSteps) {
                // 设置TestStep关联的TestCase
                testStep.setTestCase(savedTestCase);
                // 保存TestStep
                testStepService.save(testStep);
            }
        }
        
        return savedTestCase;
    }

    /**
     * 根据ID删除TestCase
     * @param id TestCase的ID
     */
    public void deleteById(Long id) {
        testCaseDao.deleteById(id);
    }

    /**
     * 根据ID查询TestCase
     * @param id TestCase的ID
     * @return TestCase对象
     */
    public TestCase findById(Long id) {
        Optional<TestCase> testCase = testCaseDao.findById(id);
        return testCase.orElse(null);
    }

    /**
     * 查询所有TestCase
     * @return TestCase列表
     */
    public List<TestCase> findAll() {
        return testCaseDao.findAll();
    }

    public String execute(Long testCaseId) {
        try {
            // 获取测试用例
            TestCase testCase = findById(testCaseId);
            if (testCase == null) {
                return "Test case not found with id: " + testCaseId;
            }

            // 执行测试用例
            executeService.execute(testCase);

            return "Test case executed successfully";
        } catch (Exception e) {
            return "Error executing test case: " + e.getMessage();
        }
    }
}