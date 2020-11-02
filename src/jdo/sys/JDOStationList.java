package jdo.sys;

import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;

/**
 * 
 * <p>
 * Title: ����վjdoList
 * </p>
 * 
 * <p>
 * Description:����վjdoList
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 20081111
 * @version 1.0
 */
public class JDOStationList extends TModifiedList {
	public JDOStationList(){
		StringBuffer sb=new StringBuffer();
		sb.append("userId:USER_ID;");
		sb.append("stationId:STATION_ID;");
		sb.append("areaType:AREA_TYPE;");
		sb.append("mainFlg:MAIN_FLG;");
		sb.append("optUser:OPT_USER;");
		sb.append("optTerm:OPT_TERM");
		setMapString(sb.toString());
	}
	/**
	 * ��������վ
	 * @return JDOStation
	 */
	public JDOStation newJDOStation() {
		JDOStation jdoStation = new JDOStation();
		this.newData(jdoStation);
		return jdoStation;
	}

	/**
	 * �õ�����
	 * @param index int
	 * @return JDOStation
	 */
	public JDOStation getJDOStation(int index) {
		return (JDOStation) get(index);
	}

	/**
	 * ��ʼ��TPARM
	 * @param parm
	 * @return �棺�ɹ����٣�ʧ��
	 */
	public boolean initParm(TParm parm) {
		if (parm == null)
			return false;
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			JDOStation jdoStation = new JDOStation();
			jdoStation.setMapString(getMapString());
			if (!jdoStation.initParm(parm, i))
				return false;
			add(jdoStation);
		}
		return true;
	}
}
