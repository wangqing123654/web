package jdo.iva;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 静配中心成品审核工具类
 * </p>
 * 
 * <p>
 * Description: 静配中心成品审核工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2013.07.21
 * @version 1.0
 */
public class IVAAllocatecheckTool extends TJDODBTool {

	/**
	 * 实例
	 */
	public static IVAAllocatecheckTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return IndAgentTool
	 */
	public static IVAAllocatecheckTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new IVAAllocatecheckTool();
		}
		return instanceObject;
	}

	/*
	 * 查询未调配信息
	 */
	public TParm queryInfo(TParm parm) {
		String sql = "SELECT C.STATION_DESC,"
		         			+" B.PAT_NAME,"
		         			+" A.CASE_NO,"
					        +" A.MR_NO, "
					        +" D.IVA_CHECK_USER "
					 +" FROM ODI_DSPND D,"
					 		+" ODI_DSPNM A, "
					        +" SYS_PATINFO B,"
					        +" SYS_STATION C "
					 +" WHERE D.BAR_CODE = '"+parm.getValue("BAR_CODE")
							 +"' AND D.IVA_DEPLOY_USER IS NOT NULL "
//							 +" AND D.IVA_CHECK_USER IS NULL "
							 +" AND D.IVA_RETN_USER IS NULL "
							 + " AND D.IVA_FLG = 'Y' "
							 +" AND A.CASE_NO = D.CASE_NO "
							 +" AND A.ORDER_NO = D.ORDER_NO "
							 +" AND A.ORDER_SEQ = D.ORDER_SEQ "
							 +" AND D.ORDER_DATE || D.ORDER_DATETIME BETWEEN A.START_DTTM "
							 											+" AND A.END_DTTM "
					 		+" AND A.IVA_FLG = 'Y' "
					 		+ " AND A.ORDER_CAT1_CODE  IN ('PHA_W','PHA_C') "
					 		+" AND A.MR_NO = B.MR_NO "
					 		+" AND A.STATION_CODE = C.STATION_CODE "
		             +" GROUP BY C.STATION_DESC,"
		             			+" B.PAT_NAME,"
		             			+" A.CASE_NO,"
		             			+" A.MR_NO,"
		             			+" D.IVA_CHECK_USER";
		System.out.println("case_no"+sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/*
	 * 患者药品明细
	 */
	public TParm querydetail(TParm tparm) {
		String sql = "SELECT  A.LINKMAIN_FLG AS SELECT_FLG,A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,"
				+ " A.MEDI_QTY || E.UNIT_CHN_DESC AS MEDI_QTY,B.DOSAGE_QTY || F.UNIT_CHN_DESC AS DOSAGE_QTY, "
				+ "B.ORDER_DATE || B.ORDER_DATETIME AS EXEC_DATE,"
				+ "C.FREQ_CHN_DESC,A.FREQ_CODE,D.ROUTE_CHN_DESC,A.LINK_NO,B.BAR_CODE "
				+ "FROM ODI_DSPNM A,ODI_DSPND B,SYS_PHAFREQ C,SYS_PHAROUTE D,SYS_UNIT E,SYS_UNIT F "
				+ "WHERE  A.CASE_NO='"
				+ tparm.getValue("CASE_NO")
				+ "' AND A.IVA_FLG = 'Y' "
				+ " AND A.ORDER_CAT1_CODE  IN ('PHA_W','PHA_C') "
				+ " AND A.CASE_NO=B.CASE_NO "
				+ " AND A.ORDER_NO=B.ORDER_NO "
				+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
				+ " AND B.ORDER_DATE || B.ORDER_DATETIME  BETWEEN  A.START_DTTM AND  A.END_DTTM "
				+ " AND B.BAR_CODE = '"+tparm.getValue("BAR_CODE")
				+ "' AND B.IVA_DEPLOY_USER IS NOT NULL "
				+ " AND B.IVA_CHECK_USER IS NULL "
				+ " AND A.FREQ_CODE=C.FREQ_CODE AND A.ROUTE_CODE=D.ROUTE_CODE AND A.MEDI_UNIT=E.UNIT_CODE "
				+ " AND B.DOSAGE_UNIT = F.UNIT_CODE "
				+ " ORDER BY B.ORDER_DATE,B.ORDER_DATETIME, CASE WHEN A.LINKMAIN_FLG = 'Y' THEN '1' ELSE '2' END";
		System.out.println("sql========"+sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/*
	 * 成品核对
	 */
	public TParm updateInfoM(TParm tparm, TConnection conn) {

		String sql = "UPDATE ODI_DSPNM SET IVA_CHECK_USER = '"
				+ tparm.getValue("IVA_CHECK_USER") + "', "
				+ " IVA_CHECK_TIME=SYSDATE WHERE CASE_NO='"
				+ tparm.getValue("CASE_NO")
				+ "' AND ORDER_NO = '"
				+ tparm.getValue("ORDER_NO")
				+ "' AND ORDER_SEQ = '"
				+ tparm.getValue("ORDER_SEQ")
				+ "' AND START_DTTM = '"
				+ tparm.getValue("START_DTTM")
				+ "' AND END_DTTM = '"
				+ tparm.getValue("END_DTTM")
				+ "' AND IVA_FLG='Y' AND IVA_DEPLOY_USER IS NOT NULL";
//		System.out.println("sqlM=========="+sql);
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	public TParm updateInfoD(TParm tparm, TConnection conn) {
		String sql = "UPDATE ODI_DSPND SET IVA_CHECK_USER='"
				+ tparm.getValue("IVA_CHECK_USER") + "',"
				+ "IVA_CHECK_TIME=SYSDATE WHERE CASE_NO='"
				+ tparm.getValue("CASE_NO")
				+ "' AND ORDER_NO='"+ tparm.getValue("ORDER_NO")
				+ "' AND ORDER_SEQ='"+ tparm.getValue("ORDER_SEQ")
				+ "' AND ORDER_DATE = '"+ tparm.getValue("ORDER_DATE")
				+ "' AND BATCH_CODE = '"+ tparm.getValue("BATCH_CODE")
				+ "' AND BAR_CODE = '"+ tparm.getValue("BAR_CODE")
				+ "' AND ORDER_DATETIME = '"+ tparm.getValue("ORDER_DATETIME")
				+ "' AND IVA_FLG='Y' AND IVA_DEPLOY_USER IS NOT NULL "
				+ " AND IVA_CHECK_USER IS NULL";
//		System.out.println("sqlD=========="+sql);
		return new TParm(TJDODBTool.getInstance().update(sql));

	}
}
