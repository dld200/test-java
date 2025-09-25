package org.example.server.dao;

import org.example.common.domain.TestStepRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestStepRunDao extends JpaRepository<TestStepRun, Long> {


}