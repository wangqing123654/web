package action.pha;

import java.sql.Timestamp;

import jdo.opd.OrderList;
import jdo.opd.OrderTool;
import jdo.pha.PHAOrderTool;
import jdo.pha.PhaOrderHistoryTool;
import jdo.reg.PatAdmTool;
import jdo.spc.INDTool;
import jdo.spc.IndStockMTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: ����ҩ��������</p>
 *
 * <p>Description: ����ҩ��������</p>
 *
 * <p>Copyright: JavaHis (c) �����к� 2008 ���� 2011</p>
 *
 * @author ZangJH 2008-09-26 ZhenQin 2011-04-26
 * @version 1.0
 */

public class PHAAction
    extends TAction {
    /**
     * ��ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm) {

        TParm result = new TParm();
        TParm orderresult = new TParm();

        // ��ѯҽ��
        orderresult = OrderTool.getInstance().queryForPHA(parm);
        if (orderresult.getErrCode() != 0) {
            return err(orderresult);
        }

        result.setData("ORDER", orderresult.getData());

        return orderresult;
    }

    /**
     * ҩ���������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onSave(TParm parm) {
        boolean returnFlg = false;
        //����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
        TConnection connection = getConnection();
        TParm result = new TParm();
        TParm orderparm = new TParm();
        // ����ҽ��
        if (parm.existData("ORDER")) {
            orderparm = parm.getParm("ORDER");
            result = PHAOrderTool.getInstance().onSave(orderparm, connection);
        }
        if (result.getErrCode() < 0) {
            System.out.println(result.getErrText());
            connection.close();
            return result;
        }
        //��⡰��������DATA���Ƿ�����ֵ
        if (parm.existData("HISTORY")) {
            //��history��Ŀ��ʱ������ҩ
            returnFlg = true;
            TParm historyparm = parm.getParm("HISTORY");
            result = PhaOrderHistoryTool.getInstance().onInsert(historyparm,
                connection);
        }
        if (result.getErrCode() < 0) {
            System.out.println(result.getErrText());
            connection.close();
            return result;
        }
        System.out.println("forIND:"+parm.getBoolean("forIND"));
        if (parm.getBoolean("forIND")) {
            TParm parmCostAmt = new TParm();
            //�����ﹹ��һ��parmCostAmt,�Ժ���Ҫ��addParm�ķ�ʽ,��Ҫ���ı�����,��������,����Ҫ��һ��forѭ����addData�ķ�ʽ���
            parmCostAmt.addData("CASE_NO",null);
            parmCostAmt.addData("RX_NO", null);
            parmCostAmt.addData("SEQ_NO",null);
            parmCostAmt.addData("COST_AMT", null);
            //***************************************************************
            //luhai 2012-1-27 add //���������Ŀۿ�����������TParm begin
            //***************************************************************
            parmCostAmt.addData("BATCH_SEQ1", null);
            parmCostAmt.addData("BATCH_SEQ2", null);
            parmCostAmt.addData("BATCH_SEQ3", null);
            parmCostAmt.addData("VERIFYIN_PRICE1", null);
            parmCostAmt.addData("VERIFYIN_PRICE2", null);
            parmCostAmt.addData("VERIFYIN_PRICE3", null);
            parmCostAmt.addData("DISPENSE_QTY1", null);
            parmCostAmt.addData("DISPENSE_QTY2", null);
            parmCostAmt.addData("DISPENSE_QTY3", null);
            parmCostAmt.addData("BATCH_NO", null);
            //***************************************************************
            //luhai 2012-1-27 add //���������Ŀۿ�����������TParm end
            //***************************************************************
            //luhai 2012-1-27 end 
            //ȡ��ϵͳʱ��
            Timestamp optTime = TJDODBTool.getInstance().getDBTime();
            //������
            TParm parmToIND = new TParm();
            //ȡ�ò��˵Ĵ���ǩ
            TParm forIndParm = orderparm.getParm(OrderList.MODIFIED);
            /**
             * ԭ trimedforIndParm - forIndParm (������cleanUp)
             * ����ZhenQin - 2011-04-26
             */
            if(result.getErrCode() < 0){
                connection.close();
                System.out.println(result.getErrText());
                return result;

            }

            /**
             * ����ZhenQin - 2011-04-26
             */
            //����������Ĵ���ǩ,ÿ��ҩƷ���ظ�
            TParm trimedforIndParm = cleanUp(forIndParm);
            System.out.println("trimedforIndParm.getCount():"+trimedforIndParm.getCount());
            String message = "";
            for (int i = 0; i < trimedforIndParm.getCount(); i++) {
            	
            	String orgCode = trimedforIndParm.getValue("EXEC_DEPT_CODE", i) ;
            	String orderCode = trimedforIndParm.getValue("ORDER_CODE", i);
            	double dosageQty = trimedforIndParm.getDouble("DOSAGE_QTY", i) ;
                //���һ��ҩƷ�Ĳ���--����IND
                parmToIND.setData("ORG_CODE", orgCode);
                parmToIND.setData("ORDER_CODE",orderCode);
                parmToIND.setData("DOSAGE_QTY",dosageQty);
                parmToIND.setData("OPT_DATE", optTime);
                parmToIND.setData("OPT_USER",trimedforIndParm.getData("OPT_USER", i));
                parmToIND.setData("OPT_TERM",trimedforIndParm.getData("OPT_TERM", i));
 
                //��ȡ����ҩ(finishFlag->Y)
                boolean checkResult = INDTool.getInstance().inspectIndStock(orgCode,orderCode,dosageQty);
                if (returnFlg && parm.getBoolean("finishFlag") &&
                    !checkResult) {
                    message += (trimedforIndParm.getData("ORDER_DESC", i) + "\n");
                }
                //Ϊ�ٵ�ʱ��--����ҩ
                if (!returnFlg && !parm.getBoolean("ISUNDISP_FLG") &&
                    !checkResult) {
                    message += (trimedforIndParm.getData("ORDER_DESC", i) + "\n");
                }
            }
            if(message.length() != 0){
                connection.rollback();
                connection.close();
                result.setErr( -1, message + "ҩƷ��治��\n���ɿۿ⣡");
                return result;
            }

            System.out.println("returnFlg:"+returnFlg);
            String lockFlg = parm.getValue("LOCK_FLG");
            
            //��ҩ����
            if(!returnFlg) {
	            if("Y".equals(lockFlg)) {
		            String msg = "";
		            for(int i = 0 ; i < trimedforIndParm.getCount();i++ ){
		            	TParm inParm = new TParm();
		            	
		            	inParm.setData("ORG_CODE", trimedforIndParm.getData("EXEC_DEPT_CODE", i));
		            	inParm.setData("ORDER_CODE",trimedforIndParm.getData("ORDER_CODE", i));
		            	inParm.setData("LOCK_QTY",trimedforIndParm.getData("DOSAGE_QTY", i));
		            	
		            	result = IndStockMTool.getInstance().onUpdateMinusLockQty(inParm, connection);
		            	
		            	if(result.getErrCode() < 0 ){
		            		msg += trimedforIndParm.getData("ORDER_DESC", i) + "\n";
		            	}
		            }
		            if(msg.length() != 0 ){
		            	connection.rollback();
		                connection.close();
		                result.setErr( -1, msg + "ҩƷ������治��\n���ɿۿ⣡");
		                return result;
		            }
	            }
            }
            
            
            //�ۿ���ϸ,����б��а������еĿۿ�ش�ֵ
            TParm costAwtList = new TParm();
            for (int i = 0; i < trimedforIndParm.getCount(); i++) {
            	
            	String orgCode =  trimedforIndParm.getValue("EXEC_DEPT_CODE", i);
            	String orderCode = trimedforIndParm.getValue("ORDER_CODE", i) ;
            	double dosageQty =  trimedforIndParm.getDouble("DOSAGE_QTY", i);
                //���һ��ҩƷ�Ĳ���--����IND
                parmToIND.setData("ORG_CODE", orgCode);
                parmToIND.setData("ORDER_CODE",orderCode);
                parmToIND.setData("DOSAGE_QTY",
                                  trimedforIndParm.getData("DOSAGE_QTY", i));
                parmToIND.setData("CASE_NO",trimedforIndParm.getData("CASE_NO", i));
                parmToIND.setData("OPT_DATE",optTime);
                parmToIND.setData("OPT_USER",trimedforIndParm.getData("OPT_USER", i));
                parmToIND.setData("OPT_TERM",trimedforIndParm.getData("OPT_TERM", i));
                
                
                String batchNo = trimedforIndParm.getValue("BATCH_NO",i); 
               // String routeCode  = trimedforIndParm.getValue("ROUTE_CODE",i);
                parmToIND.setData("BATCH_NO",batchNo);
                
                System.out.println("======11111parmToIND111111�ۿ��======"+parmToIND);
                                 
                //��ȡ����ҩ(finishFlag->Y)
                boolean checkIndStock = INDTool.getInstance().inspectIndStock(orgCode,orderCode,dosageQty);
                if (returnFlg && parm.getBoolean("finishFlag") &&
                    !checkIndStock) {
                    System.out.println(result.getErrText());
                    connection.close();
                    result.setErr( -1, "ҩƷ��治��\n���ɿۿ⣡");
                    return result;
                }
                //ȡ��CASE_NO
                String caseNo = parmToIND.getValue("CASE_NO");
                //ͨ��CASE_NO�õ�������Ϣ
                TParm caseNoParm = PatAdmTool.getInstance().getInfoForCaseNo(caseNo);

                
                //Ϊ�ٵ�ʱ��--����ҩ
                if (!returnFlg) {
                	System.out.println("ISUNDISP_FLG:"+parm.getBoolean("ISUNDISP_FLG"));
                      if (parm.getBoolean("ISUNDISP_FLG")) {
	                        result = INDTool.getInstance().unReduceIndStock(
	                            parmToIND, caseNoParm.getValue("SERVICE_LEVEL",0), connection);
	                        parmCostAmt.addData("CASE_NO",caseNo);
	                        parmCostAmt.addData("RX_NO", trimedforIndParm.getData("RX_NO", i));
	                        parmCostAmt.addData("SEQ_NO",
	                                        trimedforIndParm.getData("SEQ_NO", i));
	                        parmCostAmt.addData("COST_AMT", 0);
                      } else {
		                    //��������Ҫһ��ORG_CODE,ORDER_CODE,DOSAGE_QTY,OPT_USER,OPT_DATE,OPT_TERM������Ҫ����������
		                    //��֤���
                    	  
                    	  	boolean checkResult = false ;
                    	    /**�������ż��*/
                    	  	if(batchNo != null && !batchNo.equals("") && !(trimedforIndParm.getData("BATCH_NO",i) instanceof TNull)){
//                    	  		checkResult = INDTool.getInstance().inspectIndStock(orgCode,orderCode,batchNo,dosageQty);
//                    	  		if(!checkResult){
//                    	  			System.out.println(result.getErrText());
//     		                        result.setErr( -1, "�����ſ�治�㣬����ϵ����ҽ��");
//     		                        connection.rollback() ;
//     		                        connection.close();
//     		                        return result;
//                    	  		}
                    	  	}else{
                    	  		 checkResult = INDTool.getInstance().inspectIndStock(orgCode,orderCode,dosageQty);
                    	  		 if (!checkResult) {
     		                        System.out.println(result.getErrText());
     		                        result.setErr( -1, "ҩƷ��治��\n���ɿۿ⣡");
     		                        connection.rollback() ;
     		                        connection.close();
     		                        return result;
     		                    }
                    	  	}
		                   
                    	  	// add by wangb 2017/1/16 Ϊ�����ۿ�����������־���
                    	  	System.out.println(StringTool.getString(TJDODBTool
								.getInstance().getDBTime(), "yyyyMMddHHmmss")
								+ " �ۿ⴦��ǩ�ţ�"
								+ trimedforIndParm.getData("RX_NO", i));
                    	  	System.out.println("batchNo:"+batchNo);
                    	  	if(batchNo != null && !batchNo.equals("") && !(trimedforIndParm.getData("BATCH_NO",i) instanceof TNull)){
                    	  		 //�ۿ⶯��
			                    result = INDTool.getInstance().reduceIndStockByBatchNo(parmToIND,
			                        caseNoParm.getValue("SERVICE_LEVEL", 0),
			                        connection);
			                    if(result.getErrCode() < 0){
			                    	connection.rollback();
			                        connection.close();
			                        System.out.println(result.getErrText());
			                        return result;

			                    }
                    	  	}else{
			                    //�ۿ⶯��
                    	  		System.out.println("�ۿ����ݣ�"+parmToIND);
			                    result = INDTool.getInstance().reduceIndStock(parmToIND,
			                        caseNoParm.getValue("SERVICE_LEVEL", 0),
			                        connection);
			                    if(result.getErrCode() < 0){
			                    	connection.rollback();
			                        connection.close();
			                        System.out.println(result.getErrText());
			                        return result;

			                    }
                    	  	}
                    	  	
		                    for(int n = 0; n < result.getCount("ORDER_CODE"); n++){
		                        costAwtList.addData("CASE_NO",    caseNo);
		                        costAwtList.addData("ORDER_CODE", result.getData("ORDER_CODE",   n));
		                        costAwtList.addData("ORG_CODE",   result.getData("ORG_CODE",   n));
		                        costAwtList.addData("PRICE",      result.getData("PRICE",   n));
		                        costAwtList.addData("QTY",        result.getData("QTY",   n));
		                        costAwtList.addData("RX_NO",      trimedforIndParm.getData("RX_NO", i));
		                        costAwtList.addData("SEQ_NO",     trimedforIndParm.getData("SEQ_NO", i));
		                        costAwtList.addData("COST_AMT",   result.getData("COST_AMT"));
		                        //luhai add batch_seq 
		                        costAwtList.addData("BATCH_SEQ",   result.getData("BATCH_SEQ",   n));
		                        
		                        batchNo = result.getValue("BATCH_NO",n);
		                        costAwtList.addData("BATCH_NO", batchNo);
		                    }
                    }
                }

            }
            //*******************************************
            //��ҩ���� begin luhai 2012-1-31 begin
            //*******************************************
            if(returnFlg){
            	//ȡ��CASE_NO
            	String caseNo = parmToIND.getValue("CASE_NO");
            	//ͨ��CASE_NO�õ�������Ϣ
            	TParm caseNoParm = PatAdmTool.getInstance().getInfoForCaseNo(caseNo);
            	result = reduceMed(forIndParm,caseNoParm.getValue("SERVICE_LEVEL",0),parmCostAmt,caseNo,optTime,connection);
            	if(result.getErrCode()<0){
                    System.out.println(result.getErrText());
                    connection.rollback() ;
                    connection.close();
                    result.setErr( -1, "ҩƷ�ۿ��쳣��");
                    return result;
            	}
            }
            //*******************************************
            //��ҩ���� end luhai 2012-1-31 end
            //*******************************************
            
            //����ɱ����,result�а���ORG_CODE,ORDER_CODE,QTY,PRICE

            /**
             * tmpParm:{Data={CASE_NO=[110426000005, 110426000005, 110426000005, 110426000005],
             * ORG_NO=[1101030903, 1101030903, 1101030903, 1101030903],
             * RX_NO=[110426000002, 110426000002, 110426000002, 110426000002],
             * SEQ_NO=[1, 2, 3, 4]}
             */
            TParm tmpParm = calculateCostAwt(costAwtList, forIndParm);
            parmCostAmt.addParm(tmpParm);
            parmCostAmt.removeRow(0);


            /*
             * ԭ,û�б�
             */

            for(int k = 0; k < parmCostAmt.getCount("CASE_NO"); k++){
            	
                result = PHAOrderTool.getInstance().updateCostAmt(parmCostAmt.getRow(k), connection);
                if (result.getErrCode() < 0) {
                    System.out.println(result.getErrText());
                    connection.rollback();
                    connection.close();
                    return result;
                }
                //luhai add 2012-31 ����ʵ�ʵķ�ҩ����
                //���ж�����Ϊ������ҩ��������Ҳ�����ҩ�󱣴����������ۿ�ϸ��ĸ���
                if(!returnFlg&&(!parm.getBoolean("ISUNDISP_FLG"))){
                	result = PHAOrderTool.getInstance().updateDispenseDetailForPHA(parmCostAmt.getRow(k), connection);
                	if (result.getErrCode() < 0) {
                		System.out.println(result.getErrText());
                		 connection.rollback();
                		connection.close();
                		return result;
                	}
                }
            }
        }
        connection.commit();
        connection.close();
        return result;
        //*******************************************************************************
        // luhai modify �ۿ�����д�����batchSeq end
        //*******************************************************************************
    }
    /**
     * 
     * ��ҩ����
     * luhai 2012-1-31
     * @param reduceParm   ��ҩ�Ĵ�����Ϣ
     * @param serviceLevel ����ȼ�
     * @param case_no
     * @param Timestamp optTime
     * @param TConnection  connection
     * @return
     */
    private TParm reduceMed(TParm forIndParm,String serviceLevel,TParm parmCostAmt,String caseNo,Timestamp optTime,TConnection  connection){
    	TParm returnParm = new TParm();
    	for (int i = 0; i < forIndParm.getCount(); i++) {
    		String rxNo=forIndParm.getValue("RX_NO", i);
    		String seqNo=forIndParm.getValue("SEQ_NO", i);
    		TParm queryParm = new TParm();
    		queryParm.setData("CASE_NO",caseNo);
    		queryParm.setData("RX_NO",rxNo);
    		queryParm.setData("SEQ_NO",seqNo);
    		TParm phaDispenseParm = PHAOrderTool.getInstance().queryDispenseDetailForPHA(queryParm,connection);
    		if(phaDispenseParm.getErrCode()<0){
    			return phaDispenseParm;
    		}
    		String orgCode=phaDispenseParm.getValue("EXEC_DEPT_CODE",0);
    		String orderCode=phaDispenseParm.getValue("ORDER_CODE",0);
    		String optUser=forIndParm.getValue("OPT_USER",i);
    		String optTerm=forIndParm.getValue("OPT_TERM",i);
    		for(int j=1;j<=3;j++){
    			int batchSeq=phaDispenseParm.getInt("BATCH_SEQ"+j,0);
    			double dosageQty=phaDispenseParm.getInt("DISPENSE_QTY"+j,0);
    			if(dosageQty<=0){
    				continue;
    			}
    			returnParm=INDTool.getInstance().regressIndStockWithBatchSeq(orgCode, orderCode, batchSeq, dosageQty, optUser, optTime, optTerm, serviceLevel, connection);
    			if(returnParm.getErrCode()<0){
    				return returnParm;
    			}
    		}
//    		result = INDTool.getInstance().regressIndStock(parmToIND, caseNoParm.getValue("SERVICE_LEVEL",0),
//    				connection);
    		parmCostAmt.addData("CASE_NO", caseNo);
    		parmCostAmt.addData("RX_NO", forIndParm.getData("RX_NO", i));
    		parmCostAmt.addData("SEQ_NO", forIndParm.getData("SEQ_NO", i));
    		parmCostAmt.addData("COST_AMT", 0);
    	}
    	return returnParm;
    }

    /**
     * ����ɱ����,�ɱ������costAwtList,�������еĻش���ORG_CODE,ORDER_CODE,QTY,PRICE
     * ����ش��Ľ�����а��������еĿۿ�Լ��,��: QTY = 10, �ش�������8��2.
     * �����Ļ��ۿⲢ���Ǵ�һ������ֱ�ӿ۳�.���ǴӶ����۳���һ���ܺ�
     * �ڿۿ�ʱ,���ǰ�һ�Ŵ���ǩ�����е���ͬ�Ĵ��������ϲ���һ��.�ۿ���Ҫ����ܺ�,������������ܺͿ۳�
     * �����ڻ�д�ɱ����ʱ,��Ҫÿһ�������ĵ���ֵ.
     * �÷������ǰѿۿ�ʱ��д�����ݷ��䵽ÿһ������(����������ܰ�����ͬ������)
     * result�а���ORG_CODE,ORDER_CODE,QTY,PRICE
     * @param costAwtList TParm
     * @param forIndParm TParm
     * @return TParm
     */
    private TParm calculateCostAwt(TParm costAwtList, TParm forIndParm) {
        //����һ���µ�TParm,������¼ÿһ�������ĳɱ����
        TParm costAwts = new TParm();
        if (costAwtList == null || costAwtList.getCount("ORDER_CODE") == 0) {
            return costAwts;
        }
        // �ñ�ҩ��order_code
        String order_code = "";
        // ҩ������
        String org_code = "";
        // �ñ�ҩ������(û�кϲ��ĵ�������)
        double singleCount = 0;
        // �ɱ����
        double price = 0;
        //����ÿһ��forIndParm,��Ϊ��Ҫ��дÿһ�Ŵ���ǩ�ϵĴ���
        for (int i = 0; i < forIndParm.getCount("ORDER_CODE"); i++) {
            // �ñ�ҩ��order_code
            order_code = forIndParm.getValue("ORDER_CODE", i);
            // ҩ������
            org_code = forIndParm.getValue("EXEC_DEPT_CODE", i);

            costAwts.addData("CASE_NO", forIndParm.getValue("CASE_NO", i));
            costAwts.addData("RX_NO", forIndParm.getData("RX_NO", i));
            costAwts.addData("SEQ_NO", forIndParm.getData("SEQ_NO", i));
            // ����ҩƷ������
            singleCount = forIndParm.getDouble("DOSAGE_QTY", i);
            //�ɱ����
            price = 0;
           //luhai 2012-1-26 add begin
            String batch_seq1="null";
            String batch_seq2="null";
            String batch_seq3="null";
            String dispense_qty1="0";
            String dispense_qty2="0";
            String dispense_qty3="0";
            String verifyin_price1="0";
            String verifyin_price2="0";
            String verifyin_price3="0";
           //���ڼ�¼��ҩ�ֶμ�¼����һ��batch_seq1��batch_seq2��batch_seq3
           //������Ԥ�����ֶΣ�������
           int index=1;
           String batchNo = "";
           //luhai 2012-1-26  end
            //ͬ��ҩ�ĵ����б�
            for (int j = 0; j < costAwtList.getCount("ORDER_CODE"); j++) {
                //��һ������ǩ�ϴ�����ͬ��org_code��order_code,���ʾ����ͬ�Ĵ���
                if (costAwtList.getValue("ORDER_CODE", j).equals(order_code)
                    && costAwtList.getValue("ORG_CODE", j).equals(org_code)) {
                	String tmpDispenseQty="0";
                	String tmpBatchSeq="null";
                	String tmpVerifyinPrice="0";
                	boolean isContinue=false;
                    //ȡ�ô���ǩ�ĵ��������ۿ���
                    double tmpQty = Double.parseDouble(costAwtList.getValue("QTY", j));
                    //������ҩ����������һ���ش���,���ʾ�ۿ�ʱ�ñ�ҩ�������������п۳�
                    batchNo = costAwtList.getValue("BATCH_NO",j);
                    if (singleCount > tmpQty) {
                        //�ۿ�ĵ�һ��tmpQty
                        price += tmpQty *
                            Double.parseDouble(costAwtList.getValue("PRICE", j));
                        //����costAwtList.setData("QTY", j, 0);
                        costAwtList.setData("QTY", j, 0);
                        //�ڴ���������ȥ�ش���,֪��������singleCount = 0.
                        singleCount -= tmpQty;
                        //����ʵ�ʵķ�ҩ�ֶ�ֵ
                        tmpDispenseQty=tmpQty+"";
                        tmpBatchSeq=costAwtList.getValue("BATCH_SEQ", j);
                        tmpVerifyinPrice=costAwtList.getValue("PRICE", j);
//                        continue;
                        isContinue=true;
                        //��singleCount == tmpQty��ʾ�ô�����һ������п۳�.
                    }  else if (singleCount == tmpQty) {
                        price += singleCount *
                            Double.parseDouble(costAwtList.getValue("PRICE", j));
                        costAwtList.setData("QTY", j, 0);
                        singleCount = 0;
                        //����ʵ�ʵķ�ҩ�ֶ�ֵ
                        tmpDispenseQty=tmpQty+"";
                        tmpBatchSeq=costAwtList.getValue("BATCH_SEQ", j);
                        tmpVerifyinPrice=costAwtList.getValue("PRICE", j);
                        //����������С�ڻش���,���ʾ�ñ�ҩƷȫ����һ������п۳�
                    } else {
                        price += singleCount *
                            Double.parseDouble(costAwtList.getValue("PRICE", j));
                        costAwtList.setData("QTY", j, costAwtList.getDouble("QTY", j) - singleCount);
                        //����ʵ�ʵķ�ҩ�ֶ�ֵ
                        tmpDispenseQty=singleCount+"";
                        tmpBatchSeq=costAwtList.getValue("BATCH_SEQ", j);
                        tmpVerifyinPrice=costAwtList.getValue("PRICE", j);
                        singleCount = 0;
                    }
                    //����ʵ�ʵķ�ҩbatchSeq luhai begin
                    if(index==1){
                    	batch_seq1=tmpBatchSeq;
                    	dispense_qty1=tmpDispenseQty;
                    	verifyin_price1=tmpVerifyinPrice;
                    }else if(index==2){
                    	batch_seq2=tmpBatchSeq;
                    	dispense_qty2=tmpDispenseQty;
                    	verifyin_price2=tmpVerifyinPrice;
                    }else if(index==3){
                    	batch_seq3=tmpBatchSeq;
                    	dispense_qty3=tmpDispenseQty;
                    	verifyin_price3=tmpVerifyinPrice;
                    }
                    index++;
                    if(isContinue){
                    	continue;
                    }else{
                    	break;
                    }
                    //����ʵ�ʵķ�ҩbatchSeq luhai end
                }
            }
            //��һ��ѭ��,�������óɱ����
            costAwts.addData("COST_AMT", price);
            //luhai add begin
            costAwts.addData("BATCH_SEQ1", "null".equals(batch_seq1)?-1:batch_seq1);
            costAwts.addData("VERIFYIN_PRICE1", verifyin_price1);
            costAwts.addData("DISPENSE_QTY1", dispense_qty1);
            costAwts.addData("BATCH_SEQ2", "null".equals(batch_seq2)?-1:batch_seq2);
            costAwts.addData("VERIFYIN_PRICE2", verifyin_price2);
            costAwts.addData("DISPENSE_QTY2", dispense_qty2);
            costAwts.addData("BATCH_SEQ3", "null".equals(batch_seq3)?-1:batch_seq3);
            costAwts.addData("VERIFYIN_PRICE3", verifyin_price3);
            costAwts.addData("DISPENSE_QTY3", dispense_qty3);
            costAwts.addData("BATCH_NO",batchNo);
//            costAwts.addData("BATCH_SEQ1", "null".equals(batch_seq1)?new TNull(int.class):batch_seq1);
//            costAwts.addData("VERIFYIN_PRICE1", verifyin_price1);
//            costAwts.addData("DISPENSE_QTY1", dispense_qty1);
//            costAwts.addData("BATCH_SEQ2", "null".equals(batch_seq2)?new TNull(int.class):batch_seq2);
//            costAwts.addData("VERIFYIN_PRICE2", verifyin_price2);
//            costAwts.addData("DISPENSE_QTY2", dispense_qty2);
//            costAwts.addData("BATCH_SEQ3", "null".equals(batch_seq3)?new TNull(int.class):batch_seq3);
//            costAwts.addData("VERIFYIN_PRICE3", verifyin_price3);
//            costAwts.addData("DISPENSE_QTY3", dispense_qty3);
            //luhai add end
            
        }
        return costAwts;
    }

    /**
     * ��һ���б����Ѿ��������ҩƷ <br>
     * ��ͬһ�Ŵ���ǩ�ϴ����һ��ҩƷʱ,�Ѹ�ҩƷ��org_code��order_code��������<br>
     * �ڼ����һ��ҩƷʱ,�����ж��Ƿ��Ѿ��������ҩƷ<br>
     * ���ĳ��ҩƷ��org_code��order_code��ͬʱ,���ʾ����ҩƷ����ͬһ��.<br>
     * ������ͬ��ҩƷʱ,��Ҫ����ͬҩƷ��DOSAGE_QTY�Ӻ���ͬ���ݿ����жԱ�,�ж��Ƿ��治��<br>
     * �����µ�TParm�м�ȥ��ͬ��ҩƷ
     *
     * @param parm
     * @return ����һ�����������TParm,����µ�TParm����һ�������parm��count��
     */
    private TParm cleanUp(TParm parm) {
        if (parm == null) {
            return parm;
        }
        if (parm.getCount() <= 1) {
            return parm;
        }
        //һ���µ�TParm
        TParm newParm = new TParm();
        int count = parm.getCount();
        String order_code = "";
        String org_code = "";
        newParm.addData("CASE_NO", parm.getData("CASE_NO", 0));
        newParm.addData("ORDER_CODE", parm.getData("ORDER_CODE", 0));
        newParm.addData("EXEC_DEPT_CODE", parm.getData("EXEC_DEPT_CODE", 0));
        newParm.addData("DOSAGE_QTY", parm.getData("DOSAGE_QTY", 0));
        newParm.addData("OPT_USER", parm.getData("OPT_USER", 0));
        newParm.addData("OPT_TERM", parm.getData("OPT_TERM", 0));
        newParm.addData("SEQ_NO", parm.getData("SEQ_NO", 0));
        newParm.addData("RX_NO", parm.getData("RX_NO", 0));
        newParm.addData("ROUTE_CODE", parm.getData("ROUTE_CODE",0));
        newParm.addData("BATCH_NO", parm.getData("BATCH_NO",0));
        newParm.setData("ORDER_DESC",parm.getData("ORDER_DESC",0));
        
        for (int i = 1; i < count; i++) {
            //����һ����־λ
            boolean flg = true;
            order_code = parm.getValue("ORDER_CODE", i);
            org_code = parm.getValue("EXEC_DEPT_CODE", i);
            for (int j = 0; j < newParm.getCount("ORDER_CODE"); j++) {
                // ����Ѿ�����ͬһ��ҩƷ
                if (order_code.equals(newParm.getValue("ORDER_CODE", j)) &&
                    org_code.equals(newParm.getValue("EXEC_DEPT_CODE", j))) {
                    // ԭ����ֵ
                    double counted = Double.parseDouble(newParm.getValue(
                        "DOSAGE_QTY", j));
                    // ��ǰ�е���ֵ
                    double newcount = Double.parseDouble(parm.getValue(
                        "DOSAGE_QTY", i));
                    newParm.setData("DOSAGE_QTY", j, counted + newcount);
                    newParm.setData("SEQ_NO", j, parm.getData("SEQ_NO", 0));
                    newParm.setData("RX_NO", j, parm.getData("RX_NO", 0));
                    newParm.setData("ORDER_DESC",j, parm.getData("ORDER_DESC",0));
                    newParm.setData("ROUTE_CODE", j, parm.getData("ROUTE_CODE", 0));
                    newParm.setData("BATCH_NO",j, parm.getData("BATCH_NO",0));
                    flg = false;
                    break;
                }
            }
            //����ڲ��ѭ������,flg����true,˵��û����ͬ��ҩƷ
            if (flg) {
                newParm.addData("CASE_NO", parm.getData("CASE_NO", i));
                newParm.addData("ORDER_CODE", parm.getData("ORDER_CODE", i));
                newParm.addData("EXEC_DEPT_CODE", parm.getData("EXEC_DEPT_CODE", i));
                newParm.addData("DOSAGE_QTY", parm.getData("DOSAGE_QTY", i));
                newParm.addData("OPT_USER", parm.getData("OPT_USER", i));
                newParm.addData("OPT_TERM", parm.getData("OPT_TERM", i));
                newParm.addData("SEQ_NO", parm.getData("SEQ_NO", i));
                newParm.addData("RX_NO", parm.getData("RX_NO", i));
                newParm.addData("ORDER_DESC", parm.getData("ORDER_DESC",i));
                newParm.addData("ROUTE_CODE", parm.getData("ROUTE_CODE",i));
                newParm.addData("BATCH_NO", parm.getData("BATCH_NO",i));
            }
        }
        newParm.setCount(newParm.getCount("ORDER_CODE"));
        System.out.println("======11111newParm111111������======"+newParm);
        
        return newParm;
    }
}
