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
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
/**
*
* <p>Title: 套餐夜间批次控制类</p>
*
* <p>Description:套餐夜间批次控制类 区分套内套外</p>
*
* <p>Copyright: Copyright (c) Liu dongyang 2015</p>
*
* <p>Company: bluecore</p>
*
* @author pangben
* @version 1.0
* 
*/
public class IBSLumpWorkBatchNewTool extends TJDOTool{
	/**
     * 实例
     */
    public static IBSLumpWorkBatchNewTool instanceObject;
    /**
     * 得到实例
     * @return IBSTool
     */
    public static IBSLumpWorkBatchNewTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IBSLumpWorkBatchNewTool();
        return instanceObject;
    }
    public TParm onLumpWorkBatch(TParm admParm,TConnection connection){
    	String statCode=OdiMainTool.getInstance().getOdiSysParmData("ODI_STAT_CODE").toString();//临时医嘱频次
    	TParm result=new TParm();
    	if (admParm.getErrCode()<0) {
    		System.out.println("查询病患信息出现问题:"+admParm.getErrText());
    		result.setErr(-1,"查询病患信息出现问题:"+admParm.getErrText());
    		//this.setMessage();
			return result;
		}
    	if (admParm.getValue("CASE_NO").length()<=0) {
    		result.setData("MESSAGE","没有需要操作的数据");
    		//result.setErr(-1,"没有需要操作的数据");
    		//this.setMessage("没有需要操作的数据");
    		return result;
		}
    	if(null==admParm.getValue("LUMPWORK_RATE")||
    			admParm.getValue("LUMPWORK_RATE").length()<=0||admParm.getDouble("LUMPWORK_RATE")==0.00){
    		result.setErr(-1,"病案号:"+admParm.getValue("MR_NO")+"未设置套餐折扣");
    		return result;
    	}
    	TParm odiParm = new TParm(TJDODBTool.getInstance().select("SELECT LUMPWORK_ORDER_CODE FROM ODI_SYSPARM"));
    	if (odiParm.getValue("LUMPWORK_ORDER_CODE",0).length()<=0) {
    		result.setErr(-1,"套餐差异医嘱字典设置错误");
    		return result;
		}
    	//TConnection connection= TDBPoolManager.getInstance().getConnection();
    	String caseNo="";
    	//String mrNo="";
    	String packCode="";
    	TParm noExeBillParm=null;//未处理医嘱
    	TParm memPatPackAgeSectionParm=null;
    	double diffQty=0.00;
    	TParm numberParm=null;
    	String mCaseNo="";
    	String sumCaseNo="";
    	String caseNoNew="";//套餐病患根据母亲就诊号查询套餐中数据
		caseNo=admParm.getValue("CASE_NO");
		caseNoNew=admParm.getValue("CASE_NO");
		if (admParm.getValue("NEW_BORN_FLG").equals("Y")) {//操作当前操作病患是新生儿
			mCaseNo=admParm.getValue("M_CASE_NO");
			caseNoNew=admParm.getValue("M_CASE_NO");
			String admSql="SELECT CASE_NO FROM ADM_INP WHERE M_CASE_NO='"+mCaseNo+"' AND DS_DATE IS NULL AND CANCEL_FLG<>'Y'";
			TParm caseParm=new TParm(TJDODBTool.getInstance().select(admSql));
			if (caseParm.getCount()>0) {
				for (int j = 0; j < caseParm.getCount(); j++) {
					sumCaseNo+="'"+caseParm.getValue("CASE_NO",j)+"',";
				}
				sumCaseNo+="'"+mCaseNo+"'";
			}
		}else{
			mCaseNo="";
			String admSql="SELECT CASE_NO FROM ADM_INP WHERE M_CASE_NO='"+caseNo+"' AND DS_DATE IS NULL AND CANCEL_FLG<>'Y'";
			TParm caseParm=new TParm(TJDODBTool.getInstance().select(admSql));
			if (caseParm.getCount()>0) {
				for (int j = 0; j < caseParm.getCount(); j++) {
					sumCaseNo+="'"+caseParm.getValue("CASE_NO",j)+"',";
				}
				sumCaseNo+="'"+caseNo+"'";
			}
		}
		
		//mrNo=admParm.getValue("MR_NO");
		packCode=admParm.getValue("LUMPWORK_CODE");//套餐代码
		noExeBillParm=getNoIncludeExecFlgBill(caseNo);//根据就诊号查询未处理的计费医嘱数据
		if (noExeBillParm.getErrCode()<0) {
			System.out.println("查询未处理医嘱信息出现问题:"+noExeBillParm.getErrText());
			result.setErr(-1,"查询未处理医嘱信息出现问题:"+noExeBillParm.getErrText());
			return result;
		}
		int seqNo = 1;
		numberParm=new TParm();
		String groupSql = " SELECT MAX(TO_NUMBER(ORDERSET_GROUP_NO)) AS ORDERSET_GROUP_NO FROM IBS_ORDD"
			+ "  WHERE CASE_NO = '" + caseNo + "'";
		TParm maxGroupNoParm = new TParm(TJDODBTool.getInstance().select(groupSql));
		if (maxGroupNoParm.getErrCode() < 0) {
			return maxGroupNoParm;
		}
		TParm maxCaseNoSeqParm = selMaxCaseNoSeq(caseNo);
		numberParm.setData("CASE_NO_SEQ",maxCaseNoSeqParm.getInt("CASE_NO_SEQ",0)+1);
		if (maxGroupNoParm.getCount()<=0||null==maxGroupNoParm.getValue("ORDERSET_GROUP_NO",0)||
				maxGroupNoParm.getValue("ORDERSET_GROUP_NO",0).length()<=0) {
			numberParm.setData("GROUP_NO",1);
		}else{
			numberParm.setData("GROUP_NO",maxGroupNoParm.getInt("ORDERSET_GROUP_NO",0)+1);
		}
		for (int j = 0; j < noExeBillParm.getCount("ORDER_CODE"); j++) {
			if (noExeBillParm.getValue("ORDER_CODE",j).equals(odiParm.getValue("LUMPWORK_ORDER_CODE",0))) {
				//已经办理过出院然后操作修改身份界面套餐批次差异金额医嘱不操作
				continue;
			}
			if (noExeBillParm.getDouble("DOSAGE_QTY",j)==0) {//等于0不操作
				continue;
			}
//			if(noExeBillParm.getValue("CAT1_TYPE",j).equals("PHA")){//药品费不进行批次
//				continue;		
//			}
//			if(noExeBillParm.getValue("CHARGE_HOSP_CODE",j).equals("RA")){//血费不进行批次
//				continue;		
//			}
    		numberParm.setData("SEQ_NO",seqNo);
//			if (noExeBillParm.getDouble("DOSAGE_QTY",j)<=0) {
//				continue;
//			}
			if (mCaseNo.length()>0) {
				memPatPackAgeSectionParm=getMemPatPackAgeSection(mCaseNo, packCode, noExeBillParm.getValue("ORDER_CODE",j));
			}else{
				memPatPackAgeSectionParm=getMemPatPackAgeSection(caseNo, packCode, noExeBillParm.getValue("ORDER_CODE",j));
			}
			if (memPatPackAgeSectionParm.getErrCode()<0) {
    			System.out.println("查询套餐明细医嘱信息出现问题:"+memPatPackAgeSectionParm.getErrText());
    			result.setErr(-1,"查询套餐明细医嘱信息出现问题:"+memPatPackAgeSectionParm.getErrText());
    			return result;
			}
			TParm parmIbsOrdd=getIbsOrddOrderParm(caseNo, noExeBillParm.getValue("ORDER_CODE",j));
			if (memPatPackAgeSectionParm.getCount()<=0) {//套餐明细中不存在，此医嘱为套餐外
				result=onSaveExeIbsOrdd(parmIbsOrdd,caseNo, noExeBillParm.getDouble("DOSAGE_QTY",j), noExeBillParm.getValue("ORDER_CODE",j),
						j, admParm, numberParm, connection,statCode,caseNoNew);
				if (result.getErrCode()<0) {
					System.out.println("套餐批次添加交易表出现问题:"+result.getErrText());
					return result;
				}
			}else{
				if (memPatPackAgeSectionParm.getValue("UN_NUM_FLG",0).equals("Y")) {//pangben 2015-9-2不限量操作
					continue;
				}
				double orderSum=memPatPackAgeSectionParm.getDouble("ORDER_NUM",0);//套餐医嘱明细中汇总数量
				double execDosageQty=0.00;
				double checkDosageQty=0.00;//校验当前操作病人不包括新生儿信息，交易表中是否存在套外医嘱
				if (noExeBillParm.getDouble("DOSAGE_QTY",j)<0) {//数量小于0操作
					if (sumCaseNo.length()>0) {
						execDosageQty=getOnExeUnIncludeExecFlgBill(sumCaseNo,  noExeBillParm.getValue("ORDER_CODE",j));//已经处理的医嘱数量
					}else{
						execDosageQty=getOnExeUnIncludeExecFlgBill("'"+caseNo+"'",  noExeBillParm.getValue("ORDER_CODE",j));//已经处理的医嘱数量
					}
					if (execDosageQty!=0) {//套外没有计费的医嘱，说明此退费医嘱为套内医嘱，直接更新状态
						checkDosageQty=getOnExeUnIncludeExecFlgBill("'"+caseNo+"'",  noExeBillParm.getValue("ORDER_CODE",j));//已经处理的医嘱数量
						if(checkDosageQty==0){
							continue;
						}
					}
		    		//=====药房退药、护士取消执行负的数量操作，不包括集合医嘱取消报到，取消报到从住院计价界面操作
					result=getExeUnBill(orderSum, execDosageQty, diffQty, noExeBillParm, j, 
							parmIbsOrdd, caseNo, admParm, numberParm, connection, statCode,caseNoNew);
				}else if(noExeBillParm.getDouble("DOSAGE_QTY",j)>0){//数量大于于0操作
					if (sumCaseNo.length()>0) {
						execDosageQty=getOnExeIncludeExecFlgBill(sumCaseNo,  noExeBillParm.getValue("ORDER_CODE",j));//已经处理的医嘱数量
					}else{
						execDosageQty=getOnExeIncludeExecFlgBill("'"+caseNo+"'",  noExeBillParm.getValue("ORDER_CODE",j));//已经处理的医嘱数量
					}
					result=getExeBill(orderSum, execDosageQty, diffQty, noExeBillParm, j,
							parmIbsOrdd, caseNo, admParm, numberParm, connection, statCode,caseNoNew);
				}
				if (null==result) {
					continue;
				}
				if (result.getErrCode()<0) {
					System.out.println("套餐批次添加交易表出现问题:"+result.getErrText());
					return result;
				}
			}
			result=onSaveIbsOrdm(parmIbsOrdd.getRow(0), numberParm, caseNo, connection);
			if (result.getErrCode()<0) {
				System.out.println("套餐批次添加交易表出现问题:"+result.getErrText());
				return result;
			}
			numberParm.setData("CASE_NO_SEQ",numberParm.getInt("CASE_NO_SEQ")+1);//主键
		}
		//更新IBS_ORDD 表
		String sql="UPDATE IBS_ORDD SET INCLUDE_EXEC_FLG='Y' WHERE CASE_NO='"+caseNo+"'";
		result=new TParm(TJDODBTool.getInstance().update(sql,connection));
		if (result.getErrCode()<0) {
			System.out.println("套餐批次添加交易表出现问题:"+result.getErrText());
			return result;
		}
    	return result;
    }
    /**
     * 执行状态修改  BILL_EXE_FLG
     * @param caseNo
     * @param ordersetFlg
     * @param orderCode
     * @param connection
     * @return
     */
    private TParm getExeBillExeFlg(String caseNo,boolean ordersetFlg,String orderCode,TConnection connection){
    	String sql="UPDATE IBS_ORDD SET BILL_EXE_FLG='Y' WHERE " +
    			"CASE_NO='"+caseNo+"' AND (INCLUDE_EXEC_FLG='N' OR INCLUDE_EXEC_FLG IS NULL) AND" +
    					" (ORDERSET_CODE IS NULL OR ORDERSET_CODE=ORDER_CODE) AND ORDER_CODE='"+orderCode+"'";
    	TParm result=new TParm(TJDODBTool.getInstance().update(sql,connection));
    	if (result.getErrCode()<0) {
			System.out.println("套餐批次修改执行状态出现问题:"+result.getErrText());
			return result;
		}
    	if(ordersetFlg){//集合医嘱
    		sql="UPDATE IBS_ORDD SET BILL_EXE_FLG='Y' WHERE " +
        			"CASE_NO='"+caseNo+"' AND (INCLUDE_EXEC_FLG='N' OR INCLUDE_EXEC_FLG IS NULL) AND" +
        					" ORDERSET_CODE='"+orderCode+"'";
        	result=new TParm(TJDODBTool.getInstance().update(sql,connection));
        	if (result.getErrCode()<0) {
    			System.out.println("套餐批次修改执行状态出现问题:"+result.getErrText());
    			return result;
    		}
    	}
    	return result;
    }
    /**
     * 
    * @Title: getExeBill
    * @Description: TODO(存在套餐医嘱收费操作逻辑)
    * @author pangben
    * @param orderSum 套餐数量
    * @param execDosageQty 已经处理的医嘱数量
    * @param diffQty
    * @param noExeBillParm
    * @param j
    * @param parmIbsOrdd
    * @param caseNo
    * @param admParm
    * @param i
    * @param numberParm
    * @param connection
    * @param statCode
    * @return
    * @throws
     */
    private TParm getExeBill(double orderSum,double execDosageQty,double diffQty,
    		TParm noExeBillParm,int j,TParm parmIbsOrdd,String caseNo,
    		TParm admParm,TParm numberParm,TConnection connection,String statCode,String caseNoNew){
    	TParm result=new TParm();
    	//比较统计的数量和套餐明细中汇总出的医嘱数量
		if (orderSum>=execDosageQty) {//如果套餐明细医嘱数量大于当前已经处理过的医嘱数量
			diffQty =orderSum- execDosageQty;//（差额数量）	
			if (diffQty>=noExeBillParm.getDouble("DOSAGE_QTY",j)) {//差额数量与当前操作的计费医嘱数量比较
				//套餐内医嘱，更新状态
				//diffQty= diffQty -execDosageQty ;//统计数量
				//result=null;
				return null;
			}else{
				if(diffQty>0){//存在套餐内医嘱和套外医嘱
					//将此计费医嘱数量分开
					//1.	将此操作医嘱冲负，套餐状态为套餐内
					//2.	添加一条套餐内医嘱，数量为DIFF_QTY，金额根据数量重新计算（根据第一身份确认金额，计费时间为当前时间）
					//3.	添加一条正的数据，数量为DOSAGE_QTY -DIFF_QTY，套餐状态为套餐外，
					//金额根据数量重新计算（根据第一身份和第二身份确认金额，计费时间为当前时间）
					//例如：
					//套餐购买字典 注射器 总量为10只
					//计费表中  注射器已经存在 9只 
					//7/8 日 操作计费  注射器 3只
					//需要操作的数据
					//数量       套餐状态     计费时间
					//-3           套餐内              7/8 日    冲负使用
					// 1           套餐内              7/8 日    补齐10只套餐内
					// 2           套餐外              7/8 日    生成套餐外数据
					//套餐外数据
					result=onSaveExeIbsOrddOne(parmIbsOrdd,caseNo, noExeBillParm.getValue("ORDER_CODE",j),diffQty,noExeBillParm.getDouble("DOSAGE_QTY",j),
    						 admParm, numberParm, connection,statCode,caseNoNew);
    				if (result.getErrCode()<0) {
    					return result;
					}
				}else{//diffQty<=0套外  
					//套餐外医嘱设置，将此医嘱冲负，套餐状态为套餐内，
					//并且添加一条套餐外数据，实收金额根据身份重新计算（根据第一身份和第二身份确认金额，计费时间为当前时间）
					result=onSaveExeIbsOrdd(parmIbsOrdd,caseNo,noExeBillParm.getDouble("DOSAGE_QTY",j), noExeBillParm.getValue("ORDER_CODE",j),
    						j, admParm, numberParm, connection,statCode,caseNoNew);
    				if (result.getErrCode()<0) {
    					return result;
					}
				}
			}
		}else{
			//套餐外医嘱设置，将此医嘱冲负，套餐状态为套餐内，并且添加一条套餐外数据，
			//实收金额根据身份重新计算（根据第一身份和第二身份确认金额，计费时间为当前时间）
			result=onSaveExeIbsOrdd(parmIbsOrdd,caseNo,noExeBillParm.getDouble("DOSAGE_QTY",j), noExeBillParm.getValue("ORDER_CODE",j),
					j, admParm, numberParm, connection,statCode,caseNoNew);
			if (result.getErrCode()<0) {
				return result;
			}
		}
		return result;
    }
    /**
     * 
    * @Title: getExeUnBill
    * @Description: TODO(存在套餐医嘱退费操作逻辑)
    * @author pangben
    * @param orderSum 套餐数量
    * @param execDosageQty 已经处理的医嘱数量
    * @return
    * @throws
     */
    private TParm getExeUnBill(double orderSum,double execDosageQty,double diffQty,TParm noExeBillParm,int j
    		,TParm parmIbsOrdd,String caseNo,TParm admParm,TParm numberParm,TConnection connection,String statCode,String caseNoNew){
    	TParm result=new TParm();
    	//比较统计的数量和套餐明细中汇总出的医嘱数量
    	if (execDosageQty>0) {//如果当前已经处理过的医嘱数量大于0，可以执行转套外操作
    		diffQty=execDosageQty+noExeBillParm.getDouble("DOSAGE_QTY",j);//已经执行的数量与需要操作的数量比较
    		if (diffQty>=0) {//已经执行的套餐外医嘱数量大于需要操作的医嘱数量
    			//将退费医嘱改成套外
    			result=onSaveUnExeIbsOrdd(parmIbsOrdd,caseNo,diffQty, noExeBillParm.getDouble("DOSAGE_QTY",j), noExeBillParm.getValue("ORDER_CODE",j),
						j, admParm, numberParm, connection,statCode,caseNoNew);
				if (result.getErrCode()<0) {
					return result;
				}
			}else{//将可以转成套外的数量和套内数量分开
				//将此计费医嘱数量分开
				//套餐外数据
				result=onSaveUnExeIbsOrddOne(parmIbsOrdd,caseNo, noExeBillParm.getValue("ORDER_CODE",j),-execDosageQty,
						noExeBillParm.getDouble("DOSAGE_QTY",j), admParm, numberParm, connection,statCode,caseNoNew);
				if (result.getErrCode()<0) {
					return result;
				}
			}
    	}else{
    		return null;
    	}
    	return result;
    }
    private TParm onSaveUnExeIbsOrddOne(TParm parmIbsOrdd,String caseNo,String orderCode,double diffQty,
    		double noExeDosageQty,TParm admParm,TParm numberParm,TConnection connection,String statCode,String caseNoNew){
    	TParm result=new TParm();
		if (parmIbsOrdd.getValue("ORDER_CODE",0).equals(parmIbsOrdd.getValue("ORDERSET_CODE",0))
				&&parmIbsOrdd.getValue("INDV_FLG",0).equals("N")) {//集合医嘱操作
			TParm parmIbsOrddNew=getOrderSetParm(parmIbsOrdd.getRow(0));
			result=onSaveUnOrderSetOrddParmOne(admParm, parmIbsOrddNew, numberParm, connection, diffQty,1, noExeDosageQty*-1,statCode,caseNoNew);
			if (result.getErrCode()<0) {
				result.setErr(-1,"操作失败:"+result.getErrText());
				return result;
			}
		}else{
			result=onSaveUnOrddParmOne(admParm, parmIbsOrdd, numberParm, diffQty,1, noExeDosageQty*-1, connection,statCode,caseNoNew);
			if (result.getErrCode()<0) {
				result.setErr(-1,"操作失败:"+result.getErrText());
				return result;
			}
		}
		return result;
    }
    /**
     * 
    * @Title: getOrderSetParm
    * @Description: TODO(查询集合医嘱细项)
    * @author pangben
    * @param selectparm
    * @return
    * @throws
     */
    private TParm getOrderSetParm(TParm selectparm){
    	//TParm orderParm=null;
    	String orderSql="SELECT A.*,B.IPD_NO AS IPD_NO_M, B.MR_NO AS MR_NO_M, B.DEPT_CODE AS DEPT_CODE_M,"+ 
        "B.STATION_CODE AS STATION_CODE_M, B.BED_NO AS BED_NO_M, B.DATA_TYPE AS DATA_TYPE_M, "+
        "B.OPT_USER AS OPT_USER_M, B.OPT_DATE AS OPT_DATE_M,C.CAT1_TYPE AS SYS_FEE_CAT1_TYPE,C.CHARGE_HOSP_CODE, "+
        "B.REGION_CODE AS REGION_CODE_M, B.COST_CENTER_CODE AS COST_CENTER_CODE_M,A.DOSAGE_QTY/"+selectparm.getDouble("DOSAGE_QTY")
        +" DOSAGE_QTY_NEW FROM IBS_ORDD A,IBS_ORDM B,SYS_FEE C " +
        "WHERE A.CASE_NO=B.CASE_NO AND A.CASE_NO_SEQ=B.CASE_NO_SEQ AND A.ORDER_CODE=C.ORDER_CODE AND A.CASE_NO='"+selectparm.getData("CASE_NO")+"' " +
        "AND A.CASE_NO_SEQ='"+selectparm.getValue("CASE_NO_SEQ")+"' AND A.ORDERSET_CODE='"+selectparm.getValue("ORDERSET_CODE")+"'" +
		  " AND A.ORDERSET_GROUP_NO='"+selectparm.getValue("ORDERSET_GROUP_NO")+
      "' AND (A.INCLUDE_EXEC_FLG='N' OR A.INCLUDE_EXEC_FLG IS NULL)";
    	//System.out.println("orderSql:::"+orderSql);
		TParm result=new TParm(TJDODBTool.getInstance().select(orderSql));
    	return result;
    }
    /**
     * 
    * @Title: onSaveExeIbsOrdd
    * @Description: TODO(需要操作的数据，套餐外流程)
    * @author pangben
    * @param caseNo
    * @param noExeBillParm
    * @param j
    * @param admParm
    * @param maxCaseNoSeqParm
    * @param maxCaseNoSeq
    * @param numberParm
    * @param connection
    * @return
    * @throws
     */
    private TParm onSaveExeIbsOrdd(TParm parmIbsOrdd,String caseNo,double dosageQty,String orderCode,int j,
    		TParm admParm,TParm numberParm,TConnection connection,String statCode,String caseNoNew){
    	TParm result=new TParm();
		if (parmIbsOrdd.getValue("ORDER_CODE",0).equals(parmIbsOrdd.getValue("ORDERSET_CODE",0))
				&&parmIbsOrdd.getValue("INDV_FLG",0).equals("N")) {//集合医嘱操作
			TParm parmIbsOrddNew=getOrderSetParm(parmIbsOrdd.getRow(0));
			result=onSaveOrderSetOrddParm(admParm, parmIbsOrddNew, caseNo,
					connection, true, numberParm, dosageQty,statCode,parmIbsOrdd,caseNoNew);
			if (result.getErrCode()<0) {
				result.setErr(-1,"操作失败:"+result.getErrText());
				return result;
			}
		}else{
			result=onSaveOrddParm(admParm, parmIbsOrdd, caseNo, 
					connection, true,numberParm, dosageQty,statCode,caseNoNew);
			if (result.getErrCode()<0) {
				result.setErr(-1,"操作失败:"+result.getErrText());
				return result;
			}
		}
		return result;
    }
    /**
     * 
    * @Title: onSaveExeIbsOrdd
    * @Description: TODO(需要操作的数据，套餐外流程)退费
    * @author pangben
    * @param caseNo
    * @param noExeBillParm
    * @param j
    * @param admParm
    * @param maxCaseNoSeqParm
    * @param maxCaseNoSeq
    * @param numberParm
    * @param connection
    * @return
    * @throws
     */
    private TParm onSaveUnExeIbsOrdd(TParm parmIbsOrdd,String caseNo,double diffQty,double dosageQty,String orderCode,int j,
    		TParm admParm,TParm numberParm,TConnection connection,String statCode,String caseNoNew){
    	TParm result=new TParm();
		if (parmIbsOrdd.getValue("ORDER_CODE",0).equals(parmIbsOrdd.getValue("ORDERSET_CODE",0))
				&&parmIbsOrdd.getValue("INDV_FLG",0).equals("N")) {//集合医嘱操作
			TParm parmIbsOrddNew=getOrderSetParm(parmIbsOrdd.getRow(0));
			result=onSaveOrderSetOrddParm(admParm, parmIbsOrddNew, caseNo,
					connection, true, numberParm, dosageQty,statCode,parmIbsOrdd,caseNoNew);
			if (result.getErrCode()<0) {
				result.setErr(-1,"操作失败:"+result.getErrText());
				return result;
			}
		}else{
			result=onSaveUnOrddParm(admParm, parmIbsOrdd, caseNo, 
					connection,numberParm,diffQty, dosageQty,statCode);
			if (result.getErrCode()<0) {
				result.setErr(-1,"操作失败:"+result.getErrText());
				return result;
			}
		}
		return result;
    }
    /**
     * 
    * @Title: onSaveExeIbsOrddOne
    * @Description: TODO(出现套餐内和套餐外的情况)
    * @author pangben
    * @param caseNo
    * @param orderCode
    * @return
    * @throws
     */
    private TParm onSaveExeIbsOrddOne(TParm parmIbsOrdd,String caseNo,String orderCode,double diffQty,
    		double noExeDosageQty,TParm admParm,TParm numberParm,TConnection connection,String statCode,String caseNoNew){
    	TParm result=new TParm();
		if (parmIbsOrdd.getValue("ORDER_CODE",0).equals(parmIbsOrdd.getValue("ORDERSET_CODE",0))
				&&parmIbsOrdd.getValue("INDV_FLG",0).equals("N")) {//集合医嘱操作
			TParm parmIbsOrddNew=getOrderSetParm(parmIbsOrdd.getRow(0));
			result=onSaveOrderSetOrddParmOne(admParm, parmIbsOrddNew, numberParm, connection, diffQty,-1, noExeDosageQty,statCode,parmIbsOrdd,caseNoNew);
			if (result.getErrCode()<0) {
				result.setErr(-1,"操作失败:"+result.getErrText());
				return result;
			}
		}else{
			result=onSaveOrddParmOne(admParm, parmIbsOrdd, numberParm, diffQty,-1, noExeDosageQty, connection,statCode,caseNoNew);
			if (result.getErrCode()<0) {
				result.setErr(-1,"操作失败:"+result.getErrText());
				return result;
			}
		}
		return result;
    }
    /**
     * 
    * @Title: getOnExeIncludeExecFlgBill
    * @Description: TODO(查询已经处理过的医嘱,统计数量)收费
    * @author pangben
    * @param caseNo
    * @param orderCode
    * @return
    * @throws
     */
    private double getOnExeIncludeExecFlgBill(String caseNo,String orderCode){
    	String sql="SELECT SUM(DOSAGE_QTY) DOSAGE_QTY  " +
    			"FROM IBS_ORDD WHERE CASE_NO IN ("+caseNo+") AND ORDER_CODE='"+orderCode+"' AND  INCLUDE_EXEC_FLG='Y'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	if (result.getCount("DOSAGE_QTY")<=0||result.getDouble("DOSAGE_QTY",0)<=0) {//没有处理的医嘱
			return 0.00;
		}
    	return result.getDouble("DOSAGE_QTY",0);
    }
    /**
     * 
    * @Title: getOnExeUnIncludeExecFlgBill
    * @Description: TODO(查询已经处理过的套外医嘱,统计数量)退费
    * @author pangben
    * @param caseNo
    * @param orderCode
    * @return
    * @throws
     */
    private double getOnExeUnIncludeExecFlgBill(String caseNo,String orderCode){
    	String sql="SELECT SUM(DOSAGE_QTY) DOSAGE_QTY  " +
		"FROM IBS_ORDD WHERE CASE_NO IN ("+caseNo+") AND ORDER_CODE='"+orderCode+"' AND INCLUDE_FLG='Y' AND INCLUDE_EXEC_FLG='Y'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	if (result.getCount("DOSAGE_QTY")<=0||result.getDouble("DOSAGE_QTY",0)<=0) {//没有处理的医嘱
    		return 0.00;
    	}
    	return result.getDouble("DOSAGE_QTY",0);
    }
    /**
     * 
    * @Title: getIbsOrddOrderParm
    * @Description: TODO(冲负使用数据)
    * @author pangben
    * @param caseNo
    * @param orderCode
    * @return
    * @throws
     */
    private TParm getIbsOrddOrderParm(String caseNo,String orderCode){
    	String sql="SELECT A.*,B.IPD_NO AS IPD_NO_M, B.MR_NO AS MR_NO_M, B.DEPT_CODE AS DEPT_CODE_M,"+ 
                   "B.STATION_CODE AS STATION_CODE_M, B.BED_NO AS BED_NO_M, B.DATA_TYPE AS DATA_TYPE_M, "+
                   "B.OPT_USER AS OPT_USER_M, B.OPT_DATE AS OPT_DATE_M, "+
                   "B.REGION_CODE AS REGION_CODE_M, B.COST_CENTER_CODE AS COST_CENTER_CODE_M," +
                   "C.CAT1_TYPE AS SYS_FEE_CAT1_TYPE,C.CHARGE_HOSP_CODE "+
                   " FROM IBS_ORDD A ,IBS_ORDM B,SYS_FEE C WHERE A.CASE_NO=B.CASE_NO AND A.CASE_NO_SEQ=B.CASE_NO_SEQ " +
                   " AND A.ORDER_CODE=C.ORDER_CODE AND " +
		"A.CASE_NO='"+caseNo+"' AND (A.INCLUDE_EXEC_FLG='N' OR A.INCLUDE_EXEC_FLG IS NULL) AND" +
				" (A.ORDERSET_CODE IS NULL OR A.ORDERSET_CODE=A.ORDER_CODE) AND A.ORDER_CODE='"+orderCode+"'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
     * 
    * @Title: getAdmInpInfo
    * @Description: TODO(查询存在未处理医嘱的在院套餐病人)
    * @author pangben
    * @return
    * @throws
     */
    public TParm getAdmInpInfo(){
    	String sql="SELECT A.CASE_NO,A.LUMPWORK_CODE,A.MR_NO,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.M_CASE_NO,A.NEW_BORN_FLG FROM ADM_INP A,IBS_ORDD B WHERE A.CASE_NO=B.CASE_NO" +
    			" AND A.LUMPWORK_CODE IS NOT NULL AND A.DS_DATE IS NULL AND B.INCLUDE_EXEC_FLG='N' AND A.CANCEL_FLG ='N' " +
    			"GROUP BY A.CASE_NO,A.LUMPWORK_CODE,A.MR_NO,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.M_CASE_NO,A.NEW_BORN_FLG";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
     * 
    * @Title: getNoIncludeExecFlgBill
    * @Description: TODO(根据就诊号查询未处理的计费医嘱数据,根据医嘱代码，数量排序)
    * @author pangben
    * @param caseNo
    * @return
    * @throws
     */
    public TParm getNoIncludeExecFlgBill(String caseNo){
    	String sql="SELECT SUM(DOSAGE_QTY) DOSAGE_QTY,ORDER_CODE FROM IBS_ORDD WHERE " +
    			"CASE_NO='"+caseNo+"' AND (INCLUDE_EXEC_FLG='N' OR INCLUDE_EXEC_FLG IS NULL) AND" +
    					" (ORDERSET_CODE IS NULL OR ORDERSET_CODE=ORDER_CODE) GROUP BY ORDER_CODE ORDER BY ORDER_CODE, DOSAGE_QTY";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
     * 
    * @Title: getMemPatPackAgeSection
    * @Description: TODO(遍利查询出来的未执行计费医嘱，根据医嘱代码条件查询当前套餐的医嘱明细数据,根据医嘱分组)
    * @author pangben
    * @param packCode 套餐代码
    * @param orderCode 医嘱代码
    * @return
    * @throws
     */
    public TParm getMemPatPackAgeSection(String caseNo,String packCode,String orderCode){
    	String sql="SELECT SUM(ORDER_NUM) ORDER_NUM, ORDER_CODE,UN_NUM_FLG FROM MEM_PAT_PACKAGE_SECTION_D " +
    			"WHERE CASE_NO='"+caseNo+"' AND PACKAGE_CODE='"+packCode+"' AND ORDER_CODE='"+orderCode+"' GROUP BY ORDER_CODE,UN_NUM_FLG";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
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
	public TParm selMaxCaseNoSeq(String caseNo) {
		String sql = " SELECT MAX(CASE_NO_SEQ) AS CASE_NO_SEQ FROM IBS_ORDM WHERE CASE_NO = '"
				+ caseNo + "' ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	private TParm onSaveIbsOrdm(TParm parmIbsOrdm,TParm numberParm,String caseNo,TConnection connection){
		TParm insertIbsOrdMNegativeParm = new TParm();					
		insertIbsOrdMNegativeParm.setData("CASE_NO",caseNo);
		insertIbsOrdMNegativeParm.setData("CASE_NO_SEQ",numberParm.getInt("CASE_NO_SEQ"));
		Timestamp sysDate = SystemTool.getInstance().getDate();
		insertIbsOrdMNegativeParm.setData("BILL_DATE",sysDate);
		insertIbsOrdMNegativeParm.setData("IPD_NO",parmIbsOrdm.getData("IPD_NO_M") == null ? new TNull(String.class) : parmIbsOrdm.getData("IPD_NO_M"));
		insertIbsOrdMNegativeParm.setData("MR_NO",parmIbsOrdm.getData("MR_NO_M") == null ? new TNull(String.class) : parmIbsOrdm.getData("MR_NO_M"));
		insertIbsOrdMNegativeParm.setData("DEPT_CODE",parmIbsOrdm.getData("DEPT_CODE_M") == null ? new TNull(String.class) : parmIbsOrdm.getData("DEPT_CODE_M"));
		insertIbsOrdMNegativeParm.setData("STATION_CODE",parmIbsOrdm.getData("STATION_CODE_M") == null ? new TNull(String.class) : parmIbsOrdm.getData("STATION_CODE_M"));
		insertIbsOrdMNegativeParm.setData("BED_NO",parmIbsOrdm.getData("BED_NO_M") == null ? new TNull(String.class) : parmIbsOrdm.getData("BED_NO_M"));
		insertIbsOrdMNegativeParm.setData("DATA_TYPE","1");
		insertIbsOrdMNegativeParm.setData("BILL_NO","");
		insertIbsOrdMNegativeParm.setData("OPT_USER","PACK_BATCH");
		insertIbsOrdMNegativeParm.setData("OPT_TERM","127.0.0.1");
		insertIbsOrdMNegativeParm.setData("REGION_CODE",parmIbsOrdm.getData("REGION_CODE_M") == null ? new TNull(String.class) : parmIbsOrdm.getData("REGION_CODE_M"));
		insertIbsOrdMNegativeParm.setData("COST_CENTER_CODE",parmIbsOrdm.getData("COST_CENTER_CODE_M") == null ? new TNull(String.class) : parmIbsOrdm.getData("COST_CENTER_CODE_M"));
		
		TParm result =IBSOrdmTool.getInstance().insertdataM(insertIbsOrdMNegativeParm,connection);
        if (result.getErrCode() < 0) {
            System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
	}
	private TParm getCommOnExeIbsOrdd(TParm parmIbsOrdd,int m,int seqNo,int maxCaseNoSeq,
			double dosageQty,Timestamp sysDate ,String CTZ1,String CTZ2,String CTZ3,BIL bil,
			int groupNo,TConnection connection,int mas,String orderNo,boolean flg,String statCode,double lumpworkRate,
			String lumpWorkCode,String level,int orderSeq,String caseNoNew,boolean billExeFlg){
		TParm insertIbsOrddNegativeParm = new TParm();		
		insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", m));
		insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
		insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
		getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, m, sysDate,mas,dosageQty);
		double ownRate =0.00;
		double ownPrice=0.00;
		double [] sumPrice = new double[2];
		if (flg) {//包干外
			sumPrice = IBSTool.getInstance().getOrderOwnPrice(parmIbsOrdd.getValue("ORDER_CODE",m),level);
			ownPrice=sumPrice[0];
			insertIbsOrddNegativeParm.setData("OWN_PRICE",ownPrice);
			ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE",m) ,
					parmIbsOrdd.getValue("ORDER_CODE",m));//包干外根据身份确认
			insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
			insertIbsOrddNegativeParm.setData("OWN_AMT",dosageQty*
					ownPrice*mas);
			insertIbsOrddNegativeParm.setData("INCLUDE_FLG","Y");
		}else{
			//药品、血费根据身份统计
			if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m)
					&&parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m).equals("PHA")||
					null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m)&&parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m).equals("RA")){
				ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", m) ,
						parmIbsOrdd.getValue("ORDER_CODE", m));
				sumPrice = IBSTool.getInstance().getOrderOwnPrice(parmIbsOrdd.getValue("ORDER_CODE", m),level);
				ownPrice=sumPrice[0];
			}else{
				ownRate=lumpworkRate;//包干内 根据住院登记设置的折扣统计
				ownPrice = IBSTool.getInstance().getLumpOrderOwnPrice(caseNoNew, lumpWorkCode,
						parmIbsOrdd.getValue("ORDER_CODE", m), level);
			}
			
    		insertIbsOrddNegativeParm.setData("OWN_PRICE",ownPrice);
    		insertIbsOrddNegativeParm.setData("OWN_AMT",dosageQty*ownPrice*mas);
