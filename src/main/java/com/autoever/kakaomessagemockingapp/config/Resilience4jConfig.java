package com.autoever.kakaomessagemockingapp.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static com.autoever.kakaomessagemockingapp.constants.RateLimitConstants.LIMIT_FOR_PERIOD;
import static com.autoever.kakaomessagemockingapp.constants.RateLimitConstants.LIMIT_REFRESH_PERIOD;

@Configuration
public class Resilience4jConfig {
    @Bean
    public RateLimiter kakaoApiRateLimiter() {
        // 1분 (60초) 동안 100개의 요청만 허용
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(LIMIT_FOR_PERIOD)                   // 1분당 최대 100회
                .limitRefreshPeriod(Duration.ofMinutes(LIMIT_REFRESH_PERIOD)) // 갱신 시간
                .timeoutDuration(Duration.ofMillis(0))  // 대기 시간 없음
                .build();

        return RateLimiter.of("kakaoApiLimiter", config);
    }
}
