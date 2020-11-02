package com.javahis.ui.clp;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import jdo.clp.CLPOrderReSchdCodeTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatCLPDuration;
/**
 * <p>
 * Title: ����ʱ���޸�
 * </p>
 * 
 * <p>
 * Description: ҽ��ʱ���滻����
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
 * @author pangben 2014.09.4
 * @version 4.0
 */
public class CLPOrderReSchdCodeControl  extends TControl{
	private TTable tableOrder;
	private TTable table;
	private Pat pat;
	TextFormatCLPDuration combo_schd;
	TextFormatCLPDuration recombo_schd;
	String mrNo;
	String clpFlg;
	private Compare compare = new Compare();
	private int sortColumn = -1;
	private boolean ascending = false;
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		tableOrder=(TTable)this.getComponent("TABLE_ORDER");
		table=(TTable)this.getComponent("TABLE");
		addListener(tableOrder);
		onClear();
		//getInitParam();
		//����У��--xiongwg20150427 start
		TParm initParm = new TParm();
		Object obj = this.getParameter();
		if (obj == null || obj.equals("")){
			return;
		}
		if (obj != null && !obj.equals("")) {
			initParm = (TParm) obj;
		}
		mrNo = initParm.getData("CLP", "MR_NO").toString();
		if (initParm.getData("CLP", "FLG") != null) {
			clpFlg = initParm.getData("CLP", "FLG").toString();
			if ("Y".equals(clpFlg)) {
				pat = Pat.onQueryByMrNo(TypeTool.getString(mrNo));
				setValue("MR_NO", pat.getMrNo());
				setValue("PAT_NAME", pat.getName());
				onQuery();
			}

		}
		//����У��--xiongwg20150427 end
	}
	/**
	 * ��ʼ��
	 */
	public void getInitParam(){
		this.setValue("START_IN_DATE",StringTool.rollDate(SystemTool.getInstance().getDate(), -7));
		this.setValue("END_IN_DATE",SystemTool.getInstance().getDate());
	}
	/**
	 * ���
	 */
	public void onClear(){
		getInitParam();
		this.clearValue("DEPT_CODE;STATION_CODE;MR_NO;PAT_NAME;" +
				"CLNCPATH_CODE;SCHD_CODE;SCHD_CODE;BIL_DATE_FLG;" +
				"START_BILL_DATE;END_BILL_DATE;OPT_USER;IN_DEPT_CODE;" +
				"IN_STATION_CODE;DR_CODE;RE_CLNCPATH_CODE;RE_SCHD_CODE;CBK_SEL");
		callFunction("UI|START_BILL_DATE|setEnabled", false);
		callFunction("UI|END_BILL_DATE|setEnabled", false);
		table.setParmValue(new TParm());
		tableOrder.setParmValue(new TParm());
	}
	public void onMrNo(){
		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("�޴˲�����!");
			this.setValue("MR_NO", "");
			return;
		}
		setValue("MR_NO", pat.getMrNo());
		setValue("PAT_NAME", pat.getName());
		this.onQuery();
	}
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		TParm parm=new TParm();
		if (this.getValue("ADM_STATUS").equals("1")) {
			parm.setData("STATUS"," AND A.DS_DATE IS NULL");
		}else if(this.getValue("ADM_STATUS").equals("2")){
			parm.setData("STATUS"," AND A.DS_DATE IS NOT NULL");
		}
		String startDate = StringTool.getString(
				TCM_Transform.getTimestamp(this.getValue("START_IN_DATE")),
				"yyyyMMdd")+"000000";
		String endDate = StringTool.getString(
				TCM_Transform.getTimestamp(this.getValue("END_IN_DATE")),
				"yyyyMMdd")+ "235959";
		parm.setData("START_IN_DATE",startDate);
		parm.setData("END_IN_DATE",endDate);
		if (null!=this.getValue("DEPT_CODE") &&this.getValue("DEPT_CODE").toString().length()>0) {
			parm.setData("DEPT_CODE",this.getValue("DEPT_CODE").toString());
		}
		if (null!=this.getValue("STATION_CODE") &&this.getValue("STATION_CODE").toString().length()>0) {
			parm.setData("STATION_CODE",this.getValue("STATION_CODE").toString());
		}
