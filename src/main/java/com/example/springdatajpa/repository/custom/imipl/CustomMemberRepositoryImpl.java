package com.example.springdatajpa.repository.custom.imipl;

import com.example.springdatajpa.controller.dto.MemberDto;
import com.example.springdatajpa.domain.Member;
import com.example.springdatajpa.repository.custom.CustomMemberRepository;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;

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

    @Override
    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.name desc", Member.class)
            .setParameter("age", age)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();
    }

    @Override
    public long countByAge(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
            .setParameter("age", age)
            .getSingleResult();
    }

    @Override
    public int bulkAgePlus(int age) {
        return em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
            .setParameter("age", age)
            .executeUpdate();
    }

    @Override
    @SuppressWarnings({"unchecked", "deprecation"})
    public List<MemberDto> findNativeQueryByHibernate(int offset, int size) {
        String nativeQuery = "SELECT m.name as name from MEMBER m";

        return em.createNativeQuery(nativeQuery)
            .setFirstResult(offset)
            .setMaxResults(size)
            .unwrap(NativeQuery.class)
            .addScalar("name")
            .setResultTransformer(Transformers.aliasToBean(MemberDto.class))
            .getResultList();
    }
}
