package com.javahis.ui.opd;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * 
 * 
 * <p>
 * Title:Ԥ���������ѡ��
 * </p>
 * 
 * <p>
 * Description:Ԥ���������ѡ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) bluecore
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author caowl 20131116
 * @version 1.0
 */
public class OPDPatPreOrderChooseControl extends TControl {

	 int selectrow = -1;
	 String mrNo;
	 
	/**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        //�õ�ǰ̨���������ݲ���ʾ�ڽ�����
        TParm patInfo = (TParm) getParameter();
        this.setValue("MR_NO", patInfo.getData("MR_NO"));
        this.setValue("PAT_NAME", patInfo.getData("PAT_NAME"));
        this.setValue("SEX_CODE", patInfo.getData("SEX_CODE"));
        this.setValue("AGE", patInfo.getData("AGE"));
        
       
        
        
        //Ԥ���ѯʱ���
        Timestamp rollDay = SystemTool.getInstance().getDate();
		String s_date = StringTool.getString(rollDay, "yyyy/MM/dd 00:00:00");
		String e_day = StringTool.getString(rollDay, "yyyy/MM/dd 23:59:59");
//        this.callFunction("UI|STARTTIME|setValue",
//                          SystemTool.getInstance().getDate());
//        this.callFunction("UI|ENDTIME|setValue",
//                          SystemTool.getInstance().getDate());
		this.callFunction("UI|STARTTIME|setValue",
				s_date);
        this.callFunction("UI|ENDTIME|setValue",
		        e_day);
        //table1�ĵ��������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1��˫�������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
        onQuery();//yanjing 20131210
    }
    
    /**
     * ���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        //���������¼�
        this.callFunction("UI|TABLE|acceptText");
        selectrow = row;
    }

    /**
     * ��table���м���  ˫���¼�
     * */
    public void onTableDoubleClicked(int row) {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        this.setReturnValue(data.getRow(selectrow));
        this.callFunction("UI|onClose");
    }

    /**
     * ��ѯ
     */
    public void onQuery() {

		String date_s = getValueString("STARTTIME");
		String date_e = getValueString("ENDTIME");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("��������Ҫ��ѯ��ʱ�䷶Χ");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
    	String sql = 
    		" SELECT CASE_NO ,ADM_DATE AS PRE_DATE " +
    		" FROM REG_PATADM " +
    		" WHERE MR_NO = '"+this.getValueString("MR_NO")+"'" +
    		" AND IS_PRE_ORDER = 'Y'" +
//    		" AND APPT_CODE = 'Y'" +
    		" AND REGCAN_USER IS NULL " +
    		" AND ARRIVE_FLG = 'N'" +
    		" AND (ADM_DATE BETWEEN TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') AND TO_DATE('"+date_e+"','YYYYMMDDHH24MISS')" +
    		" OR ADM_DATE IS NULL)";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	
        if (parm.getCount() <= 0){
        	this.messageBox("������Ԥ�������Ϣ!");
        	return;
        }          
        this.callFunction("UI|TABLE|setParmValue", parm);
    }

    /**
     * ȷ�ϴ����¼�
     */
    public void onOK() {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");       
         TParm returnParm = data.getRow(selectrow);
//         if(returnParm.getCount()>0 ){//��ֵʱȡ��Ԥ��ʱ���뵱ǰʱ��Ƚ�
        	 String preDate = returnParm.getValue("PRE_DATE");
        	 Timestamp rollDay = SystemTool.getInstance().getDate();
     		String sysDate = StringTool.getString(rollDay, "yyyy/MM/dd 00:00:00");
     		if(!preDate.equals(sysDate)){
     			this.messageBox("ԤԼ��Ϣ�ǽ��ա�");
     		}
//         }
         this.setReturnValue(data.getRow(selectrow));
        this.callFunction("UI|onClose");
    }
    
    /**
   * ȡ���¼�
   * @return boolean
   */
	public void onCANCLE() {
		switch (messageBox("��ʾ��Ϣ", "ȷ��ȡ��ѡ��", this.YES_NO_OPTION)) {
		case 0:
			this.closeWindow();
		case 1:
			break;
		}
		return;
	}

}
