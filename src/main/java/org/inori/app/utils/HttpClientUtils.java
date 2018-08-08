package org.inori.app.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author InoriHimea
 * @version 1.0
 * @date 2018/8/8 19:26
 * @since jdk1.8
 */
public class HttpClientUtils {

    private static RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(15000)
            .setConnectTimeout(15000)
            .setConnectionRequestTimeout(15000)
            .build();

    /**
     * 发送Get请求（不带参数）
     * @return
     */
    public static String doGet(String url) {
        HttpGet get = new HttpGet(url);
        get.setConfig(requestConfig);

        String response_str = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
               CloseableHttpResponse response = httpClient.execute(get)) {

            HttpEntity entity = response.getEntity();
            response_str = EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response_str;
    }

}
