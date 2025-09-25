package org.example.server.service;

import jakarta.transaction.Transactional;
import org.example.common.domain.Asset;
import org.example.server.dao.AssetDao;
import org.example.server.dto.BaseReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AssetService {

    @Autowired
    private AssetDao assetDao;

    public Page<Asset> list(BaseReq req) {
        PageRequest pageRequest = PageRequest.of(req.getPage() - 1, req.getSize(), Sort.by("id").descending());
        return assetDao.findAll(pageRequest);
    }

    public Asset save(Asset asset) {
        Asset old = assetDao.findByUuid(asset.getUuid());
        if (old != null) {
            asset.setId(old.getId());
        }
        return assetDao.save(asset);
    }

    public void delete(Long id) {
        assetDao.deleteById(id);
    }

    public Asset findById(Long id) {
        return assetDao.findById(id).orElse(null);
    }

    @Transactional
    public Asset takeAssetByGroup(String groupName, String userName) {
        Asset asset = assetDao.findOneByGroupName(groupName);
        // 设置为已占用状态
        asset.setUserName(userName);
        assetDao.save(asset);
        return asset; // 登录成功
    }

    @Transactional
    public Asset releaseAsset(Long id, String userName) {
        Optional<Asset> optional = assetDao.findById(id);
        if (optional.isPresent()) {
            Asset asset = optional.get();
            if (!asset.getUserName().equals(userName)) {
                return null;
            }
            asset.setUserName("");
            assetDao.save(asset);
            return asset;
        }
        return null;
    }
}