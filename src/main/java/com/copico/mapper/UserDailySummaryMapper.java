package com.copico.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.copico.model.domain.UserDailySummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

@Mapper
public interface UserDailySummaryMapper extends BaseMapper<UserDailySummary> {
    
    @Select("SELECT * FROM user_daily_summary WHERE user_id = #{userId} AND summary_date = #{date}")
    UserDailySummary selectByUserAndDate(@Param("userId") Long userId, @Param("date") Date date);
    

}