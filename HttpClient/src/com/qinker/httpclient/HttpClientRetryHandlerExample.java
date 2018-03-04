package com.qinker.httpclient;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 
 * Copyright (c)   2018 by lory
 * @ClassName:     HttpClientRetryHandlerExample.java
 * @Description:   创建自定义HttpRequestRetryHandler以启用自定义异常恢复机制。 当使用这个接口时，
 * 需要实现retryRequest方法。 这使我们能够定义一个自定义的重试计数机制和异常恢复机制。
 * 
 * @author:        lory
 * @version:       V1.0  
 * @Date:          2018年3月4日 下午4:02:31
 */
public class HttpClientRetryHandlerExample {
	public static void main(String[] args) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setRetryHandler(retryHandler())
				.build();
		try {
            HttpGet httpget = new HttpGet("http://localhost:1234");
            System.out.println("Executing request " + httpget.getRequestLine());
            httpClient.execute(httpget);
            System.out.println("----------------------------------------");
            System.out.println("Request finished");
        } finally {
        	httpClient.close();
        }
	}
	
	private static HttpRequestRetryHandler retryHandler() {
		return (exception, executionCount, context) ->{
			System.out.println("try request: " + executionCount);
			
			if(executionCount >= 5) {
				return false;
			}
			if (exception instanceof InterruptedIOException) {
                // Timeout
                return false;
            }
            if (exception instanceof UnknownHostException) {
                // Unknown host
                return false;
            }
            if (exception instanceof SSLException) {
                // SSL handshake exception
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if(idempotent) {
            	return true;
            }
            return false;
            
		};
	}
}
