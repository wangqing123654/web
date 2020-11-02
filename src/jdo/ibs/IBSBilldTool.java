package jdo.ibs;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.jdo.TJDODBTool;
/**
 *
 * <p>Title: סԺ������ϸ��������</p>
 *
 * <p>Description: סԺ������ϸ��������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class IBSBilldTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static IBSBilldTool instanceObject;
    /**
     * �õ�ʵ��
     * @return IBSBilldTool
     */
    public static IBSBilldTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IBSBilldTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public IBSBilldTool() {
        setModuleName("ibs\\IBSBilldModule.x");
        onInit();
    }
    /**
     * ����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertdata(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertDdata",parm, connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ��
     * @param parm TParm
     * @return TParm
     */
    public TParm deletedata(TParm parm){
        TParm result = new TParm();
        result = update("deletedata",parm);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ�˵���ϸ����������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "IBSBilldTool.selectAllData()�����쳣!");
            return result;
        }
        result = query("selectAllData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }
    /**
     * ��ѯ�˵�����(�ɷ���ҵ)
     * @param parm TParm
     * @return TParm
     */
    public TParm selDataForCharge(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "IBSBilldTool.selDataForCharge()�����쳣!");
            return result;
        }
        result = query("selDataForCharge", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }
    /**
     * �����˵�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataDate(TParm parm, TConnection connection) {
    TParm result = new TParm();
    String sql =
        " UPDATE IBS_BILLD SET REFUND_FLG = 'Y',"+
        "                      REFUND_BILL_NO='"+parm.getValue("REFUND_BILL_NO")+"',"+
        "                      REFUND_CODE='"+parm.getValue("REFUND_CODE")+"',"+
        "                      REFUND_DATE = SYSDATE "+
        "  WHERE BILL_NO = '"+parm.getValue("BILL_NO")+"' "+
        "    AND REFUND_FLG <> 'Y'";
//    System.out.println("�����˵�"+sql);
    result = new TParm(TJDODBTool.getInstance().update(sql, connection));
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
    
    /**
	 * �޸����
	 * 
	 * @author caowl
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updataDateforCTZ(TParm parm, TConnection connection) {

		TParm result = new TParm();

		String sql = "UPDATE  IBS_BILLD SET REFUND_FLG = 'Y' "
				+ ",REFUND_DATE =SYSDATE " 
				+ ",REFUND_CODE = '"+parm.getValue("REFUND_CODE")+"' ,REFUND_BILL_NO = '"+parm.getValue("REFUND_BILL_NO")+"' "
				+ "WHERE BILL_NO ='" + parm.getData("BILL_NO") + "'";

		//System.out.println("sql--->"+sql);
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

}
