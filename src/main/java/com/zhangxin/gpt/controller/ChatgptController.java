package com.zhangxin.gpt.controller;

import com.zhangxin.gpt.entity.ChatgptDto;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author zhangxin
 * @date 2023-05-30 15:52
 */
@RestController
public class ChatgptController {


    @RequestMapping("/chatgpt")
    public  String  test(@RequestBody ChatgptDto question) throws IOException {
        String responseBody="";
        // 创建CloseableHttpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // ChatGPT演示服务器的API端点
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        // 设置请求头部信息
        HttpPost httpPost = new HttpPost(apiUrl);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer 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"); // 请将YOUR_API_KEY替换为你的OpenAI API密钥

        // 设置请求体内容
        // String requestBody = "{\"model\": \"gpt-3.5-turbo\",\"messages\":[{\"role\":\"system\",\"content\":\"You are a helpful assistant.\"},{\"role\":\"user\",\"content\":\"世界上最高峰是什么\"}]}";
        String requestBody = "{\"model\": \"gpt-3.5-turbo\",\"messages\":[{\"role\":\"system\",\"content\":\"You are a helpful assistant.\"},{\"role\":\"user\",\"content\":\"" + question.getQuestion() + "\"}]}";
        StringEntity entity = new StringEntity(requestBody, "UTF-8");
        httpPost.setEntity(entity);

        // 配置代理
        HttpHost proxy = new HttpHost("127.0.0.1", 7890); // 请将YOUR_PROXY_HOST和YOUR_PROXY_PORT替换为代理服务器的主机名和端口号
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
        httpPost.setConfig(config);

//        // 创建 CloseableHttpClient 实例，并设置请求配置
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//
//        // 设置请求头部信息
//        HttpPost httpPost = new HttpPost("https://api.openai.com/v1/completions");
//        httpPost.setHeader("Content-Type", "application/json");
//        httpPost.setHeader("Authorization", "Bearer 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"); // 请将YOUR_API_KEY替换为你的OpenAI API密钥
//        // 配置代理
//        HttpHost proxy = new HttpHost("127.0.0.1", 7890); // 请将YOUR_PROXY_HOST和YOUR_PROXY_PORT替换为代理服务器的主机名和端口号
//        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
//        httpPost.setConfig(config);
//        // 设置请求体内容
//        String requestBody = "{\"model\": \"gpt-3.5-turbo\",\"messages\":[{\"role\":\"system\",\"content\":\"You are a helpful assistant.\"},{\"role\":\"user\",\"content\":\"" + question.getQuestion() + "\"}]}";
//        System.out.println(requestBody);
//        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
//        httpPost.setEntity(stringEntity);

        try {
            // 发送请求并获取响应
            CloseableHttpResponse response = httpClient.execute(httpPost);

            // 解析响应
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                 responseBody = EntityUtils.toString(responseEntity);
                System.out.println(responseBody);
            }

            // 关闭响应和HttpClient
            response.close();
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        CloseableHttpResponse response = httpClient.execute(httpPost);
//        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//            String jsonStr = EntityUtils.toString(response.getEntity());
//            AIAnswer aiAnswer = JSON.parseObject(jsonStr, AIAnswer.class);
//            StringBuilder answers = new StringBuilder();
//            List<Choices> choices = aiAnswer.getChoices();
//            for (Choices choice : choices) {
//                answers.append(choice.getText());
//            }
//            return answers.toString();
//        } else {
//            throw new RuntimeException("api.openai.com Err Code is " + response.getStatusLine().getStatusCode());
//        }

        return responseBody;
    }

    public static void main(String[] args) {
        // 创建CloseableHttpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // ChatGPT演示服务器的API端点
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        // 设置请求头部信息
        HttpPost httpPost = new HttpPost(apiUrl);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer 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"); // 请将YOUR_API_KEY替换为你的OpenAI API密钥

        // 设置请求体内容
       // String requestBody = "{\"model\": \"gpt-3.5-turbo\",\"messages\":[{\"role\":\"system\",\"content\":\"You are a helpful assistant.\"},{\"role\":\"user\",\"content\":\"世界上最高峰是什么\"}]}";
        String requestBody="{\"model\": \"gpt-3.5-turbo\",\"messages\":[{\"role\":\"system\",\"content\":\"You are a helpful assistant.\"},{\"role\":\"user\",\"content\":\"世界上最高峰是什么\"}]}";
        StringEntity entity = new StringEntity(requestBody, "UTF-8");
        httpPost.setEntity(entity);

        // 配置代理
        HttpHost proxy = new HttpHost("127.0.0.1", 7890); // 请将YOUR_PROXY_HOST和YOUR_PROXY_PORT替换为代理服务器的主机名和端口号
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
        httpPost.setConfig(config);

        try {
            // 发送请求并获取响应
            CloseableHttpResponse response = httpClient.execute(httpPost);

            // 解析响应
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println(responseBody);
            }

            // 关闭响应和HttpClient
            response.close();
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
