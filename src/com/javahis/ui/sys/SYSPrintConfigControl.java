package com.javahis.ui.sys;

import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import javax.print.PrintService;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * �ն����ô�ӡ��
 * @author lixiang
 *
 */
public class SYSPrintConfigControl extends TControl{
	/**
	 * �ն˴��ӡ���б�
	 */
	private TComboBox printlist;
	/**
	 * �����б�;
	 */
	private TComboBox reportlist;
	
	
	private TTable printerTable;
	/**
	 * �ն�IP
	 */
	private TTextField termIP;
	/**
	 * 
	 */
	int selectRow = -1;
	
	//�м�¼;
	private TParm data;

    public SYSPrintConfigControl() {
    }
    /**
     * ��ʼ��
     */
    public void onInit() {
        //
        printlist = (TComboBox) getComponent("PRINT_LIST");
        //REPORT_LIST
        reportlist= (TComboBox) getComponent("REPORT_LIST");
        //
        termIP=(TTextField) getComponent("TERM_IP");
        //
        printerTable=(TTable) getComponent("PRINTER_TABLE");
        
        //�ն�IP (��װ���������)
        termIP.setValue(Operator.getIP());
        
        initPrintList();
      
        initTable();
        
        callFunction("UI|PRINTER_TABLE|addEventListener", "PRINTER_TABLE->"
                + TTableEvent.CLICKED, this, "onTABLEClicked");
        
    }
    /**
     * 
     * @param row
     */
    public void onTABLEClicked(int row) {
    	//this.messageBox("rowrow==="+row);
    	
    	if (row < 0) {
            return;
        }
    	TParm data = (TParm) callFunction("UI|PRINTER_TABLE|getParmValue");
    	//this.messageBox("==data=="+data);
    	//��ֵ;
    	termIP.setValue(data.getValue("PRINTER_IP", row));
    	reportlist.setValue(data.getValue("REPORT_ID", row));
    	printlist.setValue(data.getValue("PRINTER_CHN_DESC", row));
    	selectRow = row;
    	/**setValueForParm(
                "SEQ;UNIT_CODE;UNIT_CHN_DESC;PY1;PY2;UNIT_ENG_DESC;DESCRIPTION;MEDI_FLG;PACKAGE_FLG;DISPOSE_FLG;OTHER_FLG",
                data, row);**/

        
    	
    }
    
    
    /**
     * ��ʼ�������б�
     */

    private void initTable(){
    	String sql="SELECT a.PRINTER_IP,a.PRINTER_CHN_DESC,b.REPORT_CHN_DESC,a.OPT_USER,a.OPT_DATE,a.OPT_TERM,a.REPORT_ID";
    	sql+=" FROM SYS_PRINTER_LIST a,SYS_PRINTER_REPORT b";
    	sql+=" WHERE a.REPORT_ID(+)=b.REPORT_ID";
    	sql+=" AND a.PRINTER_IP='"+termIP.getValue().trim()+"'";
    	//System.out.println("--------------------SQL===="+sql);
    	TParm result = new TParm( this.getDBTool().select(sql));
    	//this.messageBox("result"+result.getCount());
    	printerTable.setParmValue(result);
    	selectRow=-1;
    	
    }

    /**
     * ��ʼ����ӡ���б�
     */
    private void initPrintList() {
        List list = new ArrayList();
        PrintService[] services = PrinterJob.lookupPrintServices();
        for (int i = 0; i < services.length; i++) {
            list.add(services[i].getName().trim());
        }
        String s[] = (String[]) list.toArray(new String[] {});
        printlist.setData(s);
        PrintService ps = PrinterJob.getPrinterJob().getPrintService();

        String defaultPrint = ps.getName();
        printlist.setSelectedID(defaultPrint);
    }
    
    /**
     * ����
     */
    public void onSave(){
    	//У����;
    	if(termIP.getValue().trim().equals("")){
    		this.messageBox("����д�ն˻�IP��");
    		return;
    	}
    	
    	if(reportlist.getSelectedID().equals("")){
    		this.messageBox("��ѡ�񱨸棡");
    		return;	
    	}
    	
    	if(printlist.getSelectedID().equals("")){
    		this.messageBox("��ѡ���ӡ����");
    		return;	
    	}
   	
    	if(selectRow==-1){
    		//this.messageBox("����");
    		onInsert();
    	}else{
    		//this.messageBox("����");
    		onUpdate();
    	}
    }
    
    /**
     * ����
     */
    private void onInsert(){
    	//�ж��Ƿ�����ͬ��������
    	//�У�����
    	 String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
         "yyyy/MM/dd HH:mm:ss");
      	//�Ƿ����
    	 
