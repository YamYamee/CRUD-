package com.example.backend.common.jwt;


import com.example.backend.Member.entity.Member;
import com.example.backend.Member.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    public static String getCurrentUserEmail() {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("No authentication information.");
        }

        return authentication.getName();
    }
}
