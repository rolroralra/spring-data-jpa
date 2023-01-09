package com.example.springdatajpa.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TEAM")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    @ToString.Exclude
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }

    public void addMember(Member member) {
        if (!members.contains(member)) {
            members.add(member);
            member.changeTeam(this);
        }
    }
    public void removeMember(Member member) {
        members.remove(member);
    }
}
