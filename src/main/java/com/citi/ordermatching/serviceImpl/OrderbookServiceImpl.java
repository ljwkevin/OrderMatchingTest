package com.citi.ordermatching.serviceImpl;

import com.citi.ordermatching.dao.DealRecordMapper;
import com.citi.ordermatching.dao.HistoryMapper;
import com.citi.ordermatching.domain.History;
import com.citi.ordermatching.enums.OrderStatus;
import com.citi.ordermatching.enums.OrderType;
import com.citi.ordermatching.enums.Strategy;
import com.citi.ordermatching.dao.OrderbookMapper;
import com.citi.ordermatching.domain.DealRecord;
import com.citi.ordermatching.domain.Orderbook;
import com.citi.ordermatching.service.OrderbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Dell on 2018/8/12.
 */
@Service
public class OrderbookServiceImpl implements OrderbookService {

    @Autowired
    private OrderbookMapper orderbookMapper;
    @Autowired
    private DealRecordMapper dealRecordMapper;
    @Autowired
    private HistoryMapper historyMapper;

    @Override
    public List<Orderbook> findBidBySymbol(String symbol) {
        return orderbookMapper.findBidBySymbol(symbol);
    }

    @Override
    public List<Orderbook> findAskBySymbol(String symbol) {
        return orderbookMapper.findAskBySymbol(symbol);
    }

    @Override
    public void addOrderbookItem(Orderbook orderbook) {
        orderbookMapper.insertSelective(orderbook);
    }

    @Override
    public boolean deleteOrderbookItem(int orderbookid) {
        int flag=orderbookMapper.deleteByPrimaryKey(orderbookid);
        if(flag!=-1){
            return true;
        }
        return false;
    }

    /**
     * @param orderbook
     */
    @Override
    public void processOrder(Orderbook orderbook){
/*        if(orderbook.getStratesgy().equals(Strategy.Matching)){

        }else if(orderbook.getStrategy().equals(Strategy.MKT.toString())){
            processMKT(orderbook);
        }else if(orderbook.getStrategy().equals(Strategy.LMT)){
            processLMT(orderbook);
        }else {

        }*/
    }

    @Override
    public Orderbook findById(int orderId) {
        return orderbookMapper.selectByPrimaryKey(orderId);
    }

    @Override
    public void cancelOrder(Orderbook orderbook) {
        orderbook.setStatus(OrderStatus.CANCELLED.toString());
        orderbookMapper.updateByPrimaryKey(orderbook);
    }

    public void generateDealMessage(Date dealTime, double dealPrice, int dealSize, String bidOrderId, String askOrderId){
        DealRecord dr = new DealRecord();
        //update the database Table "dealrecord"
        dr.setDealtime(dealTime);
        dr.setDealprice(dealPrice);
        dr.setDealsize(dealSize);
        dr.setDealtime(dealTime);
        dr.setBidorderid(bidOrderId);
        dr.setAskorderid(askOrderId);
        dealRecordMapper.insertSelective(dr);
    }

    /**
     * Market Order, NO "FOK"
     * @param history
     * @return
     */
    @Override
    public void processMKT(History history){
        List<Orderbook> bidList=findBidBySymbol(history.getSymbol());
        List<Orderbook> askList=findAskBySymbol(history.getSymbol());
        historyMapper.insertSelective(history);
        processMKT(history, bidList, askList);
    }


