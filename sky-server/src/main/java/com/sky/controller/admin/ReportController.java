package com.sky.controller.admin;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "订单报表")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 查询营业额数据
     *
     * @param end 结束时间
     * @return 营业额数据
     */
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> getTurnoverStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("查询营业额数据，时间范围：{} - {}", begin, end);
        TurnoverReportVO turnoverReportVO = reportService.getTurnoverStatistics(begin, end);
        log.info("营业额数据：{}", turnoverReportVO);
        return Result.success(turnoverReportVO);
    }

    /**
     * 查询用户统计接口
     *
     * @param end 结束时间
     * @return 用户统计接口数据
     */
    @GetMapping("/userStatistics")
    public Result<UserReportVO> getUserStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("查询用户统计接口数据，时间范围：{} - {}", begin, end);
        UserReportVO userReportVO = reportService.getUserStatistics(begin, end);
        log.info("用户统计接口数据：{}", userReportVO);
        return Result.success(userReportVO);
    }

    /**
     * 查询订单统计接口
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 订单统计接口数据
     */
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> getOrderStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("查询订单统计接口数据，时间范围：{} - {}", begin, end);
        OrderReportVO orderReportVO = reportService.getOrderStatistics(begin, end);
        log.info("订单统计接口数据：{}", orderReportVO);
        return Result.success(orderReportVO);
    }
}
