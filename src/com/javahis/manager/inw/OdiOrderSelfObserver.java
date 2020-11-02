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
 * <p>Title: ODI_ORDER���Թ۲죬�Ա�����֧��PDAִ��ʹ��</p>
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
public class OdiOrderSelfObserver
    extends TObserverAdapter {
    public OdiOrderSelfObserver() {
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
        if ("NS_EXEC_DATE_DAY".equals(column)) {
            Timestamp date = tParm.getTimestamp("NS_EXEC_DATE", row);
            if (date == null)
                return "";
            //�������һ��ֵ
            return StringTool.getString(date, "yyyy/MM/dd");
        }
        //���ʱ��
        if ("NS_EXEC_DATE_TIME".equals(column)) {
            Timestamp date = tParm.getTimestamp("NS_EXEC_DATE", row);
            if (date == null)
                return "";
            return StringTool.getString(date, "HH:mm:ss");
        }
        //ִ�б��
        if ("EXE_FLG".equals(column)) {
            this.getDS().showDebug();
            //��û�ʿ���ʱ��
            //Timestamp date = tParm.getTimestamp("NS_EXEC_DATE", row);
            //PS�������ȡ�����жϵġ��С������Ǳ�����ʾ���У���������setItem�����õ�����û�еģ�
            String date=tParm.getValue("NS_EXEC_CODE", row);
            //����ʿ����Ϊ�յ�ʱ��˵��û����ˣ���˱��Ϊ��N��
            if (date == null){
                return false;
            }
            return true;
        }

        //ҩƷ����+���ORDER_DESC_AND_SPECIFICATION
        if ("ORDER_DESC_AND_SPECIFICATION".equals(column)) {
            //���ҩƷ����
            String desc = tParm.getValue("ORDER_DESC", row);
            //���ҩƷ���
            String specification = tParm.getValue("SPECIFICATION", row);

            return specification.equals("") ? desc :
                desc + "(" + specification + ")";

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

    public boolean setOtherColumnValue(TDS odiDspnd, TParm parm, int row,
                                       String column, Object value) {
        //�鿴ִ�б��
        if ("EXE_FLG".equals(column)) {
            //�ж�ִ�б���Ƿ�Ϊ��
            if (TypeTool.getBoolean(value)) {
                odiDspnd.setItem(row, "NS_EXEC_CODE", Operator.getID());
                //��ô�������õ�ʱ��
                Timestamp time = TJDODBTool.getInstance().getDBTime();
                odiDspnd.setItem(row, "NS_EXEC_DATE", time);
                odiDspnd.setItem(row, "EXEC_DEPT_CODE", Operator.getDept());
            }
            else {
                odiDspnd.setItem(row, "NS_EXEC_CODE", null);
                odiDspnd.setItem(row, "NS_EXEC_DATE", null);
                odiDspnd.setItem(row, "EXEC_DEPT_CODE", null);
            }
            return true;
        }
        return true;
    }

}
