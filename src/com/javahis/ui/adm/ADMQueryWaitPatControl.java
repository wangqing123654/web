package com.javahis.ui.adm;

import jdo.sys.PatTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * <p> Title:病患入院、出床、召回查询 </p>
 *
 * <p> Description:病患入院、出床、召回查询 </p>
 *
 * <p> Copyright: Copyright (c) 2013 </p>
 *
 * <p> Company:Javahis </p>
 *
 * @author chenx 2013.5.8
 * @version 4.0
 */
public class ADMQueryWaitPatControl extends TControl{
	private TTable table ;
	/**
	 * 初始化
	 */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        this.onQuery();
    }

    /**
     * 查询
     */
    public void onQuery() {
        String mrNo = "";
        String whereSql = "";
        String sql =
                "SELECT A.*,B.PAT_NAME FROM ADM_WAIT_TRANS A ,SYS_PATINFO B "
                        + " WHERE A.MR_NO = B.MR_NO ";
        if (this.getValueString("MR_NO").length() > 0) {
            mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
            whereSql = " AND A.MR_NO ='" + mrNo + "'";
        }
        sql += whereSql;
        // System.out.println("SQL========"+sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm.getCount() < 0) {
            this.messageBox("查无数据");
            this.onClear();
            // return;
        } else {
            table.setParmValue(parm);
        }
    }
    
    /**
     * 病案号回车事件
     */
    public void onMrnoAction() {
        String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
        this.setValue("MR_NO", mrNo);
        String patName = PatTool.getInstance().getNameForMrno(mrNo);
        this.setValue("PAT_NAME", patName);
        this.onQuery();
    }

    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("MR_NO;PAT_NAME");
        table.removeRowAll();
        // this.onInit() ;
    }
}