package com.yyds.gmall.seckill.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yyds.gmall.model.activity.SeckillGoods;
import com.yyds.gmall.seckill.mapper.SeckillOrderMapper;
import com.yyds.gmall.seckill.pojo.SeckillOrder;
import com.yyds.gmall.seckill.pojo.UserRecode;
import com.yyds.gmall.seckill.service.SeckillOrderService;
import com.yyds.gmall.seckill.util.DateUtil;
import com.yyds.gmall.seckill.util.SeckillThreadLocalUtil;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: SeckillOrderServiceImpl
 * @Author: yyd
 * @Date: 2022/8/15/015
 * @Description:秒杀订单接口类的实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SeckillOrderServiceImpl implements SeckillOrderService {
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    
    /**
     * 秒杀下单: 不下单,只排队--->同步排队
     *
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    @Override
    public UserRecode addSeckillOrder(String time, String goodsId, Integer num) {
        String username = "liuyingjun";
        //排队: 记录谁 要买哪个时间段的哪个商品 买几个
        UserRecode userRecode = new UserRecode();
        //确认用户只排一次队
        Long count = redisTemplate.opsForValue().increment("User_Queue_Count_" + username, 1);
        //和订单的有效期保持一致,以免删除失败,最终出现删除失败的情况也只会影响用户5分钟不能下单
        redisTemplate.expire("User_Queue_Count_" + username, 300, TimeUnit.SECONDS);
        //TODO--排队次数没有设置过期时间
        if (count > 1) {
            //重复排队
            userRecode.setStatus(3);
            userRecode.setMsg("秒杀失败,重复排队!");
            return userRecode;
        }
        userRecode.setGoodsId(goodsId);
        userRecode.setNum(num);
        userRecode.setTime(time);
        userRecode.setCreateTime(new Date());
        userRecode.setUsername(username);
        userRecode.setStatus(1);
        userRecode.setMsg("排队中!");
        
        //异步执行
        CompletableFuture.runAsync(() -> {
            //将排队的状态写入redis
            redisTemplate.opsForValue().set("User_Recode_" + username, userRecode);
            //通知下单
            rabbitTemplate.convertAndSend("seckill_order_exchange", "seckill.order.add", JSONObject.toJSONString(userRecode));
        }, threadPoolExecutor).whenCompleteAsync((a, b) -> {
            if (b != null) {
                userRecode.setStatus(3);
                userRecode.setMsg("秒杀下单失败,请重试!");
                redisTemplate.opsForValue().set("User_Recode_" + username, userRecode);
            }
        }, threadPoolExecutor);
        
        //TODO--重复排队
        //结束
        return userRecode;
    }
    
    /**
     * 查询用户的排队状态
     *
     * @return
     */
    @Override
    public UserRecode getUserRecode() {
        String username = "liuyingjun";
        return (UserRecode) redisTemplate.opsForValue().get("User_Recode_" + username);
    }
    
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    
    /**
     * 真实下单
     *
     * @param userRecode
     */
    @Override
    public void realSeckillOrderAdd(UserRecode userRecode) throws Exception {
        //获取购买的商品的时间段
        String time = userRecode.getTime();
        //获取购买的商品id
        String goodsId = userRecode.getGoodsId();
        //获取用户购买的数量
        Integer num = userRecode.getNum();
        //获取用户的用户名
        String username = userRecode.getUsername();
        //从redis中获取商品的信息
        SeckillGoods seckillGoods =
                (SeckillGoods) redisTemplate.opsForHash().get(time, goodsId);
        //判断商品是否存在
        if (seckillGoods == null || seckillGoods.getId() == null) {
            //商品不存在,秒杀下单失败
//            userRecode.setStatus(3);
//            userRecode.setMsg("商品不存在,秒杀失败!");
//            //更新redis中排队的状态
//            redisTemplate.opsForValue().set("User_Recode_" + username, userRecode);
//            //删除排队的计数器,不能影响用户购买其他的东西
//            redisTemplate.delete("User_Queue_Count_" + username);
            addSeckillOrderFail(userRecode, "商品不存在");
            //流程结束
            return;
        }
        //是否在活动时间以内
        String nowTime =
                DateUtil.data2str(DateUtil.getDateMenus().get(0), DateUtil.PATTERN_YYYYMMDDHH);
        if (!nowTime.equals(time)) {
            //商品不在活动时间以内,秒杀下单失败
//            userRecode.setStatus(3);
//            userRecode.setMsg("商品不在活动时间以内,秒杀失败!");
//            //更新redis中排队的状态
//            redisTemplate.opsForValue().set("User_Recode_" + username, userRecode);
//            //删除排队的计数器,不能影响用户购买其他的东西
//            redisTemplate.delete("User_Queue_Count_" + username);
            addSeckillOrderFail(userRecode, "商品不在活动时间以内");
            //流程结束
            return;
        }
        //数量是否正确
        if (num <= 0 || seckillGoods.getSeckillLimit() < num) {
            //商品不存在,秒杀下单失败
//            userRecode.setStatus(3);
//            userRecode.setMsg("商品超出限购,秒杀失败!");
//            //更新redis中排队的状态
//            redisTemplate.opsForValue().set("User_Recode_" + username, userRecode);
//            //删除排队的计数器,不能影响用户购买其他的东西
//            redisTemplate.delete("User_Queue_Count_" + username);
            addSeckillOrderFail(userRecode, "商品超出限购");
            //流程结束
            return;
        }
        //方案一:
        //扣减库存---超卖需要解决---待优化为N个--TODO
        for (int i = 0; i < num; i++) {
            Object o =
                    redisTemplate.opsForList().rightPop("Seckill_Goods_Stock_Queue_" + goodsId);
            if (o == null) {
                //商品不存在,秒杀下单失败
//                userRecode.setStatus(3);
//                userRecode.setMsg("商品库存不足,秒杀失败!");
//                //更新redis中排队的状态
//                redisTemplate.opsForValue().set("User_Recode_" + username, userRecode);
//                //删除排队的计数器,不能影响用户购买其他的东西
//                redisTemplate.delete("User_Queue_Count_" + username);
                addSeckillOrderFail(userRecode, "库存不足");
                if (i > 0) {
                    //回滚
                    String[] ids = getIds(i, goodsId);
                    redisTemplate
                            .opsForList()
                            .leftPushAll("Seckill_Goods_Stock_Queue_" + seckillGoods.getId(), ids);
                }
                //流程结束
                return;
            }
        }
        //方案二,库存加的时候会出现负数的情况,有点:操作速度快,回滚速度也快
        //商品库存更新
        Long increment =
                redisTemplate.opsForHash().increment("SeckillGoodsStockCount_" + time, goodsId, -num);
//        if (increment < 0) {
//            //商品不存在,秒杀下单失败
//            userRecode.setStatus(3);
//            userRecode.setMsg("商品库存不足,秒杀失败!");
//            //更新redis中排队的状态
//            redisTemplate.opsForValue().set("User_Recode_" + username, userRecode);
//            //删除排队的计数器,不能影响用户购买其他的东西
//            redisTemplate.delete("User_Queue_Count_" + username);
//            //回滚
//            redisTemplate.opsForHash().increment("SeckillGoodsStockCount_" + time, goodsId, num);
//        }
        try {
            //商品库存更新--TODO
            
            seckillGoods.setStockCount(increment.intValue());
            redisTemplate.opsForHash().put(time, goodsId, seckillGoods);
            //下单
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setId(UUID.randomUUID().toString().replace("-", ""));
            seckillOrder.setGoodsId(goodsId);
            seckillOrder.setNum(num);
            seckillOrder.setMoney(seckillGoods.getCostPrice().multiply(new BigDecimal(num)).toString());
            seckillOrder.setUserId(username);
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setStatus("0");
            //异步写数据库保存订单
            CompletableFuture<Boolean> future1 = CompletableFuture.supplyAsync(() -> {
                //写库
                int insert = seckillOrderMapper.insert(seckillOrder);
                return true;
            }, threadPoolExecutor).exceptionally((a) -> {
                return false;
            });
            //异步写redis保存订单
            CompletableFuture<Boolean> future2 = CompletableFuture.supplyAsync(() -> {
                redisTemplate.opsForHash().put("User_Seckill_Order_" + time, seckillOrder.getId(), seckillOrder);
                return true;
            }, threadPoolExecutor).exceptionally((a) -> {
                return false;
            });
            //判断是否同时失败
            if (!future1.get() && !future2.get()) {
                throw new RuntimeException("");
            }
            //补全订单号和金额到排队状态中
            userRecode.setOrderId(seckillOrder.getId());
            userRecode.setMoney(seckillOrder.getMoney());
            userRecode.setStatus(2);
            userRecode.setMsg("秒杀成功,等待支付");
            //更新redis中排队的状态
            redisTemplate.opsForValue().set("User_Recode_" + username, userRecode);
            //TODO--订单的后续处理:1.取消 2.超时 3.支付
            //防止用户不付钱,发送延迟消息,保证用户在5min不付钱订单自动取消
            rabbitTemplate.convertAndSend(
                    "seckill_order_normal_exchange",
                    "seckill.order.dead",
                    seckillOrder.getId(),
                    (message -> {
                        //获取属性
                        MessageProperties messageProperties = message.getMessageProperties();
                        //设置过期时间
                        messageProperties.setExpiration("30000");
                        //返回
                        return message;
                    })
            );
        } catch (Exception e) {
            //商品不存在,秒杀下单失败
            userRecode.setStatus(3);
            userRecode.setMsg("秒杀失败,请重试!");
            //更新redis中排队的状态
            redisTemplate.opsForValue().set("User_Recode_" + username, userRecode);
            //删除排队的计数器,不能影响用户购买其他的东西
            redisTemplate.delete("User_Queue_Count_" + username);
            //下单若失败,库存需要回滚--待优化为N个--TODO
            redisTemplate.opsForList().leftPush("Seckill_Goods_Stock_Queue_" + goodsId, goodsId);
        }
    }
    
    /**
     * 取消订单:1.超时 2.主动
     *
     * @param username
     */
    @Override
    public void cancelSeckillOrder(String username) {
        String msg = "";
        if (!StringUtils.isEmpty(username)) {
            //超时取消,不用校验是否是自己取消自己的订单
            msg = "超时取消";
        } else {
            //主动取消,需要校验是否为自己的订单
            username = SeckillThreadLocalUtil.get();
            msg = "主动取消";
        }
        //获取用户的排队状态
        UserRecode userRecode =
                (UserRecode) redisTemplate.opsForValue().get("User_Recode" + username);
        //查询订单--数据库中订单的数据
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(
                new LambdaQueryWrapper<SeckillOrder>()
                        .eq(SeckillOrder::getUserId, username)
                        .eq(SeckillOrder::getStatus, "0")
        );
        //判断订单是否存在--在数据库中
        if (seckillOrder == null || StringUtils.isEmpty(seckillOrder.getId())) {
            //数据库中没有订单的数据,还需要去redis中进行确认
            redisTemplate.opsForHash().get("User_Seckill_Order_" + userRecode.getTime(),
                    userRecode.getOrderId());
            if (seckillOrder == null || StringUtils.isEmpty(seckillOrder.getId())) {
                return;
            }
        }
        //修改订单
        seckillOrder.setStatus(msg);
        int update = seckillOrderMapper.updateById(seckillOrder);
        if (update <= 0) {
            throw new RuntimeException("取消订单失败,订单号为:" + seckillOrder.getId());
        }
        //回滚库存
        rollbackSekillGoodsStock(userRecode);
        //标识位没清理--排队计数器删除
        redisTemplate.delete("User_Queue_Count_" + seckillOrder.getUserId());
        //排队状态--删除掉
        redisTemplate.delete("User_Recode_" + seckillOrder.getUserId());
        //删除redis中的临时订单数据
        redisTemplate.opsForHash().delete("User_Seckill_Order_" + userRecode.getTime(), seckillOrder.getId());
    }
    
    /**
     * 修改秒杀订单的支付结果
     *
     * @param result
     */
    @Override
    public void updateSeckillOrder(String result) {
        //支付结果反序列化
        Map<String, String> resultMap = JSONObject.parseObject(result, Map.class);
        //获取订单号
        String orderId = resultMap.get("out_trade_no");
        //查询订单的消息
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(
                new LambdaQueryWrapper<SeckillOrder>()
                        .eq(SeckillOrder::getId, orderId)
                        .eq(SeckillOrder::getStatus, "0")
        );
        //获取附加参数
        String attaching = resultMap.get("attach");
        Map<String, String> attach = JSONObject.parseObject(attaching, Map.class);
        String username = attach.get("username");
        //获取用户的排队状态
        UserRecode userRecode =
                (UserRecode) redisTemplate.opsForValue().get("User_Recode_" + username);
        if (seckillOrder==null||seckillOrder.getId()==null){
            //再去redis中找
            //数据库中没有订单的数据,还需要去redis中进行确认
            seckillOrder =
                    (SeckillOrder) redisTemplate.opsForHash().get("User_Seckill_Order_" + userRecode.getTime(),userRecode.getOrderId());
            if (seckillOrder == null || StringUtils.isEmpty(seckillOrder.getId())) {
                return;
            }
        }
        //订单状态
        seckillOrder.setStatus("已支付");
        //判断支付渠道
        if (resultMap.get("payway").equals("1")){
            //微信流水号
            seckillOrder.setOutTradeNo(resultMap.get("transaction_id"));
        }else {
            //支付宝流水号
            seckillOrder.setOutTradeNo(resultMap.get("trade_no"));
        }
        //修改
        int update = seckillOrderMapper.updateById(seckillOrder);
        if (update <= 0){
            throw new RuntimeException("修改订单的支付结果失败!");
        }
        //标识位清理,排队计数器删除
        redisTemplate.delete("User_Queue_Count_" + seckillOrder.getUserId());
        //排队状态删除掉
        redisTemplate.delete("User_Recode_" + seckillOrder.getUserId());
        //删除redis中的临时订单数据
        redisTemplate.opsForHash().delete("User_Seckill_Order_" + userRecode.getTime(), seckillOrder.getId());
    }
    
    /**
     * 回滚库存
     *
     * @param userRecode
     */
    private void rollbackSekillGoodsStock(UserRecode userRecode) {
        //回滚商品库存的自增值,并且获取到回滚到的库存
        Long increment = redisTemplate.opsForHash().increment(
                "SeckillGoodsStockCount_" + userRecode.getTime(),
                userRecode.getGoodsId(),
                userRecode.getNum()
        );
        //从redis中获取商品的数据
        SeckillGoods seckillGoods =
                (SeckillGoods) redisTemplate.opsForHash().get(userRecode.getTime(), userRecode.getGoodsId());
        //判断活动是否结束
        if (seckillGoods != null) {
            //活动没有结束,更新商品数据
            seckillGoods.setStockCount(increment.intValue());
            redisTemplate.opsForHash().put(userRecode.getTime(), userRecode.getGoodsId(), seckillGoods);
            //计算队列需要回滚的数量,更新队列数据
            String[] ids = getIds(userRecode.getNum(), userRecode.getGoodsId());
            redisTemplate.opsForList().leftPushAll("Seckill_Goods_Stock_Queue_" + userRecode.getGoodsId(), ids);
        }
    }
    
    /**
     * 构建秒杀商品库存长度的数组
     *
     * @param stockCount
     * @param goodsId
     * @return
     */
    private String[] getIds(Integer stockCount, String goodsId) {
        //数组初始化
        String[] ids = new String[stockCount];
        //为每个元素赋值
        for (int i = 0; i < stockCount; i++) {
            ids[i] = goodsId;
        }
        //返回
        return ids;
    }
    
    private void addSeckillOrderFail(UserRecode userRecode, String msg) {
        //商品不存在,秒杀下单失败
        userRecode.setStatus(3);
        userRecode.setMsg(msg + "秒杀失败,请重试!");
        //更新redis中排队的状态
        redisTemplate.opsForValue().set("User_Recode_" + userRecode.getUsername(), userRecode);
        //删除排队的计数器,不能影响用户购买其他的东西
        redisTemplate.delete("User_Queue_Count_" + userRecode.getUsername());
    }
}
