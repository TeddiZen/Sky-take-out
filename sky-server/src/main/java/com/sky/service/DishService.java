package com.sky.service;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishOverViewVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    void save(DishDTO dishDTO);

    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO
     * @return
     */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids
     */
    void delete(List<Long> ids);
}
