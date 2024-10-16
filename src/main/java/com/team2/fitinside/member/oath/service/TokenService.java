package com.team2.fitinside.member.oath.service;


import com.team2.fitinside.global.exception.CustomException;
import com.team2.fitinside.global.exception.ErrorCode;
import com.team2.fitinside.member.entity.Member;
import com.team2.fitinside.member.jwt.TokenProvider;
import com.team2.fitinside.member.service.MemberService;
import com.team2.fitinside.member.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;


    public String createNewAccessToken(String refreshToken) {
        // 토큰 유효성 검사에 실패하면 예외 발생
        if(!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }
        // 리프레시 토큰에서 사용자 정보 추출
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);

        // 새로운 JWT 액세스 토큰 생성
        return tokenProvider.generateAccessToken(authentication);

    }
}
