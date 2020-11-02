package com.javahis.device;

import com.dongyang.data.TParm;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.dongyang.manager.TIOM_FileServer;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import org.apache.xml.serialize.XMLSerializer;

/**
 * <p>
 * Title:
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
 * @author not attributable
 * @version 1.0
 */
public class NJCityInwDriver {
	private static final String ENCODING = "GBK";
	private static final String ROOT_NODE_NAME = "ROOT";
	private static final String RECORD_NODE_NAME = "RECORD";

	private static final boolean isDebug = true;

	public NJCityInwDriver() {
	}

	/**
	 * �����Ͼ���ҽ��XML�ļ����÷���
	 * 
	 * @param parm
	 *            TParm ���ݲ���
	 * @param fullFile
	 *            String �ļ�ȫ��
	 * @return boolean �����ļ��Ƿ�ɹ�
	 */
	public static boolean createXMLFile(TParm parm, String fullFile) {
		if (parm == null || parm.getCount() == 0) {
			System.out.println("�Ͼ���ҽ��û�����ݣ���������XML�ļ�.");
			return false;
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			if (isDebug) {
				e.printStackTrace();
			}
			return false;
		}

		Document doc = builder.newDocument();
		Element element = doc.createElement(ROOT_NODE_NAME);
		doc.appendChild(element);
		String columnName[] = parm.getNames();
		for (int i = 0; i < parm.getCount(); i++) {
			// System.out.println("======columnName======"+columnName[i]);
			Node recordElem = createWithOutContentElement(doc, element,
					RECORD_NODE_NAME);
			for (int j = 0; j < columnName.length; j++) {
				createWithContentElement(doc, recordElem, columnName[j], parm
						.getValue(columnName[j], i));

			}

		}
		String strXML = toXMLString(doc, true);
		if (!writeFile(fullFile, strXML.getBytes())) {
			System.out.println("XML�ļ�����ʧ��!");
			return false;
		}
		return true;

	}

	/**
	 * ͨ��XML�ļ������parm;
	 * 
	 * @param fullFile
	 * @return
	 */
	public static TParm getPame(String fullFile) {
		TParm parm = new TParm();

		try {
			Document doc = getDocument(fullFile);
			// System.out.println("doc"+toXMLString(doc,true));
			Element rootNode = doc.getDocumentElement();
			// System.out.println("root" + rootNode.getNodeName());
			NodeList records = rootNode.getElementsByTagName(RECORD_NODE_NAME);
			// System.out.println("=====records======"+records.getLength());
			for (int i = 0; i < records.getLength(); i++) {
				Node node = records.item(i);
				// System.out.println("node name"+node.getNodeName());
				// System.out.println("node type"+node.getNodeType());
				if (node.getNodeName().equalsIgnoreCase(RECORD_NODE_NAME)
						&& node.getNodeType() == 1) {
					// ����¼;
					fillEntry((Element) node, parm, i);

				}

			}
			// �����У�
			parm.setCount(records.getLength());

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isDebug) {
			System.out.println("====parm=====" + parm);
		}
		return parm;

	}

