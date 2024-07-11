package com.example.backend.Post.service;

import com.example.backend.Comment.Converter.CommentConverter;
import com.example.backend.Comment.dto.CommentResponseDTO;
import com.example.backend.Comment.entity.Comments;
import com.example.backend.Comment.repository.CommentRepository;
import com.example.backend.Member.service.UserService;
import com.example.backend.common.aws.s3.AmazonS3Manager;
import com.example.backend.common.aws.s3.Uuid;
import com.example.backend.common.aws.s3.UuidRepository;
import com.example.backend.common.exception.handler.PostHandler;
import com.example.backend.common.response.status.ErrorCode;
import com.example.backend.Post.dto.DeleteResponseDTO;
import com.example.backend.Post.dto.PostRequestDTO;
import com.example.backend.Post.dto.PostResponseDTO;
import com.example.backend.Post.dto.Post_CommentDTO;
import com.example.backend.Post.entity.Posts;
import com.example.backend.Post.converter.PostConverter;
import com.example.backend.Post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostConverter postConverter;
    private final CommentConverter commentConverter;
    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;

    public Posts getPost(Long id) { // 게시글 하나만 찾는 함수


        Optional<Posts> posts = postRepository.findById(id); // 게시글을 찾는데, 없다면 오류를 발생시킨다.

        if(posts.isEmpty()){
            throw new PostHandler(ErrorCode.POST_NOTFOUND);
        }

        return posts.get();
    }

    public List<PostResponseDTO> getPostByPages(Integer page) { // paging을 위해서 사용하는 함수임.

        Page<Posts> posts = postRepository.findPageBy(PageRequest.of(page-1, 5));

        if(posts.isEmpty()){
            return null;
        }

        return posts.getContent().stream().map( // 가져온 게시물을 stream함수를 활용해서 DTO로 변환후 프론트로 전달한다.
                postConverter::CreatePostResponseDTO
        ).toList();

    }

    public Post_CommentDTO getPostComment(Long id){ // 게시글과 댓글을 모두 갖고 오는 함수

        Posts post = getPost(id);

        List<Comments> post_comments = commentRepository.findCommentsByPostId(id);

        List<CommentResponseDTO> comments = post_comments.stream().map( // 댓글은 댓글 DTO로 변환한다.
                commentConverter::CreateCommentResponseDTO
        ).collect(Collectors.toList());

        return postConverter.CreatePost_Comment(post, comments); // 게시글과 댓글 목록을 합쳐서 DTO로 만든 후 프론트로 전달함.
    }

    public Posts writePost(PostRequestDTO request){ // 게시글 작성하는 함수

        Posts post = postConverter.toPosts(request);

        String uuid = UUID.randomUUID().toString();

        Uuid savedUuid = uuidRepository.save(
                Uuid.builder()
                .uuid(uuid).build());

        String pictureUrl;

        if(request.getPostPicture() == null){
            pictureUrl = null;
        } else {
            pictureUrl = s3Manager.uploadFile(s3Manager.generateReviewKeyName(savedUuid), request.getPostPicture());
        }

        post.setPictureURL(pictureUrl);

        return postRepository.save(post);
    }


    public DeleteResponseDTO deletePost(Long id){

        long postWriterId = getPost(id).getMember().getId();
        long currentMemberId = userService.getMemberFromToken().getId();

        try {
            if(Objects.equals(postWriterId, currentMemberId)) { // 현재 로그인한 사용자와 게시글을 작성한 사용자와 비교한다.
                postRepository.deleteById(id);
                commentRepository.deleteByParentId(id);
            } else {
                throw new PostHandler(ErrorCode._BAD_REQUEST); // 같지 않다면 에러 띄움
            }
        } catch (EmptyResultDataAccessException e) {
            // 예외 처리 로직
            throw new PostHandler(ErrorCode.POST_NOTFOUND); // 해당 id를 가진 게시물이 없을 경우 에러 발생시킴
        }
        return postConverter.CreateDeleteDTO(id);
    }

    /* public Posts likePost(Long postId) {
        Optional<Posts> post = postRepository.findById(postId);
        Long userId = userService.getMemberFromToken().getId();

        if (post.isPresent()) {
            Posts existingPost = post.get();
            Integer likes = existingPost.getLikes();
            List<Long> likes_list = existingPost.getLikesList();

            if (!likes_list.contains(userId)) {
                existingPost.setLikes(likes + 1);
                likes_list.add(userId);
                postRepository.save(existingPost); // 이렇게 해도 되나?

                return existingPost;
            }
        }

        return null;
    } */
}
