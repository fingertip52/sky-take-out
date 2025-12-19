package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {

    @Autowired
    OrderMapper orderMapper;

    /**
     * 处理超时订单，超时时间是15分钟，所以这里每分钟执行一次判断即可
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOutOrder() {
        //处理超时订单，判断订单状态是否是待支付状态，判断下单时间距离现在的时间是否已经超过15分钟
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> orders = orderMapper.selectOrderByStatusAndTime(Orders.PENDING_PAYMENT, time);
        for (Orders order : orders) {
            order.setStatus(Orders.CANCELLED);
            order.setCancelTime(LocalDateTime.now());
            order.setCancelReason("订单超时，自动取消");
            orderMapper.update(order);
        }
    }

    /**
     * 处理派送中订单，如果订单已经完成，但是商店一直没有点击完成，此时就需要自动完成订单，此时只需要凌晨一点执行一次即可，
     * 将当天的所有派送中的订单自动完成
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("开始处理派送中订单...");
        LocalDateTime time = LocalDateTime.now().plusHours(-1);
        List<Orders> orders = orderMapper.selectOrderByStatusAndTime(Orders.DELIVERY_IN_PROGRESS, time);
        for (Orders order : orders) {
            order.setStatus(Orders.COMPLETED);
            orderMapper.update(order);
        }
    }
}
