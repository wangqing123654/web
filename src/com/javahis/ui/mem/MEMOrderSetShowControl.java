package com.javahis.ui.mem;


import jdo.hrm.HRMPackageD;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
/**
 * <p>Title: ��Ա�ײ��趨�޸�ҽ��ϸ��</p>
 *
 * <p>Description: ��Ա�ײ��趨�޸�ҽ��ϸ��</p>
 *
 * <p>Copyright: javahis 20140103</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author duzhw
 * @version 4.5
 */
public class MEMOrderSetShowControl extends TControl {
		private TTable table;
	    private HRMPackageD packD;// �ײ�ϸ��
	    String oper = "";
	    String sectionCode = "";
	    String packageCode = "";
	    String priceType = "";
	    String ordersetGroupNo = "";
	    double rate=0;
	    String id = "";
	    TParm delParm = new TParm();

	    public void onInit() {
	        super.onInit();
	        //��ʼ���ؼ�
			initComponent();
	        initParam();
	    }
	    
	    /**
		 * ��ʼ���ؼ�
		 */
	    private void initComponent() {
	    	table=(TTable)this.getComponent("TABLE");
			table.addEventListener("TABLE->"+ TTableEvent.CHANGE_VALUE,this, "onValueChange");
	    	
	    }
	    
