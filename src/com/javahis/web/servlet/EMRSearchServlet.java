package com.javahis.web.servlet;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.javahis.web.bean.DropDownList;
import com.javahis.web.bean.EMRMetaDataDO;
import com.javahis.web.bean.EMRSearchResultVO;
import com.javahis.web.bean.EMRTableDO;
import com.javahis.web.bean.Page;
import com.javahis.web.jdo.EMRSearchTool;
import com.javahis.web.lucene.search.DocSearchHelper;
import com.javahis.web.util.JsonUtils;

/**
 * <p>
 * Title: ȫ�ļ����Ŀ�����
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @li.xiang1979@gmail.com
 * @version 1.0
 */
public class EMRSearchServlet extends HttpServlet {
	/**
	 * ���Կ���
	 */
	private boolean isDebug = false;
	
	private static int PAGESIZE = 15;

	/**
	 * ��Ӧ���񷽷�
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// ���õķ�������
		String flag = request.getParameter("method");
		try {
			Method method = this.getClass().getDeclaredMethod(
					flag,
					new Class[] { HttpServletRequest.class,
							HttpServletResponse.class });
			method.invoke(this, new Object[] { request, response });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����ҳ���ʼ��
	 */
	public void init(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// ���س�ʼ����;
		// ��ѯʱ��
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		// ��ǰʱ��
		String strEndDate = df.format(new Date());
		// ���µ�һ��
		String strStartDate = strEndDate.substring(0, 8) + "01";
		request.setAttribute("startDate", strStartDate);
		request.setAttribute("endDate", strEndDate);
		// ��ǰ����;
		List<DropDownList> regions = EMRSearchTool.getInstance()
				.getAllRegionList();

		request.setAttribute("regionList", this.buildSelect(regions));

		request.getRequestDispatcher("jsp/emr/EMRSearch.jsp").forward(request,
				response);
	}

	/**
	 * ����select
	 * 
	 * @param dropDownList
	 *            List
	 * @return String
	 */
	private String buildSelect(List<DropDownList> dropDownList) {
		StringBuilder strSelect = new StringBuilder();
		strSelect.append("<option value=''>----��ѡ��----</option>");
		for (int i = 0; i < dropDownList.size(); i++) {
			strSelect.append("<option value='" + dropDownList.get(i).getValue()
					+ "'>");
			strSelect.append(dropDownList.get(i).getTitle());
			strSelect.append("</option>");
		}
		return strSelect.toString();
	}

