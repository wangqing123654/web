/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * <p>
 * Title: ҩƷ��λ
 * </p>
 *
 * <p>
 * Description:ҩƷ��λ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 20080901
 * @version 1.0
 */
public class SYSUnitTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static SYSUnitTool instanceObject;

	/**
	 * �õ�ʵ��
	 *
	 * @return SYSPhaRouteTool
	 */
	public static SYSUnitTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSUnitTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SYSUnitTool() {
		setModuleName("sys\\SYSUnitModule.x");
		onInit();
	}
        /**
         *�õ���λ��map
         * @return Map
         */
        public Map getUnitMap() {
            Map map = new HashMap();
            TParm parm=getUnitCodeAndUnitDesc();
            if(parm.getErrCode()<0)
                return map;
            int count=parm.getCount();
            for(int i=0;i<count;i++){
                map.put(parm.getValue("UNIT_CODE",i),parm.getValue("UNIT_CHN_DESC",i));
            }
            return map;
        }
        /**
        * ��ʼ�����棬��ѯ���е�����
        *
        * @return TParm
        */
       public TParm getUnitCodeAndUnitDesc() {
               TParm parm = new TParm();
               TParm result = query("getUnitAndDesc", parm);
               if (result.getErrCode() < 0)
                       err("ERR:" + result.getErrCode() + result.getErrText()
                                       + result.getErrName());
               return result;
       }

        /**
         * ��ʼ�����棬��ѯ���е�����
	 *
	 * @return TParm
	 */
	public TParm selectall() {
		TParm parm = new TParm();
		// parm.setData("CODE",CODE);
		TParm result = query("selectall", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���ݵ�λ�����ѯ����
	 *
	 * @param UNIT_CODE
	 *            String ��λ����
	 * @return TParm
	 */
	public TParm selectdata(String UNIT_CODE) {
		TParm parm = new TParm();
		parm.setData("UNIT_CODE", UNIT_CODE);
		TParm result = query("selectdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ָ����λ����õ�����
	 *
	 * @param UNIT_CODE
	 *            String
	 * @return TParm
	 */
	public TParm insertdata(TParm parm) {
		String UNIT_CODE = parm.getValue("UNIT_CODE");
		// System.out.println("UNIT_CODE"+UNIT_CODE);
		TParm result = new TParm();
		if (!existsUNITCODE(UNIT_CODE)) {
			result = update("insertdata", parm);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		} else {
			result.setErr(-1, "��λ���� " + UNIT_CODE + " �Ѿ�����!");
			return result;
		}

		return result;
	}

	/**
	 * �ж��Ƿ��������
	 *
	 * @param UNIT_CODE
	 *            String
	 * @return boolean TRUE ���� FALSE ������
	 */
	public boolean existsUNITCODE(String UNIT_CODE) {
		TParm parm = new TParm();
		parm.setData("UNIT_CODE", UNIT_CODE);
		// System.out.println("existsUNITCODE"+UNIT_CODE);
		return getResultInt(query("existsUNITCODE", parm), "COUNT") > 0;
	}

	/**
	 * ����ָ��UNIT_CODE����
	 *
	 * @param UNIT_CODE
	 *            String
	 * @return TParm
	 */
	public TParm updatedata(TParm parm) {
		TParm result = new TParm();
		String UNIT_CODE = parm.getValue("UNIT_CODE");
		// System.out.println("true or false"+existsUNITCODE(UNIT_CODE));
		if (existsUNITCODE(UNIT_CODE)) {
			result = update("updatedata", parm);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		} else {
			result.setErr(-1, "��λ���� " + UNIT_CODE + " �ոձ�ɾ����");
			return result;
		}

		return result;
	}

	/**
	 * ɾ��ָ����λ��������
	 *
	 * @param UNIT_CODE
	 *            String
	 * @return boolean
	 */
	public TParm deletedata(String UNIT_CODE) {
		TParm parm = new TParm();
		TParm result = new TParm();
		parm.setData("UNIT_CODE", UNIT_CODE);
		if (!allowupdate(UNIT_CODE)) {
			result = update("deletedata", parm);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		} else {
			result.setErr(-1, "��λ���� " + UNIT_CODE + " ���ڱ�ʹ�ã�������ɾ����");
			return result;
		}

	}

	/**
	 * ����UNIT_CODE�ж�SYS_FEE����û�и�UNIT_CODE������������ɾ��
	 *
	 * @param UNIT_CODE
	 * @return
	 */
	public boolean allowupdate(String UNIT_CODE) {
		TParm parm = new TParm();
		parm.setData("UNIT_CODE", UNIT_CODE);
		return getResultInt(query("allowupdate", parm), "COUNT") > 0;
	}
}
