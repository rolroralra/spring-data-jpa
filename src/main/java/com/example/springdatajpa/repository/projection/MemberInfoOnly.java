package com.example.springdatajpa.repository.projection;

import org.springframework.beans.factory.annotation.Value;

public interface MemberInfoOnly {
    @Value("#{target.name + ' ' + target.age + ' ' + target.team.name}")
    String getMemberInfo();
}
