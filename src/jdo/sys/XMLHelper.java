package jdo.sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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

/**
 * XML���ô������ࣻ
 * @author lixiang
 *
 */
public class XMLHelper {

	// Ĭ�ϱ����ʽ
	private static final String ENCODING = "UTF-8";

	// ���Ա�־
	private static boolean m_debug = false;

	/**
	 * ���캯��
	 */
	public XMLHelper() {

	}

	/**
	 * ����һ��ָ�������Ƶ�Docment;
	 * @param rootTag ������;
	 * @return �´�����Document����
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
	 * ��doc���´��������ݵ��ӽڵ�
	 *
	 * @param doc
	 *            �ڵ������ڵ�doc
	 * @param name
	 *            �ڵ�����
	 * @param content
	 *            �ڵ��ı�����
	 */
	public static Element createElement(final Document doc, final String name,
			final String content) {
		return XMLHelper.createElementWithContent(doc,
				doc.getDocumentElement(), name, content);
	}

	/**
	 * ��doc���´��������ݵ��ӽڵ�
	 *
	 * @param doc
	 *            �ڵ������ڵ�doc
	 * @param name
	 *            �ڵ�����
	 */
	public static Element createElement(final Document doc, final String name) {
		return XMLHelper.createElementWithOutContent(doc, doc
				.getDocumentElement(), name);
	}

	/**
	 * ����һ���µĽڵ�
	 *
	 * @param doc
	 *            �ڵ�����Document
	 * @param parent
	 *            �½ڵ�ĸ��ڵ�
	 * @param name
	 *            �½ڵ������
	 */
	public static Element createElementWithOutContent(final Document doc,
			final Node parent, final String name) {
		Element el = doc.createElement(name);

		parent.appendChild(el);

		return el;
	}


	/**
	 * ����һ���ڵ�������ı�����
	 *
	 * @param doc
	 *            �ڵ������ڵ�doc
	 * @param parent
	 *            ���ڵ�
	 * @param name
	 *            �ڵ�����
	 * @param content
	 *            �ڵ��ı�����
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
	 * ����Ԫ������ֵ��
	 * @param element
	 * @param name
	 * @param value
	 * @return
	 */
	public static Element updateElementAttr(Element element,String name,
			String value){
		element.setAttribute(name, value);
		return element;
	}

