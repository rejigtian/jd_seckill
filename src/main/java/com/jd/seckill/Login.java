package com.jd.seckill;

import com.alibaba.fastjson.JSONObject;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Login {

    static Map<String, List<String>> requestHeaders = new HashMap<>(16);
    static String ticket = "";

    public static void login() throws IOException, URISyntaxException, InterruptedException {
        JSONObject headers = new JSONObject();
        headers.put(Start.headerAgent, Start.headerAgentArg);
        headers.put(Start.Referer, Start.RefererArg);
        long now = System.currentTimeMillis();
        HttpUrlConnectionUtil.getQCode(headers, "https://qr.m.jd.com/show?appid=133&size=147&t=" + now);
        Runtime.getRuntime().exec("open -a /System/Applications/Preview.app QCode.png");
        URI url = new URI("https://qr.m.jd.com/show?appid=133&size=147&t=" + now);
        Map<String, List<String>> stringListMap;
        stringListMap = Start.manager.get(url, requestHeaders);
        List<String> cookieList = stringListMap.get("Cookie");
        String cookies = cookieList.get(0);
        String token = cookies.split("wlfstk_smdl=")[1];
        headers.put("Cookie", cookies);
        boolean needWait = true;
        while (needWait) {
            String checkUrl = "https://qr.m.jd.com/check?appid=133&callback=jQuery" + (int) ((Math.random() * (9999999 - 1000000 + 1)) + 1000000) + "&token=" + token + "&_=" + System.currentTimeMillis();
            String qrCode = HttpUrlConnectionUtil.get(headers, checkUrl);
            if (qrCode.contains("二维码未扫描")) {
                System.out.println("二维码未扫描，请扫描二维码登录");
            } else if (qrCode.contains("请手机客户端确认登录")) {
                System.out.println("请手机客户端确认登录");
            } else {
                ticket = qrCode.split("\"ticket\" : \"")[1].split("\"\n" +
                        "}\\)")[0];
                System.out.println("已完成二维码扫描登录");
                close();
                needWait = false;
            }
            Thread.sleep(3000);
        }
        String qrCodeTicketValidation = HttpUrlConnectionUtil.get(headers, "https://passport.jd.com/uc/qrCodeTicketValidation?t=" + ticket);
        System.out.println(qrCodeTicketValidation);
        stringListMap = Start.manager.get(url, requestHeaders);
        cookieList = stringListMap.get("Cookie");
        cookies = cookieList.get(0);
        headers.put("Cookie", cookies);
    }

    public static void close() throws IOException {
        int platform = BaseUtil.getCurrentSystem();
        if (platform == BaseUtil.MAC_OS) {
            Runtime.getRuntime().exec("pkill -9 Preview");
        } else if (platform == BaseUtil.WINDOWS_OS) {
            final User32 user32 = User32.INSTANCE;
            user32.EnumWindows((hWnd1, arg1) -> {
                char[] windowText = new char[512];
                user32.GetWindowText(hWnd1, windowText, 512);
                String wText = Native.toString(windowText);
                if (wText.isEmpty()) {
                    return true;
                }
                if (wText.contains("照片")) {
                    hWnd1 = User32.INSTANCE.FindWindow(null, wText);
                    User32.INSTANCE.SendMessage(hWnd1, 0X10, null, null);
                }
                return true;
            }, null);
        } else {
            System.out.println("暂不支持linux或unix");
        }
    }

}
