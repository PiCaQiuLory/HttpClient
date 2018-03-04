package com.qinker.httpclient;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 
 * Copyright (c)   2018 by lory
 * @ClassName:     HttpGetRequestMethodExample.java
 * @Description:   使用Apache HttpClient 4.5发出Http GET请求。 Http GET方法表示指定资源的表示形式。
 *  这可能与获取HTML页面一样简单，或者使用JSON，XML等格式获取资源。使用HTTP GET请求方法的请求应该是幂等的，
 *  这意味着:这些应该只能检索数据并且应该没有其他效果。
 * 
 * @author:        lory
 * @version:       V1.0  
 * @Date:          2018年3月4日 下午3:03:00
 */
public class HttpGetRequestMethodExample {
	public static void main(String[] args) throws IOException {
		//使用try-with-resource
		try(CloseableHttpClient httpClient = HttpClients.createDefault()){
			HttpGet httpGet = new HttpGet("http://httpbin.org/get");
			System.out.println("Executing request "+httpGet.getRequestLine());
			
			//使用lambda表达式
			ResponseHandler<String> responseHandler = response -> {
				int status = response.getStatusLine().getStatusCode();
				if(status >= 200 && status<300) {
					HttpEntity entity = response.getEntity();
					return entity !=null? EntityUtils.toString(entity) : null;
				} else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
			};
			String responseBody = httpClient.execute(httpGet,responseHandler);
			System.out.println("-----------------------");
			System.out.println(responseBody);
		}
	}
}