    public void processMKT(History history, List<Orderbook> bidList, List<Orderbook>askList){
        Date dealTime = history.getCommittime();
        double dealPrice;
        int dealSize;
        if(history.getType().equals(OrderType.ASK.toString())){
            //
            Orderbook bestBid = bidList.get(0);
            if(bestBid.getSize() == history.getSize()){
                dealPrice = bestBid.getPrice();
                dealSize = bestBid.getSize();
                bestBid.setStatus(OrderStatus.FINISHED.toString());
                bestBid.setSize(0);
                history.setStatus(OrderStatus.FINISHED.toString());
                generateDealMessage(dealTime, dealPrice, dealSize, bestBid.getOrderid(),history.getOrderid());
                orderbookMapper.updateByPrimaryKeySelective(bestBid);
            }
            else if(bestBid.getSize() < history.getSize()){
                int size = history.getSize();       //每次交易后剩下的数量
                int i = 0;
                while (size > 0 && i < bidList.size()){
                    if(size>bidList.get(i).getSize()){
                        dealPrice = bidList.get(i).getPrice();
                        dealSize = bidList.get(i).getSize();
                        size = size - bidList.get(i).getSize();
                        bidList.get(i).setStatus(OrderStatus.FINISHED.toString());
                        bidList.get(i).setSize(bidList.get(i).getSize() - dealSize);    //bidList.get(i).getSize()  可用size替代
                        generateDealMessage(dealTime, dealPrice, dealSize, bidList.get(i).getOrderid(), history.getOrderid());
                        orderbookMapper.updateByPrimaryKeySelective(bidList.get(i));
                        i++;
                    }else if(size<bidList.get(i).getSize()){
                        dealPrice = bidList.get(i).getPrice();
                        dealSize = size;
                        size = 0;
                        bidList.get(i).setSize(bidList.get(i).getSize() - dealSize);
                        history.setStatus(OrderStatus.FINISHED.toString());
                        generateDealMessage(dealTime, dealPrice, dealSize, bidList.get(i).getOrderid(), history.getOrderid());
                        orderbookMapper.updateByPrimaryKey(bidList.get(i));
                        i++;
                    }else {
                        dealPrice = bidList.get(i).getPrice();
                        dealSize = bidList.get(i).getSize();
                        size = 0;
                        bidList.get(i).setStatus(OrderStatus.FINISHED.toString());
                        bidList.get(i).setSize(bidList.get(i).getSize() - dealSize);
                        history.setStatus(OrderStatus.FINISHED.toString());
                        generateDealMessage(dealTime, dealPrice, dealSize, bestBid.getOrderid(), history.getOrderid());
                        orderbookMapper.updateByPrimaryKey(bidList.get(i));
                        i++;
                    }
                }
            }else {
                dealPrice = bestBid.getPrice();
                dealSize = history.getSize();
                bestBid.setSize(bestBid.getSize() - dealSize);
                history.setStatus(OrderStatus.FINISHED.toString());
                generateDealMessage(dealTime, dealPrice, dealSize, bestBid.getOrderid(), history.getOrderid());
                orderbookMapper.updateByPrimaryKey(bidList.get(0));
            }
        }else if(history.getType().equals(OrderType.BID.toString())){
            //
            Orderbook bestAsk = askList.get(0);
            if(bestAsk.getSize() == history.getSize()){
                dealPrice = bestAsk.getPrice();
                dealSize = bestAsk.getSize();
                bestAsk.setStatus(OrderStatus.FINISHED.toString());
                bestAsk.setSize(0);
                history.setStatus(OrderStatus.FINISHED.toString());
                generateDealMessage(dealTime, dealPrice, dealSize, history.getOrderid(), bestAsk.getOrderid());
                orderbookMapper.updateByPrimaryKey(askList.get(0));
            }
            else if(bestAsk.getSize() < history.getSize()){
                int size = history.getSize();
                int i = 0;
                while (size > 0 && i < askList.size()){
                    if(size>askList.get(i).getSize()){
                        dealPrice = askList.get(i).getPrice();
                        dealSize = askList.get(i).getSize();
                        size = size - askList.get(i).getSize();
                        askList.get(i).setStatus(OrderStatus.FINISHED.toString());
                        askList.get(i).setSize(askList.get(i).getSize() - dealSize);
                        generateDealMessage(dealTime, dealPrice, dealSize, history.getOrderid(), askList.get(i).getOrderid());
                        orderbookMapper.updateByPrimaryKey(askList.get(i));
                        i++;
                    }else if(size<askList.get(i).getSize()){
                        dealPrice = askList.get(i).getPrice();
                        dealSize = size;
                        size = 0;
                        askList.get(i).setSize(askList.get(i).getSize() - dealSize);
                        history.setStatus(OrderStatus.FINISHED.toString());
                        generateDealMessage(dealTime, dealPrice, dealSize, history.getOrderid(), askList.get(i).getOrderid());
                        orderbookMapper.updateByPrimaryKey(askList.get(i));
                        i++;
                    }else {
                        dealPrice = askList.get(i).getPrice();
                        dealSize = askList.get(i).getSize();
                        size = 0;
                        askList.get(i).setSize(askList.get(i).getSize() - dealSize);
                        askList.get(i).setStatus(OrderStatus.FINISHED.toString());
                        history.setStatus(OrderStatus.FINISHED.toString());
                        generateDealMessage(dealTime, dealPrice, dealSize, history.getOrderid(), bestAsk.getOrderid());
                        orderbookMapper.updateByPrimaryKey(askList.get(i));
                        i++;
                    }

                }
            } else {
                dealPrice = bestAsk.getPrice();
                dealSize = bestAsk.getSize();
                askList.get(0).setSize(bestAsk.getSize() - dealSize);
                askList.get(0).setStatus(OrderStatus.FINISHED.toString());
                history.setStatus(OrderStatus.FINISHED.toString());
                generateDealMessage(dealTime, dealPrice, dealSize, history.getOrderid(), bestAsk.getOrderid());
                orderbookMapper.updateByPrimaryKey(askList.get(0));
            }
        }
        historyMapper.updateByOrderidSelective(history);

        //Orderbook发生变化，触发程序检查所有stop order
        processSTP(history.getSymbol());
    }

