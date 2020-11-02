package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * <p> Title: �����¼ѡ�� </p>
 * 
 * <p> Description: �����¼ѡ��  </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 * @author wanglong 20130617
 * @version 1.0
 */
public class BILPatRecordChooseControl extends TControl {
	private static String TABLE = "TABLE";
	private TTable table;

	/**
	 * ��ʼ������
	 */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent(TABLE);
        TParm parm = (TParm) this.getParameter();
        if (parm.getData("ADM_TYPE") == null || parm.getData("MR_NO") == null) {
            messageBox("E0024");// ��ʼ������ʧ��
            return;
        }
        String mrNo = parm.getValue("MR_NO");
        String admType = parm.getValue("ADM_TYPE");
        setTableParameter(admType, table);
        TParm result = getAllMedRecord(admType, mrNo);
        if (result.getErrCode() < 0) {
            this.messageBox("��ѯ���ξ����¼ʧ�� " + result.getErrText());
            this.closeWindow();
        }
        table.setParmValue(result);
    }

    /**
     * ȡ�����ξ����¼
     * @param admType
     * @param mrNo
     * @return
     */
    public TParm getAllMedRecord(String admType, String mrNo) {
        TParm result = new TParm();
        String sql = "";
        if (admType.equals("O")) {
            sql = "SELECT * FROM REG_PATADM WHERE SEE_DR_FLG<>'N' AND MR_NO = '#' ORDER BY ADM_DATE,REG_DATE";
            sql = sql.replaceFirst("#", mrNo);
        } else if (admType.equals("H")) {
            sql = "SELECT * FROM HRM_PATADM WHERE MR_NO = '#' ORDER BY REPORT_DATE";
            sql = sql.replaceFirst("#", mrNo);
        } else if (admType.equals("I")) {
            sql = "SELECT * FROM ADM_INP WHERE MR_NO = '#' ORDER BY IN_DATE";
            sql = sql.replaceFirst("#", mrNo);
        }
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
    /**
     * ���ñ���title,parmMap������
     * @param admType
     * @param table
     */
    public void setTableParameter(String admType, TTable table) {
        if (admType.equals("O")) {
            table.setHeader("�Һ�����,150,TimeStamp,yyyy/MM/dd HH:mm:ss;��������,100;��ʼ����ʱ��,150,TimeStamp,yyyy/MM/dd HH:mm:ss;�������,100,DEPT_CODE;����ҽ��,100,VC_CODE");
            table.setColumnHorizontalAlignmentData("3,left;4,left");
            table.setParmMap("REG_DATE;ADM_DATE;SEEN_DR_TIME;REALDEPT_CODE;REALDR_CODE");
        } else if (admType.equals("H")) {
            table.setHeader("��������,150,TimeStamp,yyyy/MM/dd HH:mm:ss;��������,100,DEPT_CODE;��������,160,COMPANY_CODE;��ͬ����,140,CONTRACT_CODE;�ײ�����,140,PACKAGE_CODE");
            table.setColumnHorizontalAlignmentData("1left;2,left;3,left;4,left");
            table.setParmMap("REPORT_DATE;DEPT_CODE;COMPANY_CODE;CONTRACT_CODE;PACKAGE_CODE");
        } else if (admType.equals("I")) {
            table.setHeader("��Ժ����,150,TimeStamp,yyyy/MM/dd HH:mm:ss;��Ժ����,100,DEPT_CODE;��Ժ����,100,STATION_CODE;��Ժ����,150,TimeStamp,yyyy/MM/dd HH:mm:ss;��Ժ����,90,DEPT_CODE;��Ժ����,90,STATION_CODE");
            table.setColumnHorizontalAlignmentData("1,left;2,left;4,left;5,left");
            table.setParmMap("IN_DATE;IN_DEPT_CODE;IN_STATION_CODE;DS_DATE;DS_DEPT_CODE;DS_STATION_CODE");
        }
    }

    /**
     * TABLE˫���¼�
     */
    public void onTableDoubleCliecked() {
        if ((table.getShowCount() > 0) && (table.getSelectedRow() < 0)) {
            messageBox("��ѡ��һ����¼");
            return;
        }
        int row = table.getSelectedRow();
        TParm parm = table.getParmValue();
        this.setReturnValue(parm.getValue("CASE_NO", row));
        this.closeWindow();
    }
    
}
