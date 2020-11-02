package jdo.bms.ws;

import java.util.ArrayList;
import java.util.List;
import javax.jws.WebService;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: Ѫ��webService
 * </p>
 * 
 * <p>
 * Description: Ѫ��webService
 * </p>
 * 
 * <p>
 * Copyright: BLUECORE
 * </p>
 * 
 * <p>
 * Company:BLUECORE
 * </p>
 * 
 * @author SHIBL
 * @version 1.0
 */
@WebService
public class BmsServiceImpl implements IBmsService {
	/*
	 * ���Ա�ʶ
	 */
	private boolean IsDebug = true;

	/**
	 * ��Ѫ��������
	 */
	@Override
	public List<String> getBmsApplyData(String ApplyNo) {
		List<String> list = new ArrayList<String>();
		if (ApplyNo.equals("")) {
			list.add("-1");
			list.add("���뵥�Ų���Ϊ��");
			return list;
		}
		if (IsDebug) {
			System.out.println("��Ѫ�����������:" + ApplyNo);
		}
		String sql = "SELECT ADM_TYPE FROM BMS_APPLYM WHERE APPLY_NO='"
				+ ApplyNo + "'";
		TParm applyParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (applyParm.getCount() <= 0) {
			list.add("-1");
			list.add("δ��ѯ�����뵥��Ϣ");
			return list;
		}
		String admType = applyParm.getValue("ADM_TYPE", 0);
		String reline = "";
		try {
			TParm inparm = BmsTool.getInstance().getBmsApplyData(ApplyNo,
					admType);
			if (inparm.getErrCode() < 0) {
				list.add("-1");
				list.add(inparm.getErrText());
				return list;
			}
			XMLUtil xu = new XMLUtil();
			reline = xu.getApplyXMLString(inparm,admType);
		} catch (Exception e) {
			e.printStackTrace();
			list.add("-1");
			list.add("��������xml�쳣ʧ��");
			return list;
		}
		list.add("0");
		list.add(reline);
		return list;
	}

	/**
	 * ��Ѫ��Ӧ����
	 */
	@Override
	public List<String> getBmsTranReacData(String ApplyNo) {
		List<String> list = new ArrayList<String>();
		if (ApplyNo.equals("")) {
			list.add("-1");
			list.add("��Ӧ���Ų���Ϊ��");
			return list;
		}
		if (IsDebug) {
			System.out.println("��Ѫ��Ӧ�������:" + ApplyNo);
		}
		String reline = "";
		try {
			TParm inparm = BmsTool.getInstance().getBmsReacData(ApplyNo);
			if (inparm.getErrCode() < 0) {
				list.add("-1");
				list.add(inparm.getErrText());
				return list;
			}
			XMLUtil xu = new XMLUtil();
			reline = xu.getReactXmlString(inparm);
		} catch (Exception e) {
			e.printStackTrace();
			list.add("-1");
			list.add("���ɷ�Ӧxml�쳣ʧ��");
			return list;
		}
		list.add("0");
		list.add(reline);
		return list;
	}

