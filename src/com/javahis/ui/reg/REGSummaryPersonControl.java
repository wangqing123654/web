package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import jdo.sta.STAWorkLogTool;
import com.dongyang.data.TParm;
import java.text.DecimalFormat;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.reg.PatAdmTool;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 *
 * <p>Title: �Һ�����ͳ�Ʊ���</p>
 *
 * <p>Description: �Һ�����ͳ�Ʊ���</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.06.16
 * @version 1.0
 */
public class REGSummaryPersonControl
    extends TControl {
    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        initPage();
    }

    /**
     * ��ʼ������
     */
    public void initPage() {

        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        setValue("S_TIME", yesterday);
        setValue("E_TIME", SystemTool.getInstance().getDate());
    }

    /**
     * ��ӡ
     */
    public void onPrint() {
        print();
    }

    /**
     * ���ñ����ӡԤ������
     */
    private void print() {

        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_TIME")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_TIME")), "yyyyMMdd");
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd HH:mm:ss");
        TParm printData = this.getPrintDate(startTime, endTime);
//        System.out.println("��ӡ����==>:"+printData);
        String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_TIME")), "yyyy/MM/dd");
        String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_TIME")), "yyyy/MM/dd");
        TParm parm = new TParm();
        parm.setData("Title", "�Һ�����ͳ�Ʊ�");
        parm.setData("S_TIME", sDate);
        parm.setData("E_TIME", eDate);
        parm.setData("OPT_USER", Operator.getName());
        parm.setData("OPT_DATE", sysDate);
        parm.setData("summarytable", printData.getData());
