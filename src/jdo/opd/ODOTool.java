package jdo.opd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import jdo.odo.ODO;
import jdo.odo.OpdOrder;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.ui.odo.ODORxMed;

public class ODOTool {
	public static final String MSGGETPRINTNOFAIL = "E0112";// 操作失败
	
	/**
	 * 实例
	 */
	public static ODOTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return OrderTool
	 */
	public static ODOTool getInstance() {
		if (instanceObject == null)
			instanceObject = new ODOTool();
		return instanceObject;
	}
	/**
	 * 西药值改变事件 共用
	 * 
	 * @param inRow
	 * @param execDept
	 * @param orderCodeFinal
	 * @param columnNameFinal
	 */
	private void setOnMedValueChange(final int inRow, final String execDept,
			final String orderCodeFinal, final String columnNameFinal) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					if (!checkStoreQty(inRow, execDept, orderCodeFinal,
							columnNameFinal)) {
						return ;
					}

				} catch (Exception e) {
				}
			}
		});

	}
	
	/**
	 * 设置中药显示的总克数
	 */
	public void setChnPckTot() {

	}
	
	/**
	 * 计算并设置金额
	 * 
	 * @param tableName
	 *            String
	 * @param tag
	 *            String
	 */
	public void calculateCash(String tableName, String tag) {
		
	}
	
	/**
	 * 中药计算总金额
	 * 
	 * @param rxNo
	 *            处方号
	 */
	public void calculateChnCash(String rxNo) {
		
	}
	
	/**
	 * 计算并设置OpdOrder服总量
	 * 
	 * @param rxNo
	 *            String
	 */
	public void calculatePackageTot(String rxNo) {
		
	}
	
	/**
	 * 四舍五入 保留两位小数
	 * 
	 * @param value
	 *            double
	 * @return double
	 */
	public double roundAmt(double value) {
		double result = 0;
		if (value > 0)
			result = ((int) (value * 100.0 + 0.5)) / 100.0;
		else if (value < 0)
			result = ((int) (value * 100.0 - 0.5)) / 100.0;
		return result;
	}
	
	/**
	 * 各个TABLE中值改变事件用到的后置的库存检核方法
	 * 
	 * @param row
	 *            int
	 * @param execDept
	 *            String
	 * @param orderCode
	 *            String
	 * @param columnName
	 *            String
	 * @return boolean
	 */
	public boolean checkStoreQty(final int row, final String execDept,
			final String orderCode, final String columnName) {
//		if (StringUtil.isNullString(columnName)) {
//			return true;
//		}
//		if (!("TAKE_DAYS".equalsIgnoreCase(columnName)
//				|| "MEDI_QTY".equalsIgnoreCase(columnName)
//				|| "FREQ_CODE".equalsIgnoreCase(columnName) || "EXEC_DEPT_CODE"
//				.equalsIgnoreCase(columnName))) {
//			return true;
//		}
//		if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 校验物联网注记
//			OpdOrder order = odo.getOpdOrder();
//			order.showDebug();
//			double dosageQty = TypeTool.getDouble(order.getItemData(row,
//					"DOSAGE_QTY"));
//			if(orderCode.length()<=0)//==pangben 2013-5-2 当前没有选择的医嘱不执行校验库存操作
//				return true;
//			if (isCheckKC(orderCode)) // 判断是否是“药品备注”
//				// 物联网
//				if (!INDTool.getInstance().inspectIndStock(execDept, orderCode,
//						dosageQty)) {
//					// this.messageBox("E0052"); // 库存不足
//					// $$==========add by lx 2012-06-19加入存库不足，替代药提示
//					TParm inParm = new TParm();
//					inParm.setData("orderCode", orderCode);
//					this.openDialog("%ROOT%\\config\\pha\\PHAREDrugMsg.x",
//							inParm);
//					return false;
//				}
//		}
		return true;
	}
	
	/**
	 * 过敏药物
	 * @param orderCode
	 * @return
	 */
	public TParm getAllergy(String orderCode) {
		String sql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC,"
				+ "DESCRIPTION,SPECIFICATION,REMARK_1,REMARK_2,DRUG_NOTES_DR FROM SYS_FEE"
				+ " WHERE ORDER_CODE = '" + orderCode + "'";
		TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
		return sqlparm.getRow(0);
	}
	
	/**
	 * 取得ErdBed
	 * @param caseNo
	 * @return
	 */
	public TParm getErdBedByCaseNo(String caseNo){
		String sql = " SELECT B.CHN_DESC,A.BED_DESC FROM ERD_BED A,SYS_DICTIONARY B "
			+ " WHERE B.GROUP_ID='ERD_REGION' "
			+ " AND B.ID=A.ERD_REGION_CODE "
			+ " AND A.CASE_NO='"
			+ caseNo + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	/**
	 * 取得处方打印数据
	 * @param caseNo2
	 * @param rxNo2
	 * @return
	 */
	public TParm getPrintOrderData(String caseNo2, String rxNo2){
		
		String westsql = "  SELECT   CASE WHEN   OPD_ORDER.BILL_FLG='Y' THEN '√' ELSE '' END||'  '||OPD_ORDER.LINK_NO aa , "
			+ " CASE WHEN SYS_FEE.IS_REMARK = 'Y' THEN OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.ORDER_DESC ||' '|| OPD_ORDER.SPECIFICATION  END bb , "
			+ " OPD_ORDER.SPECIFICATION cc, "
			+ " OPD_ORDER.DR_NOTE DR,"
			+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '皮试' ELSE SYS_PHAROUTE.ROUTE_CHN_DESC  END dd,"
			+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '' ELSE RTRIM(RTRIM(TO_CHAR(OPD_ORDER.MEDI_QTY,'fm9999999990.000'),'0'),'.')||''||A.UNIT_CHN_DESC  END ee,"
			//modigy  by huangtt 20141103 RPAD(SYS_PHAFREQ.FREQ_CHN_DESC, (LENGTH (sys_phafreq.freq_chn_desc)), ' ')  变为 SYS_PHAFREQ.FREQ_CHN_DESC||' '
			+ " SYS_PHAFREQ.FREQ_CHN_DESC||' ' FF,"  
			+ " CASE WHEN OPD_ORDER.DISPENSE_QTY<1 THEN TO_CHAR(OPD_ORDER.DISPENSE_QTY,'fm9999999990.00') ELSE "
			+ " TO_CHAR(OPD_ORDER.DISPENSE_QTY) END||''|| B.UNIT_CHN_DESC" +
			" ||CASE " +
			" WHEN " +
			" OPD_ORDER.EXEC_DEPT_CODE IN (SELECT ORG_CODE FROM IND_ORG WHERE ORG_FLG = 'Y' AND ORG_TYPE = 'C' AND EXINV_FLG = 'Y') " +
			" THEN '  (发药点:'||IND_ORG.ORG_CHN_DESC||')'" +
			" ELSE ''" +
			" END "+
			" ER ,"
			//modify by wanglong 20121226
			+ " CASE WHEN OPD_ORDER.RELEASE_FLG = 'Y' THEN '自备  '|| OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.DR_NOTE END gg ,OPD_ORDER.DOSAGE_QTY,OPD_ORDER.OWN_PRICE,OPD_ORDER.DISCOUNT_RATE, " 
			+ " OPD_ORDER.BATCH_NO, "//add caoyong 20140322 皮试批号 
			+ " CASE WHEN  OPD_ORDER.SKINTEST_FLG='0' THEN '(-)阴性' WHEN OPD_ORDER.SKINTEST_FLG='1' THEN '(+)阳性'  END SKINTEST_FLG,  "//add caoyong 20140322 皮试结果
			//modify end
			+ " OPD_ORDER.DOSAGE_QTY || C.UNIT_CHN_DESC ||'/' || B.UNIT_CHN_DESC AS TT, "
			+ "( RTRIM (RTRIM (TO_CHAR (OPD_ORDER.MEDI_QTY,'FM9999999990.000' ),'0'),'.')|| ''|| A.UNIT_CHN_DESC || '(' || " 
			//modigy by huangtt 20141031  start 
//			+ "trunc( (OPD_ORDER.MEDI_QTY / PHA_TRANSUNIT.MEDI_QTY),2)" 
			+ "CASE WHEN TRUNC ((OPD_ORDER.MEDI_QTY / PHA_TRANSUNIT.MEDI_QTY), 2) < 1" +
					" THEN  '0' || TRUNC ((OPD_ORDER.MEDI_QTY / PHA_TRANSUNIT.MEDI_QTY), 2)" +
					" ELSE TO_CHAR(TRUNC ((OPD_ORDER.MEDI_QTY / PHA_TRANSUNIT.MEDI_QTY), 2)) END"
			//modigy by huangtt 20141031  end 
			+ " || C.UNIT_CHN_DESC || ')')  AS HH "
			+ " FROM   OPD_ORDER, SYS_PHAFREQ, SYS_PHAROUTE,SYS_UNIT A, SYS_UNIT B,SYS_FEE, SYS_UNIT C,PHA_TRANSUNIT, IND_ORG "
			+ " WHERE       CASE_NO = '"
			+ caseNo2
			+ "'"
			+ "  AND RX_NO = '"
			+ rxNo2
			+ "'"
			+ " and SYS_PHAROUTE.ROUTE_CODE(+) = OPD_ORDER.ROUTE_CODE "
			+ "  AND SYS_PHAFREQ.FREQ_CODE(+) = OPD_ORDER.FREQ_CODE "
			+ "  AND A.UNIT_CODE(+) =  OPD_ORDER.MEDI_UNIT "
			+ "  AND B.UNIT_CODE(+) =  OPD_ORDER.DISPENSE_UNIT "
			+ "  AND OPD_ORDER.ORDER_CODE = SYS_FEE.ORDER_CODE "
			+ "  AND OPD_ORDER.DOSAGE_UNIT=C.UNIT_CODE "
            + "  AND OPD_ORDER.ORDER_CODE=PHA_TRANSUNIT.ORDER_CODE " +
            " AND OPD_ORDER.EXEC_DEPT_CODE = IND_ORG.ORG_CODE(+) "
			+ " ORDER BY   LINK_NO, LINKMAIN_FLG DESC, SEQ_NO";
		
//		System.out.println("westsql::::::::"+westsql);
		
		return new TParm(TJDODBTool.getInstance().select(
			westsql));
	}
	
	/**
	 * 取得处置、检验检查通知单数据
	 * @param caseNo
	 * @param rxNo
	 * @return
	 */
	public TParm getPrintExaData(String caseNo, String rxNo, boolean flg){
		//=====pangben 2014-3-20添加字段，显示时限、执行地点
		//=====pangben 2014-3-20添加字段，显示时限、执行地点
		String sql = "SELECT  A.MED_APPLY_NO,F.OWN_PRICE,A.ORDER_CODE,CASE WHEN A.BILL_FLG='Y' THEN '√' ELSE '' END AS BILL_FLG,  C.DEPT_CHN_DESC,F.AR_AMT, "////caowl 20131104	ADD OWN_PRICE	 	    
			+ " A.ORDER_DESC, A.MEDI_QTY,CASE WHEN A.URGENT_FLG='Y' THEN '√' ELSE '' END AS URGENT_FLG,A.DR_NOTE AS DESCRIPTION ,A.DISCOUNT_RATE,D.CHN_DESC,  "  //=====liling 20140331 将A.DR_NOTE AS DESCRIPTION由c.DESCRIPTION改回   将B.TIME_LIMIT,去掉     
			+ "TO_CHAR(A.OPT_DATE, 'YYYY/MM/DD HH24:MI:SS') AS OPT_DATE,H.CHN_DESC AS BF,A.ORDER_CAT1_CODE " //=====liling 20140331去掉, g.chn_desc
			+ " FROM  OPD_ORDER A,SYS_FEE B,SYS_DEPT C," 
			+ "(SELECT ID, CHN_DESC,COST_CENTER_CODE FROM SYS_COST_CENTER A,SYS_DICTIONARY B WHERE B.GROUP_ID = 'EXAADDRESS' AND A.EXE_PLACE=B.ID) D, " 
			+ "(SELECT   RX_NO, ORDERSET_GROUP_NO, SUM (AR_AMT) AS AR_AMT,SUM(DISPENSE_QTY*OWN_PRICE) AS OWN_PRICE, CASE_NO "//caowl 20131104	ADD OWN_PRICE
            + "FROM OPD_ORDER "
            + "WHERE CASE_NO = '"+caseNo+"' "
            + "AND RX_NO IN ("+rxNo+") "
            + "AND SETMAIN_FLG = 'N' "
            + "AND ORDERSET_GROUP_NO IN ( "
            + "  SELECT A.ORDERSET_GROUP_NO "
            + "    FROM OPD_ORDER A, SYS_FEE B, SYS_DEPT C "
            + "    WHERE A.CASE_NO = '"+caseNo+"' "
            + "      AND RX_NO IN ("+rxNo+") "
            + "       AND A.ORDER_CODE = B.ORDER_CODE "
            + "       AND C.DEPT_CODE = A.EXEC_DEPT_CODE "
            + "       AND A.CAT1_TYPE IN ('LIS','RIS')) "
            + " GROUP BY RX_NO, ORDERSET_GROUP_NO, CASE_NO) F ,( SELECT  CHN_DESC,ID  FROM SYS_DICTIONARY WHERE  GROUP_ID = 'SYS_OPTITEM') H "// add caoyong2014 0321 H G 表"======liling 20140331去掉,(SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE   GROUP_ID = 'EXAADDRESS' ) G表  和D表重复
			+ " WHERE A.CASE_NO='"+ caseNo+ "' AND A.RX_NO IN ("+ rxNo
			+ ") AND A.SETMAIN_FLG='Y' AND A.ORDER_CODE=B.ORDER_CODE "
			+ "  AND B.OPTITEM_CODE=H.ID(+)   "//ADD CAOYONG 2014/021
			+ "  AND A.CASE_NO = F.CASE_NO AND A.RX_NO = F.RX_NO " 
			+  " AND A.EXEC_DEPT_CODE=D.COST_CENTER_CODE(+) AND A.ORDERSET_GROUP_NO = F.ORDERSET_GROUP_NO AND C.DEPT_CODE=A.EXEC_DEPT_CODE AND A.CAT1_TYPE IN ('LIS','RIS') ORDER BY A.MED_APPLY_NO,A.SEQ_NO ";
		if(!flg){
			sql = sql.replace("'LIS'", "'RIS'");
		}
	
		
		
		
		
		return new TParm(TJDODBTool.getInstance().select(sql));
		
	}
	/**
	 * caoyong
	 * 点击申请单按钮操作，查询检验数据
	 * @param caseNo
	 * @param flg
	 * @return
	 */
	public TParm getPrintExaDataSum(String caseNo,boolean flg){
		String caseNoSql = "SELECT CASE_NO FROM REG_PATADM WHERE OLD_CASE_NO = '"+caseNo+"'";
		TParm caseNoParm = new TParm(TJDODBTool.getInstance().select(caseNoSql));
		String sql = "";
		if(caseNoParm.getCount()>0){//存在预开检查
			String newCaseNo = "'"+caseNo+"'";
			for(int i = 0;i<caseNoParm.getCount();i++){
				if(i!=caseNoParm.getCount()-1){
					newCaseNo += ","+"'"+caseNoParm.getValue("CASE_NO", i)+"'";	
				}else{
					newCaseNo += ","+"'"+caseNoParm.getValue("CASE_NO", i);
				}
				
			}
			//System.out.println("+++++newCaseNo newCaseNo is ::"+newCaseNo);
			 sql = "SELECT  A.MED_APPLY_NO,F.OWN_PRICE,A.ORDER_CODE,CASE WHEN A.BILL_FLG='Y' THEN '√' ELSE '' END AS BILL_FLG,  C.DEPT_CHN_DESC,F.AR_AMT, "////caowl 20131104	ADD OWN_PRICE	 	    
				+ " A.ORDER_DESC, A.MEDI_QTY,CASE WHEN A.URGENT_FLG='Y' THEN '√' ELSE '' END AS URGENT_FLG,C.DESCRIPTION ,A.DISCOUNT_RATE ,"  //huangtt 20121127 将A.DR_NOTE AS DESCRIPTION改为c.DESCRIPTION
				+ "TO_CHAR(A.ORDER_DATE,'YYYY/MM/DD HH24:MI:SS') AS OPT_DATE,G.CHN_DESC,H.CHN_DESC AS BF,A.ORDER_CAT1_CODE,A.DR_CODE, " //==liling 20140818 add A.DR_CODE
				+ "A.DEPT_CODE,"//add by huangjw 20140912
				+" A.MEM_PACKAGE_FLG"//ADD BY kangy 20170517
				+ " FROM  OPD_ORDER A,SYS_FEE B,SYS_DEPT C, (SELECT   RX_NO, ORDERSET_GROUP_NO, SUM (AR_AMT) AS AR_AMT,SUM(OWN_PRICE) AS OWN_PRICE, CASE_NO "//caowl 20131104	ADD OWN_PRICE
	            + "FROM OPD_ORDER "
	            + "WHERE CASE_NO IN ("+newCaseNo+"') "
	            + "AND SETMAIN_FLG = 'N' "
	            + "AND ORDERSET_GROUP_NO IN ( "
	            + "  SELECT A.ORDERSET_GROUP_NO "
	            + "    FROM OPD_ORDER A, SYS_FEE B, SYS_DEPT C "
	            + "    WHERE A.CASE_NO IN ("+newCaseNo+"') "
	            + "       AND A.ORDER_CODE = B.ORDER_CODE "
	            + "       AND C.DEPT_CODE = A.EXEC_DEPT_CODE "
	            + "       AND A.CAT1_TYPE IN ('LIS','RIS')) "
	            + "  GROUP BY RX_NO, ORDERSET_GROUP_NO, CASE_NO) F," 
				+ "(SELECT ID, CHN_DESC,COST_CENTER_CODE FROM SYS_COST_CENTER A,SYS_DICTIONARY B WHERE B.GROUP_ID = 'EXAADDRESS' AND A.EXE_PLACE=B.ID) G, " 
	            + " ( SELECT  CHN_DESC,ID  FROM SYS_DICTIONARY WHERE  GROUP_ID = 'SYS_OPTITEM') H "// add caoyong2014 0321 H G 表""

				+ "  WHERE A.CASE_NO IN ("+newCaseNo+"') AND A.SETMAIN_FLG='Y' AND A.ORDER_CODE=B.ORDER_CODE "
				+ "  AND A.EXEC_DEPT_CODE=G.COST_CENTER_CODE(+) "//ADD CAOYONG 2014/021
				+ "  AND B.OPTITEM_CODE=H.ID(+)   "//ADD CAOYONG 2014/021
				+ "  AND A.CASE_NO = F.CASE_NO AND A.RX_NO = F.RX_NO AND A.ORDERSET_GROUP_NO = F.ORDERSET_GROUP_NO AND C.DEPT_CODE=A.EXEC_DEPT_CODE AND A.CAT1_TYPE IN ('LIS','RIS') " 
		
				+ "　ORDER BY A.MED_APPLY_NO,A.SEQ_NO ";
			
		}else{
			 sql = "SELECT  A.MED_APPLY_NO,F.OWN_PRICE,A.ORDER_CODE,CASE WHEN A.BILL_FLG='Y' THEN '√' ELSE '' END AS BILL_FLG,  C.DEPT_CHN_DESC,F.AR_AMT, "////caowl 20131104	ADD OWN_PRICE	 	    
				+ " A.ORDER_DESC, A.MEDI_QTY,CASE WHEN A.URGENT_FLG='Y' THEN '√' ELSE '' END AS URGENT_FLG,C.DESCRIPTION ,A.DISCOUNT_RATE ,"  //huangtt 20121127 将A.DR_NOTE AS DESCRIPTION改为c.DESCRIPTION
				+ "TO_CHAR(A.ORDER_DATE,'YYYY/MM/DD HH24:MI:SS') AS OPT_DATE,A.ORDER_DATE,G.CHN_DESC,H.CHN_DESC AS BF,A.ORDER_CAT1_CODE,A.DR_CODE, " //==liling 20140818 add A.DR_CODE
				+ "A.DEPT_CODE,"//add by huangjw 20140912
				+" A.MEM_PACKAGE_FLG "//ADD BY kangy 20170517
				+ " FROM  OPD_ORDER A,SYS_FEE B,SYS_DEPT C, (SELECT   RX_NO, ORDERSET_GROUP_NO, SUM (AR_AMT) AS AR_AMT,SUM(OWN_PRICE) AS OWN_PRICE, CASE_NO "//caowl 20131104	ADD OWN_PRICE
	            + "FROM OPD_ORDER "
	            + "WHERE CASE_NO = '"+caseNo+"' "
	            + "AND SETMAIN_FLG = 'N' "
	            + "AND ORDERSET_GROUP_NO IN ( "
	            + "  SELECT A.ORDERSET_GROUP_NO "
	            + "    FROM OPD_ORDER A, SYS_FEE B, SYS_DEPT C "
	            + "    WHERE A.CASE_NO = '"+caseNo+"' "
	            + "       AND A.ORDER_CODE = B.ORDER_CODE "
	            + "       AND C.DEPT_CODE = A.EXEC_DEPT_CODE "
	            + "       AND A.CAT1_TYPE IN ('LIS','RIS')) "
	            + "  GROUP BY RX_NO, ORDERSET_GROUP_NO, CASE_NO) F," 
				+ "(SELECT ID, CHN_DESC,COST_CENTER_CODE FROM SYS_COST_CENTER A,SYS_DICTIONARY B WHERE B.GROUP_ID = 'EXAADDRESS' AND A.EXE_PLACE=B.ID) G, " 
	            + " ( SELECT  CHN_DESC,ID  FROM SYS_DICTIONARY WHERE  GROUP_ID = 'SYS_OPTITEM') H "// add caoyong2014 0321 H G 表""

				+ "  WHERE A.CASE_NO='"+ caseNo+ "' AND A.SETMAIN_FLG='Y' AND A.ORDER_CODE=B.ORDER_CODE "
				+ "  AND A.EXEC_DEPT_CODE=G.COST_CENTER_CODE(+) "//ADD CAOYONG 2014/021
				+ "  AND B.OPTITEM_CODE=H.ID(+)   "//ADD CAOYONG 2014/021
				+ "  AND A.CASE_NO = F.CASE_NO AND A.RX_NO = F.RX_NO AND A.ORDERSET_GROUP_NO = F.ORDERSET_GROUP_NO AND C.DEPT_CODE=A.EXEC_DEPT_CODE AND A.CAT1_TYPE IN ('LIS','RIS')" 

				+ " ORDER BY A.MED_APPLY_NO,A.SEQ_NO ";
			
		}
		if(!flg){
			sql = sql.replace("'LIS'", "'RIS'");
		}
		
//		System.out.println("sql:::::::::::::::::::::"+sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	public TParm getPrintOpData(String caseNo1, String rxNo1, String rxType){
        TParm dataParm = new TParm();
        String sql = // modify by wanglong 20140414
                "SELECT A.ORDER_CODE,A.DOSAGE_QTY,B.OWN_PRICE,CASE WHEN A.BILL_FLG='Y' THEN '√' ELSE '' END AS BILL_FLG,D.DEPT_CHN_DESC, "
                        + "       A.ORDER_DESC||CASE WHEN A.SPECIFICATION IS NOT NULL THEN '/'||A.SPECIFICATION ELSE '' END AS ORDER_DESC,"
                        + "       A.MEDI_QTY,CASE WHEN A.URGENT_FLG='Y' THEN '√' ELSE '' END AS URGENT_FLG,C.DESCRIPTION,"
                        + "       CASE WHEN A.DISCOUNT_RATE=0 THEN 1 ELSE A.DISCOUNT_RATE END AS DISCOUNT_RATE,B.AR_AMT "
                        + "  FROM OPD_ORDER A, ( "
                        + "          SELECT MIN(A.SEQ_NO) AS SEQ_NO,A.CASE_NO,A.RX_NO,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,SUM(A.OWN_PRICE) OWN_PRICE,SUM(A.AR_AMT) AR_AMT "
                        + "          FROM OPD_ORDER A "
                        + "         WHERE A.CASE_NO = '@' "
                        + "           AND A.RX_NO IN (#) "
                        + "           AND A.ORDERSET_CODE IS NOT NULL "
                        + "        GROUP BY A.CASE_NO, A.RX_NO, A.ORDERSET_GROUP_NO, A.ORDERSET_CODE "
                        + "        UNION "
                        + "        SELECT A.SEQ_NO, A.CASE_NO, A.RX_NO, A.ORDERSET_GROUP_NO, A.ORDER_CODE AS ORDERSET_CODE, A.OWN_PRICE, A.AR_AMT "
                        + "          FROM OPD_ORDER A "
                        + "         WHERE A.CASE_NO = '@' "
                        + "           AND A.RX_NO IN (#) "
                        + "           AND A.ORDERSET_CODE IS NULL) B,SYS_FEE C,SYS_DEPT D "
                        + " WHERE A.CASE_NO = B.CASE_NO "
                        + "   AND A.RX_NO = B.RX_NO "
                        + "   AND A.SEQ_NO = B.SEQ_NO "
                        + "   AND A.ORDER_CODE = B.ORDERSET_CODE "
                        + "   AND A.ORDER_CODE = C.ORDER_CODE "
                        + "   AND A.ORDER_CODE = C.ORDER_CODE "
                        + "   AND A.EXEC_DEPT_CODE = D.DEPT_CODE " + "ORDER BY A.SEQ_NO";
        sql = sql.replaceAll("@", caseNo1);
        sql = sql.replaceAll("#", rxNo1);
        dataParm = new TParm(TJDODBTool.getInstance().select(sql));
        return dataParm;
    }
	
	public boolean getPhaStockFlg(String orgCode){
		String sql = "SELECT PHA_STOCK_FLG FROM IND_ORG WHERE ORG_CODE = '" + orgCode + "'";
		TParm pha = new TParm(TJDODBTool.getInstance().select(sql));
		return pha.getBoolean("PHA_STOCK_FLG", 0);
	}
	
	public String queryCaseNoByRxNo(String rxNo){
		//根据处方签号查询case_no
		String selectCaseNo = "SELECT CASE_NO FROM OPD_ORDER WHERE RX_NO = '"+rxNo+"' AND IS_PRE_ORDER = 'Y'";
		TParm selectCaseNoParm = new TParm(TJDODBTool.getInstance().select(selectCaseNo));
		String caseNo = "";
		if(selectCaseNoParm.getCount()>0){
			caseNo = selectCaseNoParm.getValue("CASE_NO",0);
		}
		return caseNo;
	}
	
	/** 删除处方签时删除挂号信息
	 * yanjing 20140108
	 */
	public void deleteReg(String preCaseNo, String rxNo, String[] data) {
		if(preCaseNo.length() == 0){
			return;
		}
		boolean delFlg = true;
		for (int m = 0; m < data.length; m++) {
			if (!data[m].split(",")[0].equals(rxNo)) {
				String opdOrderSql = "SELECT CASE_NO FROM OPD_ORDER WHERE RX_NO = '"
						+ data[m].split(",")[0] + "' AND IS_PRE_ORDER = 'Y'";
				TParm opdOrderParm = new TParm(TJDODBTool.getInstance().select(
						opdOrderSql));
				if (opdOrderParm.getCount() > 0) {// OPD_OEDER表中没有该case_no的医嘱删除reg_patadm的挂号信息
					String nextCaseNo = opdOrderParm.getValue("CASE_NO", 0);
					if (preCaseNo.equals(nextCaseNo)) {
						delFlg = false;
						break;
					}
				}
			}
		}
		if (delFlg) {
			String regSql = "SELECT MR_NO FROM REG_PATADM WHERE CASE_NO = '"
					+ preCaseNo + "'";
			TParm regParm = new TParm(TJDODBTool.getInstance().select(regSql));
			if (regParm.getCount() > 0) {// 删除挂号信息
				String delSql = "DELETE REG_PATADM WHERE CASE_NO = '"
						+ preCaseNo + "'";
				TParm delRegParm = new TParm(TJDODBTool.getInstance().update(
						delSql));
			}
		}
	}
	
	public void deleteReg(OpdOrder order, int row, String rxNo, String orderCode){
		// 根据RX_NO查询case_no 和is_pre_order
		String selCaseNo = "SELECT CASE_NO,IS_PRE_ORDER FROM OPD_ORDER WHERE RX_NO = '"
				+ rxNo + "' AND IS_PRE_ORDER = 'Y'";
		TParm selectCaseNoParm = new TParm(TJDODBTool.getInstance().select(
				selCaseNo));
		if (selectCaseNoParm.getCount() > 0) {
			String isPreOrder = selectCaseNoParm.getValue("IS_PRE_ORDER", 0);// 预开检查标记
			String preCaseNo = selectCaseNoParm.getValue("CASE_NO", 0);
			;// 就诊号
			// 查询opd_order表中的数据
			if (isPreOrder.equals("Y")) {
				String slctOpdSql = "SELECT A.ORDER_CODE FROM OPD_ORDER A,REG_PATADM B WHERE "
						+ " AND A.CASE_NO = B.CASE_NO AND A.CASE_NO = '" + preCaseNo
						+ "' AND A.IS_PRE_ORDER = 'Y' AND A.SETMAIN_FLG = 'Y' AND B.SESSION_CODE IS NULL AND B.IS_PRE_ORDER = 'Y' ";
				TParm delOpdParm = new TParm(TJDODBTool.getInstance().select(
						slctOpdSql));
				if (delOpdParm.getCount() == 1
						&& delOpdParm.getValue("ORDER_CODE", 0).equals(
								orderCode)) {// 删除reg_patadm中的数据
					String delRegSql = "DELETE REG_PATADM WHERE CASE_NO = '"
							+ preCaseNo + "'";
					TParm delRegParm = new TParm(TJDODBTool.getInstance()
							.update(delRegSql));
				}
			}
		}
	}
	
	public TParm getCaseNoByOldCaseNo(String preDate, String caseNo, String mrNo){
		String selectRegSql = "";
		if("nullPreDte".equals(preDate)){
			selectRegSql = "SELECT CASE_NO FROM REG_PATADM " +
			"WHERE OLD_CASE_NO = '"+caseNo+"' " +
					"AND ADM_DATE IS NULL " +
					" AND IS_PRE_ORDER ='Y' " +
					" AND REGCAN_USER IS NULL ";
		}else{
			String admDate = preDate.substring(0, 10).replace("-", "").replace("/", "");
			String admDateS = admDate+"0000";
			String admDateE = admDate+"2359";
			//根据病案号和就诊时间查询reg_patadm表，查看挂号信息是否存在
			selectRegSql = "SELECT CASE_NO FROM REG_PATADM " +
			"WHERE MR_NO = '"+mrNo+"' " +
					"AND (APPT_CODE = 'Y' OR APPT_CODE = '' OR APPT_CODE IS NULL) " +
//			        " OLD_CASE_NO = '"+this.caseNo+"' " +
					"AND ADM_DATE BETWEEN TO_DATE('"+admDateS+"','YYYYMMDDHH24MI') " +
					" AND TO_DATE ('"+admDateE+"','YYYYMMDDHH24MI')" +
//					" AND IS_PRE_ORDER ='Y' " +
					" AND REGCAN_USER IS NULL ";
		}
		return new TParm(TJDODBTool.getInstance().select(selectRegSql));
	}
	
	public TParm selectByOrderSetCodeMemPackage(String id, String tradeNo){
		String sql =
			" SELECT ORDERSET_ID," +
			" PACKAGE_CODE," +
			" SECTION_CODE," +
			" ORDERSET_CODE,MR_NO " +
			" FROM MEM_PAT_PACKAGE_SECTION_D" +
			" WHERE ID = '" + id + "' AND TRADE_NO = '" + tradeNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		sql = 
			" SELECT ID, ORDER_CODE, ORDER_NUM, RETAIL_PRICE" +
			" FROM MEM_PAT_PACKAGE_SECTION_D" +
			" WHERE     ORDERSET_ID = '" + parm.getValue("ORDERSET_ID", 0) +"'" +
			" AND PACKAGE_CODE = '" + parm.getValue("PACKAGE_CODE", 0) +"'" +
			" AND SECTION_CODE = '" + parm.getValue("SECTION_CODE", 0) +"'" +
			" AND ORDERSET_CODE = '" + parm.getValue("ORDERSET_CODE", 0) +"'" +
			" AND MR_NO = '" + parm.getValue("MR_NO", 0) +"'" +
			" AND SETMAIN_FLG = 'N'";
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		
		return parm;
	}
	
	public TParm updateMemPackage(String caseNo){
		String sql =
			" SELECT MEM_PACKAGE_ID" +
			" FROM OPD_ORDER" +
			" WHERE CASE_NO = '" + caseNo + "' AND MEM_PACKAGE_ID IS NOT NULL";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm result = new TParm();
		StringBuffer ids = new StringBuffer();
		for (int i = 0; i < parm.getCount(); i++) {
			sql = 
				" UPDATE MEM_PAT_PACKAGE_SECTION_D" +
				" SET CASE_NO = '" + caseNo + "', USED_FLG = '1'" +
						" WHERE ID = '" + parm.getValue("MEM_PACKAGE_ID", i) + "'";
			result = new TParm(TJDODBTool.getInstance().update(sql));
			ids.append("'"+parm.getValue("MEM_PACKAGE_ID", i)+"',");
		}
		String s = ids.toString();
		if(parm.getCount()>0){
			s = s.substring(0, s.length() - 1);
		}else{
			s = "''";
		}
		sql = 
			" SELECT " +
			" TRADE_NO," +
			" PACKAGE_CODE," +
			" SECTION_CODE" +
			" FROM MEM_PAT_PACKAGE_SECTION_D" +
			" WHERE ID in (" + s + ")";
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < parm.getCount(); i++) {
			sql = 
				" UPDATE MEM_PAT_PACKAGE_SECTION" +
				" SET CASE_NO = '"+caseNo+"', USED_FLG = 1" +
				" WHERE TRADE_NO = '"+parm.getValue("TRADE_NO", i) + "' AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", i) + "' AND SECTION_CODE = '"+parm.getValue("SECTION_CODE", i) + "'";
			result = new TParm(TJDODBTool.getInstance().update(sql));
			sql = 
				" UPDATE MEM_PACKAGE_TRADE_M" +
				" SET CASE_NO = '"+caseNo+"', USED_FLG = 1" +
				" WHERE TRADE_NO = '"+parm.getValue("TRADE_NO", i) + "'";
			result = new TParm(TJDODBTool.getInstance().update(sql));
		}
		return result;
	}
	
	public TParm updateMemPackageByDelete(String caseNo, String mrNo){
		String sql =
			" SELECT MEM_PACKAGE_ID, MR_NO" +
			" FROM OPD_ORDER" +
			" WHERE CASE_NO = '" + caseNo + "' AND MEM_PACKAGE_ID IS NOT NULL";
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
		sql = "SELECT ID FROM MEM_PAT_PACKAGE_SECTION_D WHERE CASE_NO = '" + caseNo + "' AND USED_FLG = '1'";
		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < parm2.getCount(); i++) {
			parm2.addData("FLG", "Y");
			for (int j = 0; j < parm1.getCount(); j++) {
				if(parm2.getValue("ID", i).equals(parm1.getValue("MEM_PACKAGE_ID", j))){
					parm2.setData("FLG", i, "N");
				}
			}
		}
		TParm result = new TParm();
		for (int i = 0; i < parm2.getCount(); i++) {
			if(parm2.getBoolean("FLG", i)){
				sql = 
					" UPDATE MEM_PAT_PACKAGE_SECTION_D" +
					" SET CASE_NO = '', USED_FLG = '0'" +
							" WHERE ID = '" + parm2.getValue("ID", i) + "'";
				result = new TParm(TJDODBTool.getInstance().update(sql));
			}
		}
			String mSql =
				" SELECT ID," +
				" TRADE_NO," +
				" PACKAGE_CODE," +
				" SECTION_CODE" +
				" FROM MEM_PAT_PACKAGE_SECTION" +
				" WHERE MR_NO = '" + mrNo + "' AND USED_FLG = 1";
			TParm mParm = new TParm(TJDODBTool.getInstance().select(mSql));
			for (int i = 0; i < mParm.getCount(); i++) {
				String dSql =
					" SELECT COUNT (1) SUM" +
					" FROM MEM_PAT_PACKAGE_SECTION_D" +
					" WHERE     TRADE_NO = '" + mParm.getValue("TRADE_NO", i) + "'" +
					" AND PACKAGE_CODE = '" + mParm.getValue("PACKAGE_CODE", i) + "'" +
					" AND SECTION_CODE = '" + mParm.getValue("SECTION_CODE", i) + "'" +
					" AND USED_FLG = 1";
				TParm dParm = new TParm(TJDODBTool.getInstance().select(dSql));
				if(dParm.getInt("SUM", 0) == 0){
					String updateMSql =
						" UPDATE MEM_PAT_PACKAGE_SECTION" +
						" SET USED_FLG = 0" +
						" WHERE TRADE_NO = '" + mParm.getValue("TRADE_NO", i) + "'" +
						" AND ID = '" + mParm.getValue("ID", i) + "'";
					result = new TParm(TJDODBTool.getInstance().update(updateMSql));
				}
			}
		return result;
	}
	
	public TParm getPreDateByRxNo(TParm exaParm){
		//根据修改的处方签号查询预执行时间
		String rxNo = "";
		for(int i = 0;i < exaParm.getCount();i++){
			//依次取出处方签号
		    rxNo += "'"+exaParm.getValue("RX_NO", i)+"'"+",";
		    if(i == exaParm.getCount()-1){
		    	rxNo += "'"+exaParm.getValue("RX_NO", i)+"'";
		    }
		}
		String selectSql = "SELECT DISTINCT PRE_DATE,CASE_NO FROM OPD_ORDER WHERE "
				+ "RX_NO IN(" + rxNo + ") AND IS_PRE_ORDER = 'Y' ORDER BY PRE_DATE";
		TParm result = new TParm(TJDODBTool.getInstance().select(selectSql));
		return result;
	}
	
	public void deleteRegByMrnoAndPreDate(TParm preDateParm, String mrNo) {
		String preDate = "";
		TParm caseNoParm;
		for (int j = 0; j < preDateParm.getCount(); j++) {
			preDate = preDateParm.getValue("PRE_DATE", j).toString().substring(
					0, 10).replace("/", "").replace("-", "");
			String selectCaseNo = "SELECT DISTINCT CASE_NO FROM OPD_ORDER "
					+ "WHERE MR_NO = '" + mrNo
					+ "' AND PRE_DATE = TO_DATE(" + preDate
					+ ",'YYYYMMDD') ORDER BY CASE_NO";
			caseNoParm = new TParm(TJDODBTool.getInstance()
					.select(selectCaseNo));
			if (caseNoParm.getCount() <= 0) {// 删除修 改前的挂号信息
				String caseNoReg = preDateParm.getValue("CASE_NO", j);
				String deleteReg = "DELETE REG_PATADM WHERE CASE_NO = '"
						+ caseNoReg + "'";
				TJDODBTool.getInstance().update(deleteReg);
			}
		}
	}
	
	public void deleteRegByMrnoAndPreDateForTmpSave(TParm preDateParm,
			String mrNo) {
		String preDate = "";
		TParm caseNoParm;
		for (int j = 0; j < preDateParm.getCount(); j++) {
			String selectCaseNo = "";
			String sPreDate = preDateParm.getValue("PRE_DATE", j).toString();
			if (!"".equals(sPreDate) && !sPreDate.equals(null)) {
				preDate = sPreDate.substring(0, 10).replace("/", "").replace(
						"-", "");
				selectCaseNo = "SELECT DISTINCT CASE_NO FROM OPD_ORDER "
						+ "WHERE MR_NO = '" + mrNo + "' "
						+ "AND PRE_DATE = TO_DATE(" + preDate + ",'YYYYMMDD')"
						+ "ORDER BY CASE_NO";
			} else {
				selectCaseNo = "SELECT DISTINCT CASE_NO FROM OPD_ORDER "
						+ "WHERE MR_NO = '" + mrNo + "' "
						+ "AND IS_PRE_ORDER = 'Y' AND PRE_DATE IS NULL"
						+ "ORDER BY CASE_NO";
			}
			caseNoParm = new TParm(TJDODBTool.getInstance()
					.select(selectCaseNo));
			if (caseNoParm.getCount() <= 0) {// 删除修 改前的挂号信息
				String caseNoReg = preDateParm.getValue("CASE_NO", j);
				String deleteReg = "DELETE REG_PATADM WHERE CASE_NO = '"
						+ caseNoReg + "'";
				TJDODBTool.getInstance().update(deleteReg);
			}
		}
	}
	/**
	 * add caoyong 2014/3/22
	 * 查询过敏药物
	 * @param mrno
	 * @return
	 */
	public TParm getAllergyData(String mrno){
		TParm result= new TParm();
		TParm result1= new TParm();
		//药品过敏
		String  sql ="SELECT A.ALLERGY_NOTE,B.CHN_DESC,C.ORDER_DESC "+
					 "FROM OPD_DRUGALLERGY A, SYS_DICTIONARY B, SYS_FEE C "+
					 "WHERE A.MR_NO='"+mrno+"'  "+
					 "AND A.DRUG_TYPE=B.ID " +
					 "AND B.GROUP_ID='SYS_ALLERGY' "+
					 "AND A.DRUGORINGRD_CODE=C.ORDER_CODE ";
		result = new TParm(TJDODBTool.getInstance()
				.select(sql));
		
		//成分和其他过敏
		String sql1="SELECT A.ALLERGY_NOTE, B.CHN_DESC,C.CHN_DESC AS ORDER_DESC " +
				"FROM OPD_DRUGALLERGY A, "+
                "(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_ALLERGY' ) B , "+
                "(SELECT ID,CHN_DESC, CASE WHEN  GROUP_ID='PHA_INGREDIENT'  THEN 'A' WHEN GROUP_ID='SYS_ALLERGYTYPE' THEN 'C' END DRUG_TYPE FROM SYS_DICTIONARY WHERE GROUP_ID IN ('PHA_INGREDIENT','SYS_ALLERGYTYPE'))C "+
                "WHERE  "+
				"A.DRUG_TYPE=B.ID "+
				"AND A.DRUGORINGRD_CODE=C.ID " +
				"AND A.DRUG_TYPE=C.DRUG_TYPE "+
				"AND  A.MR_NO='"+mrno+"' " +
				" UNION ALL" +
				" SELECT A.ALLERGY_NOTE, B.CHN_DESC, A.DRUGORINGRD_CODE AS ORDER_DESC" +
				" FROM OPD_DRUGALLERGY A, (SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_ALLERGY') B" +
				" WHERE A.DRUG_TYPE = B.ID AND A.DRUG_TYPE='D'" +
				" AND A.MR_NO = '"+mrno+"' " +
				" AND TRIM (A.DRUGORINGRD_CODE) <> '-' ";
//		System.out.println(sql1);
		result1 = new TParm(TJDODBTool.getInstance()
				.select(sql1));
		
		TParm r = new TParm();
		int n = 0;
		for (int i = 0; i < result1.getCount(); i++) {
			if(result1.getValue("ORDER_DESC", i).equals("无")){
				if(n==0){
					r.addRowData(result1, i);
				}
				n++;
			}else{
				r.addRowData(result1, i);
			}
		}
		r.setCount(r.getCount("ORDER_DESC"));
		result1 = r;
		
		if(result.getCount()>0&&result1.getCount()>0){
			result.addParm(result1);
		}else if(result.getCount()<0&&result1.getCount()>0){
			result=result1;
		}
		
		return result;
	}
	/**
	 * 检验检查分类
	 * @return
	 */
	public  TParm getSyscate(String cateGory){
		TParm result= new TParm();
		String  sql ="SELECT CATEGORY_CHN_DESC FROM SYS_CATEGORY WHERE CATEGORY_CODE ='"+cateGory+"'";
		
//		System.out.println("检验检查分类：：：：::::::::::::"+sql);
		result = new TParm(TJDODBTool.getInstance()
				.select(sql));
		return result;
		
	}
	
	/**
	 * 医嘱执行
	 * @param caseNo
	 * @param rxNo
	 * @param seq
	 * @param optUser
	 * @return
	 * @throws Exception
	 */
	public TParm exeOpdrder(String caseNo, String rxNo, int seq, String optUser) throws Exception{
		String sql = getExeOpdrderSql(caseNo, rxNo, seq, optUser);
		return new TParm(TJDODBTool.getInstance().update(sql));
	}
	
	/**
	 * 医嘱执行
	 * @param caseNo
	 * @param rxNo
	 * @param seq
	 * @param optUser
	 * @param tConnection
	 * @return
	 * @throws Exception
	 */
	public TParm exeOpdrder(String caseNo, String rxNo, int seq, String optUser, TConnection tConnection) throws Exception{
		String sql = getExeOpdrderSql(caseNo, rxNo, seq, optUser);
		return new TParm(TJDODBTool.getInstance().update(sql, tConnection));
	}
	
	private String getExeOpdrderSql(String caseNo, String rxNo, int seq, String optUser) throws Exception{
		String sql =
			" UPDATE OPD_ORDER" +
			" SET EXEC_DATE = SYSDATE, EXEC_DR_CODE = '" + optUser + "', EXEC_FLG = 'Y'" +
			" WHERE CASE_NO = '" + caseNo + "' AND RX_NO = '" + rxNo + "' AND SEQ_NO = " + seq;
		return sql;
	}
	
	private String getDeExeOpdrderSql(String caseNo, String rxNo, int seq) throws Exception{
		String sql =
			" UPDATE OPD_ORDER" +
			" SET EXEC_DATE = '', EXEC_DR_CODE = '', EXEC_FLG = 'N'" +
			" WHERE CASE_NO = '" + caseNo + "' AND RX_NO = '" + rxNo + "' AND SEQ_NO = " + seq;
		return sql;
	}
	
	/**
	 * 医嘱取消执行
	 * @param caseNo
	 * @param rxNo
	 * @param seq
	 * @return
	 * @throws Exception
	 */
	public TParm deExeOpdrder(String caseNo, String rxNo, int seq) throws Exception{
		String sql = getDeExeOpdrderSql(caseNo, rxNo, seq);
		return new TParm(TJDODBTool.getInstance().update(sql));
	}
	
	/**
	 * 医嘱取消执行
	 * @param caseNo
	 * @param rxNo
	 * @param seq
	 * @param tConnection
	 * @return
	 * @throws Exception
	 */
	public TParm deExeOpdrder(String caseNo, String rxNo, int seq, TConnection tConnection) throws Exception{
		String sql = getDeExeOpdrderSql(caseNo, rxNo, seq);
		return new TParm(TJDODBTool.getInstance().update(sql, tConnection));
	}
	/**
	 * add caoy执行单
	 * @param caseNo
	 * @param rxNo
	 * @return
	 */
	public TParm getBottleLabel(String caseNo,String rxNo){
		String blttleSql = "SELECT   CASE "
							+ "WHEN A.LINKMAIN_FLG = 'Y' "
							+ "   THEN '主'"
							+ " ELSE '' "
							+ "END FLG, A.LINK_NO, A.ORDER_DESC||' '||'('|| A.SPECIFICATION || ')' AS ORDER_DESC, "
							+ "CASE "
							+ "WHEN "
							+ "A.MEDI_QTY < 1 "
							+ "THEN "
							+ "'0'||A.MEDI_QTY "
							+ "ELSE "
							+ "TO_CHAR(A.MEDI_QTY) END "
							+ "||''||B.UNIT_CHN_DESC AS DISPENSE_QTY, C.ROUTE_CHN_DESC, D.FREQ_CHN_DESC, A.EXEC_DR_DESC, "
							+ " A.EXEC_DATE,A.NS_NOTE,A.DOSE_TYPE,C.CLASSIFY_TYPE "
							+ "FROM OPD_ORDER A, SYS_UNIT B, SYS_PHAROUTE C, SYS_PHAFREQ D "
							+ "WHERE B.UNIT_CODE(+) = A.MEDI_UNIT "
							+ "AND A.DOSE_TYPE IN ('I', 'F') "
							+ "AND C.ROUTE_CODE(+) = A.ROUTE_CODE "
							+ "AND D.FREQ_CODE(+) = A.FREQ_CODE "
							+ "AND A.LINK_NO IS NOT NULL "
							+ "AND A.CASE_NO = '"+caseNo+"' "
							+ "AND A.RX_NO = '"+rxNo+"' "
							+ "ORDER BY A.RX_NO, A.LINK_NO, A.SEQ_NO";
		//System.out.println("blttleSql::::"+blttleSql);
		return new TParm(TJDODBTool.getInstance().select(blttleSql));
	}
	
	/**
	 * add huangjw注射执行单
	 * @param caseNo
	 * @param rxNo
	 * @return
	 */
	public TParm getNewBottleLabel(String caseNo,String rxNo,String presetNo){
		String blttleSql = "SELECT   CASE "
							+ "WHEN A.LINKMAIN_FLG = 'Y' "
							+ "   THEN '主'"
							+ " ELSE '' "
							+ "END FLG, A.LINK_NO, A.ORDER_DESC||' '||'('|| A.SPECIFICATION || ')' AS ORDER_DESC, " 
							+ "CASE "
					        + "WHEN "
					        + "A.MEDI_QTY < 1 "
					        + "THEN "
					        + "'0'||A.MEDI_QTY "
					        + "ELSE "
							+ "TO_CHAR(A.MEDI_QTY) END "
							+ "||''||B.UNIT_CHN_DESC AS DISPENSE_QTY,A.TAKE_DAYS, C.ROUTE_CHN_DESC, D.FREQ_CHN_DESC, E.USER_NAME AS EXEC_DR_DESC, "
							+ " A.EXEC_DATE,A.NS_NOTE,A.DOSE_TYPE,C.CLASSIFY_TYPE "
							+ ",A.DR_NOTE,A.INFLUTION_RATE "
							+ "FROM OPD_ORDER A, SYS_UNIT B, SYS_PHAROUTE C, SYS_PHAFREQ D ,SYS_OPERATOR E "
							+ "WHERE B.UNIT_CODE(+) = A.MEDI_UNIT "
							+ "AND A.DOSE_TYPE IN ('I','F','E','O') "
							+ "AND C.ROUTE_CODE(+) = A.ROUTE_CODE "
							+ "AND E.USER_ID(+) = A.EXEC_DR_DESC "
							+ "AND D.FREQ_CODE(+) = A.FREQ_CODE "
							+ "AND A.CASE_NO = '"+caseNo+"' "
							+ "AND A.RX_NO = '"+rxNo+"' ";
		if(!"".equals(presetNo)&&presetNo!=null){
			blttleSql+= "AND A.PRESRT_NO = '"+presetNo+"' ";
		}else{
			blttleSql+= "AND A.PRESRT_NO IS NULL ";
		}	
		blttleSql+= "ORDER BY A.RX_NO,A.PRESRT_NO, A.LINK_NO, A.SEQ_NO";
//		System.out.println("blttleSql::::"+blttleSql);
		return new TParm(TJDODBTool.getInstance().select(blttleSql));
	}
	
	public List<String> getIndOrgIsExinv(){
		String sql =
			" SELECT ORG_CODE" +
			" FROM IND_ORG" +
			" WHERE ORG_FLG = 'Y' AND ORG_TYPE = 'C' AND EXINV_FLG = 'Y'";
		TParm p = new TParm(TJDODBTool.getInstance().select(sql));
		List<String> l = (List<String>) p.getData("ORG_CODE");
		return l;
	}
	
	public void synOrders(ODO odo){
		
		OpdOrder opdOrder = odo.getOpdOrder();
		
		String sql = 
			" SELECT DISTINCT RX_NO, RX_TYPE" +
			" FROM OPD_ORDER" +
			" WHERE CASE_NO = '"+odo.getCaseNo()+"' AND RX_TYPE IN (1, 2, 3, 4, 5) " +
			" ORDER BY RX_TYPE, RX_NO";
		TParm rxParm = new TParm(TJDODBTool.getInstance().select(sql));
		
		String filter = opdOrder.getFilter();
		
		int rxType;
		String rxNo;
		for (int i = 0; i < rxParm.getCount(); i++) {
			rxType = rxParm.getInt("RX_TYPE", i);
			rxNo = rxParm.getValue("RX_NO", i);
			String filter4Syn;
			switch (rxType) {
			case ODORxMed.MED_INT:
				filter4Syn = "RX_NO='" + rxNo + "'";
				synOrdersByFilter(opdOrder, rxNo, filter4Syn);
				break;

			default:
				break;
			}
			
			
			
			
			
		}
		
		opdOrder.getBuffer(OpdOrder.PRIMARY);
		opdOrder.getNewRows();
		
		opdOrder.setFilter(filter);
		opdOrder.filter();
	}
	
	
	private void synOrdersByFilter(OpdOrder opdOrder, String rxNo, String filter){
		opdOrder.setFilter(filter);
		opdOrder.filter();
		TParm primaryParm = opdOrder.getBuffer(OpdOrder.PRIMARY);
		TParm deleteParm = opdOrder.getBuffer(OpdOrder.DELETE);
		
		int[] newRows = opdOrder.getNewRows();
		int[] modifiedRows = opdOrder.getOnlyModifiedRows(OpdOrder.PRIMARY);
		int deleteCount = opdOrder.getDeleteCount();
		
		System.out.println("primaryParm  "+primaryParm);
		System.out.println("deleteParm  "+deleteParm);
		
		String[] names = primaryParm.getNames();
		
		int count = primaryParm.getCount();
		
		TParm nullRowParm = primaryParm.getRow(count - 1);
		
		opdOrder.deleteRow(count - 1);
		opdOrder.insertRow();
		opdOrder.insertRow();
		for (int j = 0; j < names.length; j++) {
			opdOrder.setItem(count - 1, names[j], primaryParm.getData(names[j], 0), OpdOrder.PRIMARY);
			opdOrder.setItem(count, names[j], nullRowParm.getData(names[j], 0), OpdOrder.PRIMARY);
		}
		opdOrder.setItem(count - 1, "SEQ_NO", 21, OpdOrder.PRIMARY);
		opdOrder.setActive(count - 1, true);
	}
	
	/**
	 * 通过科室获取医嘱用科室大分类
	 * @param deptCode
	 * @return
	 */
	public String getOrderDeptCodeByDept(String deptCode){
		String sql =
			" SELECT ORDER_DEPT_CODE" +
			" FROM SYS_DEPT" +
			" WHERE DEPT_CODE = '" + deptCode + "'";
		TParm r = new TParm(TJDODBTool.getInstance().select(sql));
		if(r.getErrCode() < 0){
			return "";
		}
		if(r.getCount() < 1){
			return "";
		}
		return r.getValue("ORDER_DEPT_CODE", 0);
	}
	
	/**
	 * 通过科室获取诊断用科室大分类
	 * add by huangtt 20150303
	 * @param deptCode
	 * @return
	 */
	public String getDiagDeptCodeByDept(String deptCode){
		String sql =
			" SELECT DIAG_DEPT_CODE" +
			" FROM SYS_DEPT" +
			" WHERE DEPT_CODE = '" + deptCode + "'";
		
		TParm r = new TParm(TJDODBTool.getInstance().select(sql));
		if(r.getErrCode() < 0){
			return "";
		}
		if(r.getCount() < 1){
			return "";
		}
		return r.getValue("DIAG_DEPT_CODE", 0);
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, String> getRouteDoseMap(){
		String sql = "SELECT ROUTE_CODE, CLASSIFY_TYPE FROM SYS_PHAROUTE";
		TParm r = new TParm(TJDODBTool.getInstance().select(sql));
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < r.getCount(); i++) {
			if(r.getValue("CLASSIFY_TYPE", i).length() > 0){
				map.put(r.getValue("ROUTE_CODE", i), r.getValue("CLASSIFY_TYPE", i));
			}else{
				map.put(r.getValue("ROUTE_CODE", i), "#");
			}
		}
		return map;
	}
	
	
	
	
	
	
}
