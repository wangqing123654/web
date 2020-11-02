package com.javahis.ui.onw;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.reg.PatAdmTool;
import jdo.reg.SchDayTool;
import jdo.reg.SessionTool;
import jdo.sys.Operator;
import jdo.sys.PATLockTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatRegClinicArea;
import com.javahis.ui.sys.LEDONWUI;
import com.javahis.util.DateUtil;
import com.javahis.util.EmrUtil;
import com.javahis.util.OdoUtil;

/**
 * <p>Title: 护士分诊界面</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis </p>
 *
 * @author JiaoY 2008.09.28
 * @version 1.0
 */
public class ONWRegNurseStationControl
    extends TControl {
    TParm dataM, comboldata, dataD;
    int selectRowL = -1;
    int selectRowR = -1;
    private String admType = "O";
    private TParm eventParmEmr;//结构化病历返回的方法列表
    private TParm parmEKT; //医疗卡信息 yanjing 20140422
 // 病患对象
	private Pat pat; //病患信息 yanjing 20140704
	 private TTextField MR_NO;// yanjing 20140707
	 private TTextField NAME;// yanjing 20140707
	 /**
		 * TABLE右侧
		 */
		private static String TABLE = "RTABLE";
		 /**
		 * TABLE左侧
		 */
		private static String TABLEL = "LTABLE";
    
    /**
	 * 跑马灯==yanjing 2014-4-2
	 * 
	 */
    private LEDONWUI ledMedUi;
//	private LEDMEDUI ledMedUi;
	/**
	 * 跑马灯参数
	 */
	private TParm ledParm;
	TTextFormat clinicAreaCode;//诊区add by yanjing 20151104
	
    public void onInit() {
        super.onInit();
        admType = (String)this.getParameter(); //从界面接参 “O”门诊 “E”急诊
        if (admType == null || "".equals(admType)) {
            this.messageBox("未设置门急参数");
        }
        callFunction("UI|LTABLE|addEventListener",
                     "LTABLE->" + TTableEvent.CLICKED, this, "onLTABLEClicked");
        callFunction("UI|RTABLE|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this, "onClickBox");
        addEventListener("RTABLE->" + TTableEvent.CHANGE_VALUE,
                         "onChangeValue");
        this.setUI();
        this.onQuery();
        openLEDMEDUI();//打开跑马灯
    }

    /**
     * UI初始化
     */
    public void setUI() {
        this.callFunction("UI|detach|setEnabled", false); //分诊
        this.callFunction("UI|patdata|setEnabled", false); //病患信息
//        this.callFunction("UI|card|setEnabled", false); //读卡
//        this.callFunction("UI|revisit|setEnabled", false); //回诊
        callFunction("UI|setTitle", "O".equals(admType) ? "门诊护士站" : "急诊护士站");
        setValue("REGION_CODE", Operator.getRegion());
        setValue("ADM_TYPE", admType);
        //初始化日期
        Timestamp time = SystemTool.getInstance().getDate();
        setValue("ADM_DATE", time);
        //初始化时段Combo,取得默认时段
        String defSession = SessionTool.getInstance().getDefSessionNow_New(admType,Operator.getRegion());
        setValue("SESSION_CODE", defSession);
        //初始化得到默认诊区
        setValue("CLINICAREA_CODE", Operator.getStation());
        callFunction("UI|SESSION_CODE|setAdmType", admType);
        callFunction("UI|SESSION_CODE|onQuery");
        callFunction("UI|CLINICTYPE_CODE|onQuery");
        callFunction("UI|DEPT_CODE|onQuery");
        callFunction("UI|DR_CODE|onQuery");
        clinicAreaCode  = (TTextFormat) this.getComponent("CLINICAREA_CODE");// 执行科室
        TextFormatRegClinicArea combo_clinicarea = (TextFormatRegClinicArea) this
		.getComponent("CLINICAREA_CODE");
        combo_clinicarea.setDrCode(Operator.getID());
        combo_clinicarea.onQuery();
    }
    public void onSelect(){
    	clinicAreaCode  = (TTextFormat) this.getComponent("CLINICAREA_CODE");// 执行科室
        String clinicAreaSql = // add by wanglong 20131127
            "SELECT DISTINCT A.CLINICAREA_CODE AS ID, A.CLINIC_DESC AS NAME FROM REG_CLINICAREA A, SYS_OPERATOR_STATION B "
                    + " WHERE A.CLINICAREA_CODE = B.STATION_CLINIC_CODE AND B.USER_ID = '#'  ORDER BY A.CLINICAREA_CODE ";
        clinicAreaSql = clinicAreaSql.replaceFirst("#", Operator.getID());
//        System.out.println("clinicAreaSql clinicAreaSql is ::"+clinicAreaSql);
        clinicAreaCode.setPopupMenuSQL(clinicAreaSql);
    }

    /**
     * 医技进度
     */
    public void onDocplan() {
        this.openDialog("%ROOT%\\config\\onw\\ONWMEDProgress.x");
    }

    /**
     * 分诊
     */
    public void onDetach() {
        String date = StringTool.getString(TCM_Transform.getTimestamp(getValue(
            "ADM_DATE")), "yyyyMMdd"); //拿到界面的时间
        String now = StringTool.getString(SystemTool.getInstance().getDate(),
                                          "yyyyMMdd"); //拿到当前的时间
        if (!date.equals(now)) {
            this.messageBox("不可隔日分诊");
            return;
        }
        TTable RTABLE = (TTable) callFunction("UI|RTABLE|getThis"); //拿到table控件
        RTABLE.acceptText();
        int count = RTABLE.getRowCount(); //等待的病人数量
        TParm patInfo = new TParm();
        int t = 0;
        for (int i = 0; i < count; i++) {
            if (!"Y".equals(RTABLE.getValueAt(i, 0)))//判断分诊标记是否选中
                continue;
            patInfo.setRowData(dataD.getRow(i));
            //----delete by huangtt 20150609 start
//            patInfo.setData("CLINICTYPE_CODE",
//                            dataM.getData("CLINICTYPE_CODE", selectRowL)); //号别
//            patInfo.setData("ADM_TYPE", admType); //门急别
            //----delete by huangtt 20150609 end
            TParm reParm = (TParm)this.openDialog(
                "%ROOT%\\config\\onw\\ONWAssign.x", patInfo);
        }

        TParm selData = new TParm();
        //----modify by huangtt 20150609 start
//        selData.setData("ADM_TYPE", dataM.getData("ADM_TYPE", selectRowL));
//        selData.setData("ADM_DATE", dataM.getData("ADM_DATE", selectRowL));
//        selData.setData("REALDEPT_CODE",
//                        dataM.getData("DEPT_CODE", selectRowL));
//        selData.setData("REALDR_CODE", dataM.getData("DR_CODE", selectRowL));
//        selData.setData("SESSION_CODE",
//                        dataM.getData("SESSION_CODE", selectRowL));
//        selData.setData("CLINICROOM_NO",
//                        dataM.getData("CLINICROOM_NO", selectRowL));
        
        selData.setData("ADM_TYPE", patInfo.getData("ADM_TYPE"));
        selData.setData("ADM_DATE", patInfo.getData("ADM_DATE").toString().replace("/", "").replace("-", "").substring(0, 8));
        selData.setData("REALDEPT_CODE",patInfo.getData("DEPT_CODE"));
        selData.setData("REALDR_CODE", patInfo.getData("DR_CODE"));
        selData.setData("SESSION_CODE",patInfo.getData("SESSION_CODE"));
        selData.setData("CLINICROOM_NO",patInfo.getData("CLINICROOM_NO"));
      //----modify by huangtt 20150609 end
        this.onSelPat(selData);
    }

    /**
     * 报告进度
     */
    public void onPlanrep() {
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
            this.messageBox_("请选择病患！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("MR_NO",dataD.getValue("MR_NO",selectedRow));
        parm.setData("CASE_NO",dataD.getValue("CASE_NO",selectedRow));
        parm.setData("PAT_NAME",dataD.getValue("PAT_NAME",selectedRow));
        parm.setData("SEX_CODE",dataD.getValue("SEX_CODE",selectedRow));
        parm.setData("DEPT_CODE",dataD.getValue("REALDEPT_CODE",selectedRow));
        parm.setData("CLINICROOM_NO",dataD.getValue("CLINICROOM_NO",selectedRow));
        parm.setData("DR_CODE",dataD.getValue("REALDR_CODE",selectedRow));
        parm.setData("ISPRINT","N");//加打印权限，护士不让打印，只有医生才能打印//add by huangjw 20141118
        parm.setData("NUR_FLG","Y");
        this.openDialog("%ROOT%\\config\\onw\\ONWPlanReport.x",parm);
    }

    /**
     * 病患资料
     */
    public void onPatdata() {
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        TParm patParm = table.getParmValue();
        String mrNo = patParm.getValue("MR_NO", table.getSelectedRow());
        TParm sendParm = new TParm();
        sendParm.setData("ONW", "ONW");
        sendParm.setData("MR_NO", mrNo);
        this.openWindow("%ROOT%\\config\\sys\\SYSPatInfo.x", sendParm);
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = getParmForTag(
            "ADM_DATE:timestamp;SESSION_CODE;DEPT_CODE;CLINICTYPE_CODE;DR_CODE;CLINICROOM_NO;ADM_TYPE;CLINICAREA_CODE", true);
        
        
        if(parm.getValue("ADM_DATE") == null || "".equals(parm.getValue("ADM_DATE").toString())){
        	this.messageBox("日期不可为空");
        	return;
        }
        dataM = new TParm();
        //===========pangben modify 20110421 start
        String regionCode=Operator.getRegion();
        if(!"".equals(regionCode)&&null!=regionCode)
            parm.setData("REGION_CODE",regionCode);
        //===========pangben modify 20110421 stop
        dataM = SchDayTool.getInstance().selectdata(parm);
        //add by huangtt 20150525 start 
        for (int i = 0; i < dataM.getCount(); i++) {
			if(dataM.getBoolean("STOP_SESSION", i)){
				dataM.removeRow(i);
			}
		}
        //add by huangtt 20150525 end
        this.callFunction("UI|LTABLE|setParmValue", dataM);
        onQueryRTable();
    }

    /**
     * 点击左边table查询医师的挂号信息
     * @param row int
     */
    public void onLTABLEClicked(int row) {
        if (row < 0) {
            return;
        }
        setValueForParm(
            "SESSION_CODE;DEPT_CODE;CLINICTYPE_CODE;CLINICROOM_NO;DR_CODE",
            dataM, row);
        selectRowL = row;
        TTable LTABLE = (TTable) callFunction("UI|LTABLE|getThis"); //拿到table控件
        TParm selData = new TParm();
        selData.setData("ADM_TYPE", dataM.getData("ADM_TYPE", selectRowL));
        selData.setData("ADM_DATE", dataM.getData("ADM_DATE", selectRowL));
        selData.setDataN("REALDEPT_CODE", dataM.getData("DEPT_CODE", selectRowL));
        selData.setDataN("REALDR_CODE", dataM.getData("DR_CODE", selectRowL));
        selData.setData("SESSION_CODE",
                        dataM.getData("SESSION_CODE", selectRowL));
        selData.setData("CLINICROOM_NO",
                        dataM.getData("CLINICROOM_NO", selectRowL));
        this.onClinic();
        this.onSelPat(selData);
        this.clearValue("SELALL");//清空全选按钮
        this.callFunction("UI|detach|setEnabled", false); //分诊
        this.callFunction("UI|patdata|setEnabled", false); //病患信息
    }

    /**
     * 显示病患信息
     * @param parm TParm
     */
    public void onSelPat(TParm parm) {
        dataD = new TParm();
        dataD = PatAdmTool.getInstance().selectdata_name(parm);
        int count = dataD.getCount("SESSION_CODE");
        for (int i = 0; i < count; i++) {
        	dataD.addData("SEQ", (i+1)+"");
            dataD.addData("SELECT", "N");
            //====计算首再诊
            String mr_no = dataD.getValue("MR_NO", i);
			String dept_code = dataD.getValue("REALDEPT_CODE",i);
			String sql = "SELECT COUNT(MR_NO) SUM FROM SYS_EMR_INDEX WHERE MR_NO = '"+mr_no+"' AND DEPT_CODE = '"+dept_code+"'";
			//添加性别和年龄=====================add by huangjw 20140923 start
			dataD.addData("PAT_AGE", culmulateAge(dataD.getTimestamp("BIRTH_DATE",i)));
//			dataD.addData("PAT_SEX",dataD.getValue("PAT_SEX",i));
			//添加性别和年龄=====================add by huangjw 20140923 end
			//add by wuxy 2017/9/11 新增出生日期
			Timestamp birthDay = dataD.getTimestamp("BIRTH_DATE",i);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			dataD.addData("BIRTH_DAY",sdf.format(birthDay));
			//end wuxy
			TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
			if(selParm.getInt("SUM", 0)<=1){
				 dataD.addData("FIRST_FLG",  "首诊");//首诊
			}else{
				 dataD.addData("FIRST_FLG", "再诊");//再诊
			}
			if(dataD.getData("LMP_DATE", i)!=null&&
					!"".equals(dataD.getData("LMP_DATE", i))){//先判断就诊表里的LMP_DATE
				dataD.addData("PREGNANT_WEEKS", OdoUtil.getPreWeekNew(dataD.getTimestamp(
						"ADM_DATE", i), dataD.getTimestamp("LMP_DATE",i )));//====计算孕周
				/*dataD.addData("PREGNANT_WEEKS", OdoUtil.getPreWeek(dataD.getTimestamp(
						"LMP_DATE", i), dataD.getTimestamp("ADM_DATE",i ))+""+"周");//====计算孕周
*/			}else if(dataD.getData("PAT_LMP_DATE", i)!=null&&
					!"".equals(dataD.getData("PAT_LMP_DATE", i))){//其次判断
				dataD.addData("PREGNANT_WEEKS", OdoUtil.getPreWeekNew(dataD.getTimestamp(
						"ADM_DATE", i), dataD.getTimestamp("PAT_LMP_DATE",i )));//====计算孕周
			}else{
				dataD.addData("PREGNANT_WEEKS", "");//====没有LMP_DATE的孕周为空
			}
            
            //====zhangp 20120227 modify start
            if(dataD.getData("REG_ADM_TIME", i)!=null){
            	String admTime =
            		dataD.getData("REG_ADM_TIME", i).toString().substring(0,2)+":"+dataD.getData("REG_ADM_TIME", i).toString().substring(2,4);
            	dataD.setData("REG_ADM_TIME", i , admTime);
            }
            //======zhangp 20120227 modify end
        }
        
        this.clearValue("RTABLE");
        this.callFunction("UI|RTABLE|setParmValue", dataD);
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        for(int i = 0; i < table.getParmValue().getCount(); i++){
        	table.removeRowColor(i);
        	if(table.getParmValue().getInt("COUNT",i) > 0){
        		table.setRowColor(i, Color.LIGHT_GRAY);
        	}else{
        		table.setRowColor(i, Color.WHITE);
        	}
        }
    }
    

    
    /**
     * 通过诊室查询诊区
     */
    public void onClinic() {
        TParm parm = new TParm();
        parm.setData("CLINICROOM_NO", getValue("CLINICROOM_NO").toString().trim());
        parm.setData("SESSION_CODE", getValue("SESSION_CODE").toString().trim());
        parm.setData("ADM_DATE", getValue("ADM_DATE"));
        TParm result = SchDayTool.getInstance().SELECT_REG_SCHDAY_CLINICROOM(
            parm);
        setValue("CLINICAREA_CODE", result.getValue("CLINICAREA_CODE", 0));
    }
    
	/**
	 * 病案号回车事件
	 */
	public void onMrNo(){
		 MR_NO = (TTextField)this.getComponent("MR_NO");
	     String mrNo = MR_NO.getValue();
	     MR_NO.setValue(PatTool.getInstance().checkMrno(mrNo));
	     //得到病患名字
	     getPatName(mrNo);
	     this.onChooseCaseNo(MR_NO.getValue());
	}
	
	/**
	  * 获得该病人的姓名
	  * @param mrNo String
	  */
	 private void getPatName(String mrNo){
		 NAME = (TTextField)this.getComponent("PAT_NAME");
	     NAME.setValue(PatTool.getInstance().getNameForMrno(mrNo));
	 }
    
    /**
	 * 到诊
	 */
	public void onVisit(){
		TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
        	this.messageBox("请先选中病患");
        	return;
        }
//		parmEKT = EKTIO.getInstance().TXreadEKT();
//	    if (null == parmEKT || parmEKT.getErrCode() < 0 ||
//	    		parmEKT.getValue("MR_NO").length() <= 0) {
//	    	this.messageBox(parmEKT.getErrText());
//	    	parmEKT = null;
//	    	return;
//	       }
//	    String mrno = parmEKT.getValue("MR_NO");
//	    System.out.println("parmEKT parmEKT parmEKT is ::"+parmEKT);
	    String caseNo = dataD.getData("CASE_NO",selectedRow).toString();
	    //根据Case_no查询挂号信息，并写报到时间
	    String time = SystemTool.getInstance().getDate().toString().substring(0, 19).replace("/", "")
        .replace("-", "").replace(" ", "").replace(":", "");
	    String regSql = "UPDATE REG_PATADM SET ARRIVE_DATE = TO_DATE('"+time+"','YYYYMMDDHH24MISS') WHERE CASE_NO = '"+caseNo+"'";
	    TParm regParm = new TParm (TJDODBTool.getInstance().update(regSql));
	    if(regParm.getErrCode()<0){
	    	this.messageBox("更新报到时间出错。");
	    	return;
	    }else{
	    	this.messageBox("操作成功");
//	    	if(parmEKT != null && parmEKT.getCount()>0){//读了医疗卡
	    		this.getPatAndDcInFo(caseNo,false);	
//	    	}else{
//	    		TParm selData = new TParm();
//		        selData.setData("ADM_TYPE", dataM.getData("ADM_TYPE", selectRowL));
//		        selData.setData("ADM_DATE", dataM.getData("ADM_DATE", selectRowL));
//		        selData.setDataN("REALDEPT_CODE", dataM.getData("DEPT_CODE", selectRowL));
//		        selData.setDataN("REALDR_CODE", dataM.getData("DR_CODE", selectRowL));
//		        selData.setData("SESSION_CODE",
//		                        dataM.getData("SESSION_CODE", selectRowL));
//		        selData.setData("CLINICROOM_NO",
//		                        dataM.getData("CLINICROOM_NO", selectRowL));
//		        this.onClinic();
//		        this.onSelPat(selData);
//	    	}
//	    	this.onLTABLEClicked(selectRowL);
	    }
	    
	}
	/**
	 * 回诊
	 */
	 public void onRevisit() {
		 TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
	        int selectedRow = table.getSelectedRow();//获取选中行
	        if(selectedRow<0){
	        	this.messageBox("请先选中病患");
	        	return;
	        }
	        String time = SystemTool.getInstance().getDate().toString().substring(0, 19).replace("/", "")
	        .replace("-", "").replace(" ", "").replace(":", "");
	        String caseNo = dataD.getData("CASE_NO",selectedRow).toString();
		    //根据Case_no查询挂号信息，并写报到时间
		    String regSql = "UPDATE REG_PATADM SET REVISIT_DATE = TO_DATE('"+time+"','YYYYMMDDHH24MISS') WHERE CASE_NO = '"+caseNo+"'";
		    TParm regParm = new TParm (TJDODBTool.getInstance().update(regSql));
		    if(regParm.getErrCode()<0){
		    	this.messageBox("更新报到时间出错。");
		    	return;
		    }else{
		    	this.messageBox("操作成功");
//		    	this.onLTABLEClicked(selectRowL);
//		    	if(parmEKT != null && parmEKT.getCount()>0){//读了医疗卡
		    		this.getPatAndDcInFo(caseNo,true);	
//		    	}else{
//		    		TParm selData = new TParm();
//			        selData.setData("ADM_TYPE", dataM.getData("ADM_TYPE", selectRowL));
//			        selData.setData("ADM_DATE", dataM.getData("ADM_DATE", selectRowL));
//			        selData.setDataN("REALDEPT_CODE", dataM.getData("DEPT_CODE", selectRowL));
//			        selData.setDataN("REALDR_CODE", dataM.getData("DR_CODE", selectRowL));
//			        selData.setData("SESSION_CODE",
//			                        dataM.getData("SESSION_CODE", selectRowL));
//			        selData.setData("CLINICROOM_NO",
//			                        dataM.getData("CLINICROOM_NO", selectRowL));
//			        this.onClinic();
//			        this.onSelPat(selData);
//		    	}
		    }
	 }


    /**
     * 清空
     */
    public void onClear() {
//        this.setValue("CLINICAREA_CODE", "");
        this.setValue("DEPT_CODE", "");
        this.setValue("CLINICTYPE_CODE", "");
        this.setValue("CLINICROOM_NO", "");
        this.setValue("DR_CODE", "");
        this.callFunction("UI|LTABLE|removeRowAll");
        this.callFunction("UI|RTABLE|removeRowAll");
        this.setValue("MR_NO", "");
        this.setValue("PAT_NAME", "");
        this.setValue("EKT_AMT", "");
        this.callFunction("UI|detach|setEnabled", false); //分诊
        this.callFunction("UI|patdata|setEnabled", false); //病患信息
        setValue("CLINICAREA_CODE", Operator.getStation());//诊区
    }

    /**
     * 属性table改变值
     * @param obj Object
     * @return boolean
     */
    public boolean onChangeValue(Object obj) {
        TTable table = (TTable) callFunction("UI|RTABLE|getThis"); //拿到table控件
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
//        int selectRow = node.getRow();
//        String admStatus = table.getValueAt(selectRow, 5).toString();
//        if (!admStatus.equals("1")) {
//            this.messageBox("病患 " + table.getValueAt(selectRow, 3) + " 不可分诊");
//            return true;
//        }
        this.callFunction("UI|detach|setEnabled", true);
        this.callFunction("UI|patdata|setEnabled", true); //病患信息
        //判断表格中是否还有其他的选中行
        boolean flg = false;
        if("N".equals(node.getValue().toString())){
            for(int i=0;i<table.getRowCount();i++){
                if("Y".equals(table.getItemString(i,0))&&i!=node.getRow()){
                    flg = true;
                }
            }
            //如果没有其他的选中行 那么分诊按钮 设置为不可编辑
            if (!flg) {
                this.callFunction("UI|detach|setEnabled", false);
            }
        }
        this.clearValue("SELALL");//清空全选按钮
        return false;
    }

    /**
     * table 中 checkbox 事件
     * @param object Object
     */
    public void onClickBox(Object object) {
        TTable obj = (TTable) object;
        obj.acceptText();
    }

    /**
     * 病患table 选中  事件
     */
    public void onPatInfo() {
//    	this.callFunction("UI|card|setEnabled", true); //读卡
//        this.callFunction("UI|revisit|setEnabled", true); //回诊
    	TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
//        System.out.println("病患table 选中  事件 病患table 选中  事件 is ::"+dataD);
    	String mrNo = dataD.getData("MR_NO",selectedRow).toString();
    	String patName = dataD.getData("PAT_NAME",selectedRow).toString();
    	double currentBalance = EKTpreDebtTool.getInstance().getEkeMaster(mrNo);
    	String currentBalanceN = Double.toString(currentBalance);
    	String caseNo = dataD.getData("CASE_NO", selectedRow).toString();
    	this.setValue("OPD_AMT", getOpdAmt(caseNo));  //add by huangtt 20141119
    	this.setValue("EKT_AMT",currentBalanceN);
    	this.setValue("MR_NO",mrNo);
    	this.setValue("PAT_NAME",patName);
        this.callFunction("UI|patdata|setEnabled", true);
        this.callFunction("UI|detach|setEnabled", true);
        this.callFunction("UI|patdata|setEnabled", true); //病患信息
        for(int i = 0;i<table.getRowCount();i++){
        	if(i==selectedRow){
        		table.setItem(i, "SELECT", "Y");
        	}else{
        		table.setItem(i, "SELECT", "N");
        	}
        }
        this.clearValue("SELALL");//清空全选按钮

        
       
    }
    /**
     * 体征采集查询
     * yanjing  20130326
     */
    
    public void onBodyCheck(){
    	
    	 TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
         int row = table.getSelectedRow();
         if(row<0){
             this.messageBox_("请选择病患！");
             return;
         }
         TParm parm = new TParm();
         TParm emrParm = new TParm();
         String caseNo = dataD.getValue("CASE_NO",row);
         String mrNo = dataD.getValue("MR_NO",row);
         emrParm.setData("MR_CODE", TConfig.getSystemValue("ONWEmrMRCODE"));
         emrParm.setData("CASE_NO", caseNo);
         //
         emrParm.setData("DEPT_CODE", dataD.getValue("REALDEPT_CODE",row));
         //
         emrParm = EmrUtil.getInstance().getEmrFilePath(emrParm);
         parm.setData("SYSTEM_TYPE", "ONW");
         parm.setData("ADM_TYPE", admType);
         parm.setData("CASE_NO", caseNo);
         parm.setData("PAT_NAME", dataD.getValue("PAT_NAME",row));
         parm.setData("MR_NO", dataD.getValue("MR_NO",row));
         parm.setData("ADM_DATE", dataD.getTimestamp("ADM_DATE",row));
         parm.setData("DEPT_CODE", dataD.getValue("REALDEPT_CODE",row));
         parm.setData("EMR_FILE_DATA", emrParm);
         parm.setData("RULETYPE","2");//修改权限
         parm.addListener("EMR_LISTENER",this,"emrListener");
         parm.addListener("EMR_SAVE_LISTENER",this,"emrSaveListener");
//         this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    	
    	 //调用体征采集查询界面
        String SQL = "SELECT PAT_NAME FROM SYS_PATINFO WHERE MR_NO = '"+mrNo+"'";
        TParm nameParm = new TParm(TJDODBTool.getInstance().select(SQL));
        String patName = nameParm.getValue("PAT_NAME",0);
        parm.setData("CASE_NO", caseNo);
        parm.setData("PAT_NAME", patName);
        //parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
        this.openDialog("%ROOT%\\config\\emr\\EMRSignsCollect.x", parm);
    	
    }

    /**
     * 全选
     */
    public void onSelectAll() {
        TTable table = (TTable) callFunction("UI|RTABLE|getThis"); //拿到table控件
//        Boolean check = false;
        int count = table.getRowCount();
        for (int i = 0; i < count; i++) {
            if (table.getValueAt(i, table.getColumnIndex("ADM_STATUS")).equals("1")) {//"1"未看诊，只有未看诊才可以分诊
                table.setValueAt(getValue("SELALL"), i, 0);
//                check = true;
            }
        }
        if ("Y".equals(this.getValueString("SELALL"))){
            this.callFunction("UI|detach|setEnabled", true);
            this.callFunction("UI|patdata|setEnabled", false);
        }
        else{
            this.callFunction("UI|detach|setEnabled", false);
        }
    }
    /**
     * 时段Combo选择事件 清空以时段为条件查询的Combo
     */
    public void onSESSION_CODE(){
        //清空科别，诊室，看诊医师 三个combo
        this.clearValue("DEPT_CODE;CLINICROOM_NO;DR_CODE");
    }
    /**
     * 清空医师combo
     */
    public void clearDr_CODE(){
        this.clearValue("DR_CODE");
    }
    /**
     * 日期改变事件
     */
    public void onADM_DATE_Selected(){
        this.clearValue("SESSION_CODE;CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO");
        //初始化时段Combo,取得默认时段
        String defSession = SessionTool.getInstance().getDefSessionNow(admType,Operator.getRegion());
        setValue("SESSION_CODE", defSession);
        //初始化得到默认诊区
        setValue("CLINICAREA_CODE", Operator.getStation());
        callFunction("UI|SESSION_CODE|setAdmType", admType);
        callFunction("UI|SESSION_CODE|onQuery");
        callFunction("UI|CLINICTYPE_CODE|onQuery");
        callFunction("UI|DEPT_CODE|onQuery");
    }
    /**
     * 条码打印
     */
    public void onBarcode(){
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
            this.messageBox_("请选择病患！");
            return;
        }
        if(dataD.getInt("ADM_STATUS",selectedRow)<=1){
            this.messageBox_("该病患尚未看诊，不可打印！");
            return;
        }
        //参数
        TParm parm = new TParm();
        parm.setData("DEPT_CODE", dataD.getValue("REALDEPT_CODE", selectedRow)); //科室
        parm.setData("ADM_TYPE", admType); //门急住别
        parm.setData("CASE_NO", dataD.getValue("CASE_NO", selectedRow)); //CASE_NO
        parm.setData("MR_NO", dataD.getValue("MR_NO", selectedRow)); //MR_NO
        parm.setData("PAT_NAME", dataD.getValue("PAT_NAME", selectedRow)); //病患姓名
        parm.setData("ADM_DATE", dataD.getTimestamp("ADM_DATE", selectedRow)); //就诊日期
        parm.setData("CLINICAREA_CODE",
                     dataD.getValue("CLINICAREA_CODE", selectedRow)); //诊区
        parm.setData("CLINICROOM_NO",
                     dataD.getValue("CLINICROOM_NO", selectedRow)); //诊室
        parm.setData("POPEDEM", "1"); //一般权限
        this.openDialog("%ROOT%\\config\\med\\MEDApply.x", parm);
    }
    /**
     * 体征采集  调用机构化病历
     */
    public void onBody(){
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int row = table.getSelectedRow();
        if(row<0){
            this.messageBox_("请选择病患！");
            return;
        }
        TParm parm = new TParm();
        TParm emrParm = new TParm();
        String caseNo = dataD.getValue("CASE_NO",row);
        emrParm.setData("MR_CODE", TConfig.getSystemValue("ONWEmrMRCODE"));
        emrParm.setData("CASE_NO", caseNo);
        //
        emrParm.setData("DEPT_CODE", dataD.getValue("REALDEPT_CODE",row));
        //
        //
        //从数库   配置文件中取体征采集模版
        TParm result = new TParm();
        String subClassCode=TConfig.getSystemValue("ONWEmrMRCODE");
        // 2.取得科室制定的模板(门诊)
        String sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
            + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
            + " B.EMT_FILENAME FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
            + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
            + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '3' "
            + " AND DEPTORDR_CODE = '" + dataD.getValue("REALDEPT_CODE",row) +
            "' AND B.OPD_FLG = 'Y' "
            +" AND B.SUBCLASS_CODE = '" + subClassCode+"'"
            +" AND A.MAIN_FLG = 'Y'"
            + " ORDER BY MAIN_FLG DESC ";
        //System.out.println("-----取得科室制定的模板(门诊)sql11------"+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        //无个人模版时，取科室模版
        if (result == null || result.getCount() <= 0) {
        	// 1.取得医生定制的模板(门诊)
            
             sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
                + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
                + " B.EMT_FILENAME FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
                + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
                + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '4' "
                + " AND DEPTORDR_CODE = '" + Operator.getID() +
                "' AND B.OPD_FLG = 'Y' "
                +" AND B.SUBCLASS_CODE = '" + subClassCode+"'"
                +" AND A.MAIN_FLG = 'Y'"
                + " ORDER BY MAIN_FLG DESC ";
            //System.out.println("-----取得护士个人制定的模板(门诊)sql11------"+sql);
            result = new TParm(TJDODBTool.getInstance().select(sql));
        }
        //存在定制模版
        if (result!=null&&result.getCount()> 0) {
        	//System.out.println("-------SEQ------"+result.getValue("SEQ", 0));
        	emrParm.setData("SEQ", result.getValue("SEQ", 0));
        }
        //       
        emrParm = EmrUtil.getInstance().getEmrFilePath(emrParm);
        parm.setData("SYSTEM_TYPE", "ONW");
        parm.setData("ADM_TYPE", admType);
        parm.setData("CASE_NO", caseNo);
        parm.setData("PAT_NAME", dataD.getValue("PAT_NAME",row));
        parm.setData("MR_NO", dataD.getValue("MR_NO",row));
        parm.setData("ADM_DATE", dataD.getTimestamp("ADM_DATE",row));
        parm.setData("DEPT_CODE", dataD.getValue("REALDEPT_CODE",row));
        parm.setData("EMR_FILE_DATA", emrParm);
        parm.setData("RULETYPE","2");//修改权限LMP_DATE
        if(table.getParmValue().getTimestamp("LMP_DATE", table.getSelectedRow()) != null){
        	parm.setData("PRE_WEEK",OdoUtil.getPreWeekNew(table.getParmValue().getTimestamp(
    				"ADM_DATE", table.getSelectedRow()), table.getParmValue().getTimestamp("LMP_DATE",table.getSelectedRow())));//妇产科体征采集自动抓取孕周
        }else if(table.getParmValue().getTimestamp("PAT_LMP_DATE", table.getSelectedRow()) != null){
        	parm.setData("PRE_WEEK",OdoUtil.getPreWeekNew(table.getParmValue().getTimestamp(
    				"ADM_DATE", table.getSelectedRow()), table.getParmValue().getTimestamp("PAT_LMP_DATE",table.getSelectedRow())));//妇产科体征采集自动抓取孕周
        }else{
        	parm.setData("PRE_WEEK","-");//妇产科体征采集自动抓取孕周
        }
        parm.setData("WEIGHT","0.00");
        parm.setData("HEIGHT","0.00");
        parm.setData("HC","");
        
        
        Timestamp date = SystemTool.getInstance().getDate();
        Timestamp yesterday=StringTool.rollDate(date, -1);;
        String sDate = yesterday.toString().substring(0, 10).replaceAll(
				"/", "").replaceAll("-", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//格式化对象  
        if("10".equals(result.getValue("SEQ", 0))){//妇产科体征采集
        	Calendar calendar = Calendar.getInstance();//日历对象  
    		calendar.setTime(date);//设置当前日期  
    		calendar.add(Calendar.MONTH, -6);//月份加一
    		
    		sDate = sdf.format(calendar.getTime());
        }
        if("12".equals(result.getValue("SEQ", 0))){//妇产科体征采集
        	Calendar calendar = Calendar.getInstance();//日历对象  
    		calendar.setTime(date);//设置当前日期  
    		calendar.add(Calendar.DAY_OF_WEEK, -7);
    		sDate = sdf.format(calendar.getTime());
        }
		 
		
		String eDate = sdf.format(date);
		TParm hParm = new TParm(TJDODBTool.getInstance().select("SELECT ADM_DATE,HEIGHT FROM  REG_PATADM WHERE"
						+ " MR_NO = '" + dataD.getValue("MR_NO", row)
						+ "' AND CASE_NO <> '" + caseNo
						+ "' AND ADM_DATE = (SELECT MAX (ADM_DATE) FROM REG_PATADM WHERE MR_NO = '" + dataD.getValue("MR_NO", row)
						+ "' AND ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD') AND TO_DATE ('"+eDate+"', 'YYYYMMDD') "
						+ " AND CASE_NO <> '" + caseNo+ "') "
										+ " ORDER BY REG_DATE DESC "));
		if (hParm.getCount() > 0) {
			if (hParm.getDouble("HEIGHT", 0) > 0) {
				parm.setData("HEIGHT", hParm.getDouble("HEIGHT", 0) + "");
			}

		}
        
        if("12".equals(result.getValue("SEQ", 0))){//儿科体征采集
        	
        	Calendar calendar = Calendar.getInstance();//日历对象  
    		calendar.setTime(date);//设置当前日期  
    		calendar.add(Calendar.MONTH, -1);//月份加一
    		
    		sDate = sdf.format(calendar.getTime());
        	
        	String sqlHc = "SELECT  B.CASE_NO,B.PHYSEXAM_REC" +
        			" FROM REG_PATADM A, OPD_SUBJREC B" +
        			" WHERE A.CASE_NO = B.CASE_NO" +
        			" AND A.MR_NO = '"+dataD.getValue("MR_NO", row)+"'" +
        			" AND A.CASE_NO <> '" + caseNo+"'"+
        			" AND A.ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD') AND TO_DATE ('"+eDate+"', 'YYYYMMDD')" +
        			" ORDER BY B.CASE_NO DESC";
        	TParm hcParm = new TParm(TJDODBTool.getInstance().select(sqlHc));
        	if(hcParm.getCount() > 0){
        		String phy = hcParm.getValue("PHYSEXAM_REC", 0);
        		int i=phy.indexOf("头围：");
        		if(i > 0){
        			phy = phy.substring(i); 
        			int i1 = phy.indexOf("，");
        			phy = phy.substring(0,i1);
            		int j = phy.indexOf("cm");            		
            		int k=phy.indexOf("：");
            		if(k > 0 && j > 0){
            			String hc = phy.substring(k+1, j);
                		hc=hc.replaceAll("-", "");
                		if(hc.trim().length()>0){
                			parm.setData("HC",hc.trim());
                		}
            		}else if(k > 0 && j < 0){
            			String hc = phy.substring(k+1);
                		hc=hc.replaceAll("-", "");
                		if(hc.trim().length()>0){
                			parm.setData("HC",hc.trim());
                		}
            		}
            		
        		}

        	}
        	
			TParm weightParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT ADM_DATE,WEIGHT FROM  REG_PATADM WHERE"
							+ " MR_NO = '" + dataD.getValue("MR_NO", row)
							+ "' AND CASE_NO <> '" + caseNo
							+ "' AND ADM_DATE = (SELECT MAX (ADM_DATE) FROM REG_PATADM WHERE MR_NO = '" + dataD.getValue("MR_NO", row)
							+ "' AND CASE_NO <> '" + caseNo+ "') "
							+ " ORDER BY REG_DATE DESC "));
	        if(weightParm.getCount() > 0){
	        	 sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        	try {
					Timestamp timestamp1 = new Timestamp(sdf.parse(
							weightParm.getValue("ADM_DATE",0).toString().substring(0, 10)
									.replaceAll("/", "-")
									+ " 00:00:00").getTime());
					Timestamp timestamp2 = new Timestamp(sdf.parse(
							dataD.getValue("ADM_DATE",row).toString().substring(0, 10)
									.replaceAll("/", "-")
									+ " 23:59:59").getTime());
					int a = StringTool.getDateDiffer(timestamp2, timestamp1);
					if(a <= 2 && weightParm.getDouble("WEIGHT",0) > 0){
						parm.setData("WEIGHT",weightParm.getDouble("WEIGHT",0)+"");
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
        }
        
        //过敏史
        parm.setData("ALLEGYHISTORY","");
        String d="SELECT DRUGORINGRD_CODE FROM OPD_DRUGALLERGY WHERE MR_NO='"+dataD.getValue("MR_NO", row)+"' " +
        		"AND DRUG_TYPE = 'D' AND TRIM(DRUGORINGRD_CODE) <> '-' ORDER BY ADM_DATE DESC ";
        TParm parmD = new TParm(TJDODBTool.getInstance().select(d));
        if(parmD.getCount() > 0){
        	parm.setData("ALLEGYHISTORY",parmD.getValue("DRUGORINGRD_CODE", 0));
        }
        
        parm.addListener("EMR_LISTENER",this,"emrListener");
        parm.addListener("EMR_SAVE_LISTENER",this,"emrSaveListener");
        this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    }
    /**
     * 检验报告
     */
    public void onCheckrep(){
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
            this.messageBox_("请选择病患！");
            return;
        }
        SystemTool.getInstance().OpenLisWeb(dataD.getValue("MR_NO",selectedRow));
    }
    /**
     * 检查报告
     */
    public void onTestrep(){
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
            this.messageBox_("请选择病患！");
            return;
        }
        SystemTool.getInstance().OpenRisWeb(dataD.getValue("MR_NO",selectedRow));
    }
    /**
     * 补充计价
     */
    public void onSupcharge(){
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
            this.messageBox_("请选择病患！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("MR_NO",dataD.getValue("MR_NO",selectedRow));
        parm.setData("CASE_NO",dataD.getValue("CASE_NO",selectedRow));
        parm.setData("SYSTEM","ONW");
        parm.setData("ONW_TYPE",admType);//=====pangben 2013-5-15 门急诊护士站解锁使用，监听不同的界面
//        this.openDialog("%ROOT%\\config\\opb\\OPBChargesM.x",parm);
        parm.setData("count", "1");
//      this.openDialog("%ROOT%\\config\\opb\\OPBChargesM.x", parm);//旧补充计费界面
      this.openDialog("%ROOT%\\config\\opbTest\\OPBCharge.x",parm);//新补充计费界面
        unLockPat(dataD.getValue("MR_NO",selectedRow));
    }
    /**
	 * 病患解锁
	 * ============pangben 2014-7-11
	 */
	public void unLockPat(String mr_no) {
		if (null==mr_no || mr_no.length()<=0) {
			return;
		}
		// 判断是否加锁
		if (PatTool.getInstance().isLockPat(mr_no)) {
			TParm parm = PatTool.getInstance().getLockPat(mr_no);
			if (Operator.getIP().equals(parm.getValue("OPT_TERM", 0))
					&&Operator.getID().equals(parm.getValue("OPT_USER", 0))) {
				if ("OPB".equals(parm.getValue("PRG_ID", 0))
						||"ONW".equals(parm.getValue("PRG_ID", 0))
								||"ENW".equals(parm.getValue("PRG_ID", 0))) {
					PatTool.getInstance().unLockPat(mr_no);
				}
			}
		}
	}
    /**
     * 就诊记录 查询
     */
    public void onOPDRecord(){
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow(); //获取选中行
        if (selectedRow < 0) {
            this.messageBox_("请选择病患！");
            return;
        }
//        System.out.println("就诊记录入参"+selectedRow);
//        System.out.println("就诊记录入参=病案号"+dataD.getValue("MR_NO", selectedRow));
        //===zhangp 20120703 start
//        Object obj = this.openDialog("%ROOT%\\config\\opd\\OPDViewCaseHistory.x",
//                                     dataD.getValue("MR_NO", selectedRow));
        Object obj = this.openDialog("%ROOT%\\config\\opd\\OPDCaseHistory.x",
                                     dataD.getValue("MR_NO", selectedRow));
        //===zhangp 20120703 end
    }
    /**
     * 皮试
     */
    public void onPSManage(){
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
            this.messageBox_("请选择病患！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO",dataD.getData("CASE_NO",selectedRow));
        parm.setData("MR_NO",dataD.getData("MR_NO",selectedRow));
        this.openDialog("%ROOT%\\config\\onw\\ONWNSExec.x",parm);
    }

    /**
     * EMR监听
     * @param parm TParm
     */
    public void emrListener(TParm parm) {
        eventParmEmr = parm;
    }

    /**
     * EMR保存监听 取结构化病历中填写的值
     * @param parm TParm
     */
    public void emrSaveListener(TParm parm) {
        List name = new ArrayList(); //将取值控件的名字以List的形式 传入
        name.add("weight");
        name.add("H");
        //调用EMR中的取值方法， 返回Object数值
        Object[] obj = (Object[]) eventParmEmr.runListener(
            "getCaptureValueArray", name);
        if (obj == null) {
            this.messageBox_("保存病患数据失败");
            return;
        }
        if (obj.length < 1) {
            this.messageBox_("保存病患数据失败");
            return;
        }
        Object obj0 = obj[0];
        Map map = (HashMap) obj0;
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox_("请选择病患！");
            return;
        }
        String caseNo = dataD.getValue("CASE_NO", row);
        //对返回值进行操作
        String updateSql = "UPDATE REG_PATADM SET WEIGHT=" +
            TypeTool.getDouble(map.get("weight")) + " , HEIGHT=" +
            TypeTool.getDouble(map.get("H")) + ",OPT_USER='" +
            Operator.getID() +
            "' ,OPT_DATE=TO_DATE('" +
            StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                 "yyyyMMddHHmmss") +
            "','YYYYMMDDHH24MISS'),OPT_TERM='" + Operator.getIP() +
            "' WHERE CASE_NO='" + caseNo + "'";
        TParm result = new TParm(TJDODBTool.getInstance().update(updateSql));
        if (result.getErrCode() != 0) {
            System.out.println("errMsg=" + result.getErrText());
        }
    }
    /**
     * 读医疗卡
     * =======zhangp 20120227
     */
    public void onEKTcard(){
    		 //读取医疗卡
            parmEKT = EKTIO.getInstance().TXreadEKT();
            if (null == parmEKT || parmEKT.getErrCode() < 0 ||
                parmEKT.getValue("MR_NO").length() <= 0) {
                this.messageBox(parmEKT.getErrText());
                parmEKT = null;
                return;
            }
            String mrNo = parmEKT.getValue("MR_NO");
            String currentBalance = parmEKT.getValue("CURRENT_BALANCE");
            String patName = parmEKT.getValue("PAT_NAME");
            this.setValue("MR_NO", mrNo);
            this.setValue("PAT_NAME", patName);
            this.setValue("EKT_AMT", currentBalance);
            this.onChooseCaseNo(mrNo);
            
        
    }
    /**
     * 计算年龄的方法
     * yanjing 20140404
     * 
     */
 	private String culmulateAge(Timestamp birthDay){
 		//根据出生日期和当前的日期计算年龄
 		Timestamp sysDate = SystemTool.getInstance().getDate();
 			String age = "";
 			if(!"".equals(birthDay.toString())
 					&&!birthDay.toString().equals(null)){
 				age = DateUtil.showAge(birthDay, sysDate);
 			}
 			return age;
 	}
    /**
     * 就诊号选择事件
     * yanjing 20140707
     * 
     */
    private void onChooseCaseNo(String mrNo){
    	 // 初始化pat
		pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("查无此病案号");
			// 若无此病案号则不能查找挂号信息
			callFunction("UI|record|setEnabled", false);
			return;
		}
		TParm parm = new TParm();
		//计算年龄
		String age = culmulateAge(pat.getBirthday());
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("SEX_CODE", pat.getSexCode());
		parm.setData("AGE", age);
		parm.setData("count", "0");
        //根据病案号定位病人，查询本日挂号信息
        String date = StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy-MM-dd");
        String regSql = "SELECT CASE_NO FROM REG_PATADM WHERE MR_NO = '"+mrNo+"' " +
        		"AND ADM_DATE =TO_DATE('"+ date + "','YYYY-MM-DD') AND REGCAN_USER IS NULL ";
        
        TParm regSysParm =  new TParm(TJDODBTool.getInstance().select(regSql));
        String caseNo = "";
        if(regSysParm.getCount()<1){//当天没有挂号信息时
        	caseNo = (String) openDialog(
    				"%ROOT%\\config\\opb\\OPBChooseVisit.x", parm);
        }else if(regSysParm.getCount() == 1){//当天仅有一个挂号信息
        	caseNo = regSysParm.getValue("CASE_NO",0);
        	
        }else{//当天有多个挂号信息
        	caseNo = (String) openDialog(
    				"%ROOT%\\config\\opb\\OPBChooseVisit.x", parm);
        }
        this.getPatAndDcInFo(caseNo,true);
        this.setValue("OPD_AMT", this.getOpdAmt(caseNo));  //add by huangtt
	
    }
    /**
     * 获取病患信息及医生信息
     * yanjing 20140707 
     */
    private void getPatAndDcInFo(String caseNo,boolean flg){
    	String sql =
    		"SELECT ADM_TYPE,A.CASE_NO,A.MR_NO,A.REGION_CODE,A.ADM_DATE,A.REASSURE_FLG," +
    		" A.REG_DATE,A.SESSION_CODE,A.CLINICAREA_CODE,A.CLINICROOM_NO,A.QUE_NO," +
    		" A.REG_ADM_TIME,A.DEPT_CODE,C.USER_NAME AS DR_CODE,A.REALDEPT_CODE,A.REALDR_CODE," +
    		" A.APPT_CODE,A.VISIT_CODE,A.REGMETHOD_CODE,A.CTZ1_CODE,A.CTZ2_CODE," +
    		" A.CTZ3_CODE,A.TRANHOSP_CODE,A.TRIAGE_NO,A.CONTRACT_CODE,A.ARRIVE_FLG," +
    		" A.REGCAN_USER,A.REGCAN_DATE,A.ADM_REGION,A.PREVENT_SCH_CODE,A.DRG_CODE," +
    		" A.HEAT_FLG,A.ADM_STATUS,A.REPORT_STATUS,A.WEIGHT,A.HEIGHT," +
    		" A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.PAT_NAME,A.CLINICTYPE_CODE,A.LMP_DATE,B.LMP_DATE PAT_LMP_DATE," +
    		" B.BIRTH_DATE,(SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX' AND ID=B.SEX_CODE) AS PAT_SEX,"+
    		" A.VIP_FLG,B.SEX_CODE,A.SERVICE_LEVEL ,A.ARRIVE_DATE AS ARRIVE_TIME, A.REVISIT_DATE AS REVISIT_TIME " +
    		" FROM REG_PATADM A,SYS_PATINFO B,SYS_OPERATOR C  WHERE A.MR_NO=B.MR_NO AND A.DR_CODE=C.USER_ID  AND A.REGCAN_USER IS NULL " +
    		" AND A.CASE_NO = '"+caseNo+"' ORDER BY QUE_NO";
    	TParm rParm = new TParm(TJDODBTool.getInstance().select(sql));
//    	System.out.println("rParm==="+rParm);
    	if(rParm.getCount()<0){
//    		messageBox("无数据");
    		return;
    	}
    	if(rParm.getErrCode()<0){
    		messageBox(rParm.getErrText());
    		return;
    	}
    	String regionCode = "";
    	String admtype = "";
    	String admDate = "";
    	String sessionCode = "";
    	String clinic = "";
    	TParm lParm = new TParm();
    	for (int i = 0; i < rParm.getCount(); i++) {
    		rParm.addData("SEQ", ""+(i+1));
    		rParm.addData("SELECT", "Y");
    		//====计算首再诊
            String mr_no = rParm.getValue("MR_NO", i);
			String dept_code = rParm.getValue("REALDEPT_CODE",i);
			String firstFlgSql = "SELECT COUNT(MR_NO) SUM FROM SYS_EMR_INDEX WHERE MR_NO = '"+mr_no+"' AND DEPT_CODE = '"+dept_code+"'";
			TParm selParm = new TParm(TJDODBTool.getInstance().select(firstFlgSql));
			if(selParm.getInt("SUM", 0)<=1){
				rParm.addData("FIRST_FLG",  "首诊");//首诊
			}else{
				rParm.addData("FIRST_FLG", "再诊");//再诊
			}
			rParm.addData("PAT_AGE", culmulateAge(rParm.getTimestamp("BIRTH_DATE",i)));//添加年龄 add by huangjw 20140923
			if(rParm.getData("LMP_DATE", i)!=null&&
					!"".equals(rParm.getData("LMP_DATE", i))){
				rParm.addData("PREGNANT_WEEKS", OdoUtil.getPreWeekNew(rParm.getTimestamp(
						"ADM_DATE", i), rParm.getTimestamp("LMP_DATE",i )));//====计算孕周
				/*rParm.addData("PREGNANT_WEEKS", OdoUtil.getPreWeek(rParm.getTimestamp(
						"LMP_DATE", i), rParm.getTimestamp("ADM_DATE",i ))+""+"周");//====计算孕周
*/			}else if(rParm.getData("PAT_LMP_DATE", i)!=null&&
					!"".equals(rParm.getData("PAT_LMP_DATE", i))){
				rParm.addData("PREGNANT_WEEKS", OdoUtil.getPreWeekNew(rParm.getTimestamp(
						"ADM_DATE", i), rParm.getTimestamp("PAT_LMP_DATE",i )));//====计算孕周
			}else{
				rParm.addData("PREGNANT_WEEKS", "");//====没有LMP_DATE的孕周为空
			}
			
    		regionCode = rParm.getData("REGION_CODE", i).toString();
    		admtype = rParm.getData("ADM_TYPE", i).toString();
    		admDate = rParm.getData("ADM_DATE", i).toString();
    		admDate = admDate.substring(0, 4) + admDate.substring(5, 7) + admDate.substring(8, 10);
    		sessionCode = rParm.getData("SESSION_CODE", i).toString();
    		clinic = rParm.getData("CLINICROOM_NO", i).toString();
    		sql = this.getSqll(regionCode, admtype, admDate, sessionCode, clinic,flg);
    		
    		//System.out.println("::::"+sql);
//    		System.out.println(sql);
    		TParm tempParm = new TParm(TJDODBTool.getInstance().select(sql));
    		for(int k=0;k<tempParm.getCount();k++){
    			if(tempParm.getBoolean("STOP_SESSION", k)){
    				tempParm.removeRow(k);
    				continue;
    			}
    		}
    		for (int j = 0; j < tempParm.getCount(); j++) {
    			lParm.addData("REGION_CODE", tempParm.getData("REGION_CODE", j));
    			lParm.addData("ADM_TYPE", tempParm.getData("ADM_TYPE", j));
    			lParm.addData("ADM_DATE", tempParm.getData("ADM_DATE", j));
    			lParm.addData("SESSION_CODE", tempParm.getData("SESSION_CODE", j));
    			lParm.addData("CLINICROOM_NO", tempParm.getData("CLINICROOM_NO", j));
    			lParm.addData("WEST_MEDI_FLG", tempParm.getData("WEST_MEDI_FLG", j));
    			lParm.addData("DEPT_CODE", tempParm.getData("DEPT_CODE", j));
    			lParm.addData("REG_CLINICAREA", tempParm.getData("REG_CLINICAREA", j));
    			lParm.addData("DR_CODE", tempParm.getData("DR_CODE", j));
    			lParm.addData("REALDEPT_CODE", tempParm.getData("REALDEPT_CODE", j));
    			lParm.addData("REALDR_CODE", tempParm.getData("REALDR_CODE", j));
    			lParm.addData("CLINICTYPE_CODE", tempParm.getData("CLINICTYPE_CODE", j));
    			lParm.addData("QUEGROUP_CODE", tempParm.getData("QUEGROUP_CODE", j));
    			lParm.addData("QUE_NO", tempParm.getData("QUE_NO", j));
    			lParm.addData("MAX_QUE", tempParm.getData("MAX_QUE", j));
    			lParm.addData("VIP_FLG", tempParm.getData("VIP_FLG", j));
    			lParm.addData("CLINICTMP_FLG", tempParm.getData("CLINICTMP_FLG", j));
    			lParm.addData("STOP_SESSION", tempParm.getData("STOP_SESSION", j));
    			lParm.addData("REFRSN_CODE", tempParm.getData("REFRSN_CODE", j));
    			lParm.addData("OPT_USER", tempParm.getData("OPT_USER", j));
    			lParm.addData("OPT_DATE", tempParm.getData("OPT_DATE", j));
    			lParm.addData("OPT_TERM", tempParm.getData("OPT_TERM", j));
    		}
    		this.setValue("DEPT_CODE", tempParm.getData("DEPT_CODE", 0));
    		this.setValue("CLINICTYPE_CODE", tempParm.getData("CLINICTYPE_CODE", 0));
    		this.setValue("CLINICROOM_NO", tempParm.getData("CLINICROOM_NO", 0));
    		this.setValue("DR_CODE", tempParm.getData("DR_CODE", 0));
    		this.setValue("CLINICAREA_CODE", tempParm.getData("REG_CLINICAREA", 0));
    	}
    	
    	dataD = rParm;
    	dataM = lParm;
    	this.callFunction("UI|LTABLE|setParmValue", lParm);
    	this.callFunction("UI|RTABLE|setParmValue", rParm);
    	this.getTTable(TABLE).setSelectedRow(0);
    }
    /**
	 * 拿到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

    /**
     * 得到sql
     * =====zhangp 20120227
     * @param regionCode
     * @param admType
     * @param admDate
     * @param sessionCode
     * @param clinic
     * @return
     */
    public String getSqll (String regionCode,String admtype,String admDate,String sessionCode,String clinic,boolean flg){
    	String sql =
    		"SELECT REGION_CODE, ADM_TYPE, ADM_DATE, SESSION_CODE, CLINICROOM_NO," +
    		" WEST_MEDI_FLG, DEPT_CODE, REG_CLINICAREA, DR_CODE, REALDEPT_CODE," +
    		" REALDR_CODE, CLINICTYPE_CODE, QUEGROUP_CODE, QUE_NO, MAX_QUE, VIP_FLG," +
    		" CLINICTMP_FLG, STOP_SESSION, REFRSN_CODE, OPT_USER, OPT_DATE, OPT_TERM" +
    		" FROM REG_SCHDAY WHERE REGION_CODE = '"+regionCode+
    		"' AND ADM_TYPE = '"+admtype+"' AND ADM_DATE = '"+admDate+"'" +
    		" AND SESSION_CODE = '"+sessionCode+"' ";
    	if(flg){
    		sql=sql+" AND CLINICROOM_NO = '"+clinic+"'";
    	}else{
    		if(Operator.getStation()!=null && Operator.getStation().toString().length()>0){
    			sql=sql+" AND REG_CLINICAREA='"+Operator.getStation()+"'";
    		}
    	}
    	return sql;
    }
    /**
	 * 解锁监听方法
	 * 
	 * @param prgId
	 *            String
	 * @param mrNo
	 *            String
	 * @param prgIdU
	 *            String
	 * @param userId
	 *            String
	 * @return Object
	 * ======pangben 2013-5-14
	 */
	public Object onListenPm(String prgId, String mrNo, String prgIdU,
			String userId) {
		if ("ONW".equalsIgnoreCase(prgId)||"ENW".equalsIgnoreCase(prgId)) {
		}else{
			return null;
		}
		TParm parm = new TParm();
		parm.setData("PRG_ID", prgId);
		parm.setData("MR_NO", mrNo);
		parm.setData("PRG_ID_U", prgIdU);
		parm.setData("USE_ID", userId);
		String flg = (String) openDialog(
				"%ROOT%\\config\\sys\\SYSPatUnLcokMessage.x", parm);
//		if ("OK".equals(flg)) {
//			this.onClear();
//			return "OK";
//		}
		if ("OK".equals(flg)) {
			String aa = PatTool.getInstance().getLockParmString(mrNo);
			PatTool.getInstance().unLockPat(mrNo);
			PATLockTool.getInstance().log(
					"ODO->" + SystemTool.getInstance().getDate() + " "
							+ Operator.getID() + " "
							+ Operator.getName() + " 强制解锁[" + aa
							+ " 病案号：" + mrNo + "]");
			
		}
		if ("OK".equals(flg)) {
			//this.onClear();
			//this.closeWindow();
			
			return "OK";
		}
		return "";
	}
	
	
	/**
	 * 护士分诊台界面跑马灯双击操作
	 * 
	 * @param rxNo
	 *  yanjing 20140402
	 */
	public void openInwCheckWindow(TParm action) {
		
		Color DsFlgColor = new Color(0, 255, 0); //绿色
		TTable table1 = (TTable) this.getComponent("RTABLE");
		table1.acceptText();
		table1.removeRowAll();
		//根据ip查询诊室信息
		String roomSql = "SELECT CLINICROOM_NO FROM REG_CLINICROOM WHERE IP = '"+action.getValue("IP", 0)+"'";
		TParm roomParm = new TParm(TJDODBTool.getInstance().select(roomSql));
		
		this.setValue("CLINICROOM_NO", roomParm.getValue("CLINICROOM_NO", 0));
//		System.out.println("action action is ::"+action);
		this.setValue("DR_CODE", action.getValue("DR_CODE", 0));
		this.onClinic();//通过诊室查询诊区
		onQuery();
		TTable table = (TTable) this.getComponent("LTABLE");// 获得上面表格数据，如果没有数据直接移除跑马灯当前处方签
		table.acceptText();
		if (table.getRowCount() == 1) {
			table.setSelectedRow(0);
			onLTABLEClicked(0);
        }
		//确定下一位病人的就诊号
//		String queNo = ""+isPatQue(action.getValue("DR_CODE", 0));
		String drCode = action.getValue("DR_CODE", 0);
		//根据就诊号确定为哪一行，并将其背景设置为绿色
//		for(int i = 0;i<table1.getRowCount();i++){
//			table1.removeRowColor(i);
//			if(queNo.equals(table1.getItemData(i, "QUE_NO").toString())){
//				table1.setRowColor(i, DsFlgColor);
//				
//			}
//			
//		}
		getLedMedRemoveRxNo(drCode);
		
	}

	/**
	 * 跑马灯显示
	 */
	public void openLEDMEDUI() {
		Component com = (Component) getComponent();
		TParm parm = new TParm();
		parm.setData("ONW_CODE", "ONW");
		parm.setData("STATION_CODE",Operator.getStation());
		parm.addListener("onSelStation", this, "onSelStationListenerLed");
		while ((com != null) && (!(com instanceof Frame)))
			com = com.getParent();
//		this.ledMedUi = new LEDMEDUI((Frame) com, this, parm);
		//护士和医生的诊区相同时，显示跑马灯
//		if( Operator.getStation().equals(anObject))
//		parm = null;
		this.ledMedUi = new LEDONWUI((Frame) com, this, parm);
		this.ledMedUi.openWindow();
	}

	public boolean onClosing() {
			this.ledMedUi.close();
		return true;
	}

	public void onSelStationListenerLed(TParm parm) {
		this.ledParm = parm;
	}

	public void onSel() {
		this.ledParm.runListener("onListenerLed", new Object[] { "ONW" });
	}

	/**
	 * yanjing 20140402
	 */
	public void getLedMedRemoveRxNo(String drCode) {
		// if("Y".equals(Operator.getSpcFlg())) {
		TParm caseNoParm = new TParm();// =====pangben 2013-5-15 跑马灯移除数据
//		caseNoParm.setData("QUE_NO", queNo);
		caseNoParm.setData("DR_CODE", drCode);
		if (ledMedUi != null) {
			ledMedUi.removeMessage(caseNoParm);
		}
		// }
	}
	/**
	 * 查询下一位就诊病患的就诊号
	 * @return
	 */
	private int isPatQue(String drCode){
		int que = 0;
		String admDate = this.getValue("ADM_DATE").toString().substring(0, 10).replace("-", "").replace("/", "");
		String queSql = "SELECT QUE_NO FROM REG_PATADM WHERE REALDR_CODE = '"+drCode+"' AND ADM_DATE = TO_DATE('"+admDate+"','YYYYMMDD') " +
				"AND CLINICAREA_CODE = '"+this.getValue("CLINICAREA_CODE")+"' AND CLINICROOM_NO = '"+this.getValue("CLINICROOM_NO")+"' " +
						"AND SESSION_CODE = '"+this.getValue("SESSION_CODE")+"' AND ADM_STATUS = '1' ORDER BY QUE_NO";
		TParm queParm = new TParm(TJDODBTool.getInstance().select(queSql));
		que = queParm.getInt("QUE_NO",0);
		return que;
	}
	/**
     * 腕带打印
     */
    public void onWrist() {
//        if (this.getValueString("MR_NO").length() == 0 || 
//        		this.getValueString("PAT_NAME").length() == 0 ||
//        		this.getValueString("SEX_CODE").length() == 0) {
//        	this.messageBox("有未知项不符合要求,请重新选择!");
//            return;
//        }
        //获取到RTABLE信息
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        //获取到所选择的行数
        int selectedIndx=table.getSelectedRow();
    	if(selectedIndx<0){
    		this.messageBox("请选择一个挂号人员,再进行腕带打印！");
    		return;
    	}
    	if(selectedIndx>1){
    		this.messageBox("只能选择一个挂号人员进行腕带打印！");
    		return;
    	}
    	TParm parm=table.getParmValue();
        String number = parm.getValue("MR_NO",selectedIndx);
        String name = parm.getValue("PAT_NAME",selectedIndx);
        String sql = "SELECT BIRTH_DATE,SEX_CODE FROM SYS_PATINFO WHERE MR_NO = '"+number+"'";
        TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
        String time = parm1.getValue("BIRTH_DATE");
        String sexCode = parm1.getValue("SEX_CODE");
        if(sexCode.equals("[1]")){
        	sexCode="男";
        }
        if(sexCode.equals("[2]")){
        	sexCode="女";
        }
        if(sexCode.equals("[9]")){
        	sexCode="未说明";
        }
        if(sexCode.equals("[0]")){
        	sexCode="未知";
        }
        
        
        TParm print = new TParm();
        print.setData("MR_NO", "TEXT", number);
        print.setData("PAT_NAME", "TEXT", name);
        print.setData("SEX_CODE", "TEXT", sexCode);
        //print.setData("BIRTH_DATE", "TEXT", StringTool.getString(a_time,"yyyy/MM/dd"));
        print.setData("BIRTH_DATE", "TEXT", time.substring(1, 11).replace("-", "/"));
//        System.out.println("print=="+print);
//        System.out.println("---00000----");
        this.openPrintDialog("%ROOT%\\config\\prt\\ONW\\ONWRegNurseStation.jhw", print);
//        this.openPrintDialog(IReportTool.getInstance().getReportPath("REGPatAdm.jhw"),
//                             IReportTool.getInstance().getReportParm("REGPatAdm.class", print));//报表合并modify by wanglong 20130730
    }
    
    /**
     * 患者保险信息 add by sunqy 20140527
     */
    public void onInsureinfo(){
    	TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
            this.messageBox_("请选择病患！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("MR_NO",dataD.getValue("MR_NO",selectedRow));
        parm.setData("EDIT", "N");
        this.openDialog("%ROOT%\\config\\mem\\MEMInsureInfo.x",parm);
    }
    /**
	 * 护理记录
	 */
	public void onNursingRec() {
		TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
		int selRow = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		String caseNo_ = "";
		String mrNo_ = "";
		String patName_ = "";
		String deptCode = "";
		TParm parm = new TParm();
		if (tableParm!=null&&tableParm.getCount()>0&&selRow> -1) {
//			System.out.println("护理记录 tableParm is ：："+tableParm);
			caseNo_ = tableParm.getValue("CASE_NO", selRow);
			mrNo_ = tableParm.getValue("MR_NO", selRow);
			patName_ = tableParm.getValue("PAT_NAME", selRow);
			deptCode = tableParm.getValue("DEPT_CODE", selRow);
			parm.setData("ADM_DATE", tableParm.getData("ADM_DATE", selRow));
		} else{
			messageBox("请选择病人！");
			return;
		}
		if (caseNo_.length() == 0) {
			messageBox("请选择病人！");
			return;
		}
		
		parm.setData("SYSTEM_TYPE", "ONW");
		parm.setData("ADM_TYPE", admType);
		parm.setData("CASE_NO", caseNo_);
		parm.setData("PAT_NAME", patName_);
		parm.setData("MR_NO", mrNo_);
		parm.setData("IPD_NO", "");
		parm.setData("DEPT_CODE", deptCode);
		parm.setData("RULETYPE", "2");
		parm.setData("EMR_DATA_LIST", new TParm());
		parm.addListener("EMR_LISTENER", this, "emrListener");
		parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);

	}
	
	public double getOpdAmt(String caseNo){
		String sql="SELECT SUM(A.AR_AMT) AR_AMT FROM (SELECT *" +
				" FROM OPD_ORDER WHERE CASE_NO = '"+caseNo+"'" +
				" AND MEM_PACKAGE_ID IS NULL" +
				" AND ORDERSET_CODE NOT IN (" +
				" SELECT ORDER_CODE" +
				" FROM OPD_ORDER WHERE CASE_NO = '"+caseNo+"'" +
				" AND MEM_PACKAGE_ID IS NOT NULL AND SETMAIN_FLG = 'Y')" +
				" UNION SELECT * FROM OPD_ORDER WHERE CASE_NO='"+caseNo+"' " +
				" AND  ORDERSET_CODE IS NULL  AND MEM_PACKAGE_ID IS NULL ) A ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getDouble("AR_AMT", 0);
	}
	/**
	 * 根据输入信息查询右侧表格信息
	 */
	public void onQueryRTable(){
		String admDate = this.getValueString("ADM_DATE");
		String clinicareaCode = this.getValueString("CLINICAREA_CODE");
		String sessionCode = this.getValueString("SESSION_CODE");
		String deptCode = this.getValueString("DEPT_CODE");
		String clinictypeCode = this.getValueString("CLINICTYPE_CODE");
		String clinicroomNo = this.getValueString("CLINICROOM_NO");
		String drCode = this.getValueString("DR_CODE");
		StringBuffer sqlR = new StringBuffer("");
		sqlR.append("SELECT  A.ADM_TYPE,A.CASE_NO,A.MR_NO,A.REGION_CODE,A.ADM_DATE,A.REASSURE_FLG," +
				" A.REG_DATE,A.SESSION_CODE,A.CLINICAREA_CODE,A.CLINICROOM_NO,A.QUE_NO," +
				" A.REG_ADM_TIME,A.DEPT_CODE,(SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = A.DR_CODE) AS DR_CODE ,A.REALDEPT_CODE,A.REALDR_CODE, " +
				" A.APPT_CODE,A.VISIT_CODE,A.REGMETHOD_CODE,A.CTZ1_CODE,A.CTZ2_CODE, " +
				" A.CTZ3_CODE,A.TRANHOSP_CODE,A.TRIAGE_NO,A.CONTRACT_CODE,A.ARRIVE_FLG, " +
				" A.REGCAN_USER,A.REGCAN_DATE,A.ADM_REGION,A.PREVENT_SCH_CODE,A.DRG_CODE, " +
				" A.HEAT_FLG,A.ADM_STATUS,A.REPORT_STATUS,A.WEIGHT,A.HEIGHT, " +
				" A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.PAT_NAME,A.CLINICTYPE_CODE,A.LMP_DATE,B.LMP_DATE PAT_LMP_DATE,  " +
				" A.VIP_FLG,B.SEX_CODE,A.SERVICE_LEVEL,A.ARRIVE_DATE AS ARRIVE_TIME,A.REVISIT_DATE AS REVISIT_TIME,  " +
				" B.BIRTH_DATE,(SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX' AND ID=B.SEX_CODE) AS PAT_SEX,A.PAT_PACKAGE," +
				" (SELECT COUNT (*) "+
				" FROM BIL_OPB_RECP "+
				" WHERE     CASE_NO = A.CASE_NO "+
                " AND TOT_AMT > 0 "+
                " AND RESET_RECEIPT_NO IS NULL "+
                " AND PRINT_NO IS NOT NULL) "+
				" COUNT  " +
				" FROM REG_PATADM A,SYS_PATINFO B  " +
				" WHERE A.MR_NO = B.MR_NO  " +
				" AND A.REGCAN_USER IS NULL  " );
		if(null != admDate && !"".equals(admDate) ){
			admDate = admDate.substring(0, 10);
			sqlR.append(" AND ADM_DATE = TO_DATE('"+admDate+"','yyyy-mm-dd') ");
		}
		
		if(null != sessionCode && !"".equals(sessionCode) ){
			admDate = admDate.substring(0, 10);
			sqlR.append(" AND A.SESSION_CODE = '"+sessionCode+"' ");
		}
		
		if(null != clinicareaCode && !"".equals(clinicareaCode)){
			sqlR.append(" AND A.CLINICAREA_CODE = '"+clinicareaCode+"' ");
		}
		
		if(null != deptCode && !"".equals(deptCode)){
			sqlR.append(" AND A.DEPT_CODE = '"+deptCode+"' ");
		}
		
		if(null != clinictypeCode && !"".equals(clinictypeCode)){
			sqlR.append(" AND A.CLINICTYPE_CODE = '"+clinictypeCode+"' ");
		}
		
		if(null != clinicroomNo && !"".equals(clinicroomNo)){
			sqlR.append(" AND A.CLINICROOM_NO = '"+clinicroomNo+"' ");
		}
		
		if(null != drCode && !"".equals(drCode)){
			sqlR.append(" AND A.DR_CODE = '"+drCode+"' ");
		}
		
		sqlR.append(" AND A.ADM_TYPE = '"+this.admType+"'");//add  by huangjw 20160504
		sqlR.append(" ORDER BY DEPT_CODE,DR_CODE,Case_no,ARRIVE_TIME, QUE_NO");
		//System.out.println("sqlR = = = " + sqlR);
		TParm result = new TParm(TJDODBTool.getInstance().select(sqlR.toString()));
		//System.out.println("11111:"+sqlR.toString());
        int count = result.getCount("SESSION_CODE");
        for (int i = 0; i < count; i++) {
        	result.addData("SEQ", ""+(i+1));
        	result.addData("SELECT", "N");
            //====计算首再诊
            String mr_no = result.getValue("MR_NO", i);
			String dept_code = result.getValue("REALDEPT_CODE",i);
			String sql = "SELECT COUNT(MR_NO) SUM FROM SYS_EMR_INDEX WHERE MR_NO = '"+mr_no+"' AND DEPT_CODE = '"+dept_code+"'";
			result.addData("PAT_AGE", culmulateAge(result.getTimestamp("BIRTH_DATE",i)));
			//add by wuxy 2017/8/02 新增出生日期
			Timestamp birthDay = result.getTimestamp("BIRTH_DATE",i);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			result.addData("BIRTH_DAY",sdf.format(birthDay));
			//end wuxy
			result.addData("PAT_SEX",result.getValue("PAT_SEX",i));
			TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
			if(selParm.getInt("SUM", 0)<=1){
				result.addData("FIRST_FLG",  "首诊");//首诊
			}else{
				result.addData("FIRST_FLG", "再诊");//再诊
			}
			
			try {
				if(result.getData("LMP_DATE", i)!=null&&
						!"".equals(result.getData("LMP_DATE", i))){
					result.addData("PREGNANT_WEEKS", OdoUtil.getPreWeekNew(result.getTimestamp(
							"ADM_DATE", i), result.getTimestamp("LMP_DATE",i )));//====计算孕周
					/*result.addData("PREGNANT_WEEKS", OdoUtil.getPreWeek(result.getTimestamp(
							"LMP_DATE", i), result.getTimestamp("ADM_DATE",i ))+""+"周");//====计算孕周
*/				}else if(result.getData("PAT_LMP_DATE", i)!=null&&
						!"".equals(result.getData("PAT_LMP_DATE", i))){
					result.addData("PREGNANT_WEEKS", OdoUtil.getPreWeekNew(result.getTimestamp(
							"ADM_DATE", i), result.getTimestamp("PAT_LMP_DATE",i )));//====计算孕周
				}else{
					result.addData("PREGNANT_WEEKS", "");//====没有LMP_DATE的孕周为空
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
            
            //====zhangp 20120227 modify start
            if(result.getData("REG_ADM_TIME", i)!=null  && !"".equals(result.getData("REG_ADM_TIME", i))){
            	String admTime =
            		result.getData("REG_ADM_TIME", i).toString().substring(0,2)+":"+result.getData("REG_ADM_TIME", i).toString().substring(2,4);
            	result.setData("REG_ADM_TIME", i , admTime);
            }
            //======zhangp 20120227 modify end
        }		
		
		this.clearValue("RTABLE");
		//System.out.println("1111"+result);
        this.callFunction("UI|RTABLE|setParmValue", result);
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        for(int i = 0; i < table.getParmValue().getCount(); i++){
        	table.removeRowColor(i);
        	if(table.getParmValue().getInt("COUNT",i) > 0 ){
        		table.setRowColor(i, Color.LIGHT_GRAY);
        	}else{
        		table.setRowColor(i, Color.WHITE);
        	}
        }
        dataD = new TParm();  //add by huangtt 20150615 
        dataD = result;  //add by huangtt 20150615 
	}
	
}
