package system.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import system.redis.manager.RedisPoolMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis请求客户端，服务向Redis发起请求
 */
public class RequestClient {

    private static final String USER_PREFIX = "user-";
    private static final String COUPON_PREFIX = "coupon-";

    private Integer userId;


    /** 判断及领取的脚本 */
    private static String claimScript = "local stock_num=tonumber(redis.call('get',KEYS[1])) local quota=tonumber(redis.call('get',KEYS[2])) if stock_num > 0 then if quota > 0 then redis.call('decr',KEYS[1])  redis.call('decr',KEYS[2]) return 0 else return 1 end else return 2 end";


    public RequestClient(int id){
        this.userId = id;
    }


    public ClaimResult claimFromRedis(Integer couponId){

        JedisPool singleRedisPool = RedisPoolMgr.getSingleRedisPool();
        Jedis jedis = singleRedisPool.getResource();
        List<String> keys = new ArrayList<>();
        keys.add(COUPON_PREFIX + couponId);
        keys.add(COUPON_PREFIX + couponId + ":" + USER_PREFIX + userId);
        List<String> args = new ArrayList<>();
        args.add("0");
        args.add("0");
        Object eval;
        try {
            String shaKey = jedis.scriptLoad(claimScript);
            eval = jedis.evalsha(shaKey, keys, args);
        } finally {
            jedis.close();
        }
        int num = String.valueOf(eval).equals("0") ? 1 : 0;
        System.out.println("用户:" + userId + ", 领取优惠券:" + couponId + ", 数量: " + num);

        return new ClaimResult(couponId,userId,num);
    }

}
