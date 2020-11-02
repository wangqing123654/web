package com.javahis.ui.opd;

import java.awt.Component;
import java.sql.Timestamp;

import jdo.odo.CommonPackChn;
import jdo.odo.CommonPackDiag;
import jdo.odo.CommonPackExa;
import jdo.odo.CommonPackMain;
import jdo.odo.CommonPackOrder;
import jdo.odo.ODO;
import jdo.opd.OPDSysParmTool;
import jdo.pha.PhaBaseTool;
import jdo.sys.Operator;
import jdo.sys.SYSRuleTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

/**
 *
 * <p>
 * Title: ����ҽ������վ��������
 * </p>
 *
 * <p>
 * Description:����ҽ������վ��������
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 20090429
 * @version 1.0
 */
public class OdoCommonPackCotrol extends TControl {

	// �Ʊ���ҽʦ���࣬�Ʊ��ҽʦ����
	private String deptOrDr, code,desc, DEPT = "1";
	// ��ϡ����ü�����������ҩ����ҩTABLE
	private TTable tableDiag, tableMed, tableChn, tableMain,tableExa;
	// ��ҽ�ؼ���
	private TComboBox rxNo;
	// ����
	private String DEPT_LABEL = "����";
	// ҽʦ
	private String DR_LABEL = "ҽʦ";
	// ������ϣ����ã�ҩƷ��TDS����
	private CommonPackMain main;
	// ��ҽ����
	private CommonPackChn chn;
	// ����ҩ����
	private CommonPackOrder order;
	// ��϶���
	private CommonPackDiag diag;
	// ���������
	private CommonPackExa exa;
	// �������ҽ���
	private String wc;
	// Ĭ����ҩ����
	private double dctMediQty = 0.0;
	// Ĭ����ҩ����
	private double dctTakeDays = 0;
	// ����TABLE��
	private String tableName;
	// ���ܲ�����ODO����
	private ODO odo;
	// �����¼ѡ����
	private int clickMainRow;
	
	public static String TREE = "Tree";
    TTreeNode treeRoot;
    private TParm packParm;
    private int selRow=-1;
    
    private String updateFlg="";
    /**
     * �������ݷ���datastore���ڶ��������ݹ���
     */
    TDataStore treeDataStore = new TDataStore();
    /**
     * ��Ź�����𹤾�
     */
    SYSRuleTool ruleTool;
    TTreeNode node;
    
  //��ʼ��SQL
	private String INIT_SQL;


	/**
	 * ��ʼ��
	 */
	public void onInit() {
		// ��ʼ��
		super.onInit();
		// ��ʼ�����
		initParameter();
		// ��ʼ���¼�
		initEvent();
		// ��ʼ������
		onClear();
		
		INIT_SQL="SELECT * FROM OPD_PACK_MAIN WHERE DEPT_OR_DR='"+deptOrDr+"' AND DEPTORDR_CODE='"+code+"' ";
		
		// ��ʼ����
        onInitSelectTree();
        

		// ��ʼ������
		initData();
		
		callFunction("UI|PARENT_PACK_CODE|setEnabled", false);
        callFunction("UI|PARENT_PACK_DESC|setEnabled", false);
		
		
		
	}
	
	/**
     * ��ʼ����
     */
    public void onInitSelectTree() {
        // �õ�����
        treeRoot = (TTreeNode) callMessage("UI|" + TREE + "|getRoot");
        if (treeRoot == null)
            return;
        // �����ڵ����������ʾ
        treeRoot.setText(desc);
        // �����ڵ㸳tag
        treeRoot.setType("Root");
        // ���ø��ڵ��id
        treeRoot.setID("");
        // ������нڵ������
        treeRoot.removeAllChildren();
        
        TParm parentParm = new TParm();
        TParm parm = new TParm(TJDODBTool.getInstance().select(INIT_SQL+" ORDER BY PACK_CODE,SEQ"));

        for (int i = 0; i < parm.getCount(); i++) {
			if(parm.getValue("PARENT_PACK_CODE", i).length() == 0){
				parentParm.addRowData(parm, i);
				parentParm.addData("CHILD", "");
				parentParm.addData("TYPE", "PARENT");
			}
		}
        parentParm.setCount(parentParm.getCount("PACK_CODE"));
        for (int i = 0; i < parentParm.getCount(); i++) {
        	TParm childParm = new TParm();
        	String parentPackCode = parentParm.getValue("PACK_CODE", i);
        	for (int j = 0; j < parm.getCount(); j++) {
        		if(parentPackCode.equals(parm.getValue("PARENT_PACK_CODE", j))){       			
        			childParm.addRowData(parm, j);
        			childParm.addData("TYPE", "CHILD");
        		}
				
			}
        	childParm.setCount(childParm.getCount("PACK_CODE"));
        	parentParm.setData("CHILD", i, childParm.getData());
			
		}

        downloadRootTree(treeRoot,parentParm);
        
        // ���������ʼ������
        callMessage("UI|" + TREE + "|update");
    }
	
	/**
     * ����������
     * @param parentNode TTreeNode
     * @param parm TParm
     */
    public void downloadRootTree(TTreeNode parentNode,TParm parm){
    	 if(parentNode == null)
             return;
         int count = parm.getCount();
         for(int i = 0;i < count;i++){
        	 String id = parm.getValue("PACK_CODE", i);       	 
        	 String type =parm.getValue("TYPE", i);
        	 String value = parm.getValue("PARENT_PACK_CODE", i);
        	 String name = parm.getValue("PACK_DESC", i);
        	 if(value.length() == 0){
        		 if(id.length() == 3){
        			 name ="("+id+")"+ parm.getValue("PACK_DESC", i);
        		 }
        		  
        	 }
        	 
        	 TParm child = parm.getParm("CHILD",i);
        	 TTreeNode node = new TTreeNode(name,type);
        	 node.setID(id);
        	 node.setValue(value);
        	 parentNode.add(node);
        	 downloadRootTree(node,child);
        	 
         }
       
    }
    
