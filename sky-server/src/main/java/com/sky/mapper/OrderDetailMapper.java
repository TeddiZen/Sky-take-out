package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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

    /**
     * 根据订单id查询订单细节
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> listByOrderId(Long orderId);

    /**
     * 根据订单ID列表查询销量排名top10
     * @param orderIds 订单ID列表
     * @return 销量排名列表
     */
    List<Map<String, Object>> selectTop10(List<Long> orderIds);

}