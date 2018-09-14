package com.lx.crawler;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 获取网页源代码
 * 
 * @author QQ546
 *
 */
public class GetHTML {

	// 创建HttpClient实例
	static HttpClient httpClient = HttpClients.createDefault();

	public static String getHTML(String URL) {

		// 得到get请求
		HttpGet get = new HttpGet(URL);

		// 关闭连接需要
		get.setHeader(HttpHeaders.CONNECTION, "close");

		try {
			// 响应
			HttpResponse response = httpClient.execute(get);

			// 得到响应状态码
			int code = response.getStatusLine().getStatusCode();

			// 200表示成功
			if (code == 200) {

				// 得到页面内容
				HttpEntity en = response.getEntity();

				// 将页面对象转换为字符串
				String htmlContent = EntityUtils.toString(en, "utf-8");

				return htmlContent;

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			get.releaseConnection();
		}

		return "获得HTML源代码";
	}

}
