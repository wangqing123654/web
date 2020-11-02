package com.javahis.ui.med;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.hl7.Hl7Communications;
import jdo.med.MedLisSendTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p> Title:检验送检清单 </p>
 * 
 * <p> Description: 检验送检清单 </p>
 * 
 * <p> Copyright: Copyright (c) 2012 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 * @author shibl
 * @version 1.0
 */
public class MedLisSendControl extends TControl {//wanglong refactor 20140422
    private TTable table;
    private BILComparator compare = new BILComparator();
    private boolean ascending = false;
    private int sortColumn = -1;
	private TParm sendHL7Parm = new TParm();// 会调用HL7接口的数据
	private String admType;
	
	/**
	 * 初始化
	 */
    public void onInit() {
        super.onInit();
        table=(TTable)this.getComponent("TABLE");
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                                "onCheckBoxValue");
        addListener(table);// 排序监听
        this.onClear();
        this.setValue("LIS_RE_USER", Operator.getID());
        Object obj = this.getParameter();// 入参
        if (obj != null) {
            if (obj instanceof String) {
                admType = this.getParameter().toString();
                callFunction("UI|" + admType + "|setSelected", true);
                onChooseAdmType(admType);// 包含onClear()
                if (admType.equalsIgnoreCase("OE")) {
                    callFunction("UI|I|setEnabled", false);
                } else if (admType.equalsIgnoreCase("I")) {
                    callFunction("UI|OE|setEnabled", false);
                }
            }
        }
        this.setValue("LIS_RE_USER", Operator.getID());
    }

	/**
	 * 条码号回车事件
	 */
    public void onMedApplyNo() {
        TParm parm = new TParm();
        String medApplyNo = this.getValueString("MED_APPLY_NO");// 条码号
        String lis_re_user=this.getValueString("LIS_RE_USER");
        String send_user=this.getValueString("SEND_USER");
        parm.setData("MED_APPLY_NO", medApplyNo);
        boolean reFlag = (Boolean) this.callFunction("UI|RE_YES|isSelected"); // 未接收
        parm.setData("RE_FLG", reFlag);
        TParm result = execQuery(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount() <= 0) {
            this.messageBox("没有数据");
            return;
        }
        if (table.getRowCount() > 0) {
            boolean flg = true;
            TParm tableParm = table.getParmValue();
            if(result.getCount() == 1){//输入单个条码号进行判断
	            for (int i = 0; i < tableParm.getCount("MED_APPLY_NO"); i++) {
	                if (tableParm.getValue("MED_APPLY_NO", i).equals(result.getValue("MED_APPLY_NO", 0))) {
	                    this.messageBox("已扫描此条码！");
	                    flg = false;
	                    break;
	                }
	            }
            }else{//条码号为空时回车，结果显示table中剩余的部分
            	TParm newParm=new TParm();
            	boolean newflg=true;
            	//int k=0;
            	for(int j = 0;j<result.getCount();j++){
            		for(int i = 0;i<tableParm.getCount("MED_APPLY_NO");i++){
            			if(tableParm.getValue("MED_APPLY_NO", i).equals(result.getValue("MED_APPLY_NO", j))){
            				newflg=false;
            				break;
            			}
            		}
            		if(newflg){
            			newParm.addRowData(result,j);
            			//System.out.println("tt-----------------------------tttt---------------------------tt"+newParm);
            			//k++;
            		}
            		newflg=true;
            	}
            	if(newParm.getCount()<0){
            		this.messageBox("已经显示全部信息");
            		flg=false;
            	}else
            		result=newParm;
            }
            if (flg) {
            	if(getRadioButton("RE_NO").isSelected()){
            		for(int i=0;i<result.getCount();i++){
		            	result.setData("LIS_RE_USER",i, lis_re_user);
		            	result.setData("SEND_USER", i,send_user);
            		}
            	}
                tableParm.addParm(result);
                table.setParmValue(tableParm);
            	
            }
        } else {
        	if(getRadioButton("RE_NO").isSelected()){
        		for(int i=0;i<result.getCount();i++){
	            	result.setData("LIS_RE_USER",i, lis_re_user);
	            	result.setData("SEND_USER",i, send_user);
        		}
        	}
            table.setParmValue(result);
        }
        this.setValue("MED_APPLY_NO", "");
    }
	
	/**
     * 查询
     */
    public void onQuery() {
        if(this.getValue("START_DATE")==null){
            this.messageBox("请选择开始日期");
            return;
        }
        if(this.getValue("END_DATE")==null){
            this.messageBox("请选择结束日期");
            return;
        }
        TParm parm = new TParm();
        String clinicAreaCode = this.getValueString("CLINICAREA_CODE"); // 诊区
        String stationCode = this.getValueString("STATION_CODE"); // 病区
        boolean reFlag = (Boolean) this.callFunction("UI|RE_YES|isSelected"); // 已接收
        String startDate =
                StringTool.getString((Timestamp) this.getValue("START_DATE"), "yyyyMMddHHmmss");// 开始时间
        String endDate =
                StringTool.getString((Timestamp) this.getValue("END_DATE"), "yyyyMMddHHmmss"); // 结束时间
      
        String reStartDate =
                StringTool.getString((Timestamp) this.getValue("RE_START_DATE"), "yyyyMMddHHmmss");// 交接起日
        String reEndDate =
                StringTool.getString((Timestamp) this.getValue("RE_END_DATE"), "yyyyMMddHHmmss"); // 交接迄日
        String lisReUser = this.getValueString("LIS_RE_USER"); // 送检人员
        String medApplyNo = this.getValueString("MED_APPLY_NO");// 条码号
        String send_user = this.getValueString("SEND_USER");//运送人员
        parm.setData("CLINICAREA_CODE", clinicAreaCode);
        parm.setData("STATION_CODE", stationCode);
        parm.setData("RE_FLG", reFlag);
        if (!reFlag) {
            parm.setData("START_DATE", startDate);
            parm.setData("END_DATE", endDate);
        } else {
            parm.setData("RE_START_DATE", reStartDate);
            parm.setData("RE_END_DATE", reEndDate);
        }
        parm.setData("LIS_RE_USER", lisReUser);
        parm.setData("SEND_USER",send_user);
        parm.setData("MED_APPLY_NO", medApplyNo);
        TParm result = execQuery(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount() <= 0) {
            this.messageBox("没有数据");
            table.setParmValue(new TParm());
            return;
        }
        if(getRadioButton("RE_NO").isSelected()){
	        for(int i=0;i<result.getCount();i++){
		        //if("".equals(result.getValue("LIS_RE_USER",i)))
		        	//result.setData("LIS_RE_USER",i,Operator.getID());
	        }
        }
        table.setParmValue(result);
        this.setValue("ALL", "Y");
    }
    
	/**
     * 执行查询
     */
    public TParm execQuery(TParm parm) {
        TParm result = new TParm();
        if ((Boolean) callFunction("UI|OE|isSelected") == true) {// 门急诊
            result = MedLisSendTool.getInstance().selectLisDetailByOE(parm);
        } else {// 住院
            result = MedLisSendTool.getInstance().selectLisDetailByI(parm);
        }
        return result;
    }

	/**
	 * 保存动作
	 */
	public void onSave() {
		table.acceptText();
		TParm parmValue = table.getParmValue();
        if (parmValue == null) {
            this.messageBox("无保存数据！");
            return;
        }
        TParm parm = new TParm();
        Timestamp now = SystemTool.getInstance().getDate();
        for (int i = 0; i < parmValue.getCount(); i++) {
            if (!parmValue.getBoolean("FLG", i)) {
                continue;
            }
//            if (StringUtil.isNullString(parmValue.getValue("LIS_RE_USER", i))) {
//                continue;
//            }
//            if (StringUtil.isNullString(parmValue.getValue("SEND_USER", i))) {
//                continue;
//            }
            parm.addData("CASE_NO", parmValue.getValue("CASE_NO", i));
            parm.addData("MED_APPLY_NO", parmValue.getValue("MED_APPLY_NO", i));
            parm.addData("LIS_RE_USER",parmValue.getValue("LIS_RE_USER",i));
            parm.addData("SEND_USER", parmValue.getValue("SEND_USER",i));//add by huangjw 20140718
            parm.addData("LIS_RE_DATE", now);
            parm.addData("OPT_USER", Operator.getID());
            parm.addData("OPT_TERM", Operator.getIP());
            sendHL7Parm.addData("CASE_NO", parmValue.getValue("CASE_NO", i));
            sendHL7Parm.addData("PAT_NAME", parmValue.getValue("PAT_NAME", i));
            sendHL7Parm.addData("LAB_NO", parmValue.getValue("MED_APPLY_NO", i));
            sendHL7Parm.addData("CAT1_TYPE", parmValue.getValue("CAT1_TYPE", i));
            if ((Boolean) callFunction("UI|OE|isSelected") == true) {// 门急诊
                parm.addData("RX_NO", parmValue.getValue("RX_NO", i));
                parm.addData("SEQ_NO", parmValue.getInt("SEQ_NO", i));
                sendHL7Parm.addData("ORDER_NO", parmValue.getValue("RX_NO", i));
                sendHL7Parm.addData("SEQ_NO", parmValue.getInt("SEQ_NO", i));
            } else {// 住院
                parm.addData("ORDER_NO", parmValue.getValue("ORDER_NO", i));
                parm.addData("ORDER_SEQ", parmValue.getInt("ORDER_SEQ", i));
                parm.addData("START_DTTM", parmValue.getValue("START_DTTM", i));
                sendHL7Parm.addData("ORDER_NO", parmValue.getValue("ORDER_NO", i));
                sendHL7Parm.addData("SEQ_NO", parmValue.getInt("ORDER_SEQ", i));
            }
        }
        parm.setCount(parm.getCount("CASE_NO"));
        sendHL7Parm.setCount(sendHL7Parm.getCount("CASE_NO"));
        TParm result = new TParm();
        result = MedLisSendTool.getInstance().updateMedApplyLisData(parm);//更新MED_APPLY的接收人员信息
        if (result.getErrCode() < 0) {
            this.messageBox("保存失败1！" + result.getErrText());
            return;
        }
        if ((Boolean) callFunction("UI|I|isSelected") == true) {// 住院
            result = MedLisSendTool.getInstance().updateOdidspnmLisData(parm);//更新ODI_DSPNM的接收人员信息
            if (result.getErrCode() < 0) {
                this.messageBox("保存失败2！" + result.getErrText());
                return;
            }
        }
        this.messageBox("保存成功！");
		sendHL7Msg(sendHL7Parm);//发送HL7信息
		savePrint();
		table.removeRowAll();
	}

	/**
	 * 消息重送
	 */
    public void onReSendHL7() {
        TParm parmValue = table.getParmValue();
        for (int i = 0; i < parmValue.getCount(); i++) {
            if (!parmValue.getBoolean("FLG", i)) continue;
            sendHL7Parm.addData("CASE_NO", parmValue.getValue("CASE_NO", i));
            sendHL7Parm.addData("PAT_NAME", parmValue.getValue("PAT_NAME", i));
            sendHL7Parm.addData("LAB_NO", parmValue.getValue("MED_APPLY_NO", i));
            sendHL7Parm.addData("CAT1_TYPE", parmValue.getValue("CAT1_TYPE", i));
            if ((Boolean) callFunction("UI|OE|isSelected") == true) {// 门急诊
                sendHL7Parm.addData("ORDER_NO", parmValue.getValue("RX_NO", i));
                sendHL7Parm.addData("SEQ_NO", parmValue.getInt("SEQ_NO", i));
            } else {// 住院
                sendHL7Parm.addData("ORDER_NO", parmValue.getValue("ORDER_NO", i));
                sendHL7Parm.addData("SEQ_NO", parmValue.getInt("ORDER_SEQ", i));
            }
        }
        sendHL7Msg(sendHL7Parm);
    }

    /**
     * 发送HL7消息
     * @param hl7Parm
     */
    public void sendHL7Msg(TParm hl7Parm) {
        if (hl7Parm.getCount() <= 0) {
            return;
        }
        List list = new ArrayList();
        for (int i = 0; i < hl7Parm.getCount(); i++) {
            if (hl7Parm.getValue("MED_APPLY_NO", i).equals("")) {
                continue;
            }
            list.add(hl7Parm.getRow(i));
        }
        TParm resultParm = Hl7Communications.getInstance().Hl7SendLis(list); // 调用接口
        if (resultParm.getErrCode() < 0) {
            this.messageBox(resultParm.getErrText());
            return;
        }
        this.messageBox("发送成功");
    }

    /**
     * 选择就诊类别单选按钮事件
     */
    public void onChooseAdmType(String tag) {//wanglong add 20140422
        this.onClear();
        if (tag.equals("OE")) {// 门急诊
            callFunction("UI|CLINICAREA_CODE|setEnabled", true);
            callFunction("UI|STATION_CODE|setEnabled", false);
            table.setHeader("选,35,boolean;病案号,120;姓名,120;条码号,120;项目名称,200;采血时间,150,timestamp,yyyy/MM/dd HH:mm:ss;医嘱备注,150;交接时间,150,timestamp,yyyy/MM/dd HH:mm:ss;送检人员,100,LIS_RE_USER;运送人员,100");
            table.setParmMap("FLG;MR_NO;PAT_NAME;MED_APPLY_NO;ORDER_DESC;BLOOD_DATE;DR_NOTE;LIS_RE_DATE;LIS_RE_USER;SEND_USER;CASE_NO;RX_NO;SEQ_NO");
            table.setColumnHorizontalAlignmentData("1,left;3,left;5,left;7,left;9,left");
            table.setLockColumns("1,2,3,4,5,6,7");
        } else if (tag.equals("I")) {// 住院
            callFunction("UI|CLINICAREA_CODE|setEnabled", false);
            callFunction("UI|STATION_CODE|setEnabled", true);
            table.setHeader("选,35,boolean;床号,70;病案号,120;姓名,120;条码号,120;项目名称,200;护士执行时间,150,timestamp,yyyy/MM/dd HH:mm:ss;医嘱备注,150;交接时间,150,timestamp,yyyy/MM/dd HH:mm:ss;送检人员,100,LIS_RE_USER;运送人员,100");
            table.setParmMap("FLG;BED_NO;MR_NO;PAT_NAME;MED_APPLY_NO;ORDER_DESC;NS_EXEC_DATE;DR_NOTE;LIS_RE_DATE;LIS_RE_USER;SEND_USER;CASE_NO;RX_NO;SEQ_NO");
            table.setColumnHorizontalAlignmentData("1,left;3,left;5,left;7,left;9,left");
            table.setLockColumns("1,2,3,4,5,6,7,8");
        }
    }
    
	/**
	 * 改变事件
	 */
	public void onChooseREState() {
        if ((Boolean) this.callFunction("UI|RE_YES|isSelected")==true) {// 已接收
            callFunction("UI|save|setEnabled", false);
            callFunction("UI|print|setEnabled", true);
            callFunction("UI|RE_START_DATE|setEnabled", true);
            callFunction("UI|RE_END_DATE|setEnabled", true);
            Timestamp sysDate = SystemTool.getInstance().getDate();
            String tDate = StringTool.getString(sysDate, "yyyyMMdd");
            this.setValue("RE_START_DATE",
                          StringTool.getTimestamp(tDate + "000000", "yyyyMMddHHmmss")); // 默认设置起始日期
            this.setValue("RE_END_DATE", StringTool.getTimestamp(("" + sysDate).substring(0, 19)
                    .replaceAll("-", "").replaceAll(":", ""), "yyyyMMdd HHmmss")); // 默认设置终止日期
            table.removeRowAll();
        } else {
            callFunction("UI|save|setEnabled", true);
            callFunction("UI|print|setEnabled", false);
            callFunction("UI|RE_START_DATE|setEnabled", false);
            callFunction("UI|RE_END_DATE|setEnabled", false);
            this.setValue("RE_START_DATE", "");
            this.setValue("RE_END_DATE", "");
            table.removeRowAll(); 
        }
    }
	
	/**
	 * 保存后打印
	 */
	public void savePrint(){
		if (table.getRowCount() <= 0) {
            this.messageBox("无打印数据");
            return;
        }
		TParm parm = table.getParmValue();
        TParm printData = new TParm();
        int count = 0;
        for (int i = 0; i < parm.getCount("MR_NO"); i++) {
            if (!parm.getBoolean("FLG", i)) continue;
            printData.addData("MR_NO", parm.getValue("MR_NO", i));
            printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
            printData.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
            printData.addData("MED_APPLY_NO", parm.getValue("MED_APPLY_NO", i));
//            printData
//                    .addData("LIS_RE_DATE",
//                             StringTool.getString(parm.getTimestamp("LIS_RE_DATE", i), "HH:mm:ss"));
            printData.addData("DR_NOTE", parm.getValue("DR_NOTE", i));
            String lis_re_user="";
            if ((Boolean) callFunction("UI|OE|isSelected") == true) {// 门急诊
            	String sql="SELECT A.LIS_RE_DATE ,B.USER_NAME FROM MED_APPLY A,SYS_OPERATOR B" +
            			" WHERE A.APPLICATION_NO='"+parm.getValue("MED_APPLY_NO",i)+"' AND A.CAT1_TYPE='LIS' AND A.LIS_RE_USER=B.USER_ID ";
            	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
            	lis_re_user=result.getValue("USER_NAME",0);
            	printData.addData("LIS_RE_DATE", StringTool.getString(result.getTimestamp("LIS_RE_DATE",0), "HH:mm:ss"));
                printData.addData("BLOOD_DATE", StringTool.getString(parm
                        .getTimestamp("BLOOD_DATE", i), "HH:mm:ss"));
            } else {// 住院
            	String sql="SELECT A.LIS_RE_DATE,C.USER_NAME FROM ODI_DSPNM A, ODI_ORDER B,SYS_OPERATOR C " +
            		   " WHERE  B.MED_APPLY_NO = '"+parm.getValue("MED_APPLY_NO",i)+"' " +
            		   " AND A.CASE_NO = B.CASE_NO" +
                       " AND A.ORDER_NO = B.ORDER_NO" +
                       " AND A.ORDER_SEQ = B.ORDER_SEQ"+
                       " AND A.LIS_RE_USER=C.USER_ID"; 
            	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
            	lis_re_user=result.getValue("USER_NAME",0);
            	
            	//System.out.println("........................result......................"+result);
            	//System.out.println("result===="+StringTool.getString(result.getTimestamp("LIS_RE_DATE",0), "HH:mm:ss"));
            	printData.addData("LIS_RE_DATE", StringTool.getString(result.getTimestamp("LIS_RE_DATE",0), "HH:mm:ss"));
                printData.addData("BED_NO", parm.getValue("BED_NO", i));
                printData.addData("NS_EXEC_DATE", StringTool.getString(parm
                        .getTimestamp("NS_EXEC_DATE", i), "HH:mm:ss"));
                
            }
            printData.addData("LIS_RE_USER",lis_re_user);
            printData.addData("SEND_USER",parm.getValue("SEND_USER",i));
            count++;
        }
        printData.setCount(count);
        if ((Boolean) callFunction("UI|OE|isSelected") == true) {// 门急诊
            printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
            printData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printData.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");
            printData.addData("SYSTEM", "COLUMNS", "BLOOD_DATE");
            printData.addData("SYSTEM", "COLUMNS", "LIS_RE_DATE");
            printData.addData("SYSTEM", "COLUMNS", "DR_NOTE");
            printData.addData("SYSTEM", "COLUMNS", "MR_NO");
            printData.addData("SYSTEM", "COLUMNS", "LIS_RE_USER");
            printData.addData("SYSTEM", "COLUMNS", "SEND_USER");
        } else {// 住院
            printData.addData("SYSTEM", "COLUMNS", "BED_NO");
            printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
            printData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printData.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");
            printData.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
            printData.addData("SYSTEM", "COLUMNS", "LIS_RE_DATE");
            printData.addData("SYSTEM", "COLUMNS", "DR_NOTE");
            printData.addData("SYSTEM", "COLUMNS", "MR_NO");
            printData.addData("SYSTEM", "COLUMNS", "LIS_RE_USER");
            printData.addData("SYSTEM", "COLUMNS", "SEND_USER");
        }
        TParm printParm = new TParm();
        printParm.setData("TITLE", "TEXT", "检验标本送检清单");
        if ((Boolean) callFunction("UI|OE|isSelected") == true) {// 门急诊
            printParm.setData("CLINICAREA_CODE", "TEXT", "诊区：" +((TComboBox)this.getComponent("CLINICAREA_CODE")).getSelectedName() );
        } else {// 住院
            printParm.setData("STATION_CODE", "TEXT", "病区：" + this.getText("STATION_CODE"));
        }
        Timestamp reStartDate = (Timestamp) this.getValue("START_DATE"); // 开始时间
        Timestamp reEndDate = (Timestamp) this.getValue("END_DATE"); // 结束时间
        printParm
                .setData("DATE", "TEXT",
                         "交接起日：" + StringTool.getString(reStartDate, "yyy/MM/dd HH:mm:ss") + " "
                                 + "交接迄日：" + StringTool.getString(reEndDate, "yyy/MM/dd HH:mm:ss"));
        printParm.setData("TABLE", printData.getData());
        
        if ((Boolean) callFunction("UI|OE|isSelected") == true) {// 门急诊
        	String prtSwitch=IReportTool.getInstance().getPrintSwitch("MedOpdLisSend_V45.prtSwitch");
            String previewSwitch=IReportTool.getInstance().getPrintSwitch("MedOpdLisSend_V45.previewSwitch");
        	if(prtSwitch.equals(IReportTool.ON)){
				if (previewSwitch.equals(IReportTool.ON)) {
					this.openPrintWindow(IReportTool.getInstance()
							.getReportPath("MedOpdLisSend_V45.jhw"),
							IReportTool.getInstance().getReportParm("MedOpdLisSend_V45.class", printParm));
				}else{
					this.openPrintWindow(IReportTool.getInstance()
							.getReportPath("MedOpdLisSend_V45.jhw"),
							IReportTool.getInstance().getReportParm("MedOpdLisSend_V45.class", printParm),true);
				}
        	}
        } else {// 住院
        	String prtSwitch=IReportTool.getInstance().getPrintSwitch("MedLisSend_V45.prtSwitch");
        	String previewSwitch=IReportTool.getInstance().getPrintSwitch("MedLisSend_V45.previewSwitch");
        	if(prtSwitch.equals(IReportTool.ON)){
        		if (previewSwitch.equals(IReportTool.ON)) {
	        		this.openPrintWindow(IReportTool.getInstance().getReportPath("MedLisSend_V45.jhw"),
	        				IReportTool.getInstance().getReportParm("MedLisSend_V45.class",printParm));
        		}else{
        			this.openPrintWindow(IReportTool.getInstance()
							.getReportPath("MedLisSend_V45.jhw"),
							IReportTool.getInstance().getReportParm("MedLisSend_V45.class", printParm),true);
        		}
        	}
        }
	}
	/**
	 * 打印方法
	 */
    public void onPrint() {
        if (table.getRowCount() <= 0) {
            this.messageBox("无打印数据");
            return;
        }
        TParm parm = table.getParmValue();
        TParm printData = new TParm();
        int count = 0;
        for (int i = 0; i < parm.getCount("MR_NO"); i++) {
        	String lis_re_user="";
            if (!parm.getBoolean("FLG", i)) continue;
            printData.addData("MR_NO", parm.getValue("MR_NO", i));
            printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
            printData.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
            printData.addData("MED_APPLY_NO", parm.getValue("MED_APPLY_NO", i));
            printData
                    .addData("LIS_RE_DATE",
                             StringTool.getString(parm.getTimestamp("LIS_RE_DATE", i), "HH:mm:ss"));
            printData.addData("DR_NOTE", parm.getValue("DR_NOTE", i));
            if ((Boolean) callFunction("UI|OE|isSelected") == true) {// 门急诊
                printData.addData("BLOOD_DATE", StringTool.getString(parm
                        .getTimestamp("BLOOD_DATE", i), "HH:mm:ss"));
            } else {// 住院
                printData.addData("BED_NO", parm.getValue("BED_NO", i));
                printData.addData("NS_EXEC_DATE", StringTool.getString(parm
                        .getTimestamp("NS_EXEC_DATE", i), "HH:mm:ss"));
            }
            if ((Boolean) callFunction("UI|OE|isSelected") == true) {
            	String sql="SELECT B.USER_NAME FROM MED_APPLY A,SYS_OPERATOR B " +
            			" WHERE A.APPLICATION_NO='"+parm.getValue("MED_APPLY_NO",i)+"' AND A.CAT1_TYPE='LIS' AND A.LIS_RE_USER=B.USER_ID ";
            	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
            	lis_re_user=result.getValue("USER_NAME",0);
            }else{
            	String sql="SELECT A.LIS_RE_DATE,C.USER_NAME FROM ODI_DSPNM A, ODI_ORDER B,SYS_OPERATOR C " +
     		    " WHERE  B.MED_APPLY_NO = '"+parm.getValue("MED_APPLY_NO",i)+"' " +
     		    " AND A.CASE_NO = B.CASE_NO" +
                " AND A.ORDER_NO = B.ORDER_NO" +
                " AND A.ORDER_SEQ = B.ORDER_SEQ"+
                " AND A.LIS_RE_USER=C.USER_ID"; 
            	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
            	lis_re_user=result.getValue("USER_NAME",0);
            }
            printData.addData("LIS_RE_USER",lis_re_user);
            printData.addData("SEND_USER",parm.getValue("SEND_USER",i));
            //System.out.println("-----printData-----"+printData);
            count++;
        }
        printData.setCount(count);
        if ((Boolean) callFunction("UI|OE|isSelected") == true) {// 门急诊
            printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
            printData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printData.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");
            printData.addData("SYSTEM", "COLUMNS", "BLOOD_DATE");
            printData.addData("SYSTEM", "COLUMNS", "LIS_RE_DATE");
            printData.addData("SYSTEM", "COLUMNS", "DR_NOTE");
            printData.addData("SYSTEM", "COLUMNS", "MR_NO");
            printData.addData("SYSTEM", "COLUMNS", "LIS_RE_USER");
            printData.addData("SYSTEM", "COLUMNS", "SEND_USER");
        } else {// 住院
            printData.addData("SYSTEM", "COLUMNS", "BED_NO");
            printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
            printData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printData.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");
            printData.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
            
            //modify by yangjj 20150529 去掉交接时间
            //printData.addData("SYSTEM", "COLUMNS", "LIS_RE_DATE");
            
            printData.addData("SYSTEM", "COLUMNS", "DR_NOTE");
            printData.addData("SYSTEM", "COLUMNS", "MR_NO");
            printData.addData("SYSTEM", "COLUMNS", "LIS_RE_USER");
            
            //modify by yangjj 20150529 去掉运送人员
            //printData.addData("SYSTEM", "COLUMNS", "SEND_USER");
        }
        TParm printParm = new TParm();
        printParm.setData("TITLE", "TEXT", "检验标本送检清单");
        if ((Boolean) callFunction("UI|OE|isSelected") == true) {// 门急诊
            printParm.setData("CLINICAREA_CODE", "TEXT", "诊区：" +((TComboBox)this.getComponent("CLINICAREA_CODE")).getSelectedName() );
        } else {// 住院
            printParm.setData("STATION_CODE", "TEXT", "病区：" + this.getText("STATION_CODE"));
        }
        Timestamp reStartDate = (Timestamp) this.getValue("RE_START_DATE"); // 开始时间
        Timestamp reEndDate = (Timestamp) this.getValue("RE_END_DATE"); // 结束时间
        printParm
                .setData("DATE", "TEXT",
                         "交接起日：" + StringTool.getString(reStartDate, "yyy/MM/dd HH:mm:ss") + " "
                                 + "交接迄日：" + StringTool.getString(reEndDate, "yyy/MM/dd HH:mm:ss"));
        printParm.setData("TABLE", printData.getData());
        
        
        //add by yangjj 20150529 增加打印时间
        printParm
        .setData("printTime", "TEXT",SystemTool.getInstance().getDate().toString().replace("-", "/").substring(0, 19));
        
        if ((Boolean) callFunction("UI|OE|isSelected") == true) {// 门急诊
        	//===========modify by huangjw 20140718 start
        	String prtSwitch=IReportTool.getInstance().getPrintSwitch("MedOpdLisSend_new_V45.prtSwitch");
        	String previewSwitch=IReportTool.getInstance().getPrintSwitch("MedOpdLisSend_new_V45.previewSwitch");
        	if(prtSwitch.equals(IReportTool.ON)){
	        	if(previewSwitch.equals(IReportTool.ON)){
		            this.openPrintWindow(IReportTool.getInstance().getReportPath("MedOpdLisSend_new_V45.jhw"),
		            		IReportTool.getInstance().getReportParm("MedOpdLisSend_new_V45.class",printParm));
	        	}else{
	        		this.openPrintWindow(IReportTool.getInstance().getReportPath("MedOpdLisSend_new_V45.jhw"),
		            		IReportTool.getInstance().getReportParm("MedOpdLisSend_new_V45.class",printParm),true);
	        	}
        	}
        } else {// 住院
        	String prtSwitch=IReportTool.getInstance().getPrintSwitch("MedLisSend_new_V45.prtSwitch");
        	String previewSwitch=IReportTool.getInstance().getPrintSwitch("MedLisSend_new_V45.previewSwitch");
        	if(prtSwitch.equals(IReportTool.ON)){
	        	if(previewSwitch.equals(IReportTool.ON)){
	        		this.openPrintWindow(IReportTool.getInstance().getReportPath("MedLisSend_new_V45.jhw"),
	        				IReportTool.getInstance().getReportParm("MedLisSend_new_V45.class",printParm));
	        	}else{
	        		this.openPrintWindow(IReportTool.getInstance().getReportPath("MedLisSend_new_V45.jhw"),
	        				IReportTool.getInstance().getReportParm("MedLisSend_new_V45.class",printParm),true);
	        	}
        	}
        	//===========modify by huangjw 20140718 end
        }
    }

	/**
	 * 清空方法
	 */
	public void onClear() {
        this.clearValue("CLINICAREA_CODE;STATION_CODE;LIS_RE_USER;MED_APPLY_NO;SEND_USER");
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String today = StringTool.getString(sysDate, "yyyyMMdd");
        this.setValue("START_DATE", StringTool.getTimestamp(today + "000000", "yyyyMMddHHmmss"));// 默认设置起始日期
        this.setValue("END_DATE", StringTool.getTimestamp(today + "235959", "yyyyMMddHHmmss")); // 默认设置终止日期
        this.setValue("CLINICAREA_CODE", Operator.getStation());
        this.setValue("STATION_CODE", Operator.getStation());
        callFunction("UI|RE_NO|setSelected", true);
        onChooseREState();
        callFunction("UI|MED_APPLY_NO|grabFocus");
        table.removeRowAll();
    }

	/**
     * TABLE复选框勾选事件
     * @param obj
     */
    public void onCheckBoxValue(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        int col = table.getSelectedColumn();
        String columnName = table.getDataStoreColumnName(col);
        int row = table.getSelectedRow();
        TParm parm = table.getParmValue();
        TParm tableParm = parm.getRow(row);
        String applicationNo = tableParm.getValue("MED_APPLY_NO");
        if ("FLG".equals(columnName)) {
            int rowCount = parm.getCount("ORDER_DESC");
            for (int i = 0; i < rowCount; i++) {
                if (i == row)
                    continue;
                if (applicationNo.equals(parm.getValue("MED_APPLY_NO", i))) {
                    parm.setData("FLG", i, parm.getBoolean("FLG", i) ? "N"
                            : "Y");
                }
            }
            table.setParmValue(parm);
        }
    }
    
	/**
	 * 全选
	 */
	public void onAll() {
		boolean Flag = (Boolean) this.callFunction("UI|ALL|isSelected");
		TParm parm = table.getParmValue();
	
		for (int i = 0; i < parm.getCount(); i++) {
			table.setItem(i, "FLG", Flag);
		}
	}
	
	/**
	 * 获得TTextFormat
	 */
	public TTextFormat getTextFormat(String tagName){
		return (TTextFormat)getTextFormat(tagName);
	}
	
	/**
	 * 获得getRadioButton
	 */
	public TRadioButton getRadioButton(String tagName){
		return (TRadioButton)getComponent(tagName);
	}

    // ====================排序功能begin======================
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = table.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.根据点击的列,对vector排序
				// 表格排序的列名;
				String tblColumnName = table.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);
			}
		});
	}

    /**
     * 列名转列索引值
     * 
     * @param columnName
     * @param tblColumnName
     * @return
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * 得到 Vector 值
     * 
     * @param group
     *            String 组名
     * @param names
     *            String "ID;NAME"
     * @param size
     *            int 最大行数
     * @return Vector
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size) count = size;
        for (int i = 0; i < count; i++) {
            Vector row = new Vector();
            for (int j = 0; j < nameArray.length; j++) {
                row.add(parm.getData(group, nameArray[j], i));
            }
            data.add(row);
        }
        return data;
    }

    /**
     * vectory转成param
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        // 行数据;
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        table.setParmValue(parmTable);
    }
    // ====================排序功能end======================
}
