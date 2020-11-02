package com.javahis.ui.mem;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 套餐明细查询</p>
 *
 * <p>Description: 套餐明细查询</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author yuml 20141024
 * @version 1.0
 */
public class MemPackageDetailQueryControl extends TControl{
	
	TTextField ORDER_DESC; 
	TTextField WORDER_DESC;
	TTextFormat SECTION_DESC;
	
	public void onInit() {
		super.onInit();
		initPage();
	
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {
		setValue("PACKAGE_DESC", "");
		setValue("SECTION_DESC", "");
		setValue("ORDER_DESC", "");
		setValue("WORDER_DESC", "");
		
        TParm parm = new TParm();     
        parm.setData("CAT1_TYPE", "");
        //parm.setData("RX_TYPE",6);  
        ORDER_DESC = (TTextField) this.getComponent("ORDER_DESC");
        WORDER_DESC = (TTextField) this.getComponent("WORDER_DESC");
        SECTION_DESC = (TTextFormat) getComponent("SECTION_DESC");
        // 设置弹出菜单     
        ORDER_DESC.setPopupMenuParameter("UD", getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);    
        // 定义接受返回值方法  
        ORDER_DESC.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        // 设置弹出菜单     
        WORDER_DESC.setPopupMenuParameter("UA", getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);    
        // 定义接受返回值方法  
        WORDER_DESC.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturnW");
        //时程下拉菜单
        onPackageDescClick();
        
	}
	
	   /**
     * 医嘱名称接受返回值方法
     * 
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            ORDER_DESC.setValue(order_desc);
    }
	   /**
     * 物价名称接受返回值方法
     * 
     * @param tag
     * @param obj
     */
    public void popReturnW(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
        	WORDER_DESC.setValue(order_desc);
    }
	/**
	 * 查询
	 */
	public void onQuery() {
		TParm selParmone = new TParm();
		TParm selParmtwo = new TParm();
		String packageDescWhere = "";
		if (getValue("PACKAGE_DESC").toString().length() != 0)
			packageDescWhere = " AND A.PACKAGE_CODE = '"
					+ getValue("PACKAGE_DESC") + "'  ";
		String sectionDescWhere = "";
		if (!"".equals(getValueString("SECTION_DESC")))
			sectionDescWhere = " AND B.SECTION_CODE = '" + getValue("SECTION_DESC")
					+ "'  ";
		
		String orderDescWhere = "";
		if (getValue("ORDER_DESC").toString().length() != 0)
			orderDescWhere = " AND C.ORDER_DESC = '" + getValue("ORDER_DESC")
					+ "'  ";
		String worderDescWhere = "";
		if (getValue("WORDER_DESC").toString().length() != 0)
			worderDescWhere = " AND E.ORDER_DESC = '" + getValue("WORDER_DESC")
					+ "'  ";
		String worderDescWheretwo = "";
		if (getValue("WORDER_DESC").toString().length() != 0)
			worderDescWheretwo = " AND C.ORDER_DESC = '" + getValue("WORDER_DESC")
					+ "'  ";
		String sqlone =" SELECT A.PACKAGE_CODE, A.PACKAGE_DESC,B.SECTION_CODE, " +
				"B.SECTION_DESC,B.DESCRIPTION,C.ORDER_CODE, C.ORDER_DESC,D.ORDER_CODE AS WORDER_CODE ," +
				" E.ORDER_DESC AS WORDER_DESC , E.OWN_PRICE,e.active_flg" +
				" FROM MEM_PACKAGE A, MEM_PACKAGE_SECTION B, MEM_PACKAGE_SECTION_D C,SYS_ORDERSETDETAIL D," +
				"SYS_FEE E WHERE   A.PACKAGE_CODE NOT IN ('01', '02', '03') " +packageDescWhere+sectionDescWhere+orderDescWhere+worderDescWhere+
				"AND   A.PACKAGE_CODE = B.PACKAGE_CODE" +
				" AND B.PACKAGE_CODE = C.PACKAGE_CODE AND B.SECTION_CODE = C.SECTION_CODE " +
				" AND C.ORDER_CODE = D.ORDERSET_CODE" +
				"  AND D.ORDER_CODE  = E.ORDER_CODE" +
				" ORDER BY A.PACKAGE_CODE, B.SECTION_CODE,C.ORDER_CODE,e.active_flg ";
		String sqltwo = "SELECT A.PACKAGE_CODE, A.PACKAGE_DESC , B.SECTION_CODE, B.SECTION_DESC , " +
				"B.DESCRIPTION ,C.ORDER_CODE,C.ORDER_DESC,C.UNIT_PRICE ,D.active_flg " +
				"FROM MEM_PACKAGE A, MEM_PACKAGE_SECTION B, MEM_PACKAGE_SECTION_D C ，sys_fee D" +
				" WHERE A.PACKAGE_CODE NOT IN ('01', '02', '03')" +packageDescWhere+sectionDescWhere+orderDescWhere+worderDescWheretwo+
				" AND A.PACKAGE_CODE = B.PACKAGE_CODE AND B.PACKAGE_CODE = C.PACKAGE_CODE" +
				" AND B.SECTION_CODE = C.SECTION_CODE AND C.ORDER_CODE NOT LIKE 'X%'" +
				" AND C.ORDER_CODE NOT LIKE 'Y%' and c.order_code=d.order_code and c.order_code=c.orderset_code" +
				" ORDER BY A.PACKAGE_CODE, B.SECTION_CODE,C.ORDER_CODE,D.active_flg";
		System.out.println("sql1"+sqlone);
		System.out.println("sql1"+sqltwo);
		selParmone = new TParm(TJDODBTool.getInstance().select(sqlone));
		selParmtwo = new TParm(TJDODBTool.getInstance().select(sqltwo));
		if (selParmone.getCount() < 1 && selParmtwo.getCount() < 1) {
			this.messageBox("查无数据");
			this.initPage();
		}
		
		TParm endDate = new TParm();
		int count = selParmone.getCount("PACKAGE_DESC");
		for (int i = 0; i < count; i++) {
			endDate.addData("PACKAGE_CODE", selParmone.getValue("PACKAGE_CODE", i));
			endDate.addData("PACKAGE_DESC", selParmone.getValue("PACKAGE_DESC", i));
			endDate.addData("SECTION_CODE", selParmone.getValue("SECTION_CODE", i));
			endDate.addData("SECTION_DESC", selParmone.getValue("SECTION_DESC", i));
			endDate.addData("DESCRIPTION", selParmone.getValue("DESCRIPTION", i));
			endDate.addData("ORDER_CODE", selParmone.getValue("ORDER_CODE", i));
			endDate.addData("ORDER_DESC", selParmone.getValue("ORDER_DESC", i));
			endDate.addData("WORDER_CODE", selParmone.getValue("WORDER_CODE", i));
			endDate.addData("WORDER_DESC", selParmone.getValue("WORDER_DESC", i));
			endDate.addData("OWN_PRICE", selParmone.getValue("OWN_PRICE", i));
		}
		count = selParmtwo.getCount("PACKAGE_DESC");
		for (int i = 0; i < count; i++) {
			endDate.addData("PACKAGE_CODE", selParmtwo.getValue("PACKAGE_CODE", i));
			endDate.addData("PACKAGE_DESC", selParmtwo.getValue("PACKAGE_DESC", i));
			endDate.addData("SECTION_CODE", selParmtwo.getValue("SECTION_CODE", i));
			endDate.addData("SECTION_DESC", selParmtwo.getValue("SECTION_DESC", i));
			endDate.addData("DESCRIPTION", selParmtwo.getValue("DESCRIPTION", i));
			endDate.addData("ORDER_CODE", "");
			endDate.addData("ORDER_DESC", "");
			endDate.addData("WORDER_CODE", selParmtwo.getValue("ORDER_CODE", i));
			endDate.addData("WORDER_DESC", selParmtwo.getValue("ORDER_DESC", i));
			endDate.addData("OWN_PRICE", selParmtwo.getValue("UNIT_PRICE", i));
		}
		this.callFunction("UI|Table|setParmValue", endDate);
	}


	/**
	 * 汇出Excel
	 */
	public void onExport() {
		
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "套餐明细数据报表");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initPage();
		TTable table = (TTable) this.getComponent("Table");
		TParm parm = new TParm();
		table.setParmValue(parm);
	}
	