		/**
		 * ��ʼ������
		 */
		public void initParam() {
			TParm parm=(TParm)this.getParameter();
			// System.out.println("parm="+parm);
			String orderSetCode = parm.getValue("ORDERSET_CODE");
			String exec = parm.getValue("EXEC");
			sectionCode = parm.getValue("SECTION_CODE");
			packageCode = parm.getValue("PACKAGE_CODE");
			priceType = parm.getValue("PRICE_TYPE");
			ordersetGroupNo = parm.getValue("ORDERSET_GROUP_NO");
			id = parm.getValue("ID");
			rate = parm.getDouble("DISCOUNT_RATE"); //add by huangtt 20141106
//			System.out.println("rate=="+rate); 
			String sql = "";
			//������
//			String sql1 = "SELECT B.ID,A.ORDER_CODE,B.ORDERSET_CODE,A.ORDER_DESC, A.SPECIFICATION,B.ORDER_NUM AS DOSAGE_QTY," +
//					" B.UNIT_CODE AS MEDI_UNIT,B.UNIT_PRICE AS OWN_PRICE,C.RETAIL_PRICE AS PACKAGE_PRICE," +
//					" B.ORDER_NUM * C.RETAIL_PRICE AS OWN_AMT, A.EXEC_DEPT_CODE, A.OPTITEM_CODE, A.INSPAY_TYPE," +
//					" B.SECTION_CODE,B.PACKAGE_CODE,B.SETMAIN_FLG,B.ORDERSET_CODE,B.ORDERSET_GROUP_NO,B.HIDE_FLG" +
//					" FROM SYS_FEE A, MEM_PACKAGE_SECTION_D B, MEM_PACKAGE_SECTION_D_PRICE C" +
//					" WHERE A.ORDER_CODE = B.ORDER_CODE and A.ACTIVE_FLG = 'Y' AND B.HIDE_FLG='Y' " +
//					" AND B.ORDERSET_CODE = '"+orderSetCode+"' AND B.PACKAGE_CODE = '"+packageCode+"' AND B.SECTION_CODE = '"+sectionCode+"'" +
//					" AND B.PACKAGE_CODE = C.PACKAGE_CODE" +
//					" AND B.SECTION_CODE = C.SECTION_CODE" +
//					" AND B.ID = C.ID" +
//					" AND C.PRICE_TYPE = '"+priceType+"'" +
//					" AND B.ORDERSET_GROUP_NO = '"+ordersetGroupNo+"'";
			
			String sql1 = "SELECT B.ID,A.ORDER_CODE,B.ORDERSET_CODE,A.ORDER_DESC, A.SPECIFICATION,B.ORDER_NUM AS DOSAGE_QTY," +
			" B.UNIT_CODE AS MEDI_UNIT,B.UNIT_PRICE AS OWN_PRICE,C.RETAIL_PRICE AS PACKAGE_PRICE," +
			" C.RETAIL_PRICE AS OWN_AMT, A.EXEC_DEPT_CODE, A.OPTITEM_CODE, A.INSPAY_TYPE," +
			" B.SECTION_CODE,B.PACKAGE_CODE,B.SETMAIN_FLG,B.ORDERSET_CODE,B.ORDERSET_GROUP_NO,B.HIDE_FLG" +
			" FROM SYS_FEE A, MEM_PACKAGE_SECTION_D B, MEM_PACKAGE_SECTION_D_PRICE C" +
			" WHERE A.ORDER_CODE = B.ORDER_CODE and A.ACTIVE_FLG = 'Y' AND B.HIDE_FLG='Y' " +
			" AND B.ORDERSET_CODE = '"+orderSetCode+"' AND B.PACKAGE_CODE = '"+packageCode+"' AND B.SECTION_CODE = '"+sectionCode+"'" +
			" AND B.PACKAGE_CODE = C.PACKAGE_CODE" +
			" AND B.SECTION_CODE = C.SECTION_CODE" +
			" AND B.ID = C.ID" +
			" AND C.PRICE_TYPE = '"+priceType+"'" +
			" AND B.ORDERSET_GROUP_NO = '"+ordersetGroupNo+"'";
			
//			System.out.println("sql1111111111"+sql1);
			//������
			String sql2 = "SELECT A.ORDER_CODE,B.ORDERSET_CODE,A.ORDER_DESC, A.SPECIFICATION, B.DOSAGE_QTY," +
					" A.UNIT_CODE AS MEDI_UNIT, A.OWN_PRICE,A.OWN_PRICE AS PACKAGE_PRICE, A.OWN_PRICE * B.DOSAGE_QTY " +
					" AS OWN_AMT, A.EXEC_DEPT_CODE, A.OPTITEM_CODE, A.INSPAY_TYPE " +
					" FROM SYS_FEE A, SYS_ORDERSETDETAIL B " +
					" WHERE A.ORDER_CODE = B.ORDER_CODE and A.ACTIVE_FLG = 'Y' AND B.HIDE_FLG='Y' " +
					" AND B.ORDERSET_CODE = '"+orderSetCode+"'";
//			System.out.println(sql2);
			//����ҽ�������ݣ���ѯMEM_PACKAGE_SECTION_D �������ݣ���ѯSYS_ORDERSETDETAIL
			if("Y".equals(exec)){
				sql = sql1;
				oper = "UPDATE";
			}else if(exec==null || "".equals(exec) || exec.length()==0){
				sql = sql2;
				oper = "ADD";
			}
//			System.out.println("sql===----"+sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			//ORDER_DESC;DOSAGE_QTY;MEDI_UNIT;OWN_PRICE;OWN_AMT
		/*	for(int i=0;i<result.getCount();i++){
				
				result.setData("PACKAGE_PRICE", i, result.getDouble("PACKAGE_PRICE", i)*rate);
				result.setData("OWN_AMT", i, result.getDouble("PACKAGE_PRICE", i)*result.getDouble("DOSAGE_QTY", i));
				 
			}*/
			//System.out.println("result="+result);
			table.setParmValue(result);
		}

	    /**
	     * �������ݿ��������
	     * @return TJDODBTool
	     */
	    public TJDODBTool getDBTool() {
	        return TJDODBTool.getInstance();
	    }

