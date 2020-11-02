package com.javahis.ui.opb;

import java.awt.Color;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;


/**
*
* <p>Title:�ȶԡ������ײͺ��ѿ�ҽ�� </p>
*
* <p>Description:�ȶԡ������ײͺ��ѿ�ҽ�� </p>
*
* <p>Company:Bluecore </p>
*
* @author huangtt 20141016
* @version 1.0
*/
public class OPBOrderPackageComparisonControl extends TControl{
	public static String TREE = "Tree";
    TTreeNode treeRoot;
    TParm treedata;
    TParm TABLEPACKAGEDATA = new TParm();
    private TTable tablePack;
    private TTable tableOrder;
    private TTable tableOrderM; //�ײ���ҽ��
    private TTable tablePackOrder;
    String mrNo="";
    String caseNo="";
    String tradeNo="";
    String packSecID=""; //�����id
    String sectionCode=""; //����
    
    public void onInit() {
        super.onInit();
        
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			TParm acceptData = (TParm) obj;
			mrNo = acceptData.getData("MR_NO").toString();
			caseNo = acceptData.getData("CASE_NO").toString();
		}
		tablePack = getTable("TABLEPACK");
		tableOrder = getTable("TABLEORDER");
		tableOrderM = getTable("TABLEORDER_M");
		tablePackOrder = getTable("TABLE");
		getOrder();
        //��ʼ����
        onInitTree();
        //����ѡ������Ŀ
        addEventListener(TREE + "->" + TTreeEvent.CLICKED, "onTreeClicked");
        
        this.callFunction("UI|TABLEPACK|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"selExecOrUser");
        
//        this.callFunction("UI|TABLEORDER_M|addEventListener",
//				TTableEvent.CHECK_BOX_CLICKED, this,
//				"selExecMem");

       
    }
    
   
    

    /**
     * ��ʼ����
     */
    public void onInitTree() {
        treeRoot = (TTreeNode) callMessage("UI|" + TREE + "|getRoot");
        if (treeRoot == null)
            return;
        //----------------------//
        treeRoot.setText("�ײ�ʱ��");
        treeRoot.setID("ROOT");
        treeRoot.setType("ROOT");
        treeRoot.removeAllChildren();
        initPackageSession(treeRoot);
        //----------------------//
        callMessage("UI|" + TREE + "|update");
    }
    
    /**
     * װ���ײ�ʱ��
     * @param parentNode TTreeNode
     */
    public void initPackageSession(TTreeNode parentNode) {
		TABLEPACKAGEDATA = new TParm(TJDODBTool.getInstance().select(
				"SELECT ID,TRADE_NO,SECTION_DESC,SECTION_CODE FROM MEM_PAT_PACKAGE_SECTION WHERE MR_NO='"+ mrNo + "' AND REST_TRADE_NO IS NULL"));
		if (TABLEPACKAGEDATA == null)
			return;
		for (int i = 0; i < TABLEPACKAGEDATA.getCount(); i++) {
			String id = TABLEPACKAGEDATA.getValue("TRADE_NO", i) + "#"
					+ TABLEPACKAGEDATA.getValue("ID", i) + "#"
					+ TABLEPACKAGEDATA.getValue("SECTION_CODE", i);
			String name = TABLEPACKAGEDATA.getValue("SECTION_DESC", i);
			String type = "PACKAGE";
			TTreeNode node = new TTreeNode(name, type);
			node.setID(id);
			parentNode.add(node);
		}
    }
    
