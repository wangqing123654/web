package jdo.bms.ws;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * 
 * @author shibl
 * 
 */
public class XMLUtil {
	/**
	 * ���뵥xml
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getApplyXMLString(TParm parm,String admType) throws Exception {
		// ������������
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// ������
		DocumentBuilder builder = factory.newDocumentBuilder();
		// ������Document����
		Document document = builder.newDocument();
		// ����XML�İ汾
		document.setXmlVersion("1.0");
		// �������ڵ�
		Element root = document.createElement("OrderList");
		// �����ڵ���ӵ�Document������
		document.appendChild(root);
		// ���õ�һ��OrderFormԪ�ص�
		Element OrderForm = document.createElement("OrderForm");
		// �����
		Element BOrderFormID = document.createElement("BOrderFormID");
		BOrderFormID.setTextContent(parm.getValue("APPLY_NO"));
		OrderForm.appendChild(BOrderFormID);
		// ��������
		Element OrderTime = document.createElement("OrderTime");
		OrderTime.setTextContent(StringTool.getString(parm
				.getTimestamp("PRE_DATE"), "yyyy-MM-dd HH:mm:ss"));
		OrderForm.appendChild(OrderTime);
		// ������
		Element PatID = document.createElement("PatID");
		PatID.setTextContent(parm.getValue("MR_NO"));
		OrderForm.appendChild(PatID);
		// ��������
		Element PatNo = document.createElement("PatNo");
		PatNo.setTextContent(parm.getValue("CASE_NO"));
		OrderForm.appendChild(PatNo);
		// ��������
		Element Cname = document.createElement("Cname");
		Cname.setTextContent(parm.getValue("PAT_NAME"));
		OrderForm.appendChild(Cname);
		// �����Ա�
		Element Sex = document.createElement("Sex");
		Sex.setTextContent(parm.getValue("SEX"));
		OrderForm.appendChild(Sex);
		// ��������
		String[] res;
		res = StringTool.CountAgeByTimestamp(parm.getTimestamp("BIRTH_DATE"),
				StringTool.getTimestamp(StringTool.getString(parm
						.getTimestamp("IN_DATE"), "yyyyMMdd"), "yyyyMMdd"));
		int age = 0;// ��λ����
		String ageunit = "";
		if (TypeTool.getInt(res[0]) < 1) {
			age = StringTool.getDateDiffer(StringTool.getTimestamp(StringTool
					.getString(parm.getTimestamp("IN_DATE"), "yyyyMMdd"),
					"yyyyMMdd"), parm.getTimestamp("BIRTH_DATE"));
			age=age+1;
			ageunit = "��";
		} else if (TypeTool.getInt(res[0]) >= 1) {
			age = TypeTool.getInt(res[0].equals("") ? 0 : res[0]);// 11 ����
			ageunit = "��";
		}
		Element Age = document.createElement("Age");
		Age.setTextContent(age + "");
		OrderForm.appendChild(Age);
		// �������䵥λ
		Element AgeUnit = document.createElement("AgeUnit");
		AgeUnit.setTextContent(ageunit);
		OrderForm.appendChild(AgeUnit);
		// ���߳�������
		Element Birthday = document.createElement("Birthday");
		Birthday.setTextContent(StringTool.getString(parm
				.getTimestamp("BIRTH_DATE"), "yyyy-MM-dd HH:mm:ss"));
		OrderForm.appendChild(Birthday);
		// ����
		Element DeptID = document.createElement("DeptID");
		DeptID.setTextContent(parm.getValue("DEPT_CODE"));
		OrderForm.appendChild(DeptID);
		
		Element DeptName = document.createElement("DeptName");
		DeptName.setTextContent(parm.getValue("DEPT_DESC"));
		OrderForm.appendChild(DeptName);
		
		// ��λ
		Element bed = document.createElement("Bed");
		bed.setTextContent(parm.getValue("BED_DESC"));
		OrderForm.appendChild(bed);
		
		// ҽ��
		Element DoctorID = document.createElement("DoctorID");
		DoctorID.setTextContent(parm.getValue("DR_CODE"));
		OrderForm.appendChild(DoctorID);
		
		Element DoctorName = document.createElement("DoctorName");
		DoctorName.setTextContent(parm.getValue("DR_DESC"));
		OrderForm.appendChild(DoctorName);
		
		// ���
		Element Diag = document.createElement("Diag");
		Diag.setTextContent(parm.getValue("DIAG_DESC1"));
		OrderForm.appendChild(Diag);
		
		// ������Ѫʷ
		Element BeforUse = document.createElement("BeforUse");
		BeforUse.setTextContent("");
		OrderForm.appendChild(BeforUse);
		// ����ʷ
		Element Gravida = document.createElement("Gravida");
		Gravida.setTextContent("");
		OrderForm.appendChild(Gravida);
		// ��Ѫ������Ӧʷ
		Element Harm = document.createElement("Harm");
		Harm.setTextContent("");
		OrderForm.appendChild(Harm);
		// ������Ѫʷ
		Element Help = document.createElement("Help");
		Help.setTextContent("");
		OrderForm.appendChild(Help);
		// ��ҩʷ
		Element Drag = document.createElement("Drag");
		Drag.setTextContent("");
		OrderForm.appendChild(Drag);
		// ��������
		Element AddressType = document.createElement("AddressType");
		AddressType.setTextContent("");
		OrderForm.appendChild(AddressType);
		// ��Ѫ��״̬ID
		Element BPatStatusID = document.createElement("BPatStatusID");
		BPatStatusID.setTextContent("");
		OrderForm.appendChild(BPatStatusID);
		// ʹ��ʱ��
		Element UseTime = document.createElement("UseTime");
		UseTime.setTextContent(StringTool.getString(parm
				.getTimestamp("USE_DATE"), "yyyy-MM-dd HH:mm:ss"));
		OrderForm.appendChild(UseTime);
		// ��Ѫ��ʽ
		Element UseTypeID = document.createElement("UseTypeID");
		UseTypeID.setTextContent("");
		OrderForm.appendChild(UseTypeID);
		// ��Ѫʱ������
		Element BUseTimeTypeID = document.createElement("BUseTimeTypeID");
		BUseTimeTypeID.setTextContent(admType.equals("E")?"1":"");
		OrderForm.appendChild(BUseTimeTypeID);
		// ��ѪĿ��
		Element UsePurpose = document.createElement("UsePurpose");
		UsePurpose.setTextContent("");
		OrderForm.appendChild(UsePurpose);
		// Ԥ��
		Element BABORHID = document.createElement("BABORHID");
		BABORHID.setTextContent("");
		OrderForm.appendChild(BABORHID);
		// Ѫ��
		Element BPatABO = document.createElement("BPatABO");
		BPatABO.setTextContent(parm.getValue("TEST_BLD"));
		OrderForm.appendChild(BPatABO);
		// RH
		String rh = "";
		if (parm.getValue("BLOOD_RH_TYPE").equals("+")) {
			rh = "����";
		} else if (parm.getValue("BLOOD_RH_TYPE").equals("-")) {
			rh = "����";
		}
		Element BPatRH = document.createElement("BPatRH");
		BPatRH.setTextContent(rh);
		OrderForm.appendChild(BPatRH);
		// ��ע
		Element Memo = document.createElement("Memo");
		Memo.setTextContent(parm.getValue("REMARK"));
		OrderForm.appendChild(Memo);
		// ѪƷ�б�
		Element OrderItems = document.createElement("OrderItems");
		TParm bloodParm = parm.getParm("DATA");
		for (int i = 0; i < bloodParm.getCount(); i++) {
			TParm bloodrow = bloodParm.getRow(i);
			// ѪƷ�б�
			Element OrderItem = document.createElement("OrderItem");
			// ������ϸID
			Element BOrderItemID = document.createElement("BOrderItemID");
			BOrderItemID.setTextContent(bloodrow.getValue("APPLY_NO")
					+ bloodrow.getValue("BLD_CODE"));
			OrderItem.appendChild(BOrderItemID);
			// ���뵥��
			Element BBOrderItemID = document.createElement("BOrderItemID");
			BBOrderItemID.setTextContent(bloodrow.getValue("APPLY_NO"));
			OrderItem.appendChild(BBOrderItemID);
			// ѪƷ����
			Element BloodID = document.createElement("BloodID");
			BloodID.setTextContent(bloodrow.getValue("BLD_CODE"));
			OrderItem.appendChild(BloodID);
			// ѪƷ����
			Element BloodName = document.createElement("BloodName");
			BloodName.setTextContent(bloodrow.getValue("BLDCODE_DESC"));
			OrderItem.appendChild(BloodName);
			// ѪƷ��λid
			Element BUnitID = document.createElement("BUnitID");
			BUnitID.setTextContent(bloodrow.getValue("UNIT_CODE"));
			OrderItem.appendChild(BUnitID);
			// ѪƷ��λ����
			Element Bunit = document.createElement("Bunit");
			Bunit.setTextContent(bloodrow.getValue("UNIT_CHN_DESC"));
			OrderItem.appendChild(Bunit);
			// ѪƷ����
			Element BOrderCount = document.createElement("BOrderCount");
			BOrderCount.setTextContent(bloodrow.getValue("APPLY_QTY"));
			OrderItem.appendChild(BOrderCount);
			// ѪƷѪ��
			Element BApplyBld = document.createElement("BApplyBld");
			BApplyBld.setTextContent(bloodrow.getValue("APPLY_BLD"));
			OrderItem.appendChild(BApplyBld);
			// ѪƷRH
			Element BApplyRH = document.createElement("BApplyRH");
			BApplyRH.setTextContent(bloodrow.getValue("APPLY_RH_TYPE"));
			OrderItem.appendChild(BApplyRH);
			// ��ע
			Element MemoD = document.createElement("Memo");
			MemoD.setTextContent("");
			OrderItem.appendChild(MemoD);
			OrderItems.appendChild(OrderItem);
		}
		OrderForm.appendChild(OrderItems);
		// ��OrderForm�μӸ��ڵ���
		root.appendChild(OrderForm);
		// ��ʼ��Documentӳ�䵽�ļ�
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transFormer = transFactory.newTransformer();
		transFormer.setOutputProperty("encoding", "gb2312");
		// ����������
		DOMSource domSource = new DOMSource(document);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transFormer.transform(domSource, result);
		return writer.toString();
	}

	/**
	 * ��Ѫ��Ӧxml
	 * 
	 * @param parm
	 * @return
	 * @throws Exception
	 */
	public String getReactXmlString(TParm parm) throws Exception {
		// ������������
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// ������
		DocumentBuilder builder = factory.newDocumentBuilder();
		// ������Document����
		Document document = builder.newDocument();
		// ����XML�İ汾
		document.setXmlVersion("1.0");
		// �������ڵ�
		Element root = document.createElement("HarmList");
		// �����ڵ���ӵ�Document������
		document.appendChild(root);
		// ���õ�һ��HarmFormԪ�ص�
		Element HarmForm = document.createElement("HarmForm");
		// ��Ӧ��
		Element BHarmFormID = document.createElement("BHarmFormID");
		BHarmFormID.setTextContent(parm.getValue("REACT_NO"));
		HarmForm.appendChild(BHarmFormID);
		// ����
		Element deptID = document.createElement("deptID");
		deptID.setTextContent(parm.getValue("DEPT_CODE"));
		HarmForm.appendChild(deptID);
		// ����
		Element wardID = document.createElement("wardID");
		wardID.setTextContent(parm.getValue("STATION_CODE"));
		HarmForm.appendChild(wardID);
		// ������
		Element patID = document.createElement("patID");
		patID.setTextContent(parm.getValue("MR_NO"));
		HarmForm.appendChild(patID);
		// �����
		Element patNo = document.createElement("patNo");
		patNo.setTextContent(parm.getValue("CASE_NO"));
		HarmForm.appendChild(patNo);
		// סԺ��
		Element AdmissID = document.createElement("AdmissID");
		AdmissID.setTextContent(parm.getValue("IPD_NO"));
		HarmForm.appendChild(AdmissID);
		// ����
		Element PatName = document.createElement("PatName");
		PatName.setTextContent(parm.getValue("PAT_NAME"));
		HarmForm.appendChild(PatName);
		// �Ա�
		Element sex = document.createElement("sex");
		sex.setTextContent(parm.getValue("SEX"));
		HarmForm.appendChild(sex);
		// ��������
		String[] res;
		res = StringTool.CountAgeByTimestamp(parm.getTimestamp("BIRTH_DATE"),
				StringTool.getTimestamp(StringTool.getString(parm
						.getTimestamp("IN_DATE"), "yyyyMMdd"), "yyyyMMdd"));
		int age = 0;// ��λ����
		String ageunit = "";
		if (TypeTool.getInt(res[0]) < 1) {
			age = StringTool.getDateDiffer(StringTool.getTimestamp(StringTool
					.getString(parm.getTimestamp("IN_DATE"), "yyyyMMdd"),
					"yyyyMMdd"), parm.getTimestamp("BIRTH_DATE"));
			ageunit = "��";
		} else if (TypeTool.getInt(res[0]) >= 1) {
			age = TypeTool.getInt(res[0].equals("") ? 0 : res[0]);// 11 ����
			ageunit = "��";
		}
		Element Age = document.createElement("Age");
		Age.setTextContent(age + "");
		HarmForm.appendChild(Age);
		// �������䵥λ
		Element AgeUnit = document.createElement("AgeUnit");
		AgeUnit.setTextContent(ageunit);
		HarmForm.appendChild(AgeUnit);
		// ���֤
		Element IDCard = document.createElement("IDCard");
		IDCard.setTextContent(parm.getValue("IDNO"));
		HarmForm.appendChild(IDCard);
		// Ѫ��
		Element ABO = document.createElement("ABO");
		ABO.setTextContent(parm.getValue("BLOOD_TYPE"));
		HarmForm.appendChild(ABO);
		// RH
		Element RHD = document.createElement("RHD");
        String rh = "";
        if (parm.getValue("BLOOD_RH_TYPE").equals("+")) {
            rh = "����";
        } else if (parm.getValue("BLOOD_RH_TYPE").equals("-")) {
            rh = "����";
        }
        RHD.setTextContent(rh);// wanglong modify 20140811
		HarmForm.appendChild(RHD);
		// ����Ѫ��
		Element TestABO = document.createElement("TestABO");
		TestABO.setTextContent(parm.getValue("BLOOD_TYPE"));
		HarmForm.appendChild(TestABO);
		// ��Ѫʱ��
		Element UseStartTime = document.createElement("UseStartTime");
		String stime = StringTool.getString(parm.getTimestamp("START_DATE"),
				"yyyy-MM-dd HH:mm:ss");
		UseStartTime.setTextContent(stime);
		HarmForm.appendChild(UseStartTime);
		// ���ʱ��
		Element UseEndTime = document.createElement("UseEndTime");
		String etime = StringTool.getString(parm.getTimestamp("END_DATE"),
				"yyyy-MM-dd HH:mm:ss");
		UseEndTime.setTextContent(etime);
		HarmForm.appendChild(UseEndTime);
		// �з�Ӧ��Ѫ��Ʒ
		Element HarmBlood = document.createElement("HarmBlood");
		HarmBlood.setTextContent(parm.getValue("BLD_CODE"));
		HarmForm.appendChild(HarmBlood);
		// ��Ӧ�ȼ�
		Element HarmGrade = document.createElement("HarmGrade");
		HarmGrade.setTextContent(parm.getValue("REACT_CLASS"));
		HarmForm.appendChild(HarmGrade);
		// ��Ѫ��Ӧ
		Element HarmName = document.createElement("HarmName");
		HarmName.setTextContent(parm.getValue("REACTION_CODE"));
		HarmForm.appendChild(HarmName);
		// ������Ӧ
		Element OtherHarm = document.createElement("OtherHarm");
		OtherHarm.setTextContent(parm.getValue("REACT_OTH"));
		HarmForm.appendChild(OtherHarm);
		// ������Ӧʷ
		Element allergy = document.createElement("allergy");
		allergy.setTextContent(parm.getValue("REACT_HIS"));
		HarmForm.appendChild(allergy);
		// ��Ӧ֢״
		Element HarmSym = document.createElement("HarmSym");
		HarmSym.setTextContent(parm.getValue("RECAT_SYMPTOM"));
		HarmForm.appendChild(HarmSym);
		// ����
		Element therapy = document.createElement("therapy");
		therapy.setTextContent(parm.getValue("TREAT"));
		HarmForm.appendChild(therapy);
		// �Ʊ���
		Element MakerID = document.createElement("MakerID");
		MakerID.setTextContent(parm.getValue("OPT_USER"));
		HarmForm.appendChild(MakerID);
		// ��HarmForm�μӸ��ڵ���
		root.appendChild(HarmForm);
		// ��ʼ��Documentӳ�䵽�ļ�
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transFormer = transFactory.newTransformer();
		transFormer.setOutputProperty("encoding", "gb2312");
		// ����������
		DOMSource domSource = new DOMSource(document);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transFormer.transform(domSource, result);
		return writer.toString();
	}

