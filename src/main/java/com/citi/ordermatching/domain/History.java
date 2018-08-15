package com.citi.ordermatching.domain;

import java.util.Date;

public class History {
    private Integer id;

    private Date committime;

    private Integer traderid;

    private String symbol;

    private String type;

    private Integer size;

    private Double price;

    private String strategy;

    private String status;

    private String fok;

    private Integer duration;

    private String orderid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCommittime() {
        return committime;
    }

    public void setCommittime(Date committime) {
        this.committime = committime;
    }

    public Integer getTraderid() {
        return traderid;
    }

    public void setTraderid(Integer traderid) {
        this.traderid = traderid;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy == null ? null : strategy.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getFok() {
        return fok;
    }

    public void setFok(String fok) {
        this.fok = fok == null ? null : fok.trim();
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }
}