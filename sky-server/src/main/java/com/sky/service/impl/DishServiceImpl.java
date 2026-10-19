package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;

import com.sky.vo.DishOverViewVO;
import com.sky.vo.DishVO;
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

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(DishDTO dishDTO) {
        //1、将DTO转换为实体对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //2、将实体对象转换为数据库中的数据
        dishMapper.insert(dish);
        //获取菜品id
        Long dishId = dish.getId();
        List<DishFlavor> dishFlavors = new ArrayList<>(dishDTO.getFlavors());
        //3、保存菜品口味到数据库
        if (dishFlavors != null && !dishFlavors.isEmpty()) {
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertFlavors(dishFlavors);
        }

    }

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        //1、分页查询菜品
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.selectPage(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {

        // 查出菜品
        for(Long id:ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == 1){
                throw new DeletionNotAllowedException("已启售菜品不能删除");
            }
        }

        // 查看菜品是否被套餐关联了
        List<Long> dishIds = setmealDishMapper.selectByDishIds(ids);
        if(!dishIds.isEmpty()) {
            throw new DeletionNotAllowedException("套餐中包含该菜品，不能删除");
        }

        // 删除菜品以及菜品
        dishMapper.deleteByIds(ids);
        // 删除菜品口味
        dishFlavorMapper.deleteByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWithFlavor(DishDTO dishDTO) {
        //1、将DTO转换为实体对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //2、将实体对象转换为数据库中的数据
        dishMapper.updateById(dish);
        //3、将菜品口味转换为数据库中的数据
        // 先删除菜品口味
        List<Long> ids = new ArrayList<>();
        ids.add(dish.getId());
        dishFlavorMapper.deleteByIds(ids);
        // 再新增菜品口味
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if (!dishFlavors.isEmpty()) {
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
            });
            dishFlavorMapper.insertFlavors(dishFlavors);
        }
    }

    @Override
    public DishVO getById(Long id) {

        // 将查询到的菜品转换为VO中的数据
        DishVO dishVO = new DishVO();
        Dish dish = dishMapper.getById(id);
        BeanUtils.copyProperties(dish, dishVO);

        // 根据dish_id查询菜品口味
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectById(id);

        // 将菜品口味转换为VO中的数据
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    @Override
    public List<Dish> listByCategoryId(Long categoryId) {
        List<Dish> list = dishMapper.selectByCategoryId(categoryId);
        return list;
    }
}

