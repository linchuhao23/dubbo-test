package com.lin;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.Builder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SolrTest {

	private SolrClient solrClient;

	@Before
	public void before() {
		try {
			HttpSolrClient httpSolrClient = new Builder("http://127.0.0.1:8080/solr/core1").build();
			httpSolrClient.setConnectionTimeout(30000);
			httpSolrClient.setDefaultMaxConnectionsPerHost(100);
			httpSolrClient.setMaxTotalConnections(100);
			httpSolrClient.setSoTimeout(30000);

			this.solrClient = httpSolrClient;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@After
	public void after() {
		try {
			solrClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void add() {
		List<Doc> docs = new ArrayList<>();
		for (int i = 0; i < 10; ++i) {
			Doc doc = new Doc();
			doc.setId(UUID.randomUUID().toString());
			doc.setContent(generateChinese(20));
			docs.add(doc);
		}
		try {
			this.solrClient.addBeans(docs);
			this.solrClient.commit();
		} catch (SolrServerException | IOException e) {
			try {
				this.solrClient.rollback();
			} catch (SolrServerException | IOException e1) {
				e1.printStackTrace();
			}
		} finally {

		}
	}

	@Test
	public void query() throws SolrServerException, IOException {
		SolrQuery params = new SolrQuery("*:*");
		List<Doc> docs = solrClient.query(params).getBeans(Doc.class);
		if (docs != null) {
			docs.forEach(System.out::println);
		}
	}
	
	@Test
	public void update() throws SolrServerException, IOException {
		SolrQuery params = new SolrQuery("*:*");
		List<Doc> docs = solrClient.query(params).getBeans(Doc.class);
		if (docs != null) {
			docs.forEach(e -> {
				e.setContent(e.getContent() + " -> 修改过的");
			});
			this.solrClient.addBeans(docs);
			this.solrClient.commit();
		}
	}

	@Test
	public void deleteAll() throws SolrServerException, IOException {
		// this.solrClient.deleteByQuery("*:*");
		// this.solrClient.commit();
	}

	public static String generateChinese(int len) {
		if (len < 1) {
			throw new RuntimeException("len must greater then one");
		}
		StringBuilder builder = new StringBuilder(len);
		int hightPos, lowPos; // 定义高低位
		Random random = new Random();
		try {
			for (int i = 0; i < len; ++i) {
				hightPos = (176 + Math.abs(random.nextInt(39)));// 获取高位值
				lowPos = (161 + Math.abs(random.nextInt(93)));// 获取低位值
				byte[] b = new byte[2];
				b[0] = (new Integer(hightPos).byteValue());
				b[1] = (new Integer(lowPos).byteValue());
				builder.append(new String(b, "GBK"));
			}
		} catch (Exception e) {
			
		}
		return builder.toString();
	}

	public static void main(String[] args) {
		System.out.println(generateChinese(10));
	}

	public static class Doc implements Serializable {

		private static final long serialVersionUID = 1L;

		@Field
		private String id;

		@Field
		private String content;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		@Override
		public String toString() {
			return "{id:" + id + ", content:" + content + "}";
		}

	}

}
