package org.example.server.controller;

import org.example.common.domain.PageModel;
import org.example.common.dto.Result;
import org.example.server.dto.ModelReq;
import org.example.server.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public Result<PageModel> saveModel(@RequestBody PageModel req) {
        return Result.success(modelService.save(req));
    }

    @GetMapping("{id}")
    public Result<PageModel> getModel(@PathVariable Long id) {
        return Result.success(modelService.findById(id));
    }

    @PostMapping("/capture")
    public Result<PageModel> capturePage() throws IOException {
        return Result.success(modelService.capture());
    }

    @DeleteMapping("{id}")
    public Result<Void> deleteModel(@PathVariable Long id) {
        modelService.deleteById(id);
        return Result.success();
    }

    @GetMapping("{id}/summary")
    public Result<String> getModelSummary(@PathVariable Long id) {
        return Result.success(modelService.summary(id));
    }
}