package com.careerpilot.controller;

import com.careerpilot.common.Result;
import com.careerpilot.entity.TargetOffer;
import com.careerpilot.service.TargetOfferService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/target-offer")
public class TargetOfferController {

    private final TargetOfferService targetOfferService;

    public TargetOfferController(TargetOfferService targetOfferService) {
        this.targetOfferService = targetOfferService;
    }

    @GetMapping("/current")
    public Result<TargetOffer> current(@RequestParam(name = "userId", required = false, defaultValue = "1") Long userId) {
        return Result.success(targetOfferService.getCurrentTargetOffer(userId));
    }

    @PostMapping("/{jobId}")
    public Result<TargetOffer> setTargetOffer(@PathVariable Long jobId,
                                              @RequestParam(name = "userId", required = false, defaultValue = "1") Long userId) {
        return Result.success(targetOfferService.setTargetOffer(userId, jobId));
    }
}
