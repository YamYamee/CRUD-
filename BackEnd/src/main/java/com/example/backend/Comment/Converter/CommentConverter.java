package com.example.backend.Comment.Converter;

import com.example.backend.Comment.dto.CommentRequestDTO;
import com.example.backend.Comment.dto.CommentResponseDTO;
import com.example.backend.Comment.dto.DeleteResponseDTO;
import com.example.backend.Comment.entity.Comments;
import com.example.backend.Member.service.UserService;
import com.example.backend.common.jwt.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommentConverter { // 로그인 안 한 경우에 대한 예외 처리 추가 =

    private final UserService userService;

    public Comments toComments(CommentRequestDTO request){

        return Comments.builder()
                .comment(request.getContent())
                .parent_id(request.getPostId())
                .member(userService.getMemberFromToken()).build(); // 받은 토큰을 통해서 글을 작성한 사용자 저장하기


    }

    public CommentResponseDTO CreateCommentResponseDTO(Comments comment){
        return CommentResponseDTO.builder()
                .commentId(comment.getId())
                .content(comment.getComment())
                .userNickName(comment.getMember().getNickname())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public DeleteResponseDTO CreateDeleteDTO(Long id){

        return DeleteResponseDTO.builder()
                .commentId(id)
                .deletedAt(LocalDateTime.now())
                .build();
    }
}