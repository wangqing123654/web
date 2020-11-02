package com.javahis.manager.inw;


import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDS;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import java.sql.Timestamp;

/**
 * <p>Title: SUM_VITALSIGN(���µ�����)�۲�SUM_VTSNTPRDTL(���µ�ϸ��)</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:JAVAHIS 1.0 </p>
 *
 * @author ZangJH
 * @version 1.0
 */
public class SumVitalSignObserverVtsntprdtl
    extends TObserverAdapter {
    public SumVitalSignObserverVtsntprdtl() {
    }

    /**
     * �������ݵ�ʱ�����
     * @param ds TDS
     * @param row int
     * @return int
     */
    public int insertRow(TDS ds, int row) {

        if (this.getDS() == null)
            return 0;
        //Ŀ��TDS(SUM_VITALSIGN��)
        TDS sumVitalSign = getDS();
        if(row==5){
            int rowNub = sumVitalSign.insertRow();
            sumVitalSign.setItem(rowNub,"EXAMINE_DATE",(String)sumVitalSign.getAttribute("EXAMINE_DATE"));
            sumVitalSign.setItem(rowNub,"USER_ID",sumVitalSign.getAttribute("OPT_USER"));
        }


        return 0;
    }


    /**
     * ���²���(ִ��ģ��)
     * @param dspnm TDS
     * @param row int
     * @param flg String Ϊ��Y����ִ��/��N����ȡ��ִ��
     * @return int
     */

    public int updateRow(TParm dspnm, int row, String flg, Timestamp now) {
        if (this.getDS() == null)
            return 0;
        //Ŀ��TDS
        TDS dspnd = getDS();
        dspnd.showDebug();
        int count = dspnd.rowCount();

        //ѭ������ÿһ������
        for (int i = 0; i < count; i++) {

            //ִ�е�ʱ���ODI_DSPND�Ķ���
            if (flg.equals("Y")) {
                dspnd.setItem(i, "NS_EXEC_CODE", Operator.getID());
                dspnd.setItem(i, "NS_EXEC_DATE", now);
            }
            else { //ȡ��ִ�е�ʱ���ODI_DSPND�Ķ���
                dspnd.setItem(i, "NS_EXEC_CODE", null);
                dspnd.setItem(i, "NS_EXEC_DATE", now);
            }
        }
        dspnd.showDebug();
        return 0;
    }


    /**
     * ɾ������
     * @param ds TDS
     * @param row int
     * @return boolean
     */
    public boolean deleteRow(TDS ds, int row) {
        return false;
    }

    /**
     * �����������޸Ķ�Ӧ�е�ֵ
     * @param colNames String
     * @return boolean
     */
    public boolean updateDate(String colNames) {
        return false;
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

        return "";
    }


    /**
     * ��������������
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean ����Ŀǰ��ֵ
     */

    public boolean setOtherColumnValue(TDS odiDspnm, TParm parm, int row,
                                       String column, Object value) {

        return true;
    }

}
