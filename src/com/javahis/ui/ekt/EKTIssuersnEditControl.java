package com.javahis.ui.ekt;

import jdo.sys.Operator;
//import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
/**
 * <p>
 * Title: ����ԭ��
 * </p>
 * 
 * <p>
 * Description: ����ԭ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author zhangpeng
 *
 */
public class EKTIssuersnEditControl extends TControl{
	String issuersnCode = "";//ԭ�����
	String name ="";//ԭ������
	double factorage = 0.00;//������
	String optIp = "";
	String optId = "";
	TCheckBox flg = null;//Ĭ��ע��
	TTable table = null;
	int row = -1;
	TParm rowParm = null;
	TTextField code = null;
	String flgword = "N";
	public EKTIssuersnEditControl(){
		
	}
	
    /**
	 * ��ʼ������
	 */
	public void onInit() {
		optIp = Operator.getIP();
		optId = Operator.getID();
		table = (TTable) getComponent("TABLE");
		flg = (TCheckBox) getComponent("FLG");
		code = (TTextField) getComponent("ISSUERSN_CODE");
		onQuery();
	}
	/**
	 * ����
	 */
	public void onSave(){
		getThisValue();
		if(issuersnCode.equals("")){
			this.messageBox("�������ţ�");
			return;
		}
		if(name.equals("")){
			this.messageBox("���������ƣ�");
			return;
		}
		TParm result = null;
		if(row < 0){
			result = save();
		}else{
			result = update();
		}
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			return;
		}
		messageBox("�����ɹ�");
		onClear();
		onQuery();
	}
	/**
	 * ����
	 */
	public TParm save(){
		String sql = 
			"INSERT INTO EKT_ISSUERSN VALUES('"+issuersnCode+"','"+name+"',"+factorage+"," +
					"'"+optId+"',SYSDATE,'"+optIp+"','"+flgword+"')";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	}
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		getThisValue();
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT ISSUERSN_CODE,ISSUERSN_DESC,FACTORAGE_FEE,OPT_USER,OPT_DATE,OPT_TERM,FLG FROM EKT_ISSUERSN WHERE 1=1 ");
		if(!"".equals(issuersnCode)){
			sql.append(" AND ISSUERSN_CODE = '"+issuersnCode+"' ");
		}
		if(!"".equals(name)){
			sql.append(" AND ISSUERSN_DESC = '"+name+"' ");
		}
		sql.append(" ORDER BY ISSUERSN_CODE");
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		table.setParmValue(result);
	}
	/**
	 * table������
	 */
	public void onClickTable(){
		code.setEnabled(false);
		row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		rowParm = tableParm.getRow(row);
		setValue("ISSUERSN_CODE", rowParm.getValue("ISSUERSN_CODE"));
		setValue("NAME", rowParm.getValue("ISSUERSN_DESC"));
		setValue("FACTORAGE_FEE", rowParm.getValue("FACTORAGE_FEE"));
		if(rowParm.getValue("FLG").equals("Y")){
			flg.setSelected(true);
		}else{
			flg.setSelected(false);
		}
	}
	/**
	 * ���
	 */
	public void onClear(){
		code.setEnabled(true);
		clearValue("ISSUERSN_CODE;NAME;FACTORAGE_FEE");
		flg.setSelected(false);
		row = -1;
		flgword = "N";
		onQuery();
	}
	/**
	 * ɾ��
	 */
	public void onDelete(){
		if(row < 0){
			messageBox("����ѡ��");
			return;
		}
		getThisValue();
		String sql = 
			"DELETE EKT_ISSUERSN WHERE ISSUERSN_CODE = '"+issuersnCode+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			return;
		}
		messageBox("�����ɹ�");
		onClear();
		onQuery();
	}
	/**
	 * �޸�
	 */
	public TParm update(){
		String sql =
			"UPDATE EKT_ISSUERSN SET ISSUERSN_DESC = '"+name+
			"',FACTORAGE_FEE = "+factorage+
			",FLG = '"+flgword+"',OPT_USER = '"+optId+
			"',OPT_TERM = '"+optIp+"',OPT_DATE = SYSDATE WHERE ISSUERSN_CODE = '"+issuersnCode+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	}
	/**
	 * ȡ��ֵ
	 */
	public void getThisValue(){
		issuersnCode = getValueString("ISSUERSN_CODE");
		name = getValueString("NAME");
		factorage = StringTool.getDouble(getValueString("FACTORAGE_FEE"));
		if(flg.isSelected()){
			flgword = "Y";
		}else{
			flgword = "N";
		}
	}
	
}