	/**
	 * ͨ�������ò���
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	public void getDeptsByRegion(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String regionCode = request.getParameter("regionCode");
		List<DropDownList> depts = EMRSearchTool.getInstance()
				.getDeptsByRegion(regionCode);
		this.buildSelect(depts);
		this.renderText(response, this.buildSelect(depts));
	}

	/**
	 * ͨ�����Ż�ò���
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param respons
	 *            HttpServletResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	public void getStationsByDept(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String deptCode = request.getParameter("deptCode");
		List<DropDownList> depts = EMRSearchTool.getInstance()
				.getStationByDept(deptCode);
		this.buildSelect(depts);
		this.renderText(response, this.buildSelect(depts));
	}

	/**
	 * ȫ�ļ���
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doSearch(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Ԫ�ر��룻
		String metaCode[] = request.getParameterValues("metaCode");
		// Ԫ�����ݣ�
		// String metaData[]=request.getParameterValues("metaData");
		// ������
		String op[] = request.getParameterValues("op");
		// ��ѯֵ;
		String searchValue[] = request.getParameterValues("searchValue");
		// ���ӹ�ϵ;
		String join[] = request.getParameterValues("join");

		String forms = request.getParameter("checkedEMRForms");

		String emrForms[] = null;
		if (!forms.equals("")) {
			emrForms = request.getParameter("checkedEMRForms").split(",");

		}
		//
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		;
		String regionCode = request.getParameter("regionCode");
		;
		String deptCode = request.getParameter("deptCode");
		;
		String stationCode = request.getParameter("stationCode");
		// ���˵Ļ�����ѯ,�����Ӧ��MR_NO��
		TParm result = EMRSearchTool.getInstance().baseQuery(startDate,
				endDate, regionCode, deptCode, stationCode);

		// 2.����Lucene����xmlԪ��������
		Map<String, Object> returnData = new HashMap<String, Object>();
		List<EMRSearchResultVO> results = new ArrayList<EMRSearchResultVO>();
		// ͨ��lucene����
		if (isDebug) {
			System.out.println("ִ�� lucene  ��ʼ......");
		}
		// ȡ�����ļ����õ�����Ŀ¼;
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		String searchIndexPath = config.getString("", "SearchCDAIndexPath");
		if (isDebug) {
			System.out.println("========SearchCDAIndexPath====="
					+ searchIndexPath);
		}

		results = DocSearchHelper.getEMRCDAFiles(searchIndexPath, result,
				metaCode, op, searchValue, join, emrForms);
		//
		if (isDebug) {
			System.out.println("ִ�� lucene  ���.......");
		}
		returnData.put("EMRSearchResult", results);

		// ���ؽ��;
		this.renderText(response, JsonUtils.mapToJson(returnData));
	}

	public void doSearch1(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Ԫ�ر��룻
		String metaCode[] = request.getParameterValues("metaCode");
		// Ԫ�����ݣ�
		// String metaData[]=request.getParameterValues("metaData");
		// ������
		String op[] = request.getParameterValues("op");
		// ��ѯֵ;
		String searchValue[] = request.getParameterValues("searchValue");
		// ���ӹ�ϵ;
		String join[] = request.getParameterValues("join");

		String forms = request.getParameter("checkedEMRForms");

		String emrForms[] = null;
		if (!forms.equals("")) {
			emrForms = request.getParameter("checkedEMRForms").split(",");

		}
		//
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String regionCode = request.getParameter("regionCode");
		String deptCode = request.getParameter("deptCode");
		String stationCode = request.getParameter("stationCode");
		// ���˵Ļ�����ѯ,�����Ӧ��MR_NO��
		TParm result = EMRSearchTool.getInstance().baseQuery(startDate,
				endDate, regionCode, deptCode, stationCode);

		// 2.����Lucene����xmlԪ��������
		// ͨ��lucene����
		if (isDebug) {
			System.out.println("ִ�� lucene  ��ʼ......");
		}
		// ȡ�����ļ����õ�����Ŀ¼;
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		String searchIndexPath = config.getString("", "SearchCDAIndexPath");
		if (isDebug) {
			System.out.println("========SearchCDAIndexPath====="
					+ searchIndexPath);
		}

		String toPage = request.getParameter("toPage");
		Page page = new Page();
		if (toPage == null || toPage.equals("")) {
			page.setToPage(1);
		} else {
			page.setToPage(Integer.parseInt(toPage));
		}

		page.setPageSize(PAGESIZE);
		Page returnPage = DocSearchHelper.getEMRCDAFiles(searchIndexPath, result,
				metaCode, op, searchValue, join,emrForms, page);
		//
		if (isDebug) {
			System.out.println("ִ�� lucene  ���.......");
		}

		
		request.setAttribute("page", returnPage);
		if (isDebug) {
			System.out.println("��СΪ��"+returnPage.getList().size());
		}
		
		request.getRequestDispatcher("jsp/emr/EmrSearchResult.jsp").forward(request,
				response);
	}

	/**
	 * ����Ԫ���ݷ���
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	public void loadDataCateTree(HttpServletRequest request,
			HttpServletResponse response) {
		// ȡ�����޾ݣ�
		String parentId = request.getParameter("id");

		if (parentId == null || parentId.equals("") || parentId.equals("root")) {
			parentId = "root";
		}

		// ����jdo������ȡ�ӽڵ�;
		List<EMRMetaDataDO> listPO = EMRSearchTool.getInstance()
				.getMetadataCategoryByParentId(parentId);
		Document document = this.createNewDocument();
		org.w3c.dom.Element root = document.createElement("tree");
		root.setAttribute("id", (!parentId.equalsIgnoreCase("root")) ? parentId
				: "root");

		for (EMRMetaDataDO entityPO : listPO) {
			org.w3c.dom.Element item = document.createElement("item");
			item.setAttribute("id", entityPO.getDataCode());
			item.setAttribute("text", entityPO.getDataTitle());
			item.setAttribute("tooltip", entityPO.getDataDefine());
			item.setAttribute("child", entityPO.isLeaf() ? "0" : "1");
			root.appendChild(item);
		}
		document.appendChild(root);
		String xmlString = this.toXMLString(document);
		// System.out.println("==========loadDataCateTree========="+xmlString);

		try {
			response.setHeader("Cache-Control", "no-store");
			response.setHeader("Pragrma", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType("text/xml;charset=UTF-8");
			response.getWriter().write(xmlString);
		} catch (IOException e) {

		}

	}

	/**
	 * ����XML
	 * 
	 * @param response
	 *            HttpServletResponse
	 * @param xml
	 *            String
	 */
	private void renderXML(HttpServletResponse response, String xml) {
		try {
			response.setHeader("Cache-Control", "no-store");
			response.setHeader("Pragrma", "no-cache");
			response.setDateHeader("Expires", 0);

			response.setContentType("text/xml;charset=UTF-8");
			response.getWriter().write(xml);
		} catch (IOException e) {

		}
	}

