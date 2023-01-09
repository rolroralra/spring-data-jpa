package com.example.springdatajpa.repository.custom.imipl;

import com.example.springdatajpa.domain.Member;
import com.example.springdatajpa.repository.custom.CustomMemberRepository;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository {
    private final EntityManager em;

    @Override
    public List<Member> findMemberByNameAndGreaterThan(String name, Integer age) {
        return em.createQuery("select m from Member m where m.age > :age and m.name = :name", Member.class)
            .setParameter("name", name)
            .setParameter("age", age)
            .getResultList();
    }
}
