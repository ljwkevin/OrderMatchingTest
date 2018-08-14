package com.citi.ordermatching.serviceImpl;

import com.citi.ordermatching.dao.DealRecordMapper;
import com.citi.ordermatching.domain.DealRecord;
import com.citi.ordermatching.service.DealRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DealRecordServiceImpl implements DealRecordService {
    @Autowired
    DealRecordMapper dealRecordMapper;

    @Override
    public void addRecord(Date dealTime, double dealPrice, int dealSize, int bidOrderId, int askOrderId){

    }

    @Override
    public List<DealRecord> findAllRecord() {
        return dealRecordMapper.findAllDealRecord();
    }

}
