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
	// HL7�ļ�����
	private String bytFileData;
	// BPEL���ò�������
	private TParm parm;
	// ��������
	private String bytReturnData;
	// ���ر���
	private String returnCode;

	// ʱ��
	public static DateFormat dateFormat = new SimpleDateFormat(
			"yyyyMMddhhmmssSSS");
	// �������
	private Vector vct;
	// �����쳣��Ŀ��
	private Vector abnormalityVct;
	// �����쳣��Ϣ���ݣ�
	private TParm abnormalityParm;

	/**
	 * �õ�LIS�ط��ļ�ȫ������
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
	 * ��õ�ǰʱ��
	 * 
	 * @return String
	 */
	public String getNow() {
		return dateFormat.format(new Date());
	}

	/**
	 * ǩ��
	 * 
	 * @return boolean
	 */
	public TParm lisQS() throws HL7Exception {
		TParm falg = new TParm();
		// �����
		Vector vctLab = (Vector) this.parm.getData("LABNO");
		// ״̬
		Vector vctStus = (Vector) this.parm.getData("LISSTR");
		// �ż�����
		Vector admType = (Vector) this.parm.getData("ADMTYPE");
		this.vct = new Vector();
		for (int i = 0; i < vctLab.size(); i++) {
			MSA msa = new MSA();
			if (StringUtil.getInstance()
					.isNullString(vctStus.get(i).toString())) {
				System.out.println("״̬��Ϊ�գ�����");
				falg.setErr(-1, "״̬��Ϊ�գ�");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctLab.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			// ǩ��
			if (vctStus.get(i).toString().equals("SC")) {
				if (StringUtil.getInstance().isNullString(
						vctLab.get(i).toString())) {
					System.out.println("����Ϊ�գ�����");
					falg.setErr(-1, "����Ϊ�գ�");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				if (StringUtil.getInstance().isNullString(
						admType.get(i).toString())) {
					System.out.println("�ż�ס��Ϊ�գ�����");
					falg.setErr(-1, "�ż�ס��Ϊ�գ�");
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
			// ȡ��ǩ��
			if (vctStus.get(i).toString().equals("CA")) {
				if (StringUtil.getInstance().isNullString(
						vctLab.get(i).toString())) {
					System.out.println("����Ϊ�գ�����");
					falg.setErr(-1, "����Ϊ�գ�");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				if (StringUtil.getInstance().isNullString(
						admType.get(i).toString())) {
					System.out.println("�ż�ס��Ϊ�գ�����");
					falg.setErr(-1, "�ż�ס��Ϊ�գ�");
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
	 * ����
	 * 
	 * @return boolean
	 */
	public TParm lisDJ() throws HL7Exception {
		TParm falg = new TParm();
		// �����
		Vector vctLab = (Vector) this.parm.getData("LABNO");
		// ״̬
		Vector vctStus = (Vector) this.parm.getData("LISSTR");
		// �ż�����
		Vector admType = (Vector) this.parm.getData("ADMTYPE");
		this.vct = new Vector();
		for (int i = 0; i < vctLab.size(); i++) {
			MSA msa = new MSA();
			if (StringUtil.getInstance()
					.isNullString(vctStus.get(i).toString())) {
				System.out.println("״̬��Ϊ�գ�����");
				falg.setErr(-1, "״̬��Ϊ�գ�");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctLab.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			// ����
			if (vctStus.get(i).toString().equals("SC")) {
				if (StringUtil.getInstance().isNullString(
						vctLab.get(i).toString())) {
					System.out.println("����Ϊ�գ�����");
					falg.setErr(-1, "����Ϊ�գ�");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				if (StringUtil.getInstance().isNullString(
						admType.get(i).toString())) {
					System.out.println("�ż�ס��Ϊ�գ�����");
					falg.setErr(-1, "�ż�ס��Ϊ�գ�");
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
			// ȡ������
			if (vctStus.get(i).toString().equals("CA")) {
				if (StringUtil.getInstance().isNullString(
						vctLab.get(i).toString())) {
					System.out.println("����Ϊ�գ�����");
					falg.setErr(-1, "����Ϊ�գ�");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctLab.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				if (StringUtil.getInstance().isNullString(
						admType.get(i).toString())) {
					System.out.println("�ż�ס��Ϊ�գ�����");
					falg.setErr(-1, "�ż�ס��Ϊ�գ�");
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
			// �������
			if (vctStus.get(i).toString().equals("DC")) {
				if (StringUtil.getInstance().isNullString(
						vctLab.get(i).toString())) {
					System.out.println("����Ϊ�գ�����");
					falg.setErr(-1, "����Ϊ�գ�");
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
	 * ���
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
					System.out.println("״̬��Ϊ�գ�����");
					falg.setErr(-1, "״̬��Ϊ�գ�����");
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
	 * ����
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
		// �ż�ס��
		String admType = ((Vector) pv1Vct.get(0)).get(0).toString();
		Vector PIDVct1 = this.parm.getVector("MRNO");
		Vector PIDVct2 = this.parm.getVector("PATNAME");
		String mrNo = ((Vector) PIDVct1.get(0)).get(0).toString();
		String patName = ((Vector) PIDVct2.get(0)).get(0).toString();
		// System.out.println("pv1�ֶ�:" + pv1Vct);
		this.vct = new Vector();
		// add by lx �쳣��
		this.abnormalityVct = new Vector();
		TParm mesParm = new TParm();
		int mesCount = 0;
		TParm medData = Hl7Tool.getInstance().getOrder(labNo, "LIS", "");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "δ��ѯ��ҽ��");
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
			System.out.println("��󷵻�VCTOR��" + this.vct);
			return falg;
		}
		long a = System.currentTimeMillis();
		// �������(�����ҩ��ʵ��)
		String ybbh = ((Vector) obrVct.get(0)).get(8).toString();
		int lenth = LISJdo.getInstance().getorderNolen(admType);
		if (!ybbh.equals("")) {
			// ɾ��ҩ��
			falg = LISJdo.getInstance().delLAB_ANTISENSTESTData(labNo, ybbh);
			// ɾ��ϸ��
			falg = LISJdo.getInstance().delLAB_CULRPTDTLData(labNo, ybbh);
		} else {
			// ɾ��������ĵı�������
			falg = LISJdo.getInstance().delLAB_GENRPTDTLData(labNo);
		}
		// ���������
		String drUser = ((Vector) obrVct.get(0)).get(4).toString();
		// �����
		String shUser = ((Vector) obrVct.get(0)).get(5).toString();
		for (int i = 0; i < obxVct.size(); i++) {
			// �ټ�
			// HOSP_AREA,LAB_NUMBER,TESTSET_SEQ,RPT_STTS,TESTITEM_CODE,TEST_VALUE,TEST_UNIT,REMARK,UPPE_LIMIT,LOWER_LIMIT,CRTCLUPLMT,CRTCLLWLMT,SEND_DTTM
			if (((Vector) obxVct.get(i)).get(1).toString().equals("1")) {
				// System.out.println("�ټ�");
				TParm parm = new TParm();
				parm.setData("HOSP_AREA", "HIS"); // 1
				parm.setData("LAB_NUMBER", labNo); // 2
				String rptStts = ((Vector) obxVct.get(i)).get(0).toString();
				// System.out.println("RPT_STTS:" + rptStts);
				parm.setData("RPT_STTS", rptStts); // 4
				// ҽ���
				String orderNo = ((Vector) obxVct.get(i)).get(3).toString()
						.substring(0, lenth); // 5
				parm.setData("ORDER_NO", orderNo);
				// System.out.println("--------orderNo-------------" + orderNo);
				// ҽ�����
				String seqNo = ((Vector) obxVct.get(i)).get(3).toString()
						.substring(
								lenth,
								((Vector) obxVct.get(i)).get(3).toString()
										.length()); // 6
				parm.setData("SEQ_NO", seqNo);
				// System.out.println("--------seqNo-------------" + seqNo);
				// ��Ŀ����
				int lengthCount = StringTool.parseLine(((Vector) obxVct.get(i))
						.get(2).toString(), "^").length;
				String testItemCode = StringTool.parseLine(((Vector) obxVct
						.get(i)).get(2).toString(), "^")[0];
				// System.out.println("TESTITEM_CODE:" + testItemCode);
				parm.setData("TESTITEM_CODE", testItemCode); // 5
				String testItemDesc = "";
				// ��Ŀ����
				if (lengthCount > 1) {
					testItemDesc = StringTool.parseLine(
							((Vector) obxVct.get(i)).get(2).toString(), "^")[1];
					// System.out.println("TESTITEM_DESC:" + testItemDesc);
					parm.setData("TESTITEM_DESC", testItemDesc);
				} else {
					parm.setData("TESTITEM_DESC", "");
				}
				String testItemEnDesc = "";
				// Ӣ������
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
					System.out.println("��Ŀ���Ϊ�գ�");
				}
				// System.out.println("��Ŀ���:" + testValue);
				parm.setData("TEST_VALUE", testValue); // 6
				String testUnit = ((Vector) obxVct.get(i)).get(5).toString();
				// System.out.println("��Ŀ��λ:" + testUnit);
				parm.setData("TEST_UNIT", testUnit); // 7
				String remark = ((Vector) obxVct.get(i)).get(7).toString();
				// System.out.println("�쳣���:" + remark);
				parm.setData("REMARK", "");
				// System.out.println("����:" +
				// StringTool.parseLine(((Vector) obxVct.get(i)).
				// get(6).toString(), "^").length);
				String uppeLimit = "";
				try {
					uppeLimit = StringTool.parseLine(((Vector) obxVct.get(i))
							.get(6).toString(), "^")[1];
					// System.out.println("��ֵ:" + uppeLimit);
				} catch (Exception ex) {
					System.out.println("��ֵ:" + uppeLimit);
				}
				parm.setData("UPPE_LIMIT", uppeLimit);
				String lowerLimit = StringTool.parseLine(((Vector) obxVct
						.get(i)).get(6).toString(), "^")[0];
				// System.out.println("��ֵ:" + lowerLimit);
				parm.setData("LOWER_LIMIT", lowerLimit);
				int length = StringTool.parseLine(((Vector) obxVct.get(i)).get(
						6).toString(), "^").length;
				String crtcluplmt = "";
				if (length == 4) {
					crtcluplmt = StringTool.parseLine(((Vector) obxVct.get(i))
							.get(6).toString(), "^")[3];
				}
				// System.out.println("����:" + crtcluplmt);
				parm.setData("CRTCLUPLMT", crtcluplmt);

				String crtcllwlmt = "";
				if (length == 4) {
					crtcllwlmt = StringTool.parseLine(((Vector) obxVct.get(i))
							.get(6).toString(), "^")[2];
				}
				// System.out.println("����:" + crtcllwlmt);
				parm.setData("CRTCLLWLMT", remark);
				String sendDttm = this.getNow().substring(0, 14);
				// System.out.println("����ʱ��:" + sendDttm);
				parm.setData("SEND_DTTM", sendDttm);
				// // System.out.println("------->>>>>" + i + "�����ݣ�");
				// �����������
				String[] dev = StringTool.parseLine(((Vector) obxVct.get(i))
						.get(10).toString(), "^");
				String devCode = "";
				String devDesc = "";
				try {
					if (dev.length > 0) {
						devCode = dev[0];
					}
					// // System.out.println("������������:" + devCode);
					if (dev.length > 1) {
						devDesc = dev[1];
					}
					// // System.out.println("������������:" + devDesc);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				parm.setData("DEV_CODE", devCode);
				parm.setData("DEV_DESC", devDesc);
				// ���������MED_LIST_PRT��
				falg = LISJdo.getInstance().insertLAB_GENRPTDTLData(parm);
				if (falg.getErrCode() < 0) {
					LISJdo.getInstance().delLAB_GENRPTDTLData(labNo);
					break;
				}
				/**
				 * ״̬��� L:�� H:�� SL:���� SH:���� NM:����
				 */
				if (remark.equals("SL") || remark.equals("SH")) {
					// �����쳣��Ŀ��
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

				// $$============add end by lx 2011-05-17 ������쳣���
				// ���ȡ�쳣ֵ=================��
			}
			// ΢��
			if (((Vector) obxVct.get(i)).get(1).toString().equals("3")) {
				// ΢�� CAT1_TYPE APPLICATION_NO RPDTL_SEQ CULTURE_CODE
				// CULTURE_CHN_DESC ANTI_CODE ANTI_CHN_DESC TEST_VALUE TEST_UNIT
				// REMARK SENS_LEVEL DEV_CODE
				// System.out.println("΢��");
				TParm parm = new TParm();
				parm.setData("HOSP_AREA", "HIS"); // 1
				parm.setData("LAB_NUMBER", labNo); // 2
				String rptStts = ((Vector) obxVct.get(i)).get(0).toString(); // 3
				parm.setData("SAMPLE_NO", ybbh); // 4
				// System.out.println("RPDTL_SEQ:" + rptStts);
				parm.setData("RPDTL_SEQ", rptStts); // 4
				// �����ش���
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
				// ����������
				if (lengthCount > 1) {
					String antiItemDesc = StringTool.parseLine(((Vector) obxVct
							.get(i)).get(4).toString(), "^")[1];
					// System.out.println("ANTI_CHN_DESC:" + antiItemDesc);
					parm.setData("ANTI_CHN_DESC", antiItemDesc);
				} else {
					parm.setData("ANTI_CHN_DESC", "");
				}

				// Ӣ������
				if (lengthCount > 2) {
					String antiItemEnDesc = StringTool.parseLine(
							((Vector) obxVct.get(i)).get(2).toString(), "^")[2];
					// System.out.println("ANTI_ENG_DESC:" + antiItemEnDesc);
					parm.setData("ANTI_ENG_DESC", antiItemEnDesc);
				} else {
					parm.setData("ANTI_ENG_DESC", "");
				}
				parm.setData("TEST_VALUE", ""); // 6 δ��ֵ�ָ�Ϊ��
				parm.setData("TEST_UNIT", ""); // 7 δ��ֵ�ָ�Ϊ��
				parm.setData("REMARK", ""); // 8 δ��ֵ�ָ�Ϊ��
				String sensLevel = "";
				try {
					sensLevel = StringTool.parseLine(((Vector) obxVct.get(i))
							.get(7).toString(), "^")[0];
				} catch (Exception ex) {
					// System.out.println("�����Խ��Ϊ�գ�");
				}
				// System.out.println("sensLevel:" + sensLevel);
				parm.setData("SENS_LEVEL", sensLevel);
				// // System.out.println("------->>>>>" + i + "�����ݣ�");
				// �����������
				// String devCode = ((Vector)obxVct.get(i)).get(13).toString();
				String[] dev = StringTool.parseLine(((Vector) obxVct.get(i))
						.get(10).toString(), "^");
				String devCode = "";
				try {
					if (dev.length > 0) {
						devCode = dev[0];
					}
					// System.out.println("������������:" + devCode);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				parm.setData("DEV_CODE", devCode);
				// ϸ������
				int lengthCountT = StringTool.parseLine(
						((Vector) obxVct.get(i)).get(9).toString(), "^").length;
				String cultItemCode = "";
				try {
					cultItemCode = StringTool.parseLine(
							((Vector) obxVct.get(i)).get(9).toString(), "^")[0];
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// System.out.println("ϸ������:" + cultItemCode);
				parm.setData("CULTURE_CODE", cultItemCode); // 5
				// ϸ������
				if (lengthCountT > 1) {
					String cultItemDesc = StringTool.parseLine(((Vector) obxVct
							.get(i)).get(9).toString(), "^")[1];
					// System.out.println("ϸ������:" + cultItemDesc);
					parm.setData("CULTURE_CHN_DESC", cultItemDesc);
				} else {
					parm.setData("CULTURE_CHN_DESC", "");
				}
				// ���������LAB_ANTISENSTEST��
				falg = LISJdo.getInstance().insertLAB_ANTISENSTESTData(parm);
				if (falg.getErrCode() < 0) {
					LISJdo.getInstance().delLAB_ANTISENSTESTData(labNo, ybbh);
					break;
				}
			}
			// ϸ��
			if (((Vector) obxVct.get(i)).get(1).toString().equals("2")) {
				// System.out.println("ϸ��");
				TParm parm = new TParm();
				parm.setData("HOSP_AREA", "HIS"); // 1
				parm.setData("LAB_NUMBER", labNo); // 2
				parm.setData("SAMPLE_NO", ybbh); //
				String rptStts = ((Vector) obxVct.get(i)).get(0).toString(); // 3
				// System.out.println("RPDTL_SEQ:" + rptStts);
				parm.setData("RPDTL_SEQ", rptStts); // 4
				// ϸ������
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
				// ϸ������
				if (lengthCount > 1) {
					String cultItemDesc = StringTool.parseLine(((Vector) obxVct
							.get(i)).get(4).toString(), "^")[1];
					// System.out.println("CULTURE_CHN_DESC:" + cultItemDesc);
					parm.setData("CULTURE_CHN_DESC", cultItemDesc);
				} else {
					parm.setData("CULTURE_CHN_DESC", "");
				}
				parm.setData("CULTURE_RESULT", ""); // ���������������
				parm.setData("COLONY_COUNT", ""); // ������
				parm.setData("GRAM_STAIN", ""); // ������Ⱦɫ�屨��
				parm.setData("INFECT_LEVEL", ""); // ��Ⱦ�̶�
				parm.setData("REMARK", ""); // ��ע
				String[] dev = StringTool.parseLine(((Vector) obxVct.get(i))
						.get(8).toString(), "^");
				String devCode = "";
				try {
					if (dev.length > 0) {
						devCode = dev[0];
					}
					// System.out.println("������������:" + devCode);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				parm.setData("DEV_CODE", devCode); // ��������
				// ���������LAB_ANTISENSTEST��
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
			System.out.println("�����Ϊ�գ�����");
			falg.setErr(-1, "�����Ϊ�գ�����");
			return falg;
		}
		if (StringUtil.getInstance().isNullString(drUser)) {
			System.out.println("������ԱΪ�գ�����");
			falg.setErr(-1, "������ԱΪ�գ�����");
		}
		if (StringUtil.getInstance().isNullString(shUser)) {
			System.out.println("�����ԱΪ�գ�����");
			falg.setErr(-1, "�����ԱΪ�գ�����");
			return falg;
		}
		if (StringUtil.getInstance().isNullString(admType)) {
			// System.out.println("�ż�ס��Ϊ�գ�����");
			falg.setErr(-1, "�ż�ס��Ϊ�գ�����");
			return falg;
		}
		if (falg.getErrCode() < 0) {
			System.out.println("����д��ʧ�ܣ���");
			msa.setAcknowledgmentCode("CE");
			msa.setLabNo(labNo);
			msa.setMessageStater("SC");
			this.vct.add(msa);
			return falg;
		}
		// �������
		falg = LISJdo.getInstance().upDateBGEND(labNo,
				this.getNow().substring(0, 14), drUser, shUser, admType);
		try {
			msa.setMsh(new MSH());
		} catch (HL7Exception ex) {
			ex.printStackTrace();
		}
		if (falg.getErrCode() < 0) {
			System.out.println("����״̬д��ʧ�ܣ���");
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
	 * �ط��ļ�����
	 * 
	 * @return byte[]
	 */
	public String getReturnValues() {
		return this.bytReturnData;
	}

	/**
	 * LIS���̿���
	 */
	private void ControlLISProsess() throws HL7Exception {
		// System.out.println("parm"+parm);
		// System.out.println("LINE"+this.getBytFileData());
		// MSH:MTYPE:8;
		String masType = this.parm.getValue("MTYPE", 0);
		// MSH:MASID:9;
		String masId = this.parm.getValue("MASID", 0);
		String enter = "" + (char) 13 + (char) 10;
		// ����ǩ��
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
				// System.out.println("���ص�ACK:" + buff.toString());
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
				// System.out.println("���ص�ACK:" + buff.toString());
				String str = new String(buff);
				this.setBytReturnData(lisQS().getErrText());
				this.setReturnCode("-1");

			}
		}
		// �������
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
				// System.out.println("���ص�ACK:" + buff.toString());
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
				// System.out.println("���ص�ACK:" + buff.toString());
				String str = new String(buff);
				this.setBytReturnData(lisDJ().getErrText());
				this.setReturnCode("-1");

			}
		}
		// ������
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
				// System.out.println("���ص�ACK:" + buff.toString());
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
				// System.out.println("���ص�ACK:" + buff.toString());
				String str = new String(buff);
				this.setBytReturnData(lisSHWC().getErrText());
				this.setReturnCode("-1");
			}
		}
		// �������
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
					// System.out.println("���ص�ACK:" + buff.toString());
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
					// System.out.println("���ص�ACK:" + buff.toString());
					String str = new String(buff);
					this.setBytReturnData(lisBGWC().getErrText());
					this.setReturnCode("-1");
				}
			}
		} catch (Exception ex) {
			// System.out.println("����ʧ�ܣ�");
			ex.printStackTrace();
			this.setBytReturnData("����ʧ�ܣ�");
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
