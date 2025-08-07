package org.example.mobile.automation;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpUtils {

    public static String sendPost(String url, String body) {
        System.out.println(url + " " + body);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            
            StringEntity entity = new StringEntity(body, "UTF-8");
            httpPost.setEntity(entity);
            
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    String res = EntityUtils.toString(responseEntity, "UTF-8");
                    System.out.println(res);
                    return res;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("POST request failed: " + e.getMessage(), e);
        }
        return "";
    }

    public static String sendGet(String url) {
        System.out.println(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String res = EntityUtils.toString(entity, "UTF-8");
                    if(res.length() < 10240){
                        System.out.println(res);
                    }
                    return res;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("GET request failed: " + e.getMessage(), e);
        }
        return "";
    }
}