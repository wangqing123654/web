package com.javahis.ui.adm;

import com.dongyang.control.*;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.adm.ADMInPrintTool;
import jdo.sys.Operator;
import com.dongyang.ui.TComboBox;
import com.javahis.util.ExportExcelUtil;

import jdo.sys.SYSRegionTool;

/**
 * <p>Title: ��Ժͳ�Ʊ���</p>
 *
 * <p>Description: ��Ժͳ�Ʊ���</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-18
 * @version 1.0
 */
public class ADMInPrintControl
    extends TControl {
    TTable TABLE;
    TParm DATA;
    String P_DATE="";//��¼��ѯ����
    String P_CTZ="ȫ��";//��¼��ѯ����ݱ�
    public void onInit(){
        super.onInit();
        TABLE = (TTable)this.getComponent("TABLE");
        onClear();
        //========pangben modify 20110510 start Ȩ�����
        setValue("REGION_CODE", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110510 stop

    }
    /**
     * ��ѯ
     */
    public void onQuery(){
        if(this.getValue("DATE_S")==null){
            this.messageBox_("��ѡ����ʼ���ڣ�");
            this.grabFocus("DATE_S");
            return;
        }
        if(this.getValue("DATE_E")==null){
            this.messageBox_("��ѡ���ֹ���ڣ�");
            this.grabFocus("DATE_E");
            return;
        }
        TParm parm = new TParm();
        //=============pangben modify 20110510 start ����������
        if (null != this.getValueString("REGION_CODE") &&
            this.getValueString("REGION_CODE").length() != 0)
            parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop

        parm.setData("DATE_S",
                     StringTool.getString((Timestamp)this.getValue("DATE_S"),
                                          "yyyyMMddHHmmss"));
        parm.setData("DATE_E",
                     StringTool.getString((Timestamp)this.getValue("DATE_E"),
                                          "yyyyMMddHHmmss"));
        P_CTZ = "ȫ��";
        if (this.getValueString("CTZ").length() > 0) {
            parm.setData("CTZ1_CODE",this.getValueString("CTZ"));
            TComboBox box =(TComboBox)this.getComponent("CTZ");
            P_CTZ = box.getSelectedName();
        }
        DATA = ADMInPrintTool.getInstance().selectInHosp(parm);
        if(DATA.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        TABLE.setParmValue(DATA);
        P_DATE = StringTool.getString((Timestamp)this.getValue("DATE_S"),"yyyy/MM/dd HH:mm:ss") +
            " �� "+StringTool.getString((Timestamp)this.getValue("DATE_E"),"yyyy/MM/dd HH:mm:ss");
    }
    /**
     * ���
     */
    public void onClear(){
        //��ȡ��ǰ����
        String date = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");
        //��ʼ��ͳ������
        this.setValue("DATE_S",StringTool.getTimestamp(date,"yyyyMMdd"));
        this.setValue("DATE_E",StringTool.getTimestamp(date+"235959","yyyyMMddHHmmss"));
        this.clearValue("CTZ");
        TABLE.removeRowAll();
        //=======pangben modify 20110510 start
        this.setValue("REGION_CODE",Operator.getRegion());
    }
    /**
     * ��ӡ
     */
    public void onPrint(){
        TParm printData = new TParm();
        int count = 0;
        for(int i=0;i<DATA.getCount("MR_NO");i++){
            //=======chenxi modify 20120306 start
            printData.addData("REGION_CHN_ABN",DATA.getValue("REGION_CHN_ABN",i));
            //=======chenxi modify 20120306 stop
            printData.addData("MR_NO",DATA.getValue("MR_NO",i));
            printData.addData("IPD_NO",DATA.getValue("IPD_NO",i));
            printData.addData("DEPT_CHN_DESC",DATA.getValue("DEPT_CHN_DESC",i));
            printData.addData("STATION_DESC",DATA.getValue("STATION_DESC",i));
            printData.addData("CTZ_DESC",DATA.getValue("CTZ_DESC",i));
            printData.addData("PAT_NAME",DATA.getValue("PAT_NAME",i));
            printData.addData("CHN_DESC",DATA.getValue("CHN_DESC",i));
//            printData.addData("ADDRESS",DATA.getValue("ADDRESS",i));
//            printData.addData("CONTACTS_NAME",DATA.getValue("CONTACTS_NAME",i));
//            printData.addData("CELL_PHONE",DATA.getValue("CELL_PHONE",i));
            printData.addData("IN_DATE",StringTool.getString(DATA.getTimestamp("IN_DATE",i),"yyyy/MM/dd"));
            printData.addData("ADM_CLERK",DATA.getValue("ADM_CLERK",i));
            printData.addData("OPD_DR_CODE",DATA.getValue("OPD_DR_CODE",i));
            count++;
        }
        printData.setCount(count);
        //=======chenxi modify 20120306 start
        printData.addData("SYSTEM","COLUMNS","REGION_CHN_ABN");
        //=======chenxi modify 20120306 stop
        printData.addData("SYSTEM","COLUMNS","MR_NO");
        printData.addData("SYSTEM","COLUMNS","IPD_NO");
        printData.addData("SYSTEM","COLUMNS","DEPT_CHN_DESC");
        printData.addData("SYSTEM","COLUMNS","STATION_DESC");
        printData.addData("SYSTEM","COLUMNS","CTZ_DESC");
        printData.addData("SYSTEM","COLUMNS","PAT_NAME");
        printData.addData("SYSTEM","COLUMNS","CHN_DESC");
        //yanmm 20180529
//        printData.addData("SYSTEM","COLUMNS","ADDRESS");
//        printData.addData("SYSTEM","COLUMNS","CONTACTS_NAME");
//        printData.addData("SYSTEM","COLUMNS","CELL_PHONE");
        printData.addData("SYSTEM","COLUMNS","IN_DATE");
        printData.addData("SYSTEM","COLUMNS","ADM_CLERK");
        printData.addData("SYSTEM","COLUMNS","OPD_DR_CODE");

       // TParm Basic = new TParm();
       

        TParm printParm = new TParm();
        printParm.setData("T1",printData.getData());
        //========chenxi modify 20120306 start
       String region= DATA.getRow(0).getValue("REGION_CHN_ABN");
       //========chenxi modify 20120306 stop
       printParm.setData("TITLE", "TEXT",
                    ( this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals("")?region:"����ҽԺ") + "��Ժͳ�Ʊ���");
       //========pangben modify 20110510 stop
        printParm.setData("DATE","TEXT",P_DATE);
        printParm.setData("CTZ","TEXT",P_CTZ);
        //printParm.setData("Basic", Basic.getData());
        printParm.setData("P_USER","TEXT",Operator.getName());
        printParm.setData("P_DATE","TEXT",StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyy��MM��dd��"));
        System.out.println("printParm::::::"+printParm);
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMInPrint.jhw",printParm);
    }
    
    /**
     * ���Excel add by huangjw 20150408
     */
    public void onExcel() {
    	if(TABLE.getRowCount()<=0){
    		this.messageBox("û�л������");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(TABLE, "��Ժͳ�Ʊ���");
    }
}
