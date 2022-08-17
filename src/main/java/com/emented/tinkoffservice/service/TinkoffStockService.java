package com.emented.tinkoffservice.service;

import com.emented.tinkoffservice.dto.StocksDTO;
import com.emented.tinkoffservice.dto.TickersDTO;
import com.emented.tinkoffservice.exception.StockNotFoundException;
import com.emented.tinkoffservice.model.Currency;
import com.emented.tinkoffservice.model.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.tinkoff.invest.openapi.MarketContext;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrument;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrumentList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TinkoffStockService implements StockService {

    private final OpenApi openApi;

    @Async
    public CompletableFuture<MarketInstrumentList> getMarketInstrumentByTicker(String ticker) {
        MarketContext context = openApi.getMarketContext();
        return context.searchMarketInstrumentsByTicker(ticker);
    }
    @Override
    public Stock getStockByTicker(String ticker) {
        CompletableFuture<MarketInstrumentList> completableFuture = getMarketInstrumentByTicker(ticker);
        List<MarketInstrument> marketInstrumentList = completableFuture.join().getInstruments();
        if (marketInstrumentList.isEmpty()) {
            throw new StockNotFoundException(String.format("Stack %S not found.", ticker));
        }

        MarketInstrument item = marketInstrumentList.get(0);
        return new Stock(item.getTicker(),
                item.getFigi(),
                item.getName(),
                item.getType().getValue(),
                Currency.valueOf(item.getCurrency().getValue()),
                "TINKOFF");
    }

    @Override
    public StocksDTO getStocksByTickers(TickersDTO tickers) {
        List<CompletableFuture<MarketInstrumentList>> marketInstruments = new ArrayList<>();
        tickers.getTickers().forEach(ticker -> marketInstruments.add(getMarketInstrumentByTicker(ticker)));
        List<Stock> stocks = marketInstruments.stream()
                .map(CompletableFuture::join)
                .map(mi -> {
                    if (!mi.getInstruments().isEmpty()) {
                        return mi.getInstruments().get(0);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .map(element -> new Stock(element.getTicker(),
                        element.getFigi(),
                        element.getName(),
                        element.getType().getValue(),
                        Currency.valueOf(element.getCurrency().getValue()),
                        "TINKOFF")).toList();
        return new StocksDTO(stocks);
    }
}
