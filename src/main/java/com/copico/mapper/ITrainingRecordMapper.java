package com.copico.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.copico.model.domain.TrainingRecord;
import com.copico.model.vo.RankingItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 训练记录 Mapper 接口
 * </p>
 *
 * @author yuhao.chen
 * @since 2025-06-16
 */
@Mapper
public interface ITrainingRecordMapper extends BaseMapper<TrainingRecord> {

    // 查询用户所有训练日期（去重）
    @Select("SELECT DISTINCT training_date FROM training_record WHERE user_id = #{userId}")
    List<LocalDate> selectDistinctTrainingDates(@Param("userId") Long userId);

    // 查询用户今日训练次数
    @Select("SELECT COUNT(*) FROM training_record " +
            "WHERE user_id = #{userId} AND training_date = #{today}")
    Integer countTodayTrainings(@Param("userId") Long userId,
                                @Param("today") LocalDate today);

    // 查询用户总训练次数
    @Select("SELECT COUNT(*) FROM training_record WHERE user_id = #{userId}")
    Integer countTotalTrainings(@Param("userId") Long userId);


    // 总时长排行榜
    @Select("SELECT u.id AS userId, u.user_name, u.avatar_url, " +
            "SUM(r.duration) AS totalDuration, " +
            "SUM(r.calorie) AS totalCalorie " +
            "FROM training_record r " +
            "JOIN user u ON r.user_id = u.id " +
            "GROUP BY r.user_id " +
            "ORDER BY totalDuration DESC")
    List<RankingItemVO> selectTotalRanking();

    // 周排行榜
    @Select("SELECT u.id AS userId, u.user_name, u.avatar_url, " +
            "SUM(r.duration) AS totalDuration, " +
            "SUM(r.calorie) AS totalCalorie " +
            "FROM training_record r " +
            "JOIN user u ON r.user_id = u.id " +
            "WHERE r.training_date BETWEEN #{start} AND #{end} " +
            "GROUP BY r.user_id " +
            "ORDER BY totalDuration DESC")
    List<RankingItemVO> selectWeeklyRanking(@Param("start") LocalDate start,
                                            @Param("end") LocalDate end);

    // 日排行榜
    @Select("SELECT u.id AS userId, u.user_name, u.avatar_url, " +
            "SUM(r.duration) AS totalDuration, " +
            "SUM(r.calorie) AS totalCalorie " +
            "FROM training_record r " +
            "JOIN user u ON r.user_id = u.id " +
            "WHERE r.training_date = #{date} " +
            "GROUP BY r.user_id " +
            "ORDER BY totalDuration DESC")
    List<RankingItemVO> selectDailyRanking(@Param("date") LocalDate date);


}
