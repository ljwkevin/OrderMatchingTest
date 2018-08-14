package com.citi.ordermatching.domain;

import java.util.Date;

public class DealRecord {
    private Integer id;

    private Date dealtime;

    private Double dealprice;

    private Integer dealsize;

    private Integer bidorderid;

    private Integer askorderid;
/*
    public DealRecord(Date dealtime, Double dealprice, Integer dealsize, Integer bidorderid, Integer askorderid) {
        this.dealtime = dealtime;
        this.dealprice = dealprice;
        this.dealsize = dealsize;
        this.bidorderid = bidorderid;
        this.askorderid = askorderid;
    }*/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDealtime() {
        return dealtime;
    }

    public void setDealtime(Date dealtime) {
        this.dealtime = dealtime;
    }

    public Double getDealprice() {
        return dealprice;
    }

    public void setDealprice(Double dealprice) {
        this.dealprice = dealprice;
    }

    public Integer getDealsize() {
        return dealsize;
    }

    public void setDealsize(Integer dealsize) {
        this.dealsize = dealsize;
    }

    public Integer getBidorderid() {
        return bidorderid;
    }

    public void setBidorderid(Integer bidorderid) {
        this.bidorderid = bidorderid;
    }

    public Integer getAskorderid() {
        return askorderid;
    }

    public void setAskorderid(Integer askorderid) {
        this.askorderid = askorderid;
    }
}