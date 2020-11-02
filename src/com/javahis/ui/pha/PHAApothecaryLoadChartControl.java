package com.javahis.ui.pha;

import com.dongyang.control.TControl;
import com.javahis.util.JavaHisDebug;
import com.dongyang.data.TParm;
import jdo.pha.PhaStatisticsTool;
import jdo.sys.Operator;
import com.dongyang.util.StringTool;
import java.text.DecimalFormat;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import com.dongyang.ui.TComboBox;
import java.util.Map;
import java.util.HashMap;
import jdo.sys.SYSRegionTool;

/**
 *
 * <p>Title: ����ҩ��ҩʦ����ǩ������ͳ�Ʊ�������
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JAVAHIS
 *
 * @author ZangJH 2009.01.20
 * @version 1.0
 */


public class PHAApothecaryLoadChartControl
    extends TControl {

    //ͳ������
    private String type = "";


    public void onInit() {
        super.onInit();
        myInit();
    }

    public void myInit() {
        //��ʼ������
        this.callFunction("UI|mainTable|setLockColumns", "0,1,2,3,4,5,6,7");
        ((TComboBox)this.getComponent("REGION_CODE")).setValue(Operator.getRegion());
        ((TComboBox)this.getComponent("EXEC_DEPT_CODE")).setValue(Operator.getDept());
        //========pangben modify 20110421 start Ȩ�����
         this.callFunction("UI|REGION_CODE|setEnabled",SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110422 stop

    }


    //��ò�ѯ/��ӡ��Ҫ��ʾ��table�ϵ�����
    public TParm getQueryDate() {
        TParm inParm = new TParm();
        //��ò�ѯ����
        //�ż����ѯ�������ȫԺ�Ͳ���ADM_TYPE����
        if (this.getValueString("RadioO").equals("Y")) {
            //��������--O
            inParm.setData("ADM_TYPE", "O");
        }
        else if (this.getValueString("RadioE").equals("Y")) {
            //��������--E
            inParm.setData("ADM_TYPE", "E");
        }

        //�����(00:00:00)/��ʱ��(23:59:59)
        Timestamp fromDate = (Timestamp) this.getValue("START_DATE");
        inParm.setData("START_DATE", fromDate);

        String tempEnd=this.getValue("END_DATE").toString();
        Timestamp toDate=StringTool.getTimestamp(tempEnd.substring(0,4)+tempEnd.substring(5,7)+tempEnd.substring(8,10)+"235959","yyyyMMddHHmmss");
        inParm.setData("END_DATE", toDate);

        String drugRoom = this.getValueString("EXEC_DEPT_CODE");
        if (!drugRoom.equals("")) {
            inParm.setData("EXEC_DEPT_CODE", drugRoom);
        }
        //================pangben modify 20110406 start ��������ѯ����
        if(this.getValueString("REGION_CODE").length()!=0)
            inParm.setData("REGION_CODE",this.getValueString("REGION_CODE"));
        //================pangben modify 20110406 stop
        //���ú�̨��ѯ��Ӧ������
        TParm result = PhaStatisticsTool.getInstance().getPhaStatisticsMainDate(
            inParm, type);
        countDctNum(result);
        return result;
    }



    private void countDctNum(TParm result){
        String admType = "";
        if(getValueString("RadioO").equals("Y"))
            admType = " AND ADM_TYPE = 'O'";
        else if (getValueString("RadioE").equals("Y"))
            admType = " AND ADM_TYPE = 'E'";
        String region = "";
        if(getValueString("REGION_CODE").length() != 0)
            region = " AND REGION_CODE = '"+getValueString("REGION_CODE")+"'";
        String data = "";
        //��˲�ѯ
        if (type.equals("���")) {
            data = "CHECK";
        } //��ҩ��ѯ
        else if (type.equals("��ҩ")) {
            data = "DOSAGE";
        } //��ҩ��ѯ
        else if (type.equals("��ҩ")) {
            data = "DISPENSE";
        } //��ҩ��ѯ
        else if (type.equals("��ҩ")) {
            data = "RETN";
        }
        for(int i = 0;i < result.getCount();i++){
            String SQL =
                    " SELECT SUM(A.TAKE_DAYS) TAKE_DAYS "+
                    " FROM (SELECT DISTINCT B.TAKE_DAYS,B.RX_NO " +
                    "       FROM OPD_ORDER B" +
                    "       WHERE TO_CHAR(B.PHA_"+data+"_DATE,'YYYY/MM/DD') = '"+result.getValue("QDATE",i)+"'"+
                    "       AND   EXEC_DEPT_CODE = '"+result.getValue("DEPT",i)+"'"+
                    "       AND   PHA_"+data+"_CODE = '"+result.getValue("PERSON",i)+"'"+
                    "       AND   DECOCT_DATE IS NOT NULL"+
                    "       " + admType + region+ "   ) A";
            TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
            result.setDataN("DCT_RXNUM",i,parm.getValue("TAKE_DAYS",0));
        }
    }



    public void onQuery() {

        //��ò�ѯ������
        type = this.getValueString("type");
        if (type.equals("")) {
            this.messageBox("������ͳ������");
            return;
        }

        TParm tableDate = getQueryDate();
        if (tableDate.getCount() <= 0) {
            onClear();
            this.messageBox("�ò�ѯ�����������ݣ�");
            return;
        }
        //�ܽ��
        double totalAmt = 0.0;
        //===========pangben modify 20110426 start
        int sumCount=0;//������
        int sumDctRxnum=0;//��ҩ�˴�
        int sumDctCasenum=0;//��ҩ����
        //===========pangben modify 20110426 stop
        //int
        int count = tableDate.getCount();
        //ѭ���ۼ�
        for (int i = 0; i < count; i++) {
            double temp = tableDate.getDouble("CHARGE", i);
            //===========pangben modify 20110426 start
            sumCount +=tableDate.getInt("COUNT", i);
            sumDctRxnum+=tableDate.getInt("DCT_RXNUM", i);
            sumDctCasenum+=tableDate.getInt("DCT_CASENUM", i);
            //===========pangben modify 20110426 stop
            totalAmt += temp;
        }
        this.setValue("TOTFEE", totalAmt);
        //===========pangben modify 20110426 start
        tableDate.setData("QDATE", count, "");
        tableDate.setData("REGION_CHN_DESC", count, "�ܼ�:");
        tableDate.setData("DEPT", count, "");
        tableDate.setData("PERSON", count, "");
        tableDate.setData("COUNT", count, sumCount);
        tableDate.setData("DCT_RXNUM", count, sumDctRxnum);
        tableDate.setData("DCT_CASENUM", count, sumDctCasenum);
        tableDate.setData("CHARGE", count, totalAmt);
         //===========pangben modify 20110426 start
        //����table�ϵ�����
        this.callFunction("UI|mainTable|setParmValue", tableDate);

    }


    /**
     * ��ն���
     */
    public void onClear() {

        this.clearValue("drugRoom;type;TOTFEE");
        //���table
        this.callFunction("UI|mainTable|removeRowAll");
        //ѡ��ȫԺ��
        this.callFunction("UI|RadioALL|setSelected", true);
        //=========pangben modify 20110422 start
        this.setValue("REGION_CODE",Operator.getRegion());
         //=========pangben modify 20110422 stop
    }

    /**
     * ��ӡ
     */
    public void onPrint() {
        //��table�ϻ�ô�ӡ������
        TParm printData = new TParm();
        //������װ��parm
        TParm result = (TParm)this.callFunction("UI|mainTable|getShowParmValue");

        if (result.getCount() <= 0) {
            this.messageBox("�޴�ӡ���ݣ�");
            return;
        }
        //������Ҫ��ӡ�����ݣ���ʽ����С...��
        printData = arrangeData(result);

        //��õ�ǰʱ��
        Timestamp nowTime = TJDODBTool.getInstance().getDBTime();
        //==============pangben modify 20110419 start
        String region = this.getValueString("REGION_CODE");
        //��������
        String prtName = (region.equals("") || region == null ? "����ҽԺ" :
                         printData.getValue("REGION_CHN_DESC", 0) )+ type +
                         "����������ͳ�Ʊ�";
        //==============pangben modify 20110419 stop
        //������
        String proName = "��PHAApothecaryLoadChartControl��";
        //��ӡʱ��
        String prtTime = StringTool.getString(nowTime,"yyyy/MM/dd HH:mm:ss");
        //ҽԺ���
//        String HospName = Manager.getOrganization().getHospitalCHNShortName(Operator.getRegion());

        Timestamp startDate = (Timestamp) this.getValue("START_DATE");
        Timestamp endDate = (Timestamp) this.getValue("END_DATE");
        //ͳ������
        String staSection = "ͳ������: " + StringTool.getString(startDate,
            "yyyy/MM/dd") + " �� " + StringTool.getString(endDate,
            "yyyy/MM/dd");
        //�Ʊ�ʱ��
        String prtDate = "�Ʊ�ʱ��:" + StringTool.getString(nowTime,
            "yyyy/MM/dd HH:mm:ss");
        TParm parm = new TParm();
        parm.setData("prtName","TEXT", prtName);
        parm.setData("proName", "TEXT", proName);
        parm.setData("prtTime", "TEXT", prtTime);
        parm.setData("staSection", "TEXT", staSection);
        parm.setData("prtDate", "TEXT", prtDate);
        parm.setData("TABLE", printData.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\pha\\PHALoadChart.jhw", parm);


    }

    public TParm arrangeData(TParm parm) {
        //����������
        TParm reDate = new TParm();
        DecimalFormat df = new DecimalFormat("#############0.00");
        int rowCount = parm.getCount();

        //ѭ��װ��������
        for (int i = 0; i < rowCount; i++) {
            //=============pangben modify 20110418 start
            reDate.addData("REGION_CHN_DESC", parm.getData("REGION_CHN_DESC", i));
            //=============pangben modify 20110418 stop
            reDate.addData("DATE", parm.getData("QDATE", i));
            reDate.addData("DEPT", parm.getData("DEPT", i));
            reDate.addData("PERSON", parm.getData("PERSON", i));
            reDate.addData("COUNT", parm.getData("COUNT", i));
            double charge = (double) parm.getDouble("CHARGE", i);
            reDate.addData("CHARGE", df.format(StringTool.round(charge, 2)));
           // System.out.println("charge:"+df.format(StringTool.round(charge, 2)));
            reDate.addData("DCT_CASENUM", parm.getData("DCT_CASENUM", i));
            reDate.addData("DCT_RXNUM", parm.getData("DCT_RXNUM", i));
        }
        reDate.setCount(rowCount);
        //=============pangben modify 20110418 start
        reDate.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
        //=============pangben modify 20110418 stop
        reDate.addData("SYSTEM", "COLUMNS", "DATE");
        reDate.addData("SYSTEM", "COLUMNS", "DEPT");
        reDate.addData("SYSTEM", "COLUMNS", "PERSON");
        reDate.addData("SYSTEM", "COLUMNS", "COUNT");
        reDate.addData("SYSTEM", "COLUMNS", "CHARGE");
        reDate.addData("SYSTEM", "COLUMNS", "DCT_CASENUM");
        reDate.addData("SYSTEM", "COLUMNS", "DCT_RXNUM");
        return reDate;
    }

//-----------------------------����EXCEL�ķ���---Start---------------------------
    /**
     * ����EXCEL�ķ���
     */

    public void onExcel() {
        //�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
        TTable mainTable = (TTable) callFunction("UI|mainTable|getThis");
        ExportExcelUtil.getInstance().exportExcel(mainTable, "�ż���ҩʦ����������ͳ�Ʊ���");
    }

//-----------------------------����EXCEL�ķ���---end-----------------------------


    //��������
    public static void main(String[] args) {

        //JavaHisDebug.initClient();
        //JavaHisDebug.initServer();
//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("pha\\PHAApothecaryLoadChart.x");
    }

}
