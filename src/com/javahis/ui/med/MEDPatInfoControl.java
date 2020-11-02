package com.javahis.ui.med;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;

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
public class MEDPatInfoControl extends TControl {
    /**
     * table
     */
    private static String TABLE="TABLE";
    /**
     * ��ʼ������
     */
    public void onInit(){
        //TABLE1˫���¼�
      callFunction("UI|" + TABLE + "|addEventListener",
                   TABLE + "->" + TTableEvent.DOUBLE_CLICKED, this, "onTableDoubleClicked");

        Object obj=this.getParameter();
        if(obj!=null){
            TParm parm = (TParm)obj;
            String admType = parm.getValue("ADM_TYPE");
            if("O".equals(admType)){
                this.getTTable(TABLE).setHeader("������,160,Timestamp,yyyy/MM/dd HH:mm:ss;�����,150;����,150,CLINICAREA_CODE;����,150,CLINICROOM_NO");
                this.getTTable(TABLE).setParmMap("ADM_DATE;CASE_NO;CLINICAREA_CODE;CLINICROOM_NO");
            }
            if("E".equals(admType)){
                this.getTTable(TABLE).setHeader("������,160,Timestamp,yyyy/MM/dd HH:mm:ss;�����,150;����,150,CLINICAREA_CODE;����,150,CLINICROOM_NO");
                this.getTTable(TABLE).setParmMap("ADM_DATE;CASE_NO;CLINICAREA_CODE;CLINICROOM_NO");
            }
            if("I".equals(admType)){
                this.getTTable(TABLE).setHeader("������,160,Timestamp,yyyy/MM/dd HH:mm:ss;�����,150;����,150,DEPT_CODE;����,150,STATION_CODE");
                this.getTTable(TABLE).setParmMap("ADM_DATE;CASE_NO;DEPT_CODE;STATION_CODE");
            }
            if("H".equals(admType)){
                this.getTTable(TABLE).setHeader("������,160,Timestamp,yyyy/MM/dd HH:mm:ss;�����,150;����,150,DEPT_CODE");
                this.getTTable(TABLE).setParmMap("ADM_DATE;CASE_NO;DEPT_CODE");
            }
            this.getTTable(TABLE).setParmValue(parm);
        }
    }
    /**
     * ˫��
     */
    public void onTableDoubleClicked(int row){
        this.setReturnValue(this.getTTable(TABLE).getParmValue().getRow(row));
        this.closeWindow();
    }
    /**
     * �õ�TABLE����
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
}
