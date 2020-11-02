package com.javahis.ui.hrm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.hrm.HRMCheckFeeTool;
import jdo.hrm.HRMContractD;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: �����ñ��� </p>
 * 
 * <p> Description: �����ñ��� </p>
 * 
 * <p> Copyright: Copyright (c) 2012 </p>
 * 
 * <p> Company: javahis </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class HRMCheckFeeControl
        extends TControl {

    private HRMContractD contractD; // ��ͬϸ�����
    private TTextFormat company, contract; // ���塢��ͬTTextFormat
    private TTable masterTable;
    private TTable detailTable;
    private TParm parmValue = new TParm();// add by wanglong 20130118
    private TParm showParmValue = new TParm();// add by wanglong 20130118
    private boolean ascending = false;// ��������
    private int sortColumn = -1;// ��������
    private BILComparator compare = new BILComparator();// ��������

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        masterTable = getTable("TABLE_MASTER");
        detailTable = getTable("TABLE_DETAIL");
        contractD = new HRMContractD();
        company = (TTextFormat) this.getComponent("COMPANY_CODE");
        contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
        onClear();
        onTabbedPaneChoose();// add by wanglong 20130108
        addListener(masterTable);
        addListener(detailTable);
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        String companyCode = this.getValueString("COMPANY_CODE");
        String contractCode = this.getValueString("CONTRACT_CODE");
        String packageCode = this.getValueString("PACKAGE_CODE");
        String mrNo = this.getValueString("MR_NO");
        String reportDate = this.getText("REPORT_DATE");// ����ʱ��
        TParm inParm = new TParm();
        inParm.setData("COMPANY_CODE", companyCode);
        inParm.setData("CONTRACT_CODE", contractCode);
        inParm.setData("PACKAGE_CODE", packageCode);
        inParm.setData("MR_NO", mrNo);
        inParm.setData("REPORT_DATE", reportDate);
        TParm result = new TParm();
        if (getPanel("PANEL_MASTER").isShowing()) {// ����
            result = HRMCheckFeeTool.getInstance().onQueryMaster(inParm);
            int count = result.getCount();
            if (count > 0) { // ������
                result = addTotAmtRow(masterTable, result);// �����ܼ���
                masterTable.setParmValue(result);
            } else masterTable.setParmValue(new TParm());
        } else {// ��ϸ
            result = HRMCheckFeeTool.getInstance().onQueryDetail(inParm);
            int count = result.getCount();
            if (count > 0) { // ������
                result = addTotAmtRow(detailTable, result);// �����ܼ���
                detailTable.setParmValue(result);
                parmValue = result;// add by wanglong 20130118
                showParmValue = detailTable.getShowParmValue();// add by wanglong 20130118
            } else masterTable.setParmValue(new TParm());
        }
        String sql =
                "SELECT COVER_FLG,COUNT(*) COUNT FROM HRM_CONTRACTD" + " # "
                        + " GROUP BY COVER_FLG ORDER BY COVER_FLG";
        String where = "";
        if (!companyCode.equals("")) {
            where += " AND COMPANY_CODE = '" + companyCode + "' ";
        }
        if (!contractCode.equals("")) {
            where += " AND CONTRACT_CODE = '" + contractCode + "' ";
        }
        if (!packageCode.equals("")) {
            where += " AND PACKAGE_CODE = '" + packageCode + "' ";
        }
        if (!mrNo.equals("")) {
            where += " AND MR_NO = '" + mrNo + "' ";
        }
        if (!reportDate.equals("")) {
            where +=
                    " AND REAL_CHK_DATE BETWEEN TO_DATE( '" + reportDate + "', 'yyyy/mm/dd') "
                            + "AND TO_DATE( '" + reportDate
                            + " 23:59:59', 'yyyy/mm/dd hh24:mi:ss') ";
        }
        if (where.length() > 0) {
            where = where.replaceFirst("AND", "WHERE");
        }
        sql = sql.replaceFirst("#", where);
        TParm countParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (countParm.getErrCode() != 0) {
            this.messageBox("��ѯ������������");
            return;
        }
        if (!countParm.getValue("COVER_FLG", 0).equals("")) {
            this.setValue("UNREPORT_COUNT", countParm.getValue("COUNT", 0));
        }
        if (!countParm.getValue("COVER_FLG", 1).equals("")) {
            this.setValue("REPORT_COUNT", countParm.getValue("COUNT", 1));
        }
    }

    /**
     * �����ܼ���          add by wanglong 20130128
     * @param parm
     * @return
     */
    public TParm addTotAmtRow(TTable table, TParm parm) {// modify by wanglong 20130224
        int count = parm.getCount("AR_AMT");
        double totOwnAmt = 0;
        double totArAmt = 0;
        for (int i = 0; i < count; i++) {
            totOwnAmt += parm.getDouble("OWN_AMT", i);
            totArAmt += parm.getDouble("AR_AMT", i);
        }
        String[] ParmMapString = parm.getNames();
        for (int i = 0; i < ParmMapString.length; i++) {
            parm.addData(ParmMapString[i], "");
        }
        parm.setCount(count + 1);
        if (getPanel("PANEL_MASTER").isShowing()) {// ����
            parm.setData("DISCOUNT_RATE", count, "�ϼ�");
            parm.setData("OWN_AMT", count, totOwnAmt);
            parm.setData("AR_AMT", count, totArAmt);
        } else {// ��ϸ
            parm.setData("DISCOUNT_RATE", count, "�ϼ�");
            parm.setData("OWN_AMT", count, totOwnAmt);
            parm.setData("AR_AMT", count, totArAmt);
        }
        return parm;
    }
    
    /**
     * ��������ѡ�¼�������ѡ����������룬��ʼ��������ĺ�ͬ��ϢTTextFormat������ʼ���˵�����
     */
    public void onCompanyCodeChoose() {
        String companyCode = this.getValueString("COMPANY_CODE");
        if (StringUtil.isNullString(companyCode)) {
            return;
        }
        TParm contractParm = contractD.onQueryByCompany(companyCode);
        if (contractParm.getErrCode() != 0) {
            this.messageBox_("û������");
        }
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        String contractCode = contractParm.getValue("ID", 0);
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox_("δ��ѯ��������ĺ�ͬ��Ϣ");
            return;
        }
        contract.setValue(contractCode);
    }

    /**
     * �����Żس��¼�
     */
    public void onMrNo() {// modify by wanglong 20130108
        String mrNo = PatTool.getInstance().checkMrno(getValueString("MR_NO"));
        TParm parm = PatTool.getInstance().getInfoForMrno(mrNo);
        if (parm.getCount() <= 0) {
            this.messageBox_("�����Ų�����");
            onClear();
            return;
        }
        String parName = parm.getValue("PAT_NAME", 0);
        setValue("MR_NO", mrNo);
        setValue("PAT_NAME", parName);
        TParm result = HRMCheckFeeTool.getInstance().onQueryMaster(parm.getRow(0));
        if (result.getErrCode() != 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount() < 1) {
            return;
        } else if (result.getCount() == 1) {// ��������
            if (getPanel("PANEL_MASTER").isShowing()) {
                result = addTotAmtRow(masterTable, result);
                masterTable.setParmValue(result);
            } else {
                result = HRMCheckFeeTool.getInstance().onQueryDetail(parm.getRow(0));
                if (result.getErrCode() != 0) {
                    this.messageBox(result.getErrText());
                    return;
                }
                if (result.getCount() < 1) {
                    return;
                } else {
                    result = addTotAmtRow(detailTable, result);
                    detailTable.setParmValue(result);
                    parmValue = result;
                    showParmValue = detailTable.getShowParmValue();
                }
            }
        } else {// ��������(����ѡ�񴰿�)
            TParm param = new TParm();
            param.setData("TYPE", getPanel("PANEL_MASTER").isShowing() ? "MASTER" : "DETAIL");
            param.setData("parmValue", result);
            Object obj = this.openDialog("%ROOT%\\config\\hrm\\HRMPatFeeChoose.x", param);
            if (obj != null) {
                result = (TParm) obj;
                if (getPanel("PANEL_MASTER").isShowing()) {
                    result = addTotAmtRow(masterTable, result);
                    masterTable.setParmValue(result);
                } else {
                    result = addTotAmtRow(detailTable, result);
                    detailTable.setParmValue(result);
                    parmValue = result;
                    showParmValue = detailTable.getShowParmValue();
                }
            }
        }
    }

    /**
     * �����س��¼�            add by wanglong 20130128
     */
    public void onPatName() {
        String patName = getValueString("PAT_NAME").trim();
        if (StringUtil.isNullString(patName)) {
            return;
        }
        TParm result =
                new TParm(
                        TJDODBTool
                                .getInstance()
                                .select("SELECT DISTINCT MR_NO, OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, "
                                                + "BIRTH_DATE, POST_CODE, ADDRESS FROM SYS_PATINFO WHERE PAT_NAME = '"
                                                + patName + "' ORDER BY OPT_DATE DESC NULLS LAST"));
        if (result.getErrCode() != 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount() < 1) {
            this.messageBox("E0081");// ���޴˲���
            onClear();
            return;
        } else {
            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
            TParm patParm = new TParm();
            if (obj != null) {
                patParm = (TParm) obj;
                setValue("MR_NO", patParm.getValue("MR_NO"));
                setValue("PAT_NAME", patName);
            } else return;//modify by wanglong 20130217
            TParm temp = new TParm();
            temp.setData("MR_NO", patParm.getValue("MR_NO"));
            TParm result2 = HRMCheckFeeTool.getInstance().onQueryMaster(temp);
            if (result2.getErrCode() != 0) {
                this.messageBox(result2.getErrText());
                return;
            }
            if (result2.getCount() < 1) {
                this.messageBox("E0116");// û������
                return;
            } else if (result2.getCount() == 1) {// ��������
                if (getPanel("PANEL_MASTER").isShowing()) {
                    result2 = addTotAmtRow(masterTable, result2);
                    masterTable.setParmValue(result2);
                } else {
                    TParm param = result2.getRow(0);
                    param.setData("REPORT_DATE", StringTool.getString(param.getTimestamp("REPORT_DATE"), "yyyy/MM/dd"));
                    result2 = HRMCheckFeeTool.getInstance().onQueryDetail(param);
                    if (result2.getErrCode() != 0) {
                        this.messageBox(result2.getErrText());
                        return;
                    }
                    if (result2.getCount() < 1) {
                        return;
                    } else {
                        result2 = addTotAmtRow(detailTable, result2);
                        detailTable.setParmValue(result2);
                        parmValue = result2;
                        showParmValue = detailTable.getShowParmValue();
                    }
                }
            } else {// ��������(����ѡ�񴰿�)
                TParm param = new TParm();
                param.setData("TYPE", getPanel("PANEL_MASTER").isShowing() ? "MASTER" : "DETAIL");
                param.setData("parmValue", result2);
                Object obj2 = this.openDialog("%ROOT%\\config\\hrm\\HRMPatFeeChoose.x", param);
                if (obj2 != null) {
                    result2 = (TParm) obj2;
                    if (getPanel("PANEL_MASTER").isShowing()) {
                        result2 = addTotAmtRow(masterTable, result2);
                        masterTable.setParmValue(result2);
                    } else {
                        result2 = addTotAmtRow(detailTable, result2);
                        detailTable.setParmValue(result2);
                        parmValue = result2;
                        showParmValue = detailTable.getShowParmValue();
                    }
                }
            }
        }
    }
    
    /**
     * ��ӡ
     */
    public void onPrint() {// modify by wanglong 20130118
        if (parmValue.getCount() <= 0) {
            this.messageBox("�޴�ӡ����");
            return;
        }
        boolean isView = true;
        if (((TCheckBox) this.getComponent("PRINT_FLG")).getValue().equals("Y")) {
            isView = false;
        }
        Timestamp now = SystemTool.getInstance().getDate();
        String printDate = StringTool.getString(now, "yyyy/MM/dd HH:mm:ss");
        DecimalFormat df = new DecimalFormat("###########0.00");
        double allMoney = 0;
        TParm printParm = new TParm();
        int count = 0;
        for (int i = 0; i < parmValue.getCount(); i++) {
            count++;
            printParm.addData("ORDER_DESC", parmValue.getData("ORDER_DESC", i));
            printParm.addData("SPECIFICATION", parmValue.getData("SPECIFICATION", i));
            printParm.addData("UNIT_CHN_DESC", parmValue.getData("UNIT_CHN_DESC", i));
            printParm.addData("OWN_PRICE", df.format(parmValue.getDouble("OWN_PRICE", i)));
            printParm.addData("DISPENSE_QTY", parmValue.getData("DISPENSE_QTY", i));
            printParm.addData("AR_AMT", df.format(parmValue.getDouble("AR_AMT", i)));
            allMoney += parmValue.getDouble("AR_AMT", i);
            if (!parmValue.getValue("MR_NO", i).equals(parmValue.getValue("MR_NO", i + 1))) {
                printParm.setCount(count);
                printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
                printParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
                printParm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
                printParm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
                printParm.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
                printParm.addData("SYSTEM", "COLUMNS", "AR_AMT");
                TParm printData = new TParm();
                printData.setData("NAME", "TEXT", parmValue.getValue("PAT_NAME", i));
                printData.setData("COMPANY", "TEXT", showParmValue.getValue("COMPANY_CODE", i));
                printData.setData("DATE", "TEXT", printDate);
                printData.setData("USER", "TEXT", Operator.getName());
                printData.setData("TABLE", printParm.getData());
                allMoney = StringTool.round(allMoney, 2);
                printData.setData("MONEY", "TEXT", df.format(allMoney));
                printData.setData("HOSP","TEXT",Operator.getHospitalCHNFullName()+"�������嵥");//add by wanglong 20130730
                this.openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMCheckFee.jhw", printData, isView);
                printParm = new TParm();
                count = 0;
                allMoney = 0;
            }
        }
    }

    /**
     * ����excel
     */
    public void onExport() {
        if (getPanel("PANEL_MASTER").isShowing()) {// ����
            if (masterTable.getRowCount() <= 0) {
                this.messageBox("û�л������");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(masterTable, "�����û��ܱ�");
        } else {// ��ϸ
            if (detailTable.getRowCount() <= 0) {
                this.messageBox("û�л������");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(detailTable, "��������ϸ��");
        }
    }

    /**
     * ���
     */
    public void onClear() {
        this.clearValue("MR_NO;PAT_NAME;REPORT_DATE");
        this.setValue("COMPANY_CODE", "");
        this.setValue("CONTRACT_CODE", "");
        this.setValue("PACKAGE_CODE", "");
        this.setValue("UNREPORT_COUNT", "");
        this.setValue("REPORT_COUNT", "");
        contract.getPopupMenuData().getData().clear();
        contract.filter();
        if (getPanel("PANEL_MASTER").isShowing()) {// ����
//            masterTable.removeRowAll();//modify by wanglong 20131118
            masterTable.setParmValue(new TParm());
        } else {
//            detailTable.removeRowAll();
            detailTable.setParmValue(new TParm());
        }
        parmValue = new TParm();
        showParmValue = new TParm();
    }

    /**
     * ���ñ�������
     */
    public void onTabbedPaneChoose() {// add by wanglong 20130108
        if (getPanel("PANEL_DETAIL").isShowing()) {// ϸ��
            ((TMenuItem) this.getComponent("print")).setEnabled(true);
        } else {// ����
            ((TMenuItem) this.getComponent("print")).setEnabled(false);
        }
    }

    /**
     * ��ñ�����
     * 
     * @param tagName
     * @return
     */
    public TTable getTable(String tagName) {
        return (TTable) this.getComponent(tagName);
    }

    /**
     * ���������
     * 
     * @param tagName
     * @return
     */
    public TPanel getPanel(String tagName) {// add by wanglong 20130118
        return (TPanel) this.getComponent(tagName);
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