    /**
	 * ds����code�����¼�
	 * @param code String :PACK_CODE
	 */
	public void filtDs(String code){

//		String filterString="PACK_CODE='" +code+"'";
//		dsMain.setFilter(filterString);
//		dsMain.filter();
		
		packParm = new TParm(TJDODBTool.getInstance().select(INIT_SQL+" AND PACK_CODE='" +code+"'"));

	}

    
    /**
     * �����
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        node = (TTreeNode) parm;
        if (node == null)
            return;
        if ("Root".equals(node.getType())) {        	
        	onInitSelectTree();
        	onClear();        	
            return;
        }
        
        if ("PARENT".equals(node.getType())) {
        	if(node.getID().length()==3){
        		onClear();
            	this.setValue("PACK_CODE", node.getID());
//            	this.setValue("PACK_DESC", node.getText());
            	this.setValue("PACK_DESC", getDesc(node.getID()));
            	this.setValue("PARENT_PACK_CODE", node.getValue());
                callFunction("UI|PARENT_PACK_CODE|setEnabled", false);
                callFunction("UI|PARENT_PACK_DESC|setEnabled", false);
                return;
        	}else{
				callFunction("UI|new|setEnabled", false);
				callFunction("UI|PARENT_PACK_CODE|setEnabled", true);
				callFunction("UI|PARENT_PACK_DESC|setEnabled", true);
				String id = node.getID();
				filtDs(id);
				this.setValue("PACK_CODE", id);

				this.setValue("PACK_DESC", packParm.getValue("PACK_DESC", 0));
				this.setValue("OPD_FIT_FLG", packParm
						.getValue("OPD_FIT_FLG", 0));
				this.setValue("EMG_FIT_FLG", packParm
						.getValue("EMG_FIT_FLG", 0));
				this.setValue("SPCFYDEPT", packParm.getValue("SPCFYDEPT", 0));
				this.setValue("PARENT_PACK_CODE", packParm.getValue(
						"PARENT_PACK_CODE", 0));
				this.setValue("PARENT_PACK_DESC", getDesc(packParm.getValue(
						"PARENT_PACK_CODE", 0)));

				TParm parm1 = new TParm();
				parm1.setData("SUBJ_TEXT", packParm.getValue("SUBJ_TEXT", 0));
				parm1.setData("OBJ_TEXT", packParm.getValue("OBJ_TEXT", 0));
				parm1.setData("PHYSEXAM_REC", packParm.getValue("PHYSEXAM_REC",
						0));
				parm1.setData("PROPOSAL", packParm.getValue("PROPOSAL", 0));
				onClickMainTable(id, parm1);
				updateFlg = "update";

				return;
        	}
        	
        	
        }
        
        if ("CHILD".equals(node.getType())) {
        	 callFunction("UI|new|setEnabled", false);
        	 callFunction("UI|PARENT_PACK_CODE|setEnabled", true);
             callFunction("UI|PARENT_PACK_DESC|setEnabled", true);
        	 String id = node.getID();
        	 filtDs(id);
             this.setValue("PACK_CODE", id);
            
             this.setValue("PACK_DESC", packParm.getValue("PACK_DESC", 0));
             this.setValue("OPD_FIT_FLG", packParm.getValue("OPD_FIT_FLG", 0));
             this.setValue("EMG_FIT_FLG", packParm.getValue("EMG_FIT_FLG", 0));
             this.setValue("SPCFYDEPT", packParm.getValue("SPCFYDEPT", 0));
             this.setValue("PARENT_PACK_CODE", packParm.getValue("PARENT_PACK_CODE", 0));
             this.setValue("PARENT_PACK_DESC", getDesc(packParm.getValue("PARENT_PACK_CODE", 0)));

             TParm parm1 = new TParm();
             parm1.setData("SUBJ_TEXT",  packParm.getValue("SUBJ_TEXT", 0));
             parm1.setData("OBJ_TEXT",  packParm.getValue("OBJ_TEXT", 0));
             parm1.setData("PHYSEXAM_REC",  packParm.getValue("PHYSEXAM_REC", 0));
             parm1.setData("PROPOSAL",  packParm.getValue("PROPOSAL", 0));
             onClickMainTable(id,parm1);
             updateFlg="update";

            return;
         }


    }
    
    public String getDesc(String id){
    	TParm parm = new TParm(TJDODBTool.getInstance().select(INIT_SQL+" AND PACK_CODE='" +id+"'"));
    	return parm.getValue("PACK_DESC", 0);
    }
	

	/**
	 * ��ʼ�����
	 */
	public void initParameter() {
		Object obj = this.getParameter();
		if (obj == null) {
			this.messageBox_("�޳�ʼ������");
			return;
		}
		if(obj instanceof String){
			String inParam = (String)obj;
			//this.messageBox("----inParam----"+inParam);
			deptOrDr=inParam;
			if(DEPT.equalsIgnoreCase(deptOrDr)){
				code=Operator.getDept();
				desc = getDeptDesc(code);
			}
			else{
				code=Operator.getID();
				desc = getDrDesc(code);
			}
			return;
		}
		if(obj instanceof TParm){
			
			deptOrDr="2";
			code=Operator.getID();
			TParm parm=(TParm)obj;
			//this.messageBox("----obj----"+parm);
			
			odo=(ODO)parm.getData("ODO");
		}


	}

	/**
	 * ��ʼ���ؼ������¼�
	 */
	public void initEvent() {
//		// ����TABLE
//		tableMain = (TTable) this.getComponent("TABLE_MAIN");
//		tableMain.addEventListener("TABLE_MAIN->"+ TTableEvent.CHANGE_VALUE, this,
//		"onMainChangeValue");
//		tableMain.setDataStore(main);
		
		//����ѡ������Ŀ
        addEventListener(TREE + "->" + TTreeEvent.CLICKED, "onTreeClicked");
		
		// ���
		tableDiag = (TTable) this.getComponent("TABLE_DIAG");
		// //ֵ�ı��¼�
		// tableDiag.addEventListener(TTableEvent.CHANGE_VALUE, this,
		// "onDiagTableChangeValue");
		// ����Ϲ�ѡ�¼�
		tableDiag.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onDiagMain");
		// ������
		tableDiag.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onDiagCreateEditComponent");

		tableMed = (TTable) this.getComponent("TABLE_MED");
		// ֵ�ı��¼�
		tableMed.addEventListener(TTableEvent.CHANGE_VALUE, this,
				"onMedTableChangeValue");
		// ���ҩƷ
		tableMed.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onMedCreateEditComponent");
		// ����Ϲ�ѡ�¼�
		tableMed.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onLinkMain");


		tableExa = (TTable) this.getComponent("TABLE_EXA");
		// ���ҩƷ
		tableExa.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onExaCreateEditComponent");



		tableChn = (TTable) this.getComponent("TABLE_CHN");
		// ֵ�ı��¼�
		tableChn.addEventListener("TABLE_CHN->"+TTableEvent.CHANGE_VALUE, this,
				"onChnTableChangeValue");
		// �����ҩ
		tableChn.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onChnCreateEditComponent");

		rxNo=(TComboBox)this.getComponent("REXP_NO");
	}

	/**
	 * ��ʼ������
	 */
	public void initData() {

//		main = new CommonPackMain(this.deptOrDr, this.code);
//		if(odo==null){
//			if (!main.onQuery()) {
//				this.messageBox_("��ʼ��ʧ��");
//				return;
//			}
//			main.insertRow(-1);
//		}else{
//			main.initOdo(odo);
//		}
//
//		tableMain.setDataStore(main);
//		tableMain.setDSValue();
		
		

		TParm sysparm = OPDSysParmTool.getInstance().getSysParm();
		dctMediQty = sysparm.getDouble("DCT_TAKE_QTY", 0);
		dctTakeDays = sysparm.getInt("DCT_TAKE_DAYS", 0);
		this.setValue("CHN_FREQ_CODE", OPDSysParmTool.getInstance()
				.getGfreqCode());
		this.setValue("CHN_ROUTE_CODE", OPDSysParmTool.getInstance()
				.getGRouteCode());
		this.setValue("DCTAGENT_CODE", OPDSysParmTool.getInstance()
				.getGdctAgent());
	}

