package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import jdo.sys.Pat;
import jdo.sys.SystemTool; 
import com.dongyang.data.TParm;
import jdo.bms.BMSApplyMTool;
import com.javahis.util.StringUtil;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dongyang.ui.TRadioButton;
import jdo.bms.BMSApplyDTool;
import jdo.bms.BMSBloodTool;
import com.dongyang.ui.TMenuItem;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.PatTool;
import com.dongyang.data.TNull;
import com.dongyang.jdo.TJDODBTool;
import jdo.bms.BMSSQL;
import jdo.adm.ADMTool;

/**
 * <p>
 * Title: ������ѧ
 * </p>
 *
 * <p>
 * Description: ������ѧ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSBloodCrossControl
    extends TControl {

    private TTable table_m;

    private TTable table_d;

    private String case_no;

    private String adm_type;

    private String bld_type;

    public BMSBloodCrossControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        initPage();

        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm parm = (TParm) obj;
            this.setValue("APPLY_NO", parm.getValue("APPLY_NO"));
            this.onApplyNoAction();
        }
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (!CheckData()) {
            return;
        }
        TParm parm = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        for (int i = 0; i < table_m.getRowCount(); i++) {
            parm.addData("BLOOD_NO", table_m.getItemData(i, "BLOOD_NO"));
            parm.addData("STATE_CODE", "1");
            parm.addData("APPLY_NO", this.getValue("APPLY_NO"));
            parm.addData("MR_NO", this.getValue("MR_NO"));
            parm.addData("IPD_NO", this.getValue("IPD_NO"));
            parm.addData("CASE_NO", case_no);
            parm.addData("ID_NO", this.getValue("ID_NO"));
            parm.addData("CROSS_MATCH_L",
                         table_m.getItemString(i, "CROSS_MATCH_L"));            
            parm.addData("CROSS_MATCH_S",
                         table_m.getItemString(i, "CROSS_MATCH_S"));
            //parm.addData("ANTI_A", table_m.getItemData(i, "ANTI_A"));
            
            parm.addData("ANTI_A", table_m.getItemString(i, "ANTI_A"));
            
            //System.out.println("==ANTI_A=="+table_m.getItemData(i, "ANTI_A"));
            
            //System.out.println("==ANTI_B=="+table_m.getItemData(i, "ANTI_B"));

            //parm.addData("ANTI_B", table_m.getItemData(i, "ANTI_B"));
            parm.addData("ANTI_B", table_m.getItemString(i, "ANTI_B"));

            parm.addData("RESULT", table_m.getItemData(i, "RESULT"));
            
            parm.addData("RH_FLG", table_m.getItemData(i, "RH_FLG"));//==========pangben modify 20110628
            
            //System.out.println("==RH_FLG=="+table_m.getItemData(i, "RH_FLG"));
            
            parm.addData("TEST_DATE", date);
            parm.addData("TEST_USER", Operator.getID());
            parm.addData("OPT_USER", Operator.getID());
            parm.addData("OPT_DATE", date);
            parm.addData("OPT_TERM", Operator.getIP());
        }
        TParm result = TIOM_AppServer.executeAction(
            "action.bms.BMSBloodAction", "onUpdateBloodCross", parm);
        // ������ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        int row = table_m.getSelectedRow();
        if (row < 0) {
            this.messageBox("û��ѡ����");
            return;
        }
        if ("".equals(table_m.getItemString(row, "ANTI_A"))) {
            // δ������
            table_m.removeRow(row);
        }
        else {
        	TParm tableMparm = table_m.getShowParmValue() ;
        	String rhFlg = tableMparm.getValue("RH_FLG", row) ;
            // �ѱ�����
            if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ��", 2) == 0) {
                TParm parm = new TParm();
                Timestamp date = SystemTool.getInstance().getDate();
                TNull tnull = new TNull(Timestamp.class);
                parm.addData("BLOOD_NO", table_m.getItemData(row, "BLOOD_NO"));
                parm.addData("STATE_CODE", "0");
                parm.addData("APPLY_NO", "");
                parm.addData("MR_NO", "");
                parm.addData("IPD_NO", "");
                parm.addData("CASE_NO", "");
                parm.addData("ID_NO", "");
                parm.addData("CROSS_MATCH_L", "");
                parm.addData("CROSS_MATCH_S", "");
                parm.addData("ANTI_A", "");
                parm.addData("ANTI_B", ""); 
                parm.addData("RESULT", "");
                parm.addData("TEST_DATE", tnull); 
                parm.addData("TEST_USER", "");
                parm.addData("OPT_USER", Operator.getID());
                parm.addData("OPT_DATE", date);
                parm.addData("OPT_TERM", Operator.getIP());
                parm.addData("RH_FLG", rhFlg) ; 
                TParm result = TIOM_AppServer.executeAction(
                    "action.bms.BMSBloodAction", "onUpdateBloodCross", parm);
                // ������ж�
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("ɾ��ʧ��");
                    return;
                }
                this.messageBox("ɾ���ɹ�");
                table_m.removeRow(row);
            }
        }
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "APPLY_NO;PRE_DATE;USE_DATE;END_DAYS;MR_NO;IPD_NO;"
            + "PAT_NAME;SEX;AGE;ID_NO;TEST_BLD;BLOOD_TEXT;BLD_TYPE;BLD_CODE;"
            + "SUBCAT_CODE;BLOOD_NO;SHIT_FLG;CROSS_MATCH_S;CROSS_MATCH_L;"
            + "ANTI_A;ANTI_B;RESULT;TEST_DATE";
        this.clearValue(clearStr);
        this.getRadioButton("RH_A").setSelected(true);
        this.getRadioButton("RH_TYPE_A").setSelected(true);
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("TEST_DATE", date);
        table_m.removeRowAll();
        table_d.removeRowAll();
        ( (TMenuItem) getComponent("stock")).setEnabled(false);
        case_no = "";
        adm_type = "";
        bld_type = "";
    }
    //===================  chenxi modify 20121009
    /**
     * ���򲡱���
     */
    public void onTnb(){
    	 if ("".equals(this.getValueString("MR_NO"))) {
             this.messageBox("�����Ų���Ϊ��");
             return ;
         }
    	 String  mrNo = this.getValueString("MR_NO") ;
  	   SystemTool.getInstance().OpenTnbWeb(mrNo);
    }
    /**
     * RIS����
     */
    public void onRis(){
    	if ("".equals(this.getValueString("MR_NO"))) {
            this.messageBox("�����Ų���Ϊ��");
            return ;
        }
   	 String  mrNo = this.getValueString("MR_NO") ;
 	   SystemTool.getInstance().OpenRisWeb(mrNo) ;
    }
    /**
     * lis����
     * 
     */
    public void onLis(){
    	if ("".equals(this.getValueString("MR_NO"))) {
            this.messageBox("�����Ų���Ϊ��");
            return ;
        }
   	 String  mrNo = this.getValueString("MR_NO") ;
 	   SystemTool.getInstance().OpenLisWeb(mrNo);
    }
    //================    chenxi modify  20121009
    /**
     * ����ѯ
     */
    public void onStock() {
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("BLOOD_NO"))) {
            parm.setData("BLOOD_NO", this.getValue("BLOOD_NO"));
        }
        if (!"".equals(this.getValueString("BLD_CODE"))) {
            parm.setData("BLD_CODE", this.getValue("BLD_CODE"));
        }

        parm.setData("BLD_TYPE", bld_type);

        if (!"".equals(this.getValueString("SUBCAT_CODE"))) {
            parm.setData("SUBCAT_CODE", this.getValue("SUBCAT_CODE"));
        }
        
        //modify by lim 2012/05/03 begin
        Object result = openDialog("%ROOT%\\config\\bms\\BMSStockQuery.x", parm); 
        if (result != null) {
            List<TParm> addParm = (List<TParm>) result;
            if (addParm == null) {
                return;
            }        

	          TTable table1 = getTable("TABLE_M");
	          // �ж��Ƿ����ظ����� begin
	          Set<String> set = new HashSet<String>() ;
	          for (int i = 0; i < table1.getRowCount(); i++) {
	        	  set.add(table1.getItemString(i,"BLOOD_NO")) ;
	          }	          
	          for (Iterator iterator = addParm.iterator(); iterator.hasNext();) {
				TParm tParm = (TParm) iterator.next();
				if(set.contains(tParm.getValue("BLOOD_NO"))){
	                  this.messageBox("ѪƷ"+tParm.getValue("BLOOD_NO")+"�Ѵ��ڣ������ظ���Ѫ");
	                  return;					
				}else{
					set.add(tParm.getValue("BLOOD_NO")) ;
				}
			  }  
	          // �ж��Ƿ����ظ����� end
            
            for (Iterator iterator = addParm.iterator(); iterator.hasNext();) {
				TParm tParm = (TParm) iterator.next();
	            TTable table = getTable("TABLE_M");
	            int row = table.addRow();
	            table.setItem(row, "BLOOD_NO", tParm.getValue("BLOOD_NO"));
	            table.setItem(row, "BLD_CODE", tParm.getValue("BLD_CODE"));
	            table.setItem(row, "SUBCAT_CODE", tParm.getValue("SUBCAT_CODE"));
	            table.setItem(row, "BLD_TYPE", tParm.getValue("BLD_TYPE"));
	            table.setItem(row, "RH_FLG", tParm.getValue("RH_FLG"));
	            table.setItem(row, "SHIT_FLG", tParm.getValue("SHIT_FLG"));   				
			}
        }
        //modify by lim 2012/05/03 end

