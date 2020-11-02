package jdo.ibs;

import java.sql.Timestamp;

import jdo.bil.BIL;
import jdo.mem.MEMTool;
import jdo.odi.OdiMainTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
/**
*
* <p>Title: 住院计费控制类</p>
*
* <p>Description:住院计费控制类</p>
*
* <p>Copyright: Copyright (c) Liu dongyang 2015</p>
*
* <p>Company: bluecore</p>
*
* @author pangben
* @version 1.0
* 
*/
public class IBSNewTool extends TJDOTool {
	 /**
     * 实例
     */
    public static IBSNewTool instanceObject;
    /**
     * 得到实例
     * @return IBSTool
     */
    public static IBSNewTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IBSNewTool();
        return instanceObject;
    }

	/**
	 * 构造器
	 */
	public IBSNewTool() {
		onInit();
	}

	/**
	 * 修改身份
	 * 
	 * @author caowl
	 * @param parm
	 *            TParm
	 *            newBodyflg 婴儿校验
	 *            checkLumpWorkFlg 套餐病人校验
	 *            odiParm 套餐差异医嘱操作
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updBillExe(TParm parm,boolean newBodyflg,boolean checkLumpWorkFlg,
			TParm odiParm, TConnection connection,boolean flg) {
		TParm result = new TParm();
		// 得到系统时间
		Timestamp sysDate = SystemTool.getInstance().getDate();

		// 得到界面传过来的参数
		String caseNo = parm.getValue("CASE_NO");
		String optUser = parm.getValue("OPT_USER");
		String optTerm = parm.getValue("OPT_TERM");
		String sql="SELECT CASE_NO,LUMPWORK_RATE,LUMPWORK_CODE,SERVICE_LEVEL,MR_NO FROM ADM_INP WHERE CASE_NO='"+parm.getValue("CASE_NO")+"'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		//新的身份
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		String lumpworkCode=selParm.getValue("LUMPWORK_CODE",0);
		String level =selParm.getValue("SERVICE_LEVEL",0);
		double lumpworkRate=0.00;
		if(null!=selParm.getValue("LUMPWORK_RATE",0)&&selParm.getValue("LUMPWORK_RATE",0).length()>0){
			lumpworkRate=selParm.getDouble("LUMPWORK_RATE",0);
		}
		TParm queryInfoParm=onCheckLumWorkCaseNo(selParm.getValue("MR_NO",0),parm.getValue("CASE_NO"));
		String mrNoNew=queryInfoParm.getValue("MR_NO");
		String caseNoNew=queryInfoParm.getValue("CASE_NO");
		result=getUpdBillExe(caseNo, optUser, optTerm, sysDate, 
				CTZ1, CTZ2, CTZ3, newBodyflg, checkLumpWorkFlg, odiParm, connection,
				lumpworkRate,lumpworkCode,level,flg,mrNoNew,caseNoNew);
		if (result.getErrCode()<0) {
			return result;
		}
		//stept2:更新ADM_INP修改身份
		String updSql = "UPDATE ADM_INP SET CTZ1_CODE = '"+CTZ1+"' ,CTZ2_CODE =  '"+CTZ2+"',CTZ3_CODE = '"+CTZ3+"' ,OPT_USER = '"+optUser+"', OPT_DATE = SYSDATE ,OPT_TERM = '"+optTerm+"',BILL_DATE = SYSDATE  WHERE CASE_NO = '"+caseNo+"'";
		result = new TParm(TJDODBTool.getInstance().update(updSql));
		if (result.getErrCode()<0) {
			return result;
		}
		return result;
	}
	/**
	 * 修改身份 --查询日志
	 * 
	 * @author caowl
	 * @param parm
	 *            String
	 * @return int
	 */
	public int selCountOfCaseno(String caseNo) {
		String sql = "SELECT CASE_NO,SEQ_NO,MR_NO,IPD_NO,BED_NO,REGION_CODE,CTZ_CODE1_O,CTZ_CODE2_O,CTZ_CODE3_O,CTZ_CODE1_N,CTZ_CODE2_N,CTZ_CODE3_N,OPT_USER,OPT_DATE,OPT_TERM " +
				" FROM ADM_CTZ_LOG " +
				" WHERE CASE_NO = '" + caseNo
				+ "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount() == -1){
			return 0;
		}
		return result.getCount();
	}
	private TParm updateMroExe(String caseNo,String optUser,String optTerm,Timestamp sysDate ,
			String CTZ1,String CTZ2,String CTZ3,TParm selPatientParm,TConnection connection){
		//step13:更改病案首页身份
		String sqlMro = "SELECT MRO_CTZ,CTZ1_CODE FROM  MRO_RECORD WHERE CASE_NO = '"+caseNo+"' AND MR_NO = '"+selPatientParm.getValue("MR_NO",0).toString()+"'";
		TParm parmMro = new TParm(TJDODBTool.getInstance().select(sqlMro));
		TParm result=new TParm();
		//.out.println("首页-------》"+sqlMro+parmMro.getCount());
		if(parmMro.getCount()>0){
			//System.out.println("更改病案首页身份");
			String sqlCTZ = "SELECT MRO_CTZ FROM SYS_CTZ WHERE CTZ_CODE = '"+CTZ1+"'";
			TParm parmCTZ = new TParm(TJDODBTool.getInstance().select(sqlCTZ));
			String mroCTZ = "";
			//System.out.println("sqlCTZ-->"+sqlCTZ);
			if(parmCTZ.getCount()>0){
				mroCTZ = parmCTZ.getData("MRO_CTZ",0)+"";
			}
			String updMROSql = "UPDATE  MRO_RECORD SET CTZ1_CODE = '"+CTZ1+"' , MRO_CTZ = '"+mroCTZ+"' WHERE CASE_NO = '"+caseNo+"' AND MR_NO = '"+selPatientParm.getValue("MR_NO",0).toString()+"'";
			//System.out.println("病案首页身份修改sql"+updMROSql);
			result = new TParm(TJDODBTool.getInstance().update(updMROSql,connection));
			//System.out.println("病案首页身份修改成功！");
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
		}
		// step14：插入日志信息
		String mr_no = selPatientParm.getValue("MR_NO",0).toString();
		if (mr_no != null && mr_no.length() != 0) {
			//mr_no = mr_no.substring(1, mr_no.length() - 1);
		} else {
			mr_no = "";
		}

		String ipd_no = selPatientParm.getData("IPD_NO",0).toString();
		if (ipd_no != null && ipd_no.length() != 0) {
			//ipd_no = ipd_no.substring(1, ipd_no.length() - 1);
		} else {
			ipd_no = "";
		}

		String bed_no = selPatientParm.getData("BED_NO",0).toString();
		if (bed_no != null && bed_no.length() != 0) {
			//bed_no = bed_no.substring(1, bed_no.length() - 1);
		} else {
			bed_no = "";
		}

		String region_code = selPatientParm.getData("REGION_CODE",0).toString();
		if (region_code != null && region_code.length() != 0) {
			//region_code = region_code.substring(1, region_code.length() - 1);
		} else {
			region_code = "";
		}

		String ctz1_code = selPatientParm.getData("CTZ1_CODE",0).toString();
		if (ctz1_code != null && ctz1_code.length() != 0) {
			//ctz1_code = ctz1_code.substring(1, ctz1_code.length() - 1);
		} else {
			ctz1_code = "";
		}

		String ctz2_code = selPatientParm.getData("CTZ2_CODE",0).toString();
		if (ctz2_code != null && ctz2_code.length() != 0) {
			//ctz2_code = ctz2_code.substring(1, ctz2_code.length() - 1);
		} else {
			ctz2_code = "";
		}

		String ctz3_code = selPatientParm.getData("CTZ3_CODE",0).toString();
		if (ctz3_code != null && ctz3_code.length() != 0) {
			//ctz3_code = ctz3_code.substring(1, ctz3_code.length() - 1);
		} else {
			ctz3_code = "";
		}

		int seq_no = 1;
		int count = selCountOfCaseno(caseNo);
		seq_no += count;
		String logSql = "INSERT INTO "
				+ " ADM_CTZ_LOG(CASE_NO,SEQ_NO,MR_NO,IPD_NO,BED_NO,REGION_CODE,CTZ_CODE1_O,CTZ_CODE2_O,CTZ_CODE3_O,CTZ_CODE1_N,CTZ_CODE2_N,CTZ_CODE3_N,OPT_USER,OPT_DATE,OPT_TERM) "
				+ " VALUES ('"
				+ caseNo
				+ "','"
				+ seq_no
				+ "','"
				+ mr_no
				+ "','"
				+ ipd_no
				+ "','"
				+ bed_no
				+ "','"
				+ region_code
				+ "','"
				+ ctz1_code
				+ "','"
				+ ctz2_code
				+ "','"
				+ ctz3_code
				+ "','"
				+ CTZ1
				+ "','"
				+ CTZ2
				+ "','"
				+ CTZ3
				+ "','"
				+ optUser
				+ "',SYSDATE,'"
				+ optTerm + "')";
		result = new TParm(TJDODBTool.getInstance().update(logSql,connection));
		
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	private TParm selBycaseNo(String caseNo){
		TParm selParm = new TParm();
		String sql = "SELECT CASE_NO,IPD_NO,MR_NO,REGION_CODE,BED_NO,CTZ1_CODE,CTZ2_CODE,CTZ3_CODE,LUMPWORK_CODE,SERVICE_LEVEL,LUMPWORK_RATE FROM ADM_INP WHERE CASE_NO = '"
				+ caseNo + "'";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		return selParm;
	}
	public TParm getUpdBillExe(String caseNo,String optUser,String optTerm,Timestamp sysDate ,
			String CTZ1,String CTZ2,String CTZ3,
			boolean newBodyflg,boolean checkLumpWorkFlg ,TParm odiParm,TConnection connection,
			double lumpworkRate,String lumpworkCode,String level,boolean ctzFlg,String mrNoNew,String caseNoNew){
		//step1:根据caseNo查询病人信息
		TParm selPatientParm = new TParm();
		selPatientParm = selBycaseNo(caseNo);
		// 设置查询条件--根据case_no查询主张单信息
		TParm selBillmParm = new TParm();
		selBillmParm.setData("CASE_NO", caseNo);		
		TParm result=new TParm();
		//step3: 账单主档数据--根据case_no查询作废账单的全部数据
		//修改处理状态
		String sql="UPDATE IBS_ORDD SET INCLUDE_EXEC_FLG='Y' WHERE CASE_NO='"+caseNo+"'";
		result=new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode()<0) {
			return result;
		}
		TParm selBillm = IBSBillmTool.getInstance().selByCase_noExe(selBillmParm);	
		int billmcount = selBillm.getCount();// 账单主档的数据数目
		TParm maxCaseNoSeqParm = selMaxCaseNoSeq(caseNo);
		int maxCaseNoSeq = maxCaseNoSeqParm.getInt("CASE_NO_SEQ", 0) + 1;
		//获得最大组号，集合医嘱适用，区分作废数据
		int maxGroupNo=IBSLumpWorkBatchTool.getInstance().selMaxOrderSetGroupNo(caseNo);
		if (maxGroupNo==-1) {
			maxGroupNo=1;
		}
		//成员变量
		TParm exeNewParm=new TParm();
		exeNewParm.setData("maxCaseNoSeq",maxCaseNoSeq);
		exeNewParm.setData("maxGroupNo",maxGroupNo);
		//循环主账单
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}	
		//已经存在账单的数据修改状态
		result=updateBillmExe(caseNo,billmcount, selBillm, optUser, connection, sysDate, optTerm,
				maxCaseNoSeqParm, newBodyflg, checkLumpWorkFlg, 
				odiParm, CTZ1, CTZ2, CTZ3,lumpworkRate,lumpworkCode, level,ctzFlg,mrNoNew,caseNoNew,exeNewParm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}	
		//未生成账单的费用
		result=onNoBillOrddExe(caseNo, optUser, optTerm, sysDate, CTZ1, CTZ2, CTZ3, 
				newBodyflg, checkLumpWorkFlg, odiParm, connection, maxCaseNoSeqParm,
				lumpworkRate,lumpworkCode,level,ctzFlg,mrNoNew,caseNoNew,exeNewParm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}	
		if(ctzFlg){//身份修改需要操作此方法
			result=updateMroExe(caseNo, optUser, optTerm, sysDate, CTZ1, CTZ2, CTZ3, selPatientParm,connection);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}	
		}
		return result ;
	}
	 /**
	 * 查询最大账务序号
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 * caowl
	 */
	public TParm selMaxCaseNoSeq(String caseNo) {
		String sql = " SELECT MAX(CASE_NO_SEQ) AS CASE_NO_SEQ FROM IBS_ORDM WHERE CASE_NO = '"
				+ caseNo + "' ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	/**
	 * 已经存在账单的数据修改状态
	 * @param billmcount
	 * @param selBillm
	 * @param optUser
	 * @param connection
	 * @param sysDate
	 * @param optTerm
	 * @param maxCaseNoSeqParm
	 * @param newBodyflg
	 * @param checkLumpWorkFlg
	 * @param odiParm
	 * @param CTZ1
	 * @param CTZ2
	 * @param CTZ3
	 * @return
	 */
	public TParm updateBillmExe(String caseNo,int billmcount, TParm selBillm, String optUser,
			TConnection connection, Timestamp sysDate, String optTerm,
			TParm maxCaseNoSeqParm,
			boolean newBodyflg, boolean checkLumpWorkFlg, TParm odiParm,
			String CTZ1, String CTZ2, String CTZ3,double lumpworkRate,String lumpworkCode,
			String level,boolean ctzFlg,String mrNoNew ,String caseNoNew,TParm exeNewParm) {

		TParm result = new TParm();
		TParm updateBillmParm = null;
		for (int i = 0; i < billmcount; i++) {

			// System.out.println("循环第"+i+"张主账单");
			// 账单流水号--根据取号原则得到账单号和账单序号
			String newBillNo = SystemTool.getInstance().getNo("ALL", "IBS",
					"BILL_NO", "BILL_NO");
			// String newBillNo1 = SystemTool.getInstance().getNo("ALL",
			// "IBS",
			// "BILL_NO", "BILL_NO");
			//			
			// TParm selMaxSeqParm = new TParm();

			// step4:更新账单主档
			updateBillmParm = new TParm();
			updateBillmParm.setData("BILL_NO", selBillm.getData("BILL_NO", i));
			updateBillmParm.setData("REFUND_FLG", "Y");
			updateBillmParm.setData("REFUND_BILL_NO", newBillNo);
			updateBillmParm.setData("REFUND_CODE", optUser);

			result = IBSBillmTool.getInstance().updataDateforCTZ(
					updateBillmParm, connection);

			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
			// step5:主账单插入负数据
			result = insertIbsBillmExe(newBillNo, selBillm, i, sysDate,
					optTerm, optUser, connection);
			// System.out.println("主账单插入负数据");
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}

			result = insertIBsOrdmExe(selBillm, i, maxCaseNoSeqParm,sysDate, optTerm, 
					optUser, connection,newBodyflg, checkLumpWorkFlg, odiParm, CTZ1,
					CTZ2, CTZ3,lumpworkRate,lumpworkCode,level,ctzFlg,mrNoNew,caseNoNew,exeNewParm);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
			result = insertIbsBilldExe(selBillm, i, optUser, connection,
					sysDate, optTerm, newBillNo);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
//			String sql="UPDATE IBS_ORDM SET BILL_EXE_FLG='Y' WHERE CASE_NO='"+caseNo+"' AND BILL_NO='"+selBillm.getData("BILL_NO", i)+"'";
//			result=new TParm(TJDODBTool.getInstance().update(sql,connection));
//			if (result.getErrCode()<0) {
//				return result;
//			}
//			String sql="UPDATE IBS_BILLM SET BILL_EXE_FLG='Y' WHERE CASE_NO='"+caseNo+"' AND BILL_NO='"+selBillm.getData("BILL_NO", i)+"'";
//			result=new TParm(TJDODBTool.getInstance().update(sql,connection));
//			if (result.getErrCode()<0) {
//				return result;
//			}
		}
		return result;
	}
	private TParm insertIbsBilldExe(TParm selBillm, int i, String optUser,
			TConnection connection, Timestamp sysDate, String optTerm,
			String newBillNo) {
		// step10:查询账单明细档数据
		TParm selBilldParm = new TParm();
		selBilldParm.setData("BILL_NO", selBillm.getData("BILL_NO", i));
		selBilldParm.setData("BILL_SEQ", selBillm.getData("BILL_SEQ", i));
		TParm selBliid = IBSBilldTool.getInstance().selectAllData(selBilldParm);
		int billDCount = selBliid.getCount("BILL_NO");
		TParm insertBilldNegativeParm = new TParm();
		// TParm insertBilldPositiveParm = new TParm();
		TParm updateBliidParm = null;
		TParm result = new TParm();
		for (int j = 0; j < billDCount; j++) {
			// System.out.println("循环第"+j+"张明细账单");
			// step11:更新账单明细档数据
			updateBliidParm = new TParm();
			updateBliidParm.setData("BILL_NO", selBillm.getData("BILL_NO", i));
			updateBliidParm.setData("REFUND_CODE", optUser);
			updateBliidParm.setData("REFUND_BILL_NO", newBillNo);
			result = IBSBilldTool.getInstance().updataDateforCTZ(
					updateBliidParm, connection);

			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
			// step12：插入账单明细档负数据
			insertBilldNegativeParm.setData("BILL_NO", newBillNo);
			insertBilldNegativeParm.setData("BILL_SEQ", 1);
			insertBilldNegativeParm.setData("REXP_CODE", selBliid.getData(
					"REXP_CODE", j));
			insertBilldNegativeParm.setData("OWN_AMT", -selBliid.getDouble(
					"OWN_AMT", j));
			insertBilldNegativeParm.setData("AR_AMT", -selBliid.getDouble(
					"AR_AMT", j));
			insertBilldNegativeParm.setData("PAY_AR_AMT", selBliid.getDouble(
					"PAY_AR_AMT", j));
			insertBilldNegativeParm.setData("REFUND_BILL_NO", "");
			insertBilldNegativeParm.setData("REFUND_FLG", "Y");
			insertBilldNegativeParm.setData("REFUND_CODE", "");
			insertBilldNegativeParm.setData("REFUND_DATE", "");
			insertBilldNegativeParm.setData("OPT_USER", optUser);
			insertBilldNegativeParm.setData("OPT_TERM", optTerm);
			result = IBSBilldTool.getInstance().insertdata(
					insertBilldNegativeParm, connection);

			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
		}
		return result;
	}

	private TParm insertIbsBillmExe(String newBillNo, TParm selBillm, int i,
			Timestamp sysDate, String optTerm, String optUser,
			TConnection connection) {
		// 插入IBS_BILLM表中负数据
		TParm insertBillmNegativeParm = new TParm();
		insertBillmNegativeParm.setData("BILL_SEQ", 1);
		insertBillmNegativeParm.setData("BILL_NO", newBillNo);
		insertBillmNegativeParm.setData("CASE_NO", selBillm.getData("CASE_NO",
				i));
		insertBillmNegativeParm
				.setData("IPD_NO", selBillm.getData("IPD_NO", i));
		insertBillmNegativeParm.setData("MR_NO", selBillm.getData("MR_NO", i));
		insertBillmNegativeParm.setData("BILL_DATE", sysDate);
		insertBillmNegativeParm.setData("REFUND_FLG", "Y");// caowl
		// 20130419

		insertBillmNegativeParm.setData("REFUND_BILL_NO", "");// caowl
		// 20130419

		insertBillmNegativeParm.setData("RECEIPT_NO", selBillm.getData(
				"RECEIPT_NO", i) == null ? new TNull(String.class) : selBillm
				.getData("RECEIPT_NO", i));

		insertBillmNegativeParm.setData("CHARGE_DATE", selBillm.getData(
				"CHARGE_DATE", i) == null ? new TNull(String.class) : selBillm
				.getData("CHARGE_DATE", i));

		insertBillmNegativeParm.setData("CTZ1_CODE", selBillm.getData(
				"CTZ1_CODE", i) == null ? new TNull(String.class) : selBillm
				.getData("CTZ1_CODE", i));
		insertBillmNegativeParm.setData("CTZ2_CODE", selBillm.getData(
				"CTZ2_CODE", i) == null ? new TNull(String.class) : selBillm
				.getData("CTZ2_CODE", i));
		insertBillmNegativeParm.setData("CTZ3_CODE", selBillm.getData(
				"CTZ3_CODE", i) == null ? new TNull(String.class) : selBillm
				.getData("CTZ3_CODE", i));
		insertBillmNegativeParm.setData("BEGIN_DATE", selBillm.getData(
				"BEGIN_DATE", i));
		insertBillmNegativeParm.setData("END_DATE", selBillm.getData(
				"END_DATE", i));
		insertBillmNegativeParm.setData("DISCHARGE_FLG", selBillm.getData(
				"DISCHARGE_FLG", i) == null ? new TNull(String.class)
				: selBillm.getData("DISCHARGE_FLG", i));

		insertBillmNegativeParm.setData("DEPT_CODE", selBillm.getData(
				"DEPT_CODE", i));
		insertBillmNegativeParm.setData("STATION_CODE", selBillm.getData(
				"STATION_CODE", i));
		insertBillmNegativeParm.setData("REGION_CODE", selBillm.getData(
				"REGION_CODE", i));
		insertBillmNegativeParm
				.setData("BED_NO", selBillm.getData("BED_NO", i));
		insertBillmNegativeParm.setData("OWN_AMT", -selBillm.getDouble(
				"OWN_AMT", i));
		insertBillmNegativeParm.setData("NHI_AMT", -selBillm.getDouble(
				"NHI_AMT", i));
		insertBillmNegativeParm.setData("APPROVE_FLG", "N");
		insertBillmNegativeParm.setData("REDUCE_REASON", selBillm.getData(
				"REDUCE_REASON", i) == null ? new TNull(String.class)
				: selBillm.getData("REDUCE_REASON", i));
		insertBillmNegativeParm.setData("REDUCE_AMT", -selBillm.getDouble(
				"REDUCE_AMT", i));
		insertBillmNegativeParm.setData("REDUCE_DATE", selBillm.getData(
				"REDUCE_DATE", i) == null ? new TNull(Timestamp.class)
				: selBillm.getData("REDUCE_DATE", i));
		insertBillmNegativeParm.setData("REDUCE_DEPT_CODE", selBillm.getData(
				"REDUCE_DEPT_CODE", i) == null ? new TNull(String.class)
				: selBillm.getData("REDUCE_DEPT_CODE", i));
		insertBillmNegativeParm.setData("REDUCE_RESPOND", selBillm.getData(
				"REDUCE_RESPOND", i) == null ? new TNull(String.class)
				: selBillm.getData("REDUCE_RESPOND", i));
		insertBillmNegativeParm.setData("AR_AMT", -selBillm.getDouble("AR_AMT",
				i));
		insertBillmNegativeParm.setData("PAY_AR_AMT", selBillm.getDouble(
				"PAY_AR_AMT", i));
		insertBillmNegativeParm.setData("CANDEBT_CODE", selBillm.getData(
				"CANDEBT_CODE", i) == null ? new TNull(String.class) : selBillm
				.getData("CANDEBT_CODE", i));
		insertBillmNegativeParm.setData("CANDEBT_PERSON", selBillm.getData(
				"CANDEBT_PERSON", i) == null ? new TNull(String.class)
				: selBillm.getData("CANDEBT_PERSON", i));
		insertBillmNegativeParm.setData("REFUND_CODE", selBillm.getData(
				"REFUND_CODE", i) == null ? new TNull(String.class) : selBillm
				.getData("REFUND_CODE", i));
		insertBillmNegativeParm.setData("REFUND_DATE", selBillm.getData(
				"REFUND_DATE", i) == null ? new TNull(Timestamp.class)
				: selBillm.getData("REFUND_DATE", i));
		insertBillmNegativeParm.setData("OPT_USER", optUser);
		insertBillmNegativeParm.setData("OPT_TERM", optTerm);
		insertBillmNegativeParm.setData("BILL_EXE_FLG", "Y");
		TParm result = IBSBillmTool.getInstance().insertdataExe(
				insertBillmNegativeParm, connection);
		return result;
	}

	private TParm insertIBsOrdmExe(TParm selBillm, int i,
			TParm maxCaseNoSeqParm, Timestamp sysDate,
			String optTerm, String optUser, TConnection connection,boolean newBodyflg, boolean checkLumpWorkFlg,
			TParm odiParm, String CTZ1, String CTZ2, String CTZ3,double lumpworkRate,String lumpworkCode,
			String level,boolean ctzFlg,String mrNoNew,String caseNoNew,TParm exeNewParm) {
		int maxCaseNoSeq=exeNewParm.getInt("maxCaseNoSeq");//最大IBS_ORDM号
		int maxGroupNo=exeNewParm.getInt("maxGroupNo");//最大组号 
		TParm result = new TParm();
		String sqlIbsOrdm = "SELECT A.CASE_NO,A.CASE_NO_SEQ,A.BILL_DATE,A.IPD_NO,A.MR_NO,A.DEPT_CODE,A.STATION_CODE," +
				"A.BED_NO,A.DATA_TYPE,A.BILL_NO,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.REGION_CODE,A.COST_CENTER_CODE FROM IBS_ORDM A,IBS_ORDD B WHERE A.CASE_NO=B.CASE_NO AND A.CASE_NO_SEQ=B.CASE_NO_SEQ AND A.BILL_NO = '"
				+ selBillm.getData("BILL_NO", i) + "' AND A.CASE_NO = '"
				+ selBillm.getData("CASE_NO", i) + "' AND B.BILL_EXE_FLG='N' GROUP BY " +
				"A.CASE_NO,A.CASE_NO_SEQ,A.BILL_DATE,A.IPD_NO,A.MR_NO,A.DEPT_CODE,A.STATION_CODE," +
				"A.BED_NO,A.DATA_TYPE,A.BILL_NO,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.REGION_CODE,A.COST_CENTER_CODE";
		TParm parmIbsOrdm = new TParm(TJDODBTool.getInstance().select(
				sqlIbsOrdm));
		if(parmIbsOrdm.getCount()<=0){
			sqlIbsOrdm = "SELECT CASE_NO,CASE_NO_SEQ,MAX(BILL_DATE) BILL_DATE,'"+selBillm.getData("IPD_NO", i)+"' IPD_NO,'"+selBillm.getData("MR_NO", i)+
					"' MR_NO,'"+selBillm.getData("DEPT_CODE", i)+"' DEPT_CODE,'"+selBillm.getData("STATION_CODE", i)+"' STATION_CODE,'" +
					selBillm.getData("BED_NO", i)+"' BED_NO,'1' DATA_TYPE,BILL_NO,'"+selBillm.getData("REGION_CODE", i)+"' REGION_CODE,'"+selBillm.getData("DEPT_CODE", i)+
					"' COST_CENTER_CODE FROM IBS_ORDD WHERE BILL_NO = '"
					+ selBillm.getData("BILL_NO", i) + "' AND CASE_NO = '"+ selBillm.getData("CASE_NO", i) + "' AND BILL_EXE_FLG='N' GROUP BY " +
					"CASE_NO,CASE_NO_SEQ,BILL_NO";
			parmIbsOrdm = new TParm(TJDODBTool.getInstance().select(
					sqlIbsOrdm));
		}
		// 循环费用主档
		if (parmIbsOrdm.getCount() > 0) {
			TParm insertIbsOrdMNegativeParm = null;
			for (int n = 0; n < parmIbsOrdm.getCount(); n++) {
				// step6:费用主档插入负数据
				// ibs_ordm插入付负数据 case_no_seq_new1，optUser，optTerm
				// System.out.println("循环第"+n+"次费用主档");
				insertIbsOrdMNegativeParm = new TParm();
				insertIbsOrdMNegativeParm.setData("CASE_NO", parmIbsOrdm
						.getData("CASE_NO", n));
				if (maxCaseNoSeqParm.getCount("CASE_NO_SEQ") == 0) {
					insertIbsOrdMNegativeParm.setData("CASE_NO_SEQ", 1);
				} else {
					insertIbsOrdMNegativeParm.setData("CASE_NO_SEQ",
							maxCaseNoSeq);
				}

				insertIbsOrdMNegativeParm.setData("BILL_DATE", sysDate);
				insertIbsOrdMNegativeParm.setData("IPD_NO", parmIbsOrdm
						.getData("IPD_NO", n) == null ? new TNull(String.class)
						: parmIbsOrdm.getData("IPD_NO", n));
				insertIbsOrdMNegativeParm.setData("MR_NO", parmIbsOrdm.getData(
						"MR_NO", n) == null ? new TNull(String.class)
						: parmIbsOrdm.getData("MR_NO", n));
				insertIbsOrdMNegativeParm.setData("DEPT_CODE", parmIbsOrdm
						.getData("DEPT_CODE", n) == null ? new TNull(
						String.class) : parmIbsOrdm.getData("DEPT_CODE", n));
				insertIbsOrdMNegativeParm.setData("STATION_CODE", parmIbsOrdm
						.getData("STATION_CODE", n) == null ? new TNull(
						String.class) : parmIbsOrdm.getData("STATION_CODE", n));
				insertIbsOrdMNegativeParm.setData("BED_NO", parmIbsOrdm
						.getData("BED_NO", n) == null ? new TNull(String.class)
						: parmIbsOrdm.getData("BED_NO", n));
				insertIbsOrdMNegativeParm.setData("DATA_TYPE", parmIbsOrdm
						.getData("DATA_TYPE", n) == null ? new TNull(
						String.class) : parmIbsOrdm.getData("DATA_TYPE", n));
				insertIbsOrdMNegativeParm.setData("BILL_NO",
						parmIbsOrdm.getData("BILL_NO", n) == null ? new TNull(
								String.class) : parmIbsOrdm.getData("BILL_NO",
								n));
				insertIbsOrdMNegativeParm.setData("OPT_USER", optUser);
				insertIbsOrdMNegativeParm.setData("OPT_TERM", optTerm);
				insertIbsOrdMNegativeParm.setData("REGION_CODE", parmIbsOrdm
						.getData("REGION_CODE", n) == null ? new TNull(
						String.class) : parmIbsOrdm.getData("REGION_CODE", n));
				insertIbsOrdMNegativeParm
						.setData("COST_CENTER_CODE", parmIbsOrdm.getData(
								"COST_CENTER_CODE", n) == null ? new TNull(
								String.class) : parmIbsOrdm.getData(
								"COST_CENTER_CODE", n));
				//insertIbsOrdMNegativeParm.setData("BILL_EXE_FLG", "Y");
				
				result = IBSOrdmTool.getInstance().insertdataM(
						insertIbsOrdMNegativeParm, connection);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					return result;
				}
				// System.out.println("费用主档负数据插入成功！");
				// 根据bill_no查询
				String sqlIbsOrdd = "SELECT A.*,B.CAT1_TYPE AS SYS_FEE_CAT1_TYPE,B.CHARGE_HOSP_CODE FROM IBS_ORDD A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.BILL_NO = '"
						+ selBillm.getData("BILL_NO", i) + "' AND A.CASE_NO = '"
						+ selBillm.getData("CASE_NO", i)
						+ "' AND A.CASE_NO_SEQ ="
						+ parmIbsOrdm.getDouble("CASE_NO_SEQ", n)
						+ " AND A.BILL_EXE_FLG='N' ORDER BY A.CASE_NO_SEQ,A.ORDERSET_GROUP_NO,A.SEQ_NO";
				TParm parmIbsOrdd = new TParm(TJDODBTool.getInstance().select(
						sqlIbsOrdd));
				int seqNo = 1;
				// 循环费用明细档
				if (parmIbsOrdd.getCount() > 0) {
					TParm insertIbsOrddNegativeParm = null;
					maxGroupNo = maxGroupNo + 1;
					String ordersetCode = parmIbsOrdd.getValue("ORDERSET_CODE",
							0);
					String groupNoSeq = parmIbsOrdd.getValue(
							"ORDERSET_GROUP_NO", 0);
					String orderNoSeq = parmIbsOrdd.getValue("CASE_NO_SEQ", 0);
					for (int m = 0; m < parmIbsOrdd.getCount(); m++) {
						// step7:费用明细档插入负数据
						// System.out.println("循环第"+m+"次费用明细");
						// ibs_ordd插入负数据
						// case_no_seq_new1,bill_date-->sysdate,金额为负
						insertIbsOrddNegativeParm = new TParm();
						insertIbsOrddNegativeParm
								.setData("CASE_NO", parmIbsOrdd.getData(
										"CASE_NO", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"CASE_NO", m));
						if (maxCaseNoSeqParm.getCount("CASE_NO_SEQ") == 0) {
							insertIbsOrddNegativeParm.setData("CASE_NO_SEQ", 1);
						} else {
							insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",
									maxCaseNoSeq);
						}
						insertIbsOrddNegativeParm.setData("SEQ_NO", seqNo);
						insertIbsOrddNegativeParm.setData("BILL_DATE", sysDate);
						insertIbsOrddNegativeParm.setData("EXEC_DATE", sysDate);
						insertIbsOrddNegativeParm
								.setData("ORDER_NO", parmIbsOrdd.getData(
										"ORDER_NO", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"ORDER_NO", m));
						insertIbsOrddNegativeParm
								.setData("ORDER_SEQ", parmIbsOrdd.getData(
										"ORDER_SEQ", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"ORDER_SEQ", m));
						insertIbsOrddNegativeParm
								.setData("ORDER_CODE", parmIbsOrdd.getData(
										"ORDER_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"ORDER_CODE", m));
						insertIbsOrddNegativeParm
								.setData("ORDER_CAT1_CODE",
										parmIbsOrdd.getData("ORDER_CAT1_CODE",
												m) == null ? new TNull(
												String.class) : parmIbsOrdd
												.getData("ORDER_CAT1_CODE", m));
						insertIbsOrddNegativeParm
								.setData("CAT1_TYPE", parmIbsOrdd.getData(
										"CAT1_TYPE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"CAT1_TYPE", m));
						// 添加集合医嘱数据组号重新获得======pangben 2016-2-22
						if (null != parmIbsOrdd.getData("ORDERSET_CODE", m)
								&& parmIbsOrdd.getValue("ORDERSET_CODE", m)
										.length() > 0) {// 集合医嘱
							if (ordersetCode.equals(parmIbsOrdd.getValue(
									"ORDERSET_CODE", m))
									&& groupNoSeq.equals(parmIbsOrdd.getValue(
											"ORDERSET_GROUP_NO", m))
									&& orderNoSeq.equals(parmIbsOrdd.getValue(
											"CASE_NO_SEQ", m))) {
								insertIbsOrddNegativeParm.setData(
										"ORDERSET_GROUP_NO", maxGroupNo);
							} else {
								maxGroupNo = maxGroupNo + 1;
								insertIbsOrddNegativeParm.setData(
										"ORDERSET_GROUP_NO", maxGroupNo);
								ordersetCode = parmIbsOrdd.getValue(
										"ORDERSET_CODE", m);
								groupNoSeq = parmIbsOrdd.getValue(
										"ORDERSET_GROUP_NO", m);
								orderNoSeq = parmIbsOrdd.getValue(
										"CASE_NO_SEQ", m);
							}
						} else {
							insertIbsOrddNegativeParm
									.setData(
											"ORDERSET_GROUP_NO",
											parmIbsOrdd.getData(
													"ORDERSET_GROUP_NO", m) == null ? new TNull(
													String.class)
													: parmIbsOrdd
															.getData(
																	"ORDERSET_GROUP_NO",
																	m));
						}
						// insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",parmIbsOrdd.getData("ORDERSET_GROUP_NO",
						// m) == null ? new TNull(String.class) :
						// parmIbsOrdd.getData("ORDERSET_GROUP_NO", m));
						insertIbsOrddNegativeParm
								.setData(
										"ORDERSET_CODE",
										parmIbsOrdd.getData("ORDERSET_CODE", m) == null ? new TNull(
												String.class)
												: parmIbsOrdd.getData(
														"ORDERSET_CODE", m));
						insertIbsOrddNegativeParm
								.setData("INDV_FLG", parmIbsOrdd.getData(
										"INDV_FLG", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"INDV_FLG", m));
						insertIbsOrddNegativeParm
								.setData("DEPT_CODE", parmIbsOrdd.getData(
										"DEPT_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"DEPT_CODE", m));
						insertIbsOrddNegativeParm
								.setData("STATION_CODE", parmIbsOrdd.getData(
										"STATION_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"STATION_CODE", m));
						insertIbsOrddNegativeParm
								.setData("DR_CODE", parmIbsOrdd.getData(
										"DR_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"DR_CODE", m));
						insertIbsOrddNegativeParm
								.setData(
										"EXE_DEPT_CODE",
										parmIbsOrdd.getData("EXE_DEPT_CODE", m) == null ? new TNull(
												String.class)
												: parmIbsOrdd.getData(
														"EXE_DEPT_CODE", m));
						insertIbsOrddNegativeParm
								.setData("EXE_STATION_CODE",
										parmIbsOrdd.getData("EXE_STATION_CODE",
												m) == null ? new TNull(
												String.class) : parmIbsOrdd
												.getData("EXE_STATION_CODE", m));
						insertIbsOrddNegativeParm
								.setData("EXE_DR_CODE", parmIbsOrdd.getData(
										"EXE_DR_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"EXE_DR_CODE", m));
						insertIbsOrddNegativeParm.setData("MEDI_QTY",
								-parmIbsOrdd.getDouble("MEDI_QTY", m));
						insertIbsOrddNegativeParm
								.setData("MEDI_UNIT", parmIbsOrdd.getData(
										"MEDI_UNIT", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"MEDI_UNIT", m));
						insertIbsOrddNegativeParm
								.setData("DOSE_CODE", parmIbsOrdd.getData(
										"DOSE_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"DOSE_CODE", m));
						insertIbsOrddNegativeParm
								.setData("FREQ_CODE", parmIbsOrdd.getData(
										"FREQ_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"FREQ_CODE", m));
						insertIbsOrddNegativeParm
								.setData("TAKE_DAYS", parmIbsOrdd.getData(
										"TAKE_DAYS", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"TAKE_DAYS", m));
						insertIbsOrddNegativeParm.setData("DOSAGE_QTY",
								-parmIbsOrdd.getDouble("DOSAGE_QTY", m));
						insertIbsOrddNegativeParm
								.setData("DOSAGE_UNIT", parmIbsOrdd.getData(
										"DOSAGE_UNIT", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"DOSAGE_UNIT", m));
						insertIbsOrddNegativeParm.setData("OWN_PRICE",
								parmIbsOrdd.getDouble("OWN_PRICE", m));
						insertIbsOrddNegativeParm.setData("NHI_PRICE",
								parmIbsOrdd.getDouble("NHI_PRICE", m));
						insertIbsOrddNegativeParm.setData("TOT_AMT",
								-parmIbsOrdd.getDouble("TOT_AMT", m));
						insertIbsOrddNegativeParm
								.setData("OWN_FLG", parmIbsOrdd.getData(
										"OWN_FLG", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"OWN_FLG", m));
						insertIbsOrddNegativeParm
								.setData("BILL_FLG", parmIbsOrdd.getData(
										"BILL_FLG", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"BILL_FLG", m));
						insertIbsOrddNegativeParm
								.setData("REXP_CODE", parmIbsOrdd.getData(
										"REXP_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"REXP_CODE", m));
						insertIbsOrddNegativeParm
								.setData("BILL_NO", parmIbsOrdd.getData(
										"BILL_NO", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"BILL_NO", m));
						insertIbsOrddNegativeParm
								.setData("HEXP_CODE", parmIbsOrdd.getData(
										"HEXP_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"HEXP_CODE", m));
						insertIbsOrddNegativeParm
								.setData("BEGIN_DATE", parmIbsOrdd.getData(
										"BEGIN_DATE", m) == null ? new TNull(
										Timestamp.class) : parmIbsOrdd.getData(
										"BEGIN_DATE", m));
						insertIbsOrddNegativeParm
								.setData("END_DATE", parmIbsOrdd.getData(
										"END_DATE", m) == null ? new TNull(
										Timestamp.class) : parmIbsOrdd.getData(
										"END_DATE", m));
						insertIbsOrddNegativeParm.setData("OWN_AMT",
								-parmIbsOrdd.getDouble("OWN_AMT", m));
						insertIbsOrddNegativeParm.setData("OWN_RATE",
								parmIbsOrdd.getDouble("OWN_RATE", m));
						insertIbsOrddNegativeParm
								.setData("REQUEST_FLG", parmIbsOrdd.getData(
										"REQUEST_FLG", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"REQUEST_FLG", m));
						insertIbsOrddNegativeParm
								.setData("REQUEST_NO", parmIbsOrdd.getData(
										"REQUEST_NO", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"REQUEST_NO", m));
						insertIbsOrddNegativeParm
								.setData("INV_CODE", parmIbsOrdd.getData(
										"INV_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"INV_CODE", m));
						insertIbsOrddNegativeParm.setData("OPT_USER", optUser);
						insertIbsOrddNegativeParm.setData("OPT_TERM", optTerm);
						insertIbsOrddNegativeParm.setData("COST_AMT",
								-parmIbsOrdd.getDouble("COST_AMT", m));
						insertIbsOrddNegativeParm
								.setData(
										"ORDER_CHN_DESC",
										parmIbsOrdd
												.getData("ORDER_CHN_DESC", m) == null ? new TNull(
												String.class)
												: parmIbsOrdd.getData(
														"ORDER_CHN_DESC", m));
						insertIbsOrddNegativeParm
								.setData("COST_CENTER_CODE",
										parmIbsOrdd.getData("COST_CENTER_CODE",
												m) == null ? new TNull(
												String.class) : parmIbsOrdd
												.getData("COST_CENTER_CODE", m));
						insertIbsOrddNegativeParm
								.setData("SCHD_CODE", parmIbsOrdd.getData(
										"SCHD_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"SCHD_CODE", m));
						insertIbsOrddNegativeParm
								.setData(
										"CLNCPATH_CODE",
										parmIbsOrdd.getData("CLNCPATH_CODE", m) == null ? new TNull(
												String.class)
												: parmIbsOrdd.getData(
														"CLNCPATH_CODE", m));
						insertIbsOrddNegativeParm.setData("DS_FLG", parmIbsOrdd
								.getData("DS_FLG", m) == null ? new TNull(
								String.class) : parmIbsOrdd
								.getData("DS_FLG", m));
						insertIbsOrddNegativeParm.setData("KN_FLG", parmIbsOrdd
								.getData("KN_FLG", m) == null ? new TNull(
								String.class) : parmIbsOrdd
								.getData("KN_FLG", m));
						insertIbsOrddNegativeParm
								.setData("INCLUDE_FLG", parmIbsOrdd.getData(
										"INCLUDE_FLG", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"INCLUDE_FLG", m));
						insertIbsOrddNegativeParm.setData("INCLUDE_EXEC_FLG",
								"Y");// 作废数据处理状态
						insertIbsOrddNegativeParm.setData("BILL_EXE_FLG",
								"Y");// 作废数据处理状态
						seqNo++;
						result = IBSOrdmTool.getInstance().insertdataLumpworkDExe(
								insertIbsOrddNegativeParm, connection);
						if (result.getErrCode() < 0) {
							err(result.getErrName() + " " + result.getErrText());
							return result;
						}
						// System.out.println("费用明细负数据插入成功！");
						//修改执行状态
						String sql="UPDATE IBS_ORDD SET BILL_EXE_FLG='Y' WHERE CASE_NO='"+parmIbsOrdd.getValue(
								"CASE_NO", m)+"' AND CASE_NO_SEQ="+parmIbsOrdd.getValue("CASE_NO_SEQ", m)+" AND SEQ_NO="+parmIbsOrdd.getValue("SEQ_NO", m);
						result=new TParm(TJDODBTool.getInstance().update(sql,connection));
						if (result.getErrCode()<0) {
							System.out.println("套餐批次添加交易表出现问题:"+result.getErrText());
							return result;
						}
					}
				}
				maxCaseNoSeq++;
				// step8:费用主档插入正数据（修改身份后的费用）
				// 插入ibs_ordm正数据
				TParm insertIbsOrdMPositiveParm = new TParm();
				insertIbsOrdMPositiveParm.setData("CASE_NO", parmIbsOrdm
						.getData("CASE_NO", n));
				insertIbsOrdMPositiveParm.setData("CASE_NO_SEQ", maxCaseNoSeq);
				insertIbsOrdMPositiveParm.setData("BILL_DATE", sysDate);
				insertIbsOrdMPositiveParm.setData("IPD_NO", parmIbsOrdm
						.getData("IPD_NO", n) == null ? new TNull(String.class)
						: parmIbsOrdm.getData("IPD_NO", n));
				insertIbsOrdMPositiveParm.setData("MR_NO", parmIbsOrdm.getData(
						"MR_NO", n) == null ? new TNull(String.class)
						: parmIbsOrdm.getData("MR_NO", n));
				insertIbsOrdMPositiveParm.setData("DEPT_CODE", parmIbsOrdm
						.getData("DEPT_CODE", n) == null ? new TNull(
						String.class) : parmIbsOrdm.getData("DEPT_CODE", n));
				insertIbsOrdMPositiveParm.setData("STATION_CODE", parmIbsOrdm
						.getData("STATION_CODE", n) == null ? new TNull(
						String.class) : parmIbsOrdm.getData("STATION_CODE", n));
				insertIbsOrdMPositiveParm.setData("BED_NO", parmIbsOrdm
						.getData("BED_NO", n) == null ? new TNull(String.class)
						: parmIbsOrdm.getData("BED_NO", n));
				insertIbsOrdMPositiveParm.setData("DATA_TYPE", parmIbsOrdm
						.getData("DATA_TYPE", n) == null ? new TNull(
						String.class) : parmIbsOrdm.getData("DATA_TYPE", n));
				insertIbsOrdMPositiveParm.setData("BILL_NO", "");
				insertIbsOrdMPositiveParm.setData("OPT_USER", optUser);
				insertIbsOrdMPositiveParm.setData("OPT_TERM", optTerm);
				insertIbsOrdMPositiveParm.setData("REGION_CODE", parmIbsOrdm
						.getData("REGION_CODE", n) == null ? new TNull(
						String.class) : parmIbsOrdm.getData("REGION_CODE", n));
				insertIbsOrdMPositiveParm
						.setData("COST_CENTER_CODE", parmIbsOrdm.getData(
								"COST_CENTER_CODE", n) == null ? new TNull(
								String.class) : parmIbsOrdm.getData(
								"COST_CENTER_CODE", n));
				result = IBSOrdmTool.getInstance().insertdataM(
						insertIbsOrdMPositiveParm, connection);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					return result;
				}
				// System.out.println("费用主档正数据插入成功！");
				// 插入ibs_ordd正数据
				seqNo = 1;
				if (parmIbsOrdd.getCount() > 0) {
					maxGroupNo = maxGroupNo + 1;
					String ordersetCode = parmIbsOrdd.getValue("ORDERSET_CODE",
							0);
					String groupNoSeq = parmIbsOrdd.getValue(
							"ORDERSET_GROUP_NO", 0);
					String orderNoSeq = parmIbsOrdd.getValue("CASE_NO_SEQ", 0);
					TParm insertIbsOrddPositiveParm = null;
					for (int m = 0; m < parmIbsOrdd.getCount(); m++) {
						// step9:费用明细档插入正数据（修改身份后的费用）
						// System.out.println("循环第"+m+"次费用明细档");
						// ibs_ordd插入负数据
						// case_no_seq_new1,bill_date-->sysdate,金额为负
						insertIbsOrddPositiveParm = new TParm();
						insertIbsOrddPositiveParm
								.setData("CASE_NO", parmIbsOrdd.getData(
										"CASE_NO", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"CASE_NO", m));
						insertIbsOrddPositiveParm.setData("CASE_NO_SEQ",
								maxCaseNoSeq);
						insertIbsOrddPositiveParm.setData("SEQ_NO", seqNo);
						insertIbsOrddPositiveParm.setData("BILL_DATE", sysDate);
						insertIbsOrddPositiveParm.setData("EXEC_DATE", sysDate);
						insertIbsOrddPositiveParm
								.setData("ORDER_NO", parmIbsOrdd.getData(
										"ORDER_NO", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"ORDER_NO", m));
						insertIbsOrddPositiveParm
								.setData("ORDER_SEQ", parmIbsOrdd.getData(
										"ORDER_SEQ", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"ORDER_SEQ", m));
						insertIbsOrddPositiveParm
								.setData("ORDER_CODE", parmIbsOrdd.getData(
										"ORDER_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"ORDER_CODE", m));
						insertIbsOrddPositiveParm
								.setData("ORDER_CAT1_CODE",
										parmIbsOrdd.getData("ORDER_CAT1_CODE",
												m) == null ? new TNull(
												String.class) : parmIbsOrdd
												.getData("ORDER_CAT1_CODE", m));
						insertIbsOrddPositiveParm
								.setData("CAT1_TYPE", parmIbsOrdd.getData(
										"CAT1_TYPE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"CAT1_TYPE", m));
						// 添加集合医嘱数据组号重新获得======pangben 2016-2-22
						if (null != parmIbsOrdd.getData("ORDERSET_CODE", m)
								&& parmIbsOrdd.getValue("ORDERSET_CODE", m)
										.length() > 0) {// 集合医嘱
							if (ordersetCode.equals(parmIbsOrdd.getValue(
									"ORDERSET_CODE", m))
									&& groupNoSeq.equals(parmIbsOrdd.getValue(
											"ORDERSET_GROUP_NO", m))
									&& orderNoSeq.equals(parmIbsOrdd.getValue(
											"CASE_NO_SEQ", m))) {
								insertIbsOrddPositiveParm.setData(
										"ORDERSET_GROUP_NO", maxGroupNo);
							} else {
								maxGroupNo = maxGroupNo + 1;
								insertIbsOrddPositiveParm.setData(
										"ORDERSET_GROUP_NO", maxGroupNo);
								ordersetCode = parmIbsOrdd.getValue(
										"ORDERSET_CODE", m);
								groupNoSeq = parmIbsOrdd.getValue(
										"ORDERSET_GROUP_NO", m);
								orderNoSeq = parmIbsOrdd.getValue(
										"CASE_NO_SEQ", m);
							}
						} else {
							insertIbsOrddPositiveParm
									.setData(
											"ORDERSET_GROUP_NO",
											parmIbsOrdd.getData(
													"ORDERSET_GROUP_NO", m) == null ? new TNull(
													String.class)
													: parmIbsOrdd
															.getData(
																	"ORDERSET_GROUP_NO",
																	m));
						}
						insertIbsOrddPositiveParm
								.setData(
										"ORDERSET_CODE",
										parmIbsOrdd.getData("ORDERSET_CODE", m) == null ? new TNull(
												String.class)
												: parmIbsOrdd.getData(
														"ORDERSET_CODE", m));
						insertIbsOrddPositiveParm
								.setData("INDV_FLG", parmIbsOrdd.getData(
										"INDV_FLG", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"INDV_FLG", m));
						insertIbsOrddPositiveParm
								.setData("DEPT_CODE", parmIbsOrdd.getData(
										"DEPT_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"DEPT_CODE", m));
						insertIbsOrddPositiveParm
								.setData("STATION_CODE", parmIbsOrdd.getData(
										"STATION_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"STATION_CODE", m));
						insertIbsOrddPositiveParm
								.setData("DR_CODE", parmIbsOrdd.getData(
										"DR_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"DR_CODE", m));
						insertIbsOrddPositiveParm
								.setData(
										"EXE_DEPT_CODE",
										parmIbsOrdd.getData("EXE_DEPT_CODE", m) == null ? new TNull(
												String.class)
												: parmIbsOrdd.getData(
														"EXE_DEPT_CODE", m));
						insertIbsOrddPositiveParm
								.setData("EXE_STATION_CODE",
										parmIbsOrdd.getData("EXE_STATION_CODE",
												m) == null ? new TNull(
												String.class) : parmIbsOrdd
												.getData("EXE_STATION_CODE", m));
						insertIbsOrddPositiveParm
								.setData("EXE_DR_CODE", parmIbsOrdd.getData(
										"EXE_DR_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"EXE_DR_CODE", m));
						insertIbsOrddPositiveParm.setData("MEDI_QTY",
								parmIbsOrdd.getDouble("MEDI_QTY", m));
						insertIbsOrddPositiveParm
								.setData("MEDI_UNIT", parmIbsOrdd.getData(
										"MEDI_UNIT", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"MEDI_UNIT", m));
						insertIbsOrddPositiveParm
								.setData("DOSE_CODE", parmIbsOrdd.getData(
										"DOSE_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"DOSE_CODE", m));
						insertIbsOrddPositiveParm
								.setData("FREQ_CODE", parmIbsOrdd.getData(
										"FREQ_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"FREQ_CODE", m));
						insertIbsOrddPositiveParm
								.setData("TAKE_DAYS", parmIbsOrdd.getData(
										"TAKE_DAYS", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"TAKE_DAYS", m));
						insertIbsOrddPositiveParm.setData("DOSAGE_QTY",
								parmIbsOrdd.getDouble("DOSAGE_QTY", m));
						insertIbsOrddPositiveParm
								.setData("DOSAGE_UNIT", parmIbsOrdd.getData(
										"DOSAGE_UNIT", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"DOSAGE_UNIT", m));
						insertIbsOrddPositiveParm.setData("OWN_PRICE",
								parmIbsOrdd.getDouble("OWN_PRICE", m));
						insertIbsOrddPositiveParm.setData("NHI_PRICE",
								parmIbsOrdd.getDouble("NHI_PRICE", m));
						insertIbsOrddPositiveParm
								.setData("OWN_FLG", parmIbsOrdd.getData(
										"OWN_FLG", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"OWN_FLG", m));
						insertIbsOrddPositiveParm
								.setData("BILL_FLG", parmIbsOrdd.getData(
										"BILL_FLG", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"BILL_FLG", m));
						insertIbsOrddPositiveParm
								.setData("REXP_CODE", parmIbsOrdd.getData(
										"REXP_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"REXP_CODE", m));
						insertIbsOrddPositiveParm.setData("BILL_NO", "");
						insertIbsOrddPositiveParm
								.setData("HEXP_CODE", parmIbsOrdd.getData(
										"HEXP_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"HEXP_CODE", m));
						insertIbsOrddPositiveParm
								.setData("BEGIN_DATE", parmIbsOrdd.getData(
										"BEGIN_DATE", m) == null ? new TNull(
										Timestamp.class) : parmIbsOrdd.getData(
										"BEGIN_DATE", m));
						insertIbsOrddPositiveParm
								.setData("END_DATE", parmIbsOrdd.getData(
										"END_DATE", m) == null ? new TNull(
										Timestamp.class) : parmIbsOrdd.getData(
										"END_DATE", m));
						insertIbsOrddPositiveParm
								.setData("REQUEST_FLG", parmIbsOrdd.getData(
										"REQUEST_FLG", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"REQUEST_FLG", m));
						insertIbsOrddPositiveParm
								.setData("REQUEST_NO", parmIbsOrdd.getData(
										"REQUEST_NO", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"REQUEST_NO", m));
						insertIbsOrddPositiveParm
								.setData("INV_CODE", parmIbsOrdd.getData(
										"INV_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"INV_CODE", m));
						insertIbsOrddPositiveParm.setData("OPT_USER", optUser);
						insertIbsOrddPositiveParm.setData("OPT_TERM", optTerm);
						insertIbsOrddPositiveParm
								.setData(
										"ORDER_CHN_DESC",
										parmIbsOrdd
												.getData("ORDER_CHN_DESC", m) == null ? new TNull(
												String.class)
												: parmIbsOrdd.getData(
														"ORDER_CHN_DESC", m));
						insertIbsOrddPositiveParm
								.setData("COST_CENTER_CODE",
										parmIbsOrdd.getData("COST_CENTER_CODE",
												m) == null ? new TNull(
												String.class) : parmIbsOrdd
												.getData("COST_CENTER_CODE", m));
						insertIbsOrddPositiveParm
								.setData("SCHD_CODE", parmIbsOrdd.getData(
										"SCHD_CODE", m) == null ? new TNull(
										String.class) : parmIbsOrdd.getData(
										"SCHD_CODE", m));
						insertIbsOrddPositiveParm
								.setData(
										"CLNCPATH_CODE",
										parmIbsOrdd.getData("CLNCPATH_CODE", m) == null ? new TNull(
												String.class)
												: parmIbsOrdd.getData(
														"CLNCPATH_CODE", m));
						insertIbsOrddPositiveParm.setData("DS_FLG", parmIbsOrdd
								.getData("DS_FLG", m) == null ? new TNull(
								String.class) : parmIbsOrdd
								.getData("DS_FLG", m));
						insertIbsOrddPositiveParm.setData("KN_FLG", parmIbsOrdd
								.getData("KN_FLG", m) == null ? new TNull(
								String.class) : parmIbsOrdd
								.getData("KN_FLG", m));
						insertIbsOrddPositiveParm.setData("INCLUDE_FLG", "N");
						BIL bil = new BIL();
						String chargeHospCode = parmIbsOrdd.getData(
								"HEXP_CODE", m)
								+ "";
						String orderCode = parmIbsOrdd.getData("ORDER_CODE", m)
								+ "";
						double tot_amt_new = 0.00;
						double cost_amt_new = 0.00;
						double own_amt = parmIbsOrdd.getDouble("OWN_AMT", m);
						double ownRate = 1;
						// ====pangben 2015-7-28 添加套餐病人修改身份，差异医嘱不打折操作
						if (!newBodyflg) {// 婴儿不需要操作
							double ownPrice=0.00;
							double [] sumPrice=new double[2];
							if (checkLumpWorkFlg && null != odiParm) {// 套餐病人操作
								//if FLG='Y'套餐外
								//使用IBS_ORDD表中的金额\单价 \折扣  注意 INCLUDE_EXEC_FLG='Y' 不需要在操作套餐批次 状态
								//ELSE
								//使用更加身份设置的折扣比例计算  INCLUDE_EXEC_FLG='N'
								
								if("Y".equals(parmIbsOrdd.getValue("INCLUDE_FLG",m))&&parmIbsOrdd.getValue("INCLUDE_FLG",m).length()>0){//add by kangy 20170804
//									insertIbsOrddPositiveParm.setData("TOT_AMT",parmIbsOrdd.getDouble("TOT_AMT",m));
//									insertIbsOrddPositiveParm.setData("OWN_PRICE",parmIbsOrdd.getDouble("OWN_PRICE",m));
//									insertIbsOrddPositiveParm.setData("OWN_RATE",parmIbsOrdd.getDouble("OWN_RATE",m));
									insertIbsOrddPositiveParm.setData("INCLUDE_EXEC_FLG","Y");
									insertIbsOrddPositiveParm.setData("INCLUDE_FLG", "Y");
									ownPrice =parmIbsOrdd.getDouble("OWN_PRICE",m);
									//own_amt = parmIbsOrdd.getDouble("OWN_AMT",m);
									//ownRate = parmIbsOrdd.getDouble("OWN_RATE",m);
									ownRate = bil.getOwnRate(CTZ1, CTZ2, CTZ3,
											chargeHospCode, orderCode);
									tot_amt_new = own_amt * ownRate;
									cost_amt_new = own_amt * ownRate;
								}else{
									insertIbsOrddPositiveParm.setData("INCLUDE_EXEC_FLG","N");
								    if (parmIbsOrdd.getValue("ORDER_CODE", m).equals(odiParm.getValue("LUMPWORK_ORDER_CODE",
														0))) {// 套餐病人
								    	// 套餐差异医嘱不可以执行折扣
									   tot_amt_new = own_amt;
									   cost_amt_new = own_amt;
									   ownPrice=own_amt;
									} else {
										//套餐患者计费根据入院登记计算的折扣操作
						            	//药品、血费根据身份进行统计
						            	//套餐内计费
										
										if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m) && parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m).equals("PHA")||
						            			null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m) && parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m).equals("RA")){
											ownRate = bil.getOwnRate(CTZ1, CTZ2, CTZ3,
													chargeHospCode, orderCode);
											sumPrice=IBSTool.getInstance().getOrderOwnPrice(orderCode, level);	
											ownPrice=sumPrice[0];
						            	}else{
						    				ownRate=lumpworkRate;
						    				ownPrice=lumpWorkPrice(mrNoNew,caseNoNew,  lumpworkCode, orderCode, level,ctzFlg);
						    			}
										own_amt=parmIbsOrdd.getDouble("DOSAGE_QTY",m)*ownPrice;//套餐病患重新计费
										tot_amt_new = own_amt * ownRate;
										cost_amt_new = own_amt * ownRate;
									}
								}
							} else {
								insertIbsOrddPositiveParm.setData("INCLUDE_EXEC_FLG","N");
								sumPrice=IBSTool.getInstance().getOrderOwnPrice(orderCode, level);
								ownPrice=sumPrice[0];
								own_amt=parmIbsOrdd.getDouble("DOSAGE_QTY",m)*ownPrice;//套餐病患重新计费
								ownRate = bil.getOwnRate(CTZ1, CTZ2, CTZ3,
										chargeHospCode, orderCode);
								tot_amt_new = own_amt * ownRate;
								cost_amt_new = own_amt * ownRate;
							}
							insertIbsOrddPositiveParm.setData("OWN_PRICE", ownPrice);
						} else {
							double ownPrice=0.00;
							double []sumPrice=new double [2];
							if (checkLumpWorkFlg) {
								//if FLG='Y'套餐外
								//使用IBS_ORDD表中的金额\单价 \折扣 注意 INCLUDE_EXEC_FLG='Y' 不需要在操作套餐批次 状态
								//ELSE
								//使用更加身份设置的折扣比例计算 注意 INCLUDE_EXEC_FLG='N' 需要在操作套餐批次 状态
								if("Y".equals(parmIbsOrdd.getValue("INCLUDE_FLG",m))&&parmIbsOrdd.getValue("INCLUDE_FLG",m).length()>0){//add by kangy 20170804
//									insertIbsOrddPositiveParm.setData("TOT_AMT",parmIbsOrdd.getDouble("TOT_AMT",m));
//									insertIbsOrddPositiveParm.setData("OWN_PRICE",parmIbsOrdd.getDouble("OWN_PRICE",m));
//									insertIbsOrddPositiveParm.setData("OWN_RATE",parmIbsOrdd.getDouble("OWN_RATE",m));
									insertIbsOrddPositiveParm.setData("INCLUDE_EXEC_FLG","Y");
									insertIbsOrddPositiveParm.setData("INCLUDE_FLG", "Y");
									//own_amt  = parmIbsOrdd.getDouble("OWN_AMT",m) ;
									//ownRate =parmIbsOrdd.getDouble("OWN_RATE",m);
									ownRate = bil.getOwnRate(CTZ1, CTZ2, CTZ3,
											chargeHospCode, orderCode);
									ownPrice =parmIbsOrdd.getDouble("OWN_PRICE",m);
								}else{
									insertIbsOrddPositiveParm.setData("INCLUDE_EXEC_FLG","N");

								
									// 套餐病人操作
									//套餐患者计费根据入院登记计算的折扣操作
					            	//药品、血费根据身份进行统计
					            	//套餐内计费
									if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m) && parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m).equals("PHA")||
					            			null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m) && parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m).equals("RA")){
										ownRate = bil.getOwnRate(CTZ1, CTZ2, CTZ3,
												chargeHospCode, orderCode);   
										sumPrice=IBSTool.getInstance().getOrderOwnPrice(orderCode, level);
										ownPrice=sumPrice[0];
					            	}else{
					    				ownRate=lumpworkRate;
					    				ownPrice=lumpWorkPrice(mrNoNew,caseNoNew, lumpworkCode, orderCode, level,ctzFlg);
					    			}
									own_amt=parmIbsOrdd.getDouble("DOSAGE_QTY",m)*ownPrice;//套餐病患重新计费
								}
								
							}else{
								insertIbsOrddPositiveParm.setData("INCLUDE_EXEC_FLG","N");
								sumPrice=IBSTool.getInstance().getOrderOwnPrice(orderCode, level);
								ownPrice=sumPrice[0];
								own_amt=parmIbsOrdd.getDouble("DOSAGE_QTY",m)*ownPrice;//套餐病患重新计费
								ownRate = bil.getOwnRate(CTZ1, CTZ2, CTZ3,
										chargeHospCode, orderCode);
							}
							insertIbsOrddPositiveParm.setData("OWN_PRICE", ownPrice);
							tot_amt_new = own_amt * ownRate;
							cost_amt_new = own_amt * ownRate;
						}
						// double tot_amt =
						// parmIbsOrdd.getDouble("TOT_AMT",m);
						// ===========pangben 20150212 修改身份折扣金额，
						// 入院身份为0.7折 ，改成0.5折 要用自费金额* 当前折扣
						insertIbsOrddPositiveParm.setData("TOT_AMT",
								StringTool.round(tot_amt_new,2));
						insertIbsOrddPositiveParm.setData("OWN_AMT", StringTool.round(own_amt,2));
						insertIbsOrddPositiveParm.setData("OWN_RATE", ownRate);
						insertIbsOrddPositiveParm.setData("COST_AMT",
								StringTool.round(cost_amt_new,2));
						seqNo++;
						// System.out.println("明细档正数据参数："+insertIbsOrdMPositiveParm);
						result = IBSOrdmTool.getInstance().insertIbsOrddData(
								insertIbsOrddPositiveParm, connection);
						if (result.getErrCode() < 0) {
							err(result.getErrName() + " " + result.getErrText());
							return result;
						}
						// System.out.println("费用明细档正数据插入成功！");
					}
				}
				maxCaseNoSeq++;
			}

		}
		exeNewParm.setData("maxCaseNoSeq",maxCaseNoSeq);
		exeNewParm.setData("maxGroupNo",maxGroupNo);
		return result;
	}
	/**
	 * 套餐病患查询母亲病案号就诊号
	 * @param mrNo
	 * @param caseNo
	 * @param type
	 * @return
	 */
	public TParm onCheckLumWorkCaseNo(String mrNo,String caseNo){
		String sql="SELECT M_CASE_NO,NEW_BORN_FLG FROM ADM_INP WHERE CASE_NO='"+caseNo+"'";
    	TParm newBobyParm = new TParm(TJDODBTool.getInstance().select(sql));
    	String caseNoNew=caseNo;
    	String mrNoNew=mrNo;
    	if (newBobyParm.getValue("NEW_BORN_FLG",0).equals("Y")) {
    		caseNoNew=newBobyParm.getValue("M_CASE_NO",0);
    		sql="SELECT MR_NO,CASE_NO FROM ADM_INP WHERE CASE_NO='"+caseNoNew+"'";
        	newBobyParm = new TParm(TJDODBTool.getInstance().select(sql));
    		mrNoNew=newBobyParm.getValue("MR_NO",0);
		}
    	TParm result=new TParm();
    	result.setData("MR_NO",mrNoNew);
    	result.setData("CASE_NO",caseNoNew);
    	return result;
	}
	/**
	 * 未结账的费用的处理
	 * @param caseNo
	 * @param optUser
	 * @param optTerm
	 * @param sysDate
	 * @param CTZ1
	 * @param CTZ2
	 * @param CTZ3
	 * @param newBodyflg
	 * @param checkLumpWorkFlg
	 * @param odiParm
	 * @param connection
	 * @param maxCaseNoSeqParm
	 * @param maxCaseNoSeq
	 * @param maxGroupNo
	 * @return
	 */
	private TParm onNoBillOrddExe(String caseNo,String optUser,String optTerm,Timestamp sysDate ,
			String CTZ1,String CTZ2,String CTZ3,
			boolean newBodyflg,boolean checkLumpWorkFlg ,
			TParm odiParm,TConnection connection,TParm maxCaseNoSeqParm,double lumpworkRate
			,String lumpworkCode,String level,boolean ctzFlg,String mrNoNew,String caseNoNew,TParm exeNewParm){
		int maxCaseNoSeq=exeNewParm.getInt("maxCaseNoSeq");//最大IBS_ORDM号
		int maxGroupNo=exeNewParm.getInt("maxGroupNo");//最大组号 
		//未结账的费用的处理=====================开始
		TParm result=new TParm();
		String sqlIbsOrdmForUnBil = "SELECT A.CASE_NO,A.CASE_NO_SEQ,A.BILL_DATE,A.IPD_NO,A.MR_NO,A.DEPT_CODE,A.STATION_CODE," +
				"A.BED_NO,A.DATA_TYPE,A.BILL_NO,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.REGION_CODE,A.COST_CENTER_CODE FROM IBS_ORDM A,IBS_ORDD B WHERE A.CASE_NO=B.CASE_NO " +
				"AND A.CASE_NO_SEQ=B.CASE_NO_SEQ AND A.BILL_NO IS NULL AND A.CASE_NO = '"+caseNo+"' AND B.BILL_EXE_FLG='N' GROUP BY " +
				"A.CASE_NO,A.CASE_NO_SEQ,A.BILL_DATE,A.IPD_NO,A.MR_NO,A.DEPT_CODE,A.STATION_CODE,A.BED_NO,A.DATA_TYPE,A.BILL_NO,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.REGION_CODE,A.COST_CENTER_CODE";
		TParm parmIbsOrdmForUnBil = new TParm(TJDODBTool.getInstance().select(sqlIbsOrdmForUnBil));
		if(parmIbsOrdmForUnBil.getCount()>0){
			String mrNo=parmIbsOrdmForUnBil.getValue("MR_NO",0);
			TParm insertIbsOrdMNegativeParm =null;
			for(int n=0;n<parmIbsOrdmForUnBil.getCount();n++){
			//step6:费用主档插入负数据
			//ibs_ordm插入付负数据    case_no_seq_new1，optUser，optTerm
//			System.out.println("循环第"+n+"次费用主档");
			insertIbsOrdMNegativeParm = new TParm();					
			insertIbsOrdMNegativeParm.setData("CASE_NO",parmIbsOrdmForUnBil.getData("CASE_NO",n));
			if (maxCaseNoSeqParm.getCount("CASE_NO_SEQ") == 0) {
				insertIbsOrdMNegativeParm.setData("CASE_NO_SEQ",1);
			} else {
				insertIbsOrdMNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
			}
			
			insertIbsOrdMNegativeParm.setData("BILL_DATE",sysDate);
			insertIbsOrdMNegativeParm.setData("IPD_NO",parmIbsOrdmForUnBil.getData("IPD_NO", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("IPD_NO", n));
			insertIbsOrdMNegativeParm.setData("MR_NO",parmIbsOrdmForUnBil.getData("MR_NO", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("MR_NO", n));
			insertIbsOrdMNegativeParm.setData("DEPT_CODE",parmIbsOrdmForUnBil.getData("DEPT_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("DEPT_CODE", n));
			insertIbsOrdMNegativeParm.setData("STATION_CODE",parmIbsOrdmForUnBil.getData("STATION_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("STATION_CODE", n));
			insertIbsOrdMNegativeParm.setData("BED_NO",parmIbsOrdmForUnBil.getData("BED_NO", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("BED_NO", n));
			insertIbsOrdMNegativeParm.setData("DATA_TYPE",parmIbsOrdmForUnBil.getData("DATA_TYPE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("DATA_TYPE", n));
			insertIbsOrdMNegativeParm.setData("BILL_NO",parmIbsOrdmForUnBil.getData("BILL_NO", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("BILL_NO", n));
			insertIbsOrdMNegativeParm.setData("OPT_USER",optUser);
			insertIbsOrdMNegativeParm.setData("OPT_TERM",optTerm);
			insertIbsOrdMNegativeParm.setData("REGION_CODE",parmIbsOrdmForUnBil.getData("REGION_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("REGION_CODE", n));
			insertIbsOrdMNegativeParm.setData("COST_CENTER_CODE",parmIbsOrdmForUnBil.getData("COST_CENTER_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("COST_CENTER_CODE", n));
			result =IBSOrdmTool.getInstance().insertdataM(insertIbsOrdMNegativeParm,connection);
	        if (result.getErrCode() < 0) {
	                err(result.getErrName() + " " + result.getErrText());
	                return result;
	        }
//	        System.out.println("费用主档负数据插入成功！");
			//根据bill_no查询
			String sqlIbsOrdd = "SELECT A.*,B.CAT1_TYPE AS SYS_FEE_CAT1_TYPE,B.CHARGE_HOSP_CODE FROM IBS_ORDD A,SYS_FEE B " +
					"WHERE A.ORDER_CODE=B.ORDER_CODE AND A.BILL_NO IS NULL AND A.CASE_NO = '"
				+caseNo+"' AND A.CASE_NO_SEQ ="+parmIbsOrdmForUnBil.getDouble("CASE_NO_SEQ",n)+
				" AND  A.BILL_EXE_FLG='N' ORDER BY A.CASE_NO_SEQ,A.ORDERSET_GROUP_NO,A.SEQ_NO ";
			TParm parmIbsOrdd = new TParm(TJDODBTool.getInstance().select(sqlIbsOrdd));
			if(parmIbsOrdd.getErrCode()<0){
				  err(parmIbsOrdd.getErrName() + " " + parmIbsOrdd.getErrText());
	              return parmIbsOrdd;
			}
			int seqNo = 1;
			TParm insertIbsOrdMPositiveParm =null;
			//循环费用明细档
			if(parmIbsOrdd.getCount()>0){
				TParm insertIbsOrddNegativeParm=null;
				maxGroupNo=maxGroupNo+1;
				String ordersetCode=parmIbsOrdd.getValue("ORDERSET_CODE", 0);
				String groupNoSeq=parmIbsOrdd.getValue("ORDERSET_GROUP_NO", 0);
				String orderNoSeq=parmIbsOrdd.getValue("CASE_NO_SEQ", 0);
				for(int m = 0;m<parmIbsOrdd.getCount();m++){
					//step7:费用明细档插入负数据
//					System.out.println("循环第"+m+"次费用明细");
					//ibs_ordd插入负数据  case_no_seq_new1,bill_date-->sysdate,金额为负
					insertIbsOrddNegativeParm = new TParm();
					insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", m));					
					if (maxCaseNoSeqParm.getCount("CASE_NO_SEQ") == 0) {
						insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",1);
					} else {
						insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
					}							
					insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
					insertIbsOrddNegativeParm.setData("BILL_DATE",sysDate);
					insertIbsOrddNegativeParm.setData("EXEC_DATE",sysDate);
					insertIbsOrddNegativeParm.setData("ORDER_NO",parmIbsOrdd.getData("ORDER_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_NO", m));
					insertIbsOrddNegativeParm.setData("ORDER_SEQ",parmIbsOrdd.getData("ORDER_SEQ", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_SEQ", m));
					insertIbsOrddNegativeParm.setData("ORDER_CODE",parmIbsOrdd.getData("ORDER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CODE", m));
					insertIbsOrddNegativeParm.setData("ORDER_CAT1_CODE",parmIbsOrdd.getData("ORDER_CAT1_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CAT1_CODE", m));
					insertIbsOrddNegativeParm.setData("CAT1_TYPE",parmIbsOrdd.getData("CAT1_TYPE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CAT1_TYPE", m));
					//添加集合医嘱数据组号重新获得======pangben 2016-2-22
					if (null!=parmIbsOrdd.getData("ORDERSET_CODE", m)&&parmIbsOrdd.getValue("ORDERSET_CODE", m).length()>0) {//集合医嘱
						if (ordersetCode.equals(parmIbsOrdd.getValue("ORDERSET_CODE", m))&&
								groupNoSeq.equals(parmIbsOrdd.getValue("ORDERSET_GROUP_NO", m))&&
								orderNoSeq.equals(parmIbsOrdd.getValue("CASE_NO_SEQ", m))) {
							insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",maxGroupNo);
						}else{
							maxGroupNo=maxGroupNo+1;
							insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",maxGroupNo);
							ordersetCode=parmIbsOrdd.getValue("ORDERSET_CODE", m);
							groupNoSeq=parmIbsOrdd.getValue("ORDERSET_GROUP_NO", m);
							orderNoSeq=parmIbsOrdd.getValue("CASE_NO_SEQ", m);
						}
					}else{
						insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",parmIbsOrdd.getData("ORDERSET_GROUP_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_GROUP_NO", m));
					}
					//insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",parmIbsOrdd.getData("ORDERSET_GROUP_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_GROUP_NO", m));
					insertIbsOrddNegativeParm.setData("ORDERSET_CODE",parmIbsOrdd.getData("ORDERSET_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_CODE", m));
					insertIbsOrddNegativeParm.setData("INDV_FLG",parmIbsOrdd.getData("INDV_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INDV_FLG", m));
					insertIbsOrddNegativeParm.setData("DEPT_CODE",parmIbsOrdd.getData("DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DEPT_CODE", m));
					insertIbsOrddNegativeParm.setData("STATION_CODE",parmIbsOrdd.getData("STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("STATION_CODE", m));
					insertIbsOrddNegativeParm.setData("DR_CODE",parmIbsOrdd.getData("DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DR_CODE", m));
					insertIbsOrddNegativeParm.setData("EXE_DEPT_CODE",parmIbsOrdd.getData("EXE_DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DEPT_CODE", m));
					insertIbsOrddNegativeParm.setData("EXE_STATION_CODE",parmIbsOrdd.getData("EXE_STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_STATION_CODE", m));
					insertIbsOrddNegativeParm.setData("EXE_DR_CODE",parmIbsOrdd.getData("EXE_DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DR_CODE", m));
					insertIbsOrddNegativeParm.setData("MEDI_QTY",-parmIbsOrdd.getDouble("MEDI_QTY",m));
					insertIbsOrddNegativeParm.setData("MEDI_UNIT",parmIbsOrdd.getData("MEDI_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("MEDI_UNIT", m));
					insertIbsOrddNegativeParm.setData("DOSE_CODE",parmIbsOrdd.getData("DOSE_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSE_CODE", m));
					insertIbsOrddNegativeParm.setData("FREQ_CODE",parmIbsOrdd.getData("FREQ_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("FREQ_CODE", m));
					insertIbsOrddNegativeParm.setData("TAKE_DAYS",parmIbsOrdd.getData("TAKE_DAYS", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("TAKE_DAYS", m));
					insertIbsOrddNegativeParm.setData("DOSAGE_QTY",-parmIbsOrdd.getDouble("DOSAGE_QTY",m));
					insertIbsOrddNegativeParm.setData("DOSAGE_UNIT",parmIbsOrdd.getData("DOSAGE_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSAGE_UNIT", m));
					insertIbsOrddNegativeParm.setData("OWN_PRICE",parmIbsOrdd.getDouble("OWN_PRICE",m));
					insertIbsOrddNegativeParm.setData("NHI_PRICE",parmIbsOrdd.getDouble("NHI_PRICE",m));
					insertIbsOrddNegativeParm.setData("TOT_AMT",-parmIbsOrdd.getDouble("TOT_AMT",m));
					insertIbsOrddNegativeParm.setData("OWN_FLG",parmIbsOrdd.getData("OWN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("OWN_FLG", m));
					insertIbsOrddNegativeParm.setData("BILL_FLG",parmIbsOrdd.getData("BILL_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("BILL_FLG", m));
					insertIbsOrddNegativeParm.setData("REXP_CODE",parmIbsOrdd.getData("REXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REXP_CODE", m));
					insertIbsOrddNegativeParm.setData("BILL_NO",parmIbsOrdd.getData("BILL_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("BILL_NO", m));
					insertIbsOrddNegativeParm.setData("HEXP_CODE",parmIbsOrdd.getData("HEXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("HEXP_CODE", m));
					insertIbsOrddNegativeParm.setData("BEGIN_DATE",parmIbsOrdd.getData("BEGIN_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("BEGIN_DATE", m));
					insertIbsOrddNegativeParm.setData("END_DATE",parmIbsOrdd.getData("END_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("END_DATE", m));
					insertIbsOrddNegativeParm.setData("OWN_AMT",-parmIbsOrdd.getDouble("OWN_AMT",m));
					insertIbsOrddNegativeParm.setData("OWN_RATE",parmIbsOrdd.getDouble("OWN_RATE",m));
					insertIbsOrddNegativeParm.setData("REQUEST_FLG",parmIbsOrdd.getData("REQUEST_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_FLG", m));
					insertIbsOrddNegativeParm.setData("REQUEST_NO",parmIbsOrdd.getData("REQUEST_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_NO", m));
					insertIbsOrddNegativeParm.setData("INV_CODE",parmIbsOrdd.getData("INV_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INV_CODE", m));
					insertIbsOrddNegativeParm.setData("OPT_USER",optUser);
					insertIbsOrddNegativeParm.setData("OPT_TERM",optTerm);
					insertIbsOrddNegativeParm.setData("COST_AMT",-parmIbsOrdd.getDouble("COST_AMT",m));
					insertIbsOrddNegativeParm.setData("ORDER_CHN_DESC",parmIbsOrdd.getData("ORDER_CHN_DESC", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CHN_DESC", m));
					insertIbsOrddNegativeParm.setData("COST_CENTER_CODE",parmIbsOrdd.getData("COST_CENTER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("COST_CENTER_CODE", m));
					insertIbsOrddNegativeParm.setData("SCHD_CODE",parmIbsOrdd.getData("SCHD_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("SCHD_CODE", m));
					insertIbsOrddNegativeParm.setData("CLNCPATH_CODE",parmIbsOrdd.getData("CLNCPATH_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CLNCPATH_CODE", m));
					insertIbsOrddNegativeParm.setData("DS_FLG",parmIbsOrdd.getData("DS_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DS_FLG", m));
					insertIbsOrddNegativeParm.setData("KN_FLG",parmIbsOrdd.getData("KN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("KN_FLG", m));
					insertIbsOrddNegativeParm.setData("INCLUDE_FLG",parmIbsOrdd.getData("INCLUDE_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INCLUDE_FLG", m));
					insertIbsOrddNegativeParm.setData("INCLUDE_EXEC_FLG","Y");//数据处理状态
					insertIbsOrddNegativeParm.setData("BILL_EXE_FLG","Y");//数据处理状态
					seqNo++;
					result =IBSOrdmTool.getInstance().insertdataLumpworkDExe(insertIbsOrddNegativeParm,connection);
			        if (result.getErrCode() < 0) {
			                err(result.getErrName() + " " + result.getErrText());
			                return result;
			        }
			        //修改执行状态
					String sql="UPDATE IBS_ORDD SET BILL_EXE_FLG='Y' WHERE CASE_NO='"+parmIbsOrdd.getValue(
							"CASE_NO", m)+"' AND CASE_NO_SEQ="+parmIbsOrdd.getValue("CASE_NO_SEQ", m)+" AND SEQ_NO="+parmIbsOrdd.getValue("SEQ_NO", m);
					result=new TParm(TJDODBTool.getInstance().update(sql,connection));
					if (result.getErrCode()<0) {
						System.out.println("套餐批次添加交易表出现问题:"+result.getErrText());
						return result;
					}
//			        System.out.println("费用明细负数据插入成功！");
				}						
			}
			maxCaseNoSeq++;
			//step8:费用主档插入正数据（修改身份后的费用）
			//插入ibs_ordm正数据
			insertIbsOrdMPositiveParm = new TParm();					
			insertIbsOrdMPositiveParm.setData("CASE_NO",parmIbsOrdmForUnBil.getData("CASE_NO",n));					
			insertIbsOrdMPositiveParm.setData("CASE_NO_SEQ",maxCaseNoSeq);										
			insertIbsOrdMPositiveParm.setData("BILL_DATE",sysDate);
			insertIbsOrdMPositiveParm.setData("IPD_NO",parmIbsOrdmForUnBil.getData("IPD_NO", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("IPD_NO", n));
			insertIbsOrdMPositiveParm.setData("MR_NO",parmIbsOrdmForUnBil.getData("MR_NO", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("MR_NO", n));
			insertIbsOrdMPositiveParm.setData("DEPT_CODE",parmIbsOrdmForUnBil.getData("DEPT_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("DEPT_CODE", n));
			insertIbsOrdMPositiveParm.setData("STATION_CODE",parmIbsOrdmForUnBil.getData("STATION_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("STATION_CODE", n));
			insertIbsOrdMPositiveParm.setData("BED_NO",parmIbsOrdmForUnBil.getData("BED_NO", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("BED_NO", n));
			insertIbsOrdMPositiveParm.setData("DATA_TYPE",parmIbsOrdmForUnBil.getData("DATA_TYPE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("DATA_TYPE", n));
			insertIbsOrdMPositiveParm.setData("BILL_NO","");
			insertIbsOrdMPositiveParm.setData("OPT_USER",optUser);
			insertIbsOrdMPositiveParm.setData("OPT_TERM",optTerm);
			insertIbsOrdMPositiveParm.setData("REGION_CODE",parmIbsOrdmForUnBil.getData("REGION_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("REGION_CODE", n));
			insertIbsOrdMPositiveParm.setData("COST_CENTER_CODE",parmIbsOrdmForUnBil.getData("COST_CENTER_CODE", n) == null ? new TNull(String.class) : parmIbsOrdmForUnBil.getData("COST_CENTER_CODE", n));
			result =IBSOrdmTool.getInstance().insertdataM(insertIbsOrdMPositiveParm,connection);
	        if (result.getErrCode() < 0) {
	                err(result.getErrName() + " " + result.getErrText());
	                return result;
	        }
//	        System.out.println("费用主档正数据插入成功！");
			//插入ibs_ordd正数据
			seqNo = 1;
			
			if(parmIbsOrdd.getCount()>0){
				TParm insertIbsOrddPositiveParm=null;
				maxGroupNo=maxGroupNo+1;
				String ordersetCode=parmIbsOrdd.getValue("ORDERSET_CODE", 0);
				String groupNoSeq=parmIbsOrdd.getValue("ORDERSET_GROUP_NO", 0);
				String orderNoSeq=parmIbsOrdd.getValue("CASE_NO_SEQ", 0);
				for(int m = 0;m<parmIbsOrdd.getCount();m++){
					//step9:费用明细档插入正数据（修改身份后的费用）
//					System.out.println("循环第"+m+"次费用明细档");
					//ibs_ordd插入负数据  case_no_seq_new1,bill_date-->sysdate,金额为负
					insertIbsOrddPositiveParm = new TParm();
					insertIbsOrddPositiveParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", m));					
					insertIbsOrddPositiveParm.setData("CASE_NO_SEQ",maxCaseNoSeq);													
					insertIbsOrddPositiveParm.setData("SEQ_NO",seqNo);
					insertIbsOrddPositiveParm.setData("BILL_DATE",sysDate);
					insertIbsOrddPositiveParm.setData("EXEC_DATE",sysDate);
					insertIbsOrddPositiveParm.setData("ORDER_NO",parmIbsOrdd.getData("ORDER_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_NO", m));
					insertIbsOrddPositiveParm.setData("ORDER_SEQ",parmIbsOrdd.getData("ORDER_SEQ", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_SEQ", m));
					insertIbsOrddPositiveParm.setData("ORDER_CODE",parmIbsOrdd.getData("ORDER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CODE", m));
					insertIbsOrddPositiveParm.setData("ORDER_CAT1_CODE",parmIbsOrdd.getData("ORDER_CAT1_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CAT1_CODE", m));
					insertIbsOrddPositiveParm.setData("CAT1_TYPE",parmIbsOrdd.getData("CAT1_TYPE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CAT1_TYPE", m));
					//添加集合医嘱数据组号重新获得======pangben 2016-2-22
					if (null!=parmIbsOrdd.getData("ORDERSET_CODE", m)&&parmIbsOrdd.getValue("ORDERSET_CODE", m).length()>0) {//集合医嘱
						if (ordersetCode.equals(parmIbsOrdd.getValue("ORDERSET_CODE", m))&&
								groupNoSeq.equals(parmIbsOrdd.getValue("ORDERSET_GROUP_NO", m))&&
								orderNoSeq.equals(parmIbsOrdd.getValue("CASE_NO_SEQ", m))) {
							insertIbsOrddPositiveParm.setData("ORDERSET_GROUP_NO",maxGroupNo);
						}else{
							maxGroupNo=maxGroupNo+1;
							insertIbsOrddPositiveParm.setData("ORDERSET_GROUP_NO",maxGroupNo);
							ordersetCode=parmIbsOrdd.getValue("ORDERSET_CODE", m);
							groupNoSeq=parmIbsOrdd.getValue("ORDERSET_GROUP_NO", m);
							orderNoSeq=parmIbsOrdd.getValue("CASE_NO_SEQ", m);
						}
					}else{
						insertIbsOrddPositiveParm.setData("ORDERSET_GROUP_NO",parmIbsOrdd.getData("ORDERSET_GROUP_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_GROUP_NO", m));
					}
					//insertIbsOrddPositiveParm.setData("ORDERSET_GROUP_NO",parmIbsOrdd.getData("ORDERSET_GROUP_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_GROUP_NO", m));
					insertIbsOrddPositiveParm.setData("ORDERSET_CODE",parmIbsOrdd.getData("ORDERSET_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_CODE", m));
					insertIbsOrddPositiveParm.setData("INDV_FLG",parmIbsOrdd.getData("INDV_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INDV_FLG", m));
					insertIbsOrddPositiveParm.setData("DEPT_CODE",parmIbsOrdd.getData("DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DEPT_CODE", m));
					insertIbsOrddPositiveParm.setData("STATION_CODE",parmIbsOrdd.getData("STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("STATION_CODE", m));
					insertIbsOrddPositiveParm.setData("DR_CODE",parmIbsOrdd.getData("DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DR_CODE", m));
					insertIbsOrddPositiveParm.setData("EXE_DEPT_CODE",parmIbsOrdd.getData("EXE_DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DEPT_CODE", m));
					insertIbsOrddPositiveParm.setData("EXE_STATION_CODE",parmIbsOrdd.getData("EXE_STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_STATION_CODE", m));
					insertIbsOrddPositiveParm.setData("EXE_DR_CODE",parmIbsOrdd.getData("EXE_DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DR_CODE", m));
					insertIbsOrddPositiveParm.setData("MEDI_QTY",parmIbsOrdd.getDouble("MEDI_QTY",m));
					insertIbsOrddPositiveParm.setData("MEDI_UNIT",parmIbsOrdd.getData("MEDI_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("MEDI_UNIT", m));
					insertIbsOrddPositiveParm.setData("DOSE_CODE",parmIbsOrdd.getData("DOSE_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSE_CODE", m));
					insertIbsOrddPositiveParm.setData("FREQ_CODE",parmIbsOrdd.getData("FREQ_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("FREQ_CODE", m));
					insertIbsOrddPositiveParm.setData("TAKE_DAYS",parmIbsOrdd.getData("TAKE_DAYS", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("TAKE_DAYS", m));
					insertIbsOrddPositiveParm.setData("DOSAGE_QTY",parmIbsOrdd.getDouble("DOSAGE_QTY",m));
					insertIbsOrddPositiveParm.setData("DOSAGE_UNIT",parmIbsOrdd.getData("DOSAGE_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSAGE_UNIT", m));
					insertIbsOrddPositiveParm.setData("OWN_PRICE",parmIbsOrdd.getDouble("OWN_PRICE",m));
					insertIbsOrddPositiveParm.setData("NHI_PRICE",parmIbsOrdd.getDouble("NHI_PRICE",m));							
					insertIbsOrddPositiveParm.setData("OWN_FLG",parmIbsOrdd.getData("OWN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("OWN_FLG", m));
					insertIbsOrddPositiveParm.setData("BILL_FLG",parmIbsOrdd.getData("BILL_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("BILL_FLG", m));
					insertIbsOrddPositiveParm.setData("REXP_CODE",parmIbsOrdd.getData("REXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REXP_CODE", m));
					insertIbsOrddPositiveParm.setData("BILL_NO", "");
					insertIbsOrddPositiveParm.setData("HEXP_CODE",parmIbsOrdd.getData("HEXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("HEXP_CODE", m));
					insertIbsOrddPositiveParm.setData("BEGIN_DATE",parmIbsOrdd.getData("BEGIN_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("BEGIN_DATE", m));
					insertIbsOrddPositiveParm.setData("END_DATE",parmIbsOrdd.getData("END_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("END_DATE", m));							
					insertIbsOrddPositiveParm.setData("REQUEST_FLG",parmIbsOrdd.getData("REQUEST_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_FLG", m));
					insertIbsOrddPositiveParm.setData("REQUEST_NO",parmIbsOrdd.getData("REQUEST_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_NO", m));
					insertIbsOrddPositiveParm.setData("INV_CODE",parmIbsOrdd.getData("INV_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INV_CODE", m));
					insertIbsOrddPositiveParm.setData("OPT_USER",optUser);
					insertIbsOrddPositiveParm.setData("OPT_TERM",optTerm);							
					insertIbsOrddPositiveParm.setData("ORDER_CHN_DESC",parmIbsOrdd.getData("ORDER_CHN_DESC", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CHN_DESC", m));
					insertIbsOrddPositiveParm.setData("COST_CENTER_CODE",parmIbsOrdd.getData("COST_CENTER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("COST_CENTER_CODE", m));
					insertIbsOrddPositiveParm.setData("SCHD_CODE",parmIbsOrdd.getData("SCHD_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("SCHD_CODE", m));
					insertIbsOrddPositiveParm.setData("CLNCPATH_CODE",parmIbsOrdd.getData("CLNCPATH_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CLNCPATH_CODE", m));
					insertIbsOrddPositiveParm.setData("DS_FLG",parmIbsOrdd.getData("DS_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DS_FLG", m));
					insertIbsOrddPositiveParm.setData("KN_FLG",parmIbsOrdd.getData("KN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("KN_FLG", m));
					insertIbsOrddPositiveParm.setData("INCLUDE_FLG","N");
		    		BIL bil = new BIL();
		    		String chargeHospCode = parmIbsOrdd.getData("HEXP_CODE", m)+"";
		    		String orderCode = parmIbsOrdd.getData("ORDER_CODE", m)+"";
		    		double tot_amt_new = 0.00;
		    		double cost_amt_new = 0.00;
		    		double own_amt = parmIbsOrdd.getDouble("OWN_AMT",m);
		    		double ownRate =1;
		    		// ====pangben 2015-7-28 添加套餐病人修改身份，差异医嘱不打折操作
					if (!newBodyflg) {// 婴儿不需要操作
						double ownPrice=0.00;
						double [] sumPirce= new double [2];
						if (checkLumpWorkFlg && null != odiParm) {// 套餐病人操作
							//if FLG='Y'套餐外
							//使用IBS_ORDD表中的金额\单价 \折扣 注意 INCLUDE_EXEC_FLG='Y' 不需要在操作套餐批次 状态
							//ELSE
							//使用更加身份设置的折扣比例计算 注意 INCLUDE_EXEC_FLG='N' 需要在操作套餐批次 状态
							
							if("Y".equals(parmIbsOrdd.getValue("INCLUDE_FLG",m))&&parmIbsOrdd.getValue("INCLUDE_FLG",m).length()>0){//add by kangy 20170804
								/*insertIbsOrddPositiveParm.setData("TOT_AMT",parmIbsOrdd.getDouble("TOT_AMT",m));
								insertIbsOrddPositiveParm.setData("OWN_PRICE",parmIbsOrdd.getDouble("OWN_PRICE",m));
								insertIbsOrddPositiveParm.setData("OWN_RATE",parmIbsOrdd.getDouble("OWN_RATE",m));*/
								insertIbsOrddPositiveParm.setData("INCLUDE_EXEC_FLG","Y");
								insertIbsOrddPositiveParm.setData("INCLUDE_FLG", "Y");
								ownPrice =parmIbsOrdd.getDouble("OWN_PRICE",m);
								//own_amt = parmIbsOrdd.getDouble("OWN_AMT",m);
								//ownRate = parmIbsOrdd.getDouble("OWN_RATE",m);
								ownRate = bil.getOwnRate(CTZ1, CTZ2, CTZ3,
											chargeHospCode, orderCode);
								tot_amt_new = own_amt * ownRate;
								cost_amt_new = own_amt * ownRate;
							}else{
								insertIbsOrddPositiveParm.setData("INCLUDE_EXEC_FLG","N");
							if (parmIbsOrdd.getValue("ORDER_CODE", m)
									.equals(odiParm.getValue("LUMPWORK_ORDER_CODE",0))) {// 套餐病人
								// 套餐差异医嘱不可以执行折扣
								tot_amt_new = own_amt;
								cost_amt_new = own_amt;
							} else {
								//套餐患者计费根据入院登记计算的折扣操作
				            	//药品、血费根据身份进行统计
				            	//套餐内计费
								
								if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m) && parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m).equals("PHA")||
				            			null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m) && parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m).equals("RA")){
									ownRate = bil.getOwnRate(CTZ1, CTZ2, CTZ3,
											chargeHospCode, orderCode);   	
									sumPirce=IBSTool.getInstance().getOrderOwnPrice(orderCode, level);
									ownPrice=sumPirce[0];
				            	}else{
				    				ownRate=lumpworkRate;
				    				ownPrice=lumpWorkPrice(mrNoNew,caseNoNew, lumpworkCode, orderCode, level,ctzFlg);
				    			}
								own_amt=parmIbsOrdd.getDouble("DOSAGE_QTY",m)*ownPrice;//套餐病患重新计费
//								ownRate = bil.getOwnRate(CTZ1, CTZ2, CTZ3,
//										chargeHospCode, orderCode);
								tot_amt_new = own_amt * ownRate;
								cost_amt_new = own_amt * ownRate;
							}
							}
						} else {
							insertIbsOrddPositiveParm.setData("INCLUDE_EXEC_FLG","N");
							sumPirce=IBSTool.getInstance().getOrderOwnPrice(orderCode, level);
							ownPrice=sumPirce[0];
							own_amt=parmIbsOrdd.getDouble("DOSAGE_QTY",m)*ownPrice;//套餐病患重新计费
							ownRate = bil.getOwnRate(CTZ1, CTZ2, CTZ3,
									chargeHospCode, orderCode);
							tot_amt_new = own_amt * ownRate;
							cost_amt_new = own_amt * ownRate;
						}
						insertIbsOrddPositiveParm.setData("OWN_PRICE", ownPrice);
					} else {
						double ownPrice=0.00;
						double []sumPrice =new double[2];
						if (checkLumpWorkFlg) {
							
							//if INCLUDE_FLG='Y'套餐外
							//使用IBS_ORDD表中的金额\单价 \折扣 注意 INCLUDE_EXEC_FLG='Y' 不需要在操作套餐批次 状态
							//ELSE
							//使用更加身份设置的折扣比例计算 注意 INCLUDE_EXEC_FLG='N' 需要在操作套餐批次 状态
							if("Y".equals(parmIbsOrdd.getValue("INCLUDE_FLG",m))&&parmIbsOrdd.getValue("INCLUDE_FLG",m).length()>0){//add by kangy 20170804
//                              insertIbsOrddPositiveParm.setData("TOT_AMT",parmIbsOrdd.getDouble("TOT_AMT",m));
//								insertIbsOrddPositiveParm.setData("OWN_PRICE",parmIbsOrdd.getDouble("OWN_PRICE",m));
//								insertIbsOrddPositiveParm.setData("OWN_RATE",parmIbsOrdd.getDouble("OWN_RATE",m));
								insertIbsOrddPositiveParm.setData("INCLUDE_EXEC_FLG","Y");
								insertIbsOrddPositiveParm.setData("INCLUDE_FLG", "Y");
								ownPrice =parmIbsOrdd.getDouble("OWN_PRICE",m);
								//own_amt = parmIbsOrdd.getDouble("OWN_AMT",m);
								//ownRate = parmIbsOrdd.getDouble("OWN_RATE",m);
								ownRate = bil.getOwnRate(CTZ1, CTZ2, CTZ3,
										chargeHospCode, orderCode);
								tot_amt_new = own_amt * ownRate;
								cost_amt_new = own_amt * ownRate;
							}else{
								insertIbsOrddPositiveParm.setData("INCLUDE_EXEC_FLG","N");
							// 套餐病人操作
							//套餐患者计费根据入院登记计算的折扣操作
			            	//药品、血费根据身份进行统计
			            	//套餐内计费
							if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m) && parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m).equals("PHA")||
			            			null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m) && parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m).equals("RA")){
								ownRate = bil.getOwnRate(CTZ1, CTZ2, CTZ3,
										chargeHospCode, orderCode);   	
								sumPrice =IBSTool.getInstance().getOrderOwnPrice(orderCode,level);
								ownPrice=sumPrice[0];
			            	}else{
			    				ownRate=lumpworkRate;
			    				ownPrice=lumpWorkPrice(mrNoNew,caseNoNew, lumpworkCode, orderCode, level,ctzFlg);
			    			}
							}
							own_amt=parmIbsOrdd.getDouble("DOSAGE_QTY",m)*ownPrice;//套餐病患重新计费
						}else{
								insertIbsOrddPositiveParm.setData("INCLUDE_EXEC_FLG","N");
								sumPrice =IBSTool.getInstance().getOrderOwnPrice(orderCode, level);
								ownPrice=sumPrice[0];
								own_amt=parmIbsOrdd.getDouble("DOSAGE_QTY",m)*ownPrice;//套餐病患重新计费
								ownRate = bil.getOwnRate(CTZ1, CTZ2, CTZ3,
									chargeHospCode, orderCode);
						}
						insertIbsOrddPositiveParm.setData("OWN_PRICE", ownPrice);
						tot_amt_new = own_amt * ownRate;
						cost_amt_new = own_amt * ownRate;
					}
		    		//===========pangben 29150212  修改身份折扣金额， 入院身份为0.7折 ，改成0.5折 要用自费金额* 当前折扣
		    		insertIbsOrddPositiveParm.setData("TOT_AMT",StringTool.round(tot_amt_new,2));
		    		insertIbsOrddPositiveParm.setData("OWN_AMT",StringTool.round(own_amt,2));
					insertIbsOrddPositiveParm.setData("OWN_RATE",ownRate);
		    		insertIbsOrddPositiveParm.setData("COST_AMT",StringTool.round(cost_amt_new,2));
		    		seqNo++;
		    		//System.out.println("明细档正数据参数：====="+insertIbsOrddPositiveParm);
		    		result =IBSOrdmTool.getInstance().insertIbsOrddData(insertIbsOrddPositiveParm,connection);
			        if (result.getErrCode() < 0) {
			                err(result.getErrName() + " " + result.getErrText());
			                return result;
			        }
//			        System.out.println("费用明细档正数据插入成功！");
				}						
			}
			maxCaseNoSeq++;
		  }
		}
		exeNewParm.setData("maxCaseNoSeq",maxCaseNoSeq);
		exeNewParm.setData("maxGroupNo",maxGroupNo);
		//未结账的费用的处理=======================结束
		return result;
	}
	/**
	 * 获得套餐中的医嘱单价
	 * @param caseNo
	 * @param lumpworkCode
	 * @param orderCode
	 * @param level
	 * @return
	 */
	private double lumpWorkPrice(String mrNo,String caseNo,
			String lumpworkCode,String orderCode,String level,boolean ctzFlg){
		TParm lumpParm=new TParm();
		if(ctzFlg){
			lumpParm.setData("CASE_NO",caseNo);
		}else{
			lumpParm.setData("MR_NO",mrNo);
			TParm lumpWorkParm = MEMTool.getInstance().selectMemPackTradeM(mrNo,
					lumpworkCode);
			lumpParm.setData("TRADE_NO", lumpWorkParm.getValue("TRADE_NO", 0));
		}
		lumpParm.setData("PACKAGE_CODE",lumpworkCode);
		lumpParm.setData("ORDER_CODE",orderCode);
		TParm feeParm= MEMTool.getInstance().selectMemPackageSectionDByCaseNo(lumpParm); 
		double ownPrice = 0.00;
		if(feeParm.getErrCode()<0||feeParm.getCount()<=0){//套餐外医嘱，正常计费
			 double [] sumPrice = IBSTool.getInstance().getOrderOwnPrice(orderCode,level);
			 ownPrice = sumPrice[0];
		}else{
			ownPrice = feeParm.getDouble("OWN_PRICE", 0);
		}
		return ownPrice;
	}
	/**
	 * 住院登记去掉套餐，将差异金额医嘱作废
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onReLumpWorkIbsOrddOrder(TParm parm,TConnection conn) {
//		String statCode = OdiMainTool.getInstance().getOdiSysParmData(
//				"ODI_STAT_CODE").toString();// 临时医嘱频次	
		TParm odiParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT LUMPWORK_ORDER_CODE FROM ODI_SYSPARM"));
		String sumAmtSql="SELECT SUM(A.TOT_AMT) TOT_AMT FROM IBS_ORDD A,IBS_ORDM B WHERE" +
				"  A.CASE_NO=B.CASE_NO AND A.CASE_NO_SEQ=B.CASE_NO_SEQ AND A.CASE_NO = '"
				+ parm.getData("CASE_NO")
				+ "' AND A.ORDER_CODE='"
				+ odiParm.getValue("LUMPWORK_ORDER_CODE", 0)
				+ "' AND A.BILL_EXE_FLG='N'";
		TParm sumParm = new TParm(TJDODBTool.getInstance().select(
				sumAmtSql));
		if (sumParm.getErrCode()<0) {
			return sumParm;
		}
		if (sumParm.getDouble("TOT_AMT",0)==0) {
			return new TParm();
		}
		String sqlIbsOrdd = "SELECT A.*,B.MR_NO,B.IPD_NO, B.BED_NO, B.DATA_TYPE,  B.REGION_CODE, B.COST_CENTER_CODE "
				+ "FROM IBS_ORDD A ,IBS_ORDM B WHERE A.CASE_NO=B.CASE_NO AND A.CASE_NO_SEQ=B.CASE_NO_SEQ AND A.CASE_NO = '"
				+ parm.getData("CASE_NO")
				+ "' AND A.ORDER_CODE='"
				+ odiParm.getValue("LUMPWORK_ORDER_CODE", 0)
				+ "' ";
		TParm parmIbsOrdd = new TParm(TJDODBTool.getInstance().select(
				sqlIbsOrdd));
		if (parmIbsOrdd.getErrCode() < 0) {
			return parmIbsOrdd;
		}
		String sql="UPDATE IBS_ORDD SET INCLUDE_EXEC_FLG='Y',BILL_EXE_FLG='Y' WHERE CASE_NO = '"
				+ parm.getData("CASE_NO")
				+ "' AND ORDER_CODE='"
				+odiParm.getValue("LUMPWORK_ORDER_CODE",0) + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(
				sql,conn));
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		
		TParm maxCaseNoSeqParm = selMaxCaseNoSeq(parm.getValue("CASE_NO"));
		int maxCaseNoSeqOne = maxCaseNoSeqParm.getInt("CASE_NO_SEQ", 0) + 1;
		Timestamp sysDate = SystemTool.getInstance().getDate();
		result = new TParm();
		int seqNo = 1;
		// 循环费用明细档
		if (parmIbsOrdd.getCount() > 0) {
			TParm insertIbsOrddNegativeParm =  new TParm();
			insertIbsOrddNegativeParm.setData("CASE_NO",
					parmIbsOrdd.getData("CASE_NO", 0) == null ? new TNull(
							String.class) : parmIbsOrdd.getData("CASE_NO",
							0));
			if (maxCaseNoSeqParm.getCount("CASE_NO_SEQ") == 0) {
				insertIbsOrddNegativeParm.setData("CASE_NO_SEQ", 1);
			} else {
				insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",
						maxCaseNoSeqOne);
			}
			insertIbsOrddNegativeParm.setData("SEQ_NO", seqNo);
			insertIbsOrddNegativeParm.setData("BILL_DATE", sysDate);
			insertIbsOrddNegativeParm.setData("EXEC_DATE", sysDate);
			insertIbsOrddNegativeParm.setData("ORDER_NO", parmIbsOrdd
					.getData("ORDER_NO", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("ORDER_NO", 0));
			insertIbsOrddNegativeParm.setData("ORDER_SEQ", parmIbsOrdd
					.getData("ORDER_SEQ", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("ORDER_SEQ", 0));
			insertIbsOrddNegativeParm.setData("ORDER_CODE", parmIbsOrdd
					.getData("ORDER_CODE", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("ORDER_CODE", 0));
			insertIbsOrddNegativeParm
					.setData("ORDER_CAT1_CODE", parmIbsOrdd.getData(
							"ORDER_CAT1_CODE", 0) == null ? new TNull(
							String.class) : parmIbsOrdd.getData(
							"ORDER_CAT1_CODE", 0));
			insertIbsOrddNegativeParm.setData("CAT1_TYPE", parmIbsOrdd
					.getData("CAT1_TYPE", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("CAT1_TYPE", 0));

			insertIbsOrddNegativeParm
					.setData("ORDERSET_GROUP_NO", parmIbsOrdd.getData(
							"ORDERSET_GROUP_NO", 0) == null ? new TNull(
							String.class) : parmIbsOrdd.getData(
							"ORDERSET_GROUP_NO", 0));
			insertIbsOrddNegativeParm
					.setData("ORDERSET_CODE", parmIbsOrdd.getData(
							"ORDERSET_CODE", 0) == null ? new TNull(
							String.class) : parmIbsOrdd.getData(
							"ORDERSET_CODE", 0));
			insertIbsOrddNegativeParm.setData("INDV_FLG", parmIbsOrdd
					.getData("INDV_FLG", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("INDV_FLG", 0));
			insertIbsOrddNegativeParm.setData("DEPT_CODE", parmIbsOrdd
					.getData("DEPT_CODE", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("DEPT_CODE", 0));
			insertIbsOrddNegativeParm.setData("STATION_CODE", parmIbsOrdd
					.getData("STATION_CODE", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("STATION_CODE", 0));
			insertIbsOrddNegativeParm.setData("DR_CODE",
					parmIbsOrdd.getData("DR_CODE", 0) == null ? new TNull(
							String.class) : parmIbsOrdd.getData("DR_CODE",
							0));
			insertIbsOrddNegativeParm
					.setData("EXE_DEPT_CODE", parmIbsOrdd.getData(
							"EXE_DEPT_CODE", 0) == null ? new TNull(
							String.class) : parmIbsOrdd.getData(
							"EXE_DEPT_CODE", 0));
			insertIbsOrddNegativeParm
					.setData("EXE_STATION_CODE", parmIbsOrdd.getData(
							"EXE_STATION_CODE", 0) == null ? new TNull(
							String.class) : parmIbsOrdd.getData(
							"EXE_STATION_CODE", 0));
			insertIbsOrddNegativeParm.setData("EXE_DR_CODE", parmIbsOrdd
					.getData("EXE_DR_CODE", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("EXE_DR_CODE", 0));
			insertIbsOrddNegativeParm.setData("MEDI_QTY", -parmIbsOrdd
					.getDouble("MEDI_QTY", 0));
			insertIbsOrddNegativeParm.setData("MEDI_UNIT", parmIbsOrdd
					.getData("MEDI_UNIT", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("MEDI_UNIT", 0));
			insertIbsOrddNegativeParm.setData("DOSE_CODE", parmIbsOrdd
					.getData("DOSE_CODE", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("DOSE_CODE", 0));
			insertIbsOrddNegativeParm.setData("FREQ_CODE", parmIbsOrdd
					.getData("FREQ_CODE", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("FREQ_CODE", 0));
			insertIbsOrddNegativeParm.setData("TAKE_DAYS", parmIbsOrdd
					.getData("TAKE_DAYS", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("TAKE_DAYS", 0));
			insertIbsOrddNegativeParm.setData("DOSAGE_QTY", -parmIbsOrdd
					.getDouble("DOSAGE_QTY", 0));
			insertIbsOrddNegativeParm.setData("DOSAGE_UNIT", parmIbsOrdd
					.getData("DOSAGE_UNIT", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("DOSAGE_UNIT", 0));
			insertIbsOrddNegativeParm.setData("OWN_PRICE", StringTool.round(sumParm.getDouble("TOT_AMT",0),2));
			insertIbsOrddNegativeParm.setData("NHI_PRICE", StringTool.round(sumParm.getDouble("TOT_AMT",0),2));
			insertIbsOrddNegativeParm.setData("TOT_AMT", -StringTool.round(sumParm.getDouble("TOT_AMT",0),2));
			insertIbsOrddNegativeParm.setData("OWN_FLG",
					parmIbsOrdd.getData("OWN_FLG", 0) == null ? new TNull(
							String.class) : parmIbsOrdd.getData("OWN_FLG",
							0));
			insertIbsOrddNegativeParm.setData("BILL_FLG", parmIbsOrdd
					.getData("BILL_FLG", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("BILL_FLG", 0));
			insertIbsOrddNegativeParm.setData("REXP_CODE", parmIbsOrdd
					.getData("REXP_CODE", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("REXP_CODE", 0));
			insertIbsOrddNegativeParm.setData("BILL_NO","");
			insertIbsOrddNegativeParm.setData("HEXP_CODE", parmIbsOrdd
					.getData("HEXP_CODE", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("HEXP_CODE", 0));
			insertIbsOrddNegativeParm
					.setData("BEGIN_DATE", parmIbsOrdd.getData(
							"BEGIN_DATE", 0) == null ? new TNull(
							Timestamp.class) : parmIbsOrdd.getData(
							"BEGIN_DATE", 0));
			insertIbsOrddNegativeParm.setData("END_DATE", parmIbsOrdd
					.getData("END_DATE", 0) == null ? new TNull(
					Timestamp.class) : parmIbsOrdd.getData("END_DATE", 0));
			insertIbsOrddNegativeParm.setData("OWN_AMT", -StringTool.round(sumParm.getDouble("TOT_AMT",0),2));
			insertIbsOrddNegativeParm.setData("OWN_RATE", parmIbsOrdd
					.getDouble("OWN_RATE", 0));
			insertIbsOrddNegativeParm.setData("REQUEST_FLG", parmIbsOrdd
					.getData("REQUEST_FLG", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("REQUEST_FLG", 0));
			insertIbsOrddNegativeParm.setData("REQUEST_NO", parmIbsOrdd
					.getData("REQUEST_NO", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("REQUEST_NO", 0));
			insertIbsOrddNegativeParm.setData("INV_CODE", parmIbsOrdd
					.getData("INV_CODE", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("INV_CODE", 0));
			insertIbsOrddNegativeParm.setData("OPT_USER", parm
					.getValue("OPT_USER"));
			insertIbsOrddNegativeParm.setData("OPT_TERM", parm
					.getValue("OPT_TERM"));
			insertIbsOrddNegativeParm.setData("COST_AMT", -StringTool.round(sumParm.getDouble("TOT_AMT",0),2));
			insertIbsOrddNegativeParm.setData("ORDER_CHN_DESC", parmIbsOrdd
					.getData("ORDER_CHN_DESC", 0) == null ? new TNull(
					String.class) : parmIbsOrdd
					.getData("ORDER_CHN_DESC", 0));
			insertIbsOrddNegativeParm
					.setData("COST_CENTER_CODE", parmIbsOrdd.getData(
							"COST_CENTER_CODE", 0) == null ? new TNull(
							String.class) : parmIbsOrdd.getData(
							"COST_CENTER_CODE", 0));
			insertIbsOrddNegativeParm.setData("SCHD_CODE", parmIbsOrdd
					.getData("SCHD_CODE", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("SCHD_CODE", 0));
			insertIbsOrddNegativeParm
					.setData("CLNCPATH_CODE", parmIbsOrdd.getData(
							"CLNCPATH_CODE", 0) == null ? new TNull(
							String.class) : parmIbsOrdd.getData(
							"CLNCPATH_CODE", 0));
			insertIbsOrddNegativeParm.setData("DS_FLG", parmIbsOrdd
					.getData("DS_FLG", 0) == null ? new TNull(String.class)
					: parmIbsOrdd.getData("DS_FLG", 0));
			insertIbsOrddNegativeParm.setData("KN_FLG", parmIbsOrdd
					.getData("KN_FLG", 0) == null ? new TNull(String.class)
					: parmIbsOrdd.getData("KN_FLG", 0));
			insertIbsOrddNegativeParm.setData("INCLUDE_FLG", parmIbsOrdd
					.getData("INCLUDE_FLG", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("INCLUDE_FLG", 0));
			insertIbsOrddNegativeParm.setData("INCLUDE_EXEC_FLG", "Y");// 作废数据处理状态
			insertIbsOrddNegativeParm.setData("BILL_EXE_FLG", "Y");
			seqNo++;
			result = IBSOrdmTool.getInstance().insertdataLumpworkDExe(
					insertIbsOrddNegativeParm, conn);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
			// System.out.println("费用明细负数据插入成功！");
		
			// step8:费用主档插入正数据（修改身份后的费用）
			// 插入ibs_ordm正数据
			TParm insertIbsOrdMPositiveParm = new TParm();
			insertIbsOrdMPositiveParm.setData("CASE_NO", parmIbsOrdd.getData(
					"CASE_NO", 0));
			insertIbsOrdMPositiveParm.setData("CASE_NO_SEQ", maxCaseNoSeqOne);
			insertIbsOrdMPositiveParm.setData("BILL_DATE", sysDate);
			insertIbsOrdMPositiveParm.setData("IPD_NO", parmIbsOrdd.getData(
					"IPD_NO", 0) == null ? new TNull(String.class) : parmIbsOrdd
					.getData("IPD_NO", 0));
			insertIbsOrdMPositiveParm.setData("MR_NO", parmIbsOrdd.getData("MR_NO",
					0) == null ? new TNull(String.class) : parmIbsOrdd.getData(
					"MR_NO", 0));
			insertIbsOrdMPositiveParm.setData("DEPT_CODE", parmIbsOrdd.getData(
					"DEPT_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd
					.getData("DEPT_CODE", 0));
			insertIbsOrdMPositiveParm.setData("STATION_CODE", parmIbsOrdd.getData(
					"STATION_CODE", 0) == null ? new TNull(String.class)
					: parmIbsOrdd.getData("STATION_CODE", 0));
			insertIbsOrdMPositiveParm.setData("BED_NO", parmIbsOrdd.getData(
					"BED_NO", 0) == null ? new TNull(String.class) : parmIbsOrdd
					.getData("BED_NO", 0));
			insertIbsOrdMPositiveParm.setData("DATA_TYPE", parmIbsOrdd.getData(
					"DATA_TYPE", 0) == null ? new TNull(String.class) : parmIbsOrdd
					.getData("DATA_TYPE", 0));
			insertIbsOrdMPositiveParm.setData("BILL_NO", "");
			insertIbsOrdMPositiveParm
					.setData("OPT_USER", parm.getValue("OPT_USER"));
			insertIbsOrdMPositiveParm
					.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			insertIbsOrdMPositiveParm.setData("REGION_CODE", parmIbsOrdd.getData(
					"REGION_CODE", 0) == null ? new TNull(String.class)
					: parmIbsOrdd.getData("REGION_CODE", 0));
			insertIbsOrdMPositiveParm.setData("COST_CENTER_CODE", parmIbsOrdd
					.getData("COST_CENTER_CODE", 0) == null ? new TNull(
					String.class) : parmIbsOrdd.getData("COST_CENTER_CODE", 0));
			result = IBSOrdmTool.getInstance().insertdataM(
					insertIbsOrdMPositiveParm, conn);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
		}
		
		return result;
	}
}
