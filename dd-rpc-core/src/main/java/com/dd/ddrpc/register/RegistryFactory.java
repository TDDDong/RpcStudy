package com.dd.ddrpc.register;

import com.dd.ddrpc.spi.SpiLoader;

/**
 * 注册中心工厂（用于获取注册中心对象）
 */
public class RegistryFactory {
    static {
        //SPI加载器
        //加载用户自定义或系统定义的SPI加载器
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new ZooKeeperRegistry();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }
}
