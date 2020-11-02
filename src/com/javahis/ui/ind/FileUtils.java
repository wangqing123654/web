package com.javahis.ui.ind;

/**
 * <p>
 * Title: �ļ����� 
 * </p>
 *
 * <p>
 * Description:  ����DOM������XML�ļ�
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company:BlueCore
 * </p>
 *
 * @author liyh 2012.08.30
 * @version 1.0
 */
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jdo.clp.intoPathStatisticsTool;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

public class FileUtils {

	public static Map<String, Object> readXMLFile(String file) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(file); // ��ȡ��xml�ļ�
 
		// ���濪ʼ��ȡ
		Element root = doc.getDocumentElement(); // ��ȡ��Ԫ��
		NodeList lists = root.getElementsByTagName("Item");
		Map<String, Object> resultMap = new HashMap<String,Object>();
		for (int i = 0; i < lists.getLength(); i++) {
			// һ��ȡ��һ��ҩƷ
			Element ss = (Element) lists.item(i);

			Map<String,Object> map = new HashMap<String,Object>();

			//ҩƷ����
			NodeList orderCode = ss.getElementsByTagName("GOODS");
			Element e = (Element) orderCode.item(0);
			Node t = e.getFirstChild();
			map.put("ORDER_CODE", t.getNodeValue());

			//���ռ۸�
			NodeList prices = ss.getElementsByTagName("DPRC");
			e = (Element) prices.item(0);
			t = e.getFirstChild();
			map.put("DPRC", t.getNodeValue());
			
			//��������
			NodeList verifyinQty = ss.getElementsByTagName("DBILLQTY");
			e = (Element) verifyinQty.item(0);
			t = e.getFirstChild();
			map.put("VERIFYIN_QTY", t.getNodeValue());
			
/*			//��������-Ŀǰϵͳ��û��
			NodeList prdDate = ss.getElementsByTagName("PRDDATE");
			e = (Element) prdDate.item(0);
			t = e.getFirstChild();
			stu.setSex(t.getNodeValue());
			map.put("PRD_DATE", t.getNodeValue());*/
			
			//Ч��
			NodeList validDate = ss.getElementsByTagName("ENDDATE");
			e = (Element) validDate.item(0);
			t = e.getFirstChild();
			map.put("VALID_DATE", t.getNodeValue());
			
			//��Ӧ�̱���SUP_CODE
			NodeList supCode = ss.getElementsByTagName("DEALHOSCODE");
			e = (Element) supCode.item(0);
			t = e.getFirstChild();
			map.put("SUP_CODE", t.getNodeValue());
			
			//��Ʊ��
			NodeList invoiceNo = ss.getElementsByTagName("INVOICENO");
			e = (Element) invoiceNo.item(0);
			t = e.getFirstChild();
			map.put("INVOICE_NO", t.getNodeValue());
			
			//����
			NodeList batchNo = ss.getElementsByTagName("LOTNO");
			e = (Element) batchNo.item(0);
			t = e.getFirstChild();
			map.put("BATCH_NO", t.getNodeValue());
			
			//�ɹ�����ID����ʽΪA10112 (DBSCM367)������A10112��HISϵͳ�ĵ��ţ���ҪHIS����ȡ
			NodeList pupOrderNo = ss.getElementsByTagName("BILLNO");
			e = (Element) pupOrderNo.item(0);
			t = e.getFirstChild();
			map.put("PURORDER_NO", t.getNodeValue());
		
			//��Ʊ����
			NodeList invoiceDate = ss.getElementsByTagName("INVOICEDATE");
			e = (Element) invoiceDate.item(0);
			t = e.getFirstChild();
			map.put("INVOICE_DATE", t.getNodeValue());
		
			resultMap.put(i+"", map);
		}

		return resultMap;
	}
	
	public static TParm readXMLFileP(String file) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(file); // ��ȡ��xml�ļ�
		TParm errParm = new TParm();
		// ���濪ʼ��ȡ
		Element root = doc.getDocumentElement(); // ��ȡ��Ԫ��
		NodeList lists = root.getElementsByTagName("Item");
