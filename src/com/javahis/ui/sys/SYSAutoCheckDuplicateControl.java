package com.javahis.ui.sys;

import com.dongyang.control.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: 病案自动查重Control</p>
 *
 * <p>Description: 病案自动查重Control</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author Zhangjg
 * @version 1.0
 */
public class SYSAutoCheckDuplicateControl
    extends TControl {
    public SYSAutoCheckDuplicateControl() {
    }

    /** 按身份证号查重 */
    private TCheckBox IDNO_FLG;
    /** 按姓名查重 */
    private TCheckBox PAT_NAME_FLG;
    /** 已处理 */
    private TCheckBox HANDLE_FLG;
    /** 身份证号 */
    private TTextField IDNO;
    /** 姓名 */
    private TTextField PAT_NAME;
    /** MASTER_TABLE */
    private TTable MASTER_TABLE;
    /** DETAIL_TABLE */
    private TTable DETAIL_TABLE;
    /** 声明日期格式化类对象 */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    /**
     * 初始化方法
     * 初始化查询全部
     */
    public void onInit() {
        super.onInit();
        // 获取全部输入框控件
        getAllComponent();
        // 注册监听事件
        initControler();
        // 自动查重
        queryMaster();
    }

    /**
     * 获取全部输入框控件
     */
    public void getAllComponent() {
        this.IDNO_FLG = (TCheckBox)this.getComponent("IDNO_FLG");
        this.PAT_NAME_FLG = (TCheckBox)this.getComponent("PAT_NAME_FLG");
        this.HANDLE_FLG = (TCheckBox)this.getComponent("HANDLE_FLG");
        this.IDNO = (TTextField)this.getComponent("IDNO");
        this.PAT_NAME = (TTextField)this.getComponent("PAT_NAME");
        this.MASTER_TABLE = (TTable)this.getComponent("MASTER_TABLE");
        this.DETAIL_TABLE = (TTable)this.getComponent("DETAIL_TABLE");
    }

    /**
     * 注册监听事件
     */
    public void initControler() {
        // MASTER_TABLE
        callFunction("UI|MASTER_TABLE|addEventListener",
                     "MASTER_TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
    }

    public void onTableClicked(int row) {
        TParm data = MASTER_TABLE.getParmValue();
        TParm parm = data.getRow(row);
        TParm result = queryDetail(parm);
        DETAIL_TABLE.setParmValue(result, DETAIL_TABLE.getParmMap());
    }

    /**
     * 自动查重
     */
    public void queryMaster() {
        StringBuilder sqlBuf = new StringBuilder();
        // 根据身份证号和姓名查重
        if ("Y".equals(IDNO_FLG.getValue()) && "Y".equals(PAT_NAME_FLG.getValue())) {
            sqlBuf.append(" SELECT IDNO, PAT_NAME, COUNT(*) AS COUNT ");
            sqlBuf.append(" FROM SYS_PATINFO WHERE 1 = 1 ");
            if ("N".equals(HANDLE_FLG.getValue()) || !checkInputString(HANDLE_FLG.getValue())) {
            	sqlBuf.append(" AND (HANDLE_FLG = 'N' OR HANDLE_FLG IS NULL)");
            }
            if (checkInputString(IDNO.getValue())) {
                sqlBuf.append(" AND IDNO LIKE '%" + IDNO.getValue() + "%'");
            }
            if (checkInputString(PAT_NAME.getValue())) {
                sqlBuf.append(" AND PAT_NAME LIKE '%" + PAT_NAME.getValue() + "%'");
            }
            sqlBuf.append(" GROUP BY IDNO, PAT_NAME HAVING COUNT(*) > 1");
        }
        // 根据姓名查重
        else if ("Y".equals(PAT_NAME_FLG.getValue())) {
            sqlBuf.append(" SELECT '' AS IDNO, PAT_NAME, COUNT(*) AS COUNT ");
            sqlBuf.append(" FROM SYS_PATINFO WHERE 1 = 1 ");
            if ("N".equals(HANDLE_FLG.getValue()) || !checkInputString(HANDLE_FLG.getValue())) {
            	sqlBuf.append(" AND (HANDLE_FLG = 'N' OR HANDLE_FLG IS NULL)");
            }
            if (checkInputString(IDNO.getValue())) {
                sqlBuf.append(" AND IDNO LIKE '%" + IDNO.getValue() + "%'");
            }
            if (checkInputString(PAT_NAME.getValue())) {
                sqlBuf.append(" AND PAT_NAME LIKE '%" + PAT_NAME.getValue() + "%'");
            }
            sqlBuf.append(" GROUP BY PAT_NAME HAVING COUNT(*) > 1");
        }
        // 根据身份证号查重（默认）
        else {
            sqlBuf.append(" SELECT IDNO, '' AS PAT_NAME, COUNT(*) AS COUNT ");
            sqlBuf.append(" FROM SYS_PATINFO WHERE 1 = 1 ");
            if ("N".equals(HANDLE_FLG.getValue()) || !checkInputString(HANDLE_FLG.getValue())) {
            	sqlBuf.append(" AND (HANDLE_FLG = 'N' OR HANDLE_FLG IS NULL)");
            }
            if (checkInputString(IDNO.getValue())) {
                sqlBuf.append(" AND IDNO LIKE '%" + IDNO.getValue() + "%'");
            }
            if (checkInputString(PAT_NAME.getValue())) {
                sqlBuf.append(" AND PAT_NAME LIKE '%" + PAT_NAME.getValue() + "%'");
            }
            sqlBuf.append(" GROUP BY IDNO HAVING COUNT(*) > 1");
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlBuf.toString()));
        MASTER_TABLE.setParmValue(result, MASTER_TABLE.getParmMap());
    }

    /**
     * 字符串非空验证
     * @param str String
     * @return boolean
     */
    public boolean checkInputString(Object obj) {
        if (obj == null) {
            return false;
        }
        String str = String.valueOf(obj);
        if (str == null) {
            return false;
        }
        else if ("".equals(str.trim())) {
            return false;
        }
        else {
            return true;
        }
    }


    public TParm queryDetail(TParm parm) {
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append(" SELECT 'N' AS SEL_FLG, MR_NO,IPD_NO,IDNO,PAT_NAME,SEX_CODE,BIRTH_DATE,HEIGHT,WEIGHT,BLOOD_TYPE,CTZ1_CODE,NATION_CODE,SPECIES_CODE,FIRST_ADM_DATE,MERGE_TOMRNO,MERGE_FLG,HANDLE_FLG FROM SYS_PATINFO WHERE 1 = 1 ");
        // 根据身份证号和姓名查重
        if ("Y".equals(IDNO_FLG.getValue()) && "Y".equals(PAT_NAME_FLG.getValue())) {
            if(checkInputString(parm.getValue("IDNO"))) {
                sqlBuf.append(" AND IDNO = '" + parm.getValue("IDNO") + "'");
            }
            else {
                sqlBuf.append(" AND IDNO IS NULL");
            }
            if (checkInputString(parm.getValue("PAT_NAME"))) {
                sqlBuf.append(" AND PAT_NAME = '" + parm.getValue("PAT_NAME") +
                              "'");
            }
            else {
                sqlBuf.append(" AND PAT_NAME IS NULL ");
            }
        }
        // 根据姓名查重
        else if ("Y".equals(PAT_NAME_FLG.getValue())) {
            if(checkInputString(parm.getValue("IDNO"))) {
                sqlBuf.append(" AND IDNO = '" + parm.getValue("IDNO") + "'");
            }
            if (checkInputString(parm.getValue("PAT_NAME"))) {
                sqlBuf.append(" AND PAT_NAME = '" + parm.getValue("PAT_NAME") +
                              "'");
            }
            else {
                sqlBuf.append(" AND PAT_NAME IS NULL ");
            }
        }
        // 根据身份证号查重（默认）
        else {
            if(checkInputString(parm.getValue("IDNO"))) {
                sqlBuf.append(" AND IDNO = '" + parm.getValue("IDNO") + "'");
            }
            else {
                sqlBuf.append(" AND IDNO IS NULL");
            }
            if (checkInputString(parm.getValue("PAT_NAME"))) {
                sqlBuf.append(" AND PAT_NAME = '" + parm.getValue("PAT_NAME") +
                              "'");
            }
        }
        sqlBuf.append(" ORDER BY MR_NO");
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlBuf.
                toString()));
        return result;
    }

    /**
     * 清空
     */
    public void onClear() {
        this.IDNO_FLG.setValue("Y");
        this.PAT_NAME_FLG.setValue("Y");
        this.HANDLE_FLG.setValue("N");
        this.IDNO.setValue("");
        this.PAT_NAME.setValue("");
    }

    /**
     * 自动查重
     */
    public void onQuery() {
        queryMaster();
    }

    /**
     * 合并
     */
    public void onSave() {
        DETAIL_TABLE.acceptText();
        if (DETAIL_TABLE.getRowCount() <= 0) {
            this.messageBox("没有要合并的病案！");
            return;
        }
        TParm data = DETAIL_TABLE.getParmValue();
        TParm parm = new TParm();
        int count = data.getCount("SEL_FLG");
        int j = 0;
        String MERGE_TOMRNO = "";
        for (int i = 0;i<count;i++) {
        	if ("Y".equals(data.getValue("SEL_FLG", i))) {
        		parm.addData("MR_NO", data.getValue("MR_NO", i));
            	parm.addData("MERGE_FLG", "Y");
                if(j <= 0) {
                	parm.addData("MERGE_TOMRNO", "");
                	MERGE_TOMRNO = parm.getValue("MR_NO", j);
                } else {
                	parm.addData("MERGE_TOMRNO", MERGE_TOMRNO);
                }
                j++;
            }
        }
        this.messageBox(parm.toString());
        if (parm.getCount("MR_NO") < 2) {
            this.messageBox("请选择要合并的病案！");
            return;
        }
        if (this.messageBox("询问", "是否合并病案？", 2) != 0) {
            return;
        }
        TParm result = TIOM_AppServer.executeAction("action.sys.SYSAutoCheckDuplicateAction", "update", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            this.messageBox("合并失败！");
        }
        else {
            this.messageBox("合并成功！");

            onTableClicked(MASTER_TABLE.getSelectedRow());
        }
    }
     
    /**
     * 处理
     */
    public void onHandle() {
        DETAIL_TABLE.acceptText();
        if (DETAIL_TABLE.getRowCount() <= 0) {
            this.messageBox("没有要处理的病案！");
            return;
        }
        TParm data = DETAIL_TABLE.getParmValue();
        TParm realParm = new TParm();
        int count = data.getCount("SEL_FLG");
        StringBuilder buf = new StringBuilder();
        for (int i = 0;i<count;i++) {
            String selFlg = data.getValue("SEL_FLG", i);
            if ("Y".equals(selFlg)) {
                realParm.addData("MR_NO", data.getValue("MR_NO", i));
                buf.append(data.getValue("MR_NO", i) + ",");
            }
        }
        if (realParm.getCount("MR_NO") < 1) {
            this.messageBox("请选择要处理的病案！");
            return;
        }
        if (this.messageBox("询问", "是否处理？", 2) != 0) {
            return;
        }
        buf.append("-1");
        String sql = " UPDATE SYS_PATINFO SET "
        	+ " HANDLE_FLG = 'Y' "
            + " WHERE MR_NO IN ("
            + buf.toString() + ")";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            this.messageBox("处理失败！");
        }
        else {
            this.messageBox("处理成功！");
            onTableClicked(MASTER_TABLE.getSelectedRow());
        }
    }

    /**
     * 撤销
     */
    public void onUndo() {
        DETAIL_TABLE.acceptText();
        if (DETAIL_TABLE.getRowCount() <= 0) {
            this.messageBox("没有要撤销合并及处理的病案！");
            return;
        }
        TParm data = DETAIL_TABLE.getParmValue();
        TParm realParm = new TParm();
        int count = data.getCount("SEL_FLG");
        StringBuilder buf = new StringBuilder();
        for (int i = 0;i<count;i++) {
            String selFlg = data.getValue("SEL_FLG", i);
            if ("Y".equals(selFlg)) {
                realParm.addData("MR_NO", data.getValue("MR_NO", i));
                buf.append(data.getValue("MR_NO", i) + ",");
            }
        }
        if (realParm.getCount("MR_NO") < 1) {
            this.messageBox("请选择要撤销合并及处理的病案！");
            return;
        }
        if (this.messageBox("询问", "是否撤销合并及处理？", 2) != 0) {
            return;
        }
        buf.append("-1");
        String sql = " UPDATE SYS_PATINFO SET "
        	+ " HANDLE_FLG = 'N', "
        	+ " MERGE_FLG = 'N', "
            + " MERGE_TOMRNO = '' "
            + " WHERE MR_NO IN ("
            + buf.toString() + ")";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            this.messageBox("撤销失败！");
        }
        else {
            this.messageBox("撤销成功！");
            onTableClicked(MASTER_TABLE.getSelectedRow());
        }
    }

}