//        Object result = openDialog("%ROOT%\\config\\bms\\BMSStockQuery.x", parm); 
//        if (result != null) {
//            TParm addParm = (TParm) result;
//            if (addParm == null) {
//                return;
//            }
//
//            TTable table = getTable("TABLE_M");
//            // �ж��Ƿ����ظ�����
//            for (int i = 0; i < table.getRowCount(); i++) {
//                if (addParm.getValue("BLOOD_NO").equals(table.getItemString(i,
//                    "BLOOD_NO"))) {
//                    this.messageBox("ѪƷ�Ѵ��ڣ������ظ���Ѫ");
//                    return;
//                }
//            }
//            //this.messageBox_(addParm);
//
//            // �жϣ���Ѫ�� + ���������Ƿ����������
////            TParm resultParm = new TParm();
////            parm.setData("APPLY_NO", this.getValue("APPLY_NO"));
////            parm.setData("BLD_CODE", addParm.getValue("BLD_CODE"));
////            // ������  shibl 20120410 modify
////            resultParm = BMSApplyDTool.getInstance().onApplyQuery(parm);
////            if (resultParm == null || resultParm.getCount() <= 0) {
////                this.messageBox("����δ���������ѪҺ");
////                return;
////            }
//            // ��Ѫ�� + ������
////            parm.setData("SUBCAT_CODE", addParm.getValue("SUBCAT_CODE"));
////            parm.setData("BLD_TYPE", addParm.getValue("BLD_TYPE"));
////            resultParm = BMSBloodTool.getInstance().getSumTot(parm);
//            // TABLE������ӵ���Ѫ��
////            double sum_qty = 0;
////            for (int i = 0; i < table_m.getRowCount(); i++) {
////                if (table_m.getItemString(i,
////                                          "BLD_CODE").equals(addParm.getValue(
////                                              "BLD_CODE"))) {
////                    TParm parmVol = new TParm(TJDODBTool.getInstance().select(
////                        BMSSQL.getBMSBldVol(table_m.getItemString(i, "BLD_CODE"),
////                                            table_m.getItemString(i,
////                        "SUBCAT_CODE"))));
////                    sum_qty += parmVol.getDouble("BLD_VOL", 0);
////                }
////            }
////            if (resultParm.getDouble("SUM_TOT", 0) + sum_qty >
////                resultParm.getDouble("APPLY_QTY", 0)) {
////                this.messageBox("��Ѫ������������");
////                return;
////            }
//            int row = table.addRow();
//            table.setItem(row, "BLOOD_NO", addParm.getValue("BLOOD_NO"));
//            table.setItem(row, "BLD_CODE", addParm.getValue("BLD_CODE"));
//            table.setItem(row, "SUBCAT_CODE", addParm.getValue("SUBCAT_CODE"));
//            table.setItem(row, "BLD_TYPE", addParm.getValue("BLD_TYPE"));
//            table.setItem(row, "RH_FLG", addParm.getValue("RH_FLG"));
//            table.setItem(row, "SHIT_FLG", addParm.getValue("SHIT_FLG"));
//        }
    }

    /**
     * ����Ƽ�
     */
    public void onPay() {
        TParm parm = new TParm();
        if ("I".equals(adm_type)) {
            // ����סԺ����Ƽ�
            parm.setData("IBS", "CASE_NO", case_no);
            TParm adm_info = new TParm();
            adm_info.setData("CASE_NO", case_no);
            TParm result = ADMTool.getInstance().getADM_INFO(adm_info);
            parm.setData("IBS", "VS_DR_CODE",result.getValue("VS_DR_CODE", 0));
            parm.setData("IBS", "IPD_NO", this.getValueString("IPD_NO"));
            parm.setData("IBS", "MR_NO", this.getValueString("MR_NO"));
            parm.setData("IBS", "PAT_NAME", this.getValueString("PAT_NAME"));

            parm.setData("IBS", "YELLOW_SIGN", result.getData("YELLOW_SIGN",0));
            parm.setData("IBS", "RED_SIGN", result.getData("RED_SIGN",0));
            parm.setData("IBS", "STATION_CODE", result.getData("STATION_CODE",0));
            parm.setData("IBS", "GREENPATH_VALUE", result.getData("GREENPATH_VALUE",0));
            parm.setData("IBS", "CTZ2_CODE", result.getData("CTZ2_CODE",0));
            parm.setData("IBS", "TOTAL_AMT", result.getData("TOTAL_AMT",0));
            parm.setData("IBS", "DEPT_CODE", result.getData("DEPT_CODE",0));
            parm.setData("IBS", "BED_NO", result.getData("BED_NO",0));
            parm.setData("IBS", "CTZ3_CODE", result.getData("CTZ3_CODE",0));
            ///////////////////////
            parm.setData("IBS", "ORG_CODE", "");
            parm.setData("IBS", "CTZ1_CODE", result.getData("CTZ1_CODE", 0));
            parm.setData("IBS", "SAVE_FLG", true);
            parm.setData("IBS", "STOP_BILL_FLG",
                         result.getData("STOP_BILL_FLG", 0));
            parm.setData("IBS", "TYPE", "BMS");

            parm.setData("IBS", "CLNCPATH_CODE", "");
            //System.out.println("parm==="+parm);
            openDialog("%ROOT%\\config\\ibs\\IBSOrderm.x", parm);
        }
        else {
            // �������ﲹ��Ƽ�
            if ("".equals(case_no)) {
                this.messageBox("��ѡ�񲡻�");
            }
            parm.setData("MR_NO", this.getValue("MR_NO"));
            parm.setData("CASE_NO", case_no);
            parm.setData("SYSTEM","ONW");
            parm.setData("IBS", "CLNCPATH_CODE", "");
            
            openDialog("%ROOT%\\config\\opb\\OPBChargesM.x", parm);
        }
    }

    /**
     * ѪҺ����
     */
    public void onDispense() {
        // ����ѪҺ����
        onApplyNoAction();
        String apply_no = this.getValueString("APPLY_NO");
        TParm parm = new TParm();
        parm.setData("APPLY_NO", apply_no);
//        TParm result = (TParm) openWindow("%ROOT%\\config\\bms\\BMSBloodOut.x",
//                                          parm);
        openWindow("%ROOT%\\config\\bms\\BMSBloodOut.x", parm);
    }

    /**
     * ���ʱ����¼�
     */
    public void onAddAction() {
        int row = table_m.getSelectedRow();
        if (row < 0) {
            this.messageBox("û��ѡ����");
            return;
        }
        if (!checkAddData()) {
            return;
        }
        table_m.setItem(row, "CROSS_MATCH_S", this.getValue("CROSS_MATCH_S"));
        table_m.setItem(row, "CROSS_MATCH_L", this.getValue("CROSS_MATCH_L"));
        table_m.setItem(row, "ANTI_A", this.getValue("ANTI_A"));
        table_m.setItem(row, "ANTI_B", this.getValue("ANTI_B"));
        table_m.setItem(row, "RESULT", this.getValue("RESULT"));
        table_m.setItem(row, "TEST_DATE", this.getValue("TEST_DATE"));
        table_m.setItem(row, "TEST_USER", Operator.getID());
        //===========pangben modify 20110628 start RHѪ�ͱ���
        if (this.getRadioButton("RH_TYPE_A").isSelected())
            table_m.setItem(row, "RH_FLG", "+");
        else
            table_m.setItem(row, "RH_FLG", "-");
         //===========pangben modify 20110628 stop
        clearAddData();
    }

    /**
     * �����������ݼ��
     * @return boolean
     */
    private boolean checkAddData() {
    	TTable tableM = (TTable)this.getComponent("TABLE_M") ;
    	int selectedRow = tableM.getSelectedRow() ;
    	String bldCode = table_m.getItemString(selectedRow,"BLD_CODE") ;
    	String sql = "SELECT FRONTPG_TYPE FROM BMS_BLDCODE WHERE BLD_CODE='"+bldCode+"'" ;
    	TParm result2 = new TParm(TJDODBTool.getInstance().select(sql)) ;
    	String frontPgCode = "" ;
    	if(result2.getCount()>0){
    		frontPgCode =result2.getValue("FRONTPG_TYPE", 0) ; //FRONTPG_TYPEΪ03�������ΪѪ����Ѫ������������Ѫ.
    	}
    	//modify by lim 2012/05/03 begin
        String rh_type = "";
        if ( (this.getRadioButton("RH_A").isSelected())) {
            rh_type = "+";
        }
        else if ( (this.getRadioButton("RH_B").isSelected())) {
            rh_type = "-";
        }  
        //RHѪ��
        String rhFlg = table_m.getItemString(selectedRow,"RH_FLG") ;
    	//modify by lim 2012/05/03 end
    	
    	if((!"03".equals(frontPgCode)) && (!"02".equals(frontPgCode)) && (!"05".equals(frontPgCode))){//��Ѫ����״̬
        	
            if ("".equals(this.getValueString("CROSS_MATCH_S"))) {
                this.messageBox("С���治��Ϊ��");
                return false;
            }
            if ("".equals(this.getValueString("CROSS_MATCH_L"))) {
                this.messageBox("�󽻲治��Ϊ��");
                return false;
            }
            if ("".equals(this.getValueString("ANTI_A"))) {
                this.messageBox("Anti-A����Ϊ��");
                return false;
            }
            if ("".equals(this.getValueString("ANTI_B"))) {
                this.messageBox("Anti-B����Ϊ��");
                return false;
            }
            if ("".equals(this.getValueString("RESULT"))) {
                this.messageBox("��Ѫ�������Ϊ��");
                return false;
            }
            if ("".equals(this.getValueString("TEST_DATE"))) {
                this.messageBox("�������ڲ���Ϊ��");
                return false;
            }
            String s_BldType = this.getValueString("BLD_TYPE");
            String s_Anti_A = this.getValueString("ANTI_A");
            String s_Anti_B = this.getValueString("ANTI_B");
            if (s_BldType.equals("AB") &&
                (s_Anti_A.equals("-") || s_Anti_B.equals("-"))) {
                this.messageBox("Ѫ����Anti-A��Anti-B������������");
                return false;
            }
            if (s_BldType.equals("A") &&
                (s_Anti_A.equals("-") || s_Anti_B.equals("+"))) {
                this.messageBox("Ѫ����Anti-A��Anti-B������������");
                return false;
            }
            if (s_BldType.equals("B") &&
                (s_Anti_A.equals("+") || s_Anti_B.equals("-"))) {
                this.messageBox("Ѫ����Anti-A��Anti-B������������");
                return false;
            }
            if (s_BldType.equals("O") &&
                (s_Anti_A.equals("+") || s_Anti_B.equals("+"))) {
                this.messageBox("Ѫ����Anti-A��Anti-B������������");
                return false;
            } 		
    	}else{//Ѫ�������.
            if ("".equals(this.getValueString("RESULT"))) {
                this.messageBox("��Ѫ�������Ϊ��");
                return false;
            }

    	}
    	  if(!rh_type.equals(rhFlg)){
         int check = 	messageBox("��Ϣ","��Ѫ��:Ѫ������ " + table_m.getItemString(selectedRow, "BLOOD_NO") +
                          " ��RHѪ���벡��RHѪ�Ͳ���,���ܽ��н�����Ѫ,�Ƿ������",0) ;
         if(check==0){  //����У��   chenxi  modify  20130503
        	 if (!checkPW()) {
 				return false;
 			}
         }
           if(check!=0){
           this.clearValue("BLOOD_NO;BLD_CODE;SUBCAT_CODE") ;
           tableM.clearSelection() ;
          	return false ;
          	}
          }    
    	return true ;
    }

    /**
     * �����Ѫ������
     */
    private void clearAddData() {
        String clearStr = "RESULT;BLD_TYPE;BLD_CODE;SUBCAT_CODE;BLOOD_NO;"
            + "SHIT_FLG;CROSS_MATCH_S;CROSS_MATCH_L;ANTI_A;ANTI_B;TEST_DATE";
        this.clearValue(clearStr);
        this.getRadioButton("RH_TYPE_A").setSelected(true);
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("TEST_DATE", date);
    }

    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        TTable table = getTable("TABLE_M");
        int row = table.getSelectedRow();
        if (row != -1) {
            setValue("BLOOD_NO", table.getItemData(row, "BLOOD_NO"));
            setValue("BLD_CODE", table.getItemData(row, "BLD_CODE"));
            setValue("SUBCAT_CODE", table.getItemData(row, "SUBCAT_CODE"));
            setValue("BLD_TYPE", table.getItemData(row, "BLD_TYPE"));
            if ("+".equals(table.getItemString(row, "RH_FLG"))) {
                this.getRadioButton("RH_TYPE_A").setSelected(true);
            }
            else {
                this.getRadioButton("RH_TYPE_B").setSelected(true);
            }
            //this.messageBox_(table.getItemData(row, "SHIT_FLG"));
            setValue("SHIT_FLG", table.getItemData(row, "SHIT_FLG"));
            setValue("CROSS_MATCH_L", table.getItemData(row, "CROSS_MATCH_L"));
            setValue("CROSS_MATCH_S", table.getItemData(row, "CROSS_MATCH_S"));
            setValue("ANTI_A", table.getItemData(row, "ANTI_A"));
            setValue("ANTI_B", table.getItemData(row, "ANTI_B"));
            setValue("RESULT", table.getItemData(row, "RESULT"));
            if (!"".equals(table.getItemString(row, "TEST_DATE"))) {
                setValue("TEST_DATE", table.getItemData(row, "TEST_DATE"));
            }
        }
    }

    /**
     * ��Ѫ���Ų�ѯ(�س��¼�)
     */
    public void onApplyNoAction() {
        String apply_no = this.getValueString("APPLY_NO");
        if (!"".equals(apply_no)) {
            TParm parm = new TParm();
            parm.setData("APPLY_NO", apply_no);
            TParm result = BMSApplyMTool.getInstance().onApplyQuery(parm);
            //System.out.println("result==="+result);
            if (result.getCount("APPLY_NO") <= 0) {
                this.messageBox("��Ѫ��������");
                this.setValue("APPLY_NO", "");
                return;
            }
            this.setValue("PRE_DATE", result.getData("PRE_DATE", 0));
            this.setValue("USE_DATE", result.getData("USE_DATE", 0));
            this.setValue("END_DAYS", result.getData("END_DAYS", 0));
            this.setValue("MR_NO", result.getData("MR_NO", 0));
            this.setValue("IPD_NO", result.getData("IPD_NO", 0));
            // ��ѯ������Ϣ
            Pat pat = Pat.onQueryByMrNo(result.getValue("MR_NO", 0));
            this.setValue("PAT_NAME", pat.getName());
            this.setValue("SEX", pat.getSexString());
            Timestamp date = SystemTool.getInstance().getDate();
            this.setValue("AGE",
                          StringUtil.getInstance().showAge(pat.getBirthday(),
                date));
            this.setValue("ID_NO", pat.getIdNo());
            String rh_type = pat.getBloodRHType();
            //System.out.println("rh_type===="+rh_type);
            if ("-".equals(rh_type)) {
                getRadioButton("RH_B").setSelected(true);
            }
            else if ("+".equals(rh_type)) {
                getRadioButton("RH_A").setSelected(true);
            }
            else {
                getRadioButton("RH_A").setSelected(false);
                getRadioButton("RH_B").setSelected(false);
            }
            this.setValue("BLOOD_TEXT", pat.getBloodType());
            this.setValue("BLD_TYPE", pat.getBloodType());

            this.setValue("TEST_BLD", result.getData("TEST_BLD", 0));
            case_no = result.getValue("CASE_NO", 0);
            adm_type = result.getValue("ADM_TYPE", 0);
            bld_type = pat.getBloodType();

            result = BMSApplyDTool.getInstance().onApplyQuery(parm);
            if (result != null && result.getCount() > 0) {
                table_d.setParmValue(result);
            }
            parm.setData("STATE_CODE", "1");
            //modify by lim 2012/04/27 begin
            //result = BMSBloodTool.getInstance().onQueryBloodCross(parm);
            result = BMSBloodTool.getInstance().onQueryBloodCrossExceptOut(parm);
            //modify by lim 2012/04/27 end
            if (result != null && result.getCount() > 0) {
                table_m.setParmValue(result);
            }
            ( (TMenuItem) getComponent("stock")).setEnabled(true);
        }
    }

    /**
     * �����Ų�ѯ(�س��¼�)
     */
    public void onMrNoAction() {
        String mr_no = PatTool.getInstance().checkMrno(this.getValueString(
            "MR_NO"));
        TParm parm = new TParm();
        parm.setData("MR_NO", mr_no);
        TParm resultParm = BMSApplyMTool.getInstance().onApplyQuery(parm);
        //System.out.println("resultParm==="+resultParm);
        if (resultParm.getCount("APPLY_NO") <= 0) {
            this.messageBox("�����ڸò����ı�Ѫ��");
            this.setValue("MR_NO", "");
            return;
        }
        if (resultParm.getCount("APPLY_NO") == 1) {
            parm.setData("CASE_NO", resultParm.getValue("CASE_NO", 0));
        }
        else {
            TParm resultData = (TParm) openDialog(
                "%ROOT%\\config\\bms\\BMSQueryPat.x",
                parm);
            if (resultData == null) {
                return;
            }
            parm.setData("CASE_NO", resultData.getValue("CASE_NO"));
        }
        Object result = openDialog("%ROOT%\\config\\bms\\BMSApplyNo.x", parm);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return;
            }
            this.setValue("APPLY_NO", addParm.getValue("APPLY_NO"));
            onApplyNoAction();
        }
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        table_m = this.getTable("TABLE_M");
        table_d = this.getTable("TABLE_D");
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("TEST_DATE", date);
        ( (TMenuItem) getComponent("stock")).setEnabled(false);
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * ���ݼ��
     * @return boolean
     */
    private boolean CheckData() {
        if ("".equals(this.getValueString("APPLY_NO"))) {
            this.messageBox("��Ѫ���Ų���Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("MR_NO"))) {
            this.messageBox("�����Ų���Ϊ��");
            return false;
        }

        int count = table_m.getRowCount();
        if (count == 0) {
            this.messageBox("�����ڱ�������");
            return false;
        }
        
        // ���д���δ�����ʱ��������
        for (int i = 0; i < count; i++) {
            if ("".equals(table_m.getItemString(i, "RESULT"))) {
                this.messageBox("����δ�����ʱ��������");
                return false;
            }
        }

//        // ����Ѫ�� + ���������Ƿ����������
//        Map map = new HashMap();
//        for (int i = 0; i < count; i++) {
//            TParm parm = new TParm();
//            parm.setData("BLD_CODE",table_m.getItemString(i, "BLD_CODE"));
//            if (map.isEmpty()) {
//
//            }
//            else {
//
//            }
//        }
        return true;
    }
  //=========================  chenxi modify 201305
    /**
 	 * ����������֤
 	 * 
 	 * @return boolean
 	 */
 	public boolean checkPW() {
 		String bmsExe = "bmsExe";
 		String value = (String) this.openDialog(
 				"%ROOT%\\config\\inw\\passWordCheck.x", bmsExe); 
 		if (value == null) {
 			return false;
 		}
 		return value.equals("OK");
 	}	
}
