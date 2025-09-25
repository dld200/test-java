package org.example.server.controller;

import org.example.common.domain.Asset;
import org.example.server.dto.AssetReq;
import org.example.server.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    @Autowired
    private AssetService service;

    @PostMapping
    public Page<Asset> listAssets(@RequestBody AssetReq req) {
        return service.list(req);
    }

    @PostMapping("save")
    public Asset saveAsset(@RequestBody Asset config) {
        return service.save(config);
    }

    @GetMapping("/{id}")
    public Asset getAsset(@PathVariable Long id) {
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteAsset(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("take")
    public Asset takeAsset(@PathVariable String groupName, String userName) {
        return service.takeAssetByGroup(groupName, userName);
    }

    @PostMapping("release")
    public Asset releaseAsset(Long id, String userName) {
        return service.releaseAsset(id, userName);
    }
}