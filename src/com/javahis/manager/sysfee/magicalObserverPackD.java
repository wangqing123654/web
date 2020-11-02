package com.javahis.manager.sysfee;

import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDS;
import com.dongyang.data.TParm;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>Title: �۲�ҽ���ײ���������ϸ����õ�table</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JAVAHIS 1.0</p>
 *
 * @author ZangJH 2009-9-10
 * @version 1.0
 */
public class magicalObserverPackD
    extends TObserverAdapter {

    Map selFlg = new HashMap();
    Map qtyMap = new HashMap();

    public magicalObserverPackD() {
    }


    /**
     * �õ�����������
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TDS odiOrder, TParm tParm, int row,
                                      String column) {

        TDS realDS = this.getDS();
        if (column.equals("N_SEL")) {
            return selFlg.get(row * 2 + "");
        }
        if (column.equals("N_ORDER_DESC")) {
            return realDS.getItemData(row * 2, "ORDER_DESC");
        }
        if (column.equals("N_DOSAGE_QTY")) {
            if(qtyMap.get( (row * 2 ) + "")==null){
                return realDS.getItemData((row * 2 ), "DOSAGE_QTY");
            }
            return qtyMap.get( (row * 2 ) + "");

        }
        if (column.equals("N_DOSAGE_UNIT")) {
            return realDS.getItemData(row * 2, "DOSAGE_UNIT");
        }

        if (column.equals("S_SEL")) {
            return selFlg.get( (row * 2 + 1) + "");
        }
        if (column.equals("S_ORDER_DESC")) {
            return realDS.getItemData((row * 2 + 1), "ORDER_DESC");
        }
        if (column.equals("S_DOSAGE_QTY")) {
            //��tableҪˢ�´���������ݵ�ʱ�����Ȼῴ���Ƿ��и����ݣ����û����ʾ�����ݿ��ѯ������ֵ
            if(qtyMap.get( (row * 2 + 1) + "")==null){
                return realDS.getItemData((row * 2 + 1), "DOSAGE_QTY");
            }
            return qtyMap.get( (row * 2 + 1) + "");
        }
        if (column.equals("S_DOSAGE_UNIT")) {
            return realDS.getItemData((row * 2 + 1), "DOSAGE_UNIT");

        }
        return "";
    }


    /**
     * ��������������
     * @param parm TParm
     * @param row int �����ö�������(table)
     * @param column String �����ö�������(table)
     * @param value Object
     * @return boolean ����Ŀǰ��ֵ
     */

    public boolean setOtherColumnValue(TDS ds, TParm parm, int row,
                                       String column, Object value) {
        /**
         * ֻ����������
         */
        //N_SEL��ʱ������TDS�е�����ΪKEY��TDS������=table�ϵ�����*2��
        if (column.equals("N_SEL")) {
            selFlg.put(row * 2 + "", value);
        }
        //S_SEL��ʱ������TDS�е�����ΪKEY��TDS������=table�ϵ�����*2+1��
        if (column.equals("S_SEL")) {
            selFlg.put( (row * 2 + 1) + "", value);
        }

        if (column.equals("N_DOSAGE_QTY")) {
            //��MAP��ֵ���Ա�getʱʹ�������ʱ��ֵ
            qtyMap.put(row * 2 + "", value);
            //�����޸�ds�е�ֵ���Ա�contorl�õ�
            this.getDS().setItem(row * 2 , "DOSAGE_QTY", value);
        }

        if (column.equals("S_DOSAGE_QTY")) {
            qtyMap.put( (row * 2 + 1) + "", value);
            this.getDS().setItem(row * 2 + 1, "DOSAGE_QTY", value);
        }

        return true;
    }

}
