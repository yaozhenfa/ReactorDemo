package com.orvillex.reactordemo.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Order {
    private final Long id;
    private final String deliveryName;
    private final String deliveryStreet;
    private final String deliveryCity;
    private final String deliveryState;
    private final String deliveryZip;
    private final String ccNumber;
    private final String ccExpiration;
    private final String ccCVV;
    private final Date placedAt;
}
