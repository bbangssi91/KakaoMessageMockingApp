package com.autoever.kakaomessagemockingapp.api;

import com.autoever.kakaomessagemockingapp.dto.MessageRequestDto;
import com.autoever.kakaomessagemockingapp.dto.MessageResponseDto;
import com.autoever.kakaomessagemockingapp.service.RateLimiterService;
import io.github.resilience4j.ratelimiter.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@RestController
public class KakaoMessageApi {

    private final RateLimiterService rateLimiterService;

    @PostMapping(value = "/kakaotalk-messages", produces = "application/json", consumes = "application/json")
    public ResponseEntity<MessageResponseDto> sendKakaoMessage(
            @RequestHeader(value = "API_KEY") String apiKey,
            @RequestBody MessageRequestDto request
    ) {
        log.info("KakaoTalk Message Received: {}", request);

        if(apiKey == null || apiKey.isEmpty()) {
            log.error("API Key is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDto("99"));
        }

        // 토큰별로 RateLimiter 가져오기
        RateLimiter rateLimiter = rateLimiterService.getRateLimiterForToken(apiKey);

        // RateLimiter 실행
        Supplier<ResponseEntity<MessageResponseDto>> decoratedSupplier = RateLimiter
                .decorateSupplier(rateLimiter, () -> ResponseEntity.ok(new MessageResponseDto("00")));

        try {
            // RateLimiter를 통과한 경우 메시지를 보냄
            return decoratedSupplier.get();
        } catch (Exception e) {
            log.warn("Rate limit exceeded for token: {}", apiKey);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new MessageResponseDto("99"));
        }
    }
}