package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import java.util.Calendar;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.*;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.ui.TTextFormat;
import com.javahis.system.combo.TComboDept;
import com.javahis.system.textFormat.TextFormatDept;

/**
 * <p>Title: �ṩ�Ի���������ط���֧�����ʵʱ��أ��Թ���ҩƷ,��ֵ�ĲĹ�����</p>
 *
 * <p>Description: �ṩ�Ի���������ط���֧�����ʵʱ��أ��Թ���ҩƷ������</p>
 *
 * <p>Copyright: Copyright (c) Javahis 2011 </p>
 *
 * <p>Company: Javahis </p>
 * com.javahis.ui.mro.MROExpDrugsHighConsControl
 * @author ZhenQin
 * @version 4.0
 */
public class MROExpDrugsHighConsControl
    extends TControl {

    /**
     * ������ʾ���ݵ�TAble
     */
    private TTable table = null;

    /**
     * ����ҩƷ,��ֵ�ĲĵĽ��
     */
    private double price = 800;

    /**
     * ��ѯ�������ҩƷ,��ֵ�Ĳ�
     */
    private String kind = null;

    /**
     * ���췽��HighCons
     */
    public MROExpDrugsHighConsControl() {
        super();
    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        Object obj = this.getParameter();
        table = (TTable)this.getComponent("tTable_0");
        //û�в����򲻳�ʼ��
        if(obj == null){
            this.kind = null;
            initN();
            initKind(this.kind);
            initDate();
        } else if(obj instanceof String) {
            this.kind = obj.toString();
            //����ҩƷ
            if(this.kind.equalsIgnoreCase("PHA")){
                //��ʼ��ҩƷ
                initExpDrug();
                //��ѯ�����ǹ���ҩƷ
                initKind(this.kind);
                //��ʼ��ʱ��
                initDate();
                //��ֵ�Ĳ�
            }else if(this.kind.equalsIgnoreCase("INV")){
                ////��ʼ���Ĳ�
                initHighCons();
                //��ʼ����ֵ�Ĳ�
                initKind("inv");
                //��ʼ��ʱ��
                initDate();
                //�����򲻳�ʼ��
            }else{
                this.kind = null;
                initN();
                initKind(this.kind);
                initDate();

            }
            //��������������,����ʼ��
        } else {
            this.kind = null;
            initN();
            initKind(this.kind);
            initDate();

        }
    }

    /**
     * û�в���,��ʼ��Ĭ��,Ĭ�ϵ�price��800;
     */
    private void initN(){
        TTextField priceTxt = (TTextField)this.getComponent("price");
        priceTxt.setText("" + price);
    }

    /**
     * ��ʼ��ҩƷ
     */
    private void initExpDrug(){

        String sql = "SELECT HIGH_VALUE_PHA FROM PHA_SYSPARM WHERE HIGH_VALUE_PHA IS NOT NULL ORDER BY HIGH_VALUE_PHA";
        //��ѯ
        TParm data = new TParm(
            TJDODBTool.getInstance().select(sql)
        );
        price = data.getDouble("HIGH_VALUE_PHA", 0);
        TTextField priceTxt = (TTextField)this.getComponent("price");
        priceTxt.setText("" + price);
    }

    /**
     * ��ʼ���Ĳ�
     */
    private void initHighCons(){

        String sql = "SELECT HIGH_VALUE_INV FROM INV_SYSPARM WHERE HIGH_VALUE_INV IS NOT NULL ORDER BY HIGH_VALUE_INV";
        //��ѯ
        TParm data = new TParm(
            TJDODBTool.getInstance().select(sql)
        );
        price = data.getDouble("HIGH_VALUE_INV", 0);
        TTextField priceTxt = (TTextField)this.getComponent("price");
        priceTxt.setText("" + price);

    }


    /**
     * ��ʼ����ѯ����,����ҩƷ,��ֵ�Ĳ�.����null�����ò�ѯ����Ϊpha
     * @param id String
     */
    private void initKind(String id){
        initTable(id);
        if(id == null){
            this.setValue("kind", "pha");
            initExpDrug();
            return ;
        }
        TComboBox kindCombo = (TComboBox)this.getComponent("kind");
        this.setValue("kind", id);
        kindCombo.setEnabled(false);
    }


    /**
     * ��ѯ������ת��
     */
    public void changeKind(){
        String kind = this.getValueString("kind");
        table.removeRowAll();
        if(kind.equalsIgnoreCase("pha")){
            initExpDrug();
        } else {
            this.initHighCons();
        }
    }

    /**
     * ��ʼ��Table,�������������ʼ��Ĭ��,Ĭ���ǲ�ѯ����ҩƷ<br>
     * ���ǳ����ڳ�ʼ���������,������Ӧ�ķ�ʽ��ʼ��<br>
     * ����û�д������,���������Ϊ���ò�ѯʱ��̬����ʵ��ѯ����ҩƷ���Ǹ�ֵ�Ĳ�<br>
     *
     * @param kind String ��ѯ����,����ҩƷ���Ǹ�ֵ�Ĳ�(pha,inv)
     */
    private void initTable(String kind){
        String header = null;
        String parmMap = "DEPT_CHN_DESC;CASE_NO;MR_NO;PAT_NAME;IN_DATE;DS_DATE;ORDER_DESC;TOT_AMT;BILL_DATE;USER_NAME";
        if(kind == null || kind.equalsIgnoreCase("pha")){
            header = "����,120;�������,100;������,120;����,80;��Ժʱ��,100,Timestamp;��Ժʱ��,100,Timestamp;ҩƷ����,180;�۸�,80,double;ִ��ʱ��,120,Timestamp;����ҽ��,100";
        }else if(kind.equalsIgnoreCase("inv")){
            header = "����,120;�������,100;������,120;����,80;��Ժʱ��,100,Timestamp;��Ժʱ��,100,Timestamp;�Ĳ�����,180;�۸�,80,double;ִ��ʱ��,120,Timestamp;����ҽ��,100";
        }else{
            header = "����,120;�������,100;������,120;����,80;��Ժʱ��,100,Timestamp;��Ժʱ��,100,Timestamp;ҩƷ����,180;�۸�,80,double;ִ��ʱ��,120,Timestamp;����ҽ��,100";
        }
        table.setHeader(header);
        table.setParmMap(parmMap);
        table.setLockColumns("All");
        table.setColumnHorizontalAlignmentData("0,left;3,left;6,left;7,left;9,left");

    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        int subDate = getSubDate((Timestamp)this.getValue("START_DATE"), (Timestamp)this.getValue("END_DATE"));
        if(subDate < 0){
            this.messageBox("��ѡ������ʱ����!");
            return;
        }
        TParm param = this.getParmForTag("price;kind;DEPT_CODE;START_DATE;END_DATE");

        //����ʼ��û�д�����������,���������ṩ��ѯ����
        if(kind == null){
            //ÿ�β�ѯ����ʼ��һ���б�
            initTable(param.getValue("kind"));
        }
        //����ѯ���ǹ���ҩƷ
        if(param.getValue("kind").equalsIgnoreCase("pha")){
            try{
                //��������Ĺ���ҩƷ�ļ۸�,����������,����ʾ,����ִ��Ĭ�ϵĳ�ʼ������
                Double.parseDouble(param.getValue("price"));
            }catch(NumberFormatException e){
                this.messageBox("��ѡ�����Ľ��!");
                //ִ��Ĭ�ϵĳ�ʼ������
                initExpDrug();
                return;
            }
            //��ѯ��Ӧ�Ĳ�ѯ����
            queryExpDrug(param);
            //�����ǲ�ѯ��ֵ�Ĳ�
        }else{
            try{
                //��������ĸ�ֵ�Ĳĵļ۸�,���������,����ʾ��������Ӧ��Ĭ�ϳ�ʼ������
                Double.parseDouble(param.getValue("price"));
            }catch(NumberFormatException e){
                this.messageBox("��ѡ�����Ľ��!");
                //ִ��Ĭ�ϵĳ�ʼ������
                initHighCons();
                return;
            }
            //���ø�ֵ�ĲĵĲ�ѯ����
            queryHighCons(param);
        }
    }

    /**
     * ��ѯ����ҩƷ
     * @param data TParm
     */
    private void queryExpDrug(TParm data){
        //ʱ���ʽ��format
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //��ѯSQL
        StringBuilder sqlExpDrug = new StringBuilder();
        sqlExpDrug.append(" SELECT A.DEPT_CODE, E.DEPT_CHN_DESC, A.CASE_NO, D.MR_NO, ");
        sqlExpDrug.append("        (SELECT PAT_NAME ");
        sqlExpDrug.append("           FROM SYS_PATINFO ");
        sqlExpDrug.append("          WHERE MR_NO = D.MR_NO) ");
        sqlExpDrug.append(" AS PAT_NAME, D.IN_DATE, D.DS_DATE,A.ORDER_CODE, B.ORDER_DESC, ");
        sqlExpDrug.append("        A.TOT_AMT, A.BILL_DATE, A.DR_CODE, C.USER_NAME ");
        sqlExpDrug.append("    FROM IBS_ORDD A, PHA_BASE B, SYS_OPERATOR C, ADM_INP D, SYS_DEPT E ");
        sqlExpDrug.append("   WHERE A.ORDER_CODE = B.ORDER_CODE ");
        sqlExpDrug.append("     AND A.DEPT_CODE = E.DEPT_CODE ");
        sqlExpDrug.append("     AND A.DR_CODE = C.USER_ID ");
        sqlExpDrug.append("     AND A.CASE_NO = D.CASE_NO ");
        if(!data.getValue("DEPT_CODE").equals("")){
            sqlExpDrug.append("     AND A.DEPT_CODE = '");
            sqlExpDrug.append(data.getValue("DEPT_CODE"));
            sqlExpDrug.append("' ");
        }
        sqlExpDrug.append("     AND A.TOT_AMT > " + data.getValue("price"));
        sqlExpDrug.append("     AND A.ORDER_CAT1_CODE LIKE 'PHA%' ");
        sqlExpDrug.append("     AND D.REGION_CODE = '" + Operator.getRegion() + "' ");
        try {
            sqlExpDrug.append("     AND A.BILL_DATE BETWEEN TO_DATE ('");
            //����ͳ����ʼʱ��
            sqlExpDrug.append(format.format(format.parse(data.getValue("START_DATE"))));
            sqlExpDrug.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sqlExpDrug.append("     AND TO_DATE ('");
            //����ͳ�ƽ���ʱ��
            sqlExpDrug.append(format.format(format.parse(data.getValue("END_DATE"))));
            sqlExpDrug.append("', 'YYYY-MM-DD HH24:MI:SS') ");
        }catch(ParseException e){
            e.printStackTrace();
        }
        //��ѯ����
        TParm result = new TParm(
            TJDODBTool.getInstance().select(sqlExpDrug.toString()));
        //�����ѯ�쳣,����û�в�ѯ������,����ʾû�в�ѯ������
        if(result.getErrCode() < 0 || result.getCount() <= 0){
            this.messageBox("û�в�ѯ������!");
            table.removeRowAll();
            return;
        }
        table.setParmValue(result);
    }

    /**
     * ��ѯ��ֵ�Ĳ�
     * @param data TParm
     */
    private void queryHighCons(TParm data) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sqlHighCons = new StringBuilder();
        sqlHighCons.append(" SELECT A.CASE_NO, A.DEPT_CODE, E.DEPT_CHN_DESC, A.CASE_NO,D.MR_NO,  ");
        sqlHighCons.append(" (SELECT PAT_NAME ");
        sqlHighCons.append("  FROM SYS_PATINFO ");
        sqlHighCons.append(
            " WHERE MR_NO = D.MR_NO) AS PAT_NAME, D.IN_DATE, D.DS_DATE, ");
        sqlHighCons.append(
            " A.ORDER_CODE, B.ORDER_DESC, A.ORDER_CAT1_CODE, A.BILL_DATE, A.TOT_AMT, ");
        sqlHighCons.append(" A.DR_CODE, C.USER_NAME ");
        sqlHighCons.append(
            " FROM IBS_ORDD A, SYS_FEE B, SYS_OPERATOR C, ADM_INP D, SYS_DEPT E ");
        sqlHighCons.append(" WHERE A.ORDER_CODE = B.ORDER_CODE ");
        sqlHighCons.append("  AND A.DR_CODE = C.USER_ID ");
        sqlHighCons.append("  AND A.DEPT_CODE = E.DEPT_CODE ");
        sqlHighCons.append(" AND A.CASE_NO = D.CASE_NO ");
        //��ֵ�Ĳĵļ۸�
        sqlHighCons.append(" AND A.TOT_AMT > " + data.getValue("price"));
        sqlHighCons.append(" AND (A.ORDER_CAT1_CODE = 'MAT' OR A.ORDER_CAT1_CODE = 'INV') ");
        sqlHighCons.append(" AND D.REGION_CODE = '" + Operator.getRegion() + "' ");
        //����
        if (!data.getValue("DEPT_CODE").equals("")) {
            sqlHighCons.append("     AND A.DEPT_CODE = '");
            sqlHighCons.append(data.getValue("DEPT_CODE"));
            sqlHighCons.append("' ");
        }
        try {
            //��ʼ����ʼʱ��ͽ���ʱ��
            sqlHighCons.append("     AND A.BILL_DATE BETWEEN TO_DATE ('");
            sqlHighCons.append(format.format(format.parse(data.getValue(
                "START_DATE"))));
            sqlHighCons.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sqlHighCons.append("     AND TO_DATE ('");
            sqlHighCons.append(format.format(format.parse(data.getValue(
                "END_DATE"))));
            sqlHighCons.append("', 'YYYY-MM-DD HH24:MI:SS') ");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("sqlHighCons==>>  " + sqlHighCons);
        //�����ѯ�쳣,����û�в�ѯ������,����ʾû�в�ѯ������
        TParm result = new TParm(
            TJDODBTool.getInstance().select(sqlHighCons.toString()));
        if (result.getErrCode() < 0 || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ������!");
            table.removeRowAll();
            return;
        }
        table.setParmValue(result);
    }

    /**
     * ���
     */
    public void onClear() {
        this.setValue("DEPT_CODE", Operator.getDept());
        //����ʼ��û�д�����������,���������ṩ��ѯ����
        changeKind();
        initDate();
    }

    /**
     * ��ӡ
     */
    public void onPrint() {
        if(table.getRowCount() <= 0){
            this.messageBox("û����Ҫ��ӡ������!");
            return ;
        }
        TParm param = this.getParmForTag("price;kind;DEPT_CODE;START_DATE;END_DATE");
        //��ʼ��û�м������
        if(kind == null){
            //�жϲ�ѯ�ǹ���ҩƷ,
            if(param.getValue("kind").equals("pha")){
                //ִ�д�ӡ����ҩƷ
                printExpDrug(param);
                //��ֵ�Ĳ�
            }else{
                printHighCons(param);
            }
        }else{
            //��ʼ�������д������,����ҩƷ
            if(kind.equalsIgnoreCase("pha")){
                printExpDrug(param);
            }else{
                //��ֵ�Ĳ�
                printHighCons(param);
            }
        }
    }

    /**
     * ������Xls
     */
    public void onExport(){
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "����ҩƷ,��ֵ�Ĳ����Ʒ������ͳ��");

    }

    /**
     * ��ʼ��ʱ��,��ʼ��������һ�µ�1��,���������ǵ�ǰ�µ�1��
     */
    private void initDate(){
        Timestamp currentDate = SystemTool.getInstance().getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        //��ʼ������ʱ��,��ǰ������ʱ����
        this.setValue("END_DATE", new Timestamp(calendar.getTimeInMillis()));
        int year = calendar.get(Calendar.YEAR);
        //ע��: ��calendar��ȡ�õ��·��Ǵ�0��ʼ��,�����11
        int month = calendar.get(Calendar.MONTH);

        //��ʼ����ʼʱ��,ǰһ���µ�һ��
        int startyear = year;
        int startmonth = month;
        //��ʼ����ʼʱ��,���µĵ�һ��,���������1��,����ʼ��Ӧ������һ��.�����1��1��,����ʼ��Ӧ������һ���12��1��
        if(calendar.get(Calendar.DATE) == 1){
            //�жϵ�ǰ���ǲ���1��,�����1��,����ʼ��Ӧ������һ��
            startyear = month == 0 ? (year - 1) : year;
            //�����1��,����ʼ��Ӧ������һ������һ����
            startmonth = month == 0 ? 11 : month;
        }
        calendar.set(startyear, startmonth, 1, 0, 0, 0);
        this.setValue("START_DATE",new Timestamp(calendar.getTimeInMillis()));
    }

    /**
     * ��������ʱ��ε��������,�����ʵ����������ڽ�������,���򷵻�-1
     * @param startDate Timestamp ��ʼʱ��
     * @param endDate Timestamp ����ʱ��
     * @return int �����������
     */
    private int getSubDate(Timestamp startDate, Timestamp endDate){
        if(startDate == null || endDate == null){
            return -1;
        }
        //��ʼʱ��ҪС�ڽ���ʱ��
        if(startDate.getTime() >= endDate.getTime()){
            return -1;
        }
        //һ��ĺ�����
        long date = 24 * 60 * 60 * 1000;
        //��ʱ����һ��ĺ�����,������������
        return (int)((endDate.getTime() - startDate.getTime()) / date);
    }


    /**
     * ��ӡ����ҩƷ
     * @param data TParm
     */
    private void printExpDrug(TParm param){
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //TITLE;TIMEINTERVAL;DEPT;OPERATOR;DATE;EXPDRUG
        try {
            TParm data = new TParm();
            Timestamp START_DATE = (Timestamp)((TTextFormat)this.getComponent("START_DATE")).getValue();
            Timestamp END_DATE = (Timestamp)((TTextFormat)this.getComponent("END_DATE")).getValue();
            // ��ͷ����
            data.setData("TITLE", "TEXT", Operator.getHospitalCHNFullName() + "����ҩƷ����֧�����");
            data.setData("TIMEINTERVAL", "TEXT", "ͳ������: "
                     + format.format(START_DATE)
                     + " �� "
                     + format.format(END_DATE));

            TextFormatDept dept = (TextFormatDept)this.getComponent("DEPT_CODE");
            if(!param.getValue("DEPT_CODE").equals("")){
                data.setData("DEPT", "TEXT", "����: " + dept.getText());
            }else{
                data.setData("DEPT", "TEXT", "����: " + "���п���");
            }
            data.setData("EXPDRUG", "TEXT", "����ҩƷ: >= " + param.getValue("price"));
            // �������
            TParm parm = new TParm();
            TParm tableParm = table.getShowParmValue();
            // ��������е�Ԫ��
            //DISEASES_CODE;TOTAL_AMT;AVG_AMT;AVG_AMT_1
            for (int i = 0; i < table.getRowCount(); i++) {
                parm.addData("DEPT_CHN_DESC", tableParm.getValue("DEPT_CHN_DESC", i));
                parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
                parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
                parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
                parm.addData("IN_DATE", tableParm.getValue("IN_DATE", i));
                parm.addData("DS_DATE", tableParm.getValue("DS_DATE", i));
                parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
                parm.addData("TOT_AMT", tableParm.getValue("TOT_AMT", i));
                parm.addData("BILL_DATE", tableParm.getValue("BILL_DATE", i));
                parm.addData("USER_NAME", tableParm.getValue("USER_NAME", i));
            }
            // ������
            parm.setCount(parm.getCount("DEPT_CHN_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
            parm.addData("SYSTEM", "COLUMNS", "CASE_NO");
            parm.addData("SYSTEM", "COLUMNS", "MR_NO");
            parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
            parm.addData("SYSTEM", "COLUMNS", "IN_DATE");
            parm.addData("SYSTEM", "COLUMNS", "DS_DATE");
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
            parm.addData("SYSTEM", "COLUMNS", "BILL_DATE");
            parm.addData("SYSTEM", "COLUMNS", "USER_NAME");
            // �����ŵ�������
            data.setData("TABLE", parm.getData());
            // ��β����
            data.setData("DATE", "TEXT", "�Ʊ�ʱ��: " + format.format(SystemTool.getInstance().getDate()));
            data.setData("OPERATOR", "TEXT", "�Ʊ���: " + Operator.getName());
            openPrintDialog("%ROOT%\\config\\prt\\MRO\\ExpDrugPrint.jhw",
                                        data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ��ӡ��ֵ�Ĳ�
     * @param data TParm
     */
    private void  printHighCons(TParm param){
        //price;kind;DEPT_CODE;START_DATE;END_DATE,HighConsPrint.jhw
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //TITLE;TIMEINTERVAL;DEPT;OPERATOR;DATE;EXPDRUG
        try {
            TParm data = new TParm();
            Timestamp START_DATE = (Timestamp)((TTextFormat)this.getComponent("START_DATE")).getValue();
            Timestamp END_DATE = (Timestamp)((TTextFormat)this.getComponent("END_DATE")).getValue();
            // ��ͷ����
            data.setData("TITLE", "TEXT", Operator.getHospitalCHNFullName() + "��ֵ�Ĳķ���֧�����");
            data.setData("TIMEINTERVAL", "TEXT", "ͳ������: "
                     + format.format(START_DATE)
                     + " �� "
                     + format.format(END_DATE));

            TextFormatDept dept = (TextFormatDept)this.getComponent("DEPT_CODE");
            if(!param.getValue("DEPT_CODE").equals("")){
                data.setData("DEPT", "TEXT", "����: " + dept.getText());
            }else{
                data.setData("DEPT", "TEXT", "����: " + "���п���");
            }
            data.setData("HIGHCONS", "TEXT", "��ֵ�Ĳ�: >= " + param.getValue("price"));
            // �������
            TParm parm = new TParm();
            TParm tableParm = table.getShowParmValue();
            // ��������е�Ԫ��
            //DISEASES_CODE;TOTAL_AMT;AVG_AMT;AVG_AMT_1
            for (int i = 0; i < table.getRowCount(); i++) {
                parm.addData("DEPT_CHN_DESC", tableParm.getValue("DEPT_CHN_DESC", i));
                parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
                parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
                parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
                parm.addData("IN_DATE", tableParm.getValue("IN_DATE", i));
                parm.addData("DS_DATE", tableParm.getValue("DS_DATE", i));
                parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
                parm.addData("TOT_AMT", tableParm.getValue("TOT_AMT", i));
                parm.addData("BILL_DATE", tableParm.getValue("BILL_DATE", i));
                parm.addData("USER_NAME", tableParm.getValue("USER_NAME", i));
            }
            // ������
            parm.setCount(parm.getCount("DEPT_CHN_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
            parm.addData("SYSTEM", "COLUMNS", "CASE_NO");
            parm.addData("SYSTEM", "COLUMNS", "MR_NO");
            parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
            parm.addData("SYSTEM", "COLUMNS", "IN_DATE");
            parm.addData("SYSTEM", "COLUMNS", "DS_DATE");
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
            parm.addData("SYSTEM", "COLUMNS", "BILL_DATE");
            parm.addData("SYSTEM", "COLUMNS", "USER_NAME");
            // �����ŵ�������
            data.setData("TABLE", parm.getData());
            // ��β����
            data.setData("DATE", "TEXT", "�Ʊ�ʱ��: " + format.format(SystemTool.getInstance().getDate()));
            data.setData("OPERATOR", "TEXT", "�Ʊ���: " + Operator.getName());
            openPrintDialog("%ROOT%\\config\\prt\\MRO\\HighConsPrint.jhw",
                                        data);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
