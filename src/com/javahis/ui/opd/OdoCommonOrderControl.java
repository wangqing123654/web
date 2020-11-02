package com.javahis.ui.opd;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jdo.opd.OPDSysParmTool;
import jdo.pha.PhaBaseTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
/**
 *
 * <p> Title: ����ҽ������վ����ҽ��</p>
 *
 * <p> Description:����ҽ������վ����ҽ�� </p>
 *
 * <p> Copyright: Copyright (c) Liu dongyang 2008 </p>
 *
 * <p> Company:Javahis </p>
 *
 * @author ehui 20090406
 * @version 1.0
 */
public class OdoCommonOrderControl extends TControl {
	//����ҽʦ�����룬���Ż�ҽʦ����
	private String deptOrDrCode,code;
	//����������
	private final static String DEPT="1";
	//��ҩ����ҩTABLE
	private TTable medTable,chnTable;
	//��̬��ʾ���Ż�ҽʦ����COMBO
	private TComboBox combo;
	//��ҩ����ҩ����
	private TDataStore ds,chnDs;
	//��ѯ����SQL
	private final static String SQL="SELECT * FROM OPD_COMORDER";
	//��ҩ��Ĭ��������Ƶ�Ρ���������ҩ��ʽ
	private String dctTakeDays,chnFreq,chnRoute,dctAgentCode;
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		getInitParam();
		onClear();
		medTable.setDSValue();
	}
	/**
	 * ���
	 */
	public void onClear() {
		combo.setValue(code);
		ds.setSQL(SQL+" WHERE DEPT_OR_DR='"+deptOrDrCode+"' AND DEPTORDR_CODE='"+code+"' AND RX_TYPE<>'3' ORDER BY RX_TYPE,SEQ_NO");
		ds.retrieve();
		int row=ds.insertRow();
		ds.setActive(row,false);
		medTable.setDataStore(ds);
		medTable.setDSValue();

		chnDs.setSQL(SQL+" WHERE DEPT_OR_DR='"+deptOrDrCode+"' AND DEPTORDR_CODE='"+code+"' AND RX_TYPE='3' ORDER BY RX_TYPE,SEQ_NO");
		chnDs.retrieve();

		initChn();
	}
	/**
	 * ��ʼ����ҽ����
	 */
	private void initChn(){
		TParm comboParm=getRxNos();
		if(comboParm==null||comboParm.getCount()<0){
			return ;
		}

		String lastRx=comboParm.getValue("ID",comboParm.getCount()-1).replace("[", "").replace("]", "");
		chnDs.setFilter("PRESRT_NO='" +lastRx+"'");
		chnDs.filter();
		if(!StringUtil.isNullString(chnDs.getItemString(chnDs.rowCount()-1, "ORDER_CODE"))){
			int row=chnDs.insertRow();
			chnDs.setActive(row,false);
		}

		chnTable.setDataStore(chnDs);
		chnTable.setDSValue();

		TComboBox chnRx=(TComboBox)this.getComponent("RX_NO");
		chnRx.setParmValue(comboParm);
		chnRx.setSelectedID(comboParm.getValue("ID",comboParm.getCount()-1));
		if(StringUtil.isNullString(chnDs.getItemString(0, "ORDER_CODE"))){
			this.setValue("CHN_FREQ_CODE", chnFreq);
			this.setValue("CHN_ROUTE_CODE", chnRoute);
			this.setValue("TAKE_DAYS", dctTakeDays);
			TComboBox dctAgent=(TComboBox)this.getComponent("DCTAGENT_CODE");
			dctAgent.setSelectedID(dctAgentCode);
			this.setValue("PACKAGE_TOT", 0+"");
			return;
		}
		this.setValue("TAKE_DAYS", chnDs.getItemString(0, "TAKE_DAYS"));
		this.setValue("CHN_FREQ_CODE", chnDs.getItemString(0, "FREQ_CODE"));
		this.setValue("CHN_ROUTE_CODE", chnDs.getItemString(0, "ROUTE_CODE"));
		TComboBox dctAgent=(TComboBox)this.getComponent("DCTAGENT_CODE");
		dctAgent.setSelectedID(chnDs.getItemString(0, "DCTAGENT_CODE"));
		double packageTot=0.0;
		for(int i=0;i<chnDs.rowCount();i++){
			packageTot+=chnDs.getItemDouble(i, "MEDI_QTY");
		}
		this.setValue("PACKAGE_TOT", packageTot+"");
	}
	/**
	 * ���ܲ���
	 */
	public void getInitParam() {
		TComboBox other;
		TLabel l;
		deptOrDrCode=(String)this.getParameter();
		l=(TLabel)this.getComponent("LABEL");
		if(DEPT.equalsIgnoreCase(deptOrDrCode)){
			code=Operator.getDept();
			combo=(TComboBox)this.getComponent("DEPT");
			combo.setVisible(true);
			combo.setEnabled(false);
			other=(TComboBox)this.getComponent("OPERATOR");
			other.setVisible(false);
			other.setEnabled(false);
			l.setZhText("����");
			l.setEnText("Dept.");
		}else{
			code=Operator.getID();
			combo=(TComboBox)this.getComponent("OPERATOR");
			combo.setVisible(true);
			combo.setEnabled(false);
			other=(TComboBox)this.getComponent("DEPT");
			other.setVisible(false);
			other.setEnabled(false);
			l.setZhText("ҽʦ");
			l.setEnText("Dr.");
		}
		combo.setValue(code);
		medTable=(TTable)this.getComponent("MEDTABLE");
		chnTable=(TTable)this.getComponent("CHNTABLE");
		medTable.addEventListener(TTableEvent.CHANGE_VALUE, this,
		"onChangeValue");
		medTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
		"onFitFlg");
		medTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
		"onMedCreateEditComponent");

		chnTable.addEventListener("CHNTABLE->"+TTableEvent.CHANGE_VALUE, this,
		"onChnChangeValue");
		chnTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
		"onChnCreateEditComponent");

		ds=new TDataStore();
		chnDs=new TDataStore();

		TParm sysparm=OPDSysParmTool.getInstance().getSysParm();
		dctTakeDays=sysparm.getValue("DCT_TAKE_DAYS",0);
		chnFreq=sysparm.getValue("G_FREQ_CODE",0);
		chnRoute=sysparm.getValue("G_ROUTE_CODE",0);
		dctAgentCode=sysparm.getValue("G_DCTAGENT_CODE",0);
	}
	/**
	 * ȡ�ô���ǩcombo����
	 * @return
	 */
	private TParm getRxNos(){
		TParm rxNos=new TParm();
		Vector pres=chnDs.getVector("PRESRT_NO");
		if(pres==null||pres.size()<1){
			String rxNo = SystemTool.getInstance().getNo("ALL", "ODO", "RX_NO",
			"RX_NO");

			rxNos.addData("ID", rxNo);
			rxNos.addData("NAME", "�� 1 �Ŵ���");
			rxNos.setCount(1);
			//// System.out.println("rxNos="+rxNos);
			return rxNos;
		}
		List temp=new ArrayList();
		for(int i=0;i<pres.size();i++){
			String tempRx=((Vector)pres.get(i)).get(0)+"";
			if(StringUtil.isNullString(tempRx)){
				continue;
			}
			if(temp.indexOf(tempRx)<0){
				temp.add(tempRx);
			}
		}
		if(temp.size()<0){
			return rxNos;
		}
		for(int i=0;i<temp.size();i++){
			rxNos.addData("ID", temp.get(i));
			rxNos.addData("NAME", "�� "+(i+1)+ " �Ŵ���");
		}
		rxNos.setCount(rxNos.getCount("ID"));
		return rxNos;
	}
	/**
	 * ����ϵ�ѡ�¼����ж��Ƿ���������
	 * @param obj
	 */
	public boolean onFitFlg(Object obj){
		medTable.acceptText();
		medTable.setDSValue();
		return false;
	}
	/**
	 * ��ҩTABLEֵ�ı��¼�
	 * @param tNode TNOde
	 * @return
	 */
	public boolean onChangeValue(TTableNode tNode){
		int row=medTable.getSelectedRow();
		int column=medTable.getSelectedColumn();
		String colName=medTable.getParmMap(column);
		Object value=tNode.getValue();
		if(StringUtil.isNullString(ds.getItemString(row, "ORDER_CODE"))){
			return true;
		}

		//ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;DESCRIPTION;SPCFYDEPT;GIVEBOX_FLG;OPD_FIT_FLG;EMG_FIT_FLG;IPD_FIT_FLG
		//ҽ������λ����ҩ���ţ�����ס����
		if("ORDER_DESC".equalsIgnoreCase(colName)||"MEDI_UNIT".equalsIgnoreCase(colName)||"OPD_FIT_FLG".equalsIgnoreCase(colName)||"EMG_FIT_FLG".equalsIgnoreCase(colName)||"IPD_FIT_FLG".equalsIgnoreCase(colName)||"GIVEBOX_FLG".equalsIgnoreCase(colName)){
			return true;
		}
		ds.setItem(row, colName, value);
		ds.setActive(row, true);
		medTable.setDSValue();
		return false;
	}
	/**
	 * ��ҩTABLEֵ�ı��¼�
	 * @param tNode TNOde
	 * @return
	 */
	public boolean onChnChangeValue(TTableNode tNode){

		int row=chnTable.getSelectedRow();
		int column=chnTable.getSelectedColumn();
		Object value=tNode.getValue();
		chnTable.acceptText();
		if(StringUtil.isNullString(chnDs.getItemString(row, "ORDER_CODE"))){
			return true;
		}

		//ҽ������λ����ҩ���ţ�����ס����
		//GIVEBOX_FLG;OPD_FIT_FLG;EMG_FIT_FLG;IPD_FIT_FLG
		if(column==0)
			return true;
		switch (column){
			case 1:
				chnDs.setItem(row, "MEDI_QTY", value);
				double pakcageTot=0;
				for(int i=0;i<chnDs.rowCount();i++){
					pakcageTot+=chnDs.getItemDouble(i, "MEDI_QTY");
				}
				this.setValue("PACKAGE_TOT", pakcageTot+"");
				break;
			case 2:
				chnDs.setItem(row, "DCTEXCEP_CODE", value);
				break;
		}
		chnDs.setActive(row, true);
		chnTable.setDSValue();
		chnTable.getTable().grabFocus();
		chnTable.setSelectedRow(row+1);
		chnTable.setSelectedColumn(chnTable.getColumnIndex("ORDER_DESC"));
		return false;
	}
	/**
	 * ���SYS_FEE��������
	 *
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onMedCreateEditComponent(Component com, int row, int column) {

		TTable table=(TTable)this.getComponent("MEDTABLE");
		String columnName=table.getParmMap(column);
		if(!"ORDER_DESC".equalsIgnoreCase(columnName)){
			return;
		}
		if (!(com instanceof TTextField)){
			return;
		}

		TTextField textfield = (TTextField) com;
		textfield.onInit();
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 2);
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"));
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popOrderReturn");

	}
	/**
	 * ���SYS_FEE��������
	 *
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onChnCreateEditComponent(Component com, int row, int column) {

		String columnName=chnTable.getParmMap(column);
		if(!"ORDER_DESC".equalsIgnoreCase(columnName)){
			return;
		}
		if (!(com instanceof TTextField)){
			return;
		}

		TTextField textfield = (TTextField) com;
		textfield.onInit();
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 3);
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"),parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popChnOrderReturn");

	}
	/**
	 * ������ҩ
	 * @param tag
	 * @param obj
	 */
	public void popChnOrderReturn(String tag, Object obj){
		TParm parm = (TParm) obj;
		int row = chnTable.getSelectedRow();
		TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE"));
		if (parmBase.getErrCode() < 0) {
			return;
		}
		if(!StringUtil.isNullString(chnDs.getItemString(row, "ORDER_CODE"))){
			this.messageBox_("�ѿ���ҽ����������");
			chnTable.setDSValue(row);
			return;
		}
		if(StringUtil.isNullString(this.getValueString("CHN_ROUTE_CODE"))){
			this.messageBox_("������д�÷�");
			return;
		}
		if(StringUtil.isNullString(this.getValueString("CHN_FREQ_CODE"))){
			this.messageBox_("������дƵ��");
			return;
		}
		if(StringUtil.isNullString(this.getValueString("DCTAGENT_CODE"))){
			this.messageBox_("������д�巨");
			return;
		}
		String rxType="3";
		chnTable.acceptText();
		/*
		 * ҽ��,200;����,60,double;��λ,60,UNIT_CODE;Ƶ��,70,FREQ_CODE;�շ�,70,int;
		 * ҽ����ע,200;�ض�����,80,DEPT;��,40,boolean;��������,60,boolean;��������,60,boolean;סԺ����,60,boolean
		 * 0,left;1,right;2,left;3,left;4,right;5,left;6,left;
		 */
		chnDs.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
		chnDs.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));

		chnDs.setItem(row, "TRADE_ENG_DESC", parm.getValue("TRADE_ENG_DESC"));
		chnDs.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
		chnDs.setItem(row, "DEPT_OR_DR", deptOrDrCode);
		chnDs.setItem(row, "DEPTORDR_CODE", code);
		chnDs.setItem(row, "RX_TYPE", rxType);
		chnDs.setItem(row, "SEQ_NO", getChnMaxSeq()+1);
		chnDs.setItem(row, "MEDI_QTY", 0.0);
		chnDs.setItem(row, "MEDI_UNIT", parmBase.getValue("MEDI_UNIT",0));
		chnDs.setItem(row, "TAKE_DAYS", parmBase.getValue("TAKE_DAYS",0));
		chnDs.setItem(row, "FREQ_CODE", parmBase.getValue("FREQ_CODE",0));
		chnDs.setItem(row, "ROUTE_CODE", parmBase.getValue("ROUTE_CODE",0));
		chnDs.setItem(row, "DESCRIPTION", parm.getValue("DESCRIPTION"));
		chnDs.setItem(row, "DCTAGENT_CODE", this.getValueString("DCTAGENT_CODE"));
		chnDs.setItem(row, "SPCYDEPT", "");

		TComboBox rx=(TComboBox)this.getComponent("RX_NO");
		chnDs.setItem(row, "PRESRT_NO", rx.getSelectedID().replace("[", "").replace("]", ""));
		chnDs.setItem(row, "PRESRT_DESC", rx.getSelectedName());

		chnDs.setItem(row, "LINK_NO", 0);
		chnDs.setItem(row, "PACKAGE_TOT", 0);
		chnDs.setItem(row, "PRESENT_SEQ", 0);
		chnDs.setItem(row, "OPT_USER", Operator.getID());
		chnDs.setItem(row, "OPT_TERM", Operator.getIP());
		chnDs.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
		chnDs.setActive(row, true);
		int newRow=chnDs.insertRow();
		chnDs.setActive(newRow, false);

		chnTable.setDSValue();
		chnTable.getTable().grabFocus();
		chnTable.setSelectedRow(row);
		chnTable.setSelectedColumn(1);
	}
	/**
	 * ����������ҩ
	 *
	 * @param tag
	 * @param obj
	 */
	public void popOrderReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		int row = medTable.getSelectedRow();
		TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE"));
		if(!StringUtil.isNullString(ds.getItemString(row, "ORDER_CODE"))){
			medTable.setDSValue(row);
			return;
		}
		if (parmBase.getErrCode() < 0) {
			return;
		}
		String rxType,cat1Type="CAT1_TYPE",nullStr="";
		if("TRT".equalsIgnoreCase(parm.getValue(cat1Type))||"PLN".equalsIgnoreCase(parm.getValue(cat1Type))||"OTH".equalsIgnoreCase(parm.getValue(cat1Type))){
			rxType="2";
		}else if("LIS".equalsIgnoreCase(parm.getValue(cat1Type))||"RIS".equalsIgnoreCase(parm.getValue(cat1Type))){
			rxType="5";
		}else{
			rxType="1";
		}
		medTable.acceptText();
		/*
		 * ҽ��,200;����,60,double;��λ,60,UNIT_CODE;Ƶ��,70,FREQ_CODE;�շ�,70,int;
		 * ҽ����ע,200;�ض�����,80,DEPT;��,40,boolean;��������,60,boolean;��������,60,boolean;סԺ����,60,boolean
		 * 0,left;1,right;2,left;3,left;4,right;5,left;6,left;
		 */
		ds.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
		ds.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
		ds.setItem(row, "TRADE_ENG_DESC", parm.getValue("TRADE_ENG_DESC"));
		ds.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
		ds.setItem(row, "DEPT_OR_DR", deptOrDrCode);
		ds.setItem(row, "DEPTORDR_CODE", code);
		ds.setItem(row, "RX_TYPE", rxType);
		ds.setItem(row, "SEQ_NO", getMaxSeq()+1);
		ds.setItem(row, "PRESRT_NO", "0");
		ds.setItem(row, "MEDI_QTY", 0.0);
		ds.setItem(row, "MEDI_UNIT", parmBase.getValue("MEDI_UNIT",0));
		ds.setItem(row, "TAKE_DAYS", parmBase.getValue("TAKE_DAYS",0));
		ds.setItem(row, "FREQ_CODE", parmBase.getValue("FREQ_CODE",0));
		ds.setItem(row, "ROUTE_CODE", parmBase.getValue("ROUTE_CODE",0));
		ds.setItem(row, "DESCRIPTION", parm.getValue("DESCRIPTION"));
		ds.setItem(row, "SPCYDEPT", nullStr);
		ds.setItem(row, "LINK_NO", 0);
		ds.setItem(row, "PACKAGE_TOT", 0);
		ds.setItem(row, "PRESENT_SEQ", 0);
		ds.setActive(row, true);
		if(!StringUtil.isNullString(ds.getItemString(ds.rowCount()-1, "ORDER_CODE"))){
			int newRow=ds.insertRow();
			ds.setActive(newRow, false);
		}



		medTable.setDSValue();
		String specification=ds.getItemString(row, "ORDER_DESC")+" "+ds.getItemString(row, "SPECIFICATION");
		int column=medTable.getColumnIndex("ORDER_DESC");
		medTable.setValueAt(specification, row, column);
		medTable.getTable().grabFocus();
		medTable.setSelectedRow(row);
		medTable.setSelectedColumn(1);
	}

	/**
	 * ���SEQ_NO
	 * @return seq int
	 */
	public int getMaxSeq(){
		int seq=0;
		int tempSeq=0;
		int count=ds.rowCount();
		for(int i=0;i<count;i++){
			seq=ds.getItemInt(i, "SEQ_NO");
			if(seq>tempSeq)
				tempSeq=seq;
		}
		return tempSeq;
	}
	/**
	 * ��ҽ���seq_no
	 * @return
	 */
	public int getChnMaxSeq(){
		int seq=0;
		int tempSeq=0;
		int count=chnDs.rowCount();
		for(int i=0;i<count;i++){
			seq=chnDs.getItemInt(i, "SEQ_NO");
			if(seq>tempSeq)
				tempSeq=seq;
		}
		return tempSeq;
	}
	/**
	 * ɾ��
	 */
	public void onDelete(){
		TTabbedPane t=(TTabbedPane)this.getComponent("TABPANEL");
		String[] sql;
		int row=-1;
		if(t.getSelectedIndex()==0){
			row=medTable.getSelectedRow();
			if(row==medTable.getRowCount()-1){
				return;
			}
			ds.deleteRow(row);
			sql=ds.getUpdateSQL();
			TParm parm=new TParm(TJDODBTool.getInstance().update(sql));
			if(parm.getErrCode()!=0){
				this.messageBox("E0003");
//				this.messageBox_(parm.getErrText());
			}else{
				this.messageBox("P0003");
			}
			onClear();
		}
	}
	/**
	 * ����
	 */
	public void onSave(){
		//ORDER_DESC;OPTITEM_CODE;DR_NOTE;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;PAYAMOUNT;AR_AMT_MAIN;NS_NOTE;EXEC_DEPT_CODE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;NS_EXEC_DATE
		//ҽ��,150;����,80,ITEM_CODE;��ע,80;����,60,double;��λ,60;����,60,double;�ܼ�,80,double;Ӧ��,80,double;�ܽ��,80,double;��ʿ��ע;ִ�п���,100,DEPT_CODE;��,30,boolean;���ʽ,90;�ɷ�����,100;ִ������,100
		insertOptUser();
		ds.setActive(ds.rowCount()-1,false);


		String[] sql=ds.getUpdateSQL();

		String[] tempSql=chnDs.getUpdateSQL();
//		for(String temp:tempSql){
//			// System.out.println("chnDs.sql=[=="+temp);
//		}
		sql=StringTool.copyArray(sql, tempSql);

		if(sql==null||sql.length<1){
			this.messageBox_("�޿ɱ��������");
			onClear();
			return;
		}

		TParm result=new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()!=0){
			this.messageBox("E0001");
//			this.messageBox_(result.getErrText());
//			for(String temp:tempSql){
//				// System.out.println("wrong sql.temp="+temp);
//			}
//			// System.out.println("is err");
//			chnDs.showDebug();
		}else{
			this.messageBox("P0001");
		}
		onClear();
	}
	/**
	 * ����ǰ��������û���ʱ�䡢IP
	 */
	public void insertOptUser(){
		if(ds==null||ds.rowCount()<1){
			return;
		}
		int count=ds.rowCount();
		Timestamp now=TJDODBTool.getInstance().getDBTime();
		for(int i=0;i<count;i++){
			ds.setItem(i, "OPT_USER", Operator.getID());
			ds.setItem(i, "OPT_DATE", now);
			ds.setItem(i, "OPT_TERM", Operator.getIP());
		}

	}
	/**
	 * ����ǩCOMBO��ѡ�¼�
	 */
	public void onChangeRx(){
		String rxNo=this.getValueString("RX_NO");
		if(StringUtil.isNullString(rxNo)){
			return;
		}
		chnDs.setFilter("PRESRT_NO='" +rxNo+"'");
		chnDs.filter();

		int lastRow=chnDs.rowCount()-1;
		if(lastRow>0){
			if(!StringUtil.isNullString(chnDs.getItemString(lastRow, "ORDER_CODE"))){
				int row=chnDs.insertRow();
				chnDs.setItem(row, "PRESRT_NO", rxNo);
				chnDs.setActive(row,false);
			}
		}else{
			if(!StringUtil.isNullString(chnDs.getItemString(lastRow, "ORDER_CODE"))){
				int row=chnDs.insertRow();
				chnDs.setItem(row, "PRESRT_NO", rxNo);
				chnDs.setActive(row,false);
			}
		}
		if(StringUtil.isNullString(chnDs.getItemString(0, "ORDER_CODE"))){
			this.setValue("CHN_FREQ_CODE", chnFreq);
			this.setValue("CHN_ROUTE_CODE", chnRoute);
			this.setValue("TAKE_DAYS", dctTakeDays);
			TComboBox dctAgent=(TComboBox)this.getComponent("DCTAGENT_CODE");
			dctAgent.setSelectedID(dctAgentCode);
			this.setValue("PACKAGE_TOT", 0+"");
			return;
		}
		this.setValue("TAKE_DAYS", chnDs.getItemString(0, "TAKE_DAYS"));
		this.setValue("CHN_FREQ_CODE", chnDs.getItemString(0, "FREQ_CODE"));
		this.setValue("CHN_ROUTE_CODE", chnDs.getItemString(0, "ROUTE_CODE"));
		TComboBox dctAgent=(TComboBox)this.getComponent("DCTAGENT_CODE");
		dctAgent.setSelectedID(chnDs.getItemString(0, "DCTAGENT_CODE"));
		double packageTot=0.0;
		for(int i=0;i<chnDs.rowCount();i++){
			packageTot+=chnDs.getItemDouble(i, "MEDI_QTY");
		}
		this.setValue("PACKAGE_TOT", packageTot+"");


		chnTable.setDSValue();
	}
	/**
	 * ����һ�Ŵ���
	 */
	public void onNewRx(){
		if(chnDs==null){
			return;
		}
		TParm parm=this.getRxNos();
		TComboBox rx=(TComboBox)this.getComponent("RX_NO");
		//// System.out.println("parm======"+parm);

		String rxNo = SystemTool.getInstance().getNo("ALL", "ODO", "RX_NO",
		"RX_NO");
		parm.addData("ID", rxNo);
		parm.addData("NAME", "�� "+(parm.getCount()+1)+ " �Ŵ���");
		parm.setCount(parm.getCount("ID"));
		//// System.out.println("parm=== after===="+parm);
		rx.setParmValue(parm);
		rx.setSelectedID(rxNo);
		chnDs.setFilter("PRESRT_NO='" +rxNo+"'");
		chnDs.filter();
		chnTable.setDSValue();
	}
	/**
	 * ɾ������
	 */
	public void onDeleteRx(){
		TComboBox rx=(TComboBox)this.getComponent("RX_NO");
		if(rx==null){
			return;
		}
		String rxNo=rx.getSelectedID();
		if(StringUtil.isNullString(rxNo)){

			return;
		}
		chnDs.setFilter("PRESRT_NO='" +rxNo+"'");
		chnDs.filter();
		int count=chnDs.rowCount();
		for(int i=count-1;i>-1;i--){
			chnDs.deleteRow(i);
		}
		int row=chnDs.insertRow();
		chnDs.setItem(row, "PRESRT_NO", rxNo);
		chnDs.setActive(row,false);
		chnTable.setDSValue();
	}
	/**
	 * �����ı��¼�
	 */
	public void onTakeDays(){
		TComboBox rx=(TComboBox)this.getComponent("RX_NO");
		if(rx==null){
			return;
		}
		String rxNo=rx.getSelectedID();
		if(StringUtil.isNullString(rxNo)){
			return;
		}
		int days=StringTool.getInt(this.getValueString("TAKE_DAYS"));
		if(days<=0){
			this.messageBox_("��������С��0");
			return;
		}
		if(!onChnValue(rxNo,"TAKE_DAYS",days)){
			this.messageBox_("ֵ�ı�ʧ��");
		}

	}
	/**
	 * ֵ�ı��¼�
	 * @param rxNo
	 * @param fieldName
	 * @param value
	 * @return
	 */
	private boolean onChnValue(String rxNo,String fieldName,Object value){
		if(StringUtil.isNullString(rxNo)||StringUtil.isNullString(fieldName)){
			return false;
		}
		if(value==null){
			return false;
		}
		chnDs.setFilter("PRESRT_NO='" +rxNo+"'");
		chnDs.filter();
		int count=chnDs.rowCount();


		for(int i=0;i<count;i++){
			chnDs.setItem(i, fieldName, value);
		}
		return true;
	}
	/**
	 * Ƶ�θı��¼�
	 */
	public void onFreq(){
		TComboBox rx=(TComboBox)this.getComponent("RX_NO");
		if(rx==null){
			return;
		}
		String rxNo=rx.getSelectedID();
		if(StringUtil.isNullString(rxNo)){
			return;
		}
		TComboBox freq=(TComboBox)this.getComponent("CHN_FREQ_CODE");
		String freqCode=freq.getSelectedID();
		if(StringUtil.isNullString(freqCode)){
			this.messageBox_("Ƶ��ֵ����Ϊ��");
			return;
		}
		if(!onChnValue(rxNo,"FREQ_CODE",freqCode)){
			this.messageBox_("ֵ�ı�ʧ��");
		}
	}
	/**
	 * �巨ֵ�ı��¼�
	 */
	public void onDctAgent(){
		TComboBox rx=(TComboBox)this.getComponent("RX_NO");
		if(rx==null){
			return;
		}
		String rxNo=rx.getSelectedID();
		if(StringUtil.isNullString(rxNo)){
			return;
		}
		TComboBox freq=(TComboBox)this.getComponent("DCTAGENT_CODE");
		String freqCode=freq.getSelectedID();
		if(StringUtil.isNullString(freqCode)){
			this.messageBox_("�巨ֵ����Ϊ��");
			return;
		}
		if(!onChnValue(rxNo,"DCTAGENT_CODE",freqCode)){
			this.messageBox_("ֵ�ı�ʧ��");
		}
	}
	/**
	 * �巨ֵ�ı��¼�
	 */
	public void onRoute(){
		TComboBox rx=(TComboBox)this.getComponent("RX_NO");
		if(rx==null){
			return;
		}
		String rxNo=rx.getSelectedID();
		if(StringUtil.isNullString(rxNo)){
			return;
		}
		TComboBox freq=(TComboBox)this.getComponent("CHN_ROUTE_CODE");
		String freqCode=freq.getSelectedID();
		if(StringUtil.isNullString(freqCode)){
			this.messageBox_("�÷�ֵ����Ϊ��");
			return;
		}
		if(!onChnValue(rxNo,"ROUTE_CODE",freqCode)){
			this.messageBox_("ֵ�ı�ʧ��");
		}
	}
	/**
	 * ������ҽ�����¼�
	 */
	public void onNewR(){
		if(chnDs==null){
			return;
		}
		String rxNo = SystemTool.getInstance().getNo("ALL", "ODO", "RX_NO",
		"RX_NO");
//		// System.out.println("rxNo===="+rxNo);
//		this.messageBox_(rxNo);
		int row=chnDs.insertRow();
		chnDs.setItem(row, "PRESRT_NO", rxNo);
		chnDs.setActive(row,false);
		initChn();
	}

}
