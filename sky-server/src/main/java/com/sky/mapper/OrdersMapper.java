package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrdersMapper {
    /**
     * 新增订单
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 分页查询订单
     * @param ordersPageQueryDTO 订单查询参数
     * @return 订单分页结果集
     */
    Page<Orders> selectPage(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据订单id查询订单
     * @param id 订单id
     * @return 订单
     */
    @Select("select * from orders where id = #{id}")
    Orders selectById(Long id);
}