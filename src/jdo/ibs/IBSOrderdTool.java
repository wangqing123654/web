package jdo.ibs;

import jdo.sys.Operator;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * 
 * <p>
 * Title: סԺ������ϸ��������
 * </p>
 * 
 * <p>
 * Description: סԺ������ϸ��������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangl
 * @version 1.0
 */
public class IBSOrderdTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static IBSOrderdTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return IBSOrderdTool
	 */
	public static IBSOrderdTool getInstance() {
		if (instanceObject == null)
			instanceObject = new IBSOrderdTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public IBSOrderdTool() {
		setModuleName("ibs\\IBSOrderdModule.x");
		onInit();
	}

	/**
	 * ��ѯδ�����˵��ķ��õ�����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectdata(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selectdate()�����쳣!");
			return result;
		}
		result = query("selectdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * ��ѯ�վ���Ŀ(���ò�ѯ)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectdataAll(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selectdataAll()�����쳣!");
			return result;
		}
		result = query("selectdataAll", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * ��ѯ������ϸ(���ò�ѯ)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selFeeDetail(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selFeeDetail()�����쳣!");
			return result;
		}
		result = query("selFeeDetail", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}
	
	
	/**
	 * ��ѯ������ϸ
	 * @param parm
	 *            TParm
	 * @return TParm
	 * 2016.9.23 zl
	 */
	public TParm onQueryFYMX(TParm parm){
		TParm result=this.query("onQueryFYMX", parm);
    	return result;
		
	}
	
	
	
	

	/**
	 * ��ѯ�Ƽ���ϸ(���ò�ѯ)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selBillDetail(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selBillDetail()�����쳣!");
			return result;
		}
		result = query("selBillDetail", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * ��ѯ����ҽ��(���ò�ѯ)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selSingleOrder(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selSingleOrder()�����쳣!");
			return result;
		}
		result = query("selSingleOrder", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * ��ѯ�ϲ�������ϸ(���ò�ѯ)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selMergeFee(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selMergeFee()�����쳣!");
			return result;
		}
		result = query("selMergeFee", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * ���ҷ��ò�ѯ(���ò�ѯ)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selFeeByDept(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selFeeByDept()�����쳣!");
			return result;
		}
		result = query("selFeeByDept", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * ��ѯδ�����˵��ķ�����ϸ����������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selOrderDAll(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selOrderDAll()�����쳣!");
			return result;
		}
		result = query("selOrderDAll", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}
	/**
	 * ��ѯ���ܽ��
	 * @param parm
	 * @return
	 */
	public TParm onQueryIbsOrddSumTotAmt(TParm parm){
		TParm result = new TParm();
		result = query("onQueryIbsOrddSumTotAmt", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ��ѯ������Сҽ����Ч��ʱ,�����Ч��ʱ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selOrderTime(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selOrderTime()�����쳣!");
			return result;
		}
		result = query("selOrderTime", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * ����
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertdata(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertDdata", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm CASE_NO,CASE_NO_SEQ,SEQ_NO
	 */
	public TParm deletedata(TParm parm) {
		TParm result = new TParm();
		result = update("deletedata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯcase_no����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selCaseNoCount(TParm parm) {
		TParm result = new TParm();
		result = query("selCaseNoCount", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ�˵��������(�˵����)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selAuditFeeData(TParm parm) {
		TParm result = new TParm();
		result = query("selAuditFeeData", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �����˵�����
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateBillNO(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateBillNO", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ����������Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selPatOrderData(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selPatOrderData()�����쳣!");
			return result;
		}
		result = query("selPatOrderData", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * ���ұ�ҩ����
	 * 
	 * @param caseNo
	 *            String
	 * @param caseNoSeq
	 *            int
	 * @param seqNo
	 *            int
	 * @param requestFlg
	 *            String
	 * @param requestNo
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm upForDeptMedic(String caseNo, int caseNoSeq, int seqNo,
			String requestFlg, String requestNo, TConnection conn) {
		TParm parm = new TParm();
		TParm result = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("CASE_NO_SEQ", caseNoSeq);
		parm.setData("SEQ_NO", seqNo);
		parm.setData("REQUEST_FLG", requestFlg);
		parm.setData("REQUEST_NO", requestNo);
		result = this.update("upForDeptMedic", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	// �����վݷ��ò�ѯ��������(FOR ҽ��ͳ��)
	public TParm selRexpCodePatFee(String caseNo) {
		TParm result = new TParm();
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		result = query("selRexpCodePatFee", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ������Ӧҩ��,ҩƷ����(For UDD)
	 * 
	 * @param caseNo
	 *            String
	 * @param orderCode
	 *            String
	 * @param exeDeptCode
	 *            String
	 * @return TParm
	 */
	public TParm checkIBSQutryForUDD(String caseNo, String orderCode,
			String exeDeptCode) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("EXE_DEPT_CODE", exeDeptCode);
		TParm result = new TParm();
		result = query("checkIBSQutryForUDD", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ɾ����������(����)
	 * 
	 * @param caseNo
	 *            String
	 * @param billDate
	 *            String
	 * @param caseNoSeq
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm deleteOrderDPatch(String caseNo, String billDate,
			String caseNoSeq, TConnection conn) {
		TParm parm = new TParm();
		TParm result = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("S_DATE", billDate + "000000");
		parm.setData("E_DATE", billDate + "235959");
		parm.setData("CASE_NO_SEQ", caseNoSeq);
		result = update("deleteOrderDPatch", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ�����˵�����(�����˵�)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selBillReturnD(TParm parm) {
		TParm result = new TParm();
		result = query("selBillReturnD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ���δ�ɾ������
	 * 
	 * @param caseNo
	 *            String
	 * @param billDate
	 *            String
	 * @param caseNoSeq
	 *            String
	 * @return TParm
	 */
	public TParm seldelOrderDPatch(String caseNo, String billDate,
			String caseNoSeq) {
		TParm parm = new TParm();
		TParm result = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("S_DATE", billDate + "000000");
		parm.setData("E_DATE", billDate + "235959");
		parm.setData("CASE_NO_SEQ", caseNoSeq);
		result = query("seldelOrderDPatch", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ����������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selMaxSeq(TParm parm) {
		TParm result = new TParm();
		result = query("selMaxSeq", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ��ͬ���������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm =======pangben 2011-11-16
	 */
	public TParm selDifferentSeq(TParm parm) {
		TParm result = new TParm();
		result = query("selDifferentSeq", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���ҷ��ò�ѯ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm =======wangzl 2012-08-07
	 */
	public TParm selDeptFee(TParm parm) {
		String COST_CENTER_CODEWhere = "";
		String CAT1_CODEWHERE = "";
		String PHA_BASEFrom = "";
		String CTRLDRUGCLASS_codeWhere = "";
		if (parm.getData("cost_center_code").toString().length() != 0) {
			COST_CENTER_CODEWhere = " AND D.EXE_DEPT_CODE ='"
					+ parm.getData("cost_center_code") + "'";
		}
		if (parm.getData("order_cat1_code") != null
				&& parm.getData("order_cat1_code").toString().length() != 0) {
			CAT1_CODEWHERE = " AND D.ORDER_CAT1_CODE='"
					+ parm.getData("order_cat1_code") + "'";
		}
		if (parm.getData("ctfig").toString()!= "") {//===modify by caowl 20120814
			PHA_BASEFrom = " PHA_BASE  P,SYS_CTRLDRUGCLASS C,";
			CTRLDRUGCLASS_codeWhere = " AND P.ORDER_CODE = D.ORDER_CODE AND " +
					"C.CTRLDRUGCLASS_CODE = P.CTRLDRUGCLASS_CODE AND C.CTRL_FLG='"+parm.getData("ctfig")+"'";//==modify by caowl 20120821
		}
		String sql = "SELECT D.BILL_DATE,S.ORDER_DESC"
				+ "        || CASE WHEN S.SPECIFICATION IS NOT NULL THEN"
				+ "         '(' || S.SPECIFICATION || ')'" + " ELSE" + " ''"
				+ "        END AS ORDER_DESC," 
				+"S.ORDER_DESC AS ORDER_DESC1,S.SPECIFICATION,"
				+ " D.OWN_FLG, D.DOSAGE_QTY,"
				+          "D.OWN_PRICE, D.OWN_AMT,D.TOT_AMT, D.OPT_DATE,"
				+          "U.USER_NAME, D.INDV_FLG, D.ORDER_CODE,"
				+          "D.DOSAGE_UNIT, D.ORDERSET_CODE, D.CASE_NO_SEQ ,X.COST_CENTER_CHN_DESC,"//===modify by caowl 20120814
				+          "D.OWN_RATE AS ZHEKOU" //" (D.TOT_AMT/D.DOSAGE_QTY/D.OWN_PRICE) AS ZHEKOU" // zhanglei 20170427 ����ۿ۱��
				+ " FROM IBS_ORDD  D,"
				+ PHA_BASEFrom 
				+        " SYS_FEE S, "
				+        "SYS_OPERATOR  U,SYS_COST_CENTER X" //====modify by caowl 20120814
				+ " WHERE D.CASE_NO = '"+ parm.getData("CASE_NO") + "'" 
				+ 		  COST_CENTER_CODEWhere
				+ 		  " AND d.bill_date >= TO_DATE('" + parm.getData("START_DATE")
				+ 		  "', 'YYYYMMDD HH24:MI:SS')" 
				+         " AND d.bill_date <= TO_DATE('"
				+         parm.getData("END_DATE") + "', 'YYYYMMDD HH24:MI:SS')"
				+         CAT1_CODEWHERE + CTRLDRUGCLASS_codeWhere
				+         " AND S.ORDER_CODE = D.ORDER_CODE"
				+         " AND U.USER_ID = D.OPT_USER"
				+         " AND D.EXE_DEPT_CODE = X.COST_CENTER_CODE "//===modify by caowl 20120814
				+ " ORDER BY D.CASE_NO_SEQ, D.SEQ_NO";
		System.out.println("==" + sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}
	
	/**
	 * ���ҷ��û���
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm =======zhanglei 2017-06-28
	 */
	public TParm selDeptFeeSum(TParm parm) {
		//System.out.println("2222222222222222" + parm);
		String COST_CENTER_CODEWhere = "";
		String CAT1_CODEWHERE = "";
		String PHA_BASEFrom = "";
		String CTRLDRUGCLASS_codeWhere = "";
		if (parm.getData("cost_center_code").toString().length() != 0) {
			COST_CENTER_CODEWhere = " AND D.EXE_DEPT_CODE ='"
					+ parm.getData("cost_center_code") + "'";
		}
		if (parm.getData("order_cat1_code") != null
				&& parm.getData("order_cat1_code").toString().length() != 0) {
			CAT1_CODEWHERE = " AND D.ORDER_CAT1_CODE='"
					+ parm.getData("order_cat1_code") + "'";
		}
		if (parm.getData("ctfig").toString()!= "") {//===modify by caowl 20120814
			PHA_BASEFrom = " PHA_BASE  P,SYS_CTRLDRUGCLASS C,";
			CTRLDRUGCLASS_codeWhere = " AND P.ORDER_CODE = D.ORDER_CODE AND " +
					"C.CTRLDRUGCLASS_CODE = P.CTRLDRUGCLASS_CODE AND C.CTRL_FLG='"+parm.getData("ctfig")+"'";//==modify by caowl 20120821
		}
		//20170627 zhanglei ����
//		������ţ������ۺϲ���   ����ˣ�������
//		סԺ�շѡ����ò�ѯ�����У������ҷ��ò�ѯ��ѡ������ɱ����Ļ��ܣ����ؼƼ����ڡ��������ڡ�������Ա��Ϣ����ʾ��ͬһ��Ŀ���мӺͻ��ܡ�
//		ע��ͬʱ������ӡ���档

		String sql = "SELECT "
            + "S.ORDER_DESC "
            + "        || CASE WHEN S.SPECIFICATION IS NOT NULL THEN"
			+ "         '(' || S.SPECIFICATION || ')'" + " ELSE" + " ''"
			+ "        END AS ORDER_DESC, " 
			+ "S.ORDER_DESC AS ORDER_DESC1, "
			+ "D.OWN_FLG, "
			+ "SUM(D.DOSAGE_QTY) AS DOSAGE_QTY, "
			+ "D.OWN_PRICE, "
			+ "SUM(D.TOT_AMT) AS TOT_AMT, "
			+ "D.ORDER_CODE, "
			+ "D.DOSAGE_UNIT, " 
			+ "X.COST_CENTER_CHN_DESC, "
			+ "D.OWN_RATE AS ZHEKOU "      
			+ "FROM IBS_ORDD D, "
			+ "SYS_FEE S, "
			+ "SYS_OPERATOR U, "
			+ "SYS_COST_CENTER X "
			+ " WHERE D.CASE_NO = '"+ parm.getData("CASE_NO") + "'" 
			+ 		  COST_CENTER_CODEWhere
			+ 		  " AND d.bill_date >= TO_DATE('" + parm.getData("START_DATESUM")
			+ 		  "', 'YYYYMMDD HH24:MI:SS')" 
			+         " AND d.bill_date <= TO_DATE('"
			+         parm.getData("END_DATESUM") + "', 'YYYYMMDD HH24:MI:SS')"
			+         CAT1_CODEWHERE + CTRLDRUGCLASS_codeWhere
			+         " AND S.ORDER_CODE = D.ORDER_CODE"
			+         " AND U.USER_ID = D.OPT_USER"
			+         " AND D.EXE_DEPT_CODE = X.COST_CENTER_CODE "//===modify by caowl 20120814
			+ " GROUP BY X.COST_CENTER_CHN_DESC,S.ORDER_DESC,S.SPECIFICATION,D.OWN_PRICE,D.ORDER_CODE," +
					"D.DOSAGE_UNIT,X.COST_CENTER_CHN_DESC,D.OWN_RATE,D.OWN_FLG "
			+ " ORDER BY X.COST_CENTER_CHN_DESC,TOT_AMT";
		//System.out.println("1111111111111" + sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			//System.out.println("1111111111111" + result);
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ʼ���ɱ�����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm =======wangzl 2012-08-07
	 */
	public TParm initCostCenterCode() {
		String COST_CENTER_CODE = Operator.getCostCenter(); // �õ���ǰ��¼user �ĳɱ�����
		String sql = "SELECT IPD_FIT_FLG,COST_CENTER_CODE,COST_CENTER_CHN_DESC "
				+ "FROM SYS_COST_CENTER WHERE COST_CENTER_CODE='"
				+ COST_CENTER_CODE + "'"; // ץȡIPD_FIT_FLG(���ҳɱ�����fig)
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ɾ����������(����)
	 * 
	 * @param caseNo
	 *            String
	 * @param billDate
	 *            String
	 * @param caseNoSeq
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm deleteOrderDPatchSum(String caseNo, String billDate,
			String caseNoSeq, TConnection conn) {
		TParm result = new TParm();
		String sql="DELETE IBS_ORDD WHERE CASE_NO = '"+caseNo
		+"' AND BILL_DATE >= TO_DATE('"+billDate 
		+ "000000', 'yyyyMMddhh24miss') AND BILL_DATE <= TO_DATE('"+billDate +
		"235959', 'yyyyMMddhh24miss') AND CASE_NO_SEQ IN ("+caseNoSeq+")";
		result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ��ѯ���δ�ɾ������===pangben 2015-11-9
	 * 
	 * @param caseNo
	 *            String
	 * @param billDate
	 *            String
	 * @param caseNoSeq
	 *            String
	 * @return TParm
	 */
	public TParm seldelOrderDPatchSum(String caseNo, String billDate,
			String caseNoSeq) {
		TParm result = new TParm();
		String sql= "SELECT SUM(TOT_AMT) AS TOT_AMT FROM IBS_ORDD WHERE CASE_NO = '"+caseNo
		+"' AND BILL_DATE >= TO_DATE('"+billDate
		+ "000000', 'yyyyMMddhh24miss') AND BILL_DATE <= TO_DATE('"+billDate + 
		"235959', 'yyyyMMddhh24miss') AND CASE_NO_SEQ IN ("+caseNoSeq+")";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ����ٴ�·��У��
	 * @param parm
	 * @return
	 */
	public TParm selClpClncpathCode(TParm parm){
		TParm result = new TParm();
		result = query("selClpClncpathCode", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ·������У���Ƿ���ڿյ�·��ʱ��
	 * @param parm
	 * @return
	 */
	public TParm selClpSchdCode(TParm parm){
		TParm result = new TParm();
		result = query("selClpSchdCode", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
