package com.javahis.ui.ekt;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.StringUtil;


/**
 * <p>Title: �����շ��տ���ϸ��</p>
 *
 * <p>Description:  �����շ��տ���ϸ�� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author zhangp 20120111
 * @version 1.0
 */

public class EKTTopUpSellCardDetailControl extends TControl{

	/**
     * ��ʼ������
     */
    public void onInit() {
    	setValue("REGION_CODE", Operator.getRegion());
    	Timestamp today = SystemTool.getInstance().getDate();
    	setValue("START_DATE", today);
    	setValue("END_DATE", today);
    	setValue("CREAT_USER", Operator.getID());
    }
    
    /**
     * ��ȡsql
     * @return
     */
    public String getSql(){
    	String regionCode = getValueString("REGION_CODE");
    	String creatUser = getValueString("CREAT_USER");
    	StringBuilder sql = new StringBuilder();
    	String sql1 = "SELECT A.ACCNT_TYPE,COUNT(*) AS COUNT,SUM(A.AMT) AS SUM FROM EKT_BIL_PAY A,SYS_OPERATOR B WHERE A.CREAT_USER = B.USER_ID " +
    			" AND A.ACCNT_TYPE IN('1','2','3') ";
    	sql.append(sql1);
    	if(regionCode!=null&&!regionCode.equals("")){
    		sql.append(" AND B.REGION_CODE = '"+regionCode+"' ");
    	}
    	if(creatUser!=null&&!creatUser.equals("")){
    		sql.append(" AND A.CREAT_USER = '"+creatUser+"' ");
    	}
    	String startDate = "";
		String endDate = "";
		if (!"".equals(this.getValueString("START_DATE"))&&!"".equals(this.getValueString("END_DATE"))){
			startDate = getValueString("START_DATE").substring(0, 10);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10) + "000000";
		endDate = getValueString("END_DATE").substring(0, 10); 
		endDate =endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10)+"235959";
			sql.append(" AND A.OPT_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('" + endDate +
                "','YYYYMMDDHH24MISS') ");
		}
    	sql.append(" GROUP BY ACCNT_TYPE");
    	return sql.toString();
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
    	String sql = getSql();
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return;
    	}
    	if(result.getCount()<=0){
    		messageBox("��������");
    		return;
    	}
    	sql = 
    		"SELECT ISSUERSN_CODE,FACTORAGE_FEE FROM EKT_ISSUERSN WHERE ISSUERSN_CODE = '8'";
    	TParm factResult = new TParm(TJDODBTool.getInstance().select(sql));
    	TParm parm = new TParm();
    	parm.addData("ACCNT_TYPE", 1);
    	parm.addData("ACCNT_TYPE", 2);
    	parm.addData("ACCNT_TYPE", 3);
    	for (int i = 0; i < result.getCount(); i++) {
			if(result.getData("ACCNT_TYPE", i).equals("1")){
				parm.addData("COUNT", result.getData("COUNT", i));
				parm.addData("SUM", result.getData("SUM", i));
			}
			if(result.getData("ACCNT_TYPE", i).equals("2")){
				parm.addData("COUNT", result.getData("COUNT", i));
				parm.addData("SUM", factResult.getDouble("FACTORAGE_FEE", 0)*result.getInt("COUNT", i));
			}
			if(result.getData("ACCNT_TYPE", i).equals("3")){
				parm.addData("COUNT", result.getData("COUNT", i));
				parm.addData("SUM", result.getData("SUM", i));
			}
		}
    	this.callFunction("UI|TABLE|setParmValue", parm);
    }
    /**
     * ��ӡ
     */
    public void onPrint(){
    	TTable table = (TTable) this.getComponent("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox("û�д�ӡ����");
			return;
		}
		TParm tableparm = table.getParmValue();
		TParm data = new TParm();// ��ӡ������
		TParm parm = new TParm();// �������
		int qtyTotal = 0; //�����ϼ�
		double amtTotal = 0.00;//�ϼƽ��
		for (int i = 0; i < table.getRowCount(); i++) {
			if(tableparm.getData("ACCNT_TYPE", i).equals(1)){
				parm.addData("ACCNT_TYPE", "����");
			}
			if(tableparm.getData("ACCNT_TYPE", i).equals(2)){
				parm.addData("ACCNT_TYPE", "����");
			}
			if(tableparm.getData("ACCNT_TYPE", i).equals(3)){
				parm.addData("ACCNT_TYPE", "����");
			}
			parm.addData("COUNT", tableparm.getInt("COUNT", i));
			parm.addData("SUM", tableparm.getDouble("SUM", i));
			parm.addData("PARM4", "");
			qtyTotal = qtyTotal + tableparm.getInt("COUNT", i);
			amtTotal = amtTotal + tableparm.getDouble("SUM", i);
		}
		parm.addData("ACCNT_TYPE", "�ϼ�");
		parm.addData("COUNT", qtyTotal);
		parm.addData("SUM", amtTotal);
		String date = SystemTool.getInstance().getDate().toString();
		data.setData("DATE", "TEXT", date.substring(0, 4)+
    			"/"+date.substring(5, 7)+"/"+date.substring(8, 10));
		data.setData("SUMWORD", "TEXT", StringUtil.getInstance().numberToWord(amtTotal));
		parm.setCount(parm.getCount("ACCNT_TYPE"));
		parm.addData("SYSTEM", "COLUMNS", "ACCNT_TYPE");
		parm.addData("SYSTEM", "COLUMNS", "COUNT");
		parm.addData("SYSTEM", "COLUMNS", "SUM");
		parm.addData("SYSTEM", "COLUMNS", "PARM4");
		data.setData("TABLE", parm.getData());
		//======== modify by lim 2012/02/24 begin
		this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTTopUpSellCardDetail.jhw",data);
		//======== modify by lim 2012/02/24 end
    }
    /**
     * ���
     */
    public void onClear(){
    	clearValue("CREAT_USER");
    }
}
