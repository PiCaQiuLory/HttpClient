package com.qinker.httpclient;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLSession;

import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

/**
 * HTTP获取服务器证书示例
 * Copyright (c)   2018 by lory
 * @ClassName:     HttpClientGetServerCertificate.java
 * @Description:   我们向https://www.baidu.com发出请求以获取服务器证书。 首先，我们创建一个HttpResponseInterceptor，
 * 它将从SSLSession中读取证书(如果存在)，并将证书添加到HttpContext中，以便稍后用于处理。接下来，创建一个自定义的HttpClient并使用
 * addInterceptorLast()工厂方法添加拦截器。最后，向资源服务器发出一个HttpGet请求，并从HttpContext获取服务器证书，
 * 我们之前将它们放在这里。 现在拥有了证书，然后遍历集合并将一些数据打印到控制台。 
 * 
 * @author:        lory
 * @version:       V1.0  
 * @Date:          2018年3月4日 下午3:45:24
 */
public class HttpClientGetServerCertificate {

	public static final String PEER_CERTIFICATES = "PEER_CERTIFICATES ";

	public static void main(String[] args) throws ClientProtocolException, IOException {
		HttpResponseInterceptor certificateInterceptor = (httpResponse, context) -> {
			ManagedHttpClientConnection routedConnection = (ManagedHttpClientConnection) context
					.getAttribute(HttpCoreContext.HTTP_CONNECTION);
			SSLSession sslSession = routedConnection.getSSLSession();
			if (sslSession != null) {
				Certificate[] certificates = sslSession.getPeerCertificates();
				context.setAttribute(PEER_CERTIFICATES, certificates);
			}

		};
		CloseableHttpClient httpClient = HttpClients.custom().addInterceptorLast(certificateInterceptor).build();
		try {
			HttpGet httpget = new HttpGet("https://www.baidu.com");
			System.out.println("Executing request " + httpget.getRequestLine());

			HttpContext context = new BasicHttpContext();
			httpClient.execute(httpget, context);
			Certificate[] peerCertificates = (Certificate[]) context.getAttribute(PEER_CERTIFICATES);
			for (Certificate certificate : peerCertificates) {
				X509Certificate real = (X509Certificate) certificate;
				System.out.println("----------------------------------------");
				System.out.println("Type: " + real.getType());
				System.out.println("Signing Algorithm: " + real.getSigAlgName());
				System.out.println("IssuerDN Principal: " + real.getIssuerX500Principal());
				System.out.println("SubjectDN Principal: " + real.getSubjectX500Principal());
				System.out.println("Not After: " + DateUtils.formatDate(real.getNotAfter(), "dd-MM-yyyy"));
				System.out.println("Not Before: " + DateUtils.formatDate(real.getNotBefore(), "dd-MM-yyyy"));
			}
		} finally {
			httpClient.close();
		}

	}

}
