package com.javahis.ui.odo;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jdo.odo.OpdOrder;
import jdo.opd.ODOTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

public class ODOMainDeleteRxControl extends TControl{
	
	private OdoMainControl odoMainControl;
	private OpdOrder order;
	private TTable mTable;
	private TTable dTable;
	private Map<String, String> routeDoseMap;
	
	public void onInit(){
		odoMainControl = (OdoMainControl) getParameter();
		order = odoMainControl.odo.getOpdOrder();
		mTable = (TTable) getComponent("M_TABLE");
		dTable = (TTable) getComponent("D_TABLE");
		routeDoseMap = ODOTool.getInstance().getRouteDoseMap();
		initMtable();
	}
	
	private void initMtable(){
		order.setFilter("RX_TYPE='1'");
		order.filter();
		List<String> list = new ArrayList<String>();
		String presrtNo;
		TParm p = new TParm();
		int rx = 0;
		for (int i = 0; i < order.rowCount(); i++) {
			presrtNo = order.getItemString(i, "RX_NO") + order.getItemString(i, "PRESRT_NO");
			if(!list.contains(presrtNo) && order.getItemString(i, "ORDER_CODE").length() != 0){
				rx++;
				list.add(presrtNo);
				p.addData("RX_NO", order.getItemString(i, "RX_NO"));
				p.addData("PRESRT_NO", order.getItemString(i, "PRESRT_NO"));
				p.addData("NO", presrtNo);
				p.addData("DESC", "����ǩ" + rx);
//				p.addData("DOSE_TYPE", routeDoseMap.get(order.getItemString(i, "ROUTE_CODE")));
				p.addData("O", "N");
				p.addData("E", "N");
				p.addData("I", "N");
				p.addData("F", "N");
				p.setData(routeDoseMap.get(order.getItemString(i, "ROUTE_CODE")), rx - 1, "Y");
			}
		}
		mTable.setParmValue(p);
	}
	
	public void onDelete() throws Exception{
		
		mTable.acceptText();
		int row = mTable.getSelectedRow();
		TParm p = mTable.getParmValue();
		
		if(p.getCount("RX_NO") < 1){
			messageBox("�޴���");
			return;
		}
		
		if(row < 0){
			messageBox("��ѡ�񴦷���");
			return;
		}
		
		StringBuffer billFlg=new StringBuffer();//�ж��Ƿ����ɾ�� ��ͬһ�Ŵ���ǩ�е�״̬����ͬ����ɾ��
		billFlg.append(order.getItemData(0, "BILL_FLG"));
		
		int count = order.rowCount();
		
		TParm parm=new TParm();
		if (count <= 0) {
			return;
		}
		for (int i = count - 1; i > -1; i--) {
				//������start
				if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 У��������ע��
					if (!TypeTool.getBoolean(order.getItemData(i, "RELEASE_FLG"))) {// ���ж��Ƿ����Ա�ҩ  add by huangtt 20150803
						if (!order.checkDrugCanUpdate("MED", i,parm,false)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
							messageBox(parm.getValue("MESSAGE"));
							return;
						}
					}
					
				} else {
					if (i - 1 >= 0) {
						if (!odoMainControl.odoMainSpc.checkDrugCanUpdate(order, "MED", i - 1,
								false,null)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
							messageBox("E0189");
							return;
						}
					}
				}
				//������end
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!odoMainControl.odoMainOpdOrder.deleteOrder(order, i, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED2)) {
				return;
			} 
			if(!odoMainControl.odoMainOpdOrder.deleteSumRxOrder(order, i, billFlg)){
				return;
			}
			
		}
		if (odoMainControl.messageBox("��ʾ","�Ƿ�ɾ������ǩ",2)!=0) {//=========pangben 2014-1-15
			return;
		}
		for (int i = count - 1; i > -1; i--) {
			order.deleteRow(i);
		}
		
		order.setFilter("RX_NO='" + p.getValue("RX_NO", row) + "'");
		order.filter();
		
		if(order.rowCount() < 1){
			order.newOrder("1" , p.getValue("RX_NO", row));
		}
		super.closeWindow();
	}
	
	public void onTmClicked(){
		mTable.acceptText();
		int row = mTable.getSelectedRow();
		TParm p = mTable.getParmValue();
		
		String rxNo = p.getValue("RX_NO", row);
		String presrtNo = p.getValue("PRESRT_NO", row);
		
		order.setFilter("RX_NO='" + rxNo + "' AND PRESRT_NO = '" + presrtNo + "'");
		order.filter();
		
		dTable.setDataStore(order);
		dTable.setDSValue();
	}
	
}
