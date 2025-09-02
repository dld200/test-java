package org.example.server.controller;

import org.example.common.domain.PageModel;
import org.example.common.dto.Result;
import org.example.server.dto.ModelReq;
import org.example.server.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("models")
@RestController
public class ModelController {

    @Autowired
    private ModelService modelService;

    @GetMapping("/")
    public Result<List<PageModel>> queryModels() {
        return Result.success(modelService.query());
    }

    @PostMapping
    public Result<PageModel> saveModel(@RequestBody ModelReq req) {
        return Result.success(modelService.save(req));
    }

    @GetMapping("{id}")
    public Result<PageModel> getModel(@RequestBody Long id) {
        return Result.success(modelService.findById(id));
    }

    @DeleteMapping("{id}")
    public Result<Void> deleteModel(@RequestBody Long id) {
        modelService.deleteById(id);
        return Result.success();
    }

    @GetMapping("{id}/summary")
    public Result<String> getModelSummary(@RequestParam Long id) {
        return Result.success(modelService.summary(id));
    }
}