	/**
	 * ��TABLE����¼�,Ϊ�����ؼ���ֵ
	 */
	public void onClickMainTable(String packCode,TParm parm) {

//		int row = tableMain.getSelectedRow();
//		clickMainRow=row;
//		String packCode = main.getItemString(row, "PACK_CODE");
		if (StringUtil.isNullString(packCode)) {
			this.messageBox_("ȡ�����ݴ���");
			return;
		}
		order = new CommonPackOrder(this.deptOrDr, this.code, packCode);
		if(odo==null){
			if (!order.onQuery()) {
				this.messageBox_("��ѯ����ҩʧ��");
				return;
			}
			order.insertRow(-1);
		}else{
			if(!order.initOdo(odo)){
				this.messageBox_("��ѯ����ҩʧ��");
				return;
			}
		}

		tableMed.setDataStore(order);
		tableMed.setDSValue();

		exa = new CommonPackExa(this.deptOrDr, this.code, packCode);
		if(odo==null){
			if (!exa.onQuery()) {
				this.messageBox_("��ѯ����ҩʧ��");
				return;
			}
			exa.insertRow();
		}else{
			if(!exa.initOdo(odo)){
				this.messageBox_("��ѯ����ҩʧ��");
				return;
			}
		}
		tableExa.setDataStore(exa);
		tableExa.setDSValue();
		// ���
		diag = new CommonPackDiag(this.deptOrDr, this.code, packCode);
		if(odo==null){
			if (!diag.onQuery()) {
				this.messageBox_("��ѯ���ʧ��");
				return;
			}
			diag.insertRow();
		}else{
			if(!diag.initOdo(odo)){
				this.messageBox_("��ѯ���ʧ��");
				return;
			}
		}

		tableDiag.setDataStore(diag);
		tableDiag.setDSValue();

		chn = new CommonPackChn(this.deptOrDr, this.code, packCode);
		if(odo==null){
			if (!chn.onQuery()) {
				this.messageBox_("��ѯ��ҩʧ��");
				return;
			}
		}else{
			if(!chn.initOdo(odo)){
				this.messageBox_("��ѯ���ʧ��");
				return;
			}
		}
		initChnPanel();

//		this.setValue("PACK_CODE", main.getItemString(row, "PACK_CODE"));
//		this.setValue("PACK_DESC", main.getItemString(row, "PACK_DESC"));
		this.setValue("SUBJ_TEXT", parm.getValue("SUBJ_TEXT"));
		this.setValue("OBJ_TEXT", parm.getValue("OBJ_TEXT"));
		this.setValue("PHYSEXAM_REC", parm.getValue("PHYSEXAM_REC"));
		this.setValue("PROPOSAL", parm.getValue("PROPOSAL"));  //add by huangtt 20150226 ��ӽ���

	}
	/**
	 * ��ʼ����ҽ����
	 */
	private void initChnPanel(){
		chn.newPrsrp();
		initRxCombo();
		String freqCode=chn.getItemString(0, "CHN_FREQ_CODE");
		if(StringUtil.isNullString(freqCode)){
			TParm sysparm=OPDSysParmTool.getInstance().getSysParm();
			this.setValue("TAKE_DAYS", sysparm.getInt("DCT_TAKE_DAYS",0));
			this.setValue("DCT_TAKE_QTY", sysparm.getDouble("DCT_TAKE_QTY",0));
			this.setValue("CHN_FREQ_CODE", sysparm.getValue("G_FREQ_CODE",0));
			this.setValue("CHN_ROUTE_CODE", sysparm.getValue("G_ROUTE_CODE",0));
			this.setValue("DCTAGENT_CODE", sysparm.getValue("G_DCTAGENT_CODE",0));
		}else{
//			this.setValue("TAKE_DAYS", chn.getItemString(0, "TAKE_DAYS"));
			this.setValue("TAKE_DAYS", this.getValueString("TAKE_DAYS"));
			this.setValue("DCT_TAKE_QTY", chn.getItemString(0, "DCT_TAKE_QTY"));
			this.setValue("CHN_FREQ_CODE", chn.getItemString(0, "FREQ_CODE"));
			this.setValue("CHN_ROUTE_CODE", chn.getItemString(0, "ROUTE_CODE"));
			this.setValue("DCTAGENT_CODE", chn.getItemString(0, "DCTAGENT_CODE"));
			this.setValue("DESCRIPTION", chn.getItemString(0, "DESCRIPTION"));
		}


	}
	/**
	 * Ϊ������COMBO׼��Ҫ��ʾ�������б�����
	 */
	public void initRxCombo(){
		if(chn==null){
			this.messageBox_("��ʼ������ǩʧ��");
			return;
		}
		TParm orderNoData=chn.getRxComboData();
		if(orderNoData==null){
			this.messageBox_("��ʼ������ǩʧ��");
			return;
		}

		rxNo.setParmValue(orderNoData);
		String id=orderNoData.getValue("ID",orderNoData.getCount("ID")-1);
		rxNo.setValue(id);

		chn.setFilter("PRESRT_NO='" +id+"'");
		chn.filter();

		tableChn.setDataStore(chn);
		onChangeRx();
//		TParm parm=chn.getTableParm(id);
//		tableChn.setParmValue(parm);
	}
	/**
	 * �����ϵ�������
	 *
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onDiagCreateEditComponent(Component com, int row, int column) {

		String columnName = tableDiag.getParmMap(column);
		if (!"ICD_CODE".equalsIgnoreCase(columnName))
			return;
		if (!(com instanceof TTextField))
			return;

		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// ��table�ϵ���text����sys_fee��������
		TParm parm = new TParm();
		parm.setData("ICD_TYPE", wc);
		textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popDiagReturn");
	}
	/**
	 * ��������ǩ
	 */
	public void onNewRx(){
		chn.newPrsrp();
		initRxCombo();
	}
	/**
	 * ɾ������ǩ
	 */
	public void onDeleteRx(){
		if(!chn.deletePrsrp(this.getValueString("REXP_NO"))){
			this.messageBox_("ɾ��ʧ��");
			return;
		}
		initRxCombo();
	}
	/**
	 * ���SYS_FEE��������
	 *
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onMedCreateEditComponent(Component com, int row, int column) {
		String columnName = tableMed.getParmMap(column);
		if (!"ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		TParm parm = new TParm();
		parm.setData("W_GROUP", "any value");
		textfield.onInit();
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popOrderReturn");

	}

	/**
	 * ���SYS_FEE��������
	 *
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onExaCreateEditComponent(Component com, int row, int column) {
		String columnName = tableExa.getParmMap(column);
		if (!"ORDER_DESC".equalsIgnoreCase(columnName))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 5);
		textfield.onInit();
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popExaReturn");

	}

	/**
	 * ���SYS_FEE��������
	 *
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onChnCreateEditComponent(Component com, int row, int column) {
		String columnName = tableChn.getParmMap(column);
		if (!"ORDER_DESC1".equalsIgnoreCase(columnName)
				&& !"ORDER_DESC2".equalsIgnoreCase(columnName)
				&& !"ORDER_DESC3".equalsIgnoreCase(columnName)
				&& !"ORDER_DESC4".equalsIgnoreCase(columnName)) {
			return;
		}

		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 3);
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popChnOrderReturn");

	}

	/**
	 * �������,�ж��Ƿ����������ϣ�����ҽ��
	 *
	 * @param tag
	 * @param obj
	 */
	public void popDiagReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;

		tableDiag.acceptText();

