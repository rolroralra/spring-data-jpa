package com.example.springdatajpa.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String name;
    private Integer age;
    private String teamName;
}
