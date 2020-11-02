package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: �����龫��ƿ����Tool
 * </p>
 * 
 * <p>
 * Description: �����龫��ƿ����Tool
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
 * @author shendr 2013.07.18
 * @version 1.0
 */
public class SPCDrugRecycleTool extends TJDOTool {

	/**
	 * ʵ��
	 */
	private static SPCDrugRecycleTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PatAdmTool
	 */
	public static SPCDrugRecycleTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCDrugRecycleTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SPCDrugRecycleTool() {
		onInit();
	}

	/**
	 * ��ȡ�龫ҩƷ������Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getRecover(TParm parm) {
		String toxicId = parm.getValue("TOXIC_ID");
		String isRecover = parm.getValue("IS_RECOVER");
		String sql = "SELECT TOXIC_ID,ORDER_DESC,SPECIFICATION,RX_NO,MR_NO,"
				+ "PAT_NAME,ORDER_DATE,USER_NAME,RECLAIM_USER,RECLAIM_DATE "
				+ "FROM OPD_ORDER A,SYS_OPERATOR B "
				+ "WHERE A.DR_CODE=B.USER_ID " + "AND A.TOXIC_ID IS NOT NULL "
				+ "AND A.IS_RECLAIM='" + isRecover + "'";
		if (toxicId != null && !"".equals(toxicId)) {
			sql += "AND TOXIC_ID ='" + toxicId + "' ";
		}
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * ���»���״̬
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateRecover(TParm parm, TConnection conn) {
		String toxicId = parm.getValue("TOXIC_ID");
		String userId = parm.getValue("RECLAIM_USER");
		String sql = " UPDATE OPD_ORDER A SET A.IS_RECLAIM='Y',A.RECLAIM_DATE=SYSDATE,"
				+ "A.RECLAIM_USER='"
				+ userId
				+ "' WHERE TOXIC_ID='"
				+ toxicId
				+ "'";
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	/**
	 * �Ͽ��龫��������ϵ
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteToxic(TParm parm, TConnection conn) {
		String toxic_id = parm.getValue("TOXIC_ID");
		String sql = "UPDATE IND_CONTAINERD SET CABINET_ID=NULL,CONTAINER_ID=NULL WHERE TOXIC_ID='"
				+ toxic_id + "'";
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	/**
	 * ��ѯ���ܹ���Ϣ
	 * 
	 * @param toxic_id
	 * @return
	 */
	public TParm queryCabinet(String toxic_id) {
		String sql = "SELECT D.CABINET_ID,CABINET_DESC,ORG_CHN_DESC "
				+ " FROM OPD_ORDER A,IND_CABINET B,IND_ORG C,IND_CONTAINERD D "
				+ " WHERE B.ORG_CODE=C.ORG_CODE "
				+ " AND A.TOXIC_ID=D.TOXIC_ID "
				+ " AND D.CABINET_ID=B.CABINET_ID " + " AND D.TOXIC_ID='"
				+ toxic_id + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

}
