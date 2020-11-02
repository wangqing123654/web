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
	 * 申请单xml
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getApplyXMLString(TParm parm,String admType) throws Exception {
		// 解析器工厂类
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// 解析器
		DocumentBuilder builder = factory.newDocumentBuilder();
		// 操作的Document对象
		Document document = builder.newDocument();
		// 设置XML的版本
		document.setXmlVersion("1.0");
		// 创建根节点
		Element root = document.createElement("OrderList");
		// 将根节点添加到Document对象中
		document.appendChild(root);
		// 设置第一个OrderForm元素到
		Element OrderForm = document.createElement("OrderForm");
		// 申请号
		Element BOrderFormID = document.createElement("BOrderFormID");
		BOrderFormID.setTextContent(parm.getValue("APPLY_NO"));
		OrderForm.appendChild(BOrderFormID);
		// 申请日期
		Element OrderTime = document.createElement("OrderTime");
		OrderTime.setTextContent(StringTool.getString(parm
				.getTimestamp("PRE_DATE"), "yyyy-MM-dd HH:mm:ss"));
		OrderForm.appendChild(OrderTime);
		// 病案号
		Element PatID = document.createElement("PatID");
		PatID.setTextContent(parm.getValue("MR_NO"));
		OrderForm.appendChild(PatID);
		// 病案编码
		Element PatNo = document.createElement("PatNo");
		PatNo.setTextContent(parm.getValue("CASE_NO"));
		OrderForm.appendChild(PatNo);
		// 患者名称
		Element Cname = document.createElement("Cname");
		Cname.setTextContent(parm.getValue("PAT_NAME"));
		OrderForm.appendChild(Cname);
		// 患者性别
		Element Sex = document.createElement("Sex");
		Sex.setTextContent(parm.getValue("SEX"));
		OrderForm.appendChild(Sex);
		// 患者年龄
		String[] res;
		res = StringTool.CountAgeByTimestamp(parm.getTimestamp("BIRTH_DATE"),
				StringTool.getTimestamp(StringTool.getString(parm
						.getTimestamp("IN_DATE"), "yyyyMMdd"), "yyyyMMdd"));
		int age = 0;// 单位是天
		String ageunit = "";
		if (TypeTool.getInt(res[0]) < 1) {
			age = StringTool.getDateDiffer(StringTool.getTimestamp(StringTool
					.getString(parm.getTimestamp("IN_DATE"), "yyyyMMdd"),
					"yyyyMMdd"), parm.getTimestamp("BIRTH_DATE"));
			age=age+1;
			ageunit = "天";
		} else if (TypeTool.getInt(res[0]) >= 1) {
			age = TypeTool.getInt(res[0].equals("") ? 0 : res[0]);// 11 年龄
			ageunit = "岁";
		}
		Element Age = document.createElement("Age");
		Age.setTextContent(age + "");
		OrderForm.appendChild(Age);
		// 患者年龄单位
		Element AgeUnit = document.createElement("AgeUnit");
		AgeUnit.setTextContent(ageunit);
		OrderForm.appendChild(AgeUnit);
		// 患者出生日期
		Element Birthday = document.createElement("Birthday");
		Birthday.setTextContent(StringTool.getString(parm
				.getTimestamp("BIRTH_DATE"), "yyyy-MM-dd HH:mm:ss"));
		OrderForm.appendChild(Birthday);
		// 科室
		Element DeptID = document.createElement("DeptID");
		DeptID.setTextContent(parm.getValue("DEPT_CODE"));
		OrderForm.appendChild(DeptID);
		
		Element DeptName = document.createElement("DeptName");
		DeptName.setTextContent(parm.getValue("DEPT_DESC"));
		OrderForm.appendChild(DeptName);
		
		// 床位
		Element bed = document.createElement("Bed");
		bed.setTextContent(parm.getValue("BED_DESC"));
		OrderForm.appendChild(bed);
		
		// 医生
		Element DoctorID = document.createElement("DoctorID");
		DoctorID.setTextContent(parm.getValue("DR_CODE"));
		OrderForm.appendChild(DoctorID);
		
		Element DoctorName = document.createElement("DoctorName");
		DoctorName.setTextContent(parm.getValue("DR_DESC"));
		OrderForm.appendChild(DoctorName);
		
		// 诊断
		Element Diag = document.createElement("Diag");
		Diag.setTextContent(parm.getValue("DIAG_DESC1"));
		OrderForm.appendChild(Diag);
		
		// 既往输血史
		Element BeforUse = document.createElement("BeforUse");
		BeforUse.setTextContent("");
		OrderForm.appendChild(BeforUse);
		// 妊娠史
		Element Gravida = document.createElement("Gravida");
		Gravida.setTextContent("");
		OrderForm.appendChild(Gravida);
		// 输血不良反应史
		Element Harm = document.createElement("Harm");
		Harm.setTextContent("");
		OrderForm.appendChild(Harm);
		// 互助献血史
		Element Help = document.createElement("Help");
		Help.setTextContent("");
		OrderForm.appendChild(Help);
		// 用药史
		Element Drag = document.createElement("Drag");
		Drag.setTextContent("");
		OrderForm.appendChild(Drag);
		// 患者属地
		Element AddressType = document.createElement("AddressType");
		AddressType.setTextContent("");
		OrderForm.appendChild(AddressType);
		// 受血者状态ID
		Element BPatStatusID = document.createElement("BPatStatusID");
		BPatStatusID.setTextContent("");
		OrderForm.appendChild(BPatStatusID);
		// 使用时间
		Element UseTime = document.createElement("UseTime");
		UseTime.setTextContent(StringTool.getString(parm
				.getTimestamp("USE_DATE"), "yyyy-MM-dd HH:mm:ss"));
		OrderForm.appendChild(UseTime);
		// 用血方式
		Element UseTypeID = document.createElement("UseTypeID");
		UseTypeID.setTextContent("");
		OrderForm.appendChild(UseTypeID);
		// 用血时间类型
		Element BUseTimeTypeID = document.createElement("BUseTimeTypeID");
		BUseTimeTypeID.setTextContent(admType.equals("E")?"1":"");
		OrderForm.appendChild(BUseTimeTypeID);
		// 用血目的
		Element UsePurpose = document.createElement("UsePurpose");
		UsePurpose.setTextContent("");
		OrderForm.appendChild(UsePurpose);
		// 预留
		Element BABORHID = document.createElement("BABORHID");
		BABORHID.setTextContent("");
		OrderForm.appendChild(BABORHID);
		// 血型
		Element BPatABO = document.createElement("BPatABO");
		BPatABO.setTextContent(parm.getValue("TEST_BLD"));
		OrderForm.appendChild(BPatABO);
		// RH
		String rh = "";
		if (parm.getValue("BLOOD_RH_TYPE").equals("+")) {
			rh = "阳性";
		} else if (parm.getValue("BLOOD_RH_TYPE").equals("-")) {
			rh = "阴性";
		}
		Element BPatRH = document.createElement("BPatRH");
		BPatRH.setTextContent(rh);
		OrderForm.appendChild(BPatRH);
		// 备注
		Element Memo = document.createElement("Memo");
		Memo.setTextContent(parm.getValue("REMARK"));
		OrderForm.appendChild(Memo);
		// 血品列表
		Element OrderItems = document.createElement("OrderItems");
		TParm bloodParm = parm.getParm("DATA");
		for (int i = 0; i < bloodParm.getCount(); i++) {
			TParm bloodrow = bloodParm.getRow(i);
			// 血品列表
			Element OrderItem = document.createElement("OrderItem");
			// 申请明细ID
			Element BOrderItemID = document.createElement("BOrderItemID");
			BOrderItemID.setTextContent(bloodrow.getValue("APPLY_NO")
					+ bloodrow.getValue("BLD_CODE"));
			OrderItem.appendChild(BOrderItemID);
			// 申请单号
			Element BBOrderItemID = document.createElement("BOrderItemID");
			BBOrderItemID.setTextContent(bloodrow.getValue("APPLY_NO"));
			OrderItem.appendChild(BBOrderItemID);
			// 血品编码
			Element BloodID = document.createElement("BloodID");
			BloodID.setTextContent(bloodrow.getValue("BLD_CODE"));
			OrderItem.appendChild(BloodID);
			// 血品名称
			Element BloodName = document.createElement("BloodName");
			BloodName.setTextContent(bloodrow.getValue("BLDCODE_DESC"));
			OrderItem.appendChild(BloodName);
			// 血品单位id
			Element BUnitID = document.createElement("BUnitID");
			BUnitID.setTextContent(bloodrow.getValue("UNIT_CODE"));
			OrderItem.appendChild(BUnitID);
			// 血品单位名称
			Element Bunit = document.createElement("Bunit");
			Bunit.setTextContent(bloodrow.getValue("UNIT_CHN_DESC"));
			OrderItem.appendChild(Bunit);
			// 血品数量
			Element BOrderCount = document.createElement("BOrderCount");
			BOrderCount.setTextContent(bloodrow.getValue("APPLY_QTY"));
			OrderItem.appendChild(BOrderCount);
			// 血品血型
			Element BApplyBld = document.createElement("BApplyBld");
			BApplyBld.setTextContent(bloodrow.getValue("APPLY_BLD"));
			OrderItem.appendChild(BApplyBld);
			// 血品RH
			Element BApplyRH = document.createElement("BApplyRH");
			BApplyRH.setTextContent(bloodrow.getValue("APPLY_RH_TYPE"));
			OrderItem.appendChild(BApplyRH);
			// 备注
			Element MemoD = document.createElement("Memo");
			MemoD.setTextContent("");
			OrderItem.appendChild(MemoD);
			OrderItems.appendChild(OrderItem);
		}
		OrderForm.appendChild(OrderItems);
		// 将OrderForm段加根节点内
		root.appendChild(OrderForm);
		// 开始把Document映射到文件
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transFormer = transFactory.newTransformer();
		transFormer.setOutputProperty("encoding", "gb2312");
		// 设置输出结果
		DOMSource domSource = new DOMSource(document);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transFormer.transform(domSource, result);
		return writer.toString();
	}

	/**
	 * 输血反应xml
	 * 
	 * @param parm
	 * @return
	 * @throws Exception
	 */
	public String getReactXmlString(TParm parm) throws Exception {
		// 解析器工厂类
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// 解析器
		DocumentBuilder builder = factory.newDocumentBuilder();
		// 操作的Document对象
		Document document = builder.newDocument();
		// 设置XML的版本
		document.setXmlVersion("1.0");
		// 创建根节点
		Element root = document.createElement("HarmList");
		// 将根节点添加到Document对象中
		document.appendChild(root);
		// 设置第一个HarmForm元素到
		Element HarmForm = document.createElement("HarmForm");
		// 反应号
		Element BHarmFormID = document.createElement("BHarmFormID");
		BHarmFormID.setTextContent(parm.getValue("REACT_NO"));
		HarmForm.appendChild(BHarmFormID);
		// 科室
		Element deptID = document.createElement("deptID");
		deptID.setTextContent(parm.getValue("DEPT_CODE"));
		HarmForm.appendChild(deptID);
		// 病区
		Element wardID = document.createElement("wardID");
		wardID.setTextContent(parm.getValue("STATION_CODE"));
		HarmForm.appendChild(wardID);
		// 病案号
		Element patID = document.createElement("patID");
		patID.setTextContent(parm.getValue("MR_NO"));
		HarmForm.appendChild(patID);
		// 就诊号
		Element patNo = document.createElement("patNo");
		patNo.setTextContent(parm.getValue("CASE_NO"));
		HarmForm.appendChild(patNo);
		// 住院号
		Element AdmissID = document.createElement("AdmissID");
		AdmissID.setTextContent(parm.getValue("IPD_NO"));
		HarmForm.appendChild(AdmissID);
		// 姓名
		Element PatName = document.createElement("PatName");
		PatName.setTextContent(parm.getValue("PAT_NAME"));
		HarmForm.appendChild(PatName);
		// 性别
		Element sex = document.createElement("sex");
		sex.setTextContent(parm.getValue("SEX"));
		HarmForm.appendChild(sex);
		// 患者年龄
		String[] res;
		res = StringTool.CountAgeByTimestamp(parm.getTimestamp("BIRTH_DATE"),
				StringTool.getTimestamp(StringTool.getString(parm
						.getTimestamp("IN_DATE"), "yyyyMMdd"), "yyyyMMdd"));
		int age = 0;// 单位是天
		String ageunit = "";
		if (TypeTool.getInt(res[0]) < 1) {
			age = StringTool.getDateDiffer(StringTool.getTimestamp(StringTool
					.getString(parm.getTimestamp("IN_DATE"), "yyyyMMdd"),
					"yyyyMMdd"), parm.getTimestamp("BIRTH_DATE"));
			ageunit = "天";
		} else if (TypeTool.getInt(res[0]) >= 1) {
			age = TypeTool.getInt(res[0].equals("") ? 0 : res[0]);// 11 年龄
			ageunit = "岁";
		}
		Element Age = document.createElement("Age");
		Age.setTextContent(age + "");
		HarmForm.appendChild(Age);
		// 患者年龄单位
		Element AgeUnit = document.createElement("AgeUnit");
		AgeUnit.setTextContent(ageunit);
		HarmForm.appendChild(AgeUnit);
		// 身份证
		Element IDCard = document.createElement("IDCard");
		IDCard.setTextContent(parm.getValue("IDNO"));
		HarmForm.appendChild(IDCard);
		// 血型
		Element ABO = document.createElement("ABO");
		ABO.setTextContent(parm.getValue("BLOOD_TYPE"));
		HarmForm.appendChild(ABO);
		// RH
		Element RHD = document.createElement("RHD");
        String rh = "";
        if (parm.getValue("BLOOD_RH_TYPE").equals("+")) {
            rh = "阳性";
        } else if (parm.getValue("BLOOD_RH_TYPE").equals("-")) {
            rh = "阴性";
        }
        RHD.setTextContent(rh);// wanglong modify 20140811
		HarmForm.appendChild(RHD);
		// 检验血型
		Element TestABO = document.createElement("TestABO");
		TestABO.setTextContent(parm.getValue("BLOOD_TYPE"));
		HarmForm.appendChild(TestABO);
		// 输血时间
		Element UseStartTime = document.createElement("UseStartTime");
		String stime = StringTool.getString(parm.getTimestamp("START_DATE"),
				"yyyy-MM-dd HH:mm:ss");
		UseStartTime.setTextContent(stime);
		HarmForm.appendChild(UseStartTime);
		// 完成时间
		Element UseEndTime = document.createElement("UseEndTime");
		String etime = StringTool.getString(parm.getTimestamp("END_DATE"),
				"yyyy-MM-dd HH:mm:ss");
		UseEndTime.setTextContent(etime);
		HarmForm.appendChild(UseEndTime);
		// 有反应的血制品
		Element HarmBlood = document.createElement("HarmBlood");
		HarmBlood.setTextContent(parm.getValue("BLD_CODE"));
		HarmForm.appendChild(HarmBlood);
		// 反应等级
		Element HarmGrade = document.createElement("HarmGrade");
		HarmGrade.setTextContent(parm.getValue("REACT_CLASS"));
		HarmForm.appendChild(HarmGrade);
		// 输血反应
		Element HarmName = document.createElement("HarmName");
		HarmName.setTextContent(parm.getValue("REACTION_CODE"));
		HarmForm.appendChild(HarmName);
		// 其它反应
		Element OtherHarm = document.createElement("OtherHarm");
		OtherHarm.setTextContent(parm.getValue("REACT_OTH"));
		HarmForm.appendChild(OtherHarm);
		// 过敏反应史
		Element allergy = document.createElement("allergy");
		allergy.setTextContent(parm.getValue("REACT_HIS"));
		HarmForm.appendChild(allergy);
		// 反应症状
		Element HarmSym = document.createElement("HarmSym");
		HarmSym.setTextContent(parm.getValue("RECAT_SYMPTOM"));
		HarmForm.appendChild(HarmSym);
		// 治疗
		Element therapy = document.createElement("therapy");
		therapy.setTextContent(parm.getValue("TREAT"));
		HarmForm.appendChild(therapy);
		// 制表人
		Element MakerID = document.createElement("MakerID");
		MakerID.setTextContent(parm.getValue("OPT_USER"));
		HarmForm.appendChild(MakerID);
		// 将HarmForm段加根节点内
		root.appendChild(HarmForm);
		// 开始把Document映射到文件
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transFormer = transFactory.newTransformer();
		transFormer.setOutputProperty("encoding", "gb2312");
		// 设置输出结果
		DOMSource domSource = new DOMSource(document);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transFormer.transform(domSource, result);
		return writer.toString();
	}

	/**
	 * 收费xml解析
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
					result.setData("UserId", "BMSWS");// 暂设用户ID BMSWS
				}
				if (RcNode.item(i).getNodeName().equals("UserName")) {
					result.setData("UserName", "BMSWS");// 暂设用户NAME BMSWS
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
	 * 血品xml解析
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
					result.setData("UserId", "BMSWS");// 暂设用户ID BMSWS
				}
				if (RcNode.item(i).getNodeName().equals("UserName")) {
					result.setData("UserName", "BMSWS");// 暂设用户NAME BMSWS
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
	 * 血型鉴定xml解析
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
	 * 血品回退xml解析
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
		// " <UserId>D001</UserId>   <UserName>张三</UserName>  " +
		// " <MrNo>000000370728</MrNo>   <PatName>小敏</PatName>   " +
		// "<BilFees>     <BilFee>       <OrderCode>S10A1832</OrderCode>  " +
		// "     <OrderQty>2.0</OrderQty>       <Note>备注</Note>  " +
		// "   </BilFee>     <BilFee>       <OrderCode>S10A1835</OrderCode>   "
		// +
		// "    <OrderQty>1.0</OrderQty>       <Note>备注</Note>     </BilFee>  "
		// +
		// "   </BilFees> </Root>";
		String lie = "<?xml version=\"1.0\" encoding=\"utf-8\" ?> <Root>   <OutNo>131125000001</OutNo> "
				+ "  <OutDate>201403132035</OutDate>   <UserId>D001</UserId>   <UserName>张三</UserName> "
				+ "  <ApplyNo>120400000098</ApplyNo>   <MrNo>000000370728</MrNo>  "
				+ " <PatName>小敏</PatName>   <BloodList>     <Blood> 	  <BloodNo>1403122222</BloodNo> "
				+ "	  <BloodCode>21</BloodCode> 	  <BloodSpe>1301</BloodSpe>     "
				+ "  <BloodQty>2.0</BloodQty> 	  <BloodUnit>18</BloodUnit>      "
				+ " <Note>备注</Note>     </Blood>     <Blood> 	  <BloodNo>1403121420</BloodNo>   "
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
