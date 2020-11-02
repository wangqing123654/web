package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import java.text.DecimalFormat;

/**
 * <p>Title: ҽԺ������Ժ����ʹ�ü�������̬����ͳ2��1��</p>
 *
 * <p>Description: ҽԺ������Ժ����ʹ�ü�������̬����ͳ2��1��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-8
 * @version 1.0
 */
public class STAOut_1Tool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static STAOut_1Tool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static STAOut_1Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAOut_1Tool();
        return instanceObject;
    }

    public STAOut_1Tool() {
        setModuleName("sta\\STAOut_1Module.x");
        onInit();
    }

    /**
     * ��ѯ ������������ܺ� ��Ϊ ��ͳ2��1������Դ
     * @param parm TParm �������  DATE_S:��ʼ����  DATE_E:��ֹ����
     * @return TParm
     *
     */
    public TParm selectSTA_DAILY_02_Sum(TParm parm) {
        String sql = STASQLTool.getInstance().getSTA_DAILY_02_Sum(parm.getValue(
            "DATE_S"), parm.getValue("DATE_E"),parm.getValue("REGION_CODE"));//====pangben modify 20110520 ����������
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ ���������ĳһ���ܺ� ��Ϊ ��ͳ2��1������Դ
     * @param parm TParm
     * @return TParm
     * ================pangben modify 20110520 ����������
     */
    public TParm getSTA_DAILY_02_DAY_SUM(String STA_DATE,String regionCode) {
        String sql = STASQLTool.getInstance().getSTA_DAILY_02_DAY_SUM(STA_DATE,regionCode);//==pangben modify 20110520 ����������
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ ����ͳ���м������ܺ� ��Ϊ ��ͳ2��1������Դ
     * @param STADATE String  ��ʽ yyyyMM
     * @return TParm
     */
    public TParm selectSTA_OPD_DAILY_Sum(TParm parm) {
        String sql = STASQLTool.getInstance().getSTA_OPD_DAILY_Sum(parm.
            getValue("DATE_S"), parm.getValue("DATE_E"),parm.getValue("REGION_CODE"));//=========pangben modify 20110520 ����������
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯSTA_OUT_01����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSTA_OUT_01(TParm parm) {
        TParm result = this.query("selectSTA_OUT_01", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����STA_OUT_01����Ϣ  ÿ��һ��
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_OUT_01(TParm parm, TConnection conn) {
        TParm result = this.update("insertSTA_OUT_01", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �޸�STA_OUT_01����Ϣ ����SQL����
     * @param sql String[]
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateSTA_OUT_01bySQL(String[] sql, TConnection conn) {
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().update(sql, conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ��STA_OUT_01����Ϣ
     * @param STADATE String
     * @return TParm
     * ==========pangben modify 20110520 ����������
     */
    public TParm deleteSTA_OUT_01(String STADATE, String regionCode,TConnection conn) {
        TParm parm = new TParm();
        parm.setData("STA_DATE", STADATE);
        //==========pangben modify 20110520 start
        parm.setData("REGION_CODE", regionCode);
        //==========pangben modify 20110520 stop
        TParm result = this.update("deleteSTA_OUT_01", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �޸�STA_OUT_01�е�����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateSTA_OUT_01(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if (parm.getData("SQL1") == null || parm.getData("SQL2") == null) {
            result.setErr( -1, "ȱ�ٲ���");
            return result;
        }
        String[] sql1 = (String[]) parm.getData("SQL1");
        String[] sql2 = (String[]) parm.getData("SQL2");
        result = updateSTA_OUT_01bySQL(sql1, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = updateSTA_OUT_01bySQL(sql2, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection conn) {
        TParm result = new TParm();
        String STA_DATE = parm.getValue("STA_DATE");
        //=============pangben modify 20110520
        String regionCode=parm.getValue("REGION_CODE");
        result = this.deleteSTA_OUT_01(STA_DATE,regionCode, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = this.insertSTA_OUT_01(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �������ڶβ�ѯ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm getDataByDate(TParm parm) {
        DecimalFormat df = new DecimalFormat("0.00"); //�趨Double���͸�ʽ
        //��ȡ �����м�� �������ݺ�
        TParm opd_Daily = STAOut_1Tool.getInstance().selectSTA_OPD_DAILY_Sum(
            parm);
        //��ȡ �������� �������ݺ�
        TParm daily02 = STAOut_1Tool.getInstance().selectSTA_DAILY_02_Sum(parm);
        //��ȡ�����ձ��������һ�������  Ϊ�˻�ȡ ʵ�в�������ֻ�ܰ�������ͳ�Ƶ�����
        TParm daily02_day = STAOut_1Tool.getInstance().getSTA_DAILY_02_DAY_SUM(
            parm.getValue("DATE_E"),parm.getValue("REGION_CODE"));//===pangben modify 20110520 ����������
        if (opd_Daily.getErrCode() < 0 || daily02.getErrCode() < 0 ||
            daily02_day.getErrCode() < 0) {
            return null;
        }
        TParm result = new TParm();
        result.addData("DATA_01", "1");
        result.addData("DATA_02", daily02.getInt("DATA_02", 0) +
                       daily02.getInt("DATA_03", 0) +
                       opd_Daily.getInt("OTHER_NUM", 0)
            ); //�ܼ�=����+����+����
        result.addData("DATA_03",
                       daily02.getInt("DATA_02", 0) +
                       daily02.getInt("DATA_03", 0)); //�ż����
        result.addData("DATA_04", daily02.getData("DATA_02", 0)); //�����˴�
        result.addData("DATA_05", daily02.getData("DATA_03", 0)); //�����
        result.addData("DATA_06", daily02.getData("DATA_04", 0)); //��������
        result.addData("DATA_07", daily02.getData("DATA_05", 0)); //�۲�����
        result.addData("DATA_08", daily02.getData("DATA_06", 0)); //�۲�����
        result.addData("DATA_09", opd_Daily.getData("HRM_NUM", 0)); //�����������
        result.addData("DATA_10", daily02.getData("DATA_08", 0)); //��Ժ����
        result.addData("DATA_11", daily02.getData("DATA_09", 0)); //��Ժ�����ܼ�
        result.addData("DATA_12", daily02.getData("DATA_10", 0)); //���в���������
        result.addData("DATA_13", daily02.getData("DATA_11", 0)); //����
        result.addData("DATA_14", daily02.getData("DATA_12", 0)); //��ת
        result.addData("DATA_15", daily02.getData("DATA_13", 0)); //δ��
        result.addData("DATA_16", daily02.getData("DATA_14", 0)); //����
        result.addData("DATA_17", ""); //�����˴�  ����
        result.addData("DATA_18", daily02_day.getData("DATA_17", 0)); //ʵ�в�����
        result.addData("DATA_19", daily02.getData("DATA_18", 0)); //ʵ�ʿ����ܴ�����
        result.addData("DATA_20", daily02_day.getData("DATA_19", 0)); //ƽ�����Ŵ�λ��
        result.addData("DATA_21", daily02.getData("DATA_20", 0)); //ʵ��ռ���ܴ�����
        result.addData("DATA_22", daily02.getData("DATA_21", 0)); //��Ժ��ռ���ܴ�����
        result.addData("DATA_23",daily02.getInt("DATA_10",0)==0?"":df.format(daily02.getDouble("DATA_11",0)/daily02.getDouble("DATA_10",0)*100));//������
        result.addData("DATA_24",daily02.getInt("DATA_10",0)==0?"":df.format(daily02.getDouble("DATA_12",0)/daily02.getDouble("DATA_10",0)*100));//��ת��
        result.addData("DATA_25",
                       daily02.getInt("DATA_10", 0) == 0 ? "" :
                       df.
                       format(daily02.getDouble("DATA_14", 0) /
                              daily02.getDouble("DATA_10", 0) * 100)); //������
        result.addData("DATA_26",
                       daily02_day.getInt("DATA_17", 0) == 0 ? "" :
                       df.format((daily02.getDouble("DATA_11", 0) +
                                  daily02.getDouble("DATA_12", 0) +
                                  daily02.getDouble("DATA_13", 0) +
                                  daily02.getDouble("DATA_14", 0) +
                                  daily02.getDouble("DATA_15", 0) +
                                  daily02.getDouble("DATA_15_1", 0)) /
                                 daily02_day.getDouble("DATA_17", 0))); //��λ��ת����
        result.addData("DATA_27", STATool.getInstance().getDaysOfMonth(parm.getValue("DATE_E").substring(0,6))); //����������

        if (daily02.getInt("DATA_09", 0) != 0)
            result.addData("DATA_29",
                           daily02.getInt("DATA_21", 0) /
                           daily02.getInt("DATA_09", 0)); //��Ժ��ƽ��סԺ��
        else
            result.addData("DATA_29", 0);
        //ÿ����ÿ���š��������֮��
        if (daily02.getDouble("DATA_02", 0) + daily02.getDouble("DATA_03", 0) !=
            0)
            result.addData("DATA_30",
                           StringTool.round(daily02.getDouble("DATA_03", 0) /
                                   (daily02.getDouble("DATA_02", 0) +
                                           daily02.getDouble("DATA_03", 0)), 2));
        else
            result.addData("DATA_30", "");
        //����ÿ���ż�����Ժ����  ���ĵ���ͬ
        if (daily02.getInt("DATA_02", 0) + daily02.getInt("DATA_03", 0) != 0) {
            double chu = daily02.getDouble("DATA_02", 0) +
                daily02.getDouble("DATA_03", 0);
            double DATA_31 = daily02.getDouble("DATA_08", 0) / (chu / 100);
            result.addData("DATA_31",df.format(DATA_31));
        }
        else
            result.addData("DATA_31", 0);
        //�ż������ռ�����
        double sumZc = opd_Daily.getDouble("OTHER_NUM", 0) +
            daily02.getDouble("DATA_02", 0) + daily02.getDouble("DATA_03", 0); //�����=����������
        if (sumZc != 0)
            result.addData("DATA_32",df.format((daily02.getDouble("DATA_02",0)+daily02.getDouble("DATA_03",0))/sumZc*100));

        else
            result.addData("DATA_32", "");
        //���ﲡ����
        if (daily02.getDouble("DATA_03", 0) != 0)
            result.addData("DATA_33",
                           df.format(daily02.getDouble("DATA_04", 0) /
                                     daily02.getDouble("DATA_03", 0) * 100));
        else
            result.addData("DATA_33", 0);
        //�۲��Ҳ�����
        if (daily02.getDouble("DATA_05", 0) != 0)
            result.addData("DATA_34",
                           df.format(daily02.getDouble("DATA_06", 0) /
                                     daily02.getDouble("DATA_05", 0) * 100));
        else
            result.addData("DATA_34", 0);
        //����ʹ����
        if (daily02.getDouble("DATA_18", 0) != 0) {
            result.addData("DATA_28",
                           df.format(daily02.getDouble("DATA_20", 0) /
                           daily02.getDouble("DATA_18", 0) * 100)); //����ʹ����
        }
        //��Ҫ����
        result.addData("STA_DATE", "");
        return result;
    }
}
