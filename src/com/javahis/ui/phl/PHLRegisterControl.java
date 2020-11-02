package com.javahis.ui.phl;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.phl.PHLSQL;
import jdo.phl.PhlBedTool;
import jdo.phl.PhlRegionTool;
import jdo.phl.PhlRegisterTool;
import jdo.reg.ClinicRoomTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.combo.TComboOperatorReg;
import com.javahis.system.combo.TComboPHLBed;
import com.javahis.system.combo.TComboSession;
import com.javahis.system.textFormat.TextFormatREGClinicRoomForReg;
import com.javahis.system.textFormat.TextFormatSYSDeptForReg;
import com.javahis.system.textFormat.TextFormatSYSOperatorForReg;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 静点室报到
 * </p>
 *
 * <p>
 * Description: 静点室报到
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis1.0
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */

public class PHLRegisterControl
    extends TControl {

    private TTable table_m;

    private TTable table_d;
    boolean isSaveFlg=false;//判断是保存打印，还是打印按钮add by huangjw 20141222
    // 保存状态
    private boolean save_flg;
    
    public PHLRegisterControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
    	this.getTable("TABLE_D").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,"onCheckBox");
    	
        // 取得传入参数
        Object obj = getParameter();
        if (obj instanceof TParm) {
            TParm parm = (TParm) obj;
            String adm_date = parm.getValue("ADM_DATE");
            String mr_no = parm.getValue("MR_NO");
            String clinicroom_code = parm.getValue("CLINICROOM_CODE");
            intiPageByONW(adm_date, mr_no, clinicroom_code);
        }
        else {
            initPopedem();
            // 初始画面数据
            initPage();
        }
        save_flg = true;
        callFunction("UI|CLINICROOM_NO|onQuery");

    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化
        table_m = this.getTable("TABLE_M");
        table_d = this.getTable("TABLE_D");
        // 初始化验收时间
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("START_DATE", date);
    }

    /**
     * 初始化权限管控
     */
    private void initPopedem() {
        // 权限管控
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
                       // onChangeRegion();
                        break;
                    }
                }
            }
        }
    }

    /**
     * 门诊护士站调用时初始化界面
     */
    private void intiPageByONW(String adm_date, String mr_no,
                               String clinicroom_code) {
        // 设定界面控件不可输入
        callFunction("UI|START_DATE|setEnabled", false);
        callFunction("UI|MR_NO|setEnabled", false);
        callFunction("UI|ADM_TYPE|setEnabled", false);
        callFunction("UI|SESSION_CODE|setEnabled", false);
        callFunction("UI|CLINICROOM_CODE|setEnabled", false);
        callFunction("UI|DEPT_CODE|setEnabled", false);
        callFunction("UI|DR_CODE|setEnabled", false);
        callFunction("UI|REGION_CODE|setEnabled", false);
        // 传入参数
        this.setValue("START_DATE", adm_date);
        this.setValue("MR_NO", mr_no);
        this.setValue("CLINICROOM_CODE", clinicroom_code);
        // 查找诊室对应的静点室
        TParm parm = ClinicRoomTool.getInstance().selectdata(clinicroom_code);
        if (parm.getCount() <= 0) {
            this.messageBox("该诊室没有设定对应的静点室");
            return;
        }
        this.setValue("REGION_CODE", parm.getValue("PHL_REGION_CODE", 0));
        this.onQuery();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        table_m.removeRowAll();
        table_d.removeRowAll();
        TParm parm = new TParm();
        String start_date = getValueString("START_DATE");
        if ("".equals(start_date)) {
            this.messageBox("就诊日期不可为空");
        }
        parm.setData("ADM_DATE", this.getValue("START_DATE"));

        if (!"".equals(getValueString("MR_NO"))) {
            parm.setData("MR_NO", getValueString("MR_NO"));
        }
         if("".equals(getValueString("ADM_TYPE"))){
        	 parm.setData("ADM_TYPE",getValueString("ADM_TYPE"));
          }
         if("O".equals(getValueString("ADM_TYPE"))){
        	 parm.setData("ADM_TYPE_O",getValueString("ADM_TYPE"));
         }
         if("E".equals(getValueString("ADM_TYPE"))){
        	 parm.setData("ADM_TYPE_E",getValueString("ADM_TYPE"));
         }
        if (!"".equals(getValueString("SESSION_CODE"))) {
            parm.setData("SESSION_CODE", getValueString("SESSION_CODE"));
        }
        if (!"".equals(getValueString("DEPT_CODE"))) {
            parm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
        }
        if (!"".equals(getValueString("CLINICROOM_CODE"))) {
            parm.setData("CLINICROOM_NO", getValueString("CLINICROOM_CODE"));//modify caoyong 20140103
        }
        if (!"".equals(getValueString("DR_CODE"))) {
            parm.setData("DR_CODE", getValueString("DR_CODE"));
        }
        
        TParm result = PhlRegisterTool.getInstance().onQueryRegister(parm);
        if (result == null || result.getCount("CASE_NO") <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        //=====================================================add by huangjw 20141112 start
        for(int i=0;i<result.getCount("CASE_NO");i++){
        	int count=0;
        	TParm reParm=result.getRow(i);
        	TParm bedParm=new  TParm(TJDODBTool.getInstance().select("SELECT BED_NO FROM PHL_BED " +
        	" WHERE REGION_CODE='"+this.getValueString("REGION_CODE")+"' AND MR_NO='"+reParm.getValue("MR_NO")+"' AND CASE_NO='"+reParm.getValue("CASE_NO")+"'"));
        	if(bedParm.getCount()>0){
        		continue;
        	}
        	TParm param=new TParm();
        	param.setData("MR_NO",result.getValue("MR_NO",i));
        	param.setData("CASE_NO",result.getValue("CASE_NO",i));
        	TParm orderParm = PhlRegisterTool.getInstance().onQueryOrderDetail(param);
        	if(orderParm.getCount()<=0){
        		continue;
        	}
        	TParm newParm=new TParm();
        	TParm newOrderParm=new TParm();
        	for(int j=0;j<orderParm.getCount();j++){
        		newParm.setData("ADM_TYPE", result.getValue("ADM_TYPE",i));
        		newParm.setData("CASE_NO", result.getValue("CASE_NO",i));
        		newParm.setData("ORDER_NO", orderParm.getValue("RX_NO", j));
        		newParm.setData("SEQ_NO", orderParm.getInt("SEQ_NO", j));
        		newParm.setData("ORDER_CODE", orderParm.getValue("ORDER_CODE", j));
        		newOrderParm = PhlRegisterTool.getInstance().onQueryoldOrder(
        				newParm);
        		if (newOrderParm.getCount() > 0) {
        			count++;
        			
				}
        	}
        	if(count==orderParm.getCount()){
        		result.removeRow(i);
        	}
        }
        if(result.getCount("CASE_NO")<=0){
        	this.messageBox("没有数据");
        	return;
        }
      //=====================================================add by huangjw 20141112 end
        table_m.setParmValue(result);
    }

    /**
     * 保存方法
     */
    public void onSave() {
        int row = table_m.getSelectedRow();
        if (row < 0) {
            this.messageBox("没有病患信息");
            return;
        }
        if (!save_flg) {
            this.messageBox("医嘱已保存");
            return;
        }
        TParm result = new TParm();
        // 检查病患是否报到
        TParm inparm = new TParm();
        inparm.setData("MR_NO", table_m.getItemString(row, "MR_NO"));
        inparm.setData("CASE_NO", table_m.getItemString(row, "CASE_NO"));
        result = PhlBedTool.getInstance().onQuery(inparm);
        if(result.getCount()>0){
           if(!result.getValue("BED_DESC", 0).equals(this.getValueString("BED_CODE"))){
        	     this.messageBox("该病患已入床");
        	      return;
           }
        }else {
//            this.messageBox("该病患已报到");
//            return;
        
        if ("".equals(this.getValueString("REGION_CODE"))) {
            this.messageBox("请选择静点区域");
            return;
        }
        if ("".equals(this.getValueString("BED_CODE"))) {
            this.messageBox("请选择静点床位");
            return;
        }
        }
        // 检查是否存在需要新增的医嘱
        table_d.acceptText();
        boolean flg = false;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                flg = true;
                break;
            }
        }
        if (!flg) {
            this.messageBox("没有选择医嘱");
            return;
        }

        TParm parm = new TParm();
        // 更新床位信息
        TParm bedparm = new TParm();
        bedparm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        //bedparm.setData("BED_NO", this.getValueString("BED_CODE"));
        bedparm.setData("BED_NO", this.getValueString("BEDNO"));
        bedparm.setData("BED_STATUS", "1");
        bedparm.setData("MR_NO", table_m.getItemString(row, "MR_NO"));
        bedparm.setData("CASE_NO", table_m.getItemString(row, "CASE_NO"));
        bedparm.setData("PAT_STATUS", "1");
        Timestamp date = SystemTool.getInstance().getDate();;
        bedparm.setData("REGISTER_DATE", date);
        bedparm.setData("OPT_USER", Operator.getID());
        bedparm.setData("OPT_DATE", date);
        bedparm.setData("OPT_TERM", Operator.getIP());
        bedparm.setData("ADM_TYPE", table_m.getItemString(row, "ADM_TYPE"));
        bedparm.setData("PAT_NAME", table_m.getItemString(row, "PAT_NAME"));
        bedparm.setData("SEX_CODE", table_m.getItemString(row, "SEX_CODE"));
        parm.setData("BED_PARM", bedparm.getData());

        // 新增医嘱信息
        TParm orderparm = new TParm();
        TParm oldorderparm=new TParm();//已经报到过的医嘱 add caoyong 2014/4/3
        TParm tparm=new TParm();
        String start_date = SystemTool.getInstance().getDate().toString();
        start_date = start_date.substring(0, 4) + start_date.substring(5, 7) +
            start_date.substring(8, 10) + start_date.substring(11, 13) +
            start_date.substring(14, 16) + start_date.substring(17, 19);
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!"Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            
            // addd caoyong 2014/4/3 查询已经报到过的医嘱 start----
            //tparm.setData("START_DATE",  start_date);
            tparm.setData("ADM_TYPE",  table_m.getItemData(row,"ADM_TYPE"));
            tparm.setData("CASE_NO",  table_m.getItemData(row,"CASE_NO"));
            tparm.setData("ORDER_NO", table_d.getParmValue().getValue("RX_NO", i));
            tparm.setData("SEQ_NO", table_d.getParmValue().getInt("SEQ_NO", i));
            tparm.setData("ORDER_CODE",table_d.getParmValue().getValue("ORDER_CODE", i));
            
             oldorderparm=PhlRegisterTool.getInstance().onQueryoldOrder(tparm);
             if(oldorderparm.getCount()>0){
            	 continue;
             }
             
            orderparm.setData("START_DATE", i, start_date);
            orderparm.setData("ADM_TYPE", i, table_m.getItemData(row,
                "ADM_TYPE"));
            orderparm.setData("CASE_NO", i, table_m.getItemData(row,
                "CASE_NO"));
            orderparm.setData("ORDER_NO", i,
                              table_d.getParmValue().getValue("RX_NO", i));
            orderparm.setData("SEQ_NO", i,
                              table_d.getParmValue().getInt("SEQ_NO", i));
            orderparm.setData("ORDER_CODE", i,
                              table_d.getParmValue().getValue("ORDER_CODE", i));
            orderparm.setData("MR_NO", i, table_m.getItemData(row,
                "MR_NO"));
            orderparm.setData("DR_CODE", i,
                              table_d.getParmValue().getValue("DR_CODE", i));
            //-------modify caoyong 20130114  start
            Timestamp orderDttm = StringTool.getTimestamp(table_d.getItemString(i, "ORDER_DATE"),"yyyy/MM/dd HH:mm:ss");
            orderparm.setData("ORDER_DTTM",i,orderDttm);
          //-------modify caoyong 20130114 end 
            
            orderparm.setData("LINK_MAIN_FLG", i, "Y".equals(table_d.
                getItemString(i, "LINKMAIN_FLG")) ? "Y" : "N");

            orderparm.setData("LINK_NO", i,
                              table_d.getItemData(i, "LINK_NO") == null ? 0 :
                              table_d.getItemData(i, "LINK_NO"));
            orderparm.setData("ROUTE_CODE", i,
                              table_d.getParmValue().getValue("ROUTE_CODE", i));
            orderparm.setData("FREQ_CODE", i,
                              table_d.getParmValue().getValue("FREQ_CODE", i));
            orderparm.setData("TAKE_DAYS", i,
                              table_d.getParmValue().getData("TAKE_DAYS", i));
            // 调用方法取得瓶签号
            orderparm.setData("BAR_CODE", i, this.getBarCode(table_d.
                getParmValue().getValue("RX_NO", i),
                table_d.getItemString(i, "LINK_NO")));
            //需要打印的瓶签 的医嘱 add caoyon 2014/4/4 start
            if( "Y".equals(table_d.getItemString(i, "LINKMAIN_FLG"))&&!"".equals(table_d.getItemData(i, "LINK_NO"))){
            	if(!"0".equals(table_d.getItemData(i, "LINK_NO"))){
            		orderparm.setData("BAR_CODE_PRINT_FLG", i,"Y");
            	}
            }else{
            	   orderparm.setData("BAR_CODE_PRINT_FLG", i, "N");
            }
            //需要打印的瓶签 的医嘱 add caoyon 2014/4/4 end 
            orderparm.setData("BAR_CODE_PRINT_FLG", i, "Y");
            orderparm.setData("EXEC_STATUS", i, "0");
            orderparm.setData("DR_NOTE", i,
                              table_d.getItemString(i, "DR_NOTE"));
            orderparm.setData("NS_NOTE", i,
                              table_d.getParmValue().getValue("NS_NOTE", i));
            orderparm.setData("OPT_USER", i, Operator.getID());
            orderparm.setData("OPT_DATE", i, date);
            orderparm.setData("OPT_TERM", i, Operator.getIP());
        }
        parm.setData("ORDER_PARM", orderparm.getData());
        result = TIOM_AppServer.executeAction("action.phl.PHLAction",
                                              "onPhlRegister", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        // 打印床/座卡
        isSaveFlg=true;
        this.onBedPrint();
        // 打印瓶签
        this.onBottlePrintwo(false);
        onClear();
    }
    /**
     * 医疗卡读卡
     * ===caoyong
     */
    public void onEkt(){
    	TParm ektParm = EKTIO.getInstance().TXreadEKT();
    	String mrNo = ektParm.getValue("MR_NO");
    	setValue("MR_NO", mrNo);
    	this.onQuery();
    }
    /**
     * 清空方法
     */
    public void onClear() {
        // 清空VALUE
        String clear =
            "MR_NO;ADM_TYPE;SESSION_CODE;DEPT_CODE;CLINICROOM_CODE;"
            + "DR_CODE;BED_CODE;BEDNO";
        this.clearValue(clear);
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        initPage();
        save_flg = true;
        isSaveFlg=false;
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
        Object result = openDialog("%ROOT%\\config\\phl\\PHLCard.x",
                                   parm);
        if (result != null) {
            TParm resultParm = (TParm) result;
            if ("1".equals(resultParm.getValue("BED_STATUS"))) {
                this.setValue("MR_NO", resultParm.getValue("MR_NO"));
                this.onQuery();
            }
        }
    }

    /**
     * 门急诊补充计价
     */
    public void onBill() {
        int row = table_m.getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择病患");
            return;
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO", table_m.getItemString(row, "CASE_NO"));
        parm.setData("MR_NO", table_m.getItemString(row, "MR_NO"));
        parm.setData("SYSTEM","ONW");
        parm.setData("ONW_TYPE",table_m.getItemString(row, "ADM_TYPE"));
        this.openDialog("%ROOT%\\config\\opb\\OPBChargesM.x",
                                   parm);
    }
    /**
     * 瓶签打印
     */
    public void onBottlePrint(){
    	onBottlePrintwo(true);
    }
    public void onBottlePrintwo(boolean flag) {
        int row = table_m.getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择需要打印瓶签的病患");
            return;
        }
        // 检查病患是否报到
        TParm inparm = new TParm();
        inparm.setData("MR_NO", table_m.getItemString(row, "MR_NO"));
        inparm.setData("CASE_NO", table_m.getItemString(row, "CASE_NO"));
        TParm result = PhlBedTool.getInstance().onQuery(inparm);
        if (result.getCount() <= 0) {
            this.messageBox("该病人尚未报到");
            return;
        }

        // 瓶签打印数据
        // 瓶签条码
        String bar_code = "";
        // 连接号
        String link_no = "";
        // 床号
        String bed_no = result.getValue("BED_DESC", 0);

        Pat pat = Pat.onQueryByMrNo(table_m.getItemString(table_m.getSelectedRow(),
            "MR_NO"));
        // 病患姓名
        String name = pat.getName();
        // 性别
        String sex = pat.getSexString();
        // 年龄
        Timestamp date = SystemTool.getInstance().getDate();
