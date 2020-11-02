package com.service;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jdo.sys.XMLHelper;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import com.dongyang.util.TypeTool;


/**
 * 
 * XML参数传换工具
 * 
 * @author 
 * 
 */
public class XMLParamUtils {

	// 默认编码格式
	private static final String ENCODING = "UTF-8";

	// 调试标志
	private static boolean m_debug = false;
	
	public static String onCheckError(List<Map<String, Object>> list ,String message){
		StringBuffer xml = new StringBuffer();
		if (null!=list.get(0) && null != list.get(0).get("ERRCODE") && Integer.parseInt(list.get(0).get("ERRCODE").toString())<0) {
			xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TRANSFERINFO><HEADINFO>");
			xml.append("</HEADINFO><ERRCODE>-1</ERRCODE><ERRMSG>"+message+"</ERRMSG></TRANSFERINFO>");
			return xml.toString();
		}
		return "success";
	}
	public static String sendError(String error,String message){
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TRANSFERINFO><HEADINFO>");
		xml.append("</HEADINFO><ERRCODE>"+error+"</ERRCODE><ERRMSG>"+message+"</ERRMSG></TRANSFERINFO>");
		return xml.toString();
	}
	public static String onGetJsonResultXml(String [] list,List<Map<String,Object>> resultList){
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><TRANSFERINFO><HEADINFO></HEADINFO><RESULT>");
		Map<String,Object> map = null;
		for (int i = 0; i < resultList.size(); i++) {
			map = resultList.get(i);
			xml.append("<ROW>");
			for (int j = 0; j < list.length; j++) {
				if (null != map.get(list[j])
						&& map.get(list[j]).toString().length()>0) {
					xml.append("<").append(list[j]).append(">").append(map.get(list[j])).append("</").append(list[j]).append(">");
				}else{
					xml.append("<").append(list[j]).append(">").append("").append("</").append(list[j]).append(">");
				}
			}
			xml.append("</ROW>");
		}
		xml.append("</RESULT><ERRCODE>1</ERRCODE><ERRMSG></ERRMSG></TRANSFERINFO>");
		return xml.toString();
	}
	public static List<Map<String ,Object>> onGetXmlResultList(List<String> list,String xml){
		List<Map<String ,Object>> resultList = new ArrayList<Map<String ,Object>>();
		Map<String,Object> map= null;
		if (null == xml || xml.length()<=0) {
			map = new HashMap<String, Object>();
			map.put("ERRCODE", -1);
			map.put("ERRMSG", "获得信息失败");
			resultList.add(map);
			return resultList;
		}
		Document doc = getdoc(xml);
		NodeList fields = doc.getElementsByTagName("TRANSFERINFO");
		Element memuElement = (Element) fields.item(0);
		NodeList error = memuElement.getElementsByTagName("ERRCODE");
		if (error.item(0) != null) {
			int errorCode = Integer.parseInt(error.item(0).getFirstChild().getNodeValue());
			if (errorCode<0) {//错误
				String errMsg = memuElement.getElementsByTagName("ERRMSG").item(0)
						.getFirstChild().getNodeValue();
				map = new HashMap<String, Object>();
				map.put("ERRCODE", errorCode);
				map.put("ERRMSG", errMsg);
				resultList.add(map);
				return resultList;
			}
		}
		map = new HashMap<String, Object>();
		fields = doc.getElementsByTagName("HEADINFO");
		for (int i = 0; i < fields.getLength(); i++) {
			memuElement = (Element) fields.item(i);
			if (null == memuElement) {
				continue;
			}
			for (int k = 0; k < list.size(); k++) {
				//System.out.println("getFirstChild::::::"+ memuElement.getElementsByTagName(list.get(k)).item(0).getFirstChild());
				//System.out.println("getNodeValue:::::"+memuElement.getElementsByTagName(list.get(k)).item(0).getFirstChild().getNodeValue());
				if (memuElement.getElementsByTagName(list.get(k)).item(0) != null
						&& null != memuElement.getElementsByTagName(list.get(k)).item(0).getFirstChild()
						&& null != memuElement.getElementsByTagName(list.get(k)).item(0).getFirstChild().getNodeValue()) {
						map.put(list.get(k),memuElement.getElementsByTagName(list.get(k)).item(0).getFirstChild().getNodeValue());
				}
				
			}
		}
		resultList.add(map);
		return resultList;
	}
	
