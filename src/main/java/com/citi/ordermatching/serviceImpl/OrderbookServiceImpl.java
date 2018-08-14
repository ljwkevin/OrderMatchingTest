package com.citi.ordermatching.serviceImpl;

import com.citi.ordermatching.dao.DealRecordMapper;
import com.citi.ordermatching.enums.OrderStatus;
import com.citi.ordermatching.enums.OrderType;
import com.citi.ordermatching.enums.Strategy;
import com.citi.ordermatching.dao.OrderbookMapper;
import com.citi.ordermatching.domain.DealRecord;
import com.citi.ordermatching.domain.Orderbook;
import com.citi.ordermatching.service.OrderbookService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public void processOrder(Orderbook orderbook){
        if(orderbook.getStrategy().equals(Strategy.Matching)){

        }else if(orderbook.getStrategy().equals(Strategy.MKT)){
            processMKT(orderbook);
        }else {

        }
    }

    public void generateDealMessage(Date dealTime, double dealPrice, int dealSize, int bidOrderId, int askOrderId){
        DealRecord dr = new DealRecord(dealTime, dealPrice, dealSize, bidOrderId, askOrderId);
        //update the database Table "dealrecord"
        dealRecordMapper.insert(dr);
    }
    /**
     * Market Order, NO "FOK"
     */

    /**
     * Market Order, NO "FOK"
     */
    public boolean processMKT(Orderbook orderbook){
        Date dealTime;
        double dealPrice;
        int dealSize;
        if(orderbook.getType().equals(OrderType.ASK)){
            //
            List<Orderbook> bidList=findBidBySymbol(orderbook.getSymbol());
            Orderbook bestBid = bidList.get(0);
            if(bestBid.getSize() == orderbook.getSize()){
                dealPrice = bestBid.getPrice();
                dealSize = bestBid.getSize();
                generateDealMessage(new Date(), dealPrice, dealSize, bestBid.getId(), orderbook.getId());
                bestBid.setStatus(OrderStatus.FINISHED.toString());
                orderbook.setStatus(OrderStatus.FINISHED.toString());
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
                        i++;
                    }else if(size<bidList.get(i).getSize()){
                        dealPrice = bidList.get(i).getPrice();
                        dealSize = size;
                        generateDealMessage(new Date(), dealPrice, dealSize, bidList.get(i).getId(), orderbook.getId());
                        orderbook.setStatus(OrderStatus.FINISHED.toString());
                        bidList.get(i).setSize(bidList.get(i).getSize() - dealSize);
                        size = 0;
                        i++;
                    }else {
                        dealPrice = bidList.get(i).getPrice();
                        dealSize = bidList.get(i).getSize();
                        generateDealMessage(new Date(), dealPrice, dealSize, bestBid.getId(), orderbook.getId());
                        bidList.get(i).setStatus(OrderStatus.FINISHED.toString());
                        orderbook.setStatus(OrderStatus.FINISHED.toString());
                        size = 0;
                        i++;
                    }

                }
            }else {
                dealPrice = bestBid.getPrice();
                dealSize = orderbook.getSize();
                generateDealMessage(new Date(), dealPrice, dealSize, bestBid.getId(), orderbook.getId());
                orderbook.setStatus(OrderStatus.FINISHED.toString());
            }
        }else if(orderbook.getType().equals(OrderType.BID)){
            //
            List<Orderbook> askList=findAskBySymbol(orderbook.getSymbol());
            Orderbook bestAsk = askList.get(0);
            if(bestAsk.getSize() == orderbook.getSize()){
                dealPrice = bestAsk.getPrice();
                dealSize = bestAsk.getSize();
                generateDealMessage(new Date(), dealPrice, dealSize, bestAsk.getId(), orderbook.getId());
                bestAsk.setStatus(OrderStatus.FINISHED.toString());
                orderbook.setStatus(OrderStatus.FINISHED.toString());
            }
            else if(bestAsk.getSize() < orderbook.getSize()){
                int size = orderbook.getSize();
                int i = 0;
                while (size > 0 && i < askList.size()){
                    if(size>askList.get(i).getSize()){
                        dealPrice = askList.get(i).getPrice();
                        dealSize = askList.get(i).getSize();
                        generateDealMessage(new Date(), dealPrice, dealSize, askList.get(i).getId(), orderbook.getId());
                        askList.get(i).setStatus(OrderStatus.FINISHED.toString());
                        size = size - askList.get(i).getSize();
                        i++;
                    }else if(size<askList.get(i).getSize()){
                        dealPrice = askList.get(i).getPrice();
                        dealSize = size;
                        generateDealMessage(new Date(), dealPrice, dealSize, askList.get(i).getId(), orderbook.getId());
                        orderbook.setStatus(OrderStatus.FINISHED.toString());
                        size = 0;
                        i++;
                    }else {
                        dealPrice = askList.get(i).getPrice();
                        dealSize = askList.get(i).getSize();
                        generateDealMessage(new Date(), dealPrice, dealSize, bestAsk.getId(), orderbook.getId());
                        askList.get(i).setStatus(OrderStatus.FINISHED.toString());
                        orderbook.setStatus(OrderStatus.FINISHED.toString());
                        size = 0;
                        i++;
                    }

                }
            }else {
                dealPrice = bestAsk.getPrice();
                dealSize = orderbook.getSize();
                generateDealMessage(new Date(), dealPrice, dealSize, bestAsk.getId(), orderbook.getId());
                orderbook.setStatus(OrderStatus.FINISHED.toString());
            }

        }
        return false;
    }

}
