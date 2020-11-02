package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
*
* <p>Title: 套餐结转明细表</p>
*
* <p>Description: 套餐结转明细表</p>
*
* <p>Copyright: Copyright (c) 2014</p>
*
* <p>Company: JavaHis</p>
*
* @author yanj 20140424
* @version 4.5
*/
public class MEMPackAccountDetial extends TControl{
	private TTable packTable;
	 /**
     * 初始化
     */
    public void onInit() { // 初始化程序
        super.onInit();
        initComponent();
        initData();
        
    }
    /**
	 * 初始化控件
	 */
    private void initComponent() {
    	packTable = getTable("TABLE");
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
     * 初始化数据   
     */
    private void initData(){
    	//查询时间默认当天
    	Timestamp now = StringTool.getTimestamp(new Date());
   	 	this.setValue("S_DATE",
		 		now.toString().substring(0, 10).replace('-', '/')+" "+"00:00:00");
   	 	this.setValue("E_DATE",
		 		now.toString().substring(0, 10).replace('-', '/')+" "+"23:59:59");
//   	 	//开始时间为上个月-默认
//   	 	Calendar cd = Calendar.getInstance();
//   	 	cd.add(Calendar.MONTH, -1);
//   	 	String format = formateDate.format(cd.getTime());
//		this.setValue("S_DATE", formateDate.format(cd.getTime()));
   	 	this.setValue("DEPT_CODE", Operator.getDept());
   	 	this.setValue("PACKAGE_KIND", "");
		
    }
    /**
     * 查询
     */
    public void onQuery() {
    	String sDate = this.getValueString("S_DATE").substring(0, 19).replace("-", "").replace("/", "").replace(" ", "").replace(":", "");
    	String eDate = this.getValueString("E_DATE").substring(0, 19).replace("-", "").replace("/", "").replace(" ", "").replace(":", "");
    	String packageKind = "";
    	if(!"".equals(this.getValueString("PACKAGE_KIND"))&&!this.getValueString("PACKAGE_KIND").equals(null)){
    		packageKind = " AND D.PACKAGE_CODE = '"+this.getValueString("PACKAGE_KIND")+"'";
    	}
    	String orderSql = "SELECT C.BILL_DATE,A.MR_NO,F.PAT_NAME,H.PACKAGE_DESC AS PACKAGE_DESC,E.SECTION_DESC,A.SETMAIN_FLG, A.DISCOUNT_RATE, " +
    			"A.ORDER_DESC,A.DISPENSE_QTY,G.UNIT_CHN_DESC AS DISPENSE_UNIT ,A.OWN_PRICE,A.OWN_AMT,A.AR_AMT,A.ORDER_CODE,A.ORDERSET_GROUP_NO,A.RX_NO  FROM OPD_ORDER A," +
    			"BIL_OPB_RECP C,MEM_PAT_PACKAGE_SECTION_D D,MEM_PACKAGE_SECTION E,SYS_PATINFO F,SYS_UNIT G,MEM_PACKAGE H" +
    			" WHERE A.MR_NO = F.MR_NO  AND A.RECEIPT_NO = C.RECEIPT_NO AND E.PACKAGE_CODE = H.PACKAGE_CODE  " +
    			" AND A.DISPENSE_UNIT = G.UNIT_CODE(+) AND A.MEM_PACKAGE_ID = D.ID AND D.PACKAGE_CODE = E.PACKAGE_CODE AND D.SECTION_CODE = E.SECTION_CODE AND" +
    			" A.MEM_PACKAGE_ID IS NOT NULL   " +
    			packageKind+
    			" AND (A.SETMAIN_FLG = 'Y' OR A.ORDERSET_CODE IS NULL) AND C.BILL_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDhh24miss') AND TO_DATE('"+eDate+"','YYYYMMDDhh24miss')";
//    	System.out.println("========orderSql orderSql orderSql is ::"+orderSql);
    	TParm sqlParm = new TParm(TJDODBTool.getInstance().select(orderSql));
//    	System.out.println("++++sqlParm sqlParm sqlParm is ::"+sqlParm);
//    	System.out.println("--------sqlParm.getCount() sqlParm.getCount() is ::"+sqlParm.getCount("MR_NO"));
    	if(sqlParm.getCount("MR_NO")>0){
    		for(int i = 0;i < sqlParm.getCount();i++){ 
        		if(sqlParm.getValue("SETMAIN_FLG", i).equals("Y")){//集合医嘱主项
        			double arAmt = this.getArAmtOrderSetFlg("O", sqlParm.getValue("ORDER_CODE", i), sqlParm.getValue("RX_NO", i), sqlParm.getValue("ORDERSET_GROUP_NO", i));
                	double ownAmt = this.getOwnAmtOrderSetFlg("O", sqlParm.getValue("ORDER_CODE", i), sqlParm.getValue("RX_NO", i), sqlParm.getValue("ORDERSET_GROUP_NO", i));
                	double ownPrice = this.getOwnPriceOrderSetFlg("O", sqlParm.getValue("ORDER_CODE", i), sqlParm.getValue("RX_NO", i), sqlParm.getValue("ORDERSET_GROUP_NO", i));
        			sqlParm.setData("OWN_PRICE", i, ownPrice);
        			sqlParm.setData("AR_AMT", i, arAmt);
        			sqlParm.setData("OWN_AMT", i, ownAmt);
        	}
        	packTable.setParmValue(sqlParm);
        	}
    	}else{
    		this.messageBox("没有要查询的数据。");
    		packTable.setParmValue(sqlParm);
    		return;
    	}
    	
    }
    /**
     * 拿到集合医嘱价格
     * @param orderCode String
     * @return long
     */
//    public double getArAmtOrderSetFlg(String admType,String orderCode,Timestamp date,String orderNo,String orderGroupNo){
    	public double getArAmtOrderSetFlg(String admType,String orderCode,String orderNo,String orderGroupNo){
        double arAmt = 0.0;
//        String dateStr = StringTool.getString(date,"yyyyMMddHHmmss");
        if("O".equals(admType)||"E".equals(admType)){
            TParm opdParm = new TParm(this.getDBTool().select("SELECT AR_AMT FROM OPD_ORDER WHERE ORDERSET_CODE='"+orderCode+"' " +
            		"AND RX_NO='"+orderNo+"' AND ORDERSET_GROUP_NO='"+orderGroupNo+"' AND SETMAIN_FLG='N'"));
            int rowCount = opdParm.getCount();
            for(int i=0;i<rowCount;i++){
                arAmt+=opdParm.getDouble("AR_AMT",i);
            }
        }
//        if("I".equals(admType)){
//            TParm odiParm = new TParm(this.getDBTool().select("SELECT B.OWN_PRICE FROM ODI_ORDER A,SYS_FEE_HISTORY B WHERE A.ORDERSET_CODE='T0503006' AND ORDERSET_GROUP_NO='6' AND ORDER_NO='100427000002' AND SETMAIN_FLG='N' AND A.ORDER_CODE=B.ORDER_CODE AND TO_DATE('"+dateStr+"','YYYYMMDDHH24MISS') BETWEEN TO_DATE(B.START_DATE,'YYYYMMDDHH24MISS') AND TO_DATE(B.END_DATE,'YYYYMMDDHH24MISS')"));
//            int rowCount = odiParm.getCount();
//            for(int i=0;i<rowCount;i++){
//                arAmt+=odiParm.getDouble("OWN_PRICE",i);
//            }
//        }
        // System.out.println("ARAMT============"+arAmt);
        return arAmt;
    }
    	 /**
         * 拿到集合医嘱价格
         * @param orderCode String
         * @return long
         */
        	public double getOwnAmtOrderSetFlg(String admType,String orderCode,String orderNo,String orderGroupNo){
            double ownAmt = 0.0;
            if("O".equals(admType)||"E".equals(admType)){
                TParm opdParm = new TParm(this.getDBTool().select("SELECT OWN_AMT FROM OPD_ORDER WHERE ORDERSET_CODE='"+orderCode+"' " +
                		"AND RX_NO='"+orderNo+"' AND ORDERSET_GROUP_NO='"+orderGroupNo+"' AND SETMAIN_FLG='N'"));
                int rowCount = opdParm.getCount();
                for(int i=0;i<rowCount;i++){
                	ownAmt+=opdParm.getDouble("OWN_AMT",i);
                }
            }
            return ownAmt;
        }
        	 /**
             * 拿到集合医嘱价格
             * @param orderCode String
             * @return long
             */
            	public double getOwnPriceOrderSetFlg(String admType,String orderCode,String orderNo,String orderGroupNo){
                double ownAmt = 0.0;
                if("O".equals(admType)||"E".equals(admType)){
                    TParm opdParm = new TParm(this.getDBTool().select("SELECT OWN_PRICE FROM OPD_ORDER WHERE ORDERSET_CODE='"+orderCode+"' " +
                    		"AND RX_NO='"+orderNo+"' AND ORDERSET_GROUP_NO='"+orderGroupNo+"' AND SETMAIN_FLG='N'"));
                    int rowCount = opdParm.getCount();
                    for(int i=0;i<rowCount;i++){
                    	ownAmt+=opdParm.getDouble("OWN_PRICE",i);
                    }
                }
                return ownAmt;
            }
         
      /**
       * 打印方法
       */
       public void onPrint(){   
    	   //得到表格值
    	   packTable = this.getTable("TABLE");
    	   TParm packParm = new TParm();
    	   packParm = packTable.getParmValue();
    	   TParm data = new TParm();
    	   if(packParm.getCount() <= 0){
    		   this.messageBox("没有需要 打印的数据");
    		   return;
    	   }else{//整理打印数据
    		   DecimalFormat df = new DecimalFormat("0.00");
    		   //遍历表格数据
    		   TParm tableDate = new TParm();
    		   for(int i = 0;i<packParm.getCount();i++){
    			   tableDate.addData("BILL_DATE",packParm.getValue("BILL_DATE", i).substring(0, packParm.getValue("BILL_DATE", i).length()-2));
    			   tableDate.addData("MR_NO",packParm.getValue("MR_NO", i));
    			   tableDate.addData("PAT_NAME",packParm.getValue("PAT_NAME", i));
    			   tableDate.addData("PACKAGE_DESC",packParm.getValue("PACKAGE_DESC", i));
    			   tableDate.addData("SECTION_DESC",packParm.getValue("SECTION_DESC", i));
    			   tableDate.addData("ORDER_DESC",packParm.getValue("ORDER_DESC", i));
    			   tableDate.addData("DISPENSE_QTY",packParm.getInt("DISPENSE_QTY", i));
    			   tableDate.addData("DISPENSE_UNIT",packParm.getValue("DISPENSE_UNIT", i));
    			   tableDate.addData("OWN_PRICE",df.format(packParm.getDouble("OWN_PRICE", i)));
    			   tableDate.addData("OWN_AMT",df.format(packParm.getDouble("OWN_AMT", i)));
    			   tableDate.addData("AR_AMT",df.format(packParm.getDouble("AR_AMT", i)));
    			   
    			   
    		   }
    		   
    		   //总行数的设定
    		   tableDate.setCount(tableDate.getCount("MR_NO"));
    		   tableDate.addData("SYSTEM", "COLUMNS", "BILL_DATE");
    		   tableDate.addData("SYSTEM","COLUMNS", "MR_NO");
    		   tableDate.addData("SYSTEM","COLUMNS", "PAT_NAME");
    		   tableDate.addData("SYSTEM","COLUMNS", "PACKAGE_DESC");
    		   tableDate.addData("SYSTEM","COLUMNS", "PACKAGE_DESC");
    		   tableDate.addData("SYSTEM","COLUMNS", "ORDER_DESC");
    		   tableDate.addData("SYSTEM","COLUMNS", "DISPENSE_QTY");
    		   tableDate.addData("SYSTEM","COLUMNS", "DISPENSE_UNIT");
    		   tableDate.addData("SYSTEM","COLUMNS", "OWN_PRICE");
    		   tableDate.addData("SYSTEM","COLUMNS", "OWN_AMT");
    		   tableDate.addData("SYSTEM","COLUMNS", "AR_AMT");
    		  //将表格放到容器中
    		   data.setData("TABLE",tableDate.getData());
    		 //表头数据
    		   data.setData("TITLE","TEXT","爱育华套餐结转明细表");
    		   data.setData("S_DATE","TEXT",this.getValue("S_DATE").toString().substring(0, 19));
    		   data.setData("E_DATE","TEXT",this.getValue("E_DATE").toString().substring(0, 19));
    		   //表尾数据
    		   data.setData("TEST","TEXT",Operator.getName());
    		   this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMPackAccountDaily.jhw",data);
    		   
    	   }
       }   	
     /**
      * 清空方法
      */
       public void onClear(){
    	   packTable.removeRowAll();
    	   initData();
//    	 //查询时间默认当天
//       	Timestamp now = StringTool.getTimestamp(new Date());
//      	 	this.setValue("S_DATE",
//   		 		now.toString().substring(0, 19).replace('-', '/')+" "+"00:00:00");
//      	 	this.setValue("E_DATE",
//   		 		now.toString().substring(0, 19).replace('-', '/')+" "+"23:59:59");
////      	 	//开始时间为上个月-默认
////      	 	Calendar cd = Calendar.getInstance();
////      	 	cd.add(Calendar.MONTH, -1);
////      	 	String format = formateDate.format(cd.getTime());
////   		this.setValue("S_DATE", formateDate.format(cd.getTime()));
//      	 	this.setValue("DEPT_CODE", Operator.getDept());
//      	 	this.setValue("PACKAGE_KIND", "");
    	   
            	}
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    
    /**
     * 导出Excel表格
     */
    public void onExport() {
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        if (table.getRowCount() <= 0) {
            messageBox("无导出资料");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "爱育华套餐结转明细表");
    }
    
}
