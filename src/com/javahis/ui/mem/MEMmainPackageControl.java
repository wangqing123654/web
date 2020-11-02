package com.javahis.ui.mem;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import jdo.mem.MEMMainpackageTool;
import jdo.sys.Operator;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSRuleTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TMovePane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * 
 * <p>
 * Title: 套餐主档维护
 * </p>
 *s
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author duzhw 20131227
 * @version 4.5
 */
public class MEMmainPackageControl extends TControl {
	// 主、细TABLE
	private TTable table, detailTable;
	int maxseq = 0;
	String addPackageCode = "";
	TParm detailUpdateParm = new TParm();
	TParm detailAddParm = new TParm();
	boolean flag = false;
	String  newPackageCode = "";
	String  newSeq = "";
	//模板代码combo
	private TComboBox combo;
	/**
     * 树名称
     */
    private static final String TREE = "TREE";
	/**
     * 树根
     */
    private TTreeNode treeRoot;
    /**
     * 编号规则类别工具
     */
    SYSRuleTool ruleTool;
    
    /**
     * 树的数据放入datastore用于对树的数据管理
     */
    TDataStore treeDataStore = new TDataStore();
    TMenuItem menu;
	/**
	 * 初始化
	 */
	public void onInit() { // 初始化程序
		super.onInit();
		menu = (TMenuItem) this.getComponent("new");
    	menu.setEnabled(false);
		table = getTable("TABLE");
		detailTable = getTable("DETAIL_TABLE");

		// 套餐细相值改变事件
		detailTable.addEventListener("DETAIL_TABLE->"
				+ TTableEvent.CHANGE_VALUE, this, "changeValue");
		// TABLE表值改变事件
		table.addEventListener("TABLE->"
				+ TTableEvent.CHANGE_VALUE, this, "changeTableValue");
		detailTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComoponent");
		// 初始化树
        onInitSelectTree();
        // 种树
        onCreatTree();
//        //鼠标右键
//        this.callFunction("UI|" + TREE + "|addEventListener",
//                          TREE + "->" + TMouseListener.MOUSE_RIGHT_CLICKED, this,
//                          "mouseRightPressed");
        //树的监听事件
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");

		// 初始化组件
		initComponent();
		// 初始化数据
		initData();
		initCombo();
		
	}
	
	/**
	 * 初始化主Combo
	 */
	public void initCombo(){
		//取得combo用数据
		String sql = "SELECT ID,CHN_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID = 'MEM_PACKAGE_CLASS' ORDER BY ID";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
//			this.messageBox_(result.getErrText());
			this.messageBox_("找不到数据");
			return;
		}
		combo.setParmValue(result);
		combo.onInit();
	}

	/**
     * 
     */
	public void onCreateEditComoponent(Component com, int row, int column) {
		//this.messageBox("----come in onCreateEditComoponent------");
		String columnName = detailTable.getDataStoreColumnName(column);
		if (!"PACKAGE_DESC".equals(columnName))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		textFilter.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) // 判断按下的键是否是回车键
				{
					onDetailTableClick();
				}
			}
		});
	}

	/**
	 * 初始化控件
	 */
	public void initComponent() {
		combo=(TComboBox)this.getComponent("PACKAGE_CLASS");

	}

	/**
	 * 初始化数据
	 */
	public void initData() {
//		Timestamp now = StringTool.getTimestamp(new Date());
		// this.setValue("S_TIME",
		// now.toString().substring(0, 10).replace('-', '/'));// 开始时间
		// this.setValue("E_TIME",
		// now.toString().substring(0, 10).replace('-', '/'));// 结束时间

		initQuery();
	}

	/**
	 * 初始化查询
	 */
	public void initQuery() {
		String sql = this.getRootContentSql();
		//System.out.println("sssss:"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// if(result.getCount()<0){
		// this.messageBox("查无数据！");
		// return;
		// }

		// 套餐编码状态置无效
		callFunction("UI|PACKAGE_CODE|setEnabled", false);
		// 初始化查询清空系表
		onClear();
		table.setParmValue(result);
	}
	
	/**
	 * 获得根节点 的所有内容
	 * @return
	 */
	public String getRootContentSql(){
		String sql = "SELECT PACKAGE_CODE,PARENT_PACKAGE_CODE,PACKAGE_DESC,PACKAGE_ENG_DESC,PY1,PY2,SEQ,"
			+ " DESCRIPTION,ORIGINAL_PRICE,PACKAGE_PRICE,OPT_DATE,OPT_USER,OPT_TERM,START_DATE,END_DATE,LUMPWORK_CODE"
			+ " FROM MEM_PACKAGE WHERE PARENT_PACKAGE_CODE IS NULL   ORDER BY PACKAGE_CODE,SEQ";
		return  sql;
	}
	
	/**
     * 初始化树
     */
    public void onInitSelectTree() {
        // 得到树根
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        if (treeRoot == null)
            return;
        // 给根节点添加文字显示
        treeRoot.setText("套餐管理");
        // 给根节点赋tag
        treeRoot.setType("Root");
        // 设置根节点的id
        treeRoot.setID("");
        // 清空所有节点的内容
        treeRoot.removeAllChildren();
        // 调用树点初始化方法
        callMessage("UI|TREE|update");
    }
    /**
     * 初始化树上的节点
     */
    public void onCreatTree() {
        // 给dataStore赋值
        treeDataStore.setSQL("SELECT PACKAGE_CODE, PARENT_PACKAGE_CODE,PACKAGE_DESC,"
                             + " PACKAGE_ENG_DESC, PY1, PY2, SEQ,DESCRIPTION,"
                             + "  ORIGINAL_PRICE, PACKAGE_PRICE, OPT_DATE, OPT_USER,"
                             + "  OPT_TERM  FROM MEM_PACKAGE ORDER BY SEQ "
                             + "  ");
        // 如果从dataStore中拿到的数据小于0
        if (treeDataStore.retrieve() <= 0)
            return;
        // 过滤数据,是编码规则中的科室数据
        ruleTool = new SYSRuleTool("SYS_MEMPACKAGE");
        if (ruleTool.isLoad()) { // 给树篡节点参数:datastore，节点代码,节点显示文字,,节点排序
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                "PACKAGE_CODE", "PACKAGE_DESC", "Path", "SEQ");
            
            // 循环给树安插节点
            for (int i = 0; i < node.length; i++){
            	treeRoot.addSeq(node[i]);
//                System.out.println("node="+node[i]);
            }
                
        }
        // 得到界面上的树对象
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        // 更新树
        tree.update();
        // 设置树的默认选中节点
        tree.setSelectNode(treeRoot);
    }
