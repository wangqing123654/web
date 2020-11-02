package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;

import jdo.bil.BILComparator;
import jdo.sys.SystemTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.util.Manager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Vector;

/**
 * <p>Title: Ԥ�����ս���ϸ��</p>
 *
 * <p>Description: Ԥ�����ս���ϸ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BILPayInQueryControl extends TControl {

    private TTable table;
    private String day_cycle;
	// ==========modify-begin (by wanglong 20120710)===============
	// ������Ϊ������ĸ���
	//private Compare compare = new Compare();
	private BILComparator comparator=new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
	// ==========modify-end========================================
    
    public BILPayInQueryControl() {
    }

    /*
     * ��ʼ��
     */
    public void onInit() {
        table = (TTable)this.getComponent("TABLE");
		// ==========modify-begin (by wanglong 20120710)===============
		// Ϊ����Ӽ�������Ϊ������׼����
		addListener(table);
		// ==========modify-end========================================
//        day_cycle = BILSysParmTool.getInstance().getDayCycle("I").getValue(
//            "DAY_CYCLE", 0);
//        day_cycle = " " + day_cycle.substring(0, 2) + ":" +
//            day_cycle.substring(2, 4) + ":" + day_cycle.substring(4, 6);
//        String datetime = SystemTool.getInstance().getDate().toString();
//        String date = StringTool.rollDate(SystemTool.getInstance().getDate(),
//                                          -1).toString();
//        String start_date = date.substring(0, 10) + day_cycle;
//        String end_date = datetime.substring(0, 10) + day_cycle;
//        this.setValue("START_DATE", start_date.replace("-", "/"));
//        this.setValue("END_DATE", end_date.replace("-", "/"));
        String datetime = SystemTool.getInstance().getDate().toString();
        String start_date = datetime.substring(0, 10) + " 00:00:00";
        String end_date = datetime.substring(0, 10) + " 23:59:59";
        this.setValue("START_DATE", start_date.replace("-", "/"));
        this.setValue("END_DATE", end_date.replace("-", "/"));
    }

    /**
     * ��շ���
     */
    public void onClear() {
//        String datetime = SystemTool.getInstance().getDate().toString();
//        String date = StringTool.rollDate(SystemTool.getInstance().getDate(),
//                                          -1).toString();
//        String start_date = date.substring(0, 10) + day_cycle;
//        String end_date = datetime.substring(0, 10) + day_cycle;
//        this.setValue("START_DATE", start_date.replace("-", "/"));
//        this.setValue("END_DATE", end_date.replace("-", "/"));
        String datetime = SystemTool.getInstance().getDate().toString();
        String start_date = datetime.substring(0, 10) + " 00:00:00";
        String end_date = datetime.substring(0, 10) + " 23:59:59";
        this.setValue("START_DATE", start_date.replace("-", "/"));
        this.setValue("END_DATE", end_date.replace("-", "/"));

        String clearStr = "USER_ID;PAY_TYPE;TYPE_01_COUNT;TYPE_02_COUNT;"
                          +
                          "TYPE_03_COUNT;TYPE_04_COUNT;TYPE_01_AMT;TYPE_02_AMT;"
                          +
                          "TYPE_03_AMT;TYPE_04_AMT;TYPE_01_CASH;TYPE_02_CASH;"
                          +
                          "TYPE_03_CASH;TYPE_04_CASH;TYPE_01_BANK;TYPE_02_BANK;"
                          +
                          "TYPE_03_BANK;TYPE_04_BANK;TYPE_01_MEDICAL;TYPE_02_MEDICAL;"
                          +
                          "TYPE_03_MEDICAL;TYPE_04_MEDICAL;TYPE_01_DEBIT;TYPE_02_DEBIT;"
                          +
                          "TYPE_03_DEBIT;TYPE_04_DEBIT;TYPE_01_CHECK;TYPE_02_CHECK;"
                          +
                          "TYPE_03_CHECK;TYPE_04_CHECK;" +
                          "TYPE_01_WX;TYPE_02_WX;TYPE_03_WX;TYPE_04_WX;SUM_WX;" +
                          "TYPE_01_ZFB;TYPE_02_ZFB;TYPE_03_ZFB;TYPE_04_ZFB;SUM_ZFB;" +
                          "SUM_COUNT;SUM_AMT;SUM_CASH;"
                          + "SUM_BANK;SUM_MEDICAL;SUM_DEBIT;SUM_CHECK"
                          +";TYPE_01_LPK;TYPE_02_LPK;TYPE_03_LPK;TYPE_04_LPK;"//==liling 20140818 add
                          +";TYPE_01_XJZKQ;TYPE_02_XJZKQ;TYPE_03_XJZKQ;TYPE_04_XJZKQ;"//==liling 20140818 add
                          +"SUM_LPK;SUM_XJZKQ";//==liling 20140818 add
        this.clearValue(clearStr);
        table.removeRowAll();
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
        String sql = getSQL(start_date, end_date, this.getValueString("USER_ID"),
                            this.getValueString("TRANSACT_TYPE"));
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount("RECEIPT_NO") <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        } else {
            table.setParmValue(parm);
            setPayInfo(parm);
        }
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        DecimalFormat df = new DecimalFormat("##########0.00");
        if (table.getRowCount() <= 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) + "Ԥ�����ս���ϸ��");
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
                     substring(0, 19).
                     replace('-', '/'));
        // �������
        TParm parm = new TParm();
        TParm tableParm = table.getParmValue();
        for (int i = 0; i < table.getRowCount(); i++) {
            parm.addData("RECEIPT_NO", tableParm.getValue("RECEIPT_NO", i));
            parm.addData("PAY_TYPE_DESC", tableParm.getValue("PAY_TYPE_DESC", i));
            parm.addData("USER_NAME", tableParm.getValue("USER_NAME", i));
            parm.addData("DEPT_CHN_DESC", tableParm.getValue("DEPT_CHN_DESC", i));
            parm.addData("STATION_DESC", tableParm.getValue("STATION_DESC", i));
            parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
            parm.addData("IPD_NO", tableParm.getValue("IPD_NO", i));
            parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
            parm.addData("PRE_AMT", df.format(tableParm.getDouble("PRE_AMT", i)));
            if ("01".equals(tableParm.getValue("TRANSACT_TYPE", i))) {
                parm.addData("TRANSACT_TYPE", "�ɷ�");
            } else if ("02".equals(tableParm.getValue("TRANSACT_TYPE", i))) {
                parm.addData("TRANSACT_TYPE", "�˷�");
            }
//            else if ("03".equals(tableParm.getValue("TRANSACT_TYPE", i))) {
//                parm.addData("TRANSACT_TYPE", "����");
//            } 
            else if ("04".equals(tableParm.getValue("TRANSACT_TYPE", i))) {
                parm.addData("TRANSACT_TYPE", "�س�");
            }
        }
        parm.setCount(parm.getCount("RECEIPT_NO"));
        parm.addData("SYSTEM", "COLUMNS", "RECEIPT_NO");
        parm.addData("SYSTEM", "COLUMNS", "PAY_TYPE_DESC");
        parm.addData("SYSTEM", "COLUMNS", "USER_NAME");
        parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        parm.addData("SYSTEM", "COLUMNS", "MR_NO");
        parm.addData("SYSTEM", "COLUMNS", "IPD_NO");
        parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        parm.addData("SYSTEM", "COLUMNS", "PRE_AMT");
        parm.addData("SYSTEM", "COLUMNS", "TRANSACT_TYPE");
        date.setData("TABLE", parm.getData());