	/**
	 * ������ݣ�
	 * 
	 * @param item
	 * @param parm
	 */
	private static void fillEntry(Element item, TParm parm, int row) {
		// System.out.println("test xml"
		// +toXMLString(item.getOwnerDocument(),true));
		NodeList list = item.getChildNodes();
		// System.out.println("ddd"+list.item(0).getNodeName());

		// System.out.println("fillEntry length===="+list.getLength());
		// System.out.println("fillEntry size===="+list.item(0).getParentNode().getFirstChild().getChildNodes().getLength());
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (!node.getNodeName().equalsIgnoreCase("#text")
					&& node.getFirstChild() != null) {
				// ��������
				parm.insertData(node.getNodeName(), row, node.getFirstChild()
						.getNodeValue());
				// ������
				if (row == 0) {
					parm.addData("SYSTEM", "COLUMNS", node.getNodeName());
				}
			}

		}

	}

	/**
	 * ͨ�����ӣ��õ�����Ԫ���б�
	 * 
	 * @param doc
	 * @param name
	 * @return
	 */
	public static NodeList getElementsByName(final Document doc,
			final String name) {

		NodeList nodes = doc.getElementsByTagName(name);
		// System.out.println("==nodes size==="+nodes.getLength());
		return nodes;

	}

	/**
	 * 
	 * @param fullFile
	 * @return
	 * @throws Exception
	 */
	private static Document getDocument(String fullFile) throws Exception {

		/*FileInputStream in = null;
		// ���ļ�

		in = new java.io.FileInputStream(fullFile);

		InputSource input = new org.xml.sax.InputSource(in);
		// ����DOM������
		DOMParser parser = new org.apache.xerces.parsers.DOMParser();
		// �����ĵ�

		parser.parse(input);

		Document doc = parser.getDocument();

		in.close();*/
		FileInputStream in = new java.io.FileInputStream(fullFile);
		DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder=domfac.newDocumentBuilder();
		Document doc=dombuilder.parse(in);
		
		in.close();
		return doc;
	}

	/**
	 * ���������ݵ�Ԫ�ؽڵ�
	 * 
	 * @param doc
	 * @param parent
	 * @param elemName
	 * @return
	 */
	private static Node createWithOutContentElement(Document doc, Node parent,
			String elemName) {
		Element element = doc.createElement(elemName);
		parent.appendChild(element);
		return element;
	}

	/**
	 * �������������ݵĽڵ�;
	 * 
	 * @param doc
	 * @param parent
	 * @param elemName
	 * @param elemValue
	 * @return
	 */
	private static Node createWithContentElement(Document doc, Node parent,
			String elemName, String elemValue) {

		Element element = doc.createElement(elemName);
		Text data = doc.createTextNode(elemValue);
		element.appendChild(data);
		parent.appendChild(element);
		return element;
	}

	/**
	 * ����д�ļ�����
	 * 
	 * @param fullFileName
	 * @param data
	 * @return
	 */
	private static boolean writeFile(String fullFileName, byte[] data) {

		if (!TIOM_FileServer.writeFile(fullFileName, data)) {
			return false;
		}

		return true;
	}

	/**
	 * ����XML�ַ���
	 * 
	 * @param document
	 *            Document doc�ĵ�
	 * @param bFormated
	 *            boolean �Ƿ��ʽ��
	 * @return String XML�ַ���
	 */
	private static String toXMLString(Document document, boolean bFormated) {
		try {
			OutputFormat outputformat = new OutputFormat("xml", ENCODING,
					bFormated);
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
	 * ��Ԫ����
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String args[]) {

		//1.��������
		/**TParm inparm = new TParm();
		int count = 0;
		for (int i = 0; i < 4; i++) {
			inparm.insertData("TBR", i, "100013XXXX");
			inparm.insertData("XM", i, "����");
			inparm.insertData("BZ", i, "1");
			inparm.insertData("ZBM", i, "110300001");
			inparm.insertData("SL", i, "2");
			inparm.insertData("DJ", i, "12.3");
			inparm.insertData("YHLB", i, "1");
			inparm.insertData("YHJ", i, "10");a
			count++;
		}
		inparm.addData("SYSTEM", "COLUMNS", "TBR");
		inparm.addData("SYSTEM", "COLUMNS", "XM");
		inparm.addData("SYSTEM", "COLUMNS", "BZ");
		inparm.addData("SYSTEM", "COLUMNS", "ZBM");
		inparm.addData("SYSTEM", "COLUMNS", "SL");
		inparm.addData("SYSTEM", "COLUMNS", "DJ");
		inparm.addData("SYSTEM", "COLUMNS", "YHLB");
		inparm.addData("SYSTEM", "COLUMNS", "YHJ");
		inparm.setCount(count);
		System.out.println("=======inparm=============" + inparm); 
		// 2.�����ļ�
		NJCityInwDriver.createXMLFile(inparm, "c:/mzghxx.xml");*/

		// 2.
		TParm parm = getPame("c:/mzghxx.xml");

	}
}