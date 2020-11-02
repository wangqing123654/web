package com.javahis.ui.inf;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import jdo.inf.INFReportTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;

/**
 * <p>Title: ��Ⱦ�����±���</p>
 *
 * <p>Description: ��Ⱦ�����±���</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFCaseMonReportControl  extends TControl {

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
    public void initUI(){
        setValue("DATE",SystemTool.getInstance().getDate());
    }

    /**
     * ��ѯ����
     */
    public void onQuery(){
        if(getValueString("DATE").length() == 0)
            return;
        TParm parm = new TParm();
        parm.setData("DATE", getValueString("DATE").substring(0, 7).replace("-", ""));
        //===========pangben modify 20110629 start
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            parm.setData("REGION_CODE", Operator.getRegion());
        }
        //===========pangben modify 20110629 stop
        parm = INFReportTool.getInstance().countInfCaseMonReport(parm);
        getTable("TABLE").removeRowAll();
        if(parm.getParm("INF_DEPT_COUNT").getCount("DEPT_CHN_DESC")<=0){
            messageBox("��������");
            return;
        }
        getTable("TABLE").setParmValue(parm.getParm("INF_DEPT_COUNT"));
    }
    /**
     * ��ӡ�¼�
     */
    public void onPrint(){
        if(getValueString("DATE").length() == 0)
            return;
        TParm parm = new TParm();
        parm.setData("DATE",getValueString("DATE").substring(0,7).replace("-",""));
        //===========pangben modify 20110629 start
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            parm.setData("REGION_CODE", Operator.getRegion());
        }
        //===========pangben modify 20110629 stop
        parm = INFReportTool.getInstance().countInfCaseMonReport(parm);
        getTable("TABLE").removeRowAll();
        if(parm.getParm("INF_DEPT_COUNT").getCount("DEPT_CHN_DESC")<=0){
            messageBox("û����Ҫ��ӡ������");
            return;
        }
        getTable("TABLE").setParmValue(parm.getParm("INF_DEPT_COUNT"));
        openPrintWindow("%ROOT%\\config\\prt\\inf\\CountInfCaseMonReport.jhw",parm);
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
       ExportExcelUtil.getInstance().exportExcel(mainTable, "��Ⱦ�����±���");
   }

    /**
     * ��շ���
     */
    public void  onClear(){
        setValue("DATE","");
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
