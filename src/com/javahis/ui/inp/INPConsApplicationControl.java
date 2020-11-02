package com.javahis.ui.inp;
import java.sql.Timestamp;
import java.util.Date;

import jdo.inp.ConsApplicationTool;
import jdo.sys.MailUtil;
import jdo.sys.MailVO;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.XmlUtil;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
/**
*
* <p>Title: �������뱨��</p>
*
* <p>Description: �������뱨��</p>
*
* <p>Copyright: Copyright (c) caoyong 20139023</p>
*
* <p>Company: JavaHis</p>
*
* @author caoyong
* @version 1.0
*/
public class INPConsApplicationControl extends TControl {
	/**
	 * �������
	 */
   
	private String caseNo;
	private TTable table;
	/**
	 * �ʼ�TParm
	 */
	private TParm tmessage;
	
	/**
	 * ������
	 */
	private String mrno;
	/**
	 * ��������
	 */
	private String patName;
	/**
	 * ����ֵ��ҽ������;
	 */
	private String accdeptcode;
	/**
	 * �����
	 */
	private String caseno;
	/**
	 * סԺ����
	 */
	private Timestamp admDate;
	/**
	 * סԺ��
	 */
	private String ipdNo;
	/**
	 * �ż�������
	 */
	private  String AdmType;
	/**
	 * �����ַ
	 * 
	 */
	/**
	 * �����
	 */
	private String consCode;
	
	/**
	 * ��������
	 */
	private String kindCode;
	
	/**
	 * ��������
	 */
	public static final String STRMESS="��ѡ��һ�������嵥����";
	
	private  String  SQL="SELECT A.USER_NAME,A.ROLE_ID,A.USER_ID,A.E_MAIL,B.EXAMINE_CODE,B.EXAMINE_DATE,C.URG_FLG "+
                         "FROM JAVAHIS.SYS_OPERATOR A,JAVAHIS.MRO_CHRTVETREC B ,JAVAHIS.MRO_CHRTVETSTD C "+
                         "WHERE   A.USER_ID= B.VS_CODE (+) "+
                         "AND B.EXAMINE_CODE= C.EXAMINE_CODE (+)  AND  ";
	
