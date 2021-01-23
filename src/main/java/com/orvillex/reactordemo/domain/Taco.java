package com.orvillex.reactordemo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Document
@AllArgsConstructor
public class Taco {
    @Id
    private final String id;
    private final String name;
    private final String remark;
}
