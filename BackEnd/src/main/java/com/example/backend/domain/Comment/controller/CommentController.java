package com.example.backend.domain.Comment.controller;

import com.example.backend.domain.Comment.Converter.CommentConverter;
import com.example.backend.domain.Comment.dto.CommentRequestDTO;
import com.example.backend.domain.Comment.dto.CommentResponseDTO;
import com.example.backend.domain.Comment.dto.DeleteResponseDTO;
import com.example.backend.domain.Comment.entity.Comments;
import com.example.backend.domain.Comment.service.CommentService;
import com.example.backend.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class CommentController {

    private final CommentService commentService;
    private final CommentConverter commentConverter;

    @CrossOrigin
    @Operation(summary = "댓글 작성")
    @PostMapping("/comments")
    public ApiResponse<CommentResponseDTO> writePost(@Valid @RequestBody CommentRequestDTO commentRequestDTO) {
        Comments comment = commentService.writeComment(commentRequestDTO); // DTO로 데이터 받고 작성하기
        return ApiResponse.onSuccess(commentConverter.CreateCommentResponseDTO(comment));
    }

    @CrossOrigin
    @Operation(summary = "댓글 수정")
    @PutMapping("/edits/comment/{id}")
    public ApiResponse<CommentResponseDTO> editComment(@PathVariable Long id, @RequestBody CommentRequestDTO commentRequestDTO) {
        Comments comments = commentService.editPost(commentRequestDTO, id); // 수정 할 내용과 댓글 id 입력 받기
        return ApiResponse.onSuccess(commentConverter.CreateCommentResponseDTO(comments));
    }

    @CrossOrigin
    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/comment/{id}")
    public ApiResponse<DeleteResponseDTO> deletePost(@PathVariable Long id) {
        return ApiResponse.onSuccess(commentService.deleteComment(id));
    }
}
