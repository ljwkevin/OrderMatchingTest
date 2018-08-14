package com.citi.ordermatching.serviceImpl;

import com.citi.ordermatching.dao.DealRecordMapper;
import com.citi.ordermatching.domain.DealRecord;
import com.citi.ordermatching.service.DealRecordService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class DealRecordServiceImpl implements DealRecordService {
    @Autowired
    DealRecordMapper dealRecordMapper;

    @Override
    public void addRecord(Date dealTime, double dealPrice, int dealSize, int bidOrderId, int askOrderId){
        DealRecord dr = new DealRecord(dealTime, dealPrice, dealSize, bidOrderId, askOrderId);
        //update the database Table "dealrecord"
        dealRecordMapper.insert(dr);
    }
}
