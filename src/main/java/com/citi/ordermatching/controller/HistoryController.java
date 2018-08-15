package com.citi.ordermatching.controller;

import com.alibaba.fastjson.JSON;
import com.citi.ordermatching.domain.History;
import com.citi.ordermatching.service.DealRecordService;
import com.citi.ordermatching.service.HistoryService;
import com.citi.ordermatching.service.OrderbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;

/**
 * Created by Dell on 2018/8/14.
 */
@RestController
@RequestMapping("history")
@CrossOrigin
public class HistoryController {

    @Autowired
    private HistoryService historyService;





    @RequestMapping("findAllHistory")
    public String findAllHistory(){

        String resultJSON= historyService.findAllHistory();
        return resultJSON;
    }

    @RequestMapping("cancel")
    public boolean cancelOrder(@PathParam("id") int historyId){
        History history = historyService.findById(historyId);
        return historyService.cancelOrder(history);
    }


}
