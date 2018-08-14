package com.citi.ordermatching.service;

import com.citi.ordermatching.domain.DealRecord;

import java.util.Date;
import java.util.List;

public interface DealRecordService {
    public void addRecord(Date dealTime, double dealPrice, int dealSize, int bidOrderId, int askOrderId);
    public List<DealRecord> findAllRecord();
}
