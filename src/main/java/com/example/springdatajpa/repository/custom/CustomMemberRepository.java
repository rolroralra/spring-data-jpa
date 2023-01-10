package com.example.springdatajpa.repository.custom;

import com.example.springdatajpa.domain.Member;
import java.util.List;

public interface CustomMemberRepository {
    List<Member> findMemberByNameAndGreaterThan(String name, Integer age);

    List<Member> findByPage(int age, int offset, int limit);

    long countByAge(int age);

    int bulkAgePlus(int age);
}
