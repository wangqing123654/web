package jdo.hl7.ws;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import jdo.hl7.Hl7Tool;
import jdo.hl7.LISJdo;
import jdo.hl7.pojo.MSA;
import jdo.hl7.pojo.MSH;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.javahis.exception.HL7Exception;
import com.javahis.util.StringUtil;

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
public class ALISOperation implements ILISOperation {
	// HL7文件内容
	private String bytFileData;
	// BPEL设置参数对象
	private TParm parm;
	// 返回数据
	private String bytReturnData;
	// 返回编码
	private String returnCode;

	// 时间
	public static DateFormat dateFormat = new SimpleDateFormat(
			"yyyyMMddhhmmssSSS");
	// 检体核收
	private Vector vct;
	// 检体异常项目；
	private Vector abnormalityVct;
	// 检体异常消息内容；
	private TParm abnormalityParm;

	/**
	 * 拿到LIS回发文件全部内容
	 * 
	 * @param data
	 *            byte[]
	 * @param parm
	 *            TConfigParm
	 */
	public synchronized void mainLisData(String data, TParm parm)
			throws HL7Exception {
		this.setBytFileData(data);
		this.setParm(parm);
		this.ControlLISProsess();
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
	 * 签收
	 * 
	 * @return boolean
	 */
	public TParm lisQS() throws HL7Exception {
		TParm falg = new TParm();
		// 条码号
		Vector vctLab = (Vector) this.parm.getData("LABNO");
		// 状态
		Vector vctStus = (Vector) this.parm.getData("LISSTR");
		// 门急主别
		Vector admType = (Vector) this.parm.getData("ADMTYPE");
		this.vct = new Vector();
		for (int i = 0; i < vctLab.size(); i++) {
			MSA msa = new MSA();
			if (StringUtil.getInstance()
					.isNullString(vctStus.get(i).toString())) {
				System.out.println("状态码为空！！！");
				falg.setErr(-1, "状态码为空！");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctLab.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			// 签收
			if (vctStus.get(i).toString().equals("SC")) {
				if (StringUtil.getInstance().isNullString(
						vctLab.get(i).toString())) {
					System.out.println("条码为空！！！");
					falg.setErr(-1, "条码为空！");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				if (StringUtil.getInstance().isNullString(
						admType.get(i).toString())) {
					System.out.println("门急住别为空！！！");
					falg.setErr(-1, "门急住别为空！");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				falg = LISJdo.getInstance().upDateLisQS(
						vctLab.get(i).toString(), admType.get(i).toString());
				try {
					msa.setMsh(new MSH());
				} catch (HL7Exception ex) {
					ex.printStackTrace();
				}
				if (falg.getErrCode() < 0) {
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				} else {
					msa.setAcknowledgmentCode("CA");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
				}
				this.vct.add(msa);
			}
			// 取消签收
			if (vctStus.get(i).toString().equals("CA")) {
				if (StringUtil.getInstance().isNullString(
						vctLab.get(i).toString())) {
					System.out.println("条码为空！！！");
					falg.setErr(-1, "条码为空！");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				if (StringUtil.getInstance().isNullString(
						admType.get(i).toString())) {
					System.out.println("门急住别为空！！！");
					falg.setErr(-1, "门急住别为空！");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				falg = LISJdo.getInstance().upDateQXLisQS(
						vctLab.get(i).toString(), admType.get(i).toString());
				try {
					msa.setMsh(new MSH());
				} catch (HL7Exception ex) {
					ex.printStackTrace();
				}
				if (falg.getErrCode() < 0) {
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				} else {
					msa.setAcknowledgmentCode("CA");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					System.out.println("msa:" + msa.toString());
				}
				this.vct.add(msa);
			}
		}
		return falg;
	}

	/**
	 * 到检
	 * 
	 * @return boolean
	 */
	public TParm lisDJ() throws HL7Exception {
		TParm falg = new TParm();
		// 条码号
		Vector vctLab = (Vector) this.parm.getData("LABNO");
		// 状态
		Vector vctStus = (Vector) this.parm.getData("LISSTR");
		// 门急主别
		Vector admType = (Vector) this.parm.getData("ADMTYPE");
		this.vct = new Vector();
		for (int i = 0; i < vctLab.size(); i++) {
			MSA msa = new MSA();
			if (StringUtil.getInstance()
					.isNullString(vctStus.get(i).toString())) {
				System.out.println("状态码为空！！！");
				falg.setErr(-1, "状态码为空！");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctLab.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			// 到检
			if (vctStus.get(i).toString().equals("SC")) {
				if (StringUtil.getInstance().isNullString(
						vctLab.get(i).toString())) {
					System.out.println("条码为空！！！");
					falg.setErr(-1, "条码为空！");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				if (StringUtil.getInstance().isNullString(
						admType.get(i).toString())) {
					System.out.println("门急住别为空！！！");
					falg.setErr(-1, "门急住别为空！");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				falg = LISJdo.getInstance().upDateLisDJ(
						vctLab.get(i).toString(), admType.get(i).toString());
				try {
					msa.setMsh(new MSH());
				} catch (HL7Exception ex) {
					ex.printStackTrace();
				}
				if (falg.getErrCode() < 0) {
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				} else {
					msa.setAcknowledgmentCode("CA");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
				}
				this.vct.add(msa);
			}
			// 取消到检
			if (vctStus.get(i).toString().equals("CA")) {
				if (StringUtil.getInstance().isNullString(
						vctLab.get(i).toString())) {
					System.out.println("条码为空！！！");
					falg.setErr(-1, "条码为空！");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				if (StringUtil.getInstance().isNullString(
						admType.get(i).toString())) {
					System.out.println("门急住别为空！！！");
					falg.setErr(-1, "门急住别为空！");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				falg = LISJdo.getInstance().upDateLisQXDJ(
						vctLab.get(i).toString(), admType.get(i).toString());
				try {
					msa.setMsh(new MSH());
				} catch (HL7Exception ex) {
					ex.printStackTrace();
				}
				if (falg.getErrCode() < 0) {
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				} else {
					msa.setAcknowledgmentCode("CA");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					System.out.println("msa:" + msa.toString());
				}
				this.vct.add(msa);
			}
			// 检体拒收
			if (vctStus.get(i).toString().equals("DC")) {
				if (StringUtil.getInstance().isNullString(
						vctLab.get(i).toString())) {
					System.out.println("条码为空！！！");
					falg.setErr(-1, "条码为空！");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				falg = LISJdo.getInstance().upDateLisJSDJ(
						vctLab.get(i).toString(), admType.get(i).toString());
				try {
					msa.setMsh(new MSH());
				} catch (HL7Exception ex) {
					ex.printStackTrace();
				}
				if (falg.getErrCode() < 0) {
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				} else {
					msa.setAcknowledgmentCode("CA");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					System.out.println("msa:" + msa.toString());
				}
				this.vct.add(msa);
			}
		}
		return falg;
	}

	/**
	 * 审核
	 * 
	 * @return boolean
	 */
	public TParm lisSHWC() throws HL7Exception {
		TParm falg = new TParm();
		Vector vctLab = (Vector) this.parm.getData("LABNO");
		Vector vctStus = (Vector) this.parm.getData("LISSTR");
		this.vct = new Vector();
		for (int i = 0; i < vctLab.size(); i++) {
			if (vctStus.get(i).toString().equals("CM")) {
				MSA msa = new MSA();
				if (StringUtil.getInstance().isNullString(
						vctStus.get(i).toString())) {
					System.out.println("状态码为空！！！");
					falg.setErr(-1, "状态码为空！！！");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				falg = LISJdo.getInstance()
						.upDateSHWC(vctLab.get(i).toString());
				if (falg.getErrCode() < 0) {
					return falg;
				}
				try {
					msa.setMsh(new MSH());
				} catch (HL7Exception ex) {
					ex.printStackTrace();
				}
				if (falg.getErrCode() < 0) {
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				} else {
					msa.setAcknowledgmentCode("CA");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
				}
				this.vct.add(msa);
			}
		}
		return falg;
	}

	/**
	 * 报告
	 * 
	 * @return boolean
	 */
	public TParm lisBGWC() throws HL7Exception {
		TParm falg = new TParm();
		String labNo = this.parm.getValue("LABNO", 0);
		Vector obrVct = this.parm
				.getVector("ORDERNO;JYTIME;BGTIME;BBJSTIME;JYUSER;SHUSER;BZTXT;LISBGSTATUS;YBBH");
		Vector obxVct = this.parm
				.getVector("XHNO;BGTYPE;JCDM;JGNO;PROJECTEND;UNIT;CKFW;YCBJ;ADVICEID;INTEMCODE;ADVICEIDLIS");
		Vector pv1Vct = this.parm.getVector("OEIH");
		// 门急住别
		String admType = ((Vector) pv1Vct.get(0)).get(0).toString();
		Vector PIDVct1 = this.parm.getVector("MRNO");
		Vector PIDVct2 = this.parm.getVector("PATNAME");
		String mrNo = ((Vector) PIDVct1.get(0)).get(0).toString();
		String patName = ((Vector) PIDVct2.get(0)).get(0).toString();
		// System.out.println("pv1字段:" + pv1Vct);
		this.vct = new Vector();
		// add by lx 异常项
		this.abnormalityVct = new Vector();
		TParm mesParm = new TParm();
		int mesCount = 0;
		TParm medData = Hl7Tool.getInstance().getOrder(labNo, "LIS", "");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "未查询到医嘱");
			return medData;
		}
		if (((Vector) obrVct.get(0)).get(7).toString().equals("D")) {
			falg = LISJdo.getInstance().delLAB_GENRPTDTLData(labNo);
			MSA msa = new MSA();
			try {
				msa.setMsh(new MSH());
			} catch (HL7Exception ex) {
				ex.printStackTrace();
			}
			if (falg.getErrCode() < 0) {
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(labNo);
				msa.setMessageStater("SC");
			} else {
				msa.setAcknowledgmentCode("CA");
				msa.setLabNo(labNo);
				msa.setMessageStater("SC");
				System.out.println("msa:" + msa.toString());
			}
			this.vct.add(msa);
			System.out.println("最后返回VCTOR：" + this.vct);
			return falg;
		}
		long a = System.currentTimeMillis();
		// 样本编号(仅针对药敏实验)
		String ybbh = ((Vector) obrVct.get(0)).get(8).toString();
		int lenth = LISJdo.getInstance().getorderNolen(admType);
		if (!ybbh.equals("")) {
			// 删除药敏
			falg = LISJdo.getInstance().delLAB_ANTISENSTESTData(labNo, ybbh);
			// 删除细菌
			falg = LISJdo.getInstance().delLAB_CULRPTDTLData(labNo, ybbh);
		} else {
			// 删除该条码的的报告数据
			falg = LISJdo.getInstance().delLAB_GENRPTDTLData(labNo);
		}
		// 报告检验人
		String drUser = ((Vector) obrVct.get(0)).get(4).toString();
		// 审核人
		String shUser = ((Vector) obrVct.get(0)).get(5).toString();
		for (int i = 0; i < obxVct.size(); i++) {
			// 临检
			// HOSP_AREA,LAB_NUMBER,TESTSET_SEQ,RPT_STTS,TESTITEM_CODE,TEST_VALUE,TEST_UNIT,REMARK,UPPE_LIMIT,LOWER_LIMIT,CRTCLUPLMT,CRTCLLWLMT,SEND_DTTM
			if (((Vector) obxVct.get(i)).get(1).toString().equals("1")) {
				// System.out.println("临检");
				TParm parm = new TParm();
				parm.setData("HOSP_AREA", "HIS"); // 1
				parm.setData("LAB_NUMBER", labNo); // 2
				String rptStts = ((Vector) obxVct.get(i)).get(0).toString();
				// System.out.println("RPT_STTS:" + rptStts);
				parm.setData("RPT_STTS", rptStts); // 4
				// 医令号
				String orderNo = ((Vector) obxVct.get(i)).get(3).toString()
						.substring(0, lenth); // 5
				parm.setData("ORDER_NO", orderNo);
				// System.out.println("--------orderNo-------------" + orderNo);
				// 医令序号
				String seqNo = ((Vector) obxVct.get(i)).get(3).toString()
						.substring(
								lenth,
								((Vector) obxVct.get(i)).get(3).toString()
										.length()); // 6
				parm.setData("SEQ_NO", seqNo);
				// System.out.println("--------seqNo-------------" + seqNo);
				// 项目代码
				int lengthCount = StringTool.parseLine(((Vector) obxVct.get(i))
						.get(2).toString(), "^").length;
				String testItemCode = StringTool.parseLine(((Vector) obxVct
						.get(i)).get(2).toString(), "^")[0];
				// System.out.println("TESTITEM_CODE:" + testItemCode);
				parm.setData("TESTITEM_CODE", testItemCode); // 5
				String testItemDesc = "";
				// 项目名称
				if (lengthCount > 1) {
					testItemDesc = StringTool.parseLine(
							((Vector) obxVct.get(i)).get(2).toString(), "^")[1];
					// System.out.println("TESTITEM_DESC:" + testItemDesc);
					parm.setData("TESTITEM_DESC", testItemDesc);
				} else {
					parm.setData("TESTITEM_DESC", "");
				}
				String testItemEnDesc = "";
				// 英文名称
				if (lengthCount > 2) {
					testItemEnDesc = StringTool.parseLine(((Vector) obxVct
							.get(i)).get(2).toString(), "^")[2];
					// System.out.println("TESTITEMEN_DESC:" + testItemEnDesc);
					parm.setData("TESTITEMEN_DESC", testItemEnDesc);
				} else {
					parm.setData("TESTITEMEN_DESC", testItemCode);
				}
				String testValue = "";
				try {
					testValue = StringTool.parseLine(((Vector) obxVct.get(i))
							.get(4).toString(), "^")[0];
				} catch (Exception ex) {
					System.out.println("项目结果为空！");
				}
				// System.out.println("项目结果:" + testValue);
				parm.setData("TEST_VALUE", testValue); // 6
				String testUnit = ((Vector) obxVct.get(i)).get(5).toString();
				// System.out.println("项目单位:" + testUnit);
				parm.setData("TEST_UNIT", testUnit); // 7
				String remark = ((Vector) obxVct.get(i)).get(7).toString();
				// System.out.println("异常标记:" + remark);
				parm.setData("REMARK", "");
				// System.out.println("长度:" +
				// StringTool.parseLine(((Vector) obxVct.get(i)).
				// get(6).toString(), "^").length);
				String uppeLimit = "";
				try {
					uppeLimit = StringTool.parseLine(((Vector) obxVct.get(i))
							.get(6).toString(), "^")[1];
					// System.out.println("高值:" + uppeLimit);
				} catch (Exception ex) {
					System.out.println("高值:" + uppeLimit);
				}
				parm.setData("UPPE_LIMIT", uppeLimit);
				String lowerLimit = StringTool.parseLine(((Vector) obxVct
						.get(i)).get(6).toString(), "^")[0];
				// System.out.println("低值:" + lowerLimit);
				parm.setData("LOWER_LIMIT", lowerLimit);
				int length = StringTool.parseLine(((Vector) obxVct.get(i)).get(
						6).toString(), "^").length;
				String crtcluplmt = "";
				if (length == 4) {
					crtcluplmt = StringTool.parseLine(((Vector) obxVct.get(i))
							.get(6).toString(), "^")[3];
				}
				// System.out.println("超高:" + crtcluplmt);
				parm.setData("CRTCLUPLMT", crtcluplmt);

				String crtcllwlmt = "";
				if (length == 4) {
					crtcllwlmt = StringTool.parseLine(((Vector) obxVct.get(i))
							.get(6).toString(), "^")[2];
				}
				// System.out.println("超低:" + crtcllwlmt);
				parm.setData("CRTCLLWLMT", remark);
				String sendDttm = this.getNow().substring(0, 14);
				// System.out.println("报告时间:" + sendDttm);
				parm.setData("SEND_DTTM", sendDttm);
				// // System.out.println("------->>>>>" + i + "条数据！");
				// 检查仪器代码
				String[] dev = StringTool.parseLine(((Vector) obxVct.get(i))
						.get(10).toString(), "^");
				String devCode = "";
				String devDesc = "";
				try {
					if (dev.length > 0) {
						devCode = dev[0];
					}
					// // System.out.println("检验仪器代码:" + devCode);
					if (dev.length > 1) {
						devDesc = dev[1];
					}
					// // System.out.println("检验仪器名称:" + devDesc);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				parm.setData("DEV_CODE", devCode);
				parm.setData("DEV_DESC", devDesc);
				// 插入库数据MED_LIST_PRT；
				falg = LISJdo.getInstance().insertLAB_GENRPTDTLData(parm);
				if (falg.getErrCode() < 0) {
					LISJdo.getInstance().delLAB_GENRPTDTLData(labNo);
					break;
				}
				/**
				 * 状态标记 L:低 H:高 SL:超低 SH:超高 NM:正常
				 */
				if (remark.equals("SL") || remark.equals("SH")) {
					// 加入异常项目；
					mesParm.addData("MR_NO", mrNo);
					mesParm.addData("PAT_NAME", patName);
					mesParm.addData("CAT1_TYPE", "LIS");
					mesParm.addData("APPLICATION_NO", labNo);
					mesParm.addData("RPDTL_SEQ", rptStts);
					mesParm.addData("TESTITEM_ENG_DESC", testItemEnDesc);
					mesParm.addData("TESTITEM_CODE", testItemCode);
					mesParm.addData("TESTITEM_CHN_DESC", testItemDesc);
					mesParm.addData("TEST_VALUE", testValue);
					mesParm.addData("TEST_UNIT", testUnit);
					mesParm.addData("CRTCLLWLMT", remark);
					mesCount++;
				}

				// $$============add end by lx 2011-05-17 构造出异常结果
				// 如何取异常值=================；
			}
			// 微免
			if (((Vector) obxVct.get(i)).get(1).toString().equals("3")) {
				// 微免 CAT1_TYPE APPLICATION_NO RPDTL_SEQ CULTURE_CODE
				// CULTURE_CHN_DESC ANTI_CODE ANTI_CHN_DESC TEST_VALUE TEST_UNIT
				// REMARK SENS_LEVEL DEV_CODE
				// System.out.println("微免");
				TParm parm = new TParm();
				parm.setData("HOSP_AREA", "HIS"); // 1
				parm.setData("LAB_NUMBER", labNo); // 2
				String rptStts = ((Vector) obxVct.get(i)).get(0).toString(); // 3
				parm.setData("SAMPLE_NO", ybbh); // 4
				// System.out.println("RPDTL_SEQ:" + rptStts);
				parm.setData("RPDTL_SEQ", rptStts); // 4
				// 抗生素代码
				int lengthCount = StringTool.parseLine(((Vector) obxVct.get(i))
						.get(4).toString(), "^").length;
				String antiItemCode = "";
				try {
					antiItemCode = StringTool.parseLine(
							((Vector) obxVct.get(i)).get(4).toString(), "^")[0];
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// System.out.println("ANTI_CODE:" + antiItemCode);
				parm.setData("ANTI_CODE", antiItemCode); // 5
				// 抗生素名称
				if (lengthCount > 1) {
					String antiItemDesc = StringTool.parseLine(((Vector) obxVct
							.get(i)).get(4).toString(), "^")[1];
					// System.out.println("ANTI_CHN_DESC:" + antiItemDesc);
					parm.setData("ANTI_CHN_DESC", antiItemDesc);
				} else {
					parm.setData("ANTI_CHN_DESC", "");
				}

				// 英文名称
				if (lengthCount > 2) {
					String antiItemEnDesc = StringTool.parseLine(
							((Vector) obxVct.get(i)).get(2).toString(), "^")[2];
					// System.out.println("ANTI_ENG_DESC:" + antiItemEnDesc);
					parm.setData("ANTI_ENG_DESC", antiItemEnDesc);
				} else {
					parm.setData("ANTI_ENG_DESC", "");
				}
				parm.setData("TEST_VALUE", ""); // 6 未传值现赋为空
				parm.setData("TEST_UNIT", ""); // 7 未传值现赋为空
				parm.setData("REMARK", ""); // 8 未传值现赋为空
				String sensLevel = "";
				try {
					sensLevel = StringTool.parseLine(((Vector) obxVct.get(i))
							.get(7).toString(), "^")[0];
				} catch (Exception ex) {
					// System.out.println("敏感性结果为空！");
				}
				// System.out.println("sensLevel:" + sensLevel);
				parm.setData("SENS_LEVEL", sensLevel);
				// // System.out.println("------->>>>>" + i + "条数据！");
				// 检查仪器代码
				// String devCode = ((Vector)obxVct.get(i)).get(13).toString();
				String[] dev = StringTool.parseLine(((Vector) obxVct.get(i))
						.get(10).toString(), "^");
				String devCode = "";
				try {
					if (dev.length > 0) {
						devCode = dev[0];
					}
					// System.out.println("检验仪器代码:" + devCode);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				parm.setData("DEV_CODE", devCode);
				// 细菌代码
				int lengthCountT = StringTool.parseLine(
						((Vector) obxVct.get(i)).get(9).toString(), "^").length;
				String cultItemCode = "";
				try {
					cultItemCode = StringTool.parseLine(
							((Vector) obxVct.get(i)).get(9).toString(), "^")[0];
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// System.out.println("细菌编码:" + cultItemCode);
				parm.setData("CULTURE_CODE", cultItemCode); // 5
				// 细菌名称
				if (lengthCountT > 1) {
					String cultItemDesc = StringTool.parseLine(((Vector) obxVct
							.get(i)).get(9).toString(), "^")[1];
					// System.out.println("细菌名称:" + cultItemDesc);
					parm.setData("CULTURE_CHN_DESC", cultItemDesc);
				} else {
					parm.setData("CULTURE_CHN_DESC", "");
				}
				// 插入库数据LAB_ANTISENSTEST；
				falg = LISJdo.getInstance().insertLAB_ANTISENSTESTData(parm);
				if (falg.getErrCode() < 0) {
					LISJdo.getInstance().delLAB_ANTISENSTESTData(labNo, ybbh);
					break;
				}
			}
			// 细菌
			if (((Vector) obxVct.get(i)).get(1).toString().equals("2")) {
				// System.out.println("细菌");
				TParm parm = new TParm();
				parm.setData("HOSP_AREA", "HIS"); // 1
				parm.setData("LAB_NUMBER", labNo); // 2
				parm.setData("SAMPLE_NO", ybbh); //
				String rptStts = ((Vector) obxVct.get(i)).get(0).toString(); // 3
				// System.out.println("RPDTL_SEQ:" + rptStts);
				parm.setData("RPDTL_SEQ", rptStts); // 4
				// 细菌代码
				int lengthCount = StringTool.parseLine(((Vector) obxVct.get(i))
						.get(4).toString(), "^").length;
				String cultItemCode = "";
				try {
					cultItemCode = StringTool.parseLine(
							((Vector) obxVct.get(i)).get(4).toString(), "^")[0];
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// System.out.println("CULTURE_CODE:" + cultItemCode);
				parm.setData("CULTURE_CODE", cultItemCode); // 5
				// 细菌名称
				if (lengthCount > 1) {
					String cultItemDesc = StringTool.parseLine(((Vector) obxVct
							.get(i)).get(4).toString(), "^")[1];
					// System.out.println("CULTURE_CHN_DESC:" + cultItemDesc);
					parm.setData("CULTURE_CHN_DESC", cultItemDesc);
				} else {
					parm.setData("CULTURE_CHN_DESC", "");
				}
				parm.setData("CULTURE_RESULT", ""); // 培养结果报告内容
				parm.setData("COLONY_COUNT", ""); // 菌落数
				parm.setData("GRAM_STAIN", ""); // 格兰氏染色体报告
				parm.setData("INFECT_LEVEL", ""); // 感染程度
				parm.setData("REMARK", ""); // 备注
				String[] dev = StringTool.parseLine(((Vector) obxVct.get(i))
						.get(8).toString(), "^");
				String devCode = "";
				try {
					if (dev.length > 0) {
						devCode = dev[0];
					}
					// System.out.println("检验仪器代码:" + devCode);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				parm.setData("DEV_CODE", devCode); // 仪器代码
				// 插入库数据LAB_ANTISENSTEST；
				falg = LISJdo.getInstance().insertLAB_CULRPTDTLData(parm);
				if (falg.getErrCode() < 0) {
					LISJdo.getInstance().delLAB_CULRPTDTLData(labNo, ybbh);
					break;
				}
			}
		}
		mesParm.setCount(mesCount);
		MSA msa = new MSA();
		if (StringUtil.getInstance().isNullString(labNo)) {
			System.out.println("条码号为空！！！");
			falg.setErr(-1, "条码号为空！！！");
			return falg;
		}
		if (StringUtil.getInstance().isNullString(drUser)) {
			System.out.println("报告人员为空！！！");
			falg.setErr(-1, "报告人员为空！！！");
		}
		if (StringUtil.getInstance().isNullString(shUser)) {
			System.out.println("审核人员为空！！！");
			falg.setErr(-1, "审核人员为空！！！");
			return falg;
		}
		if (StringUtil.getInstance().isNullString(admType)) {
			// System.out.println("门急住别为空！！！");
			falg.setErr(-1, "门急住别为空！！！");
			return falg;
		}
		if (falg.getErrCode() < 0) {
			System.out.println("报告写入失败！！");
			msa.setAcknowledgmentCode("CE");
			msa.setLabNo(labNo);
			msa.setMessageStater("SC");
			this.vct.add(msa);
			return falg;
		}
		// 报告完成
		falg = LISJdo.getInstance().upDateBGEND(labNo,
				this.getNow().substring(0, 14), drUser, shUser, admType);
		try {
			msa.setMsh(new MSH());
		} catch (HL7Exception ex) {
			ex.printStackTrace();
		}
		if (falg.getErrCode() < 0) {
			System.out.println("报告状态写入失败！！");
			msa.setAcknowledgmentCode("CE");
			msa.setLabNo(labNo);
			msa.setMessageStater("SC");
		} else {
			msa.setAcknowledgmentCode("CA");
			msa.setLabNo(labNo);
			msa.setMessageStater("SC");
			// System.out.println("msa:" + msa.toString());
		}
		this.vct.add(msa);
		return falg;
	}

	/**
	 * 回发文件内容
	 * 
	 * @return byte[]
	 */
	public String getReturnValues() {
		return this.bytReturnData;
	}

	/**
	 * LIS流程控制
	 */
	private void ControlLISProsess() throws HL7Exception {
		// System.out.println("parm"+parm);
		// System.out.println("LINE"+this.getBytFileData());
		// MSH:MTYPE:8;
		String masType = this.parm.getValue("MTYPE", 0);
		// MSH:MASID:9;
		String masId = this.parm.getValue("MASID", 0);
		String enter = "" + (char) 13 + (char) 10;
		// 检体签收
		if (masType.equals("SIU^S12")) {
			if (this.lisQS().getErrCode() >= 0) {
				StringBuffer buff = new StringBuffer();
				MSH msh = new MSH();
				msh.setSendingApplication("HIS");
				msh.setReceivingApplication("LIS");
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
				msh.setSendingApplication("HIS");
				msh.setReceivingApplication("LIS");
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
				this.setBytReturnData(lisQS().getErrText());
				this.setReturnCode("-1");

			}
		}
		// 检体核收
		if (masType.equals("SIU")) {
			if (this.lisDJ().getErrCode() >= 0) {
				StringBuffer buff = new StringBuffer();
				MSH msh = new MSH();
				msh.setSendingApplication("HIS");
				msh.setReceivingApplication("LIS");
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
				msh.setSendingApplication("HIS");
				msh.setReceivingApplication("LIS");
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
				this.setBytReturnData(lisDJ().getErrText());
				this.setReturnCode("-1");

			}
		}
		// 审核完成
		if (masType.equals("ORR^O02")) {
			if (this.lisSHWC().getErrCode() >= 0) {
				StringBuffer buff = new StringBuffer();
				MSH msh = new MSH();
				msh.setSendingApplication("HIS");
				msh.setReceivingApplication("LIS");
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
				msh.setSendingApplication("HIS");
				msh.setReceivingApplication("LIS");
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
				this.setBytReturnData(lisSHWC().getErrText());
				this.setReturnCode("-1");
			}
		}
		// 报告完成
		// if(masType.equals("ORU^R01")){
		try {
			if (masType.equals("ORM^O01")) {
				if (this.lisBGWC().getErrCode() >= 0) {
					StringBuffer buff = new StringBuffer();
					MSH msh = new MSH();
					msh.setSendingApplication("HIS");
					msh.setReceivingApplication("LIS");
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
					msh.setSendingApplication("HIS");
					msh.setReceivingApplication("LIS");
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
					this.setBytReturnData(lisBGWC().getErrText());
					this.setReturnCode("-1");
				}
			}
		} catch (Exception ex) {
			// System.out.println("报告失败！");
			ex.printStackTrace();
			this.setBytReturnData("报告失败！");
			this.setReturnCode("-1");
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

	public TParm getParm() {
		return parm;
	}

	public void setParm(TParm parm) {
		this.parm = parm;
	}

	public void setAbnormalityParm(TParm abnormalityParm) {
		this.abnormalityParm = abnormalityParm;
	}

	public TParm getAbnormalityParm() {
		return abnormalityParm;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

}
