package com.javahis.ui.hrm;

import java.sql.Timestamp;
import jdo.hrm.HRMContractD;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: ��챨��ͳ�� </p>
 * 
 * <p> Description: ��챨��ͳ�� </p>
 * 
 * <p> Copyright: Copyright (c) 2008 </p>
 * 
 * <p> Company: javahis </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class HRMTJBDStatisticsControl extends TControl {// refactor by wanglong 20130120

    private TTable table;
    private TTextFormat contract;// ��ͬ������
    private HRMContractD contractD;// ��ͬDataStore

    /**
     * ��ʼ������
     */
    public void onInit() {
        contractD = new HRMContractD();
        contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
        this.clearValue("CONTRACT_CODE;COMPANY_CODE");
        table = (TTable) this.getComponent("TABLE");
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("START_DATE", date);
        this.setValue("END_DATE", date);
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        String startDate = this.getText("START_DATE").replaceAll("/", "");
        String endDate = this.getText("END_DATE").replaceAll("/", "");
        String companyCode = this.getValueString("COMPANY_CODE");
        String contractCode = this.getValueString("CONTRACT_CODE");
        TParm result = new TParm();
        if (getRadioButton("MASTER").isSelected()) {// ����
            String sql = // modify by wanglong 20130207
                    "SELECT COMPANY_CODE,CONTRACT_CODE,SUM(UNREPORT_NUM) UNREPORT_NUM,SUM(REPORT_NUM) REPORT_NUM,SUM(TOTAL_NUM) TOTAL_NUM "
                            + " FROM (SELECT A.COMPANY_CODE,A.CONTRACT_CODE,SUM(CASE WHEN COVER_FLG <> 'Y' THEN 1 ELSE 0 END) UNREPORT_NUM,"
                            + "              0 REPORT_NUM, COUNT(*) TOTAL_NUM "
                            + "         FROM HRM_CONTRACTD A "
                            + "        WHERE 0 = 0 "
                            + "  #  "
                            + "     GROUP BY A.COMPANY_CODE, A.CONTRACT_CODE "
                            + "        UNION "
                            + "       SELECT A.COMPANY_CODE,A.CONTRACT_CODE,0 UNREPORT_NUM,COUNT(*) REPORT_NUM,0 TOTAL_NUM "
                            + "         FROM HRM_CONTRACTD A "
                            + "        WHERE COVER_FLG = 'Y' "
                            + "  #  "
                            + "     GROUP BY A.COMPANY_CODE, A.CONTRACT_CODE) "
                            + "GROUP BY COMPANY_CODE, CONTRACT_CODE "
                            + "ORDER BY REPORT_NUM DESC, COMPANY_CODE, CONTRACT_CODE";
            String where1 = "";
            if (!companyCode.equals("")) {
                where1 += " AND A.COMPANY_CODE='" + companyCode + "' ";
            }
            if (!contractCode.equals("")) {
                where1 += " AND A.CONTRACT_CODE='" + contractCode + "' ";
            }
            String where2 = where1 + "";
            where2 +=
                    " AND A.REAL_CHK_DATE BETWEEN TO_DATE( '" + startDate + "', 'yyyy/mm/dd') "
                            + "AND TO_DATE( '" + endDate + " 23:59:59', 'yyyy/mm/dd hh24:mi:ss') ";
            sql = sql.replaceFirst("#", where1);
            sql = sql.replaceFirst("#", where2);
//            System.out.println("------------sql111--------" + sql);
            result = new TParm(TJDODBTool.getInstance().select(sql));
        } else {// ��ϸ
            String sql =
                    "SELECT * FROM (SELECT DISTINCT A.SEQ_NO,A.STAFF_NO,A.COMPANY_CODE,A.CONTRACT_CODE,A.PACKAGE_CODE,"
                            + "            A.MR_NO,A.PAT_NAME,A.SEX_CODE,A.IDNO,A.REAL_CHK_DATE REPORT_DATE,A.COVER_FLG,'N' FINISH_FLG,'N' BILL_FLG "
                            + "       FROM HRM_CONTRACTD A "
                            + "      WHERE A.COVER_FLG = 'N' "
                            + " # "
                            + "      UNION "
                            + "     SELECT DISTINCT A.SEQ_NO,A.STAFF_NO,A.COMPANY_CODE,A.CONTRACT_CODE,A.PACKAGE_CODE,"
                            + "            A.MR_NO,A.PAT_NAME,A.SEX_CODE,A.IDNO,A.REAL_CHK_DATE REPORT_DATE,A.COVER_FLG,"
                            + "            CASE WHEN NVL(C.EXEC_DR_CODE,'x')='x' THEN 'N' ELSE 'Y' END AS FINISH_FLG,C.BILL_FLG "
                            + "       FROM HRM_CONTRACTD A, HRM_ORDER C "
                            + "      WHERE A.CONTRACT_CODE = C.CONTRACT_CODE "
                            + "        AND A.MR_NO = C.MR_NO "
                            + " # "
                            + "        AND A.COVER_FLG = 'Y' "
                            + "        AND C.SETMAIN_FLG = 'Y' "
                            + "        AND C.DEPT_ATTRIBUTE = '04' "
                            + " ) "
                            + "ORDER BY COVER_FLG DESC NULLS LAST,COMPANY_CODE,CONTRACT_CODE,PACKAGE_CODE,SEQ_NO";
            String where1 = "";
            if (!companyCode.equals("")) {
                where1 += " AND A.COMPANY_CODE='" + companyCode + "' ";
            }
            if (!contractCode.equals("")) {
                where1 += " AND A.CONTRACT_CODE='" + contractCode + "' ";
            }
            String where2 = where1 + "";
            where2 +=
                    " AND A.REAL_CHK_DATE BETWEEN TO_DATE( '" + startDate + "', 'yyyy/mm/dd') "
                            + "AND TO_DATE( '" + endDate + " 23:59:59', 'yyyy/mm/dd hh24:mi:ss') ";
            sql = sql.replaceFirst("#", where1);
            sql = sql.replaceFirst("#", where2);
//            System.out.println("------------sql222--------"+sql);
            result = new TParm(TJDODBTool.getInstance().select(sql));
        }
        if (result.getErrCode() != 0) {
            this.messageBox("E0035");// ����ʧ��
            return;
        }
        if (result.getCount() <= 0) {
            table.setParmValue(new TParm());
            this.setValue("COUNT", "");
            this.messageBox("E0116");// û������
            return;
        }
        if (getRadioButton("MASTER").isSelected()) {// ����
            if (getCheckBox("NOT_SHOW").isSelected()) {
                int count = result.getCount();
                for (int i = count - 1; i > -1; i--) {
                    if (result.getInt("REPORT_NUM", i) == 0) {
                        result.removeRow(i);
                    }
                }
                result.setCount(result.getCount("REPORT_NUM"));
            }
            int count = result.getCount();
            int unReportAmount = 0;// δ��������
            int reportAmount = 0;// �ѱ�������
            int totAmount = 0;// ������
            for (int i = 0; i < result.getCount(); i++) {
                unReportAmount += result.getInt("UNREPORT_NUM", i);
                reportAmount += result.getInt("REPORT_NUM", i);
                totAmount += result.getInt("TOTAL_NUM", i);
            }
            String[] ParmMapString = result.getNames();
            for (int i = 0; i < ParmMapString.length; i++) {
                result.addData(ParmMapString[i], "");
            }
            result.setCount(count + 1);
            result.setData("CONTRACT_CODE", count, "�ϼ�");
            result.setData("UNREPORT_NUM", count, unReportAmount);
            result.setData("REPORT_NUM", count, reportAmount);
            result.setData("TOTAL_NUM", count, totAmount);
            this.setValue("COUNT", totAmount + "");
        } else {// ��ϸ
            if (getCheckBox("NOT_SHOW").isSelected()) {
                int count = result.getCount();
                for (int i = count - 1; i > -1; i--) {
                    if (!result.getValue("COVER_FLG", i).equalsIgnoreCase("Y")) {
                        result.removeRow(i);
                    }
                }
                result.setCount(result.getCount("COVER_FLG"));
            }
            this.setValue("COUNT", result.getCount() + "");
        }
        table.setParmValue(result);
    }

    // /**
    // * ��ӡ����
    // */
    // public void onPrint() {
    // int ATM = 0;
    // TTable table_d = getTTable("tbl1");
    // if (table_d.getRowCount() > 0) {
    // // ��ӡ����OPT_USER;COUNT(CASE_NO)
    // TParm date = new TParm();
    // // ��ͷ����
    // date.setData("Title", "TEXT",
    // Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion())
    // + "��챨����������");
    // date.setData("createDate", "TEXT",
    // "�Ʊ�����: "
    // + SystemTool.getInstance().getDate().toString().substring(0, 10)
    // .replace('-', '/'));
    // date.setData("start_Date", "TEXT",
    // "��ʼʱ��: " + start_Date.substring(0, 10).replace('-', '/'));
    // date.setData("end_Date", "TEXT", "����ʱ��: " + end_Date.substring(0, 10).replace('-', '/'));
    // // �������
    // TParm parm = new TParm();
    // TParm tableParm = table_d.getParmValue();
    // int count = tableParm.getCount();
    // for (int i = 0; i < count; i++) {
    // parm.addData("T2", tableParm.getValue("T2", i));
    // parm.addData("T3", tableParm.getValue("T3", i));
    // parm.addData("T1", tableParm.getValue("T1", i));
    // ATM += StringTool.getInt(tableParm.getValue("T1", i));
    // }
    // parm.setCount(parm.getCount("T1"));
    // parm.addData("SYSTEM", "COLUMNS", "T2");
    // parm.addData("SYSTEM", "COLUMNS", "T3");
    // parm.addData("SYSTEM", "COLUMNS", "T1");
    // date.setData("tbl1", parm.getData());
    // // ��β����
    // date.setData("createUser", "TEXT", "�Ʊ���: " + Operator.getName());
    // date.setData("pass", "TEXT", "�����: ");
    // date.setData("ATM", "TEXT", "�ϼ��˴Σ�" + ATM);
    // // ���ô�ӡ����
    // this.openPrintWindow("%ROOT%\\config\\prt\\HRM\\HRMTJBDStatistics.jhw", date);
    // } else {
    // this.messageBox("E0010");// ����û�д�ӡ����
    // return;
    // }
    // }
    
    /**
     * ���Excel
     */
    public void onExport() {
        if (getRadioButton("MASTER").isSelected()) {// ����
            ExportExcelUtil.getInstance().exportExcel(table, "�����˴�ͳ�Ʊ���");
        } else ExportExcelUtil.getInstance().exportExcel(table, "�����˴�ͳ�Ʊ�����ϸ");
    }

    /**
     * �������ѡ���¼�
     */
    public void onCompanyChoose() {
        String companyCode = this.getValueString("COMPANY_CODE");
        if (StringUtil.isNullString(companyCode)) {
            this.clearValue("CONTRACT_CODE");
            return;
        }
        TParm contractParm = contractD.onQueryByCompany(companyCode);// ��ѯ���Ӧ��ͬ�����������
        if (contractParm == null || contractParm.getCount() <= 0 || contractParm.getErrCode() != 0) {
            this.messageBox("û�к�ͬ����");
            return;
        }
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        contract.setHisOneNullRow(true);
        String contractCode = contractParm.getValue("ID", 0);
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox("E0034");// ȡ�����ݴ���
            return;
        }
        contract.setValue(contractCode);
    }

    /**
     * ���ñ�������
     */
    public void onSetTableAttribute() {// add by wanglong 20130120
        if (getRadioButton("MASTER").isSelected()) {// ����
            table.setHeader("��������,200,COMPANY_CODE;��ͬ����,150,CONTRACT_CODE;δ��������,120,int;�ѱ�������,120,int;������,70,int");
            table.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right;4,right");//modify by wanglong 20130207
            table.setParmMap("COMPANY_CODE;CONTRACT_CODE;UNREPORT_NUM;REPORT_NUM;TOTAL_NUM");
        } else {// ϸ��
            table.setHeader("��������,200,COMPANY_CODE;��ͬ����,150,CONTRACT_CODE;�ײ�����,150,PACKAGE_CODE;"
                    + "������,100;����,90;�Ա�,60,SEX_CODE;���֤��,135;����״̬,80,COVER_FLG;�ܼ�״̬,80,FINISH_FLG;����״̬,80,BILL_FLG");
            table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;4,left;6,left");
            table.setParmMap("COMPANY_CODE;CONTRACT_CODE;PACKAGE_CODE;MR_NO;PAT_NAME;SEX_CODE;IDNO;COVER_FLG;FINISH_FLG;BILL_FLG");
        }
    }

    /**
     * ��õ�ѡ�����
     * 
     * @param tagName
     * @return
     */
    public TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) this.getComponent(tagName);
    }
    
    /**
     * ��ø�ѡ�����
     * 
     * @param tagName
     * @return
     */
    public TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) this.getComponent(tagName);
    }

    /**
     * �õ�table���
     * 
     * @param tag
     * @return
     */
    public TTable getTTable(String tag) {
        return (TTable) this.getComponent(tag);
    }

    /**
     * ���
     */
    public void onClear() {
        this.clearValue("CONTRACT_CODE;COMPANY_CODE");
        this.setValue("COUNT", "");
        contract.getPopupMenuData().getData().clear();
        contract.filter();
        table.removeRowAll();
        onInit();
    }
}
