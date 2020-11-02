package jdo.inv;

import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author wangm	2013.06.25
 * @version 1.0
 */
public class INVNewBackDisnfectionTool extends TJDOTool{

	
	public static INVNewBackDisnfectionTool instanceObject;
	
	/**
     * 构造器
     */
    public INVNewBackDisnfectionTool() {
        setModuleName("inv\\INVNewBackDisinfectionModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static INVNewBackDisnfectionTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVNewBackDisnfectionTool();
        return instanceObject;
    }
    
    /**
     * 查询手术包信息
     * */
	public TParm queryPackageInfo(TParm parm){
		
		TParm result = this.query("queryPackageInfo", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	/**
     * 根据条码查询手术包信息
     * */
	public TParm queryPackageInfoByBarcode(TParm parm){
		
		TParm result = this.query("queryPackageInfoByBarcode", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	
	 /**
     * 查询手术包明细信息
     * */
	public TParm queryPackageDetailInfo(TParm parm){
		
		TParm result = this.query("queryPackageDetailInfo", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	/**
     * 根据条件查询回收单信息
     * */
	public TParm queryBackDisnfection(TParm parm){
		
		TParm result = this.query("queryBackDisinfection", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	
	/**
     * 根据回收单号查询回收单信息
     * */
	public TParm queryDisnfectionByNo(TParm parm){
		
		TParm result = this.query("queryDisinfectionByRecycleNo", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	
	/**
     * 查询打印条码信息
     * */
	public TParm queryBarcodeInfo(TParm parm){
		
		TParm result = this.query("queryBarcodeInfo", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	/**
     * 查询科室名称
     * */
	public TParm queryDeptName(TParm parm){
		
		TParm result = this.query("queryDeptName", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	
	/**
     * 查询人员姓名
     * */
	public TParm queryUserName(TParm parm){
		
		TParm result = this.query("queryUserName", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	/**
     * 查询手术包物资信息（损耗管理查询）
     * */
	public TParm queryPackMaterialInfoByBarcode(TParm parm){
		
		TParm result = this.query("queryPMInfoByBarcode", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	
	/**
     * 新建回收单方法
     * */
	public TParm insertBackDisnfection(TParm parm, TConnection connection){
		
		TParm disTable = parm.getParm("DISNFECTIONTABLE");			//回收表
		TParm packageMTable = parm.getParm("PACKAGEMAINTABLE");		//手术包表
		Object obj = parm.getParm("RECOUNTTIME");					//折损明细
		
		TParm result = new TParm();
		
		//插入回收单表      修改手术包状态
		for(int i=0;i<packageMTable.getCount();i++){
			
			//用于修改状态不为“出库”状态的手术包状态start
			TParm changeStatus = new TParm();
			changeStatus.setData("PACK_SEQ_NO",0,packageMTable.getData("PACK_SEQ_NO", i));
			changeStatus.setData("PACK_CODE",0,packageMTable.getData("PACK_CODE", i));
			changeStatus.setData("ONCE_USE_FLG",0,"Y");
			
			TParm statusParm = INVSterilizationHelpTool.getInstance().queryPackageStatus(changeStatus.getRow(0), connection);
			
			String statusStr =  statusParm.getData("STATUS").toString();
			if(statusStr.equals("[0]")){
				INVSterilizationHelpTool.getInstance().deletePackageDetailInfo(changeStatus.getRow(0), connection);//删除已有的手术包明细
			}else if(statusStr.equals("[2]")){
				INVSterilizationHelpTool.getInstance().updatePackageDisStatus(changeStatus.getRow(0), connection);
			}else if(statusStr.equals("[3]")){
				INVSterilizationHelpTool.getInstance().updatePackageSterStatus(changeStatus.getRow(0), connection);
			}
			//用于修改状态不为“出库”状态的手术包状态end
			
			
			TParm insertValue = new TParm();
			insertValue.setData("RECYCLE_NO", 0, disTable.getData("RECYCLE_NO", 0) );
			insertValue.setData("ORG_CODE", 0, disTable.getData("ORG_CODE", 0) );
			insertValue.setData("PACK_CODE", 0, packageMTable.getData("PACK_CODE", i) );
			insertValue.setData("PACK_SEQ_NO", 0, packageMTable.getData("PACK_SEQ_NO", i) );
			insertValue.setData("QTY", 0, packageMTable.getData("QTY", i) );
			insertValue.setData("RECYCLE_DATE", 0, packageMTable.getData("RECYCLE_DATE", i) );
			insertValue.setData("RECYCLE_USER", 0, packageMTable.getData("RECYCLE_USER", i) );
			insertValue.setData("WASH_DATE", 0, packageMTable.getData("WASH_DATE", i) );
			insertValue.setData("WASH_USER", 0, packageMTable.getData("WASH_USER", i) );
			insertValue.setData("DISINFECTION_POTSEQ", 0, packageMTable.getData("DISINFECTION_POTSEQ", i) );
			insertValue.setData("DISINFECTION_PROGRAM", 0, packageMTable.getData("DISINFECTION_PROGRAM", i) );
			insertValue.setData("DISINFECTION_OPERATIONSTAFF", 0, packageMTable.getData("DISINFECTION_USER", i) );
			insertValue.setData("DISINFECTION_DATE", 0, packageMTable.getData("DISINFECTION_DATE", i) );
			insertValue.setData("DISINFECTION_VALID_DATE", 0, packageMTable.getData("DISINFECTION_VALID_DATE", i) );
			insertValue.setData("DISINFECTION_USER", 0, packageMTable.getData("DISINFECTION_USER", i) );
			insertValue.setData("OPT_USER", 0, disTable.getData("OPT_USER", 0) );
			insertValue.setData("OPT_DATE", 0, disTable.getData("OPT_DATE", 0) );
			insertValue.setData("OPT_TERM", 0, disTable.getData("OPT_TERM", 0) );
			insertValue.setData("BARCODE", 0, packageMTable.getData("BARCODE", i) );
			insertValue.setData("STATUS", 0, "2" );	//状态更新为“回收清洗消毒”
			insertValue.setData("FINISH_FLG", 0, "N" );	//该单完成状态为“未完成”
			
			TParm t = insertValue.getRow(0);
			result = INVDisnfectionHelpTool.getInstance().insertDisnfection(t, connection);//插入回收单表
			
			result = INVDisnfectionHelpTool.getInstance().updatePackageStatus(t, connection);//修改手术包状态
		}
//		System.out.println("obj---"+obj);
		//修改折损次数
		if(obj!=null){
//			System.out.println("objobj--------"+obj);
//			System.out.println("obj2--------"+obj.toString());
			String tStr = obj.toString().substring(0, obj.toString().indexOf("}"));
			tStr = tStr.substring(tStr.indexOf("{")+1);
			if(tStr.length() == 0){
				return result;
			}
//			System.out.println("tStr---"+tStr);
			String [] detailInfo = tStr.split(", ");
//			System.out.println("detailInfo---"+detailInfo[0]);
			for(int i=0;i<detailInfo.length;i++){
				String [] str = detailInfo[i].split("=");
				String [] info = str[0].split("-");
				
//				System.out.println("str---"+str[0]);
//				System.out.println("info[0]---"+info[0]);
//				System.out.println("info---"+info);
				
				String packCode = info[0].trim();
				String packSeqNo = info[1].trim();
				String invCode = info[2].trim();
				String invSeqNo = info[3].trim();
				String recountTime = str[1].trim();
				
//				System.out.println(packCode+"---"+packSeqNo+"---"+invCode+"---"+invSeqNo+"---"+recountTime);
				
				TParm tp = new TParm();
				tp.setData("PACK_CODE",0, packCode);
				tp.setData("PACK_SEQ_NO",0, packSeqNo);
				tp.setData("INV_CODE",0, invCode);
				tp.setData("INVSEQ_NO",0, invSeqNo);
				tp.setData("RECOUNT_TIME",0, recountTime);
				tp.setData("ORG_CODE",0 ,disTable.getData("ORG_CODE", 0));
				TParm temp = tp.getRow(0);
				
				
//				System.out.println("temp---"+temp);
				//更新器械折损次数
				result = INVDisnfectionHelpTool.getInstance().updateRecountTime(temp, connection);
				
				
		//暂无用2013		
				//查询器械的批号
//				TParm res = INVDisnfectionHelpTool.getInstance().queryBatchNo(temp, connection);
//				System.out.println("res---"+res);
//				tp.setData("BATCH_NO",0,res.getData("BATCH_NO", 0));
//				temp = tp.getRow(0);
//				System.out.println("temp---"+temp);
				//更新主表库存
//				result = INVDisnfectionHelpTool.getInstance().updateStockMQTY(temp, connection);
				//更新细表库存
//				result = INVDisnfectionHelpTool.getInstance().updateStockDQTY(temp, connection);
			}
		}
		return result;
	}
	
	
	
	
	
	
	
}
