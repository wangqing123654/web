package com.javahis.ui.inf;

import com.dongyang.control.TControl;
import jdo.inf.INFReportTool;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;

/**
 * <p>Title: ҽԺ��Ⱦ���������ܱ�1</p>
 *
 * <p>Description: ҽԺ��Ⱦ���������ܱ�1</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFCaseForReportControl extends TControl {

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        initUI();
    }


    /**
     * ��ʼ������
     */
    private TParm parm;
    public void initUI(){
        setValue("INF_DATE",SystemTool.getInstance().getDate());
    }

    /**
     * ��ѯ����
     */
    public void onQuery(){
        if(getValueString("INF_DATE").length() == 0)
            return;
        parm = new TParm();
        parm.setData("INF_DATE",getValueString("INF_DATE").replace("-","").substring(0,6));
        //=============pangben modify 20110629 start
        if(null!=Operator.getRegion() && Operator.getRegion().length()>0){
            parm.setData("REGION_CODE",Operator.getRegion());
        }
        //=============pangben modify 20110629 stop
        parm = INFReportTool.getInstance().selestInfCaseForReport(parm);
        getTable("TABLE").removeRowAll();
        if(parm.getParm("TABLE").getCount("DEPT_DESC")<=0){
            messageBox("��������");
            return;
        }
        getTable("TABLE").setParmValue(parm.getParm("TABLE"));
    }

    /**
     * ��ӡ�¼�
     */
    public void onPrint(){
        if(getValueString("INF_DATE").length() == 0)
            return;
//        parm.setData("INF_DATE",getValueString("INF_DATE").replace("-","").substring(0,6));
//        parm = INFReportTool.getInstance().selestInfCaseForReport(parm);
//        getTable("TABLE").removeRowAll();
        if(parm.getParm("TABLE").getCount("DEPT_DESC")<=0){
            messageBox("û����Ҫ��ӡ������");
            return;
        }
        getTable("TABLE").setParmValue(parm.getParm("TABLE"));
        openPrintWindow("%ROOT%\\config\\prt\\inf\\INFCaseForReport.jhw",parm);
    }

    /**
     * ����Excel���
     */
    public void onExcel(){
       TTable mainTable = getTable("TABLE");
       if(mainTable.getRowCount() <= 0){
           messageBox("�޵�������");
           return;
       }
       ExportExcelUtil.getInstance().exportExcel(mainTable, "ҽԺ��Ⱦ���������ܱ�1");
   }

    /**
     * ��շ���
     */
    public void  onClear(){
        setValue("INF_DATE","");
        getTable("TABLE").removeRowAll();
    }
    /**
     * ȡ��Table�ؼ�
     * @param tableTag String
     * @return TTable
     */
    private TTable getTable(String tableTag){
        return ((TTable)getComponent(tableTag));
    }
}
