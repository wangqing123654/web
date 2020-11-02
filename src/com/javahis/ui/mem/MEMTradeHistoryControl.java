package com.javahis.ui.mem;


import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
*
* <p>Title: 会员卡购卡退卡历史交易查询</p>
*
* <p>Description: 会员卡购卡退卡历史交易查询</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author duzhw 20140418
* @version 4.5
*/
public class MEMTradeHistoryControl extends TControl {
	
	private TTable table;
	Pat pat;
	
	/**
     * 初始化界面
     */
    public void onInit() {
        super.onInit();
        initData();
        initComponent();
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm acceptData = (TParm) obj;
            String mrNo = acceptData.getData("MR_NO").toString();
            this.setValue("MR_NO", mrNo);
            this.onQuery();
        }
    }
    
    /**
     * 初始化数据
     */
    public void initData() {
    	/*
    	Timestamp now = TJDODBTool.getInstance().getDBTime();
    	//开始日期
    	this.setValue("START_DATE", now.toString().substring(0, 10).replace('-', '/'));
    	//结束日期
    	this.setValue("END_DATE", StringTool.rollDate(now, +1).toString().substring(0, 10).
                replace('-', '/'));
                */
    	
    }
    
    /**
	 * 初始化控件
	 */
    private void initComponent() {
    	table = getTable("TABLE");
    }
    
    /**
     * 查询
     */
    public void onQuery() {
    	String mrNo = this.getValueString("MR_NO");
    	if(mrNo==null || mrNo.length()<=0){
    		this.messageBox("病案号不能为空！");
    		this.grabFocus("MR_NO");
    		return;
    	}else{
    		onMrno(); 
    	}
    	
    }
    
    /**
     * 查询历史信息
     */
    public void onQueryHisInfo(){
    	String mrNo = this.getValueString("MR_NO");
    	String sql = getSql(mrNo);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount()<=0){
    		this.messageBox("查无记录！");
    		return;
    	}
    	table.setParmValue(result);
    }
    
    /**
     * 病案号回车
     */
    public void onMrno() {
    	pat = new Pat();
        String mrno = getValue("MR_NO").toString().trim();
        if (!this.queryPat(mrno))
            return;
        pat = pat.onQueryByMrNo(mrno);
        //System.out.println("pat="+pat);
        if (pat == null || "".equals(getValueString("MR_NO"))) {
            this.messageBox_("查无病患! ");
            this.onClear(); // 清空
            return;
        }else{
        	this.setValue("MR_NO", pat.getMrNo());//病案号
        	//this.setValue("PAT_NAME", pat.getName());//姓名
        	//this.setValue("SEX_CODE", pat.getSexCode());//性别
        	//病案号置无效
        	callFunction("UI|MR_NO|setEnabled", false); 
        	//查询操作
        	onQueryHisInfo();
        }
        
    }
    
    /**
     * 查询病患信息
     * @param mrNo String
     * @return boolean
     */
    public boolean queryPat(String mrNo) {
        //this.setMenu(false); //MENU 显示控制
        pat = new Pat();
        pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            //this.setMenu(false); //MENU 显示控制
            this.messageBox("E0081");
            return false;
        }
        String allMrNo = PatTool.getInstance().checkMrno(mrNo);
        if (mrNo != null && !allMrNo.equals(pat.getMrNo())) {
            //============xueyf modify 20120307 start
            messageBox("病案号" + allMrNo + " 已合并至" + pat.getMrNo());
            //============xueyf modify 20120307 stop
        }

        return true;
    }
    
    /**
     * 清除
     */
    public void onClear() {
    	//病案号置有效
    	callFunction("UI|MR_NO|setEnabled", true); 
    	this.clearValue("MR_NO;MEM_CODE");
    	/*
    	Timestamp now = TJDODBTool.getInstance().getDBTime();
    	//开始日期
    	this.setValue("START_DATE", now.toString().substring(0, 10).replace('-', '/'));
    	//结束日期
    	this.setValue("END_DATE", StringTool.rollDate(now, +1).toString().substring(0, 10).
                replace('-', '/'));
    	*/
    	table.removeRowAll();
    }
    
    /**
     * 获取sql
     */
    public String getSql(String mrNo){
    	String startDate = this.getValueString("START_DATE");
    	String endDate = this.getValueString("END_DATE");
    	String memCode = this.getValueString("MEM_CODE");
    	
    	String sql = "SELECT ROWNUM AS ID,CASE STATUS WHEN '0' THEN '预办卡' WHEN '1' THEN '已办卡' " +
    			" WHEN '2' THEN '退卡' END STATUS,A.TRADE_NO,A.MR_NO,A.MEM_CODE,A.MEM_DESC,A.MEM_CARD_NO,A.MEM_FEE," +
    			" A.START_DATE,A.END_DATE,A.DESCRIPTION,A.OPT_DATE,A.OPT_USER,A.OPT_TERM,A.REASON," +
    			" A.LAST_DEPRECIATION_END_DATE,A.INTRODUCER1,A.INTRODUCER2,A.INTRODUCER3,A.GATHER_TYPE,B.PAT_NAME,B.IDNO " +
    			" FROM MEM_TRADE A, SYS_PATINFO B WHERE A.MR_NO=B.MR_NO AND A.MR_NO = '"+mrNo+"' ";
    	if(startDate.trim().length()>0 || endDate.trim().length()>0){
    		sql += " AND A.START_DATE BETWEEN TO_DATE('"+startDate.substring(0, 10).replaceAll("-", "/")+ "','yyyy/mm/dd') " + 
    			" AND TO_DATE('" + endDate.substring(0, 10).replaceAll("-", "/") + "','yyyy/mm/dd')"; 
    	}
    	if(memCode.length()>0){
    		sql += " AND A.MEM_CODE = '"+memCode+"' ";
    	}
    	//System.out.println("sql="+sql);
    	return sql;
    }
    
    /**
	 * 得到页面中Table对象
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tag) {
		return (TTable) callFunction("UI|" + tag + "|getThis");
	}
 
}
