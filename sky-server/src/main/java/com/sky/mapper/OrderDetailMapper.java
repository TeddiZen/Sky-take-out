package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface OrderDetailMapper {
    /**
     * 新增订单细节
     * @param ordersDetail
     */
    @Options(useGeneratedKeys = true, keyProperty = "id") // 回填自增主键ID（如果需要的话）
    @Insert("insert into order_detail (order_id, dish_id, name, setmeal_id, dish_flavor, number, amount, image) " +
            "values (#{orderId}, #{dishId}, #{name}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{image})")
    void insert(OrderDetail ordersDetail);

}