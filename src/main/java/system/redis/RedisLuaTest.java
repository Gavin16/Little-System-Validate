package system.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import system.redis.manager.RedisPoolMgr;

import java.util.*;
import java.util.concurrent.*;

public class RedisLuaTest {


    private static final Integer USER_NUM = 1000;
    private static final Integer USER_ID_SHIFT = 1000;
    private static final String USER_QUOTA = "1";

    private static final Integer COUPON_NUM = 10;
    private static final String COUPON_STOCK_NUM = "20";



    private static final String USER_PREFIX = "user-";
    private static final String COUPON_PREFIX = "coupon-";

    static {
        JedisPool pool = RedisPoolMgr.getSingleRedisPool();
        Jedis jedis = pool.getResource();
        // 初始化用户限额数量
        for(int j = 0 ; j < COUPON_NUM ; j++){
            for(int k = 0 ; k < USER_NUM ; k++){
                int userId = USER_ID_SHIFT + k;
                String key = COUPON_PREFIX + j +":" + USER_PREFIX + userId;
                jedis.set(key , USER_QUOTA);
            }
        }

        // 初始化库存
        for(int i = 0 ; i < 10 ; i++){
            jedis.set(COUPON_PREFIX + i , COUPON_STOCK_NUM);
        }
        jedis.close();
    }

   private static ThreadPoolExecutor executor =
           new ThreadPoolExecutor(4,8,5, TimeUnit.SECONDS,new ArrayBlockingQueue<>(USER_NUM*COUPON_NUM));

    /**
     * 并发1000线程同时领取
     */
    public static void main(String[] args) throws Exception {
        Random random = new Random();
        List<RequestTask> tasks = new ArrayList<>();
        for(int a = 0 ; a < USER_NUM; a++){
            Integer randomCoupId = random.nextInt(COUPON_NUM);
            Integer randomUserId = USER_ID_SHIFT + random.nextInt(USER_NUM);
            tasks.add(new RequestTask(randomCoupId, randomUserId));
        }
        List<Future<ClaimResult>> futures = executor.invokeAll(tasks);
        // 允许回收核心线程
        executor.allowCoreThreadTimeOut(true);
        Map<String,Integer> map = new HashMap<>();
        for(Future<ClaimResult> future : futures){
            ClaimResult result = future.get();
            String key = COUPON_PREFIX + result.getCouponId();
            if(map.containsKey(key)){
                Integer integer = map.get(key);
                map.put(key,integer + result.getClaimedNum());
            }else{
                map.put(key,result.getClaimedNum());
            }
        }

        System.out.println("----------------实际领取数量---------------");
        map.entrySet().forEach(
                e->{
                    System.out.println(e.getKey() + " : " +  e.getValue());
                }
        );
    }



    static class RequestTask implements Callable<ClaimResult>{
        private Integer randomUserId;
        private Integer randomCoupId;

        public RequestTask(Integer randomCoupId,Integer randomUserId){
            this.randomCoupId = randomCoupId;
            this.randomUserId = randomUserId;
        }

        @Override
        public ClaimResult call() throws Exception {
            return new RequestClient(randomUserId).claimFromRedis(randomCoupId);
        }
    }
}
