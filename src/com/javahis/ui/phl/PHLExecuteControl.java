package com.javahis.ui.phl;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jdo.ekt.EKTIO;
import jdo.odo.OpdRxSheetTool;
import jdo.phl.PHLSQL;
import jdo.phl.PhlExecuteTool;
import jdo.phl.PhlRegionTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.combo.TComboPHLBed;
import com.javahis.ui.odo.OdoMainControl;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 静点室执行
 * </p>
 *
 * <p>
 * Description: 静点室执行
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class PHLExecuteControl
    extends TControl {
	private OdoMainControl odoMainControl;
    private TTable table;
    //就诊序号
    private String case_no = "";
    //开始时间
    private String start_date = "";
    private List<Integer> list;
    private static final String TABLEA = "TABLE"; // 检验检查表
   // private boolean Sflg=true;
    /**
     * 皮试批号
     */
    private  String batchno;
    private  String skinflg;
    public String getSkinflg() {
		return skinflg;
	}

	public void setSkinflg(String skinflg) {
		this.skinflg = skinflg;
	}

	public String getBatchno() {
		return batchno;
	}

	public void setBatchno(String batchno) {
		this.batchno = batchno;
	}

	public PHLExecuteControl() {
    }
    private String deptCode;
    public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	private String rxNo;
	private String orderCode;
	private String seqNo;
	private String orderNo;
    public String getRxNo() {
		return rxNo;
	}

	public void setRxNo(String rxNo) {
		this.rxNo = rxNo;
	}
    public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
    public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
    public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
     * 初始化方法
     */
    public void onInit() {
    	table = this.getTable("TABLE");
		 callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");//单击事件
		//
		 table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
			"onCheckBox");
        // 初始化权限管控
        initPopedem();
        // 初始化
        // getTextField("BAR_CODE").grabFocus();
         //this.grabFocus("BAR_CODE");
//         TTextField barCode = (TTextField)this.getComponent("BAR_CODE");
//         barCode.grabFocus();

        
        
    }

    /**
     * 初始化权限管控
     */
    private void initPopedem() {
        // 权限管控
    	callFunction("UI|skiResult|setEnabled", false);
        if (!this.getPopedem("deptAll")) {
            this.getComboBox("REGION_CODE").setEnabled(false);
            String ip = Operator.getIP();
            TParm parm = new TParm();
            TParm result = PhlRegionTool.getInstance().onQuery(parm);
            String start_ip = "";
            String end_ip = "";
            if (result.getCount() > 0) {
                for (int i = 0; i < result.getCount(); i++) {
                    start_ip = result.getValue("START_IP", i);
                    end_ip = result.getValue("END_IP", i);
                    if (StringTool.isIPBetween(start_ip, end_ip, ip)) {
                        this.setValue("REGION_CODE",
                                      result.getValue("REGION_CODE", i));
                        onChangeRegion();
                        break;
                    }
                }
            }
        }
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        table.removeRowAll();
        /*if ("".equals(this.getValueString("MR_NO")) &&
            "".equals(this.getValueString("BED_NO"))) {
            this.messageBox("病案号和床位号不能同时为空");
            return;
        }*/

	        TParm parm = new TParm();
	    if (!"".equals(getValueString("BAR_CODE"))) {
	          parm.setData("BAR_CODE", getValueString("BAR_CODE"));
	        }
	    //$-------------modify 病患来源 20140103 caoyong start----------
	    if("".equals(getValueString("ADM_TYPE"))){
	   	 parm.setData("ADM_TYPE",getValueString("ADM_TYPE"));
	     }
	    if("O".equals(getValueString("ADM_TYPE"))){
	   	 parm.setData("ADM_TYPE_O",getValueString("ADM_TYPE"));
	    }
	    if("E".equals(getValueString("ADM_TYPE"))){
	   	 parm.setData("ADM_TYPE_E",getValueString("ADM_TYPE"));
	    }
	   //$-------------20140103 caoyong end----------
    
        if (!"".equals(getValueString("MR_NO"))) {
            parm.setData("MR_NO", getValueString("MR_NO"));
        }
        if (!"".equals(getValueString("REGION_CODE"))) {
            parm.setData("REGION_CODE", getValueString("REGION_CODE"));
        }
        if (!"".equals(getValueString("BED_NO"))) {
            parm.setData("BED_NO", getValueString("BED_NO"));
        }
        if (getRadioButton("RadioButton1").isSelected()) {
            parm.setData("EXEC_STATUS", "0");
            
        }
        else {
            parm.setData("EXEC_STATUS", "1");
//            Sflg=false;
        }
        /*if (case_no.length() > 0)
            parm.setData("CASE_NO", case_no);
        if (start_date.length() > 0)
            parm.setData("START_DATE", start_date);*/
        TParm result = PhlExecuteTool.getInstance().onQuery(parm);
       
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
         list=new ArrayList<Integer>();
       for(int i=0;i<result.getCount();i++){
    	 Pat patt = Pat.onQueryByMrNo(result.getValue("MR_NO",i));
         result.addData("PAT_NAME", patt.getName());
         
              if("PS".equals(result.getValue("ROUTE_CODEA", i))){
      	        	  table.setLockCell(i, "BATCH_NO", false);
           	          table.setLockCell(i, "SKINTEST_FLG", false);
      	        	  list.add(i);//如是皮试药品&&用法为皮试用法加list
              }else{
            	      
             	      table.setLockCell(i, "BATCH_NO", true);
             	      table.setLockCell(i, "SKINTEST_FLG", true);
              }
              
              if(!"Y".equals(result.getValue("LINK_MAIN_FLG", i))&&"0".equals(result.getValue("LINK_NO", i))){//不是连接医嘱连接号应为空，不能是0 为空
     	    	 result.setData("LINK_NO",i, "");
     	       }
              
              
         }
        if(this.getValueString("MR_NO").length()>0){
//        this.setValue("BAR_CODE", result.getValue("BAR_CODE", 0));
        this.setValue("ADM_TYPE", result.getValue("ADM_TYPE", 0));
//        this.setValue("MR_NO", result.getValue("MR_NO", 0));
        this.setValue("REGION_CODE", result.getValue("REGION_CODE", 0));
        this.setValue("BED_NO", result.getValue("BED_NO", 0));
        
        Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
        this.setValue("PAT_NAME", pat.getName());
        this.setValue("SEX", pat.getSexString());
        
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("AGE",DateUtil.showAge(pat.getBirthday(), date));
        
        }
        
        
         
         for(int i=0;i<result.getCount();i++){
          result.setData("SKINTEST_FLG", i, this.getSkin(result.getValue("SKINTEST_FLG", i)));
        	 
         }
        table.setParmValue(result);
    }

    /**
     * 单选按钮变更事件
     */
    public void onChangeRadioAction() {
    	for(int i=0;i<table.getRowCount();i++){
    		table.setItem(i, "LFLG", "N");
    	}
        table.setSelectionMode(0);
        table.removeRowAll();
    }

    /**
     * 清空方法
     */
    public void onClear() {
        // 清空VALUE
        String clear =
            "BAR_CODE;MR_NO;ADM_TYPE;PAT_NAME;SEX;AGE;BED_NO;"
            + "SELECT_ALL";
        this.clearValue(clear);
        callFunction("UI|skiResult|setEnabled", false);
        this.getRadioButton("RadioButton1").setSelected(true);
        table.setSelectionMode(0);
        table.removeRowAll();
        case_no = "";
        start_date = "";
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if (table.getRowCount() == 0) {
            this.messageBox("没有执行数据");
        }
        boolean flg = true;
        table.acceptText();
        for (int i = 0; i < table.getRowCount(); i++) {
            //this.messageBox(table.getItemString(i, "EXEC_STATUS"));
            if ("Y".equals(table.getItemString(i, "LFLG"))) {
                flg = false;
                break;
            }
        }
        if (flg) {
            this.messageBox("没有执行数据");
            return;
        }

        //调用操作员输入密码界面
        Object resultData = openDialog("%ROOT%\\config\\phl\\PHLOPTCheck.x");
        String operator_id = "";
        if (resultData != null) {
            TParm resultParm = (TParm) resultData;
            //System.out.println("resultParm=="+resultParm);
            operator_id = resultParm.getValue("USER_ID");
        }
        else {
            return;
        }

        TParm parm = table.getParmValue();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        TParm orderParm = new TParm();
        //TParm orderPsparm=new TParm();
        TParm bedParm = new TParm();
        String PS="";
        if (this.getRadioButton("RadioButton1").isSelected()) {
            // 保存执行医嘱
        	
            for (int i = 0; i < parm.getCount(); i++) {
                if ("N".equals(table.getItemString(i, "LFLG"))) {
                    continue;
                }
                orderParm.addData("RX_NO", parm.getValue("RX_NO", i));
                orderParm.addData("START_DATE", parm.getValue("START_DATE", i));
                orderParm.addData("ADM_TYPE", parm.getValue("ADM_TYPE", i));
                orderParm.addData("CASE_NO", parm.getValue("CASE_NO", i));
                orderParm.addData("ORDER_NO", parm.getValue("ORDER_NO", i));
                orderParm.addData("SEQ_NO", parm.getValue("SEQ_NO", i));
                orderParm.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
                orderParm.addData("EXEC_STATUS", "1");
                orderParm.addData("EXEC_USER", operator_id);
                orderParm.addData("EXEC_DATE", date);
                orderParm.addData("NS_NOTE", parm.getValue("NS_NOTE", i));
                orderParm.addData("OPT_USER", operator_id);
                orderParm.addData("OPT_DATE", date);
                orderParm.addData("OPT_TERM", Operator.getIP());
                //add caoyong 20140328 更新opd_order 表皮试结果和批号
                if("PS".equals(parm.getValue("ROUTE_CODEA", i))){
                	if("".equals(table.getItemString(i, "BATCH_NO"))){
                		this.messageBox("请输入皮试批号");
                		return;
                	}
                	if("".equals(table.getItemString(i, "SKINTEST_FLG"))){
                		this.messageBox("请输入皮试结果");
                		return;
                	}
                 }
		               if("(+)阳性".equals(table.getItemString(i, "SKINTEST_FLG").trim())){
		      			   PS="1";
		      		   }else if("(-)阴性".equals(table.getItemString(i, "SKINTEST_FLG").trim())){
		      			   PS="0";
		      		   }else {
		      			   PS=table.getItemString(i, "SKINTEST_FLG");
		      		   }
                orderParm.addData("BATCH_NO",table.getItemString(i, "BATCH_NO"));
                orderParm.addData("SKINTEST_FLG",PS);
                
            }
            bedParm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
            bedParm.setData("BED_NO", this.getValueString("BED_NO"));
            bedParm.setData("PAT_STATUS", "2");
            bedParm.setData("OPT_USER", operator_id);
            bedParm.setData("OPT_DATE", date);
            bedParm.setData("OPT_TERM", Operator.getIP());
        }
        else {
            // 更新已执行医嘱
        	
            String patStatus = "1";
            for (int i = 0; i < parm.getCount(); i++) {
                table.acceptText();
                if (!"Y".equals(table.getItemString(i, "LFLG"))) {
                    orderParm.addData("EXEC_STATUS", "0");
                    orderParm.addData("EXEC_USER", "");
                    orderParm.addData("EXEC_DATE", tnull);
                }
                else {
                    orderParm.addData("EXEC_STATUS", "1");
                    orderParm.addData("EXEC_USER", operator_id);
                    orderParm.addData("EXEC_DATE", date);
                    patStatus = "2";
                }
                orderParm.addData("RX_NO", parm.getValue("RX_NO", i));
                orderParm.addData("START_DATE", parm.getValue("START_DATE", i));
                orderParm.addData("ADM_TYPE", parm.getValue("ADM_TYPE", i));
                orderParm.addData("CASE_NO", parm.getValue("CASE_NO", i));
                orderParm.addData("ORDER_NO", parm.getValue("ORDER_NO", i));
                orderParm.addData("SEQ_NO", parm.getValue("SEQ_NO", i));
                orderParm.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
                orderParm.addData("NS_NOTE", parm.getValue("NS_NOTE", i));
                orderParm.addData("OPT_USER", operator_id);
                orderParm.addData("OPT_DATE", date);
                orderParm.addData("OPT_TERM", Operator.getIP());
                // add caoyong start 2014/4/3
                if("PS".equals(parm.getValue("ROUTE_CODEA", i))){
                	if("".equals(table.getItemString(i, "BATCH_NO"))){
                		this.messageBox("请输入皮试批号");
                		return;
                	}
                	if("".equals(table.getItemString(i, "SKINTEST_FLG"))){
                		this.messageBox("请数据皮试结果");
                		return;
                	}
                }
	                if("(+)阳性".equals(table.getItemString(i, "SKINTEST_FLG").trim())){
	       			   PS="1";
	       		   }else if("(-)阴性".equals(table.getItemString(i, "SKINTEST_FLG").trim())){
	       			   PS="0";
	       		   }else {
	       			   PS=table.getItemString(i, "SKINTEST_FLG");
	       		   }
	             orderParm.addData("EXEC_DATE", date);//皮试执行时间add by huangjw 20141031
                 orderParm.addData("BATCH_NO",table.getItemString(i, "BATCH_NO"));
                 orderParm.addData("SKINTEST_FLG",PS);
                 // add caoyong start 2014/4/3
            }
            bedParm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
            bedParm.setData("BED_NO", this.getValueString("BED_NO"));
            bedParm.setData("PAT_STATUS", patStatus);
            bedParm.setData("OPT_USER", operator_id);
            bedParm.setData("OPT_DATE", date);
            bedParm.setData("OPT_TERM", Operator.getIP());
        }
        TParm parmData = new TParm();
        parmData.setData("ORDER_PARM", orderParm.getData());
        parmData.setData("BED_PARM", bedParm.getData());
        TParm result = TIOM_AppServer.executeAction("action.phl.PHLAction",
            "onPhlExecute", parmData);
        
        if (result.getErrCode() < 0) {
           this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
//        onPrint();//delete by sunqy 20140702
        
        this.onClear();
    }

    /**
     * 床头卡
     */
    public void onCard() {
        if ("".equals(getValueString("REGION_CODE"))) {
            this.messageBox("请选择静点区域");
            return;
        }
        TParm parm = new TParm();
        parm.setData("REGION_CODE", getValueString("REGION_CODE"));
        Object resultData = openDialog("%ROOT%\\config\\phl\\PHLCard.x",
                                       parm);
        if (resultData != null) {
            TParm resultParm = (TParm) resultData;
            //System.out.println("resultParm==="+resultParm);
            this.setValue("MR_NO", resultParm.getValue("MR_NO"));
            this.setValue("BED_NO", resultParm.getValue("BED_NO"));
            TParm inparm = new TParm();
            inparm.setData("MR_NO", resultParm.getValue("MR_NO"));
            inparm.setData("BED_NO", resultParm.getValue("BED_NO"));
            onMrNoAction();
            if (getRadioButton("RadioButton1").isSelected()) {
                inparm.setData("EXEC_STATUS", "0");
            }
            else {
                inparm.setData("EXEC_STATUS", "1");
            }
            TParm result = PhlExecuteTool.getInstance().onQuery(inparm);
            
            if (result == null || result.getCount() <= 0) {
                this.messageBox("没有查询数据");
                return;
            }
            this.setValue("BAR_CODE", result.getValue("BAR_CODE", 0));
            this.setValue("ADM_TYPE", result.getValue("ADM_TYPE", 0));
            this.setValue("MR_NO", result.getValue("MR_NO", 0));
            this.setValue("REGION_CODE", result.getValue("REGION_CODE", 0));
            this.setValue("BED_NO", result.getValue("BED_NO", 0));

            Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
            this.setValue("PAT_NAME", pat.getName());
            this.setValue("SEX", pat.getSexString());
            Timestamp date = SystemTool.getInstance().getDate();
            this.setValue("AGE",
            		DateUtil.showAge(pat.getBirthday(),date));
            list=new ArrayList();
            for(int i=0;i<result.getCount();i++){
            	 if("PS".equals(result.getValue("ROUTE_CODEA", i))){
     	        	  table.setLockCell(i, "BATCH_NO", false);
          	          table.setLockCell(i, "SKINTEST_FLG", false);
     	        	  list.add(i);//如是皮试药品&&用法为皮试用法加list
                   }else{
            	      table.setLockCell(i, "BATCH_NO", true);
            	      table.setLockCell(i, "SKINTEST_FLG", true);
                        }
            	 
            	 if(!"Y".equals(result.getValue("LINK_MAIN_FLG", i))&&"0".equals(result.getValue("LINK_NO", i))){//不是连接医嘱连接号应为空，不能是0 为空
         	    	 result.setData("LINK_NO",i, "");
         	       }
            	  result.setData("SKINTEST_FLG", i, this.getSkin(result.getValue("SKINTEST_FLG", i)));
            }
            table.setParmValue(result);
        }
    }

    /**
     * 离院方法
     */
    public void onBedOut() {
        String region_code = this.getValueString("REGION_CODE");
        String bed_no = this.getValueString("BED_NO");
        String mr_no = this.getValueString("MR_NO");
        if ("".equals(region_code)) {
            this.messageBox("静点区不能为空");
            return;
        }
        if ("".equals(bed_no) || "".equals(mr_no)) {
            this.messageBox("床位和病案号不可同时为空不可为空！");
            return;
        }
        //校验是否存在未执行的液体，若存在给出提示 yanjing 20141226 add
        onSelectForBedOut();
        if (this.messageBox("提示", "确定是否离院", 2) == 0) {
            TParm parm = new TParm();
            Timestamp date = SystemTool.getInstance().getDate();
            TNull tnull = new TNull(Timestamp.class);
            parm.setData("REGION_CODE", region_code);
            parm.setData("BED_NO", bed_no);
            parm.setData("BED_STATUS", "0");
            parm.setData("MR_NO", "");
            parm.setData("CASE_NO", "");
            parm.setData("ADM_TYPE", "");
            parm.setData("PAT_NAME", "");
            parm.setData("SEX_CODE", "");
            parm.setData("PAT_STATUS", "");
            parm.setData("REGISTER_DATE", tnull);
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());

            TParm result = TIOM_AppServer.executeAction("action.phl.PHLAction",
                "onPhlBedOut", parm);
            if (result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            this.onClear();
        }
    }

    /**
     * 打印执行单
     */
    public void onPrint() {
        table.acceptText();
        if (table.getRowCount() <= 0) {
            this.messageBox("没有打印数据");
            return;
        }
            int row = -1;
            for (int i = 0; i < table.getRowCount(); i++) {
            	 if ("Y".equals(table.getItemString(i, "LFLG"))){
            		 row=i;
            		 break;
            	 }
            }
            if(row<=-1){
            	 this.messageBox("请选择需要打印的数据");
                 return;
            }
            String mr_NO=table.getItemString(row, "MR_NO");
            String mr_NO1="";
            for (int i = 0; i < table.getRowCount(); i++) {
              if (!"Y".equals(table.getItemString(i, "LFLG"))) {
                  continue;
              }else{
            	  mr_NO1=table.getItemString(i, "MR_NO");
            	  if(!mr_NO1.equals(mr_NO)){
            		 this.messageBox("请选择一个病患的医嘱 ");
            		 return;
            	  }
              }
            }
            //===========	new  end   by liling      
   
        Timestamp datetime = SystemTool.getInstance().getDate();
        // 打印数据
        TParm date = new TParm();
        // 表头数据
        date.setData("BAR_CODE","TEXT",this.getValue("MR_NO"));//添加条码
        date.setData("OpdNewExaName_I", "TEXT", "门（急）诊药物治疗执行单");
        date.setData("HOSP_NAME", "TEXT", Operator.getHospitalCHNFullName());
        date.setData("PAY_TYPE", "TEXT", "费别:"+table.getParmValue().getValue("CTZ_DESC",row));
        date.setData("DEPT_CODE", "TEXT","科别:" +OpdRxSheetTool.getInstance().getDeptName(this.getDeptCode()));
        date.setData("PAT_NAME", "TEXT", "姓名:" + this.getValueString("PAT_NAME"));
        date.setData("CLINIC_ROOM", "TEXT","诊间:" + table.getParmValue().getValue("CLINICROOM_DESC",row));
        date.setData("SEX_CODE", "TEXT","性别:" +this.getValue("SEX"));
        date.setData("DR_CODE", "TEXT","医生:" +table.getParmValue().getValue("DR_NAME",row));
       // date.setData("AGE", "TEXT","年龄:" +this.getValue("AGE"));
       // add wuxy 2017/7/26
        Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
        Timestamp time = pat.getBirthday();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        
        date.setData("AGE", "TEXT",
        				"出生日期:" +sdf.format(time));
        
        Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp temp = time == null ? sysDate : time;
		String age1 = "0";
		age1 = DateUtil.showAge(temp, sysDate);
		date.setData("AGE1", "TEXT", "年龄:" + age1);
        
       // end wuxy 2017/7/26
        
        date.setData("ADM_DATE", "TEXT","接诊时间:" +table.getParmValue().getValue("REG_DATE",row).toString().substring(0, 10));
        date.setData("MR_NO", "TEXT","病案号:" +this.getValue("MR_NO"));
        //add wuxy 2017/07/26 新增体重显示在报表中
        String mrNo = this.getValueString("MR_NO");
        String caseNo = table.getParmValue().getValue("CASE_NO",row);
        String sql = "SELECT WEIGHT FROM REG_PATADM WHERE MR_NO = '"+mr_NO+"' AND CASE_NO = '"+caseNo+"'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        
        date.setData("WEIGHT", "TEXT","体重:" +(result.getDouble("WEIGHT", 0)>0 ? result.getValue("WEIGHT", 0)+"Kg":"-"));
        
        
        //end wuxy 2017/07/26
        String strDiag = OpdRxSheetTool.getInstance().getIcdName(table.getParmValue().getValue("CASE_NO",row));
        int diagNum=0;
        if(strDiag.indexOf("#")!=-1){
        	String diag[]=strDiag.split("#") ;
        	StringBuffer buf=new StringBuffer();
        	if(diag.length>3){
        		diagNum=3;
        	}else{
        		diagNum=diag.length;
        	}
        	date.setData("DIAG", "TEXT", diag[0]);
        	for(int i=1;i<diagNum;i++){
        		buf.append(";"+diag[i]);
        	}
        	date.setData("DIAG3", "TEXT", "" + buf.toString().substring(1,buf.toString().length() ));
        }else{
        	date.setData("DIAG", "TEXT", strDiag);
        }
//        date.setData("DIAG", "TEXT",strDiag);
        date.setData("FOOT_DR", "TEXT","医师:");
        date.setData("SESSION_CODE","TEXT","时段:" +OpdRxSheetTool.getInstance().getSessionName(table.getParmValue().getValue("CASE_NO",row)));
        //add by sunqy 20140702 ----end----
//        date.setData("TITLE", "TEXT", Manager.getOrganization(). getHospitalCHNFullName(Operator.getRegion()) +"静点执行单");
//        date.setData("REGION_CODE", "TEXT","静点区域:" + this.getComboBox("REGION_CODE").getSelectedName());
//        date.setData("NAME", "TEXT", "姓名:" + this.getValueString("PAT_NAME"));
//        date.setData("BED_NO", "TEXT","床/座号:" + this.getComboBox("BED_NO").getSelectedName());
//        date.setData("DATE", "TEXT","打印时间:" +datetime.toString().substring(0, 10).replace('-', '/'));
//        date.setData("MR_NO", "TEXT","病案号:" +this.getValue("MR_NO"));
//        date.setData("SEX", "TEXT","性别:" +this.getValue("SEX"));
//        date.setData("AGE", "TEXT","年龄:" +this.getValue("AGE"));
//        date.setData("DEPT_CODE", "TEXT","科室:" +OpdRxSheetTool.getInstance().getDeptName(this.getDeptCode()));
        // 表格数据
        TParm parm = new TParm();
        DecimalFormat df = new DecimalFormat("#####0.000");
        for (int i = 0; i < table.getRowCount(); i++) {
      	 if (!"Y".equals(table.getItemString(i, "LFLG"))) {
             continue;
         }
        //======end liling modify 20140404    	
            parm.addData("LINK_NO",
            				("Y".equals(table.getItemString(i, "LINK_MAIN_FLG")) ? "主" : "  ") + "  " +
                            table.getItemString(i, "LINK_NO"));
            parm.addData("ORDER_DESC", table.getItemString(i, "ORDER_DESC")+"("+table.getParmValue().getValue( "SPECIFICATION",i)+")");
//            parm.addData("SPECIFICATION",
//            		table.getParmValue().getValue( "SPECIFICATION",i));
            parm.addData("USE_QTY",
                         table.getParmValue().getValue("MEDI_QTY", i)+
                         StringUtil.getDesc("SYS_UNIT", "UNIT_CHN_DESC","UNIT_CODE='" +table.getParmValue().getValue("MEDI_UNIT", i)+ "'")+
                         " "+df.format(table.getParmValue().getDouble("INFLUTION_RATE", i)));
//            parm.addData("UNIT",
//                         table.getParmValue().getValue("UNIT_CHN_DESC", i));
            parm.addData("FREQ_CODE",
                         table.getParmValue().getValue("FREQ_CODE", i));
            parm.addData("ROUTE_CODE",
                         table.getParmValue().getValue("ROUTE_CODE", i));
            parm.addData("DR_NOTE", table.getParmValue().getValue("DR_NOTE", i));//添加医师备注add by huangjw 20150107
            //parm.addData("EXEC_USER", table.getItemString(i, "EXECUTE_NAME"));
            //==liling start 20140415 添加皮试批号和皮试结果BATCH_NO;SKINTEST_FLG
//            parm.addData("BATCH_NO",
//                    table.getParmValue().getValue("BATCH_NO", i));
//            parm.addData("SKINTEST_FLG",
//                    table.getParmValue().getValue("SKINTEST_FLG", i));
            //====liling end
            Timestamp nowTime = SystemTool.getInstance().getDate();
//            RadioButton1
            TRadioButton RadioButton2 = (TRadioButton) getComponent("RadioButton2");
           // System.out.println(RadioButton2);
        //    System.out.println(RadioButton2.isSelected());
            if(RadioButton2.isSelected()){
//            	 parm.addData("EXEC_DATE",
//                         table.getItemString(i, "EXEC_DATE").substring(0, 10).
//                         replace('-', '/'));
            	 parm.addData("EXEC_USER", table.getItemString(i, "EXECUTE_NAME"));
            }else{
//            	parm.addData("EXEC_DATE",
//                		nowTime.toString().substring(0, 10).
//                             replace('-', '/'));
            	parm.addData("EXEC_USER", Operator.getName());
            }
            
            parm.addData("EXEC_DATE", "");
            parm.addData("EXEC_END_DATE", "");
            
            parm.addData("TAKE_DAYS",table.getParmValue().getValue("TAKE_DAYS", i));
            //parm.addData("NS_NOTE", table.getItemString(i, "NS_NOTE"));
        }

        if (parm.getCount("ORDER_DESC") == 0) {
            this.messageBox("没有打印数据");
            return;
        }
        parm.setCount(parm.getCount("ORDER_DESC"));
        parm.addData("SYSTEM", "COLUMNS", "LINK_NO");
        parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//        parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parm.addData("SYSTEM", "COLUMNS", "USE_QTY");
//        parm.addData("SYSTEM", "COLUMNS", "UNIT");
        parm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
        parm.addData("SYSTEM", "COLUMNS", "TAKE_DAYS");
        parm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
//        parm.addData("SYSTEM", "COLUMNS","DR_NOTE");//添加医师备注add by huangjw 20150107
//        parm.addData("SYSTEM", "COLUMNS", "BATCH_NO"); //==liling start 20140415 添加皮试批号和皮试结果    
//        parm.addData("SYSTEM", "COLUMNS", "SKINTEST_FLG"); //==liling start 20140415 添加皮试批号和皮试结果        
        parm.addData("SYSTEM", "COLUMNS", "EXEC_DATE");
        parm.addData("SYSTEM", "COLUMNS", "EXEC_END_DATE");
        parm.addData("SYSTEM", "COLUMNS", "EXEC_USER");
        //parm.addData("SYSTEM", "COLUMNS", "NS_NOTE");

        date.setData("TABLE1", parm.getData());
        date.setData("INFLUTION_RATE_TEXT", "TEXT", "滴速");
        String previewSwitch=IReportTool.getInstance().getPrintSwitch("OpdBottleLabelOrder_I_V45.previewSwitch");//预览开关
	        if(previewSwitch.equals(IReportTool.ON)){//添加开关 add by huangjw 20140718 start
		        // 调用打印方法
			        this.openPrintWindow(IReportTool.getInstance().getReportPath("OpdBottleLabelOrder_I_V45.jhw"),
			        		IReportTool.getInstance().getReportParm("OpdBottleLabelOrder_I_V45.class",date));
	        }else{
	
		        	this.openPrintWindow(IReportTool.getInstance().getReportPath("OpdBottleLabelOrder_I_V45.jhw"),
			        		IReportTool.getInstance().getReportParm("OpdBottleLabelOrder_I_V45.class",date),true);
	        }//  add by huangjw 20140718 end
    }
        	
    		
       
    /**
     * 瓶签回车事件
     */
    public void onBarCodeAction() {
        String bar_code = this.getValueString("BAR_CODE");
        String adm_type=this.getValueString("ADM_TYPE");//===liling 20140404
        //this.onClear();
        TParm parm = new TParm();
        parm.setData("BAR_CODE", bar_code);
        //======start  liling 20140404
        if("".equals(getValueString("ADM_TYPE"))){
          	 parm.setData("ADM_TYPE",getValueString("ADM_TYPE"));
            }
           if("O".equals(getValueString("ADM_TYPE"))){
          	 parm.setData("ADM_TYPE_O",getValueString("ADM_TYPE"));
           }
           if("E".equals(getValueString("ADM_TYPE"))){
          	 parm.setData("ADM_TYPE_E",getValueString("ADM_TYPE"));
           }
           //======end liling 20140404
        if (getRadioButton("RadioButton1").isSelected()) {
            parm.setData("EXEC_STATUS", "0");
        }
        else {
            parm.setData("EXEC_STATUS", "1");
        }
        TParm result = PhlExecuteTool.getInstance().onQuery(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
//        this.setValue("BAR_CODE", result.getValue("BAR_CODE", 0));
//        this.setValue("ADM_TYPE", result.getValue("ADM_TYPE", 0));
//        this.setValue("MR_NO", result.getValue("MR_NO", 0));
//        this.setValue("REGION_CODE", result.getValue("REGION_CODE", 0));
//        this.setValue("BED_NO", result.getValue("BED_NO", 0));
//
//        Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
//        this.setValue("PAT_NAME", pat.getName());
//        this.setValue("SEX", pat.getSexString());
//        Timestamp date = SystemTool.getInstance().getDate();
//        this.setValue("AGE",
//                      StringUtil.getInstance().showAge(pat.getBirthday(), date));
//
//        table.setParmValue(result);
//
//        for (int i = 0; i < table.getRowCount(); i++) {
//            if (bar_code.equals(table.getParmValue().getValue("BAR_CODE", i))) {
//                table.setItem(i, "EXEC_STATUS", "Y");
//            }
//            else {
//                table.setItem(i, "EXEC_STATUS", "N");
//            }
//        }
    }

    /**
     * 区域变更事件
     */
    public void onChangeRegion() {
        this.setValue("BED_NO", "");
        ( (TComboPHLBed)this.getComponent("BED_NO")).setRegionCode(this.
            getValueString("REGION_CODE"));
        ( (TComboPHLBed)this.getComponent("BED_NO")).onQuery();
    }

    /**
     * 完成状态变更事件
     */
    public void onChangeStatus() {
        onQuery();
    }

    /**
     * 病案号查询(回车事件)
     */
    public void onMrNoAction() {
        String mr_no = PatTool.getInstance().checkMrno(this.getValueString(
            "MR_NO"));
        this.setValue("MR_NO", mr_no);
//        Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
//        this.setValue("PAT_NAME", pat.getName());
//        this.setValue("SEX", pat.getSexString());
//        Timestamp date = SystemTool.getInstance().getDate();
//        this.setValue("AGE", StringUtil.getInstance().showAge(pat.getBirthday(),
//            date));
        //System.out.println("===="+PHLSQL.getPHLRegisterList(mr_no));
        
        TParm parm = new TParm(TJDODBTool.getInstance().select(PHLSQL.
            getPHLRegisterList(mr_no)));
        if (parm == null || parm.getCount("START_DATE") <= 0) {
            this.messageBox("没有报道信息");
            return;
        }
        case_no = parm.getValue("CASE_NO", 0);
        start_date = parm.getValue("START_DATE", 0);
        this.setValue("BED_NO", parm.getValue("BED_NO", 0));
        onQuery();
        getTextField("BAR_CODE").grabFocus();
    }

    /**
     * 全选事件
     */
    public void onSelectAllAction() {
        String flg = "N";
        if ("N".equals(this.getValueString("SELECT_ALL"))) {
            flg = "N";
        }
        else {
            flg = "Y";
        }
        for (int i = 0; i < table.getRowCount(); i++) {
           table.setItem(i, "LFLG", flg);
       }
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
    
    /**
     * 医疗卡读卡
     * ===zhangp
     */
    public void onEkt(){
    	TParm ektParm = EKTIO.getInstance().TXreadEKT();
    	String mrNo = ektParm.getValue("MR_NO");
    	setValue("MR_NO", mrNo);
    	onMrNoAction();
    }
    /**
     * add caoyong 20130114 
     * 点击事件
     */
    /**
	 * 单击事件
	 */
	public void onTABLEClicked(int row){
		 TParm result = table.getParmValue().getRow(row);
//		  System.out.println("行号row为：："+row);
		  if(row>=0){
			  callFunction("UI|skiResult|setEnabled", true);
		  }else{
			  callFunction("UI|skiResult|setEnabled", false); 
		  }
		 this.setValue("BAR_CODE", result.getValue("BAR_CODE"));//=========liling 20140401 添加条码号
		 this.setValue("ADM_TYPE", result.getValue("ADM_TYPE"));
//       this.setValue("MR_NO", result.getValue("MR_NO", 0));
		 case_no = result.getValue("CASE_NO");
       this.setValue("REGION_CODE", result.getValue("REGION_CODE"));
       this.setValue("BED_NO", result.getValue("BED_NO"));
       this.setValue("MR_NO", result.getValue("MR_NO"));
       this.setDeptCode(result.getValue("DEPT_CODE"));
       this.setRxNo(result.getValue("RX_NO"));
       this.setSeqNo(result.getValue("SEQ_NO"));
       this.setOrderCode(result.getValue("ORDER_CODE"));
       this.setOrderNo(result.getValue("ORDER_NO"));
       Pat pat = Pat.onQueryByMrNo( result.getValue("MR_NO"));
       this.setValue("PAT_NAME", pat.getName());
       this.setValue("SEX", pat.getSexString());
       
       Timestamp date = SystemTool.getInstance().getDate();
       this.setValue("AGE",DateUtil.showAge(pat.getBirthday(), date));// modify caoyong 20140401
       
       this.setBatchno(result.getValue("BATCH_NO"));//药房方药后生成的皮试批号
       this.setSkinflg(result.getValue("SKINTEST_FLG"));
	}
	
	/**
	 * 皮试结果 add caoyong2014
	 */
	public void onSkiResult(){
		//TParm resultParm=table.getParmValue();
		
		int row =table.getSelectedRow();
		if(row<0){
			this.messageBox("请选择一行数据");
			return;
			
		}
		boolean flag=false;
		
		for(int i=0;i<list.size();i++){
			int a=TypeTool.getInt(list.get(i));
			if(row==a){
				flag=true;
				break;
			}
		 }
		
		if(!flag){
		  this.messageBox("此药品用法不是皮试用法");	
		  return;
		}
		TParm parm=new TParm();
		parm.setData("PHL", "PHL");
		parm.setData("BATCH_NO", this.getBatchno());
		parm.setData("SKINTEST_FLG",this.getSkinflg());
//		parm.setData("DEPT_CODE",this.getDeptCode());
		parm.setData("CASE_NO",case_no);
		parm.setData("RX_NO",this.getRxNo());
		parm.setData("SEQ_NO",this.getSeqNo());
		parm.setData("ORDER_NO",this.getOrderNo());
		parm.setData("ORDER_CODE",this.getOrderCode());
		Object obj = (TParm) this.openDialog("%ROOT%\\config\\inw\\INWSkiResult.x", parm, true);
		TParm result = (TParm) obj;
		String psResult="";
		if(result.getValue("SKINTEST_NOTE",0).equals("0")){
			psResult = "(-)阴性";
		}else if(result.getValue("SKINTEST_NOTE",0).equals("1")){
			psResult = "(+)阳性";
		}
		 //result.getValue("BATCH_NO",0)
		table.setItem(row, "BATCH_NO", result.getValue("BATCH_NO",0));
		table.setItem(row, "SKINTEST_FLG", psResult);
		table.acceptText() ;
	}
	
	/**add caoyong 2014/4/1
	 * 查询数据显示汉字
	 * @param skin
	 * @return
	 */
	public String getSkin(String skin){
		String skinDesc="";
		if("0".equals(skin)){
			skinDesc="(-)阴性";
		}else if("1".equals(skin)){
			skinDesc="(+)阳性";
		}
		return skinDesc;
	}
	/**
	 * 一组液 当选择一条时另一条必须也被选中
	 * add caoyong 2014/4/4
	 * @param obj
	 * @return
	 */
   public boolean onCheckBox(Object obj) {
	   
		TTable tables = (TTable) obj;
		TParm resultParm=table.getParmValue().getRow(tables.getSelectedRow());
		String rxno=resultParm.getValue("RX_NO");
		String mrmo=resultParm.getValue("MR_NO");
		String linkMainflg=resultParm.getValue("LINK_MAIN_FLG");
		String linkNO=resultParm.getValue("LINK_NO");
		int row=tables.getSelectedRow();
		TParm parm=table.getParmValue();
		int col = tables.getSelectedColumn();
		String	columnName =  tables.getDataStoreColumnName(col);
			if ("LFLG".equals(columnName)) {
			if( tables.getParmValue().getBoolean("LFLG",row)){
				tables.getParmValue().setData("LFLG",row,"N");
				if("Y".equals(linkMainflg)&&!"".equals(linkNO)){//是连组的主项
					for(int i=row;i<table.getRowCount();i++){
						if(rxno.equals(parm.getValue("RX_NO",i))&&mrmo.equals(parm.getValue("MR_NO",i))){//同一个处方号和同一个人
							if(linkNO.equals(parm.getValue("LINK_NO",i))){//组不为空的
							table.setItem(i, "LFLG", "N");
							}
						}
					}
					
				}
			}else{
				
				tables.getParmValue().setData("LFLG",row,"Y");
				if("Y".equals(linkMainflg)&&!"".equals(linkNO)){//是连组的主项
					for(int i=row;i<table.getRowCount();i++){//同一个处方号和同一个人
						if(rxno.equals(parm.getValue("RX_NO",i))&&mrmo.equals(parm.getValue("MR_NO",i))){
							if(linkNO.equals(parm.getValue("LINK_NO",i))){//组不为空的
							table.setItem(i, "LFLG", "Y");
							}
						}
					}
				}
			}
		}
		return true;

	}
   /**
    * 离院时查询是否有未执行的液体
    * yanjing 20141229
    */
   private void onSelectForBedOut(){
	   getRadioButton("RadioButton1").setSelected(true);
	   TParm parm = new TParm();
	    if (!"".equals(getValueString("BAR_CODE"))) {
	          parm.setData("BAR_CODE", getValueString("BAR_CODE"));
	        }
	    //$-------------modify 病患来源 20140103 caoyong start----------
	    if("".equals(getValueString("ADM_TYPE"))){
	   	 parm.setData("ADM_TYPE",getValueString("ADM_TYPE"));
	     }
	    if("O".equals(getValueString("ADM_TYPE"))){
	   	 parm.setData("ADM_TYPE_O",getValueString("ADM_TYPE"));
	    }
	    if("E".equals(getValueString("ADM_TYPE"))){
	   	 parm.setData("ADM_TYPE_E",getValueString("ADM_TYPE"));
	    }
	   //$-------------20140103 caoyong end----------
   
       if (!"".equals(getValueString("MR_NO"))) {
           parm.setData("MR_NO", getValueString("MR_NO"));
       }
       if (!"".equals(getValueString("REGION_CODE"))) {
           parm.setData("REGION_CODE", getValueString("REGION_CODE"));
       }
       if (!"".equals(getValueString("BED_NO"))) {
           parm.setData("BED_NO", getValueString("BED_NO"));
       }
       if (getRadioButton("RadioButton1").isSelected()) {
           parm.setData("EXEC_STATUS", "0");
           
       }
       else {
           parm.setData("EXEC_STATUS", "1");
//           Sflg=false;
       }
       TParm result = PhlExecuteTool.getInstance().onQuery(parm);
       
       if (result == null || result.getCount() <= 0) {
//           this.messageBox("没有查询数据");
           return;
       }else{
    	   this.messageBox("存在未执行的液体。");
    	   this.onQuery();
       }
   }
   
   /**
    * 取医嘱
    * 病患静点执行的时候，医生站又增加组液
    * yanjing 20150119
    */
   public void onFetch(){
	   TParm parm = new TParm();
	   parm.setData("CASE_NO", case_no);
	   parm.setData("MR_NO", this.getValue("MR_NO"));
	   parm.setData("ADM_TYPE", this.getValue("ADM_TYPE"));
	   parm.setData("PAT_NAME", this.getValue("PAT_NAME"));
	   TParm result = new TParm();
	   result = (TParm) this.openDialog(
				"%ROOT%\\config\\phl\\PHLGetOrder.x", parm, true);
	   getRadioButton("RadioButton1").setSelected(true);
	   this.onQuery();
	   
   }
	
}
