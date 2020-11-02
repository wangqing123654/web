package com.javahis.ui.spc;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import jdo.ind.INDTool;
import jdo.ind.IndSysParmTool;
import jdo.spc.SPCDispenseAssignWithLiquidTool;
import jdo.spc.SPCDispenseCountWithLiquidHosStrTool;
import jdo.spc.SPCDispenseOutAssginNormalTool;
import jdo.spc.SPCSQL;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.javahis.ui.spc.util.ElectronicTagUtil;


/**
 * 
 * <p>
 * Title:סԺҩ����ҩ-������ҩ�˶�control
 * </p>
 * 
 * <p>
 * Description:סԺҩ����ҩ-������ҩ�˶�control
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
 * @author liyh 20121114
 * @version 1.0
 */
public class SPCDispenseAssignWithLiquidControl  extends TControl {

	//��ת�� ���ƴ���
	private static final int LIGHT_NUM = 3;
	//��Һ��˱�ʾ
	private static final String IS_CHECK_STR = "��";	
	private static final String SELECT_FLG_Y = "Y";

    // ������
    private TTable table_m;

    // ϸ����
    private TTable table_d;
    
    // ϸ����ѡ����
    private int row_d;
    
	
	/**��ʼʱ��*/
	TTextFormat start_date;
	
	/**����ʱ��*/
	TTextFormat end_date;
	
	/**����*/
//	TTextFormat station_id;
	
	/**ͳҩ��table*/
	TTable table_order;
	
	/**ͳҩ���ſؼ�*/
	TTextField order_id;
	
	/**ͳҩ���� ��ѯ�ؼ�*/
	TTextField order_id_query;

    public SPCDispenseAssignWithLiquidControl() {
        super();
    }

    /**��ʼ������*/
    public void onInit() {
        initPage();
    }
    
    /**��ʼ��������*/
    private void initPage() {
        // ��ʼ��TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        
		start_date = (TTextFormat) getComponent("START_DATE");
		end_date = (TTextFormat) getComponent("END_DATE");
		table_order = this.getTable("TABLE_ORDER");
		order_id = (TTextField) getComponent("INTGMED_NO");
		order_id_query = (TTextField) getComponent("INTGMED_NO_QUERY");
		
		// ��ʼ����ѯ����
		Timestamp date = SystemTool.getInstance().getDate();        
        this.setValue("START_DATE",date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        this.setValue("END_DATE",date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        onQuery();
    }

    /**��ѯ����*/
    public void onQuery() {
		TParm parm = new TParm();
		
		parm.setData("START_DATE", start_date.getValue());
		parm.setData("END_DATE", end_date.getValue());
		parm.setData("STATION_ID",this.getValueString("STATION_ID"));
		parm.setData("INTGMED_NO",order_id_query.getValue());
		
		TParm result = SPCDispenseOutAssginNormalTool.getInstance().query(parm);
		table_order.setParmValue(result);
    	
    }
    
	/**table_order �����¼�*/
	public void tableOrderClicked(){
       int row_m = table_order.getSelectedRow();
        if (row_m != -1) {
        	order_id.setValue((String)table_order.getItemData(row_m,"INTGMED_NO"));
    		onIntgmEdNo();    		
        }
	}
	
	/**ͳҩ���Ļس��¼�*/
	public void onIntgmEdNo() {
    	String intgmedNo = (String) getValue("INTGMED_NO");
		if (StringUtils.isNotBlank(intgmedNo)) {
			//��ѯ������ҩ���
			System.out.println("SPCDispenseAssignWithLiquidControl----------------131---sql"+SPCSQL.getOrderCodeInfoLiquidByPation(intgmedNo));
			TParm parm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getOrderCodeInfoLiquidByPation(intgmedNo)));
			table_m.setParmValue(parm);
			if (null != parm && parm.getCount() > 0) {
				String stationDesc = (String) parm.getData("STATION_DESC", 0);
				setValue("STATION_DESC", stationDesc);
				this.getTextField("INTGMED_NO").setEditable(false);
				//ƿǩ��ý���
			    //this.getTextField("BOTTLE_CODE").grabFocus();
				//��ת����
				this.getTextField("BIG_BOX_CODE").grabFocus();
			    table_m.setSelectedRow(0);
			    onTableMClicked();
			}			
		}
	}
	

    /**�����ת��*/
    public void onClearTurnEslId(){
    	setValue("BIG_BOX_CODE", "");
    	this.getTextField("BIG_BOX_CODE").grabFocus();
    }
    
    /**���淽��*/
    public void onSave() {

    }
    