	/**
	 * ����ȫ���ĵ���Ԫ����ȡԪ��(ֻȡһ����Ӧ��index[0,1....]Ԫ��)
	 * @param doc   ������ҪԪ�ص��ĵ�
	 * @param name  Ԫ����
	 * @param index Ԫ�ؽڵ㣬û���ҵ�����null
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
	 * ���ݸ��ڵ�Ԫ�أ�Ԫ����ȡԪ��(ֻȡһ����Ӧ��index[0,1....]Ԫ��)
	 * @param parent  ���ڵ�Ԫ��
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
	 * ���ĵ��������Ϊ�ַ���
	 *
	 * @param document
	 *            �ĵ�����
	 * @param bFormated
	 *            �Ƿ��ʽ��
	 * @return docmentת���ɵ��ַ���
	 */
	public static String toXMLString(Document document, String coding,boolean bFormated) {
		try {
			OutputFormat outputformat = new OutputFormat("xml", coding,
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
	 * ��xml�ַ���ת��ΪDocument����
	 *
	 * @param document
	 *            ����������xml��ʽ��xml�ַ�����
	 */
	public static Document getDocumentByString(String document)
			throws Exception {
		Document doc = new DocumentImpl(null);

		doc.appendChild(htmlToXML(document, doc));

		return doc;
	}

	/**
	 * �Ѹ�ʽ���õ�html�ı�����Ϊelement
	 *
	 * @param html
	 *            �ṹ���õ�html�ַ���
	 * @param document
	 *            ��Ҫ������ĵ�����
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
	 * ��һ��XML�ĵ��ļ�����Ϊһ��JAVA���ĵ�����
	 *
	 * @param filename
	 *            �ĵ��ļ���
	 * @return Document�ĵ�����
	 */
	public static Document getDocument(String filename) throws Exception {
		return getSimulateData(filename);
	}




	/**
	 * ��XML��Document�ĵ����������ָ���ļ���<br/> ���XML�ļ��ı����ʽ�趨Ϊ"UTF-8"
	 *
	 * @param document
	 *            �ĵ�����
	 * @param filename
	 *            ָ���ļ������԰���·��
	 * @throws Exception
	 */
	public static void writeSimulateData(Document document, String filename,String encoding)
			throws Exception {
		//
		TransformerFactory tfactory = TransformerFactory.newInstance();
		//���벡��ģ�湫��XSL��ʽ��
		//StreamSource style = new StreamSource("D:\\xslttest\\example\\xml\\test2.xsl");
		Transformer transformer = tfactory.newTransformer();

		// ��DOM����ת��ΪDOMSource����󣬸ö������Ϊת���ɱ�ı����ʽ����Ϣ������
		DOMSource source = new DOMSource(document);

		// ���һ��StreamResult����󣬸ö�����DOM�ĵ�ת���ɵ�������ʽ���ĵ���������
		// ������XML�ļ����ı��ļ���HTML�ļ�������Ϊһ��XML�ļ���
		StreamResult result = new StreamResult(new File(filename));

		// ����API����DOM�ĵ�ת����XML�ļ���
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		transformer.transform(source, result);
	}


	/**
	 * ��һ��XML�ĵ��ļ�����Ϊ���ĵ�����
	 *
	 * @param filename
	 *            �ĵ��ļ���
	 * @return Document�ĵ�����
	 */
	public static Document getSimulateData(String filename) throws Exception {
		// ���ļ�
		FileInputStream in = new java.io.FileInputStream(filename);

		// ��������Դ
		InputSource input = new org.xml.sax.InputSource(in);

		// ����DOM������
		DOMParser parser = new org.apache.xerces.parsers.DOMParser();

		// �����ĵ�
		parser.parse(input);

		Document doc = parser.getDocument();

		in.close();

		// �����ĵ�
		return doc;
	}


	/**
	 * ����
	 */
	public static void main(String args[]){
		//=======================����DOM����================================//
		//������ڵ㣻
		Document doc=XMLHelper.defaultDocument("JAVAHIS-EMR");
		//ѭ��ҳ�ڵ㣻
		for(int i=1;i<=2;i++){
			Element pageElem=XMLHelper.createElement(doc, "page");
			XMLHelper.updateElementAttr(pageElem, "no", String.valueOf(i));
			XMLHelper.updateElementAttr(pageElem, "HEIGHT", "841");
			XMLHelper.updateElementAttr(pageElem, "WIDTH", "595");

			XMLHelper.updateElementAttr(pageElem, "Top", "28.346456692913385");
			XMLHelper.updateElementAttr(pageElem, "Bottom", "28.346456692913456");
			XMLHelper.updateElementAttr(pageElem, "Left", "42.51968503937008");
			XMLHelper.updateElementAttr(pageElem, "Right", "42.51968503936995");

			XMLHelper.updateElementAttr(pageElem, "X", "0");
			XMLHelper.updateElementAttr(pageElem, "Y", "0");

		}

		//ȡdoc�µ�page�ڵ㣻
		Element pageElm1=XMLHelper.getElementByName(doc, "page", 0);
		//page�ڵ��´���table;
		Element tableElm1InPage1=XMLHelper.createElementWithOutContent(doc, pageElm1, "table");
		//����table���ԣ�
		XMLHelper.updateElementAttr(tableElm1InPage1, "X", "0");
		XMLHelper.updateElementAttr(tableElm1InPage1, "Y", "29");
		XMLHelper.updateElementAttr(tableElm1InPage1, "HEIGHT", "71");
		XMLHelper.updateElementAttr(tableElm1InPage1, "WIDTH", "509");

		//����ѭ������У�
		//����tr
		Element trInTablePage1=XMLHelper.createElementWithOutContent(doc, tableElm1InPage1, "tr");
		//����ѭ������У�
		Element tdInTrPage1=XMLHelper.createElementWithOutContent(doc, trInTablePage1, "td");
		//���õ�Ԫ�����ԣ�
		XMLHelper.updateElementAttr(tdInTrPage1, "WIDTH", "169");
		XMLHelper.updateElementAttr(tdInTrPage1, "HEIGHT", "17");
		XMLHelper.updateElementAttr(tdInTrPage1, "X", "0");
		XMLHelper.updateElementAttr(tdInTrPage1, "Y", "0");
		XMLHelper.updateElementAttr(tdInTrPage1, "ALIGNMENT", "2");



		//td�µ�Ԫ��(������)��
		Element elm1InTDPage1=XMLHelper.createElementWithContent(doc, tdInTrPage1, "����ѡ��", "����ѡ��");
		//д����������TD�µ�Ԫ�أ�
		XMLHelper.updateElementAttr(elm1InTDPage1, "family", "����");
		XMLHelper.updateElementAttr(elm1InTDPage1, "style", "bold");
		XMLHelper.updateElementAttr(elm1InTDPage1, "size", "10");
		XMLHelper.updateElementAttr(elm1InTDPage1, "TYPE", "6");
		XMLHelper.updateElementAttr(elm1InTDPage1, "X", "79");
		XMLHelper.updateElementAttr(elm1InTDPage1, "Y", "2");
		//
		String strXML=XMLHelper.toXMLString(doc,ENCODING, true);
		//System.out.println(strXML);
		//=======================����DOM���Խ���================================//

		//=======================��ȡXML����================================//
/*		Document doc=null;
		try {
			doc=XMLHelper.getSimulateData("D:\\xslttest\\example\\xml\\test20110509-1.xml");
			String strXML=XMLHelper.toXMLString(doc,ENCODING, true);
			System.out.println(strXML);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//=======================��ȡXML����================================//

		//======================дXML�ļ�����================================//
		try {
			XMLHelper.writeSimulateData(doc, "c:\\test0509.xml", ENCODING);
			System.out.println("======write success!=======");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//=======================дXML�ļ����Խ���================================//


	}

}
