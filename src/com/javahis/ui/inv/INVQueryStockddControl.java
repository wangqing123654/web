package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

public class INVQueryStockddControl extends TControl{
	public INVQueryStockddControl(){}
	private static TTable tableM;
	private static TTable tableD;
	/**
	 * ���ǰѡ�е��к�
	 */
	private int selRow = -1;
	/**
	 * ��ʼ��
	 */
	public void init(){
		super.init();
		initPage();

	}
	
	/**
     * ��ʼ��������
     */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
        			date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		//���TABLE����
        tableM = (TTable) getComponent("TABLE1");
		tableD = (TTable) getComponent("TABLE2");
		TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "OTH");
		// ���õ����˵�
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parm);
		// ������ܷ���ֵ����
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        
     // ����tableע�ᵥ���¼�����
		this.callFunction("UI|TABLE1|addEventListener", "TABLE1->"
				+ TTableEvent.CLICKED, this, "ontableMClicked");
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
		String sql = "SELECT   A.INV_CODE, B.INV_CHN_DESC INV_DESC, B.DESCRIPTION," +
				" SUM (A.STOCK_QTY) QTY, C.MAN_CHN_DESC MAN_DESC," +
				" D.SUP_CHN_DESC SUP_DESC" +
				" FROM INV_STOCKDD A, INV_BASE B, SYS_MANUFACTURER C, SYS_SUPPLIER D" +
				" WHERE A.INV_CODE = B.INV_CODE" +
				" AND B.MAN_CODE = C.MAN_CODE" +
				" AND B.SUP_CODE = D.SUP_CODE" +
				" AND A.WAST_FLG = 'Y'";
				
        // ��ѯʱ��
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
        	String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "START_DATE")), "yyyyMMdd")+" 00:00:00";
            String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "END_DATE")), "yyyyMMdd")+" 23:59:59";
	         
//	         System.out.println(startTime);
//	         System.out.println(endTime);
	         
	         sql += " AND A.OUT_DATE BETWEEN TO_DATE('"+startTime+ "','yyyymmdd hh24:mi:ss') " + "AND TO_DATE('" + endTime
				+ "','yyyymmdd hh24:mi:ss')";
        }else{
        	this.messageBox("���� ����Ϊ��");
        	return;
        }
        //����
        if (!"".equals(this.getValueString("INV_CODE"))) {
        	String invCode=TypeTool.getString(getValue("INV_CODE"));
            sql +=" AND A.INV_CODE ="+ invCode; 
        }else {
        	 sql +=" AND A.INV_CODE LIKE '08%'";
		}
        sql += " GROUP BY A.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION,C.MAN_CHN_DESC,D.SUP_CHN_DESC";
//        System.out.println("11111==="+sql);
        TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if(selParm.getCount()<0){
        	this.messageBox("û��Ҫ��ѯ������");
        	return;
        }
      //����table�ϵ�����
        tableM.setParmValue(selParm);
	}
	
	/**
	 * ���������table�¼�
	 */
	public void ontableMClicked(int row){
		// ��ǰѡ�е��к�
		selRow = row;
		TParm parm = tableM.getParmValue();
		TParm tableDate = parm.getRow(selRow);
		String invCode=tableDate.getValue("INV_CODE");
		TParm parmD=gettableD(invCode);
		tableD.setParmValue(parmD);
	}
	
	/**
	 * �õ�ϸ���ֵ
	 * @param invCode
	 * @return
	 */
	private TParm gettableD(String invCode){
		String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd")+" 00:00:00";
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd")+" 23:59:59";
		String sql="SELECT A.RFID, B.INV_CHN_DESC INV_DESC, A.OUT_DATE,  A.WAST_ORG, A.MR_NO," +
				" A.CASE_NO, A.RX_SEQ, A.SEQ_NO, A.ADM_TYPE" +
				" FROM INV_STOCKDD A, INV_BASE B" +
				" WHERE  A.INV_CODE = B.INV_CODE AND A.WAST_FLG = 'Y' AND  A.INV_CODE='"+invCode+"'" +
				" AND A.OUT_DATE BETWEEN TO_DATE('"+startTime+ "','yyyymmdd hh24:mi:ss') " + "AND TO_DATE('" + endTime
				+ "','yyyymmdd hh24:mi:ss')";
//		System.out.println(sql);
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		return selParm;
		
	}
	
	/**
     * ��ӡ����
     */
    public void onPrint() {
    	tableM = this.getTable("TABLE1");
    	if (tableM.getRowCount() <= 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
    	//��ͷ  
    	Timestamp datetime = SystemTool.getInstance().getDate();
    	TParm data = new TParm();
    	String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd")+" 00:00:00";
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd")+" 23:59:59";
    	data.setData("TITLE", "TEXT", "��������ͳ�Ʊ�");
    	data.setData("QUERY", "TEXT", "��ѯ���ڣ�"+startTime+"��"+ endTime);
    	data.setData("DATE", "TEXT", "�Ʊ�����: " +
                datetime.toString().substring(0, 10).replace('-', '/'));
    	data.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
    	//���� 
    	TParm parm = new TParm();
    	TParm tableParm = tableM.getParmValue();
    	for(int i=0; i<tableParm.getCount(); i++){
    		parm.addData("INV_CODE", tableParm.getValue("INV_CODE",i));
    		parm.addData("INV_DESC", tableParm.getValue("INV_DESC",i));
    		parm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION",i));
    		parm.addData("QTY", tableParm.getValue("QTY",i));
    		parm.addData("MAN_DESC", tableParm.getValue("MAN_DESC",i));
    		parm.addData("SUP_DESC", tableParm.getValue("SUP_DESC",i));
    	}
    	parm.setCount(parm.getCount("INV_CODE"));
    	parm.addData("SYSTEM", "COLUMNS", "INV_CODE");
    	parm.addData("SYSTEM", "COLUMNS", "INV_DESC");
    	parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
    	parm.addData("SYSTEM", "COLUMNS", "QTY");
    	parm.addData("SYSTEM", "COLUMNS", "MAN_DESC");
    	parm.addData("SYSTEM", "COLUMNS", "SUP_DESC");
  
    	data.setData("TABLE",parm.getData());

    	this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVQueryStockddPrint.jhw", data);
    }
	
	/**
     * ��շ���
     */
	
	public void onClear() {
		String clearString ="INV_CODE;END_DATE;START_DATE;INV_DESC"; 
		this.clearValue(clearString);

		// ��ʼ����ѯ����
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
        			date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        tableM.removeRowAll();
        tableD.removeRowAll();
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
    
	

}
