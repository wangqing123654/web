package action.sys;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: ��Ա�������
 * </p>
 * 
 * <p>
 * Description: ��Ա�������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author duzhw 2013.12.26
 * @version 1.0
 */
public class SYSMemCtzAction extends TAction{
	
	/**
	 * ִ�б�������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
			//�޸�--���������޸�����
			TParm mainUpdateParm = parm.getParm("MAINUPDATEDATA");
			//System.out.println("Tool��mainUpdateParm="+mainUpdateParm);
			
			for (int n = 0; n < mainUpdateParm.getCount("CTZ_CODE"); n++) {
				TParm pdateparm=mainUpdateParm.getRow(n);
				String updatesql=this.getMainUpateSql(pdateparm);
				result = new TParm(TJDODBTool.getInstance().update(updatesql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			//�޸�--ϸ����޸�����
			TParm updateParm = parm.getParm("UPDATEDATA");
			//System.out.println("Tool��updateParm="+updateParm);
			for (int m = 0; m < updateParm.getCount("ORDER_CODE"); m++) {
				TParm pdateparm=updateParm.getRow(m);
				String updatesql=this.getUpateSql(pdateparm);
				result = new TParm(TJDODBTool.getInstance().update(updatesql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			//ɾ��
			TParm delParm = parm.getParm("DELDATA");
			//System.out.println("Tool��delParm="+delParm);
			for (int k = 0; k < delParm.getCount("CTZ_CODE"); k++) {
				TParm delparm=delParm.getRow(k);
				String delsql=this.getDelSql(delparm);
				result = new TParm(TJDODBTool.getInstance().update(delsql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			// ������Ϣ
			TParm insertParm = parm.getParm("INSERTDATA");
			//System.out.println("Tool��insertParm="+insertParm);
			for (int i = 0; i < insertParm.getCount("ORDER_DESC"); i++) {
				TParm inparm=insertParm.getRow(i);
				String insertsql=this.getSql(inparm);
				result = new TParm(TJDODBTool.getInstance().update(insertsql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * �������
	 * sql
	 * @return
	 
	private String getSql(TParm parm) {
		String sql = "INSERT INTO SYS_CTZ_FEE(CTZ_CODE,ORDER_CODE,ORDER_DESC,DISCOUNT_RATE,OPT_DATE," +
		"OPT_USER,OPT_TERM) VALUES('"+parm.getValue("CTZ_CODE")+"'," +
				"'"+parm.getValue("ORDER_CODE")+"'," +
				"'"+parm.getValue("ORDER_DESC")+"'," +
				"'"+parm.getValue("DISCOUNT_RATE")+"'," +
				"sysdate," +
				"'"+parm.getValue("OPT_USER")+"'," +
				"'"+parm.getValue("OPT_TERM")+"')";
		System.out.println("��������sql="+sql);
		return sql;
	} */
	
	/**
	 * �������-��Ŀ��ϸ��SYS_CTZ_ORDER_DETAIL
	 * sql
	 * @return
	 */
	private String getSql(TParm parm) {
		String sql = "INSERT INTO SYS_CTZ_ORDER_DETAIL(CTZ_CODE,ORDER_CODE,ORDER_DESC,DISCOUNT_RATE,OPT_DATE," +
		"OPT_USER,OPT_TERM,SETMAIN_FLG,ORDERSET_CODE,HIDE_FLG) VALUES('"+parm.getValue("CTZ_CODE")+"'," +
				"'"+parm.getValue("ORDER_CODE")+"'," +
				"'"+parm.getValue("ORDER_DESC")+"'," +
				"'"+parm.getValue("DISCOUNT_RATE")+"'," +
				"sysdate," +
				"'"+parm.getValue("OPT_USER")+"'," +
				"'"+parm.getValue("OPT_TERM")+"'," +
				"'"+parm.getValue("SETMAIN_FLG")+"'," +
				"'"+parm.getValue("ORDERSET_CODE")+"'," +
				"'"+parm.getValue("HIDE_FLG")+"')";
		//System.out.println("��������sql="+sql);
		return sql;
	}
	
	/**
	 * ɾ��sql
	 
	private String getDelSql(TParm parm) {
		String sql = "DELETE FROM SYS_CTZ_FEE WHERE CTZ_CODE = '"+parm.getValue("CTZ_CODE")+"' " +
				" AND ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"'";
		System.out.println("ɾ��sql="+sql);
		return sql;
	}*/
	
	/**
	 * ɾ��sql--SYS_CTZ_ORDER_DETAIL
	 */
	private String getDelSql(TParm parm) {
		String sql = "DELETE FROM SYS_CTZ_ORDER_DETAIL WHERE CTZ_CODE = '"+parm.getValue("CTZ_CODE")+"' " +
				" AND ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"'";
		//System.out.println("ɾ��sql="+sql);
		return sql;
	}
	
	/**
	 * �޸�sql-��ϸ��
	 
	private String getUpateSql(TParm parm) {
		String sql = "UPDATE SYS_CTZ_FEE SET DISCOUNT_RATE = '"+parm.getValue("DISCOUNT_RATE")+"',OPT_DATE = sysdate," +
				" OPT_USER = '"+parm.getValue("OPT_USER")+"', OPT_TERM = '"+parm.getValue("OPT_TERM")+"' " +
				" WHERE CTZ_CODE = '"+parm.getValue("CTZ_CODE")+"' AND ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"'";
		System.out.println("��ϸ���޸�sql="+sql);
		return sql;
	} */
	
	/**
	 * �޸�sql-��ϸ�� SYS_CTZ_ORDER_DETAIL
	 */
	private String getUpateSql(TParm parm) {
		String sql = "UPDATE SYS_CTZ_ORDER_DETAIL SET DISCOUNT_RATE = '"+parm.getValue("DISCOUNT_RATE")+"',OPT_DATE = sysdate," +
				" OPT_USER = '"+parm.getValue("OPT_USER")+"', OPT_TERM = '"+parm.getValue("OPT_TERM")+"' " +
				" WHERE CTZ_CODE = '"+parm.getValue("CTZ_CODE")+"' AND ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"'";
		//System.out.println("��ϸ���޸�sql="+sql);
		return sql;
	}
	
	/**
	 * �޸�sql-��������
	 */
	private String getMainUpateSql(TParm parm) {
		String sql = "UPDATE SYS_CHARGE_DETAIL SET DISCOUNT_RATE = '"+parm.getValue("DISCOUNT_RATE")+"',OPT_DATE = sysdate," +
		" OPT_USER = '"+parm.getValue("OPT_USER")+"', OPT_TERM = '"+parm.getValue("OPT_TERM")+"' " +
		" WHERE CTZ_CODE = '"+parm.getValue("CTZ_CODE")+"' AND CHARGE_HOSP_CODE = '"+parm.getValue("CHARGE_HOSP_CODE")+"'";
		//System.out.println("������޸�sql="+sql);
		return sql;
	}
}
