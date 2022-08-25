package com.yyds.gmall.common.cache;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 自定义的切面类
 */
@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 增强方法
     * @param point
     * @return
     */
    @Around("@annotation(com.yyds.gmall.common.cache.Java0217Cache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point){

        /*
        1.  获取参数列表
        2.  获取方法上的注解
        3.  获取前缀
        4.  获取目标方法的返回值
         */
        Object result = null;
        try {
            //获取方法的参数
            Object[] args = point.getArgs();
            //获取方法的签名
            MethodSignature signature = (MethodSignature) point.getSignature();
            //获取这个方法的指定类型的注解
            Java0217Cache java0217Cache = signature.getMethod().getAnnotation(Java0217Cache.class);
            // 获取前缀属性:getSkuInfo:
            String prefix = java0217Cache.prefix();
            // 从缓存中获取数据 key=getSkuInfo:[1]
            String key = prefix+Arrays.asList(args).toString();

            // 从redis中获取指定key的数据
            result = cacheHit(signature, key);
            if (result!=null){
                // 缓存有数据
                return result;
            }
            // redis没有数据,获取锁
            RLock lock = redissonClient.getLock(key + "lock");
            //尝试加锁
            boolean flag = lock.tryLock(100, 100, TimeUnit.SECONDS);
            if (flag){
               try {
                   try {
                       //执行方法:查询数据库
                       result = point.proceed(point.getArgs());
                       // 防止缓存穿透
                       if (null==result){
                           // 数据库没有数据
                           Object o = new Object();
                           this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(o),300,TimeUnit.SECONDS);
                           return o;
                       }else {
                           //数据库有数据
                            this.redisTemplate.opsForValue().set(key,JSONObject.toJSONString(result),24,TimeUnit.HOURS);
                       }
                   } catch (Throwable throwable) {
                       throwable.printStackTrace();
                   }
                   return result;
               }catch (Exception e){
                   e.printStackTrace();
               }finally {
                   // 释放锁
                   lock.unlock();
               }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //boolean flag = lock.tryLock(10L, 10L, TimeUnit.SECONDS);
        return result;
    }
    // 获取缓存数据
    private Object cacheHit(MethodSignature signature, String key) {
        // 使用参数的key获取redis的数据:获取到的数据为一个json类型的字符串
        String cache = (String)redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(cache)) {
            // 有，则反序列化，直接返回
            Class returnType = signature.getReturnType(); // 获取方法返回类型
            // 不能使用parseArray<cache, T>，因为不知道List<T>中的泛型
            //SkuInfo
            return JSONObject.parseObject(cache, returnType);
        }
        //redis中没有数据
        return null;
    }

}
