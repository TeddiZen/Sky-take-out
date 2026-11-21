package com.sky.controller.admin;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "订单管理")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 订单搜索
     * @param ordersPageQueryDTO 订单查询参数
     * @return 订单分页结果集
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单搜索参数：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.searchOrders(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 订单详情
     * @param id 订单id
     * @return 订单详情
     */
    @GetMapping("/details/{id}")
    @ApiOperation("订单详情")
    public Result<OrderVO> detail(@PathVariable Long id) {
        log.info("订单详情参数：{}", id);
        OrderVO orderVO = orderService.detail(id);
        return Result.success(orderVO);
    }

    /**
     * 各个状态的订单数量统计
     * @return 订单概览数据
     */
    @GetMapping("/statistics")
    @ApiOperation("订单概览")
    public Result<OrderStatisticsVO> overview() {
        log.info("查询订单概览参数");
        OrderStatisticsVO orderStatisticsVO = orderService.overview();
        log.info("查询订单概览结果：{}", orderStatisticsVO);
        return Result.success(orderStatisticsVO);
    }

    /**
     * 完成订单
     * @param id 订单id
     * @return 完成订单结果
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result<String> complete(@RequestParam Long id) {
        log.info("完成订单参数：{}", id);
        orderService.complete(id);
        return Result.success("完成订单成功");
    }

    /**
     * 接单订单
     * @param id 订单id
     * @return 接单订单结果
     */
    @PutMapping("/confirm")
    @ApiOperation("接单订单")
    public Result<String> confirm(@RequestParam Long id) {
        log.info("接单订单参数：{}", id);
        orderService.confirm(id);
        return Result.success("接单订单成功");
    }

    /**
     * 取消订单
     * @param id 订单id
     * @return 取消订单结果
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result<String> delivery(@RequestParam Long id) {
        log.info("派送订单参数：{}", id);
        orderService.delivery(id);
        return Result.success("派送订单成功");
    }

}
