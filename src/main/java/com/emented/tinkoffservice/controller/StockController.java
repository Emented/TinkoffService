package com.emented.tinkoffservice.controller;

import com.emented.tinkoffservice.dto.FigiesDTO;
import com.emented.tinkoffservice.dto.StocksDTO;
import com.emented.tinkoffservice.dto.StocksPricesDTO;
import com.emented.tinkoffservice.dto.TickersDTO;
import com.emented.tinkoffservice.model.Stock;
import com.emented.tinkoffservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/stocks/{ticker}")
    public Stock getStock(@PathVariable String ticker) {
        return stockService.getStockByTicker(ticker);
    }

    @PostMapping("/stocks/getStocksByTickers")
    public StocksDTO getStocksByTickers(@RequestBody TickersDTO tickersDTO) {
        return stockService.getStocksByTickers(tickersDTO);
    }

    @PostMapping("/prices")
    public StocksPricesDTO getPrices(@RequestBody FigiesDTO figiesDTO) {
        return stockService.getPrices(figiesDTO);
    }
}
