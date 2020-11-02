package jdo.aci;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: 药品不良反应名称</p>
 *
 * <p>Description: 药品不良反应名称</p>
 *
 * <p>Copyright: 2013 </p>
 *
 * <p>Company:BlueCore </p>
 *
 * @author wanglong 2013.09.30
 * @version 1.0
 */
public class ACIADRName extends TDataStore {

    /**
     * 查询事件
     *
     * @return
     */
    public int onQuery() {
        this.setSQL("SELECT * FROM ACI_ADRNAME ORDER BY ADR_CODE,ORGAN_CODE1");
        return 0;
    }

    /**
     * 得到一个新的ADR_ID
     * @param row
     * @return
     */
    public String getNewId(int row) {
        String adrCode = this.getItemString(row, "ADR_CODE");
        String organCode1 = this.getItemString(row, "ORGAN_CODE1");
        String sql =
                "SELECT LPAD(ADR_ID+1,10,'0') ID FROM ACI_ADRNAME WHERE ADR_CODE||ORGAN_CODE1='#'";
        sql = sql.replaceFirst("#", adrCode + organCode1);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            return "" + result.getErrCode();
        }
        String adrID = result.getValue("ADR_ID", 0);
        if (adrID.equals("")) {
            adrID = adrCode + organCode1 + "01";
        }
        return adrID;
    }
  
}
