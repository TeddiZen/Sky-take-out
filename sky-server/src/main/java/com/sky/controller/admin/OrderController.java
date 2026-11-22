package com.sky.controller.admin;

import com.sky.dto.*;
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
    public Result<String> complete(@PathVariable Long id) {
        log.info("完成订单参数：{}", id);
        orderService.complete(id);
        return Result.success("完成订单成功");
    }

    /**
     * 接单订单
     * @param ordersConfirmDTO 订单确认DTO
     * @return 接单订单结果
     */
    @PutMapping("/confirm")
    @ApiOperation("接单订单")
    public Result<String> confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单订单确认参数：{}", ordersConfirmDTO);
        orderService.confirm(ordersConfirmDTO);
        return Result.success("接单订单成功");
    }

    /**
     * 派送订单
     * @param id 订单id
     * @return 派送订单结果
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result<String> delivery(@PathVariable Long id) {
        log.info("派送订单参数：{}", id);
        orderService.delivery(id);
        return Result.success("派送订单成功");
    }

    /**
     * 拒绝订单
     * @param ordersRejectDTO 订单拒绝DTO
     * @return 拒绝订单结果
     */
    @PutMapping("/rejection")
    @ApiOperation("拒绝订单")
    public Result<String> rejection(@RequestBody OrdersRejectionDTO ordersRejectDTO) {
        log.info("拒绝订单：{}",ordersRejectDTO);
        orderService.rejection(ordersRejectDTO);
        return Result.success("拒绝订单成功");
    }

    /**
     * 取消订单
     * @param ordersCancelDTO 订单取消DTO
     * @return 取消订单结果
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result<String> cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单：{}",ordersCancelDTO);
        orderService.adminCancel(ordersCancelDTO);
        return Result.success("取消订单成功");
    }

}
