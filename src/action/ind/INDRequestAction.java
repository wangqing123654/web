package action.ind;

import java.util.ArrayList;
import java.util.List;

import jdo.ind.INDSQL;
import jdo.ind.INDTool;
import jdo.ind.IndRequestDTool;
import jdo.ind.IndRequestMTool;
import action.ind.client.SpcIndRequestd;
import action.ind.client.SpcIndRequestm;
import action.ind.client.SpcService_SpcServiceImplPort_Client;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title:���뵥
 * </p>
 *
 * <p>
 * Description: ���뵥
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p> 
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.05.24
 * @version 1.0
 */
public class INDRequestAction
    extends TAction {

	
    /**
     * ��ѯ��������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryM(TParm parm) {
        TParm result = new TParm();
        result = IndRequestMTool.getInstance().onQuery(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestMTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    //======================��װ��ҩ��=================   CHENXI MODIFY  
    /**
     * ������װ��ҩ����ҩ����ϸ��
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertD(TParm parm) {  
        TConnection conn = getConnection();
        TParm result = new TParm();
        for(int i=0;i<parm.getCount("ORDER_CODE");i++){
        	 result = IndRequestDTool.getInstance().onBoxInsert(parm.getRow(i), conn) ;
             if (result.getErrCode() < 0) {
                 err("ERR:" + result.getErrCode() + result.getErrText()
                     + result.getErrName());
                 conn.close();
                 return result;
             }	
        }
       
        conn.commit();
        conn.close();
        return result;
    }
    /**
     * ����
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onBoxInsertM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestMTool.getInstance().onBoxInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ����
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestMTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ɾ�����뵥(���ϸ��)
     *
     * @param parm
     * @return
     */
    public TParm onDeleteM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onDeleteRequestM(parm, conn);
        if (result == null || result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ������ϸ
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateD(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestDTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    
    /**
     * ������ϸ
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateRequestDM(TParm parm ) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestDTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        
        
        TParm reqParm = parm.getParm("REQ_M");
        SpcIndRequestm indRequestm = getInsertM(reqParm);
        //System.out.println("reqParm---------:"+reqParm);
        TParm reqDParm = parm.getParm("REQ_D");
        //System.out.println("reqDParm---------:"+reqDParm);
        indRequestm = getInsertD(indRequestm, reqDParm);
        try{
	        String out = SpcService_SpcServiceImplPort_Client.onSaveSpcRequest(indRequestm);

 	        if( out != null &&  !out.equals("success") ){
 	        	result.setErrCode(-1);
 	        	result.setErrText(out);
 	        	err("ERR:" + result.getErrCode() + result.getErrText()
 	                    + result.getErrName());
 	            conn.close();
 	            return result;
 	        }
        }catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace() ;
        	err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
        	result.setErrCode(-1);
        	result.setErrText("���÷���˱���ʧ��");
            conn.close();
            return result;
		}
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * ��װ������������IND_REQUESTM������
     * @param obj
     * @return
     */
    private SpcIndRequestm getInsertM(TParm parm ){
    	SpcIndRequestm obj = new SpcIndRequestm() ;
    	obj.setReqtypeCode(parm.getValue("REQTYPE_CODE"));
        obj.setAppOrgCode(parm.getValue("APP_ORG_CODE"));
        obj.setToOrgCode(parm.getValue("TO_ORG_CODE"));
        obj.setRequestDate(parm.getValue("REQUEST_DATE"));
        obj.setRequestUser(parm.getValue("REQUEST_USER"));
        obj.setReasonChnDesc(parm.getValue("REASON_CHN_DESC"));
        obj.setDescription(parm.getValue("DESCRIPTION"));
         
        obj.setUnitType(parm.getValue("UNIT_TYPE"));
        obj.setUrgentFlg(parm.getValue("URGENT_FLG"));
        obj.setOptUser(parm.getValue("OPT_USER"));
         
        obj.setOptDate( parm.getValue("OPT_DATE"));
        obj.setOptTerm(parm.getValue("OPT_TERM"));
        obj.setDrugCategory(parm.getValue("DRUG_CATEGORY"));
        obj.setRequestNo(parm.getValue("REQUEST_NO"));
        obj.setRegionCode(parm.getValue("REGION_CODE"));
        obj.setReqtypeCode(parm.getValue("REQTYPE_CODE"));
        obj.setApplyType("1");
    	return obj ;
    }
    
    
    /**
     * ��װ������������IND_REQUESTD������
     * @param obj
     * @return
     */
    private SpcIndRequestm getInsertD(SpcIndRequestm obj,TParm parm){
    	
    	int count = parm.getCount("ORDER_CODE");
    	List<SpcIndRequestd> list = new ArrayList<SpcIndRequestd>();
    	for (int i = 0; i < count; i++) {
    		TParm rowParm = parm.getRow(i);
            SpcIndRequestd objD = new SpcIndRequestd() ;
            
            String order_code = rowParm.getValue("ORDER_CODE") ;
            objD.setOrderCode(order_code);
            
            TParm orderParm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                    getPHAInfoByOrder(order_code)));
            
            String unitCode = (String) orderParm.getData("DOSAGE_UNIT", 0);
            double retailPrice =  orderParm.getDouble("RETAIL_PRICE", 0);
            double stockPrice =  orderParm.getDouble("STOCK_PRICE", 0);
            
            objD.setUnitCode(unitCode);
            objD.setStockPrice(stockPrice);
            objD.setRetailPrice(retailPrice);
            
            objD.setQty(rowParm.getDouble("QTY"));
            objD.setActualQty(rowParm.getDouble( "ACTUAL_QTY"));
            objD.setRequestNo(rowParm.getValue("REQUEST_NO"));
            objD.setSeqNo(rowParm.getInt("SEQ_NO"));
            objD.setBatchNo("");
            objD.setValidDate("");
            
            objD.setUpdateFlg(rowParm.getValue("UPDATE_FLG"));
            objD.setOptDate(rowParm.getValue("OPT_DATE"));
            objD.setOptTerm(rowParm.getValue("OPT_TERM"));
            objD.setOptUser(rowParm.getValue("OPT_USER"));
            list.add(objD);
        }
    	obj.setIndRequestds(list);
    	return obj;
    }

    

    /**
     * ���Ĳ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsertCosOut(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertCosOut(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ���ұ�ҩ���ɲ�ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDeptExm(TParm parm) {
        TParm result = new TParm();
        result = INDTool.getInstance().onQueryDeptExm(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �������쵥
     * @param parm TParm
     * @return TParm
     */
    public TParm onCreateDeptExmRequest(TParm parm) {
        TParm result = new TParm();
        TConnection conn = getConnection();
        // identity shendr 2013.7.23
        result = INDTool.getInstance().onCreateDeptExmRequest(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * �������쵥������������[���ұ�ҩ����]
     * @param parm
     * @return
     */
    public TParm onCreateDeptExmRequestSpc(TParm parm){
    	 TParm result = new TParm();
         TConnection conn = getConnection();
         result = INDTool.getInstance().onCreateDeptExmRequest(parm, conn);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             conn.close();
             return result;
         }
         
         TParm reqParm =  parm.getParm("REQUEST_M");
         SpcIndRequestm indRequestm = getInsertM(reqParm);
        // System.out.println("reqParm---------:"+reqParm);
         TParm reqDParm = parm.getParm("REQUEST_D");
        // System.out.println("reqDParm---------:"+reqDParm);
         indRequestm = getInsertD(indRequestm, reqDParm);
         try{
 	        String out = SpcService_SpcServiceImplPort_Client.onSaveSpcRequest(indRequestm);
 	        
 	        if( out != null &&  !out.equals("success") ){
 	        	result.setErrCode(-1);
 	        	result.setErrText(out);
 	        	err("ERR:" + result.getErrCode() + result.getErrText()
 	                    + result.getErrName());
 	            conn.close();
 	            return result;
 	        }
         }catch (Exception e) {
 			// TODO: handle exception
         	e.printStackTrace() ;
         	err("ERR:" + result.getErrCode() + result.getErrText()
                     + result.getErrName());
         	result.setErrCode(-1);
         	result.setErrText("���÷���˱���ʧ��");
             conn.close();
             return result;
 		}
         conn.commit();
         conn.close();
         return result;
    }
    /**
     * �����龫��ҩ����
     * @param parm
     * @return
     */
    public TParm onCreateDeptOpdRequestSpc(TParm parm){
    	 TParm result = new TParm();
         TConnection conn = getConnection();
         result = INDTool.getInstance().onCreateDeptOPDRequestSpc(parm, conn);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             conn.close();
             return result;
         }
         conn.commit();
         conn.close();
         return result;
    }
    
    /**
     * �������쵥������������[���ұ�ҩ����]
     * @param parm
     * @return
     */
    public TParm onCreateDeptOdiRequestSpc(TParm parm){
   	 TParm result = new TParm();
        TConnection conn = getConnection();
        result = INDTool.getInstance().onCreateDeptOdiRequestSpc(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        
     /*   TParm reqParm =  parm.getParm("REQUEST_M");
        SpcIndRequestm indRequestm = getInsertM(reqParm);
//        System.out.println("reqParm---------:"+reqParm);
        TParm reqDParm = parm.getParm("REQUEST_D");
//        System.out.println("reqDParm---------:"+reqDParm);
        indRequestm = getInsertD(indRequestm, reqDParm);
       try{
	        String out = SpcService_SpcServiceImplPort_Client.onSaveSpcRequest(indRequestm);
	        
	        if( out != null &&  !out.equals("success") ){
	        	result.setErrCode(-1);
	        	result.setErrText(out);
	        	err("ERR:" + result.getErrCode() + result.getErrText()
	                    + result.getErrName());
	            conn.close();
	            return result;
	        }
        }catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace() ;
        	err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
        	result.setErrCode(-1);
        	result.setErrText("���÷���˱���ʧ��");
            conn.close();
            return result;
		}*/
        conn.commit();
        conn.close();
        return result;
   }
    
    /**
     * �������쵥������������[���������龫��ҩ����]
     * @param parm
     * @author shendr 2013.07.30
     * @return
     */
    public TParm onCreateDeptOpiRequestSpc(TParm parm){
    	 TParm result = new TParm();
         TConnection conn = getConnection();
         result = INDTool.getInstance().onCreateDeptOpiRequestSpc(parm, conn);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             conn.close();
             return result;
         }
         
      /*   TParm reqParm =  parm.getParm("REQUEST_M");
         SpcIndRequestm indRequestm = getInsertM(reqParm);
//         System.out.println("reqParm---------:"+reqParm);
         TParm reqDParm = parm.getParm("REQUEST_D");
//         System.out.println("reqDParm---------:"+reqDParm);
         indRequestm = getInsertD(indRequestm, reqDParm);
        try{
 	        String out = SpcService_SpcServiceImplPort_Client.onSaveSpcRequest(indRequestm);
 	        
 	        if( out != null &&  !out.equals("success") ){
 	        	result.setErrCode(-1);
 	        	result.setErrText(out);
 	        	err("ERR:" + result.getErrCode() + result.getErrText()
 	                    + result.getErrName());
 	            conn.close();
 	            return result;
 	        }
         }catch (Exception e) {
 			// TODO: handle exception
         	e.printStackTrace() ;
         	err("ERR:" + result.getErrCode() + result.getErrText()
                     + result.getErrName());
         	result.setErrCode(-1);
         	result.setErrText("���÷���˱���ʧ��");
             conn.close();
             return result;
 		}*/
         conn.commit();
         conn.close();
         return result;
    }

    /**
     * �������뵥״̬
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateFlg(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestDTool.getInstance().onUpdateFlg(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
}
