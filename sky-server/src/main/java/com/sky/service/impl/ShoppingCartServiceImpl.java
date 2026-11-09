package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishService dishMapper;

    @Autowired
    private SetmealService setmealMapper;


    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 查询购物车中是否存在相同菜品或套餐的记录
        List<ShoppingCart> ShoppingCartList = shoppingCartMapper.list(shoppingCart);

        if (ShoppingCartList != null && !ShoppingCartList.isEmpty()) {
            // 购物车中存在相同菜品或套餐的记录，更新数量和金额
            ShoppingCart existingCart = ShoppingCartList.get(0);
            existingCart.setNumber(existingCart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(existingCart);
        } else {
            // 购物车中不存在相同菜品或套餐的记录，添加新记录
            if(shoppingCart.getSetmealId() == null) {
                DishVO dish = dishMapper.getById(shoppingCart.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
            }else  {
                SetmealVO setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }


    /**
     * 查询购物车
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> ShoppingCartList = shoppingCartMapper.list(shoppingCart);
        return ShoppingCartList;
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCartMapper.delete(shoppingCart);
    }

    /**
     * 删除购物车
     * @param shoppingCartDTO
     */
    @Override
    public void delete(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 查询是否有多个记录
        List<ShoppingCart> ShoppingCartList = shoppingCartMapper.list(shoppingCart);
        ShoppingCart shoppingCart1 = ShoppingCartList.get(0);

        if (shoppingCart1.getNumber() > 1) {
            // 购物车中存在数量大于1的记录，更新数量
            shoppingCart1.setNumber(shoppingCart1.getNumber() - 1);
            shoppingCartMapper.updateNumberById(shoppingCart1);
        } else {
            // 购物车中不存在数量等于1的记录，删除记录
            shoppingCartMapper.delete(shoppingCart1);
        }

    }
}
