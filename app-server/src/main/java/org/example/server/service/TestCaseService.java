package org.example.server.service;

import lombok.extern.slf4j.Slf4j;
import org.example.common.domain.TestCase;
import org.example.common.domain.TestStep;
import org.example.server.dao.TestCaseDao;
import org.example.server.engine.ExecuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

    public TestCase save(TestCase testCase) {
        // 保存TestCase
        TestCase savedTestCase = testCaseDao.save(testCase);

        // 保存关联的TestSteps
        List<TestStep> testSteps = testCase.getSteps();
        if (testSteps != null) {
            for (TestStep testStep : testSteps) {
                testStep.setTestCase(savedTestCase);
                testStepService.save(testStep);
            }
        }

        return savedTestCase;
    }

    public void deleteById(Long id) {
        testCaseDao.deleteById(id);
    }

    public TestCase findById(Long id) {
        Optional<TestCase> testCase = testCaseDao.findById(id);
        return testCase.orElse(null);
    }

    public List<TestCase> findAll() {
        return testCaseDao.findAll();
    }

    public String execute(Long testCaseId, Map<String, Object> params, Long deviceId) {
        try {
            TestCase testCase = findById(testCaseId);
            if (testCase == null) {
                return "Test case not found with id: " + testCaseId;
            }
            executeService.execute(testCase, params, deviceId);
            return "Test case executed successfully";
        } catch (Exception e) {
            return "Error executing test case: " + e.getMessage();
        }
    }
}