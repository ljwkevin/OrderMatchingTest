package com.citi.ordermatching.service;

import com.citi.ordermatching.domain.DealRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class DealRecordServiceTest {
    @Autowired
    private DealRecordService dealRecordService;
    DealRecord dealRecord = new DealRecord();
    Date date = new Date();

    @Test
/*    don't need test */
    public void addRecord() {
        dealRecord.setId(1);
        dealRecord.setDealtime(date);
        dealRecord.setDealsize(20);
        dealRecord.setAskorderid("test-111");
        dealRecord.setBidorderid("test-222");
        dealRecord.setDealprice(1000.22);
        //  dealRecordService.addRecord(dealRecord);
    }

    @Test
    public void findAllRecord() {

        List<DealRecord> alldealrecord = dealRecordService.findAllRecord();
        for (int i = 0; i < alldealrecord.size(); i++) {
            System.out.println("the Record ID are " + alldealrecord.get(i).getId());
        }
    }
}