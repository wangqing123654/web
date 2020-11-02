package com.javahis.ui.adm;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.adm.ADMOutPrintTool;
import jdo.sys.SystemTool;

import com.dongyang.util.StringTool;

import java.sql.Timestamp;

import jdo.sys.Operator;

import com.dongyang.ui.TComboBox;
import com.javahis.util.ExportExcelUtil;

import jdo.sys.SYSRegionTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ADMOutPrintControl
    extends TControl {
    TTable TABLE;
    TParm DATA;
    String P_DATE="";//��¼��ѯ����
    String P_CTZ="ȫ��";//��¼��ѯ����ݱ�
    //===pangben modify 20110510 start
    TParm printData;// ��ӡ��������
   //===pangben modify 20110510 stop
    public void onInit(){
        super.onInit();
        TABLE = (TTable)this.getComponent("TABLE");
        onClear();
        //========pangben modify 20110510 start Ȩ�����
        setValue("REGION_CODE", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        printData=new TParm();//��ӡ��������
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
        double SUM=0.00;//�ܽ��
        TParm parm = new TParm();
        //=============pangben modify 20110510 start ����������
        if (null != this.getValueString("REGION_CODE") &&
            this.getValueString("REGION_CODE").length() != 0)
            parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop
        parm.setData("DATE_S",StringTool.getString((Timestamp)this.getValue("DATE_S"),"yyyyMMddHHmmss"));
        parm.setData("DATE_E",StringTool.getString((Timestamp)this.getValue("DATE_E"),"yyyyMMddHHmmss"));
        P_CTZ="ȫ��";
        if(this.getValueString("CTZ").length()>0){
            parm.setData("CTZ1_CODE",this.getValueString("CTZ"));
            TComboBox box =(TComboBox)this.getComponent("CTZ");
            P_CTZ = box.getSelectedName();
        }
        DATA = ADMOutPrintTool.getInstance().selectOutHosp(parm);
        if(DATA.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        //=========pangben modify 20110510
       for(int i=0;i<DATA.getCount("MR_NO");i++){
           SUM += DATA.getDouble("TOTAL_AMT", i);
       }
       //��ʾ�ܷ�����
       //==========chenxi 20120306  start
       DATA.addData("REGION_CHN_ABN", "�ϼ�");  
       //===========chenxiu 20120306  stop
       DATA.addData("TOTAL_AMT", StringTool.round(SUM, 2));
       //������ʾ������Ա---xiongwg20150211  start
//       String sql ="SELECT CASE_NO,CASHIER_CODE FROM BIL_IBS_RECPM WHERE AR_AMT>=0 AND RESET_RECEIPT_NO IS NULL WHERE ";
//       TParm parmIn = new TParm(TJDODBTool.getInstance().select(sql));
//       for(int i=0;i <= DATA.getCount("MR_NO"); i++){
//           for(int a = 0; a <= parmIn.getCount("CASE_NO"); a++){
//        	   String caseNo = DATA.getValue("CASE_NO",i);
//        	   String caseNoIn = parm.getValue("CASE_NO",a);
//        	   if(caseNo.equals(caseNoIn)){
//        		   DATA.setData("INS_USER", i, parmIn.getValue("CASHIER_CODE",a));
//        	   }
//           }
//       }
       //������ʾ������Ա---xiongwg20150211  end
       for (int i = 0; i <= DATA.getCount("MR_NO"); i++) {
           //=======chenxi modify 20120306 start
           printData.addData("REGION_CHN_ABN",DATA.getValue("REGION_CHN_ABN",i));  

           //======chenxi modify 20120306 stop
           printData.addData("MR_NO",DATA.getValue("MR_NO",i));
           printData.addData("IPD_NO",DATA.getValue("IPD_NO",i));
           printData.addData("DEPT_CHN_DESC",DATA.getValue("DEPT_CHN_DESC",i));
           //=======pangben modify 20110510 start
           //������
           printData.addData("STATION_DESC",DATA.getValue("STATION_DESC",i));
           //=======pangben modify 20110510 stop
           printData.addData("CTZ_DESC",DATA.getValue("CTZ_DESC",i));
           printData.addData("PAT_NAME",DATA.getValue("PAT_NAME",i));
           printData.addData("CHN_DESC",DATA.getValue("CHN_DESC",i));
           printData.addData("INS",DATA.getValue("INS",i));
           printData.addData("TOTAL_AMT",DATA.getValue("TOTAL_AMT",i));
           printData.addData("INS_USER",DATA.getValue("INS_USER",i));       
           printData.addData("DS_DATE",StringTool.getString(DATA.getTimestamp("DS_DATE",i),"yyyy/MM/dd"));
           printData.addData("INS_DATE",StringTool.getString(DATA.getTimestamp("INS_DATE",i),"yyyy/MM/dd"));
       }
       TABLE.setParmValue(DATA);
       P_DATE = StringTool.getString((Timestamp)this.getValue("DATE_S"),
                                     "yyyy/MM/dd HH:mm:ss") +
                " �� " +
                StringTool.getString((Timestamp)this.getValue("DATE_E"), "yyyy/MM/dd HH:mm:ss");
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
        if(DATA.getCount()<1){
            this.messageBox("û�д�ӡ������");
            return ;
        }
        printData.setCount(DATA.getCount("MR_NO")+1);
        //=======chenxi modify 20120306 start
        printData.addData("SYSTEM", "COLUMNS", "REGION_CHN_ABN");// CHENXI
        //=======chenxi modify 20120306 stop
        printData.addData("SYSTEM","COLUMNS","MR_NO");
        printData.addData("SYSTEM","COLUMNS","IPD_NO");
        printData.addData("SYSTEM","COLUMNS","DEPT_CHN_DESC");
        //=======pangben modify 20110510 start
        printData.addData("SYSTEM", "COLUMNS", "STATION_DESC");
         //=======pangben modify 20110510 stop
        printData.addData("SYSTEM","COLUMNS","CTZ_DESC");
        printData.addData("SYSTEM","COLUMNS","PAT_NAME");
        printData.addData("SYSTEM","COLUMNS","CHN_DESC");
        printData.addData("SYSTEM","COLUMNS","INS");
        printData.addData("SYSTEM","COLUMNS","TOTAL_AMT");
        printData.addData("SYSTEM","COLUMNS","INS_USER");
        printData.addData("SYSTEM","COLUMNS","DS_DATE");
        printData.addData("SYSTEM","COLUMNS","INS_DATE");

        TParm Basic = new TParm();
        Basic.setData("P_USER", Operator.getName());
        Basic.setData("P_DATE",
                      StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyy��MM��dd��"));
        TParm printParm = new TParm();
        printParm.setData("T1",printData.getData());
        //========chenxi modify 20120306 start
        String region = DATA.getRow(0).getValue("REGION_CHN_ABN");
        //========chenxi modify 20120306 stop
        printParm.setData("TITLE", "TEXT",
                          (this.getValue("REGION_CODE") != null &&
                           !this.getValue("REGION_CODE").equals("") ? region :
                           "����ҽԺ") + "��Ժͳ�Ʊ���");
        //========pangben modify 20110510 stop
        printParm.setData("DATE", "TEXT", P_DATE);
        printParm.setData("CTZ","TEXT",P_CTZ);
        printParm.setData("Basic", Basic.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMOutPrint.jhw",printParm);
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
