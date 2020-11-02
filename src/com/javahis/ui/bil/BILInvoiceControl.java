package com.javahis.ui.bil;

import java.sql.Timestamp;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import jdo.bil.BILInvoiceTool;
import jdo.sys.Operator;

import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:�鳤����Ʊ�� </p>
 *
 * <p>Description:�鳤����Ʊ�� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class BILInvoiceControl
    extends TControl {
    int selectrow = -1;
    TParm data = new TParm();
    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        //table�������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //tableֵ�ı��¼�
        this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                              "onTableChangeValue");
        this.onQuery();
    }

    /**
     *���Ӷ�Table����ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        setValueForParm("RECP_TYPE;START_INVNO;END_INVNO;UPDATE_NO;CASHIER_CODE;START_VALID_DATE;END_VALID_DATE;STATUS",
                        data, row); //�����Ϸ�
        selectrow = row;
        callFunction("UI|delete|setEnabled", true);
    }

    /**
     * ���Ӷ�Tableֵ�ı�ļ���
     * @param obj Object
     */
    public void onTableChangeValue(Object obj) {
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return;
        if (node.getColumn() != 0)
            return;
        node.getTable().getParmValue().setData("FLG", node.getRow(),
                                               node.getValue());
//          System.out.println(node.getTable().getParmValue());
        //data.setData("FLG", node.getValue());
        //�õ��޸ĵ���
        //node.getRow();
        //�޸ĵ���ֵ
        //node.getValue();
        //callFunction("UI|delete|setEnabled", true);
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        //�õ���ѯ����
        TParm parm = this.getdata();
        //���table
        this.callFunction("UI|TABLE|removeRowAll");
        data = new TParm();
        if(!"".equals(Operator.getRegion()))
            parm.setData("REGION_CODE",Operator.getRegion());
        //��ѯ����
        String where = "";
        String date_s = getValueString("START_VALID_DATE");
		String date_e = getValueString("END_VALID_DATE");
		if (date_s.length() > 0 ) {
			date_s = date_s.substring(0, 10).replace("-", "");
			where += " AND A.START_VALID_DATE>=TO_DATE('" + date_s + "','YYYYMMDDHH24MISS')";
		}
		if (date_e.length() > 0 ) {
			date_e = date_e.substring(0, 10).replace("-", "");
			where += " AND A.END_VALID_DATE<=TO_DATE('" + date_e + "','YYYYMMDDHH24MISS')";
		}
        if(parm.getValue("REGION_CODE").length() > 0){
        	where += " AND A.REGION_CODE = '" + parm.getValue("REGION_CODE") + "'";
        }
        if(parm.getValue("RECP_TYPE").length() > 0){
        	where += " AND A.RECP_TYPE = '" + parm.getValue("RECP_TYPE") + "'";
        }
        if(parm.getValue("CASHIER_CODE").length() > 0){
        	where += " AND A.CASHIER_CODE = '" + parm.getValue("CASHIER_CODE") + "'";
        }
        if(parm.getValue("STATUS").length() > 0){
        	where += " AND A.STATUS = '" + parm.getValue("STATUS") + "'";
        }
        String sql = 
        	" SELECT 'N' AS FLG,A.RECP_TYPE,A.START_INVNO,A.END_INVNO,A.UPDATE_NO, " +
        	" A.CASHIER_CODE,A.START_VALID_DATE,A.END_VALID_DATE,A.STATUS, " +
        	" A.OPT_USER,A.OPT_DATE,A.OPT_TERM,'N' AS STATU,A.TERM_IP " +
        	" FROM BIL_INVOICE A,SYS_OPERATOR B  " +
        	" WHERE A.CASHIER_CODE = B.USER_ID " +
        	where +
        	" ORDER BY A.STATUS";
        data = new TParm(TJDODBTool.getInstance().select(sql));
//        data = BILInvoiceTool.getInstance().selDate(parm);
        //��������
        
        data = this.FinishData(data);
        //��table���
        this.callFunction("UI|TABLE|setParmValue", data);
    }

    /**
     * ��ѯ���
     * @return TParm
     */
    public TParm getdata() {
        TParm parm = new TParm();
        String value = getValueString("RECP_TYPE");
        if (value.length() > 0)
            parm.setData("RECP_TYPE", value);
        value = getValueString("CASHIER_CODE");
        if (value.length() > 0)
            parm.setData("CASHIER_CODE", value);
        value = getValueString("STATUS");
        if (value.length() > 0)
            parm.setData("STATUS", value);
        return parm;
    }

    /**
     * ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm FinishData(TParm parm) {
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getData("STATUS", i).equals("2"))
                parm.setData("FLG", i, "Y");
        }
        return parm;
    }

    /**
     * ���淽��
     * ����ѽ��ص�Ʊȷ�Ͻ���
     */
    public void onSave() {
        //���������¼�
        this.callFunction("UI|TABLE|acceptText");
        TParm parm = (TParm)this.callFunction("UI|TABLE|getParmValue");
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getData("FLG", i).equals("Y")) {
                //01����ʹ����
                if (parm.getData("STATUS", i).equals("1")) {//����״̬
                    this.messageBox("�Ѿ�����,���ܽ���");
                    //========20120217 zhangp modify start
//                    break;
                    return;
                    //======20120217 zhangp modify end
                    
                }else if(parm.getData("STATUS", i).equals("0")){//ʹ����
                	 this.messageBox("����ʹ����,���ܽ���");
                     return;
                }
//                     System.out.println(parm.getData("STATUS", i));
                TParm datat = new TParm();
                datat.setRowData( -1, parm, i);
                //3����ȷ�Ͻ���
                datat.setData("STATUS", "3");
                datat.setData("OPT_USER", Operator.getID());
                datat.setData("OPT_TERM", Operator.getIP());
                //���ø��·���//ʹ��״̬��ȷ�Ͻ���
                TParm result = BILInvoiceTool.getInstance().updataData(datat);
                parm.setData("FLG", i, "N");
                if (result.getErrCode() < 0) {
                    this.messageBox("E0005"); //ִ��ʧ��
                    return;
                }
            }
        }
        this.messageBox("P0005"); //ִ�гɹ�
        this.callFunction("UI|TABLE|setParmValue", parm);
    }
    /**
     *��Ʊ
     */
    public void Recipients() {
        String value = getValueString("RECP_TYPE");
        if (value.length() == 0) {
            this.messageBox("E0012");
            return;
        }
        this.openDialog("%ROOT%\\config\\bil\\BILRecipients.x", value);
        //====zhangp 20120307 modify start
        onClear();
        //=====zhangp 20120307 modify end
        this.onQuery();
    }

    /**
     * ��շ���
     */
    public void onClear() {
        clearValue("RECP_TYPE;START_INVNO;END_INVNO;UPDATE_NO;CASHIER_CODE;START_VALID_DATE;END_VALID_DATE;STATUS");
        selectrow = -1;
        callFunction("UI|TABLE|clearSelection");
    }
    /**
	 * ���Excel
	 */
	public void onExport() {

		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		 TTable table = (TTable) callFunction("UI|TABLE|getThis");
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(
					table,
					StringTool.getString(SystemTool.getInstance().getDate() , "yyyy.MM")
							+ "Ʊ�ݹ����");
	}
}
