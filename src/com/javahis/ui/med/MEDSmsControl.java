package com.javahis.ui.med;

import java.awt.Color;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.med.MedSmsTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

public class MEDSmsControl extends TControl {
	
	/**
	 * TABLE
	 */
	private static String TABLE = "TABLE";
	
	// ��¼���ѡ������
    int selectedRowIndex = -1;
    
	TTable table ;
	
	private TParm data;
	
	public void onInit() {
		super.onInit();
		// add by wangqing 20171229 TControl.getValue(tag)������bug
		this
        .clearValue("STATION_CODE;DEPT_CODE;BEGIN_TIME;END_TIME;MR_NO;HANDLE_SUGGEST;SMS_STATE;SMS_CODE");
		table=(TTable)this.getComponent(TABLE); 
		callFunction("UI|Table|addEventListener",
                "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        
		/**�ж� 1 ����ҽ��/ 2 ��ʿ */
		String role = isRole( Operator.getRole() );
		
		 // ��ʼ������ʱ��
        // ��������
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_TIME",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("BEGIN_TIME",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        
		this.setValue("DEPT_CODE", Operator.getDept());
		/**
		if(role != null) {
			if(role.equals("1")){
				this.setValue("DEPT_CODE", Operator.getDept());
				//this.setValue("STATION_CODE", Operator.getStation());
				
				
				callFunction("UI|DEPT_CODE|setEnabled", new Object[] {
		                Boolean.valueOf(false)
		        });
				callFunction("UI|STATION_CODE|setEnabled", new Object[] {
		                Boolean.valueOf(false)
		        })
			
			}else if(role.equals("2")){
				//this.setValue("STATION_CODE", Operator.getStation());
				this.setValue("DEPT_CODE", Operator.getDept());
				
				callFunction("UI|STATION_CODE|setEnabled", new Object[] {
		                Boolean.valueOf(false)
		        });
			}
		}*/
		onQuery();
	}
	
	public TTable getTable(String tableName){
		return  (TTable)this.getComponent(tableName);
	}
	
	public void onQueryNO(){
		String mrNo = getValueString("MR_NO");
		if (mrNo.length() > 0) {
			mrNo = PatTool.getInstance().checkMrno(mrNo);
			setValue("MR_NO", mrNo);
			this.onQuery();
		}
	}
	
	public void onTableClicked(int row) {
        callFunction("UI|DELETE|setEnabled", true);
        if (row < 0) {
            return;
        }
        else {
            setValueForParm("SMS_CODE;MR_NO;HANDLE_SUGGEST",
                            data, row);
            selectedRowIndex  = row;
      
            this.setValue("STATION_CODE", data.getValue("STATION_CODE", row));
            this.setValue("SMS_STATE",data.getValue("STATE", row));
            /**
            callFunction("UI|DEPT_CODE|setEnabled", new Object[] {
                         Boolean.valueOf(false)
            });
            callFunction("UI|STATION_CODE|setEnabled", new Object[] {
                         Boolean.valueOf(false)
            });*/
            return;
        }
    }
	
	
	public void onQuery(){
				
		TParm selectCondition = getParmForTag("DEPT_CODE;SMS_STATE;STATION_CODE;MR_NO", true);
		selectCondition.setData("BEGIN_TIME", getValue("BEGIN_TIME"));
		selectCondition.setData("END_TIME", getValue("END_TIME"));
		data = MedSmsTool.getInstance().onQuery(selectCondition); 
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }else {
           callFunction("UI|Table|setParmValue", new Object[] {
                        data
           });
           for(int i = 0 ; i < data.getCount() ; i++ ){
        	   TParm p = data.getRow(i);
        	   long time = getDiffTime(p.getValue("HANDLE_TIME"));
        	   //System.out.println("time=========:"+time);
        	   if(!p.getValue("STATE").equals("9")){
	        	   if( time > 0 && time < 31 ){
	        		   /**����ɫ*/
	        		   this.getTable(TABLE).setRowTextColor(i, new Color(0,128,255));
	        	   }
	        	   if( time > 30 && time < 41 ){
	        		   
	        		   /**��ɫ*/
	        		   this.getTable(TABLE).setRowTextColor(i, new Color(0,0,255));
	        	   }
	        	   if( time > 41  ){
	        		   
	        		   /**��ɫ*/
	        		   this.getTable(TABLE).setRowTextColor(i, new Color(255,0,0));
	        	   }
        	   }else{
        		   this.getTable(TABLE).setRowTextColor(i, new Color(0,0,0));
        	   }
           }
           return;
       }
        
       
	}
	
