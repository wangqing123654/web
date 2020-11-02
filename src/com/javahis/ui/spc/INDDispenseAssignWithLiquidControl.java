package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.ind.INDSQL;
import jdo.ind.INDTool;
import jdo.ind.IndSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeTool;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

public class INDDispenseAssignWithLiquidControl  extends TControl {


    // ������
    private TTable table_m;

    // ϸ����
    private TTable table_d;

    // ������ѡ����
    private int row_m;

    // ϸ����ѡ����
    private int row_d;

    // ϸ�����
    private int seq;

    // ���ؽ����
    private TParm resultParm;



    // ��������
    private String request_type;

    // ���뵥��
    private String request_no;

    // ʹ�õ�λ
    private String u_type;

    // ���ⲿ��
    private String out_org_code;

    // ��ⲿ��
    private String in_org_code;

    // �Ƿ����
    private boolean out_flg;

    // �Ƿ����
    private boolean in_flg;

    // ��ⵥ��
    private String dispense_no;

    // ȫԺҩ�ⲿ����ҵ����
    private boolean request_all_flg = true;

    public INDDispenseAssignWithLiquidControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
/*    	TFrame tFrame  = (TFrame)getComponent("UI");
    	final TTextField mrField = (TTextField)getComponent("ASSIGN_CODE");
    	tFrame.addWindowListener(new java.awt.event.WindowAdapter() {
    	            public void windowOpened(java.awt.event.WindowEvent evt) {
    	             mrField.requestFocus();
    	            }
    	});*/
        initPage();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        table_m.setParmValue(iniParmM());
    }

    /**
     * ��շ���
     */
    public void onClear() {
       setValue("ASSIGN_CODE", "");
       setValue("BIG_BOX_CODE", "");
       setValue("REGION_CODE", "");
       setValue("BOTTLE_CODE", "");
       table_d.removeRowAll();
       table_m.removeRowAll();
    }

    /**
     * ���淽��
     */
    public void onSave() {

    }
    
    /**
     * �˶�ƿǩ 
     */
    public void onBottleCode(){
    	TParm parm = table_m.getParmValue();
    	if(null == parm && parm.getCount()<1){
    		this.messageBox("�������ݲ���Ϊ��");
    	}
    	String bottleCode = getValueString("BOTTLE_CODE");
    	TTextField BOTTLE_CODE = (TTextField) getComponent("BOTTLE_CODE");
    	BOTTLE_CODE.select(0,bottleCode.length());
    	
     	String a = getValueString("BIG_BOX_CODE");
    	setValue("BIG_BOX_CODE",a);
    	for(int i =  0 ; i < parm.getCount(); i++){
    		String remarkStr = (String) parm.getData("REMARK", i);
    		if(null == remarkStr || "".equals(remarkStr)){
    			table_m.setItem(i, "REMARK", "��");
    			break;
    		}
    	}
    }
    /**
     * ���淽��
     */   
    public void onAssignCode(){
    	String assignCode = getValueString("ASSIGN_CODE");
    	if(null == assignCode || "".equals(assignCode)){
    		this.messageBox("��ҩ���Ų���Ϊ��");
    		return ;
    	}
//    	 ( (TTextField) getComponent("BIG_BOX_CODE")).grabFocus();
    	this.setValue("REGION_CODE", "1����");
    	 table_m.setParmValue(iniParmM());
    	 table_m.setSelectedRow(0);
    }
    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        row_d = table_m.getSelectedRow();
        TParm a = getDispenseDParm(new TParm());
        table_d.setParmValue(a);
    }

    /**
     * ��ϸ���(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
  
    }
        
	/**
	 * ��ϸ���(TABLE_D)�����¼�
	 */
	public void onClickM() {
		table_m.acceptText();
		int i = table_m.getSelectedRow();
//		table_m.setSelectedRow(i+1);
		if (i == 0) {
			//table_m.setItem(i, "REMARK", "��");
			table_m.setItem(i, "BOXCODE", "01�ź�");
			table_m.setSelectedRow(1);
		}
		if (i == 1) {
//			table_m.setItem(i, "BOXCODE", "02�ź�");
			table_m.setSelectedRow(2);
		}
		if (i == 2) {
//			table_m.setItem(i, "BOXCODE", "03�ź�");
			table_m.setSelectedRow(3);
		}
		if (i == 3) {
//			table_m.setItem(i, "BOXCODE", "04�ź�");
			table_m.setSelectedRow(4);
		}
		if (i == 4) {
//			table_m.setItem(i, "BOXCODE", "05�ź�");

		}
	
	}

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ��TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
    }




    /**
     * ���������Ϣ
     *
     * @param parm
     * @return
     */
    private TParm iniParmM() {
        // ҩ�������Ϣ
        TParm parmM = new TParm();
        //        MR_NO;ORG_CODE;BED_NO;PATION_NAME;SEX;AGE;ALL_ORDER_NAME;TIMERS;BOX_CODE;REMARK
        parmM.setData("MR_NO",0, "000000385144");
        parmM.setData("ORG_CODE",0, "��һ��");
        parmM.setData("BED_NO", 0,"0876");
        parmM.setData("PATION_NAME",0, "����");
        parmM.setData("SEX",0, "��");
        parmM.setData("AGE", 0,"78");
        parmM.setData("ALL_ORDER_NAME",0, "����Ӫ��Һ1");
        parmM.setData("TIMERS",0, "09:00");
        parmM.setData("BOX_CODE",0, "");
        parmM.setData("REMARK",0, "");
      
        parmM.setData("MR_NO",1, "000000385144");
        parmM.setData("ORG_CODE",1, "��һ��");
        parmM.setData("BED_NO", 1,"0876");
        parmM.setData("PATION_NAME",1, "����");
        parmM.setData("SEX",1,"��");
        parmM.setData("AGE", 1,"78");
        parmM.setData("ALL_ORDER_NAME",1, "����Ӫ��Һ2");
        parmM.setData("TIMERS",1, "13:00");
        parmM.setData("BOXCODE",1, "");
        parmM.setData("REMARK",1, "");
        
        parmM.setData("MR_NO",2, "000000385144");
        parmM.setData("ORG_CODE",2, "��һ��");
        parmM.setData("BED_NO", 2,"2876");
        parmM.setData("PATION_NAME",2, "����");
        parmM.setData("SEX",2, "��");
        parmM.setData("AGE", 2,"78");
        parmM.setData("ALL_ORDER_NAME",2, "����Ӫ��Һ3");
        parmM.setData("TIMERS",2, "18:00");
        parmM.setData("BOXCODE",2, "");
        parmM.setData("REMARK",2, "");
        
        parmM.setData("MR_NO",3, "000000385236");
        parmM.setData("ORG_CODE",3, "��һ��");
        parmM.setData("BED_NO", 3,"3873");
        parmM.setData("PATION_NAME",3, "����");
        parmM.setData("SEX",3, "��");
        parmM.setData("AGE", 3,"88");
        parmM.setData("ALL_ORDER_NAME",3, "����Ӫ��Һ1");
        parmM.setData("TIMERS",3, "13��00");
        parmM.setData("BOXCODE",3, "");
        parmM.setData("REMARK",3, "");
        
        parmM.setData("MR_NO",4, "000000385287");
        parmM.setData("ORG_CODE",4, "��һ��");
        parmM.setData("BED_NO", 4,"4876");
        parmM.setData("PATION_NAME",4, "��Ӣ");
        parmM.setData("SEX",4, "Ů");
        parmM.setData("AGE", 4,"77");
        parmM.setData("ALL_ORDER_NAME",4, "����Ӫ��Һ1");
        parmM.setData("TIMERS",4, "09:00");
        parmM.setData("BOXCODE",4, "");
        parmM.setData("REMARK",4, "");
        parmM.setCount(5);
        return parmM;
    }

    /**
     * �����ϸ��Ϣ
     *
     * @param parm
     * @return
     */
    private TParm getDispenseDParm(TParm parm) {
        TParm parmD = new TParm();
        //ORDER_DESC;SPECIFICATION;QTY;UNIT;BATCH_NO;VALID_DATE;PRICE
        parmD.setData("ORDER_DESC",0, "�����Ȼ�����ˮ");
        parmD.setData("SPECIFICATION", 0,"500ml");
        parmD.setData("QTY",0, "1");
        parmD.setData("UNIT",0, "ƿ");
        parmD.setData("BATCH_NO", 0,"2012042");
        parmD.setData("VALID_DATE",0, "2013-10-10");
        parmD.setData("PRICE",0, "12.00");
 
        parmD.setData("ORDER_DESC",1, "������ע��Һ");
        parmD.setData("SPECIFICATION", 1,"10%250ML");
        parmD.setData("QTY",1, "1");
        parmD.setData("UNIT",1, "��");
        parmD.setData("BATCH_NO", 1,"2112342");
        parmD.setData("VALID_DATE",1, "2013-11-11");
        parmD.setData("PRICE",1, "14.00");
        
        parmD.setData("ORDER_DESC",2, "������ע��Һ");
        parmD.setData("SPECIFICATION", 2,"5%150ML");
        parmD.setData("QTY",2, "2");
        parmD.setData("UNIT",2, "ƿ");
        parmD.setData("BATCH_NO", 2,"20130101");
        parmD.setData("VALID_DATE",2, "2013-12-22");
        parmD.setData("PRICE",2, "24.00");
        return parmD;
    }


    public void onBigBoxCode(){
    	String a = getValueString("BIG_BOX_CODE");
    	setValue("BIG_BOX_CODE",a);
    }
    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * �õ�CheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * ȡ��SYS_FEE��Ϣ��������״̬����
     * @param order_code String
     */
    private void setSysStatus(String order_code) {
        TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
        String status_desc = "ҩƷ����:" + order.getValue("ORDER_CODE")
            + " ҩƷ����:" + order.getValue("ORDER_DESC")
            + " ��Ʒ��:" + order.getValue("GOODS_DESC")
            + " ���:" + order.getValue("SPECIFICATION");
        callFunction("UI|setSysStatus", status_desc);
    }

    /**
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

    /**
     * ҩ�������Ϣ
     * @return TParm
     */
    private TParm getSysParm(){
        return IndSysParmTool.getInstance().onQuery();
    }


}
