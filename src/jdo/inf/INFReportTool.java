package jdo.inf;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import java.text.DecimalFormat;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;

/**
 * <p>Title: �пر�������</p>
 *
 * <p>Description: �пر�������</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFReportTool extends TJDOTool{

    /**
     * ������
     */
    public INFReportTool() {
        setModuleName("inf\\INFReportModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    private static INFReportTool instanceObject;

    /**
     * �õ�ʵ��
     * @return INFReportTool
     */
    public static INFReportTool getInstance() {
        if (instanceObject == null) instanceObject = new INFReportTool();
        return instanceObject;
    }

    /**
     * ��ѯ�������п��ҳ�Ժ���˲�������
     * @param parm TParm
     * @return TParm
     */
    public TParm countMroRecord(TParm parm) {
        TParm result = query("countMroRecord", parm);
        return result;
    }

    /**
     * ͳ�ƿ��Ҹпز�����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm countDeptInfCount(TParm parm) {
        TParm result = query("countDeptInfCount", parm);
        return result;
    }

    /**
     * ȡ�ÿ����ܲ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm countDeptMroRecord(TParm parm) {
        TParm result = query("countDeptMroRecord", parm);
        return result;
    }

    /**
     * ȡ�������ٴ�����
     * @return TParm
     */
    public TParm selectAllDept(TParm parm) {
        TParm result = query("selectAllDept", parm);
        return result;
    }

    /**
     * ȡ�ÿ��Ҽ�������Ϣ
     * @return TParm
     */
    public TParm selectDeptAllGainPoint(TParm parm) {
        TParm result = query("selectDeptAllGainPoint", parm);
        return result;
    }

    /**
     * ȡ�ÿ��Ҽ����Ŀ��Ϣ
     * @return TParm
     */
    public TParm selectDeptAllEXMItem(TParm parm) {
        TParm result = query("selectDeptAllEXMItem", parm);
        return result;
    }

    /**
     * ȡ�����п��Ҽ����Ϣ
     * @return TParm
     */
    public TParm selectYearMonEXMCount(TParm parm) {
        TParm result = query("selectYearMonEXMCount", parm);
        return result;
    }

    /**
     * ȡ�����п���ͳ������
     * @return TParm
     */
    public TParm selectYearMonEXMDate(TParm parm) {
        TParm result = query("selectYearMonEXMDate", parm);
        return result;
    }

    /**
     * ��Ⱦ�����±���
     * @param parm TParm
     * @return TParm
     */
    public TParm countInfCaseMonReport(TParm parm) {
        DecimalFormat df = new DecimalFormat("0.00");
        TParm result = new TParm();
        // ��ѯ�������п��ҳ�Ժ���˲�������
        TParm recordCount = countMroRecord(parm);
        // ȡ�������ٴ�����
        TParm allDept = selectAllDept(parm);
        // ���ÿ�������
        result.addData("DEPT_CHN_DESC", "�ϼ�");
        // ���ÿ����ܲ�����
        result.addData("COUNT", recordCount.getInt("COUNT", 0));
        // ���ø�Ⱦ������
        result.addData("INF_COUNT", "");
        // ���ø�Ⱦ��
        result.addData("INF_RATE", "");
        // ���ù��ɱ�
        result.addData("CONSTRUCT_RATE", "100.00");
        // ����ʵ����Ⱦ������
        result.addData("REPORT_COUNT", "");
        // ����ʵ����Ⱦ��
        result.addData("REPORT_RATE", "");
        // ����©����
        result.addData("NO_REPORT_COUNT", "");
        // ����©����
        result.addData("NO_REPORT_RATE", "");
        // �����ͼ첡����
        result.addData("ETIOLGEXM_COUNT", "");
        // �����ͼ���
        result.addData("ETIOLGEXM_RATE", "");
        // �ܸ�Ⱦ����
        int allInfCount = 0;
        // ��ʵ����Ⱦ����
        int allReportCount = 0;
        // ���ͼ���
        int allEtiolgexmCount = 0;
        for (int i = 0; i < allDept.getCount(); i++) {
            parm.setData("DEPT_CODE", allDept.getData("DEPT_CODE", i));
            // ȡ�ÿ����ܲ�����
            TParm recordDeptCount = countDeptMroRecord(parm);
            // ȡ�ÿ��Ҹпز�����Ϣ
            TParm deptInfCount = countDeptInfCount(parm);
            // ���ÿ�������
            result.addData("DEPT_CHN_DESC", allDept.getData("DEPT_CHN_DESC", i));
            // ���ÿ����ܲ�����
            result.addData("COUNT", recordDeptCount.getInt("COUNT", 0));
            // ���ø�Ⱦ������
            result.addData("INF_COUNT", deptInfCount.getInt("INF_COUNT", 0));
            // ���ø�Ⱦ��(��Ⱦ����/���Ҳ�������)
            if (recordDeptCount.getDouble("COUNT", 0) != 0) result.addData("INF_RATE", df
                    .format(deptInfCount.getDouble("INF_COUNT", 0)
                            / recordDeptCount.getDouble("COUNT", 0) * 100));
            else result.addData("INF_RATE", "0.00");
            // ���ù��ɱ�(���Ҳ�������/���п��Ҳ�������)
            if (recordCount.getDouble("COUNT", 0) != 0) result.addData("CONSTRUCT_RATE", df
                    .format(recordDeptCount.getDouble("COUNT", 0)
                            / recordCount.getDouble("COUNT", 0) * 100));
            else result.addData("CONSTRUCT_RATE", "0.00");
            // ����ʵ����Ⱦ������
            result.addData("REPORT_COUNT", deptInfCount.getInt("REPORT_COUNT", 0));
            // ����ʵ����Ⱦ��(ʵ����Ⱦ����/���Ҳ�������)
            if (recordDeptCount.getDouble("COUNT", 0) != 0) result.addData("REPORT_RATE", df
                    .format(deptInfCount.getDouble("REPORT_COUNT", 0)
                            / recordDeptCount.getDouble("COUNT", 0) * 100));
            else result.addData("REPORT_RATE", "0.00");
            // ����©����(��Ⱦ����-ʵ����Ⱦ����)
            result.addData("NO_REPORT_COUNT", deptInfCount.getInt("INF_COUNT", 0)
                    - deptInfCount.getInt("REPORT_COUNT", 0));
            // ����©����(©����/��Ⱦ����)
            if (deptInfCount.getDouble("INF_COUNT", 0) != 0) result.addData("NO_REPORT_RATE", df
                    .format((deptInfCount.getDouble("INF_COUNT", 0) - deptInfCount
                            .getDouble("REPORT_COUNT", 0))
                            / deptInfCount.getDouble("INF_COUNT", 0) * 100));
            else result.addData("NO_REPORT_RATE", "0.00");
            // �����ͼ첡����
            result.addData("ETIOLGEXM_COUNT", deptInfCount.getInt("ETIOLGEXM_COUNT", 0));
            // �����ͼ���(�ͼ�����/��Ⱦ����)
            if (deptInfCount.getDouble("INF_COUNT", 0) != 0) result.addData("ETIOLGEXM_RATE", df
                    .format(deptInfCount.getDouble("ETIOLGEXM_COUNT", 0)
                            / deptInfCount.getDouble("INF_COUNT", 0) * 100));
            else result.addData("ETIOLGEXM_RATE", "0.00");
            allInfCount += deptInfCount.getInt("INF_COUNT", 0);
            allReportCount += deptInfCount.getInt("REPORT_COUNT", 0);
            allEtiolgexmCount += deptInfCount.getInt("ETIOLGEXM_COUNT", 0);
        }
        // ���úϼ���Ӧ����
        result.setData("INF_COUNT", 0, allInfCount);
        if (recordCount.getInt("COUNT", 0) != 0) result.setData("INF_RATE", 0, df
                .format(allInfCount / recordCount.getDouble("COUNT", 0) * 100));
        else result.setData("INF_RATE", 0, "0.00");
        if (allInfCount == 0) result.setData("CONSTRUCT_RATE", 0, "0.00");
        result.setData("REPORT_COUNT", 0, allReportCount);
        if (recordCount.getInt("COUNT", 0) != 0) result.setData("REPORT_RATE", 0, df
                .format(allReportCount / recordCount.getDouble("COUNT", 0) * 100));
        else result.setData("REPORT_RATE", 0, "0.00");
        result.setData("NO_REPORT_COUNT", 0, allInfCount - allReportCount);
        if (allInfCount != 0) result.setData("NO_REPORT_RATE", 0, df
                .format((allInfCount - allReportCount) / (double) allInfCount * 100));
        else result.setData("NO_REPORT_RATE", 0, "0.00");
        result.setData("ETIOLGEXM_COUNT", 0, allEtiolgexmCount);
        if (allInfCount != 0) result.setData("ETIOLGEXM_RATE", 0, df.format(allEtiolgexmCount
                / (double) allInfCount * 100));
        else result.setData("ETIOLGEXM_RATE", 0, "0.00");
        result.setCount(result.getCount("INF_COUNT"));
        // ���ÿ�������
        result.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        // ���ÿ����ܲ�����
        result.addData("SYSTEM", "COLUMNS", "COUNT");
        // ���ø�Ⱦ������
        result.addData("SYSTEM", "COLUMNS", "INF_COUNT");
        // ���ø�Ⱦ��
        result.addData("SYSTEM", "COLUMNS", "INF_RATE");
        // ���ù��ɱ�
        result.addData("SYSTEM", "COLUMNS", "CONSTRUCT_RATE");
        // ����ʵ����Ⱦ������
        result.addData("SYSTEM", "COLUMNS", "REPORT_COUNT");
        // ����ʵ����Ⱦ��
        result.addData("SYSTEM", "COLUMNS", "REPORT_RATE");
        // ����©����
        result.addData("SYSTEM", "COLUMNS", "NO_REPORT_COUNT");
        // ����©����
        result.addData("SYSTEM", "COLUMNS", "NO_REPORT_RATE");
        // �����ͼ첡����
        result.addData("SYSTEM", "COLUMNS", "ETIOLGEXM_COUNT");
        // �����ͼ���
        result.addData("SYSTEM", "COLUMNS", "ETIOLGEXM_RATE");
        TParm printParm = new TParm();
        printParm.setData("MONTH", parm.getValue("DATE").substring(4, 6));
        printParm.setData("DATE", parm.getValue("DATE").substring(0, 4) + "��"
                + parm.getValue("DATE").substring(4, 6) + "��");
        printParm.setData("OPERATOR", Operator.getName());
        printParm.setData("INF_DEPT_COUNT", result.getData());
        return printParm;
    }

    /**
     * �����ձ�
     * @param parm TParm
     * @return TParm
     */
    public TParm selectHeatDayReport(TParm parm) {
        TParm result = new TParm();
        result = query("selectHeatDayReport", parm);
        // ����
        result.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        // ����
        result.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        // ������
        result.addData("SYSTEM", "COLUMNS", "MR_NO");
        // ��������
        result.addData("SYSTEM", "COLUMNS", "OP_DATE");
        // ����
        result.addData("SYSTEM", "COLUMNS", "TEMPERATURE");
        TParm printParm = new TParm();
        Timestamp timestamp = SystemTool.getInstance().getDate();
        printParm.setData("TABLE", result.getData());
        printParm.setData("EXAMINE_DATE", parm.getValue("EXAMINE_DATE").substring(0, 4) + "��"
                + parm.getValue("EXAMINE_DATE").substring(4, 6) + "��"
                + parm.getValue("EXAMINE_DATE").substring(6, 8) + "��");
        printParm.setData("PRINT_DATE", timestamp.toString().substring(0, 4) + "��"
                + timestamp.toString().substring(5, 7) + "��"
                + timestamp.toString().substring(8, 10) + "��");
        printParm.setData("PRINT_USER", Operator.getName());
        return printParm;
    }

    /**
     * ȡ�ò��������ز�����ʹ����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfAntibiotrcd(TParm parm) {
        TParm result = new TParm();
        result = query("selectInfAntibiotrcd", parm);
        // ����
        result.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        // ����
        result.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        // ҽ������
        result.addData("SYSTEM", "COLUMNS", "USER_NAME");
        // ��������
        result.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        // ����
        result.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        // ����
        result.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
        // ��ͣ������
        result.addData("SYSTEM", "COLUMNS", "SE_DATE");
        // ������ԭ��
        result.addData("SYSTEM", "COLUMNS", "ILLEGIT_REMARK");
        // ������ӳ
        result.addData("SYSTEM", "COLUMNS", "MEDALLERG_SYMP");
        TParm printParm = new TParm();
        Timestamp timestamp = SystemTool.getInstance().getDate();
        printParm.setData("TABLE", result.getData());
        printParm.setData("PRINT_DATE", timestamp.toString().substring(0, 4) + "��"
                + timestamp.toString().substring(5, 7) + "��"
                + timestamp.toString().substring(8, 10) + "��");
        printParm.setData("PRINT_USER", Operator.getName());
        return printParm;
    }

    /**
     * ҽԺ��Ⱦ���������ܱ�1
     * @param parm TParm
     * @return TParm
     */
    public TParm selestInfCaseForReport(TParm parm) {
        TParm result = new TParm();
        result = query("selestInfCaseForReport", parm);
        TParm dataParm = new TParm();
        String deptCode = "";
        String mrNo = "";
        if (result.getCount() > 0) {
            deptCode = result.getValue("DEPT_CODE", 0);
            mrNo = result.getValue("MR_NO", 0);
        }
        for (int i = 0; i < result.getCount(); i++) {
            if (!deptCode.equals(result.getValue("DEPT_CODE", i)) || i == 0) {
                deptCode = result.getValue("DEPT_CODE", i);
                mrNo = result.getValue("MR_NO", i);
                // ���ÿ�������
                dataParm.addData("DEPT_DESC", result.getData("DEPT_CHN_DESC", i));
                // ��Ժ����
                if (result.getData("DS_DATE", i) != null
                        && result.getValue("DS_DATE", i).length() != 0) dataParm
                        .addData("DS_COUNT", 1);
                else dataParm.addData("DS_COUNT", 0);
                // ��Ⱦ����
                dataParm.addData("INF_PAT_COUNT", 1);
                // ��Ⱦ������
                dataParm.addData("INF_COUNT", 1);
                // ��Ⱦ��λ
                for (int j = 1; j < 14; j++) {
                    if (j == result.getInt("INFPOSITION_CODE", i)) {
                        dataParm
                                .addData(
                                         "POSITION_" + "00".substring(0, 2 - ("" + j).length()) + j,
                                         1);
                        continue;
                    }
                    dataParm.addData("POSITION_" + "00".substring(0, 2 - ("" + j).length()) + j, 0);
                }
                // ��������
                if ("04".equals(result.getData("CODE1_STATUS", i))) dataParm
                        .addData("DIE_COUNT", 1);
                else dataParm.addData("DIE_COUNT", 0);
                // ������Ӱ��
                if ("4".equals(result.getData("DIEINFLU_CODE", i))) dataParm.addData("DIE_04", 1);
                else dataParm.addData("DIE_04", 0);
                if ("2".equals(result.getData("DIEINFLU_CODE", i))
                        || "3".equals(result.getData("DIEINFLU_CODE", i))) dataParm
                        .addData("DIE_02", 1);
                else dataParm.addData("DIE_02", 0);
                if ("1".equals(result.getData("DIEINFLU_CODE", i))) dataParm.addData("DIE_01", 1);
                else dataParm.addData("DIE_01", 0);
            } else {
                int row = dataParm.getCount("DEPT_DESC") - 1;
                // ��Ժ����
                if (!mrNo.equals(result.getValue("MR_NO", i))
                        && result.getData("DS_DATE", i) != null
                        && result.getValue("DS_DATE", i).length() != 0) dataParm
                        .setData("DS_COUNT", row, dataParm.getInt("DS_COUNT", row) + 1);
                // ��Ⱦ����
                if (!mrNo.equals(result.getValue("MR_NO", i))) dataParm
                        .setData("INF_PAT_COUNT", row, dataParm.getInt("INF_COUNT", row) + 1);
                // ��Ⱦ����
                dataParm.setData("INF_COUNT", row, dataParm.getInt("INF_COUNT", row) + 1);
                // ��Ⱦ��λ
                dataParm.setData("POSITION_" + result.getData("INFPOSITION_CODE", i), row, dataParm
                        .getInt("POSITION_" + result.getData("INFPOSITION_CODE", i), row) + 1);
                // ��������
                if (!mrNo.equals(result.getValue("MR_NO", i))) {
                    dataParm.setData("DIE_COUNT", row, dataParm.getInt("DIE_COUNT", row) + 1);
                    mrNo = result.getValue("MR_NO", i);
                }
                // ������Ӱ��
                if ("4".equals(result.getData("DIEINFLU_CODE", i))) dataParm
                        .setData("DIE_04", row, dataParm.getInt("DIE_04", row) + 1);
                if ("2".equals(result.getData("DIEINFLU_CODE", i))
                        || "3".equals(result.getData("DIEINFLU_CODE", i))) dataParm
                        .setData("DIE_02", row, dataParm.getInt("DIE_02", row) + 1);
                if ("1".equals(result.getData("DIEINFLU_CODE", i))) dataParm
                        .setData("DIE_01", row, dataParm.getInt("DIE_01", row) + 1);
            }
        }
        TParm printParm = new TParm();
        dataParm.setCount(dataParm.getCount("DEPT_DESC"));
        dataParm.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
        dataParm.addData("SYSTEM", "COLUMNS", "DS_COUNT");
        dataParm.addData("SYSTEM", "COLUMNS", "INF_PAT_COUNT");
        dataParm.addData("SYSTEM", "COLUMNS", "INF_COUNT");
        for (int j = 1; j < 14; j++) {
            dataParm.addData("SYSTEM", "COLUMNS", "POSITION_"
                    + "00".substring(0, 2 - ("" + j).length()) + j);
        }
        dataParm.addData("SYSTEM", "COLUMNS", "DIE_04");
        dataParm.addData("SYSTEM", "COLUMNS", "DIE_02");
        dataParm.addData("SYSTEM", "COLUMNS", "DIE_01");
        printParm.setData("TABLE", dataParm.getData());
        printParm.setData("EXM_DATE", parm.getValue("INF_DATE").substring(0, 4) + "��"
                + parm.getValue("INF_DATE").substring(4, 6) + "��");
        printParm.setData("PRINT_USER", Operator.getName());
        printParm.setData("HOSP_NAME", Operator.getHospitalCHNFullName());
        return printParm;
    }

    /**
     * ����ͳ�Ʋ�ѯ������
     * @param parm TParm
     * @return TParm
     */
    public TParm deptEXMStatisticsDate(TParm parm) {
        TParm result = new TParm();
        TParm point = selectDeptAllGainPoint(parm);
        if (point.getCount() <= 0) return result;
        TParm item = selectDeptAllEXMItem(parm);
        if (item.getCount() <= 0) return result;
        String dept = point.getValue("DEPT_CODE", 0);
        for (int i = 0; i < point.getCount(); i++) {
            if (point.getValue("DEPT_CODE", i).equals(dept) && i != 0) result
                    .addData("DEPT_CHN_DESC", "");
            else {
                result.addData("DEPT_CHN_DESC", point.getValue("DEPT_CHN_DESC", i));
                dept = point.getValue("DEPT_CODE", i);
            }
            result.addData("EXAM_DATE", point.getValue("EXAM_DATE", i));
            result.addData("ITEM_GAINPOINT", point.getValue("ITEM_GAINPOINT", i));
            for (int j = 0; j < item.getCount(); j++) {
                if (!dept.equals(item.getData("DEPT_CODE", j))
                        || !point.getValue("EXAM_NO", i).equals(item.getValue("EXAM_NO", j))
                        || !point.getValue("EXAM_DATE", i).equals(item.getValue("EXAM_DATE", j))) continue;
                result.addData("CHN_DESC", item.getValue("CHN_DESC", j));
                result.addData("PASS_FLG", item.getValue("PASS_FLG", j));
                result.addData("REMARK", item.getValue("REMARK", j));
                if (result.getCount("EXAM_DATE") < result.getCount("CHN_DESC")) {
                    result.addData("DEPT_CHN_DESC", "");
                    result.addData("EXAM_DATE", "");
                    result.addData("ITEM_GAINPOINT", "");
                }
            }
        }
        return result;
    }

    /**
     * ȡ�ÿ�����ȼ����Ϣ
     * @return TParm
     */
    public TParm selectDeptMonCount(TParm parm) {
        TParm result = query("selectDeptMonCount", parm);
        if (result.getCount() <= 0) return result;
        DecimalFormat df = new DecimalFormat("0.00");
        int pointMon = 0;
        String month = result.getValue("EXAM_MONTH", 0);
        int loc = 0;
        int count = 0;
        for (int i = 0; i < result.getCount(); i++) {
            if (month.equals(result.getValue("EXAM_MONTH", i))) {
                pointMon += result.getInt("ITEM_GAINPOINT", i);
                count++;
            } else {
                result.setData("ITEM_GAINPOINT_AVERAGE", loc, df.format(pointMon / count));
                month = result.getValue("EXAM_MONTH", i);
                loc = i;
                pointMon = result.getInt("ITEM_GAINPOINT", i);
                count = 1;
            }
            if (i == result.getCount() - 1) result.setData("ITEM_GAINPOINT_AVERAGE", loc, df
                    .format(pointMon / count));
        }
        for (int i = 0; i < result.getCount(); i++) {
            if (result.getValue("ITEM_GAINPOINT_AVERAGE", i).length() != 0) continue;
            result.setData("EXAM_MONTH", i, "");
        }
        return result;
    }

    /**
     * �����·�ȡ�����п��Ҽ����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectYearMonEXMStatistics(TParm parm) {
        TParm date = selectYearMonEXMDate(parm);
        if (date.getCount() <= 0) return date;
        DecimalFormat df = new DecimalFormat("0.00");
        TParm point = selectYearMonEXMCount(parm);
        for (int i = 0; i < point.getCount(); i++) {
            String dateAll = parm.getValue("YEAR_MONTH").substring(4, 6) + "/";
            int count = 0;
            for (int j = 0; j < date.getCount(); j++) {
                if (!point.getValue("DEPT_CODE", i).equals(date.getValue("DEPT_CODE", j))) continue;
                if (dateAll.length() != 3) dateAll += ",";
                dateAll += date.getData("EXAM_DATE", j);
                count++;
            }
            point.setData("DATE_ALL", i, dateAll);
            point
                    .setData("ITEM_GAINPOINT", i, df.format(point.getInt("ITEM_GAINPOINT", i)
                            / count));
        }
        return point;
    }

    /**
     * ��ѯICU������־
     * @param parm
     * @return
     */
    public TParm selectICULog(TParm parm) {// modify by wanglong 20140304
        String sql =
                "SELECT STA_DATE, DATA_08, DATA_16, DATA_24, DATA_23, DATA_25 "
                        + "  FROM STA_DAILY_01 " + " WHERE STA_DATE BETWEEN '#' AND '@' & "
                        + "ORDER BY STA_DATE, DEPT_CODE, STATION_CODE";
        sql = sql.replaceFirst("#", parm.getValue("START_DATE").replaceAll("/", ""));
        sql = sql.replaceFirst("@", parm.getValue("END_DATE").replaceAll("/", ""));
        if (parm.getData("DEPT_CODE") != null) {
            sql = sql.replaceFirst("&", " AND DEPT_CODE = '" + parm.getValue("DEPT_CODE") + "'");
        } else {
            sql = sql.replaceFirst("&", "");
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ������������־
     * @param parm
     * @return
     */
    public TParm selectNewBornLog(TParm parm) {// modify by wanglong 20140304
        String sql =
                "WITH A AS (SELECT TO_CHAR( A.IN_DATE, 'YYYYMMDD') AS POST_DATE, FLOOR((C.BORNWEIGHT - 1) / 500) AS WEIGHT,"
                        + "                COUNT(DISTINCT A.CASE_NO) AS NEWIN_COUNT "
                        + "           FROM ADM_INP A, SUM_NEWARRIVALSIGN C "
                        + "          WHERE A.CASE_NO = C.CASE_NO "
                        + "            AND A.IN_DATE BETWEEN TO_DATE('#', 'YYYYMMDD') AND TO_DATE('@235959', 'YYYYMMDDHH24MISS') & "
                        + "       GROUP BY TO_CHAR( A.IN_DATE, 'YYYYMMDD'), FLOOR((C.BORNWEIGHT - 1) / 500)),"
                        + "   B AS (SELECT A.POST_DATE, FLOOR((C.BORNWEIGHT - 1) / 500) AS WEIGHT,"
                        + "                COUNT(CASE WHEN A.VIP_NUM <> 0 THEN 1 ELSE 0 END) AS VIP_NUM,"
                        + "                COUNT(CASE WHEN A.BMP_NUM <> 0 THEN 1 ELSE 0 END) AS BMP_NUM,"
                        + "                COUNT(DISTINCT B.CASE_NO) AS NOWIN_COUNT "
                        + "           FROM INF_DAILY_REC A, ADM_DAILY_REC B, SUM_NEWARRIVALSIGN C "
                        + "          WHERE A.CASE_NO = C.CASE_NO "
                        + "            AND B.CASE_NO = C.CASE_NO "
                        + "            AND A.POST_DATE = B.POST_DATE "
                        + "            AND A.CASE_NO = B.CASE_NO "
                        + "            AND A.POST_DATE BETWEEN '#' AND '@' & "
                        + "       GROUP BY A.POST_DATE, FLOOR((C.BORNWEIGHT - 1) / 500)) "
                        + "SELECT A.POST_DATE, A.WEIGHT, A.NEWIN_COUNT, B.NOWIN_COUNT, B.VIP_NUM, B.BMP_NUM "
                        + "  FROM A, B        "
                        + " WHERE A.POST_DATE = B.POST_DATE(+)    "
                        + "   AND A.WEIGHT = B.WEIGHT(+)              "
                        + "ORDER BY A.POST_DATE, A.WEIGHT";
        sql = sql.replaceAll("#", parm.getValue("START_DATE").replaceAll("/", ""));
        sql = sql.replaceAll("@", parm.getValue("END_DATE").replaceAll("/", ""));
        if (parm.getData("DEPT_CODE") != null) {
            sql = sql.replaceAll("&", "AND A.DEPT_CODE = '" + parm.getValue("DEPT_CODE") + "'");
        } else {
            sql = sql.replaceAll("&", "");
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ��������Ը�Ⱦ����¼
     * @param parm
     * @return
     */
    public TParm selectCatheterMoniter(TParm parm) {// modify by wanglong 20140304
        String sql1 =
                "SELECT TO_CHAR( A.EFF_DATE, 'YYYY/MM/DD') AS STA_DATE, D.PAT_NAME, C.MR_NO,"
                        + "       CASE WHEN SUM(CASE WHEN B.ORD_SUPERVISION = '04' THEN 1 ELSE 0 END) > 0 THEN '04' "
                        + "       WHEN SUM(CASE WHEN B.ORD_SUPERVISION = '03' THEN 1 ELSE 0 END) > 0 THEN '03' "
                        + "       WHEN SUM(CASE WHEN B.ORD_SUPERVISION = '02' THEN 1 ELSE 0 END) > 0 THEN '02' ELSE '' END AS ORD_SUPERVISION,"
                        + "       A.EFF_DATE, A.DC_DATE, CEIL(A.DC_DATE - A.EFF_DATE) AS IO_DAYS, '' AS INF_FLG, '' AS SIGNATURE "
                        + "  FROM ODI_ORDER A, SYS_FEE B, ADM_INP C, SYS_PATINFO D "
                        + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                        + "   AND A.CASE_NO = C.CASE_NO "
                        + "   AND A.MR_NO = C.MR_NO "
                        + "   AND C.MR_NO = D.MR_NO "
                        + "   AND A.RX_KIND = 'UD' "
                        + "   AND A.NS_CHECK_CODE IS NOT NULL "
                        + "   AND C.DS_DATE BETWEEN TO_DATE('#', 'YYYYMMDD') AND TO_DATE('@235959', 'YYYYMMDDHH24MISS') "
                        + "   AND B.ORD_SUPERVISION IN ('02', '03', '04') & "
                        + "GROUP BY TO_CHAR(A.EFF_DATE,'YYYY/MM/DD'),C.DS_DEPT_CODE,C.DS_STATION_CODE,D.PAT_NAME,C.MR_NO,A.ORDER_CODE,A.EFF_DATE,A.DC_DATE "
                        + "ORDER BY STA_DATE, C.DS_DEPT_CODE, C.DS_STATION_CODE, C.MR_NO";
        sql1 = sql1.replaceFirst("#", parm.getValue("START_DATE").replaceAll("/", ""));
        sql1 = sql1.replaceFirst("@", parm.getValue("END_DATE").replaceAll("/", ""));
        if (parm.getData("DEPT_CODE") != null) {
            sql1 =
                    sql1.replaceFirst("&", "AND C.DS_DEPT_CODE = '" + parm.getValue("DEPT_CODE")
                            + "'");
        } else {
            sql1 = sql1.replaceFirst("&", "");
        }
        TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
        if (result1.getErrCode() < 0) {
            err("ERR:" + result1.getErrCode() + result1.getErrText() + result1.getErrName());
            return result1;
        }
        String sql2 =
                "WITH A AS (SELECT B.ORD_SUPERVISION, COUNT(DISTINCT A.CASE_NO) AS PAT_COUNT,"
                        + "                SUM(CEIL(A.DC_DATE - A.EFF_DATE)) AS TOT_DAYS,"
                        + "                ROUND(SUM(CEIL(A.DC_DATE - A.EFF_DATE)) / COUNT(DISTINCT A.CASE_NO), 1) AS AVG_DAYS,"
                        + "                '' AS INF_RATE, '' AS IO_RATE "
                        + "           FROM ODI_ORDER A, SYS_FEE B, ADM_INP C "
                        + "          WHERE A.ORDER_CODE = B.ORDER_CODE "
                        + "            AND A.CASE_NO = C.CASE_NO "
                        + "            AND A.MR_NO = C.MR_NO "
                        + "            AND A.RX_KIND = 'UD' "
                        + "            AND A.NS_CHECK_CODE IS NOT NULL "
                        + "            AND C.DS_DATE BETWEEN TO_DATE('#', 'YYYYMMDD') AND TO_DATE('@235959', 'YYYYMMDDHH24MISS') & "
                        + "            AND B.ORD_SUPERVISION IN ('02', '03', '04') "
                        + "       GROUP BY B.ORD_SUPERVISION),"
                        + "   B AS (SELECT B.ORD_SUPERVISION, A.CASE_NO, A.MR_NO,"
                        + "                SUM(DISTINCT CASE WHEN TRUNC(C.DS_DATE)-TRUNC(C.IN_DATE)<=1 THEN 1 "
                        + "                                  ELSE TRUNC(C.DS_DATE)-TRUNC(C.IN_DATE) END) AS OUT_STAY_DAYS "
                        + "           FROM ODI_ORDER A, SYS_FEE B, ADM_INP C "
                        + "          WHERE A.ORDER_CODE = B.ORDER_CODE "
                        + "            AND A.CASE_NO = C.CASE_NO "
                        + "            AND A.MR_NO = C.MR_NO "
                        + "            AND A.RX_KIND = 'UD' "
                        + "            AND A.NS_CHECK_CODE IS NOT NULL "
                        + "            AND C.DS_DATE BETWEEN TO_DATE('#', 'YYYYMMDD') AND TO_DATE('@235959', 'YYYYMMDDHH24MISS') & "
                        + "            AND B.ORD_SUPERVISION IN ('02', '03', '04') "
                        + "       GROUP BY B.ORD_SUPERVISION, A.CASE_NO, A.MR_NO) "
                        + "SELECT A.*, SUM(B.OUT_STAY_DAYS) OUT_STAY_DAYS "
                        + "  FROM A, B "
                        + " WHERE A.ORD_SUPERVISION = B.ORD_SUPERVISION "
                        + "GROUP BY A.ORD_SUPERVISION, A.PAT_COUNT, A.TOT_DAYS, A.AVG_DAYS, A.INF_RATE, A.IO_RATE "
                        + "ORDER BY A.ORD_SUPERVISION";
        sql2 = sql2.replaceAll("#", parm.getValue("START_DATE").replaceAll("/", ""));
        sql2 = sql2.replaceAll("@", parm.getValue("END_DATE").replaceAll("/", ""));
        if (parm.getData("DEPT_CODE") != null) {
            sql2 =
                    sql2.replaceAll("&", "AND C.DS_DEPT_CODE = '" + parm.getValue("DEPT_CODE")
                            + "'");
        } else {
            sql2 = sql2.replaceAll("&", "");
        }
        TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
        if (result2.getErrCode() < 0) {
            err("ERR:" + result2.getErrCode() + result2.getErrText() + result2.getErrName());
            return result2;
        }
        TParm result = new TParm();
        result.setData("M", result1.getData());
        result.setData("D", result2.getData());
        return result;
    }
}
