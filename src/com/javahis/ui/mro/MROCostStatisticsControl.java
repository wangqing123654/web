package com.javahis.ui.mro;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import java.text.ParseException;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title: ָ��ʱ�ڵ����ַ���ͳ��</p>
 *
 * <p>Description: ָ��ʱ�ڵ����ַ���ͳ��</p>
 *
 * <p>Copyright: Copyright (c) Javahis 2011</p>
 *
 * <p>Company: JavaHis</p>
 * com.javahis.ui.mro.MROCostStatisticsControl
 *
 * @author ZhenQin
 * @version 4.0
 */
public class MROCostStatisticsControl
    extends TControl {

    /**
     * ��ʾ���ݵ�Table
     */
    private TTable table = null;

    /**
     * ���췽��
     */
    public MROCostStatisticsControl() {
        super();
    }


    /**
     * ��ʼ��,ʱ��
     * ��ʼ������ǰһ�µ�1��,���������ǵ�ǰ�µ�1��
     */
    public void onInit(){
        initDate();
        table = (TTable)this.getComponent("tTable_0");
    }

    /**
     * ��ѯ
     */
    public void onQuery(){
        int subdate = getSubDate((Timestamp)getValue("START_DATE"), (Timestamp)getValue("END_DATE"));
        //���ʱ��ѡ��,��ʼʱ�����ǱȽ���ʱ���,�����ǲ������.��ʾ����ʼ��
        if(subdate < 0){
            this.messageBox("��ѡ��һ���������ʱ������!");
            initDate();
            return ;
        }
        TParm param = this.getParmForTag("START_DATE;END_DATE;DISEASES_CODE");
        param.setData("REGION_CODE", Operator.getRegion());
        //ȡ�ù����SQL,����ѯ���ݿ�
        TParm result = new TParm(
              TJDODBTool.getInstance().select(getInpatientSQL(param))
        );

        //��ѯ�쳣,����û�в�ѯ�����ݿ�,��ʾ
        if(result.getErrCode() < 0 || result.getCount("DISEASES_CODE") <= 0){
            this.messageBox("û�в�ѯ������!");
            this.err(result.getErrName() + "    " + result.getErrText());
            table.removeRowAll();
            return ;
        }

        //ȡ�ù����SQL,��ѯ���ݿ�
        TParm result_1 = new TParm(
              TJDODBTool.getInstance().select(getOutPatientSQL(param))
        );
        if(result_1.getErrCode() < 0 || result_1.getCount("DISEASES_CODE") <= 0){
            this.messageBox("û�в�ѯ������!");
            this.err(result_1.getErrName() + "    " + result_1.getErrText());
            table.removeRowAll();
            return ;
        }
        TParm data = new TParm();
        //��������,����ȡ��Ҫ������
        for(int i = 0; i < result.getCount("DISEASES_CODE"); i++){
            data.addData("DISEASES_CODE", result.getValue("DISEASES_CODE", i));
            data.addData("TOTAL_AMT", StringTool.round(result.getDouble("TOTAL_AMT", i), 2));
            data.addData("AVG_AMT",  StringTool.round(result.getDouble("AVG_AMT", i), 2));
            for(int j = 0; j < result_1.getCount("DISEASES_CODE"); j++){
                if (result.getValue("DISEASES_CODE",
                    i).equals(result_1.getValue("DISEASES_CODE", j))) {
                    data.addData("AVG_AMT_1", StringTool.round(result_1.getDouble("AVG_AMT", j), 2));
                }
            }
        }
        table.setParmValue(data);
    }

    /**
     * ���
     */
    public void onClear(){
        this.clearValue("DISEASES_CODE");
        initDate();
    }

    /**
     * ��ӡ
     */
    public void onPrint(){
        //TIMEINTERVAL,STATIS;.jhw
        if (table.getRowCount() <= 0) {
            this.messageBox("û����Ҫ��ӡ������!");
            return;
        }
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        TParm tmp = this.getParmForTag("START_DATE;END_DATE;DISEASES_CODE");
        try {
            TParm data = new TParm();
            Timestamp START_DATE = (Timestamp)((TTextFormat)this.getComponent("START_DATE")).getValue();
            Timestamp END_DATE = (Timestamp)((TTextFormat)this.getComponent("END_DATE")).getValue();
            // ��ͷ����
            data.setData("TITLE", "TEXT", Operator.getHospitalCHNFullName() + "ָ��ʱ�ڵ����ַ���ͳ��");
            data.setData("TIMEINTERVAL", "TEXT", "ͳ������: "
                     + format.format(START_DATE)
                     + " �� "
                     + format.format(END_DATE));


            if(!tmp.getValue("DISEASES_CODE").equals("")){
                data.setData("STATIS", "TEXT", "���ַ���: " + ((TTextFormat)this.getComponent("DISEASES_CODE")).getText());
            }else{
                data.setData("STATIS", "TEXT", "���ַ���: " + "���в���");
            }

            // �������
            TParm parm = new TParm();
            TParm tableParm = table.getShowParmValue();
            // ��������е�Ԫ��
            //DISEASES_CODE;TOTAL_AMT;AVG_AMT;AVG_AMT_1
            for (int i = 0; i < table.getRowCount(); i++) {
                parm.addData("DISEASES_CODE", tableParm.getValue("DISEASES_CODE", i));
                parm.addData("TOTAL_AMT", tableParm.getValue("TOTAL_AMT", i));
                parm.addData("AVG_AMT", tableParm.getValue("AVG_AMT", i));
                parm.addData("AVG_AMT_1", tableParm.getValue("AVG_AMT_1", i));
            }
            // ������
            parm.setCount(parm.getCount("DISEASES_CODE"));
            parm.addData("SYSTEM", "COLUMNS", "DISEASES_CODE");
            parm.addData("SYSTEM", "COLUMNS", "TOTAL_AMT");
            parm.addData("SYSTEM", "COLUMNS", "AVG_AMT");
            parm.addData("SYSTEM", "COLUMNS", "AVG_AMT_1");
            // �����ŵ�������
            data.setData("TABLE", parm.getData());
            // ��β����
            data.setData("OPERATOR", "TEXT", "�Ʊ���: " + Operator.getName());
            openPrintDialog("%ROOT%\\config\\prt\\MRO\\MROCostStatistics.jhw",
                                        data);
        } catch (Exception e) {
            e.printStackTrace();
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
        ExportExcelUtil.getInstance().exportExcel(table, "ָ��ʱ�ڵ����ַ���ͳ��");

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
     * ���ش�λ�Ѳ�ѯSQL
     * @param data TParm
     * @return String
     */
    private String getOutPatientSQL(TParm data){
        //ʱ���ʽ��format
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sql = new StringBuilder();
        sql.append("  SELECT A.DISEASES_CODE, AVG(TOT_AMT) AS AVG_AMT ");
        sql.append("  FROM MRO_RECORD A, IBS_ORDD C ");
        sql.append("  WHERE A.CASE_NO = C.CASE_NO ");
        sql.append("  AND A.DISEASES_CODE IS NOT NULL ");
        try {
            sql.append("     AND A.IN_DATE BETWEEN TO_DATE ('");
            //��Ժʱ�乹��ͳ����ʼʱ��
            sql.append(format.format(format.parse(data.getValue("START_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sql.append("     AND TO_DATE ('");
            //��Ժʱ�乹��ͳ�ƽ���ʱ��
            sql.append(format.format(format.parse(data.getValue("END_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");

            //��Ժʱ�乹��ͳ����ʼʱ��
            sql.append("     AND A.OUT_DATE BETWEEN TO_DATE ('");
            sql.append(format.format(format.parse(data.getValue("START_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sql.append("     AND TO_DATE ('");
            //��Ժʱ�乹��ͳ�ƽ���ʱ��
            sql.append(format.format(format.parse(data.getValue("END_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");

        } catch( ParseException e) {
            e.printStackTrace();
        }
        sql.append(" GROUP BY A.DISEASES_CODE ");
        return sql.toString();
    }

    /**
     * ������Ҫ��SQL
     * @return String
     */
    private String getInpatientSQL(TParm data){
        //ʱ���ʽ��format
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sql = new StringBuilder();
        //DISEASES_CODE;TOTAL_AMT
        sql.append("  SELECT A.DISEASES_CODE, SUM(C.TOTAL_AMT) AS TOTAL_AMT, ");
        sql.append("  AVG(C.TOTAL_AMT) AS AVG_AMT ");
        sql.append("  FROM MRO_RECORD A, ADM_INP C ");
        sql.append("  WHERE A.CASE_NO = C.CASE_NO ");
        sql.append("  AND A.DISEASES_CODE IS NOT NULL ");
        if(!data.getValue("DISEASES_CODE").equals("")){
            sql.append("  AND A.DISEASES_CODE = '");
            sql.append(data.getValue("DISEASES_CODE"));
            sql.append("'");
        }
       try {
            sql.append("     AND A.IN_DATE BETWEEN TO_DATE ('");
            //��Ժʱ�乹��ͳ����ʼʱ��
            sql.append(format.format(format.parse(data.getValue("START_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sql.append("     AND TO_DATE ('");
            //��Ժʱ�乹��ͳ�ƽ���ʱ��
            sql.append(format.format(format.parse(data.getValue("END_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");

            //��Ժʱ�乹��ͳ����ʼʱ��
            sql.append("     AND A.OUT_DATE BETWEEN TO_DATE ('");
            sql.append(format.format(format.parse(data.getValue("START_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sql.append("     AND TO_DATE ('");
            //��Ժʱ�乹��ͳ�ƽ���ʱ��
            sql.append(format.format(format.parse(data.getValue("END_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sql.append(" GROUP BY A.DISEASES_CODE ");
        }catch(ParseException e){
            e.printStackTrace();
        }
        return sql.toString();
    }
}
