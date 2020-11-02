package com.javahis.ui.spc;

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
import jdo.spc.SPCSettleAccountsTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.dom4j.Document;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ����סԺҩ������
 * </p>
 *
 * <p>
 * Description: ����סԺҩ������
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
 * @author YUANXM 2013.04.22
 * @version 1.0
 */
public class SPCSettleAccountsControl extends TControl {
	
	public TTable table;
	
	
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
        parm.setData("CAT1_TYPE", "PHA");
		// ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		
		table = this.getTable("TABLE");
		
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
		
		TParm searchParm = getSearchParm();
		
		TParm result = SPCSettleAccountsTool.getInstance().onQuery(searchParm);
		TParm newParm = new TParm() ;
		 
		//�ɹ��ܽ��
		double sumTotAmt =  0 ;
		
		//�ݹ��ܽ��
		double sumTotOddAmt = 0 ;
		int count = result.getCount() ;
		double totAmt = 0 ;
		double oddAmt = 0 ;
		int i = 0 ;
		for(int j = 0;  j < count ; j++){
			TParm rowParm = result.getRow(j);
						
			double lastOdd = rowParm.getDouble("LAST_ODD");
			double qty = rowParm.getDouble("QTY");
			double currentQty = qty - lastOdd ;
			newParm.setData("CURRENT_QTY",i,currentQty);
			newParm.setData("QTY",i,qty);
			newParm.setData("VERIFYIN_PRICE",i,StringTool.round(rowParm.getDouble("VERIFYIN_PRICE"),4));
			
			try{
				totAmt = qty*rowParm.getDouble("VERIFYIN_PRICE");
			}catch (Exception e) {
				// TODO: handle exception
				System.out.println("�������Բɹ����۳���");
				totAmt = 0;
			}
			sumTotAmt += totAmt ;
			newParm.setData("TOT_AMT",i,StringTool.round(totAmt,2));
			newParm.setData("ORDER_CODE",i,rowParm.getValue("ORDER_CODE"));
			newParm.setData("ORDER_DESC",i,rowParm.getValue("ORDER_DESC"));
			newParm.setData("SPECIFICATION",i,rowParm.getValue("SPECIFICATION"));
			newParm.setData("UNIT_CHN_DESC",i,rowParm.getValue("UNIT_CHN_DESC"));
			newParm.setData("LAST_ODD",i,lastOdd);
			newParm.setData("DOSAGE_QTY",i,rowParm.getDouble("DOSAGE_QTY"));
			newParm.setData("DOSAGE_UNIT",i,rowParm.getValue("DOSAGE_UNIT"));
			newParm.setData("ODD",i,rowParm.getDouble("ODD"));
			
			double odd = rowParm.getDouble("ODD");
			oddAmt = odd * rowParm.getDouble("VERIFYIN_PRICE");
			
			sumTotOddAmt += oddAmt ;
			newParm.setData("ODD_AMT",i,StringTool.round(oddAmt,2));
			newParm.setData("DELIVERYCODE",i,rowParm.getValue("DELIVERYCODE"));
			newParm.setData("CSTCODE",i,rowParm.getValue("CSTCODE"));
			newParm.setData("ORG_CODE",i,rowParm.getValue("ORG_CODE"));
			newParm.setData("CONTRACT_PRICE",i,rowParm.getValue("CONTRACT_PRICE"));
			newParm.setData("SUP_ORDER_CODE",i,rowParm.getValue("SUP_ORDER_CODE"));
			i++;
			
		}
		
