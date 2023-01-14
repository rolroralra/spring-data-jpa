package com.example.springdatajpa.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "MEMBER")
@Getter
@Setter(value = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "Member.all",
        attributeNodes = {
            @NamedAttributeNode("team")
        }
    )
})
//@ToString(of = {"id", "name", "age"})
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    @ToString.Exclude
    private Team team;

    public Member(String name) {
        this(name, 0);
    }

    public Member(String name, Integer age) {
        this(name, age, null);
    }

    public Member(String name, Integer age, Team team) {
        this.name = name;
        this.age = age;
        this.team = team;
    }

    public void changeTeam(Team team) {
        if (this.team == team) {
            return;
        }

        if (this.team != null) {
            this.team.removeMember(this);
        }

        this.team = team;
        team.addMember(this);
    }
}
