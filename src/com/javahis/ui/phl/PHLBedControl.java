package com.javahis.ui.phl;

import java.sql.Timestamp;
import jdo.phl.PhlBedTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>
 * Title: 静点室床位
 * </p>
 *
 * <p>
 * Description: 静点室床位
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class PHLBedControl
    extends TControl {

    private String action = "insert";

    private TTable table;
    public int selRow;
    public String beddesc="";
    
    private int count;
    
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public PHLBedControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
        this.onQuery();
    }

    /**
     * 保存方法
     */
    public void onSave() {
       /* if (!CheckData()) {
            return;
        }*/
    	if (table.getRowCount() == 0) {
            this.messageBox("没有执行数据");
        }
     
        TParm result = new TParm();
        TParm parm = new TParm();
        TParm orderparm = new TParm();//新插入
        TParm upderParm =new TParm();//修改的数据
        Timestamp date = SystemTool.getInstance().getDate();
        for (int i = 0; i < table.getRowCount(); i++) {
            if ("1".equals(table.getItemString(i, "BED_STATUS"))) {
                continue;
             }
            
            String region_code = table.getItemString(i, "REGION_CODE");
            String region_code_all = table.getItemString(i, "REGION_CODE_ALL");
            String bed_no = table.getItemString(i,"BED_NO");
            TParm rparm=checkQuer(region_code,region_code_all,bed_no);//保存之前查询验证
            if(rparm.getCount()>0){
            	upderParm.setData("REGION_CODE_ALL", i, region_code_all);
            	upderParm.setData("REGION_CODE", i, region_code);
            	upderParm.setData("TYPE", i,table.getItemString(i,"TYPE"));
            	upderParm.setData("BED_NO", i,bed_no);
            	upderParm.setData("BED_DESC", i,table.getItemString(i,"BED_DESC"));
            	upderParm.setData("BED_STATUS", i, "Y".equals(table.getItemString(i,"BED_STATUS"))? "1" : "0");
            	upderParm.setData("OPT_USER", i, Operator.getID());
            	upderParm.setData("OPT_DATE", i, date);
            	upderParm.setData("OPT_TERM", i, Operator.getIP());
            }else{
	            orderparm.setData("REGION_CODE_ALL", i, region_code_all);
	            orderparm.setData("REGION_CODE", i, region_code);
	            orderparm.setData("TYPE", i,table.getItemString(i,"TYPE"));
	            orderparm.setData("BED_NO", i,bed_no);
	            orderparm.setData("BED_DESC", i,table.getItemString(i,"BED_DESC"));
	            orderparm.setData("BED_STATUS", i, "Y".equals(table.getItemString(i,"BED_STATUS"))? "1" : "0");
	            orderparm.setData("OPT_USER", i, Operator.getID());
	            orderparm.setData("OPT_DATE", i, date);
	            orderparm.setData("OPT_TERM", i, Operator.getIP());
            }
        }
        parm.setData("BED_NUM", orderparm.getData());
        parm.setData("UPBED_NUM", upderParm.getData());
        result = TIOM_AppServer.executeAction("action.phl.PHLAction","PHLBedControl", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("保存失败");
            return;
        }
        this.messageBox("保存成功");
        getTable("TABLE").setSelectionMode(0);
        this.onClear();
        this.onQuery();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        String region_code = getValueString("REGION_CODE");
        if (!"".equals(region_code)) {
            parm.setData("REGION_CODE", region_code);
        }
        //=====pangben modify 20110622 start
        String region_code_all = getValueString("REGION_CODE_ALL");
        if (!"".equals(region_code_all)) {
            parm.setData("REGION_CODE_ALL", region_code_all);
        }
        //=====pangben modify 20110622 stop
        String bed_no = getValueString("BED_NO");
        if (!"".equals(bed_no)) {
            parm.setData("BED_NO", bed_no);
        }
        boolean onQflg=false;
        if ("Y".equals(this.getValue("BED"))&&"Y".equals(this.getValue("SEAT"))) {
	   		parm.setData("TYPE2", "");
	   		onQflg=true;
	   	}
        if(!onQflg){
        if ("Y".equals(this.getValue("BED"))) {
            parm.setData("TYPE0", "0");
            
        }
	   	if ("Y".equals(this.getValue("SEAT"))) {
	   		 parm.setData("TYPE1", "1");
	   		 
	   	}
        }
        TParm result = PhlBedTool.getInstance().onQuery(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        
        for(int i=0;i<result.getCount();i++){
        	if("1".equals(result.getValue("BED_STATUS", i))){
        		table.setLockCellRow(i, true);
        	}else{
        		table.setLockCellRow(i, false);
        	}
        	
        }
        table.setParmValue(result);
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        if (this.messageBox("提示", "是否删除", 2) == 0) {
            int row = table.getSelectedRow();
            if (row == -1) {
                return;
            }
            TParm parm = new TParm();
            parm.setData("REGION_CODE", getValueString("REGION_CODE"));
            parm.setData("REGION_CODE_ALL", getValueString("REGION_CODE_ALL"));//=======pangben modify 20110622
            parm.setData("BED_NO", getValueString("BED_NO"));
            TParm result = PhlBedTool.getInstance().onDelete(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("删除失败");
                return;
            }
            table.removeRow(row);
            table.setSelectionMode(0);
            this.messageBox("删除成功");
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
        }
        action = "insert";
    }

    /**
     * 清空方法
     */
    public void onClear() {
        // 清空VALUE
        String clear =
            "REGION_CODE;BED_NO;BED_DESC;BED_STATUS;TYPE";
        this.clearValue(clear);
        this.setValue("BED", "N");
        this.setValue("SEAT", "N");
        table.removeRowAll();
        getTable("TABLE").setSelectionMode(0);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "insert";
        this.setValue("REGION_CODE_ALL",Operator.getRegion());//========pangben modfiy 20110622
    }

    /**
     * 表格(CLNDIAG_TABLE)单击事件
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        
        if (row != -1) {
        	String bedstatus =table.getItemString(row, "BED_STATUS");
        	if("0".equals(bedstatus)){//未占床或座位的
        		 ( (TMenuItem) getComponent("delete")).setEnabled(true);
        	 }else{
        		 ( (TMenuItem) getComponent("delete")).setEnabled(false);
        	 }
           
            this.setValue("REGION_CODE", table.getItemString(row, "REGION_CODE"));
            this.setValue("REGION_CODE_ALL", table.getItemString(row, "REGION_CODE_ALL"));//pangben modify 20110622
            this.setValue("BED_NO", table.getItemString(row, "BED_NO"));
            this.setValue("BED_DESC", table.getItemString(row, "BED_DESC"));
            this.setValue("BED_STATUS", table.getItemString(row, "BED_STATUS"));
            this.setValue("TYPE", table.getItemString(row, "TYPE"));
            action = "update";
        }
    }


    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        table = this.getTable("TABLE");
        this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,"onROOMTABLEChargeValue");//回车监
      //  this.addEventListener("TABLE->" + TTableEvent.CLICKED);//回车监
        //========pangben modify 20110622 start 权限添加
        this.setValue("REGION_CODE_ALL",Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE_ALL");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE_ALL")));
        //===========pangben modify 20110622 stop

    }
    
    public void onNew(){
    	int selRow = table.addRow();
    	table.acceptText() ;
    
    	int cnum=0;
    	if(table.getRowCount()>1){
    		String rowNum=table.getItemString(selRow-1, "BED_NO");
        	 cnum =Integer.parseInt(rowNum);
    	}
    	table.setItem(selRow, "REGION_CODE_ALL",Operator.getRegion() );
		table.setItem(selRow, "REGION_CODE", this.getResion());
		table.setItem(selRow, "BED_NO", cnum+1);
		table.setItem(selRow, "BED_DESC", cnum+1+"床位");
		table.setItem(selRow, "BED_STATUS", "N");
		table.setItem(selRow, "TYPE", "0");
		table.setItem(selRow, "ADM_TYPE", "");
		table.setItem(selRow, "MR_NO", "");//总量
		table.setItem(selRow, "PAT_NAME", "");
		table.setItem(selRow, "SEX", "");
		
		
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

   
    
    /**add caoyong 2014/4/18
     * 床位类型改变值事件
     * @param obj
     * @return
     */
    public boolean onROOMTABLEChargeValue(Object obj) {
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
    	
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        //如果改变的节点数据和原来的数据相同就不改任何数据
        if (node.getValue().equals(node.getOldValue()))
            return false;
        //拿到table上的parmmap的列名
        String columnName = node.getTable().getDataStoreColumnName(node.
            getColumn());
        
        //拿到table
       // TTable table = node.getTable();
        //得到改变的行
        //int row = node.getRow();
        int row = table.getSelectedRow();
        //拿到当前改变后的数据
        String value = "" + node.getValue();
        //如果是名称改变了拼音1自动带出,并且科室名称不能为空
        
       
      if ("TYPE".equals(columnName)) {
            
    	  if("0".equals(value)){
    		/*  List list=new ArrayList();
    	        if(table.getRowCount()>0){
    	          for(int i=0;i<table.getRowCount();i++){
    	          if("1".equals(table.getItemString(i, "TYPE"))){
    	        	  list.add(table.getItemString(i, "BED_NO"));
    	            }
    	          }
    	        }*/
   		   beddesc="床位"; 
   	       }
    	  if("1".equals(value)){
   		   beddesc="座位"; 
   	      }
    	  
    	  
    	  table.setItem(row, "BED_DESC", row+1+beddesc);
            //设置行可保存

        }
        return false;
    }
    /**add caoyong 2014/4/18
     * 保存之前查询验证
     * @param obj
     * @return
     */
    public TParm checkQuer(String region_code,String region_code_all,String bed_no ){
    	TParm parm=new TParm();
    	if (!"".equals(region_code)) {
            parm.setData("REGION_CODE", region_code);
        }
        if (!"".equals(region_code_all)) {
            parm.setData("REGION_CODE_ALL", region_code_all);
        }
        if (!"".equals(bed_no)) {
            parm.setData("BED_NO", bed_no);
        }
        TParm result = PhlBedTool.getInstance().onQuery(parm);
    	return result;
    }
    
    public String getResion(){
    	String sql="SELECT REGION_CODE FROM PHL_REGION ";
    	TParm parmt = new TParm(TJDODBTool.getInstance().select(sql));
    	return parmt.getValue("REGION_CODE", 0);
    }
    /**
     * 座位/床位过滤
     */
    public void onSelect(){
    	onQuery(); 
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
}