		//�����ܼ�
		setValue("SUM_TOT_AMT", sumTotAmt);
		setValue("SUM_TOT_ODD_AMT", sumTotOddAmt);
		setValue("CURRENT_TOT_AMT", sumTotAmt-sumTotOddAmt);
		setValue("COUNT", count);
		table.setParmValue(newParm);
	}

	private TParm getSearchParm() {
		TParm searchParm = new TParm();
		String startDate = getValueString("START_DATE");
		startDate = startDate.substring(0, 4)
				+ startDate.substring(5, 7) + startDate.substring(8, 10)
				+ startDate.substring(11, 13)
				+ startDate.substring(14, 16)
				+ startDate.substring(17, 19);
		searchParm.setData("START_DATE",startDate);
		
		String endDate = getValueString("END_DATE");
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7)
				+ endDate.substring(8, 10) + endDate.substring(11, 13)
				+ endDate.substring(14, 16) + endDate.substring(17, 19);
		searchParm.setData("END_DATE",endDate);
		
		String orderCode = getValueString("ORDER_CODE");
		searchParm.setData("ORDER_CODE",orderCode);
		
		String orgCode = getValueString("ORG_CODE");
		searchParm.setData("ORG_CODE",orgCode);
		
		String supCode = getValueString("SUP_CODE");
		
		searchParm.setData("SUP_CODE",supCode);
		
        if(getRadioButton("G_DRUGS").isSelected()){
        	searchParm.setData("DRUG_CATEGORY","Y");
        }else if(getRadioButton("N_DRUGS").isSelected()){
        	searchParm.setData("DRUG_CATEGORY","N");
        }
		return searchParm;
	}

	/**
	 * �����ɹ���ΪXML�ļ�
	 */
	public void onExportXml() {
		
		String supCode = getValueString("SUP_CODE");
		if(supCode == null || supCode.equals("")){
			this.messageBox("��ѡ��Ӧ��");
			return ;
		}
		
		TParm searchParm = getSearchParm();
		// Ҫ��������ϸ������
		TParm parm   = SPCSettleAccountsTool.getInstance().onQuery(searchParm) ;
		
		if (parm == null || parm.getErrCode() < 0) {
			this.messageBox("û�е���������");
			return;
		}
		
		TParm sParm = new TParm();
		sParm.setData("SUP_CODE",supCode);
		TParm supplierParm = SPCSettleAccountsTool.getInstance().onQuerySysSupplier(sParm);
		
		String sellDeptCode = supplierParm.getRow(0).getValue("SELL_DEPT_CODE");
		 
		int count = parm.getCount();
		String msg = "";
		

		TParm newParm = new TParm();
		 
		int  newCount = 0 ;
		/**
		 * ȥ���ظ��ϲ�
		 */
		for(int i = 0;i < count;i++) { 
			TParm rowParm = parm.getRow(i);
			String orderCode = rowParm.getValue("SUP_ORDER_CODE") ;
			double dosageQty = rowParm.getDouble("DOSAGE_QTY");
			for(int j = i+1;j < count;j++){ 
				TParm rowParmNew = parm.getRow(j);
				String orderCodeNew = rowParmNew.getValue("SUP_ORDER_CODE") ;
				double dosageQtyNew = rowParmNew.getDouble("DOSAGE_QTY") ;
			    if(orderCode.equals(orderCodeNew) ){ 
			   		dosageQty += dosageQtyNew ;
				    parm.removeRow(j); 
				    j-- ;
				    count--; 
			   }
			}
			
			newParm.setData("SUP_ORDER_CODE",i,orderCode);
			newParm.setData("DELIVERYCODE",i,rowParm.getValue("DELIVERYCODE"));
			newParm.setData("CSTCODE",i,rowParm.getValue("CSTCODE"));
			newParm.setData("ORDER_DESC",i,rowParm.getValue("ORDER_DESC"));
			newParm.setData("SPECIFICATION",i,rowParm.getValue("SPECIFICATION"));
			newParm.setData("DOSAGE_UNIT",i,rowParm.getValue("DOSAGE_UNIT"));
			newParm.setData("DOSAGE_QTY",i,dosageQty);
			newParm.setData("PURCHASEID",i,rowParm.getValue("PURCHASEID"));
		
			newCount++ ;
			 
		} 
		newParm.setCount(newCount);
		List list = new ArrayList();
		
		for (int i = 0; i < newParm.getCount(); i++) {
			TParm t =  newParm.getRow(i);
			double dosageQty = t.getDouble("DOSAGE_QTY") ;
			String supOrderCode  = t.getValue("SUP_ORDER_CODE");

			if(dosageQty > 0 ){
				Map<String, String> map = new LinkedHashMap();
				map.put("deliverycode", t.getValue("DELIVERYCODE"));
				map.put("cstcode", sellDeptCode);
				map.put("goods", supOrderCode);
				map.put("goodname", t.getValue("ORDER_DESC"));
				map.put("spec", t.getValue("SPECIFICATION"));
				map.put("msunitno", t.getValue("DOSAGE_UNIT"));
				map.put("billqty",dosageQty+"");
				map.put("purchaseid", t.getValue("PURCHASEID"));
				list.add(map);
			}
		}
		Document doc = ExportXmlUtil.createXml(list);
		ExportXmlUtil.exeSaveXml(doc, "ҩƷ����.xml");
		if(msg.length() > 0 ){
			this.messageBox("���������룺"+msg+"û���ҵ�");
			String fileNmae = (new StringBuilder())
							.append(TConfig.getSystemValue("UDD_DISBATCH_LocalPath"))
							.append("\\ҩƷ���������־")
							.append(StringTool.getString(TJDODBTool.getInstance()
							.getDBTime(), "yyyyMMddHHmmss")).append(".txt")
							.toString();
				messageBox_((new StringBuilder())
						.append("��ϸ�����C:/JavaHis/logs/ҩƷ���������־")
						.append(StringTool.getString(TJDODBTool.getInstance()
								.getDBTime(), "yyyyMMddHHmmss"))
						.append(".txt�ļ�").toString());
			try {
				FileTool.setString(fileNmae, "���������룺"+msg+"û���ҵ�");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void onSave(){
		table = getTable("TABLE");
		if( table.getRowCount() <= 0){
			this.messageBox("û�н�������");
			return ;
		}
		//���㲻��ѡȡ���������
		String orgCode = getValueString("ORG_CODE");
		String orderCode = getValueString("ORDER_CODE");
		if( orgCode != null &&  orgCode.length() > 0 ){
			this.messageBox("�������Ų��ܽ���!");
			return ;
		}
		if(orderCode != null &&  orderCode.length() >  0 ){
			this.messageBox("���㲻������ҩƷ����!");
			return ;
		}
		String supCode = getValueString("SUP_CODE");
		if(supCode == null || supCode.equals("")){
			this.messageBox("��ѡ��Ӧ��");
			return ;
		}
		
		String endDate = getValueString("END_DATE");
    	String closeDate = endDate.substring(0, 4)+ endDate.substring(5, 7) + endDate.substring(8, 10);
		TParm closeParm = new TParm();
		closeParm.setData("CLOSE_DATE",closeDate);
		
		boolean exits = checkSettleAccounts(closeParm) ;
		if(exits){
			if(this.messageBox("��ʾ", "�����ѽ��㣬��ȷ���Ƿ����½���?", 2) == 0){
				TParm result = TIOM_AppServer.executeAction("action.spc.SPCSettleAccountsAction",
		                 "onDelete", closeParm);
				if (result == null || result.getErrCode() < 0) {
					this.messageBox("ɾ���ϴν�������ʧ��");
					return;
				}
			}else{
				return ;
			}
		}
		
		TParm searchParm = getSearchParm();
		TParm parm   = SPCSettleAccountsTool.getInstance().onQuery(searchParm) ;
		
		if (parm == null || parm.getErrCode() < 0) {
			this.messageBox("û�н��������");
			return;
		}
		
	
		
		if(this.messageBox("��ʾ", "��ȷ���Ƿ����?", 2) == 0){
		 
			int count = 0;
			TParm inParm = new TParm();
			for (int i = 0; i < parm.getCount(); i++) {
				TParm rowParm = (TParm) parm.getRow(i);

				inParm.setData("CLOSE_DATE",count,closeDate);
				inParm.setData("ORG_CODE",count,rowParm.getValue("ORG_CODE"));
				inParm.setData("ORDER_CODE",count,rowParm.getValue("ORDER_CODE"));
				inParm.setData("SUP_ORDER_CODE",count,rowParm.getValue("SUP_ORDER_CODE"));
				double lastOdd = rowParm.getDouble("LAST_ODD");
				inParm.setData("LAST_ODD_QTY",count,StringTool.round(lastOdd,4));
				inParm.setData("OUT_QTY",count,StringTool.round(rowParm.getDouble("QTY")-rowParm.getDouble("LAST_ODD"),4));
				
				double qty = rowParm.getDouble("QTY") ;
				inParm.setData("TOTAL_OUT_QTY",count,StringTool.round(qty,4));
				inParm.setData("TOTAL_UNIT_CODE",count,rowParm.getValue("FDOSAGE_UNIT"));
				
				double verifyinPrice = rowParm.getDouble("VERIFYIN_PRICE");
				inParm.setData("VERIFYIN_PRICE",count,StringTool.round(verifyinPrice,4));
				
				double totAmt = qty*verifyinPrice;
				
				inParm.setData("VERIFYIN_AMT",count,StringTool.round(totAmt,2));
				inParm.setData("ACCOUNT_QTY",count,rowParm.getInt("DOSAGE_QTY"));
				inParm.setData("ACCOUNT_UNIT_CODE",count,rowParm.getValue("STOCK_UNIT"));
				
				double oddQty = rowParm.getDouble("ODD") ;
				inParm.setData("ODD_QTY",count,StringTool.round(rowParm.getDouble("ODD"),4));
				double oddAmt = oddQty * verifyinPrice;
				
				inParm.setData("ODD_AMT",count,StringTool.round(oddAmt,2));
				inParm.setData("REGION_CODE",count,getValueString("REGION_CODE"));
				inParm.setData("OPT_USER",count,Operator.getID());
				inParm.setData("OPT_TERM",count,Operator.getIP());
				inParm.setData("IS_UPDATE",count,"Y");
				inParm.setData("SUP_CODE",count,getValueString("SUP_CODE"));
				inParm.setData("CONTRACT_PRICE",count,rowParm.getDouble("CONTRACT_PRICE"));
				 
				count++;
				
			}
 
			TParm result = TIOM_AppServer.executeAction("action.spc.SPCSettleAccountsAction",
	                 "onSave", inParm);
			if (result == null || result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return;
			}
			this.messageBox(result.getErrText());
		}
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
        ExportExcelUtil.getInstance().exportExcel(table, "ҩƷ����");
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
			TParm result = SPCSettleAccountsTool.getInstance().onQueryIndAccount(closeParm);
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("û��Ҫ���͵Ľ�������");
				return ;
			}
			
			result = TIOM_AppServer.executeAction("action.spc.SPCSettleAccountsAction",
	                "onSynchronous", result);   
			if (result == null || result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return;
			}
			this.messageBox("���ͳɹ�");
		}
	}
	
	/**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }
    
    /**
     * ����Ƿ�����
     * @return
     */
    public boolean checkSettleAccounts(TParm searchParm){
		TParm result = SPCSettleAccountsTool.getInstance().checkIndAccount(searchParm);
		if(result.getCount() > 0 ){
			return true;
		}
		return false;
    }
    
    public void onClear(){
    	 String clearStr = "ORDER_CODE;ORDER_DESC;ORG_CODE;";
    	 this.clearValue(clearStr);
    }
    
    public void onPrint(){
    	TParm searchParm = getSearchParm();
		TParm result   = SPCSettleAccountsTool.getInstance().onQuery(searchParm) ;
		if (result == null || result.getErrCode() < 0) {
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
        date.setData("TITLE", "סԺҩ�����㵥");
        date.setData("START_DATE",startDate);	
        date.setData("END_DATE",endDate);	
        
		TParm newParm = new TParm() ;
		 
		double sumTotAmt =  0 ;
		int count = result.getCount() ;
		for(int i = 0;  i < count ; i++){
			TParm rowParm = result.getRow(i);
			double lastOdd = rowParm.getDouble("LAST_ODD");
			double qty = rowParm.getDouble("QTY");
			double currentQty = qty - lastOdd ;
			newParm.setData("CURRENT_QTY",i,currentQty);
			newParm.setData("QTY",i,qty);
			newParm.setData("VERIFYIN_PRICE",i,StringTool.round(rowParm.getDouble("VERIFYIN_PRICE"),4));
			double totAmt = 0 ;
			try{
				totAmt = qty*rowParm.getDouble("VERIFYIN_PRICE");
				
			}catch (Exception e) {
				// TODO: handle exception
				System.out.println("�������Բɹ����۳���");
				totAmt = 0;
			}
			sumTotAmt += totAmt ;
			newParm.setData("TOT_AMT",i,StringTool.round(totAmt,2));
			newParm.setData("ORDER_CODE",i,rowParm.getValue("ORDER_CODE"));
			newParm.setData("ORDER_DESC",i,rowParm.getValue("ORDER_DESC"));
			newParm.setData("SPECIFICATION",i,rowParm.getValue("SPECIFICATION"));
			newParm.setData("UNIT_CHN_DESC",i,rowParm.getValue("UNIT_CHN_DESC"));
			newParm.setData("LAST_ODD",i,lastOdd);
			newParm.setData("DOSAGE_QTY",i,rowParm.getDouble("DOSAGE_QTY"));
			newParm.setData("DOSAGE_UNIT",i,rowParm.getValue("DOSAGE_UNIT"));
			newParm.setData("ODD",i,rowParm.getDouble("ODD"));
			newParm.setData("DELIVERYCODE",i,rowParm.getValue("DELIVERYCODE"));
			newParm.setData("CSTCODE",i,rowParm.getValue("CSTCODE"));
		}
		
		newParm.setCount(newParm.getCount("ORDER_CODE"));
		newParm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
		newParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		newParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		newParm.addData("SYSTEM", "COLUMNS", "CURRENT_QTY");
		newParm.addData("SYSTEM", "COLUMNS", "LAST_ODD");
		newParm.addData("SYSTEM", "COLUMNS", "QTY");
		newParm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		newParm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
		newParm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
		newParm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
		newParm.addData("SYSTEM", "COLUMNS", "DOSAGE_UNIT");
		newParm.addData("SYSTEM", "COLUMNS", "ODD");
		newParm.addData("SYSTEM", "COLUMNS", "DELIVERYCODE");
		newParm.addData("SYSTEM", "COLUMNS", "CSTCODE");
		date.setData("TABLE", newParm.getData());
		
		date.setData("SUM_TOT_AMT",  df2.format(StringTool.round(sumTotAmt, 2)));//���ռ۸�
		date.setData("OPT_USER",""); //Operator.getName()
		
		openPrintDialog("%ROOT%\\config\\prt\\spc\\SPCExportXmlPrint.jhw",
				date, true);
		
		
    }
    
    /**
     * ��λҩƷ����
     */
    public void onOrientationAction() {
        if ("".equals(this.getValueString("ORDER_CODE"))) {
            this.messageBox("�����붨λҩƷ");
            return;
        }
        boolean flg = false;
        TParm parm = table.getParmValue();
        String order_code = this.getValueString("ORDER_CODE");
        int row = table.getSelectedRow();
        for (int i = row + 1; i < parm.getCount("ORDER_CODE"); i++) {
            if (order_code.equals(parm.getValue("ORDER_CODE", i))) {
                row = i;
                flg = true;
                break;
            }
        }
        if (!flg) {
            this.messageBox("δ�ҵ���λҩƷ");
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

	private String formateMonth(int month){
		if(month < 10){
			return "0"+month;
		}else{
			return ""+month;
		} 
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

	//���ָ�ʽ��
	java.text.DecimalFormat df2 = new java.text.DecimalFormat("##########0.00");

}
