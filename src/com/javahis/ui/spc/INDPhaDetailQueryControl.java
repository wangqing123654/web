package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.javahis.system.textFormat.TextFormatINDOrg;
import com.dongyang.ui.TCheckBox;
import com.dongyang.util.StringTool; 

import java.sql.Timestamp;

import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;

import jdo.spc.INDSQL;
import jdo.sys.Operator;
import jdo.util.Manager;
import jdo.sys.SystemTool;    

import com.dongyang.ui.TComboBox;

import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: ������ϸ��
 * </p>
 *
 * <p> 
 * Description: ������ϸ��
 * </p>  
 *  
 * <p>  
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 *
 * @author zhangy 2009.09.22
 * @version 1.0
 */
public class INDPhaDetailQueryControl
    extends TControl {

    // ����TABLE
    private TTable table_m;
    // ������ϸTABLE
    private TTable table_d_a;         
    // �п���ϸTABLE
    private TTable table_d_b;   

    private Map map;

    public INDPhaDetailQueryControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

        // ��ʼ��ͳ������
        //luhai modify 2012-1-24 �޸Ŀ�ʼʱ��ͽ���ʱ�� begin
//        Timestamp date = TJDODBTool.getInstance().getDBTime();
//        // ����ʱ��(���µĵ�һ��)
//        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
//                                                     substring(0, 4) + "/" +
//                                                     TypeTool.getString(date).
//                                                     substring(5, 7) +
//                                                     "/01 00:00:00",
//                                                     "yyyy/MM/dd HH:mm:ss");
//        setValue("END_DATE", dateTime);
//        // ��ʼʱ��(�ϸ��µ�һ��)
//        setValue("START_DATE",
//                 StringTool.rollDate(dateTime, -1).toString().substring(0, 4) +
//                 "/" +
//                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
//                 "/01 00:00:00");
        setStartEndDate();
      //luhai modify 2012-1-24 �޸Ŀ�ʼʱ��ͽ���ʱ�� end
        table_m = this.getTable("TABLE_M");
        table_d_a = this.getTable("TABLE_D_A");
        table_d_b =  this.getTable("TABLE_D_B");

        //���״̬����
        map = new HashMap();
        map.put("VER", "����");
        map.put("RET", "�˿�");
        map.put("THI", "�������");
        map.put("DEP", "����");
        map.put("GIF", "����");
        map.put("THO", "��������"); 
        map.put("REG", "�˻�");
        map.put("FRO", "�̵�");
        map.put("O_RET", "������ҩ");
        map.put("E_RET", "������ҩ");
        map.put("I_RET", "סԺ��ҩ");
        map.put("DRT", "������ҩ");
        map.put("TEC", "��ҩ����");
        map.put("EXM", "���ұ�ҩ");
        map.put("WAS", "���");
        map.put("COS", "���Ĳ�����");
        map.put("O_DPN", "���﷢ҩ");
        map.put("E_DPN", "���﷢ҩ");
        map.put("I_DPN", "סԺ��ҩ");
    }
    /**
     *
     * ������ʼʱ��ͽ���ʱ�䣬����26-����25
     */
    private void setStartEndDate(){
    	Timestamp date = TJDODBTool.getInstance().getDBTime();
        // ����ʱ��(���µ�25)
        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
                                                     substring(0, 4) + "/" +
                                                     TypeTool.getString(date).
                                                     substring(5, 7) +
                                                     "/25 23:59:59",
                                                     "yyyy/MM/dd HH:mm:ss");
        setValue("END_DATE", dateTime);
        // ��ʼʱ��(�ϸ���26)
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(date.getTime());
        cd.add(Calendar.MONTH, -1);
        Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());
        setValue("START_DATE",
        		endDateTimestamp.toString().substring(0, 4) +
                 "/" +
                 endDateTimestamp.toString().substring(5, 7) +
                 "/26 00:00:00");
    }
    /**
     * ��ѯ����
     */
    public void onQuery() {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("��ѡ��ͳ�Ʋ���");
            return; 
        }
        String org_code = this.getValueString("ORG_CODE");
        String start_date = this.getValueString("START_DATE").substring(0, 4)
            + this.getValueString("START_DATE").substring(5, 7)
            + this.getValueString("START_DATE").substring(8, 10)
            + this.getValueString("START_DATE").substring(11, 13)
            + this.getValueString("START_DATE").substring(14, 16)
            + this.getValueString("START_DATE").substring(17, 19);
        String end_date = this.getValueString("END_DATE").substring(0, 4)
            + this.getValueString("END_DATE").substring(5, 7)     
            + this.getValueString("END_DATE").substring(8, 10)
            + this.getValueString("END_DATE").substring(11, 13)
            + this.getValueString("END_DATE").substring(14, 16)
            + this.getValueString("END_DATE").substring(17, 19);
        String qty_in = this.getValueString("CHECK_A");
        String qty_out = this.getValueString("CHECK_B");
        String qty_check = this.getValueString("CHECK_C");  
        String order_code = this.getValueString("ORDER_CODE");
           
        String drugLvl2 = this.getValueString("CHECK_D");
        
        // ҩƷ��ϸ�˻��ܲ�ѯ 		  
        String slqM = INDSQL.getINDPhaSumQuery(org_code, start_date,  
                                                   end_date,
                                                   order_code,drugLvl2);
        TParm parmM = new TParm(TJDODBTool.getInstance().select(slqM));

        // �������⣬�п��ѯ   
        if (getRadioButton("IND_ORG_A").isSelected()) {
            // ҩƷ��ϸ����ϸ��ѯ(����)
            String slqD_A = INDSQL.getINDPhaDetailDQueryA(org_code, start_date,
                end_date, qty_in, qty_out, qty_check, order_code,drugLvl2);
            TParm parmD_A = new TParm(TJDODBTool.getInstance().select(slqD_A));
            if (getRadioButton("TYPE_A").isSelected()) {
                if (parmM == null || parmM.getCount("ORDER_DESC") <= 0) {
                    table_m.removeRowAll();  
                    this.messageBox("�޲�ѯ���");
                }      
                else {
                    table_m.setParmValue(parmM);  
                    //luhai add ����ϼ���2012-2-22 begin
//                    addTotRowM();
                    //luhai add ����ϼ���2012-2-22 end
                }
            }
            else {
                if (parmD_A == null || parmD_A.getCount("ORDER_DESC") <= 0) {
                    table_d_a.removeRowAll();
                    this.messageBox("�޲�ѯ���");
                }
                else {
                    table_d_a.setParmValue(parmD_A);
                    addTotRowD();
                }
            }  
           setSumAmt();
        } 
        else {
            // ҩƷ��ϸ����ϸ��ѯ(�п�)
            String slqD_B = INDSQL.getINDPhaDetailDQueryB(org_code, start_date,
                end_date, qty_in, qty_out, qty_check, order_code,drugLvl2);   
            TParm parmD_B = new TParm(TJDODBTool.getInstance().select(slqD_B));

            if (getRadioButton("TYPE_A").isSelected()) {
                if (parmM == null || parmM.getCount("ORDER_DESC") <= 0) {
                    table_m.removeRowAll();
                    this.messageBox("�޲�ѯ���");
                }
                else {
                    table_m.setParmValue(parmM);
                    //luhai 2012-2-22 add totRow
//                    addTotRowM();
                }
                setSumAmt();
            } else {
                if (parmD_B == null || parmD_B.getCount("ORDER_DESC") <= 0) {
                    table_d_b.removeRowAll();
                    this.messageBox("�޲�ѯ���");
                }
                else {
                	double qty = 0;
                	double amt = 0;
                	double tot = 0;
                	TParm parm = new TParm();
                	String phaTransunitSql = "";
                	String sysUnitSql = "SELECT * FROM SYS_UNIT";
                	TParm sysUnitParm = new TParm(TJDODBTool.getInstance().select(sysUnitSql));
                	TParm phaTransunitParm = new TParm();
                	String stockUnit = "";
                	String dosageUnit = "";
                	double stockQty = 0;
                	double dosageQty = 0;
                	for(int i=0;i<parmD_B.getCount();i++){
            			//fux modify 20180917
                		String batchNo = parmD_B.getValue("BATCH_NO",i);
                		if(batchNo.length()>0){
                     		parm.addData("BATCH_NO",
                     				batchNo);
                		}else{  
                    		int batchSeq1 = parmD_B.getInt("BATCH_SEQ1",i); 
                			int batchSeq2 = parmD_B.getInt("BATCH_SEQ2",i); 
                			int batchSeq3 = parmD_B.getInt("BATCH_SEQ3",i); 
                			//fux modify 20180912 ���ӳ���������ʾ
                		    String searchBatch = "SELECT A.BATCH_NO FROM IND_STOCK A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE " +
                		    		" AND A.ORG_CODE='"+org_code+"'" +
                					" AND A.ORDER_CODE='"+parmD_B.getValue("ORDER_CODE",i)+"' " +
                					" AND A.BATCH_SEQ IN('"+batchSeq1+"','"+batchSeq2+"','"+batchSeq3+"')" +
                					" ORDER BY A.VALID_DATE DESC";
                		    //System.out.println("searchBatch:::"+searchBatch);
                			TParm searchBatchParm = new TParm(TJDODBTool.getInstance().select(searchBatch));
                			//������ϸ���� �������������ţ�����1,2,3 ������ʾ��
                			StringBuffer allBatchNo = new StringBuffer();
                			for (int j = 0; j < searchBatchParm.getCount("BATCH_NO"); j++) {
                				//System.out.println("+++:::"+searchBatchParm.getValue("BATCH_NO", j));
                			    if(searchBatchParm.getCount("BATCH_NO")==1){
                					allBatchNo.append(  
                						    searchBatchParm.getValue("BATCH_NO", j));
                			    }else{
                					allBatchNo.append(
                						    searchBatchParm.getValue("BATCH_NO", j)).append(",");
                			    }
                			}			
                			//String[] strs = phaDispenseNo.split(",");
                			//System.out.println("allBatchNo.toString():::"+allBatchNo.toString());
                	 		parm.addData("BATCH_NO",
                	 				allBatchNo.toString());
                		}
 
                		parm.addData("CHECK_DATE",
                				parmD_B.getValue("CHECK_DATE",
                                i).substring(0, 10));
                		parm.addData("STATUS",
	                                map.get(parmD_B.getValue("STATUS", i)));
                		parm.addData("ORDER_DESC",
	                		   parmD_B.getValue("ORDER_DESC", i));
                		parm.addData("SPECIFICATION",
	                		   parmD_B.getValue("SPECIFICATION", i));
                		parm.addData("QTY", parmD_B.getValue("QTY", i));
                		parm.addData("UNIT_CHN_DESC",
	                		   parmD_B.getValue("UNIT_CHN_DESC", i));
                		parm.addData("OWN_PRICE", parmD_B.getValue("OWN_PRICE", i));
                		parm.addData("AMT", parmD_B.getValue("AMT", i));
	                   	parm.addData("DEPT_CHN_DESC",
	                		   parmD_B.getValue("DEPT_CHN_DESC", i));
	                   	parm.addData("MR_NO", parmD_B.getValue("MR_NO", i));
	                   	parm.addData("PAT_NAME", parmD_B.getValue("PAT_NAME", i));
	                   	parm.addData("CASE_NO", parmD_B.getValue("CASE_NO", i));
	                    //20180223 add by liush
	                   	parm.addData("PHA_CHECK_CODE", parmD_B.getValue("PHA_CHECK_CODE", i));
	                   	parm.addData("PHA_CHECK_DATE", parmD_B.getValue("PHA_CHECK_DATE", i));
	                   	parm.addData("PHA_DOSAGE_CODE", parmD_B.getValue("PHA_DOSAGE_CODE", i));
	                   	parm.addData("PHA_DOSAGE_DATE", parmD_B.getValue("PHA_DOSAGE_DATE", i));
	                	parm.addData("PHA_DISPENSE_CODE", parmD_B.getValue("PHA_DISPENSE_CODE", i));
	                   	parm.addData("PHA_DISPENSE_DATE", parmD_B.getValue("PHA_DISPENSE_DATE", i));
	                   	parm.addData("PHA_RETN_CODE", parmD_B.getValue("PHA_RETN_CODE", i));
	                   	parm.addData("PHA_RETN_DATE", parmD_B.getValue("PHA_RETN_DATE", i));
                		qty += parmD_B.getDouble("QTY", i);
                		amt += parmD_B.getDouble("AMT", i);
                		tot += parmD_B.getDouble("AMT", i);
                		String qtyStr = "";
                		if(!parmD_B.getValue("STATUS", i).equals(parmD_B.getValue("STATUS", i+1))){
                			//fux modify 20180917
                	 		parm.addData("BATCH_NO","");
                			parm.addData("CHECK_DATE","С�ƣ�");
                			parm.addData("STATUS","");
                			parm.addData("ORDER_DESC","");  
                			parm.addData("SPECIFICATION","");
                			parm.addData("UNIT_CHN_DESC","");
                			parm.addData("OWN_PRICE","");
                			parm.addData("AMT", StringTool.round(amt, 7));
                			parm.addData("DEPT_CHN_DESC","");
                			parm.addData("MR_NO","");
                			parm.addData("PAT_NAME","");
                			parm.addData("CASE_NO","");
                			//20180223 add by liush
                			parm.addData("PHA_CHECK_CODE","");
    	                   	parm.addData("PHA_CHECK_DATE","");
    	                   	parm.addData("PHA_DOSAGE_CODE","");
    	                   	parm.addData("PHA_DOSAGE_DATE","");
    	                	parm.addData("PHA_DISPENSE_CODE","");
    	                   	parm.addData("PHA_DISPENSE_DATE","");
    	                   	parm.addData("PHA_RETN_CODE","");
    	                   	parm.addData("PHA_RETN_DATE","");
                			phaTransunitSql = "SELECT * FROM PHA_TRANSUNIT WHERE ORDER_CODE = '"+parmD_B.getValue("ORDER_CODE", i)+"'";
                			phaTransunitParm = new TParm(TJDODBTool.getInstance().select(phaTransunitSql));
                			if(phaTransunitParm.getValue("STOCK_UNIT", 0).equals(parmD_B.getValue("UNIT_CODE", i))){
                				parm.addData("QTY", StringTool.round(qty, 4)+parmD_B.getValue("UNIT_CHN_DESC", i));
                			}else{
                				for(int j=0; j<sysUnitParm.getCount(); j++){
                					if(sysUnitParm.getValue("UNIT_CODE", j).equals(phaTransunitParm.getValue("STOCK_UNIT", 0))){
                						stockUnit = sysUnitParm.getValue("UNIT_CHN_DESC", j);
                					}
                					if(sysUnitParm.getValue("UNIT_CODE", j).equals(phaTransunitParm.getValue("DOSAGE_UNIT", 0))){
                						dosageUnit = sysUnitParm.getValue("UNIT_CHN_DESC", j);
                					}
                				}
                				stockQty = qty/phaTransunitParm.getInt("DOSAGE_QTY", 0);
                				dosageQty = qty%phaTransunitParm.getInt("DOSAGE_QTY", 0);
                				if((int)Math.floor(stockQty) == 0){
                					qtyStr = dosageQty+dosageUnit;
                				}else if(dosageQty == 0){
                					qtyStr = (int)Math.floor(stockQty)+stockUnit;
                				}else{
                					qtyStr = (int)Math.floor(stockQty)+stockUnit+dosageQty+dosageUnit;
                				}
                				parm.addData("QTY", qtyStr);
                			}
            				qty = 0;
            				amt = 0;
                		}else{
                			if(!parmD_B.getValue("ORDER_CODE", i).equals(parmD_B.getValue("ORDER_CODE", i+1))){
                    			//fux modify 20180917
                    	 		parm.addData("BATCH_NO","");
                				parm.addData("CHECK_DATE","С�ƣ�");
                				parm.addData("STATUS","");
                				parm.addData("ORDER_DESC","");  
                				parm.addData("SPECIFICATION","");
                				parm.addData("UNIT_CHN_DESC","");
                				parm.addData("OWN_PRICE","");
                				parm.addData("AMT", StringTool.round(amt, 7));
                				parm.addData("DEPT_CHN_DESC","");
                    			parm.addData("MR_NO","");
                    			parm.addData("PAT_NAME","");
                    			parm.addData("CASE_NO","");
                    			//20180223 add by liush
                    			parm.addData("PHA_CHECK_CODE","");
        	                   	parm.addData("PHA_CHECK_DATE","");
        	                   	parm.addData("PHA_DOSAGE_CODE","");
        	                   	parm.addData("PHA_DOSAGE_DATE","");
        	                	parm.addData("PHA_DISPENSE_CODE","");
        	                   	parm.addData("PHA_DISPENSE_DATE","");
        	                   	parm.addData("PHA_RETN_CODE","");
        	                   	parm.addData("PHA_RETN_DATE","");
                				phaTransunitSql = "SELECT * FROM PHA_TRANSUNIT WHERE ORDER_CODE = '"+parmD_B.getValue("ORDER_CODE", i)+"'";
                    			phaTransunitParm = new TParm(TJDODBTool.getInstance().select(phaTransunitSql));
                    			if(phaTransunitParm.getValue("STOCK_UNIT", 0).equals(parmD_B.getValue("UNIT_CODE", i))){
                    				parm.addData("QTY", StringTool.round(qty, 4)+parmD_B.getValue("UNIT_CHN_DESC", i));
                    			}else{
                    				for(int j=0; j<sysUnitParm.getCount(); j++){
                    					if(sysUnitParm.getValue("UNIT_CODE", j).equals(phaTransunitParm.getValue("STOCK_UNIT", 0))){
                    						stockUnit = sysUnitParm.getValue("UNIT_CHN_DESC", j);
                    					}
                    					if(sysUnitParm.getValue("UNIT_CODE", j).equals(phaTransunitParm.getValue("DOSAGE_UNIT", 0))){
                    						dosageUnit = sysUnitParm.getValue("UNIT_CHN_DESC", j);
                    					}
                    				}
                    				stockQty = qty/phaTransunitParm.getInt("DOSAGE_QTY", 0);
                    				dosageQty = qty%phaTransunitParm.getInt("DOSAGE_QTY", 0);
                    				if((int)Math.floor(stockQty) == 0){
                    					qtyStr = dosageQty+dosageUnit;
                    				}else if(dosageQty == 0){
                    					qtyStr = (int)Math.floor(stockQty)+stockUnit;
                    				}else{
                    					qtyStr = (int)Math.floor(stockQty)+stockUnit+dosageQty+dosageUnit;
                    				}
                    				parm.addData("QTY", qtyStr);
                    			}
                				qty = 0;
                				amt = 0;
                			}
                		}
                	}
        			//fux modify 20180917
        	 		parm.addData("BATCH_NO","");
                	parm.addData("CHECK_DATE", "�ϼƣ�");
                	parm.addData("STATUS", "");
                	parm.addData("ORDER_DESC", "");
                	parm.addData("SPECIFICATION", "");
                	parm.addData("QTY", "");
                	parm.addData("UNIT_CHN_DESC", "");
                	parm.addData("OWN_PRICE","");
                	parm.addData("AMT", StringTool.round(tot, 7));
                	parm.addData("DEPT_CHN_DESC","");
        			parm.addData("MR_NO","");
        			parm.addData("PAT_NAME","");
        			parm.addData("CASE_NO","");
        			//20180223 add by liush
        			parm.addData("PHA_CHECK_CODE","");
                   	parm.addData("PHA_CHECK_DATE","");
                   	parm.addData("PHA_DOSAGE_CODE","");
                   	parm.addData("PHA_DOSAGE_DATE","");
                	parm.addData("PHA_DISPENSE_CODE","");
                   	parm.addData("PHA_DISPENSE_DATE","");
                   	parm.addData("PHA_RETN_CODE","");
                   	parm.addData("PHA_RETN_DATE","");
                	parm.setCount(parm.getCount("CHECK_DATE"));
                    table_d_b.setParmValue(parm);
                    this.setValue("SUM_AMT", StringTool.round(tot, 7));
                    
                    //����ϼ���
//                    addTotRowD();
                }
            }
            
        }
    }
    
    private void addTotRowM(){
        TParm tableParm = table_m.getParmValue();
        TParm parm = new TParm();
        //ҩƷ���˼���ϼƹ��� begin
        double totLastTotAMT=0;
        double totStockOutAMT=0;
        double totStockInAMT=0;
        double totModiyAMT=0;
        double totTotAMT=0;
        for (int i = 0; i < table_m.getRowCount(); i++) {
            parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
            parm.addData("SPECIFICATION",
                         tableParm.getValue("SPECIFICATION", i));
            parm.addData("LAST_TOTSTOCK_QTY",
                         tableParm.getValue("LAST_TOTSTOCK_QTY", i));
            parm.addData("UNIT_CHN_DESC",
                         tableParm.getValue("UNIT_CHN_DESC", i));
            parm.addData("LAST_TOTSTOCK_AMT",
                         tableParm.getValue("LAST_TOTSTOCK_AMT", i));
            totLastTotAMT+=Double.parseDouble(tableParm.getValue("LAST_TOTSTOCK_AMT", i));
            parm.addData("STOCKIN_QTY", tableParm.getValue("STOCKIN_QTY", i));
            parm.addData("STOCKIN_AMT", tableParm.getValue("STOCKIN_AMT", i));
            totStockInAMT+=Double.parseDouble(tableParm.getValue("STOCKIN_AMT", i));
            parm.addData("STOCKOUT_QTY",
                         tableParm.getValue("STOCKOUT_QTY", i));
            parm.addData("STOCKOUT_AMT",
                         tableParm.getValue("STOCKOUT_AMT", i));
            totStockOutAMT+=Double.parseDouble(tableParm.getValue("STOCKOUT_AMT", i));
            parm.addData("CHECKMODI_QTY",
                         tableParm.getValue("CHECKMODI_QTY", i));
            parm.addData("CHECKMODI_AMT",
                         tableParm.getValue("CHECKMODI_AMT", i));
            totModiyAMT+=Double.parseDouble(tableParm.getValue("CHECKMODI_AMT", i));
            parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
            parm.addData("STOCK_AMT", tableParm.getValue("STOCK_AMT", i));
            totTotAMT+=Double.parseDouble(tableParm.getValue("STOCK_AMT", i));
        }
        //����ϼ���
        parm.addData("ORDER_DESC","�ϼƣ�");
        parm.addData("SPECIFICATION",
                    "");
        parm.addData("LAST_TOTSTOCK_QTY",
                     "");
        parm.addData("UNIT_CHN_DESC",
                     "");
        parm.addData("LAST_TOTSTOCK_AMT",
        		totLastTotAMT );
        parm.addData("STOCKIN_QTY","");
        parm.addData("STOCKIN_AMT", StringTool.round(totStockInAMT,2));
        parm.addData("STOCKOUT_QTY",
                     "");
        parm.addData("STOCKOUT_AMT",
        		StringTool.round(totStockOutAMT,2));
        parm.addData("CHECKMODI_QTY",
                     "");
        parm.addData("CHECKMODI_AMT",
        		StringTool.round(totModiyAMT,2));
        parm.addData("STOCK_QTY", "");
        parm.addData("STOCK_AMT",StringTool.round(totTotAMT,2));
        //����ϼ���end
        parm.setCount(parm.getCount("ORDER_DESC"));
        this.table_m.setParmValue(parm);
    }
    private void addTotRowD(){
    	TParm parm=new TParm();
    	double sum_status_amt = 0;
    	//liuyalin 20170620 add ��������С��
    	double sum_status_qty = 0;
        if (this.getRadioButton("IND_ORG_A").isSelected()) {
            //�����ܼ���Ϣ begin
            double totAmt = 0;
            TParm tableParm = table_d_a.getParmValue();      
            for (int i = 0; i < table_d_a.getRowCount(); i++) {
            	if(tableParm.getValue("STATUS", i).equals("DEP")){
            		totAmt =totAmt + Double.parseDouble(tableParm.getValue("AMT", i));
            	}
            	
	            if(tableParm.getValue("STATUS", i).equals("RET")){
	            	totAmt =totAmt -Double.parseDouble(tableParm.getValue("AMT", i));
            	}  
            	
                parm.addData("CHECK_DATE",
                             tableParm.getValue("CHECK_DATE",
                             i).substring(0, 10));
                parm.addData("STATUS",
                             tableParm.getValue("STATUS", i));
                parm.addData("ORDER_DESC",
                             tableParm.getValue("ORDER_DESC", i));
                parm.addData("SPECIFICATION",
                             tableParm.getValue("SPECIFICATION", i));
                parm.addData("QTY", tableParm.getValue("QTY", i));
                parm.addData("UNIT_CHN_DESC",
                             tableParm.getValue("UNIT_CHN_DESC", i));
                parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
                parm.addData("AMT", tableParm.getValue("AMT", i));            
                //����-�˻�     
                //totAmt+=Double.parseDouble(tableParm.getValue("AMT", i));
                parm.addData("ORG_CHN_DESC",
                             tableParm.getValue("ORG_CHN_DESC", i));
                parm.addData("APP_ORG_CHN_DESC",
                        tableParm.getValue("APP_ORG_CHN_DESC", i));
                sum_status_amt += Double.parseDouble(tableParm.getValue("AMT", i));
                //liuyalin 20170620 add ��������С��
                sum_status_qty += Double.parseDouble(tableParm.getValue("QTY", i));
                if (!tableParm.getValue("STATUS", i).equals(tableParm.getValue("STATUS", i+1))) {
                	parm.addData("CHECK_DATE","С�ƣ�");
		            parm.addData("STATUS","");
		            parm.addData("ORDER_DESC","");  
		            parm.addData("SPECIFICATION","");
		            //liuyalin 20170620 modify ��������С��
		            parm.addData("QTY", StringTool.round(sum_status_qty, 4));
		            //parm.addData("QTY", "");
		            parm.addData("UNIT_CHN_DESC","");
		            parm.addData("OWN_PRICE","");
		            parm.addData("AMT", StringTool.round(sum_status_amt, 7));
		            parm.addData("ORG_CHN_DESC","");
		            parm.addData("APP_ORG_CHN_DESC","");
		            sum_status_amt = 0;
		            sum_status_qty = 0;
				}
            }
            //����ϼ���Ϣ begin
            parm.addData("CHECK_DATE",
            		"�ϼƣ�");
            parm.addData("STATUS",
            		"");
            parm.addData("ORDER_DESC",
            		"");
            parm.addData("SPECIFICATION",
            		"");
            parm.addData("QTY", "");
            parm.addData("UNIT_CHN_DESC",
            		"");
            parm.addData("OWN_PRICE","");
            parm.addData("AMT", StringTool.round(totAmt, 7));
            parm.addData("ORG_CHN_DESC",
            		"");
            parm.addData("APP_ORG_CHN_DESC",
    		"");

            //����ϼ���Ϣ end
            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "CHECK_DATE");
            parm.addData("SYSTEM", "COLUMNS", "STATUS");
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");  
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "QTY");
            parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
            parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");  
            parm.addData("SYSTEM", "COLUMNS", "AMT");
            parm.addData("SYSTEM", "COLUMNS", "ORG_CHN_DESC");
            parm.addData("SYSTEM", "COLUMNS", "APP_ORG_CHN_DESC");
            table_d_a.setParmValue(parm);
        }
        else {
            //�����ܼ���Ϣ
            double totAmt = 0;
            TParm tableParm = table_d_b.getParmValue();
            for (int i = 0; i < table_d_b.getRowCount(); i++) {
                parm.addData("CHECK_DATE",
                             tableParm.getValue("CHECK_DATE",
                             i).substring(0, 10));
                parm.addData("STATUS",
                             map.get(tableParm.getValue("STATUS", i)));
                parm.addData("ORDER_DESC",
                             tableParm.getValue("ORDER_DESC", i));
                parm.addData("SPECIFICATION",
                             tableParm.getValue("SPECIFICATION", i));
                parm.addData("QTY", tableParm.getValue("QTY", i));
                parm.addData("UNIT_CHN_DESC",
                             tableParm.getValue("UNIT_CHN_DESC", i));
                parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
                parm.addData("AMT", tableParm.getValue("AMT", i));
                totAmt+=Double.parseDouble(tableParm.getValue("AMT", i));
                parm.addData("DEPT_CHN_DESC",
                             tableParm.getValue("DEPT_CHN_DESC", i));
                parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
                parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
                parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
                // 20180223 add by liush 
                parm.addData("PHA_CHECK_CODE", tableParm.getValue("PHA_CHECK_CODE", i));
                parm.addData("PHA_CHECK_DATE", tableParm.getValue("PHA_CHECK_DATE", i));
                parm.addData("PHA_DOSAGE_CODE", tableParm.getValue("PHA_DOSAGE_CODE", i));
                parm.addData("PHA_DOSAGE_DATE", tableParm.getValue("PHA_DOSAGE_DATE", i));
                parm.addData("PHA_DISPENSE_CODE", tableParm.getValue("PHA_DISPENSE_CODE", i));
                parm.addData("PHA_DISPENSE_DATE", tableParm.getValue("PHA_DISPENSE_DATE", i));
                parm.addData("PHA_RETN_CODE", tableParm.getValue("PHA_RETN_CODE", i));
                parm.addData("PHA_RETN_DATE", tableParm.getValue("PHA_RETN_DATE", i));
                sum_status_amt += Double.parseDouble(tableParm.getValue("AMT", i));
                //liuyalin 20170620 add ��������С��
                sum_status_qty += Double.parseDouble(tableParm.getValue("QTY", i));
                if (!tableParm.getValue("STATUS", i).equals(tableParm.getValue("STATUS", i+1))) {
                	parm.addData("CHECK_DATE","С�ƣ�");
		            parm.addData("STATUS","");
		            parm.addData("ORDER_DESC","");
		            parm.addData("SPECIFICATION","");
		            //liuyalin 20170620 modify ��������С��
		            parm.addData("QTY", StringTool.round(sum_status_qty, 4));
//		            parm.addData("QTY", "");
		            parm.addData("UNIT_CHN_DESC","");
		            parm.addData("OWN_PRICE","");
		            parm.addData("AMT", StringTool.round(sum_status_amt, 7));
		            parm.addData("DEPT_CHN_DESC","");
		            parm.addData("MR_NO","");
		            parm.addData("PAT_NAME","");
		            parm.addData("CASE_NO","");
		            //20180223 add by liush
		            parm.addData("PHA_CHECK_CODE", "");
		            parm.addData("PHA_CHECK_DATE", "");
		            parm.addData("PHA_DOSAGE_CODE", "");
		            parm.addData("PHA_DOSAGE_DATE", "");
		            parm.addData("PHA_DISPENSE_CODE", "");
		            parm.addData("PHA_DISPENSE_DATE", "");
		            parm.addData("PHA_RETN_CODE", "");
		            parm.addData("PHA_RETN_DATE", "");
		            sum_status_amt = 0;
				}
            }
            //����ϼ���
	           parm.addData("CHECK_DATE","�ϼƣ�");
	           parm.addData("STATUS",
	                       "");
	           parm.addData("ORDER_DESC",
	                       "");
	           parm.addData("SPECIFICATION",
	                        "");
	           parm.addData("QTY", "");
	           parm.addData("UNIT_CHN_DESC",
	                       "");
	           parm.addData("OWN_PRICE","");
	           parm.addData("AMT", StringTool.round(totAmt,7));
	           parm.addData("DEPT_CHN_DESC",
	                        "");
	           parm.addData("MR_NO","");
	           parm.addData("PAT_NAME","");
	           parm.addData("CASE_NO","");
	           //20180223 add by liush
	           parm.addData("PHA_CHECK_CODE", "");
	           parm.addData("PHA_CHECK_DATE", "");
	           parm.addData("PHA_DOSAGE_CODE", "");
	           parm.addData("PHA_DOSAGE_DATE", "");
	           parm.addData("PHA_DISPENSE_CODE", "");
	           parm.addData("PHA_DISPENSE_DATE", "");
	           parm.addData("PHA_RETN_CODE", "");
	           parm.addData("PHA_RETN_DATE", "");
	           //add �ϼ���end
	           parm.setCount(parm.getCount("CHECK_DATE"));
	           table_d_b.setParmValue(parm);
        }

    }
    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "ORG_CODE;ORDER_CODE;ORDER_DESC;SUM_AMT";
        this.clearValue(clearStr);
        this.getRadioButton("IND_ORG_A").setSelected(true);
        onChangeOrgType();
        this.getRadioButton("TYPE_A").setSelected(true);
        onChangeInfoType();
        this.getCheckBox("CHECK_A").setSelected(true);
        this.getCheckBox("CHECK_B").setSelected(true);
        this.getCheckBox("CHECK_C").setSelected(true);
        //luhai modify 2012-1-24 ���ó�ʼ��ʱ��Ĺ��÷��� begin
