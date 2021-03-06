package com.javahis.ui.mem;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import jdo.bil.PaymentTool;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.mem.MEMPatRegisterTool;
import jdo.opb.OPBTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSRuleTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.odo.ODOMainTmplt;
import com.javahis.util.StringUtil;

/**
 * <p>Title:�ײ����� </p>
 *
 * <p>Description:�ײ����� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author duzhw 2014.01.08
 * @version 4.5
 */
public class MEMPackageSalesTimeSelControl extends TControl {
	public ODOMainTmplt odoMainTmplt;//add by huangjw 20141106
	private TParm parmEKT;//ҽ�ƿ�����
	private String ektOper = "";//֧����ʽ 1���ֽ�   2��ҽ�ƿ�
	Pat pat;
	//String IPD_NO = "";
	String MR_NO = ""; 			// ������
	
	//�ײ����۽��׺�
	String tradeNo = "";
	int SECTION_ID = 0;			//����ʱ��id���
	//int SECTION_D_ID = 0;		//����ʱ����ϸid���
	
	//����ϸTABLE//ҽ���б�tTable
	private TTable table,orderTable;
	
	String sectionCode = "";	//ʱ�̱��
	String packageCode = "";	//�ײͱ��
	
	PaymentTool paymentTool;
	
	boolean oper = true;//ȫ�ֲ���
	boolean flage = false;//�ۿ������ڵ�ѡ����� add by sunqy 20140704
	double discountPrice = 0.00;//�ۿ۽��
	String  caseNo;//�����
	/**
     * ����
     */
    private TTreeNode treeRoot;
    
    /**
     * ��Ź�����𹤾�
     */
    SYSRuleTool ruleTool;
    
    /**
     * �������ݷ���datastore���ڶ��������ݹ���
     */
    TDataStore treeDataStore = new TDataStore();
    
    private DecimalFormat df = new DecimalFormat("##########0.00");
    /**
     * ��ʼ��
     */
    public void onInit() { // ��ʼ������
        super.onInit();
        table = getTable("TABLE");
        orderTable = getTable("ORDER_TABLE");
        
        this.grabFocus("MR_NO");//���ý���
//        //������ű�
//        table.removeRowAll();
//        orderTable.removeRowAll();
//        this.setValue("PRICE_TYPE", "01");
        // ��ʼ����
        onInitSelectTree();
        // ��tree��Ӽ����¼�
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        // ��Table��������¼�
//        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
//                         "onTableChangeValue");
        
//        table.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
//				this, "onTableChangeValue");
        // Table�����¼��༭
        addEventListener("TABLE->" + TTableEvent.CREATE_EDIT_COMPONENT,
        "onCreateEditComoponentUD");
//        // ҽ��ϸ������ҽ���¼�
//        addEventListener("ORDER_TABLE->" + TTableEvent.CREATE_EDIT_COMPONENT, this,
//                                     "onDetailCreateEditComponent");
        
        
        // ����
        onCreatTree();
        callFunction("UI|new|setEnabled", false);
        onPageInit();
        TPanel p = (TPanel) getComponent("tPanel_PAY_TYPE");//��ȡ֧����ʽtpanel
        try {
			paymentTool = new PaymentTool(p, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
        initComponent();
        initData();
        initTextFormat();
        this.setValue("PRICE_TYPE", "01");
        
        //ʧȥ���㡢��ý���ִ�е��¼� add by huangjw 20141106 start
		TNumberTextField ar_amt;
		ar_amt = (TNumberTextField) this.getComponent("AR_AMT");

		//
		ar_amt.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				// ʧȥ����ִ�еĴ���
				try {
					onChangeValue();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			public void focusGained(FocusEvent e) {
				// ��ý���ִ�еĴ���
			}

		});
		//ʧȥ���㡢��ý���ִ�е��¼� add by huangjw 20141106 end

		

    }
    /**
	 * ��ʼ���ؼ�
	 */
    private void initComponent() {
    	// ��ORDER_TABLE��������¼�
        addEventListener("ORDER_TABLE->" + TTableEvent.CHANGE_VALUE,
                         "onOrderTableChangeValue");
        //�ѹ��ײ��û�
        callFunction("UI|package|setEnabled", false);
    }
    /**
     * ��ʼ���ؼ�
     */
    private void initTextFormat() {
		String sql = " SELECT ID, CHN_DESC NAME" + " FROM SYS_DICTIONARY"
				+ " WHERE GROUP_ID = 'MEM_PACKAGE_TYPE' "
				//
				+ "AND ACTIVE_FLG = 'Y' ORDER BY SEQ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));

		TTextFormat memCombo = (TTextFormat) getComponent("PRICE_TYPE");

		memCombo.setHorizontalAlignment(2);
		memCombo.setPopupMenuHeader("����,100;����,100");
		memCombo.setPopupMenuWidth(300);
		memCombo.setPopupMenuHeight(300);
		memCombo.setFormatType("combo");
		memCombo.setShowColumnList("NAME");
		memCombo.setValueColumn("ID");
		memCombo.setPopupMenuData(parm);
//		memCombo.setClickedAction("onQuery");
//		memCombo.addEventListener(TTextFormatEvent.EDIT_ENTER, this, "onQuery");

	}

    
    
    /**
     * ��ʼ������
     */
    private void initData(){
    	//�ۿ�Ĭ��Ϊ1,������
    	this.setValue("DISCOUNT_RATE", "1.00");
    	
    	TRadioButton ektPay = (TRadioButton) getComponent("gatherFlg2");
		TRadioButton cashPay = (TRadioButton) getComponent("gatherFlg1");
		ektPay.setSelected(false);
		cashPay.setSelected(true);
    	this.onGatherChange(0);
    }
    /**
     * ��ʼ����
     */
    public void onInitSelectTree() {
        // �õ�����
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        if (treeRoot == null)
            return;
        // �����ڵ����������ʾ
        treeRoot.setText("�ײ͹���");
        // �����ڵ㸳tag
        treeRoot.setType("Root");
        // ���ø��ڵ��id
        treeRoot.setID("");
        // ������нڵ������
        treeRoot.removeAllChildren();
        // ���������ʼ������
        callMessage("UI|TREE|update");
    }
    /**
     * ��ʼ�����ϵĽڵ�
     */
    public void onCreatTree() {
        // ��dataStore��ֵ
        treeDataStore.setSQL("SELECT PACKAGE_CODE, PARENT_PACKAGE_CODE,PACKAGE_DESC,"
                             + " PACKAGE_ENG_DESC, PY1, PY2, SEQ,DESCRIPTION,"
                             + "  ORIGINAL_PRICE, PACKAGE_PRICE, OPT_DATE, OPT_USER,"
                             + "  OPT_TERM  FROM MEM_PACKAGE"
                             + "  ");
        // �����dataStore���õ�������С��0
        if (treeDataStore.retrieve() <= 0)
            return;
        // ��������,�Ǳ�������еĿ�������
        ruleTool = new SYSRuleTool("SYS_MEMPACKAGE");
        if (ruleTool.isLoad()) { // �����۽ڵ����:datastore���ڵ����,�ڵ���ʾ����,,�ڵ�����
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                "PACKAGE_CODE", "PACKAGE_DESC", "Path", "SEQ");
            
            // ѭ����������ڵ�
            for (int i = 0; i < node.length; i++){
            	treeRoot.addSeq(node[i]);
//                System.out.println("node="+node[i]);
            }
                
        }
        // �õ������ϵ�������
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        // ������
        tree.update();
        // ��������Ĭ��ѡ�нڵ�
        tree.setSelectNode(treeRoot);
    }

    /**
     * ������
     *
     * @param parm
     *            Object
     */
    public void onTreeClicked(Object parm) { // ������ť������
        callFunction("UI|new|setEnabled", false);
        // �õ�������Ľڵ����
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        // �õ�table����
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        // table�������иı�ֵ
        table.acceptText();
        // �������������ĸ����
//        System.out.println("���ڵ㣺"+node.getType());
        if (node.getType().equals("Root")){
            // ���ݹ�����������Tablet�ϵ�����
//        	String id = node.getID();
        	//openMainPackage();//�����ײ�����ά��ҳ��
//        	System.out.println("Root id="+id);
            table.setFilter(getQuerySrc());
        	//table.setFilter("");
        }else { // �����Ĳ��Ǹ����

            // �õ���ǰѡ�еĽڵ��idֵ
            String id = node.getID();
            packageCode = id;
            // �õ���ID
//            String parentID = node.getID();
//            int classify = 1;
//            if (parentID.length() > 0)
//                classify = ruleTool.getNumberClass(parentID) + 1;
            // �������С�ڵ�,��������һ��
//            if (classify > ruleTool.getClassifyCurrent()) {
        	if (isLeaf(id)) {
            	//�жϲ������Ƿ����-�Ⱦ�����
            	if(existMrNo()){
            		showSectionDetail(id);//ʱ����ϸ
            	}else{
            		this.grabFocus("MR_NO");
            	}
                
            }else{
            	//onClear();
            }
            
            
            
//            // �õ���������
//            String s = getQuerySrc();
//            // ������������д�������
//            if (s.length() > 0)
//                s += " AND ";
//            s += "PACKAGE_CODE like '" + id + "%'";
//            // table�е�datastore�й��������������
//            table.setFilter(s);
        }
        // ִ��table�Ĺ���
//        table.filter();
//        // ��table���ݼ���������
//        table.setSort("PACKAGE_CODE");
//        // table��������¸�ֵ
//        table.sort();
//        // �õ���ID
//        String parentID = node.getID();
//        int classify = 1;
//        if (parentID.length() > 0)
//            classify = ruleTool.getNumberClass(parentID) + 1;
//        // �������С�ڵ�,��������һ��
//        if (classify > ruleTool.getClassifyCurrent()) {
//            callFunction("UI|new|setEnabled", true);
//        }
    }
    
    /**
     * �õ�������
     *
     * @return TTree
     */
    public TTree getTree() {
        return (TTree) callFunction("UI|TREE|getThis");
    }
    /**
     * ��ʼ������
     */
    public void onPageInit() {
        String s = "";
        //===========pangben modify 20110422 start
        if (null != Operator.getRegion() && !"".equals(Operator.getRegion()))
            s = " REGION_CODE='" + Operator.getRegion() + "' ";
        //===========pangben modify 20110422 stop

        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        table.setFilter(s);
        // ��table���ݼ���������
        table.setSort("DEPT_CODE");
        // table��������¸�ֵ
        table.sort();
        // ִ��table�Ĺ���
        table.filter();
    }
    /**
     * ������ѯ
     */
    public void onPatinfoQuery() {
        TParm sendParm = new TParm();
        TParm reParm = (TParm)this.openDialog(
                "%ROOT%\\config\\adm\\ADMPatQuery.x", sendParm);
        if (reParm == null)
            return;
        this.setValue("MR_NO", reParm.getValue("MR_NO"));
        //System.out.println("�����ţ�"+reParm.getValue("MR_NO"));
        this.onMrno();
    }
    
