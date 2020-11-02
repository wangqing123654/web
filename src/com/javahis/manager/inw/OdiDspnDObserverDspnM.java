package com.javahis.manager.inw;

import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDS;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import java.util.List;
import com.dongyang.util.TypeTool;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

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

public class OdiDspnDObserverDspnM
    extends TObserverAdapter {
    public OdiDspnDObserverDspnM() {
    }

    //�洢order�����кź�M�����кŵĶ�Ӧ��ϵ(key:String value:list)
    Map orderRNToMRN = new HashMap();
    ArrayList MRNList = new ArrayList();

    /**
     * ��ODI_DSPND�в������ݣ����ģ�飩
     * @param ds TDS��ODI_DSPNM��
     * @param row int
     * @return int
     */
    public int insertRow(TParm origin, int row, TParm toDspnd) {
//        System.out.println("===================");
//        System.out.println("����D�۲��ŵĲ��뷽��");
//        System.out.println("===================");


        if (this.getDS() == null)
            return 0;
        //Ŀ��TDS
        TDS dspnd = getDS();
        Timestamp time = (Timestamp) dspnd.getAttribute("EXE_DATE");
        if (time == null)
            time = TJDODBTool.getInstance().getDBTime();
        //չ��������
        int transInRows = ( (List) toDspnd.getData("START_DTTM_LIST")).size();
        List startDttmList = (List) toDspnd.getData("START_DTTM_LIST");
        //ѭ������ÿһ������
        for (int i = 0; i < transInRows; i++) {
            //�ĸ�����ȷ���������Ƿ��Ѿ����ڣ����ڲ������У�ֻ�õ��к�/�����ڲ������У��õ��кţ�
            String caseNo=(String) origin.getData("CASE_NO");
//            System.out.println("=====caseNo======>"+caseNo);
            String orderNo=(String) origin.getData("ORDER_NO");
//            System.out.println("=====caseNo======>"+orderNo);
            String orderSeq=origin.getData("ORDER_SEQ").toString();
//            System.out.println("=====caseNo======>"+orderSeq);
            String orderDate = startDttmList.get(i).toString();//ҽ��ʱ��
//            System.out.println("=====caseNo======>"+orderDate);
            //�������ڵ�ʱ�򷵻�-1.���ڷ����к�
            int newRow=isExist(caseNo, orderNo, orderSeq, orderDate);
            if ( newRow== -1){
                //��ODI_DSPND�в���һ�����У������ڸ����к�
                newRow = dspnd.insertRow();
            }
//            System.out.println("=����1111111111111==>" + newRow);
            MRNList.add(newRow);
//            System.out.println("=����1111111111111==>" + MRNList);

            dspnd.setItem(newRow, "CASE_NO", origin.getData("CASE_NO"));
            dspnd.setItem(newRow, "ORDER_NO", origin.getData("ORDER_NO"));
            dspnd.setItem(newRow, "ORDER_SEQ", origin.getData("ORDER_SEQ"));
            dspnd.setItem(newRow, "ORDER_DATE", orderDate);
            dspnd.setItem(newRow, "ORDER_DATETIME", time); //ҽ��ִ��ʱ��

//            dspnd.setItem(newRow, "STATION_CODE",
//                          origin.getData("STATION_CODE"));

            dspnd.setItem(newRow, "BATCH_CODE", origin.getData("BATCH_CODE"));
            dspnd.setItem(newRow, "TREAT_START_TIME",
                          origin.getData("TREAT_START_TIME"));
            dspnd.setItem(newRow, "TREAT_END_TIME",
                          origin.getData("TREAT_END_TIME"));
            dspnd.setItem(newRow, "NURSE_DISPENSE_FLG",
                          origin.getData("NURSE_DISPENSE_FLG"));
            dspnd.setItem(newRow, "BAR_CODE", origin.getData("BAR_CODE"));

            dspnd.setItem(newRow, "ORDER_CODE",
                          origin.getData("ORDER_CODE"));
            //UDD����������
            dspnd.setItem(newRow, "MEDI_QTY",
                          toDspnd.getData("MEDI_QTY"));
            dspnd.setItem(newRow, "MEDI_UNIT",
                          toDspnd.getData("MEDI_UNIT"));
            dspnd.setItem(newRow, "DOSAGE_QTY",
                          toDspnd.getData("DOSAGE_QTY"));
            dspnd.setItem(newRow, "DOSAGE_UNIT",
                          toDspnd.getData("DOSAGE_UNIT"));

            dspnd.setItem(newRow, "TOT_AMT", origin.getData("TOT_AMT"));
            dspnd.setItem(newRow, "DC_DATE", origin.getData("DC_DATE"));
            dspnd.setItem(newRow, "PHA_DISPENSE_NO",
                          origin.getData("PHA_DISPENSE_NO"));
            dspnd.setItem(newRow, "PHA_DOSAGE_CODE",
                          origin.getData("PHA_DOSAGE_CODE"));
            dspnd.setItem(newRow, "PHA_DOSAGE_DATE",
                          origin.getData("PHA_DOSAGE_DATE"));

            dspnd.setItem(newRow, "PHA_DISPENSE_CODE",
                          origin.getData("PHA_DISPENSE_CODE"));
            dspnd.setItem(newRow, "PHA_DISPENSE_DATE",
                          origin.getData("PHA_DISPENSE_DATE"));
            dspnd.setItem(newRow, "NS_EXEC_CODE",
                          origin.getData("NS_EXEC_CODE"));
            dspnd.setItem(newRow, "NS_EXEC_DATE", origin.getData("NS_EXEC_DATE"));
            dspnd.setItem(newRow, "NS_EXEC_DC_CODE",
                          origin.getData("NS_EXEC_DC_CODE"));

            dspnd.setItem(newRow, "NS_EXEC_DC_DATE",
                          origin.getData("NS_EXEC_DC_DATE"));
            dspnd.setItem(newRow, "NS_USER", origin.getData("NS_USER"));
            dspnd.setItem(newRow, "EXEC_NOTE", origin.getData("EXEC_NOTE"));
            dspnd.setItem(newRow, "EXEC_DEPT_CODE",
                          origin.getData("EXEC_DEPT_CODE"));
            dspnd.setItem(newRow, "BILL_FLG", origin.getData("BILL_FLG"));

            dspnd.setItem(newRow, "CASHIER_CODE",
                          origin.getData("CASHIER_CODE"));
            dspnd.setItem(newRow, "CASHIER_DATE", origin.getData("CASHIER_DATE"));
            dspnd.setItem(newRow, "PHA_RETN_CODE",
                          origin.getData("PHA_RETN_CODE"));
            dspnd.setItem(newRow, "PHA_RETN_DATE",
                          origin.getData("PHA_RETN_DATE"));
            dspnd.setItem(newRow, "TRANSMIT_RSN_CODE",
                          origin.getData("TRANSMIT_RSN_CODE"));

            dspnd.setItem(newRow, "STOPCHECK_USER",
                          origin.getData("STOPCHECK_USER"));
            dspnd.setItem(newRow, "STOPCHECK_DATE",
                          origin.getData("STOPCHECK_DATE"));
            dspnd.setItem(newRow, "IBS_CASE_NO",
                          origin.getData("IBS_CASE_NO"));
            dspnd.setItem(newRow, "IBS_CASE_NO_SEQ",
                          origin.getData("IBS_CASE_NO_SEQ"));

            dspnd.setItem(newRow, "OPT_DATE", time);
            dspnd.setItem(newRow, "OPT_USER", Operator.getID());
            dspnd.setItem(newRow, "OPT_TERM", Operator.getIP());
        }

        //��order����к�ΪKEY��M����к�ΪVALUE
        orderRNToMRN.put(row,MRNList);
        return 0;
    }

    /**
     * �������ݵ�ʱ����ã����ģ�飩
     * @param ds TDS
     * @param row int
     * @return int
     */
    public int insertRow(TDS ds, int row) {
        //�õ���Ҫ��������еĲ���������EHUI�ķ��������к�
        TParm orderPrm = (TParm) ds.getAttribute("m_parm");
        int modifyRow = (Integer) ds.getAttribute("m_row");
        TParm dapndData = (TParm) ds.getAttribute("m_dspndData");
        //�������صĲ��뷽��
        insertRow(orderPrm, modifyRow, dapndData);
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
//        System.out.println("=ִ��---�������ֶ�==");
        if (this.getDS() == null)
            return 0;
        //Ŀ��TDS
        TDS dspnd = getDS();
        int count = dspnd.rowCount();

        //ѭ������ÿһ������
        for (int i = 0; i < count; i++) {

            //ִ�е�ʱ���ODI_DSPND�Ķ���
            if (flg.equals("Y")) {
                dspnd.setItem(i, "NS_EXEC_CODE", Operator.getID());
                dspnd.setItem(i, "NS_EXEC_DATE", now);
            }
            else { //ȡ��ִ�е�ʱ���ODI_DSPND�Ķ���
//                System.out.println("===>ȡ��ִ��");
                dspnd.setItem(i, "NS_EXEC_CODE", null);
                dspnd.setItem(i, "NS_EXEC_DATE", null);
            }
        }
        return 0;
    }


    /**
     * ɾ������
     * @param ds TDS
     * @param row int
     * @return boolean
     */
    public boolean deleteRow(TDS ds, int row) {

//        System.out.println("wwwwwwwwwwwwwwwwwww");
//        System.out.println("����D�۲��ŵ�ɾ������");
//        System.out.println("wwwwwwwwwwwwwwwwwww");
        if(ds.getAttribute("MRowNumber")==null){
//            System.out.println("=========�տ�=======>");
            return false;
        }
//        System.out.println("fffffffffffffffff>>>>>>" + ds.getAttribute("MRowNumber"));
        int MRowNumber=(Integer) ds.getAttribute("MRowNumber");
        ArrayList mRows = (ArrayList) orderRNToMRN.get(MRowNumber);
        //��ʱ�յ�ʱ���൱��M����û�в�����
        if (mRows != null) {
//            System.out.println("D������===>" + mRows.toString());
            //�õ�����order���ݶ�Ӧ��M���е�����
            int count = mRows.size();
            for (int i = 0; i < count; i++) {
                int rowNumber = (Integer) mRows.get(i);
                this.getDS().setActive(rowNumber, false); //����M������ACTIVE��false
            }
        }

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

    public boolean setItem(TDS ds, int row, String column, Object value) {
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
     * �жϸ���ҽ���Ƿ��Ѿ����ڣ�������������²��루���������ظ���
     * @return boolean ������ȷ��Ψһ���ݣ�ODI_DSPND��
     */
    private int isExist(String caseNo,String orderNo,String orderSeq,String orderDate){
//        System.out.println("======888888888888888888888====>"+caseNo+"  "+orderNo+"  "+orderSeq+"  "+orderDate);
        int row=-1;

        //�õ���ǰ��TDS
        TParm dspnD_P=this.getDS().getBuffer(TDS.PRIMARY);
        int count=dspnD_P.getCount();
        if(count<=0)
            return -1;
        Vector caseNo_V=(Vector) dspnD_P.getData("CASE_NO");
        Vector orderNo_V=(Vector) dspnD_P.getData("ORDER_NO");
        Vector orderSeq_V=(Vector) dspnD_P.getData("ORDER_SEQ");
        Vector orderDate_V=(Vector) dspnD_P.getData("ORDER_DATE");
//        System.out.println("=====>"+caseNo_V);
//        System.out.println("=====>"+orderNo_V);
//        System.out.println("=====>"+orderSeq_V);
//        System.out.println("=====>"+orderDate_V);
        for(int i=0;i<count;i++){
//            System.out.println("==111111111======>"+caseNo);
            if(!caseNo.equals(caseNo_V.get(i))){
//                System.out.println("==222222222======>"+caseNo);
                continue;
            }
            if(!orderNo.equals(orderNo_V.get(i))){
//                System.out.println("==333333333======>"+orderNo);
                continue;
            }
            if(!orderSeq.equals(orderSeq_V.get(i).toString())){
//                System.out.println("==44444444444======>"+orderSeq);
                continue;
            }
            if(!orderDate.equals(orderDate_V.get(i))){
//                System.out.println("==555555555======>"+orderDate);
                continue;
            }
            row=i;
        }
//        System.out.println("======888888888888888888888====>"+row);
        return row;
    }


    /**
     * �������������ݣ�ִ��ģ�飩
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean ����Ŀǰ��ֵ
     */

    public boolean setOtherColumnValue(TDS odiDspnm, TParm parm, int row,
                                       String column, Object value) {

        //�鿴ִ�б��
        if ("EXE_FLG".equals(column)) {
            //�ж�ִ�б���Ƿ�Ϊ��
            if (TypeTool.getBoolean(value)) {

                odiDspnm.setItem(row, "NS_EXEC_CODE", Operator.getID());
                Timestamp time = (Timestamp) odiDspnm.getAttribute("EXE_DATE");
                if (time == null)
                    time = TJDODBTool.getInstance().getDBTime();
                odiDspnm.setItem(row, "NS_EXEC_DATE", time);
                //�ڹ�ѡ��ʱ�򼤷����붯��
                this.updateRow(parm, row, "Y", time);

            }
            else {
                odiDspnm.setItem(row, "NS_EXEC_CODE", null);
                odiDspnm.setItem(row, "NS_EXEC_DATE", null);
                this.updateRow(parm, row, "N", null);
            }

            return true;
        }

        return true;
    }
}
