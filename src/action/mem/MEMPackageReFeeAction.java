package action.mem;

import java.util.List;

import jdo.mem.MEMTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: �ײ��˷�
 * </p>
 * 
 * <p>
 * Description: �ײ��˷�
 * </p>
 * 
 * <p>onSave
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author duzhw 2014.03.05
 * @version 4.5
 */
public class MEMPackageReFeeAction extends TAction {
	
	String updateTradeNo = "";	//��ǰ�˷ѽ��׺�
	
	/**
	 * ִ�б�������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm) {
		TParm result = new TParm();
		TConnection conn = getConnection();
		TParm payCashParm=parm.getParm("payCashParm");//΢��֧�������׺ű���
		boolean isDedug=true; //add by huangtt 20160505 ��־���
		try {
			//ҽ�ƿ��ۿ�
			if(parm.getData("ektSql") != null){
				List<String> ektSql = (List<String>) parm.getData("ektSql");
				for (int i = 0; i < ektSql.size(); i++) {
					result = new TParm(TJDODBTool.getInstance().update(ektSql.get(i), conn));
					if (result.getErrCode() < 0) {
						if(isDedug){  
							System.out.println(" come in class:MEMPackageSalesTimeSelAction.class ��method ��onSave");
							System.out.println("�������ݣ�"+result);
						}
						conn.rollback();
						conn.close();
						return result;
					}
				}
			}
			/**
			//ɾ���ײ�����
			for (int i = 0; i < parm.getCount(); i++) {
				//1-ɾ��ʱ��ϸ������
				TParm ddparm=parm.getRow(i);
				String ddSql=this.getDelDetailSql(ddparm);
				result = new TParm(TJDODBTool.getInstance().update(ddSql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
				//2-ɾ��ʱ��������
				TParm dsparm=parm.getRow(i);
				String dsSql=this.getDelSectionSql(dsparm);
				result = new TParm(TJDODBTool.getInstance().update(dsSql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}  **/
			//1-���뽻�ױ�-�˷Ѽ�¼
			TParm tradeParm = parm.getParm("TRADEDATA");
			TParm iparm=tradeParm.getRow(0);
			updateTradeNo = iparm.getValue("TRADE_NO");
			String insertTradeSql = getInsertTradeSql(iparm);
			
