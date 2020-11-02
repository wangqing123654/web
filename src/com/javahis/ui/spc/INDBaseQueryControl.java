package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**   
 * <p>
 * Title: ����ҩ��ѯControl
 * </p>
 *   
 * <p>
 * Description: ����ҩ��ѯControl  
 * </p>
 *     
 * <p>
 * Copyright: Copyright (c) 2008   
 * </p>
 * 
 * <p>                   
 * Company: bluecore  
 * </p>
 * 
 * @author wjc 2015.01.16 
 * @version 1.0
 */
public class INDBaseQueryControl extends TControl {
	
	private SimpleDateFormat formateDate = new SimpleDateFormat("yyyy-MM-dd");
	private TTable table;
	private String start_date;
	private String end_date;

	/**
	 * ��ʼ������
	 */
	public void onInit(){
		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		// ���õ����˵�
		TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		this.table = this.getTable("TABLE");
		//��ѯ����
		this.start_date = formateDate.format(this.getTextFormat("START_DATE").getValue());
		this.end_date = formateDate.format(this.getTextFormat("END_DATE").getValue());
		//��ѯ��ҩ����
		String sql = "SELECT BASEMANAGE_NO FROM IND_BASEMANAGEM WHERE OPT_DATE BETWEEN TO_DATE ('"
				+this.start_date+" 00:00:00','YYYY-MM-DD HH24:MI:SS') AND TO_DATE ('"
				+this.end_date+" 23:59:59','YYYY-MM-DD HH24:MI:SS') ";
		//��ҩ����
		String app_org_code = this.getValueString("APP_ORG_CODE");
		if(!app_org_code.equals("")){
			sql += " AND APP_ORG_CODE ='"+app_org_code+"' ";
		}
		//��Ӧ����
		String to_org_code = this.getValueString("TO_ORG_CODE");
		if(!to_org_code.equals("")){
			sql += " AND TO_ORG_CODE ='"+to_org_code+"' ";
		}
		//��ҩ����
		String basemanage_no = this.getValueString("BASEMANAGE_NO");
		if(!basemanage_no.equals("")){
			sql += " AND BASEMANAGE_NO ='"+basemanage_no+"' ";
		}	
		//��ѯ����
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() <= 0){
			this.table.removeRowAll();
			this.messageBox("δ��ѯ����Ӧ���ݣ�");
			return;
		}
		//ҩƷ����
		String order_code = this.getTextField("ORDER_CODE").getValue();
		TParm result = new TParm();
		//��ѯ��ϸ
		for(int i=0;i<parm.getCount();i++){
			String item_sql_start = "SELECT A.BASEMANAGE_NO, "
								+" A.QTY, G.UNIT_CHN_DESC AS UNIT, A.RETAIL_PRICE, C.VERIFYIN_PRICE, "
								+" SUM(C.STOCK_QTY) AS STOCK_QTY, H.DOSAGE_QTY, D.ORDER_DESC, D.SPECIFICATION, "
								+" E.DEPT_CHN_DESC AS APP_CHN_DESC, F.DEPT_CHN_DESC AS TO_CHN_DESC, "
								+" I.UNIT_CHN_DESC AS BIG_UNIT, J.UNIT_CHN_DESC AS SMALL_UNIT "
					+" FROM IND_BASEMANAGED A, IND_BASEMANAGEM B, IND_STOCK C, "
							+" PHA_BASE D, SYS_DEPT E, SYS_DEPT F, SYS_UNIT G, PHA_TRANSUNIT H, "
							+" SYS_UNIT I, SYS_UNIT J "
						+" WHERE A.BASEMANAGE_NO='" +parm.getValue("BASEMANAGE_NO", i)+ "' "
								+" AND A.BASEMANAGE_NO = B.BASEMANAGE_NO "
								+" AND B.APP_ORG_CODE = C.ORG_CODE "
								+" AND B.APP_ORG_CODE = E.DEPT_CODE "
								+" AND B.TO_ORG_CODE = F.DEPT_CODE "
								+" AND A.ORDER_CODE = D.ORDER_CODE "
								+" AND A.ORDER_CODE = C.ORDER_CODE "
								+" AND A.UNIT_CODE = G.UNIT_CODE "
								+" AND A.ORDER_CODE = H.ORDER_CODE "
								+" AND H.STOCK_UNIT = I.UNIT_CODE "
								+" AND H.DOSAGE_UNIT = J.UNIT_CODE ";
			String item_sql_end = " GROUP BY "
									+" A.BASEMANAGE_NO, "
									+" A.QTY, G.UNIT_CHN_DESC, A.RETAIL_PRICE, C.VERIFYIN_PRICE, "
									+" D.ORDER_DESC, D.SPECIFICATION, "
									+" E.DEPT_CHN_DESC, F.DEPT_CHN_DESC, "
									+" I.UNIT_CHN_DESC, J.UNIT_CHN_DESC, "
									+" H.DOSAGE_QTY, A.SEQ_NO "
							+ "ORDER BY A.SEQ_NO, A.BASEMANAGE_NO ";
			if(!order_code.equals("")){
				item_sql_start += " AND A.ORDER_CODE = '"+order_code+"' ";
			}
//			System.out.println(item_sql_start+item_sql_end);
			TParm value = new TParm(TJDODBTool.getInstance().select(item_sql_start+item_sql_end));
			for(int j=0;j<value.getCount();j++){
				//��ҩ����
				result.addData("APP_CHN_DESC", value.getValue("APP_CHN_DESC", j));
				//��Ӧ����
				result.addData("TO_CHN_DESC", value.getValue("TO_CHN_DESC", j));
				//ҩƷ����
				result.addData("ORDER_DESC", value.getValue("ORDER_DESC", j));
				//���
				result.addData("SPECIFICATION", value.getValue("SPECIFICATION", j));
				//��ҩ����
				result.addData("QTY", value.getValue("QTY", j));
				//��λ
				result.addData("UNIT", value.getValue("UNIT", j));
				//����
				result.addData("YS_PRICE", value.getValue("VERIFYIN_PRICE", j));
				//���ۼ�
				result.addData("RETAIL_PRICE", value.getValue("RETAIL_PRICE", j));
				//��ǰ���
				int packQty = value.getInt("STOCK_QTY", j)/value.getInt("DOSAGE_QTY", j);//����װ
				int scatteredQty = value.getInt("STOCK_QTY", j)%value.getInt("DOSAGE_QTY", j);//��ɢ��װ
				result.addData("STOCK_QTY", packQty+value.getValue("BIG_UNIT", j)+scatteredQty+value.getValue("SMALL_UNIT", j));
				//��ҩ����
				result.addData("BASEMANAGE_NO", value.getValue("BASEMANAGE_NO", j));
			}
		}
		this.table.setParmValue(result);
	}
	
	/**
	 * ���
	 */
	public void onClear(){
		String clearStr = "APP_ORG_CODE;ORDER_CODE;ORDER;BASEMANAGE_NO;TO_ORG_CODE";
		this.clearValue(clearStr);
		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		this.table.removeRowAll();
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
        String order = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order))
            getTextField("ORDER").setValue(order);
    }
	
    /**
     * ��ӡ����
     */
	public void onPrint(){
		this.table = this.getTable("TABLE");
		if(this.table.getRowCount()>0){
			//��ͷ����
			TParm date = new TParm();
			//�Ʊ�ʱ��
			date.setData("DATA", "TEXT",
                    "ͳ������: " + this.start_date +" 00:00:00��"+this.end_date+" 23:59:59");
			//�Ʊ���
			date.setData("USER", "TEXT", Operator.getName());
			//�������
			TParm parm = new TParm();
			for(int i = 0; i < this.table.getRowCount(); i++){
				parm.addData("APP_CHN_DESC",this.table.getItemString(i, "APP_CHN_DESC"));
				parm.addData("TO_CHN_DESC",this.table.getItemString(i, "TO_CHN_DESC"));
				parm.addData("ORDER_DESC",this.table.getItemString(i, "ORDER_DESC"));
				parm.addData("SPECIFICATION",this.table.getItemString(i, "SPECIFICATION"));
				parm.addData("QTY",this.table.getItemString(i, "QTY"));
				parm.addData("UNIT",this.table.getItemString(i, "UNIT"));
				parm.addData("YS_PRICE",this.table.getItemString(i, "YS_PRICE"));
				parm.addData("RETAIL_PRICE",this.table.getItemString(i, "RETAIL_PRICE"));
				parm.addData("STOCK_QTY",this.table.getItemString(i, "STOCK_QTY"));
				parm.addData("BASEMANAGE_NO",this.table.getItemString(i, "BASEMANAGE_NO"));
				
			}
			parm.setCount(parm.getCount("APP_CHN_DESC"));
			parm.addData("SYSTEM", "COLUMNS", "APP_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "TO_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			parm.addData("SYSTEM", "COLUMNS", "QTY");
			parm.addData("SYSTEM", "COLUMNS", "UNIT");
			parm.addData("SYSTEM", "COLUMNS", "YS_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "RETAIL_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
			parm.addData("SYSTEM", "COLUMNS", "BASEMANAGE_NO");
			date.setData("TABLE",parm.getData());
			this.openPrintWindow("%ROOT%\\config\\prt\\spc\\INDBaseQuery.jhw", date);
		}else{
			this.messageBox("û�д�ӡ����");
            return;
		}
	}
	
	/**
	 * ����Excel
	 */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "ҩ��������ѯ��ϸ��");
	}
	
	/**
	 * ��ȡTTextFormat�ؼ�
	 * @param tag
	 * @return TTextFormat
	 */
	public TTextFormat getTextFormat(String tag){
		return (TTextFormat) this.getComponent(tag);
	}
	
	/**
	 * ��ȡTTextField�ؼ�
	 * @param tag
	 * @return TTextField
	 */
	public TTextField getTextField(String tag){
		return (TTextField) this.getComponent(tag);
	}
	
	/**
	 * ��ȡTTable�ؼ�
	 * @param tag 
	 * @return TTable
	 */
	public TTable getTable(String tag){
		return (TTable) this.getComponent(tag);
	}
}
