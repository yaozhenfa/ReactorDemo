package com.orvillex.reactordemo.domain;

import com.orvillex.reactordemo.enums.Type;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {
    @Id
    private Long id;
    private String name;
    private Type type;
}