    /**
     * Limit Order
     * @param history
     */
    @Override
    public void processLMT(History history) {
        Orderbook orderbook = new Orderbook();
        orderbook.setSymbol(history.getSymbol());
        orderbook.setSize(history.getSize());
        orderbook.setStrategy(history.getStrategy());
        orderbook.setType(history.getType());
        orderbook.setTraderid(history.getTraderid());
        orderbook.setStatus(history.getStatus());
        orderbook.setOperatetime(history.getCommittime());
        orderbook.setPrice(history.getPrice());
        orderbook.setOrderid(history.getOrderid());

        orderbookMapper.insert(orderbook);

        Date dealTime = orderbook.getOperatetime();
        double dealPrice;
        int dealSize;

        List<Orderbook> bidList=findBidBySymbol(orderbook.getSymbol());
        List<Orderbook> askList=findAskBySymbol(orderbook.getSymbol());

        if(orderbook.getType().equals(OrderType.ASK.toString())){
            //

            Orderbook bestBid = bidList.get(0);
            double bestBidPrice=bestBid.getPrice();
            double bestAskPrice=askList.get(0).getPrice();
            if(bestAskPrice-bestBidPrice>0.001){
                history.setStatus(OrderStatus.WAITING.toString());
                historyMapper.insertSelective(history);
                return ;
            }else{
                if(bestBid.getSize() == orderbook.getSize()){
                    dealPrice = bestBid.getPrice();
                    dealSize = bestBid.getSize();
                    generateDealMessage(dealTime, dealPrice, dealSize, bestBid.getOrderid(), orderbook.getOrderid());
                    bestBid.setStatus(OrderStatus.FINISHED.toString());
                    orderbook.setStatus(OrderStatus.FINISHED.toString());
/*                    //---------
                    history.setStatus(OrderStatus.FINISHED.toString());
                    //---------*/
                    bidList.get(0).setSize(bidList.get(0).getSize() - dealSize);
                    askList.get(0).setSize(0);
                    askList.get(0).setStatus(OrderStatus.FINISHED.toString());
                    orderbookMapper.updateByPrimaryKey(askList.get(0));
                    orderbookMapper.updateByPrimaryKey(bidList.get(0));
                }
                else if(bestBid.getSize() < orderbook.getSize()){


                    int size = orderbook.getSize();
                    int i = 0;
                    while (size > 0 && i < bidList.size()){
                        if(bestAskPrice-bidList.get(i).getPrice()>0.001) {
                            history.setStatus(OrderStatus.WAITING.toString());
                            historyMapper.insertSelective(history);
                            return;
                        }
                        if(size>bidList.get(i).getSize()){
                            dealPrice = bidList.get(i).getPrice();
                            dealSize = bidList.get(i).getSize();
                            generateDealMessage(dealTime, dealPrice, dealSize, bidList.get(i).getOrderid(), orderbook.getOrderid());

                            bidList.get(i).setStatus(OrderStatus.FINISHED.toString());
                            size = size - bidList.get(i).getSize();
                            bidList.get(i).setSize(0);
                            askList.get(0).setSize(size);
                            orderbookMapper.updateByPrimaryKey(askList.get(0));
                            orderbookMapper.updateByPrimaryKey(bidList.get(i));
                            i++;
                        }else if(size<bidList.get(i).getSize()){
                            dealPrice = bidList.get(i).getPrice();
                            dealSize = size;
                            generateDealMessage(dealTime, dealPrice, dealSize, bidList.get(i).getOrderid(), orderbook.getOrderid());

                            orderbook.setStatus(OrderStatus.FINISHED.toString());
                            orderbook.setSize(0);
                            askList.get(0).setSize(0);
                            askList.get(0).setStatus(OrderStatus.FINISHED.toString());
                            bidList.get(i).setSize(bidList.get(i).getSize() - dealSize);
                            orderbookMapper.updateByPrimaryKey(askList.get(0));
                            orderbookMapper.updateByPrimaryKey(bidList.get(i));
                            size = 0;
                            i++;
                        }else {
                            dealPrice = bidList.get(i).getPrice();
                            dealSize = bidList.get(i).getSize();
                            generateDealMessage(dealTime, dealPrice, dealSize, bestBid.getOrderid(), orderbook.getOrderid());
                            bidList.get(i).setStatus(OrderStatus.FINISHED.toString());
                            orderbook.setStatus(OrderStatus.FINISHED.toString());
                            bidList.get(i).setSize(bidList.get(i).getSize() - dealSize);
                            orderbook.setSize(0);
                            orderbookMapper.updateByPrimaryKey(orderbook);
                            orderbookMapper.updateByPrimaryKey(bidList.get(i));
                            size = 0;
                            i++;
                        }
                    }
                }else {
                    dealPrice = bestBid.getPrice();
                    dealSize = orderbook.getSize();
                    generateDealMessage(dealTime, dealPrice, dealSize, bestBid.getOrderid(), orderbook.getOrderid());
                    orderbook.setSize(0);
                    orderbook.setStatus(OrderStatus.FINISHED.toString());
                    bidList.get(0).setSize(bidList.get(0).getSize() - dealSize);
                    orderbookMapper.updateByPrimaryKey(orderbook);
                    orderbookMapper.updateByPrimaryKey(bidList.get(0));
                }
            }


        }else if(orderbook.getType().equals(OrderType.BID.toString())){
            //
            Orderbook bestBid = bidList.get(0);
            double bestBidPrice=bestBid.getPrice();
            double bestAskPrice=askList.get(0).getPrice();
            if(bestAskPrice-bestBidPrice>0.001){
                history.setStatus(OrderStatus.WAITING.toString());
                historyMapper.insertSelective(history);
                return ;
            }else {

                Orderbook bestAsk = askList.get(0);
                double orderPrice=orderbook.getPrice();

                if (bestAsk.getSize() == orderbook.getSize()) {
                    dealPrice = bestAsk.getPrice();
                    dealSize = bestAsk.getSize();
                    generateDealMessage(dealTime, dealPrice, dealSize,orderbook.getOrderid(),bestAsk.getOrderid());
                    bestAsk.setStatus(OrderStatus.FINISHED.toString());
                    orderbook.setStatus(OrderStatus.FINISHED.toString());
                    askList.get(0).setSize(askList.get(0).getSize() - dealSize);
                    bidList.get(0).setSize(0);
                    orderbookMapper.updateByPrimaryKey(askList.get(0));
                    orderbookMapper.updateByPrimaryKey(bidList.get(0));
                } else if (bestAsk.getSize() < orderbook.getSize()) {
                    int size = orderbook.getSize();
                    int i = 0;
                    while (size > 0 && i < askList.size()) {
                        if(askList.get(i).getPrice()-bestBidPrice>0.001) {
                            history.setStatus(OrderStatus.WAITING.toString());
                            historyMapper.insertSelective(history);
                            return;
                        }
                        if (size > askList.get(i).getSize()) {
                            dealPrice = askList.get(i).getPrice();
                            dealSize = askList.get(i).getSize();
                            generateDealMessage(dealTime, dealPrice, dealSize, orderbook.getOrderid(), askList.get(i).getOrderid());

                            askList.get(i).setStatus(OrderStatus.FINISHED.toString());
                            size = size - askList.get(i).getSize();
                            askList.get(i).setSize(0);
                            // askList.get(i).setSize(askList.get(i).getSize() - dealSize);
                            bidList.get(0).setSize(size);
                            orderbookMapper.updateByPrimaryKey(bidList.get(0));
                            orderbookMapper.updateByPrimaryKey(askList.get(i));
                            i++;
                        } else if (size < askList.get(i).getSize()) {
                            dealPrice = askList.get(i).getPrice();
                            dealSize = size;
                            generateDealMessage(dealTime, dealPrice, dealSize, orderbook.getOrderid(),  askList.get(i).getOrderid());
                            orderbook.setSize(0);
                            orderbook.setStatus(OrderStatus.FINISHED.toString());
                            size = 0;
                            bidList.get(0).setSize(0);
                            bidList.get(0).setStatus(OrderStatus.FINISHED.toString());

                            askList.get(i).setSize(askList.get(i).getSize() - dealSize);
                            orderbookMapper.updateByPrimaryKey(bidList.get(0));
                            orderbookMapper.updateByPrimaryKey(askList.get(i));
                            i++;
                        } else {
                            dealPrice = askList.get(i).getPrice();
                            dealSize = askList.get(i).getSize();
                            generateDealMessage(dealTime, dealPrice, dealSize, orderbook.getOrderid(), bestAsk.getOrderid());
                            askList.get(i).setStatus(OrderStatus.FINISHED.toString());
                            orderbook.setStatus(OrderStatus.FINISHED.toString());
                            size = 0;
                            orderbook.setSize(0);
                            askList.get(i).setSize(askList.get(i).getSize() - dealSize);
                            orderbookMapper.updateByPrimaryKey(orderbook);
                            orderbookMapper.updateByPrimaryKey(askList.get(i));
                            i++;
                        }

                    }
                } else {
                    dealPrice = bestAsk.getPrice();
                    dealSize = orderbook.getSize();
                    generateDealMessage(dealTime, dealPrice, dealSize, orderbook.getOrderid(), bestAsk.getOrderid());
                    orderbook.setSize(0);
                    orderbook.setStatus(OrderStatus.FINISHED.toString());
                    askList.get(0).setSize(askList.get(0).getSize() - dealSize);
                    orderbookMapper.updateByPrimaryKey(orderbook);
                    orderbookMapper.updateByPrimaryKey(askList.get(0));
                }

            }

        }
        history.setStatus(OrderStatus.FINISHED.toString());
        historyMapper.insertSelective(history);

        //Orderbook发生变化，触发程序检查所有stop order
        processSTP(history.getSymbol());
    }

