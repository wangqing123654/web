package com.javahis.ui.opd;

import com.dongyang.control.*;
import jdo.bil.BIL;
import com.dongyang.util.TypeTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import jdo.reg.SessionTool;
import jdo.sys.Operator;
import jdo.ekt.EKTIO;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.odo.OPDAbnormalRegTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.reg.ClinicRoomTool;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title: �ǳ�̬����</p>
 *
 * <p>Description: �ǳ�̬����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-10-26
 * @version 1.0
 */
public class OPDAbnormalRegControl
    extends TControl {
    private String admType = "O";//�ż���
    private Pat pat;
    private String CASE_NO;
    private String tredeNo;//ҽ�ƿ����׺�
    public void onInit(){
        pageInit();
    }
    /**
     * ҳ���ʼ��
     */
    public void pageInit(){
        initCombo();
        initClinicRoom();
    }
    /**
     * ��ʼ��combo
     */
    public void initCombo(){
        //��ʼ������
        this.setValue("DATE",SystemTool.getInstance().getDate());
        //��ʼ��ʱ��
        String sessionCode = SessionTool.getInstance().getDefSessionNow(admType,
            Operator.getRegion());
        this.setValue("SESSION_CODE", sessionCode);
        //��ʼ���Ʊ���Ա
        this.setValue("DEPT_CODE",Operator.getDept());
        this.setValue("DR_CODE",Operator.getID());
        this.setValue("SERVICE_LEVEL","1");
    }
    /**
     * ��ʼ�����combo
     */
    public void initClinicRoom(){
        TParm pa = new TParm();
        pa.setData("ADM_TYPE","O");
        pa.setData("ADM_DATE",StringTool.getString((Timestamp)this.getValue("DATE"),"yyyyMMdd"));
        pa.setData("SESSION_CODE",this.getValue("SESSION_CODE"));
        pa.setData("REGION_CODE",Operator.getRegion());
        TParm parm = ClinicRoomTool.getInstance().getNotUseForODO(pa);
        TTextFormat tf = (TTextFormat)this.getComponent("CLINICROOM_NO");
        tf.setPopupMenuData(parm);
        tf.setComboSelectRow();
        tf.popupMenuShowData();
    }
    /**
     * ��ʼ��������Ϣ
     */
    public void onMrNo(){
        pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
        if(pat==null){
            this.messageBox_("�޴˲���");
            return;
        }
        this.setValue("MR_NO",pat.getMrNo());
        this.setValue("SEX_CODE",pat.getSexCode());
        this.setValue("PAT_NAME",pat.getName());
        this.setValue("CTZ1_CODE",pat.getCtz1Code());
        this.setValue("CTZ2_CODE",pat.getCtz2Code());
        this.setValue("CTZ3_CODE",pat.getCtz3Code());
        this.setValue("BIRTHDAY",pat.getBirthday());
    }
    /**
     * ���ѡ���¼�
     */
    public void onClickClinicType(){
        double reg_fee = BIL.getRegDetialFee(admType,getValueString("CLINICTYPE_CODE"),
                                             "REG_FEE",getValueString("CTZ1_CODE"),
                                             getValueString("CTZ2_CODE"),
                                             getValueString("CTZ3_CODE"),
                                             this.getValueString("SERVICE_LEVEL"));
        //�Һŷ�
        this.setValue("REG_FEE", reg_fee);
        double clinic_fee = BIL.getRegDetialFee(admType,getValueString("CLINICTYPE_CODE"),
                                                "CLINIC_FEE",getValueString("CTZ1_CODE"),
                                                getValueString("CTZ2_CODE"),
                                                getValueString("CTZ3_CODE"),
                                                this.getValueString("SERVICE_LEVEL"));
        //����
        this.setValue("CLINIC_FEE", clinic_fee);
        //�ܷ���
        setValue("AR_AMT", reg_fee + clinic_fee);
    }
    /**
     * ҽ�ƿ�����
     */
    public void onEKT(){
        TParm patParm = EKTIO.getInstance().getPat();
//        TParm patParm = new TParm();
//        patParm.setData("CARD_NO", "123");
//        patParm.setData("NAME", "xingming");
//        patParm.setData("SEX", "��");
//        patParm.setData("MR_NO", "000000000033");
//        patParm.setData("CURRENT_BALANCE", "123");
//        // System.out.println("����������"+patParm);
        if (patParm.getErrCode() < 0) {
            this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
            return;
        }
        if (patParm.getValue("MR_NO") == null ||
            patParm.getValue("MR_NO").length() == 0){
            this.messageBox_("����ʧ��");
        }
        else{
            this.setValue("MR_NO",patParm.getValue("MR_NO"));
            this.onMrNo();
        }
    }
    /***
     * ����
     */
    public void onSave(){
        //�������
        if(!checkData()){
            return;
        }
        CASE_NO = SystemTool.getInstance().getNo("ALL", "REG","CASE_NO", "CASE_NO");
        //����ҽ�ƿ�
        if (!this.onEktSave("Y")) {
            return;
        }
        //����Һ���Ϣ
        TParm result= TIOM_AppServer.executeAction(
		        "action.opd.OPDAbnormalRegAction", "saveReg", this.getSaveData());
        if(result.getErrCode()!=0){
            EKTIO.getInstance().unConsume(tredeNo,this);
            this.messageBox("E0005");
            return;
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        TParm re = OPDAbnormalRegTool.getInstance().selectRegForOPD(parm);
        this.setReturnValue(re);
        this.messageBox("P0005");
        this.closeWindow();
    }
    /**
     * ���
     */
    public void onClear(){
        this.clearValue("CLINICTYPE_CODE;MR_NO;PAT_NAME;SEX_CODE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;SERVICE_LEVEL;REG_FEE;CLINIC_FEE;AR_AMT");
        pat = null;
        CASE_NO = "";
        tredeNo="";
        initCombo();
    }
    /**
     * ��˱��������Ƿ��������
     * @return boolean
     */
    private boolean checkData(){
        if(pat==null){
            return false;
        }
        if(this.getValueString("DATE").length()==0){
            this.messageBox_("��ѡ������");
            this.grabFocus("DATE");
            return false;
        }
        if(this.getValueString("SESSION_CODE").length()==0){
            this.messageBox_("��ѡ��ʱ��");
            this.grabFocus("SESSION_CODE");
            return false;
        }
        if(this.getValueString("DEPT_CODE").length()==0){
            this.messageBox_("��ѡ��Ʊ�");
            this.grabFocus("DEPT_CODE");
            return false;
        }
        if(this.getValueString("DR_CODE").length()==0){
            this.messageBox_("��ѡ��ҽ��");
            this.grabFocus("DR_CODE");
            return false;
        }
        if(this.getValueString("CLINICTYPE_CODE").length()==0){
            this.messageBox_("��ѡ�����");
            this.grabFocus("CLINICTYPE_CODE");
            return false;
        }
        if(this.getValueString("CLINICROOM_NO").length()==0){
            this.messageBox_("��ѡ�����");
            this.grabFocus("CLINICROOM_NO");
            return false;
        }
        if(this.getValueString("CTZ1_CODE").length()==0){
            this.messageBox_("��������ݲ���Ϊ��");
            this.grabFocus("CTZ1_CODE");
            return false;
        }
        if(this.getValueString("SERVICE_LEVEL").length()==0){
            this.messageBox_("����ȼ�����Ϊ��");
            this.grabFocus("SERVICE_LEVEL");
            return false;
        }
        return true;
    }
    /**
     * ��ȡҪ���������
     * @return TParm
     */
    private TParm getSaveData(){
        TParm parm = new TParm();
        //����CASE_NO
        parm.setData("CASE_NO",CASE_NO);
        parm.setData("ADM_TYPE",admType);
        parm.setData("MR_NO",pat.getMrNo());
        parm.setData("REGION_CODE",Operator.getRegion());
        parm.setData("ADM_DATE",StringTool.getString((Timestamp)this.getValue("DATE"),"yyyyMMdd"));
        parm.setData("REG_DATE",SystemTool.getInstance().getDate());
        parm.setData("SESSION_CODE",this.getValue("SESSION_CODE"));
        parm.setData("CLINICTYPE_CODE",this.getValue("CLINICTYPE_CODE"));
        parm.setData("DEPT_CODE",this.getValue("DEPT_CODE"));
        parm.setData("DR_CODE",this.getValue("DR_CODE"));
        parm.setData("REALDEPT_CODE",this.getValue("DEPT_CODE"));
        parm.setData("REALDR_CODE",this.getValue("DR_CODE"));
        parm.setData("APPT_CODE","1");//����
        parm.setData("VISIT_CODE","0");//����
        parm.setData("REGMETHOD_CODE", "A");//�Һŷ�ʽ Ĭ���ֳ��Һ�
        parm.setData("CTZ1_CODE",this.getValue("CTZ1_CODE"));
        parm.setData("CTZ2_CODE",this.getValue("CTZ2_CODE"));
        parm.setData("CTZ3_CODE",this.getValue("CTZ3_CODE"));
        parm.setData("ARRIVE_FLG","Y");//����ע��
        parm.setData("ADM_REGION",Operator.getRegion());
        parm.setData("ADM_STATUS","1");//�������  1���ѹҺ�
        parm.setData("REPORT_STATUS","1");//����״̬  1ȫ��δ���
        parm.setData("SEE_DR_FLG","N");//����ע��
        parm.setData("HEAT_FLG","N");//����ע��
        parm.setData("SERVICE_LEVEL",this.getValue("SERVICE_LEVEL"));//����ȼ�
        parm.setData("TREDE_NO",tredeNo);//ҽ�ƿ����׺�
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        parm.setData("WEIGHT",0);
        parm.setData("HEIGHT",0);
        parm.setData("CLINICROOM_NO",this.getValue("CLINICROOM_NO"));//���
        return parm;
    }

    /**
     * ҽ�ƿ�����
     * @param FLG String
     * @return boolean
     */
    public boolean onEktSave(String FLG) {
        int type = 0;
        TParm parm = new TParm();
        //���ʹ��ҽ�ƿ������ҿۿ�ʧ�ܣ��򷵻ز�����
        if (EKTIO.getInstance().ektSwitch()) { //ҽ�ƿ����أ���¼�ں�̨config�ļ���
            parm = onOpenCard(FLG);
            if (parm == null) {
                this.messageBox("E0115");
                return false;
            }
            type = parm.getInt("OP_TYPE");
            if (type == 3) {
                this.messageBox("E0115");
                return false;
            }
            if (type == 2) {
                return false;
            }
            tredeNo = parm.getValue("TREDE_NO");
        }
        else {
            this.messageBox_("ҽ�ƿ��ӿ�δ����");
            return false;
        }
        return true;
    }
    /**
     * ��ҽ�ƿ�
     * @param FLG String
     * @return TParm
     */
    public TParm onOpenCard(String FLG) {
        //׼������ҽ�ƿ��ӿڵ�����
        TParm orderParm = new TParm();
        orderParm.addData("RX_NO", "REG"); //д�̶�ֵ
        orderParm.addData("ORDER_CODE", "REG"); //д�̶�ֵ
        orderParm.addData("SEQ_NO", "1"); //д�̶�ֵ
        orderParm.addData("AMT", TypeTool.getDouble(getValue("AR_AMT")));
        orderParm.addData("EXEC_FLG", "N"); //д�̶�ֵ
        orderParm.addData("RECEIPT_FLG", "N"); //д�̶�ֵ
        orderParm.addData("BILL_FLG", FLG);
        orderParm.setData("MR_NO", pat.getMrNo());
        orderParm.setData("BUSINESS_TYPE", "REG");
        orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
        TParm orderSumParm=new TParm();
		orderSumParm.setData("TOT_AMT",0, TypeTool.getDouble(getValue("AR_AMT")));
		orderParm.setData("orderSumParm", orderSumParm.getData()); // �˲��������շ�ҽ�������Ѿ���Ʊ��
		orderParm.setData("ektSumParm", (new TParm()).getData()); // ҽ�ƿ��Ѿ��շѵ�����
        //��ҽ�ƿ�������ҽ�ƿ��Ļش�ֵ
        TParm parm = EKTIO.getInstance().onOPDAccntClient(orderParm, CASE_NO, this);
        return parm;
    }

}