    /**
     * �����
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        if ("ROOT".equals(node.getType())) {
            onInitTree();
            return;
        }
        if ("PACKAGE".equals(node.getType())) {
        	getOrder();
            String id = node.getID();
            String[] aa = id.split("#");
            tradeNo= aa[0];
            packSecID=aa[1];
            sectionCode=aa[2];
            typeNodeClick(tradeNo,sectionCode);
            //�ײͺ��ѿ���ҽ���ȶ�
            onComparison();
           return;
        }

    }
   
    /**
     * �ײ����ѿ���ҽ���ȶ�
     * @param tradeNo
     */
    public void onComparison(){
    	TParm parmPack = tablePack.getParmValue();
    	TParm parmOrder = tableOrder.getParmValue();
    	TParm result = new TParm();
    	for(int i=0;i<parmOrder.getCount("USED_FLG");i++){
    		if(!parmOrder.getBoolean("USED_FLG", i)){
    			for(int j=0;j<parmPack.getCount("USED_FLG");j++){
        			if(!parmPack.getBoolean("USED_FLG", j)){
        				if(parmPack.getValue("ORDER_CODE", j).equals(parmOrder.getValue("ORDER_CODE", i)) &&
        						parmPack.getValue("UNIT_CODE", j).equals(parmOrder.getValue("MEDI_UNIT", i)) &&
        						parmPack.getInt("ORDER_NUM", j) == parmOrder.getInt("DOSAGE_QTY", i)){


        					result.addData("SEQ_NO", parmOrder.getValue("SEQ_NO", i));
        					result.addData("ORDER_CODE", parmPack.getValue("ORDER_CODE", j));
        					result.addData("ORDER_CODE1", parmOrder.getValue("ORDER_CODE", i));
        					result.addData("ORDER_DESC", parmOrder.getValue("ORDER_DESC", i));
        					result.addData("ORDERSET_GROUP_NO", parmOrder.getValue("ORDERSET_GROUP_NO", i));
        					result.addData("RX_TYPE", parmOrder.getValue("RX_TYPE", i));
        					result.addData("RX_NO", parmOrder.getValue("RX_NO", i));
        					result.addData("ORDERSET_CODE", parmOrder.getValue("ORDERSET_CODE", i));
        					result.addData("MEDI_UNIT", parmOrder.getValue("MEDI_UNIT", i));
        					result.addData("DOSAGE_QTY", parmOrder.getDouble("DOSAGE_QTY", i));
        					result.addData("OWN_PRICE", parmOrder.getDouble("OWN_PRICE",i));
        					result.addData("AR_AMT", parmOrder.getDouble("AR_AMT", i));
        					result.addData("TRADE_NO", tradeNo);
        					result.addData("ID", parmPack.getValue("ID", j));
        					
        					parmOrder.setData("USED_FLG", i, "Y");
        					parmOrder.setData("COMP_FLG", i, "Y");
        					parmPack.setData("USED_FLG", j, "Y");
        					parmPack.setData("COMP_FLG", j, "Y");
        					break;
        				}
        			}
        		}
    		}
    		
    	}
    	if(result != null){
    		tablePackOrder.setParmValue(result);
        	tablePack.setParmValue(parmPack);
        	tableOrder.setParmValue(parmOrder);
        	onLockTable(tablePack);
        	onLockTable(tableOrder);
    	}
   	
    }
    
