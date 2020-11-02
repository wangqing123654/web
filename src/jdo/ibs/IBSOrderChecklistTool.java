package jdo.ibs;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
*
* <p>Title:医嘱费用审核</p>
*
* <p>Description: 医嘱费用审核</p>
*
* <p>Copyright: Copyright (c) caoyong 20131022</p>
*
* <p>Company:爱育华项目</p>
*
* @author caoyong
* @version 1.0
*/
public class IBSOrderChecklistTool extends TJDOTool{
	public static IBSOrderChecklistTool instanceObject;
	
	public static  IBSOrderChecklistTool getInstance(){
		if(instanceObject==null){
			instanceObject=new IBSOrderChecklistTool();
		}
		return instanceObject;
	}
	
	public IBSOrderChecklistTool() {
		setModuleName("ibs\\IBSOrderChecklistModule.x");
		onInit();
	}
	
	/**
	 * 查询操作
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdata(TParm cparm,TParm parm) {
		TParm result = new TParm();
		String sql1="";
		String sql="SELECT DISTINCT ORDER_CODE,MIN( TO_CHAR(EFF_DATE,'yyyy/MM/dd HH:mm:ss' )) AS EFF_DATE,MAX( TO_CHAR(DC_DATE,'yyyy/MM/dd HH:mm:ss' )) AS DC_DATE, " +
				   "ORDER_DESC,SUM(DISPENSE_QTY) AS DISPENSE_QTY, " +
				   "DISPENSE_UNIT,FREQ_CODE,SPECIFICATION,ORDERSET_CODE AS ORDERS_CODE,ORDER_SEQ,RX_KIND " +
				   "FROM ODI_ORDER " +
				   "WHERE " +
				   "NS_CHECK_CODE IS NOT NULL AND "+
				   "(ORDERSET_CODE IS NULL OR ORDERSET_CODE=ORDER_CODE ) "+
				   "AND CASE_NO='"+cparm.getValue("CASE_NO")+"' ";
		   if(cparm.getValue("ORDER_CODE").length()>0){
			   sql+="AND ORDER_CODE='"+cparm.getValue("ORDER_CODE")+"' ";
		   }
		   if(cparm.getValue("DEPT_CODE").length()>0){
			   sql+=" AND DEPT_CODE='"+cparm.getValue("DEPT_CODE")+"' ";
		   }
		
           if(parm.getCount("RX_KIND")>0){
        	  for(int i=0;i<parm.getCount("RX_KIND");i++){
        	      if(i==parm.getCount("RX_KIND")-1){
        	         sql1+="'"+parm.getValue("RX_KIND",i)+"'"; 
        	       }else{
        	    	 sql1+="'"+parm.getValue("RX_KIND",i)+"',";
        	       }
        	  }
	          
        	  sql=sql+"AND RX_KIND IN "+"("+sql1+") " ;
            }
              sql+="GROUP BY ORDER_CODE, ORDER_DESC, DISPENSE_UNIT,"+
                   "FREQ_CODE, SPECIFICATION, ORDERSET_CODE,ORDER_SEQ,RX_KIND ORDER BY EFF_DATE";
            //System.out.println("===左边========"+sql);
           result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		   return result;
	}
	/**
	 * 医嘱汇总查询
	 * @param parm
	 * @return
	 */
	public TParm selectdatatype(TParm cparm,TParm parm) {
		TParm result = new TParm();
		//result = query("selectdatall", parm);
        String sql1="";
		String sql= "SELECT DISTINCT A.ORDER_CODE,MIN(TO_CHAR(A.BEGIN_DATE,'yyyy/MM/dd HH:mm:ss')) AS EFF_DATE,MAX( TO_CHAR(A.END_DATE,'yyyy/MM/dd HH:mm:ss')) AS DC_DATE, " +
				    "C.ORDER_DESC, "+
		            "SUM(A.DOSAGE_QTY) AS DISPENSE_QTY,A.MEDI_UNIT AS DISPENSE_UNIT, " +
		            "A.FREQ_CODE,C.SPECIFICATION, A.ORDERSET_CODE AS ORDERS_CODE,B.DATA_TYPE "+
				    "FROM IBS_ORDD A,IBS_ORDM B,SYS_FEE C "+
				    "WHERE A.CASE_NO = B.CASE_NO "+
				    "AND (A.ORDERSET_CODE IS NULL OR A.ORDERSET_CODE=A.ORDER_CODE ) "+
				    "AND A.CASE_NO_SEQ=B.CASE_NO_SEQ "+
				    "AND A.ORDER_CODE = C.ORDER_CODE "+
				    "AND B.DATA_TYPE NOT IN('2','3','5','6') "+
				    "AND A.CASE_NO='"+cparm.getValue("CASE_NO")+"' ";
		
					if(cparm.getValue("ORDER_CODE").length()>0){
						   sql+=" AND A.ORDER_CODE='"+cparm.getValue("ORDER_CODE")+"' ";
					   }
					if(cparm.getValue("DEPT_CODE").length()>0){
						   sql+=" AND A.DEPT_CODE='"+cparm.getValue("DEPT_CODE")+"' ";
					   }
					 if(parm.getCount("DATA_TYPE")>0){
				   	  for(int i=0;i<parm.getCount("DATA_TYPE");i++){
				   	      if(i==parm.getCount("DATA_TYPE")-1){
				   	         sql1+="'"+parm.getValue("DATA_TYPE",i)+"'"; 
				   	       }else{
				   	    	 sql1+="'"+parm.getValue("DATA_TYPE",i)+"',";
				   	       }
				   	  }
				   	   sql=sql+"AND B.DATA_TYPE IN "+"("+sql1+")" ;
				   	  }
	   String sqlg= " GROUP BY  A.ORDER_CODE, C.ORDER_DESC, "+ 
				    " A.MEDI_UNIT,A.FREQ_CODE,C.SPECIFICATION,A.ORDERSET_CODE,B.DATA_TYPE  ORDER BY EFF_DATE";
	                  sql=sql+sqlg;
	                  
	                  //System.out.println("右边：：：："+sql);
	   result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 医嘱显示明细查询
	 * @param parm
	 * @return
	 */
	public TParm selectdatac(TParm parm) {
		TParm result = new TParm();
		String sql="SELECT  TO_CHAR(A.BILL_DATE,'yyyy/MM/dd HH:mm:ss' ) AS BILL_DATE,A.DOSAGE_QTY,TOT_AMT,A.ORDER_CODE, " +
			         "A.ORDER_CHN_DESC,A.OWN_PRICE,A.DOSAGE_UNIT,B.SPECIFICATION,A.OPT_USER " +
			         "FROM IBS_ORDD A ,SYS_FEE B " +
				     "WHERE "+
				     "A.ORDER_CODE=B.ORDER_CODE "+
				     "AND A.CASE_NO='"+parm.getValue("CASE_NO")+"'  "+
				     "AND A.BILL_DATE  BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMMDDHH24MISS') "+
				     "AND TO_DATE ('"+parm.getValue("E_DATE")+"','YYYYMMDDHH24MISS') ";
			    if(parm.getValue("DEPT_CODE").length()>0){
			       sql+="AND A.DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
			    }
				if(parm.getValue("ORDERSET_CODE").length()>0){
					sql+="AND A.ORDERSET_CODE='"+parm.getValue("ORDERSET_CODE")+"' AND A.ORDERSET_CODE<>A.ORDER_CODE ";
				}
				if(parm.getValue("ORDER_CODE").length()>0){
					 sql+="AND A.ORDER_CODE='"+parm.getValue("ORDER_CODE")+"'";
				}
		        if(parm.getValue("FREQ_CODE").length()>0){ 
		        	 sql+="AND A.FREQ_CODE='"+parm.getValue("FREQ_CODE")+"'";
		        }
		        sql+="ORDER BY A.BILL_DATE,A.ORDER_CODE,A.REXP_CODE ";
                //System.out.println("我是sql"+sql);
                 result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
	}
	/**
	 * order_code 对应的orderNO
	 * @param orderCode
	 * @param caseNo
	 * @return
	 */
	public TParm selectdataorderno(TParm parm) {
		TParm result = new TParm();
		String freqCode="";
		String deptCode="";
		if(parm.getValue("FREQ_CODE").length()>0){
			freqCode=" AND FREQ_CODE='"+parm.getValue("FREQ_CODE")+"' ";
		}
		if(parm.getValue("DEPT_CODE").length()>0){
			deptCode=" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
		}
		 String sql="SELECT DISTINCT ORDER_NO,ORDER_SEQ FROM IBS_ORDD " +
		 		    "WHERE ORDER_CODE='"+parm.getValue("ORDER_CODE")+"' " +
		 	        "AND CASE_NO='"+parm.getValue("CASE_NO")+"' " +
		 	        "AND ORDER_NO IS NOT NULL "+freqCode+deptCode;
		   
		  result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
	}
	/**
	 * 医嘱显示明细查询
	 * @param parm
	 * @return
	 */
	public TParm selectdatao(TParm parm ,String orderNo,String orderSeq) {
		TParm result = new TParm();
		String sql="SELECT TO_CHAR(A.BILL_DATE,'yyyy/MM/dd HH:mm:ss' ) AS BILL_DATE,A.DOSAGE_QTY,A.TOT_AMT,A.ORDER_CODE, " +
			         "A.ORDER_CHN_DESC,A.OWN_PRICE,A.DOSAGE_UNIT,B.SPECIFICATION,A.OPT_USER " +
			         "FROM IBS_ORDD A ,SYS_FEE B " +
				     "WHERE "+
				     "A.ORDER_CODE=B.ORDER_CODE "+
				     "AND A.CASE_NO='"+parm.getValue("CASE_NO")+"'  "+
				     "AND A.BILL_DATE  BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMMDDHH24MISS') "+
				     "AND TO_DATE ('"+parm.getValue("E_DATE")+"','YYYYMMDDHH24MISS') "+
				     "AND A.ORDER_NO='"+orderNo+"' AND A.ORDER_SEQ='"+orderSeq+"' ";
				     
				     //"A.ORDERSET_CODE<>'"+parm.getValue("ORDERSET_CODE")+"' ";
				 if(parm.getValue("DEPT_CODE").length()>0){
				       sql+="AND A.DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
				    }  
			     if(parm.getValue("ORDER_CODE").length()>0){
			    	 sql+="AND A.ORDER_CODE<>'"+parm.getValue("ORDER_CODE")+"' AND (A.ORDERSET_CODE<>A.ORDER_CODE OR A.ORDERSET_CODE IS NULL) "; 
			       }
				 if(parm.getValue("ORDERSET_CODE").length()>0){
					sql+="AND (A.ORDERSET_CODE<>'"+parm.getValue("ORDERSET_CODE")+"' AND A.ORDERSET_CODE<>A.ORDER_CODE OR  A.ORDERSET_CODE IS  NULL)";
				   }
				
				//if(parm.getValue("ORDER_CODE").length()>0){
					// sql+="AND A.ORDER_CODE='"+parm.getValue("ORDER_CODE")+"'";
				//}
		
		         sql+="ORDER BY A.BILL_DATE,A.ORDER_CODE,A.REXP_CODE ";
           // System.out.println("xxxxxxxxxxxxxx补充计价的费用"+sql);
                 result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
	}
	/**
	 * 费用汇总
	 * @param parm
	 * @return
	 */
	public TParm selectdatall(TParm parm) {
		TParm result = new TParm();
		result = query("selectdatall", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 联合费用总结额
	 * @param parm
	 * @return
	 */
    public TParm selecttotamt(TParm parm){
	  TParm result = new TParm();
		result = query("selecttotamt", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
  }
 
	
}
