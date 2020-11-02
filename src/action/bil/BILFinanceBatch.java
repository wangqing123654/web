package action.bil;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import jdo.bil.BILFinanceTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.patch.Patch;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
/**
 * <p>Title: 财务接口批次展开动作类</p>
 *
 * <p>Description: 财务接口批次展开动作类</p>
 *
 * <p>Copyright: Copyright (c) 2014</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author pangben 2014.04.24
 * @version 1.0
 */
public class BILFinanceBatch extends Patch {
	public BILFinanceBatch() {
    }
    /**
     * 批次线程
     * @return boolean
     */
    public boolean run() {
    	boolean isDebug = true;
    	TConnection connection = TDBPoolManager.getInstance().getConnection("javahisHRP");
    	try {
	        TParm checkParm=new TParm();
	        System.out.println("执行HRP批次...");
	        String sDate = StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
	        String [] tableName={"di_incomeexp","di_incomereal","di_incomepre","his_feedetail","di_servicevolexp","di_drugs"};//这里的表名一定要按照此顺序排列
	    	for(int j=0;j<tableName.length;j++){
				checkParm.setData("BATCHNAME",sDate);//日期，删除此日期的数据
				checkParm.setData("EXPTABLENAME",tableName[j]);
		        TParm checkLog=BILFinanceTool.getInstance().checkLogDrlogAyh(checkParm);
				if (checkLog.getErrCode()<0) {
					System.out.println("查询中间库LOG表出现错误:::::"+checkLog.getErrText());
					//this.setMessage("查询中间库LOG表出现错误:"+checkLog.getErrText());
					insertPatchHrpLog(tableName[j]+"表查询中间库LOG表出现错误"+checkLog.getErrText());
					continue;
				}
				if (checkLog.getCount()>0) {
					System.out.println(tableName[j]+"表"+sDate+"数据已经审核,不可以导入");
					//this.setMessage("存在"+sDate+"数据,不可以导入");
					insertPatchHrpLog(tableName[j]+"表"+sDate+"数据已经审核,不可以导入");
					continue;
				}
				TParm exeParm=onExeParm(j);
				if (connection.isClosed()) {
		        	System.out.println("数据库没连接");
		        	connection.close();
		        	return false;
				}
		        TParm result=onExe(exeParm,connection,j,tableName[j]);
		        if (result.getErrCode()<0) {
					connection.rollback();
					connection.close();
					continue;
				}
		        connection.commit();
		        System.out.println(tableName[j]+"表执行完毕....");
	    	}
    	} catch (Exception e) {
    		if(isDebug){
				System.out.println("come in class: BILFinanceBatch ，method ：run");
				e.printStackTrace();
			}
		}finally{
			connection.close();
		}
        return true;
     }
    /**
	 * 查询
	 */
	public TParm onExe(TParm exeParm,TConnection connection,int j,String tableName){
		TParm result=new TParm();
		String sDate = StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		//sDate="20140322";
		TParm checkParm=new TParm();
		checkParm.setData("BATCHNAME",sDate);//日期，删除此日期的数据
		//TParm tableResult=new TParm();
		if (null!=exeParm) {
			if(tableName.equals("di_drugs")){//药品入库操作
				 //药品入库字典查询
		        String sql =" SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2,DESCRIPTION FROM SYS_DICTIONARY " +
		            "WHERE GROUP_ID='BIL_FINANCE_PHA'";
				TParm phaParm=new TParm(TJDODBTool.getInstance().select(sql));
				if(phaParm.getErrCode()<0||phaParm.getCount()<=0){
					phaParm=new TParm();
					phaParm.setErr(-1,"药品入库字典没有设置");
					return phaParm;
				}
				for (int i = 0; i < phaParm.getCount(); i++) {
					//System.out.println(phaParm.getValue("NAME",i)+":PARM:"+exeParm.getParm(phaParm.getValue("NAME",i)));
					if(exeParm.getParm(phaParm.getValue("NAME",i)).getCount()<=0){
						insertPatchHrpLog(tableName+"表"+phaParm.getValue("NAME",i)+sDate+"不存在数据");
					}else{
						result=onBilFinanceSave(checkParm, exeParm.getParm(phaParm.getValue("NAME",i)), connection, phaParm.getValue("NAME",i), j+1);
						if (result.getErrCode()<0) {
							return result;
						}
					}
				}
				
			}else{
				if (null!=exeParm.getParm("OPB_PARM")&&
						null!=exeParm.getParm("OPB_PARM").getValue("START_DATE")&&
						exeParm.getParm("OPB_PARM").getValue("START_DATE").length()>0) {
					result=onBilFinanceSave(checkParm, exeParm.getParm("OPB_PARM"), connection, "门诊", j+1);
					if (result.getErrCode()<0) {
						return result;
					}
				}else{
					insertPatchHrpLog(tableName+"表门诊"+sDate+"不存在数据");
				}
				if (null!=exeParm.getParm("ODI_PARM")&&
						null!=exeParm.getParm("ODI_PARM").getValue("START_DATE")&&
						exeParm.getParm("ODI_PARM").getValue("START_DATE").length()>0) {
					result=onBilFinanceSave(checkParm, exeParm.getParm("ODI_PARM"), connection, "住院", j+1);
					if (result.getErrCode()<0) {
						return result;
					}
				}else{
					insertPatchHrpLog(tableName+"表住院"+sDate+"不存在数据");
				}
			}
		}
		if (null==result) {
			result=new TParm();
		}
		
		return result;
	}
     private void onCheckParmNull(TParm result,TParm parm,String type){
    	if (parm!=null) {
    		result.setData(type,parm.getData());
		}
    }
     /**
      * 
     * @Title: insertPatchHrpLog
     * @Description: TODO(本地数据log表添加)
     * @author pangben
     * @param message
     * @return
     * @throws
      */
     private TParm insertPatchHrpLog(String message){
     	TParm parm=new TParm();
     	parm.setData("PATCH_CODE",getUUID());
        String postDate = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss");
     	parm.setData("PATCH_START_DATE",postDate);
     	parm.setData("PATCH_DESC","HRP批次log");
     	parm.setData("PATCH_TYPE","1");
     	parm.setData("PATCH_STATUS",message);
     	parm.setData("OPT_USER","HRP_SERVER");
     	parm.setData("OPT_TERM","127.0.0.1");
     	TParm result=BILFinanceTool.getInstance().insertPatchHrpLog(parm);
     	return result;
     }
    private TParm onExeParm(int j){
    	
		TParm parm=new TParm();
		TParm result=new TParm();
		String sDate = StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		//sDate="20140322";
		TParm checkParm=new TParm();
		checkParm.setData("BATCHNAME",sDate);//日期，删除此日期的数据
		parm.setData("START_DATE",sDate+"000000");
		parm.setData("END_DATE",sDate+"235959");
		TParm tableResult=new TParm();
		TParm temp=new TParm();
		switch (j){
		case 0:
			//应收数据门诊:TYPE:1  门诊:O
			parm.setData("TYPE","1");
			parm.setData("PRINT_TYPE","2");
			tableResult=BILFinanceTool.getInstance().onQueryByAccounts(parm);
			temp=diIncomeexpAndDiIncomerealParm(1,"O", tableResult);
			onCheckParmNull(result, temp, "OPB_PARM");
			//应收数据住院:TYPE:2  住院:O
			parm.setData("TYPE","2");
			tableResult=BILFinanceTool.getInstance().onQueryByAccounts(parm);
			temp=diIncomeexpAndDiIncomerealParm(1,"I", tableResult);
			onCheckParmNull(result, temp, "ODI_PARM");
			break;
		case 1:
			//实收数据门诊:TYPE:1  门诊:O
			parm.setData("TYPE","1");
			tableResult=BILFinanceTool.getInstance().onQueryByPaid(parm);
			temp=diIncomeexpAndDiIncomerealParm(2,"O",tableResult);
			onCheckParmNull(result, temp, "OPB_PARM");
			//实收数据住院:TYPE:2  住院:O
			parm.setData("TYPE","2");
			tableResult=BILFinanceTool.getInstance().onQueryByPaid(parm);
			temp=diIncomeexpAndDiIncomerealParm(2,"I",tableResult);
			onCheckParmNull(result, temp, "ODI_PARM");
			break;
		case 2:
			//预交金数据TYPE:1 门诊:O
			parm.setData("TYPE","1");
			tableResult=BILFinanceTool.getInstance().onQueryByTypePay(parm);
			temp=diIncomepreParm("O",tableResult);
			onCheckParmNull(result, temp, "OPB_PARM");
			//预交金数据TYPE:2  住院:I
			parm.setData("TYPE","2");
			tableResult=BILFinanceTool.getInstance().onQueryByTypePay(parm);
			temp=diIncomepreParm("I",tableResult);
			onCheckParmNull(result, temp, "ODI_PARM");
			break;
		case 3:
			//===================================================================
			//高值耗材数据TYPE:1门诊：O
			parm.setData("TYPE",1);
			tableResult=BILFinanceTool.getInstance().onQureyByBillDate(parm);
			temp=diIncomehighParm("O",tableResult);
			onCheckParmNull(result, temp, "OPB_PARM");
			//高值耗材数据TYPE:2门诊：I
			parm.setData("TYPE",2);
			tableResult=BILFinanceTool.getInstance().onQureyByBillDate(parm);
			temp=diIncomehighParm("I",tableResult);
			onCheckParmNull(result, temp, "ODI_PARM");
			break;
		case 4:
			//===================================================================
			//=======================================成本核算
			//成本核算数据 TYPE:1门诊：O
			parm.setData("TYPE",1);
			tableResult=BILFinanceTool.getInstance().onQueryCostAcount(parm);
			temp=diIncomeCostAcountParm("O",tableResult);
			onCheckParmNull(result, temp, "OPB_PARM");
			//成本核算数据 TYPE:2住院：I
			parm.setData("TYPE",2);
			tableResult=BILFinanceTool.getInstance().onQueryCostAcount(parm);
			temp=diIncomeCostAcountParm("I",tableResult);
			onCheckParmNull(result, temp, "ODI_PARM");
			break;
		case 5:
			 //药品入库字典查询
	        String sql =" SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2,DESCRIPTION FROM SYS_DICTIONARY " +
	            "WHERE GROUP_ID='BIL_FINANCE_PHA'";
			TParm phaParm=new TParm(TJDODBTool.getInstance().select(sql));
			if(phaParm.getErrCode()<0||phaParm.getCount()<=0){
				System.out.println("药品入库字典配置有问题");
				result=null;
				break;
			}
			parm.setData("BATCHNAME",sDate);
			parm.setData("EXPTABLENAME","DI_DRUGS".toLowerCase());
			for (int i = 0; i < phaParm.getCount(); i++) {
				parm.setData("BUSITYPE_NAME",phaParm.getValue("ID",i));
				tableResult=BILFinanceTool.getInstance().onQueryPhaData(parm);
				temp=diDrugsParm(tableResult, phaParm.getValue("NAME",i));
				onCheckParmNull(result, temp, phaParm.getValue("NAME",i));
			}
			break;
			//=======================================成本核算
		}
		if (null==result) {
			result=new TParm();
		}
		return result;
	
    }
   
