package jdo.hrm;

import java.util.HashMap;
import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title: ���������˱�������</p>
 * 
 * <p>Description: ���������˱�������</p>
 * 
 * <p>Copyright: javahis 20090922</p>
 * 
 * <p>Company:JavaHis</p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMCompanyReportTool extends TJDOTool {
	/**
     * ʵ��
     */
    public static HRMCompanyReportTool instanceObject;
    /**
     * �õ�ʵ��
     * @return HRMPersonReportTool
     */
    public static HRMCompanyReportTool getInstance()
    {
        if(instanceObject == null){
        	instanceObject = new HRMCompanyReportTool();
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
		Map inMap=(HashMap)parm.getData("IN_MAP");
		String[] sql=(String[])inMap.get("SQL");
//		// System.out.println("HRMCompanyReportTool.sql.length:"+sql.length);
		if(sql==null){
			return result;
		}
		if(sql.length<1){
			return result;
		}
		for(String tempSql:sql){
//			// System.out.println("HRMCompanyReportTool.sql=="+tempSql);
			result=new TParm(TJDODBTool.getInstance().update(tempSql, conn));
			if(result.getErrCode()!=0){
				return result;
			}
		}
		return result;
	
    }

    /**
     * ���½��첡���ĵ绰(HRM_CONTRACTD��) add by wanglong 20130110
     * @param mrNo
     * @return
     */
    public TParm updateTELFromHRMContractd(String tel, String mrNo, TConnection conn) {
        String sql = "UPDATE HRM_CONTRACTD SET TEL = '" + tel + "' WHERE MR_NO = '" + mrNo + "'";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        return result;
    }
    
    /**
     * ���½��첡���ĵ绰(HRM_PATADM��) add by wanglong 20130110
     * @param caseNo
     * @return
     */
    public TParm updateTELFromHRMPatAdm(String tel, String caseNo, TConnection conn) {
        String sql = "UPDATE HRM_PATADM SET TEL = '" + tel + "' WHERE CASE_NO = '" + caseNo + "'";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        return result;
    }
    
    /**
     * ���½��첡���ĵ绰(SYS_PATINFO��) add by wanglong 20130110
     * @param mrNo
     * @return
     */
    public TParm updateTELFromSYSPatInfo(String tel, String mrNo, TConnection conn) {
        String sql = "UPDATE SYS_PATINFO SET TEL_HOME = '" + tel + "' WHERE MR_NO = '" + mrNo + "'";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        return result;
    }
    
}
