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
 * <p>Title: 药品不良事件工具类 </p>
 *
 * <p>Description: 药品不良事件工具类 </p>
 *
 * <p>Copyright: Copyright (c) 2013 </p>
 *
 * <p>Company: BLueCore </p>
 *
 * @author wanglong 2013.09.30
 * @version 1.0
 */
public class ACIADRTool extends TJDOTool {
	
    public static ACIADRTool instanceObject;// 实例
    
    /**
     * 得到实例
     * @return ACIRecordTool
     */
    public static ACIADRTool getInstance() {
        if (instanceObject == null) instanceObject = new ACIADRTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ACIADRTool() {
        setModuleName("aci\\ACIADRModule.x");
        onInit();
    }
    
    /**
     * 保存
     * 
     * @param parm
     * @return
     */
    public TParm onSave(TParm parm, TConnection conn) {
        TParm result = new TParm();
        Map inMap = (HashMap) parm.getData("IN_MAP");
        String[] sql = (String[]) inMap.get("SQL");
        if (sql == null||sql.length < 1) {
            result.setErr(-1, "参数为空");
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
     * 查询不良事件
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
     * 删除不良事件主档数据
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
     * 删除不良事件细档数据
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
     * 已上报/取消已上报
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
     * 查询不良事件统计信息（按例数）
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticByCount(TParm parm) {
		TParm back = new TParm();
		String headerName = "";// 表格Header
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
			headerName += "年份     ,60";
		} else if (parm.getValue("DATE_TYPE").equals("MONTH")) {
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm') ");
			sql = sql.replaceFirst("#", "ADD_MONTHS (TO_DATE ('@', 'yyyy/mm'), 1) - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
			headerName += "月份     ,60";
		} else if (parm.getValue("DATE_TYPE").equals("DAY")) {
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') ");
			sql = sql.replaceFirst("#", "TO_DATE ('@', 'yyyy/mm/dd') + 1 - 1 / 24 / 60 / 60 ");
			sql = sql.replaceFirst("#", "yyyy/mm/dd");
			sql = sql.replaceFirst("@", parm.getValue("START_DATE"));
			sql = sql.replaceFirst("@", parm.getValue("END_DATE"));
			headerName += "日期     ,60";
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		back.addData("FIRST_COLUMN", "例数");
		String parmMap = "FIRST_COLUMN";// 表格ParmMap
		int count = 0;// 记录总例数
		for (int i = 0; i < result.getCount(); i++) {
			String title = "DATE_" + (i + 1);
			parmMap += ";" + title;
			headerName += ";" + result.getValue("EVENT_DATE", i);
			back.addData(title, result.getValue("COUNT", i));
			count += result.getInt("COUNT", i);
		}
		//===========以下开始生成总计行=============//delete by wanglong 20130418
//		String[] headerArr = parmMap.split(";");
//		if (headerArr.length == 2) {
//			back.addData("FIRST_COLUMN", "总数");
//			back.addData(headerArr[1], count);// 用于总计行
//		} else if (headerArr.length > 2) {
//			back.addData("FIRST_COLUMN", "");
//			back.addData(headerArr[headerArr.length - 1], count);
//			back.addData(headerArr[headerArr.length - 2], "总数");
//			for (int i = headerArr.length - 3; i > 0; i--) {
//				back.addData(headerArr[i], "");
//			}
//		}
	    //===========以下开始生成总计列=============//add by wanglong 20130418
        headerName = headerName + ";总数";
        parmMap += ";COUNT";
        back.addData("COUNT", count);
		headerName = headerAddLengthInfo(headerName);//调整各数值列的宽度
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
    
	/**
     * 查询不良事件统计信息（按性别）
     * @param parm TParm
     * @return TParm
     */
    public TParm selectStatisticBySex(TParm parm) {
        String sql = "SELECT SEX_CODE,TO_CHAR(EVENT_DATE,'#') AS EVENT_DATE, COUNT(*) AS COUNT "
                + "     FROM ACI_ADRM                   " 
                + "    WHERE EVENT_DATE BETWEEN # AND # "
                + " GROUP BY SEX_CODE, TO_CHAR(EVENT_DATE, '#') "
                + " ORDER BY EVENT_DATE, SEX_CODE ";// 排序其实不需要
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
        String headerName = "";// 表格Header
        ArrayList<String> headerList = new ArrayList<String>();
        ArrayList<String> dateList = new ArrayList<String>();
        headerList.add(0, "SEX_CODE");
        String parmMap = "SEX_CODE";// 表格ParmMap
        int index = 0;
        TreeSet<String> dateSet = new TreeSet<String>();
        for (int i = 0; i < result.getCount("EVENT_DATE"); i++) {
            dateSet.add(result.getValue("EVENT_DATE", i));
        }
        for (String str : dateSet) {
            headerName += ";" + str;// 确立表格header
            dateList.add(str);
            String title = "DATE_" + (++index);
            headerList.add(title);
            parmMap += ";" + title;// 确立表格parmMap
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
            back.addData(header[0], str);// 确立科室列的数据
        }
        for (int i = 1; i < header.length; i++) {
            for (int j = 0; j < back.getCount(header[0]); j++) {
                back.addData(header[i], "");// 填入默认值【空白】占位
            }
        }
        int count = 0;// 记录总例数
        for (int i = 0; i < back.getCount("SEX_CODE"); i++) {
            for (int j = 0; j < result.getCount(); j++) {
                for (int k = 0; k < date.length; k++) {
                    if (result.getValue("SEX_CODE", j).equals(back.getValue("SEX_CODE", i))
                            && result.getValue("EVENT_DATE", j).equals(date[k])) {
                        back.setData(header[k + 1], i, result.getValue("COUNT", j));// 开始修改该默认值为统计值（例数）
                        count += result.getInt("COUNT", j);
                    } else {
                        continue;
                    }
                }
            }
        }
        //===========以下开始生成总计行=============
        headerName += ";合计";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("SEX_CODE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("SEX_CODE", "合计");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("SEX_CODE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
        headerName = headerName.replaceFirst(";", "性别    ,60,SEX_CODE;");
        headerName = headerAddLengthInfo(headerName);//调整各数值列的宽度
        parmMap += ";合计";
        back.setData("TABLE_HEADER", headerName);
        back.setData("TABLE_PARMMAP", parmMap);
        return back;
    }
    
    /**
     * 查询不良事件统计信息（按年龄段）
     * @param parm TParm
     * @return TParm
     */
    public TParm selectStatisticByAge(TParm parm) {
        String sql = "SELECT TO_CHAR(ROUND(MONTHS_BETWEEN(SYSDATE,BIRTH_DATE)/120)) AS AGE_SEG, "
                + "          TO_CHAR(EVENT_DATE,'#') AS EVENT_DATE, COUNT(*) AS COUNT "
                + "     FROM ACI_ADRM                   " 
                + "    WHERE EVENT_DATE BETWEEN # AND # "
        		+ " GROUP BY TO_CHAR(ROUND(MONTHS_BETWEEN(SYSDATE,BIRTH_DATE)/120)), TO_CHAR(EVENT_DATE, '#') "
        		+ " ORDER BY EVENT_DATE, TO_CHAR(ROUND(MONTHS_BETWEEN(SYSDATE,BIRTH_DATE)/120))";// 排序其实不需要
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
        String headerName = "";// 表格Header
        ArrayList<String> headerList = new ArrayList<String>();
        ArrayList<String> dateList = new ArrayList<String>();
        headerList.add(0, "AGE_SEG");
        String parmMap = "AGE_SEG";// 表格ParmMap
        int index = 0;
        TreeSet<String> dateSet = new TreeSet<String>();
        for (int i = 0; i < result.getCount("EVENT_DATE"); i++) {
            dateSet.add(result.getValue("EVENT_DATE", i));
        }
        for (String str : dateSet) {
            headerName += ";" + str;// 确立表格header
            dateList.add(str);
            String title = "DATE_" + (++index);
            headerList.add(title);
            parmMap += ";" + title;// 确立表格parmMap
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
            back.addData(header[0], str);// 确立年龄列的数据
        }
        for (int i = 1; i < header.length; i++) {
            for (int j = 0; j < back.getCount(header[0]); j++) {
                back.addData(header[i], "");// 填入默认值【空白】占位
            }
        }
        int count = 0;// 记录总例数
        for (int i = 0; i < back.getCount("AGE_SEG"); i++) {
            for (int j = 0; j < result.getCount(); j++) {
                for (int k = 0; k < date.length; k++) {
                    if (result.getValue("AGE_SEG", j).equals(back.getValue("AGE_SEG", i))
                            && result.getValue("EVENT_DATE", j).equals(date[k])) {
                        back.setData(header[k + 1], i, result.getValue("COUNT", j));// 开始修改该默认值为统计值（例数）
                        count += result.getInt("COUNT", j);
                    } else {
                        continue;
                    }
                }
            }
        }
        //===========以下开始生成总计行=============
        headerName += ";合计";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("AGE_SEG"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("AGE_SEG", "合计");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("AGE_SEG"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
        headerName = headerName.replaceFirst(";", "年龄组(岁) ,80,AGE_SEG;");
        headerName = headerAddLengthInfo(headerName);//调整各数值列的宽度
        parmMap += ";合计";
        back.setData("TABLE_HEADER", headerName);
        back.setData("TABLE_PARMMAP", parmMap);
        return back;
    }
    
    /**
     * 查询不良事件统计信息（按上报科室）
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticByDept(TParm parm) {
		String sql = "SELECT REPORT_DEPT AS DEPT_CODE, TO_CHAR(EVENT_DATE, '#') AS EVENT_DATE, COUNT(*) AS COUNT "
				+ " FROM ACI_ADRM                      "
				+ " WHERE EVENT_DATE BETWEEN # AND # ? "
				+ " GROUP BY REPORT_DEPT, TO_CHAR(EVENT_DATE, '#') "
				+ " ORDER BY EVENT_DATE, REPORT_DEPT";// 排序其实不需要
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
		String headerName = "";// 表格Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "DEPT_CODE");
		String parmMap = "DEPT_CODE";// 表格ParmMap
		int index = 0;
		TreeSet<String> dateSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("EVENT_DATE"); i++) {
			dateSet.add(result.getValue("EVENT_DATE", i));
		}
		for (String str : dateSet) {
			headerName += ";" + str;// 确立表格header
			dateList.add(str);
			String title = "DATE_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// 确立表格parmMap
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
			back.addData(header[0], str);// 确立科室列的数据
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// 填入默认值【空白】占位
			}
		}
		int count = 0;// 记录总例数
		for (int i = 0; i < back.getCount("DEPT_CODE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("DEPT_CODE", j).equals(back.getValue("DEPT_CODE", i))
							&& result.getValue("EVENT_DATE", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// 开始修改该默认值为统计值（例数）
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========以下开始生成总计行=============
        headerName += ";合计";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("DEPT_CODE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("DEPT_CODE", "合计");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("DEPT_CODE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "上报科室          ,120,DEPT_CODE;");
		headerName = headerAddLengthInfo(headerName);//调整各数值列的宽度
	    parmMap += ";合计";
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}
    
    /**
     * 查询不良事件统计信息（按药品分类）
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticByPha(TParm parm) {
        String sql = "SELECT SUBSTR(B.ORDER_CODE,0,2) AS PHA_TYPE, TO_CHAR(A.EVENT_DATE,'#') AS EVENT_DATE, COUNT(*) AS COUNT "
                + "     FROM ACI_ADRM A, ACI_ADRD B "
                + "    WHERE A.EVENT_DATE BETWEEN # AND # ? "
                + "      AND A.ACI_NO = B.ACI_NO "
                + "      AND B.TYPE = '0'        "//怀疑药品
                + " GROUP BY SUBSTR(B.ORDER_CODE,0,2), TO_CHAR(A.EVENT_DATE, '#') "
                + " ORDER BY 1,2";// 排序其实不需要
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
		String headerName = "";// 表格Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "PHA_TYPE");
		String parmMap = "PHA_TYPE";// 表格ParmMap
		int index = 0;
		TreeSet<String> dateSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("EVENT_DATE"); i++) {
			dateSet.add(result.getValue("EVENT_DATE", i));
		}
		for (String str : dateSet) {
			headerName += ";" + str;// 确立表格header
			dateList.add(str);
			String title = "DATE_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// 确立表格parmMap
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
			back.addData(header[0], str);// 确立分级列的数据
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// 填入默认值【空白】占位
			}
		}
		int count = 0;// 记录总例数
		for (int i = 0; i < back.getCount("PHA_TYPE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("PHA_TYPE", j).equals(back.getValue("PHA_TYPE", i))
							&& result.getValue("EVENT_DATE", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// 开始修改该默认值为统计值（例数）
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========以下开始生成总计行=============
        headerName += ";合计";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("PHA_TYPE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("PHA_TYPE", "合计");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("PHA_TYPE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "怀疑药品分类               ,180,PHA_TYPE;");
		headerName = headerAddLengthInfo(headerName);//调整各数值列的宽度
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}

	   /**
     * 查询不良事件统计信息（按报告类型）
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
                + " ORDER BY EVENT_DATE, REPORT_KIND";// 排序其实不需要
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
        String headerName = "";// 表格Header
        ArrayList<String> headerList = new ArrayList<String>();
        ArrayList<String> dateList = new ArrayList<String>();
        headerList.add(0, "REPORT_KIND");
        String parmMap = "REPORT_KIND";// 表格ParmMap
        int index = 0;
        TreeSet<String> dateSet = new TreeSet<String>();
        for (int i = 0; i < result.getCount("EVENT_DATE"); i++) {
            dateSet.add(result.getValue("EVENT_DATE", i));
        }
        for (String str : dateSet) {
            headerName += ";" + str;// 确立表格header
            dateList.add(str);
            String title = "DATE_" + (++index);
            headerList.add(title);
            parmMap += ";" + title;// 确立表格parmMap
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
            back.addData(header[0], str);// 确立科室列的数据
        }
        for (int i = 1; i < header.length; i++) {
            for (int j = 0; j < back.getCount(header[0]); j++) {
                back.addData(header[i], "");// 填入默认值【空白】占位
            }
        }
        int count = 0;// 记录总例数
        for (int i = 0; i < back.getCount("REPORT_KIND"); i++) {
            for (int j = 0; j < result.getCount(); j++) {
                for (int k = 0; k < date.length; k++) {
                    if (result.getValue("REPORT_KIND", j).equals(back.getValue("REPORT_KIND", i))
                            && result.getValue("EVENT_DATE", j).equals(date[k])) {
                        back.setData(header[k + 1], i, result.getValue("COUNT", j));// 开始修改该默认值为统计值（例数）
                        count += result.getInt("COUNT", j);
                    } else {
                        continue;
                    }
                }
            }
        }
        //===========以下开始生成总计行=============
        headerName += ";合计";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("REPORT_KIND"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("REPORT_KIND", "合计");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("REPORT_KIND"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
        headerName = headerName.replaceFirst(";", "报告类型  ,70,REPORT_KIND;");
        headerName = headerAddLengthInfo(headerName);//调整各数值列的宽度
        parmMap += ";合计";
        back.setData("TABLE_HEADER", headerName);
        back.setData("TABLE_PARMMAP", parmMap);
        return back;
    }
    
    /**
     * 查询不良事件统计信息（按事件名称）
     * @param parm TParm
     * @return TParm
     */
    public TParm selectStatisticByName(TParm parm) {
        String sql = "SELECT ADR_ID1 AS ADR_TYPE, TO_CHAR(EVENT_DATE,'#') AS EVENT_DATE, COUNT(*) AS COUNT "
                + "     FROM ACI_ADRM                     "
                + "    WHERE EVENT_DATE BETWEEN # AND # ? "
                + " GROUP BY ADR_ID1, TO_CHAR(EVENT_DATE, '#') "
                + " ORDER BY EVENT_DATE, ADR_ID1";// 排序其实不需要
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
        String headerName = "";// 表格Header
        ArrayList<String> headerList = new ArrayList<String>();
        ArrayList<String> dateList = new ArrayList<String>();
        headerList.add(0, "ADR_TYPE");
        String parmMap = "ADR_TYPE";// 表格ParmMap
        int index = 0;
        TreeSet<String> dateSet = new TreeSet<String>();
        for (int i = 0; i < result.getCount("EVENT_DATE"); i++) {
            dateSet.add(result.getValue("EVENT_DATE", i));
        }
        for (String str : dateSet) {
            headerName += ";" + str;// 确立表格header
            dateList.add(str);
            String title = "DATE_" + (++index);
            headerList.add(title);
            parmMap += ";" + title;// 确立表格parmMap
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
            back.addData(header[0], str);// 确立名称列的数据
        }
        for (int i = 1; i < header.length; i++) {
            for (int j = 0; j < back.getCount(header[0]); j++) {
                back.addData(header[i], "");// 填入默认值【空白】占位
            }
        }
        int count = 0;// 记录总例数
        for (int i = 0; i < back.getCount("ADR_TYPE"); i++) {
            for (int j = 0; j < result.getCount(); j++) {
                for (int k = 0; k < date.length; k++) {
                    if (result.getValue("ADR_TYPE", j).equals(back.getValue("ADR_TYPE", i))
                            && result.getValue("EVENT_DATE", j).equals(date[k])) {
                        back.setData(header[k + 1], i, result.getValue("COUNT", j));// 开始修改该默认值为统计值（例数）
                        count += result.getInt("COUNT", j);
                    } else {
                        continue;
                    }
                }
            }
        }
        // ===========以下开始生成总计行=============
        headerName += ";合计";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("ADR_TYPE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("ADR_TYPE", "合计");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("ADR_TYPE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
        headerName = headerName.replaceFirst(";", "事件名称                    ,180,ADR_TYPE;");
        headerName = headerAddLengthInfo(headerName);// 调整各数值列的宽度
        back.setData("TABLE_HEADER", headerName);
        back.setData("TABLE_PARMMAP", parmMap);
        return back;
    }
    
    /**
     * 查询不良事件统计信息（按“药品分类/上报科室”	）
     * @param parm TParm
     * @return TParm
     */
	public TParm selectStatisticByPhaAndDept(TParm parm) {
        String sql = "SELECT SUBSTR(B.ORDER_CODE,0,2) AS PHA_RULE, A.REPORT_DEPT AS DEPT_CODE, "
	            + "          C.DEPT_CHN_DESC AS DEPT_DESC, COUNT(*) AS COUNT "
                + "     FROM ACI_ADRM A, ACI_ADRD B, SYS_DEPT C "
                + "    WHERE A.EVENT_DATE BETWEEN # AND # "
                + "      AND A.ACI_NO = B.ACI_NO          "
                + "      AND B.TYPE = '0'                 "//怀疑药品
                + "      AND A.REPORT_DEPT = C.DEPT_CODE "
                + " GROUP BY SUBSTR(B.ORDER_CODE,0,2), A.REPORT_DEPT, C.DEPT_CHN_DESC "
                + " ORDER BY SUBSTR(B.ORDER_CODE,0,2), A.REPORT_DEPT, C.DEPT_CHN_DESC";// 排序其实不需要
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
		String headerName = "";// 表格Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "PHA_RULE");
		String parmMap = "PHA_RULE";// 表格ParmMap
		int index = 0;
		TreeSet<String> deptSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("DEPT_DESC"); i++) {
			deptSet.add(result.getValue("DEPT_DESC", i));
		}
		for (String str : deptSet) {
			headerName += ";" + str;// 确立表格header
			dateList.add(str);
			String title = "DEPT_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// 确立表格parmMap
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
			back.addData(header[0], str);// 确立药品分类列的数据
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// 填入默认值【空白】占位
			}
		}
		int count = 0;// 记录总例数
		for (int i = 0; i < back.getCount("PHA_RULE"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("PHA_RULE", j).equals(back.getValue("PHA_RULE", i))
							&& result.getValue("DEPT_DESC", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// 开始修改该默认值为统计值（例数）
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========以下开始生成总计行=============
        headerName += ";合计";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("PHA_RULE"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("PHA_RULE", "合计");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("PHA_RULE"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "药品分类﹨上报科室              ,200,PHA_RULE;");
		headerName = headerAddLengthInfo(headerName);//调整各数值列的宽度
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}

	/**
     * 查询不良事件统计信息（按“事件名称/上报科室”）
     * @param parm TParm
     * @return TParm
     */
    public TParm selectStatisticByNameAndDept(TParm parm) {
        String sql = "SELECT A.ADR_ID1 ADR_ID, A.REPORT_DEPT AS DEPT_CODE, B.DEPT_CHN_DESC AS DEPT_DESC, COUNT(*) AS COUNT " 
                + "     FROM ACI_ADRM A, SYS_DEPT B       "
                + "    WHERE A.EVENT_DATE BETWEEN # AND # "
                + "      AND A.REPORT_DEPT = B.DEPT_CODE  "
                + " GROUP BY A.ADR_ID1, A.REPORT_DEPT, B.DEPT_CHN_DESC "
                + " ORDER BY A.ADR_ID1, A.REPORT_DEPT, B.DEPT_CHN_DESC";// 排序其实不需要
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
        String headerName = "";// 表格Header
        ArrayList<String> headerList = new ArrayList<String>();
        ArrayList<String> dateList = new ArrayList<String>();
        headerList.add(0, "ADR_ID");
        String parmMap = "ADR_ID";// 表格ParmMap
        int index = 0;
        TreeSet<String> deptSet = new TreeSet<String>();
        for (int i = 0; i < result.getCount("DEPT_DESC"); i++) {
            deptSet.add(result.getValue("DEPT_DESC", i));
        }
        for (String str : deptSet) {
            headerName += ";" + str;// 确立表格header
            dateList.add(str);
            String title = "DEPT_" + (++index);
            headerList.add(title);
            parmMap += ";" + title;// 确立表格parmMap
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
            back.addData(header[0], str);// 确立事件名称列的数据
        }
        for (int i = 1; i < header.length; i++) {
            for (int j = 0; j < back.getCount(header[0]); j++) {
                back.addData(header[i], "");// 填入默认值【空白】占位
            }
        }
        int count = 0;// 记录总例数
        for (int i = 0; i < back.getCount("ADR_ID"); i++) {
            for (int j = 0; j < result.getCount(); j++) {
                for (int k = 0; k < date.length; k++) {
                    if (result.getValue("ADR_ID", j).equals(back.getValue("ADR_ID", i))
                            && result.getValue("DEPT_DESC", j).equals(date[k])) {
                        back.setData(header[k + 1], i, result.getValue("COUNT", j));// 开始修改该默认值为统计值（例数）
                        count += result.getInt("COUNT", j);
                    } else {
                        continue;
                    }
                }
            }
        }
        //===========以下开始生成总计行=============
        headerName += ";合计";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("ADR_ID"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("ADR_ID", "合计");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("ADR_ID"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
        headerName = headerName.replaceFirst(";", "事件名称﹨上报科室              ,330,ADR_ID;");
        headerName = headerAddLengthInfo(headerName);//调整各数值列的宽度
        back.setData("TABLE_HEADER", headerName);
        back.setData("TABLE_PARMMAP", parmMap);
        return back;
    }
    
	/**
	 * 查询不良事件统计信息（按“事件名称/药品分类” ）
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm selectStatisticByNameAndPha(TParm parm) {
        String sql = "SELECT A.ADR_ID1 ADR_ID, SUBSTR(B.ORDER_CODE,0,2) AS PHA_TYPE, C.CATEGORY_CHN_DESC AS PHA_RULE, COUNT(*) AS COUNT "
                + "     FROM ACI_ADRM A, ACI_ADRD B, SYS_CATEGORY C "
                + "    WHERE A.EVENT_DATE BETWEEN # AND # "
                + "      AND A.ACI_NO = B.ACI_NO      "
                + "      AND B.TYPE = '0'             "//怀疑药品
                + "      AND C.RULE_TYPE = 'PHA_RULE' "//药品分类
                + "      AND C.DETAIL_FLG = 'N'       "//非根节点
                + "      AND LENGTH(C.CATEGORY_CODE) = 2 "//分支节点
                + "      AND SUBSTR(B.ORDER_CODE,0,2) = C.CATEGORY_CODE "
                + " GROUP BY A.ADR_ID1, SUBSTR(B.ORDER_CODE,0,2), C.CATEGORY_CHN_DESC "
                + " ORDER BY A.ADR_ID1, SUBSTR(B.ORDER_CODE,0,2), C.CATEGORY_CHN_DESC";// 排序其实不需要
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
		String headerName = "";// 表格Header
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> dateList = new ArrayList<String>();
		headerList.add(0, "ADR_ID");
		String parmMap = "ADR_ID";// 表格ParmMap
		int index = 0;
		TreeSet<String> phaSet = new TreeSet<String>();
		for (int i = 0; i < result.getCount("PHA_RULE"); i++) {
			phaSet.add(result.getValue("PHA_RULE", i));
		}
		for (String str : phaSet) {
			headerName += ";" + str;// 确立表格header
			dateList.add(str);
			String title = "PHA_" + (++index);
			headerList.add(title);
			parmMap += ";" + title;// 确立表格parmMap
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
			back.addData(header[0], str);// 确立事件名称列的数据
		}
		for (int i = 1; i < header.length; i++) {
			for (int j = 0; j < back.getCount(header[0]); j++) {
				back.addData(header[i], "");// 填入默认值【空白】占位
			}
		}
		int count = 0;// 记录总例数
		for (int i = 0; i < back.getCount("ADR_ID"); i++) {
			for (int j = 0; j < result.getCount(); j++) {
				for (int k = 0; k < date.length; k++) {
					if (result.getValue("ADR_ID", j).equals(back.getValue("ADR_ID", i))
							&& result.getValue("PHA_RULE", j).equals(date[k])) {
						back.setData(header[k + 1], i, result.getValue("COUNT", j));// 开始修改该默认值为统计值（例数）
						count += result.getInt("COUNT", j);
					} else {
						continue;
					}
				}
			}
		}
		//===========以下开始生成总计行=============
        headerName += ";合计";
        parmMap += ";SUM";
        String[] mapName = parmMap.split(";");
        for (int i = 0; i < back.getCount("ADR_ID"); i++) {
            int sum = 0;
            for (int j = 1; j < mapName.length; j++) {
                sum += back.getInt(mapName[j], i);
            }
            back.addData("SUM", sum);
        }
        back.addData("ADR_ID", "合计");
        back.addData("SUM", count);
        for (int i = 1; i < mapName.length - 1; i++) {
            int sum = 0;
            for (int j = 0; j < back.getCount("ADR_ID"); j++) {
                sum += back.getInt(mapName[i], j);
            }
            back.addData(mapName[i], sum);
        }
		headerName = headerName.replaceFirst(";", "事件名称﹨药品分类             ,250,ADR_ID;");
		headerName = headerAddLengthInfo(headerName);//调整各数值列的宽度
		back.setData("TABLE_HEADER", headerName);
		back.setData("TABLE_PARMMAP", parmMap);
		return back;
	}

	/**
	 * 给Table的header增加长度信息
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
     * 计算文字所占的长度
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
        int length = (str.length() * 8) < 50 ? 50 : (str.length() * 8);//宽度不小于50
        return length ;
    }
	
	

    /**
     * 返回操作人员的详细信息
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
