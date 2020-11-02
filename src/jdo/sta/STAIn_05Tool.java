package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import java.text.DecimalFormat;
import com.dongyang.db.TConnection;

/**
 * <p>Title: STA_IN_05��Ժ����Դ��������Ŀ����</p>
 *
 * <p>Description: STA_IN_05��Ժ����Դ��������Ŀ����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-23
 * @version 1.0
 */
public class STAIn_05Tool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static STAIn_05Tool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static STAIn_05Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAIn_05Tool();
        return instanceObject;
    }

    public STAIn_05Tool() {
        setModuleName("sta\\STAIn_05Module.x");
        onInit();
    }

    /**
     * ָ��sql����ȡ����
     * @param type String  ָ�� sql���
     * @param parm TParm ��������� DATE_S:��ʼ����;DATE_E:��ֹ����   ��ѡ����:DEPT_CODE:����CODE
     * @return TParm
     */
    public TParm selectDataT(String type, TParm parm) {
        TParm result = this.query(type, parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ������������������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSTA_DAILY_02(TParm parm) {
        TParm result = this.query("selectSTA_DAILY_02", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��������(��4������Ϊ��λ)
     * @param p TParm ��������� DATE_S:��ʼ����;DATE_E:��ֹ����   ��ѡ����:DEPT_CODE:����CODE
     * @return TParm
     */
    public TParm selectData(TParm p) {
        DecimalFormat df = new DecimalFormat("0.00");
        TParm result = new TParm();
        if (p == null) {
            result.setErr( -1, "��������Ϊ��");
            return result;
        }
        //��ѯ��Ժ����
        TParm selectDATA_01 = this.selectDataT("selectDATA_01", p);
        if (selectDATA_01.getErrCode() < 0) {
            err("ERR:" + selectDATA_01.getErrCode() + selectDATA_01.getErrText() +
                selectDATA_01.getErrName());
            return selectDATA_01;
        }
        //��ѯ���б�������
        TParm selectDATA_02 = this.selectDataT("selectDATA_02", p);
        if (selectDATA_02.getErrCode() < 0) {
            err("ERR:" + selectDATA_02.getErrCode() + selectDATA_02.getErrText() +
                selectDATA_02.getErrName());
            return selectDATA_02;
        }
        //��ѯ������������
        TParm selectDATA_04 = this.selectDataT("selectDATA_04", p);
        if (selectDATA_04.getErrCode() < 0) {
            err("ERR:" + selectDATA_04.getErrCode() + selectDATA_04.getErrText() +
                selectDATA_04.getErrName());
            return selectDATA_04;
        }
        //��ѯ��ʡ���г�Ժ����
        TParm selectDATA_06 = this.selectDataT("selectDATA_06", p);
        if (selectDATA_06.getErrCode() < 0) {
            err("ERR:" + selectDATA_06.getErrCode() + selectDATA_06.getErrText() +
                selectDATA_06.getErrName());
            return selectDATA_06;
        }
        //��ѯ��ʡ�еĳ�Ժ����
        TParm selectDATA_08 = this.selectDataT("selectDATA_08", p);
        if (selectDATA_08.getErrCode() < 0) {
            err("ERR:" + selectDATA_08.getErrCode() + selectDATA_08.getErrText() +
                selectDATA_08.getErrName());
            return selectDATA_08;
        }
        //��ѯ����ȷ������
        TParm selectDATA_10 = this.selectDataT("selectDATA_10", p);
        if (selectDATA_10.getErrCode() < 0) {
            err("ERR:" + selectDATA_10.getErrCode() + selectDATA_10.getErrText() +
                selectDATA_10.getErrName());
            return selectDATA_10;
        }
        //��Ժ24Сʱ����
        TParm selectDATA_12 = this.selectDataT("selectDATA_12", p);
        if (selectDATA_12.getErrCode() < 0) {
            err("ERR:" + selectDATA_12.getErrCode() + selectDATA_12.getErrText() +
                selectDATA_12.getErrName());
            return selectDATA_12;
        }
        //��ѯ����ȷ������
        TParm selectDATA_14 = this.selectDataT("selectDATA_14", p);
        if (selectDATA_14.getErrCode() < 0) {
            err("ERR:" + selectDATA_14.getErrCode() + selectDATA_14.getErrText() +
                selectDATA_14.getErrName());
            return selectDATA_14;
        }
        //�����������������ȳɹ�����
        TParm selectDATA_18 = this.selectDataT("selectDATA_18", p);
        if (selectDATA_18.getErrCode() < 0) {
            err("ERR:" + selectDATA_18.getErrCode() + selectDATA_18.getErrText() +
                selectDATA_18.getErrName());
            return selectDATA_18;
        }
        
        //===========add by wangbin 20140708 start
        //��ѯ�⼮����
        TParm selectDATA_21 = this.selectDataT("selectDATA_21", p);
        if (selectDATA_21.getErrCode() < 0) {
            err("ERR:" + selectDATA_21.getErrCode() + selectDATA_21.getErrText() +
            		selectDATA_21.getErrName());
            return selectDATA_21;
        }
        //===========add by wangbin 20140708 end
        
        //�жϲ�ѯ�������Ƿ�������ţ����ָ���˲�����ôֻͳ�Ƹò��ŵ���Ϣ �������ź���
        TParm deptList = new TParm();
        String dept_code = "";
        if (p.getValue("DEPT_CODE").trim().length() <= 0) {
            //��ѯ�����ļ�����
            deptList = STATool.getInstance().getDeptByLevel(new String[] {"4"},p.getValue("REGION_CODE"));//=======pangben modify 20110524
        }else {
            //��ѯָ���Ĳ��� ����IPD_DEPT_CODE ��ѯ
            deptList = STADeptListTool.getInstance().selectNewIPDDeptCode(p.
                getValue("DEPT"),p.getValue("REGION_CODE"));//=======pangben modify 20110524
            if (deptList.getErrCode() < 0) {
                return deptList;
            }
            dept_code = deptList.getValue("DEPT_CODE", 0); //��ȡ�м�����CODE
        }
        //������������������  ��ѯSTA_DAILY_02���DEPT_CODE��������ѯ��ͬ ����������ϲ���
        TParm p02 = new TParm();
        p02.setData("DATE_S", p.getValue("DATE_S"));
        p02.setData("DATE_E", p.getValue("DATE_E"));
        if (dept_code.length() > 0)
            p02.setData("DEPT_CODE", dept_code);
        //===========pangben modify 20110525 �����������
        p02.setData("REGION_CODE",p.getValue("REGION_CODE"));
        TParm selectSTA_DAILY_02 = this.selectSTA_DAILY_02(p02);
        if (selectSTA_DAILY_02.getErrCode() < 0) {
            err("ERR:" + selectSTA_DAILY_02.getErrCode() +
                selectSTA_DAILY_02.getErrText() +
                selectSTA_DAILY_02.getErrName());
            return selectSTA_DAILY_02;
        }
        String StartDate = p.getValue("DATE_S"); // ��ʼ����
		String EndDate = p.getValue("DATE_E"); // ��ֹ����
        String deptCode = ""; //��¼����CODE
        String IPD_DEPT_CODE = ""; //��¼סԺ����CODE
//        String STATION_CODE = "";
        int TDATA_01 = 0;
        int TDATA_02 = 0;
        double TDATA_03 = 0;
        int TDATA_04 = 0;
        double TDATA_05 = 0;
        int TDATA_06 = 0;
        double TDATA_07 = 0;
        int TDATA_08 = 0;
        double TDATA_09 = 0;
        int TDATA_10 = 0;
        double TDATA_11 = 0;
        int TDATA_12 = 0;
        double TDATA_13 = 0;
        int TDATA_14 = 0;
        double TDATA_15 = 0;
        int TDATA_16 = 0;
        double TDATA_17 = 0;
        int TDATA_18 = 0;
        int TDATA_19 = 0;
        double TDATA_20 = 0;
        for (int i = 0; i < deptList.getCount(); i++) {
            deptCode = deptList.getValue("DEPT_CODE", i);
            IPD_DEPT_CODE = deptList.getValue("IPD_DEPT_CODE", i); //סԺ����CODE
//            STATION_CODE = deptList.getValue("STATION_CODE", i); //סԺ����CODE
            if (IPD_DEPT_CODE.trim().length() <= 0) { //�ж��Ƿ���סԺ���� ������Ǿͽ�����һ��ѭ��
                continue;
            }
            //��������ֶεı���
            int DATA_01 = 0;
            int DATA_02 = 0;
            double DATA_03 = 0;
            int DATA_04 = 0;
            double DATA_05 = 0;
            int DATA_06 = 0;
            double DATA_07 = 0;
            int DATA_08 = 0;
            double DATA_09 = 0;
            int DATA_10 = 0;
            double DATA_11 = 0;
            int DATA_12 = 0;
            double DATA_13 = 0;
            int DATA_14 = 0;
            double DATA_15 = 0;
            int DATA_16 = 0;
            double DATA_17 = 0;
            int DATA_18 = 0;
            int DATA_19 = 0;
            double DATA_20 = 0;
            //�⼮����
            int DATA_21 = 0;
            String stationCode = IPD_DEPT_CODE; //��¼����CODE
            for (int j = 0; j < selectDATA_01.getCount(); j++) {
                if (selectDATA_01.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_01.getValue("OUT_STATION", j))
                		) {
                    DATA_01 = selectDATA_01.getInt("NUM", j); //��Ժ����
                }
            }
            for (int j = 0; j < selectDATA_02.getCount(); j++) {
                if (selectDATA_02.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_02.getValue("OUT_STATION", j))
                    ) {
                    DATA_02 = selectDATA_02.getInt("NUM", j); //���б�������Ժ����
                }
            }
            for (int j = 0; j < selectDATA_04.getCount(); j++) {
                if (selectDATA_04.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_04.getValue("OUT_STATION", j))
                    ) {
                    DATA_04 = selectDATA_04.getInt("NUM", j); //������������Ժ����
                }
            }
            for (int j = 0; j < selectDATA_06.getCount(); j++) {
                if (selectDATA_06.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_06.getValue("OUT_STATION", j))
                    ) {
                    DATA_06 = selectDATA_06.getInt("NUM", j); //��ʡ���У���Ժ����
                }
            }
            for (int j = 0; j < selectDATA_08.getCount(); j++) {
                if (selectDATA_08.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_08.getValue("OUT_STATION", j))
                    ) {
                    DATA_08 = selectDATA_08.getInt("NUM", j); //��ʡ��
                }
            }
            for (int j = 0; j < selectDATA_10.getCount(); j++) {
                if (selectDATA_10.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_10.getValue("OUT_STATION", j))
                    ) {
                    DATA_10 = selectDATA_10.getInt("NUM", j); //����ȷ��,����
                }
            }
            for (int j = 0; j < selectDATA_12.getCount(); j++) {
                if (selectDATA_12.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_12.getValue("OUT_STATION", j))
                    ) {
                    DATA_12 = selectDATA_12.getInt("NUM", j); //24Сʱ��Ժ��������
                }
            }
            for (int j = 0; j < selectDATA_14.getCount(); j++) {
                if (selectDATA_14.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_14.getValue("OUT_STATION", j))
                    ) {
                    DATA_14 = selectDATA_14.getInt("NUM", j); //48Сʱ��Ժ��������
                }
            }
            for (int j = 0; j < selectSTA_DAILY_02.getCount(); j++) {
                if (selectSTA_DAILY_02.getValue("DEPT_CODE",
                                                j).equals(deptCode)) { //ע�� �����������м�� Ҫ���м䲿��code
                    DATA_16 = selectSTA_DAILY_02.getInt("DATA_30", j); //������������
                    DATA_17 = selectSTA_DAILY_02.getDouble("DATA_41", j); //������
                }
            }
            for (int j = 0; j < selectDATA_18.getCount(); j++) {
                if (selectDATA_18.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_18.getValue("OUT_STATION", j))
                    ) {
                    DATA_18 = selectDATA_18.getInt("GET_TIMES", j); //����������
                    DATA_19 = selectDATA_18.getInt("SUCCESS_TIMES", j); //�ɹ��˴�
                }
            }
            if (DATA_18 != 0) {
                DATA_20 = (double) DATA_19 / (double) DATA_18 * 100; //�ɹ���
            }
            if (DATA_01 != 0) {
                DATA_03 = (double) DATA_02 / (double) DATA_01 * 100; //���б������ٷֱ�
                DATA_05 = (double) DATA_04 / (double) DATA_01 * 100; //�����������ٷֱ�
                DATA_07 = (double) DATA_06 / (double) DATA_01 * 100; //��ʡ���У��ٷֱ�
                DATA_09 = (double) DATA_08 / (double) DATA_01 * 100; //��ʡ��
                DATA_11 = (double) DATA_10 / (double) DATA_01 * 100; //����ȷ����
                DATA_13 = (double) DATA_12 / (double) DATA_01 * 100; //24Сʱ��Ժ������
                DATA_15 = (double) DATA_14 / (double) DATA_01 * 100; //48Сʱ��Ժ������
            }
            
            //===========add by wangbin 20140708 start
			for (int j = 0; j < selectDATA_21.getCount(); j++) {
				if (selectDATA_21.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)) {
					DATA_21 = selectDATA_21.getInt("NUM", j); // �⼮����
				}
			}
         	//===========add by wangbin 20140708 start

            //�������
            result.addData("STA_DATE", StartDate + "-" + EndDate);
            result.addData("DEPT_CODE", deptCode);
            result.addData("STATION_CODE", null==stationCode || stationCode.length()==0? IPD_DEPT_CODE:stationCode);//==============pangben modify 20110525
            result.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
            result.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
            result.addData("DATA_03", DATA_03 == 0 ? "" : df.format(DATA_03));
            result.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
            result.addData("DATA_05", DATA_05 == 0 ? "" : df.format(DATA_05));
            result.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
            result.addData("DATA_07", DATA_07 == 0 ? "" : df.format(DATA_07));
            result.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
            result.addData("DATA_09", DATA_09 == 0 ? "" : df.format(DATA_09));
            result.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
            result.addData("DATA_11", DATA_11 == 0 ? "" : df.format(DATA_11));
            result.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
            result.addData("DATA_13", DATA_13 == 0 ? "" : df.format(DATA_13));
            result.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
            result.addData("DATA_15", DATA_15 == 0 ? "" : df.format(DATA_15));
            result.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
            result.addData("DATA_17", DATA_17 == 0 ? "" : df.format(DATA_17));
            result.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
            result.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
            result.addData("DATA_20", DATA_20 == 0 ? "" : df.format(DATA_20));
            //===========add by wangbin 20140708 start
            result.addData("DATA_21", DATA_21 == 0 ? "" : DATA_21);
            //===========add by wangbin 20140708 end
            result.addData("CONFIRM_FLG", "N");
            result.addData("CONFIRM_USER", "");
            result.addData("CONFIRM_DATE", "");
            result.addData("OPT_USER", "");
            result.addData("OPT_TERM", "");
        }
        return result;
    }

    /**
     * ɾ����STA_IN_05����
     * @param STA_DATE String
     * @return TParm
     * ==============pangben modify 20110525 ����������
     */
    public TParm deleteSTA_IN_05(String STA_DATE,String regionCode, TConnection conn) {
        TParm parm = new TParm();
        parm.setData("STA_DATE", STA_DATE);
        parm.setData("REGION_CODE", regionCode);
        TParm result = this.update("deleteSTA_IN_05", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����STA_IN_05����
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_IN_05(TParm parm, TConnection conn) {
        TParm result = this.update("insertSTA_IN_05", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �޸�STA_IN_05����
     * @param parm TParm
     * @return TParm
     * ============pangben modify 20110526
     */
    public TParm updateSTA_IN_05(TParm parm, TConnection conn) {
        TParm  result = this.update("updateSTA_IN_05", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����STA_IN_05����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if (parm.getCount("STA_DATE") <= 0) {
            result.setErr( -1, "û�пɲ��������");
            return result;
        }
        String STA_DATE = parm.getValue("STA_DATE", 0);
        if (STA_DATE.trim().length() <= 0) {
            result.setErr( -1, "STA_DATE����Ϊ��");
            return result;
        }
        //============pangben modify 20110525
        result = this.deleteSTA_IN_05(STA_DATE,parm.getValue("REGION_CODE",0), conn); //ɾ�������ڵ�����
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        TParm insert = null;
        for (int i = 0; i < parm.getCount("STA_DATE"); i++) {
            insert = new TParm();
            insert.setData("STA_DATE", parm.getValue("STA_DATE", i));
            insert.setData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
            insert.setData("STATION_CODE", parm.getValue("STATION_CODE", i));
            insert.setData("DATA_01", parm.getValue("DATA_01", i));
            insert.setData("DATA_02", parm.getValue("DATA_02", i));
            insert.setData("DATA_03", parm.getValue("DATA_03", i));
            insert.setData("DATA_04", parm.getValue("DATA_04", i));
            insert.setData("DATA_05", parm.getValue("DATA_05", i));
            insert.setData("DATA_06", parm.getValue("DATA_06", i));
            insert.setData("DATA_07", parm.getValue("DATA_07", i));
            insert.setData("DATA_08", parm.getValue("DATA_08", i));
            insert.setData("DATA_09", parm.getValue("DATA_09", i));
            insert.setData("DATA_10", parm.getValue("DATA_10", i));
            insert.setData("DATA_11", parm.getValue("DATA_11", i));
            insert.setData("DATA_12", parm.getValue("DATA_12", i));
            insert.setData("DATA_13", parm.getValue("DATA_13", i));
            insert.setData("DATA_14", parm.getValue("DATA_14", i));
            insert.setData("DATA_15", parm.getValue("DATA_15", i));
            insert.setData("DATA_16", parm.getValue("DATA_16", i));
            insert.setData("DATA_17", parm.getValue("DATA_17", i));
            insert.setData("DATA_18", parm.getValue("DATA_18", i));
            insert.setData("DATA_19", parm.getValue("DATA_19", i));
            insert.setData("DATA_20", parm.getValue("DATA_20", i));
            //============add by wangbin 20140709 start
            // �⼮����
            insert.setData("DATA_21", parm.getValue("DATA_21", i));
            //============add by wangbin 20140709 end
            insert.setData("CONFIRM_FLG", parm.getValue("CONFIRM_FLG", i));
            insert.setData("CONFIRM_USER", parm.getValue("CONFIRM_USER", i));
            insert.setData("CONFIRM_DATE", parm.getValue("CONFIRM_DATE", i));
            insert.setData("OPT_USER", parm.getValue("OPT_USER", i));
            insert.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
            //============pangben modify 20110525 start
            insert.setData("REGION_CODE", parm.getValue("REGION_CODE", i));
            //============pangben modify 20110525 stop
            result = this.insertSTA_IN_05(insert, conn); //����������
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;
    }
    /**
     * �޸�sta_in_05���е�����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * =============pangben modify 20110526
     */
    public TParm updateData(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = updateSTA_IN_05(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯSTA_IN_05������
     * @param STA_DATE String
     * @return TParm
     * ==========pangben modify 20110525 ����������
     */
    public TParm selectSTA_IN_05(String STA_DATE,String regionCode) {
        TParm parm = new TParm();
        parm.setData("STA_DATE", STA_DATE);
        //===========pangben modify 20110525 start
        if (null != regionCode && regionCode.length() > 0)
            parm.setData("REGION_CODE", regionCode);
        //===========pangben modify 20110525 stop
        TParm result = this.query("selectSTA_IN_05", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
