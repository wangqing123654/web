package com.javahis.ui.mem;

import jdo.sys.Operator;
import jdo.sys.SYSHzpyTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.TypeTool;

/**
*
* <p>Title: 合同企业设置</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author duzhw 20140210
* @version 4.5
*/
public class MEMContractEnterpriseControl extends TControl {
	
	private TTable table;
	
	public MEMContractEnterpriseControl() {
		super();
	}
	
	
	 /**
     * 初始化
     */
    public void onInit() {//初始化程序
        super.onInit();
        table = getTable("TABLE");
        initData();
        
    }
    
    public void initData(){
    	String sql = "SELECT CONTRACTOR_CODE,CONTRACTOR_DESC,UNIT_ENG_DESC,PY1,PY2,SEQ,CONTRACTOR_TYPE," +
    			" START_DATE,END_DATE,DESCRIPTION,OVERDRAFT,OPT_DATE,OPT_USER,OPT_TERM FROM MEM_CONTRACTOR ORDER BY SEQ ";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	//System.out.println("result==="+result);
    	table.setParmValue(result);
    }
    
    /**
     * 查询
     */
    public void onQuery() {
    	 String cCode = this.getValueString("CONTRACTOR_CODE");
    	 String cDesc = this.getValueString("CONTRACTOR_DESC");
    	 String engDesc = this.getValueString("UNIT_ENG_DESC");
    	 
    	 String sql = "SELECT CONTRACTOR_CODE,CONTRACTOR_DESC,UNIT_ENG_DESC,PY1,PY2,SEQ,CONTRACTOR_TYPE," +
    	 		" START_DATE,END_DATE,DESCRIPTION,OVERDRAFT,OPT_DATE,OPT_USER,OPT_TERM FROM MEM_CONTRACTOR " +
    	 		" WHERE 1=1 ";
    	 if(cCode.length()>0){
    		 sql += " AND CONTRACTOR_CODE = '"+cCode+"' ";
    	 }
    	 if(cDesc.length()>0){
    		 sql += " AND CONTRACTOR_DESC LIKE '%"+cDesc+"%' ";
    	 }
    	 if(engDesc.length()>0){
    		 sql += " AND UNIT_ENG_DESC LIKE '%"+engDesc+"%' ";
    	 }
    	 sql += " ORDER BY SEQ ";
    	 
    	 TParm result = new TParm(TJDODBTool.getInstance().select(sql));
     	 //System.out.println("result==="+result);
     	 table.setParmValue(result);
    	 
    	
    }
    
    /**
     * 保存
     */
    public void onSave() {
    	//编号为灰则修改；编号为有效则新增
    	String oper = "";
    	TTextField contractorCode = (TTextField)this.getComponent("CONTRACTOR_CODE");
    	boolean flag = contractorCode.isEnabled();
    	if(flag){
    		oper = "ADD";
    	}else{
    		oper = "UPDATE";
    	}
    	//校验数据
    	if(!checkData()){
    		return;
    	}
    	
    	//获取页面数据
    	TParm dataParm =  getData();
    	TParm result = new TParm();
    	if("ADD".equals(oper)){
    		//判断录入编号是否已存在
    		if(!checkCode()){
    			return;
    		}
//    		//获取当前序号seq
//    		int seq = getMaxSeq("SEQ","MEM_CONTRACTOR","","","","");
//    		dataParm.setData("SEQ", seq);
    		//新增操作
			String sql = getInsertSql(dataParm);
			result = new TParm(TJDODBTool.getInstance().update(sql));
    		
    	}else if("UPDATE".equals(oper)){
    		String sql = getUpateSql(dataParm);
    		result = new TParm(TJDODBTool.getInstance().update(sql));
    	}
    	
    	if (result.getErrCode() < 0) {
			this.messageBox("更新失败！" + result.getErrName()
							+ result.getErrText());
			return;
		}else{
			this.messageBox("保存成功！");
			onClear();
			onInit();//初始化
//			if("ADD".equals(oper)){
//				//单位编号置灰
//		    	callFunction("UI|CONTRACTOR_CODE|setEnabled", false);
//			}
		}
    }
    
    /**
     * 删除方法
     */
    public void onDelete() {
    	int selectedIndx=table.getSelectedRow();
    	if(selectedIndx<0){
    		this.messageBox("没有选中要删除的数据！");
    		return;
    	}
    	TParm parm=table.getParmValue();
    	String cCode = parm.getValue("CONTRACTOR_CODE",selectedIndx);
    	String sql = "DELETE FROM MEM_CONTRACTOR WHERE CONTRACTOR_CODE = '"+cCode+"'";
    	TParm result = new TParm();
    	result = new TParm(TJDODBTool.getInstance().update(sql));
    	if (result.getErrCode() < 0) {
			this.messageBox("删除失败！" + result.getErrName()
							+ result.getErrText());
			return;
		}else{
			this.messageBox("删除成功！");
			onClear();//清除
			onInit();//初始化
		}
    	
    }
    
