package com.javahis.ui.bil;


import java.sql.Timestamp;
import jdo.bil.BILContractDTool;
import jdo.bil.BILContractMTool;
import jdo.sys.Operator;
import jdo.sys.PATLockTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: ��ͬ��λ��Ϣά��
 * </p>
 * 
 * <p>
 * Description: ��ͬ��λ��Ϣά��
 * </p>
 * 
 * <p>
 * Copyright: Copyright bluecore
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author caowl
 * @version 1.0
 */
public class BILContractConfigControl extends TControl{


	//action��·��
	private static final String actionName = "action.bil.BILContractAction";
	
    //ѡ�����
	
	// �ϴε�ѡ��ҳǩ����
	private int lastSelectedIndex = 0;
	private TTabbedPane tTabbedPane_0;
	// ��������
	private Pat pat;
	
	/**
	 * ��һ��ҳǩ�Ŀ���  ��ͬ��λ��Ϣ
	 * */
	private TComboBox  REGION_CODE;
	private TTextField CONTRACT_CODE;
	private TTextField CONTRACT_DESC;
	private TTextField TEL1;
	private TTextField CONTACT;
	private TTextField TEL2;
	private TTextField ADDRESS;
	private TTextField DESCRIPTION;
	private TTextField LIMIT_AMT;
	private TTable table1;//��ͬ��
	
	/**
	 * �ڶ���ҳǩ�Ŀ���  ������Ϣ
	 * */  

	private TTextField CONTRACT_CODE1;
	private TTextField CONTRACT_DESC1;
	private TTextField MR_NO;
	private TTextField PAT_NAME;
	private TComboBox SEX; 
	private TTextField TEL;
	private TCheckBox FOREIGN_FLG;
	private TTextField IDNO;
	private TTextFormat BIRTH_DATE;
	private TComboBox CTZ1_CODE;
	private TComboBox CTZ2_CODE;
	private TComboBox CTZ3_CODE;
	private TTextField  LIMIT;
    private TTable table2;//Ա����
    //���ﲡ��TCheckBox
    private TCheckBox oldPat;
    
  
    int selectRow = -1;
    public BILContractConfigControl() {

    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        //���ȫ���ؼ�
        getAllComponent();  
        oldPat.setSelected(true);
        //��ѯȫ����Ϣ
        this.onQuery();
        //��ͬ��λ��Ϣ��
		callFunction("UI|Table1|addEventListener", "Table1->"
				+ TTableEvent.CLICKED, this, "onTableClickedForTable1");
		//������Ϣ��
		callFunction("UI|Table2|addEventListener", "Table2->"
				+ TTableEvent.CLICKED, this, "onTableClickedForTable2");	
		this.setValue("REGION_CODE", Operator.getRegion());
		
    }
  

    /**
     * ��ʼ��ҳ��
     */
    private void getAllComponent() {
    	/**
		 * ���ѡ��ؼ�
		 */
		this.tTabbedPane_0 = (TTabbedPane) this.getComponent("TablePane");
		/**
		 * ��һ��ҳǩ
		 * */
		this.REGION_CODE = (TComboBox) this.getComponent("REGION_CODE");
		this.CONTRACT_CODE = (TTextField)this.getComponent("CONTRACT_CODE");
		this.CONTRACT_DESC =  (TTextField)this.getComponent("CONTRACT_DESC");
		this.TEL1 = (TTextField)this.getComponent("TEL1");
		this.CONTACT = (TTextField)this.getComponent("CONTACT");
		this.TEL2 = (TTextField)this.getComponent("TEL2");
		this.ADDRESS =  (TTextField)this.getComponent("ADDRESS");
		this.DESCRIPTION =  (TTextField)this.getComponent("DESCRIPTION");
		this.LIMIT_AMT = (TTextField)this.getComponent("LIMIT_AMT");		
    	this.table1=(TTable) this.getComponent("Table1");
    	/**
    	 * �ڶ���ҳǩ
    	 * */
    	
    	this.CONTRACT_CODE1 = (TTextField)this.getComponent("CONTRACT_CODE1");	
    	this.CONTRACT_DESC1 = (TTextField)this.getComponent("CONTRACT_DESC1");	   
    	this.MR_NO = (TTextField)this.getComponent("MR_NO");	
    	this.PAT_NAME = (TTextField)this.getComponent("PAT_NAME");	
    	this.SEX = (TComboBox)this.getComponent("SEX");	
    	this.TEL = (TTextField)this.getComponent("TEL");	
    	this.FOREIGN_FLG = (TCheckBox)this.getComponent("FOREIGN_FLG");	
        this.IDNO = (TTextField)this.getComponent("IDNO");	
        this.BIRTH_DATE=(TTextFormat)this.getComponent("BIRTH_DATE");	
        this.CTZ1_CODE = (TComboBox)this.getComponent("CTZ1_CODE");	
        this.CTZ2_CODE = (TComboBox)this.getComponent("CTZ2_CODE");	
        this.CTZ3_CODE = (TComboBox)this.getComponent("CTZ3_CODE");	
        this.LIMIT = (TTextField)this.getComponent("LIMIT");	
    	this.table2 = (TTable)this.getComponent("Table2");
    	this.oldPat = (TCheckBox)this.getComponent("FLG");
    	//System.out.println(oldPat.getTag());
    	//��������������
//    	addListener(table1);
//    	addListener(table2);
    	
    }
    
