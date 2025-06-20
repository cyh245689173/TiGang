package com.copico.controller;

import com.copico.common.base.RestResult;
import com.copico.common.util.ThreadLocalUtil;
import com.copico.model.domain.TrainingRecord;
import com.copico.model.request.TrainingRecordRequest;
import com.copico.service.impl.TrainingRecordServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 训练记录 前端控制器
 * </p>
 *
 * @author yuhao.chen
 * @since 2025-06-16
 */
@RestController
@RequestMapping("/training")
public class TrainingRecordController {


    @Autowired
    private TrainingRecordServiceImpl trainingService;

    @PostMapping("/addRecord")
    public RestResult<TrainingRecord> recordTraining(
            @RequestBody TrainingRecordRequest request) {
        Map<String, Object> map = ThreadLocalUtil.get();
        String userIdStr = (String) map.get("userIdStr");
        Long userId = Long.parseLong(userIdStr);
        trainingService.recordTraining(userId, request);
        return RestResult.success();
    }

    @GetMapping("/stats")
    public RestResult<?> getStats() {
        Map<String, Object> map = ThreadLocalUtil.get();
        String userIdStr = (String) map.get("userIdStr");
        Long userId = Long.parseLong(userIdStr);
        return RestResult.success(trainingService.getTrainingStatistics(userId));
    }
}