	/**
	 * ���ɴ��ı�
	 * 
	 * @param response
	 *            HttpServletResponse
	 * @param text
	 *            String
	 */
	public void renderText(HttpServletResponse response, String text) {
		try {
			response.setHeader("Cache-Control", "no-store");
			response.setHeader("Pragrma", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType("text/plain;charset=UTF-8");
			response.getWriter().write(text);
		} catch (IOException e) {

		}
	}

	/**
	 * ����EMR������
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	public void loadEMRCateTree(HttpServletRequest request,
			HttpServletResponse response) {
		// ȡ�����޾ݣ�
		String parentId = request.getParameter("id");
		// System.out.println("parentId ago=========="+parentId);
		if (parentId == null || parentId.equals("") || parentId.equals("root")) {
			parentId = "root";
		}
		List<EMRTableDO> listPO = EMRSearchTool.getInstance()
				.getEMRCategoryByParentId(parentId);

		Document document = this.createNewDocument();
		org.w3c.dom.Element root = document.createElement("tree");
		root.setAttribute("id", (!parentId.equalsIgnoreCase("root")) ? parentId
				: "root");

		for (EMRTableDO entityPO : listPO) {
			org.w3c.dom.Element item = document.createElement("item");
			item.setAttribute("id", entityPO.getClassCode());
			item.setAttribute("text", entityPO.getClassDesc());
			item.setAttribute("tooltip", entityPO.getDescription());
			item.setAttribute("child", entityPO.isLeaf() ? "0" : "1");
			root.appendChild(item);
		}
		document.appendChild(root);
		String xmlString = this.toXMLString(document);

		// System.out.println("==========loadEMRCateTree========="+xmlString);
		try {
			response.setHeader("Cache-Control", "no-store");
			response.setHeader("Pragrma", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType("text/xml;charset=UTF-8");
			response.getWriter().write(xmlString);
		} catch (IOException e) {

		}

	}

	/**
	 * Document ת���ַ���
	 * 
	 * @param document
	 *            Document
	 * @return String
	 */
	private String toXMLString(Document document) {
		StringWriter pw = new StringWriter();
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return pw.toString();
	}

	/**
	 * ����Doc
	 * 
	 * @return Document
	 */
	private Document createNewDocument() {

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();

		documentBuilderFactory.setNamespaceAware(true);

		DocumentBuilder documentBuilder = null;

		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return documentBuilder.newDocument();
	}

}
