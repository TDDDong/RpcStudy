package com.dd.ddrpc.register;

import com.dd.ddrpc.model.ServiceMetaInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 注册中心服务本地缓存
 *
 */
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    //List<ServiceMetaInfo> serviceCache;
    Map<String, List<ServiceMetaInfo>> serviceCacheMap;

    /**
     * 写缓存
     *
     * @param newServiceCache
     * @return
     */
    /*void writeCache(List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache = newServiceCache;
    }*/
    void writeCache(String serviceKey, List<ServiceMetaInfo> newServiceCache) {
        this.serviceCacheMap.put(serviceKey, newServiceCache);
    }

    /**
     * 读缓存
     *
     * @return
     */
    /*List<ServiceMetaInfo> readCache() {
        return this.serviceCache;
    }*/
    List<ServiceMetaInfo> readCache(String serviceKey) {
        return this.serviceCacheMap.getOrDefault(serviceKey, new ArrayList<ServiceMetaInfo>());
    }

    /**
     * 清空缓存
     */
    /*void clearCache() {
        this.serviceCache = null;
    }*/
    void clearCache() {
        this.serviceCacheMap = null;
    }
}
