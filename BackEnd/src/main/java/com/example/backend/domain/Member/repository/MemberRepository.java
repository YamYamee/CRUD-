package com.example.backend.domain.Member.repository;
import com.example.backend.domain.Member.entity.Member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    Member findByNickname(String nickname);

    Optional<Member> findBysocialId(Long socialId);

}
