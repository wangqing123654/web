package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import jdo.inv.INVSQL;
import com.dongyang.ui.TComboBox;
import jdo.inv.INVPublicTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTextFormat;
import java.sql.Timestamp;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TDS;



/**
 * <p>Title: 灭菌记账明细</p>
 *
 * <p>Description:灭菌记账明细 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 090729
 * @version 1.0
 */
public class INVSuptitemDetallControl
    extends TControl {

    /**
     * 物资主表
     */
    private TTable table;


    public void onInit() {
        super.onInit();
        //项目主表
        table = (TTable) getComponent("TABLE");
        //初始化table
        iniTable();
        //初始化combo
        iniCombo();
        //放入部分参数
        initValue();
        observer();
    }

    /**
     * 添加观察者
     */
    public void observer() {
        //给主表添加观察者
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL("SELECT SUPITEM_CODE,SUPITEM_DESC FROM INV_SUPTITEM");
        dataStore.retrieve();
        //明细表添加观察者
        TDS tds = (TDS) table.getDataStore();
        tds.addObserver(new INVSuptitemTob(dataStore));
    }

    /**
     * 初始化部分数据
     */
    public void iniCombo() {
        String sql =
            "SELECT DEPT_CODE,DEPT_CHN_DESC,DEPT_ABS_DESC,PY1 FROM SYS_DEPT " +
            "WHERE ACTIVE_FLG='Y' AND FINAL_FLG='Y' ORDER BY SEQ,DEPT_CODE";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));

        TComboBox comboBox = new TComboBox();
        comboBox.setParmMap("id:DEPT_CODE;name:DEPT_CHN_DESC");
        comboBox.setParmValue(parm);
        comboBox.setShowID(true);
        comboBox.setShowName(true);
        comboBox.setExpandWidth(30);
        comboBox.setTableShowList("name");
        table.addItem("DEPT", comboBox);

        TParm parmUser = INVPublicTool.getInstance().getInvOperator();
        comboBox = (TComboBox)this.getComponent("CASHIER_CODE");
        comboBox.setParmValue(parmUser);
        comboBox.updateUI();
        comboBox.setValue(Operator.getID());

        comboBox = new TComboBox();
        comboBox.setParmMap("id:USER_ID;name:USER_NAME;py1:PY1");
        comboBox.setParmValue(parmUser);
        comboBox.setShowID(true);
        comboBox.setShowName(true);
        comboBox.setExpandWidth(30);
        comboBox.setTableShowList("name");
        table.addItem("OPERATOR", comboBox);
    }

    /**
     * 初始化tale
     */
    public void iniTable() {
        table.setSQL(
            "SELECT * FROM INV_SUPTITEMDETAIL WHERE SUP_DETAIL_NO IS NULL");
        table.retrieve();
    }

    /**
     * 放入部分初始数据
     */
    public void initValue() {
        //入库人员
        TComboBox conBox = (TComboBox)this.getComponent("CASHIER_CODE");
        conBox.setValue(Operator.getID());
        String orgCode = "";
//       //查找科室
//      TParm toOrgParm = INVPublicTool.getInstance().getOrgCode("B");
//      if (toOrgParm.getErrCode() >= 0) {
//          orgCode = toOrgParm.getValue("ORG_CODE", 0);
//      }
//      //默认记账科室
//      this.setValue("USE_DEPT",orgCode);

        //入库时间
        Timestamp date = SystemTool.getInstance().getDate();
        TTextFormat format = (TTextFormat)this.getComponent("SUP_DATE");
        this.setValue("SUP_DATEE", date);
        format.setValue(date);

    }

    public void onSupitemCodeSelected() {
        TTextFormat format = (TTextFormat)this.getComponent("SUPITEM_CODE");
        this.setValue("COST_PRICE", format.getComboValue("COST_PRICE"));
        this.setValue("ADD_PRICE", format.getComboValue("ADD_PRICE"));
    }

    //zhangyong20091204 onChangeQty
    public void onChangeQty() {
        //this.messageBox("");
        double qty = this.getValueDouble("QTY");
        TTextFormat format = (TTextFormat)this.getComponent("SUPITEM_CODE");
        this.setValue("COST_PRICE",
                      Double.parseDouble(format.getComboValue("COST_PRICE").
                                         toString()) * qty);
        this.setValue("ADD_PRICE",
                      Double.parseDouble(format.getComboValue("ADD_PRICE").
                                         toString()) * qty);
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = getQueryData();
        String sql = INVSQL.getInvSuptitemDetallQuerySql(parm);
        table.setSQL(sql);
        table.retrieve();
        table.setDSValue();
    }

    /**
     * 删除
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("没有选择删除数据");
            return;
        }
        table.removeRow(row);
        if (!table.update()) {
            messageBox("删除失败!");
            return;
        }
        messageBox("删除成功!");
        this.onClear();

    }

    /**
     * 得到查询sql条件
     * @return String
     */
    public TParm getQueryData() {
        TParm parm = new TParm();
        parm.setData("SUP_DETAIL_NO", getValueString("SUP_DETAIL_NO"));
        parm.setData("SUP_DETAIL_SEQ", getValueInt("SUP_DETAIL_SEQ"));
        parm.setData("SUP_DATE", getValue("SUP_DATE"));
        parm.setData("SUP_DATEE", getValue("SUP_DATEE"));
        parm.setData("USE_DEPT", getValueString("USE_DEPT"));
        parm.setData("SUPITEM_CODE", getValueString("SUPITEM_CODE"));
        return parm;
    }

    /**
     * table的点击事件
     */
    public void onClickedTable() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        //table里面的数据
        TParm tableValue = table.getDataStore().getBuffer(table.getDataStore().
            PRIMARY);
        setTextValue(tableValue.getRow(row));
        setEnableD(false);
    }

    public void setEnableD(boolean boo) {
        this.callFunction("UI|SUP_DETAIL_NO|setEnabled", boo);
    }

    /**
     * 数据上翻
     * @param parm TParm
     */
    public void setTextValue(TParm parm) {
        this.setValueForParm("SUP_DETAIL_NO;SUP_DETAIL_SEQ;SUP_DATE;" +
                             "USE_DEPT;SUPITEM_CODE;QTY;COST_PRICE;" +
                             "ADD_PRICE;CASHIER_CODE;DESCRIPTION", parm);
    }

    /**
     * 清空
     */
    public void onClear() {
        setEnableD(true);
        this.clearValue("SUP_DETAIL_NO;SUP_DETAIL_SEQ;SUP_DATE;SUP_DATEE" +
                        "USE_DEPT;SUPITEM_CODE;QTY;COST_PRICE;" +
                        "ADD_PRICE;CASHIER_CODE;DESCRIPTION");
        clearTextFormat();
        table.clearSelection();
        initValue();
         table.removeRowAll();
         table.resetModify();
    }

    /**
     * 清空textFormat的数据
     */
    public void clearTextFormat() {
        //科室
        this.setValue("USE_DEPT", "");
        this.setText("USE_DEPT", "");
        //项目
        this.setValue("SUPITEM_CODE", "");
        this.setText("SUPITEM_CODE", "");
    }

    /**
     * 保存
     * @return boolean
     */
    public boolean onUpdate() {
    	if(table.getRowCount()==0 ||table ==null){
    		messageBox("没有保存数据") ;
    		return false ;
    	}
        int row = table.getSelectedRow();
        boolean saveFlg = false;
        //如果有选中则为更新
        if (row >= 0) {
            //处理更新数据
            if (!dealUpdate(row, saveFlg))
                return false;
        }
        else {
            //处理新增数据
        	for(int i=0;i<table.getRowCount();i++){
        	        //单号
        	        if(table.getItemString(i, "SUP_DETAIL_NO").equals("") || 
        	        		table.getItemString(i, "SUP_DETAIL_NO").length()<=0) {
        	        	 String supdetailNo = getSupDetailNo();
             	        if (supdetailNo == null || supdetailNo.length() == 0)
             	            return false;
             	       table.setItem(i, "SUP_DETAIL_NO", supdetailNo);
        	        }
        	  
        	}
//            if (!dealNewData())
//                return false;
        }
        //保存数据方法
        return saveData();
    }

    /**
     * 调用保存方法
     * @return boolean
     */
    public boolean saveData() {
    	
        if (!table.update()) {
            messageBox("保存失败!");
            return false;
        }
        messageBox("保存成功!");
        table.resetModify();
        table.setDSValue();
        onClear();
        return true;
    }

    /**
     * 处理新增
     * @return boolean
     */
    public boolean dealNewData() {
        //名称
        String useDept = getValueString("USE_DEPT");
        //检核名称
        if (!checkItemDesc(useDept))
            return false;

        //记账项目
        String supItemCode = getValueString("SUPITEM_CODE");
        if (supItemCode == null || supItemCode.length() == 0) {
            messageBox("记账项目不能为空!");
            return false;
        }

        //得到结成本价格
        double costPrice = getValueDouble("COST_PRICE");
        if (costPrice < 0) {
            messageBox("成本不能小于零!");
            return false;
        }

        //数量
        int qty = getValueInt("QTY");
        if (qty <= 0) {
            messageBox("数量不合法!");
            return false;
        }

        //得到附加金额
        double addPrice = getValueDouble("ADD_PRICE");
        if (addPrice < 0) {
            messageBox("附加金额不能小于零!");
            return false;
        }
        //收费员
        String casherCode = getValueString("CASHIER_CODE");
        if (casherCode == null || casherCode.length() == 0) {
            messageBox("收费员不能为空!");
            return false;
        }

        //处理新增数据
        return setNewData();
    }

    /**
     * 检核代码
     * @param supItemCode String
     * @return boolean
     */
    public boolean checkSupItemCode(String supItemCode) {
        if (supItemCode == null || supItemCode.length() == 0) {
            messageBox("代码不能为空");
            return false;
        }
        if (table.getDataStore().exist("SUPITEM_CODE='" + supItemCode + "'")) {
            messageBox("代码" + supItemCode + "重复");
            return false;
        }
        return true;    
    }

    /**
     * 处理新增数据 
     * @return boolean
     */
    public boolean setNewData() {
//        String supdetailNo = getSupDetailNo();
//        if (supdetailNo == null || supdetailNo.length() == 0)
//            return false;
        int row = table.addRow();
        //单号
//        table.setItem(row, "SUP_DETAIL_NO", supdetailNo);
        //编号
        table.setItem(row, "SUP_DETAIL_SEQ", table.getRowCount());
        //时间
        table.setItem(row, "SUP_DATE", SystemTool.getInstance().getDate());
        //收费员
        table.setItem(row, "CASHIER_CODE", getValueString("CASHIER_CODE"));
        //项目代码
        table.setItem(row, "SUPITEM_CODE", getValueString("SUPITEM_CODE"));
        //科室
        table.setItem(row, "USE_DEPT", getValueString("USE_DEPT"));
        //成本
        table.setItem(row, "COST_PRICE", getValue("COST_PRICE"));
        //附加费用
        table.setItem(row, "ADD_PRICE", getValue("ADD_PRICE"));
        //备注
        table.setItem(row, "DESCRIPTION", getValue("DESCRIPTION"));
        //zhangyong20091028
        table.setItem(row, "QTY", this.getValueInt("QTY"));

        //添加固定参数
        setTableData(row); 
        this.clearValue("SUP_DETAIL_NO;SUP_DETAIL_SEQ;SUP_DATE;SUP_DATEE" +
                "USE_DEPT;SUPITEM_CODE;QTY;COST_PRICE;" +
                "ADD_PRICE;CASHIER_CODE;DESCRIPTION");
        clearTextFormat();
        return true;
    }

    /**
     * 取记账编号
     * @return String
     */
    public String getSupDetailNo() {
        //入库单号
        String dispenseNo = SystemTool.getInstance().getSUDispense();
        if (dispenseNo == null || dispenseNo.length() <= 0) {
            messageBox_("取记账编号错误!");
            return null;
        }
        return dispenseNo;
    }

    /**
     * 处理更新数据
     * @param row int
     * @param saveFlg boolean
     * @return boolean
     */
    public boolean dealUpdate(int row, boolean saveFlg) {
        //名称
        String useDept = getValueString("USE_DEPT");
        //检核名称
        if (!checkItemDesc(useDept))
            return false;
        String desc = table.getItemString(row, "USE_DEPT");
        //如果有修改则更新
        if (!useDept.equals(desc)) {
            table.setItem(row, "USE_DEPT", useDept);
            //记录是否有修改
            saveFlg = true;
        }
        //记账项目
        String supItemCode = getValueString("SUPITEM_CODE");
        if (supItemCode == null || supItemCode.length() == 0) {
            messageBox("记账项目不能为空!");
            return false;
        }
        String itemCode = table.getItemString(row, "SUPITEM_CODE");
        if (!itemCode.equals(supItemCode)) {
            table.setItem(row, "SUPITEM_CODE", supItemCode);
            //记录是否有修改
            saveFlg = true;
        }
        //收费员
        String casherCode = getValueString("CASHIER_CODE");
        if (casherCode == null || casherCode.length() == 0) {
            messageBox("收费员不能为空!");
            return false;
        }
        if (!casherCode.equals(table.getItemString(row, "CASHIER_CODE"))) {
            table.setItem(row, "CASHIER_CODE", casherCode);
            //记录是否有修改
            saveFlg = true;

        }
        //得到结成本价格
        double costPrice = getValueDouble("COST_PRICE");
        if (costPrice < 0) {
            messageBox("成本不能小于零!");
            return false;
        }
        double price = table.getItemDouble(row, "COST_PRICE");
        if (costPrice != price) {
            table.setItem(row, "COST_PRICE", costPrice);
            //记录是否有修改
            saveFlg = true;
        }
        //数量
        int qty = getValueInt("QTY");
        if (qty <= 0) {
            messageBox("数量不合法!");
            return false;
        }
        if (qty != table.getItemInt(row, "QTY")) {
            table.setItem(row, "QTY", qty);
            //记录是否有修改
            saveFlg = true;
        }
        //得到附加金额
        double addPrice = getValueDouble("ADD_PRICE");
        if (addPrice < 0) {
            messageBox("附加金额不能小于零!");
            return false;
        }
        price = table.getItemDouble(row, "ADD_PRICE");
        if (addPrice != price) {
            table.setItem(row, "ADD_PRICE", costPrice);
            //记录是否有修改
            saveFlg = true;
        }
        //名称
        String decription = getValueString("DESCRIPTION");
        String decript = table.getItemString(row, "DESCRIPTION");
        //如果有修改则更新
        if (!decription.equals(decript)) {
            table.setItem(row, "DESCRIPTION", decription);
            //记录是否有修改
            saveFlg = true;
        }
        //如果有修改则修改固定数据
        if (saveFlg)
            setTableData(row);
        return true;
    }

    /**
     * 修固定数据
     * @param row int
     */
    public void setTableData(int row) {
        table.setItem(row, "OPT_USER", Operator.getID());
        table.setItem(row, "OPT_DATE", SystemTool.getInstance().getDate());
        table.setItem(row, "OPT_TERM", Operator.getIP());
    }

    /**
     *  检核名称
     * @param useDept String
     * @return boolean
     */
    public boolean checkItemDesc(String useDept) {
        if (useDept == null || useDept.length() == 0) {
            messageBox("记账科室不能为空");
            return false;
        }
        return true;
    }

    //zhangyong20100129 添加打印功能
    public void onPrint() {
        //zhangyong20110217 修改打印信息
        int selectRow = table.getSelectedRow();
        if (selectRow < 0) {
            this.messageBox("请选择打印信息");
            return;
        }
        TParm parm = new TParm();
        parm.setData("HOSP_AREA", "TEXT", "消毒供应中心消毒记账单");
        parm.setData("SUP_DETAIL_NO", "TEXT", "记账单号： " + getValueString("SUP_DETAIL_NO"));
        TTextFormat use_dept = (TTextFormat)this.getComponent("USE_DEPT");
        parm.setData("USE_DEPT", "TEXT", "消毒科室： "+ use_dept.getText());
        parm.setData("SUP_DATE", "TEXT", "记账时间： " +
                     table.getDataStore().getItemData(selectRow, "SUP_DATE").
                     toString().substring(0, 19).replace("-", "/"));
        TComboBox operator = (TComboBox)this.getComponent("CASHIER_CODE");
        parm.setData("USER", "TEXT", "操作人： " + operator.getSelectedName());

        String sql =
                " SELECT A.SUP_DETAIL_SEQ, B.SUPITEM_DESC, B.DESCRIPTION AS SPECIFICATION, "
                + " A.COST_PRICE / A.QTY AS OWN_PRICE, A.QTY, '个' AS UNIT_DESC, "
                +
                " A.ADD_PRICE/ A.QTY AS ADD_PRICE , A.COST_PRICE  AS AR_AMT, A.DESCRIPTION "
                + " FROM INV_SUPTITEMDETAIL A, INV_SUPTITEM B "
                + " WHERE A.SUPITEM_CODE = B.SUPITEM_CODE AND A.SUP_DETAIL_NO = '" +
                getValueString("SUP_DETAIL_NO") + "' ";

        TParm parmData = new TParm(TJDODBTool.getInstance().select(sql));
        //System.out.println("sql---"+sql);
        double sum_ar_amt = 0;
        for (int i = 0; i < parmData.getCount("SUP_DETAIL_SEQ"); i++) {
            sum_ar_amt += parmData.getDouble("AR_AMT", i);
        }
        parmData.addData("SUP_DETAIL_SEQ", "合计:");
        parmData.addData("SUPITEM_DESC", "");
        parmData.addData("SPECIFICATION", "");
        parmData.addData("OWN_PRICE", "");
        parmData.addData("QTY", "");
        parmData.addData("UNIT_DESC", "");
        parmData.addData("ADD_PRICE", "");
        parmData.addData("AR_AMT", sum_ar_amt);
        parmData.addData("DESCRIPTION", "");

        parmData.setCount(parmData.getCount("SUP_DETAIL_SEQ"));
//        parmData.addData("SYSTEM", "COLUMNS", "SUP_DETAIL_SEQ");
//        parmData.addData("SYSTEM", "COLUMNS", "SUPITEM_DESC");
//        parmData.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//        parmData.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
//        parmData.addData("SYSTEM", "COLUMNS", "QTY");
//        parmData.addData("SYSTEM", "COLUMNS", "UNIT_DESC");
//        parmData.addData("SYSTEM", "COLUMNS", "ADD_PRICE");
//        parmData.addData("SYSTEM", "COLUMNS", "AR_AMT");
//        parmData.addData("SYSTEM", "COLUMNS", "DESCRIPTION");

        parm.setData("TABLE", parmData.getData());
        //parm.setData("T1", "SQL", sql);
        //System.out.println("parm===" + parm);
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVSuptitemDetall.jhw", parm);
    }

}
