package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * 每分钟执行一次,自动查询订单状态,更新订单状态为已完成或已取消
     */
    @Scheduled(cron = "0 * * * * ?")
    public void checkOrderStatus() {
        log.info("查找订单状态,更新订单状态为已完成或已取消");

        // 计算15分钟前的时间
        LocalDateTime date =  LocalDateTime.now().plusMinutes(-15);

        // 查找status为4和时间大于当前时间的订单,即派送中的订单
        List<Orders> ordersList = ordersMapper.selectByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, date);

        // 遍历订单列表,更新订单状态为已完成或已取消
        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(date);
                ordersMapper.update(orders);
            }
        }

    }

    /**
     * 每天凌晨一点执行一次，自动设置派送中订单为已完成
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void setDeliveredOrdersToCompleted() {
        log.info("设置派送中订单为已完成");

        // 计算60分钟前的时间
        LocalDateTime date =  LocalDateTime.now().plusMinutes(-60);

        // 查找所有派送中订单
        List<Orders> ordersList = ordersMapper.selectByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, date);

        // 遍历订单列表,更新订单状态为已完成
        // 凌晨一点把零点前的订单设置为已完成
        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                ordersMapper.update(orders);
            }
        }
    }
}
