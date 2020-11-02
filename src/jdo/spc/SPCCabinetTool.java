package jdo.spc;

import java.sql.Timestamp;
import java.util.Date;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

public class SPCCabinetTool extends TJDOTool {

	/**
	 * ʵ��
	 */
	private static SPCCabinetTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PatAdmTool
	 */
	public static SPCCabinetTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCCabinetTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SPCCabinetTool() {
		setModuleName("spc\\SPCCabinetModule.x");
		onInit();
	}

	/**
	 * ������ܹ�
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
	 * �������ܹ�
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
	 * �������ܹ�
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
	 * �������ܹ�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryInfo(TParm parm) {
		TParm result = new TParm();
		result = query("queryInfo",parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * �������ܹ�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryLog(TParm parm) {
		TParm result = new TParm(); 
		Object TASK_TYPE =  parm.getData("TASK_TYPE"); 
		Object EVENT_TYPE =  parm.getData("EVENT_TYPE"); 
		Object START_DATE =  parm.getData("START_DATE"); 
		Object END_DATE =  parm.getData("END_DATE"); 
		String sql ="SELECT A.CABINET_ID," +
				"B.CABINET_DESC, A.LOG_TIME, (CASE WHEN A.TASK_TYPE=1 THEN '��������-������ҩ���' WHEN A.TASK_TYPE=2 " +
				"THEN '��ʱҽ��/������-������ҩ����' WHEN A.TASK_TYPE=3 THEN '��������-������ҩ����' WHEN A.TASK_TYPE=4 THEN " +
				"'��������-�龫���' WHEN A.TASK_TYPE=5 THEN '��������-�龫����' WHEN A.TASK_TYPE=6 THEN '��ʱҽ��/������-�����龫����' " +
				"WHEN A.TASK_TYPE=7 THEN '��������-�����龫����' WHEN A.TASK_TYPE=8 THEN '��ʱҽ��/������-סԺҩ���龫����' ELSE '' END) " +
				"AS TASK_TYPE,A.TASK_NO,(CASE WHEN A.EVENT_TYPE=1 THEN '����' WHEN A.EVENT_TYPE=2 THEN '����' " +
				"ELSE '' END) AS EVENT_TYPE, A.GUARD_ID,A.OPT_USER,A.OPT_DATE,A.OPT_TERM FROM IND_CABINET_LOG A,IND_CABINET B  WHERE A.CABINET_ID=B.CABINET_ID ";
			if(TASK_TYPE!=null) {
				sql = sql+" AND A.TASK_TYPE='"+TASK_TYPE+"'";
			}	
			if(EVENT_TYPE!=null) {
				sql = sql+" AND A.EVENT_TYPE='"+TASK_TYPE+"'";
			}	 
			if((START_DATE!=null)&&(END_DATE!=null)) {
				sql = sql + " AND A.LOG_TIME BETWEEN TO_DATE('"+START_DATE.toString()+"','YYYY-MM-DD HH24:MI:SS')"+
				"  AND TO_DATE('"+END_DATE+"','YYYY-MM-DD HH24:MI:SS') ";			
			}   
			sql = sql+	" ORDER BY A.LOG_TIME DESC";		  
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}	

		return result;
	}

	public TParm onSaveCabinet(TParm parm, TConnection conn) {
		TParm result = new TParm();
		if (parm == null)
			return null;
		Object update = parm.getData("UPDATE_SQL");
		// System.out.println("update:"+update);
		/*
		 * if (update == null) { return null; }
		 */
		if (update != null) {
			String[] updateSql;
			if (update instanceof String[]) {
				updateSql = (String[]) update;
			} else {
				return null;
			}
			// ���涩����ϸ��
			for (int i = 0; i < updateSql.length; i++) {
				result = new TParm(TJDODBTool.getInstance().update(
						updateSql[i], conn));
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		int i = parm.getInt("NUM");
		for (int j = 0; j < i; j++) {
			String guardId = (String) parm.getData("GUARD_ID", j);
			String guardDesc = (String) parm.getData("GUARD_DESC", j);
			String type = (String) parm.getData("IS_TOXIC_GUARD", j);
			Timestamp date = StringTool.getTimestamp(new Date());
			String user = (String) parm.getData("OPT_USER", j);
			String term = (String) parm.getData("OPT_TERM", j);
			String sql = "UPDATE IND_CABINET_GUARD SET GUARD_DESC='"
					+ guardDesc + "',IS_TOXIC_GUARD='" + type + "',"
					+ "OPT_DATE=SYSDATE,OPT_USER='" + user + "',OPT_TERM='"
					+ term + "' WHERE GUARD_ID='" + guardId + "'";
			result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		}
		return result;
	}

	/**
	public TParm getRfid(TParm parm) {
		TParm result = new TParm();
		// 2.д����
		String data = "";
		try {
			ModuleDriver.write("COM8", AntTypeE.ONE_ANT, "201211020001");
			data = ModuleDriver.read("COM8", AntTypeE.ONE_ANT, 6, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.getMessage();
		}
		result.setData("rfid", data);
		return result;
	}*/
	
	public TParm queryCode(TParm parm) {
		TParm result = new TParm();
		result = query("queryCode",parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}
	
	public TParm queryChild(TParm parm) {
		TParm result = new TParm();
		result = query("queryChild",parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}
	
	/**
	 * ɾ���Ž�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm deleteChild(TParm parm) {
		TParm result = new TParm();
		result = update("deleteChild", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}
	
	
	/**
	 * ��ѯ���ܹ�
	 * @param parm
	 * @return
	 */
	public TParm onQuery(TParm parm){
			
			String ip = parm.getValue("IP");
			String sql = " SELECT A.CABINET_ID,A.CABINET_DESC,A.CABINET_IP,A.GUARD_IP,B.ORG_CODE, B.ORG_CHN_DESC "+
						 " FROM IND_CABINET A,IND_ORG B "+
						 " WHERE  A.CABINET_IP='"+ip+"' AND A.ORG_CODE=B.ORG_CODE";
						
			return new TParm(TJDODBTool.getInstance().select(sql));
		}


	/**
	 * ���ݲ��ŵõ����ܹ�ID
	 * @param parm
	 * @return
	 */
	public TParm onQueryByOrgCode(TParm parm){
		String sql = " SELECT A.CABINET_ID,A.CABINET_DESC,A.CABINET_IP,A.GUARD_IP,A.ORG_CODE "+
					 " FROM IND_CABINET A "+
					 " WHERE A.ORG_CODE='"+parm.getValue("ORG_CODE")+"' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
}
