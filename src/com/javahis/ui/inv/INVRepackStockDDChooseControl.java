package com.javahis.ui.inv;

import jdo.inv.INVSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

/**
 * <p>Title: ��Ӧ�����´������ѡ�������һ���Ե�������</p>
 *
 * <p>Description: ��Ӧ�����´������ѡ�������һ���Ե�������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author wangm 2013.8.6
 * @version 1.0
 */
public class INVRepackStockDDChooseControl
    extends TControl {

    /**
     * ��
     */
    private TTable table;

    private String inv_code = "";

    private String inv_chn_desc = "";

    private double qty = 0;

    public INVRepackStockDDChooseControl() {
    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        table = (TTable)getComponent("TABLE");
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        inv_code = parm.getValue("INV_CODE");
        inv_chn_desc = parm.getValue("INV_CHN_DESC");
        qty = parm.getDouble("QTY");
        TParm result = new TParm(TJDODBTool.getInstance().select(getSQL(inv_code)));
        table.setParmValue(result);
        this.setValue("QTY", qty);
        this.setValue("INV_CHN_DESC", inv_chn_desc);
    }

    /**
     * ���ط���
     */
    public void onReturn() {
        table.acceptText();
        TParm result = table.getParmValue();
        TParm resultParm = new TParm();
        for (int i = result.getCount("RFID") - 1; i >= 0; i--) {
            if ("Y".equals(table.getItemString(i, "SELECT_FLG"))) {
                resultParm.addData("RFID",
                                   result.getRow(i).getValue("RFID"));
                resultParm.addData("INV_CODE",
                                   result.getRow(i).getValue("INV_CODE"));
                resultParm.addData("INVSEQ_NO", result.getRow(i).getValue("INVSEQ_NO"));
            }
        }
        double return_qty = resultParm.getCount("RFID");
        if (return_qty <= 0) {
            this.messageBox("û��ѡ������");
            return;
        }
        if (return_qty > qty) {
            this.messageBox("ѡ������������������");
            return;
        }
        setReturnValue(resultParm);
        this.closeWindow();
    }
    
    /**
	 * ɨ������
	 */
	public void onScream() {
		
		String barcode = this.getValueString("BARCODE");
		
		if(barcode!=null&&barcode.length()>0){
			TParm result = table.getParmValue();
			for(int i = result.getCount("RFID") - 1; i >= 0; i--){
				if (barcode.equals(result.getData("RFID", i))) {
					table.setItem(i, "SELECT_FLG", "Y");
				}
			}
		}
		
		((TTextField)getComponent("BARCODE")).setValue("");
	}
	
	
	/**
	 * ��ѯSQL
	 */
	private String getSQL(String invCode){
		String sql = "SELECT 'N' AS SELECT_FLG, B.INV_CHN_DESC, S.INV_CODE, S.INVSEQ_NO, S.RFID FROM INV_STOCKDD S INNER JOIN INV_BASE B ON S.INV_CODE = B.INV_CODE " 
			+ " WHERE S.INV_CODE = '"+invCode+"' AND S.PACK_FLG = 'N' AND S.WAST_FLG = 'N' ";
		return sql;
	}
}
