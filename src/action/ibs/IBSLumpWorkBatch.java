package action.ibs;

import java.sql.Timestamp;

import jdo.bil.BIL;
import jdo.ibs.IBSLumpWorkBatchNewTool;
import jdo.ibs.IBSLumpWorkBatchTool;
import jdo.ibs.IBSOrdmTool;
import jdo.ibs.IBSTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.patch.Patch;

/**
*
* <p>Title: 住院套餐夜间批次</p>
*
* <p>Description: 自动区分套内套外</p>
*
* <p>Copyright: Copyright (c) Liu dongyang 2015</p>
*
* <p>Company: bluecore</p>
*
* @author pangben 
* @version 1.0
*/
public class IBSLumpWorkBatch  extends Patch{

	 /**
     * 批次线程
     * @return boolean
     */
    public boolean run() {
    	TParm admParm=getAdmInpInfo();
    	if (admParm.getCount()<=0) {
    		this.setMessage("没有需要操作的数据");
    		return false;
		}
    	TParm result=null;
    	TConnection connection= TDBPoolManager.getInstance().getConnection();
    	for (int i = 0; i < admParm.getCount("CASE_NO"); i++) {
    		result=IBSLumpWorkBatchNewTool.getInstance().onLumpWorkBatch(admParm.getRow(i), connection);
        	if (result.getErrCode()<0) {
        		System.out.println("套餐批次出现问题::"+admParm.getRow(i).getValue("CASE_NO")+"::"+result.getErrText());
    			//this.setMessage(result.getErrText());
    			connection.rollback();
    			continue;
    		}
           	connection.commit();
        	result = IBSTool.getInstance().updateAdmTotAmt(admParm.getRow(i));
    		if (result.getErrCode() < 0) {
    			//connection.rollback();
    			continue;
    		}
		}
    	connection.close();
    	return true;
    }
    /**
     * 
    * @Title: getAdmInpInfo
    * @Description: TODO(查询存在未处理医嘱的在院套餐病人)
    * @author pangben
    * @return
    * @throws
     */
    public TParm getAdmInpInfo(){
    	String sql="SELECT A.CASE_NO,A.LUMPWORK_CODE,A.MR_NO,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.M_CASE_NO,A.NEW_BORN_FLG,A.LUMPWORK_RATE,A.SERVICE_LEVEL " +
    			"FROM ADM_INP A,IBS_ORDD B,SYS_FEE C WHERE A.CASE_NO=B.CASE_NO AND B.ORDER_CODE=C.ORDER_CODE" +
    			" AND A.LUMPWORK_CODE IS NOT NULL AND C.CAT1_TYPE<>'PHA' AND C.CHARGE_HOSP_CODE<>'RA' AND A.DS_DATE IS NULL " +
    			" AND B.INCLUDE_EXEC_FLG='N' AND A.CANCEL_FLG ='N' " +
    			"GROUP BY A.CASE_NO,A.LUMPWORK_CODE,A.MR_NO,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.M_CASE_NO," +
    			"A.NEW_BORN_FLG,A.LUMPWORK_RATE,A.SERVICE_LEVEL";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
}
