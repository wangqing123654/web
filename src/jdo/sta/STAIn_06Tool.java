package jdo.sta;

import java.text.DecimalFormat;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: STA_IN_06Σ��������Ч��������</p>
 *
 * <p>Description: STA_IN_06Σ��������Ч��������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-22
 * @version 1.0
 */
public class STAIn_06Tool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static STAIn_06Tool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static STAIn_06Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAIn_06Tool();
        return instanceObject;
    }

    public STAIn_06Tool() {
        setModuleName("sta\\STAIn_06Module.x");
        onInit();
    }

    /**
     * ��ѯ��ҳ��ͼ ��ѯΣ������
     * @param parm TParm
     * @param IN_CONDITION String
     * @return TParm
     */
    public TParm selectMRO_RECORD(TParm parm, String IN_CONDITION) {
        parm.setData("IN_CONDITION", IN_CONDITION);
        TParm result = this.query("selectMRO_RECORD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��Ժ����
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOutNum(TParm parm){
        TParm result = this.query("selectOutNum", parm);
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
    public TParm selectData(TParm parm) {
        DecimalFormat df = new DecimalFormat("0.00");
        TParm result = new TParm();
        if (parm==null) {
            result.setErr( -1, "��������Ϊ��");
            return result;
        }
        String StartDate = parm.getValue("DATE_S"); // ��ʼ����
		String EndDate = parm.getValue("DATE_E"); // ��ֹ����
        //��Σ����
        TParm IN_CONDITION_1 = this.selectMRO_RECORD(parm,"1");
        if (IN_CONDITION_1.getErrCode() < 0) {
            err("ERR:" + IN_CONDITION_1.getErrCode() +
                IN_CONDITION_1.getErrText() +
                IN_CONDITION_1.getErrName());
            return IN_CONDITION_1;
        }
        //��������
        TParm IN_CONDITION_2 = this.selectMRO_RECORD(parm,"2");
        if (IN_CONDITION_2.getErrCode() < 0) {
            err("ERR:" + IN_CONDITION_2.getErrCode() +
                IN_CONDITION_2.getErrText() +
                IN_CONDITION_2.getErrName());
            return IN_CONDITION_2;
        }
        //��ѯ��Ժ����
        TParm outNum = this.selectOutNum(parm);
        if (outNum.getErrCode() < 0) {
            err("ERR:" + outNum.getErrCode() +
                outNum.getErrText() +
                outNum.getErrName());
            return outNum;
        }

        //�жϲ�ѯ�������Ƿ�������ţ����ָ���˲�����ôֻͳ�Ƹò��ŵ���Ϣ �������ź���
        TParm deptList = new TParm();
        if(parm.getValue("DEPT_CODE").trim().length()<=0){
            //��ѯ�����ļ�����
            deptList = STATool.getInstance().getDeptByLevel(new String[]{"4"},parm.getValue("REGION_CODE"));//======pangben modify 20110525
        }
        else{
            //��ѯָ���Ĳ��� ����IPD_DEPT_CODE ��ѯ
            deptList = STADeptListTool.getInstance().selectNewIPDDeptCode(parm.getValue("DEPT"),parm.getValue("REGION_CODE"));//======pangben modify 20110525
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
            double DATA_02 = 0;
            double DATA_03 = 0;
            double DATA_04 = 0;
            int DATA_05 = 0;
            int DATA_06 = 0;
            int DATA_07 = 0;
            int DATA_08 = 0;
            int DATA_09 = 0;
            double DATA_10 = 0;
            double DATA_11 = 0;
            double DATA_12 = 0;
            double DATA_13 = 0;
            double DATA_14 = 0;
            int DATA_15 = 0;
            int DATA_16 = 0;
            int DATA_17 = 0;
            int DATA_18 = 0;
            int DATA_19 = 0;
            double DATA_20 = 0;
            double DATA_21 = 0;
            double DATA_22 = 0;
            double DATA_23 = 0;
            double DATA_24 = 0;
            int out_num = 0;//��¼��Ժ��������
            String stationCode = IPD_DEPT_CODE; //��¼����CODE
            int inDays1 = 0;//��¼סԺ����
            int inDays2 = 0;//��¼סԺ����
            //ѭ����Σ������Ϣ
            for (int j = 0; j < IN_CONDITION_1.getCount(); j++) {
                //�����Ժ����CODE����סԺ����CODE �����
                if (IN_CONDITION_1.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(IN_CONDITION_1.getValue("OUT_STATION", j))
                    ) {
                    String STATUS = IN_CONDITION_1.getValue("CODE1_STATUS",j);//��Ժ���״̬
                    if(STATUS.equals("1")){//����
                        DATA_06 = IN_CONDITION_1.getInt("NUM", j);
                        DATA_05 += DATA_06;//��Σ�ϼ��ۼ�
                        inDays1 += IN_CONDITION_1.getInt("DAYS", j);//סԺ����
                    }else if(STATUS.equals("2")){//��ת
                        DATA_07 = IN_CONDITION_1.getInt("NUM", j);
                        DATA_05 +=DATA_07;//��Σ�ϼ��ۼ�
                        inDays1 += IN_CONDITION_1.getInt("DAYS", j);//סԺ����
                    }else if(STATUS.equals("3")){//δ��
                        DATA_08 = IN_CONDITION_1.getInt("NUM", j);
                        DATA_05 += DATA_08;//��Σ�ϼ��ۼ�
                        inDays1 += IN_CONDITION_1.getInt("DAYS", j);//סԺ����
                    }else if(STATUS.equals("4")){//����
                        DATA_09 = IN_CONDITION_1.getInt("NUM", j);
                        DATA_05 += DATA_09;//��Σ�ϼ��ۼ�
                        inDays1 += IN_CONDITION_1.getInt("DAYS", j);//סԺ����
                    }
                }
            }
            if(DATA_05!=0){
                DATA_10 = (double) DATA_06 / (double) DATA_05 * 100; //������
                DATA_11 = (double) DATA_07 / (double) DATA_05 * 100; //��ת��
                DATA_12 = (double) DATA_08 / (double) DATA_05 * 100; //δ����
                DATA_13 = (double) DATA_09 / (double) DATA_05 * 100; //������
                DATA_14 = (double) inDays1 / (double) DATA_05;//ƽ��סԺ����
            }
            //ѭ������������Ϣ
            for (int j = 0; j < IN_CONDITION_2.getCount(); j++) {
                //�����Ժ����CODE����סԺ����CODE �����
                if (IN_CONDITION_2.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)
//                    &&STATION_CODE.equals(IN_CONDITION_2.getValue("OUT_STATION", j))
                    ) {
                    String STATUS = IN_CONDITION_2.getValue("CODE1_STATUS",j);//��Ժ���״̬
                    if(STATUS.equals("1")){//����
                        DATA_16 = IN_CONDITION_2.getInt("NUM", j);
                        DATA_15 += DATA_16;//�����ϼ��ۼ�
                        inDays2 += IN_CONDITION_2.getInt("DAYS", j);//סԺ����
                    }else if(STATUS.equals("2")){//��ת
                        DATA_17 = IN_CONDITION_2.getInt("NUM", j);
                        DATA_15 +=DATA_17;//�����ϼ��ۼ�
                        inDays2 += IN_CONDITION_2.getInt("DAYS", j);//סԺ����
                    }else if(STATUS.equals("3")){//δ��
                        DATA_18 = IN_CONDITION_2.getInt("NUM", j);
                        DATA_15 += DATA_18;//�����ϼ��ۼ�
                        inDays2 += IN_CONDITION_2.getInt("DAYS", j);//סԺ����
                    }else if(STATUS.equals("4")){//����
                        DATA_19 = IN_CONDITION_2.getInt("NUM", j);
                        DATA_15 += DATA_19;//�����ϼ��ۼ�
                        inDays2 += IN_CONDITION_2.getInt("DAYS", j);//סԺ����
                    }
                }
            }
            if(DATA_15!=0){
                DATA_20 = (double) DATA_16 / (double) DATA_15 * 100; //������
                DATA_21 = (double) DATA_17 / (double) DATA_15 * 100; //��ת��
                DATA_22 = (double) DATA_18 / (double) DATA_15 * 100; //δ����
                DATA_23 = (double) DATA_19 / (double) DATA_15 * 100; //������
                DATA_24 = (double) inDays2 / (double) DATA_15;//ƽ��סԺ����
            }
            int out = 0;
            //ѭ��ÿ���ҳ�Ժ����
            for(int j=0;j<outNum.getCount();j++){
                //�����Ժ����CODE����סԺ����CODE �����
                if (outNum.getValue("OUT_DEPT", j).equals(IPD_DEPT_CODE)) {
                    out = outNum.getInt("NUM",j);
                    out_num += out;
                }
            }
            DATA_01 = DATA_05 + DATA_15;//Σ�����˳�Ժ����
            if(out_num!=0){
                DATA_02 = (double)(DATA_05 + DATA_15)/(double)out_num*100;//Σ������ռ�ٷֱ�
                DATA_03 = (double)DATA_05/(double)out_num*100;//��Σ����ռ��Ժ���˰ٷֱ�
                DATA_04 = (double)DATA_15/(double)out_num*100;//��������ռ��Ժ���˰ٷֱ�
            }
            //�������
            result.addData("STA_DATE", StartDate + "-" + EndDate);
            result.addData("DEPT_CODE", deptCode);
            result.addData("STATION_CODE", null==stationCode || stationCode.length()==0? IPD_DEPT_CODE:stationCode);//===========pangben modify 20110525
            result.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
            result.addData("DATA_02", DATA_02 == 0 ? "" :df.format(DATA_02));
            result.addData("DATA_03", DATA_03 == 0 ? "" : df.format(DATA_03));
            result.addData("DATA_04", DATA_04 == 0 ? "" : df.format(DATA_04));
            result.addData("DATA_05", DATA_05 == 0 ? "" : DATA_05);
            result.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
            result.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
            result.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
            result.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
            result.addData("DATA_10", DATA_10 == 0 ? "" : df.format(DATA_10));
            result.addData("DATA_11", DATA_11 == 0 ? "" : df.format(DATA_11));
            result.addData("DATA_12", DATA_12 == 0 ? "" : df.format(DATA_12));
            result.addData("DATA_13", DATA_13 == 0 ? "" : df.format(DATA_13));
            result.addData("DATA_14", DATA_14 == 0 ? "" : df.format(DATA_14));
            result.addData("DATA_15", DATA_15 == 0 ? "" : DATA_15);
            result.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
            result.addData("DATA_17", DATA_17 == 0 ? "" : DATA_17);
            result.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
            result.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
            result.addData("DATA_20", DATA_20 == 0 ? "" : df.format(DATA_20));
            result.addData("DATA_21", DATA_21 == 0 ? "" : df.format(DATA_21));
            result.addData("DATA_22", DATA_22 == 0 ? "" : df.format(DATA_22));
            result.addData("DATA_23", DATA_23 == 0 ? "" : df.format(DATA_23));
            result.addData("DATA_24", DATA_24 == 0 ? "" : df.format(DATA_24));
            result.addData("CONFIRM_FLG", "N");
            result.addData("CONFIRM_USER", "");
            result.addData("CONFIRM_DATE", "");
            result.addData("OPT_USER", "");
            result.addData("OPT_TERM", "");
            //--------------------ǰ̨�������----------------------------
            result.addData("DATA_25", out_num);
            result.addData("DATA_26", inDays1);
            result.addData("DATA_27", inDays2);
        }
        return result;
    }
    /**
     * ɾ����STA_IN_06����
     * @param STA_DATE String
     * @return TParm
     * =============pangben modify 201110525 ����������
     */
    public TParm deleteSTA_IN_06(String STA_DATE,String regionCode,TConnection conn){
        TParm parm = new TParm();
        parm.setData("STA_DATE",STA_DATE);
        //============pangben modify 201110525
        parm.setData("REGION_CODE",regionCode);
        TParm result = this.update("deleteSTA_IN_06",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����STA_IN_06����
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_IN_06(TParm parm,TConnection conn){
        TParm result = this.update("insertSTA_IN_06",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����STA_IN_06����Ϣ
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
        //============pangben modify 20110525 start
         String regionCode = parm.getValue("REGION_CODE", 0);
        if (STA_DATE.trim().length() <= 0) {
            result.setErr( -1, "STA_DATE����Ϊ��");
            return result;
        }
        //============pangben modify 20110525 ����������
        result = this.deleteSTA_IN_06(STA_DATE,regionCode, conn); //ɾ�������ڵ�����
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
            //===========pangben modify 20110525 start
            insert.setData("REGION_CODE", parm.getValue("REGION_CODE", i));
            //===========pangben modify 20110525 stop
            result = this.insertSTA_IN_06(insert, conn); //����������
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;
    }
    /**
     * ��ѯSTA_IN_06������
     * @param STA_DATE String
     * @return TParm
     * =================pangben modify 20110525
     */
    public TParm selectSTA_IN_06(String STA_DATE,String regionCode) {
        TParm parm = new TParm();
        parm.setData("STA_DATE", STA_DATE);
        parm.setData("REGION_CODE", regionCode);
        TParm result = this.query("selectSTA_IN_06", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �޸�STA_IN_06����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateSTA_IN_06(TParm parm, TConnection conn) {
        TParm  result = this.update("updateSTA_IN_06", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
