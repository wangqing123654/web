package com.javahis.ui.ins;

import java.sql.Timestamp;

import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSTJFlow;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * 
 * Title: �������
 * 
 * Description:������ˣ���ѯ������Ϣ
 * 
 * Copyright: BlueCore (c) 2011
 * 
 * @author pangben 2012-1-8
 * @version 2.0
 */
public class INSChooseVisitControl extends TControl {
	TParm regionParm;// ҽ���������
	int selectrow = -1;
	TTable table;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		// �õ�ǰ̨���������ݲ���ʾ�ڽ�����
		// TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		// cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
		// this.getValueString("REGION_CODE")));
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// ���ҽ���������
		this.setValue("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));
		//TParm recptype = (TParm) getParameter();
		// Ԥ�����ʱ���
		this.callFunction("UI|STARTTIME|setValue", SystemTool.getInstance()
				.getDate());
		this.callFunction("UI|ENDTIME|setValue", SystemTool.getInstance()
				.getDate());
		table = (TTable) this.getComponent("TABLE");
		// // table1�ĵ��������¼�
		// callFunction("UI|TABLE|addEventListener", "TABLE->"
		// + TTableEvent.CLICKED, this, "onTableClicked");
		// Ĭ��Table����ʾ����Һż�¼
		// onQuery();
	}

	// /**
	// *���Ӷ�Table�ļ���
	// *
	// * @param row
	// * int
	// */
	// public void onTableClicked(int row) {
	// // ���������¼�
	// this.callFunction("UI|TABLE|acceptText");
	// // TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
	// selectrow = row;
	// }

	// public void onTableDoubleClicked(int row) {
	// TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
	// this.setReturnValue((String) data.getData("CONFIRM_NO", row));
	// this.callFunction("UI|onClose");
	// }

