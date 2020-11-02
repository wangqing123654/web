package com.javahis.ui.clp;

import jdo.clp.CLPOrderReplaceTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.system.textFormat.TextFormatCLPDuration;

/**
 * <p>
 * Title: ·��ҽ���滻
 * </p>
 * 
 * <p>
 * Description: �ٴ�·��ҽ���滻����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 2014.08.28
 * @version 4.0
 */
public class CLPOrderReplaceControl extends TControl {
	private TTable tableReplace;
	private TTable tableClpPack;
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		this.setValue("CLNCPATH_CODE", "");
		// ֻ��text���������������sys_fee������
		callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
				"%ROOT%\\config\\sys\\SYSFeePopup.x");
		// ���ܻش�ֵ
		callFunction("UI|ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		getInitParam();
	}
	/**
	 * ���ô��������б�ѡ��
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
		this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
	}
	/**
	 * ���ܲ���
	 */
	private void getInitParam(){
		tableReplace=(TTable) this.getComponent("TABLE_REPLACE");
		tableClpPack=(TTable) this.getComponent("TABLE_CLP_PACK");
		((TTable) getComponent("TABLE_CLP_PACK")).addEventListener(
				TTableEvent.CHECK_BOX_CLICKED, this, "onTableComponent");
	}
	/**
	 * ���Ӷ�TalbeReplace�ļ����¼�
	 */
	public void onTableClicked(){
		int row=tableReplace.getSelectedRow();
		if (row<0) {
			return;
		}
		TParm parm=tableReplace.getParmValue();
		TParm temp=parm.getRow(row);
		temp.setData("ORDER_CODE",temp.getValue("ORDER_CODE_OLD"));
		if (null!=this.getValue("CLNCPATH_CODE")&& this.getValue("CLNCPATH_CODE").toString().length()>0) {
			temp.setData("CLNCPATH_CODE",this.getValue("CLNCPATH_CODE"));
		}
		if (null!=this.getValue("SCHD_CODE") &&this.getValue("SCHD_CODE").toString().length()>0) {
			temp.setData("SCHD_CODE",this.getValue("SCHD_CODE"));
		}
		TParm result=CLPOrderReplaceTool.getInstance().selectClpPack(temp);
		if (result.getErrCode()<0) {
			this.messageBox("��ѯ���ִ���");
			return;
		}
		if (result.getCount()<=0) {
			this.messageBox("û�в�ѯ������");
			tableClpPack.removeRowAll();
			return;
		}
		TParm parmValue=new TParm();
		for (int i = 0; i < result.getCount(); i++) {
			parmValue.addData("CLNCPATH_CODE", result.getValue("CLNCPATH_CODE",i));
			parmValue.addData("SCHD_CODE", result.getValue("SCHD_CODE",i));
			parmValue.addData("ORDER_TYPE", result.getValue("ORDER_TYPE",i));
			parmValue.addData("ORDER_CODE_OLD", result.getValue("ORDER_CODE",i));
			parmValue.addData("ORDER_DESC_OLD", temp.getValue("ORDER_DESC_OLD"));
			parmValue.addData("CHKTYPE_CODE", result.getValue("CHKTYPE_CODE",i));
			parmValue.addData("ORDER_SEQ_NO", result.getValue("ORDER_SEQ_NO",i));
			parmValue.addData("MEDI_QTY_OLD", result.getDouble("DOSE",i));
			parmValue.addData("MEDI_UNIT_OLD", result.getValue("DOSE_UNIT",i));
			parmValue.addData("ROUTE_CODE_OLD", result.getValue("ROUT_CODE",i));
			parmValue.addData("CLP_STATUS", result.getValue("CLP_STATUS",i));
//			parmValue.addData("ORDER_CODE", temp.getValue("ORDER_CODE"));
			parmValue.addData("ORDER_CODE", parm.getValue("ORDER_CODE",row));
			parmValue.addData("ORDER_DESC", temp.getValue("ORDER_DESC"));
			parmValue.addData("MEDI_QTY",temp.getDouble("MEDI_QTY")*result.getDouble("DOSE",i)/
					temp.getDouble("MEDI_QTY_OLD"));//��ҽ������ ���ݱ�������  �ֵ�����ҽ��������*��ҽ��������/�ֵ��ҽ��������
			parmValue.addData("UNIT_CODE", temp.getValue("MEDI_UNIT"));
			parmValue.addData("ROUTE_CODE", temp.getValue("ROUTE_CODE"));
			parmValue.addData("VERSION", result.getValue("VERSION",i));
			parmValue.addData("FREQ_CODE",result.getValue("FREQ_CODE",i));
			parmValue.addData("DOSE_DAYS",result.getValue("DOSE_DAYS",i));
			parmValue.addData("NOTE",result.getValue("NOTE",i));
			parmValue.addData("ORDER_FLG",result.getValue("ORDER_FLG",i));
			parmValue.addData("SEQ",result.getValue("SEQ",i));
			parmValue.addData("RBORDER_DEPT_CODE",result.getValue("RBORDER_DEPT_CODE",i));
			parmValue.addData("URGENT_FLG",result.getValue("URGENT_FLG",i));
			parmValue.addData("CHKUSER_CODE",result.getValue("CHKUSER_CODE",i));
			parmValue.addData("EXEC_FLG",result.getValue("EXEC_FLG",i));
			parmValue.addData("ORDTYPE_CODE",result.getValue("ORDTYPE_CODE",i));
			parmValue.addData("STANDARD",result.getValue("STANDARD",i));
			parmValue.addData("OWN_PRICE",temp.getDouble("OWN_PRICE"));
			parmValue.addData("START_DAY",result.getValue("START_DAY",i));
			parmValue.addData("FLG", "N");
		}
		parmValue.setCount(parmValue.getCount("CLNCPATH_CODE"));
		System.out.println("======+++++======parmValue parmValue is ::"+parmValue);
		tableClpPack.setParmValue(parmValue);
	}
	/**
	 * ��ѯ����
	 */
	public void onQuery(){
		this.onQueryOrder();
	}
	/**
	 * ��ѯ��ť
	 */
	public void onQueryOrder(){
		TParm parm=new TParm();
		if (this.getValue("ORDER_CODE").toString().length()>0) {
			parm.setData("ORDER_CODE_OLD",this.getValue("ORDER_CODE"));
		}
		parm.setData("PACK_CODE","CLP");//01:ҽ���ײ�,02:��ʿ�ײ�,03:�ٴ�·���ײ�
		TParm result=CLPOrderReplaceTool.getInstance().selectComorderReplace(parm);
		if (result.getErrCode()<0) {
			this.messageBox("��ѯ���ִ���");
			return;
		}
		if (result.getCount()<=0) {
			this.messageBox("û�в�ѯ������");
			tableReplace.removeRowAll();
			return;
		}
		tableReplace.setParmValue(result);
	}
	/**
	 * �������
	 */
	public void onSave(){
		tableClpPack.acceptText();
		TParm tableParm=tableClpPack.getParmValue();
		TParm parm=new TParm();
		int index=0;
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getValue("FLG",i).equals("Y")) {//��ѡ
				parm.addRowData(tableParm, i);
				index++;
			}
		}
		if (index<=0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		parm.setCount(index);
		parm.setData("OPT_USER_T",Operator.getID());
		parm.setData("OPT_TERM_T",Operator.getIP());
		TParm result = TIOM_AppServer.executeAction(
				"action.clp.CLPOrderReplaceAction", "onSave", parm);
		if (result.getErrCode()<0) {
			this.messageBox("����ʧ��");
			System.out.println("����ʧ��:"+result.getErrText());
			return;
		}
		this.messageBox("����ɹ�");
		this.onTableClicked();
	}
	/**
	 * �ٴ�·���ؼ������¼�
	 */
	public void onClickClncpathCode() {
		TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this
				.getComponent("SCHD_CODE");
		if (this.getValue("CLNCPATH_CODE").toString().length() > 0) {
			combo_schd.setClncpathCode(this.getValue("CLNCPATH_CODE")
					.toString());
		}
		combo_schd.onQuery();
	}
	/**
	 * ȫѡ��ť
	 */
	public void onSelAll(){
		TParm tableParm=tableClpPack.getParmValue();
		if (((TCheckBox)this.getComponent("CHK_ALL")).isSelected()) {//ȫѡ
			for (int i = 0; i < tableParm.getCount(); i++) {
				if (tableParm.getValue("CLP_STATUS",i).length()<=0||
						tableParm.getValue("CLP_STATUS",i).equals("1")) {//�޶�״̬�������޸�	
				}else{
					tableParm.setData("FLG",i,"Y");
				}
			}
		}else{
			for (int i = 0; i < tableParm.getCount(); i++) {
				tableParm.setData("FLG",i,"N");
			}
		}
		tableClpPack.setParmValue(tableParm);	
	}
	/**
	 * ��ѡ��ѡ�����
	 */
	public void onTableComponent(Object obj){
		TTable table = (TTable) obj;
		table.acceptText();
		//TParm tableParm = table.getParmValue();
		int col = tableClpPack.getSelectedColumn();
		int row = tableClpPack.getSelectedRow();
		String columnName = tableClpPack.getDataStoreColumnName(col);
		if (columnName.equals("FLG")&&table.getParmValue().getValue("FLG", row).equals("Y")) {
			if (table.getParmValue().getValue("CLP_STATUS",row).length()>0
					&&!table.getParmValue().getValue("CLP_STATUS",row).equals("2")) {
				this.messageBox("��·����Ŀ���޶���,�������޸�ҽ��");
				tableClpPack.getParmValue().setData("FLG",row,"N");
				tableClpPack.setParmValue(tableClpPack.getParmValue());
				return;
			}
		}
	}
	public void onExe(){
		TParm result=new TParm(TJDODBTool.getInstance().select("javahisLSJ", "SELECT * FROM SYMBOL_DICT"));
//		System.out.println("result::::"+result);
		this.messageBox_(result);
	}
	public void onClear(){
		this.setValue("CLNCPATH_CODE", "");
		this.setValue("ORDER_CODE", "");
		this.setValue("SCHD_CODE", "");
		this.setValue("ORDER_DESC", "");
		this.setValue("CHK_ALL", false);
		tableClpPack.removeRowAll();
		tableReplace.removeRowAll();
	}
}