//        System.out.println("date---" + date);

        // ��β����
        date.setData("TYPE_01_COUNT", "TEXT",
                     "�ɷ�:" + this.getValueInt("TYPE_01_COUNT") + "��");
        date.setData("TYPE_01_AMT", "TEXT",
                     "�ɷѽ��:" + df.format(this.getValueDouble("TYPE_01_AMT")));
        date.setData("TYPE_01_CASH", "TEXT",
                     "�ֽ�:" + df.format(this.getValueDouble("TYPE_01_CASH")));
        date.setData("TYPE_01_BANK", "TEXT",
                     "���п�:" + df.format(this.getValueDouble("TYPE_01_BANK")));
        date.setData("TYPE_01_MEDICAL", "TEXT",
                     "ҽ�ƿ�:" + df.format(this.getValueDouble("TYPE_01_MEDICAL")));
        date.setData("TYPE_01_DEBIT", "TEXT",
                     "ת��:" + df.format(this.getValueDouble("TYPE_01_DEBIT")));
        date.setData("TYPE_01_CHECK", "TEXT",
                     "֧Ʊ:" + df.format(this.getValueDouble("TYPE_01_CHECK")));
        date.setData("TYPE_01_LPK", "TEXT",
                "��Ʒ��:" + df.format(this.getValueDouble("TYPE_01_LPK")));//==liling 20140818 add
        date.setData("TYPE_01_XJZKQ", "TEXT",
                "�ֽ��ۿ�ȯ:" + df.format(this.getValueDouble("TYPE_01_XJZKQ")));//==liling 20140818 add
        date.setData("TYPE_01_WX", "TEXT",
        		"΢��:" + df.format(this.getValueDouble("TYPE_01_WX")));//==kangy 20160704 add
        date.setData("TYPE_01_ZFB", "TEXT",
        		"֧����:" + df.format(this.getValueDouble("TYPE_01_ZFB")));//==kangy 20160704 add

        date.setData("TYPE_02_COUNT", "TEXT",
                     "�˷�:" + this.getValueInt("TYPE_02_COUNT") + "��");
        date.setData("TYPE_02_AMT", "TEXT",
                     "�˷ѽ��:" + df.format(this.getValueDouble("TYPE_02_AMT")));
        date.setData("TYPE_02_CASH", "TEXT",
                     "�ֽ�:" + df.format(this.getValueDouble("TYPE_02_CASH")));
        date.setData("TYPE_02_BANK", "TEXT",
                     "���п�:" + df.format(this.getValueDouble("TYPE_02_BANK")));
        date.setData("TYPE_02_MEDICAL", "TEXT",
                     "ҽ�ƿ�:" + df.format(this.getValueDouble("TYPE_02_MEDICAL")));
        date.setData("TYPE_02_DEBIT", "TEXT",
                     "ת��:" + df.format(this.getValueDouble("TYPE_02_DEBIT")));
        date.setData("TYPE_02_CHECK", "TEXT",
                     "֧Ʊ:" + df.format(this.getValueDouble("TYPE_02_CHECK")));
        date.setData("TYPE_02_LPK", "TEXT",
                "��Ʒ��:" + df.format(this.getValueDouble("TYPE_02_LPK")));//==liling 20140818 add
        date.setData("TYPE_02_XJZKQ", "TEXT",
                "�ֽ��ۿ�ȯ:" + df.format(this.getValueDouble("TYPE_02_XJZKQ")));//==liling 20140818 add
        date.setData("TYPE_02_WX", "TEXT",
        		"΢��:" + df.format(this.getValueDouble("TYPE_02_WX")));//==kangy 20160704 add
        date.setData("TYPE_02_ZFB", "TEXT",
        		"֧����:" + df.format(this.getValueDouble("TYPE_02_ZFB")));//==kangy 20160704 add
