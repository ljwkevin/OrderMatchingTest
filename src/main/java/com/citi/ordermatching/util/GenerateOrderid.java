package com.citi.ordermatching.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GenerateOrderid {
    public static String generateOrderid(Date date, int traderId){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
        String orderId = sdf.format(date) + "_" + traderId;
        return orderId;
    }

}