    public void onSave(){
    	tablePackOrder.acceptText();
    	tableOrderM.acceptText();
    	TParm packOrderParm = tablePackOrder.getParmValue(); 
    	TParm packParm = tablePack.getParmValue();
    	TParm execPack = new TParm();
    	if(packParm != null){
    		for(int i=0;i<packParm.getCount("EXEC_FLG");i++){
        		if(packParm.getBoolean("EXEC_FLG", i)){
        			execPack.addData("ID", packParm.getData("ID", i));
        			execPack.addData("TRADE_NO", tradeNo);
        			execPack.addData("ORDER_CODE", packParm.getData("ORDER_CODE", i));
        		}
        	}
    	}
    	
    	execPack.setCount(execPack.getCount("ID"));
    	
    	TParm orderParmM = tableOrderM.getParmValue();
//    	System.out.println("orderParmM----"+orderParmM);
    	TParm orderMemParm = new TParm();
    	if(orderParmM != null){
    		for (int i = 0; i < orderParmM.getCount("USED_FLG"); i++) {
    			if(orderParmM.getBoolean("USED_FLG", i)){
    				orderMemParm.addData("MEM_PACKAGE_ID", orderParmM.getValue("MEM_PACKAGE_ID", i));
    				orderMemParm.addData("ORDER_CODE", orderParmM.getValue("ORDER_CODE", i));
    			}
    		}
    	}
    	
    	orderMemParm.setCount(orderMemParm.getCount("MEM_PACKAGE_ID"));
    	
    	
    	if(packOrderParm == null){
    		packOrderParm = new TParm();
    	}
    	
    	if(packOrderParm.getCount("ORDER_CODE") < 0 && execPack.getCount() < 0 && orderMemParm.getCount() < 0){
    		this.messageBox("û����Ҫ���������");
    		return;
    	}
    	packOrderParm.setData("mrNo", mrNo);
    	packOrderParm.setData("caseNo", caseNo);
    	packOrderParm.setData("packSecID", packSecID);    	
    	packOrderParm.setData("tradeNo", tradeNo);    	
    	packOrderParm.setData("execPack", execPack.getData());    	
    	packOrderParm.setData("orderMemParm", orderMemParm.getData());    	
    	TParm parm = TIOM_AppServer.executeAction("action.mem.MEMPackageSectionAction",
				"updateOrderPack", packOrderParm);
    	if(parm.getErrCode()<0){
    		if(parm.getErrCode() == -2){
    			this.messageBox(parm.getValue("MESSAGE"));
    			return;
    		}
    		this.messageBox("����ʧ�ܣ�");
			return;
    	}
    	this.messageBox("����ɹ���");
    	//20161228 add by huangtt ���ڱ�����ʱ����������ײ�ϸ�� δʹ�ã���Ҫ�������Ϊδʹ��
    	if(orderMemParm.getCount() > 0){
    		String sqlwhere = "";
    		for (int i = 0; i < orderMemParm.getCount(); i++) {
    			sqlwhere+= "'"+orderMemParm.getValue("MEM_PACKAGE_ID",i)+"',";
    		}
    		
    		sqlwhere = sqlwhere.substring(0, sqlwhere.length()-1);
    		
        	String sql = "SELECT TRADE_NO,PACKAGE_CODE,SECTION_CODE FROM MEM_PAT_PACKAGE_SECTION_D WHERE ID IN("+sqlwhere+") GROUP BY TRADE_NO,PACKAGE_CODE,SECTION_CODE";
        	
        	TParm parmMem = new TParm(TJDODBTool.getInstance().select(sql));
        	for (int i = 0; i < parmMem.getCount(); i++) {// modify by kangy 20170817 ��ʱ����������Ŀ����δʹ��״̬ʱ,����ʱ��״̬�����޸�Ϊδʹ��
        		String tradeNo = parmMem.getValue("TRADE_NO", i);
			/*	String sqlD = "SELECT COUNT(ID) USEDCOUNT FROM MEM_PAT_PACKAGE_SECTION_D WHERE TRADE_NO='"+tradeNo+"' AND USED_FLG='1'";
				TParm parmDCount = new TParm(TJDODBTool.getInstance().select(sqlD));
				if(parmDCount.getInt("USEDCOUNT", 0) == 0){
					String sqlM = "UPDATE MEM_PAT_PACKAGE_SECTION SET USED_FLG='0' WHERE TRADE_NO='"+tradeNo+"'";
					TParm parmM = new TParm(TJDODBTool.getInstance().update(sqlM));
				}*/
	    		String packageCode = parmMem.getValue("PACKAGE_CODE", i);
	    		String sectionCode = parmMem.getValue("SECTION_CODE", i);
	    		String sqlD="SELECT COUNT(ID) USEDCOUNT FROM MEM_PAT_PACKAGE_SECTION_D WHERE TRADE_NO='"+tradeNo+"'" +
	    				" AND PACKAGE_CODE='"+packageCode+"' AND SECTION_CODE='"+sectionCode+"'" +
	    				" AND USED_FLG='1' AND HIDE_FLG='N'";
	    		TParm parmDCount = new TParm(TJDODBTool.getInstance().select(sqlD));
				if(parmDCount.getInt("USEDCOUNT", 0) == 0){
					String sqlM = "UPDATE MEM_PAT_PACKAGE_SECTION SET USED_FLG='0' WHERE TRADE_NO='"+tradeNo+"'" +
							      " AND  PACKAGE_CODE='"+packageCode+"' AND SECTION_CODE='"+sectionCode+"'";
					TParm parmM = new TParm(TJDODBTool.getInstance().update(sqlM));
				}
			}
    	}
    	
    	onClear();
    }
    