	/**
	 * 时程下拉菜单
	 */
	public void onPackageDescClick(){
		String packageCode = getValueString("PACKAGE_DESC");
		
        String where = "";
		if (packageCode.length() > 0){
			where = " where package_code = '"
					+ packageCode + "'  ";
		}
		String sql = " select SECTION_CODE ID,SECTION_DESC NAME from mem_package_section "
				+ where + " order by ID";
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
		parm1 = addNullRow(parm1);
		SECTION_DESC.setHorizontalAlignment(2);
		SECTION_DESC.setPopupMenuHeader("代码,100;名称,200");
		SECTION_DESC.setPopupMenuWidth(300);
		SECTION_DESC.setPopupMenuHeight(300);
		SECTION_DESC.setFormatType("combo");
		SECTION_DESC.setShowColumnList("NAME");
		SECTION_DESC.setValueColumn("ID");
		SECTION_DESC.setPopupMenuData(parm1);
		SECTION_DESC.onQuery();
	}
	
	/**
	 * 在下拉菜单上面添加空行
	 * @param parm
	 * @return
	 */
	public TParm addNullRow(TParm parm){
		TParm result = new TParm();
		result.addData("ID", "");
		result.addData("NAME", "");
		for (int i = 0; i < parm.getCount(); i++) {
			result.addData("ID", parm.getData("ID", i));
			result.addData("NAME", parm.getData("NAME", i));
		}
		result.addData("SYSTEM", "COLUMNS", "ID");
		result.addData("SYSTEM", "COLUMNS", "NAME");
		result.setCount(parm.getCount()+1);
		return result;
	}

}
