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
 * Title: 门诊医生工作站常用组套
 * </p>
 *
 * <p>
 * Description:门诊医生工作站常用组套
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

	// 科别与医师分类，科别或医师代码
	private String deptOrDr, code,desc, DEPT = "1";
	// 诊断、处置及其他、西成药、中药TABLE
	private TTable tableDiag, tableMed, tableChn, tableMain,tableExa;
	// 中医控件组
	private TComboBox rxNo;
	// 科室
	private String DEPT_LABEL = "科室";
	// 医师
	private String DR_LABEL = "医师";
	// 主表，诊断，处置，药品的TDS对象
	private CommonPackMain main;
	// 中医对象
	private CommonPackChn chn;
	// 西成药对象
	private CommonPackOrder order;
	// 诊断对象
	private CommonPackDiag diag;
	// 检验检查对象
	private CommonPackExa exa;
	// 诊断中西医标记
	private String wc;
	// 默认中药用量
	private double dctMediQty = 0.0;
	// 默认中药天数
	private double dctTakeDays = 0;
	// 共用TABLE名
	private String tableName;
	// 接受参数用ODO对象
	private ODO odo;
	// 主表记录选中行
	private int clickMainRow;
	
	public static String TREE = "Tree";
    TTreeNode treeRoot;
    private TParm packParm;
    private int selRow=-1;
    
    private String updateFlg="";
    /**
     * 树的数据放入datastore用于对树的数据管理
     */
    TDataStore treeDataStore = new TDataStore();
    /**
     * 编号规则类别工具
     */
    SYSRuleTool ruleTool;
    TTreeNode node;
    
  //初始化SQL
	private String INIT_SQL;


	/**
	 * 初始化
	 */
	public void onInit() {
		// 初始化
		super.onInit();
		// 初始化入参
		initParameter();
		// 初始化事件
		initEvent();
		// 初始化界面
		onClear();
		
		INIT_SQL="SELECT * FROM OPD_PACK_MAIN WHERE DEPT_OR_DR='"+deptOrDr+"' AND DEPTORDR_CODE='"+code+"' ";
		
		// 初始化树
        onInitSelectTree();
        

		// 初始化数据
		initData();
		
		callFunction("UI|PARENT_PACK_CODE|setEnabled", false);
        callFunction("UI|PARENT_PACK_DESC|setEnabled", false);
		
		
		
	}
	
	/**
     * 初始化树
     */
    public void onInitSelectTree() {
        // 得到树根
        treeRoot = (TTreeNode) callMessage("UI|" + TREE + "|getRoot");
        if (treeRoot == null)
            return;
        // 给根节点添加文字显示
        treeRoot.setText(desc);
        // 给根节点赋tag
        treeRoot.setType("Root");
        // 设置根节点的id
        treeRoot.setID("");
        // 清空所有节点的内容
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
        
        // 调用树点初始化方法
        callMessage("UI|" + TREE + "|update");
    }
	
	/**
     * 下载树数据
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
	 * ds根据code过滤事件
	 * @param code String :PACK_CODE
	 */
	public void filtDs(String code){

//		String filterString="PACK_CODE='" +code+"'";
//		dsMain.setFilter(filterString);
//		dsMain.filter();
		
		packParm = new TParm(TJDODBTool.getInstance().select(INIT_SQL+" AND PACK_CODE='" +code+"'"));

	}

    
    /**
     * 点击树
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
	 * 初始化入参
	 */
	public void initParameter() {
		Object obj = this.getParameter();
		if (obj == null) {
			this.messageBox_("无初始化参数");
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
	 * 初始化控件及其事件
	 */
	public void initEvent() {
//		// 主表TABLE
//		tableMain = (TTable) this.getComponent("TABLE_MAIN");
//		tableMain.addEventListener("TABLE_MAIN->"+ TTableEvent.CHANGE_VALUE, this,
//		"onMainChangeValue");
//		tableMain.setDataStore(main);
		
		//单击选中树项目
        addEventListener(TREE + "->" + TTreeEvent.CLICKED, "onTreeClicked");
		
		// 诊断
		tableDiag = (TTable) this.getComponent("TABLE_DIAG");
		// //值改变事件
		// tableDiag.addEventListener(TTableEvent.CHANGE_VALUE, this,
		// "onDiagTableChangeValue");
		// 主诊断勾选事件
		tableDiag.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onDiagMain");
		// 添加诊断
		tableDiag.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onDiagCreateEditComponent");

		tableMed = (TTable) this.getComponent("TABLE_MED");
		// 值改变事件
		tableMed.addEventListener(TTableEvent.CHANGE_VALUE, this,
				"onMedTableChangeValue");
		// 添加药品
		tableMed.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onMedCreateEditComponent");
		// 主诊断勾选事件
		tableMed.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onLinkMain");


		tableExa = (TTable) this.getComponent("TABLE_EXA");
		// 添加药品
		tableExa.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onExaCreateEditComponent");



		tableChn = (TTable) this.getComponent("TABLE_CHN");
		// 值改变事件
		tableChn.addEventListener("TABLE_CHN->"+TTableEvent.CHANGE_VALUE, this,
				"onChnTableChangeValue");
		// 添加中药
		tableChn.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onChnCreateEditComponent");

		rxNo=(TComboBox)this.getComponent("REXP_NO");
	}

	/**
	 * 初始化数据
	 */
	public void initData() {

//		main = new CommonPackMain(this.deptOrDr, this.code);
//		if(odo==null){
//			if (!main.onQuery()) {
//				this.messageBox_("初始化失败");
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
	 * 主TABLE点击事件,为各个控件赋值
	 */
	public void onClickMainTable(String packCode,TParm parm) {

//		int row = tableMain.getSelectedRow();
//		clickMainRow=row;
//		String packCode = main.getItemString(row, "PACK_CODE");
		if (StringUtil.isNullString(packCode)) {
			this.messageBox_("取得数据错误");
			return;
		}
		order = new CommonPackOrder(this.deptOrDr, this.code, packCode);
		if(odo==null){
			if (!order.onQuery()) {
				this.messageBox_("查询西成药失败");
				return;
			}
			order.insertRow(-1);
		}else{
			if(!order.initOdo(odo)){
				this.messageBox_("查询西成药失败");
				return;
			}
		}

		tableMed.setDataStore(order);
		tableMed.setDSValue();

		exa = new CommonPackExa(this.deptOrDr, this.code, packCode);
		if(odo==null){
			if (!exa.onQuery()) {
				this.messageBox_("查询西成药失败");
				return;
			}
			exa.insertRow();
		}else{
			if(!exa.initOdo(odo)){
				this.messageBox_("查询西成药失败");
				return;
			}
		}
		tableExa.setDataStore(exa);
		tableExa.setDSValue();
		// 诊断
		diag = new CommonPackDiag(this.deptOrDr, this.code, packCode);
		if(odo==null){
			if (!diag.onQuery()) {
				this.messageBox_("查询诊断失败");
				return;
			}
			diag.insertRow();
		}else{
			if(!diag.initOdo(odo)){
				this.messageBox_("查询诊断失败");
				return;
			}
		}

		tableDiag.setDataStore(diag);
		tableDiag.setDSValue();

		chn = new CommonPackChn(this.deptOrDr, this.code, packCode);
		if(odo==null){
			if (!chn.onQuery()) {
				this.messageBox_("查询中药失败");
				return;
			}
		}else{
			if(!chn.initOdo(odo)){
				this.messageBox_("查询诊断失败");
				return;
			}
		}
		initChnPanel();

//		this.setValue("PACK_CODE", main.getItemString(row, "PACK_CODE"));
//		this.setValue("PACK_DESC", main.getItemString(row, "PACK_DESC"));
		this.setValue("SUBJ_TEXT", parm.getValue("SUBJ_TEXT"));
		this.setValue("OBJ_TEXT", parm.getValue("OBJ_TEXT"));
		this.setValue("PHYSEXAM_REC", parm.getValue("PHYSEXAM_REC"));
		this.setValue("PROPOSAL", parm.getValue("PROPOSAL"));  //add by huangtt 20150226 添加建议

	}
	/**
	 * 初始化中医界面
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
	 * 为处方号COMBO准备要显示的下拉列表数据
	 */
	public void initRxCombo(){
		if(chn==null){
			this.messageBox_("初始化处方签失败");
			return;
		}
		TParm orderNoData=chn.getRxComboData();
		if(orderNoData==null){
			this.messageBox_("初始化处方签失败");
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
	 * 添加诊断弹出窗口
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
		// 给table上的新text增加sys_fee弹出窗口
		TParm parm = new TParm();
		parm.setData("ICD_TYPE", wc);
		textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popDiagReturn");
	}
	/**
	 * 新增处方签
	 */
	public void onNewRx(){
		chn.newPrsrp();
		initRxCombo();
	}
	/**
	 * 删除处方签
	 */
	public void onDeleteRx(){
		if(!chn.deletePrsrp(this.getValueString("REXP_NO"))){
			this.messageBox_("删除失败");
			return;
		}
		initRxCombo();
	}
	/**
	 * 添加SYS_FEE弹出窗口
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
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popOrderReturn");

	}

	/**
	 * 添加SYS_FEE弹出窗口
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
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popExaReturn");

	}

	/**
	 * 添加SYS_FEE弹出窗口
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
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popChnOrderReturn");

	}

	/**
	 * 新增诊断,判断是否可以做主诊断（中西医）
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
			this.messageBox_("该诊断已开立");// liudy
			return;
		}

		boolean allowMain = parm.getBoolean("MAIN_DIAG_FLG");
		if (diag.isHavaMainDiag() < 0 && allowMain) {
			if ("C".equalsIgnoreCase(wc) && !OdoUtil.isAllowChnDiag(parm)) {
				this.messageBox_("该诊断为症候，不能做为诊断");
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
	 * 新增西、成药
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
			this.messageBox_("以开立医嘱不允许修改名称");
			tableMed.setDSValue(row);
			tableMed.getTable().grabFocus();
			tableMed.setSelectedRow(tableMed.getRowCount()-1);
			tableMed.setSelectedColumn(tableMed.getColumnIndex("ORDER_DESC"));
			return;
		}
		if (order.isSameOrder(parm.getValue("ORDER_CODE"))) {
			if (this.messageBox("提示信息", "该医嘱已经开立，是否继续？", 0) == 1) {
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
	 * 新增西、成药
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
			if (this.messageBox("提示信息", "该医嘱已经开立，是否继续？", 0) == 1) {
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
	 * 新增中医
	 *
	 * @param tag
	 * @param obj
	 */
	public void popChnOrderReturn(String tag, Object obj) {

		TParm parm = (TParm) obj;

		String rxNo;
		if (StringUtil.isNullString(this.getValueString("CHN_FREQ_CODE"))) {
			this.messageBox_("请选择频次");
			return;
		}
		int row = tableChn.getSelectedRow();
		int oldRow = row;
		int column = tableChn.getSelectedColumn();
		String code = (String) tableChn.getValueAt(row, column);
		if (chn.isSameOrder(parm.getValue("ORDER_CODE"))) {
			if (this.messageBox("提示", "该医嘱已经开立，是否继续？", 0) == 1) {
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
	 * 中医TABL值改变事件
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
	 * 为中药TABLE新增一行
	 *
	 * @param rxNo
	 *            处方号
	 * @param row
	 *            行号
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
	 * 补足一行4味中药
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
			this.messageBox_("没有处方签");
			return false;
		}
		TParm tableParm=chn.getTableParm(rxNo);
		tableChn.setParmValue(tableParm);
		return true;
	}

	/**
	 * 主诊断点选事件，判断是否可作主诊断//liudy more remark
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

				this.messageBox_("该诊断为症候，不能做为诊断");

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
	 * 主表值改变事件
	 * @param obj
	 * @return
	 */
	public boolean onMainChangeValue(TTableNode tNode){
		int column=tableMain.getSelectedColumn();
		int columnInt=tableMain.getColumnIndex("PACK_DESC");
		int row=tNode.getRow();
		if(!"PACK_DESC".equalsIgnoreCase(tableMain.getParmMap(tNode.getColumn()))&&StringUtil.isNullString( main.getItemString(row, "PACK_DESC"))){
			this.messageBox_("没有模板名称");
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
//	 * 诊断中英文显示
//	 */
//	public void onChnEng() {
//		TCheckBox chnEng = (TCheckBox) this.getComponent("CHN_DSNAME");
//		if (chnEng.isSelected()) {
//			chnEng.setText("Chinese version");
//			tableDiag
//					.setHeader("主,30,boolean;Disease Code,80;Disease Name,150;备注,130");
//			tableDiag
//					.setParmMap("MAIN_DIAG_FLG;ICD_CODE;ICD_ENG_DESC;DIAG_NOTE");
//			tableDiag.setDSValue();
//		} else {
//			chnEng.setText("英文病名");
//			tableDiag.setHeader("主,30,boolean;疾病诊断,80;名称,150;备注,130");
//			tableDiag
//					.setParmMap("MAIN_DIAG_FLG;ICD_CODE;ICD_CHN_DESC;DIAG_NOTE");
//			tableDiag.setDSValue();
//		}
//	}

	/**
	 * 清空
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
		
		// 按科别、医师的类别初始化显示出来
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
	 * 连接医嘱勾选事件，如果本条医嘱为空，则不操作，如勾选为连接医嘱，则下一行的连接行自动带入，如原来的连接医嘱勾选为不连接，则取消原来的连接医嘱号
	 */
	public boolean onLinkMain(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int row = table.getSelectedRow();
		int column = table.getSelectedColumn();
		if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
			this.messageBox_("医嘱为空无法操作");
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
	 * 改变中西医标记的RADIO事件
	 *
	 * @param wc
	 *            String 诊断中西医标记
	 */
	public void onWc(String wc) {
		this.wc = wc;
	}

	/**
	 * 给公用 TABLE_NAME赋值，删除事件使用
	 *
	 * @param tableName
	 *            String
	 */
	public void onTableClick(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * 保存模板名称
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
	 * 删除事件，中药TABLE删除该列数据，重新显示该TABLE，其他TABLE判断是否最后一行并且是空行，如是则返回，如不是则删除该行数据
	 */
	public void onDelete() {
		if(tableName == null){

			if ("PARENT".equals(node.getType())) {
				String parentCode = node.getID();
				if(parentCode.length() == 3){
					String parentSql = "SELECT * FROM OPD_PACK_MAIN WHERE DEPT_OR_DR='"+deptOrDr+"' AND DEPTORDR_CODE='"+code+"'  AND PARENT_PACK_CODE='"+parentCode+"'";
					TParm childParm = new TParm(TJDODBTool.getInstance().select(parentSql));
					if(childParm.getCount() > 0){
						this.messageBox("该分类下有套餐模版，不能直接删除分类");
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
		// 非中药TABLE
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
		// 中药TABLE
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
	 * 处方签号改变事件
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
	 * 保存事件
	 */
	public void onSave() {
//		int row=tableMain.getSelectedRow();
//		if(row<0){
//			this.messageBox_("没有可保存的数据，请选择表格里的某行进行保存");
//			return;
//		}
		if (StringUtil.isNullString(this.getValueString("PACK_CODE"))) {
			this.messageBox_("没有可保存的数据");
			this.callFunction("UI|PACK_DESC|grabFocus");
			return;
		}
		if (StringUtil.isNullString(this.getValueString("PACK_DESC"))) {
			this.messageBox_("请填写模版名称");
			this.callFunction("UI|PACK_DESC|grabFocus");
			return;
		}
		
		if(this.getValueString("PARENT_PACK_CODE").length() > 0){
			if(getValueString("PARENT_PACK_CODE").length() != 3){
				this.messageBox("请填写正确的上级分类代码，例如001");
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
	 * 保存的实体
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
					this.messageBox("没有该分类，请填写已有分类");
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
					this.messageBox("没有该分类，请填写已有分类");
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
	 * 中医共用控件的回车事件，整体修改一张处方签里所有共用信息的值
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
			this.messageBox_("赋值失败");
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
				this.messageBox("不能在该目录下创建模版");
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
