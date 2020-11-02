package com.javahis.ui.bil;

import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.dongyang.control.TControl;
import java.text.DecimalFormat;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ִ�п��Һ��㱨��
 * </p>
 *
 * <p>
 * Description: ִ�п��Һ��㱨��
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author wangl 2009.10.07
 * @version 1.0
 */
public class BILExeDeptWordControl extends TControl {
    public void onInit() {
        super.onInit();
        initPage();
    }

    String[] chargName = {"CHARGE01", "CHARGE02", "CHARGE03", "CHARGE04",
                         "CHARGE05", "CHARGE06", "CHARGE07", "CHARGE08",
                         "CHARGE09",
                         "CHARGE10", "CHARGE11", "CHARGE12", "CHARGE13",
                         "CHARGE14",
                         "CHARGE15", "CHARGE16", "CHARGE17", "CHARGE18",
                         //===zhangp 20120312 start
                         "CHARGE19","CHARGE20"};
    					//===zhangp 20120312 end
    private TParm parmName; //��������
    private TParm parmCode; //���ô���
    /**
     * ��ʼ������
     */
    public void initPage() {
    	//add by huangtt 20141202 start
    	 String title= (String) this.getParameter();
         this.setTitle(title);
       //add by huangtt 20141202 end
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        setValue("S_DATE", yesterday);
        setValue("E_DATE", SystemTool.getInstance().getDate());
        setValue("DEPT_CODE", "");
        this.callFunction("UI|Table|removeRowAll");
        String sql = SYSSQL.getBillRecpparm(); //��÷��ô���
        sql += " WHERE ADM_TYPE='I'";
        parmCode = new TParm(TJDODBTool.getInstance().select(sql));
        if (parmCode.getErrCode() < 0 || parmCode.getCount() <= 0) {
            this.messageBox("���÷����ֵ�������");
            return;
        }
        //��÷�������
        sql =
                "SELECT ID ,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE'";
        parmName = new TParm(TJDODBTool.getInstance().select(sql));

        TTable table = (TTable)this.getComponent("Table");
        table.setParmValue(getHeard());
    }
    /**
     * ��ӱ�ͷ
     * @return TParm
     */
    private TParm getHeard() {
        TParm heardParm = new TParm();
        heardParm.addData("DEPT_CHN_DESC", "�ɱ�����");
        heardParm.addData("TOT_AMT", "�ϼƽ��");
        for (int i = 0; i < chargName.length; i++) {
            heardParm.addData(chargName[i],
                              getChargeName(parmName,
                                            parmCode.getValue(chargName[i], 0)));
        }
        heardParm.setCount(1);
        return heardParm;
    }
    /**
     * ��÷�������
     * @param parmName TParm
     * @param chargeCode String
     * @return String
     */
    private String getChargeName(TParm parmName, String chargeCode) {
        for (int i = 0; i < parmName.getCount(); i++) {
            if (parmName.getValue("ID", i).equals(chargeCode)) {
                return parmName.getValue("CHN_DESC", i);
            }
        }
        return "";
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
        TTable table = (TTable)this.getComponent("Table");
        int row = table.getRowCount();
        if (row < 1) {
            this.messageBox_("�Ȳ�ѯ����!");
            return;
        }
        String startTime = StringTool.getString(TypeTool
                                                .getTimestamp(getValue("S_DATE")),
                                                "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool
                                              .getTimestamp(getValue("E_DATE")),
                                              "yyyyMMdd");
        String sysDate = StringTool.getString(SystemTool.getInstance()
                                              .getDate(), "yyyy/MM/dd HH:mm:ss");
        TParm printData = this.getPrintDate(startTime, endTime);
        String sDate = StringTool.getString(TypeTool
                                            .getTimestamp(getValue("S_DATE")),
                                            "yyyy/MM/dd");
        String eDate = StringTool.getString(TypeTool
                                            .getTimestamp(getValue("E_DATE")),
                                            "yyyy/MM/dd");
        TParm parm = new TParm();
        parm.setData("TITLE", "TEXT", "ִ�п���������㱨��");
        parm.setData("DATE", "TEXT", sDate + " �� " + eDate);
        parm.setData("E_DATE", eDate);
        parm.setData("OPT_USER", "TEXT", Operator.getName());
        parm.setData("printDate", "TEXT", sysDate);
        parm.setData("T1", printData.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\IBS\\BILExeDeptFee.jhw",
                             parm);

    }