//			ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE",m) ,
//					parmIbsOrdd.getValue("ORDER_CODE",m));//包干内 根据住院登记计算的折扣确认
			insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
		}
		insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
		insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate,2));
		insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",groupNo);
		insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
		insertIbsOrddNegativeParm.setData("ORDER_NO",orderNo);
		insertIbsOrddNegativeParm.setData("TAKE_DAYS",1);
		insertIbsOrddNegativeParm.setData("ORDER_SEQ",orderSeq);
		TParm result =null;
		if (billExeFlg) {
			insertIbsOrddNegativeParm.setData("BILL_EXE_FLG","Y");
			result =IBSOrdmTool.getInstance().insertdataLumpworkDExe(insertIbsOrddNegativeParm,connection);
		}else{
			result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
		}

        return result;
	}
	/**
	 * 集合医嘱操作套餐
	 * @param parmIbsOrdd
	 * @param m
	 * @param seqNo
	 * @param maxCaseNoSeq
	 * @param orderSeq
	 * @param dosageQty
	 * @param sysDate
	 * @param CTZ1
	 * @param CTZ2
	 * @param CTZ3
	 * @param bil
	 * @param groupNo
	 * @param connection
	 * @param mas
	 * @param orderNo
	 * @param flg
	 * @param statCode
	 * @param lumpworkRate
	 * @param lumpWorkCode
	 * @param level
	 * @param parmIbsOrddOld
	 * @param caseNo
	 * @return
	 */
	private TParm getCommOnExeOrderSetIbsOrdd(int seqNo,int maxCaseNoSeq,int orderSeq,
			double dosageQty,Timestamp sysDate ,String CTZ1,String CTZ2,String CTZ3,BIL bil,
			int groupNo,TConnection connection,String orderNo,boolean flg,String statCode,double lumpworkRate,
			String lumpWorkCode,String level,TParm parmIbsOrddOld,String caseNo,String caseNoNew){
		TParm result=new TParm();
		if(flg){//套外
			result=lumpWorkOutOnExeOrderSet(seqNo, maxCaseNoSeq, dosageQty, sysDate, CTZ1, CTZ2, CTZ3, bil,
					groupNo, connection,orderNo, statCode, lumpworkRate, level, parmIbsOrddOld, caseNo, orderSeq, true);
		}else{
			result=lumpWorkInOnExeOrderSet(seqNo, maxCaseNoSeq, dosageQty, sysDate, CTZ1,
					CTZ2, CTZ3, bil, groupNo, connection, orderNo, statCode, lumpworkRate, 
					lumpWorkCode, level, parmIbsOrddOld, caseNo, orderSeq, false,caseNoNew);
		}
		return result;
	}
	/**
	 * 套内集合医嘱执行
	 * @param parmIbsOrdd
	 * @param seqNo
	 * @param maxCaseNoSeq
	 * @param dosageQty
	 * @param sysDate
	 * @param CTZ1
	 * @param CTZ2
	 * @param CTZ3
	 * @param bil
	 * @param groupNo
	 * @param connection
	 * @param mas
	 * @param orderNo
	 * @param statCode
	 * @param lumpworkRate
	 * @param level
	 * @param parmIbsOrddOld
	 * @param caseNo
	 * @param orderSeq
	 * @param flg
	 * @return
	 */
	private TParm lumpWorkInOnExeOrderSet(int seqNo,int maxCaseNoSeq,
			double dosageQty,Timestamp sysDate ,String CTZ1,String CTZ2,String CTZ3,BIL bil,
			int groupNo,TConnection connection,String orderNo,String statCode,double lumpworkRate,String lumpworkCode,
			String level,TParm parmIbsOrddOld,String caseNo,int orderSeq,boolean flg,String caseNoNew){
		
		//重新获得折扣后的金额
    	String querySql = "SELECT A.ORDER_CODE,B.ORDER_CAT1_CODE,B.CAT1_TYPE,A.ORDERSET_CODE,B.CHARGE_HOSP_CODE HEXP_CODE," +
    			"A.ORDER_NUM MEDI_QTY,A.UNIT_CODE AS MEDI_UNIT,'' DOSE_CODE,A.ORDER_NUM DOSAGE_QTY,A.UNIT_CODE DOSAGE_UNIT," +
    			"C.IPD_CHARGE_CODE REXP_CODE,A.ORDER_DESC ORDER_CHN_DESC,A.UNIT_PRICE AS OWN_PRICE " +
    			"FROM MEM_PAT_PACKAGE_SECTION_D A ,SYS_FEE B,SYS_CHARGE_HOSP C " +
    			"WHERE A.ORDER_CODE=B.ORDER_CODE AND B.CHARGE_HOSP_CODE=C.CHARGE_HOSP_CODE  AND A.CASE_NO='" +caseNo
		+ "' AND A.PACKAGE_CODE = '"+lumpworkCode+"' AND A.ORDERSET_CODE='"+parmIbsOrddOld.getValue("ORDER_CODE",0)+"'";
    	TParm parmIbsOrddNew = new TParm(TJDODBTool.getInstance().select(querySql));
    	TParm insertIbsOrddNegativeParm=null;
    	TParm result=new TParm();
    	if(parmIbsOrddNew.getCount()>0){//套内集合医嘱
    		for (int m = 0; m < parmIbsOrddNew.getCount(); m++) {
    			//重新获得折扣后的金额
		        insertIbsOrddNegativeParm = new TParm();		
				insertIbsOrddNegativeParm.setData("CASE_NO",caseNo);					
				insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);							
				insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
				insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
				if (dosageQty<0) {
					getIbsOrddParmOne(insertIbsOrddNegativeParm, parmIbsOrddNew, m, sysDate,
							-1,dosageQty*Math.abs(parmIbsOrddNew.getDouble("DOSAGE_QTY",m))*-1,parmIbsOrddOld);
				}else{
					getIbsOrddParmOne(insertIbsOrddNegativeParm, parmIbsOrddNew, m, sysDate,1,
							dosageQty*Math.abs(parmIbsOrddNew.getDouble("DOSAGE_QTY",m)),parmIbsOrddOld);
				}
				insertIbsOrddNegativeParm.setData("ORDER_NO",orderNo);
				insertIbsOrddNegativeParm.setData("ORDER_SEQ",orderSeq);
				++orderSeq;
				insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",groupNo);
				double ownRate =0.00;
				double ownPrice=0.00;
				//药品、血费根据身份统计
				if(null!=parmIbsOrddNew.getValue("CAT1_TYPE",m)
						&&parmIbsOrddNew.getValue("CAT1_TYPE",m).equals("PHA")||
						null!=parmIbsOrddNew.getValue("HEXP_CODE",m)&&parmIbsOrddNew.getValue("HEXP_CODE",m).equals("RA")){
					 ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrddNew.getValue("HEXP_CODE",m) ,
							 parmIbsOrddNew.getValue("ORDER_CODE", m));		 
				}else{
					ownRate =lumpworkRate;//包干内根据住院登记操作时计算的折扣统计
				}
				ownPrice = parmIbsOrddNew.getDouble("OWN_PRICE", m);
				if (parmIbsOrddNew.getValue("ORDER_CODE", m).equals(parmIbsOrddNew.getValue("ORDERSET_CODE", m))) {
					insertIbsOrddNegativeParm.setData("OWN_PRICE",0);
					insertIbsOrddNegativeParm.setData("NHI_PRICE",0);
					insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
					insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
					insertIbsOrddNegativeParm.setData("OWN_AMT",0);
					insertIbsOrddNegativeParm.setData("TOT_AMT",0);
				}else{
					insertIbsOrddNegativeParm.setData("OWN_PRICE",ownPrice);
					insertIbsOrddNegativeParm.setData("NHI_PRICE",0);
					insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
					insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
					insertIbsOrddNegativeParm.setData("OWN_AMT",ownPrice*
							insertIbsOrddNegativeParm.getDouble("DOSAGE_QTY"));
					insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate,2));
				}

				++seqNo;
				result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
		        if (result.getErrCode() < 0) {
		        	System.out.println(result.getErrName() + " " + result.getErrText());
	                return result;
		        }
    		}
    		result.setData("SEQ_NO",seqNo);
    		result.setData("ORDER_SEQ",orderSeq);
    	}else{
    		result =lumpWorkOutOnExeOrderSet(seqNo, maxCaseNoSeq, dosageQty, sysDate, CTZ1, CTZ2,
    				CTZ3, bil, groupNo, connection, orderNo, statCode, lumpworkRate, level,
    				parmIbsOrddOld, caseNo, orderSeq, flg);
    		if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
                return result;
	        }
    	}
    	return result;
	}
	/**
	 * 套餐外集合医嘱执行
	 * 
	 * @return
	 */
	private TParm lumpWorkOutOnExeOrderSet(int seqNo,int maxCaseNoSeq,
			double dosageQty,Timestamp sysDate ,String CTZ1,String CTZ2,String CTZ3,BIL bil,
			int groupNo,TConnection connection,String orderNo,String statCode,double lumpworkRate,
			String level,TParm parmIbsOrddOld,String caseNo,int orderSeq,boolean flg){

		//套餐内不存在此集合医嘱,查询SYS_FEE转成套内
		String querySql = "SELECT A.ORDER_CODE,A.ORDER_CAT1_CODE,A.CAT1_TYPE,A.ORDER_CODE ORDERSET_CODE,A.CHARGE_HOSP_CODE HEXP_CODE,"
						+"1 MEDI_QTY,A.UNIT_CODE AS MEDI_UNIT,'' DOSE_CODE, 1 DOSAGE_QTY,A.UNIT_CODE DOSAGE_UNIT,"
						+"C.IPD_CHARGE_CODE REXP_CODE,A.ORDER_DESC ORDER_CHN_DESC,A.OWN_PRICE,A.OWN_PRICE2,A.OWN_PRICE3  FROM SYS_FEE A,SYS_CHARGE_HOSP C  "
						+"WHERE A.CHARGE_HOSP_CODE=C.CHARGE_HOSP_CODE AND A.ORDER_CODE='"+ parmIbsOrddOld.getValue("ORDER_CODE",0)+"' " 
						+"UNION ALL  SELECT A.ORDER_CODE,A.ORDER_CAT1_CODE,A.CAT1_TYPE,B.ORDERSET_CODE,A.CHARGE_HOSP_CODE HEXP_CODE," 
						+"B.DOSAGE_QTY MEDI_QTY,A.UNIT_CODE AS MEDI_UNIT,'' DOSE_CODE,B.DOSAGE_QTY,A.UNIT_CODE DOSAGE_UNIT," 
						+"C.IPD_CHARGE_CODE REXP_CODE,A.ORDER_DESC ORDER_CHN_DESC,A.OWN_PRICE,A.OWN_PRICE2,A.OWN_PRICE3 FROM SYS_FEE A,SYS_ORDERSETDETAIL B,SYS_CHARGE_HOSP C " 
						+" WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CHARGE_HOSP_CODE=C.CHARGE_HOSP_CODE AND B.ORDERSET_CODE='"
						+ parmIbsOrddOld.getValue("ORDER_CODE",0)+"'";
		TParm parmIbsOrddNew = new TParm(TJDODBTool.getInstance().select(querySql));
		TParm insertIbsOrddNegativeParm=null;
		TParm result=new TParm();
		for (int m = 0; m < parmIbsOrddNew.getCount(); m++) {
			//重新获得折扣后的金额
	        insertIbsOrddNegativeParm = new TParm();		
			insertIbsOrddNegativeParm.setData("CASE_NO",caseNo);					
			insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);							
			insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
			insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
			if (dosageQty<0) {
				getIbsOrddParmOne(insertIbsOrddNegativeParm, parmIbsOrddNew, m, sysDate,
						-1,dosageQty*Math.abs(parmIbsOrddNew.getDouble("DOSAGE_QTY",m))*-1,parmIbsOrddOld);
			}else{
				getIbsOrddParmOne(insertIbsOrddNegativeParm, parmIbsOrddNew, m, sysDate,1,
						dosageQty*Math.abs(parmIbsOrddNew.getDouble("DOSAGE_QTY",m)),parmIbsOrddOld);
			}
			insertIbsOrddNegativeParm.setData("ORDER_NO",orderNo);
			insertIbsOrddNegativeParm.setData("ORDER_SEQ",orderSeq);
			++orderSeq;
			insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",groupNo);
			double ownRate =0.00;
			double ownPrice=0.00;
			if(flg){//套外
				 ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrddNew.getValue("HEXP_CODE",m) ,
						 parmIbsOrddNew.getValue("ORDER_CODE", m));		 
				insertIbsOrddNegativeParm.setData("INCLUDE_FLG","Y");
			}else{
				//药品、血费根据身份统计
				if(null!=parmIbsOrddNew.getValue("CAT1_TYPE",m)
						&&parmIbsOrddNew.getValue("CAT1_TYPE",m).equals("PHA")||
						null!=parmIbsOrddNew.getValue("HEXP_CODE",m)&&parmIbsOrddNew.getValue("HEXP_CODE",m).equals("RA")){
					 ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrddNew.getValue("HEXP_CODE",m) ,
							 parmIbsOrddNew.getValue("ORDER_CODE", m));		 
				}else{
					ownRate =lumpworkRate;//包干内根据住院登记操作时计算的折扣统计
				}
				insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
			}
			if ("2".equals(level)) {
                ownPrice = parmIbsOrddNew.getDouble("OWN_PRICE2", m);
            } else if ("3".equals(level)) {
                ownPrice = parmIbsOrddNew.getDouble("OWN_PRICE3", m);
            } else
                ownPrice = parmIbsOrddNew.getDouble("OWN_PRICE", m);
			insertIbsOrddNegativeParm.setData("OWN_PRICE",ownPrice);
			insertIbsOrddNegativeParm.setData("NHI_PRICE",0);
			insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
			insertIbsOrddNegativeParm.setData("OWN_AMT",ownPrice*
					insertIbsOrddNegativeParm.getDouble("DOSAGE_QTY"));
			insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate,2));
			++seqNo;
			result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
	        if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
                return result;
	        }
		}	
		result.setData("SEQ_NO",seqNo);
		result.setData("ORDER_SEQ",orderSeq);
		return result;
	}
	/**
	 * 集合医嘱部分套内部分套外操作
	 * @param parm
	 * @param parmIbsOrdd
	 * @param numberParm
	 * @param connection
	 * @param diffQty 套餐外数量
	 * @param mas
	 * @param noExeDosageQty 套餐内数量
	 * @param statCode
	 * @param parmIbsOrddOld
	 * @return
	 */
	private TParm onSaveOrderSetOrddParmOne(TParm parm,TParm parmIbsOrdd,TParm numberParm,
			TConnection connection,double diffQty,int mas, double noExeDosageQty,String statCode,
			TParm parmIbsOrddOld,String caseNoNew){
		//新的身份
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		double lumpworkRate=parm.getDouble("LUMPWORK_RATE");//套餐折扣
		String level=parm.getValue("SERVICE_LEVEL");//服务等级
		String lumpworkCode=parm.getValue("LUMPWORK_CODE");//套餐代码
		// 得到系统时间
		Timestamp sysDate = SystemTool.getInstance().getDate();

		//循环费用明细档
		int seqNo=numberParm.getInt("SEQ_NO");
		int groupNo=numberParm.getInt("GROUP_NO");
		int maxCaseNoSeq=numberParm.getInt("CASE_NO_SEQ");
        BIL bil = new BIL();
        TParm result=new TParm();
        //TParm insertIbsOrddNegativeParm =null;
		String orderNo=SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO");
		int orderSeq=1;
        if(parmIbsOrdd.getCount("CASE_NO")>0){
			for(int m = 0;m<parmIbsOrdd.getCount("CASE_NO");m++){
				//step7:费用明细档插入负数据
				//ibs_ordd插入负数据  case_no_seq_new1,bill_date-->sysdate,金额为负
				result=getCommOnExeIbsOrdd(parmIbsOrdd, m, seqNo, maxCaseNoSeq,
						noExeDosageQty*Math.abs(parmIbsOrdd.getDouble("DOSAGE_QTY_NEW",m)), sysDate, CTZ1, 
						CTZ2, CTZ3, bil, groupNo, connection,mas,orderNo,false,statCode,lumpworkRate,lumpworkCode,
						level,orderSeq,caseNoNew,true);
				if (result.getErrCode() < 0) {
		        	System.out.println(result.getErrName() + " " + result.getErrText());
		            return result;
			    }
				++seqNo;
				++orderSeq;
//				result=getCommOnExeIbsOrdd(parmIbsOrdd, m, seqNo, maxCaseNoSeq,
//						diffQty*Math.abs(parmIbsOrdd.getDouble("DOSAGE_QTY_NEW",m)), sysDate, CTZ1, CTZ2, CTZ3,
//						bil, groupNo+1, connection,-mas,orderNo,false,statCode,lumpworkRate,lumpworkCode,level);
//				if (result.getErrCode() < 0) {
//		        	System.out.println(result.getErrName() + " " + result.getErrText());
//		            return result;
//			    }
//				++seqNo;
//				result=getCommOnExeIbsOrdd(parmIbsOrdd, m, seqNo, maxCaseNoSeq,
//						(noExeDosageQty-diffQty)*Math.abs(parmIbsOrdd.getDouble("DOSAGE_QTY_NEW",m)), sysDate, CTZ1, CTZ2, CTZ3,
//						bil, groupNo+2, connection,-mas,orderNo,true,statCode,lumpworkRate,lumpworkCode,level);
//				if (result.getErrCode() < 0) {
//		        	System.out.println(result.getErrName() + " " + result.getErrText());
//		            return result;
//			    }
//				++seqNo;
			}
			//执行状态修改  BILL_EXE_FLG
			result = getExeBillExeFlg(parmIbsOrddOld.getValue("CASE_NO",0),true, parmIbsOrddOld.getValue("ORDER_CODE",0), connection);
			if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
                return result;
			}
			//正数据操作集合医嘱
			//添加正数据
			//操作集合医嘱 重新计算费用 1. 套餐内转成套餐外，查询SYS_FEE表集合医嘱细项以及费用，根据SYS_FEE表数据添加IBS_ORDD表
	        //2.套餐外转成套餐内，查询MEM_PAT_PACKAGE_SECTION_D表此集合医嘱细项以及费用，
	        //根据MEM_PAT_PACKAGE_SECTION_D表数据添加IBS_ORDD表，
	        //如果MEM_PAT_PACKAGE_SECTION_D 表不存在此集合医嘱数据,则根据SYS_FEE表数据添加IBS_ORDD表
			result=getCommOnExeOrderSetIbsOrdd(seqNo, maxCaseNoSeq, orderSeq, diffQty,
					sysDate, CTZ1, CTZ2, CTZ3, bil, groupNo+1, connection, orderNo, false, statCode, 
					lumpworkRate, lumpworkCode, level, parmIbsOrddOld, parmIbsOrddOld.getValue("CASE_NO",0),caseNoNew);
			if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
	            return result;
		    }
			seqNo=result.getInt("SEQ_NO");
			orderSeq=result.getInt("ORDER_SEQ");
			result=getCommOnExeOrderSetIbsOrdd(seqNo, maxCaseNoSeq, orderSeq, (noExeDosageQty-diffQty),
					sysDate, CTZ1, CTZ2, CTZ3, bil, groupNo+2, connection, orderNo, true, statCode, 
					lumpworkRate, lumpworkCode, level, parmIbsOrddOld, parmIbsOrddOld.getValue("CASE_NO",0),caseNoNew);
			if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
	            return result;
		    }
		}
        return result;
	}
	/**
	 * 
	* @Title: onSaveUnOrderSetOrddParmOne
	* @Description: TODO(退费操作)
	* @author pangben
	* @param parm
	* @param parmIbsOrdd
	* @param numberParm
	* @param connection
	* @param diffQty
	* @param mas
	* @param noExeDosageQty
	* @param statCode
	* @return
	* @throws
	 */
	private TParm onSaveUnOrderSetOrddParmOne(TParm parm,TParm parmIbsOrdd,TParm numberParm,
			TConnection connection,double diffQty,int mas, double noExeDosageQty,String statCode,String caseNoNew){
		//新的身份
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		double lumpworkRate=parm.getDouble("LUMPWORK_RATE");//套餐折扣
		String level=parm.getValue("SERVICE_LEVEL");//服务等级
		String lumpworkCode=parm.getValue("LUMPWORK_CODE");//套餐代码
		// 得到系统时间
		Timestamp sysDate = SystemTool.getInstance().getDate();

		//循环费用明细档
		int seqNo=numberParm.getInt("SEQ_NO");
		int groupNo=numberParm.getInt("GROUP_NO");
		int maxCaseNoSeq=numberParm.getInt("CASE_NO_SEQ");
        BIL bil = new BIL();
        TParm result=new TParm();
        int orderSeq=1;
        //TParm insertIbsOrddNegativeParm =null;
		String orderNo=SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO");
        if(parmIbsOrdd.getCount("CASE_NO")>0){
			for(int m = 0;m<parmIbsOrdd.getCount("CASE_NO");m++){
				//step7:费用明细档插入负数据
				//ibs_ordd插入负数据  case_no_seq_new1,bill_date-->sysdate,金额为负
//				result=getCommOnExeIbsOrdd(parmIbsOrdd, m, seqNo, maxCaseNoSeq,
//						noExeDosageQty, sysDate, CTZ1, CTZ2, CTZ3, bil, groupNo, connection,mas,orderNo,false,statCode);
//				if (result.getErrCode() < 0) {
//		        	System.out.println(result.getErrName() + " " + result.getErrText());
//		            return result;
//			    }
//				++seqNo;
				result=getCommOnExeIbsOrdd(parmIbsOrdd, m, seqNo, maxCaseNoSeq,
						diffQty*Math.abs(parmIbsOrdd.getDouble("DOSAGE_QTY_NEW",m)), sysDate, CTZ1, CTZ2, CTZ3, 
						bil, groupNo+1, connection,mas,orderNo,true,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,true);
				if (result.getErrCode() < 0) {
		        	System.out.println(result.getErrName() + " " + result.getErrText());
		            return result;
			    }
				++seqNo;
				++orderSeq;
				result=getCommOnExeIbsOrdd(parmIbsOrdd, m, seqNo, maxCaseNoSeq,
						(diffQty+noExeDosageQty)*Math.abs(parmIbsOrdd.getDouble("DOSAGE_QTY_NEW",m)), sysDate, CTZ1, CTZ2, CTZ3, 
						bil, groupNo+2, connection,1,orderNo,false,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,false);
				if (result.getErrCode() < 0) {
		        	System.out.println(result.getErrName() + " " + result.getErrText());
		            return result;
			    }
				++seqNo;
				++orderSeq;
			}
		}
        return result;
	}
	/**
	 * 
	* @Title: onSaveOrderSetOrddParm
	* @Description: TODO(集合医嘱操作套餐外)
	* @author pangben
	* @param parm
	* @param maxCaseNoSeqParm
	* @param parmIbsOrdd
	* @param caseNo
	* @param connection
	* @param flg
	* @param maxCaseNoSeq
	* @param numberParm
	* @param dosageQty
	* @return
	* @throws
	 */
	private TParm onSaveOrderSetOrddParm(TParm parm ,TParm parmIbsOrdd,
			String caseNo,TConnection connection,boolean flg,TParm numberParm,
			double dosageQty,String statCode,TParm parmIbsOrddOld,String caseNoNew){
		//新的身份
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		double lumpworkRate=parm.getDouble("LUMPWORK_RATE");//套餐折扣
		String level=parm.getValue("SERVICE_LEVEL");//服务等级
		String lumpworkCode=parm.getValue("LUMPWORK_CODE");//套餐代码
		// 得到系统时间
		Timestamp sysDate = SystemTool.getInstance().getDate();

		//循环费用明细档
		int seqNo=numberParm.getInt("SEQ_NO");
		int groupNo=numberParm.getInt("GROUP_NO");
		int maxCaseNoSeq=numberParm.getInt("CASE_NO_SEQ");
		int orderSeq=1;
        BIL bil = new BIL();
        TParm result=new TParm();
        TParm insertIbsOrddNegativeParm =null;
		if(parmIbsOrdd.getCount("CASE_NO")>0){
			String orderNo=SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO");
			for(int m = 0;m<parmIbsOrdd.getCount("CASE_NO");m++){
				//step7:费用明细档插入负数据
				//ibs_ordd插入负数据  case_no_seq_new1,bill_date-->sysdate,金额为负
				insertIbsOrddNegativeParm = new TParm();		
				insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", m));
				insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
				insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
				if (dosageQty<0) {//集合医嘱数量重新计算
					getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, m, sysDate,1,dosageQty*Math.abs(parmIbsOrdd.getDouble("DOSAGE_QTY_NEW",m))*-1);
				}else{
					getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, m, sysDate,-1,dosageQty*Math.abs(parmIbsOrdd.getDouble("DOSAGE_QTY_NEW",m)));
				}
				double ownRate =0.00;
				//药品、血费根据身份统计
				if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m)
						&&parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m).equals("PHA")||
						null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m)&&parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m).equals("RA")){
					ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", m) ,
							parmIbsOrdd.getValue("ORDER_CODE", m));
				}else{
					ownRate=lumpworkRate;//包干内 根据住院登记设置的折扣统计
				}
				
