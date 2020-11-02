package com.javahis.ui.ins;

import java.util.Map;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.ENumberChoose;
import com.dongyang.ui.TWord;

/**
 * 
 * @author lixiang
 *
 */
public class INSMTRegControl extends TControl {
	/**
	 * 
	 */
	public static final String TN_TEMPLET_PATH="JHW\\סԺ־\\��Ժ��¼";
	/**
	 * 
	 */
	public static final String TN_EMT_FILENAME="�������صǼ�";
	
	public static final String SEPARATED="@";
	
	/**
	 * WORD����
	 */
	private static final String TWORD = "WORD";
	
	private String caseNo;
	private String mrNo;
	/**
	 * WORD����
	 */
	private TWord word;
	public void onInit() {
		super.onInit();
		// ��ʼ��WORD
		initWord();
		// ��ʼ������
		initPage();
	}

	public void initWord() {
		word = this.getTWord(TWORD);
		this.setWord(word);
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getMrNo() {
		return mrNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	/**
	 * �õ�WORD����
	 * 
	 * @param tag
	 *            String
	 * @return TWord
	 */
	public TWord getTWord(String tag) {
		return (TWord) this.getComponent(tag);
	}

	/**
	 * 
	 * @param word
	 *            TWord
	 */
	public void setWord(TWord word) {
		this.word = word;
	}

	public TWord getWord() {
		return this.word;
	}

	/**
	 * ��ʼ������
	 */
	public void initPage() {
        //
		Object obj = this.getParameter();
		if(obj!=null){
			this.setMrNo(((TParm) obj).getValue("MR_NO"));
			this.setCaseNo(((TParm) obj).getValue("CASE_NO"));
			
		}else{
		    //test 
			this.setMrNo("000000032649");
			this.setCaseNo("120320000769");			
		}
		// 1.ͨ�������ȡ REG_PATADM ���� DISEASE_HISTORY�� MED_HISTORY ��ASSISTANT_EXAMINE��ASSSISTANT_STUFF
		//���粻��
		//ȡ���ݣ� ����ṹ���ĵ�;
		
		//�ǿ����ģ��,����
		// �򿪲���
		if (!this.getWord().onOpen(
				TN_TEMPLET_PATH,
				TN_EMT_FILENAME, 2, false)) {

			return;
		}
		//this.messageBox("file name====="+this.getWord().getFileOpenName());
		
		if (this.getWord().getFileOpenName() == null) {
			// û����Ҫ�༭���ļ�
			this.messageBox("E0100");
			return;
		}
		this.getWord().update();
		TParm allParm = new TParm();
		//allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
		//allParm.setData("FILE_TITLE_EN_TEXT", "TEXT", this.hospEngAreaName);
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		//allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
		allParm.addListener("onMouseRightPressed", this, "onMouseRightPressed");
		this.getWord().setWordParameter(allParm);
		
		// ���ò��ɱ༭
		//this.getWord().setCanEdit(false);
		setMicroField();
		this.getWord().setCanEdit(true);
		//this.messageBox("��Ҫ");
		// �༭״̬(������)
		this.getWord().onEditWord();
		//�Ƿ���ڼ�¼
		//����
		TParm parm=getRegedInfo(this.getMrNo(),this.getCaseNo());
		if(parm!=null){
			//����
			if(!parm.getValue("DISEASE_HISTORY", 0).equals("")){
				//��ʷ
				this.setDiseaseHistory(parm.getValue("DISEASE_HISTORY", 0));
				//�������
				this.setExamine(parm.getValue("ASSISTANT_EXAMINE", 0));
				//����ʷ
				this.setMedHistory(parm.getValue("MED_HISTORY", 0));					
				//��������
				this.setStuff(parm.getValue("ASSSISTANT_STUFF", 0));
				
			}
			
		}
			
	}
	/**
	 * 
	 */
	public void setMicroField(){
		TParm allParm = new TParm();
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		//allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		
		Map map = this.getDBTool().select(
				"SELECT * FROM MACRO_PATINFO_VIEW WHERE 1=1 AND MR_NO='"
						+ this.getMrNo() + "'");
		this.getWord().setWordParameter(allParm);
		TParm parm = new TParm(map);
		if (parm.getErrCode() < 0) {
			// ȡ�ò��˻�������ʧ��
			this.messageBox("E0110");
			return;
		}
		
		if (parm.getCount() > 0) {
			for (String parmName : parm.getNames()) {
				parm.addData(parmName, parm.getValue(parmName, 0));
			}

		} else {
			for (String parmName : parm.getNames()) {
				parm.addData(parmName, "");
			}

		}
		//
		String names[] = parm.getNames();
		TParm obj = (TParm) this.getWord().getFileManager().getParameter();
		String strSex="";
		for (String temp : names) {			
			if ("�Ա�".equals(temp)) {
				if (parm.getInt(temp, 0) == 0) {
					strSex="δ˵��";
				} else if(parm.getInt(temp, 0) == 1){
					strSex="��";
					// 1.�� 2.Ů
					//this.getWord().setSexControl(parm.getInt(temp, 0));				
				} else if(parm.getInt(temp, 0) == 2){
					strSex="Ů";
					
				} else if(parm.getInt(temp, 0) == 9){
					strSex="δ˵��";
				}
				
				this.getWord().setMicroField(temp, strSex);
			}else{
				obj.setData(temp, "TEXT", parm.getValue(temp, 0));
				//System.out.println("===temp==="+temp);
				
				this.getWord().setMicroField(temp, parm.getValue(temp,0));
				this.getWord().setWordParameter(obj);
				
			}
			
		}
		
		
	}

	/**
	 * ����
	 */
	public void onSave() {
		//1. ͨ������Ÿ��� REG_PATADM ���е� DISEASE_HISTORY�� MED_HISTORY ��ASSISTANT_EXAMINE��ASSSISTANT_STUFF
	    //this.messageBox("==come in==");
		//��ʷ
		String diseaseHistory=getDiseaseHistory();
		//���
		String examine=getExamine();
		//����ʷ
		String medHis=getMedHistory();
		//����
		String stuff=getStuff();
		String sql="UPDATE REG_PATADM SET DISEASE_HISTORY='"+diseaseHistory+"',";
		       sql+="ASSISTANT_EXAMINE='"+examine+"',";
		       sql+="MED_HISTORY='"+medHis+"',";
		       sql+="ASSSISTANT_STUFF='"+stuff+"'";
		       sql+=" WHERE CASE_NO='"+this.getCaseNo()+"'";
		       sql+=" AND MR_NO='"+this.getMrNo()+"'";
		       
		       
		this.getDBTool().update(sql);
		
		
		
	}

	/**
	 * ���첡ʷ
	 * 
	 * @return
	 */
	private void setDiseaseHistory(String strDH){
		String str[]=getArray(strDH,SEPARATED);
		for(int i=1;i<=4;i++){
			if(str[i-1].equals("0")){
				getCheckboxCom("A"+i+"_1").setChecked(true);
			}else if(str[i-1].equals("1")){
				getCheckboxCom("A"+i+"_2").setChecked(true);
			}			
		}
		//
		getNumCom("A5").setText(str[4]);
		getNumCom("A6").setText(str[5]);
		
		
	}

	/**
	 * ���켴����
	 * 
	 * @return
	 */
	private void setMedHistory(String strMH) {
		String str[]=getArray(strMH,SEPARATED);
		for(int i=1;i<=4;i++){
			if(str[i-1].equals("0")){
				getCheckboxCom("C"+i+"_1").setChecked(true);
			}else if(str[i-1].equals("1")){
				getCheckboxCom("C"+i+"_2").setChecked(true);
			}				
		}
		//
		this.setCaptureValue("C5",str[4]);
		
	}

	/**
	 * �������
	 * 
	 * @return
	 */
	private void setExamine(String strExa) {
		String str[]=getArray(strExa,SEPARATED);
		//
		for(int i=1;i<=11;i++){
			getNumCom("B"+i).setText(str[i-1]);			
		}
	
	}

	/**
	 * ��������
	 * 
	 * @return
	 */
	private void setStuff(String strSuff) {
		String str[]=getArray(strSuff,SEPARATED);
		for(int i=1;i<=3;i++){
			if(str[i-1].equals("0")){
				getCheckboxCom("D"+i+"_1").setChecked(true);
			}else if(str[i-1].equals("1")){
				getCheckboxCom("D"+i+"_2").setChecked(true);
			}				
		}

	}
	
	/**
	 * 
	 * @param str
	 * @param suff
	 * @return
	 */
	private String[] getArray(String str,String suff){
		String arr[]=str.split(suff);
		
		return arr;
		
	}

	/**
	 * ������
	 * 
	 * @return
	 */
	private String  getMedHistory() {
		
		String result=getString(4 ,2,"C");
		result+="@";
		result+=getCaptureValue("C5");
		
		return result;

	}
	
	

	/**
	 * �������
	 * 
	 * @return
	 */
	private String getExamine() {		
		String result="";
		for(int i=1;i<=11;i++){
			result+=getNum("B"+i)+"@";			
		}
		//
		result=result.substring(0, result.length()-1);
			
		return result;
	}

	/**
	 * ��������
	 * 
	 * @return
	 */
	private String getStuff() {
		String result=getString(3,2,"D");
		return result;
	}
	
	/**
	 * ��ȡ��ʷ
	 * @return
	 */
	private String getDiseaseHistory(){		
		String  result="";
		result+=getString(4 ,2,"A");
		result+="@";
		result+=getNum("A5");
		result+="@";
		result+=getNum("A6");
		return result;
	}
	
	/**
	 * ��ȡҽ���豣����ַ���
	 * @param row
	 * @param col
	 * @param tableName
	 * @return
	 */
	private String getString(int row ,int col,String tableName){
		String  result="";
		//���һ�£�һ���ֻ��ѡһ��
		//��
		boolean flg=false;
		for(int i=1;i<=row;i++){
			boolean group1=false;
			boolean group2=false;
			//��
			for(int j=1;j<=col;j++){
				flg=getCheckBox(tableName+i+"_"+j);
				//��һ��ֵΪ0
				if(j==1){
				  if(flg){
					  group1=true;
					  result+="0"; 
				  }
					
				//�ڶ���ֵΪ1
				}else if(j==2){
					if(flg){
						group2=true;
						result+="1";   
					}
				}
			}
		    //ͬ�鶼Ϊtrue��"ͬһ��ֻ��ѡ��һ��"
			if(group1&&group2){
				this.messageBox("ͬһ��ֻ��ѡ��һ��");
				break;
			}else{
				result+="@";
			}
		}
		result=result.substring(0, result.length()-1);
		//System.out.println("===str==="+result);
		return result;
		
	}
	
	
	/**
	 *  ͨ��checkboxȡ
	 */
	private boolean getCheckBox(String comName){
		/**EComponent com = this.getWord().getPageManager().findObject(
				comName, EComponent.CHECK_BOX_CHOOSE_TYPE);	**/
		
		ECheckBoxChoose checkbox = getCheckboxCom(comName);
	
		return checkbox.isChecked();
		
	}
	
	private ECheckBoxChoose getCheckboxCom(String comName){
		EComponent com = this.getWord().getPageManager().findObject(
				comName, EComponent.CHECK_BOX_CHOOSE_TYPE);	
		ECheckBoxChoose checkbox = (ECheckBoxChoose) com;
		return checkbox;
	}
	
	/**
	 * �������ֿؼ�
	 * @param comName
	 * @return
	 */
	private String getNum(String comName){
		
		EComponent com = this.getWord().getPageManager().findObject(
				comName, EComponent.NUMBER_CHOOSE_TYPE);	
		ENumberChoose numberChoose = (ENumberChoose) com;
		
		return numberChoose.getText();
		
	}
	
	private ENumberChoose getNumCom(String comName){
		
		EComponent com = this.getWord().getPageManager().findObject(
				comName, EComponent.NUMBER_CHOOSE_TYPE);	
		ENumberChoose numberChoose = (ENumberChoose) com;
		
		return numberChoose;
	}
	
	/**
	 * ��ȡץȡֵ
	 * 
	 * @param name
	 *            String
	 * @return String
	 */
	public String getCaptureValue(String name) {
		return this.getWord().getCaptureValue(name);
	}
	/**
	 * ����ֵ
	 * @param name
	 * @param value
	 */
	public void  setCaptureValue(String name,String value){
		ECapture ecap = this.getWord().findCapture(name);
		if (ecap == null) {
			return;
		}
		ecap.setFocusLast();
		ecap.clear();
		this.getWord().pasteString(value);
	}
	/**
	 * �Ƿ��ѵǼ�
	 * @param mrNo   ������
	 * @param caseNo
	 * @return
	 */
	private TParm getRegedInfo(String mrNo,String caseNo){
		String sql="SELECT DISEASE_HISTORY,MED_HISTORY,ASSISTANT_EXAMINE,ASSSISTANT_STUFF FROM REG_PATADM";
		       sql+=" WHERE MR_NO='"+mrNo+"'";
		       sql+=" AND CASE_NO='"+caseNo+"'";
		TParm parm=new TParm(this.getDBTool().select(sql));
		if (parm.getErrCode() < 0) {
			this.messageBox("δȡ��������Ϣ��");
			return null;
		}
		
		return parm;

	}
	
	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
	
	/**
	 * ��ӡ
	 */
	public void onPrintXDDialog() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onPreviewWord();
			this.getWord().printXDDialog();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

}