		int rowNo = tableDiag.getSelectedRow();
		String icdTemp = parm.getValue("ICD_CODE");
		if (diag.isHaveSameDiag(icdTemp)) {

			tableDiag.acceptText();
			tableDiag.getTable().grabFocus();
			tableDiag.setSelectedRow(0);
			tableDiag.setSelectedColumn(1);
			tableDiag.removeRow(rowNo);
			tableDiag.addRow();
//			tableDiag.setDSValue(rowNo);
			this.messageBox_("������ѿ���");// liudy
			return;
		}

		boolean allowMain = parm.getBoolean("MAIN_DIAG_FLG");
		if (diag.isHavaMainDiag() < 0 && allowMain) {
			if ("C".equalsIgnoreCase(wc) && !OdoUtil.isAllowChnDiag(parm)) {
				this.messageBox_("�����Ϊ֢�򣬲�����Ϊ���");
				diag.deleteRow(rowNo);
				// odo.getDiagrec().insertRow();
				tableDiag.acceptText();
				tableDiag.getTable().grabFocus();
				tableDiag.setSelectedRow(0);
				tableDiag.setSelectedColumn(1);
				tableDiag.addRow();
				tableDiag.setDSValue();
				return;
			}
			diag.setItem(rowNo, "MAIN_DIAG_FLG", "Y");
		} else {
			diag.setItem(rowNo, "MAIN_DIAG_FLG", "N");
			// mainFlg=false;
		}
		diag.setActive(rowNo, true);
		diag.setItem(rowNo, "ICD_TYPE", wc);
		diag.setItem(rowNo, "DIAG_NOTE", parm.getValue("DESCRIPTION"));
		diag.setItem(rowNo, "ICD_CODE", parm.getValue("ICD_CODE"));
		if (rowNo == tableDiag.getRowCount() - 1) {
			rowNo = diag.insertRow();
		}
		tableDiag.setDSValue();
		tableDiag.getTable().grabFocus();
		tableDiag.setSelectedRow(rowNo);
		tableDiag.setSelectedColumn(1);
	}

	/**
	 * ����������ҩ
	 *
	 * @param tag
	 * @param obj
	 */
	public void popOrderReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		int row = tableMed.getSelectedRow();
		int oldRow = row;
		int column = tableMed.getSelectedColumn();
		String code = (String) tableMed.getValueAt(oldRow, column);
		if(!StringUtil.isNullString(order.getItemString(row, "ORDER_DESC"))){
			this.messageBox_("�Կ���ҽ���������޸�����");
			tableMed.setDSValue(row);
			tableMed.getTable().grabFocus();
			tableMed.setSelectedRow(tableMed.getRowCount()-1);
			tableMed.setSelectedColumn(tableMed.getColumnIndex("ORDER_DESC"));
			return;
		}
		if (order.isSameOrder(parm.getValue("ORDER_CODE"))) {
			if (this.messageBox("��ʾ��Ϣ", "��ҽ���Ѿ��������Ƿ������", 0) == 1) {
				tableMed.setValueAt(code, oldRow, column);
				return;
			}

		}
		tableMed.acceptText();
		order.setActive(row, true);
		order.initOrder(row, parm);
		if (row == tableMed.getRowCount() - 1) {
			order.insertRow(-1);
		}
		tableMed.setDSValue();
		tableMed.getTable().grabFocus();
		tableMed.setSelectedRow(oldRow);
		tableMed.setSelectedColumn(3);
		//// System.out.println("in popup");
