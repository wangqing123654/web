package com.javahis.ui.inv;

import jdo.inv.INVSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

/**
 * <p>Title: ��Ӧ�ҳ���ѡ����Ź����������</p>
 *
 * <p>Description: ��Ӧ�ҳ���ѡ����Ź����������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.3.9
 * @version 1.0
 */
public class INVSupPackStockDDChooseControl
    extends TControl {

    /**
     * ��
     */
    private TTable table;

    private String org_code = "";

    private String pack_code = "";

    private String pack_desc = "";

    private double qty = 0;

    public INVSupPackStockDDChooseControl() {
    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        table = (TTable) callFunction("UI|TABLE|getThis");
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        org_code = parm.getValue("ORG_CODE");
        pack_code = parm.getValue("INV_CODE");
        pack_desc = parm.getValue("INV_CHN_DESC");
        qty = parm.getDouble("QTY");
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INVSQL.getINvSupPackChoose(org_code, pack_code)));
        table.setParmValue(result);
        this.setValue("QTY", qty);
        this.setValue("PACK_DESC", pack_desc);
    }

    /**
     * ���ط���
     */
    public void onReturn() {
        table.acceptText();
        TParm result = table.getParmValue();
        TParm resultParm = new TParm();
        for (int i = result.getCount("PACK_SEQ_NO") - 1; i >= 0; i--) {
            if ("Y".equals(table.getItemString(i, "SELECT_FLG"))) {
                resultParm.addData("INVSEQ_NO",
                                   result.getRow(i).getInt("PACK_SEQ_NO"));
                resultParm.addData("ORG_CODE",
                                   result.getRow(i).getValue("ORG_CODE"));
                resultParm.addData("COST_PRICE",
                                   result.getRow(i).getDouble("COST_PRICE"));
                resultParm.addData("QTY", 1);
                resultParm.addData("INV_CODE", pack_code);
                resultParm.addData("INV_CHN_DESC", pack_desc);
                resultParm.addData("BARCODE", result.getRow(i).getValue("BARCODE"));
                resultParm.addData("PACK_BATCH_NO", result.getRow(i).getInt("PACK_BATCH_NO"));
            }
        }
        double return_qty = resultParm.getCount("INVSEQ_NO");
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
		
		String barcode = this.getValueString("INV_CODE");
		
		if(barcode!=null&&barcode.length()>0){
			TParm result = table.getParmValue();
			for(int i = result.getCount("PACK_SEQ_NO") - 1; i >= 0; i--){
				if (barcode.equals(result.getData("BARCODE", i))) {
					table.setItem(i, "SELECT_FLG", "Y");
				}
			}

		}
		
		((TTextField)getComponent("INV_CODE")).setValue("");
	}

}