    @Override
    public void processSTP(History history) {
        historyMapper.insertSelective(history);
    }

    public void processSTP(String symbol){
        ArrayList<History> bitHistories = historyMapper.selectBitSTPWaitingByPriceAscByTimeAsc(symbol);
        ArrayList<History> askHistories = historyMapper.selectAskSTPWaitingByPriceDescByTimeAsc(symbol);
        List<Orderbook> askList=findAskBySymbol(symbol);
        List<Orderbook> bidList=findBidBySymbol(symbol);
        for(History history : bitHistories){
            double ask=askList.get(0).getPrice();
            double price = history.getPrice();
            if(ask >= price){
                processMKT(history, bidList, askList);
            }
        }
        for(History history : askHistories){
            double bit=bidList.get(0).getPrice();
            double price = history.getPrice();
            if(bit <= price){
                processMKT(history, bidList, askList);
            }
        }
    }

    /**
     * Matching Order
     * @param history
     */
    @Override
    public void processMatching(History history) {
        Orderbook orderbook = new Orderbook();
        orderbook.setSymbol(history.getSymbol());
        orderbook.setSize(history.getSize());
        orderbook.setStrategy(history.getStrategy());
        orderbook.setType(history.getType());
        orderbook.setTraderid(history.getTraderid());
        orderbook.setStatus(history.getStatus());
        orderbook.setOperatetime(history.getCommittime());
        orderbook.setPrice(history.getPrice());
        orderbook.setOrderid(history.getOrderid());

        int i = orderbookMapper.insert(orderbook);
        int j = historyMapper.insert(history);
        if(j > 0 && j > 0){
             match(orderbook.getSymbol());
        }

        //Orderbook发生变化，触发程序检查所有stop order
        processSTP(history.getSymbol());
    }


