package com.javahis.system.choice;

import com.dongyang.control.TControl;

import jdo.med.MedNodifyTool;
import jdo.sys.OperatorTool;
import com.dongyang.util.TSystem;
import jdo.sys.MessageTool;
import jdo.sys.SYSNewRegionTool;
import jdo.sys.SYSRegionTool;
import com.dongyang.ui.TComponent;
import com.dongyang.config.TRegistry;
import com.dongyang.config.TConfig;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import java.util.List;
import jdo.sys.Operator;
import jdo.sys.SYSOperatorTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;


/**
 *
 * <p>
 * Title: 登录面板控制类
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author lzk 2008.9.26
 * @version 1.0
 */
public class TChoicePanelControl extends TControl {
    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        TSystem.setObject("MessageObject", new MessageTool());
        String defaultRegion = SYSRegionTool.getInstance().getRegionByIP(
                OperatorTool.getInstance().getUserIP());
        this.callFunction("UI|REGION|setValue", defaultRegion);
        TComponent com = (TComponent) callFunction("UI|getParentComponent");
        //为了直接CS登录调试DEBUG
        if( null!= com ){
        	  com.callFunction("addEventListener", "start", this, "start");
        }
        String userID = TRegistry
                .get("HKEY_CURRENT_USER\\Software\\JavaHis\\Login\\UserID");
        String language = TRegistry
                .get("HKEY_CURRENT_USER\\Software\\JavaHis\\Login\\Language");
        TSystem.setObject("ZhFontSizeProportion", StringTool.getDouble(TConfig
                .getSystemValue("ZhFontSizeProportion")));
        TSystem.setObject("EnFontSizeProportion", StringTool.getDouble(TConfig
                .getSystemValue("EnFontSizeProportion")));

        if (userID != null) {
            setValue("USER_ID", userID);
            callFunction("UI|DEPT|onQuery");
            onDefDept();
            callFunction("UI|STATION|onQuery");
            onDefStation();
            //============add by wanglong 20131122
            TParm optParm = SYSOperatorTool.getInstance().selectdata(userID);
            if (optParm.getErrCode() < 0) {
                this.messageBox(optParm.getErrText());
                return;
            }
            if (optParm.getCount("REGION_CODE") <= 0) {
                this.messageBox("无此用户");
                return;
            }
            this.callFunction("UI|USER_NAME|setValue", optParm.getValue("USER_NAME", 0));
            //===========add end
        }
        if (language == null)
            language = "zh";
        setValue("languageCombo_0", language);
        onChangeLanguage();


