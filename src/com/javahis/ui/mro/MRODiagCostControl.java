package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import java.util.Calendar;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: ���Ӳ���,����ξ�����ͳ�Ʊ���</p>
 *
 * <p>Description: ���Ӳ���,����ξ�����ͳ�Ʋ�ѯ����</p>
 *
 * <p>Copyright: Copyright (c) 2011 </p>
 *
 * <p>Company: JavaHis </p>
 * com.javahis.ui.mro.MRODiagCostControl
 * @author ZhenQin
 * @version 4.0
 */
public class MRODiagCostControl
    extends TControl {

    /**
     * ��ʾ���ݵ�Table
     */
    private TTable table = null;

    /**
     * ���췽��
     */
    public MRODiagCostControl() {
        super();
    }

    /**
     * ��ʼ��
     */
    public void onInit(){
        super.onInit();
        table = (TTable)this.getComponent("tTable_1");
        initDate();
        //ICD10��������
        callFunction("UI|ICD_CODE|setPopupMenuParameter", "ICD10",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        //����ICD10�����ش�ֵ
        callFunction("UI|ICD_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "ICD10Return");

    }

    /**
     * ���ICD10ѡ�񷵻����ݴ���
     * @param tag String
     * @param obj Object
     */
    public void ICD10Return(String tag, Object obj) {
        if (obj == null){
            return;
        }
        TParm returnParm = (TParm) obj;
        this.setValue("ICD_CODE",returnParm.getValue("ICD_CODE"));
        this.setValue("ICD_DESC",returnParm.getValue("ICD_CHN_DESC"));
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
        TParm param = this.getParmForTag("START_DATE;END_DATE;ICD_CODE;ICD_DESC");
        param.setData("REGION_CODE", Operator.getRegion());
        String sql = getSQL(param);
        //ȡ�ù����SQL,����ѯ���ݿ�
        TParm result = new TParm(
              TJDODBTool.getInstance().select(sql)
        );
        //��ѯ�쳣,����û�в�ѯ�����ݿ�,��ʾ
        if(result.getErrCode() < 0 || result.getCount("ICD_CODE") <= 0){
            this.messageBox("û�в�ѯ������!");
            this.err(result.getErrName() + "    " + result.getErrText());
            table.removeRowAll();
            return ;
        }
        //=========pangben modify 20110704 start
        double sumAMT=0.00;//���ܼ۸�
        int sumCount=0;//��������
        for (int i = 0; i < result.getCount("ICD_CODE"); i++) {
            result.addData("AVG_AMT",
                           StringTool.round(result.getDouble("AR_AMT", i) /
                                            result.getDouble("COUNT_PAT", i), 3));
//            sumAMT+=StringTool.round(result.getDouble("AR_AMT", i) /
//                                            result.getDouble("COUNT_PAT", i), 3);
            sumAMT+=result.getDouble("AR_AMT", i);  //modify by huangtt 20141212
            sumCount+=result.getInt("COUNT_PAT", i);
        }
        result.addData("COUNT_PAT",sumCount);
//        result.addData("AVG_AMT",sumAMT);
        result.addData("AVG_AMT",StringTool.round(sumAMT/sumCount,3));  //modify by huangtt 20141212
        result.addData("ICD_CODE","�ϼ�:");
        //=========pangben modify 20110704 start
        table.setParmValue(result);

    }

    /**
     * ��ӡ����
     */
    public void onPrint(){

    }

    /**
     * �������
     */
    public void onClear(){
        this.clearValue("START_DATE;END_DATE;ICD_CODE;ICD_DESC");
        initDate();

    }

    /**
     * ������Xls
     */
    public void onExport(){
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "����ξ�����ͳ��");

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
     * �����ѯSQL
     * @param data TParm ��ѯ����
     * @return String ���ز�ѯSQL
     */
    private String getSQL(TParm data){
        //ʱ���ʽ��format
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT   ICD_CODE, ICD_TYPE, ADM_TYPE,ICD_CHN_DESC, SUM (AR_AMT) AS AR_AMT, ");
        sql.append("  COUNT (CASE_NO) AS COUNT_PAT ");
        sql.append(" FROM (SELECT   A.CASE_NO, B.ADM_TYPE, B.ICD_CODE, B.ICD_TYPE, C.ICD_CHN_DESC,  ");

        sql.append("   SUM (A.AR_AMT) AS AR_AMT ");
        sql.append("  FROM OPD_ORDER A, OPD_DIAGREC B, sys_diagnosis C ");
        sql.append("  WHERE A.CASE_NO = B.CASE_NO ");
        sql.append("    AND B.ICD_CODE = C.ICD_CODE ");
        sql.append("    AND B.ICD_TYPE = C.ICD_TYPE ");
        sql.append("    AND B.MAIN_DIAG_FLG = 'Y' ");
        sql.append("    AND A.REGION_CODE = '");
        sql.append(data.getValue("REGION_CODE"));
        sql.append("'  ");
        if(!data.getValue("ICD_CODE").equals("") &&
              !data.getValue("ICD_DESC").equals("")){
            sql.append("    AND B.ICD_CODE = '");
            sql.append(data.getValue("ICD_CODE"));
            sql.append("'  ");
        }
        try {
            sql.append("  AND A.BILL_DATE BETWEEN TO_DATE ('");
            //��Ժʱ�乹��ͳ����ʼʱ��
            sql.append(format.format(format.parse(data.getValue("START_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sql.append("  AND TO_DATE ('");
            //��Ժʱ�乹��ͳ�ƽ���ʱ��
            sql.append(format.format(format.parse(data.getValue("END_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");

        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        sql.append("  GROUP BY A.CASE_NO, B.ADM_TYPE, B.ICD_CODE, B.ICD_TYPE, C.ICD_CHN_DESC) ");
        sql.append(" GROUP BY ICD_CODE, ICD_TYPE, ADM_TYPE,ICD_CHN_DESC    ");


        return sql.toString();

    }
}
