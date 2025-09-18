package org.example.server.dao;

import org.example.common.domain.App;
import org.example.common.domain.TestDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestDeviceDao extends JpaRepository<TestDevice, Long> {

}