//		System.out.println("lists.len : "+lists.getLength());
		TParm result= new TParm();
		for (int i = 0; i < lists.getLength(); i++) {
			// һ��ȡ��һ��ҩƷ
			Element ss = (Element) lists.item(i);

			//ҩƷ����
			NodeList orderCode = ss.getElementsByTagName("GOODS");
			Element e = (Element) orderCode.item(0);
			Node t = e.getFirstChild();
			result.addData("ORDER_CODE", t.getNodeValue());

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
			
			
/*			//������λ ����
			NodeList billUnit = ss.getElementsByTagName("MSUNITNO");
			e = (Element) billUnit.item(0);
			t = e.getFirstChild();
			result.addData("BILL_UNIT", t.getNodeValue());
			
			//������ ����
			NodeList producer = ss.getElementsByTagName("PRODUCER");
			e = (Element) producer.item(0);
			t = e.getFirstChild();
			result.addData("MAN_CODE", t.getNodeValue());*/
			
/*			//��������-Ŀǰϵͳ��û��
			NodeList prdDate = ss.getElementsByTagName("PRDDATE");
			e = (Element) prdDate.item(0);
			t = e.getFirstChild();
			stu.setSex(t.getNodeValue());
			map.put("PRD_DATE", t.getNodeValue());*/
			
			//Ч��
			NodeList validDate = ss.getElementsByTagName("ENDDATE");
			e = (Element) validDate.item(0);
			t = e.getFirstChild();
			result.addData("VALID_DATE", t.getNodeValue());
//			System.out.println("VALID_DATE: "+t.getNodeValue());
			
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
			
/*			//�ɹ�����ID����ʽΪ12062500000312(DBSCM367)������12062500000312��HISϵͳ�ĵ��ţ���ҪHIS����ȡ��12062500000312 ǰ12λ�Ƕ�����������λ�����
			NodeList pupOrderNo = ss.getElementsByTagName("BILLNO");
			e = (Element) pupOrderNo.item(0);
			t = e.getFirstChild();
			String tString = t.getNodeValue().trim();
			int len = tString.indexOf("(");
			if(len != -1){
				tString = tString.substring(0, len);
				//��ȡ��������
				if(tString.indexOf("-") != -1){
						String[] pruId = tString.split("-");
						result.addData("PURORDER_NO", pruId[0]);
						//��ȡ�������
						result.addData("PURSEQ_NO", pruId[1]);
				}else{
					result = new TParm();
					result.setData("msg", "�����Ÿ�ʽ����");
					break;
				}
			}else{
				result = new TParm();
				result.setData("msg", "�����Ÿ�ʽ����");
				break;
			}*/
			//��Ʊ����
			NodeList invoiceDate = ss.getElementsByTagName("INVOICEDATE");
			e = (Element) invoiceDate.item(0);
			t = e.getFirstChild();
			result.addData("INVOICE_DATE", t.getNodeValue());
		
			result.addData("STOCK_PRICE", 0);
			result.setCount(i);
		}

		return result;
	}
	

	/**
	 * ��ȡ��λ�͵��ӱ�ǩ�Ĺ�ϵxml
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static TParm readXMLFileOfMateriaLocAndOrderCode(String file) throws Exception {
		System.out.println("------------file: "+file);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(file); // ��ȡ��xml�ļ�
		// ���濪ʼ��ȡ
		Element root = doc.getDocumentElement(); // ��ȡ��Ԫ��
		NodeList lists = root.getElementsByTagName("dw_gyyhd_detail_row");
		TParm result= new TParm();
		for (int i = 0; i < lists.getLength(); i++) {
			// һ��ȡ��һ��ҩƷ
			Element ss = (Element) lists.item(i);
			//ҩƷ����
			NodeList orderCode = ss.getElementsByTagName("ORDER_CODE");
			Element e = (Element) orderCode.item(0);
			Node t = e.getFirstChild();
			result.addData("ORDER_CODE", t.getNodeValue());

			//ҩƷ����
			NodeList orderDesc = ss.getElementsByTagName("ORDER_DESC");
			e = (Element) orderDesc.item(0);
			t = e.getFirstChild();
			result.addData("ORDER_DESC", t.getNodeValue());
			
			//���ӱ�ǩ����
			NodeList eleTagCode = ss.getElementsByTagName("ELETAG_CODE");
			e = (Element) eleTagCode.item(0);
			t = e.getFirstChild();
			result.addData("ELETAG_CODE", t.getNodeValue());
			
			//��λ����
			NodeList materLocCode = ss.getElementsByTagName("MATERIAL_LOC_CODE");
			e = (Element) materLocCode.item(0);
			t = e.getFirstChild();
			result.addData("MATERIAL_LOC_CODE", t.getNodeValue());
			
			//��λ����
			NodeList materLocChnDesc = ss.getElementsByTagName("MATERIAL_LOC_DESC");
			e = (Element) materLocChnDesc.item(0);
			t = e.getFirstChild();
			result.addData("MATERIAL_LOC_DESC", t.getNodeValue());
			
			//����
			NodeList orgCode = ss.getElementsByTagName("ORG_CODE");
			e = (Element) orgCode.item(0);
			t = e.getFirstChild();
			result.addData("ORG_CODE", t.getNodeValue());
			
			result.setCount(i);
		}
		System.out.println(result);
		return result;
	}

	  
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
		TParm errParm = new TParm();
		// ���濪ʼ��ȡ
		Element root = doc.getDocumentElement(); // ��ȡ��Ԫ��
		NodeList lists = root.getElementsByTagName("Item");
		System.out.println("lists.len : "+lists);
		TParm result= new TParm();
		for (int i = 0; i < lists.getLength(); i++) {
			// һ��ȡ��һ��ҩƷ
			Element ss = (Element) lists.item(i);

			//ҩƷ����
			NodeList orderCode = ss.getElementsByTagName("GOODS");
			Element e = (Element) orderCode.item(0);
			Node t = e.getFirstChild();
			System.out.println("----"+t);
			result.addData("ORDER_CODE", t.getNodeValue());

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
			
			
/*			//������λ ����
			NodeList billUnit = ss.getElementsByTagName("MSUNITNO");
			e = (Element) billUnit.item(0);
			t = e.getFirstChild();
			result.addData("BILL_UNIT", t.getNodeValue());
			
			//������ ����
			NodeList producer = ss.getElementsByTagName("PRODUCER");
			e = (Element) producer.item(0);
			t = e.getFirstChild();
			result.addData("MAN_CODE", t.getNodeValue());*/
			