//        // ��ʼ��ͳ������
//        Timestamp date = TJDODBTool.getInstance().getDBTime();
//        // ����ʱ��(���µĵ�һ��)
//        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
//            substring(0, 4) + "/" +
//            TypeTool.getString(date).
//            substring(5, 7) +
//            "/01 00:00:00",
//            "yyyy/MM/dd HH:mm:ss");
//        setValue("END_DATE", dateTime);
//        // ��ʼʱ��(�ϸ��µ�һ��)
//        setValue("START_DATE",
//                 StringTool.rollDate(dateTime, -1).toString().substring(0, 4) +
//                 "/" +
//                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
//                 "/01 00:00:00");
        //��ʼ����ѯʱ��
        setStartEndDate();
        //luhai modify 2012-1-24 ���ó�ʼ��ʱ��Ĺ��÷��� end
        table_m.removeRowAll();
        table_d_a.removeRowAll();
        table_d_b.removeRowAll();
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
    	 //*********************************************
    	//ҩ����ϸ�˼���ϼƹ��� luhai begin 2012-2-22
    	//*********************************************
        // ��ӡ����
        TParm date = new TParm();
        date.setData("ORG_CODE", "TEXT", "ͳ�Ʋ���: " +
                     getTextFormat("ORG_CODE").getText());
        String start_date = getValueString("START_DATE");
        String end_date = getValueString("END_DATE");
        date.setData("DATE_AREA", "TEXT", "ͳ������: " +
                     start_date.substring(0, 4) + "/" +
                     start_date.substring(5, 7) + "/" +
                     start_date.substring(8, 10) + " " +
                     start_date.substring(11, 13) + ":" +
                     start_date.substring(14, 16) + ":" +
                     start_date.substring(17, 19) + " ~ " +
                     end_date.substring(0, 4) + "/" +
                     end_date.substring(5, 7) + "/" +
                     end_date.substring(8, 10) + " " +
                     end_date.substring(11, 13) + ":" +
                     end_date.substring(14, 16) + ":" +
                     end_date.substring(17, 19) );
        date.setData("DATE", "TEXT", "�Ʊ�ʱ��: " +
                     SystemTool.getInstance().getDate().toString().
                     substring(0, 10).replace('-', '/'));
        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());

        // �������
        TParm parm = new TParm();
        if (this.getRadioButton("TYPE_A").isSelected()) {
            //����
            if (this.getRadioButton("IND_ORG_A").isSelected()) {
                //ҩ��
                date.setData("TITLE", "TEXT", Manager.getOrganization().
                             getHospitalCHNFullName(Operator.getRegion()) +
                             "ҩ��ҩƷ����");
            }
            else {
                //ҩ��
                date.setData("TITLE", "TEXT", Manager.getOrganization().
                             getHospitalCHNFullName(Operator.getRegion()) +
                             "ҩ��ҩƷ����");
            }
            TParm tableParm = table_m.getParmValue();
            double sum_amt = 0;//add by shendr 20131217 ���������ͳһ  �����������ټ���
            for (int i = 0; i < table_m.getRowCount(); i++) {
                parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
                parm.addData("SPECIFICATION",
                             tableParm.getValue("SPECIFICATION", i));
                parm.addData("LAST_TOTSTOCK_QTY",
                             tableParm.getValue("LAST_TOTSTOCK_QTY", i));
                parm.addData("UNIT_CHN_DESC",
                             tableParm.getValue("UNIT_CHN_DESC", i));
                // <---  identity by shendr 20131217 ���������ͳһ  �����������ټ���
                //parm.addData("LAST_TOTSTOCK_AMT",
                //             tableParm.getValue("LAST_TOTSTOCK_AMT", i));
                parm.addData("LAST_TOTSTOCK_AMT",
                		StringTool.round(tableParm.getDouble("LAST_TOTSTOCK_AMT", i),2));
                parm.addData("STOCKIN_QTY", tableParm.getValue("STOCKIN_QTY", i));
                //parm.addData("STOCKIN_AMT", tableParm.getValue("STOCKIN_AMT", i));
                parm.addData("STOCKIN_AMT", StringTool.round(tableParm.getDouble("STOCKIN_AMT", i),2));
                parm.addData("STOCKOUT_QTY",
                             tableParm.getValue("STOCKOUT_QTY", i));
                //parm.addData("STOCKOUT_AMT",
                //             tableParm.getValue("STOCKOUT_AMT", i));
                parm.addData("STOCKOUT_AMT",
                		StringTool.round(tableParm.getDouble("STOCKOUT_AMT", i),2));
                parm.addData("CHECKMODI_QTY",
                             tableParm.getValue("CHECKMODI_QTY", i));
                //parm.addData("CHECKMODI_AMT",
                //             tableParm.getValue("CHECKMODI_AMT", i));
                parm.addData("CHECKMODI_AMT",
                		StringTool.round(tableParm.getDouble("CHECKMODI_AMT", i),2));
                parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
                //parm.addData("STOCK_AMT", tableParm.getValue("STOCK_AMT", i));
                parm.addData("STOCK_AMT", StringTool.round(tableParm.getDouble("STOCK_AMT", i),2));
                sum_amt+=StringTool.round(tableParm.getDouble("STOCK_AMT", i),2);
                // ---->
            }
            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "LAST_TOTSTOCK_QTY");
            parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
            parm.addData("SYSTEM", "COLUMNS", "LAST_TOTSTOCK_AMT");
            parm.addData("SYSTEM", "COLUMNS", "STOCKIN_QTY");
            parm.addData("SYSTEM", "COLUMNS", "STOCKIN_AMT");
            parm.addData("SYSTEM", "COLUMNS", "STOCKOUT_QTY");
            parm.addData("SYSTEM", "COLUMNS", "STOCKOUT_AMT");
            parm.addData("SYSTEM", "COLUMNS", "CHECKMODI_QTY");
            parm.addData("SYSTEM", "COLUMNS", "CHECKMODI_AMT");
            parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
            parm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
            date.setData("TABLE", parm.getData());
            // ��β����
            //<--- identity by shendr 20131217
            //date.setData("SUM_AMT", "TEXT",
            //             "�ܽ� " + StringTool.round(getValueDouble("SUM_AMT"),2));//luhai add �������� 2012-2-13
            date.setData("SUM_AMT", "TEXT", "�ܽ� " + sum_amt);
            //--->
            // ���ô�ӡ����
            this.openPrintWindow(
                "%ROOT%\\config\\prt\\spc\\INDPhaDetailQueryA.jhw", date);
        }
        else {
            if (this.getRadioButton("IND_ORG_A").isSelected()) {
                //ҩ����ϸ
                date.setData("TITLE", "TEXT", Manager.getOrganization().
                             getHospitalCHNFullName(Operator.getRegion()) +
                             "ҩ��ҩƷ��ϸ��");
                TParm tableParm = table_d_a.getParmValue();
//                System.out.println("---   PRINT ҩ��ҩƷ��ϸ��-"+tableParm);
                double sum_amt = 0;//add by shendr 20131217 ���������ͳһ  �����������ټ���
                for (int i = 0; i < table_d_a.getRowCount(); i++) {
                	if(tableParm.getValue("CHECK_DATE",
                            i).length()>11){
                		parm.addData("CHECK_DATE",
                				tableParm.getValue("CHECK_DATE",
                						i).substring(0, 10));
                	}else{
                		parm.addData("CHECK_DATE",
                				tableParm.getValue("CHECK_DATE",
                						i));
                	}
                	String key = tableParm.getValue("STATUS", i);
//                	System.out.println("----------key:"+key);
//                	System.out.println("------------map>"+map.get(key));
//                    parm.addData("STATUS",
//                                 map.get(tableParm.getValue("STATUS", i)));
                	parm.addData("STATUS", tableParm.getValue("STATUS", i));
                    parm.addData("ORDER_DESC",
                                 tableParm.getValue("ORDER_DESC", i));
                    parm.addData("SPECIFICATION",
                                 tableParm.getValue("SPECIFICATION", i));
                    parm.addData("QTY", tableParm.getValue("QTY", i));
                    parm.addData("UNIT_CHN_DESC",
                                 tableParm.getValue("UNIT_CHN_DESC", i));
                    parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
                    //<--- identity by shendr 20131217
                    //parm.addData("AMT", tableParm.getValue("AMT", i));
                    parm.addData("AMT", StringTool.round(tableParm.getDouble("AMT", i),2));
                    sum_amt += StringTool.round(tableParm.getDouble("AMT", i),2);
                    //--->
                    parm.addData("ORG_CHN_DESC",
                                 tableParm.getValue("ORG_CHN_DESC", i));
                }
                parm.setCount(parm.getCount("ORDER_DESC"));
                parm.addData("SYSTEM", "COLUMNS", "CHECK_DATE");
                parm.addData("SYSTEM", "COLUMNS", "STATUS");
                parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
                parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
                parm.addData("SYSTEM", "COLUMNS", "QTY");
                parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
                parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
                parm.addData("SYSTEM", "COLUMNS", "AMT");
                parm.addData("SYSTEM", "COLUMNS", "ORG_CHN_DESC");
                date.setData("TABLE", parm.getData());
                // ��β����
                //<--- identity by shendr 20131217
                //date.setData("SUM_AMT", "TEXT",
                //             "�ܽ� " + StringTool.round(getValueDouble("SUM_AMT"),2));//luhai 2012-2-13 ��������
                date.setData("SUM_AMT", "TEXT", "�ܽ� " + sum_amt);
                //--->
                // ���ô�ӡ����
                this.openPrintWindow(
                    "%ROOT%\\config\\prt\\spc\\INDPhaDetailQueryB.jhw", date);
            }
            else {
                //ҩ����ϸ
                date.setData("TITLE", "TEXT", Manager.getOrganization().
                             getHospitalCHNFullName(Operator.getRegion()) +
                             "ҩ��ҩƷ��ϸ��");
                TParm tableParm = table_d_b.getParmValue();
                double sum_amt = 0;//add by shendr 20131217 ���������ͳһ  �����������ټ���
                for (int i = 0; i < table_d_b.getRowCount(); i++) {
                	if(tableParm.getValue("CHECK_DATE",
                            i).length()>=11){
                		parm.addData("CHECK_DATE",
                				tableParm.getValue("CHECK_DATE",
                						i).substring(0, 10));
                	}else{
                		parm.addData("CHECK_DATE",
                				tableParm.getValue("CHECK_DATE",
                						i));
                	}
//                    parm.addData("STATUS",
//                                 map.get(tableParm.getValue("STATUS", i)));
                	parm.addData("STATUS", tableParm.getValue("STATUS", i));
                    System.out.println("zhaolingling------"+tableParm.getValue("STATUS", i));
                    parm.addData("ORDER_DESC",
                                 tableParm.getValue("ORDER_DESC", i));
                    parm.addData("SPECIFICATION",
                                 tableParm.getValue("SPECIFICATION", i));
                    parm.addData("QTY", tableParm.getValue("QTY", i));
                    parm.addData("UNIT_CHN_DESC",
                                 tableParm.getValue("UNIT_CHN_DESC", i));
                    parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
                    //<--- identity by shendr 20131217
                    //parm.addData("AMT", tableParm.getValue("AMT", i));
                    parm.addData("AMT", StringTool.round(tableParm.getDouble("AMT", i),2));
                    sum_amt += StringTool.round(tableParm.getDouble("AMT", i),2);
                    //--->
                    parm.addData("DEPT_CHN_DESC",
                                 tableParm.getValue("DEPT_CHN_DESC", i));
                    parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
                    parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
                    parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
                    //20180223 add by liush
		            parm.addData("PHA_CHECK_CODE", tableParm.getValue("PHA_CHECK_CODE", i));
		            parm.addData("PHA_CHECK_DATE", tableParm.getValue("PHA_CHECK_DATE", i));
		            parm.addData("PHA_DOSAGE_CODE", tableParm.getValue("PHA_DOSAGE_CODE", i));
		            parm.addData("PHA_DOSAGE_DATE", tableParm.getValue("PHA_DOSAGE_DATE", i));
		            parm.addData("PHA_DISPENSE_CODE", tableParm.getValue("PHA_DISPENSE_CODE", i));
		            parm.addData("PHA_DISPENSE_DATE", tableParm.getValue("PHA_DISPENSE_DATE", i));
		            parm.addData("PHA_RETN_CODE", tableParm.getValue("PHA_RETN_CODE", i));
		            parm.addData("PHA_RETN_DATE", tableParm.getValue("PHA_RETN_DATE", i));
                }
                parm.setCount(parm.getCount("ORDER_DESC"));
                parm.addData("SYSTEM", "COLUMNS", "CHECK_DATE");
                parm.addData("SYSTEM", "COLUMNS", "STATUS");
                parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
                parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
                parm.addData("SYSTEM", "COLUMNS", "QTY");
                parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
                parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
                parm.addData("SYSTEM", "COLUMNS", "AMT");
                parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
                parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
                parm.addData("SYSTEM", "COLUMNS", "MR_NO");
                parm.addData("SYSTEM", "COLUMNS", "CASE_NO");
                // 20180223 add by liush
                parm.addData("SYSTEM", "COLUMNS", "PHA_CHECK_CODE");
                parm.addData("SYSTEM", "COLUMNS", "PHA_CHECK_DATE");
                parm.addData("SYSTEM", "COLUMNS", "PHA_DOSAGE_CODE");
                parm.addData("SYSTEM", "COLUMNS", "PHA_DOSAGE_DATE");
                parm.addData("SYSTEM", "COLUMNS", "PHA_DISPENSE_CODE");
                parm.addData("SYSTEM", "COLUMNS", "PHA_DISPENSE_DATE");
                parm.addData("SYSTEM", "COLUMNS", "PHA_RETN_CODE");
                parm.addData("SYSTEM", "COLUMNS", "PHA_RETN_DATE");
                date.setData("TABLE", parm.getData());
                // ��β����
                //<--- identity by shendr 20131217
                //date.setData("SUM_AMT", "TEXT",
                //             "�ܽ� " + StringTool.round(getValueDouble("SUM_AMT"),2));//luhai 2012-2-13 ��������
                date.setData("SUM_AMT", "TEXT", "�ܽ� " + sum_amt);
                //--->
                // ���ô�ӡ����
                this.openPrintWindow(
                    "%ROOT%\\config\\prt\\spc\\INDPhaDetailQueryC.jhw", date);
            }
        }