	/**
	 * ����¼�
	 */
	public void onClear(){
		this
        .clearValue("STATION_CODE;DEPT_CODE;BEGIN_TIME;END_TIME;MR_NO;HANDLE_SUGGEST;SMS_STATE;SMS_CODE");
		selectedRowIndex = -1;
		//callFunction("UI|DELETE|setEnabled", false);
		this.getTable("TABLE").clearSelection(); // ���TABLEѡ��״̬
		onInit();
	}
	
	/**
	 * ���Ŵ���
	 */
	public void onSave(){ 
		TParm parm = getParmForTag("HANDLE_SUGGEST;SMS_CODE");
		if(parm.getValue("SMS_CODE") == null || parm.getValue("SMS_CODE").equals("")){
			this.messageBox("��ѡ��Σ��ֵ��¼");
			return ;
		}
		
		if(parm.getValue("HANDLE_SUGGEST") == null || parm.getValue("HANDLE_SUGGEST").equals("")){
			this.messageBox("����д�������");
			return ;
		}
		
		TParm result = MedSmsTool.getInstance().updateMedSms(parm);
	    
        
        if (result.getErrCode() < 0) {
            this.messageBox("����", result.getErrText(), -1);
            return;
        }else{
        	this.messageBox("Σ��ֵ������ɣ�");
        	this.getTable("TABLE").setSelectedRow(selectedRowIndex);
        }
        onClear();
        onQuery();
       
	}
	
