package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class SPCSettleAccountsHistoryTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	private static SPCSettleAccountsHistoryTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PatAdmTool
	 */
	public static SPCSettleAccountsHistoryTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCSettleAccountsHistoryTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SPCSettleAccountsHistoryTool() {
		setModuleName("spc\\SPCSettleAccountsModule.x");
		onInit();
	}
	
	public TParm onQuery(TParm parm ){
		TParm result = new TParm();
		
		//Ժ��
		String regionCode = parm.getValue("REGION_CODE");
		
		//����ʱ��
		String closeDate = parm.getValue("CLOSE_DATE");
		
		//����
		String orgCode = parm.getValue("ORG_CODE");
		
		//ҩƷ����
		String orderCode = parm.getValue("ORDER_CODE");
		
		String sql = " SELECT A.CLOSE_DATE,A.ORG_CODE,A.ORDER_CODE,A.LAST_ODD_QTY,A.OUT_QTY,  " +
					 "        A.TOTAL_OUT_QTY,A.TOTAL_UNIT_CODE,A.VERIFYIN_PRICE,A.VERIFYIN_AMT,A.ACCOUNT_QTY, " +
					 "        A.ACCOUNT_UNIT_CODE,A.ODD_QTY,A.ODD_AMT,A.OPT_USER,A.OPT_DATE, " +
					 "        A.OPT_TERM,A.REGION_CODE,A.SUP_CODE,A.CONTRACT_PRICE,A.SUP_ORDER_CODE," +
					 "        B.ORDER_DESC,B.SPECIFICATION "+
					 " FROM IND_ACCOUNT A,PHA_BASE B  "+   
					 " WHERE A.ORDER_CODE=B.ORDER_CODE AND  A.CLOSE_DATE='"+closeDate+"' " ;
		
		if(regionCode != null && !regionCode.equals("")){
			sql += " AND A.REGION_CODE='"+regionCode+"' ";
		}
		
		if(orgCode != null && !orgCode.equals("")){
			sql += " AND A.ORG_CODE='"+orgCode+"' ";
		}
		
		if(orderCode != null && !orderCode.equals("")){
			sql += " AND A.ORDER_CODE='"+orderCode+"' " ;
		}
		
	    result = new TParm(TJDODBTool.getInstance().select(sql));
	    return result  ;
	}
	
}
