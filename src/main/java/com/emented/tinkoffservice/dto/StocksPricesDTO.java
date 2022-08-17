package com.emented.tinkoffservice.dto;

import lombok.Value;

import java.util.List;

@Value
public class StocksPricesDTO {
    List<StockPrice> prices;
}
