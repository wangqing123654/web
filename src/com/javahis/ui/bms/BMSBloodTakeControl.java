package com.javahis.ui.bms;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.bms.BMSSQL;
import jdo.bms.BMSTakeTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ȡѪ��
 * </p>
 * 
 * @author shibl
 * 
 */
public class BMSBloodTakeControl extends TControl {
	/**
	 * ���
	 */
	private TTable table;

	private String action = "INSERT";

	private TParm parm = new TParm();
	/**
	 * ���뵥����
	 */
	private TParm applyParm;
	/**
	 *�Ѵ���ȡѪ������
	 */
	private TParm CtakeParm;

	public BMSBloodTakeControl() {

	}

	public void onInit() {
		super.onInit();
		table = this.getTable("TABLE");
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			parm = (TParm) obj;
			setValueForParm(
					"APPLY_NO;MR_NO;PAT_NAME;DEPT_CODE;STATION_CODE;BED_NO;"
							+ "BLOOD_TYPE;CASE_NO;IPD_NO;ADM_TYPE", parm);
			if (parm.getValue("BLOOD_RH_TYPE").equals("+")) {
				this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(true);
				this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
			} else if (parm.getValue("BLOOD_RH_TYPE").equals("-")) {
				this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
				this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(true);
			} else {
				this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
				this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
			}
		}
		if (parm.getValue("TYPE").equals("UPDATE")) {
			this.setValue("BLOOD_DATE", parm.getTimestamp("BLOOD_DATE"));
			this.setValue("BLOOD_USER", parm.getValue("BLOOD_USER"));
			this.setValue("BLOOD_TANO", parm.getValue("BLOOD_TANO"));
			onQuery();
		} else {
			Timestamp  now=SystemTool.getInstance().getDate();
			this.setValue("BLOOD_DATE", now);
			this.setValue("BLOOD_TANO", BMSTakeTool.getNo());
		}
		initData();
	}

	/**
	 * ��ʼ������
	 */
	public void initData() {
		
		String applysql = "SELECT BLD_CODE,APPLY_QTY,UNIT_CODE FROM BMS_APPLYD "
				+ " WHERE APPLY_NO='" + parm.getValue("APPLY_NO") + "'";
		applyParm = new TParm(TJDODBTool.getInstance().select(applysql));
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String bloodTaNO = this.getValueString("BLOOD_TANO");
		if (bloodTaNO.equals("")) {
			this.messageBox("û�пɲ�ѯ��ȡѪ����");
			return;
		}
		TParm parm = new TParm();
		parm.setData("BLOOD_TANO", bloodTaNO);
		String Dsql = BMSTakeTool.getInstance().getDQuerysql(parm);
		TParm DParm = new TParm(TJDODBTool.getInstance().select(Dsql));
		if (DParm.getCount() <= 0) {
			this.messageBox("���ݲ�ѯ����");
			return;
		}
		table.setParmValue(DParm);
		action = "UPDATE";
	}

	/**
	 * ɾ��
	 */
	public void onDelete() {
		// ȡѪ����
		String bloodTaNO = this.getValueString("BLOOD_TANO");
		if (bloodTaNO.equals("")) {
			this.messageBox("û�п�ɾ����ȡѪ��");
			return;
		}
		TParm parm = new TParm();
		parm.setData("BLOOD_TANO", bloodTaNO);
		TParm result = TIOM_AppServer.executeAction("action.bms.BMSTakeAction",
				"onDelete", parm);
		// �����ж�
		if (result == null || result.getErrCode() < 0) {
			this.messageBox("E0001");
			return;
		}
		this.messageBox("P0001");
		this.closeWindow();
		return;
	}

	/**
	 * ����
	 */
	public void add() {
		if (this.getComboBox("BLD_CODE").getSelectedIndex() <= 0) {
			this.messageBox("E0138");
			return;
		}
		for (int i = 0; i < table.getRowCount(); i++) {
			if (getValueString("BLD_CODE").equals(
					table.getItemString(i, "BLD_CODE"))) {
				this.messageBox("��ѪƷ�Ѵ���");
				return;
			}
		}
		table.acceptText();
		int row = table.addRow();
		table.setItem(row, "BLD_CODE", this.getComboBox("BLD_CODE").getValue());
		table.setItem(row, "APPLY_QTY", this.getValueDouble("APPLY_QTY"));
		table.setItem(row, "UNIT_CODE", this.getValueString("UNIT_CODE"));
		
		//add by yangjj 20150522
		table.setItem(row, "BLOOD_TYPE", this.getValueString("APPLY_BLOOD_TYPE"));
		table.setItem(row, "RH", this.getValueString("APPLY_RH"));
		
		TCheckBox irr = (TCheckBox) this.getComponent("IRRADIATION");
		table.setItem(row, "IRRADIATION", irr.isSelected()?"Y":"N");
		
		//modify by yangjj 20150522
		this.clearValue("BLD_CODE;APPLY_QTY;UNIT_CODE;APPLY_BLOOD_TYPE;APPLY_RH;IRRADIATION");

	}

	/**
	 * ѡ���¼�
	 */
	public void onBldCodeSel() {
		String bldCode = this.getComboBox("BLD_CODE").getSelectedID();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < applyParm.getCount(); i++) {
			TParm parmRow = applyParm.getRow(i);
			list.add(parmRow.getValue("BLD_CODE"));
		}
		
		if (!list.contains(bldCode)) {
			this.messageBox("��Ѫ�������޴�ѪƷ������ѡ��");
			this.clearValue("BLD_CODE");
			return;
		}
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				BMSSQL.getBMSUnit(bldCode)));
		this.setValue("UNIT_CODE", parm.getValue("UNIT_CODE", 0));
		this.clearValue("APPLY_QTY");
	}

	/**
	 * ɾ��ѪҺ����
	 */
	public void onRemove() {
		TTable table = this.getTable("TABLE");
		int row = table.getSelectedRow();
		if (row == -1) {
			this.messageBox("E0134");
		}
		table.removeRow(row);
	}

	/**
	 * ����
	 */
	public void onSave() {
		table.acceptText();
		TParm inparm = new TParm();
		TParm tableParm = getTableParm();
		if (!onCheck(tableParm)) {
			return;
		}
		TParm Mparm = new TParm();
		Mparm.setData("BLOOD_TANO", getValueString("BLOOD_TANO"));
		Mparm.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
		Mparm.setData("APPLY_NO", parm.getValue("APPLY_NO"));
		Mparm.setData("CASE_NO", getValueString("CASE_NO"));
		Mparm.setData("IPD_NO", getValueString("IPD_NO"));
		Mparm.setData("MR_NO", getValueString("MR_NO"));
		Mparm.setData("BED_NO", getValueString("BED_NO"));
		Mparm.setData("STATION_CODE", getValueString("STATION_CODE"));
		Mparm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
		Mparm.setData("BLOOD_TYPE", getValueString("BLOOD_TYPE"));
		if (this.getRadioButton("BLOOD_RH_TYPE_A").isSelected()) {
			Mparm.setData("BLOOD_RH_TYPE", "+");
		} else {
			Mparm.setData("BLOOD_RH_TYPE", "-");
		}
		Mparm.setData("BLOOD_DATE", StringTool.getString(
				(Timestamp) getValue("BLOOD_DATE"), "yyyyMMddHHmmss"));
		Mparm.setData("BLOOD_USER", getValueString("BLOOD_USER"));
		Mparm.setData("OPT_USER", Operator.getID());
		Mparm.setData("OPT_TERM", Operator.getIP());
		inparm.setData("MTABLE", Mparm.getData());
		inparm.setData("DTABLE", tableParm.getData());
		if (action.equals("INSERT")) {
			TParm result = TIOM_AppServer.executeAction(
					"action.bms.BMSTakeAction", "onInsert", inparm);
			
			// �����ж�
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			this.messageBox("P0001");

		} else {
			TParm result = TIOM_AppServer.executeAction(
					"action.bms.BMSTakeAction", "onUpdate", inparm);
			// �����ж�
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			this.messageBox("P0001");

		}
		action = "UPDATE";
		return;
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		if ("".equals(this.getValueString("BLOOD_TANO"))) {
			this.messageBox("ȡѪ��������");
			return;
		}
		String mrNo=this.getValueString("MR_NO");
		Pat pat=Pat.onQueryByMrNo(mrNo);
		// ��ӡ����
		TParm date = new TParm();
		date.setData("TITLE", "TEXT", "ȡѪ��");//modify by sunqy 20140806 ȥ��ҽԺȫ��  ��ͷ�Ѵ���
//		date.setData("TITLE", "TEXT", Manager.getOrganization()
//				.getHospitalCHNFullName(Operator.getRegion())+"ȡѪ��");
		date.setData("BAR_CODE", "TEXT", getValueString("BLOOD_TANO"));
		date.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
		date.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT",pat.getIpdNo());
		date.setData("filePatName", "TEXT", pat.getName());
		date.setData("fileSex", "TEXT", pat.getSexString());
		String birth=StringTool.getString(pat.getBirthday(), "yyyy/MM/dd");
		date.setData("fileBirthday", "TEXT", birth);
		date.setData("PATNAME", "TEXT","��Ѫ������:"+pat.getName());
		String stationCode=((TTextFormat)this.getComponent("STATION_CODE")).getText();
		date.setData("STATION_CODE", "TEXT","����:"+stationCode);
		String bed=((TTextFormat)this.getComponent("BED_NO")).getText();
		date.setData("BED_NO", "TEXT","����:"+bed);
		String bld=getComboBox("BLOOD_TYPE").getSelectedName();
		date.setData("BLOOD_TYPE", "TEXT", "ABOѪ��:"+bld);
		String rh="Y".equals(getValueString("BLOOD_RH_TYPE_A")) ? "RH(D):����"
					: "RH(D):����";
		date.setData("RH_TYPE", "TEXT",rh);
		String bldTime=StringTool.getString((Timestamp) getValue("BLOOD_DATE"), "yyyy/MM/dd HH:mm:ss");
		date.setData("BLOOD_DATE", "TEXT","ȡѪʱ��:"+bldTime);
		String bldUser=((TTextFormat)this.getComponent("BLOOD_USER")).getText();
		date.setData("BLOOD_USER", "TEXT","ȡѪ��:"+bldUser);
		//�������
		TParm parm = new TParm();
		String bloodCode = "";//add by sunqy 20140805
		for (int i = 0; i < table.getRowCount(); i++) {
			String bldCode = table.getItemString(i, "BLD_CODE");
			bloodCode += bldCode + "','";//add by sunqy 20140805
			TParm inparm = new TParm(TJDODBTool.getInstance().select(
					BMSSQL.getBMSBldCodeInfo(bldCode)));
			TParm unitparm = new TParm(TJDODBTool.getInstance().select(
					BMSSQL.getBMSUnit(bldCode)));
			if (inparm == null || inparm.getErrCode() < 0) {
				this.messageBox("E0034");
				return;
			}
			parm.addData("BLDCODE_DESC", inparm.getValue("BLDCODE_DESC", 0));
			
			//add by yangjj 20150710
			double apply_qty = table.getItemDouble(i, "APPLY_QTY");
			if(apply_qty % 1.0 == 0){
				parm.addData("QTY", (long)apply_qty);
			}else{
				parm.addData("QTY", apply_qty);
			}

			parm.addData("UNIT", unitparm.getValue("UNIT_CHN_DESC", 0));
			
			//add by yangjj 20150522
			parm.addData("BLOOD_TYPE", table.getItemData(i , "BLOOD_TYPE"));
			
			String rh1 = table.getItemData(i , "RH")+"";
			String r = "";
			if("+".equals(rh1)){
				r = "����";
			}else if("-".equals(rh1)){
				r = "����";
			}else{
				r="";
			}
			parm.addData("RH", r);
			
			String irr = table.getItemData(i , "IRRADIATION")+"";
			parm.addData("IRRADIATION", "Y".equals(irr)?"��":"��");
		}
		//add by sunqy 20140805=============
		bloodCode = bloodCode.substring(0, bloodCode.length()-3);
		TParm inparmExcept = new TParm(TJDODBTool.getInstance().select(BMSSQL.getBMSBldCodeInfoExcept(bloodCode)));
		for (int i = 0; i < inparmExcept.getCount(); i++) {
			parm.addData("BLDCODE_DESC", inparmExcept.getValue("BLDCODE_DESC", i));
			parm.addData("QTY", 0);
			parm.addData("UNIT", inparmExcept.getValue("UNIT_CHN_DESC", i));
		}
		//add by sunqy 20140805==============
		parm.setCount(parm.getCount("BLDCODE_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "BLDCODE_DESC");
		
		//add by yangjj 20150522
		parm.addData("SYSTEM", "COLUMNS", "BLOOD_TYPE");
		parm.addData("SYSTEM", "COLUMNS", "RH");

		parm.addData("SYSTEM", "COLUMNS", "QTY");
		parm.addData("SYSTEM", "COLUMNS", "UNIT");
		
		//add by yangjj 20150522
		parm.addData("SYSTEM", "COLUMNS", "IRRADIATION");
		
		date.setData("TABLE", parm.getData());
		this.openPrintDialog("%ROOT%\\config\\prt\\BMS\\TakeNo_V45.jhw", date);
	}

	/**
	 * ȡѪ����������֤
	 * 
	 * @return
	 */
	private boolean onCheck(TParm parm) {
		if (parm.getCount() <= 0) {
			this.messageBox("��ȡѪѪƷ����,���ܱ���");
			return false;
		}
		String takesql = "SELECT BLD_CODE,SUM(APPLY_QTY) APPLY_QTY  FROM BMS_BLDTAKED A,BMS_BLDTAKEM B "
				+ " WHERE A.BLOOD_TANO=B.BLOOD_TANO "
				+ " AND B.APPLY_NO='"
				+ getValueString("APPLY_NO")
				+ "'"
				+ " AND A.BLOOD_TANO<>'"
				+ this.getValueString("BLOOD_TANO") + "' GROUP BY BLD_CODE";
		CtakeParm = new TParm(TJDODBTool.getInstance().select(takesql));
		Map<String, Double> bldQtyMap = new HashMap<String, Double>();
		for (int i = 0; i < parm.getCount(); i++) {
			String bldCode = parm.getValue("BLD_CODE", i);
			double qty = parm.getDouble("APPLY_QTY", i);
			bldQtyMap.put(bldCode, qty);

		}
		for (int i = 0; i < CtakeParm.getCount(); i++) {
			String bldCode = CtakeParm.getValue("BLD_CODE", i);
			double qty = CtakeParm.getDouble("APPLY_QTY", i);
			if (bldQtyMap.get(bldCode) != null) {
				double sum = bldQtyMap.get(bldCode) + qty;
				bldQtyMap.put(bldCode, sum);
			}
		}
		// ---------------------------------------------
		for (int i = 0; i < applyParm.getCount(); i++) {
			TParm parmRow = applyParm.getRow(i);
			String bldCode = parmRow.getValue("BLD_CODE");
			double applyQty = parmRow.getDouble("APPLY_QTY");
			if (bldQtyMap.get(bldCode)!=null&&
					bldQtyMap.get(bldCode).doubleValue() > applyQty) {
				TParm inparm = new TParm(TJDODBTool.getInstance().select(
						BMSSQL.getBMSBldCodeInfo(bldCode)));
				this.messageBox(inparm.getValue("BLDCODE_DESC", 0)+":ȡѪ����("+bldQtyMap.get(bldCode)+")\r\n" +
						"������������("+applyQty+")");
				return false;
			}
		}
		return true;
	}

	/**
	 * �������
	 * 
	 * @return
	 */
	private TParm getTableParm() {
		TParm parm = new TParm();
		if (table.getRowCount() <= 0) {
			return parm;
		}
		int seq = 0;
		for (int i = 0; i < table.getRowCount(); i++) {
			parm.addData("BLOOD_TANO", getValueString("BLOOD_TANO"));
			parm.addData("SEQ", seq);
			parm.addData("BLD_CODE", table.getItemData(i, "BLD_CODE"));
			parm.addData("APPLY_QTY", table.getItemData(i, "APPLY_QTY"));
			parm.addData("UNIT_CODE", table.getItemData(i, "UNIT_CODE"));
			
			//add by yangjj 20150522
			parm.addData("BLOOD_TYPE", table.getItemData(i, "BLOOD_TYPE"));
			parm.addData("RH", table.getItemData(i, "RH"));
			parm.addData("IRRADIATION", table.getItemData(i, "IRRADIATION"));
			
			parm.addData("OPT_USER", Operator.getID());
			parm.addData("OPT_TERM", Operator.getIP());
			seq++;
		}
		parm.setCount(parm.getCount("BLOOD_TANO"));
		return parm;
	}

	/**
	 * ���
	 */
	public void onClear() {
		clearValue("APPLY_NO;MR_NO;PAT_NAME;DEPT_CODE;STATION_CODE;BED_NO;"
				+ "BLOOD_TYPE;BLOOD_TANO;BLOOD_DATE;BLOOD_USER");
		if (table.getRowCount() > 0) {
			table.removeRowAll();
		}
	}

	/**
	 * �õ�Table����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * �õ�ComboBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * �õ�RadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	/**
	 * �õ�TextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}
}
