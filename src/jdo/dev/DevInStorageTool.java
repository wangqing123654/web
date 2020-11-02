package jdo.dev;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.dongyang.db.TConnection;

import jdo.sys.SystemTool;


/**
 * <p>Title:�豸��� </p>
 *
 * <p>Description:�豸��� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company:javahis </p>
 *
 * @author sundx
 * @version 1.0
 */
public class DevInStorageTool  extends TJDOTool{

    /**
     * ������
     */
    public DevInStorageTool() {
        setModuleName("dev\\DevInStorageModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    private static DevInStorageTool instanceObject;

    /**
     * �õ�ʵ��
     * @return MainStockRoomTool
     */
    public static DevInStorageTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DevInStorageTool();
        return instanceObject;
    }

    /**
     * �õ�����������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDevInStorageInf(TParm parm){
        if(parm.getValue("RECEIPT_NO").length() != 0 ||
           parm.getValue("RECEIPT_START_DATE").length() != 0 ||
           parm.getValue("RECEIPT_END_DATE").length() != 0)
            parm = query("selectDevInStorageInf1",parm);
        else
            parm = query("selectDevInStorageInf2",parm);
        return parm;
    } 
    /**
     * �õ����յ���Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDevReceipt(TParm parm){
        parm = query("selectDevReceipt",parm);
        return parm;
    }
 
    
    /**
     * �õ����յ���Ϣ
     * @param receiptNo String
     * @return TParm
     */
    public TParm selectReceiptD(String receiptNo){
        TParm parm = new TParm();
        parm.setData("RECEIPT_NO",receiptNo);
        parm = query("selectReceiptD",parm);
        return parm; 
    }
    
    /**
     * �õ����ⵥ��Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDevexwarehouse(TParm parm){
        parm = query("selectDevexwareHouse",parm);
        return parm;
    } 
    /** 
     * �õ����ⵥ��Ϣ
     * @param EXWAREHOUSENo String
     * @return TParm
     */ 
    public TParm selectDevexwarehouseD(String receiptNo){
        TParm parm = new TParm();
        parm.setData("EXWAREHOUSE_NO",receiptNo); 
        parm = query("selectDevexwarehouseD",parm);  
        return parm; 
    }
    
    /** 
     * �õ����ⵥ��Ź�����Ϣ
     * @param EXWAREHOUSENo String
     * @return TParm
     */
    public TParm selectDevexwarehouseDD(String receiptNo){
        TParm parm = new TParm();
        parm.setData("EXWAREHOUSE_NO",receiptNo);
        parm = query("selectDevexwarehouseDD",parm); 
        return parm;
    }
 
    /**
     * ȡ������
     * @param devCode String
     * @param depDate Timestamp
     * @param guarepDate Timestamp
     * @return TParm
     */
    public TParm getBatchNo(String devCode,Timestamp depDate,Timestamp guarepDate){
        TParm parm = new TParm();
        parm.setData("DEV_CODE",devCode);
        parm.setData("DEP_DATE",depDate);
        parm.setData("GUAREP_DATE",guarepDate);
        parm = query("getBatchNo",parm);
        return parm;
    }
    
    /**
     * ȡ������
     * @param devCode String
     * @param depDate Timestamp
     * @param guarepDate Timestamp
     * @return TParm 
     */
    public TParm getBatchNo(String devCode){
        TParm parm = new TParm();
        parm.setData("DEV_CODE",devCode);
        parm = query("getBatchNo",parm);
        return parm;
    }
    
    /**
     * ȡ���������
     * @param devCode String 
     * @return TParm
     */ 
    public TParm getMaxBatchNo(String devCode){
        TParm parm = new TParm();
        parm.setData("DEV_CODE",devCode);
        parm = query("getMaxBatchNo",parm);
        return parm;
    }

    /**
     * ȡ����ⵥ��
     * @return String
     */
    public String getInwarehouseNo(){
        return SystemTool.getInstance().getNo("ALL", "DEV",
                "INWAREHOUSE_NO", "INWAREHOUSE_NO");
    }

    /**
     * ȡ������豸���
     * @param devCode String
     * @return int
     */
    public int getMaxDevSeqNo(String devCode){
        TParm parm = new TParm();
        parm.setData("DEV_CODE",devCode);
      //�õ��豸���˳���
//        SELECT MAX(DEVSEQ_NO) DEVSEQ_NO 
//        FROM   DEV_STOCKDD 
//        WHERE  DEV_CODE=<DEV_CODE>  
        parm = query("getMaxDevSeqNo",parm);
        if(parm.getErrCode() < 0)              
            return 1;
        if(parm.getCount("DEVSEQ_NO") <= 0) 
            return 1;
        if(parm.getValue("DEVSEQ_NO",0) == null||
           parm.getValue("DEVSEQ_NO",0).length() == 0)
            return 1;    
        //system.out.println("ȡ������豸���"+parm.getInt("DEVSEQ_NO",0) + 1);
        return parm.getInt("DEVSEQ_NO",0) + 1;
    }

    /**
     * ȡ�õ�ǰʹ�õ�����
     * @param devCode String
     * @param depDate Timestamp
     * @param guarepDate Timestamp
     * @return int
     */
    public int getUseBatchSeq(String devCode,Timestamp depDate,Timestamp guarepDate){
        TParm parmBat = getBatchNo(devCode,depDate,guarepDate);
        //û��ѯ������
        if(parmBat.getErrCode() < 0)
            return 1;
        if(parmBat.getCount("BATCH_SEQ") > 0)
            return parmBat.getInt("BATCH_SEQ",0);  
        //�õ��������
        TParm parmMaxBat = getMaxBatchNo(devCode);
        if(parmMaxBat.getErrCode() < 0)   
            return 1;
        if(parmMaxBat.getCount("BATCH_SEQ") <= 0)
            return 1;
        if(parmMaxBat.getValue("BATCH_SEQ",0) == null ||
           parmMaxBat.getValue("BATCH_SEQ",0).length() == 0)
            return 1;
        return parmMaxBat.getInt("BATCH_SEQ",0) + 1;
    }

    /**
     * ȡ�õ�ǰʹ�õ�����
     * @param devCode String
     * @return int
     */
    public int getUseBatchSeq(String devCode){
        TParm parmBat = getBatchNo(devCode);
        //û��ѯ������ 
        if(parmBat.getErrCode() < 0)
            return 1;
        if(parmBat.getCount("BATCH_SEQ") > 0)
            return parmBat.getInt("BATCH_SEQ",0);  
        //�õ��������
        TParm parmMaxBat = getMaxBatchNo(devCode);
        if(parmMaxBat.getErrCode() < 0)   
            return 1;
        if(parmMaxBat.getCount("BATCH_SEQ") <= 0)
            return 1;
        if(parmMaxBat.getValue("BATCH_SEQ",0) == null ||
           parmMaxBat.getValue("BATCH_SEQ",0).length() == 0)
            return 1;
        return parmMaxBat.getInt("BATCH_SEQ",0) + 1;
    }
    
    
    /**
     * д���豸��ⵥ����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertDevInwarehouseM(TParm parm, TConnection connection){
    	List<TParm> parmList = getTParmList(parm);
    	//system.out.println("parmList"+parmList);
    	for (TParm tParm : parmList) { 
			parm = update("insertDevInwarehouseM",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm; 
			}
		} 
        
        return parm;
    }
    /**
     * д���豸�����ϸ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertDevInwarehouseD(TParm parm, TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	//for (TParm tParm : parmList) {  
    		//system.out.println("�ѵ�û���������"); 
			parm = update("insertDevInwarehouseD",parm,connection);
			//system.out.println("д���豸�����ϸ��parm"+parm); 
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm; 
			}
		//}
        
        return parm;
    }
    /**
     * д���豸�����ϸ��Ź���
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */ 
    public TParm insertDevInwarehouseDD(TParm parm, TConnection connection){
		//system.out.println("SEQMAN_FLG������"+parm.getValue("SEQMAN_FLG",0));
		if ("Y".equals(parm.getValue("SEQMAN_FLG"))) {  
			parm = update("insertDevInwarehouseDD",parm,connection);
			if (parm.getErrCode() < 0) {
				connection.close(); 
				return parm;      
			}
		}
        return parm;
    }
    /**
     * ������ⵥ�Ų�ѯ�豸�����ϸ��Ϣ
     * @param inwarehouseNo String
     * @return TParm
     */
    public TParm selectDevInwarehouseD(String inwarehouseNo){
        TParm parm = new TParm();
        parm.setData("INWAREHOUSE_NO",inwarehouseNo);
        return query("selectDevInwarehouseD",parm);
    }
    /**
     * ������ⵥ�Ų�ѯ�����ϸ��Ź�����Ϣ
     * @param inwarehouseNo String
     * @return TParm
     */
    public TParm selectDevInwarehouseDD(String inwarehouseNo){
        TParm parm = new TParm();
        parm.setData("INWAREHOUSE_NO",inwarehouseNo); 
        return query("selectDevInwarehouseDD",parm);
    }
    /**
     * �����豸�����ϸ��Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateDevInwarehouseM(TParm parm, TConnection connection){
        parm = update("updateDevInwarehouseM",parm,connection);   
        return parm;
    }
    /**
     * �����豸�����ϸ��Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateDevInwarehouseD(TParm parm, TConnection connection){
        parm = update("updateDevInwarehouseD",parm,connection);
        return parm;
    }
    /**
     * �����豸�����Ź�����ϸ��Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateDevInwarehouseDD(TParm parm, TConnection connection){
    	if(parm == null){
    		return null; 
    	}        
        parm = update("updateDevInwarehouseDD",parm,connection);
        return parm;
    }

    /**
     * �õ��豸��ǰ���
     * @param parm TParm
     * @return TParm
     */
    public TParm getStock(TParm parm){
        parm = query("getStock",parm);
        return parm;
    }
    
    /**
     * �����豸�����Ź�����ϸ��Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateStock(TParm parm, TConnection connection){
        parm = update("updateStock",parm,connection);
        return parm;
    }
    
    /**
     * �����豸��ϸ����Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateStockD(TParm parm, TConnection connection){
        parm = update("updateStockD",parm,connection);
        return parm;
    }
    
//    /**
//     * �����豸�����Ź�����ϸ��Ϣ
//     * @param parm TParm
//     * @param connection TConnection 
//     * @return TParm
//     */ 
//    public TParm updateStockDD(TParm parm, TConnection connection){
//        parm = update("updateStockDD",parm,connection);
//        return parm;
//    }
    
    /**
     * д���豸�����Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertStock(TParm parm, TConnection connection){
    	parm = update("insertStock",parm);
        return parm;
    }
    
    
    /**
     * �������߸��¿������
     * @param parm
     * @param connection
     * @return  
     */
    public TParm insertOrUpdateStockM(TParm parm, TConnection connection) {
    	    //��ѯStockm�����������������룬��������¿��
    	//system.out.println("�������߸��¿������parm"+parm);
    		if (0 == selectCountOfStock(parm, connection)) { 
    			//system.out.println("�½�"); 
				parm = insertStock(parm, connection);
				if (parm.getErrCode() < 0) {
					return parm;   
				}                         
			}else { 
				//system.out.println("����"); 
				parm = updateStock(parm, connection);
				if (parm.getErrCode() < 0) {
					return parm;
				} 
			}
		
    	 
    	return parm;
    }
    /**
     * д���豸���ϸ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm  
     */
    public TParm insertOrUpdateStockD(TParm parm, TConnection connection){
    	 //��ѯStockd�����������������룬��������¿��
    	//system.out.println("д���豸���ϸ��parm"+parm);
    			if (0 == selectCountOfStockD(parm, connection)) {
    				parm = update("insertStockD",parm,connection);
    				if (parm.getErrCode() < 0) {
    					return parm;
    				} 
    			}else {  
    				//���¿���� 
    				parm = updateStockD(parm, connection);
    				if (parm.getErrCode() < 0) {
    					return parm;
    				}	
    			}
        return parm;
    }
    
    
    /**
     * д���豸�����Ź�����Ϣ��ϸ��
     * @param parm TParm
     * @param connection TConnection 
     * @return TParm    
     */  
    public TParm insertOrUpdateStockDD(TParm parm, TConnection connection){
    	//��Ź����д��    
    	//system.out.println("��Ź���parm"+parm);
    		//system.out.println("�豸�����Ź�����Ϣ��ϸ��SEQMAN_FLGY"+parm.getValue("SEQMAN_FLG"));
    		if ("Y".equals(parm.getValue("SEQMAN_FLG"))) {
    			//if (0 == selectCountOfStockD(parm, connection)) {
    				parm = update("insertStockDD",parm,connection);
    				if (parm.getErrCode() < 0) {
    					return parm; 
    		//		}  
//    			}else {     
//    				//updateStockDD���²�����Ρ�����
//    				parm = updateStockDD(parm, connection);
//    				if (parm.getErrCode() < 0) {
//    					return parm;
//    				} 
    			}
			} 
    	
        return parm;
    }
    
    
    
    /**
     * ���ȷ�ϲ�����������߼�����(�жϿ��Һ��豸����)
     * @param parm
     * @param connection
     * @return  
     */ 
    public TParm insertStockM(TParm parm, TConnection connection) {
    	    //��ѯStockm�����������������룬��������¿��
    	        //system.out.println("�����������parm"+parm);
    			//system.out.println("�½�"); 
				parm = insertStock(parm, connection);
				if (parm.getErrCode() < 0) {
					return parm;   
				}                         

    	return parm;
    }
    /**
     * ���ȷ��һ���Բ�����ϸ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm  
     */
    public TParm insertStockD(TParm parm, TConnection connection){
    	 //��ѯStockd�����������������룬��������¿��
    				parm = update("insertStockD",parm,connection);
    				if (parm.getErrCode() < 0) {
    					return parm;
    				} 
        return parm;
    }
     
    
    /**
     * �����豸�����Ź�����Ϣ��ϸ��(DEPT_CODE��WAIT_DEPT_CODE)
     * @param parm TParm
     * @param connection TConnection 
     * @return TParm     
     */   
    public TParm UpdateStockDD(TParm parm, TConnection connection){
    	//��Ź����д��    
    	//system.out.println("��Ź���parm"+parm);
    		//system.out.println("�豸�����Ź�����Ϣ��ϸ��SEQMAN_FLGY"+parm.getValue("SEQMAN_FLG"));
    		if ("Y".equals(parm.getValue("SEQMAN_FLG"))) {
    				parm = update("updateStockDD",parm,connection);
    				if (parm.getErrCode() < 0) {
    					return parm; 
    			} 
			} 
    	
        return parm;
    }
    
    
    /**
     * �����豸�����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateStockDSumQty(TParm parm, TConnection connection){
        parm = update("updateStockDSumQty",parm,connection);
        return parm;
    }
    
    /**
     * ��ѯĳ�豸�ڿ�������е�����
     * @param parm
     * @param connection
     * @return
     */ 
    public int selectCountOfStock(TParm parm, TConnection connection) {
    	//system.out.println("��ѯĳ�豸�ڿ�������е�����parm"+parm);
    	parm = query("getCountOfStock", parm);  
    	//system.out.println("getCountOfStock-parm"+parm);
    	if (parm.getErrCode() < 0) {     
			connection.close();
		}       
    	//system.out.println("����1"+parm.getValue("NUM",0));
    	int count =0;   
    	if("0".equals(parm.getValue("NUM",0))||parm.getValue("NUM",0)== "0"){
    	   count = 0; 
    	}  
    	else {
    		count = 1;	
    	}
    	//system.out.println("count1"+count); 
    	return count;
    } 
    
    /** 
     * ��ѯĳ�豸�ڿ��ϸ���е����� 
     * @param parm
     * @param connection
     * @return  
     */
    public int selectCountOfStockD(TParm parm, TConnection connection) {
    	//system.out.println("��ѯĳ�豸�ڿ��ϸ���е����� parm"+parm);
    	parm = query("getCountOfStockD", parm);
    	//system.out.println("getCountOfStockD-parm"+parm);
    	if (parm.getErrCode() < 0) {   
			connection.close();
		}   
    	//system.out.println("����2"+parm.getValue("NUM",0)); 
    	int count =0;
    	if("0".equals(parm.getValue("NUM",0))||parm.getValue("NUM",0)=="0"){
    	  //system.out.println("��ѯĳ�豸�ڿ��ϸ���е�����"); 
    	   count = 0; 
    	}  
    	else {
    		count = 1;	
    	}
    	//system.out.println("count2"+count); 
    	return count;
    }
    //fux modify
    /**
     * ɾ�����ϸ��DEV_INWAREHOUSED
     * @param parm
     * @param connection
     * @return
     */
    public TParm deleteinwarehoused(TParm parm) {
    	parm = update("deleteinwarehoused", parm);
		if (parm.getErrCode() < 0) {
   		    err("ERR:" + parm.getErrCode() + parm.getErrText() +
			             parm.getErrName());
	        return parm;  
		}    
    	return parm;
    }   
    /**
     * ɾ��������б�DEV_INWAREHOUSEDD
     * @param parm
     * @param connection 
     * @return 
     */
    public TParm deleteinwarehousedd(TParm parm) {
    	parm = update("deleteinwarehousedd", parm);
    	if (parm.getErrCode() < 0) {
    		 err("ERR:" + parm.getErrCode() + parm.getErrText() +
    				      parm.getErrName());
    		return parm;
		}
    	return parm;
    }
    
    
    /**
     * ���յ�ϸ��״̬(�������յ�ϸ�����״̬)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm 
     */ 
    public TParm onUpdateDevReceiptD(TParm parm, TConnection conn){
        TParm result = this.update("updateDevReceiptD", parm, conn);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result; 
    }
    /**
     * ���յ�����״̬(�������յ��������״̬)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */ 
    public TParm onUpdateDevReceiptM(TParm parm, TConnection conn) {
        TParm result = this.update("updateDevReceiptM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ���³��ⵥϸ�����״̬
     * @param parm TParm
     * @param conn TConnection
     * @return TParm 
     */ 
    public TParm onUpdateDevExWareHouseD(TParm parm, TConnection conn){
        TParm result = this.update("UpdateDevExWareHouseD", parm, conn);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;  
    }  
     
    /**
     * ���³��ⵥ�������״̬  
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */ 
    public TParm onUpdateDevExWareHouseM(TParm parm, TConnection conn) {
        TParm result = this.update("UpdateDevExWareHouseM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * ������parmת����parmList
     * @param parm
     * @return
     */
    public List<TParm> getTParmList(TParm parm){
    	// ���ؽ��list
    	List<TParm> parmList = new ArrayList<TParm>();
    	TParm tempParm;
    	String[] names = parm.getNames();
    	// һ��parm���Ŷ���������
    	int count = parm.getCount(names[0]);
    	for (int i=0; i<count; i++) {
			tempParm = new TParm();
			for (String name : names) {
				tempParm.setData(name, parm.getData(name, i));
			}
			parmList.add(tempParm);
		}
    	
    	return parmList;
    }
}
