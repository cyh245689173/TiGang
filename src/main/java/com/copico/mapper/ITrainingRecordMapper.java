package com.copico.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.copico.model.domain.TrainingRecord;
import org.apache.ibatis.annotations.Mapper;

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

}
