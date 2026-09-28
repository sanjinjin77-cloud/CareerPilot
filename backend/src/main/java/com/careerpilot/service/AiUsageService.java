package com.careerpilot.service;

import java.util.Optional;
import java.util.function.Supplier;

public interface AiUsageService {

    <T> AiExecution<T> execute(Long userId,
                                String feature,
                                boolean available,
                                String model,
                                Supplier<Optional<T>> llmCall,
                                Supplier<T> fallback);

    record AiExecution<T>(T value, String source, String model) {
    }
}
