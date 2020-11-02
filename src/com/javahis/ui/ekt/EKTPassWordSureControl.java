package com.javahis.ui.ekt;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.ekt.EKTTool;
import jdo.sys.OperatorTool;

/**
 * <p>Title: ҽ�ƿ�����ȷ��</p>
 *
 * <p>Description: ҽ�ƿ�����ȷ��</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20111007
 * @version 1.0
 */
public class EKTPassWordSureControl extends TControl{
    public EKTPassWordSureControl() {
    }
    private TParm ektParm;//ҽ�ƿ���Ϣ
    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        onInitParm((TParm)getParameter());
        //zhangp 20121219 �ӽ���
        this.grabFocus("CARD_PWD");
    }
    /**
     * ��ò���
     * @param parm TParm
     */
    private void onInitParm(TParm parm){
        if(parm == null)
            return;
        ektParm = parm;
    }
    /**
     * ִ�з���
     */
    public void onSave(){
        if(this.getValue("CARD_PWD").toString().length()<=0){
            this.messageBox("���벻����Ϊ��");
            this.grabFocus("CARD_PWD");
            return ;
        }
        ektParm.setData("CARD_NO",ektParm.getValue("MR_NO")+ektParm.getValue("SEQ"));
        TParm result = EKTTool.getInstance().selectPWDEKTIssuelog(ektParm);
        String old_pwd=OperatorTool.getInstance().decrypt(result.getValue("PASSWORD",0));//����
        if(result.getCount()<=0 || !this.getValue("CARD_PWD").toString().equals(old_pwd)){
            this.messageBox("���벻��ȷ");
            this.grabFocus("CARD_PWD");
        }else{
            TParm parm =new TParm();
            parm.setData("FLG","Y");
            setReturnValue(parm);
            closeWindow();
        }
    }

    /**
     * ȡ����
     */
    public void onCancel() {
        closeWindow();
    }

}
