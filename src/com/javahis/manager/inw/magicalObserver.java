package com.javahis.manager.inw;

import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDS;
import com.dongyang.data.TParm;

/**
 * <p>Title: �۲�ٵ�TDS�����Թ۲��κ�����޸��κ����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: 2008</p>
 *
 * <p>Company: JAVAHIS 1.0</p>
 *
 * @author ZangJH
 * @version 1.0
 */


public class magicalObserver
    extends TObserverAdapter {
    public magicalObserver() {
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
        //��Ҫ�ж���ֵ�Ƿ����0.0����Ϊ��ܻ��Զ������ݿ���NUMBER��������Ϊnull���0.0��
        //��1��
        if (row == 0 && column.equals("A")) {
            double temp = realDS.getItemDouble(0, "TEMPERATURE");
            //���Ϊ0.0��table����ʾ��
            return temp == 0.0 ? null : temp;
        }
        if (row == 0 && column.equals("B")) {
            double temp = realDS.getItemDouble(1, "TEMPERATURE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 0 && column.equals("C")) {
            double temp = realDS.getItemDouble(2, "TEMPERATURE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 0 && column.equals("D")) {
            double temp = realDS.getItemDouble(3, "TEMPERATURE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 0 && column.equals("E")) {
            double temp = realDS.getItemDouble(4, "TEMPERATURE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 0 && column.equals("F")) {
            double temp = realDS.getItemDouble(5, "TEMPERATURE");
            return temp == 0.0 ? null : temp;
        }
        //��2��
        if (row == 1 && column.equals("A")) {
            double temp = realDS.getItemDouble(0, "PLUSE");
            //���Ϊ0.0��table����ʾ��
            return temp == 0.0 ? null : temp;
        }
        if (row == 1 && column.equals("B")) {
            double temp = realDS.getItemDouble(1, "PLUSE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 1 && column.equals("C")) {
            double temp = realDS.getItemDouble(2, "PLUSE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 1 && column.equals("D")) {
            double temp = realDS.getItemDouble(3, "PLUSE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 1 && column.equals("E")) {
            double temp = realDS.getItemDouble(4, "PLUSE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 1 && column.equals("F")) {
            double temp = realDS.getItemDouble(5, "PLUSE");
            return temp == 0.0 ? null : temp;
        }
        //��3��
        if (row == 2 && column.equals("A")) {
            double temp = realDS.getItemDouble(0, "RESPIRE");
            //���Ϊ0.0��table����ʾ��
            return temp == 0.0 ? null : temp;
        }
        if (row == 2 && column.equals("B")) {
            double temp = realDS.getItemDouble(1, "RESPIRE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 2 && column.equals("C")) {
            double temp = realDS.getItemDouble(2, "RESPIRE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 2 && column.equals("D")) {
            double temp = realDS.getItemDouble(3, "RESPIRE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 2 && column.equals("E")) {
            double temp = realDS.getItemDouble(4, "RESPIRE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 2 && column.equals("F")) {
            double temp = realDS.getItemDouble(5, "RESPIRE");
            return temp == 0.0 ? null : temp;
        }

        //��4�� ����ѹ
        if (row == 3 && column.equals("A")) {
            double temp = realDS.getItemDouble(0, "DIASTOLICPRESSURE");
            //���Ϊ0.0��table����ʾ��
            return temp == 0.0 ? null : temp;
        }
        if (row == 3 && column.equals("B")) {
            double temp = realDS.getItemDouble(1, "DIASTOLICPRESSURE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 3 && column.equals("C")) {
            double temp = realDS.getItemDouble(2, "DIASTOLICPRESSURE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 3 && column.equals("D")) {
            double temp = realDS.getItemDouble(3, "DIASTOLICPRESSURE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 3 && column.equals("E")) {
            double temp = realDS.getItemDouble(4, "DIASTOLICPRESSURE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 3 && column.equals("F")) {
            double temp = realDS.getItemDouble(5, "DIASTOLICPRESSURE");
            return temp == 0.0 ? null : temp;
        }

        //��5�� ����ѹ
        if (row == 4 && column.equals("A")) {
            double temp = realDS.getItemDouble(0, "SYSTOLICPRESSURE");
            //���Ϊ0.0��table����ʾ��
            return temp == 0.0 ? null : temp;
        }
        if (row == 4 && column.equals("B")) {
            double temp = realDS.getItemDouble(1, "SYSTOLICPRESSURE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 4 && column.equals("C")) {
            double temp = realDS.getItemDouble(2, "SYSTOLICPRESSURE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 4 && column.equals("D")) {
            double temp = realDS.getItemDouble(3, "SYSTOLICPRESSURE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 4 && column.equals("E")) {
            double temp = realDS.getItemDouble(4, "SYSTOLICPRESSURE");
            return temp == 0.0 ? null : temp;
        }
        if (row == 4 && column.equals("F")) {
            double temp = realDS.getItemDouble(5, "SYSTOLICPRESSURE");
            return temp == 0.0 ? null : temp;
        }

        //System.out.println("getOtherColumnValue " + row + " " + column);
        return "AAA";
    }


    /**
     * ��������������
     * @param parm TParm
     * @param row int �����ö�������(table)
     * @param column String �����ö�������(table)
     * @param value Object
     * @return boolean ����Ŀǰ��ֵ
     */

    public boolean setOtherColumnValue(TDS odiDspnm, TParm parm, int row,
                                       String column, Object value) {
        System.out.println("==========================");
        TDS realDS = this.getDS();
        //��Ҫ�ж���ֵ�Ƿ����0.0����Ϊ��ܻ��Զ������ݿ���NUMBER��������Ϊnull���0.0��
        //��1��
        if (row == 0 && column.equals("A")) {
            realDS.setItem(0,"TEMPERATURE",value);
            return true;
        }
        if (row == 0 && column.equals("B")) {
            realDS.setItem(1,"TEMPERATURE",value);
            return true;
        }
        if (row == 0 && column.equals("C")) {
            realDS.setItem(2,"TEMPERATURE",value);
            return true;
        }
        if (row == 0 && column.equals("D")) {
            realDS.setItem(3,"TEMPERATURE",value);
            return true;
        }
        if (row == 0 && column.equals("E")) {
            realDS.setItem(4,"TEMPERATURE",value);
            return true;
        }
        if (row == 0 && column.equals("F")) {
            realDS.setItem(5,"TEMPERATURE",value);
            return true;
        }

        //��2��
        if (row == 1 && column.equals("A")) {
            realDS.setItem(0,"PLUSE",value);
            return true;
        }
        if (row == 1 && column.equals("B")) {
            realDS.setItem(1,"PLUSE",value);
            return true;
        }
        if (row == 1 && column.equals("C")) {
            realDS.setItem(2,"PLUSE",value);
            return true;
        }
        if (row == 1 && column.equals("D")) {
            realDS.setItem(3,"PLUSE",value);
            return true;
        }
        if (row == 1 && column.equals("E")) {
            realDS.setItem(4,"PLUSE",value);
            return true;
        }
        if (row == 1 && column.equals("F")) {
            realDS.setItem(5,"PLUSE",value);
            return true;
        }
        //��3��
        if (row == 2 && column.equals("A")) {
            realDS.setItem(0,"RESPIRE",value);
            return true;
        }
        if (row == 2 && column.equals("B")) {
            realDS.setItem(1,"RESPIRE",value);
            return true;
        }
        if (row == 2 && column.equals("C")) {
            realDS.setItem(2,"RESPIRE",value);
            return true;
        }
        if (row == 2 && column.equals("D")) {
            realDS.setItem(3,"RESPIRE",value);
            return true;
        }
        if (row == 2 && column.equals("E")) {
            realDS.setItem(4,"RESPIRE",value);
            return true;
        }
        if (row == 2 && column.equals("F")) {
            realDS.setItem(5,"RESPIRE",value);
            return true;
        }

        //��4�� ����ѹ
        if (row == 3 && column.equals("A")) {
            realDS.setItem(0,"DIASTOLICPRESSURE",value);
            return true;
        }
        if (row == 3 && column.equals("B")) {
            realDS.setItem(1,"DIASTOLICPRESSURE",value);
            return true;
        }
        if (row == 3 && column.equals("C")) {
            realDS.setItem(2,"DIASTOLICPRESSURE",value);
            return true;
        }
        if (row == 3 && column.equals("D")) {
            realDS.setItem(3,"DIASTOLICPRESSURE",value);
            return true;
        }
        if (row == 3 && column.equals("E")) {
            realDS.setItem(4,"DIASTOLICPRESSURE",value);
            return true;
        }
        if (row == 3 && column.equals("F")) {
            realDS.setItem(5,"DIASTOLICPRESSURE",value);
            return true;
        }

        //��5�� ����ѹ
        if (row == 4 && column.equals("A")) {
            realDS.setItem(0,"SYSTOLICPRESSURE",value);
            return true;
        }
        if (row == 4 && column.equals("B")) {
            realDS.setItem(1,"SYSTOLICPRESSURE",value);
            return true;
        }
        if (row == 4 && column.equals("C")) {
            realDS.setItem(2,"SYSTOLICPRESSURE",value);
            return true;
        }
        if (row == 4 && column.equals("D")) {
            realDS.setItem(3,"SYSTOLICPRESSURE",value);
            return true;
        }
        if (row == 4 && column.equals("E")) {
            realDS.setItem(4,"SYSTOLICPRESSURE",value);
            return true;
        }
        if (row == 4 && column.equals("F")) {
            realDS.setItem(5,"SYSTOLICPRESSURE",value);
            return true;
        }

        System.out.println("setOtherColumnValue " + row + " " + column + " " +
                           value);
        return true;
    }

}