//		if (null!=pat) {
//			parm.setData("MR_NO",pat.getMrNo());
//		}
		if (null!=this.getValue("MR_NO") &&this.getValue("MR_NO").toString().length()>0) {
			parm.setData("MR_NO",this.getValue("MR_NO").toString());
		}
//		if(!clpFlg.equals("") && clpFlg.equals("Y")){
			parm.setData("FLG",clpFlg);
//		}
		TParm result=CLPOrderReSchdCodeTool.getInstance().selectAdmInp(parm);
		if (result.getErrCode()<0) {
			this.messageBox("��ѯ���ִ���");
			table.setParmValue(new TParm());
			tableOrder.setParmValue(new TParm());
			return;
		}
		if (result.getCount()<=0) {
			this.messageBox("û�в�ѯ������");
			table.setParmValue(new TParm());
			tableOrder.setParmValue(new TParm());
			return;
		}
		table.setParmValue(result);
	}
	/**
	 * ���Ӷ�TalbeReplace�ļ����¼�
	 */
	public void onTableClicked(){
		int row=table.getSelectedRow();
		if (row<0) {
			return;
		}
		this.setValue("CLNCPATH_CODE", table.getParmValue().getValue("CLNCPATH_CODE",row));
		combo_schd = (TextFormatCLPDuration) this.getComponent("SCHD_CODE");
		//combo_schd.setSqlFlg("Y");
        combo_schd.setClncpathCode(table.getParmValue().getValue("CLNCPATH_CODE",row));
        combo_schd.onQuery();
	}
	/**
	 * �����ѯ��ť����
	 */
	public void onOrderQuery(){
		TParm parm=new TParm();
		int row=table.getSelectedRow();
		if (row<0) {
			this.messageBox("��ѡ����Ҫ��ѯ�Ĳ���");
			return;
		}
		TParm tableParm=table.getParmValue();
		if (this.getValue("CLNCPATH_CODE").toString().length()>0) {
			parm.setData("CLNCPATH_CODE",this.getValue("CLNCPATH_CODE").toString());
		}
		if (null!=this.getValue("SCHD_CODE") &&this.getValue("SCHD_CODE").toString().length()>0) {
			parm.setData("SCHD_CODE",this.getValue("SCHD_CODE").toString());
		}
		if (null!=this.getValue("EXEC_DEPT_CODE")&&this.getValue("EXEC_DEPT_CODE").toString().length()>0) {
			parm.setData("EXEC_DEPT_CODE",this.getValue("EXEC_DEPT_CODE").toString());
		}
		if (null!=this.getValue("OPT_USER") &&this.getValue("OPT_USER").toString().length()>0) {
			parm.setData("OPT_USER",this.getValue("OPT_USER").toString());
		}
		if (null!=this.getValue("IN_DEPT_CODE") &&this.getValue("IN_DEPT_CODE").toString().length()>0) {
			parm.setData("IN_DEPT_CODE",this.getValue("IN_DEPT_CODE").toString());
		}
		if (null!=this.getValue("IN_STATION_CODE")&&this.getValue("IN_STATION_CODE").toString().length()>0) {
			parm.setData("IN_STATION_CODE",this.getValue("IN_STATION_CODE").toString());
		}
		if (null!=this.getValue("DR_CODE")&&this.getValue("DR_CODE").toString().length()>0) {
			parm.setData("DR_CODE",this.getValue("DR_CODE").toString());
		}
		parm.setData("CASE_NO",tableParm.getValue("CASE_NO",row));
		if (((TCheckBox)this.getComponent("BIL_DATE_FLG")).isSelected()) {
			if (null==this.getValue("START_BILL_DATE")||
					this.getValue("START_BILL_DATE").toString().length()<=0) {
				this.messageBox("�Ʒѿ�ʼʱ�䲻��Ϊ��");
				return;
			}
			if (null==this.getValue("END_BILL_DATE")||
					this.getValue("END_BILL_DATE").toString().length()<=0) {
				this.messageBox("�Ʒѽ���ʱ�䲻��Ϊ��");
				return;
			}
			String startDate =StringTool.getString(
					TCM_Transform.getTimestamp(this.getValue("START_BILL_DATE")),
					"yyyyMMddHHmmss");
			String endDate = StringTool.getString(
					TCM_Transform.getTimestamp(this.getValue("END_BILL_DATE")),
					"yyyyMMddHHmmss");
			parm.setData("START_BILL_DATE",startDate);
			parm.setData("END_BILL_DATE",endDate);
		}
		TParm result=CLPOrderReSchdCodeTool.getInstance().queryIbsOrder(parm);
		if (result.getErrCode()<0) {
			this.messageBox("��ѯ���ִ���");
			tableOrder.setParmValue(new TParm());
			return;
		}
		if (result.getCount()<=0) {
			this.messageBox("û�в�ѯ������");
			tableOrder.setParmValue(new TParm());
			return;
		}
		tableOrder.setParmValue(result);
	}
	/**
	 * �ٴ�·����Ŀ�ؼ�ѡ���¼�
	 */
	public void onClncpathCode(){
		combo_schd = (TextFormatCLPDuration) this.getComponent("SCHD_CODE");
		//combo_schd.setSqlFlg("Y");
        combo_schd.setClncpathCode(this.getValueString("CLNCPATH_CODE"));
        combo_schd.onQuery();
	}
	public void onRgClncpathCode(){
		recombo_schd = (TextFormatCLPDuration) this.getComponent("RE_SCHD_CODE");
		//combo_schd.setSqlFlg("Y");
		recombo_schd.setClncpathCode(this.getValueString("RE_CLNCPATH_CODE"));
		recombo_schd.onQuery();
	}
	/**
	 * ȫѡ
	 */
	public void onSel(){
		TParm tableParm=tableOrder.getParmValue();
		if (((TCheckBox)this.getComponent("CBK_SEL")).isSelected()) {//ȫѡ
			for (int i = 0; i < tableParm.getCount(); i++) {
				tableParm.setData("FLG",i,"Y");
			}
		}else{
			for (int i = 0; i < tableParm.getCount(); i++) {
				tableParm.setData("FLG",i,"N");
			}
		}
		tableOrder.setParmValue(tableParm);	
	}
	/**
	 * �滻����
	 */
	public void onSave(){
		tableOrder.acceptText();
		TParm tableParm=tableOrder.getParmValue();
		if (tableParm.getCount()<=0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		TParm parm=new TParm();
		if (this.getValue("RE_CLNCPATH_CODE").toString().length()>0) {
			parm.setData("RE_CLNCPATH_CODE",this.getValue("RE_CLNCPATH_CODE").toString());
			if (this.getValue("RE_SCHD_CODE").toString().length()<=0) {
				this.messageBox("·���޸Ĳ���,ʱ��Ҳ��Ҫ�޸�");
				return;
			}
		}
		if (this.getValue("RE_SCHD_CODE").toString().length()>0) {
			parm.setData("RE_SCHD_CODE",this.getValue("RE_SCHD_CODE").toString());
		}
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getValue("FLG",i).equals("Y")) {
				parm.addRowData(tableParm, i);
			}
		}
		if (parm.getCount("FLG")<=0) {
			this.messageBox("��ѡ����Ҫ����������");
			return;
		}
		TParm result = TIOM_AppServer.executeAction(
				"action.clp.CLPOrderReSchdCodeAction", "onSave", parm);
		if (result.getErrCode()<0) {
			this.messageBox("�滻ʧ��");
			return;
		}
		this.messageBox("�滻�ɹ�");
		onOrderQuery();
	}
	/**
	 * ���渴ѡ���¼�
	 */
	public void onChk(){
		if (((TCheckBox)this.getComponent("BIL_DATE_FLG")).isSelected()) {
			this.callFunction("UI|START_BILL_DATE|setEnabled", true);
			this.callFunction("UI|END_BILL_DATE|setEnabled", true);
		}else{
			this.callFunction("UI|START_BILL_DATE|setEnabled", false);
			this.callFunction("UI|END_BILL_DATE|setEnabled", false);
		}
		this.setValue("START_BILL_DATE", "");
		this.setValue("END_BILL_DATE", "");
	}
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = tableOrder.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = tableOrder.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		tableOrder.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}
	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}
	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}
}