	public String getIpdNo() {
		return ipdNo;
	}
	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}
	
	
	public String getAccdeptcode() {
		return accdeptcode;
	}
	public void setAccdeptcode(String accdeptcode) {
		this.accdeptcode = accdeptcode;
	}
	public String getAssdeptcode() {
		return assdeptcode;
	}
	public void setAssdeptcode(String assdeptcode) {
		this.assdeptcode = assdeptcode;
	}
	private String assdeptcode;//����ָ��ҽ������
	public String getPatName() {
		return patName;
	}
	public void setPatName(String patName) {
		this.patName = patName;
	}
	public void setMrno(String mrno){
		this.mrno=mrno;
	}
	public String getMrno(){
		return this.mrno;
	}
	
	
	public Timestamp getAdmDate() {
		return admDate;
	}
	public void setAdmDate(Timestamp admDate) {
		this.admDate = admDate;
	}
	
	public String getCaseno() {
		return caseno;
	}
	public void setCaseno(String caseno) {
		this.caseno = caseno;
	}
	public TParm getTmessage() {
		return tmessage;
	}
	public void setTmessage(TParm tmessage) {
		this.tmessage = tmessage;
	}
	
	public String getKindCode() {
		return kindCode;
	}
	public void setKindCode(String kindCode) {
		this.kindCode = kindCode;
	}
	public void setConsCode(String consCode) {
		this.consCode = consCode;
	}
	public String getConsCode(){
		return this.consCode;
	}
	public String getAdmType() {
		return AdmType;
	}
	public void setAdmType(String admType) {
		AdmType = admType;
	}
	/**
	 * �õ�TABLE����
	 * @param tagName
	 * @return
	 */
	 private TTable getTable(String tagName) {
			return (TTable) getComponent(tagName);
		}
	
	  /**
     * ��ʼ��
     */
    public void onInit(){
    	 callFunction("UI|Tble|addEventListener","Tble->"+TTableEvent.CLICKED,this,"onTABLEClicked");//�����¼�
    	//���ܴ���Ĳ���
    	TParm outsideParm = (TParm) this.getParameter();
		String mrno=outsideParm.getValue("INW","MR_NO");//������
		String admDate=outsideParm.getValue("INW","ADM_DATE");//סԺ����
		String patname=outsideParm.getValue("INW","PAT_NAME");//��������
		String caseno=outsideParm.getValue("INW","CASE_NO");//�����
		String ipdno=outsideParm.getValue("INW","IPD_NO");//סԺ��
		String admType=outsideParm.getValue("INW","ADM_TYPE");//�ż���סԺ
		String odiFlg=outsideParm.getValue("INW","ODI_FLG");//סԺҽ��վ����رհ�ť
		String kindCode=outsideParm.getValue("INW","KIND_CODE");
		if(odiFlg!=null&&odiFlg.length()>0){
			callFunction("UI|close|setEnabled",false);
		}
		//���Ӳ�����ֵ
        Timestamp ipdDate = StringTool.getTimestamp(admDate,"yyyyMMdd");
		this.setMrno(mrno);//������
		this.setCaseno(caseno);//�����
		this.setAdmDate(ipdDate);//סԺ����
		this.setIpdNo(ipdno);//סԺ��
		this.setAdmType(admType);
         //��ʼ�����ﵥ��
 		this.setValue("CONS_CODE", this.getTConsCode());//���ﵥ��	
	    this.setValue("KIND_CODE", kindCode);//��ʼ����������
 		//���͹��滼������
 		this.setPatName(patname);//��������
    	onQueryall() ;
    	this.initPage();
        
    }
    /**
     * �Զ���ȡ���ﵥ��
     */
    public synchronized String getTConsCode(){
    	String conscode="";
    	conscode = SystemTool.getInstance().getNo("ALL", "INP","CONS_NO", "CONS_NO");//�Զ���ȡ���ﵥ��
    	return conscode;
    }
    /**
     * ��ѯһ�����ߵ����л��������¼
     */
    public void onQueryall(){
    	TParm result=new TParm();
		TParm parm= new TParm();
		TParm outsideParm = (TParm) this.getParameter();//���ܴ���Ĳ���
		this.caseNo=outsideParm.getValue("INW","CASE_NO");
		parm.setData("CASE_NO",this.caseNo);
		result=ConsApplicationTool.getInstance().selectdataall(parm);
		
		 table=this.getTable("Tble");
		
	     table.setParmValue(result);
    	
    }
    /**
     * ��ʼ��
     */
    private void initPage() {	
			Timestamp date = StringTool.getTimestamp(new Date());
		    this.setValue("CONS_DATE",date.toString().substring(0,10).replace('-', '/'));//��ʼ������ʱ��
		    this.setValue("DR_CODE",Operator.getID());//��ʼ������ҽ��
		    this.setValue("DR_TEL", this.getTel(Operator.getID()));
		    this.setValue("DR_DEPT", Operator.getDept());//��ʼ����������
		    this.setValue("ADM_TYPE", this.getAdmType());
			    
    }
    /**
     * ����
     */
    public void onSave(){
    	
    	TParm parm=new TParm();
    	TParm result= new TParm();
    	TParm tparm=new TParm();
    	if("".equals(this.getValueString("CONS_CODE"))){
    		this.messageBox("���ﵥ�Ų���Ϊ��");
    		this.grabFocus("CONS_CODE");
    		return;
    	}
    	/*if("".equals(this.getValueString("ACCEPT_DEPT_CODE"))){
    		this.messageBox("������Ҳ���Ϊ��");
    		this.grabFocus("ACCEPT_DEPT_CODE");
    		return;
    	}*/
    	/*if("".equals(this.getValueString("ASSEPT_DEPT_CODE"))){
    		this.messageBox("ָ��������Ҳ���Ϊ��");
    		this.grabFocus("ASSEPT_DEPT_CODE");
    		return;
    	}
    	if("".equals(this.getValueString("ASSIGN_DR_CODE"))){
    		this.messageBox("ָ������ҽ������Ϊ��");
    		this.grabFocus("ASSIGN_DR_CODE");
    		return;
    	}*/
    	TParm outsideParm = (TParm) this.getParameter();//���ܴ���Ĳ���
		this.caseNo=outsideParm.getValue("INW","CASE_NO");
    	parm.setData("URGENT_FLG",this.getValueString("URGENT_FLG"));//��
    	parm.setData("ADM_TYPE",this.getValueString("ADM_TYPE"));// �ż�ס��
    	parm.setData("ASSEPT_DEPT_CODE",this.getValueString("ASSEPT_DEPT_CODE"));//ָ��������ң�
    	parm.setData("CASE_NO",this.caseNo);//��Ϻ�
    	//parm.setData("SEX_CODE",this.getValueString("SEX_CODE"));//�Ա�
    	//parm.setData("AGE",this.getValueString("AGE"));//����
    	//parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));//�Ʊ�
    	//parm.setData("PAT_AREA",this.getValueString("PAT_AREA"));//����
    	//parm.setData("BED_NO",this.getValueString("BED_NO"));//����
    	//parm.setData("VC_CODE",this.getValueString("VC_CODE"));//����ҽ��
    	//parm.setData("IN_DATE",this.getValueString("IN_DATE"));//��Ժʱ��
    	
    	parm.setData("KIND_CODE",this.getValueString("KIND_CODE"));//��������
    	parm.setData("CONS_CODE",this.getValueString("CONS_CODE"));//���ﵥ��
    	parm.setData("CONS_DATE",this.getValue("CONS_DATE"));//����ʱ��
    	parm.setData("DR_CODE",this.getValueString("DR_CODE"));//����ҽ��
    	parm.setData("DR_TEL",this.getValueString("DR_TEL"));//����ҽ���绰
    	parm.setData("ACCEPT_DEPT_CODE",this.getValueString("ACCEPT_DEPT_CODE"));//���ҽ��
    	parm.setData("ACCEPT_DR_CODE",this.getValueString("ACCEPT_DR_CODE"));//���ֵ��ҽ��
    	parm.setData("ACCEPT_DR_TEL",this.getValueString("ACCEPT_DR_TEL"));//���ҽ���绰
    	parm.setData("ASSIGN_DR_CODE",this.getValueString("ASSIGN_DR_CODE"));//��ָ��ҽ��
    	parm.setData("ASSIGN_DR_TEL",this.getValueString("ASSIGN_DR_TEL"));//��ָ��ҽ���绰
    	
    	parm.setData("REAL_DR_CODE",this.getValueString("ASSIGN_DR_TEL"));//ʵ�ʱ���ҽ����yanjing 20131115
    	parm.setData("DR_DEPT",Operator.getDept());//��������
    	parm.setData("REGION_CODE",Operator.getRegion());//����
    	parm.setData("OPT_TERM", Operator.getIP());
    	parm.setData("OPT_USER", Operator.getID());
    	
    	tparm.setData("CONS_CODE",this.getValueString("CONS_CODE"));
    	String sql="SELECT CONS_CODE FROM INP_CONS WHERE CONS_CODE='"+this.getValueString("CONS_CODE")+"'";
    	TParm sparm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(sparm.getCount()>0){
    		result=ConsApplicationTool.getInstance().updatedata(parm);
    		if(result.getErrCode()<0){
          		 this.messageBox("�޸�ʧ��");
          		 return ;
          	     }
          		 this.messageBox("�޸ĳɹ�");
          		 //this.onQueryall();
          		// this.getConsCode();//���»��ﵥ��
          		 this.onClear();
          		
    	}else{
        	result= ConsApplicationTool.getInstance().insertdata(parm);
        	if(result.getErrCode()<0){
         		 this.messageBox("���ʧ��");
         		 return ;
         	     }
         		 this.messageBox("��ӳɹ�");
    		    // this.onQueryall();
    		    // this.getConsCode();//���»��ﵥ��
         		
    		  if(!"".equals(this.getValueString("ACCEPT_DEPT_CODE"))){
    		    	 onBoardMessage(this.getValueString("ACCEPT_DEPT_CODE"),"0") ;//���͹���
    		    }
    		  if(!"".equals(this.getValueString("ASSIGN_DR_CODE"))){
    			  onBoardMessage(this.getValueString("ASSIGN_DR_CODE"),"1") ;//���͹���
    			  
    		  }   
    		     
    		     this.onClear();
    		     
    	}
    	
    }
    /**
     * ����ָ��ҽ����Ϊ��ʱ����ձ���ֵ����Һ�ֵ��ҽ��
     */
    public void setNull(){
    	if(this.getValueString("ASSIGN_DR_CODE").length()>0){
    		this.setValue("ACCEPT_DEPT_CODE", "");//����ֵ��ҽ��
    		this.setValue("ACCEPT_DR_CODE", "");//����ֵ��ҽ��
    		this.setValue("ACCEPT_DR_TEL", "");
    		callFunction("UI|ACCEPT_DEPT_CODE|setEnabled", false);//������ֵ��ҽ��
    		callFunction("UI|ACCEPT_DR_CODE|setEnabled", false);//������ֵ��ҽ��
    	}else{
    		callFunction("UI|ACCEPT_DEPT_CODE|setEnabled", true);//������ֵ��ҽ��
    		callFunction("UI|ACCEPT_DR_CODE|setEnabled", true);//������ֵ��ҽ��
    		this.setValue("ASSIGN_DR_TEL", "");
    	}
    }
    /**
	 * *���Ӷ�Table�ļ���
	 */
	public void onTABLEClicked(int row){
	 TParm tparm = table.getParmValue().getRow(row);
	 
	 //�������ֵ��ҽ����ֵ����������ָ�����Һ�ҽ��
	  if(tparm.getValue("ACCEPT_DEPT_CODE").length()>0){
		
		  callFunction("UI|ASSEPT_DEPT_CODE|setEnabled", true);//������ָ������
  		  callFunction("UI|ASSIGN_DR_CODE|setEnabled", true);//������ָ��ҽ��
  		  callFunction("UI|ACCEPT_DEPT_CODE|setEnabled", true);//
 		  callFunction("UI|ACCEPT_DR_CODE|setEnabled", true);//
	  }
	  //����ָ��ҽ��ҽ����ֵ��������ֵ����Һ�ҽ��
	  if(tparm.getValue("ASSIGN_DR_CODE").length()>0){
		  
		  callFunction("UI|ASSEPT_DEPT_CODE|setEnabled", true);//
 		  callFunction("UI|ASSIGN_DR_CODE|setEnabled", true);//
		  callFunction("UI|ACCEPT_DEPT_CODE|setEnabled", false);//������ֵ��ҽ��
  		  callFunction("UI|ACCEPT_DR_CODE|setEnabled", false);//������ֵ��ҽ��
  		 
	  }
			  this.setValue("KIND_CODE", tparm.getValue("KIND_CODE"));
			  this.setValue("CONS_CODE",  tparm.getValue("CONS_CODE"));
			  this.setValue("CONS_DATE", tparm.getValue("CONS_DATE").substring(0,10).replace("-","/"));
			  this.setValue("DR_DEPT",  tparm.getValue("DR_DEPT"));
			  this.setValue("ACCEPT_DR_CODE",   tparm.getValue("ACCEPT_DR_CODE")); 
			  this.setValue("ACCEPT_DEPT_CODE",  tparm.getValue("ACCEPT_DEPT_CODE"));
			  
			  this.setValue("ASSEPT_DEPT_CODE", tparm.getValue("ASSEPT_DEPT_CODE"));
			  this.setValue("ASSIGN_DR_CODE",   tparm.getValue("ASSIGN_DR_CODE"));
			  this.setValue("URGENT_FLG", tparm.getValue("URGENT_FLG"));
			 
			  this.setValue("DR_CODE", tparm.getValue("DR_CODE"));
			  this.setValue("DR_TEL", tparm.getValue("DR_TEL"));
			  this.setValue("ACCEPT_DR_TEL", tparm.getValue("ACCEPT_DR_TEL"));
			  this.setValue("ASSIGN_DR_TEL", tparm.getValue("ASSIGN_DR_TEL"));
			  
			  //this.setAssdeptcode(tparm.getValue("ASSEPT_DEPT_CODE"));//���͹��汻ָ������
			  //
			  this.setConsCode(tparm.getValue("CONS_CODE"));
			  this.setKindCode(tparm.getValue("KIND_CODE"));
			  //�����ʼ���ֵ
			  TParm mparm=new TParm();
			  if(tparm.getValue("ACCEPT_DEPT_CODE").length()>0){
				  mparm.setData("ACCEPT_DEPT_CODE",tparm.getValue("ACCEPT_DEPT_CODE"));
			  }
			  if(tparm.getValue("ASSIGN_DR_CODE").length()>0){
				  mparm.setData("ASSIGN_DR_CODE",tparm.getValue("ASSIGN_DR_CODE"));
				  mparm.setData("ASSIGN_DR_TEL",tparm.getValue("ASSIGN_DR_TEL"));
			  }
			  mparm.setData("OPT_USER",Operator.getID());
			  mparm.setData("OPT_TERM",Operator.getIP());
			  this.setTmessage(mparm);
	}
	/**
	 * ��ѯ�����Ƿ����
	 * @param tparm
	 * @return
	 */
	
	public TParm getCons(){
		   TParm xparm=new TParm();
	  		xparm.setData("CONS_CODE",this.getConsCode());
	  		xparm.setData("KIND_CODE",this.getKindCode());
	  		xparm.setData("CASE_NO",this.getCaseno());
	  		
	  	   TParm xresult=ConsApplicationTool.getInstance().selectdCons(xparm);
	  	   return xresult;
	}
   
    /**
     * �绰���¼�
     */
    public void getDtel(){
    	if(!"".equals(getValueString("DR_CODE"))){//ȡ�ÿ���ҽ���绰
            this.setValue("DR_TEL", this.getTel(getValueString("DR_CODE")));
    	}
    	if(!"".equals(getValueString("ACCEPT_DR_CODE"))){//ȡ�ñ���ֵ��ҽ���绰
    		this.setValue("ACCEPT_DR_TEL", this.getTel(getValueString("ACCEPT_DR_CODE")));
    	}
    	if(!"".equals(getValueString("ASSIGN_DR_CODE"))){//ȡ��ָ������ҽ���绰
    		this.setValue("ASSIGN_DR_TEL", this.getTel(getValueString("ASSIGN_DR_CODE")));
    	}
    		
    }
    /**
     * ��ѯҽ������Ӧ�ĵ绰����
     */
    public String getTel(String value){
    	
    	if(!"".equals(value)){
    		String sqlt="SELECT TEL1 FROM SYS_OPERATOR WHERE USER_ID='"+value+"'";
        	TParm parmt = new TParm(TJDODBTool.getInstance().select(sqlt));
        	return parmt.getValue("TEL1", 0);
    	}else{
    		return "";
    	}
    	
    	
    }
    /**
	 * ���
	 */
	public void onClear() {
		String clearString= "KIND_CODE;CONS_DATE;DR_CODE;DR_TEL;ACCEPT_DEPT_CODE;ASSEPT_DEPT_CODE; " +
				            "ACCEPT_DR_TEL;ASSIGN_DR_CODE;ASSIGN_DR_TEL;ACCEPT_DR_CODE;REPORT_DEPT_CODE;URGENT_FLG";
		clearValue(clearString);
		  callFunction("UI|ASSEPT_DEPT_CODE|setEnabled", true);
		  callFunction("UI|ASSIGN_DR_CODE|setEnabled", true);
		  callFunction("UI|ACCEPT_DEPT_CODE|setEnabled", true);
 		  callFunction("UI|ACCEPT_DR_CODE|setEnabled", true);

		this.onInit();
		/*this.initPage();
		table.removeRowAll();*/
		
	}
    /**
     * ��ѯ����
     */
	public void onQuery() {
		/*TParm result=new TParm();
		TParm parm= new TParm();
		//TParm outsideParm = (TParm) this.getParameter();//���ܴ���Ĳ���
		//this.caseNo=outsideParm.getValue("INW","CASE_NO");
		if("".equals(this.getValueString("CONS_CODE"))){
			this.messageBox("�������ѯ�Ļ��ﵥ��");
			this.grabFocus("CONS_CODE");
			return;
		}
		parm.setData("CONS_CODE",this.getValueString("CONS_CODE"));
		result=ConsApplicationTool.getInstance().selectdata(parm);
		if(result.getErrCode()<0){
			this.messageBox("��ѯ��������");
			return;
		}
		if(result.getCount()<=0){
			this.messageBox("û�в�ѯ����");
			this.onClear();
			return;
		}
		 table=this.getTable("Tble");
	     table.setParmValue(result);*/
		 this.openDialog("%ROOT%\\config\\inp\\INPSchedlu.x");
		 
	}
	 /**
     *ȡ������
     */
    public void onCancel() {
    	int row =table.getSelectedRow();
    	if(row<0){
    		this.messageBox("��ѡ��һ��ȡ����������Ϣ");
    		return;
    	}
    	TParm result=this.getCons();
    	if("Y".equals(result.getValue("REPORT_FLG",0))){
    		this.messageBox("��������ɣ�������ȡ��");
    		return;
    	}
    	TParm rparm= table.getParmValue().getRow(row);
    	TParm parm = new TParm();
    	String sqlq="SELECT A.MR_NO,A.IPD_NO,A.DEPT_CODE,A.BED_NO," +
				    "A.IN_DATE,A.STATION_CODE,A.VS_DR_CODE,B.PAT_NAME, " +
				    "B.BIRTH_DATE,B.SEX_CODE  "+
				    "FROM " +
				    "ADM_INP A,SYS_PATINFO B "+
				    "WHERE "+
				    "A.MR_NO=B.MR_NO(+) AND " +
				    "A.CASE_NO='"+this.caseNo+"'";
    	
	TParm parmq = new TParm(TJDODBTool.getInstance().select(sqlq));
			parm.setData("MR_NO", parmq.getValue("MR_NO",0));//������
			parm.setData("IPD_NO", parmq.getValue("IPD_NO",0));//סԺ��
			parm.setData("PAT_NAME", parmq.getValue("PAT_NAME",0));//����
			parm.setData("SEX_CODE", "1".equals(parmq.getValue("SEX_CODE",0)) ? "��" :"Ů");//�Ա�
			parm.setData("DEPT_CODE", parmq.getValue("DEPT_CODE",0));//�Ʊ�
			parm.setData("VS_DR_CODE", parmq.getValue("VS_DR_CODE",0));//����ҽ��
			parm.setData("STATION_CODE", parmq.getValue("STATION_CODE",0));//����
			//parm.setData("BED_NO", parmq.getValue("BED_NO",0));//����
			parm.setData("IN_DATE", parmq.getValue("IN_DATE",0).substring(0, 10).replace("-", "/"));//סԺ����
			parm.setData("CONS_CODE",rparm.getValue("CONS_CODE"));
			parm.setData("CANCEL_DR_CODE",Operator.getID());
			
       /* String value = getValueString("RECP_TYPE");
        if (value.length() == 0) {
            this.messageBox("E0012");
            return;
        }*/
        this.openDialog("%ROOT%\\config\\inp\\INPConsCancel.x",parm);
       // onClear();
        //this.onQuery();
    }
	/**
	 * ɾ������
	 */
	
	public void onDelete(){
		 TParm result =new TParm();
		
		 TParm parm=table.getParmValue();
		 if(parm.getCount()<=0){
			 
			this.messageBox("û�б�������");
			return;
		 }
		int row=table.getSelectedRow();
		if(row<0){
			this.messageBox("��ѡ��Ҫɾ��������");
			return ;
		}
		if (this.messageBox("�Ƿ�ɾ��", "ȷ��Ҫɾ����", 2) == 0) {
		  TParm tparm=table.getParmValue().getRow(row);
		  TParm dparm=new TParm();
		  dparm.setData("CONS_CODE", tparm.getValue("CONS_CODE"));
		  result=ConsApplicationTool.getInstance().deletedata(dparm);
		
		  if(result.getErrCode()<0){
			  this.messageBox(" ɾ��ʧ��");
			  return;
		  }
		       this.messageBox("ɾ���ɹ�");
		  }
		      this.onQueryall();
		      String clearString= "KIND_CODE;CONS_CODE;CONS_DATE;DR_CODE;DR_TEL;ACCEPT_DEPT_CODE;ASSEPT_DEPT_CODE; " +
	                              "ACCEPT_DR_TEL;ASSIGN_DR_CODE;ASSIGN_DR_TEL;ACCEPT_DR_CODE;REPORT_DEPT_CODE";
                clearValue(clearString);
	}
	
	/**
	 * ���Ӳ���
	 */
	
	public void onConsApplyDetail(){
		int row = table.getSelectedRow();
		if(row<0){
			this.messageBox(STRMESS);
			return;
		}
		
		onInpDepApp(this.getMrno(),this.getCaseno(),this.getAdmDate(),this.getIpdNo());//���Ӳ�����ֵ
		
	}
	/**
	 * ���Ӳ�������ֵ
	 * 
	 */
	public void onInpDepApp(String mrno,String caseno,Timestamp admDate,String ipdno) {
		TParm parm = new TParm();
		parm.setData("MR_NO", mrno);
		parm.setData("CASE_NO", caseno);
		parm.setData("ADM_DATE",admDate);
		parm.setData("IPD_NO",ipdno);
		this.openDialog("%ROOT%\\config\\inp\\INPDeptApp.x", parm);
	}
	
	/**
	 * 
	 */
	public String getBody(){
		 String   body =Operator.getName()+"ҽ����������:"+this.getMrno()+"����Ϊ:"+this.getPatName()+"�Ĳ���������" ;
		 return body;
		
	}
	/**
	 * ���﷢��������
	 * 
	 */
	public void onBoardMessage(String typecode, String type) {
		
		TParm parm =new TParm();
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("MR_NO", this.getMrno());
		parm.setData("USER_NAME", Operator.getName());
		parm.setData("PAT_NAME", this.getPatName());
		if("1".equals(type)){
		parm.setData("ASSIGN_DR_CODE", typecode);//����ָ��ҽ��
		}else{
		parm.setData("ACCEPT",typecode);//����ֵ�����/
		}
		//parm.setData("ASSEPT_DEPT_CODE",this.getAccdeptcode());//����ֵ�����/����ָ��ҽ��
		// ִ����������
		TParm result =this.onOdiBoardMessage(parm);
		// �����ж�
		if (result == null || result.getErrCode()<0) {
			this.messageBox("���淢��ʧ��" + " , " + result.getErrText());
			return;
		}
		   this.messageBox("���淢�ͳɹ�");
	}
	/**
	 * ���빫���� �ͽ��ܵ�
	 * @param parm
	 * @return
	 */
	  public TParm onOdiBoardMessage(TParm parm) {
	    	 TParm result = new TParm();
	    	 TParm sendMessage=new TParm();
	    	 TParm parmacc=new TParm();
	    	 
	    	 if(parm.getValue("ACCEPT").length()>0){
	    		 parmacc.setData("DEPT_CODE",parm.getValue("ACCEPT"));
		         sendMessage = ConsApplicationTool.getInstance().selectdacc(parmacc);//��ѯ����ֵ��ҽ��
	    	 }
	    	 
	    	if(parm.getValue("ASSIGN_DR_CODE").length()>0){
	    		 sendMessage.setData("ASSIGN_DR_CODE", parm.getValue("ASSIGN_DR_CODE"));
	    	}
	         
	         
	         if (sendMessage == null||sendMessage.getCount() <= 0) {
	             result.setErrText("û��δ���͵��ʼ���");
	         }
	        
	         
	         // 2.���͹�������Ϣ���Ҹ��¹�����״̬
	         TParm resultMessage = sendBoardOid(parm,sendMessage);
	         
	              TParm inparm = new TParm();
	         for (int i = 0; i < resultMessage.getCount("USER_ID"); i++) {
	        	
	             inparm.setData("MESSAGE_NO", getMessageNo());
	             inparm.setData("POST_TYPE", "P");
	             inparm.setData("POST_GROUP","ODI");
	             inparm.setData("USER_ID", resultMessage.getValue("USER_ID",i));
	             inparm.setData("READ_FLG", "N");
	             inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
	             inparm.setData("POST_TIME", SystemTool.getInstance().getDate());
	             inparm.setData("OPT_DATE", SystemTool.getInstance().getDate());
	             inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
	             inparm.setData("POST_SUBJECT", resultMessage.getValue("TITLE",0));
	             inparm.setData("URG_FLG", "Y");
	             inparm.setData("POST_INFOMATION", resultMessage.getValue("BODY",0));
	             inparm.setData("RESPONSE_NO", 0);//��Ӧ����
	             inparm.setData("POST_ID", parm.getValue("OPT_USER"));
	            result = ConsApplicationTool.getInstance().insertPostRCV(inparm);
	             if (result.getErrCode() < 0) {
	                 return result;
	             }
	              
	             result = ConsApplicationTool.getInstance().insertBoard(inparm);
	             if (result.getErrCode() < 0) {
	                 return result;
	             }
	        }
	         return result;
	    }
		/**
		 * ������������,���͹������Ա
		 */
		private TParm sendBoardOid(TParm parm, TParm sendMessage) {
			TParm result = new TParm();
			String title = "";
//			String body = "";
			title = "�������뱨�浥";// ���ﱨ�����
//			body = parm.getValue("USER_NAME") + "ҽ����������Ϊ��" + parm.getValue("MR_NO")
//					+ ",����Ϊ��" + parm.getValue("PAT_NAME") + "�Ĳ���������";
			result.addData("TITLE", title);
			result.addData("BODY", getBody());
			if (sendMessage.getValue("ASSIGN_DR_CODE").length() > 0) {
				result.addData("USER_ID", sendMessage.getValue("ASSIGN_DR_CODE"));// ��ָ��ҽ��
			} else {
				for (int i = 0; i < 6; i++) {
					if (null!=sendMessage.getValue("DR_CODE"+(i+1), 0)
							&&sendMessage.getValue("DR_CODE"+(i+1), 0).length()>0) {
						result.addData("USER_ID", sendMessage.getValue("DR_CODE"+(i+1), 0));// ֵ��ҽ��
					}
				}
			}
			return result;
		}
	  /**
	     * ȡ����ˮ��
	     * @return String
	     */
	    private synchronized String getMessageNo() {
	        String messageNo = "";
	        messageNo = SystemTool.getInstance().getNo("ALL", "PUB", "MESSAGE_NO", "MESSAGE_NO");
	        return messageNo;
	    }
	    
	    
	    
	    /**
	     * �����ʼ�
	     */
	    
	    public void onBoardMessage(){
	    	int row = table.getSelectedRow();
	    	if(row<0){
	    		this.messageBox(STRMESS);
	    		return;
	    	}
	    	TParm result=this.onSendMessage(this.getTmessage());
	    	this.messageBox(result.getValue("MESSAGE"));
	    	
	    
	    	
	    	
	    }
	    
	    /**
	     * �����ʼ�
	     * @param parm TParm
	     * @return TParm
	     */
	    public TParm onSendMessage(TParm parm) {
	    
	        TParm result = new TParm();
	        TParm sendMessage=new TParm();
	        TParm parmacc=new TParm();
	        TParm treslut=new TParm();
	        // 1.��ѯδ���͵��ʼ�
	        

	    	 if(parm.getValue("ACCEPT_DEPT_CODE").length()>0){
	    		 parmacc.setData("DEPT_CODE",parm.getValue("ACCEPT_DEPT_CODE"));
		         sendMessage = ConsApplicationTool.getInstance().selectdacc(parmacc);//��ѯ����ֵ��ҽ��
		        
		         String sql1=" A.USER_ID='"+sendMessage.getValue("DR_CODE1",0)+"'";
		         String sql2=" A.USER_ID='"+sendMessage.getValue("DR_CODE2",0)+"'";
		         String sql3=" A.USER_ID='"+sendMessage.getValue("DR_CODE3",0)+"'";
		         String sql4=" A.USER_ID='"+sendMessage.getValue("DR_CODE4",0)+"'";
		         String sql5=" A.USER_ID='"+sendMessage.getValue("DR_CODE5",0)+"'";
		         String sql6=" A.USER_ID='"+sendMessage.getValue("DR_CODE6",0)+"'";
		         
		         TParm paramess1 = new TParm(TJDODBTool.getInstance().select(SQL+sql1));//����ֵ��ҽ��1
		         TParm paramess2 = new TParm(TJDODBTool.getInstance().select(SQL+sql2));//����ֵ��ҽ��2
		         TParm paramess3 = new TParm(TJDODBTool.getInstance().select(SQL+sql3));//����ֵ��ҽ��3
		         TParm paramess4 = new TParm(TJDODBTool.getInstance().select(SQL+sql4));//����ֵ��ҽ��4
		         TParm paramess5 = new TParm(TJDODBTool.getInstance().select(SQL+sql5));//����ֵ��ҽ��5
		         TParm paramess6 = new TParm(TJDODBTool.getInstance().select(SQL+sql6));//����ֵ��ҽ��6
		        
		              if(paramess1.getCount()>0){
		            	  treslut.addRowData(paramess1,0);
		              }
		              if(paramess2.getCount()>0){
		            	  treslut.addRowData(paramess2,0);
		              }
		              if(paramess3.getCount()>0){
		            	  treslut.addRowData(paramess3,0);
		              }
		              if(paramess4.getCount()>0){
		            	  treslut.addRowData(paramess4,0);
		              }
		              if(paramess5.getCount()>0){
		            	  treslut.addRowData(paramess5,0);
		              }
		              if(paramess6.getCount()>0){
		            	  treslut.addRowData(paramess6,0);
		              }
	    	 }
	    	 
	    	if(parm.getValue("ASSIGN_DR_CODE").length()>0){
	    		 //sendMessage.setData("ASSIGN_DR_CODE", parm.getValue("ASSIGN_DR_CODE"));
	    		 String messql=" A.USER_ID='"+parm.getValue("ASSIGN_DR_CODE")+"'"; 
	    		 TParm taprm = new TParm(TJDODBTool.getInstance().select(SQL+messql));//����ָ��ҽ��
	    		 treslut.addRowData(taprm, 0);
	    	}
	        
	        if (sendMessage == null || sendMessage.getCount() <= 0) {
	            result.setErrText("û��δ���͵��ʼ���");
	        }
		        // 1.�����ʼ�
		        TParm resultMessage = sendEMail(treslut);
		        String message = resultMessage.getValue("MESSAGE");
		        // 2.�����ʼ�״̬
	        for (int i = 0; i < resultMessage.getCount("CASE_NO"); i++) {
		            TParm inparm = new TParm();
		            inparm.setData("CASE_NO", this.getCaseno());
		            inparm.setData("EXAMINE_CODE", resultMessage.getValue("EXAMINE_CODE", i));
		            inparm.setData("EXAMINE_DATE", resultMessage.getValue("EXAMINE_DATE", i));
		            inparm.setData("EMAIL_STATUS", "Y");
		            inparm.setData("OPT_USER",Operator.getID());
		            inparm.setData("OPT_TERM",Operator.getIP());
		            result = ConsApplicationTool.getInstance().updateEMail(inparm);
	          
	            if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
	                return result;
	            }
	           }
			        result.setData("MESSAGE", message);
			        return result;
			    }
	    
	    /**
	     * �����ʼ����ҷ���
	     * @param parm TParm
	     * @return TParm
	     */
	    public TParm sendEMail(TParm parm) {
//	    System.out.println("======�������parm��"+parm);
	        String message = "";
	        boolean flg = false;
	        TParm result = new TParm();
	        String e_mail = "";
	        String title = "";
	        String dc_name_message = "";//yanjing 20131113 ͳ�Ʒ���ʧ��ҽ������
	       /// String body = "";
	        //String dr_name = "";
	        for (int i = 0; i < parm.getCount("E_MAIL"); i++) {
	        	
	            e_mail = parm.getValue("E_MAIL", i);
	            title = "���������ʼ�ͨ��";
	            //System.out.println("================"+e_mail +"||"+title+"||"+this.getBody());
	            //dr_name = parm.getValue("USER_ID", i);
	            //body =Operator.getName()+"ҽ����������Ϊ��"+this.getMrno()+",����Ϊ��"+this.getPatName()+"�Ĳ���������" ;
	            
	            flg = sendEMail(e_mail, title, this.getBody());
	            if (flg) {
	                result.addData("CASE_NO", this.getCaseno());
	                result.addData("EXAMINE_CODE", parm.getValue("EXAMINE_CODE", i));
	                result.addData("EXAMINE_DATE", parm.getValue("EXAMINE_DATE", i));
	                result.addData("ROLE_ID", parm.getValue("ROLE_ID", i));
	                result.addData("USER_ID", parm.getValue("USER_ID", i));
	                result.addData("TITLE", title);
	                result.addData("BODY", this.getBody());//�ʼ�����
	                result.addData("URG_FLG", parm.getValue("URG_FLG", i));
	            }else{
	            dc_name_message +=parm.getValue("USER_NAME", i)+ ",\n";
	            //message += dr_name + (flg ? "�ʼ����ͳɹ�" : "�ʼ�����ʧ��") + "\n";
	            } 
	        }
	           message=(flg ? "�ʼ����ͳɹ�" : dc_name_message+"�ʼ�����ʧ��") + "\n";
		         result.setData("MESSAGE", message);
		         return result;
	    }
	    /**
	     * �����ʼ����ҷ���
	     * @param email_address String
	     * @param title String
	     * @param body String
	     * @return TParm
	     */
	    public boolean sendEMail(String email_address, String title, String body) {
	        MailVO mail = new MailVO();
	        mail.getToAddress().add(email_address);
	        mail.setSubject(title);
	        mail.setContent(body);
	        TParm MailSendResult = MailUtil.getInstance().sendMail(mail);
	        if (MailSendResult.getErrCode() < 0) {
	            return false;
	        }
	        else {
	            return true;
	        }
	    }
	    
	    /**
	     * ���Ͷ���Ϣ
	     */
	    public void onMessage(){
	    	int row = table.getSelectedRow();
	    	if(row<0){
	    		this.messageBox(STRMESS);
	    		return;
	    	}
	    	TParm parm = new TParm();
	    	TParm tparm=this.getTmessage();//ѡ��һ�еõ����ﲿ������
	    	
	    	TParm parmacc=this.getTmessage();
	    	TParm sendMessage= new TParm();
	    	TParm tel= new TParm();
	    	String Doctor="";
		    	 if(tparm.getValue("ACCEPT_DEPT_CODE").length()>0){//������ֵ������µ�ҽ��������Ϣ
		    		  Doctor="���Ǳ���ֵ��ҽ����";
		    		  parmacc.setData("DEPT_CODE",tparm.getValue("ACCEPT_DEPT_CODE"));
			          sendMessage = ConsApplicationTool.getInstance().selectdMess(parmacc);//��ѯ����ֵ��ҽ���绰����
		    	 }
		    	 if(tparm.getValue("ASSIGN_DR_TEL").length()>0){//����ָ��ҽ���绰
		    		 Doctor="���Ǳ���ָ��ҽ����";
		    		 tel.addData("TEL1", tparm.getValue("ASSIGN_DR_TEL"));
		    	 }
		    	 
		    	 if(sendMessage.getValue("DR_TEL1",0).length()>0){//����ֵ��ҽ��1�绰
		    		tel.addData("TEL1", sendMessage.getValue("DR_TEL1",0));
		    	 }
		    	 if(sendMessage.getValue("DR_TEL2",0).length()>0){//����ֵ��ҽ��2�绰
		    	 	tel.addData("TEL1", sendMessage.getValue("DR_TEL2",0));
		    	 }
		    	 if(sendMessage.getValue("DR_TEL3",0).length()>0){//����ֵ��ҽ��3�绰
		    		tel.addData("TEL1", sendMessage.getValue("DR_TEL3",0));
		    	 }
		    	 if(sendMessage.getValue("DR_TEL4",0).length()>0){//����ֵ��ҽ��4�绰
		    		tel.addData("TEL1", sendMessage.getValue("DR_TEL4",0));
		    	 }
		    	 if(sendMessage.getValue("DR_TEL5",0).length()>0){//����ֵ��ҽ��5�绰
		    		 tel.addData("TEL1", sendMessage.getValue("DR_TEL5",0));
		    	 }
		    	 if(sendMessage.getValue("DR_TEL6",0).length()>0){//����ֵ��ҽ��6�绰
		    		 tel.addData("TEL1", sendMessage.getValue("DR_TEL6",0));
		    	  }
		    	 
	    	 
				parm.setData("Content", this.getBody().replace("��","��")+Doctor);//��������
				parm.setData("MrNo", this.mrno);//������
				//parm.setData("CaseNo", this.caseno);//�����
				parm.setData("Name", this.getPatName());//��������
				parm.setData("Title","����֪ͨ");//����
				parm.setData("SysNo","INP");//����	
				XmlUtil.createSmsFile(parm, tel);//��������XML����
	    
	    }
	    
	    
	    
}