    /**
     * 清空方法
     */
    public void onClear() {
    	callFunction("UI|CONTRACTOR_CODE|setEnabled", true);//单位编号置有效
    	this.clearValue("CONTRACTOR_CODE;CONTRACTOR_DESC;PY1;CONTRACTOR_TYPE;SEQ;UNIT_ENG_DESC;" +
    			"START_DATE;END_DATE;DESCRIPTION");
    	table.removeRowAll();
    }
    
    
    /**
	 * 主表点击事件
	 */
    public void onCtzClick(){
    	
    	int selectedIndx=table.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	//单位编号置灰
    	callFunction("UI|CONTRACTOR_CODE|setEnabled", false);
    	
    	TParm parm=table.getParmValue();
    	String cCode = parm.getValue("CONTRACTOR_CODE",selectedIndx);
    	String cDesc = parm.getValue("CONTRACTOR_DESC",selectedIndx);
    	String engDesc = parm.getValue("UNIT_ENG_DESC",selectedIndx);
    	String py1 = parm.getValue("PY1",selectedIndx);
    	String cType = parm.getValue("CONTRACTOR_TYPE",selectedIndx);
    	String seq  = parm.getValue("SEQ", selectedIndx);
    	String startDate = parm.getValue("START_DATE",selectedIndx);
    	
    	if(startDate.length()>0){
    		startDate = startDate.substring(0, 10).replaceAll("-", "/");
    	}
    	String endDate = parm.getValue("END_DATE",selectedIndx);
    	if(endDate.length()>0){
    		endDate = endDate.substring(0, 10).replaceAll("-", "/");
    	}
    	String description = parm.getValue("DESCRIPTION",selectedIndx);
    	//String overdraft = parm.getValue("OVERDRAFT",selectedIndx);
    	
    	this.setValue("CONTRACTOR_CODE", cCode);
    	this.setValue("CONTRACTOR_DESC", cDesc);
    	this.setValue("PY1", py1);
    	this.setValue("CONTRACTOR_TYPE", cType);
    	this.setValue("SEQ", seq);
    	this.setValue("UNIT_ENG_DESC", engDesc);
    	//this.setValue("OVERDRAFT", overdraft);
    	this.setValue("START_DATE", startDate);
    	this.setValue("END_DATE", endDate);
    	this.setValue("DESCRIPTION", description);
    }
    
    /**
     * 获取页面数据
     */
    public TParm getData(){
    	TParm parm = new TParm();
    	parm = getParmForTag("CONTRACTOR_CODE;CONTRACTOR_DESC;PY1;CONTRACTOR_TYPE;SEQ;" +
    			"UNIT_ENG_DESC;DESCRIPTION");
    	parm.setData("OPT_USER", Operator.getID());
    	parm.setData("OPT_TERM", Operator.getIP());
    	String startDate = this.getValueString("START_DATE");
    	if(startDate.length()>0){
    		startDate = startDate.replaceAll("-", "/").substring(0, 10);
    	}
    	String endDate = this.getValueString("END_DATE");
    	if(endDate.length()>0){
    		endDate = endDate.replaceAll("-", "/").substring(0, 10);
    	}
    	parm.setData("START_DATE", startDate);
    	parm.setData("END_DATE", endDate);
    	//打印测试
    	//System.out.println("页面数据："+parm);
    	return parm;
    }
    
    /**
     * 校验页面数据
     */
    public boolean checkData() {
    	boolean flg = true;
    	//校验类型不能为空
    	String cType = this.getValueString("CONTRACTOR_TYPE");
    	if(cType.length()==0){
    		this.messageBox("类别不能为空！");
    		flg = false;
    		return flg;
    	}
    	
    	return flg;
    }
    
