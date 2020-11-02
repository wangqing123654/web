package com.javahis.ui.inv;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.bil.BILSysParmTool;
import jdo.inv.INVsettlementTool;
import jdo.spc.IndAgentTool;
import jdo.spc.SPCSettleAccountsTool;
import jdo.spc.SPCSysFeeTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.dom4j.Document;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.ExportXmlUtil;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ���ʽ���
 * </p>
 *
 * <p>
 * Description: ���ʽ���     
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 *
 * <p>
 * Company: BLUECORE
 * </p>
 *
 * @author chenx 2013.07.19
 * @version 4.0
 */
public class INVAccountControl extends TControl {
	
	public TTable table;
	
	TParm  accountResult   ;
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ��ʼ����ѯ����
		this.setValue("START_DATE",
				getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
						SystemTool.getInstance().getDate(), "yyyyMMdd"))));
		Timestamp rollDay = StringTool.rollDate(getDateForInitLast(SystemTool
				.getInstance().getDate()), -1);
		this.setValue("END_DATE", rollDay);
		 
		setValue("REGION_CODE", Operator.getRegion());
		
		TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "OTH");
		// ���õ����˵�
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parm);
        // ������ܷ���ֵ����
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		
		table = this.getTable("TABLE");
		
		
	}
	/**
	 * ������ʷ��¼��ѯ
	 */
	public  void  onHistoryQuery(){
		TParm  parmD = this.getAccountNoByDate() ;
		if(parmD.getErrCode()<0){
			return  ;
		}
		TTable  tableD = (TTable)this.getComponent("TABLED");
		tableD.setParmValue(parmD) ;
	}
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		String supCode = getValueString("SUP_CODE");
		if(supCode == null || supCode.equals("")){
			this.messageBox("��ѡ��Ӧ��");
			return ;
		}
		TParm searchParm = getSearchParm();  //�����ѯ������
		//�������� result
		accountResult = INVsettlementTool.getInstance().getAccountData(searchParm) ;     
		
		
		TParm newParm = new TParm() ;
		 
		//�ɹ��ܽ��
		double sumTotAmt =  0 ;
		int count = accountResult.getCount() ;
		int i = 0 ;
		for(int j = 0;  j < count ; j++){
			TParm rowParm = accountResult.getRow(j);
			
			//��־�Ƿ��ڹ�Ӧ�̶�Ӧ����Ʒ�����true��
			boolean b = false;
			String invCode = rowParm.getValue("INV_CODE") ;
			TParm sParm = new TParm();
			sParm.setData("SUP_CODE",supCode);
			sParm.setData("INV_CODE",invCode)  ;
			TParm invAgenTParm =  INVsettlementTool.getInstance().getAgentInvCode(sParm) ;
			if(invAgenTParm.getCount()<0){
				this.messageBox("��Ӧ�̶�ӦҩƷΪ��") ;
				return  ;
			}
				TParm indRowParm = invAgenTParm.getRow(0) ;
				String agtInvCode = indRowParm.getValue("INV_CODE");
				if(invCode.equals(agtInvCode)){
					b = true;
				
				}
			if(b){
				//���ʴ���
				newParm.setData("INV_CODE",i,invCode);
				//��������
				newParm.setData("INV_DESC",i,rowParm.getValue("INV_CHN_DESC"));
				//���
				newParm.setData("DESCRIPTION",i,rowParm.getValue("DESCRIPTION"));
				//��������
				newParm.setData("QTY",i,rowParm.getValue("QTY"));
				//��λ
				newParm.setData("UNIT_CHN_DESC",i,rowParm.getValue("UNIT_CHN_DESC"));
				//�ɹ�����
				newParm.setData("OWN_PRICE",i,rowParm.getDouble("OWN_PRICE"));
				//�ɹ����
				newParm.setData("TOT_AMT",i,rowParm.getDouble("TOT_AMT"));
				sumTotAmt +=rowParm.getDouble("TOT_AMT") ;
				//��������
				newParm.setData("ORG_DESC",i,rowParm.getValue("ORG_DESC"));
				i++;
			}
		}
		//�ɹ��ܽ��
		setValue("SUM_TOT_AMT", StringTool.round(sumTotAmt,2));
		//�ϼƱ���
		setValue("COUNT", count);
		table.setParmValue(newParm);
	}
   /**
    * ��ȡ��ѯ��������
    * @return
    */
	private TParm getSearchParm() {
		TParm searchParm = new TParm();
		String startDate = getValueString("START_DATE");
		startDate = startDate.substring(0, 4) + startDate.substring(5, 7)
				+ startDate.substring(8, 10) + startDate.substring(11, 13)
				+ startDate.substring(14, 16) + startDate.substring(17, 19);
		searchParm.setData("START_DATE",startDate);
		String endDate = getValueString("END_DATE");
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7)
				+ endDate.substring(8, 10) + endDate.substring(11, 13)
				+ endDate.substring(14, 16) + endDate.substring(17, 19);
		searchParm.setData("END_DATE",endDate);
		
		String invCode = getValueString("INV_CODE");
		searchParm.setData("INV_CODE",invCode);
		
		String orgCode = getValueString("ORG_CODE");
		searchParm.setData("ORG_CODE",orgCode);
		
		String supCode = getValueString("SUP_CODE");
		
		searchParm.setData("SUP_CODE",supCode);
		return searchParm;
	}

	/**
	 * table  �ĵ����¼�
	 */
	public void onTableClicked(int row ){
		if(row<0)  return ;
		this.onClear() ;
		TTable tableD = this.getTable("TABLED");
		row = tableD.getSelectedRow() ;
		for(int i=0;i<tableD.getRowCount();i++){
			tableD.setItem(i, "FLG", "N");
		}
		tableD.setItem(row, "FLG", "Y");
		String accountNo = tableD.getItemString(row, "ACCOUNT_NO") ;
		TParm result= INVsettlementTool.getInstance().getAccountData(accountNo) ;
		if(result.getCount()<0){
			this.messageBox("û��ϸ������") ;
			return ;
		}
		double allMoney = 0.00 ;
		for(int i=0;i<result.getCount();i++){
			allMoney += result.getDouble("TOT_AMT", i) ;
		}
		//�ɹ��ܽ��
		setValue("SUM_TOT_AMT", StringTool.round(allMoney,2));
		//�ϼƱ���
		setValue("COUNT", result.getCount());
		table.setParmValue(result) ;
	}
	/**
	 * �����ɹ���ΪXML�ļ�
	 */
	@SuppressWarnings("unchecked")
	public void onExportXml() {
		
		String supCode = getValueString("SUP_CODE");
		if(supCode == null || supCode.equals("")){
			this.messageBox("��ѡ��Ӧ��");
			return ;
		}
		// Ҫ������������
		TParm parm   = table.getParmValue() ;
		
		if (parm == null || parm.getCount("INV_CODE") < 0) {
			this.messageBox("û�е���������");
			return;
		}
		
		int count = parm.getCount("INV_CODE");

		TParm newParm = new TParm();
		 
		int  newCount = 0 ;
		/**
		 * ȥ���ظ��ϲ�,��������ʾ���ǰ��ղ��ŷ��࣬������ҩ��xml��Ҫ��ͬһ�����ʻ���
		 */
		for(int i = 0;i < count;i++) { 
			TParm rowParm = parm.getRow(i);
			String invCode = rowParm.getValue("INV_CODE") ;
			double dosageQty = rowParm.getDouble("QTY");
			for(int j = i+1;j < count;j++){ 
				TParm rowParmNew = parm.getRow(j);
				String invCodeNew = rowParmNew.getValue("INV_CODE") ;
				double dosageQtyNew = rowParmNew.getDouble("QTY") ;
			    if(invCode.equals(invCodeNew) ){ 
			   		dosageQty += dosageQtyNew ;
				    parm.removeRow(j); 
				    j-- ;
				    count--; 
			   }
			}
			
			newParm.setData("INV_CODE",i,invCode);
			newParm.setData("CSTCODE",i,rowParm.getValue("OWN_PRICE"));
			newParm.setData("INV_DESC",i,rowParm.getValue("INV_CHN_DESC"));
			newParm.setData("SPECIFICATION",i,rowParm.getValue("DESCRIPTION"));
			newParm.setData("DOSAGE_UNIT",i,rowParm.getValue("UNIT_CHN_DESC"));
			newParm.setData("DOSAGE_QTY",i,dosageQty);
			newCount++ ;
			 
		} 
		newParm.setCount(newCount);
		List list = new ArrayList();
		String msg = "";
		for (int i = 0; i < newParm.getCount(); i++) {
			TParm t =  newParm.getRow(i);	
				double dosageQty = t.getDouble("DOSAGE_QTY") ;
					if(dosageQty > 0 ){
						Map<String, String> map = new LinkedHashMap();
						map.put("cstcode",  t.getValue("CSTCODE"));//����
						map.put("goods", t.getValue("INV_CODE") );   //����Ŷ����
						map.put("goodname", t.getValue("INV_DESC")); //��������
						map.put("spec", t.getValue("SPECIFICATION"));//���
						map.put("msunitno", t.getValue("DOSAGE_UNIT"));//��λ
						map.put("billqty",dosageQty+"");//��������
						list.add(map);
					}

		}
		try {
			Document doc = ExportXmlUtil.createXml(list);
			ExportXmlUtil.exeSaveXml(doc, "���ʽ���.xml");
		} catch (Exception e) {
			System.out.println("������Ϣ=============="+e.toString());
		}
		
	}
	/**
	 * ����
	 * ������Ľ������ݲ��뵽account���У���ˮ��account_no Ϊ����
	 */
	public void onSave(){
		if( table.getRowCount() <= 0){
			this.messageBox("û�н�������");
			return ;
		}
		
		String invCode = getValueString("INV_CODE");
		if(invCode != null &&  invCode.length() >  0 ){
			this.messageBox("���㲻���������ʱ���!");
			return ;
		}
		String supCode = getValueString("SUP_CODE");
		if(supCode == null || supCode.equals("")){
			this.messageBox("��ѡ��Ӧ��");
			return ;
		}
		
		String endDate = getValueString("END_DATE");
    	String closeDate = endDate.substring(0, 4)+ endDate.substring(5, 7) + endDate.substring(8, 10);
    	String startDate = getValueString("START_DATE");
    	 startDate = startDate.substring(0, 4)+ startDate.substring(5, 7) + startDate.substring(8, 10);
		TParm parm = this.getSearchParm() ;
		//��ѯ������Ƿ�������  true�����
		TParm exits = checkSettleAccounts(parm) ;
		if(exits.getCount()>0){
			this.messageBox("�����ѽ���") ;
				return ;

		}
		if(this.messageBox("��ʾ", "��ȷ���Ƿ����?", 2) == 0){
		 
			int count = 0;
			TParm inParm = new TParm();
			String accountNo = SystemTool.getInstance().getNo("ALL", "INV", "INV_ACCOUNT", "No");
			for (int i = 0; i < accountResult.getCount(); i++) {
				TParm rowParm = accountResult.getRow(i);
				    inParm.setData("REGION_CODE",i,Operator.getRegion());
				    inParm.setData("ACCOUNT_NO",i,accountNo);
					inParm.setData("CLOSE_DATE",i,closeDate);
					inParm.setData("START_DATE",i,startDate);
					inParm.setData("ORG_CODE",i,rowParm.getValue("ORG_CODE"));
					inParm.setData("INV_CODE",i,rowParm.getValue("INV_CODE"));
					//��������
					inParm.setData("TOTAL_OUT_QTY",i,rowParm.getValue("QTY"));
					inParm.setData("TOTAL_UNIT_CODE",i,rowParm.getValue("UNIT_CODE"));
					//����
					inParm.setData("VERIFYIN_PRICE",i,rowParm.getDouble("OWN_PRICE"));
					//�ܽ��
					inParm.setData("VERIFYIN_AMT",i,rowParm.getDouble("TOT_AMT"));
					//��Ӧ����
					inParm.setData("SUP_CODE",i,this.getValueString("SUP_CODE"));
					inParm.setData("OPT_USER",i,Operator.getID());
					inParm.setData("OPT_TERM",i,Operator.getIP());
					count++;
				}
			inParm.setCount(count) ;
			TParm result = TIOM_AppServer.executeAction("action.inv.INVsettlementAction",
	                 "onSave", inParm);
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("����ʧ��");
				return;
			}
			this.messageBox("����ɹ�") ;
		}
	}
	/**
	 * ȡ�����㹦��
	 */
	public void onCancleAccount(){
		 TTable table = this.getTable("TABLED");
		if(table.getRowCount()<0 || table==null){
			this.messageBox("û�н�������") ;
			return  ;
		}
		TParm  parm  = new TParm() ;
		int count = 0;
		for(int i=0;i<table.getRowCount();i++){
			if(table.getItemData(i, "FLG").equals("Y")){
				count ++ ;
				parm.addData("ACCOUNT_NO", table.getItemString(i, "ACCOUNT_NO")) ;
			}
		}
		if(count==0){
			this.messageBox("ѡ��ȡ������") ;
			return  ;
		}
		
		parm.setCount(count) ;
    	TParm result = TIOM_AppServer.executeAction("action.inv.INVsettlementAction",
                "onCancleAccountData", parm);
		if (result == null || result.getErrCode() < 0) {
			this.messageBox("ȡ������ʧ��");
			return ;
		}
		this.messageBox("ȡ���ɹ�") ;
		this.onClear() ;
		table.removeRowAll() ;
		return   ;
	}
	/**
	 * ���ݽ�����ȡ���������
	 * @return
	 */
	public TParm getAccountNoByDate(){
		TParm errParm = new TParm() ;
		errParm.setErrCode(-1) ;
		String endDate = getValueString("END_DATE");
    	String closeDate = endDate.substring(0, 4)+ endDate.substring(5, 7) + endDate.substring(8, 10);	
    	//���ݽ���ڵ��ѯҪȡ�����������
    		TParm  parm = INVsettlementTool.getInstance().onCancleInvAccount(closeDate) ;
        	if(parm.getCount()<0){
        		this.messageBox("û�н�������") ;      
        		return errParm ;
        	}
        	return parm ;
	}
	 /**
     * ���Excel
     */
    public void onExportXls() {
        TTable table = this.getTable("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "���ʽ���");
    }
    
    /**
     * ͬ������������HIS
     */
    public void onSynchronous(){
    	String endDate = getValueString("END_DATE");
    	String closeDate = endDate.substring(0, 4)+ endDate.substring(5, 7) + endDate.substring(8, 10);
		TParm closeParm = new TParm();
		closeParm.setData("CLOSE_DATE",closeDate);
		
		if(this.messageBox("��ʾ", "��ȷ���Ƿ�����"+closeDate+"����������HIS?", 2) == 0){
			TParm result = INVsettlementTool.getInstance().onQueryInvAccount(closeParm) ;
			if (result == null || result.getCount() < 0) {
				this.messageBox("û��Ҫ���͵Ľ�������");
				return ;
			}
			
			result = TIOM_AppServer.executeAction("action.inv.INVsettlementAction",
	                "onSynchronous", result);   
			if (result == null || result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return;
			}
		}
		this.messageBox("�����ɹ�") ;
	}
	
	/**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("INV_CODE").setValue(order_code);
        String order_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("INV_DESC").setValue(order_desc);
    }
    
    /**
     * ����Ƿ�����
     * @return
     */
    public TParm checkSettleAccounts(TParm searchParm){
		TParm result = INVsettlementTool.getInstance().checkInvAccount(searchParm) ;
	     return  result ;
    }
    /**
     * ���
     */
    public void onClear(){
    	 String clearStr = "INV_CODE;INV_DESC;ORG_CODE;SUM_TOT_AMT;COUNT;";
    	 this.clearValue(clearStr);
    	 table.removeRowAll() ;
    }
    /**
     * ��ӡ
     */
    public void onPrint(){
		TParm result   = table.getParmValue() ;
		if (result == null || result.getCount("INV_CODE") < 0) {
			this.messageBox("û�д�ӡ����");
			return;
		}
		
		 // ��ӡ����
        TParm date = new TParm();
        
        String startDate = getValueString("START_DATE");
        startDate = startDate.substring(0,19);
        String endDate = getValueString("END_DATE");
        endDate = endDate.substring(0,19);
        // ��ͷ����
        date.setData("TITLE", "���ʽ��㵥");
        date.setData("START_DATE",startDate);	
        date.setData("END_DATE",endDate);	
        
		TParm newParm = new TParm() ;
		 
		double sumTotAmt =  0 ;
		int count = result.getCount("INV_CODE") ;
		for(int i = 0;  i < count ; i++){
			TParm rowParm = result.getRow(i);
			newParm.setData("INV_CODE",i,rowParm.getValue("INV_CODE"));
			newParm.setData("INV_DESC",i,rowParm.getValue("INV_DESC"));
			newParm.setData("DESCRIPTION",i,rowParm.getValue("DESCRIPTION"));
			newParm.setData("OWN_PRICE",i,rowParm.getDouble("OWN_PRICE"));
			newParm.setData("QTY",i,rowParm.getDouble("QTY"));
			newParm.setData("UNIT_CHN_DESC",i,rowParm.getValue("UNIT_CHN_DESC"));
			newParm.setData("TOT_AMT",i,rowParm.getDouble("TOT_AMT"));
			sumTotAmt +=rowParm.getDouble("TOT_AMT");
			newParm.setData("ORG_DESC",i,rowParm.getValue("ORG_DESC"));
		}
		
		newParm.setCount(newParm.getCount("INV_CODE"));
		newParm.addData("SYSTEM", "COLUMNS", "INV_CODE");
		newParm.addData("SYSTEM", "COLUMNS", "INV_DESC");
		newParm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		newParm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
		newParm.addData("SYSTEM", "COLUMNS", "QTY");
		newParm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		newParm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
		newParm.addData("SYSTEM", "COLUMNS", "ORG_DESC");
		date.setData("TABLE", newParm.getData());
		
		date.setData("SUM_TOT_AMT",  df2.format(StringTool.round(sumTotAmt, 2)));//���ռ۸�
		date.setData("OPT_USER",Operator.getName()); //Operator.getName()
		
		openPrintDialog("%ROOT%\\config\\prt\\inv\\INVAcconutPrint.jhw",
				date);
		
		
    }
    
    /**
     * ��λ���ʹ���
     */
    public void onOrientationAction() {
        if ("".equals(this.getValueString("INV_CODE"))) {
            this.messageBox("�����붨λ����");
            return;
        }
        boolean flg = false;
        TParm parm = table.getParmValue();
        String order_code = this.getValueString("INV_CODE");
        int row = table.getSelectedRow();
        for (int i = row + 1; i < parm.getCount("INV_CODE"); i++) {
            if (order_code.equals(parm.getValue("INV_CODE", i))) {
                row = i;
                flg = true;
                break;
            }
        }
        if (!flg) {
            this.messageBox("δ�ҵ���λ����");
        }
        else {
            table.setSelectedRow(row);
        }
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
	 * �õ��ϸ���
	 * 
	 * @param dateStr
	 *            String
	 * @return Timestamp
	 */
	public Timestamp queryFirstDayOfLastMonth(String dateStr) {
		DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
		try {
			d = defaultFormatter.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return StringTool.getTimestamp(cal.getTime());
	}

	/**
	 * ��ʼ��ʱ������
	 * 
	 * @param date
	 *            Timestamp
	 * @return Timestamp
	 */
	public Timestamp getDateForInit(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		return result;
	}
	
	/**
	 * ��ʼ��ʱ������
	 * 
	 * @param date
	 *            Timestamp
	 * @return Timestamp
	 */
	@SuppressWarnings("deprecation")
	public Timestamp getDateForInitLast(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		String dayCycle = sysParm.getValue("DAY_CYCLE",0);
		int hours = Integer.parseInt( dayCycle.substring(0,2));
		result.setHours(hours);
		int minutes = Integer.parseInt(dayCycle.substring(2,4));
		result.setMinutes(minutes);
		int seconds = Integer.parseInt(dayCycle.substring(4,6));
		result.setSeconds(seconds);
		return result;
	}
	//���ָ�ʽ��
	java.text.DecimalFormat df2 = new java.text.DecimalFormat("##########0.00");
}
