package com.yyds.gmall.product.service.impl;

import com.yyds.gmall.product.service.TestRedisService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: TestRedisServiceImpl
 * @Author: yyd
 * @Date: 2022/8/2/002
 * @Description:测试实现类
 */
@Service
public class TestRedisServiceImpl implements TestRedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * redis的测试
     */
    @Override
    public void setRedis() {
        //每个线程进来都生成唯一的标识
        String uuid = UUID.randomUUID().toString().replace("-", "");
        //使用redis的setnx方法加分布式锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid,10,TimeUnit.SECONDS);
        if (lock) {
            //从redis中获取一个key为java0217的value值
            Integer java0217 = (Integer) redisTemplate.opsForValue().get("java0217");
            //如果value值不为null,那么对value进行++操作
            if (java0217 != null) {
                java0217++;
                //更新这个key的value
                redisTemplate.opsForValue().set("java0217", java0217);
            }
            //释放锁,判断这个锁是否是我自己的锁
//            String uuidRedis = (String) redisTemplate.opsForValue().get("lock");
//            if (uuidRedis.equals(uuid)){
//                redisTemplate.delete("lock");
//            }
            //lua脚本语言
            DefaultRedisScript defaultRedisScript = new DefaultRedisScript();
            defaultRedisScript.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
            defaultRedisScript.setResultType(Long.class);
            redisTemplate.execute(defaultRedisScript, Arrays.asList("lock"), uuid);
        } else {
            try {
                //加锁失败
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            setRedis();
        }
    }

    @Autowired
    private RedissonClient redissonClient;
    /**
     * rediss的测试
     */
    @Override
    public void setRedisson() {
        //获取锁
        RLock lock = redissonClient.getLock("lock");
        //加锁成功,redis++
        try {
            if (lock.tryLock(100,100,TimeUnit.SECONDS)){
                try {
                    //加锁成功
                    //从redis中获取一个key为java0217的value值
                    Integer java0217 = (Integer) redisTemplate.opsForValue().get("java0217");
                    //如果value值不为null,那么对value进行++操作
                    if (java0217 != null) {
                        java0217++;
                        //更新这个key的value
                        redisTemplate.opsForValue().set("java0217", java0217);
                    }
                }catch (Exception e){
                    System.out.println("加锁成功,代码执行出现异常");
                }finally {
                    //释放锁
                    lock.unlock();
                }
            }else {
                System.out.println("抢锁失败");
            }
        }catch (Exception e){
            System.out.println("加锁的时候出现异常,加锁失败");
        }
    }
}