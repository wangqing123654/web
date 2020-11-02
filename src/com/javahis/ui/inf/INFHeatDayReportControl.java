package com.javahis.ui.inf;

import jdo.inf.INFReportTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: �����ձ�</p>
 *
 * <p>Description: �����ձ�</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis </p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFHeatDayReportControl   extends TControl {

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
        setValue("EXAMINE_DATE",SystemTool.getInstance().getDate());
        setValue("STATION_CODE",Operator.getStation());
    }

    /**
     * ��ѯ�¼�
     */
    public void onQuery(){
        if(getValueString("EXAMINE_DATE").length() == 0 ||
           getValueString("STATION_CODE").length() == 0){
            messageBox("�����������ڼ�����");
            return;
        }
        TParm parm = new TParm();
        parm.setData("EXAMINE_DATE",getValueString("EXAMINE_DATE").substring(0,10).replace("-",""));
        parm.setData("STATION_CODE",getValueString("STATION_CODE"));
        parm = INFReportTool.getInstance().selectHeatDayReport(parm);
        getTable("TABLE").removeRowAll();
        if(parm.getParm("TABLE").getCount("STATION_DESC")<=0){
            messageBox("��������");
            return;
        }
        getTable("TABLE").setParmValue(parm.getParm("TABLE"));
    }

    /**
     * ��ӡ�¼�
     */
    public void onPrint(){
        if(getValueString("EXAMINE_DATE").length() == 0 ||
           getValueString("STATION_CODE").length() == 0){
            messageBox("�����������ڼ�����");
            return;
        }
        TParm parm = new TParm();
        parm.setData("EXAMINE_DATE",getValueString("EXAMINE_DATE").substring(0,10).replace("-",""));
        parm.setData("STATION_CODE",getValueString("STATION_CODE"));
        parm = INFReportTool.getInstance().selectHeatDayReport(parm);
        getTable("TABLE").removeRowAll();
        if(parm.getParm("TABLE").getCount("STATION_DESC")<=0){
            messageBox("��������");
            return;
        }
        getTable("TABLE").setParmValue(parm.getParm("TABLE"));
        openPrintWindow("%ROOT%\\config\\prt\\inf\\INFHeatDayReport.jhw",parm);
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
       ExportExcelUtil.getInstance().exportExcel(mainTable, "�����ձ�");
   }

    /**
     * ��շ���
     */
    public void  onClear(){
        setValue("EXAMINE_DATE","");
        setValue("STATION_CODE","");
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
