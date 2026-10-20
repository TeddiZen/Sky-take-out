package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.SetmealDish;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

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

    /**
     * 根据id查询套餐
     * @param id 套餐id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 修改套餐信息
     * @param setmealDTO 套餐信息
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 修改套餐状态
     * @param status 套餐状态
     * @param id 套餐id
     */
    void updateStatus(Integer status, Long id);
}
