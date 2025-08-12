package org.jeayoung.template.planetbackend.repository;

import java.util.Optional;
import org.jeayoung.template.planetbackend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);
}
