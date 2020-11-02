package action.opb;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;

public class OPBArrearageBatch extends Patch{
	
	public OPBArrearageBatch(){
		
	}
	
    /**
     * 批次线程
     * @return boolean
     */
    public boolean run() {
    	
    	TConnection connection = TDBPoolManager.getInstance().getConnection();
    	
    	Timestamp today = SystemTool.getInstance().getDate();
    	
    	String t_1 = StringTool.rollDate(today, -1).toString().substring(0, 10).replace("-", "");
    	String t_2 = StringTool.rollDate(today, -2).toString().substring(0, 10).replace("-", "");
    	
    	String sql =
    		" SELECT DISTINCT MR_NO" +
    		" FROM OPD_ORDER" +
    		" WHERE     EXEC_FLG = 'Y'" +
    		" AND BILL_FLG = 'N'" +
    		" AND MEM_PACKAGE_ID IS NULL" +
    		" AND ADM_TYPE = 'O'" +
    		" AND AR_AMT > 0" +
    		" AND ORDER_DATE BETWEEN TO_DATE ('" + t_1 + "000000', 'YYYYMMDDHH24MISS')" +
    		" AND TO_DATE ('" + t_1 + "235959', 'YYYYMMDDHH24MISS')" +
    		" UNION" +
    		" SELECT DISTINCT MR_NO" +
    		" FROM OPD_ORDER" +
    		" WHERE     EXEC_FLG = 'Y'" +
    		" AND BILL_FLG = 'N'" +
    		" AND MEM_PACKAGE_ID IS NULL" +
    		" AND ADM_TYPE = 'E'" +
    		" AND AR_AMT > 0" +
    		" AND ORDER_DATE BETWEEN TO_DATE ('" + t_2 + "000000', 'YYYYMMDDHH24MISS')" +
    		" AND TO_DATE ('" + t_2 + "235959', 'YYYYMMDDHH24MISS')";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	
    	String mrNo;
    	TParm result;
    	for (int i = 0; i < parm.getCount(); i++) {
    		
    		mrNo = parm.getValue("MR_NO", i);
    		
    		result = arreage(mrNo, connection);
    		
    		if(result.getErrCode() < 0){
    			connection.rollback();
    			connection.close();
    			return false;
    		}
		}
    	
    	sql =
    		"SELECT MR_NO FROM SYS_PATINFO WHERE OPB_ARREAGRAGE = 'Y'";
    	parm = new TParm(TJDODBTool.getInstance().select(sql));
    	
    	for (int i = 0; i < parm.getCount(); i++) {
    		mrNo = parm.getValue("MR_NO", i);
    		result = deArreage(mrNo, connection);
		}
    	
    	connection.commit();
    	connection.close();
		return true;
    }
    
    private TParm arreage(String mrNo, TConnection connection){
    	String sql = 
    		" UPDATE SYS_PATINFO" +
    		" SET OPB_ARREAGRAGE = 'Y'" +
    		" WHERE MR_NO = '" + mrNo + "'";
    	TParm parm = new TParm(TJDODBTool.getInstance().update(sql, connection));
    	return parm;
    }
    
    private TParm deArreage(String mrNo, TConnection connection){
    	
    	String sql = 
    		" SELECT COUNT(*) COUNT,SUM(AR_AMT) OWE_AMT" +
    		" FROM OPD_ORDER" +
    		" WHERE     EXEC_FLG = 'Y'" +
    		" AND BILL_FLG = 'N'" +
    		" AND MEM_PACKAGE_ID IS NULL" +
    		" AND AR_AMT > 0" +
    		" AND MR_NO = '" + mrNo + "' GROUP BY CASE_NO";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	
		if (parm.getInt("COUNT", 0) > 0) {
			sql = " UPDATE SYS_PATINFO" + " SET OWE_AMT = "+parm.getDouble("OWE_AMT",0)+" "
			+ " WHERE MR_NO = '" + mrNo + "'";
			parm = new TParm(TJDODBTool.getInstance().update(sql, connection));
			return parm;
		} else {
			sql = " UPDATE SYS_PATINFO" + " SET OPB_ARREAGRAGE = 'N' , OWE_AMT=0"
					+ " WHERE MR_NO = '" + mrNo + "'";
			parm = new TParm(TJDODBTool.getInstance().update(sql, connection));
			return parm;
		}
    }
    
}
