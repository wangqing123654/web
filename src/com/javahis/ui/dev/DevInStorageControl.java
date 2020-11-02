package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

import jdo.dev.DevDepTool;
import jdo.dev.DevInStorageTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import java.awt.Component;
import com.dongyang.jdo.TJDODBTool;
import jdo.dev.DevTypeTool;
import com.javahis.system.textFormat.TextFormatDEVOrg;
import com.javahis.util.RFIDPrintUtils;

/**
 * <p>Title: 设备入库</p>
 *
 * <p>Description: 设备入库</p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company:javahis </p>
 *
 * @author  fux
 * @version 1.0
 */
public class DevInStorageControl extends TControl {
	//入库日期,100;入库单号,100;入库科室,100,DEPT_COMBO;验收单号,100;验收日期,100;入库人员,100,OPERATOR_COMBO
	//INWAREHOUSE_DATE;INWAREHOUSE_NO;INWAREHOUSE_DEPT;VERIFY_NO;RECEIPT_DATE;INWAREHOUSE_USER
	
    //设备入库明细缓冲区
    TParm parmD = new TParm();
    //设备明细序号管理缓冲区
    TParm parmDD = new TParm();
    //验收细表parm
    TParm ReceiptD = new TParm();  
              
    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit(); 
        //设备明细表格编辑事件
        addEventListener("DEV_INWAREHOUSED->" + TTableEvent.CHANGE_VALUE,"onTableValueChange");
        //设备录入事件
        getTTable("DEV_INWAREHOUSED").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,"onCreateEditComoponent");
        //设备录事件
        getTTable("DEV_INWAREHOUSED").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,"onTableComponent");
        //初始化换面启动时控件默认值
        initComponent(); 
        //初始化设备入库明细表格
        initTableD();
        //初始化设备入库明细序号管理表格
        initTableDD();
        //加入空行
        addDRow();
        //锁住表格不可编辑栏位
        setTableLock();
        //设置权限
        onInitOperaotrDept();
    }

    /** 
     * 设置权限
     */
    private void onInitOperaotrDept(){
        // 显示全院药库部门
        if (getPopedem("deptAll")) 
            return;   
//        ((TextFormatDEVOrg)getComponent("INWAREHOUSE_DEPT")).setOperatorId(Operator.getID());
//        ((TextFormatDEVOrg)getComponent("DEPT")).setOperatorId(Operator.getID());
    }

    /**
     * 锁住表格不可编辑栏位
     */
    public void setTableLock(){
//        ((TTable)getComponent("DEV_INWAREHOUSED")).setLockColumns("3,4,5,6,8,"+
//                                                                  "9,10,12,16,17,18,"+
//                                                                  "19,20,21,22,"+
//                                                                  "24,25,26,27,28");
//        ((TTable)getComponent("DEV_INWAREHOUSEDD")).setLockColumns("0,1,2,3,4,5,11,"+
//                                                                   "12,13,14,15");
    }
    /**
     * 初始化界面控件默认值
     */
    public void initComponent(){
        Timestamp timestamp = SystemTool.getInstance().getDate();
        setValue("INWARE_START_DATE",timestamp);
        setValue("INWARE_END_DATE",timestamp);
        setValue("INWAREHOUSE_DEPT",Operator.getDept()); 
        setValue("INWAREHOUSE_DATE",timestamp);
        setValue("DEPT",Operator.getDept());
        setValue("OPERATOR",Operator.getID());
    }
    //RECEIPT
    //入库日期,100;入库单号,100;入库科室,100,DEPT_COMBO;验收单号,100;验收日期,100;入库人员,100,OPERATOR_COMBO
    //INWAREHOUSE_DATE;INWAREHOUSE_NO;INWAREHOUSE_DEPT;VERIFY_NO;RECEIPT_DATE;INWAREHOUSE_USER
    /**
     * 初始化设备入库明细表格
     */ 
    public void initTableD(){
        String column = "DEL_FLG;DEVPRO_CODE;DEV_CODE;BATCH_SEQ;DEV_CHN_DESC;"+
                        "SPECIFICATION;MAN_CODE;QTY;SUM_QTY;RECEIPT_QTY;"+
                        "UNIT_CODE;UNIT_PRICE;TOT_VALUE;MAN_DATE;LAST_PRICE;"+
                        "GUAREP_DATE;DEPR_METHOD;USE_DEADLINE;DEP_DATE;"+
                        "MAN_NATION;SEQMAN_FLG;MEASURE_FLG;BENEFIT_FLG;"+
                        "FILES_WAY;VERIFY_NO;VERIFY_NO_SEQ;INWAREHOUSE_NO;SEQ_NO;DEVKIND_CODE";
        String stringMap[] = StringTool.parseLine(column,";");
        TParm tableDParm = new TParm(); 
        for(int i = 0;i<stringMap.length;i++){
            tableDParm.addData(stringMap[i],"");
        }
        ((TTable)getComponent("DEV_INWAREHOUSED")).setParmValue(tableDParm);
        ((TTable)getComponent("DEV_INWAREHOUSED")).removeRow(0);
    }
    /**
     * 初始化设备入库明细序号管理表格
     */
    public void initTableDD(){
    	//DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;BATCH_SEQ;DEV_CODE;DDSEQ_NO;DEV_CHN_DESC;BARCODE;MAIN_DEV;MAN_DATE;MAN_SEQ;
    	//LAST_PRICE;GUAREP_DATE;DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;DEVSEQ_NO
        String column = "DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;BATCH_SEQ;DEV_CODE;DDSEQ_NO;DEV_CHN_DESC;BARCODE;"+
                        "MAIN_DEV;MAN_DATE;MAN_SEQ;LAST_PRICE;GUAREP_DATE;DEP_DATE;"+ 
                        "TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;DEVSEQ_NO";
        String stringMap[] = StringTool.parseLine(column,";");
        TParm tableDParm = new TParm();
        for(int i = 0;i<stringMap.length;i++){
            tableDParm.addData(stringMap[i],"");
        }
        ((TTable)getComponent("DEV_INWAREHOUSEDD")).setParmValue(tableDParm);
        ((TTable)getComponent("DEV_INWAREHOUSEDD")).removeRow(0);
    }
    /**
     * 加入空行
     */
    public void addDRow(){
        String column = "DEL_FLG;DEVPRO_CODE;DEV_CODE;BATCH_SEQ;DEV_CHN_DESC;"+
                        "SPECIFICATION;MAN_CODE;QTY;SUM_QTY;RECEIPT_QTY;"+ 
                        "UNIT_CODE;UNIT_PRICE;TOT_VALUE;MAN_DATE;LAST_PRICE;"+
                        "GUAREP_DATE;DEPR_METHOD;USE_DEADLINE;DEP_DATE;"+
                        "MAN_NATION;SEQMAN_FLG;MEASURE_FLG;BENEFIT_FLG;"+
                        "FILES_WAY;VERIFY_NO;VERIFY_NO_SEQ;INWAREHOUSE_NO;SEQ_NO;DEVKIND_CODE";
        String stringMap[] = StringTool.parseLine(column,";");
        TParm tableDParm = new TParm();
        for(int i = 0;i<stringMap.length;i++){
            if(stringMap[i].equals("DEVPRO_CODE"))
                tableDParm.setData(stringMap[i],"A");
            else
                tableDParm.setData(stringMap[i],"");
        }
        ((TTable)getComponent("DEV_INWAREHOUSED")).addRow(tableDParm);
    }

    /**
     * 查询方法
     */
    public void onQuery(){
        ((TTable)getComponent("RECEIPT")).removeRowAll();
        ((TTable)getComponent("RECEIPT")).resetModify();
        TParm parm = new TParm();
        //入库单号
        if(getValueString("INWAREHOUSE_NO").length()!=0)
            parm.setData("INWAREHOUSE_NO",getValueString("INWAREHOUSE_NO"));
        //入库开始时间
        if(getValueString("INWARE_START_DATE").length()!=0)
            parm.setData("INWARE_START_DATE",StringTool.getTimestamp(getValueString("INWARE_START_DATE"),"yyyy-MM-dd"));
        //入库结束时间
        if(getValueString("INWARE_END_DATE").length()!=0)
            parm.setData("INWARE_END_DATE",StringTool.getTimestamp(getValueString("INWARE_END_DATE"),"yyyy-MM-dd"));
        //入库科室
        if(getValueString("INWAREHOUSE_DEPT").length()!=0)
            parm.setData("INWAREHOUSE_DEPT",getValueString("INWAREHOUSE_DEPT"));
        //入库人员
        if(getValueString("INWAREHOUSE_USER").length()!=0)
            parm.setData("INWAREHOUSE_USER",getValueString("INWAREHOUSE_USER"));
        //验收单号
        if(getValueString("RECEIPT_NO").length()!=0)
            parm.setData("RECEIPT_NO",getValueString("RECEIPT_NO"));
        //验收开始时间
        if(getValueString("RECEIPT_START_DATE").length()!=0)
            parm.setData("RECEIPT_START_DATE",StringTool.getTimestamp(getValueString("RECEIPT_START_DATE"),"yyyy-MM-dd"));
        //验收结束时间
        if(getValueString("RECEIPT_END_DATE").length()!=0)
            parm.setData("RECEIPT_END_DATE",StringTool.getTimestamp(getValueString("RECEIPT_END_DATE"),"yyyy-MM-dd"));
        if(parm.getNames().length == 0)
            return;
        parm = DevInStorageTool.getInstance().selectDevInStorageInf(parm);
        if(parm.getErrCode() < 0 )
            return;
        if(parm.getCount()<=0)
            return;
        ((TTable)getComponent("RECEIPT")).setParmValue(parm);

    }


    /**
     * 拿到供应商相关信息
     * @param receiptNo String
     * @return TParm
     */
    public TParm getSupInf(String receiptNo){
        TParm parm = new TParm(getDBTool().select(" SELECT B.SUP_CODE,B.SUP_SALES1,B.SUP_SALES1_TEL"+
                                                  " FROM DEV_RECEIPTM A, SYS_SUPPLIER B"+
                                                  " WHERE A.RECEIPT_NO = '"+receiptNo+"'"+
                                                  " AND   A.SUP_CODE = B.SUP_CODE"));
        return parm;
    }

    /**
     * 查询验收单产生生成入库单信息 fux
     */
    public void onGenerateReceipt(){
        if(getValueString("INWAREHOUSE_DATE").length() == 0){
            messageBox("请录入入库日期");
            return;
        }
        onClear();
        ((TTable)getComponent("RECEIPT")).setParmValue(((TTable)getComponent("RECEIPT")).getParmValue());
        TParm parmDiag = (TParm)openDialog("%ROOT%\\config\\dev\\ReceiptUI.x");
        if(parmDiag == null)
            return; 
        String receiptNo = parmDiag.getValue("RECEIPT_NO");
        //有SEQ_NO
        ReceiptD = DevInStorageTool.getInstance().selectReceiptD(receiptNo);
        //System.out.println("selectReceiptD"+ReceiptD);
        if(ReceiptD.getErrCode() < 0)
            return;
        if(ReceiptD.getCount() <= 0){ 
            messageBox("此入库单设备已经全部入库");
            return;
        } 
        setValue("SUP_CODE",parmDiag.getValue("SUP_CODE"));
        setValue("SUP_BOSSNAME",parmDiag.getValue("SUP_SALES1"));
        setValue("SUP_TEL",parmDiag.getValue("SUP_SALES1_TEL"));
        setValue("VERIFY_NO",receiptNo);  
        Timestamp timestamp = StringTool.getTimestamp(SystemTool.getInstance().getDate().toString(),"yyyy-MM-dd");
        TParm tableParm = new TParm();
        TParm tableParmDD = new TParm();
        for(int i = 0;i<ReceiptD.getCount();i++){
            //取得入库日期
            String inWarehouseDate = getValueString("INWAREHOUSE_DATE");
            //根据使用年限计算折旧终止日期
            int year = Integer.parseInt(inWarehouseDate.substring(0, 4)) +
                       ReceiptD.getInt("USE_DEADLINE", i);
            String depDate = year + inWarehouseDate.substring(4,inWarehouseDate.length());
            //根据折旧终止日期保修终止日期以及设备编码取得批号
            int batchNo = DevInStorageTool.getInstance().getUseBatchSeq(ReceiptD.getValue("DEV_CODE",i),
                                           StringTool.getTimestamp(depDate,"yyyy-MM-dd"),
                                           timestamp);
            tableParm.addData("DEL_FLG","N"); 
            tableParm.addData("DEVPRO_CODE",ReceiptD.getData("DEVPRO_CODE",i));
            tableParm.addData("DEV_CODE",ReceiptD.getData("DEV_CODE",i));
            tableParm.addData("BATCH_SEQ",batchNo);
            tableParm.addData("DEV_CHN_DESC",ReceiptD.getData("DEV_CHN_DESC",i));
            tableParm.addData("SPECIFICATION",ReceiptD.getData("SPECIFICATION",i));
            tableParm.addData("MAN_CODE",ReceiptD.getData("MAN_CODE",i));
            //入库数量,100,INT;累计入库数,100;验收数,100;     
            tableParm.addData("QTY",ReceiptD.getData("QTY",i)); 
            //初始带入累计入库数应该为0
            tableParm.addData("SUM_QTY",0);  
            //fux need modify
            tableParm.addData("RECEIPT_QTY",ReceiptD.getData("SUM_QTY",i)); 
            tableParm.addData("UNIT_CODE",ReceiptD.getData("UNIT_CODE",i));
            tableParm.addData("UNIT_PRICE",ReceiptD.getData("UNIT_PRICE",i));
            tableParm.addData("TOT_VALUE",ReceiptD.getDouble("UNIT_PRICE",i) * ReceiptD.getDouble("QTY",i));
            tableParm.addData("MAN_DATE",timestamp);
            tableParm.addData("LAST_PRICE",ReceiptD.getData("UNIT_PRICE",i));
            tableParm.addData("GUAREP_DATE",timestamp);
            tableParm.addData("DEPR_METHOD",ReceiptD.getData("DEPR_METHOD",i));
            tableParm.addData("USE_DEADLINE",ReceiptD.getData("USE_DEADLINE",i));
            tableParm.addData("DEP_DATE",StringTool.getTimestamp(depDate,"yyyy-MM-dd"));
            tableParm.addData("MAN_NATION",ReceiptD.getData("MAN_NATION",i));
            tableParm.addData("SEQMAN_FLG",ReceiptD.getData("SEQMAN_FLG",i));
            tableParm.addData("MEASURE_FLG",ReceiptD.getData("MEASURE_FLG",i));
            tableParm.addData("BENEFIT_FLG",ReceiptD.getData("BENEFIT_FLG",i));
            tableParm.addData("FILES_WAY","");
            tableParm.addData("VERIFY_NO",receiptNo);
            tableParm.addData("VERIFY_NO_SEQ",ReceiptD.getData("SEQ_NO",i));
            tableParm.addData("INWAREHOUSE_NO","");
            tableParm.addData("SEQ_NO",""); 
            tableParm.addData("DEVKIND_CODE",ReceiptD.getData("DEVKIND_CODE",i));
            //有序号管理则展开序号管理明细
            if("Y".equals(ReceiptD.getData("SEQMAN_FLG",i)))
                addDevDDTable(tableParmDD, tableParm, i);
        } 
        ((TTable)getComponent("DEV_INWAREHOUSED")).setParmValue(tableParm);
        ((TTable)getComponent("DEV_INWAREHOUSEDD")).setParmValue(tableParmDD);
        ((TTextFormat)getComponent("INWAREHOUSE_DATE")).setEnabled(false);
        setTableLock(); 
    }

    /**
     * 设置序号管理明细表信息
     * @param tableParmDD TParm 
     * @param tableParm TParm
     * @param row int 
     */ 
    public void addDevDDTable(TParm tableParmDD,TParm tableParm,int row){
    	//删,30,BOOLEAN;属性,100,DEV_PRO;设备编号,100;批次序号,100;设备名称,200;规格型号,100;批号,100;效期,100,Timestamp;生产厂商,200,MAN_TF;入库数量,100,INT;累计入库数,100;验收数,100;单位,100,DEV_UNIT;单价,100,DOUBLE;财产价值,100;出场日期,100,Timestamp;残值,100,DOUBLE;保修终止日期,100,Timestamp;折旧,60,DEP_METHOD;使用年限,100;折旧终止日期,100,Timestamp;生产国,100,MAN_NATION_TF;序号管理,100,BOOLEAN;计量设备,100,BOOLEAN;效益评估,100,BOOLEAN;技术文件,100;验收单号,100;验收单明细序号,100;入库单号,100;入库单序号,100;设备类别,100,DEVKIND_CODE
//    	DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;BATCH_SEQ;DEV_CODE;DDSEQ_NO;DEV_CHN_DESC;BARCODE;
//    	MAIN_DEV;MAN_DATE;MAN_SEQ;LAST_PRICE;GUAREP_DATE;DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;DEVSEQ_NO
        for(int i = 0;i<tableParm.getInt("QTY",row);i++){
            tableParmDD.addData("DEL_FLG","N");
            tableParmDD.addData("SELECT_FLG","Y");
            tableParmDD.addData("PRINT_FLG","Y");
            tableParmDD.addData("DEVPRO_CODE",tableParm.getData("DEVPRO_CODE",row));
            tableParmDD.addData("BATCH_SEQ",tableParm.getData("BATCH_SEQ",row));
            tableParmDD.addData("DEV_CODE",tableParm.getData("DEV_CODE",row));
            tableParmDD.addData("DDSEQ_NO",i + 1);
            tableParmDD.addData("DEV_CHN_DESC",tableParm.getData("DEV_CHN_DESC",row));
            tableParmDD.addData("MAIN_DEV","");
            tableParmDD.addData("MAN_DATE",tableParm.getData("MAN_DATE",row));
            tableParmDD.addData("MAN_SEQ","");
            tableParmDD.addData("LAST_PRICE",tableParm.getData("LAST_PRICE",row));
            tableParmDD.addData("GUAREP_DATE",tableParm.getData("GUAREP_DATE",row));
            tableParmDD.addData("DEP_DATE",tableParm.getData("DEP_DATE",row));
            tableParmDD.addData("TOT_VALUE",tableParm.getData("UNIT_PRICE",row));
            tableParmDD.addData("INWAREHOUSE_NO","");
            tableParmDD.addData("SEQ_NO","");
            tableParmDD.addData("DEVSEQ_NO",""); 
            tableParmDD.addData("BARCODE",""); 
   
        }
    }
 
    /**
     * 入库量改变事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableValueChange(Object obj) {
        TTableNode node = (TTableNode)obj;
        //删除动作
        onTableValueChange0(node);
        //设备属性
        onTableValueChange1(node);
        //设备编号动作
        onTableValueChange2(node);
        //入库量动作
        onTableValueChange7(node);
        //单价动作
        onTableValueChange11(node);
        //保修日期动作
        onTableValueChange15(node);
        //折旧日期动作
        onTableValueChange18(node); 
        return false;
   }


   /**
    * 设备删动作
    * @param node TTableNode
    */
   public void onTableValueChange0(TTableNode node){
        if(node.getColumn() != 0 )
            return;
        TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
        TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
        for(int i = 0;i < ddTable.getRowCount();i++){
            if(ddTable.getValueAt(i,5).equals(dTable.getValueAt(dTable.getSelectedRow(),2))&&
                    ddTable.getValueAt(i,4).equals(dTable.getValueAt(dTable.getSelectedRow(),3)))
               updateTableData("DEV_INWAREHOUSEDD", i, 0,node.getValue()); 
        }
   }

   /**
    * 设备属性动作
    * @param node TTableNode
    */
   public void onTableValueChange1(TTableNode node){
        if(node.getColumn() != 1 )
            return;
        TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
        //批次序号
        String devBatchNo = "" + dTable.getValueAt(node.getRow(),3);
        if(devBatchNo.length() != 0)
            node.setValue(dTable.getValueAt(node.getRow(),node.getColumn()));
   }

   /**
    * 更新数据表数据连同后端Parm
    * @param tableTag String
    * @param row int
    * @param column int
    * @param obj Object
    */
   public void updateTableData(String tableTag,int row,int column,Object obj){
       ((TTable)getComponent(tableTag)).setValueAt(obj,row,column);
       ((TTable)getComponent(tableTag)).getParmValue().setData(getFactColumnName(tableTag,column),row,obj);
   }

   /**
    * 设备编号动作
    * @param node TTableNode
    */
   public void onTableValueChange2(TTableNode node){
        if(node.getColumn() != 2 )
            return;
        String devCodeOld = "" + getTTable("DEV_INWAREHOUSED").getValueAt(node.getRow(),node.getColumn());
        if(("" +getTTable("DEV_INWAREHOUSED").getValueAt(node.getRow(),3)).length() != 0){
            node.setValue(devCodeOld);
            return;
        }
   }

   /**
    * 设备入库数量动作检核
    * @param node TTableNode
    * @return boolean
    */
   private boolean onTableValueChange9Check(TTableNode node){
       TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
//       if(("" + dTable.getValueAt(node.getRow(),26)).length() != 0&&
//          (Double.parseDouble("" + dTable.getValueAt(node.getRow(),11)) -
//           Double.parseDouble("" + dTable.getValueAt(node.getRow(),10)) <
//           Double.parseDouble("" + node.getValue()))){ 
           //messageBox("第" + String.valueOf(node.getRow()+1) + "行入库数量不可大于验收数与累计入库数之差");
       //fux need modify 20130802  
       if(("" + dTable.getValueAt(node.getRow(),26)).length() != 0&& 
    	          (Double.parseDouble("" + dTable.getValueAt(node.getRow(),11))<
    	           Double.parseDouble("" + node.getValue()))){  
           messageBox("第" + String.valueOf(node.getRow()+1) + "行入库数量不可大于验收数");
           node.setValue(dTable.getValueAt(node.getRow(),9));
           return true;
       }  
       if((""+node.getValue()).length() == 0){
           messageBox("第" + String.valueOf(node.getRow()+1) + "行请入库数量不能为空");
           node.setValue(dTable.getValueAt(node.getRow(),9));
           return true;
       }
       return false; 
   }
   /**
    * 设备入库数量动作
    * @param node TTableNode
    */
   public void onTableValueChange7(TTableNode node) {
       if(node.getColumn() != 7)
           return; 
       if(node.getValue().toString().length() == 0)
           return; 
       if(onTableValueChange9Check(node))
           return;
       TParm parm = ((TTable)getComponent("DEV_INWAREHOUSED")).getParmValue();
       TParm tableParmDD = new TParm();
       TTable dTable = ((TTable) getComponent("DEV_INWAREHOUSED"));
       //根据单价以及入库量计算总价值
       double unitPrice = 0;
       if(("" + dTable.getValueAt(node.getRow(), 11)).length() != 0)
           unitPrice = Double.parseDouble("" + dTable.getValueAt(node.getRow(), 11));
       double totValue = Double.parseDouble("" + node.getValue()) * unitPrice;
       updateTableData("DEV_INWAREHOUSED", node.getRow(), 12, totValue);
       //重新将序号管理明细展开
       for(int i = 0; i< parm.getCount("DEVPRO_CODE");i++){
           if("N".equals(parm.getData("SEQMAN_FLG",i)))
               continue; 
           int rowCount = 0;
           String value = ("" + node.getValue()).length() == 0?"0":("" + node.getValue());
           if(i == node.getRow())
               rowCount = Integer.parseInt(value);
           else
               rowCount = parm.getInt("QTY",i);
           for(int j = 0;j<rowCount;j++){
               tableParmDD.addData("DEL_FLG",parm.getData("DEL_FLG",i));
               tableParmDD.addData("SELECT_FLG","N");
               tableParmDD.addData("PRINT_FLG","N");
               tableParmDD.addData("DEVPRO_CODE",parm.getData("DEVPRO_CODE",i));
               tableParmDD.addData("BATCH_SEQ",parm.getData("BATCH_SEQ",i));
               tableParmDD.addData("DEV_CODE",parm.getData("DEV_CODE",i));
               tableParmDD.addData("DDSEQ_NO",j + 1);
               tableParmDD.addData("DEV_CHN_DESC",parm.getData("DEV_CHN_DESC",i));
               tableParmDD.addData("MAIN_DEV","");
               tableParmDD.addData("MAN_DATE",parm.getData("MAN_DATE",i));
               tableParmDD.addData("MAN_SEQ","");
               tableParmDD.addData("LAST_PRICE",parm.getData("LAST_PRICE",i));
               tableParmDD.addData("GUAREP_DATE",parm.getData("GUAREP_DATE",i));
               tableParmDD.addData("DEP_DATE",parm.getData("DEP_DATE",i));
               tableParmDD.addData("TOT_VALUE",parm.getData("UNIT_PRICE",i));
               tableParmDD.setData("INWAREHOUSE_NO","");
               tableParmDD.setData("SEQ_NO","");
               tableParmDD.setData("DEVSEQ_NO","");
               tableParmDD.setData("BARCODE","");
           }
       }
       ((TTable)getComponent("DEV_INWAREHOUSEDD")).setParmValue(tableParmDD);
   }
   /**
    * 单价动作
    * @param node TTableNode
    */
   public void onTableValueChange11(TTableNode node) {
       if(node.getColumn() != 11)
           return;
       if(node.getValue().toString().length() == 0)
           return;
       TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
       TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
       //根据修改的单价以及入库数量计算总价值
       int qty = 0;
       if(("" + dTable.getValueAt(node.getRow(),7)).length() != 0)
           qty = Integer.parseInt("" + dTable.getValueAt(node.getRow(),7));
       double totValue = Double.parseDouble("" + node.getValue()) * qty; 
       updateTableData("DEV_INWAREHOUSED", node.getRow(), 12, totValue);
       for(int i = 0;i < ddTable.getRowCount();i++){ 
           if(ddTable.getValueAt(i,5).equals(dTable.getValueAt(dTable.getSelectedRow(),2))&&
              ddTable.getValueAt(i,4).equals(dTable.getValueAt(dTable.getSelectedRow(),3)))
               updateTableData("DEV_INWAREHOUSEDD", i, 12, node.getValue());
        }
   }
   /**
    * 保修日期动作
    * @param node TTableNode
    */
   public void onTableValueChange15(TTableNode node){
       //fux need modify 20130802
        if(node.getColumn() != 15 )
            return;
        if(node.getValue().toString().length() == 0)
           return;
        TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
        TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
        Timestamp guarepDate = (Timestamp)node.getValue();
        Timestamp depDate = (Timestamp)dTable.getValueAt(dTable.getSelectedRow(),18);
        String devCode = "" + dTable.getValueAt(dTable.getSelectedRow(),2);
        int batNo = DevInStorageTool.getInstance().getUseBatchSeq(devCode,depDate,guarepDate);
        for(int i = 0;i < ddTable.getRowCount();i++){
            if(ddTable.getValueAt(i,5).equals(dTable.getValueAt(dTable.getSelectedRow(),2))&&
               ddTable.getValueAt(i,4).equals(dTable.getValueAt(dTable.getSelectedRow(),3))){
            	//4为BATCH_SEQ
                updateTableData("DEV_INWAREHOUSEDD", i, 9, node.getValue());
                if(batNo!=Integer.parseInt("" + ddTable.getValueAt(i,4)))
                updateTableData("DEV_INWAREHOUSEDD", i, 4, batNo);    
            }
        }
        if(batNo!=Integer.parseInt("" + dTable.getValueAt(dTable.getSelectedRow(),3)))
            updateTableData("DEV_INWAREHOUSED", dTable.getSelectedRow(), 3, batNo);
   }

   /**
    * 折旧日期动作
    * @param node TTableNode
    */
   public void onTableValueChange18(TTableNode node){
       //fux need modify 20130802
        if(node.getColumn() != 18 )
            return; 
        if(node.getValue().toString().length() == 0)
           return;
        TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
        TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
        Timestamp depDate = (Timestamp)node.getValue();
        Timestamp guarepDate = (Timestamp)dTable.getValueAt(dTable.getSelectedRow(),15);
        String devCode = "" + dTable.getValueAt(dTable.getSelectedRow(),2);
        int batNo = DevInStorageTool.getInstance().getUseBatchSeq(devCode,depDate,guarepDate);
        for(int i = 0;i < ddTable.getRowCount();i++){
            if(ddTable.getValueAt(i,5).equals(dTable.getValueAt(dTable.getSelectedRow(),2))&&
               ddTable.getValueAt(i,4).equals(dTable.getValueAt(dTable.getSelectedRow(),3))){
                updateTableData("DEV_INWAREHOUSEDD", i, 12, node.getValue());
                //3为BATCH_SEQ
                if(batNo!=Integer.parseInt("" + ddTable.getValueAt(i,4)))
                    updateTableData("DEV_INWAREHOUSEDD", i, 4, batNo);
            }
        }
        if(batNo!=Integer.parseInt("" + dTable.getValueAt(dTable.getSelectedRow(),3)))
            updateTableData("DEV_INWAREHOUSED", dTable.getSelectedRow(), 3, batNo);
   }

   /**
    * 保存方法
    */
   public void onSave(){
       //检核是否有保存数据
       if(onSaveCheck())
           return;
       if(getValueString("INWAREHOUSE_DATE").length() == 0){
           messageBox("入库日期不可为空");
           return;
       }
       if(getValueString("OPERATOR").length() == 0){
           messageBox("入库人员不可为空");
           return;
       }
       if(getValueString("DEPT").length() == 0){
           messageBox("入库科室不可为空");
           return;
       }
       getTTable("DEV_INWAREHOUSED").acceptText();
       getTTable("DEV_INWAREHOUSEDD").acceptText();
       //判断是新增还是修改
       if(((TTable)getComponent("RECEIPT")).getSelectedRow()<0)
           onNew();
       else
           onUpdate();
   }

   /**
    * 保存修改动作
    */
   private void onUpdate(){
       Timestamp timestamp = SystemTool.getInstance().getDate();
       TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
       TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
       TParm tableParmD = dTable.getParmValue();
       TParm tableParmDD = ddTable.getParmValue();
       TParm parmInD = new TParm();
       TParm parmInDD = new TParm();
       TParm parmStockM = new TParm();
       TParm parmStockD = new TParm();
       TParm parmStockDD = new TParm();
       for(int i = 0;i<tableParmD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmD,tableParmD,i))
               continue;
           cloneTParm(tableParmD,parmInD,i);
           parmInD.addData("OPT_USER",Operator.getID());
           parmInD.addData("OPT_DATE",timestamp);
           parmInD.addData("OPT_TERM",Operator.getIP());
           cloneTParm(tableParmD,parmStockM,i);
           parmStockM.addData("OPT_USER",Operator.getID());
           parmStockM.addData("OPT_DATE",timestamp);
           parmStockM.addData("OPT_TERM",Operator.getIP());
           cloneTParm(tableParmD,parmStockD,i);
           parmStockD.addData("OPT_USER",Operator.getID());
           parmStockD.addData("OPT_DATE",timestamp);
           parmStockD.addData("OPT_TERM",Operator.getIP());
       }
       for(int i = 0;i<tableParmDD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmDD,tableParmDD,i))
               continue;
           cloneTParm(tableParmDD,parmInDD,i);
           parmInDD.addData("OPT_USER",Operator.getID());
           parmInDD.addData("OPT_DATE",timestamp);
           parmInDD.addData("OPT_TERM",Operator.getIP());
           cloneTParm(tableParmDD,parmStockDD,i);
           parmStockDD.addData("OPT_USER",Operator.getID());
           parmStockDD.addData("OPT_DATE",timestamp);
           parmStockDD.addData("OPT_TERM",Operator.getIP());
       }    
       TParm parm = new TParm();
       parm.setData("DEV_INWAREHOUSED",parmInD.getData());
       parm.setData("DEV_INWAREHOUSEDD",parmInDD.getData());
       parm.setData("DEV_STOCKM",parmStockM.getData());
       parm.setData("DEV_STOCKD",parmStockD.getData());
       parm.setData("DEV_STOCKDD",parmStockDD.getData());
       parm = TIOM_AppServer.executeAction(
           "action.dev.DevAction","updateInStorageReceipt", parm);
       if(parm.getErrCode() < 0){
           messageBox("保存失败");
           return; 
       }
       messageBox("保存成功");
       onDevInwarehouse();
       onPrintReceipt(); 
   }
   /**
    * 新增
    */
   private void onNew()
   { 
     String inwarehouseNo = DevInStorageTool.getInstance().getInwarehouseNo();
     Timestamp timestamp = SystemTool.getInstance().getDate();
     TParm dParm = new TParm();
     TParm ddParm = new TParm();
     TParm mParm = new TParm();
     TParm stockMParm = new TParm();
     TParm stockDParm = new TParm();
     TParm stockDDParm = new TParm();
     TTable dTable = (TTable)getComponent("DEV_INWAREHOUSED");
     TTable ddTable = (TTable)getComponent("DEV_INWAREHOUSEDD");

     for (int i = 0; i < dTable.getRowCount(); ++i)
     { 
       //DEV_CODE
       if ((""+dTable.getValueAt(i, 2)).length() == 0) {
         continue; 
       }  
       if (dTable.getValueAt(i, 0).equals("Y"))
       {
         continue;
       } 
       //DEL_FLG;DEVPRO_CODE;DEV_CODE;BATCH_SEQ;DEV_CHN_DESC;SPECIFICATION;BATCH_NO;VALID_DATE;MAN_CODE;QTY;SUM_QTY;RECEIPT_QTY;UNIT_CODE;UNIT_PRICE;TOT_VALUE;MAN_DATE;LAST_PRICE;GUAREP_DATE;DEPR_METHOD;USE_DEADLINE;DEP_DATE;MAN_NATION;SEQMAN_FLG;MEASURE_FLG;BENEFIT_FLG;FILES_WAY;VERIFY_NO;VERIFY_NO_SEQ;INWAREHOUSE_NO;SEQ_NO;DEVKIND_CODE
       Object obj = null;
       TParm parm = (TParm)obj;  
       parm = dTable.getParmValue();
       //获得批号   ----------- fux need modify 20130813
       String inWarehouseDate = getValueString("INWAREHOUSE_DATE");
       int year = Integer.parseInt(inWarehouseDate.substring(0, 4)) + parm.getInt("USE_DEADLINE",i);
       String depDate = year + inWarehouseDate.substring(4,inWarehouseDate.length()); 
       int batchSeq = DevInStorageTool.getInstance().getUseBatchSeq(parm.getValue("DEV_CODE",i),
                                                                  StringTool.getTimestamp(depDate,"yyyy-MM-dd"),
                                                                  timestamp);
       dParm.addData("INWAREHOUSE_NO", inwarehouseNo); 
       dParm.addData("SEQ_NO", Integer.valueOf(i + 1)); 
       dParm.addData("DEV_CODE", dTable.getItemData(i, "DEV_CODE"));
       dParm.addData("BATCH_SEQ", batchSeq);
       dParm.addData("SEQMAN_FLG", dTable.getItemData(i, "SEQMAN_FLG"));
       dParm.addData("QTY", dTable.getItemData(i, "QTY"));
       dParm.addData("UNIT_PRICE", dTable.getItemData(i, "UNIT_PRICE"));
       dParm.addData("MAN_DATE", dTable.getItemData(i, "MAN_DATE"));
       dParm.addData("SCRAP_VALUE", dTable.getItemData(i, "LAST_PRICE"));
       dParm.addData("GUAREP_DATE", dTable.getItemData(i, "GUAREP_DATE"));
       dParm.addData("DEP_DATE", dTable.getItemData(i, "DEP_DATE"));
       dParm.addData("FILES_WAY", dTable.getItemData(i, "FILES_WAY"));
       dParm.addData("VERIFY_NO", dTable.getItemData(i, "VERIFY_NO"));
       dParm.addData("VERIFY_NO_SEQ", dTable.getItemData(i, "VERIFY_NO_SEQ"));
       dParm.addData("OPT_USER", Operator.getID());
       dParm.addData("OPT_DATE", timestamp); 
       dParm.addData("OPT_TERM", Operator.getIP());
       //打印准备 
       dParm.addData("DEVPRO_CODE", dTable.getItemData(i, "DEVPRO_CODE"));
       dParm.addData("DEV_CHN_DESC", dTable.getItemData(i, "DEV_CHN_DESC"));
       dParm.addData("SPECIFICATION", dTable.getItemData(i, "SPECIFICATION"));
       dParm.addData("TOT_VALUE", dTable.getItemData(i, "TOT_VALUE"));
//       INSERT INTO DEV_INWAREHOUSEDD ( INWAREHOUSE_NO, SEQ_NO, DDSEQ_NO, DEV_CODE, DEVSEQ_NO, BATCH_SEQ, SETDEV_CODE, MAN_DATE, MANSEQ_NO, SCRAP_VALUE, GUAREP_DATE,DEP_DATE,UNIT_PRICE,OPT_USER, OPT_DATE, OPT_TERM, RFID , ORGIN_CODE) VALUES ( <INWAREHOUSE_NO>, <SEQ_NO>, <DDSEQ_NO>, <DEV_CODE>, <DEVSEQ_NO>, <BATCH_SEQ>, <SETDEV_CODE>, <MAN_DATE>, <MANSEQ_NO>, <SCRAP_VALUE>, <GUAREP_DATE>,<DEP_DATE>,<UNIT_PRICE>,<OPT_USER>, <OPT_DATE>, <OPT_TERM>,<RFID>,<ORGIN_CODE>)
//       {Data={MANSEQ_NO=null, RFID=, UNIT_PRICE=null, MAN_DATE=2013-07-18 00:00:00.0, OPT_TERM=127.0.0.1, OPT_DATE=2013-07-18 09:10:48.0, SEQ_NO=1, INWAREHOUSE_NO=130718000007, SCRAP_VALUE=null, DEV_CODE=211AA0001, DEP_DATE=2023-07-18 00:00:00.0, DDSEQ_NO=1, OPT_USER=D001, BATCH_SEQ=1, DEVSEQ_NO=1, SETDEV_CODE=null, ORGIN_CODE=null, GUAREP_DATE=2013-07-18 00:00:00.0}}
       stockMParm.addData("DEPT_CODE", getValue("DEPT"));
       stockMParm.addData("DEV_CODE", dTable.getItemData(i, "DEV_CODE"));
       stockMParm.addData("MAN_DATE", dTable.getItemData(i, "MAN_DATE"));
       //残值确认是否应该都为0
       stockMParm.addData("SCRAP_VALUE", dTable.getItemData(i, "LAST_PRICE"));
       stockMParm.addData("GUAREP_DATE", dTable.getItemData(i, "GUAREP_DATE"));
       stockMParm.addData("DEP_DATE", dTable.getItemData(i, "DEP_DATE"));
       stockMParm.addData("FILES_WAY", dTable.getItemData(i, "FILES_WAY"));
       stockMParm.addData("CARE_USER", getValue("OPERATOR"));
       stockMParm.addData("USE_USER", ""); 
       stockMParm.addData("LOC_CODE", "");  
       stockMParm.addData("INWAREHOUSE_DATE", StringTool.getTimestampDate(timestamp));
       stockMParm.addData("STOCK_FLG", "");
       stockMParm.addData("QTY", dTable.getItemData(i, "QTY"));
       stockMParm.addData("UNIT_PRICE", dTable.getItemData(i, "UNIT_PRICE"));
       stockMParm.addData("OPT_USER", Operator.getID());
       stockMParm.addData("OPT_DATE", timestamp);
       stockMParm.addData("OPT_TERM", Operator.getIP());
       stockMParm.addData("REGION_CODE", Operator.getRegion());
       //System.out.println("stockMParm" + stockMParm);
 
       TParm devBase = getDevBase(""+dTable.getItemData(i, "DEV_CODE"));
       stockDParm.addData("DEPT_CODE", getValue("DEPT"));
       stockDParm.addData("DEV_CODE", dTable.getItemData(i, "DEV_CODE"));
       stockDParm.addData("BATCH_SEQ", batchSeq);
       stockDParm.addData("DEVKIND_CODE", dTable.getItemData(i, "DEVKIND_CODE"));
       stockDParm.addData("DEVTYPE_CODE", getDevTypeCode((String) dTable.getValueAt(i, 2)));
       stockDParm.addData("DEVPRO_CODE", dTable.getItemData(i, "DEVPRO_CODE"));
       if(devBase.getData("SETDEV_CODE", 0)!=null){  
    	       stockDParm.addData("SETDEV_CODE", devBase.getData("SETDEV_CODE", 0));
           } 
           else{ 
        	   stockDParm.addData("SETDEV_CODE", "");   
           } 
       stockDParm.addData("SPECIFICATION", dTable.getItemData(i, "SPECIFICATION"));

       stockDParm.addData("QTY", dTable.getItemData(i, "QTY"));
       stockDParm.addData("UNIT_PRICE", dTable.getItemData(i, "UNIT_PRICE"));
       stockDParm.addData("BUYWAY_CODE", devBase.getData("BUYWAY_CODE", 0));
       stockDParm.addData("MAN_NATION", devBase.getData("MAN_NATION", 0));
       stockDParm.addData("MAN_CODE", devBase.getData("MAN_CODE", 0));
       stockDParm.addData("SUPPLIER_CODE", "");
       stockDParm.addData("MAN_DATE", dTable.getItemData(i, "MAN_DATE"));
       stockDParm.addData("MANSEQ_NO", Integer.valueOf(0));
       stockDParm.addData("FUNDSOURCE", "");
       stockDParm.addData("APPROVE_AMT", "");
       stockDParm.addData("SELF_AMT", "");
       stockDParm.addData("GUAREP_DATE", dTable.getItemData(i, "GUAREP_DATE"));
       stockDParm.addData("DEPR_METHOD", devBase.getData("DEPR_METHOD", 0));
       stockDParm.addData("DEP_DATE", dTable.getItemData(i, "DEP_DATE"));
       stockDParm.addData("SCRAP_VALUE", dTable.getItemData(i, "LAST_PRICE"));
       stockDParm.addData("QUALITY_LEVEL", "");
       stockDParm.addData("DEV_CLASS", devBase.getData("DEV_CLASS", 0));
       stockDParm.addData("STOCK_STATUS", "");
       stockDParm.addData("SERVICE_STATUS", "");
       stockDParm.addData("CARE_USER", getValue("OPERATOR"));
       stockDParm.addData("USE_USER", "");
       stockDParm.addData("LOC_CODE", ""); 

       stockDParm.addData("MEASURE_FLG", devBase.getData("MEASURE_FLG", 0));
       stockDParm.addData("MEASURE_ITEMDESC", devBase.getData("MEASURE_ITEMDESC", 0));
       stockDParm.addData("MEASURE_DATE", "");
       stockDParm.addData("OPT_USER", Operator.getID());
       stockDParm.addData("OPT_DATE", timestamp);
       stockDParm.addData("OPT_TERM", Operator.getIP());

       stockDParm.addData("INWAREHOUSE_DATE", StringTool.getTimestampDate(timestamp));
       int maxDevSeqNo = 0;
       for (int j = 0; j < ddTable.getRowCount(); j++)
       { 
           if (dTable.getValueAt(i, 2).equals(ddTable.getValueAt(j, 5))) {
           if (!(dTable.getValueAt(i, 3).equals(ddTable.getValueAt(j, 4))))
             continue; 
           //DEL_FLG
           if (ddTable.getValueAt(j, 0).equals("Y"))
             continue;     
             //fux  select_flg待修改
//           //SELECT_FLG
//           if (ddTable.getValueAt(j, 1).equals("N"))
//               continue; 
           ddParm.addData("INWAREHOUSE_NO", inwarehouseNo);
           ddParm.addData("SEQ_NO", Integer.valueOf(i + 1)); 
           ddParm.addData("DDSEQ_NO", ddTable.getItemData(j, "DDSEQ_NO"));
           for (int k = 0; k < ddParm.getCount("DEV_CODE"); ++k) {
             if ((!(ddParm.getValue("DEV_CODE", k).equals(ddTable.getValueAt(j, 5)))) || 
               (maxDevSeqNo >= ddParm.getInt("DEVSEQ_NO", k))) continue;
             maxDevSeqNo = ddParm.getInt("DEVSEQ_NO", k); 
           } 
           if(maxDevSeqNo ==0){ 
        	   
             maxDevSeqNo = DevInStorageTool.getInstance().getMaxDevSeqNo(""+ddTable.getValueAt(j, 5));
           } 
           else{
        	 maxDevSeqNo++;  
           } 
           //DEVKIND_CODE  IN_DEPT OUT_DEPT 
           String barCode = SystemTool.getInstance().getNo("ALL", "DEV", "BARCODE_NO", "BARCODE_NO");  
           //DEL_FLG;PRINT_FLG;DEVPRO_CODE;BATCH_SEQ;DEV_CODE;DDSEQ_NO;DEV_CHN_DESC;RFID;MAIN_DEV;MAN_DATE;MAN_SEQ;LAST_PRICE;
           //GUAREP_DATE;DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;DEVSEQ_NO
//           ddParm{Data={MANSEQ_NO=[null, null],  UNIT_PRICE=[null, null] SCRAP_VALUE=[null, null], SETDEV_CODE=[null, null], ORGIN_CODE=[null, null], }
//           stockDDParm{Data={RFID=[, ], BATCH_NO=[null, null],  VALID_DATE=[null, null], STOCK_UNIT=[null, null], SETDEV_CODE=[null, null],  ORGIN_CODE=[null, null],}}
           ddParm.addData("DEV_CODE", ddTable.getItemData(j, "DEV_CODE"));
           //序号管理明细序号       
           ddParm.addData("DEVSEQ_NO", Integer.valueOf(maxDevSeqNo));  
           ddParm.addData("BATCH_SEQ", batchSeq); 
           ddParm.addData("SEQMAN_FLG", "Y"); 
           if(devBase.getData("SETDEV_CODE", 0)!=null){
        	   ddParm.addData("SETDEV_CODE", devBase.getData("SETDEV_CODE", 0));
               } 
               else{ 
               ddParm.addData("SETDEV_CODE", "");   
               }  
           ddParm.addData("MAN_DATE", ddTable.getItemData(j, "MAN_DATE"));
           ddParm.addData("MANSEQ_NO", ddTable.getItemData(j, "MAN_SEQ"));
           ddParm.addData("SCRAP_VALUE", ddTable.getItemData(j, "LAST_PRICE"));
           ddParm.addData("GUAREP_DATE", ddTable.getItemData(j, "GUAREP_DATE"));
           ddParm.addData("DEP_DATE", ddTable.getItemData(j, "DEP_DATE"));
           ddParm.addData("UNIT_PRICE", dTable.getItemData(i, "UNIT_PRICE"));
           ddParm.addData("OPT_USER", Operator.getID());
           ddParm.addData("OPT_DATE", timestamp); 
           ddParm.addData("OPT_TERM", Operator.getIP()); 
           //默认rfid先为"" 加入普通条码
           ddParm.addData("BARCODE", barCode); 
           ddParm.addData("RFID", "");
           //System.out.println("ddParm" + ddParm);
           devBase = getDevBase(""+ddTable.getItemData(j, "DEV_CODE"));
           stockDDParm.addData("DEV_CODE", ddTable.getItemData(j, "DEV_CODE"));
           stockDDParm.addData("DEVSEQ_NO", Integer.valueOf(maxDevSeqNo));
           stockDDParm.addData("REGION_CODE", Operator.getRegion());
           stockDDParm.addData("BATCH_SEQ", batchSeq);
           stockDDParm.addData("DEPT_CODE", getValue("DEPT"));
           stockDDParm.addData("STOCK_QTY", "1");
           stockDDParm.addData("UNIT_PRICE", dTable.getItemData(i, "UNIT_PRICE"));
           stockDDParm.addData("STOCK_UNIT", dTable.getItemData(i, "UNIT_CODE"));
           stockDDParm.addData("CHECKTOLOSE_FLG", "N");
           stockDDParm.addData("WAST_FLG", "N"); 
           stockDDParm.addData("INWAREHOUSE_DATE", StringTool.getTimestampDate(timestamp));
           stockDDParm.addData("WAIT_ORG_CODE", "");
           stockDDParm.addData("OPT_USER", Operator.getID());
           stockDDParm.addData("OPT_DATE", timestamp);
           stockDDParm.addData("OPT_TERM", Operator.getIP()); 
           //默认rfid先为"" 加入普通条码
           stockDDParm.addData("RFID", "");
           stockDDParm.addData("BARCODE", barCode);  
           stockDDParm.addData("ACTIVE_FLG", devBase.getData("ACTIVE_FLG", 0));
           stockDDParm.addData("SEQMAN_FLG", "Y"); 
           if(devBase.getData("SETDEV_CODE", 0)!=null){
           stockDDParm.addData("SETDEV_CODE", devBase.getData("SETDEV_CODE", 0));
           } 
           else{ 
           stockDDParm.addData("SETDEV_CODE", "");    
           }    
           //原值，月折旧值，累计折旧值，现值(待修改)  
           TParm parmDep  = DevDepTool.getInstance().selectSeqDevInf(getDevBase(ddTable.getItemString(j, "DEV_CODE")),
        		   dTable.getItemDouble(i, "UNIT_PRICE"),inWarehouseDate);   
           //parmDep{Data={MDEP_PRICE=100.00, CURR_PRICE=59900.00, DEP_PRICE=100.00}}
           System.out.println("parmDep"+parmDep);    
           Double mdepPrice = Double.parseDouble(parmDep.getData("MDEP_PRICE").toString()); 
           Double depPrice = Double.parseDouble(parmDep.getData("DEP_PRICE").toString()); 
           Double currPrice = Double.parseDouble(parmDep.getData("CURR_PRICE").toString()); 
           System.out.println("!"+ mdepPrice);        
           System.out.println("@"+ depPrice);  
           System.out.println("#"+ currPrice); 
           stockDDParm.addData("UNIT_PRICE", dTable.getItemData(i, "UNIT_PRICE")); 
           stockDDParm.addData("MDEP_PRICE", mdepPrice);   
           stockDDParm.addData("DEP_PRICE", depPrice);     
           stockDDParm.addData("CURR_PRICE", currPrice); 
           //System.out.println("stockDDParm"+stockDDParm);   
         } 
       }   
     }
     mParm.addData("INWAREHOUSE_NO", inwarehouseNo);
     mParm.addData("VERIFY_NO", dTable.getItemData(0, "VERIFY_NO"));
     mParm.addData("INWAREHOUSE_DATE", StringTool.getTimestampDate(timestamp));
     mParm.addData("INWAREHOUSE_USER", getValue("OPERATOR"));
     mParm.addData("INWAREHOUSE_DEPT", getValue("DEPT"));
     mParm.addData("OPT_USER", Operator.getID());
     mParm.addData("OPT_DATE", timestamp);
     mParm.addData("OPT_TERM", Operator.getIP());
     //System.out.println("mParm" + mParm);
     TParm parm = new TParm();
     parm.setData("DEV_INWAREHOUSEM", mParm.getData());
     parm.setData("DEV_INWAREHOUSED", dParm.getData());
     parm.setData("DEV_INWAREHOUSEDD", ddParm.getData());
     parm.setData("DEV_STOCKM", stockMParm.getData());
     parm.setData("DEV_STOCKD", stockDParm.getData());
     parm.setData("DEV_STOCKDD", stockDDParm.getData());  
     //新加数据     
     TParm  parmReceipt = new TParm();  
     String receiptNo =  this.getValueString("VERIFY_NO");
     parmReceipt.setData("RECEIPT_NO",receiptNo);   
     parm.setData("DEV_RECEIPTD",getReceiptDData(parmReceipt).getData()); 
     parm.setData("DEV_RECEIPTM",getReceiptMData(parmReceipt).getData()); 
     TParm result = TIOM_AppServer.executeAction(
       "action.dev.DevAction", "generateInStorageReceipt", parm);
     if (result.getErrCode() < 0) {
       messageBox("保存失败");  
   	   err(result.getErrText()); 
       return;  
     }
     messageBox("保存成功");
     //fux modify 20130815 加入条码打印
     onPrintBarcode();
     onPrintNew(parm);
     onClear(); 
     onQuery();
   } 
   //由于基础字典变动,所以这里也要改动，最好改成* 
   /**
    * 得到设备基本属性信息
    * @param devCode String
    * @return TParm
    */
   public TParm getDevBase(String devCode){
       String SQL="SELECT * FROM DEV_BASE WHERE DEV_CODE = '"+devCode+"'";
       TParm parm = new TParm(getDBTool().select(SQL));
       return parm;  
    }
    /**
     * 得到设备编码规则信息
     * @param code String
     * @return String
     */
    private String getDevTypeCode(String code){
       TParm parm = DevTypeTool.getInstance().getDevRule();
       int classify1 = parm.getInt("CLASSIFY1",0);
       return code.substring(0,classify1);
    }
    
     
    /**
     * 验收单细项数据
     * @param parm TParm
     * @return TParm
     */
    public TParm getReceiptDData(TParm parm) {         
        TTable dTable = (TTable)getComponent("DEV_INWAREHOUSED");
        TParm receipt = new TParm(); 
        for (int i = 0; i < dTable.getRowCount(); i++) {
            if ("Y".equals(dTable.getItemString(i, "DEL_FLG"))) {
                continue;
            } 
            else if ("".equals(dTable.getParmValue().getValue("DEV_CODE", i))) {
                continue;     
            }     
            else  {    
            	receipt.addData("RECEIPT_NO", parm.getData("RECEIPT_NO"));
            	receipt.addData("SEQ_NO",ReceiptD.getData("SEQ_NO",i)); 
            	receipt.addData("QTY", dTable.getItemDouble(i, "QTY"));
            	//QTY;SUM_QTY;RECEIPT_QTY     
            	//dTable.getParmValue().getDouble("SUM_QTY", i) ==
                if (dTable.getItemDouble(i, "QTY") == 
                	dTable.getParmValue().getDouble("RECEIPT_QTY", i)
                	||  dTable.getItemDouble(i, "SUM_QTY") ==
                    	dTable.getParmValue().getDouble("RECEIPT_QTY", i)) {
                	receipt.addData("FINAL_FLG", "1");
                }       
                else {     
                	receipt.addData("FINAL_FLG", "0"); 
                }
                receipt.addData("OPT_USER", Operator.getID());
                receipt.addData("OPT_DATE",
                                 SystemTool.getInstance().getDate());
                receipt.addData("OPT_TERM", Operator.getIP());
            }
        }
        return receipt;
    }

    /**
     * 验收单主项数据
     * @param parm TParm
     * @return TParm
     */
    public TParm getReceiptMData(TParm parm) {
        TParm ReceiptM = new TParm();  
        ReceiptM.addData("RECEIPT_NO",parm.getData("RECEIPT_NO"));
        boolean flg = true; 
        TParm ReceiptD = parm.getParm("DEV_RECEIPTD");
        for (int i = 0; i < ReceiptD.getCount("RECEIPT_NO"); i++) {
            if ("0".equals(ReceiptD.getValue("FINAL_FLG", i))) {
                flg = false;
                break; 
            } 
        }  
        if (flg) {
        	ReceiptM.addData("FINAL_FLG", "1"); 
        }  
        else {
        	ReceiptM.addData("FINAL_FLG", "0");
        }
        ReceiptM.addData("OPT_USER", Operator.getID());
        ReceiptM.addData("OPT_DATE",
                         SystemTool.getInstance().getDate());
        ReceiptM.addData("OPT_TERM", Operator.getIP()); 
        return ReceiptM;   
    }

    /**
     * 保存后打印设备入库单
     * @param inParm TParm
     */
    public void onPrintNew(TParm inParm){
       //设置表头信息
       TParm printParm = new TParm();  
       printParm.setData("HOSP_NAME_T",Operator.getHospitalCHNFullName());
       printParm.setData("INWAREHOUSE_NO_T",inParm.getParm("DEV_INWAREHOUSEM").getData("INWAREHOUSE_NO",0));
       printParm.setData("MAN_CODE_T",getSupDesc(getValueString("SUP_CODE")));
       printParm.setData("INWAREHOUSE_DEPT_T",getDeptDesc(inParm.getParm("DEV_INWAREHOUSEM").getValue("INWAREHOUSE_DEPT",0)));
       printParm.setData("INWAREHOUSE_DATE_T", inParm.getParm("DEV_INWAREHOUSEM").getValue("INWAREHOUSE_DATE",0).substring(0,10).replace('-','/'));
       printParm.setData("CONTACT_T",getValue("SUP_BOSSNAME"));
       printParm.setData("INWAREHOUSE_USER_T",getOperatorName(inParm.getParm("DEV_INWAREHOUSEM").getValue("INWAREHOUSE_USER",0)));
       printParm.setData("VERIFY_NO_T",inParm.getParm("DEV_INWAREHOUSEM").getValue("VERIFY_NO",0));
       printParm.setData("TEL_NO_T",getValue("SUP_TEL"));
       TParm parm = new TParm();
       //将数据拷贝到入库单传入参当中
       for(int i = 0;i<inParm.getParm("DEV_INWAREHOUSED").getCount("DEV_CODE");i++){
           cloneTParm(inParm.getParm("DEV_INWAREHOUSED"),parm,i);
       }
       //转换时间日期格式
       for(int i = 0;i<parm.getCount("DEVPRO_CODE");i++){
           parm.setData("MAN_DATE",i,parm.getValue("MAN_DATE",i).substring(0,10).replace('-','/'));
           parm.setData("GUAREP_DATE",i,parm.getValue("GUAREP_DATE",i).substring(0,10).replace('-','/'));
           parm.setData("DEP_DATE",i,parm.getValue("DEP_DATE",i).substring(0,10).replace('-','/'));
           parm.setData("DEVPRO_CODE",i,getDevProDesc(parm.getValue("DEVPRO_CODE",i)));
       }
       //设置入库单表格信息
       parm.setCount(inParm.getParm("DEV_INWAREHOUSED").getCount("DEV_CODE"));
       parm.addData("SYSTEM", "COLUMNS", "DEVPRO_CODE");
       parm.addData("SYSTEM", "COLUMNS", "BATCH_SEQ");
       parm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
       parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
       parm.addData("SYSTEM", "COLUMNS", "QTY");
       parm.addData("SYSTEM", "COLUMNS", "TOT_VALUE");
       parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
       parm.addData("SYSTEM", "COLUMNS", "MAN_DATE");
       parm.addData("SYSTEM", "COLUMNS", "SCRAP_VALUE");
       parm.addData("SYSTEM", "COLUMNS", "GUAREP_DATE");
       parm.addData("SYSTEM", "COLUMNS", "DEP_DATE");
       printParm.setData("RECEIPT",parm.getData()); 
       openPrintWindow("%ROOT%\\config\\prt\\dev\\DevInStorageReceipt.jhw",printParm);
   }
   /** 
    * 清空方法
    */
   public void onClear(){
       setValue("INWAREHOUSE_NO_QUARY","");
       setValue("INWAREHOUSE_DATE","");
       ((TTextFormat)getComponent("INWAREHOUSE_DATE")).setEnabled(true);
       setValue("VERIFY_NO",""); 
       setValue("DEPT",""); 
       ((TTextFormat)getComponent("DEPT")).setEnabled(true);
       setValue("OPERATOR","");
       ((TTextFormat)getComponent("OPERATOR")).setEnabled(true);
       ((TTable)getComponent("RECEIPT")).removeRowAll();
       ((TTable)getComponent("RECEIPT")).resetModify();
       setValue("SUP_CODE","");
       setValue("SUP_BOSSNAME","");
       setValue("SUP_TEL","");
       parmD = new TParm();
       parmDD = new TParm();
       initComponent();
       initTableD();
       initTableDD();
       addDRow();
       setTableLock();
   } 
   /**
    * 设备入库单主信息单击事件
    */
   public void onDevInwarehouse(){
	   //fux 2013
       TTable table = ((TTable)getComponent("RECEIPT"));
       int row = table.getSelectedRow();
       TParm parm = DevInStorageTool.getInstance().selectDevInwarehouseD("" + table.getValueAt(row,1));
       if(parm.getErrCode()<0)
           return;
       parmD = new TParm();
       for(int i = 0;i<parm.getCount();i++)   
           cloneTParm(parm,parmD,i);
       ((TTable)getComponent("DEV_INWAREHOUSED")).setParmValue(parm); 
       parm = DevInStorageTool.getInstance().selectDevInwarehouseDD("" + table.getValueAt(row,1));
       if(parm.getErrCode()<0)
         return;
       parmDD = new TParm(); 
      
       for(int i = 0;i<parm.getCount();i++) 
           cloneTParm(parm,parmDD,i);
       ((TTable)getComponent("DEV_INWAREHOUSEDD")).setParmValue(parm);
       ((TTable)getComponent("DEV_INWAREHOUSED")).setLockColumns("0,1,2,3,4,5,6,"+
                                                                 "7,8,9,10,12,15,"+
                                                                 "16,17,18,19,20,"+
                                                                 "21,22,24,25,26,27,28");
       ((TTable)getComponent("DEV_INWAREHOUSEDD")).setLockColumns("0,1,2,3,4,5,10,"+
                                                                  "11,12,13,14,15");
       setValue("INWAREHOUSE_NO_QUARY",table.getValueAt(row,1));
       setValue("INWAREHOUSE_DATE",table.getValueAt(row,0));
       ((TTextFormat)getComponent("INWAREHOUSE_DATE")).setEnabled(false);
       setValue("VERIFY_NO",table.getValueAt(row,3));
       setValue("DEPT",table.getValueAt(row,2));
       ((TTextFormat)getComponent("DEPT")).setEnabled(false); 
       setValue("OPERATOR",table.getValueAt(row,5));
       ((TTextFormat)getComponent("OPERATOR")).setEnabled(false);
       if(("" + table.getValueAt(row,3)).length() == 0)
           return;
       TParm supParm = getSupInf("" + table.getValueAt(row,3));
       if(supParm.getErrCode() < 0)
           return;
       if(supParm.getCount() < 0)
           return;
       setValue("SUP_CODE",supParm.getValue("SUP_CODE",0));
       setValue("SUP_BOSSNAME",supParm.getValue("SUP_SALES1",0));
       setValue("SUP_TEL",supParm.getValue("SUP_SALES1_TEL",0));
   }

   /**
    * 拷贝TParm
    * @param from TParm
    * @param to TParm
    * @param row int
    */
   private void cloneTParm(TParm from,TParm to,int row){
       String names[] = from.getNames();
       for(int i = 0;i < names.length;i++){
           Object obj = from.getData(names[i],row);
           if(obj == null)
               obj = "";
           to.addData(names[i],obj);
       }
    }

    /**
     * 比较相同列TParm的值是否改变
     * @param parmA TParm
     * @param parmB TParm
     * @param row int
     * @return boolean
     */
    private boolean compareTo(TParm parmA,TParm parmB,int row){
       String names[] = parmA.getNames();
       for(int i = 0;i < names.length;i++){
           if(parmA.getValue(names[i],row).equals(parmB.getValue(names[i],row)))
               continue;
           return false;
       } 
       return true;
   }

   /**
    * 检核画面是否有需要保存的信息
    * @return boolean
    */
   private boolean onSaveCheck(){
       int rowCount = 0;
       TTable tableD = getTTable("DEV_INWAREHOUSED");
       TTable tableDD = getTTable("DEV_INWAREHOUSEDD");
       for(int i = 0;i<tableD.getRowCount();i++){
           if(("" + tableD.getValueAt(i,0)).equals("N")&&
              ("" + tableD.getValueAt(i,3)).length()!=0)
               rowCount++;
       } 
       if(getTTable("RECEIPT").getSelectedRow() < 0 &&
          rowCount == 0){
           messageBox("无保存信息");
           return true; 
       }    
       //fux modify 20130805 
       double contdd = 0;   
       for(int i = 0;i<tableD.getRowCount();i++){
    	   if(tableD.getItemData(i,"SEQMAN_FLG").equals("N"))
    		   continue;    
    	   double contd = tableD.getItemDouble(i, "QTY");
    	   for(int j = 0;j<tableDD.getRowCount();j++){  
      		if ("N".equals(tableDD.getItemData(j, "SELECT_FLG").toString())) {
                continue;
			}       
      		else   
      			contdd++; 
    	   }        
    	   if(contd != contdd){ 
    		   this.messageBox("设备"+tableD.getItemData(i,"DEV_CODE").toString()+"的细表勾选数量和主表出库数量不一致");
            return true;  
    	   } 
       } 
       return false;
   }
   /**
    * 设备入库单打印
    */
   public void onPrintReceipt(){ 
       TTable table = (TTable)getComponent("RECEIPT"); 
       int row = table.getSelectedRow();
       if(row < 0)
           return;
       TParm tableParmD = getTTable("DEV_INWAREHOUSED").getParmValue();
       TParm tableParmDD = getTTable("DEV_INWAREHOUSEDD").getParmValue();
       for(int i = 0;i<tableParmD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmD,tableParmD,i))
               continue;
           messageBox("您对数据进行了修改请保存后再打印");
           return;
       }
       for(int i = 0;i<tableParmDD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmDD,tableParmDD,i))
               continue;
           messageBox("您对数据进行了修改请保存后再打印");
           return;
       }
       TParm printParm = new TParm();
       printParm.setData("HOSP_NAME_T",Operator.getHospitalCHNFullName());
       printParm.setData("INWAREHOUSE_NO_T",table.getValueAt(row,1));
       printParm.setData("MAN_CODE_T",getSupDesc(getValueString("SUP_CODE")));
       printParm.setData("INWAREHOUSE_DEPT_T",getDeptDesc("" + table.getValueAt(row,2)));
       printParm.setData("INWAREHOUSE_DATE_T", ("" + table.getValueAt(row,0)).substring(0,10).replace('-','/'));
       printParm.setData("CONTACT_T",getValue("SUP_BOSSNAME"));
       printParm.setData("INWAREHOUSE_USER_T",getOperatorName("" + table.getValueAt(row,5)));
       printParm.setData("VERIFY_NO_T",table.getValueAt(row,3));
       printParm.setData("TEL_NO_T",getValue("SUP_TEL"));
       TParm tableParm = ((TTable)getComponent("DEV_INWAREHOUSED")).getParmValue();
       TParm parm = new TParm();
       for(int i = 0;i<tableParm.getCount("DEV_CODE");i++){
           cloneTParm(tableParm,parm,i);
       }
       for(int i = 0;i<parm.getCount("DEVPRO_CODE");i++){
           parm.setData("MAN_DATE",i,parm.getValue("MAN_DATE",i).substring(0,10).replace('-','/'));
           parm.setData("GUAREP_DATE",i,parm.getValue("GUAREP_DATE",i).substring(0,10).replace('-','/'));
           parm.setData("DEP_DATE",i,parm.getValue("DEP_DATE",i).substring(0,10).replace('-','/'));
           parm.setData("DEVPRO_CODE",i,getDevProDesc(parm.getValue("DEVPRO_CODE",i)));
       }
       parm.setCount(tableParm.getCount("DEV_CODE"));
       parm.addData("SYSTEM", "COLUMNS", "DEVPRO_CODE");
       parm.addData("SYSTEM", "COLUMNS", "BATCH_SEQ");
       parm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
       parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
       parm.addData("SYSTEM", "COLUMNS", "QTY");
       parm.addData("SYSTEM", "COLUMNS", "TOT_VALUE");
       parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
       parm.addData("SYSTEM", "COLUMNS", "MAN_DATE");
       parm.addData("SYSTEM", "COLUMNS", "LAST_PRICE");
       parm.addData("SYSTEM", "COLUMNS", "GUAREP_DATE");
       parm.addData("SYSTEM", "COLUMNS", "DEP_DATE");
       printParm.setData("RECEIPT",parm.getData());
       openPrintWindow("%ROOT%\\config\\prt\\dev\\DevInStorageReceipt.jhw",printParm);
   }

   /**
    * 取得设备属性中文描述
    * @param devProCode String
    * @return String
    */
   public String getDevProDesc(String devProCode){
       TParm parm = new TParm(getDBTool().select(" SELECT CHN_DESC FROM SYS_DICTIONARY "+
                                                 " WHERE GROUP_ID = 'DEVPRO_CODE'"+
                                                 " AND   ID = '"+devProCode+"'"));
       return parm.getValue("CHN_DESC",0);
    }

   /**
    * 返回数据库操作工具
    * @return TJDODBTool 
    */
   public TJDODBTool getDBTool() {
       return TJDODBTool.getInstance();
   }
   /**
     * 拿到科室
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode){
        TParm parm = new TParm(getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'"));
        return parm.getValue("DEPT_CHN_DESC",0);
    }
    /**
     * 拿到用户名
     * @param userID String
     * @return String
     */
    public String getOperatorName(String userID){
        TParm parm = new TParm(getDBTool().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"+userID+"'"));
        return parm.getValue("USER_NAME",0);
    }
    /**
      * 拿到供应厂商
      * @param deptCode String
      * @return String
      */
     public String getSupDesc(String supCode){
         TParm parm = new TParm(getDBTool().select("SELECT SUP_CHN_DESC FROM SYS_SUPPLIER WHERE SUP_CODE='"+supCode+"'"));
         return parm.getValue("SUP_CHN_DESC",0);
    }
   /**
    * 拿到TTable
    * @param tag String
    * @return TTable 
    */
   public TTable getTTable(String tag){
       return (TTable)getComponent(tag);
    }
    /**
     * 设备录入事件
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateEditComoponent(Component com,int row,int column){
        //设备属性
        String devProCode = ""+getTTable("DEV_INWAREHOUSED").getValueAt(row,1);
        //状态条显示
        callFunction("UI|setSysStatus","");
        //拿到列名
        String columnName = getFactColumnName("DEV_INWAREHOUSED",column);
        if(!"DEV_CODE".equals(columnName))
            return;
        if(!(com instanceof TTextField))
            return;                     
        TTextField textFilter = (TTextField)com;
        textFilter.onInit();
        if(("" +getTTable("DEV_INWAREHOUSED").getValueAt(row,column)).length() != 0)
            return;
        TParm parm = new TParm();
        parm.setData("DEVPRO_CODE",devProCode);
        parm.setData("ACTIVE","Y");
        //设置弹出菜单
        textFilter.setPopupMenuParameter("DEVBASE",getConfigParm().newConfig("%ROOT%\\config\\sys\\DEVBASEPopupUI.x"),parm);
        //定义接受返回值方法
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE,this,"popReturn");
    }

    /**
     * 设备录入返回值事件
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag,Object obj){ 
        //判断对象是否为空和是否为TParm类型
        if (obj == null &&!(obj instanceof TParm))
            return ;
        if(getValueString("INWAREHOUSE_DATE").length() == 0){
            messageBox("请录入入库日期");
            return;
        }
        //类型转换成TParm
        TParm parm = (TParm)obj;
        Timestamp timestamp = StringTool.getTimestamp(SystemTool.getInstance().getDate().toString(),"yyyy-MM-dd");
 
        String inWarehouseDate = getValueString("INWAREHOUSE_DATE");
        //这里计算有问题 
        int year = Integer.parseInt(inWarehouseDate.substring(0, 4)) + parm.getInt("USE_DEADLINE");
        String depDate = year + inWarehouseDate.substring(4,inWarehouseDate.length()); 
        int batchSeq = DevInStorageTool.getInstance().getUseBatchSeq(parm.getValue("DEV_CODE"), 
                                                                   StringTool.getTimestamp(depDate,"yyyy-MM-dd"),
                                                                   timestamp);
        //检核录入设备是否已经存在  
        //int batchSeq = DevInStorageTool.getInstance().getUseBatchSeq(parm.getValue("DEV_CODE"));
        for(int i = 0;i< getTTable("DEV_INWAREHOUSED").getRowCount();i++){
            if (!parm.getValue("DEV_CODE").equals(getTTable("DEV_INWAREHOUSED").getValueAt(i, 2)))
                continue;
            if(batchSeq != Integer.parseInt(""+getTTable("DEV_INWAREHOUSED").getValueAt(i, 3)))
                continue;
            messageBox("此设备已经存在,并已将数量添加到相应列中");
            int qty = 1 + Integer.parseInt("" + getTTable("DEV_INWAREHOUSED").getValueAt(i,7));
            updateTableData("DEV_INWAREHOUSED", i, 7, qty);
            updateTableData("DEV_INWAREHOUSED", i, 12, qty * Double.parseDouble("" + getTTable("DEV_INWAREHOUSED").getValueAt(i,11)));
            updateTableData("DEV_INWAREHOUSED", getTTable("DEV_INWAREHOUSED").getSelectedRow(), 2, "");
            resetDDTableData();
            return;
        }
        callFunction("UI|setSysStatus",parm.getValue("DEV_CODE")+":"+parm.getValue("DEV_CHN_DESC")+parm.getValue("SPECIFICATION"));
        getTTable("DEV_INWAREHOUSED").acceptText();
        TParm tableParm =  new TParm();
        TParm tableParmDD = new TParm();
        tableParm.setData("DEL_FLG","N");
        tableParm.setData("DEVPRO_CODE",parm.getData("DEVPRO_CODE"));
        tableParm.setData("DEV_CODE",parm.getData("DEV_CODE")); 
        tableParm.setData("BATCH_SEQ",batchSeq);
        tableParm.setData("DEV_CHN_DESC",parm.getData("DEV_CHN_DESC"));
        tableParm.setData("SPECIFICATION",parm.getData("SPECIFICATION"));
        tableParm.setData("MAN_CODE",parm.getData("MAN_CODE"));
        tableParm.setData("QTY",1);
        tableParm.setData("SUM_QTY",0);
        tableParm.setData("RECEIPT_QTY",0);
        tableParm.setData("UNIT_CODE",parm.getData("UNIT_CODE"));
        tableParm.setData("UNIT_PRICE",parm.getData("UNIT_PRICE"));
        tableParm.setData("TOT_VALUE",parm.getDouble("UNIT_PRICE"));
        tableParm.setData("MAN_DATE",timestamp);
        tableParm.setData("LAST_PRICE",parm.getData("UNIT_PRICE"));
        tableParm.setData("GUAREP_DATE",timestamp);
        tableParm.setData("DEPR_METHOD",parm.getData("DEPR_METHOD"));
        tableParm.setData("USE_DEADLINE",parm.getData("USE_DEADLINE"));
        tableParm.setData("DEP_DATE",StringTool.getTimestamp(depDate,"yyyy-MM-dd"));
        tableParm.setData("MAN_NATION",parm.getData("MAN_NATION"));
        tableParm.setData("SEQMAN_FLG",parm.getData("SEQMAN_FLG"));
        tableParm.setData("MEASURE_FLG",parm.getData("MEASURE_FLG"));
        tableParm.setData("BENEFIT_FLG",parm.getData("BENEFIT_FLG"));
        tableParm.setData("FILES_WAY","");
        tableParm.setData("VERIFY_NO","");
        tableParm.setData("VERIFY_NO_SEQ","");
        tableParm.setData("INWAREHOUSE_NO","");
        tableParm.setData("SEQ_NO","");
        tableParm.setData("DEVKIND_CODE",parm.getData("DEVKIND_CODE"));
        ((TTable)getComponent("DEV_INWAREHOUSED")).removeRow(((TTable)getComponent("DEV_INWAREHOUSED")).getRowCount()-1);
        ((TTable)getComponent("DEV_INWAREHOUSED")).addRow(tableParm);
        //做序号管理的设备展开序号管理明细
        if("Y".equals(parm.getData("SEQMAN_FLG"))){
            setDevDDTablePop(tableParmDD, tableParm);
            ((TTable)getComponent("DEV_INWAREHOUSEDD")).addRow(tableParmDD);
        }
        addDRow();
        setTableLock();
        ((TTextFormat)getComponent("INWAREHOUSE_DATE")).setEnabled(false);
    }
    /**
     * 重新整理序号管理明细信息 
     */
    public void resetDDTableData(){
       TTable tableD = getTTable("DEV_INWAREHOUSED");
       TParm parmD = tableD.getParmValue();
       TTable tableDD = getTTable("DEV_INWAREHOUSEDD");
       TParm parmDD = new TParm();
       for(int i = 0;i < parmD.getCount("DEV_CODE");i++){
           if(parmD.getValue("BATCH_SEQ",i).length() == 0)
               continue;
           if(!"Y".equals(parmD.getValue("SEQMAN_FLG",i)))
               continue;
           addDevDDTable(parmDD, parmD, i);
       }
       tableDD.setParmValue(parmDD);
   }
   /**
    * 设置设备录入时序号管理明细信息
    * @param tableParmDD TParm
    * @param tableParm TParm
    */ 
   public void setDevDDTablePop(TParm tableParmDD,TParm tableParm){
            tableParmDD.setData("DEL_FLG","N");
            tableParmDD.setData("DEVPRO_CODE",tableParm.getData("DEVPRO_CODE"));
            tableParmDD.setData("BATCH_SEQ",tableParm.getData("BATCH_SEQ"));
            tableParmDD.setData("DEV_CODE",tableParm.getData("DEV_CODE"));
            tableParmDD.setData("DDSEQ_NO",1);
            tableParmDD.setData("DEV_CHN_DESC",tableParm.getData("DEV_CHN_DESC"));
            tableParmDD.setData("MAIN_DEV","");
            tableParmDD.setData("MAN_DATE",tableParm.getData("MAN_DATE"));
            tableParmDD.setData("MAN_SEQ","");  
            tableParmDD.setData("LAST_PRICE",tableParm.getData("LAST_PRICE"));
            tableParmDD.setData("GUAREP_DATE",tableParm.getData("GUAREP_DATE"));
            tableParmDD.setData("DEP_DATE",tableParm.getData("DEP_DATE"));
            tableParmDD.setData("TOT_VALUE",tableParm.getData("UNIT_PRICE"));
            tableParmDD.setData("INWAREHOUSE_NO","");
            tableParmDD.setData("SEQ_NO","");
            tableParmDD.setData("DEVSEQ_NO","");
    }
    /**
     * 得到表格列名
     * @param tableTag String
     * @param column int
     * @return String
     */
    public String getFactColumnName(String tableTag,int column){
        int col = getThisColumnIndex(column);
        return getTTable(tableTag).getDataStoreColumnName(col);
    }

    /**
     * 得到表格列索引
     * @param column int
     * @return int
     */
    public int getThisColumnIndex(int column){
        return getTTable("DEV_INWAREHOUSED").getColumnModel().getColumnIndex(column);
    }

    /** 
     * 账单table监听事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable chargeTable = (TTable) obj;
        chargeTable.acceptText();
        return true;
    }
    
    /**
     * 打开赋码(rfid相关暂时不用)
     */
    public void onAddRfid()
    {
      if ("".equals(getValueString("DEPT"))) {
        messageBox("入库科室不能为空");
        return;
      } 
      TTable tableDD = getTTable("DEV_INWAREHOUSEDD");
      tableDD.acceptText();
      TParm parm = tableDD.getShowParmValue();
      Object result = openDialog("%ROOT%\\config\\dev\\DEVBarcodeAndRFID.x", parm);
      if (result != null) {
        TParm addParm = (TParm)result;  
        if (addParm == null);
        Map map = new HashMap(); 
        String rfid;  
        for (int i = 0; i < addParm.getCount("RFID"); ++i) {
          rfid = addParm.getValue("RFID", i);
          String code = addParm.getValue("ORGIN_CODE", i);
          map.put(rfid, code);
        }
        for (int i = 0; i < tableDD.getRowCount(); ++i) {
          rfid = tableDD.getItemData(i, "RFID").toString();
          tableDD.setItem(i, "ORGIN_CODE", map.get(rfid));
        }
      }
    }
//    //打印版
//    /**
//     * 生成设备条码
//     */
//    public void onBarcodePrint(){
//       if(getTTable("RECEIPT").getSelectedRow() < 0){
//           messageBox("请选择一笔入库单再打印条码");
//           return;
//       }
//       TParm tableParmD = getTTable("DEV_INWAREHOUSED").getParmValue();
//       TParm tableParmDD = getTTable("DEV_INWAREHOUSEDD").getParmValue();
//       for(int i = 0;i<tableParmD.getCount("INWAREHOUSE_NO");i++){
//           if(compareTo(parmD,tableParmD,i))
//               continue;
//           messageBox("您对数据进行了修改请保存后在打印");
//           return;
//       }
//       for(int i = 0;i<tableParmDD.getCount("INWAREHOUSE_NO");i++){
//           if(compareTo(parmDD,tableParmDD,i))
//               continue;
//           messageBox("您对数据进行了修改请保存后在打印");
//           return;
//       }
//       TParm parm = new TParm();
//       parm.setData("DEV_INWAREHOUSED",((TTable)getComponent("DEV_INWAREHOUSED")).getParmValue().getData());
//       parm.setData("DEV_INWAREHOUSEDD",((TTable)getComponent("DEV_INWAREHOUSEDD")).getParmValue().getData());
//       parm.setData("INWAREHOUSE_NO",getValue("INWAREHOUSE_NO_QUARY"));
//       parm.setData("INWAREHOUSE_DATE",getValue("INWAREHOUSE_DATE"));
//       parm.setData("INWAREHOUSE_DEPT",getValue("DEPT"));
//       parm.setData("INWAREHOUSE_USER",getValue("OPERATOR"));
//       openDialog("%ROOT%\\config\\dev\\DEVBarcodeUI.x", parm);
//   }
    /**
     * 打印条码(加入界面)
     */
    //fux need modify 打印一个条码
    public void onPrintBarcode()   
    {
      if (getTTable("DEV_INWAREHOUSEDD").getRowCount() <= 0) {
        messageBox("请选中明细项");
      }
      TParm parm = getTTable("DEV_INWAREHOUSEDD").getParmValue();

      for (int i = 0; i < getTTable("DEV_INWAREHOUSEDD").getRowCount(); ++i) {
        TParm newParm = new TParm(); 
        //SELECT_FLG;PRINT_FLG
        if (!("Y".equals(getTTable("DEV_INWAREHOUSEDD").getItemData(i, "SELECT_FLG")))) {
            continue; 
          } 
        if (!("Y".equals(getTTable("DEV_INWAREHOUSEDD").getItemData(i, "PRINT_FLG")))) {
          continue;
        }     
        newParm.setData("P_CODE", parm.getData("DEV_CODE", i).toString().trim());
        newParm.setData("P_NAME", parm.getData("DEV_CHN_DESC", i));
        newParm.setData("P_BARCODE", parm.getData("BARCODE", i).toString().trim()); 
        openPrintWindow("%ROOT%\\config\\prt\\dev\\DevBarcode.jhw",newParm);
        try {     
          Thread.sleep(2000L); 
        } 
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      } 
    }
}
