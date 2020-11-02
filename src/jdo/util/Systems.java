package jdo.util;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Vector;

import jdo.sys.SYSRoomTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TStrike;
import com.dongyang.util.RunClass;
import com.dongyang.util.TSystem;

/**
 *
 * <p>
 * Title: ϵͳ����
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author lzk 2009.6.2
 * @version JavaHis 1.0
 */
public class Systems extends TStrike {
	/**
	 * �õ�ϵͳʱ��
	 *
	 * @return Timestamp
	 */
	public Timestamp getDate() {
		if (isClientlink())
			return (Timestamp) callServerMethod();
		return SystemTool.getInstance().getDate();
	}

	/**
	 * �õ�������
	 *
	 * @return String
	 */
	public synchronized String getMrNo() {
		if (isClientlink())
			return (String) callServerMethod();
		return SystemTool.getInstance().getMrNo();
	}

	/**
	 * �õ�סԺ��
	 *
	 * @return String
	 */
	public synchronized String getIpdNo() {
		if (isClientlink())
			return (String) callServerMethod();
		return SystemTool.getInstance().getIpdNo();
	}

	/**
	 * �õ�ȡ��ԭ��
	 *
	 * @param regionCode
	 *            String
	 * @param systemCode
	 *            String
	 * @param operation
	 *            String
	 * @param section
	 *            String
	 * @return String
	 */
	public synchronized String getNo(String regionCode, String systemCode,
			String operation, String section) {
		if (isClientlink())
			return (String) callServerMethod(regionCode, systemCode, operation,
					section);
		return SystemTool.getInstance().getNo(regionCode, systemCode,
				operation, section);
	}

	/**
	 * ����V3���
	 *
	 * @param actionName
	 *            String Action����
	 * @param methodName
	 *            String ��������
	 * @param tparm
	 *            TParm ����"COMMIT��,RETURN��"
	 * @return TParm ����"COMMIT��,RETURN��"
	 */
	public TParm doAction(String actionName, String methodName, TParm tparm) {
		Object obj = TSystem.getObject("V3Manager");
		if (obj == null) {
			TParm result = new TParm();
			result.setErr(-1, "û������V3ϵͳ");
			System.out.println("û������V3ϵͳ");
			return result;
		}
		return (TParm) RunClass.runMethod(obj, "doAction", new Object[] {
				actionName, methodName, tparm });
	}

	/**
	 * ����V3���
	 *
	 * @param ServiceName
	 * @param Parameter
	 * @return
	 */
	public Vector doService(String ServiceName, Vector Parameter) {
		Object obj = TSystem.getObject("V3Manager");
		if (obj == null) {
			System.out.println("û������V2ϵͳ");
			return null;
		}
		return (Vector) RunClass.runMethod(obj, "doService", new Object[] {
				ServiceName, Parameter });
	}

	/**
	 * ���ݲ�����ȡ�ò�����Ϣ
	 *
	 * @param room_code
	 *            ������
	 * @return Map TParm{ROOM_CODE, ROOM_DESC, PY1, PY2, SEQ, DESCRIPT,
	 *         STATION_CODE, REGION_CODE, SEX_LIMIT_FLG, RED_SIGN, YELLOW_SIGN,
	 *         OPT_USER, OPT_DATE, OPT_TERM }
	 */
	public Map getSYSRoom(String room_code) {
		if (isClientlink())
			return (Map) callServerMethod(room_code);
		return SYSRoomTool.getInstance().selectdata(room_code).getData();
	}
	/**
	 * ���ݸ����ֵȫ�ļ�����������pdf�ļ�������
	 * @param queryWord String
	 * @return TParm
	 */
	public static TParm queryByWord(String queryWord){
		TParm result=new TParm();
		TParm parm=new TParm();
		parm.setData("QUERY_WORD",queryWord);

		return result;
	}

}
