package com.example.backend.domain.Post.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PostResponseDTO {
    Long postId;
    Long totalPost;
    String postTitle;
    String postWriter;
    String imageURL;
    LocalDateTime createdAt;
}
