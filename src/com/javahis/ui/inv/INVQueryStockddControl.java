package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

public class INVQueryStockddControl extends TControl{
	public INVQueryStockddControl(){}
	private static TTable tableM;
	private static TTable tableD;
	/**
	 * 表格当前选中的行号
	 */
	private int selRow = -1;
	/**
	 * 初始化
	 */
	public void init(){
		super.init();
		initPage();

	}
	
	/**
     * 初始画面数据
     */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
        			date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		//获得TABLE对象
        tableM = (TTable) getComponent("TABLE1");
		tableD = (TTable) getComponent("TABLE2");
		TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "OTH");
		// 设置弹出菜单
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parm);
		// 定义接受返回值方法
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        
     // 给上table注册单击事件监听
		this.callFunction("UI|TABLE1|addEventListener", "TABLE1->"
				+ TTableEvent.CLICKED, this, "ontableMClicked");
	}
	  /**
     * 得到TextField对象
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
    
    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String order_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("INV_CODE").setValue(order_code);
        String order_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("INV_DESC").setValue(order_desc);
    }
	
	  /**
     * 查询
     */
	public void onQuery(){
		String sql = "SELECT   A.INV_CODE, B.INV_CHN_DESC INV_DESC, B.DESCRIPTION," +
				" SUM (A.STOCK_QTY) QTY, C.MAN_CHN_DESC MAN_DESC," +
				" D.SUP_CHN_DESC SUP_DESC" +
				" FROM INV_STOCKDD A, INV_BASE B, SYS_MANUFACTURER C, SYS_SUPPLIER D" +
				" WHERE A.INV_CODE = B.INV_CODE" +
				" AND B.MAN_CODE = C.MAN_CODE" +
				" AND B.SUP_CODE = D.SUP_CODE" +
				" AND A.WAST_FLG = 'Y'";
				
        // 查询时间
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
        	String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "START_DATE")), "yyyyMMdd")+" 00:00:00";
            String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "END_DATE")), "yyyyMMdd")+" 23:59:59";
	         
//	         System.out.println(startTime);
//	         System.out.println(endTime);
	         
	         sql += " AND A.OUT_DATE BETWEEN TO_DATE('"+startTime+ "','yyyymmdd hh24:mi:ss') " + "AND TO_DATE('" + endTime
				+ "','yyyymmdd hh24:mi:ss')";
        }else{
        	this.messageBox("日期 不能为空");
        	return;
        }
        //物资
        if (!"".equals(this.getValueString("INV_CODE"))) {
        	String invCode=TypeTool.getString(getValue("INV_CODE"));
            sql +=" AND A.INV_CODE ="+ invCode; 
        }else {
        	 sql +=" AND A.INV_CODE LIKE '08%'";
		}
        sql += " GROUP BY A.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION,C.MAN_CHN_DESC,D.SUP_CHN_DESC";
//        System.out.println("11111==="+sql);
        TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if(selParm.getCount()<0){
        	this.messageBox("没有要查询的数据");
        	return;
        }
      //加载table上的数据
        tableM.setParmValue(selParm);
	}
	
	/**
	 * 单击上面的table事件
	 */
	public void ontableMClicked(int row){
		// 当前选中的行号
		selRow = row;
		TParm parm = tableM.getParmValue();
		TParm tableDate = parm.getRow(selRow);
		String invCode=tableDate.getValue("INV_CODE");
		TParm parmD=gettableD(invCode);
		tableD.setParmValue(parmD);
	}
	
	/**
	 * 得到细表的值
	 * @param invCode
	 * @return
	 */
	private TParm gettableD(String invCode){
		String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd")+" 00:00:00";
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd")+" 23:59:59";
		String sql="SELECT A.RFID, B.INV_CHN_DESC INV_DESC, A.OUT_DATE,  A.WAST_ORG, A.MR_NO," +
				" A.CASE_NO, A.RX_SEQ, A.SEQ_NO, A.ADM_TYPE" +
				" FROM INV_STOCKDD A, INV_BASE B" +
				" WHERE  A.INV_CODE = B.INV_CODE AND A.WAST_FLG = 'Y' AND  A.INV_CODE='"+invCode+"'" +
				" AND A.OUT_DATE BETWEEN TO_DATE('"+startTime+ "','yyyymmdd hh24:mi:ss') " + "AND TO_DATE('" + endTime
				+ "','yyyymmdd hh24:mi:ss')";
//		System.out.println(sql);
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		return selParm;
		
	}
	
	/**
     * 打印方法
     */
    public void onPrint() {
    	tableM = this.getTable("TABLE1");
    	if (tableM.getRowCount() <= 0) {
            this.messageBox("没有打印数据");
            return;
        }
    	//表头  
    	Timestamp datetime = SystemTool.getInstance().getDate();
    	TParm data = new TParm();
    	String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd")+" 00:00:00";
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd")+" 23:59:59";
    	data.setData("TITLE", "TEXT", "物资消耗统计表");
    	data.setData("QUERY", "TEXT", "查询日期："+startTime+"～"+ endTime);
    	data.setData("DATE", "TEXT", "制表日期: " +
                datetime.toString().substring(0, 10).replace('-', '/'));
    	data.setData("USER", "TEXT", "制表人: " + Operator.getName());
    	//数据 
    	TParm parm = new TParm();
    	TParm tableParm = tableM.getParmValue();
    	for(int i=0; i<tableParm.getCount(); i++){
    		parm.addData("INV_CODE", tableParm.getValue("INV_CODE",i));
    		parm.addData("INV_DESC", tableParm.getValue("INV_DESC",i));
    		parm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION",i));
    		parm.addData("QTY", tableParm.getValue("QTY",i));
    		parm.addData("MAN_DESC", tableParm.getValue("MAN_DESC",i));
    		parm.addData("SUP_DESC", tableParm.getValue("SUP_DESC",i));
    	}
    	parm.setCount(parm.getCount("INV_CODE"));
    	parm.addData("SYSTEM", "COLUMNS", "INV_CODE");
    	parm.addData("SYSTEM", "COLUMNS", "INV_DESC");
    	parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
    	parm.addData("SYSTEM", "COLUMNS", "QTY");
    	parm.addData("SYSTEM", "COLUMNS", "MAN_DESC");
    	parm.addData("SYSTEM", "COLUMNS", "SUP_DESC");
  
    	data.setData("TABLE",parm.getData());

    	this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVQueryStockddPrint.jhw", data);
    }
	
	/**
     * 清空方法
     */
	
	public void onClear() {
		String clearString ="INV_CODE;END_DATE;START_DATE;INV_DESC"; 
		this.clearValue(clearString);

		// 初始化查询区间
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
        			date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        tableM.removeRowAll();
        tableD.removeRowAll();
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
    
	

}
