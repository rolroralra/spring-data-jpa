package com.example.springdatajpa.repository.custom;

import com.example.springdatajpa.domain.Member;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface CustomMemberRepository {
    List<Member> findMemberByNameAndGreaterThan(String name, Integer age);
}
