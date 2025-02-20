package com.korea.shop.repository;

import com.korea.shop.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /*
    * @EntityGraph
    * JPA에서는 fetch = FetchType.LAZY로 가져오지만 
    * EntityGraph으로 사용하게 되면 한쿼리로 조인하여 연관 데이터까지 가져옴
    */
    @EntityGraph(attributePaths =  {"memberRoleList"}) // memberRoleList 즉시로딩함. // 객체 형식으로 가져옴
    @Query("select m from Member m where m.email = :email")
    Member getWithRoles(@Param("email") String email);

    public List<Member> findByName(String name);
}