		/**
		 * TABLEֵ�ı��¼�
		 * @param tNode
		 * @return
		 */
	    public boolean onValueChange(TTableNode tNode) {
	    	boolean flg = false;
	        int row = tNode.getRow();
	        int column = tNode.getColumn();
	        
	        String dosageQty = table.getItemString(row, "DOSAGE_QTY");
	        String colName = tNode.getTable().getParmMap(column);

	        double ownAmt = Double.parseDouble(table.getItemString(row, "OWN_AMT"));
	        // �޸������������޸��ܼ�
	        if ("DOSAGE_QTY".equalsIgnoreCase(colName)) {
	            double qty = TypeTool.getDouble(tNode.getValue());
	            double oldQty = TypeTool.getDouble(tNode.getOldValue());
	            //this.messageBox("��ֵ��"+qty+" ��ֵ��"+oldQty);
	            if (qty == oldQty) {
	                return true;
	            }
	            if (qty <= 0) {
	            	this.messageBox("��������С��0��");
	                return true;
	            }
	            //�����ܼ�
	            double packagePrice = Double.parseDouble(table.getItemString(row, "PACKAGE_PRICE"));
	            ownAmt = qty * packagePrice;
	            table.setItem(row, "OWN_AMT", ownAmt);
	            return false;
	        }
	        // �޸��ײͼۣ������޸��ܼ�ORDER_DESC;DOSAGE_QTY;MEDI_UNIT;OWN_PRICE;PACKAGE_PRICE;OWN_AMT
	        if ("PACKAGE_PRICE".equalsIgnoreCase(colName)) {
	            double price = TypeTool.getDouble(tNode.getValue());
	            double oldPrice = TypeTool.getDouble(tNode.getOldValue());
	            double ownPrice = Double.parseDouble(table.getItemString(row, "RETAIL_PRICE"));
	            if (price == oldPrice) {
	                return true;
	            }
	            if (price <= 0.0) {
	            	this.messageBox("�ײͼ۲���С��0��");
	                return true;
	            }
	            if(price > ownPrice) {
	            	this.messageBox("�ײͼ۲��ܴ���ԭ�ۣ�");
	                return true;
	            }
	            //�����ܼ�
	            double qty = Double.parseDouble(table.getItemString(row, "DOSAGE_QTY"));
	            ownAmt = qty * price;
	            table.setItem(row, "OWN_AMT", ownAmt);
	            return false;
	        }
	        
	        
	        return false;
	    }
	    
		/**
		 * ɾ���¼�
		 */
	    public void onDelete() {
	        int row = table.getSelectedRow();
	        if (row < 0) {
	            this.messageBox_("û�����ݿ�ɾ��");
	            return;
	        }
	        TParm parm=table.getParmValue();
	        delParm.addData("ORDER_CODE", parm.getValue("ORDER_CODE", row));
	        delParm.addData("SECTION_CODE", parm.getValue("SECTION_CODE", row));
	        delParm.addData("PACKAGE_CODE", parm.getValue("PACKAGE_CODE", row));
	        delParm.addData("SETMAIN_FLG", parm.getValue("SETMAIN_FLG", row));
	        delParm.addData("ORDERSET_CODE", parm.getValue("ORDERSET_CODE", row));
	        delParm.addData("ORDERSET_GROUP_NO", parm.getValue("ORDERSET_GROUP_NO", row));
	        delParm.addData("HIDE_FLG", parm.getValue("HIDE_FLG", row));
	        
	        table.removeRow(row);
	        //onSave();
	    }
	    
