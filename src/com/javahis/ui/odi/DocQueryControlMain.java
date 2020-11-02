package com.javahis.ui.odi;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

//import com.bluecore.ca.pdf.CaPdfUtil;
import com.bluecore.ca.pdf.CaPdfUtil;
import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.base.TComboBoxBase;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;


public class DocQueryControlMain extends TControl {

	/**
	 * 已审核
	 */
	private static int PDF_FLG_YSH = 2;
	/**
	 * 已提交
	 */
	private static int PDF_FLG_YTJ = 1;     
	/**
	 * 未提交
	 */
	private static int PDF_FLG_WTJ = -1;       
	/**
	 * 审核退回
	 */
	private static int PDF_FLG_SHTH = -2;
	/**
	 * 归档退回
	 */
	private static int PDF_FLG_GDTH = -3;
	/**
	 * 已归档
	 */
	private static int PDF_FLG_YGD = 3;
	/**
	 * 审核通过UPDATASQL
	 */
	private String UPDATE_EXAMINE = "";
	/**
	 * 审核退回UPDATASQL
	 */
	private String UPDATE_EXAMINECANCEL = "";
	/**
	 * 已归档UPDATASQL
	 */
	private String UPDATE_FILEOK = "";
	/**
	 * 归档退回UPDATASQL
	 */
	private String UPDATE_FILECANCEL = "";
	/**
	 * 临时目录
	 */
	String tempPath = "C:\\JavaHisFile\\temp\\pdf";
	String CAPath = "C:\\JavaHisFile\\temp\\CA";
	private String KEYPATH = "C:\\CA\\"+Operator.getID()+"\\Key" ;
	private String IMAGEPATH = "C:\\CA\\"+Operator.getID()+"\\Image" ;
	/**String tempPath = TConfig.getSystemValue("FileServer.Main.Root")
			+ "\\temp\\pdf";**/
	
//	String tempPath ="C:\\JavaHisFile\\temp\\pdf";

	// =================排序辅助==============add by wanglong 20120921
	private BILComparator compare = new BILComparator();
	private int sortColumn = -1;
	private boolean ascending = false;
	
	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		 callFunction("UI|PRINT|setEnabled", false);
		 callFunction("UI|PRINT_DR_CODE|setEnabled", false);
		 callFunction("UI|PRINT_DATE|setEnabled", false);
		 callFunction("UI|PrintSave|setEnabled", false);  
		if(this.getPopedem("MEDICALRECORD")){//病案室
			((TRadioButton)this.getComponent("radioStatusWTJ")).setVisible(false);
			((TRadioButton) this.getComponent("radioStatusYTJ")).setVisible(false);
			((TRadioButton) this.getComponent("radioStatusSHTH")).setVisible(false);
			((TRadioButton) this.getComponent("radioStatusYSH")).setX(9);
			((TRadioButton) this.getComponent("radioStatusGDTH")).setX(97);
			((TRadioButton) this.getComponent("radioStatusYGD")).setX(193);
			((TMenuItem)this.getComponent("examine")).setVisible(false);
			((TMenuItem)this.getComponent("examineCancel")).setVisible(false);
			((TRadioButton) this.getComponent("radioStatusYSH")).setSelected(true);
		}
		if(this.getPopedem("DR")){//医师
			((TRadioButton) this.getComponent("radioStatusYTJ")).setSelected(true);
//			((TRadioButton) this.getComponent("radioStatusYSH")).setVisible(false);
//			((TRadioButton) this.getComponent("radioStatusGDTH")).setVisible(false);
			((TRadioButton) this.getComponent("radioStatusYGD")).setVisible(false);
			((TMenuItem)this.getComponent("fileOK")).setVisible(false);
			((TMenuItem)this.getComponent("fileCancel")).setVisible(false);
			((TMenuItem)this.getComponent("addFile")).setVisible(false);
		}
		              
