package com.yyds.gmall.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yyds.gmall.cart.feign.CartFeign;
import com.yyds.gmall.model.cart.CartInfo;
import com.yyds.gmall.model.enums.OrderStatus;
import com.yyds.gmall.model.enums.ProcessStatus;
import com.yyds.gmall.model.order.OrderDetail;
import com.yyds.gmall.model.order.OrderInfo;
import com.yyds.gmall.order.mapper.OrderDetailMapper;
import com.yyds.gmall.order.mapper.OrderInfoMapper;
import com.yyds.gmall.order.service.OrderService;
import com.yyds.gmall.order.util.OrderThreadLocalUtil;
import com.yyds.gmall.product.feign.ItemFeign;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: OrderServiceImpl
 * @Author: yyd
 * @Date: 2022/8/10/010
 * @Description: 普通订单接口的实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {
    @Autowired
    private CartFeign cartFeign;
    
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    
    @Autowired
    private ItemFeign itemFeign;
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 新增订单
     *
     * @param orderInfo
     */
    @Override
    public void addOrder(OrderInfo orderInfo) {
        
        //参数校验
        if (orderInfo == null) {
            throw new RuntimeException("参数错误,新增订单失败");
        }
        //使用用户名自增值,每次+1
        Long increment =
                redisTemplate.opsForValue().increment("user_add_order_count_" + OrderThreadLocalUtil.get(), 1);
        //设置标识位过期时间10秒最长,影响到是接下来最长10秒内用户不能下单
        redisTemplate.expire("user_add_order_count_" + OrderThreadLocalUtil.get(), 10, TimeUnit.SECONDS);
        if (increment > 1) {
            throw new RuntimeException("新增订单失败,重复提交订单");
        }
        try {
            //查询购物车的信息
            Map<String, Object> cartInfo = cartFeign.getCartInfo();
            //判断购物车返回的是否为空
            if (cartInfo == null) {
                throw new RuntimeException("用户购物车没有商品,新增订单失败");
            }
            //补全orderInfo对象缺少的信息
            orderInfo.setTotalAmount(new BigDecimal(cartInfo.get("totalMoney").toString()));
            orderInfo.setOrderStatus(OrderStatus.UNPAID.getComment());
            orderInfo.setUserId(OrderThreadLocalUtil.get());
            orderInfo.setCreateTime(new Date());
            orderInfo.setExpireTime(new Date(System.currentTimeMillis() + 1800000));
            orderInfo.setProcessStatus(ProcessStatus.UNPAID.getComment());
            //新增订单---事务一
            int insert = orderInfoMapper.insert(orderInfo);
            if (insert <= 0) {
                throw new RuntimeException("用户购物车没有选中商品,新增订单失败");
            }
            //新增订单成功,则拥有订单号
            Long orderId = orderInfo.getId();
            //新增订单的详情
            List cartInfoList = (List) cartInfo.get("cartInfoList");
            //新增订单的详情,并且根据需要扣减的商品的信息和库存的信息
            Map<String, Object> skuParam =
                    saveOrderDetail(orderId, cartInfoList);
            //扣减库存
            itemFeign.decountStocks(skuParam);
            //购物车清理
//        cartFeign.clearCart();
            //发送延迟消息,防止超时不付钱的订单
            rabbitTemplate.convertAndSend("order_nomal_exchange",
                    "order.dead",
                    orderId + "",
                    (message -> {
                        //获取消息的属性
                        MessageProperties messageProperties = message.getMessageProperties();
                        //设置过期时间30分钟(测试20s)
                        messageProperties.setExpiration(20000 + "");
                        //返回
                        return message;
                    }));
        } catch (Exception e) {
            //代码执行出现问题
            throw new RuntimeException("新增订单失败,失败的原因为:" + e.getMessage());
        } finally {
            //清理标识位
            redisTemplate.delete("user_add_order_count_" + OrderThreadLocalUtil.get());
        }
    }
    
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    
    /**
     * 保存新增订单的详情
     *
     * @param orderId
     * @param cartInfoList
     * @return
     */
    private Map<String, Object> saveOrderDetail(Long orderId, List cartInfoList) {
        //初始化
        Map<String, Object> skuParam = new ConcurrentHashMap<>();
        //遍历新增订单的详情
        cartInfoList.stream().forEach(o -> {
            //序列化
            String s = JSONObject.toJSONString(o);
            //反序列化
            CartInfo cartInfo = JSONObject.parseObject(s, CartInfo.class);
            //包装购物车数据为订单详情
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setOrderPrice(cartInfo.getSkuPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setSkuId(cartInfo.getSkuId());
            skuParam.put(cartInfo.getSkuId() + "", cartInfo.getSkuNum());
            //新增
            int insert = orderDetailMapper.insert(orderDetail);
            if (insert <= 0) {
                throw new RuntimeException("新增订单详情失败");
            }
        });
        //返回
        return skuParam;
    }
    
    /**
     * 取消订单
     *
     * @param orderId
     */
    @Override
    public void cancelOrder(Long orderId) {
        //从本地线程获取用户名
        String username = OrderThreadLocalUtil.get();
        //幂等性问题:只处理未支付的订单
        OrderInfo orderInfo = orderInfoMapper.selectOne(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getId, orderId)
                        .eq(OrderInfo::getOrderStatus, OrderStatus.UNPAID.getComment())
        );
        if (orderInfo == null || orderInfo.getId() == null) {
            throw new RuntimeException("取消订单失败!");
        }
        //判断是否为空
        if (StringUtils.isEmpty(username)) {
            //超时取消
            orderInfo.setOrderStatus(OrderStatus.TIMEOUT.getComment());
            orderInfo.setProcessStatus(OrderStatus.TIMEOUT.getComment());
        } else {
            //用户主动取消
            orderInfo.setOrderStatus(OrderStatus.CANCLE.getComment());
            orderInfo.setProcessStatus(OrderStatus.TIMEOUT.getComment());
        }
        //修改订单
        int update = orderInfoMapper.updateById(orderInfo);
        if (update <= 0) {
            throw new RuntimeException("取消订单失败!");
        }
        //回滚库存--依据订单详情
        rollbackStock(orderId);
    }
    
    /**
     * 修改订单的支付状态
     *
     * @param result
     */
    @Override
    public void updateOrder(String result) {
        //支付结果反序列化
        Map<String, String> resultMap = JSONObject.parseObject(result, Map.class);
        //获取订单号
        String orderId = resultMap.get("out_trade_no");
        //查询订单的信息
        OrderInfo orderInfo = orderInfoMapper.selectOne(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getId, Long.parseLong(orderId))
                        .eq(OrderInfo::getOrderStatus, OrderStatus.UNPAID.getComment())
        );
        if (orderInfo ==null||orderInfo.getId()==null){
            return;
        }
        //存储第三方支付结果的完整报文
        orderInfo.setTradeBody(result);
        //订单状态
        orderInfo.setOrderStatus(OrderStatus.PAID.getComment());
        orderInfo.setProcessStatus(OrderStatus.PAID.getComment());
        //判断支付渠道
        if (resultMap.get("payway").equals("1")){
            //微信流水号
            orderInfo.setOutTradeNo(resultMap.get("transaction_id"));
        }else {
            //支付宝流水号
            orderInfo.setOutTradeNo((resultMap.get("trade_no")));
        }
        //修改
        int update = orderInfoMapper.updateById(orderInfo);
        if (update<=0){
            throw new RuntimeException("修改订单的支付结果事变!");
        }
        //实战----仓库管理系统---发送发货的消息--生产者--TODO
    }
    
    /**
     * 回滚库存,依据订单详情
     *
     * @param orderId
     */
    private void rollbackStock(Long orderId) {
        //查询订单的详情数据
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(
                new LambdaQueryWrapper<OrderDetail>()
                        .eq(OrderDetail::getOrderId, orderId)
        );
        //统计需要回滚的库存
        Map<String, Object> skuParam = new ConcurrentHashMap<>();
        orderDetailList.stream().forEach(orderDetail -> {
            //获取商品的id
            Long skuId = orderDetail.getSkuId();
            //获取商品的数量
            Integer skuNum = orderDetail.getSkuNum();
            //保存
            skuParam.put(skuId + "", skuNum);
        });
        //调用回滚
        itemFeign.rollbackStocks(skuParam);
    }
}
