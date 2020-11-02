package com.javahis.ui.ins;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;


import jdo.ins.INSADMConfirmTool;
import jdo.ins.INSIbsOrderTool;
import jdo.ins.INSIbsTool;
import jdo.ins.INSIbsUpLoadTool;
import jdo.ins.INSTJTool;
import jdo.ins.InsManager;
import jdo.sys.CTZTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
/**
 * <p>
 * Title:����渶�ָ�
 * </p>
 * 
 * <p>
 * Description:����渶�ָ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author zhangp 2012-2-13
 * @version 1.0
 */
public class INSBalance_DFControl extends TControl{
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	TTable tableInfo;// ����������Ϣ�б�
	TTable oldTable;// ���÷ָ�ǰ����
	TTable newTable;// ���÷ָ������
	TParm regionParm;// ҽ���������
	int radio = 1;//1����סԺ������	2��ȫ������
	TTabbedPane tabbedPane;// ҳǩ
	// ���÷ָ�ǰ�������
	private String[] pageFour = { "ORDER_CODE", "ORDER_DESC", "DOSE_DESC",
			"STANDARD", "PHAADD_FLG", "CARRY_FLG", "PRICE",
			"NHI_ORD_CLASS_CODE", "NHI_CODE_I", "OWN_PRICE", "BILL_DATE" };
	// ���÷ָ��������
	private String[] pageFive = { "SEQ_NO", "ORDER_CODE", "ORDER_DESC",
			"DOSE_CODE", "STANDARD", "PHAADD_FLG", "CARRY_FLG", "PRICE",
			"NHI_ORDER_CODE", "NHI_ORD_CLASS_CODE", "NHI_FEE_DESC",
			"OWN_PRICE", "CHARGE_DATE" };
	
	/**
     * ��ʼ������
     */
    public void onInit() {
		tableInfo = (TTable) this.getComponent("TABLEINFO");// ����������Ϣ�б�
		tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE");// ҳǩ
		oldTable = (TTable) this.getComponent("OLD_TABLE");// ���÷ָ�ǰ����
		newTable = (TTable) this.getComponent("NEW_TABLE");// ���÷ָ������
		setValue("START_DATE", SystemTool.getInstance().getDate());
		setValue("END_DATE", SystemTool.getInstance().getDate());
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// ���ҽ���������
		setValue("NHI_ORD_CLASS_CODE", "01");
    }
	/**
	 * У��Ϊ�շ���
	 * 
	 * @param name
	 * @param message
	 */
	private void onCheck(String name, String message) {
		this.messageBox(message);
		this.grabFocus(name);
	}
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		if (null == this.getValue("START_DATE")
				|| this.getValue("START_DATE").toString().length() <= 0) {
			onCheck("START_DATE", "��Ժ��ʼʱ�䲻����Ϊ��");
			return;
		}
		if (null == this.getValue("END_DATE")
				|| this.getValue("END_DATE").toString().length() <= 0) {
			onCheck("END_DATE", "��Ժ����ʱ�䲻����Ϊ��");
			return;
		}

