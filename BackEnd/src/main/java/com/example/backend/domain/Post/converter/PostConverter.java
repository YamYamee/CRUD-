package com.example.backend.domain.Post.converter;

import com.example.backend.domain.Comment.dto.CommentResponseDTO;
import com.example.backend.domain.Member.service.UserService;
import com.example.backend.domain.Post.dto.DeleteResponseDTO;
import com.example.backend.domain.Post.dto.PostRequestDTO;
import com.example.backend.domain.Post.dto.PostResponseDTO;
import com.example.backend.domain.Post.dto.Post_CommentDTO;
import com.example.backend.domain.Post.entity.Posts;
import com.example.backend.domain.Post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostConverter { // 로그인 안 한 경우에 대한 예외 처리 추가 =

    LocalDateTime now = LocalDateTime.now();
    private final UserService userService;
    private final PostRepository postRepository;

    public Posts toPosts(PostRequestDTO request){

        return Posts.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .Likes(0)
                .member(userService.getMemberFromToken()).build();
    }

    public PostResponseDTO CreatePostResponseDTO(Posts post){
        return PostResponseDTO.builder()
                .postId(post.getId())
                .postTitle(post.getTitle())
                .postWriter(post.getMember().getNickname())
                .createdAt(post.getCreatedAt())
                .imageURL(post.getPictureURL())
                .totalPost(postRepository.count())
                .build();
    }

    public DeleteResponseDTO CreateDeleteDTO(Long id){

        return DeleteResponseDTO.builder()
                .postId(id)
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public Post_CommentDTO CreatePost_Comment(Posts post, List<CommentResponseDTO> post_comments){

        return Post_CommentDTO.builder()
                .postContent(post.getContent())
                .postTitle(post.getTitle())
                .postWriter(post.getMember().getNickname())
                .comments(post_comments)
                .createdAt(post.getCreatedAt())
                .imageURL(post.getPictureURL())
                .build();
    }
}
