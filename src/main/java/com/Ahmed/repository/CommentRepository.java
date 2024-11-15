package com.Ahmed.repository;

import com.Ahmed.entity.Comment;
import com.Ahmed.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post, Pageable pageable);
}
