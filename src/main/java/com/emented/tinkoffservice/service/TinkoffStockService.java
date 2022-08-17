package com.emented.tinkoffservice.service;

import com.emented.tinkoffservice.dto.*;
import com.emented.tinkoffservice.exception.StockNotFoundException;
import com.emented.tinkoffservice.model.Currency;
import com.emented.tinkoffservice.model.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.tinkoff.invest.openapi.MarketContext;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrument;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrumentList;
import ru.tinkoff.invest.openapi.model.rest.Orderbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
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

    @Async
    public CompletableFuture<Optional<Orderbook>> getOrderbookByFigi(String figi) {
        CompletableFuture<Optional<Orderbook>> orderbook = openApi.getMarketContext().getMarketOrderbook(figi, 0);
        log.info("Getting price {} from Tinkoff", figi);
        return orderbook;
    }

    @Override
    public StocksPricesDTO getPrices(FigiesDTO figiesDTO) {
        long start = System.currentTimeMillis();
        List<CompletableFuture<Optional<Orderbook>>> orderbooks = new ArrayList<>();
        figiesDTO.getFigies().forEach(figi -> orderbooks.add(getOrderbookByFigi(figi)));
        List<StockPrice> stockPrices = orderbooks.stream()
                .map(CompletableFuture::join)
                .map(ob -> ob.orElseThrow(() -> new StockNotFoundException("Stock not found.")))
                .map(ob -> new StockPrice(
                        ob.getFigi(),
                        ob.getLastPrice().doubleValue()))
                .toList();
        log.info("Time - {}", System.currentTimeMillis() - start);
        return new StocksPricesDTO(stockPrices);
    }
}
