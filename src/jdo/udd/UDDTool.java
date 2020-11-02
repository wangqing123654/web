/**
 * @className INDTool.java 
 * @author litong
 * @Date 2013-3-18 
 * @version V 1.0 
 */
package jdo.udd;

//import jdo.ekt.EKTTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * @author litong
 * @Date 2013-3-18
 */
public class UDDTool extends TJDOTool {

	/**
	 * 构造器
	 */
	private UDDTool() {
		// 加载Module文件
		this.setModuleName("UDD\\UDDSellOrder.x");
		onInit();
	}

	/**
	 * 实例
	 */
	private static UDDTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return
	 */
	public static UDDTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new UDDTool();
		}
		return instanceObject;
	}

	/**
	 * 查询抗生素销售数据
	 * 
	 * @return TParm
	 */
	public TParm getSellOrder(TParm parm) {
		String where="";
		if (null!=parm.getValue("ORDER_CODE") &&parm.getValue("ORDER_CODE").toString().length()>0) {
			where+= " WHERE DD.ORDER_CODE='"+parm.getValue("ORDER_CODE")+"'";
		}
		if (null!=parm.getValue("DS_FLG") &&parm.getValue("DS_FLG").toString().length()>0) {
			if (where.length()>0) {
				where+= " AND  DD.DS_FLG='"+parm.getValue("DS_FLG")+"' ";
			}else{
				where+= " WHERE  DD.DS_FLG='"+parm.getValue("DS_FLG")+"' ";
			}
		}
		if (null!=parm.getValue("ANTIBIOTIC_CODE") &&parm.getValue("ANTIBIOTIC_CODE").toString().length()>0) {
			if (where.length()>0) {
				where+= " AND  DD.ANTIBIOTIC_CODE='"+parm.getValue("ANTIBIOTIC_CODE")+"' ";
			}else{
				where+= " WHERE  DD.ANTIBIOTIC_CODE='"+parm.getValue("ANTIBIOTIC_CODE")+"' ";
			}
		}
		
		String sql="SELECT DD.REGION_CHN_ABN,DD.ORDER_CODE, DD.ORDER_DESC, "+
  			 "DD.SPECIFICATION,DD.OWN_PRICE,SUM (DD.SUM_QTY) AS SUM_QTY, "+
   			" SUM (DD.OWN_PRICE * DD.SUM_QTY) AS SUM_AMT,DD.STOCK_PRICE ,SUM(DD.STOCK_PRICE* DD.SUM_QTY) AS STOCK_AMT" +
   			"  ,DD.STOCK_UNIT,DD.STOCK_PRICE1,DD.OWN_PRICE1,SUM (DD.SUM_QTY1) AS SUM_QTY1   "+
    		" FROM ("+
    		"	(SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, "+
         	"		 A.SPECIFICATION AS SPECIFICATION,"+
         	"		 A.OWN_PRICE AS OWN_PRICE1,SUM (A.DOSAGE_QTY) AS SUM_QTY1, SUM (A.AR_AMT) AS SUM_AMT,B.STOCK_PRICE AS STOCK_PRICE1 ," +
         	"        SUM(B.STOCK_PRICE*A.DOSAGE_QTY) AS STOCK_AMT,B.ANTIBIOTIC_CODE,A.DR_CODE,'N' DS_FLG" +
         	"        ,B.STOCK_PRICE*D.DOSAGE_QTY AS STOCK_PRICE,A.OWN_PRICE*D.DOSAGE_QTY AS OWN_PRICE "+
            "        ,SUM (A.DOSAGE_QTY/D.DOSAGE_QTY) AS SUM_QTY,D.STOCK_UNIT "+
          	 "  FROM OPD_ORDER A, PHA_BASE B ,SYS_REGION F,REG_PATADM C,PHA_TRANSUNIT D "+
              "       WHERE A.ORDER_CODE = D.ORDER_CODE " +
              "          AND C.ADM_TYPE IN ('O', 'E') "+
              "          AND A.ORDER_CODE = B.ORDER_CODE "+
             "           AND A.CASE_NO=C.CASE_NO "+
             "           AND F.REGION_CODE=C.REGION_CODE "+
          	 "       AND b.ANTIBIOTIC_CODE IS NOT NULL "+
     		"	AND A.BILL_DATE >=TO_DATE ('"+parm.getValue("START_DATE")+"','YYYY/MM/DD HH24:MI:SS') "+
             "           AND A.BILL_DATE<=TO_DATE ('"+parm.getValue("END_DATE")+"', 'YYYY/MM/DD HH24:MI:SS') "+
             "           AND A.CAT1_TYPE = 'PHA' "+
		   " GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN, "+
		    "   A.SPECIFICATION, A.OWN_PRICE,B.STOCK_PRICE,B.ANTIBIOTIC_CODE,A.DR_CODE,D.STOCK_UNIT,D.DOSAGE_QTY) "+
             "      UNION ALL(SELECT F.REGION_CHN_ABN, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC, "+
             "           B.SPECIFICATION AS SPECIFICATION, A.OWN_PRICE AS OWN_PRICE1, "+
            "         SUM (A.DOSAGE_QTY) AS SUM_QTY1, SUM (TOT_AMT) AS SUM_AMT,C.STOCK_PRICE AS STOCK_PRICE1 ," +
            "        SUM(C.STOCK_PRICE*A.DOSAGE_QTY) AS STOCK_AMT," +
            "         C.ANTIBIOTIC_CODE,A.DR_CODE,A.DS_FLG " +
            "         ,C.STOCK_PRICE*E.DOSAGE_QTY AS STOCK_PRICE,A.OWN_PRICE*E.DOSAGE_QTY AS OWN_PRICE   "+
	        "         ,SUM (A.DOSAGE_QTY/E.DOSAGE_QTY) AS SUM_QTY,E.STOCK_UNIT  "+
             "  FROM IBS_ORDD A, SYS_FEE B, PHA_BASE C,SYS_REGION F,ADM_INP D,PHA_TRANSUNIT E  "+
             "        WHERE A.ORDER_CODE = E.ORDER_CODE  " +
             "          AND A.ORDER_CODE = B.ORDER_CODE "+
             "          AND A.ORDER_CODE = C.ORDER_CODE "+  
            "           AND A.CASE_NO = D.CASE_NO "+
		    "   AND A.CAT1_TYPE= 'PHA'  "+
		    "   AND C.ANTIBIOTIC_CODE IS NOT NULL  "+
             "          AND D.REGION_CODE=F.REGION_CODE "+
             "          AND A.BILL_DATE  "+
             "        BETWEEN TO_DATE ('"+parm.getValue("START_DATE")+"','YYYY/MM/DD HH24:MI:SS') "+
             "          AND TO_DATE ('"+parm.getValue("END_DATE")+"', 'YYYY/MM/DD HH24:MI:SS') "+
             "        GROUP BY A.ORDER_CODE, B.ORDER_DESC,F.REGION_CHN_ABN, "+
            "      B.SPECIFICATION, A.OWN_PRICE,C.STOCK_PRICE,C.ANTIBIOTIC_CODE,A.DR_CODE,A.DS_FLG,E.STOCK_UNIT,E.DOSAGE_QTY)"+
             "         ) DD   "+where+
            "  GROUP BY DD.ORDER_CODE, DD.ORDER_DESC,DD.REGION_CHN_ABN, DD.SPECIFICATION, DD.OWN_PRICE,DD.STOCK_PRICE," +
            "  DD.STOCK_UNIT,DD.STOCK_PRICE1,DD.OWN_PRICE1  "+
             "      ORDER BY SUM_QTY DESC";
		TParm result =new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}  
		return result;
	}

	public TParm getAllOrder(TParm parm) {
		String where="";
		if (null!=parm.getValue("ORDER_CODE") &&parm.getValue("ORDER_CODE").toString().length()>0) {
			where+= " WHERE DD.ORDER_CODE='"+parm.getValue("ORDER_CODE")+"'";
		}
		if (null!=parm.getValue("DS_FLG") &&parm.getValue("DS_FLG").toString().length()>0) {
			if (where.length()>0) {
				where+= " AND  DD.DS_FLG='"+parm.getValue("DS_FLG")+"' ";
			}else{
				where+= " WHERE  DD.DS_FLG='"+parm.getValue("DS_FLG")+"' ";
			}
		}
//		if (null!=parm.getValue("ANTIBIOTIC_CODE") &&parm.getValue("ANTIBIOTIC_CODE").toString().length()>0) {
//			if (where.length()>0) {
//				where+= " AND  DD.ANTIBIOTIC_CODE='"+parm.getValue("ANTIBIOTIC_CODE")+"' ";
//			}else{
//				where+= " WHERE  DD.ANTIBIOTIC_CODE='"+parm.getValue("ANTIBIOTIC_CODE")+"' ";
//			}  
//		}
		
		String sql="SELECT DD.REGION_CHN_ABN,DD.ORDER_CODE, DD.ORDER_DESC, "+
  			 "DD.SPECIFICATION,DD.OWN_PRICE,SUM (DD.SUM_QTY) AS SUM_QTY, "+
   			" SUM (DD.OWN_PRICE * DD.SUM_QTY) AS SUM_AMT,DD.STOCK_PRICE ,SUM(DD.STOCK_PRICE* DD.SUM_QTY) AS STOCK_AMT" +
   			"  ,DD.STOCK_UNIT,DD.STOCK_PRICE1,DD.OWN_PRICE1,SUM (DD.SUM_QTY1) AS SUM_QTY1   "+
    		" FROM ("+
    		"	(SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, "+
         	"		 A.SPECIFICATION AS SPECIFICATION,"+
         	"		 A.OWN_PRICE AS OWN_PRICE1,SUM (A.DOSAGE_QTY) AS SUM_QTY1, SUM (A.AR_AMT) AS SUM_AMT,B.STOCK_PRICE AS STOCK_PRICE1 ," +
         	"        SUM(B.STOCK_PRICE*A.DOSAGE_QTY) AS STOCK_AMT,'N' DS_FLG" +
         	"        ,B.STOCK_PRICE*D.DOSAGE_QTY AS STOCK_PRICE,A.OWN_PRICE*D.DOSAGE_QTY AS OWN_PRICE "+
            "        ,SUM (A.DOSAGE_QTY/D.DOSAGE_QTY) AS SUM_QTY,D.STOCK_UNIT "+
          	 "  FROM OPD_ORDER A, PHA_BASE B ,SYS_REGION F,REG_PATADM C,PHA_TRANSUNIT D "+
              "       WHERE A.ORDER_CODE = D.ORDER_CODE " +
              "          AND C.ADM_TYPE IN ('O', 'E') "+
              "          AND A.ORDER_CODE = B.ORDER_CODE "+
             "           AND A.CASE_NO=C.CASE_NO "+
             "           AND F.REGION_CODE=C.REGION_CODE "+
     		"	AND A.BILL_DATE >=TO_DATE ('"+parm.getValue("START_DATE")+"','YYYY/MM/DD HH24:MI:SS') "+
             "           AND A.BILL_DATE<=TO_DATE ('"+parm.getValue("END_DATE")+"', 'YYYY/MM/DD HH24:MI:SS') "+
             "           AND A.CAT1_TYPE = 'PHA' "+
		   " GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN, "+
		    "   A.SPECIFICATION, A.OWN_PRICE,B.STOCK_PRICE,D.STOCK_UNIT,D.DOSAGE_QTY) "+
             "      UNION ALL(SELECT F.REGION_CHN_ABN, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC, "+
             "           B.SPECIFICATION AS SPECIFICATION, A.OWN_PRICE AS OWN_PRICE1, "+
            "         SUM (A.DOSAGE_QTY) AS SUM_QTY1, SUM (TOT_AMT) AS SUM_AMT,C.STOCK_PRICE AS STOCK_PRICE1 ," +
            "        SUM(C.STOCK_PRICE*A.DOSAGE_QTY) AS STOCK_AMT," +
            "         A.DS_FLG " +
            "         ,C.STOCK_PRICE*E.DOSAGE_QTY AS STOCK_PRICE,A.OWN_PRICE*E.DOSAGE_QTY AS OWN_PRICE   "+
	        "         ,SUM (A.DOSAGE_QTY/E.DOSAGE_QTY) AS SUM_QTY,E.STOCK_UNIT  "+
             "  FROM IBS_ORDD A, SYS_FEE B, PHA_BASE C,SYS_REGION F,ADM_INP D,PHA_TRANSUNIT E  "+
             "        WHERE A.ORDER_CODE = E.ORDER_CODE  " +
             "          AND A.ORDER_CODE = B.ORDER_CODE "+
             "          AND A.ORDER_CODE = C.ORDER_CODE "+      
            "           AND A.CASE_NO = D.CASE_NO "+
		    "   AND A.CAT1_TYPE= 'PHA'  "+
             "          AND D.REGION_CODE=F.REGION_CODE "+
             "          AND A.BILL_DATE  "+
             "        BETWEEN TO_DATE ('"+parm.getValue("START_DATE")+"','YYYY/MM/DD HH24:MI:SS') "+
             "          AND TO_DATE ('"+parm.getValue("END_DATE")+"', 'YYYY/MM/DD HH24:MI:SS') "+
             "        GROUP BY A.ORDER_CODE, B.ORDER_DESC,F.REGION_CHN_ABN, "+
            "      B.SPECIFICATION, A.OWN_PRICE,C.STOCK_PRICE,A.DS_FLG,E.STOCK_UNIT,E.DOSAGE_QTY)"+
             "         ) DD   "+where+
            "  GROUP BY DD.ORDER_CODE, DD.ORDER_DESC,DD.REGION_CHN_ABN, DD.SPECIFICATION, DD.OWN_PRICE,DD.STOCK_PRICE," +
            "  DD.STOCK_UNIT,DD.STOCK_PRICE1,DD.OWN_PRICE1  "+
             "      ORDER BY SUM_QTY DESC";  
		//System.out.println("全品种："+sql);
		TParm result =new TParm(TJDODBTool.getInstance().select(sql));    
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}  
		return result;
	}

	public TParm getSellOrder1(TParm parm) {
		TParm result = query("getSellOrder1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm getAllOrder1(TParm parm) {
		TParm result = query("getAllOrder1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getSellOrderOPD(TParm parm) {
		TParm result = query("getSellOrderOPD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getSellOrderODI(TParm parm) {
		TParm result = query("getSellOrderODI", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getAllOrderOPD(TParm parm) {
		TParm result = query("getAllOrderOPD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getAllOrderODI(TParm parm) {
		TParm result = query("getAllOrderODI", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getSellOrderOPD1(TParm parm) {
		TParm result = query("getSellOrderOPD1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getAllOrderOPD1(TParm parm) {
		TParm result = query("getAllOrderOPD1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getSellOrderODI1(TParm parm) {
		TParm result = query("getSellOrderODI1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	public TParm getAllOrderODI1(TParm parm) {
		TParm result = query("getAllOrderODI1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}