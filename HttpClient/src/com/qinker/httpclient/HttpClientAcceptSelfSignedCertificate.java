package com.qinker.httpclient;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

/**
 * HttpClient忽略证书错误
 * 引言：通常，开发人员将在本地机器上或项目的开发阶段使用自签名证书。 默认情况下，HttpClient(和Web浏览器)不会接受不可信的连接。 
 * 但是，可以配置HttpClient以允许不可信的自签名证书。注意:这是一个可能存在安全风险，因为您将其用于生产时，基本上会禁用所有的认证检查，
 * 这可通导致受到攻击。在这个例子中，我们演示了如何忽略Apache HttpClient 4.5中的SSL / TLS证书错误。
 * Copyright (c)   2018 by lory
 * @ClassName:     HttpClientAcceptSelfSignedCertificate.java
 * @Description:   我们配置一个自定义的HttpClient。 首先使用SSLContextBuilder设置SSLContext并使用
 * TrustSelfSignedStrategy类来允许自签名证书。 使用NoopHostnameVerifier本质上关闭主机名验证。
 *  创建SSLConnectionSocketFactory并传入SSLContext和HostNameVerifier，并使用工厂方法构建HttpClient。
 * 
 * @author:        lory
 * @version:       V1.0  
 * @Date:          2018年3月4日 下午3:48:09
 */
public class HttpClientAcceptSelfSignedCertificate {

	public static void main(String[] args) {
		try (CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient()) {
            HttpGet httpget = new HttpGet("https://www.yiibai.com");
            System.out.println("Executing request " + httpget.getRequestLine());

            httpclient.execute(httpget);
            System.out.println("----------------------------------------");
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | IOException e) {
            throw new RuntimeException(e);
        }
	}
	
	private static CloseableHttpClient createAcceptSelfSignedCertificateClient() 
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		
		//使用TrustSelfSignedStrategy来允许自定义证书
		SSLContext context = SSLContextBuilder
				.create()
				.loadTrustMaterial(new TrustSelfSignedStrategy())
				.build();
		
		//我们可以选择禁用主机名验证
		//如果你不想进一步削弱安全性，你可以不必包含这个。
		HostnameVerifier hostnameVerifier = new NoopHostnameVerifier();
		
		//创建一个SSL套接字工厂使用SSLContext使用信任的自签名证书策略并允许所有主机验证程序
		SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(context, hostnameVerifier);
		
		//最后创建使用HttpClient工厂方法HttpClient指定SSL套接字工厂
		return HttpClients
				.custom()
				.setSSLSocketFactory(factory)
				.build();
	}
}