		if (((Timestamp) this.getValue("START_DATE")).after(((Timestamp) this
				.getValue("END_DATE")))) {
			this.messageBox("��ʼʱ�䲻���Դ��ڽ���ʱ��");
			return;
		}
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("START_DATE", sdf.format(this.getValue("START_DATE")));// ��Ժʱ��
		parm.setData("END_DATE", sdf.format(this.getValue("END_DATE")));// ��Ժ����ʱ��
		TParm result = INSADMConfirmTool.getInstance().INS_DF_Seq(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// ִ��ʧ��
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("û�в�ѯ������");
			tableInfo.removeRowAll();
			return;
		}
		tableInfo.setParmValue(result);
	}
	/**
	 * ���ص渶סԺ˳���
	 */
	public void onDownload(){
		if (getValue("START_DATE").equals("") ||
				getValue("END_DATE").equals("")) {
		      messageBox("��ʼ���ںͽ������ڲ���Ϊ��");
		      return;
		    }
		    if (radio == 1&&getValueString("CASE_NO").equals("")) {
		      messageBox("��סԺ�����ص渶˳���ʱ���Ǽ�סԺ�Ų���Ϊ��");
		      return;
		    }
		    String case_no = " ";
		    if (radio == 1) {
		      case_no = getValueString("CASE_NO");
		    }
		    String startDate = getValueString("START_DATE").substring(0, 19);;
	    	String endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10)+"000000";
			endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10)+"235959";
		    TParm parm = new TParm();
	    	TParm regionParm = SYSRegionTool.getInstance().selectdata(Operator.getRegion());
	    	String hospital =  regionParm.getData("NHI_NO", 0).toString();//��ȡHOSP_NHI_NO
		    parm.setData("PIPELINE", "DataDown_czyd");
		    parm.setData("PLOT_TYPE", "M");
		    parm.addData("HOSP_NHI_NO", hospital); //ҽԺ����
		    parm.addData("CASE_NO", case_no);
		    parm.addData("BEGIN_DATE", startDate);
		    parm.addData("END_DATE", endDate);
		    parm.addData("CTZ_TYPE", "21"); //סԺ
		    parm.addData("ADVANCE_TYPE", "01"); //�����渶
		    parm.addData("PARM_COUNT", 6); //��������
		    TParm resultParm = InsManager.getInstance().safe(parm,"");
		    if (resultParm == null) {
		      messageBox("����ʧ��");
		      return;
		    }
		      this.insert_DF_confrim_no(resultParm);
		    messageBox("���سɹ�");
	}
	/**
	 * radio������
	 * @param i
	 */
	public void onSelect(int i){
		radio = i;
		clearValue("CASE_NO");
		TTextField case_no = (TTextField) getComponent("CASE_NO");
		if(i == 1){
			case_no.setEnabled(true);
		}
		if(i == 2){
			case_no.setEnabled(false);
		}
	}
	/**
	 * ����INS_ADVANCE_PAYMENT
	 * @param parm
	 */
	public void insert_DF_confrim_no(TParm parm){
		TParm p = new TParm();
		for (int i = 0; i < parm.getCount("CONFIRM_NO"); i++){
			p.setData("CONFIRM_NO", parm.getData("CONFIRM_NO", i));//�渶סԺ˳���
			p.setData("SID", parm.getData("SID", i));//���֤����
			p.setData("NAME", parm.getData("NAME", i));//����
			p.setData("SEX_CODE", parm.getData("SEX_CODE", i));//�Ա�
			p.setData("WORK_DEPARTMENT", parm.getData("WORK_DEPARTMENT", i));//��λ����
			p.setData("CTZ_CODE", parm.getData("CTZ_CODE", i));//��Ա���
			p.setData("IN_HOSP_DATE", parm.getData("IN_HOSP_DATE", i));//��Ժʱ��
			p.setData("OUT_HOSP_DATE", parm.getData("OUT_HOSP_DATE", i));//��Ժʱ��
			p.setData("CASE_NO", parm.getData("CASE_NO", i));//סԺ��
			p.setData("REG_TOT_AMT", parm.getData("REG_TOT_AMT", i));//�����ܶ�
			p.setData("ADVANCE_TYPE", parm.getData("ADVANCE_TYPE", i));//�渶����
			p.setData("CONTACT_ADDR", parm.getData("CONTACT_ADDR", i));//��ϵ��λ
			p.setData("CONTACT_TYPE", parm.getData("CONTACT_TYPE", i));//��ϵ��ʽ
			p.setData("CONTACTS", parm.getData("CONTACTS", i));//��ϵ��
			p.setData("OPT_USER", Operator.getID());
			p.setData("OPT_DATE", SystemTool.getInstance().getDate());
			p.setData("OPT_TERM", Operator.getIP());
			TParm result = INSADMConfirmTool.getInstance().insertAdvancePayment(p);
			if(result.getErrCode()<0){
				messageBox(result.getErrText());
				return;
			}
		}
		onQuery();
	}
	/**
	 * ת�걨
	 */
	public void onApply() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		parm.setData("TYPE", "H");// M :ת������Ϣ���� ,H :ת�걨���� A : �Զ�
		parm.setData("REGION_CODE", Operator.getRegion());// ҽԺ����
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("END_DATE", sdf.format(SystemTool.getInstance().getDate()));// ϵͳʱ��
		TParm result = TIOM_AppServer.executeAction(
											"action.ins.INSBalanceAction", "changeReport", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		String Msg = "ת�����\n" + "�ɹ�����:" + result.getValue("SUCCESS_INDEX")
				+ "\n" + "ʧ�ܱ���:" + result.getValue("ERROR_INDEX");
		this.messageBox(Msg);
	}
	/**
	 * У���Ƿ��л�ý���
	 * 
	 * @return
	 */
	private TParm getTableSeleted() {
		int row = tableInfo.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫִ�е�����");
			tabbedPane.setSelectedIndex(0);
			return null;
		}
		TParm parm = tableInfo.getParmValue().getRow(row);
		parm.setData("YEAR_MON", parm.getValue("IN_DATE").replace("/", ""));// �ں�
		parm.setData("CASE_NO", parm.getValue("CASE_NO"));// �������
		parm.setData("ADM_SEQ", parm.getValue("ADM_SEQ"));// �渶˳���
