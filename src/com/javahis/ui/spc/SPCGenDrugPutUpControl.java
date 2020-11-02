package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.label.Constant;
import jdo.spc.INDTool;
import jdo.spc.IndStockDTool;
import jdo.spc.IndSysParmTool;
import jdo.spc.SPCGenDrugPutUpTool;
import jdo.spc.SPCMaterialLocTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;

/** 
 * 
 * <p>Title: ҩ����ҩ�ϼ�(���)</p>
 *
 * <p>Description:TODO ҩ����ҩ�ϼ�</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class SPCGenDrugPutUpControl  extends TControl {
	
	
	//δ���
	TTable table_N;
	
	// �����
	TTable table_Y;
	
	TPanel N_PANEL;
	TPanel Y_PANEL;
	
	TParm parm1 = new TParm();
	int k = 0;
	
	//���յ���
	String  verifyinNo;
	
	//������ӱ�ǩ
	String  contaninerTags ;
	
	//���ӱ�ǩ
	String  electraonicTags;
	
	String orgCode = "";
	
	
	/**
	 * ���ӱ�ǩ
	 */
	TParm labelParm = new TParm();
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// TFrame tFrame = (TFrame) getComponent("UI");
		// final TTextField mrField = (TTextField)
		// getComponent("DIANZIBIAOQIAN");
		// tFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		// public void windowOpened(java.awt.event.WindowEvent evt) {
		// mrField.requestFocus();
		// }
		// });
		Timestamp sysDate = SystemTool.getInstance().getDate();
		setValue("END_DATE",sysDate.toString().substring(0, 10).replace('-', '/'));
		setValue("START_DATE",StringTool.rollDate(sysDate, -2).toString().substring(0, 10).replace('-', '/') );
		table_N = this.getTable("TABLE_N");
		table_Y = this.getTable("TABLE_Y");
		 
	}

	
	public void onQuery(){
		
		verifyinNo = getValueString("VERIFYIN_NO");
		contaninerTags = getValueString("CONTAINER_TAGS");
		
		N_PANEL = (TPanel)getComponent("N_PANEL");
		Y_PANEL = (TPanel)getComponent("Y_PANEL");
		TParm parm = new TParm();
		parm.setData("VERIFYIN_NO",verifyinNo);
		parm.setData("CONTAINER_TAGS",contaninerTags);
		
		if(N_PANEL.isShowing()){
			parm.setData("IS_PUTAWAY","N");
		}
		if(Y_PANEL.isShowing()){
			parm.setData("IS_PUTAWAY","Y");
			
			String startDate = getValueString("START_DATE");
			if(startDate != null && !startDate.equals("")){
				startDate = startDate.substring(0,10);
				parm.setData("START_DATE",startDate);
			}
			String endDate = getValueString("END_DATE");
			if(endDate != null && !endDate.equals("")){
				endDate = endDate.substring(0,10);
				parm.setData("END_DATE",endDate);
			}
			
		}
		TParm result = SPCGenDrugPutUpTool.getInstance().onQuery(parm);
		//������µ�ԭ�����²�ѯһ��
		labelParm = SPCGenDrugPutUpTool.getInstance().onQuery(parm);
		this.setValue("VERIFYIN_NO", "");
		this.setValue("CONTAINER_TAGS", "");
		
		if(result.getCount() <  0 ){
			this.messageBox("û�в�ѯ������");
			if(N_PANEL.isShowing()){
				table_N.setParmValue(new TParm());
			}
			if(Y_PANEL.isShowing()){
				table_Y.setParmValue(new TParm());
			}
			return ;
		}
		
		if(N_PANEL.isShowing()){
			
			//TParm tableParm = table_N.getParmValue() ;
		//	result.addParm(tableParm);   
			table_N.setParmValue(result);
			table_Y.setParmValue(null);
			
			
			/**
			 * ȥ���ظ��ϲ�
			 */
			
			int count = labelParm.getCount() ;
			for(int i = 0;i < count;i++) { 
				TParm rowParm = labelParm.getRow(i);
				String orderCode = rowParm.getValue("ORDER_CODE") ;
				 
				for(int j = i+1;j < count;j++){ 
					TParm rowParm1 = labelParm.getRow(j);
					String orderCode1 = rowParm1.getValue("ORDER_CODE") ;
					 
				   if(orderCode.equals(orderCode1)){ 
				   		 
					   labelParm.removeRow(j); 
					   j--;
					   count--;
				   }
				}
			} 
			labelParm.setCount(count);
			
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			//���õ��ӱ�ǩ�ӿ�
			for(int i = 0 ; i < labelParm.getCount() ; i++ ){
				TParm rowParm = (TParm)labelParm.getRow(i);
				
				
				String orgCode = rowParm.getValue("ORG_CODE");
				String apRegion = getApRegion(orgCode);
				
				if(apRegion == null || apRegion.equals("")){
					this.messageBox("û�����õ��ӱ�ǩ���� "+orgCode);
					continue ;
				}
				
				
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				String orderDesc = rowParm.getValue("ORDER_DESC");
				String eletagCode = rowParm.getValue("ELETAG_CODE");
				String spec = rowParm.getValue("SPECIFICATION");
				if(null!= spec && spec.length()>11)  {
					spec = spec.substring(0, 11);
				}
				map.put("ProductName", orderDesc);
				map.put("SPECIFICATION", spec);
				map.put("TagNo", eletagCode);
				map.put("Light", 50);
				
				
				map.put("APRegion", apRegion);
				list.add(map);
								
			}
			try{
				String url = Constant.LABELDATA_URL ;
				LabelControl.getInstance().sendLabelDate(list, url);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				System.out.println("�ϼܲ�ѯ����õ��ӱ�ǩʧ��");
			}
			
		}
		if(Y_PANEL.isShowing()){
			table_Y.setParmValue(result);
			table_N.setParmValue(null);
		}
		
	}



	/**
	 * ���䵥�š����յ��Żس��¼�
	 * */
	public void onMonCHD() {
		onQuery();
		this.getTextField("ELECTRONIC_TAGS").grabFocus();
		return ;
	}

	/**
	 * ��λ���ӱ�ǩ�Ļس��¼�
	 * */
	public void onClickByElectTags() {
		electraonicTags =  getValueString("ELECTRONIC_TAGS");
		if (electraonicTags.length() == 0)
	        return;
		verifyinNo = getValueString("VERIFYIN_NO");
		contaninerTags = getValueString("CONTAINER_TAGS");
		table_N.acceptText();
		//����IND_MATERIALLOC,�õ�ORDER_CODE
		TParm tabParm = table_N.getParmValue();		
		String labelNo = "";
		String productName = "";
		String orgCode = "";
		String orderCode = "";
		String spec = "";
		 
		int count = table_N.getParmValue().getCount();
		boolean flg = false;
		TParm eleParm = null;
		for (int i = 0; i < count; i++) {
			TParm rowParm = tabParm.getRow(i);		
			//�ж��Ƿ��ҵ�
			if(electraonicTags.equals(rowParm.getData("ELETAG_CODE"))){				 
				productName = rowParm.getValue("ORDER_DESC");
				orgCode = rowParm.getValue("ORG_CODE");
				orderCode= rowParm.getValue("ORDER_CODE");
				
				TParm result = onSaveStock(i);
				if (result.getErrCode() < 0) {
					this.messageBox("����ʧ��");
					break ;
					
				}
				//�ҵ��Ƴ�һ��  �����õ��ӱ�ǩ�ӿ���ʾ�������˸
				
				table_N.removeRow(i);
				eleParm = new TParm();
				eleParm.setData("ELE_TAG",electraonicTags);
				eleParm.setData("ORDER_CODE",orderCode);
				eleParm.setData("ORG_CODE",orgCode);
				eleParm.setData("ELETAG_CODE",electraonicTags);
				labelNo = electraonicTags;
				/*TParm inParm = new TParm();
				inParm.setData("ORDER_CODE",orderCode);
				inParm.setData("ORG_CODE",orgCode);
				inParm.setData("ELETAG_CODE",electraonicTags);
				TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(inParm);
				spec = outParm.getValue("SPECIFICATION");*/
				flg = true;
				i--;
				}
			 
		}
		if(flg) {
				TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(eleParm);	
				if(outParm.getCount() >  0 ){
					spec = outParm.getValue("SPECIFICATION",0);
				}
				
				 
				String apRegion = getApRegion(orgCode);
				if(apRegion == null || apRegion.equals("")){
					this.messageBox("û�����õ��ӱ�ǩ���� "+orgCode);
					 return ;
				}
				
				List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				 
				if(null!= spec && spec.length()>11)  {
					spec = spec.substring(0, 11);
				}
				map.put("ProductName", productName);
				map.put("SPECIFICATION", spec+" "+outParm.getValue("QTY",0));
				map.put("TagNo", labelNo);
				map.put("Light", 0);
				map.put("APRegion",apRegion);
				list.add(map);
				try{
					String url = Constant.LABELDATA_URL ;
					LabelControl.getInstance().sendLabelDate(list, url);
				}catch (Exception e) {
					// TODO: handle exception
					System.out.println("�ϼ�������õ��ӱ�ǩʧ��");
				}
				 

		}
		this.setValue("VERIFYIN_NO", verifyinNo);
		this.setValue("CONTAINER_TAGS", contaninerTags);
		this.clearValue("ELECTRONIC_TAGS");
		
		setValue("ELETAG_CODE", "");
		
		/**
		TParm tabNowParm = table_N.getParmValue();
		//�ж��Ƿ�ȫ�ϼ����
		for(int k = 0 ; k < count ; k++ ){
			TParm rowParm = tabParm.getRow(k);
			String materiaLoc = rowParm.getValue("MATERIAL_LOC_CODE");
			if(!StringUtil.isNullString(materiaLoc)){
				materiaLoc = materiaLoc.substring(0,3);
			}
			boolean bo = true;
			for(int j = 0 ; j < tabNowParm.getCount() ; j++ ){
				TParm rowSecParm = tabNowParm.getRow(j);
				String materiaSec = rowSecParm.getValue("MATERIAL_LOC_CODE");
				if(!StringUtil.isNullString(materiaSec)){
					materiaSec = materiaSec.substring(0,3);
				}
				
				if(materiaLoc.equals(materiaSec)){
					bo = false;
					break ;
				}
			}
			
			//��λ�ű�ǩ����
			if(bo){
				login();
				sendEleTag(materiaLoc+"000", materiaLoc+"000", "", "", 0);
			}
		}*/
		return;
	}
	
	public void onTPanlClick() {
		verifyinNo = getValueString("VERIFYIN_NO");
		contaninerTags = getValueString("CONTAINER_TAGS");
		onQuery();
		return;
	}
	
	
	/**
	 * �ϼ����
	 * @param i  ��ǰƥ���ϵ�������
	 */
	public TParm onSaveStock(int i){
		TParm parm = new TParm();
		
		TParm result = new TParm() ;
		//String org_code = "040101";
		parm.setData("IS_PUTAWAY","Y");
		parm.setData("PUTAWAY_USER",Operator.getID());
		//parm.setData("ORG_CODE", org_code);
		/*parm.setData("SUP_CODE", getValueString("SUP_CODE"));
		parm.setData("PLAN_NO", getValue("PLAN_NO"));
		parm.setData("REASON_CHN_DESC", getValueString("REASON_CODE"));
		parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));*/
		
		parm.setData("PLAN_NO", "");
		parm.setData("REASON_CHN_DESC", "");
		parm.setData("DESCRIPTION", "");
		// �����Ա
		parm.setData("CHECK_USER", Operator.getID());
		
		Timestamp date = SystemTool.getInstance().getDate();
		// ���ʱ��
		parm.setData("CHECK_DATE", date);
		// ������
		double qty = 0.00;
		// ������
		double g_qty = 0.00;
		// ������
		double pur_qty = 0.00;
		// �����
		double stock_qty = 0.00;
		// ҩƷ����
		String order_code = "";
		// ���������ת����
		TParm getTRA = new TParm();
		// �����Ч��������
		TParm getSEQ = new TParm();
		// ����ת����
		double s_qty = 0.00;
		// ���ת����
		double d_qty = 0.00;
		// ����
		String batch_no = "";
		// �������
		int batch_seq = 1;
		// ��Ч��
		String valid_date = "";
		// ���»�����IND_STOCK��FLG->U:����;I,����
		String flg = "";
		// ���ۼ۸�
		double retail = 0.00;
		// ���յ���
		double verprice = 0.00;
		// �ۼ������ۿ���
		double deduct_atm = 0.00;
		// ���ƽ����
		double stock_price = 0.00;
		// ��������
		String pur_no = "";
		// �����������
		int pur_no_seq = 0;
		//��ת�� 
		String spcBoxCode = "";

		// �õ�IND_SYSPARM��Ϣ
		result = IndSysParmTool.getInstance().onQuery();
		
		
		parm.setData("REUPRICE_FLG", "Y");

		// �ж��Ƿ��Զ������һ��������ⵥ��ά����ҩƷ��������������
		parm.setData("UPDATE_TRADE_PRICE",  "Y" );

		// ϸ����Ϣ
		String material_loc_code = "";

		// ��ⵥ�ż���
		// ҩƷ���뼯��
		TParm rowParm = table_N.getParmValue();
		String org_code=rowParm.getValue("ORG_CODE",i);  
		parm.setData("ORG_CODE", org_code);
		String verifyin_no = rowParm.getValue("VERIFYIN_NO",i);
		parm.setData("VERIFYIN", verifyin_no);
		order_code = rowParm.getValue("ORDER_CODE", i);
		parm.setData("ORDER_CODE", order_code);
		
		//��Ӧ��
		String supCode = rowParm.getValue("SUP_CODE",i) ;
		parm.setData("SUP_CODE", supCode);  
		// ���������ת����                       
		getTRA = INDTool.getInstance().getTransunitByCode(order_code); 
		if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
			this.messageBox("ҩƷ" + order_code + "ת���ʴ���");
			return getTRA;
		}
		// ����ת���ʼ���
		s_qty = getTRA.getDouble("STOCK_QTY", 0);
		parm.setData("STOCK_QTY", s_qty);
		// ���ת���ʼ���
		d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
		parm.setData("DOSAGE_QTY", d_qty);
		// �����
		stock_qty = INDTool.getInstance().getStockQTY(org_code, order_code);
		parm.setData("QTY", stock_qty);  
		// ���ż���              
		//batch_no = dataStore.getItemString(i, "BATCH_NO");
		batch_no = rowParm.getValue("BATCH_NO",i);
		parm.setData("BATCH_NO", batch_no);
		// ��Ч��   
		valid_date = StringTool.getString(rowParm.getTimestamp("VALID_DATE",i),"yyyy/MM/dd");      
		parm.setData("VALID_DATE", valid_date);
		//20121128 liyh�����ת��
		spcBoxCode = rowParm.getValue("SPC_BOX_BARCODE",i);
		parm.setData("SPC_BOX_BARCODE", spcBoxCode);
		// *************************************************************
		// luahi 2012-01-10 add �����������ѡȡ�����������ռ۸�����begin
		// *************************************************************
		// �����Ч�������� 
		// getSEQ =
		// IndStockDTool.getInstance().onQueryStockBatchSeq(
		// org_code, order_code, batch_no, valid_date);
		// add by liyh 20120614 ��IND_STOCK
		// ���ѯҩƷ���ռ۸�ʱ��Ӧ����Ƭ/֧�ļ۸���С��λ�ļ۸� start
		double verpriceD = rowParm.getDouble("VERIFYIN_PRICE",i) / d_qty ;
				
		verpriceD = StringTool.round(verpriceD, 4);
		String verpriceDStr = String.valueOf(verpriceD);
		 
 
		//��Ӧ��ҩƷ����
		String supOrderCode = rowParm.getValue("SUP_ORDER_CODE",i);
		parm.setData("SUP_ORDER_CODE",supOrderCode);
		
		getSEQ = IndStockDTool.getInstance().onQueryStockBatchSeqBy(org_code, order_code, batch_no, valid_date, verpriceDStr,supCode,supOrderCode);//
		
		// *************************************************************
		// luahi 2012-01-10 add �����������ѡȡ�����������ռ۸�����end
		// *************************************************************
		if (getSEQ.getErrCode() < 0) {
			this.messageBox("ҩƷ" + order_code + "������Ŵ���");
			return getSEQ;
		}
		if (getSEQ.getCount("BATCH_SEQ") > 0) {
			flg = "U";
			// ������ҩƷ����
			batch_seq = getSEQ.getInt("BATCH_SEQ", 0);
		} else {
			flg = "I";
			// ������ҩƷ������,ץȡ�ÿ��ҩƷ���+1�����������
			getSEQ = IndStockDTool.getInstance().onQueryStockMaxBatchSeq(org_code, order_code);
			if (getSEQ.getErrCode() < 0) {
				this.messageBox("ҩƷ" + order_code + "������Ŵ���");
				return getSEQ;
			}
			// ���+1�������
			batch_seq = getSEQ.getInt("BATCH_SEQ", 0) + 1;
			material_loc_code = getSEQ.getValue("MATERIAL_LOC_CODE", 0);
		}
		// �����Ч�������߼���
		parm.setData("BATCH_SEQ", batch_seq);
		// ��λ
		parm.setData("MATERIAL_LOC_CODE", material_loc_code);
		// ���»�����IND_STOCK��FLG����
		parm.setData("UI_FLG", flg);
		// ����������
		
		qty = rowParm.getDouble("VERIFYIN_QTY",i);
		
		parm.setData("VERIFYIN_QTY", qty);
		// ����������
		g_qty = rowParm.getDouble("GIFT_QTY",i);
		
		parm.setData("GIFT_QTY",g_qty);

		// ���ۼ۸񼯺�
		retail = rowParm.getDouble("RETAIL_PRICE",i);
		
		parm.setData("RETAIL_PRICE", retail);
		
		// ���ռ۸񼯺� �޸����ռ۸�ĵ�λΪ��ҩ��λ
		double verifyinPrice = rowParm.getDouble("VERIFYIN_PRICE",i);
		//���ϼ۸�
		parm.setData("INVENT_PRICE",verifyinPrice);
		
		verprice = verifyinPrice/ d_qty ;
		
		
		parm.setData("VERIFYIN_PRICE", StringTool.round(verprice, 4));
		// zhangyong20091014 end
		// ������ż���
		int seq_no = rowParm.getInt("SEQ_NO",i);
		parm.setData("SEQ_NO", seq_no);
		// �ۼ������ۿ����
		deduct_atm =rowParm.getDouble("QUALITY_DEDUCT_AMT",i); 		
		parm.setData("QUALITY_DEDUCT_AMT", deduct_atm);
		// �������ż���
		pur_no = rowParm.getValue("PURORDER_NO",i);	
		parm.setData("PURORDER_NO", pur_no);
		// ����������ż���
		pur_no_seq = rowParm.getInt("PURSEQ_NO",i);
		
		parm.setData("PURSEQ_NO", pur_no_seq);

	/*	TParm inparm = new TParm();
		inparm.setData("PURORDER_NO", pur_no);
		inparm.setData("SEQ_NO", pur_no_seq);
		result = IndPurorderMTool.getInstance().onQueryDone(inparm);
		if (result.getCount() == 0 || result.getErrCode() < 0) {
			this.messageBox("����");
			return;
		}*/
		// ����������		                        
		pur_qty = result.getDouble("PURORDER_QTY", 0);
		parm.setData("PURORDER_QTY",pur_qty);
		// ���ƽ���ۼ���
		stock_price = result.getDouble("STOCK_PRICE", 0);
		parm.setData("STOCK_PRICE", stock_price);
		
		// �ۼ�������
		parm.setData("ACTUAL_QTY", result.getDouble("ACTUAL_QTY", i));
		// ״̬
		String update_flg = "3";
		parm.setData("UPDATE_FLG",update_flg);
 
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", date);
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());	
		parm.setData("VERIFYIN_NO", verifyin_no);
		 
		parm.setData("PRC",rowParm.getDouble("PRC",i));
		
		// ִ�����ݸ���
		result = TIOM_AppServer.executeAction("action.spc.INDVerifyinAction", "onUpdateSpc", parm);
		// �����ж�
		if (result == null || result.getErrCode() < 0) {
			this.messageBox("E0001");
			return result;
		}   
		return result;
		//this.messageBox("P0001");
	}
	

	private String getApRegion( String orgCode) {
		TParm searchParm = new TParm () ;
		searchParm.setData("ORG_CODE",orgCode);
		TParm resulTParm = SPCGenDrugPutUpTool.getInstance().onQueryLabelByOrgCode(searchParm);
		String apRegion = "";
		if(resulTParm != null ){
			apRegion = resulTParm.getValue("AP_REGION");
		}
		return apRegion;
	}
	


	/**
	 * ��ղ���
	 * */
	public void onClear() {
		String controlName = "VERIFYIN_NO;CONTAINER_TAGS;ELECTRONIC_TAGS";
		this.clearValue(controlName);
		this.getTextField("VERIFYIN_NO").setEditable(true);
		table_Y.removeRowAll();
		table_N.removeRowAll();
		this.getTextField("CONTAINER_TAGS").grabFocus();
	}
	
	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}

	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}
	
}