    /**
     * �����Żس���ѯ�¼�
     */
    public void onMrno() {
   	 	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
   	 	Date currentDate=SystemTool.getInstance().getDate();
        String date = df.format(currentDate);
        setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // ��������
        pat = new Pat();
        String mrno = getValue("MR_NO").toString().trim();
        if (!this.queryPat(mrno))
            return;
        pat = Pat.onQueryByMrNo(mrno);
        if (pat == null || "".equals(getValueString("MR_NO"))) {
            this.messageBox_("���޲���! ");
            this.onClear(); // ���
            //this.setUi(); // ������Ϣ�ɱ༭
            //this.setUIAdmF(); // סԺ�Ǽ���Ϣ���ɱ༭
            //this.setMenu(false);
            return;
        } else {
            callFunction("UI|MR_NO|setEnabled", false); // ������
            callFunction("UI|IPD_NO|setEnabled", false); // סԺ��
            //�ѹ��ײ�����Ч
            callFunction("UI|package|setEnabled", true);
            //callFunction("UI|patinfo|setEnabled", true); // ������Ϣ
            //this.callFunction("UI|NEW_PAT|setText", "�޸Ĳ�������");
            //callFunction("UI|NEW_PAT|setEnabled", true); // ��������botton
            //callFunction("UI|PHOTO_BOTTON|setEnabled", true);
            MR_NO = pat.getMrNo();
        }
        
      //huangtt 20180515
		double payOther3 = EKTpreDebtTool.getInstance().getPayOther(pat.getMrNo(), EKTpreDebtTool.PAY_TOHER3);
		double payOther4 = EKTpreDebtTool.getInstance().getPayOther(pat.getMrNo(), EKTpreDebtTool.PAY_TOHER4);
		setValue("GIFT_CARD", payOther3);
		setValue("GIFT_CARD2", payOther4);
		setValue("NO_PAY_OTHER_ALL", getValueDouble("EKT_CURRENT_BALANCE") - payOther3 - payOther4);

        
        this.setPatForUI(pat);
        Timestamp t = StringTool.getTimestamp(date, "yyyyMMddHH");//add by lich 20141202 
        this.setValue("START_DATE", t);//add by lich 20141202 
//        //���ʧЧ����  start by huangjw 20141126
//        Calendar gc = Calendar.getInstance();
//        gc.setTime(currentDate);
//        gc.add(Calendar.YEAR,1);
//        gc.add(Calendar.DAY_OF_YEAR,-1);
//		this.setValue("END_DATE", gc.getTime());
//		//���ʧЧ����   end by huangjw 20141126
       
        t = StringTool.rollDate(t, 364);//add by lich 20141202 
        this.setValue("END_DATE", t);//add by lich 20141202 
        
        
        //���������ڵ���ڣ���ִ��
        if(packageCode.length()>0){
        	showSectionDetail(packageCode);//ʱ����ϸ
        }
    }
    /**
     * ������Ϣ��ֵ
     *
     * @param patInfo
     *            Pat
     */
    public void setPatForUI(Pat patInfo) {
        // ������,����,�Ա�,����,���֤��,�绰
        this.setValueForParm(
                "MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;IDNO;TEL_HOME",patInfo.getParm());
        this.setValue("IPD_NO", pat.getIpdNo());
        this.setValue("CTZ_CODE1", patInfo.getCtz1Code());//���������ʾadd by huangjw 20141126
        
        setValue("CUSTOMER_SOURCE",MEMPatRegisterTool.getInstance().getMemCustomerSource(patInfo.getMrNo()));
		
        //System.out.println("tttttt:::"+patInfo.getParm());
        //add by sunqy 20140812 ����ĩ���¾����ڼ���Ԥ������Ϣ  ----start----
        //modified by lich 20141016 ---start---
        String lmpStr = patInfo.getParm().getValue("LMP_DATE",0);
		if (!"".equals(lmpStr) && lmpStr != null) {//���ж�LMP_DATEʱ���Ƿ�Ϊ�գ�����ִ�лᱨ��setBirth()�����Ͳ�ִ���� modify by huangjw 20141106
			Timestamp lmp = StringTool.getTimestamp(lmpStr, "yyyyMMddHHmmss");
			// modified by lich 20141016 ---end---

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(lmp);
			calendar.add(Calendar.DAY_OF_YEAR, +279);
			String lmpDate = sdf.format(calendar.getTime());
			this.setValue("LMP_DATE", lmpDate);
		}
        //add by sunqy 20140812 ����ĩ���¾����ڼ���Ԥ������Ϣ  ----end----
        setBirth(); // ��������
    }
    /**
     * ��������
     */
    public void setBirth() {
        if ("".equals(this.getValueString("BIRTH_DATE")))
            return;
//        String AGE = com.javahis.util.StringUtil.showAge(
//                (Timestamp) getValue("BIRTH_DATE"),
//                (Timestamp) getValue("IN_DATE"));
        String AGE = com.javahis.util.DateUtil.showAge(
                (Timestamp) getValue("BIRTH_DATE"),
                (Timestamp) getValue("IN_DATE"));
        setValue("AGE", AGE);
    }
    /**
     * ��ѯ������Ϣ
     * @param mrNo String
     * @return boolean
     */
    public boolean queryPat(String mrNo) {
        //this.setMenu(false); //MENU ��ʾ����
        pat = new Pat();
        pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            //this.setMenu(false); //MENU ��ʾ����
            this.messageBox("E0081");
            return false;
        }
        String allMrNo = PatTool.getInstance().checkMrno(mrNo);
        if (mrNo != null && !allMrNo.equals(pat.getMrNo())) {
            //============xueyf modify 20120307 start
            messageBox("������" + allMrNo + " �Ѻϲ���" + pat.getMrNo());
            //============xueyf modify 20120307 stop
        }

        return true;
    }
    
