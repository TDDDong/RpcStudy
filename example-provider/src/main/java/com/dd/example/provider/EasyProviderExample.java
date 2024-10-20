package com.dd.example.provider;

import com.dd.ddrpc.register.LocalRegistry;
import com.dd.ddrpc.service.VertxHttpServer;
import com.dd.example.common.service.UserService;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        //注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        //提供服务
        VertxHttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
