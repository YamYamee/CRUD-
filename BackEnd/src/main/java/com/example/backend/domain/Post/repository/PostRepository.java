package com.example.backend.domain.Post.repository;

import com.example.backend.domain.Post.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostRepository extends JpaRepository<Posts, Long> {

    Page<Posts> findPageBy(Pageable page);

}
