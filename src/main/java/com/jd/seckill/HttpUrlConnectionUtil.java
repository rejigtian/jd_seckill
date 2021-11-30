package com.jd.seckill;

import com.alibaba.fastjson.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpUrlConnectionUtil {

    public static String get(JSONObject headers, String url) throws IOException {
        StringBuilder response = new StringBuilder();
        HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(url).openConnection());
        httpURLConnection.setRequestMethod("GET");
        if (headers != null) {
            for (String headerName : headers.keySet()) {
                httpURLConnection.setRequestProperty(headerName, headers.get(headerName).toString());
            }
        }
        httpURLConnection.connect();
        if (httpURLConnection.getResponseCode() == 200) {
            InputStream inputStream = httpURLConnection.getInputStream();
            byte[] buffer;
            buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                response.append(new String(buffer, 0, length, StandardCharsets.UTF_8));
            }
            httpURLConnection.disconnect();
        }
        return response.toString();
    }

    public static String post(JSONObject headers, String url, JSONObject params) throws IOException {
        StringBuilder response = new StringBuilder();
        HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(url).openConnection());
        httpURLConnection.setRequestMethod("POST");
        if (headers != null) {
            for (String headerName : headers.keySet()) {
                httpURLConnection.setRequestProperty(headerName, headers.get(headerName).toString());
            }
        }
        httpURLConnection.setDoOutput(true);
        httpURLConnection.connect();
        if (params != null) {
            httpURLConnection.getOutputStream().write(params.toJSONString().getBytes(StandardCharsets.UTF_8));
        }
        httpURLConnection.getInputStream();
        if (httpURLConnection.getResponseCode() == 200) {
            InputStream inputStream = httpURLConnection.getInputStream();
            byte[] buffer;
            buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                response.append(new String(buffer, 0, length, StandardCharsets.UTF_8));
            }
            httpURLConnection.disconnect();
        }
        httpURLConnection.disconnect();
        return response.toString();
    }

    public static void getQCode(JSONObject headers, String url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(url).openConnection());
        httpURLConnection.setRequestMethod("GET");
        if (headers != null) {
            for (String headerName : headers.keySet()) {
                httpURLConnection.setRequestProperty(headerName, headers.get(headerName).toString());
            }
        }
        httpURLConnection.connect();
        if (httpURLConnection.getResponseCode() == 200) {
            InputStream inputStream = httpURLConnection.getInputStream();
            OutputStream outputStream = new FileOutputStream("QCode.png");
            byte[] buffer;
            int length;
            buffer = new byte[1024];
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            httpURLConnection.disconnect();
        }
    }

    public static Long dateToTime(String date) throws ParseException {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date data = sdfTime.parse(date);
        return data.getTime();
    }

}
