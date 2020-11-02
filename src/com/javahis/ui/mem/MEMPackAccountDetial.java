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
* <p>Title: �ײͽ�ת��ϸ��</p>
*
* <p>Description: �ײͽ�ת��ϸ��</p>
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
     * ��ʼ��
     */
    public void onInit() { // ��ʼ������
        super.onInit();
        initComponent();
        initData();
        
    }
    /**
	 * ��ʼ���ؼ�
	 */
    private void initComponent() {
    	packTable = getTable("TABLE");
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
     * ��ʼ������   
     */
    private void initData(){
    	//��ѯʱ��Ĭ�ϵ���
    	Timestamp now = StringTool.getTimestamp(new Date());
   	 	this.setValue("S_DATE",
		 		now.toString().substring(0, 10).replace('-', '/')+" "+"00:00:00");
   	 	this.setValue("E_DATE",
		 		now.toString().substring(0, 10).replace('-', '/')+" "+"23:59:59");
//   	 	//��ʼʱ��Ϊ�ϸ���-Ĭ��
//   	 	Calendar cd = Calendar.getInstance();
//   	 	cd.add(Calendar.MONTH, -1);
//   	 	String format = formateDate.format(cd.getTime());
//		this.setValue("S_DATE", formateDate.format(cd.getTime()));
   	 	this.setValue("DEPT_CODE", Operator.getDept());
   	 	this.setValue("PACKAGE_KIND", "");
		
    }
    /**
     * ��ѯ
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
        		if(sqlParm.getValue("SETMAIN_FLG", i).equals("Y")){//����ҽ������
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
    		this.messageBox("û��Ҫ��ѯ�����ݡ�");
    		packTable.setParmValue(sqlParm);
    		return;
    	}
    	
    }
    /**
     * �õ�����ҽ���۸�
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
         * �õ�����ҽ���۸�
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
             * �õ�����ҽ���۸�
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
       * ��ӡ����
       */
       public void onPrint(){   
    	   //�õ����ֵ
    	   packTable = this.getTable("TABLE");
    	   TParm packParm = new TParm();
    	   packParm = packTable.getParmValue();
    	   TParm data = new TParm();
    	   if(packParm.getCount() <= 0){
    		   this.messageBox("û����Ҫ ��ӡ������");
    		   return;
    	   }else{//�����ӡ����
    		   DecimalFormat df = new DecimalFormat("0.00");
    		   //�����������
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
    		   
    		   //���������趨
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
    		  //�����ŵ�������
    		   data.setData("TABLE",tableDate.getData());
    		 //��ͷ����
    		   data.setData("TITLE","TEXT","�������ײͽ�ת��ϸ��");
    		   data.setData("S_DATE","TEXT",this.getValue("S_DATE").toString().substring(0, 19));
    		   data.setData("E_DATE","TEXT",this.getValue("E_DATE").toString().substring(0, 19));
    		   //��β����
    		   data.setData("TEST","TEXT",Operator.getName());
    		   this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMPackAccountDaily.jhw",data);
    		   
    	   }
       }   	
     /**
      * ��շ���
      */
       public void onClear(){
    	   packTable.removeRowAll();
    	   initData();
//    	 //��ѯʱ��Ĭ�ϵ���
//       	Timestamp now = StringTool.getTimestamp(new Date());
//      	 	this.setValue("S_DATE",
//   		 		now.toString().substring(0, 19).replace('-', '/')+" "+"00:00:00");
//      	 	this.setValue("E_DATE",
//   		 		now.toString().substring(0, 19).replace('-', '/')+" "+"23:59:59");
////      	 	//��ʼʱ��Ϊ�ϸ���-Ĭ��
////      	 	Calendar cd = Calendar.getInstance();
////      	 	cd.add(Calendar.MONTH, -1);
////      	 	String format = formateDate.format(cd.getTime());
////   		this.setValue("S_DATE", formateDate.format(cd.getTime()));
//      	 	this.setValue("DEPT_CODE", Operator.getDept());
//      	 	this.setValue("PACKAGE_KIND", "");
    	   
            	}
    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    
    /**
     * ����Excel���
     */
    public void onExport() {
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        if (table.getRowCount() <= 0) {
            messageBox("�޵�������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "�������ײͽ�ת��ϸ��");
    }
    
}