	/**
	 * ��ѯ������Ϣ
	 */
	public void onQuery() {
		TParm parm = new TParm();
		if (this.getValueString("HOSP_NHI_NO").length()<=0) {
			this.messageBox("ҽ�����򲻿���Ϊ��");
			this.grabFocus("HOSP_NHI_NO");
			return;
		}
		if (this.getValue("MR_NO").toString().length() > 0) {
			parm.setData("MR_NO", this.getValue("MR_NO"));
		}
		if (this.getValueString("PAT_NAME").length() > 0) {
			parm.setData("PAT_NAME", this.getValue("PAT_NAME"));
		}
		if (this.getValueString("HOSP_NHI_NO").length() > 0) {// ҽ������Ų�ѯ
			parm.setData("HOSP_NHI_NO", this.getValue("HOSP_NHI_NO"));
		}
		if (getValue("STARTTIME") == null
				|| getValueString("STARTTIME").length() <= 0) {
			messageBox("��ѡ��ʼʱ��!");
			return;
		}
		if (getValue("ENDTIME") == null
				|| getValueString("ENDTIME").length() <= 0) {
			messageBox("��ѡ�����ʱ��!");
			return;
		}
		parm.setData("START_DATE", StringTool.setTime((Timestamp) this
				.getValue("STARTTIME"), "00:00:00"));
		parm.setData("END_DATE", StringTool.setTime((Timestamp) this
				.getValue("ENDTIME"), "23:59:59"));
		parm = INSOpdTJTool.getInstance().selectAutoAccount(parm);
		if (parm.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (parm.getCount() <= 0) {
			this.messageBox("û�в�ѯ������");
			table.setParmValue(new TParm());
			return;
		}
		table.setParmValue(parm);
	}

	/**
	 * �����Żس��¼�
	 */
	public void onQueryMrNo() {
		Pat pat = Pat.onQueryByMrNo(this.getValue("MR_NO").toString());
		if (pat == null) {
			this.messageBox("�޴˲�����!");
			return;
		}
		this.setValue("MR_NO", pat.getMrNo());
	}

	/**
	 * �������ƻس��¼�
	 */
	public void onQueryPatName() {
		TParm sendParm = new TParm();
		sendParm.setData("PAT_NAME", this.getValue("PAT_NAME"));
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMPatQuery.x", sendParm);
		if (reParm == null)
			return;
		this.setValue("PAT_NAME", reParm.getValue("PAT_NAME"));
	}

	/**
    *
    */
	public void onOK() {
		if (this.getValueString("HOSP_NHI_NO").length()<=0) {
			this.messageBox("ҽ�����򲻿���Ϊ��");
			this.grabFocus("HOSP_NHI_NO");
			return;
		}
		int row=table.getSelectedRow();
		if (row<0) {
			this.messageBox("��ѡ��Ҫִ�е�����");
			return;
		}
		if (this.messageBox("��ʾ","�Ƿ�ִ��",2)!=0) {
			return;
		}
		
		
		TParm tempParm = table.getParmValue().getRow(row);
		//tempParm.setData("NHI_REGION_CODE", this.getValue("HOSP_NHI_NO"));
		TParm returnParm=null;
		// ���ҽ��������Ϣ
//		TParm mzConfirmParm = INSMZConfirmTool.getInstance()
//				.queryMZConfirmOne(tempParm);
//		if (mzConfirmParm.getErrCode()<0 || mzConfirmParm.getCount()<=0) {
//			this.messageBox("�������ʧ��");
//			return;
//		}
		int insType=getInsType(tempParm);
		if (tempParm.getValue("RECP_TYPE").equals("REGT")
				|| tempParm.getValue("RECP_TYPE").equals("OPBT")) {
			
			if (insType==-1) {
				this.messageBox("�������ʧ��");
				return;
			}
			tempParm.setData("NHI_REGION_CODE",this.getValue("HOSP_NHI_NO"));
			tempParm.setData("REGION_CODE",this.getValue("HOSP_NHI_NO"));
			tempParm.setData("CHOOSE_FLG", "Y");//���˲���
			//TParm parm=INSTJFlow.getInstance().cancelBalance(commFunction(tempParm),insType);
			returnParm = INSTJFlow.getInstance().reSetAutoAccount(tempParm, insType);
			if (returnParm.getErrCode() < 0) {
				this.messageBox(returnParm.getErrText());
			}
		} else {
			// ������
			TParm parm=null;
			if (null != tempParm.getValue("INSAMT_FLG")
					&& tempParm.getValue("INSAMT_FLG").length() > 0
					&& tempParm.getInt("INSAMT_FLG") == 1) {// ״̬Ϊ1
															// У���Ƿ��Ѿ���Ʊ������Ѿ���Ʊ�Ĳ���������ȷ�Ͻӿ�ֱ�ӽ�״̬�ı�3
				String sql = "";
				String tableName = "";

				if (tempParm.getValue("RECP_TYPE").equals("REG")) {// �Һ�
					tableName = "BIL_REG_RECP";
				} else if (tempParm.getValue("RECP_TYPE").equals("OPB")) {// �շ�
					tableName = "BIL_OPB_RECP";
				}
				sql = "SELECT CASE_NO,PRINT_NO FROM "
						+ tableName
						+ " WHERE CASE_NO='"
						+ tempParm.getValue("CASE_NO")
						+ "' AND AR_AMT>0 AND (RESET_RECEIPT_NO IS NULL OR RESET_RECEIPT_NO='')";
				TParm bilParm = new TParm(TJDODBTool.getInstance().select(sql));
				if (bilParm.getErrCode() < 0 || bilParm.getCount() > 1) {
					this.messageBox("�˲���Ʊ���ж������,����ϵ��Ϣ���Ĳ���");
					return;
				}
				if (bilParm.getCount() <= 0) {//������Ʊ��ִ�г���ҽ���������
					parm = INSTJFlow.getInstance().cancelBalance(
							commFunction(tempParm), insType);
				} else {
					sql = "SELECT DISEASE_CODE,PAY_KIND,CTZ_CODE AS PAT_TYPE FROM INS_MZ_CONFIRM"
							+ " WHERE CASE_NO='"
							+ tempParm.getValue("CASE_NO")
							+ "' AND CONFIRM_NO='"
							+ tempParm.getValue("CONFIRM_NO") + "'";
					TParm mzConfirmParm = new TParm(TJDODBTool.getInstance()
							.select(sql));//��ѯ����ҽ����Ϣ
					if (mzConfirmParm.getErrCode() < 0
							|| mzConfirmParm.getCount() > 1) {
						this.messageBox("�˲���Ʊ���ж������,����ϵ��Ϣ���Ĳ���");
						return;
					}
					tempParm.setData("PRINT_NO",bilParm.getValue("PRINT_NO",0));//=====pangben 2013-8-29 Ʊ�����
					parm = INSTJFlow.getInstance().confirmBalance(
							commFunction(tempParm), mzConfirmParm.getRow(0),
							insType);
				}
			} else {
				parm = INSTJFlow.getInstance().cancelBalance(
						commFunction(tempParm), insType);
			}
//			returnParm = autoAccount(tempParm, insType, mzConfirmParm,
//					buffer);
			if (parm.getErrCode() < 0) {
				this.messageBox(parm.getErrText());
			}
		}
		this.messageBox("�����ɹ�");
		onQuery();
//		TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
//		this.setReturnValue((String) data.getData("CONFIRM_NO", selectrow));
//		this.callFunction("UI|onClose");
	}
	private TParm commFunction(TParm opbReadCardParm) {
		TParm parm = new TParm();
		//parm.setData("INS_TYPE", getType());
		parm.setData("REGION_CODE", this.getValue("HOSP_NHI_NO"));
		parm.setData("RECP_TYPE", opbReadCardParm.getValue("RECP_TYPE"));
		parm.setData("CASE_NO", opbReadCardParm.getValue("CASE_NO"));
		parm.setData("CONFIRM_NO", opbReadCardParm.getValue("CONFIRM_NO"));
		parm.setData("EXE_USER",Operator.getID());//=====pangben 2013-3-13 ���
		parm.setData("EXE_TERM",Operator.getIP());//=====pangben 2013-3-13 ���
		parm.setData("EXE_TYPE", opbReadCardParm.getValue("RECP_TYPE"));//=====pangben 2013-3-13 ���
		parm.setData("opbReadCardParm", opbReadCardParm.getData());
		parm.setData("ACCOUNT_PAY_AMT", opbReadCardParm.getDouble("ACCOUNT_PAY_AMT"));// �ʻ�֧�����-----�����ʻ��������-
		parm.setData("INS_PAY_AMT",opbReadCardParm.getDouble("INS_PAY_AMT"));// ҽ��֧�����
		parm.setData("UACCOUNT_PAY_AMT", opbReadCardParm.getDouble("UNACCOUNT_PAY_AMT")); // ��ҽ��֧��
		parm.setData("UNREIM_AMT", opbReadCardParm.getDouble("UNREIM_AMT"));
		parm.setData("INSAMT_FLG", opbReadCardParm.getValue("INSAMT_FLG"));//����״̬
		parm.setData("CHOOSE_FLG", "Y");//���˲���
		parm.setData("PRINT_NO", opbReadCardParm.getValue("PRINT_NO"));//Ʊ�ݺ���//pangben 2013-8-29
		return parm;
	}

	/**
	 * ��þ������
	 * 
	 * @return
	 */
	private int getInsType(TParm parm) {
		int insType = -1;
		if (parm.getInt("INS_CROWD_TYPE") == 1
				&& parm.getInt("INS_PAT_TYPE") == 1) {
			insType = 1;// ��ְ��ͨ
		} else if (parm.getInt("INS_CROWD_TYPE") == 1
				&& parm.getInt("INS_PAT_TYPE") == 2) {
			insType = 2;// ��ְ����
		} else if (parm.getInt("INS_CROWD_TYPE") == 2
				&& parm.getInt("INS_PAT_TYPE") == 2) {
			insType = 3;// �Ǿ�����
		}
		return insType;
	}

}
