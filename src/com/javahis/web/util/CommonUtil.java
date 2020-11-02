package com.javahis.web.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.dongyang.data.TParm;
import com.javahis.web.form.SysEmrIndexForm;
import com.javahis.web.jdo.CommonTool;
import java.util.GregorianCalendar;
import java.util.Calendar;

import jdo.sys.SystemTool;

public class CommonUtil {
	/**
	 * ҳ���ʼ��
	 */
	public static void onInit(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		CommonTool ct = new CommonTool();
		// �������
		String deptSelect = CommonUtil.getDeptSelect(ct.getDeptList());
		// �ż�ס��
		String admTypeCheckBox = CommonUtil.getAdmTypeCheckBox(ct
				.getDictionaryList("SYS_ADMTYPE"));
		// ��������
		DateFormat dateFormatBegin = new SimpleDateFormat("yyyy-MM");
		DateFormat dateFormatEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date sysDate = new Date();
                //6����ǰ
                GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
                gc.setTime(sysDate);
                gc.add(2, -6);
                Date startDate=gc.getTime();



		String admDateBegin = dateFormatBegin.format(startDate) + "-01 00:00:00";


		String admDateEnd = dateFormatEnd.format(sysDate);
		// �����ʽ
		String viewPatternSelect = CommonUtil.getViewPatternSelect();
		// ����ʽ1
		String sort1Select = CommonUtil.getSortSelect(1);
		// ����ʽ2
		String sort2Select = CommonUtil.getSortSelect(2);
		// ����ʽ3
		String sort3Select = CommonUtil.getSortSelect(3);
		// ��ʼ����Ϣ
		request.setAttribute("deptSelect", deptSelect);
		request.setAttribute("admTypeCheckBox", admTypeCheckBox);
		request.setAttribute("admDateBegin", admDateBegin);
		request.setAttribute("admDateEnd", admDateEnd);
		request.setAttribute("viewPatternSelect", viewPatternSelect);
		request.setAttribute("sort1Select", sort1Select);
		request.setAttribute("sort2Select", sort2Select);
		request.setAttribute("sort3Select", sort3Select);
		//Mr_No������
		String Mr_No = request.getParameter("Mr_No");
		request.setAttribute("Mr_No", Mr_No);
//		request.getSession().setAttribute("Mr_No", Mr_No);
//		TSocket socket = new com.dongyang.data.TSocket("localhost", 8080, "web");
//		TIOM_AppServer.SOCKET = socket;
	}

	/**
	 * ���ֵ䣨�ż�ס��������
	 */
	public static String getDictionarySelect(TParm parm) {
		String dictionarySelect = "";
		for (int i = 0; i < parm.getCount("ID"); i++) {
			dictionarySelect += "<option value='" + parm.getValue("ID", i)
					+ "'>" + parm.getValue("CHN_DESC", i) + "</option>";
		}
		return dictionarySelect;
	}

	/**
	 * �ż�ס��ѡ��
	 */
	public static String getAdmTypeCheckBox(TParm parm) {
		String admTypeCheckBox = "";
		for (int i = 0; i < parm.getCount("ID"); i++) {
			admTypeCheckBox += "<input name='ADM_TYPE_" + i + "' id='ADM_TYPE_" + i
					+ "' type='checkbox' value='" + parm.getValue("ID", i)
					+ "' checked='checked'>";
			admTypeCheckBox += "<label for='ADM_TYPE_" + i + "'>"
					+ parm.getValue("CHN_DESC", i) + "</label>";
		}
		return admTypeCheckBox;
	}

	/**
	 * �������������
	 */
	public static String getDeptSelect(TParm parm) {
		String deptSelect = "";
		for (int i = 0; i < parm.getCount("DEPT_CODE"); i++) {
			deptSelect += "<option value='" + parm.getValue("DEPT_CODE", i)
					+ "'>" + parm.getValue("DEPT_CHN_DESC", i) + "</option>";
		}
		return deptSelect;
	}

