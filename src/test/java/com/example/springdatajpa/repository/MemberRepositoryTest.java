package com.example.springdatajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.springdatajpa.controller.dto.MemberDto;
import com.example.springdatajpa.domain.Member;
import com.example.springdatajpa.domain.Team;
import com.example.springdatajpa.repository.common.JpaRepositoryTest;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

class MemberRepositoryTest extends JpaRepositoryTest<Member, Long> {
    @Autowired
    private EntityManager em;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private TeamRepository teamRepository;

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
    @DisplayName("")
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
    @DisplayName("")
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

    @Override
    protected JpaRepository<Member, Long> repository() {
        return memberRepository;
    }

    @Override
    protected Member createTestInstance() {
        return new Member("test");
    }
}
