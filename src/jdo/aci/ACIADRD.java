package jdo.aci;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;
/**
 * <p>Title: 不良反应药品信息 </p>
 *
 * <p>Description: 不良反应药品信息 </p>
 *
 * <p>Copyright: 2013 </p>
 *
 * <p>Company:BlueCore </p>
 *
 * @author wanglong 2013.09.30
 * @version 1.0
 */
public class ACIADRD extends TDataStore {

    /**
     * 查询事件
     *
     * @return
     */
    public int onQuery() {
        this.setSQL("SELECT * FROM ACI_ADRD ORDER BY ACI_NO,TYPE,SEQ");
        return 0;
    }

    /**
     * 根据ACI_NO初始化对象
     * 
     * @param aciNo
     * @return
     */
    public int onQueryByACINo(String aciNo) {
        if (StringUtil.isNullString(aciNo)) {
            return -1;
        }
        this.setSQL("SELECT * FROM ACI_ADRD WHERE ACI_NO='#' ORDER BY ACI_NO,TYPE,SEQ"
                .replaceFirst("#", aciNo));
        int result = this.retrieve();
        if (result < 0) {
            return result;
        }
        return 0;
    }

    /**
     * 根据ACI_NO和TYPE初始化对象
     * @param aciNo
     * @param type
     * @return
     */
    public int onQueryByACINoAndType(String aciNo,String type) {
        if (StringUtil.isNullString(aciNo)||StringUtil.isNullString(type)) {
            return -1;
        }
        this.setSQL("SELECT * FROM ACI_ADRD WHERE ACI_NO='#' AND TYPE='#' ORDER BY ACI_NO,TYPE,SEQ"
                .replaceFirst("#", aciNo).replaceFirst("#", type));
        int result = this.retrieve();
        if (result < 0) {
            return result;
        }
        return 0;
    }

    /**
     * 获得下一个SEQ_NO
     * @return
     */
    public int getNextSeqNo() {
        TParm buff = new TParm();
        if (isFilter()) {
            buff = this.getBuffer(FILTER);
        } else {
            buff = getBuffer(PRIMARY);
        }
        int count = buff.getCount();
        int seq = 0;
        for (int i = 0; i < count; i++) {
            int x = buff.getInt("SEQ", i);
            if (seq < x) seq = x;
        }
        return seq + 1;
    }
    
    /**
     * 是否有新行
     * 
     * @return boolean
     */
    public int isNewRow() {
        int rowCount = this.rowCount();
        for (int i = 0; i < rowCount; i++) {
            if (!this.isActive(i)) return i;
        }
        return -1;
    }

    /**
     * 获得药品信息
     * @param row
     * @return
     */
    public TParm getPhaOtherData(int row){
        String orderCode = this.getItemString(row, "ORDER_CODE");
        String sql = "SELECT * FROM PHA_BASE WHERE ORDER_CODE = '" + orderCode + "' ";
        // System.out.println("getPhaOtherData.sql================"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
//    /**
//     * 获得药品信息
//     * @param row
//     * @return
//     */
//    public TParm getBatchNoRangeData(int row) {
//        String orderCode = this.getItemString(row, "ORDER_CODE");
//        String sql =
//                "SELECT DISTINCT BATCH_NO FROM IND_STOCK WHERE ORDER_CODE = '" + orderCode + "' ";
//        // System.out.println("getBatchNoRangeData.sql================"+sql);
//        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//        return result;
//    }
  
}
