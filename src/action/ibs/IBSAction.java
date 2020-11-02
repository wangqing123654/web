package action.ibs;

import java.sql.Timestamp;
import java.util.List;

import com.dongyang.action.TAction;
import com.dongyang.db.TConnection;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.adm.ADMInpTool;
import jdo.bil.BIL;
import jdo.ibs.IBSLumpWorkBatchNewTool;
import jdo.ibs.IBSLumpWorkBatchTool;
import jdo.ibs.IBSNewTool;
import jdo.ibs.IBSOrdmTool;
import jdo.ibs.IBSTool;
import jdo.mem.MEMTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

/**
 *
 * <p>Title: 住院计价动作类</p>
 *
 * <p>Description: 住院计价动作类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class IBSAction
    extends TAction {	
//    /**
//     * 新增医嘱
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onNewIBSOrder(String dataType, TParm parm) {
//        TConnection connection = getConnection();
//        TParm result = new TParm();
//        //IBS(补充批价产生)
//        if (dataType.equals("1")) {
//            result = IBSOrdermTool.getInstance().insertdata(parm, connection);
//            result = IBSOrderdTool.getInstance().insertdata(parm,connection);
//        }
//        //UDD(药房发药)
//        if (dataType.equals("2")) {
//        }
//        //INW(护士执行计费)
//        if (dataType.equals("3")) {
//        }
//        //医技计费
//        if (dataType.equals("4")) {
//
//        }
//
//        connection.commit();
//        connection.close();
//        return result;
//
//    }
    /**
     * 插入计价档
     * @param parm TParm
     * @return TParm
     */
    public TParm onNewIBSBill(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = IBSTool.getInstance().insertIBSBillData(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 执行缴费作业
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveIBSCharge(TParm parm){
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = IBSTool.getInstance().insertIBSChargeData(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        String sqlCardTypeAndRemark=parm.getValue("sqlCardTypeAndRemark");
        TParm cardTypeParm = new TParm(TJDODBTool.getInstance().update(sqlCardTypeAndRemark,connection));
    	if (cardTypeParm.getErrCode() < 0) {
    		err(cardTypeParm.getErrName() + " " + cardTypeParm.getErrText());
    		connection.close();
            return cardTypeParm;
        }
    	
    	//医疗卡扣款
		if(parm.getData("ektSql") != null){
			List<String> ektSql = (List<String>) parm.getData("ektSql");
			for (int i = 0; i < ektSql.size(); i++) {
				result = new TParm(TJDODBTool.getInstance().update(ektSql.get(i), connection));
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
			}
			 String updateEktTradeSql=parm.getValue("updateEktTradeSql");
			 result = new TParm(TJDODBTool.getInstance().update(updateEktTradeSql, connection));
			 if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
		}
    	
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 作废账单
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveBillReturn(TParm parm){
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = IBSTool.getInstance().insertBillReturn(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
	 * 修改身份
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author caowl
	 */
	public TParm updBill(TParm parm) {

		TConnection connection = getConnection();
		TParm result = new TParm();
		TParm temp=new TParm();
		TParm selParm =new TParm();
		//如果存在婴儿将婴儿身份一起修改====pangben 2015-7-28
//		if (null!=parm.getValue("CHECK_LUMPWORK_FLG")&&parm.getValue("CHECK_LUMPWORK_FLG").equals("Y")) {//套餐病患需要操作婴儿
//			
//		}
		String sql="SELECT CASE_NO,LUMPWORK_CODE FROM ADM_INP WHERE M_CASE_NO='"+parm.getValue("CASE_NO")+"' AND NEW_BORN_FLG='Y'";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < selParm.getCount("CASE_NO"); i++) {
			temp.setData("CASE_NO", selParm.getValue("CASE_NO",i));
			temp.setData("CTZ1_CODE", parm.getValue("CTZ1_CODE"));
			temp.setData("CTZ2_CODE", parm.getValue("CTZ2_CODE"));
			temp.setData("CTZ3_CODE", parm.getValue("CTZ3_CODE"));
			temp.setData("CTZ11_CODE", parm.getValue("CTZ11_CODE"));
			temp.setData("CTZ22_CODE", parm.getValue("CTZ22_CODE"));
			temp.setData("CTZ33_CODE", parm.getValue("CTZ33_CODE"));
			temp.setData("OPT_USER", parm.getValue("OPT_USER"));
			temp.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			if(null!= selParm.getData("LUMPWORK_CODE",i) && selParm.getValue("LUMPWORK_CODE",i).length()>0){
				result = IBSNewTool.getInstance().updBillExe(temp,true,true,null,connection,true);
			}else{
				result = IBSNewTool.getInstance().updBillExe(temp,true,false,null,connection,true);
			}
			
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		if (null!=parm.getValue("CHECK_LUMPWORK_FLG")&&parm.getValue("CHECK_LUMPWORK_FLG").equals("Y")) {
			TParm odiParm = new TParm(TJDODBTool.getInstance().select("SELECT LUMPWORK_ORDER_CODE FROM ODI_SYSPARM"));
			result = IBSNewTool.getInstance().updBillExe(parm,false,true,odiParm, connection,true);
		}else{
			result = IBSNewTool.getInstance().updBillExe(parm,false,false,null, connection,true);
		}
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		String ssql="UPDATE IBS_ORDD SET INCLUDE_EXEC_FLG='Y' WHERE CASE_NO='"+parm.getValue("CASE_NO")+"'";
		result=new TParm(TJDODBTool.getInstance().update(ssql));
		if (result.getErrCode()<0) {
			return result;
		}
		for (int i = 0; i < selParm.getCount("CASE_NO"); i++) {
			String msql="UPDATE IBS_ORDD SET INCLUDE_EXEC_FLG='Y' WHERE CASE_NO='"+selParm.getValue("CASE_NO",i)+"'";
			result=new TParm(TJDODBTool.getInstance().update(msql));
			if (result.getErrCode()<0) {
				return result;
			}
		}
		if (null!=parm.getValue("CHECK_LUMPWORK_FLG")&&parm.getValue("CHECK_LUMPWORK_FLG").equals("Y")) {//套餐病患需要操作婴儿
			for (int i = 0; i < selParm.getCount("CASE_NO"); i++) {
				result = IBSTool.getInstance().updateAdmTotAmt(selParm.getRow(i));
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		result = IBSTool.getInstance().updateAdmTotAmt(parm);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}
	/**
	 * 重新生成账单
	 * */
	public TParm onBill(TParm parm){
		//判断如果存在费用 那么产生账单 否则不产生
		TConnection connection = getConnection();
		
        TParm result = IBSTool.getInstance().insertIBSBillData(parm, connection);
//        System.out.println("费用回参："+result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
		connection.close();

		return result;
	}
	 /**
     * 修改身份，婴儿已经出院，自动生成账单
     */
	public TParm onNewBadyBill(TParm parm){
		TConnection connection = getConnection();
		TParm result = new TParm();
		TParm rowParm = null;
		for(int i=0; i< parm.getCount("CASE_NO");i++){
			rowParm = parm.getRow(i);
			rowParm.setData("OPT_USER",parm.getValue("OPT_USER"));
			rowParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
			result = IBSTool.getInstance().insertIBSBillDataTwo(rowParm, connection);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            connection.rollback();
	            connection.close();
	            return result;
	        }
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * 
	* @Title: onSaveLumpworkCheck
	* @Description: TODO(包干费用审核)
	* @author pangben 2015-6-16
	* @param parm
	* @return
	* @throws
	 */
	public TParm onSaveLumpworkCheck(TParm parm){
		TConnection connection = getConnection();
		TParm result=null;
		String sql="UPDATE ADM_INP SET INCLUDE_FLG ='"+parm.getValue("ADM_INCLUDE_FLG")+
		"'  WHERE CASE_NO='"+parm.getValue("CASE_NO")+"'";
		result = new TParm(TJDODBTool.getInstance().update(sql,
				connection));
		if (result.getErrCode() < 0) {
			result.setErr(-1, "包干套餐审核修改出现问题");
			connection.rollback();
			connection.close();
			return result;
		}
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		double lumpworkRate=parm.getDouble("LUMPWORK_RATE");//套餐折扣
		String lumpworkCode=parm.getValue("LUMPWORK_CODE");//套餐
		String level=parm.getValue("SERVICE_LEVEL");//服务
		String opt_user=parm.getValue("OPT_USER");
		String opt_term=parm.getValue("OPT_TERM");
		TParm maxCaseNoSeqParm = selMaxCaseNoSeq(parm.getValue("CASE_NO"));
		int maxCaseNoSeq = maxCaseNoSeqParm.getInt("CASE_NO_SEQ", 0) + 1;
		if (maxCaseNoSeqParm.getCount("CASE_NO_SEQ") == 0) {
			maxCaseNoSeq=1;
		}
		TParm queryInfoParm=IBSNewTool.getInstance().onCheckLumWorkCaseNo(parm.getValue("MR_NO"),parm.getValue("CASE_NO"));
		//String mrNoNew=queryInfoParm.getValue("MR_NO");
		String caseNoNew=queryInfoParm.getValue("CASE_NO");
		for (int i = 0; i < parm.getCount("SEQ_NO"); i++) {
			String querySql="";
			TParm parmIbsOrdd=null;
			TParm parmIbsOrdm=null; 
			int groupNo=-1;
			if (parm.getValue("ORDER_CODE",i).equals(parm.getValue("ORDERSET_CODE",i))
						&&parm.getValue("INDV_FLG",i).equals("N")) {//集合医嘱
				querySql = "SELECT A.*,B.CAT1_TYPE AS SYS_FEE_CAT1_TYPE,B.CHARGE_HOSP_CODE FROM IBS_ORDD A ,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='" + parm.getValue("CASE_NO")
					+ "' AND A.CASE_NO_SEQ='"+parm.getValue("CASE_NO_SEQ",i)+
					"' AND A.ORDERSET_CODE='"+parm.getValue("ORDERSET_CODE",i)+
					"' AND A.ORDERSET_GROUP_NO='"+parm.getValue("ORDERSET_GROUP_NO",i)+"'";
				groupNo=selMaxOrderSetGroupNo(parm.getValue("CASE_NO"), parm.getValue("ORDER_CODE",i));
			}else{
				querySql = "SELECT A.*, B.CAT1_TYPE AS SYS_FEE_CAT1_TYPE,B.CHARGE_HOSP_CODE FROM IBS_ORDD A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='" + parm.getValue("CASE_NO")
					+ "' AND A.CASE_NO_SEQ='" + parm.getData("CASE_NO_SEQ",i)
					+ "' AND A.SEQ_NO='" + parm.getData("SEQ_NO",i) + "'";
			}
			parmIbsOrdd = new TParm(TJDODBTool.getInstance().select(querySql));
			querySql = "SELECT * FROM IBS_ORDM WHERE  CASE_NO='"  + parm.getValue("CASE_NO")
					+ "' AND CASE_NO_SEQ='" + parm.getData("CASE_NO_SEQ",i) + "'";
			parmIbsOrdm = new TParm(TJDODBTool.getInstance().select(querySql));
			result = onSaveIbsOrdmAndOrddParm(parm.getRow(i), parmIbsOrdm.getRow(0), parmIbsOrdd, parm.getValue("CASE_NO"),
					connection,parm.getBoolean("INCLUDE_FLG",i),groupNo,CTZ1,CTZ2,CTZ3,opt_user,
					opt_term,maxCaseNoSeq,lumpworkRate,lumpworkCode,level,caseNoNew);
			if (result.getErrCode() < 0) {
				result.setErr(-1, "包干套餐审核修改出现问题");
				connection.rollback();
				connection.close();
				return result;
			}
			maxCaseNoSeq=result.getInt("CASE_NO_SEQ");
		}
		connection.commit();
		connection.close();
		result = IBSTool.getInstance().updateAdmTotAmt(parm);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}
	/**
	 * 集合医嘱公用
	 * @param parmIbsOrddNew
	 * @param parm
	 * @param seqNo
	 * @param caseNo
	 * @param groupNo
	 * @param sysDate
	 * @param parmIbsOrdd
	 * @param maxCaseNoSeq
	 * @param CTZ1
	 * @param CTZ2
	 * @param CTZ3
	 * @param bil
	 * @param lumpworkRate
	 * @param connection
	 * @param flg
	 * @param level
	 * @return
	 */
	private TParm onExeLumpWorkCharge(TParm parmIbsOrddNew,TParm parm,int seqNo,String caseNo,
			int groupNo,Timestamp sysDate,TParm parmIbsOrdd,int maxCaseNoSeq,String CTZ1,
			String CTZ2,String CTZ3,BIL bil,double lumpworkRate,TConnection connection,boolean flg,String level){
		TParm insertIbsOrddNegativeParm=null;
		TParm result=new TParm();
		for (int j = 0; j < parmIbsOrddNew.getCount(); j++) {
			//重新获得折扣后的金额
	        insertIbsOrddNegativeParm = new TParm();	
	        getIbsOrddParm(insertIbsOrddNegativeParm, parm, parmIbsOrddNew, j, sysDate,parmIbsOrdd);
	        insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",groupNo+2);
			insertIbsOrddNegativeParm.setData("CASE_NO",caseNo);					
			insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);							
			insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
			double ownRate =0.00;
			double ownPrice=0.00;
			if (flg) {//套餐外
				if ("2".equals(level)) {
                    ownPrice = parmIbsOrddNew.getDouble("OWN_PRICE2", j);
                } else if ("3".equals(level)) {
                    ownPrice = parmIbsOrddNew.getDouble("OWN_PRICE3", j);
                } else{
                    ownPrice = parmIbsOrddNew.getDouble("OWN_PRICE", j);
                }
				ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrddNew.getValue("HEXP_CODE", j) ,
						 parmIbsOrddNew.getValue("ORDER_CODE", j));		
			}else{
				//药品、血费根据身份统计
				if(null!=parmIbsOrddNew.getValue("CAT1_TYPE",j)
						&&parmIbsOrddNew.getValue("CAT1_TYPE",j).equals("PHA")||
						null!=parmIbsOrddNew.getValue("HEXP_CODE",j)&&parmIbsOrddNew.getValue("HEXP_CODE",j).equals("RA")){
					 ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrddNew.getValue("HEXP_CODE", j) ,
							 parmIbsOrddNew.getValue("ORDER_CODE", j));		 
				}else{
					ownRate =lumpworkRate;//包干内根据住院登记操作时计算的折扣统计
				}
				ownPrice = parmIbsOrddNew.getDouble("OWN_PRICE", j);
			}
			if (parmIbsOrddNew.getValue("ORDER_CODE",j).equals(parmIbsOrddNew.getValue("ORDERSET_CODE",j))) {
				insertIbsOrddNegativeParm.setData("OWN_PRICE",0);
				insertIbsOrddNegativeParm.setData("NHI_PRICE",0);
				insertIbsOrddNegativeParm.setData("OWN_AMT",0);
				insertIbsOrddNegativeParm.setData("TOT_AMT",0);
				insertIbsOrddNegativeParm.setData("COST_AMT",0);
			}else{
				parmIbsOrddNew.setData("OWN_AMT",j,ownPrice*
	   					parmIbsOrddNew.getDouble("DOSAGE_QTY",j)*parm.getDouble("DOSAGE_QTY"));
				insertIbsOrddNegativeParm.setData("OWN_PRICE",ownPrice);
				insertIbsOrddNegativeParm.setData("OWN_AMT",ownPrice*
	   					parmIbsOrddNew.getDouble("DOSAGE_QTY",j)*parm.getDouble("DOSAGE_QTY"));
				insertIbsOrddNegativeParm.setData("TOT_AMT",ownPrice*
	   					parmIbsOrddNew.getDouble("DOSAGE_QTY",j)*parm.getDouble("DOSAGE_QTY")*ownRate);
			}
			insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
			if (flg) {//套餐外
				insertIbsOrddNegativeParm.setData("INCLUDE_FLG","Y");
			}else{
				insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
			}

			result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
			if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            return result;
	        }
			++seqNo;
		}
		return result;
	}
	/**
	 * 
	* @Title: getIbsOrddParm
	* @Description: TODO(公用獲得IBS_ORDD表入參)
	* @author pangben
	* @param parm
	* @param parmIbsOrdm
	* @param parmIbsOrdd
	* @param caseNo
	* @param connection
	* @param flg
	* @return
	* @throws
	 */
	private TParm onSaveIbsOrdmAndOrddParm(TParm parm,TParm parmIbsOrdm ,TParm parmIbsOrdd,
			String caseNo,TConnection connection,boolean flg,int groupNo,String CTZ1,
			String CTZ2,String CTZ3,String opt_user,String opt_term,int maxCaseNoSeq,
			double lumpworkRate,String lumpworkCode,String level,String caseNoNew){
		//新的身份
		// 得到系统时间
		Timestamp sysDate = SystemTool.getInstance().getDate();
		//循环费用明细档
		TParm insertIbsOrdMNegativeParm = new TParm();					
		insertIbsOrdMNegativeParm.setData("CASE_NO",caseNo);
		insertIbsOrdMNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
		insertIbsOrdMNegativeParm.setData("BILL_DATE",sysDate);
		insertIbsOrdMNegativeParm.setData("IPD_NO",parmIbsOrdm.getData("IPD_NO") == null ? new TNull(String.class) : parmIbsOrdm.getData("IPD_NO"));
		insertIbsOrdMNegativeParm.setData("MR_NO",parmIbsOrdm.getData("MR_NO") == null ? new TNull(String.class) : parmIbsOrdm.getData("MR_NO"));
		insertIbsOrdMNegativeParm.setData("DEPT_CODE",parmIbsOrdm.getData("DEPT_CODE") == null ? new TNull(String.class) : parmIbsOrdm.getData("DEPT_CODE"));
		insertIbsOrdMNegativeParm.setData("STATION_CODE",parmIbsOrdm.getData("STATION_CODE") == null ? new TNull(String.class) : parmIbsOrdm.getData("STATION_CODE"));
		insertIbsOrdMNegativeParm.setData("BED_NO",parmIbsOrdm.getData("BED_NO") == null ? new TNull(String.class) : parmIbsOrdm.getData("BED_NO"));
		insertIbsOrdMNegativeParm.setData("DATA_TYPE",parmIbsOrdm.getData("DATA_TYPE") == null ? new TNull(String.class) : parmIbsOrdm.getData("DATA_TYPE"));
		insertIbsOrdMNegativeParm.setData("BILL_NO","");
		insertIbsOrdMNegativeParm.setData("OPT_USER",opt_user);
		insertIbsOrdMNegativeParm.setData("OPT_TERM",opt_term);
		insertIbsOrdMNegativeParm.setData("REGION_CODE",parmIbsOrdm.getData("REGION_CODE") == null ? new TNull(String.class) : parmIbsOrdm.getData("REGION_CODE"));
		insertIbsOrdMNegativeParm.setData("COST_CENTER_CODE",parmIbsOrdm.getData("COST_CENTER_CODE") == null ? new TNull(String.class) : parmIbsOrdm.getData("COST_CENTER_CODE"));
		
		TParm result =IBSOrdmTool.getInstance().insertdataM(insertIbsOrdMNegativeParm,connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        parm.setData("OPT_USER",opt_user);
        parm.setData("OPT_TERM",opt_term);
        BIL bil = new BIL();
		if(parmIbsOrdd.getCount()>0){
			TParm insertIbsOrddNegativeParm =null;
			int seqNo = 1;
			for(int m = 0;m<parmIbsOrdd.getCount();m++){
				//step7:费用明细档插入负数据
//				System.out.println("循环第"+m+"次费用明细");
				//ibs_ordd插入负数据  case_no_seq_new1,bill_date-->sysdate,金额为负
				//修改处理状态
				String sql="UPDATE IBS_ORDD SET INCLUDE_EXEC_FLG='Y' WHERE CASE_NO='"
					+parmIbsOrdd.getData("CASE_NO", m)+"' AND CASE_NO_SEQ='"
					+parmIbsOrdd.getData("CASE_NO_SEQ", m)+"' AND SEQ_NO='"
					+parmIbsOrdd.getData("SEQ_NO", m)+"'";
				result=new TParm(TJDODBTool.getInstance().update(sql,connection));
				if (result.getErrCode()<0) {
					return result;
				}
				insertIbsOrddNegativeParm = new TParm();		
				insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", m));					
				//insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",parmIbsOrdd.getData("CASE_NO_SEQ", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO_SEQ", m));						
				insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
				insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
				getIbsOrddParm(insertIbsOrddNegativeParm, parm, parmIbsOrdd, m, sysDate,-1);
				insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",groupNo+1);
				insertIbsOrddNegativeParm.setData("OWN_RATE",parmIbsOrdd.getDouble("OWN_RATE",m));
				insertIbsOrddNegativeParm.setData("TOT_AMT",-parmIbsOrdd.getDouble("TOT_AMT",m));
				insertIbsOrddNegativeParm.setData("INCLUDE_FLG",parmIbsOrdd.getData("INCLUDE_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INCLUDE_FLG", m));
				++seqNo;
				result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
		        if (result.getErrCode() < 0) {
	                err(result.getErrName() + " " + result.getErrText());
	                return result;
		        }
			}	
			//添加正数据
			//操作集合医嘱 重新计算费用 1. 套餐内转成套餐外，查询SYS_FEE表集合医嘱细项以及费用，根据SYS_FEE表数据添加IBS_ORDD表
	        //2.套餐外转成套餐内，查询MEM_PAT_PACKAGE_SECTION_D表此集合医嘱细项以及费用，
	        //根据MEM_PAT_PACKAGE_SECTION_D表数据添加IBS_ORDD表，
	        //如果MEM_PAT_PACKAGE_SECTION_D 表不存在此集合医嘱数据,则根据SYS_FEE表数据添加IBS_ORDD表
	        TParm parmIbsOrddNew=null;
	        if (parm.getValue("ORDER_CODE").equals(parm.getValue("ORDERSET_CODE"))
					&&parm.getValue("INDV_FLG").equals("N")) {
	        	if (flg) {//包干内
	        		//重新获得折扣后的金额
		        	String querySql = "SELECT A.ORDER_CODE,B.ORDER_CAT1_CODE,B.CAT1_TYPE,A.ORDERSET_CODE,B.CHARGE_HOSP_CODE HEXP_CODE," +
		        			"A.ORDER_NUM MEDI_QTY,A.UNIT_CODE AS MEDI_UNIT,'' DOSE_CODE,A.ORDER_NUM DOSAGE_QTY,A.UNIT_CODE DOSAGE_UNIT," +
		        			"C.IPD_CHARGE_CODE REXP_CODE,A.ORDER_DESC ORDER_CHN_DESC,A.UNIT_PRICE AS OWN_PRICE " +
		        			"FROM MEM_PAT_PACKAGE_SECTION_D A ,SYS_FEE B,SYS_CHARGE_HOSP C " +
		        			"WHERE A.ORDER_CODE=B.ORDER_CODE AND B.CHARGE_HOSP_CODE=C.CHARGE_HOSP_CODE  AND A.CASE_NO='" +caseNoNew
					+ "' AND A.PACKAGE_CODE = '"+lumpworkCode+"' AND A.ORDERSET_CODE='"+parm.getValue("ORDER_CODE")+"'";
		        	parmIbsOrddNew = new TParm(TJDODBTool.getInstance().select(querySql));
		        	if(parmIbsOrddNew.getCount()>0){//套内集合医嘱
		        		result=onExeLumpWorkCharge(parmIbsOrddNew, parm, seqNo, 
		        				caseNo, groupNo, sysDate, parmIbsOrdd, maxCaseNoSeq, 
		        				CTZ1, CTZ2, CTZ3, bil, lumpworkRate, connection,false,level);
		        	}else{
		        		result=lumpWorkChangeComm(parm, seqNo, caseNo, groupNo, sysDate, 
		        				parmIbsOrdd, maxCaseNoSeq, CTZ1, CTZ2, CTZ3, bil, lumpworkRate, connection,false,level);
		        	}
		        	if (result.getErrCode() < 0) {
	    	            err(result.getErrName() + " " + result.getErrText());
	    	            return result;
	    	        }
	        		
				} else {
					result = lumpWorkChangeComm(parm, seqNo, caseNo, groupNo,
							sysDate, parmIbsOrdd, maxCaseNoSeq, CTZ1, CTZ2,
							CTZ3, bil, lumpworkRate, connection,true,level);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						return result;
					}
				}
	        }else{
	        	for(int m = 0;m<parmIbsOrdd.getCount();m++){
	        		 //重新获得折扣后的金额
	    	        insertIbsOrddNegativeParm = new TParm();		
	    			insertIbsOrddNegativeParm.setData("CASE_NO",caseNo);					
	    			insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);							
	    			insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
	    			getIbsOrddParm(insertIbsOrddNegativeParm, parm, parmIbsOrdd, m, sysDate,1);
	    			insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",groupNo+2);
	    			double ownRate =0.00;
	    			double ownPrice=0.00;
	    			double [] sumPrice = new double[2];
	    			if (flg) {//包干内
	    				//药品、血费根据身份统计
	    				if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m)
	    						&&parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m).equals("PHA")||
	    						null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m)&&parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m).equals("RA")){
	    					ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", m) ,
		    						parmIbsOrdd.getValue("ORDER_CODE", m));//包干外根据身份计算
	    					sumPrice=IBSTool.getInstance().getOrderOwnPrice(parmIbsOrdd.getValue("ORDER_CODE", m), level);
	    					ownPrice=sumPrice[0];
	    				}else{
	    					ownRate =lumpworkRate;//包干内根据住院登记操作时计算的折扣统计
	    					ownPrice=IBSTool.getInstance().getLumpOrderOwnPrice(caseNoNew, lumpworkCode, 
	    							parmIbsOrdd.getValue("ORDER_CODE", m), level);
	    				}
//	    				double ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", m) ,
//	    						parmIbsOrdd.getValue("ORDER_CODE", m));包干内 根据第一身份和第二身份统计
	    				insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
	    				insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
	    			}else{
	    				sumPrice=IBSTool.getInstance().getOrderOwnPrice(parmIbsOrdd.getValue("ORDER_CODE", m), level);
    					ownPrice=sumPrice[0];
	    				ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", m) ,
	    						parmIbsOrdd.getValue("ORDER_CODE", m));//包干外根据身份计算
	    				insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
	    				insertIbsOrddNegativeParm.setData("INCLUDE_FLG","Y");
	    			}
	    			insertIbsOrddNegativeParm.setData("OWN_PRICE",ownPrice);
	    			parmIbsOrdd.setData("OWN_PRICE",m,ownPrice);//包干内的医嘱单价根据套餐交易表中获取
	    			parmIbsOrdd.setData("OWN_AMT",m,ownPrice*
	    					parmIbsOrdd.getDouble("DOSAGE_QTY",m));
	    			insertIbsOrddNegativeParm.setData("TOT_AMT",parmIbsOrdd.getDouble("OWN_AMT",m)*ownRate);
	    			result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
	    			if (result.getErrCode() < 0) {
	    	            err(result.getErrName() + " " + result.getErrText());
	    	            return result;
	    	        }
	    			++seqNo;
				}
	        }
		}
		result.setData("CASE_NO_SEQ",maxCaseNoSeq+1);
		return result;
	}
	/**
	 * 住院套餐操作转换套内套外公用方法
	 * @param parm
	 * @param seqNo
	 * @param caseNo
	 * @param groupNo
	 * @param sysDate
	 * @param parmIbsOrdd
	 * @param maxCaseNoSeq
	 * @param CTZ1
	 * @param CTZ2
	 * @param CTZ3
	 * @param bil
	 * @param lumpworkRate
	 * @param connection
	 * @return
	 */
	private TParm lumpWorkChangeComm(TParm parm,int seqNo,String caseNo,
		int groupNo,Timestamp sysDate,TParm parmIbsOrdd,int maxCaseNoSeq,String CTZ1,
		String CTZ2,String CTZ3,BIL bil,double lumpworkRate,TConnection connection,boolean flg,String level){
		//TParm insertIbsOrddNegativeParm=null;
		//套餐内不存在此集合医嘱,查询SYS_FEE转成套内
		String querySql = "SELECT A.ORDER_CODE,A.ORDER_CAT1_CODE,A.CAT1_TYPE,A.ORDER_CODE ORDERSET_CODE,A.CHARGE_HOSP_CODE HEXP_CODE,"
						+"1 MEDI_QTY,A.UNIT_CODE AS MEDI_UNIT,'' DOSE_CODE, 1 DOSAGE_QTY,A.UNIT_CODE DOSAGE_UNIT,"
						+"C.IPD_CHARGE_CODE REXP_CODE,A.ORDER_DESC ORDER_CHN_DESC,A.OWN_PRICE, A.OWN_PRICE2, A.OWN_PRICE3 FROM SYS_FEE A,SYS_CHARGE_HOSP C  "
						+"WHERE A.CHARGE_HOSP_CODE=C.CHARGE_HOSP_CODE AND A.ORDER_CODE='"+ parm.getValue("ORDER_CODE")+"' " 
						+"UNION ALL  SELECT A.ORDER_CODE,A.ORDER_CAT1_CODE,A.CAT1_TYPE,B.ORDERSET_CODE,A.CHARGE_HOSP_CODE HEXP_CODE," 
						+"B.DOSAGE_QTY MEDI_QTY,A.UNIT_CODE AS MEDI_UNIT,'' DOSE_CODE,B.DOSAGE_QTY,A.UNIT_CODE DOSAGE_UNIT," 
						+"C.IPD_CHARGE_CODE REXP_CODE,A.ORDER_DESC ORDER_CHN_DESC,A.OWN_PRICE,A.OWN_PRICE2,A.OWN_PRICE3 FROM SYS_FEE A,SYS_ORDERSETDETAIL B,SYS_CHARGE_HOSP C " 
						+" WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CHARGE_HOSP_CODE=C.CHARGE_HOSP_CODE AND B.ORDERSET_CODE='"
						+ parm.getValue("ORDER_CODE")+"'";
		TParm parmIbsOrddNew = new TParm(TJDODBTool.getInstance().select(querySql));
		if(parmIbsOrddNew.getErrCode()<0){
			return parmIbsOrddNew;
		}
		TParm result=onExeLumpWorkCharge(parmIbsOrddNew, parm, seqNo, 
				caseNo, groupNo, sysDate, parmIbsOrdd, maxCaseNoSeq, CTZ1, CTZ2, CTZ3, 
				bil, lumpworkRate, connection,flg,level);
		return result;
	}
	/**
	 * 集合医嘱操作套餐
	 * @param insertIbsOrddNegativeParm
	 * @param parm
	 * @param parmIbsOrdd
	 * @param m
	 * @param sysDate
	 * @param parmIbsOrddOld
	 */
	private void getIbsOrddParm(TParm insertIbsOrddNegativeParm,TParm parm,
			TParm parmIbsOrdd,int m,Timestamp sysDate,TParm parmIbsOrddOld){
		insertIbsOrddNegativeParm.setData("BILL_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("EXEC_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("ORDER_NO",parmIbsOrddOld.getValue("ORDER_NO",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("ORDER_NO", 0));
		insertIbsOrddNegativeParm.setData("ORDER_SEQ","");
		insertIbsOrddNegativeParm.setData("ORDER_CODE",parmIbsOrdd.getData("ORDER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CODE", m));
		insertIbsOrddNegativeParm.setData("ORDER_CAT1_CODE",parmIbsOrdd.getData("ORDER_CAT1_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CAT1_CODE", m));
		insertIbsOrddNegativeParm.setData("CAT1_TYPE",parmIbsOrdd.getData("CAT1_TYPE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CAT1_TYPE", m));
		insertIbsOrddNegativeParm.setData("ORDERSET_CODE",parmIbsOrdd.getData("ORDERSET_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_CODE", m));
		if (parmIbsOrdd.getValue("ORDER_CODE",m).equals(parmIbsOrdd.getValue("ORDERSET_CODE",m))) {
			insertIbsOrddNegativeParm.setData("INDV_FLG","N");
		}else{
			insertIbsOrddNegativeParm.setData("INDV_FLG","Y");
		}
		insertIbsOrddNegativeParm.setData("DEPT_CODE",parmIbsOrddOld.getValue("DEPT_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("DEPT_CODE", 0));
		insertIbsOrddNegativeParm.setData("STATION_CODE",parmIbsOrddOld.getValue("STATION_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("STATION_CODE", 0));
		insertIbsOrddNegativeParm.setData("DR_CODE",parmIbsOrddOld.getValue("DR_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("DR_CODE", 0));
		insertIbsOrddNegativeParm.setData("EXE_DEPT_CODE",parmIbsOrddOld.getData("EXE_DEPT_CODE", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("EXE_DEPT_CODE",0));
		insertIbsOrddNegativeParm.setData("EXE_STATION_CODE",parmIbsOrddOld.getData("EXE_STATION_CODE", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("EXE_STATION_CODE",0));
		insertIbsOrddNegativeParm.setData("EXE_DR_CODE",parmIbsOrddOld.getData("EXE_DR_CODE", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("EXE_DR_CODE", 0));
		insertIbsOrddNegativeParm.setData("MEDI_QTY",parmIbsOrdd.getDouble("MEDI_QTY",m)*parm.getDouble("DOSAGE_QTY"));
		insertIbsOrddNegativeParm.setData("MEDI_UNIT",parmIbsOrdd.getData("MEDI_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("MEDI_UNIT", m));
		insertIbsOrddNegativeParm.setData("DOSE_CODE",parmIbsOrdd.getData("DOSE_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSE_CODE", m));
		insertIbsOrddNegativeParm.setData("FREQ_CODE",parmIbsOrddOld.getData("FREQ_CODE", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("FREQ_CODE", 0));
		insertIbsOrddNegativeParm.setData("TAKE_DAYS","1");
		insertIbsOrddNegativeParm.setData("DOSAGE_QTY",parmIbsOrdd.getDouble("DOSAGE_QTY",m)*parm.getDouble("DOSAGE_QTY"));
		insertIbsOrddNegativeParm.setData("DOSAGE_UNIT",parmIbsOrdd.getData("DOSAGE_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSAGE_UNIT", m));
		insertIbsOrddNegativeParm.setData("NHI_PRICE",0);
		insertIbsOrddNegativeParm.setData("OWN_FLG","Y");
		insertIbsOrddNegativeParm.setData("BILL_FLG","Y");
		insertIbsOrddNegativeParm.setData("REXP_CODE",parmIbsOrdd.getData("REXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REXP_CODE", m));
		insertIbsOrddNegativeParm.setData("BILL_NO","");
		insertIbsOrddNegativeParm.setData("HEXP_CODE",parmIbsOrdd.getData("HEXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("HEXP_CODE", m));
		insertIbsOrddNegativeParm.setData("BEGIN_DATE",parmIbsOrddOld.getData("BEGIN_DATE", 0) == null ? new TNull(Timestamp.class) : parmIbsOrddOld.getData("BEGIN_DATE", 0));
		insertIbsOrddNegativeParm.setData("END_DATE",parmIbsOrddOld.getData("END_DATE", 0) == null ? new TNull(Timestamp.class) : parmIbsOrddOld.getData("END_DATE", 0));
		insertIbsOrddNegativeParm.setData("OWN_AMT",parmIbsOrdd.getDouble("OWN_AMT",m));
		insertIbsOrddNegativeParm.setData("REQUEST_FLG",parmIbsOrddOld.getData("REQUEST_FLG", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("REQUEST_FLG", 0));
		insertIbsOrddNegativeParm.setData("REQUEST_NO",parmIbsOrddOld.getData("REQUEST_NO", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("REQUEST_NO", 0));
		insertIbsOrddNegativeParm.setData("INV_CODE","");
		insertIbsOrddNegativeParm.setData("OPT_USER",parm.getValue("OPT_USER"));
		insertIbsOrddNegativeParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
		insertIbsOrddNegativeParm.setData("COST_AMT",0);
		insertIbsOrddNegativeParm.setData("ORDER_CHN_DESC",parmIbsOrdd.getData("ORDER_CHN_DESC", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CHN_DESC", m));
		insertIbsOrddNegativeParm.setData("COST_CENTER_CODE",parmIbsOrddOld.getData("COST_CENTER_CODE", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("COST_CENTER_CODE", 0));
		insertIbsOrddNegativeParm.setData("SCHD_CODE",parmIbsOrddOld.getData("SCHD_CODE", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("SCHD_CODE", 0));
		insertIbsOrddNegativeParm.setData("CLNCPATH_CODE",parmIbsOrddOld.getData("CLNCPATH_CODE", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("CLNCPATH_CODE", 0));
		insertIbsOrddNegativeParm.setData("DS_FLG","N");
		insertIbsOrddNegativeParm.setData("KN_FLG",parmIbsOrddOld.getData("KN_FLG", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("KN_FLG", 0));
		insertIbsOrddNegativeParm.setData("INCLUDE_EXEC_FLG","Y");

	}
	/**
	 * 
	* @Title: getIbsOrddParm
	* @Description: TODO(获得IBS_ORDD表数据)
	* @author pangben
	* @param insertIbsOrddNegativeParm
	* @param parm
	* @param parmIbsOrdd
	* @param m
	* @param sysDate
	* @param mas
	* @throws
	 */
	private void getIbsOrddParm(TParm insertIbsOrddNegativeParm,TParm parm,
			TParm parmIbsOrdd,int m,Timestamp sysDate,int mas){
		insertIbsOrddNegativeParm.setData("BILL_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("EXEC_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("ORDER_NO",parmIbsOrdd.getData("ORDER_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_NO", m));
		insertIbsOrddNegativeParm.setData("ORDER_SEQ",parmIbsOrdd.getData("ORDER_SEQ", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_SEQ", m));
		insertIbsOrddNegativeParm.setData("ORDER_CODE",parmIbsOrdd.getData("ORDER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CODE", m));
		insertIbsOrddNegativeParm.setData("ORDER_CAT1_CODE",parmIbsOrdd.getData("ORDER_CAT1_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CAT1_CODE", m));
		insertIbsOrddNegativeParm.setData("CAT1_TYPE",parmIbsOrdd.getData("CAT1_TYPE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CAT1_TYPE", m));
		insertIbsOrddNegativeParm.setData("ORDERSET_CODE",parmIbsOrdd.getData("ORDERSET_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_CODE", m));
		insertIbsOrddNegativeParm.setData("INDV_FLG",parmIbsOrdd.getData("INDV_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INDV_FLG", m));
		insertIbsOrddNegativeParm.setData("DEPT_CODE",parmIbsOrdd.getData("DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DEPT_CODE", m));
		insertIbsOrddNegativeParm.setData("STATION_CODE",parmIbsOrdd.getData("STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("STATION_CODE", m));
		insertIbsOrddNegativeParm.setData("DR_CODE",parmIbsOrdd.getData("DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DR_CODE", m));
		insertIbsOrddNegativeParm.setData("EXE_DEPT_CODE",parmIbsOrdd.getData("EXE_DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DEPT_CODE", m));
		insertIbsOrddNegativeParm.setData("EXE_STATION_CODE",parmIbsOrdd.getData("EXE_STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_STATION_CODE", m));
		insertIbsOrddNegativeParm.setData("EXE_DR_CODE",parmIbsOrdd.getData("EXE_DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DR_CODE", m));
		insertIbsOrddNegativeParm.setData("MEDI_QTY",parmIbsOrdd.getDouble("MEDI_QTY",m)*mas);
		insertIbsOrddNegativeParm.setData("MEDI_UNIT",parmIbsOrdd.getData("MEDI_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("MEDI_UNIT", m));
		insertIbsOrddNegativeParm.setData("DOSE_CODE",parmIbsOrdd.getData("DOSE_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSE_CODE", m));
		insertIbsOrddNegativeParm.setData("FREQ_CODE",parmIbsOrdd.getData("FREQ_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("FREQ_CODE", m));
		insertIbsOrddNegativeParm.setData("TAKE_DAYS",parmIbsOrdd.getData("TAKE_DAYS", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("TAKE_DAYS", m));
		insertIbsOrddNegativeParm.setData("DOSAGE_QTY",parmIbsOrdd.getDouble("DOSAGE_QTY",m)*mas);
		insertIbsOrddNegativeParm.setData("DOSAGE_UNIT",parmIbsOrdd.getData("DOSAGE_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSAGE_UNIT", m));
		insertIbsOrddNegativeParm.setData("OWN_PRICE",parmIbsOrdd.getDouble("OWN_PRICE",m));
		insertIbsOrddNegativeParm.setData("NHI_PRICE",parmIbsOrdd.getDouble("NHI_PRICE",m));
		insertIbsOrddNegativeParm.setData("OWN_FLG",parmIbsOrdd.getData("OWN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("OWN_FLG", m));
		insertIbsOrddNegativeParm.setData("BILL_FLG",parmIbsOrdd.getData("BILL_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("BILL_FLG", m));
		insertIbsOrddNegativeParm.setData("REXP_CODE",parmIbsOrdd.getData("REXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REXP_CODE", m));
		insertIbsOrddNegativeParm.setData("BILL_NO","");
		insertIbsOrddNegativeParm.setData("HEXP_CODE",parmIbsOrdd.getData("HEXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("HEXP_CODE", m));
		insertIbsOrddNegativeParm.setData("BEGIN_DATE",parmIbsOrdd.getData("BEGIN_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("BEGIN_DATE", m));
		insertIbsOrddNegativeParm.setData("END_DATE",parmIbsOrdd.getData("END_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("END_DATE", m));
		insertIbsOrddNegativeParm.setData("OWN_AMT",parmIbsOrdd.getDouble("OWN_AMT",m)*mas);
		insertIbsOrddNegativeParm.setData("REQUEST_FLG",parmIbsOrdd.getData("REQUEST_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_FLG", m));
		insertIbsOrddNegativeParm.setData("REQUEST_NO",parmIbsOrdd.getData("REQUEST_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_NO", m));
		insertIbsOrddNegativeParm.setData("INV_CODE",parmIbsOrdd.getData("INV_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INV_CODE", m));
		insertIbsOrddNegativeParm.setData("OPT_USER",parm.getValue("OPT_USER"));
		insertIbsOrddNegativeParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
		insertIbsOrddNegativeParm.setData("COST_AMT",parmIbsOrdd.getDouble("COST_AMT",m)*mas);
		insertIbsOrddNegativeParm.setData("ORDER_CHN_DESC",parmIbsOrdd.getData("ORDER_CHN_DESC", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CHN_DESC", m));
		insertIbsOrddNegativeParm.setData("COST_CENTER_CODE",parmIbsOrdd.getData("COST_CENTER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("COST_CENTER_CODE", m));
		insertIbsOrddNegativeParm.setData("SCHD_CODE",parmIbsOrdd.getData("SCHD_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("SCHD_CODE", m));
		insertIbsOrddNegativeParm.setData("CLNCPATH_CODE",parmIbsOrdd.getData("CLNCPATH_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CLNCPATH_CODE", m));
		insertIbsOrddNegativeParm.setData("DS_FLG",parmIbsOrdd.getData("DS_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DS_FLG", m));
		insertIbsOrddNegativeParm.setData("KN_FLG",parmIbsOrdd.getData("KN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("KN_FLG", m));
		insertIbsOrddNegativeParm.setData("INCLUDE_EXEC_FLG","Y");
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
	 * 查询最大账务序号
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 * caowl
	 */
	public int selMaxOrderSetGroupNo(String caseNo,String orderCode) {
		String sql = " SELECT MAX(TO_NUMBER(ORDERSET_GROUP_NO)) AS ORDERSET_GROUP_NO FROM IBS_ORDD WHERE CASE_NO = '"
				+ caseNo + "' AND ORDERSET_CODE IS NOT NULL";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount()>0) {
			return result.getInt("ORDERSET_GROUP_NO",0);
		}
		return -1;
	}
	/**
	 * 住院套餐费用整理，重新计算费用
	 * 调用修改身份操作
	 * @return
	 */
	public TParm onExeLumpWorkFeeCount(TParm parm){
		String caseNo=parm.getValue("CASE_NO");
		TConnection connection = getConnection();
		String sql = "UPDATE ADM_INP SET LUMPWORK_RATE="+parm.getDouble("LUMPWORK_RATE")+" WHERE CASE_NO='" + caseNo
			+ "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			result.setErr(-1,"E0005");
			return result;
		}
		sql = "SELECT CASE_NO,MR_NO,NEW_BORN_FLG FROM ADM_INP WHERE M_CASE_NO ='"
			+ caseNo + "' AND NEW_BORN_FLG='Y'";
		TParm admDateParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (admDateParm.getCount() > 0) {//存在婴儿数据
			for (int i = 0; i < admDateParm.getCount(); i++) {
				sql = "UPDATE ADM_INP SET LUMPWORK_RATE="+parm.getDouble("LUMPWORK_RATE")+" WHERE CASE_NO='"
						+ admDateParm.getValue("CASE_NO", i) + "'";
				result = new TParm(TJDODBTool.getInstance().update(sql,
						connection));
				if (result.getErrCode() < 0) {
					result.setErr(-1,"E0005");
					connection.rollback();
					connection.close();
					return result;
				}
			}
		}
		connection.commit();
		sql="SELECT CASE_NO,LUMPWORK_RATE,LUMPWORK_CODE,SERVICE_LEVEL FROM ADM_INP WHERE CASE_NO='"+caseNo+"'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		parm.setData("LUMPWORK_CODE",selParm.getValue("LUMPWORK_CODE",0));
		parm.setData("SERVICE_LEVEL",selParm.getValue("SERVICE_LEVEL",0));
		result = ADMInpTool.getInstance().updlumpWorkBill(parm, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * 
	* @Title: onExeIbsLumpWorkBatch
	* @Description: TODO(套餐批次区分套内套外)
	* @author pangben 2015-7-15
	* @return
	* @throws
	 */
	public TParm onExeIbsLumpWorkBatch(TParm parm){
		TConnection connection = getConnection();
		TParm admParm=ADMInpTool.getInstance().onQueryLumpWorkBatch(parm);
		if (admParm.getCount()<=0) {
			TParm result=new TParm();
			result.setData("MESSAGE","没有需要操作的数据");
			return result;
		}
		TParm result=new TParm();
		//母亲婴儿跑套餐批次操作
		for (int i = 0; i < admParm.getCount(); i++) {
			result=IBSLumpWorkBatchNewTool.getInstance().onLumpWorkBatch(admParm.getRow(i), connection);
	    	if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
	    	connection.commit();
	    	result = IBSTool.getInstance().updateAdmTotAmt(admParm.getRow(i));
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		}
		connection.close();
    	return result;
	}
}
