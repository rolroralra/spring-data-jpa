package com.example.springdatajpa.repository.spec;

import com.example.springdatajpa.domain.Member;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class MemberSpec {
    public static Specification<Member> teamName(final String teamName) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.isEmpty(teamName)) {
                return null;
            }

            Join<Object, Object> t = root.join("team", JoinType.INNER);

            return criteriaBuilder.equal(t.get("name"), teamName);
        };
    }

    public static Specification<Member> memberName(final String memberName) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("name"), memberName);
    }

}
