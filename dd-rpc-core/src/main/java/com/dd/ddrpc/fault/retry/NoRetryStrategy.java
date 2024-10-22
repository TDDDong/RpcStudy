package com.dd.ddrpc.fault.retry;

import com.dd.ddrpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略 -- 不重试
 */
public class NoRetryStrategy implements RetryStrategy{
    /**
     * 重试方法
     * @param callable
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
