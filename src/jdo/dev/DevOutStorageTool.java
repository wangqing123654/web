package jdo.dev;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.SystemTool;

/**
 * <p>Title: ���⹤����</p>
 *
 * <p>Description:���⹤���� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>  
 *
 * @author sundx 
 * @version 1.0
 */
public class DevOutStorageTool extends TJDOTool{

    /**
     * ������
     */
    public DevOutStorageTool() {
        setModuleName("dev\\DevOutStorageModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    private static DevOutStorageTool instanceObject;

    /**
     * �õ�ʵ��
     * @return MainStockRoomTool
     */
    public static DevOutStorageTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DevOutStorageTool();
        return instanceObject;
    }
    /**
     * ȡ�ó��ⵥ��
     * @return String
     */
    public String getExwarehouseNo(){
        return SystemTool.getInstance().getNo("ALL", "DEV",
                "EXWAREHOUSE_NO", "EXWAREHOUSE_NO"); 
    }

    /**
     * �õ����ⵥ��Ϣ  
     * @param parm TParm
     * @return TParm 
     */ 
    public TParm selectDevOutStorageInf(TParm parm){
        parm = query("selectDevOutStorageInf",parm);
        //system.out.println("�õ����ⵥ��Ϣparm"+parm);
        return parm;
    }  
    /** 
     * �õ��豸������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm getExStorgeInf(TParm parm){
        if(parm.getValue("SEQMAN_FLG").equals("N"))
            parm = query("getExStorgeInf1",parm);
        else
            parm = query("getExStorgeInf2",parm);
        return parm;
    }

    /**
     * ȡ�ó����豸��ǰ���
     * @param parm TParm
     * @return TParm
     */
    public TParm getDeptExStorgeQty(TParm parm){
        parm = query("getDeptExStorgeQty",parm);
        return parm;
    }

    /**
     * ɾ���豸�����Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteExStorgeQty(TParm parm,TConnection connection){
        parm = update("deleteExStorgeQty",parm,connection);
        return parm;
    }

    /**
     * �޸��豸�����Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm modifyStorgeQty(TParm parm,TConnection connection){
        parm = update("modifyStorgeQty",parm,connection);
        return parm;
    }
 
    /**
     * д���豸������ϸ��Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertExStorgeD(TParm parm,TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("insertExStorgeD",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }

    
    /**
     * д���豸������ϸ��Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertExStorgeDD(TParm parm,TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("insertExStorgeDD",tParm,connection);
			if (parm.getErrCode() < 0) { 
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }
    
    /**
     * д���豸�����Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertStock(TParm parm,TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("insertStock",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }
    /**
     * �����豸�����Ź�����Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateStockD(TParm parm,TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("updateStockD",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }

    /**
     * д���豸����������Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertExStorgeM(TParm parm,TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) { 
			parm = update("insertExStorgeM",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }

    /**
     * �����豸������ϸ��Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateExStorgeD(TParm parm,TConnection connection){
        parm = update("updateExStorgeD",parm,connection);
        return parm;
    }
    
    /**
     * �������쵥�Ų�ѯ�豸������ϸ��Ϣ
     * @param exWarehouseNo String
     * @return TParm
     */
    public TParm queryRequestD(String requsetD){
        TParm parm = new TParm();
        parm.setData("REQUEST_NO",requsetD);
        parm = query("REQUEST_NO",parm); 
        return parm;
    }
    /**
     * ���ݳ��ⵥ�Ų�ѯ�豸��;������ϸ��Ϣ
     * @param exWarehouseNo String
     * @return TParm
     */
    public TParm queryZTExStorgeD(String exWarehouseNo){
        TParm parm = new TParm();
        parm.setData("EXWAREHOUSE_NO",exWarehouseNo);
        parm = query("queryZTExStorgeD",parm); 
        return parm;
    } 
    /**
     * ���ݳ��ⵥ�Ų�ѯ�豸����ɳ�����ϸ��Ϣ
     * @param exWarehouseNo String
     * @return TParm
     */
    public TParm queryWCExStorgeD(String exWarehouseNo){
        TParm parm = new TParm();
        parm.setData("EXWAREHOUSE_NO",exWarehouseNo);
        parm = query("queryWCExStorgeD",parm);
        return parm; 
    }

    
    
    /**
     * ���ݳ��ⵥ�Ų�ѯ�豸������Ź�����ϸ��Ϣ
     * @param exWarehouseNo String
     * @return TParm
     */
    public TParm queryExStorgeDD(String exWarehouseNo){
        TParm parm = new TParm();
        parm.setData("EXWAREHOUSE_NO",exWarehouseNo);
        parm = query("queryExStorgeDD",parm);
        return parm;
    }
    
    /** 
     * ���ݳ��ⵥ�Ų�ѯ�豸������Ϣ
     * @param exWarehouseNo String
     * @return TParm
     */
    public TParm queryExReceiptData(String exWarehouseNo){
        TParm parm = new TParm();
        parm.setData("EXWAREHOUSE_NO",exWarehouseNo);
        parm = query("queryExReceiptData",parm);
        return parm;
    }

    /**
     * �����豸�����Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateStockM(TParm parm,TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("updateStockM",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }
    
    /**
     * �۳��������
     * @param parm
     * @param connection
     * @return
     */
    public TParm deductStockM(TParm parm, TConnection connection) {
    	List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("deductStockMQty",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }
    

    
    /**
     * ����DEV_STOCKD������
     * ʵ����updateRFID�豸��dept_code
     * �ɳ������ĳ�������
     * @param parm
     * @param connection
     * @return
     */
    public TParm updateStockMD(TParm parm, TConnection connection) {
    	List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
    		// �������Ź��� update dev_code
    		if ("Y".equals(tParm.getData("SEQMAN_FLG"))) {
    			parm = update("updateStockDRFID",tParm,connection);
    			if (parm.getErrCode() < 0) {
					connection.close();
					return parm;
				}
			}
    		
			// ���������û�и��豸
			if (0 == getCountOfStockD(tParm)) {
				parm = update("insertStock",tParm,connection);
				if (parm.getErrCode() < 0) {
					connection.close();
					return parm;
				}
			}else { // ������ڸ��豸�������ӿ����
				parm = addStockMQty(tParm, connection);
				if (parm.getErrCode() < 0) {
					connection.close();
					return parm;
				}
			}
		}
        
        return parm;
    }
    
    /**
     * �۳������ϸ�� 
     * @param parm
     * @param connection
     * @return
     */
    public TParm deductStockD(TParm parm, TConnection connection) {
    	List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) { 
			parm = update("deductStockDQty",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			} 
		}
        
        return parm;
    }
    
