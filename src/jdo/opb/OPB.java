package jdo.opb;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOObject;
import jdo.sys.Pat;
import jdo.reg.Reg;
import jdo.ins.INSIbsTool;
import jdo.opd.PrescriptionList;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.OdoUtil;
import jdo.opd.OrderList;
import jdo.opd.Order;
import jdo.opd.OrderTool;

import java.util.ArrayList;
import java.util.Map;
import jdo.bil.BilInvoice;
import jdo.bil.BilInvrcpt;
import jdo.sys.Operator;
import java.util.List;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import java.util.Vector;
import jdo.bil.BIL;
import com.dongyang.util.StringTool;
import jdo.pha.PhaSysParmTool;
import com.dongyang.data.TNull;

/**
 * 
 * <p>
 * Title: �����շ�
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
 * Company: JavaHis
 * </p>
 * 
 * @author lzk 2008.09.26
 * @version 1.0
 */
public class OPB extends TJDOObject {
	// ���
	private String recpType = "OPB";
	private String admType = "O";
	/**
	 * ���߶���
	 */
	private Pat pat;
	/**
	 * �ҺŶ���
	 */
	private Reg reg;
	/**
	 * ҽ���������
	 */
	private OdoUtil odoUtil;
	/**
	 * Ʊ��list
	 */
	private OPBReceiptList receiptList;

	/**
	 * ����ǩ
	 */
	private PrescriptionList prescriptionList;
	/**
	 * �������
	 */
	private Map chargeMap;
	/**
	 * �շ���ϸ����
	 */
	private TParm chargeParm;
	/**
	 * Ʊ�ݴ�ӡ����
	 */
	private BilInvoice bilInvoice;
	/**
	 * ������ǩ
	 */
	private TParm prescription;
	/**
	 * Ʊ�ݴ�ӡ��ϸ
	 */
	private BilInvrcpt bilInvrcpt;
	/**
	 * ���񷽷�����
	 */
	BIL bil = new BIL();
	/**
	 * ����A�_�z���̎�����ü����\̖
	 * yanjing 20131217
	 */
	private List<String> preCaseNos= new ArrayList(); 
//	private Map<String, String> preCaseNos;

	/**
	 * ��ʼ��
	 */
	public OPB() {
		// ��ʼ������ǩ
		setPrescriptionList(new PrescriptionList());
	}

	/**
	 * ���û��߶���
	 * 
	 * @param pat
	 *            Pat
	 */
	public void setPat(Pat pat) {
		this.pat = pat;
	}

	/**
	 * �õ����߶���
	 * 
	 * @return Pat
	 */
	public Pat getPat() {
		return pat;
	}

	/**
	 * ���ùҺŶ���
	 * 
	 * @param reg
	 *            Reg
	 */
	public void setReg(Reg reg) {
		this.reg = reg;
	}

	/**
	 * �õ��ҺŶ���
	 * 
	 * @return Reg
	 */
	public Reg getReg() {
		return reg;
	}

	/**
	 * ����ҽ������
	 * 
	 * @param odoUtil
	 *            OdoUtil
	 */
	public void setOdoUtil(OdoUtil odoUtil) {
		this.odoUtil = odoUtil;
	}

	/**
	 * �õ�ҽ������
	 * 
	 * @return OdoUtil
	 */
	public OdoUtil getOdoUtil() {
		return odoUtil;
	}

	/**
	 * ����Ʊ��List
	 * 
	 * @param opbReceiptList
	 *            OPBReceiptList
	 */
	public void setReceiptList(OPBReceiptList opbReceiptList) {
		this.receiptList = opbReceiptList;
	}

	/**
	 * �õ�Ʊ��List
	 * 
	 * @return OPBReceiptList
	 */
	public OPBReceiptList getReceiptList() {
		return receiptList;
	}

	/**
	 * ���ô���ǩ
	 * 
	 * @param prescriptionList
	 *            PrescriptionList
	 */
	public void setPrescriptionList(PrescriptionList prescriptionList) {
		this.prescriptionList = prescriptionList;
	}

	/**
	 * �õ�����ǩ
	 * 
	 * @return PrescriptionList
	 */
	public PrescriptionList getPrescriptionList() {
		return prescriptionList;
	}

	/**
	 * �����շ���ϸmap
	 * 
	 * @param chargeMap
	 *            Map
	 */
	public void setChargeMap(Map chargeMap) {
		this.chargeMap = chargeMap;
	}

	/**
	 * �õ��շ���ϸmap
	 * 
	 * @return Map
	 */
	public Map getChargeMap() {
		return this.chargeMap;
	}

	/**
	 * �����շ�
	 * 
	 * @param chargeParm
	 *            TParm
	 */
	public void setChargeParm(TParm chargeParm) {
		this.chargeParm = chargeParm;
	}

	/**
	 * �õ��շ�
	 * 
	 * @return TParm
	 */
	public TParm getChargeParm() {
		return this.chargeParm;
	}

	/**
	 * ����Ʊ�ݴ�ӡ����
	 * 
	 * @param bilInvoice
	 *            BilInvoice
	 */
	public void setBilInvoice(BilInvoice bilInvoice) {
		this.bilInvoice = bilInvoice;
	}

	/**
	 * �õ�Ʊ�ݴ�ӡ����
	 * 
	 * @return BilInvoice
	 */
	public BilInvoice getBilInvoice() {
		return this.bilInvoice;
	}

	/**
	 * ���ô�ӡƱ����ϸ
	 * 
	 * @param bilInvrcpt
	 *            BilInvrcpt
	 */
	public void setBilInvrcpt(BilInvrcpt bilInvrcpt) {
		this.bilInvrcpt = bilInvrcpt;
	}

	/**
	 * �õ���ӡƱ����ϸ
	 * 
	 * @return BilInvrcpt
	 */
	public BilInvrcpt getBilinvrcpt() {
		return this.bilInvrcpt;
	}

	/**
	 * ��ʼ������
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean �棺�ɹ����٣�ʧ��
	 */
	public boolean initParm(TParm parm) {
		if (parm == null)
			return false;
		if (!initPrescription(parm.getParm("ORDER")))
			return false;
		return true;
	}

	/**
	 * ��ʼ������
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean true:�ɹ���false:ʧ��
	 */
	public boolean initPrescription(TParm parm) {
		if (parm == null)
			return false;
		return getPrescriptionList().initParm(parm);
	}

	/**
	 * �������
	 * 
	 * @return TParm
	 */
	public TParm checkData() {
		TParm result = new TParm();
		return result;
	}

	/**
	 * �õ�ȫ������
	 * 
	 * @return TParm
	 */
	public TParm getParm() {
		TParm result = new TParm();
		TParm modifyOrderParm = this.getPrescriptionList().getParm();
		modifyOrderParm.getParm(OrderList.MODIFIED);
		// ����ҽ������
		result.setData("ORDER", this.getPrescriptionList().getParm().getData());
		// �õ�Ʊ��
		TParm newOpbReceipt = receiptList.getParm(receiptList.PRIMARY);
		// ����Ʊ��
		result.setData("RECEIPT", newOpbReceipt.getData());
		// �õ���ǰʹ�õ�Ʊ��
		TParm newBilInvoice = bilInvoice.getParm();
		result.setData("BILINVOICE", newBilInvoice.getData());
		// �õ���ӡ��Ʊ��
		TParm newBilInvrcpt = bilInvrcpt.getParm();
		result.setData("BILINVRCPT", newBilInvrcpt.getData());
		return result;
	}

	/**
	 * ���ݲ����Ų�ѯOPB
	 * 
	 * @param mrno
	 *            String
	 * @return OPB
	 */
	public boolean onQueryByMrNo(String mrno) {
		// ��ʼ��pat
		pat = pat.onQueryByMrNo(mrno);
		if (pat == null)
			return false;
		setPat(pat);
		return true;
	}

	/**
	 * ���ݴ���ǩ�Ų�ѯOPB
	 * 
	 * @param rxNo
	 *            String
	 * @return OPB
	 */
	public static OPB onQueryByRxNo(String rxNo) {
		return null;
	}

	/**
	 * ����Ʊ�ݶ���
	 * 
	 * @return OPBReceipt
	 */
	public OPBReceipt newReceipt() {
		OPBReceipt newReceipt = receiptList.newReceipt();
		return newReceipt;
	}

	/**
	 * ���ݴ����TParm ����Ʊ��List���� ���ݴ�������������Զ�����
	 * 
	 * @param parm
	 *            TParm
	 */
	public void InitOpbReceiptList(TParm parm) {
		if (parm.getErrCode() < 0) {
			err(parm.getErrCode() + " " + parm.getErrText());
			return;
		}
		receiptList.InitOpbReceiptList(parm);
	}

	/**
	 * ɾ��һ��Ʊ��
	 * 
	 * @param receipt
	 *            OPBReceipt
	 */
	public void deleteReceipt(OPBReceipt receipt) {
		if (this.receiptList == null)
			return;

	}

