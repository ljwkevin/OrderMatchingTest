package com.citi.ordermatching.controller;

import com.citi.ordermatching.domain.DealRecord;
import com.citi.ordermatching.domain.Orderbook;
import com.citi.ordermatching.enums.OrderStatus;
import com.citi.ordermatching.enums.OrderType;
import com.citi.ordermatching.enums.Strategy;
import com.citi.ordermatching.service.OrderbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by Dell on 2018/8/12.
 */

@RestController
@RequestMapping("orderbook")
@CrossOrigin
public class OrderbookController {

    @Autowired
    private OrderbookService orderbookService;

    @RequestMapping("findBidBySymbol")
    @ResponseBody
    public List<Orderbook> findBidBySymbol(@RequestParam("symbol")String symbol){
        return orderbookService.findBidBySymbol(symbol);
    }

    @RequestMapping("findAskBySymbol")
    @ResponseBody
    public List<Orderbook> findAskBySymbol(@RequestParam("symbol")String symbol){
        return orderbookService.findAskBySymbol(symbol);
    }

    /**
     * Receive Trader Submit Order
     * traderid,symbol,type,size,strategy,price
     */
    @RequestMapping("submitOrder")
    @ResponseBody
    public void selectStrategy(@RequestParam("symbol") String symbol, @RequestParam("size") Integer size,
                               @RequestParam("type") String type, @RequestParam("strategy") String strategy,
                               @RequestParam("traderid") Integer traderid, @RequestParam("price") double price){
        Orderbook orderbook = new Orderbook();
        orderbook.setSymbol(symbol);
        orderbook.setSize(size);
        orderbook.setStrategy(strategy);
        orderbook.setType(type);
        orderbook.setTraderid(traderid);
        orderbook.setStatus(OrderStatus.WAITING.toString());
        if(strategy.equals(Strategy.Matching)){
            orderbook.setPrice(price);
            orderbook.setOperatetime(new Date());
            orderbookService.processMatching(orderbook);
        }else if(strategy.equals(Strategy.MKT)){
            orderbookService.processMKT(orderbook);
        }else if(strategy.equals(Strategy.LMT)){
            orderbook.setPrice(price);
            orderbookService.processLMT(orderbook);
        }else {

        }


    }
}