//        String age = StringUtil.getInstance().showAge(pat.getBirthday(), date);
        String age = DateUtil.showAge(pat.getBirthday(), date);//==liling 20140703 modify
        TParm parmData = new TParm();
        table_d.acceptText();
        TParm tablePrm  = table_d.getParmValue();
        for (int i = 0; i < table_d.getRowCount(); i++) {
           if(!flag){
	           if (!"Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
	               continue;
	            }
           }else{
	           if (!"Y".equals(table_d.getItemString(i, "REPORT_FLG"))) {
	        	   continue;
	           }
           }
            link_no = tablePrm.getValue("LINK_NO", i);
            if ("".equals(link_no)&&!"0".equals(link_no)) {
                continue;
            }
            bar_code = this.getBarCode(tablePrm.getValue("RX_NO", i), link_no);
            // 瓶签信息部分
            TParm parm=table_m.getParmValue().getRow(row);
            String mrNo = parm.getValue("MR_NO");
    		String drName = parm.getValue("DR_NAME");
    		String sexCode = sex;
    		String patName = parm.getValue("PAT_NAME");
    		String bedDesc=parm.getValue("BED_DESC");
    		String deptName = parm.getValue("DEPT_CHN_DESC");
    		
    		
    		parmData.addData("BAR_CODE_NO", bar_code);
    		parmData.addData("BAR_CODE", bar_code);
            parmData.addData("LINK_NO", link_no);
            parmData.addData("DEPT_TYPE",  "门诊注射药");
	        parmData.addData("MR_NO",mrNo);
	        parmData.addData("DEPT_CODE",  deptName);
	        parmData.addData("BED_CODE",  bedDesc);
	        parmData.addData("PAT_CODE", patName);
	        parmData.addData("SEX_CODE", sexCode );
	        parmData.addData("AGE",age);
	        // add by wangb 2017/08/23 增加出生日期
	        Timestamp birthdate = pat.getBirthday();
	        String birthdateStr = "";
	        if (null != birthdate) {
	        	birthdateStr = birthdate.toString();
	        	if (birthdateStr.length() >= 10) {
					birthdateStr = birthdateStr.substring(0, 4) + "年"
							+ birthdateStr.substring(5, 7) + "月"
							+ birthdateStr.substring(8, 10) + "日";
	        	}
	        }
	        parmData.addData("BIRTHDATE", birthdateStr);
	        parmData.addData("DOCTOR_CODE",drName);
	        parmData.addData("NORSE_CODE",Operator.getName());
            
            parmData.addData("GOODS_DESC", table_d.getItemString(i, "ORDER_DESC"));
            parmData.addData("SPECIFICATION",table_d.getItemString(i, "SPECIFICATION") );
            parmData.addData("QTY", tablePrm.getValue("DISPENSE_QTY", i));
            parmData.addData("INFLUTION_RATE", tablePrm.getValue("INFLUTION_RATE", i));
            parmData.addData("ROUTE_CODE",tablePrm.getValue("ROUTE_CODEA", i) );
            parmData.addData("FREQ_CODE",tablePrm.getValue("FREQ_CODEA", i) );
            parmData.addData("SEND_CODE",table_d.getItemString(i, "PHA_DISPENSE_NAME") );
            
        }
        if (parmData == null || parmData.getCount("BAR_CODE_NO") <= 0) {
        	if(flag){
            this.messageBox("没有打印数据");
        	}
            return;
        }
        // 打印瓶签
        onPrintBottleData(parmData,flag);
    }

   

    /**
     * 瓶签打印数据
     * @param parm TParm
     */
    private void onPrintBottleData(TParm parm,boolean flag) {
        // 打印数据
        TParm date = new TParm();
        // 用于保存不同的瓶签号(BAR_CODE)
        Map<String,Object> map = new HashMap<String,Object> ();
        for (int i = 0; i < parm.getCount("BAR_CODE"); i++) {
            if (map.isEmpty()) {
                map.put(parm.getValue("BAR_CODE", i), i);
            }
            else {
                if (map.containsKey(parm.getValue("BAR_CODE", i))) {
                    continue;
                }
                else {
                    map.put(parm.getValue("BAR_CODE", i), i);
                }
            }
        }

        Iterator iterator = map.keySet().iterator();
        String bar_code = "";
        int row = 0;

        while (iterator.hasNext()) {
            // 表格数据
            TParm parmData = new TParm();
            bar_code = TypeTool.getString(iterator.next());
            row = TypeTool.getInt(map.get(bar_code));
           
	        
            date.setData("LINK_NO", "TEXT", parm.getValue("LINK_NO", row));
            date.setData("DEPT_TYPE", "TEXT", "门诊【注射】药");
            date.setData("MR_NO", "TEXT" ,parm.getValue("MR_NO", row));
            date.setData("DEPT_CODE", "TEXT", parm.getValue("DEPT_CODE", row));
            date.setData("BED_CODE","TEXT",  this.getValueString("BED_CODE"));
            date.setData("PAT_CODE", "TEXT", parm.getValue("PAT_CODE", row));
            date.setData("SEX_CODE", "TEXT",parm.getValue("SEX_CODE", row) );
            date.setData("AGE", "TEXT",parm.getValue("AGE", row));
            date.setData("ROUTE_CODE", "TEXT",parm.getData("ROUTE_CODE", row));
			date.setData("FREQ_CODE", "TEXT",parm.getData("FREQ_CODE", row));
            date.setData("SEND_CODE","TEXT",parm.getValue("SEND_CODE", row));
            date.setData("DOCTOR_CODE","TEXT",parm.getValue("DOCTOR_CODE", row));
            date.setData("BAR_CODE", "TEXT",bar_code);
            date.setData("NORSE_CODE","TEXT", parm.getValue("NORSE_CODE", row));
            // add by wangb 2017/08/23 增加出生日期
            date.setData("BIRTHDATE","TEXT", parm.getValue("BIRTHDATE", row));
            int count = 0;
            DecimalFormat df = new DecimalFormat("#####0.000");
            for (int i = row; i < parm.getCount("BAR_CODE"); i++) {
                if (bar_code.equals(parm.getValue("BAR_CODE", i))) {
                
//                    parmData.addData("GOODS_DESC", parm.getValue("GOODS_DESC", i)+ parm.getValue("SPECIFICATION", i));
                	parmData.addData("GOODS_DESC", parm.getValue("GOODS_DESC", i));//去掉规格modify by huangjw 20150210
//                    parmData.addData("SPECIFICATION",  parm.getValue("SPECIFICATION", i));
                	parmData.addData("QTY",parm.getValue("QTY", i));
                    parmData.addData("INFLUTION_RATE",df.format(parm.getDouble("INFLUTION_RATE", i)));
//                    parmData.addData("ROUTE_CODE", parm.getValue("ROUTE_CODE", i));
//                    parmData.addData("FREQ_CODE",parm.getValue("FREQ_CODE", i));
                    count++;
                }
                else {
                    break;
                }
            }
            // 不足5个医嘱手动补齐
            if (count < 5) {
                for (int i = count; i < 6; i++) {
                    parmData.addData("GOODS_DESC", "");
//                    parmData.addData("SPECIFICATION", "");
                    parmData.addData("QTY", "");
                    parmData.addData("INFLUTION_RATE", "");
//                    parmData.addData("ROUTE_CODE", "");
//                    parmData.addData("FREQ_CODE", "");
                }
            }
                parmData.setCount(5);
                parmData.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
//                parmData.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
                parmData.addData("SYSTEM", "COLUMNS", "QTY");
                parmData.addData("SYSTEM", "COLUMNS", "INFLUTION_RATE");
//                parmData.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
//                parmData.addData("SYSTEM", "COLUMNS","FREQ_CODE");
            System.out.println("parmData---" + parmData.getData());
            date.setData("TABLE", parmData.getData());
            System.out.println("date---" + date);
            // 调用打印方法
//            this.openPrintWindow("%ROOT%\\config\\prt\\PHL\\PhlBarCode.jhw",date);
            //this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\phaInfusion.jhw",date); //==liling 20140703 modify 和门诊发药输液签一致
            //=======================配置打印开关 start huangjw
            if(flag){//true为打印按钮
	            String prtSwitch=IReportTool.getInstance().getPrintSwitch("phaInfusion_new.prtSwitch");
	        	String previewSwitch=IReportTool.getInstance().getPrintSwitch("phaInfusion_new.previewSwitch");
	        	if(prtSwitch.equals(IReportTool.ON)){
	        		if (previewSwitch.equals(IReportTool.ON)) {
		        		this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInfusion_new.jhw"),
		        				IReportTool.getInstance().getReportParm("phaInfusion_new.class",date));
	        		}else{
	        			this.openPrintWindow(IReportTool.getInstance()
								.getReportPath("phaInfusion_new.jhw"),
								IReportTool.getInstance().getReportParm("phaInfusion_new.class", date),true);
	        		}
	        	}
            }else{//false为保存后打印
            	String prtSwitch=IReportTool.getInstance().getPrintSwitch("phaInfusion.prtSwitch");
	        	String previewSwitch=IReportTool.getInstance().getPrintSwitch("phaInfusion.previewSwitch");
	        	if(prtSwitch.equals(IReportTool.ON)){
	        		if (previewSwitch.equals(IReportTool.ON)) {
		        		this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInfusion.jhw"),
		        				IReportTool.getInstance().getReportParm("phaInfusion.class",date));
	        		}else{
	        			this.openPrintWindow(IReportTool.getInstance()
								.getReportPath("phaInfusion.jhw"),
								IReportTool.getInstance().getReportParm("phaInfusion.class", date),true);
	        		}
	        	}
            }
          //=======================配置打印开关 end huangjw
        }

    }

    /**
     * 床/座打印
     */
    public void onBedPrint() {
        int row = table_m.getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择需要打印床/座卡的病患");
            return;
        }
        // 检查病患是否报到
        TParm inparm = new TParm();
        inparm.setData("MR_NO", table_m.getItemString(row, "MR_NO"));
        inparm.setData("CASE_NO", table_m.getItemString(row, "CASE_NO"));
        TParm result = PhlBedTool.getInstance().onQuery(inparm);
        if (result.getCount() <= 0) {
            this.messageBox("该病人尚未报到");
            return;
        }
        // 床/座数据
        String sql = PHLSQL.getPHLBedCard(table_m.getItemString(row, "MR_NO"),
                                          table_m.getItemString(row, "CASE_NO"));
        TParm parmData = new TParm(TJDODBTool.getInstance().select(sql));
        if (parmData == null || parmData.getCount() <= 0) {
            this.messageBox("没有打印数据");
            return;
        }
        //打印床/座瓶签
        onPrintBedData(parmData,isSaveFlg);
    }

    /**
     * 床/座瓶签打印数据
     * @param parm TParm
     */
    public void onPrintBedData(TParm parm,boolean flg) {
        int row = table_m.getSelectedRow();
        // 打印数据
        TParm date = new TParm();
        // 表头数据
        date.setData("ORDER_DTTM", "TEXT",
                     "就诊:" +
                     table_m.getItemString(row, "ADM_DATE").substring(0, 10).
                     replace('-', '/'));
        String register_date = parm.getValue("REGISTER_DATE", 0);
        date.setData("REGISTER_DATE", "TEXT",
                     "报到:" + register_date.substring(0, 19).replace('-', '/'));
        date.setData("NAME", "TEXT",
                     "姓名:" + table_m.getItemString(row, "PAT_NAME"));
        date.setData("SEX", "TEXT",
                     "1".equals(parm.getValue("SEX_CODE", 0)) ? "性别:男" : "性别:女");
        Timestamp datetime = SystemTool.getInstance().getDate();
        date.setData("AGE", "TEXT",
                     "年龄:" + DateUtil.showAge(parm.
            getTimestamp("BIRTH_DATE", 0), datetime));
        date.setData("CTZ", "TEXT",
                     "身份:" + parm.getValue("CTZ_DESC", 0));
        date.setData("REGION_CODE", "TEXT",
                     "静点区域:" + parm.getValue("REGION_DESC", 0));
        date.setData("BED_NO", "TEXT",
                     "床号:" + parm.getValue("BED_DESC", 0));
        date.setData("MR_NO","TEXT",parm.getValue("MR_NO",0));
        // 调用打印方法
        
        //this.openPrintWindow("%ROOT%\\config\\prt\\PHL\\PHLBedCard.jhw",
        //                     date);
        //=======================配置打印开关 start huangjw
        if(flg){//true为保存后打印
        	String prtSwitch=IReportTool.getInstance().getPrintSwitch("PHLBedCard.prtSwitch");
        	String previewSwitch=IReportTool.getInstance().getPrintSwitch("PHLBedCard.previewSwitch");
        	if(prtSwitch.equals(IReportTool.ON)){
        		if (previewSwitch.equals(IReportTool.ON)) {
	        		this.openPrintWindow(IReportTool.getInstance().getReportPath("PHLBedCard.jhw"),
	        				IReportTool.getInstance().getReportParm("PHLBedCard.class",date));
        		}else{
        			this.openPrintWindow(IReportTool.getInstance()
							.getReportPath("PHLBedCard.jhw"),
							IReportTool.getInstance().getReportParm("PHLBedCard.class", date),true);
        		}
        	}
        }else{//false为打印按钮
        	String prtSwitch=IReportTool.getInstance().getPrintSwitch("PHLBedCard_new.prtSwitch");
        	String previewSwitch=IReportTool.getInstance().getPrintSwitch("PHLBedCard_new.previewSwitch");
        	if(prtSwitch.equals(IReportTool.ON)){
        		if (previewSwitch.equals(IReportTool.ON)) {
	        		this.openPrintWindow(IReportTool.getInstance().getReportPath("PHLBedCard_new.jhw"),
	        				IReportTool.getInstance().getReportParm("PHLBedCard_new.class",date));
        		}else{
        			this.openPrintWindow(IReportTool.getInstance()
							.getReportPath("PHLBedCard_new.jhw"),
							IReportTool.getInstance().getReportParm("PHLBedCard_new.class", date),true);
        		}
        	}
        }
      //=======================配置打印开关 end huangjw
    }

    /**
     * 表格(CLNDIAG_TABLE)单击事件
     */
	public void onTableMClicked() {
		table_d.setSelectionMode(0);
		table_d.removeRowAll();
		int row = table_m.getSelectedRow();
		TParm tparm = table_m.getParmValue().getRow(row);
		// $----------add caoyong 20130123 显示医疗卡余额 start
		double ektAmt = EKTpreDebtTool.getInstance().getMasterForPHL(
				table_m.getItemString(row, "CASE_NO"),
				table_m.getItemString(row, "MR_NO"));
		this.setValue("EKT_AMT", ektAmt);
		// $----------add caoyong 20130123 显示医疗卡余额 end
		this.setValue("MR_NO", tparm.getValue("MR_NO"));
		this.setValue("ADM_TYPE", tparm.getValue("ADM_TYPE"));
		initDeptList();
		this.setValue("SESSION_CODE", tparm.getValue("SESSION_CODE"));
		this.setValue("DEPT_CODE", tparm.getValue("DEPT_CODE"));
		this.setValue("CLINICROOM_CODE", tparm.getValue("CLINICROOM_NO"));
		this.setValue("DR_CODE", tparm.getValue("DR_CODE"));
		this.setValue("BED_CODE", tparm.getValue("BED_DESC"));
		if (row != -1) {
			TParm parm = new TParm();
			parm.setData("MR_NO", table_m.getItemString(row, "MR_NO"));
			parm.setData("CASE_NO", table_m.getItemString(row, "CASE_NO"));
			TParm result = PhlRegisterTool.getInstance().onQueryOrderDetail(
					parm);
			
			if (result == null || result.getCount() <= 0) {
				this.messageBox("没有医嘱信息");
				return;
			}
			String type = "医生已开立";
			// --------add caoyong 已报到的医嘱 2014、4、3 START
			TParm sparm = new TParm();// add caoyong 已报到的医嘱 2014、4、3
			TParm oldorderparm = new TParm();// add caoyong 已报到的医嘱 2014、4、3

			for (int i = 0; i < result.getCount(); i++) {
				sparm.setData("ADM_TYPE", table_m.getItemData(row, "ADM_TYPE"));
				sparm.setData("CASE_NO", table_m.getItemData(row, "CASE_NO"));
				sparm.setData("ORDER_NO", result.getValue("RX_NO", i));
				sparm.setData("SEQ_NO", result.getInt("SEQ_NO", i));
				sparm.setData("ORDER_CODE", result.getValue("ORDER_CODE", i));
				oldorderparm = PhlRegisterTool.getInstance().onQueryoldOrder(
						sparm);
				if (oldorderparm.getCount() > 0) {
					result.setData("REPORT_FLG", i, "Y");
					result.setData("SELECT_FLG", i, "N");
				}
				
				if (!"Y".equals(result.getValue("LINK_MAIN_FLG", i))
						&& "0".equals(result.getValue("LINK_NO", i))) {// 不是连接医嘱连接号应为空，不能是0
																		// 为空
					result.setData("LINK_NO", i, "");
				}
				// --------add caoyong 已报到的医嘱 2014、4、3 START

				// ---- add caoyong 添加药品状态 start 20140226
				if (!"".equals(result.getValue("PHA_RETN_CODE", i))
						&& result.getValue("PHA_RETN_CODE", i) != null) {
					type = "药房已退药";
				} else if (!"".equals(result.getValue("PHA_DISPENSE_CODE", i))
						&& result.getValue("PHA_DISPENSE_CODE", i) != null) {
					type = "药房已发药";
				} else if (!"".equals(result.getValue("PHA_DOSAGE_CODE", i))
						&& result.getValue("PHA_DOSAGE_CODE", i) != null) {
					type = "药房已配药";
				} else if (!"".equals(result.getValue("PHA_CHECK_CODE", i))
						&& result.getValue("PHA_CHECK_CODE", i) != null) {
					type = "药房已审核";
				}
				result.setData("EXEC_FLG", i, type);
				//================================================modify by huangjw 20141112 start
				if("".equals(this.getValueString("BED_CODE"))){
					if(result.getValue("REPORT_FLG",i).equals("Y")){
						result.removeRow(i);
					}
				}
				//================================================modify by huangjw 20141112 end
			}
			// add caoyong 添加药品状态 end 20140226
			table_d.setParmValue(result);

			/*
			 * TComboPHLBed bed = (TComboPHLBed)this.getComponent("BED_CODE");
			 * bed.setBedStatus("0"); bed.onQuery();
			 */
		}
	}

    /**
     * 病案号变更事件
     */
    public void onChangeMrNo() {
        String mr_no = PatTool.getInstance().checkMrno(this.
            getValueString("MR_NO"));
        this.setValue("MR_NO", mr_no);
        TParm parm = new TParm(TJDODBTool.getInstance().select(PHLSQL.
            getLastAdmDateByMrNo(mr_no)));
        if (parm.getCount() <= 0) {
            this.messageBox("不存在就诊记录");
            return;
        }
        this.setValue("START_DATE",
                      parm.getValue("ADM_DATE", 0).substring(0, 10).
                      replace('-', '/'));
        this.onQuery();
    }

    /**
     * 病患来源变更事件
     */
    public void onChangeAdmType() {
        this.setValue("SESSION_CODE", "");
        ( (TComboSession)this.getComponent("SESSION_CODE")).setAdmType(this.
            getValueString("ADM_TYPE"));
        ( (TComboSession)this.getComponent("SESSION_CODE")).onQuery();
        initDeptList();
        initClinicRoomList();
        initDrCodeList();
    }

    /**
     * 时段变更事件
     */
    public void onChangeSeesion() {
        initDeptList();
        initClinicRoomList();
        initDrCodeList();
    }

    /**
     * 科别变更事件
     */
    public void onChangeDept() {
        initDrCodeList();
    }

    /**
     * 区域变更事件
     */
    public void onChangeRegion() {
        this.setValue("BED_CODE", "");
        ( (TComboPHLBed)this.getComponent("BED_CODE")).setRegionCode(this.
            getValueString("REGION_CODE"));
        ( (TComboPHLBed)this.getComponent("BED_CODE")).onQuery();
        
        ( (TextFormatREGClinicRoomForReg)this.getComponent(
            "CLINICROOM_CODE")).setPhlRegionCode(this.getValueString(
            "REGION_CODE"));
        ( (TextFormatREGClinicRoomForReg)this.getComponent("CLINICROOM_CODE")).
            onQuery();
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
     * 初始化科别
     */
    private void initDeptList() {
        this.setValue("DEPT_CODE", "");
        String adm_type = this.getValueString("ADM_TYPE");
        String adm_date = this.getValueString("START_DATE");
        adm_date = adm_date.substring(0, 4) + adm_date.substring(5, 7) +
            adm_date.substring(8, 10);
        if (!"".equals(adm_type)) {
            ( (TextFormatSYSDeptForReg)this.getComponent("DEPT_CODE")).setAdmType(
                adm_type);
            ( (TextFormatSYSDeptForReg)this.getComponent("DEPT_CODE")).setAdmDate(
                adm_date);
        }
        String session_code = this.getValueString("SESSION_CODE");
        if (!"".equals(session_code)) {
            ( (TextFormatSYSDeptForReg)this.getComponent("DEPT_CODE")).
                setSessionCode(session_code);
            ( (TextFormatSYSDeptForReg)this.getComponent("DEPT_CODE")).setAdmDate(
                adm_date);
        }
        ( (TextFormatSYSDeptForReg)this.getComponent("DEPT_CODE")).onQuery();
    }

    /**
     * 初始化诊区
     */
    private void initClinicRoomList() {
        String adm_type = this.getValueString("ADM_TYPE");
        String phl_region_code = getValueString("REGION_CODE");
        //this.messageBox(phl_region_code);
        if (!"".equals(adm_type)) {
            ( (TextFormatREGClinicRoomForReg)this.getComponent(
                "CLINICROOM_CODE")).setAdmType(adm_type);
        }
        String session_code = this.getValueString("SESSION_CODE");
        if (!"".equals(session_code)) {
            ( (TextFormatREGClinicRoomForReg)this.getComponent(
                "CLINICROOM_CODE")).setSessionCode(session_code);
        }
        ( (TextFormatREGClinicRoomForReg)this.getComponent(
            "CLINICROOM_CODE")).setPhlRegionCode(phl_region_code);
        ( (TextFormatREGClinicRoomForReg)this.getComponent("CLINICROOM_CODE")).
            onQuery();
    }

    /**
     * 初始化就诊医生
     */
    private void initDrCodeList() {
        this.setValue("DR_CODE", "");
        String adm_type = this.getValueString("ADM_TYPE");
        if (!"".equals(adm_type)) {
            ( (TextFormatREGClinicRoomForReg)this.getComponent("CLINICROOM_CODE")).
                setAdmType(adm_type);
        }
        String session_code = this.getValueString("SESSION_CODE");
        if (!"".equals(session_code)) {
            ( (TextFormatSYSOperatorForReg)this.getComponent("DR_CODE")).
                setSessionCode(session_code);
        }
        String dept_code = this.getValueString("DEPT_CODE");
        if (!"".equals(dept_code)) {
            ( (TextFormatSYSOperatorForReg)this.getComponent("DR_CODE")).
                setDeptCode(dept_code);
        }
        ( (TComboOperatorReg)this.getComponent("DR_CODE")).onQuery();
    }

    /**
     * 取得瓶签号
     * @param orderNo String
     * @param linkNo String
     * @return String
     */
    private String getBarCode(String orderNo, String linkNo) {
        if ("".equals(linkNo)) {
            return "";
        }
        linkNo = "00".substring(0, 2 - linkNo.length()) + linkNo.trim();
        return orderNo + linkNo;
    }
    
	  
	/**
	 * 复选框连组关联 
	 * @param obj
	 * @return
	 */
	public boolean onCheckBox(Object obj) {
			TTable tables = (TTable) obj;
			TParm resultParm=table_d.getParmValue().getRow(tables.getSelectedRow());
			String rxno=resultParm.getValue("RX_NO");
			String linkMainflg=resultParm.getValue("LINKMAIN_FLG");
			String linkNO=resultParm.getValue("LINK_NO");
			int row=tables.getSelectedRow();
			TParm parm=table_d.getParmValue();
			if( tables.getParmValue().getBoolean("SELECT_FLG",row)){
				tables.getParmValue().setData("SELECT_FLG",row,"N");
				if("Y".equals(linkMainflg)&&!"".equals(linkNO)){//是连组的主项
					for(int i=row;i<table_d.getRowCount();i++){
						if(rxno.equals(parm.getValue("RX_NO",i))){//同一个处方号
							if(linkNO.equals(parm.getValue("LINK_NO",i))){//组不为空的
								table_d.setItem(i, "SELECT_FLG", "N");
							}
						}
					}
					
				}
			}else{
				if("Y".equals(linkMainflg)&&!"".equals(linkNO)){//是连组的主项
					for(int i=row;i<table_d.getRowCount();i++){//同一个处方号和同一个人
						if(rxno.equals(parm.getValue("RX_NO",i))){
							if(linkNO.equals(parm.getValue("LINK_NO",i))){//组号相同的
								table_d.setItem(i, "SELECT_FLG", "Y");
							}
						}
					}
				}
			}
			return true;
	  }
	/**
	 * 床位检索
	 */
	public void onSelectCard() {
        if ("".equals(getValueString("REGION_CODE"))) {
            this.messageBox("请选择静点区域");
            return;
        }
        TParm parm = new TParm();
        parm.setData("REGION_CODE", getValueString("REGION_CODE"));
        Object result = openDialog("%ROOT%\\config\\phl\\PHLSelectCard.x",
                                   parm);
        if (result != null) {
               TParm resultParm = (TParm) result;
               if("1".equals(resultParm.getValue("BED_STATUS"))){
            	   this.messageBox("此床位已被占用");
            	   return;
               }
                this.setValue("BED_CODE", resultParm.getValue("BED_DESC"));
                this.setValue("BEDNO", resultParm.getValue("BED_NO"));///add  by huangjw 20150325
            }
    }
}
