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
 * 输入知乎问题ID可已得到指定问题个数下的所有图片
 * 使用了知乎的API得到一个问题ID下的所有图片
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

		// 创建用于存储图片路径的数组
		ArrayList<String> list = new ArrayList<>();

		// 判断存储路径是否存在，不存在便创建
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		// 多个回答的URL
		for (String url : HTMLList(URL)) {

			// 得到网页的源代码
			String html = GetHTML.getHTML(url);

			// 装换为文档对象
			Document document = Jsoup.parse(html);

			// 使用Jsoup
			Elements elements = document.select("img");

			// 筛选和记录符合要求的图片
			for (Element element : elements) {
				// 获的src下的链接
				String src = element.attr("src");

				// 筛选符合要求的图片
				if (src.indexOf(".jpg") != -1) {
					list.add(src);
				} else if (src.indexOf(".png") != -1) {
					list.add(src);
				} else if (src.indexOf(".gif") != -1) {
					list.add(src);
				}
			}

			// 创建可缓冲的线程池
			ExecutorService es = Executors.newCachedThreadPool();

			for (String pic : list) {
				es.execute(new Runnable() {
					public void run() {

						HttpGet get = new HttpGet(pic);

						try {
							HttpResponse response = httpClient.execute(get);
							HttpEntity en = response.getEntity();
							in = en.getContent();

							// 用apache的字符串工具得到文件名
							index++;
							String name = StringUtils.substringAfterLast(pic, ".");
							out = new FileOutputStream(new File(file, index + "." +name));

							// 用apache的IO流工具将远程资源复制到本地
							IOUtils.copy(in, out);
							System.out.println("图片：" + index + "完成！");
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
		System.out.println("图片下载完成！");

	}

	/**
	 * 得到所有100个回答的URL
	 * 
	 * @param URL
	 * @return
	 */
	private ArrayList<String> HTMLList(String URL) {

		ArrayList<String> list = new ArrayList<>();

		String id = StringUtils.substringAfterLast(URL, "/");
		int index = 0;

		while (true) {
			// 通过知乎的API找到一个问题ID下的所有回答ID
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
