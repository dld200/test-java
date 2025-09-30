package org.example.server.task;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.common.domain.Asset;
import org.example.mobile.automation.android.AndroidAutomation;
import org.example.mobile.automation.ios.IosAutomation;
import org.example.mobile.dto.Device;
import org.example.server.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DeviceTask {

    @Autowired
    private AssetService assetService;

    //定时任务获取设备列表
    @Scheduled(fixedRate = 30 * 1000)
    public void getDeviceList() {
        List<Device> devices = new IosAutomation().listDevices();
        devices.addAll(new AndroidAutomation().listDevices());

        for (Device device : devices) {
            Asset asset = Asset.builder()
                    .name(device.getName())
                    .type("device")
                    .groupName("iphone")
                    .uuid(device.getUdid())
                    .info(JSON.toJSONString(device))
                    .build();
            assetService.save(asset);
        }
    }
}