    public void onAdd(){
    	tablePack.acceptText();
    	tableOrder.acceptText();
    	if(onCheck(tablePack) && onCheck(tableOrder)){
    		int packRow = -1;
        	int orderRow = -1;
        	TParm packParm = tablePack.getParmValue();
        	TParm orderParm = tableOrder.getParmValue();
        	TParm packOrderParm = tablePackOrder.getParmValue();
        	//System.out.println("packOrderParm="+packOrderParm);
        	if(packOrderParm == null){
        		packOrderParm = new TParm();
        	}
        	for(int i=0;i<packParm.getCount("USED_FLG");i++){
        		if(packParm.getBoolean("USED_FLG", i) && !packParm.getBoolean("COMP_FLG", i)){
        			packRow = i;
        		}
        	}
        	for(int i=0;i<orderParm.getCount("USED_FLG");i++){
        		if(orderParm.getBoolean("USED_FLG", i) && !orderParm.getBoolean("COMP_FLG", i)){
        			orderRow = i;
        		}
        	}

        	if(packRow == -1 || orderRow == -1){
        		return;
        	}

        	if(packParm.getValue("ORDER_CODE", packRow).equals(orderParm.getValue("ORDER_CODE", orderRow)) ||
        		("9".equals(orderParm.getValue("RX_TYPE",orderRow)) && "CLINIC_FEE".equals(orderParm.getValue("RX_NO",orderRow)))){
        		packParm.setData("USED_FLG", packRow, "Y");
        		packParm.setData("COMP_FLG", packRow, "Y");
        		orderParm.setData("USED_FLG", orderRow, "Y");
        		orderParm.setData("COMP_FLG", orderRow, "Y");
        		

        		packOrderParm.addData("SEQ_NO", orderParm.getValue("SEQ_NO", orderRow));
        		packOrderParm.addData("ORDER_CODE", packParm.getValue("ORDER_CODE", packRow));  //�ײ͵�orderCode
        		packOrderParm.addData("ORDER_CODE1", orderParm.getValue("ORDER_CODE", orderRow)); //����ҽ����orderCoder
        		packOrderParm.addData("ORDER_DESC", orderParm.getValue("ORDER_DESC", orderRow));
        		packOrderParm.addData("ORDERSET_GROUP_NO", orderParm.getValue("ORDERSET_GROUP_NO", orderRow));
        		packOrderParm.addData("RX_NO", orderParm.getValue("RX_NO", orderRow));
        		packOrderParm.addData("RX_TYPE", orderParm.getValue("RX_TYPE", orderRow));
        		packOrderParm.addData("ORDERSET_CODE", orderParm.getValue("ORDERSET_CODE", orderRow));
        		packOrderParm.addData("MEDI_UNIT", orderParm.getValue("MEDI_UNIT", orderRow));
        		packOrderParm.addData("DOSAGE_QTY", orderParm.getDouble("DOSAGE_QTY", orderRow));
        		packOrderParm.addData("OWN_PRICE", orderParm.getDouble("OWN_PRICE",orderRow));
        		packOrderParm.addData("AR_AMT", orderParm.getDouble("AR_AMT", orderRow));
        		packOrderParm.addData("TRADE_NO", tradeNo);
        		packOrderParm.addData("ID", packParm.getValue("ID", packRow));
        		
        		tablePackOrder.setParmValue(packOrderParm);
            	tablePack.setParmValue(packParm);
            	tableOrder.setParmValue(orderParm);
            	onLockTable(tablePack);
            	onLockTable(tableOrder);
        	}else{
        		this.messageBox("ҽ�����벻ͬ��������������ݣ�");
        		return;
        	}
    	}else{
    		this.messageBox("��һ��һ���бȶ���ӣ�");
    	}
   	
    }
    