//				double ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", m) ,
//						parmIbsOrdd.getValue("ORDER_CODE", m));//包干内 第一身份和第二身份统计
				insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
				insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
				insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate, 2));
				insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",groupNo);
				insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
				insertIbsOrddNegativeParm.setData("BILL_EXE_FLG","Y");
				++seqNo;
				result =IBSOrdmTool.getInstance().insertdataLumpworkDExe(insertIbsOrddNegativeParm,connection);
		        if (result.getErrCode() < 0) {
		        	System.out.println(result.getErrName() + " " + result.getErrText());
	                return result;
		        }
		       
			}
			//执行状态修改  BILL_EXE_FLG
			result = getExeBillExeFlg(caseNo,true, parmIbsOrddOld.getValue("ORDER_CODE",0), connection);
			if (result.getErrCode() < 0) {
		        	System.out.println(result.getErrName() + " " + result.getErrText());
	                return result;
		    }
			//添加正数据
			//操作集合医嘱 重新计算费用 1. 套餐内转成套餐外，查询SYS_FEE表集合医嘱细项以及费用，根据SYS_FEE表数据添加IBS_ORDD表
	        //2.套餐外转成套餐内，查询MEM_PAT_PACKAGE_SECTION_D表此集合医嘱细项以及费用，
	        //根据MEM_PAT_PACKAGE_SECTION_D表数据添加IBS_ORDD表，
	        //如果MEM_PAT_PACKAGE_SECTION_D 表不存在此集合医嘱数据,则根据SYS_FEE表数据添加IBS_ORDD表
			//正数据操作集合医嘱
			//添加正数据
			//操作集合医嘱 重新计算费用 1. 套餐内转成套餐外，查询SYS_FEE表集合医嘱细项以及费用，根据SYS_FEE表数据添加IBS_ORDD表
	        //2.套餐外转成套餐内，查询MEM_PAT_PACKAGE_SECTION_D表此集合医嘱细项以及费用，
	        //根据MEM_PAT_PACKAGE_SECTION_D表数据添加IBS_ORDD表，
	        //如果MEM_PAT_PACKAGE_SECTION_D 表不存在此集合医嘱数据,则根据SYS_FEE表数据添加IBS_ORDD表
			result=getCommOnExeOrderSetIbsOrdd(seqNo, maxCaseNoSeq, orderSeq, dosageQty,
					sysDate, CTZ1, CTZ2, CTZ3, bil, groupNo+1, connection, orderNo, flg, statCode, 
					lumpworkRate, lumpworkCode, level, parmIbsOrddOld, parmIbsOrddOld.getValue("CASE_NO",0),caseNoNew);
			if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
	            return result;
		    }
			numberParm.setData("GROUP_NO",groupNo+2);//确定GROUP_NO唯一性	
		}
		return result;
	}
	/**
	 * 
	* @Title: onSaveUnOrddParmOne
	* @Description: TODO(药品退药操作)
	* @author pangben
	* @param parm
	* @param parmIbsOrdd
	* @param numberParm
	* @param diffQty
	* @param mas
	* @param noExeDosageQty
	* @param connection
	* @param statCode
	* @return
	* @throws
	 */
	private TParm onSaveUnOrddParmOne(TParm parm,TParm parmIbsOrdd,
			TParm numberParm,double diffQty,int mas,double noExeDosageQty,TConnection connection,String statCode,String caseNoNew){
		//新的身份
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		double lumpworkRate=parm.getDouble("LUMPWORK_RATE");//套餐折扣
		String level=parm.getValue("SERVICE_LEVEL");//服务等级
		String lumpworkCode=parm.getValue("LUMPWORK_CODE");//套餐代码
		// 得到系统时间
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String orderNo=SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO");
		//循环费用明细档
		int seqNo=numberParm.getInt("SEQ_NO");
		int maxCaseNoSeq=numberParm.getInt("CASE_NO_SEQ");
        BIL bil = new BIL();
        TParm result=new TParm();
        int orderSeq=1;
        //TParm insertIbsOrddNegativeParm = new TParm();
        result=getCommOnExeIbsOrdd(parmIbsOrdd, 0, seqNo, maxCaseNoSeq,
				noExeDosageQty, sysDate, CTZ1, CTZ2, CTZ3, bil, 0, 
				connection,mas,orderNo,false,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,true);
		if (result.getErrCode() < 0) {
        	System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
	    }
		result = getExeBillExeFlg(parmIbsOrdd.getValue("CASE_NO",0),false, parmIbsOrdd.getValue("ORDER_CODE",0), connection);
		if (result.getErrCode() < 0) {
	        System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
	    }
		//diffQty套餐外已经执行的医嘱    退diffQty套餐外
		if (diffQty!=0) {
			++seqNo;
			++orderSeq;
			result=getCommOnExeIbsOrdd(parmIbsOrdd, 0, seqNo, maxCaseNoSeq,
					diffQty, sysDate, CTZ1, CTZ2, CTZ3, bil,0, connection,1,orderNo,
					true,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,false);
			if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
	            return result;
		    }
		}
		//diffQty+noExeDosageQty 为正数  执行到 套餐内
		if (-noExeDosageQty-diffQty!=0) {
			++seqNo;
			++orderSeq;
			result=getCommOnExeIbsOrdd(parmIbsOrdd, 0, seqNo, maxCaseNoSeq,
					-noExeDosageQty-diffQty, sysDate, CTZ1, CTZ2, CTZ3, bil, 0,
					connection,1,orderNo,false,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,false);
			if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
	            return result;
		    }
		}
		return result;
	}
	private TParm onSaveOrddParmOne(TParm parm,TParm parmIbsOrdd,
			TParm numberParm,double diffQty,int mas,double noExeDosageQty,TConnection connection,String statCode,String caseNoNew){
		//新的身份
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		double lumpworkRate=parm.getDouble("LUMPWORK_RATE");//套餐折扣
		String level=parm.getValue("SERVICE_LEVEL");//服务等级
		String lumpworkCode=parm.getValue("LUMPWORK_CODE");//套餐代码
		// 得到系统时间
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String orderNo=SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO");
		//循环费用明细档
		int seqNo=numberParm.getInt("SEQ_NO");
		int maxCaseNoSeq=numberParm.getInt("CASE_NO_SEQ");
		int orderSeq=1;
        BIL bil = new BIL();
        TParm result=new TParm();
        //TParm insertIbsOrddNegativeParm = new TParm();
        result=getCommOnExeIbsOrdd(parmIbsOrdd, 0, seqNo, maxCaseNoSeq,
				noExeDosageQty, sysDate, CTZ1, CTZ2, CTZ3, bil, 0, connection,mas,
				orderNo,false,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,true);
		if (result.getErrCode() < 0) {
        	System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
	    }
		result = getExeBillExeFlg(parmIbsOrdd.getValue("CASE_NO",0),false, parmIbsOrdd.getValue("ORDER_CODE",0), connection);
		if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
                return result;
	    }
		++seqNo;
		++orderSeq;
		result=getCommOnExeIbsOrdd(parmIbsOrdd, 0, seqNo, maxCaseNoSeq,
				diffQty, sysDate, CTZ1, CTZ2, CTZ3, bil,0, connection,1,orderNo,
				false,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,false);
		if (result.getErrCode() < 0) {
        	System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
	    }
		++seqNo;
		++orderSeq;
		result=getCommOnExeIbsOrdd(parmIbsOrdd, 0, seqNo, maxCaseNoSeq,
				noExeDosageQty-diffQty, sysDate, CTZ1, CTZ2, CTZ3, bil, 0, 
				connection,1,orderNo,true,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,false);
		if (result.getErrCode() < 0) {
        	System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
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
	private TParm onSaveOrddParm(TParm parm,TParm parmIbsOrdd,
			String caseNo,TConnection connection,boolean flg,
			TParm numberParm,double dosageQty,String statCode,String caseNoNew){
		//新的身份
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		double lumpworkRate=parm.getDouble("LUMPWORK_RATE");//套餐折扣
		String lumpWorkCode=parm.getValue("LUMPWORK_CODE");
		String level=parm.getValue("SERVICE_LEVEL");//服务等级
		// 得到系统时间
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String orderNo=SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO");
		//循环费用明细档
		int seqNo=numberParm.getInt("SEQ_NO");
		int maxCaseNoSeq=numberParm.getInt("CASE_NO_SEQ");
        BIL bil = new BIL();
        TParm result=new TParm();
        TParm insertIbsOrddNegativeParm = new TParm();	
		insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", 0) == null 
				? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", 0));						
		insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
		insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
		getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,-1,dosageQty);
		double ownRate=0.00;
		//药品、血费根据身份统计
		if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",0)
				&&parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",0).equals("PHA")||
				null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",0)&&parmIbsOrdd.getValue("CHARGE_HOSP_CODE",0).equals("RA")){
			ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", 0) ,
					parmIbsOrdd.getValue("ORDER_CODE", 0));
		}else{
			ownRate=lumpworkRate;//包干内 根据住院登记设置的折扣统计
		}
