package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;

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

    /**
     * 批量删除套餐
     * @param ids 套餐id集合
     */
    void delete(List<Long> ids);
}
