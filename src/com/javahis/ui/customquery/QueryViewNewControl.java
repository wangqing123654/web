package com.javahis.ui.customquery;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 *
 * <p>Title: 查询显示窗口控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: BlueCore</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangjc 20151127
 * @version 1.0
 */
public class QueryViewNewControl extends TControl {
	
    /**
     * 初始化
     */
    public void onInit() {

    }


    /**
     * 清空事件
     */
    public void onClear() {
    	this.clearValue("FILE_NAME;MY_SQL");
    	((TTable)this.getComponent("TABLE")).setParmValue(new TParm());
    }
/*
    public void onExport() {
    	
//    	TTextArea title = (TTextArea) this.getComponent("TITLE");
    	String fileName = this.getValueString("FILE_NAME");
//    	String titleStr = title.getValue();
    	String sqlStr = this.getValueString("MY_SQL");
    	if(fileName.equals("")){
    		this.messageBox("请填写文件名");
    		return;
    	}
    	if(sqlStr.equals("")){
    		this.messageBox("请填写sql语句");
    		return;
    	}
    	String parmMap = "";
    	String titleStr = "";
//    	String[] b = sqlStr.split(",");
//    	boolean flg = false;
//    	for(int i=0;i<b.length;i++){
//    		String[] c = b[i].split(" ");
//    		for(int j=0;j<c.length;j++){
//    			if(c[j].equalsIgnoreCase("from")){
//    				int n = 1;
////    				System.out.println(c[j-1]);
//    				while(true){
//    					if(c[j-n].equals("") || c[j-n].equals("\n") || c[j-n].equals("\r")){
//    						n++;
//    					}else{
//    						flg = true;
//    	    				String[] d = c[j-n].split("\\.");
//    		    			if(d.length == 1){
//    		    				parmMap += c[j-n];
//    	    					titleStr1 += c[j-n]+",150";
//    		    			}else{
//    		    				parmMap += d[d.length-1]+";";
//    		    				titleStr1 += d[d.length-1]+",150;";
//    		    			}
//    		    			break;
//    					}
//    				}
//	    			break;
//    			}
//    		}
//    		if(flg){
//    			break;
//    		}else{
//    			String[] d = c[c.length-1].split("\\.");
//    			if(d.length == 1){
//    				parmMap += c[c.length-1]+";";
//    				titleStr1 += c[c.length-1]+",150;";
//    			}else{
//    				parmMap += d[d.length-1]+";";
//    				titleStr1 += d[d.length-1]+",150;";
//    			}
//    		}
//    	}
//    	System.out.println(titleStr1);
//    	System.out.println(sqlStr);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sqlStr));
    	if(parm == null || parm.getCount() <= 0){
    		this.messageBox("未查询到数据");
    		return;
    	}
    	String til[] = parm.getNames();
    	for(int h=0;h<til.length;h++){
    		titleStr += til[h]+",150;";
    		parmMap += til[h]+";";
    	}
    	TTable table = new TTable();
    	table.setHeader(titleStr);
    	table.setParmMap(parmMap);
    	table.setParmValue(parm);
    	ExportExcelUtil.getInstance().exportExcel(table,fileName);
    	
    }
*/   
    public void onQuery(){
    	String sqlStr = this.getValueString("MY_SQL");
    	if(sqlStr.equals("")){
    		this.messageBox("请填写sql语句");
    		return;
    	}
    	
    	String parmMap = "";
    	String titleStr = "";
    	
    	
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sqlStr));
    	if(parm == null || parm.getCount() <= 0){
    		this.messageBox("未查询到数据");
    		return;
    	}
    	String til[] = parm.getNames();
    	for(int h=0;h<til.length;h++){
    		if(h != til.length-1){
    			titleStr += til[h]+",150;";
        		parmMap += til[h]+";";
    		}else{
    			titleStr += til[h]+",150";
        		parmMap += til[h];
    		}
    		
    	}
    	
    	TTable table = (TTable) this.getComponent("TABLE");
    	table.setLockColumns("all");
    	table.setHeader(titleStr);
    	table.setParmMap(parmMap);
    	table.setParmValue(parm);
    }
    
    public void onExport(){
    	TTable table = (TTable) this.getComponent("TABLE");
    	TParm parm = table.getParmValue();
    	String fileName = this.getValueString("FILE_NAME");
    	if(fileName.equals("")){
    		this.messageBox("请填写文件名");
    		return;
    	}
    	
        if (null == parm || parm.getCount() <= 0) {
            this.messageBox("没有需要导出的数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, fileName);
    	
    }

}
