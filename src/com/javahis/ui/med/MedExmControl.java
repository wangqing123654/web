package com.javahis.ui.med;

import com.dongyang.control.*;

import jdo.reg.PatAdmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import jdo.sys.PatTool;
import com.dongyang.data.TParm;
import com.javahis.util.DateUtil;
import com.javahis.util.OdiUtil;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;

import jdo.med.ExecRecordDataStore;
import com.dongyang.ui.TTableNode;
import com.javahis.util.StringUtil;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MedExmControl
    extends TControl {
    /**
     * TABLE
     */
    private static String TABLE1="TABLE1";
    TTextFormat execDeptCode;// �ϱ����� add by wanglong 20131127
    /**
     * TABLE
     */
    private static String TABLE2="TABLE2";
    private String type ="";
    
    private TCheckBox cIsExinv;
    private TTextFormat cIndOrg;
    /**
     * ��ʼ������
     */
    public void onInitParameter(){
        this.setPopedem("deptEnabled", true);
    }

    public void onInit() {
        super.onInit();
        onInitPopeDem();
        onPageInit();
        if (type!=null &&(type.equals("OPO")||type.equals("OPE")||type.equals("OPI"))) {//====pangben 2014-3-20 ��������
        	this.setTableHeaderForTRT();
        }
        cIsExinv = (TCheckBox) getComponent("IS_EXINV");
        cIndOrg = (TTextFormat) getComponent("IND_ORG");
    }
    /**
     * ����ִ�����ñ�ͷ
     * yanjing  20141225 
     */
    
    private void setTableHeaderForTRT(){
    	this.getTTable(TABLE1).setHeader("ִ,30,boolean;�ż�ס��,80,ADM_TYPE;������,100;����,80;�Ա�,60,SEX_CODE;��Ŀ����,160;����ʱ��,150,Timestamp,yyyy/MM/dd HH:mm:ss;���,80,double;����,80;��ע,160;����״̬,90;ִ��״̬,90;����ʱ��,120,Timestamp,yyyy/MM/dd HH:mm");
    	this.getTTable(TABLE1).setParmMap("FLG;ADM_TYPE;MR_NO;PAT_NAME;SEX_CODE;ORDER_DESC;ORDER_DATE;AR_AMT;DISPENSE_QTY;DR_NOTE;STATUS_TYPE;EXE_FLG;EXEC_DATE;DEV_CODE;EXEC_FLG;EXEC_DEPT_CODE");
    	this.getTTable(TABLE1).setColumnHorizontalAlignmentData("1,left;2,left;4,left;5,left;6,left;7,right;8,right;9,left;10,left;11,left;12,left");
    	this.getTTable(TABLE1).setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12");
    }
    /**
     * ��ҩ�����ñ�ͷ
     * yanjing  20141225 
     */
    
    private void setTableHeaderForEXINV(){
    	this.getTTable(TABLE1).setHeader("ִ,30,boolean;�ż�ס��,80,ADM_TYPE;������,100;����,80;�Ա�,60,SEX_CODE;��Ŀ����,160;�÷�,80;����,60;Ƶ��,80;����ʱ��,150,Timestamp,yyyy/MM/dd HH:mm:ss;���,80,double;��ע,160;����״̬,90;ִ��״̬,90;����ʱ��,120,Timestamp,yyyy/MM/dd HH:mm");
    	this.getTTable(TABLE1).setParmMap("FLG;ADM_TYPE;MR_NO;PAT_NAME;SEX_CODE;ORDER_DESC;ROUTE_CODE;DISPENSE_QTY;FREQ_CODE;ORDER_DATE;AR_AMT;DR_NOTE;STATUS_TYPE;EXE_FLG;EXEC_DATE;DEV_CODE;EXEC_FLG;EXEC_DEPT_CODE");
    	this.getTTable(TABLE1).setColumnHorizontalAlignmentData("1,left;2,left;4,left;5,left;6,left;7,right;8,left;9,left;10,right;11,left;12,left;13,left;14,left");
    	this.getTTable(TABLE1).setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12,13,14");
    }
    /**
     * ҳ���ʼ��
     */
    public void onPageInit(){
    	this.setValue("CLINICAREA_CODE", Operator.getStation());
    	execDeptCode  = (TTextFormat) this.getComponent("EXEC_DEPT_CODE");// ִ�п���
    	 this.onSelectDept();
    	 String sysDate = SystemTool.getInstance().getDate().toString();
    	 sysDate=sysDate.substring(0, 4)+"/"+sysDate.substring(5, 7)+ "/"+sysDate.substring(8, 10);
        //Ĭ��������ʼ����"
        this.setValue("START_DATE",sysDate+ " 00:00:00");
        //Ĭ��������ֹ����
        this.setValue("END_DATE",sysDate+" 23:59:59");
//        this.setValue("EXEC_DEPT_CODE", this.setExecDeptCode());//ִ�п���
        this.setValue("CLINICAREA_CODE", Operator.getStation());
        //TABLE1˫���¼�
        callFunction("UI|" + TABLE1 + "|addEventListener",TABLE1 + "->" + TTableEvent.CLICKED, this, "onTableClicked");
        //TABLE2ֵ�ı��¼�
        addEventListener(TABLE2+"->"+TTableEvent.CHANGE_VALUE, this,"onChangeTableValue");
        //TABLE2COMBOXֵ�ı��¼�
        getTTable(TABLE2).addEventListener(TTableEvent.CHECK_BOX_CLICKED,this,"onCheckBoxValue");
//        //TABLE1COMBOXֵ�ı��¼�
//        getTTable(TABLE1).addEventListener(TTableEvent.CHECK_BOX_CLICKED,this,"onCheckBoxValue1");

    }
    /**
     * ����ѡ���¼�
     */
    public void onSelectDept(){
    	execDeptCode  = (TTextFormat) this.getComponent("EXEC_DEPT_CODE");// ִ�п���
//    	 String deptSql = // add by wanglong 20131127
//             "SELECT DISTINCT A.DEPT_CODE AS ID, A.DEPT_ABS_DESC AS NAME, A.PY1,A.SEQ FROM SYS_DEPT A, REG_CLINICROOM B "
//                     + " WHERE B.MED_EXEC_DEPT = A.DEPT_CODE AND B.CLINICAREA_CODE = '#' AND A.ACTIVE_FLG = 'Y' ORDER BY A.DEPT_CODE, A.SEQ";
    	 String clinicAreaCode = "";
    	 if (!(this.getValue("CLINICAREA_CODE").equals(null))&&!"".equals(this.getValue("CLINICAREA_CODE"))
  			   &&this.getValue("CLINICAREA_CODE").toString().length()>0) {
    		 clinicAreaCode = " AND B.CLINICAREA_CODE = '"+this.getValueString("CLINICAREA_CODE")+"'";
    	 }
    	 String deptSql = // add by wanglong 20131127
             "SELECT DISTINCT A.COST_CENTER_CODE AS ID, A.COST_CENTER_ABS_DESC AS NAME, A.PY1,A.SEQ FROM SYS_COST_CENTER A, REG_CLINICROOM B "
                     + " WHERE B.MED_EXEC_DEPT = A.COST_CENTER_CODE "+clinicAreaCode+" AND A.ACTIVE_FLG = 'Y' ORDER BY A.COST_CENTER_CODE, A.SEQ";
//          deptSql = deptSql.replaceFirst("#", this.getValueString("CLINICAREA_CODE"));
         execDeptCode.setPopupMenuSQL(deptSql);
         execDeptCode.setModifySQL(true);
         if ((this.getValue("CLINICAREA_CODE").equals(null))||"".equals(this.getValue("CLINICAREA_CODE"))){
        	 this.setValue("EXEC_DEPT_CODE", "");
         }
    }
    /**
     * �һ�MENU�����¼�
     * @param tableName
     */
    public void showPopMenu(String tableName){
       int selRow = this.getTTable(tableName).getSelectedRow();
       TParm orderP = this.getTTable(tableName).getParmValue().getRow(selRow);
        if ("Y".equals(orderP.getValue("SETMAIN_FLG"))) {
            this.getTTable(tableName).setPopupMenuSyntax("��ʾ����ҽ��ϸ��,openRigthPopMenu");
            return;
        }
        if ("N".equals(orderP.getValue("SETMAIN_FLG"))) {
            this.getTTable(tableName).setPopupMenuSyntax("");
            return;
        }
    }
    /**
     * �򿪼���ҽ��ϸ���ѯ
     */
    public void openRigthPopMenu(){
        int selRow = this.getTTable(TABLE1).getSelectedRow();
        TParm orderP = this.getTTable(TABLE1).getParmValue().getRow(selRow);
        int groupNo = orderP.getInt("ORDERSET_GROUP_NO");
        String orderCode = orderP.getValue("ORDER_CODE");
        String caseNo = orderP.getValue("CASE_NO");
        String orderNo = orderP.getValue("ORDER_NO");
        String admType = orderP.getValue("ADM_TYPE");
        TParm parm = getOrderSetDetails(admType,groupNo, orderCode,orderNo,caseNo);
        this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
    }
    /**
         * ���ؼ���ҽ��ϸ���TParm��ʽ
         * @return result TParm
         */
        public TParm getOrderSetDetails(String admType,int groupNo, String orderSetCode,String orderNo,String caseNo) {
            TParm result = new TParm();
            if (groupNo < 0) {
                // System.out.println(
                //    "OpdOrder->getOrderSetDetails->groupNo is invalie");
                return result;
            }
            if (StringUtil.isNullString(orderSetCode)) {
                // System.out.println(
              //      "OpdOrder->getOrderSetDetails->orderSetCode is invalie");
                return result;
            }
            TParm parm = new TParm();
            if("O".equals(admType)){
              parm = new TParm(this.getDBTool().select("SELECT * FROM OPD_ORDER WHERE CASE_NO='"+caseNo+"' AND RX_NO='"+orderNo+"' AND ORDERSET_CODE='"+orderSetCode+"' AND ORDERSET_GROUP_NO='"+groupNo+"'"));
            }
            if("I".equals(admType)){
              parm = new TParm(this.getDBTool().select("SELECT * FROM ODI_ORDER WHERE CASE_NO='"+caseNo+"' AND ORDER_NO='"+orderNo+"' AND ORDERSET_CODE='"+orderSetCode+"' AND ORDERSET_GROUP_NO='"+groupNo+"'"));
            }

            int count = parm.getCount();
            if (count < 0) {
                return result;
            }
            String tempCode;
            int tempNo;
            //temperrϸ��۸�
            for (int i = 0; i < count; i++) {
                tempCode = parm.getValue("ORDERSET_CODE", i);
                tempNo = parm.getInt("ORDERSET_GROUP_NO", i);
//            // System.out.println("tempCode==========" + tempCode);
//            // System.out.println("tempNO============" + tempNo);
//            // System.out.println("setmain_flg========" + parm.getBoolean("SETMAIN_FLG", i));
                if (tempCode.equalsIgnoreCase(orderSetCode) && tempNo == groupNo &&
                    !parm.getBoolean("SETMAIN_FLG", i)) {
                    //ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
                    result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
                    result.addData("SPECIFICATION",
                                   parm.getValue("SPECIFICATION", i));
                    result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
                    result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
                    //��ѯ����
                    TParm ownPriceParm = new TParm(this.getDBTool().select("SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE='"+parm.getValue("ORDER_CODE", i)+"'"));
//                this.messageBox_(ownPriceParm);
                    //�����ܼ۸�
                    double ownPrice = ownPriceParm.getDouble("OWN_PRICE",0)* parm.getDouble("MEDI_QTY", i);
                    result.addData("OWN_PRICE", ownPriceParm.getDouble("OWN_PRICE",0));
                    result.addData("OWN_AMT", ownPrice);
                    result.addData("EXEC_DEPT_CODE",
                                   parm.getValue("EXEC_DEPT_CODE", i));
                    result.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE", i));
                    result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
                }
            }
            return result;
    }

    /**
     * ��ʱҽ���޸��¼�����
     * @param obj Object
     */
    public boolean onChangeTableValue(Object obj){
        //�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
        TTableNode node = (TTableNode) obj;
        if (node == null)
           return true;
       //����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
       // System.out.println("old=="+node.getOldValue());
       // System.out.println("new=="+node.getValue());
       if (node.getValue()==null||node.getValue().equals(node.getOldValue()))
           return true;
       //�õ�table�ϵ�parmmap������
       String columnName = node.getTable().getDataStoreColumnName(node.getColumn());
       //�жϵ�ǰ���Ƿ���ҽ��
       int selRow = node.getRow();
       TParm orderP = this.getTTable(TABLE2).getDataStore().getRowParm(selRow);
       int id = (Integer)this.getTTable(TABLE2).getDataStore().getItemData(selRow,"#ID#");
       //EXEC_DATE;EXEC_STATUS;EXEC_USER;DEVICE_ID;REMARK
//       this.messageBox_(node.getValue()+"=="+columnName);
       if("Y".equals(orderP.getValue("SETMAIN_FLG"))){
           String buff = this.getTTable(TABLE2).getDataStore().isFilter()?this.getTTable(TABLE2).getDataStore().FILTER:this.getTTable(TABLE2).getDataStore().PRIMARY;
           int rowCount = this.getTTable(TABLE2).getDataStore().getBuffer(buff).getCount();
//           this.messageBox_(rowCount);
           for(int i=0;i<rowCount;i++){
               if((Integer)this.getTTable(TABLE2).getDataStore().getItemData(i,"#ID#",buff)!=id){
                   // System.out.println(orderP.getValue("EXEC_NO"));
                   // System.out.println(this.getTTable(TABLE2).getDataStore().getItemData(i,"EXEC_NO",buff).toString());
                   // System.out.println(orderP.getValue("EXEC_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"EXEC_NO",buff).toString()));
                   if(orderP.getValue("EXEC_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"EXEC_NO",buff).toString())&&orderP.getValue("MR_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"MR_NO",buff).toString())&&orderP.getValue("CASE_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"CASE_NO",buff).toString())&&orderP.getValue("RX_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"RX_NO",buff).toString())&&orderP.getValue("ORDERSET_CODE").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"ORDERSET_CODE",buff).toString())&&orderP.getValue("ORDERSET_GROUP_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"ORDERSET_GROUP_NO",buff).toString())){
                       this.getTTable(TABLE2).getDataStore().setItem(i,columnName,node.getValue(),buff);
                   }
               }
           }
       }
       // System.out.println(this.getTTable(TABLE2).getDataStore().getRowParm(selRow));
       return false;
   }
    public void onCheckBoxValue(Object obj){
        TTable table = (TTable)obj;
        table.acceptText();
        int col = table.getSelectedColumn();
        String columnName = this.getTTable(TABLE2).getDataStoreColumnName(col);
        int row = table.getSelectedRow();
        TParm linkParm = table.getDataStore().getRowParm(row);
        int id = (Integer)this.getTTable(TABLE2).getDataStore().getItemData(row,"#ID#");
        String buff = this.getTTable(TABLE2).getDataStore().isFilter()?this.getTTable(TABLE2).getDataStore().FILTER:this.getTTable(TABLE2).getDataStore().PRIMARY;
        //EXEC_DATE;EXEC_STATUS;EXEC_USER;DEVICE_ID;REMARK
        if("Y".equals(linkParm.getValue("SETMAIN_FLG"))){
            if("EXEC_STATUS".equals(columnName)){
               int rowCount = this.getTTable(TABLE2).getDataStore().getBuffer(buff).getCount();
               for(int i=0;i<rowCount;i++){
                   if((Integer)this.getTTable(TABLE2).getDataStore().getItemData(i,"#ID#",buff)!=id){
                       if(linkParm.getValue("EXEC_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"EXEC_NO",buff).toString())
                    		   &&linkParm.getValue("MR_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"MR_NO",buff).toString())
                    		   &&linkParm.getValue("CASE_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"CASE_NO",buff).toString())
                    		   &&linkParm.getValue("RX_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"RX_NO",buff).toString())
                    		   &&linkParm.getValue("ORDERSET_CODE").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"ORDERSET_CODE",buff).toString())
                    		   &&linkParm.getValue("ORDERSET_GROUP_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"ORDERSET_GROUP_NO",buff).toString())){
                           this.getTTable(TABLE2).getDataStore().setItem(i,columnName,linkParm.getValue("EXEC_STATUS"),buff);
                           if("Y".equals(linkParm.getValue("EXEC_STATUS"))){
                               this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_USER",Operator.getID(),buff);
                               this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_DATE",SystemTool.getInstance().getDate(),buff);
                               this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_DEPT",this.getValue("EXEC_DEPT_CODE"),buff);
                           }else{
                               this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_USER","",buff);
                               this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_DATE",null,buff);
                               this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_DEPT",this.getValue("EXEC_DEPT_CODE"),buff);
                           }
                       }
                   }else{
                       if("Y".equals(linkParm.getValue("EXEC_STATUS"))){
                           this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_USER",Operator.getID(),buff);
                           this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_DATE",SystemTool.getInstance().getDate(),buff);
                           this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_DEPT",this.getValue("EXEC_DEPT_CODE"),buff);
                       }else{
                           this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_USER","",buff);
                           this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_DATE",null,buff);
                           this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_DEPT",this.getValue("EXEC_DEPT_CODE"),buff);
                       }
                   }
               }
            }
            this.getTTable(TABLE2).setDSValue();
        }else{
            if ("Y".equals(linkParm.getValue("EXEC_STATUS"))) {
                this.getTTable(TABLE2).getDataStore().setItem(row, "EXEC_USER",
                    Operator.getID());
                this.getTTable(TABLE2).getDataStore().setItem(row, "EXEC_DATE",
                    SystemTool.getInstance().getDate());
            }else {
                this.getTTable(TABLE2).getDataStore().setItem(row, "EXEC_USER",
                    "");
                this.getTTable(TABLE2).getDataStore().setItem(row, "EXEC_DATE",
                    null);
            }
            this.getTTable(TABLE2).setDSValue();
        }
    }

    /**
    * ��ʼ��Ȩ��
    */
   public void onInitPopeDem() {
       //Ȩ�޿ɷ�ѡ�����
       if (!this.getPopedem("deptEnabled")) {
           this.callFunction("UI|DEPT_CODE|setEnabled", false);
       }
       type = (String) this.getParameter();
       isExeOp();
   }
   /**
    * ��������
    * pangben 2014-3-20 
    */
   private void isExeOp(){
	   if (type!=null &&type.equals("OPO")) {
    	   this.setTitle("��������ִ��");
    	   this.callFunction("UI|ADM_TYPEQ|setEnabled", false);
    	   this.setValue("ADM_TYPEQ", "O");
    	   ((TPanel)this.getComponent("APanel")).setBorder("��|��������");
    	   ((TPanel)this.getComponent("BPanel")).setBorder("��|����ִ��");
       }else if(type!=null &&type.equals("OPE")){
    	   this.setTitle("��������ִ��");
    	   this.callFunction("UI|ADM_TYPEQ|setEnabled", false);
    	   this.setValue("ADM_TYPEQ", "E");
    	   ((TPanel)this.getComponent("APanel")).setBorder("��|��������");
    	   ((TPanel)this.getComponent("BPanel")).setBorder("��|����ִ��");
       }else if(type!=null &&type.equals("OPI")){
    	   this.setTitle("סԺ����ִ��");
    	   this.callFunction("UI|ADM_TYPEQ|setEnabled", false);
    	   this.setValue("ADM_TYPEQ", "I");
    	   ((TPanel)this.getComponent("APanel")).setBorder("��|��������");
    	   ((TPanel)this.getComponent("BPanel")).setBorder("��|����ִ��");
       }else{
    	   this.callFunction("UI|ADM_TYPEQ|setEnabled", true);
       }
   }
   /**
     * �õ�TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }

   /**
    * �����Ų�ѯ�¼�
    */
   public void onQMrNo(){
       Timestamp sysDate = SystemTool.getInstance().getDate();
       this.setValue("MR_NO",PatTool.getInstance().checkMrno(this.getValueString("MR_NO")));
       this.setValue("PAT_NAME",PatTool.getInstance().getNameForMrno(PatTool.getInstance().checkMrno(this.getValueString("MR_NO"))));
       TParm patParm = PatTool.getInstance().getInfoForMrno(this.getValueString("MR_NO"));
       Timestamp temp = patParm.getTimestamp("BIRTH_DATE", 0) == null ? sysDate : patParm.getTimestamp("BIRTH_DATE", 0);
       this.setValue("SEX_CODE", patParm.getValue("SEX_CODE", 0));
//       this.setValue("AGE", OdiUtil.getInstance().showAge(temp, sysDate));
       this.setValue("AGE", DateUtil.showAge(temp, sysDate));
       
       if(this.getValueString("ADM_TYPEQ").length()==0){
          this.messageBox("�����벡����Դ��");
          return;
      }
       boolean ektFlg = true;
      this.onQuery(true);
   }
   /**
    * TABLE1����¼�
    * @param row int
    */
   public void onTableClicked(int row){
       TParm rowParm = this.getTTable(TABLE1).getParmValue().getRow(row);
       ExecRecordDataStore execRecord = new ExecRecordDataStore();
       execRecord.setMrNo(rowParm.getValue("MR_NO"));
       execRecord.setCaseNo(rowParm.getValue("CASE_NO"));
       execRecord.setRxNo(rowParm.getValue("ORDER_NO"));
       execRecord.setSeqNo(rowParm.getValue("SEQ_NO"));
       execRecord.setOrderCode(rowParm.getValue("ORDER_CODE"));
       execRecord.setOrderSetCode(rowParm.getValue("ORDERSET_CODE"));
       execRecord.setOrderSetGroupNo(rowParm.getValue("ORDERSET_GROUP_NO"));
       execRecord.onQuery(type);
       
       if(execRecord.rowCount()>0){
           this.getTTable(TABLE2).setDataStore(execRecord);
           this.getTTable(TABLE2).setFilter("HIDE_FLG='N'");
//           this.getTTable(TABLE2).setSort("EXEC_NO");
           this.getTTable(TABLE2).filter();
//           this.getTTable(TABLE2).sort();
           this.getTTable(TABLE2).setDSValue();
           return;
       }
//       System.out.println("��������� sql sql is����"+rowParm);
    	 //���ݾ���Ų�ѯ������ִ�п���
    	   String selectSql = "SELECT A.CLINICAREA_CODE,B.MED_EXEC_DEPT FROM REG_PATADM A ,REG_CLINICROOM B " +
    	   		"WHERE A.CLINICAREA_CODE = B.CLINICAREA_CODE AND A.CLINICROOM_NO = B.CLINICROOM_NO AND A.CASE_NO = '"+rowParm.getValue("CASE_NO")+"' ";
//    	   System.out.println("��������� sql sql is����"+selectSql);
    	   TParm parm = new TParm(getDBTool().select(selectSql));
    	   this.setValue("CLINICAREA_CODE", parm.getValue("CLINICAREA_CODE",0));
    	   this.onSelectDept();
    	   this.setValue("EXEC_DEPT_CODE", parm.getValue("MED_EXEC_DEPT",0));
       this.getTTable(TABLE2).setDataStore(insertRowData(rowParm));
       this.getTTable(TABLE2).setFilter("HIDE_FLG = 'N'");
//       this.getTTable(TABLE2).setSort("EXEC_NO");
       this.getTTable(TABLE2).filter();
//       this.getTTable(TABLE2).sort();
       this.getTTable(TABLE2).setDSValue();
   }
   /**
    * ��������
    * @param rowParm TParm
    * @return ExecRecordDataStore
    */
   public ExecRecordDataStore insertRowData(TParm rowParm){
       ExecRecordDataStore execRecord = new ExecRecordDataStore();
       execRecord.onQuery(type);
       int addRowCount = (int) Math.ceil(rowParm.getDouble("DISPENSE_QTY"));
       TParm maxParm = new TParm(this.getDBTool().select("SELECT NVL(MAX(TO_NUMBER(EXEC_NO))+1,1) AS EXEC_NO FROM EXM_EXEC_RECORD WHERE CASE_NO='"+rowParm.getValue("CASE_NO")+"' AND RX_NO='"+rowParm.getValue("ORDER_NO")+"' AND ORDER_CODE='"+rowParm.getValue("ORDER_NO")+"'"));
       int maxNo=maxParm.getInt("EXEC_NO",0);
       for(int i=0;i<addRowCount;i++){
           if("O".equals(rowParm.getValue("ADM_TYPE"))||"E".equals(rowParm.getValue("ADM_TYPE"))){
               if("Y".equals(rowParm.getValue("SETMAIN_FLG"))){
                   int rowId = execRecord.insertRow();
                   execRecord.setItem(rowId,"MR_NO",rowParm.getValue("MR_NO"));
                   execRecord.setItem(rowId,"CASE_NO",rowParm.getValue("CASE_NO"));
                   execRecord.setItem(rowId,"RX_NO",rowParm.getValue("ORDER_NO"));
                   execRecord.setItem(rowId,"SEQ_NO",rowParm.getValue("SEQ_NO"));
                   execRecord.setItem(rowId,"SETMAIN_FLG",rowParm.getValue("SETMAIN_FLG"));
                   execRecord.setItem(rowId,"HIDE_FLG",rowParm.getValue("HIDE_FLG"));
                   execRecord.setItem(rowId,"ORDERSET_GROUP_NO",rowParm.getValue("ORDERSET_GROUP_NO"));
                   execRecord.setItem(rowId,"ORDERSET_CODE",rowParm.getValue("ORDERSET_CODE"));
                   execRecord.setItem(rowId,"ORDER_DESC",rowParm.getValue("ORDER_DESC"));
                   execRecord.setItem(rowId,"ORDER_CODE",rowParm.getValue("ORDER_CODE"));
                   execRecord.setItem(rowId,"EXEC_NO",maxNo);
                   execRecord.setItem(rowId,"BOOKING_DATE","");
                   execRecord.setItem(rowId,"EXEC_STATUS","N");
                   if(type.equals("OPO")||type.equals("OPE")){
//                	   execRecord.setItem(rowId,"EXEC_DEPT",rowParm.getValue("EXEC_DEPT_CODE"));
                	   execRecord.setItem(rowId,"EXEC_DEPT",this.getValue("EXEC_DEPT_CODE"));
                   }else{
                	   execRecord.setItem(rowId,"EXEC_DEPT",Operator.getDept());
                   }
//                   execRecord.setItem(rowId,"EXEC_DEPT","");
//                   execRecord.setItem(rowId,"EXEC_DEPT",Operator.getDept());
                   execRecord.setItem(rowId,"EXEC_DATE","");
                   execRecord.setItem(rowId,"EXEC_USER","");
                   execRecord.setItem(rowId,"DEVICE_ID",rowParm.getValue("DEV_CODE"));
                   execRecord.setItem(rowId,"REMARK",rowParm.getValue("DR_NOTE"));
                   //��ϸ��
                   TParm itemParm = new TParm(this.getDBTool().select("SELECT A.ADM_TYPE,A.MR_NO,A.ORDER_DESC,A.ORDER_DATE,A.DISPENSE_QTY,A.AR_AMT," +
                   		"A.DR_NOTE,A.DEV_CODE,A.ORDERSET_GROUP_NO,A.ORDER_CODE,A.RX_NO AS ORDER_NO,A.SETMAIN_FLG,A.SEQ_NO,A.CASE_NO,A.HIDE_FLG," +
                   		"A.ORDERSET_CODE FROM OPD_ORDER A WHERE CASE_NO='"+rowParm.getValue("CASE_NO")+"' AND RX_NO='"+rowParm.getValue("ORDER_NO")+"' " +
                   				"AND ORDERSET_GROUP_NO='"+rowParm.getValue("ORDERSET_GROUP_NO")+"' AND ORDERSET_CODE='"+rowParm.getValue("ORDER_CODE")+"' " +
                   						"AND SETMAIN_FLG='N'"));
                   int rowCount = itemParm.getCount();
                   for(int j=0;j<rowCount;j++){
                       TParm temp = itemParm.getRow(j);
                       int rowIds = execRecord.insertRow();
                       execRecord.setItem(rowIds,"MR_NO",temp.getValue("MR_NO"));
                       execRecord.setItem(rowIds,"CASE_NO",temp.getValue("CASE_NO"));
                       execRecord.setItem(rowIds,"RX_NO",temp.getValue("ORDER_NO"));
                       execRecord.setItem(rowIds,"SEQ_NO",temp.getValue("SEQ_NO"));
                       execRecord.setItem(rowIds,"SETMAIN_FLG",temp.getValue("SETMAIN_FLG"));
                       execRecord.setItem(rowIds,"HIDE_FLG",temp.getValue("HIDE_FLG"));
                       execRecord.setItem(rowIds,"ORDERSET_GROUP_NO",temp.getValue("ORDERSET_GROUP_NO"));
                       execRecord.setItem(rowIds,"ORDERSET_CODE",temp.getValue("ORDERSET_CODE"));
                       execRecord.setItem(rowIds,"ORDER_DESC",temp.getValue("ORDER_DESC"));
                       execRecord.setItem(rowIds,"ORDER_CODE",temp.getValue("ORDER_CODE"));
                       execRecord.setItem(rowIds,"EXEC_NO",maxNo);
                       execRecord.setItem(rowIds,"BOOKING_DATE","");
                       execRecord.setItem(rowIds,"EXEC_STATUS","N");
                       if(type.equals("OPO")||type.equals("OPE")){
//                    	   execRecord.setItem(rowIds,"EXEC_DEPT",rowParm.getValue("EXEC_DEPT_CODE"));
                    	   execRecord.setItem(rowIds,"EXEC_DEPT",this.getValue("EXEC_DEPT_CODE"));
                       }else{
                    	   execRecord.setItem(rowIds,"EXEC_DEPT",Operator.getDept());
                       }
//                       execRecord.setItem(rowIds,"EXEC_DEPT","");
//                       execRecord.setItem(rowIds,"EXEC_DEPT",Operator.getDept());
                       execRecord.setItem(rowIds,"EXEC_DATE","");
                       execRecord.setItem(rowIds,"EXEC_USER","");
                       execRecord.setItem(rowIds,"DEVICE_ID",rowParm.getValue("DEV_CODE"));
                       execRecord.setItem(rowIds,"REMARK",temp.getValue("DR_NOTE"));
                   }
                   maxNo++;
               }else{
                   int rowId = execRecord.insertRow();
                   execRecord.setItem(rowId,"MR_NO",rowParm.getValue("MR_NO"));
                   execRecord.setItem(rowId,"CASE_NO",rowParm.getValue("CASE_NO"));
                   execRecord.setItem(rowId,"RX_NO",rowParm.getValue("ORDER_NO"));
                   execRecord.setItem(rowId,"SEQ_NO",rowParm.getValue("SEQ_NO"));
                   execRecord.setItem(rowId,"SETMAIN_FLG",rowParm.getValue("SETMAIN_FLG"));
                   execRecord.setItem(rowId,"HIDE_FLG",rowParm.getValue("HIDE_FLG"));
                   execRecord.setItem(rowId,"ORDERSET_GROUP_NO",rowParm.getValue("ORDERSET_GROUP_NO"));
                   execRecord.setItem(rowId,"ORDERSET_CODE",rowParm.getValue("ORDERSET_CODE"));
                   execRecord.setItem(rowId,"ORDER_DESC",rowParm.getValue("ORDER_DESC"));
                   execRecord.setItem(rowId,"ORDER_CODE",rowParm.getValue("ORDER_CODE"));
                   execRecord.setItem(rowId,"EXEC_NO",maxNo);
                   execRecord.setItem(rowId,"BOOKING_DATE","");
                   execRecord.setItem(rowId,"EXEC_STATUS","N");
//                   execRecord.setItem(rowId,"EXEC_DEPT","");
//                   execRecord.setItem(rowId,"EXEC_DEPT",Operator.getDept());
                   if(type.equals("OPO")||type.equals("OPE")){
//                	   execRecord.setItem(rowId,"EXEC_DEPT",rowParm.getValue("EXEC_DEPT_CODE"));
                	   execRecord.setItem(rowId,"EXEC_DEPT",this.getValue("EXEC_DEPT_CODE"));
                   }else{
                	   execRecord.setItem(rowId,"EXEC_DEPT",Operator.getDept());
                   }
                   execRecord.setItem(rowId,"EXEC_DATE","");
                   execRecord.setItem(rowId,"EXEC_USER","");
                   execRecord.setItem(rowId,"DEVICE_ID",rowParm.getValue("DEV_CODE"));
                   execRecord.setItem(rowId,"REMARK",rowParm.getValue("DR_NOTE"));
                   maxNo++;
               }
           }
           if("I".equals(rowParm.getValue("ADM_TYPE"))){
               if("Y".equals(rowParm.getValue("SETMAIN_FLG"))){
                   int rowId = execRecord.insertRow();
                   execRecord.setItem(rowId,"MR_NO",rowParm.getValue("MR_NO"));
                   execRecord.setItem(rowId,"CASE_NO",rowParm.getValue("CASE_NO"));
                   execRecord.setItem(rowId,"RX_NO",rowParm.getValue("ORDER_NO"));
                   execRecord.setItem(rowId,"SEQ_NO",rowParm.getValue("SEQ_NO"));
                   execRecord.setItem(rowId,"SETMAIN_FLG",rowParm.getValue("SETMAIN_FLG"));
                   execRecord.setItem(rowId,"HIDE_FLG",rowParm.getValue("HIDE_FLG"));
                   execRecord.setItem(rowId,"ORDERSET_GROUP_NO",rowParm.getValue("ORDERSET_GROUP_NO"));
                   execRecord.setItem(rowId,"ORDERSET_CODE",rowParm.getValue("ORDERSET_CODE"));
                   execRecord.setItem(rowId,"ORDER_DESC",rowParm.getValue("ORDER_DESC"));
                   execRecord.setItem(rowId,"ORDER_CODE",rowParm.getValue("ORDER_CODE"));
                   execRecord.setItem(rowId,"EXEC_NO",maxNo);
                   execRecord.setItem(rowId,"BOOKING_DATE","");
                   execRecord.setItem(rowId,"EXEC_STATUS","N");
                   execRecord.setItem(rowId,"EXEC_DEPT",Operator.getDept());
                   execRecord.setItem(rowId,"EXEC_DATE","");
                   execRecord.setItem(rowId,"EXEC_USER","");
                   execRecord.setItem(rowId,"DEVICE_ID",rowParm.getValue("DEV_CODE"));
                   execRecord.setItem(rowId,"REMARK",rowParm.getValue("DR_NOTE"));
                   //��ϸ��
                   TParm itemParm = new TParm(this.getDBTool().select("SELECT 'I' AS ADM_TYPE,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.ORDER_DESC,A.ORDER_DATE,A.DISPENSE_QTY,'' AS AR_AMT,A.DR_NOTE,A.DEV_CODE,A.NS_CHECK_DATE,A.ORDERSET_GROUP_NO,A.ORDER_CODE,A.ORDER_NO,A.SETMAIN_FLG,A.ORDER_SEQ AS SEQ_NO,A.CASE_NO,A.HIDE_FLG,A.ORDERSET_CODE FROM ODI_ORDER A WHERE CASE_NO='"+rowParm.getValue("CASE_NO")+"' AND ORDER_NO='"+rowParm.getValue("ORDER_NO")+"' AND ORDERSET_GROUP_NO='"+rowParm.getValue("ORDERSET_GROUP_NO")+"' AND ORDERSET_CODE='"+rowParm.getValue("ORDER_CODE")+"' AND SETMAIN_FLG='N'"));
                   int rowCount = itemParm.getCount();
                   for(int j=0;j<rowCount;j++){
                       TParm temp = itemParm.getRow(j);
                       int rowIds = execRecord.insertRow();
                       execRecord.setItem(rowIds,"MR_NO",temp.getValue("MR_NO"));
                       execRecord.setItem(rowIds,"CASE_NO",temp.getValue("CASE_NO"));
                       execRecord.setItem(rowIds,"RX_NO",temp.getValue("ORDER_NO"));
                       execRecord.setItem(rowIds,"SEQ_NO",temp.getValue("SEQ_NO"));
                       execRecord.setItem(rowIds,"SETMAIN_FLG",temp.getValue("SETMAIN_FLG"));
                       execRecord.setItem(rowIds,"HIDE_FLG",temp.getValue("HIDE_FLG"));
                       execRecord.setItem(rowIds,"ORDERSET_GROUP_NO",temp.getValue("ORDERSET_GROUP_NO"));
                       execRecord.setItem(rowIds,"ORDERSET_CODE",temp.getValue("ORDERSET_CODE"));
                       execRecord.setItem(rowIds,"ORDER_DESC",temp.getValue("ORDER_DESC"));
                       execRecord.setItem(rowIds,"ORDER_CODE",temp.getValue("ORDER_CODE"));
                       execRecord.setItem(rowIds,"EXEC_NO",maxNo);
                       execRecord.setItem(rowIds,"BOOKING_DATE","");
                       execRecord.setItem(rowIds,"EXEC_STATUS","N");
                       execRecord.setItem(rowIds,"EXEC_DEPT",Operator.getDept());
                       execRecord.setItem(rowIds,"EXEC_DATE","");
                       execRecord.setItem(rowIds,"EXEC_USER","");
                       execRecord.setItem(rowIds,"DEVICE_ID",rowParm.getValue("DEV_CODE"));
                       execRecord.setItem(rowIds,"REMARK",temp.getValue("DR_NOTE"));
                   }
                   maxNo++;
               }else{
                   int rowId = execRecord.insertRow();
                   execRecord.setItem(rowId,"MR_NO",rowParm.getValue("MR_NO"));
                   execRecord.setItem(rowId,"CASE_NO",rowParm.getValue("CASE_NO"));
                   execRecord.setItem(rowId,"RX_NO",rowParm.getValue("ORDER_NO"));
                   execRecord.setItem(rowId,"SEQ_NO",rowParm.getValue("SEQ_NO"));
                   execRecord.setItem(rowId,"SETMAIN_FLG",rowParm.getValue("SETMAIN_FLG"));
                   execRecord.setItem(rowId,"HIDE_FLG",rowParm.getValue("HIDE_FLG"));
                   execRecord.setItem(rowId,"ORDERSET_GROUP_NO",rowParm.getValue("ORDERSET_GROUP_NO"));
                   execRecord.setItem(rowId,"ORDERSET_CODE",rowParm.getValue("ORDERSET_CODE"));
                   execRecord.setItem(rowId,"ORDER_DESC",rowParm.getValue("ORDER_DESC"));
                   execRecord.setItem(rowId,"ORDER_CODE",rowParm.getValue("ORDER_CODE"));
                   execRecord.setItem(rowId,"EXEC_NO",maxNo);
                   execRecord.setItem(rowId,"BOOKING_DATE","");
                   execRecord.setItem(rowId,"EXEC_STATUS","N");
                   execRecord.setItem(rowId,"EXEC_DEPT",Operator.getDept());
                   execRecord.setItem(rowId,"EXEC_DATE","");
                   execRecord.setItem(rowId,"EXEC_USER","");
                   execRecord.setItem(rowId,"DEVICE_ID",rowParm.getValue("DEV_CODE"));
                   execRecord.setItem(rowId,"REMARK",rowParm.getValue("DR_NOTE"));
                   maxNo++;
               }
           }
       }
       return execRecord;
   }
   /**
    * ����
    */
   public void onSave(){
       this.getTTable(TABLE2).acceptText();
       int newRow = this.getTTable(TABLE2).getDataStore().getNewRows().length;
       int modifidRow = this.getTTable(TABLE2).getDataStore().getModifiedRows().length;
       int row=this.getTTable(TABLE1).getSelectedRow();
       boolean table1Flg = false;
		if (null!=type && (type.equals("OPO")||type.equals("OPE"))) {//===pangben 2014-3-20 �������˲���ִ��opd_order��EXEC_FLG״̬
			if (row<0) {
//				this.messageBox("��ѡ��Ҫִ�е�����");
//				return;
			}else{
				table1Flg = true;
				this.getTTable(TABLE1).acceptText();
				TParm tableParm=this.getTTable(TABLE1).getParmValue();
				String tipString = "";//ȡ��
				String onSaveString = "";//����
				for(int i = 0;i<tableParm.getCount();i++){
				if(tableParm.getRow(i).getValue("FLG").equals("Y")){
//				String sql="";
				if (tableParm.getRow(i).getValue("EXEC_FLG").equals("Y")) {
					String patName = tableParm.getRow(i).getValue("PAT_NAME");
					tipString += patName + ",";
//					sql="UPDATE OPD_ORDER SET EXEC_FLG='N',EXEC_DR_CODE = '', EXEC_DATE = ''  WHERE CASE_NO='"+
//					tableParm.getRow(i).getValue("CASE_NO")+"' AND RX_NO='"+tableParm.getRow(i).getValue("ORDER_NO")+
//					"' AND (SEQ_NO='"+tableParm.getRow(i).getValue("SEQ_NO")+"' " +
//							"OR ORDERSET_GROUP_NO = '"+tableParm.getRow(i).getValue("ORDERSET_GROUP_NO")+"')";
				}else{
					String sql="UPDATE OPD_ORDER SET EXEC_FLG='Y',EXEC_DR_CODE = '"+Operator.getID()+"', EXEC_DATE = SYSDATE WHERE CASE_NO='"+
					tableParm.getRow(i).getValue("CASE_NO")+"' AND RX_NO='"+tableParm.getRow(i).getValue("ORDER_NO")+
					"' AND (SEQ_NO='"+tableParm.getRow(i).getValue("SEQ_NO")+"' " +
							"OR (ORDERSET_GROUP_NO = '"+tableParm.getRow(i).getValue("ORDERSET_GROUP_NO")+"') AND ORDERSET_CODE IS NOT NULL)";
					TParm result=new TParm(TJDODBTool.getInstance().update(sql));
					if (result.getErrCode()<0) {
						this.messageBox("ִ��ҽ������ʧ��");
						return;
					}
				}
				
			}
			}
				if(!"".equals(tipString)&&!tipString.equals(null)){//����ȡ������
					if (messageBox("��ʾ��Ϣ Tips", tipString+"�Ƿ�ȡ������? \n Are you sure cancel?",
							this.YES_NO_OPTION) == 0) {
							for(int j = 0;j<tableParm.getCount();j++){
								if(tableParm.getRow(j).getValue("FLG").equals("Y")&&
										tableParm.getRow(j).getValue("EXEC_FLG").equals("Y")){
									String sql="UPDATE OPD_ORDER SET EXEC_FLG='N',EXEC_DR_CODE = '', EXEC_DATE = ''  WHERE CASE_NO='"+
									tableParm.getRow(j).getValue("CASE_NO")+"' AND RX_NO='"+tableParm.getRow(j).getValue("ORDER_NO")+
									"' AND (SEQ_NO='"+tableParm.getRow(j).getValue("SEQ_NO")+"' " +
											"OR (ORDERSET_GROUP_NO = '"+tableParm.getRow(j).getValue("ORDERSET_GROUP_NO")+"') AND ORDERSET_CODE IS NOT NULL )";
									TParm result=new TParm(TJDODBTool.getInstance().update(sql));
									if (result.getErrCode()<0) {
										this.messageBox("ִ��ҽ������ʧ��");
										return;
//										AND ORDERSET_CODE IS NOT NULL
									}
								}
						}
	        		}
				}
		}
		}
       if(newRow>0||modifidRow>0){
           if(this.getTTable(TABLE2).getDataStore().update()){
               TParm rowParm = this.getTTable(TABLE1).getParmValue().getRow(this.getTTable(TABLE1).getSelectedRow());
               if(updateOpdOrder(rowParm)){
                   this.messageBox("����ɹ���");
                   onTableClicked(this.getTTable(TABLE1).getSelectedRow());
                   if (null!=type && (type.equals("OPO")||type.equals("OPE"))) {//===pangben 2014-3-20 �������˲���ִ��opd_order��EXEC_FLG״̬
                	   //onQuery();
                	  String tableSql = getQuerySQLOpd(this.getTTable(TABLE1).getParmValue().getValue("CASE_NO",row).toString(),true);
//                 	   this.getTTable(TABLE1).removeRow(row);
                	  TParm parm = new TParm(getDBTool().select(tableSql));
                	  int rowCount = parm.getCount();
                	  
                      for(int i=0;i<rowCount;i++){
                          if(parm.getValue("SETMAIN_FLG",i).equals("Y")){
                        	  parm.setData("AR_AMT",i,getArAmtOrderSetFlg("O",parm.getValue("ORDER_CODE",i),parm.getTimestamp("ORDER_DATE",i),parm.getValue("ORDER_NO",i),parm.getValue("ORDERSET_GROUP_NO",i)));
                          }
                      }
                      this.getTTable(TABLE1).setParmValue(parm);
//                	   this.getTTable(TABLE2).removeRowAll();
                   }
               }else{
                   this.messageBox("����ʧ�ܣ�");
               }
           }else{
               this.messageBox("����ʧ�ܣ�");
           }
       }else if(!table1Flg){      
//           if (null!=type && (type.equals("OPO")||type.equals("OPE"))) {//===pangben 2014-3-20 �������˲���ִ��opd_order��EXEC_FLG״̬
//        	   //onQuery();
//        	   this.messageBox("����ɹ���");
//        	  String tableSql = getQuerySQLOpd(this.getTTable(TABLE1).getParmValue().getValue("CASE_NO",row).toString());
////         	   this.getTTable(TABLE1).removeRow(row);
//        	  TParm parm = new TParm(getDBTool().select(tableSql));
//        	  int rowCount = parm.getCount();
//        	  
//              for(int i=0;i<rowCount;i++){
//                  if(parm.getValue("SETMAIN_FLG",i).equals("Y")){
//                	  parm.setData("AR_AMT",i,getArAmtOrderSetFlg(this.getValueString("ADM_TYPEQ"),parm.getValue("ORDER_CODE",i),
//                			  parm.getTimestamp("ORDER_DATE",i),parm.getValue("ORDER_NO",i),parm.getValue("ORDERSET_GROUP_NO",i)));
//                  }
//              }
//              this.getTTable(TABLE1).setParmValue(parm);
////        	   this.getTTable(TABLE2).removeRowAll();
//           }else{
        	   this.messageBox("û����Ҫ��������ݣ�");
//           }
       }
       this.onQuery(false);
   }
   /**
    * ������ס��״̬
    * @param parm TParm
    * @return boolean
    */
   public boolean updateOpdOrder(TParm parm){
       String dateStr = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMddHHmmss");
//       this.messageBox_(parm.getValue("ADM_TYPE"));
//       // System.out.println("SQL====="+"SELECT RX_NO FROM EXM_EXEC_RECORD WHERE MR_NO='"+parm.getValue("MR_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"' AND RX_NO='"+parm.getValue("ORDER_NO")+"' AND ORDERSET_CODE='"+parm.getValue("ORDERSET_CODE")+"' AND ORDERSET_GROUP_NO='"+parm.getValue("ORDERSET_GROUP_NO")+"' AND EXEC_STATUS='N' ORDER BY CASE_NO,RX_NO,SEQ_NO,EXEC_NO");
       TParm tableParm = new TParm(this.getDBTool().select("SELECT RX_NO FROM EXM_EXEC_RECORD WHERE MR_NO='"+parm.getValue("MR_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"' AND RX_NO='"+parm.getValue("ORDER_NO")+"' AND ORDERSET_CODE='"+parm.getValue("ORDERSET_CODE")+"' AND ORDERSET_GROUP_NO='"+parm.getValue("ORDERSET_GROUP_NO")+"' AND EXEC_STATUS='N' ORDER BY CASE_NO,RX_NO,SEQ_NO,EXEC_NO"));
       if("O".equals(parm.getValue("ADM_TYPE"))||"E".equals(parm.getValue("ADM_TYPE"))){
//           this.messageBox_(tableParm.getCount("RX_NO"));
           if(tableParm.getCount("RX_NO")<=0){
               // System.out.println("UPDATE OPD_ORDER SET EXM_EXEC_END_DATE=TO_DATE('"+dateStr+"','YYYYMMDDHH24MISS') WHERE MR_NO='"+parm.getValue("MR_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"' AND RX_NO='"+parm.getValue("ORDER_NO")+"' AND ORDERSET_CODE='"+parm.getValue("ORDERSET_CODE")+"' AND ORDERSET_GROUP_NO='"+parm.getValue("ORDERSET_GROUP_NO")+"'");
               TParm result = new TParm(this.getDBTool().update("UPDATE OPD_ORDER SET EXM_EXEC_END_DATE=TO_DATE('"+dateStr+"','YYYYMMDDHH24MISS') WHERE MR_NO='"+parm.getValue("MR_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"' AND RX_NO='"+parm.getValue("ORDER_NO")+"' AND ORDERSET_CODE='"+parm.getValue("ORDERSET_CODE")+"' AND ORDERSET_GROUP_NO='"+parm.getValue("ORDERSET_GROUP_NO")+"'"));
               if(result.getErrCode()!=0)
                   return false;
           }
       }
       if("I".equals(parm.getValue("ADM_TYPE"))){
           if(tableParm.getCount("RX_NO")<=0){
               TParm result = new TParm(this.getDBTool().update("UPDATE ODI_ORDER SET EXM_EXEC_END_DATE=TO_DATE('"+dateStr+"','YYYYMMDDHH24MISS') WHERE MR_NO='"+parm.getValue("MR_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"' AND ORDER_NO='"+parm.getValue("ORDER_NO")+"' AND ORDERSET_CODE='"+parm.getValue("ORDERSET_CODE")+"' AND ORDERSET_GROUP_NO='"+parm.getValue("ORDERSET_GROUP_NO")+"'"));
               if(result.getErrCode()!=0)
                   return false;
           }
       }
       return true;
   }
   /**
    * ���
    */
	public void onClear() {
		// Timestamp sysDate = SystemTool.getInstance().getDate();
		String sysDate = SystemTool.getInstance().getDate().toString();
		sysDate = sysDate.substring(0, 4) + "/" + sysDate.substring(5, 7) + "/"
				+ sysDate.substring(8, 10);
		// Ĭ��������ʼ����"
		this.setValue("START_DATE", sysDate + " 00:00:00");
		// Ĭ��������ֹ����
		this.setValue("END_DATE", sysDate + " 23:59:59");
		//����ִ�п���
		cIsExinv.setSelected(false);
		this.setValue("IND_ORG", "");//ִ�п���
		this.setValue("EXEC_DEPT_CODE", "");//ִ�п���
		this.setValue("CLINICAREA_CODE", Operator.getStation());//ִ�п���
		if (this.getTTable(TABLE2).getDataStore() != null) {
			ExecRecordDataStore execRecord = new ExecRecordDataStore();
			execRecord.onQuery(type);
			this.getTTable(TABLE2).setDataStore(execRecord);
			this.getTTable(TABLE2).setDSValue();
		}
		clearValue("MR_NO;PAT_NAME;SEX_CODE;AGE;");
//		isExeOp();
		this.getTTable(TABLE1).removeRowAll();
		this.getTTable(TABLE2).removeRowAll();
	}
	
	private String setExecDeptCode(){
		//���ÿ���
        String execDeptSql = "SELECT COST_CENTER_CODE FROM SYS_OPERATOR WHERE USER_ID = '"+Operator.getID()+"'";
        TParm execParm = new TParm(TJDODBTool.getInstance().select(execDeptSql));
		 String execDept = execParm.getValue("COST_CENTER_CODE", 0);
	        this.setValue("EXEC_DEPT_CODE", execDept);//ִ�п���
	        return execDept;
	}
   /**
    * ����
    */
   public void onreadCard(){
	   if("O".equals(this.getValueString("ADM_TYPEQ"))){
		   this.setValue("EXEC_DEPT_CODE", Operator.getDept());
	   }
       TParm patParm = jdo.ekt.EKTIO.getInstance().TXreadEKT();
       if (patParm.getErrCode() < 0) {
           this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
           return;
       }
       
       this.setValue("MR_NO", patParm.getValue("MR_NO"));
       this.onQMrNo();
   }
   /**
    * ��ѯ
    */
   public void onQuery(boolean ektFlg){
       Timestamp sysDate = SystemTool.getInstance().getDate();
       if(this.getValueString("MR_NO").length()!=0){
           this.setValue("MR_NO",PatTool.getInstance().checkMrno(this.getValueString("MR_NO")));
           this.setValue("PAT_NAME",PatTool.getInstance().getNameForMrno(PatTool.getInstance().checkMrno(this.getValueString("MR_NO"))));
           TParm patParm = PatTool.getInstance().getInfoForMrno(this.getValueString("MR_NO"));
           Timestamp temp = patParm.getTimestamp("BIRTH_DATE", 0) == null ? sysDate : patParm.getTimestamp("BIRTH_DATE", 0);
           this.setValue("SEX_CODE", patParm.getValue("SEX_CODE", 0));
           this.setValue("AGE", OdiUtil.showAge(temp, sysDate));
       }
       if(this.getValueString("ADM_TYPEQ").length()==0){
          this.messageBox("�����벡����Դ��");
          return;
      }
       if(this.getTTable(TABLE2).getDataStore()!=null){
           ExecRecordDataStore execRecord = new ExecRecordDataStore();
           execRecord.onQuery(type);
           this.getTTable(TABLE2).setDataStore(execRecord);
           this.getTTable(TABLE2).setDSValue();
       }
       String admType = this.getValueString("ADM_TYPEQ");
       String sql = "";
       //����
       if("O".equals(admType)||"E".equals(admType)){
           sql = getQuerySQLOpd(getCaseNoOpb(ektFlg),ektFlg);
       }
       //סԺ
       if("I".equals(admType)){
           sql = getQuerySQLOdi(getCaseNoOdi());
       }
//       System.out.println("sql sql sql sql is ::"+sql);
       if(sql.length()==0){
           this.messageBox("�޲�ѯSQL");
           return;
       }
       TParm parm = new TParm(getDBTool().select(sql));
       if(parm.getCount()<=0){
    	   this.getTTable(TABLE1).removeRowAll();
    	   this.getTTable(TABLE2).removeRowAll();
           this.messageBox("����Ҫ���������ݡ�");
           return;
       }
       int rowCount = parm.getCount();
       TParm newParm = new TParm();
       for(int i=0;i<rowCount;i++){
    	   //��ѯEXM_EXEC_RECORD���е����ҽ����ִ��״̬
    	   String exmSql = "SELECT EXEC_STATUS FROM EXM_EXEC_RECORD WHERE MR_NO = '"+parm.getValue("MR_NO",i)+"' " +
    	   		"AND CASE_NO = '"+parm.getValue("CASE_NO",i)+"' AND RX_NO = '"+parm.getValue("ORDER_NO",i)+"' " +
    	   				"AND SEQ_NO = '"+parm.getValue("SEQ_NO",i)+"'  ";
//    	   System.out.println("exmSql exmSql exmSql is ::"+exmSql);
    	   TParm exmParm = new TParm(TJDODBTool.getInstance().select(exmSql));
    	   String execFlg = "NO";
    	   if(exmParm.getCount()<=0){
    		   parm.setData("EXE_FLG",i,"ȫ��δ���");
    	   }else{
    		   
    		   int m = 0;
        	   for(int j = 0;j<exmParm.getCount();j++){
        		   if(exmParm.getValue("EXEC_STATUS", j).equals("Y")){
        			   execFlg = "YES";
        			   m++;
        		   }
        	   }
        	   if(m == exmParm.getCount()){
        		   execFlg = "ALL";
        	   }
    	   }
//    	   System.out.println("execFlg execFlg execFlg is ::"+execFlg);
    	   if(execFlg.equals("NO")){//ȫ��δִ��
    		   parm.setData("EXE_FLG",i,"ȫ��δ���");
    		   newParm.addRowData(parm, i);
    	   }else if(execFlg.equals("YES")){
    		   parm.setData("EXE_FLG",i,"����δ���");
    		   newParm.addRowData(parm, i);
    	   }else if(execFlg.equals("ALL")){
    		   parm.setData("EXE_FLG",i,"ȫ�����");
    	   }
           if(parm.getValue("SETMAIN_FLG",i).equals("Y")){
               if("O".equals(admType)||"E".equals(admType)){
                   parm.setData("AR_AMT",i,getArAmtOrderSetFlg(admType,parm.getValue("ORDER_CODE",i),parm.getTimestamp("ORDER_DATE",i),parm.getValue("ORDER_NO",i),parm.getValue("ORDERSET_GROUP_NO",i)));
                   newParm.setData("AR_AMT",i,getArAmtOrderSetFlg(admType,newParm.getValue("ORDER_CODE",i),newParm.getTimestamp("ORDER_DATE",i),newParm.getValue("ORDER_NO",i),newParm.getValue("ORDERSET_GROUP_NO",i)));
               }
               if("I".equals(admType)){
                   parm.setData("AR_AMT",i,getArAmtOrderSetFlg(admType,parm.getValue("ORDER_CODE",i),parm.getTimestamp("NS_CHECK_DATE",i),parm.getValue("ORDER_NO",i),parm.getValue("ORDERSET_GROUP_NO",i)));
                   newParm.setData("AR_AMT",i,getArAmtOrderSetFlg(admType,newParm.getValue("ORDER_CODE",i),newParm.getTimestamp("ORDER_DATE",i),newParm.getValue("ORDER_NO",i),newParm.getValue("ORDERSET_GROUP_NO",i)));
               }
               
           }
           if(parm.getValue("SETMAIN_FLG",i).equals("N")){
                if("I".equals(admType)){
                    parm.setData("AR_AMT",i,getOwnPrice(parm.getValue("ORDER_CODE",i),parm.getTimestamp("NS_CHECK_DATE",i)));
                    newParm.setData("AR_AMT",i,getOwnPrice(newParm.getValue("ORDER_CODE",i),newParm.getTimestamp("NS_CHECK_DATE",i)));
                }
           }
       }
       if(ektFlg){//��ҽ�ƿ����߲����Żس��¼�
    	   if(newParm.getCount("EXE_FLG")==-1){
//    		   this.messageBox("�ò�����Ŀ�����ꡣ");
    		   this.messageBox("����Ҫ���������ݡ�");
    	   }
    	   this.getTTable(TABLE1).setParmValue(newParm);
       }else{//��ѯ��ť
    	   this.getTTable(TABLE1).setParmValue(parm);
       }
   }
   /**
    * �õ�ҽ���۸�
    * @param orderCode String
    * @param date Timestamp
    * @return double
    */
   public double getOwnPrice(String orderCode,Timestamp date){
       double price =0.0;
       String dateStr = StringTool.getString(date,"yyyyMMddHHmmss");
       TParm sysParm = new TParm(this.getDBTool().select("SELECT OWN_PRICE FROM SYS_FEE_HISTORY WHERE ORDER_CODE='"+orderCode+"' AND TO_DATE('"+dateStr+"','YYYYMMDDHH24MISS') BETWEEN TO_DATE(START_DATE,'YYYYMMDDHH24MISS') AND TO_DATE(END_DATE,'YYYYMMDDHH24MISS')"));
       if(sysParm.getCount()>0){
           price = sysParm.getDouble("OWN_PRICE",0);
       }
       return price;
   }
   /**
    * �õ�����ҽ���۸�
    * @param orderCode String
    * @return long
    */
   public double getArAmtOrderSetFlg(String admType,String orderCode,Timestamp date,String orderNo,String orderGroupNo){
       double arAmt = 0.0;
       String dateStr = StringTool.getString(date,"yyyyMMddHHmmss");
       if("O".equals(admType)||"E".equals(admType)){
           TParm opdParm = new TParm(this.getDBTool().select("SELECT AR_AMT FROM OPD_ORDER WHERE ORDERSET_CODE='"+orderCode+"' AND RX_NO='"+orderNo+"' AND ORDERSET_GROUP_NO='"+orderGroupNo+"' AND SETMAIN_FLG='N'"));
           int rowCount = opdParm.getCount();
           for(int i=0;i<rowCount;i++){
               arAmt+=opdParm.getDouble("AR_AMT",i);
           }
       }
       if("I".equals(admType)){
           TParm odiParm = new TParm(this.getDBTool().select("SELECT B.OWN_PRICE FROM ODI_ORDER A,SYS_FEE_HISTORY B WHERE A.ORDERSET_CODE='T0503006' AND ORDERSET_GROUP_NO='6' AND ORDER_NO='100427000002' AND SETMAIN_FLG='N' AND A.ORDER_CODE=B.ORDER_CODE AND TO_DATE('"+dateStr+"','YYYYMMDDHH24MISS') BETWEEN TO_DATE(B.START_DATE,'YYYYMMDDHH24MISS') AND TO_DATE(B.END_DATE,'YYYYMMDDHH24MISS')"));
           int rowCount = odiParm.getCount();
           for(int i=0;i<rowCount;i++){
               arAmt+=odiParm.getDouble("OWN_PRICE",i);
           }
       }
       // System.out.println("ARAMT============"+arAmt);
       return arAmt;
   }
   /**
    * �õ�CASE_NO
    * @return String
    */
   public String getCaseNoOpb(boolean ektFlg){
        TParm parm = new TParm();
        String caseNo = "";
        if(this.getValueString("MR_NO").length()==0)
            return "";
        parm.setData("MR_NO", this.getValueString("MR_NO"));
        parm.setData("PAT_NAME", this.getValueString("PAT_NAME"));
        parm.setData("SEX_CODE", this.getValueString("SEX_CODE"));
        parm.setData("AGE", this.getValueString("AGE"));
        parm.setData("ADM_TYPE",this.getValueString("ADM_TYPEQ"));
        parm.setData("START_DATE",this.getValue("START_DATE"));
        parm.setData("END_DATE",this.getValue("END_DATE"));
        //�ж��Ƿ����ϸ�㿪�ľ����ѡ��
        parm.setData("count", "0");
        parm.setData("REGION_CODE",Operator.getRegion());
        TParm caseNoParm = new TParm();
        caseNoParm = PatAdmTool.getInstance().selDateByMrNoAdm(getValueString("MR_NO"),
                (Timestamp) getValue("START_DATE"), (Timestamp) getValue("END_DATE"),
                this.getValueString("ADM_TYPEQ"),Operator.getRegion());
        if(caseNoParm.getCount()==1){
        	caseNo = caseNoParm.getValue("CASE_NO",0);
        }else if(ektFlg){//�������߻س�ʱ
        Object obj = openDialog(
            "%ROOT%\\config\\med\\MEDChooseVisitADM.x", parm);
        
        if(obj!=null){
            TParm opdParm = (TParm)obj;
//            this.messageBox_(opdParm);
            caseNo = opdParm.getValue("CASE_NO");
            this.setValue("START_DATE",opdParm.getTimestamp("ADM_DATE"));
        }
        }

        if (caseNo == null || caseNo.length() == 0 || caseNo.equals("null"))
            return "";
        return caseNo;
   }
   /**
    * �õ�CASE_NO
    * @return String
    */
   public String getCaseNoOdi(){
       TParm parm = new TParm();
       TParm caseNoParm = new TParm();
       String caseNo = "";
       if(this.getValueString("MR_NO").length()==0)
            return "";
       parm.setData("MR_NO", this.getValueString("MR_NO"));
       parm.setData("PAT_NAME", this.getValueString("PAT_NAME"));
       parm.setData("SEX_CODE", this.getValueString("SEX_CODE"));
       parm.setData("AGE", this.getValueString("AGE"));
       parm.setData("ADM_TYPE",this.getValueString("ADM_TYPEQ"));
       parm.setData("START_DATE",this.getValue("START_DATE"));
       parm.setData("END_DATE",this.getValue("END_DATE"));
       //�ж��Ƿ����ϸ�㿪�ľ����ѡ��
       parm.setData("count", "0");
       parm.setData("REGION_CODE",Operator.getRegion());
       String sql = "SELECT CASE_NO,IN_DATE AS ADM_DATE,'' AS SESSION_CODE,DEPT_CODE,VS_DR_CODE AS DR_CODE "+
       "FROM ADM_INP "+
       "  WHERE MR_NO='"+this.getValueString("MR_NO")+"'"+
       " AND IN_DATE BETWEEN TO_DATE('"+StringTool.getString((Timestamp) getValue("STARTTIME"),"yyyyMMdd")+"','YYYYMMDD') AND TO_DATE('"+StringTool.getString((Timestamp) getValue("ENDTIME"),"yyyyMMdd")+"','YYYYMMDD')"+
       " ORDER BY CASE_NO";
   // System.out.println("SQL==="+sql);
       caseNoParm = new TParm(getDBTool().select(sql));
       if(caseNoParm.getCount() == 1){
    	   caseNo = caseNoParm.getValue("CASE_NO",0);
       }else{
    	   Object obj = openDialog(
    	            "%ROOT%\\config\\med\\MEDChooseVisitADM.x", parm);
    	        
    	        if(obj!=null){
    	            TParm opdParm = (TParm)obj;
//    	            this.messageBox_(opdParm);
    	            caseNo = opdParm.getValue("CASE_NO");
    	            this.setValue("START_DATE",opdParm.getTimestamp("ADM_DATE"));
    	        }
       }
       
        if (caseNo == null || caseNo.length() == 0 || caseNo.equals("null"))
            return "";
        return caseNo;
   }

   /**
    * �õ���ѯ��SQL����
    * @param tableName String
    * @return String
    */
   public String getQuerySQLOpd(String caseNo,boolean ektFlg){
       String startDate=SystemTool.getInstance().getDateReplace(this.getValue("START_DATE").toString(), true).toString();
       String endDate =SystemTool.getInstance().getDateReplace(this.getValue("END_DATE").toString(), true).toString();
       String sql="";
       if (null!=type && (type.equals("OPO")||type.equals("OPE"))) {//==========pangben 2014-3-20 ����������˹���
    	   String mrNo = "";
    	   if (this.getValue("MR_NO").toString().length()>0) {
    		   mrNo=" AND A.MR_NO='"+this.getValue("MR_NO").toString()+"' ";
    	   }
//    	   String deptCode="";
//    	   if (this.getValue("DEPT_CODE").toString().length()>0) {
//    		   deptCode=" AND A.DEPT_CODE='"+this.getValue("DEPT_CODE").toString()+"' ";
//    	   }
    	   String execDeptCode="";
    	   String clinicAreaCode="";
    	   if(!ektFlg){
    		   if (!(this.getValue("CLINICAREA_CODE").equals(null))&&!"".equals(this.getValue("CLINICAREA_CODE"))
        			   &&this.getValue("CLINICAREA_CODE").toString().length()>0) {
        		   clinicAreaCode=" AND D.CLINICAREA_CODE='"+this.getValue("CLINICAREA_CODE").toString()+"' ";
        	   }
        	    
    	   }
    	   if (!(this.getValue("EXEC_DEPT_CODE").equals(null))&&!"".equals(this.getValue("EXEC_DEPT_CODE"))
    			   &&this.getValue("EXEC_DEPT_CODE").toString().length()>0 && !cIsExinv.isSelected()) {
    		   execDeptCode=" AND A.EXEC_DEPT_CODE='"+this.getValue("EXEC_DEPT_CODE").toString()+"' ";
    	   } 
    	   String exinvLhs1="";
    	   String exinvLhs2="";
    	   String exinvTable="";
    	   String exinvroute = "";
    	   if(cIsExinv.isSelected()){
    		   exinvLhs1 = " AND A.EXEC_DEPT_CODE = C.ORG_CODE";
    		   if( getValueString("IND_ORG").length() > 0 ){
    			   exinvLhs1 += " AND A.EXEC_DEPT_CODE = '" + getValueString("IND_ORG") + "'";
    		   }
    		   exinvroute = " ,E.ROUTE_CHN_DESC AS ROUTE_CODE ";
    		   exinvLhs2 = " AND C.EXINV_FLG = 'Y' AND A.CAT1_TYPE = 'PHA' AND A.ROUTE_CODE = E.ROUTE_CODE ";
    		   exinvTable=", IND_ORG C,SYS_PHAROUTE E";
    	   }else{
    		   exinvLhs2 = " AND A.CAT1_TYPE = 'TRT' ";
    	   }
    	   if(caseNo==null||caseNo.length()==0){
	           sql = "SELECT 'N' FLG,A.ADM_TYPE,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.ORDER_DESC"+exinvroute+",A.DISPENSE_QTY,F.FREQ_CHN_DESC AS FREQ_CODE,A.ORDER_DATE,A.AR_AMT,A.DR_NOTE,A.DEV_CODE,A.ORDERSET_GROUP_NO," +
	           		"A.ORDER_CODE,A.RX_NO AS ORDER_NO,A.SETMAIN_FLG,A.SEQ_NO,A.CASE_NO,A.HIDE_FLG,A.ORDERSET_CODE," +
	           		"CASE WHEN EXEC_FLG='Y' THEN '������' ELSE 'δ����' END STATUS_TYPE,EXEC_FLG,'ȫ��δִ��' AS EXE_FLG,A.EXEC_DEPT_CODE,A.EXEC_DATE   " +
	           		" FROM OPD_ORDER A,SYS_PATINFO B"+exinvTable+",REG_PATADM D ,SYS_PHAFREQ F " +
	           				"WHERE A.MR_NO=B.MR_NO  AND A.CASE_NO = D.CASE_NO AND A.FREQ_CODE = F.FREQ_CODE " +
	           				""+exinvLhs1+"" +execDeptCode +mrNo+clinicAreaCode+  //��ѯ����ȥ��ִ�п���
	           		exinvLhs2+" AND A.RX_TYPE <> '7' AND A.ORDER_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')" +//AND A.RX_TYPE = '7'Ϊ����Ʒ�¼�� 
	           						" AND A.ADM_TYPE = '"+this.getValueString("ADM_TYPEQ")+"'  ORDER BY A.MR_NO,A.ORDER_DATE";//yanjing cat1_type ȥ��'PLN','OTH',A.EXM_EXEC_END_DATE IS NULL
	       }else{
	           sql = "SELECT 'N' FLG,A.ADM_TYPE,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.ORDER_DESC "+exinvroute+",A.DISPENSE_QTY,F.FREQ_CHN_DESC AS FREQ_CODE,A.ORDER_DATE," +
	           		"A.DISPENSE_QTY,A.AR_AMT,A.DR_NOTE,A.DEV_CODE,A.ORDERSET_GROUP_NO,A.ORDER_CODE" +
	           		",A.RX_NO AS ORDER_NO,A.SETMAIN_FLG,A.SEQ_NO,A.CASE_NO,A.HIDE_FLG,A.ORDERSET_CODE," +
	           		"CASE WHEN EXEC_FLG='Y' THEN '������' ELSE 'δ����' END STATUS_TYPE,EXEC_FLG,A.EXEC_DEPT_CODE,A.EXEC_DATE " +
	           		"FROM OPD_ORDER A,SYS_PATINFO B"+exinvTable+" , REG_PATADM D,SYS_PHAFREQ F" +
	           				" WHERE A.MR_NO=B.MR_NO  AND A.CASE_NO = D.CASE_NO AND A.FREQ_CODE = F.FREQ_CODE "+exinvLhs1+""+execDeptCode +mrNo+ clinicAreaCode+ //��ѯ����ȥ��ִ�п���
	           				" AND A.RX_TYPE <> '7' AND A.CASE_NO='"+caseNo+"' " +exinvLhs2+//AND A.RX_TYPE = '7'Ϊ����Ʒ�¼�� 
	           						" AND A.ADM_TYPE = '"+this.getValueString("ADM_TYPEQ")+"' AND A.ORDER_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')  ORDER BY A.MR_NO,A.ORDER_DATE";
	       }
       }else{
	       if(caseNo==null||caseNo.length()==0){
	           sql = "SELECT A.ADM_TYPE,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.ORDER_DESC,A.ORDER_DATE,A.DISPENSE_QTY,A.AR_AMT,A.DR_NOTE,A.DEV_CODE,A.ORDERSET_GROUP_NO,A.ORDER_CODE,A.RX_NO AS ORDER_NO,A.SETMAIN_FLG,A.SEQ_NO,A.CASE_NO,A.HIDE_FLG,A.ORDERSET_CODE FROM OPD_ORDER A,SYS_PATINFO B,SYS_ORDER_CAT1 C WHERE A.MR_NO=B.MR_NO AND DEPT_CODE='"+this.getValue("DEPT_CODE")+"' AND A.BILL_FLG='Y' AND A.ORDER_CAT1_CODE=C.ORDER_CAT1_CODE AND C.TREAT_FLG='Y' AND ((A.SETMAIN_FLG='Y' AND A.HIDE_FLG='N') OR (A.SETMAIN_FLG='N' AND A.HIDE_FLG='N')) AND A.ORDER_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') AND A.EXM_EXEC_END_DATE IS NULL ORDER BY A.MR_NO";
	       }else{
	           sql = "SELECT A.ADM_TYPE,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.ORDER_DESC,A.ORDER_DATE,A.DISPENSE_QTY,A.AR_AMT,A.DR_NOTE,A.DEV_CODE,A.ORDERSET_GROUP_NO,A.ORDER_CODE,A.RX_NO AS ORDER_NO,A.SETMAIN_FLG,A.SEQ_NO,A.CASE_NO,A.HIDE_FLG,A.ORDERSET_CODE FROM OPD_ORDER A,SYS_PATINFO B,SYS_ORDER_CAT1 C WHERE A.MR_NO=B.MR_NO AND DEPT_CODE='"+this.getValue("DEPT_CODE")+"' AND A.BILL_FLG='Y' AND A.ORDER_CAT1_CODE=C.ORDER_CAT1_CODE AND C.TREAT_FLG='Y' AND A.CASE_NO='"+caseNo+"' AND ((A.SETMAIN_FLG='Y' AND A.HIDE_FLG='N') OR (A.SETMAIN_FLG='N' AND A.HIDE_FLG='N')) AND A.ORDER_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') AND A.EXM_EXEC_END_DATE IS NULL ORDER BY A.MR_NO";
	       }
       }
       
       return sql;
   }
   
   /**
    * �õ���ѯ��SQLסԺ
    * @param tableName String
    * @return String
    */
   public String getQuerySQLOdi(String caseNo){
       Timestamp sDate = (Timestamp)this.getValue("START_DATE");
       String startDate = StringTool.getString(sDate,"yyyyMMdd");
       Timestamp eDate = (Timestamp)this.getValue("END_DATE");
       String endDate = StringTool.getString(eDate,"yyyyMMdd");
       String sql="";
       if(caseNo==null||caseNo.length()==0){
           sql = "SELECT 'I' AS ADM_TYPE,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.ORDER_DESC,A.ORDER_DATE,A.DISPENSE_QTY,'' AS AR_AMT,A.DR_NOTE,A.DEV_CODE,A.NS_CHECK_DATE,A.ORDERSET_GROUP_NO,A.ORDER_CODE,A.ORDER_NO,A.SETMAIN_FLG,A.ORDER_SEQ AS SEQ_NO,A.CASE_NO,A.HIDE_FLG,A.ORDERSET_CODE FROM ODI_ORDER A,SYS_PATINFO B,SYS_ORDER_CAT1 C WHERE A.MR_NO=B.MR_NO AND DEPT_CODE='"+this.getValue("DEPT_CODE")+"' AND  A.NS_CHECK_DATE IS NOT NULL AND A.ORDER_CAT1_CODE=C.ORDER_CAT1_CODE AND C.TREAT_FLG='Y' AND ((A.SETMAIN_FLG='Y' AND A.HIDE_FLG='N') OR (A.SETMAIN_FLG='N' AND A.HIDE_FLG='N')) AND A.ORDER_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDD') AND TO_DATE('"+endDate+"','YYYYMMDD') AND A.EXM_EXEC_END_DATE IS NULL ORDER BY A.MR_NO";
       }else{
           sql = "SELECT 'I' AS ADM_TYPE,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.ORDER_DESC,A.ORDER_DATE,A.DISPENSE_QTY,'' AS AR_AMT,A.DR_NOTE,A.DEV_CODE,A.NS_CHECK_DATE,A.ORDERSET_GROUP_NO,A.ORDER_CODE,A.ORDER_NO,A.SETMAIN_FLG,A.ORDER_SEQ AS SEQ_NO,A.CASE_NO,A.HIDE_FLG,A.ORDERSET_CODE FROM ODI_ORDER A,SYS_PATINFO B,SYS_ORDER_CAT1 C WHERE A.MR_NO=B.MR_NO AND DEPT_CODE='"+this.getValue("DEPT_CODE")+"' AND  A.NS_CHECK_DATE IS NOT NULL AND A.ORDER_CAT1_CODE=C.ORDER_CAT1_CODE AND C.TREAT_FLG='Y' AND A.CASE_NO='"+caseNo+"' AND ((A.SETMAIN_FLG='Y' AND A.HIDE_FLG='N') OR (A.SETMAIN_FLG='N' AND A.HIDE_FLG='N')) AND A.ORDER_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDD') AND TO_DATE('"+endDate+"','YYYYMMDD') AND A.EXM_EXEC_END_DATE IS NULL ORDER BY A.MR_NO";
       }
       // System.out.println("sqlI==="+sql);
       return sql;

   }
   /**
    * ִ�п��Ҹı��¼�
    * 
    */
   public void onChangeDept(){
	   String buff = this.getTTable(TABLE2).getDataStore().isFilter()?this.getTTable(TABLE2).
			   getDataStore().FILTER:this.getTTable(TABLE2).getDataStore().PRIMARY;
			   int rowCount = this.getTTable(TABLE2).getDataStore().getBuffer(buff).getCount();
	   for(int i = 0;i<rowCount;i++){
			   this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_DEPT",this.getValue("EXEC_DEPT_CODE"),buff);  
			   }
	   this.getTTable(TABLE2).setDSValue();
			   
   }
   /**
    * ����EXECL
    */
   public void onExecl(){
       if(this.getValueString("ADM_TYPEQ").length()==0){
           this.messageBox("��ѡ������Դ��");
           return;
       }
       TParm parm = new TParm();
       parm.setData("ADM_TYPE",this.getValueString("ADM_TYPEQ"));
       parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
       this.openDialog("%ROOT%\\config\\med\\MedExmQuery.x",parm);
   }
   /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    
    public void onExinvChanged(){
    	cIndOrg.setEnabled(cIsExinv.isSelected());
    	if(cIsExinv.isSelected()){
    		this.setTableHeaderForEXINV();
    	}else{
    		this.setTableHeaderForTRT();
    	}
    	onQuery(false);
    }

}
