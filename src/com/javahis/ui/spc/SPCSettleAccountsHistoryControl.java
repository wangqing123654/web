package com.javahis.ui.spc;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.spc.SPCSettleAccountsHistoryTool;
import jdo.spc.SPCSysFeeTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.dom4j.Document;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ����סԺҩ�����㡾��ʷ��ѯ��
 * </p>
 *
 * <p>
 * Description: ����סԺҩ�����㡾��ʷ��ѯ��
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
public class SPCSettleAccountsHistoryControl extends TControl {
	
	public TTable table;
	
	
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		Timestamp date = SystemTool.getInstance().getDate();
		
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date);
		cal2.setTime(date);
		
		cal2.roll(Calendar.MONTH, -1);
		// ��ʼ����ѯ����
		
//		cal.get(Calendar.m)
		String startStr = cal1.get(Calendar.YEAR) + "/" + formateMonth((cal2.get(Calendar.MONTH)+1)) + "/26" + " 00:00:00";
		String endStr = cal1.get(Calendar.YEAR) + "/" + formateMonth((cal1.get(Calendar.MONTH)+1)) + "/25" + " 23:59:59";

		// ��ʼ����ѯ����
		this.setValue("END_DATE", endStr);
		this.setValue("START_DATE", startStr);
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
		
		TParm searchParm = getSearchParm();
		String msg = searchParm.getValue("MSG");
		if(msg.length() >  0 ){
			this.messageBox(msg);
			return ;
		}
		
		TParm result = SPCSettleAccountsHistoryTool.getInstance().onQuery(searchParm);
		
		//�ɹ��ܽ��
		double sumTotAmt =  0 ;
		
		//�ݹ��ܽ��
		double sumTotOddAmt = 0 ;
		int count = result.getCount() ;
		double totAmt = 0 ;
		double oddAmt = 0 ;
		for(int i = 0;  i < count ; i++){
		    TParm rowParm = (TParm)result.getRow(i);
		    totAmt = rowParm.getDouble("VERIFYIN_AMT");
		    sumTotAmt += totAmt ;
		    oddAmt = rowParm.getDouble("ODD_AMT");
		    sumTotOddAmt += oddAmt ;
		}
		
		//�����ܼ�
		setValue("SUM_TOT_AMT", sumTotAmt);
		setValue("SUM_TOT_ODD_AMT", sumTotOddAmt);
		setValue("COUNT", count);
		table.setParmValue(result);
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
		
		String msg = "" ;
		if(!startDate.contains("26")){
			msg += "��ʼʱ�����ѡÿ���µ�26��\n";
		}
		
		String closeDate = "" ;
		
		String endDate = getValueString("END_DATE");
		searchParm.setData("END_DATE",endDate);
		if(!endDate.contains("25")){
			msg += "����ʱ�����ѡÿ���µ�25��\n";
		}else{
			 
	    	closeDate = endDate.substring(0, 4)+ endDate.substring(5, 7) + endDate.substring(8, 10);
		}
		
		String orderCode = getValueString("ORDER_CODE");
		searchParm.setData("ORDER_CODE",orderCode);
		
		String orgCode = getValueString("ORG_CODE");
		searchParm.setData("ORG_CODE",orgCode);
		
		String supCode = getValueString("SUP_CODE");
		if(supCode == null || supCode.equals("")){
			supCode = "18";
		}
		searchParm.setData("SUP_CODE",supCode);
		searchParm.setData("CLOSE_DATE",closeDate);
		
		//������ʾ��Ϣ
		searchParm.setData("MSG",msg);
		return searchParm;
	}

	/**
	 * �����ɹ���ΪXML�ļ�
	 */
	public void onExportXml() {
		TParm searchParm = getSearchParm();
		String errmsg = searchParm.getValue("MSG");
		if(errmsg.length() >  0 ){
			this.messageBox(errmsg);
			return ;
		}
		
		TParm parm = SPCSettleAccountsHistoryTool.getInstance().onQuery(searchParm);

		if (parm == null || parm.getErrCode() < 0) {
			this.messageBox("û�е���������");
			return;
		}
		
		String regionCode = getValueString("REGION_CODE");
		
		int count = parm.getCount();
		
		 
		TParm newParm = new TParm();
		 
		int  newCount = 0 ;
		/**
		 * ȥ���ظ��ϲ�
		 */
		for(int i = 0;i < count;i++) { 
			TParm rowParm = parm.getRow(i);
			String orderCode = rowParm.getValue("ORDER_CODE") ;
			double dosageQty = rowParm.getDouble("DOSAGE_QTY");
			for(int j = i+1;j < count;j++){ 
				TParm rowParmNew = parm.getRow(j);
				String orderCodeNew = rowParmNew.getValue("ORDER_CODE") ;
				double dosageQtyNew = rowParmNew.getDouble("DOSAGE_QTY") ;
			    if(orderCode.equals(orderCodeNew) ){ 
			   		dosageQty += dosageQtyNew ;
				    parm.removeRow(j); 
				    j-- ;
				    count--; 
			   }
			}
			
			newParm.setData("ORDER_CODE",i,orderCode);
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
		String msg = "";
		for (int i = 0; i < newParm.getCount(); i++) {
			TParm t =  newParm.getRow(i);
			// System.out.println("Parm:"+parm);
			
			double dosageQty = t.getDouble("DOSAGE_QTY") ;
			
			String orderCode = t.getValue("ORDER_CODE") ;
			TParm searchHisParm = new TParm();
			searchHisParm.setData("REGION_CODE",regionCode);
			searchHisParm.setData("ORDER_CODE",orderCode);
			TParm result = SPCSysFeeTool.getInstance().queryHisOrderCode(searchHisParm);		
			String hisOrderCode = result.getValue("HIS_ORDER_CODE", 0);
			
			if(hisOrderCode == null || hisOrderCode.equals("")){
				msg += orderCode+",";
			}else{
				if(dosageQty > 0 ){
					Map<String, String> map = new LinkedHashMap();
					map.put("deliverycode", t.getValue("DELIVERYCODE"));
					map.put("cstcode",  t.getValue("CSTCODE"));
					map.put("goods", hisOrderCode);
					map.put("goodname", t.getValue("ORDER_DESC"));
					map.put("spec", t.getValue("SPECIFICATION"));
					map.put("msunitno", t.getValue("DOSAGE_UNIT"));
					map.put("billqty",dosageQty+"");
					map.put("purchaseid", t.getValue("PURCHASEID"));
					list.add(map);
				}
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
	
	
	 /**
     * ���Excel
     */
    public void onExportXls() {
        TTable table = this.getTable("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "ҩƷ������ʷ����");
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
    
   
    
    public void onClear(){
    	 String clearStr = "ORDER_CODE;ORDER_DESC;ORG_CODE;";
    	 this.clearValue(clearStr);
    }
    
    public void onPrint(){
    	TParm searchParm = getSearchParm();
		String msg = searchParm.getValue("MSG");
		if(msg.length() >  0 ){
			this.messageBox(msg);
			return ;
		}
		
		TParm result = SPCSettleAccountsHistoryTool.getInstance().onQuery(searchParm);
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
	

	//���ָ�ʽ��
	java.text.DecimalFormat df2 = new java.text.DecimalFormat("##########0.00");

}