	/**
	 * �����շ����
	 */
	public void initReceiptCharge() {
		// System.out.println("ssssssssssss");
		this.setChargeMap(bil.getChargeToRexpCodeMap(admType));
		// TParm parm = new TParm();
		// parm.setData("ADM_TYPE", "O");
		// TParm result = BILRecpParmTool.getInstance().selectcharge(parm);
		// if (result.getErrCode() < 0) {
		// err(result.getErrCode() + " " + result.getErrText());
		// return;
		// }
		// // System.out.println("result>>>>>>>>>>>>>>>>"+result);
		// Map map =
		// map.put(result.getValue("CHARGE01", 0), "CHARGE01");
		// map.put(result.getValue("CHARGE02", 0), "CHARGE02");
		// map.put(result.getValue("CHARGE03", 0), "CHARGE03");
		// map.put(result.getValue("CHARGE04", 0), "CHARGE04");
		// map.put(result.getValue("CHARGE05", 0), "CHARGE05");
		// map.put(result.getValue("CHARGE06", 0), "CHARGE06");
		// map.put(result.getValue("CHARGE07", 0), "CHARGE07");
		// map.put(result.getValue("CHARGE08", 0), "CHARGE08");
		// map.put(result.getValue("CHARGE09", 0), "CHARGE09");
		// map.put(result.getValue("CHARGE10", 0), "CHARGE10");
		// map.put(result.getValue("CHARGE11", 0), "CHARGE11");
		// map.put(result.getValue("CHARGE12", 0), "CHARGE12");
		// map.put(result.getValue("CHARGE13", 0), "CHARGE13");
		// map.put(result.getValue("CHARGE14", 0), "CHARGE14");
		// map.put(result.getValue("CHARGE15", 0), "CHARGE15");
		// map.put(result.getValue("CHARGE16", 0), "CHARGE16");
		// map.put(result.getValue("CHARGE17", 0), "CHARGE17");
		// map.put(result.getValue("CHARGE18", 0), "CHARGE18");
		// map.put(result.getValue("CHARGE19", 0), "CHARGE19");
		// map.put(result.getValue("CHARGE20", 0), "CHARGE20");
		// map.put(result.getValue("CHARGE21", 0), "CHARGE21");
		// map.put(result.getValue("CHARGE22", 0), "CHARGE22");
		// map.put(result.getValue("CHARGE23", 0), "CHARGE23");
		// map.put(result.getValue("CHARGE24", 0), "CHARGE24");
		// map.put(result.getValue("CHARGE25", 0), "CHARGE25");
		// map.put(result.getValue("CHARGE26", 0), "CHARGE26");
		// map.put(result.getValue("CHARGE27", 0), "CHARGE27");
		// map.put(result.getValue("CHARGE28", 0), "CHARGE28");
		// map.put(result.getValue("CHARGE29", 0), "CHARGE29");
		// map.put(result.getValue("CHARGE30", 0), "CHARGE30");
		// //�������
		// this.setChargeMap(map);
		return;
	}

	/**
	 * �����շѽ��
	 * 
	 * @return TParm
	 */
	public TParm chargeParm() {
		// TParm chargeParm = new TParm();
		// chargeParm.setData("CHARGE01", 0);
		// chargeParm.setData("CHARGE02", 0);
		// chargeParm.setData("CHARGE03", 0);
		// chargeParm.setData("CHARGE04", 0);
		// chargeParm.setData("CHARGE05", 0);
		// chargeParm.setData("CHARGE06", 0);
		// chargeParm.setData("CHARGE07", 0);
		// chargeParm.setData("CHARGE08", 0);
		// chargeParm.setData("CHARGE09", 0);
		// chargeParm.setData("CHARGE10", 0);
		// chargeParm.setData("CHARGE11", 0);
		// chargeParm.setData("CHARGE12", 0);
		// chargeParm.setData("CHARGE13", 0);
		// chargeParm.setData("CHARGE14", 0);
		// chargeParm.setData("CHARGE15", 0);
		// chargeParm.setData("CHARGE16", 0);
		// chargeParm.setData("CHARGE17", 0);
		// chargeParm.setData("CHARGE18", 0);
		// chargeParm.setData("CHARGE19", 0);
		// chargeParm.setData("CHARGE20", 0);
		// chargeParm.setData("CHARGE21", 0);
		// chargeParm.setData("CHARGE22", 0);
		// chargeParm.setData("CHARGE23", 0);
		// chargeParm.setData("CHARGE24", 0);
		// chargeParm.setData("CHARGE25", 0);
		// chargeParm.setData("CHARGE26", 0);
		// chargeParm.setData("CHARGE27", 0);
		// chargeParm.setData("CHARGE28", 0);
		// chargeParm.setData("CHARGE29", 0);
		// chargeParm.setData("CHARGE30", 0);
		return bil.getChargeParm();
	}

	/**
	 * ��ʼ����ǰʹ�õ�Ʊ��
	 * 
	 * @return boolean
	 */
	public boolean initBilInvoice() {
		bilInvoice = new BilInvoice().initBilInvoice("OPB");
		return true;
	}

	/**
	 * ��ʼ����ӡƱ��
	 * 
	 * @return boolean
	 */
	public boolean initBilInvrcpt() {
		bilInvrcpt = new BilInvrcpt();
		bilInvrcpt.setCashierCode(Operator.getID());
		bilInvrcpt.setRecpType(recpType);
		return true;
	}

	/**
	 * ����OPB����
	 * 
	 * @param reg
	 *            Reg
	 * @return OPB
	 */
	public static OPB onQueryByCaseNo(Reg reg) {
		if (reg == null)
			return null;
		if (reg.caseNo() == null || reg.caseNo().length() == 0)
			return null;
		OPB opb = new OPB();
		// ����pat
		opb.setPat(reg.getPat());
		// ����reg
		opb.setReg(reg);
		// ����reg
		opb.onQuery(reg.caseNo());
		// �½�Ʊ��list
		OPBReceiptList receiptList = new OPBReceiptList();
		// Ʊ�������opb
		receiptList.setReg(reg);
		receiptList.setPat(reg.getPat());
		// ����Ʊ��list
		opb.setReceiptList(receiptList);
		// ��ʼ���շ���Ŀmap
		opb.initReceiptCharge();
		// //��ʼ�������շѷ���
		// opb.chargeParm();
		// ��ʼ����ǰʹ�õ�Ʊ����
		opb.initBilInvoice();
		// ��ʼ��Ҫ��ӡ��һ��Ʊ����ϸ
		opb.initBilInvrcpt();
		// ������ǩ����ҺŶ���
		opb.getPrescriptionList().setReg(reg);
		return opb;
	}

	/**
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("OPB->\n");
		sb.append("  PrescriptionList:");
		sb.append(getPrescriptionList());
		return sb.toString();
	}

	/**
	 * ��ѯ
	 * 
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	public boolean onQuery(String caseNo) {
		// ���pat
		if (getPat() == null)
			return false;
		// ���casno
		if (getPat().getMrNo() == null || getPat().getMrNo().length() == 0)
			return false;
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", getPat().getMrNo());
		// ��ѯ���е�order
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onQuery", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		// ��ʼ������ǩ
		if (!initParm(result)) {
			err("��������ʧ��");
			return false;
		}
		return true;
	}

	/**
	 * �շѱ��ȫѡ
	 * 
	 * @param boo
	 *            boolean
	 * @param rxNo
	 *            String
	 */
	public void chargeTAll(boolean boo, String rxNo) {
		// ÿ�μ�����ö�Ҫ���³�ʼ���շ�parm
		TParm chargeParm = chargeParm();
		// �����õ���ʾorder������
		//JJJ
		TParm orderParm = getOrderParm(boo, rxNo, false, false, false,"");
		int count = orderParm.getCount();
		for (int i = 0; i < count; i++) {
			// ȡһ��
			Order order = (Order) orderParm.getData("OBJECT", i);
			// ����˷ѱ��true
			if (order.getChargeFlg()) {
				// �շ����
				String rexpCode = order.getRexpCode();
				// ͨ���շ����õ�CHARGE
				String charge = chargeMap.get(rexpCode).toString();
				// �õ�parm�е��Ͻ��
				double oldTotAmt = chargeParm.getDouble(charge);
				// �����½��
				double totAmt = oldTotAmt - order.getArAmt();
				// �����շѽ��
				chargeParm.setData(charge, totAmt);
			}
		}
		setChargeParm(chargeParm);
	}

	/**
	 * �շѱ��ȫѡ
	 * 
	 * @param boo
	 *            boolean
	 * @param rxNo
	 *            String
	 */
	public void chargeAll(boolean boo, String rxNo) {
		// ÿ�μ�����ö�Ҫ���³�ʼ���շ�parm
		TParm chargeParm = chargeParm();
		// �����õ���ʾorder������
		//JJJ
		TParm orderParm = getOrderParm(boo, rxNo, true, false, false,"");
		int count = orderParm.getCount();
		for (int i = 0; i < count; i++) {
			// ȡһ��
			Order order = (Order) orderParm.getData("OBJECT", i);
			// ����շѱ��true
			if (order.getChargeFlg()) {
				// �շ����
				String rexpCode = order.getRexpCode();
				// ͨ���շ����õ�CHARGE
				String charge = chargeMap.get(rexpCode).toString();
				// �õ�parm�е��Ͻ��
				double oldTotAmt = chargeParm.getDouble(charge);
				// �����½��
				double totAmt = order.getArAmt() + oldTotAmt;
				// �����շѽ��
				chargeParm.setData(charge, totAmt);
			}
		}
		setChargeParm(chargeParm);
	}

