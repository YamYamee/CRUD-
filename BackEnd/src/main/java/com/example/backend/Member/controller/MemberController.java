package com.example.backend.Member.controller;

import com.example.backend.Member.dto.MemberLoginRequestDTO;
import com.example.backend.Member.dto.UserResponseDTO;
import com.example.backend.Member.dto.UserSignUpRequestDto;
import com.example.backend.Member.entity.Authority;
import com.example.backend.Member.entity.Member;
import com.example.backend.Member.entity.SocialType;
import com.example.backend.Member.repository.MemberRepository;
import com.example.backend.Member.service.UserService;
import com.example.backend.common.response.ApiResponse;
import com.example.backend.common.response.status.SuccessCode;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class MemberController {

    private final UserService usersService;
    private final MemberRepository memberRepository;

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
    public ApiResponse<String> login(@RequestBody HashMap<String, String> requestBody) throws JsonProcessingException {

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

        // ResponseEntity에서 JSON 파싱하여 id 추출
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        long id = rootNode.path("id").asLong();
        String nickName = rootNode.path("nickname").toString();

        if (memberRepository.findById(id).isEmpty()){
            Member member = Member.builder()
                    .id(id)
                    .email("temp@naver.com") // 원래는 이메일도 카카오 서버로부터 가져와야 하는데, 이게 비즈니스 앱을 신청 해야만 해서, 임시 이메일을 저장.
                    .nickname(nickName)
                    .socialType(SocialType.KAKAO)
                    .roles(Collections.singletonList(Authority.ROLE_USER.name()))
                    .build();

            memberRepository.save(member);
        }

        return ApiResponse.of(SuccessCode._SIGNUP_SUCCESS, "회원가입 성공!");
    }

}