	/**
	 * ����ʽ
	 */
	public static String getSortSelect(int flg) {
		String sortSelect = "";
		sortSelect += "<option value='ADM_DATE'>����ʱ��</option>";
		sortSelect += "<option value='ADM_TYPE'>�ż�ס��</option>";
		sortSelect += "<option value='DEPT_CODE'>�������</option>";
		return sortSelect;
	}

	/**
	 * �����ʽ
	 */
	public static String getViewPatternSelect() {
		String viewPatternSelect = "";
		viewPatternSelect += "<option value='HTML'>HTML</option>";
		viewPatternSelect += "<option value='PDF'>PDF</option>";
		viewPatternSelect += "<option value='JHW' selected>JHW</option>";
		viewPatternSelect += "<option value='CDA'>CDA</option>";
		return viewPatternSelect;
	}

	/**
	 * ��ѯ����
	 */
	public static SysEmrIndexForm getForm(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		SysEmrIndexForm form = new SysEmrIndexForm();
		form.setAdm_date_begin(request.getParameter("ADM_DATE_BEGIN"));
		form.setAdm_date_end(request.getParameter("ADM_DATE_END"));
		form.setAdm_type_0(request.getParameter("ADM_TYPE_0"));
		form.setAdm_type_1(request.getParameter("ADM_TYPE_1"));
		form.setAdm_type_2(request.getParameter("ADM_TYPE_2"));
		form.setAdm_type_3(request.getParameter("ADM_TYPE_3"));
		form.setDept_code(request.getParameter("DEPT_CODE"));
		form.setSort1(request.getParameter("SORT1"));
		form.setSort2(request.getParameter("SORT2"));
		form.setSort3(request.getParameter("SORT3"));
		form.setView_pattern(request.getParameter("VIEW_PATTERN"));
		form.setCase_no(request.getParameter("CASE_NO"));
		form.setMr_no(request.getParameter("Mr_No"));
		return form;
	}


	/**
     * �ַ����ǿ���֤
     */
    public static boolean checkInputString(Object obj) {
        if (obj == null) {
            return false;
        }
        String str = String.valueOf(obj);
        if (str == null) {
            return false;
        }
        else if ("".equals(str.trim())) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * ����xml�ĵ�����
     */
	public static Document createXMLDoc() {
		Document XMLDoc = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			XMLDoc = builder.newDocument();
		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		}
		return XMLDoc;
	}

	/**
     * ����xml���ڵ�Ԫ��
     */
	public static Node XMLRoot(Document XMLDoc, String name, TParm attributes) {
		Element element = XMLDoc.createElement(name);
		XMLDoc.appendChild(element);
		String names[] = attributes.getNames();
		for (int i = 0; i < names.length; i++)
			element.setAttribute(names[i], attributes.getValue(names[i]));
		return element;
	}

