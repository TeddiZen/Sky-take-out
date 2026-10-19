package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;

public interface SetmealService {

    /**
     * 新增套餐
     * @param setmealDTO 套餐
     */
    void insert(SetmealDTO setmealDTO);


    /**
     * 套餐分页查询
     * @param setmealDTO 套餐
     * @return
     */
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);
}
