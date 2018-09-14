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
 * ��ȡ��ҳԴ����
 * 
 * @author QQ546
 *
 */
public class GetHTML {

	// ����HttpClientʵ��
	static HttpClient httpClient = HttpClients.createDefault();

	public static String getHTML(String URL) {

		// �õ�get����
		HttpGet get = new HttpGet(URL);

		// �ر�������Ҫ
		get.setHeader(HttpHeaders.CONNECTION, "close");

		try {
			// ��Ӧ
			HttpResponse response = httpClient.execute(get);

			// �õ���Ӧ״̬��
			int code = response.getStatusLine().getStatusCode();

			// 200��ʾ�ɹ�
			if (code == 200) {

				// �õ�ҳ������
				HttpEntity en = response.getEntity();

				// ��ҳ�����ת��Ϊ�ַ���
				String htmlContent = EntityUtils.toString(en, "utf-8");

				return htmlContent;

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// �ر�����
			get.releaseConnection();
		}

		return "���HTMLԴ����";
	}

}
