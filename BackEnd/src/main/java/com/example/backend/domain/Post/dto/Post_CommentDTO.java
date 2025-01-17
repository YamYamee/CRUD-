package com.example.backend.domain.Post.dto;

import com.example.backend.domain.Comment.dto.CommentResponseDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class Post_CommentDTO {
    String postTitle;
    String postWriter;
    String postContent;
    String imageURL;
    LocalDateTime createdAt;
    List<CommentResponseDTO> comments;
}
