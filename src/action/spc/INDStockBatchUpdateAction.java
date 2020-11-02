package action.spc;

import jdo.spc.INDTool;
import jdo.spc.IndStockBatchUpdateTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class INDStockBatchUpdateAction extends TAction {
	
    public INDStockBatchUpdateAction() {
    }
    
    /**
     * ָ������ҩƷ����ֶ��ս�������ҵ
     * @param parm TParm
     * @return TParm
     */
    public TParm onIndDDStockBatch(TParm parm) {
    	   TConnection conn = getConnection();
           TParm result = new TParm();
           result = IndStockBatchUpdateTool.getInstance().onReStockBatch(parm, conn);
           if (result.getErrCode() < 0) {
               err("ERR:" + result.getErrCode() + result.getErrText()
                   + result.getErrName());
               conn.rollback();
               conn.close();
               return result;
           }
           conn.commit();
           conn.close();
           return result;
    }
	
}
