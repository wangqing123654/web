package jdo.aci;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ҩƷ�����¼������� </p>
 *
 * <p>Description: ҩƷ�����¼������� </p>
 *
 * <p>Copyright: Copyright (c) 2013 </p>
 *
 * <p>Company: BLueCore </p>
 *
 * @author wanglong 2013.09.30
 * @version 1.0
 */
public class ACIADRTool extends TJDOTool {
	
    public static ACIADRTool instanceObject;// ʵ��
    
    /**
     * �õ�ʵ��
     * @return ACIRecordTool
     */
    public static ACIADRTool getInstance() {
        if (instanceObject == null) instanceObject = new ACIADRTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ACIADRTool() {
        setModuleName("aci\\ACIADRModule.x");
        onInit();
    }
    
    /**
     * ����
     * 
     * @param parm
     * @return
     */
    public TParm onSave(TParm parm, TConnection conn) {
        TParm result = new TParm();
        Map inMap = (HashMap) parm.getData("IN_MAP");
        String[] sql = (String[]) inMap.get("SQL");
        if (sql == null||sql.length < 1) {
            result.setErr(-1, "����Ϊ��");
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
     * ��ѯ�����¼�
     * @param parm TParm
     * @return TParm
     */
    public TParm queryADRData(TParm parm) {
        TParm result = new TParm();
        result = query("queryADRData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        for (int i = 0; i < result.getCount(); i++) {
            if (!result.getValue("ADR_DESC", i).equals("")) {
                result.setData("ADR_DESC", i, result.getValue("ADR_DESC", i).replaceAll(" ", ";"));
            }
        }
        return result;
    }

    /**
     * ɾ�������¼���������
     * 
     * @param parm
     * @return
     */
    public TParm onDeleteADRMData(TParm parm, TConnection conn) {
        TParm result = update("deleteADRMData", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ�������¼�ϸ������
     * @param parm
     * @return
     */
    public TParm onDeleteADRDData(TParm parm, TConnection conn) {
        TParm result = update("deleteADRDData", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ���ϱ�/ȡ�����ϱ�
     * @param parm TParm
     * @return TParm
     */
    public TParm updateADRReportState(TParm parm) {
        TParm result = update("updateADRReportState", parm);
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
				+ " FROM ACI_ADRM "
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
		headerName = headerAddLengthInfo(headerName);//��������ֵ�еĿ��
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
    
	/**
     * ��ѯ�����¼�ͳ����Ϣ�����Ա�
     * @param parm TParm
     * @return TParm
     */
    public TParm selectStatisticBySex(TParm parm) {
        String sql = "SELECT SEX_CODE,TO_CHAR(EVENT_DATE,'#') AS EVENT_DATE, COUNT(*) AS COUNT "
                + "     FROM ACI_ADRM                   " 
                + "    WHERE EVENT_DATE BETWEEN # AND # "
                + " GROUP BY SEX_CODE, TO_CHAR(EVENT_DATE, '#') "
                + " ORDER BY EVENT_DATE, SEX_CODE ";// ������ʵ����Ҫ
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
        headerList.add(0, "SEX_CODE");
        String parmMap = "SEX_CODE";// ���ParmMap
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
        TreeSet<String> sexSet = new TreeSet<String>();
        for (int i = 0; i < result.getCount("SEX_CODE"); i++) {
            sexSet.add(result.getValue("SEX_CODE", i));
        }
        for (String str : sexSet) {
            back.addData(header[0], str);// ȷ�������е�����
        }
        for (int i = 1; i < header.length; i++) {
            for (int j = 0; j < back.getCount(header[0]); j++) {
                back.addData(header[i], "");// ����Ĭ��ֵ���հס�ռλ
            }
        }
        int count = 0;// ��¼������
        for (int i = 0; i < back.getCount("SEX_CODE"); i++) {
            for (int j = 0; j < result.getCount(); j++) {
                for (int k = 0; k < date.length; k++) {
                    if (result.getValue("SEX_CODE", j).equals(back.getValue("SEX_CODE", i))
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
        for (int i = 0; i < back.getCount("SEX_CODE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("SEX_CODE", "�ϼ�");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("SEX_CODE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
        headerName = headerName.replaceFirst(";", "�Ա�    ,60,SEX_CODE;");
        headerName = headerAddLengthInfo(headerName);//��������ֵ�еĿ��
        parmMap += ";�ϼ�";
        back.setData("TABLE_HEADER", headerName);
        back.setData("TABLE_PARMMAP", parmMap);
        return back;
    }
    
    /**
     * ��ѯ�����¼�ͳ����Ϣ��������Σ�
     * @param parm TParm
     * @return TParm
     */
    public TParm selectStatisticByAge(TParm parm) {
        String sql = "SELECT TO_CHAR(ROUND(MONTHS_BETWEEN(SYSDATE,BIRTH_DATE)/120)) AS AGE_SEG, "
                + "          TO_CHAR(EVENT_DATE,'#') AS EVENT_DATE, COUNT(*) AS COUNT "
                + "     FROM ACI_ADRM                   " 
                + "    WHERE EVENT_DATE BETWEEN # AND # "
        		+ " GROUP BY TO_CHAR(ROUND(MONTHS_BETWEEN(SYSDATE,BIRTH_DATE)/120)), TO_CHAR(EVENT_DATE, '#') "
        		+ " ORDER BY EVENT_DATE, TO_CHAR(ROUND(MONTHS_BETWEEN(SYSDATE,BIRTH_DATE)/120))";// ������ʵ����Ҫ
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
        headerList.add(0, "AGE_SEG");
        String parmMap = "AGE_SEG";// ���ParmMap
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
        TreeSet<String> ageSet = new TreeSet<String>();
        for (int i = 0; i < result.getCount("AGE_SEG"); i++) {
            ageSet.add(result.getValue("AGE_SEG", i));
        }
        for (String str : ageSet) {
            back.addData(header[0], str);// ȷ�������е�����
        }
        for (int i = 1; i < header.length; i++) {
            for (int j = 0; j < back.getCount(header[0]); j++) {
                back.addData(header[i], "");// ����Ĭ��ֵ���հס�ռλ
            }
        }
        int count = 0;// ��¼������
        for (int i = 0; i < back.getCount("AGE_SEG"); i++) {
            for (int j = 0; j < result.getCount(); j++) {
                for (int k = 0; k < date.length; k++) {
                    if (result.getValue("AGE_SEG", j).equals(back.getValue("AGE_SEG", i))
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
        for (int i = 0; i < back.getCount("AGE_SEG"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("AGE_SEG", "�ϼ�");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("AGE_SEG"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
        headerName = headerName.replaceFirst(";", "������(��) ,80,AGE_SEG;");
        headerName = headerAddLengthInfo(headerName);//��������ֵ�еĿ��
        parmMap += ";�ϼ�";
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
		String sql = "SELECT REPORT_DEPT AS DEPT_CODE, TO_CHAR(EVENT_DATE, '#') AS EVENT_DATE, COUNT(*) AS COUNT "
				+ " FROM ACI_ADRM                      "
				+ " WHERE EVENT_DATE BETWEEN # AND # ? "
				+ " GROUP BY REPORT_DEPT, TO_CHAR(EVENT_DATE, '#') "
				+ " ORDER BY EVENT_DATE, REPORT_DEPT";// ������ʵ����Ҫ
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
		for (int i = 0; i < result.getCount("DEPT_CODE"); i++) {
			deptSet.add(result.getValue("DEPT_CODE", i));
		}
		for (String str : deptSet) {
			back.addData(header[0], str);// ȷ�������е�����
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// ����Ĭ��ֵ���հס�ռλ
			}
		}
		int count = 0;// ��¼������
		for (int i = 0; i < back.getCount("DEPT_CODE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("DEPT_CODE", j).equals(back.getValue("DEPT_CODE", i))
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
		headerName = headerAddLengthInfo(headerName);//��������ֵ�еĿ��
	    parmMap += ";�ϼ�";
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
    
    /**
     * ��ѯ�����¼�ͳ����Ϣ����ҩƷ���ࣩ
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticByPha(TParm parm) {
        String sql = "SELECT SUBSTR(B.ORDER_CODE,0,2) AS PHA_TYPE, TO_CHAR(A.EVENT_DATE,'#') AS EVENT_DATE, COUNT(*) AS COUNT "
                + "     FROM ACI_ADRM A, ACI_ADRD B "
                + "    WHERE A.EVENT_DATE BETWEEN # AND # ? "
                + "      AND A.ACI_NO = B.ACI_NO "
                + "      AND B.TYPE = '0'        "//����ҩƷ
                + " GROUP BY SUBSTR(B.ORDER_CODE,0,2), TO_CHAR(A.EVENT_DATE, '#') "
                + " ORDER BY 1,2";// ������ʵ����Ҫ
		if (!parm.getValue("PHA_RULE").equals("")) {
			sql = sql.replaceFirst("\\?", " AND SUBSTR(B.ORDER_CODE,0,2)='" + parm.getValue("PHA_RULE") + "' ");
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
		headerList.add(0, "PHA_TYPE");
		String parmMap = "PHA_TYPE";// ���ParmMap
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
		TreeSet<String> phaSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("PHA_TYPE"); i++) {
			phaSet.add(result.getValue("PHA_TYPE", i));
		}
		for (String str : phaSet) {
			back.addData(header[0], str);// ȷ���ּ��е�����
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// ����Ĭ��ֵ���հס�ռλ
			}
		}
		int count = 0;// ��¼������
		for (int i = 0; i < back.getCount("PHA_TYPE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("PHA_TYPE", j).equals(back.getValue("PHA_TYPE", i))
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
        for (int i = 0; i < back.getCount("PHA_TYPE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("PHA_TYPE", "�ϼ�");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("PHA_TYPE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "����ҩƷ����               ,180,PHA_TYPE;");
		headerName = headerAddLengthInfo(headerName);//��������ֵ�еĿ��
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}

	   /**
     * ��ѯ�����¼�ͳ����Ϣ�����������ͣ�
     * @param parm TParm
     * @return TParm
     */
    public TParm selectStatisticByType(TParm parm) {
        String sql = "SELECT REPORT_KIND, EVENT_DATE, COUNT(*) AS COUNT "
                + "     FROM (SELECT CASE WHEN DIED_DATE IS NOT NULL THEN '4' ELSE REPORT_TYPE END AS REPORT_KIND,"
                + "                  TO_CHAR( EVENT_DATE, '#') AS EVENT_DATE "
                + "             FROM ACI_ADRM                    "
                + "            WHERE EVENT_DATE BETWEEN # AND #) "
                + "    WHERE 1=1 ?                   "
                + " GROUP BY REPORT_KIND, EVENT_DATE "
                + " ORDER BY EVENT_DATE, REPORT_KIND";// ������ʵ����Ҫ
//        String sql = "SELECT REPORT_TYPE AS REPORT_KIND, TO_CHAR(EVENT_DATE, '#') AS EVENT_DATE, COUNT(*) AS COUNT "
//                + " FROM ACI_ADRM                      "
//                + " WHERE EVENT_DATE BETWEEN # AND # ? "
//                + " GROUP BY REPORT_TYPE, TO_CHAR(EVENT_DATE, '#') "
//                + " ORDER BY EVENT_DATE, REPORT_TYPE";
        if (!parm.getValue("REPORT_TYPE").equals("")) {
            sql = sql.replaceFirst("\\?", " AND REPORT_KIND='" + parm.getValue("REPORT_TYPE") + "' ");
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
        headerList.add(0, "REPORT_KIND");
        String parmMap = "REPORT_KIND";// ���ParmMap
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
        for (int i = 0; i < result.getCount("REPORT_KIND"); i++) {
            deptSet.add(result.getValue("REPORT_KIND", i));
        }
        for (String str : deptSet) {
            back.addData(header[0], str);// ȷ�������е�����
        }
        for (int i = 1; i < header.length; i++) {
            for (int j = 0; j < back.getCount(header[0]); j++) {
                back.addData(header[i], "");// ����Ĭ��ֵ���հס�ռλ
            }
        }
        int count = 0;// ��¼������
        for (int i = 0; i < back.getCount("REPORT_KIND"); i++) {
            for (int j = 0; j < result.getCount(); j++) {
                for (int k = 0; k < date.length; k++) {
                    if (result.getValue("REPORT_KIND", j).equals(back.getValue("REPORT_KIND", i))
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
        for (int i = 0; i < back.getCount("REPORT_KIND"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("REPORT_KIND", "�ϼ�");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("REPORT_KIND"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
        headerName = headerName.replaceFirst(";", "��������  ,70,REPORT_KIND;");
        headerName = headerAddLengthInfo(headerName);//��������ֵ�еĿ��
        parmMap += ";�ϼ�";
        back.setData("TABLE_HEADER", headerName);
        back.setData("TABLE_PARMMAP", parmMap);
        return back;
    }
    
    /**
     * ��ѯ�����¼�ͳ����Ϣ�����¼����ƣ�
     * @param parm TParm
     * @return TParm
     */
    public TParm selectStatisticByName(TParm parm) {
        String sql = "SELECT ADR_ID1 AS ADR_TYPE, TO_CHAR(EVENT_DATE,'#') AS EVENT_DATE, COUNT(*) AS COUNT "
                + "     FROM ACI_ADRM                     "
                + "    WHERE EVENT_DATE BETWEEN # AND # ? "
                + " GROUP BY ADR_ID1, TO_CHAR(EVENT_DATE, '#') "
                + " ORDER BY EVENT_DATE, ADR_ID1";// ������ʵ����Ҫ
        if (!parm.getValue("ADR_ID").equals("")) {
            sql = sql.replaceFirst("\\?", " AND ADR_ID='" + parm.getValue("ADR_ID") + "' ");
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
        headerList.add(0, "ADR_TYPE");
        String parmMap = "ADR_TYPE";// ���ParmMap
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
        TreeSet<String> nameSet = new TreeSet<String>();
        for (int i = 0; i < result.getCount("ADR_TYPE"); i++) {
            nameSet.add(result.getValue("ADR_TYPE", i));
        }
        for (String str : nameSet) {
            back.addData(header[0], str);// ȷ�������е�����
        }
        for (int i = 1; i < header.length; i++) {
            for (int j = 0; j < back.getCount(header[0]); j++) {
                back.addData(header[i], "");// ����Ĭ��ֵ���հס�ռλ
            }
        }
        int count = 0;// ��¼������
        for (int i = 0; i < back.getCount("ADR_TYPE"); i++) {
            for (int j = 0; j < result.getCount(); j++) {
                for (int k = 0; k < date.length; k++) {
                    if (result.getValue("ADR_TYPE", j).equals(back.getValue("ADR_TYPE", i))
                            && result.getValue("EVENT_DATE", j).equals(date[k])) {
                        back.setData(header[k + 1], i, result.getValue("COUNT", j));// ��ʼ�޸ĸ�Ĭ��ֵΪͳ��ֵ��������
                        count += result.getInt("COUNT", j);
                    } else {
                        continue;
                    }
                }
            }
        }
        // ===========���¿�ʼ�����ܼ���=============
        headerName += ";�ϼ�";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("ADR_TYPE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("ADR_TYPE", "�ϼ�");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("ADR_TYPE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
        headerName = headerName.replaceFirst(";", "�¼�����                    ,180,ADR_TYPE;");
        headerName = headerAddLengthInfo(headerName);// ��������ֵ�еĿ��
        back.setData("TABLE_HEADER", headerName);
        back.setData("TABLE_PARMMAP", parmMap);
        return back;
    }
    
    /**
     * ��ѯ�����¼�ͳ����Ϣ������ҩƷ����/�ϱ����ҡ�	��
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticByPhaAndDept(TParm parm) {
        String sql = "SELECT SUBSTR(B.ORDER_CODE,0,2) AS PHA_RULE, A.REPORT_DEPT AS DEPT_CODE, "
	            + "          C.DEPT_CHN_DESC AS DEPT_DESC, COUNT(*) AS COUNT "
                + "     FROM ACI_ADRM A, ACI_ADRD B, SYS_DEPT C "
                + "    WHERE A.EVENT_DATE BETWEEN # AND # "
                + "      AND A.ACI_NO = B.ACI_NO          "
                + "      AND B.TYPE = '0'                 "//����ҩƷ
                + "      AND A.REPORT_DEPT = C.DEPT_CODE "
                + " GROUP BY SUBSTR(B.ORDER_CODE,0,2), A.REPORT_DEPT, C.DEPT_CHN_DESC "
                + " ORDER BY SUBSTR(B.ORDER_CODE,0,2), A.REPORT_DEPT, C.DEPT_CHN_DESC";// ������ʵ����Ҫ
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
		headerList.add(0, "PHA_RULE");
		String parmMap = "PHA_RULE";// ���ParmMap
		int index = 0;
		TreeSet<String> deptSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("DEPT_DESC"); i++) {
			deptSet.add(result.getValue("DEPT_DESC", i));
		}
		for (String str : deptSet) {
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
		TreeSet<String> phaSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("PHA_RULE"); i++) {
			phaSet.add(result.getValue("PHA_RULE", i));
		}
		for (String str : phaSet) {
			back.addData(header[0], str);// ȷ��ҩƷ�����е�����
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// ����Ĭ��ֵ���հס�ռλ
			}
		}
		int count = 0;// ��¼������
		for (int i = 0; i < back.getCount("PHA_RULE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("PHA_RULE", j).equals(back.getValue("PHA_RULE", i))
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
        for (int i = 0; i < back.getCount("PHA_RULE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("PHA_RULE", "�ϼ�");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("PHA_RULE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "ҩƷ���੅�ϱ�����              ,200,PHA_RULE;");
		headerName = headerAddLengthInfo(headerName);//��������ֵ�еĿ��
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}

	/**
     * ��ѯ�����¼�ͳ����Ϣ�������¼�����/�ϱ����ҡ���
     * @param parm TParm
     * @return TParm
     */
    public TParm selectStatisticByNameAndDept(TParm parm) {
        String sql = "SELECT A.ADR_ID1 ADR_ID, A.REPORT_DEPT AS DEPT_CODE, B.DEPT_CHN_DESC AS DEPT_DESC, COUNT(*) AS COUNT " 
                + "     FROM ACI_ADRM A, SYS_DEPT B       "
                + "    WHERE A.EVENT_DATE BETWEEN # AND # "
                + "      AND A.REPORT_DEPT = B.DEPT_CODE  "
                + " GROUP BY A.ADR_ID1, A.REPORT_DEPT, B.DEPT_CHN_DESC "
                + " ORDER BY A.ADR_ID1, A.REPORT_DEPT, B.DEPT_CHN_DESC";// ������ʵ����Ҫ
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
        headerList.add(0, "ADR_ID");
        String parmMap = "ADR_ID";// ���ParmMap
        int index = 0;
        TreeSet<String> deptSet = new TreeSet<String>();
        for (int i = 0; i < result.getCount("DEPT_DESC"); i++) {
            deptSet.add(result.getValue("DEPT_DESC", i));
        }
        for (String str : deptSet) {
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
        TreeSet<String> nameSet = new TreeSet<String>();
        for (int i = 0; i < result.getCount("ADR_ID"); i++) {
            nameSet.add(result.getValue("ADR_ID", i));
        }
        for (String str : nameSet) {
            back.addData(header[0], str);// ȷ���¼������е�����
        }
        for (int i = 1; i < header.length; i++) {
            for (int j = 0; j < back.getCount(header[0]); j++) {
                back.addData(header[i], "");// ����Ĭ��ֵ���հס�ռλ
            }
        }
        int count = 0;// ��¼������
        for (int i = 0; i < back.getCount("ADR_ID"); i++) {
            for (int j = 0; j < result.getCount(); j++) {
                for (int k = 0; k < date.length; k++) {
                    if (result.getValue("ADR_ID", j).equals(back.getValue("ADR_ID", i))
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
        for (int i = 0; i < back.getCount("ADR_ID"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("ADR_ID", "�ϼ�");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("ADR_ID"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
        headerName = headerName.replaceFirst(";", "�¼����Ʃ��ϱ�����              ,330,ADR_ID;");
        headerName = headerAddLengthInfo(headerName);//��������ֵ�еĿ��
        back.setData("TABLE_HEADER", headerName);
        back.setData("TABLE_PARMMAP", parmMap);
        return back;
    }
    
	/**
	 * ��ѯ�����¼�ͳ����Ϣ�������¼�����/ҩƷ���ࡱ ��
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm selectStatisticByNameAndPha(TParm parm) {
        String sql = "SELECT A.ADR_ID1 ADR_ID, SUBSTR(B.ORDER_CODE,0,2) AS PHA_TYPE, C.CATEGORY_CHN_DESC AS PHA_RULE, COUNT(*) AS COUNT "
                + "     FROM ACI_ADRM A, ACI_ADRD B, SYS_CATEGORY C "
                + "    WHERE A.EVENT_DATE BETWEEN # AND # "
                + "      AND A.ACI_NO = B.ACI_NO      "
                + "      AND B.TYPE = '0'             "//����ҩƷ
                + "      AND C.RULE_TYPE = 'PHA_RULE' "//ҩƷ����
                + "      AND C.DETAIL_FLG = 'N'       "//�Ǹ��ڵ�
                + "      AND LENGTH(C.CATEGORY_CODE) = 2 "//��֧�ڵ�
                + "      AND SUBSTR(B.ORDER_CODE,0,2) = C.CATEGORY_CODE "
                + " GROUP BY A.ADR_ID1, SUBSTR(B.ORDER_CODE,0,2), C.CATEGORY_CHN_DESC "
                + " ORDER BY A.ADR_ID1, SUBSTR(B.ORDER_CODE,0,2), C.CATEGORY_CHN_DESC";// ������ʵ����Ҫ
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
		headerList.add(0, "ADR_ID");
		String parmMap = "ADR_ID";// ���ParmMap
		int index = 0;
		TreeSet<String> phaSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("PHA_RULE"); i++) {
			phaSet.add(result.getValue("PHA_RULE", i));
		}
		for (String str : phaSet) {
			headerName += ";" + str;// ȷ�����header
			dateList.add(str);
			String title = "PHA_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// ȷ�����parmMap
		}
		String[] header = new String[headerList.size()];
		String[] date = new String[dateList.size()];
		headerList.toArray(header);
		dateList.toArray(date);
		TreeSet<String> nameSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("ADR_ID"); i++) {
			nameSet.add(result.getValue("ADR_ID", i));
		}
		for (String str : nameSet) {
			back.addData(header[0], str);// ȷ���¼������е�����
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// ����Ĭ��ֵ���հס�ռλ
			}
		}
		int count = 0;// ��¼������
		for (int i = 0; i < back.getCount("ADR_ID"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("ADR_ID", j).equals(back.getValue("ADR_ID", i))
							&& result.getValue("PHA_RULE", j).equals(date[k])) {
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
        for (int i = 0; i < back.getCount("ADR_ID"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("ADR_ID", "�ϼ�");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("ADR_ID"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "�¼����Ʃ�ҩƷ����             ,250,ADR_ID;");
		headerName = headerAddLengthInfo(headerName);//��������ֵ�еĿ��
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}

	/**
	 * ��Table��header���ӳ�����Ϣ
	 * @param tableHeader
	 * @return
	 */
	public String headerAddLengthInfo(String tableHeader) {
		String[] headerPart = tableHeader.split(";");
		String returnStr = headerPart[0];
		for (int i = 1; i < headerPart.length; i++) {
			returnStr += ";" + headerPart[i] + "," + countStrLength(headerPart[i]);
		}
		return returnStr;
	}
	
	
    /**
     * ����������ռ�ĳ���
     * 
     * @param str
     * @return
     */
    public static int countStrLength(String str) {
        try {
            str = new String(str.getBytes("GBK"), "ISO8859_1");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int length = (str.length() * 8) < 50 ? 50 : (str.length() * 8);//��Ȳ�С��50
        return length ;
    }
	
	

    /**
     * ���ز�����Ա����ϸ��Ϣ
     * @param id
     * @return
     */
    public TParm getOperatorInfo(String id) {
        String sql = "SELECT * FROM SYS_OPERATOR  WHERE USER_ID = '#'";
        sql = sql.replaceFirst("#", id);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
 
}
