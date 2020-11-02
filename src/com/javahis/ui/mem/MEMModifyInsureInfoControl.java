package com.javahis.ui.mem;

import java.sql.Timestamp;

import jdo.opb.OPB;
import jdo.reg.Reg;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TRadioButton;
import com.javahis.util.DateUtil;

public class MEMModifyInsureInfoControl extends TControl{
	String mrNo;
	String caseNo;
	String oldInsure;
	String newInsure;
	public void onInit() {
		super.onInit();
		TParm obj = (TParm) this.getParameter();
		mrNo = obj.getValue("MR_NO");
		this.setValue("MR_NO", mrNo);
		this.setValue("PAT_NAME", obj.getValue("PAT_NAME"));
		this.setValue("AGE", obj.getValue("AGE"));
		this.setValue("SEX_CODE", obj.getValue("SEX_CODE"));
		this.setValue("ADM_DATE", TJDODBTool.getInstance().getDBTime().toString().substring(0,10).replaceAll("-", "/"));
	}
	
	/**
	 * 
	 * ��ѯ
	 */
	public void onQuery(){
		Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("���޴˲�����");
			return;
		}
		mrNo = pat.getMrNo();
		this.setValue("MR_NO", pat.getMrNo());
		this.setValue("PAT_NAME", pat.getName());
		this.setValue("SEX_CODE", pat.getSexCode());
		this.setValue("AGE", patAge(pat.getBirthday()));
		if(getTRadioButton("A").isSelected()){//����
			String sql = "SELECT CASE_NO,REALDEPT_CODE,REALDR_CODE,ADM_DATE,INSURE_INFO,OLD_INSURE_INFO FROM REG_PATADM   " +
					" WHERE MR_NO='"+mrNo+"' AND ADM_DATE = TO_DATE('"+this.getValue("ADM_DATE").toString().substring(0,10).replaceAll("-", "/")+"','yyyy/MM/dd') ";
			//System.out.println(""+sql);
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if(parm.getCount() == 1){
				this.setValue("DEPT_CODE", parm.getValue("REALDEPT_CODE",0));
				this.setValue("DR_CODE", parm.getValue("REALDR_CODE",0));
				this.setValue("ADM_DATE", parm.getTimestamp("ADM_DATE",0));
				this.setValue("INSURE_INFO", parm.getValue("INSURE_INFO",0));
				this.setValue("OLD_INSURE_INFO", parm.getValue("OLD_INSURE_INFO",0));
				caseNo = parm.getValue("CASE_NO",0);
				this.oldInsure = parm.getValue("INSURE_INFO",0);
			}
			
			if(parm.getCount()>1 || parm.getCount() <= 0){
				
				this.onRecord();
			}
			
		}else{//סԺ
			
			TParm data = new TParm();
			data.setData("MR_NO",mrNo);
			data.setData("PAT_NAME",this.getValue("PAT_NAME"));
			data.setData("SEX_CODE",this.getValue("SEX_CODE"));
			TParm result = (TParm) this.openDialog("%ROOT%\\config\\adm\\admInsureInfo.x",data);
			
			this.setValue("DEPT_CODE", result.getValue("IN_DEPT_CODE"));
			this.setValue("DR_CODE", result.getValue("VS_DR_CODE"));
			this.setValue("ADM_DATE", result.getTimestamp("IN_DATE"));
			this.setValue("INSURE_INFO", result.getValue("INSURE_INFO"));
			this.setValue("OLD_INSURE_INFO", result.getValue("OLD_INSURE_INFO"));
			caseNo = result.getValue("CASE_NO");
			this.oldInsure = result.getValue("INSURE_INFO");
		}
		this.setValue("CASE_NO", caseNo);
		
	}
	
	/**
	    * ��������
	    * @param date
	    * @return
	    */
	   private String patAge(Timestamp date){
		   Timestamp sysDate = SystemTool.getInstance().getDate();
	       Timestamp temp = date == null ? sysDate : date;
	       String age = "0";
	       age = DateUtil.showAge(temp, sysDate);
	       return age;
	   }
	
	/**
	 * ����
	 */
	public void onSave(){
		
		if(this.getValueString("CASE_NO").equals("")){//У�������Ƿ� Ϊ�գ���Ϊ��ʱ���ܱ���
			this.messageBox("���Ȳ�ѯ");
			return;
		}
		if(!"".equals(this.getValue("INSURE_INFO")) && this.getValue("INSURE_INFO") != null){
			TParm checkParm = new TParm(TJDODBTool.getInstance().select("SELECT VALID_FLG FROM MEM_INSURE_INFO WHERE MR_NO= '"+mrNo+"' AND CONTRACTOR_CODE = '"+this.getValue("INSURE_INFO")+"' "));
			if(checkParm.getCount() <= 0 ){
				this.messageBox("�ò���û�й���˱���");
				return;
			}
			if(!checkParm.getValue("VALID_FLG",0).equals("Y")){
				this.messageBox("�ñ�����Ч��������ѡ��");
				return;
			}
		}
		if(getTRadioButton("A").isSelected()){//����
			TParm update = new TParm(TJDODBTool.getInstance().update("UPDATE  REG_PATADM SET INSURE_INFO = '"+this.getValue("INSURE_INFO")+"'," +
					" OLD_INSURE_INFO='"+this.oldInsure+"' WHERE CASE_NO = '"+caseNo+"'"));
			if(update.getErrCode()<0){
				this.messageBox(update.getErrText());
				return;
			}
		}else{//סԺ
			TParm update = new TParm(TJDODBTool.getInstance().update("UPDATE  ADM_INP SET INSURE_INFO = '"+this.getValue("INSURE_INFO")+"'," +
					" OLD_INSURE_INFO='"+this.oldInsure+"' WHERE CASE_NO = '"+caseNo+"'"));
			if(update.getErrCode()<0){
				this.messageBox(update.getErrText());
				return;
			}
		}
		this.messageBox("�޸ĳɹ�");
		this.setValue("OLD_INSURE_INFO", oldInsure);
	}
	
	/**
	 * �����Ų�ѯ
	 */
	public void onMrno(){
		onQuery();
	}
	
	/**
	 * ���
	 */
	public void onClear(){
		this.clearValue("MR_NO;PAT_NAME;SEX_CODE;AGE;ADM_DATE;DEPT_CODE;INSURE_INFO;DR_CODE;CASE_NO;OLD_INSURE_INFO");
	}
	
	/**
	 * �����¼ѡ��
	 */
	public void onRecord() {
		// ��ʼ��pat
		Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("���޴˲�����!");
			// ���޴˲��������ܲ��ҹҺ���Ϣ
			callFunction("UI|record|setEnabled", false);
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("SEX_CODE", pat.getSexCode());
		parm.setData("AGE", getValue("AGE"));
		// �ж��Ƿ����ϸ�㿪�ľ����ѡ��
		parm.setData("count", "0");
		caseNo = (String) openDialog(
				"%ROOT%\\config\\opb\\OPBChooseVisit.x", parm);
		if (caseNo == null || caseNo.length() == 0 || caseNo.equals("null")) {
			return;
		}
		Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
		if (reg == null) {
			messageBox("�Һ���Ϣ����!");
			return;
		}
		// �������
		callFunction("UI|DEPT_CODE|setValue", reg.getRealdeptCode());
		// ����ҽ��
		callFunction("UI|DR_CODE|setValue", reg.getRealdrCode());
		callFunction("UI|ADM_DATE|setValue", reg.getAdmDate());
		OPB opb = OPB.onQueryByCaseNo(reg);
		
		caseNo = opb.getReg().caseNo();
		checkArrive();//�Ƿ񱨵�У��
		if (opb == null) {
			// this.messageBox_(33333333);
			this.messageBox_("�˲�����δ����!");
			return;
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select("SELECT INSURE_INFO,OLD_INSURE_INFO FROM REG_PATADM WHERE CASE_NO = '"+caseNo+"'"));
		this.setValue("INSURE_INFO", result.getValue("INSURE_INFO",0));
		this.setValue("OLD_INSURE_INFO", result.getValue("OLD_INSURE_INFO",0));
		this.oldInsure = result.getValue("INSURE_INFO",0);
	}
	
	/**
	 * ���鲡���Ƿ񱨵�
	 * yanjing 20131231 
	 */
    private void checkArrive(){
    	//��Ӿ�����Ƿ񱨵���У��   20130814 yanjing
		String sql = "SELECT ARRIVE_FLG,IS_PRE_ORDER FROM REG_PATADM WHERE CASE_NO = '"+caseNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("��ѯsql  is����"+sql);
		if(result.getValue("ARRIVE_FLG",0).equals("N")&&!(result.getValue("IS_PRE_ORDER",0).equals("Y"))){
			this.messageBox("�˲�����δ����!");
			this.onClear();
			return;
		}
    }
    
    public void  selectAction(){
    	if(getTRadioButton("A").isSelected()){
    		getTLabel("tLabel").setText("��������:");
    		getTLabel("tLabel_6").setText("�������:");
    		getTLabel("tLabel_43").setText("����ҽ��:");
    	}else{
    		getTLabel("tLabel").setText("��Ժ����:");
    		getTLabel("tLabel_6").setText("��Ժ����:");
    		getTLabel("tLabel_43").setText("����ҽ��:");
    	}
    }
    
    /**
     * ���TRadioButton����
     * @param tagName
     * @return
     */
    public TRadioButton getTRadioButton(String tagName){
    	return (TRadioButton)(this.getComponent("A"));
    }
    
    /**
     * ���TLabel����
     * @param tagName
     * @return
     */
    public TLabel getTLabel(String tagName){
    	return (TLabel)(this.getComponent(tagName));
    }
    
}
