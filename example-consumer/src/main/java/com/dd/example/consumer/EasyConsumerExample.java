package com.dd.example.consumer;

import com.dd.ddrpc.proxy.ServiceProxy;
import com.dd.ddrpc.proxy.ServiceProxyFactory;
import com.dd.example.common.model.User;
import com.dd.example.common.service.UserService;

public class EasyConsumerExample {
    public static void main(String[] args) {
        //静态代理
        //UserService userService = new UserServiceProxy();

        //动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("dd");
        //调用
        User newUser = userService.getUser(user);

        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
