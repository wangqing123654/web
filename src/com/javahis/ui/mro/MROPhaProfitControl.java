package com.javahis.ui.mro;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import java.util.Calendar;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import jdo.sys.Operator;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: ҩ��ҩƷ����ռ������ı���</p>
 *
 * <p>Description: ҩ��ҩƷ����ռ������ı���</p>
 *
 * <p>Copyright: Copyright (c) �����к� 2011</p>
 *
 * <p>Company: javahis </p>
 * com.javahis.ui.mro.MROPhaProfitControl
 * @author ZhenQin
 * @version 4.0
 */
public class MROPhaProfitControl
    extends TControl {


    /**
     * ������ʾ���ݵ�TAble
     */
    private TTable table = null;

    private List headerList = null;
    /**
     * ���췽��
     */
    public MROPhaProfitControl() {
        super();
    }

    /**
     * ��ʼ��,ʱ��
     * ��ʼ������ǰһ�µ�1��,���������ǵ�ǰ�µ�1��
     */
    public void onInit(){
        initDate();
        table = (TTable)this.getComponent("tTable_1");
        String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'STA_CHARGE' ORDER BY ID ";
        //��ѯ
        TParm parm = new TParm(
              TJDODBTool.getInstance().select(sql)
        );
        //��ͷ
        StringBuilder header = new StringBuilder();
        //���еĶ�Ӧ��TParm��������
        StringBuilder parmMap = new StringBuilder();
        //���䷽ʽ
        StringBuilder alignment = new StringBuilder();
        //���б���
        header.append("����,100;ҽʦ,85");
        //��������
        parmMap.append("DEPT_CHN_DESC;USER_NAME");
        //#####0.000%
        alignment.append("0,left;1,left");
        headerList = new ArrayList();
        if(parm.getCount("ID") > 0){
            int i = 0;
            for(i = 0; i < parm.getCount("ID"); i++){
                //���һ���ֺ���������ǰһ��
                header.append(";");
                //���һ��head,ͬʱ��Ӧһ��map
                header.append(parm.getValue("CHN_DESC", i));
                //��ӱ������ʾ���
                header.append(",80");
                alignment.append(";" + (i * 2 + 2) + ",right");

                //��map���һ����������
                parmMap.append(";DATA" + parm.getValue("ID", i));
                headerList.add("DATA" + parm.getValue("ID", i));

                //���һ���ֺ���������ǰһ��
                header.append(";");
                //���һ��head,ͬʱ��Ӧһ��map
                header.append(parm.getValue("CHN_DESC", i));
                header.append("ռ���������");
                //��ӱ������ʾ���
                header.append(",120");
                alignment.append(";" + (i * 2 + 3) + ",right");

                //��map���һ����������
                parmMap.append(";DATA_" + parm.getValue("ID", i));
                headerList.add("DATA_" + parm.getValue("ID", i));
            }
            alignment.append(";" + (i * 2 + 2) + ",right");
        }
        header.append(";������,60");
        parmMap.append(";TOT_AMT");
        table.setHeader(header.toString());
        table.setParmMap(parmMap.toString());
        table.setColumnHorizontalAlignmentData(alignment.toString());
    }

    /**
     * ��ѯ
     */
    public void onQuery(){
        int subdate = getSubDate((Timestamp)getValue("START_DATE"), (Timestamp)getValue("END_DATE"));
        if(subdate < 0){
            this.messageBox("��ѡ��һ���������ʱ����!");
            return ;
        }
        //��ѯʱ��
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //ȡ�ÿ�ʼʱ��,�ñ���ʼʱ���Ǳ����
        String startDate = format.format((Timestamp)this.getValue("START_DATE"));
        //ȡ�ý���ʱ��,����ʱ��Ҳ�Ǳ����
        String endDate = format.format((Timestamp)this.getValue("END_DATE"));
        //ȡ�ù����SQL
        String sql = getQuerySQL(startDate, endDate);
        //��ѯ
        TParm parm = new TParm(
              TJDODBTool.getInstance().select(sql)
        );
        //���û�в�ѯ������,����ʾ������
        if(parm.getErrCode() < 0 || parm.getCount("DEPT_CODE") == 0){
            this.messageBox("û�в�ѯ������!");
            return ;
        }
        TParm result = addCleanParm(parm);
        cleanParm(result);
        table.setParmValue(result);

    }

    public void onExport(){
    if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "ҩ��ҩƷ����ռ������ı���");

}
    /**
     * ���
     */
    public void onClear(){
        this.clearValue("DEPT_CODE;USER_CODE");
        initDate();
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
     * @param startDate Timestamp
     * @param endDate Timestamp
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
     * ��ѯһ��ʱ��ҩƷ��������,������Ҫ��SQL
     * @return String ���ز�ѯSQL
     */
    private String getQuerySQL(String startDate, String endDate) {
        TParm parm = this.getParmForTag("DEPT_CODE;USER_CODE");
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT DEPT_CODE, DEPT_CHN_DESC, DR_CODE, USER_NAME, ID, SUM(TOT_AMT) AS TOT_AMT ");
        sql.append(" FROM (SELECT   B.DEPT_CODE, E.DEPT_CHN_DESC, B.DR_CODE, F.USER_NAME, C.ID, ");
        sql.append(" SUM (B.AR_AMT) AS TOT_AMT ");
        sql.append(" FROM SYS_CHARGE_HOSP A, OPD_ORDER B, SYS_DICTIONARY C, SYS_DEPT E, SYS_OPERATOR F ");
        sql.append(" WHERE A.CHARGE_HOSP_CODE = B.HEXP_CODE ");
        sql.append(" AND B.DR_CODE = F.USER_ID ");
        sql.append(" AND B.DEPT_CODE = E.DEPT_CODE ");
        sql.append(" AND A.STA_CHARGE_CODE = C.ID ");
        sql.append(" AND C.GROUP_ID = 'STA_CHARGE' ");

        if(!parm.getValue("DEPT_CODE").equals("")){
            sql.append(" AND B.DEPT_CODE = '");
            sql.append(parm.getValue("DEPT_CODE"));
            sql.append("' ");
        }
        if(!parm.getValue("USER_CODE").equals("")){
            sql.append(" AND B.DR_CODE = '");
            sql.append(parm.getValue("USER_CODE"));
            sql.append("' ");
        }
        sql.append(" AND B.REGION_CODE = '" + Operator.getRegion() + "'");
        sql.append(" AND E.REGION_CODE = '" + Operator.getRegion() + "'");
        sql.append(" AND B.BILL_DATE BETWEEN TO_DATE('");
        sql.append(startDate);
        sql.append("', 'YYYY-MM-DD') ");
        sql.append(" AND TO_DATE('");
        sql.append(endDate);
        sql.append("', 'YYYY-MM-DD') ");
        sql.append("GROUP BY B.DEPT_CODE, E.DEPT_CHN_DESC, B.DR_CODE, C.ID, F.USER_NAME  ");
        //����������ѯ
        sql.append("UNION ALL ");

        sql.append(" SELECT C.DEPT_CODE, E.DEPT_CHN_DESC, C.EXE_DR_CODE AS DR_CODE, ");
        sql.append(" F.USER_NAME, G.STA_CHARGE_CODE AS ID, ");
        sql.append(" SUM (C.TOT_AMT) AS TOT_AMT ");
        sql.append(" FROM IBS_ORDD C, SYS_FEE D, SYS_DEPT E, SYS_OPERATOR F, SYS_CHARGE_HOSP G ");
        sql.append(" WHERE C.ORDER_CODE = D.ORDER_CODE ");
        sql.append(" AND C.DEPT_CODE = E.DEPT_CODE ");
        sql.append("  AND C.EXE_DR_CODE = F.USER_ID ");
        sql.append(" AND C.HEXP_CODE = G.CHARGE_HOSP_CODE ");
        sql.append(" AND E.REGION_CODE = '" + Operator.getRegion() + "'");
        //���ѡ��ɿ���,���ṩ���տ��Ҳ�ѯ
        if(!parm.getValue("DEPT_CODE").equals("")){
            sql.append(" AND C.DEPT_CODE = '");
            sql.append(parm.getValue("DEPT_CODE"));
            sql.append("' ");
        }
        //���ѡ����ҽʦ,���ҽʦcode����һ����ѯ����
        if(!parm.getValue("USER_CODE").equals("")){
            sql.append(" AND C.EXE_DR_CODE = '");
            sql.append(parm.getValue("USER_CODE"));
            sql.append("' ");
        }
        sql.append(" AND C.BILL_DATE BETWEEN TO_DATE('");
        sql.append(startDate);
        sql.append("', 'YYYY-MM-DD') ");
        sql.append(" AND TO_DATE('");
        sql.append(endDate);
        sql.append("', 'YYYY-MM-DD') ");

        sql.append(" GROUP BY C.DEPT_CODE, ");
        sql.append(" E.DEPT_CHN_DESC, ");
        sql.append(" C.EXE_DR_CODE, ");
        sql.append(" F.USER_NAME, ");
        sql.append("  G.STA_CHARGE_CODE) A ");
        sql.append(
            " GROUP BY DEPT_CODE, DEPT_CHN_DESC, DR_CODE, USER_NAME, ID ");
        sql.append(" ORDER BY DEPT_CODE, DR_CODE, ID ");


        return sql.toString();
    }

    /**
     * ��������,�����ݿ��в�ѯ�������ݷ������������е�,������Ҫ���������еĿ������ݹ���ɺ�������<br>
     * ��:DEPT_CODE  DR_CODE  ID   TOT_AMT
     *    1010101   1001      01   100.0
     *    1010101   1001      02   200.0
     *
     * ��DEPT_CODE��DR_CODE��ͬʱ,������Ҫ���������,ID�е�������ָҩƷ��,���ѵ�
     * DEPT_CODE  DR_CODE  ҩƷ�� ҩƷ��ռ�ܷ��õı��� ���� ����ռ�ܷ��õı��� �ܷ���
     * 1010101    1001     100   0                200    0                300
     * @param data TParm ԭ����
     * @return TParm ���ؾ������������
     */
    private TParm addCleanParm(TParm data) {
        TParm result = new TParm();
        result.addData("DEPT_CODE", null);
        result.addData("DR_CODE", null);
        result.addData("DEPT_CHN_DESC", null);
        result.addData("USER_NAME", null);
        //������Ҫ��¼����ͷ(��ͷ)
        for (int i = 0; i < headerList.size(); i++) {
            result.addData(headerList.get(i).toString(), null);
        }
        result.addData("TOT_AMT", null);
        // ��ѯ������,��Ҫ�����ҽʦ��ҩ������ռ������İٷֱ�
        for (int i = 0; i < data.getCount("DEPT_CODE"); i++) {
            //ȡ��ԭ���ݵĵ�������
            TParm rowParm = data.getRow(i);
            //��־λ,��DEPT_CODE��DR_CODEֵflgΪfalse
            boolean flg = true;
            for (int j = 0; j < result.getCount("DEPT_CODE"); j++) {
                //��DEPT_CODE��DR_CODE����ͬһ������
                if (result.getValue("DEPT_CODE", j).equals(
                    rowParm.getValue("DEPT_CODE"))
                    && result.getValue("DR_CODE", j).equals(
                        rowParm.getValue("DR_CODE"))) {
                    result.setData("DATA" + rowParm.getValue("ID"), j,
                                   rowParm.getValue("TOT_AMT"));
                    result.setData("DATA_" + rowParm.getValue("ID"), j, 0);
                    double tot_amt = 0;
                    try {
                        //��ֹ������
                        tot_amt = Double.parseDouble(result.getValue("TOT_AMT", j));
                    }
                    catch (NumberFormatException e) {
                        //����������,��ֵ����Ĭ��0
                        tot_amt = 0;
                    }
                    result.setData("TOT_AMT", j, tot_amt + rowParm.getDouble("TOT_AMT"));
                    flg = false;
                    break;
                }
            }
            //��flg=true,����Ҫ���һ��������
            if (flg) {
                result.addData("DEPT_CODE", rowParm.getValue("DEPT_CODE"));
                result.addData("DR_CODE", rowParm.getValue("DR_CODE"));
                result.addData("DEPT_CHN_DESC", rowParm.getValue("DEPT_CHN_DESC"));
                result.addData("USER_NAME", rowParm.getValue("USER_NAME"));
                result.addData("TOT_AMT", rowParm.getDouble("TOT_AMT"));
                //����ͷ��DATA01,���Ӧ�İٷֱ���DATA_01
                int k = 0;
                //����ѭ����Ҫÿ����ѭ��һ��
                while (k < headerList.size()) {
                    if ( ("DATA" +
                        rowParm.getValue("ID")).equals(headerList.get(k).
                        toString())) {
                        result.addData(headerList.get(k).toString(),
                                       rowParm.getValue("TOT_AMT"));
                        result.addData(headerList.get(k + 1).toString(), 0);
                    }
                    else {
                        result.addData(headerList.get(k).toString(), 0);
                        result.addData(headerList.get(k + 1).toString(), 0);
                    }

                    k += 2;
                }

            }

        }
        //��ȥ��һ������
        result.removeRow(0);
        return result;
    }

    /**
     * ��������,����ٷֱȵ�
     * @param parm TParm
     */
    private void cleanParm(TParm parm){
        for(int i = 0; i < parm.getCount("DEPT_CODE"); i++){
            double tot_amt = parm.getDouble("TOT_AMT", i);
            for(int j = 1; j < headerList.size(); ){
                double data = parm.getDouble(headerList.get(j - 1).toString(), i);
                try{
                    double tmp = data / tot_amt * 100;
                    //����2ΪС��
                    parm.setData(headerList.get(j).toString(), i, (StringTool.round(tmp, 2)) + "%");
                }catch(NumberFormatException e){
                    parm.setData(headerList.get(j).toString(), i, "0%");
                }
                j += 2;
            }
        }
    }
}
