package com.sky.controller.admin;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/turnoverStatistics")
    public TurnoverReportVO getTurnoverStatistics(@RequestParam LocalDate begin, @RequestParam LocalDate end) {
        log.info("查询营业额数据，时间范围：{} - {}", begin, end);
        return reportService.getTurnoverStatistics(begin, end);
    }
}
