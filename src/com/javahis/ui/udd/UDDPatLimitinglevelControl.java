package com.javahis.ui.udd;
import java.sql.Timestamp; 
import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.udd.UDDPatLimitinglevelTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable; 
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;


/**
 * <p>
 * Title: ��/��/סʹ�����Ƽ����Ͽ���ҩ�����õĻ��߱���ͳ��
 * </p>
 * 
 * <p>
 * Description: ��/��/סʹ�����Ƽ����Ͽ���ҩ�����õĻ��߱���ͳ��
 * </p>
 * 
 * <p>
 * Copyright: Bluecore
 * 20140212
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author caoy
 * @version 1.0
 */
public class UDDPatLimitinglevelControl extends TControl {
	private TTable table_i;// סԺ��
	private TTable table_oe;// �ż����
	private TTable table_oes;// �ż����---����
	private int pageFlg = 0;// ҳǩ��ǣ�0��סԺ��1���ż��

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ��ʼ��ʱ��ؼ�
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("S_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 00:00:00");
		this.setValue("E_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 23:59:59");
		this.table_i = this.getTable("TABLE_I");// �õ�סԺ���
		this.table_oe = this.getTable("TABLE_OE");// �õ��ż�����
		table_oes = this.getTable("TABLE_OES");// �õ��ż�����---����
		// ��ʼ��Ժ��
		setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		setValue("OPT_USER", Operator.getID());// ��ʼ��������
		this.clearValue("ODI_TYPE;DEPT_CODE;DR_CODE;ADM_TYPE;DR_OE_CODE");
		this.setValue("ODI_TYPE", "2");
		this.callFunction("UI|ODI_TYPE|setEnabled", false);
	}

	/**
	 * ��ȡ������
	 * 
	 * @param tagName
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	/**
	 * ҳǩ����¼�
	 */
	public void onChangeTTabbedPane() {
		TTabbedPane tabbedPane = ((TTabbedPane) this
				.getComponent("tTabbedPane_2"));
		if (tabbedPane.getSelectedIndex() == 0) {
			pageFlg = 0;// סԺ
		} else if (tabbedPane.getSelectedIndex() == 1) {
			pageFlg = 1;// �ż���
		} else {
			pageFlg = 2;// �ż���--����
		}
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm parm=new TParm();
		TParm reultTotnum=new TParm();
		TParm resultantnum=new TParm();
		DecimalFormat dec=new DecimalFormat("##.##%");
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMddHHmmss");
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMddHHmmss");
				parm.setData("S_DATE",sDate);
				parm.setData("E_DATE",eDate);
				parm.setData("DR_CODE",this.getValueString("DR_CODE"));
				parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
				
		
		
		TTabbedPane tabbedPane = ((TTabbedPane) this
				.getComponent("tTabbedPane_2"));
		if (tabbedPane.getSelectedIndex() == 0) {
			
			reultTotnum=UDDPatLimitinglevelTool.getInstance().getSelectTotnum(parm);
			if(reultTotnum.getErrCode()<0){
				this.messageBox("��ѯ���ֳ�����");
				return;
			}
			
			resultantnum=UDDPatLimitinglevelTool.getInstance().getselectAntnum(parm);
			
			if(resultantnum.getErrCode()<0){
				this.messageBox("��ѯ���ֳ�����");
				return;
			}
			int antNum=0;//�Ƽ���������
			/** �ϲ��������Ƽ��������� **/
			for(int i=0;i<reultTotnum.getCount();i++){
				boolean flag=false;
				for(int j=0;j<resultantnum.getCount();j++){
					if(reultTotnum.getValue("DEPT_CODE",i).equals(resultantnum.getValue("DEPT_CODE",j))&&
							reultTotnum.getValue("VS_DR_CODE",i).equals(resultantnum.getValue("VS_DR_CODE",j))){
						    antNum=resultantnum.getInt("ANTI_NUM",j);
						    flag=true;
						    break;
					}
				}
				
				if(flag){
					 reultTotnum.addData("ANTI_NUM", antNum)	;
				}else{
					 reultTotnum.addData("ANTI_NUM", 0)	;
				}
				
				
			}
			/** �������Ƽ������������� **/
			double antiRate=0;//����
			double aNum=0;//����������
			double tNum=0;//������
			for(int a=0;a<reultTotnum.getCount();a++){
				aNum=reultTotnum.getDouble("ANTI_NUM",a);
				tNum=reultTotnum.getDouble("TOT_NUM",a);
				if(tNum!=0){
					antiRate=aNum/tNum;
				}
				reultTotnum.addData("ANTI_RATE", dec.format(antiRate));
			}
			
			/** ���С�� **/
			String deptCode=reultTotnum.getValue("DEPT_CODE", 0);
			double totnum=0;
			double antnum=0;
			double antirate=0;
			TParm sparm=new TParm();
			for(int k=0;k<reultTotnum.getCount();k++){
				if(deptCode.equals(reultTotnum.getValue("DEPT_CODE", k))){
					sparm.addRowData(reultTotnum, k);
					totnum+=reultTotnum.getDouble("TOT_NUM", k);
					antnum+=reultTotnum.getDouble("ANTI_NUM", k);
				}else{
					deptCode=reultTotnum.getValue("DEPT_CODE", k);
					sparm.addData("REGION_CODE", "С��:");
					sparm.addData("ODI_TYPE",reultTotnum.getValue("ODI_TYPE", k-1) );
					sparm.addData("DEPT_CODE", reultTotnum.getValue("DEPT_CODE", k-1));
					sparm.addData("VS_DR_CODE", "");
					sparm.addData("TOT_NUM", totnum);
					sparm.addData("ANTI_NUM", antnum);
					sparm.addData("ANTI_RATE", totnum!=0?dec.format(antnum/totnum):dec.format(antirate));
					 totnum=0;
					 antnum=0;
					 antirate=0;
					 if(deptCode.equals(reultTotnum.getValue("DEPT_CODE", k))){
						    sparm.addRowData(reultTotnum, k);
						    totnum+=reultTotnum.getDouble("TOT_NUM", k);
							antnum+=reultTotnum.getDouble("ANTI_NUM", k);
					 }
				}
				if(k==reultTotnum.getCount()-1){
					sparm.addData("REGION_CODE", "С��:");
					sparm.addData("ODI_TYPE",reultTotnum.getValue("ODI_TYPE", k-1) );
					sparm.addData("DEPT_CODE", reultTotnum.getValue("DEPT_CODE", k-1));
					sparm.addData("VS_DR_CODE", "");
					sparm.addData("TOT_NUM", totnum);
					sparm.addData("ANTI_NUM", antnum);
					sparm.addData("ANTI_RATE", totnum!=0?dec.format(antnum/totnum):dec.format(antirate));
				}
				
				
			}
			table_i.setParmValue(sparm);
			
			
			
			
		} 

	}
	
	/**
	 * ���Excel
	 */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		try {
			TTabbedPane tabbedPane = ((TTabbedPane) this
					.getComponent("tTabbedPane_2"));
			if (tabbedPane.getSelectedIndex() == 0) {
				if (table_i.getParmValue().getCount()<=0) {
					messageBox("û�пɵ�������ݣ�");
					return;
				}
				ExportExcelUtil.getInstance().exportExcel(table_i,
						"סԺ���߿���ҩ�ﴦ������ͳ��");
			} else if (tabbedPane.getSelectedIndex() == 1) {
				if (table_oe.getParmValue().getCount()<=0) {
					messageBox("û�пɵ�������ݣ�");
					return;
				}
				ExportExcelUtil.getInstance().exportExcel(table_oe,
						"�ż��ﻼ�߿���ҩ�ﴦ������ͳ��-����");
			}else{
				if (table_oes.getParmValue().getCount()<=0) {
					messageBox("û�пɵ�������ݣ�");
					return;
				}
				ExportExcelUtil.getInstance().exportExcel(table_oes,
				"�ż��ﻼ�߿���ҩ�ﴦ������ͳ��-����");
			}

		} catch (NullPointerException e) {
			// TODO: handle exception
			messageBox("û�пɵ�������ݣ�");
			return;
		}
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		Timestamp date = SystemTool.getInstance().getDate();
		// ����ʱ��ؼ���ֵ
		this.setValue("S_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 00:00:00");
		this.setValue("E_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 23:59:59");
		// �������е�����
		table_i.setParmValue(new TParm());
		table_oe.setParmValue(new TParm());
		table_oes.setParmValue(new TParm());
		// ��������ؼ���ֵ
		this.clearValue("OPT_USER;ODI_TYPE;DEPT_CODE;DR_CODE;ADM_TYPE;DR_OE_CODE");
		this.setValue("ODI_TYPE", "2");
	}
}