//    /**
//     * 树右键事件
//     * @param e MouseEvent
//     */
//    public void mouseRightPressed(MouseEvent e) {
//    	this.openDialog("%ROOT%\\config\\mem\\MEMMainPackageTree.x");
//    }
//    
    /**
     * 常用选项关闭事件
     */
    public void onCloseChickedCY() {
        TMovePane mp = (TMovePane)this.getComponent("tMovePane_0");
        mp.onDoubleClicked(true);
    }
    /**
     * 树单击事件
     */
    public void onTreeClicked(){
    	newPackageCode = "";
    	menu.setEnabled(true);
    	//检核是否得到树的节点
        if (((TTree)getComponent("TREE")).getSelectNode() == null) {
            return;
        }
        this.clearValue("PACKAGE_CODE;PACKAGE_DESC;PACKAGE_ENG_DESC;PY1;PY2;SEQ;DESCRIPTION");
        //((TTextField) getComponent("PACKAGE_CODE")).setEnabled(true);
        this.onTreeQuery();
        
        this.setValue("tCheckBox_0", "N");
        this.setValue("tCheckBox_1", "Y");
    }
    /**
     * 点击树时将子类套餐放入套餐表格内
     */
	private void onTreeQuery() {
		String parentPackageCode = ((TTree)getComponent("TREE")).getSelectNode().getID();
		String sql = "SELECT 'N' AS XUAN,'N' AS FLG,ACTIVE_FLG,PACKAGE_CODE,PARENT_PACKAGE_CODE,PACKAGE_DESC,PACKAGE_ENG_DESC,PY1,PY2,SEQ,"
			+ " DESCRIPTION,ORIGINAL_PRICE,PACKAGE_PRICE,OPT_DATE,OPT_USER,OPT_TERM,START_DATE,END_DATE,LUMPWORK_CODE,PACKAGE_CLASS "
			+ " FROM MEM_PACKAGE WHERE 1=1 ";
		if(parentPackageCode.length()>0){
			sql += " AND PARENT_PACKAGE_CODE = '"+parentPackageCode+"'";
		}else{
			sql += " AND PARENT_PACKAGE_CODE IS NULL";
		}
		sql += " ORDER BY SEQ ";
		//System.out.println("sql:::::"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		table.setParmValue(result);
		for(int i = 0; i < result.getCount(); i++){
			table.setLockCellRow(i, true);
			table.setLockCell(i, "XUAN", false);
		}
		detailTable.removeRowAll();
	}
	
	/**
	 * 获取最大套餐编码
	 * @return
	 */
	public String getMaxPackageCode(String parentPackageCode){
		String sql = "SELECT MAX(PACKAGE_CODE) PACKAGE_CODE, MAX(SEQ) SEQ FROM MEM_PACKAGE WHERE 1=1 ";
		if(parentPackageCode.length()>0){
			sql += " AND PARENT_PACKAGE_CODE = '"+parentPackageCode+"'";
		}else{
			sql += " AND PARENT_PACKAGE_CODE IS NULL";
		}
		sql += " ORDER BY PACKAGE_CODE,SEQ ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		newSeq = (Integer.parseInt(result.getValue("SEQ",0))+1)+"";
		if(newPackageCode == ""){
			String packageCode = result.getValue("PACKAGE_CODE",0);
			if(!packageCode.equals("") && packageCode!=null){
				if(packageCode.length()==2 && (Integer.parseInt(packageCode)+1) > 10){//第一集目录且大于10
					newPackageCode = (Integer.parseInt(packageCode)+1)+"";
				}else{
					newPackageCode = "0"+(Integer.parseInt(packageCode)+1);
				}
			}else{
				newPackageCode = ((TTree)getComponent("TREE")).getSelectNode().getID()+"01";
			}
		}else{
			if(newPackageCode.length()==2 && (Integer.parseInt(newPackageCode)+1) >= 10){//第一集目录且大于10
				newPackageCode = (Integer.parseInt(newPackageCode)+1)+"";
			}else{
				newPackageCode = "0"+(Integer.parseInt(newPackageCode)+1);
			}
		}
		return newPackageCode;
	}
	
	/**
	 * 查询操作
	 */
	public void onQuery() {
		String packageCode = this.getValueString("PACKAGE_CODE");
		String packageDesc = this.getValueString("PACKAGE_DESC");

		String sql = "SELECT 'N' AS XUAN, ACTIVE_FLG,PACKAGE_CODE,PARENT_PACKAGE_CODE,PACKAGE_DESC,PACKAGE_ENG_DESC,PY1,PY2,SEQ,"
				+ " DESCRIPTION,ORIGINAL_PRICE,PACKAGE_PRICE,OPT_DATE,OPT_USER,OPT_TERM,START_DATE,END_DATE"
				+ " FROM MEM_PACKAGE WHERE PARENT_PACKAGE_CODE IS NULL   ";
		if (packageCode.length() > 0) {
			sql += " AND PACKAGE_CODE = '" + packageCode + "' ";
		}
		if (packageDesc.length() > 0) {
			sql += " AND PACKAGE_DESC LIKE '%" + packageDesc + "%' ";
		}
		sql += " ORDER BY PACKAGE_CODE, SEQ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() < 0) {
			this.messageBox("查无数据！");
			return;
		}

		// 套餐编码状态置无效
