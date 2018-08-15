package com.citi.ordermatching.controller;

import com.citi.ordermatching.domain.DealRecord;
import com.citi.ordermatching.domain.History;
import com.citi.ordermatching.domain.Orderbook;
import com.citi.ordermatching.enums.OrderStatus;
import com.citi.ordermatching.enums.OrderType;
import com.citi.ordermatching.enums.Strategy;
import com.citi.ordermatching.service.OrderbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.citi.ordermatching.util.GenerateOrderid.generateOrderid;

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
                               @RequestParam("traderid") Integer traderid, @RequestParam(value = "price", defaultValue = "0") double price){
        //接收到order请求后，首先，初始化History对象
        History history = new History();
        Date date = new Date();
        history.setCommittime(date);
        history.setSymbol(symbol);
        history.setSize(size);
        history.setType(type);
        history.setStrategy(strategy);
        history.setTraderid(traderid);
        history.setStatus(OrderStatus.WAITING.toString());
        history.setOrderid(generateOrderid(date, traderid));

        //根据strategy，选择进一步要赋值的History对象的属性，并调用对应service
        if(strategy.equals(Strategy.Matching.toString())){
            history.setPrice(price);
            orderbookService.processMatching(history);
        }else if(strategy.equals(Strategy.MKT.toString())){
            orderbookService.processMKT(history);
        }else if(strategy.equals(Strategy.LMT.toString())){
            history.setPrice(price);
            orderbookService.processLMT(history);
        }else {

        }


    }
}
