package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

/**
 * 
 * <p>
 * Title: ������ҩ��ҩ�����
 * </p>
 * 
 * <p>
 * Description:  ������ҩ��ҩ�����
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
 * @author liyh 20121017
 * @version 1.0
 */
public class INDDisInNormalCabinetReginControl extends TControl {
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

/*        //bill_no��ý���
		TFrame tFrame = (TFrame) getComponent("UI");
		final TTextField mrField = (TTextField) getComponent("RX_NO");
		tFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				mrField.requestFocus();
			}
		});*/
		
	}

	/**ͳҩ���س��¼�*/
	public void onRxNo() {
		TParm parm = fillNParm(new TParm());
    	( (TTextField) getComponent("BIG_BOX_CODE")).grabFocus();
		this.setValue("APP_ORG", "1����");
		this.setValue("TO_ORG", "סԺҩ��");
		this.setValue("REQ_TYPE", "��ҩ����");
		table_n.setParmValue(parm);
	}
	
	/**��ת��س��¼�*/
	public void onBigBoxCode() {
		table_n.removeRowAll();
		TParm parm = fillNParm(new TParm());
		table_y.setParmValue(parm);
	}
	
	/**ҩƷ���ƻس��¼�*/
	public void onContainerEnter() {
		
		elc_label.setText("00004");
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

		return;
	}

	
	//ѡ����
	public void onTPanlClicked() {
		table_y.setParmValue(fillYParm(new TParm()));
	}
	
	/**δ����table������װ*/
	private TParm fillNParm(TParm parm) {
		//MED_NAME;SPEC;BETCH_NO;VALID_DATE;PRICE;AMOUNT;UNIT
		parm.setData("ORDER_CODE", 0, "0102AB");
		parm.setData("ORDER_DESC", 0, "Σ����������������Ƭ");
		parm.setData("SPECIFICATION", 0, "75mg*10");
		parm.setData("AMOUNT", 0, "10");
		parm.setData("UNIT", 0, "Ƭ");
		parm.setData("PRICE", 0, "5.6000");
		
		parm.setData("ORDER_CODE", 1, "0102AB");
		parm.setData("ORDER_DESC", 1, "���");
		parm.setData("SPECIFICATION", 1, "10mg*5");
		parm.setData("AMOUNT", 1, "10");
		parm.setData("UNIT", 1, "֧");
		parm.setData("PRICE", 1, "320.0000");
		
		parm.setData("ORDER_CODE", 2, "0102DB");
		parm.setData("ORDER_DESC", 2, "��̫��ע��Һ");
		parm.setData("SPECIFICATION", 2, "20mg*10");
		parm.setData("AMOUNT", 2, "10");
		parm.setData("UNIT", 2, "֧");
		parm.setData("PRICE", 2, "200.2000");
		
		parm.setCount(3);
		return parm;
	}
	

	/**δ����table������װ*/
	private TParm fillYParm(TParm parm) {
		//MED_NAME;SPEC;BETCH_NO;VALID_DATE;PRICE;AMOUNT;UNIT
		parm.setData("TRUNK_NO", 0, "1");
		parm.setData("ORDER_CODE", 0, "0102AB");
		parm.setData("ORDER_DESC", 0, "Σ����������������Ƭ");
		parm.setData("SPECIFICATION", 0, "75mg*10");
		parm.setData("AMOUNT", 0, "10");
		parm.setData("UNIT", 0, "Ƭ");
		parm.setData("PRICE", 0, "5.6000");
		
		parm.setData("TRUNK_NO", 1, "1");
		parm.setData("ORDER_CODE", 1, "0102AB");
		parm.setData("ORDER_DESC", 1, "��˾ƥ�ֳ���Ƭ");
		parm.setData("SPECIFICATION", 1, "10mg*5");
		parm.setData("AMOUNT", 1, "10");
		parm.setData("UNIT", 1, "Ƭ");
		parm.setData("PRICE", 1, "6.5880");
		
		parm.setData("TRUNK_NO", 2, "1");
		parm.setData("ORDER_CODE", 2, "0102DB");
		parm.setData("ORDER_DESC", 2, "��̫��ע��Һ");
		parm.setData("SPECIFICATION", 2, "20mg*10");
		parm.setData("AMOUNT", 2, "10");
		parm.setData("UNIT", 2, "֧");
		parm.setData("PRICE", 2, "200.2000");
		
		parm.setCount(3);
		return parm;
	}
	
	
	
    /**��շ���*/
    public void onClear() {
        table_n.removeRowAll();
        table_y.removeRowAll();
        
        this.setValue("RX_NO", "");
		this.setValue("BIG_BOX_CODE", "");
        this.setValue("APP_ORG", "");
		this.setValue("TO_ORG", "");
		this.setValue("REQ_TYPE", "");
        table_y_parm = new TParm();
    }
    
    /**��ѯ*/
    public void onQuery() {
    	onRxNo() ;
    }
}
