package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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


    /**
     * 订单概览
     * @return 订单概览数据
     */
    OrderStatisticsVO overview();

    /**
     * 根据订单状态和订单时间查询订单
     * @param deliveryInProgress 订单状态
     * @param date 订单时间
     * @return 订单列表
     */
    @Select("select * from orders where status = #{deliveryInProgress} and order_time < #{date}")
    List<Orders> selectByStatusAndOrderTime(Integer deliveryInProgress, LocalDateTime date);


    /**
     * 查询营业额数据
     * @param map
     * @return
     */
    Double selectTurnoverByDate(Map<String, Object> map);
}