package com.javahis.manager.inw;

import com.dongyang.jdo.TObserverAdapter;
import jdo.sys.Operator;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TDS;
import java.sql.Timestamp;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: ODI_DSPND�Լ���(����ʹ��)  д��ʿ��עʱ��Ҳ��ʹ��</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright:  2008</p>
 *
 * <p>Company: JAVAHIS 1.0</p>
 *
 * @author ZangJH
 * @version 1.0
 */
public class OdiDspnDSelfObserver
    extends TObserverAdapter {
    public OdiDspnDSelfObserver() {
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
        //������������ݿ��б���û�е�ʵ����
        //�������
        if ("ORDER_DATE_DAY".equals(column)) {
            String date = tParm.getValue("ORDER_DATE", row);
            if (date == null)
                return "";
            //�������һ��ֵ
            return date.substring(0,4)+"/"+date.substring(4,6)+"/"+date.substring(6,8);
        }
        //���ʱ��
        if ("ORDER_DATE_TIME".equals(column)) {
            String time = tParm.getValue("ORDER_DATE", row);
            if (time == null)
                return "";
            return time.substring(8,10)+":"+time.substring(10,12);
        }
        //ִ�б��
        if ("EXE_FLG".equals(column)) {
            System.out.println("ִ�б��2");
            //��û�ʿ���ʱ��
            //Timestamp date = tParm.getTimestamp("NS_EXEC_DATE", row);
            String nsExeCode=tParm.getValue("NS_EXEC_CODE", row);
            //��Ϊ�յ�ʱ��˵��û����ˣ���˱��Ϊ��N��
            if ("".equals(nsExeCode))
                return false;

            return true;
        }

        return "";
    }

    /**
     * �������ݿ����м���ʱ����
     * @param odiOrder TDS ODI_ORDER��
     * @param parm TParm ���е�����TParm��ʽ
     * @param row int �޸ĵ�����
     * @param column String ���޸ĵ������������У�
     * @param value Object ���е�ֵ
     * @return boolean
     */

    public boolean setOtherColumnValue(TDS odiOrder, TParm parm, int row,
                                       String column, Object value) {
        //�鿴ִ�б��
        if ("EXE_FLG".equals(column)) {
            //�ж�ִ�б���Ƿ�Ϊ��
            if (TypeTool.getBoolean(value)) {
                Timestamp time = TJDODBTool.getInstance().getDBTime();
                odiOrder.setItem(row, "NS_EXEC_CODE", Operator.getID());
                //odiOrder.setItem(row, "EXEC_DEPT_CODE", Operator.getDept());
                odiOrder.setItem(row, "NS_EXEC_DATE", time);
                odiOrder.setActive(row,true);
            }
            else {
                odiOrder.setItem(row, "NS_EXEC_CODE", null);
                //odiOrder.setItem(row, "EXEC_DEPT_CODE", null);
                odiOrder.setItem(row, "NS_EXEC_DATE", null);
                odiOrder.setActive(row,false);
            }
            return true;
        }
        return true;
    }

}
