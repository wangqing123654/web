package jdo.opd;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 *
 * <p>
 * Title: ����ҽ��վ����tool
 *
 * <p>
 * Description: ����ҽ��վ����tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company: javahis
 *
 * @author ehui 20081224
 * @version 1.0
 */
public class OPDSysParmTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static OPDSysParmTool instanceObject;
	public static final String SQL="SELECT * FROM OPD_SYSPARM";
	public TParm parm;

	/**
	 * �õ�ʵ��
	 *
	 * @return OPDSysParmTool
	 */
	public static OPDSysParmTool getInstance() {
		if (instanceObject == null)
			instanceObject = new OPDSysParmTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public OPDSysParmTool() {
		setModuleName("opd\\OPDSysParmModule.x");
		parm=new TParm(TJDODBTool.getInstance().select(SQL));
		onInit();
	}

	/**
	 * ����������еĶ�ͯ�������Ķ��壬����6���������ͯ��Ȼ�󷵻� ����+6>=sysdate��������������Ƕ�ͯ����֮�����Ǵ���
	 *
	 * @param age
	 *            ������������
	 * @return boolean �棺��ͯ���٣�����
	 */
	public boolean isChild(Timestamp age) {
            if(age == null)
            {
                err("ERR:age����Ϊ��!");
                return false;
            }
            TParm data = selectAll();
            if (data.getErrCode() < 0) {
                err("ERR:" + data.getErrCode() + data.getErrText()
                    + data.getErrName());
                return false;
            }
            String stryear = data.getValue("AGE", 0);
            int year = (stryear == null || stryear.trim().length() < 1) ? 0
                : Integer.parseInt(stryear);
            if(year < 0)
            {
                err("ERR:��˶�OPD_SYSPARM����AGE�ֶΣ�������ͯ�������޴���!");
                return false;
            }
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(age);
            gc.add(GregorianCalendar.YEAR, year);
            return gc.getTime().getTime() > SystemTool.getInstance()
                .getDate().getTime();
        }
	/**
	 * ȫ�ֶμ���OpdSysParm
	 *
	 * @return TParm
	 */
	public TParm selectAll() {
		TParm result = query("isChild");
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		return result;
	}

	/**
	 * ����һ������
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updataData(TParm parm) {
		TParm result = new TParm();
		result = update("updatedata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		return result;

	}
	/**
	 * ������ҽ�����÷�
	 * @return String
	 */
	public String getGRouteCode(){
		String routeCode="";
		routeCode=parm.getValue("G_ROUTE_CODE",0);
//		//System.out.println("SYSpARM==============================routeCode"+routeCode);
		return routeCode;
	}
	/**
	 * ������ҽ����Ƶ��
	 * @return String
	 */
	public String getGfreqCode(){
		String freqCode;
		freqCode=parm.getValue("G_FREQ_CODE",0);
//		//System.out.println("SYSpARM==============================freqCode"+freqCode);
		return freqCode;
	}
	/**
	 * ������ҽ���ü�ҩ��ʽ
	 * @return String
	 */
	public String getGdctAgent(){
		String dctAgent;
		dctAgent=parm.getValue("G_DCTAGENT_CODE",0);
		return dctAgent;
	}
	/**
	 * ����ȫ������
	 * @return TParm
	 */
	public TParm getSysParm(){
		if(parm==null||parm.getErrCode()!=0)
			parm=new TParm(TJDODBTool.getInstance().select(SQL));
		return parm;
	}
        /**
         * ��ȡ�����޶���������
         * @return int
         */
        public int getEDays(){
            int days;
            days=parm.getInt("E_DAYS",0);
            return days;
        }
}
