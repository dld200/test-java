package org.example.server.service;

import lombok.extern.slf4j.Slf4j;
import org.example.common.domain.TestCase;
import org.example.common.domain.TestCaseRun;
import org.example.common.domain.TestStep;
import org.example.server.dao.TestCaseDao;
import org.example.server.dao.TestCaseRunDao;
import org.example.server.dto.BaseReq;
import org.example.server.engine.ExecuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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

    @Autowired
    private TestCaseRunDao testCaseRunDao;

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

    public TestCaseRun execute(Long testCaseId, Map<String, Object> runtimeParams, Long deviceId) {
        TestCase testCase = findById(testCaseId);
        return executeService.execute(testCase, runtimeParams, deviceId);
    }

    public TestCaseRun getTestRun(Long id) {
        return testCaseRunDao.findById(id).orElse(null);
    }

    public Page<TestCaseRun> listTestCaseRuns(BaseReq req) {
        PageRequest pageRequest = PageRequest.of(req.getPage() - 1, req.getSize(), Sort.by("id").descending());
        return testCaseRunDao.findAll(pageRequest);
    }

    public Page<TestCase> listTestCases(BaseReq req) {
        PageRequest pageRequest = PageRequest.of(req.getPage() - 1, req.getSize(), Sort.by("id").descending());
        return testCaseDao.findAll(pageRequest);
    }
}