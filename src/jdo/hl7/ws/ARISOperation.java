package jdo.hl7.ws;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import jdo.hl7.Hl7Tool;
import jdo.hl7.PDFJdo;
import jdo.hl7.RISJdo;
import jdo.hl7.pojo.MSA;
import jdo.hl7.pojo.MSH;
import jdo.hl7.pojo.Status;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.javahis.exception.HL7Exception;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: RIS接口方法
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
 * @author WangM
 * @version 1.0
 */
public class ARISOperation implements IRISOperation {
	// HL7文件内容
	private String bytFileData;
	// BPEL设置参数对象
	private TParm parm;
	// 返回编码
	private String returnCode;
	// 结果TParm
	private TParm reParm;
	// 返回数据
	private String bytReturnData;
	// 时间
	public static DateFormat dateFormat = new SimpleDateFormat(
			"yyyyMMddhhmmssSSS");
	// 检体核收
	private Vector vct;

	/**
	 * 拿到RIS回发文件全部内容
	 * 
	 * @param data
	 *            byte[]
	 * @param parm
	 *            TConfigParm
	 */
	public void mainLisData(String data, TParm parm) throws HL7Exception {
		this.setBytFileData(data);
		this.setParm(parm);
		this.ControlLISProsess();

	}

	/**
	 * 预约
	 * 
	 * @return boolean
	 */
	public TParm risYY() throws HL7Exception {
		TParm reParm = new TParm();
		// 条码号
		Vector vctApplyNo = (Vector) this.parm.getData("RISAPPNO");
		// RIS检查号
		String risNo = this.parm.getValue("RISNO", 0);
		// 预约时间
		String yyDate = this.parm.getValue("YYDATE", 0);
		this.vct = new Vector();
		int rowCount = vctApplyNo.size();
		for (int i = 0; i < rowCount; i++) {
			MSA msa = new MSA();
			if (StringUtil.getInstance().isNullString(
					vctApplyNo.get(i).toString())) {
				// // System.out.println("条码号为空！！");
				reParm.setErr(-1, "条码号为空！！");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			TParm statusParm = Hl7Tool.getInstance().getOrder(
					vctApplyNo.get(i).toString(), "RIS","");
			if (statusParm.getCount() <= 0) {
				reParm.setErr(-1, "没有找到对应医嘱！！");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			int status = statusParm.getInt("STATUS", 0);
			if (status >= 4) {
				reParm.setErr(-1, "医嘱：" + Status.getStatusMap().get(status)
						+ ",不可预约");
			}
			if (StringUtil.getInstance().isNullString(yyDate)) {
				// // System.out.println("预约时间为空！！");
				reParm.setErr(-1, "预约时间为空！！");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			reParm = RISJdo.getInstance().upDateRisYY(
					vctApplyNo.get(i).toString(), "2", yyDate);
			if (reParm.getErrCode() < 0) {
				reParm.setErr(-1, "预约更新失败");
				return reParm;
			}
			try {
				msa.setMsh(new MSH());
			} catch (HL7Exception ex) {
				ex.printStackTrace();
			}
			if (reParm.getErrCode() < 0) {
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			} else {
				msa.setAcknowledgmentCode("CA");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
			}
			this.vct.add(msa);
		}
		return reParm;
	}

	/**
	 * 到检
	 * 
	 * @return boolean
	 */
	public TParm risDJ() throws HL7Exception {
		TParm reParm = new TParm();
		// 条码号
		Vector vctApplyNo = (Vector) this.parm.getData("LABNO");
		String RUType = this.parm.getValue("INMEUF", 0);
		// 类型
		String typeDJ = this.parm.getValue("LISSTR", 0);
//		// 预约时间(超声到检取预约时间)
		String yyDate = this.parm.getValue("YYDATE", 0);
		// 到检时间
		String djDate = this.parm.getValue("DJDATE", 0);
//		// (超声到检取预约时间)
		if (RUType.equals("ULT") && !yyDate.equals("")) {
			djDate = yyDate;
		}
		// 门急住别 20120229 shibl modify
		String admType = this.parm.getValue("OEIH", 0);
		// ORDERNO SEQNO
		String orderNoseqNo = this.parm.getValue("ORDERNOSEQ", 0);
		// 检查人员
		String jcRisUser = this.parm.getValue("JCRISUSER", 0);
		// 设备ID
		String devCode = "";
		String devDesc = "";
		// 状态
		String status = "2";
		this.vct = new Vector();
		int rowCount = vctApplyNo.size();
		// 到检
		if ("IP".equals(typeDJ)) {
			status = "4";
		}
		// 取消到检
		if ("CA".equals(typeDJ)) {
			status = "5";
			jcRisUser = "";
		}
		// 检查完成
		if ("CM".equals(typeDJ)) {
			String[] dev = StringTool.parseLine(this.parm
					.getValue("EXECDEV", 0), "^");
			try {
				// 设备ID
				if (dev.length > 0) {
					devCode = dev[0];
				}
				// 设备名称
				if (dev.length > 1) {
					devDesc = dev[1];
				}
			} catch (Exception ex) {

			}
			status = "6";
		}
		for (int i = 0; i < rowCount; i++) {
			MSA msa = new MSA();
			if ("CM".equals(typeDJ)) {
				if (StringUtil.getInstance().isNullString(
						vctApplyNo.get(i).toString())) {
					// System.out.println("条码为空！！！");
					reParm.setErr(-1, "条码为空！！！");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctApplyNo.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				if (StringUtil.getInstance().isNullString(jcRisUser)) {
					// System.out.println("检查人员为空！！！");
					jcRisUser = "未知";
				}
				reParm = RISJdo.getInstance().upDateRisWCJC(
						vctApplyNo.get(i).toString(), status, djDate,
						jcRisUser, devCode, devDesc);
				if (reParm.getErrCode() < 0) {
					reParm.setErr(-1, "到检完成失败");
					return reParm;
				}
			} else {
				if (StringUtil.getInstance().isNullString(
						vctApplyNo.get(i).toString())) {
					// System.out.println("条码为空！！！");
					reParm.setErr(-1, "条码为空！！！");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctApplyNo.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				if (StringUtil.getInstance().isNullString(admType)) {
					// System.out.println("门急别为空！！！");
					reParm.setErr(-1, "门急别为空！！！");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctApplyNo.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				reParm = RISJdo.getInstance().upDateRisDJ(
						vctApplyNo.get(i).toString(), status, djDate, admType,
						jcRisUser);

			}
			try {
				msa.setMsh(new MSH());
			} catch (HL7Exception ex) {
				ex.printStackTrace();
			}
			if (reParm.getErrCode() < 0) {
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			} else {
				msa.setAcknowledgmentCode("CA");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
			}
			this.vct.add(msa);
		}
		return reParm;
	}

	/**
	 * 取消预约
	 * 
	 * @return boolean
	 */
	public TParm risCanYY()throws HL7Exception {
		TParm falg = new TParm();
		// 条码号
		Vector vctApplyNo = (Vector) this.parm.getData("RISAPPNO");
		// RIS检查号
		String risNo = this.parm.getValue("RISNO", 0);
		// 预约时间
		String yyDate = this.parm.getValue("YYDATE", 0);
		this.vct = new Vector();
		int rowCount = vctApplyNo.size();
		for (int i = 0; i < rowCount; i++) {
			MSA msa = new MSA();
			if (StringUtil.getInstance().isNullString(
					vctApplyNo.get(i).toString())) {
				// System.out.println("条码号为空！！");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			TParm statusParm = Hl7Tool.getInstance().getOrder(
					vctApplyNo.get(i).toString(), "RIS","");
			if (statusParm.getCount() <= 0) {
				reParm.setErr(-1, "没有找到对应医嘱！！");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			int status = statusParm.getInt("STATUS", 0);
			if (status != Status.YYS) {
				reParm.setErr(-1, "医嘱：" + Status.getStatusMap().get(status)
						+ ",不可取消预约");
			}
			falg = RISJdo.getInstance().upDateRisYY(
					vctApplyNo.get(i).toString(), "3", yyDate);
			if (falg.getErrCode() < 0) {
				falg.setErr(-1, "取消预约更新失败");
				return falg;
			}
			try {
				msa.setMsh(new MSH());
			} catch (HL7Exception ex) {
				ex.printStackTrace();
			}
			if (falg.getErrCode() < 0) {
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			} else {
				msa.setAcknowledgmentCode("CA");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
			}
			this.vct.add(msa);
		}
		return falg;
	}

	/**
	 * 审核
	 * 
	 * @return boolean
	 */
	public TParm risSHWC() {
		return new TParm();
	}

	/**
	 * 报告
	 * 
	 * @return boolean
	 */
	public TParm risBGWC() throws HL7Exception{
		TParm falg = new TParm();
		// 门急住别
		String admType = this.parm.getValue("OEIH", 0);
		// 厂商
		String inMuf = this.parm.getValue("INMEUF", 0);
		// 条码号
		Vector vctApplyNo = (Vector) this.parm.getData("ORDERNO");
		// 类型
		String typeDJ = this.parm.getValue("STUTSBG", 0);
		// 报告时间
		String timeDate[] = StringTool.parseLine(this.parm.getValue(
				"RISBGDATE", 0), "~");
		// 报告医师
		String temp[] = StringTool.parseLine(this.parm.getValue("SHBGUSER", 0),
				"~");
		// 仪器代码(检查号)
		String chNo = "";
		String devArray[] = StringTool.parseLine(this.parm.getValue("DEVCODE",
				0), "^");
		String bgDr = "";
		String djDate = "";
		try {
			if (2 <= devArray.length) {
				chNo = devArray[1];
			}
			if (0 < temp.length) {
				String dr[]=StringTool.parseLine(temp[0], "^");
				bgDr = (dr.length)>1?dr[1]:"";
			}
			if (0 < timeDate.length) {
				djDate = timeDate[0];
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// 审核医师
		String shDr = "";
		String shDate = "";
		if (temp.length >= 2) {
			String dr[]=StringTool.parseLine(temp[1], "^");
			shDr = (dr.length)>1?dr[1]:"";
		}
		if (timeDate.length >= 2) {
			shDate = timeDate[1];
		}
		// 报告页号
		List<String> bgSeq = this.getTXBGNo();
		// 图像地址
		List<String> imageUrl = getTXimageUrl();
		// pdf报告路径
		List<String> pdfPath = this.getPdfPath();
		// 诊断印象
		List<String> zdyx = getCEData();
		// 诊断所见
		List<String> zdsj = getTXData();
		// 阴阳性
		List<String> type = getYYType();
		// 状态
		String status = "6";
		this.vct = new Vector();
		int rowCount = vctApplyNo.size();
		// 报告完成
		if ("F".equals(typeDJ)) {
			status = "7";
		}
		// 取消报告
		if ("D".equals(typeDJ)) {
			status = "10";
		}
		for (int i = 0; i < rowCount; i++) {
			MSA msa = new MSA();
			if (StringUtil.getInstance().isNullString(
					vctApplyNo.get(i).toString())) {
				System.out.println("条码为空！！！");
				falg.setErr(-1, "条码为空！！！");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			if (StringUtil.getInstance().isNullString(typeDJ)) {
				System.out.println("报告状态为空！！！");
				falg.setErr(-1, "报告状态为空！！！");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			if (StringUtil.getInstance().isNullString(inMuf)) {
				System.out.println("厂商为空！！！");
				falg.setErr(-1, "厂商为空！！！");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			if (StringUtil.getInstance().isNullString(admType)) {
				System.out.println("门急住别为空！！！");
				falg.setErr(-1, "门急住别为空！！！");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			if ("F".equals(typeDJ)) {
				if (StringUtil.getInstance().isNullString(shDr)) {
					System.out.println("审核医师为空！！！");
					falg.setErr(-1, "审核医师为空！！！");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctApplyNo.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				if (StringUtil.getInstance().isNullString(bgDr)) {
					System.out.println("报告医师为空！！！");
					falg.setErr(-1, "报告医师为空！！！");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctApplyNo.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
			}
			// 确认是数据存在数据库中
			TParm dataParm = PDFJdo.getInstance().QueryMedapply(
					vctApplyNo.get(i).toString(), "RIS");
			if (dataParm.getCount() > 0) {
				falg = RISJdo.getInstance().upDateRisSHBG(
						vctApplyNo.get(i).toString(), status, djDate, shDate,
						shDr, bgDr, typeDJ, zdyx, zdsj, type, inMuf, chNo,
						bgSeq, imageUrl, pdfPath, admType);
				if (falg.getErrCode() < 0) {
					falg.setErr(-1, "报告更新失败");
					return falg;
				}
			} else {
				System.out.println("未在Medapply中查询到报告医嘱！");
				falg.setErr(-1, "未查询到报告医嘱");
				return falg;
			}
			try {
				msa.setMsh(new MSH());
			} catch (HL7Exception ex) {
				ex.printStackTrace();
			}
			if (falg.getErrCode() < 0) {
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				break;
			} else {
				msa.setAcknowledgmentCode("CA");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
			}
			this.vct.add(msa);
		}
		return falg;
	}

	/**
	 * 得到诊断印象
	 * 
	 * @return String
	 */
	public List<String> getCEData() throws HL7Exception{
		List<String> list = new ArrayList<String>();
		String zdyx = "";
		String hl7Data = this.bytFileData;
		String enter = "" + (char) 10;
		String fileData[] = StringTool.parseLine(hl7Data, enter);
		for (String temp : fileData) {
			String massagesData = StringTool.parseLine(temp, "|")[0];
			if ("OBX".equals(massagesData)) {
				// if (6 > fileData.length) {
				// break;
				// }
				if ("CE".equals(StringTool.parseLine(temp, "|")[2])) {
					if (StringTool.parseLine(temp, "|").length < 6) {
						break;
					}
					zdyx = StringTool.parseLine(temp, "|")[5];
					list.add(zdyx.trim());
				}
			}
		}
		return list;
	}

	/**
	 * 得到诊断所见
	 * 
	 * @return String
	 */
	public List<String> getTXData()throws HL7Exception {
		List<String> list = new ArrayList<String>();
		String zdsj = "";
		String hl7Data = this.bytFileData;
		String enter = "" + (char) 10;
		String fileData[] = StringTool.parseLine(hl7Data, enter);
		for (String temp : fileData) {
			String massagesData = StringTool.parseLine(temp, "|")[0];
			if ("OBX".equals(massagesData)) {
				// if (6 > fileData.length) {
				// break;
				// }
				if ("TX".equals(StringTool.parseLine(temp, "|")[2])) {
					if (StringTool.parseLine(temp, "|").length < 6) {
						break;
					}
					zdsj = StringTool.parseLine(temp, "|")[5];
					list.add(zdsj.trim());
				}
			}
		}
		return list;
	}

	/**
	 * 得到报告页号
	 * 
	 * @return String
	 */
	public List<String> getTXBGNo()throws HL7Exception {
		List<String> list = new ArrayList<String>();
		String bgNo = "";
		String hl7Data = this.bytFileData;
		String enter = "" + (char) 10;
		String fileData[] = StringTool.parseLine(hl7Data, enter);
		for (String temp : fileData) {
			String massagesData = StringTool.parseLine(temp, "|")[0];
			if ("OBX".equals(massagesData)) {
				if ("TX".equals(StringTool.parseLine(temp, "|")[2])) {
					if (StringTool.parseLine(temp, "|").length < 6) {
						break;
					}
					bgNo = StringTool.parseLine(temp, "|")[21];
					list.add(bgNo.trim());
				}
			}
		}
		return list;
	}

	/**
	 * 得到图像地址
	 * 
	 * @return String
	 */
	public List<String> getTXimageUrl()throws HL7Exception {
		List<String> list = new ArrayList<String>();
		String imageUrl = "";
		String hl7Data = this.bytFileData;
		String enter = "" + (char) 10;
		String fileData[] = StringTool.parseLine(hl7Data, enter);
		for (String temp : fileData) {
			String massagesData = StringTool.parseLine(temp, "|")[0];
			if ("OBX".equals(massagesData)) {
				if ("TX".equals(StringTool.parseLine(temp, "|")[2])) {
					if (StringTool.parseLine(temp, "|").length < 6) {
						break;
					}
					String[] line = StringTool.parseLine(temp, "|")[20]
							.split("@@");
					if (line.length > 0)
						list.add(line[0]);
				}
			}
		}
		return list;
	}

	/**
	 * 得到pdfPath报告路径
	 * 
	 * @return String
	 */
	public List<String> getPdfPath()throws HL7Exception {
		List<String> list = new ArrayList<String>();
		String PdfPath = "";
		String hl7Data = this.bytFileData;
		String enter = "" + (char) 10;
		String fileData[] = StringTool.parseLine(hl7Data, enter);
		for (String temp : fileData) {
			String massagesData = StringTool.parseLine(temp, "|")[0];
			if ("OBX".equals(massagesData)) {
				if ("TX".equals(StringTool.parseLine(temp, "|")[2])) {
					if (StringTool.parseLine(temp, "|").length < 6) {
						break;
					}
					String[] line = StringTool.parseLine(temp, "|")[20]
							.split("@@");
					if (line.length > 1)
						list.add(line[1]);
				}
			}
		}
		return list;
	}

	/**
	 * 阴阳性
	 * 
	 * @return String
	 */
	public List<String> getYYType()throws HL7Exception {
		List<String> list = new ArrayList<String>();
		String type = "";
		String hl7Data = this.bytFileData;
		String enter = "" + (char) 10;
		String fileData[] = StringTool.parseLine(hl7Data, enter);
		for (String temp : fileData) {
			String massagesData = StringTool.parseLine(temp, "|")[0];
			if ("OBX".equals(massagesData)) {
				// if (8 > fileData.length) {
				// break;
				// }
				if ("TX".equals(StringTool.parseLine(temp, "|")[2])) {
					if (StringTool.parseLine(temp, "|").length < 9) {
						break;
					}
					type = StringTool.parseLine(temp, "|")[8];
					list.add("N".equals(type) ? "T" : "H");
				}

			}
		}
		return list;
	}

	/**
	 * 获得当前时间
	 * 
	 * @return String
	 */
	public String getNow() {
		return dateFormat.format(new Date());
	}

	/**
	 * LIS流程控制
	 */
	private void ControlLISProsess() throws HL7Exception {
		// 得到IN厂商
		String inMuefStr = this.parm.getValue("INMEUF", 0);
		// 得到OUT厂商
		String outMuefStr = this.parm.getValue("OUTMEUF", 0);
		// 消息类型
		String masType = this.parm.getValue("MTYPE", 0);
		String masId = this.parm.getValue("MASID", 0);
		String enter = "" + (char) 13 + (char) 10;
		// 预约
		if ("SIU^S12".equals(masType)) {
			if (risYY().getErrCode() >= 0) {
				StringBuffer buff = new StringBuffer();
				MSH msh = new MSH();
				msh.setSendingApplication(outMuefStr);
				msh.setReceivingApplication(inMuefStr);
				msh.setDateTimeOfMessage(this.getNow());
				msh.setMessageType("ACK");
				msh.setMessageControlID(masId);
				buff.append(msh.toString());
				buff.append(enter);
				for (int i = 0; i < this.vct.size(); i++) {
					buff.append(((MSA) this.vct.get(i)).toString());
				}
				// System.out.println("返回的ACK:" + buff.toString());
				String str = new String(buff);
				this.setBytReturnData(str);
				this.setReturnCode("0");
			} else {
				StringBuffer buff = new StringBuffer();
				MSH msh = new MSH();
				msh.setSendingApplication(outMuefStr);
				msh.setReceivingApplication(inMuefStr);
				msh.setDateTimeOfMessage(this.getNow());
				msh.setMessageType("ACK");
				msh.setMessageControlID(masId);
				buff.append(msh.toString());
				buff.append(enter);
				for (int i = 0; i < this.vct.size(); i++) {
					buff.append(((MSA) this.vct.get(i)).toString());
				}
				// System.out.println("返回的ACK:" + buff.toString());
				String str = new String(buff);
				this.setBytReturnData(risYY().getErrText());
				this.setReturnCode("-1");
			}
		}
		// 取消预约
		if ("SIU^S15".equals(masType)) {
			if (this.risCanYY().getErrCode() >= 0) {
				StringBuffer buff = new StringBuffer();
				MSH msh = new MSH();
				msh.setSendingApplication(outMuefStr);
				msh.setReceivingApplication(inMuefStr);
				msh.setDateTimeOfMessage(this.getNow());
				msh.setMessageType("ACK");
				msh.setMessageControlID(masId);
				buff.append(msh.toString());
				buff.append(enter);
				for (int i = 0; i < this.vct.size(); i++) {
					buff.append(((MSA) this.vct.get(i)).toString());
				}
				// System.out.println("返回的ACK:" + buff.toString());
				String str = new String(buff);
				this.setBytReturnData(str);
				this.setReturnCode("0");
			} else {
				StringBuffer buff = new StringBuffer();
				MSH msh = new MSH();
				msh.setSendingApplication(outMuefStr);
				msh.setReceivingApplication(inMuefStr);
				msh.setDateTimeOfMessage(this.getNow());
				msh.setMessageType("ACK");
				msh.setMessageControlID(masId);
				buff.append(msh.toString());
				buff.append(enter);
				for (int i = 0; i < this.vct.size(); i++) {
					buff.append(((MSA) this.vct.get(i)).toString());
				}
				// System.out.println("返回的ACK:" + buff.toString());
				String str = new String(buff);
				this.setBytReturnData(risCanYY().getErrText());
				this.setReturnCode("-1");
			}
		}
		// 到检、取消到检、检查完成
		if (masType.equals("ORM^O01")) {
			if (this.risDJ().getErrCode() >= 0) {
				StringBuffer buff = new StringBuffer();
				MSH msh = new MSH();
				msh.setSendingApplication(outMuefStr);
				msh.setReceivingApplication(inMuefStr);
				msh.setDateTimeOfMessage(this.getNow());
				msh.setMessageType("ACK");
				msh.setMessageControlID(masId);
				buff.append(msh.toString());
				buff.append(enter);
				for (int i = 0; i < this.vct.size(); i++) {
					buff.append(((MSA) this.vct.get(i)).toString());
				}
				// System.out.println("返回的ACK:" + buff.toString());
				String str = new String(buff);
				this.setBytReturnData(str);
				this.setReturnCode("0");
			} else {
				StringBuffer buff = new StringBuffer();
				MSH msh = new MSH();
				msh.setSendingApplication(outMuefStr);
				msh.setReceivingApplication(inMuefStr);
				msh.setDateTimeOfMessage(this.getNow());
				msh.setMessageType("ACK");
				msh.setMessageControlID(masId);
				buff.append(msh.toString());
				buff.append(enter);
				for (int i = 0; i < this.vct.size(); i++) {
					buff.append(((MSA) this.vct.get(i)).toString());
				}
				// System.out.println("返回的ACK:" + buff.toString());
				String str = new String(buff);
				this.setBytReturnData(risDJ().getErrText());
				this.setReturnCode("-1");
			}
		}
		// 报告完成
		if (masType.equals("ORU^R01")) {
			if (this.risBGWC().getErrCode() >= 0) {
				StringBuffer buff = new StringBuffer();
				MSH msh = new MSH();
				msh.setSendingApplication(outMuefStr);
				msh.setReceivingApplication(inMuefStr);
				msh.setDateTimeOfMessage(this.getNow());
				msh.setMessageType("ACK");
				msh.setMessageControlID(masId);
				buff.append(msh.toString());
				buff.append(enter);
				for (int i = 0; i < this.vct.size(); i++) {
					buff.append(((MSA) this.vct.get(i)).toString());
				}
				// System.out.println("返回的ACK:" + buff.toString());
				String str = new String(buff);
				this.setBytReturnData(str);
				this.setReturnCode("0");
			} else {
				StringBuffer buff = new StringBuffer();
				MSH msh = new MSH();
				msh.setSendingApplication(outMuefStr);
				msh.setReceivingApplication(inMuefStr);
				msh.setDateTimeOfMessage(this.getNow());
				msh.setMessageType("ACK");
				msh.setMessageControlID(masId);
				buff.append(msh.toString());
				buff.append(enter);
				// System.out.println("" + this.vct.size());
				for (int i = 0; i < this.vct.size(); i++) {
					buff.append(((MSA) this.vct.get(i)).toString());
				}
				// System.out.println("返回的ACK:" + buff.toString());
				String str = new String(buff);
				this.setBytReturnData(risBGWC().getErrText());
				this.setReturnCode("-1");
			}
		}
	}

	public String getBytFileData() {
		return bytFileData;
	}

	public void setBytFileData(String bytFileData) {
		this.bytFileData = bytFileData;
	}

	public String getBytReturnData() {
		return bytReturnData;
	}

	public void setBytReturnData(String bytReturnData) {
		this.bytReturnData = bytReturnData;
	}

	/**
	 * 设置参数
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setParm(TParm parm) {
		this.parm = parm;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

}