    /**
     * 判断录入编号是否存在
     */
    public boolean checkCode(){
    	boolean flg = true;
    	String cCode = this.getValueString("CONTRACTOR_CODE");
    	if(cCode.length()==0){
    		this.messageBox("单位编号不能为空！");
    		flg = false;
    		return flg;
    	}
    	String cDesc = this.getValueString("CONTRACTOR_DESC");
    	if(cDesc.length()==0){
    		this.messageBox("名称不能为空！");
    		flg = false;
    		return flg;
    	}
    	String sql = "SELECT CONTRACTOR_CODE FROM MEM_CONTRACTOR WHERE CONTRACTOR_CODE = '"+cCode+"'";
    	TParm parm = new TParm();
    	parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getValue("CONTRACTOR_CODE", 0).toString().length()>0){
    		this.messageBox("该单位编号已经存在！");
    		flg = false;
    		return flg;
    	}
    	return flg;
    }
    
    /**
     * 获取修改sql
     */
    public String getUpateSql(TParm parm){
    	String sql = "UPDATE MEM_CONTRACTOR SET CONTRACTOR_DESC='"+parm.getValue("CONTRACTOR_DESC")+"'," +
    					"UNIT_ENG_DESC='"+parm.getValue("UNIT_ENG_DESC")+"'," +
    					"PY1='"+parm.getValue("PY1")+"'," +
    					"CONTRACTOR_TYPE='"+parm.getValue("CONTRACTOR_TYPE")+"'," +
    					"SEQ='"+parm.getValue("SEQ")+"'," +
    					"START_DATE=TO_DATE('"+parm.getValue("START_DATE")+"','yyyy/MM/dd')," +
    					"END_DATE=TO_DATE('"+parm.getValue("END_DATE")+"','yyyy/MM/dd')," +
    					"DESCRIPTION='"+parm.getValue("DESCRIPTION")+"'," +
    					//"OVERDRAFT='"+parm.getValue("OVERDRAFT")+"'," +
    					"OPT_DATE=sysdate," +
    					"OPT_USER='"+parm.getValue("OPT_USER")+"'," +
    					"OPT_TERM='"+parm.getValue("OPT_TERM")+"' " +
    					" WHERE CONTRACTOR_CODE = '"+parm.getValue("CONTRACTOR_CODE")+"'";
    	//System.out.println("打印修改sql="+sql);
    	return sql;
    }
    
    /**
     * 获取插入sql
     */
    public String getInsertSql(TParm parm){
    	String sql = "INSERT INTO MEM_CONTRACTOR(CONTRACTOR_CODE,CONTRACTOR_DESC,UNIT_ENG_DESC," +
    			"PY1,SEQ,CONTRACTOR_TYPE,START_DATE,END_DATE,DESCRIPTION,OPT_DATE,OPT_USER,OPT_TERM)" +
    			" VALUES('"+parm.getValue("CONTRACTOR_CODE")+"'," +
    					"'"+parm.getValue("CONTRACTOR_DESC")+"'," +
    					"'"+parm.getValue("UNIT_ENG_DESC")+"'," +
    					"'"+parm.getValue("PY1")+"'," +
    					"'"+parm.getValue("SEQ")+"'," +
    					"'"+parm.getValue("CONTRACTOR_TYPE")+"'," +
    					"TO_DATE('"+parm.getValue("START_DATE")+"','yyyy/MM/dd')," +
    					"TO_DATE('"+parm.getValue("END_DATE")+"','yyyy/MM/dd')," +
    					"'"+parm.getValue("DESCRIPTION")+"'," +
    					//"'"+parm.getValue("OVERDRAFT")+"'," +
    					"sysdate," +
    					"'"+parm.getValue("OPT_USER")+"'," +
    					"'"+parm.getValue("OPT_TERM")+"')";
    	//System.out.println("打印插入sql="+sql);
    	return sql;
    }
    
    /**
     * 获取当前序号seq
     */
    public int getMaxSeq(String maxValue, String tableName,
            String where1,String value1,String where2,String value2) {
    	String sql = "SELECT MAX("+maxValue+") AS "+maxValue+" FROM "+tableName+" WHERE 1=1 ";
    	if(where1.trim().length()>0){
    		sql += " AND "+where1+" ='"+value1+"'";
    	}
    	if(where2.trim().length()>0){
    		sql += " AND "+where2+" ='"+value2+"'";
    	}
    	//System.out.println("最大的编号sql="+sql);
    	// 保存最大号
    	int max = 0;
    	//查询最大序号
    	TParm seqParm = new TParm(TJDODBTool.getInstance().select(sql));
    	String seq = seqParm.getValue(maxValue,0).toString().equals("")?"0"
    			:seqParm.getValue(maxValue,0).toString();
    	//System.out.println("seq="+seq);
    	int value = Integer.parseInt(seq);
    	//System.out.println("value="+value);
    	// 保存最大值
    	if (max < value) {
    		max = value;
    	}
    	// 最大号加1
    	max++;
    	//System.out.println("最大的编号 +1="+max);
    	return max;

    }
    
    /**
     * 获得简拼
     */
    public void onCPY1() {
    	this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
				TypeTool.getString(getValue("CONTRACTOR_DESC"))));// 简拼
    	this.grabFocus("CONTRACTOR_TYPE");
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
