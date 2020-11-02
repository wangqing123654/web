package com.javahis.ui.erd;

import com.dongyang.control.TControl;
import com.javahis.util.DateUtil;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import jdo.erd.ErdForBedAndRecordTool;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;
import java.util.Vector;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.data.TNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import com.javahis.util.StringUtil;
import jdo.sys.SystemTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Description: 住院留观护士站主程序</p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author ZangJH 2009-9-10
 *
 * @version 1.0
 */

public class ERDOrderExecMainControl
    extends TControl {

    /**
     * 界面上的cUI对象
     */
    private TTextFormat from_Date;
    private TTextFormat to_Date;

    private TTextField from_Time;
    private TTextField to_Time;

    //医嘱种类
    private TRadioButton ord2All;
    private TRadioButton ord2PHA;
    private TRadioButton ord2PL;

    //审核状态
    private TRadioButton checkAll;
    private TRadioButton checkYES;
    private TRadioButton checkNO;

    //药嘱种类
    TCheckBox typeO;
    TCheckBox typeE;
    TCheckBox typeI;
    TCheckBox typeF;


    //主table
    private TTable mainTable;
    //全部执行
    private TCheckBox exeAll;

    private String caseNo = "";
    private String mrNo="";
    private String patName="";

    public ERDOrderExecMainControl() {
    }

    /**
     * 初始化函数
     */
    public void onInit() {
        super.onInit();
        //得到外部的参数
        initParmFromOutside();
        //本界面的初始化
        myInitControler();
        //执行查询
        onQuery();
        //设置医嘱执行单类型默认值
        setValue("EXESHEET_TYPE","O");
    }

    /**
     * 查询函数
     * 1)查询
     * 2)当有数据的时候改变相关的控件状态
     */
    public void onQuery() {
        //初始化当前table并且有数据
        if (initTable())
            changeStatus();
    }

    /**
     * 保存动作
     */
    public boolean onSave() {

        //立刻接受值的改变
        mainTable.acceptText();
        boolean existOption=false;
        if(checkAll.isSelected()){
            this.messageBox("全部状态下\n不可保存！");
            return false;
        }
        //检查是否有选中的数据
        for (int i = 0; i < mainTable.getRowCount(); i++) {
            boolean selFlg=TypeTool.getBoolean(mainTable.getValueAt(i, 12));
            if ((checkNO.isSelected()&&selFlg)||(checkYES.isSelected()&&!selFlg)) {
                existOption=true;
                break;
            }
        }
        //如果没有存在选择的数据
        if(!existOption){
            this.messageBox("没选中保存数据！");
            return false;
        }

        //密码判断
        if (!checkPW()) {
            return false;
        }

        //调用保存
        if (checkNO.isSelected()) {
            if (!onExec()) {
                this.messageBox("E0001");
                onQuery();
                return false;
            }
        } //如果审核被选择（说明保存时是--取消审核），需要验证是否有执行的
        else {
            if (!onUndoExec()) {
                this.messageBox("E0001");
                onQuery();
                return false;
            }

        }
        onQuery();
        this.messageBox("P0001");
        //保存后再执行一边查询
//        onQuery();
        return true;
    }

    /**
     * 取消执行主方法
     * @return boolean
     */
    public boolean onUndoExec() {
        //拿到所有挑勾--展开人的caseNo
        TParm execData = new TParm();
        TParm tablValue = mainTable.getParmValue();
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        int rowCount = mainTable.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String caseNo = (String) tablValue.getData("CASE_NO", i);
            String orderNo = (String) tablValue.getData("RX_NO", i);
            String orderSeq = tablValue.getData("SEQ_NO", i) + "";
            if (!TypeTool.getBoolean(mainTable.getValueAt(i, 12))) {
                execData.addData("CASE_NO", caseNo);
                execData.addData("RX_NO", orderNo);
                execData.addData("SEQ_NO", orderSeq);
                execData.addData("OPT_USER", Operator.getID());
                execData.addData("OPT_DATE", now);
                execData.addData("OPT_TERM", Operator.getIP());

                //处理集合医嘱
                String setMainFlg = tablValue.getData("SETMAIN_FLG", i) + "";
                String orderSetGroupNo = tablValue.getData("ORDERSET_GROUP_NO",
                    i) + "";
                //假参以便使用getSetOrder函数
                Timestamp falseParm = new Timestamp(0);
                if ("Y".equals(setMainFlg)) {
                    execData = getSetOrder(execData, caseNo, orderNo,
                                           orderSetGroupNo, now, falseParm);
                }
                //护士备注
                execData.addData("NS_NOTE", new TNull(String.class));
            }
        }
        //拿到数量
        int count = ((Vector) execData.getData("OPT_USER")).size();
        execData.setCount(count);

        //调用action执行事务
        TParm result = TIOM_AppServer.executeAction(
            "action.erd.ERDOrderExecAction",
            "onUndoSave", execData);
        if (result.getErrCode() < 0) {
            this.messageBox_(result);
            return false;
        }

        return true;

    }


    /**
     * 执行主方法
     * @return boolean
     */
    public boolean onExec() {
        //拿到所有挑勾--展开人的caseNo
        TParm execData = new TParm();
        TParm tablValue = mainTable.getParmValue();
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        int rowCount = mainTable.getRowCount();
        //主表的数据
        for (int i = 0; i < rowCount; i++) {
            String caseNo = (String) tablValue.getData("CASE_NO", i);
            String orderNo = (String) tablValue.getData("RX_NO", i);
            String orderSeq = tablValue.getData("SEQ_NO", i) + "";

            //判断是否写执行/DC的人员
            String dcFlg = (String) tablValue.getData("DC_DR_CODE", i);
            if (TypeTool.getBoolean(mainTable.getValueAt(i, 12))) {
                execData.addData("CASE_NO", caseNo);
                execData.addData("RX_NO", orderNo);
                execData.addData("SEQ_NO", orderSeq);
                //该医嘱为DC医嘱
                if (dcFlg != null) {
                    execData.addData("DC_ORDER", true);
                }
                else {
                    execData.addData("DC_ORDER", false);
                }
                execData.addData("OPT_USER", Operator.getID());
                execData.addData("OPT_DATE", now);
                execData.addData("OPT_TERM", Operator.getIP());

                String execDate = (String) mainTable.getValueAt(i, 14);
                String execTime = (String) mainTable.getValueAt(i, 15);
                Timestamp checkDateTime = StringTool.getTimestamp(execDate +
                    execTime, "yyyy/MM/ddHH:mm:ss");


                //允许人工修改的‘护士备注’
                execData.addData("NS_EXEC_DATE", checkDateTime);

                //处理集合医嘱
                String setMainFlg = tablValue.getData("SETMAIN_FLG", i) + "";
                String orderSetGroupNo = tablValue.getData("ORDERSET_GROUP_NO",
                    i) + "";
                if ("Y".equals(setMainFlg)) {
                    execData = getSetOrder(execData, caseNo, orderNo,
                                           orderSetGroupNo, now, checkDateTime);
                }

                //护士备注
                String execNote = (String) mainTable.getValueAt(i, 16);
                execData.addData("NS_NOTE", execNote==null?new TNull(String.class):execNote);

            }
        }

        //拿到数量
        int count = ( (Vector) execData.getData("OPT_USER")).size();
        execData.setCount(count);

        //调用action执行事务
        TParm result = TIOM_AppServer.executeAction(
            "action.erd.ERDOrderExecAction",
            "onSave", execData);
        if (result.getErrCode() < 0) {
            this.messageBox_(result);
            return false;
        }

        return true;
    }

    /**
     * 得到集合医嘱细项，后台保存用
     * @param parm TParm
     * @param caseNo String
     * @param orderNo String
     * @param orderSetGroupNo String
     * @param now Timestamp
     * @return TParm
     */
    private TParm getSetOrder(TParm parm, String caseNo, String orderNo,
                              String orderSetGroupNo, Timestamp now,
                              Timestamp execDate) {
        String SelSql = "SELECT * FROM OPD_ORDER WHERE CASE_NO='" +
            caseNo + "' AND RX_NO='" + orderNo +
            "' AND ORDERSET_GROUP_NO=" + Integer.parseInt(orderSetGroupNo);
        TParm result = new TParm(TJDODBTool.getInstance().select(SelSql));
        int count = result.getCount();
        for (int i = 0; i < count; i++) {
            String setMainFlg = result.getData("SETMAIN_FLG", i) + "";
            if (!"Y".equals(setMainFlg)) { //因为主项已经在上面压入，所以可以不重复包含
                String dcFlg = (String) result.getData("DC_DR_CODE", i) + "";
                parm.addData("CASE_NO", caseNo);
                parm.addData("RX_NO", orderNo);
                parm.addData("SEQ_NO", result.getData("SEQ_NO", i));
                //该医嘱为DC医嘱
                if (dcFlg != null && dcFlg.length() != 0) {
                    parm.addData("DC_ORDER", true);
                }
                else {
                    parm.addData("DC_ORDER", false);
                }
                parm.addData("OPT_USER", Operator.getID());
                parm.addData("OPT_DATE", now);
                parm.addData("OPT_TERM", Operator.getIP());

                //允许人工修改
                parm.addData("NS_EXEC_DATE", execDate);
            }
        }
        return parm;
    }


    /**
     * 调用密码验证
     * @return boolean
     */
    public boolean checkPW() {
    	 String erdExe = "erdExe";
        String value = (String)this.openDialog(
            "%ROOT%\\config\\inw\\passWordCheck.x",erdExe);
        if (value == null) {
            return false;
        }
        return value.equals("OK");
    }

    /**
     * 通过剂型过滤药品
     * @param parm TParm
     */
    private void filterDrugByDoseCode(TParm parm){
        if(!ord2PHA.isSelected())
            return;
        if (parm.getCount() <= 0)
            return;
        for(int i = parm.getCount() - 1;i >= 0;i--){
            if(!parm.getValue("ORDER_CAT1_CODE",i).startsWith("PHA"))
                continue;
            if (!typeO.isSelected() && "O".equals(parm.getValue("CLASSIFY_TYPE",i)))
                parm.removeRow(i);
            if (!typeE.isSelected() && "E".equals(parm.getValue("CLASSIFY_TYPE",i)))
                parm.removeRow(i);
            if (!typeI.isSelected() && "I".equals(parm.getValue("CLASSIFY_TYPE",i)))
                parm.removeRow(i);
            if (!typeF.isSelected() && "F".equals(parm.getValue("CLASSIFY_TYPE",i)))
                parm.removeRow(i);
        }
        parm.setCount(parm.getCount("ORDER_CAT1_CODE"));
    }

    /**
     * 初始化Table
     * @return boolean
     */
    public boolean initTable() {

        TParm selParm = new TParm();
        selParm = getQueryParm();
        TParm query = ErdForBedAndRecordTool.getInstance().selOrderExec(selParm);
        filterDrugByDoseCode(query);
        if (query.getCount() <= 0) {
            mainTable.setParmValue(query);
            this.messageBox("没有相关数据！");
            return false;
        }
        for (int i = 0; i < query.getCount(); i++) {
            //和已执行标记一致
            query.addData("EXE_FLG", checkYES.isSelected());
            Timestamp exeDate = (Timestamp) query.getData("NS_EXEC_DATE", i);
            if (exeDate != null) {
                String day = StringTool.getString(exeDate, "yyyy/MM/dd");
                String time = StringTool.getString(exeDate, "HH:mm:ss");
                query.addData("NS_EXEC_DATE_DAY", day);
                query.addData("NS_EXEC_DATE_TIME", time);
            }
        }

        mainTable.setParmValue(query);

        return true;
    }

    /**
     * table上的checkBox注册监听
     * @param obj Object
     */
    public void onTableCheckBoxChangeValue(Object obj) {
        //当全部执行的时候设置一次时间
        Timestamp chackTime = TJDODBTool.getInstance().getDBTime();
        //获得点击的table对象
        TTable table = (TTable) obj;
        //只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
        table.acceptText();
        TParm tblParm = table.getParmValue();

        //获得选中的列/行
        int col = table.getSelectedColumn();
        int row = table.getSelectedRow();
        //获得table上的行数
        int rowcount = table.getRowCount();
        //如果选中的是第12列就激发执行动作--执行
        String columnName = table.getParmMap(col);
        if (columnName.equals("EXE_FLG")) {
            boolean exeFlg;
            //获得点击时的值
            exeFlg = TypeTool.getBoolean(table.getValueAt(row, col));
            //勾选时
            if (exeFlg) {
                //勾选行动作
                selection(row, Operator.getName(), chackTime);
                //得到选中数据的医嘱类型（由于现在是不同医嘱类型各自计算连接号，所以为了避免在“全部”的状况下会出现重复连接号情况）
                String rxKind = (String) tblParm.getValue("RX_KIND", row);
                //-----------------------处理连结医嘱start----------------------------
                //找到相同的连接号
                String linkNo = (String) table.getValueAt(row, 4);
                if (TypeTool.getBoolean(linkNo)) {
                    for (int i = 0; i < rowcount; i++) {
                        //除了当前点击的行号以外的
                        if (i != row &&
                            linkNo.equals( (String) table.getValueAt(i, 4)) &&
                            rxKind.equals( (String) tblParm.getValue("RX_KIND",
                            i))) {

                            selection(i, Operator.getName(), chackTime);
                        }
                    }
                }
            }
            else { //取消时
                //勾选行动作
                unselection(row);
                //得到选中数据的医嘱类型（由于现在是不同医嘱类型各自计算连接号，所以为了避免在“全部”的状况下会出现重复连接号情况）
                String rxKind = (String) tblParm.getValue("RX_KIND", row);
                //-----------------------处理连结医嘱start----------------------------
                //找到相同的连接号
                String linkNo = (String) table.getValueAt(row, 4);
                if (TypeTool.getBoolean(linkNo)) {
                    for (int i = 0; i < rowcount; i++) {
                        //除了当前点击的行号以外的
                        if (i != row &&
                            linkNo.equals( (String) table.getValueAt(i, 4)) &&
                            rxKind.equals( (String) tblParm.getValue("RX_KIND",
                            i))) {

                            unselection(i);
                        }
                    }
                }
            }
            //-----------------------end----------------------------------------
        }
    }

    /**
     * 选择
     * @param i int
     * @param nowFlag boolean
     * @param optName String
     * @param chackTime Timestamp
     */
    private void selection(int i, String optName,
                           Timestamp chackTime) {
        mainTable.setValueAt(true, i, 12);
        mainTable.setValueAt(optName, i, 13);
        mainTable.setValueAt(StringTool.getString(chackTime,
                                                  "yyyy/MM/dd"), i, 14);
        mainTable.setValueAt(StringTool.getString(chackTime, "HH:mm:ss"),
                             i, 15);

        //DC时间，检验是否有DC
        Timestamp dcDate = (Timestamp) mainTable.getValueAt(i, 16);

        if (dcDate != null && !"".equals(dcDate)) {
            mainTable.setValueAt(optName, i, 18);
            mainTable.setValueAt(StringTool.getString(chackTime,
                "yyyy/MM/dd"), i, 19);
            mainTable.setValueAt(StringTool.getString(chackTime,
                "HH:mm:ss"),
                                 i, 20);
        }

    }

    /**
     * 取消选择
     * @param i int
     * @param nowFlag boolean
     * @param optName String
     * @param chackTime Timestamp
     */
    private void unselection(int i) {
        mainTable.setValueAt(false, i, 12);
        mainTable.setValueAt("", i, 13);
        mainTable.setValueAt("", i, 14);
        mainTable.setValueAt("", i, 15);
        //DC时间，检验是否有DC
        Timestamp dcDate = (Timestamp) mainTable.getValueAt(i, 16);
        if (dcDate != null && !"".equals(dcDate)) {
            mainTable.setValueAt("", i, 18);
            mainTable.setValueAt("", i, 19);
            mainTable.setValueAt("", i, 20);

        }

    }


    /**
     * 获得界面上的所有查询参数
     * @return TParm
     */
    public TParm getQueryParm() {
        //获得界面上的参数
        TParm result = new TParm();

        //医嘱种类
        if (ord2All.isSelected()) {
            //所有
        }
        else if (ord2PHA.isSelected()) {
            //药嘱
            result.setData("CAT1_TYPEPHA", "Y");
        }
        else if (ord2PL.isSelected()) {
            //处置(检验检查)
            result.setData("CAT1_TYPEPL", "Y");
        }

        //医嘱种类
        /*if (typeO.isSelected()) {
            //所有
            result.addData("DOSE_TYPEO", " DOSE_TYPE = 'O' ");
        }
        if (typeE.isSelected()) {
            //药嘱
            result.addData("DOSE_TYPEE", " DOSE_TYPE = 'E' ");
        }
        if (typeI.isSelected()) {
            //处置(检验检查)
            result.addData("DOSE_TYPEI", " DOSE_TYPE = 'I'  ");
        }
        if (typeF.isSelected()) {
            //嘱托
            result.addData("DOSE_TYPEF", " DOSE_TYPE = 'F' ");
        }*/

        //审核状态
        if (checkAll.isSelected()) {
            //所有
        }
        else if (checkYES.isSelected()) {
            //已审核
            result.setData("EXECTYPE_YES", "Y");
            String fromDate = StringTool.getString( (Timestamp) from_Date.
                getValue(), "yyyyMMdd");
            String fromTime = (String) from_Time.getValue();
            String fromCheckDate = fromDate + fromTime.substring(0, 2) +
                fromTime.substring(3);
            String toDate = StringTool.getString( (Timestamp) to_Date.
                                                 getValue(), "yyyyMMdd");
            String toTime = (String) to_Time.getValue();
            String toCheckDate = toDate + toTime.substring(0, 2) +
                toTime.substring(3);
            result.setData("fromCheckDate", fromCheckDate);
            result.setData("toCheckDate", toCheckDate);
        }
        else if (checkNO.isSelected()) {
            //未审核
            result.setData("EXECTYPE_NO", "Y");
        }

        //加如看诊号
        if (getCaseNo() != null && !"".equals(getCaseNo().trim()) &&
            !"null".equals(caseNo)) {
            result.setData("CASE_NO", getCaseNo());
        }
        else {
            //为空的时候
        }

        return result;
    }


    /**
     * 当查询有数据之后改变某些控件的状态
     */
    public void changeStatus() {
        exeAll.setEnabled(true);
    }


    /**
     * 首先得到所有UI的控件对象/注册相应的事件
     */

    public void myInitControler() {

        //得到时间控件
        from_Date = (TTextFormat)this.getComponent("from_Date");
        to_Date = (TTextFormat)this.getComponent("to_Date");
        from_Time = (TTextField)this.getComponent("from_Time");
        to_Time = (TTextField)this.getComponent("to_Time");

        //得到table控件
        mainTable = (TTable)this.getComponent("MAINTABLE");
        //给table注册CHECK_BOX_CLICKED点击监听事件
        this.callFunction("UI|MAINTABLE|addEventListener",
                          TTableEvent.CHECK_BOX_CLICKED, this,
                          "onTableCheckBoxChangeValue");

        mainTable.addEventListener(mainTable.getTag() + "->" +
                                   TTableEvent.CHANGE_VALUE, this,
                                   "onDateTime");

        ord2All = (TRadioButton)this.getComponent("ord2All");
        ord2PHA = (TRadioButton)this.getComponent("ord2PHA");
        ord2PL = (TRadioButton)this.getComponent("ord2PL");

        checkAll = (TRadioButton)this.getComponent("checkAll");
        checkYES = (TRadioButton)this.getComponent("checkYES");
        checkNO = (TRadioButton)this.getComponent("checkNO");

        typeO = (TCheckBox)this.getComponent("typeO");
        typeE = (TCheckBox)this.getComponent("typeE");
        typeI = (TCheckBox)this.getComponent("typeI");
        typeF = (TCheckBox)this.getComponent("typeF");

        //得到全全部执行控件
        exeAll = (TCheckBox)this.getComponent("exeALL");

        //初始化时间
        initDateTime();
    }

    /**
     * 清除table上的的数据行
     */
    public void onRemoveTbl() {
        mainTable.setParmValue(new TParm());
        exeAll.setSelected(false);
    }


    public void selDOSE(Object flg) {
        //清空选择
        typeO.setSelected(false);
        typeE.setSelected(false);
        typeI.setSelected(false);
        typeF.setSelected(false);
        ((TCheckBox)this.getComponent("DOSE_ALL")).setSelected(false);
        boolean temp = TypeTool.getBoolean(flg);
        //编辑状态
        typeO.setEnabled(temp);
        typeE.setEnabled(temp);
        typeI.setEnabled(temp);
        typeF.setEnabled(temp);
        ((TCheckBox)this.getComponent("DOSE_ALL")).setEnabled(temp);
        //清除table
        onRemoveTbl();
    }

    public void doseSelAll(){
        if(!((TCheckBox)getComponent("DOSE_ALL")).isSelected()){
            typeO.setSelected(false);
            typeE.setSelected(false);
            typeI.setSelected(false);
            typeF.setSelected(false);
        }
        else{
            typeO.setSelected(true);
            typeE.setSelected(true);
            typeI.setSelected(true);
            typeF.setSelected(true);
        }
    }

    //全部执行
    public void onCheck() {

        boolean nowFlag = exeAll.isSelected();
        //当全部执行的时候设置一次时间
        Timestamp chackTime = TJDODBTool.getInstance().getDBTime();
        String optName = Operator.getName();

        //得到行数
        int ordCount = mainTable.getRowCount();
        for (int i = 0; i < ordCount; i++) {
            //循环取消对勾
            if (nowFlag) {
                selection(i, optName, chackTime);
            }
            else { //取消审核
                unselection(i);
            }
        }

    }


    /**
     * 初始化时间控件
     */
    public void initDateTime() {
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //String now = StringTool.getString(date, "yyyyMMddHHmmss");
        //用今天的00：00初始化起始时间
        from_Date.setValue(date);
        from_Time.setValue("00:00");
        to_Date.setValue(date);
        to_Time.setValue("23:59");
    }

    /**
     * 初始化界面参数caseNo/mrNo（从外部病患管理界面传来的参数）
     */
    public void initParmFromOutside() {
        //从病案管理界面拿到参数TParm
        TParm outsideParm = (TParm)this.getParameter();
        if (outsideParm != null) {
            //按就诊号查询的caseNo
            setCaseNo(outsideParm.getValue("CASE_NO"));
            setMrNo(outsideParm.getValue("MR_NO"));
            setPatName(outsideParm.getValue("PAT_NAME"));
        }
    }

    /**
     * 条码打印
     */
    public void onBarCode(){
        String sql1="SELECT * FROM REG_PATADM WHERE CASE_NO = '"+this.getCaseNo()+"'";
        TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
        String mrNo=result1.getValue("MR_NO",0);
        String sql2="SELECT * FROM SYS_PATINFO WHERE MR_NO = '"+mrNo+"'";
        TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
        TParm parm = new TParm();
        //参数
        parm.setData("DEPT_CODE",result1.getValue("DEPT_CODE",0));
        parm.setData("ADM_TYPE","E");
        parm.setData("CASE_NO",this.getCaseNo());
        parm.setData("MR_NO",result1.getValue("MR_NO",0));
        parm.setData("PAT_NAME",result2.getValue("PAT_NAME",0));
        parm.setData("ADM_DATE",result1.getData("ADM_DATE",0));
        parm.setData("CLINICAREA_CODE",result1.getValue("CLINICAREA_CODE",0));
        parm.setData("CLINICROOM_NO",result1.getValue("CLINICROOM_NO",0));
        parm.setData("POPEDEM","1");
        openDialog("%ROOT%\\config\\med\\MEDApply.x",parm);
    }
    /**
     * 护士执行单
     */
