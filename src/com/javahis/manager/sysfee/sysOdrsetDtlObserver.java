package com.javahis.manager.sysfee;

import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDS;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import java.util.Vector;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: ����ҽ��ϸ��Ĺ۲�</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS 1.0 </p>
 *
 * <p>Company: JAVAHIS 1.0</p>
 *
 * @author ZangJH 2009-9-10
 * @version 1.0
 */
public class sysOdrsetDtlObserver
    extends TObserverAdapter {
    public sysOdrsetDtlObserver() {


    }
    //
    TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");
    /**
     *
     * @param s ���ݵ�ORDER_CODE
     * @param colName Ҫ�������
     * @return String
     */
    public String getTableShowValue(String s,String colName) {
        if (dataStore == null)
            return s;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER :
            dataStore.PRIMARY;
        TParm parm = dataStore.getBuffer(bufferString);
        Vector vKey = (Vector) parm.getData("ORDER_CODE");
        Vector vDesc = (Vector) parm.getData(colName);
        int count = vKey.size();
        for (int i = 0; i < count; i++) {
            if (s.equals(vKey.get(i)))
                return "" + vDesc.get(i);
        }
        return s;
    }



    /**
     * �õ�����������
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TDS ds, TParm tParm, int row,
                                      String column) {

        String result = "";
        if (column.equals("N_DEL")) {
            return "";
        }

        //��������N_��ͷ��
        if(!column.startsWith("N_")){
            String orderCode = tParm.getValue("ORDER_CODE", row);
            result = getTableShowValue(orderCode, column);
        }



        if (column.equals("N_TOTFEE")) {
            String orderCode = tParm.getValue("ORDER_CODE", row);
            double ownPrice = TypeTool.getDouble(getTableShowValue(orderCode, "OWN_PRICE"));
            double qty=tParm.getDouble("DOSAGE_QTY", row);
            return ownPrice*qty;
        }
        return result;
    }

    /**
     * ��������������
     * @param parm TParm
     * @param row int �����ö�������(table)
     * @param column String �����ö�������(table)
     * @param value Object
     * @return boolean ����Ŀǰ��ֵ
     */

//    public boolean setOtherColumnValue(TDS ds, TParm parm, int row,
//                                       String column, Object value) {
//        if (column.equals("N_DEL")) {
//            if(TypeTool.getBoolean(value)){
//                ds.deleteRow(row);
//            }
//            return true;
//        }
//        return true;
//    }

}
