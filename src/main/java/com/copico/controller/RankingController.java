package com.copico.controller;

import com.copico.common.base.RestResult;
import com.copico.model.vo.RankingItemVO;
import com.copico.service.impl.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ranking")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @Operation(description = "总时长排行榜")
    @GetMapping("/total")
    public RestResult<List<RankingItemVO>> getTotalRanking() {
        return RestResult.success(rankingService.getTotalRanking());
    }

    @Operation(description = "周排行榜")
    @GetMapping("/weekly")
    public RestResult<List<RankingItemVO>> getWeeklyRanking() {
        return RestResult.success(rankingService.getWeeklyRanking());
    }

    @Operation(description = "日排行榜")
    @GetMapping("/daily")
    public RestResult<List<RankingItemVO>> getDailyRanking() {
        return RestResult.success(rankingService.getDailyRanking());
    }
}