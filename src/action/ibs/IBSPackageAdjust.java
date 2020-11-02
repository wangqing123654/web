package action.ibs;

import java.sql.Timestamp;

import javax.jws.soap.InitParam;

import jdo.bil.BIL;
import jdo.ibs.IBSNewTool;
import jdo.ibs.IBSOrdmTool;
import jdo.ibs.IBSTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

public class IBSPackageAdjust extends TAction {
	TParm infoParm = new TParm();
	TParm tableYparm = new TParm();
	TParm tableNparm = new TParm();
	public TParm onPackageAdjust(TParm parm) {
		TConnection connection = getConnection();
		// 作废数据
		// ibs_ordd 插入负数
		TParm result = new TParm();
		 infoParm = parm.getParm("initParm");
		 tableYparm = parm.getParm("tableYParm");
		 tableNparm = parm.getParm("tableNParm");
		String CTZ1 = infoParm.getValue("CTZ1_CODE");
		String CTZ2 = infoParm.getValue("CTZ2_CODE");
		String CTZ3 = infoParm.getValue("CTZ3_CODE");
		double lumpworkRate = infoParm.getDouble("LUMPWORK_RATE");// 套餐折扣
		String lumpworkCode = infoParm.getValue("LUMPWORK_CODE");// 套餐
		String level = infoParm.getValue("SERVICE_LEVEL");// 服务
		String opt_user = infoParm.getValue("OPT_USER");
		String opt_term = infoParm.getValue("OPT_TERM");
		TParm maxCaseNoSeqParm = selMaxCaseNoSeq(infoParm.getValue("CASE_NO"));
		int maxCaseNoSeq = maxCaseNoSeqParm.getInt("CASE_NO_SEQ", 0) + 1;
		if (maxCaseNoSeqParm.getCount("CASE_NO_SEQ") == 0) {
			maxCaseNoSeq = 1;
		}
		TParm queryInfoParm = IBSNewTool.getInstance().onCheckLumWorkCaseNo(
				infoParm.getValue("MR_NO"), infoParm.getValue("CASE_NO"));
		// String mrNoNew=queryInfoParm.getValue("MR_NO");
		String caseNoNew = queryInfoParm.getValue("CASE_NO");
		String querySql = "";
		TParm parmIbsOrdd = null;
		TParm parmIbsOrdm = null;
		int groupNo = -1;
		if (infoParm.getValue("ORDER_CODE").equals(
				infoParm.getValue("ORDERSET_CODE"))
				&& infoParm.getValue("INDV_FLG").equals("N")) {// 集合医嘱
			querySql = "SELECT A.*,B.CAT1_TYPE AS SYS_FEE_CAT1_TYPE,B.CHARGE_HOSP_CODE FROM IBS_ORDD A ,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='"
					+ infoParm.getValue("CASE_NO")
					+ "' AND A.CASE_NO_SEQ='"
					+ infoParm.getValue("CASE_NO_SEQ")
					+ "' AND A.ORDERSET_CODE='"
					+ infoParm.getValue("ORDERSET_CODE")
					+ "' AND A.ORDERSET_GROUP_NO='"
					+ infoParm.getValue("ORDERSET_GROUP_NO") + "'" 
					+" AND A.ORDER_CODE='" 
					+infoParm.getValue("ORDER_CODE")+"'";
			groupNo = selMaxOrderSetGroupNo(infoParm.getValue("CASE_NO"), infoParm
					.getValue("ORDER_CODE"));
		} else {
			querySql = "SELECT A.*, B.CAT1_TYPE AS SYS_FEE_CAT1_TYPE,B.CHARGE_HOSP_CODE FROM IBS_ORDD A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='"
					+ infoParm.getValue("CASE_NO")
					+ "' AND A.CASE_NO_SEQ='"
					+ infoParm.getData("CASE_NO_SEQ")
					+ "' AND A.SEQ_NO='"
					+ infoParm.getData("SEQ_NO") + "'" 
					+" AND A.ORDER_CODE='"+infoParm.getValue("ORDER_CODE")+"'";
		}
		parmIbsOrdd = new TParm(TJDODBTool.getInstance().select(querySql));
		//parmIbsOrdd=parmIbsOrdd.getRow(0);
		querySql = "SELECT * FROM IBS_ORDM WHERE  CASE_NO='"
				+ infoParm.getValue("CASE_NO") + "' AND CASE_NO_SEQ='"
				+ infoParm.getData("CASE_NO_SEQ") + "'";
		parmIbsOrdm = new TParm(TJDODBTool.getInstance().select(querySql));
		result = onSaveIbsOrdmAndOrddParm(infoParm, parmIbsOrdm.getRow(0),
				parmIbsOrdd, infoParm.getValue("CASE_NO"), connection, infoParm
						.getBoolean("INCLUDE_FLG"), groupNo, CTZ1, CTZ2, CTZ3,
				opt_user, opt_term, maxCaseNoSeq, lumpworkRate, lumpworkCode,
				level, caseNoNew);
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
	 * 查询最大账务序号
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm caowl
	 */
	public int selMaxOrderSetGroupNo(String caseNo, String orderCode) {
		String sql = " SELECT MAX(TO_NUMBER(ORDERSET_GROUP_NO)) AS ORDERSET_GROUP_NO FROM IBS_ORDD WHERE CASE_NO = '"
				+ caseNo + "' AND ORDERSET_CODE IS NOT NULL";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() > 0) {
			return result.getInt("ORDERSET_GROUP_NO", 0);
		}
		return -1;
	}