        //
        //messageBox_(" ### " +TSystem.getObject("LDAPUSER") );
        String ldapuser = (String)TSystem.getObject("LDAPUSER");
        String ldappw = (String)TSystem.getObject("LDAPPW");
        if( null!=ldapuser && !"".equals(ldapuser) && null!=ldappw && !"".equals(ldappw) ){
            setValue("USER_ID", ldapuser);
            this.selRegion();//add by wanglong 20131220 执行用户名输入框的回车事件，才能带出ID对应的人名
            setValue("PASSWORD", ldappw);
        }

    }

    /**
     * 开始
     */
    public void start() {
        if (getText("USER_ID").length() > 0)
            grabFocus("PASSWORD");
        else
            grabFocus("USER_ID");
    }

    /**
     * 设置用户的默认主科室
     */
    public void onDefDept() {
        String defDept = OperatorTool.getInstance().getMainDept(
                getText("USER_ID"));
        this.callFunction("UI|DEPT|setValue", defDept);
    }

    /**
     * 设置用户的默认病区
     */
    public void onDefStation() {
        String defDept = OperatorTool.getInstance().getMainStation(
                getText("USER_ID"));
        this.callFunction("UI|STATION|setValue", defDept);
        // ============ add by wanglong 20131122
        TParm optParm = SYSOperatorTool.getInstance().selectdata(getText("USER_ID"));
        if (optParm.getErrCode() < 0) {
            this.messageBox(optParm.getErrText());
            return;
        }
        if (optParm.getCount("REGION_CODE") <= 0) {
            this.messageBox("无此用户");
            return;
        }
        this.callFunction("UI|USER_NAME|setValue", optParm.getValue("USER_NAME", 0));
        // ============ add end
    }

    /**
     * 登录
     */
	public void onLogin() {

        String optCode = this.getValueString("USER_ID");
        TParm optParm = SYSOperatorTool.getInstance().selectdata(optCode);
        if (optParm.getErrCode() < 0) {
            this.messageBox(optParm.getErrText());
            return;
        }
        /**
         * if (optParm.getCount("REGION_CODE") <= 0) { this.messageBox("无此用户");
         * return; } String optRegion = optParm.getValue("REGION_CODE", 0); if
         * (!optRegion.equals(this.getValue("REGION"))) {
         * this.messageBox("登录区域与用户所在区域不符!"); return; }
         **/

        String userID = getText("USER_ID");
        if (userID == null || userID.length() == 0) {
            messageBox_("请输入用户名称");
            callFunction("UI|USER_ID|grabFocus");
            return;
        }


        //# - 启动ldap验证
        boolean isLdap = SYSNewRegionTool.getInstance().isLDAP(Operator.getRegion());

        //messageBox_(" ======= isLdap: " + isLdap );

        this.syncLDAPUserPassword(isLdap, userID);

        this.doGeneralLogin(optCode, optParm);
        this.checkIp() ;
    }

    /**
     *
     * @param isLdap
     * @param userID
     */
	@SuppressWarnings("unchecked")
	private void syncLDAPUserPassword(boolean isLdap, String userID){
		//messageBox_(" ======= +++" +isLdap);
		if( isLdap ){

			String pw = getText("PASSWORD") ;

			//
			TParm parm1 = new TParm();
			parm1.setData("USERID", userID);
			parm1.setData("PASSWORD", pw);
			//messageBox_(" ======= isLdap - pw --- in : " + pw );
			TParm result = null;
			result = TIOM_AppServer.executeAction("action.sys.LDAPLoginAction", "doLDAPLogin", parm1);

			//判断WS是否正常通信
			if(  result.getErrCode()<0 ) return;

			//
			List<Object> obj = (List<Object>) result.getData("RESULT");
			//messageBox_(" ======= isLdap - re1 : " + obj.get(0) );
			//messageBox_(" ======= isLdap - re2 : " + obj.get(1) );
			if( (Boolean) obj.get(0) ){
				//messageBox_(" ======= isLdap - pw sync ");
				String decodePw = OperatorTool.getInstance().encrypt( pw );
				//
				String sqlUpdate = " UPDATE SYS_OPERATOR SET USER_PASSWORD = '"+decodePw+"' " +
				" WHERE USER_ID ='" + userID + "' AND USER_PASSWORD <> '"+decodePw+"'";

				 TParm parm =  new TParm(TJDODBTool.getInstance().update(sqlUpdate));
				 //messageBox_(" ======= isLdap - pw : " + parm.getErrCode() );
				 if( parm.getErrCode()<0 ){
				      this.messageBox("密码同步失败!");
			          return;
				 }
			}

		}

	}

    /**
	 * 普通登录
	 *
	 * @param optCode
	 * @param optParm
	 */
    private void doGeneralLogin(String optCode,TParm optParm){


        // $$====== modified by lx 2012/03/16 修改 是管理员角色的区域可空======= $$//
        TParm roleParm = new TParm(TJDODBTool.getInstance().select(
                "SELECT ROLE_ID FROM SYS_OPERATOR WHERE USER_ID='" + optCode
                        + "'"));
        //System.out.println("=======roleParm=========" + roleParm);
        if (!roleParm.getValue("ROLE_ID", 0).equals("ADMIN")) {

            if (optParm.getCount("REGION_CODE") <= 0) {
                this.messageBox("无此用户");
                return;
            }
            String optRegion = optParm.getValue("REGION_CODE", 0);
            if(!optRegion.equals("")){
                if (!optRegion.equals(this.getValue("REGION"))) {
                    this.messageBox("登录区域与用户所在区域不符!");
                    return;
                }
            }

        }
        // $$======= modified by lx 2012/03/16 修改是管理员角色的区域可空end=== $$//

        String resgion = getText("REGION");
        // if(resgion == null || resgion.length() == 0)
        // {
        // messageBox_("请输入登录区域");
        // callFunction("UI|REGION|grabFocus");
        // return;
        // }
        /*
         * String dept = getText("DEPT"); if(dept == null || dept.length() == 0)
         * { messageBox_("请输入登录科室"); callFunction("UI|DEPT|grabFocus"); return;
         * }
         */
        // ==========pangben modify 20110428 start
        TParm resultPwd = OperatorTool.getInstance().getUserInfo(
                getText("USER_ID"));
        // ==========pangben modify 20110428 stop

        //System.out.println("======resultPwd========" + resultPwd);

        TParm result = OperatorTool.getInstance().login(getText("USER_ID"),
                getText("PASSWORD"), (String) getValue("REGION"),
                (String) getValue("DEPT"), (String) getValue("STATION"));
        if (result.getErrCode() != 0) {
        	//-3 代表提示 7天内   注册到期的提示消息
        	if (result.getErrCode() == -3) {
        		//是否 现在  注册
        		messageBox(result.getErrText());
        		//
        	}else{
	            messageBox(result.getErrText());
	            if (result.getErrCode() == -2) {
	                openDialog("%ROOT%\\config\\system\\choice\\RegistryDialog.x");
	                return;
	            }
	            callFunction("UI|PASSWORD|grabFocus");
	            return;
        	}
        }
        // 密码验证有效日期
        // ==========pangben modify 20110428 start
        // ========查找此用户的密码启用时间以及终止时间
        Timestamp loginTime = resultPwd.getTimestamp("PWD_STARTDATE", 0);

        if ( null == loginTime || loginTime.equals("") ) {
            this.messageBox("用户第一次登录,请修改密码");
            // 第一次登录要求用户更新密码,显示更新密码界面
            this.openDialog("%ROOT%\\config\\sys\\SYSUpdatePassword.x", "Y");
        }
        // 用户第一次登录，点击X不可以登陆
        resultPwd = OperatorTool.getInstance().getUserInfo(getText("USER_ID"));
        loginTime = resultPwd.getTimestamp("PWD_STARTDATE", 0);
        if ( null == loginTime || loginTime.equals("") )  {
            this.messageBox("用户第一次登录,请修改密码");
            return;
        }


        if (resultPwd != null) {
            // 密码失效不可以登录
            if (OperatorTool.getInstance().affecttedTimePwd(resultPwd)) {
                this.messageBox("密码超过有效期，请管理员设置新密码");
                return;
            }
            if (null != loginTime && !loginTime.equals("")) {

                // 密码超过三个月提示用户更新密码
                TParm parm = new TParm();
                // 校验密码天数
                if (Operator.getRegion() != null
                        && Operator.getRegion().length() > 0) {
                    parm = new TParm(TJDODBTool.getInstance().select(
                            "SELECT DETECTPWDTIME FROM SYS_REGION WHERE REGION_CODE='"
                                    + Operator.getRegion() + "'"));
                } else
                    parm.setData("DETECTPWDTIME", 0, 999);// 没有区域登录默认校验天数999天
                if (OperatorTool.getInstance().afterThreeMonthPwd(resultPwd,
                        parm)) {
                    this.messageBox("密码已经超过" + parm.getInt("DETECTPWDTIME", 0)
                            + "天没有更新,请用户设置新密码");
                }
            }
        }
        // ==========pangben modify 20110428 end
        String language = getText("languageCombo_0");
        if (language == null || language.length() == 0)
            language = "zh";
        TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\Login\\UserID",
                getText("USER_ID"));
        TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\Login\\Language",
                language);
        callFunction("UI|P1|visible", false);
        TSystem.setObject("Language", language);
        openWindow("%ROOT%\\config\\main\\SystemMain.x");
    }

    /**
     * 双语支持
     */
    public void onChangeLanguage() {
        callFunction("UI|changeLanguage", getValue("languageCombo_0"));
    }

    /**
     * 设置登录人员所在区域
     */
    public void selRegion() {
        String optCode = this.getValueString("USER_ID");
        TParm optParm = SYSOperatorTool.getInstance().selectdata(optCode);
        if (optParm.getErrCode() < 0) {
            this.messageBox(optParm.getErrText());
            return;
        }
        if (optParm.getCount("REGION_CODE") <= 0) {
            this.messageBox("无此用户");
            return;
        }
        this.callFunction("UI|USER_NAME|setValue", optParm.getValue("USER_NAME", 0));// add by wanglong 20131122
        String optRegion = optParm.getValue("REGION_CODE", 0);
        this.callFunction("UI|REGION|setValue", optRegion);
        this.grabFocus("PASSWORD");
    }
    //===================================add  by  chenxi 20140324
    /**
     * 通过校验用户的ip
     * 更新用户名MED_NODIFY_CONFIG
     */
    public void checkIp(){
    	TParm queryParm = new TParm() ;
    	queryParm.setData("SKT_USER", Operator.getID()) ;  
    	queryParm.setData("OPT_TERM", Operator.getIP()) ;
    		  TParm result = TIOM_AppServer.executeAction("action.med.MedNodifyAction", 
    			      "onUpdateName", queryParm);
    		  if(result.getErrCode()<0)
    			 return ;
    	}
    }

