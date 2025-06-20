package com.copico.model.request;

import lombok.Data;

/**
 * @author 陈玉皓
 * @date 2025/6/16 20:56
 * @description: TODO
 */
@Data
public class TrainingRecordRequest {

    /**
     * 训练难度
     */
    private String difficulty;

    /**
     * 训练时长, 单位: min
     */
    private Integer duration;

}
