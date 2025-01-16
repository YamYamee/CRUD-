package com.example.backend.domain.Member.controller;

import com.example.backend.domain.Member.dto.MemberLoginRequestDTO;
import com.example.backend.domain.Member.dto.UserResponseDTO;
import com.example.backend.domain.Member.dto.UserSignUpRequestDto;
import com.example.backend.domain.Member.entity.Member;
import com.example.backend.domain.Member.repository.MemberRepository;
import com.example.backend.domain.Member.service.UserService;
import com.example.backend.common.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class MemberController {

    private final UserService usersService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @CrossOrigin
    @Operation(summary = "회원가입 API!")
    @PostMapping("/sign-up")
    public ApiResponse<String> signUp(@Valid @RequestBody UserSignUpRequestDto userSignUpRequestDto) {
        return usersService.signUp(userSignUpRequestDto);
    }

    @CrossOrigin
    @Operation(summary = "로그인 API")
    @PostMapping("/login")
    public ApiResponse<UserResponseDTO> login(@Valid @RequestBody MemberLoginRequestDTO memberLoginRequestDTO) {
        return usersService.login(memberLoginRequestDTO);
    }

    @CrossOrigin
    @Operation(summary = "로그인 API")
    @PostMapping("/oauth/kakao")
    public ApiResponse<UserResponseDTO> socialLogin(@RequestBody HashMap<String, String> requestBody) throws JsonProcessingException {

        ApiResponse<UserResponseDTO> Response = null;
        String accessToken = requestBody.get("access_token"); // 프론트로부터 accessToken을 받는다.

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        String url = "https://kapi.kakao.com/v2/user/me";

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );

        // ResponseEntity에서 JSON 파싱하여 id, nickname, (+이메일) 추출
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        long id = rootNode.path("id").asLong();
        String nickName = rootNode.path("nickname").asText();

        if (memberRepository.findBysocialId(id).isEmpty()) {

            String tempEmail = UUID.randomUUID().toString();
            String tempPassword = UUID.randomUUID().toString();

            UserSignUpRequestDto signUp = UserSignUpRequestDto.builder()
                    .socialId(id)
                    .email(tempEmail + "@kakao.com")
                    .nickname(nickName)
                    .password(tempPassword)
                    .build();

            usersService.signUp(signUp);
        }

        Member member = memberRepository.findBysocialId(id).orElse(null);

        return usersService.socialLogin(member); // DTO 만들고 함수 호출
    }
}
