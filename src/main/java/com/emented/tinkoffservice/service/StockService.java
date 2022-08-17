package com.emented.tinkoffservice.service;

import com.emented.tinkoffservice.model.Stock;

public interface StockService {
    Stock getStockByTicker(String ticker);
}
