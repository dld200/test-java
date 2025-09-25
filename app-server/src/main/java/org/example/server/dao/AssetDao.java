package org.example.server.dao;

import org.example.common.domain.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetDao extends JpaRepository<Asset, Long> {

    Asset findOneByGroupName(String groupName);

    Asset findByUuid(String uuid);
}