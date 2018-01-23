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
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.common.params.GroupParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SolrTest {

	private SolrClient solrClient;

	@Before
	public void before() {
		try {
			HttpSolrClient httpSolrClient = new Builder("http://192.168.1.102:8984/solr").build();
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

	// @Test
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

	// @Test
	public void query() throws SolrServerException, IOException {
		SolrQuery params = new SolrQuery("*:*");
		List<Doc> docs = solrClient.query(params).getBeans(Doc.class);
		if (docs != null) {
			docs.forEach(System.out::println);
		}
	}

	// @Test
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

	// @Test
	public void deleteAll() throws SolrServerException, IOException {
		// this.solrClient.deleteByQuery("*:*");
		// this.solrClient.commit();
	}

	@Test
	public void facetCount() throws SolrServerException, IOException {
		SolrQuery solrQuery = new SolrQuery("*:*");
		solrQuery.setFacet(true);
		solrQuery.setRows(0);
		solrQuery.addFacetField("eventTag", "status", "tradeTag", "valueIndex");
		List<FacetField> facetFields = solrClient.query(solrQuery).getFacetFields();
		for (FacetField f : facetFields) {
			List<Count> values = f.getValues();
			System.out.println("-------------------------------------");
			System.out.println(f.getName() + ", " + f.getValueCount());
			for (Count c : values) {
				System.out.println(c.getName() + ", " + c.getCount());
			}
			System.out.println("--------------------------------------");
		}
	}

	@Test
	public void groupCount() throws SolrServerException, IOException {
		SolrQuery solrQuery = new SolrQuery("*:*");
		solrQuery.setParam(GroupParams.GROUP, true);
		solrQuery.setParam(GroupParams.GROUP_FIELD, "valueIndex", "status");
		solrQuery.setParam(GroupParams.GROUP_FACET, true);
		solrQuery.setParam(GroupParams.GROUP_TOTAL_COUNT, true);
		//solrQuery.setParam(GroupParams.GROUP_QUERY, "valueIndex:3");
		// 设置每个quality对应的
		solrQuery.setParam(GroupParams.GROUP_LIMIT, "1");
		solrQuery.setParam(GroupParams.GROUP_FUNC, "exists(expertComments)", "exists(eventTag)");
		// 设置返回doc文档数据，因只需要数量，故设置为0
		//solrQuery.setRows(10);
		List<GroupCommand> values = solrClient.query(solrQuery).getGroupResponse().getValues();
		for (GroupCommand g : values) {
			System.out.println("----------------------------------------");
			System.out.println(g.getName() + "," + g.getMatches() + ", " + g.getNGroups() + ", " + g.getValues().size());
			for (Group e : g.getValues()) {
				System.out.println(e.getGroupValue() + ", " + e.getResult().getNumFound());
			}
			System.out.println("----------------------------------------");
		}
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
