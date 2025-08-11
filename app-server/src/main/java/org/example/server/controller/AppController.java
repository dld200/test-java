package org.example.server.controller;

import org.example.common.domain.App;
import org.example.server.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apps")
public class AppController {

    @Autowired
    private AppService appService;

    /**
     * 创建或更新App
     *
     * @param app App对象
     * @return 保存后的App对象
     */
    @PostMapping
    public App save(@RequestBody App app) {
        return appService.save(app);
    }

    /**
     * 根据ID删除App
     *
     * @param id App的ID
     * @return 删除是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        appService.deleteById(id);
        return true;
    }

    /**
     * 根据ID获取App
     *
     * @param id App的ID
     * @return App对象
     */
    @GetMapping("/{id}")
    public App getById(@PathVariable Long id) {
        return appService.findById(id);
    }

    /**
     * 获取所有App
     *
     * @return App列表
     */
    @GetMapping
    public List<App> getAll() {
        return appService.findAll();
    }

    /**
     * 根据名称查询App
     *
     * @param name App名称
     * @return App列表
     */
    @GetMapping("/search")
    public List<App> getByName(@RequestParam String name) {
        return appService.findByName(name);
    }
}