package com.emented.tinkoffservice.dto;

import lombok.Value;

@Value
public class StockPrice {
    String figi;
    Double price;
}