		// 初始化页面
		UPDATE_EXAMINE = "UPDATE MRO_MRV_TECH SET CHECK_FLG=" + PDF_FLG_YSH
				+ ", CHECK_CODE='" + Operator.getID() + "' ,CHECK_DATE=SYSDATE";
		UPDATE_EXAMINECANCEL = "UPDATE MRO_MRV_TECH SET CHECK_FLG="
				+ PDF_FLG_SHTH + ", CHECK_CODE='" + Operator.getID()
				+ "' ,CHECK_DATE=SYSDATE";
		UPDATE_FILEOK = "UPDATE MRO_MRV_TECH SET CHECK_FLG=" + PDF_FLG_YGD
				+ ", ARCHIVE_CODE='" + Operator.getID()
				+ "' ,ARCHIVE_DATE=SYSDATE";
		UPDATE_FILECANCEL = "UPDATE MRO_MRV_TECH SET CHECK_FLG=" + PDF_FLG_GDTH
				+ ", CHECK_CODE='" + Operator.getID() + "' ,CHECK_DATE=SYSDATE";
		TTable table = (TTable) this.getComponent("TABLE");//add by wanglong 20120921 加排序
		addSortListener(table);//add by wanglong 20120921 加排序
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
		"onCheckBox");    //    add  by  chenx  添加复选框选择事件
	}

	/**     
	 * 查询
	 */
	public void onQuery() {
		String mrNo = getValueString("MR_NO");
		if (mrNo.length() > 0) {
			mrNo = PatTool.getInstance().checkMrno(mrNo);
			setValue("MR_NO", mrNo);
		}
		TTable table = (TTable) this.getComponent("TABLE");
		// 公用参数
		//MRO_RECORD B,MRO_MRV_TECH C
		String SQL = "select '' AS FLG,A.MR_NO,B.PAT_NAME,B.SEX SEX_CODE,B.BIRTH_DATE,B.IN_DATE,B.OUT_DATE AS DS_DATE," + //modify by wanglong 20120921 修改日期从mro_record表中查询
				"A.DS_DEPT_CODE IN_DEPT_CODE,A.VS_DR_CODE,A.IPD_NO,"
				+ "A.CASE_NO,C.CHECK_FLG,C.MERGE_CODE,C.MERGE_DATE,C.SUBMIT_CODE,C.SUBMIT_DATE,C.CHECK_CODE,C.CHECK_DATE,C.ARCHIVE_CODE,"
				+ "C.ARCHIVE_DATE ,TRUNC(C.CHECK_DATE, 'DD')   -   TRUNC(A.DS_DATE, 'DD') AS DAYS,C.PRINT_USER AS OPT_USER,C.PRINT_DATE  AS OPT_DATE"
				+ " FROM ADM_INP A,MRO_RECORD B,MRO_MRV_TECH C where A.MR_NO=B.MR_NO " +
				  " AND  A.MR_NO=C.MR_NO(+) AND A.CASE_NO=B.CASE_NO AND A.CASE_NO=C.CASE_NO(+) AND CANCEL_FLG ='N' ";
		String MR_NO = ((TTextField) this.getComponent("MR_NO")).getValue();
		String sreachType = ((TComboBox) this.getComponent("sreachType"))
				.getValue();     
		String inStartDate = ((TTextFormat) this.getComponent("inStartDate"))
				.getText();
		String inEndDate = ((TTextFormat) this.getComponent("inEndDate")).getText();
		String outStartDate = ((TTextFormat) this.getComponent("outStartDate"))
		.getText();
		String outEndDate = ((TTextFormat) this.getComponent("outEndDate")).getText();
		String DEPT_CODE = ((TTextFormat) this.getComponent("DEPT_CODE"))
				.getComboValue();
		String filterSQL = "  ";
		if (!StringUtil.isNullString(MR_NO)) {
			filterSQL += " AND A.MR_NO='" + MR_NO + "'";
		}

		if (!StringUtil.isNullString(inStartDate)) {
			filterSQL += "  AND B.IN_DATE>=TO_DATE('" + inStartDate //modify by wanglong 20120921 修改日期从mro_record表中查询
					+ " 00:00:00', 'YYYY/MM/DD  HH24:MI:SS')";
		}
		if (!StringUtil.isNullString(inEndDate)) {
			filterSQL += "  AND B.IN_DATE<=TO_DATE('" + inEndDate //modify by wanglong 20120921 修改日期从mro_record表中查询
					+ " 23:59:59', 'YYYY/MM/DD  HH24:MI:SS')";
		}
		if (!StringUtil.isNullString(outStartDate)) {
			filterSQL += "  AND B.OUT_DATE>=TO_DATE('" + outStartDate //modify by wanglong 20120921 修改日期从mro_record表中查询
			+ " 00:00:00', 'YYYY/MM/DD  HH24:MI:SS')";
		}
		if (!StringUtil.isNullString(outEndDate)) {
			filterSQL += "  AND B.OUT_DATE<=TO_DATE('" + outEndDate //modify by wanglong 20120921 修改日期从mro_record表中查询
			+ " 23:59:59', 'YYYY/MM/DD  HH24:MI:SS')";
		}
		if (!StringUtil.isNullString(DEPT_CODE)) {
			filterSQL += "  AND A.DS_DEPT_CODE='" + DEPT_CODE + "'";
		}

		if (sreachType.equals("0")) {// 未出院
			filterSQL += " AND B.OUT_DATE IS NULL ";
		} else if (sreachType.equals("1")) {// 出院/未完成
			filterSQL += " AND B.OUT_DATE IS NOT NULL "
					+ " AND (B.ADMCHK_FLG<>'Y' OR B.DIAGCHK_FLG<>'Y' OR B.BILCHK_FLG<>'Y' OR B.QTYCHK_FLG<>'Y')";
		} else if (sreachType.equals("2")) {// 出院/已完成
			filterSQL += " AND B.OUT_DATE IS NOT NULL "
					+ " AND B.ADMCHK_FLG='Y' " + " AND B.DIAGCHK_FLG='Y' "
					+ " AND B.BILCHK_FLG='Y' " + " AND B.QTYCHK_FLG='Y' ";
		}
         
		int status = getStatusFlg();
		if (status != 0) {
			if(status==PDF_FLG_WTJ){
				filterSQL += "  AND C.CHECK_FLG IS NULL ";
			}else{
				filterSQL += "  AND C.CHECK_FLG=" + status + "";
			}
		}
//		System.out.println("===sql==="+SQL + filterSQL);
		TParm result = new TParm(TJDODBTool.getInstance().select(
				SQL + filterSQL));
		int count=result.getCount("CASE_NO");
        if (count < 0) {
            ((TTextField) this.getComponent("diseaseCount")).setValue(0 + "");
        } else
		((TTextField) this.getComponent("diseaseCount")).setValue(count	+ "");
		for (int i = 0; i < count; i++) {
			String indatestr = result.getValue("ARCHIVE_DATE", i);
			if(indatestr.length()>1){
				Timestamp date = StringTool.getTimestamp(indatestr,
						"yyyy-MM-dd");
				result.setData("ARCHIVE_DATE", i, date);
			}
			indatestr = result.getValue("CHECK_DATE", i);
			if(indatestr.length()>1){
				Timestamp date = StringTool.getTimestamp(indatestr,
						"yyyy-MM-dd");
				result.setData("CHECK_DATE", i, date);
			}
			indatestr = result.getValue("MERGE_DATE", i);
			if(indatestr.length()>1){
				Timestamp date = StringTool.getTimestamp(indatestr,
				"yyyy-MM-dd");
				result.setData("MERGE_DATE", i, date);
			}
			indatestr = result.getValue("SUBMIT_DATE", i);
			if(indatestr.length()>1){
				Timestamp date = StringTool.getTimestamp(indatestr,
				"yyyy-MM-dd");
				result.setData("SUBMIT_DATE", i, date);
			}	
			indatestr = result.getValue("OPT_DATE", i);
			if(indatestr.length()>1){
				Timestamp date = StringTool.getTimestamp(indatestr,
				"yyyy-MM-dd");
				result.setData("OPT_DATE", i, date);
			}	
		}
		table.setParmValue(result);
	}
	/**       
	 * 已归档按钮点击事件  
	 * 
	 * @return  void 
	 */    
	public void onradioStatusGDTH(){
		if (isSelected("radioStatusGDTH")){
//			this.clearValue("PRINT_DR_CODE","PRINT_DATE") ;
			 callFunction("UI|PRINT|setEnabled", false);
			 callFunction("UI|PRINT_DR_CODE|setEnabled", false);
			 callFunction("UI|PRINT_DATE|setEnabled", false);
			 callFunction("UI|PrintSave|setEnabled", false);
		}
	}  
	public void onradioStatusYGD(){
		if (isSelected("radioStatusYGD")){
			 callFunction("UI|PRINT|setEnabled", true);
			 callFunction("UI|PRINT_DR_CODE|setEnabled", true);
			 callFunction("UI|PRINT_DATE|setEnabled", true);
			 callFunction("UI|PrintSave|setEnabled", true);
		}
	}  
	public void onradioStatusYSH(){
		if (isSelected("radioStatusYSH")){
			 callFunction("UI|PRINT|setEnabled", false);
			 callFunction("UI|PRINT_DR_CODE|setEnabled", false);
			 callFunction("UI|PRINT_DATE|setEnabled", false);
			 callFunction("UI|PrintSave|setEnabled", false);
		}
	}  
	/**       
	 * 复印按钮点击事件  
	 * 
	 * @return  void 
	 */    
	public void PrintClick(){
		   if (Selected("PRINT")) {  
		       // 得到当前时间
		       Timestamp date = SystemTool.getInstance().getDate();
		       // 初始化查询区间
		       setValue("PRINT_DATE",
		                      date.toString().substring(0, 10).replace('-', '/'));
	           setValue("PRINT_DR_CODE", Operator.getID()); 
		}        
		   else{
			   setValue("PRINT_DATE","");
		       setValue("PRINT_DR_CODE",""); 
		  }    

	}
	/**
	 * 病案号回车查询
	 */
	public void onPush(){
		this.onQuery() ;
	}
     //==================  add  by  chenxi 
	/**    
	 * 复印保存事件  
	 * 
	 * @return  void 
	 */            
	public void onPrintSave(){  
	  TTable  table =(TTable)this.getComponent("TABLE") ;
	  if(table==null || table.getRowCount()<=0){
		  this.messageBox("没有保存的数据") ;
		  return   ;
	  }
	  table.clearSelection();
	  TParm tableParm = table.getParmValue() ;
	  for(int i=0;i<tableParm.getCount("MR_NO");i++){
		  if(tableParm.getBoolean( "FLG",i)){
			  String mrNo = tableParm.getValue("MR_NO",i) ;
			  String optUser = this.getValueString("PRINT_DR_CODE");
				String optDate = this.getValueString("PRINT_DATE").substring(0,10);
				String UpdateSql = " UPDATE MRO_MRV_TECH " +  
		           " SET  PRINT_USER='"+optUser+"', " + 
		           " PRINT_DATE=TO_DATE('"+optDate+"','YYYY-MM-DD HH24MISS') " +  
		           " WHERE MR_NO = '"+mrNo+"'" +
		           " AND CHECK_FLG = " + PDF_FLG_YGD + " ";   
				TParm  result = new TParm(TJDODBTool.getInstance().update(UpdateSql)) ;
				if(result.getErrCode()<0){
					this.messageBox("E0005");
					return  ;
				}
		  }
	  }
	 
			this.messageBox("P0005");
			this.onQuery() ;
		
	}
	/**
	 * 复选框选中事件
	 * 
	 * @param obj
	 * @return
	 */
	public void onCheckBox(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int row =table.getSelectedRow() ;
		TParm parm =table.getParmValue() ;
		parm.setData("FLG", row, "Y");
	}
	//===========================  add  by  chenx  end 
	/**
	 * 返回查询状态
	 * 
	 * @return
	 */
	private int getStatusFlg() {
		if (isSelected("radioStatusWTJ")) {
			return PDF_FLG_WTJ;
		} else if (isSelected("radioStatusYTJ")) {
			return PDF_FLG_YTJ;
		} else if (isSelected("radioStatusYSH")) {
			return PDF_FLG_YSH;
		} else if (isSelected("radioStatusSHTH")) {
			return PDF_FLG_SHTH;
		} else if (isSelected("radioStatusYGD")) {
			return PDF_FLG_YGD;
		} else if (isSelected("radioStatusGDTH")) {
			return PDF_FLG_GDTH;
		}
		return 0;
	}

	/**
	 * TRadioButton判断是否选中
	 * 
	 * @param tagName
	 * @return
	 */
	private boolean isSelected(String tagName) {
		return ((TRadioButton) this.getComponent(tagName)).isSelected();
	}
	/**
	 * CheckBox判断是否选中
	 * 
	 * @param tagName
	 * @return
	 */
	private boolean Selected(String tagName) {
		return ((TCheckBox) this.getComponent(tagName)).isSelected();
	}

	/**
	 * 审核通过
	 * 
	 * @return
	 */
	public void onExamine() {
		update(UPDATE_EXAMINE);
	}

	/**
	 * 审核退回
	 * 
	 * @return
	 */
	public void onexamineCancel() {
		update(UPDATE_EXAMINECANCEL);
	}

	/**
	 * 归档通过
	 * 
	 * @return
	 */
	public void onFileOK() {
		update(UPDATE_FILEOK);
	}

	/**
	 * 归档退回
	 * 
	 * @return
	 */
	public void onFileCancel() {
		update(UPDATE_FILECANCEL);
	}

	/**
	 * 合并病案首页
	 * 
	 * @return
	 */
	public void onAddFile() {
		TTable table = (TTable) this.getComponent("TABLE");
		int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择需要合并病案行。");
			return;
		}
		this.openDialog("%ROOT%\\config\\odi\\ODIDocMergeHome.x", table
				.getParmValue().getRow(row), false);

	}

	/**
	 * 更新状态
	 * 
	 * @return
	 */
	public void update(String updateType) {
		TTable table = (TTable) this.getComponent("TABLE");
		table.acceptText();
		String filterSQL = "";
		TParm parm = table.getParmValue();
		for (int i = 0; i < parm.getCount("FLG"); i++) {
			if (parm.getBoolean("FLG", i)) {
				if (filterSQL.equals("")) {
					filterSQL += " WHERE (MR_NO='" + parm.getValue("MR_NO", i)
							+ "' AND CASE_NO='" + parm.getValue("CASE_NO", i)
							+ "')";
				} else {
					filterSQL += " OR (MR_NO='" + parm.getValue("MR_NO", i)
							+ "' AND CASE_NO='" + parm.getValue("CASE_NO", i)
							+ "')";
				}
			}
		}
		if (filterSQL.equals("")) {
			this.messageBox("请您选择需要操作的病患。");
			return;
		}
		parm = new TParm(TJDODBTool.getInstance()
				.update(updateType + filterSQL));
		if (parm.getErrCode() < 0) {
			this.messageBox_("更新状态失败。");
			return;
		}
		this.messageBox_("操作成功。");
		onQuery();

	}

	/**
	 * 浏览
	 */
	public void onReaderSubmitPDF() {
		TTable table = (TTable) this.getComponent("TABLE");
		int col = table.getSelectedColumn();
		if (col == 0) {
			return;
		}
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		String MR_NO = parm.getValue("MR_NO");
		String caseNo = parm.getValue("CASE_NO");
		String bigFilePath = TConfig
		.getSystemValue("FileServer.Main.Root")
				+ "\\正式病历\\" + MR_NO.substring(0, 7) + "\\" + MR_NO + "\\"
				+ caseNo + ".pdf";
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
				.getSocket(),bigFilePath);
		if (data == null) {
			messageBox_("尚未提交PDF");
			return;
		}
		try {
			FileTool.setByte(tempPath + "\\" + caseNo + ".pdf", data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Runtime runtime = Runtime.getRuntime();
		try {
			// 打开文件
			runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
					+ "\\" + caseNo + ".pdf");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	/**
	 * 导出EXECL
	 */
	public void onExecl() {
		ExportExcelUtil.getInstance().exportExcel(this.getTTable("TABLE"),
				"病案归档");
	}/**
	 * 得到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {    
		return (TTable) this.getComponent(tag);
	}
	
	// ====================排序功能begin======================add by wanglong 20120921
	/**
	 * 加入表格排序监听方法
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// 点击相同列，翻转排序
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// 取得表单中的数据
				String columnName[] = tableData.getNames("Data");// 获得列名
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
				int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * 根据列名数据，将TParm转为Vector
	 * 
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 返回指定列在列名数组中的index
	 * @param columnName
	 * @param tblColumnName
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * 根据列名数据，将Vector转成Parm
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */

	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
	// ====================排序功能end======================
//=====================================================   chenxi modify  20130410  CA数字签名
	//上传
	public void onUpLoad()
	{
		String caseNo;
		String MR_NO;
		TTable table = (TTable)getComponent("TABLE");
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		if (table.getSelectedRow() < 0 || table == null)
		{
			messageBox("没有选中病患");
			return;
		}
		String sreachType = ((TComboBox)getComponent("sreachType")).getValue();
		caseNo = parm.getValue("CASE_NO");
		MR_NO = parm.getValue("MR_NO");
		int status = getStatusFlg(); 
		if (sreachType.equals("2") || status == 3){	
		String dir;
		String localPath = CAPath + "\\" + caseNo + ".pdf" ;   //签名之后保存的本地路劲
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(), localPath);
		String dirpath = TConfig.getSystemValue("FileServer.Main.Root")
		                   + "\\正式病历\\" + MR_NO.substring(0, 7) + "\\" + MR_NO ;  //上传到服务器的路劲
			  dir = dirpath + "\\"+caseNo + ".pdf" ;
		File filepath = new File(dirpath);
		filepath = new File(localPath);
		if (!filepath.exists())
		{
			messageBox("该病患pdf文件尚未签名,不可上传");
			return;
		}
		if (!filepath.exists())
			filepath.mkdirs();
		try
		{
			TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(), dir, data);
			messageBox("上传成功");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		}
		else {
			messageBox("已完成,已归档的pdf文件才能上传");
			return;
		}
		
	}
   //  数字签名认证
	@SuppressWarnings("static-access")
	public void onInPassword()
	{	
		TTable table = (TTable)getComponent("TABLE");
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		if (table.getSelectedRow() < 0 || table == null)
		{
			messageBox("没有选中病患");
			return;
		}
		String caseNo = parm.getValue("CASE_NO");
		String MR_NO = parm.getValue("MR_NO");
		CaPdfUtil caPdf = new CaPdfUtil();
		String keyPath = KEYPATH+"\\"+"key.pfx";//-=====密钥路径
		String  sourcePdfPath = tempPath + "\\" + caseNo + ".pdf" ; ;//需签名PDF路径
		String signPdfPath = CAPath + "\\" + caseNo + ".pdf" ; ;//已签名的PDF路径
		String signImagePath = IMAGEPATH+"\\"+"Image.gif";//签名使用的图片路径
		String reason = Operator.getName();//文字描述1
		String location = SystemTool.getInstance().getDate().toString().substring(0, 19);  //文字描述2
		String bigFilePath = TConfig
		.getSystemValue("FileServer.Main.Root")
				+ "\\正式病历\\" + MR_NO.substring(0, 7) + "\\" + MR_NO + "\\"
				+ caseNo + ".pdf";
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
				.getSocket(),bigFilePath);
		if (data == null) {
			messageBox_("尚未提交PDF,不可签名");
			return;
		}
		String password = (String)openDialog("%ROOT%\\config\\mro\\MROInPassword.x");//	密码    
		//  =====创建文件夹
		File file = new File(KEYPATH) ;
		if(!file.exists()){
			file.mkdirs() ;
		}
		file = new File(IMAGEPATH) ;
		if(!file.exists()){
			file.mkdirs() ;
		}
		file = new File(CAPath) ;
		if(!file.exists()){
			file.mkdirs() ;
		}
		//判断图片和秘钥是否存在，并给出相应的提示
		file =  new File(keyPath) ;
		if(!file.exists()){
			this.messageBox("密钥不存在，请将其拷贝到"+KEYPATH+"并命名为key.pfx") ;
			return ;
		}
		file =  new File(signImagePath) ;
		if(!file.exists()){
			this.messageBox("图片不存在，请将其拷贝到"+KEYPATH+"并命名为Image.pfx") ;
			return ;
		}  
		if (password == null)
			return;
		try
		{
			caPdf.doPdfSign(keyPath, password, sourcePdfPath, signPdfPath, signImagePath, reason, location);
			messageBox("签名成功");
		}
		catch (Exception e)
		{
			System.out.println(e.toString());   
		}
		return;
	}
	public static void main(String[] args) {
		String keyPath = "C:\\test20121015.pfx";//-=====密钥路径
		String  sourcePdfPath = "C:\\1.pdf" ; ;//需签名PDF路径
		String signPdfPath = "C:\\2.pdf" ; ;//已签名的PDF路径
		String signImagePath = "C:\\1.gif";//签名使用的图片路径
		String reason = "1";//文字描述1
		String location = "2";  //文字描述2           
		String password = "111111";//	密码
		System.out.println("1111111111====222222=密钥路径====="+keyPath);
		System.out.println("1111111112=====需签名PDF路径====="+sourcePdfPath);     
		System.out.println("1111111113=====已签名的PDF路径====="+signPdfPath);     
		System.out.println("1111111114===签名使用的图====片路径======="+signImagePath);
		System.out.println("1111111115====文字描述1==5ewewe==="+reason);   
		System.out.println("1111111116====文字描述2======"+location);  
		System.out.println("1111111116===3232==密码==rerer==="+password);  
		System.out.println("1111111111=====密钥路径====="+keyPath);
//		System.out.println("1111111112=====需签名PDF路径====="+sourcePdfPath);
//		System.out.println("1111111113=====已签名的PDF路径====="+signPdfPath);
//		System.out.println("1111111114===签名使用的图片路径======="+signImagePath);
//		System.out.println("1111111115====文字描述1====="+reason);    
//		System.out.println("1111111116====文字描述2======"+location);
//		System.out.println("1111111116=====密码====="+password);      
		CaPdfUtil.doPdfSign(keyPath, password, sourcePdfPath, signPdfPath, signImagePath, reason, location);
	}

}
