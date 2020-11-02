package jdo.hrm;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
*
* <p>Title: ������鱸ҩ����Tool</p>
*
* <p>Description: ������鱸ҩ����Tool</p>
*
* <p>Copyright: Copyright (c) 2009</p>
*
* <p>Company: javahis</p>
*
* @author Yuanxm 20120907
* @version 1.0
*/
public class HRMDeptRequestTool  extends TJDOTool{
	 /**
     * ʵ��
     */
    public static HRMDeptRequestTool instanceObject;
    /**
     * �õ�ʵ��
     * @return HRMFeePackTool
     */
    public static HRMDeptRequestTool getInstance()
    {
        if(instanceObject == null){
        	instanceObject = new HRMDeptRequestTool();
        }

        return instanceObject;
    }
    
    /**
     * ���ұ�ҩ���ɲ�ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDeptExm(TParm parm) {
    	// ���ݼ��
        if (parm == null)
            return null;
        // �����
        TParm result = new TParm();
        TParm resultM = new TParm();
        TParm resultD = new TParm();
        //����resultM
        if ("Y".equals(parm.getValue("REQUEST_FLG_A"))) {
            resultM = this.onQueryExm(parm);//�����루���ܣ�
                if (resultM.getErrCode() < 0) {
                return resultM;
            }
        }
        else {
            resultM = this.onQueryHRMDeptExmM(parm);//δ���루���ܣ�
            if (resultM.getErrCode() < 0) {
                return resultM;
            }
        }

        resultD = this.onQueryHRMDeptExmD(parm);//��ϸ
        if (resultD.getErrCode() < 0) {
            return resultD;
        }
        result.addData("RESULT_M", resultM.getData());
        result.addData("RESULT_D", resultD.getData());
        return result ;
    }
   
    /**
     * ���ұ�ҩ���ɲ�ѯ    δ���루���ܣ�
     * @param parm
     * @return
     */
    public  TParm onQueryHRMDeptExmM(TParm parm){
    	 
    	String sql = "SELECT 'N' AS SELECT_FLG, A.REQUEST_NO, C.ORDER_CODE, C.ORDER_DESC, "+
         		     " C.SPECIFICATION, SUM (A.DISPENSE_QTY) AS DOSAGE_QTY, D.UNIT_CHN_DESC, "+
         		     " B.STOCK_PRICE, B.STOCK_PRICE * SUM (A.DISPENSE_QTY) AS STOCK_AMT, "+
         		     "A.OWN_PRICE, SUM (A.AR_AMT) AS OWN_AMT,  "+
         		     " SUM (A.AR_AMT) - B.STOCK_PRICE * SUM (A.DISPENSE_QTY) AS DIFF_AMT  "+
    		       "FROM  HRM_ORDER A, PHA_BASE B, SYS_FEE C, SYS_UNIT D  "+
		   		   "WHERE A.ORDER_CODE = B.ORDER_CODE  "+
		     		"	AND A.ORDER_CODE = C.ORDER_CODE  "+
		     		"	AND B.ORDER_CODE = C.ORDER_CODE  "+
		     		"	AND A.ORDER_CAT1_CODE = C.ORDER_CAT1_CODE  "+
		     		"	AND A.DISPENSE_UNIT = D.UNIT_CODE  "+
//		     		"	AND A.BILL_FLG = 'Y'  "+//delete by wanglong 20130308 ��ʱ�޸�
		     			//AND A.RECEIPT_NO IS NOT NULL &
	                "   AND A.REQUEST_NO IS NULL "+//add by wanglong 20130324
		     		"	AND C.CAT1_TYPE = 'PHA'   " ;
			     String groupSql =  "  GROUP BY A.REQUEST_NO,  "+
		         	"	       C.ORDER_CODE,  "+
		         	"	       C.ORDER_DESC,  "+
		         	"	       C.SPECIFICATION,  "+
		         	"	       D.UNIT_CHN_DESC,  "+
		         	"	       A.OWN_PRICE,  "+
		         	"	       B.STOCK_PRICE ";
    	String startDate =parm.getValue("START_DATE");
    	String endDate = parm.getValue("END_DATE");
//    	sql += "	AND A.BILL_DATE BETWEEN TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS')  "+//delete by wanglong 20130308 ��ʱ�޸�
//        "           AND TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS')  ";
    	String appOrgCode = parm.getValue("APP_ORG_CODE");
    	if(appOrgCode != null && !appOrgCode.equals("")){
    		sql += "	AND A.EXEC_DEPT_CODE = '"+appOrgCode+"' ";
    	}
    	
    	String requestNo = parm.getValue("REQUEST_NO");
    	String regionCode = parm.getValue("REGION_CODE");
    	String requestFlg = parm.getValue("REQUEST_FLG");
    	
    	/**
    	if(regionCode != null && !regionCode.equals("")){
    		sql += " AND A.REGION_CODE='"+regionCode+"' ";
    	}*/
    	
    	if(requestNo != null && !requestNo.equals("")){
    		sql += " AND A.REQUEST_NO='"+requestNo+"' ";
    	}
    	
    	if(requestFlg != null && !requestFlg.equals("")){
    		sql += " AND  A.REQUEST_FLG='"+requestFlg+"' ";
    	}
    	
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql+groupSql));
    	return result ;
    }
    
    /**
     * ���ұ�ҩ���ɲ�ѯ  (��ϸ)
     * @param parm
     * @return
     */
    public  TParm onQueryHRMDeptExmD(TParm parm ){
    	String sql = "SELECT 'N' AS SELECT_FLG, D.PAT_NAME, A.BILL_DATE, C.ORDER_CODE, C.ORDER_DESC, "+
    				 "   C.SPECIFICATION, A.DISPENSE_QTY, E.UNIT_CHN_DESC, B.STOCK_PRICE,  "+
    	             "    B.STOCK_PRICE * A.DISPENSE_QTY AS STOCK_AMT, A.OWN_PRICE, A.AR_AMT AS OWN_AMT, "+
       			     "  A.AR_AMT - B.STOCK_PRICE * A.DISPENSE_QTY AS DIFF_AMT, A.CASE_NO,  "+
       			     "  '' AS RX_NO, A.SEQ_NO,A.MR_NO,A.EXEC_DEPT_CODE  "+
  		          " FROM HRM_ORDER A, PHA_BASE B, SYS_FEE C, SYS_PATINFO D, SYS_UNIT E "+
 		          " WHERE A.ORDER_CODE = B.ORDER_CODE "+
   					" AND A.ORDER_CODE = C.ORDER_CODE "+
   			        " AND B.ORDER_CODE = C.ORDER_CODE "+
   			        " AND A.ORDER_CAT1_CODE = C.ORDER_CAT1_CODE "+
   			        " AND A.MR_NO = D.MR_NO "+
   			        " AND A.DISPENSE_UNIT = E.UNIT_CODE "+
//  		            " AND A.BILL_FLG = 'Y' "+//delete by wanglong 20130308 ��ʱ�޸�
  		            " AND C.CAT1_TYPE = 'PHA' ";
    	String startDate =parm.getValue("START_DATE");
    	String endDate = parm.getValue("END_DATE");
//    	sql += "	AND A.BILL_DATE BETWEEN TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS')  "+//delete by wanglong 20130308 ��ʱ�޸�
//        "           AND TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS')  ";
    	String appOrgCode = parm.getValue("APP_ORG_CODE");
    	if(appOrgCode != null && !appOrgCode.equals("")){
    		sql += "	AND A.EXEC_DEPT_CODE = '"+appOrgCode+"' ";
    	}
    	String requestNo = parm.getValue("REQUEST_NO");
    	String regionCode = parm.getValue("REGION_CODE");
    	String requestFlg = parm.getValue("REQUEST_FLG");
    	
    	if(regionCode != null && !regionCode.equals("")){
    		sql += " AND B.REGION_CODE='"+regionCode+"' ";
    	}
    	
    	if(requestNo != null && !requestNo.equals("")){
    		sql += " AND A.REQUEST_NO='"+requestNo+"' ";
        } else {// add by wanglong 20130402
            sql += " AND A.REQUEST_NO IS NULL ";
        }
    	if(requestFlg != null && !requestFlg.equals("")){
    		sql += " AND  A.REQUEST_FLG='"+requestFlg+"' ";
    	}
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	return result ;
    }
    
    /**
     * ���ұ�ҩ���ɲ�ѯ         �����루���ܣ�
     * @param parm
     * @return
     */
    public  TParm onQueryExm(TParm parm) {
    	String sql = "SELECT 'Y' AS SELECT_FLG, A.REQUEST_NO, A.ORDER_CODE, C.ORDER_DESC, "+
	       			    " C.SPECIFICATION, A.QTY AS DOSAGE_QTY, D.UNIT_CHN_DESC, A.STOCK_PRICE, "+
	       			    " A.STOCK_PRICE * A.QTY AS STOCK_AMT, A.RETAIL_PRICE AS OWN_PRICE, "+
	       			    " A.RETAIL_PRICE * A.QTY AS OWN_AMT, "+
	       			    " A.RETAIL_PRICE * A.QTY - A.STOCK_PRICE * A.QTY AS DIFF_AMT "+
  		           " FROM IND_REQUESTD A, IND_REQUESTM B, SYS_FEE C, SYS_UNIT D" +
//  		           ",PHA_BASE E "+//delete by wanglong 20130308
 		           " WHERE A.REQUEST_NO = B.REQUEST_NO "+
	   			       " AND A.ORDER_CODE = C.ORDER_CODE  "+
	   			       " AND A.UNIT_CODE = D.UNIT_CODE "+
//	   			       " AND E.ORDER_CODE=C.ORDER_CODE"//delete by wanglong 20130308
	   			       ""; 

    	String orderSql = "ORDER BY A.REQUEST_NO, A.SEQ_NO" ;
    	String startDate =parm.getValue("START_DATE");
    	String endDate = parm.getValue("END_DATE");
    	sql += "	AND B.REQUEST_DATE  BETWEEN TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS')  "+
        "           AND TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS')  ";
    	String appOrgCode = parm.getValue("APP_ORG_CODE");
    	if(appOrgCode != null && !appOrgCode.equals("")){
    		sql += "	AND B.APP_ORG_CODE = '"+appOrgCode+"' ";
    	}
    	String requestNo = parm.getValue("REQUEST_NO");
    	String regionCode = parm.getValue("REGION_CODE");
    	String requestFlg = parm.getValue("REQUEST_FLG");
    	
    	if(regionCode != null && !regionCode.equals("")){
    		sql += " AND B.REGION_CODE='"+regionCode+"' ";
    	}
    	
    	if(requestNo != null && !requestNo.equals("")){
    		sql += " AND A.REQUEST_NO='"+requestNo+"' ";
    	}
    	
    	if(requestFlg != null && !requestFlg.equals("")){
    		sql += " AND  A.REQUEST_FLG='"+requestFlg+"' ";
    	}
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql+orderSql));
    	return result ;
    }

}
