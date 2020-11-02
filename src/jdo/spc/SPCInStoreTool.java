package jdo.spc;

import java.util.List;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:�龫ҩ������
 * </p>
 * 
 * <p>
 * Description:�龫ҩ������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author fuwj 2012.10.23
 * @version 1.0
 */
public class SPCInStoreTool extends TJDOTool {

	/**
	 * ʵ��
	 */
	private static SPCInStoreTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PatAdmTool
	 */
	public static SPCInStoreTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCInStoreTool();
		return instanceObject;
	}
 
	/**
	 * ������
	 */
	public SPCInStoreTool() {
		setModuleName("spc\\SPCInStoreModule.x");
		onInit();
	}

	/**
	 * ��ѯ���ܹ���Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryCabinet(TParm parm) {
		TParm result = new TParm();
		result = query("queryCabinet", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * ��ѯ��־��Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryInfo(TParm parm) {
		TParm result = new TParm();
		result = query("queryInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * �����־��Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertLog(TParm parm) {
		TParm result = new TParm();
		result = update("insertLog", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * ��ѯ���� 
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryOrg(TParm parm) {
		TParm result = new TParm();
		result = query("queryOrg", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * �õ��龫��ˮ��
	 * 
	 * @return String
	 */
	public String getToxic() {
		return getResultString(call("getToxic"), "TOXIC_NO");
	}
	
	/**
	 * �õ��������
	 * 
	 * @return String
	 */
	public String getToxBox() {
		return getResultString(call("getToxBox"), "TOXBOX_NO");
	}


	/**
	 * �޸Ľ�������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateToxicm(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = update("updateToxicm", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * �޸Ľ����ӱ�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateToxicd(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = update("updateToxicd", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * �޸���������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateContainerM(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = update("updateContainerM", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * �޸������ӱ�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateContainerD(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = update("updateContainerD", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}
	
	/**
	 * �޸������ӱ�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateContainerDYF(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = update("updateContainerDYF", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}
	
	/**
	 * �龫ҩ�����ܹ�
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm inStock(TParm parm, TConnection conn) {
		TParm result = updateToxicm(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = updateToxicd(parm, conn);		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = updateContainerM(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = updateContainerD(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * �龫ҩ�����ܹ�
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm inStockYF(TParm parm, TConnection conn) {
		TParm result = updateToxicm(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = updateToxicd(parm, conn);		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = updateContainerM(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = updateContainerDYF(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * �޸Ľ�������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateToxicmAll(TParm parm) {  
		TParm result = new TParm();
		result = update("updateToxicmAll", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		
		return result;
	}
	
	   /**
	    * ���ݳ��ⵥ��ѯ�����Ӧ����Ϣ
	    * @param parm
	    * @return
	    */
	   public TParm onQueryDispenseD(TParm parm) {
		   
		   String dispenseNo = parm.getValue("DISPENSE_NO");
		   String seqNo = parm.getValue("SEQ_NO");
		   String containerId = parm.getValue("CONTAINER_ID");
		   if(dispenseNo == null || dispenseNo.equals("")){
			   return new TParm();
		   }
		   if(seqNo == null || seqNo.equals("")){  
			   return new TParm();
		   }
		   if(containerId == null || containerId.equals("")){
			   return new TParm();
		   }
		   String sql = "SELECT A.*,B.DOSAGE_QTY,B.STOCK_QTY,(SELECT COUNT(*) FROM IND_CONTAINERD WHERE CONTAINER_ID='"+containerId+"') AS OUT_QTY,A.SUP_ORDER_CODE FROM IND_DISPENSED A,PHA_TRANSUNIT B WHERE A.DISPENSE_NO='"+dispenseNo+"' AND A.SEQ_NO='"+seqNo+"' AND A.ORDER_CODE=B.ORDER_CODE";
		   return new TParm(TJDODBTool.getInstance().select(sql));
	   }
	                                
	   /**
	    * ���ݳ��ⵥ��ѯ�����Ӧ����Ϣ
	    * @param parm
	    * @return
	    */
	   public TParm onQueryDispenseM(TParm parm) {
		   
		   String dispenseNo = parm.getValue("DISPENSE_NO");
		   if(dispenseNo == null || dispenseNo.equals("")){
			   return new TParm();
		   }
		   String sql = " SELECT A.*,(SELECT COUNT(*) FROM IND_DISPENSED WHERE DISPENSE_NO='"+dispenseNo+"' AND IS_PUTAWAY='N') AS ORDERCOUNT FROM IND_DISPENSEM A WHERE A.DISPENSE_NO='"+dispenseNo+"' ";
		   
		   return new TParm(TJDODBTool.getInstance().select(sql));
	   }      
	   
	   /**
	    * ���������ܹ��ѯ����������Ϣ
	    * @param parm
	    * @return
	    */
	   public TParm queryContainer(TParm parm) {
		   String cabinetId = parm.getValue("CABINET_ID");
		   String sql ="SELECT A.CONTAINER_ID,A.CONTAINER_DESC,A.ORDER_CODE,B.ORDER_DESC," +
		   		"B.SPECIFICATION,(SELECT COUNT(*) FROM IND_CONTAINERD C WHERE C.CONTAINER_ID=A.CONTAINER_ID " +
		   		"AND C.CABINET_ID='"+cabinetId+"' ) AS NUM FROM IND_CONTAINERM A,PHA_BASE B WHERE " +
		   		"A.ORDER_CODE=B.ORDER_CODE AND A.CABINET_ID='"+cabinetId+"'";
		   return new TParm(TJDODBTool.getInstance().select(sql)); 
	   }	
	   
	   /**
	    * ��ѯ�龫��ϸ��Ϣ
	    * @param parm
	    * @return  
	    */
	   public TParm queryToxic(TParm parm) {
		   String cabinetId = parm.getValue("CABINET_ID");
		   String containerId = parm.getValue("CONTAINER_ID");
		   String sql = "SELECT ROWNUM,A.CONTAINER_ID,A.TOXIC_ID,A.BATCH_NO,A.VALID_DATE," +
		   		"B.UNIT_CHN_DESC FROM IND_CONTAINERD A,SYS_UNIT B WHERE A.UNIT_CODE=B.UNIT_CODE " +
		   		"AND A.CONTAINER_ID='"+containerId+"' AND A.CABINET_ID='"+cabinetId+"'";
		   return new TParm(TJDODBTool.getInstance().select(sql)); 
	   }
	   
	   /**
	    * ��ѯ�����Ž����
	    * @param parm
	    * @return  
	    */   
	   public TParm queryCabinetGuard(TParm parm) {
		   String cabinetId = parm.getValue("CABINET_ID");
		   String sql = "SELECT GUARD_ID FROM IND_CABINET_GUARD " +
		   		"WHERE CABINET_ID='"+cabinetId+"' AND IS_TOXIC_GUARD='Y'";
		   return new TParm(TJDODBTool.getInstance().select(sql)); 
	   }
	   
	   
}