//		order.showDebug();
	}
	/**
	 * ����������ҩ
	 *
	 * @param tag
	 * @param obj
	 */
	public void popExaReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		int row = tableExa.getSelectedRow();
		int oldRow = row;
		int column = tableExa.getSelectedColumn();
		String code = (String) tableExa.getValueAt(oldRow, column);
		tableExa.acceptText();
		if (exa.isSameOrder(parm.getValue("ORDER_CODE"))) {
			if (this.messageBox("��ʾ��Ϣ", "��ҽ���Ѿ��������Ƿ������", 0) == 1) {
				tableExa.setValueAt(code, oldRow, column);
				return;
			}

		}
		exa.setActive(row, true);
		exa.initOrder(row, parm);
		if (row == tableExa.getRowCount() - 1) {
			exa.insertRow();
		}
		tableExa.setDSValue();
		tableExa.getTable().grabFocus();
		tableExa.setSelectedRow(oldRow);
		tableExa.setSelectedColumn(3);
	}
	/**
	 * ������ҽ
	 *
	 * @param tag
	 * @param obj
	 */
	public void popChnOrderReturn(String tag, Object obj) {

		TParm parm = (TParm) obj;

		String rxNo;
		if (StringUtil.isNullString(this.getValueString("CHN_FREQ_CODE"))) {
			this.messageBox_("��ѡ��Ƶ��");
			return;
		}
		int row = tableChn.getSelectedRow();
		int oldRow = row;
		int column = tableChn.getSelectedColumn();
		String code = (String) tableChn.getValueAt(row, column);
		if (chn.isSameOrder(parm.getValue("ORDER_CODE"))) {
			if (this.messageBox("��ʾ", "��ҽ���Ѿ��������Ƿ������", 0) == 1) {
				tableChn.setValueAt(code, oldRow, column);
				return;
			}

		}
		tableChn.acceptText();
		int realColumn = 0;
		switch (column) {
		case 0:
			realColumn = 0;
			break;
		case 3:
			realColumn = 1;
			break;
		case 6:
			realColumn = 2;
			break;
		case 9:
			realColumn = 3;
			break;
		}
		int realrow = row * 4 + realColumn;
		rxNo = (String) this.getValue("REXP_NO");
		TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE"));
		if (parmBase.getErrCode() < 0) {
			return;
		}
		chn.setActive(realrow, true);

		chn.initOrder(realrow, parm);

		chn.setItem(realrow, "DCT_TAKE_QTY", this
				.getValueDouble("DCT_TAKE_QTY"));
//		chn.setItem(realrow, "TAKE_DAYS", this.getValueDouble("DCT_TAKE_DAYS"));
		chn.setItem(realrow, "TAKE_DAYS", this.getValueString("TAKE_DAYS"));
		chn.setItem(realrow, "FREQ_CODE", this.getValue("CHN_FREQ_CODE"));
		chn.setItem(realrow, "ROUTE_CODE", this.getValue("CHN_ROUTE_CODE"));
		chn.setItem(realrow, "DCTAGENT_CODE", this.getValue("DCTAGENT_CODE"));
		String desc=parm.getValue("ORDER_DESC")+" ("+parm.getValue("SPECIFICATION")+")";
		chn.setItem(realrow, "ORDER_DESC", desc);

		tableChn.getTable().grabFocus();
		if (column == 9&&!StringUtil.isNullString(chn.getItemString(chn.rowCount()-1, "ORDER_CODE"))) {
			addChnRow(rxNo, realrow);
		}
		tableChn.setSelectedRow(row);
		tableChn.setSelectedColumn(column + 1);
//		// System.out.println("after popchn");
//		chn.showDebug();
		tableChn.setDSValue(row);
	}
	/**
	 * ��ҽTABLֵ�ı��¼�
	 */
	public void onChnTableChangeValue(TTableNode tNode){
		int column=tNode.getColumn();
		int row=tNode.getRow();

		if(column==0||column==3||column==6||column==9){
			return;
		}
		tNode.getTable().acceptText();
		int realrow;
		switch (column) {
		case 1:
			realrow = row * 4 + 0;
			chn.setItem(realrow, "MEDI_QTY", TCM_Transform.getDouble(tNode
					.getValue()));
			break;
		case 4:
			realrow = row * 4 + 1;
			chn.setItem(realrow, "MEDI_QTY", TCM_Transform.getDouble(tNode
					.getValue()));
			break;
		case 7:
			realrow = row * 4 + 2;
			chn.setItem(realrow, "MEDI_QTY", TCM_Transform.getDouble(tNode
					.getValue()));
			break;
		case 10:
			realrow = row * 4 + 3;
			chn.setItem(realrow, "MEDI_QTY", TCM_Transform.getDouble(tNode
					.getValue()));
			break;
		case 2:
			realrow=row*4+0;
			chn.setItem(realrow, "DCTEXCEP_CODE", TCM_Transform.getString(tNode
					.getValue()));
			break;
		case 5:
			realrow=row*4+1;
			chn.setItem(realrow, "DCTEXCEP_CODE", TCM_Transform.getString(tNode
					.getValue()));
			break;
		case 8:
			realrow=row*4+2;
			chn.setItem(realrow, "DCTEXCEP_CODE", TCM_Transform.getString(tNode
					.getValue()));
			break;
		case 11:
			realrow=row*4+3;
			chn.setItem(realrow, "DCTEXCEP_CODE", TCM_Transform.getString(tNode
					.getValue()));
			break;
		}
		tNode.getTable().setDSValue(row);
//		// System.out.println("herer======");
//		chn.showDebug();
	}
	/**
	 * Ϊ��ҩTABLE����һ��
	 *
	 * @param rxNo
	 *            ������
	 * @param row
	 *            �к�
	 */
	public void addChnRow(String rxNo, int row) {
		if (StringUtil.isNullString(rxNo)){
			return;
		}
		for (int i = row; i < row + 4; i++) {
			chn.insertRow(-1,rxNo);
		}
		initChnTable(rxNo);
	}
	/**
	 * ����һ��4ζ��ҩ
	 * @param rxNo
	 */
	private void fillChnRow(String rxNo){
		int count=chn.rowCount();
		int countToAdd=(count/4+1)*4;
		String lastCode=chn.getItemString(chn.rowCount()-1, "ORDER_CODE");
		if(count%4==0&&StringUtil.isNullString(lastCode)){
			return;
		}
		for(int i=count;i<countToAdd;i++){
			chn.insertRow(i, rxNo);
		}
	}
	/**
	 *
	 * @param rxNo
	 * @param isInit
	 * @return
	 */
	public boolean initChnTable(String rxNo) {
		if (StringUtil.isNullString(rxNo)) {
			this.messageBox_("û�д���ǩ");
			return false;
		}
		TParm tableParm=chn.getTableParm(rxNo);
		tableChn.setParmValue(tableParm);
		return true;
	}

	/**
	 * ����ϵ�ѡ�¼����ж��Ƿ���������//liudy more remark
	 *
	 * @param obj
	 */
	public boolean onDiagMain(Object obj) {

		int row = tableDiag.getSelectedRow();

		if(StringUtil.isNullString(diag.getItemString(row, "ICD_CODE"))){
			tableDiag.setDSValue(row);
			return true;
		}
		tableDiag.acceptText();
		boolean isHavingMain=diag.haveMainDiag(new int[1]);
		if (!isHavingMain) {
			TParm parm=new TParm();
			parm.setData("SYNDROME_FLG",diag.getItemData(row, "SYNDROME_FLG"));
			if(TypeTool.getBoolean(this.getValue("C")) &&!OdoUtil.isAllowChnDiag(parm)){

				this.messageBox_("�����Ϊ֢�򣬲�����Ϊ���");

				return true;
			}
		}
		int count=diag.rowCount();
		for(int i=0;i<count;i++){
			diag.setItem(i, "MAIN_DIAG_FLG", "N");
		}
		diag.setItem(row, "MAIN_DIAG_FLG", "Y");
		tableDiag.setDSValue();
		return false;
	}
	/**
	 * ����ֵ�ı��¼�
	 * @param obj
	 * @return
	 */
	public boolean onMainChangeValue(TTableNode tNode){
		int column=tableMain.getSelectedColumn();
		int columnInt=tableMain.getColumnIndex("PACK_DESC");
		int row=tNode.getRow();
		if(!"PACK_DESC".equalsIgnoreCase(tableMain.getParmMap(tNode.getColumn()))&&StringUtil.isNullString( main.getItemString(row, "PACK_DESC"))){
			this.messageBox_("û��ģ������");
			return true;
		}
		if(column==columnInt){
			main.setActive(row,true);
			if(main.isActive(main.rowCount()-1)){
				row=main.insertRow(-1);
			}
			tableMain.setDSValue();
			tableMain.getTable().grabFocus();
			tableMain.setSelectedRow(row);
			tableMain.setSelectedColumn(tableMain.getColumnIndex("PACK_DESC"));
		}
		return false;
	}
