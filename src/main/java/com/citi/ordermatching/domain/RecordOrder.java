package com.citi.ordermatching.domain;

import java.util.List;

/**
 * Created by Dell on 2018/8/15.
 */
public class RecordOrder
{
    private History history;
    private List<Execution> executionList;
    private Orderbook orderbook;

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public List<Execution> getExecutionList() {
        return executionList;
    }

    public void setExecutionList(List<Execution> executionList) {
        this.executionList = executionList;
    }

    public Orderbook getOrderbook() {
        return orderbook;
    }

    public void setOrderbook(Orderbook orderbook) {
        this.orderbook = orderbook;
    }
}
