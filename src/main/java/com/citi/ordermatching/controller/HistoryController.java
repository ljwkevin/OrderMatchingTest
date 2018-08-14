package com.citi.ordermatching.controller;

import com.alibaba.fastjson.JSON;
import com.citi.ordermatching.domain.History;
import com.citi.ordermatching.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Dell on 2018/8/14.
 */
@RestController
@RequestMapping("history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @RequestMapping("findAllHistory")
    public String findAllHistory(){
        List<History> historyList=historyService.findAllHistory();
        String resultJSON= JSON.toJSONString(historyList);
        return resultJSON;
    }

}