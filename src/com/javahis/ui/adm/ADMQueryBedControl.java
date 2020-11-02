package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TDataStore;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import jdo.adm.ADMInpTool;
import jdo.sys.SYSBedTool;
import com.dongyang.ui.TCheckBox;

/**
 * <p>Title: ��λѡ��</p>
 *
 * <p>Description: ��λѡ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk
 * @version 1.0
 */
public class ADMQueryBedControl
    extends TControl {

    TParm acceptParm = new TParm();
    private String type ;
    public void onInit() {
        acceptParm = (TParm)this.getParameter();
        type = acceptParm.getValue("TYPE") ;  //chenxi modify �ж������ԤԼסԺ��סԺ�Ǽǡ�ת��
        initCombo();
        initTable();
        callFunction("UI|ROOM_CODE|onQuery");
    }

    /**
     * ���ܲ�����ֵ
     */
    public void initCombo() {
        this.setValue("DEPT_CODE", acceptParm.getData("DEPT_CODE"));
        this.setValue("STATION_CODE", acceptParm.getData("STATION_CODE"));
       
    }

    /**
     * ������λTABLE��ʾ
     */
    public void initTable() {
        TParm parm = new TParm();
        parm.setData("DEPT_CODE", getValue("DEPT_CODE").toString());
        parm.setData("STATION_CODE", getValue("STATION_CODE").toString());
        if(type.equals("RESV")){
        	 parm.setData("DR_APPROVE_FLG", "Y");	
        }else if(type.equals("REWBODYREGISTER")){//������ע�� duzhw add 20140324
        	parm.setData("DR_APPROVE_FLG", "Y");
        	parm.setData("ROOM_CODE", acceptParm.getData("ROOM_CODE"));
        	parm.setData("BABY_BED_FLG", "Y");
        	parm.setData("ALLO_FLG", "N");//ռ�����
        }
        TParm result = ADMInpTool.getInstance().QueryBed(parm);
        TTable table = (TTable)this.callFunction("UI|BED|getThis");
        table.setParmValue(result);
    }

    /**
     * ��λ����
     */
    public void onSelectBed() {
        TCheckBox checkbox = (TCheckBox)this.callFunction("UI|ALLO|getThis");
        TParm parm = new TParm();
        if (checkbox.isSelected()) {
            parm.setData("ALLO_FLG", "N");
        }
        if(type.equals("RESV")){
       	 parm.setData("DR_APPROVE_FLG", "Y");	
       }
        parm.setData("DEPT_CODE", getValue("DEPT_CODE").toString());
        parm.setData("STATION_CODE", getValue("STATION_CODE").toString());
        parm.setData("STATION_CODE", getValue("STATION_CODE").toString());
        parm.setDataN("ROOM_CODE", this.getValue("ROOM_CODE"));
        parm.setDataN("BED_CLASS_CODE", this.getValue("BED_CLASS_CODE"));
        TParm result = ADMInpTool.getInstance().QueryBed(parm);
        TTable table = (TTable)this.callFunction("UI|BED|getThis");
        table.setParmValue(result);
    }

    /**
     * ��ѡ��λ
     */
    public void onBedTable() {
        if (!check()) {//����Ƿ�ռ��
            return;
        }
        TTable table = (TTable)this.callFunction("UI|BED|getThis");

        TParm parm = table.getParmValue();

        TParm queryParm = new TParm();
        //����CODE
        queryParm.setData("BED_NO",
                          parm.getValue("BED_NO", table.getSelectedRow()));
        TParm sendParm = SYSBedTool.getInstance().queryBedYellowRed(queryParm);
        this.setReturnValue(sendParm);
        this.closeWindow();
    }
    /**
     * ��˴�λ
     * @return boolean
     */
    public boolean check() {
        TTable table = (TTable)this.callFunction("UI|BED|getThis");
        if (table.getSelectedRow() < 0) {
            this.messageBox("δѡ��λ");
            return false;
        }
        String pat = table.getValueAt(table.getSelectedRow(), 1).toString();
        //=============shibl 20130106 add======���˵�ͬһ����δˢ��ҳ��=============================
        TParm  parm=table.getParmValue().getRow(table.getSelectedRow());
        TParm  inParm=new TParm();
        inParm.setData("BED_NO", parm.getValue("BED_NO"));
        TParm result = ADMInpTool.getInstance().QueryBed(inParm);
        String APPT_FLG=result.getCount()>0?result.getValue("APPT_FLG",0):"";
        String ALLO_FLG=result.getCount()>0?result.getValue("ALLO_FLG",0):"";
        String BED_STATUS=result.getCount()>0?result.getValue("BED_STATUS",0):"";
        //======================  chenxi modify ռ�����Ա�ԤԼ���޸�
        if(type.equals("RESV") ){
        	 if ("Y".equals(table.getValueAt(table.getSelectedRow(), 0).toString())||APPT_FLG.equals("Y")) {
        		 this.messageBox("�˴���ԤԼ�������ٴ�ԤԼ");
                 initTable();
                 return false; 
        	 }
        	 if((parm.getValue("MEDDISCH_DATE").toString().equals("") || parm.getValue("MEDDISCH_DATE")==null) 
        			 && ("Y".equals(pat)||ALLO_FLG.equals("Y"))){
        		 this.messageBox("�˲���û��ԤԼ��Ժʱ�䣬�˴�����ԤԼ");
                 initTable();
                 return false;
        	 }
        }
        else {
        if ("Y".equals(pat)||ALLO_FLG.equals("Y")) {
            this.messageBox("�˴���ռ��");
            initTable();
            return false;
        }
        if ("Y".equals(table.getValueAt(table.getSelectedRow(), 2).toString())||BED_STATUS.equals("1")) {
            this.messageBox("�˴��ѱ�����");
            initTable();
            return false;
        }
        if ("Y".equals(table.getValueAt(table.getSelectedRow(), 0).toString())||APPT_FLG.equals("Y")) {
        	int check = this.messageBox("��Ϣ", "�˴��ѱ�Ԥ�����Ƿ��������", 0);
			if (check != 0) {
				 initTable();
				return  false;
			}
            return true;
        }
        }
        return true;
    }
    /**
     * �մ�
     */
    public void onAllo() {
        TCheckBox checkbox = (TCheckBox)this.callFunction("UI|ALLO|getThis");
        if (checkbox.isSelected()) {
            TParm parm = new TParm();
            parm.setData("DEPT_CODE", getValue("DEPT_CODE").toString());
            parm.setData("STATION_CODE", getValue("STATION_CODE").toString());
            parm.setData("ALLO_FLG", "N");
            if(type.equals("RESV")){
           	 parm.setData("DR_APPROVE_FLG", "Y");	
           }
            TParm result = ADMInpTool.getInstance().QueryBed(parm);
            TTable table = (TTable)this.callFunction("UI|BED|getThis");
            table.setParmValue(result);
        }
        else {
            onSelectBed();
        }
    }
}
