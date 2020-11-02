package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.SYSBedTool;
import com.dongyang.ui.TComboBox;
import jdo.sys.Operator;

/**
 * <p>Title:�������� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMSysBedAlloControl extends TControl{
     TParm acceptData = new TParm(); //�Ӳ�

    public void onInit(){
        acceptData=(TParm)this.getParameter();
        this.setValueForParm("DEPT_CODE;STATION_CODE",acceptData);
        this.initRoom();
        TComboBox box = (TComboBox)this.getComponent("BED_NO");
        box.onQuery();
    }
    /**
     * ��ʼ������
     */
    public void initRoom() {
        TParm parm = new TParm();
        parm.setData("BED_NO", acceptData.getData("BED_NO"));
        TParm result = SYSBedTool.getInstance().queryAll(parm);
        this.setValue("ROOM_CODE", result.getData("ROOM_CODE", 0));
    }

    /**
     * ��������
     */
    public void onBed(){
        TParm parm = new TParm();
        parm.setData("BED_NO",this.getValue("BED_NO"));
        parm.setData("CASE_NO",acceptData.getData("CASE_NO"));
        parm.setData("IPD_NO",acceptData.getData("IPD_NO"));
        parm.setData("MR_NO",acceptData.getData("MR_NO"));
        parm.setData("ALLO_FLG","Y");
        parm.setData("BED_OCCU_FLG","Y");
        parm.setData("BED_STATUS","3");//��λ״̬����Ϊ ����
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        TParm result = SYSBedTool.getInstance().alloSysbed(parm);
        if(result.getErrCode()<0){
             this.messageBox("E0005"); //ʧ��
        }else{
             this.messageBox("P0005"); //�ɹ�
             TComboBox box = (TComboBox)this.getComponent("BED_NO");
             box.setSelectedIndex(0);
             box.onQuery();
        }
    }
    public void onCan(){
        this.closeWindow();
    }
}
