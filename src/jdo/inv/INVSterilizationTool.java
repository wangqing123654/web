package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * 
 * <p>
 * Title: ��� ��� Tool
 * </p>
 * 
 * <p>
 * Description: ��� ��� Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wangzl 2013-04-27
 * @version 1.0
 */
public class INVSterilizationTool extends TJDODBTool {
	/** ʵ�� */
	private static INVSterilizationTool instance;

	/** �õ�ʵ�� */
	public static INVSterilizationTool getInstance() {
		if (instance == null)
			return instance = new INVSterilizationTool();
		return instance;
	}
	/**
     * ������������¼
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertValue(TParm parm, TConnection connection) {
    	String sterilization_date=parm.getValue("STERILIZATION_DATE");//�������
    	sterilization_date = sterilization_date.substring(0, 4)
				+ sterilization_date.substring(5, 7) + sterilization_date.substring(8, 10)
				+ sterilization_date.substring(11, 13)
				+ sterilization_date.substring(14, 16)
				+ sterilization_date.substring(17, 19);
    	
    	String sterilization_valid_date=parm.getValue("STERILIZATION_VALID_DATE");//���Ч��
    	String sterilizationValidDateSql=null;
    	if(null!=sterilization_valid_date && sterilization_valid_date.length()!=0)
    		sterilizationValidDateSql="TO_DATE('"+sterilization_valid_date.substring(0, sterilization_valid_date.indexOf("."))+"','YYYY-MM-DD HH24:MI:SS')";
    	
    	String pack_date=parm.getValue("PACK_DATE");//�������
    	String packDateSql=null;
    	if(null!=pack_date && pack_date.length()!=0)
    		packDateSql="TO_DATE('"+pack_date.substring(0, pack_date.indexOf("."))+"','YYYY-MM-DD HH24:MI:SS')";
    	
    	String sql="INSERT INTO INV_DISINFECTION (ORG_CODE,PACK_CODE,PACK_SEQ_NO,STERILIZATION_DATE,STERILIZATION_VALID_DATE," +
    											  "QTY,STERILIZATION_POTSEQ,STERILIZATION_PROGRAM,STERILIZATION_OPERATIONSTAFF," +
    											  "STERILIZATION_USER,PACK_DATE,OPT_USER,OPT_DATE,OPT_TERM)" +
    					"VALUES (" 
    						   +"'"+parm.getData("ORG_CODE")+"'," 
    						   +"'"+parm.getData("PACK_CODE")+"'," 
    						   +""+parm.getInt("PACK_SEQ_NO")+"," 
    						   +"'"+sterilization_date+"'," 
    						   +""+sterilizationValidDateSql+"," 
    						   +""+parm.getInt("QTY")+"," 
    						   +"'"+parm.getData("STERILIZATION_POTSEQ")+"'," 
    						   +"'"+parm.getData("STERILIZATION_PROGRAM")+"',"
    						   +"'"+parm.getData("STERILIZATION_OPERATIONSTAFF")+"'," 
    						   +"'"+parm.getData("STERILIZATION_USER")+"'," 
    						   +""+packDateSql+"," 
    						   +"'"+parm.getData("OPT_USER")+"'," 
    						   +"TO_DATE('"+parm.getValue("OPT_DATE").substring(0, parm.getValue("OPT_DATE").indexOf("."))+"','YYYY-MM-DD HH24:MI:SS')," 
    						   +"'"+parm.getData("OPT_TERM")+"'" 
    						   +")";
    	TParm result=new TParm(TJDODBTool.getInstance().update(sql,connection));
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }
    /**ɾ����������¼*/
    public TParm deleteValue(TParm parm,TConnection connection){
    	String sql="DELETE INV_DISINFECTION " 
    			   +"WHERE ORG_CODE='"+parm.getData("ORG_CODE")+"' " 
    			   	 +"AND PACK_CODE='"+parm.getData("PACK_CODE")+"' " 
    			   	 +"AND PACK_SEQ_NO="+parm.getInt("PACK_SEQ_NO")+" " 
    			   	 +"AND STERILIZATION_DATE='"+parm.getData("STERILIZATION_DATE")+"'";
    	TParm result=new TParm(TJDODBTool.getInstance().update(sql,connection));
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
}
