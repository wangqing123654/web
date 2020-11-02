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
 * Title: RIS�ӿڷ���
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
	// HL7�ļ�����
	private String bytFileData;
	// BPEL���ò�������
	private TParm parm;
	// ���ر���
	private String returnCode;
	// ���TParm
	private TParm reParm;
	// ��������
	private String bytReturnData;
	// ʱ��
	public static DateFormat dateFormat = new SimpleDateFormat(
			"yyyyMMddhhmmssSSS");
	// �������
	private Vector vct;

	/**
	 * �õ�RIS�ط��ļ�ȫ������
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
	 * ԤԼ
	 * 
	 * @return boolean
	 */
	public TParm risYY() throws HL7Exception {
		TParm reParm = new TParm();
		// �����
		Vector vctApplyNo = (Vector) this.parm.getData("RISAPPNO");
		// RIS����
		String risNo = this.parm.getValue("RISNO", 0);
		// ԤԼʱ��
		String yyDate = this.parm.getValue("YYDATE", 0);
		this.vct = new Vector();
		int rowCount = vctApplyNo.size();
		for (int i = 0; i < rowCount; i++) {
			MSA msa = new MSA();
			if (StringUtil.getInstance().isNullString(
					vctApplyNo.get(i).toString())) {
				// // System.out.println("�����Ϊ�գ���");
				reParm.setErr(-1, "�����Ϊ�գ���");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			TParm statusParm = Hl7Tool.getInstance().getOrder(
					vctApplyNo.get(i).toString(), "RIS","");
			if (statusParm.getCount() <= 0) {
				reParm.setErr(-1, "û���ҵ���Ӧҽ������");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			int status = statusParm.getInt("STATUS", 0);
			if (status >= 4) {
				reParm.setErr(-1, "ҽ����" + Status.getStatusMap().get(status)
						+ ",����ԤԼ");
			}
			if (StringUtil.getInstance().isNullString(yyDate)) {
				// // System.out.println("ԤԼʱ��Ϊ�գ���");
				reParm.setErr(-1, "ԤԼʱ��Ϊ�գ���");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			reParm = RISJdo.getInstance().upDateRisYY(
					vctApplyNo.get(i).toString(), "2", yyDate);
			if (reParm.getErrCode() < 0) {
				reParm.setErr(-1, "ԤԼ����ʧ��");
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
	 * ����
	 * 
	 * @return boolean
	 */
	public TParm risDJ() throws HL7Exception {
		TParm reParm = new TParm();
		// �����
		Vector vctApplyNo = (Vector) this.parm.getData("LABNO");
		String RUType = this.parm.getValue("INMEUF", 0);
		// ����
		String typeDJ = this.parm.getValue("LISSTR", 0);
//		// ԤԼʱ��(��������ȡԤԼʱ��)
		String yyDate = this.parm.getValue("YYDATE", 0);
		// ����ʱ��
		String djDate = this.parm.getValue("DJDATE", 0);
//		// (��������ȡԤԼʱ��)
		if (RUType.equals("ULT") && !yyDate.equals("")) {
			djDate = yyDate;
		}
		// �ż�ס�� 20120229 shibl modify
		String admType = this.parm.getValue("OEIH", 0);
		// ORDERNO SEQNO
		String orderNoseqNo = this.parm.getValue("ORDERNOSEQ", 0);
		// �����Ա
		String jcRisUser = this.parm.getValue("JCRISUSER", 0);
		// �豸ID
		String devCode = "";
		String devDesc = "";
		// ״̬
		String status = "2";
		this.vct = new Vector();
		int rowCount = vctApplyNo.size();
		// ����
		if ("IP".equals(typeDJ)) {
			status = "4";
		}
		// ȡ������
		if ("CA".equals(typeDJ)) {
			status = "5";
			jcRisUser = "";
		}
		// ������
		if ("CM".equals(typeDJ)) {
			String[] dev = StringTool.parseLine(this.parm
					.getValue("EXECDEV", 0), "^");
			try {
				// �豸ID
				if (dev.length > 0) {
					devCode = dev[0];
				}
				// �豸����
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
					// System.out.println("����Ϊ�գ�����");
					reParm.setErr(-1, "����Ϊ�գ�����");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctApplyNo.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				if (StringUtil.getInstance().isNullString(jcRisUser)) {
					// System.out.println("�����ԱΪ�գ�����");
					jcRisUser = "δ֪";
				}
				reParm = RISJdo.getInstance().upDateRisWCJC(
						vctApplyNo.get(i).toString(), status, djDate,
						jcRisUser, devCode, devDesc);
				if (reParm.getErrCode() < 0) {
					reParm.setErr(-1, "�������ʧ��");
					return reParm;
				}
			} else {
				if (StringUtil.getInstance().isNullString(
						vctApplyNo.get(i).toString())) {
					// System.out.println("����Ϊ�գ�����");
					reParm.setErr(-1, "����Ϊ�գ�����");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctApplyNo.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				if (StringUtil.getInstance().isNullString(admType)) {
					// System.out.println("�ż���Ϊ�գ�����");
					reParm.setErr(-1, "�ż���Ϊ�գ�����");
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
	 * ȡ��ԤԼ
	 * 
	 * @return boolean
	 */
	public TParm risCanYY()throws HL7Exception {
		TParm falg = new TParm();
		// �����
		Vector vctApplyNo = (Vector) this.parm.getData("RISAPPNO");
		// RIS����
		String risNo = this.parm.getValue("RISNO", 0);
		// ԤԼʱ��
		String yyDate = this.parm.getValue("YYDATE", 0);
		this.vct = new Vector();
		int rowCount = vctApplyNo.size();
		for (int i = 0; i < rowCount; i++) {
			MSA msa = new MSA();
			if (StringUtil.getInstance().isNullString(
					vctApplyNo.get(i).toString())) {
				// System.out.println("�����Ϊ�գ���");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			TParm statusParm = Hl7Tool.getInstance().getOrder(
					vctApplyNo.get(i).toString(), "RIS","");
			if (statusParm.getCount() <= 0) {
				reParm.setErr(-1, "û���ҵ���Ӧҽ������");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			int status = statusParm.getInt("STATUS", 0);
			if (status != Status.YYS) {
				reParm.setErr(-1, "ҽ����" + Status.getStatusMap().get(status)
						+ ",����ȡ��ԤԼ");
			}
			falg = RISJdo.getInstance().upDateRisYY(
					vctApplyNo.get(i).toString(), "3", yyDate);
			if (falg.getErrCode() < 0) {
				falg.setErr(-1, "ȡ��ԤԼ����ʧ��");
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
	 * ���
	 * 
	 * @return boolean
	 */
	public TParm risSHWC() {
		return new TParm();
	}

	/**
	 * ����
	 * 
	 * @return boolean
	 */
	public TParm risBGWC() throws HL7Exception{
		TParm falg = new TParm();
		// �ż�ס��
		String admType = this.parm.getValue("OEIH", 0);
		// ����
		String inMuf = this.parm.getValue("INMEUF", 0);
		// �����
		Vector vctApplyNo = (Vector) this.parm.getData("ORDERNO");
		// ����
		String typeDJ = this.parm.getValue("STUTSBG", 0);
		// ����ʱ��
		String timeDate[] = StringTool.parseLine(this.parm.getValue(
				"RISBGDATE", 0), "~");
		// ����ҽʦ
		String temp[] = StringTool.parseLine(this.parm.getValue("SHBGUSER", 0),
				"~");
		// ��������(����)
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
		// ���ҽʦ
		String shDr = "";
		String shDate = "";
		if (temp.length >= 2) {
			String dr[]=StringTool.parseLine(temp[1], "^");
			shDr = (dr.length)>1?dr[1]:"";
		}
		if (timeDate.length >= 2) {
			shDate = timeDate[1];
		}
		// ����ҳ��
		List<String> bgSeq = this.getTXBGNo();
		// ͼ���ַ
		List<String> imageUrl = getTXimageUrl();
		// pdf����·��
		List<String> pdfPath = this.getPdfPath();
		// ���ӡ��
		List<String> zdyx = getCEData();
		// �������
		List<String> zdsj = getTXData();
		// ������
		List<String> type = getYYType();
		// ״̬
		String status = "6";
		this.vct = new Vector();
		int rowCount = vctApplyNo.size();
		// �������
		if ("F".equals(typeDJ)) {
			status = "7";
		}
		// ȡ������
		if ("D".equals(typeDJ)) {
			status = "10";
		}
		for (int i = 0; i < rowCount; i++) {
			MSA msa = new MSA();
			if (StringUtil.getInstance().isNullString(
					vctApplyNo.get(i).toString())) {
				System.out.println("����Ϊ�գ�����");
				falg.setErr(-1, "����Ϊ�գ�����");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			if (StringUtil.getInstance().isNullString(typeDJ)) {
				System.out.println("����״̬Ϊ�գ�����");
				falg.setErr(-1, "����״̬Ϊ�գ�����");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			if (StringUtil.getInstance().isNullString(inMuf)) {
				System.out.println("����Ϊ�գ�����");
				falg.setErr(-1, "����Ϊ�գ�����");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			if (StringUtil.getInstance().isNullString(admType)) {
				System.out.println("�ż�ס��Ϊ�գ�����");
				falg.setErr(-1, "�ż�ס��Ϊ�գ�����");
				msa.setAcknowledgmentCode("CE");
				msa.setLabNo(vctApplyNo.get(i).toString());
				msa.setMessageStater("SC");
				this.vct.add(msa);
				break;
			}
			if ("F".equals(typeDJ)) {
				if (StringUtil.getInstance().isNullString(shDr)) {
					System.out.println("���ҽʦΪ�գ�����");
					falg.setErr(-1, "���ҽʦΪ�գ�����");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctApplyNo.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
				if (StringUtil.getInstance().isNullString(bgDr)) {
					System.out.println("����ҽʦΪ�գ�����");
					falg.setErr(-1, "����ҽʦΪ�գ�����");
					msa.setAcknowledgmentCode("CE");
					msa.setLabNo(vctApplyNo.get(i).toString());
					msa.setMessageStater("SC");
					this.vct.add(msa);
					break;
				}
			}
			// ȷ�������ݴ������ݿ���
			TParm dataParm = PDFJdo.getInstance().QueryMedapply(
					vctApplyNo.get(i).toString(), "RIS");
			if (dataParm.getCount() > 0) {
				falg = RISJdo.getInstance().upDateRisSHBG(
						vctApplyNo.get(i).toString(), status, djDate, shDate,
						shDr, bgDr, typeDJ, zdyx, zdsj, type, inMuf, chNo,
						bgSeq, imageUrl, pdfPath, admType);
				if (falg.getErrCode() < 0) {
					falg.setErr(-1, "�������ʧ��");
					return falg;
				}
			} else {
				System.out.println("δ��Medapply�в�ѯ������ҽ����");
				falg.setErr(-1, "δ��ѯ������ҽ��");
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
	 * �õ����ӡ��
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
	 * �õ��������
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
	 * �õ�����ҳ��
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
	 * �õ�ͼ���ַ
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
	 * �õ�pdfPath����·��
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
	 * ������
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
	 * ��õ�ǰʱ��
	 * 
	 * @return String
	 */
	public String getNow() {
		return dateFormat.format(new Date());
	}

	/**
	 * LIS���̿���
	 */
	private void ControlLISProsess() throws HL7Exception {
		// �õ�IN����
		String inMuefStr = this.parm.getValue("INMEUF", 0);
		// �õ�OUT����
		String outMuefStr = this.parm.getValue("OUTMEUF", 0);
		// ��Ϣ����
		String masType = this.parm.getValue("MTYPE", 0);
		String masId = this.parm.getValue("MASID", 0);
		String enter = "" + (char) 13 + (char) 10;
		// ԤԼ
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
				// System.out.println("���ص�ACK:" + buff.toString());
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
				// System.out.println("���ص�ACK:" + buff.toString());
				String str = new String(buff);
				this.setBytReturnData(risYY().getErrText());
				this.setReturnCode("-1");
			}
		}
		// ȡ��ԤԼ
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
				// System.out.println("���ص�ACK:" + buff.toString());
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
				// System.out.println("���ص�ACK:" + buff.toString());
				String str = new String(buff);
				this.setBytReturnData(risCanYY().getErrText());
				this.setReturnCode("-1");
			}
		}
		// ���졢ȡ�����졢������
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
				// System.out.println("���ص�ACK:" + buff.toString());
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
				// System.out.println("���ص�ACK:" + buff.toString());
				String str = new String(buff);
				this.setBytReturnData(risDJ().getErrText());
				this.setReturnCode("-1");
			}
		}
		// �������
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
				// System.out.println("���ص�ACK:" + buff.toString());
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
				// System.out.println("���ص�ACK:" + buff.toString());
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
	 * ���ò���
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