		/**
		 * ����
		 */
	    public void onSave() {
	        table.acceptText();
	        TParm result = new TParm();
	        TParm parm = new TParm();
	        parm = table.getParmValue();
//	        System.out.println("table///////"+parm);
	        //System.out.println("����ҽ��ϸ�����Ϣ��"+parm);
	        //��������ϸ����ܼ۸�
	        TParm allPriceParm = getAllArmPrice(parm);
//	        System.out.println("allPriceParm-----="+allPriceParm);
	        //System.out.println("allPriceParm="+allPriceParm);
	        if("UPDATE".equals(oper)){//�������޸�
	        	//�Ƿ���ɾ������
	        	if(delParm.getCount("ORDER_CODE")>0){
	        		//System.out.println("ɾ������--��"+delParm.getData());
	        		for (int i = 0; i < delParm.getCount("ORDER_CODE"); i++) {
	        			TParm delParm2 = delParm.getRow(i);
						String sql = getDelSql(delParm2);
						result = new TParm(TJDODBTool.getInstance().update(sql));
					}
	        	}
	        	//�޸�����
	        	for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
	        		TParm updateParm = parm.getRow(j);
	        		String sql = getSaveSql(updateParm);
	        		result = new TParm(TJDODBTool.getInstance().update(sql));
				}
	        	//�޸�����۸�����
//	        	String updateMainSql = updateMainPackagePriceSql(parm, allPriceParm);
//	        	System.out.println(" allPriceParm = " + allPriceParm);
	        	String updateMainSql = updateMainPackagePriceSql1(parm, allPriceParm);
	        	result = new TParm(TJDODBTool.getInstance().update(updateMainSql));
	        	if (result.getErrCode() < 0) {
	    			return ;
	    		}
	        	//ͳ��ʱ���ײ�����
	        	String sumSectionSql = getAllFeeD(parm);
	        	result = new TParm(TJDODBTool.getInstance().select(sumSectionSql));
	        	//�޸�ʱ���ײ�����
	        	String updateSectionSql = updateSectionPrice(parm, result);
	        	result = new TParm(TJDODBTool.getInstance().update(updateSectionSql));
	        	if (result.getErrCode() < 0) {
	        		this.messageBox("����ʧ�ܣ�");
	    			return ;
	    		}else{
	    			this.messageBox("����ɹ���");
	    			oper = "SAVE";
	    			result.setData("OPER", oper);
	    			this.setReturnValue(result);
	    			this.closeWindow();
	    		}
	        }
	        
	        
//	        if (!packD.isModified()) {
//	            return;
//	        }
//	        String[] sql = packD.getUpdateSQL();
//	        if (sql == null) {
//	            return;
//	        }
//	        if (sql.length < 1) {
//	            return;
//	        }
//	        TParm inParm = new TParm();
//	        Map inMap = new HashMap();
//	        inMap.put("SQL", sql);
//	        inParm.setData("IN_MAP", inMap);
//	        TParm result =
//	                TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);// �����ײ�ϸ��
//	        if (result.getErrCode() != 0) {
//	            this.messageBox("E0001");
//	            this.closeWindow();
//	        }
//	        String updatePackageTotAmtSql =
//	                "UPDATE HRM_PACKAGEM B SET B.TC_TOT_AMT = ROUND((SELECT SUM(A.DISPENSE_QTY*A.ORIGINAL_PRICE) "
//	                        + " FROM HRM_PACKAGED A WHERE A.PACKAGE_CODE = B.PACKAGE_CODE GROUP BY A.PACKAGE_CODE),2) ";// ԭ���ܶ�
//	        String updatePackageArAmtSql =
//	                "UPDATE HRM_PACKAGEM A SET A.TC_AR_AMT = ROUND((SELECT SUM(B.DISPENSE_QTY*B.PACKAGE_PRICE) "
//	                        + " FROM HRM_PACKAGED B WHERE A.PACKAGE_CODE = B.PACKAGE_CODE),2) ";// �ײͼ��ܶ�
//	        inParm = new TParm();
//	        inMap = new HashMap();
//	        inMap.put("SQL", new String[]{updatePackageTotAmtSql, updatePackageArAmtSql });
//	        inParm.setData("IN_MAP", inMap);
//	        result =
//	                TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);// �����ײͽ��
//	        if (result.getErrCode() != 0) {
//	            this.messageBox("�����ײ��ܼ�ʧ��");
//	            this.closeWindow();
//	        }
//	        this.setReturnValue(result);
//	        this.closeWindow();
//	        this.messageBox("P0001");
//	        packD.resetModify();
//	        table.setDSValue();
	    }
	    
	    /**
	     * ���ϸ���ܼ۸�
	     */
	    public TParm getAllArmPrice(TParm parm) {
	    	TParm result = new TParm();
	    	int count = parm.getCount("ORDER_CODE");
	    	double allRetailPrice = 0.00;
	    	double allUnitPrice = 0.00;
	    	for (int i = 0; i < count; i++) {
				double ownPrice = parm.getDouble("OWN_PRICE", i);
				double qty = parm.getDouble("DOSAGE_QTY", i);
				double unitPrice = qty * ownPrice;
				double ownAmt = parm.getDouble("PACKAGE_PRICE", i);
				
				allRetailPrice += ownAmt;
				allUnitPrice += unitPrice;
			}
	    	result.setData("RETAIL_PRICE", allRetailPrice);
	    	result.setData("UNIT_PRICE", allUnitPrice);
	    	return result;
	    }
	    
	    /**
	     * ϸ���޸�sql
	     */
	    public String getSaveSql(TParm parm) {
	    	String 
//	    	sql = "UPDATE MEM_PACKAGE_SECTION_D SET " +
//	    			" ORDER_NUM = '"+parm.getValue("DOSAGE_QTY")+"'," +
//	    			" RETAIL_PRICE = '"+parm.getValue("PACKAGE_PRICE")+"'," +
//	    			" OPT_DATE = SYSDATE," +
//	    			" OPT_USER = '"+Operator.getID()+"'," +
//	    			" OPT_TERM = '"+Operator.getIP()+"'" +
//	    			" WHERE SECTION_CODE = '"+parm.getValue("SECTION_CODE")+"' " +
//	    			" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE")+"' " +
//	    			" AND ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"' " +
//	    			" AND ORDERSET_CODE = '"+parm.getValue("ORDERSET_CODE")+"'" +
//	    			" AND ORDERSET_GROUP_NO = '"+parm.getValue("ORDERSET_GROUP_NO")+"'" +
//	    			" AND SETMAIN_FLG = 'N' AND HIDE_FLG = 'Y'";
	    	sql = 
	    		" UPDATE MEM_PACKAGE_SECTION_D_PRICE " +
	    		" SET RETAIL_PRICE = "+parm.getValue("PACKAGE_PRICE")+
	    		" WHERE PRICE_TYPE = '"+priceType+"'" +
	    		" AND ID = '"+parm.getValue("ID")+"'" +
	    		" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE")+"'" +
	    		" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE")+"'";
