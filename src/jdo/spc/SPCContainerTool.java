package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: ����
 * </p>
 * 
 * <p>
 * Description:TODO
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author Yuanxm
 * @version 1.0
 */
public class SPCContainerTool extends TJDOTool {

	/**
	 * ʵ��
	 */
	public static SPCContainerTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INDTool
	 */
	public static SPCContainerTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCContainerTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SPCContainerTool() {
		setModuleName("spc\\SPCContainerModule.x");
		onInit();
	}

	/**
	 * ��ѯ������Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryInfo(TParm tparm) {
		TParm result = new TParm();
		result = query("queryInfo", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * �������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertInfo(TParm parm) {
		TParm result = new TParm();
		result = update("insertInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * ��������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateInfo(TParm parm) {
		TParm result = new TParm();
		result = update("updateInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * ɾ������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm deleteInfo(TParm parm) {
		TParm result = new TParm();
		result = update("deleteInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * ��ѯ������ϸ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryMxInfo(TParm tparm) {
		TParm result = new TParm();
		result = query("queryMxInfo", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * �������ҩ��ˮ�Ų�ѯҩƷ���
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryMXByToxicId(TParm tparm) {
		TParm result = new TParm();
		result = query("queryMXByToxicId", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * �������ҩ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateOrderCode(TParm parm) {
		TParm result = new TParm();
		result = update("updateOrderCode", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * �鿴�Ƿ�Ϊ���ҩ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryISMj(TParm tparm) {
		TParm result = new TParm();
		result = query("queryISMj", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * �����龫��ˮ�ţ�ҩƷ�����ѯ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQuery(TParm parm) {

		// �龫��ˮ��
		String toxicId = parm.getValue("TOXIC_ID");
		// ҩƷCODE
		String orderCode = parm.getValue("ORDER_CODE");
		// ���ܹ�
		String cabinetId = parm.getValue("CABINET_ID");

		if (StringUtil.isNullString(toxicId)
				|| StringUtil.isNullString(orderCode)) {
			return new TParm();
		}

		String sql = " SELECT A.CONTAINER_DESC,A.CONTAINER_ID,B.ORDER_CODE,B.BATCH_NO, "
				+ " 		  B.VALID_DATE,B.VERIFYIN_PRICE,B.BATCH_SEQ,B.TOXIC_ID,B.UNIT_CODE , "
				+ "           B.SUP_ORDER_CODE,B.SUP_CODE,B.INVENT_PRICE,B.RETAIL_PRICE "
				+ " FROM IND_CONTAINERM A,IND_CONTAINERD B "   
				+ " WHERE  A.CONTAINER_ID=B.CONTAINER_ID  " 
				+ "  AND  B.TOXIC_ID='"
				+ toxicId
				+ "' AND B.CABINET_ID='"
				+ cabinetId
				+ "' AND B.ORDER_CODE='"
				+ orderCode
				+ "' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * ����������ѯ��Ӧ������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQueryBy(TParm parm) {

		String containerId = parm.getValue("CONTAINER_ID");
		if (StringUtil.isNullString(containerId)) {
			return new TParm();
		}
		String sql = " SELECT A.CONTAINER_ID,A.CONTAINER_DESC,A.TOXIC_QTY,A.ORDER_CODE "
				+ " FROM IND_CONTAINERM A "
				+ " WHERE A.CONTAINER_ID='"
				+ containerId + "' ";

		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * �����:��ѯ�������е��龫
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQueryByContainerId(TParm parm) {
		String containerId = parm.getValue("CONTAINER_ID");
		String cabinetId = parm.getValue("CABINET_ID");
		if (StringUtil.isNullString(containerId)) {
			return new TParm();
		}
		String sql = " SELECT A.CONTAINER_DESC,A.CONTAINER_ID,B.ORDER_CODE,B.BATCH_NO, B.BATCH_NO, "
				+ " 		  B.VALID_DATE,B.VERIFYIN_PRICE,B.BATCH_SEQ,B.TOXIC_ID,B.UNIT_CODE  "
				+ " FROM IND_CONTAINERM A,IND_CONTAINERD B "
				+ " WHERE  A.CONTAINER_ID=B.CONTAINER_ID "
				+ "     AND A.CONTAINER_ID='"
				+ containerId
				+ "' AND B.CABINET_ID='" + cabinetId + "' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * ����
	 */
	public TParm onUpdate(TParm parm, TConnection conn) {
		String containerId = parm.getValue("CONTAINER_ID");
		String toxicId = parm.getValue("TOXIC_ID");

		if ((containerId == null || containerId.equals(""))
				|| (toxicId == null || toxicId.equals(""))) {
			return new TParm();
		}

		String sql = " UPDATE IND_CONTAINERD SET CABINET_ID='' ,CONTAINER_ID='"
				+ containerId + "'  " + " WHERE TOXIC_ID='" + toxicId + "' ";

		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	/**
	 * ����CONTAINERM��
	 */
	public TParm onUpdateM(TParm parm, TConnection conn) {
		String containerId = parm.getValue("CONTAINER_ID");

		if ((containerId == null || containerId.equals(""))) {
			return new TParm();
		}

		String sql = " UPDATE IND_CONTAINERM SET CABINET_ID=''  "
				+ " WHERE CONTAINER_ID='" + containerId + "' ";
		System.out.println("onUpdateM-------:" + sql);
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	/**
	 * ����IND_CONTAINERM��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onUpdateM(TParm parm) {
		String sql = "UPDATE IND_CONTAINERM A SET A.CABINET_ID=''  "
				+ "     WHERE (SELECT COUNT(TOXIC_ID) FROM IND_CONTAINERD B WHERE B.CONTAINER_ID=A.CONTAINER_ID)<=0";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	public TParm onQuerySysParm() {
		String sql = "SELECT TOXIC_LENGTH FROM IND_SYSPARM ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm != null && parm.getCount() > 0) {
			return parm.getRow(0);
		}
		return new TParm();
	}

	/**
	 * ��ѯ�龫ҩ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryJjMj(TParm tparm) {
		TParm result = new TParm();
		result = query("queryJjMj", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ����������ѯ�龫ҩ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryJjContainer(TParm tparm) {
		TParm result = new TParm();
		result = query("queryJjContainer", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ�龫���볤��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryBarCodeSize() {
		String sql = "SELECT TOXIC_LENGTH FROM IND_SYSPARM";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * �̵����ܹ�ҩƷ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryToxicCheck(TParm parm) {
		String containerId = parm.getValue("CONTAINER_ID");
		String cabinetId = parm.getValue("CABINET_ID");
		String sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,A.UNIT_CHN_DESC,"
				+ "COUNT(A.CONTAINER_ID) AS CONTAINER_QTY,SUM(A.TOT_QTY) AS TOT_QTY,"
				+ "A.SPECIFICATION,0 AS CHECKNUM FROM( SELECT B.ORDER_CODE,D.ORDER_DESC,B.CONTAINER_ID,"
				+ "C.UNIT_CHN_DESC,COUNT(B.CONTAINER_ID) AS TOT_QTY,D.SPECIFICATION  AS SPECIFICATION"
				+ " FROM IND_CONTAINERD B ,SYS_UNIT C, PHA_BASE D WHERE  CABINET_ID='"
				+ cabinetId
				+ "' "
				+ "AND CONTAINER_ID IN("
				+ containerId
				+ ") AND B.ORDER_CODE=D.ORDER_CODE  "
				+ "AND B.UNIT_CODE=C.UNIT_CODE GROUP BY B.ORDER_CODE,D.ORDER_DESC,CONTAINER_ID,C.UNIT_CHN_DESC ,D.SPECIFICATION) A GROUP BY  A.ORDER_CODE,A.ORDER_DESC,A.UNIT_CHN_DESC ,A.SPECIFICATION";

		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * ɾ���龫ҩ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm deleteByToxic(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = update("deleteByToxic", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * ��ѯ��������ҩƷ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryToxicD(TParm tparm) {
		TParm result = new TParm();
		result = query("queryToxicD", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ����ORDER_CODE��ѯ���ܹ�ʵ�ʿ����
	 * 
	 * @param tparm
	 * @return
	 */
	public TParm queryCabinetQty(TParm tparm) {
		String cabinetId = tparm.getValue("CABINET_ID");
		String orderCode = tparm.getValue("ORDER_CODE");
		String sql = "SELECT COUNT(*) AS NUM FROM IND_CONTAINERD WHERE ORDER_CODE='"
				+ orderCode + "' AND CABINET_ID='" + cabinetId + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * �������
	 * 
	 * @param tparm
	 * @return
	 */
	public TParm resetContainer(TParm tparm) {
		String containerID = tparm.getValue("CONTAINER_ID");
		String sql = "UPDATE IND_CONTAINERD SET CONTAINER_ID=NULL,CABINET_ID=NULL WHERE CONTAINER_ID='"
				+ containerID + "'";		
		return new TParm(TJDODBTool.getInstance().update(sql));		
	}

}