//        // ��ӡ����
//        TParm date = new TParm();
//        date.setData("ORG_CODE", "TEXT", "ͳ�Ʋ���: " +
//                     getTextFormat("ORG_CODE").getText());
//        String start_date = getValueString("START_DATE");
//        String end_date = getValueString("END_DATE");
//        date.setData("DATE_AREA", "TEXT", "ͳ������: " +
//                     start_date.substring(0, 4) + "/" +
//                     start_date.substring(5, 7) + "/" +
//                     start_date.substring(8, 10) + " " +
//                     start_date.substring(11, 13) + ":" +
//                     start_date.substring(14, 16) + ":" +
//                     start_date.substring(17, 19) + " ~ " +
//                     end_date.substring(0, 4) + "/" +
//                     end_date.substring(5, 7) + "/" +
//                     end_date.substring(8, 10) + " " +
//                     end_date.substring(11, 13) + ":" +
//                     end_date.substring(14, 16) + ":" +
//                     end_date.substring(17, 19) );
//        date.setData("DATE", "TEXT", "�Ʊ�ʱ��: " +
//                     SystemTool.getInstance().getDate().toString().
//                     substring(0, 10).replace('-', '/'));
//        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
//
//        // �������
//        TParm parm = new TParm();
//        if (this.getRadioButton("TYPE_A").isSelected()) {
//            //����
//            if (this.getRadioButton("IND_ORG_A").isSelected()) {
//                //ҩ��
//                date.setData("TITLE", "TEXT", Manager.getOrganization().
//                             getHospitalCHNFullName(Operator.getRegion()) +
//                             "ҩ��ҩƷ����");
//            }
//            else {
//                //ҩ��
//                date.setData("TITLE", "TEXT", Manager.getOrganization().
//                             getHospitalCHNFullName(Operator.getRegion()) +
//                             "ҩ��ҩƷ����");
//            }
//            TParm tableParm = table_m.getParmValue();
//            //ҩƷ���˼���ϼƹ��� begin
//            double totLastTotAMT=0;
//            double totStockOutAMT=0;
//            double totStockInAMT=0;
//            double totModiyAMT=0;
//            double totTotAMT=0;
//            for (int i = 0; i < table_m.getRowCount(); i++) {
//                parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
//                parm.addData("SPECIFICATION",
//                             tableParm.getValue("SPECIFICATION", i));
//                parm.addData("LAST_TOTSTOCK_QTY",
//                             tableParm.getValue("LAST_TOTSTOCK_QTY", i));
//                parm.addData("UNIT_CHN_DESC",
//                             tableParm.getValue("UNIT_CHN_DESC", i));
//                parm.addData("LAST_TOTSTOCK_AMT",
//                             tableParm.getValue("LAST_TOTSTOCK_AMT", i));
//                totLastTotAMT+=Double.parseDouble(tableParm.getValue("LAST_TOTSTOCK_AMT", i));
//                parm.addData("STOCKIN_QTY", tableParm.getValue("STOCKIN_QTY", i));
//                parm.addData("STOCKIN_AMT", tableParm.getValue("STOCKIN_AMT", i));
//                totStockInAMT+=Double.parseDouble(tableParm.getValue("STOCKIN_AMT", i));
//                parm.addData("STOCKOUT_QTY",
//                             tableParm.getValue("STOCKOUT_QTY", i));
//                parm.addData("STOCKOUT_AMT",
//                             tableParm.getValue("STOCKOUT_AMT", i));
//                totStockOutAMT+=Double.parseDouble(tableParm.getValue("STOCKOUT_AMT", i));
//                parm.addData("CHECKMODI_QTY",
//                             tableParm.getValue("CHECKMODI_QTY", i));
//                parm.addData("CHECKMODI_AMT",
//                             tableParm.getValue("CHECKMODI_AMT", i));
//                totModiyAMT+=Double.parseDouble(tableParm.getValue("CHECKMODI_AMT", i));
//                parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
//                parm.addData("STOCK_AMT", tableParm.getValue("STOCK_AMT", i));
//                totTotAMT+=Double.parseDouble(tableParm.getValue("STOCK_AMT", i));
//            }
//            //����ϼ���
//            parm.addData("ORDER_DESC","�ϼƣ�");
//            parm.addData("SPECIFICATION",
//                        "");
//            parm.addData("LAST_TOTSTOCK_QTY",
//                         "");
//            parm.addData("UNIT_CHN_DESC",
//                         "");
//            parm.addData("LAST_TOTSTOCK_AMT",
//            		totLastTotAMT );
//            parm.addData("STOCKIN_QTY","");
//            parm.addData("STOCKIN_AMT", StringTool.round(totStockInAMT,2));
//            parm.addData("STOCKOUT_QTY",
//                         "");
//            parm.addData("STOCKOUT_AMT",
//            		StringTool.round(totStockOutAMT,2));
//            parm.addData("CHECKMODI_QTY",
//                         "");
//            parm.addData("CHECKMODI_AMT",
//            		StringTool.round(totModiyAMT,2));
//            parm.addData("STOCK_QTY", "");
//            parm.addData("STOCK_AMT",StringTool.round(totTotAMT,2));
//            //����ϼ���end
//            parm.setCount(parm.getCount("ORDER_DESC"));
//            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//            parm.addData("SYSTEM", "COLUMNS", "LAST_TOTSTOCK_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
//            parm.addData("SYSTEM", "COLUMNS", "LAST_TOTSTOCK_AMT");
//            parm.addData("SYSTEM", "COLUMNS", "STOCKIN_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "STOCKIN_AMT");
//            parm.addData("SYSTEM", "COLUMNS", "STOCKOUT_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "STOCKOUT_AMT");
//            parm.addData("SYSTEM", "COLUMNS", "CHECKMODI_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "CHECKMODI_AMT");
//            parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
//            date.setData("TABLE", parm.getData());
//            // ��β����
//            date.setData("SUM_AMT", "TEXT",
//                         "�ܽ� " + StringTool.round(getValueDouble("SUM_AMT"),2));//luhai add �������� 2012-2-13
//            // ���ô�ӡ����
//            this.openPrintWindow(
//                "%ROOT%\\config\\prt\\spc\\INDPhaDetailQueryA.jhw", date);
//        }
//        else {
//            if (this.getRadioButton("IND_ORG_A").isSelected()) {
//                //ҩ����ϸ
//                date.setData("TITLE", "TEXT", Manager.getOrganization().
//                             getHospitalCHNFullName(Operator.getRegion()) +
//                             "ҩ��ҩƷ��ϸ��");
//                //�����ܼ���Ϣ begin
//                double totAmt = 0;
//                TParm tableParm = table_d_a.getParmValue();
//                for (int i = 0; i < table_d_a.getRowCount(); i++) {
//                    parm.addData("CHECK_DATE",
//                                 tableParm.getValue("CHECK_DATE",
//                                 i).substring(0, 10));
//                    parm.addData("STATUS",
//                                 map.get(tableParm.getValue("STATUS", i)));
//                    parm.addData("ORDER_DESC",
//                                 tableParm.getValue("ORDER_DESC", i));
//                    parm.addData("SPECIFICATION",
//                                 tableParm.getValue("SPECIFICATION", i));
//                    parm.addData("QTY", tableParm.getValue("QTY", i));
//                    parm.addData("UNIT_CHN_DESC",
//                                 tableParm.getValue("UNIT_CHN_DESC", i));
//                    parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
//                    parm.addData("AMT", tableParm.getValue("AMT", i));
//                    totAmt+=Double.parseDouble(tableParm.getValue("AMT", i));
//                    parm.addData("ORG_CHN_DESC",
//                                 tableParm.getValue("ORG_CHN_DESC", i));
//                }
//                //����ϼ���Ϣ begin
//                parm.addData("CHECK_DATE",
//                		"�ϼƣ�");
//                parm.addData("STATUS",
//                		"");
//                parm.addData("ORDER_DESC",
//                		"");
//                parm.addData("SPECIFICATION",
//                		"");
//                parm.addData("QTY", "");
//                parm.addData("UNIT_CHN_DESC",
//                		"");
//                parm.addData("OWN_PRICE","");
//                parm.addData("AMT", StringTool.round(totAmt, 2));
//                parm.addData("ORG_CHN_DESC",
//                		"");
//
//                //����ϼ���Ϣ end
//                parm.setCount(parm.getCount("ORDER_DESC"));
//                parm.addData("SYSTEM", "COLUMNS", "CHECK_DATE");
//                parm.addData("SYSTEM", "COLUMNS", "STATUS");
//                parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//                parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//                parm.addData("SYSTEM", "COLUMNS", "QTY");
//                parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
//                parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
//                parm.addData("SYSTEM", "COLUMNS", "AMT");
//                parm.addData("SYSTEM", "COLUMNS", "ORG_CHN_DESC");
//                date.setData("TABLE", parm.getData());
//                // ��β����
//                date.setData("SUM_AMT", "TEXT",
//                             "�ܽ� " +  StringTool.round(totAmt, 2));//luhai 2012-2-13 ��������
//                // ���ô�ӡ����
//                this.openPrintWindow(
//                    "%ROOT%\\config\\prt\\IND\\INDPhaDetailQueryB.jhw", date);
//            }
//            else {
//                //ҩ����ϸ
//                date.setData("TITLE", "TEXT", Manager.getOrganization().
//                             getHospitalCHNFullName(Operator.getRegion()) +
//                             "ҩ��ҩƷ��ϸ��");
//                //�����ܼ���Ϣ
//                double totAmt = 0;
//                TParm tableParm = table_d_b.getParmValue();
//                for (int i = 0; i < table_d_b.getRowCount(); i++) {
//                    parm.addData("CHECK_DATE",
//                                 tableParm.getValue("CHECK_DATE",
//                                 i).substring(0, 10));
//                    parm.addData("STATUS",
//                                 map.get(tableParm.getValue("STATUS", i)));
//                    parm.addData("ORDER_DESC",
//                                 tableParm.getValue("ORDER_DESC", i));
//                    parm.addData("SPECIFICATION",
//                                 tableParm.getValue("SPECIFICATION", i));
//                    parm.addData("QTY", tableParm.getValue("QTY", i));
//                    parm.addData("UNIT_CHN_DESC",
//                                 tableParm.getValue("UNIT_CHN_DESC", i));
//                    parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
//                    parm.addData("AMT", tableParm.getValue("AMT", i));
//                    totAmt+=Double.parseDouble(tableParm.getValue("AMT", i));
//                    parm.addData("DEPT_CHN_DESC",
//                                 tableParm.getValue("DEPT_CHN_DESC", i));
//                    parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
//                    parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
//                    parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
//                }
//                //����ϼ���
//		           parm.addData("CHECK_DATE","�ϼƣ�");
//		           parm.addData("STATUS",
//		                       "");
//		           parm.addData("ORDER_DESC",
//		                       "");
//		           parm.addData("SPECIFICATION",
//		                        "");
//		           parm.addData("QTY", "");
//		           parm.addData("UNIT_CHN_DESC",
//		                       "");
//		           parm.addData("OWN_PRICE","");
//		           parm.addData("AMT", StringTool.round(totAmt,2));
//		           parm.addData("DEPT_CHN_DESC",
//		                        "");
//		           parm.addData("MR_NO","");
//		           parm.addData("PAT_NAME","");
//		           parm.addData("CASE_NO","");
//           //add �ϼ���end
//                parm.setCount(parm.getCount("ORDER_DESC"));
//                parm.addData("SYSTEM", "COLUMNS", "CHECK_DATE");
//                parm.addData("SYSTEM", "COLUMNS", "STATUS");
//                parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//                parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//                parm.addData("SYSTEM", "COLUMNS", "QTY");
//                parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
//                parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
//                parm.addData("SYSTEM", "COLUMNS", "AMT");
//                parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
//                parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//                parm.addData("SYSTEM", "COLUMNS", "MR_NO");
//                parm.addData("SYSTEM", "COLUMNS", "CASE_NO");
//                date.setData("TABLE", parm.getData());
//                // ��β����
//                date.setData("SUM_AMT", "TEXT",
//                             "�ܽ� " + StringTool.round(getValueDouble("SUM_AMT"),2));//luhai 2012-2-13 ��������
//                // ���ô�ӡ����
//                this.openPrintWindow(
//                    "%ROOT%\\config\\prt\\IND\\INDPhaDetailQueryC.jhw", date);
//            }
//        }
   	 //*********************************************
    	//ҩ����ϸ�˼���ϼƹ��� luhai end 2012-2-22
    	//*********************************************
    }

    /**
     * �����ܽ��
     */
    private void setSumAmt() {
        double amt = 0;
//        if (this.getRadioButton("IND_ORG_A").isSelected()) {
//        		for (int i = 0; i < table_m.getRowCount(); i++) {
//        			amt += table_m.getItemDouble(i, "STOCK_AMT");
//        		}
//        }
//        else if (this.getRadioButton("TYPE_A").isSelected()) {
//            for (int i = 0; i < table_d_a.getRowCount(); i++) {
//                amt += table_d_a.getItemDouble(i, "AMT");
//            }
//        }
//        else {
//            for (int i = 0; i < table_d_b.getRowCount(); i++) {
//                amt += table_d_b.getItemDouble(i, "AMT");
//            }
//        }
        if (this.getRadioButton("TYPE_A").isSelected()){
    		for (int i = 0; i < table_m.getRowCount()-1; i++) {//ȥ���ϼ���
    			amt += table_m.getItemDouble(i, "STOCK_AMT");
    		}
        }else{
		      if (this.getRadioButton("IND_ORG_A").isSelected()) {
		          for (int i = 0; i < table_d_a.getRowCount()-1; i++) {//ȥ���ϼ���
	        		  amt += table_d_a.getItemDouble(i, "AMT");                     
		          }
		      }      
		      else {
		          for (int i = 0; i < table_d_b.getRowCount()-1; i++) {//ȥ���ϼ���
	        		  amt += table_d_b.getItemDouble(i, "AMT");
		          }
		      } 
		      amt = amt/2; //ȥ��С����
        }
        this.setValue("SUM_AMT", amt);
    }

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }

    /**
     * ͳ�Ƶ�λ����¼�
     */
    public void onChangeOrgType() {
        TextFormatINDOrg ind_org = (TextFormatINDOrg)this.getComponent(
            "ORG_CODE");
        ind_org.setValue("");
        if (getRadioButton("IND_ORG_A").isSelected()) {
            ind_org.setOrgType("A");
        }
        else {
            ind_org.setOrgType("B");
        }
        ind_org.onQuery();
        onChangeInfoType();
    }

    /**
     * ���ͱ���¼�
     */
    public void onChangeInfoType() {
        if (getRadioButton("TYPE_A").isSelected()) {
            getCheckBox("CHECK_A").setEnabled(false);
            getCheckBox("CHECK_B").setEnabled(false);
            getCheckBox("CHECK_C").setEnabled(false);
            table_m.setVisible(true);
            table_d_a.setVisible(false);
            table_d_b.setVisible(false);
        }
        else {
            getCheckBox("CHECK_A").setEnabled(true);
            getCheckBox("CHECK_B").setEnabled(true);
            getCheckBox("CHECK_C").setEnabled(true);
            table_m.setVisible(false);
            if (getRadioButton("IND_ORG_A").isSelected()) {
                table_d_a.setVisible(true);
                table_d_b.setVisible(false);
            }
            else {
                table_d_a.setVisible(false);
                table_d_b.setVisible(true);
            }
        }
        setSumAmt();
    }
    
    /**
	 * ���Excel
	 * @author shendr 20140212
	 */
	public void onExport() {
		if (table_m.isShowing()) {
			if (table_m.getRowCount() <= 0) {
				this.messageBox("û�л������");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table_m, "ҩƷ��ϸ�˻���");
		}
		if (table_d_a.isShowing()) {
			if (table_d_a.getRowCount() <= 0) {
				this.messageBox("û�л������");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table_d_a, "ҩƷ��ϸ����ϸ");
		}
		if (table_d_b.isShowing()) {
			if (table_d_b.getRowCount() <= 0) {
				this.messageBox("û�л������");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table_d_b, "ҩƷ��ϸ����ϸ");
		}
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
     * �õ�CheckBox����
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
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
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

}