	/**
     * ����xml�Ǹ��ڵ�Ԫ��
     */
	public static Node XMLElement(Document XMLDoc, String name, TParm attributes, Node parent) {
		Element element = null;
		try {
			element = XMLDoc.createElement(name);
			parent.appendChild(element);
			String names[] = attributes.getNames();
			for (int i = 0; i < names.length; i++)
				element.setAttribute(names[i], attributes.getValue(names[i]));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return element;
	}

	/**
     * ��ȡxml�ַ���
     */
	public static String getXMLString(Document XMLDoc) {
		StringWriter pw = new StringWriter();
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(XMLDoc);
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return pw.toString();
	}

	/**
	 * ��ȡdhtmltree�������xmlͷ��Ϣ
	 */
	public static TParm getTreeAttributes() {
		TParm attributes = new TParm();
		attributes.setData("id", "0");
		return attributes;
	}

	/**
	 * ���ɾ������������νṹxml�ַ���
	 */
	public static String getSysEmrIndexTree(SysEmrIndexForm form) {
		TParm parm = CommonUtil.getSysEmrIndexParm(form);
		Document XMLDoc = CommonUtil.createXMLDoc();
		Node nodeTree = CommonUtil.XMLRoot(XMLDoc, "tree", CommonUtil.getTreeAttributes());
		Node nodeRoot = CommonUtil.XMLElement(XMLDoc, "item", CommonUtil.getSysEmrIndexRootAttributes(form), nodeTree);
		int count = parm.getCount("CASE_NO");
		if (!CommonUtil.checkInputString(form.getSort1())
				|| !CommonUtil.checkInputString(form.getSort2())
				||!CommonUtil.checkInputString(form.getSort3())) {
			for(int i=0;i<count;i++) {
				TParm attributes = new TParm();
				String desc = parm.getData("ADM_DATE", i)
				+ "," + parm.getData("ADM_TYPE", i)
				+ "," + parm.getData("DEPT_CODE", i);
				attributes.setData("text", desc);
				attributes.setData("id", parm.getData("CASE_NO", i));
				XMLElement(XMLDoc, "item", attributes, nodeRoot);
			}
		} else {
			for(int i=0;i<count;i++) {
				TParm attributes = new TParm();
				String desc = parm.getData(form.getSort1(), i)
				+ "," + parm.getData(form.getSort2(), i)
				+ "," + parm.getData(form.getSort3(), i);
				attributes.setData("text", desc);
				attributes.setData("id", parm.getData("CASE_NO", i));
				XMLElement(XMLDoc, "item", attributes, nodeRoot);
			}
		}
		String SysEmrIndexTree = getXMLString(XMLDoc);
		return SysEmrIndexTree;
	}

	/**
	 * �������������ڵ�Ԫ����Ϣ
	 */
	public static TParm getSysEmrIndexRootAttributes(SysEmrIndexForm form) {
		TParm attributes = new TParm();
		TParm patInfoParm = CommonUtil.getPatInfoByMrNo(form);
		if (patInfoParm.getCount("PAT_NAME") > 0) {
			attributes.setData("text", "����������" + patInfoParm.getValue("PAT_NAME"));
		} else {
			attributes.setData("text", "����������");
		}

		attributes.setData("id", "Root");
		attributes.setData("open", "1");
		attributes.setData("call", "1");
		attributes.setData("select", "1");
		return attributes;
	}

	/**
	 * ���ݲ����Ų�ѯ������Ϣ
	 */
	public static TParm getPatInfoByMrNo(SysEmrIndexForm form) {
		CommonTool ct = new CommonTool();
		return ct.getPatInfoByMrNo(form);
	}

	/**
	 * ��������������
	 */
	public static TParm getSysEmrIndexParm(SysEmrIndexForm form) {
		CommonTool ct = new CommonTool();
		return ct.getEmrIndexParm(form);
	}

	/**
	 * ���ɵ��Ӳ������νṹxml�ַ���
	 */
    public static String getEmrFileIndexTree(SysEmrIndexForm form){
    	Document XMLDoc = CommonUtil.createXMLDoc();
		Node nodeTree = CommonUtil.XMLRoot(XMLDoc, "tree", CommonUtil.getTreeAttributes());
		Node nodeRoot = CommonUtil.XMLElement(XMLDoc, "item", CommonUtil.getEmrFileIndexRootAttributes(form), nodeTree);

		TParm mainParm = CommonUtil.getEmrClassParm(form);
//		System.out.println("start---"+SystemTool.getInstance().getDate());
		TParm emrParm = CommonUtil.queryEmrTempletFileIndexParm(form);
//		System.out.println("emrParm---"+emrParm);
//		System.out.println("emrParm.count---"+emrParm.getCount());
		putDataInXMLDoc(XMLDoc, nodeRoot, mainParm, form, emrParm);
//		System.out.println("end---"+SystemTool.getInstance().getDate());
        String EmrFileIndexTree = CommonUtil.getXMLString(XMLDoc);
        return EmrFileIndexTree;
    }
    
    /**
     * �ݹ��㷨���ɵ��Ӳ������νṹ��Ҷ�ڵ㲻�ٲ�ѯ��
     */
    public static void putDataInXMLDoc(Document XMLDoc, Node parentElement, TParm data, SysEmrIndexForm form, TParm emrParm) {
    	String classCode = "";
    	if (CommonUtil.checkInputString(((Element)parentElement).getAttribute("ROOT"))) {
    		classCode = ((Element)parentElement).getAttribute("ROOT");
    	} else {
    		classCode = ((Element)parentElement).getAttribute("id");
    	}
    	for(int i=0;i<data.getInt("ACTION","COUNT");i++) {
    		TParm emrCLassParm = data.getRow(i);
    		if(classCode.equals(emrCLassParm.getValue("PARENT_CLASS_CODE"))) {
    			TParm emrCLassAttributes = new TParm();
    			emrCLassAttributes.setData("text", emrCLassParm.getValue("CLASS_DESC"));
    			emrCLassAttributes.setData("id", emrCLassParm.getValue("CLASS_CODE"));
    			Node element = CommonUtil.XMLElement(XMLDoc, "item", emrCLassAttributes, parentElement);

    			//�ݹ����
    			CommonUtil.putDataInXMLDoc(XMLDoc, element, data, form);

//    			TParm filesParm = CommonUtil.getEmrTempletFileIndexParm(emrCLassParm.getValue("CLASS_CODE"), form);
    			
    			TParm filesParm = new TParm();
    			if(emrParm.getCount()>0){
    				for(int m=0;m<emrParm.getCount();m++){
        				TParm tp = emrParm.getRow(m);
        				if(tp.getData("CLASS_CODE").equals(emrCLassParm.getValue("CLASS_CODE"))){
        					filesParm.addRowData(tp, filesParm.getCount());
        				}
        			}
    				for(int j=0;j<filesParm.getCount();j++) {
        				TParm fileParm = filesParm.getRow(j);
        				TParm fileAttributes = new TParm();
        				fileAttributes.setData("text", fileParm.getValue("DESIGN_NAME"));
        				fileAttributes.setData("id", "EmrFileIndex_" + fileParm.getValue("CASE_NO") + "_" + fileParm.getValue("FILE_SEQ"));
        				XMLElement(XMLDoc, "item", fileAttributes, element);
        			}
    			}
    		}
    	}
    }

    /**
     * �ݹ��㷨���ɵ��Ӳ������νṹ
     */
    public static void putDataInXMLDoc(Document XMLDoc, Node parentElement, TParm data, SysEmrIndexForm form) {
    	String classCode = "";
    	if (CommonUtil.checkInputString(((Element)parentElement).getAttribute("ROOT"))) {
    		classCode = ((Element)parentElement).getAttribute("ROOT");
    	} else {
    		classCode = ((Element)parentElement).getAttribute("id");
    	}
    	for(int i=0;i<data.getInt("ACTION","COUNT");i++) {
    		TParm emrCLassParm = data.getRow(i);
    		if(classCode.equals(emrCLassParm.getValue("PARENT_CLASS_CODE"))) {
    			TParm emrCLassAttributes = new TParm();
    			emrCLassAttributes.setData("text", emrCLassParm.getValue("CLASS_DESC"));
    			emrCLassAttributes.setData("id", emrCLassParm.getValue("CLASS_CODE"));
    			Node element = CommonUtil.XMLElement(XMLDoc, "item", emrCLassAttributes, parentElement);

//    			if("N".equals(emrCLassParm.getValue("LEAF_FLG"))) {
    				CommonUtil.putDataInXMLDoc(XMLDoc, element, data, form);
//    			}
    			TParm filesParm = CommonUtil.getEmrTempletFileIndexParm(emrCLassParm.getValue("CLASS_CODE"), form);
    			for(int j=0;j<filesParm.getInt("ACTION","COUNT");j++) {
    				TParm fileParm = filesParm.getRow(j);
    				TParm fileAttributes = new TParm();
    				fileAttributes.setData("text", fileParm.getValue("DESIGN_NAME"));
    				fileAttributes.setData("id", "EmrFileIndex_" + fileParm.getValue("CASE_NO") + "_" + fileParm.getValue("FILE_SEQ"));
    				XMLElement(XMLDoc, "item", fileAttributes, element);
    			}
    		}
    	}
    }

    /**
     * ���Ӳ������ڵ�Ԫ����Ϣ
     */
	public static TParm getEmrFileIndexRootAttributes(SysEmrIndexForm form) {
		CommonTool ct = new CommonTool();
		TParm rootParm = ct.getEmrFileIndexRootAttributes();
		TParm attributes = new TParm();
		attributes.setData("text", "���Ӳ���");
		attributes.setData("ROOT", rootParm.getValue("CLASS_CODE", 0));
		attributes.setData("id", "ROOT_" + form.getMr_no() + "_" + form.getCase_no());
		attributes.setData("open", "1");
		attributes.setData("call", "1");
		attributes.setData("select", "1");
		return attributes;
	}

	/**
	 * ���Ӳ���Ŀ¼
	 */
    public static TParm getEmrClassParm(SysEmrIndexForm form) {
    	CommonTool ct = new CommonTool();
    	return ct.getEmrClassParm(form);
    }

    /**
     * ���Ӳ����ĵ�
     */
    public static TParm getEmrTempletFileIndexParm(String classCode, SysEmrIndexForm form) {
    	CommonTool ct = new CommonTool();
    	return ct.getEmrTempletFileIndexParm(classCode, form);
    }
    
    /**
     * ��ѯ���һ�����ڵ㣨���Ӳ�����
     */
    public static TParm queryEmrTempletFileIndexParm(SysEmrIndexForm form) {
    	CommonTool ct = new CommonTool();
    	return ct.queryEmrTempletFileIndexParm(form);
    }

    public static TParm getEmrFileIndexObject(String caseNo, String fileSeq) {
    	TParm parm = new TParm();
    	parm.setData("CASE_NO", caseNo);
    	parm.setData("FILE_SEQ", fileSeq);
    	CommonTool ct = new CommonTool();
    	return ct.getEmrFileIndexObject(parm);
    }

    public static boolean isFileExists(String filePath) {
        File f = new File(filePath);
    	if (f.exists() && f.isFile()) {
    		return true;
    	} else {
    		return false;
    	}
    }
    /**
     * ����xml�ĵ�����
     */
	public static String getXMLString(String uri) {
		StringWriter pw = new StringWriter();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document XMLDoc = builder.parse(new File(uri));
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(XMLDoc);
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return pw.toString();
	}

	public static String changToXML(String xmlpath, String xslpath) {
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		dbfactory.setNamespaceAware(true);
		DocumentBuilder domparser;
		try {
			domparser = dbfactory.newDocumentBuilder();
			Document doc = domparser.parse(new File(xmlpath));
			Source xmlsource = new DOMSource(doc);
			TransformerFactory xslfactory = TransformerFactory.newInstance();
			// create a transformer
			Transformer xformer = xslfactory.newTransformer(new StreamSource(
					xslpath));
			// StreamResult result = new StreamResult();
			StringWriter sw = new StringWriter();
			xformer.transform(xmlsource, new StreamResult(sw));
			return sw.toString();
			// xformer.transform(xmlsource, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) {
		String xmlFilePath = "C:/JavaHisFile/EmrFileData/EmrData/XML/11/07/000000002555/110705000005_��Ժ��¼_2_CDA.xml";
		String xslFilePath = "C:/JavaHisFile/EmrFileData/EmrTemplet/JHW/��Ժ��¼/��Ժ��¼/��Ժ��¼.xsl";
		String cdaXml = CommonUtil.changToXML(xmlFilePath, xslFilePath);
		System.out.println(cdaXml);
	}
}