    public void onDelete(){
    	int row = tablePackOrder.getSelectedRow();
    	if(row == -1)
    		return;
    	TParm packOrderParm = tablePackOrder.getParmValue();
    	TParm packParm = tablePack.getParmValue();
    	TParm orderParm = tableOrder.getParmValue();
    	String orderCode = packOrderParm.getValue("ORDER_CODE", row);
    	for(int i=0;i<packParm.getCount("USED_FLG");i++){
    		if(orderCode.equals(packParm.getValue("ORDER_CODE", i))){
    			packParm.setData("USED_FLG", i, "N");
    			packParm.setData("COMP_FLG", i, "N");
    			break;
    		}
    	}
    	for(int i=0;i<orderParm.getCount("USED_FLG");i++){
    		if(orderCode.equals(orderParm.getValue("ORDER_CODE", i))){
    			orderParm.setData("USED_FLG", i, "N");
    			orderParm.setData("COMP_FLG", i, "N");
    			break;
    		}
    	}
    	tablePack.setParmValue(packParm);
    	tableOrder.setParmValue(orderParm);
    	onLockTable(tablePack);
    	onLockTable(tableOrder);
    	tablePackOrder.removeRow(row);
    	
    }
    
    /**
     * ��ѯ��ǰʱ����ϸ����table��ֵ��Tree��
     * @param quegroupCode String
     */
    public void typeNodeClick(String tradeNo,String sectionCode) {
    	String sql = "SELECT   (CASE USED_FLG" +
    			" WHEN '1' THEN 'Y'" +
    			" WHEN '0' THEN 'N'" +
    			"  END) AS USED_FLG, (CASE USED_FLG" +
    			" WHEN '1' THEN 'Y'" +
    			" WHEN '0' THEN 'N'" +
    			"  END) AS COMP_FLG , 'N' EXEC_FLG, SECTION_DESC, ORDER_CODE, ORDER_DESC, ORDER_NUM," +
    			" UNIT_CODE, UNIT_PRICE, RETAIL_PRICE,ID " +
    			" FROM MEM_PAT_PACKAGE_SECTION_D" +
    			" WHERE TRADE_NO = '"+tradeNo+"' AND " +
    			" SECTION_CODE = '"+sectionCode+"' AND HIDE_FLG = 'N' ORDER BY SEQ";
//    	System.out.println("sql=="+sql);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	tablePack.setParmValue(parm);
    	onLockTable(tablePack);
    	
    	
    }
    
    
    public boolean onCheck(TTable table){
    	boolean flg = true;
    	table.acceptText();
    	TParm tableParm = table.getParmValue();
    	int count =0;
    	for(int i=0;i<tableParm.getCount("USED_FLG");i++){
    		if(tableParm.getBoolean("USED_FLG", i) && !tableParm.getBoolean("COMP_FLG", i)){
    			count++;
    		}
    	}
    	if(count > 1){
    		flg=false;
    	}
    	return flg;
    }
    
    /**
     * �������
     */
    public void onLockTable(TTable table ){
    	table.acceptText();
    	String lock = "";
    	for(int i=0;i<table.getRowCount();i++){
    		if("Y".equals(table.getItemString(i, "USED_FLG"))){
    			lock = lock + i + ",";
    		}
    	}
    	if(lock.length()>0){
    		lock = lock.substring(0, lock.length()-1);
    		
    	}
    	table.setLockRows(lock);
    }
    
    /**
     * �õ��ѿ�����û�мƷѵ�ҽ��
     */
    public void getOrder(){
    	String sql = "SELECT 'N' USED_FLG,'N' COMP_FLG, OPD_ORDER.* FROM OPD_ORDER" +
    			" WHERE CASE_NO = '"+caseNo+"' AND MEM_PACKAGE_ID IS NULL AND BILL_FLG='N' AND PRINT_FLG = 'N'";
    	TParm parmOrder = new TParm(TJDODBTool.getInstance().select(sql));
    	tableOrder.setParmValue(tableShow(parmOrder));
    	setExecColor();
    	
    	sql = "SELECT 'N' USED_FLG, OPD_ORDER.* FROM OPD_ORDER" +
		" WHERE CASE_NO = '"+caseNo+"' AND MEM_PACKAGE_ID IS NOT NULL AND BILL_FLG='N' AND PRINT_FLG = 'N'";
    	TParm parmOrderM = new TParm(TJDODBTool.getInstance().select(sql));
    	tableOrderM.setParmValue(tableShow(parmOrderM));
    	
    	
    }
    
