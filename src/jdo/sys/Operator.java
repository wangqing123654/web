package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.util.TSystem;

/**
 * ������Ա��
 * <p>
 * Title:
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author lzk 2008.08.22
 * @version 1.0
 */
public class Operator {
	public static TParm getData() {
		TParm parm = (TParm) TSystem.getObject("OperatorObject");
		if (parm == null) {
			parm = new TParm();
			TSystem.setObject("OperatorObject", parm);
		}
		return parm;
	}

	/**
	 * ���ö���
	 *
	 * @param ID
	 *            String �û����
	 * @param region
	 *            String ��¼����
	 * @param IP
	 *            String �û�IP
	 * @param dept
	 *            String ��¼����
	 * @param station
	 *            String ��¼����
	 */
	public static void setData(String ID, String region, String IP,
			String dept, String station) {
		getData().setData("ID", ID);
		getData().setData("REGION", region);
		//=====pangben 2013-4-18 ���������ע��
		TParm parm=SYSRegionTool.getInstance().selectdata(region);
		getData().setData("SPC_FLG", null!=parm
				.getValue("SPC_FLG",0)&&parm.getValue("SPC_FLG",0).length()>0?parm
						.getValue("SPC_FLG",0):"N");
		//==========pangben 2013-11-7 �������湦�ܣ��������汾
		getData().setData("LOCK_FLG",null!=parm
				.getValue("LOCK_FLG",0)&&parm.getValue("LOCK_FLG",0).length()>0?parm
						.getValue("LOCK_FLG",0):"N");
		getData().setData("IP", IP);
		getData().setData("DEPT", dept);
		getData().setData("STATION", station);
		getData().setData("ROLE_ID",
				OperatorTool.getInstance().getOperatorRole(ID));
	}

	/**
	 * �õ��û�ID
	 *
	 * @return String
	 */
	public static String getID() {
		return getData().getValue("ID");
	}

	/**
	 * �õ��û�����
	 *
	 * @return String
	 */
	public static String getName() {
		return OperatorTool.getInstance().getOperatorName(getID());
	}
        /**
         * �õ��û�Ӣ����
         * @return String
         */
        public static String getEngName() {
                return OperatorTool.getInstance().getOperatorEngName(getID());
        }

	/**
	 * �õ��û�IP
	 *
	 * @return String
	 */
	public static String getIP() {
		return getData().getValue("IP");
	}

	/**
	 * �õ��û���¼����
	 *
	 * @return String
	 */
	public static String getRegion() {
		return getData().getValue("REGION");
	}
	/**
	 * �õ�������ע��
	 *
	 * @return String
	 */
	public static String getSpcFlg() {
		return getData().getValue("SPC_FLG");
	}
	/**
	 * �õ������ע��
	 * ====pangben 2013-11-7
	 * @return String
	 */
	public static String getLockFlg() {
		return getData().getValue("LOCK_FLG");
	}
	/**
	 * �õ�������
	 *
	 * @return String
	 */
	public static String getDept() {
		return getData().getValue("DEPT");
	}

	/**
	 * �õ���ɫ
	 *
	 * @return String
	 */
	public static String getRole() {
		return getData().getValue("ROLE_ID");
	}

	/**
	 * �õ��û�ְ��
	 *
	 * @return String
	 */
	public static String getPosition() {
		return "";
	}

	/**
	 * �õ�����
	 *
	 * @return String
	 */
	public static String getStation() {
		return getData().getValue("STATION");
	}

	/**
	 * �õ���¼����
	 * 
	 * @return String
	 */
	public static String getLanguage() {
		return (String) TSystem.getObject("Language");
	}
	/**
	 * �õ�ҽԺ����ȫ��
	 *
	 * @return String
	 */
	public static String getHospitalCHNFullName() {
		return SYSNewRegionTool.getInstance().getHospitalName(getRegion())
				.getValue("REGION_CHN_DESC", 0);
	}

	/**
	 * �õ�ҽԺ���ļ��
	 *
	 * @return String
	 */
	public static String getHospitalCHNShortName() {
		return SYSNewRegionTool.getInstance().getHospitalName(getRegion())
				.getValue("REGION_CHN_ABN", 0);
	}

	/**
	 * �õ�ҽԺӢ��ȫ��
	 *
	 * @return String
	 */
	public static String getHospitalENGFullName() {
		return SYSNewRegionTool.getInstance().getHospitalName(getRegion())
				.getValue("REGION_ENG_DESC", 0);
	}
        /**
         * �õ�ҽԺӢ�ļ��
         * @return String
         */
        public static String getHospitalENGShortName() {
		return SYSNewRegionTool.getInstance().getHospitalName(getRegion())
				.getValue("REGION_ENG_ABN", 0);
	}
        /**
         * �õ��ɱ����Ĵ���
         * @return String
         */
        public static String getCostCenter() {
                return OperatorTool.getInstance().getCostCenterCode(getID());
        }
        /**
         * ����ʹ����ID�õ�ʹ���ߵ����п���
         * @param userID String
         * @return TParm
         */
        public static TParm getOperatorDept(String userID) {
		return OperatorTool.getInstance().getOperatorDept(userID);
	}

}
