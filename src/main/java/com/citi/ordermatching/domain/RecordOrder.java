package com.citi.ordermatching.domain;

import java.util.List;

/**
 * Created by Dell on 2018/8/15.
 */
public class RecordOrder
{
    private History history;
    private List<DealRecord> dealRecordList;
    private Orderbook orderbook;

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public List<DealRecord> getDealRecordList() {
        return dealRecordList;
    }

    public void setDealRecordList(List<DealRecord> dealRecordList) {
        this.dealRecordList = dealRecordList;
    }

    public Orderbook getOrderbook() {
        return orderbook;
    }

    public void setOrderbook(Orderbook orderbook) {
        this.orderbook = orderbook;
    }
}
