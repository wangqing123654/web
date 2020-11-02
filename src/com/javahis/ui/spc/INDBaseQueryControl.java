package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**   
 * <p>
 * Title: 基数药查询Control
 * </p>
 *   
 * <p>
 * Description: 基数药查询Control  
 * </p>
 *     
 * <p>
 * Copyright: Copyright (c) 2008   
 * </p>
 * 
 * <p>                   
 * Company: bluecore  
 * </p>
 * 
 * @author wjc 2015.01.16 
 * @version 1.0
 */
public class INDBaseQueryControl extends TControl {
	
	private SimpleDateFormat formateDate = new SimpleDateFormat("yyyy-MM-dd");
	private TTable table;
	private String start_date;
	private String end_date;

	/**
	 * 初始化界面
	 */
	public void onInit(){
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		// 设置弹出菜单
		TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // 定义接受返回值方法
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		this.table = this.getTable("TABLE");
		//查询区间
		this.start_date = formateDate.format(this.getTextFormat("START_DATE").getValue());
		this.end_date = formateDate.format(this.getTextFormat("END_DATE").getValue());
		//查询备药单号
		String sql = "SELECT BASEMANAGE_NO FROM IND_BASEMANAGEM WHERE OPT_DATE BETWEEN TO_DATE ('"
				+this.start_date+" 00:00:00','YYYY-MM-DD HH24:MI:SS') AND TO_DATE ('"
				+this.end_date+" 23:59:59','YYYY-MM-DD HH24:MI:SS') ";
		//备药部门
		String app_org_code = this.getValueString("APP_ORG_CODE");
		if(!app_org_code.equals("")){
			sql += " AND APP_ORG_CODE ='"+app_org_code+"' ";
		}
		//供应部门
		String to_org_code = this.getValueString("TO_ORG_CODE");
		if(!to_org_code.equals("")){
			sql += " AND TO_ORG_CODE ='"+to_org_code+"' ";
		}
		//备药单号
		String basemanage_no = this.getValueString("BASEMANAGE_NO");
		if(!basemanage_no.equals("")){
			sql += " AND BASEMANAGE_NO ='"+basemanage_no+"' ";
		}	
		//查询数据
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() <= 0){
			this.table.removeRowAll();
			this.messageBox("未查询到相应数据！");
			return;
		}
		//药品名称
		String order_code = this.getTextField("ORDER_CODE").getValue();
		TParm result = new TParm();
		//查询明细
		for(int i=0;i<parm.getCount();i++){
			String item_sql_start = "SELECT A.BASEMANAGE_NO, "
								+" A.QTY, G.UNIT_CHN_DESC AS UNIT, A.RETAIL_PRICE, C.VERIFYIN_PRICE, "
								+" SUM(C.STOCK_QTY) AS STOCK_QTY, H.DOSAGE_QTY, D.ORDER_DESC, D.SPECIFICATION, "
								+" E.DEPT_CHN_DESC AS APP_CHN_DESC, F.DEPT_CHN_DESC AS TO_CHN_DESC, "
								+" I.UNIT_CHN_DESC AS BIG_UNIT, J.UNIT_CHN_DESC AS SMALL_UNIT "
					+" FROM IND_BASEMANAGED A, IND_BASEMANAGEM B, IND_STOCK C, "
							+" PHA_BASE D, SYS_DEPT E, SYS_DEPT F, SYS_UNIT G, PHA_TRANSUNIT H, "
							+" SYS_UNIT I, SYS_UNIT J "
						+" WHERE A.BASEMANAGE_NO='" +parm.getValue("BASEMANAGE_NO", i)+ "' "
								+" AND A.BASEMANAGE_NO = B.BASEMANAGE_NO "
								+" AND B.APP_ORG_CODE = C.ORG_CODE "
								+" AND B.APP_ORG_CODE = E.DEPT_CODE "
								+" AND B.TO_ORG_CODE = F.DEPT_CODE "
								+" AND A.ORDER_CODE = D.ORDER_CODE "
								+" AND A.ORDER_CODE = C.ORDER_CODE "
								+" AND A.UNIT_CODE = G.UNIT_CODE "
								+" AND A.ORDER_CODE = H.ORDER_CODE "
								+" AND H.STOCK_UNIT = I.UNIT_CODE "
								+" AND H.DOSAGE_UNIT = J.UNIT_CODE ";
			String item_sql_end = " GROUP BY "
									+" A.BASEMANAGE_NO, "
									+" A.QTY, G.UNIT_CHN_DESC, A.RETAIL_PRICE, C.VERIFYIN_PRICE, "
									+" D.ORDER_DESC, D.SPECIFICATION, "
									+" E.DEPT_CHN_DESC, F.DEPT_CHN_DESC, "
									+" I.UNIT_CHN_DESC, J.UNIT_CHN_DESC, "
									+" H.DOSAGE_QTY, A.SEQ_NO "
							+ "ORDER BY A.SEQ_NO, A.BASEMANAGE_NO ";
			if(!order_code.equals("")){
				item_sql_start += " AND A.ORDER_CODE = '"+order_code+"' ";
			}
//			System.out.println(item_sql_start+item_sql_end);
			TParm value = new TParm(TJDODBTool.getInstance().select(item_sql_start+item_sql_end));
			for(int j=0;j<value.getCount();j++){
				//备药部门
				result.addData("APP_CHN_DESC", value.getValue("APP_CHN_DESC", j));
				//供应部门
				result.addData("TO_CHN_DESC", value.getValue("TO_CHN_DESC", j));
				//药品名称
				result.addData("ORDER_DESC", value.getValue("ORDER_DESC", j));
				//规格
				result.addData("SPECIFICATION", value.getValue("SPECIFICATION", j));
				//备药数量
				result.addData("QTY", value.getValue("QTY", j));
				//单位
				result.addData("UNIT", value.getValue("UNIT", j));
				//进价
				result.addData("YS_PRICE", value.getValue("VERIFYIN_PRICE", j));
				//零售价
				result.addData("RETAIL_PRICE", value.getValue("RETAIL_PRICE", j));
				//当前库存
				int packQty = value.getInt("STOCK_QTY", j)/value.getInt("DOSAGE_QTY", j);//整包装
				int scatteredQty = value.getInt("STOCK_QTY", j)%value.getInt("DOSAGE_QTY", j);//零散包装
				result.addData("STOCK_QTY", packQty+value.getValue("BIG_UNIT", j)+scatteredQty+value.getValue("SMALL_UNIT", j));
				//备药单号
				result.addData("BASEMANAGE_NO", value.getValue("BASEMANAGE_NO", j));
			}
		}
		this.table.setParmValue(result);
	}
	
	/**
	 * 清空
	 */
	public void onClear(){
		String clearStr = "APP_ORG_CODE;ORDER_CODE;ORDER;BASEMANAGE_NO;TO_ORG_CODE";
		this.clearValue(clearStr);
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		this.table.removeRowAll();
	}
	
	 /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
    	TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order))
            getTextField("ORDER").setValue(order);
    }
	
    /**
     * 打印报表
     */
	public void onPrint(){
		this.table = this.getTable("TABLE");
		if(this.table.getRowCount()>0){
			//表头数据
			TParm date = new TParm();
			//制表时间
			date.setData("DATA", "TEXT",
                    "统计区间: " + this.start_date +" 00:00:00～"+this.end_date+" 23:59:59");
			//制表人
			date.setData("USER", "TEXT", Operator.getName());
			//表格数据
			TParm parm = new TParm();
			for(int i = 0; i < this.table.getRowCount(); i++){
				parm.addData("APP_CHN_DESC",this.table.getItemString(i, "APP_CHN_DESC"));
				parm.addData("TO_CHN_DESC",this.table.getItemString(i, "TO_CHN_DESC"));
				parm.addData("ORDER_DESC",this.table.getItemString(i, "ORDER_DESC"));
				parm.addData("SPECIFICATION",this.table.getItemString(i, "SPECIFICATION"));
				parm.addData("QTY",this.table.getItemString(i, "QTY"));
				parm.addData("UNIT",this.table.getItemString(i, "UNIT"));
				parm.addData("YS_PRICE",this.table.getItemString(i, "YS_PRICE"));
				parm.addData("RETAIL_PRICE",this.table.getItemString(i, "RETAIL_PRICE"));
				parm.addData("STOCK_QTY",this.table.getItemString(i, "STOCK_QTY"));
				parm.addData("BASEMANAGE_NO",this.table.getItemString(i, "BASEMANAGE_NO"));
				
			}
			parm.setCount(parm.getCount("APP_CHN_DESC"));
			parm.addData("SYSTEM", "COLUMNS", "APP_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "TO_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			parm.addData("SYSTEM", "COLUMNS", "QTY");
			parm.addData("SYSTEM", "COLUMNS", "UNIT");
			parm.addData("SYSTEM", "COLUMNS", "YS_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "RETAIL_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
			parm.addData("SYSTEM", "COLUMNS", "BASEMANAGE_NO");
			date.setData("TABLE",parm.getData());
			this.openPrintWindow("%ROOT%\\config\\prt\\spc\\INDBaseQuery.jhw", date);
		}else{
			this.messageBox("没有打印数据");
            return;
		}
	}
	
	/**
	 * 导出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "药房基数查询明细表");
	}
	
	/**
	 * 获取TTextFormat控件
	 * @param tag
	 * @return TTextFormat
	 */
	public TTextFormat getTextFormat(String tag){
		return (TTextFormat) this.getComponent(tag);
	}
	
	/**
	 * 获取TTextField控件
	 * @param tag
	 * @return TTextField
	 */
	public TTextField getTextField(String tag){
		return (TTextField) this.getComponent(tag);
	}
	
	/**
	 * 获取TTable控件
	 * @param tag 
	 * @return TTable
	 */
	public TTable getTable(String tag){
		return (TTable) this.getComponent(tag);
	}
}