	/**
	 * ��ʾ��ʽ(δ�շ�)
	 * 
	 * @param boo
	 *            boolean
	 * @param rxNo
	 *            String
	 * @param cat1Type
	 *            String
	 * @return TParm
	 */
	public TParm getOrderParmNotCharge(boolean boo, String rxNo, String cat1Type, 
			boolean memPackageFlg, boolean unExecFlg,String memTradeNo) {
		// �õ�ȫ��order
		TParm parm = new TParm();
		if (cat1Type == null || cat1Type.length() == 0)
			parm = getOrderParm(boo, rxNo, true, memPackageFlg, unExecFlg,memTradeNo);
		else
			parm = getOrderParm(boo, rxNo, true, cat1Type, memPackageFlg, unExecFlg,memTradeNo);
		// ÿ�μ�����ö�Ҫ���³�ʼ���շ�parm
		TParm chargeParm = chargeParm();
		// �õ�����
		int count = parm.getCount();
		// parmɾ����������
		int parmCount = 0;
		for (int i = count - 1; i >= 0; i--) {
			Order order = (Order) parm.getData("OBJECT", i);
			// ������շ�
			if (cat1Type == null || cat1Type.length() == 0) {
				if (order.getBillFlg().equals("Y")) {
					// ��������е��շѱ��
					order.modifyChargeFlg(false);
					// ɾ��
					parm.removeRow(i);
					parmCount++;
				} else {
					// �շ����
					String rexpCode = order.getRexpCode();
					// ͨ���շ����õ�CHARGE
					String charge = chargeMap.get(rexpCode).toString();
					// �õ�parm�е��Ͻ��
					double oldTotAmt = chargeParm.getDouble(charge);
					// �����½��
					double totAmt = order.getArAmt() + oldTotAmt;
					// �����շѽ��
					chargeParm.setData(charge, totAmt);
				}

			} else {
				if (order.getBillFlg().equals("Y")
						|| !cat1Type.equals(order.getCat1Type())) {
					// ��������е��շѱ��
					order.modifyChargeFlg(false);
					// ɾ��
					parm.removeRow(i);
					parmCount++;
				} else {
					// �շ����
					String rexpCode = order.getRexpCode();
					// ͨ���շ����õ�CHARGE
					String charge = chargeMap.get(rexpCode).toString();
					// �õ�parm�е��Ͻ��
					double oldTotAmt = chargeParm.getDouble(charge);
					// �����½��
					double totAmt = order.getArAmt() + oldTotAmt;
					// �����շѽ��
					chargeParm.setData(charge, totAmt);
				}
			}
		}
		parm.setData("ACTION", "COUNT", count - parmCount);
		setChargeParm(chargeParm);
		return parm;
	}

	/**
	 * ��ʾ��ʽ(EKT�˷�)
	 * 
	 * @param boo
	 *            boolean
	 * @param rxNo
	 *            String
	 * @param cat1Type
	 *            String
	 * @return TParm
	 */
	public TParm getOrderParmEKTTCharge(boolean boo, String rxNo,
			String cat1Type, boolean memPackageFlg, boolean unExecFlg) {
		// �õ�ȫ��order
		TParm parm = new TParm();
		if (cat1Type == null || cat1Type.length() == 0)
			parm = getOrderParm(boo, rxNo, false, memPackageFlg, unExecFlg,"");
		else
			parm = getOrderParm(boo, rxNo, false, cat1Type, memPackageFlg, unExecFlg,"");
		// System.out.println("parm"+parm);
		// �õ�����
		int count = parm.getCount();
		// parmɾ����������
		int parmCount = 0;
		for (int i = count - 1; i >= 0; i--) {
			Order order = (Order) parm.getData("OBJECT", i);
			if (cat1Type == null || cat1Type.length() == 0) {
				// ���δ�շ��շ�
				if ((!order.getBillFlg().equals("Y"))
						|| (order.getBillFlg().equals("Y") && "C".equals(order
								.getBillType()))) {
					// ��������е��շѱ��
					order.modifyChargeFlg(false);
					// ɾ��
					parm.removeRow(i);
					parmCount++;
				}

			} else {
				// ���δ�շ��շ�
				if (((!order.getBillFlg().equals("Y")) || (order.getBillFlg()
						.equals("Y") && "C".equals(order.getBillType())))
						|| !cat1Type.equals(order.getCat1Type())) {
					// ��������е��շѱ��
					order.modifyChargeFlg(false);
					// ɾ��
					parm.removeRow(i);
					parmCount++;
				}
			}
		}
		parm.setData("ACTION", "COUNT", count - parmCount);
		return parm;
	}

	/**
	 * �õ�����ǩ������comb
	 * 
	 * @param cat1Type
	 *            String
	 * @return Vector
	 */
	public Vector getPrescriptionComb(String cat1Type) {
		// ����ǩcomb������
		Vector prescriptionComb = getPrescriptionList().getPrescriptionComb(
				cat1Type);
		return prescriptionComb;
	}

	/**
	 * �õ����е����ݸ�table��ʾ
	 * 
	 * @param boo
	 *            boolean
	 * @param rxNo
	 *            String
	 * @param changeFlg
	 *            boolean true �շ� false �˷�
	 * @return TParm
	 */
	public TParm getOrderParm(boolean boo, String rxNo, boolean changeFlg, 
			boolean memPackageFlg, boolean unExecFlg,String memTradeNo) {
		// �õ����е�parma��order
		TParm parm = getPrescriptionList().getParmShow();
//		 System.out.println("table��ʾ����"+parm);
		// ��������
		int count = parm.getCount();
		
		//add by huangtt 20150424 start  ȥ������Ϊ0��ҽ��
		int delCount =0;
		for (int i = count - 1; i >= 0; i--) {
			Order order = (Order) parm.getData("OBJECT", i);
			if(order.getDosageQty() == 0){
				parm.removeRow(i);
				delCount++;
			}
		}
		parm.setData("ACTION", "COUNT", count - delCount);
		count = parm.getCount();
		//add by huangtt 20150424 end  ȥ������Ϊ0��ҽ��
				

		int rxNoLength = rxNo.length();
		// parmɾ����������
		int parmCount = 0;
		TParm memTradeParm = null;
		for (int i = count - 1; i >= 0; i--) {
			// �õ���ǰorder
			Order order = (Order) parm.getData("OBJECT", i);
			if(!memPackageFlg){
				if(order.getMemPackageId() != null){
					parm.removeRow(i);
					parmCount++;
				}
			}else{
				if(order.getMemPackageId() == null){
					parm.removeRow(i);
					parmCount++;
				}else{
					//=====pangben 2019-4-25 ����ײ͹��˲�ѯ
					if(null != memTradeNo && memTradeNo.length()>0){
						String sql ="SELECT TRADE_NO FROM MEM_PAT_PACKAGE_SECTION_D "
								+ "WHERE CASE_NO='"+order.getCaseNo()+"' AND ID='"+order.getMemPackageId()+"'";
						memTradeParm = new TParm(TJDODBTool.getInstance().select(sql));
						if(!memTradeParm.getValue("TRADE_NO",0).equals(memTradeNo)){
							parm.removeRow(i);
							parmCount++;
						}
					}
					
				}
				
			}
			if(unExecFlg){
				if("Y".equals(order.getExecFlg())){
					parm.removeRow(i);
					parmCount++;
				}
			}
			// ��������е��շѱ��
			order.modifyChargeFlg(false);
			// ���order�д���ǩ�����
			if (rxNoLength == 0)
				continue;
			if (!order.getRxNo().equals(rxNo)) {
				parm.removeRow(i);
				parmCount++;
				continue;
			}
		}
		// ��ǰҽ������
		parm.setData("ACTION", "COUNT", count - parmCount);
		// �շѱ��
		for (int i = 0; i < count - parmCount; i++) {
			// �õ�ҽ��
			Order order = (Order) parm.getData("OBJECT", i);
			// �շ�
			if (changeFlg) {
				if (order.getBillFlg().equals("N")) {
					// ��ҽ������շѱ��
					//getPhaDosageCode() ��ΪgetPhaCheckCode modify by huangtt 20180824
					if (order.getExecFlg().equals("Y")||(order.getPhaCheckCode().length()>0 && order.getPhaRetnCode().length() == 0)) {
						order.modifyChargeFlg(boo);//====pangben 2014-4-2
					}else{
						order.modifyChargeFlg(false);
					}
				}
			} else {
				if (order.getBillFlg().equals("Y")
						&& "E".equals(order.getBillType())) {
					// ��ҽ������˷ѱ��
//					if (order.getExecFlg().equals("Y")||(order.getPhaDosageCode().length()>0 && order.getPhaRetnCode().length() == 0)) {
						order.modifyChargeFlg(boo);//====pangben 2014-4-2
//					}else{
//						order.modifyChargeFlg(false);
//					}
				}
			}
			// ��ӱ��
			parm.addData("CHARGE", order.getChargeFlg());
			//���OTC���  add by huangtt 20150520
			parm.addData("PRESCRIPTION_FLG", getPrescriptionFlg(order.getOrderCode()));
		}
		return parm;
	}

