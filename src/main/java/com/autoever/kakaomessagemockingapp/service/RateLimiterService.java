package com.autoever.kakaomessagemockingapp.service;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.autoever.kakaomessagemockingapp.constants.RateLimitConstants.LIMIT_FOR_PERIOD;
import static com.autoever.kakaomessagemockingapp.constants.RateLimitConstants.LIMIT_REFRESH_PERIOD;

@Slf4j
@Service
public class RateLimiterService {

    private final RateLimiterRegistry rateLimiterRegistry;

    public RateLimiterService() {
        // 기본 설정을 사용하여 RateLimiterRegistry 초기화
        this.rateLimiterRegistry = RateLimiterRegistry.ofDefaults();
    }

    public RateLimiter getRateLimiterForToken(String apiKey) {
        // 토큰별로 RateLimiter 생성 또는 가져오기
        return rateLimiterRegistry.rateLimiter(apiKey, RateLimiterConfig.custom()
                .limitForPeriod(LIMIT_FOR_PERIOD)      // 1분당 100회 호출 제한
                .limitRefreshPeriod(Duration.ofMinutes(LIMIT_REFRESH_PERIOD))
                .timeoutDuration(Duration.ZERO)        // 대기 시간 없음
                .build()
        );
    }
}