//        date.setData("TYPE_03_COUNT", "TEXT",
//                     "������ " + this.getValueInt("TYPE_03_COUNT") + "��");
//        date.setData("TYPE_03_AMT", "TEXT",
//                     "������ " + df.format(this.getValueDouble("TYPE_03_AMT")));
//        date.setData("TYPE_03_CASH", "TEXT",
//                     "�ֽ� " + df.format(this.getValueDouble("TYPE_03_CASH")));
//        date.setData("TYPE_03_BANK", "TEXT",
//                     "���п��� " + df.format(this.getValueDouble("TYPE_03_BANK")));
//        date.setData("TYPE_03_MEDICAL", "TEXT",
//                     "ҽ�ƿ��� " + df.format(this.getValueDouble("TYPE_03_MEDICAL")));
//        date.setData("TYPE_03_DEBIT", "TEXT",
//                     "ת�ˣ� " + df.format(this.getValueDouble("TYPE_03_DEBIT")));
//        date.setData("TYPE_03_CHECK", "TEXT",
//                     "֧Ʊ�� " + df.format(this.getValueDouble("TYPE_03_CHECK")));

        date.setData("TYPE_04_COUNT", "TEXT",
                     "�س�:" + this.getValueInt("TYPE_04_COUNT") + "��");
        date.setData("TYPE_04_AMT", "TEXT",
                     "�س���:" + df.format(this.getValueDouble("TYPE_04_AMT")));
        date.setData("TYPE_04_CASH", "TEXT",
                     "�ֽ�:" + df.format(this.getValueDouble("TYPE_04_CASH")));
        date.setData("TYPE_04_BANK", "TEXT",
                     "���п�:" + df.format(this.getValueDouble("TYPE_04_BANK")));
        date.setData("TYPE_04_MEDICAL", "TEXT",
                     "ҽ�ƿ�:" + df.format(this.getValueDouble("TYPE_04_MEDICAL")));
        date.setData("TYPE_04_DEBIT", "TEXT",
                     "ת��:" + df.format(this.getValueDouble("TYPE_04_DEBIT")));
        date.setData("TYPE_04_CHECK", "TEXT",
                     "֧Ʊ:" + df.format(this.getValueDouble("TYPE_04_CHECK")));
        date.setData("TYPE_04_LPK", "TEXT",
                "��Ʒ��:" + df.format(this.getValueDouble("TYPE_04_LPK")));//==liling 20140818 add
        date.setData("TYPE_04_XJZKQ", "TEXT",
                "�ֽ��ۿ�ȯ:" + df.format(this.getValueDouble("TYPE_04_XJZKQ")));//==liling 20140818 add
        date.setData("TYPE_04_WX", "TEXT",
        		"΢��:" + df.format(this.getValueDouble("TYPE_04_WX")));//==kangy 20160704 add
        date.setData("TYPE_04_ZFB", "TEXT",
        		"֧����:" + df.format(this.getValueDouble("TYPE_04_ZFB")));//==kangy 20160704 add
        date.setData("SUM_COUNT", "TEXT",
                     "�ܼ�:" + this.getValueInt("SUM_COUNT") + "��");
        date.setData("SUM_AMT", "TEXT",
                     "�ܼƽ��:" + df.format(this.getValueDouble("SUM_AMT")));
        date.setData("SUM_CASH", "TEXT",
                     "�ֽ�:" + df.format(this.getValueDouble("SUM_CASH")));
        date.setData("SUM_BANK", "TEXT",
                     "���п�:" + df.format(this.getValueDouble("SUM_BANK")));
        date.setData("SUM_MEDICAL", "TEXT",
                     "ҽ�ƿ�:" + df.format(this.getValueDouble("SUM_MEDICAL")));
        date.setData("SUM_DEBIT", "TEXT",
                     "ת��:" + df.format(this.getValueDouble("SUM_DEBIT")));
        date.setData("SUM_CHECK", "TEXT",
                     "֧Ʊ:" + df.format(this.getValueDouble("SUM_CHECK")));
        date.setData("SUM_LPK", "TEXT",
                "��Ʒ��:" + df.format(this.getValueDouble("SUM_LPK")));//==liling 20140818 add
        date.setData("SUM_XJZKQ", "TEXT",
                "�ֽ��ۿ�ȯ:" + df.format(this.getValueDouble("SUM_XJZKQ")));//==liling 20140818 add
        date.setData("SUM_WX", "TEXT",
        		"΢��:" + df.format(this.getValueDouble("SUM_WX")));//==kangy 20160704 add
        date.setData("SUM_ZFB", "TEXT",
        		"֧����:" + df.format(this.getValueDouble("SUM_ZFB")));//==kangy 20160704 add
        
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILPayInQuery.jhw",
                             date);

    }

    /**
     * ȡ��SQL
     * @param start_date String
     * @param end_date String
     * @param user_id String
     * @param transact_type String
     * @return String
     */
    private String getSQL(String start_date, String end_date, String user_id,
                          String transact_type) {
        String where = "";
        if (!"".equals(user_id)) {
            where += " AND A.CASHIER_CODE = '" + user_id + "'";
        }
        if (!"".equals(transact_type)) {
            where += " AND A.TRANSACT_TYPE = '" + transact_type + "'";
        }
        String sql = "SELECT A.RECEIPT_NO, B.CHN_DESC AS PAY_TYPE_DESC, " +
                     " C.USER_NAME, E.DEPT_CHN_DESC, F.STATION_DESC, A.MR_NO, " +
                     " A.IPD_NO, G.PAT_NAME, A.PRE_AMT, A.TRANSACT_TYPE, A.PAY_TYPE " +
                     " FROM BIL_PAY A, SYS_DICTIONARY B, SYS_OPERATOR C, " +
                     " ADM_INP D, SYS_DEPT E, SYS_STATION F, SYS_PATINFO G " +
                     " WHERE A.PAY_TYPE = B.ID AND B.GROUP_ID = 'GATHER_TYPE' " +
                     " AND A.CASHIER_CODE = C.USER_ID AND A.CASE_NO = D.CASE_NO " +
                     " AND D.DEPT_CODE = E.DEPT_CODE " +
                     " AND D.STATION_CODE = F.STATION_CODE " +
                     " AND A.MR_NO = G.MR_NO " +
                     " AND A.CHARGE_DATE BETWEEN TO_DATE(" + start_date +
                     ", 'YYYYMMDDHH24MISS') AND TO_DATE(" + end_date +
                     " ,'YYYYMMDDHH24MISS') AND A.TRANSACT_TYPE <>'03'" + where +
                     " ORDER BY RECEIPT_NO";
        return sql;
    }

    /**
     * ��������Ϣ��ֵ
     * @param parm TParm
     */
    private void setPayInfo(TParm parm) {
        int TYPE_01_COUNT = 0;
        int TYPE_02_COUNT = 0;
        int TYPE_03_COUNT = 0;
        int TYPE_04_COUNT = 0;

        double TYPE_01_AMT = 0;
        double TYPE_02_AMT = 0;
        double TYPE_03_AMT = 0;
        double TYPE_04_AMT = 0;

        double TYPE_01_CASH = 0;
        double TYPE_02_CASH = 0;
        double TYPE_03_CASH = 0;
        double TYPE_04_CASH = 0;

        double TYPE_01_BANK = 0;
        double TYPE_02_BANK = 0;
        double TYPE_03_BANK = 0;
        double TYPE_04_BANK = 0;

        double TYPE_01_MEDICAL = 0;
        double TYPE_02_MEDICAL = 0;
        double TYPE_03_MEDICAL = 0;
        double TYPE_04_MEDICAL = 0;

        double TYPE_01_DEBIT = 0;
        double TYPE_02_DEBIT = 0;
        double TYPE_03_DEBIT = 0;
        double TYPE_04_DEBIT = 0;

        double TYPE_01_CHECK = 0;
        double TYPE_02_CHECK = 0;
        double TYPE_03_CHECK = 0;
        double TYPE_04_CHECK = 0;
        //==liling 20140818 add start==
        double TYPE_01_LPK = 0;
        double TYPE_02_LPK = 0;
        double TYPE_03_LPK = 0;
        double TYPE_04_LPK = 0;
        double TYPE_01_XJZKQ = 0;
        double TYPE_02_XJZKQ = 0;
        double TYPE_03_XJZKQ = 0;
        double TYPE_04_XJZKQ = 0;
      //==liling 20140818 add end==
        
        //==kagny 20160704 add start==
        double TYPE_01_WX = 0;
        double TYPE_02_WX = 0;
        double TYPE_03_WX = 0;
        double TYPE_04_WX = 0;
        double TYPE_01_ZFB = 0;
        double TYPE_02_ZFB = 0;
        double TYPE_03_ZFB = 0;
        double TYPE_04_ZFB = 0;
      //==liling 20160704 add end==
        for (int i = 0; i < parm.getCount("RECEIPT_NO"); i++) {
            if ("01".equals(parm.getValue("TRANSACT_TYPE", i))) {
                TYPE_01_COUNT++;
                if ("C0".equals(parm.getValue("PAY_TYPE", i))) {//�ֽ�
                    TYPE_01_CASH += parm.getDouble("PRE_AMT", i);
                } else if ("C1".equals(parm.getValue("PAY_TYPE", i))) {//ˢ��
                    TYPE_01_BANK += parm.getDouble("PRE_AMT", i);
                } else if ("EKT".equals(parm.getValue("PAY_TYPE",
                        i))) {//ҽ�ƿ�
                    TYPE_01_MEDICAL += parm.getDouble("PRE_AMT", i);
                } else if ("C4".equals(parm.getValue("PAY_TYPE", i))) {//Ӧ��
                    TYPE_01_DEBIT += parm.getDouble("PRE_AMT", i);
                } else if ("T0".equals(parm.getValue("PAY_TYPE", i))) {//֧Ʊ
                    TYPE_01_CHECK += parm.getDouble("PRE_AMT", i);
                } else if ("LPK".equals(parm.getValue("PAY_TYPE", i))) {//��Ʒ��
                    TYPE_01_LPK += parm.getDouble("PRE_AMT", i);
                }else if ("XJZKQ".equals(parm.getValue("PAY_TYPE", i))) {//�ֽ��ۿ�ȯ
                    TYPE_01_XJZKQ += parm.getDouble("PRE_AMT", i);
                }else if ("WX".equals(parm.getValue("PAY_TYPE", i))) {//΢��
                    TYPE_01_WX += parm.getDouble("PRE_AMT", i);
                }
                else if ("ZFB".equals(parm.getValue("PAY_TYPE", i))) {//֧����
                    TYPE_01_ZFB += parm.getDouble("PRE_AMT", i);
                }
                TYPE_01_AMT += parm.getDouble("PRE_AMT", i);
            } else if ("02".equals(parm.getValue("TRANSACT_TYPE", i))) {
                TYPE_02_COUNT++;
                if ("C0".equals(parm.getValue("PAY_TYPE", i))) {
                    TYPE_02_CASH += parm.getDouble("PRE_AMT", i);
                } else if ("C1".equals(parm.getValue("PAY_TYPE", i))) {
                    TYPE_02_BANK += parm.getDouble("PRE_AMT", i);
                } else if ("EKT".equals(parm.getValue("PAY_TYPE",
                        i))) {
                    TYPE_02_MEDICAL += parm.getDouble("PRE_AMT", i);
                } else if ("C4".equals(parm.getValue("PAY_TYPE", i))) {
                    TYPE_02_DEBIT += parm.getDouble("PRE_AMT", i);
                } else if ("T0".equals(parm.getValue("PAY_TYPE", i))) {
                    TYPE_02_CHECK += parm.getDouble("PRE_AMT", i);
                } else if ("LPK".equals(parm.getValue("PAY_TYPE", i))) {//��Ʒ��
                    TYPE_02_LPK += parm.getDouble("PRE_AMT", i);
                }else if ("XJZKQ".equals(parm.getValue("PAY_TYPE", i))) {//�ֽ��ۿ�ȯ
                    TYPE_02_XJZKQ += parm.getDouble("PRE_AMT", i);
                }else if ("WX".equals(parm.getValue("PAY_TYPE", i))) {//΢��
                    TYPE_02_WX += parm.getDouble("PRE_AMT", i);
                }else if ("ZFB".equals(parm.getValue("PAY_TYPE", i))) {//֧����
                    TYPE_02_ZFB += parm.getDouble("PRE_AMT", i);
                }
                TYPE_02_AMT += parm.getDouble("PRE_AMT", i);
            } else if ("03".equals(parm.getValue("TRANSACT_TYPE", i))) {
                TYPE_03_COUNT++;
                if ("C0".equals(parm.getValue("PAY_TYPE", i))) {
                    TYPE_03_CASH += parm.getDouble("PRE_AMT", i);
                } else if ("C1".equals(parm.getValue("PAY_TYPE", i))) {
                    TYPE_03_BANK += parm.getDouble("PRE_AMT", i);
                } else if ("EKT".equals(parm.getValue("PAY_TYPE",
                        i))) {
                    TYPE_03_MEDICAL += parm.getDouble("PRE_AMT", i);
                } else if ("C4".equals(parm.getValue("PAY_TYPE", i))) {
                    TYPE_03_DEBIT += parm.getDouble("PRE_AMT", i);
                } else if ("T0".equals(parm.getValue("PAY_TYPE", i))) {
                    TYPE_03_CHECK += parm.getDouble("PRE_AMT", i);
                } else if ("LPK".equals(parm.getValue("PAY_TYPE", i))) {//��Ʒ��
                    TYPE_03_LPK += parm.getDouble("PRE_AMT", i);
                }else if ("XJZKQ".equals(parm.getValue("PAY_TYPE", i))) {//�ֽ��ۿ�ȯ
                    TYPE_03_XJZKQ += parm.getDouble("PRE_AMT", i);
                }else if ("WX".equals(parm.getValue("PAY_TYPE", i))) {//΢��
                    TYPE_03_WX += parm.getDouble("PRE_AMT", i);
                }else if ("ZFB".equals(parm.getValue("PAY_TYPE", i))) {//֧����
                    TYPE_03_ZFB += parm.getDouble("PRE_AMT", i);
                }
                TYPE_03_AMT += parm.getDouble("PRE_AMT", i);
            } else if ("04".equals(parm.getValue("TRANSACT_TYPE", i))) {
                TYPE_04_COUNT++;
                if ("C0".equals(parm.getValue("PAY_TYPE", i))) {
                    TYPE_04_CASH += parm.getDouble("PRE_AMT", i);
                } else if ("C1".equals(parm.getValue("PAY_TYPE", i))) {
                    TYPE_04_BANK += parm.getDouble("PRE_AMT", i);
                } else if ("EKT".equals(parm.getValue("PAY_TYPE",
                        i))) {
                    TYPE_04_MEDICAL += parm.getDouble("PRE_AMT", i);
                } else if ("C4".equals(parm.getValue("PAY_TYPE", i))) {
                    TYPE_04_DEBIT += parm.getDouble("PRE_AMT", i);
                } else if ("T0".equals(parm.getValue("PAY_TYPE", i))) {
                    TYPE_04_CHECK += parm.getDouble("PRE_AMT", i);
                } else if ("LPK".equals(parm.getValue("PAY_TYPE", i))) {//��Ʒ��
                    TYPE_04_LPK += parm.getDouble("PRE_AMT", i);
                }else if ("XJZKQ".equals(parm.getValue("PAY_TYPE", i))) {//�ֽ��ۿ�ȯ
                    TYPE_04_XJZKQ += parm.getDouble("PRE_AMT", i);
                }else if ("WX".equals(parm.getValue("PAY_TYPE", i))) {//΢��
                    TYPE_04_WX += parm.getDouble("PRE_AMT", i);
                }else if ("ZFB".equals(parm.getValue("PAY_TYPE", i))) {//֧����
                    TYPE_04_ZFB += parm.getDouble("PRE_AMT", i);
                }
                TYPE_04_AMT += parm.getDouble("PRE_AMT", i);
            }
        }

        this.setValue("TYPE_01_COUNT", TYPE_01_COUNT);
        this.setValue("TYPE_02_COUNT", TYPE_02_COUNT);
        this.setValue("TYPE_03_COUNT", TYPE_03_COUNT);
        this.setValue("TYPE_04_COUNT", TYPE_04_COUNT);

        this.setValue("TYPE_01_AMT", TYPE_01_AMT);
        this.setValue("TYPE_02_AMT", TYPE_02_AMT);
        this.setValue("TYPE_03_AMT", TYPE_03_AMT);
        this.setValue("TYPE_04_AMT", TYPE_04_AMT);

        this.setValue("TYPE_01_CASH", TYPE_01_CASH);
        this.setValue("TYPE_02_CASH", TYPE_02_CASH);
        this.setValue("TYPE_03_CASH", TYPE_03_CASH);
        this.setValue("TYPE_04_CASH", TYPE_04_CASH);

        this.setValue("TYPE_01_BANK", TYPE_01_BANK);
        this.setValue("TYPE_02_BANK", TYPE_02_BANK);
        this.setValue("TYPE_03_BANK", TYPE_03_BANK);
        this.setValue("TYPE_04_BANK", TYPE_04_BANK);

        this.setValue("TYPE_01_MEDICAL", TYPE_01_MEDICAL);
        this.setValue("TYPE_02_MEDICAL", TYPE_02_MEDICAL);
        this.setValue("TYPE_03_MEDICAL", TYPE_03_MEDICAL);
        this.setValue("TYPE_04_MEDICAL", TYPE_04_MEDICAL);

        this.setValue("TYPE_01_DEBIT", TYPE_01_DEBIT);
        this.setValue("TYPE_02_DEBIT", TYPE_02_DEBIT);
        this.setValue("TYPE_03_DEBIT", TYPE_03_DEBIT);
        this.setValue("TYPE_04_DEBIT", TYPE_04_DEBIT);

        this.setValue("TYPE_01_CHECK", TYPE_01_CHECK);
        this.setValue("TYPE_02_CHECK", TYPE_02_CHECK);
        this.setValue("TYPE_03_CHECK", TYPE_03_CHECK);
        this.setValue("TYPE_04_CHECK", TYPE_04_CHECK);
        
        //==liling 20140818 add start===
        this.setValue("TYPE_01_LPK", TYPE_01_LPK);
        this.setValue("TYPE_02_LPK", TYPE_02_LPK);
        this.setValue("TYPE_03_LPK", TYPE_03_LPK);
        this.setValue("TYPE_04_LPK", TYPE_04_LPK);

        this.setValue("TYPE_01_XJZKQ", TYPE_01_XJZKQ);
        this.setValue("TYPE_02_XJZKQ", TYPE_02_XJZKQ);
        this.setValue("TYPE_03_XJZKQ", TYPE_03_XJZKQ);
        this.setValue("TYPE_04_XJZKQ", TYPE_04_XJZKQ);
        
        //==kangy 20160704 add start===
        this.setValue("TYPE_01_WX", TYPE_01_WX);
        this.setValue("TYPE_02_WX", TYPE_02_WX);
        this.setValue("TYPE_03_WX", TYPE_03_WX);
        this.setValue("TYPE_04_WX", TYPE_04_WX);

        this.setValue("TYPE_01_ZFB", TYPE_01_ZFB);
        this.setValue("TYPE_02_ZFB", TYPE_02_ZFB);
        this.setValue("TYPE_03_ZFB", TYPE_03_ZFB);
        this.setValue("TYPE_04_ZFB", TYPE_04_ZFB);
        this.setValue("SUM_WX",
                TYPE_01_WX + TYPE_02_WX + TYPE_03_WX + TYPE_04_WX);
        this.setValue("SUM_ZFB",
                TYPE_01_ZFB + TYPE_02_ZFB + TYPE_03_ZFB + TYPE_04_ZFB);
      //==kangy 20160704 add end====  
        
        this.setValue("SUM_LPK",
                TYPE_01_LPK + TYPE_02_LPK + TYPE_03_LPK + TYPE_04_LPK);
        this.setValue("SUM_XJZKQ",
                TYPE_01_XJZKQ + TYPE_02_XJZKQ + TYPE_03_XJZKQ + TYPE_04_XJZKQ);
        //==liling 20140818 add end====

        this.setValue("SUM_COUNT",
                      TYPE_01_COUNT + TYPE_02_COUNT + TYPE_03_COUNT +
                      TYPE_04_COUNT);
        this.setValue("SUM_AMT",
                      TYPE_01_AMT + TYPE_02_AMT + TYPE_03_AMT + TYPE_04_AMT);
        this.setValue("SUM_CASH",
                      TYPE_01_CASH + TYPE_02_CASH + TYPE_03_CASH + TYPE_04_CASH);
        this.setValue("SUM_BANK",
                      TYPE_01_BANK + TYPE_02_BANK + TYPE_03_BANK + TYPE_04_BANK);
        this.setValue("SUM_MEDICAL",
                      TYPE_01_MEDICAL + TYPE_02_MEDICAL + TYPE_03_MEDICAL +
                      TYPE_04_MEDICAL);
        this.setValue("SUM_DEBIT",
                      TYPE_01_DEBIT + TYPE_02_DEBIT + TYPE_03_DEBIT +
                      TYPE_04_DEBIT);
        this.setValue("SUM_CHECK",
                      TYPE_01_CHECK + TYPE_02_CHECK + TYPE_03_CHECK +
                      TYPE_04_CHECK);
    }
    
	// ==========modify-begin (by wanglong 20120710)===============
	// ����Ϊ��Ӧ��굥���¼��ķ��������ڻ�ȡȫ����Ԫ���ֵ������ĳ�������Լ���ظ���������
	/**
	 * �����������������
	 * @param table TTable
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				int i = table.getTable().columnAtPoint(me.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = table.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				comparator.setDes(ascending);
				comparator.setCol(col);
				java.util.Collections.sort(vct, comparator);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);
				//getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * �õ� Vector ֵ
	 * @param parm TParm
	 * @param group String
	 * @param names String
	 * @param size int
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * ת��parm�е���
	 * @param columnName String[]
	 * @param tblColumnName String
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * vectoryת��param
	 * @param vectorTable Vector
	 * @param parmTable TParm
	 * @param columnNames String
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);
	}
	// ==========modify-end========================================
}
