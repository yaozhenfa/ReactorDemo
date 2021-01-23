package com.orvillex.reactordemo.domain;

import com.orvillex.reactordemo.enums.Type;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@Data
@With
@Table
@AllArgsConstructor
public class Ingredient {
    @Id
    private final Long id;
    private final String name;
    private final Type type;
}
