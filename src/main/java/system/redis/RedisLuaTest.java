package system.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import system.redis.manager.RedisPoolMgr;

import java.util.Random;

public class RedisLuaTest {


    private static final Integer USER_NUM = 30;
    private static final Integer USER_ID_SHIFT = 1000;
    private static final String USER_QUOTA = "1";

    private static final Integer COUPON_NUM = 10;
    private static final String COUPON_STOCK_NUM = "30";



    private static final String USER_PREFIX = "user-";
    private static final String COUPON_PREFIX = "coupon-";

    static {
        JedisPool pool = RedisPoolMgr.getSingleRedisPool();
        Jedis jedis = pool.getResource();
        // 初始化用户限额数量
        for(int j = 0 ; j < COUPON_NUM ; j++){
            for(int k = USER_ID_SHIFT ; k <USER_ID_SHIFT + USER_NUM ; k++){
                String key = COUPON_PREFIX + j +":" + USER_PREFIX + k;
                jedis.set(key , USER_QUOTA);
            }
        }

        // 初始化库存
        for(int i = 0 ; i < 10 ; i++){
            jedis.set(COUPON_PREFIX + i , COUPON_STOCK_NUM);
        }

    }



    /**
     * 并发100线程同时领取
     */
    public static void main(String[] args) {
        Random random = new Random();
        for(int a = USER_ID_SHIFT ; a <USER_ID_SHIFT + USER_NUM ; a++){
            Integer randomCoupId = random.nextInt(COUPON_NUM);
            new RequestClient(a).claimFromRedis(randomCoupId);
        }
    }

}
