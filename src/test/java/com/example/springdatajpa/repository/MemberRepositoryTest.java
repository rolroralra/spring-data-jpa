package com.example.springdatajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.springdatajpa.controller.dto.MemberDto;
import com.example.springdatajpa.domain.Member;
import com.example.springdatajpa.domain.Team;
import com.example.springdatajpa.repository.common.JpaRepositoryTest;
import com.example.springdatajpa.repository.projection.MemberInfoOnly;
import com.example.springdatajpa.repository.projection.MemberNameOnlyDto;
import com.example.springdatajpa.repository.projection.MemberProjection;
import com.example.springdatajpa.repository.projection.NestedClosedProjection;
import com.example.springdatajpa.repository.spec.MemberSpec;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

class MemberRepositoryTest extends JpaRepositoryTest<Member, Long> {
    @Autowired
    private EntityManager em;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private TeamRepository teamRepository;

    @Override
    protected JpaRepository<Member, Long> repository() {
        return memberRepository;
    }

    @Override
    protected Member createTestInstance() {
        return new Member("test");
    }

    @Test
    @DisplayName("test")
    void test() {
        // Given
        Member member = new Member("memberA");

        // When
        Member savedMember = memberRepository.save(member);

        // Then
        Optional<Member> findById = memberRepository.findById(savedMember.getId());
        assertThat(findById).isPresent()
            .get()
            .isEqualTo(member);
    }
    
    @Test
    @DisplayName("testEntity")
    void testEntity() {
        // Given
        long beforeCount = memberRepository.count();
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);
        
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        
        em.flush();
        em.clear();
        
        // When
        List<Member> members = em.createQuery("select m from Member m", Member.class)
            .getResultList();
        

        // Then
        assertThat(members).hasSize((int) (beforeCount + 4));
        