    public boolean checkMatch(String symbol){
        List<Orderbook> bidList=findBidBySymbol(symbol);
        List<Orderbook> askList=findAskBySymbol(symbol);
        double bid=bidList.get(0).getPrice();
        double ask=askList.get(0).getPrice();

        if(bid-ask<0.000001){
            return true;
        }else{
            return false;
        }

    }

    public void match(String symbol){
        List<Orderbook> bidList=findBidBySymbol(symbol);
        List<Orderbook> askList=findAskBySymbol(symbol);
        History bidHistory = historyMapper.selectByOrderid(bidList.get(0).getOrderid());
        History askHistory = historyMapper.selectByOrderid(askList.get(0).getOrderid());
        boolean flag=checkMatch(symbol);
        if(flag){
            int dealSize;
            if(bidList.get(0).getSize() > askList.get(0).getSize()){
                dealSize = askList.get(0).getSize();
                bidList.get(0).setSize(bidList.get(0).getSize() - askList.get(0).getSize());
                askList.get(0).setSize(0);
                askList.get(0).setStatus(OrderStatus.FINISHED.toString());
                askHistory.setStatus(OrderStatus.FINISHED.toString());
            }else if(bidList.get(0).getSize() < askList.get(0).getSize()){
                dealSize = bidList.get(0).getSize();
                askList.get(0).setSize(askList.get(0).getSize() - bidList.get(0).getSize());
                bidList.get(0).setSize(0);
                bidList.get(0).setStatus(OrderStatus.FINISHED.toString());
                bidHistory.setStatus(OrderStatus.FINISHED.toString());
            }else {
                dealSize = bidList.get(0).getSize();
                askList.get(0).setSize(0);
                bidList.get(0).setSize(0);
                bidList.get(0).setStatus(OrderStatus.FINISHED.toString());
                askList.get(0).setStatus(OrderStatus.FINISHED.toString());
                bidHistory.setStatus(OrderStatus.FINISHED.toString());
                askHistory.setStatus(OrderStatus.FINISHED.toString());
            }

            orderbookMapper.updateByPrimaryKeySelective(bidList.get(0));
            orderbookMapper.updateByPrimaryKeySelective(askList.get(0));

            historyMapper.updateByOrderidSelective(bidHistory);
            historyMapper.updateByOrderidSelective(askHistory);
            generateDealMessage(new Date(), bidList.get(0).getPrice(), dealSize, bidList.get(0).getOrderid(), askList.get(0).getOrderid());
        }else{
        }
    }

    public Map<String, Double> getTheBestPrices(String symbol){
        Map<String, Double> bestPrice = new HashMap();
        List<Orderbook> bidList=findBidBySymbol(symbol);
        List<Orderbook> askList=findAskBySymbol(symbol);
        double bid=bidList.get(0).getPrice();
        double ask=askList.get(0).getPrice();
        bestPrice.put("bestBid", bid);
        bestPrice.put("bestAsk", ask);
        return bestPrice;
    }

    public boolean isTheBestPriceChanged(Orderbook orderbook){
        String symbol = orderbook.getSymbol();
        Map<String, Double> beforeBestPrice = getTheBestPrices(symbol);
        orderbookMapper.insertSelective(orderbook);
        Map<String, Double> afterBestPrice = getTheBestPrices(symbol);
        if(beforeBestPrice.get("bestBid")-afterBestPrice.get("bestBid")<0.000001 ||
        beforeBestPrice.get("bestAsk")-afterBestPrice.get("bestAsk")<0.000001){
            return false;
        }
        return true;
    }
}
