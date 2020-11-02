package jdo.ekt;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class EKTNewTool extends TJDOTool{
	/**
	 * ������
	 */
	public EKTNewTool() {
		setModuleName("ekt\\EKTNewModule.x");
		onInit();
	}

	/**
	 * ʵ��
	 */
	private static EKTNewTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return EKTTool
	 */
	public static EKTNewTool getInstance() {
		if (instanceObject == null)
			instanceObject = new EKTNewTool();
		return instanceObject;
	}
	/**
	 * ��ѯ�˲������շ�δ��Ʊ���������ݻ��ܽ��
	 * @return
	 */
	public TParm selectEktTrade(TParm parm){
		String sql="SELECT TRADE_NO,CARD_NO,CARD_NO, MR_NO, CASE_NO, PAT_NAME, "+
                    "OLD_AMT, AMT, STATE,BUSINESS_TYPE,GREEN_BALANCE,GREEN_BUSINESS_AMT, RESET_TRADE_NO,OPT_USER, OPT_DATE, "+
                    "OPT_TERM FROM EKT_TRADE "+
                   "WHERE CASE_NO="+parm.getValue("CASE_NO")+" AND STATE='1' "+
              	          "AND BUSINESS_TYPE IN ("+parm.getValue("EKT_TRADE_TYPE")+")";  
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	/**
	 * ��Ʊ���� ��ѯ���д˴β����Ľ��
	 * @return
	 */
	public TParm selectEktSumTrade(TParm parm){
		String sql = "SELECT SUM(AMT) AS AMT, SUM(GREEN_BUSINESS_AMT) AS GREEN_BUSINESS_AMT FROM EKT_TRADE "
				+"WHERE CASE_NO='" + parm.getValue("CASE_NO") + "' AND TRADE_NO IN ("+ parm.getValue("TRADE_SUM_NO")
				+ ") ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	/**
	 * ����Ʊ���� �۳�ҽ�ƿ�����ѯ�˲������վݺ������еĲ�ͬ���ڲ����׺�
	 * @return
	 */
	public TParm selectBusinessByReceiptNo(TParm parm){
		return	query("selectBusinessByReceiptNo", parm);	
	}
	
	/**
	 * ҽ��վ����ɾ�����ݲ�ѯ ͨ������ǩ��ѯ����Ҫ������ҽ��
	 * @param parm
	 * @return
	 */
	public TParm selectOpdRxNo(TParm parm){
		return	query("selectOpdRxNo", parm);	
	}
	/**
	 * �ۿ�帺���ݲ�ѯ
	 * @param parm
	 * @return
	 */
	public TParm selectTradeNo(TParm parm) {
		if(parm.getValue("TRADE_SUM_NO").length()<=0){
			TParm result=new TParm();
			result.setCount(-1);
			return result;
		}
		String sql = "SELECT TRADE_NO,CARD_NO,CARD_NO, MR_NO, CASE_NO, PAT_NAME, "
				+ "OLD_AMT, AMT, STATE,BUSINESS_TYPE,GREEN_BALANCE,GREEN_BUSINESS_AMT,RESET_TRADE_NO, OPT_USER, OPT_DATE, "
				+ "OPT_TERM, PAY_OTHER3, PAY_OTHER4 FROM EKT_TRADE "
				+ "WHERE CASE_NO='"
				+ parm.getValue("CASE_NO")
				+ "' AND TRADE_NO IN ("+parm.getValue("TRADE_SUM_NO")+") ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	/**
	 * ��ѯ�˾��ﲡ���������ݻ��ܽ��
	 * @return
	 */
	public TParm selectSumOrderUnBillFlg(TParm parm){
		return	query("selectSumOrderUnBillFlg", parm);	
	}
	/**
	 * ��ѯ�˴β����շѵ�������Ҫ�˻����
	 * @return
	 */
	public TParm selectEktTradeUnSum(TParm parm){
		return	query("selectEktTradeUnSum", parm);	
	}
	
	/**
	 * �������
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertEktTrade(TParm parm,TConnection connection){
		return update("insertEktTrade", parm, connection);
	}
	/**
	 * ɾ������
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteEktTrade(TParm parm,TConnection connection){
		return update("deleteEktTrade", parm, connection);
	}
	/**
	 * ���������޸�״̬
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm updateOpdEktTrade(TParm parm,TConnection connection){
		return update("updateOpdEktTrade", parm, connection);
	}
	/**
	 * ҽ���˷Ѳ��� ���޸�OPD_ORDER �����ڲ����׺���
	 */
	public TParm updateOpdOrderBusinessNo(TParm parm,TConnection connection){
		return update("updateOpdOrderBusinessNo", parm, connection);
	}
	
	/**
	 * �޸����Ʊ��
	 * @return
	 */
	public TParm updateEktTradeByPrintNo(TParm parm,TConnection connection){
		return update("updateEktTreadeByPrintNo", parm, connection);
	}
	/**
	 * ����ҽ�ƿ��������
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateEKTMaster(TParm parm, TConnection connection) {
		TParm result = update("updateEKTMaster", parm, connection);
		return result;
	}
	/**
	 * ���ҽ�ƿ��������
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertEKTMaster(TParm parm, TConnection connection) {
		TParm result = update("insertEKTMaster", parm, connection);
		return result;
	}
	/**
	 * ɾ��ҽ�ƿ��������
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm deleteEKTMaster(TParm parm, TConnection connection) {
		TParm result = update("deleteEKTMaster", parm, connection);
		return result;
	}
}