//	    	System.out.println("�޸�ϸ��sql="+sql);
	    	return sql;
	    }
	    
	    /**
	     * �����޸������ײͼ۸�sql(MEM_PACKAGE_SECTION_D)
	     */
	    public String updateMainPackagePriceSql(TParm parm, TParm priceParm) {
	    	String sql = "UPDATE MEM_PACKAGE_SECTION_D SET " +
				" UNIT_PRICE = '"+priceParm.getValue("UNIT_PRICE")+"'," +
				" RETAIL_PRICE = '"+priceParm.getValue("RETAIL_PRICE")+"'," +
				" OPT_DATE = SYSDATE," +
				" OPT_USER = '"+Operator.getID()+"'," +
				" OPT_TERM = '"+Operator.getIP()+"'" +
				" WHERE SECTION_CODE = '"+parm.getValue("SECTION_CODE", 0)+"' " +
				" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", 0)+"' " +
				" AND ORDER_CODE = '"+parm.getValue("ORDERSET_CODE", 0)+"' " +
				" AND ORDERSET_CODE = '"+parm.getValue("ORDERSET_CODE", 0)+"'" +
				" AND ORDERSET_GROUP_NO = '"+parm.getValue("ORDERSET_GROUP_NO", 0)+"'" +
				" AND SETMAIN_FLG = 'Y' AND HIDE_FLG = 'N'";
	    	//System.out.println("�����޸������ײͼ۸�sql="+sql);
	    	return sql;
	    }
	    
	    /**
	     * �����޸������ײͼ۸�sql(MEM_PACKAGE_SECTION_D_PRICE)
	     */
	    public String updateMainPackagePriceSql1(TParm parm, TParm priceParm) {
//	    	System.out.println("priceParm = "+ priceParm);
	    	String sql = "UPDATE MEM_PACKAGE_SECTION_D_PRICE " +
	    			" SET RETAIL_PRICE = '"+priceParm.getDouble("RETAIL_PRICE")+"', " +
	    			" OPT_DATE = SYSDATE, " +
	    			" OPT_USER = '"+Operator.getID()+"', " +
	    			" OPT_TERM = '"+Operator.getIP()+"' " +
	    			" WHERE SECTION_CODE = '"+parm.getValue("SECTION_CODE", 0)+"' " +
	    			" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", 0)+"' " +
	    			" AND PRICE_TYPE = '"+priceType+"' " +
	    			" AND ID = '"+id+"'";
	    	System.out.println("�����޸������ײͼ۸�sql="+sql);
	    	return sql;
	    }
	    
	    
	    
	    /**
	     * �����޸�ʱ���ײ��ܼ�sql(MEM_PACKAGE_SECTION)
	     */
	    public String updateSectionPrice(TParm parm, TParm result) {
	    	String sql = "UPDATE MEM_PACKAGE_SECTION SET " +
	    			" ORIGINAL_PRICE = '"+result.getValue("ALL_UNIT_PRICE", 0)+"'," +
	    			" SECTION_PRICE = '"+result.getValue("ALL_RETAIL_PRICE", 0)+"'," +
	    			" OPT_DATE = SYSDATE," +
	    			" OPT_USER = '"+Operator.getID()+"'," +
	    			" OPT_TERM = '"+Operator.getIP()+"'" +
	    			" WHERE SECTION_CODE = '"+parm.getValue("SECTION_CODE", 0)+"' " +
	    			" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", 0)+"' ";
	    	//System.out.println("�����޸�ʱ���ײ��ܼ�sql="+sql);
	    	return sql;
	    }
	    /**
	     * ��ȡɾ��ϸ��sql
	     */
	    public String getDelSql(TParm parm) {
	    	String sql = "DELETE FROM MEM_PACKAGE_SECTION_D " +
	    			" WHERE ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"' " +
	    			" AND SECTION_CODE = '"+parm.getValue("SECTION_CODE")+"' " +
	    			" AND PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE")+"' " +
	    			" AND SETMAIN_FLG = 'N' " +
	    			" AND ORDERSET_CODE = '"+parm.getValue("ORDERSET_CODE")+"' " +
	    			" AND ORDERSET_GROUP_NO = '"+parm.getValue("ORDERSET_GROUP_NO")+"' " +
	    			" AND HIDE_FLG = 'Y' ";
	    	//System.out.println("ɾ��sql:"+sql);
	    	return sql;
	    }
	    
	    /**
		 * ͳ��ҽ��ϸ���ײ��ܼ� MEM_PACKAGE_SECTION_D
		 * SUM(UNIT_PRICE), SUM(RETAIL_PRICE)
		 */
		private String getAllFeeD(TParm parm){
			String sectionCode = parm.getValue("SECTION_CODE", 0);
			String packageCode = parm.getValue("PACKAGE_CODE", 0);
			String sql = "SELECT SUM(UNIT_PRICE*ORDER_NUM) AS ALL_UNIT_PRICE, SUM(RETAIL_PRICE*ORDER_NUM) AS ALL_RETAIL_PRICE " +
					" FROM MEM_PACKAGE_SECTION_D WHERE SECTION_CODE = '"+sectionCode+"' AND PACKAGE_CODE = '"+packageCode+"' " +
					" AND HIDE_FLG = 'N' ";
			//System.out.println("ͳ��ҽ��ϸ���ײ��ܼ�sql="+sql);
			return sql;
		}
	    
}
