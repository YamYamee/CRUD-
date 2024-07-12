package com.example.backend.Member.service;

import com.example.backend.Member.dto.MemberLoginRequestDTO;
import com.example.backend.Member.dto.UserResponseDTO;
import com.example.backend.Member.dto.UserSignUpRequestDto;
import com.example.backend.Member.entity.Authority;
import com.example.backend.Member.entity.Member;
import com.example.backend.common.exception.handler.UserHandler;
import com.example.backend.common.jwt.JwtTokenProvider;
import com.example.backend.Member.repository.MemberRepository;

import java.util.Collections;

import com.example.backend.common.jwt.SecurityUtil;
import com.example.backend.common.jwt.TokenInfo;
import com.example.backend.common.response.ApiResponse;
import com.example.backend.common.response.status.ErrorCode;
import com.example.backend.common.response.status.SuccessCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public ApiResponse<String> signUp(UserSignUpRequestDto signUp) {

        if (memberRepository.existsByEmail(signUp.getEmail())) {
            throw new UserHandler(ErrorCode.EMAIL_ALREADY_EXIST);
        }

        Member user = Member.builder()
                    .socialId(signUp.getSocialId())
                    .email(signUp.getEmail())
                    .password(passwordEncoder.encode(signUp.getPassword()))
                    .nickname(signUp.getNickname())
                    .roles(Collections.singletonList(Authority.ROLE_USER.name()))
                    .build();
        memberRepository.save(user);

        return ApiResponse.of(SuccessCode._SIGNUP_SUCCESS, "회원가입 성공!");
    }

    public ApiResponse<UserResponseDTO> login(MemberLoginRequestDTO memberLoginRequestDTO) {


        if (!memberRepository.findByEmail(memberLoginRequestDTO.getEmail()).isPresent()) {
            log.warn("회원 정보를 찾을 수 없음: {}", memberLoginRequestDTO.getEmail());
            throw new UserHandler(ErrorCode.MEMBER_NOT_FOUND);
        }
        try {
            UsernamePasswordAuthenticationToken authenticationToken = memberLoginRequestDTO.toAuthentication();

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

            Member member = memberRepository.findByEmail(memberLoginRequestDTO.getEmail()).orElseThrow();
            UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                    .tokenInfo(tokenInfo)
                    .nickName(member.getNickname())
                    .build();

            return ApiResponse.of(SuccessCode._LOGIN_SUCCESS, userResponseDTO);

        } catch (AuthenticationException e) {
            throw new UserHandler(ErrorCode.MEMBER_LOGIN_FAILURE);
        }
    }

    public ApiResponse<UserResponseDTO> socialLogin(Member member) {

        Authentication authentication = new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());

        System.out.println(authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        System.out.println(tokenInfo);

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .tokenInfo(tokenInfo)
                .nickName(member.getNickname())
                .build();

        return ApiResponse.of(SuccessCode._LOGIN_SUCCESS, userResponseDTO);
    }



    /*
    public ResponseEntity<?> reissue(UserRequestDto.Reissue reissue) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(reissue.getRefreshToken())) {
            return response.fail("Refresh Token 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 2. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());

        // 3. Redis 에서 User email 을 기반으로 저장된 Refresh Token 값을 가져옵니다.
        String refreshToken = (String)redisTemplate.opsForValue().get("RT:" + authentication.getName());
        // (추가) 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
        if(ObjectUtils.isEmpty(refreshToken)) {
            return response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        }
        if(!refreshToken.equals(reissue.getRefreshToken())) {
            return response.fail("Refresh Token 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 4. 새로운 토큰 생성
        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        // 5. RefreshToken Redis 업데이트
        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return response.success(tokenInfo, "Token 정보가 갱신되었습니다.", HttpStatus.OK);
    }

     */


    /*
    public ResponseEntity<?> authority() {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No authentication information."));
        // add ROLE_ADMIN
        member.getRoles().add(Authority.ROLE_ADMIN.name());
        memberRepository.save(member);

        return response.success();
    }
     */

    public Member getMemberFromToken() {

        String userEmail = SecurityUtil.getCurrentUserEmail();

        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserHandler(ErrorCode._UNAUTHORIZED));

        return member;
    }

    public Member findByEmail(String email) {
        Member member =
                memberRepository
                        .findByEmail(email)
                        .orElseThrow();
        return member;
    }

}
