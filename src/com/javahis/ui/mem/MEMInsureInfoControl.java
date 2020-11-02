package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextArea;
import com.dongyang.util.StringTool;

/**
*
* <p>Title: ���߱�����Ϣά��ҳ��</p>
*
* <p>Description: ���߱�����Ϣά��ҳ��</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author sunqy 20140512
* @version 4.5
*/

public class MEMInsureInfoControl extends TControl{
	
//	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	
	private static TTable table;
	
	private boolean flg = false;
	
	private String acceptEdit = "";
	
	public MEMInsureInfoControl(){
		super();
	}
	
	/**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        table = (TTable) getComponent("TABLE"); 
        //��ʼ������ʱ��
//        timerInit();
        //��ʼ���ؼ�
        ComponentInit();
        //��ǰʱ��
        this.setValue("IN_DATE",StringTool.getTimestamp(new Date()).toString().substring(0, 10).replace('-', '/'));//��ǰʱ��
        //�����ϸ�ҳ�洫����MR_NO
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm acceptData = (TParm) obj;
            String mrNo = acceptData.getData("MR_NO").toString();
            this.setValue("MR_NO", mrNo);
            //System.out.println("------------->"+mrNo);
            onMrno();
            setBirth();
            String edit = acceptData.getData("EDIT").toString();
            if("N".equals(edit)){
            	acceptEdit = edit;
            	callFunction("UI|save|setEnabled", false); //���水ť�û�
//        		callFunction("UI|new|setEnabled", false); //������ť�û�//==liling 20140812 ����
        		callFunction("UI|query|setEnabled", false); //��ѯ��ť�û�
        		callFunction("UI|delete|setEnabled", false); //ɾ����ť�û�
        		callFunction("UI|clear|setEnabled", false); //��հ�ť�û�
//        		callFunction("UI|MR_NO|setEnabled", true); //�����ſ���
        		callFunction("UI|CONTRACTOR_CODE|setEnabled", false); //���չ�˾�û�
        		callFunction("UI|INSURANCE_NUMBER|setEnabled", false); //���տ����û�
        		callFunction("UI|INSURE_PAY_TYPE|setEnabled", false); //����֧�������û�
        		callFunction("UI|START_DATE|setEnabled", false); //���տ�ʼ�����û�
        		callFunction("UI|END_DATE|setEnabled", false); //���ս��������û�
        		callFunction("UI|INSURANCE_CLAUSE_O|setEnabled", false); //�ż��ﱣ��ϸ���û�
        		callFunction("UI|INSURANCE_CLAUSE_I|setEnabled", false); //סԺ����ϸ���û�
        		callFunction("UI|VALID_FLG|setEnabled", false); //��ЧcheckBox�û�
        		callFunction("UI|DEPT_FLG|setEnabled", false); //Ƿ��checkBox�û�
        		callFunction("UI|INSURANCE_BILL_NUMBER|setEnabled", false); //���յ����û�
            }
        }
    }
    
	/**
	 * ��ʼ������ʱ��
	 */
//	private void timerInit() {
//		//������Ч��-����0��
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.DATE, +1);
//		String insureStartDate = sdf.format(calendar.getTime())+ " 00:00:00";
//		this.setValue("START_DATE",insureStartDate);
//		//����ʧЧ�գ�Ĭ�������Ƴ�һ����
//		Calendar calendar1 = Calendar.getInstance();
//		calendar1.add(Calendar.MONTH, +1);
//		String insureEndDate = sdf.format(calendar1.getTime())+ " 23:59:59";
//		this.setValue("END_DATE",insureEndDate);
//	}
	
    /**
     * ��ʼ���ؼ�
     */
	private void ComponentInit() {
		callFunction("UI|save|setEnabled", true); //���水ť�û�
		callFunction("UI|delete|setEnabled", false); //ɾ����ť�û�	
		callFunction("UI|MR_NO|setEnabled", false); //�������û�
		callFunction("UI|PAT_NAME|setEnabled", false); //�����û�
		callFunction("UI|FIRST_NAME|setEnabled", false); //FIRSTNAME�û�
		callFunction("UI|LAST_NAME|setEnabled", false); //LASTNAME�û�
		callFunction("UI|SEX_CODE|setEnabled", false); //�Ա��û�
		callFunction("UI|BIRTH_DATE|setEnabled", false); //���������û�
		callFunction("UI|AGE|setEnabled", false); //�����û�
		callFunction("UI|ID_TYPE|setEnabled", false); //֤�������û�
		callFunction("UI|IDNO|setEnabled", false); //֤�����û�
	}
	
	/**
	 * �����Ų�ѯ
	 */
	public void onQuery(){
		onMrno();
	}
	public void onMrno(){
		if(acceptEdit!="N"){//==liling 20140812 add
		callFunction("UI|save|setEnabled", true); //���水ť����//==liling 20140812 ȡ������
		callFunction("UI|delete|setEnabled", true); //ɾ����ť����
//		callFunction("UI|new|setEnabled", true); //������ť����//==liling 20140812 ����
		}
		//��ѯSYS_PATINFO����
		TParm mrNoParm = new TParm();
		mrNoParm.setData("MR_NO", this.getValueString("MR_NO"));
		TParm sysParm = TIOM_AppServer.executeAction("action.mem.MEMInsureInfoAction",
				"onQuery", mrNoParm);
		//System.out.println("--------------!!!!sysParm=="+sysParm);
		//System.out.println("--------------!!!!mrNoParm=="+mrNoParm);
		if(sysParm.getCount()>0){
			//������set��ҳ������TParm
    		this.setMemPatinfoForUI(sysParm);
    		//��ѯMEM_INSURE_INFO
    		TParm memTradeParm = TIOM_AppServer.executeAction("action.mem.MEMInsureInfoAction",
    				"onQueryMEM", mrNoParm);
    		if(memTradeParm.getCount()>0){
    			for(int i=0;i<memTradeParm.getCount();i++){
    				if("Y".equals(memTradeParm.getValue("VALID_FLG", i)))
    					memTradeParm.setData("VALID_FLG",i,"��");
    				else
    					memTradeParm.setData("VALID_FLG",i,"��");
    			}
    			//������set��ҳ��������TABLE
    			table.setParmValue(memTradeParm);
    		}
		}
		setBirth();
	}
	 /**
     * ������Ա��Ϣ��ֵ
     */
    public void setMemPatinfoForUI(TParm parm) {
    	this.setValue("PAT_NAME", parm.getValue("PAT_NAME", 0));
    	this.setValue("FIRST_NAME", parm.getValue("FIRST_NAME", 0));
    	this.setValue("LAST_NAME", parm.getValue("LAST_NAME", 0));
    	this.setValue("SEX_CODE", parm.getValue("SEX_CODE", 0));
    	this.setValue("ID_TYPE", parm.getValue("ID_TYPE", 0));
    	this.setValue("IDNO", parm.getValue("IDNO", 0));
    	String bDate = parm.getValue("BIRTH_DATE", 0);
    	bDate = bDate.substring(0, 10).replaceAll("-", "/");
    	this.setValue("BIRTH_DATE", bDate);
    }
	/**
	 * ����(����޸Ĺ���)
	 */
	public void onSave(){
		
		if(!checkData()){
			return;
		}
		//==liling 20140812 modify start=====
		/** //============���жϴ˱�����Ϣ�Ƿ�����Ч�ģ��������Ч�ģ���ֱ�ӽ�������ı��棻���������Ч�ģ�ͬ�������һ��
		int i=table.getSelectedRow();
		TParm tableParm=table.getParmValue();
		if(i>=0){//==liling 20140812���˶δ����߼���ԭ�� ���湦�� ��Ϊ�������޸� ������ʱ���޸Ĺ��ܵĲ����߼���
			String insurance_no=tableParm.getValue("INSURANCE_NUMBER", i);
			TParm exitParm=new TParm(TJDODBTool.getInstance().select("SELECT * FROM MEM_INSURE_INFO WHERE INSURANCE_NUMBER='"+insurance_no+"'"));
			if(!"Y".equals(exitParm.getValue("VALID_FLG",0))){
				if(getCheckBox("VALID_FLG").isSelected()){
					if(!isExit()){
						onClear();
						return;
					}
				}
			}
		}
		//============ */
		if(getCheckBox("VALID_FLG").isSelected()){
			if(!isExit()){// isExit()=false �ò����Ѵ�����Ч������Ϣ
				onClear();
				return;
			}
		}//==liling 20140812 modify end=====
		TParm parm = new TParm();
		parm.setData("MR_NO", this.getValueString("MR_NO"));
		parm.setData("CONTRACTOR_CODE", this.getValueString("CONTRACTOR_CODE"));
		parm.setData("INSURANCE_NUMBER", this.getValueString("INSURANCE_NUMBER"));
		
		parm.setData("INSURANCE_BILL_NUMBER",this.getValueString("INSURANCE_BILL_NUMBER"));
		parm.setData("VALID_FLG",this.getValueString("VALID_FLG"));
		parm.setData("DEPT_FLG",this.getValueString("DEPT_FLG"));
		//parm.setData("DETP_ATM",this.getValueString("DEPT_ATM"));
		parm.setData("AMT",this.getValueDouble("AMT"));
		parm.setData("INSURE_PAY_TYPE", this.getValueString("INSURE_PAY_TYPE"));
		String startDate = this.getValueString("START_DATE").toString().replaceAll("-", "/").replaceAll(":", "").substring(0,10);
		parm.setData("START_DATE", startDate);
		String endDate = this.getValueString("END_DATE").toString().replaceAll("-", "/").replaceAll(":", "").substring(0,10);
		parm.setData("END_DATE", endDate);
		parm.setData("INSURANCE_CLAUSE_O", this.getValueString("INSURANCE_CLAUSE_O"));
		parm.setData("INSURANCE_CLAUSE_I", this.getValueString("INSURANCE_CLAUSE_I"));
		parm.setData("MEMO", this.getValueString("MEMO"));//==liling 20140714 add ����Ƿ�ѱ�ע
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		//System.out.println("parm=============================="+parm);
		TParm result = TIOM_AppServer.executeAction("action.mem.MEMInsureInfoAction",
				"update", parm);
		if(result.getErrCode()<0){
			this.messageBox("����ʧ��!");
			return;
		}else{
			this.messageBox("����ɹ�!");
		}
		//==liling modify 20140714 start ����Ƿ�ѱ��ͬ����������Ϣ��===
		 String sql2="UPDATE SYS_PATINFO SET  DEPT_FLG='"+parm.getValue("DEPT_FLG")+"' WHERE MR_NO='"+parm.getValue("MR_NO")+"'";
        TJDODBTool.getInstance().update(sql2);
      //==liling modify 20140714 end===
		onQuery();
		onClear();
	}
	
	/**
	 * ɾ��
	 */
	public void onDelete() {
		TParm result = new TParm();
		TParm parm = table.getParmValue();
		if (parm.getCount() <= 0) {

			this.messageBox("û�б�������");
			return;
		}
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			this.messageBox("��ѡ��Ҫɾ��������");
			return;
		}
		if (this.messageBox("�Ƿ�ɾ��", "ȷ��Ҫɾ����", 2) != 0) {
			return;
		}else{
			// TParm tparm=table.getParmValue().getRow(selectedRow);//@���ﲻ��,��Ӧ����table��MR_NO
			TParm dparm = new TParm();
			dparm.setData("MR_NO", this.getValueString("MR_NO"));
			dparm.setData("CONTRACTOR_CODE", parm.getValue("CONTRACTOR_CODE",selectedRow));
			dparm.setData("INSURANCE_NUMBER", parm.getValue("INSURANCE_NUMBER",selectedRow));
//			System.out.println("dparm=======>"+dparm);
			result = TIOM_AppServer.executeAction(
					"action.mem.MEMInsureInfoAction", "delete", dparm);
		}
		if (result.getErrCode() < 0) {
			this.messageBox(" ɾ��ʧ��");
			return;
		} else {
			this.messageBox("ɾ���ɹ�");
			table.removeRowAll();
			onMrno();
		}
		onClear();
		
	}
	
	/**
	 * ����
	 
	private void onNew(){
		
		if(!checkData()){
			return;
		}
//		onClear();
//		callFunction("UI|delete|setEnabled", false); //ɾ����ť�û�
//		callFunction("UI|query|setEnabled", false); //��ѯ��ť�û�
//		callFunction("UI|save|setEnabled", true); //���水ť����
//		callFunction("UI|MR_NO|setEnabled", false); //�������û�
//		callFunction("UI|IDNO|setEnabled", false); //֤�����û�
		//�жϸò����Ƿ��Ѿ�������Ч�ı�����Ϣ����������򲻿��������Ч�ı�����Ϣ��
		if(getCheckBox("VALID_FLG").isSelected()){
			if(!isExit()){
				onClear();
				return;
			}
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", this.getValueString("MR_NO"));
		parm.setData("CONTRACTOR_CODE", this.getValueString("CONTRACTOR_CODE"));
		parm.setData("INSURANCE_NUMBER", this.getValueString("INSURANCE_NUMBER"));
		
		parm.setData("INSURANCE_BILL_NUMBER",this.getValueString("INSURANCE_BILL_NUMBER"));
		parm.setData("VALID_FLG",this.getValueString("VALID_FLG"));
		parm.setData("DEPT_FLG",this.getValueString("DEPT_FLG"));
		parm.setData("AMT",this.getValueDouble("AMT"));
		parm.setData("INSURE_PAY_TYPE", this.getValueString("INSURE_PAY_TYPE"));
//		System.out.println("startDate=="+this.getValueString("START_DATE"));
		String startDate = this.getValueString("START_DATE").toString().replaceAll("-", "/").replaceAll(":", "").substring(0,10);
		parm.setData("START_DATE", startDate);
		String endDate = this.getValueString("END_DATE").toString().replaceAll("-", "/").replaceAll(":", "").substring(0,10);
		parm.setData("END_DATE", endDate);
		parm.setData("INSURANCE_CLAUSE_O", this.getValueString("INSURANCE_CLAUSE_O"));
		parm.setData("INSURANCE_CLAUSE_I", this.getValueString("INSURANCE_CLAUSE_I"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("MEMO", this.getValueString("MEMO"));//==liling 20140714 add ����Ƿ�ѱ�ע
		
		TParm result = TIOM_AppServer.executeAction("action.mem.MEMInsureInfoAction",
				"onSave", parm);
//		System.out.println("resultLLLLLLLL"+result);
		if(result.getErrCode()<0){
			this.messageBox("����ʧ��");
			onClear();
			this.grabFocus("CONTRACTOR_CODE");
		}else{
			this.messageBox("�����ɹ�");
		}
		//==liling modify 20140714 start ����Ƿ�ѱ��ͬ����������Ϣ��===
		 String sql2="UPDATE SYS_PATINFO SET  DEPT_FLG='"+parm.getValue("DEPT_FLG")+"' WHERE MR_NO='"+parm.getValue("MR_NO")+"'";
         TJDODBTool.getInstance().update(sql2);
       //==liling modify 20140714 end===
		onClear();
		onMrno();
	}
	*/
	
	/**
	 * �жϲ����Ƿ�����Ѿ�������Ч�ı�����Ϣ
	 */
	public boolean isExit(){
		String mr_no=this.getValueString("MR_NO");
		String contractor_code=this.getValueString("CONTRACTOR_CODE");
		String insurance_number=this.getValueString("INSURANCE_NUMBER");
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT VALID_FLG FROM MEM_INSURE_INFO WHERE MR_NO ='"+mr_no+"' " +
						"AND CONTRACTOR_CODE = '"+contractor_code+"' AND INSURANCE_NUMBER = '"+insurance_number+"'"));
		TParm exitParm=new TParm(TJDODBTool.getInstance().select("SELECT VALID_FLG FROM MEM_INSURE_INFO WHERE MR_NO='"+mr_no+"'"));
		if (exitParm.getErrCode() < 0) {
	           messageBox(exitParm.getErrText());
	           return false;
        }
		if(exitParm.getCount()>0){
			if("Y".equals(parm.getValue("VALID_FLG", 0))){
			}else{
				for(int i=0;i<exitParm.getCount();i++){
					if("Y".equals(exitParm.getValue("VALID_FLG",i))){
						messageBox("�û����Ѿ�������Ч������Ϣ");
						getCheckBox("VALID_FLG").setSelected(false);
						return false;
					}else{
						continue;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * ���
	 */
	public void onClear() {
//		callFunction("UI|IDNO|setEnabled", false); //֤�����û�
		callFunction("UI|save|setEnabled", true); //���水ť�û�
		callFunction("UI|delete|setEnabled", false); //ɾ����ť�û�
//		callFunction("UI|MR_NO|setEnabled", true); //�����ſ���
		callFunction("UI|query|setEnabled", true); //��ѯ��ť����
//		callFunction("UI|new|setEnabled", true); //������ť����//==liling 20140812 ����
		callFunction("UI|CONTRACTOR_CODE|setEnabled", true); //���չ�˾����
		callFunction("UI|INSURANCE_NUMBER|setEnabled", true); //���տ��ſ���
		callFunction("UI|DEPT_ATM|setEnabled",true);//Ƿ�ѽ���û�
		this.clearValue("CONTRACTOR_CODE;INSURANCE_NUMBER;INSURE_PAY_TYPE;START_DATE;" +
				"END_DATE;INSURANCE_BILL_NUMBER;VALID_FLG;DEPT_FLG;AMT;MEMO;INSURANCE_CLAUSE_O;INSURANCE_CLAUSE_I;MEMO;START_DATE;END_DATE");
		isSelectDeptFlg();
		this.grabFocus("CONTRACTOR_CODE");
//		table.setSelectedRow(-1);//==liling 20140812 add
		onQuery();
//		table.removeRowAll();
//		timerInit();
	}
	
	/**
	 * ��񵥻��¼�-���ұ���Ŀ��ʾ��Ϣ,���չ�˾�ͱ��պŲ������޸�
	 */
	public void onTableClick(){
		if(acceptEdit!="N"){
		callFunction("UI|save|setEnabled", true); //���水ť����
		callFunction("UI|delete|setEnabled", true);
//		callFunction("UI|new|setEnabled", false); //������ť�û�//==liling 20140812 ����
		}
		if(acceptEdit=="N"){
			callFunction("UI|CONTRACTOR_CODE|setEnabled", false); //���չ�˾�û�
			callFunction("UI|INSURANCE_NUMBER|setEnabled", false); //���տ����û�
		}
		int row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		String mrNo = this.getValueString("MR_NO");
		String contractorCode = tableParm.getValue("CONTRACTOR_CODE", row);
		String insuranceNumber = tableParm.getValue("INSURANCE_NUMBER",row);
		String sql = "SELECT INSURE_PAY_TYPE,START_DATE,END_DATE,INSURANCE_CLAUSE_O,INSURANCE_CLAUSE_I,VALID_FLG,DEPT_FLG,AMT,MEMO,INSURANCE_BILL_NUMBER " +
				"FROM MEM_INSURE_INFO " +
				"WHERE MR_NO='"+mrNo+"' AND CONTRACTOR_CODE='"+contractorCode+"' AND INSURANCE_NUMBER='"+insuranceNumber+"'";
//		System.out.println("-------sql=="+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("-------parm-->"+parm);
		this.setValue("CONTRACTOR_CODE", contractorCode);
		this.setValue("INSURANCE_NUMBER", insuranceNumber);
		this.setValue("INSURE_PAY_TYPE", parm.getValue("INSURE_PAY_TYPE",0));
		String startDate = parm.getValue("START_DATE",0).toString().replaceAll("-", "/").substring(0, 10);
//		System.out.println("--------startDate-:>"+startDate);
		this.setValue("START_DATE", startDate);
		String endDate = parm.getValue("END_DATE",0).toString().replaceAll("-", "/").substring(0, 10);
//		System.out.println("--------endDate-:>"+endDate);
		this.setValue("END_DATE", endDate);
		this.setValue("INSURANCE_CLAUSE_O", parm.getValue("INSURANCE_CLAUSE_O",0));
		this.setValue("INSURANCE_CLAUSE_I", parm.getValue("INSURANCE_CLAUSE_I",0));
		
	   this.setValue("VALID_FLG", parm.getValue("VALID_FLG",0));
	   this.setValue("DEPT_FLG", parm.getValue("DEPT_FLG",0));
	   this.setValue("INSURANCE_BILL_NUMBER", parm.getValue("INSURANCE_BILL_NUMBER",0));
	   this.setValue("AMT", parm.getValue("AMT",0));
	   this.setValue("MEMO", parm.getValue("MEMO",0));//==liling 20140714 add ����Ƿ�ѱ�ע
	   isSelectDeptFlg();
	   
	   
	}

	/**
	 * ѡ��֤���¼�
	 */
	public void onClickIdType(){
		callFunction("UI|IDNO|setEnabled", true); //֤���ſ���
	}
	
	/**
     * ��������
     */
    public void setBirth() {
        if (getValue("BIRTH_DATE") == null || "".equals(getValue("BIRTH_DATE")))
            return;
        String AGE = com.javahis.util.DateUtil.showAge(
                (Timestamp) getValue("BIRTH_DATE"),
                (Timestamp) getValue("IN_DATE"));
        setValue("AGE", AGE);
    }
    /**
     * �س������ת  
     * add by huangjw 20140808
     */
    public void onUnitCode(){
    	if(getCheckBox("DEPT_FLG").isSelected()){
    		getNumberTextField("AMT").grabFocus();
    	}else{
    		getTextArea("INSURANCE_CLAUSE_O").grabFocus();
    	}
    }
    /**
     * ���ҳ��Ԫ��
     */
    public boolean checkData(){
    	//��鲡����
    	if(this.getValueString("MR_NO")==null || "".equals(this.getValueString("MR_NO"))){
    		this.messageBox("�����Ų���Ϊ��!");
    		return flg;
    	}
    	//��鱣�չ�˾
    	if(this.getValueString("CONTRACTOR_CODE")==null || "".equals(this.getValueString("CONTRACTOR_CODE"))){
    		this.messageBox("����д���չ�˾");
    		return flg;
    	}
    	//��鱣�տ���
    	if(this.getValueString("INSURANCE_NUMBER")==null || "".equals(this.getValueString("INSURANCE_NUMBER"))){
    		this.messageBox("����д���տ���");
    		return flg;
    	}
    	//��鱣�տ���λ��
    	if(this.getValueString("INSURANCE_NUMBER").length()>50){
    		this.messageBox("���տ��Ų��ó���50λ");
    		return flg;
    	}
    	//��鱣�տ�ʼ����
    	if(this.getValueString("START_DATE")==null || "".equals(this.getValueString("START_DATE"))){
    		this.messageBox("����д���տ�ʼ����!");
    		return flg;
    	}
    	//��鱣�ս�������
    	if(this.getValueString("END_DATE")==null || "".equals(this.getValueString("END_DATE"))){
    		this.messageBox("����д���ս�������!");
    		return flg;
    	}
    	//�ȽϿ�ʼʱ�������ʱ��Ĵ�С
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    	try {
			Date startdate = sdf.parse(this.getValueString("START_DATE").toString().replaceAll("-", "/").substring(0, 10));
			Date enddate = sdf.parse(this.getValueString("END_DATE").toString().replaceAll("-", "/").substring(0, 10));
			int i = enddate.compareTo(startdate);
//			System.out.println("startdate=="+startdate+"    enddate=="+enddate+"    i=="+i);
			if(i<=0){
				this.messageBox("��ʼ������������ڲ�����Ҫ��!");
				return flg;
			}
		} catch (ParseException e) {
		}
		return true;
    }
    /**
     * ���TCheckBox
     */
    private TCheckBox getCheckBox(String tagName){
    	return (TCheckBox)getComponent(tagName);
    }
    /**
     * ���TNumberTextField  add by huangjw 20140808
     * @param tagName
     * @return
     */
    private TNumberTextField getNumberTextField(String tagName){
    	return (TNumberTextField)getComponent(tagName);
    }
    /**
     * ���TTextArea   add by huangjw 20140808
     * @param tagName
     * @return
     */
    private TTextArea getTextArea(String tagName){
    	return (TTextArea)getComponent(tagName);
    }
    /**
     * Ƿ�ѿؼ��ı��¼�
     */
    public void isSelectDeptFlg(){
    	if(getCheckBox("DEPT_FLG").isSelected()){
    		callFunction("UI|AMT|setEnabled",true);
    		callFunction("UI|MEMO|setEnabled",true);
    	}
    	else{
    		callFunction("UI|AMT|setEnabled",false);
    		callFunction("UI|MEMO|setEnabled",false);
    		this.setValue("AMT", 0.00);
    		this.setValue("MEMO", "");
    	}
    }
    /**
     * ��������
     */
    public void onChange(){
    	TParm parm=new TParm();
    	parm.setData("MR_NO",this.getValue("MR_NO"));
    	parm.setData("PAT_NAME",this.getValue("PAT_NAME"));
    	parm.setData("AGE",this.getValue("AGE"));
    	parm.setData("SEX_CODE",this.getValue("SEX_CODE"));
    	parm=(TParm) this.openDialog("%ROOT%\\config\\mem\\MEMModifyInsureInfo.x",parm);
    }
}
