package com.citi.ordermatching.service;

import com.citi.ordermatching.domain.History;
import com.citi.ordermatching.domain.Orderbook;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 2018/8/12.
 */
public interface OrderbookService {

    List<Orderbook> findBidBySymbol(String symbol);
    List<Orderbook> findAskBySymbol(String symbol);
    public void addOrderbookItem(Orderbook orderbook);
    public boolean deleteOrderbookItem(int orderbookid);

    public void processOrder(Orderbook orderbook);

    public void processMatching(History history);

    public void processMKT(History history);

    public void processLMT(History history);

    public void processSTP(History history);

    Orderbook findById(int orderId);

    void cancelOrder(Orderbook orderbook);

}
