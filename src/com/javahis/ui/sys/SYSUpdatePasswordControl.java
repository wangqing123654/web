package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TLabel;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: �û��޸�����</p>
 *
 * <p>Description: �û��޸�����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHIS</p>
 *
 * @author zhangy 2010.4.10
 * @version 1.0
 */
public class SYSUpdatePasswordControl
    extends TControl {

    private String user_id;

    private String old_user_password;

    private String loginUI = "";
    private TParm parm = null;//===pangben modify 20110608
    public SYSUpdatePasswordControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        user_id = Operator.getID();
        old_user_password = OperatorTool.getInstance().decrypt(OperatorTool.
            getInstance().getOperatorPassword(user_id));
        //================pangben modify 20110531 start
        Object obj = this.getParameter();
        if (obj instanceof String) {
            loginUI = (String) obj;
        }
        if (null != loginUI && loginUI.equals("Y")) {
            TButton button = (TButton)this.getComponent("BTN_CANCEL");
            button.setEnabled(false);
        }
        //================pangben modify 20110531 stop
        //============pangben modify 20110607 start
        //��ѯ�����������Ƿ���ʾУ��ǿ�� ��������ݿ�PWD_STRENGTH�ֶ�Ϊ'N'������ʾУ�飬������ʾ
        if (null != Operator.getRegion() && !"".equals(Operator.getRegion())) {

            String pwdSQL =
                    "SELECT PWD_STRENGTH,DETECTPWDTIME FROM SYS_REGION WHERE REGION_CODE='" +
                    Operator.getRegion() + "'";
            parm = new TParm(TJDODBTool.getInstance().select(pwdSQL));
        } else {
            parm = new TParm();
            parm.setData("PWD_STRENGTH",0, "N");
            parm.setData("DETECTPWDTIME",0, "999");//Ĭ������
        }
        if ("N".equals(parm.getValue("PWD_STRENGTH", 0))) {
            callFunction("UI|pwd_LBL|setVisible", false);
            callFunction("UI|PWD_POOFSTH|setVisible", false);
        } else {
            callFunction("UI|pwd_LBL|setVisible", true);
            callFunction("UI|PWD_POOFSTH|setVisible", true);
        }
        this.setValue("pwd_LBL", ""); //��ʦ���治��ʾ
        //============pangben modify 20110607 stop

    }
    /**
     * ȷ���¼�
     */
    public void onOK() {
        //============pangben modify 20110608 start
        TLabel pwdLbl = (TLabel)this.getComponent("PWD_POOFSTH");
        if (pwdLbl.isVisible())
            pwdPoofSth();
        //============pangben modify 20110608 stop
        String old_password = this.getValueString("OLD_PASSWORD");
        String new_password = this.getValueString("NEW_PASSWORD");
        String check_password = this.getValueString("CHECK_PASSWORD");
        if ("".equals(old_password)) {
            this.messageBox("�����벻��Ϊ��");
            return;
        }

        //=======pangben modify 20110631 start
         //��֤����ǿ�ȣ���ǿ�ȹ��Ͳ����Ա���
         TLabel lbl = (TLabel)this.getComponent("pwd_LBL");
         if (pwdLbl.isVisible() && lbl.getValue().equals("��")) {
             this.messageBox("����ǿ��̫��,�����Ա���!");
             return;
         }
         //=======pangben modify 20110631 stop

        if ("".equals(new_password)) {
            this.messageBox("�����벻��Ϊ��");
            return;
        }
        if ("".equals(check_password)) {
            this.messageBox("ȷ�����벻��Ϊ��");
            return;
        }
        if (!check_password.equals(new_password)) {
            this.messageBox("��������ȷ�����벻��ͬ");
            return;
        }
        if (!old_user_password.equals(old_password)) {
            this.messageBox("���������벻��ȷ");
            return;
        }
        boolean flg = OperatorTool.getInstance().setOperatorPassword(user_id,
            OperatorTool.getInstance().encrypt(check_password));
        if (flg) {
            if (!OperatorTool.getInstance().updatePwdstartDate(user_id,parm)) {
               err("��һ���޸��������");
               return;
           }
            this.messageBox("������³ɹ�");
            this.closeWindow();
        }
        else {
            this.messageBox("�������ʧ��");
        }
    }
    /**
     * ȡ���¼�
     */
    public void onCancel() {
        this.closeWindow();
    }


    /**
     * �����ı�����ʾ����ǿ�ȷ���
     * pangben modify 20110607
     */
    public void pwdPoofSth() {
        //pwdMessageShow = true;
        //ֻ�д����û�������ʾУ��ǿ��
        String sPW = this.getValueString("NEW_PASSWORD");
        TLabel lbl = (TLabel)this.getComponent("pwd_LBL");
        if (sPW.length() <= 4) {
            this.setValue("pwd_LBL", "��");
            lbl.setColor("RED");
            return; //����̫��
        }
        String modes = "";
        for (int i = 0; i < sPW.length(); i++) {
            //����ÿһ���ַ������ͳ��һ���ж�����ģʽ.
            modes = modes + getCharCode(sPW.charAt(i));
        }
        int result = bitTotal(modes);
        switch (result) {
        case 0:
            this.setValue("pwd_LBL", "��");
            lbl.setColor("RED");
            break;
        case 1:
            this.setValue("pwd_LBL", "��");
            lbl.setColor("��");
            break;
        case 2:
            this.setValue("pwd_LBL", "ǿ");
            lbl.setColor("BLUE");
            break;
        default:
            this.setValue("pwd_LBL", "��");
            lbl.setColor("RED");
            break;
        }

    }

    /**
     * ��֤����ǿ�ȷ�����ʾǿ�ȵ����֣�0 �ͣ�1 �У�2 ǿ
     * @param modes String
     * @return int
     * pangben modify 20110607
     */
    public int bitTotal(String modes) {
        int ans = 0;
        if (modes.contains("1") && modes.contains("2") && modes.contains("3"))
            ans = 2;
        else if (modes.contains("1") && modes.contains("2"))
            ans = 1;
        else if (modes.contains("2") && modes.contains("3"))
            ans = 1;
        else if (modes.contains("1") && modes.contains("3"))
            ans = 1;
        else
            ans = 0;
        return ans;
    }

    /**
     * ͨ����֤ÿһ���ַ���ASCII��ֵ�����Ҫ����ֵ
     * @param iN char
     * @return String
     * pangben modify 20110607
     */
    public String getCharCode(char iN) {
        if (iN >= 48 && iN <= 57) //����
            return "1";
        if (iN >= 65 && iN <= 90) //��д��ĸ
            return "2";
        if (iN >= 97 && iN <= 122) //Сд
            return "2";
        else
            return "3"; //�����ַ�
    }
}
