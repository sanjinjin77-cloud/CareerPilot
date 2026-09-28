package com.careerpilot.controller;

import com.careerpilot.common.Result;
import com.careerpilot.entity.TargetOffer;
import com.careerpilot.service.AuthSessionService;
import com.careerpilot.service.TargetOfferService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/target-offer")
public class TargetOfferController {

    private final TargetOfferService targetOfferService;
    private final AuthSessionService authSessionService;

    public TargetOfferController(TargetOfferService targetOfferService, AuthSessionService authSessionService) {
        this.targetOfferService = targetOfferService;
        this.authSessionService = authSessionService;
    }

    @GetMapping("/current")
    public Result<TargetOffer> current(HttpServletRequest request) {
        return Result.success(targetOfferService.getCurrentTargetOffer(authSessionService.currentUserId(request)));
    }

    @PostMapping("/{jobId}")
    public Result<TargetOffer> setTargetOffer(@PathVariable Long jobId, HttpServletRequest request) {
        return Result.success(targetOfferService.setTargetOffer(authSessionService.currentUserId(request), jobId));
    }
}
