package com.javahis.ui.opb;

import java.text.DecimalFormat;

import jdo.sys.IReportTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ͨ����ѡ��ӡ�����ײ��վ�
 * </p>
 * 
 * <p>
 * Description: ͨ����ѡ��ӡ�����ײ��վ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author sunqy 20140714
 * @version 1.0
 */

public class OPBPackageChooseControl extends TControl {
	
	TParm packageParm = null;//�ײ�
	TParm printParm = new TParm();
	TParm parm = null;
	double sumTotS = 0.00;// �ϼ�Ӧ�ս��
	double sumTotR = 0.00;// �ϼ�ʵ�ս��
	double sumPackage = 0.00;//�ײͼ۸�
	TCheckBox inside ;//�ײ���
	TCheckBox outside;//�ײ���

	
	public void onInit(){
		super.onInit();
//		System.out.println("========come in=======");
		inside = (TCheckBox)this.getComponent("INCLUDE_FLG");//�ײ���
		outside = (TCheckBox)this.getComponent("OUTSIDE_FLG");//�ײ���
		packageParm = (TParm)this.getParameter();
//		System.out.println("packageParm============="+packageParm);
		parm = (TParm)packageParm.getData("RECEIVE_PARM");
//		System.out.println("parm============="+parm);
		sumTotS = parm.getDouble("SUMTOTS");
		sumTotR = parm.getDouble("SUMTOTR");
		sumPackage = packageParm.getDouble("SUM_PACKAGE");
	}
	
