package org.example.server.dao;

import org.example.common.domain.PageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageModelDao extends JpaRepository<PageModel, Long> {
    // PageModel特有的数据库操作方法可以在这里添加

    @Query("SELECT p FROM PageModel p WHERE p.name = :name")
    List<PageModel> findByName(@Param("name") String name);
}