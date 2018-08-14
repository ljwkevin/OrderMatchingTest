package com.citi.ordermatching.controller;

import com.citi.ordermatching.domain.DealRecord;
import com.citi.ordermatching.domain.Orderbook;
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


    public boolean checkMatch(String symbol){
        List<Orderbook> bidList=orderbookService.findBidBySymbol(symbol);
        List<Orderbook> askList=orderbookService.findAskBySymbol(symbol);
        double bid=bidList.get(0).getPrice();
        double ask=askList.get(0).getPrice();

        if(bid-ask<0.000001){
            return true;
        }else{
            return false;
        }

    }


    public void match(@RequestParam("symbol")String symbol){

        boolean flag=checkMatch(symbol);
        if(flag){


        }else{
            Orderbook orderbook=new Orderbook();
            orderbook.setType("");
            orderbook.setSymbol(symbol);
            orderbook.setSize(1);
            Date date=new Date();
            orderbook.setOperatetime(date);
            orderbookService.addOrderbookItem(orderbook);

        }
    }

    /**
     *
     */
    @RequestMapping("submitOrder")
    public void selectStrategy(Orderbook orderbook){
        orderbookService.processOrder(orderbook);
    }
}
