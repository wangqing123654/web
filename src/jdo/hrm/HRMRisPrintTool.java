package jdo.hrm;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: ��鵥��ӡJDO
 * </p>
 *
 * <p>Description: ��鵥��ӡJDO</p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangm
 * @version 1.0
 */
public class HRMRisPrintTool extends TJDODBTool {
	/**
     * ʵ��
     */
    public static HRMRisPrintTool instanceObject;

    /**
     * �õ�ʵ��
     * @return HRMRisPrintTool
     */
    public static HRMRisPrintTool getInstance() {
        if (instanceObject == null)
            instanceObject = new HRMRisPrintTool();
        return instanceObject;
    }
    
    
    /**
     * �����ӡ״̬
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm saveHRMRisPrintStat(TParm parm, TConnection con) {
        String sqlStr[] = (String[]) parm.getData("SQL");
        TParm result = new TParm(this.update(sqlStr, con));
        return result;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
