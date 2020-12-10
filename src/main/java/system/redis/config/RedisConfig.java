package system.redis.config;

import lombok.Data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 单例模式提供读取出来的配置
 */
@Data
public class RedisConfig {

    private static RedisConfig config = new RedisConfig();

    private String address;
    private Integer port;
    private Integer timeout;

    private Integer maxTotal;
    private Integer maxIdle;
    private Integer minIdle;
    private Long maxWaitMillis;
    private Boolean testOnBorrow;
    private Boolean testOnReturn;
    private Boolean testWhileIdle;
    private Long timeBetweenEvictionRunsMillis;
    private Integer numTestsPerEvictionRun;

    private RedisConfig(){
        try {
            Properties props = new Properties();
            props.load(this.getClass().getResourceAsStream("/redis.properties") );
            this.setAddress(props.getProperty("redis.address"));
            this.setPort(Integer.parseInt(props.getProperty("redis.port")));
            this.setTimeout(Integer.parseInt(props.getProperty("redis.timeout")));
            this.setMaxTotal(Integer.parseInt(props.getProperty("redis.pool.maxTotal")));
            this.setMaxIdle(Integer.parseInt(props.getProperty("redis.pool.maxIdle")));
            this.setMinIdle(Integer.parseInt(props.getProperty("redis.pool.minIdle")));
            this.setMaxWaitMillis(Long.parseLong(props.getProperty("redis.pool.maxWaitMillis")));
            this.setTestWhileIdle(Boolean.valueOf(props.getProperty("redis.pool.testWhileIdle")));
            this.setTestOnBorrow(Boolean.valueOf(props.getProperty("redis.pool.testOnBorrow")));
            this.setTestOnReturn(Boolean.valueOf(props.getProperty("redis.pool.testOnReturn")));
            this.setTimeBetweenEvictionRunsMillis(Long.parseLong(props.getProperty("redis.pool.timeBetweenEvictionRunsMillis")));
            this.setNumTestsPerEvictionRun(Integer.parseInt(props.getProperty("redis.pool.numTestsPerEvictionRun")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RedisConfig getInstance(){
        return config;
    }
}
