package jdo.opb;

import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

public class OPDFeeListPrintTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static OPDFeeListPrintTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INVVerifyinTool
	 */
	public static OPDFeeListPrintTool getInstance() {
		if (instanceObject == null)
			instanceObject = new OPDFeeListPrintTool();
		return instanceObject;
	}

	/**
	 * ���ػ����嵥��������/סԺ��Ӣ���嵥
	 * 
	 * @param parm
	 *            TParm ====pangben 2015-7-22
	 * @return TParm
	 */
	public TParm getSumPrintData(String caseNo, String optUser, String admType) {
		String regSql ="";
		String sql="";
		if (admType.equals("O")) {
			regSql = "SELECT B.PAT_NAME,B.MR_NO,B.BIRTH_DATE,MAX(C.BILL_DATE) BILL_DATE,E.CONTRACTOR_DESC AS INSURANCE_COMPANY,A.ADM_TYPE "
					+ "FROM REG_PATADM A,SYS_PATINFO B,BIL_OPB_RECP C,MEM_INSURE_INFO D,MEM_CONTRACTOR E"
					+ " WHERE A.CASE_NO=C.CASE_NO AND A.MR_NO=B.MR_NO AND A.MR_NO=D.MR_NO(+) "
					+ "AND D.CONTRACTOR_CODE=E.CONTRACTOR_CODE(+) AND A.CASE_NO='"
					+ caseNo
					+ "'"
					+ " GROUP BY B.PAT_NAME,B.MR_NO,B.BIRTH_DATE,E.CONTRACTOR_DESC,A.ADM_TYPE ";
			sql = "SELECT ORDER_CODE,ORDER_DESC,SPECIFICATION,DOSAGE_QTY,DOSAGE_UNIT,OWN_AMT, AR_AMT,"
					+ "REXP_CODE,HEXP_CODE,CHN_DESC,UNIT_CHN_DESC "
					+ " FROM ( SELECT   A.ORDER_CODE,"
					+ " D.TRADE_ENG_DESC AS ORDER_DESC,'' SPECIFICATION,"
					+ " SUM(A.DOSAGE_QTY) DOSAGE_QTY,A.DOSAGE_UNIT,SUM(A.OWN_AMT) OWN_AMT,SUM(A.AR_AMT) AR_AMT,"
					+ " A.REXP_CODE, A.HEXP_CODE,C.ENG_DESC AS CHN_DESC,B.UNIT_ENG_DESC AS UNIT_CHN_DESC "
					+ " FROM OPD_ORDER A,SYS_DICTIONARY C,"
					+ " SYS_FEE D,SYS_UNIT B"
					+ " WHERE A.ORDER_CODE = D.ORDER_CODE AND  A.DOSAGE_UNIT = B.UNIT_CODE(+) "
					+ " AND a.REXP_CODE = C.ID AND C.GROUP_ID = 'SYS_CHARGE'"
					+ " AND A.CASE_NO = '"
					+ caseNo
					+ "' AND A.BILL_FLG='Y' AND A.RECEIPT_NO IS NOT NULL AND A.OWN_AMT<>0 "
					+ " GROUP BY A.ORDER_CODE,D.TRADE_ENG_DESC,A.DOSAGE_UNIT,A.REXP_CODE, A.HEXP_CODE,C.ENG_DESC,B.UNIT_ENG_DESC "
					+ " UNION ALL SELECT  A.ORDER_CODE,"
					+ " A.ORDER_DESC,'' SPECIFICATION, 0 AS DOSAGE_QTY,"
					+ " A.DOSAGE_UNIT ,0 OWN_AMT,0 AR_AMT,"
					+ " A.REXP_CODE, A.HEXP_CODE,C.CHN_DESC,B.UNIT_CHN_DESC "
					+ " FROM OPD_ORDER A,SYS_DICTIONARY C,"
					+ " SYS_FEE D,SYS_UNIT B"
					+ " WHERE A.ORDER_CODE = D.ORDER_CODE AND  A.DOSAGE_UNIT = B.UNIT_CODE(+) "
					+ " AND a.REXP_CODE = C.ID AND C.GROUP_ID = 'SYS_CHARGE'"
					+ " AND A.CASE_NO = '"
					+ caseNo
					+ "' AND A.BILL_FLG='Y' AND A.RECEIPT_NO IS NOT NULL AND A.OWN_AMT<>0"
					+ " GROUP BY A.ORDER_CODE, A.ORDER_DESC, A.DOSAGE_UNIT, A.REXP_CODE, A.HEXP_CODE,C.CHN_DESC,B.UNIT_CHN_DESC ) ORDER BY  REXP_CODE,ORDER_CODE,DOSAGE_QTY DESC";
		}else{
			regSql = "SELECT B.PAT_NAME,B.MR_NO,B.BIRTH_DATE,MAX(C.CHARGE_DATE) BILL_DATE,E.CONTRACTOR_DESC AS INSURANCE_COMPANY "
				+ "FROM ADM_INP A,SYS_PATINFO B,BIL_IBS_RECPM C,MEM_INSURE_INFO D,MEM_CONTRACTOR E"
				+ " WHERE A.CASE_NO=C.CASE_NO(+) AND A.MR_NO=B.MR_NO AND A.MR_NO=D.MR_NO(+) "
				+ "AND D.CONTRACTOR_CODE=E.CONTRACTOR_CODE(+) AND A.CASE_NO='"
				+ caseNo
				+ "'"
				+ " GROUP BY B.PAT_NAME,B.MR_NO,B.BIRTH_DATE,E.CONTRACTOR_DESC ";
			sql = "SELECT ORDER_CODE,ORDER_DESC,SPECIFICATION,DOSAGE_QTY,DOSAGE_UNIT,OWN_AMT,AR_AMT,"
				+ "REXP_CODE,HEXP_CODE,CHN_DESC,UNIT_CHN_DESC "
				+ " FROM ( SELECT   A.ORDER_CODE,"
				+ " D.TRADE_ENG_DESC AS ORDER_DESC,'' SPECIFICATION, SUM(A.DOSAGE_QTY) DOSAGE_QTY,"
				+ " A.DOSAGE_UNIT , SUM(A.OWN_AMT) OWN_AMT, SUM(A.TOT_AMT) AS AR_AMT,"
				+ " A.REXP_CODE, A.HEXP_CODE,C.ENG_DESC AS CHN_DESC,B.UNIT_ENG_DESC AS UNIT_CHN_DESC "
				+ " FROM IBS_ORDD A,SYS_DICTIONARY C,"
				+ " SYS_FEE D,SYS_UNIT B"
				+ " WHERE A.ORDER_CODE = D.ORDER_CODE AND  A.DOSAGE_UNIT = B.UNIT_CODE(+) "
				+ " AND a.REXP_CODE = C.ID AND C.GROUP_ID = 'SYS_CHARGE'"
				+ " AND A.CASE_NO = '"
				+ caseNo
				+ "' AND A.BILL_FLG='Y' AND A.OWN_AMT<>0 "
				+ " GROUP BY A.ORDER_CODE,D.TRADE_ENG_DESC,A.DOSAGE_UNIT,A.REXP_CODE, A.HEXP_CODE,C.ENG_DESC,B.UNIT_ENG_DESC "
				+ " UNION ALL SELECT  A.ORDER_CODE,"
				+ " A.ORDER_CHN_DESC ORDER_DESC,'' SPECIFICATION, 0 AS DOSAGE_QTY,"
				+ " A.DOSAGE_UNIT ,0 OWN_AMT,0 AR_AMT,"
				+ " A.REXP_CODE, A.HEXP_CODE,C.CHN_DESC,B.UNIT_CHN_DESC "
				+ " FROM IBS_ORDD A,SYS_DICTIONARY C,"
				+ " SYS_FEE D,SYS_UNIT B"
				+ " WHERE A.ORDER_CODE = D.ORDER_CODE AND  A.DOSAGE_UNIT = B.UNIT_CODE(+) "
				+ " AND a.REXP_CODE = C.ID AND C.GROUP_ID = 'SYS_CHARGE'"
				+ " AND A.CASE_NO = '"
				+ caseNo
				+ "' AND A.BILL_FLG='Y' AND A.OWN_AMT<>0"
				+ " GROUP BY A.ORDER_CODE,A.ORDER_CHN_DESC, A.DOSAGE_UNIT,A.REXP_CODE, A.HEXP_CODE,C.CHN_DESC,B.UNIT_CHN_DESC ) ORDER BY  REXP_CODE,ORDER_CODE,DOSAGE_QTY DESC";
		}
		TParm regParm = new TParm(TJDODBTool.getInstance().select(regSql));
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm result = new TParm();
		String colunmName = "REXP_CODE";// ��¼ָ��������
		String descName = "CHN_DESC"; // �վݷ������ĵ�����
		DecimalFormat df = new DecimalFormat("0.00");
		DecimalFormat df1 = new DecimalFormat("0.0");
		String code = ""; // ��¼ÿ�����ݵ� �վ����ʹ��� ���� Ժ�ڷ��ô���
		double tot = 0; // ��¼�ܼ۸� ʵ��
		double own = 0; // ��¼�ܼ۸� Ӧ�� liling add
		int count = parm.getCount();
		int printCount = 0;
		// modify by liming 2012/03/06 begin
		String orderCode = "";
		String orderDesc = "";
		double dosageQty = 0;// �洢����
		double ownAmt = 0;// ===liling add Ӧ�ս��
		double totAmt = 0;// ʵ�ս��
		String feeTypeDesc = "";
		String cName = "";
		String specification = "";
		String unitChnDesc = "";
		String dosageUnit = "";

		double totDay = 0;
		double ownDay = 0; // ==liling add
		double outTotFee = 0;
		double outOwnFee = 0;
		if (parm.getCount() > 0) {
			// ��ʼ����һ��
			orderCode = parm.getValue("ORDER_CODE", 0);
			feeTypeDesc = parm.getValue(descName, 0);
			cName = parm.getValue(colunmName, 0);
			specification = parm.getValue("SPECIFICATION", 0);
			orderDesc = parm.getValue("ORDER_DESC", 0);
			unitChnDesc = parm.getValue("UNIT_CHN_DESC", 0);
			dosageQty = Double.valueOf(df1.format(parm
					.getDouble("DOSAGE_QTY", 0)));
			ownAmt = Double.valueOf(df.format(parm.getDouble("OWN_AMT", 0)));// ==liling
			// add
			// Ӧ�ս��
			totAmt = Double.valueOf(df.format(parm.getDouble("AR_AMT", 0)));
			dosageUnit = parm.getValue("DOSAGE_UNIT", 0);

			// �������һ��
			parm.addData(descName, "");
			parm.addData("SPECIFICATION", "");
			parm.addData("ORDER_DESC", "");
			parm.addData("UNIT_CHN_DESC", "");
			parm.addData("DOSAGE_QTY", 0);
			parm.addData("OWN_AMT", 0);// ==liling add
			parm.addData("AR_AMT", 0);
		}

		TParm tempParm = new TParm();

		for (int i = 1; i < parm.getCount("ORDER_DESC"); i++) {
			if (orderCode.equals(parm.getValue("ORDER_CODE", i))
					&& dosageUnit.equals(parm.getValue("DOSAGE_UNIT", i))
					&& orderDesc.equals(parm.getValue("ORDER_DESC", i))) {// ������ִ�п��Ҳ�ͬ�����ֻͳ��ҽ����ͬ���ۼӺϲ�
				dosageQty += Double.valueOf(df1.format(parm.getDouble(
						"DOSAGE_QTY", i)));
				ownAmt += Double.valueOf(df
						.format(parm.getDouble("OWN_AMT", i)));// ==liling add
				totAmt += Double
						.valueOf(df.format(parm.getDouble("AR_AMT", i)));
			} else {
				tempParm.addData(descName, feeTypeDesc);
				tempParm.addData(colunmName, cName);
				tempParm.addData("SPECIFICATION", specification);
				tempParm.addData("ORDER_DESC", orderDesc);
				tempParm.addData("DOSAGE_QTY", dosageQty);
				tempParm.addData("UNIT_CHN_DESC", unitChnDesc);
				tempParm.addData("OWN_AMT", ownAmt);
				tempParm.addData("AR_AMT", totAmt);
				orderCode = parm.getValue("ORDER_CODE", i);
				dosageQty = 0;
				totAmt = 0;
				ownAmt = 0;// ==liling add

				feeTypeDesc = parm.getValue(descName, i);
				cName = parm.getValue(colunmName, i);
				specification = parm.getValue("SPECIFICATION", i);
				orderDesc = parm.getValue("ORDER_DESC", i);
				unitChnDesc = parm.getValue("UNIT_CHN_DESC", i);
				// ����ִ�п���
				dosageUnit = parm.getValue("DOSAGE_UNIT", i);
				dosageQty += Double.valueOf(df1.format(parm.getDouble(
						"DOSAGE_QTY", i)));
				ownAmt += Double.valueOf(df
						.format(parm.getDouble("OWN_AMT", i)));// ===liling add
				totAmt += Double
						.valueOf(df.format(parm.getDouble("AR_AMT", i)));
			}

		}

		parm = tempParm;
		orderCode = "";
		count = parm.getCount("ORDER_DESC");
		String rexpType = "";
		// modify by liming 2012/03/06 end
		for (int i = 0; i < count; i++) {
			// ��������������еĴ��벻ͬ ��ô��¼���µĴ��룬�����ڱ����д�ӡ����Ӧ������ ��Ϊһ������Ŀ�ʼ��
			if (!code.equals(parm.getValue(colunmName, i))) {
				if (i != 0) {
					// result.addData(".TableRowLineShow", true);
					result.addData("FEE_TYPE_DESC", "");
					result.addData("ORDER_DESC", "");
					result.addData("DOSAGE_QTY", "SumTotal��");
					// result.addData("TOTAL", "С��:");// ==liling add
					result.addData("OWN_AMT", df.format(ownDay));
					result.addData("AR_AMT", df.format(totDay));
					// result.addData("EXE_DEPT", "");//==liling ����ִ�п���
					ownDay = 0;// ==liling add
					totDay = 0;
					printCount++;
				}
				code = parm.getValue(colunmName, i);
				orderCode = parm.getValue("ORDER_CODE", i);
				rexpType += parm.getValue(descName, i);
				result.addData("FEE_TYPE_DESC", parm.getValue(descName, i));
				if (printCount > 0) {
					result.setData(".TableRowLineShow", printCount - 2, true); // ����һ�е��߸�Ϊ��ʾ
				}
			} else {
				if (rexpType.contains(parm.getValue(descName, i))) {
					result.addData("FEE_TYPE_DESC", ""); // ֻ��һ�����ݵ�������ʾ��������
					// �����в���ʾ
				} else {
					rexpType += parm.getValue(descName, i);
					result.addData("FEE_TYPE_DESC", parm.getValue(descName, i));
				}
			}
			String SPECIFICATION = parm.getValue("SPECIFICATION", i).length() <= 0 ? ""
					: "(" + parm.getValue("SPECIFICATION", i) + ")";
			/******* add by zhangzc 20120612 ÿ�շ����嵥���ӱ�ʶ **************/
			result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i)
					+ SPECIFICATION);
			result.addData("DOSAGE_QTY",
					parm.getDouble("DOSAGE_QTY", i) == 0 ? "  "
							+ parm.getValue("UNIT_CHN_DESC", i) : df1
							.format(parm.getDouble("DOSAGE_QTY", i))
							+ "  " + parm.getValue("UNIT_CHN_DESC", i));
			result.addData("OWN_AMT", parm.getDouble("OWN_AMT", i) == 0 ? ""
					: df.format(parm.getDouble("OWN_AMT", i)));
			result.addData("AR_AMT", parm.getDouble("OWN_AMT", i) == 0 ? ""
					: df.format(parm.getDouble("AR_AMT", i)));
			// result.addData("EXE_DEPT", parm.getValue("DEPT_CHN_DESC",
			// i));//==liling ����ִ�п���
			if (i + 1 == count)
				result.addData(".TableRowLineShow", false); // ��������ݵ����һ�� ��ô�Ӻ���
			else
				result.addData(".TableRowLineShow", false);
			own += StringTool.round(parm.getDouble("OWN_AMT", i), 2);
			tot += StringTool.round(parm.getDouble("AR_AMT", i), 2);

			printCount++;
			ownDay += parm.getDouble("OWN_AMT", i);// ==liling add
			totDay += parm.getDouble("AR_AMT", i);
			outTotFee += parm.getDouble("AR_AMT", i);
			outOwnFee += parm.getDouble("OWN_AMT", i);
		}
		result.addData(".TableRowLineShow", true);
		result.addData("FEE_TYPE_DESC", "");
		result.addData("ORDER_DESC", "");
		result.addData("DOSAGE_QTY", "SumTotal��");
		result.addData("OWN_AMT", df.format(ownDay));
		result.addData("AR_AMT", df.format(totDay));
		printCount++;
		result.addData(".TableRowLineShow", false);
		result.addData("FEE_TYPE_DESC", "");
		result.addData("ORDER_DESC", "");
		result.addData("DOSAGE_QTY", "");
		result.addData("OWN_AMT", "");
		result.addData("AR_AMT", "");
		printCount++;
		result.addData(".TableRowLineShow", true);
		result.setCount(printCount);
		result.addData("SYSTEM", "COLUMNS", "FEE_TYPE_DESC");
		result.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		result.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
		result.addData("SYSTEM", "COLUMNS", "OWN_AMT");
		result.addData("SYSTEM", "COLUMNS", "AR_AMT");
		// result.addData("SYSTEM", "COLUMNS", "EXE_DEPT");//==liling ����ִ�п���
		TParm print = new TParm();
		print.setData("TABLE", result.getData());
		print.setData("INIT_AMT", "TEXT", df.format(outOwnFee));
		print.setData("TOT_AMT", "TEXT", df.format(outTotFee));
		print.setData("MR_NO", "TEXT", regParm.getValue("MR_NO", 0));
		print.setData("PAT_NAME", "TEXT", regParm.getValue("PAT_NAME", 0));
		String birthday = regParm.getValue("BIRTH_DATE", 0) != null &&regParm.getValue("BIRTH_DATE", 0).length()>0? regParm
				.getValue("BIRTH_DATE", 0).toString().substring(0, 10).replace(
						"-", "/") : "";
		String billDate = regParm.getValue("BILL_DATE", 0) != null &&regParm.getValue("BILL_DATE", 0).length()>0? regParm
				.getValue("BILL_DATE", 0).toString().substring(0, 10).replace(
						"-", "/") : "";
		print.setData("BIRTH_DATE", "TEXT", birthday);

		print.setData("BILL_DATE", "TEXT", billDate);
		if (admType.equals("O")) {
			if (regParm.getValue("ADM_TYPE", 0).equals("O")) {
				print.setData("ADM_TYPE", "TEXT", "OPT����");
			} else {
				print.setData("ADM_TYPE", "TEXT", "EPT����");
			}
		}else{
			print.setData("ADM_TYPE", "TEXT", "IPTסԺ");
		}
		print.setData("PRINT_DATE", "TEXT", StringTool.getString(SystemTool
				.getInstance().getDate(), "yyyy/MM/dd HH:mm"));
		print.setData("OPT_USER", "TEXT", optUser);
		return print;
	}
}
