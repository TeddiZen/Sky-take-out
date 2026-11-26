package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    OrdersMapper ordersMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    OrdersMapper orderMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;


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

        // 转换为LocalDateTime
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 预先计算总天数，初始化集合容量
        long dayCount = ChronoUnit.DAYS.between(begin, end) + 1;
        List<LocalDate> dateList = new ArrayList<>((int) dayCount);
        List<Integer> totalUserList = new ArrayList<>((int) dayCount);
        List<Integer> newUserList = new ArrayList<>((int) dayCount);

        // =========数据库查询统一放最前面=========
        // 查询起始时间前存量用户
        Integer baseTotal = Optional.ofNullable(userMapper.selectCountByBegin(beginTime)).orElse(0);
        // 查询时间段内每日新增，兜底空集合
        List<Map<String, Object>> rawList = Optional.ofNullable(userMapper.listUserDailyNew(beginTime, endTime))
                .orElse(Collections.emptyList());

        Map<LocalDate, Integer> dayNewMap = new HashMap<>((int) dayCount);
        for (Map<String, Object> item : rawList) {
            Object dayObj = item.get("day");
            if (dayObj == null) {
                continue;
            }
            java.sql.Date sqlDate = (java.sql.Date) dayObj;
            int cnt = ((Number) item.get("newUserCount")).intValue();
            dayNewMap.put(sqlDate.toLocalDate(), cnt);
        }

        LocalDate currentDay = begin;
        int total = baseTotal;
        while (!currentDay.isAfter(end)) {
            int todayNew = dayNewMap.getOrDefault(currentDay, 0);
            total += todayNew;

            dateList.add(currentDay);
            newUserList.add(todayNew);
            totalUserList.add(total);

            currentDay = currentDay.plusDays(1);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 查询订单统计接口
     * @param begin 开始时间
     * @param end 结束时间
     * @return 订单统计接口数据
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        LocalDateTime currentTime = beginTime;

        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;
        Double orderCompletionRate = 0.0;


        // 查询时间段内每日总订单数、每日有效订单数
        while (!currentTime.isAfter(endTime)) {

            // 查询当前日期订单数
            Integer todayOrderCount = ordersMapper.sumTodayOrder(currentTime);
            todayOrderCount = todayOrderCount == null ? 0 : todayOrderCount;
            totalOrderCount += todayOrderCount;

            // 查询有效订单
            Integer todayValidOrderCount = ordersMapper.sumValidOrder(currentTime);
            todayValidOrderCount = todayValidOrderCount == null ? 0 : todayValidOrderCount;
            validOrderCount += todayValidOrderCount;

            // 日期列表
            dateList.add(currentTime.toLocalDate());
            // 订单数列表
            orderCountList.add(todayOrderCount);
            // 有效订单数列表
            validOrderCountList.add(todayValidOrderCount);

            // 日期加1天
            currentTime = currentTime.plusDays(1);
        }

        // 计算订单完成率
        if (totalOrderCount > 0) {
            orderCompletionRate = validOrderCount.doubleValue() / (double) totalOrderCount;
        }


        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 查询销量排名top10接口
     * @param begin 开始时间
     * @param end 结束时间
     * @return 销量排名top10接口数据
     */
    @Override
    public SalesTop10ReportVO getSalesRankingTop10(LocalDate begin, LocalDate end) {

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 第一步：查询时间范围内已完成的订单ID列表
        Map<String, Object> map = new HashMap<>();
        map.put("begin", beginTime);
        map.put("end", endTime);
        map.put("status", Orders.COMPLETED);
        List<Long> orderIds = ordersMapper.selectOrderIdsByCondition(map);

        // 如果没有符合条件的订单，直接返回空数据
        if (orderIds == null || orderIds.isEmpty()) {
            return SalesTop10ReportVO.builder()
                    .nameList("")
                    .numberList("")
                    .build();
        }

        // 第二步：根据订单ID列表查询order_detail表的销量top10
        List<Map<String, Object>> top10List = orderDetailMapper.selectTop10(orderIds);

        // 提取商品名称和销量
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();

        for (Map<String, Object> item : top10List) {
            nameList.add((String) item.get("name"));
            numberList.add(((Number) item.get("number")).intValue());
        }

        // 构建返回结果
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }
}