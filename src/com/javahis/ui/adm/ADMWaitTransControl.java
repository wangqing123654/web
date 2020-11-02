package com.javahis.ui.adm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMSQLTool;
import jdo.adm.ADMWaitTransTool;
import jdo.bil.BILPayTool;
import jdo.hl7.Hl7Communications;
import jdo.ibs.IBSOrdermTool;
import jdo.mro.MROBorrowTool;
import jdo.mro.MROQueueTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ���ת����
 * </p>
 *
 * <p>
 * Description: ���ת����    
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
 * @author JiaoY
 * @version 1.0
 */
public class ADMWaitTransControl extends TControl {
	TParm patInfo = null;// ��ת��Ĳ�����Ϣ
	TParm admPat = null;// ��Ժ�Ĳ�����Ϣ
	TParm admInfo;// ��¼������λ״̬

	public void onInit() {
		super.onInit();
		pageInit();
	}

	/**
	 * ҳ���ʼ��
	 */
	private void pageInit() {
		//============add  by  chenxi
		callFunction("UI|WAIT_IN|addEventListener",
                "WAIT_IN->" + TTableEvent.CLICKED, this, "onTABLEClicked");
		onQuery();
		onInit_Dept_Station();
		initInStation();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					chose();
				} catch (Exception e) {
				}
			}
		});
	}
	//====================chenxi add  
	public void onTABLEClicked(int row ){
		if (row < 0)
            return;
		TTable table = (TTable)this.getComponent("WAIT_IN") ;
		TTable inTable = (TTable)this.getComponent("in");
		int selectRow = 0 ;
		String bedNo= table.getValueAt(row, 4).toString().trim() ;
		if(!bedNo.equals("") || bedNo.length()<0){
		
			int check =	this.messageBox("��Ϣ", "�˲�����ԤԼ"+bedNo+"����,�Ƿ�Ժ���ס?", 0) ;
		    if(check!=0){
		    	String updatesql = "UPDATE SYS_BED SET APPT_FLG = 'N'  WHERE BED_NO_DESC = '"+bedNo+"'"  ;
				TParm bedParm = new TParm(TJDODBTool.getInstance().update(updatesql)) ;
				if (bedParm.getErrCode() < 0) { 
					this.messageBox("E0005");
					return;
				} 
				return ;
		    }
		    	
		    else {
		    	for(int i=0 ;i<inTable.getRowCount();i++){
		    		if(inTable.getValueAt(i, 2).equals(bedNo))
		    			selectRow = i ;
		    	}
		    	this.onCheckin(selectRow) ;
		    }
		    	
		}
	
	}

	/**
	 * ���Ų����봲
	 */
	public void onCheckin(int selectRow) {
		// �õ���ת��table
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		// �õ���ת��DS
		TDataStore ds = waitIn.getDataStore();
		// �õ���Ժ����table
		TTable checkIn = (TTable) this.callFunction("UI|in|getThis");
		checkIn.setSelectedRow(selectRow);
		int waitIndex = waitIn.getSelectedRow();// ��ת���ѡ���к�
		if (waitIndex < 0) {
			this.messageBox_("��ѡ��Ҫ��ס�Ĳ���!");
			return;
		}
		int checkIndex = checkIn.getSelectedRow();// ��Ժ�����б�ѡ���к�
		if (checkIndex < 0) {
			this.messageBox_("��ѡ��Ҫ��ס�Ĵ�λ!");
			return;
		}
		//ˢ�¼��
		if(!check()){// shibl 20130117 add 
			return;
		}
		// �˻��ߴ�ת�벡�����Ǳ�����
		if (!this.getValueString("STATION_CODE").equalsIgnoreCase(
				ds.getItemString(waitIndex, "IN_STATION_CODE"))) {
			this.messageBox_("�˻��ߴ�ת�벡�����Ǳ�����");
			return;
		}
		// �õ���ת��ѡ���е�����
		TParm updata = new TParm();
		// ��λ��
		updata.setData("BED_NO",
				checkIn.getValueAt(checkIn.getSelectedRow(), 0));
		// ԤԼע��
		updata.setData("APPT_FLG", "N");
		// ռ��ע��
		updata.setData("ALLO_FLG", "Y");
		// ��ת�벡����
		updata.setData("MR_NO", waitIn.getValueAt(waitIndex, 0));
		// ��ת������
		updata.setData("CASE_NO", waitIn.getValueAt(waitIndex, 1));
		// ��ת��סԺ��
		updata.setData("IPD_NO", waitIn.getValueAt(waitIndex, 6));
		// ռ��ע��
		updata.setData("BED_STATUS", "1");
		// ��λ���ڲ���
		updata.setData("STATION_CODE", this.getValueString("STATION_CODE"));
		// ��λ��
		updata.setData("BED_NO",
				checkIn.getValueAt(checkIn.getSelectedRow(), 1));
		// ����
		updata.setData("DEPT_CODE", ds.getItemString(waitIndex, "IN_DEPT_CODE"));
		// dataStore

		updata.setData("OPT_USER", Operator.getID());
		updata.setData("OPT_TERM", Operator.getIP());
		// ��鲡���Ƿ����
		if (checkOccu(waitIn.getValueAt(waitIndex, 1).toString())) {
			updata.setData("OCCU_FLG", "Y");// ��ʾ�ò������й���������
			// ����ò����а��� ��ô�жϲ�����ס�Ĵ�λ�ǲ��Ǹò���ָ���Ĵ�λ���������Ҫ�������ѣ�������Ϣ�ᱻȡ��
			// ���ת��Ĵ�λ��MR_NOΪ�ջ����벡����MR_NO����ͬ ��ʾ�ô�λ���ǲ���ָ���Ĵ�λ
			if (checkIn.getValueAt(checkIndex, 3) == null
					|| "".equals(checkIn.getValueAt(checkIndex, 3))
					|| !waitIn
							.getValueAt(waitIndex, 0)
							.toString()
							.equals(checkIn.getValueAt(checkIndex, 2)
									.toString())) {
				int check = this.messageBox("��Ϣ",
						"�˲����Ѱ���������סָ����λ��ȡ���ò����İ������Ƿ������", 0);
				if (check != 0) {
					return;
				}
				updata.setData("CHANGE_FLG", "Y");// ��ʾ��������ס��ָ����λ����ոò����İ�����Ϣ
			} else {
				updata.setData("CHANGE_FLG", "N");// ��ʾ������ס��ָ����λ
			}
		} else {
			updata.setData("OCCU_FLG", "N");// ��ʾ�ò���û�а���
		}
		waitIn.removeRow(waitIndex);
		updata.setData("UPDATE", ds.getUpdateSQL());
		// =========pangben modify 20110617 start
		updata.setData("REGION_CODE", Operator.getRegion());
//		System.out.println("----------------updata----------"+updata);
		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onInSave", updata); // �봲����
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			waitIn.retrieve();
			return;
		}
		else {
			this.messageBox("P0005");
			sendHL7Mes(updata);
			initInStation();
			chose();
		}
	}
	/**
	 * ����������Ϣ
	 */
	public void onReload() {
		pageInit();
	}

	/**
	 * ��ѯ�������������Ա𣬳�������
	 */
	public void onQuery() {
		TParm parm = new TParm();
		// =============pangben modify 20110512 start ��������ѯ
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop
		// ===pangben modify ��Ӳ���
		patInfo = ADMWaitTransTool.getInstance().selpatInfo(parm); // ��ת��Ĳ�����Ϣ
		admPat = ADMWaitTransTool.getInstance().selAdmPat(parm); // ��Ժ�Ĳ�����Ϣ
	}

	/**
	 * ��ת��ת�� ����combo ��ѡ�¼�
	 */
	public void chose() {
		this.onSelectIn();
		this.onSelectOut();
	}

	/**
	 * ��ת��ת��TABLE ��ʾ
	 *
	 * @param tag
	 *            String
	 */
	public void creatDataStore(String tag) {
		Pat pat = null;
		TParm parm = new TParm();
		TParm result = new TParm();
		if (patInfo == null)
			return;
		TTable table = (TTable) this.callFunction("UI|" + tag + "|getThis");
		String mrNo = "";//
		String caseNo = "";//
		//System.out.println("row count===="+table.getRowCount());
		Timestamp date=SystemTool.getInstance().getDate() ;   //=======  chenxi modify 20130228
		/**
		 * ѭ��table ��ʾ�����������Ա�����
		 */
		for (int i = 0; i < table.getRowCount(); i++) {
			// �õ�table�е�ֵ
			mrNo = table.getValueAt(i, 0).toString().trim();
			caseNo = table.getValueAt(i, 1).toString().trim();
			parm = new TParm();
			result = new TParm();
			pat = new Pat();
			// �õ�pat�����õ�����
			pat = pat.onQueryByMrNo(mrNo);
			parm.setData("MR_NO", mrNo);
			parm.setData("CASE_NO", caseNo);
			result = ADMInpTool.getInstance().selectBedNo(parm) ;
			// �õ���������
			String[] AGE = StringTool.CountAgeByTimestamp(pat.getBirthday(),
					date);
			//=================  chenxi modify 20130228
			// ��table��ֵ
			if(tag.equals("WAIT_IN")){
			table.setValueAt(pat.getName(), i, 2);
			table.setValueAt(pat.getSexCode(), i, 3);
			table.setValueAt(result.getValue("BED_NO_DESC", 0), i, 4);   //=====ԤԼ����
			table.setValueAt(AGE[0], i, 5);
			}
			else {
			table.setValueAt(pat.getName(), i, 3);
			table.setValueAt(pat.getSexCode(), i, 4);
			table.setValueAt(AGE[0], i, 5);}

		}
	}

	/**
	 * ��Ժ������ѡ�¼�
	 */
	public void onInStation() {
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 3) == null) {
			this.messageBox_("��ѡ�񲡻���");
			return;
		}
		if ("3".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("�˲����ǰ�������ѡ�񲡻�ʵס������");
			return;
		}
		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("�˲���δ��ס��");
			return;
		}
		TParm parm = table.getParmValue();
		TParm sendParm = new TParm();
		// ���
		sendParm.setData("ADM", "ADM");
		// ������
		sendParm.setData("MR_NO", parm.getData("MR_NO", selectRow));
		// סԺ��
		sendParm.setData("IPD_NO", parm.getData("IPD_NO", selectRow));
		// �����
		sendParm.setData("CASE_NO", parm.getData("CASE_NO", selectRow));
		// ����
		sendParm.setData("PAT_NAME", parm.getData("PAT_NAME", selectRow));
		// �Ա�
		sendParm.setData("SEX_CODE", parm.getData("SEX_CODE", selectRow));
		// ����
		sendParm.setData("AGE", parm.getData("AGE", selectRow));
		// ����
		sendParm.setData("BED_NO", parm.getData("BED_NO", selectRow));
		// ����
		sendParm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		// ����
		sendParm.setData("STATION_CODE",
				parm.getData("STATION_CODE", selectRow));
		// ����ҽʦ
		sendParm.setData("VS_DR_CODE", parm.getData("VS_DR_CODE", selectRow));
		// ����ҽʦ
		sendParm.setData("ATTEND_DR_CODE",
				parm.getData("ATTEND_DR_CODE", selectRow));
		// ������
		sendParm.setData("DIRECTOR_DR_CODE",
				parm.getData("DIRECTOR_DR_CODE", selectRow));
		// ���ܻ�ʿ
		sendParm.setData("VS_NURSE_CODE",
				parm.getData("VS_NURSE_CODE", selectRow));
		// ��Ժ״̬
		sendParm.setData("PATIENT_CONDITION",
				parm.getData("PATIENT_CONDITION", selectRow));
		// ������
		sendParm.setData("DIRECTOR_DR_CODE",
				parm.getData("DIRECTOR_DR_CODE", selectRow));
		// BED_OCCU_FLG
		sendParm.setData("BED_OCCU_FLG",
				parm.getData("BED_OCCU_FLG", selectRow));
		// ���水ť״̬
		sendParm.setData("SAVE_FLG", this.getPopedem("admChangeDr"));
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\AdmPatinfo.x", sendParm); 
		initInStation();
	}

	/**
	 * ��Ժ������Ϣ��ѯ
	 */
	public void initInStation() {
		TParm parm = new TParm();
		parm.setData("STATION_CODE", getValue("STATION_CODE").toString()==null?"":getValue("STATION_CODE").toString());
		//==================shibl
//		parm.setData("DEPT_CODE", getValue("DEPT_CODE").toString()==null?"":getValue("DEPT_CODE").toString());
		// =============pangben modify 20110512 start ��Ӳ���
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop
//		System.out.println("-----------parm-----"+parm);
		admInfo = ADMInpTool.getInstance().queryInStation(parm);
//		System.out.println("-------admInfo--1------------"+admInfo);
		if (admInfo.getErrCode() < 0) {
			this.messageBox_(admInfo.getErrText());
			return;
		}
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		// �������û��ס�� �������0ȥ��
		for (int i = 0; i < admInfo.getCount(); i++) {

			if (admInfo.getInt("AGE", i) == 0) {
				// ���ס�в������Ҵ˲������ǰ��� ����Ϊ0�� ��ô�Զ���һ
				if (admInfo.getValue("MR_NO", i).length() > 0
						&& !admInfo.getValue("BED_STATUS", i).equals("3"))
					admInfo.setData("AGE", i, "1");
				else
					// û�в����ڴ���0��Ϊ��
					admInfo.setData("AGE", i, "");
			} else {
				// �õ���������========pangb 2011-11-18 �������һ��
				String[] AGE = StringTool.CountAgeByTimestamp(
						admInfo.getTimestamp("BIRTH_DATE", i),
						admInfo.getTimestamp("IN_DATE", i));
				admInfo.setData("AGE", i, AGE[0]);
			}
			if (admInfo.getData("IN_DATE", i) != null
					&& admInfo.getValue("MR_NO", i).length() > 0
					&& !admInfo.getValue("BED_STATUS", i).equals("3")) {
				int days = StringTool.getDateDiffer(SystemTool.getInstance()
						.getDate(), admInfo.getTimestamp("IN_DATE", i));
				if (days > 0) {
					admInfo.setData("DAYNUM", i, days);
				} else {
					admInfo.setData("DAYNUM", i, "1");
				}
			} else
				admInfo.setData("DAYNUM", i, "");

		}
//		System.out.println("-------admInfo--------------"+admInfo);
		table.setParmValue(admInfo);
	}
	/**
	 * ��ת�����COMBO��ѡ�¼�
	 */
	public void onSelectIn() {
		//=========modify lim 20120323 begin
//		TTable WAIT_IN = (TTable) this.callFunction("UI|WAIT_IN|getThis");
//		WAIT_IN.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_IN("", ""));
//		WAIT_IN.retrieve();		
		//=========modify lim 20120323 end
		
		String filter = "";
		if (this.getValueString("IN_STATION_CODE").length() > 0)
			filter += " IN_STATION_CODE ='" + this.getValueString("IN_STATION_CODE")
					+ "'";
		if (this.getValueString("IN_DEPT_CODE").length() > 0)
			filter += " AND IN_DEPT_CODE ='"
					+ this.getValueString("IN_DEPT_CODE") + "'";
		TTable table = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		table.setFilter(filter);
		table.filter();
		table.setDSValue();
		table.getDataStore().showDebug();
		creatDataStore("WAIT_IN");

	}

	/**
	 * ��ת������COMBO��ѡ�¼�
	 */
	public void onSelectOut() {
		String filter = "";
		if (this.getValueString("OUT_STATION_CODE").length() > 0)
			filter += " OUT_STATION_CODE ='"
					+ this.getValueString("OUT_STATION_CODE") + "'";
		if (this.getValueString("OUT_DEPT_CODE").length() > 0)
			filter += " AND OUT_DEPT_CODE ='"
					+ this.getValueString("OUT_DEPT_CODE") + "'";

		TTable table = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		table.setFilter(filter);
		table.filter();
		table.setDSValue();
		creatDataStore("WAIT_OUT");
	}

	/**
	 * ���Ų����봲
	 */
	public void onCheckin() {
		// �õ���ת��table
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		// �õ���ת��DS
		TDataStore ds = waitIn.getDataStore();
		// �õ���Ժ����table
		TTable checkIn = (TTable) this.callFunction("UI|in|getThis");
		int waitIndex = waitIn.getSelectedRow();// ��ת���ѡ���к�
		if (waitIndex < 0) {
			this.messageBox_("��ѡ��Ҫ��ס�Ĳ���!");
			return;
		}
		int checkIndex = checkIn.getSelectedRow();// ��Ժ�����б�ѡ���к�
		if (checkIndex < 0) {
			this.messageBox_("��ѡ��Ҫ��ס�Ĵ�λ!");
			return;
		}
		//ˢ�¼��
		if(!check()){// shibl 20130117 add 
			return;
		}
		// �˻��ߴ�ת�벡�����Ǳ�����
		if (!this.getValueString("STATION_CODE").equalsIgnoreCase(
				ds.getItemString(waitIndex, "IN_STATION_CODE"))) {
			this.messageBox_("�˻��ߴ�ת�벡�����Ǳ�����");
			return;
		}
		//========================  chenxi modify 20130228 
//		// �õ���ת���벡������
//		if ("1".equals(checkIn.getValueAt(checkIn.getSelectedRow(), 0))) {
//			this.messageBox_("�˴���ռ�ã�");
//			return;
//		}
//		// �ж�ѡ�еĴ����Ƿ��Ѿ���Ԥ��
//		if (admInfo.getBoolean("APPT_FLG", checkIndex)) {
//			int check = this.messageBox("��Ϣ", "�˴��ѱ�Ԥ�����Ƿ��������", 0);
//			if (check != 0) {
//				return;
//			}
//		}
//		// �ж�ѡ�еĴ����Ƿ񱻰���
//		if (admInfo.getBoolean("BED_OCCU_FLG", checkIndex)) {
//			this.messageBox_("�˴�λ�ѱ�������������ס��");
//			return;
//		}
		// �õ���ת��ѡ���е�����
		TParm updata = new TParm();
        // ��λ��
        updata.setData("BED_NO", checkIn.getValueAt(checkIn.getSelectedRow(), 1));
		// ԤԼע��
		updata.setData("APPT_FLG", "N");
		// ռ��ע��
		updata.setData("ALLO_FLG", "Y");
		// ��ת�벡����
		updata.setData("MR_NO", waitIn.getValueAt(waitIndex, 0));
		// ��ת������
		updata.setData("CASE_NO", waitIn.getValueAt(waitIndex, 1));
		// ��ת��סԺ��
		updata.setData("IPD_NO", waitIn.getValueAt(waitIndex, 6));
		// ռ��ע��
		updata.setData("BED_STATUS", "1");
        // ����ת������ wanglong add 20140728
        updata.setData("OUT_STATION_CODE", ds.getItemString(waitIndex, "OUT_STATION_CODE"));
		// ��λ���ڲ���
		updata.setData("STATION_CODE", this.getValueString("STATION_CODE"));
        // ����ת������ wanglong add 20140728
        updata.setData("OUT_DEPT_CODE", ds.getItemString(waitIndex, "OUT_DEPT_CODE"));
		// ��λ���ڿ���
		updata.setData("DEPT_CODE", ds.getItemString(waitIndex, "IN_DEPT_CODE"));

		updata.setData("OPT_USER", Operator.getID());
		updata.setData("OPT_TERM", Operator.getIP());
		// ��鲡���Ƿ����
		if (checkOccu(waitIn.getValueAt(waitIndex, 1).toString())) {
			updata.setData("OCCU_FLG", "Y");// ��ʾ�ò������й���������
			// ����ò����а��� ��ô�жϲ�����ס�Ĵ�λ�ǲ��Ǹò���ָ���Ĵ�λ���������Ҫ�������ѣ�������Ϣ�ᱻȡ��
			// ���ת��Ĵ�λ��MR_NOΪ�ջ����벡����MR_NO����ͬ ��ʾ�ô�λ���ǲ���ָ���Ĵ�λ
			if (checkIn.getValueAt(checkIndex, 3) == null
					|| "".equals(checkIn.getValueAt(checkIndex, 3))
					|| !waitIn
							.getValueAt(waitIndex, 0)
							.toString()
							.equals(checkIn.getValueAt(checkIndex, 2)
									.toString())) {
				int check = this.messageBox("��Ϣ",
						"�˲����Ѱ���������סָ����λ��ȡ���ò����İ������Ƿ������", 0);
				if (check != 0) {
					return;
				}
				updata.setData("CHANGE_FLG", "Y");// ��ʾ��������ס��ָ����λ����ոò����İ�����Ϣ
			} else {
				updata.setData("CHANGE_FLG", "N");// ��ʾ������ס��ָ����λ
			}
		} else {
			updata.setData("OCCU_FLG", "N");// ��ʾ�ò���û�а���
		}
		waitIn.removeRow(waitIndex);
		updata.setData("UPDATE", ds.getUpdateSQL());
		// =========pangben modify 20110617 start
		updata.setData("REGION_CODE", Operator.getRegion());
//		System.out.println("----------------updata----------"+updata);
		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onInSave", updata); // �봲����
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			waitIn.retrieve();
			return;
		} 
		else {
			this.messageBox("P0005");
			sendHL7Mes(updata);
			initInStation();
			chose();
		}
	}

	/**
	 * ���ĳһ�����Ƿ����
	 *
	 * @param caseNo
	 *            String
	 * @return boolean true������ false��δ����
	 */
	public boolean checkOccu(String caseNo) {
		TParm qParm = new TParm();
		qParm.setData("CASE_NO", caseNo);
		TParm occu = SYSBedTool.getInstance().queryAll(qParm);
		int count = occu.getCount("BED_OCCU_FLG");
		String check = "N";
		for (int i = 0; i < count; i++) {
			if ("Y".equals(occu.getData("BED_OCCU_FLG", i))) {
				check = "Y";
			}
		}
		if ("Y".equals(check)) {
			return true;
		} else {
			return false;
		}
	}
	/**
     * ��˴�λ
     * @return boolean
     */
    public boolean check() {
        TTable table = (TTable)this.callFunction("UI|in|getThis");
        TTable waitTable = (TTable)this.callFunction("UI|WAIT_IN|getThis");  //chenxi modify 20130308
        if (table.getSelectedRow() < 0) {
            this.messageBox("δѡ��λ");
            return false;
        }
        //=============shibl 20130106 add======���˵�ͬһ����δˢ��ҳ��=============================
        TParm  parm=table.getParmValue().getRow(table.getSelectedRow());
        TParm  inParm=new TParm();
        inParm.setData("BED_NO", parm.getValue("BED_NO"));
        TParm result = ADMInpTool.getInstance().QueryBed(inParm);
        String APPT_FLG=result.getCount()>0?result.getValue("APPT_FLG",0):"";
        String ALLO_FLG=result.getCount()>0?result.getValue("ALLO_FLG",0):"";
        String BED_STATUS=result.getCount()>0?result.getValue("BED_STATUS",0):"";
        if (ALLO_FLG.equals("Y")) {
            this.messageBox("�˴���ռ��");
            onReload();
            return false;
        }
        if (BED_STATUS.equals("1")) {
            this.messageBox("�˴��ѱ�����");
            onReload();
            return false;
        }
        //=================  chenxi modify 20130308
        if (APPT_FLG.equals("Y")) {
        	if(!waitTable.getValueAt(waitTable.getSelectedRow(), 4).equals(parm.getValue("BED_NO_DESC"))){
        		int check = this.messageBox("��Ϣ", "�˴��ѱ�Ԥ�����Ƿ��������", 0);
    			if (check != 0) {
    				onReload();
    				return  false;
    			}
                return true;
        	}
        	
        }
        return true;
    }
	/**
	 * ������ס���
	 *
	 * @return boolean
	 */
	public boolean checkSysBed() {
		// �õ���ת��table
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		// �õ���Ժ����table
		TTable checkIn = (TTable) this.callFunction("UI|in|getThis");
		String waitMr = waitIn.getValueAt(waitIn.getSelectedRow(), 0)
				.toString();
		String bedMr = checkIn.getValueAt(checkIn.getSelectedRow(), 3) == null ? ""
				: checkIn.getValueAt(checkIn.getSelectedRow(), 3).toString();
		if (bedMr == null || "".equals(bedMr))
			return true;
		else if (waitMr.equals(bedMr)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ת�ƹ���
	 */
	public void onOutDept() {
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();
		if (selectRow == -1) {
			this.messageBox("��ѡ����Ժ������");
			return;
		}
		if (table.getValueAt(selectRow, 3) == null) {
			this.messageBox("��ѡ����Ժ������");
			return;
		}
		if ("3".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("�˲����ǰ�������ѡ�񲡻�ʵס������");
			return;
		}

		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox("�˲���δ��ס��");
			return;
		}
		TParm parm = table.getParmValue();
		TParm sendParm = new TParm();
		// ������
		sendParm.setData("MR_NO", parm.getData("MR_NO", selectRow));
		// סԺ��
		sendParm.setData("IPD_NO", parm.getData("IPD_NO", selectRow));
		// �����
		sendParm.setData("CASE_NO", parm.getData("CASE_NO", selectRow));
		// ����
		sendParm.setData("PAT_NAME", parm.getData("PAT_NAME", selectRow));
		// �Ա�
		sendParm.setData("SEX_CODE", parm.getData("SEX_CODE", selectRow));
		// ����
		sendParm.setData("AGE", parm.getData("AGE", selectRow));
		// ����
		sendParm.setData("BED_NO", parm.getData("BED_NO", selectRow));
		// ����
		sendParm.setData("OUT_DEPT_CODE", parm.getData("DEPT_CODE", selectRow));
		// ����
		sendParm.setData("OUT_STATION_CODE",
				parm.getData("STATION_CODE", selectRow));
		// ��Ժʱ��
		sendParm.setData("IN_DATE", parm.getData("IN_DATE", selectRow));

		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMOutInp.x", sendParm);
		initInStation();
		TTable outTable = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		//System.out.println("=======WAIT_OUT======"+outTable.getFilter());
		//$$=========add by lx===========$$//
		//outTable.retrieve();
		//onSelectOut();
		outTable.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_OUT(this.getValueString("OUT_DEPT_CODE"), this.getValueString("OUT_STATION_CODE")));
		outTable.retrieve();
		//$$==================$$//
		creatDataStore("WAIT_OUT");
	}

	/**
	 * ��������
	 */
	public void onBed() {
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 3) == null) {
			this.messageBox_("��ѡ�񲡻���");
			return;
		}
		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("�˲���δ��ס��");
			return;
		}
		if ("3".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("�˲����ǰ�������ѡ�񲡻�ʵס������");
			return;
		}
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", admInfo.getValue("CASE_NO", selectRow));
		sendParm.setData("MR_NO", admInfo.getValue("MR_NO", selectRow));
		sendParm.setData("IPD_NO", admInfo.getValue("IPD_NO", selectRow));
		sendParm.setData("DEPT_CODE", admInfo.getValue("DEPT_CODE", selectRow));
		sendParm.setData("STATION_CODE",
				admInfo.getValue("STATION_CODE", selectRow));
		sendParm.setData("BED_NO", admInfo.getValue("BED_NO", selectRow));
		TParm bed = new TParm();
		bed.setData("BED_NO", admInfo.getValue("BED_NO", selectRow));
		TParm check = SYSBedTool.getInstance().queryRoomBed(bed);
		String caseNo = admInfo.getValue("CASE_NO", selectRow);
		int count = check.getCount("BED_NO");
		boolean flg = false ;
		for (int i = 0; i < count; i++) {
			if ("Y".equals(check.getData("ALLO_FLG", i))
					&& !caseNo.equals(check.getData("CASE_NO", i))) {
				flg = true ;
		}
			}
		if(flg==true){
			int checkFlg=	this.messageBox("��Ϣ","�˲���������������!�Ƿ����������",0);
			if(checkFlg!=0)
				return;
			}	
		
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMSysBedAllo.x", sendParm);
		initInStation();
		chose();
	}

	/**
	 * ��ʼ������Ĭ�Ͽ��� ����
	 */
	public void onInit_Dept_Station() {
		String userId = Operator.getID();
		String station = Operator.getStation();
		String dept = Operator.getDept();
		TComboBox admstation = (TComboBox) this.getComponent("STATION_CODE");
		TParm Station = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserStationList(userId)));
		admstation.setParmValue(Station);
		admstation.onQuery();
		TComboBox admdept = (TComboBox) this.getComponent("DEPT_CODE");
		TParm Dept = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserStationList(userId)));
		admdept.setParmValue(Dept);
		admdept.onQuery();
		TComboBox in_station = (TComboBox) this.getComponent("IN_STATION_CODE");
		TParm inStation = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserStationList(userId)));
		in_station.setParmValue(inStation);
		in_station.onQuery();
		TComboBox out_station = (TComboBox) this.getComponent("OUT_STATION_CODE");
		//===========modify lim  begin
		//TParm outStaion = new TParm(TJDODBTool.getInstance().select(ADMSQLTool.getInstance().getUserStationList(userId)));
		TParm outStaion = new TParm(TJDODBTool.getInstance().select(ADMSQLTool.getInstance().getUserStationListForDynaSch()));
		//===========modify lim  end
		out_station.setParmValue(outStaion);
		out_station.onQuery();
		TComboBox in_dept = (TComboBox) this.getComponent("IN_DEPT_CODE");
		TParm inDept = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserDeptList(userId)));
		in_dept.setParmValue(inDept);
		in_dept.onQuery();
		TComboBox out_dept = (TComboBox) this.getComponent("OUT_DEPT_CODE");
		TParm outDept = new TParm(TJDODBTool.getInstance().select(ADMSQLTool.getInstance().getUserDeptList(userId)));
		//TParm outDept = new TParm(TJDODBTool.getInstance().select(ADMSQLTool.getInstance().getUserDeptListForDynaSch()));
		out_dept.setParmValue(outDept);
		out_dept.onQuery();
		// ��ת��ʹ�ת�� Grid ��ֵ
		TTable WAIT_IN = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		TTable WAIT_OUT = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		WAIT_IN.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_IN("", station));
		WAIT_IN.retrieve();
		WAIT_OUT.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_OUT("", ""));
		WAIT_OUT.retrieve();
		// �����û�����Ĭ�Ͽ��ҺͲ���
		setValue("IN_DEPT_CODE", "");
		setValue("DEPT_CODE", dept);
		setValue("OUT_DEPT_CODE", "");
		setValue("IN_STATION_CODE", station);
		setValue("STATION_CODE", station);
		setValue("OUT_STATION_CODE", station);
	}

	/**
	 * ȡ������
	 */
	public void onCancelBed() {
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 3) == null) {
			this.messageBox_("��ѡ�񲡻���");
			return;
		}
		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("�˲���δ��ס��");
			return;
		}
		int re = this.messageBox("��ʾ", "ȷ��Ҫȡ���ò����İ�����", 0);
		if (re != 0) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("CASE_NO", admInfo.getValue("CASE_NO", selectRow));
		TParm result = SYSBedTool.getInstance().clearOCCUBed(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005");
		initInStation();
		chose();
	}

    //$$==========liuf==========$$//
	/**
	 * CIS��Ѫ�ǲ�����ס����
	 * @param parm
	 */
	private void sendHL7Mes(TParm parm) {
		System.out.println("sendHL7Mes()");
		// ICU��CCUע��
		String caseNO = parm.getValue("CASE_NO");		
		boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNO);
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNO);
		String type="ADM_IN";
		parm.setData("ADM_TYPE", "I");
		//CIS
		if (IsICU||IsCCU)
		{ 
		  List list = new ArrayList();
		  parm.setData("SEND_COMP", "CIS");
		  list.add(parm);
		  TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
		  if (resultParm.getErrCode() < 0)
				messageBox(resultParm.getErrText());
		}
		//Ѫ��
		List list = new ArrayList();
		parm.setData("SEND_COMP", "NOVA");	
		list.add(parm);
		TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
		if (resultParm.getErrCode() < 0)
		  messageBox(resultParm.getErrText());
	} 
	  //$$==========liuf==========$$//
	

	/**
	 * ȡ��ת��
	 * @param parm 
	 */	
	public void onCancelTrans(){ 
		TTable table = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		int selectRow = table.getSelectedRow();
		if(selectRow<0){
			this.messageBox("��ѡ��Ҫȡ��ת�ƵĲ���.");
			return ;
		}
		String caseNo = (String)table.getValueAt(selectRow, 2) ;
		TParm parm = new TParm() ;
		parm.setData("OPT_USER",Operator.getID()) ;
		parm.setData("OPT_TERM",Operator.getIP()) ;
		parm.setData("DATE",SystemTool.getInstance().getDate()) ;
		parm.setData("CASE_NO", caseNo);

		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onUpdateTransAndLog", parm); 	
		if(result.getErrCode()<0){
			messageBox("ȡ��ת��ʧ��.") ;
		}else{
			initInStation();
			TTable outTable = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
			outTable.retrieve();
			creatDataStore("WAIT_OUT");	
			TTable inTable = (TTable) this.callFunction("UI|WAIT_IN|getThis");
			inTable.retrieve();
			creatDataStore("WAIT_IN");			
			messageBox("ȡ��ת�Ƴɹ�.") ; 
		}
	}
	/**
	 * ȡ��סԺ         chenxi   modify  20130417
	 */
	public void onCancelInHospital(){
		 if (!checkDate())
	            return;
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();
		TParm  tableParm = table.getParmValue() ;
		String caseNo = tableParm.getData("CASE_NO", selectRow).toString() ; 
		TParm result = new TParm();
		//=================ִ��ȡ��סԺ����
      int check = this.messageBox("��Ϣ", "�Ƿ�ȡ����", 0);
      if (check == 0) {
          TParm parm = new TParm();
          parm.setData("CASE_NO", caseNo);
          parm.setData("PSF_KIND", "INC");
          parm.setData("PSF_HOSP", "");
          parm.setData("CANCEL_FLG", "Y");
          parm.setData("CANCEL_DATE", SystemTool.getInstance().getDate());
          parm.setData("CANCEL_USER", Operator.getID());
          parm.setData("OPT_USER", Operator.getID());
          parm.setData("OPT_TERM", Operator.getIP());
          if (null != Operator.getRegion()
              && Operator.getRegion().length() > 0) {
              parm.setData("REGION_CODE", Operator.getRegion());
          }
         result = TIOM_AppServer.executeAction(
                  "action.adm.ADMInpAction", "ADMCanInp", parm); //
          if (result.getErrCode() < 0) {
              this.messageBox("E0005");
          } else {
              this.messageBox("P0005");
              
              // add by wangbin 20141017 ȡ��סԺɾ����ʱ���Ӧ������ START
              this.cancelMroRegAppointment(caseNo);
              // add by wangbin 20141017 ȡ��סԺɾ����ʱ���Ӧ������ END
              
              // add by wangbin 20141017 ����ѳ������ȡ��סԺ�����ݽ��й黹�����趨 START
              this.updateRtnDateByCaseNo(caseNo);
              // add by wangbin 20141017 ����ѳ������ȡ��סԺ�����ݽ��й黹�����趨 END
              
              initInStation();
              chose();
          }
	}
	}
	  /**
     * �������
     *
     * @return boolean
     */
    public boolean checkDate() {
    	TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 3) == null) {
			this.messageBox_("��ѡ�񲡻���");
			return false;
		}
		TParm  tableParm = table.getParmValue() ;
		String caseNo = tableParm.getData("CASE_NO", selectRow).toString() ; 
		//==============Ԥ����δ��,����ȡ��סԺ
		 TParm result = BILPayTool.getInstance().selBilPayLeft(caseNo);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrName()) ;
            return false;
        }
        if (result.getDouble("PRE_AMT", 0) > 0) {
        	 this.messageBox("�˲�������Ԥ����δ��,����ȡ��סԺ");
            return false;
        }
        //==================�ԼƷѲ���ȡ��סԺ
        boolean checkflg =  IBSOrdermTool.getInstance().existFee(tableParm.getRow(selectRow));
        if(!checkflg){
      	  messageBox("�Ѳ�������,����ȡ��סԺ") ;
      	  return false;
        }
        // ���ҽ���Ƿ���ҽ��
     TParm    parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        if (this.checkOrderisEXIST(parm)) {    
        	this.messageBox( "�ò����ѿ���ҽ��������ȡ��סԺ��");
        	  callFunction("UI|save|setEnabled", false);
        	  return false  ;     
        }
      
    	return true ;
    }
    /**
	 * ���ò����Ƿ���ҽ��
	 * 
	 * 
	 */
	public boolean checkOrderisEXIST(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo + "' AND DC_DATE IS NULL ";
		TParm result = new TParm(TJDODBTool.getInstance().select(checkSql));
		// ���û��Ϊִ�е����ݷ������ݼ�����Ϊ0
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	
	/**
	 * ����������ȡ��
	 * 
	 * @param caseNo
	 *            �����
	 * @author wangbin 2014/10/17
	 */
	private void cancelMroRegAppointment(String caseNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());

		TParm result = MROBorrowTool.getInstance().cancelMroRegAppointment(
				parm, null);

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
	}
	
	/**
	 * ����ѳ������ȡ��סԺ�����ݽ��й黹�����趨
	 * 
	 * @param caseNo
	 *            �����
	 * @author wangbin 2014/10/17
	 */
	private void updateRtnDateByCaseNo(String caseNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);

		TParm result = MROQueueTool.getInstance().updateRtnDateByCaseNo(parm);

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
	}
}
