package org.example.server.service;

import org.example.common.domain.App;
import org.example.server.dao.AppDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AppService {

    @Autowired
    private AppDao appDao;

    /**
     * 保存App信息
     * @param app App对象
     * @return 保存后的App对象
     */
    public App save(App app) {
        Date now = new Date();
        if (app.getId() == null) {
            // 新增
            app.setCreateTime(now);
            app.setUpdateTime(now);
        } else {
            // 更新
            app.setUpdateTime(now);
            // 保持创建时间不变
        }
        return appDao.save(app);
    }

    /**
     * 根据ID删除App
     * @param id App的ID
     */
    public void deleteById(Long id) {
        appDao.deleteById(id);
    }

    /**
     * 根据ID查询App
     * @param id App的ID
     * @return App对象
     */
    public App findById(Long id) {
        Optional<App> app = appDao.findById(id);
        return app.orElse(null);
    }

    /**
     * 查询所有App
     * @return App列表
     */
    public List<App> findAll() {
        return appDao.findAll();
    }

    /**
     * 根据名称查询App
     * @param name App名称
     * @return App列表
     */
    public List<App> findByName(String name) {
        return appDao.findByName(name);
    }
}