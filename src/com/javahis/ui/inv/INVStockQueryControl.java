package com.javahis.ui.inv;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;

public class INVStockQueryControl extends TControl{
	
	private TTable table;
	
	/**
	 * ��ʼ��
	 */
	public void init(){
		super.init();
		initPage();

	}

	private void initPage() {
		//���TABLE����
        table=(	TTable)this.getComponent("TABLE");
         
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "OTH");
		// ���õ����˵�
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parm);
		// ������ܷ���ֵ����
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		
		
	}
	
	/**
     * �õ�TextField����
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
    
    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String order_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("INV_CODE").setValue(order_code);
        String order_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("INV_DESC").setValue(order_desc);
    }
    
    /**
     * ��ѯ
     */
    public void onQuery(){
    	String sql=" SELECT A.INV_CODE,C.ORG_DESC, B.INV_CHN_DESC, B.DESCRIPTION, " +
    			"  SUM (A.STOCK_QTY) QTY,D.SUP_CHN_DESC " +
    			"  FROM INV_STOCKM A, INV_BASE B,INV_ORG C ,SYS_SUPPLIER D" +
    			"  WHERE A.INV_CODE = B.INV_CODE " +
    			"  AND  A.ORG_CODE = C.ORG_CODE" +
    			"  AND B.UP_SUP_CODE=D.SUP_CODE ";
    			
    	if(this.getValueString("INV_CODE")!=null&&!"".equals(this.getValueString("INV_CODE"))){
    		sql += " AND A.INV_CODE='"+this.getValueString("INV_CODE")+"'";
    	}
    	if(this.getValueString("ORG_CODE")!=null&&!"".equals(this.getValueString("ORG_CODE"))){
    		sql += " AND A.ORG_CODE='"+this.getValueString("ORG_CODE")+"'";
    	}
    	
    	sql +=" GROUP BY A.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION,D.SUP_CHN_DESC,C.ORG_DESC";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()<0){
    		this.messageBox("û��Ҫ��ѯ������");
    		this.onClear() ;
    		return;
    	}
    	table.setParmValue(parm);
    }
    /**
     * ��ӡ
     */
    public void onPrint(){
    	table.acceptText();
    	TParm tableParm = table.getParmValue();
    	if(tableParm.getCount()<0){
    		this.messageBox("û��Ҫ��ӡ������");
    	}
    	Timestamp datetime = SystemTool.getInstance().getDate();
    	TParm data = new TParm();
    	data.setData("TITLE", "TEXT", "���ʿ��ͳ�Ƶ�");
    	data.setData("USER", "TEXT", "�Ʊ��ˣ�"+Operator.getName());
    	data.setData("DATE", "TEXT", "�Ʊ����ڣ�"+datetime.toString().substring(0, 10).replace('-', '/'));
    	
    	TParm parm = new TParm();
    	for(int i=0;i<tableParm.getCount();i++){
    		parm.addData("ORG_DESC", tableParm.getValue("ORG_DESC", i));
    		parm.addData("INV_CODE", tableParm.getValue("INV_CODE", i));
    		parm.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i));
    		parm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i));
    		parm.addData("QTY", tableParm.getValue("QTY", i));
    		parm.addData("MAN_CHN_DESC", tableParm.getValue("SUP_CHN_DESC", i));
    	}
    	
    	parm.setCount(parm.getCount("INV_CODE"));  
    	 parm.addData("SYSTEM", "COLUMNS", "ORG_DESC");
        parm.addData("SYSTEM", "COLUMNS", "INV_CODE");
        parm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
        parm.addData("SYSTEM", "COLUMNS", "QTY");
        parm.addData("SYSTEM", "COLUMNS", "MAN_CHN_DESC");
        
        data.setData("TABLE",parm.getData());
       	
        this.openPrintWindow("%ROOT%\\config\\prt\\inv\\INVStockQueryPrint.jhw", data);
    	
    }
    

    /**
     * ��շ���
     */
	
	public void onClear() {
		String clearString ="INV_CODE;INV_DESC;ORG_CODE"; 	
        this.clearValue(clearString);
        table.removeRowAll();
	}
	

}
