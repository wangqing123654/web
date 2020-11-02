package action.dev;

import java.util.List;

import jdo.dev.DevCheckTool;
import jdo.dev.DevInStorageTool;
import jdo.dev.DevOutRequestDTool;
import jdo.dev.DevOutRequestMTool;
import jdo.dev.DevOutStorageTool;
import jdo.dev.DevPurChaseTool;
import jdo.dev.DevRequestTool;
import jdo.dev.DevTrackTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 设备ACTION</p>  
 *
 * <p>Description:设备ACTION</p>
 *  
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: javahis</p>  
 *
 * @author  fux  
 * @version 1.0
 */  
public class DevAction extends TAction {
	/**
	 * 设备请购保存
	 * 
	 * @param parm
	 *            TParm   
	 * @return TParm    DEV_PURCHASEM
	 */
	public TParm saveDevPurChase(TParm parm) {
		TParm result = new TParm(); 
		TConnection connection = getConnection();
		result = DevPurChaseTool.getInstance().saveDevPurChase(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		return result;
	}

	/**
	 * 设备请购细表保存
	 * 
	 * @param parm
	 *            TParm   
	 * @return TParm    DEV_PURCHASED
	 */  
	public TParm saveDevPurChased(TParm parm) {
		TParm result = new TParm(); 
		TConnection connection = getConnection();
		result = DevPurChaseTool.getInstance().saveDevPurChase(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		return result;
	}
	/**
	 * 设备入库保存
	 * 
	 * @param parm  
	 *            TParm  
	 * @return TParm 
	 */  
	public TParm generateInStorageReceipt(TParm parm) {
		TParm result = new TParm();
		                           
		TConnection connection = getConnection(); 
		// 入库主表 %
		result = DevInStorageTool.getInstance().insertDevInwarehouseM(parm.getParm("DEV_INWAREHOUSEM"), connection);
		System.out.println("入库主表result"+result);
		connection.commit();
		System.out.println("// 入库主表 %");
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();
			return result;
		}
		// 入库细表  %
		TParm inWareHouseD = parm.getParm("DEV_INWAREHOUSED");  
		System.out.println("inWareHouseD"+inWareHouseD);
	    for (int i = 0; i < inWareHouseD.getCount("INWAREHOUSE_NO"); i++) { 
	    	  System.out.println("// 入库细表  %");
		result = DevInStorageTool.getInstance().insertDevInwarehouseD(inWareHouseD.getRow(i), connection);
		System.out.println("入库细表 result"+result); 
		
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();  
			return result;       
		}    
		connection.commit();
		  } 
		// 入库明细表 % ORGIN_CODE
	    TParm inWareHouseDD = parm.getParm("DEV_INWAREHOUSEDD");
	    System.out.println("inWareHouseDD"+inWareHouseDD);
	    for (int i = 0; i < inWareHouseDD.getCount("INWAREHOUSE_NO"); i++) { 
	    	System.out.println("// 入库明细表 % ORGIN_CODE");
//	    	if(inWareHouseDD.getBoolean(""))
		result = DevInStorageTool.getInstance().insertDevInwarehouseDD(inWareHouseDD.getRow(i), connection);
		System.out.println("入库明细表result"+result);
		connection.commit(); 
		if (result.getErrCode() < 0) { 
			//connection.rollback();
			connection.close();
			return result;       
		}   
	    }
	    
	   	    
		// 库存主表(插入或更新)  %
	    TParm stockM = parm.getParm("DEV_STOCKM");
	    for (int i = 0; i < stockM.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertOrUpdateStockM(stockM.getRow(i), connection);
		System.out.println(" 库存主表result"+result);
		connection.commit();
		if (result.getErrCode() < 0) { 
			//connection.rollback();
			connection.close();
			return result;                            }   
	    }
	    System.out.println("// 库存主表(插入或更新)  %");
		// 库存细表  %
        TParm stockD = parm.getParm("DEV_STOCKD");
        for (int i = 0; i < stockD.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertOrUpdateStockD(stockD.getRow(i), connection);
		System.out.println(" 库存细表result"+result);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close(); 
			return result;    
		            }              
        }
        System.out.println("// 库存细表  %");
		// 库存明细表--序号管理设备  % 
        TParm stockDD = parm.getParm("DEV_STOCKDD");
        for (int i = 0; i < stockDD.getCount("DEV_CODE"); i++) {  
		result = DevInStorageTool.getInstance().insertOrUpdateStockDD(stockDD.getRow(i), connection);
		System.out.println(" 库存明细表result"+result);
		connection.commit(); 
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close(); 
			return result; 
		   }          
        } 
        System.out.println("// 库存明细表--序号管理设备  % ");
		return result;
	}

	
	/**
	 * 设备入库保存
	 * 
	 * @param parm  
	 *            TParm   
	 * @return TParm 
	 */  
	public TParm generateOutReqInStorageReceipt(TParm parm) {
		TParm result = new TParm();
		                           
		TConnection connection = getConnection(); 
		// 入库主表 %
		result = DevInStorageTool.getInstance().insertDevInwarehouseM(parm.getParm("DEV_INWAREHOUSEM"), connection);
		System.out.println("入库主表result"+result);
		connection.commit();
		System.out.println("// 入库主表 %");
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();
			return result; 
		}
		// 入库细表  %
		TParm inWareHouseD = parm.getParm("DEV_INWAREHOUSED");  
		System.out.println("inWareHouseD"+inWareHouseD);
	    for (int i = 0; i < inWareHouseD.getCount("INWAREHOUSE_NO"); i++) { 
	    	  System.out.println("// 入库细表  %");
		result = DevInStorageTool.getInstance().insertDevInwarehouseD(inWareHouseD.getRow(i), connection);
		System.out.println("入库细表 result"+result); 
		
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();  
			return result;       
		}     
		connection.commit();
		  }  
		// 入库明细表 % ORGIN_CODE
	    TParm inWareHouseDD = parm.getParm("DEV_INWAREHOUSEDD");
	    System.out.println("inWareHouseDD"+inWareHouseDD);
	    for (int i = 0; i < inWareHouseDD.getCount("INWAREHOUSE_NO"); i++) { 
	    	System.out.println("// 入库明细表 % ORGIN_CODE");
//	    	if(inWareHouseDD.getBoolean(""))
		result = DevInStorageTool.getInstance().insertDevInwarehouseDD(inWareHouseDD.getRow(i), connection);
		System.out.println("入库明细表result"+result);
		connection.commit(); 
		if (result.getErrCode() < 0) { 
			//connection.rollback();
			connection.close();
			return result;       
		}   
	    }
	    
	   	    
		// 库存主表(插入或更新)  %
	    TParm stockM = parm.getParm("DEV_STOCKM");
	    for (int i = 0; i < stockM.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertStockM(stockM.getRow(i), connection);
		System.out.println(" 库存主表result"+result);
		connection.commit();
		if (result.getErrCode() < 0) { 
			//connection.rollback();
			connection.close();
			return result;                            }   
	    }
	    System.out.println("// 库存主表(插入或更新)  %");
		// 库存细表  %
        TParm stockD = parm.getParm("DEV_STOCKD");
        for (int i = 0; i < stockD.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertStockD(stockD.getRow(i), connection);
		System.out.println(" 库存细表result"+result);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close(); 
			return result;    
		            }              
        }
        System.out.println("// 库存细表  %");
		// 库存明细表--序号管理设备  % 
        TParm stockDD = parm.getParm("DEV_STOCKDD");
        for (int i = 0; i < stockDD.getCount("DEV_CODE"); i++) {  
		result = DevInStorageTool.getInstance().UpdateStockDD(stockDD.getRow(i), connection);
		System.out.println(" 库存明细表result"+result);
		connection.commit(); 
		if (result.getErrCode() < 0) {
			//connection.rollback(); 
			connection.close(); 
			return result; 
		   }          
        } 
        System.out.println("// 库存明细表--序号管理设备  % ");
		return result;
	}

	
	
	
	
	
	
	
	
	
	
	/**
	 * 设备入库保存(update)(序号   非序号)
	 * 
	 * @param parm 
	 *            TParm
	 * @return TParm     
	 */ 
	public TParm updateInStorageReceipt(TParm parm) {
		TParm result = new TParm();

		TConnection connection = getConnection();
		// 入库主表
		result = DevInStorageTool.getInstance().updateDevInwarehouseM(parm.getParm("DEV_INWAREHOUSEM"), connection);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();
			return result;
		}
		
		
		// 入库细表 
		TParm inWareHouseD = parm.getParm("DEV_INWAREHOUSED");
	    for (int i = 0; i < inWareHouseD.getCount("INWAREHOUSE_NO"); i++) { 
		result = DevInStorageTool.getInstance().updateDevInwarehouseD(inWareHouseD.getRow(i), connection);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();
			return result;       
		}       
		  } 
		// 入库明细表
	    TParm inWareHouseDD = parm.getParm("DEV_INWAREHOUSEDD");
	    for (int i = 0; i < inWareHouseDD.getCount("INWAREHOUSE_NO"); i++) { 
		result = DevInStorageTool.getInstance().updateDevInwarehouseDD(inWareHouseDD.getRow(i), connection);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();
			return result;       
		}   
	    }
		// 库存主表(插入或更新)  是否更新？
	    TParm stockM = parm.getParm("DEV_STOCKM");
	    for (int i = 0; i < stockM.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertOrUpdateStockM(stockM.getRow(i), connection);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback(); 
			connection.close();
			return result;                            }   
	    }
		  
		// 库存细表  更新库存
        TParm stockD = parm.getParm("DEV_STOCKD"); 
        for (int i = 0; i < stockD.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertOrUpdateStockD(stockD.getRow(i), connection);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback(); 
			connection.close();
			return result;   
		            }              
        } 
		// 库存明细表--序号管理设备？？？
        TParm stockDD = parm.getParm("DEV_STOCKDD"); 
        for (int i = 0; i < stockDD.getCount("DEV_CODE"); i++) { 
		result = DevInStorageTool.getInstance().insertOrUpdateStockDD(stockDD.getRow(i), connection);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();
			return result;
		   }         
        }
		return result;
	}	
	/**
	 * 设备请领保存(插入)
	 * @param parm
	 * @return 
	 */
	public TParm InsertRequest(TParm parm) {  
		System.out.println("STARTTTTTTT--INSERT"); 
		TParm result = new TParm(); 
		TConnection connection = getConnection(); 
		// 插入请领主表 
		result = DevOutRequestMTool.getInstance().createNewRequestM(parm.getParm("DEV_REQUESTMM"), connection);
		System.out.println("主result"+result); 
		if (result.getErrCode() < 0) {
			connection.close();  
			return result;   
		}    
		// 插入请领明细表 
		result = DevOutRequestDTool.getInstance().createNewRequestD(parm.getParm("DEV_REQUESTDD"), connection);
		System.out.println("细result"+result);
		if (result.getErrCode() < 0) { 
			connection.close();   
			return result;   
		}   
		
		connection.commit();
		return result;

	}
	/**
	 * 设备请领保存(更新)
	 * @param parm
	 * @return
	 */
	public TParm UpdateRequest(TParm parm) {
		System.out.println("STARTTTTTTT----UPDATE"); 
		TParm result = new TParm();
		TConnection connection = getConnection(); 
		// 更新请领主表    
		result = DevOutRequestMTool.getInstance().updateRequestM(parm.getParm("DEV_REQUESTMM"), connection);
		if (result.getErrCode() < 0) {  
			connection.close();   
			return result;    
		}
		// 更新请领明细表  
		result = DevOutRequestDTool.getInstance().updateRequestD(parm.getParm("DEV_REQUESTDD"), connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}   
//	    // 更新请领明细表状态
//	    result = DevOutRequestDTool.getInstance().updateRequestDDFlg(parm.getParm("DEV_REQUESTDD"), connection);
//			if (result.getErrCode() < 0) {
//				connection.close();
//				return result;
//				}
		connection.close();
		connection.commit();
		return result;
	}
	/**
	 * 设备出库保存(new)
	 * @param parm
	 * @return
	 */
	public TParm generateExStorageReceipt(TParm parm) {
		TParm result = new TParm();
		
		TConnection connection = getConnection();
		// 出库主表
		result = DevOutStorageTool.getInstance().insertExStorgeM(parm.getParm("DEV_EXWAREHOUSEM"), connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		
		TParm ParmD = parm.getParm("DEV_EXWAREHOUSED"); 
		System.out.println("出库明细表ParmD"+ParmD);
		// 出库明细表
		result = DevOutStorageTool.getInstance().insertExStorgeD(ParmD, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;  
		}     
		TParm ParmDD = parm.getParm("DEV_EXWAREHOUSEDD");
		System.out.println("出库序号明细表ParmDD"+ParmDD); 
		// 出库序号明细表
		result = DevOutStorageTool.getInstance().insertExStorgeDD(ParmDD, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;  
		} 

		// 减库存主表，细表，和插入DD表数量(未完成) 
		TParm StockMParm = parm.getParm("DEV_STOCKM");  


		result = DevOutStorageTool.getInstance().deductStockM(StockMParm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;  
		}  
		//fux need modify
//		TParm StockDParm = parm.getParm("DEV_STOCKD"); 
//		result = DevOutStorageTool.getInstance().deductStockD(StockDParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;  
//		}  
		//待查
		TParm StockDDParm = parm.getParm("DEV_STOCKDD");  
		result = DevOutStorageTool.getInstance().updateStockdd(StockDDParm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;  
		}  
////		result = DevOutStorageTool.getInstance().updateStockMD(tmepParm, connection);
//		TParm requestMParm = parm.getParm("DEV_REQUESTM"); 
//		TParm requestDParm = parm.getParm("DEV_REQUESTD"); 
//		//更新请领主表
//		result = DevOutStorageTool.getInstance().UpdateRequsetMFinal(requestMParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;    
//		}  
//		//更新请领细表
//		result = DevOutStorageTool.getInstance().UpdateRequsetDFate(requestDParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;  
//		}  
		connection.commit();  
		return result;
	}
	/**
	 * 设备出库保存(update)
	 * @param parm
	 * @return
	 */
	public TParm updateExStorageReceipt(TParm parm) {
		TParm result = new TParm();
		
		TConnection connection = getConnection();
		// 出库主表
		result = DevOutStorageTool.getInstance().insertExStorgeM(parm.getParm("DEV_EXWAREHOUSEM"), connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		
		TParm tmepParm = parm.getParm("DEV_EXWAREHOUSED");
		// 出库明细表
		result = DevOutStorageTool.getInstance().insertExStorgeD(tmepParm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// 减库存主表数量(未完成)
//		result = DevOutStorageTool.getInstance().deductStockM(tmepParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;  
//		}  
//		// 序号管理 更新明细表数量；非序号管理 更新/插入主表数据
//		result = DevOutStorageTool.getInstance().updateStockMD(tmepParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;  
//		} 
		// 修改DEV_RFIDBASE表所属科室信息
//		result = DevOutStorageTool.getInstance().updateRFIDBase(tmepParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;
//		}  
		  
		connection.commit();
		return result;
	}
	
	
	/**
	 * 设备追踪 出库
	 * @param parm
	 * @return
	 */
	public TParm onTrackOut(TParm parm) {
		TParm result = new TParm();
//		TConnection connection = getConnection(); 
		// 插入设备追踪表
		result = DevTrackTool.getInstance().insertDevTrackInf(parm.getParm("OUT"));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// 更新DEV_RFIDBASE表 状态为正常
		result = DevTrackTool.getInstance().updateDevRFIDBaseOnpass(parm.getParm("OUT"));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
	//	connection.commit();
		return result;
	}
	
	
	/**
	 * 设备追踪 入库
	 * @param parm
	 * @return
	 */
	public TParm onTrackIn(TParm parm) {
		TParm result = new TParm();
	//	TConnection connection = getConnection();
		System.out.println("1:" + parm);
		// 插入设备追踪表
		result = DevTrackTool.getInstance().insertDevTrackInf(parm.getParm("IN"));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		System.out.println("2:" + parm);
		// 更新DEV_RFIDBASE表 状态为正常
		result = DevTrackTool.getInstance().updateDevRFIDBaseNormal(parm.getParm("IN"));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
//		connection.commit();
		return result;
	}
	
	
	/**
	 * 根据盘点结果更新库存表
	 * @param parm
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public TParm updateStockByCheck(TParm parm) {
		TParm result = new TParm();
		TConnection connection = getConnection();
		List<TParm> parmList = (List<TParm>)parm.getData("update");
		result = DevCheckTool.getInstance().updateStockByCheck(parmList, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		return result;
	}
	
	
	/**
     * 设备请购保存
     * @param parm TParm
     * @return TParm
     */
    public TParm saveDevRequest(TParm parm){ 
        TParm result = new TParm(); 
        TConnection connection = getConnection();
        result = DevRequestTool.getInstance().saveDevRequest(parm,connection);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        connection.commit();
        return result;
    }
} 