    /**
	 * ������ִ����ɫ
	 */
	private void setExecColor(){
		TParm parm = tableOrder.getParmValue();
		for (int i = 0; i < tableOrder.getRowCount(); i++) {
			tableOrder.setRowColor(i, null);
			if("9".equals(parm.getValue("RX_TYPE",i)) && "CLINIC_FEE".equals(parm.getValue("RX_NO",i))){
				tableOrder.setRowColor(i, Color.YELLOW);
			}
		}
	}
    
    /**
	 * ����ҽ������ϸ��
	 * 
	 * @param parm
	 *            TParm
	 */
    public TParm tableShow(TParm parm){    	
    	// ҽ������
		String orderCode = "";
		// ҽ�����
		int groupNo = -1;
		// ���㼯��ҽ�����ܷ���
		double fee = 0.0;
		double ownPrice = 0;
		// ҽ������
		int count = parm.getCount("ORDER_CODE");
		// ��Ҫɾ����ϸ���б�
		int[] removeRow = new int[count < 0 ? 0 : count]; // =====pangben modify
		// 20110801
		int removeRowCount = 0;
		// ѭ��ҽ��
		for (int i = 0; i < count; i++) {
			// ������Ǽ���ҽ������
			if (parm.getValue("SETMAIN_FLG", i) != null
					&& !parm.getValue("SETMAIN_FLG", i).equals("Y")) {
				continue;
			}
			groupNo = -1;
			fee = 0.0;
			ownPrice = 0;
			// ҽ������
			orderCode = parm.getValue("ORDER_CODE", i);
			String rxNo =  parm.getValue("RX_NO", i);
			// �� 
			groupNo = parm.getInt("ORDERSET_GROUP_NO", i);
			// ���������ѭ������ҽ������ϸ��
			for (int j = i; j < count; j++) {
				// �������������ϸ��
				if (orderCode.equals(parm.getValue("ORDERSET_CODE", j))
						&& parm.getInt("ORDERSET_GROUP_NO", j) == groupNo
						&& !parm.getValue("ORDER_CODE", j).equals(
								parm.getValue("ORDERSET_CODE", j))
						&& rxNo.equals(parm.getValue("RX_NO", j))) {
					// �������
					fee += parm.getDouble("AR_AMT", j);
					ownPrice += parm.getDouble("OWN_AMT", j) * parm.getDouble("DISPENSE_QTY", j);
					// ����Ҫɾ������
					removeRow[removeRowCount] = j;
					// �Լ�
					removeRowCount++;
				}
			}
			// ϸ����ð�����
			parm.setData("AR_AMT", i, fee);
			parm.setData("OWN_PRICE", i, ownPrice);
		}
		// ɾ������ҽ��ϸ��
		if (removeRowCount > 0) {
			for (int i = removeRowCount - 1; i >= 0; i--) {
				parm.removeRow(removeRow[i]);
			}

		}   	
    	return parm;
    	
    }
    
    public void onClear(){
    	tablePackOrder.removeRowAll();
    	tablePack.removeRowAll();
    	tableOrder.removeRowAll();
    	tradeNo="";
        packSecID=""; //�����id
        sectionCode="";
        getOrder();
        onInitTree();
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
	
	public void selExecOrUser(Object obj){
		tablePack=(TTable) obj;
		tablePack.acceptText();
		int row = tablePack.getSelectedRow();
		if ("Y".equals(tablePack.getItemString(row, "USED_FLG")) && "Y"
				.equals(tablePack.getItemString(row, "EXEC_FLG"))) {
			this.messageBox("������ȶ���ִ��һ��ѡ��");
			tablePack.setItem(row, "USED_FLG", "N");
			tablePack.setItem(row, "EXEC_FLG", "N");
		}
		
	}
	
	
}
