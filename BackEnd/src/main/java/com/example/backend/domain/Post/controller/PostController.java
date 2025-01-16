package com.example.backend.domain.Post.controller;


import com.example.backend.common.response.ApiResponse;
import com.example.backend.domain.Post.converter.PostConverter;
import com.example.backend.domain.Post.dto.DeleteResponseDTO;
import com.example.backend.domain.Post.dto.PostRequestDTO;
import com.example.backend.domain.Post.dto.PostResponseDTO;
import com.example.backend.domain.Post.dto.Post_CommentDTO;
import com.example.backend.domain.Post.entity.Posts;
import com.example.backend.domain.Post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class PostController {

    private final PostService postService;
    private final PostConverter postConverter;

    @CrossOrigin
    @Operation(summary = "글 목록 보여주기")
    @GetMapping("/posts/home/{page}")
    public ApiResponse<List<PostResponseDTO>> getPosts(@PathVariable Integer page) {

        List<PostResponseDTO> posts = postService.getPostByPages(page); // 게시글을 DTO 형태로 가져옴. Paging 적용함.

        return ApiResponse.onSuccess(posts);
    }

    @CrossOrigin
    @Operation(summary = "특정 글 보여주기 + 댓글 보여주기")
    @GetMapping("/posts/{id}")
    public ApiResponse<Post_CommentDTO> getDetailPage(@PathVariable Long id) {

        Post_CommentDTO post_comment = postService.getPostComment(id); // 게시글과 댓글을 가져와서 DTO 형태로 프론트에 보내줌.

        return ApiResponse.onSuccess(post_comment);
    }

    @CrossOrigin
    @Operation(summary = "글 작성")
    @PostMapping("/posts")
    public ApiResponse<PostResponseDTO> writePost(@Valid @ModelAttribute PostRequestDTO postRequestDTO) {

        Posts post = postService.writePost(postRequestDTO); //Multifile 때문에 @ModelAttribute 형태로 DTO를 받는다.

        return ApiResponse.onSuccess(postConverter.CreatePostResponseDTO(post));
    }

    @CrossOrigin
    @Operation(summary = "글 삭제")
    @DeleteMapping("/posts/{id}")
    public ApiResponse<DeleteResponseDTO> deletePost(@PathVariable Long id) {
        return ApiResponse.onSuccess(postService.deletePost(id));
    }

    /*
    @CrossOrigin
    @Operation(summary = "좋아요 누르기")
    @DeleteMapping("/posts/likes/{id}")
    public ApiResponse<Posts> likePost(@PathVariable Long id) {
        return ApiResponse.onSuccess(postService.likePost(id));
    }*/

}