	public void onSelect(){
		boolean engFlag = packageParm.getBoolean("engFlag");
		
		/**
		 * Ӣ�Ĵ�ӡ
		 */
		/*Ӣ�Ĵ�ӡ  add by lich ----------start */
		if(engFlag){
			
			TParm p = new TParm();//�����ÿղ���ѡ�ı��
			TParm q = new TParm();//���ڼ������պϼ�
			DecimalFormat df = new DecimalFormat("##########0.00");
//		System.out.println("packageParm.getData=--=-=-=-=-=-=-=-"+packageParm.getData());
			if(inside.isSelected() && outside.isSelected()){//�ײ����ⶼѡ��
//				if("Y".equals(packageParm.getValue("CLEAR_FLG"))){
//					printParm.setData("TITLE", "TEXT", "�����ײͷ��ý����嵥");
//				}else{
//					printParm.setData("TITLE", "TEXT", "�����ײͷ�����ϸ�嵥");
//				}
				printParm.setData("TABLEPACKAGE", packageParm.getData());//�ײͱ��
				printParm.setData("TABLEORDER", parm.getData());//������ñ��
				printParm.setData("OTHERPAY", "TEXT", "��������");//��������
				q.addData("TOT", "�ϼƣ�");
				q.addData("SUMTOTS", df.format(StringTool.round(sumTotS,2)+sumPackage));// �ϼ�Ӧ�ս��
				q.addData("SUMTOTR", df.format(StringTool.round(sumTotR,2)+sumPackage));// �ϼ�ʵ�ս��
				q.setCount(1);
				q.addData("SYSTEM", "COLUMNS", "TOT");
				q.addData("SYSTEM", "COLUMNS", "SUMTOTS");
				q.addData("SYSTEM", "COLUMNS", "SUMTOTR");
				printParm.setData("TABLESUM", q.getData());// �ϼ�Ӧ�ս��
			}
			if(inside.isSelected() && !outside.isSelected()){//ֻѡ���ײ���
//				if("Y".equals(packageParm.getValue("CLEAR_FLG"))){
//					printParm.setData("TITLE", "TEXT", "�����ײͷ��ý����嵥");
//				}else{
//					printParm.setData("TITLE", "TEXT", "�����ײͷ�����ϸ�嵥");
//				}
				p.setData("Visible", false);
				printParm.setData("TABLEPACKAGE", packageParm.getData());//�ײͱ��
				printParm.setData("TABLEORDER", p.getData());//������ñ��
				printParm.setData("OTHERPAY", "TEXT", "");//��������
				printParm.setData("TABLESUM", p.getData());//�����ܼ�
			}
			if(!inside.isSelected() && outside.isSelected()){//ֻѡ���ײ���
//				if("Y".equals(packageParm.getValue("CLEAR_FLG"))){
//					printParm.setData("TITLE", "TEXT", "������ý����嵥");
//				}else{
//					printParm.setData("TITLE", "TEXT", "���������ϸ�嵥");
//				}
				p.setData("Visible", false);
				printParm.setData("TABLEPACKAGE", p.getData());//�ײͱ��
				printParm.setData("TABLEORDER", parm.getData());//������ñ��
				printParm.setData("OTHERPAY", "TEXT", "");//��������
				q.addData("TOT", "�ϼƣ�");
				q.addData("SUMTOTS", df.format(StringTool.round(sumTotS,2)));// �ϼ�Ӧ�ս��
				q.addData("SUMTOTR", df.format(StringTool.round(sumTotR,2)));// �ϼ�ʵ�ս��
				q.setCount(1);
				q.addData("SYSTEM", "COLUMNS", "TOT");
				q.addData("SYSTEM", "COLUMNS", "SUMTOTS");
				q.addData("SYSTEM", "COLUMNS", "SUMTOTR");
				printParm.setData("TABLESUM", q.getData());// �ϼ�Ӧ�ս��
			}
			if(!inside.isSelected() && !outside.isSelected()){//��û��ѡ��
				this.messageBox("û��ѡ���ײ�ѡ��!");
				return;
			}
			
			
		
			printParm.setData("HOSP","TEXT",packageParm.getData("HOSP"));//ҽԺ����
			printParm.setData("MR_NO", "TEXT", packageParm.getData("MR_NO"));//������
			printParm.setData("PAT_NAME", "TEXT", packageParm.getData("PAT_NAME"));//����
			printParm.setData("InsCom","TEXT", packageParm.getData("InsCom"));//���չ�˾����
			printParm.setData("InsNum","TEXT", packageParm.getData("InsNum"));//���պ�
			printParm.setData("PRINT_NO", "TEXT", packageParm.getData("PRINT_NO"));//�վݺ�
			printParm.setData("DATE", "TEXT", packageParm.getData("BILL_DATE"));//�շ�����
			printParm.setData("NOW", "TEXT", packageParm.getData("DATE"));//��ӡ����
			printParm.setData("OP_NAME", "TEXT", packageParm.getData("OP_NAME"));//������
			printParm.setData("TOT_AMT1","TEXT" ,packageParm.getData("TOT_AMT1"));//�ܽ��
			printParm.setData("Birthday","TEXT" ,packageParm.getData("Birthday"));//����
			printParm.setData("TOT_AMT",df.format(StringTool.round(sumTotS,2)));//�ϼ�Ӧ��
			printParm.setData("OWN_AMT", df.format(StringTool.round(sumTotR,2)));//�ϼ�ʵ��
			printParm.setData("SUM_", df.format(sumPackage));
			//20141223 wangjingchun add start 854
			String patinfo_sql = "SELECT VALID_FLG, DEPT_FLG FROM MEM_INSURE_INFO WHERE MR_NO = '"+packageParm.getData("MR_NO")+"'";
			TParm patinfo_parm = new TParm(TJDODBTool.getInstance().select(patinfo_sql));
			int i = 1;
			if(patinfo_parm.getValue("DEPT_FLG",0).equals("N")&&patinfo_parm.getValue("VALID_FLG",0).equals("Y")){
				i=2;
			}
			for(int j=0;j<i;j++){
				this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListPackEnV45.jhw"),
						IReportTool.getInstance().getReportParm("OPDFeeListV45.class", printParm));//����ϲ�  modify by sunqy 20140630
			}
			//20141223 wangjingchun add end 854
			/*Ӣ�Ĵ�ӡ  add by lich ----------end*/
		
			
		/**
		 * ���Ĵ�ӡ
		 */
		}else{
				
				TParm p = new TParm();//�����ÿղ���ѡ�ı��
				TParm q = new TParm();//���ڼ������պϼ�
				DecimalFormat df = new DecimalFormat("##########0.00");
//			System.out.println("packageParm.getData=--=-=-=-=-=-=-=-"+packageParm.getData());
				if(inside.isSelected() && outside.isSelected()){//�ײ����ⶼѡ��
					if("Y".equals(packageParm.getValue("CLEAR_FLG"))){
						printParm.setData("TITLE", "TEXT", "�����ײͷ��ý����嵥");
					}else{
						printParm.setData("TITLE", "TEXT", "�����ײͷ�����ϸ�嵥");
					}
					printParm.setData("TABLEPACKAGE", packageParm.getData());//�ײͱ��
					printParm.setData("TABLEORDER", parm.getData());//������ñ��
					printParm.setData("OTHERPAY", "TEXT", "��������");//��������
					q.addData("TOT", "�ϼƣ�");
					q.addData("SUMTOTS", df.format(StringTool.round(sumTotS,2)));// �ϼ�Ӧ�ս��
					q.addData("SUMTOTR", df.format(StringTool.round(sumTotR,2)));// �ϼ�ʵ�ս��
					q.setCount(1);
					q.addData("SYSTEM", "COLUMNS", "TOT");
					q.addData("SYSTEM", "COLUMNS", "SUMTOTS");
					q.addData("SYSTEM", "COLUMNS", "SUMTOTR");
					printParm.setData("TABLESUM", q.getData());// �ϼ�Ӧ�ս��
				}
				if(inside.isSelected() && !outside.isSelected()){//ֻѡ���ײ���
					if("Y".equals(packageParm.getValue("CLEAR_FLG"))){
						printParm.setData("TITLE", "TEXT", "�����ײͷ��ý����嵥");
					}else{
						printParm.setData("TITLE", "TEXT", "�����ײͷ�����ϸ�嵥");
					}
					p.setData("Visible", false);
					printParm.setData("TABLEPACKAGE", packageParm.getData());//�ײͱ��
					printParm.setData("TABLEORDER", p.getData());//������ñ��
					printParm.setData("OTHERPAY", "TEXT", "");//��������
					printParm.setData("TABLESUM", p.getData());//�����ܼ�
				}
				if(!inside.isSelected() && outside.isSelected()){//ֻѡ���ײ���
					if("Y".equals(packageParm.getValue("CLEAR_FLG"))){
						printParm.setData("TITLE", "TEXT", "������ý����嵥");
					}else{
						printParm.setData("TITLE", "TEXT", "���������ϸ�嵥");
					}
					p.setData("Visible", false);
					printParm.setData("TABLEPACKAGE", p.getData());//�ײͱ��
					printParm.setData("TABLEORDER", parm.getData());//������ñ��
					printParm.setData("OTHERPAY", "TEXT", "");//��������
					q.addData("TOT", "�ϼƣ�");
					q.addData("SUMTOTS", df.format(StringTool.round(sumTotS,2)));// �ϼ�Ӧ�ս��
					q.addData("SUMTOTR", df.format(StringTool.round(sumTotR,2)));// �ϼ�ʵ�ս��
					q.setCount(1);
					q.addData("SYSTEM", "COLUMNS", "TOT");
					q.addData("SYSTEM", "COLUMNS", "SUMTOTS");
					q.addData("SYSTEM", "COLUMNS", "SUMTOTR");
					printParm.setData("TABLESUM", q.getData());// �ϼ�Ӧ�ս��
				}
				if(!inside.isSelected() && !outside.isSelected()){//��û��ѡ��
					this.messageBox("û��ѡ���ײ�ѡ��!");
					return;
				}
				printParm.setData("DEPT", "TEXT", packageParm.getData("DEPT"));// ����  add by huangtt 20150925
				printParm.setData("MR_NO", "TEXT", packageParm.getData("MR_NO"));//������
				printParm.setData("PAT_NAME", "TEXT", packageParm.getData("PAT_NAME"));//����
				printParm.setData("PRINT_NO", "TEXT", packageParm.getData("PRINT_NO"));//�վݺ�
				printParm.setData("BILL_DATE", "TEXT", packageParm.getData("BILL_DATE"));//�շ�����
				printParm.setData("DATE", "TEXT", packageParm.getData("DATE"));//��ӡ����
				printParm.setData("OP_NAME", "TEXT", packageParm.getData("OP_NAME"));//������
				printParm.setData("SUM_", df.format(sumPackage));
				//20141223 wangjingchun add start 854
				String patinfo_sql = "SELECT VALID_FLG, DEPT_FLG FROM MEM_INSURE_INFO WHERE MR_NO = '"+packageParm.getData("MR_NO")+"'";
				TParm patinfo_parm = new TParm(TJDODBTool.getInstance().select(patinfo_sql));
				int i1 = 1;
				if(patinfo_parm.getValue("DEPT_FLG",0).equals("N")&&patinfo_parm.getValue("VALID_FLG",0).equals("Y")){
					i1=2;
				}
				for(int j=0;j<i1;j++){
					this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListPackV45.jhw"),
							IReportTool.getInstance().getReportParm("OPDFeeListV45.class", printParm));//����ϲ�  modify by sunqy 20140630
				}
				//20141223 wangjingchun add end 854
			}
		}
	
	
}
