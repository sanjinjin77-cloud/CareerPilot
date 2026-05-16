package com.careerpilot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.careerpilot.entity.TargetOffer;

public interface TargetOfferService extends IService<TargetOffer> {

    TargetOffer getCurrentTargetOffer(Long userId);

    TargetOffer setTargetOffer(Long userId, Long jobId);
}
