package com.lx.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * ����֪������ID���ѵõ�ָ����������µ�����ͼƬ
 * ʹ����֪����API�õ�һ������ID�µ�����ͼƬ
 * 
 * @author QQ546
 *
 */
public class GetPic {

	HttpClient httpClient = HttpClients.createDefault();
	InputStream in = null;
	OutputStream out = null;
	int index = 0;

	public void getPic(int ID, String path) {

		String URL = "https://www.zhihu.com/question/" + ID;

		// �������ڴ洢ͼƬ·��������
		ArrayList<String> list = new ArrayList<>();

		// �жϴ洢·���Ƿ���ڣ������ڱ㴴��
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		// ����ش��URL
		for (String url : HTMLList(URL)) {

			// �õ���ҳ��Դ����
			String html = GetHTML.getHTML(url);

			// װ��Ϊ�ĵ�����
			Document document = Jsoup.parse(html);

			// ʹ��Jsoup
			Elements elements = document.select("img");

			// ɸѡ�ͼ�¼����Ҫ���ͼƬ
			for (Element element : elements) {
				// ���src�µ�����
				String src = element.attr("src");

				// ɸѡ����Ҫ���ͼƬ
				if (src.indexOf(".jpg") != -1) {
					list.add(src);
				} else if (src.indexOf(".png") != -1) {
					list.add(src);
				} else if (src.indexOf(".gif") != -1) {
					list.add(src);
				}
			}

			// �����ɻ�����̳߳�
			ExecutorService es = Executors.newCachedThreadPool();

			for (String pic : list) {
				es.execute(new Runnable() {
					public void run() {

						HttpGet get = new HttpGet(pic);

						try {
							HttpResponse response = httpClient.execute(get);
							HttpEntity en = response.getEntity();
							in = en.getContent();

							// ��apache���ַ������ߵõ��ļ���
							index++;
							String name = StringUtils.substringAfterLast(pic, ".");
							out = new FileOutputStream(new File(file, index + "." +name));

							// ��apache��IO�����߽�Զ����Դ���Ƶ�����
							IOUtils.copy(in, out);
							System.out.println("ͼƬ��" + index + "��ɣ�");
							System.out.println(pic);

						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if (out != null) {
								try {
									out.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							if (in != null) {
								try {
									in.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}

					}
				});
			}
			es.shutdown();
		}
		System.out.println("ͼƬ������ɣ�");

	}

	/**
	 * �õ�����100���ش��URL
	 * 
	 * @param URL
	 * @return
	 */
	private ArrayList<String> HTMLList(String URL) {

		ArrayList<String> list = new ArrayList<>();

		String id = StringUtils.substringAfterLast(URL, "/");
		int index = 0;

		while (true) {
			// ͨ��֪����API�ҵ�һ������ID�µ����лش�ID
			String url = "https://www.zhihu.com/api/v4/questions/" + id + "/answers?limit=1&offset=" + index;

			String json = GetHTML.getHTML(url);

			JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
			JsonArray data = obj.get("data").getAsJsonArray();
			JsonObject object = data.get(0).getAsJsonObject();

			String path = "https://www.zhihu.com/question/" + id + "/answer/" + object.get("id");

			System.out.println(path);
			index++;

			list.add(path);

			if (index > 50) {
				break;
			}

		}

		return list;

	}

}
