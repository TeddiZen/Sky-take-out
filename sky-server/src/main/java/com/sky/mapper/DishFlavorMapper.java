package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 新增菜品口味
     * @param dishFlavors
     */
    void insertFlavors(List<DishFlavor> dishFlavors);

    /**
     * 根据id删除菜品口味
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据dish_id查询菜品口味
     * @param dishId
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> selectById(Long dishId);
}
