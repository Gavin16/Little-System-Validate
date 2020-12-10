package system.redis.manager;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;
import system.redis.config.RedisConfig;

/**
 * Redis 连接池配置
 *
 * Jedis连接池提供单例
 */
public class RedisPoolMgr {

    private static volatile JedisPool jedisPool;


    private static JedisPool genRedisPool(){
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        RedisConfig config = RedisConfig.getInstance();

        poolConfig.setMaxTotal(config.getMaxTotal());
        poolConfig.setMaxIdle(config.getMaxIdle());
        poolConfig.setMaxWaitMillis(config.getMaxWaitMillis());
        poolConfig.setMinIdle(config.getMinIdle());
        poolConfig.setTestWhileIdle(config.getTestWhileIdle());
        poolConfig.setTestOnBorrow(config.getTestOnBorrow());
        poolConfig.setTestOnReturn(config.getTestOnReturn());
        poolConfig.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMillis());
        poolConfig.setNumTestsPerEvictionRun(config.getNumTestsPerEvictionRun());

        JedisPool jedisPool = new JedisPool(poolConfig,config.getAddress(), config.getPort(),config.getTimeout());

        return jedisPool;
    }



    /**
     * 获取唯一的 JedisPool
     */
    public static JedisPool getSingleRedisPool(){
        if(null == jedisPool){
            synchronized (RedisPoolMgr.class){
                if (null == jedisPool){
                    jedisPool = genRedisPool();
                }
            }
        }
        return jedisPool;
    }
}
