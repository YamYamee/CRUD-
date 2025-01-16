package com.example.backend.domain.Comment.service;

import com.example.backend.domain.Comment.Converter.CommentConverter;
import com.example.backend.domain.Comment.dto.CommentRequestDTO;
import com.example.backend.domain.Comment.dto.DeleteResponseDTO;
import com.example.backend.domain.Comment.entity.Comments;
import com.example.backend.domain.Comment.repository.CommentRepository;
import com.example.backend.domain.Member.service.UserService;
import com.example.backend.common.exception.handler.PostHandler;
import com.example.backend.common.response.status.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentConverter commentConverter;
    private final UserService userService;

    public Comments getComment(Long id){

        Optional<Comments> comments = commentRepository.findById(id); // 레포지토리에서 id에 해당하는 댓글 찾기

        if(comments.isEmpty()){
            throw new PostHandler(ErrorCode.POST_NOTFOUND); // 댓글을 찾지 못했다면 에러 띄우기
        }

        return comments.get(); // 찾았다면 Optional -> Comments로 자료형 바꿔주기
    }

    public Comments writeComment(CommentRequestDTO request) {

        Comments comment = commentConverter.toComments(request); // Coverter 파일을 통해서 DTO를 Comment 형식으로 바꿔주기
        return commentRepository.save(comment);

    }

    public Comments editPost(CommentRequestDTO request, Long id) {

        Comments comment = getComment(id);;

        try {
            if (Objects.equals(userService.getMemberFromToken().getId(), comment.getMember().getId())) { // 수정하기 전에, 글을 작성한 사용자의 id와 요청 보낸 사용자의 id 확인하기
                comment.setComment(request.getContent());
                commentRepository.save(comment);
            } else {
                throw new PostHandler(ErrorCode._BAD_REQUEST);
            }
        } catch (EmptyResultDataAccessException e) {
            // 예외 처리 로직
            throw new PostHandler(ErrorCode.POST_NOTFOUND); // 해당 id가 없을 경우 에러 발생시키기
        }

        return comment;
    }

    public DeleteResponseDTO deleteComment(Long id){

        long commentWriterId = getComment(id).getMember().getId();
        long currentMemberId = userService.getMemberFromToken().getId();

        try {
            if (Objects.equals(commentWriterId, currentMemberId)){
                commentRepository.deleteById(id);
            } else {
                throw new PostHandler(ErrorCode._BAD_REQUEST);
            }
        } catch (EmptyResultDataAccessException e) {
            // 예외 처리 로직
            throw new PostHandler(ErrorCode.POST_NOTFOUND); // 해당 id가 없을 경우 에러 발생시키기
        }

        return commentConverter.CreateDeleteDTO(id);
    }
}
