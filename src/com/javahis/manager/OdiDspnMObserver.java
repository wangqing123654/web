package com.javahis.manager;

import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDS;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

/**
 * <p>Title: �۲�ODI_DSPNM���Ƿ���Ҫ��ODI_DSPND�в�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright:JAVAHIS</p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH
 * @version 1.0
 */

public class OdiDspnMObserver
    extends TObserverAdapter {
    public OdiDspnMObserver() {
        System.out.println("OdiDspnMObserver");
    }

    /**
     * ��ODI_DSPND�в�������
     * @param ds TDS��ODI_DSPNM��
     * @param row int
     * @return int
     */
    public int insertRow(TParm origin, int row) {
        System.out.println("��ODI_DSPND�в�������===>");
        if (this.getDS() == null)
            return 0;
        //Ŀ��TDS
        TDS dspnd = getDS();
        Timestamp time = (Timestamp)dspnd.getAttribute("EXE_DATE");
        //�õ�������������������ת��ΪӦ�ò���ϸ�������
        int insertRows = origin.getCount();
        //չ������������UDD����
        int transInRows = insertRows + 9;
        //ѭ������ÿһ������
        for (int i = 0; i <= transInRows; i++) {
            //��ODI_DSPND�в���һ������
            int newRow = dspnd.insertRow();

            dspnd.setItem(newRow, "CASE_NO", origin.getData("CASE_NO", row));
            dspnd.setItem(newRow, "ORDER_NO", origin.getData("ORDER_NO", row));
            dspnd.setItem(newRow, "ORDER_SEQ", origin.getData("ORDER_SEQ", row));
            dspnd.setItem(newRow, "ORDER_DATE", i+row + "");
            dspnd.setItem(newRow, "STATION_CODE",origin.getData("STATION_CODE", row));

            dspnd.setItem(newRow, "BATCH_CODE",origin.getData("BATCH_CODE", 0));
            dspnd.setItem(newRow, "TREAT_START_TIME",origin.getData("TREAT_START_TIME", row));
            dspnd.setItem(newRow, "TREAT_END_TIME",origin.getData("TREAT_END_TIME", row));
            dspnd.setItem(newRow, "NURSE_DISPENSE_FLG",origin.getData("NURSE_DISPENSE_FLG", row));
            dspnd.setItem(newRow, "BAR_CODE",origin.getData("BAR_CODE", row));

            dspnd.setItem(newRow, "ORDER_CODE",origin.getData("ORDER_CODE", row));
            dspnd.setItem(newRow, "DOSAGE_QTY",origin.getData("DOSAGE_QTY", row));
            dspnd.setItem(newRow, "DOSAGE_UNIT",origin.getData("DOSAGE_UNIT", row));
            dspnd.setItem(newRow, "TOT_AMT",origin.getData("TOT_AMT", row));
            dspnd.setItem(newRow, "DC_DATE",time);

            dspnd.setItem(newRow, "PHA_DISPENSE_NO",origin.getData("PHA_DISPENSE_NO", row));
            dspnd.setItem(newRow, "PHA_DOSAGE_CODE",origin.getData("PHA_DOSAGE_CODE", row));
            dspnd.setItem(newRow, "PHA_DOSAGE_DATE",time);
            dspnd.setItem(newRow, "PHA_DISPENSE_CODE",origin.getData("PHA_DISPENSE_CODE", row));
            dspnd.setItem(newRow, "PHA_DISPENSE_DATE",time);

            dspnd.setItem(newRow, "NS_EXEC_CODE",origin.getData("NS_EXEC_CODE", row));
            dspnd.setItem(newRow, "NS_EXEC_DATE",time);
            dspnd.setItem(newRow, "NS_EXEC_DC_CODE",origin.getData("NS_EXEC_DC_CODE", row));
            dspnd.setItem(newRow, "NS_EXEC_DC_DATE",time);
            dspnd.setItem(newRow, "NS_USER",origin.getData("NS_USER", row));

            dspnd.setItem(newRow, "EXEC_NOTE",origin.getData("EXEC_NOTE", row));
            dspnd.setItem(newRow, "EXEC_DEPT_CODE",origin.getData("EXEC_DEPT_CODE", row));
            dspnd.setItem(newRow, "BILL_FLG",origin.getData("BILL_FLG", row));
            dspnd.setItem(newRow, "CASHIER_CODE",origin.getData("CASHIER_CODE", row));
            dspnd.setItem(newRow, "CASHIER_DATE",time);

            dspnd.setItem(newRow, "PHA_RETN_CODE",origin.getData("PHA_RETN_CODE", row));
            dspnd.setItem(newRow, "PHA_RETN_DATE",time);
            dspnd.setItem(newRow, "TRANSMIT_RSN_CODE",origin.getData("TRANSMIT_RSN_CODE", row));
            dspnd.setItem(newRow, "STOPCHECK_USER",origin.getData("STOPCHECK_USER", row));
            dspnd.setItem(newRow, "STOPCHECK_DATE",time);

            dspnd.setItem(newRow, "IBS_CASE_NO",origin.getData("IBS_CASE_NO", row));
            dspnd.setItem(newRow, "IBS_CASE_NO_SEQ",origin.getData("IBS_CASE_NO_SEQ", row));
            dspnd.setItem(newRow, "OPT_DATE",time);
            dspnd.setItem(newRow, "OPT_USER", Operator.getID());
            dspnd.setItem(newRow, "OPT_TERM", Operator.getIP());

        }
        return 0;
    }

    /**
     * gen���²���
     * @param dspnm TDS
     * @param row int
     * @param flg String Ϊ��Y����ִ��/��N����ȡ��ִ��
     * @return int
     */

    public int updateRow(TParm dspnm, int row, String flg, Timestamp now) {
        System.out.println("=dddd==>");
        if (this.getDS() == null)
            return 0;
        //Ŀ��TDS
        TDS dspnd = getDS();
        dspnd.showDebug();
        //�õ�������������������ת��ΪӦ�ò���ϸ�������
        int insertRows = dspnm.getCount();
        //չ������������UDD����
        int transInRows = insertRows + 2;
        //ѭ������ÿһ������
        for (int i = 0; i <= transInRows; i++) {
            System.out.println("===>" + i);

            //ִ�е�ʱ���ODI_DSPND�Ķ���
            if (flg.equals("Y")) {
                dspnd.setItem(i, "NS_EXEC_CODE", Operator.getID());
                dspnd.setItem(i, "NS_EXEC_DATE",now);
            }
            else { //ȡ��ִ�е�ʱ���ODI_DSPND�Ķ���
                dspnd.setItem(i, "NS_EXEC_CODE", null);
                dspnd.setItem(i, "NS_EXEC_DATE", now);
            }
        }
        return 0;
    }

    /**
     * �������ݵ�ʱ�����
     * @param ds TDS
     * @param row int
     * @return int
     */
    public int insertRow(TDS ds, int row) {

        //�õ���Ҫ��������еĲ��������ö���ķ��������к�
        TParm orderPrm=(TParm) ds.getAttribute("m_parm");
        int modifyRow = (Integer)ds.getAttribute("m_row");

        //�������صĲ��뷽��
        insertRow(orderPrm,modifyRow);
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
            //��û�ʿ���ʱ��
            Timestamp date = tParm.getTimestamp("NS_EXEC_DATE", row);
            //��Ϊ�յ�ʱ��˵��û����ˣ���˱��Ϊ��N��
            if (date == null)
                return false;

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
     * ��������������
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */

    public boolean setOtherColumnValue(TDS odiDspnm, TParm parm, int row,
                                       String column, Object value) {

        //�鿴ִ�б��
        if ("EXE_FLG".equals(column)) {
            //Timestamp now=TJDODBTool.getInstance().getDBTime();
            //�ж�ִ�б���Ƿ�Ϊ��
            if (TCM_Transform.getBoolean(value)) {
                odiDspnm.setItem(row, "NS_EXEC_CODE", Operator.getID());
                Timestamp time = (Timestamp)odiDspnm.getAttribute("EXE_DATE");
                if(time == null)
                    time = TJDODBTool.getInstance().getDBTime();
                odiDspnm.setItem(row, "NS_EXEC_DATE",time);
                //�ڹ�ѡ��ʱ�򼤷����붯��
                this.updateRow(parm, row, "Y",time);
            }
            else {
                odiDspnm.setItem(row, "NS_EXEC_CODE", null);
                odiDspnm.setItem(row, "NS_EXEC_DATE", null);
                this.updateRow(parm, row, "N",null);
            }
            return true;
        }

        return true;
    }
}
