package com.jd.seckill;

import com.alibaba.fastjson.JSONObject;
import com.sun.webkit.network.CookieManager;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Start {

    final static String headerAgent = "User-Agent";
    final static String headerAgentArg = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36";
    final static String Referer = "Referer";
    final static String RefererArg = "https://passport.jd.com/new/login.aspx";
    //茅台 100012043978 双手柄 100021367452 单手柄 100019378198
    //显卡 100011553443 100024794564
    static String pid = new Scanner(System.in).nextLine();
    static String eid = "X";
    static String fp = "X";
    volatile static Integer ok = new Scanner(System.in).nextInt();
    static CookieManager manager = new CookieManager();

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException, ParseException {
        CookieHandler.setDefault(manager);
        Login.login();
        judgePruchase();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1000, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        for (int i = 0; i < 5; i++) {
            threadPoolExecutor.execute(new RushToPurchase());
        }
        new RushToPurchase().run();
    }

    public static void judgePruchase() throws IOException, ParseException, InterruptedException {
        JSONObject headers = new JSONObject();
        headers.put(Start.headerAgent, Start.headerAgentArg);
        headers.put(Start.Referer, Start.RefererArg);
        String str = HttpUrlConnectionUtil.get(headers, "https://item-soa.jd.com/getWareBusiness?skuId=" + pid);
        JSONObject shopDetail = JSONObject.parseObject(str);
        if (shopDetail.get("yuyueInfo") != null) {
            String buyDate = JSONObject.parseObject(shopDetail.get("yuyueInfo").toString()).get("buyTime").toString();
            String startDate = buyDate.split("-202")[0] + ":00";
            System.out.println("抢购时间为：" + startDate);
            long startTime = HttpUrlConnectionUtil.dateToTime(startDate);
            boolean needWait = true;
            while (needWait) {
                JSONObject jdTime = JSONObject.parseObject(HttpUrlConnectionUtil.get(headers, "https://api.m.jd.com/client.action?functionId=queryMaterialProducts&client=wh5"));
                long serverTime = Long.parseLong(jdTime.get("currentTime2").toString());
                if (startTime - serverTime > 5 * 60 * 1000) {
                    System.out.println("剩余时间大于5分钟，长等待");
                    Thread.sleep(60 * 1000);
                } else if (startTime - serverTime > 60 * 1000) {
                    System.out.println("剩余时间大于1分钟，等待");
                    Thread.sleep(1000);
                } else if (startTime - serverTime > 0){
                    System.out.println("剩余时间小于1分钟，短等待");
                    Thread.sleep(50);
                }else {
                    needWait = false;
                }
            }
        }
    }

}
