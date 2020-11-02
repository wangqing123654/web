package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.util.Date;

import jdo.reg.REGMedicalTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
/**
 * <p>Title: ��/����Һž�������ͳ�Ʊ���</p>
 *
 * <p>Description: ����/����Һž�������ͳ����</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2013</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2013.7.05
 * @version 1.0
 */
	public class REGMedicalControl extends TControl {
		private TTable table;

		public void onInit() {
	
			initPage();
	}

	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
	 /**
	 * ��ѯ��/����Һž�������
	 */
	public void onQuery() {
		if(getValueString("ADM_TYPE").length() == 0){ //��֤�ż����ѯ���
			messageBox("��ѡ���ż���");
			return;
		}
		table.removeRowAll();
		TParm parm = new TParm();
		 //���ղ�ѯʱ���
//		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMddhhmmss");
//		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMddhhmmss");
		String sDate = getValueString("S_DATE");
		sDate = sDate.substring(0, 4)+sDate.substring(5, 7)+sDate.substring(8, 10)+sDate.substring(11, 13)+sDate.substring(14, 16)+sDate.substring(17, 19);
		String eDate = getValueString("E_DATE");
		eDate = eDate.substring(0, 4)+eDate.substring(5, 7)+eDate.substring(8, 10)+eDate.substring(11, 13)+eDate.substring(14, 16)+eDate.substring(17, 19);
		parm.setData("S_DATE", sDate);
		parm.setData("E_DATE", eDate);
        //��֤�����������
		if (getValue("ADM_TYPE").toString().length() != 0
				&& getValue("ADM_TYPE") != null) {
			parm.setData("ADM_TYPE", getValue("ADM_TYPE"));
		}
        //��֤����ʱ��ҽ��
		if (this.getValueString("REALDR_CODE").length() != 0
				&& this.getValueString("REALDR_CODE") != null) {
			parm.setData("REALDR_CODE", this.getValueString("REALDR_CODE"));
		}
		//��֤����ʱ�����
		if (this.getValueString("REALDEPT_CODE").length() != 0
				&& this.getValueString("REALDEPT_CODE") != null) {
			parm.setData("REALDEPT_CODE", this.getValueString("REALDEPT_CODE"));
		}
		TParm result = new TParm();
		if(getValueString("ADM_TYPE").equals("E")){ //�����ѯ
			result = REGMedicalTool.getInstance().selectdata(parm);
		}else{
			result = REGMedicalTool.getInstance().selectdataO(parm); //�����ѯ
		}
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ��������");
			return;
		}
		if (result.getCount()<=0) {
			this.messageBox("û�в�ѯ������");
			return;
		}
			int count = 0;//����
			int index=0;//С��ͳ��
			TParm resultParm=new TParm();
			String date="";//����ʱ��
			String type="";//����״̬:N δ����  T �ݴ�  Y �����
			String stype="";//���Ｖ��
			stype=result.getValue("ADM_TYPE",0);
			date=result.getValue("REG_DATE",0);
			type=result.getValue("SEE_DR_FLG",0);
			for (int i = 0; i < result.getCount("MR_NO"); i++) {
			
			if (date.equals(result.getValue("REG_DATE", i)) &&//�ۼƸ���ʱ�䡢�������;���״̬ ʵ��С�ƹ���
					type.equals(result.getValue("SEE_DR_FLG", i))&& 
					stype.equals(result.getValue("ADM_TYPE", i))) {
				resultParm.setRowData(count, result, i);
				resultParm.setData("SEE_DR_FLG", count, onGetSeeDrType(result.getValue("SEE_DR_FLG", i)));//����״̬ת������
				count++;
				index++;
			}else{//����ͬ ���С��
				resultParm.addData("REG_DATE", "����״̬С��");
				resultParm.addData("ADM_TYPE","" );
				resultParm.addData("MR_NO", "");
				resultParm.addData("SESSION_CODE", "");
				resultParm.addData("ADM_TYPE", "");
				resultParm.addData("SEX_CODE", "");
				resultParm.addData("PAT_NAME", "");
				resultParm.addData("SEE_DR_FLG", "");
				resultParm.addData("REALDEPT_CODE", "");
				resultParm.addData("REALDR_CODE", index);
				count++;
				index=0;
				date=result.getValue("REG_DATE",i);
				type=result.getValue("SEE_DR_FLG",i);
				stype=result.getValue("ADM_TYPE",i);
				if (date.equals(result.getValue("REG_DATE", i)) &&//�ۼƸ���ʱ�䡢�������;���״̬ ʵ��С�ƹ���
						type.equals(result.getValue("SEE_DR_FLG", i))&& 
						stype.equals(result.getValue("ADM_TYPE", i))) {
					resultParm.setRowData(count, result, i);
					resultParm.setData("SEE_DR_FLG", count, onGetSeeDrType(result.getValue("SEE_DR_FLG", i)));//����״̬ת������
					count++;
					index++;
				}
			}
			if (i==result.getCount()-1) {
				resultParm.addData("REG_DATE", "����״̬С��");
				resultParm.addData("ADM_TYPE", result.getValue("ADM_TYPE", 0));
				resultParm.addData("MR_NO", "");
				resultParm.addData("SESSION_CODE", "");
				resultParm.addData("PAT_NAME", "");
				resultParm.addData("SEX_CODE", "");
				//resultParm.addData("SEE_DR_FLG",onGetSeeDrType(result.getValue("SEE_DR_FLG", i)));
				resultParm.addData("SEE_DR_FLG","");
				resultParm.addData("REALDEPT_CODE", "");
				resultParm.addData("REALDR_CODE", index);
			}
		}
		resultParm.setCount(count);
		table.setParmValue(resultParm);

	}
	/**
	 *��ʼ��
	 */
	private void initPage() {

		table = getTable("TABLE");
		//����ѯʱ��θ�ֵ
		 //Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        // Timestamp today = SystemTool.getInstance().getDate();
        //setValue("S_DATE", yesterday);
        //setValue("E_DATE", today);
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("E_DATE",date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("S_DATE", date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        this.setValue("ADM_TYPE","O");
	}
	  /**
	 * �������
	 */
	public void onClear() {
		table.removeRowAll();
		// ��ջ�������
		String clearString = "S_DATE;E_DATE;REALDEPT_CODE;REALDR_CODE";
		clearValue(clearString);
		initPage();//���¸���ѯʱ��θ�ֵ
		
	}
    /**
     * ��ӡ
     */
	public void onPrint() {
		// REG_DATE;ADM_TYPE;MR_NO;PAT_NAME;SEX_CODE;SESSION_CODE;SEE_DR_FLG;REALDEPT_CODE;REALDR_CODE
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyy/MM/dd HH:mm:ss");
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyy/MM/dd HH:mm:ss");
		table = (TTable) this.getComponent("TABLE");
		int row = table.getRowCount()-2;
		
		Timestamp today = SystemTool.getInstance().getDate();
		TParm parm = new TParm();
		TParm tableParm = table.getParmValue();
		if (table.getRowCount() > 0) {
			// ��ӡ����
			TParm date = new TParm();
			// ��ͷ����   
			date.setData("TITLE", "TEXT", "��/����Һž�������ͳ�Ʊ���");
			date.setData("REG_DATE","TEXT","ͳ��ʱ��:"+sDate+"--"+eDate);
			//���ѡ����Ҳ�ѯ��������ͷ��ֵ����
			if (this.getValueString("REALDEPT_CODE").length() != 0
					&& this.getValueString("REALDEPT_CODE") != null) {
				date.setData("REALDEPT_CODE","TEXT", "ʱ�����:"+tableParm.getValue("REALDEPT_CODE",0));
			}
			date.setData("USER","TEXT","��ӡ��Ա:"+Operator.getName());
			// �������
			
			for (int i = 0; i < table.getRowCount(); i++) {
				parm.addData("REG_DATE", tableParm.getValue("REG_DATE", i));
				parm.addData("SEX_CODE", tableParm.getValue("SEX_CODE", i));
				parm.addData("ADM_TYPE", tableParm.getValue("ADM_TYPE", i));
				parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
				parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
				parm.addData("SESSION_CODE", tableParm.getValue("SESSION_CODE",
						i));
				parm.addData("SEE_DR_FLG", tableParm.getValue("SEE_DR_FLG", i));
				parm.addData("REALDEPT_CODE", tableParm.getValue(
						"REALDEPT_CODE", i));
				parm.addData("REALDR_CODE", tableParm
						.getValue("REALDR_CODE", i));

			}
			
			parm.setCount(parm.getCount("REG_DATE"));
			parm.addData("SYSTEM", "COLUMNS", "REG_DATE");
			parm.addData("SYSTEM", "COLUMNS", "ADM_TYPE");
			parm.addData("SYSTEM", "COLUMNS", "MR_NO");
			parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
			parm.addData("SYSTEM", "COLUMNS", "SEX_CODE");
			parm.addData("SYSTEM", "COLUMNS", "SESSION_CODE");
			parm.addData("SYSTEM", "COLUMNS", "SEE_DR_FLG");
			parm.addData("SYSTEM", "COLUMNS", "REALDEPT_CODE");
			parm.addData("SYSTEM", "COLUMNS", "REALDR_CODE");
			date.setData("TABLE", parm.getData());
			date.setData("DATE","TEXT","��ӡʱ��:"+today);
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\REG\\REGMedicalList.jhw", date);
		} else {
			this.messageBox("û�д�ӡ����");
			return;
		}
	}

	/**
	 * �õ�����״̬����
	 */
	public String onGetSeeDrType(String type) {
		String typeName = "";
		if (type.equals("N"))
			typeName = "δ����";
		if ("T".endsWith(type)) {
			typeName = "�ݴ�";
		}
		if ("Y".endsWith(type)) {
			typeName = "�����";
		}
		return typeName;
	}
}