//		parm.setData("START_DATE", parm.getValue("IN_DATE").replace("/", ""));// ��ʼʱ��
		parm.setData("MR_NO", parm.getValue("MR_NO"));
		parm.setData("PAT_AGE", parm.getValue("PAT_AGE"));// ����
		return parm;
	}
	
	/**
	 * ���÷ָ�ִ�в���
	 */
	public void onUpdate() {
		TParm parm = getTableSeleted();
		if (parm == null) {
			return;
		}
//		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
//			return;
//		}
		if (!this.CheckTotAmt()) {
		} else {
			feePartitionEnable(false);
			updateRun();// ׼���ϴ�ҽ��
			update1();// �ۼ�����
			feePartitionEnable(true);
		}

	}
	
	/**
	 * ���÷ָ�ִ���Ժ����ݱȽ�
	 * 
	 * @return
	 */
	public boolean CheckTotAmt() {
		TParm parm = getTableSeleted();
		if (null != parm) {
			TParm ibsUpLoadParm = INSIbsUpLoadTool.getInstance()
					.queryCheckSumIbsUpLoad(parm);
			if (ibsUpLoadParm.getErrCode() < 0) {
				return false;
			}
			TParm ibsOrderParm = INSIbsOrderTool.getInstance()
					.queryCheckSumIbsOrder(parm);
			if (ibsOrderParm.getErrCode() < 0) {
				return false;
			}
			if (ibsUpLoadParm.getDouble("TOTAL_AMT", 0) == ibsOrderParm
					.getDouble("TOTAL_AMT", 0)) {
				return true;
			} else {
				if (this.messageBox("��Ϣ", "���÷ָ�ǰ�ͷ��÷ָ���ܷ��ò�����Ƿ����", 2) == 0) {
					return true;
				}
				return false;
			}
		}
		return true;
	}
	/**
	 * ���÷ָ�����а�ť�û�
	 * 
	 * @param enAble
	 */
	private void feePartitionEnable(boolean enAble) {
		callFunction("UI|save|setEnabled", enAble);
		callFunction("UI|new|setEnabled", enAble);
		callFunction("UI|delete|setEnabled", enAble);
		callFunction("UI|query|setEnabled", enAble);
		callFunction("UI|changeInfo|setEnabled", enAble);
		callFunction("UI|apply|setEnabled", enAble);
		callFunction("UI|onSave|setEnabled", enAble);
		for (int i = 1; i < 11; i++) {
			callFunction("UI|NEW_RDO_" + i + "|setEnabled", enAble);
		}

	}
	/**
	 * ׼���ϴ�ҽ��
	 * �ǾӲ�����Ҫ�ж��Ƿ� ȡ��ҽ���Ƿ��Ƕ�ͯ��ҩ���ͯ������Ŀ
	 * 
	 * �����ֲ��� INS_IBS�޸Ĵ�λ���������ҽ�ò��Ϸ�������
	 */
	private void updateRun() {
		TParm commParm = getTableSeleted();
		if (null == commParm) {

			return;
		}
		TParm parmValue = newTable.getParmValue();// ��÷��÷ָ��������
		double bedFee = regionParm.getDouble("TOP_BEDFEE", 0);
		boolean flg = false;// �����Ϣ��ܿ� �ж��Ƿ�ָ�ɹ�
		TParm tableParm = null;
		TParm newParm = new TParm();// �ۼ�����
		TParm ctzParm=null;
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
			String nhiOrderCode = tableParm.getValue("NHI_ORDER_CODE");
			// �ۼ���������ʱ�����ݿ�����һ��ҽ��Ϊ***018������
			if ("***018".equals(nhiOrderCode) || nhiOrderCode.equals("")) {// ҽ������
				continue;
			}
			if (nhiOrderCode.length() > 4) {
				String billdate = tableParm.getValue("CHARGE_DATE").replace("/", "");// ��ϸ������ʱ��
				TParm parm = new TParm();
				ctzParm=CTZTool.getInstance().getNhiNoCtz(this.getValueString("CTZ1_CODE"));
				parm.setData("NHI_ORDER_CODE", nhiOrderCode);// ҽ��ҽ������
				parm.setData("CTZ1_CODE", ctzParm.getValue("NHI_NO",0));// ���
				parm.setData("QTY", tableParm.getValue("QTY"));// ����
				parm.setData("TOTAL_AMT", tableParm.getValue("TOTAL_AMT"));// �ܽ��
				parm.setData("TIPTOP_BED_AMT", bedFee);// ��ߴ�λ��
				parm.setData("PHAADD_FLG", tableParm.getValue("PHAADD_FLG")
						.equals("Y") ? "1" : "0");// ҩƷ����ע��
				parm.setData("FULL_OWN_FLG", tableParm.getValue("FULL_OWN_FLG")
						.equals("Y") ? "0" : "1");// ȫ�Էѱ�־
				parm.setData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));// ҽ���������
				parm.setData("CHARGE_DATE", billdate);// ���÷���ʱ��
				TParm splitParm = new TParm();
				if (this.getValueInt("INS_CROWD_TYPE") == 1) {// 1.��ְ 2.�Ǿ�
					// סԺ������ϸ�ָ�
					splitParm = INSTJTool.getInstance().DataDown_sp1_B(parm);

				} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
					// סԺ������ϸ�ָ�
					commParm.setData("SFXMBM",nhiOrderCode);
					commParm.setData("CHARGE_DATE",billdate+"000000");//У���������ڵ�����
					TParm tempParm=INSIbsTool.getInstance().queryInsRuleET(commParm);
					if (tempParm.getErrCode()<0) {
						break;
					}
					if (null==commParm.getValue("PAT_AGE") || null==tempParm || null==tempParm.getValue("ETYYBZ",0)) {
						break;
					}
					//�����ļ�Ҫ����18����Ϊ�жϱ�־,�����Ƕ�ͯ��ҩ  ETYYBZ=1 ������ִ��
					if (commParm.getInt("PAT_AGE")>18 && 
							tempParm.getValue("ETYYBZ",0).equals("1")) {
						System.out.println("����ִ�з��÷ָ�ǾӲ���");
					}else{
						splitParm = INSTJTool.getInstance().DataDown_sp1_G(parm);
					}
				}
				if (!INSTJTool.getInstance().getErrParm(splitParm)) {
					flg = true;
					this.messageBox(parmValue.getValue("SEQ_NO", i) + "�зָ�ʧ��");
					break;
				}
				// �ۼ����ݲ���
				setIbsUpLoadParm(tableParm, splitParm, newParm);
			} else {
				this.messageBox("����" + parmValue.getValue("SEQ_NO", i)
						+ "��ҽ������");// ���
			}

		}
		newParm.setData("OPT_USER",Operator.getID());
		newParm.setData("OPT_TERM",Operator.getIP());
