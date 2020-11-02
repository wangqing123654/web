package action.dev;

import java.util.List;

import jdo.dev.DevCheckTool;
import jdo.dev.DevInStorageTool;
import jdo.dev.DevOutRequestDTool;
import jdo.dev.DevOutRequestMTool;
import jdo.dev.DevOutStorageTool;
import jdo.dev.DevPurChaseTool;
import jdo.dev.DevRequestTool;
import jdo.dev.DevTrackTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: �豸ACTION</p>  
 *
 * <p>Description:�豸ACTION</p>
 *  
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: javahis</p>  
 *
 * @author  fux  
 * @version 1.0
 */  
public class DevAction extends TAction {
	/**
	 * �豸�빺����
	 * 
	 * @param parm
	 *            TParm   
	 * @return TParm    DEV_PURCHASEM
	 */
	public TParm saveDevPurChase(TParm parm) {
		TParm result = new TParm(); 
		TConnection connection = getConnection();
		result = DevPurChaseTool.getInstance().saveDevPurChase(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		return result;
	}

	/**
	 * �豸�빺ϸ����
	 * 
	 * @param parm
	 *            TParm   
	 * @return TParm    DEV_PURCHASED
	 */  
	public TParm saveDevPurChased(TParm parm) {
		TParm result = new TParm(); 
		TConnection connection = getConnection();
		result = DevPurChaseTool.getInstance().saveDevPurChase(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		return result;
	}
	/**
	 * �豸��Ᵽ��
	 * 
	 * @param parm  
	 *            TParm  
	 * @return TParm 
	 */  
	public TParm generateInStorageReceipt(TParm parm) {
		TParm result = new TParm();
		                           
		TConnection connection = getConnection(); 
		// ������� %
		result = DevInStorageTool.getInstance().insertDevInwarehouseM(parm.getParm("DEV_INWAREHOUSEM"), connection);
		System.out.println("�������result"+result);
		connection.commit();
		System.out.println("// ������� %");
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();
			return result;
		}
		// ���ϸ��  %
		TParm inWareHouseD = parm.getParm("DEV_INWAREHOUSED");  
		System.out.println("inWareHouseD"+inWareHouseD);
	    for (int i = 0; i < inWareHouseD.getCount("INWAREHOUSE_NO"); i++) { 
	    	  System.out.println("// ���ϸ��  %");
		result = DevInStorageTool.getInstance().insertDevInwarehouseD(inWareHouseD.getRow(i), connection);
		System.out.println("���ϸ�� result"+result); 
		
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();  
			return result;       
		}    
		connection.commit();
		  } 
		// �����ϸ�� % ORGIN_CODE
	    TParm inWareHouseDD = parm.getParm("DEV_INWAREHOUSEDD");
	    System.out.println("inWareHouseDD"+inWareHouseDD);
	    for (int i = 0; i < inWareHouseDD.getCount("INWAREHOUSE_NO"); i++) { 
	    	System.out.println("// �����ϸ�� % ORGIN_CODE");
//	    	if(inWareHouseDD.getBoolean(""))
		result = DevInStorageTool.getInstance().insertDevInwarehouseDD(inWareHouseDD.getRow(i), connection);
		System.out.println("�����ϸ��result"+result);
		connection.commit(); 
		if (result.getErrCode() < 0) { 
			//connection.rollback();
			connection.close();
			return result;       
		}   
	    }
	    
	   	    
		// �������(��������)  %
	    TParm stockM = parm.getParm("DEV_STOCKM");
	    for (int i = 0; i < stockM.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertOrUpdateStockM(stockM.getRow(i), connection);
		System.out.println(" �������result"+result);
		connection.commit();
		if (result.getErrCode() < 0) { 
			//connection.rollback();
			connection.close();
			return result;                            }   
	    }
	    System.out.println("// �������(��������)  %");
		// ���ϸ��  %
        TParm stockD = parm.getParm("DEV_STOCKD");
        for (int i = 0; i < stockD.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertOrUpdateStockD(stockD.getRow(i), connection);
		System.out.println(" ���ϸ��result"+result);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close(); 
			return result;    
		            }              
        }
        System.out.println("// ���ϸ��  %");
		// �����ϸ��--��Ź����豸  % 
        TParm stockDD = parm.getParm("DEV_STOCKDD");
        for (int i = 0; i < stockDD.getCount("DEV_CODE"); i++) {  
		result = DevInStorageTool.getInstance().insertOrUpdateStockDD(stockDD.getRow(i), connection);
		System.out.println(" �����ϸ��result"+result);
		connection.commit(); 
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close(); 
			return result; 
		   }          
        } 
        System.out.println("// �����ϸ��--��Ź����豸  % ");
		return result;
	}

	
	/**
	 * �豸��Ᵽ��
	 * 
	 * @param parm  
	 *            TParm   
	 * @return TParm 
	 */  
	public TParm generateOutReqInStorageReceipt(TParm parm) {
		TParm result = new TParm();
		                           
		TConnection connection = getConnection(); 
		// ������� %
		result = DevInStorageTool.getInstance().insertDevInwarehouseM(parm.getParm("DEV_INWAREHOUSEM"), connection);
		System.out.println("�������result"+result);
		connection.commit();
		System.out.println("// ������� %");
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();
			return result; 
		}
		// ���ϸ��  %
		TParm inWareHouseD = parm.getParm("DEV_INWAREHOUSED");  
		System.out.println("inWareHouseD"+inWareHouseD);
	    for (int i = 0; i < inWareHouseD.getCount("INWAREHOUSE_NO"); i++) { 
	    	  System.out.println("// ���ϸ��  %");
		result = DevInStorageTool.getInstance().insertDevInwarehouseD(inWareHouseD.getRow(i), connection);
		System.out.println("���ϸ�� result"+result); 
		
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();  
			return result;       
		}     
		connection.commit();
		  }  
		// �����ϸ�� % ORGIN_CODE
	    TParm inWareHouseDD = parm.getParm("DEV_INWAREHOUSEDD");
	    System.out.println("inWareHouseDD"+inWareHouseDD);
	    for (int i = 0; i < inWareHouseDD.getCount("INWAREHOUSE_NO"); i++) { 
	    	System.out.println("// �����ϸ�� % ORGIN_CODE");
//	    	if(inWareHouseDD.getBoolean(""))
		result = DevInStorageTool.getInstance().insertDevInwarehouseDD(inWareHouseDD.getRow(i), connection);
		System.out.println("�����ϸ��result"+result);
		connection.commit(); 
		if (result.getErrCode() < 0) { 
			//connection.rollback();
			connection.close();
			return result;       
		}   
	    }
	    
	   	    
		// �������(��������)  %
	    TParm stockM = parm.getParm("DEV_STOCKM");
	    for (int i = 0; i < stockM.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertStockM(stockM.getRow(i), connection);
		System.out.println(" �������result"+result);
		connection.commit();
		if (result.getErrCode() < 0) { 
			//connection.rollback();
			connection.close();
			return result;                            }   
	    }
	    System.out.println("// �������(��������)  %");
		// ���ϸ��  %
        TParm stockD = parm.getParm("DEV_STOCKD");
        for (int i = 0; i < stockD.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertStockD(stockD.getRow(i), connection);
		System.out.println(" ���ϸ��result"+result);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close(); 
			return result;    
		            }              
        }
        System.out.println("// ���ϸ��  %");
		// �����ϸ��--��Ź����豸  % 
        TParm stockDD = parm.getParm("DEV_STOCKDD");
        for (int i = 0; i < stockDD.getCount("DEV_CODE"); i++) {  
		result = DevInStorageTool.getInstance().UpdateStockDD(stockDD.getRow(i), connection);
		System.out.println(" �����ϸ��result"+result);
		connection.commit(); 
		if (result.getErrCode() < 0) {
			//connection.rollback(); 
			connection.close(); 
			return result; 
		   }          
        } 
        System.out.println("// �����ϸ��--��Ź����豸  % ");
		return result;
	}

	
	
	
	
	
	
	
	
	
	
	/**
	 * �豸��Ᵽ��(update)(���   �����)
	 * 
	 * @param parm 
	 *            TParm
	 * @return TParm     
	 */ 
	public TParm updateInStorageReceipt(TParm parm) {
		TParm result = new TParm();

		TConnection connection = getConnection();
		// �������
		result = DevInStorageTool.getInstance().updateDevInwarehouseM(parm.getParm("DEV_INWAREHOUSEM"), connection);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();
			return result;
		}
		
		
		// ���ϸ�� 
		TParm inWareHouseD = parm.getParm("DEV_INWAREHOUSED");
	    for (int i = 0; i < inWareHouseD.getCount("INWAREHOUSE_NO"); i++) { 
		result = DevInStorageTool.getInstance().updateDevInwarehouseD(inWareHouseD.getRow(i), connection);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();
			return result;       
		}       
		  } 
		// �����ϸ��
	    TParm inWareHouseDD = parm.getParm("DEV_INWAREHOUSEDD");
	    for (int i = 0; i < inWareHouseDD.getCount("INWAREHOUSE_NO"); i++) { 
		result = DevInStorageTool.getInstance().updateDevInwarehouseDD(inWareHouseDD.getRow(i), connection);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();
			return result;       
		}   
	    }
		// �������(��������)  �Ƿ���£�
	    TParm stockM = parm.getParm("DEV_STOCKM");
	    for (int i = 0; i < stockM.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertOrUpdateStockM(stockM.getRow(i), connection);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback(); 
			connection.close();
			return result;                            }   
	    }
		  
		// ���ϸ��  ���¿��
        TParm stockD = parm.getParm("DEV_STOCKD"); 
        for (int i = 0; i < stockD.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertOrUpdateStockD(stockD.getRow(i), connection);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback(); 
			connection.close();
			return result;   
		            }              
        } 
		// �����ϸ��--��Ź����豸������
        TParm stockDD = parm.getParm("DEV_STOCKDD"); 
        for (int i = 0; i < stockDD.getCount("DEV_CODE"); i++) { 
		result = DevInStorageTool.getInstance().insertOrUpdateStockDD(stockDD.getRow(i), connection);
		connection.commit();
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();
			return result;
		   }         
        }
		return result;
	}	
	/**
	 * �豸���챣��(����)
	 * @param parm
	 * @return 
	 */
	public TParm InsertRequest(TParm parm) {  
		System.out.println("STARTTTTTTT--INSERT"); 
		TParm result = new TParm(); 
		TConnection connection = getConnection(); 
		// ������������ 
		result = DevOutRequestMTool.getInstance().createNewRequestM(parm.getParm("DEV_REQUESTMM"), connection);
		System.out.println("��result"+result); 
		if (result.getErrCode() < 0) {
			connection.close();  
			return result;   
		}    
		// ����������ϸ�� 
		result = DevOutRequestDTool.getInstance().createNewRequestD(parm.getParm("DEV_REQUESTDD"), connection);
		System.out.println("ϸresult"+result);
		if (result.getErrCode() < 0) { 
			connection.close();   
			return result;   
		}   
		
		connection.commit();
		return result;

	}
	/**
	 * �豸���챣��(����)
	 * @param parm
	 * @return
	 */
	public TParm UpdateRequest(TParm parm) {
		System.out.println("STARTTTTTTT----UPDATE"); 
		TParm result = new TParm();
		TConnection connection = getConnection(); 
		// ������������    
		result = DevOutRequestMTool.getInstance().updateRequestM(parm.getParm("DEV_REQUESTMM"), connection);
		if (result.getErrCode() < 0) {  
			connection.close();   
			return result;    
		}
		// ����������ϸ��  
		result = DevOutRequestDTool.getInstance().updateRequestD(parm.getParm("DEV_REQUESTDD"), connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}   
//	    // ����������ϸ��״̬
//	    result = DevOutRequestDTool.getInstance().updateRequestDDFlg(parm.getParm("DEV_REQUESTDD"), connection);
//			if (result.getErrCode() < 0) {
//				connection.close();
//				return result;
//				}
		connection.close();
		connection.commit();
		return result;
	}
	/**
	 * �豸���Ᵽ��(new)
	 * @param parm
	 * @return
	 */
	public TParm generateExStorageReceipt(TParm parm) {
		TParm result = new TParm();
		
		TConnection connection = getConnection();
		// ��������
		result = DevOutStorageTool.getInstance().insertExStorgeM(parm.getParm("DEV_EXWAREHOUSEM"), connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		
		TParm ParmD = parm.getParm("DEV_EXWAREHOUSED"); 
		System.out.println("������ϸ��ParmD"+ParmD);
		// ������ϸ��
		result = DevOutStorageTool.getInstance().insertExStorgeD(ParmD, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;  
		}     
		TParm ParmDD = parm.getParm("DEV_EXWAREHOUSEDD");
		System.out.println("���������ϸ��ParmDD"+ParmDD); 
		// ���������ϸ��
		result = DevOutStorageTool.getInstance().insertExStorgeDD(ParmDD, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;  
		} 

		// ���������ϸ���Ͳ���DD������(δ���) 
		TParm StockMParm = parm.getParm("DEV_STOCKM");  


		result = DevOutStorageTool.getInstance().deductStockM(StockMParm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;  
		}  
		//fux need modify
//		TParm StockDParm = parm.getParm("DEV_STOCKD"); 
//		result = DevOutStorageTool.getInstance().deductStockD(StockDParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;  
//		}  
		//����
		TParm StockDDParm = parm.getParm("DEV_STOCKDD");  
		result = DevOutStorageTool.getInstance().updateStockdd(StockDDParm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;  
		}  
////		result = DevOutStorageTool.getInstance().updateStockMD(tmepParm, connection);
//		TParm requestMParm = parm.getParm("DEV_REQUESTM"); 
//		TParm requestDParm = parm.getParm("DEV_REQUESTD"); 
//		//������������
//		result = DevOutStorageTool.getInstance().UpdateRequsetMFinal(requestMParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;    
//		}  
//		//��������ϸ��
//		result = DevOutStorageTool.getInstance().UpdateRequsetDFate(requestDParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;  
//		}  
		connection.commit();  
		return result;
	}
	/**
	 * �豸���Ᵽ��(update)
	 * @param parm
	 * @return
	 */
	public TParm updateExStorageReceipt(TParm parm) {
		TParm result = new TParm();
		
		TConnection connection = getConnection();
		// ��������
		result = DevOutStorageTool.getInstance().insertExStorgeM(parm.getParm("DEV_EXWAREHOUSEM"), connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		
		TParm tmepParm = parm.getParm("DEV_EXWAREHOUSED");
		// ������ϸ��
		result = DevOutStorageTool.getInstance().insertExStorgeD(tmepParm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// �������������(δ���)
//		result = DevOutStorageTool.getInstance().deductStockM(tmepParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;  
//		}  
//		// ��Ź��� ������ϸ������������Ź��� ����/������������
//		result = DevOutStorageTool.getInstance().updateStockMD(tmepParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;  
//		} 
		// �޸�DEV_RFIDBASE������������Ϣ
//		result = DevOutStorageTool.getInstance().updateRFIDBase(tmepParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;
//		}  
		  
		connection.commit();
		return result;
	}
	
	
	/**
	 * �豸׷�� ����
	 * @param parm
	 * @return
	 */
	public TParm onTrackOut(TParm parm) {
		TParm result = new TParm();
//		TConnection connection = getConnection(); 
		// �����豸׷�ٱ�
		result = DevTrackTool.getInstance().insertDevTrackInf(parm.getParm("OUT"));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// ����DEV_RFIDBASE�� ״̬Ϊ����
		result = DevTrackTool.getInstance().updateDevRFIDBaseOnpass(parm.getParm("OUT"));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
	//	connection.commit();
		return result;
	}
	
	
	/**
	 * �豸׷�� ���
	 * @param parm
	 * @return
	 */
	public TParm onTrackIn(TParm parm) {
		TParm result = new TParm();
	//	TConnection connection = getConnection();
		System.out.println("1:" + parm);
		// �����豸׷�ٱ�
		result = DevTrackTool.getInstance().insertDevTrackInf(parm.getParm("IN"));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		System.out.println("2:" + parm);
		// ����DEV_RFIDBASE�� ״̬Ϊ����
		result = DevTrackTool.getInstance().updateDevRFIDBaseNormal(parm.getParm("IN"));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
//		connection.commit();
		return result;
	}
	
	
	/**
	 * �����̵������¿���
	 * @param parm
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public TParm updateStockByCheck(TParm parm) {
		TParm result = new TParm();
		TConnection connection = getConnection();
		List<TParm> parmList = (List<TParm>)parm.getData("update");
		result = DevCheckTool.getInstance().updateStockByCheck(parmList, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		return result;
	}
	
	
	/**
     * �豸�빺����
     * @param parm TParm
     * @return TParm
     */
    public TParm saveDevRequest(TParm parm){ 
        TParm result = new TParm(); 
        TConnection connection = getConnection();
        result = DevRequestTool.getInstance().saveDevRequest(parm,connection);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        connection.commit();
        return result;
    }
} 