	/**
	 * �շ�xml����
	 * 
	 * @param inputStr
	 * @return
	 * @throws Exception
	 */
	public TParm getFeeParserXml(String inputStr) throws Exception {
		TParm result = new TParm();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream io = new ByteArrayInputStream(inputStr.getBytes("UTF-8"));
		Document document = builder.parse(io);
		Element rootElement = document.getDocumentElement();
		NodeList RcNode = rootElement.getChildNodes();
		for (int i = 0; i < RcNode.getLength(); i++) {
			if (RcNode.item(i).getNodeType() == Node.ELEMENT_NODE) {
				if (RcNode.item(i).getNodeName().equals("ApplyNo")) {
					result.setData("ApplyNo", RcNode.item(i).getTextContent());
				}
				if (RcNode.item(i).getNodeName().equals("UserId")) {
					result.setData("UserId", "BMSWS");// �����û�ID BMSWS
				}
				if (RcNode.item(i).getNodeName().equals("UserName")) {
					result.setData("UserName", "BMSWS");// �����û�NAME BMSWS
				}
				if (RcNode.item(i).getNodeName().equals("MrNo")) {
					result.setData("MrNo", RcNode.item(i).getTextContent());
				}
				if (RcNode.item(i).getNodeName().equals("PatName")) {
					result.setData("PatName", RcNode.item(i).getTextContent());
				}
			}
		}
		TParm orderParm = new TParm();
		NodeList nodelist = rootElement.getElementsByTagName("BilFee");
		for (int i = 0; i < nodelist.getLength(); i++) {
			Element nodeElement = (Element) nodelist.item(i);
			NodeList childNodes = nodeElement.getChildNodes();
			for (int j = 0; j < childNodes.getLength(); j++) {
				if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
					if (childNodes.item(j).getNodeName().equals("OrderCode")) {
						orderParm.addData("OrderCode", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("OrderQty")) {
						orderParm.addData("OrderQty", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("Note")) {
						orderParm.addData("Note", childNodes.item(j)
								.getTextContent());
					}
				}
			}
		}
		orderParm.setCount(orderParm.getCount("OrderCode"));
		result.setData("orderParm", orderParm.getData());
		return result;
	}

	/**
	 * ѪƷxml����
	 * 
	 * @param inputStr
	 * @return
	 * @throws Exception
	 */
	public TParm getBloodParserXml(String inputStr) throws Exception {
		TParm result = new TParm();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream io = new ByteArrayInputStream(inputStr.getBytes("UTF-8"));
		Document document = builder.parse(io);
		Element rootElement = document.getDocumentElement();
		NodeList RcNode = rootElement.getChildNodes();
		for (int i = 0; i < RcNode.getLength(); i++) {
			if (RcNode.item(i).getNodeType() == Node.ELEMENT_NODE) {
				if (RcNode.item(i).getNodeName().equals("OutNo")) {
					result.setData("OutNo", RcNode.item(i).getTextContent());
				}
				if (RcNode.item(i).getNodeName().equals("OutDate")) {
					result.setData("OutDate", RcNode.item(i).getTextContent());
				}
				if (RcNode.item(i).getNodeName().equals("UserId")) {
					result.setData("UserId", "BMSWS");// �����û�ID BMSWS
				}
				if (RcNode.item(i).getNodeName().equals("UserName")) {
					result.setData("UserName", "BMSWS");// �����û�NAME BMSWS
				}
				if (RcNode.item(i).getNodeName().equals("ApplyNo")) {
					result.setData("ApplyNo", RcNode.item(i).getTextContent());
				}
				if (RcNode.item(i).getNodeName().equals("MrNo")) {
					result.setData("MrNo", RcNode.item(i).getTextContent());
				}
				if (RcNode.item(i).getNodeName().equals("PatName")) {
					result.setData("PatName", RcNode.item(i).getTextContent());
				}
				if (RcNode.item(i).getNodeName().equals("PBloodType")) {
					result.setData("PBloodType", RcNode.item(i)
							.getTextContent());
				}
				if (RcNode.item(i).getNodeName().equals("PRhType")) {
					result.setData("PRhType", RcNode.item(i).getTextContent());
				}
			}
		}
		TParm orderParm = new TParm();
		NodeList nodelist = rootElement.getElementsByTagName("Blood");
		for (int i = 0; i < nodelist.getLength(); i++) {
			Element nodeElement = (Element) nodelist.item(i);
			NodeList childNodes = nodeElement.getChildNodes();
			for (int j = 0; j < childNodes.getLength(); j++) {
				if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
					if (childNodes.item(j).getNodeName().equals("BloodNo")) {
						orderParm.addData("BloodNo", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("BloodCode")) {
						orderParm.addData("BloodCode", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("BloodSpe")) {
						orderParm.addData("BloodSpe", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("BloodQty")) {
						orderParm.addData("BloodQty", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("BloodUnit")) {
						orderParm.addData("BloodUnit", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("BloodType")) {
						orderParm.addData("BloodType", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("RhType")) {
						orderParm.addData("RhType", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("Note")) {
						orderParm.addData("Note", childNodes.item(j)
								.getTextContent());
					}
				}
			}
		}
		orderParm.setCount(orderParm.getCount("BloodNo"));
		result.setData("bloodParm", orderParm.getData());
		return result;
	}

	/**
	 * Ѫ�ͼ���xml����
	 * 
	 * @param inputStr
	 * @return
	 * @throws Exception
	 */
	public TParm getConfirmParserXml(String inputStr) throws Exception {
		TParm result = new TParm();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream io = new ByteArrayInputStream(inputStr.getBytes("UTF-8"));
		Document document = builder.parse(io);
		Element rootElement = document.getDocumentElement();
		NodeList RcNode = rootElement.getChildNodes();
		for (int i = 0; i < RcNode.getLength(); i++) {
			if (RcNode.item(i).getNodeType() == Node.ELEMENT_NODE) {
				if (RcNode.item(i).getNodeName().equals("MrNo")) {
					result.setData("MrNo", RcNode.item(i).getTextContent());
				}
				if (RcNode.item(i).getNodeName().equals("PatName")) {
					result.setData("PatName", RcNode.item(i).getTextContent());
				}
				if (RcNode.item(i).getNodeName().equals("PBloodType")) {
					result.setData("PBloodType", RcNode.item(i)
							.getTextContent());
				}
				if (RcNode.item(i).getNodeName().equals("PRhType")) {
					result.setData("PRhType", RcNode.item(i).getTextContent());
				}
			}
		}
		return result;
	}

	/**
	 * ѪƷ����xml����
	 * 
	 * @param inputStr
	 * @return
	 * @throws Exception
	 */
	public TParm getRefundParserXml(String inputStr) throws Exception {
		TParm result = new TParm();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream io = new ByteArrayInputStream(inputStr.getBytes("UTF-8"));
		Document document = builder.parse(io);
		Element rootElement = document.getDocumentElement();
		TParm orderParm = new TParm();
		NodeList nodelist = rootElement.getElementsByTagName("Blood");
		for (int i = 0; i < nodelist.getLength(); i++) {
			Element nodeElement = (Element) nodelist.item(i);
			NodeList childNodes = nodeElement.getChildNodes();
			for (int j = 0; j < childNodes.getLength(); j++) {
				if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
					if (childNodes.item(j).getNodeName().equals("BloodNo")) {
						orderParm.addData("BloodNo", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("BloodCode")) {
						orderParm.addData("BloodCode", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("BloodSpe")) {
						orderParm.addData("BloodSpe", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("BloodQty")) {
						orderParm.addData("BloodQty", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("BloodUnit")) {
						orderParm.addData("BloodUnit", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("BloodType")) {
						orderParm.addData("BloodType", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("RhType")) {
						orderParm.addData("RhType", childNodes.item(j)
								.getTextContent());
					}
					if (childNodes.item(j).getNodeName().equals("Note")) {
						orderParm.addData("Note", childNodes.item(j)
								.getTextContent());
					}
				}
			}
		}
		orderParm.setCount(orderParm.getCount("BloodNo"));
		result.setData("bloodParm", orderParm.getData());
		return result;
	}
	public static void main(String[] args) {
		XMLUtil xu = new XMLUtil();
		String line = null;
		try {
			line = xu.getApplyXMLString(new TParm(),"");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("============" + line);
		// String
		// lie="<?xml version=\"1.0\" encoding=\"utf-8\" ?> <Root>   <ApplyNo>120400000098</ApplyNo>  "
		// +
		// " <UserId>D001</UserId>   <UserName>����</UserName>  " +
		// " <MrNo>000000370728</MrNo>   <PatName>С��</PatName>   " +
		// "<BilFees>     <BilFee>       <OrderCode>S10A1832</OrderCode>  " +
		// "     <OrderQty>2.0</OrderQty>       <Note>��ע</Note>  " +
		// "   </BilFee>     <BilFee>       <OrderCode>S10A1835</OrderCode>   "
		// +
		// "    <OrderQty>1.0</OrderQty>       <Note>��ע</Note>     </BilFee>  "
		// +
		// "   </BilFees> </Root>";
		String lie = "<?xml version=\"1.0\" encoding=\"utf-8\" ?> <Root>   <OutNo>131125000001</OutNo> "
				+ "  <OutDate>201403132035</OutDate>   <UserId>D001</UserId>   <UserName>����</UserName> "
				+ "  <ApplyNo>120400000098</ApplyNo>   <MrNo>000000370728</MrNo>  "
				+ " <PatName>С��</PatName>   <BloodList>     <Blood> 	  <BloodNo>1403122222</BloodNo> "
				+ "	  <BloodCode>21</BloodCode> 	  <BloodSpe>1301</BloodSpe>     "
				+ "  <BloodQty>2.0</BloodQty> 	  <BloodUnit>18</BloodUnit>      "
				+ " <Note>��ע</Note>     </Blood>     <Blood> 	  <BloodNo>1403121420</BloodNo>   "
				+ "    <BloodCode>12</BloodCode> 	  <BloodSpe>1302</BloodSpe>      "
				+ " <BloodQty>2.0</BloodQty> 	  <BloodUnit>18</BloodUnit>    "
				+ "   <Note/>    </Blood>     </BloodList> </Root>";
		try {
			// TParm parm=xu.getFeeParserXml(lie);
			TParm parm = xu.getBloodParserXml(lie);
			System.out.println("========" + parm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