//        this.openPrintWindow("%ROOT%\\config\\prt\\reg\\REGSummary.jhw", parm);
        this.openPrintWindow("%ROOT%\\config\\prt\\REGSummary.jhw", parm);

    }
    /**
     * �����ӡ����
     * @param startTime String
     * @param endTime String
     * @return TParm
     */
    private TParm getPrintDate(String startTime, String endTime) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm oFeeDate = new TParm();
        oFeeDate = PatAdmTool.getInstance().selSummaryPersonO(startTime,
            endTime);
        int oFeeDateCount = oFeeDate.getCount();
        TParm eFeeDate = new TParm();
        eFeeDate = PatAdmTool.getInstance().selSummaryPersonE(startTime,
            endTime);
        int eFeeDateCount = eFeeDate.getCount();
        TParm retrunFeeDate = new TParm();
        retrunFeeDate = PatAdmTool.getInstance().selSummaryPersonReturn(
            startTime, endTime);
        int retrunFeeDateCount = retrunFeeDate.getCount();
        //��ȡ����1��2��3������ ��Ϊ�������߲�����
        TParm DeptList = STAWorkLogTool.getInstance().selectDeptList();
        //��ȡ����STA_DAILY_02�����ݣ����ļ�����Ϊ���������ݣ�
        TParm printData = new TParm(); //��ӡ����
        for (int i = 0; i < DeptList.getCount(); i++) {
            String d_LEVEL = DeptList.getValue("DEPT_LEVEL", i); //���ŵȼ�
            String d_CODE = DeptList.getValue("DEPT_CODE", i); //�м䲿��CODE
            String d_DESC = DeptList.getValue("DEPT_DESC", i); //�м䲿������
            int subIndex = 0; //��¼���ݿ��Ҽ���Ҫ��ȡCODE�ĳ���
            //�����һ������ code����Ϊ1
            if (d_LEVEL.equals("1")) {
                subIndex = 1;
            }
            //����Ƕ������� code����Ϊ3
            else if (d_LEVEL.equals("2")) {
                subIndex = 3;
                d_DESC = " " + d_DESC; //����ǰ���ո�
            }
            //������������� code����Ϊ5
            else if (d_LEVEL.equals("3")) {
                subIndex = 5;
                d_DESC = "  " + d_DESC; //����ǰ���ո�
            }
            /*������� �����ۼ��Ӳ��ŵ���ֵ ��ʼֵΪ-1����������ۼӵ�����Ϊnull��ô����ʼ��Ϊ-1��
                      �Ǹ�����ñ��������ֶε�ʱ��Ͳ���null*/
            double regFeeO = 0;
            double clinicFeeO = 0;
            int countPersonO = 0;
            double regFeeE = 0;
            double clinicFeeE = 0;
            int countPersonE = 0;
            int countPersonReturn = 0;
            double returnFee = 0;
            int totPerson = 0;
            double totFee = 0;

            //ѭ���������� ȡ�����������Ĳ��ŵ����ݽ����ۼ�
            for (int j = 0; j < oFeeDateCount; j++) {
                //�������id��ȡ��ָ�����Ⱥ� �������ѭ���еĲ���CODE��ô�������ѭ�����Ӳ��ţ��ͽ����ۼ�
//                System.out.println("��ȡ������"+subIndex);
//                System.out.println("��ȡ����CODE��"+reData.getValue("DEPT_CODE",j).substring(0,subIndex));
//                System.out.println("1��2�����ţ�"+d_CODE);
                if (oFeeDate.getValue("DEPT_CODE", j).substring(0, subIndex).
                    equals(d_CODE)) {
//                    System.out.println("ѭ������:"+i);
                    regFeeO += oFeeDate.getDouble("REG_FEE", j);
                    clinicFeeO += oFeeDate.getDouble("CLINIC_FEE", j);
                    countPersonO += oFeeDate.getInt("COUNT", j);
                }
            }
            for (int k = 0; k < eFeeDateCount; k++) {
                if (eFeeDate.getValue("DEPT_CODE", k).substring(0, subIndex).
                    equals(d_CODE)) {
                    regFeeE += eFeeDate.getDouble("REG_FEE", k);
                    clinicFeeE += eFeeDate.getDouble("CLINIC_FEE", k);
                    countPersonE += eFeeDate.getInt("COUNT", k);
                }
            }
//            System.out.println("��" + i + "����" + subIndex);
            for (int h = 0; h < retrunFeeDateCount; h++) {
                if (retrunFeeDate.getValue("DEPT_CODE", h).substring(0,
                    subIndex).equals(d_CODE)) {
                    returnFee += retrunFeeDate.getDouble("RETURN_FEE", h);
                    countPersonReturn += retrunFeeDate.getInt("COUNT", h);
                }
            }
            totPerson = countPersonO + countPersonE + countPersonReturn;
            totFee = regFeeO + clinicFeeO + regFeeE + clinicFeeE + returnFee;
            printData.addData("DEPT_DESC", d_DESC);
            printData.addData("REG_FEE_O", regFeeO == 0 ? "" : df.format(regFeeO));
            printData.addData("CLINIC_FEE_O", clinicFeeO == 0 ? "" : df.format(clinicFeeO));
            printData.addData("COUNT_PERSON_O",
                              countPersonO == 0 ? "" : countPersonO);
            printData.addData("REG_FEE_E", regFeeE == 0 ? "" : df.format(regFeeE));
            printData.addData("CLINIC_FEE_E", clinicFeeE == 0 ? "" : df.format(clinicFeeE));
            printData.addData("COUNT_PERSON_E",
                              countPersonE == 0 ? "" : countPersonE);
            printData.addData("RETURN_FEE", returnFee == 0 ? "" : df.format(returnFee));
            printData.addData("TOT_PERSON", totPerson == 0 ? "" : totPerson);
            printData.addData("TOT_FEE", totFee == 0 ? "" : df.format(totFee));
        }
//        System.out.println(""+printData);
        printData.setCount(DeptList.getCount());
        printData.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
        printData.addData("SYSTEM", "COLUMNS", "REG_FEE_O");
        printData.addData("SYSTEM", "COLUMNS", "CLINIC_FEE_O");
        printData.addData("SYSTEM", "COLUMNS", "COUNT_PERSON_O");
        printData.addData("SYSTEM", "COLUMNS", "REG_FEE_E");
        printData.addData("SYSTEM", "COLUMNS", "CLINIC_FEE_E");
        printData.addData("SYSTEM", "COLUMNS", "COUNT_PERSON_E");
        printData.addData("SYSTEM", "COLUMNS", "RETURN_FEE");
        printData.addData("SYSTEM", "COLUMNS", "TOT_PERSON");
        printData.addData("SYSTEM", "COLUMNS", "TOT_FEE");
        return printData;
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_TIME")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_TIME")), "yyyyMMdd");
        TParm printData = this.getPrintDate(startTime, endTime);
        if (printData.getErrCode() < 0) {
            messageBox(printData.getErrText());
            return;
        }
        this.callFunction("UI|Table|setParmValue", printData);

    }
    /**
     * ���Excel
     */
    public void onExcel() {

        //�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
        TTable table = (TTable) callFunction("UI|Table|getThis");
        ExportExcelUtil.getInstance().exportExcel(table,"�Һ�����ͳ�Ʊ���");
    }

    /**
     * ���
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();

    }


}
