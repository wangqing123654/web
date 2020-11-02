package com.javahis.ui.sys;

import com.dongyang.config.TConfig;
import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.sys.OperatorTool;
import com.javahis.system.root.RootClientListener;

/**
 * <p>Title: ������ǿ�ƽ�����ʾ����</p>
 *
 * <p>Description: ������ǿ�ƽ�����ʾ����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-12-28
 * @version 1.0
 */
public class SYSPatUnLcokMessageControl
    extends TControl implements Runnable{
	
	//����������;
	public final static String  LOCK_PAT_TIME_PARAM="LockPatTime";	
    String mrNo;
    String optUser;
    String prgId;
    String prgIdU;
    Thread thread;
    
    //
    String lockTime;
    
    public void onInit(){
        super.onInit();
        Object obj = this.getParameter();
        if(obj != null)
        {
            TParm parm = (TParm) obj;
            optUser = parm.getValue("USE_ID");
            prgId = parm.getValue("PRG_ID");
            mrNo = parm.getValue("MR_NO");
            prgIdU = parm.getValue("PRG_ID_U");
            String userName = OperatorTool.getInstance().getOperatorName(
                optUser);
            setValue("MESSAGE", getPrgName(prgIdU) + userName + "�����������,�Ƿ�ͬ��?");
        }
        lockTime = TConfig.getSystemValue(LOCK_PAT_TIME_PARAM);      
        if(lockTime==null||lockTime.equals("")){
        	lockTime="15";
        }
        //this.messageBox("--lockTime--"+lockTime);
        setText("TIME",lockTime);
        start();
    }
    public void start()
    {
        thread = new Thread(this);
        thread.start();
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
            RootClientListener.getInstance().sendMessage(optUser,
            "#ACTION#UI|onListenRm|" + prgIdU +"|"+"TimeOut");
            this.setReturnValue("OK");
            this.closeWindow();
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
        if ("ONW".equalsIgnoreCase(prgId))
            return "���ﻤʿվ";
        return "";
    }

    public void onOK() {
        stop();
        boolean result = RootClientListener.getInstance().sendMessage(optUser,
            "#ACTION#UI|onListenRm|" + prgIdU +"|"+"OK");
        this.setReturnValue("OK");
        this.closeWindow();
    }

    public void onCancel() {
        stop();
        boolean result = RootClientListener.getInstance().sendMessage(optUser,
            "#ACTION#UI|onListenRm|" + prgIdU +"|"+"CANCEL");
        this.closeWindow();

    }

    public boolean onClosing(){
        if(thread != null){
            stop();
            boolean result = RootClientListener.getInstance().sendMessage(
                optUser,
                "#ACTION#UI|onListenRm|" + prgIdU + "|" + "CANCEL");
        }
        return true;
    }
}
