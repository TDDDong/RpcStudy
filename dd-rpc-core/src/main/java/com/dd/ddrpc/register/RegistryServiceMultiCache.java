package com.dd.ddrpc.register;

import com.dd.ddrpc.model.ServiceMetaInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心服务本地缓存 （支持多个服务）
 */
public class RegistryServiceMultiCache {

    /**
     * 服务缓存
     */
    Map<String, List<ServiceMetaInfo>> serviceCacheMap = new ConcurrentHashMap<>();

    /**
     * 写缓存
     *
     * @param newServiceCache
     * @return
     */
    void writeCache(String serviceKey, List<ServiceMetaInfo> newServiceCache) {
        this.serviceCacheMap.put(serviceKey, newServiceCache);
    }

    /**
     * 读缓存
     *
     * @return
     */
    List<ServiceMetaInfo> readCache(String serviceKey) {
        return this.serviceCacheMap.getOrDefault(serviceKey, new ArrayList<ServiceMetaInfo>());
    }

    /**
     * 清空缓存
     */
    void clearCache(String serviceKey) {
        this.serviceCacheMap.remove(serviceKey);
    }
}
