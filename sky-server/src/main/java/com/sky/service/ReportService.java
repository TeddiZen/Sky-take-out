package com.sky.service;

import com.sky.entity.User;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {

    /**
     * 查询营业额数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业额数据
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 查询用户统计接口
     * @param begin 开始时间
     * @param end 结束时间
     * @return 用户统计接口数据
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);
}
