package com.javahis.ui.inp;

import java.awt.Component;

import jdo.odi.OdiMainTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;

/**
 * 
 * @author lixiang
 *
 */
public class INPOrderControl  extends TControl{
	
//	private static String TABLE2 = "TABLE2";
	// ������
	private TTable table;
	
	
	private String caseNo;
	private String mrNo;
	private TWord word;
	public void setWord(TWord word) {
		this.word = word;
	}

	public TWord getWord() {
		return this.word;
	}
	/**
	 * ��ǰ�༭��
	 */
	int rowOnly;
	
	/**
	 * 
	 */
	public void onInit() {
		Object obj = this.getParameter();	
		table=getTTable("TABLE2");
		getTTable("TABLE2").addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComoponent");
		// TABLEֵ�ı����
		addEventListener("TABLE2" + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValue");
		if(obj!=null){
			// ���þ����
			this.setCaseNo(((TParm)obj).getData("CASE_NO")
					.toString());
			// ���ò�����
			this.setMrNo(((TParm) obj).getData("MR_NO")
					.toString());
		}else{
			this.setCaseNo("130716000433");
			this.setMrNo("000000438282");
		}
		
		initDataStoreToTable();
		
		
	}
	
	public void initSYSFeePopup() {
		
		
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

	public int getRowOnly() {
		return rowOnly;
	}

	public void setRowOnly(int rowOnly) {
		this.rowOnly = rowOnly;
	}

	/**
	 * ��ʼ��DataStore
	 */
	public void initDataStoreToTable() {
//		TParm action = new TParm();
		// ���ò�ѯ��ֵCASE_NO
		//action.setData("CASE_NO", getCaseNo());
		// ��ʼ��Table
		table = getTTable("TABLE2");
		getTTable("TABLE2").addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComoponent");
		//table.removeRowAll();
		//TDataStore dataStore = new TDataStore();
		//(ORDER_DESC || SPECIFICATION) as 
		//+ "        CASE WHEN SPECIFICATION IS NULL THEN '' ELSE ('(' || SPECIFICATION || ')') END"
//		String sql="SELECT (ORDER_DESC||SPECIFICATION) as ORDER_DESC"
//			+",MEDI_QTY,MEDI_UNIT,FREQ_CODE,ROUTE_CODE,"
//        + "CASE_NO, PHA_SEQ, SEQ_NO, MR_NO, ORDER_CODE,"
//        + "ORDER_DATE,NODE_FLG,APPROVE_FLG,USE_FLG,OPT_USER,"
//        + "OPT_DATE,OPT_TERM,ANTI_REASON AS ANTI_REASON_CODE, "
//        + "FROM PHA_ANTI "
//        + "WHERE CASE_NO='"+this.getCaseNo()+"' AND USE_FLG='N'"
//        + " ORDER BY ORDER_CODE";
		String sql="SELECT '' FLAG,'0' ANTI_TAKE_DAYS, (A.ORDER_DESC||A.SPECIFICATION) as ORDER_DESC"
			+",A.SPECIFICATION,A.MEDI_QTY,A.MEDI_UNIT MEDI_UNIT_DESC,A.MEDI_UNIT,A.FREQ_CODE,A.FREQ_CODE AS FREQ_DESC_CODE,A.ROUTE_CODE,A.ROUTE_CODE AS ROUTE_DESC_CODE,"
        + "A.CASE_NO, A.PHA_SEQ, A.SEQ_NO, A.MR_NO, A.ORDER_CODE,"
        + "A.ORDER_DATE,A.NODE_FLG,A.APPROVE_FLG,A.USE_FLG,A.OPT_USER,"
        + "A.OPT_DATE,A.OPT_TERM,A.ANTI_REASON AS ANTI_REASON_CODE,'' ANTI_REASON,'N' AS DATA_FLG "
        + "FROM PHA_ANTI A "
        + "WHERE  A.CASE_NO='"+
        this.getCaseNo()+"' AND A.USE_FLG='N' AND APPROVE_FLG<>'Y' AND A.OVERRIDE_FLG<>'Y'"
        + " ORDER BY A.ORDER_CODE";
		//System.out.println("----SQL-----"+sql);
		TParm parmorder = new TParm(TJDODBTool.getInstance().select(sql));
		//System.out.println("------parmorder----"+parmorder);
		/*dataStore.setSQL(sql);
		dataStore.retrieve();
		table.setDataStore(dataStore);
		table.setDSValue();*/
//		for (int i = 0; i < parmorder.getCount(); i++) {
//			table.setItem(i, "FLAG",parmorder.getValue("FLAG",i));
//			table.setItem(i, "ANTI_TAKE_DAYS",parmorder.getValue("ANTI_TAKE_DAYS",i));
//			table.setItem(i, "ANTI_REASON",parmorder.getValue("ANTI_REASON",i));
//			table.setItem(i, "ORDER_DESC", parmorder.getValue("ORDER_DESC",i));
//			table.setItem(i, "SPECIFICATION", parmorder.getValue("SPECIFICATION",i));
//			table.setItem(i, "MEDI_QTY",parmorder.getDouble("MEDI_QTY",i));
//			table.setItem(i, "MEDI_UNIT",parmorder.getValue("MEDI_UNIT",i));
//			table.setItem(i, "MEDI_UNIT_DESC", parmorder.getValue("MEDI_UNIT_DESC",i));
////			table.setItem(row, "FREQ_CODE", "");
//			table.setItem(i, "ROUTE_CODE", parmorder.getValue("ROUTE_CODE",i));
//			table.setItem(i, "ROUTE_DESC_CODE",parmorder.getValue("ROUTE_DESC_CODE",i));
//			table.setItem(i, "FREQ_DESC_CODE",parmorder.getValue("FREQ_DESC_CODE",i));
//			table.setItem(i, "FREQ_CODE", parmorder.getValue("FREQ_CODE",i));
//			table.setItem(i, "ORDER_CODE",parmorder.getValue("ORDER_CODE",i));
//			table.setItem(i,"ANTI_REASON_CODE",parmorder.getValue("ANTI_REASON_CODE",i));
//		}
		table.setParmValue(parmorder);
		this.insertOrderRow();
		
	}
	
	/**
	 * ���һ��������
	 */
	public void insertOrderRow() {
		table = getTTable("TABLE2");
		table.acceptText();
		int row = table.addRow();
		table.setItem(row, "FLAG", "");
		table.setItem(row, "ANTI_TAKE_DAYS", "");
		table.setItem(row, "ANTI_REASON", "");
		table.setItem(row, "ORDER_DESC", "");
		table.setItem(row, "SPECIFICATION", "");
		table.setItem(row, "MEDI_QTY", "");
		table.setItem(row, "MEDI_UNIT", "");
		table.setItem(row, "MEDI_UNIT_DESC", "");
//		table.setItem(row, "FREQ_CODE", "");
		table.setItem(row, "ROUTE_CODE", "");
		table.setItem(row, "ROUTE_DESC_CODE", "");
		table.setItem(row, "FREQ_DESC_CODE", "");
		table.setItem(row, "FREQ_CODE", "");
		table.setItem(row, "ORDER_CODE", "");
		table.setItem(row,"ANTI_REASON_CODE","");
		//FLAG;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;FREQ_CODE;ORDER_CODE
//		FLAG;ANTI_TAKE_DAYS;ANTI_REASON;ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT_DESC;
//		ROUTE_DESC_CODE;FREQ_DESC_CODE;ORDER_CODE;MEDI_UNIT;ROUTE_CODE;FREQ_CODE
	}
	
	
	/**
	 * ��TABLE�����༭�ؼ�ʱ����
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	/*public void onCreateEditComoponentUD(Component com, int row, int column) {
		// ״̬����ʾ
		//callFunction("UI|setSysStatus", "");
		// ��ǰ�༭��
		this.rowOnly = row;
		// �õ�����
		String columnName = this.getFactColumnName(TABLE2, column);
		if (!"ORDER_DESC".equals(columnName))
			return;
		// 20121109 shibl add �ظ��޸�ҽ������
		TTable table = getTTable(TABLE2);
		int selRow = this.getTTable(TABLE2).getSelectedRow();
		TParm existParm = this.getTTable(TABLE2).getDataStore().getRowParm(
				selRow);
		if (this.isOrderSet(existParm)) {
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			return;
		}
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// ����ҽ������
		TParm parm = new TParm();
		
		if ("B".equals(this.getValue("KLSTARUD"))) {
			// �˿���״̬�����ڳ�Ժ��ҩ��ʹ��
			this.messageBox("E0161");
			return;
		} else {
			parm.setData("ODI_ORDER_TYPE", this.getValue("KLSTARUD"));
		}
		// ���õ����˵�
		//textFilter.setPopupMenuParameter("ITEM", getConfigParm().newConfig(
				//"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		
		textFilter.setPopupMenuParameter("ITEM", getConfigParm().newConfig(
		"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}*/
	/**
	 * ����ʵ������
	 * 
	 * @param column
	 *            String
	 * @param column
	 *            int
	 * @return String
	 */
	public String getFactColumnName(String tableTag, int column) {
		int col = this.getThisColumnIndex(column, tableTag);
		return this.getTTable(tableTag).getDataStoreColumnName(col);
	}
	
	/**
	 * �õ�����֮ǰ���к�
	 * 
	 * @param column
	 *            int
	 * @return int
	 */
	public int getThisColumnIndex(int column, String table) {
		return this.getTTable(table).getColumnModel().getColumnIndex(column);
	}
	
	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	
	/**
	 * ��TABLE�����༭�ؼ�ʱ
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponent(Component com, int row, int column) {
		// �õ�����
		String columnName = this.getFactColumnName("TABLE2", column);
		if (!columnName.contains("ORDER_DESC"))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// ���õ����˵�
		textFilter.setPopupMenuParameter("ITEM", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");

	}
	
	/**
	 * ����ֵ�¼�
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		TParm parm = (TParm) obj;
		if ("ITEM".equals(tag)) {
			this.getTTable("TABLE2").acceptText();
			int selRow = this.getTTable("TABLE2").getSelectedRow();
			// ������
			onInsertOrderList(selRow, parm);
			
			
		}
	}
	
	/**
	 * ����ϸ��
	 * 
	 * @param type
	 *            int
	 */
	public void onInsertOrderList(int row, TParm parm) {
		table.acceptText();
		String orderCode = parm.getValue("ORDER_CODE");
		if (!isAnti(orderCode)) {
			this.messageBox("��ҩƷΪ�ǿ�����,��ֹ����");
			table.setItem(row, "ORDER_DESC", "");
			return;
		} else if (this.isSame(table, orderCode, row)) {
			if (messageBox("��ʾ��Ϣ", "����ͬҽ���Ƿ�����ҽ��?", this.YES_NO_OPTION) != 0) {
				table.setItem(row, "ORDER_DESC", "");
				return;
			} else {
				insertPackOrder(row, parm);
				
				if (row == table.getRowCount() - 1)
					this.insertOrderRow();
			}
		} else {
			insertPackOrder(row, parm);
			if (row == table.getRowCount() - 1)
				this.insertOrderRow();
		}
	}
	
	/**
	 * �ж��Ƿ�����ͬ��ҩ��
	 * @param table
	 * @param orderCode
	 * @param row
	 * @return
	 */
	public boolean isSame(TTable table, String orderCode, int row) {
		table.acceptText();
		for (int i = 0; i < table.getRowCount(); i++) {
			if (i == row)
				continue;
			String orderCodeC = table.getItemString(i, "ORDER_CODE");
			if (orderCodeC.equals(orderCode))
				return true;
		}
		return false;
	}
	
	/**
	 * �ж��Ƿ�Ϊ������
	 */
	private boolean isAnti(String orderCode) {
		TParm actionDs = new TParm();
		actionDs.setData("ORDER_CODE", orderCode);
		TParm resultDs = OdiMainTool.getInstance().queryPhaBase(actionDs);
		if (resultDs.getValue("ANTIBIOTIC_CODE", 0).equals("")) {
			return false;
		} else {
			return true;
		}
	}
//	/**
//	 * �ж��Ƿ�ͣ��
//	 */
//	private boolean isActive(String orderCode) {
//		TParm actionDs = new TParm();
//		actionDs.setData("ORDER_CODE", orderCode);
//		TParm resultDs = OdiMainTool.getInstance().querySysFee(actionDs);
//		if (resultDs.getValue("ACTIVE_FLG", 0).equals("N")) {
//			return false;
//		} else {
//			return true;
//		}
//	}
	
	private void insertPackOrder(int row, TParm parm) {
		// TODO Auto-generated method stub
		
		table.acceptText();
        
		table.setItem(row, "FLAG", "Y");
		table.setItem(row, "DATA_FLG", "Y");
		table.setItem(row, "ORDER_CODE", parm.getData("ORDER_CODE"));
		table.setItem(row, "ORDER_DESC", parm.getData("ORDER_DESC"));
		table.setItem(row, "SPECIFICATION", parm.getData("SPECIFICATION"));
		 TParm tempParm=new TParm(TJDODBTool.getInstance().select("SELECT FREQ_CODE,ROUTE_CODE FROM PHA_BASE WHERE ORDER_CODE='"+parm.getData("ORDER_CODE")+"'"));
		if (tempParm.getCount()>0) {
			table.setItem(row, "FREQ_CODE", tempParm.getData("FREQ_CODE",0));
			table.setItem(row, "FREQ_DESC_CODE", tempParm.getData("FREQ_CODE",0));
			table.setItem(row, "ROUTE_DESC_CODE", tempParm.getData("ROUTE_CODE",0));
			table.setItem(row, "ROUTE_CODE", tempParm.getData("ROUTE_CODE",0));
		}else{
			table.setItem(row, "FREQ_CODE", "");
			table.setItem(row, "ROUTE_CODE", "");
			table.setItem(row, "FREQ_DESC_CODE", "");
			table.setItem(row, "ROUTE_DESC_CODE", "");
		}
		//table.setItem(row, "FREQ_DESC_CODE", parm.getData("FREQ_DESC_CODE"));
		// �õ�PHA_BASE����
		TParm actionDs = new TParm();
		actionDs.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
		TParm resultDs = OdiMainTool.getInstance().queryPhaBase(actionDs);
		//System.out.println("---resultDs--"+resultDs);
		table.setItem(row, "MEDI_QTY", resultDs.getData("MEDI_QTY", 0));
		table.setItem(row, "MEDI_UNIT", resultDs.getData("MEDI_UNIT", 0));
		table.setItem(row, "MEDI_UNIT_DESC", resultDs.getData("MEDI_UNIT", 0));
//		if (row >= 0) {
//			String ROUTE_CODE = table.getItemString(row - 1, "ROUTE_CODE");
//			if (ROUTE_CODE.length() != 0) {
//				table.setItem(row, "ROUTE_CODE", ROUTE_CODE);
//			} else {
//				table.setItem(row, "ROUTE_CODE", resultDs.getData(
//						"ROUTE_CODE", 0));
//			}
//			String FREQ_CODE = table.getItemString(row - 1, "FREQ_CODE");
//			if (FREQ_CODE.length() != 0) {
//				table.setItem(row, "FREQ_CODE", FREQ_CODE);
//			} else {
//				table.setItem(row, "FREQ_CODE", resultDs.getData(
//						"FREQ_CODE", 0));
//			}
//		} else {
//			table.setItem(row, "ROUTE_CODE", resultDs.getData(
//					"ROUTE_CODE", 0));
//			table.setItem(row, "FREQ_CODE", "");
//		}
	}
	/**
	 * ҽ���޸��¼�����
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onChangeTableValue(Object obj) {
		// this.messageBox("================onChangeTableValueST=====================");
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
//		if (node.getValue().equals(node.getOldValue()))
//			return true;
		// �õ�table�ϵ�parmmap������
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		// �жϵ�ǰ���Ƿ���ҽ��
		int selRow = node.getRow();
//		TParm orderP = this.getTTable("TABLE2").getDataStore().getRowParm(selRow);
//		// System.out.println("=======orderP======="+orderP);
//
//		// this.messageBox("================11111=====================");
//		if (orderP.getValue("ORDER_CODE").length() == 0) {
//			// ���ҽ������
////			clearRow("ST", selRow, "ORDER_DESC");
//			this.getTTable("TABLE2").setDSValue(selRow);
//		}
		if ("ANTI_REASON".equals(columnName)) {
			getTTable("TABLE2").setItem(selRow, "ANTI_REASON_CODE",node.getValue());
//			String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'ANTI_REASON'";
//			TParm inparm = new TParm(TJDODBTool.getInstance().select(sql));
//			System.out.println("==========");
//			for (int i = 0; i < inparm.getCount(); i++) {
//				System.out.println("==========inparm.getValue()"
//						+ inparm.getValue("ID", i));
//				if (node.getValue().equals(inparm.getValue("CHN_DESC", i))) {
//					
//				}
//			}
		}
		if ("FREQ_DESC_CODE".equals(columnName)) {
			getTTable("TABLE2").setItem(selRow, "FREQ_CODE",node.getValue());
		}
		if ("ROUTE_DESC_CODE".equals(columnName)) {
			getTTable("TABLE2").setItem(selRow, "ROUTE_CODE",node.getValue());
		}
		return false;
	}
	
	/**
	 * ���� add caoyong 20131008
	 */
	public void onSend() {
		this.table.acceptText();
		TParm parm = table.getShowParmValue();
		int rowCount = parm.getCount();
		TParm result=new TParm();
		StringBuffer but=new StringBuffer();
		for (int i = 0; i < rowCount; i++) {
			if (!parm.getBoolean("FLAG",i)){
				continue;
			}
			
		    if(!"".equals(parm.getValue("ORDER_CODE",i))){
		    	//ȡ������е����ʹ������,yanjing,20131022
		    	int days = TypeTool.getInt(table.getItemData(i, "ANTI_TAKE_DAYS"));
		    	if(days <= 0){
		    		this.messageBox("����дʹ������");
		    		return;
		    	}
//		    	if(isActive(parm.getValue("ORDER_CODE",i))){
//		    		this.messageBox(parm.getValue("ORDER_DESC",i)+"��ͣ�á�");
//		    		return;
//		    	}
		    	String antiReason= TypeTool.getString(table.getItemData(i, "ANTI_REASON"));
		    	if (null==antiReason || antiReason.trim().length()<=0) {
					this.messageBox("����д����ԭ��");
					return;
				}
		    	parm.addData("MR_NO",this.getMrNo() );
		    	parm.addData("CASE_NO", this.caseNo);
		    	parm.addData("APPROVE_FLG", "Y");// ����ע��
		    	parm.addData("USE_FLG", "N");
		    	parm.addData("OPT_USER", Operator.getID());
		    	parm.addData("OPT_TERM", Operator.getIP());
		    	parm.addData("OVERRIDE_FLG", "N");
		    	parm.addData("CHECK_FLG", "N");
				result.addRowData(parm, i);
				System.out.println("result::::::"+result);
//				System.out.println("=====�ش����� result is:"+result);
				if(i==rowCount-2){//��ɵ�ÿ��ҽ���ӡ����������ӡ�����
					//ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT_DESC;ROUTE_DESC_CODE;FREQ_DESC_CODE;
				      but.append("ҩƷ��"+result.getValue("ORDER_DESC",i)+" ���"+result.getValue("SPECIFICATION",i)+" ������"+result.getValue("MEDI_QTY",i)+result.getValue("MEDI_UNIT_DESC",i)+
						    " Ƶ�Σ�"+result.getValue("ROUTE_DESC_CODE",i)+result.getValue("FREQ_DESC_CODE",i)).append("��");
				
				}else{
					  but.append("ҩƷ��"+result.getValue("ORDER_DESC",i)+" ���"+result.getValue("SPECIFICATION",i)+" ������"+result.getValue("MEDI_QTY",i)+result.getValue("MEDI_UNIT_DESC",i)+
							 " Ƶ�Σ�"+result.getValue("ROUTE_DESC_CODE",i)+result.getValue("FREQ_DESC_CODE",i)).append("��");
								
				}
		    }
		  }
		       this.setReturnValue(but);

		if (result.getCount("FLAG")<=0) {
			this.messageBox("��ѡ����Ҫ���ص�����");
			return;
		}
		TParm result1 = TIOM_AppServer.executeAction(
				"action.pha.PHAAntiAction", "onSavePhaAnti", result);
		if(result1.getErrCode()<0){
    		 this.messageBox("���ʧ��");
    		 return ;
    	 }
		
		this.closeWindow();
	}
	/**
	 * ɾ�� add  caoyong 20131009
	 */
	public void onDeleteRoworder() {
		TTable table = getTTable("TABLE2");
		int row = table.getTable().getSelectedRow();
		if(row<0||table.getItemData(row, "ORDER_DESC").equals("")){
			this.messageBox("��ѡ����Ҫɾ��������");
			return;
		}
		table.removeRow(row);
	}
}
