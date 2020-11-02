package com.javahis.ui.bil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.bil.BillItemTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title:ҽ����Ŀ��ϸ�գ��£��ᱨ��
 *
 * <p>Description: ҽ����Ŀ��ϸ�գ��£��ᱨ��
 *
 * <p>Copyright: Copyright (c) 2013
 *
 * <p>Company: BlueCore</p>
 *
 * @author  chenx 2013.01.30
 * @version 1.0
 */
public class BILItemFeeDetailControl
        extends TControl {// refactor by wanglong 20130613

    private TTable tableM,tableD;
    private BILComparator compare = new BILComparator();
    private boolean ascending = false;
    private int sortColumn = -1;
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        tableM = (TTable) this.getComponent("TABLE_M");
        tableD = (TTable) this.getComponent("TABLE_D");
        addSortListener(tableM);// add by wanglong 20130926
        addSortListener(tableD);// add by wanglong 20130613
        this.initUI();
        callFunction("UI|MR_NO|setEnabled", false);
    }

    /**
     * ��ʼ������
     */
    public void initUI() {
        // this.setValue("DEPT_CODE", Operator.getDept()) ;
        String now = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMdd");
        this.setValue("START_DATE", StringTool.getTimestamp(now, "yyyyMMdd"));// ��ʼʱ��
        this.setValue("END_DATE", StringTool.getTimestamp(now, "yyyyMMdd"));// ����ʱ��
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        if (!check()) {
            return;
        }
        TParm inParm = new TParm();
        inParm.setData("START_DATE", this.getValue("START_DATE"));
        inParm.setData("END_DATE", this.getValue("END_DATE"));
        inParm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
        inParm.setData("MR_NO", this.getValueString("MR_NO"));
        if (!this.getValueString("HOSP_CODE").equals("")) {
            inParm.setData("TRANS_HOSP_CODE", this.getValueString("HOSP_CODE"));
        }
        TParm result = new TParm();
        String admType=this.getValueString("ADM_TYPE");
        if (getPanel("PANEL_D").isShowing()) {// ===========================��ϸҳǩ
            if (admType.equals("O")) {// ����OPD_ORDER
                if (isSelected("SELECT_DEPT")) { // ����
                    String sql = BillItemTool.getInstance().onQueryOPDByDept(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("��ѯʧ�� "+result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// ����
                    String caseNo = onRecordChoose(admType, this.getValueString("MR_NO"));
                    if (caseNo.equals("")) {
                        return;
                    }
                    inParm.setData("CASE_NO", caseNo);
                    String sql = BillItemTool.getInstance().onQueryOPDByMr(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("��ѯʧ�� "+result.getErrText());
                        this.onClear();
                        return;
                    }
                }
            } else if (admType.equals("H")) { // �������HRM_ORDER
                if (isSelected("SELECT_DEPT")) { // ����
                    String sql = BillItemTool.getInstance().onQueryHRMByDept(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("��ѯʧ�� "+result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// ����
                    String caseNo = onRecordChoose(admType, this.getValueString("MR_NO"));
                    if (caseNo.equals("")) {
                        return;
                    }
                    inParm.setData("CASE_NO", caseNo);
                    String sql = BillItemTool.getInstance().onQueryHRMByMr(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("��ѯʧ�� "+result.getErrText());
                        this.onClear();
                        return;
                    }
                }
            } else if(admType.equals("I")) {//סԺ IBS_ORDD
                if (isSelected("SELECT_DEPT")) { // ����
                    String sql = BillItemTool.getInstance().onQueryODIByDept(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("��ѯʧ�� "+result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// ����
                    String caseNo = onRecordChoose(admType, this.getValueString("MR_NO"));
                    if (caseNo.equals("")) {
                        return;
                    }
                    inParm.setData("CASE_NO", caseNo);
                    String sql = BillItemTool.getInstance().onQueryODIByMr(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("��ѯʧ�� "+result.getErrText());
                        this.onClear();
                        return;
                    }
                }
            } else if (admType.equals("S")) {// ����
                if (isSelected("SELECT_DEPT")) { // ����
                    String sqlO =
                            BillItemTool.getInstance().onQueryOPDByDept(inParm).split("ORDER BY")[0];
                    String sqlH =
                            BillItemTool.getInstance().onQueryHRMByDept(inParm).split("ORDER BY")[0];
                    String sqlI =
                            BillItemTool.getInstance().onQueryODIByDept(inParm).split("ORDER BY")[0];
                    String sql =
                            "SELECT DEPT_CODE,REXP_CODE,CHN_DESC,ORDER_CODE,ORDER_DESC,TRUNC(AR_AMT/OWN_AMT,2) DISCOUNT_RATE,UNIT_CODE,"
                                    + "SUM(DISPENSE_QTY) DISPENSE_QTY, OWN_PRICE, SUM(OWN_AMT) OWN_AMT, SUM(AR_AMT) AR_AMT, NS_NOTE "
                                    + "FROM(SELECT * FROM(#)       UNION ALL      SELECT * FROM(#)       UNION ALL      SELECT * FROM(#)   ) "
                                    + "GROUP BY DEPT_CODE,REXP_CODE,CHN_DESC,ORDER_CODE,ORDER_DESC,UNIT_CODE,OWN_PRICE,AR_AMT/OWN_AMT,NS_NOTE "
                                    + "ORDER BY DEPT_CODE,CHN_DESC,ORDER_CODE,DISCOUNT_RATE,DISPENSE_QTY";
                    sql = sql.replaceFirst("#", sqlO);
                    sql = sql.replaceFirst("#", sqlH);
                    sql = sql.replaceFirst("#", sqlI);
//                    System.out.println("-------��ϸ��ѯ����--------" + sql);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if (result.getErrCode() < 0) {
                        this.messageBox("��ѯʧ�� " + result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// ����
                    this.messageBox("���˲���ʹ�û��ܲ�ѯ");
                    return;
                }
            }
            // System.out.println("=================result==============" + result);
            tableD.setParmValue(result);
        } else {// =================================================����ҳǩ
            if (admType.equals("O")) {// ����OPD_ORDER
                if (isSelected("SELECT_DEPT")) { // ����
                    String sql = BillItemTool.getInstance().onQueryTotOPDByDept(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("��ѯʧ�� "+result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// ����
                    String caseNo = onRecordChoose(admType, this.getValueString("MR_NO"));
                    if (caseNo.equals("")) {
                        return;
                    }
                    inParm.setData("CASE_NO", caseNo);
                    String sql = BillItemTool.getInstance().onQueryTotOPDByMr(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("��ѯʧ�� "+result.getErrText());
                        this.onClear();
                        return;
                    }
                }
            } else if (admType.equals("H")) { // �������HRM_ORDER
                if (isSelected("SELECT_DEPT")) { // ����
                    String sql = BillItemTool.getInstance().onQueryTotHRMByDept(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("��ѯʧ�� "+result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// ����
                    String caseNo = onRecordChoose(admType, this.getValueString("MR_NO"));
                    if (caseNo.equals("")) {
                        return;
                    }
                    inParm.setData("CASE_NO", caseNo);
                    String sql = BillItemTool.getInstance().onQueryTotHRMByMr(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("��ѯʧ�� "+result.getErrText());
                        this.onClear();
                        return;
                    }
                }
            } else if(admType.equals("I")) {//סԺ IBS_ORDD
                if (isSelected("SELECT_DEPT")) { // ����
                    String sql = BillItemTool.getInstance().onQueryTotODIByDept(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("��ѯʧ�� "+result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// ����
                    String caseNo = onRecordChoose(admType, this.getValueString("MR_NO"));
                    if (caseNo.equals("")) {
                        return;
                    }
                    inParm.setData("CASE_NO", caseNo);
                    String sql = BillItemTool.getInstance().onQueryTotODIByMr(inParm);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if(result.getErrCode()<0){
                        this.messageBox("��ѯʧ�� "+result.getErrText());
                        this.onClear();
                        return;
                    }
                }
            } else if (admType.equals("S")) {// ����
                if (isSelected("SELECT_DEPT")) { // ����
                    String sqlO =
                            BillItemTool.getInstance().onQueryTotOPDByDept(inParm).split("ORDER BY")[0];
                    String sqlH =
                            BillItemTool.getInstance().onQueryTotHRMByDept(inParm).split("ORDER BY")[0];
                    String sqlI =
                            BillItemTool.getInstance().onQueryTotODIByDept(inParm).split("ORDER BY")[0];
                    String sql =
                            "SELECT DEPT_CODE,REXP_CODE,CHN_DESC,ORDER_CODE,ORDER_DESC,UNIT_CODE,SUM(DISPENSE_QTY) DISPENSE_QTY "
                                    + " FROM (#  UNION ALL  #  UNION ALL  # ) "
                                    + "GROUP BY DEPT_CODE,REXP_CODE,CHN_DESC,ORDER_CODE,ORDER_DESC,UNIT_CODE "
                                    + "ORDER BY DEPT_CODE,CHN_DESC,ORDER_CODE,DISPENSE_QTY";
                    sql = sql.replaceFirst("#", sqlO);
                    sql = sql.replaceFirst("#", sqlH);
                    sql = sql.replaceFirst("#", sqlI);
//                    System.out.println("------���ܲ�ѯ����---------" + sql);
                    result = new TParm(TJDODBTool.getInstance().select(sql));
                    if (result.getErrCode() < 0) {
                        this.messageBox("��ѯʧ�� " + result.getErrText());
                        this.onClear();
                        return;
                    }
                } else {// ����
                    this.messageBox("���˲���ʹ�û��ܲ�ѯ");
                    return;
                }
            }
            // System.out.println("=================result==============" + result);
            tableM.setParmValue(result);
        }
    }

    /**
     * У��
     * 
     * @return
     */
    public boolean check() {
        if (StringUtil.isNullString(this.getValueString("START_DATE"))) {
            this.messageBox("��ʼʱ�䲻��Ϊ��");
            return false;
        }
        if (StringUtil.isNullString(this.getValueString("END_DATE"))) {
            this.messageBox("����ʱ�䲻��Ϊ��");
            return false;
        }
        if (StringUtil.isNullString(this.getValueString("ADM_TYPE"))) {
            this.messageBox("���������Ϊ��");
            return false;
        }
        if (isSelected("SELECT_DEPT")) {
            if (StringUtil.isNullString(this.getValueString("DEPT_CODE"))) {
                this.messageBox("���Ҳ���Ϊ��");
                return false;
            }
        }
        if (isSelected("SELECT_MR")) {
            if (StringUtil.isNullString(this.getValueString("MR_NO"))) {
                this.messageBox("�����Ų���Ϊ��");
                return false;
            }
        }
        return true;
    }

    /**
     * �����Żس�
     */
    public void onMrNo() {
        String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
        this.setValue("MR_NO", mrNo);
        this.setValue("PAT_NAME", PatTool.getInstance().getNameForMrno(mrNo));
        this.onQuery();
    }
    
    /**
     * �����¼ѡ��
     * @param admType
     * @param mrNo
     * @return
     */
    public String onRecordChoose(String admType, String mrNo) {
        if (StringUtil.isNullString(mrNo)) {
            this.messageBox("E0024");// ��ʼ������ʧ��
            return "";
        }
        if (!PatTool.getInstance().existsPat(mrNo)) {
            this.messageBox("E0081");// ���޴˲���
            return "";
        }
        TParm parm = new TParm();
        parm.setData("MR_NO", mrNo);
        parm.setData("ADM_TYPE", admType);
        String lastCaseNo = (String) openDialog("%ROOT%\\config\\bil\\BilPatRecordChoose.x", parm);
        if (StringUtil.isNullString(lastCaseNo)) {
            return "";
        }
        if (lastCaseNo.equals("-1")) {
            this.messageBox("�����ھ�����Ϣ");
            return "";
        }
        return lastCaseNo;
    }

    /**
     * ���á������б�
     */
    public void showPatList() {
        if (isSelected("SELECT_DEPT")) {// ֻ�ڰ����Ҳ�ѯʱ����Ч
            TParm inParm = new TParm();
            TParm parmValue = new TParm();
            int row = 0;
            if (getPanel("PANEL_D").isShowing()) {
                parmValue = (TParm) callFunction("UI|TABLE_D|getParmValue");
                row = (Integer) callFunction("UI|TABLE_D|getSelectedRow");// ��ȡѡ����
            } else {
                parmValue = (TParm) callFunction("UI|TABLE_M|getParmValue");
                row = (Integer) callFunction("UI|TABLE_M|getSelectedRow");// ��ȡѡ����
            }
            if (row < 0) {
                return;
            }
            inParm.setData("START_DATE", this.getValue("START_DATE"));
            inParm.setData("END_DATE", this.getValue("END_DATE"));
            inParm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
            inParm.setData("DEPT_CODE", parmValue.getValue("DEPT_CODE", row));
            inParm.setData("REXP_CODE", parmValue.getValue("REXP_CODE", row));//add by wanglong 20130717
            inParm.setData("ORDER_CODE", parmValue.getValue("ORDER_CODE", row));
            inParm.setData("OWN_PRICE", parmValue.getDouble("OWN_PRICE", row));//add by wanglong 20130805
            inParm.setData("OWN_AMT", parmValue.getDouble("OWN_AMT", row)/parmValue.getDouble("DISPENSE_QTY", row));
            inParm.setData("AR_AMT", parmValue.getDouble("AR_AMT", row)/parmValue.getDouble("DISPENSE_QTY", row));
//             System.out.println("====================inParm================" + inParm);
            this.openDialog("%ROOT%\\config\\bil\\BILItemPatList.x", inParm);
        }
    }
    
    /**
     * ���Excel
     */
    public void onExport() {
        if (getPanel("PANEL_D").isShowing()) {// ��ϸҳǩ
            TParm parm = tableD.getParmValue();
            if (null == parm || parm.getCount("DEPT_CODE") <= 0) {
                this.messageBox("û����Ҫ����������");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(tableD, "ҽ����Ŀͳ����(��)����ϸ��");
        }else{
            TParm parm = tableM.getParmValue();
            if (null == parm || parm.getCount("DEPT_CODE") <= 0) {
                this.messageBox("û����Ҫ����������");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(tableM, "ҽ����Ŀͳ����(��)����ܱ�");
        }

    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        TParm titleValue = new TParm();
        TParm parmValue = new TParm();
        TParm printParm = new TParm();
        if (getPanel("PANEL_D").isShowing()) {// ��ϸҳǩ
            parmValue = tableD.getShowParmValue();
            if (parmValue == null || parmValue.getCount("CHN_DESC") <= 0) {
                this.messageBox("��������");
                return;
            }
            parmValue.setCount(parmValue.getCount("CHN_DESC"));
            parmValue.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
            parmValue.addData("SYSTEM", "COLUMNS", "CHN_DESC");
            parmValue.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parmValue.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
            parmValue.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            parmValue.addData("SYSTEM", "COLUMNS", "UNIT_CODE");
            parmValue.addData("SYSTEM", "COLUMNS", "OWN_AMT");
            parmValue.addData("SYSTEM", "COLUMNS", "AR_AMT");
            titleValue.addData("DEPT_CODE", "����");
            titleValue.addData("CHN_DESC", "��Ŀ");
            titleValue.addData("ORDER_DESC", "��Ŀ��ϸ");
            titleValue.addData("OWN_PRICE", "����");
            titleValue.addData("DISPENSE_QTY", "����");
            titleValue.addData("UNIT_CODE", "��λ");
            titleValue.addData("OWN_AMT", "Ӧ�ս��");
            titleValue.addData("AR_AMT", "ʵ�ս��");
            titleValue.setCount(1);
            titleValue.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
            titleValue.addData("SYSTEM", "COLUMNS", "CHN_DESC");
            titleValue.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            titleValue.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
            titleValue.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            titleValue.addData("SYSTEM", "COLUMNS", "UNIT_CODE");
            titleValue.addData("SYSTEM", "COLUMNS", "OWN_AMT");
            titleValue.addData("SYSTEM", "COLUMNS", "AR_AMT");
            printParm.setData("TITLE", "TEXT", "ҽ��ͳ����ϸ��");
            printParm.setData("TITLE_D", titleValue.getData());
            printParm.setData("TABLE_D", parmValue.getData());
        } else {
            parmValue = tableM.getShowParmValue();
            if (parmValue == null || parmValue.getCount("CHN_DESC") <= 0) {
                this.messageBox("��������");
                return;
            }
            parmValue.setCount(parmValue.getCount("CHN_DESC"));
            parmValue.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
            parmValue.addData("SYSTEM", "COLUMNS", "CHN_DESC");
            parmValue.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
            parmValue.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parmValue.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            parmValue.addData("SYSTEM", "COLUMNS", "UNIT_CODE");
            titleValue.addData("DEPT_CODE", "����");
            titleValue.addData("CHN_DESC", "��Ŀ");
            titleValue.addData("ORDER_CODE", "��Ŀ����");
            titleValue.addData("ORDER_DESC", "��Ŀ��ϸ");
            titleValue.addData("DISPENSE_QTY", "����");
            titleValue.addData("UNIT_CODE", "��λ");
            titleValue.setCount(1);
            titleValue.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
            titleValue.addData("SYSTEM", "COLUMNS", "CHN_DESC");
            titleValue.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
            titleValue.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            titleValue.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            titleValue.addData("SYSTEM", "COLUMNS", "UNIT_CODE");
            printParm.setData("TITLE", "TEXT", "ҽ��ͳ�ƻ��ܱ�");
            printParm.setData("TITLE_M", titleValue.getData());
            printParm.setData("TABLE_M", parmValue.getData());
        }
        // tableParm.addData("SYSTEM", "COLUMNS", "NS_NOTE");
        String time = this.getText("START_DATE") + " �� " + this.getText("END_DATE");
        printParm.setData("REPORT_DATE", "TEXT", "ͳ������:" + time);
        String date = SystemTool.getInstance().getDate().toString().substring(0, 19);
        printParm.setData("PRINT_DATE", "TEXT", "�Ʊ�ʱ��: " + date);
        printParm.setData("PRINT_USER", "TEXT", "�Ʊ���: " + Operator.getName());
        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILItemFeeDetail.jhw", printParm);
    }
    
    /**
     * ���
     */
    public void onClear() {
        this.clearValue("ADM_TYPE;HOSP_CODE;DEPT_CODE;MR_NO;PAT_NAME");
        tableD.setParmValue(new TParm());
        tableM.setParmValue(new TParm());
        this.initUI();
    }
    
    /**
     * TRadioButton�ж��Ƿ�ѡ��
     * 
     * @param tagName
     * @return
     */
    private boolean isSelected(String tagName) {
        return ((TRadioButton) this.getComponent(tagName)).isSelected();
    }

    /**
     * radio button ѡ�����
     */
    public void onSelect2() {
        this.clearText("DEPT_CODE");
        callFunction("UI|DEPT_CODE|setEnabled", false);
        callFunction("UI|MR_NO|setEnabled", true);
    }

    /**
     * radio button ѡ�����
     */
    public void onSelect1() {
        this.clearValue("MR_NO;PAT_NAME");
        callFunction("UI|MR_NO|setEnabled", false);
        callFunction("UI|DEPT_CODE|setEnabled", true);
    }
    
    /**
     * ���������
     * 
     * @param tagName
     * @return
     */
    public TPanel getPanel(String tagName) {// add by wanglong 20130926
        return (TPanel) this.getComponent(tagName);
    }


    // ====================������begin======================add by wanglong 20130613
    /**
     * �����������������
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// �����ͬ�У���ת����
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();// ȡ�ñ��е�����
                String columnName[] = tableData.getNames("Data");// �������
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn); // ������������;
                int col = tranParmColIndex(columnName, tblColumnName); // ����ת��parm�е�������
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // ��������vectorת��parm;
                cloneVectoryParam(vct, new TParm(), strNames, table);
            }
        });
    }

    /**
     * �����������ݣ���TParmתΪVector
     * @param parm
     * @param group
     * @param names
     * @param size
     * @return
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
     * ����ָ���������������е�index
     * @param columnName
     * @param tblColumnName
     * @return int
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * �����������ݣ���Vectorת��Parm
     * @param vectorTable
     * @param parmTable
     * @param columnNames
     * @param table
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
            String columnNames, final TTable table) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        table.setParmValue(parmTable);
    }
    // ====================������end======================
}