	/**
	 * Ѫ���շ�
	 */
	@Override
	public List<String> onBmsFee(String XmlData) {
		List<String> list = new ArrayList<String>();
		if (XmlData.equals("")) {
			list.add("-1");
			list.add("xml�ַ�������Ϊ��");
			return list;
		}
		if (IsDebug) {
			System.out.println("Ѫ���շ��������:" + XmlData);
		}
		XMLUtil xu = new XMLUtil();
		TParm parm = new TParm();
		try {
			parm = xu.getFeeParserXml(XmlData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			list.add("-1");
			list.add("�����Ʒ�xml�ַ����쳣");
			return list;
		}
		String applyNo = parm.getValue("ApplyNo");
		String sql = "SELECT ADM_TYPE FROM BMS_APPLYM WHERE APPLY_NO='"
				+ applyNo + "'";
		TParm applyParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (applyParm.getCount() <= 0) {
			list.add("-1");
			list.add("δ��ѯ�����뵥��Ϣ");
			return list;
		}
		try {
			if (applyParm.getValue("ADM_TYPE", 0).equals("I")) {
				TParm result = BmsFeeTool.getInstance().onBmsIFee(parm);
				if (result.getErrCode() < 0) {
					list.add("-1");
					list.add(result.getErrText());
					return list;
				}
			} else {
				TParm result = BmsFeeTool.getInstance().onBmsOEFee(parm);
				if (result.getErrCode() < 0) {
					list.add("-1");
					list.add(result.getErrText());
					return list;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			list.add("-1");
			list.add("Ѫ��ƷѲ����쳣");
			return list;
		}
		list.add("0");
		list.add("�ɹ�");
		return list;
	}

	/**
	 * ѪƷ����
	 */
	@Override
	public List<String> onBmsOutBound(String XmlData) {
		List<String> list = new ArrayList<String>();
		if (XmlData.equals("")) {
			list.add("-1");
			list.add("xml�ַ�������Ϊ��");
			return list;
		}
		if (IsDebug) {
			System.out.println("ѪƷ�����������:" + XmlData);
		}
		XMLUtil xu = new XMLUtil();
		TParm parm = new TParm();
		try {
			parm = xu.getBloodParserXml(XmlData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			list.add("-1");
			list.add("��������xml�ַ����쳣");
			return list;
		}
		String applyNo = parm.getValue("ApplyNo");
		String sql = "SELECT ADM_TYPE FROM BMS_APPLYM WHERE APPLY_NO='"
				+ applyNo + "'";
		TParm applyParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (applyParm.getCount() <= 0) {
			list.add("-1");
			list.add("δ��ѯ�����뵥��Ϣ");
			return list;
		}
		String admType = applyParm.getValue("ADM_TYPE", 0);
		TParm result = new TParm();
		try {
			result = BmsTool.getInstance().onBmsOut(parm, admType);
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, "Ѫ���������쳣");
		}
		if (result.getErrCode() < 0) {
			list.add("-1");
			list.add(result.getErrText());
		}
		list.add("0");
		list.add("�ɹ�");
		return list;
	}

	/**
	 * Ѫ�ͼ���
	 */
	@Override
	public List<String> onBmsConfirmData(String XmlData) {
		List<String> list = new ArrayList<String>();
		if (XmlData.equals("")) {
			list.add("-1");
			list.add("xml�ַ�������Ϊ��");
			return list;
		}
		if (IsDebug) {
			System.out.println("Ѫ�ͼ����������:" + XmlData);
		}
		XMLUtil xu = new XMLUtil();
		TParm parm = new TParm();
		try {
			parm = xu.getConfirmParserXml(XmlData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			list.add("-1");
			list.add("����Ѫ�ͼ���xml�ַ����쳣");
			return list;
		}
		if (!parm.getValue("PBloodType").equals("")
				&& !(parm.getValue("PBloodType").toUpperCase().equals("NULL"))) {
			TParm patParm = new TParm();
			patParm.setData("MR_NO", parm.getValue("MrNo"));
			patParm.setData("BLOOD_TYPE", parm.getValue("PBloodType"));
			patParm.setData("BLOOD_RH_TYPE", parm.getValue("PRhType"));
			TParm reParm = new TParm();
			;
			try {
				reParm = new TParm(TJDODBTool.getInstance().update(
						BmsTool.getInstance().onUpdatePatSql(patParm)));
			} catch (Exception e) {
				e.printStackTrace();
				reParm.setErr(-1, "���²���Ѫ�Ͳ����쳣");
			}
			if (reParm.getErrCode() < 0) {
				list.add("-1");
				list.add("���²���Ѫ�ʹ���");
				return list;
			}
		}
		list.add("0");
		list.add("�ɹ�");
		return list;
	}

	/**
	 * ѪƷ����
	 * 
	 * @param XmlData
	 * @return
	 */
	@Override
	public List<String> onBmsRefund(String XmlData) {
		List<String> list = new ArrayList<String>();
		if (XmlData.equals("")) {
			list.add("-1");
			list.add("xml�ַ�������Ϊ��");
			return list;
		}
		if (IsDebug) {
			System.out.println("ѪƷ�����������:" + XmlData);
		}
		XMLUtil xu = new XMLUtil();
		TParm parm = new TParm();
		try {
			parm = xu.getRefundParserXml(XmlData);
		} catch (Exception e) {
			e.printStackTrace();
			list.add("-1");
			list.add("����ѪƷ����xml�ַ����쳣");
			return list;
		}
		TParm result = new TParm();
		try {
			result = BmsTool.getInstance().onDelOutBlood(parm);
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, "Ѫ���������쳣");
		}
		if (result.getErrCode() < 0) {
			list.add("-1");
			list.add(result.getErrText());
			return list;
		}
		list.add("0");
		list.add("�ɹ�");
		return list;
	}

	/**
	 * ��˱�Ѫ����
	 */
	@Override
	public List<String> onBmsApplyCheckData(String XmlData) {

		return null;
	}

	/**
	 * �����Ѫ��Ӧ
	 */
	@Override
	public List<String> onBmsTranReacCheckData(String XmlData) {

		return null;
	}

}