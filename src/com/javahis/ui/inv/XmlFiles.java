package com.javahis.ui.inv;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dongyang.data.TParm;

public class XmlFiles {
	/**
	 * ��ȡ��������Ϣ
	 * @param file
	 * @return
	 * @throws Exception    
	 */
	public static TParm readExcelFile(String file) throws Exception {   
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(file); // ��ȡ��xml�ļ�
		// ���濪ʼ��ȡ
		Element root = doc.getDocumentElement(); // ��ȡ��Ԫ��     
		NodeList lists = root.getElementsByTagName("Item");
		TParm result= new TParm();
		for (int i = 0; i < lists.getLength(); i++) {
			// һ��ȡ��һ������
			Element ss = (Element) lists.item(i);
			//���ʱ���
			NodeList orderCode = ss.getElementsByTagName("GOODS");
			Element e = (Element) orderCode.item(0);
			Node t = e.getFirstChild();
			result.addData("INV_CODE", t.getNodeValue());

			//���ռ۸�
			NodeList prices = ss.getElementsByTagName("DPRC");
			e = (Element) prices.item(0);
			t = e.getFirstChild();
			result.addData("PURORDER_PRICE", t.getNodeValue());
			
			//��������
			NodeList verifyinQty = ss.getElementsByTagName("DBILLQTY");
			e = (Element) verifyinQty.item(0);
			t = e.getFirstChild();
			result.addData("PURORDER_QTY", t.getNodeValue());
			
			//Ч��
			NodeList validDate = ss.getElementsByTagName("ENDDATE");
			e = (Element) validDate.item(0);
			t = e.getFirstChild();
			result.addData("VALID_DATE", t.getNodeValue());
			
			//��Ӧ�̱���SUP_CODE
			NodeList supCode = ss.getElementsByTagName("DEALHOSCODE");
			e = (Element) supCode.item(0);
			t = e.getFirstChild();
			result.addData("SUP_CODE", t.getNodeValue());
			
			//��Ʊ��
			NodeList invoiceNo = ss.getElementsByTagName("INVOICENO");
			e = (Element) invoiceNo.item(0);
			t = e.getFirstChild();
			result.addData("INVOICE_NO", t.getNodeValue());
			
			//����
			NodeList batchNo = ss.getElementsByTagName("LOTNO");
			e = (Element) batchNo.item(0);
			t = e.getFirstChild();
			result.addData("BATCH_NO", t.getNodeValue());
			
			//��������
			NodeList pupOrderNo = ss.getElementsByTagName("BILLNO");
			e = (Element) pupOrderNo.item(0);
			t = e.getFirstChild();
			result.addData("PURORDER_NO", getValue(t));
			//��Ʊ����
			NodeList invoiceDate = ss.getElementsByTagName("INVOICEDATE");
			e = (Element) invoiceDate.item(0);
			t = e.getFirstChild();
			result.addData("INVOICE_DATE", t.getNodeValue());
			
		}
		result.setCount(result.getCount("INV_CODE"));
		return result;
	}
	/**
	 * �õ��ڵ�ֵ
	 * @param t
	 * @return
	 */
	public static String getValue(Node t){
		if(null == t){
			return "";
		}else {
			return t.getNodeValue();
		}
	}
}