/*			//��������-Ŀǰϵͳ��û��
			NodeList prdDate = ss.getElementsByTagName("PRDDATE");
			e = (Element) prdDate.item(0);
			t = e.getFirstChild();
			stu.setSex(t.getNodeValue());
			map.put("PRD_DATE", t.getNodeValue());*/
			
			//Ч��
			NodeList validDate = ss.getElementsByTagName("ENDDATE");
			e = (Element) validDate.item(0);
			t = e.getFirstChild();
			result.addData("VALID_DATE", t.getNodeValue());
//			System.out.println("VALID_DATE: "+t.getNodeValue());
			
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
			
			//�ɹ�����ID����ʽΪ12062500000312(DBSCM367)������12062500000312��HISϵͳ�ĵ��ţ���ҪHIS����ȡ��12062500000312 ǰ12λ�Ƕ�����������λ�����
			NodeList pupOrderNo = ss.getElementsByTagName("BILLNO");
			e = (Element) pupOrderNo.item(0);
			t = e.getFirstChild();
			System.out.println("==PURORDER_NO: "+t);
			result.addData("PURORDER_NO", getValue(t));
/*			int len = tString.indexOf("(");
			if(len != -1){
				tString = tString.substring(0, len);
				//��ȡ��������
				if(tString.indexOf("-") != -1){
						String[] pruId = tString.split("-");
						result.addData("PURORDER_NO", pruId[0]);
						//��ȡ�������
						result.addData("PURSEQ_NO", pruId[1]);
				}else{
					result = new TParm();
					result.setData("msg", "�����Ÿ�ʽ����");
					break;
				}
			}else{
				result = new TParm();
				result.setData("msg", "�����Ÿ�ʽ����");
				break;
			}*/
			//��Ʊ����
			NodeList invoiceDate = ss.getElementsByTagName("INVOICEDATE");
			e = (Element) invoiceDate.item(0);
			t = e.getFirstChild();
			result.addData("INVOICE_DATE", t.getNodeValue());
			

			result.addData("STOCK_PRICE", 0);
			result.setCount(i);
		}

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
		
	
	// д�������ļ�
	public static void callWriteXmlFile(Document doc, Writer w, String encoding) {
		try {
			Source source = new DOMSource(doc);

			Result result = new StreamResult(w);

			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();
			xformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			xformer.transform(source, result);

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}


	@SuppressWarnings("unused")
	private void writeXMLFileFromTParm(TParm parm,String outfile) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (Exception e) {
		}
		Document doc = builder.newDocument();

		Element root = doc.createElement("dw_gyyhd_detail");
		doc.appendChild(root); // ����Ԫ����ӵ��ĵ���
		
		int count = parm.getCount();
		// ��ȡѧ����Ϣ
//		System.out.println("------count��"+count);
		for (int i = 0; i < count; i++) {
//			System.out.println("PURORDER_NO: "+parm.getValue("PURORDER_NO", i));
			// dw_gyyhd_detail_row
			Element stu = doc.createElement("dw_gyyhd_detail_row");
			root.appendChild(stu);
			// ���� ̩��ҽԺ����ڵ� �̶�����
			Element warehouse = doc.createElement("warehouse");
			stu.appendChild(warehouse);
			Text warehouseV = doc.createTextNode("K932290201");
			warehouse.appendChild(warehouseV);
			
			// �����ͻ���ַ����ڵ� �̶�����
			Element deliverycode = doc.createElement("deliverycode");
			stu.appendChild(deliverycode);
			Text deliverycodeV = doc.createTextNode("932290201");
			deliverycode.appendChild(deliverycodeV);

			// �����������Žڵ�
			Element requestNo = doc.createElement("requestNo");
			stu.appendChild(requestNo); // ������������ӵ�dw_gyyhd_detail_row����
			Text requestNoV = doc.createTextNode(parm.getValue("PURORDER_NO", i));
			requestNo.appendChild(requestNoV); // ���ı��ڵ���ڶ������Žڵ���
		}
		try {
			FileOutputStream fos = new FileOutputStream(outfile);
			OutputStreamWriter outwriter = new OutputStreamWriter(fos);
			// ((XmlDocument)doc).write(outwriter); //����
			callWriteXmlFile(doc, outwriter, "gb2312");
			outwriter.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
//		String str = "C:/student.xml";
		String str = "C:/hisupload/drog/invoice.xml";
		FileUtils t = new FileUtils();
	/*	try {
			Map<String, Object>v = t.readXMLFile(str);
			Set<String> set = v.keySet();
			Iterator it = set.iterator();
			int ia = 0;
			while (it.hasNext()) {
				String s = (String) it.next();
				Map<String, Object> map = (Map<String, Object>) v.get(s);
				Iterator<String> i = map.keySet().iterator();
				while (i.hasNext()) {
					String type = (String) i.next();
					System.out.println(type+":"+map.get(type));
				}
				System.out.println(ia++ + "--------------");
			}
		} catch (Exception e) {
			e.printStackTrace();

		}*/
		try {
			TParm a  = FileUtils.readXMLFileP("C:/upload/00000688.xml");
			System.out.println("a:"+a);			
//			TParm a  = FileUtils.readXMLFileOfMateriaLocAndOrderCode("c:/temp/123.xml");
//			System.out.println("a:"+a);
//			System.out.println("---cont: "+a.getCount());
/*			for(int i = 0 ; i < a.getCount(); i++){
				System.out.println("ORDER_CODE:"+a.getData("ORDER_CODE", i));
				System.out.println("PURORDER_NO:"+a.getData("PURORDER_NO", i));
				System.out.println("PURSEQ_NO:"+a.getData("", i));
			}*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		



/*	 String outfile = "C:/stucopy.xml";
		t.writeXMLFile(outfile);
 
		XmlCreater t1 = new XmlCreater();
		 String outfile1 = "C:/stucopy1.xml";
		try {
		//	t.writeXMLFile(outfile);
			t1.writeXMLFileFromTParm(null,outfile1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
