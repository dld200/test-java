package org.example.server.service;

import org.example.common.domain.TestStep;
import org.example.server.dao.TestStepDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TestStepService {

    @Autowired
    private TestStepDao testStepDao;

    /**
     * 保存TestStep信息
     * @param testStep TestStep对象
     * @return 保存后的TestStep对象
     */
    public TestStep save(TestStep testStep) {
        Date now = new Date();
        if (testStep.getId() == null) {
            // 新增
            testStep.setCreateTime(now);
            testStep.setUpdateTime(now);
        } else {
            // 更新
            testStep.setUpdateTime(now);
            // 保持创建时间不变
        }
        return testStepDao.save(testStep);
    }

    /**
     * 根据ID删除TestStep
     * @param id TestStep的ID
     */
    public void deleteById(Long id) {
        testStepDao.deleteById(id);
    }

    /**
     * 根据ID查询TestStep
     * @param id TestStep的ID
     * @return TestStep对象
     */
    public TestStep findById(Long id) {
        Optional<TestStep> testStep = testStepDao.findById(id);
        return testStep.orElse(null);
    }

    /**
     * 查询所有TestStep
     * @return TestStep列表
     */
    public List<TestStep> findAll() {
        return testStepDao.findAll();
    }
    
    /**
     * 根据TestCase ID查询TestStep列表
     * @param testCaseId TestCase的ID
     * @return TestStep列表
     */
    public List<TestStep> findByTestCaseId(Long testCaseId) {
        return testStepDao.findByTestCaseId(testCaseId);
    }
}