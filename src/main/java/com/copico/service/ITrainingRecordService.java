package com.copico.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.copico.model.domain.TrainingRecord;
import com.copico.model.request.TrainingRecordRequest;

/**
 * <p>
 * 训练记录 服务类
 * </p>
 *
 * @author yuhao.chen
 * @since 2025-06-16
 */
public interface ITrainingRecordService extends IService<TrainingRecord> {

    TrainingRecord recordTraining(Long userId, TrainingRecordRequest request);

    Object getDailyRanking();

}
