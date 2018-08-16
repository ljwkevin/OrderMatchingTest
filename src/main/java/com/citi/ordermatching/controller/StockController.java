package com.citi.ordermatching.controller;

import com.alibaba.fastjson.JSON;
import com.citi.ordermatching.domain.OrderStock;
import com.citi.ordermatching.domain.Orderbook;
import com.citi.ordermatching.domain.Stock;
import com.citi.ordermatching.service.OrderbookService;
import com.citi.ordermatching.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dell on 2018/8/12.
 */

@RestController
@RequestMapping("/stock")
@CrossOrigin
public class StockController {

    @Autowired
    private StockService stockService;

    @Autowired
    private OrderbookService orderbookService;

    /*
     Some comment to test!
     */
    @RequestMapping("/findAllStocks")
    @ResponseBody
    public String findAllStocks(){

        List<Stock> result=new ArrayList<>();

        List<Stock> stocks= stockService.findAllStocks();
        for(Stock stock:stocks){
            System.out.println(stock.getSymbol());
            List<Orderbook> bidList=orderbookService.findBidBySymbol(stock.getSymbol());
            List<Orderbook> askList=orderbookService.findAskBySymbol(stock.getSymbol());
            System.out.println(bidList.size());
            System.out.println(askList.size());
            double bestBid=bidList.get(0).getPrice();
            double bestAsk=askList.get(0).getPrice();

            Stock stock1=new Stock();
            stock1.setBestBid(bestBid);
            stock1.setBestAsk(bestAsk);
            stock1.setSymbol(stock.getSymbol());
            stock1.setName(stock.getName());

            result.add(stock1);
        }

        String jsonResult= JSON.toJSONString(result);
        return jsonResult;
    }



    @RequestMapping("/findStock")
    @ResponseBody
    public String findStock(@PathParam("param")String param){


            Stock stock= stockService.findStockByParam(param);
            List<Stock> resultStock=new ArrayList<>();
            List<Orderbook> bidList=orderbookService.findBidBySymbol(stock.getSymbol());
            List<Orderbook> askList=orderbookService.findAskBySymbol(stock.getSymbol());
            double bestBid=bidList.get(0).getPrice();
            double bestAsk=askList.get(0).getPrice();

            Stock stock1=new Stock();
            stock1.setBestBid(bestBid);
            stock1.setBestAsk(bestAsk);
            stock1.setSymbol(stock.getSymbol());
            stock1.setName(stock.getName());

            resultStock.add(stock1);
            String jsonResult= JSON.toJSONString(resultStock);
            return jsonResult;
    }


    @RequestMapping("/findOrderbook")
    @ResponseBody
    public String findOrderbook(@PathParam("symbol")String symbol){
        List<Orderbook> bidlist=orderbookService.findBidBySymbol(symbol);
        List<Orderbook> askList=orderbookService.findAskBySymbol(symbol);
        List<OrderStock> orderStocks=new ArrayList<>();
        Map map=new HashMap();
        map.put("bidList",bidlist);
        map.put("askList",askList);

        int a=askList.size();
        int b=bidlist.size();
        if(a>b){
            for(int i=0;i<a;i++){
                OrderStock orderStock=new OrderStock();
                orderStock.setAsk(String.valueOf(askList.get(i).getPrice()));
                orderStock.setAskSize(String.valueOf(askList.get(i).getSize()));

                if(i<b){
                    orderStock.setBidSize(String.valueOf(bidlist.get(i).getSize()));
                    orderStock.setBid(String.valueOf(bidlist.get(i).getPrice()));
                }else {
                    orderStock.setBidSize("");
                    orderStock.setBid("");
                }

               orderStocks.add(orderStock);
            }
        }else {



            for(int i=0;i<b;i++){
                OrderStock orderStock=new OrderStock();

                orderStock.setBid(String.valueOf(bidlist.get(i).getPrice()));
                orderStock.setBidSize(String.valueOf(bidlist.get(i).getSize()));



                if(i<a){
                    orderStock.setAskSize(String.valueOf(askList.get(i).getSize()));
                    orderStock.setAsk(String.valueOf(askList.get(i).getPrice()));
                }else {
                    orderStock.setAskSize("");
                    orderStock.setAsk("");
                }

                orderStocks.add(orderStock);
            }

        }
        String jsonResult=JSON.toJSONString(orderStocks);
        return jsonResult;
    }

}
