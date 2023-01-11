package com.example.springdatajpa.controller;

import com.example.springdatajpa.domain.Member;
import com.example.springdatajpa.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/api/v1/members/{id}")
    public String findMemberV1(@PathVariable Long id) throws NotFoundException {
        return memberRepository.findById(id).orElseThrow(NotFoundException::new).getName();
    }

    @GetMapping("/api/v2/members/{id}")
    public String findMemberV2(@PathVariable("id") Member member) {
        return member.getName();
    }

    @GetMapping("/api/v1/members")
    public List<String> findMembersV1(@PageableDefault(page = 0, size= 20, sort = {"name"}) Pageable pageable) {
        return memberRepository.findAll(pageable).map(Member::getName).getContent();
    }

}
