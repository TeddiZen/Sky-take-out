package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "购物车接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @ApiOperation("添加购物车")
    @PostMapping("/add")
    public Result<String> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车：{}", shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        log.info("添加购物车成功：{}", shoppingCartDTO);
        return Result.success();
    }

    @ApiOperation("查询购物车")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        log.info("查询购物车");
        return Result.success(shoppingCartService.list());
    }

    @ApiOperation("清空购物车")
    @DeleteMapping("/clean")
    public Result<String> clean() {
        log.info("清空购物车");
        shoppingCartService.clean();
        log.info("清空购物车成功");
        return Result.success();
    }

    @ApiOperation("删除购物车")
    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("删除购物车：{}", shoppingCartDTO);
        shoppingCartService.delete(shoppingCartDTO);
        return Result.success();
    }
}
