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
import com.citi.ordermatching.service.HistoryService;
import com.citi.ordermatching.service.OrderbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
        if(orderbook.getStrategy().equals(Strategy.Matching)){

        }else if(orderbook.getStrategy().equals(Strategy.MKT.toString())){
            processMKT(orderbook);
        }else if(orderbook.getStrategy().equals(Strategy.LMT)){
            processLMT(orderbook);
        }else {

        }
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

    public void generateDealMessage(Date dealTime, double dealPrice, int dealSize, int bidOrderId, int askOrderId){
        DealRecord dr = new DealRecord();
        //update the database Table "dealrecord"
        dr.setDealtime(dealTime);
        dr.setDealprice(dealPrice);
        dr.setDealsize(dealSize);
        dr.setDealtime(dealTime);
        dr.setBidorderid(bidOrderId);
        dealRecordMapper.insertSelective(dr);
    }

    /**
     * Market Order, NO "FOK"
     * @param orderbook
     * @return
     */
    @Override
    public boolean processMKT(Orderbook orderbook){
        Date dealTime;
        double dealPrice;
        int dealSize;
        History history = new History();
        if(orderbook.getType().equals(OrderType.ASK.toString())){
            //
            List<Orderbook> bidList=findBidBySymbol(orderbook.getSymbol());
            Orderbook bestBid = bidList.get(0);
            if(bestBid.getSize() == orderbook.getSize()){
                dealPrice = bestBid.getPrice();
                dealSize = bestBid.getSize();
                generateDealMessage(new Date(), dealPrice, dealSize, bestBid.getId(), 1);
                bestBid.setStatus(OrderStatus.FINISHED.toString());
                orderbook.setStatus(OrderStatus.FINISHED.toString());
                bidList.get(0).setSize(bidList.get(0).getSize() - dealSize);
                orderbookMapper.updateByPrimaryKey(bidList.get(0));
            }
            else if(bestBid.getSize() < orderbook.getSize()){
                int size = orderbook.getSize();
                int i = 0;
                while (size > 0 && i < bidList.size()){
                    if(size>bidList.get(i).getSize()){
                        dealPrice = bidList.get(i).getPrice();
                        dealSize = bidList.get(i).getSize();
                        generateDealMessage(new Date(), dealPrice, dealSize, bidList.get(i).getId(), orderbook.getId());
                        bidList.get(i).setStatus(OrderStatus.FINISHED.toString());
                        size = size - bidList.get(i).getSize();
                        bidList.get(i).setSize(bidList.get(i).getSize() - dealSize);
                        orderbookMapper.updateByPrimaryKey(bidList.get(i));
                        i++;
                    }else if(size<bidList.get(i).getSize()){
                        dealPrice = bidList.get(i).getPrice();
                        dealSize = size;
                        generateDealMessage(new Date(), dealPrice, dealSize, bidList.get(i).getId(), orderbook.getId());
                        orderbook.setStatus(OrderStatus.FINISHED.toString());
                        bidList.get(i).setSize(bidList.get(i).getSize() - dealSize);
                        orderbookMapper.updateByPrimaryKey(bidList.get(i));
                        size = 0;
                        i++;
                    }else {
                        dealPrice = bidList.get(i).getPrice();
                        dealSize = bidList.get(i).getSize();
                        generateDealMessage(new Date(), dealPrice, dealSize, bestBid.getId(), orderbook.getId());
                        bidList.get(i).setStatus(OrderStatus.FINISHED.toString());
                        orderbook.setStatus(OrderStatus.FINISHED.toString());
                        bidList.get(i).setSize(bidList.get(i).getSize() - dealSize);
                        orderbookMapper.updateByPrimaryKey(bidList.get(i));
                        size = 0;
                        i++;
                    }
                }
            }else {
                dealPrice = bestBid.getPrice();
                dealSize = orderbook.getSize();
                generateDealMessage(new Date(), dealPrice, dealSize, bestBid.getId(), orderbook.getId());
                orderbook.setStatus(OrderStatus.FINISHED.toString());
                bidList.get(0).setSize(bidList.get(0).getSize() - dealSize);
                orderbookMapper.updateByPrimaryKey(bidList.get(0));
            }
        }else if(orderbook.getType().equals(OrderType.BID.toString())){
            //
            List<Orderbook> askList=findAskBySymbol(orderbook.getSymbol());
            Orderbook bestAsk = askList.get(0);
            if(bestAsk.getSize() == orderbook.getSize()){
                dealPrice = bestAsk.getPrice();
                dealSize = bestAsk.getSize();
                generateDealMessage(new Date(), dealPrice, dealSize, bestAsk.getId(), 1);
                bestAsk.setStatus(OrderStatus.FINISHED.toString());
                orderbook.setStatus(OrderStatus.FINISHED.toString());
                askList.get(0).setSize(askList.get(0).getSize() - dealSize);
                orderbookMapper.updateByPrimaryKey(askList.get(0));
            }
            else if(bestAsk.getSize() < orderbook.getSize()){
                int size = orderbook.getSize();
                int i = 0;
                while (size > 0 && i < askList.size()){
                    if(size>askList.get(i).getSize()){
                        dealPrice = askList.get(i).getPrice();
                        dealSize = askList.get(i).getSize();
                        generateDealMessage(new Date(), dealPrice, dealSize, askList.get(i).getId(),1);
                        askList.get(i).setStatus(OrderStatus.FINISHED.toString());
                        size = size - askList.get(i).getSize();
                        askList.get(i).setSize(askList.get(i).getSize() - dealSize);
                        orderbookMapper.updateByPrimaryKey(askList.get(i));
                        i++;
                    }else if(size<askList.get(i).getSize()){
                        dealPrice = askList.get(i).getPrice();
                        dealSize = size;
                        generateDealMessage(new Date(), dealPrice, dealSize, askList.get(i).getId(), 1);
                        orderbook.setStatus(OrderStatus.FINISHED.toString());
                        size = 0;
                        askList.get(i).setSize(askList.get(i).getSize() - dealSize);
                        orderbookMapper.updateByPrimaryKey(askList.get(i));
                        i++;
                    }else {
                        dealPrice = askList.get(i).getPrice();
                        dealSize = askList.get(i).getSize();
                        generateDealMessage(new Date(), dealPrice, dealSize, bestAsk.getId(), orderbook.getId());
                        askList.get(i).setStatus(OrderStatus.FINISHED.toString());
                        orderbook.setStatus(OrderStatus.FINISHED.toString());
                        size = 0;
                        askList.get(i).setSize(askList.get(i).getSize() - dealSize);
                        orderbookMapper.updateByPrimaryKey(askList.get(i));
                        i++;
                    }

                }
            } else {
                dealPrice = bestAsk.getPrice();
                dealSize = orderbook.getSize();
                generateDealMessage(new Date(), dealPrice, dealSize, bestAsk.getId(), orderbook.getId());
                orderbook.setStatus(OrderStatus.FINISHED.toString());
                askList.get(0).setSize(askList.get(0).getSize() - dealSize);
                orderbookMapper.updateByPrimaryKey(askList.get(0));
            }
        }
        history.setCommittime(new Date());
        history.setSize(orderbook.getSize());
        history.setStatus(OrderStatus.FINISHED.toString());
        history.setTraderid(orderbook.getTraderid());
        history.setType(orderbook.getType());
        history.setSymbol(orderbook.getSymbol());
        history.setStrategy(Strategy.MKT.toString());
        historyMapper.insertSelective(history);
        return true;
    }

    /**
     * Limit Order
     * @param orderbook
     */
    @Override
    public void processLMT(Orderbook orderbook) {

        orderbookMapper.insertSelective(orderbook);


        Date dealTime;
        double dealPrice;
        int dealSize;
        History history = new History();
        List<Orderbook> bidList=findBidBySymbol(orderbook.getSymbol());
        List<Orderbook> askList=findAskBySymbol(orderbook.getSymbol());

        if(orderbook.getType().equals(OrderType.ASK.toString())){
            //

            Orderbook bestBid = bidList.get(0);
            double bestBidPrice=bestBid.getPrice();
            double bestAskPrice=askList.get(0).getPrice();
            if(bestAskPrice-bestBidPrice>0.01){
                return ;
            }else{
                if(bestBid.getSize() == orderbook.getSize()){
                    dealPrice = bestBid.getPrice();
                    dealSize = bestBid.getSize();
                    generateDealMessage(new Date(), dealPrice, dealSize, bestBid.getId(), orderbook.getId());
                    bestBid.setStatus(OrderStatus.FINISHED.toString());
                    orderbook.setStatus(OrderStatus.FINISHED.toString());
                    bidList.get(0).setSize(bidList.get(0).getSize() - dealSize);
                    orderbookMapper.updateByPrimaryKey(bidList.get(0));
                }
                else if(bestBid.getSize() < orderbook.getSize()){
                    int size = orderbook.getSize();
                    int i = 0;
                    while (size > 0 && i < bidList.size()){
                        if(size>bidList.get(i).getSize()){
                            dealPrice = bidList.get(i).getPrice();
                            dealSize = bidList.get(i).getSize();
                            generateDealMessage(new Date(), dealPrice, dealSize, bidList.get(i).getId(), orderbook.getId());
                            bidList.get(i).setStatus(OrderStatus.FINISHED.toString());
                            size = size - bidList.get(i).getSize();
                            bidList.get(i).setSize(bidList.get(i).getSize() - dealSize);
                            orderbookMapper.updateByPrimaryKey(bidList.get(i));
                            i++;
                        }else if(size<bidList.get(i).getSize()){
                            dealPrice = bidList.get(i).getPrice();
                            dealSize = size;
                            generateDealMessage(new Date(), dealPrice, dealSize, bidList.get(i).getId(), orderbook.getId());
                            orderbook.setStatus(OrderStatus.FINISHED.toString());
                            bidList.get(i).setSize(bidList.get(i).getSize() - dealSize);
                            orderbookMapper.updateByPrimaryKey(bidList.get(i));
                            size = 0;
                            i++;
                        }else {
                            dealPrice = bidList.get(i).getPrice();
                            dealSize = bidList.get(i).getSize();
                            generateDealMessage(new Date(), dealPrice, dealSize, bestBid.getId(), orderbook.getId());
                            bidList.get(i).setStatus(OrderStatus.FINISHED.toString());
                            orderbook.setStatus(OrderStatus.FINISHED.toString());
                            bidList.get(i).setSize(bidList.get(i).getSize() - dealSize);
                            orderbookMapper.updateByPrimaryKey(bidList.get(i));
                            size = 0;
                            i++;
                        }
                    }
                }else {
                    dealPrice = bestBid.getPrice();
                    dealSize = orderbook.getSize();
                    generateDealMessage(new Date(), dealPrice, dealSize, bestBid.getId(), orderbook.getId());
                    orderbook.setStatus(OrderStatus.FINISHED.toString());
                    bidList.get(0).setSize(bidList.get(0).getSize() - dealSize);
                    orderbookMapper.updateByPrimaryKey(bidList.get(0));
                }
            }


        }else if(orderbook.getType().equals(OrderType.BID.toString())){
            //
            Orderbook bestBid = bidList.get(0);
            double bestBidPrice=bestBid.getPrice();
            double bestAskPrice=askList.get(0).getPrice();
            if(bestAskPrice-bestBidPrice>0.01){
                return ;
            }else {

                Orderbook bestAsk = askList.get(0);
                if (bestAsk.getSize() == orderbook.getSize()) {
                    dealPrice = bestAsk.getPrice();
                    dealSize = bestAsk.getSize();
                    generateDealMessage(new Date(), dealPrice, dealSize, bestAsk.getId(), orderbook.getId());
                    bestAsk.setStatus(OrderStatus.FINISHED.toString());
                    orderbook.setStatus(OrderStatus.FINISHED.toString());
                    askList.get(0).setSize(askList.get(0).getSize() - dealSize);
                    orderbookMapper.updateByPrimaryKey(askList.get(0));
                } else if (bestAsk.getSize() < orderbook.getSize()) {
                    int size = orderbook.getSize();
                    int i = 0;
                    while (size > 0 && i < askList.size()) {
                        if (size > askList.get(i).getSize()) {
                            dealPrice = askList.get(i).getPrice();
                            dealSize = askList.get(i).getSize();
                            generateDealMessage(new Date(), dealPrice, dealSize, askList.get(i).getId(), orderbook.getId());
                            askList.get(i).setStatus(OrderStatus.FINISHED.toString());
                            size = size - askList.get(i).getSize();
                            askList.get(i).setSize(askList.get(i).getSize() - dealSize);
                            orderbookMapper.updateByPrimaryKey(askList.get(i));
                            i++;
                        } else if (size < askList.get(i).getSize()) {
                            dealPrice = askList.get(i).getPrice();
                            dealSize = size;
                            generateDealMessage(new Date(), dealPrice, dealSize, askList.get(i).getId(), orderbook.getId());
                            orderbook.setStatus(OrderStatus.FINISHED.toString());
                            size = 0;
                            askList.get(i).setSize(askList.get(i).getSize() - dealSize);
                            orderbookMapper.updateByPrimaryKey(askList.get(i));
                            i++;
                        } else {
                            dealPrice = askList.get(i).getPrice();
                            dealSize = askList.get(i).getSize();
                            generateDealMessage(new Date(), dealPrice, dealSize, bestAsk.getId(), orderbook.getId());
                            askList.get(i).setStatus(OrderStatus.FINISHED.toString());
                            orderbook.setStatus(OrderStatus.FINISHED.toString());
                            size = 0;
                            askList.get(i).setSize(askList.get(i).getSize() - dealSize);
                            orderbookMapper.updateByPrimaryKey(askList.get(i));
                            i++;
                        }

                    }
                } else {
                    dealPrice = bestAsk.getPrice();
                    dealSize = orderbook.getSize();
                    generateDealMessage(new Date(), dealPrice, dealSize, bestAsk.getId(), orderbook.getId());
                    orderbook.setStatus(OrderStatus.FINISHED.toString());
                    askList.get(0).setSize(askList.get(0).getSize() - dealSize);
                    orderbookMapper.updateByPrimaryKey(askList.get(0));
                }

            }
        }
        history.setCommittime(new Date());
        history.setSize(orderbook.getSize());
        history.setStatus(OrderStatus.FINISHED.toString());
        history.setPrice(orderbook.getPrice());
        history.setTraderid(orderbook.getTraderid());
        history.setType(orderbook.getType());
        history.setSymbol(orderbook.getSymbol());
        history.setStrategy(Strategy.LMT.toString());
        historyMapper.insertSelective(history);

    }

    /**
     * Matching Order
     * @param orderbook
     */
    @Override
    public void processMatching(Orderbook orderbook) {
        History history = new History();
        history.setCommittime(new Date());
        history.setSize(orderbook.getSize());
        history.setStatus(OrderStatus.WAITING.toString());
        history.setPrice(orderbook.getPrice());
        history.setTraderid(orderbook.getTraderid());
        history.setType(orderbook.getType());
        history.setSymbol(orderbook.getSymbol());
        history.setStrategy(Strategy.Matching.toString());
        int i = orderbookMapper.insertSelective(orderbook);
        int j = historyMapper.insertSelective(history);
        if(i > 0 && j > 0){
            match(orderbook.getSymbol(), history.getId());
        }
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

    public void match(String symbol, int historyId){
        List<Orderbook> bidList=findBidBySymbol(symbol);
        List<Orderbook> askList=findAskBySymbol(symbol);
        boolean flag=checkMatch(symbol);
        if(flag){

            int dealSize;
            if(bidList.get(0).getSize() > askList.get(0).getSize()){
                dealSize = askList.get(0).getSize();
                bidList.get(0).setSize(bidList.get(0).getSize() - askList.get(0).getSize());
                askList.get(0).setSize(0);
                askList.get(0).setStatus(OrderStatus.FINISHED.toString());
            }else if(bidList.get(0).getSize() < askList.get(0).getSize()){
                dealSize = bidList.get(0).getSize();
                askList.get(0).setSize(askList.get(0).getSize() - bidList.get(0).getSize());
                bidList.get(0).setSize(0);
                bidList.get(0).setStatus(OrderStatus.FINISHED.toString());
            }else {
                dealSize = bidList.get(0).getSize();
                askList.get(0).setSize(0);
                bidList.get(0).setSize(0);
                bidList.get(0).setStatus(OrderStatus.FINISHED.toString());
                askList.get(0).setStatus(OrderStatus.FINISHED.toString());
            }
            //?History
            orderbookMapper.updateByPrimaryKeySelective(bidList.get(0));
            orderbookMapper.updateByPrimaryKeySelective(askList.get(0));
            History history = historyMapper.selectByPrimaryKey(historyId);
            history.setStatus(OrderStatus.FINISHED.toString());
            historyMapper.updateByPrimaryKeySelective(history);
            generateDealMessage(new Date(), bidList.get(0).getPrice(), dealSize, bidList.get(0).getId(), askList.get(0).getId());
        }else{
/*            Orderbook orderbook=new Orderbook();
            orderbook.setType("");
            orderbook.setSymbol(symbol);
            orderbook.setSize(1);
            Date date=new Date();
            orderbook.setOperatetime(date);
            addOrderbookItem(orderbook);*/

        }
    }
}
