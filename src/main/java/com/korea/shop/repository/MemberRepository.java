package com.korea.shop.repository;

import com.korea.shop.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /*
    * @EntityGraph
    * JPA에서는 fetch = FetchType.LAZY로 가져오지만 
    * EntityGraph으로 사용하게 되면 한쿼리로 조인하여 연관 데이터까지 가져옴
    */
    @EntityGraph(attributePaths =  {"memberRoleList"}) // memberRoleList 즉시로딩함. // 객체 형식으로 가져옴
    @Query("select m from Member m where m.email = :email")
    Optional<Member> getWithRoles(@Param("email") String email);

    public List<Member> findByName(String name);

    /*
    * 리포지토리 @Query 메서드
    *  JPQL findByName() -- 리포지토리에 메서드 생성해 준게 없음
    *  왜?
    * JPA가 필드명을 보고 알아서 판단해서 조회할 수 있도록 해줌.
    * findById() - id명을 기준으로 검색하면 되겠네? - 그래서 따로 메서드 생성 meberRepository.findByName("앨리스")
    * 멤버 테이블에서 앨리스 이름으로 검색해와 name을 기준으로 검색해줌
    * = name 필드에 매칭되는 값을 알아서 조회해음 이걸 안해줘도 된다는 얘기임.
    * @Query("select m from Member m where m.name = :name")
    * public Member findByName(@Param("name") String name);
    * 응용) meberRepository.findByNamdAndge(name, age); (이름과 나이로 검색) meberRepository.findByNameOrAge(name, age); (이름 또는 나이로 검색
    * */


}
