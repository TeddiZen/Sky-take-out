package com.sky.mapper;

import com.sky.entity.User;
import com.sky.vo.UserReportVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     * @param openid 微信openid
     * @return 用户
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入用户
     * @param user 用户
     */
    void insert(User user);

    /**
     * 根据用户ID查询用户
     * @param id 用户ID
     * @return 用户
     */
    @Select("select * from user where id = #{id}")
    User getById(Long id);

    /**
     * 查询用户统计接口
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 用户统计接口数据
     */
    List<Map<String, Object>> listUserDailyNew(LocalDateTime beginTime, LocalDateTime endTime);


    /**
     * 查询用户在beginTime前的总量
     * @param beginTime 开始时间
     * @return 用户总量
     */
    @Select("select count(*) from user where create_time < #{beginTime}")
    int selectCountByBegin(LocalDateTime beginTime);
}