	public TParm getOrderParm(boolean boo, String rxNo, boolean changeFlg,
			String cat1Type, boolean memPackageFlg, boolean unExecFlg,String memTradeNo) {
		// �õ����е�parma��order
		TParm parm = getPrescriptionList().getParmShow();
		// System.out.println("table��ʾ����"+parm);
		// ��������
		int count = parm.getCount();
		
		//add by huangtt 20150424 start  ȥ������Ϊ0��ҽ��
		int delCount =0;
		for (int i = count - 1; i >= 0; i--) {
			Order order = (Order) parm.getData("OBJECT", i);
			if(order.getDosageQty() == 0){
				parm.removeRow(i);
				delCount++;
			}
		}
		parm.setData("ACTION", "COUNT", count - delCount);
		count = parm.getCount();
		//add by huangtt 20150424 end  ȥ������Ϊ0��ҽ��
		
		int rxNoLength = rxNo.length();
		// parmɾ����������
		int parmCount = 0;
		TParm memTradeParm =null;
		for (int i = count - 1; i >= 0; i--) {
			// �õ���ǰorder
			Order order = (Order) parm.getData("OBJECT", i);
			if(!memPackageFlg){
				if(order.getMemPackageId() != null){
					parm.removeRow(i);
					parmCount++;
				}
			}else{
				if(order.getMemPackageId() == null){
					parm.removeRow(i);
					parmCount++;
				}else{
					//=====pangben 2019-4-25 ����ײ͹��˲�ѯ
					if(null != memTradeNo && memTradeNo.length()>0){
						String sql ="SELECT TRADE_NO FROM MEM_PAT_PACKAGE_SECTION_D "
								+ "WHERE CASE_NO='"+order.getCaseNo()+"' AND ID='"+order.getMemPackageId()+"'";
						memTradeParm = new TParm(TJDODBTool.getInstance().select(sql));
						if(!memTradeParm.getValue("TRADE_NO",0).equals(memTradeNo)){
							parm.removeRow(i);
							parmCount++;
						}
					}
				}
				
			}
			if(unExecFlg){
				if("Y".equals(order.getExecFlg())){
					parm.removeRow(i);
					parmCount++;
				}
			}
			// ��������е��շѱ��
			order.modifyChargeFlg(false);
			// ���order�д���ǩ�����
			if (rxNoLength == 0)
				continue;
			if (cat1Type == null || cat1Type.length() == 0) {
				if (!order.getRxNo().equals(rxNo)) {
					parm.removeRow(i);
					parmCount++;
					continue;
				}
			} else {
				if (!order.getRxNo().equals(rxNo)
						|| !cat1Type.equals(order.getCat1Type())) {
					parm.removeRow(i);
					parmCount++;
					continue;

				}
			}
		}
		// ��ǰҽ������
		parm.setData("ACTION", "COUNT", count - parmCount);
		// �շѱ��
		for (int i = 0; i < count - parmCount; i++) {
			// �õ�ҽ��
			Order order = (Order) parm.getData("OBJECT", i);
			// �շ�
			if (changeFlg) {
				if (order.getBillFlg().equals("N")) {
					// ��ҽ������շѱ��
					order.modifyChargeFlg(boo);
				}
			} else {
				if (order.getBillFlg().equals("Y")
						&& "E".equals(order.getBillType())) {
					// ��ҽ������˷ѱ��
					order.modifyChargeFlg(boo);
				}
			}
			// ��ӱ��
			parm.addData("CHARGE", order.getChargeFlg());
		}
		return parm;
	}