    /**ȫѡ��ѡ��ѡ���¼�*/
    public void onCheckSelectAll() {
        table_m.acceptText();
        if (table_m.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table_m.getRowCount(); i++) {
            	table_m.setItem(i, "SELECT_FLG", true);
            }
        }
        else {
            for (int i = 0; i < table_m.getRowCount(); i++) {
            	table_m.setItem(i, "SELECT_FLG", false);
            }
        }
    }
    
    /**�˶�ƿǩ*/
    public void onBottleCode(){
    	table_m.acceptText();
    	String intgmedNo = getValueString("INTGMED_NO");
    	TParm parmM = table_m.getParmValue();
    	if(null == parmM && parmM.getCount()<1){
    		this.messageBox("��Һ��������Ϊ��");
    		this.getTextField("INTGMED_NO").setEditable(true);
    		this.getTextField("INTGMED_NO").grabFocus();
    		return ;
    	}
    	if(row_d < 0){
    		this.messageBox("��ѡ����Һ");
    		return ;
    	}
    	String caseNoM = parmM.getValue("CASE_NO", row_d);
    	String bottleCode = getValueString("BOTTLE_CODE").toUpperCase();
    	TTextField BOTTLE_CODE = (TTextField) getComponent("BOTTLE_CODE");
    	BOTTLE_CODE.select(0,bottleCode.length());
    	
    	//ͨ��ƿǩ��ѯ��Һ��ϸ
    	System.out.println("-----ͨ��ƿǩ��ѯ��Һ��ϸ----------sql:"+SPCSQL.getOrderCodeAssignLiquid(caseNoM, bottleCode));
    	TParm parmDParm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getOrderCodeAssignLiquid(caseNoM, bottleCode)));
    	if(null != parmDParm && parmDParm.getCount() > 0){
    		table_d.setParmValue(parmDParm);
    		row_d += 1;
    		table_m.setSelectedRow(row_d);
    	}
    	setValue("BOTTLE_CODE", "");
    	getTextField("BOTTLE_CODE").grabFocus(); 

    }
    
    /**���淽��*/   
    public void onAssignCode(){
    	onQuery();
    }
    
    /** ������(TABLE_M)�����¼�*/
    public void onTableMClicked() {
        row_d = table_m.getSelectedRow();
        if(row_d > -1){
        	getTextField("BOTTLE_CODE").grabFocus();
        	onBottleCode();
/*        	TParm parmM = table_m.getParmValue();
        	String linkNo = parmM.getValue("LINK_NO",row_d);
        	String caseNo = parmM.getValue("CASE_NO",row_d);
        	String intgmedNo = parmM.getValue("INTGMED_NO",row_d);
        	System.out.println("-----------203---sql: "+SPCSQL.getOrderCodeInfoDLiquidByPation(intgmedNo,caseNo,linkNo));
        	TParm parmD = new TParm(TJDODBTool.getInstance().select(SPCSQL.getOrderCodeInfoDLiquidByPation(intgmedNo,caseNo,linkNo)));
        	table_d.setParmValue(parmD);*/
        }
    }

    /**����ת��  */
    public void onBigBoxCode(){
    	String bigBoxCode = getValueString("BIG_BOX_CODE");
    	String stattionDesc = getValueString("STATION_DESC");
    	String intgmedNo = getValueString("INTGMED_NO");
    	if(StringUtils.isNotBlank(bigBoxCode)){
    		TParm parmM = table_m.getParmValue();
    		int count = parmM.getCount();
    		for (int i = 0; i < count; i++) {
				String selectFlg = parmM.getValue("SELECT_FLG", i);
				String turnEslId = parmM.getValue("TURN_ESL_ID", i);
				if(SELECT_FLG_Y.equalsIgnoreCase(selectFlg) && StringUtils.isBlank(turnEslId)){
					String caseNo = parmM.getValue("CASE_NO",i);
					String orderNo = parmM.getValue("ORDER_NO",i);
					String orderSeq = parmM.getValue("ORDER_SEQ",i);
					String startDttm = parmM.getValue("START_DTTM",i);
//					table_m.setItem(i, "TURN_ESL_ID", bigBoxCode);
					System.out.println("-------update turnEslId sql : "+SPCSQL.updateTrunElsIdDspnM(intgmedNo, caseNo, orderNo, orderSeq, startDttm, bigBoxCode));
					TParm resultParm = new TParm(TJDODBTool.getInstance().update(SPCSQL.updateTrunElsIdDspnM(intgmedNo, caseNo, orderNo, orderSeq, startDttm, bigBoxCode)));
				}
			}
    		ElectronicTagUtil.getInstance().login();
    		ElectronicTagUtil.getInstance().sendEleTag(bigBoxCode, stattionDesc, "", "", LIGHT_NUM);
    	}
    	
    }
    /**�õ�Table����*/
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**�õ�TextField����*/
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**�õ�ComboBox����*/
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**�õ�RadioButton����*/
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**�õ�CheckBox����*/
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**ȡ��SYS_FEE��Ϣ��������״̬����*/
    private void setSysStatus(String order_code) {
        TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
        String status_desc = "ҩƷ����:" + order.getValue("ORDER_CODE")
            + " ҩƷ����:" + order.getValue("ORDER_DESC")
            + " ��Ʒ��:" + order.getValue("GOODS_DESC")
            + " ���:" + order.getValue("SPECIFICATION");
        callFunction("UI|setSysStatus", status_desc);
    }

    /**��շ���*/
    public void onClear() {
       setValue("INTGMED_NO", "");
       setValue("STATION_DESC", "");
       setValue("BOTTLE_CODE", "");
       setValue("BIG_BOX_CODE", "");
       this.getTextField("INTGMED_NO").setEditable(true);
       table_d.removeRowAll();
       table_m.removeRowAll();
    }

    
    /**�õ�TextFormat����*/
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

    /**ҩ�������Ϣ*/
    private TParm getSysParm(){
        return IndSysParmTool.getInstance().onQuery();
    }


}
