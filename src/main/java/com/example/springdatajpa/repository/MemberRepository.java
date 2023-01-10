package com.example.springdatajpa.repository;

import com.example.springdatajpa.controller.dto.MemberDto;
import com.example.springdatajpa.domain.Member;
import com.example.springdatajpa.repository.custom.CustomMemberRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {

    List<Member> findByNameAndAgeGreaterThan(String name, Integer age);

    @Query(name = "Member.findCustomMemberByName")
        // 생략해도 동작
    List<Member> findCustomMemberByName(@Param("name") String name);

    @Query(value = "select m from Member m where m.age = :age and m.name = :name")
    List<Member> findUser(@Param("name") String name, @Param("age") Integer age);

    @Query("select m.name from Member m")
    List<String> findUsernameList();

    @Query(
        "select new com.example.springdatajpa.controller.dto.MemberDto(m.id, m.name, m.age, t.name)"
            + " from Member m join m.team t")
    List<MemberDto> findMemberDtos();

    @Query("select m from Member m where m.name like concat('%', concat(:name, '%'))")
    List<Member> findMembers(@Param("name") String name);

    @Query("select m from Member m where m.name in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    @EntityGraph()
    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findMemberByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlusOne(@Param("age") int age);

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findByName(String name);

    @EntityGraph(value = "Member.all")
    @Query("select m from Member m")
    List<Member> findMemberByNamedEntityGraph();
}
