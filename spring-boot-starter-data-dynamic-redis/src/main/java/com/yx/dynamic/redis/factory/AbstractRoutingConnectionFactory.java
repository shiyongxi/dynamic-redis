package com.yx.dynamic.redis.factory;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;

public abstract class AbstractRoutingConnectionFactory implements InitializingBean, DisposableBean, RedisConnectionFactory {


    /**
     * 子类实现决定最终数据源
     *
     * @return 数据源
     */
    protected abstract RedisConnectionFactory determineDataSource();

    @Override
    public void destroy() throws Exception {
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }


    @Override
    public RedisConnection getConnection() {
        return determineDataSource().getConnection();
    }

    @Override
    public RedisClusterConnection getClusterConnection() {
        return determineDataSource().getClusterConnection();
    }

    @Override
    public boolean getConvertPipelineAndTxResults() {
        return determineDataSource().getConvertPipelineAndTxResults();
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        return determineDataSource().getSentinelConnection();
    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException e) {
        return determineDataSource().translateExceptionIfPossible(e);
    }
}