//		double ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", 0) ,
//				parmIbsOrdd.getValue("ORDER_CODE", 0));//包干内 第一身份和第二身份统计
		insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
		insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
		insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate,2));
		insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",0);
		insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
		insertIbsOrddNegativeParm.setData("BILL_EXE_FLG","Y");
		++seqNo;
		result =IBSOrdmTool.getInstance().insertdataLumpworkDExe(insertIbsOrddNegativeParm,connection);
        if (result.getErrCode() < 0) {
        	System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //执行状态修改  BILL_EXE_FLG
		result = getExeBillExeFlg(caseNo,false, parmIbsOrdd.getValue("ORDER_CODE",0), connection);
		if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
                return result;
	    }
        //重新获得折扣后的金额
        insertIbsOrddNegativeParm = new TParm();		
		insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", 0) == null ?
				new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", 0));					
		insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);						
		insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
		//getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,1,dosageQty);
		getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,1,dosageQty);
		insertIbsOrddNegativeParm.setData("ORDER_NO",orderNo);
		insertIbsOrddNegativeParm.setData("ORDER_SEQ","1");
		insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",0);
		insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
		double ownPrice=0.00;
		TParm feeParm =null;
		double [] sumPrice=new double[2];
		if (flg) {//包干外
			sumPrice = IBSTool.getInstance().getOrderOwnPrice(parmIbsOrdd.getValue("ORDER_CODE",0),level);
			ownPrice=sumPrice[0];
			insertIbsOrddNegativeParm.setData("OWN_PRICE",ownPrice);
			ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE",0) ,
					parmIbsOrdd.getValue("ORDER_CODE",0));//包干外第一身份
			insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
			insertIbsOrddNegativeParm.setData("OWN_AMT",ownPrice*dosageQty);
			insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(ownPrice*dosageQty*ownRate,2));
			insertIbsOrddNegativeParm.setData("INCLUDE_FLG","Y");
		}else{
			//药品、血费根据身份统计
			if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",0)
					&&parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",0).equals("PHA")||
					null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",0)&&parmIbsOrdd.getValue("CHARGE_HOSP_CODE",0).equals("RA")){
				ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", 0) ,
						parmIbsOrdd.getValue("ORDER_CODE", 0));
				sumPrice = IBSTool.getInstance().getOrderOwnPrice(parmIbsOrdd.getValue("ORDER_CODE",0),level);
				ownPrice=sumPrice[0];
			}else{
				ownRate=lumpworkRate;//包干内 根据住院登记设置的折扣统计
				ownPrice = IBSTool.getInstance().getLumpOrderOwnPrice(caseNoNew, lumpWorkCode, 
						parmIbsOrdd.getValue("ORDER_CODE", 0), level);
			}
			
            insertIbsOrddNegativeParm.setData("OWN_PRICE",ownPrice);
			insertIbsOrddNegativeParm.setData("OWN_AMT",ownPrice*dosageQty);
