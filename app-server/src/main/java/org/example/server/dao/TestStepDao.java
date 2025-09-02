package org.example.server.dao;

import org.example.common.domain.TestStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestStepDao extends JpaRepository<TestStep, Long> {
    // TestStep特有的数据库操作方法可以在这里添加
    List<TestStep> findByTestCaseId(Long testCaseId);
}