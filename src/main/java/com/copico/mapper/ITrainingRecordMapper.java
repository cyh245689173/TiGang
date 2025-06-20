package com.copico.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.copico.model.domain.TrainingRecord;
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


}
