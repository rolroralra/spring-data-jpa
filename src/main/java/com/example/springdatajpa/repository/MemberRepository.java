package com.example.springdatajpa.repository;

import com.example.springdatajpa.controller.dto.MemberDto;
import com.example.springdatajpa.domain.Member;
import com.example.springdatajpa.repository.custom.CustomMemberRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {
    List<Member> findByNameAndAgeGreaterThan(String name, Integer age);

//    @Query(name = "Member.findCustomMemberByName")    // 생략해도 동작
    List<Member> findCustomMemberByName(@Param("name") String name);

    @Query(value = "select m from Member m where m.age = :age and m.name = :name")
    List<Member> findUser(@Param("name") String name, @Param("age") Integer age);

    @Query("select m.name from Member m")
    List<String> findUsernameList();

    @Query("select new com.example.springdatajpa.controller.dto.MemberDto(m.id, m.name, m.age, t.name)"
        + " from Member m join m.team t")
    List<MemberDto> findMemberDtos();

    @Query("select m from Member m where m.name like concat('%', concat(:name, '%'))")
    List<Member> findMembers(@Param("name") String name);

    @Query("select m from Member m where m.name in :names")
    List<Member> findByNames(@Param("names") List<String> names);
}