//	/**
//	 * �����Ӣ����ʾ
//	 */
//	public void onChnEng() {
//		TCheckBox chnEng = (TCheckBox) this.getComponent("CHN_DSNAME");
//		if (chnEng.isSelected()) {
//			chnEng.setText("Chinese version");
//			tableDiag
//					.setHeader("��,30,boolean;Disease Code,80;Disease Name,150;��ע,130");
//			tableDiag
//					.setParmMap("MAIN_DIAG_FLG;ICD_CODE;ICD_ENG_DESC;DIAG_NOTE");
//			tableDiag.setDSValue();
//		} else {
//			chnEng.setText("Ӣ�Ĳ���");
//			tableDiag.setHeader("��,30,boolean;�������,80;����,150;��ע,130");
//			tableDiag
//					.setParmMap("MAIN_DIAG_FLG;ICD_CODE;ICD_CHN_DESC;DIAG_NOTE");
//			tableDiag.setDSValue();
//		}
//	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue("PACK_CODE;PACK_DESC;SPCFYDEPT;PARENT_PACK_CODE;SUBJ_TEXT;OBJ_TEXT;PHYSEXAM_REC;PROPOSAL;PARENT_PACK_DESC");
		this.setValue("OPD_FIT_FLG", "N");
		this.setValue("EMG_FIT_FLG", "N");
		callFunction("UI|new|setEnabled", true);
		callFunction("UI|PARENT_PACK_CODE|setEnabled", false);
        callFunction("UI|PARENT_PACK_DESC|setEnabled", false);
		
		tableDiag.removeRowAll();
    	tableMed.removeRowAll();
    	tableChn.removeRowAll();
    	tableExa.removeRowAll();
		
		// ���Ʊ�ҽʦ������ʼ����ʾ����
		TComboBox dept1 = (TComboBox) this.getComponent("DEPT1");
		TComboBox dept = (TComboBox) this.getComponent("DEPT");
		TComboBox dr = (TComboBox) this.getComponent("OPERATOR");
		TLabel label=(TLabel)this.getComponent("LABEL");
		if (DEPT.equalsIgnoreCase(deptOrDr)) {
			label.setZhText(DEPT_LABEL);
			label.setEnText("Dept.");
			dept.setValue(Operator.getDept());
			dept.setVisible(true);
			//
			dept1.setValue(Operator.getDept());
			dept1.setVisible(true);
			//
			dr.setVisible(false);
		} else {
			label.setZhText(DR_LABEL);
			label.setEnText("Dr.");
			dr.setValue(Operator.getID());
			dr.setVisible(true);
			dept.setVisible(false);
			//
			dept1.setVisible(false);
		}
		TRadioButton w = (TRadioButton) this.getComponent("W");
		w.setSelected(true);
		wc = "W";
		packParm= new TParm();
	}

	/**
	 * ����ҽ����ѡ�¼����������ҽ��Ϊ�գ��򲻲������繴ѡΪ����ҽ��������һ�е��������Զ����룬��ԭ��������ҽ����ѡΪ�����ӣ���ȡ��ԭ��������ҽ����
	 */
	public boolean onLinkMain(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int row = table.getSelectedRow();
		int column = table.getSelectedColumn();
		if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
			this.messageBox_("ҽ��Ϊ���޷�����");
			table.setValueAt("N", row, column);
			return true;
		}
		int linkNo;
		boolean value = TCM_Transform.getBoolean(table.getValueAt(table
				.getSelectedRow(), table.getSelectedColumn()));
		if (value) {
			linkNo = order.getMaxLinkNo() + 1;

			order.setItem(row, "LINK_NO", linkNo);
			order.setItem(row, "LINKMAIN_FLG", "Y");
			if (row + 1 < order.rowCount())
				order.setItem(row + 1, "LINK_NO", linkNo);
		} else {
			int linktemp = order.getItemInt(row, "LINK_NO");
			for (int i = 0; i < order.rowCount(); i++) {
				if (linktemp == order.getItemInt(i, "LINK_NO")) {
					order.setItem(i, "LINK_NO", "");
					order.setItem(i, "LINKMAIN_FLG", "N");
				}
			}

		}
		tableMed.setDSValue();
		return false;
	}

	public boolean onMedTableChangeValue(TTableNode tNode) {
		tableMed.acceptText();
		tableMed.setDSValue();
		return false;
	}

	/**
	 * �ı�����ҽ��ǵ�RADIO�¼�
	 *
	 * @param wc
	 *            String �������ҽ���
	 */
	public void onWc(String wc) {
		this.wc = wc;
	}

	/**
	 * ������ TABLE_NAME��ֵ��ɾ���¼�ʹ��
	 *
	 * @param tableName
	 *            String
	 */
	public void onTableClick(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * ����ģ������
	 */
	public void onSavePackName(){
		String packName=this.getValueString("PACK_DESC");
		int row=tableMain.getSelectedRow();
		if(row<0){
			return;
		}
		main.setItem(row, "PACK_DESC", packName);
	}
	/**
	 * ɾ���¼�����ҩTABLEɾ���������ݣ�������ʾ��TABLE������TABLE�ж��Ƿ����һ�в����ǿ��У������򷵻أ��粻����ɾ����������
	 */
	public void onDelete() {
		if(tableName == null){

			if ("PARENT".equals(node.getType())) {
				String parentCode = node.getID();
				if(parentCode.length() == 3){
					String parentSql = "SELECT * FROM OPD_PACK_MAIN WHERE DEPT_OR_DR='"+deptOrDr+"' AND DEPTORDR_CODE='"+code+"'  AND PARENT_PACK_CODE='"+parentCode+"'";
					TParm childParm = new TParm(TJDODBTool.getInstance().select(parentSql));
					if(childParm.getCount() > 0){
						this.messageBox("�÷��������ײ�ģ�棬����ֱ��ɾ������");
						return;
					}
					

					this.setValue("SUBJ_TEXT", "");
					this.setValue("OBJ_TEXT", "");
					this.setValue("PHYSEXAM_REC", "");
					
					String[] sql = new String[1];
					sql[0]="DELETE FROM OPD_PACK_MAIN " +
					" WHERE PACK_CODE='"+getValueString("PACK_CODE")+"' " +
					" AND DEPTORDR_CODE = '"+code+"' " +
					" AND DEPT_OR_DR = '"+deptOrDr+"'";

					TParm saveInParm = new TParm();
					saveInParm.setData("SQL", sql);
					
					TParm result = TIOM_AppServer.executeAction(
							"action.opd.OpdCommonPackAction", "onSave", saveInParm);
					if (result.getErrCode() != 0) {
						this.messageBox("E0001");
					} else {
						this.messageBox("P0001");
						onClear();
					}
					
					onInitSelectTree();	
					updateFlg="update";
					tableName=null;

					return;
				}else{
					updateFlg = "delete";
					order.deletePack(parentCode);
					diag.deletePack(parentCode);
					exa.deletePack(parentCode);
					chn.deletePack(parentCode);
					tableMed.setDSValue();
					tableExa.setDSValue();
					tableDiag.setDSValue();
					this.setValue("SUBJ_TEXT", "");
					this.setValue("OBJ_TEXT", "");
					this.setValue("PHYSEXAM_REC", "");
					this.initChnTable(this.getValueString("REXP_NO"));
					saveBody();
					return;
				}
				
			}

			if ("CHILD".equals(node.getType())) {
				String packCode = getValueString("PACK_CODE");
				// String packCode=main.getItemString(row, "PACK_CODE");
				// main.deleteRow(row);
				updateFlg = "delete";
				order.deletePack(packCode);
				diag.deletePack(packCode);
				exa.deletePack(packCode);
				chn.deletePack(packCode);
				tableMed.setDSValue();
				tableExa.setDSValue();
				tableDiag.setDSValue();
				this.setValue("SUBJ_TEXT", "");
				this.setValue("OBJ_TEXT", "");
				this.setValue("PHYSEXAM_REC", "");
				this.initChnTable(this.getValueString("REXP_NO"));
				saveBody();
				return;
			}

		}
		
		TTable table = (TTable) this.getComponent(tableName);
		int row = table.getSelectedRow();
		int column = table.getSelectedColumn();
		updateFlg="update";
		// ����ҩTABLE
		if (!"TABLE_CHN".equalsIgnoreCase(tableName)) {

			if (row == table.getRowCount() - 1) {
				return;
			}
			if("TABLE_MED".equalsIgnoreCase(tableName)){
				order.deleteRow(row);
				table.setDSValue();
			}else if("TABLE_DIAG".equalsIgnoreCase(tableName)){
				diag.deleteRow(row);
				table.setDSValue();
			}else if("TABLE_EXA".equalsIgnoreCase(tableName)){
				exa.deleteRow(row);
				table.setDSValue();
			}else{

				String packCode=getValueString("PACK_CODE");
//				String packCode=main.getItemString(row, "PACK_CODE");
//				main.deleteRow(row);
				updateFlg="delete";
				order.deletePack(packCode);
				diag.deletePack(packCode);
				exa.deletePack(packCode);
				chn.deletePack(packCode);
				tableMed.setDSValue();
				tableExa.setDSValue();
				tableDiag.setDSValue();
				this.setValue("SUBJ_TEXT", "");
				this.setValue("OBJ_TEXT", "");
				this.setValue("PHYSEXAM_REC", "");
				this.initChnTable(this.getValueString("REXP_NO"));
				table.setDSValue();
			}
			saveBody();
			return;
		}
		// ��ҩTABLE
		if (column >= 0 && column <= 2){
			column = 0;
		}
		else if (column >= 3 && column <= 5){
			column = 1;
		}
		else if (column >= 6 && column <= 8){
			column = 2;
		}else{
			column = 3;
		}

		int realRow = row * 4 + column;
		chn.deleteRow(realRow);
		this.initChnTable(this.getValueString("REXP_NO"));
		saveBody();
	}
	/**
	 * ����ǩ�Ÿı��¼�
	 *
	 */
	public void onChangeRx(){
		String rxNo=this.getValueString("REXP_NO");
		chn.setFilter("PRESRT_NO='" +rxNo+"'");
		chn.filter();
//		chn.showDebug();
		fillChnRow(rxNo);

		TParm parm=chn.getTableParm(rxNo);
		tableChn.setParmValue(parm);
		//// System.out.println("tableParm="+parm);
		String freqCode=chn.getItemString(0, "CHN_FREQ_CODE");
		if(StringUtil.isNullString(freqCode)){
			TParm sysparm=OPDSysParmTool.getInstance().getSysParm();
			this.setValue("TAKE_DAYS", sysparm.getInt("DCT_TAKE_DAYS",0));
			this.setValue("DCT_TAKE_QTY", sysparm.getDouble("DCT_TAKE_QTY",0));
			this.setValue("CHN_FREQ_CODE", sysparm.getValue("G_FREQ_CODE",0));
			this.setValue("CHN_ROUTE_CODE", sysparm.getValue("G_ROUTE_CODE",0));
			this.setValue("DCTAGENT_CODE", sysparm.getValue("G_DCTAGENT_CODE",0));
		}else{
			this.setValue("TAKE_DAYS", chn.getItemString(0, "TAKE_DAYS"));
			this.setValue("DCT_TAKE_QTY", chn.getItemString(0, "DCT_TAKE_QTY"));
			this.setValue("CHN_FREQ_CODE", chn.getItemString(0, "FREQ_CODE"));
			this.setValue("CHN_ROUTE_CODE", chn.getItemString(0, "ROUTE_CODE"));
			this.setValue("DCTAGENT_CODE", chn.getItemString(0, "DCTAGENT_CODE"));
			this.setValue("DESCRIPTION", chn.getItemString(0, "DESCRIPTION"));
		}
	}
	/**
	 * �����¼�
	 */
	public void onSave() {
//		int row=tableMain.getSelectedRow();
//		if(row<0){
//			this.messageBox_("û�пɱ�������ݣ���ѡ�������ĳ�н��б���");
//			return;
//		}
		if (StringUtil.isNullString(this.getValueString("PACK_CODE"))) {
			this.messageBox_("û�пɱ��������");
			this.callFunction("UI|PACK_DESC|grabFocus");
			return;
		}
		if (StringUtil.isNullString(this.getValueString("PACK_DESC"))) {
			this.messageBox_("����дģ������");
			this.callFunction("UI|PACK_DESC|grabFocus");
			return;
		}
		
		if(this.getValueString("PARENT_PACK_CODE").length() > 0){
			if(getValueString("PARENT_PACK_CODE").length() != 3){
				this.messageBox("����д��ȷ���ϼ�������룬����001");
				this.callFunction("UI|PARENT_PACK_CODE|grabFocus");
				return;
			}
		}
		
//		main.setItem(clickMainRow, "SUBJ_TEXT", this.getValue("SUBJ_TEXT"));
//		main.setItem(clickMainRow, "OBJ_TEXT", this.getValue("OBJ_TEXT"));
//		main.setItem(clickMainRow, "PHYSEXAM_REC", this.getValue("PHYSEXAM_REC"));
//		main.setItem(clickMainRow, "PROPOSAL", this.getValue("PROPOSAL"));  //add by huangtt 20150226
		
		 if ("Root".equals(node.getType())) {
			 TParm saveInParm = new TParm();
			 String[] sql = new String[1];
			 sql[0]=" INSERT INTO OPD_PACK_MAIN" +
				"(DEPT_OR_DR, DEPTORDR_CODE, PACK_CODE, " +
				" PACK_DESC, OPD_FIT_FLG, EMG_FIT_FLG, " +
				" SPCFYDEPT, OPT_USER, OPT_DATE, OPT_TERM," +
				" SUBJ_TEXT, OBJ_TEXT, PHYSEXAM_REC, PROPOSAL,PARENT_PACK_CODE)" +
				" VALUES" +
				" ('"+deptOrDr+"', '"+code+"', '"+getValueString("PACK_CODE")+"', " +
				" '"+getValueString("PACK_DESC")+"', 'N','N', " +
				" '', '"+Operator.getID()+"', SYSDATE, '"+Operator.getIP()+"', " +
				" '','','','','')";
			saveInParm.setData("SQL", sql);
			TParm result = TIOM_AppServer.executeAction(
					"action.opd.OpdCommonPackAction", "onSave", saveInParm);
			if (result.getErrCode() != 0) {
				this.messageBox("E0001");
				// this.messageBox_(result.getErrText());
			} else {
				this.messageBox("P0001");
				onClear();
			}

			onInitSelectTree();
			updateFlg = "update";
			tableName = null;

			return;
	      }
	        
		
		saveBody();
	}
	/**
	 * �����ʵ��
	 */
	private void saveBody(){
		TParm saveInParm = new TParm();
		Timestamp now=TJDODBTool.getInstance().getDBTime();
//		main.setOperator(Operator.getID(), now, Operator.getIP());
//		String[] sql = main.getUpdateSQL();
		
		String[] sql = new String[1];
		if(updateFlg.equals("insert")){
			sql[0]=" INSERT INTO OPD_PACK_MAIN" +
					"(DEPT_OR_DR, DEPTORDR_CODE, PACK_CODE, " +
					" PACK_DESC, OPD_FIT_FLG, EMG_FIT_FLG, " +
					" SPCFYDEPT, OPT_USER, OPT_DATE, OPT_TERM," +
					" SUBJ_TEXT, OBJ_TEXT, PHYSEXAM_REC, PROPOSAL,PARENT_PACK_CODE)" +
					" VALUES" +
					" ('"+deptOrDr+"', '"+code+"', '"+getValueString("PACK_CODE")+"', " +
					" '"+getValueString("PACK_DESC")+"', '"+getValueString("OPD_FIT_FLG")+"','"+getValueString("EMG_FIT_FLG")+"', " +
					" '"+getValueString("SPCFYDEPT")+"', '"+Operator.getID()+"', SYSDATE, '"+Operator.getIP()+"', " +
					" '"+getValueString("SUBJ_TEXT")+"','"+getValueString("OBJ_TEXT")+"','"+getValueString("PHYSEXAM_REC")+"','"+
					getValueString("PROPOSAL")+"','"+getValueString("PARENT_PACK_CODE")+"')";
			
			if(this.getValueString("PARENT_PACK_CODE").length() > 0){
				TParm parm = new TParm(TJDODBTool.getInstance().select(INIT_SQL+" AND PACK_CODE='" +getValueString("PARENT_PACK_CODE")+"'"));
				if(parm.getCount() < 0){
					this.messageBox("û�и÷��࣬����д���з���");
					return;
					
				}
			}
			
			
		}else if(updateFlg.equals("update")){
			sql[0]="UPDATE OPD_PACK_MAIN " +
					" SET PACK_DESC='"+getValueString("PACK_DESC")+"', PARENT_PACK_CODE='"+getValueString("PARENT_PACK_CODE")+"'," +
					" OPD_FIT_FLG='"+getValueString("OPD_FIT_FLG")+"',EMG_FIT_FLG='"+getValueString("EMG_FIT_FLG")+"'," +
					" SUBJ_TEXT='"+getValueString("SUBJ_TEXT")+"',OBJ_TEXT='"+getValueString("OBJ_TEXT")+"'," +
					" PHYSEXAM_REC='"+getValueString("PHYSEXAM_REC")+"',PROPOSAL='"+getValueString("PROPOSAL")+"'," +
					" SPCFYDEPT='"+getValueString("SPCFYDEPT")+"',OPT_USER='"+Operator.getID()+"'," +
					" OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"'" +
					" WHERE PACK_CODE='"+getValueString("PACK_CODE")+"' " +
					" AND DEPTORDR_CODE = '"+code+"'" +
					" AND DEPT_OR_DR = '"+deptOrDr+"'";
			
			if(this.getValueString("PARENT_PACK_CODE").length() > 0){
				TParm parm = new TParm(TJDODBTool.getInstance().select(INIT_SQL+" AND PACK_CODE='" +getValueString("PARENT_PACK_CODE")+"'"));
				if(parm.getCount() < 0){
					this.messageBox("û�и÷��࣬����д���з���");
					return;
					
				}
			}
			
		}else if(updateFlg.equals("delete")){
			sql[0]="DELETE FROM OPD_PACK_MAIN " +
					" WHERE PACK_CODE='"+getValueString("PACK_CODE")+"' " +
					" AND DEPTORDR_CODE = '"+code+"' " +
					" AND DEPT_OR_DR = '"+deptOrDr+"'";
		}

		order.setOperator(Operator.getID(), now, Operator.getIP());
		String[] tempSql = order.getUpdateSQL();
		sql = StringTool.copyArray(sql, tempSql);
		chn.setOperator(Operator.getID(), now, Operator.getIP());
		
		tempSql = chn.getUpdateSQL();
		sql = StringTool.copyArray(sql, tempSql);
		
//		System.out.println("save sql1111111111====="+sql);
		
		exa.setOperator(Operator.getID(), now, Operator.getIP());
		tempSql=exa.getUpdateSQL();
		sql= StringTool.copyArray(sql, tempSql);
		diag.setOperator(Operator.getID(), now, Operator.getIP());
		tempSql = diag.getUpdateSQL();
		sql = StringTool.copyArray(sql, tempSql);
		saveInParm.setData("SQL", sql);
		
		for(String sqls:sql){
			System.out.println("save sql====="+sqls);
		}
		
		TParm result = TIOM_AppServer.executeAction(
				"action.opd.OpdCommonPackAction", "onSave", saveInParm);
		if (result.getErrCode() != 0) {
			this.messageBox("E0001");
//			this.messageBox_(result.getErrText());
		} else {
			this.messageBox("P0001");
			onClear();
		}
		
		onInitSelectTree();
		
//		main.resetModify();
		order.resetModify();
		chn.resetModify();
		exa.resetModify();
		diag.resetModify();		
		updateFlg="update";
		tableName=null;
	}
	/**
	 * ��ҽ���ÿؼ��Ļس��¼��������޸�һ�Ŵ���ǩ�����й�����Ϣ��ֵ
	 * @param tagName String
	 */
	public void onChangeChn(String tagName){
		String rxNo=this.getValueString("REXP_NO");
		if(StringUtil.isNullString(rxNo)){
			return;
		}
		int count=chn.rowCount();
		if(count<1){
			return;
		}
		String value=this.getValueString(tagName);
		String column="";
		if("TAKE_DAYS".equalsIgnoreCase(tagName)){
			column="TAKE_DAYS";
		}else if("DCT_TAKE_QTY".equalsIgnoreCase(tagName)){
			column="DCT_TAKE_QTY";
		}else if("CHN_FREQ_CODE".equalsIgnoreCase(tagName)){
			column="FREQ_CODE";
		}else if("CHN_ROUTE_CODE".equalsIgnoreCase(tagName)){
			column="ROUTE_CODE";
		}else if("DCTAGENT_CODE".equalsIgnoreCase(tagName)){
			column="DCTAGENT_CODE";
		}else if("DESCRIPTION".equalsIgnoreCase(tagName)){
			column="DESCRIPTION";
		}
		if("".equalsIgnoreCase(column)){
			this.messageBox_("��ֵʧ��");
			return;
		}
		for(int i=0;i<count;i++){
			if(StringUtil.isNullString(chn.getItemString(i, "ORDER_CODE"))){
				continue;
			}
			chn.setItem(i, column, value);
			chn.setActive(i,true);
		}
	}
	
	public String getDeptDesc(String deptCode){
		String sql = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}
	
	public String getDrDesc(String drCode){
		String sql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+drCode+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("USER_NAME", 0);
	}
	

	
	
	public void onNew(){
		
		if ("Root".equals(node.getType())) {
			
			String sql = "SELECT  MAX(PACK_CODE) PACK_CODE  FROM OPD_PACK_MAIN WHERE DEPT_OR_DR='"+deptOrDr+"' AND DEPTORDR_CODE='"+code+"' AND PARENT_PACK_CODE IS NULL AND LENGTH(PACK_CODE)=3";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			String packCode = "";
			if(parm.getValue("PACK_CODE", 0).length() == 0){
				packCode="001";
			}else{
				packCode = StringTool.addString(parm.getValue("PACK_CODE", 0));
			}
			this.setValue("PACK_CODE", packCode);
			this.setValue("PACK_DESC", "");
			this.setValue("OPD_FIT_FLG", "N");
			this.setValue("EMG_FIT_FLG", "N");
			this.setValue("SPCFYDEPT", "");
			this.setValue("PARENT_PACK_CODE", "");
			
			return;
		}

		if ("PARENT".equals(node.getType())) {
			if(node.getID().length() > 3){
				this.messageBox("�����ڸ�Ŀ¼�´���ģ��");
				return;
			}
			
			TParm parm = new TParm();
			String packCode =  SystemTool.getInstance().getNo("ALL", "ODO", "PACK","PACK_CODE");
			this.setValue("PACK_CODE", packCode);
			this.setValue("PACK_DESC", "");
			this.setValue("OPD_FIT_FLG", "Y");
			this.setValue("EMG_FIT_FLG", "Y");
			this.setValue("SPCFYDEPT", "");
			this.setValue("PARENT_PACK_CODE", node.getID());
			updateFlg = "insert";
			selRow=-1;
			
			parm.setData("SUBJ_TEXT", "");
			parm.setData("OBJ_TEXT", "");
			parm.setData("PHYSEXAM_REC", "");
			parm.setData("PROPOSAL", "");
			onClickMainTable(packCode,parm);
			return;
		}
		
		
	}
}
