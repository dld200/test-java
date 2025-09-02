package org.example.server.dao;

import org.example.common.domain.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestCaseDao extends JpaRepository<TestCase, Long> {
    // TestCase特有的数据库操作方法可以在这里添加
    
    @EntityGraph(attributePaths = "testSteps")
    Optional<TestCase> findWithTestStepsById(Long id);
}