//    public void onPrint(){
//        this.messageBox("执行单打印……");
//
//    }
    /**
     * 医嘱单
     */
    public void onOrderPrt(){
        TParm parm = new TParm();
        parm.setData("INW", "CASE_NO", this.getCaseNo());
        this.openDialog("%ROOT%\\config\\erd\\ERDOrderSheetPrtAndPreView.x",parm);
    }
    /**
     * 体温单
     */
    public void onTempPrt(){
        String sql=" SELECT B.CHN_DESC,A.BED_DESC FROM ERD_BED A,SYS_DICTIONARY B "+
            " WHERE B.GROUP_ID='ERD_REGION' "+
            " AND B.ID=A.ERD_REGION_CODE "+
            " AND A.CASE_NO='"+getCaseNo()+"'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        TParm sumParm=new TParm();

        sumParm.setData("SUM", "CASE_NO", getCaseNo());
        sumParm.setData("SUM", "MR_NO", getMrNo());
        sumParm.setData("SUM", "IPD_NO", "");
        sumParm.setData("SUM", "STATION_CODE", result.getData("CHN_DESC",0));
        sumParm.setData("SUM", "BED_NO", result.getData("BED_DESC",0));
        sumParm.setData("SUM", "ADM_TYPE", "E");

        this.openDialog("%ROOT%\\config\\sum\\SUMVitalSign.x",sumParm);

    }
    /**
     * 护理记录
     */
    public void onNurseRec(){
        TParm parm = new TParm();
        parm.setData("SYSTEM_TYPE", "EMG");
        parm.setData("ADM_TYPE","E");
        parm.setData("CASE_NO",getCaseNo());
        parm.setData("PAT_NAME",getPatName());
        parm.setData("MR_NO",getMrNo());
        parm.setData("IPD_NO","");
        parm.setData("ADM_DATE",getAdmDate());
        parm.setData("DEPT_CODE",Operator.getDept());
        parm.setData("RULETYPE","2");
        parm.setData("EMR_DATA_LIST",new TParm());
        this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x",parm);
    }
    private Object getAdmDate(){
        return new TParm(TJDODBTool.getInstance().select(" SELECT ADM_DATE "+
                                                         " FROM REG_PATADM "+
                                                         " WHERE CASE_NO = '"+getCaseNo()+"'")).getData("ADM_DATE",0);
    }
    /**
     * 得到执行单SQL
     * @param caseNo String
     * @return String
     */
    private String getExeSheetSQL(String caseNo){
        String doseType = "";
        if("O".equals(getValueString("EXESHEET_TYPE")))
            doseType = " AND F.CLASSIFY_TYPE = 'O'  AND CAT1_TYPE LIKE 'PHA%' ";
        else if("I".equals(getValueString("EXESHEET_TYPE")))
            doseType = " AND F.CLASSIFY_TYPE = 'I' AND CAT1_TYPE LIKE 'PHA%' ";
        else if("F".equals(getValueString("EXESHEET_TYPE")))
            doseType = " AND F.CLASSIFY_TYPE = 'F' AND CAT1_TYPE LIKE 'PHA%' ";
        else
            doseType = " AND ((F.CLASSIFY_TYPE = 'E' AND CAT1_TYPE LIKE 'PHA%') OR (CAT1_TYPE NOT LIKE  'PHA%') )";

        String SQL = " SELECT A.CASE_NO,A.MR_NO,B.BED_NO,ORDER_CODE,ORDER_DESC,"+
                     "        MEDI_QTY||C.UNIT_CHN_DESC MEDI_QTY,A.ROUTE_CODE,FREQ_CODE,DR_NOTE,DISPENSE_QTY||D.UNIT_CHN_DESC DISPENSE_QTY,"+
                     "        LINKMAIN_FLG,LINK_NO,NS_EXEC_DATE,B.ERD_REGION_CODE,E.CHN_DESC REGION_DESC"+
                     " FROM OPD_ORDER A,ERD_BED B,SYS_UNIT C,SYS_UNIT D,SYS_DICTIONARY E,SYS_PHAROUTE F "+
                     " WHERE  A.CASE_NO = '"+caseNo+"'"+
                     doseType+
                     " AND    A.CASE_NO = B.CASE_NO"+
                     " AND    MEDI_UNIT = C.UNIT_CODE"+
                     " AND    DISPENSE_UNIT = D.UNIT_CODE"+
                     //" AND    NS_EXEC_DATE IS NOT NULL"+
                     " AND    E.GROUP_ID = 'ERD_REGION'"+
                     " AND    B.ERD_REGION_CODE = E.ID"+
                     " AND    A.ROUTE_CODE = F.ROUTE_CODE(+)" +
                     " AND    ((A.ORDERSET_CODE IS NOT NULL AND A.ORDERSET_CODE = ORDER_CODE)"+
                     "       OR(A.ORDERSET_CODE IS NULL))";
        return SQL;
    }
    /**
     * 处理空值
     * @param parm TParm
     */
    private void clearNullAndCode(TParm parm){
      String names[] = parm.getNames();
      for(int i = 0;i < names.length;i++){
          for(int j = 0 ; j < parm.getCount(names[i]) ; j++){
              if(parm.getData(names[i],j) == null ||
                 parm.getValue(names[i],j).equalsIgnoreCase("null"))
                  parm.setData(names[i],j,"");
          }
      }
   }
    private TParm getExeSheetParm(){
         return new TParm(TJDODBTool.getInstance().select(getExeSheetSQL(getCaseNo())));
    }

    /**
     * 整理数据
     * @param parm TParm
     * @return TParm
     */
    private TParm arrangeData(TParm parm, String flg) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm order = parm.getRow(i);
            //判断连接医嘱
            if (ifLinkOrder(order)) {
                //如果为连接医嘱细项则不予处理
                if (ifLinkOrderSubItem(order))
                    continue;
                String finalOrder = getLinkOrder(order, parm);
                String medi=getLinkQty(order, parm);
                result.addData("ORDER_DESC", finalOrder);
                result.addData("MEDI_QTY", medi);
//                result.addData("ORDER_DESC", order.getData("ORDER_DESC"));
//                result.addData("MEDI_QTY", order.getData("MEDI_QTY"));
            }
            else { //普通医嘱
                String drNote = (String) order.getData("DR_NOTE");
                String desc = (String) order.getData("ORDER_DESC");
                //判断是否是医嘱备注
                if (ifZ00Order(order)) {
                    desc = drNote;
                    drNote = "";
                }
                String finalDesc = "" + desc;
                //主要参数--医嘱
                result.addData("ORDER_DESC", finalDesc);
                result.addData("MEDI_QTY", ""+order.getData("MEDI_QTY"));
            }
            //根据医嘱类型设置不同的数据列
            if ("O".equals(flg)) {
                result.addData("ROUTE_CODE", order.getData("ROUTE_CODE"));
                result.addData("FREQ_CODE", order.getData("FREQ_CODE"));
                result.addData("DISPENSE_QTY", order.getData("DISPENSE_QTY"));
            }
            else if ("I".equals(flg)) { //注射
                result.addData("ROUTE_CODE", order.getData("ROUTE_CODE"));
                result.addData("FREQ_CODE", order.getData("FREQ_CODE"));
            }
            else if("F".equals(flg)) { //输液
                result.addData("ROUTE_CODE", order.getData("ROUTE_CODE"));
                result.addData("FREQ_CODE", order.getData("FREQ_CODE"));
                result.addData("DR_NOTE", order.getData("DR_NOTE"));
            }else if("COM".equals(flg)){
                result.addData("CASE_NO", order.getData("CASE_NO")==null?"":order.getData("CASE_NO"));
                result.addData("MR_NO", order.getData("MR_NO")==null?"":order.getData("MR_NO"));
                result.addData("BED_NO", order.getData("BED_NO")==null?"":order.getData("BED_NO"));
                result.addData("ROUTE_CODE", order.getData("ROUTE_CODE")==null?"":order.getData("ROUTE_CODE"));
                result.addData("FREQ_CODE", order.getData("FREQ_CODE")==null?"":order.getData("FREQ_CODE"));
                result.addData("NS_EXEC_DATE", (order.getData("NS_EXEC_DATE")==null ||
                                                order.getValue("NS_EXEC_DATE").length() == 0 ||
                                                order.getValue("NS_EXEC_DATE").equalsIgnoreCase("null"))
                                                ?
                                                "":
                                                order.getData("NS_EXEC_DATE").toString().substring(0,order.getData("NS_EXEC_DATE").toString().length() - 2));
                result.addData("DR_NOTE", order.getData("DR_NOTE")==null?"":order.getData("DR_NOTE"));
            }
        }
        result.setCount(result.getCount("ORDER_DESC"));
        return result;
    }

    /**
     * 判断是否是连接医嘱
     * @return boolean
     */
    private boolean ifLinkOrder(TParm oneOrder) {
        String LinkNo = (String) oneOrder.getData("LINK_NO");
        if (LinkNo == null || LinkNo.length() == 0)
            return false;
        return true;
    }


    /**
     * 判断是否是链接医嘱子项
     * @return boolean
     */
    private boolean ifLinkOrderSubItem(TParm oneOrder) {
        return!TypeTool.getBoolean(oneOrder.getData("LINKMAIN_FLG"));

    }
    /**
     * 判断数否是医嘱备注
     * @param parm TParm
     * @return boolean
     */
    private boolean ifZ00Order(TParm parm) {
        String orderCode = (String) parm.getData("ORDER_CODE");
        return orderCode.startsWith("Z");
    }
    /**
     * 整理连接医嘱ORDER_DESC
     * @param order TParm
     * @param parm TParm
     * @return String
     */
    private String getLinkOrder(TParm order, TParm parm) {
        String resultDesc = "";
        String mainOrder = (String) order.getData("ORDER_DESC");
        String mainLinkNo = (String) order.getData("LINK_NO");
        resultDesc = mainOrder;
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            String linkNo = (String) parm.getData("LINK_NO", i);
            if (mainLinkNo.equals(linkNo) &&
                !TypeTool.getBoolean(parm.getData("LINKMAIN_FLG", i))) {
                String subOrder = (String) parm.getData("ORDER_DESC", i);
                resultDesc += "\r" + subOrder;
            }
            else
                continue;
        }
        return resultDesc;
    }
    /**
     *
     * @param order TParm
     * @param parm TParm
     * @return String
     */
    private String getLinkQty(TParm order, TParm parm) {
        String resultString = "";
        String mainMediQty = (String) order.getData("MEDI_QTY");
        String mainLinkNo = (String) order.getData("LINK_NO");
        resultString = mainMediQty;
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            String linkNo = (String) parm.getData("LINK_NO", i);
            if (mainLinkNo.equals(linkNo) &&
                !TypeTool.getBoolean(parm.getData("LINKMAIN_FLG", i))) {
                String subMediQty = (String) parm.getData("MEDI_QTY", i);
                resultString += "\r" + subMediQty;
            }
            else
                continue;
        }
        return resultString;
    }

    /**
     * 打印程序
     */
    public void onPrint() {
        if(getValueString("EXESHEET_TYPE").length() == 0){
            messageBox("请选择医嘱执行单类型");
            return;
        }
        TParm selectData = getExeSheetParm();
        clearNullAndCode(selectData);
        if(selectData.getCount() <= 0){
            messageBox("无打印数据");
            return;
        }
        TParm printParm=new TParm();
        //判断执行那种打印单
        if ("O".equals(getValueString("EXESHEET_TYPE"))) { //口服单
            printParm=arrangeData(selectData,"O");
            printParm.addData("SYSTEM", "COLUMNS","ORDER_DESC");
            printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
            printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            TParm data = new TParm();
            data.setData("NAME", "TEXT", this.getPatName());
            data.setData("STATION", "TEXT", selectData.getValue("REGION_DESC", 0));
            data.setData("FROMTODATE", "TEXT", from_Date.getText()+" "+from_Time.getText()+"～"+to_Date.getText()+" "+to_Time.getText());
            data.setData("TABLE", printParm.getData());
            this.openPrintWindow(
                "%ROOT%\\config\\prt\\erd\\ERDExecOrderPrt_O.jhw",
                data);
        }
        else if ("I".equals(getValueString("EXESHEET_TYPE"))) { //注射
            printParm=arrangeData(selectData,"I");
            printParm.addData("SYSTEM", "COLUMNS","ORDER_DESC");
            printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
            printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
            TParm data = new TParm();
            data.setData("NAME", "TEXT", this.getPatName());
            data.setData("STATION", "TEXT", selectData.getValue("REGION_DESC", 0));
            data.setData("FROMTODATE", "TEXT", from_Date.getText()+" "+from_Time.getText()+"～"+to_Date.getText()+" "+to_Time.getText());
            data.setData("TABLE", printParm.getData());

            this.openPrintWindow(
                "%ROOT%\\config\\prt\\erd\\ERDExecOrderPrt_I.jhw",
                data);
        }
        else if ("F".equals(getValueString("EXESHEET_TYPE"))) { //输液
            printParm=arrangeData(selectData,"F");
            printParm.addData("SYSTEM", "COLUMNS","ORDER_DESC");
            printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
            printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
            TParm data = new TParm();
            data.setData("NAME", "TEXT", this.getPatName());
            data.setData("STATION", "TEXT", selectData.getValue("REGION_DESC", 0));;
            //统计时间
            data.setData("FROMTODATE", "TEXT", from_Date.getText()+" "+from_Time.getText()+"～"+to_Date.getText()+" "+to_Time.getText());
            data.setData("TABLE", printParm.getData());
            this.openPrintWindow(
                "%ROOT%\\config\\prt\\erd\\ERDExecOrderPrt_F.jhw",
                data);
        }
        else { //普通执行单
            printParm=arrangeData(selectData,"COM");
            if(printParm.getCount() <= 0){
                messageBox("无打印数据");
                return;
            }
            printParm.addData("SYSTEM", "COLUMNS", "CASE_NO");
            printParm.addData("SYSTEM", "COLUMNS", "MR_NO");
            printParm.addData("SYSTEM", "COLUMNS", "BED_NO");
            printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
            printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
            printParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
            TParm data = new TParm();
            //病区
            data.setData("STATION", "TEXT", selectData.getValue("REGION_DESC", 0));
            data.setData("RX_TYPE", "TEXT", "全部");
            data.setData("PAT_INFO", "TEXT", "就诊号:" + getCaseNo() +
                                             "  床位:" + getBedDesc(selectData.getValue("BED_NO", 0),
                                                                  selectData.getValue("ERD_REGION_CODE", 0)).getValue("BED_DESC",0)+
                                             "  病患姓名:" + getPatName());
            //统计时间
            data.setData("FROMTODATE", "TEXT", from_Date.getText()+" "+from_Time.getText()+"～"+to_Date.getText()+" "+to_Time.getText());
            data.setData("TABLE", printParm.getData());
            this.openPrintWindow(
                "%ROOT%\\config\\prt\\erd\\ERDExecSheetPrt.jhw",
                data);
        }
    }

    /**
     * 取得床位中文描述
     * @param bedNo String
     * @param regionCode String
     * @return TParm
     */
    private TParm getBedDesc(String bedNo,String regionCode){
         return new TParm(TJDODBTool.getInstance().select(" SELECT BED_DESC FROM ERD_BED "+
                                                          " WHERE BED_NO = '"+bedNo+"'"+
                                                          " AND   ERD_REGION_CODE = '"+regionCode+"'"));
    }

    /**
     * 关闭事件
     * @return boolean
     */
    public boolean onClosing() {
//        switch (messageBox("提示信息", "是否保存?", this.YES_NO_CANCEL_OPTION)) {
//            case 0:
//                if (!onSave())
//                    return false;
//                break;
//            case 1:
//                break;
//            case 2:
//                return false;
//        }
        return true;
    }


    public String getCaseNo() {
        return caseNo;
    }

    public String getMrNo() {
        return mrNo;
    }

    public String getPatName() {
        return patName;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    /**
     * 显示菜单
     */
    public void onShowWindowsFunction(){
        callFunction("UI|showTopMenu");
    }
//
//    /**
//     * 瓶签打印
//     */
//    public void onPrintPaster(){ 
//        Vector vct = new Vector();
//        TParm parm = mainTable.getParmValue();
//        for(int i = 0;i<21;i++){
//            vct.add(new Vector());
//        }
//        for(int i = 0;i<parm.getCount("MR_NO");i++){
//            ((Vector)vct.get(0)).add(parm.getData("BED_NO",i));
//            ((Vector)vct.get(1)).add(parm.getData("MR_NO",i));
//            ((Vector)vct.get(2)).add(parm.getData("PAT_NAME",i));
//            ((Vector)vct.get(3)).add(parm.getData("LINKMAIN_FLG",i));
//            ((Vector)vct.get(4)).add(parm.getData("LINK_NO",i));
//            ((Vector)vct.get(5)).add(parm.getData("ORDERDESC",i));
//            ((Vector)vct.get(6)).add(parm.getData("MEDI_QTY",i));
//            ((Vector)vct.get(7)).add(parm.getData("MEDI_UNIT",i));
//            ((Vector)vct.get(8)).add(parm.getData("ORDER_CODE",i));
//            ((Vector)vct.get(9)).add(parm.getData("RX_NO",i));
//            ((Vector)vct.get(10)).add(parm.getData("SEQ_NO",i));
//            ((Vector)vct.get(11)).add(StringTool.getString((Timestamp)parm.getData("ORDER_DATE",i),"yyyyMMdd"));
//            ((Vector)vct.get(12)).add(parm.getData("FREQ_CODE",i));
//
//            ((Vector)vct.get(13)).add(getStationDesc());
//            TParm sexParm = new TParm(TJDODBTool.getInstance().select(
//                    " SELECT *"+
//                    " FROM SYS_PATINFO A"+
//                    " WHERE MR_NO ='" + getMrNo() + "'"));
//            TParm sexDescParm = new TParm(TJDODBTool.getInstance().select(
//                    " SELECT CHN_DESC"+
//                    " FROM SYS_DICTIONARY A "+
//                    " WHERE GROUP_ID = 'SYS_SEX'"+
//                    " AND  ID = '" + sexParm.getData("SEX_CODE",0) + "'"));
//           ((Vector)vct.get(14)).add(sexDescParm.getData("CHN_DESC",0));
//           ((Vector)vct.get(15)).add(StringUtil.getInstance().showAge((Timestamp)sexParm.getData("BIRTH_DATE",0),
//                                                            SystemTool.getInstance().getDate()));
//           String udSt = "";
//           ((Vector)vct.get(16)).add(udSt);
//           ((Vector)vct.get(17)).add(getRouteDesc(parm.getValue("ROUTE_CODE",i)));
//           ((Vector)vct.get(18)).add(getOperatorName(parm.getValue("DR_CODE",i)));
//           ((Vector)vct.get(19)).add(parm.getData("DISPENSE_QTY",i));
//           ((Vector)vct.get(20)).add(parm.getData("DISPENSE_UNIT",i));
//        }
//        vct.add(getUnitMap());
//        openWindow("%ROOT%\\config\\inw\\INWPrintPQUI.x",vct);
//    }
    
    /**
   * 瓶签打印
   * yanjing 20140731 modify
   */
  public void onPrintPaster(){ 
	  TParm parm = mainTable.getParmValue();
	  int row = mainTable.getSelectedRow();
	  if(row<0){
		  this.messageBox("请选择要打印的数据。");
		  return;
	  }else{//为选择的药嘱打印帖签
		  String baseSql = "SELECT A.BIRTH_DATE,C.CHN_DESC, B.USER_NAME FROM SYS_PATINFO A,SYS_OPERATOR B,SYS_DICTIONARY C " +
			"WHERE A.SEX_CODE = C.ID AND  C.GROUP_ID = 'SYS_SEX' " +
				"AND B.USER_ID = '"+parm.getData("DR_CODE", row)+"' " +
						"AND A.MR_NO = '"+mrNo+"' ";//根据mr_no查询病患的基本信息
	TParm resultParm = new TParm(TJDODBTool.getInstance().select(baseSql));
		  String doseType = parm.getData("DOSE_TYPE", row).toString();
			 Timestamp day = SystemTool.getInstance().getDate();
			 String age = DateUtil.showAge(resultParm.getTimestamp("BIRTH_DATE", 0), day);//==liling 20140703 modify
		  if("O".equals(doseType)||"E".equals(doseType)){//口服或者外用
			  TParm date = new TParm();
				
				String doseTypeChn = "";
				if("O".equals(doseType)){
					doseTypeChn = "口服";
				}else{
					doseTypeChn = "外用";
				}
				TParm newTParm = new TParm();
				newTParm.addData("GOODS_DESC", parm.getData("SPECIFICATION",row));
//				newTParm.addData("GOODS_DESC", parm.getData("ORDERDESC", row)+" "+parm.getData("SPECIFICATION",row));
//				newTParm.addData("SPECIFICATION", tparmPHA.getData("SPECIFICATION",i));
//				newTParm.addData("DOSAGE_QTY", tparmPHA.getData("DOSAGE_QTY", i));
				newTParm.addData("FREQ_CODE", parm.getData("FREQ_CODE", row));
				newTParm.addData("QTY", parm.getData("DISPENSE_QTY", row));
				newTParm.addData("ROUTE_CODE", parm.getData("ROUTE_CODE", row));
				
//				newTParm.addData("TAKE_DAYS", tparmPHA.getData("TAKE_DAYS", i));
				
				newTParm.setCount(1);
				newTParm.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
//				newTParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//				newTParm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
				newTParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
				newTParm.addData("SYSTEM", "COLUMNS", "QTY");
				newTParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
				
//				newTParm.addData("SYSTEM", "COLUMNS", "TAKE_DAYS");
				
				
				date.setData("TABLE", newTParm.getData());
				
				// 表头数据 MR_NO
				
				
				date.setData("LINK_NO", "TEXT", parm.getData("LINK_NO", row));
				date.setData("DEPT_TYPE", "TEXT", "门诊【"+doseTypeChn+"】药");
				date.setData("MR_NO", "TEXT", mrNo);
				date.setData("DEPT_CODE", "TEXT", parm.getData("DEPT_ENG_DESC", row));
				date.setData("BED_CODE", "TEXT", parm.getData("BED_CODE", row));
				date.setData("PAT_CODE", "TEXT", patName);
				date.setData("SEX_CODE", "TEXT",resultParm.getValue("CHN_DESC", 0) );
				date.setData("AGE", "TEXT",age);
				 
				date.setData("SEND_CODE","TEXT",resultParm.getValue("USER_NAME", 0));
				date.setData("DOCTOR_CODE","TEXT",resultParm.getValue("USER_NAME", 0));
								
				this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\PHAPersral.jhw", date,
						true);	
		  }else if("I".equals(doseType)||"F".equals(doseType)){//针剂或大量点滴
			  String linkNo = parm.getValue("LINK_NO",0);
			  Set<String>  set=new HashSet<String>();  
				for(int i = 0 ; i < parm.getCount() ; i++){
					linkNo = parm.getValue("LINK_NO",i);
					set.add(linkNo);
				}
			  for( Iterator <String>  it = set.iterator();  it.hasNext(); )  {
					linkNo = (String)it.next();
					TParm tparmPHA  = new TParm();
					TParm date = new TParm();
					int count = 0;
					if("".equals(linkNo)){
						tparmPHA.addData("GOODS_DESC", parm.getData("ORDER_DESC_AND_SPECIFICATION",row));
//						tparmPHA.addData("GOODS_DESC", parm.getData("ORDERDESC", i)+" "+parm.getData("ORDER_DESC_AND_SPECIFICATION",i));
//						tparmPHA.addData("SPECIFICATION", parm.getData("SPECIFICATION",i));
						tparmPHA.addData("QTY", parm.getData("MEDI_QTY", row));
//						tparmPHA.addData("ROUTE_CODE", parm.getData("ROUTE_CODE", i));
//						tparmPHA.addData("FREQ_CODE", parm.getData("FREQ_CODE", i));
						count++ ;
					}else{
						for(int i = 0 ; i < parm.getCount() ; i++){
							String linkNoNew = parm.getValue("LINK_NO",i);
							if(linkNo.equals(linkNoNew)){
								tparmPHA.addData("GOODS_DESC", parm.getData("ORDER_DESC_AND_SPECIFICATION",i));
//								tparmPHA.addData("GOODS_DESC", parm.getData("ORDERDESC", i)+" "+parm.getData("ORDER_DESC_AND_SPECIFICATION",i));
//								tparmPHA.addData("SPECIFICATION", parm.getData("SPECIFICATION",i));
								tparmPHA.addData("QTY", parm.getData("MEDI_QTY", i));
//								tparmPHA.addData("ROUTE_CODE", parm.getData("ROUTE_CODE", i));
//								tparmPHA.addData("FREQ_CODE", parm.getData("FREQ_CODE", i));
								count++ ;
							}
						}	
					}
					
					tparmPHA.setCount(count);
					tparmPHA.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
//					tparmPHA.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
					tparmPHA.addData("SYSTEM", "COLUMNS", "QTY");
//					tparmPHA.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
//					tparmPHA.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
					 
					date.setData("TABLE", tparmPHA.getData());
					// 表头数据 MR_NO
					date.setData("LINK_NO", "TEXT", linkNo);
					date.setData("DEPT_TYPE", "TEXT", "门诊【注射】药");
					date.setData("MR_NO", "TEXT", mrNo);
					date.setData("DEPT_CODE", "TEXT", parm.getData("DEPT_ENG_DESC", row));
					date.setData("BED_CODE", "TEXT",parm.getData("BED_NO", 0));
					date.setData("PAT_CODE", "TEXT", patName);
					date.setData("SEX_CODE", "TEXT",resultParm.getValue("CHN_DESC", 0) );
					date.setData("AGE", "TEXT",age);
					date.setData("ROUTE_CODE", "TEXT",parm.getData("ROUTE_CODE", 0));
					date.setData("FREQ_CODE", "TEXT",parm.getData("FREQ_CODE", 0));
					date.setData("BAR_CODE", "TEXT","");
					
					date.setData("SEND_CODE","TEXT",resultParm.getValue("USER_NAME", 0));
					date.setData("DOCTOR_CODE","TEXT",resultParm.getValue("USER_NAME", 0));
					
					this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\phaInfusion.jhw", date,
							true);	
				}
			  
		  }else{
			  this.messageBox("不可打印。");
			  return;
		  }
	  }
	  
	  
  }

    public String getStationDesc(){
        String sql=" SELECT B.CHN_DESC,A.BED_DESC FROM ERD_BED A,SYS_DICTIONARY B "+
            " WHERE B.GROUP_ID='ERD_REGION' "+
            " AND B.ID=A.ERD_REGION_CODE "+
            " AND A.CASE_NO='"+getCaseNo()+"'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result.getValue("CHN_DESC",0);
    }

    public String getRouteDesc(String routeCode){
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                " SELECT ROUTE_CHN_DESC "+
                " FROM SYS_PHAROUTE "+
                " WHERE ROUTE_CODE = '"+routeCode+"'"));
        return parm.getValue("ROUTE_CHN_DESC",0);
    }

    public String getOperatorName(String userID){
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                " SELECT USER_NAME "+
                " FROM SYS_OPERATOR "+
                " WHERE USER_ID = '"+userID+"'"));
        return parm.getValue("USER_NAME",0);
    }
    /**
     * 取得单位字典
     * @return Map
     */
    public Map getUnitMap(){
        Map map = new  HashMap();
        TParm parm = new TParm(TJDODBTool.getInstance().select("SELECT UNIT_CODE,UNIT_CHN_DESC FROM SYS_UNIT"));
        for(int i = 0;i < parm.getCount();i++){
            map.put(parm.getData("UNIT_CODE",i),parm.getData("UNIT_CHN_DESC",i));
        }
        return map;
    }

    /**
     * 补充计费
     */
    public void onCharge(){
        TParm parm = new TParm();
        parm.setData("MR_NO",mrNo);
        parm.setData("CASE_NO",caseNo);
        parm.setData("SYSTEM","ONW");
//        openWindow("%ROOT%\\config\\opb\\OPBChargesM.x",parm);
        parm.setData("count", "1");
//      this.openDialog("%ROOT%\\config\\opb\\OPBChargesM.x", parm);//旧补充计费界面
      this.openDialog("%ROOT%\\config\\opbTest\\OPBCharge.x",parm);//新补充计费界面
    }

    //测试用例
    public static void main(String[] args) {
        JavaHisDebug.initClient();

//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("erd\\ERDOrderExecMain.x");
    }


}