//		callFunction("UI|PACKAGE_CODE|setEnabled", false);//delete by sunqy 20140723
		table.setParmValue(result);
	}

	/**
	 * 保存操作
	 */
	public void onSave() {
		try{
			//日历回车问题，取消编辑状态  TEST
			if(detailTable.getTable().isEditing()){
				detailTable.getTable().getCellEditor().stopCellEditing();			
				//detailTable.getTable().getCellEditor(detailTable.getSelectedRow(),detailTable.getSelectedColumn()).stopCellEditing();
			}
			//
			String oper = "UPDATE";
			TTextField packageCodeFlag = (TTextField) this.getComponent("PACKAGE_CODE");
			//boolean flag = packageCodeFlag.isEnabled();
			
//			 System.out.println("状态："+flag);
			if (flag) {
				oper = "ADD";
			}
			if(!flag){
				int row = table.getSelectedRow();
				if(row < 0 && this.getValueString("PACKAGE_CODE").equals("")){
					this.messageBox("请选择一条数据");
					return;
				}
			}
			// 获取页面数据
			String seq = this.getValueString("SEQ");
	//		int seq = 0;
			String parentPackageCode = ((TTree)getComponent("TREE")).getSelectNode().getID();
			String packageCode = this.getValueString("PACKAGE_CODE");
			String packageDesc = this.getValueString("PACKAGE_DESC");
			String packageEngDesc = this.getValueString("PACKAGE_ENG_DESC");
			String py1 = this.getValueString("PY1");
			String py2 = this.getValueString("PY2");
			String description = this.getValueString("DESCRIPTION");
	
			String userId = Operator.getID();
			String optTerm = Operator.getIP();
			Timestamp date = StringTool.getTimestamp(new Date());
			
			//add by yangjj 20150407 
			String lumpworkCode = this.getValueString("LUMPWORK_CODE");
			//活动套餐标记 add by huangjw 20160608
			String activeFlg = this.getValueString("ACTIVE_FLG");
			String packageClass = this.getValueString("PACKAGE_CLASS");
			TParm parm = new TParm();
			// ----------新增操作--------
			if ("ADD".equals(oper)) {
				TParm tableParm = table.getParmValue();
				for(int i = 0; i < tableParm.getCount(); i++){
					if(tableParm.getValue("FLG",i).equals("Y") && tableParm.getValue("PACKAGE_CODE",i).equals("")){
						this.messageBox("套餐名称不能为空！");
						return;
					}
					if(tableParm.getValue("FLG",i).equals("Y")){
						parm.addData("OPER", oper);
						parm.addData("PARENT_PACKAGE_CODE", parentPackageCode);
						parm.addData("PACKAGE_CODE", tableParm.getValue("PACKAGE_CODE",i));
						parm.addData("PACKAGE_DESC", tableParm.getValue("PACKAGE_DESC",i));
						parm.addData("PACKAGE_ENG_DESC", tableParm.getValue("PACKAGE_ENG_DESC",i));
						parm.addData("PY1", tableParm.getValue("PY1",i));
						parm.addData("PY2", "");
						parm.addData("SEQ", tableParm.getValue("SEQ",i));
						parm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION",i));
						parm.addData("OPT_USER", tableParm.getValue("OPT_USER",i));
						parm.addData("OPT_DATE", tableParm.getValue("OPT_DATE",i).toString().replaceAll("-", "/").substring(0, 10));
						parm.addData("OPT_TERM", tableParm.getValue("OPT_TERM",i));
						parm.addData("LUMPWORK_CODE", tableParm.getValue("LUMPWORK_CODE",i));
						parm.addData("ACTIVE_FLG", tableParm.getValue("ACTIVE_FLG",i));
						parm.addData("PACKAGE_CLASS", packageClass);
						
					}
				}
				//add by huangtt 20160809 在界面上输入信息也可以进行保存
				if(parm.getCount("OPER")<0){
					if(packageCode.length() == 0){
						this.messageBox("套餐名称不能为空！");
						return;
					}
					parm.addData("OPER", oper);
					parm.addData("PARENT_PACKAGE_CODE", parentPackageCode);
					parm.addData("PACKAGE_CODE", packageCode);
					parm.addData("PACKAGE_DESC", packageDesc);
					parm.addData("PACKAGE_ENG_DESC", packageEngDesc);
					parm.addData("PY1", py1);
					parm.addData("PY2", py2);
					parm.addData("SEQ", seq);
					parm.addData("DESCRIPTION", description);
					parm.addData("OPT_USER", userId);
					parm.addData("OPT_DATE", date.toString().replaceAll("-", "/").substring(0, 10));
					parm.addData("OPT_TERM", optTerm);
					parm.addData("LUMPWORK_CODE", lumpworkCode);
					parm.addData("ACTIVE_FLG", activeFlg);
					parm.addData("PACKAGE_CLASS", packageClass);
				}
				
			}
			if("UPDATE".equals(oper)){
				
				parm.addData("OPER", oper);
				parm.addData("PARENT_PACKAGE_CODE", parentPackageCode);
				parm.addData("PACKAGE_CODE", packageCode);
				parm.addData("PACKAGE_DESC", packageDesc);
				parm.addData("PACKAGE_ENG_DESC", packageEngDesc);
				parm.addData("PY1", py1);
				parm.addData("PY2", py2);
				parm.addData("SEQ", seq);
				parm.addData("DESCRIPTION", description);
				// System.out.println("@@@@@@@@startDate=="+startDate);
				// parm.addData("START_DATE", startDate);
				// System.out.println("@@@@@@@@endDate=="+endDate);
				// parm.addData("END_DATE", endDate);
				parm.addData("OPT_USER", userId);
				parm.addData("OPT_DATE", date.toString().replaceAll("-", "/").substring(0, 10));
				parm.addData("OPT_TERM", optTerm);
				
				//add by yangjj 20150407
				parm.addData("LUMPWORK_CODE", lumpworkCode);
				parm.addData("ACTIVE_FLG", activeFlg);//add by huangjw 20160608
				parm.addData("PACKAGE_CLASS", packageClass);
			}
			
			// 保存主表信息
			System.out.println("parm--"+parm);
			TParm result = TIOM_AppServer.executeAction(
					"action.mem.MEMMainPackageAction", "onSavePackageData", parm);
			//for(int i = 0; i < parm.getCount("OPER"); i++){
			//result = new TParm(TJDODBTool.getInstance().update(parm));
			//}
			TParm updateAllParm = new TParm();
			if (result.getErrCode() < 0) {
				this.messageBox("保存失败！");
				return;
			} else {
				if ("ADD".equals(parm.getValue("OPER", 0))) {
					this.messageBox("保存成功！");
					onInit();
					return;
				}
	//			if("UPDATE".equals(parm.getValue("OPER", 0))){
	//				 onInit();
	//				 return;
	//			}
				// 保存细表信息
				TParm all = detailTable.getParmValue();
				// int row = table.getSelectedRow();
				String flgIn = "";
				String flgUp = "";
				TParm result2 = new TParm();
				int detailTableSelRow = detailTable.getSelectedRow();
				//System.out.println("all------"+all+"     count----"+all.getCount());
				if (all != null && all.getCount() > 1 && detailTableSelRow >= 0) {
					Timestamp startDate = detailTable.getItemTimestamp(detailTable.getSelectedRow(), "START_DATE");// this.getValueString("S_TIME");
					Timestamp endDate = detailTable.getItemTimestamp(detailTable.getSelectedRow(), "END_DATE");// this.getValueString("E_TIME");
					String sDate = "";
					String eDate = "";
					if ((startDate == null || startDate.equals("")) && detailTable.getSelectedRow() != -1 ) {
						// startDate = startDate.replaceAll("-", "/").substring(0,
						// 10);
						// if(startDate.length()<=0){
						this.messageBox("请录入套餐开始时间!");
						//detailTable.removeRowAll();
						return;
						// }
					} else {
						sDate = startDate.toString();
						if ((sDate != null || !"".equals(sDate)) && detailTable.getSelectedRow() != -1){
							sDate = sDate.replaceAll("-", "/").substring(0, 10);
						}
					}
					if ((endDate == null || endDate.equals("")) && detailTable.getSelectedRow() != -1 ) {
						this.messageBox("请录入套餐结束时间!");
						//detailTable.removeRowAll();
						return;
					} else {
						eDate = endDate.toString();
						if ((eDate != null || !"".equals(eDate)) && detailTable.getSelectedRow() != -1){
							eDate = eDate.replaceAll("-", "/").substring(0, 10);
						}
					}
					// 比较开始时间与结束时间的大小
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
					try {
						Date startdate = sdf.parse(sDate);
						Date enddate = sdf.parse(eDate);
						int i = enddate.compareTo(startdate);
						// System.out.println("startdate=="+startdate+"    enddate=="+enddate+"    i=="+i);
						if (i <= 0) {
							this.messageBox("开始时间与结束时间不符合要求!");
							return;
						}
					} catch (ParseException e) {
					}
					//
					for (int i = 0; i <= all.getCount() - 1; i++) {
						String exec = all.getValue("EXEC", i);
						String currPackageCode = detailTable.getItemString(i,
								"PACKAGE_CODE");
						if (!"Y".equals(exec)) {// 新增数据
							flgIn = "INSERT";
							if (currPackageCode.length() > 0) {
								
	//							// 获取PY1
	//							String desc = detailTable.getItemString(i,
	//									"PACKAGE_DESC");
								// String Py = SYSHzpyTool.getInstance().charToCode(
								// TypeTool.getString(desc));
								detailAddParm.addData("PACKAGE_CODE", detailTable.getItemString(i, "PACKAGE_CODE"));
								// M表选中行的套餐代码作为上级代码
								detailAddParm.addData("PARENT_PACKAGE_CODE", this.getValueString("PACKAGE_CODE"));
								detailAddParm.addData("PACKAGE_DESC", detailTable.getItemString(i, "PACKAGE_DESC"));
								detailAddParm.addData("PACKAGE_ENG_DESC",detailTable.getItemString(i,"PACKAGE_ENG_DESC"));
								detailAddParm.addData("SEQ", detailTable.getItemString(i, "SEQ"));
								// detailAddParm.addData("PY1", Py);
								detailAddParm.addData("PY1", detailTable.getItemString(i, "PY1"));
								detailAddParm.addData("DESCRIPTION", detailTable.getItemString(i, "DESCRIPTION"));
								detailAddParm.addData("ORIGINAL_PRICE", detailTable.getItemString(i, "ORIGINAL_PRICE"));
								detailAddParm.addData("PACKAGE_PRICE", detailTable.getItemString(i, "PACKAGE_PRICE"));
								detailAddParm.addData("START_DATE", detailTable.getItemString(i, "START_DATE"));
								detailAddParm.addData("END_DATE", detailTable.getItemString(i, "END_DATE"));
								detailAddParm.addData("OPT_USER", detailTable.getItemString(i, "OPT_USER"));
								detailAddParm.addData("OPT_DATE", detailTable.getItemString(i, "OPT_DATE").replaceAll("-", "/").substring(0, 10));
								detailAddParm.addData("OPT_TERM", detailTable.getItemString(i, "OPT_TERM"));
								
								//add by yangjj 20150407
								detailAddParm.addData("LUMPWORK_CODE", detailTable.getItemString(i, "LUMPWORK_CODE"));
								
								detailAddParm.addData("ACTIVE_FLG", detailTable.getItemString(i, "ACTIVE_FLG"));
								
							}
							
						}
					}
					//for (int j = 0; j <= all.getCount() - 1; j++) {
						String exec = all.getValue("EXEC", detailTableSelRow);
						if ("Y".equals(exec) ) {// 修改数据
							flgUp = "UPDATE";
							updateAllParm.addData("PACKAGE_CODE", detailTable
									.getItemString(detailTableSelRow, "PACKAGE_CODE"));
							updateAllParm.addData("PARENT_PACKAGE_CODE",
									detailTable.getItemString(detailTableSelRow,
											"PARENT_PACKAGE_CODE"));
							updateAllParm.addData("PACKAGE_DESC", detailTable
									.getItemString(detailTableSelRow, "PACKAGE_DESC"));
							updateAllParm.addData("PACKAGE_ENG_DESC", detailTable
									.getItemString(detailTableSelRow, "PACKAGE_ENG_DESC"));
							updateAllParm.addData("PY1", detailTable.getItemString(
									detailTableSelRow, "PY1"));
							updateAllParm.addData("DESCRIPTION", detailTable
									.getItemString(detailTableSelRow, "DESCRIPTION"));
							updateAllParm.addData("ORIGINAL_PRICE", detailTable
									.getItemString(detailTableSelRow, "ORIGINAL_PRICE"));
							updateAllParm.addData("PACKAGE_PRICE", detailTable
									.getItemString(detailTableSelRow, "PACKAGE_PRICE"));
							updateAllParm.addData("START_DATE", detailTable
									.getItemString(detailTableSelRow, "START_DATE"));
							// System.out.println("!!!!!!!!!!startDate======"+detailTable.getItemString(j,
							// "START_DATE"));
							updateAllParm.addData("END_DATE", detailTable
									.getItemString(detailTableSelRow, "END_DATE"));
							// System.out.println("!!!!!!!!!!endDate======"+detailTable.getItemString(j,
							// "END_DATE"));
							updateAllParm.addData("OPT_USER", detailTable
									.getItemString(detailTableSelRow, "OPT_USER"));
							updateAllParm.addData("OPT_DATE", detailTable
									.getItemString(detailTableSelRow, "OPT_DATE").replaceAll("-",
											"/").substring(0, 10));
							updateAllParm.addData("OPT_TERM", detailTable
									.getItemString(detailTableSelRow, "OPT_TERM"));
							
							//add by yangjj 20150407
							updateAllParm.addData("LUMPWORK_CODE", detailTable
									.getItemString(detailTableSelRow, "LUMPWORK_CODE"));
							
							
							updateAllParm.addData("ACTIVE_FLG", detailTable
									.getItemString(detailTableSelRow, "ACTIVE_FLG"));
							
						}
						
	
					//}
					//System.out.println("保存--新增" + detailAddParm);
					// System.out.println("保存--修改："+updateAllParm);
					if (flgIn == "INSERT" || "INSERT".equals(flgIn)) {
						
						for (int i = 0; i < detailAddParm.getCount("PACKAGE_CODE"); i++) {
							// System.out.println("--------i"+i);
							//add by yangjj 20150408
							if(detailAddParm.getData("START_DATE", i).equals("")){
								continue;
							}
							result2 = new TParm(TJDODBTool.getInstance().update(
									insertSql(detailAddParm, i)));
							// System.out.println("--------i[]"+i);
						}
					}
					
					if (flgUp == "UPDATE" || "UPDATE".equals(flgUp)) {
						for (int j = 0; j < updateAllParm.getCount("PACKAGE_CODE"); j++) {
							//if(detailTableSelRow == j){
								result2 = new TParm(TJDODBTool.getInstance().update(
										updateSql(updateAllParm, j)));
							//}
						}
					}
					// System.out.println("-0-0-0-000-0-0result2===="+result2);
					// System.out.println("-------"+result2.getErrCode());
					if (result2.getErrCode() < 0) {
						this.messageBox("保存失败！");
						return;
					} else {
						detailUpdateParm = new TParm();
						detailAddParm = new TParm();
						this.messageBox("保存成功！");
					}
				} else {
					this.messageBox("保存成功！");
				}
			}
			onInit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 删除
	 *//*
	public void onDelete() {
		int tableIndx = table.getSelectedRow();
		int selectedIndx = detailTable.getSelectedRow();
		String packageCode = this.getValueString("PACKAGE_CODE");
		TParm sectionResult = new TParm();
		if (selectedIndx < 0) {
			if (packageCode.length() == 0) {
				this.messageBox("无删除数据！");
			} else {
				if (tableIndx >= 0) {
					// 校验是否可删，可以
					if (ifDeleteSql(packageCode)) {
						if (JOptionPane.showConfirmDialog(null, "是否删除该条数据？",
								"信息", JOptionPane.YES_NO_OPTION) == 0) {
							//需求861--杨老师：在套餐主档页面，删除套餐时，应判断是否有时程信息，有的话提示不能删除add by sunqy 20140808 ----start----
							sectionResult = new TParm(
									TJDODBTool.getInstance().select("SELECT * FROM MEM_PACKAGE_SECTION WHERE PACKAGE_CODE = '"
											+ table.getItemString(tableIndx,"PACKAGE_CODE") +"'"));
							if(sectionResult.getCount()>0){
								this.messageBox("该项套餐有时程,不允许删除");
								return;
							}
							//需求861--杨老师：在套餐主档页面，删除套餐时，应判断是否有时程信息，有的话提示不能删除add by sunqy 20140808 ----end----
							TParm result = new TParm(
									TJDODBTool
											.getInstance()
											.update(
													""
															+ "DELETE FROM MEM_PACKAGE WHERE PACKAGE_CODE = '"
															+ packageCode + "'"));
							// System.out.println("---DELETE FROM MEM_PACKAGE WHERE PACKAGE_CODE = '"+packageCode+"'");
							if (result.getErrCode() < 0) {
								this.messageBox("删除失败！");
								return;
							} else {
								this.messageBox("删除成功！");
								onInit();
							}
						}
					} else {
						// 不可以删除
						this.messageBox("不可以删除！");
					}

				}
			}
		} else {// 删除细表数据，放到deleteParm中
			// 添加系表删除方法
			if (JOptionPane.showConfirmDialog(null, "是否删除该条数据？", "信息",
					JOptionPane.YES_NO_OPTION) == 0) {
				//需求861--杨老师：在套餐主档页面，删除套餐时，应判断是否有时程信息，有的话提示不能删除add by sunqy 20140723 ----start----
				sectionResult = new TParm(
						TJDODBTool.getInstance().select("SELECT * FROM MEM_PACKAGE_SECTION WHERE PACKAGE_CODE = '"
								+ detailTable.getItemString(selectedIndx,"PACKAGE_CODE") +"'"));
				if(sectionResult.getCount()>0){
					this.messageBox("该项套餐有时程,不允许删除");
					return;
				}
				//需求861--杨老师：在套餐主档页面，删除套餐时，应判断是否有时程信息，有的话提示不能删除add by sunqy 20140723 ----end----
				TParm result = new TParm(
						TJDODBTool.getInstance().update("" + "DELETE FROM MEM_PACKAGE WHERE PACKAGE_CODE = '" 
								+ detailTable.getItemString(selectedIndx,"PACKAGE_CODE") + "'"));
				// System.out.println("---DELETE FROM MEM_PACKAGE WHERE PACKAGE_CODE = '"+packageCode+"'");
				if (result.getErrCode() < 0) {
					this.messageBox("删除失败！");
					return;
				} else {
					this.messageBox("删除成功！");
					onInit();
				}
			}

		}

	}
*/
	public void onDelete() {
		int tableIndx = table.getSelectedRow();
		int selectedIndx = detailTable.getSelectedRow();
		String packageCode = this.getValueString("PACKAGE_CODE");
		TParm sectionResult = new TParm();
		if (selectedIndx < 0) {
			if (packageCode.length() == 0) {
				this.messageBox("无删除数据！");
			} else {
				if (tableIndx >= 0) {
					// 校验是否可删，可以
						if (JOptionPane.showConfirmDialog(null, "套餐内有项目，是否删除？",
								"信息", JOptionPane.YES_NO_OPTION) == 0) {
							//需求861--杨老师：在套餐主档页面，删除套餐时，应判断是否有时程信息，有的话提示不能删除add by sunqy 20140808 ----start----
							sectionResult = new TParm(
									TJDODBTool.getInstance().select("SELECT * FROM MEM_PACKAGE_SECTION WHERE PACKAGE_CODE = '"+ packageCode +"'"));
							if(sectionResult.getCount()>0){
								this.messageBox("该项套餐有时程,不允许删除");
								return;
							}
							for(int i = 0; i < detailTable.getParmValue().getCount(); i++){
								sectionResult = new TParm(
										TJDODBTool.getInstance().select("SELECT * FROM MEM_PACKAGE_SECTION WHERE PACKAGE_CODE = "+ "'"+detailTable.getItemString(i,"PACKAGE_CODE")+"'"));
								if(sectionResult.getCount()>0){
									this.messageBox("子节点套餐有时程,不允许删除");
									return;
								}
								sectionResult = new TParm(TJDODBTool.getInstance().select("SELECT * FROM MEM_PACKAGE WHERE PARENT_PACKAGE_CODE = '"+detailTable.getItemString(i,"PACKAGE_CODE")+"'"));
								if(sectionResult.getCount()>0){
									this.messageBox("子节点套餐存在子套餐,不允许删除");
									return;
								}
							}
							//需求861--杨老师：在套餐主档页面，删除套餐时，应判断是否有时程信息，有的话提示不能删除add by sunqy 20140808 ----end----
							TParm result = new TParm(
									TJDODBTool
											.getInstance()
											.update(
													""
															+ "DELETE FROM MEM_PACKAGE WHERE PACKAGE_CODE = '"
															+ packageCode + "' OR PARENT_PACKAGE_CODE = '"+ packageCode +"'"));
							// System.out.println("---DELETE FROM MEM_PACKAGE WHERE PACKAGE_CODE = '"+packageCode+"'");
							if (result.getErrCode() < 0) {
								this.messageBox("删除失败！");
								return;
							} else {
								this.messageBox("删除成功！");
								onInit();
							}
						}
				}
			}
		} else {// 删除细表数据，放到deleteParm中
			// 添加系表删除方法
			if (JOptionPane.showConfirmDialog(null, "是否删除该条数据？", "信息",
					JOptionPane.YES_NO_OPTION) == 0) {
				//需求861--杨老师：在套餐主档页面，删除套餐时，应判断是否有时程信息，有的话提示不能删除add by sunqy 20140723 ----start----
				sectionResult = new TParm(
						TJDODBTool.getInstance().select("SELECT * FROM MEM_PACKAGE_SECTION WHERE PACKAGE_CODE = '"
								+ detailTable.getItemString(selectedIndx,"PACKAGE_CODE") +"'"));
				if(sectionResult.getCount()>0){
					this.messageBox("该项套餐有时程,不允许删除");
					return;
				}
				//需求861--杨老师：在套餐主档页面，删除套餐时，应判断是否有时程信息，有的话提示不能删除add by sunqy 20140723 ----end----
				TParm result = new TParm(
						TJDODBTool.getInstance().update("" + "DELETE FROM MEM_PACKAGE WHERE PACKAGE_CODE = '" 
								+ detailTable.getItemString(selectedIndx,"PACKAGE_CODE") + "'"));
				// System.out.println("---DELETE FROM MEM_PACKAGE WHERE PACKAGE_CODE = '"+packageCode+"'");
				if (result.getErrCode() < 0) {
					this.messageBox("删除失败！");
					return;
				} else {
					this.messageBox("删除成功！");
					onInit();
				}
			}

		}

	}

	/**
	 * 清除
	 */
	public void onClear() {
		flag = false;
		this.setValue("tCheckBox_0", "N");
		this.setValue("tCheckBox_1", "Y");
		// 套餐编码状态置有效
		callFunction("UI|PACKAGE_CODE|setEnabled", true);
		this.clearValue("PACKAGE_CODE;PACKAGE_DESC;PACKAGE_ENG_DESC;PY1;PY2;DESCRIPTION;SEQ;LUMPWORK_CODE;ACTIVE_FLG;PACKAGE_CLASS");
		table.removeRowAll();
		detailTable.removeRowAll();
		menu.setEnabled(false);
		newPackageCode = "";
		initCombo();
	}

	/**
	 * 主TABLE点击事件,根据选定的套餐代码，初始化套餐细相信息
	 */
	public void onMainClick() {
		if(!getFlagPackageCode(this.getValueString("PACKAGE_CODE"))){
			flag = false;//false 代表是更新
		}
		//flag = false;
		// 套餐编码输入框置无效
		callFunction("UI|PACKAGE_CODE|setEnabled", false);
		detailTable.removeRowAll();
		int selectedIndx = table.getSelectedRow();
		if (selectedIndx < 0) {
			return;
		}
		TParm tableparm = table.getParmValue();
		String packageCode = tableparm.getValue("PACKAGE_CODE", selectedIndx);
		String packageDesc = tableparm.getValue("PACKAGE_DESC", selectedIndx);
		String packageEngDesc = tableparm.getValue("PACKAGE_ENG_DESC",
				selectedIndx);
		String py1 = tableparm.getValue("PY1", selectedIndx);
		String py2 = tableparm.getValue("PY2", selectedIndx);
		String seq = tableparm.getValue("SEQ", selectedIndx);
		String description = tableparm.getValue("DESCRIPTION", selectedIndx);
		String activeFlg = tableparm.getValue("ACTIVE_FLG", selectedIndx);
		String packageClass = tableparm.getValue("PACKAGE_CLASS", selectedIndx); //add by huangtt 20160711
		// String startDate = tableparm.getValue("START_DATE",selectedIndx);
		// String endDate = tableparm.getValue("END_DATE",selectedIndx);

		this.setValue("PACKAGE_CODE", packageCode);
		this.setValue("PACKAGE_DESC", packageDesc);
		this.setValue("PACKAGE_ENG_DESC", packageEngDesc);
		this.setValue("PY1", py1);
		this.setValue("PY2", py2);
		this.setValue("DESCRIPTION", description);
		this.setValue("SEQ", seq);
		this.setValue("ACTIVE_FLG", activeFlg);
		this.setValue("PACKAGE_CLASS", packageClass);
		// if(startDate.length()>0){
		// this.setValue("S_TIME", startDate.replaceAll("-", "/").substring(0,
		// 10));
		// }else{
		// this.setValue("S_TIME", "");
		// }
		// if(endDate.length()>0){
		// this.setValue("E_TIME", endDate.replaceAll("-", "/").substring(0,
		// 10));
		// }else{
		// this.setValue("E_TIME", "");
		// }
		// 主表是主表的序号加
		// 系表是系表的序号加
		String sql = "SELECT 'Y' AS EXEC,ACTIVE_FLG,PACKAGE_CODE,PARENT_PACKAGE_CODE,PACKAGE_DESC,PACKAGE_ENG_DESC,PY1,PY2,SEQ,"
				+ " DESCRIPTION,ORIGINAL_PRICE,PACKAGE_PRICE,OPT_DATE,OPT_USER,OPT_TERM,START_DATE,END_DATE,LUMPWORK_CODE "
				+ " FROM MEM_PACKAGE " 
				+ " WHERE PARENT_PACKAGE_CODE = '"
				+ packageCode + "' ORDER BY SEQ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		//add by yangjj 20150407
		String sql1 = "SELECT 'Y' AS EXEC,ACTIVE_FLG,PACKAGE_CODE,PARENT_PACKAGE_CODE,PACKAGE_DESC,PACKAGE_ENG_DESC,PY1,PY2,SEQ,"
			+ " DESCRIPTION,ORIGINAL_PRICE,PACKAGE_PRICE,OPT_DATE,OPT_USER,OPT_TERM,START_DATE,END_DATE,LUMPWORK_CODE "
			+ " FROM MEM_PACKAGE " 
			+ " WHERE PACKAGE_CODE = '"
			+ packageCode + "' ORDER BY SEQ";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		
		this.setValue("LUMPWORK_CODE", result1.getData("LUMPWORK_CODE", 0));
		// System.out.println("--result"+result);
		// 查出来数据的是系表
		if (result.getCount() < 0) {
			detailTable.setParmValue(new TParm());
			// 主表
			// this.messageBox("查无数据！");
			// 获取序号SEQ
			int seq2 = getMaxSeq("SEQ", "MEM_PACKAGE", "", "",
					"PARENT_PACKAGE_CODE", "");
			maxseq = seq2;
			addPackageCode = getPackageCode(this.getValueString("PACKAGE_CODE"));
			insertRow("DETAIL_TABLE");

			return;
		}
		detailTable.setParmValue(result);
		// 获取序号SEQ
		int seq2 = getMaxSeq("SEQ", "MEM_PACKAGE", "", "",
				"PARENT_PACKAGE_CODE", packageCode);
		maxseq = seq2;
		addPackageCode = getPackageCode(this.getValueString("PACKAGE_CODE"));
		insertRow("DETAIL_TABLE");

	}

	/**
	 * 关闭
	 */
	// public void onClose(){
	// TParm result = new TParm();
	// result.setData("OPER", "");
	// this.setReturnValue(result);
	// //this.closeWindow();
	// }
	/**
	 * 得到最大的编号 +1
	 * 
	 * @param dataStore
	 *            TDataStore
	 * @param columnName
	 *            String
	 * @return String
	 */
	public int getMaxSeq(String maxValue, String tableName, String where1,
			String value1, String where2, String value2) {
		String sql = " SELECT MAX(" + maxValue + ") AS " + maxValue + " "
				+ " FROM " + tableName + " WHERE 1=1 ";
		if (where1.trim().length() > 0) {
			sql += " AND " + where1 + " ='" + value1 + "'";
		}
		if (where2.trim().length() > 0) {
			sql += " AND " + where2 + " ='" + value2 + "'";
		}
		// System.out.println("SQL"+sql);
		// 保存最大号
		int max = 0;
		// 查询最大序号
		TParm seqParm = new TParm(TJDODBTool.getInstance().select(sql));
		String seq = seqParm.getValue(maxValue, 0).toString() == null ? "0"
				: seqParm.getValue(maxValue, 0).toString();
		int value = Integer.parseInt(seq);

		// 保存最大值
		if (max < value) {
			max = value;
		}
		// 最大号加1
		max++;
		return max;

	}

	/**
	 * 比对输入套餐编号是否可用
	 */
	public boolean getFlagPackageCode(String value) {
		boolean flag = false;
		String sql = " SELECT PACKAGE_CODE FROM MEM_PACKAGE WHERE PACKAGE_CODE = '"
				+ value + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		flag = parm.getValue("PACKAGE_CODE", 0).toString().length() > 0 ? false
				: true;
		return flag;

	}

	/**
	 * 得到页面中Table对象
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tag) {
		return (TTable) callFunction("UI|" + tag + "|getThis");
	}

	/**
	 * 添加一行新数据-医嘱表
	 */
	public void insertRow(String opertable) {
		TTable table = (TTable) callFunction("UI|" + opertable + "|getThis");
		table.acceptText();
		int row = table.addRow();
		// int oldrow = table.getRowCount()-1;
		// System.out.println("detailTable的套餐编码："+table.getParmValue().getValue("PACKAGE_CODE",
		// row));
		table.setItem(row, "PACKAGE_CODE", addPackageCode);
		// System.out.println("detailTable的序号："+table.getParmValue().getValue("SEQ",
		// oldrow));
		table.setItem(row, "SEQ", maxseq);
		table.setItem(row, "OPT_USER", Operator.getID());
		Timestamp date = StringTool.getTimestamp(new Date());
		table.setItem(row, "OPT_DATE", date);
		table.setItem(row, "OPT_TERM", Operator.getIP());

	}

	/**
	 * 细表修改事件-值改变事件
	 */
	public boolean changeValue(TTableNode tNode) {
		// int selectedIndx=detailTable.getSelectedRow();
		// detailTable.acceptText();
		int selectedIndx = tNode.getRow();
		TParm tableparm = detailTable.getParmValue();
		String packageCode = tableparm.getValue("PACKAGE_CODE", selectedIndx);
		String exec = tableparm.getValue("EXEC", selectedIndx);
		boolean flag = false;
		if ("Y".equals(exec)) {// 老数据
			for (int i = 0; i < detailUpdateParm.getCount("PACKAGE_CODE"); i++) {
				String a = detailUpdateParm.getValue("PACKAGE_CODE", i);
				if (a.equals(packageCode)) {
					flag = true;
				}
			}
			if (!flag) {
				detailUpdateParm.addData("PACKAGE_CODE", detailTable
						.getItemString(selectedIndx, "PACKAGE_CODE"));
				// detailUpdateParm.addData("PACKAGE_DESC",
				// detailTable.getItemString(selectedIndx, "PACKAGE_DESC"));
				// detailUpdateParm.addData("PACKAGE_ENG_DESC",
				// detailTable.getItemString(selectedIndx, "PACKAGE_ENG_DESC"));
				// detailUpdateParm.addData("PY1",
				// detailTable.getItemString(selectedIndx, "PY1"));
				// detailUpdateParm.addData("DESCRIPTION",
				// detailTable.getItemString(selectedIndx, "DESCRIPTION"));
				// detailUpdateParm.addData("ORIGINAL_PRICE",
				// detailTable.getItemString(selectedIndx, "ORIGINAL_PRICE"));
				// detailUpdateParm.addData("PACKAGE_PRICE",
				// detailTable.getItemString(selectedIndx, "PACKAGE_PRICE"));

			}
		}
		// 拿到table上的parmmap的列名
		String columnName = detailTable.getDataStoreColumnName(tNode
				.getColumn());
		// 得到改变的行
		int row = tNode.getRow();
		// 拿到当前改变后的数据
		String value = "" + tNode.getValue();
		// 如果是名称改变了拼音1自动带出,并且名称不能为空
		if ("PACKAGE_DESC".equals(columnName)) {
			if (value.equals("") || value == null) {
				messageBox_("名称不能为空!");
				return true;
			}
			// 获取PY1
			String py = SYSHzpyTool.getInstance().charToCode(value);
			detailTable.setItem(row, "PY1", py);
		}
		
		return false;
	}
	/**
	 * 主表修改事件
	 * @return
	 */
	public boolean changeTableValue(TTableNode tNode){
		
		// 拿到table上的parmmap的列名
		String columnName = table.getDataStoreColumnName(tNode
				.getColumn());
		// 得到改变的行
		int row = tNode.getRow();
		// 拿到当前改变后的数据
		String value = "" + tNode.getValue();
		// 如果是名称改变了拼音1自动带出,并且名称不能为空
		if ("PACKAGE_DESC".equals(columnName)) {
			if (value.equals("") || value == null) {
				messageBox_("名称不能为空!");
				return true;
			}
			// 获取PY1
			String py = SYSHzpyTool.getInstance().charToCode(value);
			table.setItem(row, "PY1", py);
			table.getParmValue().setData("FLG", row, "Y");
		}
		
		return false;
	}
	
	/**
	 * 医嘱明细表单击事件
	 */
	public void onDetailTableClick() {
		detailTable.acceptText();
		detailTable.getTable().grabFocus(); // 获取焦点，可以回车方便操作
		// 添加一行新数据
		int oldrow = detailTable.getRowCount() - 1;
		if (detailTable.getSelectedRow() == oldrow) {
			String packageDec = detailTable.getItemString(oldrow,
					"PACKAGE_DESC");
			if (packageDec.length() > 0) {
				// 将数据插入到明细table中
				// String packageCode =
				// getPackageCode(this.getValueString("PACKAGE_CODE"));
				// detailTable.setItem(oldrow, 0, addPackageCode);//设置新增套餐编码
				packageCodeAdd();// 自增
				// detailTable.setItem(oldrow, 1, maxseq);//设置新增序号
				maxseq++;
				// detailTable.setItem(oldrow, 2,
				// this.getValueString("PACKAGE_CODE"));//设置新增所属套餐
				insertRow("DETAIL_TABLE");
			}
			// autoHeight();
		}
	}

	/**
	 * 保存sql
	 */
	public String getSaveSql(TParm parm) {
		String oper = parm.getValue("OPER");
		String sql = "";
		if ("ADD".equals(oper)) {
			sql = "INSERT INTO MEM_PACKAGE(PACKAGE_CODE,PARENT_PACKAGE_CODE,PACKAGE_DESC,"
					+ " PACKAGE_ENG_DESC,PY1,PY2,SEQ,DESCRIPTION,ORIGINAL_PRICE,PACKAGE_PRICE,OPT_DATE,"
					+ " OPT_USER,OPT_TERM,START_DATE,END_DATE,LUMPWORK_CODE,ACTIVE_FLG) " + " VALUES('"
					+ parm.getValue("PACKAGE_CODE")
					+ "',"
					+ "'" 
					+ parm.getValue("PARENT_PACKAGE_CODE")
					+ "',"
					+ "'"
					+ parm.getValue("PACKAGE_DESC")
					+ "',"
					+ "'"
					+ parm.getValue("PACKAGE_ENG_DESC")
					+ "',"
					+ "'"
					+ parm.getValue("PY1")
					+ "',"
					+ "'"
					+ parm.getValue("PY2")
					+ "',"
					+ "'"
					+ parm.getValue("SEQ")
					+ "',"
					+ "'"
					+ parm.getValue("DESCRIPTION")
					+ "',"
					+ "'',"
					+ "'',"
					+ "to_date('"
					+ parm.getValue("OPT_DATE")
					+ "','yyyy/MM/dd'),"
					+ "'"
					+ parm.getValue("OPT_USER")
					+ "'," + "'" + parm.getValue("OPT_TERM") + "',";
			if (parm.getValue("START_DATE").length() > 0) {
				sql += "to_date('" + parm.getValue("START_DATE")
						+ "','yyyy/MM/dd'),";
			} else {
				sql += "'',";
			}
			if (parm.getValue("END_DATE").length() > 0) {
				sql += "to_date('" + parm.getValue("END_DATE")
						+ "','yyyy/MM/dd')";
			} else {
				sql += "''";
			}
				
			//add by yangjj 20150407
			sql += ","+"'"+parm.getValue("LUMPWORK_CODE")+"'"; 
			sql += ","+"'"+parm.getValue("ACTIVE_FLG")+"'";
			sql += ")";
			 //System.out.println("保存sql="+sql);

		} else if ("UPDATE".equals(oper)) {// 修改
			sql = "UPDATE MEM_PACKAGE SET " + "PACKAGE_DESC='"
					+ parm.getValue("PACKAGE_DESC") + "',"
					+ "PACKAGE_ENG_DESC='"
					+ parm.getValue("PACKAGE_ENG_DESC") + "'," + "PY1='"
					+ parm.getValue("PY1") + "'," + "PY2='"
					+ parm.getValue("PY2") + "'," + "DESCRIPTION='"
					+ parm.getValue("DESCRIPTION") + "',"
					+ "OPT_DATE = to_date('" + parm.getValue("OPT_DATE")
					+ "','yyyy/MM/dd')," + "OPT_USER='"
					+ parm.getValue("OPT_USER") + "'," + "OPT_TERM='"
					+ parm.getValue("OPT_TERM") + "',"
					+ "START_DATE = to_date('" + parm.getValue("START_DATE")
					+ "','yyyy/MM/dd')," + "END_DATE = to_date('"
					+ parm.getValue("END_DATE") + "','yyyy/MM/dd') "
					+ ",SEQ='"+parm.getValue("SEQ")+"'"
					
					//add by yangjj 20150407
					+ ",LUMPWORK_CODE = '"+parm.getValue("LUMPWORK_CODE")+"'"
					+ ",ACTIVE_FLG = '"+parm.getValue("ACTIVE_FLG")+"'"
					+ " WHERE PACKAGE_CODE = '"
					+ parm.getValue("PACKAGE_CODE") + "'";
//			 System.out.println("修改sql="+sql);
		}
		return sql;
	}

	/**
	 * 新增细表数据
	 */
	public String insertSql(TParm parm, int i) {
		String sql = "";
		sql = "INSERT INTO MEM_PACKAGE(PACKAGE_CODE,PARENT_PACKAGE_CODE,PACKAGE_DESC,"
				+ " PACKAGE_ENG_DESC,PY1,PY2,SEQ,DESCRIPTION,ORIGINAL_PRICE,PACKAGE_PRICE,OPT_DATE,"
				+ " OPT_USER,OPT_TERM,START_DATE,END_DATE,LUMPWORK_CODE,ACTIVE_FLG) " + " VALUES('"
				+ parm.getValue("PACKAGE_CODE", i)
				+ "',"
				+ "'"
				+ parm.getValue("PARENT_PACKAGE_CODE", i)
				+ "',"
				+ "'"
				+ parm.getValue("PACKAGE_DESC", i)
				+ "',"
				+ "'"
				+ parm.getValue("PACKAGE_ENG_DESC", i)
				+ "',"
				+ "'"
				+ parm.getValue("PY1", i)
				+ "',"
				+ "'"
				+ parm.getValue("PY2", i)
				+ "',"
				+ "'"
				+ parm.getValue("SEQ", i)
				+ "',"
				+ "'"
				+ parm.getValue("DESCRIPTION", i)
				+ "',"
				+ "'"
				+ parm.getValue("ORIGINAL_PRICE", i)
				+ "',"
				+ "'"
				+ parm.getValue("PACKAGE_PRICE", i)
				+ "',"
				+ "to_date('"
				+ parm.getValue("OPT_DATE", i)
				+ "','yyyy/MM/dd'),"
				+ "'"
				+ parm.getValue("OPT_USER", i)
				+ "',"
				+ "'"
				+ parm.getValue("OPT_TERM", i) 
				+ "',"
				+ "to_date('"
				+ parm.getValue("START_DATE", i).toString().substring(0,10).replace('-', '/')
				+ "','yyyy/MM/dd'),"
				+ "to_date('"
				+ parm.getValue("END_DATE", i).toString().substring(0,10).replace('-', '/')
				+ "','yyyy/MM/dd'),"
				
				//add by yangjj 20150407
				+ " '"+parm.getValue("LUMPWORK_CODE", i)+"',"
				+ " '"+parm.getValue("ACTIVE_FLG", i)+"'";
		/*
		 * if(parm.getValue("START_DATE").length()>0){ sql +=
		 * "to_date('"+parm.getValue("START_DATE", i).toString().replaceAll("-",
		 * "/").substring(0, 10)+"','yyyy/MM/dd'),"; }else{
		 */
		//sql += "'',";
		// }
		/*
		 * if(parm.getValue("END_DATE").length()>0){ sql +=
		 * "to_date('"+parm.getValue("END_DATE", i).toString().replaceAll("-",
		 * "/").substring(0, 10)+"','yyyy/MM/dd')"; }else{
		 */
		//sql += "''";
		// }

		sql += ")";
//		System.out.println("新增保存sql=" + sql);
		return sql;
	}

	/**
	 * 修改细表数据
	 */
	public String updateSql(TParm parm, int i) {
		String sql = "";
		// UPDATE MEM_PACKAGE SET PACKAGE_DESC =
		// '"+parm.getValue("PACKAGE_DESC", i)+"'
		// WHERE LastName = 'Wilson'
		sql = " UPDATE MEM_PACKAGE SET PACKAGE_DESC = '"
				+ parm.getValue("PACKAGE_DESC", i)
				+ "',"
				+ " PACKAGE_ENG_DESC = '"
				+ parm.getValue("PACKAGE_ENG_DESC", i)
				+ "',"
				+ " PY1 = '"
				+ parm.getValue("PY1", i)
				+ "',"
				+ " DESCRIPTION = '"
				+ parm.getValue("DESCRIPTION", i)
				+ "',"
				+ " ORIGINAL_PRICE = '"
				+ parm.getValue("ORIGINAL_PRICE", i)
				+ "',"
				+ " PACKAGE_PRICE = '"
				+ parm.getValue("PACKAGE_PRICE", i)
				+ "',"
				+ " OPT_DATE = TO_DATE('"
				+ parm.getValue("OPT_DATE", i)
				+ "','YYYY/MM/DD'),"
				+ " OPT_USER = '"
				+ parm.getValue("OPT_USER", i)
				+ "',"
				+ " OPT_TERM = '"
				+ parm.getValue("OPT_TERM", i)
				+ "', "
				+ " START_DATE = TO_DATE('"
				+ parm.getValue("START_DATE", i).substring(0, 10).replaceAll(
						"-", "/")
				+ "','YYYY/MM/DD'),"
				+ " END_DATE = TO_DATE('"
				+ parm.getValue("END_DATE", i).substring(0, 10).replaceAll("-",
						"/") + "','YYYY/MM/DD'), " 
				//add by yangjj 20150407
				+ " LUMPWORK_CODE = '"+parm.getValue("LUMPWORK_CODE", i)+"',"
				+ " ACTIVE_FLG = '"+parm.getValue("ACTIVE_FLG", i)+"'"
				+ " WHERE PACKAGE_CODE = '"
				+ parm.getValue("PACKAGE_CODE", i) + "' ";
		 //System.out.println("修改保存sql="+sql);
		return sql;
	}

	/**
	 * 校验主表数据是否可以删除
	 */
	public boolean ifDeleteSql(String packageCode) {
		boolean flag = false;
		String sql = "SELECT PACKAGE_CODE FROM MEM_PACKAGE WHERE PARENT_PACKAGE_CODE = '"
				+ packageCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// 如果有系表数据不能删除
		if (result.getCount("PACKAGE_CODE") > 0) {
			flag = false;
		} else {
			flag = true;
		}
		return flag;
	}

	/**
	 * 获取细表套餐编码
	 */
	public String getPackageCode(String pCode) {
		String packageCode = "";
		String sql = "SELECT MAX(PACKAGE_CODE) AS PACKAGE_CODE FROM MEM_PACKAGE "
				+ " WHERE PARENT_PACKAGE_CODE = '" + pCode + "'";
		// System.out.println("sql---"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		String a = result.getValue("PACKAGE_CODE", 0);
		int alenth = a.length();
		if (alenth > 0) {
			int b = Integer.parseInt(a) + 1;
			String c = String.valueOf(b);
			int blenth = c.length();
			int e = alenth - blenth;
			packageCode = c;
			if (e > 0) {
				for (int i = 0; i < e; i++) {
					packageCode = "0" + packageCode;
				}
			}
		} else {
			packageCode = pCode + "01";
		}

		return packageCode;
	}

	/**
	 * 套餐编码+1操作
	 */
	public void packageCodeAdd() {
		String packageCode = "";
		int alenth = addPackageCode.length();
		int b = Integer.parseInt(addPackageCode) + 1;
		String c = String.valueOf(b);
		int blenth = c.length();
		int e = alenth - blenth;
		packageCode = c;
		if (e > 0) {
			for (int i = 0; i < e; i++) {
				packageCode = "0" + packageCode;
			}
		}
		addPackageCode = packageCode;
	}

	/**
	 * 根据名称获取拼音
	 */
	public void onCPY1() {
		this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
				TypeTool.getString(getValue("PACKAGE_DESC"))));// 简拼
		this.grabFocus("PACKAGE_ENG_DESC");//add by sunqy 20140718添加下一焦点
	}
	
	/**
	 * 新增
	 */
	public void onNew(){
		String parentPackageCode = ((TTree)getComponent("TREE")).getSelectNode().getID();
		if(parentPackageCode == null && parentPackageCode.equals("")){
			this.messageBox("请选择树节点");
			return;
		}
		flag = true;
		getMaxPackageCode(parentPackageCode);
		((TTextField)this.getComponent("PACKAGE_CODE")).setEnabled(false);
		this.setValue("PACKAGE_CODE", newPackageCode);
		this.setValue("SEQ", newSeq);
		table.acceptText();
		int row = table.addRow();
		table.setItem(row, "PACKAGE_CODE", this.getValue("PACKAGE_CODE"));
		table.setItem(row, "SEQ", newSeq);
		table.setItem(row, "OPT_USER", Operator.getID());
		table.setItem(row, "OPT_DATE",TJDODBTool.getInstance().getDBTime().toString().substring(0,10).replaceAll("-", "/"));
		table.setItem(row, "OPT_TERM", Operator.getIP());
		table.setLockCellRow(row, false);
		table.setLockCell(row, "PACKAGE_CODE", true);
		table.setLockCell(row, "OPT_USER", true);
		table.setLockCell(row, "OPT_DATE", true);
		table.setLockCell(row, "OPT_TERM", true);
	}
	
	/**
	 * 全选事件
	 */
	public void selectAll(){
		table.acceptText();
		TParm tableParm = table.getParmValue();
		if(this.getCheckBox("tCheckBox_0").isSelected()){
			for(int i = 0; i < tableParm.getCount("XUAN"); i++){
				table.setItem(i,"XUAN","Y");
			}
		}else{
			for(int i = 0; i < tableParm.getCount("XUAN"); i++){
				table.setItem(i,"XUAN","N");
			}
		}
	}
	
	/**
	 * @param tagName
	 * @return
	 */
	public TCheckBox getCheckBox(String tagName){
		return (TCheckBox) this.getComponent(tagName);
	}
	
	/**
	 * 批量更新活动标记
	 */
	public void updateActiveFlg(){
		table.acceptText();
		String packageCodes = "";
		for(int i = 0 ; i < table.getParmValue().getCount("XUAN"); i++){
			if("Y".equals(table.getParmValue().getValue("XUAN",i))){
				packageCodes += table.getParmValue().getValue("PACKAGE_CODE",i)+",";
			}
		}
		if("".equals(packageCodes)){
			this.messageBox("请选择操作数据");
			return;
		}
		
		TParm parm = new TParm();
		parm.setData("ACTIVE_FLG",this.getValue("tCheckBox_1"));
		parm.setData("packageCodes",packageCodes.substring(0,packageCodes.length()-1));
		TParm result = TIOM_AppServer.executeAction(
				"action.mem.MEMMainPackageAction", "updateActiveFlg", parm);
		if(result.getErrCode() < 0){
			this.messageBox("err:"+result.getErrText());
			return;
		}
		this.messageBox("执行成功");
		this.onInit();
	}
}