    	String sql="INSERT INTO SYS_PRINTER_LIST (PRINTER_IP,PRINTER_CHN_DESC,REPORT_ID,OPT_USER,OPT_DATE,OPT_TERM)";
    	sql+=" VALUES(";
    	sql+="'"+termIP.getValue().trim()+"',";
    	sql+="'"+printlist.getSelectedID()+"',";
    	sql+="'"+reportlist.getSelectedID()+"',";
    	sql+="'"+Operator.getID()+"',";
    	sql += "TO_DATE('" + sysDate +
        "','YYYY/MM/DD HH24:MI:SS') ,";
    	sql+="'"+Operator.getIP()+"'";
    	sql+=")";
    	    	
    	TParm parm = new TParm(this.getDBTool().update(sql));
    	//parm.getErrText()
    	if (parm.getErrCode() < 0) {
            this.messageBox("����ʧ�ܣ�");
            return;
            
    	}else{
    		this.messageBox("����ɹ���");
    		initTable();
    		onClear();
    		
    	}  	
    	
    }
    
    /**
     * ����
     */
    private void onUpdate(){
    	int row=selectRow;
    	//this.messageBox("==row=="+row);
    	TParm data = (TParm) callFunction("UI|PRINTER_TABLE|getParmValue");
    	//���ѡ��ֵΪ��ǰֵ;
    	String cTermIP=data.getValue("PRINTER_IP", row);
    	String creportID=data.getValue("REPORT_ID", row);
    	String printerChnDesc=data.getValue("PRINTER_CHN_DESC", row);   	
    	String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
        "yyyy/MM/dd HH:mm:ss");
    	
    	String sql="UPDATE SYS_PRINTER_LIST SET PRINTER_IP='"+termIP.getValue().trim()+"'," ;
    		   sql+="PRINTER_CHN_DESC='"+printlist.getSelectedID()+"',";
    		   sql+="REPORT_ID='"+reportlist.getSelectedID()+"',";
    		   sql+="OPT_DATE=TO_DATE('" + sysDate +
    			        "','YYYY/MM/DD HH24:MI:SS')";
    		   sql+=" WHERE PRINTER_IP='"+cTermIP+"'";
    		   sql+=" AND REPORT_ID='"+creportID+"'";
    		   sql+=" AND PRINTER_CHN_DESC='"+printerChnDesc+"'";
    		   //System.out.println("===update sql==="+sql);
    		   
    	TParm parm = new TParm(this.getDBTool().update(sql));
    	if (parm.getErrCode() < 0) {
            this.messageBox("�޸�ʧ�ܣ�");
            return;
            
    	}else{
    		this.messageBox("�޸ĳɹ���");
    		initTable();
    		onClear();
    	}
    	
    	
    	
    }
    
    /**
     * ɾ����¼
     */
    public void onDelete(){
    	
    	 if(selectRow<0){
    		 this.messageBox("����ѡ���¼?");
    		 return;
    	 }
    	 
    	 TParm data = (TParm) callFunction("UI|PRINTER_TABLE|getParmValue");
    	 
    	//this.messageBox("selectRow==="+selectRow);
     	//���ѡ��ֵΪ��ǰֵ;
     	String cTermIP=data.getValue("PRINTER_IP", selectRow);
     	String creportID=data.getValue("REPORT_ID", selectRow);
     	String printerChnDesc=data.getValue("PRINTER_CHN_DESC", selectRow);
    	
    	 if (this.messageBox("��ʾ��Ϣ", "ȷ��ɾ��?", 2) == 0) {
    		 String sql="DELETE FROM SYS_PRINTER_LIST";
    		 sql+=" WHERE PRINTER_IP='"+cTermIP+"'";
  		     sql+=" AND REPORT_ID='"+creportID+"'";
  		     sql+=" AND PRINTER_CHN_DESC='"+printerChnDesc+"'";
  		   
    		 TParm parm = new TParm(this.getDBTool().update(sql));
    		 if (parm.getErrCode() < 0) {
    	            this.messageBox("ɾ��ʧ�ܣ�");
    	            return;
    	            
    	     }else{
    	    		this.messageBox("ɾ���ɹ���");
    	    		initTable();
    	    		onClear();
    	     }
    	 }   	
    }
    /**
     * ���
     */
    public void onClear(){
    	//initPrintList();
    	reportlist.setSelectedID("");
    	initTable();
    	
    }
    
    /**
     * ��ѯ����
     */
    public void onQuery(){
    	initTable();
    }
    
    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    
}
