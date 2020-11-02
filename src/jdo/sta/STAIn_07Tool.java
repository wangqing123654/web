package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import java.text.DecimalFormat;
import com.dongyang.db.TConnection;

/**
 * <p>Title: STA_IN_07ҽԺ�����������</p>
 *
 * <p>Description: STA_IN_07ҽԺ�����������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-24
 * @version 1.0
 */
public class STAIn_07Tool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static STAIn_07Tool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static STAIn_07Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAIn_07Tool();
        return instanceObject;
    }

    public STAIn_07Tool() {
        setModuleName("sta\\STAIn_07Module.x");
        onInit();
    }
    /**
     * ָ��sql����ȡ����
     * @param type String  ָ�� sql���
     * @param DATE_S String  ��ʼ����
     * @param DATE_E String  ��ֹ����
     * @return TParm
     */
    public TParm selectDataT(String type,TParm parm){
        TParm result = this.query(type,parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��������(��4������Ϊ��λ)
     * @param sta_date String
     * @return TParm
     */
    public TParm selectData(TParm p) {
        DecimalFormat df = new DecimalFormat("0.00");
        TParm result = new TParm();
        if (p==null) {
            result.setErr( -1, "��������Ϊ��");
            return result;
        }
        String StartDate = p.getValue("DATE_S"); // ��ʼ����
		String EndDate = p.getValue("DATE_E"); // ��ֹ����
        //��Ժ�������˴���
        TParm selectDATA_01 = this.selectDataT("selectDATA_01",p);
        if (selectDATA_01.getErrCode() < 0) {
            err("ERR:" + selectDATA_01.getErrCode() + selectDATA_01.getErrText() +
                selectDATA_01.getErrName());
            return selectDATA_01;
        }
        //��Ժ����������
        TParm selectDATA_02 = this.selectDataT("selectDATA_02",p);
        if (selectDATA_02.getErrCode() < 0) {
            err("ERR:" + selectDATA_02.getErrCode() + selectDATA_02.getErrText() +
                selectDATA_02.getErrName());
            return selectDATA_02;
        }
        //����ռ���գ���ǰռ���գ�����ƽ��סԺ�գ���ǰƽ��סԺ��
        TParm selectDATA_03 = this.selectDataT("selectDATA_03",p);
        if (selectDATA_03.getErrCode() < 0) {
            err("ERR:" + selectDATA_03.getErrCode() + selectDATA_03.getErrText() +
                selectDATA_03.getErrName());
            return selectDATA_03;
        }
        //�޾�������������
        TParm selectDATA_07 = this.selectDataT("selectDATA_07",p);
        if (selectDATA_07.getErrCode() < 0) {
            err("ERR:" + selectDATA_07.getErrCode() + selectDATA_07.getErrText() +
                selectDATA_07.getErrName());
            return selectDATA_07;
        }
        //�޾��������׼�����������
        TParm selectDATA_08 = this.selectDataT("selectDATA_08",p);
        if (selectDATA_08.getErrCode() < 0) {
            err("ERR:" + selectDATA_08.getErrCode() + selectDATA_08.getErrText() +
                selectDATA_08.getErrName());
            return selectDATA_08;
        }
        //�޾�����,�пڻ�ŧ��
        TParm selectDATA_09 = this.selectDataT("selectDATA_09",p);
        if (selectDATA_09.getErrCode() < 0) {
            err("ERR:" + selectDATA_09.getErrCode() + selectDATA_09.getErrText() +
                selectDATA_09.getErrName());
            return selectDATA_09;
        }

        //��������������������
        TParm selectDATA_12 = this.selectDataT("selectDATA_12",p);
        if (selectDATA_12.getErrCode() < 0) {
            err("ERR:" + selectDATA_12.getErrCode() + selectDATA_12.getErrText() +
                selectDATA_12.getErrName());
            return selectDATA_12;
        }
        //��������������ʮ��������
        TParm selectDATA_13 = this.selectDataT("selectDATA_13",p);
        if (selectDATA_13.getErrCode() < 0) {
            err("ERR:" + selectDATA_13.getErrCode() + selectDATA_13.getErrText() +
                selectDATA_13.getErrName());
            return selectDATA_13;
        }

        //��������������������
        TParm selectDATA_14 = this.selectDataT("selectDATA_14",p);
        if (selectDATA_14.getErrCode() < 0) {
            err("ERR:" + selectDATA_14.getErrCode() + selectDATA_14.getErrText() +
                selectDATA_14.getErrName());
            return selectDATA_14;
        }
        //�����˴Σ�סԺ�о�
        TParm selectDATA_26 = this.selectDataT("selectDATA_26",p);
        if (selectDATA_26.getErrCode() < 0) {
            err("ERR:" + selectDATA_26.getErrCode() + selectDATA_26.getErrText() +
                selectDATA_26.getErrName());
            return selectDATA_26;
        }
        //�жϲ�ѯ�������Ƿ�������ţ����ָ���˲�����ôֻͳ�Ƹò��ŵ���Ϣ �������ź���
        TParm deptList = new TParm();
        if(p.getValue("OUT_DEPT").trim().length()<=0){
            //��ѯ�����ļ�����
            deptList = STATool.getInstance().getDeptByLevel(new String[]{"4"},p.getValue("REGION_CODE"));//==========pangben modify 20110525
        }
        else{
            //��ѯָ���Ĳ��� ����IPD_DEPT_CODE ��ѯ
            deptList = STADeptListTool.getInstance().selectNewIPDDeptCode(p.getValue("DEPT"),p.getValue("REGION_CODE"));//==========pangben modify 20110525
            if(deptList.getErrCode()<0){
                return deptList;
            }
        }
        String deptCode = ""; //��¼����CODE
        String IPD_DEPT_CODE = ""; //��¼סԺ����CODE
//        String STATION_CODE = "";//סԺ����CODE
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
            int DATA_03 = 0;
            int DATA_04 = 0;
            double DATA_05 = 0;
            double DATA_06 = 0;
            int DATA_07 = 0;
            int DATA_08 = 0;
            int DATA_09 = 0;
            double DATA_10 = 0;
            double DATA_11 = 0;
            int DATA_12 = 0;
            int DATA_13 = 0;
            int DATA_14 = 0;
            double DATA_15 = 0;
            double DATA_16 = 0;
            double DATA_17 = 0;
            int DATA_18 = 0;
            int DATA_19 = 0;
            double DATA_20 = 0;
            int DATA_21 = 0;
            int DATA_22 = 0;
            int DATA_23 = 0;
            int DATA_24 = 0;
            int DATA_25 = 0;
            int DATA_26 = 0;
            int DATA_27 = 0;
            String stationCode = IPD_DEPT_CODE; //��¼����CODE
            for(int j=0;j<selectDATA_01.getCount();j++){
                if(selectDATA_01.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_01.getValue("OUT_STATION", j))
                    ) {
                    DATA_01 = selectDATA_01.getInt("NUM",j);//��Ժ�������˴�
                }
            }
            for(int j=0;j<selectDATA_02.getCount();j++){
                if(selectDATA_02.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_02.getValue("OUT_STATION", j))
                    ) {
                    DATA_02 = selectDATA_02.getInt("NUM",j);//��Ժ����������
                }
            }
            for(int j=0;j<selectDATA_03.getCount();j++){
                if(selectDATA_03.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_03.getValue("OUT_STATION", j))
                    ) {
                    DATA_03 = selectDATA_03.getInt("DATA_03",j);//����ռ���ܴ�����
                    DATA_04 = selectDATA_03.getInt("DATA_04",j);//��ǰռ���ܴ�����
                }
            }
            for(int j=0;j<selectDATA_07.getCount();j++){
                if(selectDATA_07.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_07.getValue("OUT_STATION", j))
                    ) {
                    DATA_07 = selectDATA_07.getInt("NUM",j);//�޾�������������
                }
            }
            for(int j=0;j<selectDATA_08.getCount();j++){
                if(selectDATA_08.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_08.getValue("OUT_STATION", j))
                    ) {
                    DATA_08 = selectDATA_08.getInt("NUM",j);//�޾��������׼�����������
                }
            }
            for(int j=0;j<selectDATA_09.getCount();j++){
                if(selectDATA_09.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_09.getValue("OUT_STATION", j))
                    ) {
                    DATA_09 = selectDATA_09.getInt("NUM",j);//�޾�����,�пڻ�ŧ��
                }
            }
            for(int j=0;j<selectDATA_12.getCount();j++){
                if(selectDATA_12.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_12.getValue("OUT_STATION", j))
                    ) {
                    DATA_12 = selectDATA_12.getInt("NUM",j);//��������������������
                }
            }
            for(int j=0;j<selectDATA_13.getCount();j++){
                if(selectDATA_13.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_13.getValue("OUT_STATION", j))
                    ) {
                    DATA_13 = selectDATA_13.getInt("NUM",j);//��������������ʮ��������
                }
            }
            for(int j=0;j<selectDATA_14.getCount();j++){
                if(selectDATA_14.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_14.getValue("OUT_STATION", j))
                    ) {
                    DATA_14 = selectDATA_14.getInt("NUM",j);//��������������������
                }
            }
            for(int j=0;j<selectDATA_26.getCount();j++){
                if(selectDATA_26.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(selectDATA_26.getValue("OUT_STATION", j))
                    ) {
                    DATA_26 = selectDATA_26.getInt("NUM",j);//��������������������
                }
            }
            if(DATA_07!=0){
                DATA_10 = (double)DATA_08/ (double)DATA_07*100;//�޾�����,�׼�������
                DATA_11 = (double)DATA_09/ (double)DATA_07*100;//�޾�����,�пڻ�ŧ��
            }
            if(DATA_02!=0){
                DATA_15 =  (double)DATA_12/(double)DATA_02*100;//��������������������
                DATA_16 = (double)DATA_13/(double)DATA_02 *100;//��������������ʮ����������
                DATA_17 = (double)DATA_14/(double)DATA_02*100;//��������������������
            }
            DATA_21 = DATA_22 + DATA_25;//�����˴Σ��ϼ�
            DATA_22 = DATA_23+DATA_24;//�����˴Σ�����ϼ�
            if(DATA_02!=0){
                DATA_05 = (double)DATA_03/(double)DATA_02;//����ƽ��סԺ��
                DATA_06 = (double)DATA_04/(double)DATA_02;//��ǰƽ��סԺ��
            }
            //�������
            result.addData("STA_DATE", StartDate + "-" + EndDate);
            result.addData("DEPT_CODE", deptCode);
            result.addData("STATION_CODE", null==stationCode || stationCode.length()==0? IPD_DEPT_CODE:stationCode);//=======pangben modify 20110525
            result.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
            result.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
            result.addData("DATA_03", DATA_03 == 0 ? "" : DATA_03);
            result.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
            result.addData("DATA_05", DATA_05 == 0 ? "" : df.format(DATA_05));
            result.addData("DATA_06", DATA_06 == 0 ? "" : df.format(DATA_06));
            result.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
            result.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
            result.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
            result.addData("DATA_10", DATA_10 == 0 ? "" : df.format(DATA_10));
            result.addData("DATA_11", DATA_11 == 0 ? "" : df.format(DATA_11));
            result.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
            result.addData("DATA_13", DATA_13 == 0 ? "" : DATA_13);
            result.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
            result.addData("DATA_15", DATA_15 == 0 ? "" : df.format(DATA_15));
            result.addData("DATA_16", DATA_16 == 0 ? "" : df.format(DATA_16));
            result.addData("DATA_17", DATA_17 == 0 ? "" : df.format(DATA_17));
            result.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
            result.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
            result.addData("DATA_20", DATA_20 == 0 ? "" : df.format(DATA_20));
            result.addData("DATA_21", DATA_21 == 0 ? "" : DATA_21);
            result.addData("DATA_22", DATA_22 == 0 ? "" : DATA_22);
            result.addData("DATA_23", DATA_23 == 0 ? "" : DATA_23);
            result.addData("DATA_24", DATA_24 == 0 ? "" : DATA_24);
            result.addData("DATA_25", DATA_25 == 0 ? "" : DATA_25);
            result.addData("DATA_26", DATA_26 == 0 ? "" : DATA_26);
            result.addData("DATA_27", DATA_27 == 0 ? "" : DATA_27);
            result.addData("CONFIRM_FLG", "N");
            result.addData("CONFIRM_USER", "");
            result.addData("CONFIRM_DATE", "");
            result.addData("OPT_USER", "");
            result.addData("OPT_TERM", "");
        }
        return result;
    }
    /**
     * ɾ����STA_IN_07����
     * @param STA_DATE String
     * @return TParm
     * ===========pangben modify 20110525 ����������
     */
    public TParm deleteSTA_IN_07(String STA_DATE,String regionCode,TConnection conn){
        TParm parm = new TParm();
        parm.setData("STA_DATE",STA_DATE);
        //===========pangben modify 20110525
        parm.setData("REGION_CODE",regionCode);
        TParm result = this.update("deleteSTA_IN_07",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����STA_IN_07����
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_IN_07(TParm parm,TConnection conn){
        TParm result = this.update("insertSTA_IN_07",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����STA_IN_07����Ϣ
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
        //===========pangben modify 20110525
        String regionCode = parm.getValue("REGION_CODE", 0);
        if (STA_DATE.trim().length() <= 0) {
            result.setErr( -1, "STA_DATE����Ϊ��");
            return result;
        }
        result = this.deleteSTA_IN_07(STA_DATE, regionCode, conn); //ɾ�������ڵ�����=pangben modify 20110525
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
            insert.setData("DATA_21", parm.getValue("DATA_21", i));
            insert.setData("DATA_22", parm.getValue("DATA_22", i));
            insert.setData("DATA_23", parm.getValue("DATA_23", i));
            insert.setData("DATA_24", parm.getValue("DATA_24", i));
            insert.setData("DATA_25", parm.getValue("DATA_25", i));
            insert.setData("DATA_26", parm.getValue("DATA_26", i));
            insert.setData("DATA_27", parm.getValue("DATA_27", i));
            insert.setData("CONFIRM_FLG", parm.getValue("CONFIRM_FLG", i));
            insert.setData("CONFIRM_USER", parm.getValue("CONFIRM_USER", i));
            insert.setData("CONFIRM_DATE", parm.getValue("CONFIRM_DATE", i));
            insert.setData("OPT_USER", parm.getValue("OPT_USER", i));
            insert.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
            insert.setData("REGION_CODE", parm.getValue("REGION_CODE", i));//======pangben modify 20110525
            result = this.insertSTA_IN_07(insert, conn); //����������
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * ��ѯSTA_IN_07������
     * @param STA_DATE String
     * @return TParm
     * =============pangben modify 20110525 ����������
     */
    public TParm selectSTA_IN_07(String STA_DATE,String regionCode) {
        TParm parm = new TParm();
        parm.setData("STA_DATE", STA_DATE);
        parm.setData("REGION_CODE", regionCode);
        TParm result = this.query("selectSTA_IN_07", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �޸�STA_IN_07����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateSTA_IN_07(TParm parm, TConnection conn) {
        TParm  result = this.update("updateSTA_IN_07", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