    /**
     * 添加中间库数据公用方法
     * @param checkParm
     * @param result
     * @param connection
     * @param admType
     * @param dataType
     * @param dataTypeSum
     * @param index
     * @return
     */
    private TParm onBilFinanceSave(TParm checkParm,TParm result,TConnection connection,
    		String admType,int index){
    	checkParm.setData("BUSITYPENAME",admType);
		result.setData("BUSITYPENAME_SUM",admType);
		TParm checkCommParm=null;
		switch (index) {
		case 1:
			checkCommParm=BILFinanceTool.getInstance().checkDiIncomeexp(checkParm,connection);
			if (checkCommParm.getCount()>0) {
				//添加接口校验
				result.setData("CHECK_FLG","Y");
			}else{
			}
			TParm diIncomeexpParm=onSaveDiIncomeexp(result, connection);//应收数据保存数据
			if (diIncomeexpParm.getErrCode()<0) {
				return diIncomeexpParm;
			}
			break;
		case 2:
			checkCommParm=BILFinanceTool.getInstance().checkDiIncomereal(checkParm,connection);
			if (checkCommParm.getCount()>0) {
				//添加接口校验
				result.setData("CHECK_FLG","Y");
			}else{
			}
			TParm diIncomerealParm=onSaveDiIncomereal(result, connection);
			if (diIncomerealParm.getErrCode()<0) {
				return diIncomerealParm;
			}
			break;
		case 3:
//			checkCommParm=BILFinanceTool.getInstance().checkDiIncomepre(checkParm,connection);
			if(admType.equals("门诊")){
				checkCommParm=BILFinanceTool.getInstance().checkDiIncomepreO(checkParm,connection);
				
			}else{
				checkCommParm=BILFinanceTool.getInstance().checkDiIncomepre(checkParm,connection);
			}
			if (checkCommParm.getCount()>0) {
				//添加接口校验
				result.setData("CHECK_FLG","Y");
			}else{
			}
			TParm diIncomepreParm=onSaveDiIncomepre(result, connection);
			if (diIncomepreParm.getErrCode()<0) {
				return diIncomepreParm;
			}
			break;
		case 4://高值耗材
			checkCommParm=BILFinanceTool.getInstance().checkDiIncomeHigh(checkParm,connection);
			if (checkCommParm.getCount()>0) {
				//添加接口校验
				result.setData("CHECK_FLG","Y");
			}else{
			}
			TParm diIncomehighParm=onSaveDiIncomehigh(result, connection);
			if(diIncomehighParm.getErrCode()<0){
				return diIncomehighParm;
			}
			break;
		case 5://成本核算
			checkCommParm=BILFinanceTool.getInstance().checkDiIncomeCostAcount(checkParm, connection);
			if(checkCommParm.getCount()>0){
				//添加接口校验
				result.setData("CHECK_FLG","Y");
			}else{
			}
			TParm diIncomeCostAcountParm=onSaveDiIncomeCostAcount(result,connection);
			if(diIncomeCostAcountParm.getErrCode()<0){
				return diIncomeCostAcountParm;
			}
			break;
		case 6://药品入库
			checkCommParm=BILFinanceTool.getInstance().checkDidrugs(checkParm);
			if(checkCommParm.getCount()>0){
				//添加接口校验
				result.setData("CHECK_FLG","Y");
			}
			TParm diDrugsParm=onSaveDidrugs(result, connection);
			if(diDrugsParm.getErrCode()<0){
				return diDrugsParm;
			}
			break;
		default:
			break;
		}
		return checkParm;
    }
    /**
	 * 药品入库数据
	 * pangben 2016-6-16
	 * @param parm
	 * @return
	 */
	private TParm onSaveDidrugs(TParm parm,TConnection connection){
		TParm parmValue=new TParm();
		TParm result=null;
		TParm logParm=parm.getParm("LOG_PARM");//日志表数据
		if (null!=parm.getValue("CHECK_FLG") &&parm.getValue("CHECK_FLG").equals("Y")) {//查询是否有数据选择确认动作
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			parmValue.setData("EXPTABLENAME",logParm.getValue("EXPTABLENAME"));
			parmValue.setData("BUSITYPENAME",logParm.getValue("SOURCESYSTEM"));//外部系统
			parmValue.setData("SOURCESYSTEM",logParm.getValue("SOURCESYSTEM"));
			result=BILFinanceTool.getInstance().deleteDidrugsByBatchname(parmValue, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
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
	 * 药品入库
	 * @param type
	 * @return
	 */
	private TParm diDrugsParm(TParm parm,String billType){
		String date="";
		TParm parmValue=new TParm();
		int sum=0;
		String eDate =  StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		//String eDateT= StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyy-MM-dd");
		parmValue.setData("START_DATE",eDate+"000000");
		parmValue.setData("END_DATE",eDate+"235959");
		String uid=getUUID();//日志表主键
		//String checkDate="";
		for (int i = 0; i <parm.getCount("ACCOUNT_DATE"); i++) {
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			parmValue.addData("PK_DRUGS",getUUID());//外部系统收入表主键
			//parmValue.addData("DATA_TYPE","C");
			parmValue.addData("AMOUNT",  parm.getDouble("AR_AMT",i));//金额
			parmValue.addData("PK_EXPLOG", uid);//导出日志表主键
			parmValue.addData("BATCHNAME",eDate);//批标记建议用"YYYYMMDD"
			parmValue.addData("BILLCLERKNAME",parm.getValue("USER_NAME",i));//制单人
			parmValue.addData("BUSITYPENAME",parm.getValue("BILL_TYPE",i));//业务类型名称
			parmValue.addData("CDAY",parm.getValue("ACCOUNT_DATE",i));//日期
			parmValue.addData("CMONTH", date.substring(4,6));//月份
			parmValue.addData("CYEAR", date.substring(0,4));//年度
			//parmValue.addData("EXPTIME", SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//导出时间建议用“HHMMSS”
			parmValue.addData("CORPNAME", "北京爱育华妇儿医院");//公司名称--可选编码，名称，pk,推荐名称
			parmValue.addData("BILLDEPTCODE", parm.getValue("DEPT_CODE",i));//开单科室代码
			parmValue.addData("BILLDEPTNAME", parm.getValue("DEPT_DESC",i));//开单科室名称
			parmValue.addData("DDATE", parm.getValue("ACCOUNT_DATE",i));//日期
			parmValue.addData("PK_CORP", "001");//公司主键--可选编码，名称，pk,推荐名称
			parmValue.addData("EXECDEPTCODE", parm.getValue("EXE_DEPT_CODE",i));//执行科室代码
			parmValue.addData("EXECDEPTNAME", parm.getValue("EXE_DEPT_DESC",i));//执行单科室名称
			parmValue.addData("DURGCLCODE", parm.getValue("PHA_TYPE",i));//药品类别代码
			parmValue.addData("DURGCLNAME", parm.getValue("PHA_TYPE_DESC",i));//药品类别名称
			parmValue.addData("CUSTCODE", parm.getValue("SUP_CODE",i));//供货商代码
			parmValue.addData("CUSTNAME", parm.getValue("SUP_CHN_DESC",i));//供货商名称
			parmValue.addData("PHARMACYCODE", parm.getValue("PHA_CODE",i));//药房代码
			parmValue.addData("PHARMACYNAME", parm.getValue("PHA_DESC",i));//药房名称		
			parmValue.addData("INOUT", parm.getValue("INOUT",i));//收/退
			parmValue.addData("VDEF1", parm.getValue("VDEF1",i));//门诊住院类别
			parmValue.addData("OPT_USER", Operator.getID());//
			parmValue.addData("OPT_TERM", Operator.getIP());//
			sum++;
		}
		//parmValue.setData("TYPE_SUM",billType);
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		parmValue.setData("CHEKC_DATE",eDate);
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//主键
		logParm.setData("EXPTABLENAME","DI_DRUGS".toLowerCase());//导出表名
		logParm.setData("BATCHNAME",eDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//导出时间建议用“HHMMSS”
		logParm.setData("EXPOPERATOR",Operator.getName());//导出操作员姓名
		logParm.setData("EXPSTATE",0);//导出状态
		logParm.setData("TAOTALNUM",sum);//导出记录总数
		logParm.setData("SOURCESYSTEM",parm.getValue("BILL_TYPE",0));
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		//=====================================================
		return parmValue;
	}
	/**
	 * 应收\实收
	 * @return
	 */
	private TParm diIncomeexpAndDiIncomerealParm(int index,String type,TParm parm){
		String date="";
		TParm parmValue=new TParm();
		int row=parm.getCount();
		if (row<=0) {
			return null;
		}
		int sum=0;
		int dataType=0;
		//String dataMessage="";
		if (type.equals("O")) {
			dataType=13;
		}else if (type.equals("I")) {
			dataType=14;
		}
		String uid=getUUID();//作为日志表主键  add by huangjw 20150414
		String eDate =  StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		String eDateT= StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyy-MM-dd");
		parmValue.setData("START_DATE",eDate+"000000");
		parmValue.setData("END_DATE",eDate+"235959");
		//String checkDate="";
		String bilDate="";
		for (int i = 0; i <row; i++) {
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			bilDate=SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true).toString();
			//checkDate=date.substring(0,8);
			if (index==1) {
				parmValue.addData("PK_INCOMEEXP",getUUID());//外部系统收入表主键
				parmValue.addData("DATA_TYPE","A");
			}else if(index==2){
				parmValue.addData("DATA_TYPE","B");
				parmValue.addData("PK_INCOMEREAL",getUUID());//外部系统收入表主键
			}
			parmValue.addData("PK_EXPLOG", uid);//导出日志表主键
			parmValue.addData("BATCHNAME", eDate);//批标记建议用"YYYYMMDD"
			parmValue.addData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//导出时间建议用“HHMMSS”
			parmValue.addData("CYEAR", date.substring(0,4));//年度
			parmValue.addData("CMONTH", date.substring(4,6));//月份
			parmValue.addData("CDAY",bilDate.substring(0,4)+"-"+bilDate.substring(4,6)+"-"+bilDate.substring(6,8));//日期
			parmValue.addData("PK_CORP", "001");//公司主键--可选编码，名称，pk,推荐名称
			parmValue.addData("CORPNAME", "北京爱育华妇儿医院");//公司名称--可选编码，名称，pk,推荐名称
			parmValue.addData("PK_ITEM", parm.getValue("BILL_ID",i));//收入主键
			parmValue.addData("ITEMNAME", parm.getValue("BILL_TYPE",i));//收入名称
			parmValue.addData("PK_INCOMETYPE", parm.getValue("BILL_ID",i));//收入分类主键
			parmValue.addData("INCOMETYPENAME", parm.getValue("BILL_TYPE",i));//收入分类名称
			parmValue.addData("PK_BUSITYPE",parm.getValue("TYPE_ID",i));//业务类型主键
			parmValue.addData("BUSITYPENAME", parm.getValue("TYPE",i));//业务类型名称
			parmValue.addData("PK_DIAGNOSISDEPT", parm.getValue("DEPT_CODE",i));//开单科室主键
			parmValue.addData("DIAGNOSISDEPTNAME", parm.getValue("DEPT_DESC",i));//开单科室名称
			parmValue.addData("PK_DIAGNOSTICIAN", parm.getValue("DR_CODE",i));//开单医生主键
			parmValue.addData("DIAGNOSTICIANNAME",parm.getValue("DR_DESC",i));//开单医生名称
			parmValue.addData("PK_EXEDEPT", parm.getValue("EXE_DEPT_CODE",i));//执行科室主键
			parmValue.addData("EXEDEPTNAME", parm.getValue("EXE_DEPT_DESC",i));//执行科室名称
			parmValue.addData("PK_EXECUTOR", Operator.getID());//执行人主键
			parmValue.addData("EXECUTORNAME",Operator.getName());//执行人名称
			parmValue.addData("PK_BILLCLERK",parm.getValue("PRINT_USER",i));//收费员主键
			parmValue.addData("BILLCLERKNAME",parm.getValue("PRINT_NAME",i));//收费员名称
			parmValue.addData("PK_PATIENT", parm.getValue("MR_NO",i));//病人主键
			parmValue.addData("PATIENTNAME", parm.getValue("PAT_NAME",i));//病人名称
			parmValue.addData("CUSTCODE", parm.getValue("MR_NO",i));//客户编码--中间表
			parmValue.addData("CUSTNAME", parm.getValue("PAT_NAME",i));//客户名称--中间表
			parmValue.addData("CPAYMENT", parm.getValue("BILL_TYPE",i));//支付方式--中间表
			parmValue.addData("CPAYITEM", "");//支付明细--中间表
			parmValue.addData("DDATE", parm.getValue("ACCOUNT_DATE",i).substring(0,10));//核算日期--中间表//核算日期--中间表
			parmValue.addData("REALDATE",  SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true));//结算日期--中间表
			parmValue.addData("VNUMBER", parm.getValue("TID",i));//序号--中间表
			parmValue.addData("CPAYMENTNUM", parm.getValue("BILL_ID",i));//支付方式编码--中间表
			parmValue.addData("CPAYITEMNUM", "");//支付明细编码--中间表
			parmValue.addData("PK_CINPATIENTAREA", parm.getValue("CLINICAREA_CODE",i));//病区主键
			parmValue.addData("CINPATIENTAREA", parm.getValue("CLINIC_DESC",i));//病区
			parmValue.addData("ISINSURANCE", parm.getValue("INS_FLG",i));//是否医保Y:是医保;N:非医保
			parmValue.addData("DISEASE", "");//单病种
			parmValue.addData("DISEASEGROUP", "");//病种组
			parmValue.addData("AMOUNT",  parm.getDouble("TOT_AMT",i));//金额
			parmValue.addData("SOURCESYSTEM", 1);//外部数据来源系统
			parmValue.addData("PK_OUTERSYSTEM", "HIS");//外系统主键
			parmValue.addData("INPRICE", "");//收入单价
			parmValue.addData("CASHBACK", "");//收退
			parmValue.addData("ADM_TYPE", type);//类型
			parmValue.addData("OPT_USER", "FC_BATCH");//
			parmValue.addData("OPT_TERM", "127.0.0.1");//
			sum++;
		}
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		parmValue.setData("CHEKC_DATE",eDate);
		//====================================================添加到日志表add by huangjw 20150414
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//主键
		if(index==1){//应收表
			logParm.setData("EXPTABLENAME","DI_INCOMEEXP".toLowerCase());//导出表名
		}else{//实收表
			logParm.setData("EXPTABLENAME","DI_INCOMEREAL".toLowerCase());//导出表名
		}
		
		logParm.setData("BATCHNAME",eDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//导出时间建议用“HHMMSS”
		logParm.setData("EXPOPERATOR","FC_BATCH");//导出操作员姓名
		logParm.setData("EXPSTATE",0);//导出状态
		logParm.setData("TAOTALNUM",sum);//导出记录总数
		logParm.setData("SOURCESYSTEM",dataType);
		parmValue.setData("LOG_PARM",logParm.getData());
		//=====================================================
		return parmValue;
	}
	
	/**
	 * 预交金
	 * @return
	 */
	private TParm diIncomepreParm(String type,TParm parm){
		String date="";
		TParm parmValue=new TParm();
		int row=parm.getCount();
		if (row<=0) {
			return null;
		}
		int sum=0;
		int dataType=0;
		if (type.equals("O")) {
			dataType=13;
		}else if (type.equals("I")) {
			dataType=14;
		}
		String uid=getUUID();
		String eDate = StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		String eDateT= StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyy-MM-dd");
		parmValue.setData("START_DATE",eDate+"000000");
		parmValue.setData("END_DATE",eDate+"235959");//String checkDate="";
		String bilDate="";
		for (int i = 0; i <row; i++) {
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			bilDate=SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true).toString();
			//checkDate=date.substring(0,8);
			parmValue.addData("PK_INCOMEPRE",getUUID());//外部系统收入表主键
			parmValue.addData("DATA_TYPE","C");
			parmValue.addData("PK_EXPLOG", uid);//导出日志表主键
			parmValue.addData("BATCHNAME",eDate);//批标记建议用"YYYYMMDD"
			parmValue.addData("EXPTIME", SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//导出时间建议用“HHMMSS”
			parmValue.addData("CYEAR", date.substring(0,4));//年度
			parmValue.addData("CMONTH", date.substring(4,6));//月份
			parmValue.addData("CDAY",bilDate.substring(0,4)+"-"+bilDate.substring(4,6)+"-"+bilDate.substring(6,8));//日期
			parmValue.addData("DDATE", parm.getValue("ACCOUNT_DATE",i).substring(0,10));//日期
			parmValue.addData("PK_CORP", "001");//公司主键--可选编码，名称，pk,推荐名称
			parmValue.addData("CORPNAME", "北京爱育华妇儿医院");//公司名称--可选编码，名称，pk,推荐名称
			parmValue.addData("SOURCESYSTEM", 1);//外部数据来源系统
			parmValue.addData("PK_OUTERSYSTEM", "HIS");//外系统主键
			parmValue.addData("REALDATE",  SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true));//结算日期--中间表
			parmValue.addData("VNUMBER", parm.getValue("TID",i));//序号--中间表
			parmValue.addData("BUSITYPENAME", parm.getValue("TYPE",i));//业务类型名称
			parmValue.addData("PK_INCOMETYPE", parm.getValue("BILL_ID",i));//收入分类主键
			parmValue.addData("INCOMETYPENAME", parm.getValue("BILL_TYPE",i));//收入分类名称
			parmValue.addData("AMOUNT",  parm.getDouble("TOT_AMT",i));//金额
			parmValue.addData("CPAYMENT", parm.getValue("BILL_TYPE",i));//支付方式--中间表
			parmValue.addData("CPAYITEM", "");//支付明细--中间表
			parmValue.addData("CASHBACK", parm.getDouble("TOT_AMT",i)<0?"退":"收");//收退
			parmValue.addData("BILLCLERKNAME",parm.getValue("PRINT_NAME",i));//收费员名称
			parmValue.addData("PK_BILLCLERK",parm.getValue("PRINT_USER",i));//收费员主键
			parmValue.addData("CPAYMENTNUM", parm.getValue("BILL_ID",i));//支付方式编码--中间表
			parmValue.addData("CPAYITEMNUM", "");//支付明细编码--中间表
			parmValue.addData("PK_PATIENT", parm.getValue("MR_NO",i));//病人主键
			parmValue.addData("PATIENTNAME", parm.getValue("PAT_NAME",i));//病人名称
			parmValue.addData("ADM_TYPE", type);//类型
			parmValue.addData("OPT_USER", "FC_BATCH");//
			parmValue.addData("OPT_TERM", "127.0.0.1");//
			parmValue.addData("VDEF2", parm.getValue("PACKAGE_CODE",i));//套餐编码
			parmValue.addData("VDEF3", parm.getValue("PACKAGE_DESC",i));//套餐名称
			sum++;
		}
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(parmValue.getCount("PK_INCOMEPRE"));
		parmValue.setData("CHEKC_DATE",eDate);
		//====================================================添加到日志表add by huangjw 20150414
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//主键
		logParm.setData("EXPTABLENAME","di_incomepre");//导出表名
		logParm.setData("BATCHNAME",eDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//导出时间建议用“HHMMSS”
		logParm.setData("EXPOPERATOR","FC_BATCH");//导出操作员姓名
		logParm.setData("EXPSTATE",0);//导出状态
		logParm.setData("TAOTALNUM",sum);//导出记录总数
		logParm.setData("SOURCESYSTEM",dataType);
		parmValue.setData("LOG_PARM",logParm.getData());
		//=====================================================
		return parmValue;
	}
	
	/**
	 * 高值耗材
	 * @param type
	 * @param parm
	 * @return
	 */
	private TParm diIncomehighParm(String type,TParm parm){
		String date="";
		TParm parmValue=new TParm();
		int row=parm.getCount();
		if (row<=0) {
			return null;
		}
		String uid= getUUID();
		int sum=0;
		int dataType=0;
		String dataMessage="";
		if (type.equals("O")) {
			dataType=13;
			dataMessage="门诊";
		}else if (type.equals("I")) {
			dataType=14;
			dataMessage="住院";
		}
		String eDate = StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		String eDateT= StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyy-MM-dd");
		parmValue.setData("START_DATE",eDate+"000000");
		parmValue.setData("END_DATE",eDate+"235959");
		for(int i=0;i<row;i++){
			parmValue.addData("FLG", "Y");
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			parmValue.addData("PK_EXPLOG", uid);//导出日志表主键
			parmValue.addData("BATCHNAME", eDate);//批标记建议用"YYYYMMDD"
			parmValue.addData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));
			parmValue.addData("SOURCESYSTEM", dataType);//外部数据来源系统
			
			parmValue.addData("PK_FEEDETAIL",getUUID());//外部系统收入表主键
			parmValue.addData("BISCHARGE",parm.getValue("BILL_FLG",i));
			//parmValue.addData("BISPROC", parm.getValue("HRP_FLG",i));
			parmValue.addData("VHISBUSID",parm.getValue("RECEIPT_NO",i));
			parmValue.addData("VBARCODE",parm.getValue("INV_CODE",i));
			parmValue.addData("VITEMCODE",parm.getValue("ORDER_CODE",i));
			parmValue.addData("VITEMNAME",parm.getValue("ORDER_DESC",i));
			parmValue.addData("VRECEIPTCODE",parm.getValue("REXP_CODE",i));
			parmValue.addData("VRECEIPTNAME",parm.getValue("REXP_DESC",i));
			parmValue.addData("PK_CORP","001");
			parmValue.addData("NCHARGEMNY",parm.getValue("AR_AMT",i));
			parmValue.addData("NCHARGENUMBER",parm.getValue("DOSAGE_QTY",i));
			parmValue.addData("DCHARGEDATE",date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8));
			parmValue.addData("VBILLCLERKCODE",parm.getValue("BILL_USER",i));
			parmValue.addData("VBILLCKERKNAME",parm.getValue("BILL_NAME",i));
			parmValue.addData("VBILLDEPTCODE",parm.getValue("DEPT_CODE",i));
			parmValue.addData("VBILLDEPTNAME",parm.getValue("DEPT_NAME",i));
			parmValue.addData("VEXECDEPTCODE",parm.getValue("EXEC_DEPT_CODE",i));
			parmValue.addData("VEXECDEPTNAME",parm.getValue("EXEC_DEPT_NAME",i));
			parmValue.addData("VADMNUMBER",parm.getValue("MR_NO",i));
			parmValue.addData("VPATIENTNAME",parm.getValue("PAT_NAME",i));
			parmValue.addData("VSEX",parm.getValue("SEX_CODE",i));
			parmValue.addData("VOPNAME",parm.getValue("OP_DESC",i));
			parmValue.addData("DOPDATE",parm.getValue("OP_DATE",i));
			parmValue.addData("BUSITYPENAME",dataMessage);
			parmValue.addData("VNAMEPHYSICIAN",parm.getValue("DR_NAME",i));
			parmValue.addData("IPATIENTAGE",getYear(parm.getTimestamp("BIRTH_DATE",i)));
			parmValue.addData("VFAMILYMEMBERS",parm.getValue("PAT_FAMLIY",i));
			parmValue.addData("VTEL",parm.getValue("PAT_TEL",i));
			parmValue.addData("VUSERCODE",parm.getValue("BILL_USER",i));//FC_BATCH 改为 parm.getValue("BILL_USER",i) 20160120 huangjw
			parmValue.addData("UNITCODE","001");
			parmValue.addData("BISPROC","N");
			//=================================住院用
			parmValue.addData("CASE_NO_SEQ", parm.getValue("CASE_NO_SEQ",i));//
			parmValue.addData("CASE_NO", parm.getValue("CASE_NO",i));//就诊记录
			parmValue.addData("SEQ_NO", parm.getValue("SEQ_NO",i));//序号
			//=================================住院用
			sum++;
		}
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(parmValue.getCount("PK_FEEDETAIL"));
		parmValue.setData("CHEKC_DATE",eDate);
		//====================================================添加到日志表add by huangjw 20150414
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//主键
		logParm.setData("EXPTABLENAME","his_feedetail");//导出表名
		logParm.setData("BATCHNAME",eDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//导出时间建议用“HHMMSS”
		logParm.setData("EXPOPERATOR",parm.getValue("BILL_USER",0));//导出操作员姓名  FC_BATCH 改为 parm.getValue("BILL_USER",i) 20160120 huangjw
		logParm.setData("EXPSTATE",0);//导出状态
		logParm.setData("TAOTALNUM",sum);//导出记录总数
		logParm.setData("SOURCESYSTEM",dataType);
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		return parmValue;
	}
	/**
	 *成本核算数据add by huangjw 20150410
	 * @param type
	 * @param parm
	 * @return
	 */
	public TParm diIncomeCostAcountParm(String type,TParm parm){
		TParm parmValue=new TParm();
		int row=parm.getCount();
		if (row<=0) {
			return null;
		}
		String exptime=SystemTool.getInstance().getDate().toString().substring(0,19);
		String exptime1=SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", "");
		
		String sDate = StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		String uid= getUUID();
		int sum=0;
		int dataType=0;
		String dataMessage="";
		if ("O".equals(type)) {
			dataType=13;
			dataMessage="门诊";
		}else if ("I".equals(type)) {
			dataType=14;
			dataMessage="住院";
		}
		String eDate = StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyyMMdd");
		String eDateT= StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(),-1), "yyyy-MM-dd");
		String cyear=eDateT.substring(0,4);
		String cmonth=eDateT.substring(5,7);
		String cday=eDateT;
		parmValue.setData("START_DATE",eDate+"000000");
		parmValue.setData("END_DATE",eDate+"235959");
		for (int i = 0; i <row; i++) {
			parmValue.addData("PK_SERVICEVOLEXP", getUUID());//外部系统对外服务量表主建
			parmValue.addData("PK_EXPLOG", uid);//导出日志表主键
			parmValue.addData("BATCHNAME", sDate);//导出批标记
			if("O".equals(type)){
				parmValue.addData("TYPE", "1");
			}else if("I".equals(type)){
				parmValue.addData("TYPE", "2");
			}
			parmValue.addData("BUSITYPENAME", parm.getValue("TYPE",i));
			parmValue.addData("EXPTIME", exptime1);//导出时间
			parmValue.addData("CYEAR", cyear);//年度
			parmValue.addData("CMONTH", cmonth);//月份
			parmValue.addData("CDAY", cday);//业务日期
			parmValue.addData("PK_CORP", "001");//公司主键
			parmValue.addData("CORPNAME", "北京爱育华妇儿医院");//公司名称
			parmValue.addData("PK_ITEM", parm.getValue("PK_ITEM",i));//服务项目主键
			parmValue.addData("ITEMNAME", parm.getValue("ITEMNAME",i));//服务项目名称
			parmValue.addData("VOLUME", parm.getDouble("VOLUME",i));//服务量
			parmValue.addData("PK_DIAGNOSISDEPT", parm.getValue("PK_DIAGNOSISDEPT",i));//科室主键
			parmValue.addData("DIAGNOSISDEPTNAME", parm.getValue("DIAGNOSISDEPTNAME",i));//科室名称
			parmValue.addData("PK_OUTERSYSTEM", dataMessage);//
			parmValue.addData("SOURCESYSTEM", dataType);//
			sum+=parm.getDouble("VOLUME",i);
		}
		parmValue.setData("CHEKC_DATE",sDate);
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//主键
		logParm.setData("EXPTABLENAME","DI_SERVICEVOLEXP".toLowerCase());//导出表名
		logParm.setData("BATCHNAME",sDate);
		logParm.setData("EXPTIME",exptime1);//导出时间建议用“HHMMSS”
		logParm.setData("EXPOPERATOR","FC_BATCH");//导出操作员姓名
		logParm.setData("EXPSTATE",0);//导出状态
		logParm.setData("TAOTALNUM",sum);//导出记录总数
		logParm.setData("SOURCESYSTEM",dataType);
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		parmValue.setData("LOCAL_LOG_FLG",parm.getBoolean("LOCAL_LOG_FLG"));
		return parmValue;
	}
	private String getYear(Timestamp newDate){
		java.util.Date date = new Date();
		long day = (date.getTime() - newDate.getTime()) / (24 * 60 * 60 * 1000)
				+ 1;
		String year=new java.text.DecimalFormat("#.00").format(day/365f);
		return year;
	}
	
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		// 去掉“-”符号
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18)
				+ s.substring(19, 23);
    } 
	/**
	 * 应收操作保存数据
	 * @param parm
	 * @param connection
	 * @return
	 */
	private TParm onSaveDiIncomeexp(TParm parm,TConnection connection){
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
				return result;
			}
		}
		//新增应收日志表数据
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
			
		}else{
			sql="UPDATE IBS_ORDD SET BIL_FINANCE_FLG='Y' WHERE BILL_NO IN (SELECT A.BILL_NO FROM IBS_BILLM A,IBS_BILLD B WHERE A.BILL_NO=B.BILL_NO AND A.BILL_SEQ=B.BILL_SEQ  AND A.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+
		"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS'))";
		}
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode()<0) {
			return result;
		}
		return result;
	}
	/**
	 * 新增实收数据
	 * @param parm
	 * @return
	 */
	private TParm onSaveDiIncomereal(TParm parm,TConnection connection){
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
			//新增实收数据
			result=BILFinanceTool.getInstance().insertDiIncomereal(parmValue, connection);
			if (result.getErrCode()<0) {
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
			
		}else{
			sql="UPDATE BIL_IBS_RECPM SET BIL_FINANCE_FLG='Y' WHERE ACCOUNT_SEQ IS NOT NULL AND CHARGE_DATE BETWEEN TO_DATE('"
				+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')";
		}
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode()<0) {
			return result;
		}
		return result;
	}
	
	/**
	 * 新增预交金数据
	 * @param parm
	 * @return
	 */
	public TParm onSaveDiIncomepre(TParm parm,TConnection connection){
		TParm parmValue=new TParm();
		TParm result=null;
		TParm logParm=parm.getParm("LOG_PARM");//日志表数据
		if (null!=parm.getValue("CHECK_FLG")&&parm.getValue("CHECK_FLG").equals("Y")) {//查询是否有数据选择确认动作
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			parmValue.setData("EXPTABLENAME",logParm.getValue("EXPTABLENAME"));
			parmValue.setData("BUSITYPENAME",parm.getValue("BUSITYPENAME_SUM"));
			parmValue.setData("SOURCESYSTEM",logParm.getValue("SOURCESYSTEM"));//外部系统
//			result=BILFinanceTool.getInstance().deleteDiIncomepreByBatchname(parmValue, connection);
			if(parm.getValue("BUSITYPENAME_SUM").equals("门诊")){
				result=BILFinanceTool.getInstance().deleteDiIncomepreByBatchnameO(parmValue, connection);
			}else{
				result=BILFinanceTool.getInstance().deleteDiIncomepreByBatchname(parmValue, connection);
			}									
			if (result.getErrCode()<0) {
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
			sql="UPDATE MEM_PACKAGE_TRADE_M SET BIL_FINANCE_FLG='Y' WHERE  ACCOUNT_DATE BETWEEN "+                           
			" TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') ";
			
		}else{
			sql="UPDATE BIL_PAY SET BIL_FINANCE_FLG='Y' WHERE TRANSACT_TYPE IN('01','04','02') AND CHARGE_DATE BETWEEN "+                           
			" TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') " ;
		}
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode()<0) {
			return result;
		}
		return result;
	}
	
	/**
	 * 高值耗材
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm onSaveDiIncomehigh(TParm parm,TConnection connection){
		TParm parmValue=new TParm();
		TParm result=null;
		TParm logParm=parm.getParm("LOG_PARM");//日志表数据
		boolean flg=true;
		if (null!=parm.getValue("CHECK_FLG")&&parm.getValue("CHECK_FLG").equals("Y")) {//查询是否有数据选择确认动作
			parmValue.setData("BATCHNAME",parm.getValue("CHEKC_DATE"));
			//parmValue.setData("EXPTABLENAME",logParm.getValue("EXPTABLENAME"));
			parmValue.setData("BUSITYPENAME",parm.getValue("BUSITYPENAME_SUM"));
			//parmValue.setData("SOURCESYSTEM",logParm.getValue("SOURCESYSTEM"));//外部系统
			result=BILFinanceTool.getInstance().deleteDiIncomeHighPriceByBatchname(parmValue, connection);
			if (result.getErrCode()<0) {
				return result;
			}
			flg=false;
		}
		for (int i = 0; i < parm.getCount(); i++) {
			parmValue=parm.getRow(i);
			//新增高值耗材数据
			result=BILFinanceTool.getInstance().insertDiIncomeHigh(parmValue, connection);
			if (result.getErrCode()<0) {
				return result;
			}
		}
		if(flg){
			result=BILFinanceTool.getInstance().insertDiExpLog(logParm, connection);
			if (result.getErrCode()<0) {
				connection.rollback();
				connection.close();
				return result;
			}
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
				" AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')  AND ORDER_CODE = '"+parm.getValue("VITEMCODE",i)+"' ";
				result = new TParm(TJDODBTool.getInstance().update(sql));
			}
		}
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode()<0) {
			return result;
		}
		return result;
	}
	/**
	 * 成本核算
	 * @return
	 */
	public TParm onSaveDiIncomeCostAcount(TParm parm,TConnection connection){
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
			TConnection localConnection = TDBPoolManager.getInstance().getConnection();;
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
}