    /**
     * ����DEV_CODE  DEPT_CODE  BATCH_SEQ
     * ��ѯ���豸�� DEV_STOCKM���е�����
     * @param parm
     * @return
     */
    public int getCountOfStockD(TParm parm) {
    	parm = query("getCountOfStock", parm);
    	if (parm.getErrCode() < 0) {
			return -1;
		}
    	int num = parm.getInt("NUM", 0);
    	return num;
    }
    
    /**
     * ����ʱ�����������ҵ��豸���� 
     * @param parm
     * @param connection
     * @return
     */
    public TParm addStockMQty(TParm parm, TConnection connection) {
    	parm = update("addStockMQty", parm, connection);
    	return parm;
    }
    
    /**
     * ���ݳ��ⵥ�Ų�ѯ�豸������ϸ��Ϣ
     * @param exWarehouseNo String
     * @return TParm
     */
    public TParm queryStockDD(String devCode,String deptCode){
        TParm parm = new TParm();  
        parm.setData("DEV_CODE",devCode);   
        parm.setData("DEPT_CODE",deptCode);
        parm = query("queryStockDD",parm);  
        return parm;              
    }
    
    
    /**
     * �������stockDD�����
     * @param parm
     * @param connection
     * @return
     */
    public TParm updateStockdd(TParm parm, TConnection connection) {
    	List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {  
			parm = update("updateStockDD",tParm,connection);
			if (parm.getErrCode() < 0) { 
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }
    
    /**
     * ����ظ���������
     * @param parm
     * @param connection
     * @return
     */
    public TParm UpdateRequsetMFinal(TParm parm, TConnection connection) {
    	List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) { 
			parm = update("UpdateRequsetMFinal",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }
    
    
    
    
    
    /**
     * ����ظ�����ϸ��
     * @param parm
     * @param connection
     * @return
     */ 
    public TParm UpdateRequsetDFate(TParm parm, TConnection connection) {
    	List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("UpdateRequsetDFate",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
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
    
    
    /**
     * ���տ��Һ��豸�����ѯSTOCKD������
     * @param parm TParm
     * @return TParm 
     */ 
    public TParm selectDevStockD(TParm parm){
        parm = query("selectDevStockD",parm);
        return parm;
    } 

}