    /**
     * �����ӡ����
     *
     * @param startTime
     *            String
     * @param endTime
     *            String
     * @return TParm
     */
    private TParm getPrintDate(String startTime, String endTime) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm selParm = new TParm();
        String deptWhere = "";
        if (getValue("DEPT_CODE") != null) {
            if (getValue("DEPT_CODE").toString().length() != 0)
                deptWhere = " AND C.COST_CENTER_CODE = '" + getValue("DEPT_CODE")
                            + "'  ";
        }
        String regionWhere = "";
        if (!"".equals(Operator.getRegion()))
            regionWhere = " AND C.REGION_CODE = '" + Operator.getRegion()
                          + "' ";
        String sql =
//                "   SELECT A.EXE_DEPT_CODE, SUM (A.TOT_AMT)  AS TOT_AMT, A.REXP_CODE, "+
//                "          C.COST_CENTER_CHN_DESC AS DEPT_CHN_DESC "+
//                "     FROM IBS_ORDD A, SYS_DEPT B,SYS_COST_CENTER C " +
//                "    WHERE A.BILL_DATE BETWEEN TO_DATE ('" + startTime + "000000" +"', 'yyyyMMddhh24miss') " +
//                "      AND TO_DATE ('"+ endTime + "235959"+ "', 'yyyyMMddhh24miss') "+
//                deptWhere +
//                regionWhere +
//                "      AND A.EXE_DEPT_CODE = B.DEPT_CODE " +
//                "      AND B.COST_CENTER_CODE = C.COST_CENTER_CODE "+
//                " GROUP BY A.EXE_DEPT_CODE, A.REXP_CODE, C.COST_CENTER_CHN_DESC " +
//                " ORDER BY A.EXE_DEPT_CODE ";
        "SELECT   A.EXE_DEPT_CODE, SUM (A.TOT_AMT) AS TOT_AMT, A.REXP_CODE," +
        " C.COST_CENTER_CHN_DESC AS DEPT_CHN_DESC" +
        " FROM IBS_ORDD A, SYS_COST_CENTER C" +
        " WHERE A.BILL_DATE BETWEEN TO_DATE ('"+startTime+"000000', 'yyyyMMddhh24miss')" +
        " AND TO_DATE ('"+ endTime + "235959', 'yyyyMMddhh24miss')" +
        	deptWhere +
        	regionWhere +
        " AND A.EXE_DEPT_CODE = C.COST_CENTER_CODE" +
        " GROUP BY A.EXE_DEPT_CODE, A.REXP_CODE, C.COST_CENTER_CHN_DESC" +
        " ORDER BY A.EXE_DEPT_CODE";
//            System.out.println("�ɱ�����sql"+sql);
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (selParm.getCount("EXE_DEPT_CODE") < 1) {
            // ��������
            this.messageBox("E0008");
            this.initPage();
            return selParm;
        }
        BILIbsForCostCenterPrint endData = new BILIbsForCostCenterPrint();
        TParm endParm = endData.getValue(selParm);
//        System.out.println("endParm:::"+endParm);
        TParm resultParm = getHeard(); //��ͷ
        int count = resultParm.getCount();
        for (int i = 0; i < endParm.getCount(); i++) {
            resultParm.setRowData(count, endParm, i);
            count++;
        }
        resultParm.setCount(count);
        resultParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        resultParm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
        for (int i = 0; i < chargName.length; i++) {
            resultParm.addData("SYSTEM", "COLUMNS", chargName[i]);
        }
        this.callFunction("UI|Table|setParmValue", resultParm);
        return resultParm;
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        String startTime = StringTool.getString(TypeTool
                                                .getTimestamp(getValue("S_DATE")),
                                                "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool
                                              .getTimestamp(getValue("E_DATE")),
                                              "yyyyMMdd");
        TParm printData = this.getPrintDate(startTime, endTime);
    }

    /**
     * ���Excel
     */
    public void onExport() {

        // �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
        TTable table = (TTable) callFunction("UI|Table|getThis");
        if (table.getRowCount() > 0)
            ExportExcelUtil.getInstance().exportExcel(table, "ִ�п���������㱨��");
    }

    /**
     * ���
     */
    public void onClear() {
    	TTable table = (TTable) this.getComponent("Table");
        table.removeRowAll();
        initPage();
        
        this.clearValue("DEPT_CODE");
    }

    /**
     * ����combo�¼�
     */
    public void onDEPT() {
        this.clearValue("STATION_CODE");
        this.callFunction("UI|STATION_CODE|onQuery");
    }

}
