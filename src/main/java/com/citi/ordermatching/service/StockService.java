package com.citi.ordermatching.service;

import com.citi.ordermatching.domain.Stock;

import java.util.List;

/**
 * Created by Dell on 2018/8/12.
 */
public interface StockService {

    public List<Stock> findAllStocks();
    public Stock findStockByParam(String param);
}
