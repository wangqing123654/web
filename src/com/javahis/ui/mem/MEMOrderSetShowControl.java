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
 * <p>Title: 会员套餐设定修改医嘱细相</p>
 *
 * <p>Description: 会员套餐设定修改医嘱细相</p>
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
	    private HRMPackageD packD;// 套餐细项
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
	        //初始化控件
			initComponent();
	        initParam();
	    }
	    
	    /**
		 * 初始化控件
		 */
	    private void initComponent() {
	    	table=(TTable)this.getComponent("TABLE");
			table.addEventListener("TABLE->"+ TTableEvent.CHANGE_VALUE,this, "onValueChange");
	    	
	    }
	    
		/**
		 * 初始化参数
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
			//老数据
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
			//新数据
			String sql2 = "SELECT A.ORDER_CODE,B.ORDERSET_CODE,A.ORDER_DESC, A.SPECIFICATION, B.DOSAGE_QTY," +
					" A.UNIT_CODE AS MEDI_UNIT, A.OWN_PRICE,A.OWN_PRICE AS PACKAGE_PRICE, A.OWN_PRICE * B.DOSAGE_QTY " +
					" AS OWN_AMT, A.EXEC_DEPT_CODE, A.OPTITEM_CODE, A.INSPAY_TYPE " +
					" FROM SYS_FEE A, SYS_ORDERSETDETAIL B " +
					" WHERE A.ORDER_CODE = B.ORDER_CODE and A.ACTIVE_FLG = 'Y' AND B.HIDE_FLG='Y' " +
					" AND B.ORDERSET_CODE = '"+orderSetCode+"'";
//			System.out.println(sql2);
			//集合医嘱老数据：查询MEM_PACKAGE_SECTION_D 新增数据：查询SYS_ORDERSETDETAIL
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
	     * 返回数据库操作工具
	     * @return TJDODBTool
	     */
	    public TJDODBTool getDBTool() {
	        return TJDODBTool.getInstance();
	    }

		/**
		 * TABLE值改变事件
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
	        // 修改数量，并且修改总价
	        if ("DOSAGE_QTY".equalsIgnoreCase(colName)) {
	            double qty = TypeTool.getDouble(tNode.getValue());
	            double oldQty = TypeTool.getDouble(tNode.getOldValue());
	            //this.messageBox("新值："+qty+" 老值："+oldQty);
	            if (qty == oldQty) {
	                return true;
	            }
	            if (qty <= 0) {
	            	this.messageBox("数量不能小于0！");
	                return true;
	            }
	            //计算总价
	            double packagePrice = Double.parseDouble(table.getItemString(row, "PACKAGE_PRICE"));
	            ownAmt = qty * packagePrice;
	            table.setItem(row, "OWN_AMT", ownAmt);
	            return false;
	        }
	        // 修改套餐价，并且修改总价ORDER_DESC;DOSAGE_QTY;MEDI_UNIT;OWN_PRICE;PACKAGE_PRICE;OWN_AMT
	        if ("PACKAGE_PRICE".equalsIgnoreCase(colName)) {
	            double price = TypeTool.getDouble(tNode.getValue());
	            double oldPrice = TypeTool.getDouble(tNode.getOldValue());
	            double ownPrice = Double.parseDouble(table.getItemString(row, "RETAIL_PRICE"));
	            if (price == oldPrice) {
	                return true;
	            }
	            if (price <= 0.0) {
	            	this.messageBox("套餐价不能小于0！");
	                return true;
	            }
	            if(price > ownPrice) {
	            	this.messageBox("套餐价不能大于原价！");
	                return true;
	            }
	            //计算总价
	            double qty = Double.parseDouble(table.getItemString(row, "DOSAGE_QTY"));
	            ownAmt = qty * price;
	            table.setItem(row, "OWN_AMT", ownAmt);
	            return false;
	        }
	        
	        
	        return false;
	    }
	    
		/**
		 * 删除事件
		 */
	    public void onDelete() {
	        int row = table.getSelectedRow();
	        if (row < 0) {
	            this.messageBox_("没有数据可删除");
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
		 * 保存
		 */
	    public void onSave() {
	        table.acceptText();
	        TParm result = new TParm();
	        TParm parm = new TParm();
	        parm = table.getParmValue();
//	        System.out.println("table///////"+parm);
	        //System.out.println("集合医嘱细项保存信息："+parm);
	        //计算所有细项的总价格
	        TParm allPriceParm = getAllArmPrice(parm);
//	        System.out.println("allPriceParm-----="+allPriceParm);
	        //System.out.println("allPriceParm="+allPriceParm);
	        if("UPDATE".equals(oper)){//老数据修改
	        	//是否有删除数据
	        	if(delParm.getCount("ORDER_CODE")>0){
	        		//System.out.println("删除数据--："+delParm.getData());
	        		for (int i = 0; i < delParm.getCount("ORDER_CODE"); i++) {
	        			TParm delParm2 = delParm.getRow(i);
						String sql = getDelSql(delParm2);
						result = new TParm(TJDODBTool.getInstance().update(sql));
					}
	        	}
	        	//修改数据
	        	for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
	        		TParm updateParm = parm.getRow(j);
	        		String sql = getSaveSql(updateParm);
	        		result = new TParm(TJDODBTool.getInstance().update(sql));
				}
	        	//修改主项价格数据
//	        	String updateMainSql = updateMainPackagePriceSql(parm, allPriceParm);
//	        	System.out.println(" allPriceParm = " + allPriceParm);
	        	String updateMainSql = updateMainPackagePriceSql1(parm, allPriceParm);
	        	result = new TParm(TJDODBTool.getInstance().update(updateMainSql));
	        	if (result.getErrCode() < 0) {
	    			return ;
	    		}
	        	//统计时程套餐数据
	        	String sumSectionSql = getAllFeeD(parm);
	        	result = new TParm(TJDODBTool.getInstance().select(sumSectionSql));
	        	//修改时程套餐数据
	        	String updateSectionSql = updateSectionPrice(parm, result);
	        	result = new TParm(TJDODBTool.getInstance().update(updateSectionSql));
	        	if (result.getErrCode() < 0) {
	        		this.messageBox("保存失败！");
	    			return ;
	    		}else{
	    			this.messageBox("保存成功！");
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
//	                TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);// 更新套餐细项
//	        if (result.getErrCode() != 0) {
//	            this.messageBox("E0001");
//	            this.closeWindow();
//	        }
//	        String updatePackageTotAmtSql =
//	                "UPDATE HRM_PACKAGEM B SET B.TC_TOT_AMT = ROUND((SELECT SUM(A.DISPENSE_QTY*A.ORIGINAL_PRICE) "
//	                        + " FROM HRM_PACKAGED A WHERE A.PACKAGE_CODE = B.PACKAGE_CODE GROUP BY A.PACKAGE_CODE),2) ";// 原价总额
//	        String updatePackageArAmtSql =
//	                "UPDATE HRM_PACKAGEM A SET A.TC_AR_AMT = ROUND((SELECT SUM(B.DISPENSE_QTY*B.PACKAGE_PRICE) "
//	                        + " FROM HRM_PACKAGED B WHERE A.PACKAGE_CODE = B.PACKAGE_CODE),2) ";// 套餐价总额
//	        inParm = new TParm();
//	        inMap = new HashMap();
//	        inMap.put("SQL", new String[]{updatePackageTotAmtSql, updatePackageArAmtSql });
//	        inParm.setData("IN_MAP", inMap);
//	        result =
//	                TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);// 更新套餐金额
//	        if (result.getErrCode() != 0) {
//	            this.messageBox("更新套餐总价失败");
//	            this.closeWindow();
//	        }
//	        this.setReturnValue(result);
//	        this.closeWindow();
//	        this.messageBox("P0001");
//	        packD.resetModify();
//	        table.setDSValue();
	    }
	    
	    /**
	     * 获得细项总价格
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
	     * 细项修改sql
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
//	    	System.out.println("修改细项sql="+sql);
	    	return sql;
	    }
	    
	    /**
	     * 保存修改主项套餐价格sql(MEM_PACKAGE_SECTION_D)
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
	    	//System.out.println("保存修改主项套餐价格sql="+sql);
	    	return sql;
	    }
	    
	    /**
	     * 保存修改主项套餐价格sql(MEM_PACKAGE_SECTION_D_PRICE)
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
	    	System.out.println("保存修改主项套餐价格sql="+sql);
	    	return sql;
	    }
	    
	    
	    
	    /**
	     * 保存修改时程套餐总价sql(MEM_PACKAGE_SECTION)
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
	    	//System.out.println("保存修改时程套餐总价sql="+sql);
	    	return sql;
	    }
	    /**
	     * 获取删除细项sql
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
	    	//System.out.println("删除sql:"+sql);
	    	return sql;
	    }
	    
	    /**
		 * 统计医嘱细表套餐总价 MEM_PACKAGE_SECTION_D
		 * SUM(UNIT_PRICE), SUM(RETAIL_PRICE)
		 */
		private String getAllFeeD(TParm parm){
			String sectionCode = parm.getValue("SECTION_CODE", 0);
			String packageCode = parm.getValue("PACKAGE_CODE", 0);
			String sql = "SELECT SUM(UNIT_PRICE*ORDER_NUM) AS ALL_UNIT_PRICE, SUM(RETAIL_PRICE*ORDER_NUM) AS ALL_RETAIL_PRICE " +
					" FROM MEM_PACKAGE_SECTION_D WHERE SECTION_CODE = '"+sectionCode+"' AND PACKAGE_CODE = '"+packageCode+"' " +
					" AND HIDE_FLG = 'N' ";
			//System.out.println("统计医嘱细表套餐总价sql="+sql);
			return sql;
		}
	    
}
