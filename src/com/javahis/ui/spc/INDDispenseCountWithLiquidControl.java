package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

/**
 * 
 * <p>
 * Title:סԺҩ����ͨҩƷͳҩ
 * </p>
 * 
 * <p>
 * Description: סԺҩ����ͨҩƷͳҩ
 * </p>
 * 
 * <p>
 * Copyright (c) BlueCore 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author liuzhen 20121015
 * @version 1.0
 */
public class INDDispenseCountWithLiquidControl extends TControl {
	// δȡҩ
	TTable table_n ;

	// ��ȡҩ
	TTable table_y ;
	
	//ͳҩ��
	private TTextField bill_no;
	
	//����
	private TTextField area;
	
	//��ת��
	private TTextField trunk_no;
	
	//��λ���ӱ�ǩ
	private TTextField elc_label;
	
	//��ȡҩtable parm
	private TParm table_y_parm= new TParm();

	
	/**��ʼ������*/
	public void onInit() {

		table_n = (TTable) getComponent("TABLE_N");
		table_y = (TTable) getComponent("TABLE_Y");
		
		bill_no = (TTextField) getComponent("BILL_NO");
		area = (TTextField) getComponent("AREA");
        trunk_no = (TTextField) getComponent("TRUNK_NO");
        elc_label = (TTextField) getComponent("ELC_LABEL");
        
/*        //bill_no��ý���
		TFrame tFrame = (TFrame) getComponent("UI");
		final TTextField mrField = (TTextField) getComponent("BILL_NO");
		tFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				mrField.requestFocus();
			}
		});*/
		
	}

	/**ͳҩ���س��¼�*/
	public void onBillEnter() {
		TParm parm = fillNParm(new TParm());
		table_n.setParmValue(parm);
		bill_no.setText("00001");
		bill_no.setEditable(false);
		trunk_no.grabFocus();
		area.setText("7����");
	}
	
	/**��ת��س��¼�*/
	public void onTrunkEnter() {
		trunk_no.setText("000020");
		elc_label.grabFocus();
	}
	
	/**ҩƷ���ƻس��¼�*/
	public void onContainerEnter() {
		String a = getValueString("ELC_LABEL");
		elc_label.setText(a);
		int count = table_n.getParmValue().getCount();

		for (int i = 0; i < count; i++) {
			//TRUNK_NO;MED_NAME;SPEC;BETCH_NO;VALID_DATE;PRICE;AMOUNT;UNIT
			TParm parm = table_n.getParmValue().getRow(i);
			
			table_y_parm.addData("TRUNK_NO", trunk_no.getText());
			
			table_y_parm.addData("MED_NAME", parm.getValue("MED_NAME"));
			table_y_parm.addData("SPEC", parm.getValue("SPEC"));
			table_y_parm.addData("BETCH_NO", parm.getValue("BETCH_NO"));
			table_y_parm.addData("VALID_DATE", parm.getValue("VALID_DATE"));
			table_y_parm.addData("AMOUNT", parm.getValue("AMOUNT"));
			table_y_parm.addData("UNIT", parm.getValue("UNIT"));
			table_y_parm.addData("PRICE", parm.getValue("PRICE"));
			
			table_n.removeRow(i);
			
			table_y.setParmValue(fillYParm(new TParm()));
			
			break;
		}
		String bottleCode = getValueString("ELC_LABEL");
    	TTextField BOTTLE_CODE = (TTextField) getComponent("ELC_LABEL");
    	BOTTLE_CODE.select(0,bottleCode.length());
    	
		return;
	}

	
	//ѡ����
	public void onTPanlClicked() {
		table_y.setParmValue(fillYParm(new TParm()));
	}
	
	/**δ����table������װ*/
	private TParm fillNParm(TParm parm) {
		//MED_NAME;SPEC;BETCH_NO;VALID_DATE;PRICE;AMOUNT;UNIT
		
		parm.setData("MED_NAME", 0, "Σ����������������Ƭ");
		parm.setData("SPEC", 0, "25mg*7");
		parm.setData("BETCH_NO", 0, "00002");
		parm.setData("VALID_DATE", 0, "2015-10-13");
		parm.setData("AMOUNT", 0, "10");
		parm.setData("UNIT", 0, "Ƭ");
		parm.setData("PRICE", 0, "12.7620");
		
		parm.setData("MED_NAME", 1, "��˾ƥ�ֳ���Ƭ");
		parm.setData("SPEC", 1, "75mg*20");
		parm.setData("BETCH_NO", 1, "00002");
		parm.setData("VALID_DATE", 1, "2015-10-13");
		parm.setData("AMOUNT", 1, "10");
		parm.setData("UNIT", 1, "Ƭ");
		parm.setData("PRICE", 1, "6.4590");
		
		parm.setData("MED_NAME", 2, "��̫��ע��Һ");
		parm.setData("SPEC", 2, "100mg*10");
		parm.setData("BETCH_NO", 2, "00002");
		parm.setData("VALID_DATE", 2, "2015-10-13");
		parm.setData("AMOUNT", 2, "10");
		parm.setData("UNIT", 2, "Ƭ");
		parm.setData("PRICE",2, "200.0000");
		
		parm.setCount(3);
		return parm;
	}
	
	
	/**�ѳ���table������װ*/
	private TParm fillYParm(TParm parm) {

		return this.table_y_parm;
	}
	
	
    /**��շ���*/
    public void onClear() {
        table_n.removeRowAll();
        table_y.removeRowAll();
        
        trunk_no.setText("");
        
        bill_no.setText("");
        bill_no.setEditable(true);
        
        elc_label.setText("");
        area.setText("");
        
        table_y_parm = new TParm();
    }
    
    /**��ѯ*/
    public void onQuery() {
    	onBillEnter();
    }
}
