package com.citi.ordermatching.domain;

import java.util.Date;

public class Orderbook {
    private Integer id;

    private String symbol;

    private Integer traderid;

    private String type;

    private Integer size;

    private Double price;

    private String status;

    private Date operatetime;

    private String strategy;

    private String fok;

    private String duration;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getOperatetime() {
        return operatetime;
    }

    public void setOperatetime(Date operatetime) {
        this.operatetime = operatetime;
    }

    public String getStrategy() { return strategy; }

    public void setStrategy(String strategy) { this.strategy = strategy; }

    public String getFok() {
        return fok;
    }

    public void setFok(String fok) {
        this.fok = fok;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}