package com.emented.tinkoffservice.service;

import com.emented.tinkoffservice.dto.FigiesDTO;
import com.emented.tinkoffservice.dto.StocksDTO;
import com.emented.tinkoffservice.dto.StocksPricesDTO;
import com.emented.tinkoffservice.dto.TickersDTO;
import com.emented.tinkoffservice.model.Stock;

public interface StockService {
    Stock getStockByTicker(String ticker);

    StocksDTO getStocksByTickers(TickersDTO tickersDTO);

    StocksPricesDTO getPrices(FigiesDTO figiesDTO);
}
