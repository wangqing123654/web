package com.javahis.ui.bil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.bil.BILPrintTool;
import jdo.bil.BILSQL;
import jdo.sys.SystemTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;


/**
 * <p> Title: Ʊ�����ϻ��������  </p>
 * 
 * <p> Description: Ʊ�����ϻ��������  </p>
 * 
 * <p> Copyright: Copyright (c) Liu dongyang 2008 </p>
 * 
 * <p> Company: JavaHis </p>
 * 
 * @author wangl
 * @version 1.0
 */
public class BILExportRecpControl extends TControl {

    TTable table;
    TParm table2;
    TParm table3;

    private boolean ascending = false;// �������� 
    private int sortColumn = -1;// ��������
    private BILComparator compare = new BILComparator();// ��������add by wanglong 20130120
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        // �˵�tableר�õļ���
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onTableComponent");
        onSetTableAttribute();// add by wanglong 20130109
        initPage();
        addListener(table);//add by wanglong 20130120
    }

    /**
     * �˵�table�����¼�
     * 
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable printTable = (TTable) obj;
        printTable.acceptText();
        return true;
    }

    /**
     * ��ʼ����������
     */
    public void initPage() {
        // ��ʼ����ѯ��ʱ,��ʱ
        String yesterday =
                StringTool.getString(StringTool.rollDate(SystemTool.getInstance().getDate(), -1),
                                     "yyyy/MM/dd");
        String today = StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd");
        setValue("S_DATE", yesterday);
        setValue("E_DATE", today);
        String todayTime = StringTool.getString(SystemTool.getInstance().getDate(), "HH:mm:ss");
        setValue("S_TIME", "00:00:00");
//        setValue("E_TIME", todayTime);
        setValue("E_TIME", "23:59:59");
        setValue("SEL_O", "Y");
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        String sTime =
                StringTool.getString((Timestamp) this.getValue("S_DATE"), "yyyyMMdd")
                        + StringTool.getString((Timestamp) this.getValue("S_TIME"), "HHmmss");
        String eTime =
                StringTool.getString((Timestamp) this.getValue("E_DATE"), "yyyyMMdd")
                        + StringTool.getString((Timestamp) this.getValue("E_TIME"), "HHmmss");
        String recpType = "";
        String sql = "";
        TParm result = new TParm();
        double charge01 = 0.00;
        double charge02 = 0.00;
        double charge0102 = 0.00;// add by wanglong 20130109
        double charge03 = 0.00;
        double charge04 = 0.00;
        double charge0304 = 0.00;// add by wanglong 20130109
        double charge05 = 0.00;
        double charge06 = 0.00;
        double charge07 = 0.00;
        double charge08 = 0.00;
        double charge09 = 0.00;
        double charge10 = 0.00;
        double charge11 = 0.00;
        double charge12 = 0.00;
        double charge13 = 0.00;
        double charge14 = 0.00;
        double charge15 = 0.00;
        double charge16 = 0.00;
        double charge17 = 0.00;
        double charge18 = 0.00;
        double charge19 = 0.00;
        double charge20 = 0.00;// add by wanglong 20130109
        double totAmt = 0.00;
        int count = 0;
        if (this.getValue("SEL_O").toString().equals("Y")) {// ����  modify by wanglong 20130120
            recpType = "OPB";
            sql = BILSQL.getRecpDataO(sTime, eTime, null);// modify by wanglong 20130120
            result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                messageBox("���ݲ�ѯ����  " + result.getErrText());
                return;
            }
            count = result.getCount();
            for (int i = 0; i < count; i++) {
                charge0102 = charge0102 + result.getDouble("CHARGE0102", i);
                charge03 = charge03 + result.getDouble("CHARGE03", i);
                charge04 = charge04 + result.getDouble("CHARGE04", i);
                charge05 = charge05 + result.getDouble("CHARGE05", i);
                charge06 = charge06 + result.getDouble("CHARGE06", i);
                charge07 = charge07 + result.getDouble("CHARGE07", i);
                charge08 = charge08 + result.getDouble("CHARGE08", i);
                charge09 = charge09 + result.getDouble("CHARGE09", i);
                charge10 = charge10 + result.getDouble("CHARGE10", i);
                charge11 = charge11 + result.getDouble("CHARGE11", i);
                charge12 = charge12 + result.getDouble("CHARGE12", i);
                charge13 = charge13 + result.getDouble("CHARGE13", i);
                charge14 = charge14 + result.getDouble("CHARGE14", i);
                charge15 = charge15 + result.getDouble("CHARGE15", i);
                charge16 = charge16 + result.getDouble("CHARGE16", i);
                charge17 = charge17 + result.getDouble("CHARGE17", i);
                charge18 = charge18 + result.getDouble("CHARGE18", i);
                charge19 = charge19 + result.getDouble("CHARGE19", i);
                totAmt = totAmt + result.getDouble("TOT_AMT", i);
            }
            result.addData("INV_NO", "");
            result.addData("PRINT_DATE", "");
            result.addData("RECEIPT_NO", "");
            result.addData("CANCEL_FLG", "");
            result.addData("PAT_NAME", "��  �ƣ�");
            result.addData("TOT_AMT", StringTool.round(totAmt, 2));
            result.addData("CHARGE0102", StringTool.round(charge0102, 2));
            result.addData("CHARGE03", StringTool.round(charge03, 2));
            result.addData("CHARGE04", StringTool.round(charge04, 2));
            result.addData("CHARGE05", StringTool.round(charge05, 2));
            result.addData("CHARGE10", StringTool.round(charge10, 2));
            result.addData("CHARGE06", StringTool.round(charge06, 2));
            result.addData("CHARGE07", StringTool.round(charge07, 2));
            result.addData("CHARGE08", StringTool.round(charge08, 2));
            result.addData("CHARGE09", StringTool.round(charge09, 2));
            result.addData("CHARGE18", StringTool.round(charge18, 2));
            result.addData("CHARGE13", StringTool.round(charge13, 2));
            result.addData("CHARGE14", StringTool.round(charge14, 2));
            result.addData("CHARGE15", StringTool.round(charge15, 2));
            result.addData("CHARGE17", StringTool.round(charge17, 2));
            result.addData("CHARGE16", StringTool.round(charge16, 2));
            result.addData("CHARGE11", StringTool.round(charge11, 2));
            result.addData("CHARGE12", StringTool.round(charge12, 2));
            result.addData("CHARGE19", StringTool.round(charge19, 2));
            result.setCount(count + 1);
        }
        else if (this.getValue("SEL_I").toString().equals("Y")) {// סԺ modify by wanglong 20130120
            recpType = "IBS";
            sql = BILSQL.getRecpDataI(sTime, eTime, null);// modify by wanglong 20130120
            TParm selRecpDParm = new TParm(TJDODBTool.getInstance().select(sql));
            if (selRecpDParm.getErrCode() < 0) {
                messageBox("���ݲ�ѯ����  " + selRecpDParm.getErrText());
                return;
            }
            HashSet invNo = new HashSet();
            for (int i = 0; i < selRecpDParm.getCount(); i++) {
                invNo.add(selRecpDParm.getValue("INV_NO", i));
            }
            // System.out.println("Ʊ����"+invNo);
            Iterator iter = invNo.iterator();
            int row = 0; // ��¼����
            while (iter.hasNext()) {
                String invNoS = iter.next().toString(); // �շ�ԱCODE
                // System.out.println("Ʊ��"+invNoS);
                double sum = 0; // �����ܽ��
                count = selRecpDParm.getCount("REXP_CODE");
                String rexpCode = "";
                double totAmtI = 0.00;
                double wrtOffSingle = 0.00;
                result.addData("INV_NO", "");
                result.addData("PRINT_DATE", "");
                result.addData("RECEIPT_NO", "");
                result.addData("CANCEL_FLG", "");
                result.addData("PAT_NAME", "");
                result.addData("CHARGE01", 0.00);
                result.addData("CHARGE02", 0.00);
                result.addData("CHARGE0304", 0.00);
                result.addData("CHARGE05", 0.00);
                result.addData("CHARGE06", 0.00);
                result.addData("CHARGE07", 0.00);
                result.addData("CHARGE08", 0.00);
                result.addData("CHARGE09", 0.00);
                result.addData("CHARGE10", 0.00);
                result.addData("CHARGE11", 0.00);
                result.addData("CHARGE12", 0.00);
                result.addData("CHARGE13", 0.00);
                result.addData("CHARGE14", 0.00);
                result.addData("CHARGE15", 0.00);
                result.addData("CHARGE16", 0.00);
                result.addData("CHARGE17", 0.00);
                result.addData("CHARGE18", 0.00);
                result.addData("CHARGE19", 0.00);
                result.addData("CHARGE20", 0.00);
                result.addData("TOT_AMT", 0.00);
                for (int i = 0; i < selRecpDParm.getCount(); i++) {
                    if (invNoS.equals(selRecpDParm.getValue("INV_NO", i))) {
                        rexpCode = selRecpDParm.getValue("REXP_CODE", i);
                        wrtOffSingle = selRecpDParm.getDouble("WRT_OFF_AMT", i);
                        totAmtI = totAmtI + wrtOffSingle;
                        sum = sum + wrtOffSingle;
                        result.setData("INV_NO", row, selRecpDParm.getData("INV_NO", i));
                        result.setData("PRINT_DATE", row, selRecpDParm.getData("PRINT_DATE", i));
                        result.setData("RECEIPT_NO", row, selRecpDParm.getData("RECEIPT_NO", i));
                        result.setData("CANCEL_FLG", row, selRecpDParm.getData("CANCEL_FLG", i));
                        result.setData("PAT_NAME", row, selRecpDParm.getData("PAT_NAME", i));
                        if ("020".equals(rexpCode)) { // ��λ��
                            result.setData("CHARGE01", row, result.getDouble("CHARGE01", row)
                                    + wrtOffSingle);
                        } else if ("021".equals(rexpCode)) { // ����
                            result.setData("CHARGE02", row, result.getDouble("CHARGE02", row)
                                    + wrtOffSingle);
                        } else if ("022.01".equals(rexpCode)) { // ��ҩ�ѣ������أ�
                            // result.setData("CHARGE03", row, wrtOffSingle);
                            charge03 += wrtOffSingle;// modify by wanglong 20130129
                        } else if ("022.02".equals(rexpCode)) { // ��ҩ�ѣ��ǿ����أ�
                            // result.setData("CHARGE04", row, wrtOffSingle);
                            charge04 += wrtOffSingle;// modify by wanglong 20130129
                        } else if ("023".equals(rexpCode)) { // �г�ҩ
                            result.setData("CHARGE05", row, result.getDouble("CHARGE05", row)
                                    + wrtOffSingle);
                        } else if ("024".equals(rexpCode)) { // �в�ҩ
                            result.setData("CHARGE06", row, result.getDouble("CHARGE06", row)
                                    + wrtOffSingle);
                        } else if ("025".equals(rexpCode)) { // ����
                            result.setData("CHARGE07", row, result.getDouble("CHARGE07", row)
                                    + wrtOffSingle);
                        } else if ("026".equals(rexpCode)) { // ���Ʒ�
                            result.setData("CHARGE08", row, result.getDouble("CHARGE08", row)
                                    + wrtOffSingle);
                        } else if ("027".equals(rexpCode)) { // �����
                            result.setData("CHARGE09", row, result.getDouble("CHARGE09", row)
                                    + wrtOffSingle);
                        } else if ("028".equals(rexpCode)) { // ������;
                            result.setData("CHARGE10", row, result.getDouble("CHARGE10", row)
                                    + wrtOffSingle);
                        } else if ("029".equals(rexpCode)) { // �����
                            result.setData("CHARGE11", row, result.getDouble("CHARGE11", row)
                                    + wrtOffSingle);
                        } else if ("02A".equals(rexpCode)) { // ��Ѫ��
                            result.setData("CHARGE12", row, result.getDouble("CHARGE12", row)
                                    + wrtOffSingle);
                        } else if ("02B".equals(rexpCode)) { // ������
                            result.setData("CHARGE13", row, result.getDouble("CHARGE13", row)
                                    + wrtOffSingle);
                        } else if ("02C".equals(rexpCode)) { // ������
                            result.setData("CHARGE14", row, result.getDouble("CHARGE14", row)
                                    + wrtOffSingle);
                        } else if ("02D".equals(rexpCode)) { // �����
                            result.setData("CHARGE15", row, result.getDouble("CHARGE15", row)
                                    + wrtOffSingle);
                        } else if ("02E".equals(rexpCode)) { // �Ҵ���
                            result.setData("CHARGE16", row, result.getDouble("CHARGE16", row)
                                    + wrtOffSingle);
                        } else if ("032".equals(rexpCode)) { // CT��
                            result.setData("CHARGE17", row, result.getDouble("CHARGE17", row)
                                    + wrtOffSingle);
                        } else if ("033".equals(rexpCode)) { // MR��
                            result.setData("CHARGE18", row, result.getDouble("CHARGE18", row)
                                    + wrtOffSingle);
                        } else if ("02F".equals(rexpCode)) { // �ԷѲ���
                            result.setData("CHARGE19", row, result.getDouble("CHARGE19", row)
                                    + wrtOffSingle);
                        } else if ("035".equals(rexpCode)) { // ���Ϸ�
                            result.setData("CHARGE20", row, result.getDouble("CHARGE20", row)
                                    + wrtOffSingle);
                        }
                        result.setData("TOT_AMT", row, StringTool.round(sum, 2));
                    }
                }
               // result.setData("CHARGE0304", row, result.getDouble("CHARGE03", row) + result.getDouble("CHARGE04", row));
                result.setData("CHARGE0304", row, charge03 + charge04);// modify by wanglong 20130120
                charge03 = 0;// add by wanglong 20130129
                charge04 = 0;// add by wanglong 20130129
                row++;
            }// while����
            result.setCount(row);
            charge01 = 0.00;
            charge02 = 0.00;
            charge0304 = 0.00;
            charge05 = 0.00;
            charge06 = 0.00;
            charge07 = 0.00;
            charge08 = 0.00;
            charge09 = 0.00;
            charge10 = 0.00;
            charge11 = 0.00;
            charge12 = 0.00;
            charge13 = 0.00;
            charge14 = 0.00;
            charge15 = 0.00;
            charge16 = 0.00;
            charge17 = 0.00;
            charge18 = 0.00;
            charge19 = 0.00;
            charge20 = 0.00;
            totAmt = 0.00;
            count = result.getCount();
            for (int i = 0; i < count; i++) {
                charge01 = charge01 + result.getDouble("CHARGE01", i);
                charge02 = charge02 + result.getDouble("CHARGE02", i);
                charge0304 = charge0304 + result.getDouble("CHARGE0304", i);
                charge05 = charge05 + result.getDouble("CHARGE05", i);
                charge06 = charge06 + result.getDouble("CHARGE06", i);
                charge07 = charge07 + result.getDouble("CHARGE07", i);
                charge08 = charge08 + result.getDouble("CHARGE08", i);
                charge09 = charge09 + result.getDouble("CHARGE09", i);
                charge10 = charge10 + result.getDouble("CHARGE10", i);
                charge11 = charge11 + result.getDouble("CHARGE11", i);
                charge12 = charge12 + result.getDouble("CHARGE12", i);
                charge13 = charge13 + result.getDouble("CHARGE13", i);
                charge14 = charge14 + result.getDouble("CHARGE14", i);
                charge15 = charge15 + result.getDouble("CHARGE15", i);
                charge16 = charge16 + result.getDouble("CHARGE16", i);
                charge17 = charge17 + result.getDouble("CHARGE17", i);
                charge18 = charge18 + result.getDouble("CHARGE18", i);
                charge19 = charge19 + result.getDouble("CHARGE19", i);
                charge20 = charge20 + result.getDouble("CHARGE20", i);
                totAmt = totAmt + result.getDouble("TOT_AMT", i);
            }
      
            result.addData("INV_NO", "");
            result.addData("PRINT_DATE", "");
            result.addData("RECEIPT_NO", "");
            result.addData("CANCEL_FLG", "");
            result.addData("PAT_NAME", "��  �ƣ�");
            result.addData("TOT_AMT", StringTool.round(totAmt, 2));
            result.addData("CHARGE01", StringTool.round(charge01, 2));
            result.addData("CHARGE02", StringTool.round(charge02, 2));
            result.addData("CHARGE0304", StringTool.round(charge0304, 2));
            result.addData("CHARGE05", StringTool.round(charge05, 2));
            result.addData("CHARGE06", StringTool.round(charge06, 2));
            result.addData("CHARGE07", StringTool.round(charge07, 2));
            result.addData("CHARGE08", StringTool.round(charge08, 2));
            result.addData("CHARGE09", StringTool.round(charge09, 2));
            result.addData("CHARGE10", StringTool.round(charge10, 2));
            result.addData("CHARGE11", StringTool.round(charge11, 2));
            result.addData("CHARGE12", StringTool.round(charge12, 2));
            result.addData("CHARGE13", StringTool.round(charge13, 2));
            result.addData("CHARGE14", StringTool.round(charge14, 2));
            result.addData("CHARGE15", StringTool.round(charge15, 2));
            result.addData("CHARGE16", StringTool.round(charge16, 2));
            result.addData("CHARGE17", StringTool.round(charge17, 2));
            result.addData("CHARGE18", StringTool.round(charge18, 2));
            result.addData("CHARGE20", StringTool.round(charge20, 2));
            result.addData("CHARGE19", StringTool.round(charge19, 2));
            result.setCount(count+1);
        }
        else if (this.getValue("SEL_H").toString().equals("Y")){// ���� modify by wanglong 20130120
            recpType = "HRM";//modify by wanglong 20130120
            sql = BILSQL.getRecpDataH(sTime, eTime, null);// modify by wanglong 20130120
            result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                messageBox("���ݲ�ѯ����  " + result.getErrText());
                return;
            }
            count = result.getCount();
            for (int i = 0; i < count; i++) {
                charge0102 = charge0102 + result.getDouble("CHARGE0102", i);
                charge03 = charge03 + result.getDouble("CHARGE03", i);
                charge04 = charge04 + result.getDouble("CHARGE04", i);
                charge05 = charge05 + result.getDouble("CHARGE05", i);
                charge06 = charge06 + result.getDouble("CHARGE06", i);
                charge07 = charge07 + result.getDouble("CHARGE07", i);
                charge08 = charge08 + result.getDouble("CHARGE08", i);
                charge09 = charge09 + result.getDouble("CHARGE09", i);
                charge10 = charge10 + result.getDouble("CHARGE10", i);
                charge11 = charge11 + result.getDouble("CHARGE11", i);
                charge12 = charge12 + result.getDouble("CHARGE12", i);
                charge13 = charge13 + result.getDouble("CHARGE13", i);
                charge14 = charge14 + result.getDouble("CHARGE14", i);
                charge15 = charge15 + result.getDouble("CHARGE15", i);
                charge16 = charge16 + result.getDouble("CHARGE16", i);
                charge17 = charge17 + result.getDouble("CHARGE17", i);
                charge18 = charge18 + result.getDouble("CHARGE18", i);
                charge19 = charge19 + result.getDouble("CHARGE19", i);
                totAmt = totAmt + result.getDouble("TOT_AMT", i);
            }
            result.addData("INV_NO", "");
            result.addData("PRINT_DATE", "");
            result.addData("RECEIPT_NO", "");
            result.addData("CANCEL_FLG", "");
            result.addData("COMPANY_DESC", "");
            result.addData("PAT_NAME", "��  �ƣ�");
            result.addData("TOT_AMT", StringTool.round(totAmt, 2));
            result.addData("CHARGE0102", StringTool.round(charge0102, 2));
            result.addData("CHARGE03", StringTool.round(charge03, 2));
            result.addData("CHARGE04", StringTool.round(charge04, 2));
            result.addData("CHARGE05", StringTool.round(charge05, 2));
            result.addData("CHARGE10", StringTool.round(charge10, 2));
            result.addData("CHARGE06", StringTool.round(charge06, 2));
            result.addData("CHARGE07", StringTool.round(charge07, 2));
            result.addData("CHARGE08", StringTool.round(charge08, 2));
            result.addData("CHARGE09", StringTool.round(charge09, 2));
            result.addData("CHARGE18", StringTool.round(charge18, 2));
            result.addData("CHARGE13", StringTool.round(charge13, 2));
            result.addData("CHARGE14", StringTool.round(charge14, 2));
            result.addData("CHARGE15", StringTool.round(charge15, 2));
            result.addData("CHARGE17", StringTool.round(charge17, 2));
            result.addData("CHARGE16", StringTool.round(charge16, 2));
            result.addData("CHARGE11", StringTool.round(charge11, 2));
            result.addData("CHARGE12", StringTool.round(charge12, 2));
            result.addData("CHARGE19", StringTool.round(charge19, 2));
            result.setCount(count + 1); 
        }
        else if (this.getValue("SEL_R").toString().equals("Y")) {//�Һ� add by wanglong 20130120
            recpType = "REG";
            sql = BILSQL.getRecpDataR(sTime, eTime, null);// modify by wanglong 20130120
            result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                messageBox("���ݲ�ѯ����  " + result.getErrText());
                return;
            }
            count = result.getCount();
            double regFeeReal = 0;
            double clinicFeeReal = 0;
            double spcFee = 0;
            double otherFee1 = 0;
            double otherFee2 = 0;
            double otherFee3 = 0;
            for (int i = 0; i < count; i++) {
                regFeeReal = regFeeReal + result.getDouble("REG_FEE_REAL", i);
                clinicFeeReal = clinicFeeReal + result.getDouble("CLINIC_FEE_REAL", i);
                spcFee = spcFee + result.getDouble("SPC_FEE", i);
                otherFee1 = otherFee1 + result.getDouble("OTHER_FEE1", i);
                otherFee2 = otherFee2 + result.getDouble("OTHER_FEE2", i);
                otherFee3 = otherFee3 + result.getDouble("OTHER_FEE3", i);
                totAmt = totAmt + result.getDouble("AR_AMT", i);
            }
            result.addData("INV_NO", "");
            result.addData("PRINT_DATE", "");
            result.addData("RECEIPT_NO", "");
            result.addData("CANCEL_FLG", "");
            result.addData("PAT_NAME", "��  �ƣ�");
            result.addData("AR_AMT", StringTool.round(totAmt, 2));
            result.addData("REG_FEE_REAL", StringTool.round(regFeeReal, 2));
            result.addData("CLINIC_FEE_REAL", StringTool.round(clinicFeeReal, 2));
            result.addData("SPC_FEE", StringTool.round(spcFee, 2));
            result.addData("OTHER_FEE1", StringTool.round(otherFee1, 2));
            result.addData("OTHER_FEE2", StringTool.round(otherFee2, 2));
            result.addData("OTHER_FEE3", StringTool.round(otherFee3, 2));
            result.setCount(count + 1);
        }
        table.setDSValue();
        table.setParmValue(result);
        // ==========pangben modify 20110704 ��ѯ��������û�й���Ժ��
        String sqlTable2 = BILSQL.getRecpDataAll(sTime, eTime, recpType, null);//ȫ��Ʊ�� modify by wanglong 20130120
        // System.out.println("Ʊ���ܲ�ѯ" + sqlTable2);
        TParm table2Parm = new TParm(TJDODBTool.getInstance().select(sqlTable2));
        if (table2Parm.getErrCode() < 0) {
            System.out.println("�����ݲ�ѯ����  " + table2Parm.getErrText());
            return;
        }
        int nowRecpCount = table2Parm.getCount("INV_NO");
        // ==========pangben modify 20110704 ��ѯ��������û�й���Ժ��
        String sqlTable2Left = BILSQL.getRecpDataLeft(sTime, eTime, recpType, null);//ȫ������Ʊ�� modify by wanglong 20130120
        TParm table2LeftParm = new TParm(TJDODBTool.getInstance().select(sqlTable2Left));
        if (table2LeftParm.getErrCode() < 0) {
            System.out.println("�������ݲ�ѯ����  " + table2LeftParm.getErrText());
            return;
        }
        int cancelCount = table2LeftParm.getCount("INV_NO");
        table2 = new TParm();
        table2.addData("NOW_RECP", nowRecpCount >= 0 ? nowRecpCount : 0);
        table2.addData("CANCEL_RECP", cancelCount >= 0 ? cancelCount : 0);
        table2.addData("SYSTEM", "COLUMNS", "NOW_RECP");
        table2.addData("SYSTEM", "COLUMNS", "CANCEL_RECP");
        table2.setCount(1);
        String sqlTable3 = "";
        if ("IBS".equals(recpType)) {// modify by wanglong 20130120
            sqlTable3 = BILSQL.getCancelRecpI(sTime, eTime, recpType, null);// modify by wanglong 20130120
        } else if ("OPB".equals(recpType)) {
            sqlTable3 = BILSQL.getCancelRecpO(sTime, eTime, recpType, null);// modify by wanglong 20130120
        } else if ("HRM".equals(recpType)) {// modify by wanglong 20130120
            sqlTable3 = BILSQL.getCancelRecpH(sTime, eTime, "OPB", null);// modify by wanglong 20130120
        } else if ("REG".equals(recpType)) {// add by wanglong 20130120
            sqlTable3 = BILSQL.getCancelRecpR(sTime, eTime, recpType, null);
        }
        // System.out.println("����Ʊ�ݲ�ѯ" + sqlTable3);
        table3 = new TParm(TJDODBTool.getInstance().select(sqlTable3));
        if (table3.getErrCode() < 0) {
            System.out.println("�������ݲ�ѯ����  " + table3.getErrText());
            return;
        }
    }

    /**
     * ���
     */
    public void onExport() {
        TParm table1 = table.getParmValue();
        if (table1.getCount() <= 1) {
            this.messageBox("û����Ҫ���������");
            return;
        }
        String type = "";
        if (((TRadioButton) this.getComponent("SEL_O")).isSelected()) {// ����
            type = "����";
        } else if (((TRadioButton) this.getComponent("SEL_I")).isSelected()) {
            type = "סԺ";
        } else if (((TRadioButton) this.getComponent("SEL_H")).isSelected()) {// modify by wanglong 20130120
            type = "����";
        } else if (((TRadioButton) this.getComponent("SEL_R")).isSelected()) {// add by wanglong 20130120
            type = "�Һ�";
        }
        table1.setData("TITLE", type + "��Ʊ��ϸ");
        table1.setData("HEAD", table.getHeader());
        String[] header = table.getParmMap().split(";");
        for (int i = 0; i < header.length; i++) {
            table1.addData("SYSTEM", "COLUMNS", header[i]);
        }
        // System.out.println("table2" + table2);
        table2.setData("TITLE", type + "��Ʊ����");// modify by wanglong 20130120
        table2.setData("HEAD", "ʹ�÷�Ʊ��,140;���Ϸ�Ʊ��,200");// modify by wanglong 20130120
        table3.setData("TITLE",  type + "��Ʊ�嵥");// modify by wanglong 20130120
        table3.setData("HEAD", "��Ʊ��,120;�������,130;���,200;״̬,100");
        // System.out.println("table2" + table2);
        TParm[] execleTable = new TParm[]{table1, table2, table3 };
        ExportExcelUtil.getInstance().exeSaveExcel(execleTable, type + "˰��Ʊ��ϸ");// modify by wanglong 20130120
    }

    /**
     * ���
     */
    public void onClear() {
    	initPage();
        table.setDSValue();//modify by wanglong 20130120
    }

    /**
     * ���ñ�������
     */
    public void onSetTableAttribute() {// add by wanglong 20130109
        if (((TRadioButton) this.getComponent("SEL_O")).isSelected()) {// ����
            String header="��Ʊ��,100;����,150,TimeStamp,yyyy/MM/dd HH:mm:ss;�������,120;״̬,60;����,100;�ϼ�,90,double,#########0.00;��ҩ��,90,double,#########0.00";
            String chargeStr="CHARGE03;CHARGE04;CHARGE05;CHARGE10;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE18;CHARGE13;CHARGE14;CHARGE15;CHARGE17;CHARGE16;CHARGE11;CHARGE12;CHARGE19";
            String[] chargeArr=chargeStr.split(";");
            TParm chargeDescParm= BILPrintTool.getInstance().getChargeDesc("O");//modify by wanglong 20130730
            for (int i = 0; i < chargeArr.length; i++) {
                header+=";"+chargeDescParm.getValue(chargeArr[i])+",90,double,#########0.00";
            }
            table.setHeader(header);
            table.setColumnHorizontalAlignmentData("3,left;4,left;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right");
            table.setParmMap("INV_NO;PRINT_DATE;RECEIPT_NO;CANCEL_FLG;PAT_NAME;TOT_AMT;CHARGE0102;CHARGE03;CHARGE04;CHARGE05;CHARGE10;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE18;CHARGE13;CHARGE14;CHARGE15;CHARGE17;CHARGE16;CHARGE11;CHARGE12;CHARGE19");
        } else if (((TRadioButton) this.getComponent("SEL_H")).isSelected()) {// ����
            String header="��Ʊ��,100;����,150,TimeStamp,yyyy/MM/dd HH:mm:ss;�������,120;״̬,60;��������,150;��������,100;�ϼ�,90,double,#########0.00;��ҩ��,90,double,#########0.00";
            String chargeStr="CHARGE03;CHARGE04;CHARGE05;CHARGE10;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE18;CHARGE13;CHARGE14;CHARGE15;CHARGE17;CHARGE16;CHARGE11;CHARGE12;CHARGE19";
            String[] chargeArr=chargeStr.split(";");
            TParm chargeDescParm= BILPrintTool.getInstance().getChargeDesc("O");//modify by wanglong 20130730
            for (int i = 0; i < chargeArr.length; i++) {
                header+=";"+chargeDescParm.getValue(chargeArr[i])+",90,double,#########0.00";
            }
            table.setHeader(header);
            table.setColumnHorizontalAlignmentData("3,left;4,left;5,left;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right");
            table.setParmMap("INV_NO;PRINT_DATE;RECEIPT_NO;CANCEL_FLG;COMPANY_DESC;PAT_NAME;TOT_AMT;CHARGE0102;CHARGE03;CHARGE04;CHARGE05;CHARGE10;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE18;CHARGE13;CHARGE14;CHARGE15;CHARGE17;CHARGE16;CHARGE11;CHARGE12;CHARGE19");
        } else if (((TRadioButton) this.getComponent("SEL_I")).isSelected()) {// סԺ  // modify by wanglong 20130120
            String header="��Ʊ��,100;����,150,TimeStamp,yyyy/MM/dd HH:mm:ss;�������,120;״̬,60;����,100;�ϼ�,90,double,#########0.00";
            String chargeStr="CHARGE01;CHARGE02;CHARGE0304;CHARGE05;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE10;CHARGE11;CHARGE12;CHARGE13;CHARGE14;CHARGE15;CHARGE16;CHARGE17;CHARGE18;CHARGE20;CHARGE19";
            String[] chargeArr=chargeStr.split(";");
            TParm chargeDescParm= BILPrintTool.getInstance().getChargeDesc("I");//modify by wanglong 20130730
            for (int i = 0; i < chargeArr.length; i++) {
                if(chargeArr[i].equals("CHARGE0304")){
                    header+=";��ҩ��,90,double,#########0.00";
                }else{
                    header+=";"+chargeDescParm.getValue(chargeArr[i])+",90,double,#########0.00"; 
                }
            }
            table.setHeader(header);
            table.setColumnHorizontalAlignmentData("3,left;4,left;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right");
            table.setParmMap("INV_NO;PRINT_DATE;RECEIPT_NO;CANCEL_FLG;PAT_NAME;TOT_AMT;CHARGE01;CHARGE02;CHARGE0304;CHARGE05;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE10;CHARGE11;CHARGE12;CHARGE13;CHARGE14;CHARGE15;CHARGE16;CHARGE17;CHARGE18;CHARGE20;CHARGE19");
        } else if (((TRadioButton) this.getComponent("SEL_R")).isSelected()) {// �Һ� // add by wanglong 20130120
            table.setHeader("��Ʊ��,100;����,150,TimeStamp,yyyy/MM/dd HH:mm:ss;�������,120;״̬,60;����,100;�ϼ�,90,double,#########0.00;�Һŷ�,90,double,#########0.00;����,90,double,#########0.00;���ӷ�,90,double,#########0.00;��������1,90,double,#########0.00;��������2,90,double,#########0.00;��������3,90,double,#########0.00");
            table.setColumnHorizontalAlignmentData("3,left;4,left;5,right;6,right;7,right;8,right;9,right;10,right;11,right");
            table.setParmMap("INV_NO;PRINT_DATE;RECEIPT_NO;CANCEL_FLG;PAT_NAME;AR_AMT;REG_FEE_REAL;CLINIC_FEE_REAL;SPC_FEE;OTHER_FEE1;OTHER_FEE2;OTHER_FEE3");
        }
    }
    
    // =================================�����ܿ�ʼ==================================
    /**
     * �����������������
     * 
     * @param table
     */
    public void addListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                // �������򷽷�;
                // ת�����û���������к͵ײ����ݵ��У�Ȼ���ж�
                if (j == sortColumn) {
                    ascending = !ascending;
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                // table.getModel().sort(ascending, sortColumn);
                // �����parmֵһ��,
                // 1.ȡparamwֵ;
                TParm tableData = table.getParmValue();
                TParm totAmtRow = tableData.getRow(tableData.getCount() - 1);// add by wanglong 20130108
                tableData.removeRow(tableData.getCount() - 1);// add by wanglong 20130108
                // System.out.println("tableData:"+tableData);
                tableData.removeGroupData("SYSTEM");
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
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // ��������vectorת��parm;
                TParm lastResultParm = new TParm();// ��¼���ս��
                lastResultParm = cloneVectoryParam(vct, new TParm(), strNames);// �����м�����
                for (int k = 0; k < columnName.length; k++) {// add by wanglong 20130108
                    lastResultParm.addData(columnName[k], totAmtRow.getData(columnName[k]));
                }
                lastResultParm.setCount(lastResultParm.getCount(columnName[0]));// add by wanglong 20130108
                table.setParmValue(lastResultParm);
            }
        });
    }

    /**
     * ����ת������ֵ
     * 
     * @param columnName
     * @param tblColumnName
     * @return
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
     * �õ� Vector ֵ
     * 
     * @param group
     *            String ����
     * @param names
     *            String "ID;NAME"
     * @param size
     *            int �������
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size) count = size;
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
     * vectoryת��param
     */
    private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames) {
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
        return parmTable;
    }
    // ================================�����ܽ���==================================
    
}
 