	public String isRole(String roleId){
		/**1: ҽ�� 2:��ʿ*/
		if("ODO".equals(roleId) || "ODI".equals(roleId) || "OIDR".equals(roleId) ){
			return "1";
		}
		if("NWO".equals(roleId) || "NWE".equals(roleId) || "NBW".equals(roleId) ||
			"HREG".equals(roleId) || "NWH".equals(roleId) || "NM".equals(roleId) || 
			"NWICU".equals(roleId)){
			return "2";
		}
		return null;
	}
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/**
	 * ��ʱ��֮�����
	 * @param medSms
	 * @return
	 */ 
	private long getDiffTime(String sendTime) {
		
		//String systemTime = DateUtil.getNowTime(TIME_FORMAT); 
		String systemTime = StringTool.getString(SystemTool.getInstance().getDate(), TIME_FORMAT);
		Date begin = null;
		Date end = null;
		try {
			end = sdf.parse(systemTime);
			begin = sdf.parse(sendTime);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/**��**/
		long between=(end.getTime()-begin.getTime())/1000;
		
		/**����**/
		long minute=between/60;
		return minute;
	} 

	/**
	 * ���� add by wangqing 20171116
	 */
	public void onExport(){ 
		onExportExcel();
	}
	
	/**
	 * ��������  add by wangqing 20171116
	 */
	public void onExportPrt(){	
		TParm data = new TParm();
		data.setData("PRINT_USER", "TEXT", Operator.getName());
		data.setData("PRINT_TIME", "TEXT", StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss"));
		data.setData("BEGIN_TIME", "TEXT", StringTool.getString((Timestamp) this.getValue("BEGIN_TIME"), "yyyy/MM/dd HH:mm:ss"));
		data.setData("END_TIME", "TEXT", StringTool.getString((Timestamp) this.getValue("END_TIME"), "yyyy/MM/dd HH:mm:ss"));
		if(table==null){
			return;
		}
		TParm tableParm = table.getShowParmValue();
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("û������");
			return;
		}
		TParm parm = new TParm();
		int count = 0;
		// ADM_TYPE;MR_NO;PATIENT_NAME;TESTITEM_CHN_DESC;
		// TEST_VALUE;CRTCLLWLMT;STATE;DEPT_CODE;BILLING_DOCTORS;
		// NOTIFY_DOCTORS_TIME;DIRECTOR_DR_CODE;NOTIFY_DIRECTOR_DR_TIME;
		// COMPETENT_CODE;NOTIFY_COMPETENT_TIME;DEAN_CODE;NOTIFY_DEAN_TIME;HANDLE_USER;HANDLE_TIME;SMS_CODE;HANDLE_OPINION
		for(int i=0; i<table.getRowCount(); i++){
			parm.addData("ADM_TYPE", tableParm.getValue("ADM_TYPE",i));// �ż�ס��
			parm.addData("MR_NO", tableParm.getValue("MR_NO",i));// ������
			parm.addData("PATIENT_NAME", tableParm.getValue("PATIENT_NAME",i));// ��������
			parm.addData("TESTITEM_CHN_DESC", tableParm.getValue("TESTITEM_CHN_DESC",i));// �����
			parm.addData("TEST_VALUE", tableParm.getValue("TEST_VALUE",i));// ���ֵ			
			parm.addData("CRTCLLWLMT", tableParm.getValue("CRTCLLWLMT",i));// Σ��ֵ
			parm.addData("STATE", tableParm.getValue("STATE",i));// ״̬
			parm.addData("DEPT_CODE", tableParm.getValue("DEPT_CODE",i));// ����
			parm.addData("BILLING_DOCTORS", tableParm.getValue("BILLING_DOCTORS",i));// ����ҽʦ
			parm.addData("NOTIFY_DOCTORS_TIME", tableParm.getValue("NOTIFY_DOCTORS_TIME",i));// ֪ͨ����ҽʦʱ��
			parm.addData("DIRECTOR_DR_CODE", tableParm.getValue("DIRECTOR_DR_CODE",i));// ������		
			parm.addData("NOTIFY_DIRECTOR_DR_TIME", tableParm.getValue("NOTIFY_DIRECTOR_DR_TIME",i));// ֪ͨ������ʱ��
			parm.addData("COMPETENT_CODE", tableParm.getValue("COMPETENT_CODE",i));// ҽ������
			parm.addData("NOTIFY_COMPETENT_TIME", tableParm.getValue("NOTIFY_COMPETENT_TIME",i));// ֪ͨҽ������ʱ��
			parm.addData("DEAN_CODE", tableParm.getValue("DEAN_CODE",i));// ����Ժ��
			parm.addData("NOTIFY_DEAN_TIME", tableParm.getValue("NOTIFY_DEAN_TIME",i));// ֪ͨ����Ժ��ʱ��
			parm.addData("HANDLE_USER", tableParm.getValue("HANDLE_USER",i));// �����û�
			parm.addData("HANDLE_TIME", tableParm.getValue("HANDLE_TIME",i));// ����ʱ��
			count++;
		}
		parm.setCount(count);
		parm.addData("SYSTEM", "COLUMNS", "ADM_TYPE");// �ż�ס��
		parm.addData("SYSTEM", "COLUMNS", "MR_NO");// ������
		parm.addData("SYSTEM", "COLUMNS", "PATIENT_NAME");// ��������
		parm.addData("SYSTEM", "COLUMNS", "TESTITEM_CHN_DESC");// �����
		parm.addData("SYSTEM", "COLUMNS", "TEST_VALUE");// ���ֵ	
		parm.addData("SYSTEM", "COLUMNS", "CRTCLLWLMT");// Σ��ֵ
		parm.addData("SYSTEM", "COLUMNS", "STATE");// ״̬
		parm.addData("SYSTEM", "COLUMNS", "DEPT_CODE");// ����
		parm.addData("SYSTEM", "COLUMNS", "BILLING_DOCTORS");// ����ҽʦ
		parm.addData("SYSTEM", "COLUMNS", "NOTIFY_DOCTORS_TIME");// ֪ͨ����ҽʦʱ��
		parm.addData("SYSTEM", "COLUMNS", "DIRECTOR_DR_CODE");// ������		
		parm.addData("SYSTEM", "COLUMNS", "NOTIFY_DIRECTOR_DR_TIME");// ֪ͨ������ʱ��
		parm.addData("SYSTEM", "COLUMNS", "COMPETENT_CODE");// ҽ������
		parm.addData("SYSTEM", "COLUMNS", "NOTIFY_COMPETENT_TIME");// ֪ͨҽ������ʱ��
		parm.addData("SYSTEM", "COLUMNS", "DEAN_CODE");// ����Ժ��
		parm.addData("SYSTEM", "COLUMNS", "NOTIFY_DEAN_TIME");// ֪ͨ����Ժ��ʱ��
		parm.addData("SYSTEM", "COLUMNS", "HANDLE_USER");// �����û�
		parm.addData("SYSTEM", "COLUMNS", "HANDLE_TIME");// ����ʱ��		
		data.setData("TABLE",parm.getData());
//		System.out.println("@test by wangqing@---data="+data);
		openPrintDialog("%ROOT%\\config\\prt\\MED\\MEDSmsExport.jhw", data, false);
	}
	
	/**
	 * ����Excel add by wangqing 20171116
	 */
	public void onExportExcel() {
		if (table.getRowCount() <= 0) {
			messageBox("�޵�������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "Σ��ֵ����");
	}
	
	
}
