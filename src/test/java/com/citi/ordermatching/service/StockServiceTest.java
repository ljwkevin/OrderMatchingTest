package com.citi.ordermatching.service;

import com.citi.ordermatching.controller.StockController;
import com.citi.ordermatching.domain.Stock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class StockServiceTest {
    @Autowired
    private StockService stockService;
    StockController sc=new StockController();
    Stock stock = new Stock();

    @Test
    public void findAllStocks() {
        //  stock.setName("SNY");
        List<Stock> allStocks = stockService.findAllStocks();
        for (int i = 0; i < allStocks.size(); i++) {
            System.out.println("the stocks are " + allStocks.get(i).getName());
        }
    }

    @Test
    public void findStockByParam() {
        /* find by Symbol*/
        stock.setSymbol("AAPL");
        Stock StocksBySymbol = stockService.findStockByParam(stock.getSymbol());
        System.out.println("the stock is " + StocksBySymbol.getName());

        /* find by name*/
        stock.setName("IBM");
        Stock StocksByName = stockService.findStockByParam(stock.getName());
        System.out.println("the stock is " + StocksByName.getName());

    }
}