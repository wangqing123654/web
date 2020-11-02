package com.javahis.ui.onw;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TToolButton;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;

/**
 * <p>Title: 报告进度查询</p>
 *
 * <p>Description: 报告进度查询</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-02-03
 * @version 1.0
 */
public class ONWPlanReportControl
    extends TControl {
    
    private String serverPath = "";//wanglong add 20140714
    private String tempPath = "C:\\JavaHisFile\\temp\\pdf";
    Pat pat;//病患对象
    String mrno;//病案号
	/**
	 * 就诊号
	 */
    private String caseNo;
    Timestamp date = SystemTool.getInstance().getDate();
    public void onInit() {
        super.onInit();
        this.setValue("START_DATE", date.toString().substring(0,10).replace('-', '/')+" 00:00:00");
        this.setValue("END_DATE", date.toString().substring(0,10).replace('-', '/')+" 23:59:59");
        this.setValue("CLINICAREA_CODE", Operator.getStation());
        //Table监听
        callFunction("UI|Table|addEventListener",
                TTableEvent.CHECK_BOX_CLICKED, this, "onClickBox");
        /*addEventListener("Table->" + TTableEvent.CHANGE_VALUE,
                    "reportPrintFlg");*/
        pageInit();
        File f = new File(tempPath);//wanglong add 20140714
        if (!f.exists()) {
            f.mkdirs();
        }
    }
    /**
     * table 中 checkbox 事件
     * @param object Object
     */
    public void onClickBox(Object object) {
        TTable obj = (TTable) object;
        obj.acceptText();
        //reportPrintFlg();
    }
    /**
     * 页面初始化
     */
    private void pageInit(){
        Object obj = this.getParameter();
        if(obj==null){
            this.messageBox_("参数错误");
            return;
        }
        TParm parm = new TParm();
        if(obj instanceof TParm){
            parm = (TParm)obj;
        }
        //如果用户是护士就不让打印
        /*if("N".equals(parm.getValue("ISPRINT"))){
        	callFunction("UI|print|setEnabled", false);
        }*/
        //caseNo=parm.getValue("CASE_NO");
        mrno=parm.getValue("MR_NO");
        this.setValue("MR_NO",parm.getValue("MR_NO"));
        this.setValue("PAT_NAME",parm.getValue("PAT_NAME"));
        this.setValue("SEX",parm.getValue("SEX_CODE"));
        if(parm.getValue("NUR_FLG").equals("Y")){
	        this.setValue("DEPT_CODE",parm.getValue("DEPT_CODE"));
	        this.setValue("CLINICROOM_NO",parm.getValue("CLINICROOM_NO"));
        }
        //this.setValue("DR_CODE",parm.getValue("DR_CODE"));
        
        TParm data = this.queryMedApply(mrno);
        TTable table = (TTable)this.getComponent("Table");
        for(int i = 0;i<data.getCount();i++){
        	String selSysFee = "SELECT TIME_LIMIT,TRANS_OUT_FLG FROM SYS_FEE " +
        			"WHERE ORDER_CODE = '"+data.getValue("ORDER_CODE",i)+"'";
            TParm sysParm = new TParm(TJDODBTool.getInstance().select(selSysFee));
            if(sysParm.getCount()>0){
            	data.setData("TIME_LIMIT",i, sysParm.getData("TIME_LIMIT",0));
            	data.setData("TRANS_OUT_FLG",i, sysParm.getData("TRANS_OUT_FLG",0));
            }else{
            	data.setData("TIME_LIMIT",i, "");
            	data.setData("TRANS_OUT_FLG",i, "");
            }
        }
        table.setParmValue(data);
    }
    /**
     * 查询
     */
    public void onQuery(){
    	TTable table = (TTable)this.getComponent("Table");
    	mrno=this.getValueString("MR_NO");
    	String allMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
    	this.getPatinfo(allMrNo);
    	this.setValue("MR_NO", allMrNo);
    	TParm data = this.queryMedApply(allMrNo);
    	if(data.getErrCode()<0){
    		this.messageBox(data.getErrText());
    		return;
    	}
    	if(data.getCount()<0){
    		this.messageBox("没有数据");
    		table.removeRowAll();
    		return;
    	}
        
        for(int i = 0;i<data.getCount();i++){
        	String selSysFee = "SELECT TIME_LIMIT,TRANS_OUT_FLG FROM SYS_FEE " +
        			"WHERE ORDER_CODE = '"+data.getValue("ORDER_CODE",i)+"'";
            TParm sysParm = new TParm(TJDODBTool.getInstance().select(selSysFee));
            if(sysParm.getCount()>0){
            	data.setData("TIME_LIMIT",i, sysParm.getData("TIME_LIMIT",0));
            	data.setData("TRANS_OUT_FLG",i, sysParm.getData("TRANS_OUT_FLG",0));
            }else{
            	data.setData("TIME_LIMIT",i, "");
            	data.setData("TRANS_OUT_FLG",i, "");
            }
        }
        table.setParmValue(data);
        //table.repaint();
    }
    
    /**
     * table双击事件
     */
    public void onTableDoubleCliecked() {//wanglong add 20140714
        TTable table = (TTable) this.getComponent("Table");
        TParm parmValue = table.getParmValue();
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择一行");
            return;
        }
        caseNo=parmValue.getValue("CASE_NO",row);
        if (!isExist(parmValue.getRow(row),1)) {
            this.messageBox("请检查报告状态，尚未生成报告");
            return;
        }
        // 更新阅读标记
        String sql = "UPDATE MED_APPLY SET ISREAD='Y' WHERE CAT1_TYPE='#' AND APPLICATION_NO='#'";
        sql = sql.replaceFirst("#", parmValue.getValue("CAT1_TYPE", row));
        sql = sql.replaceFirst("#", parmValue.getValue("APPLICATION_NO", row));
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("更新阅读标记失败 " + result.getErrText());
            return;
        }
    }

    /**
     * 检查pdf文件是否存在
     * @param parm
     * @return
     */
    private boolean isExist(TParm parm,int i) {//wanglong add 20140714
        String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO ='" + caseNo + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        String pdfPath = result.getValue("FILE_PATH", 0).replaceFirst("JHW", "PDF");
        if (result.getValue("FILE_PATH", 0) == null || result.getValue("FILE_PATH", 0).equals("")) {
            pdfPath =
                    "PDF\\" + caseNo.substring(0, 2) + "\\" + caseNo.substring(2, 4) + "\\"
                            + result.getValue("MR_NO", 0);
        }
        serverPath =
                TConfig.getSystemValue("FileServer.Main.Root") + "\\"
                        + TConfig.getSystemValue("EmrData") + "\\" + pdfPath;
        
        String pdfFile[] = TIOM_FileServer.listFile(TIOM_FileServer.getSocket(), serverPath);
        if (pdfFile == null || pdfFile.length < 1) {
            return false;
        }
        
        List pdfList = Arrays.asList(pdfFile);
        String type = parm.getValue("CAT1_TYPE").equalsIgnoreCase("LIS") ? "检验报告" : "检查报告";
        String pdfFileName = caseNo + "_" + type + "_" + parm.getValue("APPLICATION_NO") + ".pdf";
        if (pdfList.contains(pdfFileName)) {
            byte data[] =
                    TIOM_FileServer.readFile(TIOM_FileServer.getSocket(), serverPath + "\\"
                            + pdfFileName);
            if (data == null) {
                this.messageBox_("服务器上没有找到文件 " + serverPath + "\\" + pdfFileName);
            }
            try {
                FileTool.setByte(tempPath + "\\" + pdfFileName, data);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            Runtime runtime = Runtime.getRuntime();
            try {
            	if(i==1){
	                runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath + "\\"
	                        + pdfFileName); // 打开文件
            	}else{
            		runtime.exec("cmd.exe /C start acrord32 /P /h " + tempPath + "\\"
	                        + pdfFileName); 
            	}
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            return true;
        }
        return false;
    }
    
    /**
     * 全选事件add by huangjw 20140918
     */
    public void selectAll(){
    	TTable table=(TTable)getComponent("Table");
    	table.acceptText();
    	TParm tableParm=table.getParmValue();
    	if("Y".equals(this.getValueString("SELECT_ALL"))){
    		for(int i=0;i<tableParm.getCount();i++){
    			tableParm.setData("XUAN",i,"Y");
    		}
    	}else{
    		for(int i=0;i<tableParm.getCount();i++){
    			tableParm.setData("XUAN",i,"N");
    		}
    	}
    	
    	table.setParmValue(tableParm);
    }
    /**
     * 查询报告进度
     * @param parm TParm
     * @return TParm
     */
    public TParm queryMedApply(String mrno){
    	System.out.println("=================="+this.getValueString("START_DATE"));
    	String start_date=this.getValueString("START_DATE").toString().substring(0,19).replace('-', '/');
    	String end_date=this.getValueString("END_DATE").toString().substring(0,19).replace('-', '/');
    	StringBuffer sql=new StringBuffer();
    	sql.append("SELECT 'N' AS XUAN,"
    	       +" REPORT_PRINT_FLG,"
    	       +" ORDER_CODE,"
    	       +" ORDER_DESC,"
    	       +" APPLICATION_NO,"
    	       +" OPTITEM_CHN_DESC,"
    	       +" ORDER_DATE,"
    	       +" STATUS,"
    	       +" ORDER_DR_CODE,"
    	       +" RESERVED_DATE,"
    	       +" QS_DATE,"
    	       +" REGISTER_DATE,"
    	       +" INSPECT_DATE,"
    	       +" EXAMINE_DATE,"
    	       +" EXEC_DR_CODE,"
    	       +" REPORT_DR,"
    	       +" EXAMINE_DR,"
    	       +" CASE_NO,"
    	       +" CAT1_TYPE"
    	       +" FROM MED_APPLY"
    	       +" WHERE MR_NO='"+mrno+"' AND ORDER_DATE BETWEEN TO_DATE('"+start_date+"','YYYY/MM/DD HH24:MI:SS') " 
    	       +" AND TO_DATE('" + end_date + "','YYYY/MM/DD HH24:MI:SS')");
    	if(this.getRadioButton("LIS").isSelected()){
    		sql.append(" AND CAT1_TYPE='LIS'");
    	}
    	if(this.getRadioButton("RIS").isSelected()){
    		sql.append(" AND CAT1_TYPE='RIS'");
    	}
    	if(this.getRadioButton("PRINT1").isSelected()){
    		sql.append(" AND REPORT_PRINT_FLG='N'");
    	}
    	if(this.getRadioButton("PRINT2").isSelected()){
    		sql.append(" AND REPORT_PRINT_FLG='Y'");
    	}
    	if(this.getRadioButton("FINISH").isSelected()){
    		if(this.getRadioButton("RIS").isSelected()){
    			sql.append(" AND STATUS='7'");
    		}
    		if(this.getRadioButton("LIS").isSelected()){
    			sql.append(" AND STATUS='6'");
    		}
    	}
    	if(this.getRadioButton("UNFINISH").isSelected()){
    		if(this.getRadioButton("RIS").isSelected()){
    			sql.append(" AND STATUS<>'7'");
    		}
    		if(this.getRadioButton("LIS").isSelected()){
    			sql.append(" AND STATUS<>'6'");
    		}
    	}
    	if(!"".equals(this.getValue("CLINICAREA_CODE"))){
    		sql.append(" AND CLINICAREA_CODE='"+this.getValue("CLINICAREA_CODE")+"'");
    	}
    	if(!"".equals(this.getValueString("DEPT_CODE"))){
    		sql.append(" AND DEPT_CODE='"+this.getValue("DEPT_CODE")+"'");
    	}
    	if(!"".equals(this.getValueString("CLINICROOM_NO"))){
    		sql.append(" AND CLINICROOM_NO='"+this.getValue("CLINICROOM_NO")+"'");
    	}
    	if(!"".equals(this.getValueString("DR_CODE"))){
    		sql.append(" AND ORDER_DR_CODE='"+this.getValue("DR_CODE")+"'");
    	}
        TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
        return result;
    }
    /**
     * 清空
     */
    public void onClear(){
    	this.clearValue("MR_NO;PAT_NAME;SEX");
    	this.callFunction("UI|MR_NO|setEnabled", true);
    	TTable table=(TTable)getComponent("Table");
    	table.removeRowAll();
    }
    /**
     * 病案号查询
     */
    public void queryByMrno(){
    	onQuery();
    	this.callFunction("UI|MR_NO|setEnabled", false);
    }
    /**
     * 获取病患信息
     * @param mrno
     */
    public void getPatinfo(String mrno){
    	String sql="SELECT PAT_NAME,SEX_CODE FROM SYS_PATINFO WHERE MR_NO='"+mrno+"'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount()<0){
    		return;
    	}
    	this.setValue("PAT_NAME", result.getValue("PAT_NAME",0));
    	this.setValue("SEX", result.getValue("SEX_CODE",0));
    }
    /**
     * 打印PDF文件
     */
    public void onPrint(){
    	TTable table=(TTable)getComponent("Table");
    	table.acceptText();
    	TParm tableParm=table.getParmValue();
    	if(tableParm.getCount()<0){
    		this.messageBox("没有数据");
    		return;
    	}
    	int count=0;
    	for(int i=0;i<tableParm.getCount();i++){
    		if("Y".equals(tableParm.getRow(i).getValue("XUAN"))){
    			count++;
    			caseNo=tableParm.getRow(i).getValue("CASE_NO");
    			if(isExist(tableParm.getRow(i),2)){
	    			//更新打印标记
	    			String sql="UPDATE MED_APPLY SET REPORT_PRINT_FLG='Y' WHERE APPLICATION_NO='"+tableParm.getRow(i).getValue("APPLICATION_NO")+"'";
	    			TParm result=new TParm(TJDODBTool.getInstance().update(sql));
	    			if(result.getErrCode()<0){
	    				this.messageBox("更新打印标记失败 " + result.getErrText());
	    				return;
	    			}
	    			
    			}else{
    				this.messageBox("请检查报告状态，尚未生成报告");
    	            continue;
    			}
    		}
    	}
    	if(count==0){
    		this.messageBox("请勾选");
    		return;
    	}
    	onQuery();
    }
    /**
     * 是否取消打印注记
     */
    public void reportPrintFlg(){
    	TTable table=(TTable)getComponent("Table");
    	
    	//table.acceptText();
    	TParm tableParm=table.getParmValue();
    	int row=table.getSelectedRow();
    	if("Y".equals(tableParm.getValue("REPORT_PRINT_FLG", row))){
    		if(JOptionPane.showConfirmDialog(null, "是否取消打印注记？", "信息",
    				JOptionPane.YES_NO_OPTION) == 0){
    			String sql="UPDATE MED_APPLY SET REPORT_PRINT_FLG='N' WHERE APPLICATION_NO='"+tableParm.getValue("APPLICATION_NO",row)+"'";
    			TParm result=new TParm(TJDODBTool.getInstance().update(sql));
    			if(result.getErrCode()<0){
    				this.messageBox("更新打印标记失败 " + result.getErrText());
    				return;
    			}
    		}
    		queryByMrno();
    	}
    }
    /**
	  * 获得TRadioButton
	  * @param tagName
	  * @return
	  */
	public TRadioButton getRadioButton(String tagName){
	    return (TRadioButton)getComponent(tagName);
	}
	
	
//	public TParm getRowData() {
//		
//	}
	
	/**20180906  yanglu
	 * 回传给危机值
	 */
	public void callBackToMEDSmsMag() {
		TTable table=(TTable)getComponent("Table");
		TParm tableParm = table.getParmValue();
//		System.out.println("+_+++++++++++++++++++++++"+table.getSelectedRow());
		TParm rowParm = tableParm.getRow(table.getSelectedRow());// danhang
//		TParm parm2 = new TParm();
//		for(int i=0; i<tableParm.getCount(); i++) {
//			if(true) {
//				parm2.addRowData(tableParm, i);		
//			}
//		}	
		
		
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>"+rowParm);
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>"+tableParm);

		this.setReturnValue(rowParm);
		this.closeWindow();
	}
	
}
