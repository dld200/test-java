package org.example.server.dao;

import org.example.common.domain.App;
import org.example.common.domain.PageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageModelDao extends JpaRepository<PageModel, Long> {
    // App特有的数据库操作方法可以在这里添加

    @Query("SELECT a FROM App a WHERE a.name = :name")
    List<App> findByName(@Param("name") String name);
}