	private static Document getdoc(String xml) {
		Document doc = null;
		try {
			doc = XMLHelper.getDocumentByString(xml);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return doc;
	}

	/**
	 * 将文档对象输出为字符串
	 * 
	 * @param document
	 *            文档对象
	 * @param bFormated
	 *            是否格式化
	 * @return docment转换成的字符串
	 */
	public static String toXMLString(Document document, String coding,
			boolean bFormated) {
		try {
			OutputFormat outputformat = new OutputFormat("xml", coding,
					bFormated);
			// outputformat.setIndenting(true);
			outputformat.setStandalone(true);
			StringWriter stringwriter = new StringWriter();
			XMLSerializer xmlserializer = new XMLSerializer(stringwriter,
					outputformat);

			xmlserializer.serialize(document);

			return stringwriter.toString();
		} catch (Exception exception) {
			return null;
		}
	}

	/**
	 * 创建一个指定根名称的Docment;
	 * 
	 * @param rootTag
	 *            根名称;
	 * @return 新创建的Document对象
	 */
	public static Document defaultDocument(String rootTag) {

		Document doc = new org.apache.xerces.dom.DocumentImpl(null);

		if (rootTag != null) {

			Element root = doc.createElement(rootTag);

			doc.appendChild(root);
		}

		return doc;
	}

	/**
	 * 在doc根下创建有内容的子节点
	 * 
	 * @param doc
	 *            节点所属于的doc
	 * @param name
	 *            节点名称
	 * @param content
	 *            节点文本内容
	 */
	public static Element createElement(final Document doc, final String name,
			final String content) {
		return createElementWithContent(doc, doc.getDocumentElement(), name,
				content);
	}

	/**
	 * 在doc根下创建无内容的子节点
	 * 
	 * @param doc
	 *            节点所属于的doc
	 * @param name
	 *            节点名称
	 */
	public static Element createElement(final Document doc, final String name) {
		return createElementWithOutContent(doc, doc.getDocumentElement(), name);
	}

	/**
	 * 创建一个新的节点
	 * 
	 * @param doc
	 *            节点所属Document
	 * @param parent
	 *            新节点的父节点
	 * @param name
	 *            新节点的名称
	 */
	public static Element createElementWithOutContent(final Document doc,
			final Node parent, final String name) {
		Element el = doc.createElement(name);

		parent.appendChild(el);

		return el;
	}

	/**
	 * 创建一个节点和它的文本内容
	 * 
	 * @param doc
	 *            节点所属于的doc
	 * @param parent
	 *            父节点
	 * @param name
	 *            节点名称
	 * @param content
	 *            节点文本内容
	 */
	public static Element createElementWithContent(final Document doc,
			final Node parent, final String name, final String content) {
		Element el = doc.createElement(name);

		parent.appendChild(el);

		Text t = doc.createTextNode(((content == null) ? "" : content));

		el.appendChild(t);

		return el;
	}

	/**
	 * 设置元素属性值；
	 * 
	 * @param element
	 * @param name
	 * @param value
	 * @return
	 */
	public static Element updateElementAttr(Element element, String name,
			String value) {
		element.setAttribute(name, value);
		return element;
	}

	/**
	 * 根据全部文档，元素名取元素(只取一个对应的index[0,1....]元素)
	 * 
	 * @param doc
	 *            包含需要元素的文档
	 * @param name
	 *            元素名
	 * @param index
	 *            元素节点，没有找到返回null
	 * @return
	 */
	public static Element getElementByName(final Document doc,
			final String name, final int index) {
		try {
			NodeList nodes = doc.getElementsByTagName(name);

			return (Element) nodes.item(index);
		} catch (Exception e) {
			if (m_debug) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 根据父节点元素，元素名取元素(只取一个对应的index[0,1....]元素)
	 * 
	 * @param parent
	 *            父节点元素
	 * @param name
	 * @return
	 */
	public static Element getElementByName(final Element parent,
			final String name, final int index) {
		try {
			NodeList nodes = parent.getElementsByTagName(name);

			return (Element) nodes.item(index);
		} catch (Exception e) {
			if (m_debug) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 将xml字符串转换为Document对象
	 * 
	 * @param document
	 *            符合完整的xml格式的xml字符串。
	 */
	public static Document getDocumentByString(String document)
			throws Exception {
		Document doc = new DocumentImpl(null);

		doc.appendChild(htmlToXML(document, doc));

		return doc;
	}

	/**
	 * 把格式良好的html文本构造为element
	 * 
	 * @param html
	 *            结构良好的html字符串
	 * @param document
	 *            需要加入的文档对象
	 * @return element
	 */
	public static Element htmlToXML(String html, Document document) {
		try {
			StringReader reader = new StringReader(html);
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document htmlDoc = docBuilder.parse(new InputSource(reader));
			Element root = htmlDoc.getDocumentElement();
			Node node = document.importNode(root, true);

			return (Element) node;
		} catch (Exception e) {
			if (m_debug) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 将一个XML文档文件解析为一个JAVA的文档对象
	 * 
	 * @param filename
	 *            文档文件名
	 * @return Document文档对象
	 */
	public static Document getDocument(String filename) throws Exception {
		return getSimulateData(filename);
	}

	/**
	 * 将XML的Document文档对象输出到指定文件中<br/>
	 * 输出XML文件的编码格式设定为"UTF-8"
	 * 
	 * @param document
	 *            文档对象
	 * @param filename
	 *            指定文件，可以包括路径
	 * @throws Exception
	 */
	public static void writeSimulateData(Document document, String filename,
			String encoding) throws Exception {
		//
		TransformerFactory tfactory = TransformerFactory.newInstance();
		// 加入病历模版公用XSL样式；
		// StreamSource style = new
		// StreamSource("D:\\xslttest\\example\\xml\\test2.xsl");
		Transformer transformer = tfactory.newTransformer();

		// 将DOM对象转化为DOMSource类对象，该对象表现为转化成别的表达形式的信息容器。
		DOMSource source = new DOMSource(document);

		// 获得一个StreamResult类对象，该对象是DOM文档转化成的其他形式的文档的容器，
		// 可以是XML文件，文本文件，HTML文件。这里为一个XML文件。
		StreamResult result = new StreamResult(new File(filename));

		// 调用API，将DOM文档转化成XML文件。
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		transformer.transform(source, result);
	}

	/**
	 * 将一个XML文档文件解析为的文档对象
	 * 
	 * @param filename
	 *            文档文件名
	 * @return Document文档对象
	 */
	public static Document getSimulateData(String filename) throws Exception {
		// 打开文件
		FileInputStream in = new java.io.FileInputStream(filename);

		// 创建输入源
		InputSource input = new org.xml.sax.InputSource(in);

		// 创建DOM解析器
		DOMParser parser = new org.apache.xerces.parsers.DOMParser();

		// 解析文档
		parser.parse(input);

		Document doc = parser.getDocument();

		in.close();

		// 返回文档
		return doc;
	}

	/**
	 * 测试
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>"
				+ "<TRANSFERINFO><HEADINFO></HEADINFO><RESULT><ROW><DEPT_CODE>1012</DEPT_CODE>"
				+ "<DEPT_NAME>内科</DEPT_NAME></ROW><ROW><DEPT_CODE>1013</DEPT_CODE><DEPT_NAME>外科</DEPT_NAME>"
				+ "</ROW></RESULT><ERRCODE>1</ERRCODE><ERRMSG></ERRMSG></TRANSFERINFO>";
		xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?><TRANSFERINFO><HEADINFO><PAT_NAME>张三</PAT_NAME><IDNO>1202222198507086417</IDNO><SEX_CODE>1</SEX_CODE><BIRTH_DATE>1985-07-08</BIRTH_DATE><TEL>21312315545</TEL><EMAIL>pangben10@126.com</EMAIL></HEADINFO><ERRCODE>1</ERRCODE><ERRMSG></ERRMSG></TRANSFERINFO>";
		List<String> list = new ArrayList<String>();
		list.add("SEX_CODE");
		list.add("PAT_NAME");
		list.add("IDNO");
		list.add("BIRTH_DATE");
		list.add("TEL");
		list.add("EMAIL");
		String sessionDate01="08:00-09:00";
		
		String starTime =  sessionDate01.substring(0, sessionDate01.lastIndexOf("-")).replace(":", "");
		String endTime =  sessionDate01.substring(sessionDate01.lastIndexOf("-")+1, sessionDate01.length()).replace(":", "");
		System.out.println("starTime::"+starTime);
		System.out.println("endTime::"+endTime);
		//onGetXmlResultList(list, xml);
//		List<SysIo> list = new ArrayList<SysIo>();
//		SysIo io = new  SysIo();
//		io.setColumnName("DEPT_CODE");
//		io.setColumnDesc("科室代码");
//		list.add(io);
//		io = new  SysIo();
//		io.setColumnName("DEPT_NAME");
//		io.setColumnDesc("科室名称");
//		list.add(io);
//		io = new  SysIo();
//		io.setColumnName("ACTIVE_FLG");
//		io.setColumnDesc("启用状态");
//		list.add(io);
		//onGetXmlResultList(list, xml);
		// String
		// xml="<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"yes\" ?><output><sqldata><row><akb020>服务机构编号</akb020><ykb037>清算分中心</ykb037><yae366>清算期号</yae366></row><row><akb020>服务机构编号1</akb020><ykb037>清算分中心1</ykb037><yae366>清算期号1</yae366></row></sqldata></output>";
		//System.out.println(xml2Parm(xml));
	}
}
