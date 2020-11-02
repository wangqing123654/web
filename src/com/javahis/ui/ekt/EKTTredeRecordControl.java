package com.javahis.ui.ekt;

import com.dongyang.control.TControl;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;

import java.text.DecimalFormat;
import java.util.Date;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.Pat;

import java.util.ArrayList;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextField;
import java.awt.Component;
import java.util.Vector;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;

import jdo.bil.BIL;
import jdo.ekt.EKTTool;

/**
 *
 * <p>Title: ҽ�ƿ����׼�¼������</p>
 *
 * <p>Description: ҽ�ƿ����׼�¼������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangp 2011.12.19
 * @version 1.0
 */
public class EKTTredeRecordControl
    extends TControl {
    int selectRow = -1;
    boolean flg;
    TParm data;
    /**
     * zhangp 20121219 ���벡����
     */
    private TParm acceptData = new TParm(); //�Ӳ�
    
    private String cardNo = "";
    
    private Pat pat;
    /**
     * ��ʼ��
     */
    public void onInit() {
    	
    	TParm parm = new TParm();
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            acceptData = (TParm) obj;
            cardNo = acceptData.getData("CARD_NO").toString();
            
            parm.setData("CARD_NO", cardNo);
            
        }
        super.onInit();
        initPage();
        onQuery();
    }

    /**
     * ��ʼ������
     */
    public void initPage() {

    }

    /**
     * �õ�TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
    
    /**
     * �ܽ��table�����¼�
     */
    public void onTREDETABLEClicked() {
    	TTable table = (TTable)this.callFunction("UI|TREDETABLE|getThis");
    	int row = table.getSelectedRow();
    	TParm p = new TParm();
    	p.setData("BUSINESS_NO", table.getValueAt(row, 1));
    	this.onQueryDetail(p);
    }
    /**
     * ���sql
     * @return
     */
    private String getSQL() {
    	String state = this.getValue("STATE").toString();
    	String businessNo = this.getValue("BUSINESS_NO").toString();
    	String businessType = this.getValue("BUSINESS_TYPE").toString();
    	String tredeNo = this.getValue("TREDE_NO").toString();
    	StringBuilder sql = new StringBuilder();
    	String sql1 = "SELECT BUSINESS_NO,CARD_NO,MR_NO,CASE_NO,PAT_NAME,AMT,BUSINESS_TYPE,STATE,TREDE_NO FROM EKT_TREDE WHERE CARD_NO = "+"'"+cardNo+"'";
    	sql.append(sql1);
    	if (!"".equals(state)) {
    		sql.append(" AND STATE = "+"'"+state+"'");
    	}else{
    		sql.append(" AND STATE ='1' or STATE = '0'");
    	}
    	if (!"".equals(businessNo)) {
    		sql.append(" AND BUSINESS_NO = "+"'"+businessNo+"'");
    	}
    	if (!"".equals(businessType)) {
    		sql.append(" AND BUSINESS_TYPE = "+"'"+businessType+"'");
    	}
    	if (!"".equals(tredeNo)) {
    		sql.append(" AND TREDE_NO = "+"'"+tredeNo+"'");
    	}
    	return sql.toString();
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
//        TParm result = EKTTool.getInstance().selectEKTTredeTotal(parm);
//         //�жϴ���ֵ
//        if (result.getErrCode() < 0) {
//            messageBox(result.getErrText());
//            return;
//        }
//        ((TTable)getComponent("TREDETABLE")).setParmValue(result);
    	String sql = getSQL();
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
          messageBox(result.getErrText());
          return;
        }
        ((TTable)getComponent("TREDETABLE")).setParmValue(result);
    }
    
    /**
     * ��ѯ��ϸ
     * 
     */
    public void onQueryDetail(TParm parm){
        TParm result = EKTTool.getInstance().selectEKTTradeDetail(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        ((TTable)getComponent("TREDETABLEDETAIL")).setParmValue(result);
    }
    
    /**
     * ��ӡ����
     */
    public void onPrint(){
    	// ��ӡ������
		TParm data = new TParm();
		//��Ҫ��ӡ��Table
		TTable table = (TTable) this.getComponent("TREDETABLEDETAIL");
		//���б��в������κ�����ʱ,��ʾ
		if (table.getRowCount() <= 0) {
			this.messageBox("û�д�ӡ����");
			return;
		}
		// ��ͷ����
		data.setData("TITLE", "TEXT", "ҽ�ƿ����׼�¼");
		data.setData("DATE", "TEXT", "ʱ��: "
				+ SystemTool.getInstance().getDate());
		pat = Pat.onQueryByMrNo(TypeTool.getString(table.getValueAt(0, 2)));
		data.setData("NAME", "TEXT", "�Ʊ���: " + pat.getName());
		// �������
		TParm parm = new TParm();
		TParm tableParm = table.getParmValue();
		double sumOriginalBalance = 0.00;
		DecimalFormat df = new DecimalFormat("##########0.00");
		for (int i = 0; i < table.getRowCount(); i++) {
			parm.addData("BUSINESS_NO", tableParm.getValue("BUSINESS_NO", i));
			parm.addData("CARD_NO", tableParm.getValue("CARD_NO", i));
			parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
			parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
			double originalBalance = tableParm.getDouble("ORIGINAL_BALANCE", i);
			double businessAmt = tableParm.getDouble("BUSINESS_AMT", i);
			double currentBalance = tableParm.getDouble("CURRENT_BALANCE", i);
			parm.addData("ORIGINAL_BALANCE", df.format(originalBalance));
			parm.addData("BUSINESS_AMT", df.format(businessAmt));
			parm.addData("CURRENT_BALANCE", df.format(currentBalance));
		}
		parm.setCount(parm.getCount("BUSINESS_NO"));
		parm.addData("SYSTEM", "COLUMNS", "BUSINESS_NO");
		parm.addData("SYSTEM", "COLUMNS", "CARD_NO");
		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
		parm.addData("SYSTEM", "COLUMNS", "CASE_NO");
		parm.addData("SYSTEM", "COLUMNS", "ORIGINAL_BALANCE");
		parm.addData("SYSTEM", "COLUMNS", "BUSINESS_AMT");
		parm.addData("SYSTEM", "COLUMNS", "CURRENT_BALANCE");
		//�ѱ��������ӽ�Ҫ��ӡ��parm
		data.setData("TABLE", parm.getData());
		// ��β����
		data.setData("NAME", "TEXT", "�ֿ��ˣ�"+pat.getName());
		// ���ô�ӡ����,����·��
		//========modify by lim 2012/02/24 begin
		this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTTredeRecord.jhw",data);
		//========modify by lim 2012/02/24 end

    }
    
    /**
     * ���Excel
     */
    public void onExport() {
    	 TTable table = (TTable) callFunction("UI|TREDETABLEDETAIL|getThis");
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "ҽ�ƿ����׼�¼��");
    }
    
    /**
     * ���
     */
    public void onClear() {
        String clear =
            "TREDE_NO;BUSINESS_NO;STATE;BUSINESS_TYPE";
        this.clearValue(clear);
    }

    
}
