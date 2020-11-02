package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.util.Manager;
import com.dongyang.ui.TCheckBox;
import com.dongyang.jdo.TJDODBTool;

import java.text.DecimalFormat;
import java.util.Vector;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: Ԥ������ܱ���</p>
 *
 * <p>Description: Ԥ������ܱ���</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.5.6
 * @version 1.0
 */
public class BILPayCollectQueryControl
    extends TControl {

    private TTable table;

    public BILPayCollectQueryControl() {
    }

    /*
     * ��ʼ��
     */
    public void onInit() {
        table = (TTable)this.getComponent("TABLE");
        String datetime = SystemTool.getInstance().getDate().toString();
        String start_date = datetime.substring(0, 10) + " 00:00:00";
        String end_date = datetime.substring(0, 10) + " 23:59:59";
        this.setValue("START_DATE", start_date.replace("-", "/"));
        this.setValue("END_DATE", end_date.replace("-", "/"));
        // ��TBL_DISPENSE�е�CHECKBOX��������¼�
        callFunction("UI|TABLE|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        setValue("CASHIER_CODE", Operator.getID());
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        String start_date = this.getValueString("START_DATE").substring(0, 19);
        start_date = start_date.substring(0, 4) + start_date.substring(5, 7) +
            start_date.substring(8, 10) + start_date.substring(11, 13) +
            start_date.substring(14, 16) + start_date.substring(17, 19);
        String end_date = this.getValueString("END_DATE").substring(0, 19);
        end_date = end_date.substring(0, 4) + end_date.substring(5, 7) +
            end_date.substring(8, 10) + end_date.substring(11, 13) +
            end_date.substring(14, 16) + end_date.substring(17, 19);
        String user_id = this.getValueString("CASHIER_CODE");
        String sql = "";
        TParm result = new TParm();

        String[] cloumns = {
            "SELECT_FLG", "USER_NAME", "PAY_COUNT", "PAY_CASH", "PAY_CHECK",
            "PAY_BANK_CARD", "PAY_DEBIT", "PAY_MEDICAL_CARD","PAY_TYPE09","PAY_TYPE10",
            "PAY_AMT", "BACK_AMT", "SUM_AMT"};
        String regionCode = "";
        if (!"".equals(Operator.getRegion()))
            regionCode = Operator.getRegion();
        String regionWhere = "";
        for (int i = 0; i < cloumns.length; i++) {
            result.setData(cloumns[i], new Vector());
        }
        if (!"".equals(user_id)) {
            // �õ��õ��س������SQL
            sql = getHCCount(start_date, end_date, user_id, regionCode);
            TParm parmHCSQL = new TParm(TJDODBTool.getInstance().select(sql));
            sql = getSQL(start_date, end_date, user_id, regionCode);
            TParm parmSQL = new TParm(TJDODBTool.getInstance().select(sql));
            result = getParm(result, parmHCSQL, parmSQL);
        }
        else {
            // ��ѯ�����շ���Ա���б�
//            sql = " SELECT A.USER_ID, A.USER_NAME " +
//                " FROM SYS_OPERATOR A, SYS_POSITION B " +
//                " WHERE A.POS_CODE = B.POS_CODE " +
//                " AND B.POS_TYPE = '5'";
            if (!"".equals(regionCode))
                regionWhere = " AND A.REGION_CODE = '" + regionCode + "' ";
            sql =
                " SELECT A.USER_ID, A.USER_NAME " +
                " FROM SYS_OPERATOR A, SYS_POSITION B " +
                " WHERE A.POS_CODE = B.POS_CODE " +
                regionWhere;
            TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
            for (int i = 0; i < parm.getCount("USER_ID"); i++) {
                sql = getHCCount(start_date, end_date,
                                 parm.getValue("USER_ID", i), regionCode);
                //System.out.println("getHCCount---" + sql);
                TParm parmHCSQL = new TParm(TJDODBTool.getInstance().select(sql));
                sql = getSQL(start_date, end_date, parm.getValue("USER_ID", i),
                             regionCode);
                //System.out.println("getSQL---" + sql);
                TParm parmSQL = new TParm(TJDODBTool.getInstance().select(sql));
                result = getParm(result, parmHCSQL, parmSQL);
            }
        }
        if (result == null || result.getCount("USER_NAME") <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        table.setParmValue(result);
    }

    /**
     * ȡ��SQL
     * @param start_date String
     * @param end_date String
     * @param user_id String
     * @param regionCode String
     * @return String
     */
    private String getSQL(String start_date, String end_date, String user_id,
                          String regionCode) {
        String regionWhere = "";
        if (!"".equals(regionCode))
            regionWhere = " AND B.REGION_CODE = '" + regionCode + "' ";
        return
            "SELECT 'N' AS SELECT_FLG, B.USER_NAME, COUNT (*) AS PAY_COUNT, "
            + " A.PAY_TYPE, SUM (A.PRE_AMT) AS AMT "
            + " FROM BIL_PAY A, SYS_OPERATOR B "
            + " WHERE A.CASHIER_CODE = B.USER_ID "
            + " AND A.CASHIER_CODE = '" + user_id +
            "' AND A.TRANSACT_TYPE IN ('01', '02','04')"+
            regionWhere +
             " AND A.CHARGE_DATE BETWEEN TO_DATE('" + start_date +
            "','YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
            "','YYYYMMDDHH24MISS') GROUP BY B.USER_NAME, A.PAY_TYPE ";
    }

    /**
     * �õ��õ��س������SQL
     * @param start_date String
     * @param end_date String
     * @param user_id String
     * @param regionCode String
     * @return String
     */
    private String getHCCount(String start_date, String end_date,
                              String user_id, String regionCode) {
        String regionWhere = "";
        if (!"".equals(regionCode))
            regionWhere = " AND A.REGION_CODE = '" + regionCode + "' ";
        return "SELECT SUM (A.PREPAY_WRTOFF) AS BACK_BILPAY, "
            + " COUNT (A.CASHIER_CODE) AS HCNUM, A.CASHIER_CODE, D.USER_NAME," +
            		"A.PAY_TYPE09,A.PAY_TYPE10 "
            + " FROM BIL_IBS_RECPM A, BIL_ACCOUNT C, SYS_OPERATOR D "
            + " WHERE C.ACCOUNT_TYPE = 'BIL' "
            + " AND C.ACCOUNT_SEQ = A.ACCOUNT_SEQ "
            + " AND C.ACCOUNT_USER = D.USER_ID "
            + " AND A.CHARGE_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') "
            + " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') "
            + regionWhere
            + " AND A.CASHIER_CODE = '" + user_id
            + "' GROUP BY A.CASHIER_CODE, D.USER_NAME,A.PAY_TYPE09,A.PAY_TYPE10 ";
    }

    /**
     * ��������
     * @param result TParm
     * @param parmHCSQL TParm
     * @param parmSQL TParm
     * @return TParm
     */
    private TParm getParm(TParm result, TParm parmHCSQL, TParm parmSQL) {
        if (parmHCSQL.getCount("CASHIER_CODE") <= 0 &&
            parmSQL.getCount("USER_NAME") <= 0) {
            return result;
        }
        int row = result.insertRow();
        result.setData("SELECT_FLG", row, "N");
        result.setData("USER_NAME", row, parmSQL.getValue("USER_NAME", 0));
        result.setData("PAY_COUNT", row, 0);
        result.setData("PAY_CASH", row, 0);
        result.setData("PAY_CHECK", row, 0);
        result.setData("PAY_BANK_CARD", row, 0);
        result.setData("PAY_DEBIT", row, 0);
        result.setData("PAY_MEDICAL_CARD", row, 0);
        result.setData("PAY_TYPE09", row, 0);
        result.setData("PAY_TYPE10", row, 0);
        result.setData("PAY_AMT", row, 0);
        result.setData("BACK_AMT", row, 0);
        result.setData("SUM_AMT", row, 0);

        for (int i = 0; i < parmSQL.getCount("USER_NAME"); i++) {
            result.setData("PAY_COUNT", row,
                           result.getInt("PAY_COUNT", row) +
                           parmSQL.getInt("PAY_COUNT", i));
            if ("C0".equals(parmSQL.getValue("PAY_TYPE", i))) {//�ֽ�
                result.setData("PAY_CASH", row,
                               result.getDouble("PAY_CASH", row) +
                               parmSQL.getDouble("AMT", i));
            }
            else if ("T0".equals(parmSQL.getValue("PAY_TYPE", i))) {//֧Ʊ
                result.setData("PAY_CHECK", row,
                               result.getDouble("PAY_CHECK", row) +
                               parmSQL.getDouble("AMT", i));
            }
            else if ("C1".equals(parmSQL.getValue("PAY_TYPE", i))) {//ˢ��
                result.setData("PAY_BANK_CARD", row,
                               result.getDouble("PAY_BANK_CARD", row) +
                               parmSQL.getDouble("AMT", i));
            }
            else if ("C4".equals(parmSQL.getValue("PAY_TYPE", i))) {//Ӧ�տ�
                result.setData("PAY_DEBIT", row,
                               result.getDouble("PAY_DEBIT", row) +
                               parmSQL.getDouble("AMT", i));
            }
            else if ("EKT".equals(parmSQL.getValue("PAY_TYPE", i))) {//ҽ�ƿ�
                result.setData("PAY_MEDICAL_CARD", row,
                               result.getDouble("PAY_MEDICAL_CARD", row) +
                               parmSQL.getDouble("AMT", i));
            }
            else if ("WX".equals(parmSQL.getValue("PAY_TYPE", i))) {//΢��
            	result.setData("PAY_TYPE09", row,
            			result.getDouble("PAY_TYPE09", row) +
            			parmSQL.getDouble("AMT", i));
            }
            else if ("ZFB".equals(parmSQL.getValue("PAY_TYPE", i))) {//֧����
            	result.setData("PAY_TYPE10", row,
            			result.getDouble("PAY_TYPE10", row) +
            			parmSQL.getDouble("AMT", i));
            }
            result.setData("PAY_AMT", row,
                           result.getDouble("PAY_AMT", row) +
                           parmSQL.getDouble("AMT", i));
        }
        if (parmHCSQL.getCount("CASHIER_CODE") > 0) {
            result.setData("PAY_COUNT", row,
                           result.getInt("PAY_COUNT", row) +
                           parmHCSQL.getInt("HCNUM", 0));
            result.setData("BACK_AMT", row,
                           parmHCSQL.getDouble("BACK_BILPAY", 0));
        }
        result.setData("SUM_AMT", row,
                       result.getDouble("PAY_AMT", row) -
                       result.getDouble("BACK_AMT", row));
        return result;
    }

    /**
     * ��Ӻϼ�
     * @param parm TParm
     * @return TParm
     */
    private TParm addTOT(TParm parm) {
        int pay_count = 0;
        double pay_cash = 0;
        double pay_check = 0;
        double pay_bank_card = 0;
        double pay_debit = 0;
        double pay_medical_card = 0;
        double pay_type09 = 0;
        double pay_type10 = 0;
        double pay_amt = 0;
        double back_amt = 0;
        double sum_amt = 0;
        for (int i = 0; i < parm.getCount("PAY_COUNT"); i++) {
            pay_count += parm.getInt("PAY_COUNT", i);
            pay_cash += parm.getDouble("PAY_CASH", i);
            pay_check += parm.getDouble("PAY_CHECK", i);
            pay_bank_card += parm.getDouble("PAY_BANK_CARD", i);
            pay_debit += parm.getDouble("PAY_DEBIT", i);
            pay_medical_card += parm.getDouble("PAY_MEDICAL_CARD", i);
            pay_type09 += parm.getDouble("PAY_TYPE09", i);
            pay_type10 += parm.getDouble("PAY_TYPE10", i);
            pay_amt += parm.getDouble("PAY_AMT", i);
            back_amt += parm.getDouble("BACK_AMT", i);
            sum_amt += parm.getDouble("SUM_AMT", i);
        }

        parm.addData("USER_NAME", "�ϼ�");
        parm.addData("PAY_COUNT", pay_count);
        parm.addData("PAY_CASH", pay_cash);
        parm.addData("PAY_CHECK", pay_check);
        parm.addData("PAY_BANK_CARD", pay_bank_card);
        parm.addData("PAY_DEBIT", pay_debit);
        parm.addData("PAY_MEDICAL_CARD", pay_medical_card);
        parm.addData("PAY_TYPE09", pay_type09);
        parm.addData("PAY_TYPE10", pay_type10);
        parm.addData("PAY_AMT", pay_amt);
        parm.addData("BACK_AMT", back_amt);
        parm.addData("SUM_AMT", sum_amt);

        return parm;
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String datetime = SystemTool.getInstance().getDate().toString();
        String start_date = datetime.substring(0, 10) + " 00:00:00";
        String end_date = datetime.substring(0, 10) + " 23:59:59";
        this.setValue("START_DATE", start_date.replace("-", "/"));
        this.setValue("END_DATE", end_date.replace("-", "/"));
        table.removeRowAll();
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
    	DecimalFormat df = new DecimalFormat("##########0.00");//==liling 20140710 add 
        TParm parmTable = table.getParmValue();
        TParm printParm = new TParm();
        for (int i = parmTable.getCount("USER_NAME") - 1; i >= 0; i--) {
            if ("Y".equals(table.getItemString(i, "SELECT_FLG"))) {
                printParm.addData("USER_NAME",
                                  parmTable.getValue("USER_NAME", i));
                printParm.addData("PAY_COUNT",
                                  parmTable.getDouble("PAY_COUNT", i));
                printParm.addData("PAY_CASH", parmTable.getDouble("PAY_CASH", i));
                printParm.addData("PAY_CHECK",
                                  parmTable.getDouble("PAY_CHECK", i));
                printParm.addData("PAY_BANK_CARD",
                                  parmTable.getDouble("PAY_BANK_CARD", i));
                printParm.addData("PAY_DEBIT",
                                  parmTable.getDouble("PAY_DEBIT", i));
                printParm.addData("PAY_MEDICAL_CARD",
                                  parmTable.getDouble("PAY_MEDICAL_CARD", i));
                printParm.addData("PAY_TYPE09",
                		parmTable.getDouble("PAY_TYPE09", i));
                printParm.addData("PAY_TYPE10",
                		parmTable.getDouble("PAY_TYPE10", i));
                printParm.addData("PAY_AMT", parmTable.getDouble("PAY_AMT", i));
                printParm.addData("BACK_AMT", parmTable.getDouble("BACK_AMT", i));
                printParm.addData("SUM_AMT", parmTable.getDouble("SUM_AMT", i));
            }
        }
        if (printParm.getCount("USER_NAME") <= 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        addTOT(printParm);
        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNShortName(Operator.getRegion()) + "סԺԤ������ܱ���");
        String start_date = getValueString("START_DATE");
        String end_date = getValueString("END_DATE");
        date.setData("DATE_AREA", "TEXT",
                     "ͳ������: " +
                     start_date.substring(0, 4) + "/" +
                     start_date.substring(5, 7) + "/" +
                     start_date.substring(8, 10) + " " +
                     start_date.substring(11, 13) + ":" +
                     start_date.substring(14, 16) + ":" +
                     start_date.substring(17, 19) +
                     " ~ " +
                     end_date.substring(0, 4) + "/" +
                     end_date.substring(5, 7) + "/" +
                     end_date.substring(8, 10) + " " +
                     end_date.substring(11, 13) + ":" +
                     end_date.substring(14, 16) + ":" +
                     end_date.substring(17, 19));
        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
        date.setData("DATE", "TEXT",
                     "�Ʊ�ʱ��: " +
                     SystemTool.getInstance().getDate().toString().
                     substring(0, 10).
                     replace('-', '/'));
        // �������
        TParm parm = new TParm();
        for (int i = 0; i < printParm.getCount("USER_NAME"); i++) {
            parm.addData("USER_NAME", printParm.getValue("USER_NAME", i));
            parm.addData("PAY_COUNT", printParm.getInt("PAY_COUNT", i));
            //===liling 20140710 add df.format()   start===
            parm.addData("PAY_CASH", df.format(printParm.getDouble("PAY_CASH", i)));
            parm.addData("PAY_CHECK", df.format(printParm.getDouble("PAY_CHECK", i)));
            parm.addData("PAY_BANK_CARD",
            		df.format(printParm.getDouble("PAY_BANK_CARD", i)));
            parm.addData("PAY_DEBIT",df.format( printParm.getDouble("PAY_DEBIT", i)));
            parm.addData("PAY_MEDICAL_CARD",
            		df.format(printParm.getDouble("PAY_MEDICAL_CARD", i)));
            parm.addData("PAY_TYPE09",
            		df.format(printParm.getDouble("PAY_TYPE09", i)));
            parm.addData("PAY_TYPE10",
            		df.format(printParm.getDouble("PAY_TYPE10", i)));
            parm.addData("PAY_AMT", df.format(printParm.getDouble("PAY_AMT", i)));
            //parm.addData("BACK_AMT", parmTable.getDouble("BACK_AMT", i));
            //parm.addData("SUM_AMT", parmTable.getDouble("SUM_AMT", i));
            //===liling 20140710 add df.format()   end===
        }
        parm.setCount(parm.getCount("USER_NAME"));
        parm.addData("SYSTEM", "COLUMNS", "USER_NAME");
        parm.addData("SYSTEM", "COLUMNS", "PAY_COUNT");
        parm.addData("SYSTEM", "COLUMNS", "PAY_CASH");
        parm.addData("SYSTEM", "COLUMNS", "PAY_CHECK");
        parm.addData("SYSTEM", "COLUMNS", "PAY_BANK_CARD");
        parm.addData("SYSTEM", "COLUMNS", "PAY_DEBIT");
        parm.addData("SYSTEM", "COLUMNS", "PAY_MEDICAL_CARD");
        parm.addData("SYSTEM", "COLUMNS", "PAY_TYPE09");
        parm.addData("SYSTEM", "COLUMNS", "PAY_TYPE10");
        parm.addData("SYSTEM", "COLUMNS", "PAY_AMT");
        //parm.addData("SYSTEM", "COLUMNS", "BACK_AMT");
        //parm.addData("SYSTEM", "COLUMNS", "SUM_AMT");
        date.setData("TABLE", parm.getData());
        //System.out.println("date---"+date);
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILPayCollect.jhw",
                             date);
    }

    /**
     * ȫѡ��ѡ��ѡ���¼�
     */
    public void onCheckSelectAll() {
        table.acceptText();
        if (table.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        TParm parm = table.getParmValue();
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table.getRowCount(); i++) {
                parm.setData("SELECT_FLG", i, "Y");
            }
        }
        else {
            for (int i = 0; i < table.getRowCount(); i++) {
                parm.setData("SELECT_FLG", i, "N");
            }
        }
        table.setParmValue(parm);
    }

    /**
     * �õ�CheckBox����
     * @param tagName String
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * ���(TABLE)��ѡ��ı��¼�
     * @param obj Object
     */
    public void onTableCheckBoxClicked(Object obj) {
        int column = table.getSelectedColumn();
        if (column == 0) {
            table.acceptText();
        }
    }


}