//    /**
//     * ���ݾ������ڻ�ȡ����� add by huangjw 20141027
//     */
//    public void getCaseNo(){
//    	if("".equals(this.getValueString("MR_NO"))){
//    		this.messageBox("�����벡���ţ�");
//    		return;
//    	}
//    	String adm_date=this.getValueString("ADM_DATE");
//    	String adm_date_q=adm_date.substring(0,4)+adm_date.substring(5,7)+adm_date.substring(8,10);
//    	TParm result=new TParm(TJDODBTool.getInstance().select("SELECT CASE_NO FROM REG_PATADM WHERE" 
//    			+" MR_NO='"+this.getValueString("MR_NO")+"' AND ADM_DATE=TO_DATE('"+adm_date_q+"','YYYYMMDDHH24MISS')"));
//    	if(result.getCount()<0){
//    		this.messageBox("û�о����¼");
//    		
//    	}else{
//    		if(result.getCount()==1){
//    			caseNo=result.getValue("CASE_NO",0);
//    		}else{
//    			TParm parm=new TParm();
//    			parm.setData("MR_NO", pat.getMrNo());
//    			parm.setData("PAT_NAME", pat.getName());
//    			parm.setData("SEX_CODE", pat.getSexCode());
//    			parm.setData("AGE", this.getValue("AGE"));
//    			// �ж��Ƿ����ϸ�㿪�ľ����ѡ��
//    			parm.setData("count", "0");
//    			caseNo=(String) openDialog(
//    					"%ROOT%\\config\\opb\\OPBChooseVisit.x", parm);
//    			
//    		}
//    			orderTable(caseNo);
//    	}
//    	
//    }
   /* *//**
     * ���ݾ���Ų�ѯҽ���б� add by huangjw 20141027
     * @param caseno
     *//*
    public void orderTable(String caseno){
    	
    	String sql="SELECT B.CHN_DESC AS ADM_TYPE,A.ORDER_DESC,A.DOSAGE_QTY,C.UNIT_CHN_DESC AS DOSAGE_UNIT,"
    			+" A.AR_AMT,D.USER_NAME AS DR_CODE,A.ORDER_DATE,A.BILL_FLG,A.PRINT_FLG,A.EXEC_FLG" 
    			+" FROM OPD_ORDER A, SYS_DICTIONARY B,SYS_UNIT C,SYS_OPERATOR D " 
    			+" WHERE B.GROUP_ID='SYS_ADMTYPE' AND ID=A.ADM_TYPE" 
    			+" AND C.UNIT_CODE=A.DOSAGE_UNIT "
    			+" AND D.USER_ID=A.DR_CODE"
    			+" AND HIDE_FLG = 'N' AND CASE_NO = '"+caseno+"'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount()>0){
    		tTable.setParmValue(result);
    	}else{
    		this.messageBox("û������");
    	}
    }*/
    /**
     * ��ѯ
     */
    public void onQuery() {
    	this.onMrno();
    }
    /**
     * ����
     */
    public void onSave() {
    	
    	
    	TParm result = new TParm();
    	TParm update = new TParm();
    	TParm tradeParm = new TParm();//��������parm
    	TParm patSectionParm = new TParm();//����ʱ�̽���parm
    	TParm patSectionDParm = new TParm();//����ʱ����ϸ����parm
    	
    	if (getValue("EKT_BILL_TYPE").equals("")) {
			this.messageBox("֧����ʽ����Ϊ��");
			return ;
		}
    	
    	//���ҳ������
    	if(!checkData()){
    		return;
    	}
    	
    	
    	//�ж��ײ��Ƿ����add by sunqy 20140721
    	Date now = new Date();
    	DateFormat format = DateFormat.getDateInstance();
    	String dateNow = format.format(now).replaceAll("-", "/");
//    	String dateEnd = this.getValue("END_DATE").toString().replaceAll("-", "/").substring(0, 10);
    	String dateEnd = this.getValueString("END_DATE");
    	
    	if(!"".equals(dateEnd)&&dateEnd != null){
    		dateEnd = dateEnd.toString().substring(0, 10).replaceAll("-", "");
    	}
    	String dateStart=this.getValueString("START_DATE");
    	if(!"".equals(dateStart)&&dateStart != null){
    		dateStart = dateStart.toString().substring(0, 10).replaceAll("-", "");
    	}
    	/*if("".equals(dateEnd)||dateEnd == null){
    		messageBox("������ʧЧ����");
    		return;
    	}else{
    		dateEnd = dateEnd.toString().replaceAll("-", "/").substring(0, 10);
    	}*/
    	
    	/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    	try {
    		Date startdate = sdf.parse(dateNow);
    		Date enddate = sdf.parse(dateEnd);
    		int i = enddate.compareTo(startdate);
    		if (i <= 0) {
    			if(this.messageBox("�ײ���Ч��", "���ײͲ�����Ч���ڣ��Ƿ����", 2)!=0){
    				return;
    			}
    		}
    	} catch (ParseException e) {
    	}*/
    	//��ȡ���׺�
    	tradeNo = getTradeNo();
    	//System.out.println("tradeNo111111111="+tradeNo);
    	//��ȡʱ�������
    	SECTION_ID = getMaxSeq("ID","MEM_PAT_PACKAGE_SECTION","","","","");
    	//System.out.println("ʱ�̱�ţ�"+SECTION_ID);
    	//��ȡʱ����ϸ�����
//    	SECTION_D_ID = getMaxSeq("ID","MEM_PAT_PACKAGE_SECTION_D","","","","");
//    	System.out.println("��ϸ��ţ�"+SECTION_D_ID);
    	//��ȡҳ����Ϣ
    	tradeParm = getPatData();
    	tradeParm.addData("START_DATE", dateStart);
    	tradeParm.addData("END_DATE", dateEnd);
    	if(!oper){//ȫ�ֲ���-����֧�������쳣����
    		return;
    	}
    	
//    	System.out.println("ҳ����Ϣ��"+tradeParm);
    	//��ù�ѡʱ��parm
    	patSectionParm = getSectionParm(tradeParm);
//    	System.out.println("��ѡʱ��parm="+patSectionParm);
    	//��ȡʱ�̶�Ӧ����ϸparm+��̯�۸�
    	patSectionDParm = getSectionDParm(patSectionParm);
//    	System.out.println("patSectionDParm="+patSectionDParm);
    	
    	update.setData("TRADEDATA", tradeParm.getData());
    	update.setData("SECTIONDATA", patSectionParm.getData());
    	update.setData("SECTIONDDATA", patSectionDParm.getData());
    	if(parmEKT!=null){
    		update.setData("PARMEKT", parmEKT.getData());//ҽ�ƿ���Ϣ
    	}
    	TRadioButton cashPay = (TRadioButton) getComponent("gatherFlg1");
    	if(cashPay.isSelected()){
    		//add bby sunqy 20140710 �ж���δ�趨֧����ʽ�Ľ������
            TParm payTypeTParm = paymentTool.table.getParmValue();
        	//�ֽ��Ʊ������У���Ƿ����֧������΢�Ž��
    		TParm checkCashTypeParm=OPBTool.getInstance().checkCashTypeOther(payTypeTParm);
    		TParm payCashParm=null;
    		if(null!=checkCashTypeParm.getValue("WX_FLG")&&
    				checkCashTypeParm.getValue("WX_FLG").equals("Y")||null!=checkCashTypeParm.getValue("ZFB_FLG")&&
    				checkCashTypeParm.getValue("ZFB_FLG").equals("Y")){
    			Object resultParm = this.openDialog(
        	            "%ROOT%\\config\\bil\\BILPayTypeOnFeeTransactionNo.x", checkCashTypeParm, false);
    			if(null==resultParm){
    				return ;
    			}
    			payCashParm=(TParm)resultParm;
    		}
    		if(null!=payCashParm){
    			update.setData("payCashParm",payCashParm.getData());
    		}
    	}else{
    		if(this.onPayOther()){//δ�շ�
				return ;
			}
    		
    		if(parmEKT == null){
    			this.messageBox("���ȡҽ�ƿ���Ϣ");
    			return ;
    		}
    		if (!EKTIO.getInstance().ektSwitch()) {
    			messageBox_("ҽ�ƿ�����û������!");
    			return ;
    		}
    		TParm parm = new TParm();
    		parm.setData("READ_CARD", parmEKT.getData());
    		parm.setData("PAY_OTHER3",this.getValueDouble("PAY_OTHER3"));
    		parm.setData("PAY_OTHER4",this.getValueDouble("PAY_OTHER4"));
    		parm.setData("EXE_AMT",this.getValueDouble("EKT_TOT_AMT"));
    		parm.setData("MR_NO",this.getValueString("MR_NO"));
    		parm.setData("BUSINESS_TYPE","MEM");
    		parm.setData("CASE_NO",tradeNo);//���׺�
    		Object r =  this.openDialog("%ROOT%\\config\\ekt\\EKTChageOtherUI.x",parm);
    		if(r == null){
    			this.messageBox("ҽ�ƿ��ۿ�ȡ������ִ�б���");
    			return;
    		}
    		TParm rParm = (TParm) r;
    		if(rParm.getErrCode() < 0){
    			this.messageBox(rParm.getErrText());
    			return;
    		}else if(rParm.getValue("OP_TYPE").equals("2")){
    			return;
    		}else{
    			update.setData("ektSql", rParm.getData("ektSql"));
    		}
    		
    	}
    	
    	
    	result = TIOM_AppServer.executeAction("action.mem.MEMPackageSalesTimeSelAction",
				"onSave", update);
    	
    	String uSql =
    		" SELECT ID, ORDERSET_GROUP_NO, PACKAGE_CODE, SECTION_CODE FROM MEM_PAT_PACKAGE_SECTION_D WHERE  USED_FLG = 0 AND SETMAIN_FLG = 'Y' " +
    		" AND TRADE_NO = '"+tradeNo+"'";
//    	System.out.println("uSql====== "+uSql);
    	TParm uParm = new TParm(TJDODBTool.getInstance().select(uSql));
//    	System.out.println("uParm===== "+uParm);
//    	messageBox(""+uParm.getCount("ID"));
    	for (int i = 0; i < uParm.getCount("ID"); i++) {
    		uSql = 
    			" UPDATE MEM_PAT_PACKAGE_SECTION_D" +
    			" SET ORDERSET_ID = '" + uParm.getValue("ID", i) + "'" +
    			" WHERE TRADE_NO = '" + tradeNo + "'" +
    			" AND ORDERSET_GROUP_NO = '" + uParm.getValue("ORDERSET_GROUP_NO", i) + "'" +
    			" AND PACKAGE_CODE = '" + uParm.getValue("PACKAGE_CODE", i) + "'" +
    			" AND SECTION_CODE = '" + uParm.getValue("SECTION_CODE", i) + "'";
//    		System.out.println("uSql==asdasasdda=="+uSql);
    		TJDODBTool.getInstance().update(uSql);
		}
    	
    	if(result.getErrCode()<0){
    		this.messageBox("����ʧ�ܣ�"+result.getErrText());
    		return;
    	}else{
    		this.messageBox("����ɹ���");
    		
    		//add by sunqy 20140616  ----start----
    		boolean isDedug=true; //add by huangtt 20160505 ��־���
    		try {
			
    		String sql = "SELECT  A.GATHER_TYPE,B.CHN_DESC FROM BIL_GATHERTYPE_PAYTYPE A ,SYS_DICTIONARY B" +
    				" WHERE A.GATHER_TYPE= B.ID AND B.GROUP_ID = 'GATHER_TYPE'"; 
    		TParm payTypeParm = new TParm(TJDODBTool.getInstance().select(sql));
    		DecimalFormat df=new DecimalFormat("0.00");
    		HashMap<String, String> map = new HashMap<String, String>();
    		for (int i = 0; i < payTypeParm.getCount(); i++) {
				map.put(payTypeParm.getValue("GATHER_TYPE", i), payTypeParm.getValue("CHN_DESC", i));
			}
//    		map.put("C0", "�ֽ�");
//    		map.put("C1", "ˢ��");
//    		map.put("T0", "֧Ʊ");
//    		map.put("C4", "Ӧ�տ�");
//    		map.put("LPK", "��Ʒ��");
//    		map.put("XJZKQ", "�ֽ��ۿ�ȯ");
    		map.put("INS", "ҽ����");
    		TParm parm = new TParm();
    		parm.setData("TITLE", "TEXT",  "�ײͽ����վ�");
    		parm.setData("TYPE", "TEXT", "����"); //���
    		parm.setData("MR_NO", "TEXT", this.getValue("MR_NO")); // ������
    		 sql = "SELECT MEMO1,MEMO2,MEMO3,MEMO4,MEMO5,MEMO6,MEMO7,MEMO8,MEMO9,MEMO10,CARD_TYPE,WX_BUSINESS_NO,ZFB_BUSINESS_NO " +
    				"FROM MEM_PACKAGE_TRADE_M WHERE MR_NO = '"+ this.getValueString("MR_NO")+"' ORDER BY OPT_DATE DESC";
    		TParm sqlResult = new TParm(TJDODBTool.getInstance().select(sql));
    		sqlResult = sqlResult.getRow(0);
    		parm.setData("RecNO", "TEXT", SystemTool.getInstance().getNo("ALL", "EKT", "MEM_NO", "MEM_NO")); //Ʊ�ݺ�
    		parm.setData("DEPT_CODE", "TEXT", "");// �Ʊ�
    		parm.setData("PAT_NAME", "TEXT", this.getValue("PAT_NAME")); // ����
    		TTable table1 = paymentTool.table;//֧����ʽ���
			int tableRow = table1.getRowCount();
			int tableColumn0= table1.getColumnIndex("PAY_TYPE");//֧����ʽ��
			int tableColumn1 = table1.getColumnIndex("AMT");//֧�������
			String payType = "";
			for(int i=0;i<tableRow;i++){
				if(table1.getValueAt(i, tableColumn0)==null||table1.getValueAt(i, tableColumn0)==""){
					break;
				}
				if((table1.getValueAt(i, tableColumn0)!=null||table1.getValueAt(i, tableColumn0)!="") 
						&& (table1.getValueAt(i, tableColumn1)==null || table1.getValueAt(i, tableColumn1)=="")){
					break;
				}
				payType += ";"+map.get(table1.getValueAt(i,tableColumn0)) + ":" 
				+ df.format(Double.parseDouble((table1.getValueAt(i, tableColumn1))+""))+"Ԫ";// ���õ���֧����ʽ��֧�����ϲ�
			}
			payType = payType.substring(1, payType.length());
			String arr[] = payType.split(";");//���֧����ʽ��3�����������ʾ
			if(arr.length>2&&arr.length<4){
				parm.setData("PAY_TYPE2", "TEXT", arr[0]);
				parm.setData("PAY_TYPE3", "TEXT", arr[1]+";"+arr[2]);
			}
			//add by kangy 20171019 
			if(arr.length>=4){
				parm.setData("PAY_TYPE2", "TEXT", arr[0]+";"+arr[1]);
				parm.setData("PAY_TYPE3", "TEXT", arr[2]+";"+arr[3]);
			}
			if(arr.length<=2){
				parm.setData("PAY_TYPE", "TEXT", payType);// ֧����ʽ
			}
			if(this.getValueBoolean("gatherFlg2")){
				parm.setData("PAY_TYPE", "TEXT", "ҽ�ƿ�:"+this.getValueDouble("EKT_TOT_AMT")+"Ԫ");// ֧����ʽ
			}
			parm.setData("MONEY", "TEXT", (df.format(StringTool.round(this.getValueDouble("AR_AMT"), 2))+"Ԫ")); // ���
			parm.setData("CAPITAL", "TEXT", StringUtil.getInstance().numberToWord(this.getValueDouble("AR_AMT"))); // ��д���
			//String bankNo = "";
			//��ӿ����� add by huangjw 20141230
			//==start==modify by kangy 20171019 
			String memo2="";
			String memo9="";
			String memo10="";
			memo2=sqlResult.getValue("MEMO2");
			memo9=sqlResult.getValue("MEMO9");
			memo10=sqlResult.getValue("MEMO10");
//			String[] str;
//			String str1="";
//			int count=0;
//			for (int i = 1; i < 11; i++) {//sunqy 20140715
//				if(!"".equals(sqlResult.getValue("MEMO"+i))&&sqlResult.getValue("MEMO"+i)!=null){
//					if(!"".equals(cardtype)||cardtype!=null){
//						count++;
//						str=cardtype.split(";");
//						if(count<=str.length){
//							String cardsql="select CHN_DESC from sys_dictionary where id='"+str[count-1]+"' and group_id='SYS_CARDTYPE' ";
//							TParm Cardparm=new TParm(TJDODBTool.getInstance().select(cardsql));
//							str1=str1+Cardparm.getValue("CHN_DESC",0);
//						}
//					}
//					bankNo += str1+" "+sqlResult.getValue("MEMO"+i)+";";
//					str1="";
//				}
//			}
//			if(bankNo.length()>0){
//				bankNo = bankNo.substring(0, bankNo.length()-1);
//			}
			String cardtypeString="";
			if(!"".equals(memo2)&&!"#".equals(memo2)){
				String [] str=memo2.split("#");
				String [] str1=str[0].split(";");
//				String [] str2=str[1].split(";");
				String [] str2=null;
				if(str.length == 2){
					str2=str[1].split(";");
				}
				for(int m=0;m<str1.length;m++){
					String cardsql= "select CHN_DESC from sys_dictionary where id='"+str1[m]+"' and group_id='SYS_CARDTYPE'";
					TParm cardParm=new TParm(TJDODBTool.getInstance().select(cardsql));
					cardtypeString+=";"+cardParm.getValue("CHN_DESC",0)+" ";				
					if(str2 != null){
						if(m < str2.length ){
							cardtypeString=cardtypeString+str2[m]+" ";
						}
					}
					
				}
			}
			
			if(!"".equals(memo9)&&!"#".equals(memo9)){
				String [] str=memo9.split("#");
				String [] str1=str[0].split(";");
				String [] str2=null;
				if(str.length == 2){
					str2=str[1].split(";");
				}
				for(int m=0;m<str1.length;m++){
					String cardsql= "select CHN_DESC from sys_dictionary where id='"+str1[m]+"' and group_id='SYS_CARDTYPE'";
					TParm cardParm=new TParm(TJDODBTool.getInstance().select(cardsql));
					cardtypeString+=";"+cardParm.getValue("CHN_DESC",0)+" ";				
					if(str2 != null){
						if(m < str2.length ){
							cardtypeString+="��ע:"+str2[m]+" ";
						}
					}
					if(sqlResult.getValue("WX_BUSINESS_NO").length()>0){
						cardtypeString+=" ���׺�:"+sqlResult.getValue("WX_BUSINESS_NO");
					}
				}
			}
			if(!"".equals(memo10)&&!"#".equals(memo10)){
				String [] str=memo10.split("#");
				String [] str1=str[0].split(";");
//				String [] str2=str[1].split(";");
				String [] str2=null;
				if(str.length == 2){
					str2=str[1].split(";");
				}
				for(int m=0;m<str1.length;m++){
					String cardsql= "select CHN_DESC from sys_dictionary where id='"+str1[m]+"' and group_id='SYS_CARDTYPE'";
					TParm cardParm=new TParm(TJDODBTool.getInstance().select(cardsql));
					cardtypeString+=";"+cardParm.getValue("CHN_DESC",0)+" ";				
					if(str2 != null){
						if(m < str2.length ){
							cardtypeString+="��ע:"+str2[m]+" ";
						}
					}
					if(sqlResult.getValue("ZFB_BUSINESS_NO").length()>0){
						cardtypeString+=" ���׺�:"+sqlResult.getValue("ZFB_BUSINESS_NO");
					}
					
				}
			}
			if (cardtypeString.length()>1) {
				cardtypeString = cardtypeString.substring(1, cardtypeString.length());
			}
			if(cardtypeString.length()<=50){
				parm.setData("ACOUNT_NO1", "TEXT", cardtypeString);// ��Ʒ
			}else{
				parm.setData("ACOUNT_NO2","TEXT",cardtypeString.substring(0,50));
				parm.setData("ACOUNT_NO3","TEXT",cardtypeString.substring(50,cardtypeString.length()));
			}
			//==end==modify by kangy20171019 
			//===================================modify by huangjw 20141110 start
			String ctz_code=this.getText("PACKAGE_CODE");
			if(ctz_code.length()<=7){
				parm.setData("CTZ_CODE0", "TEXT", ctz_code);// ��Ʒ
			}else{
				parm.setData("CTZ_CODE","TEXT",ctz_code.substring(0,7));
				parm.setData("CTZ_CODE1","TEXT",ctz_code.substring(7,ctz_code.length()));
			}
			//===================================modify by huangjw 20141110 end
//			parm.setData("REASON", "TEXT", ((TComboBox)getComponent("DISCOUNT_REASON")).getSelectedName());// �ۿ�ԭ��
			parm.setData("REASON", "TEXT", ((TTextFormat)getComponent("DISCOUNT_REASON")).getText());// �ۿ�ԭ��
			String date = StringTool.getTimestamp(new Date()).toString().substring(
					0, 19).replace('-', '/');
			parm.setData("DATE", "TEXT", date);// ����
			parm.setData("OP_NAME", "TEXT", Operator.getID()); // �տ���
			parm.setData("RETURN", "TEXT", ""); // ��
			parm.setData("o", "TEXT", "");// ��
			parm.setData("COPY", "TEXT", ""); // ��ӡע��
			parm.setData("EXPLAIN", "TEXT", "˵�������վݲ�������ƾ֤�������ɵ��ͷ���ӡ��Ʊ"); //˵�� add by huangtt 201500924
			parm.setData("HOSP_NAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
	        		Operator.getHospitalCHNFullName() : "����ҽԺ");
	        parm.setData("HOSP_ENAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
	        		Operator.getHospitalENGFullName() : "ALL HOSPITALS");
//			this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMPackReceiptV45.jhw",parm, true);
			this.openPrintDialog(IReportTool.getInstance().getReportPath("MEMPackReceiptV45.jhw"),
					IReportTool.getInstance().getReportParm("MEMPackReceiptV45.class", parm));//����ϲ� 
			
			} catch (Exception e) {
				// TODO: handle exception
				if(isDedug){  
					System.out.println(" come in class: MEMPackageSalesTimeSelControl ��method ��onSave");
					e.printStackTrace();
				}
			}
    		//add by sunqy 20140616  ----end----
    		
    		tradeNo = "";//���׺��ÿ�
    		onClear();//���
    		
    	}
    	
    }
    
    /**
     * ���
     */
    public void onClear() {
    	callFunction("UI|MR_NO|setEnabled", true); // ������
        callFunction("UI|IPD_NO|setEnabled", true); // סԺ��
        //��ղ�����Ϣ
    	this.clearValue("MR_NO;IPD_NO;PAT_NAME;TEL_HOME;IDNO;SEX_CODE;BIRTH_DATE;AGE;ADM_DATE;CUSTOMER_SOURCE;CTZ_CODE1");
    	//�ײ���Ϣ
    	this.clearValue("PRICE_TYPE;PACKAGE_CODE;START_DATE;END_DATE");
    	//�ѹ��ײ��û�
        callFunction("UI|package|setEnabled", false);
    	
    	//����ۿ���Ϣ
    	this.clearValue("DISCOUNT_REASON;DISCOUNT_APPROVER;DISCOUNT_TYPE;" +
    			"ORIGINAL_PRICE;RETAIL_PRICE;AR_AMT;DESCRIPTION;INTRODUCER1;INTRODUCER2;INTRODUCER3;DISCOUNT_PRICE;LMP_DATE");
    	//add by huangtt 20180515
    	this.clearValue("EKT_BILL_TYPE;EKT_CURRENT_BALANCE;EKT_AMT;GIFT_CARD2;GIFT_CARD;NO_PAY_OTHER_ALL;PAY_OTHER4;PAY_OTHER3;NO_PAY_OTHER;EKT_TOT_AMT;EKT_PAY;EKT_PAY_RETURN");

		TRadioButton ektPay = (TRadioButton) getComponent("gatherFlg2");
		TRadioButton cashPay = (TRadioButton) getComponent("gatherFlg1");
		ektPay.setSelected(false);
		cashPay.setSelected(true);
    	this.onGatherChange(0);
    	
    	this.setValue("DISCOUNT_RATE", "1.00");//�����ۿ���Ϊ1.00
    	table.removeRowAll();
    	orderTable.removeRowAll();
    	//tTable.removeRowAll();
    	paymentTool.onClear();
    	TRadioButton defaultButton = (TRadioButton) this.getComponent("SALE_BUTTON");
    	defaultButton.setSelected(true);//add by sunqy 20140718��պ�Ĭ��ѡ���ײ�ԭʼ�۸�
//    	initTextFormat();
    	initTextFormat();
        this.setValue("PRICE_TYPE", "01");
        flage=false;//add by huangjw 2014111
        
    }
    
    /**
     * �ѹ��ײ�
     */
    public void onPackage() {
    	TParm parm = new TParm();
    	parm.setData("MR_NO", this.getValueString("MR_NO"));
    	this.openDialog("%ROOT%\\config\\mem\\MEMPackageSalesInfo.x", parm);
    	
    }

    /**
     * ��������
     *
     * @return String
     */
    private String getQuerySrc() { // �õ����Ҵ���
        String code = getText("PACKAGE_CODE");
        // �õ���������
        //String desc = getText("PACKAGE_DESC");
        String sb = "";
        if(null!=Operator.getRegion()&&!"".equals(Operator.getRegion()))
           // sb= " REGION_CODE='"+Operator.getRegion()+"' ";
        // ���������
        if (code != null && code.length() > 0)
            sb += "PACKAGE_CODE like '" + code + "%'";
//        if (desc != null && desc.length() > 0) {
//            if (sb.length() > 0)
//                sb += " AND ";
//            sb += "DEPT_CHN_DESC like '" + desc + "%'";
//        }
//        System.out.println("sb===="+sb);
        return sb;
    }
    /**
     * ʱ����ϸ�б���ʾ
     */
    public void showSectionDetail(String packageCode){
    	
    	//��Ч����-ʧЧ����
    	//setStartAndEndDate(packageCode);
    	//�����ײ�����
    	this.setValue("PACKAGE_CODE", packageCode);
//    	//��ȡʱ�̱�CODE���ֵ
//    	sectionIdNo = getMaxSeq("SECTION_CODE","SYS_PACKAGE_SECTION","","","","");
//    	//��ȡʱ�̱�SEQ���ֵ
//    	sectionSeqNo = getMaxSeq("SEQ","SYS_PACKAGE_SECTION","PACKAGE_CODE",packageCode,"","");
    	
    	queryByPriceType();
    }
    /**
     * �ײ����͸ı��¼� add by huangjw 20141027
     */
    public void queryByPriceType(){
    	orderTable.removeRowAll();//���ϸ������
    	String sql = "SELECT 'N' AS EXEC, A.SECTION_CODE,A.PACKAGE_CODE,A.SECTION_DESC,A.SECTION_ENG_DESC, " +
		" A.PY1,A.PY2,A.SEQ,A.DESCRIPTION,A.ORIGINAL_PRICE,C.SECTION_PRICE,A.OPT_DATE," +
		" A.OPT_USER,A.OPT_TERM,A.START_DATE,A.END_DATE, B.PACKAGE_ENG_DESC " +
		" FROM MEM_PACKAGE_SECTION A,MEM_PACKAGE B," +
		" ( SELECT PRICE_TYPE,PACKAGE_CODE,SECTION_CODE,DISCOUNT_RATE,SECTION_PRICE FROM MEM_PACKAGE_SECTION_PRICE "
				+ " WHERE PRICE_TYPE = '"+ this.getValue("PRICE_TYPE")+ "') C" +
		" WHERE A.PACKAGE_CODE = '"+packageCode+"'" +
		" AND A.PACKAGE_CODE = B.PACKAGE_CODE   ";
    	if(!"".equals(this.getValueString("PRICE_TYPE"))){
    		sql+=" AND A.PACKAGE_CODE=C.PACKAGE_CODE(+) ";
    		sql+=" AND A.SECTION_CODE=C.SECTION_CODE(+) ";
    		sql+=" ORDER BY A.PACKAGE_CODE,A.SEQ";
    	}
		//System.out.println("ʱ����ϸsql-"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		table.setParmValue(result);
		paymentTool.onClear();
    }
    /**
     * ʱ�̱����¼�-ҽ������ʾ
     */
    public void onMainTableClick(){
    	orderTable.removeRowAll();//���������
    	int selectedIndx=table.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	if(table.getSelectedColumn() == 0){//��һ��"ѡ"
    		table.acceptText();
    		int row = table.getSelectedRow();
    		TParm parm = table.getParmValue();
    		parm.setData("EXEC",row, parm.getValue("EXEC", row).equals("Y")?"N":"Y");
    		table.setParmValue(parm);
    		table.setSelectedRow(row);
    		table.acceptText();
    		//�۸�ͳ��
        	PriceStatistics();
    		
    	}

    	TParm tableparm=table.getParmValue();
    	if(!"".equals(tableparm.getValue("SECTION_CODE",selectedIndx))){
    		sectionCode = tableparm.getValue("SECTION_CODE",selectedIndx);
    	}
    	if(!"".equals(tableparm.getValue("PACKAGE_CODE",selectedIndx))){
    		packageCode = tableparm.getValue("PACKAGE_CODE",selectedIndx);
    	}
    	//System.out.println("sectionCode="+sectionCode+" packageCode="+packageCode);
    	
    	orderTable.setParmValue(packageDetail());
//    	onTableChangeValue();
    }
    
    /**
     * �ײ���ϸ modify by huangjw 20141117
     * @return
     */
	public TParm packageDetail() {
		String orderSql = "SELECT A.ID,A.SEQ,A.SECTION_DESC,A.ORDER_CODE,A.ORDER_DESC,A.ORDER_NUM,"
				+ " A.UNIT_CODE,A.UNIT_PRICE,B.RETAIL_PRICE,A.DESCRIPTION,A.OPT_DATE,A.OPT_USER,A.OPT_TERM,"
				+ " A.SECTION_CODE,A.PACKAGE_CODE,A.SETMAIN_FLG,A.ORDERSET_CODE,A.ORDERSET_GROUP_NO,A.HIDE_FLG"
				+ " FROM MEM_PACKAGE_SECTION_D A,"
				+ " (SELECT ID, PRICE_TYPE, PACKAGE_CODE, SECTION_CODE, DISCOUNT_RATE, RETAIL_PRICE"
				+ " FROM MEM_PACKAGE_SECTION_D_PRICE"
				+ " WHERE PRICE_TYPE = '"
				+ this.getValue("PRICE_TYPE")
				+ "') B"
				+ " WHERE A.PACKAGE_CODE = '"
				+ packageCode
				+ "' AND A.SECTION_CODE = '"
				+ sectionCode
				+ "' AND A.HIDE_FLG = 'N'";
		if (!"".equals(this.getValueString("PRICE_TYPE"))) {
			orderSql += " AND A.PACKAGE_CODE=B.PACKAGE_CODE(+) ";
			orderSql += " AND A.SECTION_CODE=B.SECTION_CODE(+) ";
			orderSql += " AND A.ID=B.ID(+) ";
			orderSql += " ORDER BY SEQ";
		}
		 //System.out.println("ҽ��sql="+orderSql);
		TParm result = new TParm(TJDODBTool.getInstance().select(orderSql));
		return result;
	}
    
    /**
	 * �õ�ҳ����Table����
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tag) {
		return (TTable) callFunction("UI|" + tag + "|getThis");
	}
	/**
	 * ȫѡ
	 */
	public void onSelAll() {
		TParm parm = table.getParmValue();
		int rowCount = parm.getCount();
		if(rowCount>0){
			for (int i = 0; i < rowCount; i++) {
				if (this.getTCheckBox("ALLCHECK").isSelected())
					parm.setData("EXEC", i, "Y");
				else
					parm.setData("EXEC", i, "N");
			}
			table.setParmValue(parm);
			//�۸�ͳ��
	    	PriceStatistics();
		}
		
	}
	/**
	 * �õ�TCheckBox
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

	/**
     * �õ�TextFormat����
     * @param tagName String
     * @return TTextFormat
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }
	/**
	 * ������Ч���ں�ʧЧ����
	 */
	public void setStartAndEndDate(String packageCode) {
		String sql = "SELECT START_DATE,END_DATE FROM MEM_PACKAGE WHERE PACKAGE_CODE ='"+packageCode+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()>0){
			if(result.getValue("START_DATE", 0).length()>0){
				this.setValue("START_DATE", result.getValue("START_DATE", 0).substring(0, 10).replace('-', '/'));
			}
			if(result.getValue("END_DATE", 0).length()>0){
				this.setValue("END_DATE", result.getValue("END_DATE", 0).substring(0, 10).replace('-', '/'));
			}
		}
	}
	/**
	 * �����ײͼ۸����� add by huangjw 20141027
	 */
	public void setPriceType(String packageCode){
		String sql = "SELECT B.PRICE_TYPE FROM MEM_PACKAGE_SECTION A, MEM_PACKAGE_SECTION_PRICE B " 
				+" WHERE A.PACKAGE_CODE=B.PACKAGE_CODE"
				+" AND A.SECTION_CODE=B.SECTION_CODE AND A.PACKAGE_CODE='"+packageCode+"'";
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()>0){
			this.setValue("PRICE_TYPE", result.getValue("PRICE_TYPE",0));
		}
	}
	/**
	 * �������Ƿ����-�Ⱦ�����
	 */
	public boolean existMrNo(){
		boolean flag = false;
		String mrNo = this.getValueString("MR_NO");
		if(mrNo.length()>0){
			flag = true;
		}else{
			this.messageBox("�������벡���ţ�");
			return flag;
		}
		
		return flag;
	}
	
	/**
	 * table��ѡ�����¼�
	 */
	public void onTableChangeValue() {
		table.acceptText();
//		int row = table.getSelectedRow();
//		TParm parm = table.getParmValue();
		
//		messageBox("s"+parm.getBoolean("EXEC", row)+row);
		int selectedIndx=table.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	//System.out.println("��ѡ��2��"+selectedIndx);
//    	PriceStatistics();
	}
	
	/**
	 * �������
	 */
	public boolean checkData(){
		boolean flg2 = true;
		if(MR_NO.length()<=0){
    		this.messageBox("�����Ų���Ϊ�գ�");
    		flg2 = false;
    		return flg2;
    	}
		TTextField packageCodeFlag = (TTextField)this.getComponent("MR_NO");
    	boolean oper = packageCodeFlag.isEnabled();
    	if(oper){
    		this.messageBox("�����Ų����ڣ�");
    		flg2 = false;
    		return flg2;
    	}
		//�����Ƿ�ѡ������
		TParm parm = table.getParmValue();
		int count = parm.getCount();
		if(count<=0){
			this.messageBox("û���ײ����ݣ��޷����棡");
			flg2 = false;
    		return flg2;
		}else{
			boolean flg = false;
			for (int i = 0; i < count; i++) {
				boolean exec = parm.getBoolean("EXEC", i);
				if(exec){
					flg = true;
				}
			}
			if(!flg){
				this.messageBox("û��ѡ��ʱ���ײͣ�");
				flg2 = false;
	    		return flg2;
			}
		}
		//֧����ʽ
//		String payType = this.getValue("PAY_TYPE").toString();
//		if("".equals(payType) || payType.length()<=0){
//			this.messageBox("֧����ʽ����Ϊ�գ�");
//			this.grabFocus("PAY_TYPE");
//			flg2 = false;
//    		return flg2;
//		}
		
		//֧����ʽΪҽ�ƿ��ж�ҽ�ƿ��Ƿ�����Ϣ
//		if("2".equals(payType)){
//			if(parmEKT==null){
//				this.messageBox("ҽ�ƿ���ϢΪ�գ�");
//				flg2 = false;
//	    		return flg2;
//			}
//		}
		
    	double arAmt = this.getValueDouble("AR_AMT");
    	
    	BigDecimal arAmount = new BigDecimal(arAmt).setScale(2, RoundingMode.HALF_UP);
    	
    	//��ע�͵�����Ϊ����ѵĶ�ͯ�ײ�    �����ۿ۽����0�ģ�Ҳ�ɱ���  modify by huangjw 20141106
    	/*if(arAmt <= 0){
    		this.messageBox("�����ۿۼ۲���Ϊ�գ�");
    		this.grabFocus("AR_AMT");
    		flg2 = false;
    		return flg2;
    	}*/
//    	double retailPrice = this.getValueDouble("RETAIL_PRICE");//�ײ����ۼ�
//    	if(arAmt>retailPrice){
//    		this.messageBox("�ۿۼ۲��ܴ����ײͼۣ�");
//    		return;
//    	}
    	double discountRate = this.getValueDouble("DISCOUNT_RATE");
    	if(discountRate<0){
    		this.messageBox("�ۿ۲���Ϊ����");
    		flg2 = false;
    		return flg2;
    	}else if(discountRate > 1){//Modified by lich
    		this.messageBox("�ۿ۲��ܴ���1��");
    		flg2 = false;
    		return flg2;
    	}else if(discountRate>=100){
    		this.messageBox("�ۿ۲��ܴ���100��");
    		flg2 = false;
    		return flg2;
    	}
    	/** duzhw del 0513
    	//add by huangtt 20140512
    	TParm parm1 = null;
		try {
			parm1 = paymentTool.getAmts();
		} catch (Exception e) {
			e.printStackTrace();
			messageBox(e.getMessage());
			flg2 = false;
    		return flg2;
		}  **/
    	TRadioButton cashPay = (TRadioButton) getComponent("gatherFlg1");
    	if(cashPay.isSelected()){
    		//add bby sunqy 20140710 �ж���δ�趨֧����ʽ�Ľ������
        	TParm payParm = paymentTool.table.getParmValue();
        	int payCount = payParm.getCount();
        	String payType = "";
        	double amt = 0.00;
        	for (int i = 0; i <= payCount; i++) {
        		payType = payParm.getValue("PAY_TYPE", i);
    			amt = payParm.getDouble("AMT", i);
    			if(amt > 0 && ("".equals(payType)||payType==null)){
    				this.messageBox("����δ�趨֧����ʽ�Ľ��,����д��");
    				flg2 = false;
    				return flg2;
    			}
    		}
        	//----start-------add by kangy 20160718------΢��֧����֧����Ҫ��ӽ��׺�
        	boolean flg3=paymentTool.onCheckPayType(payParm);
    	    if (flg3) {
    	    } else {
    			this.messageBox("�����������ͬ��֧����ʽ��");
    			flg2 = false;
    			return flg2;
    		}
    	    //----end-----add by kangy 20160718------΢��֧����֧����Ҫ��ӽ��׺�
    	}
    	
    	
    	
    	TRadioButton original = (TRadioButton)getComponent("ORIGINAL_BUTTON");
    	TRadioButton sale = (TRadioButton)getComponent("SALE_BUTTON");
    	String originalPrice = getValueString("ORIGINAL_PRICE").replaceAll(",", "");//ԭʼ�۸�
    	BigDecimal orgPrice = new BigDecimal(originalPrice).setScale(2, RoundingMode.HALF_UP);
    	String retailPrice = getValueString("RETAIL_PRICE").replaceAll(",", "");//�ײͼ۸�
    	BigDecimal retPrice = new BigDecimal(retailPrice).setScale(2, RoundingMode.HALF_UP);
    	String discountPrice = getValueString("DISCOUNT_PRICE").replaceAll(",", "");//�ۿ۽��
    	BigDecimal disPrice = new BigDecimal(discountPrice).setScale(2, RoundingMode.HALF_UP);
    	if(original.isSelected()){
			if(!arAmount.equals(orgPrice.subtract(disPrice))){
    			this.messageBox("�ۿ۽������!");
    			this.grabFocus("AR_AMT");
    			flg2 = false;
				return flg2;
    		}
    	}
    	if(sale.isSelected()){
			if(!arAmount.equals(retPrice.subtract(disPrice))){
    			this.messageBox("�ۿ۽������!");
    			this.grabFocus("AR_AMT");
    			flg2 = false;
				return flg2;
    		}
    	}
    	//Modified by lich
    	if(discountRate < 1.0){
//    		messageBox("Ŷ");
    		String discountReason = this.getValueString("DISCOUNT_REASON");
    		if(discountReason == null || "".equals(discountReason.trim())){
    			messageBox("����д�ۿ�ԭ��");
    			flg2 = false;
    			return flg2;
    		}
    	}
    	
    	return flg2;
	}
	/**
	 * �۸�ͳ��-��ѡʱ���ײ͵�"ѡ"��ȫѡʱ�����ײͼ۸�ͳ�Ʋ���
	 */
	public void PriceStatistics(){
		String originalPrice = "";
		String retailPrice = "";
		double allOriginalPrice = 0.00;
		double allRetailPrice = 0.00;
		table.acceptText();
		TParm parm = table.getParmValue();
		//System.out.println("parm="+parm);
		//messageBox(""+parm.getData("EXEC"));
		int count = parm.getCount();
		//System.out.println("count="+count);
		if(count>0){
			for (int i = 0; i < count; i++) {
				boolean exec = parm.getBoolean("EXEC", i);
				double originalPrice1 = parm.getDouble("ORIGINAL_PRICE", i);
				double retailPrice1 = parm.getDouble("SECTION_PRICE", i);
				if(exec){
					allOriginalPrice += originalPrice1;
					allRetailPrice += retailPrice1;
//					String sql="SELECT DISCOUNT_RATE FROM MEM_PACKAGE_SECTION_PRICE " +
//					"WHERE PACKAGE_CODE='"+parm.getValue("PACKAGE_CODE",i)+"' " +
//					"AND SECTION_CODE='"+parm.getValue("SECTION_CODE",i)+"' AND PRICE_TYPE='"+this.getValueString("PRICE_TYPE")+"'";
//					TParm resultParm=new TParm(TJDODBTool.getInstance().select(sql));
//					this.setValue("DISCOUNT_RATE", resultParm.getDouble("DISCOUNT_RATE",0));
					
				}
			}
		}
		DecimalFormat df = new DecimalFormat("###,##0.00");
        originalPrice = df.format(allOriginalPrice);
        retailPrice = df.format(allRetailPrice);
//		originalPrice = feeConversion(originalPrice);//�Ӷ��Ŵ���
//		retailPrice = feeConversion(retailPrice);//�Ӷ��Ŵ���
		this.setValue("ORIGINAL_PRICE", originalPrice);
		this.setValue("RETAIL_PRICE", retailPrice);
		
		
		//�����ۿۼ���ʵ�ս��
		queryFee();
		queryDiscount();//add by sunqy 20140704�ۿ۽�����
	}
	/**
     * ÿ��λ�Ӷ��Ŵ���
     */
    public String feeConversion(String fee){
    	String str1 = ""; 
    	String[] s = fee.split("\\.");//��"."���ָ�
    	
        str1 = new StringBuilder(s[0].toString()).reverse().toString();     //�Ƚ��ַ����ߵ�˳��  
        String str2 = "";  
        for(int i=0;i<str1.length();i++){  
            if(i*3+3>str1.length()){  
                str2 += str1.substring(i*3, str1.length());  
                break;  
            }  
            str2 += str1.substring(i*3, i*3+3)+",";  
        }  
        if(str2.endsWith(",")){  
            str2 = str2.substring(0, str2.length()-1);  
        }  
        //����ٽ�˳��ת����  
        String str3 = new StringBuilder(str2).reverse().toString();
        //����С��������
        StringBuffer str4 = new StringBuffer(str3);
        str4 = str4.append(".").append(s[1]);
    	return str4.toString();
    }
    /**
     * ȥ���Ŵ���
     */
    public String getFeeConversion(String fee) {
    	fee = fee.replaceAll(",", "");
    	return fee;
    	
    }
    /**
	 * �ײ����۽��׺�-ȡ�ý��׺�
	 * 
	 * @return String
	 */
    public String getTradeNo() {
    	if (tradeNo.length() <= 0) {
    		tradeNo = SystemTool.getInstance().getNo("ALL", "MEM", "TRADENO",
					"TRADENO");
//    		System.out.println("=====tradeNo=="+tradeNo);
		}
		return tradeNo;
    }
    /**
     * �õ����ı�� +1
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    public int getMaxSeq(String maxValue, String tableName,
                         String where1,String value1,String where2,String value2) {
    	String sql = "SELECT MAX("+maxValue+") AS "+maxValue+" FROM "+tableName+" WHERE 1=1 ";
    	if(where1.trim().length()>0){
    		sql += " AND "+where1+" ='"+value1+"'";
    	}
    	if(where2.trim().length()>0){
    		sql += " AND "+where2+" ='"+value2+"'";
    	}
    	//System.out.println("���ı��sql="+sql);
    	// ��������
        int max = 0;
    	//��ѯ������
    	TParm seqParm = new TParm(TJDODBTool.getInstance().select(sql));
    	String seq = seqParm.getValue(maxValue,0).toString().equals("")?"0"
    			:seqParm.getValue(maxValue,0).toString();
    	//System.out.println("seq="+seq);
    	int value = Integer.parseInt(seq);
    	//System.out.println("value="+value);
    	// �������ֵ
        if (max < value) {
            max = value;
        }
        // ���ż�1
        max++;
        //System.out.println("���ı�� +1="+max);
        return max;
        
    }
    /**
     * ��ȡҳ����Ϣ
     */
    public TParm getPatData() {
    	oper = true;//ȫ�ֲ���
    	TParm result = new TParm();
    	Timestamp date = StringTool.getTimestamp(new Date());
    	//������1
    	String introducer1 = this.getValueString("INTRODUCER1");
    	//������2
    	String introducer2 = this.getValueString("INTRODUCER2");
    	//������3
    	String introducer3 = this.getValueString("INTRODUCER3");
    	//�ۿ�ԭ��
    	String discountReasion = this.getValueString("DISCOUNT_REASON");
    	//�ۿ�������
    	String discountApprover = this.getValueString("DISCOUNT_APPROVER");
    	//�ۿ۷�ʽ
    	String discountType = this.getValueString("DISCOUNT_TYPE");
    	//�ۿ�
    	double discountRate = this.getValueDouble("DISCOUNT_RATE");
    	//�ײ���Ŀԭʼ�۸�
    	String originalPrice = this.getValueString("ORIGINAL_PRICE");
    	originalPrice = getFeeConversion(originalPrice);//ȥ���Ŵ���
    	//�ײ����ۼ۸�
    	String retailPrice = this.getValueString("RETAIL_PRICE");
    	retailPrice = getFeeConversion(retailPrice);//ȥ���Ŵ���
    	//�ۿ����ۼ۸�
    	double arAmt = new BigDecimal(this.getValueDouble("AR_AMT")).setScale(2, RoundingMode.HALF_UP).doubleValue();
    	//��ע
    	String description = this.getValueString("DESCRIPTION");
    	//��̯����
    	double retailPrice1=Double.parseDouble(retailPrice);
    	double rate = arAmt/retailPrice1;
    	//֧����ʽ
    	ektOper = this.getValueString("PAY_TYPE");
    	
    	result.addData("TRADE_NO", tradeNo);
    	result.addData("MR_NO", MR_NO);
    	result.addData("INTRODUCER1", introducer1);
    	result.addData("INTRODUCER2", introducer2);
    	result.addData("INTRODUCER3", introducer3);
    	result.addData("DISCOUNT_REASON", discountReasion);
    	result.addData("DISCOUNT_APPROVER", discountApprover);
    	result.addData("DISCOUNT_TYPE", discountType);
    	result.addData("DISCOUNT_RATE", discountRate);
    	result.addData("ORIGINAL_PRICE", originalPrice);
    	result.addData("RETAIL_PRICE", retailPrice);
    	result.addData("AR_AMT", arAmt);
    	result.addData("DESCRIPTION", description);
    	

//    	for (int i = 0; i < 10; i++) {
//    		result.addData("MEMO"+(i+1), "");
//		}
//    	for (int i = 0; i <  paymentTool.table.getRowCount(); i++) {
//    		if("C1".equals(paymentTool.table.getItemData(i, "PAY_TYPE"))){
//    			int row =Integer.parseInt(payParm.getValue("PAY_TYPE", i).substring(payParm.getValue("PAY_TYPE", i).length()-2)) ;
//    			result.setData("MEMO"+row,0, payParm.getData("REMARKS", i));
//
//    		}
//		}
    	
    	
    	
//    	int count = 0;
//		for (int j = 0; j < paymentTool.table.getRowCount(); j++) {//add by sunqy 20140714���ˢ��ʱ�ı�ע(��¼���п���)
//			if("C1".equals(paymentTool.table.getItemData(j, "PAY_TYPE"))){
//				result.addData("MEMO"+(j+1), paymentTool.table.getItemData(j, "REMARKS"));
//			}else{
//				result.addData("MEMO"+(j+1), "");
//			}
//			count ++ ;
//		}
//		for (int i = count; i < count+(10-paymentTool.table.getRowCount()); i++) {//add by sunqy 20140714��10���ֶθ�Ϊ���ַ���
//			result.addData("MEMO"+(i+1), "");
//		}
//		for (int i = 1; i < 11; i++) {//add by sunqy 20140714��10���ֶθ�Ϊ���ַ���
//			result.setData("MEMO"+i, "");
//		}
//		for (int j = 0; j < paymentTool.table.getRowCount(); j++) {//add by sunqy 20140714���ˢ��ʱ�ı�ע(��¼���п���)
//			if("C1".equals(paymentTool.table.getItemData(j, "PAY_TYPE"))){
//				result.setData("MEMO"+(j+1), paymentTool.table.getItemData(j, "REMARKS"));
//			}
//		}
    	result.addData("RATE", rate);
    	result.addData("OPT_USER", Operator.getID());
    	result.addData("OPT_DATE", date);
    	result.addData("OPT_TERM", Operator.getIP());
    	result.addData("PAY_TYPE", ektOper);
    	
//    	result.addData("PAY_TYPE01", 0.00);
//    	result.addData("PAY_TYPE02", 0.00);
//    	result.addData("PAY_TYPE03", 0.00);
//    	result.addData("PAY_TYPE04", 0.00);
//    	result.addData("PAY_TYPE05", 0.00);
//    	result.addData("PAY_TYPE06", 0.00);
//    	result.addData("PAY_TYPE07", 0.00);
//    	result.addData("PAY_TYPE08", 0.00);
//    	result.addData("PAY_TYPE09", 0.00);
//    	result.addData("PAY_TYPE10", 0.00);
    	
//    	if(payParm.getCount()>0){
//    		String cardType = "";//add by sunqy 20140728���ڼ�¼����������
//    		for (int i = 0; i < payParm.getCount(); i++) {
//    			if(i<9){
////    				result.setData("PAY_TYPE0"+(i+1), 0, payParm.getValue("AMT", i));
//    				result.setData(payParm.getValue("PAY_TYPE", i), 0, payParm.getValue("AMT", i));//modify by sunqy 20140717
//    			}else if(i==9){
//    				result.setData("PAY_TYPE10", 0, payParm.getValue("AMT", i));
//    			}
//    			if(!"".equals(payParm.getValue("CARD_TYPE", i))){
//    				cardType += ","+payParm.getValue("CARD_TYPE", i);
//    			}
//			}
//    		if(cardType.length()>0){
//    			cardType = cardType.substring(1, cardType.length());
//    		}
//    		result.addData("CARD_TYPE", cardType);
//    	}
    	
    	result.addData("BILL_TYPE", this.getValueString("EKT_BILL_TYPE"));
    	
    	//��֧����ʽ
    	TParm payParm = new TParm();
    	TRadioButton cashPay = (TRadioButton) getComponent("gatherFlg1");
    	if(cashPay.isSelected()){
    		result.addData("PAY_MEDICAL_CARD", 0);
    		try {
        		paymentTool.setAmt(arAmt);//add by sunqy 20140714 �ж��Ƿ�������С���ۿ����ۼ۸�
        		payParm =  paymentTool.getAmts();
        		
//    			System.out.println("payParm = "+payParm);
    		} catch (Exception e) {
    			e.printStackTrace();
    			messageBox(e.getMessage());
    			oper = false;
    		}
    	}else{
    		result.addData("PAY_MEDICAL_CARD", this.getValueString("EKT_TOT_AMT"));
    	}
    	

    	String cardType;//�������ͺͿ��Ŵ浽һ���ֶ��� modify by huangjw 20150104
		String cardTypeKey;
		double v;
		String key;
		TParm payCountParm = new TParm(TJDODBTool.getInstance().select("SELECT COUNT(PAYTYPE) PAYTYPE FROM BIL_GATHERTYPE_PAYTYPE"));
//		System.out.println("payCountParm======:"+payCountParm.getInt("PAYTYPE", 0));
		for(int j=1;j<=payCountParm.getInt("PAYTYPE", 0);j++){
			cardTypeKey="MEMO"+j;
			if(j<10){
				key="PAY_TYPE0"+j;
			}else{
				key="PAY_TYPE"+j;
			}
			cardType = "";
			v=0.00;
			for(int i=0;i<payParm.getCount("PAY_TYPE");i++){
				if(key.equals(payParm.getValue("PAY_TYPE", i))){
					v = payParm.getDouble("AMT", i);
					//if("PAY_TYPE02".equals(payParm.getValue("PAY_TYPE", i))){
						cardType = payParm.getValue("CARD_TYPE", i)+"#"+payParm.getValue("REMARKS",i);
					//}
					break;
				}
			}
			result.addData(key, v);
			result.addData(cardTypeKey, cardType);
		}
//		System.out.println("result----"+result);
    	return result;
    }
    /**
     * ��ù�ѡʱ��parm
     */
    public TParm getSectionParm(TParm parm) {
    	TParm sectionParm = new TParm();
    	TParm result = new TParm();
    	TParm tableParm = table.getParmValue();
		int count = tableParm.getCount();
		if(count>0){
			int j = 0;
			double sumArAmt = 0;
			for (int i = 0; i < count; i++) {
				boolean exec = tableParm.getBoolean("EXEC", i);
				
				if(exec){
					//��ӵ�sectionParm��
					sectionParm.addData("ID", SECTION_ID);
					sectionParm.addData("TRADE_NO", tradeNo);
					sectionParm.addData("PACKAGE_CODE", tableParm.getValue("PACKAGE_CODE", i));
					sectionParm.addData("SECTION_CODE", tableParm.getValue("SECTION_CODE", i));
					sectionParm.addData("MR_NO", MR_NO);
					sectionParm.addData("PACKAGE_DESC", this.getTextFormat("PACKAGE_CODE").getText());
					sectionParm.addData("PACKAGE_ENG_DESC", tableParm.getValue("PACKAGE_ENG_DESC", i));
					sectionParm.addData("SECTION_DESC", tableParm.getValue("SECTION_DESC", i));
					sectionParm.addData("ORIGINAL_PRICE", tableParm.getValue("ORIGINAL_PRICE", i));
					sectionParm.addData("SECTION_PRICE", tableParm.getValue("SECTION_PRICE", i));
					
					if(tableParm.getDouble("SECTION_PRICE", i) == 0){
						sectionParm.addData("AR_AMT", 0);

					}else{
						double arAmt = new BigDecimal(tableParm.getDouble("SECTION_PRICE", i)*Double.parseDouble(parm.getValue("RATE", 0))).setScale(2, RoundingMode.HALF_UP).doubleValue();
						sumArAmt = sumArAmt + arAmt;
						sectionParm.addData("AR_AMT", arAmt);

					}
//					sectionParm.addData("AR_AMT", tableParm.getDouble("SECTION_PRICE", i)*Double.parseDouble(parm.getValue("RATE", 0)));
					sectionParm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i));
					sectionParm.addData("USED_FLG", "0");//δʹ��
					sectionParm.addData("RATE", parm.getValue("RATE", 0));
					
					result.addRowData(sectionParm, j);
					j++;
					
					SECTION_ID++;
				}
			}
			
			double realAmt = new BigDecimal(parm.getDouble("AR_AMT", 0) - sumArAmt).setScale(2, RoundingMode.HALF_UP).doubleValue();
			
			result.setData("AR_AMT", 0, result.getDouble("AR_AMT", 0)+realAmt);
			
			
		}
    	return result;
    }
    /**
     * ��ȡʱ������ϸparm
     */
    public TParm getSectionDParm(TParm parm) {
    	TParm result = new TParm();
    	TParm uparm = new TParm();
    	TParm detailParm = new TParm();
    	TParm operParm = new TParm();
    	int count = parm.getCount("SECTION_CODE");
    	Timestamp date = StringTool.getTimestamp(new Date());
    	TParm orderSetParm = new TParm();
    	if(count>0){
    		int seq = 0;
    		int k = 0;
    		for (int i = 0; i < count; i++) {
    			//��ȡ��ʱ���µ�������ϸ����
    			String sectionCode = parm.getValue("SECTION_CODE", i);
    			String packageCode = parm.getValue("PACKAGE_CODE", i);
    			operParm.addData("SECTION_CODE", sectionCode);
    			operParm.addData("PACKAGE_CODE", packageCode);
    			operParm.addData("PRICE_TYPE", getValueString("PRICE_TYPE"));
    			
//    			detailParm = TIOM_AppServer.executeAction("action.mem.MEMPackageSalesTimeSelAction",
//    					"onQueryDetail", operParm);
    			detailParm = new TParm(TJDODBTool.getInstance().select(onQueryDetailSql(operParm)));
    			System.out.println("detailParm== "+detailParm);
    			operParm.removeData("SECTION_CODE");
    			operParm.removeData("PACKAGE_CODE");
    			if(detailParm.getCount()>0){
    				double arAmt = parm.getDouble("AR_AMT", i);//ʱ�̼�Ǯ
    				double rate = parm.getDouble("RATE", i); //����ۿ���
    				BigDecimal sumAmt = BigDecimal.ZERO;
    				double maxPrice = 0;
    				int maxInt = 0;
    				for (int j = 0; j < detailParm.getCount(); j++) {
    					uparm.addData("ID", detailParm.getValue("ID", j));
    					uparm.addData("TRADE_NO", tradeNo);
    					uparm.addData("PACKAGE_CODE", parm.getValue("PACKAGE_CODE", i));
    					uparm.addData("SECTION_CODE", parm.getValue("SECTION_CODE", i));
    					uparm.addData("PACKAGE_DESC", this.getTextFormat("PACKAGE_CODE").getText());
    					uparm.addData("SECTION_DESC", detailParm.getValue("SECTION_DESC", j));
    					uparm.addData("CASE_NO", "1");//--δ����
    					uparm.addData("MR_NO", MR_NO);
    					uparm.addData("SEQ", seq++);
    					uparm.addData("ORDER_CODE", detailParm.getValue("ORDER_CODE", j));
    					uparm.addData("ORDER_DESC", detailParm.getValue("ORDER_DESC", j));
    					uparm.addData("ORDER_NUM", detailParm.getValue("ORDER_NUM", j));
    					uparm.addData("UNIT_CODE", detailParm.getValue("UNIT_CODE", j));
    					uparm.addData("UNIT_PRICE", detailParm.getValue("UNIT_PRICE", j));
    					BigDecimal retailPrice = BigDecimal.ZERO;
    					if(detailParm.getDouble("RETAIL_PRICE", j) > 0){
    						 retailPrice = new BigDecimal(detailParm.getDouble("RETAIL_PRICE", j)*rate).setScale(2, RoundingMode.HALF_UP);
    					}
    					if(("Y".equals(detailParm.getValue("SETMAIN_FLG", j)) && "N".equals(detailParm.getValue("HIDE_FLG", j)))
    							|| ("N".equals(detailParm.getValue("SETMAIN_FLG", j)) && "N".equals(detailParm.getValue("HIDE_FLG", j)))
    									){
    						sumAmt = sumAmt.add(retailPrice);
//        					System.out.println(j+"--retailPrice--"+retailPrice.doubleValue());
//        					System.out.println(j+"--sumAmt--"+sumAmt.doubleValue());
    					}
    					
    					uparm.addData("RETAIL_PRICE", retailPrice.doubleValue());
    					if(maxPrice < retailPrice.doubleValue()){
    						maxPrice = retailPrice.doubleValue();
    						maxInt = k;
    					}
    					uparm.addData("DESCRIPTION", detailParm.getValue("DESCRIPTION", j));
    					uparm.addData("OPT_DATE", date);
    					uparm.addData("OPT_USER", Operator.getID());
    					uparm.addData("OPT_TERM", Operator.getIP());
    					uparm.addData("SETMAIN_FLG", detailParm.getValue("SETMAIN_FLG", j));
    					uparm.addData("ORDERSET_CODE", detailParm.getValue("ORDERSET_CODE", j));
    					uparm.addData("ORDERSET_GROUP_NO", detailParm.getValue("ORDERSET_GROUP_NO", j));
    					uparm.addData("HIDE_FLG", detailParm.getValue("HIDE_FLG", j));
    					uparm.addData("USED_FLG", "0");
    					//pangben 2015-9-2 ��Ӳ������ֶ�
    					uparm.addData("UN_NUM_FLG", detailParm.getValue("UN_NUM_FLG", j));
    					//add by lich ����Ӣ��ҽ���ֶ�  TRADE_ENG_DESC 20141010
    					uparm.addData("TRADE_ENG_DESC", parm.getValue("TRADE_ENG_DESC", i));
    					
    					if("Y".equals(detailParm.getValue("SETMAIN_FLG", j)) && "N".equals(detailParm.getValue("HIDE_FLG", j))){
    						orderSetParm.addRowData(uparm, k);
    					}
    					
    					result.addRowData(uparm, k);
    					k++;
    					//SECTION_D_ID++;
    				}
//    				System.out.println(i+"----"+parm.getRow(i));
//    				System.out.println("rate---"+rate);
//    				System.out.println("arAmt---"+arAmt);
//    				System.out.println("sumAmt---"+sumAmt.doubleValue());
    				double diffAmt = new BigDecimal(arAmt - sumAmt.doubleValue()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    				double price = new BigDecimal(result.getDouble("RETAIL_PRICE", maxInt)+diffAmt).setScale(2, RoundingMode.HALF_UP).doubleValue();
//    				System.out.println("diffAmt---"+diffAmt);
//    				System.out.println("price---"+price);
//    				System.out.println("maxInt---"+maxInt);
    				result.setData("RETAIL_PRICE", maxInt, price);
    				
    			}
    		}
    		
    	}
//    	System.out.println("result===="+result);
//    	System.out.println("orderSetParm---"+orderSetParm);
    	if(orderSetParm.getCount("SECTION_CODE") > 0){
    		for (int l = 0; l < orderSetParm.getCount("SECTION_CODE"); l++) {
				double sumAmt = 0;
				double maxAmt = 0;
				int row = 0;
				for (int m = 0; m < result.getCount("SECTION_CODE"); m++) {
					//ORDERSET_GROUP_NO PACKAGE_CODE SECTION_CODE
					if(orderSetParm.getValue("ORDERSET_GROUP_NO", l).equals(result.getValue("ORDERSET_GROUP_NO", m)) &&
							orderSetParm.getValue("PACKAGE_CODE", l).equals(result.getValue("PACKAGE_CODE", m)) &&
							orderSetParm.getValue("SECTION_CODE", l).equals(result.getValue("SECTION_CODE", m)) &&
							"N".equals(result.getValue("SETMAIN_FLG", m)) && "Y".equals(result.getValue("HIDE_FLG", m))
					){
						sumAmt = sumAmt + result.getDouble("RETAIL_PRICE", m);
						if(maxAmt < result.getDouble("RETAIL_PRICE", m)){
							maxAmt = result.getDouble("RETAIL_PRICE", m);
							row = m;
						}
					}	
				}
				double diffAmt = new BigDecimal(orderSetParm.getDouble("RETAIL_PRICE", l) - sumAmt).setScale(2, RoundingMode.HALF_UP).doubleValue();
				double price = new BigDecimal(maxAmt + diffAmt).setScale(2, RoundingMode.HALF_UP).doubleValue();
//				System.out.println("diffAmt----"+diffAmt);
//				System.out.println("price----"+price);
//				System.out.println("row----"+row);
				result.setData("RETAIL_PRICE", row, price);
				
			}
    	}
    	
    	return result;
    }
    
    /**
     * �����ۿۼ���ʵ�ս��
     * @throws Exception 
     */
    public void queryRate() throws Exception{
    	//У���ۿ۲��ܴ���1
    	double rate = this.getValueDouble("DISCOUNT_RATE");
    	if(rate>1){
    		this.messageBox("�ۿ��ʲ��ܴ���1��");
    		this.setValue("DISCOUNT_RATE", "1.00");
    		return;
    	}
    	queryFee();
    	queryDiscount();//add by sunqy 20140704
    }
    /**
     * ����ʵ�ս��
     */
    public void queryFee(){
//    	DecimalFormat df = new DecimalFormat("##0.00");
    	double rate = this.getValueDouble("DISCOUNT_RATE");
    	BigDecimal rt = new BigDecimal(rate).setScale(2, RoundingMode.HALF_UP);
    	//����ʵ�ս��ۿ����ۼ۸�
    	//����radiobutton ��ȷ���շѽ�� add by huangjw 20141106  start
    	String retailPrice = "";
    	if("Y".equals(this.getValueString("ORIGINAL_BUTTON"))){
    		retailPrice = this.getValueString("ORIGINAL_PRICE");
    	}else if("Y".equals(this.getValueString("SALE_BUTTON"))){
    		retailPrice = this.getValueString("RETAIL_PRICE");
    	}
    	//����radiobutton ��ȷ���շѽ�� add by huangjw 20141106 end 
    	double retailPrice2 = 0.00;
    	double arAmt = 0.00;//�ۿ����ۼ�
    	if(retailPrice.length()>0){
    		retailPrice = retailPrice.replaceAll(",", "");
    		//ת����double����
    		retailPrice2 = Double.parseDouble(retailPrice);
    		BigDecimal bd = new BigDecimal(retailPrice2).setScale(2, RoundingMode.HALF_UP);
    		//System.out.println("rate="+rate+" retailPrice2="+retailPrice2);
//    		arAmt = retailPrice2 * rate;
    		arAmt = bd.multiply(rt).doubleValue();
    		this.setValue("AR_AMT", arAmt);
    		this.setValue("EKT_TOT_AMT", arAmt); //add by huangtt 20180515
    		this.setValue("EKT_AMT", this.getValueDouble("EKT_CURRENT_BALANCE")-arAmt); //add by huangtt 20180515
    		paymentTool.setAmt(arAmt);//Ǯ���仯ʱ��ֵ
    		
    	}
    }
    
    /**
     * ��ҽ�ƿ�����
     */
    public void onEKTcard(){
    	//��ȡҽ�ƿ�
        parmEKT = EKTIO.getInstance().TXreadEKT();
        if (null == parmEKT || parmEKT.getErrCode() < 0 ||
            parmEKT.getValue("MR_NO").length() <= 0) {
            this.messageBox(parmEKT.getErrText());
            parmEKT = null;
            return;
        }
        
        //callFunction("UI|MR_NO|setEnabled", false); //�������ɱ༭
        this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
        //this.setValue("CARD_NO", parmEKT.getValue("CARD_NO"));
        //֧����ʽ��ֵ��ҽ�ƿ�֧��
        this.setValue("PAY_TYPE", 2);
        
        parmEKT.setData("OPT_USER", Operator.getID());
        parmEKT.setData("OPT_TERM", Operator.getIP());
        
        //add by huangtt 20180515
        setValue("EKT_CURRENT_BALANCE", parmEKT
				.getDouble("CURRENT_BALANCE"));
        double totAmt = getValueDouble("EKT_TOT_AMT");
		setValue("EKT_AMT", StringTool.round((parmEKT.getDouble("CURRENT_BALANCE") - totAmt), 2));
		TRadioButton ektPay = (TRadioButton) getComponent("gatherFlg2");
		TRadioButton cashPay = (TRadioButton) getComponent("gatherFlg1");
		ektPay.setSelected(true);
		cashPay.setSelected(false);
		this.onGatherChange(1);
		onMrno();
        
    }
    
    /**
     * �ײ�ԭʼ�۸�ѡ�� sunqy 20140704
     */
    public void onSelectOriginalPrice(){
    	if(flage == false){
    		flage = true;
    	}
    	double rate = this.getValueDouble("DISCOUNT_RATE");
    	double arAmt = Double.parseDouble(this.getValueString("ORIGINAL_PRICE").replaceAll(",", "")) * rate;
    	this.setValue("AR_AMT", arAmt);
    	this.setValue("EKT_TOT_AMT", arAmt); //add by huangtt 20180515
    	this.setValue("EKT_AMT", this.getValueDouble("EKT_CURRENT_BALANCE")-arAmt); //add by huangtt 20180515
    	discountPrice = Double.parseDouble(this.getValueString("ORIGINAL_PRICE").replaceAll(",", ""))-this.getValueDouble("AR_AMT");
    	this.setValue("DISCOUNT_PRICE", new DecimalFormat("######0.00").format(discountPrice));
    	
    	queryFee();//add by huangjw 20141106
    }
    
    /**
     * �ײ����ۼ۸�ѡ��sunqy 20140704
     */
    public void onSelectSalePrice(){
    	if(flage == true){
    		flage = false;
    	}
    	double rate = this.getValueDouble("DISCOUNT_RATE");
    	double arAmt = Double.parseDouble(this.getValueString("RETAIL_PRICE").replaceAll(",", "")) * rate;
    	this.setValue("AR_AMT", arAmt);
    	this.setValue("EKT_TOT_AMT", arAmt); //add by huangtt 20180515
    	this.setValue("EKT_AMT", this.getValueDouble("EKT_CURRENT_BALANCE")-arAmt); //add by huangtt 20180515
    	discountPrice = Double.parseDouble(this.getValueString("RETAIL_PRICE").replaceAll(",", ""))-this.getValueDouble("AR_AMT");
    	this.setValue("DISCOUNT_PRICE", new DecimalFormat("######0.00").format(discountPrice));
    	
    	queryFee();//add by huangjw 20141106
    }
    
    /**
     * �����ۿ۽�� sunqy 20140704
     */
    private void queryDiscount(){
    	DecimalFormat df = new DecimalFormat("######0.00");
    	
    	if(flage == true){
    		double rate = this.getValueDouble("DISCOUNT_RATE");
        	double arAmt = Double.parseDouble(this.getValueString("ORIGINAL_PRICE").replaceAll(",", "")) * rate;
        	this.setValue("AR_AMT", arAmt);
        	this.setValue("EKT_TOT_AMT", arAmt); //add by huangtt 20180515
        	this.setValue("EKT_AMT", this.getValueDouble("EKT_CURRENT_BALANCE")-arAmt); //add by huangtt 20180515
        	discountPrice = Double.parseDouble(this.getValueString("ORIGINAL_PRICE").replaceAll(",", ""))-this.getValueDouble("AR_AMT");
    		this.setValue("DISCOUNT_PRICE", df.format(discountPrice));
    	}
    	if(flage == false){
    		double rate = this.getValueDouble("DISCOUNT_RATE");
        	double arAmt = Double.parseDouble(this.getValueString("RETAIL_PRICE").replaceAll(",", "")) * rate;
        	this.setValue("AR_AMT", arAmt);
        	this.setValue("EKT_TOT_AMT", arAmt); //add by huangtt 20180515
        	this.setValue("EKT_AMT", this.getValueDouble("EKT_CURRENT_BALANCE")-arAmt); //add by huangtt 20180515
        	discountPrice = Double.parseDouble(this.getValueString("RETAIL_PRICE").replaceAll(",", ""))-this.getValueDouble("AR_AMT");
    		this.setValue("DISCOUNT_PRICE", df.format(discountPrice));
    	}
    }
    
    /**
     * �ۿ����ۼ۸�ı��¼�sunqy 20140704
     * @throws Exception
     */
    public void onChangeValue() throws Exception{
    	
    	DecimalFormat format = new DecimalFormat("0.00");
    	double amt = this.getValueDouble("AR_AMT");//�ۿ����ۼ۸�
    	this.setValue("EKT_TOT_AMT", amt); // add by huangtt 20180515
    	this.setValue("EKT_AMT", this.getValueDouble("EKT_CURRENT_BALANCE")-amt); //add by huangtt 20180515
    	paymentTool.onClear();
    	paymentTool.setAmt(amt);
    	if(flage){
    		double originalPrice = TypeTool.getDouble(getValue("ORIGINAL_PRICE").toString().replaceAll(",", ""));//�ײ���Ŀԭʼ�۸�
    		BigDecimal origimnal = new BigDecimal(originalPrice);
    		BigDecimal bigAmt = new BigDecimal(amt);
    		this.setValue("DISCOUNT_PRICE", format.format(origimnal.subtract(bigAmt)));//�ۿ۽��
    		this.setValue("DISCOUNT_RATE", bigAmt.divide(origimnal,4, BigDecimal.ROUND_HALF_UP).doubleValue());
    	}else{
    		double retailPrice = TypeTool.getDouble(getValue("RETAIL_PRICE").toString().replaceAll(",", ""));//�ײ����ۼ۸�
    		BigDecimal retail = new BigDecimal(retailPrice);
    		BigDecimal bigAmt = new BigDecimal(amt);
    		System.out.println("retail--"+retail.doubleValue());
    		System.out.println("bigAmt--"+bigAmt.doubleValue());
//    		System.out.println("bigAmt/retail--"+ bigAmt.divide(retail).doubleValue());
    		this.setValue("DISCOUNT_PRICE", format.format(retail.subtract(bigAmt)));
    		this.setValue("DISCOUNT_RATE", bigAmt.divide(retail,4, BigDecimal.ROUND_HALF_UP).doubleValue());
    	}
    	
    }
    
    /**
     * �ж��Ƿ�Ϊ���ڵ� add by sunqy 20140811
     * @param packageCode
     * @return
     */
	private boolean isLeaf(String packageCode){
		String sql = "SELECT SEQ FROM MEM_PACKAGE WHERE PARENT_PACKAGE_CODE = '"+packageCode+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()<=0){
			return true;
		}
		return false;
	}
	
	
	/**
	 * ��ѯ��ϸҽ��sql
	 */
	public String onQueryDetailSql(TParm parm) {
																				//add by lich ����Ӣ��ҽ���ֶ�  TRADE_ENG_DESC 20141010

		String sql = "SELECT A.ID,A.SEQ,A.SECTION_DESC,A.ORDER_CODE,A.ORDER_DESC,A.ORDER_NUM,A.TRADE_ENG_DESC," +
				" A.UNIT_CODE,A.UNIT_PRICE,B.RETAIL_PRICE,A.DESCRIPTION,A.OPT_DATE,A.OPT_USER,A.OPT_TERM," +
				" A.SECTION_CODE,A.PACKAGE_CODE,A.SETMAIN_FLG,A.ORDERSET_CODE,A.ORDERSET_GROUP_NO,A.HIDE_FLG,A.UN_NUM_FLG" +//====pangben 2015-9-2 ���UN_NUM_FLG�ֶ�
				" FROM MEM_PACKAGE_SECTION_D A, (SELECT ID,"+
                " PRICE_TYPE,"+
                " PACKAGE_CODE,"+
                " SECTION_CODE,"+
                " DISCOUNT_RATE,"+
                " RETAIL_PRICE "+
                " FROM MEM_PACKAGE_SECTION_D_PRICE "+
                " WHERE PRICE_TYPE = '"+parm.getValue("PRICE_TYPE", 0)+"') B " +
				" WHERE A.PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", 0)+"' " +
				" AND A.SECTION_CODE = '"+parm.getValue("SECTION_CODE", 0)+"'" +
				" AND A.PACKAGE_CODE = B.PACKAGE_CODE(+)" +
				" AND A.SECTION_CODE = B.SECTION_CODE(+)" +
				" AND A.ID = B.ID(+)" +
				//" AND B.PRICE_TYPE = '"+parm.getValue("PRICE_TYPE", 0)+"'" +
				" ORDER BY A.SEQ ";

		System.out.println("��ѯ��ϸҽ��sql="+sql);
		return sql;
	}
	/**
	 * �����¼
	 * add by huangjw 20141106
	 */
	public void onCaseHistory() {
		Object obj=null;
		if(!"".equals(this.getValueString("MR_NO"))){
			 obj=this.openDialog("%ROOT%\\config\\opd\\OPDCaseHistory.x",this.getValueString("MR_NO"));
		}else{
			this.messageBox("�����벡����");
			this.grabFocus("MR_NO");
		}
		if(obj==null)
			return;
		if (!(obj instanceof TParm)) {
			return;
		}
		TParm parm=(TParm)obj;
		TParm newParm=(TParm) parm.getData("DIAG");
		String sql="SELECT ADM_DATE FROM REG_PATADM WHERE CASE_NO='"+newParm.getValue("CASE_NO",0)+"'";
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("ADM_DATE", result.getTimestamp("ADM_DATE",0));
	}
	
	public void onGatherChange(int t){

		boolean b = false;
		String lockRow = "";


		switch (t) {
		case 0:
			clearValue("PAY_OTHER4;PAY_OTHER3;EKT_PAY;");
			setValue("EKT_BILL_TYPE", "C");
			double amt = getValueDouble("AR_AMT");
			paymentTool.setAmt(amt);
			break;
		case 1:
			b = true;
			lockRow = "0";
			setValue("EKT_BILL_TYPE", "E");
			paymentTool.onClear();
			break;
		}

		TNumberTextField payOther4 = (TNumberTextField) getComponent("PAY_OTHER4");
		TNumberTextField payOther3 = (TNumberTextField) getComponent("PAY_OTHER3");
		TNumberTextField pay = (TNumberTextField) getComponent("EKT_PAY");

		payOther4.setEnabled(b);
		payOther3.setEnabled(b);
		pay.setEnabled(b);

		paymentTool.table.setLockRows(lockRow);
	}
	public void onPayOther3(){	
		double payOther3 = getValueDouble("PAY_OTHER3");
		double payOther4 = 0;
		double payOtherTop3 = getValueDouble("GIFT_CARD");
		double payOtherTop4 = getValueDouble("GIFT_CARD2");
		double arAmt = getValueDouble("EKT_TOT_AMT");
		if(getValueDouble("PAY_OTHER4") == 0){
			payOther4 = arAmt - payOther3;
			setValue("PAY_OTHER4", df.format(payOther4) );
		}
		TParm result = EKTpreDebtTool.getInstance().checkPayOther(payOther3, payOther4, arAmt, payOtherTop3, payOtherTop4);
		if(result.getErrCode() == -3){
			setValue("PAY_OTHER4", df.format(payOtherTop4) );
		}
		onPayOther();
	}

	public void onPayOther4(){		
		double payOther3 = 0;
		double payOther4 = getValueDouble("PAY_OTHER4");
		double payOtherTop3 = getValueDouble("GIFT_CARD");
		double payOtherTop4 = getValueDouble("GIFT_CARD2");
		double arAmt = getValueDouble("EKT_TOT_AMT");
		if(getValueDouble("PAY_OTHER3") == 0){
			payOther3 = arAmt - payOther4;
			setValue("PAY_OTHER3", df.format(payOther3) );
		}
		TParm result = EKTpreDebtTool.getInstance().checkPayOther(payOther3, payOther4, arAmt, payOtherTop3, payOtherTop4);
		if(result.getErrCode() == -2){
			setValue("PAY_OTHER3", df.format(payOtherTop3));
		}
		onPayOther();
	}

	public boolean onPayOther(){
		double payOther3 = getValueDouble("PAY_OTHER3");
		double payOther4 = getValueDouble("PAY_OTHER4");
		double payOtherTop3 = getValueDouble("GIFT_CARD");
		double payOtherTop4 = getValueDouble("GIFT_CARD2");
		double payCashTop = getValueDouble("NO_PAY_OTHER_ALL");
		double arAmt = getValueDouble("EKT_TOT_AMT");
		double payCash = Double.parseDouble(df.format(arAmt - payOther3 - payOther4));
		TParm result = EKTpreDebtTool.getInstance().checkPayOther(payOther3, payOther4, arAmt, payOtherTop3, payOtherTop4);
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			setValue("PAY_OTHER3", 0);
			setValue("PAY_OTHER4", 0);
			return true;
		}
		if(payCash > payCashTop){
			messageBox("�ֽ���");
			System.out.println(pat.getMrNo()+"-----payCash===="+payCash+"-----payCashTop==="+payCashTop);
			setValue("PAY_OTHER3", 0);
			setValue("PAY_OTHER4", 0);
			return true;
		}
		setValue("NO_PAY_OTHER", payCash);
		return false;
	}
	
	/**
	 * ������س��¼� PAY
	 */
	public void onPay() {
		// �ۿ۽��
		double pay = getValueDouble("EKT_PAY");
		// �ۿ۽��
		double arAmt = getValueDouble("EKT_TOT_AMT");
		if (pay - arAmt < 0 || pay == 0) {
			this.messageBox("����!");
			return;
		}
		// ��ֵ
		callFunction("UI|EKT_PAY_RETURN|setValue", StringTool.round((pay - arAmt),
				2));
		// this.grabFocus("CHARGE");
	}
}
