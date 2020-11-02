package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: ��������ֵ�ά��Tool
 * </p>
 * 
 * <p>
 * Description: ��������ֵ�ά��Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wangzl 20130502
 * @version 1.0
 */
public class INVSUPTitemTool extends TJDODBTool {
	/** ʵ�� */
	private static INVSUPTitemTool instance;

	/** �õ�ʵ�� */
	public static INVSUPTitemTool getInstance() {
		if (instance == null)
			return instance = new INVSUPTitemTool();
		return instance;
	}
	/**���淽��*/
	public TParm onSave(TParm parm){
		String sql="INSERT INTO INV_SUPTITEM (SUPITEM_CODE,SUPITEM_DESC,PY1,PY2,DESCRIPTION," 
											+"COST_PRICE,ADD_PRICE,OPT_USER,OPT_DATE,OPT_TERM) " 
					   +"VALUES (" 
					   			+"'"+parm.getData("SUPITEM_CODE")+"'," 
					   			+"'"+parm.getData("SUPITEM_DESC")+"'," 
					   			+"'"+parm.getData("PY1")+"'," 
					   			+"NULL," 
					   			+"'"+parm.getData("DESCRIPTION")+"'," 
					   			+""+parm.getDouble("COST_PRICE")+"," 
					   			+""+parm.getDouble("ADD_PRICE")+"," 
					   			+"'"+parm.getData("OPT_USER")+"'," 
					   			+"SYSDATE," 
					   			+"'"+parm.getData("OPT_TERM")+"')";
		TParm result=new TParm(this.update(sql));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	/**���·���*/
	public TParm onUpdate(TParm parm){
		String sql="UPDATE INV_SUPTITEM " 
					 +"SET SUPITEM_DESC='"+parm.getData("SUPITEM_DESC")+"'," +
					 		"PY1='"+parm.getData("PY1")+"'," +
					 		"DESCRIPTION='"+parm.getData("DESCRIPTION")+"'," +
					 		"COST_PRICE="+parm.getDouble("COST_PRICE")+"," +
					 		"ADD_PRICE="+parm.getDouble("ADD_PRICE")+"," +
					 		"OPT_USER='"+parm.getData("OPT_USER")+"'," +
					 		"OPT_DATE=SYSDATE," +
					 		"OPT_TERM='"+parm.getData("OPT_TERM")+"' " 
				   +"WHERE SUPITEM_CODE='"+parm.getData("SUPITEM_CODE")+"'";
		TParm result=new TParm(this.update(sql));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	/**ɾ������*/
	public TParm onDelete(TParm parm){
		String sql="DELETE INV_SUPTITEM WHERE SUPITEM_CODE='"+parm.getData("SUPITEM_CODE")+"'";
		TParm result=new TParm(this.update(sql));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
}
