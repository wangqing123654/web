package com.javahis.ui.ekt;

import jdo.ekt.EKTIO;
import com.dongyang.control.TControl;
import jdo.sys.Pat;
import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;
import jdo.ekt.EKTTool;
import jdo.sys.OperatorTool;

/**
 * <p>Title: ҽ�ƿ������޸�</p>
 *
 * <p>Description: ҽ�ƿ������޸�</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20111010
 * @version 1.0
 */
public class EKTUpdatePassWordControl extends TControl{
    private Pat pat; //������Ϣ
    private TParm parmEKT;
    private boolean ektFlg = false;
    public EKTUpdatePassWordControl() {
    }
    /**
    * ��ʼ������
    */
   public void onInit() {
   }

    /**
    * ��ҽ�ƿ�
    */
   public void onReadEKT() {
       //��ȡҽ�ƿ�
       parmEKT = EKTIO.getInstance().TXreadEKT();
       if (null == parmEKT || parmEKT.getErrCode() < 0 ||
           parmEKT.getValue("MR_NO").length() <= 0) {
           this.messageBox(parmEKT.getErrText());
           parmEKT = null;
           return;
       }
       //��Ƭ���
       this.setValue("OLD_EKTFEE", parmEKT.getDouble("CURRENT_BALANCE"));
       //��Ƭ���
       this.setValue("SEQ", parmEKT.getValue("SEQ"));
       //callFunction("UI|CARD_CODE|setEnabled", false); //���Ų��ɱ༭
       this.setValue("MR_NO", parmEKT.getValue("MR_NO"));


       onQuery();
   }
   /**
        * ��ѯ������Ϣ
        */
       private void onQuery() {
           pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
           if (pat == null) {
               this.messageBox("�޴˲�����Ϣ");
               this.setValue("MR_NO", "");
               return;
           }
           TParm parm = new TParm();
           parm.setData("CARD_NO",pat.getMrNo()+ parmEKT.getValue("SEQ"));
           parmEKT = EKTTool.getInstance().selectPWDEKTIssuelog(parm);
           if (parmEKT.getCount() <= 0) {
               this.messageBox("�˲���û��ҽ�ƿ���Ϣ");
               //������ҽ�ƿ���д��ʱ��������EKT_MASTER����Ϣ
               ektFlg = true;
           }
           //������
           this.setValue("MR_NO", pat.getMrNo());
           //����
           this.setValue("PAT_NAME", pat.getName());
           //��������
           setValue("BIRTH_DATE", pat.getBirthday());
           //�Ա�
           setValue("SEX_CODE", pat.getSexCode());
           callFunction("UI|MR_NO|setEnabled", false); //�����Ų��༭
    }
    /**
     * �޸�����
     */
    public void onSave(){
        if(null == parmEKT || parmEKT.getErrCode() < 0 || null == pat){
            this.messageBox("û�л��ҽ�ƿ���Ϣ");
            return;
        }
        if(ektFlg){
            this.messageBox("û�б���ҽ�ƿ���Ϣ�������޸�����");
            return;
        }

        if(this.getValue("NEW_PWD").toString().length()<=0){
            this.messageBox("�����벻����Ϊ��");
            return;
        }
        if(!this.getValue("NEW_PWD").equals(this.getValue("CHECK_PASSWORD"))){
            this.messageBox("��������������벻������������");
            this.setValue("CHECK_PASSWORD","");
            this.grabFocus("CHECK_PASSWORD");
            return;
        }

        //====zhangp 20120413 start
        //������Ƚ��Ƿ���ͬ
//        String oldPwd = parmEKT.getValue("PASSWORD",0);
//        String old_pwd= OperatorTool.getInstance().decrypt(oldPwd);//11
//        if(old_pwd.equals(this.getValue("OLD_PWD").toString())){
            TParm parm=new TParm();
            String new_pwd= OperatorTool.getInstance().encrypt(this.getValue("NEW_PWD").toString());
            parm.setData("PASSWORD",new_pwd);
            parm.setData("CARD_NO",parmEKT.getValue("CARD_NO",0));
            parm=EKTTool.getInstance().updateEKTPWD(parm);//�޸�����
            if(parm.getErrCode()<0){
                this.messageBox("�����޸�ʧ��");
            }else{
                this.messageBox("�����޸ĳɹ�");
                onClear();
            }
//        }else{
//            this.messageBox("�������������");
//        }
            ///===zhangp 20120413 end
    }
    /**
     * ��ղ���
     */
    public void onClear() {
        clearValue(" MR_NO;PAT_NAME;SEQ; " +
                   " BIRTH_DATE;SEX_CODE; " +
                   " OLD_PWD;NEW_PWD;CHECK_PASSWORD ");
        parmEKT = null;
        ektFlg = false;
        pat = null;
    }
}
