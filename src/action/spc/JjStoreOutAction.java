package action.spc;

import java.util.List;
import java.util.Set;

import jdo.spc.INDTool;
import jdo.spc.JjStoreOutTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class JjStoreOutAction extends TAction {

	public TParm JjStockOut(TParm parm) {
		TConnection conn = getConnection();
		TParm result = JjStoreOutTool.getInstance().JjStockOut(parm, conn);
		 conn.commit();
	     conn.close();
		return result;
	}
	
	 /**
     * Èë¿âÐÂÔö
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertIn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseIn(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
		return result;
    }
	
}