//		newParm.setData("TYPE",type);//�ж�ִ������ ��SINGLE:�����ֲ���
		newParm.setData("CASE_NO", commParm.getValue("CASE_NO"));//�����ֲ���ʹ��
		newParm.setData("YEAR_MON", commParm.getValue("YEAR_MON"));//�ںŵ����ֲ���ʹ��
		//ִ���޸�INS_IBS_UPLOAD�����
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "onSaveInsUpLoad", newParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (flg) {
			this.messageBox("�ָ�ʧ��");
		} else {
			this.messageBox("�ָ�ɹ�");
		}
	}
	/**
	 * �ۼ�����
	 */
	private void update1() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// �ۼ����� ��ѯ�˲��������¼���ܽ��
		// ��ְ ���� ��ѯ������Ӧ�����ۼ�����ΪY������
		TParm result = INSIbsUpLoadTool.getInstance().querySumIbsUpLoad(parm);
		if (result.getErrCode() < 0) {
			return;
		}
		TParm splitParm = new TParm();
		splitParm.setData("ADDPAY_ADD", result.getDouble("TOTAL_AMT", 0));
		splitParm.setData("HOSP_START_DATE", parm.getValue("YEAR_MON"));
		if (this.getValueInt("INS_CROWD_TYPE") == 1) {// 1.��ְ 2.�Ǿ�
			// ��ְ�ۼ�����
			splitParm = INSTJTool.getInstance().DataDown_sp1_C(splitParm);
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
			// �Ǿ� סԺ�ۼ���������
			splitParm = INSTJTool.getInstance().DataDown_sp1_H(splitParm);
		}
		if (!INSTJTool.getInstance().getErrParm(splitParm)) {
			return;
		}
		TParm exeParm = new TParm();
		exeParm.setData("NHI_AMT", splitParm.getDouble("NHI_AMT"));// �걨���
		exeParm.setData("TOTAL_AMT", result.getDouble("TOTAL_AMT", 0));// �������
		exeParm.setData("TOTAL_NHI_AMT", result.getDouble("TOTAL_NHI_AMT", 0));// ҽ�����
		exeParm.setData("ADD_AMT", splitParm.getDouble("ADDPAY_AMT"));// �ۼ��������
		exeParm.setData("ADDPAY_AMT", splitParm.getDouble("ADDPAY_AMT"));// �ۼ��������
		exeParm.setData("OWN_AMT", splitParm.getDouble("OWN_AMT"));// �Էѽ��
		exeParm.setData("CASE_NO", this.getValue("CASE_NO"));// �������
		exeParm.setData("REGION_CODE", Operator.getRegion());// ����
		// ��ѯ���SEQ_NO
		TParm maxSeqParm = INSIbsUpLoadTool.getInstance().queryMaxIbsUpLoad(
				parm);
		if (maxSeqParm.getErrCode() < 0) {
			return;
		}
		exeParm.setData("SEQ_NO", maxSeqParm.getInt("SEQ_NO", 0) + 1);// ˳���
		exeParm.setData("DOSE_CODE", "");// ����
		exeParm.setData("STANDARD", "");// ���
		exeParm.setData("PRICE", 0);// ����
		exeParm.setData("QTY", 0);// ����
		exeParm.setData("ADM_SEQ", maxSeqParm.getValue("ADM_SEQ", 0));// ҽ�������
		exeParm.setData("OPT_USER", Operator.getID());// ID
		exeParm.setData("OPT_TERM", Operator.getIP());
		exeParm.setData("HYGIENE_TRADE_CODE", maxSeqParm.getValue(
				"HYGIENE_TRADE_CODE", 0));// ��׼�ĺ�
		exeParm.setData("ORDER_CODE", "***018");// ҽ������
		exeParm.setData("NHI_ORDER_CODE", "***018");// ҽ��ҽ������
		exeParm.setData("ORDER_DESC", "һ���Բ����ۼ�����");
		exeParm.setData("ADDPAY_FLG", "Y");// �ۼ�������־��Y���ۼ�������N�����ۼ�������
		exeParm.setData("PHAADD_FLG", "N");// ����ҩƷ
		exeParm.setData("CARRY_FLG", "N");// ��Ժ��ҩ
		exeParm.setData("OPT_TERM", Operator.getIP());//
		exeParm.setData("NHI_ORD_CLASS_CODE", "06");// ͳ�ƴ��� 
		exeParm.setData("CHARGE_DATE", SystemTool.getInstance().getDateReplace(
				result.getValue("CHARGE_DATE", 0), true));// ��ϸ¼��ʱ��
		exeParm.setData("YEAR_MON", parm.getValue("YEAR_MON"));// �ں�
		result = TIOM_AppServer.executeAction("action.ins.INSBalanceAction",
				"onAdd", exeParm);
		if (result.getErrCode() < 0) {
			this.messageBox("ִ���ۼ�����ʧ��");
			return;
		}
	}
	/**
	 * ���÷ָ� �ۼ����� ���INS_IBS_UPLOAD �����
	 * 
	 * @param tableParm
	 * @param newParm
	 */
	private void setIbsUpLoadParm(TParm tableParm, TParm splitParm,
			TParm newParm) {
		newParm.addData("ADM_SEQ", tableParm.getValue("ADM_SEQ"));//����˳���
		newParm.addData("SEQ_NO", tableParm.getValue("SEQ_NO"));//���
		newParm.addData("CHARGE_DATE", SystemTool.getInstance()
				.getDateReplace(tableParm.getValue("CHARGE_DATE"), true));// ��ϸ������ʱ��
		newParm.addData("ADDPAY_AMT", splitParm.getValue("ADDPAY_AMT"));// �������
		newParm.addData("TOTAL_NHI_AMT", splitParm.getValue("NHI_AMT"));// �걨���
		newParm.addData("OWN_AMT", splitParm.getValue("OWN_AMT"));// ȫ�Էѽ��
		newParm.addData("OWN_RATE", splitParm.getValue("OWN_RATE"));// �Ը�����
		newParm.addData("NHI_ORD_CLASS_CODE", splitParm
				.getValue("NHI_ORD_CLASS_CODE"));// ͳ�ƴ���
		newParm.addData("ADDPAY_FLG", splitParm.getValue("ADDPAY_FLG")
				.equals("Y") ? "1" : "0");// �ۼ�������־

	}
	/**
	 * ҳǩ����¼�
	 */
	public void onChangeTab() {
		switch (tabbedPane.getSelectedIndex()) {
		// 1 :���÷ָ�ǰҳǩ2�����÷ָ��ҳǩ
		case 1:
			onSplitOld();
			break;
		case 2:
			onSplitNew();
			break;
		}
	}
	private void onSplitOld(boolean flg) {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// ͳ�ƴ����ѯ��01 ҩƷ�ѣ�02 ���ѣ�03 ���Ʒѣ�04�����ѣ�05��λ�ѣ�06���Ϸѣ�07�����ѣ�08ȫѪ�ѣ�09�ɷ�Ѫ��
		String classCode = getValueString("NHI_ORD_CLASS_CODE");
		parm.setData("NHI_ORD_CLASS_CODE", classCode);
		parm.setData("YEAR_MON", parm.getValue("YEAR_MON").substring(0, 4)+parm.getValue("YEAR_MON").substring(5, 7));
		TParm result = INSIbsOrderTool.getInstance().queryOldSplit(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (flg) {
			if (result.getCount() <= 0) {
				oldTable.acceptText();
				oldTable.setDSValue();
				oldTable.removeRowAll();
				this.messageBox("û�в�ѯ������");
				return;
			}
		} else {
			if (result.getCount() <= 0) {
				oldTable.acceptText();
				oldTable.setDSValue();
				oldTable.removeRowAll();
				return;
			}
		}
		double qty = 0.00;// ����
		double totalAmt = 0.00;// �������
		double totalNhiAmt = 0.00;// �걨���
		double ownAmt = 0.00;// �Էѽ��
		double addPayAmt = 0.00;// �������
		for (int i = 0; i < result.getCount(); i++) {
			qty += result.getDouble("QTY", i);
			totalAmt += result.getDouble("TOTAL_AMT", i);
			totalNhiAmt += result.getDouble("TOTAL_NHI_AMT", i);
			ownAmt += result.getDouble("OWN_AMT", i);
			addPayAmt += result.getDouble("ADDPAY_AMT", i);
		}

		// //��Ӻϼ�
		for (int i = 0; i < pageFour.length; i++) {
			if (i == 0) {
				result.addData(pageFour[i], "�ϼ�:");
				continue;
			}
			result.addData(pageFour[i], "");
		}
		result.addData("QTY", qty);
		result.addData("TOTAL_AMT", totalAmt);
		result.addData("TOTAL_NHI_AMT", totalNhiAmt);
		result.addData("OWN_AMT", ownAmt);
		result.addData("ADDPAY_AMT", addPayAmt);
		result.setCount(result.getCount() + 1);
		oldTable.setParmValue(result);
	}
	/**
	 * ���÷ָ�ǰ����
	 */
	public void onSplitOld() {
		onSplitOld(true);

	}
	/**
	 * ���÷ָ������
	 */
	public void onSplitNew() {
		onSplitNew(true);
	}
	private void onSplitNew(boolean flg) {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// ͳ�ƴ����ѯ��01 ҩƷ�ѣ�02 ���ѣ�03 ���Ʒѣ�04�����ѣ�05��λ�ѣ�06���Ϸѣ�07�����ѣ�08ȫѪ�ѣ�09�ɷ�Ѫ��
			String classCode = getValueString("NHI_ORD_CLASS_CODE");
			parm.setData("NHI_ORD_CLASS_CODE", classCode);
			parm.setData("YEAR_MON", parm.getValue("YEAR_MON").substring(0, 4)+parm.getValue("YEAR_MON").substring(5, 7));
		TParm upLoadParmOne = INSIbsUpLoadTool.getInstance()
				.queryNewSplit(parm);
		if (upLoadParmOne.getErrCode() < 0) {
			this.messageBox("E0005");// ִ��ʧ��
			return;
		}
		TParm upLoadParmTwo = INSIbsUpLoadTool.getInstance()
				.queryNewSplitUpLoad(parm);
		if (upLoadParmTwo.getErrCode() < 0) {
			this.messageBox("E0005");// ִ��ʧ��
			return;
		}
		if (flg) {
			if (upLoadParmOne.getCount() == 0 && upLoadParmTwo.getCount() == 0) {
				newTable.acceptText();
				newTable.setDSValue();
				newTable.removeRowAll();
				this.messageBox("û�в�ѯ������");
				callFunction("UI|upload|setEnabled", false);// û�����ݲ�����ִ�зָ����
				return;
			}
		} else {
			if (upLoadParmOne.getCount() == 0 && upLoadParmTwo.getCount() == 0) {
				newTable.acceptText();
				newTable.setDSValue();
				newTable.removeRowAll();
				callFunction("UI|upload|setEnabled", false);// û�����ݲ�����ִ�зָ����
				return;
			}
		}

		if (null == upLoadParmOne) {
			upLoadParmOne = new TParm();
		}
		// �ϲ�����
		if (upLoadParmTwo.getCount() > 0) {
			for (int i = 0; i < upLoadParmTwo.getCount(); i++) {
				upLoadParmOne.setRowData(upLoadParmOne.getCount() + 1,
						upLoadParmTwo, i);
			}

		}
		upLoadParmOne.setCount(upLoadParmOne.getCount() + 1);
		double qty = 0.00;// ����
		double totalAmt = 0.00;// �������
		double totalNhiAmt = 0.00;// �걨���
		double ownAmt = 0.00;// �Էѽ��
		double addPayAmt = 0.00;// �������
		for (int i = 0; i < upLoadParmOne.getCount(); i++) {
			if (upLoadParmOne.getValue("ORDER_CODE").equals("***018")) {//�ϴ�ҽ���������ۼƽ��
				continue;
			}
			qty += upLoadParmOne.getDouble("QTY", i);
			totalAmt += upLoadParmOne.getDouble("TOTAL_AMT", i);
			totalNhiAmt += upLoadParmOne.getDouble("TOTAL_NHI_AMT", i);
			ownAmt += upLoadParmOne.getDouble("OWN_AMT", i);
			addPayAmt += upLoadParmOne.getDouble("ADDPAY_AMT", i);
		}

		// //��Ӻϼ�
		for (int i = 0; i < pageFive.length; i++) {
			if (i == 1) {
				upLoadParmOne.addData(pageFive[i], "�ϼ�:");
				continue;
			}
			upLoadParmOne.addData(pageFive[i], "");
		}
		upLoadParmOne.addData("QTY", qty);
		upLoadParmOne.addData("TOTAL_AMT", totalAmt);
		upLoadParmOne.addData("TOTAL_NHI_AMT", totalNhiAmt);
		upLoadParmOne.addData("OWN_AMT", ownAmt);
		upLoadParmOne.addData("ADDPAY_AMT", addPayAmt);
		upLoadParmOne.addData("ADM_SEQ", "");// ����˳��� ����
		upLoadParmOne.addData("FLG", "");// ��������
		upLoadParmOne.addData("HYGIENE_TRADE_CODE", "");// ����׼��
		upLoadParmOne.addData("CHARGE_DATE", "");
		upLoadParmOne.addData("ADDPAY_FLG", "");
		upLoadParmOne.setCount(upLoadParmOne.getCount() + 1);
		// ��Ӻϼ�
		newTable.setParmValue(upLoadParmOne);
		callFunction("UI|upload|setEnabled", true);
	}
	/**
	 * ���÷ָ����ϸ���ݱ������
	 */
	public void onSave() {
		TParm parm = newTable.getParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("û����Ҫ���������");
			return;
		}
		parm.setData("OPT_USER", Operator.getID());// id
		parm.setData("OPT_TERM", Operator.getIP());// Ip
		parm.setData("REGION_CODE", Operator.getRegion());// �������
		// ִ�����INS_IBS_UPLOAD�����
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "updateUpLoad", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
			onSplitNew(false);
		}
	}
	/**
	 * ���÷ָ����ϸ�����½�����
	 */
	public void onNew() {
		String[] amtName = { "PRICE", "QTY", "TOTAL_AMT", "TOTAL_NHI_AMT",
				"OWN_AMT", "ADDPAY_AMT" };
		TParm parm = newTable.getParmValue();
		TParm result = new TParm();
		// ���һ��������
		for (int i = 0; i < pageFive.length; i++) {
			result.setData(pageFive[i], "");
		}
		for (int j = 0; j < amtName.length; j++) {
			result.setData(amtName[j], "0.00");
		}

		result.setData("FLG", "Y");// ��������
		if (parm.getCount() > 0) {
			// ��úϼ�����
			result.setData("ADM_SEQ", parm.getValue("ADM_SEQ", 0));// ����˳��� ����
			result.setData("HYGIENE_TRADE_CODE", parm.getValue(
					"HYGIENE_TRADE_CODE", 0));// ����׼��
			TParm lastParm = parm.getRow(parm.getCount() - 1);
			parm.removeRow(parm.getCount() - 1);// �Ƴ��ϼ�
			int seqNo = -1;// ������˳�����
			for (int i = 0; i < parm.getCount(); i++) {
				if (null != parm.getValue("SEQ_NO", i)
						&& parm.getValue("SEQ_NO", i).length() > 0) {
					if (parm.getInt("SEQ_NO", i) > seqNo) {
						seqNo = parm.getInt("SEQ_NO", i);
					}
				}
			}
			result.setData("SEQ_NO", seqNo + 1);// ˳���
			parm.setRowData(parm.getCount(), result, -1);// ����½�������
			parm.setCount(parm.getCount() + 1);
			parm.setRowData(parm.getCount(), lastParm, -1);// ���ϼ����·���
			parm.setCount(parm.getCount() + 1);
		} else {
			this.messageBox("û�����ݲ������½�����");
			return;
			// result.setData("ADM_SEQ",parm.getValue("ADM_SEQ",0));//����˳��� ����
			// parm.setRowData(parm.getCount(),result,-1);
			// parm.setCount(parm.getCount()+1);
		}
		newTable.setParmValue(parm);
	}
	/**
	 * ���÷ָ����ϸ����ɾ������
	 */
	public void onDel() {
		int row = newTable.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫɾ��������");
			return;

		}
		TParm parm = newTable.getParmValue();
		if (parm.getValue("FLG", row).trim().length() <= 0) {
			this.messageBox("������ɾ���ϼ�����");
			return;
		}
		TParm result = INSIbsUpLoadTool.getInstance().deleteINSIbsUploadSeq(
				parm.getRow(row));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// ִ��ʧ��
			return;
		}
		this.messageBox("P0005");// ִ�гɹ�
		onSplitNew(false);
	}

}
