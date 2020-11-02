package com.javahis.ui.reg;

import javax.swing.JOptionPane;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.javahis.system.combo.TComboSession;

import jdo.sys.SystemTool;

/**
 *
 * <p>Title:周班转日班控制类 </p>
 *
 * <p>Description:周班转日班控制类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.18
 * @version 1.0
 */
public class REGSchWeekForDayControl
    extends TControl {
    TParm data;
    int selectRow = -1;
    String clinictypeCode;
    public void onInit() {
        super.onInit();
        //初始化REGION登陆默认登录区域
        //===pangben modify 20110410
        callFunction("UI|REGION_CODE|setValue", Operator.getRegion());
        this.setValue("ADM_DATE_START", SystemTool.getInstance().getDate());
        this.setValue("ADM_DATE_END", SystemTool.getInstance().getDate());
        Object obj = this.getParameter();
		if (obj instanceof TParm) {
			TParm parm = (TParm) obj;
			this.setValue("ADM_TYPE", parm.getValue("ADM_TYPE"));
			this.setValue("DEPT_CODE", parm.getValue("DEPT_CODE"));
			this.setValue("DR_CODE", parm.getValue("DR_CODE"));
			this.setValue("SESSION_CODE", parm.getValue("SESSION_CODE"));
			clinictypeCode= parm.getValue("CLINICTYPE_CODE");
			TComboSession session = (TComboSession) this.getComponent("SESSION_CODE");
			session.onQuery();
		}

    }

    /**
     * 新增
     */
    public void onInsert() {
        TParm parm = getParmForTag("REGION_CODE;ADM_TYPE;ADM_DATE_START:Timestamp;ADM_DATE_END:Timestamp;DEPT_CODE;DR_CODE;SESSION_CODE");
      //add by huangtt 2015003 start
        if(parm.getValue("SESSION_CODE").length() == 0){
    	   if (JOptionPane.showConfirmDialog(null, "时段为空，是否继续？", "信息",
					JOptionPane.YES_NO_OPTION) != 0) {
    		   return;
			}
       }
       if(parm.getValue("DEPT_CODE").length() == 0){
    	   if (JOptionPane.showConfirmDialog(null, "科室为空，是否继续？", "信息",
					JOptionPane.YES_NO_OPTION) != 0) {
    		   return;
			}
       }
       if(parm.getValue("DR_CODE").length() == 0){
    	   if (JOptionPane.showConfirmDialog(null, "医生为空，是否继续？", "信息",
					JOptionPane.YES_NO_OPTION) != 0) {
    		   return;
			}
       }
     //add by huangtt 2015003 end
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("CLINICTYPE_CODE", clinictypeCode);
        TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
            "schWeekForDay", parm);
        
        if (result.getErrCode() != 0) {
            messageBox(result.getErrText());
            return;
        }
        //提示信息“执行成功” 
        this.messageBox("P0005");
        this.closeWindow();

    }

    /**
     * 保存
     */
    public void onSave() {
        onInsert();
    }

    /**
     *清空
     */
    public void onClear() {
        clearValue(
            "ADM_DATE_START;ADM_DATE_END;REGION_CODE;ADM_TYPE;DEPT_CODE;DR_CODE;SESSION_CODE");
        callFunction("UI|REGION_CODE|setValue", Operator.getRegion());
        this.setValue("ADM_DATE_START", SystemTool.getInstance().getDate());
        this.setValue("ADM_DATE_END", SystemTool.getInstance().getDate());

    }
}
