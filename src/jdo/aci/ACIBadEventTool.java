package jdo.aci;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeSet;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.SystemTool;

/**
 * <p>Title: �����¼�������</p>
 *
 * <p>Description: �����¼�������</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BLueCore</p>
 *
 * @author wanglong 2012.11.22
 * @version 1.0
 */
public class ACIBadEventTool extends TJDOTool {
	
    public static ACIBadEventTool instanceObject;// ʵ��
    
    /**
     * �õ�ʵ��
     * @return ACIRecordTool
     */
    public static ACIBadEventTool getInstance() {
        if (instanceObject == null) instanceObject = new ACIBadEventTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ACIBadEventTool() {
        setModuleName("aci\\ACIBadEventModule.x");
        onInit();
    }
    
    /**
     * ��ѯ�����¼�
     * @param parm TParm
     * @return TParm
     */
    public TParm selectBadEventData(TParm parm) {
        TParm result = new TParm();
        result = query("selectBadEventData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
		for (int i = 0; i < result.getCount("FLG"); i++) {
			result.addData("EXAMINE_FLG", result.getValue("FLG", i).equals("Y") ? true : false);
		}
        return result;
    }
    
    /**
     * ���������¼�
     * @param parm TParm
     * @return TParm
     */
    public TParm insertBadEventData(TParm parm) {
        String aciNo = SystemTool.getInstance().getNo("ALL", "ACI", "RECORD_NO", "ACI_NO"); // ����ȡ��ԭ��
        parm.setData("ACI_NO", aciNo);
        TParm result = update("insertBadEventData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���²����¼�
     * @param parm TParm
     * @return TParm
     */
    public TParm updateBadEventData(TParm parm) {
        TParm result = update("updateBadEventData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ�������¼�
     * @param aciNo String
     * @return TParm
     */
    public TParm deleteBadEventData(String aciNo) {
        TParm parm = new TParm();
        if (aciNo.equals("")) {
            parm.setErr(-1, "������ȫ");
            return parm;
        }
        parm.setData("ACI_NO", aciNo);
        TParm result = update("deleteBadEventData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ��˲����¼�
     * @param parm TParm
     * @return TParm
     */
    public TParm examineBadEventData(TParm parm) {
        if (parm.getValue("ACI_NO").equals("")) {
            parm.setErr(-1, "������ȫ");
            return parm;
        }
        TParm result = update("examineBadEventData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����¼�ȡ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm unExamineBadEventData(String aciNo) {
        TParm parm = new TParm();
        if (aciNo.equals("")) {
            parm.setErr(-1, "������ȫ");
            return parm;
        }
        parm.setData("ACI_NO", aciNo);
        TParm result = update("unExamineBadEventData", parm);
        if (result.getErrCode() < 0) {
            err("ERR1:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        parm.setData("ASSESS_USER", "");
        parm.setData("SAC_CLASS", "");
        parm.setData("ASSESS_DATE",  "");
        TParm result1 = update("rateBadEventData", parm);
        if (result1.getErrCode() < 0) {
            err("ERR2:" + result1.getErrCode() + result1.getErrText() + result1.getErrName());
            return result1;
        }
        return result1;
    }
    
    /**
     * �����¼�����
     * @param parm TParm
     * @return TParm
     */
    public TParm rateBadEventData(TParm parm) {//add by wanglong 20131101
        if (parm.getValue("ACI_NO").equals("")) {
            parm.setErr(-1, "������ȫ");
            return parm;
        }
        TParm result = update("rateBadEventData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ��ѯ�����¼�ͳ����Ϣ����������
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticByCount(TParm parm) {
		TParm back = new TParm();
		String headerName = "";// ���Header
		String sql = "SELECT TO_CHAR(EVENT_DATE,'#') AS EVENT_DATE, COUNT(*) AS COUNT "
				+ " FROM ACI_BADEVENT "
				+ " WHERE EVENT_DATE BETWEEN # AND # "
				+ " GROUP BY TO_CHAR(EVENT_DATE, '#') "
				+ " ORDER BY EVENT_DATE";
		if (parm.getValue("DATE_TYPE").equals("YEAR")) {
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 12) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE") + "/01");
			sql = sql.replaceFirst("@", parm.getValue("END_DATE") + "/01");
			headerName += "���     ,60";
		} else if (parm.getValue("DATE_TYPE").equals("MONTH")) {
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 1) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
			headerName += "�·�     ,60";
		} else if (parm.getValue("DATE_TYPE").equals("DAY")) {
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') ");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') + 1 - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
			headerName += "����     ,60";
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		back.addData("FIRST_COLUMN", "����");
		String parmMap = "FIRST_COLUMN";// ���ParmMap
		int count = 0;// ��¼������
		for (int i = 0; i < result.getCount(); i++) {
			String title = "DATE_" + (i + 1);
			parmMap += ";" + title;
			headerName += ";" + result.getValue("EVENT_DATE", i);
			back.addData(title, result.getValue("COUNT", i));
			count += result.getInt("COUNT", i);
		}
		//===========���¿�ʼ�����ܼ���=============//delete by wanglong 20130418
//		String[] headerArr = parmMap.split(";");
//		if (headerArr.length == 2) {
//			back.addData("FIRST_COLUMN", "����");
//			back.addData(headerArr[1], count);// �����ܼ���
//		} else if (headerArr.length > 2) {
//			back.addData("FIRST_COLUMN", "");
//			back.addData(headerArr[headerArr.length - 1], count);
//			back.addData(headerArr[headerArr.length - 2], "����");
//			for (int i = headerArr.length - 3; i > 0; i--) {
//				back.addData(headerArr[i], "");
//			}
//		}
	    //===========���¿�ʼ�����ܼ���=============//add by wanglong 20130418
        headerName = headerName + ";����";
        parmMap += ";COUNT";
        back.addData("COUNT", count);
		headerName = headerAddLengthInfo(headerName, 100);//��������ֵ�еĿ��Ϊͳһֵ
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
    
    /**
     * ��ѯ�����¼�ͳ����Ϣ�����ϱ����ң�
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticByDept(TParm parm) {
		String sql = "SELECT REPORT_DEPT, TO_CHAR(EVENT_DATE, '#') AS EVENT_DATE, COUNT(*) AS COUNT "
				+ " FROM ACI_BADEVENT "
				+ " WHERE EVENT_DATE BETWEEN # AND # ? "
				+ " GROUP BY REPORT_DEPT, TO_CHAR(EVENT_DATE, '#') "
				+ " ORDER BY EVENT_DATE,REPORT_DEPT";// ������ʵ����Ҫ
		if (!parm.getValue("REPORT_DEPT").equals("")) {
			sql = sql.replaceFirst("\\?", " AND REPORT_DEPT='" + parm.getValue("REPORT_DEPT") + "' ");
		} else {
			sql = sql.replaceFirst("\\?", "");
		}
		if (parm.getValue("DATE_TYPE").equals("YEAR")) {
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 12) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE") + "/01");
			sql = sql.replaceFirst("@", parm.getValue("END_DATE") + "/01");
		} else if (parm.getValue("DATE_TYPE").equals("MONTH")) {
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 1) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
		} else if (parm.getValue("DATE_TYPE").equals("DAY")) {
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') ");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') + 1 - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		TParm back = new TParm();
		String headerName = "";// ���Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "DEPT_CODE");
		String parmMap = "DEPT_CODE";// ���ParmMap
		int index = 0;
		TreeSet<String> dateSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("EVENT_DATE"); i++) {
			dateSet.add(result.getValue("EVENT_DATE", i));
		}
		for (String str : dateSet) {
			headerName += ";" + str;// ȷ�����header
			dateList.add(str);
			String title = "DATE_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// ȷ�����parmMap
		}
		String[] header = new String[headerList.size()];
		String[] date = new String[dateList.size()];
		headerList.toArray(header);
		dateList.toArray(date);
		TreeSet<String> deptSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("REPORT_DEPT"); i++) {
			deptSet.add(result.getValue("REPORT_DEPT", i));
		}
		for (String str : deptSet) {
			back.addData(header[0], str);// ȷ�������е�����
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// ��ͳ�����ݣ�����������Ĭ��ֵ
			}
		}
		int count = 0;// ��¼������
		for (int i = 0; i < back.getCount("DEPT_CODE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("REPORT_DEPT", j).equals(back.getValue("DEPT_CODE", i))
							&& result.getValue("EVENT_DATE", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// ��ʼ�޸ĸ�Ĭ��ֵΪͳ��ֵ��������
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========���¿�ʼ�����ܼ���=============
        headerName += ";�ϼ�";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("DEPT_CODE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("DEPT_CODE", "�ϼ�");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("DEPT_CODE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "�ϱ�����          ,120,DEPT_CODE;");
		headerName = headerAddLengthInfo(headerName, 100);//��������ֵ�еĿ��Ϊͳһֵ
	    parmMap += ";�ϼ�";
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
    
    /**
     * ��ѯ�����¼�ͳ����Ϣ����SAC�ּ���
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticBySac(TParm parm) {
	    //modify by wanglong 20131030
		String sql = "SELECT NVL(SAC_CLASS, 'x') SAC_CLASS, TO_CHAR(EVENT_DATE, '#') AS EVENT_DATE, COUNT(*) AS COUNT "
				+ " FROM ACI_BADEVENT "
				+ " WHERE EVENT_DATE BETWEEN # AND # ? "
				+ " GROUP BY SAC_CLASS, TO_CHAR(EVENT_DATE, '#') "
				+ " ORDER BY EVENT_DATE, SAC_CLASS";// ������ʵ����Ҫ
		if (!parm.getValue("SAC_CLASS").equals("")) {
			sql = sql.replaceFirst("\\?", " AND SAC_CLASS='" + parm.getValue("SAC_CLASS") + "' ");
		} else {
			sql = sql.replaceFirst("\\?", "");
		}
		if (parm.getValue("DATE_TYPE").equals("YEAR")) {
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 12) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE") + "/01");
			sql = sql.replaceFirst("@", parm.getValue("END_DATE") + "/01");
		} else if (parm.getValue("DATE_TYPE").equals("MONTH")) {
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 1) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
		} else if (parm.getValue("DATE_TYPE").equals("DAY")) {
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') ");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') + 1 - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		TParm back = new TParm();
		String headerName = "";// ���Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "CLASS_CODE");
		String parmMap = "CLASS_CODE";// ���ParmMap
		int index = 0;
		TreeSet<String> dateSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("EVENT_DATE"); i++) {
			dateSet.add(result.getValue("EVENT_DATE", i));
		}
		for (String str : dateSet) {
			headerName += ";" + str;// ȷ�����header
			dateList.add(str);
			String title = "DATE_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// ȷ�����parmMap
		}
		String[] header = new String[headerList.size()];
		String[] date = new String[dateList.size()];
		headerList.toArray(header);
		dateList.toArray(date);
		TreeSet<String> sacSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("SAC_CLASS"); i++) {
			sacSet.add(result.getValue("SAC_CLASS", i));
		}
		for (String str : sacSet) {
			back.addData(header[0], str);// ȷ���ּ��е�����
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// ��ͳ�����ݣ�����������Ĭ��ֵ
			}
		}
		int count = 0;// ��¼������
		for (int i = 0; i < back.getCount("CLASS_CODE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("SAC_CLASS", j).equals(back.getValue("CLASS_CODE", i))
							&& result.getValue("EVENT_DATE", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// ��ʼ�޸ĸ�Ĭ��ֵΪͳ��ֵ��������
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========���¿�ʼ�����ܼ���=============
        headerName += ";�ϼ�";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("CLASS_CODE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("CLASS_CODE", "�ϼ�");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("CLASS_CODE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "SAC�ּ�     ,80,CLASS_CODE;");
		headerName = headerAddLengthInfo(headerName, 100);//��������ֵ�еĿ��Ϊͳһֵ
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
    
    /**
     * ��ѯ�����¼�ͳ����Ϣ�����¼����ࣩ
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticByType(TParm parm) {
	    int length = levelCodeLength(parm.getInt("LEVEL"));// �¼�����ȼ�
//		String sql = "SELECT EVENT_TYPE, TO_CHAR(EXAMINE_DATE,'#') AS EXAM_DATE, COUNT(*) AS COUNT "
//				+ " FROM ACI_BADEVENT "
//				+ " WHERE EXAMINE_DATE BETWEEN # AND # ? "
//				+ " GROUP BY EVENT_TYPE, TO_CHAR(EXAMINE_DATE,'#') "
//				+ " ORDER BY EXAM_DATE, EVENT_TYPE";// ������ʵ����Ҫ
        String sql = "SELECT SUBSTR( EVENT_TYPE, 0, @) AS EVENT_TYPE, TO_CHAR(EVENT_DATE, '#') AS EVENT_DATE, COUNT(*) AS COUNT "
                + " FROM ACI_BADEVENT "
                + " WHERE EVENT_DATE BETWEEN # AND # ? "
                + " GROUP BY SUBSTR( EVENT_TYPE, 0, @), TO_CHAR(EVENT_DATE, '#') "
                + " ORDER BY EVENT_DATE, EVENT_TYPE";
        if (length > 0) {
            sql = sql.replaceAll("@", length + "");
        } else {
            sql = sql.replaceFirst("@", 0 + "");
        }
		if (!parm.getValue("EVENT_TYPE").equals("")) {
			sql = sql.replaceFirst("\\?", " AND EVENT_TYPE like '" + parm.getValue("EVENT_TYPE") + "%' ");
		} else {
			sql = sql.replaceFirst("\\?", "");
		}
		if (parm.getValue("DATE_TYPE").equals("YEAR")) {
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 12) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE") + "/01");
			sql = sql.replaceFirst("@", parm.getValue("END_DATE") + "/01");
		} else if (parm.getValue("DATE_TYPE").equals("MONTH")) {
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 1) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
		} else if (parm.getValue("DATE_TYPE").equals("DAY")) {
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') ");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') + 1 - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		TParm back = new TParm();
		String headerName = "";// ���Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "TYPE_CODE");
		String parmMap = "TYPE_CODE";// ���ParmMap
		int index = 0;
		TreeSet<String> dateSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("EVENT_DATE"); i++) {
			dateSet.add(result.getValue("EVENT_DATE", i));
		}
		for (String str : dateSet) {
			headerName += ";" + str;// ȷ�����header
			dateList.add(str);
			String title = "DATE_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// ȷ�����parmMap
		}
		String[] header = new String[headerList.size()];
		String[] date = new String[dateList.size()];
		headerList.toArray(header);
		dateList.toArray(date);
		TreeSet<String> typeSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("EVENT_TYPE"); i++) {
			typeSet.add(result.getValue("EVENT_TYPE", i));
		}
		for (String str : typeSet) {
			back.addData(header[0], str);// ȷ�������е�����
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// ��ͳ�����ݣ�����������Ĭ��ֵ
			}
		}
		int count = 0;// ��¼������
		for (int i = 0; i < back.getCount("TYPE_CODE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("EVENT_TYPE", j).equals(back.getValue("TYPE_CODE", i))
							&& result.getValue("EVENT_DATE", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// ��ʼ�޸ĸ�Ĭ��ֵΪͳ��ֵ��������
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========���¿�ʼ�����ܼ���=============
        headerName += ";�ϼ�";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("TYPE_CODE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("TYPE_CODE", "�ϼ�");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("TYPE_CODE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "�¼�����                                         ,330,TYPE_CODE;");
		headerName = headerAddLengthInfo(headerName, 100);//��������ֵ�еĿ��Ϊͳһֵ
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
    
    /**
     * ��ѯ�����¼�ͳ����Ϣ�������ϱ�����/SAC�ּ���	��
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticByDeptAndSac(TParm parm) {
	    //modify by wanglong 20131030
        String sql = "WITH A AS (SELECT A.REPORT_DEPT, NVL(A.SAC_CLASS, 'x') AS SAC_CLASS "
                + "                FROM ACI_BADEVENT A          "
                + "               WHERE A.EVENT_DATE BETWEEN  # AND # ),"
                + "       B  AS (SELECT B.ID, B.CHN_DESC        "
                + "                FROM SYS_DICTIONARY B        "
                + "               WHERE B.GROUP_ID = 'SAC_CLASS'"
                + "               UNION                         "
                + "              SELECT 'x' ID, 'δ�ּ�' CHN_DESC FROM DUAL) "
                + "SELECT A.REPORT_DEPT, B.CHN_DESC CLASS_DESC, COUNT(*) AS COUNT "
                + "  FROM A, B                        "
                + " WHERE A.SAC_CLASS = B.ID          "
                + "GROUP BY A.REPORT_DEPT, B.CHN_DESC "
                + "ORDER BY A.REPORT_DEPT, B.CHN_DESC";// ������ʵ����Ҫ
		if (parm.getValue("DATE_TYPE").equals("YEAR")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 12) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE") + "/01");
			sql = sql.replaceFirst("@", parm.getValue("DATE") + "/01");
		} else if (parm.getValue("DATE_TYPE").equals("MONTH")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 1) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
		} else if (parm.getValue("DATE_TYPE").equals("DAY")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') ");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') + 1 - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		TParm back = new TParm();
		String headerName = "";// ���Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "DEPT_CODE");
		String parmMap = "DEPT_CODE";// ���ParmMap
		int index = 0;
		TreeSet<String> classSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("CLASS_DESC"); i++) {
			classSet.add(result.getValue("CLASS_DESC", i));
		}
		for (String str : classSet) {
			headerName += ";" + str;// ȷ�����header
			dateList.add(str);
			String title = "CLASS_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// ȷ�����parmMap
		}
		String[] header = new String[headerList.size()];
		String[] date = new String[dateList.size()];
		headerList.toArray(header);
		dateList.toArray(date);
		TreeSet<String> deptSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("REPORT_DEPT"); i++) {
			deptSet.add(result.getValue("REPORT_DEPT", i));
		}
		for (String str : deptSet) {
			back.addData(header[0], str);// ȷ�������е�����
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// ��ͳ�����ݣ�����������Ĭ��ֵ
			}
		}
		int count = 0;// ��¼������
		for (int i = 0; i < back.getCount("DEPT_CODE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("REPORT_DEPT", j).equals(back.getValue("DEPT_CODE", i))
							&& result.getValue("CLASS_DESC", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// ��ʼ�޸ĸ�Ĭ��ֵΪͳ��ֵ��������
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========���¿�ʼ�����ܼ���=============
        headerName += ";�ϼ�";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("DEPT_CODE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("DEPT_CODE", "�ϼ�");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("DEPT_CODE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "�ϱ����ҩ�SAC�ּ�              ,200,DEPT_CODE;");
		headerName = headerAddLengthInfo(headerName, 100);//��������ֵ�еĿ��Ϊͳһֵ
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
	
	/**
	 * ��ѯ�����¼�ͳ����Ϣ�������ϱ�����/�¼����ࡱ ��
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm selectStatisticByDeptAndType(TParm parm) {
        int length = levelCodeLength(parm.getInt("LEVEL"));//�¼�����ȼ�
//        String sql = "SELECT B.DEPT_CHN_DESC AS DEPT_DESC, A.EVENT_TYPE, COUNT(*) AS COUNT "
//                + "  FROM ACI_BADEVENT A, SYS_DEPT B "
//                + " WHERE A.EXAMINE_DATE BETWEEN # AND # "
//                + "   AND A.REPORT_DEPT = B.DEPT_CODE "
//                + "GROUP BY B.DEPT_CHN_DESC, A.EVENT_TYPE "
//                + "ORDER BY B.DEPT_CHN_DESC, A.EVENT_TYPE";
        String sql =
                "SELECT DEPT_DESC, EVENT_TYPE, COUNT(*) AS COUNT "
                        + "  FROM (SELECT B.DEPT_CHN_DESC AS DEPT_DESC, SUBSTR( A.EVENT_TYPE, 0, #) AS EVENT_TYPE, A.ACI_NO "
                        + "          FROM ACI_BADEVENT A, SYS_DEPT B "
                        + "         WHERE A.EVENT_DATE BETWEEN # AND # "
                        + "           AND A.REPORT_DEPT = B.DEPT_CODE) "
                        + " GROUP BY DEPT_DESC, EVENT_TYPE "
                        + " ORDER BY DEPT_DESC, EVENT_TYPE";
        if (length > 0) {
            sql = sql.replaceFirst("#", length + "");
        } else {
            sql = sql.replaceFirst("#", 0 + "");
        }      
		if (parm.getValue("DATE_TYPE").equals("YEAR")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 12) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE") + "/01");
			sql = sql.replaceFirst("@", parm.getValue("DATE") + "/01");
		} else if (parm.getValue("DATE_TYPE").equals("MONTH")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 1) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
		} else if (parm.getValue("DATE_TYPE").equals("DAY")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') ");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') + 1 - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		TParm back = new TParm();
		String headerName = "";// ���Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "TYPE_CODE");
		String parmMap = "TYPE_CODE";// ���ParmMap
		int index = 0;
		TreeSet<String> typeSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("DEPT_DESC"); i++) {
			typeSet.add(result.getValue("DEPT_DESC", i));
		}
		for (String str : typeSet) {
			headerName += ";" + str;// ȷ�����header
			dateList.add(str);
			String title = "DEPT_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// ȷ�����parmMap
		}
		String[] header = new String[headerList.size()];
		String[] date = new String[dateList.size()];
		headerList.toArray(header);
		dateList.toArray(date);
		TreeSet<String> deptSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("EVENT_TYPE"); i++) {
			deptSet.add(result.getValue("EVENT_TYPE", i));
		}
		for (String str : deptSet) {
			back.addData(header[0], str);// ȷ�������е�����
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// ��ͳ�����ݣ�����������Ĭ��ֵ
			}
		}
		int count = 0;// ��¼������
		for (int i = 0; i < back.getCount("TYPE_CODE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("EVENT_TYPE", j).equals(back.getValue("TYPE_CODE", i))
							&& result.getValue("DEPT_DESC", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// ��ʼ�޸ĸ�Ĭ��ֵΪͳ��ֵ��������
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========���¿�ʼ�����ܼ���=============
        headerName += ";�ϼ�";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("TYPE_CODE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("TYPE_CODE", "�ϼ�");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("TYPE_CODE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "�¼����੅�ϱ�����             ,250,TYPE_CODE;");
		headerName = headerAddLengthInfo(headerName, 100);//��������ֵ�еĿ��Ϊͳһֵ
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}

	/**
	 * ��ѯ�����¼�ͳ����Ϣ������SAC�ּ�/�¼����ࡱ��
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm selectStatisticBySacAndType(TParm parm) {
        int length = levelCodeLength(parm.getInt("LEVEL"));// �¼�����ȼ�
//		String sql = "SELECT B.CHN_DESC AS CLASS_DESC, A.EVENT_TYPE, COUNT (*) AS COUNT " +
//				" FROM ACI_BADEVENT A, SYS_DICTIONARY B " +
//				" WHERE A.EXAMINE_DATE BETWEEN # AND # " +
//				" AND A.SAC_CLASS = B.ID " +
//				" AND B.GROUP_ID = 'SAC_CLASS' " +
//				" GROUP BY A.SAC_CLASS, A.EVENT_TYPE, B.CHN_DESC " +
//				" ORDER BY SAC_CLASS";// ������ʵ����Ҫ
        //modify by wanglong 20131030
        String sql = "WITH A AS (SELECT SUBSTR( A.EVENT_TYPE, 0, #) AS EVENT_TYPE, NVL(A.SAC_CLASS, 'x') AS SAC_CLASS "
                + "                FROM ACI_BADEVENT A          "
                + "               WHERE A.EVENT_DATE BETWEEN # AND #),"
                + "        B AS (SELECT B.ID, B.CHN_DESC        "
                + "                FROM SYS_DICTIONARY B        "
                + "               WHERE B.GROUP_ID = 'SAC_CLASS'"
                + "               UNION                         "
                + "              SELECT 'x' ID, 'δ�ּ�' CHN_DESC FROM DUAL) "
                + "SELECT A.EVENT_TYPE, B.CHN_DESC CLASS_DESC, COUNT(*) AS COUNT "
                + "  FROM A, B                       "
                + " WHERE A.SAC_CLASS = B.ID         "
                + "GROUP BY A.EVENT_TYPE, B.CHN_DESC "
                + "ORDER BY A.EVENT_TYPE, B.CHN_DESC";
        if (length > 0) {
            sql = sql.replaceFirst("#", length + "");
        } else {
            sql = sql.replaceFirst("#", 0 + "");
        }
		if (parm.getValue("DATE_TYPE").equals("YEAR")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 12) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE") + "/01");
			sql = sql.replaceFirst("@", parm.getValue("DATE") + "/01");
		} else if (parm.getValue("DATE_TYPE").equals("MONTH")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 1) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
		} else if (parm.getValue("DATE_TYPE").equals("DAY")) {
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') ");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') + 1 - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
			sql = sql.replaceFirst("@", parm.getValue("DATE"));
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		TParm back = new TParm();
		String headerName = "";// ���Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "TYPE_CODE");
		String parmMap = "TYPE_CODE";// ���ParmMap
		int index = 0;
		TreeSet<String> classSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("CLASS_DESC"); i++) {
			classSet.add(result.getValue("CLASS_DESC", i));
		}
		for (String str : classSet) {
			headerName += ";" + str;// ȷ�����header
			dateList.add(str);
			String title = "CLASS_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// ȷ�����parmMap
		}
		String[] header = new String[headerList.size()];
		String[] date = new String[dateList.size()];
		headerList.toArray(header);
		dateList.toArray(date);
		TreeSet<String> typeSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("EVENT_TYPE"); i++) {
			typeSet.add(result.getValue("EVENT_TYPE", i));
		}
		for (String str : typeSet) {
			back.addData(header[0], str);// ȷ���¼������е�����
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// ��ͳ�����ݣ�����������Ĭ��ֵ
			}
		}
		int count = 0;// ��¼������
		for (int i = 0; i < back.getCount("TYPE_CODE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("EVENT_TYPE", j).equals(back.getValue("TYPE_CODE", i))
							&& result.getValue("CLASS_DESC", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// ��ʼ�޸ĸ�Ĭ��ֵΪͳ��ֵ��������
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========���¿�ʼ�����ܼ���=============
        headerName += ";�ϼ�";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("TYPE_CODE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("TYPE_CODE", "�ϼ�");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("TYPE_CODE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "�¼����੅SAC�ּ�              ,330,TYPE_CODE;");
		headerName = headerAddLengthInfo(headerName, 100);//��������ֵ�еĿ��Ϊͳһֵ
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
	
	/**
	 * ��Table��header���ӳ�����Ϣ
	 * @param tableHeader
	 * @param length
	 * @return
	 */
	public String headerAddLengthInfo(String tableHeader, int length) {
		String[] headerPart = tableHeader.split(";");
		String returnStr = headerPart[0];
		for (int i = 1; i < headerPart.length; i++) {
			returnStr += ";" + headerPart[i] + "," + length;
		}
		return returnStr;
	}

	/**
	 * �鿴�¼�����ȼ���Ӧ�ı��볤��
	 * @param level
	 * @return
	 */
    public int levelCodeLength(int level) {
        String sql = "SELECT CLASSIFY1,CLASSIFY2,CLASSIFY3,CLASSIFY4,CLASSIFY5 FROM SYS_RULE WHERE RULE_TYPE = 'ACI_BADEVENT'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0 || result.getCount() < 0) {
            return -1;
        }
        if (result.getCount() == 0) {
            return 0;
        }
        int length = 0;
        if (level == 1) {
            length = result.getInt("CLASSIFY1", 0);
        } else if (level == 2) {
            length = result.getInt("CLASSIFY1", 0) + result.getInt("CLASSIFY2", 0);
        } else if (level == 3) {
            length = result.getInt("CLASSIFY1", 0) + result.getInt("CLASSIFY2", 0)
                   + result.getInt("CLASSIFY3", 0);
        }
        return length;
    }
    
    /**
     * ����
     * 
     * @param parm
     * @return
     */
    public TParm onSave(TParm parm, TConnection conn) {//add by wanglong 20131101
        TParm result = new TParm();
        Map inMap = (HashMap) parm.getData("IN_MAP");
        String[] sql = (String[]) inMap.get("SQL");
        if (sql == null) {
            return result;
        }
        if (sql.length < 1) {
            return result;
        }
        for (String tempSql : sql) {
            result = new TParm(TJDODBTool.getInstance().update(tempSql, conn));
            if (result.getErrCode() != 0) {
                return result;
            }
        }
        return result;
    }
    
    /**
     * ��ö����û���Ϣ
     * @param packageCode
     * @return
     */
    public TParm getSMSUsers(String packageCode) { // add by wanglong 20131101
        String sql =
                "SELECT PACKAGE_CODE, USER_ID, TEL, DEPT_CODE, POS_CODE FROM ACI_SMSPACKAGED "
                        + " WHERE PACKAGE_CODE = '#'";
        sql = sql.replaceFirst("#", packageCode);
        return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    /**
     * ���Ͷ���
     * @param parm
     */
    public static void sendSms(TParm parm) { // add by wanglong 20131101
        String smsPath = jdo.util.XmlUtil.getSmsPath();
        String fileName = "ACI_" + jdo.util.XmlUtil.getNowTime("yyyyMMddHHmmssSSS") + "_" + parm.getValue("ACI_NO") + ".xml";
        String path = smsPath + fileName;
        createXml(parm, path, fileName);
    }

  
    /**
     * ���ɶ���xml
     * @param parm
     * @param path
     * @param fileName
     */
    public static void createXml(TParm parm, String path, String fileName) { // add by wanglong 20131101
        if(parm.getCount("TEL")<=0){
            return;
        }
        FileOutputStream ps = null;
        OutputStreamWriter ow = null;
        BufferedWriter buf = null;
        PrintWriter out = null;
        jdo.util.XmlUtil.createNewFile(path);
        try {
            ps = new FileOutputStream(path);
            ow = new OutputStreamWriter(ps, "utf8");
            buf = new BufferedWriter(ow);
            out = new PrintWriter(buf);
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<Root>");
            out.println("<Tels>");
            for (int i = 0; i < parm.getCount("TEL"); i++) {
                out.println("<Tel>" + parm.getValue("TEL", i) + "</Tel>");
            }
            out.println("</Tels>");
            out.println("<TITLE>�����¼�֪ͨ</TITLE>");
            out.println("<Content>" + parm.getValue("SMS_TEXT") + "</Content>");
            out.println("<MrNo></MrNo>");
            out.println("<Name></Name>");
            out.println("</Root>");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        finally {
            if (out != null) {
                out.close();
            }
            try {
                if (buf != null) {
                    buf.close();
                }
                if (ow != null) {
                    ow.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // �ϴ�FTP
        jdo.util.XmlUtil.invokeFtp(path, fileName);
    }

    /**
     * �����ײͺ�(ȡ��ԭ��)
     * @return
     */
    public String getNewPackCode() { // add by wanglong 20131101
        String packCode = SystemTool.getInstance().getNo("ALL", "ODO", "PACKAGE_NO", "PACKAGE_NO");
        return packCode;
    }
    
    /**
     * ���ز�����Ա����ϸ��Ϣ
     * @param id
     * @return
     */
    public TParm getOperatorInfo(String id) { // add by wanglong 20131101
        String sql = "SELECT * FROM SYS_OPERATOR  WHERE USER_ID = '#'";
        sql = sql.replaceFirst("#", id);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    

}