        members.forEach(member -> {
            System.out.println("member = " + member);
            System.out.println("member.getTeam() = " + member.getTeam());
        });
    }


    @Test
    @DisplayName("findByNameAndAgeGreaterThan")
    void findByNameAndAgeGreaterThan() {
        // Given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // When
        List<Member> result = memberRepository.findByNameAndAgeGreaterThan("AAA", 15);

        // Then
        assertThat(result).hasSize(1)
            .contains(m2)
            .first()
            .hasFieldOrPropertyWithValue("name", "AAA")
            .hasFieldOrPropertyWithValue("age", 20);
    }
    
    @Test
    @DisplayName("findMemberByNameAndGreaterThan")
    void findMemberByNameAndGreaterThan() {
        // Given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // When
        List<Member> result = memberRepository.findMemberByNameAndGreaterThan("AAA", 15);

        // Then
        assertThat(result).hasSize(1)
            .contains(m2)
            .first()
            .hasFieldOrPropertyWithValue("name", "AAA")
            .hasFieldOrPropertyWithValue("age", 20);
    }

    @Test
    @DisplayName("findMemberByName")
    void findMemberByName() {
        // Given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // When
        List<Member> result = memberRepository.findCustomMemberByName("AAA");

        // Then
        assertThat(result).hasSize(2)
            .contains(m1, m2);
    }

    @Test
    @DisplayName("findUser")
    void findUser() {
        // Given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // When
        List<Member> members = memberRepository.findUser("AAA", 10);

        // Then
        assertThat(members).hasSize(1)
            .contains(m1)
            .doesNotContain(m2)
            .first()
            .hasFieldOrPropertyWithValue("name", "AAA")
            .hasFieldOrPropertyWithValue("age", 10);
    }

    @Test
    @DisplayName("")
    void findUsernameList() {
        // Given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // When
        List<String> usernameList = memberRepository.findUsernameList();

        // Then
        assertThat(usernameList)
            .hasSize((int) memberRepository.count())
            .contains("AAA");
    }

    @Test
    @DisplayName("findMemberDtos")
    void findMemberDtos() {
        // Given


        // When
        List<MemberDto> memberDtos = memberRepository.findMemberDtos();

        // Then
        assertThat(memberDtos).isNotEmpty();
    }

    @Test
    @DisplayName("findMembers")
    void findMembers() {
        // Given
        Member m1 = new Member("ABC", 10);
        Member m2 = new Member("ABC", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // When
        List<Member> members = memberRepository.findMembers("B");

        // Then
        assertThat(members).hasSize(2)
            .containsOnly(m1, m2);
    }

    @Test
    @DisplayName("findByNames")
    void findByNames() {
        // Given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);


        // When
        List<Member> members = memberRepository.findByNames(List.of("AAA", "BBB"));

        // Then
        assertThat(members).hasSize(2)
            .containsOnly(m1, m2);
    }

    @Test
    @DisplayName("findByPage")
    void findByPage() {
        // Given
        Member m1 = new Member("AAA", 50);
        Member m2 = new Member("BBB", 50);
        Member m3 = new Member("CCC", 50);
        Member m4 = new Member("DDD", 50);
        Member m5 = new Member("FFF", 50);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);

        // When
        List<Member> members = memberRepository.findByPage(50, 0, 3);
        long totalCount = memberRepository.countByAge(50);

        // Then
        assertThat(members).hasSize(3)
           .containsExactly(m5, m4, m3);

        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    @DisplayName("findByAge")
    void findByAge() {
        // Given
        Member m1 = new Member("AAA", 50);
        Member m2 = new Member("BBB", 50);
        Member m3 = new Member("CCC", 50);
        Member m4 = new Member("DDD", 50);
        Member m5 = new Member("FFF", 50);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);

        // When
        Page<Member> page = memberRepository.findByAge(50,
            PageRequest.of(0, 3, Sort.by(Order.desc("name"))));

        long totalCount = memberRepository.countByAge(50);

        // Then
        assertThat(page.getContent()).hasSize(3)
            .containsExactly(m5, m4, m3);

        assertThat(page.getTotalElements()).isEqualTo(totalCount);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    @Test
    @DisplayName("findMemberByAge")
    void findMemberByAge() {
        // Given
        Member m1 = new Member("AAA", 50);
        Member m2 = new Member("BBB", 50);
        Member m3 = new Member("CCC", 50);
        Member m4 = new Member("DDD", 50);
        Member m5 = new Member("FFF", 50);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);

        // When
        Slice<Member> slice = memberRepository.findMemberByAge(50,
            PageRequest.of(0, 3, Sort.by(Order.desc("name"))));

        // Then
        assertThat(slice.getContent()).hasSize(3)
            .containsExactly(m5, m4, m3);

        assertThat(slice.getNumber()).isEqualTo(0);
        assertThat(slice.isFirst()).isTrue();
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    @DisplayName("bulkAgePlus")
    void bulkAgePlus() {
        // Given
        Member m1 = new Member("AAA", 50);
        Member m2 = new Member("BBB", 50);
        Member m3 = new Member("CCC", 50);
        Member m4 = new Member("DDD", 50);
        Member m5 = new Member("FFF", 50);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);

        // When
        int resultCount = memberRepository.bulkAgePlus(50);

        // Then
        assertThat(resultCount).isEqualTo(5);
        Page<Member> page = memberRepository.findByAge(51, PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(5)
            .containsOnly(m1, m2, m3, m4, m5);
    }

    @Test
    @DisplayName("bulkAgePlusOne")
    void bulkAgePlusOne() {
        // Given
        Member m1 = new Member("AAA", 50);
        Member m2 = new Member("BBB", 50);
        Member m3 = new Member("CCC", 50);
        Member m4 = new Member("DDD", 50);
        Member m5 = new Member("FFF", 50);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);

        // When
        int resultCount = memberRepository.bulkAgePlusOne(50);

        // Then
        assertThat(resultCount).isEqualTo(5);
        Page<Member> page = memberRepository.findByAge(51, PageRequest.of(0, 10));
        List<Member> result = page.getContent();
        assertThat(result).hasSize(5);
        result.forEach(member ->
            assertThat(member).hasFieldOrPropertyWithValue("age", 51)
        );
    }

    @Test
    @DisplayName("testLazyLoading")
    void testLazyLoading() {
        // Given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();

        // Then
        members.forEach(member -> {
            assertThat(Hibernate.isInitialized(member.getTeam())).isFalse();

            PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
            assertThat(util.isLoaded(member.getTeam())).isFalse();
        });
    }
    
    @Test
    @DisplayName("findMemberEntityGraph")
    void findMemberEntityGraph() {
        // Given
        Member m1 = new Member("AAA", 50);
        Member m2 = new Member("BBB", 50);
        Member m3 = new Member("CCC", 50);
        Member m4 = new Member("DDD", 50);
        Member m5 = new Member("FFF", 50);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);

        // When
        List<Member> members = memberRepository.findMemberEntityGraph();

        // Then
        members.forEach(member -> {
            assertThat(Hibernate.isInitialized(member.getTeam())).isTrue();

            PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
            assertThat(util.isLoaded(member.getTeam())).isTrue();
        });
    }

    @Test
    @DisplayName("findByName")
    void findByName() {
        // Given
        Member m1 = new Member("AAA", 50);
        Member m2 = new Member("AAA", 50);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // When
        List<Member> members = memberRepository.findByName("AAA");

        // Then
        assertThat(members).hasSize(2)
                .containsExactly(m1, m2);

        members.forEach(member -> {
            assertThat(Hibernate.isInitialized(member.getTeam())).isTrue();

            PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
            assertThat(util.isLoaded(member.getTeam())).isTrue();
        });
    }

    @Test
    @DisplayName("findMemberByNamedEntityGraph")
    void findMemberByNamedEntityGraph() {
        // Given
        memberRepository.deleteAll();

        Member m1 = new Member("AAA", 50);
        Member m2 = new Member("BBB", 50);
        Member m3 = new Member("CCC", 50);
        Member m4 = new Member("DDD", 50);
        Member m5 = new Member("FFF", 50);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);


        // When
        List<Member> members = memberRepository.findMemberByNamedEntityGraph();

        // Then
        members.forEach(member -> {
            assertThat(Hibernate.isInitialized(member.getTeam())).isTrue();

            PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
            assertThat(util.isLoaded(member.getTeam())).isTrue();
        });
    }

    @Test
    @DisplayName("jpaSpecificationExecutor")
    void jpaSpecificationExecutor() {
        // Given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("memberName1", 10, teamA);
        Member m2 = new Member("memberName2", 10, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        // When
        Specification<Member> spec = MemberSpec.memberName("memberName1")
            .and(MemberSpec.teamName("teamA"));

        List<Member> result = memberRepository.findAll(spec);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findQueryByExample")
    void findQueryByExample() {
        // Given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("memberName1", 10, teamA);
        Member m2 = new Member("memberName2", 10, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        // When
        Member member = new Member("memberName1");
        Team team = new Team("teamA");
        member.changeTeam(team);

        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withIgnorePaths("age");

        Example<Member> example = Example.of(member, exampleMatcher);

        List<Member> result = memberRepository.findAll(example);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("closedProjection")
    void closedProjection() {
        // Given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        // When
        Member m1 = new Member("m1", 10, teamA);
        Member m2 = new Member("m2", 10, teamA);

        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

//        List<MemberNameOnly> result = memberRepository.findProjectionsByName("m1");

        // Then
//        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("openProjection")
    void openProjection() {
        // Given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        // When
        Member m1 = new Member("m1", 10, teamA);
        Member m2 = new Member("m2", 10, teamA);

        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        List<MemberInfoOnly> result = memberRepository.findProjectionsByName("m1");

        // Then
        assertThat(result).hasSize(1);

        result.stream().map(MemberInfoOnly::getMemberInfo).forEach(System.out::println);
    }

    @Test
    @DisplayName("dynamicProjection")
    void dynamicProjection() {
        // Given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        // When
        Member m1 = new Member("m1", 10, teamA);
        Member m2 = new Member("m2", 10, teamA);

        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // Dynamic Projection 의 경우, 매핑할 파라미터에 맞는 생성자가 1개 있어야함.
        List<MemberNameOnlyDto> result = memberRepository.findProjectionsByName("m1", MemberNameOnlyDto.class);

        // Then
        assertThat(result).hasSize(1);

        result.stream().map(MemberNameOnlyDto::getName).forEach(System.out::println);
    }

    @Test
    @DisplayName("nestedClosedProjection")
    void nestedClosedProjection() {
        // Given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        // When
        Member m1 = new Member("m1", 10, teamA);
        Member m2 = new Member("m2", 10, teamA);

        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        List<NestedClosedProjection> result = memberRepository.findProjectionsByName("m1", NestedClosedProjection.class);

        // Then
        assertThat(result).hasSize(1);

        result.forEach(proj -> {
            System.out.println("proj.getName() = " + proj.getName());
            System.out.println("proj.getTeam().getName() = " + proj.getTeam().getName());
        });
    }

    @Test
    @DisplayName("findByNativeProjection")
    void findByNativeProjection() {
        // Given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        em.flush();
        em.clear();

        // When
        Page<MemberProjection> page = memberRepository.findByNativeProjection(
            PageRequest.of(0, 3));

        // Then
        assertThat(page).hasSize(3);
        page.getContent().forEach(memberProjection -> {
            System.out.println("memberProjection.getId() = " + memberProjection.getId());
            System.out.println("memberProjection.getName() = " + memberProjection.getName());
            System.out.println("memberProjection.getTeamName() = " + memberProjection.getTeamName());
        });
    }

    @Test
    @DisplayName("findNativeQuery")
    void findNativeQuery() {
        // Given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("m1", 10, teamA);
        Member member2 = new Member("m2", 20, teamA);
        Member member3 = new Member("m3", 30, teamB);
        Member member4 = new Member("m4", 40, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        em.flush();
        em.clear();

        // When
        Member result = memberRepository.findByNativeQuery("m2");

        // Hibenate Transformers 의 경우, DTO 클래스에 기본생성자가 있어야함.

        // Then
        assertThat(result).isNotNull()
            .hasFieldOrPropertyWithValue("name", "m2")
            .hasFieldOrPropertyWithValue("age", 20);
    }

    @Test
    @DisplayName("findNativeQueryByHibernate")
    void findNativeQueryByHibernate() {
        // Given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        em.flush();
        em.clear();

        // When
        List<MemberDto> result = memberRepository.findNativeQueryByHibernate(0, 3);

        // Hibenate Transformers 의 경우, DTO 클래스에 기본생성자가 있어야함.

        // Then
        assertThat(result).hasSize(3);
        result.forEach(memberDto -> System.out.println("memberDto.getName() = " + memberDto.getName()));
    }
}
