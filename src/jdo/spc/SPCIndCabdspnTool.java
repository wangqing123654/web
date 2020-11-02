package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class SPCIndCabdspnTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static SPCIndCabdspnTool instanceObject;
    /**
     * �õ�ʵ��
     * @return ClinicRoomTool
     */
    public static SPCIndCabdspnTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCIndCabdspnTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SPCIndCabdspnTool() {
        setModuleName("spc\\SPCIndCabdspnModule.x");
        onInit();
    }
    
    
    /**
     * ����IND_CABDSPN
     * @param parm
     * @return
     */
    public TParm insert(TParm parm,TConnection conn){
        TParm result = new TParm();
        result = update("insert", parm,conn);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result ;
    }
    
    /**
     * ����������ѯ
     * @param parm
     * @return
     */
    public TParm query(TParm parm){
    	TParm result = new TParm();
        result = query("query", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result ;
    }
    
    /**
     * ɾ�� ����IND_CABDSPN�ĸ�����
     * @param parm
     * @param conn
     * @return
     */
    public TParm delete(TParm parm ,TConnection conn){
    	TParm result = new TParm();
        result = update("delete", parm,conn);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result ;
    	
    }
    
    /**
     * ��ѯ�Ƿ���ҩ
     * @param parm
     * @return
     */
    public TParm onQueryIsDrug(TParm parm) {
    	String sql = " SELECT A.ORDER_CODE FROM ODI_DSPNM A "+
    				 " WHERE ( A.CTRLDRUGCLASS_CODE IS NULL OR "+
					 "							A.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE FROM SYS_CTRLDRUGCLASS WHERE CTRL_FLG='Y'))    " +
					 " AND A.ORDER_CODE='"+parm.getValue("ORDER_CODE")+"' ";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    
    public TParm onQueryIsNotDrug(TParm parm) {
    	String sql = " SELECT A.ORDER_CODE FROM ODI_DSPNM A "+
		 " WHERE  ( A.CTRLDRUGCLASS_CODE IS NOT NULL OR "+
		 "							A.CTRLDRUGCLASS_CODE  IN (SELECT CTRLDRUGCLASS_CODE FROM SYS_CTRLDRUGCLASS WHERE CTRL_FLG='Y'))    " +
		 " AND A.ORDER_CODE='"+parm.getValue("ORDER_CODE")+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
    }
}