//			ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE",0) ,
//					parmIbsOrdd.getValue("ORDER_CODE",0));//包干内 第一身份和第二身份统计
			insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
			insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(ownPrice*dosageQty*ownRate,2));
			insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
		}
		result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
		if (result.getErrCode() < 0) {
			System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
        }
		++seqNo;
		return result;
	}
	/**
	 * 
	* @Title: onSaveUnOrddParm
	* @Description: TODO(公用獲得IBS_ORDD表入參) 退费
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
	private TParm onSaveUnOrddParm(TParm parm,TParm parmIbsOrdd,
			String caseNo,TConnection connection,TParm numberParm,double diffQty,double dosageQty,String statCode){
		//新的身份
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		double lumpworkRate=parm.getDouble("LUMPWORK_RATE");//套餐折扣
		// 得到系统时间
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String orderNo=SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO");
		//循环费用明细档
		int seqNo=numberParm.getInt("SEQ_NO");
		int maxCaseNoSeq=numberParm.getInt("CASE_NO_SEQ");
        BIL bil = new BIL();
        TParm result=new TParm();
        TParm insertIbsOrddNegativeParm = new TParm();	
		insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", 0) == null 
				? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", 0));						
		insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
		insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
		getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,1,dosageQty*-1);
		
		double  ownRate =0.00;//包干内根据住院登记设置的折扣进行计算
		//药品、血费根据身份统计
		if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",0)
				&&parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",0).equals("PHA")||
				null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",0)&&parmIbsOrdd.getValue("CHARGE_HOSP_CODE",0).equals("RA")){
			ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", 0) ,
					parmIbsOrdd.getValue("ORDER_CODE", 0));
		}else{
			ownRate=lumpworkRate;//包干内 根据住院登记设置的折扣统计
		}
//		double ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", 0) ,
//				parmIbsOrdd.getValue("ORDER_CODE", 0));//包干内 第一身份和第二身份统计
		insertIbsOrddNegativeParm.setData("OWN_PRICE",insertIbsOrddNegativeParm.getDouble("OWN_PRICE"));
		insertIbsOrddNegativeParm.setData("NHI_PRICE",insertIbsOrddNegativeParm.getDouble("NHI_PRICE"));
		insertIbsOrddNegativeParm.setData("OWN_AMT",insertIbsOrddNegativeParm.getDouble("OWN_AMT"));
		insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
		insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
		insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate,2));
		insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",0);
		insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
		insertIbsOrddNegativeParm.setData("BILL_EXE_FLG","Y");
		result =IBSOrdmTool.getInstance().insertdataLumpworkDExe(insertIbsOrddNegativeParm,connection);
        if (result.getErrCode() < 0) {
        	System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
        }
        
        //执行状态修改  BILL_EXE_FLG
		result = getExeBillExeFlg(caseNo,false, parmIbsOrdd.getValue("ORDER_CODE",0), connection);
		if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
                return result;
	    }
        ++seqNo;
        //重新获得折扣后的金额
        insertIbsOrddNegativeParm = new TParm();		
		insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", 0) == null ?
				new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", 0));					
		insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);						
		insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
		//getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,1,dosageQty);
		getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,1,diffQty*-1);
		insertIbsOrddNegativeParm.setData("ORDER_NO",orderNo);
		insertIbsOrddNegativeParm.setData("ORDER_SEQ","1");
		insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",0);
		insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
		ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE",0) ,
				parmIbsOrdd.getValue("ORDER_CODE",0));//包干外根据身份进行计算
		insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
		insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate,2));
		insertIbsOrddNegativeParm.setData("INCLUDE_FLG","Y");
		result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
		if (result.getErrCode() < 0) {
        	System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
		}
		//getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,1,dosageQty);
		if (diffQty+dosageQty!=0) {
			 ++seqNo;
	        //重新获得折扣后的金额
	        insertIbsOrddNegativeParm = new TParm();		
			insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", 0) == null ?
					new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", 0));					
			insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);						
			insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
			getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,1,diffQty+dosageQty);
			insertIbsOrddNegativeParm.setData("ORDER_NO",orderNo);
			insertIbsOrddNegativeParm.setData("ORDER_SEQ","1");
			insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",0);
			insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
			ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE",0) ,
					parmIbsOrdd.getValue("ORDER_CODE",0));//包干外根据身份进行计算
			insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
			insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate,2));
			insertIbsOrddNegativeParm.setData("INCLUDE_FLG","Y");
			result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrName() + " " + result.getErrText());
	            return result;
	        }
		}
		++seqNo;
		return result;
	}
	/**
	 * 集合医嘱使用
	 * @param insertIbsOrddNegativeParm
	 * @param parmIbsOrdd
	 * @param m
	 * @param sysDate
	 * @param mas
	 * @param dosageQty
	 */
	private void getIbsOrddParmOne(TParm insertIbsOrddNegativeParm,
			TParm parmIbsOrddNew,int m,Timestamp sysDate,int mas,double dosageQty,TParm parmIbsOrdd){
		insertIbsOrddNegativeParm.setData("BILL_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("EXEC_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("ORDER_NO",parmIbsOrdd.getData("ORDER_NO", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_NO",0));
		insertIbsOrddNegativeParm.setData("ORDER_SEQ",parmIbsOrdd.getData("ORDER_SEQ", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_SEQ", 0));
		insertIbsOrddNegativeParm.setData("ORDER_CODE",parmIbsOrddNew.getData("ORDER_CODE", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("ORDER_CODE", m));
		insertIbsOrddNegativeParm.setData("ORDER_CAT1_CODE",parmIbsOrddNew.getData("ORDER_CAT1_CODE", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("ORDER_CAT1_CODE", m));
		insertIbsOrddNegativeParm.setData("CAT1_TYPE",parmIbsOrddNew.getData("CAT1_TYPE", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("CAT1_TYPE", m));
		insertIbsOrddNegativeParm.setData("ORDERSET_CODE",parmIbsOrddNew.getData("ORDERSET_CODE", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("ORDERSET_CODE", m));
		if (parmIbsOrddNew.getValue("ORDER_CODE",m).equals(parmIbsOrddNew.getValue("ORDERSET_CODE",m))) {
			insertIbsOrddNegativeParm.setData("INDV_FLG","N");
		}else{
			insertIbsOrddNegativeParm.setData("INDV_FLG","Y");
		}
		
		insertIbsOrddNegativeParm.setData("DEPT_CODE",parmIbsOrdd.getData("DEPT_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("DEPT_CODE", 0));
		insertIbsOrddNegativeParm.setData("STATION_CODE",parmIbsOrdd.getData("STATION_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("STATION_CODE", 0));
		insertIbsOrddNegativeParm.setData("DR_CODE",parmIbsOrdd.getData("DR_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("DR_CODE", 0));
		insertIbsOrddNegativeParm.setData("EXE_DEPT_CODE",parmIbsOrdd.getData("EXE_DEPT_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DEPT_CODE", 0));
		insertIbsOrddNegativeParm.setData("EXE_STATION_CODE",parmIbsOrdd.getData("EXE_STATION_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_STATION_CODE", 0));
		insertIbsOrddNegativeParm.setData("EXE_DR_CODE",parmIbsOrdd.getData("EXE_DR_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DR_CODE", 0));
		insertIbsOrddNegativeParm.setData("MEDI_QTY",dosageQty*mas);
		insertIbsOrddNegativeParm.setData("MEDI_UNIT",parmIbsOrddNew.getData("MEDI_UNIT", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("MEDI_UNIT", m));
		insertIbsOrddNegativeParm.setData("DOSE_CODE","");
		insertIbsOrddNegativeParm.setData("FREQ_CODE",parmIbsOrdd.getData("FREQ_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("FREQ_CODE", 0));
		insertIbsOrddNegativeParm.setData("TAKE_DAYS",1*mas);
		insertIbsOrddNegativeParm.setData("DOSAGE_QTY",dosageQty*mas);
		insertIbsOrddNegativeParm.setData("DOSAGE_UNIT",parmIbsOrddNew.getData("DOSAGE_UNIT", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("DOSAGE_UNIT", m));
		insertIbsOrddNegativeParm.setData("OWN_PRICE",parmIbsOrddNew.getDouble("OWN_PRICE",m));
		insertIbsOrddNegativeParm.setData("NHI_PRICE",parmIbsOrddNew.getDouble("NHI_PRICE",m));
		insertIbsOrddNegativeParm.setData("OWN_FLG",parmIbsOrdd.getData("OWN_FLG", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("OWN_FLG", 0));
		insertIbsOrddNegativeParm.setData("BILL_FLG",parmIbsOrdd.getData("BILL_FLG", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("BILL_FLG", 0));
		insertIbsOrddNegativeParm.setData("REXP_CODE",parmIbsOrddNew.getData("REXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("REXP_CODE", m));
		insertIbsOrddNegativeParm.setData("BILL_NO","");
		insertIbsOrddNegativeParm.setData("HEXP_CODE",parmIbsOrddNew.getData("HEXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("HEXP_CODE", m));
		insertIbsOrddNegativeParm.setData("BEGIN_DATE",parmIbsOrdd.getData("BEGIN_DATE", 0) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("BEGIN_DATE", 0));
		insertIbsOrddNegativeParm.setData("END_DATE",parmIbsOrdd.getData("END_DATE", 0) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("END_DATE", 0));
		insertIbsOrddNegativeParm.setData("OWN_AMT",parmIbsOrdd.getDouble("OWN_PRICE",m)*dosageQty*mas);
		insertIbsOrddNegativeParm.setData("REQUEST_FLG",parmIbsOrdd.getData("REQUEST_FLG", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_FLG", 0));
		insertIbsOrddNegativeParm.setData("REQUEST_NO",parmIbsOrdd.getData("REQUEST_NO", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_NO", 0));
		insertIbsOrddNegativeParm.setData("INV_CODE",parmIbsOrdd.getData("INV_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("INV_CODE", 0));
		insertIbsOrddNegativeParm.setData("OPT_USER","PACK_BATCH");
		insertIbsOrddNegativeParm.setData("OPT_TERM","127.0.0.1");
		insertIbsOrddNegativeParm.setData("COST_AMT",dosageQty*mas>=0?Math.abs(parmIbsOrdd.getDouble("COST_AMT",0)):-Math.abs(parmIbsOrdd.getDouble("COST_AMT",0)));
		insertIbsOrddNegativeParm.setData("ORDER_CHN_DESC",parmIbsOrddNew.getData("ORDER_CHN_DESC", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("ORDER_CHN_DESC", m));
		insertIbsOrddNegativeParm.setData("COST_CENTER_CODE",parmIbsOrdd.getData("COST_CENTER_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("COST_CENTER_CODE", 0));
		insertIbsOrddNegativeParm.setData("SCHD_CODE",parmIbsOrdd.getData("SCHD_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("SCHD_CODE", 0));
		insertIbsOrddNegativeParm.setData("CLNCPATH_CODE",parmIbsOrdd.getData("CLNCPATH_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("CLNCPATH_CODE", 0));
		insertIbsOrddNegativeParm.setData("DS_FLG",parmIbsOrdd.getData("DS_FLG", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("DS_FLG", 0));
		insertIbsOrddNegativeParm.setData("KN_FLG",parmIbsOrdd.getData("KN_FLG", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("KN_FLG", 0));
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
	private void getIbsOrddParm(TParm insertIbsOrddNegativeParm,
			TParm parmIbsOrdd,int m,Timestamp sysDate,int mas,double dosageQty){
		insertIbsOrddNegativeParm.setData("BILL_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("EXEC_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("ORDER_NO",parmIbsOrdd.getData("ORDER_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_NO",m));
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
		if (parmIbsOrdd.getValue("CAT1_TYPE",m).equals("PHA")) {//药品计算用量
			String sql="SELECT MEDI_QTY FROM PHA_TRANSUNIT WHERE ORDER_CODE='"+parmIbsOrdd.getData("ORDER_CODE", m)+"'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			insertIbsOrddNegativeParm.setData("MEDI_QTY",result.getDouble("MEDI_QTY",0)*dosageQty*mas);
		}else{
			insertIbsOrddNegativeParm.setData("MEDI_QTY",dosageQty*mas);
		}
		insertIbsOrddNegativeParm.setData("MEDI_UNIT",parmIbsOrdd.getData("MEDI_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("MEDI_UNIT", m));
		insertIbsOrddNegativeParm.setData("DOSE_CODE",parmIbsOrdd.getData("DOSE_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSE_CODE", m));
		insertIbsOrddNegativeParm.setData("FREQ_CODE",parmIbsOrdd.getData("FREQ_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("FREQ_CODE", m));
		insertIbsOrddNegativeParm.setData("TAKE_DAYS",1*mas);
		insertIbsOrddNegativeParm.setData("DOSAGE_QTY",dosageQty*mas);
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
		insertIbsOrddNegativeParm.setData("OWN_AMT",parmIbsOrdd.getDouble("OWN_PRICE",m)*dosageQty*mas);
		insertIbsOrddNegativeParm.setData("REQUEST_FLG",parmIbsOrdd.getData("REQUEST_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_FLG", m));
		insertIbsOrddNegativeParm.setData("REQUEST_NO",parmIbsOrdd.getData("REQUEST_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_NO", m));
		insertIbsOrddNegativeParm.setData("INV_CODE",parmIbsOrdd.getData("INV_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INV_CODE", m));
		insertIbsOrddNegativeParm.setData("OPT_USER","PACK_BATCH");
		insertIbsOrddNegativeParm.setData("OPT_TERM","127.0.0.1");
		insertIbsOrddNegativeParm.setData("COST_AMT",dosageQty*mas>=0?Math.abs(parmIbsOrdd.getDouble("COST_AMT",m)):-Math.abs(parmIbsOrdd.getDouble("COST_AMT",m)));
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
	public int selMaxOrderSetGroupNo(String caseNo) {
		String sql = " SELECT MAX(TO_NUMBER(ORDERSET_GROUP_NO)) AS ORDERSET_GROUP_NO FROM IBS_ORDD WHERE CASE_NO = '"
				+ caseNo + "' AND ORDERSET_CODE IS NOT NULL";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount()>0) {
			return result.getInt("ORDERSET_GROUP_NO",0);
		}
		return -1;
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
		insertIbsOrddNegativeParm.setData("NHI_PRICE",parmIbsOrdd.getDouble("OWN_PRICE",m));
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
}