    /**
	 * ��ⲡ����ͬ����
	 */
	public void onPatName() {
		String patName = this.getValueString("PAT_NAME");
		if (StringUtil.isNullString(patName)) {
			return;
		}
		String selPat = "SELECT  DISTINCT(A.MR_NO) AS MR_NO, A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, B.EKT_CARD_NO "
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE PAT_NAME = '"
				+ patName
				+ "'  "
				+ " AND A.MR_NO = B.MR_NO (+) "
				+ " ORDER BY A.OPT_DATE,A.BIRTH_DATE";
	
		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}
		//setPatName1();
		// ѡ�񲡻���Ϣ
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("��ʾ��Ϣ", "������ͬ����������Ϣ,�Ƿ�������������Ϣ", 0);
			if (sameCount != 1) {
				this.grabFocus("PY1");
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;				
				onMrNo1(patParm.getValue("MR_NO"));
				return;
			}
		}
		this.grabFocus("PY1");
	}

    /**
     * ҳ���ѯ����
     */
    public void onQuery() {
    	// ��ȡҳǩ����
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		//��ѯȫ����ͬ��λ��Ϣ
    	if(selectedIndex == 0){
    		 setPrimaryKeyEnabled(true);
    	     TParm selectParm = getSelectedCondition();
    	     TParm result = BILContractMTool.getInstance().selectData("selectAll", selectParm);
    	     this.callFunction("UI|Table1|setParmValue", result);	
    	}
    	//��ѯĳ����ͬ��λ��ȫ��������Ϣ
    	if(selectedIndex == 1){   		
    		TParm parm = new TParm();     		
    	    parm.setData("CONTRACT_CODE1", CONTRACT_CODE1.getValue());
    	    putParamLikeWithObjName("MR_NO", parm);
    	    putParamLikeWithObjName("PAT_NAME", parm);
    	    putParamLikeWithObjName("CONTRACT_DESC1", parm);   	    		
    		TParm result = BILContractDTool.getInstance().selectData("selectAllPat", parm);  
    		//System.out.println("result:::"+result);
    		TParm selParm = new TParm();
    		//MR_NO;PAT_NAME;SEX_CODE;IDNO;TEL_HOME;LIMIT;CONTRACT_CODE;CONTRACT_DESC;FOREIGNER_FLG;BIRTH_DATE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE
    		int count = result.getCount();
    		for(int i = 0;i<count ;i++){
    			this.setValue("SEX_CODE",result.getData("SEX_CODE",i));
    		}
    		this.callFunction("UI|Table2|setParmValue", result);
    	}       
    }

    /**
     * ɾ������
     */
    public void onDelete() {
    	// ��ȡҳǩ����
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		//ɾ����ͬ��λ��Ϣ
		if(selectedIndex == 0){
			selectRow = this.getSelectedRow("Table1");
	        if (selectRow == -1) {
	            this.messageBox("��ѡ����Ҫɾ��������");
	            return;
	        }
	        if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) == 0) {
	            TTable table = (TTable)this.getComponent("Table1");
	            int selRow = table.getSelectedRow();
	            TParm tableParm = table.getParmValue();
	            String REGION_CODE = tableParm.getValue("REGION_CODE", selRow);
	            String CONTRACT_CODE = tableParm.getValue("CONTRACT_CODE", selRow);
	            TParm parm = new TParm();
	            parm.setData("REGION_CODE", REGION_CODE);
	            parm.setData("CONTRACT_CODE", CONTRACT_CODE);
	            TParm result = TIOM_AppServer.executeAction(actionName, "onDelAcctionCompany", parm);
	            if (result.getErrCode() < 0) {
	                messageBox(result.getErrText());
	                return;
	            }
	            //ɾ��������ʾ
	            int row = (Integer) callFunction("UI|Table1|getSelectedRow");
	            if (row < 0) {
	                return;
	            }
	            this.callFunction("UI|Table1|removeRow", row);
	            this.callFunction("UI|Table1|setSelectRow", row);
	            this.messageBox("P0003");
	        } else {
	            return;
	        }
		}
		//ɾ��ĳһ������Ϣ
		if(selectedIndex ==1){
			selectRow = this.getSelectedRow("Table2");
	        if (selectRow == -1) {
	            this.messageBox("��ѡ����Ҫɾ��������");
	            return;
	        }
	        if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) == 0) {
	            TTable table = (TTable)this.getComponent("Table2");
	            int selRow = table.getSelectedRow();
	            TParm tableParm = table.getParmValue();
	            String REGION_CODE = tableParm.getValue("REGION_CODE", selRow);
	            String CONTRACT_CODE = tableParm.getValue("CONTRACT_CODE", selRow);
	            String MR_NO = tableParm.getValue("MR_NO",selRow);
	            TParm parm = new TParm();
	            parm.setData("REGION_CODE", REGION_CODE);
	            parm.setData("CONTRACT_CODE", CONTRACT_CODE);
	            parm.setData("MR_NO",MR_NO);
	            TParm result = TIOM_AppServer.executeAction(actionName, "onDelAcctionPat", parm);
	            if (result.getErrCode() < 0) {
	                messageBox(result.getErrText());
	                return;
	            }
	            //ɾ��������ʾ
	            int row = (Integer) callFunction("UI|Table2|getSelectedRow");
	            if (row < 0) {
	                return;
	            }
	            this.callFunction("UI|Table2|removeRow", row);
	            this.callFunction("UI|Table2|setSelectRow", row);
	            this.messageBox("P0003");
	        } else {
	            return;
	        }
		}
        
    }

    /**
     * ��շ���
     */
    public void onClear() {
    	int selectedIndex = tTabbedPane_0.getSelectedIndex();
    	//��һ��ҳǩ���
    	if(selectedIndex ==0){
	    	this.setValue("REGION_CODE", "");
			this.setValue("CONTRACT_CODE", "");
			this.setValue("CONTRACT_DESC", "");
			this.setValue("TEL1", "");
			this.setValue("CONTACT", "");
			this.setValue("TEL2", "");
			this.setValue("LIMIT_AMT", "");
			this.setValue("ADDRESS", "");
			this.setValue("DESCRIPTION", "");
			CONTRACT_CODE.setEditable(true);
			table1.removeRowAll();
    	}
    	//�ڶ���ҳǩ���
    	if(selectedIndex == 1){    		
//    		this.setValue("CONTRACT_CODE1", "");
//    		this.setValue("CONTRACT_DESC1", "");
    		this.setValue("MR_NO", "");
    		this.setValue("PAT_NAME", "");
    		this.setValue("SEX", "");
    		this.setValue("TEL", "");
    		this.setValue("FOREIGN_FLG", "N");
    		this.setValue("IDNO", "");
    		this.setValue("BIRTH_DATE", "");
    		this.setValue("CTZ1_CODE", "");
    		this.setValue("CTZ2_CODE", "");
    		this.setValue("CTZ3_CODE", "");
    		this.setValue("LIMIT", "");   
    		MR_NO.setEditable(true);
    		PAT_NAME.setEditable(true);
//    		CONTRACT_CODE1.setEditable(true);
//    		CONTRACT_DESC1.setEditable(true);
    		table2.removeRowAll();
    		
    	}
    }

    /**
     * ���淽��
     */
    public void onSave() {
    	// ��ȡҳǩ����
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		//�����ͬ��λ��Ϣ
		if(selectedIndex == 0){
			 //��֤����
	        if (!validBasicData()) {
	            return;
	        }
	       //���ݺ�ͬ�����Զ�����ƴ��
	        String PY1 = TMessage.getPy(getValueString("CONTRACT_DESC"));
	        TParm parm = new TParm();
            parm.setData("PY1", PY1);//4
	        this.putParamWithObjName("CONTRACT_CODE", parm);//1
	        this.putParamWithObjName("REGION_CODE", parm);//19
	        this.putParamWithObjName("CONTRACT_DESC", parm);//2
	        this.putParamWithObjName("TEL1", parm);//10
	        this.putParamWithObjName("CONTACT", parm);//12
	        this.putParamWithObjName("TEL2", parm);//11
	        this.putParamWithObjName("ADDRESS", parm);//9
	        this.putParamWithObjName("DESCRIPTION", parm);//7
	        this.putParamWithObjName("LIMIT_AMT", parm);//13
	        this.putBasicSysInfoIntoParm(parm);//16,17,18
	        parm.setData("CONTRACT_ABS_DESC","");//3
	        parm.setData("PY2","");//5
	        parm.setData("DEL_FLG","N");//15
	        parm.setData("PREPAY_AMT","");//14
	        parm.setData("POST_CODE","");//8
	        //�ж������Ƿ����
			TParm result = BILContractMTool.getInstance().selectData("checkDataExist", parm);
			//System.out.println(result.getCount());		
	        boolean isdataExist = Integer.parseInt(result.getValue("DATACOUNT", 0)) >
	                              0 ? true : false;
	        TParm resultopt = null;
	        if (isdataExist) {
	        	//���������
	        	resultopt = TIOM_AppServer.executeAction(actionName, "onUpdAcctionCompany", parm);
	             if (resultopt.getErrCode() >= 0) {
	                this.messageBox("P0001");
	            } else {
	                this.messageBox("E0001");
	            }
	        } else {
	        	//�����������
	        	//��ѯ����seq
	        	String seq_max = "SELECT MAX(SEQ) AS SEQ  FROM BIL_CONTRACTM WHERE DEL_FLG = 'N' ";
	        	TParm seqParm = new TParm(TJDODBTool.getInstance().select(seq_max));
	        	int seq = 0;
	        	if(seqParm.getCount()<=0){
	        		seq = 1;
	        	}else{
	        		seq = seqParm.getInt("SEQ",0) + 1;
	        	}
	        	parm.setData("SEQ",seq);
	        	resultopt = TIOM_AppServer.executeAction(actionName, "onSaveAcctionCompany", parm);
	            if (resultopt.getErrCode() >= 0) {
	                this.messageBox("P0002");
	            } else {
	                this.messageBox("E0002");
	            }
	        }
	        this.onQuery();
		}
		//���没����Ϣ
		if(selectedIndex == 1){
			//1.��֤���ݵĺϷ���
			//2.�������
			TParm parm = new TParm();
			parm.setData("CONTRACT_CODE",CONTRACT_CODE.getValue());
			parm.setData("MR_NO",MR_NO.getValue());
			//�ж������Ƿ����
			TParm result = BILContractDTool.getInstance().selectData("checkPatDataExist", parm);
			//System.out.println(result.getCount());		
	        boolean isdataExist = Integer.parseInt(result.getValue("DATACOUNT", 0)) >
	                              0 ? true : false;
	        TParm resultopt = null;
	        //CONTRACT_CODE,MR_NO,LIMIT_AMT,PREPAY_AMT,DEL_FLG, COMPANY_FLG,PAY_FLG,OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE
	        parm.setData("CONTRACT_CODE",CONTRACT_CODE.getValue());
	        parm.setData("MR_NO",MR_NO.getValue());
	        parm.setData("LIMIT_AMT",LIMIT.getValue());
	        parm.setData("PREPAY_AMT","");
	        parm.setData("DEL_FLG","N");
	        parm.setData("COMPANY_FLG","1");
	        parm.setData("PAY_FLG","1");
	        parm.setData("REGION_CODE",REGION_CODE.getValue());
	        this.putBasicSysInfoIntoParm(parm);
	        if (isdataExist) {
	        	//���������
	        	resultopt = TIOM_AppServer.executeAction(actionName, "onUpdAcctionPat", parm);
	             if (resultopt.getErrCode() >= 0) {
	                this.messageBox("P0001");
	            } else {
	                this.messageBox("E0001");
	            }
	        } else {
	        	//�����������
	        	resultopt = TIOM_AppServer.executeAction(actionName, "onSaveAcctionPat", parm);
	            if (resultopt.getErrCode() >= 0) {
	                this.messageBox("P0002");
	            } else {
	                this.messageBox("E0002");
	            }
	        }
	        this.onQueryPat();
		}
       
    }
    /*
     *�����Żس���ѯ 
     */
    public void onMrNo(){
    	String mr_no = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
    	String sql = "SELECT MR_NO,PAT_NAME,FOREIGNER_FLG,IDNO,BIRTH_DATE," +
    			" CTZ1_CODE,CTZ2_CODE,CTZ3_CODE,TEL_HOME,SEX_CODE,ADDRESS " +
    			" FROM SYS_PATINFO WHERE MR_NO = '"+mr_no+"'";
    	TParm selParm  = new TParm(TJDODBTool.getInstance().select(sql));
    	if(selParm.getCount()<=0){
    		this.messageBox("�˲��˲����ڣ�");
    		return;
    	}
    	this.setValue("MR_NO", selParm.getData("MR_NO",0));    		
		this.setValue("PAT_NAME", selParm.getData("PAT_NAME",0));
		this.setValue("SEX", selParm.getData("SEX_CODE",0));
		this.setValue("TEL", selParm.getData("TEL_HOME",0));
		this.setValue("FOREIGN_FLG", selParm.getData("FOREIGNER_FLG",0));
		this.setValue("IDNO", selParm.getData("IDNO",0));
		this.setValue("BIRTH_DATE", selParm.getData("BIRTH_DATE",0));
		this.setValue("CTZ1_CODE", selParm.getData("CTZ1_CODE",0));
		this.setValue("CTZ2_CODE", selParm.getData("CTZ2_CODE",0));
		this.setValue("CTZ3_CODE", selParm.getData("CTZ3_CODE",0));
		//���ÿؼ�Ϊ���ɱ༭
		MR_NO.setEditable(false);
		PAT_NAME.setEditable(false);
		//��ѯ���ߵ�������� 
		String sqls = "SELECT LIMIT_AMT" +
						" FROM BIL_CONTRACTD " +
						" WHERE MR_NO = '"+mr_no+"' " +
						" AND CONTRACT_CODE = '"+this.getValueString("CONTRACT_CODE1")+"'";
		TParm selParms = new TParm(TJDODBTool.getInstance().select(sqls));		
		if(selParms.getCount()<= 0){
			this.setValue("LIMIT", ""); 
		}
		this.setValue("LIMIT", selParms.getValue("LIMIT_AMT",0));  
		this.onQuery();
    }
    
    /*
     *���ݲ����Ų�ѯ
     */
    public void onMrNo1(String mr_no){
    	
    	String sql = "SELECT MR_NO,PAT_NAME,FOREIGNER_FLG,IDNO,BIRTH_DATE," +
    			" CTZ1_CODE,CTZ2_CODE,CTZ3_CODE,TEL_HOME,SEX_CODE,ADDRESS " +
    			" FROM SYS_PATINFO WHERE MR_NO = '"+mr_no+"'";
    	TParm selParm  = new TParm(TJDODBTool.getInstance().select(sql));
    	if(selParm.getCount()<=0){
    		this.messageBox("�˲��˲����ڣ�");
    		return;
    	}
    	this.setValue("MR_NO", selParm.getData("MR_NO",0));    		
		this.setValue("PAT_NAME", selParm.getData("PAT_NAME",0));
		this.setValue("SEX", selParm.getData("SEX_CODE",0));
		this.setValue("TEL", selParm.getData("TEL_HOME",0));
		this.setValue("FOREIGN_FLG", selParm.getData("FOREIGNER_FLG",0));
		this.setValue("IDNO", selParm.getData("IDNO",0));
		this.setValue("BIRTH_DATE", selParm.getData("BIRTH_DATE",0));
		this.setValue("CTZ1_CODE", selParm.getData("CTZ1_CODE",0));
		this.setValue("CTZ2_CODE", selParm.getData("CTZ2_CODE",0));
		this.setValue("CTZ3_CODE", selParm.getData("CTZ3_CODE",0));
		//���ÿؼ�Ϊ���ɱ༭
		MR_NO.setEditable(false);
		PAT_NAME.setEditable(false);
		//��ѯ���ߵ�������� 
		String sqls = "SELECT LIMIT_AMT" +
						" FROM BIL_CONTRACTD " +
						" WHERE MR_NO = '"+mr_no+"' " +
						" AND CONTRACT_CODE = '"+this.getValueString("CONTRACT_CODE1")+"'";
		TParm selParms = new TParm(TJDODBTool.getInstance().select(sqls));		
		if(selParms.getCount()<= 0){
			this.setValue("LIMIT", ""); 
		}
		this.setValue("LIMIT", selParms.getValue("LIMIT_AMT",0));    
    }

    /**
	 * ��ѡ��л�ʱ ���ô˷���
	 */
	public void onChange() {
		// ��ȡҳǩ����
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		// ��ѯ�˵�����
		//menuControl();
		// ����ǰ��ѡ��ҳǩ����Ϊ0����ͬ��λ��Ϣ���������κδ���ֱ�ӷ���
		if (selectedIndex <= 0) {
			//���򲻿ɱ༭
			this.REGION_CODE.setEditable(false);
			// ��¼�˴ε�ѡ��ҳǩ����
			lastSelectedIndex = selectedIndex;
			return;
		}
		// ����ǰ��ѡ��ҳǩ����Ϊ1��������Ϣ��������֤�Ƿ�ѡ���˺�ͬ��λ
		if (selectedIndex == 1) {
			if (table1.getSelectedRow() < 0) {
				this.messageBox("��ѡ���ͬ��λ��Ϣ��");
				tTabbedPane_0.setSelectedIndex(0);
				return;
			}			
			//�����ǰ��Ϣ�����²�ѯʱ����Ϣ
			if (lastSelectedIndex == 0) {
				// �����ǰ��Ϣ
				onClear();
				// ģ����ѯ����
				onQueryPat();
				//��ʼ�����ֿؼ�
				CONTRACT_CODE1.setEditable(false);
				CONTRACT_DESC1.setEditable(false); 
				CONTRACT_CODE1.setValue(CONTRACT_CODE.getValue());
				CONTRACT_DESC1.setValue(CONTRACT_DESC.getValue());	
			}
		}
		
	}
	/**
	 * ģ����ѯ����
	 * */
	public void onQueryPat(){
		//ģ����ѯ����
		TParm parm = new TParm();
		parm.setData("CONTRACT_CODE1",CONTRACT_CODE.getValue());			
		TParm result = BILContractDTool.getInstance().selectData("selectAllPat", parm);
		this.callFunction("UI|Table2|setParmValue", result);	
	}
    /**
	 * ��nullת��Ϊ���ַ���
	 */
	public String nullToString(String str) {
		if (str == null || "null".equals(str)) {
			return "";
		} else {
			return str.trim();
		}
	}
    /**
     * ������ͬ��λ��Ϣ��
     * */
	public void onTableClickedForTable1(int row) {
		if (row < 0) {
			return;
		}
		// ��ȡ���ڱ༭״̬������
		table1.acceptText();
		// ��ͬ���벻�ɱ༭
		CONTRACT_CODE.setEditable(false);
		
		/*
		 * ��ȡѡ�е����ݣ�����Щ�������õ���������ؼ���
		 */
		REGION_CODE.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("REGION_CODE", row))));
		CONTRACT_CODE.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("CONTRACT_CODE", row))));
		CONTRACT_DESC.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("CONTRACT_DESC", row))));
		TEL2.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("TEL2", row))));
		ADDRESS.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("ADDRESS", row))));
		DESCRIPTION.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("DESCRIPTION", row))));
		LIMIT_AMT.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("LIMIT_AMT", row))));
		TEL1.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("TEL1", row))));
		CONTACT.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("CONTACT", row))));
		
	}
	 /**
     * ����������Ϣ��
     * */
	public void onTableClickedForTable2(int row) {
		if (row < 0) {
			return;
		}
		// ��ȡ���ڱ༭״̬������
		table2.acceptText();
		// ��ͬ���벻�ɱ༭
		CONTRACT_CODE1.setEditable(false);		
		//��ͬ��λ���Ʋ��ɱ༭		
		CONTRACT_DESC1.setEditable(false);
		//�����Ų��ɱ༭
		MR_NO.setEditable(false);
//		//�������ɱ༭
//		PAT_NAME.setEditable(false);
				
		//��ȡѡ�е����ݣ�����������ʾ�ڿؼ���
		CONTRACT_CODE1.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("CONTRACT_CODE", row))));
		CONTRACT_DESC1.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("CONTRACT_DESC", row))));
		MR_NO.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("MR_NO", row))));
		PAT_NAME.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("PAT_NAME", row))));
		SEX.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("SEX_CODE", row))));
		TEL.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("TEL_HOME", row))));
		this.setValue("FOREIGN_FLG",table2
				.getParmValue().getBoolean("FOREIGNER_FLG", row));
		IDNO.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("IDNO", row))));		
		CTZ1_CODE.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("CTZ1_CODE", row))));
		CTZ2_CODE.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("CTZ2_CODE", row))));
		CTZ3_CODE.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("CTZ3_CODE", row))));
		LIMIT.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("LIMIT", row))));
		this.setValue("BIRTH_DATE",table2
				.getParmValue().getTimestamp("BIRTH_DATE", row));
		
	}
    /**
     * ��֤��Ϣ
     * @return TParm
     */
    private boolean validBasicData() {    	
        //��ͬ��λ��������Ϊ��
        if (!this.emptyTextCheck("CONTRACT_DESC")) {
        	this.messageBox("��ͬ��λ��������Ϊ�գ�");
            return false;
        }
        //������Ϊ��
        if (!this.emptyTextCheck("REGION_CODE")) {
        	this.messageBox("������Ϊ�գ�");
            return false;
        }  
        //��ͬ��λ��Ų���Ϊ��
        if(!this.emptyTextCheck("CONTRACT_CODE")){
        	this.messageBox("��ͬ��λ���벻��Ϊ�գ�");
        	return false;
        }
        
        return true;
    }

    /**
     * ���ÿؼ��Ƿ���÷���
     * @param flag boolean
     */
    private void setPrimaryKeyEnabled(boolean flag) {
        TTextField tTextField = (TTextField)this.getComponent("CONTRACT_CODE");
        tTextField.setEnabled(flag);
    }

    /**
     * ��ҳ��õ���ѯ��������
     */
    private TParm getSelectedCondition() {
        TParm selectedCondition = new TParm();
        putParamLikeWithObjName("CONTRACT_CODE", selectedCondition);
        putParamLikeWithObjName("CONTRACT_DESC", selectedCondition);
        selectedCondition.setData("REGION_CODE", Operator.getRegion());
        return selectedCondition;
    }

    /**
     * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        objstr = objstr;
        //����ֵ��ؼ�����ͬ
        parm.setData(objName, objstr);
    }
    /**
     * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
     * @param objName String
     * @param parm TParm
     */
    private void putParamLikeWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr.trim() + "%";
            //����ֵ��ؼ�����ͬ
            parm.setData(objName, objstr);
        }
    }
    /**
     * �õ�ָ��table��ѡ����
     * @param tableName String
     * @return int
     */
    private int getSelectedRow(String tableName) {
        int selectedIndex = -1;
        if (tableName == null || tableName.length() <= 0) {
            return -1;
        }
        Object componentObj = this.getComponent(tableName);
        if (!(componentObj instanceof TTable)) {
            return -1;
        }
        TTable table = (TTable) componentObj;
        selectedIndex = table.getSelectedRow();
        return selectedIndex;
    }
    /**
     * ��TParm�м���ϵͳĬ����Ϣ
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm) {
      //  int total = parm.getCount();
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM", Operator.getIP());
    }
	//===============================
    /**
	 * ���没����Ϣ
	 */
	public void onSavePat() {
		if (pat != null)
			PatTool.getInstance().unLockPat(pat.getMrNo());
		// ���������ֵ
		if (getValue("BIRTH_DATE") == null) {
			this.messageBox("�������ڲ���Ϊ��!");
			return;
		}
		if (!this.emptyTextCheck("PAT_NAME,SEX,CTZ1_CODE"))
			return;
		pat = new Pat();
		// ��������
		pat.setName(TypeTool.getString(getValue("PAT_NAME")));
		// Ӣ����
		String PAT_NAME1 = SYSHzpyTool.getInstance().charToAllPy(
				TypeTool.getString(getValue("PAT_NAME")));
		pat.setName1(PAT_NAME1);
		// ����ƴ��
		String PY1 = SYSHzpyTool.getInstance().charToCode(
				TypeTool.getString(getValue("PAT_NAME")));
		pat.setPy1(PY1);
		// ���֤��
		pat.setIdNo(TypeTool.getString(getValue("IDNO")));
		// �����ע��
		pat.setForeignerFlg(TypeTool.getBoolean(getValue("FOREIGN_FLG")));
		// ��������
		pat.setBirthday(TypeTool.getTimestamp(getValue("BIRTH_DATE")));
		// �Ա�
		pat.setSexCode(TypeTool.getString(getValue("SEX")));
		// �绰
		pat.setTelHome(TypeTool.getString(getValue("TEL")));
		// �ʱ�
		pat.setPostCode("");
		// ��ַ
		pat.setAddress("");
		// ���1
		pat.setCtz1Code(TypeTool.getString(getValue("CTZ1_CODE")));
		// ���2
		pat.setCtz2Code(TypeTool.getString(getValue("CTZ2_CODE")));
		// ���3
		pat.setCtz3Code(TypeTool.getString(getValue("CTZ3_CODE")));
		// ҽ��������
		pat.setNhiNo(""); // =============pangben		
		if (this.messageBox("������Ϣ", "�Ƿ񱣴�", 0) != 0)
			return;
		TParm patParm = new TParm();
		patParm.setData("MR_NO", getValue("MR_NO"));
		patParm.setData("PAT_NAME", getValue("PAT_NAME"));
		patParm.setData("PAT_NAME1", PAT_NAME1);
		patParm.setData("PY1", PY1);
		patParm.setData("IDNO", getValue("IDNO"));
		patParm.setData("BIRTH_DATE", getValue("BIRTH_DATE"));
		patParm.setData("TEL_HOME", getValue("TEL"));
		patParm.setData("SEX_CODE", getValue("SEX"));
		patParm.setData("POST_CODE","123456");
		patParm.setData("ADDRESS", "1111111");
		patParm.setData("CTZ1_CODE", getValue("CTZ1_CODE"));
		patParm.setData("CTZ2_CODE", getValue("CTZ2_CODE"));
		patParm.setData("CTZ3_CODE", getValue("CTZ3_CODE"));
		patParm.setData("NHI_NO","1111");
		
		if (StringUtil.isNullString(getValue("MR_NO").toString())) {
			patParm.setData("MR_NO", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("PAT_NAME").toString())) {
			patParm.setData("PAT_NAME", new TNull(String.class));
		}
		if (StringUtil.isNullString(PAT_NAME1)) {
			patParm.setData("PAT_NAME1", new TNull(String.class));
		}
		if (StringUtil.isNullString(PY1)) {
			patParm.setData("PY1", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("IDNO").toString())) {
			patParm.setData("IDNO", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("BIRTH_DATE").toString())) {
			patParm.setData("BIRTH_DATE", new TNull(Timestamp.class));
		}
		if (StringUtil.isNullString(getValue("TEL").toString())) {
			patParm.setData("TEL_HOME", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("SEX").toString())) {
			patParm.setData("SEX_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString("")) {
			patParm.setData("POST_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString("")) {
			patParm.setData("ADDRESS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ1_CODE").toString())) {
			patParm.setData("CTZ1_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ2_CODE").toString())) {
			patParm.setData("CTZ2_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ3_CODE").toString())) {
			patParm.setData("CTZ3_CODE", new TNull(String.class));
		}
		
		if (StringUtil.isNullString("")) {
			patParm.setData("NHI_NO", new TNull(String.class));
		}
		TParm result = new TParm();
		
		if (!"".equals(getValueString("MR_NO"))) {
			
			if (getValue("MR_NO").toString().length() == 0) {
				this.messageBox("���ȼ���������");
				return;
			}
			// ���²���
			result = PatTool.getInstance().upDateForReg(patParm);
			setValue("MR_NO", getValue("MR_NO"));
			pat.setMrNo(getValue("MR_NO").toString());
		} else {
			// ��������
			
			pat.onNew();
			setValue("MR_NO", pat.getMrNo());
		}
		if (result.getErrCode() != 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
		}
	
		
	
	}
	/**
     * ����CHECK_BOX
     */
    public void isOldPat() {
        // ѡ��״̬
        if (oldPat.isSelected()) {
        	MR_NO.setValue("");
            this.callFunction("UI|MR_NO|grabFocus");
            MR_NO.setEnabled(true);
        }
        else {
        	MR_NO.setEnabled(false);
        }
    }
    
}
