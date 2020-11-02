package com.javahis.ui.emr;

import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.StringTool;

/**
 * 
 * ̩�ļ�¼��ҹ�������;
 * @author lixiang
 *
 */
public class EMRMedRecTXControl extends TControl {
	
	TComboBox boxFileName ;
	
	public EMRMedRecTXControl() {
    }
	
	 public void onInit() {
	        Object obj = this.getParameter();
	        if (obj != null) {
	            String type = ( (TParm) obj).getValue("TYPE");
	            if ("1".equals(type)) {
	                getTRadioButton("ADDLOW").setSelected(true);
	            }
	            if ("2".equals(type)) {
	                getTRadioButton("ADDAFTER").setSelected(true);
	            }
	            String caseNo = ( (TParm) obj).getValue("CASE_NO");
	            //String fileName = ( (TParm) obj).getValue("FILE_NAME");
	            String emtFileName = ( (TParm) obj).getValue("SUBTEMPLET_FILE");
	            //String emtFileName=( (TParm) obj).getValue("EMT_FILENAME");

	            boxFileName = (TComboBox)this.getComponent("FILE_NAME");
	           //this.messageBox("caseNo" + caseNo);
	           //this.messageBox("EMRMedRecTXControl emtFileName" + emtFileName);
	            //ȡ�����ļ�
	            boxFileName.setSQL(
	                "SELECT FILE_NAME,DESIGN_NAME FROM EMR_FILE_INDEX WHERE CASE_NO='" +
	                caseNo + "' AND FILE_NAME LIKE '%"+emtFileName+"%' ORDER BY CREATOR_DATE DESC");
	            
	            boxFileName.retrieve();

	            //this.setValue("FILE_NAME", fileName);
	            //ͨ��case_no ȡ�� file_name��design_name;

	            this.setValue("SYS_DATE", StringTool.getTimestamp(new Date()));
	            
	            //����Ϊ��Ժʱ��
	            this.setValue("ADM_DATE",
	                          StringTool.getString( ( (TParm) obj).
	                                               getTimestamp("ADM_DATE"),
	                                               "yyyy/MM/dd HH:mm:ss"));
	            
	            //����Ϊ��Ժʱ��
	            if ( ( (TParm) obj).getValue("DS_DATE").length() == 0) {
	                this.setValue("DS_DATE", "");
	            }
	            else {
	                this.setValue("DS_DATE",
	                              StringTool.getString( ( (TParm) obj).
	                    getTimestamp("DS_DATE"), "yyyy/MM/dd HH:mm:ss"));
	            }

	        }

	    }

	    /**
	     * ȷ��
	     */
	    public void onOk() {
	        //����ѡ��������;
	        String strFileNameMain=boxFileName.getValue();
	        //this.messageBox("==strFileNameMain=="+strFileNameMain);
	        if(strFileNameMain.equals("")){
	            this.messageBox("��ѡ�񲡳̱��⣡");
	            return;
	        }

	        //��ǰ����ʱ���Ƿ��Ѿ���������
	        Timestamp sysDate = (Timestamp)this.getValue("SYS_DATE");
	        long sDate = strToDate(StringTool.getString(sysDate,
            "yyyy/MM/dd HH:mm:ss"), "yyyy/MM/dd HH:mm:ss").getTime();
	        //
	        long aDate = 0;
	        if (this.getValueString("ADM_DATE").length() != 0) {
	            Timestamp admDate = StringTool.getTimestamp(this.getValueString(
	                "ADM_DATE"), "yyyy/MM/dd HH:mm:ss");
	            aDate = strToDate(StringTool.getString(admDate,
	                "yyyy/MM/dd HH:mm:ss"), "yyyy/MM/dd HH:mm:ss").getTime();
	        }
	        //
	        if (sDate < aDate && aDate != 0) {
	            this.messageBox("�Ѿ���������ʱ�����ޣ�");
	            return;
	        }	        
	       
	        //
	        long dDate = 0;
	        if (this.getValueString("DS_DATE").length() != 0) {
	            Timestamp dsDate = StringTool.getTimestamp(this.getValueString(
	                "DS_DATE"), "yyyy/MM/dd HH:mm:ss");
	            dDate = strToDate(StringTool.getString(dsDate,
	                "yyyy/MM/dd HH:mm:ss"), "yyyy/MM/dd HH:mm:ss").getTime();
	        }
	        if (sDate > dDate && dDate != 0) {
	            this.messageBox("�Ѿ���������ʱ�����ޣ�");
	            return;
	        }
	        //
	        TParm returnParm = new TParm();

	        if (getTRadioButton("ADDAFTER").isSelected()) {
	            returnParm.setData("EMRADD_DATE",
	                               StringTool.getString(sysDate, "yyyy/MM/dd HH:mm:ss"));
	        }
	        if (getTRadioButton("ADDLOW").isSelected()) {
	            returnParm.setData("EMR_DATE",
	                               StringTool.getString(sysDate, "yyyy/MM/dd HH:mm:ss"));
	        }
	            //��¼��ʱ���   �� Ϊ��̬Ϊץȡ��ֵ 	        
	             returnParm.setData("RECORD_TIME", sysDate);
	         
	             //׷�Ӳ����ļ���
	        	returnParm.setData("FILE_NAME_MAIN", strFileNameMain);
	        

	        this.setReturnValue(returnParm);
	        this.closeWindow();
	    }


	    /**
	     * ����ʱ���ʽ�ַ���ת��Ϊʱ�� yyyy-MM-dd HH:mm:ss
	     * @param strDate String
	     * @return Date
	     */
	    public Date strToDate(String strDate, String forMat) {
	        SimpleDateFormat formatter = new SimpleDateFormat(forMat);
	        ParsePosition pos = new ParsePosition(0);
	        Date strtodate = formatter.parse(strDate, pos);
	        return strtodate;
	    }

	    /**
	     * �õ�TRadioButton
	     * @param tag String
	     * @return TRadioButton
	     */
	    public TRadioButton getTRadioButton(String tag) {
	        return (TRadioButton)this.getComponent(tag);
	    }

	    /**
	     * �������ݿ��������
	     * @return TJDODBTool
	     */
	    public TJDODBTool getDBTool() {
	        return TJDODBTool.getInstance();
	    }
}