	/**
	 * 查询最大账务序号
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm caowl
	 */
	public TParm selMaxCaseNoSeq(String caseNo) {
		String sql = " SELECT MAX(CASE_NO_SEQ) AS CASE_NO_SEQ FROM IBS_ORDM WHERE CASE_NO = '"
				+ caseNo + "' ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
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
				String sql="UPDATE IBS_ORDD SET INCLUDE_EXEC_FLG='Y' WHERE CASE_NO='"
					+parmIbsOrdd.getData("CASE_NO",m)+"' AND CASE_NO_SEQ='"
					+parmIbsOrdd.getData("CASE_NO_SEQ",m)+"' AND SEQ_NO='"
					+parmIbsOrdd.getData("SEQ_NO",m)+"'"
					+" AND ORDER_CODE='"+parmIbsOrdd.getValue("ORDER_CODE",m)+"'";
				result=new TParm(TJDODBTool.getInstance().update(sql,connection));
				if (result.getErrCode()<0) {
					return result;
				}
				insertIbsOrddNegativeParm = new TParm();		
				insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO",m));					
				//insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",parmIbsOrdd.getData("CASE_NO_SEQ", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO_SEQ", m));						
				insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
				insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
				insertIbsOrddNegativeParm=getIbsOrddParm(insertIbsOrddNegativeParm, parm, parmIbsOrdd,m, sysDate,-1);
				insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",groupNo+1);
				insertIbsOrddNegativeParm.setData("OWN_RATE",parmIbsOrdd.getDouble("OWN_RATE",m));
				insertIbsOrddNegativeParm.setData("TOT_AMT",-parmIbsOrdd.getDouble("TOT_AMT",m));
				insertIbsOrddNegativeParm.setData("INCLUDE_FLG",parmIbsOrdd.getData("INCLUDE_FLG",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INCLUDE_FLG",m));
				++seqNo;
				result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
				if (result.getErrCode() < 0) {
	                err(result.getErrName() + " " + result.getErrText());
	                return result;
		        }
			}
			maxCaseNoSeq++;
				//添加正数据
					seqNo=1;
				TParm insertIbsOrdMPositiveParm=new TParm();
				insertIbsOrdMPositiveParm.setData("CASE_NO",caseNo);
				insertIbsOrdMPositiveParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
				insertIbsOrdMPositiveParm.setData("BILL_DATE",sysDate);
				insertIbsOrdMPositiveParm.setData("IPD_NO",parmIbsOrdm.getData("IPD_NO") == null ? new TNull(String.class) : parmIbsOrdm.getData("IPD_NO"));
				insertIbsOrdMPositiveParm.setData("MR_NO",parmIbsOrdm.getData("MR_NO") == null ? new TNull(String.class) : parmIbsOrdm.getData("MR_NO"));
				insertIbsOrdMPositiveParm.setData("DEPT_CODE",parmIbsOrdm.getData("DEPT_CODE") == null ? new TNull(String.class) : parmIbsOrdm.getData("DEPT_CODE"));
				insertIbsOrdMPositiveParm.setData("STATION_CODE",parmIbsOrdm.getData("STATION_CODE") == null ? new TNull(String.class) : parmIbsOrdm.getData("STATION_CODE"));
				insertIbsOrdMPositiveParm.setData("BED_NO",parmIbsOrdm.getData("BED_NO") == null ? new TNull(String.class) : parmIbsOrdm.getData("BED_NO"));
				insertIbsOrdMPositiveParm.setData("DATA_TYPE",parmIbsOrdm.getData("DATA_TYPE") == null ? new TNull(String.class) : parmIbsOrdm.getData("DATA_TYPE"));
				insertIbsOrdMPositiveParm.setData("BILL_NO","");
				insertIbsOrdMPositiveParm.setData("OPT_USER",opt_user);
				insertIbsOrdMPositiveParm.setData("OPT_TERM",opt_term);
				insertIbsOrdMPositiveParm.setData("REGION_CODE",parmIbsOrdm.getData("REGION_CODE") == null ? new TNull(String.class) : parmIbsOrdm.getData("REGION_CODE"));
				insertIbsOrdMPositiveParm.setData("COST_CENTER_CODE",parmIbsOrdm.getData("COST_CENTER_CODE") == null ? new TNull(String.class) : parmIbsOrdm.getData("COST_CENTER_CODE"));

				result = IBSOrdmTool.getInstance().insertdataM(
						insertIbsOrdMPositiveParm, connection);
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					return result;
				}
				TParm parmIbsOrddNew=null;
			 if (parm.getValue("ORDER_CODE").equals(parm.getValue("ORDERSET_CODE"))
						&&parm.getValue("INDV_FLG").equals("N")) {//集合医嘱
		        		//重新获得折扣后的金额
			        	String querySql = "SELECT A.ORDER_CODE,B.ORDER_CAT1_CODE,B.CAT1_TYPE,A.ORDERSET_CODE,B.CHARGE_HOSP_CODE HEXP_CODE," +
			        			"A.ORDER_NUM MEDI_QTY,A.UNIT_CODE AS MEDI_UNIT,'' DOSE_CODE,A.ORDER_NUM DOSAGE_QTY,A.UNIT_CODE DOSAGE_UNIT," +
			        			"C.IPD_CHARGE_CODE REXP_CODE,A.ORDER_DESC ORDER_CHN_DESC,A.UNIT_PRICE AS OWN_PRICE " +
			        			"FROM MEM_PAT_PACKAGE_SECTION_D A ,SYS_FEE B,SYS_CHARGE_HOSP C " +
			        			"WHERE A.ORDER_CODE=B.ORDER_CODE AND B.CHARGE_HOSP_CODE=C.CHARGE_HOSP_CODE  AND A.CASE_NO='" +caseNoNew
						+ "' AND A.PACKAGE_CODE = '"+lumpworkCode+"' AND A.ORDERSET_CODE='"+parm.getValue("ORDER_CODE")+"'";
			        	parmIbsOrddNew = new TParm(TJDODBTool.getInstance().select(querySql));
			        	if(parmIbsOrddNew.getCount()>0){
			        		++seqNo;
			        		result=onExeLumpWorkCharge(parmIbsOrddNew, parm, seqNo, 
			        				caseNo, groupNo, sysDate, parmIbsOrdd, maxCaseNoSeq, 
			        				CTZ1, CTZ2, CTZ3, bil, lumpworkRate, connection,false,level);
			        	}else{
							 ++seqNo;
			        		result=lumpWorkChangeComm(parm, seqNo, caseNo, groupNo, sysDate, 
			        				parmIbsOrdd, maxCaseNoSeq, CTZ1, CTZ2, CTZ3, bil, lumpworkRate, connection,false,level);
			        	}  
			        	if (result.getErrCode() < 0) {
		    	            err(result.getErrName() + " " + result.getErrText());
		    	            return result;
		    	        }
					//套外
						 seqNo=result.getInt("SEQ_NO");
						result = lumpWorkChangeComm(parm,++seqNo, caseNo, groupNo,
								sysDate, parmIbsOrdd, maxCaseNoSeq, CTZ1, CTZ2,
								CTZ3, bil, lumpworkRate, connection,true,level);
						if (result.getErrCode() < 0) {
							err(result.getErrName() + " " + result.getErrText());
							return result;
					}
				 
				 
				 
				 
				 
			 }else{//非集合医嘱
	    	        TParm parmN = new TParm();		
	    	        parmN.setData("CASE_NO",caseNo);					
	    	        parmN.setData("CASE_NO_SEQ",maxCaseNoSeq);	
	    	        parmN.setData("SEQ_NO",seqNo);
	    	        double ownRate =0.00;
	    			double ownPrice=0.00;
	    			double [] sumPrice = new double[2];
	    	        for(int m=0;m<parmIbsOrdd.getCount();m++){
	    		     parmN=getIbsOrddParm(parmN, parm, parmIbsOrdd,m, sysDate,1);
	    		     parmN.setData("ORDERSET_GROUP_NO",groupNo+2);
	    			 ownRate =0.00;
	    			 ownPrice=0.00;
	    			  sumPrice = new double[2];
	    		
	    				//药品、血费根据身份统计
	    				if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m)
	    						&&parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m).equals("PHA")||
	    						null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m)&&parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m).equals("RA")){
	    					ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE",m) ,
		    						parmIbsOrdd.getValue("ORDER_CODE",m));
	    					sumPrice=IBSTool.getInstance().getOrderOwnPrice(parmIbsOrdd.getValue("ORDER_CODE",m), level);
	    					ownPrice=sumPrice[0];
	    				}else{
	    					ownRate =lumpworkRate;
	    					ownPrice=IBSTool.getInstance().getLumpOrderOwnPrice(caseNoNew, lumpworkCode, 
	    							parmIbsOrdd.getValue("ORDER_CODE",m), level);
	    				}
	    				parmN.setData("OWN_RATE",ownRate);
	    				parmN.setData("INCLUDE_FLG","N");
	    				parmN.setData("OWN_PRICE",ownPrice);
	    				parmN.setData("DOSAGE_QTY",tableNparm.getDouble("DOSAGE_QTY",0));
		    			parmIbsOrdd.setData("OWN_PRICE",m,ownPrice);
		    			parmIbsOrdd.setData("OWN_AMT",m,ownPrice*
		    					tableNparm.getDouble("DOSAGE_QTY",0));
		    			parmN.setData("TOT_AMT",parmIbsOrdd.getDouble("OWN_AMT",m)*ownRate);
		    			++seqNo;
		    			result =IBSOrdmTool.getInstance().insertdataLumpworkD(parmN,connection);
		    			if (result.getErrCode() < 0) {
		    	            err(result.getErrName() + " " + result.getErrText());
		    	            return result;
		    	        }
	    	        }
		    			 TParm parmY = new TParm();		
		    			 parmY.setData("CASE_NO",caseNo);					
		    			 parmY.setData("CASE_NO_SEQ",maxCaseNoSeq);	
		    			 parmY.setData("SEQ_NO",seqNo);
		    			 for(int m=0;m<parmIbsOrdd.getCount();m++){
		    				 ownRate =0.00;
			    			 ownPrice=0.00;
			    			  sumPrice = new double[2];
		    			 
		    			 parmY=getIbsOrddParm(parmY, parm, parmIbsOrdd,m, sysDate,1);
			    		     parmY.setData("ORDERSET_GROUP_NO",groupNo+3);
	    				sumPrice=IBSTool.getInstance().getOrderOwnPrice(parmIbsOrdd.getValue("ORDER_CODE",m), level);
    					ownPrice=sumPrice[0];
	    				ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE",m) ,
	    						parmIbsOrdd.getValue("ORDER_CODE",m));//包干外根据身份计算
	    				parmY.setData("OWN_RATE",ownRate);
	    				parmY.setData("INCLUDE_FLG","Y");
	    				parmY.setData("OWN_PRICE",ownPrice);
	    				parmY.setData("DOSAGE_QTY",tableYparm.getDouble("DOSAGE_QTY",0));
		    			parmIbsOrdd.setData("OWN_PRICE",m,ownPrice);
		    			parmIbsOrdd.setData("OWN_AMT",m,ownPrice*
		    					tableYparm.getDouble("DOSAGE_QTY",0));
		    			parmY.setData("TOT_AMT",parmIbsOrdd.getDouble("OWN_AMT",m)*ownRate);
	    			result =IBSOrdmTool.getInstance().insertdataLumpworkD(parmY,connection);
	    			if (result.getErrCode() < 0) {
	    	            err(result.getErrName() + " " + result.getErrText());
	    	            return result;
	    	        }
	    			}
			 }
	    	        
			 }
        result.setData("CASE_NO_SEQ",maxCaseNoSeq+1);
		return result;
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
	private TParm getIbsOrddParm(TParm insertIbsOrddNegativeParm,TParm parm,
			TParm parmIbsOrdd,int m,Timestamp sysDate,int mas){
		insertIbsOrddNegativeParm.setData("BILL_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("EXEC_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("ORDER_NO",parmIbsOrdd.getData("ORDER_NO",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_NO",m));
		insertIbsOrddNegativeParm.setData("ORDER_SEQ",parmIbsOrdd.getData("ORDER_SEQ",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_SEQ",m));
		insertIbsOrddNegativeParm.setData("ORDER_CODE",parmIbsOrdd.getData("ORDER_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CODE",m));
		insertIbsOrddNegativeParm.setData("ORDER_CAT1_CODE",parmIbsOrdd.getData("ORDER_CAT1_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CAT1_CODE",m));
		insertIbsOrddNegativeParm.setData("CAT1_TYPE",parmIbsOrdd.getData("CAT1_TYPE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CAT1_TYPE",m));
		insertIbsOrddNegativeParm.setData("ORDERSET_CODE",parmIbsOrdd.getData("ORDERSET_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_CODE",m));
		insertIbsOrddNegativeParm.setData("INDV_FLG",parmIbsOrdd.getData("INDV_FLG",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INDV_FLG",m));
		insertIbsOrddNegativeParm.setData("DEPT_CODE",parmIbsOrdd.getData("DEPT_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DEPT_CODE",m));
		insertIbsOrddNegativeParm.setData("STATION_CODE",parmIbsOrdd.getData("STATION_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("STATION_CODE",m));
		insertIbsOrddNegativeParm.setData("DR_CODE",parmIbsOrdd.getData("DR_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DR_CODE",m));
		insertIbsOrddNegativeParm.setData("EXE_DEPT_CODE",parmIbsOrdd.getData("EXE_DEPT_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DEPT_CODE",m));
		insertIbsOrddNegativeParm.setData("EXE_STATION_CODE",parmIbsOrdd.getData("EXE_STATION_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_STATION_CODE",m));
		insertIbsOrddNegativeParm.setData("EXE_DR_CODE",parmIbsOrdd.getData("EXE_DR_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DR_CODE",m));
		insertIbsOrddNegativeParm.setData("MEDI_QTY",parmIbsOrdd.getDouble("MEDI_QTY",m)*mas);
		insertIbsOrddNegativeParm.setData("MEDI_UNIT",parmIbsOrdd.getData("MEDI_UNIT",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("MEDI_UNIT",m));
		insertIbsOrddNegativeParm.setData("DOSE_CODE",parmIbsOrdd.getData("DOSE_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSE_CODE",m));
		insertIbsOrddNegativeParm.setData("FREQ_CODE",parmIbsOrdd.getData("FREQ_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("FREQ_CODE",m));
		insertIbsOrddNegativeParm.setData("TAKE_DAYS",parmIbsOrdd.getData("TAKE_DAYS",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("TAKE_DAYS",m));
		insertIbsOrddNegativeParm.setData("DOSAGE_QTY",parmIbsOrdd.getDouble("DOSAGE_QTY",m)*mas);
		insertIbsOrddNegativeParm.setData("DOSAGE_UNIT",parmIbsOrdd.getData("DOSAGE_UNIT",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSAGE_UNIT",m));
		insertIbsOrddNegativeParm.setData("OWN_PRICE",parmIbsOrdd.getDouble("OWN_PRICE",m));
		insertIbsOrddNegativeParm.setData("NHI_PRICE",parmIbsOrdd.getDouble("NHI_PRICE",m));
		insertIbsOrddNegativeParm.setData("OWN_FLG",parmIbsOrdd.getData("OWN_FLG",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("OWN_FLG",m));
		insertIbsOrddNegativeParm.setData("BILL_FLG",parmIbsOrdd.getData("BILL_FLG",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("BILL_FLG",m));
		insertIbsOrddNegativeParm.setData("REXP_CODE",parmIbsOrdd.getData("REXP_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REXP_CODE",m));
		insertIbsOrddNegativeParm.setData("BILL_NO","");
		insertIbsOrddNegativeParm.setData("HEXP_CODE",parmIbsOrdd.getData("HEXP_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("HEXP_CODE",m));
		insertIbsOrddNegativeParm.setData("BEGIN_DATE",parmIbsOrdd.getData("BEGIN_DATE",m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("BEGIN_DATE",m));
		insertIbsOrddNegativeParm.setData("END_DATE",parmIbsOrdd.getData("END_DATE",m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("END_DATE",m));
		insertIbsOrddNegativeParm.setData("OWN_AMT",parmIbsOrdd.getDouble("OWN_AMT",m)*mas);
		insertIbsOrddNegativeParm.setData("REQUEST_FLG",parmIbsOrdd.getData("REQUEST_FLG",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_FLG",m));
		insertIbsOrddNegativeParm.setData("REQUEST_NO",parmIbsOrdd.getData("REQUEST_NO",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_NO",m));
		insertIbsOrddNegativeParm.setData("INV_CODE",parmIbsOrdd.getData("INV_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INV_CODE",m));
		insertIbsOrddNegativeParm.setData("OPT_USER",parm.getValue("OPT_USER"));
		insertIbsOrddNegativeParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
		insertIbsOrddNegativeParm.setData("COST_AMT",parmIbsOrdd.getDouble("COST_AMT",m)*mas);
		insertIbsOrddNegativeParm.setData("ORDER_CHN_DESC",parmIbsOrdd.getData("ORDER_CHN_DESC",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CHN_DESC",m));
		insertIbsOrddNegativeParm.setData("COST_CENTER_CODE",parmIbsOrdd.getData("COST_CENTER_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("COST_CENTER_CODE",m));
		insertIbsOrddNegativeParm.setData("SCHD_CODE",parmIbsOrdd.getData("SCHD_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("SCHD_CODE",m));
		insertIbsOrddNegativeParm.setData("CLNCPATH_CODE",parmIbsOrdd.getData("CLNCPATH_CODE",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CLNCPATH_CODE",m));
		insertIbsOrddNegativeParm.setData("DS_FLG",parmIbsOrdd.getData("DS_FLG",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DS_FLG",m));
		insertIbsOrddNegativeParm.setData("KN_FLG",parmIbsOrdd.getData("KN_FLG",m) == null ? new TNull(String.class) : parmIbsOrdd.getData("KN_FLG",m));
		insertIbsOrddNegativeParm.setData("INCLUDE_EXEC_FLG","Y");
		return insertIbsOrddNegativeParm;
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
		TParm parmY=null;
		TParm result=new TParm();
		double ownRate =0.00;
		double ownPrice=0.00;
		for (int j = 0; j < parmIbsOrddNew.getCount(); j++) {
			if(flg){
			parmY = new TParm();
	        parmY.setData("ORDERSET_GROUP_NO",groupNo+2);
	        parmY.setData("CASE_NO",caseNo);
	        parmY.setData("CASE_NO_SEQ",maxCaseNoSeq);
	        parmY.setData("SEQ_NO",seqNo);
			parmY=getIbsOrddParm(parmY, parm, parmIbsOrddNew, j, sysDate,parmIbsOrdd);
			
			//套餐外
				if ("2".equals(level)) {
                    ownPrice = parmIbsOrddNew.getDouble("OWN_PRICE2", j);
                } else if ("3".equals(level)) {
                    ownPrice = parmIbsOrddNew.getDouble("OWN_PRICE3", j);
                } else{
                    ownPrice = parmIbsOrddNew.getDouble("OWN_PRICE", j);
                }
				ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrddNew.getValue("HEXP_CODE", j) ,
						 parmIbsOrddNew.getValue("ORDER_CODE", j));	
				if (parmIbsOrddNew.getValue("ORDER_CODE",j).equals(parmIbsOrddNew.getValue("ORDERSET_CODE",j))) {
					parmY.setData("OWN_PRICE",0);
					parmY.setData("NHI_PRICE",0);
					parmY.setData("OWN_AMT",0);
					parmY.setData("TOT_AMT",0);
					parmY.setData("COST_AMT",0);
					parmY.setData("DOSAGE_QTY",tableYparm.getDouble("DOSAGE_QTY",0));
				}else{
					parmIbsOrddNew.setData("OWN_AMT",j,ownPrice*
		   					parmIbsOrddNew.getDouble("DOSAGE_QTY",j)*tableYparm.getDouble("DOSAGE_QTY",0));
					parmY.setData("DOSAGE_QTY",parmIbsOrddNew.getDouble("DOSAGE_QTY",j)*tableYparm.getDouble("DOSAGE_QTY",0));
					parmY.setData("OWN_PRICE",ownPrice);
					parmY.setData("OWN_AMT",ownPrice*
		   					parmIbsOrddNew.getDouble("DOSAGE_QTY",j)*tableYparm.getDouble("DOSAGE_QTY",0));
					parmY.setData("TOT_AMT",ownPrice*
		   					parmIbsOrddNew.getDouble("DOSAGE_QTY",j)*tableYparm.getDouble("DOSAGE_QTY",0)*ownRate);
					}
				parmY.setData("OWN_RATE",ownRate);
				parmY.setData("INCLUDE_FLG","Y");
				result =IBSOrdmTool.getInstance().insertdataLumpworkD(parmY,connection);
				if (result.getErrCode() < 0) {
		            err(result.getErrName() + " " + result.getErrText());
		            return result;
		        }
				}else{
				//++maxCaseNoSeq;
			TParm parmN = new TParm();
			  	parmN.setData("ORDERSET_GROUP_NO",groupNo+2);
		        parmN.setData("CASE_NO",caseNo);
		        parmN.setData("CASE_NO_SEQ",maxCaseNoSeq);		
		        parmN.setData("SEQ_NO",seqNo);
				parmN=getIbsOrddParm(parmN, parm, parmIbsOrddNew, j, sysDate,parmIbsOrdd);
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
				if (parmIbsOrddNew.getValue("ORDER_CODE",j).equals(parmIbsOrddNew.getValue("ORDERSET_CODE",j))) {
					parmN.setData("OWN_PRICE",0);
					parmN.setData("NHI_PRICE",0);
					parmN.setData("OWN_AMT",0);
					parmN.setData("TOT_AMT",0);
					parmN.setData("COST_AMT",0);
					parmN.setData("DOSAGE_QTY",tableNparm.getDouble("DOSAGE_QTY",0));
				}else{
					parmIbsOrddNew.setData("OWN_AMT",j,ownPrice*
			   					parmIbsOrddNew.getDouble("DOSAGE_QTY",j)*tableNparm.getDouble("DOSAGE_QTY",0));
					parmN.setData("OWN_PRICE",ownPrice);
					parmN.setData("DOSAGE_QTY",parmIbsOrddNew.getDouble("DOSAGE_QTY",j)*tableNparm.getDouble("DOSAGE_QTY",0));
					parmN.setData("OWN_AMT",ownPrice*
			   					parmIbsOrddNew.getDouble("DOSAGE_QTY",j)*tableNparm.getDouble("DOSAGE_QTY",0));
					parmN.setData("TOT_AMT",ownPrice*
			   					parmIbsOrddNew.getDouble("DOSAGE_QTY",j)*tableNparm.getDouble("DOSAGE_QTY",0)*ownRate);
					
					}
				parmN.setData("OWN_RATE",ownRate);
				parmN.setData("INCLUDE_FLG","N");
				result =IBSOrdmTool.getInstance().insertdataLumpworkD(parmN,connection);
				if (result.getErrCode() < 0) {
		            err(result.getErrName() + " " + result.getErrText());
		            return result;
		        }
				}
			++seqNo;
			result.setData("SEQ_NO",seqNo);
		}
		return result;
	}
	
	
	private TParm lumpWorkChangeComm(TParm parm,int seqNo,String caseNo,
		int groupNo,Timestamp sysDate,TParm parmIbsOrdd,int maxCaseNoSeq,String CTZ1,
		String CTZ2,String CTZ3,BIL bil,double lumpworkRate,TConnection connection,boolean flg,String level){
		//TParm insertIbsOrddNegativeParm=null;
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
	private TParm getIbsOrddParm(TParm insertIbsOrddNegativeParm,TParm parm,
			TParm parmIbsOrdd,int m,Timestamp sysDate,TParm parmIbsOrddOld){
		insertIbsOrddNegativeParm.setData("BILL_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("EXEC_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("ORDER_NO",parmIbsOrddOld.getValue("ORDER_NO",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("ORDER_NO",0));
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
		insertIbsOrddNegativeParm.setData("DEPT_CODE",parmIbsOrddOld.getValue("DEPT_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("DEPT_CODE",0));
		insertIbsOrddNegativeParm.setData("STATION_CODE",parmIbsOrddOld.getValue("STATION_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("STATION_CODE",0));
		insertIbsOrddNegativeParm.setData("DR_CODE",parmIbsOrddOld.getValue("DR_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("DR_CODE",0));
		insertIbsOrddNegativeParm.setData("EXE_DEPT_CODE",parmIbsOrddOld.getData("EXE_DEPT_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("EXE_DEPT_CODE",0));
		insertIbsOrddNegativeParm.setData("EXE_STATION_CODE",parmIbsOrddOld.getData("EXE_STATION_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("EXE_STATION_CODE",0));
		insertIbsOrddNegativeParm.setData("EXE_DR_CODE",parmIbsOrddOld.getData("EXE_DR_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("EXE_DR_CODE",0));
		insertIbsOrddNegativeParm.setData("MEDI_QTY",parmIbsOrdd.getDouble("MEDI_QTY",m)*parm.getDouble("DOSAGE_QTY"));
		insertIbsOrddNegativeParm.setData("MEDI_UNIT",parmIbsOrdd.getData("MEDI_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("MEDI_UNIT", m));
		insertIbsOrddNegativeParm.setData("DOSE_CODE",parmIbsOrdd.getData("DOSE_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSE_CODE", m));
		insertIbsOrddNegativeParm.setData("FREQ_CODE",parmIbsOrddOld.getData("FREQ_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("FREQ_CODE",0));
		insertIbsOrddNegativeParm.setData("TAKE_DAYS","1");
		insertIbsOrddNegativeParm.setData("DOSAGE_QTY",parmIbsOrdd.getDouble("DOSAGE_QTY",m)*parm.getDouble("DOSAGE_QTY"));
		insertIbsOrddNegativeParm.setData("DOSAGE_UNIT",parmIbsOrdd.getData("DOSAGE_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSAGE_UNIT", m));
		insertIbsOrddNegativeParm.setData("NHI_PRICE",0);
		insertIbsOrddNegativeParm.setData("OWN_FLG","Y");
		insertIbsOrddNegativeParm.setData("BILL_FLG","Y");
		insertIbsOrddNegativeParm.setData("REXP_CODE",parmIbsOrdd.getData("REXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REXP_CODE", m));
		insertIbsOrddNegativeParm.setData("BILL_NO","");
		insertIbsOrddNegativeParm.setData("HEXP_CODE",parmIbsOrdd.getData("HEXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("HEXP_CODE", m));
		insertIbsOrddNegativeParm.setData("BEGIN_DATE",parmIbsOrddOld.getData("BEGIN_DATE",0) == null ? new TNull(Timestamp.class) : parmIbsOrddOld.getData("BEGIN_DATE",0));
		insertIbsOrddNegativeParm.setData("END_DATE",parmIbsOrddOld.getData("END_DATE",0) == null ? new TNull(Timestamp.class) : parmIbsOrddOld.getData("END_DATE",0));
		insertIbsOrddNegativeParm.setData("OWN_AMT",parmIbsOrdd.getDouble("OWN_AMT",m));
		insertIbsOrddNegativeParm.setData("REQUEST_FLG",parmIbsOrddOld.getData("REQUEST_FLG",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("REQUEST_FLG",0));
		insertIbsOrddNegativeParm.setData("REQUEST_NO",parmIbsOrddOld.getData("REQUEST_NO",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("REQUEST_NO",0));
		insertIbsOrddNegativeParm.setData("INV_CODE","");
		insertIbsOrddNegativeParm.setData("OPT_USER",parm.getValue("OPT_USER"));
		insertIbsOrddNegativeParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
		insertIbsOrddNegativeParm.setData("COST_AMT",0);
		insertIbsOrddNegativeParm.setData("ORDER_CHN_DESC",parmIbsOrdd.getData("ORDER_CHN_DESC", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CHN_DESC", m));
		insertIbsOrddNegativeParm.setData("COST_CENTER_CODE",parmIbsOrddOld.getData("COST_CENTER_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("COST_CENTER_CODE",0));
		insertIbsOrddNegativeParm.setData("SCHD_CODE",parmIbsOrddOld.getData("SCHD_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("SCHD_CODE",0));
		insertIbsOrddNegativeParm.setData("CLNCPATH_CODE",parmIbsOrddOld.getData("CLNCPATH_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("CLNCPATH_CODE",0));
		insertIbsOrddNegativeParm.setData("DS_FLG","N");
		insertIbsOrddNegativeParm.setData("KN_FLG",parmIbsOrddOld.getData("KN_FLG",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("KN_FLG",0));
		insertIbsOrddNegativeParm.setData("INCLUDE_EXEC_FLG","Y");
		return insertIbsOrddNegativeParm;
	}
}
