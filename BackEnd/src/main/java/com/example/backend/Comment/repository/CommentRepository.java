package com.example.backend.Comment.repository;

import com.example.backend.Comment.entity.Comments;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comments, Long> {

    @Query("SELECT c FROM Comments c WHERE c.parent_id = :postId")
    List<Comments> findCommentsByPostId(@Param("postId") Long postId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comments c WHERE c.parent_id = :postId")
    void deleteByParentId(@Param("postId") Long postId);
}
