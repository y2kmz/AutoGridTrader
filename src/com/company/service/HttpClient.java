package com.company.service;

import com.company.TradeLoader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {
    private static Logger logger = LoggerFactory.getLogger(HttpClient.class);

    public static String requestGet(String url, HashMap<String, String> headerMap) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient client = HttpClients.createDefault();

        //设置header
        if(null != headerMap){
            for (Map.Entry<String, String> entry: headerMap.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }

        //定义接收数据
        String result = null;
        try {
            HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            logger.error("GET连接错误！", e);
        } finally {
            try {
                client.close();
            } catch (Exception e) {
                logger.error("关闭GET连接失败！", e);
            }
        }
        return result;
    }

    public static String requestPost(String url, HashMap<String, String> headerMap, String paramJson){
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient client = HttpClients.createDefault();

        //设置header
        if(null != headerMap){
            for (Map.Entry<String, String> entry: headerMap.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }

        //JOSN字符串 做 请求参数 封装
        StringEntity entity = new StringEntity(paramJson, "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        //定义接收数据
        String jsonResult = null;
        try {
            HttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                jsonResult = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            logger.error("POST连接错误！", e);
        } finally {
            try {
                client.close();
            } catch (Exception e) {
                logger.error("关闭POST连接失败！", e);
            }
        }
        return jsonResult;
    }

}
