package action.adm;

import java.sql.Timestamp;

import com.dongyang.data.TParm;
//import com.dongyang.db.TConnection;
//import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;

/**
 * 病历批次
 * @author li.xiang
 *
 */
public class ADMEmrLockBatch extends Patch{
	
	/**
	 * 
	 */
	public ADMEmrLockBatch() {
		//
		
    }
	
	/**
	 * 
	 */
	public boolean run() {
       // TConnection connection = TDBPoolManager.getInstance().getConnection();
        //1.
        //TParm sql = new TParm();
       // Timestamp date = StringTool.getTimestamp(new Date());
        //Timestamp yesterday = StringTool.rollDate(date, -1);
        //sql.setData("STADATE", StringTool.getString(yesterday, "yyyyMMdd"));
        //String yesterDay=StringTool.getString(yesterday, "yyyyMMdd")+"23:59:59";
        
        String strSQL="select (sysdate - interval '8' day) lockEmrDate from dual";
        //
        //System.out.println("111==yesterday===111"+StringTool.getString(yesterday, "yyyyMMdd HH:mm:ss"));
        //String strSQL="";
        //2.
        //查询在院病患
        TParm parm = new TParm(TJDODBTool.getInstance().select(strSQL));
        
        //System.out.println("1111"+parm);
        //
        Timestamp lockEmrDate=(Timestamp)parm.getData("LOCKEMRDATE", 0);
        //
        //System.out.println("=====lockEmrDate1111======="+lockEmrDate);
        //
        System.out.println("=====lockEmrDate======="+StringTool.getString(lockEmrDate, "yyyyMMdd"));
        //
        String lockStartDate= StringTool.getString(lockEmrDate, "yyyyMMdd")+"000000";
        //
        String lockEndDate= StringTool.getString(lockEmrDate, "yyyyMMdd")+"235959";
        
      	/*
		 * SELECT case_no FROM adm_inp WHERE DS_DATE BETWEEN TO_DATE
		 * ('20180906000000', 'YYYYMMDDHH24MISS') AND TO_DATE ('20180906235959',
		 * 'YYYYMMDDHH24MISS')
		 */
        //
		String querySQL = "SELECT CASE_NO FROM adm_inp WHERE DS_DATE BETWEEN TO_DATE ('" + lockStartDate
				+ "', 'YYYYMMDDHH24MISS') AND TO_DATE ('" + lockEndDate + "', 'YYYYMMDDHH24MISS')";
		
		 System.out.println("=====222querySQL2222======="+querySQL);
		//
		// StringTool.getString(lockEmrDate, "yyyyMMdd");//06 号
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(querySQL));
		//
		try {
			for (int i = 0; i < parm1.getCount("CASE_NO"); i++) {
				if (upateSingleData(parm1, i)) { //

					//
				}else{
					continue;
				}
			}
			//
		} catch (Exception e) {
			e.printStackTrace();
		}
        //
        return true;
	}
	
	
	/**
	 * 更新病历锁定状态
	 * @param parm
	 * @param row
	 * @return
	 */
    public boolean upateSingleData(TParm parm, int row) {
    	//
    	String caseNo = parm.getValue("CASE_NO", row);
    	System.out.println("=====2222caseNo2222======="+caseNo);
    	//
    	TParm saveParm = new TParm(
    			TJDODBTool.getInstance().update("UPDATE ADM_INP SET EMR_LOCK_FLG='Y' WHERE CASE_NO='" + caseNo + "'"));
    	//
    	if (saveParm.getErrCode() != 0) {
			return false;
		} 
    	return true;
    }

}
