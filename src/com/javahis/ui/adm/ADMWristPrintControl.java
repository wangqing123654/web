package com.javahis.ui.adm;

import java.sql.Timestamp;

import javax.swing.JOptionPane;

import jdo.adm.ADMTool;
import jdo.reg.Reg;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

public class ADMWristPrintControl extends TControl{
	Pat pat;
	String mrNo;
	String caseNo;
	TParm admParm = new TParm();
	TParm admInfo;
	String age;
	//��ʼ��
	public void onInit() {
		
		super.onInit();
		TParm parm=(TParm) this.getParameter();
		mrNo=parm.getValue("MR_NO");
		caseNo=parm.getValue("CASE_NO");
		pat = Pat.onQueryByMrNo(mrNo);
		admParm.setData("CASE_NO", caseNo);
		admInfo = ADMTool.getInstance().getADM_INFO(admParm);
	    Timestamp sysDate = SystemTool.getInstance().getDate();
	    age = DateUtil.showAge(pat.getBirthday(), sysDate);
	    
	}
	//���������ӡ
	public void onWristAdult(){
		TParm newBornParm=new TParm(TJDODBTool.getInstance().select("select new_born_flg from adm_inp where case_no='"+caseNo+"'"));
	    if("Y".equals(newBornParm.getValue("NEW_BORN_FLG",0))){
	    	if(JOptionPane.showConfirmDialog(null, "�˲��������������Ƿ������", "��Ϣ",
					JOptionPane.YES_NO_OPTION) == 0){
	    		adultContent();
		    }
	    }else{
	    	adultContent();
	    }
	    
	}
	//����������� 20170626 zhanglei������Ҫ��ʾ�����Լ�JHW�ļ���Ҫ��ʾ1�����룻2������OK��3���Ա� ������4���������� ������5������ ������6������ ������7������OK��8������OK��
	public void adultContent(){
		TParm print=new TParm();
		//�õ�����
		print.setData("PatName", "TEXT", "����:"+pat.getName());
		//�õ�������
		print.setData("Barcode1", "TEXT", pat.getMrNo());
		//�õ��Ա�
		print.setData("Sex", "TEXT", "�Ա�:"+pat.getSexString());
		//�õ���������
		print.setData("BirthDay", "TEXT", "��������:"+StringTool.getString(pat.getBirthday(), "yyyy/MM/dd HH:mm"));
		//����
		print.setData("WEIGHT", "TEXT", "����:"+pat.getWeight()+ "KG");
		//�õ�����
    	print.setData("Age", "TEXT", "����:"+age);
    	//�õ�����
    	print.setData("Dept", "TEXT", "����:"+StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='" + admInfo.getValue("DEPT_CODE", 0) + "'"));
    	//�õ�����
    	print.setData("BedNO","TEXT","����:"+admInfo.getValue("BED_NO",0));
    	//this.messageBox_(print);
    	this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMWristAdult.jhw",print,true);
	}
	//��ͯ�����ӡ20170626 zhanglei������Ҫ��ʾ�����Լ�JHW�ļ� 1�����룻2��������3���Ա�4���������ڣ�5�����أ�6�����ţ�7�����䣻8�����ҡ�
	public void onWristChild(){
		TParm print=new TParm();
		//�õ�����
		print.setData("PatName", "TEXT", "����:"+pat.getName());
		//�õ�������
		print.setData("Barcode1", "TEXT", pat.getMrNo());
		//�õ��Ա�
		print.setData("Sex", "TEXT", "�Ա�:"+pat.getSexString());
		//�õ���������
		print.setData("Birth", "TEXT", "��������:"+StringTool.getString(pat.getBirthday(), "yyyy/MM/dd HH:mm"));
		//����
		print.setData("Weight", "TEXT", "����:"+pat.getWeight() + "KG");
		//�õ�����
    	print.setData("Age", "TEXT", "����:"+age);
    	//�õ�����
    	print.setData("Dept", "TEXT", "����:"+StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='" + admInfo.getValue("DEPT_CODE", 0) + "'"));
    	//�õ�����
    	print.setData("BedNO","TEXT","����:"+admInfo.getValue("BED_NO",0));
    	//this.messageBox_(print);
//		print.setData("PatName", "TEXT", "����:"+pat.getName());
//		print.setData("Barcode1", "TEXT", pat.getMrNo());
//    	print.setData("Age", "TEXT", "����:"+age);
//    	print.setData("Dept", "TEXT", "����:"+StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='" + admInfo.getValue("DEPT_CODE", 0) + "'"));
//    	print.setData("BedNO","TEXT","����:");
    	this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMWrist.jhw",print,true);
	}
	//�����������ӡ20170626 zhanglei������Ҫ��ʾ�����Լ�JHW�ļ�1�����룻2��������3���Ա�4���������ڣ�5�����أ�6�����ţ�7�����䣻8�����ҡ�
	public void onWristNewBody(){
		TParm print=new TParm();
		//�õ�����
		print.setData("PatName", "TEXT", "����:"+pat.getName());
		//�õ�������
		print.setData("Barcode", "TEXT", pat.getMrNo());
		//�õ��Ա�
		print.setData("SEX", "TEXT", "�Ա�:"+pat.getSexString());
		//�õ���������
		print.setData("Birth", "TEXT", "��������:"+StringTool.getString(pat.getBirthday(), "yyyy/MM/dd HH:mm"));
		//����������
		print.setData("Weight","TEXT","����:"+pat.getNewBodyWeight()+"g");
		//�õ�����
    	print.setData("Age", "TEXT", "����:"+age);
    	//�õ�����
    	print.setData("Dept", "TEXT", "����:"+StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='" + admInfo.getValue("DEPT_CODE", 0) + "'"));
    	//�õ�����
    	print.setData("BedNO","TEXT","����:"+admInfo.getValue("BED_NO",0));
    	//this.messageBox_(print);
//		print.setData("PatName", "TEXT", "����:"+pat.getName());
//		print.setData("Barcode", "TEXT", pat.getMrNo());
//    	print.setData("Birth", "TEXT", "��������:"+StringTool.getString(pat.getBirthday(), "yyyy/MM/dd HH:mm"));
//    	print.setData("Weight","TEXT","����:"+pat.getNewBodyWeight()+"g");
//    	print.setData("BedNO","TEXT","����:");
    	this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMWristBaby.jhw",print,true);
	}
}
