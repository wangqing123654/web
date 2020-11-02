package com.javahis.ui.opd;

import java.util.HashMap;
import java.util.Map;

import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 * 
 * <p>Title: ����ҽ������վ������ʷ��ѯ</p>
 * 
 * <p>Description:����ҽ������վ������ʷ��ѯ</p>
 * 
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 * 
 * <p>Company:JavaHis</p>
 * 
 * @author ehui 2008.09.01
 * @version 1.0
 */
public class OpdViewCaseHistoryControl extends TControl {

	//����MR_NO
	private String mrNo="";
	//�����¼TABLE
	private TTable table,diagTable,exaTable,opTable,medTable,chnTable,ctrlTable;
	//��ҽ����ǩCOMBO
	private TComboBox rxNoCom;
	//�����¼
	private TParm parm;
	//�����¼��ѯSQL
	private static final String SQL="SELECT A.ADM_DATE,A.REALDR_CODE DR_CODE,A.CASE_NO,B.PAT_NAME FROM REG_PATADM A,SYS_PATINFO B WHERE A.MR_NO='#' AND A.MR_NO=B.MR_NO ORDER BY A.ADM_DATE";
	//��ѯ���߿���
	private static final String SUBJ_SQL="SELECT * FROM OPD_SUBJREC WHERE CASE_NO='#'";
	//��ѯ���
	private static final String DIAG_SQL=
			"SELECT A.*,B.ICD_CHN_DESC " +
			"	FROM OPD_DIAGREC A,SYS_DIAGNOSIS B " +
			"	WHERE CASE_NO='#' " +
			"		  AND A.ICD_CODE=B.ICD_CODE " +
			"	ORDER BY A.MAIN_DIAG_FLG DESC,A.ICD_CODE ";
	//�������ѯ
	private static final String EXA_SQL="SELECT * FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='5' AND SETMAIN_FLG='Y' ORDER BY RX_NO,SEQ_NO";
	//���ò�ѯ
	private static final String OP_SQL="SELECT * FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='4' ORDER BY RX_NO,SEQ_NO";
	//��ҩ��ѯ
	private static final String MED_SQL="SELECT * FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='1' ORDER BY RX_NO,SEQ_NO";
	//����ҩƷ��ѯ
	private static final String CTRL_SQL="SELECT * FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='2' ORDER BY RX_NO,SEQ_NO";
	//��ҽ����ǩ��Ϣ��ѯ
	private static final String CHN_RX_SQL="SELECT DISTINCT RX_NO ID FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='3' ORDER BY RX_NO";
	//���ݴ���ǩ��ѯ��ҽ��Ϣ
	private static final String CHN_SQL_BY_RX="SELECT * FROM OPD_ORDER WHERE RX_NO='#'";
	//�ش�������ҩ����
	private TParm chnComboParm;
	//��ҽ����
	private Map chn=new HashMap();
    /**
     * ����Ӧҩ��
     */
    public void onInitParameter(){
    	Object obj=this.getParameter();
    	messageBox(obj.toString());
    	if(obj==null){
    		return ;
    	}
    	mrNo=(String)obj;
    	messageBox(mrNo);
    }
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		initComponent();
		if(!StringUtil.isNullString(mrNo)){
			this.setValue("MR_NO", mrNo);
			onMrNo();
		}
	}
	/**
	 * ��ʼ���ؼ�
	 */
	private void initComponent(){
		table=(TTable)this.getComponent("TABLE");
		diagTable=(TTable)this.getComponent("DIAG_TABLE");
		exaTable=(TTable)this.getComponent("EXA_TABLE");
		opTable=(TTable)this.getComponent("OP_TABLE");
		medTable=(TTable)this.getComponent("MED_TABLE");
		chnTable=(TTable)this.getComponent("CHN_TABLE");
		ctrlTable=(TTable)this.getComponent("CTRL_TABLE");
		rxNoCom=(TComboBox)this.getComponent("RX_NO");
		//====zhangp 20120628 start
//		TParm patParm=new TParm(TJDODBTool.getInstance().select("SELECT PAT_NAME,IDNO,MR_NO,PY1 FROM SYS_PATINFO ORDER BY MR_NO"));
//		if(patParm.getErrCode()!=0){
//			this.messageBox_("ȡ�ò�������ʧ��");
//			return;
//		}
		//====zhangp 20120628 end
	}
	/**
	 * ����¼�
	 */
	public void onClear(){
		this.setValue("MR_NO", "");
		this.setValue("ID_NO", "");
		this.setValue("PAT_NAME", "");
		this.setValue("SUBJ_TEXT", "");
		this.setValue("OBJ_TEXT", "");
		this.setValue("PHYSEXAM_REC", "");
		table.removeRowAll();
		diagTable.removeRowAll();
		exaTable.removeRowAll();
		opTable.removeRowAll();
		medTable.removeRowAll();
		chnTable.removeRowAll();
		ctrlTable.removeRowAll();
		rxNoCom.removeAllItems();
	}
	
	/**
	 * ���ݸ���MR_NO��ʼ�������¼TABLE
	 * @return
	 */
	private boolean initForm(){
		String sql=SQL.replace("#", mrNo);
		parm=new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getErrCode()!=0){
			return false;
		}
		table.setParmValue(parm);
		this.setValue("PAT_NAME", parm.getValue("PAT_NAME",0));
		return true;
	}
	/**
	 * �����¼TABLE�����¼�
	 */
	public void onTableClicked(){
		int row=table.getSelectedRow();
		if(row<0||parm==null){
			return;
		}
		String caseNo=parm.getValue("CASE_NO",row);
//		this.messageBox_(caseNo);
		if(StringUtil.isNullString(caseNo)){
//			this.messageBox_("no caseNo");
			return;
		}
		//�����߿��߸�ֵ
		String subjSql=SUBJ_SQL.replace("#", caseNo);
		TParm subjRec=new TParm(TJDODBTool.getInstance().select(subjSql));
		// System.out.println("subjRec="+subjRec);
		if(subjRec.getErrCode()<0){
			this.messageBox_("��ѯ���߿�������ʧ��");
			return;
		}
		int count=subjRec.getCount();
		if(count>0){
			this.setValue("SUBJ_TEXT", subjRec.getValue("SUBJ_TEXT",0));
			this.setValue("OBJ_TEXT", subjRec.getValue("OBJ_TEXT",0));
			this.setValue("PHYSEXAM_REC", subjRec.getValue("PHYSEXAM_REC",0));
		}
		
		String diagSql=DIAG_SQL.replace("#", caseNo);
		// System.out.println("diuagSql="+diagSql);
		TParm diagRec=new TParm(TJDODBTool.getInstance().select(diagSql));
		if(diagRec.getErrCode()<0){
			this.messageBox_("��ѯ�����Ϣʧ��");
			return;
		}
		diagTable.setParmValue(diagRec);
		//��,30,boolean;��,30,int;ҽ��,200;����,40,double,##0.00;��λ,40,UNIT_CODE;Ƶ��,50,FREQ_CODE;�շ�,40,int,#0;����,40,double;ִ�п���,80,OP_EXEC_DEPT;ҽ����ע,200;��ʿ��ע,200;����,40,boolean;�������,80,INS_PAY
		//LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;DISPENSE_QTY;EXEC_DEPT_CODE;DR_NOTE;NS_NOTE;URGENT_FLG;INS_PAY
		
		String exaSql=EXA_SQL.replace("#", caseNo);
		TParm exa=new TParm(TJDODBTool.getInstance().select(exaSql));
		if(exa.getErrCode()<0){
			this.messageBox_("��ѯҽ����Ϣʧ��");
			return;
		}
		exaTable.setParmValue(exa);
		
		String opSql=OP_SQL.replace("#", caseNo);
		TParm op=new TParm(TJDODBTool.getInstance().select(opSql));
		if(op.getErrCode()<0){
			this.messageBox_("��ѯҽ����Ϣʧ��");
			return;
		}
		opTable.setParmValue(op);
		
		String medSql=MED_SQL.replace("#", caseNo);
		TParm med=new TParm(TJDODBTool.getInstance().select(medSql));
		if(med.getErrCode()<0){
			this.messageBox_("��ѯҽ����Ϣʧ��");
			return;
		}
		medTable.setParmValue(med);
		
		String chnRxSql=CHN_RX_SQL.replace("#", caseNo);
		// System.out.println("chnRxSql="+chnRxSql);
		chnComboParm=new TParm(TJDODBTool.getInstance().select(chnRxSql));
		if(chnComboParm==null||chnComboParm.getErrCode()<0){
			this.messageBox_("��ѯҽ����Ϣʧ��");
			return;
		}
		count=chnComboParm.getCount();
		for(int i=0;i<count;i++){
			chnComboParm.setData("NAME", i, "��"+(i+1)+"�Ŵ���");
		}
		rxNoCom.setParmValue(chnComboParm);
		rxNoCom.setSelectedID(chnComboParm.getValue("ID",0));
		onChangeRx();
		
		String ctrlSql=CTRL_SQL.replace("#", caseNo);
		TParm ctrl=new TParm(TJDODBTool.getInstance().select(ctrlSql));
		if(ctrl.getErrCode()<0){
			this.messageBox_("��ѯҽ����Ϣʧ��");
			return;
		}
		ctrlTable.setParmValue(ctrl);
	}
	/**
	 * ����ǩ�ı��¼�
	 */
	public void onChangeRx(){
		String rxNo=this.rxNoCom.getSelectedID();
		if(StringUtil.isNullString(rxNo)){
			return;
		}
		TParm parm=new TParm();
//		if(chn.get(rxNo)==null){
//			
//			chn.put(rxNo, parm);
//		}else{
//			parm=(TParm)chn.get(rxNo);
//		}
		
		parm=new TParm(TJDODBTool.getInstance().select(this.CHN_SQL_BY_RX.replace("#", rxNo)));
		TParm chnParm=this.getChnParm(parm);
		// System.out.println("chnParm="+chnParm);
		chnTable.setParmValue(chnParm);
		this.setValue("TAKE_DAYS", parm.getValue("TAKE_DAYS",0));
		this.setValue("DCT_TAKE_QTY", parm.getValue("DCT_TAKE_QTY",0));
		this.setValue("FREQ_CODE", parm.getValue("FREQ_CODE",0));
		
		this.setValue("DCTAGENT_CODE", parm.getValue("DCTAGENT_CODE",0));
		this.setValue("CHN_ROUTE_CODE", parm.getValue("ROUTE_CODE",0));
	}
	/**
	 * ȡ��
	 * @param parm
	 * @return
	 */
	private TParm getChnParm(TParm parm){
		TParm result=new TParm();
		if(parm==null){
			result.setErrCode(-1);
			return result;
		}
		int count=parm.getCount();
		if(count<1){
			result.setErrCode(-1);
			return result;
		}
		int countAll=(count/4+1)*4;
		for(int i=0;i<countAll;i++){
			int row=i/4;
			int column=i%4+1;
			if(i<count){
				result.setData("ORDER_DESC"+column, row, parm.getValue("ORDER_DESC",i));
				result.setData("MEDI_QTY"+column,row,parm.getValue("MEDI_QTY",i));
				result.setData("DCTEXCEP_CODE"+column,row,parm.getValue("DCTEXCEP_CODE",i));				
			}else{
				result.setData("ORDER_DESC"+column, row, "");
				result.setData("MEDI_QTY"+column,row,"");
				result.setData("DCTEXCEP_CODE"+column,row,"");
			}

		}
		result.setCount(countAll);
		
		return result;
	}
	/**
	 * �ش�
	 */
	public void onFetch(){
		if(table==null){
			return;
		}
		if(table.getSelectedRow()<0){
			return;
		}
		TParm exa=exaTable.getParmValue();
		TParm op=opTable.getParmValue();
		TParm med=medTable.getParmValue();
		TParm ctrl=ctrlTable.getParmValue();
		TParm result=new TParm();
		if(TypeTool.getBoolean(this.getValue("ZS"))){
			result.setData("SUB",this.getValueString("SUBJ_TEXT"));	
		}
		if(TypeTool.getBoolean(this.getValue("KS"))){
			result.setData("OBJ",this.getValueString("OBJ_TEXT"));	
		}
		if(TypeTool.getBoolean(this.getValue("TZ"))){
			result.setData("PHY",this.getValueString("PHYSEXAM_REC"));	
		}
		if(TypeTool.getBoolean(this.getValue("ORDER"))){
			result.setData("EXA",exa);
			result.setData("OP",op);
			result.setData("MED",med);
			result.setData("CTRL",ctrl);
			if(chnComboParm!=null){
				int count=chnComboParm.getCount();
				if(count>-1){
					for(int i=0;i<count;i++){
						String rxNo=chnComboParm.getValue("ID",i);
						TParm chnParm=new TParm(TJDODBTool.getInstance().select(this.CHN_SQL_BY_RX.replace("#", rxNo)));
						chn.put(rxNo, chnParm);
					}
				}
			}
			result.setData("CHN",chn);
		}
		if(TypeTool.getBoolean(this.getValue("DIAG"))){
			result.setData("DIAG",diagTable.getParmValue());	
		}
		this.setReturnValue(result);
		this.closeWindow();
	}
	/**
	 * ���ݲ����Ų�ѯ�����¼
	 */
	public void onMrNo(){
		String no=this.getValueString("MR_NO");
		if(StringUtil.isNullString(no)){
			return;
		}
		TParm parm=new TParm(TJDODBTool.getInstance().select("SELECT LENGTH(MAX(MR_NO)) MR_LENGTH FROM SYS_PATINFO"));
		
		int maxLength=parm.getInt("MR_LENGTH",0);
		if(maxLength<=0){
			this.messageBox_("ȡ�ò�������ʧ��");
			return;
		}
		mrNo=StringTool.fill0(no, PatTool.getInstance().getMrNoLength()); //========  chenxi
		this.setValue("MR_NO", mrNo);
		initForm();
	}

}

