package action.bil;

import jdo.bil.BILFinanceTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
/**
*
* <p>Title: 财务收入报表</p>
*
* <p><b>Description:</b>
* <br>给用友发送数据
* </p>
*
* <p>Copyright: </p>
*
* <p>Company: bluecore </p>
*
* @alias财务收入报表
* @author design: pangben 2014-4-22
* <br> coding:
* @version 4.0
*/
public class BILFinanceAction extends TAction {
	
	/**
	 * 药品入库数据
	 * pangben 2016-6-16
	 * @param parm
	 * @return
	 */
	public TParm onSaveDidrugs(TParm parm){
		TConnection connection=this.getConnection("javahisHRP");
		TParm parmValue=new TParm();
		TParm result=null;
		TParm logParm=parm.getParm("LOG_PARM");//日志表数据
		if (null!=parm.getValue("CHECK_FLG") &&parm.getValue("CHECK_FLG").equals("Y")) {//查询是否有数据选择确认动作
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			parmValue.setData("EXPTABLENAME",logParm.getValue("EXPTABLENAME"));
			parmValue.setData("BUSITYPENAME",parm.getValue("BUSITYPENAME_SUM"));//外部系统
			parmValue.setData("SOURCESYSTEM",parm.getValue("BUSITYPENAME_SUM"));
			result=BILFinanceTool.getInstance().deleteDidrugsByBatchname(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
			parmValue.setData("SOURCESYSTEM",logParm.getValue("SOURCESYSTEM"));//外部系统
			result=BILFinanceTool.getInstance().deleteDiExpLog(parmValue, connection);//删除日志表里的药品入库数据
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		for (int i = 0; i < parm.getCount(); i++) {
			parmValue=parm.getRow(i);
			//新增成本数据
			result=BILFinanceTool.getInstance().insertDidrugs(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		//新增log表
		
		//logParm.setData("BUSITYPENAME",parm.getValue("BUSITYPENAME_SUM"));
		result=BILFinanceTool.getInstance().insertDiExpLog(logParm, connection);
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * 成本核算
	 * @param parm
	 * @return
	 */
	public TParm onsaveDiIncomeCostAcount(TParm parm){
		TConnection connection=this.getConnection("javahisHRP");
		
		TParm parmValue=new TParm();
		TParm result=null;
		TParm logParm=parm.getParm("LOG_PARM");//日志表数据
		if (null!=parm.getValue("CHECK_FLG") &&parm.getValue("CHECK_FLG").equals("Y")) {//查询是否有数据选择确认动作
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			parmValue.setData("EXPTABLENAME",logParm.getValue("EXPTABLENAME"));
			parmValue.setData("SOURCESYSTEM",logParm.getValue("SOURCESYSTEM"));//外部系统
			result=BILFinanceTool.getInstance().deleteDiIncomeCostAcountByBatchname(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
			result=BILFinanceTool.getInstance().deleteDiExpLog(parmValue, connection);//删除日志表里的成本数据
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		
		for (int i = 0; i < parm.getCount(); i++) {
			parmValue=parm.getRow(i);
			//新增成本数据
			result=BILFinanceTool.getInstance().insertDiIncomeCostAcount(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		//新增log表
		
		//logParm.setData("BUSITYPENAME",parm.getValue("BUSITYPENAME_SUM"));
		result=BILFinanceTool.getInstance().insertDiExpLog(logParm, connection);
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		if(!parm.getBoolean("LOCAL_LOG_FLG")){
			//插入本地数据库
			TConnection localConnection = this.getConnection();
			for (int i = 0; i < parm.getCount(); i++) {
				parmValue=parm.getRow(i);
				if(!"02".equals(parmValue.getValue("PK_ITEM"))){//床日数据插入日志表
					continue;
				}
				result = new TParm(TJDODBTool.getInstance().update(getInsertDiIncomeCostSql(parmValue), localConnection));
				if(result.getErrCode() < 0){
					localConnection.rollback();
					localConnection.close();
					connection.rollback();
					connection.close();
					return result;
				}
			}
			localConnection.commit();
			localConnection.close();
		}
		connection.commit();
		connection.close();
		return result;
	}
	
	public String getInsertDiIncomeCostSql(TParm parm){
		String sql = "INSERT INTO DI_SERVICEVOLEXP_LOG (PK_SERVICEVOLEXP,PK_EXPLOG,BATCHNAME,"+
			" BUSITYPENAME,EXPTIME,CYEAR,CMONTH,CDAY,PK_CORP,CORPNAME,"+
			" PK_ITEM,ITEMNAME,VOLUME,PK_DIAGNOSISDEPT,DIAGNOSISDEPTNAME,"+
			" PK_OUTERSYSTEM,SOURCESYSTEM,TYPE) "+
			" VALUES( '"+parm.getValue("PK_SERVICEVOLEXP")+"','"+parm.getValue("PK_EXPLOG")+"'," +
					" '"+parm.getValue("BATCHNAME")+"'," +
					" '"+parm.getValue("BUSITYPENAME")+"',"+
					" '"+parm.getValue("EXPTIME")+"',"+
					" '"+parm.getValue("CYEAR")+"',"+
					" '"+parm.getValue("CMONTH")+"',"+
					" '"+parm.getValue("CDAY")+"',"+
					" '"+parm.getValue("PK_CORP")+"',"+
					" '"+parm.getValue("CORPNAME")+"',"+
					" '"+parm.getValue("PK_ITEM")+"',"+
					" '"+parm.getValue("ITEMNAME")+"',"+
					" '"+parm.getValue("VOLUME")+"',"+
					" '"+parm.getValue("PK_DIAGNOSISDEPT")+"',"+
					" '"+parm.getValue("DIAGNOSISDEPTNAME")+"',"+
					" '"+parm.getValue("PK_OUTERSYSTEM")+"',"+
					" '"+parm.getValue("SOURCESYSTEM")+"',"+
					" '"+parm.getValue("TYPE")+"' )";
		System.out.println("sql：：：：：：：：："+sql);
		
		return sql;
	}
	
	/**
	 * 新增应收数据
	 * @param parm
	 * @return
	 */
	public TParm onSaveDiIncomeexp(TParm parm){
		//TConnection connection = getConnection();
		TConnection connection=this.getConnection("javahisHRP");
		TParm parmValue=new TParm();
		TParm result=null;
		TParm logParm=parm.getParm("LOG_PARM");//日志表数据
		if (null!=parm.getValue("CHECK_FLG") &&parm.getValue("CHECK_FLG").equals("Y")) {//查询是否有数据选择确认动作
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			parmValue.setData("EXPTABLENAME",logParm.getValue("EXPTABLENAME"));
			parmValue.setData("BUSITYPENAME",parm.getValue("BUSITYPENAME_SUM"));
			parmValue.setData("SOURCESYSTEM",logParm.getValue("SOURCESYSTEM"));//外部系统
			result=BILFinanceTool.getInstance().deleteDiIncomeexpByBatchname(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
			result=BILFinanceTool.getInstance().deleteDiExpLog(parmValue, connection);//删除日志表里的应收数据
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		for (int i = 0; i < parm.getCount(); i++) {
			parmValue=parm.getRow(i);
			if (i!=0&&i%1000==0) {
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					
					// TODO: handle exception
				}
			}
			//新增应收数据
			result=BILFinanceTool.getInstance().insertDiIncomeexp(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		result=BILFinanceTool.getInstance().insertDiExpLog(logParm, connection);
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		String sql="";
		if (parm.getValue("BUSITYPENAME_SUM").equals("门诊")) {
			//修改本地数据状态
			sql="UPDATE OPD_ORDER SET BIL_FINANCE_FLG='Y' WHERE EXEC_FLG='Y' AND EXEC_DATE BETWEEN TO_DATE("+parm.getValue("START_DATE")
			+",'YYYYMMDDHH24MISS') AND TO_DATE("+parm.getValue("END_DATE")+",'YYYYMMDDHH24MISS') ";
			result = new TParm(TJDODBTool.getInstance().update(sql));
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
			sql="UPDATE OPD_ORDER_HISTORY SET BIL_FINANCE_FLG='Y' WHERE RECEIPT_NO IS NOT NULL AND OPT_DATE BETWEEN TO_DATE("+parm.getValue("START_DATE")
			+",'YYYYMMDDHH24MISS') AND TO_DATE("+parm.getValue("END_DATE")+",'YYYYMMDDHH24MISS') ";
		}else{
			sql="UPDATE IBS_ORDD SET BIL_FINANCE_FLG='Y' WHERE BILL_NO IN (SELECT A.BILL_NO FROM IBS_BILLM A,IBS_BILLD B WHERE A.BILL_NO=B.BILL_NO AND A.BILL_SEQ=B.BILL_SEQ  AND A.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+
		"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS'))";
		}
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * 新增实收数据
	 * @param parm
	 * @return
	 */
	public TParm onSaveDiIncomereal(TParm parm){
		TConnection connection = getConnection("javahisHRP");
		TParm parmValue=new TParm();
		TParm result=null;
		TParm logParm=parm.getParm("LOG_PARM");//日志表数据
		if (null!=parm.getValue("CHECK_FLG")&&parm.getValue("CHECK_FLG").equals("Y")) {//查询是否有数据选择确认动作
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			parmValue.setData("EXPTABLENAME",logParm.getValue("EXPTABLENAME"));
			parmValue.setData("BUSITYPENAME",parm.getValue("BUSITYPENAME_SUM"));
			parmValue.setData("SOURCESYSTEM",logParm.getValue("SOURCESYSTEM"));//外部系统
			result=BILFinanceTool.getInstance().deleteDiIncomerealByBatchname(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
			result=BILFinanceTool.getInstance().deleteDiExpLog(parmValue, connection);//删除日志表里的实收数据
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		for (int i = 0; i < parm.getCount(); i++) {
			parmValue=parm.getRow(i);
//			if (i!=0&&i%1000==0) {
//				try {
//					Thread.sleep(5000);
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//			}
			//新增实收数据
			result=BILFinanceTool.getInstance().insertDiIncomereal(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		result=BILFinanceTool.getInstance().insertDiExpLog(logParm, connection);
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		String sql="";
		if (parm.getValue("BUSITYPENAME_SUM").equals("门诊")) {
			//修改本地数据状态
			sql="UPDATE BIL_OPB_RECP SET BIL_FINANCE_FLG='Y' WHERE ACCOUNT_SEQ IS NOT NULL  AND ACCOUNT_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"
			+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')";
			// add by kangy 上传门诊数据更新BIL_MEM_RECP 表数据状态
			String memsql="UPDATE BIL_MEM_RECP SET BIL_FINANCE_FLG='Y' WHERE ACCOUNT_SEQ IS NOT NULL  AND ACCOUNT_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"
			+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')";
			result = new TParm(TJDODBTool.getInstance().update(memsql));
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}else{
			sql="UPDATE BIL_IBS_RECPM SET BIL_FINANCE_FLG='Y' WHERE ACCOUNT_SEQ IS NOT NULL AND CHARGE_DATE BETWEEN TO_DATE('"
				+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')";
		}
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * 新增预交金数据
	 * @param parm
	 * @return
	 */
	public TParm onSaveDiIncomepre(TParm parm){
		TConnection connection = getConnection("javahisHRP");
		TParm parmValue=new TParm();
		TParm result=null;
		TParm logParm=parm.getParm("LOG_PARM");//日志表数据
		if (null!=parm.getValue("CHECK_FLG")&&parm.getValue("CHECK_FLG").equals("Y")) {//查询是否有数据选择确认动作
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			parmValue.setData("EXPTABLENAME",logParm.getValue("EXPTABLENAME"));
			parmValue.setData("BUSITYPENAME",parm.getValue("BUSITYPENAME_SUM"));
			parmValue.setData("SOURCESYSTEM",logParm.getValue("SOURCESYSTEM"));//外部系统
			if(parm.getValue("BUSITYPENAME_SUM").equals("门诊")){
				result=BILFinanceTool.getInstance().deleteDiIncomepreByBatchnameO(parmValue, connection);
			}else{
				result=BILFinanceTool.getInstance().deleteDiIncomepreByBatchname(parmValue, connection);
			}
			
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
			result=BILFinanceTool.getInstance().deleteDiExpLog(parmValue, connection);//删除日志表里的成本数据
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		for (int i = 0; i < parm.getCount(); i++) {
			parmValue=parm.getRow(i);
			//新增预交金数据
			result=BILFinanceTool.getInstance().insertDiIncomepre(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		result=BILFinanceTool.getInstance().insertDiExpLog(logParm, connection);
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		String sql="";
		if (parm.getValue("BUSITYPENAME_SUM").equals("门诊")) {
			//修改本地数据状态
			sql="UPDATE EKT_BIL_PAY SET BIL_FINANCE_FLG='Y' WHERE ACCNT_TYPE IN('4','6') AND STORE_DATE BETWEEN "+                           
			" TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') ";
			result = new TParm(TJDODBTool.getInstance().update(sql));
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
			sql="UPDATE MEM_TRADE SET BIL_FINANCE_FLG='Y' WHERE  ACCOUNT_DATE BETWEEN "+                           
			" TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') ";
			result = new TParm(TJDODBTool.getInstance().update(sql));
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
			sql="UPDATE MEM_PACKAGE_TRADE_M SET BIL_FINANCE_FLG='Y' WHERE  ACCOUNT_DATE BETWEEN "+                           
			" TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') ";
			
		}else{
			sql="UPDATE BIL_PAY SET BIL_FINANCE_FLG='Y' WHERE TRANSACT_TYPE IN('01','04','02') AND CHARGE_DATE BETWEEN "+                           
			" TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') " ;
		}
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * 新增成本数据
	 * @param parm
	 * @return
	 */
	public TParm onSaveCost(TParm parm){
		TConnection connection = getConnection();
		TParm parmValue=new TParm();
		TParm result=null;
		if (null!=parm.getValue("CHECK_FLG") &&parm.getValue("CHECK_FLG").equals("Y")) {//查询是否有数据选择确认动作
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			parmValue.setData("ADM_TYPE",parm.getValue("TYPE_SUM"));
			parmValue.setData("DATA_TYPE",parm.getValue("DATA_TYPE_SUM"));
			//parmValue.setData("SOURCESYSTEM",parm.getValue("SOURCESYSTEM_SUM"));//外部系统
			result=BILFinanceTool.getInstance().deleteDiIncomeexpByBatchname(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
			result=BILFinanceTool.getInstance().deleteDiExpLog(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		for (int i = 0; i < parm.getCount(); i++) {
			parmValue=parm.getRow(i);
			//新增应收数据
			result=BILFinanceTool.getInstance().insertDiIncomeexp(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		//新增log表
		TParm logParm=parm.getParm("LOG_PARM");
		result=BILFinanceTool.getInstance().insertDiExpLog(logParm, connection);
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	
	/**
	 * 新增分摊数据
	 * @param parm
	 * @return
	 */
	public TParm onSaveApportion(TParm parm){
		TConnection connection = getConnection();
		TParm parmValue=new TParm();
		TParm result=null;
		if (null!=parm.getValue("CHECK_FLG") &&parm.getValue("CHECK_FLG").equals("Y")) {//查询是否有数据选择确认动作
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			parmValue.setData("TYPE",parm.getValue("TYPE_SUM"));
			result=BILFinanceTool.getInstance().deleteApportion(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		for (int i = 0; i < parm.getCount(); i++) {
			parmValue=parm.getRow(i);
			//新增应收数据
			result=BILFinanceTool.getInstance().insertApportion(parmValue, connection);
			if (result.getErrCode()<0) {
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
	 * 新增高值耗材数据 add by huangjw 20141114
	 * @param parm
	 * @return
	 */
	public TParm onSaveDiIncomeHigh(TParm parm){
		TConnection connection=this.getConnection("javahisHRP");
		TParm parmValue=new TParm();
		TParm result=null;
		
		for (int i = 0; i < parm.getCount(); i++) {
			parmValue=parm.getRow(i);
			if (parmValue.getValue("FLG").equals("Y")) {
				//新增高值耗材数据
				result=BILFinanceTool.getInstance().insertDiIncomeHigh(parmValue, connection);
				if (result.getErrCode()<0) {
					connection.rollback();
					connection.close();
					return result;
				}
			}
		}
		TParm logParm=parm.getParm("LOG_PARM");//日志表数据
		result=BILFinanceTool.getInstance().insertDiExpLog(logParm, connection);
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		String sql="";
		if (parm.getValue("BUSITYPENAME_SUM").equals("门诊")) {
			//修改本地数据状态
			for (int i = 0; i < parm.getCount(); i++) {
				TParm newParm=parm.getRow(i);
				sql="UPDATE OPD_ORDER SET BIL_HIGH_FINANCE_FLG='Y' WHERE RECEIPT_NO='"+newParm.getValue("VHISBUSID")+"' AND BILL_DATE " +
						"BETWEEN TO_DATE("+parm.getValue("START_DATE") 
				+",'YYYYMMDDHH24MISS') AND TO_DATE("+parm.getValue("END_DATE")+",'YYYYMMDDHH24MISS') AND ORDER_CODE = '"+parm.getValue("VITEMCODE",i)+"' ";
				result = new TParm(TJDODBTool.getInstance().update(sql));
				if (result.getErrCode()<0) {
					connection.rollback();
					connection.close();
					return result;
				}
				if(newParm.getDouble("NCHARGEMNY")<0){
					
					sql="UPDATE OPD_ORDER_HISTORY  SET BIL_HIGH_FINANCE_FLG_RETURN='Y' " +
							" WHERE RECEIPT_NO = (SELECT  RECEIPT_NO FROM BIL_OPB_RECP WHERE RESET_RECEIPT_NO='"+newParm.getValue("VHISBUSID")+"'" +
							" AND BILL_DATE " +
							"BETWEEN TO_DATE("+parm.getValue("START_DATE")
					+",'YYYYMMDDHH24MISS') AND TO_DATE("+parm.getValue("END_DATE")+",'YYYYMMDDHH24MISS') ) AND ORDER_CODE = '"+parm.getValue("VITEMCODE",i)+"'  ";
					result = new TParm(TJDODBTool.getInstance().update(sql));
				}
			}
		}else{
			for (int i = 0; i < parm.getCount(); i++) {
				TParm newParm=parm.getRow(i);
				sql="UPDATE IBS_ORDD SET BIL_HIGH_FINANCE_FLG='Y' WHERE CASE_NO_SEQ ="+Integer.parseInt(newParm.getValue("CASE_NO_SEQ"))+"" +
				" AND CASE_NO='"+newParm.getValue("CASE_NO")+"' AND SEQ_NO="+Integer.parseInt(newParm.getValue("SEQ_NO"))+""+
				" AND BILL_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') " +
				" AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS') AND ORDER_CODE = '"+parm.getValue("VITEMCODE",i)+"' ";
				result = new TParm(TJDODBTool.getInstance().update(sql));
			}
		}
		
		if (result.getErrCode()<0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
}
