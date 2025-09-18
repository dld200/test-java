package org.example.server.dao;

import org.example.common.domain.TestCaseRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseRunDao extends JpaRepository<TestCaseRun, Long> {

}