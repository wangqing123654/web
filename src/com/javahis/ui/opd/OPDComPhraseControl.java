package com.javahis.ui.opd;

import java.util.ArrayList;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;

/**
 *
 * <p> Title: Ƭ��Panel </p>
 *
 * <p> Description:Ƭ��Panel </p>
 *
 * <p> Copyright: Copyright (c) Liu dongyang 2008 </p>
 *
 * <p> Company:Javahis </p>
 *
 * @author ehui 20080924
 * @version 1.0
 */
public class OPDComPhraseControl extends TControl {
	//�����п���ҽʦ����
	private String deptOrDr="";
	//�����п��ҡ�ҽʦ����
	private String deptOrDrCode="";
	//�Ƿ񱣴�
	private boolean isSave=false;
	//TABLE
	private TTable table;
	//COMBOBOX
	private TComboBox combo,other;
	//���ҡ�ҽʦLABEL
	private TLabel label;
	
	/**
	 * ��ʼ������
	 */
    public void onInit() {
        super.onInit();
        // ���ݽ�����������ҡ�ҽʦ�ķ�����ֵ
        label = (TLabel) this.getComponent("LABEL");
        deptOrDr = this.getParameter() + "";
        if ("1".equalsIgnoreCase(deptOrDr)) {
            deptOrDrCode = Operator.getDept();
            combo = (TComboBox) this.getComponent("DEPT");
            combo.setSelectedID(deptOrDrCode);
            combo.setVisible(true);
            combo.setEnabled(false);
            other = (TComboBox) this.getComponent("OPERATOR");
            other.setVisible(false);
            label.setText("����");
            label.setZhText("����");
            label.setEnText("Dept.");
        } else {
            deptOrDrCode = Operator.getID();
            combo = (TComboBox) this.getComponent("OPERATOR");
            combo.setValue(deptOrDrCode);
            combo.setVisible(true);
            combo.setEnabled(false);
            other = (TComboBox) this.getComponent("DEPT");
            other.setVisible(false);
            label.setText("ҽʦ");
            label.setZhText("ҽʦ");
            label.setEnText("Dr.");
        }
        table = (TTable) this.getComponent("TABLECOM");
        table.addEventListener("TABLECOM->" + TTableEvent.CHANGE_VALUE, this, "onTableChangeValue");
        // table.addEventListener("TABLECOM->" + TTableEvent.DOUBLE_CLICKED, this,
        // "onDoubleClicked");
        TDataStore tds = new TDataStore();
        tds.setSQL("SELECT * FROM OPD_COMPHRASE WHERE DEPT_OR_DR='" + deptOrDr
                + "' AND DEPTORDR_CODE='" + deptOrDrCode + "' ORDER BY PHRASE_CODE");
        tds.retrieve();
        int row = tds.insertRow(-1);
        tds.setActive(row, false);
        table.setDataStore(tds);
        table.setDSValue();
    }
	
	/**
	 * TABLEֵ�ı��¼�
	 * @param tableNode
	 */
    public boolean onTableChangeValue(TTableNode tNode) {
        // System.out.println("==============onTableChangeValue=====================");
        int row = table.getSelectedRow();
        table.setItem(row, "DEPT_OR_DR", deptOrDr);
        table.setItem(row, "DEPTORDR_CODE", deptOrDrCode);
        table.setItem(row, "OPT_DATE", table.getDataStore().getDBTime());
        table.setItem(row, "OPT_USER", Operator.getID());
        table.setItem(row, "OPT_TERM", Operator.getIP());
        TDataStore tds = table.getDataStore();
        tds.setActive(row, true);
        String colName = table.getParmMap(tNode.getColumn());
        if ("PHRASE_CODE".equalsIgnoreCase(colName)) {
            String text = tNode.getValue().toString().trim();// modify by wanglong 20131115 ȥ��ǰ��ո�
            tNode.setValue(text);// add by wanglong 20131115
            tNode.getTable().grabFocus();
            String py = TMessage.getPy(text);
            tds.setItem(row, "PY1", py);
        }
        return false;
    }
	
	/**
	 * ����
	 */
    public void onSave() {
        table.acceptText();
        String[] sql = table.getDataStore().getUpdateSQL();
        //modify by huangjw 20140926 start
        int sucess=0;
        for(int i=0;i<sql.length;i++){
        	TParm result = new TParm(TJDODBTool.getInstance().update(sql[i]));
        	if(result.getErrCode()!=0){
        		continue;
        	}
        	sucess++;
        }
      //modify by huangjw 20140926 end
        //TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (sucess== 0) {
            this.messageBox("E0001");
            onClear();
            table.getDataStore().resetModify();
            return;
        }
        this.messageBox("P0001");
        onClear();
        table.getDataStore().resetModify();
    }
    
	/**
	 * ���
	 */
    public void onClear() {
        table.retrieve();
        TDataStore tds = table.getDataStore();
        if (!"".equalsIgnoreCase(table.getValueAt(table.getRowCount() - 1, 3) + "")) {
            tds.insertRow(-1);
            tds.setActive(tds.rowCount() - 1, false);
        }
        table.setDSValue();
    }

//    public void onDoubleClicked(int row) {
//        // System.out.println("=========onTableOpenEMRTemplateEdit============"+row);
//    }
}
