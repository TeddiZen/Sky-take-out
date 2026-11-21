package com.sky.service;


import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.*;

public interface OrderService {
    
    /**
     * 提交订单
     * @param ordersSubmitDTO 订单提交DTO
     * @return 订单提交VO
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO 订单查询参数
     * @return 订单分页结果集
     */
    PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 订单详情
     */
    OrderVO detail(Long id);


    /**
     * 取消订单
     * @param id 订单id
     */
    void cancel(Long id);

    /**
     * 再来一单
     * @param id 订单id
     */
    void repetition(Long id);

    /**
     * 订单搜索
     * @param ordersPageQueryDTO 订单查询参数
     * @return 订单查询参数集
     */
    PageResult searchOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 订单概览
     * @return 订单概览数据
     */
    OrderStatisticsVO overview();

    /**
     * 完成订单
     * @param id 订单id
     */
    void complete(Long id);

    /**
     * 接单订单
     * @param id 订单id
     */
    void confirm(Long id);

    /**
     * 派送订单
     * @param id 订单id
     */
    void delivery(Long id);
}
