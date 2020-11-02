package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ҩƷ����ת��
 * </p>
 *
 * <p>
 * Description: ҩƷ����ת��
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company: BLUECORE
 * </p>
 *
 * @author fuwj 2012.12.19
 * @version 1.0
 */
public class SPCSysFeeTool extends TJDOTool {
	
	 /**
     * ʵ��
     */
    public static SPCSysFeeTool instanceObject;				

    /**
     * �õ�ʵ��							
     *				
     * @return IndStockMTool					
     */
    public static SPCSysFeeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCSysFeeTool();
        return instanceObject;
    }
  
    /**
     * ������
     */
    public SPCSysFeeTool() {
    	setModuleName("spc\\SPCSysFeeModule.x");
        onInit();
    }
    
    /**
     * ��ѯҩƷ��Ϣ
     * @param tparm    
     * @return
     */
    public TParm queryInfo(TParm tparm) {
    	TParm result = query("queryInfo", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**
     * ����SYS_FEE��ҩƷ����
     * @param parm
     * @param conn
     * @return
     */
    public TParm updateSysFee(TParm parm,TConnection conn) {
    	TParm result = update("updateSysFee",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
   /**
    * ����PHA_BASE��ҩƷ���� 
    * @param parm
    * @param conn
    * @return
    */
    public TParm updatePhaBase(TParm parm,TConnection conn) {
    	TParm result = update("updatePhaBase",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**
     * ����PHA_TRANSUNIT��ҩƷ����
     * @param parm
     * @param conn
     * @return
     */
    public TParm updatePhaTransunit(TParm parm,TConnection conn) {
    	TParm result = update("updatePhaTransunit",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**
     * ���SYS_FEE_SPC
     * @param parm
     * @param conn
     * @return
     */
    public TParm insertSPCSysFee(TParm parm,TConnection conn) {
    	TParm result = update("insertSPCSysFee",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;
    }
    
    /**
     * ��ѯ������ҩƷ��Ϣ
     * @param tparm    
     * @return
     */
    public TParm querySpcSysFee(TParm tparm) {
    	TParm result = query("querySpcSysFee", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    
    /**
     * ɾ��SYS_FEEδ����������
     * @param parm
     * @param conn
     * @return
     */
    public TParm deleteSysFee(TParm parm,TConnection conn) {
    	TParm result = update("deleteSysFee",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;
    }
    
    /**
     * ɾ��PHA_BASEδ����������
     * @param parm
     * @param conn
     * @return
     */
    public TParm deletePhaBase(TParm parm,TConnection conn) {
    	TParm result = update("deletePhaBase",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;
    }
    
    /**
     * ɾ��PHA_TRANSUNITδ����������
     * @param parm
     * @param conn
     * @return
     */
    public TParm deletePhaTransunit(TParm parm,TConnection conn) {
    	TParm result = update("deletePhaTransunit",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;
    }
    
    
    /**
     * ����his��ҩƷ�����ѯ��������ҩƷ����
     * @param tparm    
     * @return
     */
    public TParm querySysFeeSpc(TParm tparm) {
    	TParm result = query("querySysFeeSpc", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    
    /**
     * ����updateIndStock
     * @param parm
     * @param conn
     * @return
     */
    public TParm updateIndStock(TParm parm,TConnection conn) {
    	TParm result = update("updateIndStock",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;
    }
    
    /**
     * ����updateIndStock
     * @param parm
     * @param conn
     * @return
     */
    public TParm updateIndStockM(TParm parm,TConnection conn) {
    	TParm result = update("updateIndStockM",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;
    }
    
    
    /**
     * ����his��ҩƷ�����ѯ��������ҩƷ����
     * @param tparm    
     * @return
     */
    public TParm queryHisOrderCode(TParm tparm) {
    	TParm result = query("queryHisOrderCode", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**
     * ����updateIndAgent
     * @param parm
     * @param conn
     * @return 
     */
    public TParm updateIndAgent(TParm parm,TConnection conn) {
    	TParm result = update("updateIndAgent",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result; 
    }
    
    /**
     * ����updateSysFeeHistory
     * @param parm
     * @param conn
     * @return   
     */
    public TParm updateSysFeeHistory(TParm parm,TConnection conn) {
    	TParm result = update("updateSysFeeHistory",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    /**
     * ���SysFeeSpc������
     * @param parm
     * @param conn
     * @return
     */
    public TParm insertSysFeeSpc(TParm parm) {
    	TParm result = update("insertSysFeeSpc",parm);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    /**
     * ɾ��SysFeeSpc������
     * @param parm
     * @param conn
     * @return
     */
    public TParm deleteSysFeeSpc(TParm parm) {
    	TParm result = update("deleteSysFeeSpc",parm);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    /**
     * ����SysFeeSpc������
     * @param parm
     * @param conn
     * @return
     */
    public TParm updateSysFeeSpc(TParm parm) {
    	TParm result = update("updateSysFeeSpc",parm);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    
    /**
     * ����SysFeeSpc������
     * @param parm
     * @param conn
     * @return
     */
    public TParm indStockInsert(TParm parm,TConnection conn) {
    	TParm result = update("indStockInsert",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    
    public TParm updateQty(TParm parm,TConnection conn) {
    	TParm result = update("updateQty",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    public TParm updateTag(TParm parm,TConnection conn) {
    	TParm result = update("updateTag",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    public TParm searchTag(TParm parm) {
    	TParm result = query("searchTag",parm);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    public TParm updatePackUnit(TParm parm,TConnection conn) {
    	TParm result = update("updatePackUnit",parm,conn);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;  
    }
    
    public TParm insertIndAgent(TParm parm) {
    	TParm result = update("insertIndAgent",parm);
    	if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}   
		return result;   
    }
	
}
