package com.example.backend.Post.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class PostRequestDTO {

    @NotEmpty(message = "제목은 반드시 입력하셔야 합니다.")
    @Column(name = "Title", columnDefinition = "TEXT")
    private String title;

    @NotEmpty(message = "내용은 반드시 입력하셔야 합니다.")
    @Column(name = "Content", columnDefinition = "TEXT")
    private String content;

    MultipartFile PostPicture;
}