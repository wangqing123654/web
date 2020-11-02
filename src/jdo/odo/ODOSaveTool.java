package jdo.odo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 *
 * <p>
 * Title: ҽ��վ����
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author ehui 20091015
 * @version 1.0
 */
public class ODOSaveTool extends TJDOTool {
	/**
     * ʵ��
     */
    public static ODOSaveTool instanceObject;
    /**
     * �õ�ʵ��
     * @return ODOSaveTool
     */
    public static ODOSaveTool getInstance()
    {
        if(instanceObject == null){
        	instanceObject = new ODOSaveTool();
        }

        return instanceObject;
    }
    /**
     * ����
     * @param parm
     * @return
     */
    public TParm onSave(TParm parm,TConnection conn){

		TParm result=new TParm();
		String[] sql=(String[])parm.getData("SQL");
		//System.out.println("ODOSaveTool.sql.length:"+sql.length);
		if(sql==null){
			return result;
		}
		if(sql.length<1){
			return result;
		}
		for(String tempSql:sql){
			result=new TParm(TJDODBTool.getInstance().update(tempSql, conn));
			if(result.getErrCode()!=0){
				System.out.println("ODOSaveTool wrong sql:"+tempSql);
				return result;
			}
		}
		return result;

    }
    /**
     * ��ӡ�����־
     * @param parm
     */
    public void  onCheckOrderLog(TParm parm){
    	String[] sql=(String[])parm.getData("SQL");
		if(sql==null){
			return ;
		}
		if(sql.length<1){
			return ;
		}
		String caseNo=parm.getValue("CASE_NO");
		String delOrderStr="DELETE FROM OPD_ORDER";
		String delMedStr="DELETE FROM MED_APPLY";
		String inOrderStr="INSERT INTO OPD_ORDER_HISTORY";
		List<String> OrderList = Arrays.asList(sql);
		List<String> delOrderList = new ArrayList<String>();
		List<String> delMedList = new ArrayList<String>();
		List<String> inOrderList = new ArrayList<String>();
		for(String tempSql:sql){
			if(tempSql.contains(delOrderStr))
				delOrderList.add(tempSql);
			if(tempSql.contains(delMedStr))
				delMedList.add(tempSql);
			if(tempSql.contains(inOrderStr))
				inOrderList.add(tempSql);
		}
		String sqllog=this.getCheckSql(caseNo);
		TParm logParm=new TParm(TJDODBTool.getInstance().select(sqllog));
		if(logParm.getCount()>0){
			System.out.println("============����ҽ��վɾ�����������ݴ���ʼ��־׷��==============="+"\n");
			System.out.println("sql����:"+OrderList+"\n");
			System.out.println("ɾ��ҽ������:"+delOrderList+"\n");
			System.out.println("ɾ�������鼯��"+delMedList+"\n");
			System.out.println("������ʷ����"+inOrderList+"\n");
			System.out.println("=====================������־׷��==================================="+"\n");
		}
    }
    /**
     * У�����ݽ��sql
     */
    public String  getCheckSql(String caseNo){
    	StringBuffer sb=new StringBuffer();
    	sb.append("SELECT CASE_NO, ORDER_NO, SEQ_NO FROM MED_APPLY WHERE CASE_NO='#' ");
    	sb.append(" MINUS ");
    	sb.append("SELECT CASE_NO, RX_NO ORDER_NO, SEQ_NO FROM OPD_ORDER");
    	sb.append(" WHERE SETMAIN_FLG = 'Y' AND (CAT1_TYPE = 'LIS' OR CAT1_TYPE = 'RIS') AND RX_TYPE = '5' AND  CASE_NO='#' ");
    	return sb.toString().replace("#", caseNo);	
    }
}