			result = new TParm(TJDODBTool.getInstance().update(insertTradeSql, conn));
			if (result.getErrCode() < 0) {
				if(isDedug){  
					System.out.println(" come in class: MEMPackageReFeeAction ��method ��onSave");
					System.out.println("ERRor��"+result);
				}
				conn.rollback();
				conn.close();
				return result;
			}
			//΢��֧�������潻�׺���
	    	if(null!=payCashParm){
	    		TParm tempParm=new TParm();
	    		tempParm.setData("TRADE_NO",updateTradeNo);
	    		if(null!=payCashParm.getValue("WX")&&payCashParm.getValue("WX").length()>0){
	    			tempParm.setData("WX_BUSINESS_NO",payCashParm.getValue("WX"));
				}else{
					tempParm.setData("WX_BUSINESS_NO","");
				}
				if(null!=payCashParm.getValue("ZFB")&&payCashParm.getValue("ZFB").length()>0){
					tempParm.setData("ZFB_BUSINESS_NO",payCashParm.getValue("ZFB"));
				}else{
					tempParm.setData("ZFB_BUSINESS_NO","");
				}
	    		result = MEMTool.getInstance().updateCashTypeMemPackTradeMBusinessNo(tempParm, conn);
	        	if (result.getErrCode() < 0) {
	        		conn.rollback();
	        		conn.close();
	    			return result;
	    		}
	    	}
			//2-�޸�ʱ�̱�rest_trade_noΪ��ǰ�˷ѽ��׺�
			TParm sectionParm = parm.getParm("SECTIONDATA");
			for (int m = 0; m < sectionParm.getCount("TRADE_NO"); m++) {
				TParm uparm=sectionParm.getRow(m);
				String uSql=this.updateSecPackageSql(uparm);
				result = new TParm(TJDODBTool.getInstance().update(uSql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
				//3.�޸�ʱ��ҽ����rest_trade_noΪ��ǰ�˷ѽ��׺� modify by huangjw 20160520
				String orderDetailSql = updateOrderPackageSql(uparm);
				result = new TParm(TJDODBTool.getInstance().update(orderDetailSql, conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, e.getMessage());
			err(e.getMessage());
		}
		
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * ��ȡ���뽻�ױ��˷Ѽ�¼sql
	 */
	public String getInsertTradeSql(TParm parm) {
		String sql = "INSERT INTO MEM_PACKAGE_TRADE_M(TRADE_NO,MR_NO,ORIGINAL_PRICE,RETAIL_PRICE,AR_AMT," +
				"OPT_USER,CASHIER_CODE,OPT_DATE,BILL_DATE,OPT_TERM,REST_FLAG" +
				" ,PAY_TYPE01, PAY_TYPE02, PAY_TYPE03, PAY_TYPE04, PAY_TYPE05, PAY_TYPE06, " +
				" PAY_TYPE07, PAY_TYPE08, PAY_TYPE09, PAY_TYPE10" +
				" ,MEMO1, MEMO2, MEMO3, MEMO4, MEMO5, MEMO6, MEMO7, MEMO8, MEMO9, MEMO10, CARD_TYPE,DESCRIPTION" +
				" ,PAY_TYPE11,MEMO11,BILL_TYPE,PAY_MEDICAL_CARD" +
				") VALUES(" +
				"'"+parm.getValue("TRADE_NO")+"','"+parm.getValue("MR_NO")+"'," +
				"'"+parm.getValue("ORIGINAL_PRICE")+"','"+parm.getValue("RETAIL_PRICE")+"'," +
				"'"+parm.getValue("AR_AMT")+"','"+parm.getValue("OPT_USER")+"','" +parm.getValue("OPT_USER")+"'," +
				"sysdate,sysdate,'"+parm.getValue("OPT_TERM")+"','"+parm.getValue("REST_FLAG")+"'" +
				//add by huangtt 20141226 ����֧����ʽ
				" ,'"+parm.getValue("PAY_TYPE01")+"','"+parm.getValue("PAY_TYPE02")+"'" +
				" ,'"+parm.getValue("PAY_TYPE03")+"','"+parm.getValue("PAY_TYPE04")+"'" +
				" ,'"+parm.getValue("PAY_TYPE05")+"','"+parm.getValue("PAY_TYPE06")+"'" +
				" ,'"+parm.getValue("PAY_TYPE07")+"','"+parm.getValue("PAY_TYPE08")+"'" +
				" ,'"+parm.getValue("PAY_TYPE09")+"','"+parm.getValue("PAY_TYPE10")+"'" +
				" ,'"+parm.getValue("MEMO1")+"','"+parm.getValue("MEMO2")+"'" +
				" ,'"+parm.getValue("MEMO3")+"','"+parm.getValue("MEMO4")+"'" +
				" ,'"+parm.getValue("MEMO5")+"','"+parm.getValue("MEMO6")+"'" +
				" ,'"+parm.getValue("MEMO7")+"','"+parm.getValue("MEMO8")+"'" +
				" ,'"+parm.getValue("MEMO9")+"','"+parm.getValue("MEMO10")+"'" +
				" ,'"+parm.getValue("CARD_TYPE")+"'" +
				" ,'"+parm.getValue("DESCRIPTION")+"'"+
				",'"+parm.getValue("PAY_TYPE11")+"','"+parm.getValue("MEMO11")+"'"+
				",'"+parm.getValue("BILL_TYPE")+"','"+parm.getValue("PAY_MEDICAL_CARD")+"'" +
				")";
//		System.out.println("��ȡ���뽻�ױ��˷Ѽ�¼sql="+sql);
		return sql;
	}
	/**
	 * ��ȡ�޸�ʱ���ײͱ�rest_trade_no�ֶ�Ϊ��ǰ�˷ѽ��׺�
	 */
	public String updateSecPackageSql(TParm parm) {
		String sql = "UPDATE MEM_PAT_PACKAGE_SECTION SET REST_TRADE_NO = '"+updateTradeNo+"' " +
			" WHERE TRADE_NO = '"+parm.getValue("TRADE_NO")+"' AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE")+"' " +
			" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE")+"' AND MR_NO = '"+parm.getValue("MR_NO")+"'";
		return sql;
	}
	
	/**
	 * ��ȡ�޸��ײ�ʱ��ҽ����rest_trade_no�ֶ�Ϊ��ǰ�˷ѽ��׺�
	 * @param parm
	 * @return
	 */
	public String updateOrderPackageSql(TParm parm){
		String sql = "UPDATE MEM_PAT_PACKAGE_SECTION_D SET REST_TRADE_NO = '"+updateTradeNo+"' " +
		" WHERE TRADE_NO = '"+parm.getValue("TRADE_NO")+"' AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE")+"' " +
		" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE")+"' AND MR_NO = '"+parm.getValue("MR_NO")+"'";
		return sql;
	}
	
	/**
	 * ��ȡɾ��ʱ��������sql
	 
	public String getDelSectionSql(TParm parm){
		String sql = "DELETE FROM MEM_PAT_PACKAGE_SECTION " +
				" WHERE TRADE_NO = '"+parm.getValue("TRADE_NO")+"' AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE")+"' " +
				" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE")+"' AND MR_NO = '"+parm.getValue("MR_NO")+"'";
		System.out.println("sql1="+sql);
		return sql;
	}*/
	
	/**
	 * ��ȡɾ��ʱ��ϸ������sql
	 
	public String getDelDetailSql(TParm parm){
		String sql = "DELETE FROM MEM_PAT_PACKAGE_SECTION_D " +
		" WHERE TRADE_NO = '"+parm.getValue("TRADE_NO")+"' AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE")+"' " +
		" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE")+"' AND MR_NO = '"+parm.getValue("MR_NO")+"'";
		System.out.println("sql2="+sql);
		return sql;
	}*/
}
