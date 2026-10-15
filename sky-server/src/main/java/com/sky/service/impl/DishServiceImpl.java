package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(DishDTO dishDTO) {
        //1、将DTO转换为实体对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //2、将实体对象转换为数据库中的数据
        dishMapper.insert(dish);
        //获取菜品id
        Long dishId = dish.getId();
        List<DishFlavor> dishFlavors = new ArrayList<>(dishDTO.getFlavors());
        //3、保存菜品口味到数据库
        if (!dishFlavors.isEmpty() && dishFlavors != null) {
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertFlavors(dishFlavors);
        }

    }
}
