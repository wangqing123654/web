package jdo.ibs;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

public class IBSOrderdmTool extends TJDODBTool {
		/**
		 * 实例
		 */
		public static IBSOrderdmTool instanceObject;
	
		/**
		 * 得到实例
		 * 
		 * @return IBSOrderdTool
		 */
		public static IBSOrderdmTool getInstance() {
			if (instanceObject == null)
				instanceObject = new IBSOrderdmTool();
			return instanceObject;
		}
	
		/**
		 * 单项医嘱页面查询SQL
		 * @param parm
		 * @return
		 */
		public TParm queryPatInfo(TParm parm) {
	
			String SQL = "SELECT D.BILL_DATE,D.OWN_FLG,D.DOSAGE_QTY, CASE WHEN D.OWN_AMT = 0 THEN B.OWN_AMT   ELSE CASE WHEN D.ORDERSET_CODE IS NOT NULL THEN D.OWN_AMT/ D.DOSAGE_QTY ELSE D.OWN_PRICE END END OWN_PRICE, "
					+ " CASE  WHEN D.TOT_AMT=0 THEN B.TOT_AMT ELSE D.TOT_AMT END TOT_AMT,O.USER_NAME,D.DR_CODE,D.EXE_DR_CODE,"
					+ " D.CASE_NO_SEQ," 
					+ " D.INCLUDE_FLG,D.BILL_EXE_FLG," // add by kangy 0161014 添加套内套外、作废注记
					+ " D.OWN_RATE AS ZHEKOU"//" (D.TOT_AMT/D.DOSAGE_QTY/D.OWN_PRICE) AS ZHEKOU" // zhanglei 20170427 添加折扣标记
					+ " FROM IBS_ORDD D,(SELECT CASE_NO_SEQ,ORDERSET_GROUP_NO,CASE_NO,SUM(TOT_AMT) TOT_AMT,SUM(OWN_AMT) OWN_AMT,"
					+ " ORDERSET_CODE FROM IBS_ORDD WHERE ORDERSET_CODE IS NOT NULL "
					+ " AND CASE_NO='&' "
					+ " GROUP BY CASE_NO_SEQ,ORDERSET_GROUP_NO,CASE_NO,"
					+ " ORDERSET_CODE) B, SYS_OPERATOR O,IBS_ORDM M  "
					+ " WHERE D.OPT_USER = O.USER_ID AND D.CASE_NO=B.CASE_NO(+) AND D.CASE_NO_SEQ=B.CASE_NO_SEQ(+) "
					+ " AND D.ORDERSET_GROUP_NO=B.ORDERSET_GROUP_NO(+) "
					+ " AND D.CASE_NO='&'"
					+ " AND D.ORDER_CODE='!'" 
					+ " AND D.CASE_NO=M.CASE_NO" 
				    + " AND D.CASE_NO_SEQ=M.CASE_NO_SEQ"
					+ " ORDER BY D.BILL_DATE,D.CASE_NO,D.CASE_NO_SEQ,D.SEQ_NO";
			
			
			//String sql = "";
//			System.out.println(parm);
			SQL = SQL.replaceAll("&", parm.getValue("CASE_NO"));
//			SQL = SQL.replaceAll("@", parm.getValue("INDV_FLG"));
			SQL = SQL.replaceAll("!", parm.getValue("ORDER_CODE"));
//			SQL = SQL.replaceAll("^", parm.getValue("ORDERSET_CODE"));

			System.out.println("********"+SQL);
			
			return new TParm(TJDODBTool.getInstance().select(SQL));
			
		}
	
	}
