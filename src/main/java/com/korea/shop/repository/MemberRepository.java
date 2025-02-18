package com.korea.shop.repository;

import com.korea.shop.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @EntityGraph(attributePaths =  {"memberRoleList"}) // memberRoleList 즉시로딩함.
    @Query("select m from Member m where m.email = :email")
    Member getWithRoles(@Param("email") String email);

    public List<Member> findByName(String name);
}
