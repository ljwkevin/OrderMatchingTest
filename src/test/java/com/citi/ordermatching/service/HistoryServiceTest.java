package com.citi.ordermatching.service;

import com.citi.ordermatching.dao.HistoryMapper;
import com.citi.ordermatching.domain.History;
import com.citi.ordermatching.domain.RecordOrder;
import com.citi.ordermatching.domain.User;
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
public class HistoryServiceTest {
    @Autowired
    private HistoryService historyService;
    private HistoryMapper historyMapper;
    private RecordOrder recordOrder;
    private UserService userService;
    History history = new History();
    Date date = new Date();
    User user = new User();

    @Test
    public void insertHistory() {
        //history.setId(83);    这一行不用，数据库设置了主键自增
        history.setCommittime(date);
        history.setTraderid(1);
        history.setSymbol("IBM");
        history.setStatus("FINISHED");
        history.setCommittime(date);
        history.setType("ASK");
        history.setSize(10);
        history.setStrategy("Matching");
        history.setPrice(2000.00);
        history.setDuration(2);
        history.setFok("have");
        //orderid字段或者说属性的生成方法：
        //com.citi.ordermatching.util.GenerateOrderid
        //    public static String generateOrderid(Date date, int traderId)
        //history.setOrderid(String.valueOf(3));
        history.setOrderid("20808170533100023_1");
        historyService.insertHistory(history);
        System.out.println("id is" + history.getId());
        System.out.println("type is " + history.getType());
        System.out.println("time is " + history.getCommittime());
    }

    @Test
    public void findById() {
        history.setId(2);
        History his = historyService.findById(history.getId());
        System.out.println(history.getType());
    }

    @Test
    public void findAllHistory() {
        user.setId(6);
        String findhistory = historyService.findAllHistory(user.getId());
        System.out.println("the all history is " + findhistory);

    }

    @Test
    public void cancelOrder() {
        //history.setId(3);
        historyService.cancelOrder(history);
        assertEquals(history.getStatus(), "CANCELLED");

    }
}