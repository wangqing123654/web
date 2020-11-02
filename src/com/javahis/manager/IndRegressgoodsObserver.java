package com.javahis.manager;

import java.util.Vector;

import jdo.spc.INDTool;
import jdo.spc.IndVerifyinDTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;

/**
 * <p>
 * Title: ҩ�ⲿ���˻�����۲���
 * </p>
 *
 * <p>
 * Description: ҩ�ⲿ���˻�����۲���
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.05.21
 * @version 1.0
 */

public class IndRegressgoodsObserver
    extends TObserverAdapter {

    // ���prefech��������
    TDataStore dataStore;

    String org_code = "";

    public IndRegressgoodsObserver() {
        dataStore = new TDataStore();
        dataStore.setSQL(
            "SELECT ORDER_CODE ,ORDER_DESC ,SPECIFICATION FROM SYS_FEE");
        dataStore.retrieve();
    }

    public IndRegressgoodsObserver(String org_code) {
        this.org_code = org_code;
        dataStore = new TDataStore();
        dataStore.setSQL(
            "SELECT ORDER_CODE ,ORDER_DESC ,SPECIFICATION FROM SYS_FEE");
        dataStore.retrieve();
    }

    /**
     *
     * @param s
     *            ���ݵ�ORDER_CODE
     * @param colName
     *            Ҫ�������
     * @return String
     */
    public String getTableShowValue(String s, String colName) {
        if (dataStore == null)
            return s;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER
            : dataStore.PRIMARY;
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
     *
     * @param parm
     *            TParm
     * @param row
     *            int
     * @param column
     *            String
     * @return Object
     */
    public Object getOtherColumnValue(TDS ds, TParm parm, int row,
                                      String column) {
        //System.out.println("parm===" + parm);
        if ("SELECT_FLG".equals(column)) {
            if ("".equals(parm.getValue("UPDATE_FLG", row))) {
                return "N";
            }
            else {
                return "Y";
            }
        }
        if ("ORDER_DESC".equals(column)) {
            String orderCode = parm.getValue("ORDER_CODE", row);
            String icd_name = getTableShowValue(orderCode, "ORDER_DESC");
            if ("".equals(icd_name))
                return "";
            // �������һ��ֵ
            return icd_name;
        }
        // ҩƷ���
        if ("SPECIFICATION".equals(column)) {
            String orderCode = parm.getValue("ORDER_CODE", row);
            String icd_name = getTableShowValue(orderCode, "SPECIFICATION");
            if ("".equals(icd_name))
                return "";
            // �������һ��ֵ
            return icd_name;
        }
        // ���۽��
        if ("SELL_SUM".equals(column)) {
            double price = parm.getDouble("RETAIL_PRICE", row);
            double amount1 = parm.getDouble("QTY", row);
            return StringTool.round(price * amount1, 2);
        }

        // �������
        if ("DIFF_SUM".equals(column)) {
            double price = parm.getDouble("RETAIL_PRICE", row);
            double amount1 = parm.getDouble("QTY", row);
            double amt1 = StringTool.round(price * amount1, 2);
            double amt2 = parm.getDouble("AMT", row);
            return amt1 - amt2;
        }

        // �����
        if ("SUM_QTY".equals(column)) {
            String orderCode = parm.getValue("ORDER_CODE", row);
            String batch_no = parm.getValue("BATCH_NO", row);
            if ("".equals(parm.getValue("VALID_DATE", row))) {
                return 0;
            }
            else {
                String valid_date = parm.getValue("VALID_DATE", row).substring(0,
                    10);
                double qty = INDTool.getInstance().getStockQTYReturn(org_code, orderCode,
                    batch_no, valid_date, Operator.getRegion());
                //System.out.println("org_code---" + org_code);
                //System.out.println("qty---" + qty);
                return qty;
            }
        }

        // �ۼ��˻���
        if ("VER_ACTUAL_QTY".equals(column)) {
            String ver_no = parm.getValue("VERIFYIN_NO", row);
            String ver_seq = parm.getValue("VERSEQ_NO", row);
            TParm inparm = new TParm();
            inparm.setData("VERIFYIN_NO", ver_no);
            inparm.setData("SEQ_NO", ver_seq);
            TParm result = IndVerifyinDTool.getInstance().onQuery(inparm);
            return result.getDouble("ACTUAL_QTY", 0);
        }
        //luhai 2012-2-13 ����ɱ���� begin
        if ("VERIFYIN_AMT".equals(column)) {
        	double verifyinPrice = parm.getDouble("VERIFYIN_PRICE",row);
        	double qty  = parm.getDouble("QTY",row);
        	return StringTool.round(verifyinPrice*qty, 2)+"";
        }
        //luhai 2012-2-13 ����ɱ���� begin
        return "";
    }

    /**
     * ��������������
     *
     * @param parm
     *            TParm
     * @param row
     *            int �����ö�������(table)
     * @param column
     *            String �����ö�������(table)
     * @param value
     *            Object
     * @return boolean ����Ŀǰ��ֵ
     */
    public boolean setOtherColumnValue(TDS ds, TParm parm, int row,
                                       String column, Object value) {
        if (column.equals("ORDER")) {
            ds.setItem(row, "ORDER_CODE", parm.getValue("ORDER", row));
            return true;
        }
        if (column.equals("SELECT_FLG")) {
            if ("Y".equals(TypeTool.getString(value))) {
                ds.setActive(row, true);
            }
            else {
                ds.setActive(row, false);
            }
            return true;
        }
        return true;
    }
}
