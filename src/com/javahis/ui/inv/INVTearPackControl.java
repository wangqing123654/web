package com.javahis.ui.inv;

import com.dongyang.ui.event.TPopupMenuEvent;
import java.util.Map;
import com.dongyang.ui.TTable;
import java.util.HashMap;
import com.dongyang.data.TParm;
import jdo.inv.INV;
import com.dongyang.control.TControl;
import jdo.inv.INVSQL;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import jdo.inv.INVOrgTool;
import com.dongyang.jdo.TDS;
import jdo.inv.INVPackStockDTool;
import jdo.inv.INVPublicTool;
import jdo.sys.Operator;

import com.dongyang.jdo.TJDODBTool;
import com.javahis.manager.INVPackOberver;

/**
 *
 * <p>Title:手术包拆包 </p>
 *
 * <p>Description: 手术包拆包</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 2009--5-26
 * @version 1.0
 */
public class INVTearPackControl
    extends TControl {
    /**
     * 主表
     */
    private TTable tableM;
    /**
     * 细表
     */
    private TTable tableD;
    /**
     * 计算库存的方法
     */
    private INV inv = new INV();
    /**
     * 记录手术包返回属性
     */
    private TParm returnParm;
    /**
     * 记录物资来自序号管理
     */
    private Map map = new HashMap();
    /**
     * 是否新增注记
     */
    private boolean isNew = false;
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        //物资弹出窗口
        callFunction("UI|PACK_CODE|setPopupMenuParameter", "PACKCODE",
                     "%ROOT%\\config\\inv\\INVChoose.x");
        //接受回传值
        callFunction("UI|PACK_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        //table不可编辑
        tableM = (TTable) getComponent("TABLEM");
        tableD = (TTable) getComponent("TABLED");
        //初始化table
        initTable();
        //初始化界面上的数据
        initValue();

        initCombox();
        //添加观察者
        observer();
    }

    /**
     * 添加观察者
     */
    public void observer() {
        //给主表添加观察者
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL("SELECT INV_CODE,INV_CHN_DESC,DESCRIPTION FROM INV_BASE");
        dataStore.retrieve();
        //明细表添加观察者
        TDS tds = (TDS) tableD.getDataStore();
        tds.addObserver(new INVPackOberver());

        //给主表添加观察者
        dataStore = new TDataStore();
        dataStore.setSQL("SELECT PACK_CODE,PACK_DESC FROM INV_PACKM");
        dataStore.retrieve();
        //明细表添加观察者
        tds = (TDS) tableM.getDataStore();
        tds.addObserver(new INVPackTob(dataStore));

    }

    /**
     * 物资科室
     */
    public void initCombox() {
        TParm parm = INVOrgTool.getInstance().getDept();
        TComboBox comboBox = (TComboBox)this.getComponent("ORG_CODE");
        comboBox.setParmValue(parm);
        comboBox.updateUI();
        //默认入库科室
        String toOrgCode = "";
        TParm toOrgParm = INVPublicTool.getInstance().getOrgCode("B");
        if (toOrgParm.getErrCode() >= 0) {
            toOrgCode = toOrgParm.getValue("ORG_CODE", 0);
            comboBox.setValue(toOrgCode);
        }

        TParm parmUser = INVPublicTool.getInstance().getInvOperator();
        comboBox = new TComboBox();
       comboBox.setParmMap("id:USER_ID;name:USER_NAME;py1:PY1");
       comboBox.setParmValue(parmUser);
       comboBox.setShowID(true);
       comboBox.setShowName(true);
       comboBox.setExpandWidth(30);
       comboBox.setTableShowList("name");
       tableM.addItem("OPERATOR", comboBox);
       tableD.addItem("OPERATOR", comboBox);
    }

    /**
     * 初始化table
     */
    public void initTable() {
        //主表
        retriveTable(tableM, INVSQL.getInitPackStockMSql());
        //细表
        retriveTable(tableD, INVSQL.getInitPackStockDSql());
    }

    /**
     * 刷新table
     * @param table TTable
     * @param sql String
     */
    public void retriveTable(TTable table, String sql) {
        table.setSQL(sql);
        table.retrieve();
        table.setDSValue();
    }

    /**
     * 初始化界面上的数据
     */
    public void initValue() {
        //状态
        setValue("STATUS", "1");
    }

    /**
     * 查询PACK_CODE
     */
    public void onQuery() {
        //手术包
        String sql = INVSQL.getQueryStockMSql(getValueString("PACK_CODE"),
                                              getValueInt("PACK_SEQ_NO"));
        //刷新table
        retrieveTable(tableM, sql);
    }

    /**
     * 取得界面上输入的查询条件
     * @return TParm
     */
    public TParm getQueryParm() {
        return this.getParmForTag("PACK_CODE;PACK_SEQ_NO;QTY;" +
                                  "DISINFECTION_DATE;VALUE_DATE;DISINFECTION_USER;STATUS");
    }

    /**
     * 主表的点击事件
     */
    public void onTableMClicked() {
        int row = tableM.getSelectedRow();
        if (row < 0)
            return;
        //数据上翻
        setTextValue(tableM, row);
        //查找明细
        String sql = INVSQL.getQueryStockDSql(getValueString("PACK_CODE"),
                                              getValueInt("PACK_SEQ_NO"));
        retrieveTable(tableD, sql);


    }

    /**
     * 数据上翻
     * @param table TTable
     * @param row int
     */
    public void setTextValue(TTable table, int row) {
        setValueForParm("PACK_CODE;PACK_SEQ_NO;QTY;" +
                        "DISINFECTION_DATE;VALUE_DATE;DISINFECTION_USER;STATUS",
                        table.getDataStore().getRowParm(row));
    }

    /**
     * 双击查看手术包明细
     */
    public void onTableMDoubleClick() {
        //新增检核数据变更
        if (!this.checkValueChange())
            return;
        String sql = INVSQL.getQueryStockDSql(getValueString("PACK_CODE"),
                                              getValueInt("PACK_SEQ_NO"));
        retrieveTable(tableD, sql);
    }

    /**
     * 刷新table
     * @param table TTable
     * @param sql String
     */
    public void retrieveTable(TTable table, String sql) {
        table.setSQL(sql);
        table.retrieve();
        table.setDSValue();
    }

    /**
     * 手术包选择返回数据处理
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        if (obj == null)
            return;
        returnParm = (TParm) obj;
        //调用处理返回数据的方法
        onDealRetrunValue(returnParm);
        //查询
        onQuery();
        //测试用
        int rowCount = tableM.getRowCount();
        if (rowCount == 0) {
            messageBox("手术包不存在!");
            return;
        }
        //数据上翻
        setTextValue(tableM, 0);
    }

    /**
     * 处理手术包选择返回数据
     * @param parm TParm
     */
    public void onDealRetrunValue(TParm parm) {
        //包号
        setValue("PACK_CODE", parm.getValue("PACK_CODE"));
        //包名
        setValue("PACK_DESC", parm.getValue("PACK_DESC"));
        this.callFunction("UI|PACK_CODE|setEnabled", false);
    }

    /**
     * 扫描条码
     */
    public void onScream() {
        String packCode = getValueString("SCREAM");
        if (packCode == null || packCode.length() == 0) {
            return;
        }
        setValue("PACK_CODE", packCode.substring(0, packCode.length() - 4));
        setValue("PACK_SEQ_NO",
                 packCode.substring( (packCode.length() - 4), packCode.length()));
        //查找分隔符分开的字符串
        this.onQuery();
    }


    /**
     * 清空
     */
    public void onClear() {
        //检核数据变更
        if (!checkValueChange())
            return;
        //请空属性
        onClearText();
        //清空部分属性
        clearNoEnoughInv();
        //可编辑状态
        clearEnable(true);
        //初始化界面上的数据
        initValue();
    }

    /**
     * 清空没有选择足够的物资
     */
    public void clearNoEnoughInv() {
        //清空主表
        onClearTable(tableM);
        //清空细表
        onClearTable(tableD);
        //清空新增状态
        isNew = false;
    }

    /**
     * 改变编辑状态
     * @param statuse boolean
     */
    public void clearEnable(boolean statuse) {
        //科室
        this.callFunction("UI|ORG_CODE|setEnabled", statuse);
        //包号
        this.callFunction("UI|PACK_CODE|setEnabled", statuse);
        //序号
        this.callFunction("UI|PACK_SEQ_NO|setEnabled", statuse);
    }

    /**
     * 清空界面
     */
    public void onClearText() {
        //清空过滤条件
        clearValue("PACK_CODE;PACK_DESC;PACK_SEQ_NO;QTY;" +
                   "DISINFECTION_DATE;VALUE_DATE;DISINFECTION_USER");
    }

    /**
     * 清空table
     * @param table TTable
     */
    public void onClearTable(TTable table) {
        //接收最后此更改
        table.acceptText();
        //清空选中行
        table.clearSelection();
        //删除所有行
        table.removeRowAll();
        //清空修改记录
        table.resetModify();
    }

    /**
     * 保存入口
     * @return boolean
     */
    public boolean onUpdate() {
        String orgCode = this.getValueString("ORG_CODE");
        if (orgCode == null || orgCode.length() == 0) {
            messageBox_("请选择科室!");
            return false;
        }
        int row = tableM.getSelectedRow();
        if (row < 0) {
            messageBox_("请选择要拆的手术包!");
            return false;
        }
        TParm saveParm = getSaveParm(row);
        System.out.println("saveParm="+saveParm);
        if (saveParm == null)
            return false;
        //先打印出库单
        onPrint();
        //调用保存事务
        TParm result = TIOM_AppServer.executeAction("action.inv.INVAction",
            "saveTearPack", saveParm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            //保存失败
            messageBox_("保存失败!");
            return false;
        }
        //保存成功
        messageBox_("保存成功!");
        //保存后的数据清空
        resertSave(row);
        return true;
    }

    /**
     * 处理保存后的数据清理
     * @param row int
     */
    public void resertSave(int row) {
        tableM.removeRow(row);
        //清空主表保存记录
        tableM.resetModify();
        //清空细表保存记录
        tableD.removeRowAll();
        tableD.resetModify();
    }


    /**
     * 获得保存parm
     * @param row int
     * @return TParm
     */
    public TParm getSaveParm(int row) {
        //包序号
        int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
        if (packSeqNo == 0) {
            messageBox("此类手术包不可拆包!");
            return null;
        }
        String status = tableM.getItemString(row, "STATUS");
        if (status.equals("1")) {
            messageBox("手术包不在库!");
            return null;
        }
        //包号
        String packCode = tableM.getItemString(row, "PACK_CODE");
        TParm result = INVPackStockDTool.getInstance().getPackDetial(packCode,
            packSeqNo);
        if (result.getErrCode() < 0) {
            messageBox("查找明细失败!");
            return null;
        }
        //要保存的数据
        TParm saveParm = new TParm();
        //序号管理表
        saveParm.setData("STOCKDD", result.getData());
        //库存主档
        TParm stockMParm = this.getStockMParm(result);
        if (stockMParm == null)
            return null;
        saveParm.setData("STOCKM", stockMParm.getData());
        //库存明细档
        TParm stockDParm = this.getStockDParm(result);
        if (stockDParm == null)
            return null;
        saveParm.setData("STOCKD", stockDParm.getData());
        //手术包库存档
        TParm packStock = new TParm();
        packStock.setData("PACK_CODE", packCode);
        packStock.setData("PACK_SEQ_NO", packSeqNo);
        saveParm.setData("PACKSTOCK", packStock.getData());

        return saveParm;

    }

    /**
     * 得到更新库存主档的数据包
     * @param parm TParm
     * @return TParm
     */
    public TParm getStockMParm(TParm parm) {
        TParm stockMparm = new TParm();
        int rowCount = parm.getCount();
        Map invMap = new HashMap();

        TParm oneRow;
        for (int i = 0; i < rowCount; i++) {
            oneRow = parm.getRow(i);
            //物资代码
            String invCode = oneRow.getValue("INV_CODE");
            //如果为空
            if (invMap.get(invCode) == null ||
                invMap.get(invCode).toString().length() == 0) {
                int row = stockMparm.insertRow();
                //记录存储的物资代码
                invMap.put(invCode, row + "");
                //科室
                oneRow.setData("ORG_CODE", getValue("ORG_CODE"));
                //库存量
                oneRow.setData("STOCK_QTY", oneRow.getData("QTY"));
                stockMparm.setRowData(row, oneRow);
                stockMparm.setCount(row + 1);
            } else {
                int row =  Integer.valueOf (invMap.get(invCode).toString());
                //加总数量
                stockMparm.setData("STOCK_QTY", row,(stockMparm.getDouble("STOCK_QTY", row) +
                                   oneRow.getDouble("QTY")));
            }
        }
        return stockMparm;
    }

    /**
     * 得到更新库存主档的数据包
     * @param parm TParm
     * @return TParm
     */
    public TParm getStockDParm(TParm parm) {
        TParm stockDparm = new TParm();
        int rowCount = parm.getCount();
        Map invMap = new HashMap();
        TParm oneRow;
        for (int i = 0; i < rowCount; i++) {
            oneRow = parm.getRow(i);
            String invCode = oneRow.getValue("INV_CODE");
            int bacchSeq = oneRow.getInt("BATCH_SEQ");
            //批次序号和物资代码共同确定
            if (invMap.get(invCode + "|" + bacchSeq) == null) {
                int row = stockDparm.insertRow();
                //记录存储的物资代码
                invMap.put(invCode + "|" + bacchSeq, row);
                //科室
                oneRow.setData("ORG_CODE", getValue("ORG_CODE"));
                //库存量
                oneRow.setData("STOCK_QTY", oneRow.getData("QTY"));
                stockDparm.setRowData(row, oneRow);
                stockDparm.setCount(row + 1);
            }
            else {
                int row = Integer.parseInt(invMap.get(invCode + "|" + bacchSeq).
                                           toString());
                //加总数量
                stockDparm.setData("STOCK_QTY", row,
                                   stockDparm.getDouble("STOCK_QTY", row) +
                                   oneRow.getDouble("QTY"));
            }
        }
        return stockDparm;
    }

    /**
     * 检核手术包是否有序号管理
     * @return boolean
     */
    public boolean onCheckSeqFlg() {
        String seqManFlg = returnParm.getValue("SEQ_FLG");
        if (seqManFlg == null || seqManFlg.length() == 0 ||
            seqManFlg.equals("0"))
            return false;
        return true;
    }


    /**
     * 检核数据变更提示信息（变更主细表时提示数据的变更）
     * @return boolean
     */
    public boolean checkValueChange() {
        // 如果有数据变更
        if (checkData())
            switch (this.messageBox("提示信息",
                                    "数据变更是否保存", this.YES_NO_CANCEL_OPTION)) {
                //保存
                case 0:
                    if (!onUpdate())
                        return false;
                    return true;
                    //不保存
                case 1:
                    return true;
                    //撤销
                case 2:
                    return false;
            }
        return true;
    }

    /**
     * 检核数据变更
     * @return boolean
     */
    public boolean checkData() {
        //主表
        if (tableM.isModified())
            return true;
        //细表
        if (tableD.isModified())
            return true;
        return false;
    }

    /**
     * 是否关闭窗口(当有数据变更时提示是否保存)
     * @return boolean true 关闭 false 不关闭
     */
    public boolean onClosing() {
        // 如果有数据变更
        if (checkData())
            switch (this.messageBox("提示信息",
                                    "是否保存", this.YES_NO_CANCEL_OPTION)) {
                //保存
                case 0:
                    if (!onUpdate())
                        return false;
                    break;
                    //不保存
                case 1:
                    return true;
                    //撤销
                case 2:
                    return false;
            }
        //没有变更的数据
        return true;
    }
    /**
     * 打印拆包入库单
     */
    public void onPrint() {
        int row =tableM.getSelectedRow();
        if(row<0){
            messageBox_("请选择手术包");
            return;
        }

        String packCode = tableM.getItemString(row, "PACK_CODE");
        int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
        //科室
        TComboBox value;
        value = (TComboBox)this.getComponent("ORG_CODE");
        String orgDesc = value.getSelectedName();
        TParm parm = new TParm();
        parm.setData("PACK_CODE", packCode);
        parm.setData("PACK_SEQ_NO", packSeqNo);
        parm.setData("ORG_DESC", orgDesc);
        parm.setData("HOSP_AREA",getHospArea());
        String sql = INVSQL.getTearPackPrintSql(packCode, packSeqNo);
        parm.setData("T1", "SQL", sql);
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVTearPack.jhw", parm);
    }
    /**
     * 得到医院简称
     * @return String
     */
    public String getHospArea(){
        String hospChnAbn=Operator.getHospitalCHNShortName();
        return hospChnAbn;
    }

}
