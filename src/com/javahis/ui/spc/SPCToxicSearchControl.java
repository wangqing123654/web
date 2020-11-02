package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.spc.SPCToxicSearchTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: �龲������ϸControl
 * </p>
 *
 * <p>
 * Description: �龲������ϸControl
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: bluecore
 * </p>
 *
 * @author:��fuwj 20130906
 * @version 1.0
 */
public class SPCToxicSearchControl extends TControl {
	
	// ������
	private TTable table;

	public SPCToxicSearchControl() {
		super();
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		this.initPage();
	}
	
	/**
	 * ��ʼ������
	 */
	private void initPage() {
		 // ��ʼ������ʱ��
        // ��������
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
		table = this.getTable("TOXIC_TABLE");
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");		
		// ���õ����˵�
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����  
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}
	
	/**
	 * ��ѯ
	 */
	 public void onQuery() {
		 table.removeRowAll();
	     String orgCode = this.getValueString("ORG_CODE");
	     TParm searchParm = new TParm();
	     searchParm.setData("ORG_CODE", orgCode);
	     String startDate = this.getValueString("START_DATE");
	     String endDate = this.getValueString("END_DATE");
	     String toxicId = this.getValueString("TOXIC_ID");
	     String mrNo = this.getValueString("MR_NO");
	     String orderCode =this.getValueString("ORDER_CODE");
	     searchParm.setData("START_DATE",startDate);
	     searchParm.setData("END_DATE",endDate);
	     searchParm.setData("TOXIC_ID",toxicId);
	     searchParm.setData("MR_NO",mrNo);
	     searchParm.setData("ORDER_CODE",orderCode);
	     if("".equals(orgCode)||orgCode==null) {
	    	 TParm result = SPCToxicSearchTool.getInstance().getAll(searchParm);
		     if (result == null || result.getErrCode() < 0||result.getCount()<=0) {					
	             this.messageBox("�޲�ѯ����");
	             this.setValue("COUNT", "");
	             return;
	         }		
		     this.setValue("COUNT", result.getCount()+"");
	    	 table.setParmValue(result);
	    	 return;
	     }
	     TParm result = SPCToxicSearchTool.getInstance().getOrgCode(searchParm);
	     if (result == null || result.getErrCode() < 0) {
             this.messageBox("�޲�ѯ����");
             this.setValue("COUNT", "");
             return;
         }		
	     String stationFlg = (String) result.getData("STATION_FLG",0);
	     if("Y".equals(stationFlg)) {
	    	 result = SPCToxicSearchTool.getInstance().getStationDate(searchParm);
	    	 if (result == null || result.getErrCode() < 0||result.getCount()<=0) {
	             this.messageBox("�޲�ѯ����");
	             this.setValue("COUNT", "");
	             return;
	         }
	    	 this.setValue("COUNT", result.getCount()+"");
	    	 table.setParmValue(result);
	     }/*else if("040102".equals(orgCode)){
	    	 result = SPCToxicSearchTool.getInstance().getOpdDate(searchParm);
	    	 if (result == null || result.getErrCode() < 0||result.getCount()<=0) {
	             this.messageBox("�޲�ѯ����");
	             this.setValue("COUNT", "");
	             return;
	         }
	    	 this.setValue("COUNT", result.getCount()+"");
	    	 table.setParmValue(result);
	     }else {
	    	 result = SPCToxicSearchTool.getInstance().getInvDate(searchParm);
	    	 if (result == null || result.getErrCode() < 0||result.getCount()<=0) {
	             this.messageBox("�޲�ѯ����");
	             this.setValue("COUNT", "");
	             return;
	         }
	    	 this.setValue("COUNT", result.getCount()+"");
	    	 table.setParmValue(result);
	     }*/
	     
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
		
		public void onQueryMrNo() {
			TParm searchParm = new TParm();
			String mrNo = this.getValueString("MR_NO");
			if("".equals(mrNo)||mrNo==null) {
				this.messageBox("�޴˲�����");
	             return;
			}
			mrNo = StringTool.fillLeft(mrNo ,12 ,"0" );
			searchParm.setData("MR_NO",mrNo);
			TParm result = SPCToxicSearchTool.getInstance().getMrNo(searchParm);
		     if (result == null || result.getErrCode() < 0) {
	             this.messageBox("�޴˲�����");
	             this.setValue("MR_NO", "");
	             return;
	         }
		     mrNo = (String) result.getData("MR_NO",0);
		     String patName = (String) result.getData("PAT_NAME",0);
		     this.setValue("MR_NO", mrNo);
		     this.setValue("PAT_NAME", patName);
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
		 * ���ܷ���ֵ����
		 * 
		 * @param tag
		 * @param obj
		 */
		public void popReturn(String tag, Object obj) {
			TParm parm = (TParm) obj;
			String order_code = parm.getValue("ORDER_CODE");
			if (!StringUtil.isNullString(order_code)) {
				getTextField("ORDER_CODE").setValue(order_code);
			}
			String order_desc = parm.getValue("ORDER_DESC");
			if (!StringUtil.isNullString(order_desc)) {
				getTextField("ORDER_DESC").setValue(order_desc);
			}
		}
		
		public void onClear() {
			this.setValue("TOXIC_ID", "");
			this.setValue("ORG_CODE", "");
			this.setValue("ORDER_CODE", "");
			this.setValue("ORDER_DESC", "");
			this.setValue("MR_NO", "");
			this.setValue("PAT_NAME", "");
			this.setValue("COUNT", "");
			table.removeRowAll();
		}
		
		 /**
	     * ���Excel
	     */
	    public void onExportXls() {
	        TTable table = this.getTable("TOXIC_TABLE");
	        if (table.getRowCount() <= 0) {
	            this.messageBox("û�л������");   
	            return;
	        }
	        ExportExcelUtil.getInstance().exportExcel(table, "�龫ҩƷʹ�ü�¼");
	    }

}
