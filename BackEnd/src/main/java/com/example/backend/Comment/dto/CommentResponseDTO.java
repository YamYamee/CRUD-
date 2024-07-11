package com.example.backend.Comment.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CommentResponseDTO {
    Long commentId;
    String userNickName;
    String content;
    LocalDateTime createdAt;
}
