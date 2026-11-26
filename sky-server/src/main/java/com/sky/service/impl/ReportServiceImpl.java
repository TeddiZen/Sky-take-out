package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    OrdersMapper ordersMapper;

    @Autowired
    UserMapper userMapper;


    /**
     * 查询营业额数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业额数据
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        // 日期列表
        List<LocalDate> dateList = new ArrayList<>();
        // 营业额列表
        List<Double> turnoverList = new ArrayList<>();

        while (!begin.isAfter(end)) {

            LocalDateTime beginDateTime = LocalDateTime.of(begin, LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(begin, LocalTime.MAX);

            // 查询营业额
            Map<String, Object> map = new HashMap<>();
            map.put("begin", beginDateTime);
            map.put("end", endDateTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = ordersMapper.selectTurnoverByDate(map);
            turnover = turnover == null ? 0.0 : turnover;

            // 日期列表
            dateList.add(begin);
            // 营业额列表
            turnoverList.add(turnover);

            // 日期加1天
            begin = begin.plusDays(1);
        }

        String dateListStr = StringUtils.join(dateList, ",");
        String turnoverListStr = StringUtils.join(turnoverList, ",");
        return TurnoverReportVO.builder()
                .dateList(dateListStr)
                .turnoverList(turnoverListStr)
                .build();
    }

    /**
     * 查询用户统计接口
     * @param begin 开始时间
     * @param end 结束时间
     * @return 用户统计接口数据
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {


        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 日期列表
        List<LocalDate> dateList = new ArrayList<>();
        // 用户总量列表
        List<Integer> totalUserList = new ArrayList<>();
        // 新增用户列表
        List<Integer> newUserList = new ArrayList<>();

        // 一次性查询：拿到区间内每天新增用户
        List<Map<String, Object>> rawList = userMapper.listUserDailyNew(beginTime, endTime);

        // 处理查询到的新增用户数据
        Map<LocalDate, Integer> dayNewMap = new HashMap<>();
        for (Map<String, Object> item : rawList) {
            java.sql.Date sqlDate = (java.sql.Date) item.get("day");
            int cnt = ((Number) item.get("newUserCount")).intValue();
            dayNewMap.put(sqlDate.toLocalDate(), cnt);
        }

        // 遍历日期，计算用户总量和新增用户
        LocalDate currentDay = begin;
        Integer baseTotal = Optional.ofNullable(userMapper.selectCountByBegin(beginTime)).orElse(0);
        int total = baseTotal;
        while (!currentDay.isAfter(end)) {
            int todayNew = dayNewMap.getOrDefault(currentDay, 0);
            total += todayNew;

            dateList.add(currentDay);
            newUserList.add(todayNew);
            totalUserList.add(total);

            currentDay = currentDay.plusDays(1);
        }

        // 构建返回值
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }
}
