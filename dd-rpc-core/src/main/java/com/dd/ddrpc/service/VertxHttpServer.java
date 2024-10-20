package com.dd.ddrpc.service;

import io.vertx.core.Vertx;

/**
 * Vertx HTTP 服务器
 */
public class VertxHttpServer implements HttpServer{
    @Override
    public void doStart(int port) {
        //创建vertx实例
        Vertx vertx = Vertx.vertx();

        //创建 HTTP 服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        //处理请求
        server.requestHandler(null);

        //启动HTTP服务器并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port: " + port);
            } else {
                System.out.println("Failed to start server:" + result.cause());
            }
        });
    }
}
