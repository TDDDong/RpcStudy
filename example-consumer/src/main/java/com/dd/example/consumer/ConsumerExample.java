package com.dd.example.consumer;

import com.dd.ddrpc.bootstrap.ConsumerBootstrap;
import com.dd.ddrpc.proxy.ServiceProxyFactory;
import com.dd.example.common.model.User;
import com.dd.example.common.service.UserService;

/**
 * 服务消费者示例
 */
public class ConsumerExample {
    public static void main(String[] args) {
        //服务消费者初始化
        ConsumerBootstrap.init();

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("dd");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
