package org.example.mobile.device;

import java.util.Map;

/**
 * 自动化接口
 */
public interface Automation {
    
    /**
     * 初始化设备
     * @param options 初始化选项
     */
    void init(Map<String, Object> options);
    
    /**
     * 屏幕相关操作接口
     * @param action 屏幕操作动作
     * @param params 操作参数
     * @return 操作结果
     */
    Object screen(String action, Map<String, Object> params);
}