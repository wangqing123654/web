package com.javahis.ui.emr;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.javahis.util.StringUtil;

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
public class EMRTempInfoUIControl extends TControl {

    public void onInit(){
        super.onInit();
        Object obj = this.getParameter();
        //this.messageBox("aaaa"+(String)((TParm)obj).getData("NEW_SUBCLASS_CODE"));
        if(obj!=null){
            TParm parm = (TParm)obj;
            if(parm.getData("OP")!=null&&parm.getData("OP").equals("ADD")){
                 String subClassCode=((String)parm.getData("SUBCLASS_CODE")).substring(0,9);
                 String subClassSeq=((String)parm.getData("SUBCLASS_CODE")).substring(9,((String)parm.getData("SUBCLASS_CODE")).length());
                 this.setValue("SUBCLASS_CODE",subClassCode);
                 this.setValue("SUBCLASS_SEQ",subClassSeq);

            }else{
                this.setValue("USER_ID",parm.getData("USER_ID"));
                this.setValue("DEPT_CODE",parm.getData("DEPT_CODE"));
                this.setValue("EMT_FILENAME",parm.getData("EMT_FILENAME"));
                this.setValue("SUBCLASS_DESC",parm.getData("SUBCLASS_DESC"));
                this.setValue("RUN_PROGARM", parm.getData("RUN_PROGARM"));
                this.setValue("SUBTEMPLET_CODE", parm.getData("SUBTEMPLET_CODE"));
                this.setValue("CLASS_STYLE", parm.getData("CLASS_STYLE"));
                this.setValue("PY1",parm.getData("PY1"));
                this.setValue("IPD_FLG",parm.getData("IPD_FLG"));
                this.setValue("HRM_FLG",parm.getData("HRM_FLG"));
                this.setValue("OPD_FLG",parm.getData("OPD_FLG"));
                this.setValue("EMG_FLG",parm.getData("EMG_FLG"));
                this.setValue("OIDR_FLG",parm.getData("OIDR_FLG"));
                this.setValue("NSS_FLG",parm.getData("NSS_FLG"));
                this.setValue("DOCUMENT_TYPE",parm.getData("DOCUMENT_TYPE"));
                this.setValue("MBABY_FLG",parm.getData("MBABY_FLG"));     
                this.setValue("PUBLIC_FLG",parm.getData("PUBLIC_FLG")); 
                this.setValue("STOP_FLG",parm.getData("STOP_FLG")); 
                
                //this.setValue("REF_FLG",parm.getData("REF_FLG"));
                String strSubClassCode= ((String)parm.getData("SUBCLASS_CODE"));
                if(strSubClassCode.length()>=9){
                    String subClassCode=((String)parm.getData("SUBCLASS_CODE")).substring(0,9);
                    String subClassSeq=((String)parm.getData("SUBCLASS_CODE")).substring(9,((String)parm.getData("SUBCLASS_CODE")).length());
                    this.setValue("SUBCLASS_CODE",subClassCode);
                    this.setValue("SUBCLASS_SEQ",subClassSeq);
                }else{
                    this.setValue("SUBCLASS_CODE",strSubClassCode);
                }
                this.setValue("SYSTEM_CODE",parm.getData("SYSTEM_CODE"));
                setTRadioButtonStatus();

            }

        }
    }
    /**
     * ȷ��
     */
    public void onOK(){
        if(this.getValueString("SUBCLASS_SEQ").length()==0){
             this.messageBox("����дģ����ţ�");
            return;
        }
        if(this.getValueString("EMT_FILENAME").length()!=0&&this.getValueString("SUBCLASS_DESC").length()!=0){

            TParm parm = new TParm();
            parm.setData("USER_ID",this.getValueString("USER_ID"));
            parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
            parm.setData("EMT_FILENAME",this.getValueString("EMT_FILENAME").trim());
            parm.setData("SUBCLASS_DESC",this.getValueString("SUBCLASS_DESC"));
            parm.setData("RUN_PROGARM",this.getValueString("RUN_PROGARM"));
            parm.setData("SUBTEMPLET_CODE",this.getValueString("SUBTEMPLET_CODE"));
            parm.setData("CLASS_STYLE",this.getValueString("CLASS_STYLE"));
            parm.setData("PY1",this.getValueString("PY1"));
            parm.setData("IPD_FLG",getFlgStatus("IPD_FLG"));
            parm.setData("HRM_FLG",getFlgStatus("HRM_FLG"));
            parm.setData("OPD_FLG",getFlgStatus("OPD_FLG"));
            parm.setData("EMG_FLG",getFlgStatus("EMG_FLG"));
            parm.setData("OIDR_FLG",getFlgStatus("OIDR_FLG"));
            parm.setData("NSS_FLG",getFlgStatus("NSS_FLG"));
            parm.setData("SYSTEM_CODE",this.getValueString("SYSTEM_CODE"));
            parm.setData("TITLE_TYPE",getTitleType());
            parm.setData("SUBCLASS_CODE",this.getValueString("SUBCLASS_CODE")+this.getValueString("SUBCLASS_SEQ"));
            parm.setData("DOCUMENT_TYPE",this.getValueString("DOCUMENT_TYPE"));
            //parm.setData("REF_FLG",this.getValueString("REF_FLG"));
            //����ĸӤ���ñ�־
            parm.setData("MBABY_FLG",this.getValueString("MBABY_FLG"));
            //����ȫ�ֲ���
            parm.setData("PUBLIC_FLG",this.getValueString("PUBLIC_FLG"));
            //ͣ�ñ�־
            parm.setData("STOP_FLG",this.getValueString("STOP_FLG"));
            //this.messageBox("---MBABY_FLG-"+parm.getData("MBABY_FLG"));
            
            this.setReturnValue(parm);
            this.closeWindow();
        }else{
            this.messageBox("����дģ�����ƺ���ʾ���ƣ�");
            return;
        }
    }
    /**
     * �õ���������
     * @return String
     */
    public String getTitleType(){
        if(getTRadioButton("NULLTITLE").isSelected()){
            return "1";
        }
        if(getTRadioButton("TITLE").isSelected()){
            return "2";
        }
        if(getTRadioButton("IMGTITLE").isSelected()){
            return "3";
        }
        if(getTRadioButton("A5NULL").isSelected()){
            return "4";
        }
        if(getTRadioButton("A5TITLE").isSelected()){
            return "5";
        }
        if(getTRadioButton("A5IMG").isSelected()){
            return "6";
        }
        return "1";
    }
    /**
     * ����ģ��״̬
     */
    public void setTRadioButtonStatus(){
        getTRadioButton("NULLTITLE").setEnabled(false);
        getTRadioButton("TITLE").setEnabled(false);
        getTRadioButton("IMGTITLE").setEnabled(false);
        getTRadioButton("A5NULL").setEnabled(false);
        getTRadioButton("A5TITLE").setEnabled(false);
        getTRadioButton("A5IMG").setEnabled(false);
    }
    /**
     * �õ�TRadioButton
     * @param tag String
     * @return TRadioButton
     */
    public TRadioButton getTRadioButton(String tag){
        return (TRadioButton)this.getComponent(tag);
    }
    /**
     * ȡ��
     */
    public void onCancel(){
        this.closeWindow();
    }
    /**
     * ƴ���¼�
     */
    public void onPY(){
        String py1 = StringUtil.onCode(this.getValueString("SUBCLASS_DESC"));
        this.setValue("PY1",py1);
    }
    /**
     * �õ�TCheckBox״̬
     * @param tag String
     * @return String
     */
    public String getFlgStatus(String tag){
        if(getTCheckBox(tag).isSelected())
            return "Y";
        else
            return "N";
    }
    /**
     * �õ�TCheckBox
     * @param tag String
     * @return TCheckBox
     */
    public TCheckBox getTCheckBox(String tag){
        return (TCheckBox)this.getComponent(tag);
    }
}
