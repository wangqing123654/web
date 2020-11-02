package com.javahis.ui.adm;

import java.util.ArrayList;
import java.util.List;

import jdo.hl7.Hl7Communications;
import jdo.sys.SYSBedTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;



/**
 * 转出科室弹出框
 * @author whaosoft
 *
 */
public class ADMOutBedControl extends TControl {


    /**
     *
     */
	public void onInit(){

	}

    /**
     * 转科保存(转出床位-新科室)
     */
    public void inOutDept(){

    	TParm tp = (TParm)this.getParameter();

    	tp.setData("IN_DEPT_CODE", this.getValue("IN_DEPT_CODE") );
    	tp.setData("IN_STATION_CODE",this.getValue("IN_STATION_CODE") );

    	boolean IsICU = SYSBedTool.getInstance().checkIsICU(tp.getValue("CASE_NO"));
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(tp.getValue("CASE_NO"));
		tp.setData("ICU_FLG", IsICU);
		tp.setData("CCU_FLG", IsCCU);

        TParm result = TIOM_AppServer.executeAction(
                "action.adm.ADMWaitTransAction", "onInOutSave", tp);
        if ("F".equals(result.getData("CHECK"))) {
            this.messageBox("此病患暂不能转科！");
        }
        if (result.getErrCode() < 0) {
            this.messageBox("E0005");
        } else {
            this.messageBox("P0005");
            /*********************** HL7 *******************************/
            //## 暂时不调用  sendHL7Mes(tp, "ADM_TRAN");

            this.onCan();
        }
    }

    /**
     * hl7接口
     * @param parm TParm
     * @param type String
     */
    private void sendHL7Mes(TParm parm, String type) {

    	//System.out.println("parm:"+parm);
        String caseNo = parm.getValue("CASE_NO");
        //转科
        if (type.equals("ADM_TRAN"))
        {
            String InDeptCode = parm.getValue("IN_DEPT_CODE");
     		boolean IsICU = parm.getBoolean("ICU_FLG");
    		boolean IsCCU = parm.getBoolean("CCU_FLG");
    		//CIS
            if (InDeptCode.equals("0303")||InDeptCode.equals("0304")||IsICU||IsCCU)
            {
                List list = new ArrayList();
                parm.setData("ADM_TYPE", "I");
                parm.setData("SEND_COMP", "CIS");
                list.add(parm);
                TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
                if (resultParm.getErrCode() < 0)
                    this.messageBox(resultParm.getErrText());
            }
            //血糖
            List list = new ArrayList();
            parm.setData("ADM_TYPE", "I");
            parm.setData("SEND_COMP", "NOVA");
            list.add(parm);
            TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
            if (resultParm.getErrCode() < 0)
                this.messageBox(resultParm.getErrText());
        }
        //出院
        if (type.equals("ADM_OUT"))
        {
     		boolean IsICU = parm.getBoolean("ICU_FLG");
    		boolean IsCCU = parm.getBoolean("CCU_FLG");
    		//CIS
    		if (IsICU||IsCCU)
    		{
              List list = new ArrayList();
              parm.setData("ADM_TYPE", "I");
              parm.setData("SEND_COMP", "CIS");
              list.add(parm);
              TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
              if (resultParm.getErrCode() < 0)
                messageBox(resultParm.getErrText());
    		}
            //血糖
            List list = new ArrayList();
            parm.setData("ADM_TYPE", "I");
            parm.setData("SEND_COMP", "NOVA");
            list.add(parm);
            TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
            if (resultParm.getErrCode() < 0)
                this.messageBox(resultParm.getErrText());
        }
    }

    /**
     * 关闭
     */
    public void onCan(){
        this.closeWindow();
    }

}
