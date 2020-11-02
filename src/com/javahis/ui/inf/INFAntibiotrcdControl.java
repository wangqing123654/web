package com.javahis.ui.inf;

import com.dongyang.control.TControl;
import jdo.inf.INFReportTool;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import java.sql.Timestamp;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: �����ز�����ʹ�÷�����</p>
 *
 * <p>Description: �����ز�����ʹ�÷�����</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFAntibiotrcdControl   extends TControl {

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
        Timestamp timestamp = SystemTool.getInstance().getDate();
        setValue("START_DATE",timestamp);
        setValue("END_DATE",timestamp);
        setValue("DEPT_CODE",Operator.getDept());
        setValue("STATION_CODE",Operator.getStation());
    }

    /**
     * ��ӡ�¼�
     */
    public void onQuery(){
        if(getValueString("START_DATE").length() == 0 ||
           getValueString("END_DATE").length() == 0){
            messageBox("�����뿪ʼ��������");
            return;
        }
        if(getValueString("START_DATE").compareTo(getValueString("END_DATE")) > 0){
            messageBox("����Ŀ�ʼ�������ڲ��Ϸ�");
            return;
        }
        TParm parm = new TParm();
        parm.setData("START_DATE",getValue("START_DATE"));
        parm.setData("END_DATE",getValue("END_DATE"));
        if(getValueString("DEPT_CODE").length() != 0)
            parm.setData("DEPT_CODE",getValue("DEPT_CODE"));
        if(getValueString("STATION_CODE").length() != 0)
            parm.setData("STATION_CODE",getValue("STATION_CODE"));
        if(getValueString("DR_CODE").length() != 0)
            parm.setData("VS_DR_CODE",getValue("DR_CODE"));
        //===========pangben modify 20110629 start
        if(null!=Operator.getRegion() && Operator.getRegion().length()>0){
            parm.setData("REGION_CODE",Operator.getRegion());
        }
        //===========pangben modify 20110629 stop
        parm = INFReportTool.getInstance().selectInfAntibiotrcd(parm);
        getTable("TABLE").removeRowAll();
        if(parm.getParm("TABLE").getCount("DEPT_CHN_DESC")<=0){
            messageBox("��������");
            return;
        }
        getTable("TABLE").setParmValue(parm.getParm("TABLE"));
    }
    /**
     * ��ӡ�¼�
     */
    public void onPrint(){
        if(getValueString("START_DATE").length() == 0 ||
           getValueString("END_DATE").length() == 0){
            messageBox("�����뿪ʼ��������");
            return;
        }
        if(getValueString("START_DATE").compareTo(getValueString("END_DATE")) > 0){
            messageBox("����Ŀ�ʼ�������ڲ��Ϸ�");
            return;
        }
        TParm parm = new TParm();
        parm.setData("START_DATE",getValue("START_DATE"));
        parm.setData("END_DATE",getValue("END_DATE"));
        if(getValueString("DEPT_CODE").length() != 0)
            parm.setData("DEPT_CODE",getValue("DEPT_CODE"));
        if(getValueString("STATION_CODE").length() != 0)
            parm.setData("STATION_CODE",getValue("STATION_CODE"));
        if(getValueString("DR_CODE").length() != 0)
            parm.setData("VS_DR_CODE",getValue("DR_CODE"));
        //===========pangben modify 20110629 start
       if(null!=Operator.getRegion() && Operator.getRegion().length()>0){
           parm.setData("REGION_CODE",Operator.getRegion());
       }
       //===========pangben modify 20110629 stop
        parm = INFReportTool.getInstance().selectInfAntibiotrcd(parm);
        getTable("TABLE").removeRowAll();
        if(parm.getParm("TABLE").getCount("DEPT_CHN_DESC")<=0){
            messageBox("û����Ҫ��ӡ������");
            return;
        }
        getTable("TABLE").setParmValue(parm.getParm("TABLE"));
        openPrintWindow("%ROOT%\\config\\prt\\inf\\InfAntibiotrcdReport.jhw",parm);
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
       ExportExcelUtil.getInstance().exportExcel(mainTable, "�����ز�����ʹ�÷�����");
   }


    /**
     * ��շ���
     */
    public void  onClear(){
        setValue("START_DATE","");
        setValue("END_DATE","");
        setValue("DEPT_CODE","");
        setValue("STATION_CODE","");
        setValue("DR_CODE","");
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