	/**
	 * �õ�Ŀǰѡ�����ݵ��շ���Ŀ�۷���
	 * 
	 * @param chargeFlg
	 *            boolean
	 * @param cat1Type
	 *            String
	 * @return double
	 */
	public double getFee(boolean chargeFlg, String cat1Type) {
		// ÿ�μ�����ö�Ҫ���³�ʼ���շ�parm
		TParm chargeParm = chargeParm();
		double fee = 0.0;
		// �õ����е�order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			// ȡһ��order
			Order order = (Order) list.get(i);
			// ������շ�
			if (!order.getChargeFlg())
				continue;
			// ȥ������
			if (order.getOrderCode().equals(order.getOrdersetCode()))
				continue;
			// ҽ�����ɸѡ
			if (cat1Type == null || cat1Type.length() == 0) {
			} else {
				if (!cat1Type.equals(order.getCat1Type()))
					continue;
			}
			if (chargeFlg)
				fee += order.getArAmt();
			else
				fee -= order.getArAmt();
			// �վ����
			String rexpCode = order.getRexpCode();
			// ͨ���շ����õ�CHARGE
			String charge = chargeMap.get(rexpCode).toString();
			// �õ�parm�е��Ͻ��
			double oldTotAmt = chargeParm.getDouble(charge);
			// �����½��
			double totAmt = order.getArAmt() + oldTotAmt;
			// �����շѽ��
			chargeParm.setData(charge, totAmt);
		}
		setChargeParm(chargeParm);
		// System.out.println("�½��"+fee);
		return fee;
	}

	public double getFee(boolean chargeFlg) {
		// ÿ�μ�����ö�Ҫ���³�ʼ���շ�parm
		TParm chargeParm = chargeParm();
		double fee = 0.0;
		// �õ����е�order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			// ȡһ��order
			Order order = (Order) list.get(i);
			// ������շ�
			if (!order.getChargeFlg())
				continue;
			// ȥ������
			if (order.getOrderCode().equals(order.getOrdersetCode()))
				continue;
			if (chargeFlg)
				fee += order.getArAmt();
			else
				fee -= order.getArAmt();
			// �վ����
			String rexpCode = order.getRexpCode();
			// ͨ���շ����õ�CHARGE
			String charge = chargeMap.get(rexpCode).toString();
			// �õ�parm�е��Ͻ��
			double oldTotAmt = chargeParm.getDouble(charge);
			// �����½��
			double totAmt = order.getArAmt() + oldTotAmt;
			// �����շѽ��
			chargeParm.setData(charge, totAmt);
		}
		setChargeParm(chargeParm);
		// System.out.println("�½��"+fee);
		return fee;
	}

	/**
	 * ����ǰ�ı�order������
	 */
	public void chargeOrder() {
		Timestamp date = SystemTool.getInstance().getDate();
		// ȡ���е�order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			// ȡһ��order
			Order order = (Order) list.get(i);
			// ���û���շѱ��
			if (!order.getChargeFlg())
				continue;
			// �����շ�ʱ��
			order.modifyBillDate(date);
			// �����շѱ��
			order.modifyBillFlg("Y");
			// �����շ���Ա
			order.modifyBillUser(Operator.getID());
			// ����ֽ��շ�
			order.modifyBillType("C");
		}
	}

	/**
	 * ҽ�ƿ�����ǰ�ı�order������
	 * 
	 * @param parm
	 *            TParm
	 * @param flg
	 *            boolean
	 */
	public void chargeOrderEKT(TParm parm, boolean flg) {
		Timestamp date = SystemTool.getInstance().getDate();
		// ȡ���е�order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		int opType = parm.getInt("OP_TYPE");
		List listRx = (List) parm.getData("RX_LIST");
		for (int i = 0; i < count; i++) {
			// ȡһ��order
			Order order = (Order) list.get(i);
			// �ۿ�
			// System.out.println("opType" + opType + " " +
			// order.getChargeFlg());
			if (flg && order.getChargeFlg()) {
				order.modifyBillFlg("N");
				order.modifyBillUser("");
				order.modifyBillType("");
				continue;
			}
			if (opType == 1 || opType == 4) {
				// ���û���շѱ��
				if (!order.getChargeFlg())
					continue;
				// �����շ�ʱ��
				order.modifyBillDate(date);
				// �����շѱ��
				order.modifyBillFlg("Y");
				// �����շ���Ա
				order.modifyBillUser(Operator.getID());
				// ���ҽ�ƿ��շ�
				order.modifyBillType("E");
				continue;
			}
			// �˷�
			if (opType == 2) {
				if (listRx.indexOf(order.getRxNo()) == -1)
					continue;
				if ("C".equals(order.getBillType()))
					continue;
				if ("N".equals(order.getBillFlg()))
					continue;
				order.modifyBillFlg("N");
				// �����շ�ʱ��
				order.modifyBillDate(null);
				order.modifyBillUser("");
				order.modifyBillType("");
			}
		}
	}

	/**
	 * �ֽ𱣴�ǰ�ı�order������
	 */
	public void chargeOrderCash() {
		Timestamp date = SystemTool.getInstance().getDate();
		// ȡ���е�order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			// ȡһ��order
			Order order = (Order) list.get(i);
			// ���û���շѱ��
			if (!order.getChargeFlg())
				continue;
			// �����շ�ʱ��
			order.modifyBillDate(date);
			// �����շѱ��
			order.modifyBillFlg("Y");
			// �����շ���Ա
			order.modifyBillUser(Operator.getID());
			// ����ֽ��շ�
			order.modifyBillType("C");
		}
	}

	/**
	 * ���ҩƷ�Ƿ��Ѿ����䷢ �Ƿ������ҩ
	 * 
	 * @param order
	 *            TParm
	 * @param row
	 *            int
	 *            flg У��ɾ�����޸Ĳ�����ɾ��������ɾ��һ�����ݣ�ֻ��ɾ������ǩ
	 *            flg true ɾ��һ�� �����޸Ķ���ʹ��   false  ɾ������ǩʹ��
	 * @return  int 0 ���� 1û�����䷢  2.û���˷�
	 */
	public boolean checkDrugCanUpdate(TParm order, int row,TParm parm,boolean flg) {
		boolean needExamineFlg = false;
		// System.out.println("У���ܷ���ҩ"+order);
		// �������ҩ ��˻���ҩ��Ͳ������ٽ����޸Ļ���ɾ��
		if ("W".equals(order.getValue("PHA_TYPE"))
				|| "C".equals(order.getValue("PHA_TYPE"))) {
			// �ж��Ƿ����
			needExamineFlg = PhaSysParmTool.getInstance().needExamine();
		}
		// System.out.println("");
		// �������ҩ ��˻���ҩ��Ͳ������ٽ����޸Ļ���ɾ��
		if ("G".equals(order.getValue("PHA_TYPE"))) {
			// �ж��Ƿ����
			needExamineFlg = PhaSysParmTool.getInstance().needExamineD();
		}
		// if (order.getValue("PHA_CHECK_CODE", row).length() > 0
		// && order.getValue("PHA_RETN_CODE", row).length() == 0) {
		// return false;
		// }
		// ������������ ��ô�ж����ҽʦ�Ƿ�Ϊ��
//		if (needExamineFlg) {
//			// System.out.println("�����");
//			// ��������Ա���� ��������ҩ��Ա ��ô��ʾҩƷ����� ���������޸�
//			if (order.getValue("PHA_CHECK_CODE").length() > 0
//					&& order.getValue("PHA_RETN_CODE").length() == 0) {
//				return false;
//			}
//		} else {// û��������� ֱ����ҩ
//			// �ж��Ƿ�����ҩҩʦ
//			// System.out.println("�����");
//			if (order.getValue("PHA_DOSAGE_CODE").length() > 0
//					&& order.getValue("PHA_RETN_CODE").length() == 0) {
//				return false;// �Ѿ���ҩ���������޸�
//			}
//		}
		int reIndex=OrderTool.getInstance().checkPhaIsExe(needExamineFlg, order,flg);
		switch (reIndex) {// 0���� 1û�����䷢  2.û���˷�
		case 1:
			parm.setData("MESSAGE","E0189");
			return false;
		case 2:
			parm.setData("MESSAGE","�ѼƷѲ������޸Ļ�ɾ������");
			return false;
		case 3:
			//true:ɾ��һ��  false ɾ������ǩ ���շѵ����Ѿ���ҩ״̬
			parm.setData("MESSAGE","����ɾ�����������˷�");
			parm.setData("MESSAGE_FLG","Y");
			parm.setData("MESSAGE_INDEX","Y");
			return false;
		case 4://true:ɾ��һ��  false ɾ������ǩ ���շѵ����Ѿ���ҩ״̬
			parm.setData("MESSAGE","��������ҩ�����޸ģ���ɾ������");
			parm.setData("MESSAGE_FLG","Y");
			parm.setData("MESSAGE_INDEX","Y");
			return false;
		case 5://����ҩ  δ�շ�
			parm.setData("MESSAGE","��������ҩ�����޸ģ���ɾ������");
			parm.setData("MESSAGE_FLG","Y");
			parm.setData("MESSAGE_INDEX","Y");
			return false;
		}
		return true;
	}

	/**
	 * �õ�ҽ�ƿ��������
	 * 
	 * @param flg
	 *            boolean
	 * @param cat1Type
	 *            String
	 * @return TParm
	 */
	public TParm getEKTParm(boolean flg, String cat1Type) {
		TParm parm = new TParm();
		// ȡ���е�order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		double sum=0.00;//�˴β�����δ�շ�ҽ��
		for (int i = 0; i < count; i++) {
			// ȡһ��order
			Order order = (Order) list.get(i);
			parm.setData("MR_NO", order.getMrNo());
			// if("C".equals(order.getBillType()))
			// continue;
			if (null != order.getPrintFlg() && order.getPrintFlg().equals("Y")) {
				continue;
			}
			sum+=order.getArAmt();
			parm.addData("RX_NO", order.getRxNo());
			parm.addData("ORDER_CODE", order.getOrderCode());
			parm.addData("SEQ_NO", order.getSeqNo());
			parm.addData("AMT", order.getArAmt());
			parm.addData("AR_AMT", order.getArAmt());
			parm.addData("EXEC_FLG", order.getExecFlg());
			parm.addData("RECEIPT_FLG", order.getReceiptFlg());
			parm.addData("OWN_PRICE", order.getOwnPrice());
			parm.addData("QTY", order.getDosageQty());
			parm.addData("BUSINESS_NO", order.getBusinessNo());
			parm.addData("CHARGE_FLG", order.getChargeFlg());
			if (order.getChargeFlg()) {
				if (flg) {
					parm.addData("BILL_FLG", "N");
					//parm.setData("BUSINESS_TYPE", "OPBT");
					//parm.setData("TYPE_FLG", "Y");// ҽ�ƿ��շ�����EKT_TREDE state ֵ�ж�
					// STATE=3 �˷�
				} else {
					parm.addData("BILL_FLG", "Y");
					//parm.setData("BUSINESS_TYPE", "OPB");
				}
			} else
				parm.addData("BILL_FLG", order.getBillFlg());

		}
		parm.setData("SUM",sum);
		return parm;
	}
	/**
	 * �õ�ҽ��������� �ֽ�������
	 * 
	 * @param flg
	 *            boolean
	 * @param cat1Type
	 *            String
	 * @return TParm ==================pangben 2014-8-20
	 */
	public TParm getReduceCashParm(boolean flg){
		TParm parm = new TParm();
		// ȡ���е�order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		double billAmt = 0.00;// δ�շ�ҽ�����
		double sumAmt = 0.00;// ���н�� �����շ�δ�շѵ�
		for (int i = 0; i < count; i++) {
			// ȡһ��order
			Order order = (Order) list.get(i);
			parm.setData("MR_NO", order.getMrNo());
			if (null != order.getPrintFlg() && order.getPrintFlg().equals("Y")) {
				continue;
			}
			// ���δ�շѲ���û�й�ѡ��ִ���շѲ���
			// ChargeFlg =N && BILL_FLG=N ��ִ��
			if (!order.getChargeFlg() && order.getBillFlg().equals("N")) {
				continue;
			}
			if (null != order.getBillFlg() && order.getBillFlg().equals("N")) {// δ�շ�ҽ��
				billAmt += order.getArAmt();
			}
			sumAmt += order.getArAmt();
			parm.addData("RX_NO", order.getRxNo());
			parm.addData("ORDER_CODE", order.getOrderCode());
			parm.addData("SEQ_NO", order.getSeqNo());
			parm.addData("AMT", order.getArAmt());
			parm.addData("AR_AMT", order.getArAmt());
			parm.addData("REXP_CODE", order.getRexpCode());
			parm.addData("OWN_PRICE", order.getOwnPrice());
			parm.addData("QTY", order.getDosageQty());
			parm.addData("DOSAGE_QTY", order.getDosageQty());
			parm.addData("SPECIFICATION", order.getSpecification());
			parm.addData("ORDERSET_CODE", order.getOrdersetCode());
			parm.addData("HIDE_FLG", order.getHideFlg());
			parm.addData("ORDER_DESC", order.getOrderDesc());
			parm.addData("SETMAIN_FLG", order.getSetmainFlg());
			parm.addData("DOSAGE_UNIT", order.getDosageUnit());
			if (order.getChargeFlg()) {
				if (flg) {
					parm.addData("BILL_FLG", "N");
					//parm.setData("BUSINESS_TYPE", "OPBT");
					parm.setData("TYPE_FLG", "Y");// ҽ�ƿ��շ�����EKT_TREDE state ֵ�ж�
					// STATE=3 �˷�
				} else {
					parm.addData("BILL_FLG", "Y");
					//parm.setData("BUSINESS_TYPE", "OPB");
				}
			} else
				parm.addData("BILL_FLG", order.getBillFlg());

		}
		parm.setCount(parm.getCount("ORDER_CODE"));
		parm.setData("sumAmt", sumAmt);// ���н�� �����շ�δ�շ�
		parm.setData("billAmt", billAmt);// δ�շ�ҽ�����
		return parm;
	}
	/**
	 * �õ�ҽ�ƿ�������� ҽ���ָ��������
	 * 
	 * @param flg
	 *            boolean
	 * @param cat1Type
	 *            String
	 * @return TParm ==================pangben 2012-3-29
	 */
	public TParm getInsEKTParm(boolean flg, String cat1Type) {
		TParm parm = new TParm();
		// ȡ���е�order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		TParm parmBill = new TParm();
		double billAmt = 0.00;// δ�շ�ҽ�����
		double sumAmt = 0.00;// ���н�� �����շ�δ�շѵ�
		StringBuffer phaRxNo=new StringBuffer(); //��ʹ�� �޸��Ѿ��ۿ������ �帺ʹ��
		for (int i = 0; i < count; i++) {
			// ȡһ��order
			Order order = (Order) list.get(i);
			parm.setData("MR_NO", order.getMrNo());
			if (null != order.getPrintFlg() && order.getPrintFlg().equals("Y")) {
				continue;
			}
			// ���δ�շѲ���û�й�ѡ��ִ���շѲ���
			// =============pangben 2012-4-3
			// ChargeFlg =N && BILL_FLG=N ��ִ��
			if (!order.getChargeFlg() && order.getBillFlg().equals("N")) {
				continue;
			}
			
//			if (null != order.getBusinessNo()) {
//				if (!tradeNo.toString().contains(order.getBusinessNo())) {
//					// UPDATE EKT_TRADE ��ʹ�� �޸��Ѿ��ۿ������ �帺ʹ��
//					tradeNo.append("'").append(order.getBusinessNo()).append(
//							"',");
//				}
//			}
			// if("C".equals(order.getBillType()))
			// continue;
			if (null != order.getBillFlg() && order.getBillFlg().equals("N")) {// δ�շ�ҽ��
				setExeParm(parmBill, order);
				if (null != order.getCat1Type() && // ==pangben2013-5-15���ҩ����ҩ��ʾ���������
						order.getCat1Type().equals("PHA") && !order.getRxType().equals("7")
						&& !order.getRxType().equals("0")) {
					if (!phaRxNo.toString().contains(order.getRxNo())) {
						phaRxNo.append(order.getRxNo()).append(",");
					}
				}
			}
			if (cat1Type == null || cat1Type.length() == 0) {
				if (order.getOrdersetCode() != null
						&& order.getOrdersetCode().equals(order.getOrderCode())) {
					continue;
				}
			} else {
				if (!cat1Type.equals(order.getCat1Type())
						|| (order.getOrdersetCode() != null && order
								.getOrdersetCode().equals(order.getOrderCode()))) {
					continue;
				}

			}
			if (null != order.getBillFlg() && order.getBillFlg().equals("N")) {// δ�շ�ҽ��
				billAmt += order.getArAmt();
			}
			sumAmt += order.getArAmt();// =======pangben 2012-8-13
			parm.addData("RX_NO", order.getRxNo());
			parm.addData("ORDER_CODE", order.getOrderCode());
			parm.addData("SEQ_NO", order.getSeqNo());
			parm.addData("AMT", order.getArAmt());
			parm.addData("AR_AMT", order.getArAmt());
			parm.addData("REXP_CODE", order.getRexpCode());
			parm.addData("EXEC_FLG", order.getExecFlg());
			parm.addData("RECEIPT_FLG", order.getReceiptFlg());
			parm.addData("OWN_PRICE", order.getOwnPrice());
			parm.addData("QTY", order.getDosageQty());
			parm.addData("DOSAGE_QTY", order.getDosageQty());
			parm.addData("SPECIFICATION", order.getSpecification());
			parm.addData("TAKE_DAYS", order.getTakeDays());
			parm.addData("DR_NOTE", order.getDrNote());
			parm.addData("ORDERSET_CODE", order.getOrdersetCode());
			parm.addData("HIDE_FLG", order.getHideFlg());
			parm.addData("NHI_CODE_O", order.getNhiCodeO());// ====pangben
															// 2012-8-8
			parm.addData("NHI_CODE_E", order.getNhiCodeE());// ====pangben
															// 2012-8-8
			parm.addData("NHI_CODE_I", order.getNhiCodeI());// ====pangben
															// 2012-8-8
			parm.addData("ORDER_DESC", order.getOrderDesc()
					+ order.getSpecification());
			parm.addData("YF", "");// �÷�
			parm.addData("ZFBL1", "");// �Ը�����
			parm.addData("PZWH", "");// ��׼�ĺ�
			parm.addData("BUSINESS_NO", order.getBusinessNo());
			// System.out.println("ҽ��"+order.getOrderDesc()+"�շ�ע��"+order.getChargeFlg());
			// System.out.println(" order.getBillFlg()::"+ order.getBillFlg());
			if (order.getChargeFlg()) {
				if (flg) {
					parm.addData("BILL_FLG", "N");
					//parm.setData("BUSINESS_TYPE", "OPBT");
					parm.setData("TYPE_FLG", "Y");// ҽ�ƿ��շ�����EKT_TREDE state ֵ�ж�
					// STATE=3 �˷�
				} else {
					parm.addData("BILL_FLG", "Y");
					//parm.setData("BUSINESS_TYPE", "OPB");
				}
			} else
				parm.addData("BILL_FLG", order.getBillFlg());

		}
		parm.setData("sumAmt", sumAmt);// ���н�� �����շ�δ�շ�
		parm.setData("billAmt", billAmt);// δ�շ�ҽ�����
		parm.setData("parmBill", parmBill.getData());// δ�շ�ҽ������
		parm.setData("PHA_RX_NO", phaRxNo.length()>0? phaRxNo.toString().substring(0,
				phaRxNo.toString().lastIndexOf(",")):"");//=pangben2013-5-15���ҩ����ҩ��ʾ���������
		//String exeTradeNo = "";
		// ����ڲ����׺��� ���˴β�����ҽ���ۿ�������Ҫ�˻���ҽ��
//		if (tradeNo.length() > 0) {
//			exeTradeNo = tradeNo.toString().substring(0,
//					tradeNo.toString().lastIndexOf(","));
//		}
	   //parm.setData("TRADE_SUM_NO",exeTradeNo);//UPDATE EKT_TRADE �帺����,ҽ�ƿ��ۿ��ڲ����׺���,��ʽ'xxx','xxx'
		return parm;
	}
	private void setExeParm(TParm result,Order order){
		//result.addData("CAT1_TYPE", order.getCat1Type());// ���˼����飬ҽ���޸�ҽ��ҽ�ƿ��շ�ʱʹ��
		result.addData("RX_NO", order.getRxNo());
		//result.addData("MED_APPLY_NO", order.getMedApplyNo());// ������
		//result.addData("SETMAIN_FLG", order.getSetmainFlg());// ����ҽ��
		result.addData("SEQ_NO", order.getSeqNo());
		result.addData("BILL_TYPE","E");
		result.addData("BILL_FLG", "Y");
		result.addData("AMT", order.getArAmt());
		result.addData("AR_AMT", order.getArAmt());
		//result.addData("BUSINESS_NO", null==order.getBusinessNo()?"":order.getBusinessNo());
		result.addData("ORDER_CODE", order.getOrderCode());
	} 
	/**
	 * �õ��ֽ��������
	 * 
	 * @param flg
	 *            boolean
	 * @param cat1Type
	 *            String
	 * @return TParm
	 */
	public TParm getCashParm(boolean flg, String cat1Type) {
		TParm parm = new TParm();
		// ȡ���е�order
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			// ȡһ��order
			Order order = (Order) list.get(i);
			parm.setData("MR_NO", order.getMrNo());
			// if("C".equals(order.getBillType()))
			// continue;
			if (cat1Type == null || cat1Type.length() == 0) {
				if (order.getOrdersetCode() != null
						&& order.getOrdersetCode().equals(order.getOrderCode())) {
					continue;
				}
			} else {
				if (!cat1Type.equals(order.getCat1Type())
						|| (order.getOrdersetCode() != null && order
								.getOrdersetCode().equals(order.getOrderCode()))) {
					continue;
				}

			}
			parm.addData("RX_NO", order.getRxNo());
			parm.addData("ORDER_CODE", order.getOrderCode());
			parm.addData("SEQ_NO", order.getSeqNo());
			parm.addData("AMT", order.getArAmt());
			parm.addData("QTY", order.getDctTakeQty());
			parm.addData("EXEC_FLG", order.getExecFlg());
			parm.addData("RECEIPT_FLG", order.getReceiptFlg());
			// System.out.println("ҽ��"+order.getOrderDesc()+"�շ�ע��"+order.getChargeFlg());
			if (order.getChargeFlg()) {
				if (flg) {
					parm.addData("BILL_FLG", "N");
					parm.setData("BUSINESS_TYPE", "OPBT");
				} else {
					parm.addData("BILL_FLG", "Y");
					parm.setData("BUSINESS_TYPE", "OPB");
				}
			} else
				parm.addData("BILL_FLG", order.getBillFlg());

		}
		return parm;
	}

	/**
	 * ����Ƿ���Ҫ�շѵ�ҽ��
	 * 
	 * @return boolean
	 */
	public boolean checkOrder() {
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			// ȥһ��order
			Order order = (Order) list.get(i);
			if (!order.getChargeFlg())
				continue;
			return false;
		}
		return true;
	}

	/**
	 * ����ҽ���޸�������
	 * 
	 * @param orderCode
	 *            String
	 * @param orderSetGroupNo
	 *            int
	 * @param rxNo
	 *            String
	 * @param b
	 *            boolean
	 */
	public void congregation(String orderCode, int orderSetGroupNo,
			String rxNo, boolean b) {
		List list = getPrescriptionList().getOrder();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			// �õ�һ��order
			Order order = (Order) list.get(i);
			if (!order.getRxNo().equals(rxNo))
				continue;
			// ����Ǽ���ҽ��ϸ�������շѱ��
			if (order.getOrdersetCode().equals(orderCode)
					&& order.getOrderSetGroupNo() == orderSetGroupNo) {
				// �����շѱ��ΪN
				order.modifyChargeFlg(b);
			}
		}
	}

	/**
	 * ����Ƿ���ҽ��
	 * 
	 * @return boolean
	 */
	public boolean checkOrderCount() {
		List list = getPrescriptionList().getOrder();
		// System.out.println("ҽ��list"+list);
		if (list.size() > 0)
			return false;
		return true;
	}

	/**
	 * ���ʣ��Ʊ����
	 * 
	 * @return TParm
	 */
	public TParm checkInvoice() {
		TParm parm = new TParm();
		if (bilInvoice == null) {
			parm.addData("COUNT", -1);
			return parm;
		}
		String endNo = bilInvoice.getEndInvno();
		String updateNo = bilInvoice.getUpdateNo();
		return parm;
	}

	/**
	 * ҽ�ƿ�����
	 * 
	 * @param parm
	 *            TParm
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	public TParm onSaveEKT(TParm parm, boolean flg) {
		// System.out.println("����ǰparm" + parm);

		// ������Ҫ�շѵ�order��ӻ�д����
		chargeOrderEKT(parm, flg);
		TParm opbParm =parm.getParm("orderParm");
		// ����ҽ������------��Ҫ�޸� ҽ���շѿ۷Ѳ��� ORDER parm �޸�
		opbParm.setData("TRADE_NO", parm.getData("TRADE_NO"));
		// opbParm.setData("GREEN_FLG", parm.getValue("GREEN_FLG")); //
		// ����ɫͨ�����ע��
		opbParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		opbParm.setData("OPT_DATE", parm.getValue("OPT_DATE"));
		opbParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		opbParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		opbParm.setData("MR_NO", parm.getValue("MR_NO"));
//		opbParm.setData("BILL_TYPE", "E");
//		// N:�˷� Y:�շ�
//		if (flg) {
//			opbParm.setData("BILL_FLG", "N");
//		} else {
//			opbParm.setData("BILL_FLG", "Y");
//		}
		// ����opbaction
		// System.out.println("�����parm" + opbParm);
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onSaveEKT", opbParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;

	}

	/**
	 * �ֽ𱣴�
	 * 
	 * @param parm
	 *            TParm
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	public TParm onSaveCash(TParm parm, boolean flg) {
		// System.out.println("�ֽ�����ǰparm" + parm);
		// ������Ҫ�շѵ�order��ӻ�д����
		chargeOrderCash();
		TParm opbParm = new TParm();
		TParm modifyOrderParm = this.getPrescriptionList().getParm();
		modifyOrderParm.getParm(OrderList.MODIFIED);
		// ����ҽ������
		// TParm ins_result = parm.getParm("INS_RESULT");// ҽ���ز�
		// ҽ���շѲ���======pangb 2011-12-03
		opbParm
				.setData("ORDER", this.getPrescriptionList().getParm()
						.getData());
		if (flg)
			opbParm.setData("FLG", "N");
		else
			opbParm.setData("FLG", "Y");
		opbParm.setData("parmReduce",parm.getParm("parmReduce").getData());//�ֽ����=====pangben 2014-8-21
		// ����opbaction
		// System.out.println("�ֽ������parm" + opbParm);
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onSave", opbParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;

	}

	/**
	 * ����
	 * 
	 * @param charge
	 *            String
	 * @return TParm
	 */
	public TParm onSave(String charge) {
		TParm parm = checkData();
		if (parm.getErrCode() == -1)
			return parm;
		// ������Ҫ�շѵ�order��ӻ�д����
		chargeOrder();
		// �õ�odo�е�parma
		TParm opbParm = getParm();
		// �Ƿ��շ�
		// �շ�Ȩ��
		TParm right = new TParm();
		// ����Ȩ��
		right.setData("RIGHT", charge);
		// ���Ȩ��
		opbParm.setData("RIGHT", right.getData());
		// ����opbaction
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onSave", opbParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * �õ��˷ѱ���parm
	 * 
	 * @param row
	 *            int
	 * @return TParm
	 */
	public TParm getBackReceiptParm(int row) {
		TParm backReceiptParm = new TParm();
		OPBReceipt receipt = (OPBReceipt) receiptList.get(row);
		if (receipt == null)
			return null;
		String receiptNo = receipt.getReceiptNo();
		if (receiptNo == null || receiptNo.length() == 0)
			return null;
		BilInvrcpt bilInvrcpt = new BilInvrcpt();
		// ��ʼ��һ���˷ѵ�Ʊ��
		if (!bilInvrcpt.initBilInvrcpt(recpType, receipt.getPrintNo()))
			return null;
		// �����˷�order
		receipt.setBackReceiptValue();
		// �õ��˷�Ʊ��
		OPBReceipt receiptBack = receipt.getReceiptByTParm(receipt.getParm(),
				-1);
		TParm receiptParm = receipt.getParm();
		backReceiptParm.setData("UPRECEIPT", receiptParm.getData());
		// �õ��շ�Ʊ��
		TParm receiptBaceParm = receiptBack.getParm();
		backReceiptParm.setData("INRECEIPT", receiptBaceParm.getData());
		// �õ��վ�����
		TParm bilInvrcptParm = setBackBilInvrcptValue(receipt.getPrintNo());
		backReceiptParm.setData("BILINVRCPT", bilInvrcptParm.getData());
		// �õ�ҽ���б�
		OrderList orderList = receipt.getOrderList();
		// �õ�ҽ��parm
		TParm orderListParm = orderList.getParm();
		backReceiptParm.setData("ORDER", orderListParm.getParm(
				orderList.MODIFIED).getData());

		return backReceiptParm;
	}

	/**
	 * �����˷�����
	 * 
	 * @param printNo
	 *            String
	 * @return TParm
	 */
	public TParm setBackBilInvrcptValue(String printNo) {
		TParm bilInvrcptParm = new TParm();
		bilInvrcptParm.setData("RECP_TYPE", recpType);
		bilInvrcptParm.setData("CANCEL_FLG", "1");
		bilInvrcptParm.setData("INV_NO", printNo);
		bilInvrcptParm.setData("CANCEL_DATE", SystemTool.getInstance()
				.getDate());
		bilInvrcptParm.setData("CANCEL_USER", Operator.getID());
		bilInvrcptParm.setData("OPT_USER", Operator.getID());
		bilInvrcptParm.setData("OPT_TERM", Operator.getIP());
		return bilInvrcptParm;
	}

	/**
	 * �˷ѱ���
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean onSaveBackReceipt(int row) {
		TParm saveParm = getBackReceiptParm(row);
		if (saveParm == null)
			return false;
		// ����opbaction
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"backReceipt", saveParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * �õ����������
	 * 
	 * @param row
	 *            int
	 * @return TParm
	 */
	public TParm getSaveRePrintParm(int row) {
		TParm saveRePrintParm = new TParm();
		OPBReceipt receipt = (OPBReceipt) receiptList.get(row);
		if (receipt == null)
			return null;
		String receiptNo = receipt.getReceiptNo();
		if (receiptNo == null || receiptNo.length() == 0)
			return null;
		BilInvrcpt backBilInvrcpt = new BilInvrcpt();
		// ��ʼ��һ������Ʊ��
		if (!backBilInvrcpt.initBilInvrcpt(recpType, receipt.getPrintNo()))
			return null;

		// �õ�����Ʊ�����ݴ���
		TParm bilInvrcptParm = setBackBilInvrcptValue(receipt.getPrintNo());
		saveRePrintParm.setData("UPBILINVRCPT", bilInvrcptParm.getData());

		// ��Ʊ�ݷ����µĴ�ӡƱ��
		receipt.setPrintNo(bilInvoice.getUpdateNo());
		TParm receiptParm = receipt.getParm();
		saveRePrintParm.setData("UPRECEIPT", receiptParm.getData());

		// �õ���ӡ��Ʊ��
		// ����Ҫ��ӡ��Ʊ������
		dealNewInvrcp(receipt);
		TParm newBilInvrcpt = bilInvrcpt.getParm();
		saveRePrintParm.setData("INBILINVRCPT", newBilInvrcpt.getData());

		// �õ���ǰʹ�õ�Ʊ�ݡ�����ǰƱ���Լ�1
		bilInvoice.setUpdateNo(StringTool.addString(bilInvoice.getUpdateNo()));
		TParm newBilInvoice = bilInvoice.getParm();
		saveRePrintParm.setData("BILINVOICE", newBilInvoice.getData());
		return saveRePrintParm;
	}

	/**
	 * ����Ҫ��ӡ��Ʊ��
	 * 
	 * @param receipt
	 *            OPBReceipt
	 */
	public void dealNewInvrcp(OPBReceipt receipt) {
		bilInvrcpt.setInvNo(bilInvoice.getUpdateNo());
		bilInvrcpt.setArAmt(receipt.getArAmt());
		bilInvrcpt.setReceiptNo(receipt.getReceiptNo());
		bilInvrcpt.setCancelFlg("0");
	}

	/**
	 * Ʊ�ݲ���
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean onSaveRePrint(int row) {
		TParm saveRePrintParm = getSaveRePrintParm(row);
		if (saveRePrintParm == null)
			return false;
		// ����opbaction
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"saveRePrint", saveRePrintParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}

		return true;
	}

	/**
	 * �õ�����ҽ������ϸ
	 * 
	 * @param groupNo
	 *            int
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderSetParm(int groupNo, String orderCode) {
		return getPrescriptionList().getOrderSetParm(groupNo, orderCode);
	}

	/**
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String args[]) {
		com.javahis.util.JavaHisDebug.initClient();
		TIOM_AppServer.resetAction();
		Pat pat = Pat.onQueryByMrNo("20");
		Reg reg = Reg.onQueryByCaseNo(pat, "090108000006");
		OPB opb = OPB.onQueryByCaseNo(reg);
		// System.out.println(opb);
		// System.out.println(opb.getOrderParm());
		// ((Order)opb.getOrderParm(true,"").getData("OBJECT",0)).modifyChargeFlg(true);
		// ((Order)opb.getOrderParm(true,"").getData("OBJECT",1)).modifyChargeFlg(true);
		// order.modifyChargeFlg(false);
		// System.out.println(opb.getFee());
		// System.out.println(opb.getPrescriptionList().getParm());
		// System.out.println(opb.getChargeParm());
		// System.out.println(opb.getBilInvoice().getParm());
		// opb.getPrescriptionComb();
		// System.out.println("  �վݴ���"+BIL.getRexpCode("101"));
		// BIL.getRexpCode("101");
		// System.out.println("11".compareTo("22"));
	}

	/**
	 * �õ������������ ҽ���ָ��������
	 * 
	 * @param flg
	 *            boolean
	 * @param cat1Type
	 *            String
	 * @return TParm
	 * 
	 */
	public TParm getInsCcbParm(boolean flg, TParm tparm) {
		TParm parm = new TParm();
		// ȡ���е�order
		// List list = getPrescriptionList().getOrder();
		int count = tparm.getCount();
		TParm parmSum = new TParm();
		double billAmt = 0.00;// δ�շ�ҽ�����
		double sumAmt = 0.00;// δ�շ�ҽ�����
		TParm result = null;
		for (int i = 0; i < count; i++) {
			// ȡһ��order
			// Order order = (Order) list.get(i);
			// parm.setData("MR_NO", );
			/*
			 * if (null!=order.getPrintFlg() && order.getPrintFlg().equals("Y"))
			 * { continue; }
			 */
			// ���δ�շѲ���û�й�ѡ��ִ���շѲ���
			// =============pangben 2012-4-3
			// ChargeFlg =N && BILL_FLG=N ��ִ��
			parmSum.addData("CAT1_TYPE", tparm.getData("CAT1_TYPE", i));// ���˼����飬ҽ���޸�ҽ��ҽ�ƿ��շ�ʱʹ��
			parmSum.addData("RX_NO", tparm.getData("RX_NO", i));
			parmSum.addData("MED_APPLY_NO", tparm.getData("MED_APPLY_NO", i));// ������
			parmSum.addData("SETMAIN_FLG", tparm.getData("SETMAIN_FLG", i));// ����ҽ��
			parmSum.addData("SEQ_NO", tparm.getData("SEQ_NO", i));
			parmSum.addData("BILL_FLG", "Y");

			if (tparm.getData("ORDERSET_CODE", i) != null
					&& tparm.getData("ORDERSET_CODE", i).equals(
							tparm.getData("ORDER_CODE", i))) {
				continue;
			}
			if (null != tparm.getData("BILL_FLG", i)
					&& "N".equals(tparm.getData("BILL_FLG", i))) {// δ�շ�ҽ��
				billAmt += tparm.getDouble("AR_AMT", i);
			}
			sumAmt += tparm.getDouble("AR_AMT", i);
			parm.addData("RX_NO", tparm.getData("RX_NO", i));
			parm.addData("ORDER_CODE", tparm.getData("ORDER_CODE", i));
			parm.addData("NHI_CODE_O", tparm.getData("NHI_CODE_O", i));
			parm.addData("SEQ_NO", tparm.getData("SEQ_NO", i));
			parm.addData("AMT", tparm.getData("AMT", i));
			parm.addData("AR_AMT", tparm.getData("AR_AMT", i));
			parm.addData("EXEC_FLG", tparm.getData("EXEC_FLG", i));
			parm.addData("RECEIPT_FLG", tparm.getData("RECEIPT_FLG", i));
			parm.addData("OWN_PRICE", tparm.getData("OWN_PRICE", i));
			parm.addData("QTY", tparm.getData("QTY", i));
			parm.addData("DOSAGE_QTY", tparm.getData("DOSAGE_QTY", i));
			parm.addData("SPECIFICATION", tparm.getData("SPECIFICATION", i));
			parm.addData("TAKE_DAYS", tparm.getData("TAKE_DAYS", i));
			parm.addData("DR_NOTE", tparm.getData("DR_NOTE", i));
			parm.addData("ORDERSET_CODE", tparm.getData("ORDERSET_CODE", i));
			parm.addData("HIDE_FLG", tparm.getData("HIDE_FLG", i));
			parm.addData("NHI_CODE_O", tparm.getData("NHI_CODE_O", i));
			parm.addData("NHI_CODE_E", tparm.getData("NHI_CODE_E", i));
			parm.addData("NHI_CODE_I", tparm.getData("NHI_CODE_I", i));
			parm.addData("ORDER_DESC", (String) tparm.getData("ORDER_DESC", i)
					+ (String) tparm.getData("SPECIFICATION", i));
			TParm insparm = new TParm();
			insparm.setData("BILL_D", SystemTool.getInstance().getDate());
			insparm.setData("INS_CODE", tparm.getData("NHI_CODE_O", i));
			result = INSIbsTool.getInstance().queryInsIbsOrderByInsRule(parm);
			if (result.getErrCode() < 0) {
				return result;
			}
			parm.addData("YF", result.getValue("YF", 0));// �÷�
			parm.addData("ZFBL1", result.getValue("ZFBL1", 0));// �Ը�����
			parm.addData("PZWH", result.getValue("PZWH", 0));// ��׼��
			if (tparm.getBoolean("HIDE_FLG", i)) {
				if (flg) {
					parm.addData("BILL_FLG", "N");
					parm.setData("BUSINESS_TYPE", "OPBT");
					parm.setData("TYPE_FLG", "Y");// ҽ�ƿ��շ�����EKT_TREDE state ֵ�ж�
					// STATE=3 �˷�
				} else {
					parm.addData("BILL_FLG", "Y");
					parm.setData("BUSINESS_TYPE", "OPB");
				}
			} else
				parm.addData("BILL_FLG", tparm.getData("BILL_FLG", i));

		}
		parm.setData("sumAmt", sumAmt);// ���н�� �����շ�δ�շ�
		parm.setData("billAmt", billAmt);// δ�շ�ҽ�����
		parm.setData("parmSum", parmSum.getData());// ��������ҽ������޸�OPD_ORDER
													// MED_APPLYʹ��
		return parm;
	}
/**
 * �A�_�z����\̖set��get����
 * yanjing 20131217
 * @return
 */

	public List<String> getPreCaseNos() {
		return preCaseNos;
	}

	public void setPreCaseNos(List<String> preCaseNos) {
		this.preCaseNos = preCaseNos;
	}
	
	/**
	 * ��ô������
	 * @param orderCode
	 * @return
	 */
	public static boolean getPrescriptionFlg(String orderCode){
		boolean flg = false;
		String sql = "SELECT PRESCRIPTION_FLG FROM PHA_BASE WHERE ORDER_CODE ='"+orderCode+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() > 0){
			flg = !parm.getBoolean("PRESCRIPTION_FLG", 0);
		}
		return flg;
	}
	
}
