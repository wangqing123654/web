package com.javahis.ui.sys;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.OperatorTool;
import com.javahis.system.root.RootClientListener;
import jdo.sys.Operator;
import com.dongyang.ui.TButton;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SYSPatLcokMessageControl
    extends TControl implements Runnable{
	//����������;
	public final static String  LOCK_PAT_TIME_PARAM="LockPatTime";	
	//
    TParm parm;
    String optUser;
    String prgId;
    String mrNo;
    String prgIdU;
    Thread thread;
    String userName;
    String windowId;
    //
    String lockTime;
    
    /**
     * ��ʼ��
     */
    public void onInit() {
        parm = (TParm)this.getParameter();
//        System.out.println("��ʼ�����"+parm);
        optUser = parm.getValue("OPT_USER", 0);
        prgId = parm.getValue("PRG_ID", 0);
        mrNo = parm.getValue("MR_NO");
        prgIdU = parm.getValue("PRGID_U");
        userName = OperatorTool.getInstance().getOperatorName(optUser);
        windowId =  parm.getValue("WINDOW_ID");
        setValue("MESSAGE", getPrgName(prgId) + userName + "�����ò���");
        RootClientListener.getInstance().addListener(this);
        lockTime = TConfig.getSystemValue(LOCK_PAT_TIME_PARAM);      
        if(lockTime==null||lockTime.equals("")){
        	lockTime="15";
        }
    }
    public void start()
    {
    	
    	setText("TIME",lockTime);
        if(thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }
    public void stop()
    {
        thread = null;
    }
    public void run()
    {
        while(thread != null)
        {
            sleep();
            int x = Integer.parseInt(getText("TIME"));
            x--;
            if(x >= 0)
            {
                setText("TIME","" + x);
                continue;
            }
            thread = null;
            TButton button = (TButton)this.getComponent("OK");
            button.setEnabled(true);
            button = (TButton)this.getComponent("SEND");
            button.setEnabled(false);
        }
    }
    public void sleep()
    {
        if(thread == null)
            return;
        try{
            thread.sleep(1000);
        }catch(Exception e)
        {

        }
    }
    public boolean onClosing(){
        RootClientListener.getInstance().removeListener(this);
        if(thread != null)
            thread = null;
        return true;
    }
    /**
     * �õ�ϵͳ����
     * @param prgId String
     * @return String
     */
    public String getPrgName(String prgId) {

        if ("ODO".equalsIgnoreCase(prgId))
            return "����ҽ��վ";
        if ("ODE".equalsIgnoreCase(prgId))
            return "����ҽ��վ";
        if ("OPB".equalsIgnoreCase(prgId))
            return "�����շ�";
        if ("ODI".equalsIgnoreCase(prgId))
            return "סԺҽ��վ";
        if ("ONW".equalsIgnoreCase(prgId))//====pangben 2013-5-14 ��ӻ�ʿվ�����ܿ�
            return "���ﻤʿվ";
        if ("ENW".equalsIgnoreCase(prgId))//====pangben 2013-5-14 ��ӻ�ʿվ�����ܿ�
            return "���ﻤʿվ";
        return "";
    }

    /**
     * ֪ͨ��ť
     */
    public void toldButton() {
        this.start();
        boolean result =  RootClientListener.getInstance().sendMessage(optUser,
            "#ACTION#UI|"+windowId+"|onListenPm|" + prgId + "|" + mrNo + "|" + prgIdU + "|" +Operator.getID());
       TButton button = (TButton)this.getComponent("SEND");
        button.setEnabled(false);
//        System.out.println("֪ͨ����"+result);
    }

    public void onCancel(){
        stop();
        this.closeWindow();
    }
    public void onOK(){
        stop();
        this.setReturnValue("OK");
        this.closeWindow();
    }
    /**
     * �ش����
     * @param prgId String
     * @param flg Object
     * @return Object
     */
    public Object onListenRm(String prgId, String flg) {
        stop();
        if (!prgIdU.equalsIgnoreCase(prgId))
            return null;
        if ("OK".equals(flg)) {
            this.messageBox(userName + "�ѽ���");
            this.setReturnValue("UNLOCKING");
            this.closeWindow();
            return "OK";
        }
        if ("CANCEL".equals(flg)) {
            this.messageBox(userName + "�ܾ�����");
            this.setReturnValue("LOCKING");
            this.closeWindow();
            return "OK";
        }
        if ("TimeOut".equals(flg)) {
            this.messageBox(userName + "��ʱδ��Ӧ");
            TButton button = (TButton)this.getComponent("OK");
            button.setEnabled(true);
            button = (TButton)this.getComponent("SEND");
            button.setEnabled(false);


            return "OK";
        }
        return "OK";
    }
}
