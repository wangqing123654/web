package com.javahis.ui.adm;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import jdo.adm.ADMInpTool;
import jdo.adm.ADMSQLTool;
import jdo.adm.ADMTransLogTool;
import jdo.adm.ADMWaitTransTool;
import jdo.adm.ADMXMLTool;
import jdo.bil.BILPayTool;
import jdo.hl7.Hl7Communications;
import jdo.ibs.IBSOrdermTool;
import jdo.inw.InwForOutSideTool;
import jdo.odi.OdiOrderTool;
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
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.dongyang.wcomponent.ui.WMatrix;
import com.dongyang.wcomponent.ui.WPanel;
import com.dongyang.wcomponent.ui.model.DefaultWMatrixModel;
import com.dongyang.wcomponent.util.DropTargetUtil;
import com.dongyang.wcomponent.util.IDropTarget;
import com.dongyang.wcomponent.util.TiString;
import com.javahis.component.BaseCard;
import com.javahis.component.S_Card;
import com.javahis.component.T_Card;
import com.javahis.util.DateUtil;
import com.tiis.ui.TiPanel;


/**
 * <p>
 * Title: ���ת���� - <��ק����>
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
public class ADMWaitTransDropControl extends TControl {


	TParm patInfo = null;// ��ת��Ĳ�����Ϣ

	/** ## del

	TParm admPat = null;// ��Ժ�Ĳ�����Ϣ

	## */

	/** */
	TParm admInfo;// ��¼������λ״̬

	/** */
	private TParm newBaby =new TParm();

	//
	public void onInit() {
		super.onInit();
		pageInit();
	}

	/**
	 * ҳ���ʼ��
	 */
	private void pageInit() {

		//
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

		//## ��ק�޸ĳ�ʼ��

		//��ʼ����ͷ��
		onQueryData();

		//
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		waitIn.getTable().setDragEnabled(true);
		//

	}

	/***********  �¼��봲ͷ������  *************/

	//##private TPanel tiPanel3 = new TPanel();
	private JScrollPane jScrollPane1 = new JScrollPane();

	// ����
	private TComboBox stationCode;
	/** �򿨵�ѡ */
	private TRadioButton sCard;
	/** ϸ����ѡ  */
	private TRadioButton tCard;
	/** �������� */
	private TTextField sickCount;
	/** ��λ���� */
	private TTextField bedTotal;
	private TPanel tiPanel1;
	private TiPanel tiPanel2 = new TiPanel();


	/**
	 *
	 */
	public void onQueryData() {

		this.initBedCard();
	}

	/**
	 * ���Ų����봲
	 */
	public void onCheckin(int selectRow) {

		// �õ���ת��table
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");

		// �õ���ת��DS
		TDataStore ds = waitIn.getDataStore();

		/** ## del in
			// �õ���Ժ����table
			//TTable checkIn = (TTable) this.callFunction("UI|in|getThis");
			//checkIn.setSelectedRow(selectRow);
		 ## */

		int waitIndex = waitIn.getSelectedRow();// ��ת���ѡ���к�
		if (waitIndex < 0) {
			this.messageBox_("��ѡ��Ҫ��ס�Ĳ���!");
			return;
		}

		/** ## del in
		int checkIndex = checkIn.getSelectedRow();// ��Ժ�����б�ѡ���к�
		if (checkIndex < 0) {
			this.messageBox_("��ѡ��Ҫ��ס�Ĵ�λ!");
			return;
		}
		## */

		//ˢ�¼��
		if (!check()) {// shibl 20130117 add
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

		/** ## del in
		// ��λ��
		updata.setData("BED_NO",
				checkIn.getValueAt(checkIn.getSelectedRow(), 0));
		## */

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

		/** ## del in
		// ��λ��
		updata.setData("BED_NO",
				checkIn.getValueAt(checkIn.getSelectedRow(), 1));
		## */

		// ����
		updata.setData("DEPT_CODE", ds.getItemString(waitIndex,
						"IN_DEPT_CODE"));
		// dataStore

		updata.setData("OPT_USER", Operator.getID());
		updata.setData("OPT_TERM", Operator.getIP());
		// ��鲡���Ƿ����
		if (checkOccu(waitIn.getValueAt(waitIndex, 1).toString())) {
			updata.setData("OCCU_FLG", "Y");// ��ʾ�ò������й���������

			/** ## del in
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
			 ## */

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
		} else {
			this.messageBox("P0005");
			//## ��ʱ������  sendHL7Mes(updata);
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

		/** ## del

		admPat = ADMWaitTransTool.getInstance().selAdmPat(parm); // ��Ժ�Ĳ�����Ϣ

		##*/

	}

	/**
	 * ��ת��ת�� ����combo ��ѡ�¼�
	 */
	public void chose() {

		this.onSelectIn();
		this.onSelectOut(false);
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
		Timestamp date = SystemTool.getInstance().getDate(); //=======  chenxi modify 20130228
		if(tag.equals("WAIT_IN")){
			newBaby=new TParm();
		}
		//ѭ��table ��ʾ�����������Ա�����
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
			result = ADMInpTool.getInstance().selectBedNo(parm);
			// �õ���������
			String[] AGE = StringTool.CountAgeByTimestamp(pat.getBirthday(),
					date);
			//=================  chenxi modify 20130228
			// ��table��ֵ
			if (tag.equals("WAIT_IN")) {

				table.setValueAt(pat.getName(), i, 2);
				table.setValueAt(pat.getSexCode(), i, 3);
				table.setValueAt(result.getValue("BED_NO_DESC", 0), i, 4); //=====ԤԼ����
				table.setValueAt(AGE[0], i, 5);

				//
				newBaby.addData("NEW_BORN_FLG", this.getNewBornFlg(mrNo, caseNo));
				newBaby.addData("MRNO", mrNo);
				newBaby.addData("ACTION","COUNT",newBaby.getCount("MRNO"));

			} else {
				table.setValueAt(pat.getName(), i, 3);
				table.setValueAt(pat.getSexCode(), i, 4);
				table.setValueAt(AGE[0], i, 5);
			}

		}
//           System.out.println(newBaby);
	}

	/**
	 * �õ�������ע��
	 * @param mrNo
	 * @param caseNo
	 * @return
	 */
	private String getNewBornFlg(String mrNo,String caseNo){

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT A.NEW_BORN_FLG FROM ADM_INP A ");
		sb.append("   WHERE  A.CASE_NO = '"+caseNo+"'");
		sb.append("   AND A.MR_NO = '"+mrNo+"'");
//       System.out.println("===+++====sb.toString() sb.toString() is :"+sb.toString());
        return new TParm(TJDODBTool.getInstance().select(sb.toString())).getValue("NEW_BORN_FLG", 0);
	}

	/**
	 * ��Ժ������ѡ�¼�
	 */
	public void onInStation() {

		//queryInStation_old();

		//�����·���
		queryInStation_new();
	}

	/**
	 *  ������Ϣ������   <##ֹͣʹ��###>
	 */
	private void queryInStation_old(){

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
		sendParm.setData("STATION_CODE", parm
				.getData("STATION_CODE", selectRow));
		// ����ҽʦ
		sendParm.setData("VS_DR_CODE", parm.getData("VS_DR_CODE", selectRow));
		// ����ҽʦ
		sendParm.setData("ATTEND_DR_CODE", parm.getData("ATTEND_DR_CODE",
				selectRow));
		// ������
		sendParm.setData("DIRECTOR_DR_CODE", parm.getData("DIRECTOR_DR_CODE",
				selectRow));
		// ���ܻ�ʿ
		sendParm.setData("VS_NURSE_CODE", parm.getData("VS_NURSE_CODE",
				selectRow));
		// ��Ժ״̬
		sendParm.setData("PATIENT_CONDITION", parm.getData("PATIENT_CONDITION",
				selectRow));
		// ������
		sendParm.setData("DIRECTOR_DR_CODE", parm.getData("DIRECTOR_DR_CODE",
				selectRow));
		// BED_OCCU_FLG
		sendParm.setData("BED_OCCU_FLG", parm
				.getData("BED_OCCU_FLG", selectRow));
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
		parm.setData("STATION_CODE",
				getValue("STATION_CODE").toString() == null ? "" : getValue(
						"STATION_CODE").toString());
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

		//initInStationBedCard_old();
	}

	/**
	 * [�����´�ͷ���Ժ�˷����Ѿ���װ�ڴ�ͷ������] ��ͣʹ��
	 */
	public void initInStationBedCard_old(){

		/** ## del in
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

		 	##*/
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
			filter += " IN_STATION_CODE ='"
					+ this.getValueString("IN_STATION_CODE") + "'";
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
	public void onSelectOut(boolean isInit) {
		String filter = "";

		if( isInit ){
			//��ʼ����¼��-���Ҳ���
			filter += " OUT_STATION_CODE ='"+ Operator.getStation() + "'";
		    filter += " AND OUT_DEPT_CODE ='"+ Operator.getDept() + "'";

		    //
            this.setValue("OUT_STATION_CODE", Operator.getStation());

		}else{

			if (this.getValueString("OUT_STATION_CODE").length() > 0)
				filter += " OUT_STATION_CODE ='"
						+ this.getValueString("OUT_STATION_CODE") + "'";
			if (this.getValueString("OUT_DEPT_CODE").length() > 0)
				filter += " AND OUT_DEPT_CODE ='"
						+ this.getValueString("OUT_DEPT_CODE") + "'";
		}

		TTable table = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		table.setFilter(filter);
		table.filter();
		table.setDSValue();
		creatDataStore("WAIT_OUT");
	}

	/**
	 * ���Ų����봲   <##ֹͣʹ��###>
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
		if (!check()) {// shibl 20130117 add
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
		updata.setData("BED_NO", checkIn
				.getValueAt(checkIn.getSelectedRow(), 0));
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
		updata.setData("BED_NO", checkIn
				.getValueAt(checkIn.getSelectedRow(), 1));
		// ����
		updata.setData("DEPT_CODE", ds.getItemString(waitIndex,
						"IN_DEPT_CODE"));
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
					|| !waitIn.getValueAt(waitIndex, 0).toString().equals(
							checkIn.getValueAt(checkIndex, 2).toString())) {
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
		} else {
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
	 * ��˴�λ   <##ֹͣʹ��###>
	 * @return boolean
	 */
	public boolean check() {
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		TTable waitTable = (TTable) this.callFunction("UI|WAIT_IN|getThis"); //chenxi modify 20130308
		if (table.getSelectedRow() < 0) {
			this.messageBox("δѡ��λ");
			return false;
		}
		//=============shibl 20130106 add======���˵�ͬһ����δˢ��ҳ��=============================
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		TParm inParm = new TParm();
		inParm.setData("BED_NO", parm.getValue("BED_NO"));
		TParm result = ADMInpTool.getInstance().QueryBed(inParm);
		String APPT_FLG = result.getCount() > 0 ? result
				.getValue("APPT_FLG", 0) : "";
		String ALLO_FLG = result.getCount() > 0 ? result
				.getValue("ALLO_FLG", 0) : "";
		String BED_STATUS = result.getCount() > 0 ? result.getValue(
				"BED_STATUS", 0) : "";
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
			if (!waitTable.getValueAt(waitTable.getSelectedRow(), 4).equals(
					parm.getValue("BED_NO_DESC"))) {
				int check = this.messageBox("��Ϣ", "�˴��ѱ�Ԥ�����Ƿ��������", 0);
				if (check != 0) {
					onReload();
					return false;
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

		/// onOutDept_old();

		//�����·���
		onOutDept_new();
	}

	/**
	 *  ת�ƹ���   <##ֹͣʹ��###>
	 */
	private void onOutDept_old() {

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
		sendParm.setData("OUT_STATION_CODE", parm.getData("STATION_CODE",
				selectRow));
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
		outTable.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_OUT(
				this.getValueString("OUT_DEPT_CODE"),
				this.getValueString("OUT_STATION_CODE")));
		outTable.retrieve();
		//$$==================$$//
		creatDataStore("WAIT_OUT");
	}

	/**
	 * ��������
	 */
	public void onBed() {

		// doOnBed_old();

		//�����·���
		doOnBed_new();
	}

	/**
	 * ��������   <##ֹͣʹ��###>
	 */
	private void doOnBed_old(){

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
		sendParm.setData("STATION_CODE", admInfo.getValue("STATION_CODE",
				selectRow));
		sendParm.setData("BED_NO", admInfo.getValue("BED_NO", selectRow));
		TParm bed = new TParm();
		bed.setData("BED_NO", admInfo.getValue("BED_NO", selectRow));
		TParm check = SYSBedTool.getInstance().queryRoomBed(bed);
		String caseNo = admInfo.getValue("CASE_NO", selectRow);
		int count = check.getCount("BED_NO");
		boolean flg = false;
		for (int i = 0; i < count; i++) {
			if ("Y".equals(check.getData("ALLO_FLG", i))
					&& !caseNo.equals(check.getData("CASE_NO", i))) {
				flg = true;
			}
		}
		if (flg == true) {
			int checkFlg = this.messageBox("��Ϣ", "�˲���������������!�Ƿ����������", 0);
			if (checkFlg != 0)
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
		TParm outStaion = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserStationListForDynaSch()));
		//===========modify lim  end
		out_station.setParmValue(outStaion);
		out_station.onQuery();
		TComboBox in_dept = (TComboBox) this.getComponent("IN_DEPT_CODE");
		TParm inDept = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserDeptList(userId)));
		in_dept.setParmValue(inDept);
		in_dept.onQuery();
		TComboBox out_dept = (TComboBox) this.getComponent("OUT_DEPT_CODE");
		TParm outDept = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserDeptList(userId)));
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

		//doOnCancelBed_old();

		//�����·���
		doOnCancelBed_new();
	}

	/**
	 * ȡ������   <##ֹͣʹ��###>
	 */
	private void doOnCancelBed_old(){

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

		//System.out.println("sendHL7Mes()");

		// ICU��CCUע��
		String caseNO = parm.getValue("CASE_NO");
		boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNO);
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNO);
		String type = "ADM_IN";
		parm.setData("ADM_TYPE", "I");
		//CIS
		if (IsICU || IsCCU) {
			List list = new ArrayList();
			parm.setData("SEND_COMP", "CIS");
			list.add(parm);
			TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(
					list, type);
			if (resultParm.getErrCode() < 0)
				messageBox(resultParm.getErrText());
		}
		//Ѫ��
		List list = new ArrayList();
		parm.setData("SEND_COMP", "NOVA");
		list.add(parm);
		TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,
				type);
		if (resultParm.getErrCode() < 0)
			messageBox(resultParm.getErrText());
	}

	//$$==========liuf==========$$//

	/**
	 * ȡ��ת��( ���ˢ�´�ͷ������ )
	 * @param parm
	 */
	public void onCancelTrans() {

		//�����·���
		doOnCancelTrans_new();
	}


	/**
	 * ȡ��סԺ
	 */
	public void onCancelInHospital() {

		// doOnCancelInHospital_old();

		//�����·���
		doOnCancelInHospital_new();
	}

	/**
	 * ȡ��סԺ   <##ֹͣʹ��###>   chenxi   modify  20130417
	 */
	private void doOnCancelInHospital_old(){

		if (!checkDate())
			return;
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		String caseNo = tableParm.getData("CASE_NO", selectRow).toString();
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
			result = TIOM_AppServer.executeAction("action.adm.ADMInpAction",
					"ADMCanInp", parm); //
			if (result.getErrCode() < 0) {
				this.messageBox("E0005");
			} else {
				this.messageBox("P0005");
				initInStation();
				chose();
			}
		}
	}

	/**
	 * �������  <##ֹͣʹ��###>
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
		TParm tableParm = table.getParmValue();
		String caseNo = tableParm.getData("CASE_NO", selectRow).toString();
		//==============Ԥ����δ��,����ȡ��סԺ
		TParm result = BILPayTool.getInstance().selBilPayLeft(caseNo);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrName());
			return false;
		}
		if (result.getDouble("PRE_AMT", 0) > 0) {
			this.messageBox("�˲�������Ԥ����δ��,����ȡ��סԺ");
			return false;
		}
		//==================�ԼƷѲ���ȡ��סԺ
		boolean checkflg = IBSOrdermTool.getInstance().existFee(
				tableParm.getRow(selectRow));
		if (!checkflg) {
			messageBox("�Ѳ�������,����ȡ��סԺ");
			return false;
		}
		// ���ҽ���Ƿ���ҽ��
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		if (this.checkOrderisEXIST(parm)) {
			this.messageBox("�ò����ѿ���ҽ��������ȡ��סԺ��");
			callFunction("UI|save|setEnabled", false);
			return false;
		}

		return true;
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

	//#####

	/**
	 *
	 */
	private void initBedCard() {

		tiPanel1 = ((TPanel) getComponent("tPanel_3"));
		//testBorder3 = new TitledBorder(BorderFactory.createEtchedBorder(new Color(228, 255, 255),new Color(112, 154, 161)),"�����ػ�");
		//tiPanel2.setBounds(new Rectangle(0, 1, 1000, 530));
		tiPanel2.setSize(1010, 680);
		tiPanel2.setLayout(null);
		jScrollPane1.setBounds(new Rectangle(3, 3, 992, 502));
		tiPanel1.add(tiPanel2, null);
		tiPanel2.add(jScrollPane1, null);

		//##jScrollPane1.getViewport().add(tiPanel3, null);
		sickCount = (TTextField) this.getComponent("tTextField_1");
		bedTotal = (TTextField) this.getComponent("bedTotal_00");

		sCard = (TRadioButton) this.getComponent("S_CARD");
		tCard = (TRadioButton) this.getComponent("T_CARD");

		//����
		this.stationCode = (TComboBox) this.getComponent("STATION_CODE");

		//��ʼ����λ
		buildBedData();
	}


	/** ��ͷ�� */
	private WMatrix jList = null;

	/**
	 * ��̬���촲λ����
	 */
	private void buildBedData() {

		//@@
		DefaultWMatrixModel model = new DefaultWMatrixModel();

		//##tiPanel3.removeAll();
		int j = 0;
		// this.messageBox("sCard"+sCard);

		//this.messageBox("Operator.getRegion()  " + Operator.getRegion());
		//this.messageBox("this.stationCode.getValue()  " +this.stationCode.getValue());

		//������ʱ��
		Timestamp serverTime = TJDODBTool.getInstance().getDBTime();

		// �ж� �ò�����ϵͳ��δ�贲λ
		String bedsSql = " SELECT BED_NO,BED_NO_DESC,ALLO_FLG,BABY_BED_FLG  "  //����Ӥ����
				+ " FROM SYS_BED  " + " WHERE REGION_CODE='"
				+ Operator.getRegion() + "' " + " AND STATION_CODE='"
				+ this.stationCode.getValue() + "' " + " AND ACTIVE_FLG='Y' "
				+ " ORDER BY REGION_CODE,BED_NO ";
		//System.out.println("==bedsSql=="+bedsSql);
		TParm bedParm = new TParm(TJDODBTool.getInstance().select(bedsSql));
		int count = 0;
		int occuBedCount = 0;
		//this.messageBox("bedParm" + bedParm.getCount());
		if (bedParm.getCount() == 0 || bedParm.getCount() == -1) {
			this.messageBox("�ò�����ϵͳ��δ�贲λ");
		} else {

			// ��
			if (sCard.isSelected()) {//
				occuBedCount = 0;

				//this.messageBox("��ʾ��");

				// ��ѯסԺ��������
				String sPatSql = "Select A.bed_no_desc,C.pat_name,B.mr_no,B.nursing_class,";
				sPatSql += "D.CHN_DESC,E.colour_red,E.colour_green,E.colour_blue,F.colour_red as colour_red1,";
				sPatSql += "F.colour_green as colour_green1,F.colour_blue as colour_blue1,B.PATIENT_STATUS,A.BED_OCCU_FLG,";
				sPatSql += "B.REGION_CODE,B.case_no,C.BIRTH_DATE,C.SEX_CODE,'' ins_status,B.CLNCPATH_CODE,B.DEPT_CODE,B.IPD_NO,";
				sPatSql += "A.ROOM_CODE,G.BEDTYPE_DESC, ";
				sPatSql += "to_number(to_char(sysdate,'yyyy'))-to_number(to_char(C.BIRTH_DATE,'yyyy')) age,TO_CHAR(B.in_date,'yyyy/MM/dd HH24:mm:ss') IN_DATE";
				//## --��ק�������--
				sPatSql += " ,A.bed_no,A.BED_STATUS," ;
				sPatSql += " B.VS_DR_CODE,B.ATTEND_DR_CODE,B.DIRECTOR_DR_CODE,B.VS_NURSE_CODE,B.PATIENT_CONDITION ";
				//--������
				sPatSql += " ,B.NEW_BORN_FLG,A.BABY_BED_FLG ";
				//--�������㷨
				sPatSql += " ,B.in_date as IN_DATE_2 ";
				//##
				sPatSql += "from SYS_BED A,ADM_INP B,SYS_PATINFO C,(select * from sys_dictionary where group_id='SYS_SEX') D,";
				sPatSql += "ADM_NURSING_CLASS E,ADM_PATIENT_STATUS F,SYS_BED_TYPE G ";
				sPatSql += "Where A.REGION_CODE='"+Operator.getRegion()+"' ";
				sPatSql += "and A.station_code='"+this.stationCode.getValue()+"' ";
				sPatSql += "and A.REGION_CODE=B.REGION_CODE ";
				sPatSql += "and A.case_no=B.case_no ";
				sPatSql += "and B.mr_no=C.mr_no ";
				sPatSql += "and C.SEX_CODE=D.ID ";
				sPatSql += "and E.NURSING_CLASS_Code(+)=B.NURSING_CLASS ";
				sPatSql += "and F.PATIENT_STATUS_code(+)=B.PATIENT_STATUS ";
				sPatSql += "and A.BED_TYPE_CODE=G.BED_TYPE_CODE(+) ";
//                sPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "; //�������һ��
                sPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' "; //�粻ȥ����λ״̬���� �еİ�����ʾ������
				sPatSql += "ORDER BY A.REGION_CODE,A.BED_NO";

				//				System.out.println("=====sPatSql====="+sPatSql);
				TParm sPatParm = new TParm(TJDODBTool.getInstance().select(
						sPatSql));
				//this.messageBox("++sPatParm+"+sPatParm);
				int result_count = sPatParm.getCount();

				//this.messageBox("SB SBL ++result_count  +"+result_count);
				//this.messageBox("sCard"+result_count);
				// ��λ����
				int row = Integer.parseInt(String.valueOf(bedParm.getCount()));
				bedTotal.setText(row+"");
				//(int) TiMath.ceil(row / 5.0, 0)

				//System.out.println("=============�򿨳���================"+row);

				for (int i = 0; i < row; i++) {
					S_Card s_card[] = new S_Card[row];
					//this.messageBox("==i=="+i);
					// ������ͬ��˵������ʹ�ô�λ
					// String.valueOf(((Vector)Sta_bed.get(1)).get(i)).equals(String.valueOf(((Vector)result.get(0)).get(j)))
					if (result_count != 0
							&& bedParm.getValue("BED_NO_DESC", i).equals(
									sPatParm.getValue("BED_NO_DESC", j))) {

						String s[] = new String[31];
						//this.messageBox("COLOUR_RED====="+sPatParm.getValue("COLOUR_RED", j));
						s[0] = sPatParm.getValue("COLOUR_RED", j).equals("") ? "255"
								: sPatParm.getValue("COLOUR_RED", j);
						s[1] = sPatParm.getValue("COLOUR_GREEN", j).equals("") ? "255"
								: sPatParm.getValue("COLOUR_GREEN", j);
						s[2] = sPatParm.getValue("COLOUR_BLUE", j).equals("") ? "255"
								: sPatParm.getValue("COLOUR_BLUE", j);
						s[3] = sPatParm.getValue("COLOUR_RED1", j).equals("") ? "255"
								: sPatParm.getValue("COLOUR_RED1", j);
						s[4] = sPatParm.getValue("COLOUR_GREEN1", j).equals("") ? "255"
								: sPatParm.getValue("COLOUR_GREEN1", j);
						s[5] = sPatParm.getValue("COLOUR_BLUE1", j).equals("") ? "255"
								: sPatParm.getValue("COLOUR_BLUE1", j);
						// PATIENT_STATUS
						s[6] = sPatParm.getValue("PATIENT_STATUS", j);
						// BED_OCCU_FLG
						for (int k = 0; k < sPatParm.getCount("MR_NO"); k++) {
							if (sPatParm.getValue("MR_NO", j).equals(
									sPatParm.getValue("MR_NO", k))) {
								if (sPatParm.getValue("BED_OCCU_FLG", j)
										.equals("Y")) {
									s[7] = "Y";
									occuBedCount++;
									break;
								}
							}
						}
						if (s[7] == null || s[7].length() <= 0)
							s[7] = sPatParm.getValue("BED_OCCU_FLG", j);
						// REGION_CODE
						s[8] = sPatParm.getValue("REGION_CODE", j);
						// case_no
						s[9] = sPatParm.getValue("CASE_NO", j);
						// BIRTH_DATE
						s[10] = sPatParm.getValue("BIRTH_DATE", j);
						// SEX_CODE
						s[11] = sPatParm.getValue("SEX_CODE", j);
						// ins_status
						s[12] = sPatParm.getValue("INS_STATUS", j);
						// CLNCPATH_CODE
						s[13] = sPatParm.getValue("CLNCPATH_CODE", j);
						s[14] = sPatParm.getValue("DEPT_CODE", j);
						s[15] = sPatParm.getValue("IPD_NO", j);
						s[16] = sPatParm.getValue("ROOM_CODE", j);
						s[17] = sPatParm.getValue("BEDTYPE_DESC", j);
                        //
						s[18] = sPatParm.getValue("AGE", j);
						s[19] = sPatParm.getValue("IN_DATE", j);

						//##
						s[20] = sPatParm.getValue("BED_NO", j);
						s[21] = sPatParm.getValue("BED_STATUS", j);
						s[22] = sPatParm.getValue("VS_DR_CODE", j);
						s[23] = sPatParm.getValue("ATTEND_DR_CODE", j);
						s[24] = sPatParm.getValue("DIRECTOR_DR_CODE", j);
						s[25] = sPatParm.getValue("VS_NURSE_CODE", j);
						s[26] = sPatParm.getValue("PATIENT_CONDITION", j);
						s[27] = sPatParm.getValue("NEW_BORN_FLG", j);
						s[28] = sPatParm.getValue("PATIENT_STATUS", j);

						//�������㷨
						//Timestamp indate = (Timestamp)sPatParm.getData("IN_DATE_2", j);
						Timestamp birth = (Timestamp)sPatParm.getData("BIRTH_DATE", j);
						//s[29] = DateUtil.showAge( birth, indate);
						s[29] = DateUtil.showAge( birth,serverTime);

						//Ӥ����
						s[30] = sPatParm.getValue("BABY_BED_FLG", j);

						//
						//this.messageBox("BED_NO"+sPatParm.getValue("BED_NO", j));
						s_card[i] = new S_Card( sPatParm.getValue("BED_NO_DESC", j),
								                sPatParm.getValue("PAT_NAME", j),
								                sPatParm .getValue("MR_NO", j),
								                sPatParm.getValue("NURSING_CLASS", j) == null ? "" : sPatParm.getValue("NURSING_CLASS", j),
								                (String) stationCode.getValue(),
								                sPatParm.getValue("CHN_DESC", j) == null ? "": sPatParm.getValue("CHN_DESC", j),
								                		
								                s );

						if (j < result_count - 1) {
							j++;
						}
						count++;
						// �ǿմ������
					} else {
						// String.valueOf(((Vector)Sta_bed.get(1)).get(i))
						s_card[i] = new S_Card( bedParm.getValue("BED_NO_DESC", i),
								                "",
								                bedParm.getValue("BED_NO", i),
								                bedParm.getValue("BABY_BED_FLG", i) );
					}
					//s_card[i].setPreferredSize(new Dimension(150, 60));
					s_card[i].setPreferredSize(new Dimension(159, 71));
					//this.messageBox("=====s_card["+i+"]"+s_card[i].sBed_no);
					// s_card[i].addMouseListener(this) ;
					//TButton btest=new TButton();
					//btest.setLabel("ok");
					//tiPanel3.add(btest,null);

					//##tiPanel3.add(s_card[i], null);
					//@@
					WPanel jp = new WPanel();
					jp.setId("jp_" + i);
					jp.add(s_card[i], null);
					model.addElement(jp);
					jp.setRoundedBorder();

				}
				count -= occuBedCount;
				// ϸ��
			} else if (tCard.isSelected()) {
				occuBedCount = 0;

				//this.messageBox("��ʾϸ��");

				// ��ѯסԺ��������
				String tPatSql = "Select A.bed_no_desc,C.pat_name,B.mr_no,B.nursing_class,D.CHN_DESC,";
				tPatSql += /*"case NHI_CTZ_FLG when 'Y' then 'ҽ��' when 'N' then '�Է�' end*/"E.CTZ_DESC CTZ,B.PATIENT_STATUS,G.USER_NAME,'' BLANK,";//modify by sunqy 20140526
				tPatSql += "to_number(to_char(sysdate,'yyyy'))-to_number(to_char(C.BIRTH_DATE,'yyyy')) age,TO_CHAR(B.in_date,'yyyy/MM/dd HH24:mm:ss') IN_DATE,A.case_no,";
				tPatSql += "I.colour_red,I.colour_green,I.colour_blue,J.colour_red  as colour_red1,J.colour_green as colour_green1,J.colour_blue as colour_blue1,A.BED_OCCU_FLG,";
				tPatSql += "B.REGION_CODE,B.case_no patCaseNo,C.BIRTH_DATE,C.SEX_CODE,'' ins_status,B.CLNCPATH_CODE,B.DEPT_CODE,B.IPD_NO ";
				//## --��ק�������--
				tPatSql += " ,A.bed_no,A.BED_STATUS," ;
				tPatSql += " B.VS_DR_CODE,B.ATTEND_DR_CODE,B.DIRECTOR_DR_CODE,B.VS_NURSE_CODE,B.PATIENT_CONDITION ";
				//--������
				tPatSql += " ,B.NEW_BORN_FLG,A.BABY_BED_FLG ";
				//--�������㷨
				tPatSql += " ,B.in_date as IN_DATE_2 ";
				//##
				tPatSql +="from SYS_BED A,ADM_INP B,SYS_PATINFO C,(select * from sys_dictionary where group_id='SYS_SEX') D,";
				tPatSql +="SYS_CTZ E,SYS_OPERATOR G,ADM_NURSING_CLASS I,ADM_PATIENT_STATUS J ";
				tPatSql +="Where A.REGION_CODE='"+Operator.getRegion()+"' ";
				tPatSql +="and A.station_code='"+this.stationCode.getValue()+"' ";
				tPatSql +="and A.REGION_CODE=B.REGION_CODE ";
				tPatSql +="and A.case_no=B.case_no ";
				tPatSql +="and B.mr_no=C.mr_no ";
				tPatSql +="and C.SEX_CODE=D.ID ";
				tPatSql +="and B.ctz1_code=E.ctz_code(+) ";//add ���(+)
				tPatSql +="and B.VS_DR_CODE=G.user_id(+) ";// modified by WangQing 20170516 add(+)
				tPatSql +="AND b.nursing_class=i.nursing_class_code(+) ";
				tPatSql +="AND b.patient_status=j.patient_status_code(+) ";
//				tPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' ";
				tPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y'";

				//tPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' ";
				tPatSql +="ORDER BY A.REGION_CODE,A.BED_NO";

				//				System.out.println("=====tPatSql====="+tPatSql);
				//
				TParm tPatParm = new TParm(TJDODBTool.getInstance().select(
						tPatSql));
				int result_count = tPatParm.getCount();
				//this.messageBox("tCard"+result_count);
				int row = bedParm.getCount();
				bedTotal.setText(row+"");
				//(int)TiMath.ceil(row/5.0,0)

				T_Card t_card[] = new T_Card[row];
				for (int i = 0; i < row; i++) {
					String s[] = new String[33];
					//��ռ�ô�λ
					if (result_count != 0
							&& result_count != 0
							&& bedParm.getValue("BED_NO_DESC", i).equals(
									tPatParm.getValue("BED_NO_DESC", j))) {

						//this.messageBox("case no"+tPatParm.getValue("CASE_NO",j));
						//����ϸ������
						s[0] = tPatParm.getValue("CHN_DESC", j);
						s[1] = tPatParm.getValue("CTZ", j);
						s[2] = tPatParm.getValue("PATIENT_STATUS", j);
						s[3] = tPatParm.getValue("USER_NAME", j);
						s[4] = tPatParm.getValue("BLANK", j);
						s[5] = tPatParm.getValue("AGE", j);
						s[6] = tPatParm.getValue("IN_DATE", j);
						//20150126 wangjingchun ע�͵� 817
						//ȡ�������  ADM_INP ADM_INPDIAG
//						String diagSql = "Select b.icd_chn_desc ";
//						diagSql += "from ADM_INP a ,SYS_DIAGNOSIS b ";
//						diagSql += "where a.CASE_NO='"
//								+ tPatParm.getValue("CASE_NO", j) + "' ";
//						//diagSql+="and a.maindiag_flg = 'Y' ";
//						diagSql += "and b.icd_code = a.MAINDIAG ";
//						//diagSql+="order by a.io_type desc";
//
//						//		                System.out.println("=====diagSql====="+diagSql);
//						TParm diagParm = new TParm(TJDODBTool.getInstance()
//								.select(diagSql));
//						if (diagParm.getCount() == 0
//								|| diagParm.getCount() == -1) {
//							s[7] = "";
//						} else {
//							s[7] = diagParm.getValue("ICD_CHN_DESC", 0);
//						}
						
						//20150126 wangjingchun add 817
						s[7] = this.getIcdChnDesc(tPatParm.getValue("CASE_NO", j));
						//colour_red
						//this.messageBox("colour_red====="+tPatParm.getValue("colour_red", j));
						//System.out.println("++++colour red++++"+tPatParm.getValue("COLOUR_RED", j));
						s[8] = tPatParm.getValue("COLOUR_RED", j).equals("") ? "255"
								: tPatParm.getValue("COLOUR_RED", j);
						s[9] = tPatParm.getValue("COLOUR_GREEN", j).equals("") ? "255"
								: tPatParm.getValue("COLOUR_GREEN", j);
						s[10] = tPatParm.getValue("COLOUR_BLUE", j).equals("") ? "255"
								: tPatParm.getValue("COLOUR_BLUE", j);

						s[11] = tPatParm.getValue("COLOUR_RED1", j).equals("") ? "255"
								: tPatParm.getValue("COLOUR_RED1", j);
						s[12] = tPatParm.getValue("COLOUR_GREEN1", j)
								.equals("") ? "255" : tPatParm.getValue(
								"COLOUR_GREEN1", j);
						s[13] = tPatParm.getValue("COLOUR_BLUE1", j).equals("") ? "255"
								: tPatParm.getValue("COLOUR_BLUE1", j);
						//BED_OCCU_FLG
						for (int k = 0; k < tPatParm.getCount("MR_NO"); k++) {
							if (tPatParm.getValue("MR_NO", j).equals(
									tPatParm.getValue("MR_NO", k))) {
								if (tPatParm.getValue("BED_OCCU_FLG", j)
										.equals("Y")) {
									s[14] = "Y";
									occuBedCount++;
									break;
								}
							}
						}
						if (s[14] == null || s[14].length() <= 0)
							s[14] = tPatParm.getValue("BED_OCCU_FLG", j);
						s[15] = tPatParm.getValue("REGION_CODE", j);
						s[16] = tPatParm.getValue("CASE_NO", j);
						s[17] = tPatParm.getValue("BIRTH_DATE", j);
						s[18] = tPatParm.getValue("SEX_CODE", j);
						s[19] = tPatParm.getValue("INS_STATUS", j);
						s[20] = tPatParm.getValue("CLNCPATH_CODE", j);
						s[21] = tPatParm.getValue("DEPT_CODE", j);
						s[22] = tPatParm.getValue("IPD_NO", j);

						//##
						s[23] = tPatParm.getValue("BED_NO", j);
						s[24] = tPatParm.getValue("BED_STATUS", j);
						s[25] = tPatParm.getValue("VS_DR_CODE", j);
						s[26] = tPatParm.getValue("ATTEND_DR_CODE", j);
						s[27] = tPatParm.getValue("DIRECTOR_DR_CODE", j);
						s[28] = tPatParm.getValue("VS_NURSE_CODE", j);
						s[29] = tPatParm.getValue("PATIENT_CONDITION", j);
						s[30] = tPatParm.getValue("NEW_BORN_FLG", j);

						//�������㷨
						//Timestamp indate = (Timestamp)tPatParm.getData("IN_DATE_2", j);
						Timestamp birth = (Timestamp)tPatParm.getData("BIRTH_DATE", j);
						//s[31] = DateUtil.showAge( birth, indate);
						s[31] = DateUtil.showAge( birth,serverTime);

						//Ӥ����
						s[32] = tPatParm.getValue("BABY_BED_FLG", j);

						t_card[i] = new T_Card( tPatParm.getValue("BED_NO_DESC",j),
								                tPatParm.getValue("PAT_NAME", j),
								                tPatParm.getValue("MR_NO", j),
								                tPatParm.getValue("NURSING_CLASS", j),
								                (String) stationCode.getValue(),
								                s );

						if (j < result_count - 1) {
							j++;
						}
						count++;
					} else {
						//�մ�λ
						t_card[i] = new T_Card( bedParm.getValue("BED_NO_DESC",i),
								                "", bedParm.getValue("BED_NO", i),
								                bedParm.getValue("BABY_BED_FLG", i) );
					}
					t_card[i].setPreferredSize(new Dimension(153, 180));
					//t_card[i].addMouseListener(this) ;

					//##tiPanel3.add(t_card[i], null);
					//@@
					T_Card tc = t_card[i];
					tc.tiL_ipdno.setVisible(false);
					WPanel jp = new WPanel();
					jp.add(tc, null);
					jp.setId("jp_" + i);
					model.addElement(jp);
					jp.setRoundedBorder();

				}
				count -= occuBedCount;
			}
		}
		
		//this.messageBox("�ػ�panel3");
		//���������������
		//## jScrollPane1.setViewportView(tiPanel3);

		//@@
		//
		TTable waitOut = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		TTable waitIn = (TTable) callFunction("UI|WAIT_IN|getThis");
		//
		jList = new WMatrix(model);
		jList.doProcessRowCount(6);
		jScrollPane1.setViewportView(jList);
		//
		doTuoZhuai2ListInit(waitIn, jList);
		doTuoZhuai2TableInit(jList, waitOut);
		//
		this.doMouseClick(jList);

		//���ô�λ����;
		sickCount.setText(String.valueOf(count));
		//tiPanel3.repaint();

	}
	
	/**
	 * ��ȡ�������
	 * 20150126 wangjinghchun add 817
	 * @param caseNo
	 * @return
	 */
	public String getIcdChnDesc(String caseNo){
		String icdChnDesc = "";
		String icdCodeSql = "SELECT A.DESCRIPTION,B.ICD_CHN_DESC FROM ADM_INPDIAG A,SYS_DIAGNOSIS B WHERE A.CASE_NO = '"
						+caseNo
						+"' AND A.MAINDIAG_FLG = 'Y' AND A.ICD_CODE = B.ICD_CODE ORDER BY DECODE (A.IO_TYPE, 'O',1,'M',2,'I',3) ";
//		System.out.println(icdCodeSql);
		TParm icdCodeParm = new TParm(TJDODBTool.getInstance().select(icdCodeSql));
		if(icdCodeParm.getValue("ICD_CHN_DESC", 0).equals("")){
			icdChnDesc = icdCodeParm.getValue("DESCRIPTION", 0);
		}else{
			icdChnDesc = icdCodeParm.getValue("ICD_CHN_DESC", 0);
		}
		return icdChnDesc;
	}

	/**
	 *
	 * @param jList
	 */
	private void doMouseClick(final WMatrix jList) {

		jList.addMouseListener(new MouseAdapter() {

			//���
			public void mouseClicked(MouseEvent e) {

				if (jList.getSelectedIndex() != -1) {

					//����
					if (e.getClickCount() == 1) {

						singleClick(jList);
					}

					//˫��
					if (e.getClickCount() == 2) {

						doubleClick(jList.getSelectedValue());
					}
				}
			}

			//����
			private void singleClick(WMatrix jList) {

				doProcessList2ListMouse(jList);
			}

			//˫��
			private void doubleClick(Object value) {
     
				jList.setDoubleClick(value);
				jList.setSelectedIndex(-1);

				//
/*				WPanel dcvalue = (WPanel) jList.getDoubleClick();
				BaseCard tc = (BaseCard)dcvalue.getComponent(0);
				dcvalue.setToolTipText( tc.sName );*/
			}
		});
	}

	/**
	 * �Ӵ�ͷ����ק���ڶ���Table(ת���б�)
	 * @param jList
	 * @param tb
	 */
	private void doTuoZhuai2TableInit(final WMatrix jList, final TTable tb) {

		//
		DropTargetUtil.doDropTarget(jList, tb, new IDropTarget() {

			@Override
			public void doDrop(String res, DropTargetDropEvent dtde) {

				// messageBox_(" ###  " + res);

				//Object obj = ((WPanel) jList.getSelectedValue()).getComponent(0);

				//ѡ�д�ͷ����ק
				//if( null!=obj ){


					if (null == jList.getDoubleClick()) {

						messageBox_(" ��˫��ѡ��һ����λ!  ");
						return;
					}

					//˫��������
					WPanel dcvalue = (WPanel) jList.getDoubleClick();
					BaseCard tc = (BaseCard)dcvalue.getComponent(0);

					//��֤�մ�
			        if (TiString.isEmpty(tc.sMr_no)) {
			             messageBox_("��ѡ����Ժ������");
			             doCancelClick(jList);
			             return;
			        }

	                //
		        	int check = messageBox("��Ϣ", "ȷ��"+tc.sBed_no+"������ת��������", 0);
					if (check!= 0) {
						//
						doCancelClick(jList);
						return;
					}

					//
                    inOutDept(tc);

					//
	       /*         inOutDept(tc.sCase_no,tc.sMr_no,tc.sCode,tc.sIpdNo,
	                		tc.sDeptCode,tc.sStation_code);*/

	               // messageBox_(" ###  " + tc.sDeptCode);
	               // messageBox_(" ###  " + tc.sStation_code);
				//}
			}
		});
	}

	/**
	 * 1. �ӵ�һ��table(���봲�б�) ��ק�� ��ͷ��
	 * 2. ��ͷ����ק����ͷ��
	 * @param list
	 */
	private void doTuoZhuai2ListInit(final TTable waitIn, final WMatrix list) {

		list.setDragEnabled(true);

		//
		DropTargetUtil.doDropTarget(waitIn, list, new IDropTarget() {

			@Override
			public void doDrop(String res, DropTargetDropEvent dtde) {

				//ListModel model = list.getModel();

				//messageBox_(  " ###2  " + res );

				//messageBox_(  " ###2-1  " + waitIn.getItemString(waitIn.getSelectedRow(),1));

				//messageBox_( " ^^^^^^^^ DoubleClick " +list.getDoubleClick() );

				if (null == list.getDoubleClick()) {

					messageBox_(" ��˫��ѡ��һ����λ!  ");
					return;
				}

				/*
				messageBox_(  " ###ѡ�е�table��  " + waitIn.getSelectedRow() );

				waitIn.getModel().removeRow(waitIn.getSelectedRow());
				String [] str= {"21","32"+Math.random()};
				waitIn.getModel().addRow( str );
				 */

				//
				doBedCardDropTarget( waitIn,list );
			}
		});
	}

	/**
	 * ��ͷ����ק����
	 * @param dcvalue
	 */
	private void doBedCardDropTarget(TTable waitIn,WMatrix list){

		//˫��ѡ�е�Panel
		WPanel dcvalue = (WPanel) list.getDoubleClick();

		//��ǰ����ѡ�е�Panel
		//WPanel svalue = (WPanel) list.getSelectedValue();

		//��ǰ���ѡ�е�Table����
		int tIndex = waitIn.getSelectedRow();

		//messageBox_(  " ###tIndex  " + tIndex );

		//˫���Ĵ�ͷ��
		BaseCard c = (BaseCard) dcvalue.getComponent(0);
        if( TiString.isNotEmpty(c.sMr_no) ){
        	messageBox_(  " ��ѡ��һ���մ�!  "  );
        	return;
        }

        //�����Ĵ�ͷ��
        // BaseCard sc = (BaseCard) svalue.getComponent(0);

        // messageBox_(  " tIndex "  +tIndex);
        // messageBox_(  " svalue " +svalue );


        //���������ק (����)
        if( tIndex>=0 ){

    		//
			String flg = newBaby.getValue("NEW_BORN_FLG", tIndex);

	        //����������֤ #1
	        if(  flg.equals("Y") && !c.baby_bed_flg.equals("Y") ){
	        	 this.messageBox_("��������ѡ��Ӥ������");
	        	 this.doCancelClick(jList);
	        	 return;
	        }
	        // #2
//	        System.out.println("====newBabynewBaby is ::"+newBaby);
//	        System.out.println("1111flg flg flg is :"+flg);
//	        System.out.println("2222flg flg flg is :"+c.baby_bed_flg);
	        	if( !flg.equals("Y") && c.baby_bed_flg.equals("Y") ){
		        	 this.messageBox_("���˲�������Ӥ������");
		        	 this.doCancelClick(jList);
		        	 return;
	        }
	        

        	//
        	int check = this.messageBox("��Ϣ", "ȷ���˲����뵽"+c.sBed_no+"����", 0);
			if (check == 0) {
				doProcessTable2List(waitIn, list, dcvalue);
			}else{

				this.doCancelClick(list);
			}

        	return;
        }

        /**
        //���ͬ��ͷ������ק
        if( null!=sc ){

        	int check = this.messageBox("��Ϣ", "ȷ���˲���ת����", 0);
			if (check == 0) {
				doProcessList2List( (BaseCard)svalue.getComponent(0), c );
			}else{

				this.doCancelClick(list);
			}

        	return;
        }
        **/

	}


	/**
	 * �ӱ�����봲ͷ��
	 * @param waitIn
	 */
	private void doProcessTable2List(TTable waitIn, WMatrix list, WPanel dcvalue) {

		//String mrNo = waitIn.getItemString(waitIn.getSelectedRow(),0);
		//String caseNo = waitIn.getItemString(waitIn.getSelectedRow(),1);
		//String ipdNo = waitIn.getItemString(waitIn.getSelectedRow(),6);

		//
		BaseCard c = (BaseCard) dcvalue.getComponent(0);
		String bedNo = c.sBed_no;
		String mrNo = c.sMr_no;
		String code = c.sCode;

		//�봲
		this.doInBed(mrNo, bedNo, code);
	}

	/**
	 * ��ͷ��ĳ���е���ͷ����һ��
	 * @param source
	 * @param target
	 */
	private void doProcessList2List(BaseCard source,BaseCard target){


		if (!doCheckBed_2(target.sCode)) {
			this.messageBox_("�봲���ɹ�!");
			return;
		}

        this.doChangeBed(source, target);
	}

	/**
	 * ��ͷ��ĳ��Ƶ���ͷ����һ�� ( ˫��Ŀ��+������� )
	 * @param source
	 * @param target
	 */
	private void doProcessList2ListMouse(WMatrix jList){

		//˫��ѡ�е�Panel
		WPanel dcvalue = (WPanel) jList.getDoubleClick();

		//��ǰ����ѡ�е�Panel
		WPanel svalue = (WPanel) jList.getSelectedValue();

		//˫�����Ȳ����ڿ�ʱ��������
		if( null!= dcvalue ){

			//�����Ĵ�ͷ��
			BaseCard sbc = (BaseCard)svalue.getComponent(0);
			//˫���Ĵ�ͷ��
			BaseCard dbc = (BaseCard) dcvalue.getComponent(0);

			String MESSAGE_1 = "ִ��'�봲'��'ת��'����ʱ,Ӧ��ѡ��մ�λ��";

		    //
	        if ( TiString.isEmpty(sbc.sMr_no) && TiString.isNotEmpty(dbc.sMr_no) ) {
	        	//˫�����Ѿ����˵�������Ϊ��,Ӧ���ȱ���ѡ�մ�λ
	            this.messageBox_(MESSAGE_1);
	            this.doCancelClick(jList);
	            return;
	        }
	        if ( TiString.isNotEmpty(sbc.sMr_no) && TiString.isNotEmpty(dbc.sMr_no) ) {
	        	//˫���͵�����˫ͷ����Ӧ�ö�����,Ӧ���ȱ���ѡ�մ�λ
	            this.messageBox_(MESSAGE_1);
	            this.doCancelClick(jList);
	            return;
	        }
	        if ( TiString.isEmpty(sbc.sMr_no) ) {
	            this.messageBox_("��ѡ����Ժ������");
	            this.doCancelClick(jList);
	            return;
	        }
	        if ( dbc.sMr_no.equals(sbc.sMr_no) ) {
	            this.messageBox_("��ѡ��ͬ�Ĳ�����");
	            this.doCancelClick(jList);
	            return;
	        }

	        //����������֤ #1
	        if( sbc.new_born_flg.equals("Y") && !dbc.baby_bed_flg.equals("Y") ){
	        	 this.messageBox_("��������ѡ��Ӥ������");
	        	 this.doCancelClick(jList);
	        	 return;
	        }
	        // #2
	        if( !sbc.new_born_flg.equals("Y") && dbc.baby_bed_flg.equals("Y") ){
	        	 this.messageBox_("���˲�����ת��Ӥ������");
	        	 this.doCancelClick(jList);
	        	 return;
	        }
	        
	        //fux modify 20180111 id:5746 ���߻�����������ҩƷ��Ϣ��������
	        TParm parm = new TParm();
  
	        parm = new TParm();
			parm.setData("CASE_NO", sbc.sCase_no);
	        // ��鳤��ҽ��
//	        if (OdiOrderTool.getInstance().getUDOrder(sbc.sCase_no)) {
//	        	this.messageBox("�ò�����δͣ�õĳ���ҽ����������ת����");
//		        this.doCancelClick(jList);
//		        return;
//	            
//	        }
	        // ��黤ʿ���
	        parm = new TParm();
	        parm.setData("CASE_NO", sbc.sCase_no);
	        if (InwForOutSideTool.getInstance().checkOrderisCHECKTool(parm)) {
	        	this.messageBox("�ò�����δ��˵�ҽ����������ת����");
		        this.doCancelClick(jList);
		        return;
	        }
	        // ��黤ʿִ��
	        parm = new TParm();
	        parm.setData("CASE_NO", sbc.sCase_no);
	        if (InwForOutSideTool.getInstance().checkOrderisEXETool(parm)) {
	        	this.messageBox("�ò�����δִ�е�ҽ����������ת����");
		        	 this.doCancelClick(jList);
		        	 return;
	        }
	        parm = new TParm();
	        parm.setData("CASE_NO", sbc.sCase_no);
	        // �����ҩִ�� true����δ���ҩ,false:û��δ���ҩ
			if(InwForOutSideTool.getInstance().checkDrug(parm)){
				 this.messageBox( "ҩ���иò���δ��˵�ҽ����������ת����");
	        	 this.doCancelClick(jList);
	        	 return;
			}
			// �����ҩִ�� true����δ���ҩ,false:û��δ���ҩ
			parm = new TParm();
			parm.setData("CASE_NO", sbc.sCase_no);
			if(InwForOutSideTool.getInstance().exeDrug(parm)){
				this.messageBox( "ҩ���иò���δ��ɵ���ҩ��������ת����");
	        	this.doCancelClick(jList);
	        	return;
			}
			if (OdiOrderTool.getInstance().getRtnCfmM(sbc.sCase_no)) {
				this.messageBox( "�ò�����ҩ����ҩδȷ�ϵ���Ϣ��������ת����");
	        	this.doCancelClick(jList);
	        	return;
			}
			//
        	int check = this.messageBox("��Ϣ", "ȷ��"+sbc.sBed_no+"������ת����"+dbc.sBed_no+"����", 0);
			if (check == 0) {
				this.doProcessList2List( sbc , dbc );
			}else{
				this.doCancelClick(jList);
			}
		}

	}

	/**
	 * ���Ų����봲
	 */
	private void doInBed(String mrNo, String bedNo, String code) {

		// messageBox_(  " ### caseNo  " + caseNo );
		// messageBox_(  " ### mrNo  " + mrNo );
		// messageBox_(  " ### ipdNo  " + ipdNo );
		// messageBox_(  " ### bedNo  " + bedNo );
		// messageBox_(  " ### code  " + code );

		// �õ���ת��table
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		// �õ���ת��DS
		TDataStore ds = waitIn.getDataStore();

		int waitIndex = waitIn.getSelectedRow();// ��ת���ѡ���к�
		if (waitIndex < 0) {
			this.messageBox_("��ѡ��Ҫ��ס�Ĳ���!");
			return;
		}

		//System.out.println( newBaby.getValue("NEW_BORN_FLG", waitIndex) );


		//ˢ�¼��
		/**
		if(!check()){
			return;
		}
		 **/
		if (!doCheckBed(bedNo, code)) {
			this.messageBox_("�봲���ɹ�!");
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

		/* ## */
		// �����Ĵ���
		updata.setData("BED_NO", code);
		/* ## */

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

			/**
			  // checkIn.getValueAt(checkIndex, 3)  ������
			  // checkIn.getValueAt(checkIndex, 2)  ��λ��
			if (checkIn.getValueAt(checkIndex, 3) == null
					|| "".equals(checkIn.getValueAt(checkIndex, 3))
					|| !waitIn
							.getValueAt(waitIndex, 0)
							.toString()
							.equals(checkIn.getValueAt(checkIndex, 2)
									.toString()))
			 **/
			if (mrNo == null
					|| "".equals(mrNo)
					|| !waitIn.getValueAt(waitIndex, 0).toString()
							.equals(bedNo))

			{
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

		updata.setData("REGION_CODE", Operator.getRegion());

		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onInSave", updata); // �봲����
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			waitIn.retrieve();
			return;
		} else {
			this.messageBox("P0005");
			//## ��ʱ������  sendHL7Mes(updata);
			initInStation();

			//
			doRefreshWaitOutAll(false);
		}
	}

	/**
	 * ���Ų���ת��
	 * @param source Դ����
	 * @param target Ŀ������
	 */
	private void doChangeBed(BaseCard source,BaseCard target) {

		// �˻��ߴ�ת�벡�����Ǳ�����
		if (!this.getValueString("STATION_CODE").equalsIgnoreCase( source.sStation_code )) {
			this.messageBox_("�˻��ߴ�ת�벡�����Ǳ�����");
			return;
		}

		//
        TParm parm = new TParm();
        parm.setData("CASE_NO",  source.sCase_no );
        parm.setData("MR_NO", source.sMr_no );
        parm.setData("IPD_NO", source.sIpdNo );
        //��ѯ����סԺ��Ϣ
        TParm in_result = ADMInpTool.getInstance().selectall(parm);
        TParm initParm = new TParm();
        initParm.setRowData(in_result);

        //
        TParm newParm = new TParm();

        //##

        newParm.setData("MR_NO", source.sMr_no);
        newParm.setData("IPD_NO", source.sIpdNo);
        newParm.setData("PAT_NAME", source.sName);
        newParm.setData("SEX_CODE", source.sSex_code);
        newParm.setData("AGE", source.sAge);
        newParm.setData("DEPT_CODE", source.sDeptCode);
        newParm.setData("STATION_CODE", source.sStation_code);
        newParm.setData("VS_DR_CODE", source.sVs_dr_code);
        newParm.setData("ATTEND_DR_CODE", source.sAttend_dr_code);
        newParm.setData("DIRECTOR_DR_CODE", source.sDirector_dr_code);
        newParm.setData("PATIENT_CONDITION", source.sPatient_condition);
        newParm.setData("NURSING_CLASS", source.sNURSING_CLASS);
        //��Ϊ�յ�ʱ��
        String vs_nurse_code = TiString.isNotEmpty(source.sVs_nurse_code)?source.sVs_nurse_code:"";
        newParm.setData("VS_NURSE_CODE", vs_nurse_code );

        //
        newParm.setData("PATIENT_STATUS", "");
        newParm.setData("DIE_CONDITION", "");
        newParm.setData("CARE_NUM", "");
        newParm.setData("IO_MEASURE", "");
        newParm.setData("ISOLATION", "");
        newParm.setData("TOILET", "");
        newParm.setData("ALLERGY","");

        //##

        newParm.setData("CASE_NO", source.sCase_no );
        newParm.setData("BED_NO", source.sCode  );

        //�Ƿ�ת��
        newParm.setData("BED", "Y");
        //�´���
        newParm.setData("TRAN_BED", target.sCode );
        //�ϴ���
        newParm.setData("BED_NO_DESC",source.sCode  );

        //
        /**
        //�Ƿ����
        if(this.getValueBoolean("ALLERGY_Y")){
            newParm.setData("ALLERGY","Y");
        }else{
            newParm.setData("ALLERGY","N");
        }
        */

        newParm.setData("OPT_USER", Operator.getID());
        newParm.setData("OPT_TERM", Operator.getIP());
        TParm DATA = new TParm();
        DATA.setData("OLD_DATA", initParm.getData());
        DATA.setData("NEW_DATA", newParm.getData());
//        System.out.println("data=====ת�����������==========="+DATA);

        if (null != Operator.getRegion() &&
                Operator.getRegion().length() > 0){
            DATA.setData("REGION_CODE", Operator.getRegion());
        }
        TParm result = TIOM_AppServer.executeAction(
            "action.adm.ADMWaitTransAction",
            "changeDcBed", DATA); // ����
        if (result.getErrCode() < 0){
            this.messageBox("ִ��ʧ�ܣ���"+result.getErrName()); //����������ʾ
        }
        else
        {
            this.messageBox("P0005");
            //===liuf ���͸�CISת������Ϣ===
            //## ��ʱ������   sendMessage(newParm);

            ADMXMLTool.getInstance().creatXMLFile( source.sCase_no );

            //
            doRefreshWaitOutAll(false);
        }
	}

	/**
	 * ��˴�λ
	 * @return boolean
	 */
	private boolean doCheckBed(String bedNo, String code) {

		TTable waitTable = (TTable) this.callFunction("UI|WAIT_IN|getThis");

		TParm inParm = new TParm();

		/** ## */
		inParm.setData("BED_NO", code);
		/** ## */

		TParm result = ADMInpTool.getInstance().QueryBed(inParm);
		String APPT_FLG = result.getCount() > 0 ? result
				.getValue("APPT_FLG", 0) : "";
		String ALLO_FLG = result.getCount() > 0 ? result
				.getValue("ALLO_FLG", 0) : "";
		String BED_STATUS = result.getCount() > 0 ? result.getValue(
				"BED_STATUS", 0) : "";
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

		if (APPT_FLG.equals("Y")) {

			/**##
			if (!waitTable.getValueAt(waitTable.getSelectedRow(), 4).equals(
					parm.getValue("BED_NO_DESC")))
			 **/
			if (!waitTable.getValueAt(waitTable.getSelectedRow(), 4).equals(
					bedNo)) {
				int check = this.messageBox("��Ϣ", "�˴��ѱ�Ԥ�����Ƿ��������", 0);
				if (check != 0) {
					onReload();
					return false;
				}
				return true;
			}

		}
		return true;
	}

	/**
	 * ��˴�λ
	 * @return boolean
	 */
	private boolean doCheckBed_2(String code) {

		TParm inParm = new TParm();

		/** ## */
		inParm.setData("BED_NO", code);
		/** ## */

		TParm result = ADMInpTool.getInstance().QueryBed(inParm);
		String APPT_FLG = result.getCount() > 0 ? result
				.getValue("APPT_FLG", 0) : "";
		String ALLO_FLG = result.getCount() > 0 ? result
				.getValue("ALLO_FLG", 0) : "";
		String BED_STATUS = result.getCount() > 0 ? result.getValue(
				"BED_STATUS", 0) : "";
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


		return true;
	}

	/**
	 * �õ�˫���Ĵ�ͷ������֤
	 * @return
	 */
	private BaseCard getBedCard(){

		if( null==jList ){
			return null;
		}

		//��ǰѡ�еĴ�ͷ��
		WPanel dcvalue = (WPanel) jList.getDoubleClick();

		//
		if ( null == dcvalue ) {
			this.messageBox("��˫��ѡ����Ժ������");
			return null;
		}

		//
		BaseCard bc = (BaseCard) dcvalue.getComponent(0);
		//String bedNo = c.sBed_no;
		//String mrNo = c.sMr_no;
		String bedStatus = bc.sBedStatus;

        //
		if ( TiString.isEmpty( bc.sMr_no ) ) {
			this.messageBox("��ѡ����Ժ������");
			return null;
		}

		if ( "3".equals(bedStatus) ) {
			this.messageBox_("�˲����ǰ�������ѡ�񲡻�ʵס������");
			return null;
		}

		if ( "0".equals(bedStatus) ) {
			this.messageBox("�˲���δ��ס��");
			return null;
		}

		//
		return bc;
	}

	/**
	 * ת�ƹ���
	 */
	private void onOutDept_new() {

		BaseCard c = this.getBedCard();
		if( null==c ){ return; }

		//
		TParm sendParm = new TParm();
		// ������
		sendParm.setData("MR_NO", c.sMr_no);
		// סԺ��
		sendParm.setData("IPD_NO", c.sIpdNo );
		// �����
		sendParm.setData("CASE_NO", c.sCase_no );
		// ����
		sendParm.setData("PAT_NAME", c.sName );
		// �Ա�
		sendParm.setData("SEX_CODE", c.sSex_code );
		// ����
		sendParm.setData("AGE", c.sAge );
		// ����  update by sunqy 20140609 ----start----
//		String sql = "SELECT bed_no_desc FROM SYS_BED WHERE MR_NO = '"+c.sMr_no+"' AND BED_NO = '"+c.sBed_no+"'";
//		TParm parm = new TParm(TJDODBTool.getInstance().select(
//				sql));
//		System.out.println("------------sql-------------"+sql);
		sendParm.setData("BED_NO", c.sBed_no);
		// update by sunqy 20140609 ----end----
		// ����
		sendParm.setData("OUT_DEPT_CODE", c.sDeptCode );
		// ����
		sendParm.setData("OUT_STATION_CODE", c.sStation_code );
		//
		//this.messageBox("--sInDate111--"+c.sInDate);
		// ��Ժʱ��
		sendParm.setData("IN_DATE", StringTool.getTimestamp(c.sInDate,"yyyy/MM/dd HH:mm:ss") );

		this.openDialog("%ROOT%\\config\\adm\\ADMOutInp.x", sendParm);
		initInStation();
		TTable outTable = (TTable) this.callFunction("UI|WAIT_OUT|getThis");

		outTable.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_OUT(
				this.getValueString("OUT_DEPT_CODE"),
				this.getValueString("OUT_STATION_CODE")));
		outTable.retrieve();

		creatDataStore("WAIT_OUT");

		//ִ�����
		doRefreshBedCard();
	}

    /**
     * ת�Ʊ���(ת����λ-�¿���)
     */
    private void inOutDept(BaseCard tc){

    	if (!checkOutDate( tc.sCase_no,tc.sMr_no,
        		tc.sDeptCode,tc.sStation_code,true )) return;

    	TParm tp = new TParm();
    	tp.setData("CASE_NO",tc.sCase_no);
    	tp.setData("MR_NO", tc.sMr_no);
    	tp.setData("IPD_NO", tc.sIpdNo);
    	tp.setData("BED_NO",tc.sCode);

        //���Ŀ��� �Ǳ���Ŀ���
    	tp.setData("OUT_DEPT_CODE", tc.sDeptCode );
    	tp.setData("OUT_STATION_CODE", tc.sStation_code );

    	tp.setData("OUT_DATE", SystemTool.getInstance().getDate());//��ǰ����
    	tp.setData("PSF_KIND", "INDP");
    	tp.setData("OPT_USER", Operator.getID());
    	tp.setData("OPT_TERM", Operator.getIP());
    	tp.setData("REGION_CODE", Operator.getRegion());

        //
    	openDialog("%ROOT%\\config\\adm\\ADMOutBed.x",tp);

    	doRefreshWaitOutAll(true);
    }


	/**
	 * <��ͣʹ�� -- ת����������>
     * ת�Ʊ���(ת����λ-�¿���)
     */
    private void inOutDept(String caseNo,String sMr_no,String bedNo,String ipdNo,
    		String sDeptCode,String sStationCode) {

    	//this.messageBox("��֤"+sMr_no );
    	//this.messageBox("��֤"+caseNo );
        //this.messageBox("��֤"+checkOutDate( caseNo,sMr_no ));

        if (!checkOutDate( caseNo,sMr_no,sDeptCode,sStationCode,false )) return;

        //
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("MR_NO", sMr_no);
        parm.setData("IPD_NO", ipdNo);
        parm.setData("BED_NO",bedNo);
        //##
        //���ڵ��߼���Ҫ��Ŀ���(��ק��ȥ��) ��ѡ�Ĵ����Ĳ����Ϳ���
        parm.setData("IN_DEPT_CODE", this.getValue("OUT_DEPT_CODE") );
        parm.setData("IN_STATION_CODE",this.getValue("OUT_STATION_CODE") );
        //���Ŀ��� �Ǳ���Ŀ���
        parm.setData("OUT_DEPT_CODE", sDeptCode );
        parm.setData("OUT_STATION_CODE", sStationCode );
        //##
        parm.setData("OUT_DATE", SystemTool.getInstance().getDate());//��ǰ����
        parm.setData("PSF_KIND", "INDP");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());

        parm.setData("REGION_CODE", Operator.getRegion());


 		boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNo);
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNo);
		parm.setData("ICU_FLG", IsICU);
		parm.setData("CCU_FLG", IsCCU);

        TParm result = TIOM_AppServer.executeAction(
                "action.adm.ADMWaitTransAction", "onInOutSave", parm);
        if ("F".equals(result.getData("CHECK"))) {
            this.messageBox("�β����ݲ���ת�ƣ�");
        }
        if (result.getErrCode() < 0) {
            this.messageBox("E0005");
        } else {
            this.messageBox("P0005");
            /*********************** HL7 *******************************/
            sendHL7Mes(parm, "ADM_TRAN");

            //
            doRefreshWaitOutAll(true);
        }
    }

    /**
     * ���ת������
     *
     * @return boolean
     */
    private boolean checkOutDate(String caseNo,String sMr_no,String sDeptCode,String sStationCode,boolean isPop) {

        if (TiString.isEmpty(sMr_no)) {
                this.messageBox_("��ѡ����Ժ������");
                return false;
        }

        //
        if( !isPop ){
            if (this.getValue("OUT_DEPT_CODE") == null
                    || this.getValue("OUT_DEPT_CODE").equals("")) {
                    this.messageBox_("��ѡ��ת����ң�");
                    return false;
                }
                if (this.getValue("OUT_STATION_CODE") == null
                    || this.getValue("OUT_STATION_CODE").equals("")) {
                    this.messageBox_("��ѡ��ת�벡����");
                    return false;
                }
        }

        if ( TiString.isEmpty(sDeptCode)) {
            this.messageBox_(" ����ת������Ϊ�գ�");
            return false;
        }

        if ( TiString.isEmpty(sStationCode) ) {
            this.messageBox_(" ����ת������Ϊ�գ�");
            return false;
        }

        //

        TParm parm = new TParm();
        int re = 0;
        // ��鳤��ҽ��
        if (OdiOrderTool.getInstance().getUDOrder( caseNo )) {
            re = this.messageBox("��ʾ", "�ò�����δͣ�õĳ���ҽ����ȷ�ϳ�����", 0);
            if (re == 1) {
                return false;
            }
        }
        // ��黤ʿ���
        parm = new TParm();
        parm.setData("CASE_NO", caseNo );
        if (InwForOutSideTool.getInstance().checkOrderisCHECKTool(parm)) {
            re = this.messageBox("��ʾ", "�ò�����δ��˵�ҽ����ȷ�ϳ�����?", 0);
            if (re == 1) {
                return false;
            }
        }
        // ��黤ʿִ��
        parm = new TParm();
        parm.setData("CASE_NO",caseNo );
        if (InwForOutSideTool.getInstance().checkOrderisEXETool(parm)) {
            re = this.messageBox("��ʾ", "�ò�����δִ�е�ҽ����ȷ�ϳ�����?", 0);
            if (re == 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * hl7�ӿ�
     * @param parm TParm
     * @param type String
     */
    private void sendHL7Mes(TParm parm, String type) {

    	//System.out.println("parm:"+parm);
        String caseNo = parm.getValue("CASE_NO");
        //ת��
        if (type.equals("ADM_TRAN"))
        {
            String InDeptCode = parm.getValue("IN_DEPT_CODE");
     		boolean IsICU = parm.getBoolean("ICU_FLG");
    		boolean IsCCU = parm.getBoolean("CCU_FLG");
    		//CIS
            if (InDeptCode.equals("0303")||InDeptCode.equals("0304")||IsICU||IsCCU)
            {
                List list = new ArrayList();
                parm.setData("ADM_TYPE", "I");
                parm.setData("SEND_COMP", "CIS");
                list.add(parm);
                TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
                if (resultParm.getErrCode() < 0)
                    this.messageBox(resultParm.getErrText());
            }
            //Ѫ��
            List list = new ArrayList();
            parm.setData("ADM_TYPE", "I");
            parm.setData("SEND_COMP", "NOVA");
            list.add(parm);
            TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
            if (resultParm.getErrCode() < 0)
                this.messageBox(resultParm.getErrText());
        }
        //��Ժ
        if (type.equals("ADM_OUT"))
        {
     		boolean IsICU = parm.getBoolean("ICU_FLG");
    		boolean IsCCU = parm.getBoolean("CCU_FLG");
    		//CIS
    		if (IsICU||IsCCU)
    		{
              List list = new ArrayList();
              parm.setData("ADM_TYPE", "I");
              parm.setData("SEND_COMP", "CIS");
              list.add(parm);
              TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
              if (resultParm.getErrCode() < 0)
                messageBox(resultParm.getErrText());
    		}
            //Ѫ��
            List list = new ArrayList();
            parm.setData("ADM_TYPE", "I");
            parm.setData("SEND_COMP", "NOVA");
            list.add(parm);
            TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
            if (resultParm.getErrCode() < 0)
                this.messageBox(resultParm.getErrText());
        }
    }

    /**
     * ˢ�´�������б�
     */
    private void doRefreshWaitOutInTable(boolean isInit){

    	String station = Operator.getStation();
		// ��ת��ʹ�ת�� Grid ��ֵ
		TTable WAIT_IN = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		TTable WAIT_OUT = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		WAIT_IN.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_IN("", station));
		WAIT_IN.retrieve();
		WAIT_OUT.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_OUT("", ""));
		WAIT_OUT.retrieve();

		//
		this.onSelectIn();
		this.onSelectOut(isInit);
    }

    /**
	 *
	 */
	private void doRefreshWaitOutAll(boolean isInit) {

        //
        doRefreshWaitOutInTable(isInit);

        //
        doRefreshBedCard();
	}

	/**
	 * ˢ�´�ͷ��
	 */
	private void doRefreshBedCard() {

        jList.setDoubleClick(null);
		buildBedData();
	}

	/**
	 * ������Ϣ������
	 */
	private void queryInStation_new(){

		BaseCard c = this.getBedCard();
		if( null==c ){
			this.doCancelClick(jList);
			return;
		}

		//
		TParm sendParm = new TParm();
		// ���
		sendParm.setData("ADM", "ADM");
		// ������
		sendParm.setData("MR_NO", c.sMr_no );
		// סԺ��
		sendParm.setData("IPD_NO", c.sIpdNo );
		// �����
		sendParm.setData("CASE_NO", c.sCase_no );
		// ����
		sendParm.setData("PAT_NAME", c.sName );
		// �Ա�
		sendParm.setData("SEX_CODE", c.sSex_code );
		// ����
		sendParm.setData("AGE", c.sAge );
		// ����
		sendParm.setData("BED_NO",c.sCode );
		// ����
		sendParm.setData("DEPT_CODE",  c.sDeptCode );
		// ����
		sendParm.setData("STATION_CODE", c.sStation_code );

		//������ʳ add by sunqy 20140527 
		String sql = "SELECT SPECIAL_DIET FROM SYS_PATINFO WHERE MR_NO='"+c.sMr_no+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		sendParm.setData("SPECIAL_DIET", parm.getValue("SPECIAL_DIET", 0));
		
		// ����ҽʦ
		sendParm.setData("VS_DR_CODE", c.sVs_dr_code );

		// ����ҽʦ
		sendParm.setData("ATTEND_DR_CODE", c.sAttend_dr_code );
		// ������
		sendParm.setData("DIRECTOR_DR_CODE", c.sDirector_dr_code );
		// ���ܻ�ʿ
		sendParm.setData("VS_NURSE_CODE", c.sVs_nurse_code );
		// ��Ժ״̬
		sendParm.setData("PATIENT_CONDITION", c.sPatient_condition );

		// BED_OCCU_FLG
		sendParm.setData("BED_OCCU_FLG", c.occupy_bed_flg );

		// ���水ť״̬
		sendParm.setData("SAVE_FLG", this.getPopedem("admChangeDr"));
		this.openWindow(
				"%ROOT%\\config\\adm\\AdmPatinfo.x", sendParm);
		initInStation();
		//
		this.doCancelClick(jList);
	}

	/**
	 * ��������
	 */
	private void doOnBed_new(){

		BaseCard c = this.getBedCard();
		if( null==c ){
		    this.doCancelClick(jList);
			return;
		}

		//
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", c.sCase_no );
		sendParm.setData("MR_NO", c.sMr_no );
		sendParm.setData("IPD_NO", c.sIpdNo );
		sendParm.setData("DEPT_CODE", c.sDeptCode );
		sendParm.setData("STATION_CODE", c.sStation_code );
		sendParm.setData("BED_NO", c.sCode );
		TParm bed = new TParm();
		bed.setData("BED_NO", c.sCode );
		TParm check = SYSBedTool.getInstance().queryRoomBed(bed);
		String caseNo = c.sCase_no ;
		int count = check.getCount("BED_NO");
		boolean flg = false;
		for (int i = 0; i < count; i++) {
			if ("Y".equals(check.getData("ALLO_FLG", i))
					&& !caseNo.equals(check.getData("CASE_NO", i))) {
				flg = true;
			}
		}
		if (flg == true) {//modify by sunqy 20140522
			this.messageBox("�˲���������������!");
			return;
//			int checkFlg = this.messageBox("��Ϣ", "�˲���������������!�Ƿ����������", 0);
//			if (checkFlg != 0){
//				this.doCancelClick(jList);
//				return;
//			}
		}

		this.openDialog("%ROOT%\\config\\adm\\ADMSysBedAllo.x", sendParm);//modify by huangjw 20140730

		//ִ�����
		initInStation();
		chose();
		doRefreshBedCard();
	}

	/**
	 * ȡ������
	 */
	private void doOnCancelBed_new(){

		if( null==jList ){
			return ;
		}

		//��ǰѡ�еĴ�ͷ��
		WPanel dcvalue = (WPanel) jList.getDoubleClick();

		//
		if ( null == dcvalue ) {
			this.messageBox("��˫��ѡ����Ժ������");
			return;
		}

		//
		BaseCard c = (BaseCard) dcvalue.getComponent(0);

        //
		if ( TiString.isEmpty( c.sMr_no ) ) {
			this.messageBox("��ѡ����Ժ������");
			this.doCancelClick(jList);
			return;
		}


		if ( "0".equals(c.sBedStatus) ) {
			this.messageBox("�˲���δ��ס��");
			return;
		}

		if ( !"3".equals(c.sBedStatus) ) {
			this.messageBox_("��ѡ�񱻰����Ĳ�����");
			this.doCancelClick(jList);
			return;
		}

		int re = this.messageBox("��ʾ", "ȷ��Ҫȡ���ò����İ�����", 0);
		if (re != 0) {
			this.doCancelClick(jList);
			return;
		}
		TParm parm = new TParm();
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("CASE_NO", c.sCase_no );
		TParm result = SYSBedTool.getInstance().clearOCCUBed(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005");

		//ִ�����
		initInStation();
		chose();
		doRefreshBedCard();
	}

	/**
	 * ȡ��ת��
	 */
	private void doOnCancelTrans_new() {

		TTable table = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		int selectRow = table.getSelectedRow();
		if (selectRow < 0) {
			this.messageBox("��ѡ��Ҫȡ��ת�ƵĲ���.");
			return;
		}
		String caseNo = (String) table.getValueAt(selectRow, 2);
		TParm parm = new TParm();
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("DATE", SystemTool.getInstance().getDate());
		parm.setData("CASE_NO", caseNo);

		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onUpdateTransAndLog", parm);
		if (result.getErrCode() < 0) {
			messageBox("ȡ��ת��ʧ��.");
		} else {
			initInStation();
			TTable outTable = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
			outTable.retrieve();
			creatDataStore("WAIT_OUT");
			TTable inTable = (TTable) this.callFunction("UI|WAIT_IN|getThis");
			inTable.retrieve();
			creatDataStore("WAIT_IN");
			messageBox("ȡ��ת�Ƴɹ�.");

			//
			doRefreshBedCard();
		}
	}

	/**
	 * ȡ��סԺ
	 */
	private void doOnCancelInHospital_new(){

		if (!checkDate_BedCard()) return;

		//
		BaseCard c = this.getBedCard();

        //
		TParm result = new TParm();
		//=================ִ��ȡ��סԺ����
		int check = this.messageBox("��Ϣ", "�Ƿ�ȡ����", 0);
		if (check == 0) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", c.sCase_no);
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
			result = TIOM_AppServer.executeAction("action.adm.ADMInpAction",
					"ADMCanInp", parm); //
			if (result.getErrCode() < 0) {
				this.messageBox("E0005");
			} else {
				this.messageBox("P0005");
				initInStation();
				chose();

				//
				doRefreshBedCard();
			}
		}
	}

	/**
	 * �������
	 *
	 * @return boolean
	 */
	private boolean checkDate_BedCard() {

		BaseCard c = this.getBedCard();
		if( null==c ){ return false; }

		//
		String caseNo = c.sCase_no;

		//
		TParm tpC = new TParm();
		tpC.setData("CASE_NO", caseNo);

		//==============Ԥ����δ��,����ȡ��סԺ,yanjing 20140728  start 
		TParm result = BILPayTool.getInstance().selBilPayLeft(caseNo);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrName());
			return false;
		}
		if (result.getDouble("PRE_AMT", 0) > 0) {
//			this.messageBox("�˲�������Ԥ����δ��,����ȡ��סԺ");
			if (messageBox("��ʾ��Ϣ Tips", "�˲�������Ԥ����δ��,�Ƿ����ȡ��סԺ? \n Are you cancel?",
					this.YES_NO_OPTION) != 0) {
			return false;
			}
		}
		//==============Ԥ����δ��,����ȡ��סԺ,yanjing 20140728  end 
		//==================�ԼƷѲ���ȡ��סԺ
		boolean checkflg = IBSOrdermTool.getInstance().existFee( tpC );
		if (!checkflg) {
			messageBox("�ѷ�������,�����˷�");
			return false;
		}
		// ���ҽ���Ƿ���ҽ��
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		if (this.checkOrderisEXIST(parm)) {
			this.messageBox("�ò����ѿ���ҽ��������ȡ��סԺ��");
			callFunction("UI|save|setEnabled", false);
			return false;
		}

		return true;
	}

    /**
     * ����CIS��Ѫ��ת����Ϣ
     * @param parm
     */
    private void sendMessage(TParm parm) {

		// ICU��CCUע��
		String caseNO = parm.getValue("CASE_NO");
		boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNO);
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNO);
		// ת��
		String type = "ADM_TRAN_BED";
		parm.setData("ADM_TYPE", "I");
		// CIS
		if (IsICU || IsCCU) {
			List list = new ArrayList();
			parm.setData("SEND_COMP", "CIS");
			list.add(parm);
			TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(
					list, type);
			if (resultParm.getErrCode() < 0)
				messageBox(resultParm.getErrText());
		}
		// Ѫ��
		List list = new ArrayList();
		parm.setData("SEND_COMP", "NOVA");
		list.add(parm);
		TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,
				type);
		if (resultParm.getErrCode() < 0)
			messageBox(resultParm.getErrText());
	}

    /**
     * ȡ������/˫��
     * @param jList
     */
    private void doCancelClick(WMatrix jList){

		jList.setDoubleClick(null);
		jList.setSelectedIndex(-1);
    }

    /**
     * �����������봲
     */
    public void doInBed(){

    	TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");

    	this.doBedCardDropTarget(waitIn, jList);
    }
    
    /**
     * ���û��߱�����Ϣά��ҳ�� add by sunqy 20140516
     */
    public void onInsureInfo(){
    	BaseCard c = this.getBedCard();
    	TParm parm = new TParm();
    	parm.setData("MR_NO", c.sMr_no );
    	parm.setData("EDIT", "N");
    	this.openDialog(
				"%ROOT%\\config\\mem\\MEMInsureInfo.x", parm);
    }
    /**
     * �����¼ add by sunqy 20140708
     */
    public void onNursingRec() {
    	BaseCard base = getBedCard();
    	//this.messageBox("===sInDate==="+base.sInDate);
    	//
    	Timestamp inDate = StringTool.getTimestamp(base.sInDate,"yyyy/MM/dd HH:mm:ss");
        TParm parm = new TParm();
        parm.setData("SYSTEM_TYPE", "INW");
        parm.setData("ADM_TYPE", "I");
        parm.setData("CASE_NO", base.sCase_no);
        parm.setData("PAT_NAME", base.sName);
        parm.setData("MR_NO", base.sMr_no);
        parm.setData("IPD_NO", base.sIpdNo);
        // parm.setData("CASE_NO",this.getCaseNo());
        // parm.setData("PAT_NAME",this.getPatName());
        // parm.setData("MR_NO",this.getMrNo());
        // parm.setData("IPD_NO",this.getIpdNo());
        parm.setData("ADM_DATE", inDate);
        parm.setData("DEPT_CODE", base.sDeptCode);
        parm.setData("RULETYPE", "2");
        parm.setData("EMR_DATA_LIST", new TParm());
        parm.addListener("EMR_LISTENER", this, "emrListener");
        parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
        this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);

    }
    /**
     * ���±� add by xiongwg 20150128
     */
    public void onSelTWD() {
    	BaseCard twd = getBedCard();
    	TParm parm = new TParm();
        parm.setData("SUM","ADM_TYPE", "I");
        parm.setData("SUM","CASE_NO", twd.sCase_no);
        parm.setData("SUM","PAT_NAME", twd.sName);
        parm.setData("SUM","MR_NO", twd.sMr_no);
        parm.setData("SUM","IPD_NO", twd.sIpdNo);
        parm.setData("SUM","BED_NO", twd.sBed_no);
        parm.setData("SUM","STATION_CODE", twd.sStation_code);
        //System.out.println("parm::==="+parm);
        this.openWindow("%ROOT%\\config\\sum\\SUMVitalSign.x", parm);

    }

    /**
	 * ȡ�����
	 * add by yangjj 20150526
	 */
	public void  onCancleInDP(){
		
		BaseCard c = this.getBedCard();
		if( null==c ){
			return; 
		}
		
		String caseNo = c.sCase_no;
		TParm parm=new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm tranLogDept = ADMTransLogTool.getInstance().getTranDeptData(parm);
		if(tranLogDept.getCount()<=0){
			this.messageBox("��ѯת�Ƽ�¼����");
			return;
		}
		if(!isEnableCancleInDP(tranLogDept.getRow(0))){
			return;
		}
		
		TParm sendParm = new TParm();
		sendParm.setData("MR_NO", c.sMr_no);
		sendParm.setData("IPD_NO", c.sIpdNo );
		sendParm.setData("CASE_NO", c.sCase_no );
		sendParm.setData("PAT_NAME", c.sName );
		sendParm.setData("SEX_CODE", c.sSex_code );
		sendParm.setData("AGE", c.sAge );
		sendParm.setData("BED_NO", c.sBed_no);
		sendParm.setData("OUT_DEPT_CODE", c.sDeptCode );
		sendParm.setData("OUT_STATION_CODE", c.sStation_code );
		sendParm.setData("IN_DATE", StringTool.getTimestamp(c.sInDate,"yyyy/MM/dd HH:mm:ss") );
		
		TParm trandParm=sendParm;
		trandParm.setData("OPT_USER", Operator.getID());
		trandParm.setData("OPT_TERM", Operator.getIP());
		TParm result = TIOM_AppServer.executeAction(
                "action.adm.ADMWaitTransAction", "onCancleInDP", trandParm);
		if(result.getErrCode()<0){
			this.messageBox("ִ��ʧ��");
			return;
		}else{
			initInStation();
			TTable outTable = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
			outTable.retrieve();
			creatDataStore("WAIT_OUT");	
			TTable inTable = (TTable) this.callFunction("UI|WAIT_IN|getThis");
			inTable.retrieve();
			creatDataStore("WAIT_IN");	
		    this.messageBox("ִ�гɹ�");
		}
		onQueryData();
	}
	/**
	 * ��֤ȡ�����
	 * @param caseNo
	 * @return
	 * add by yangjj 20150526
	 */
	public  boolean  isEnableCancleInDP(TParm parm){
		/*
		if(InwForOutSideTool.getInstance().checkOrderisExistExec(parm)){
			this.messageBox("������ƺ���ִ�е�ҽ��,����ȡ�����");
			return false;
		}
		if(InwForOutSideTool.getInstance().checkOrderisExistCheck(parm)){
			this.messageBox("������ƺ�����˵�ҽ��,����ȡ�����");
			return false;
		}
		*/
		if(InwForOutSideTool.getInstance().checkOrderisExist(parm)){
			this.messageBox("������ƺ��ѿ�����ҽ��,����ȡ�����");
			return false;
		}
		TParm parmfee=InwForOutSideTool.getInstance().checkOrderFee(parm);
		if(parmfee.getDouble("TOT_AMT", 0)!=0){
			this.messageBox("��ƺ���ܷ���Ϊ:"+parmfee.getDouble("TOT_AMT", 0)+",����ȡ�����");
			return false;
		}
		return true;
	}
}
