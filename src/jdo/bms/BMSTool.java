package jdo.bms;

import jdo.sys.PatTool;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TNull;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: Ѫ�����
 * </p>
 *
 * <p>
 * Description: Ѫ�����
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class BMSTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static BMSTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return BMSBloodTool
     */
    public static BMSTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BMSTool();
        return instanceObject;
    }

    /**
     * ѪҺ���(����)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onBMSBloodInInsert(TParm parm, TConnection conn) {
        // ����ѪҺ��Ϣ
        TParm result = BMSBloodTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // ����ѪҺ���
        TParm inparm = BMSBldStockTool.getInstance().onQuery(parm);
        if (inparm.getCount("BLD_CODE") > 0) {
            parm.setData("SAFE_QTY", inparm.getData("SAFE_QTY", 0));
            parm.setData("CURR_QTY", inparm.getDouble("CURR_QTY", 0) + 1);
            parm.setData("ACC_QTY", inparm.getDouble("ACC_QTY", 0) + 1);
            parm.setData("SAFE_VOL", inparm.getData("SAFE_VOL", 0));
            parm.setData("TOT_VOL",
                         inparm.getDouble("TOT_VOL", 0) +
                         parm.getDouble("BLOOD_VOL"));
            parm.setData("ACC_VOL",
                         inparm.getDouble("ACC_VOL", 0) +
                         parm.getDouble("BLOOD_VOL"));

            result = BMSBldStockTool.getInstance().onUpdate(parm, conn);
        }
        else {
            parm.setData("SAFE_QTY", 0);
            parm.setData("CURR_QTY", 1);
            parm.setData("ACC_QTY", 1);
            parm.setData("SAFE_VOL", 0);
            parm.setData("TOT_VOL", parm.getData("BLOOD_VOL"));
            parm.setData("ACC_VOL", parm.getData("BLOOD_VOL"));

            result = BMSBldStockTool.getInstance().onInsert(parm, conn);
        }
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ��ѪҺ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onBMSBloodInDelete(TParm parm, TConnection conn) {
        // ɾ��ѪҺ��Ϣ
        TParm result = BMSBloodTool.getInstance().onDelete(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // ����ѪҺ���
        TParm inparm = BMSBldStockTool.getInstance().onQuery(parm);
        parm.setData("SAFE_QTY", inparm.getData("SAFE_QTY", 0));
        parm.setData("CURR_QTY", inparm.getDouble("CURR_QTY", 0) - 1);
        parm.setData("ACC_QTY", inparm.getDouble("ACC_QTY", 0) - 1);
        parm.setData("SAFE_VOL", inparm.getData("SAFE_VOL", 0));
        parm.setData("TOT_VOL",
                     inparm.getDouble("TOT_VOL", 0) -
                     parm.getDouble("BLOOD_VOL"));
        parm.setData("ACC_VOL",
                     inparm.getDouble("ACC_VOL", 0) -
                     parm.getDouble("BLOOD_VOL"));

        result = BMSBldStockTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����¼��ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryPat(TParm parm) {
        TParm result = new TParm();
        TParm resultOE = BMSQueryPatTool.getInstance().onQueryOE(parm);
        TParm resultI = BMSQueryPatTool.getInstance().onQueryI(parm);
        String status_desc = "";
        if ("O".equals(parm.getValue("IO_TYPE")) ||
            "E".equals(parm.getValue("IO_TYPE"))) {
            for (int i = 0; i < resultOE.getCount("ADM_TYPE"); i++) {
                result.addData("ADM_TYPE", resultOE.getValue("ADM_TYPE", i));
                result.addData("IN_DATE", resultOE.getValue("ADM_DATE", i));
                result.addData("CASE_NO", resultOE.getValue("CASE_NO", i));
                result.addData("USER_NAME", resultOE.getValue("USER_NAME", i));
                result.addData("DEPT_DESC",
                               resultOE.getValue("DEPT_CHN_DESC", i));
                result.addData("PAT_NAME", resultOE.getValue("PAT_NAME", i));
                result.addData("CTZ_DESC", resultOE.getValue("CTZ_DESC", i));
                result.addData("OUT_DATE", resultOE.getValue("ADM_DATE", i));
                result.addData("ID_NO", resultOE.getValue("IDNO", i));
                result.addData("MR_NO", resultOE.getValue("MR_NO", i));
                if (!"".equals(resultOE.getValue("REGCAN_DATE", i))) {
                    status_desc = "���˹�";
                }
                else if ("Y".equals(resultOE.getValue("SEE_DR_FLG", i))) {
                    status_desc = "�ѿ���";
                }
                else if ("T".equals(resultOE.getValue("SEE_DR_FLG", i))) {
                    status_desc = "����ݴ�";
                }
                else {
                    status_desc = "δ����";
                }
                result.addData("STATUS", status_desc);
                result.addData("DEPT_CODE", resultOE.getValue("DEPT_CODE", i));
                result.addData("CTZ1_CODE", resultOE.getValue("CTZ1_CODE", i));
                result.addData("BED_NO", "");
                result.addData("IPD_NO", "");
                result.addData("BLOOD_RH_TYPE",
                               resultOE.getValue("BLOOD_RH_TYPE", i));
                result.addData("BLOOD_TYPE", resultOE.getValue("BLOOD_TYPE", i));
                result.addData("TESTBLD_DR", resultOE.getValue("TESTBLD_DR", i));
            }
        }
        else if ("I".equals(parm.getValue("IO_TYPE"))) {
            for (int i = 0; i < resultI.getCount("ADM_TYPE"); i++) {
                result.addData("ADM_TYPE", resultI.getValue("ADM_TYPE", i));
                result.addData("IN_DATE", resultI.getValue("IN_DATE", i));
                result.addData("CASE_NO", resultI.getValue("CASE_NO", i));
                result.addData("USER_NAME", resultI.getValue("USER_NAME", i));
                result.addData("DEPT_DESC",
                               resultI.getValue("DEPT_CHN_DESC", i));
                result.addData("PAT_NAME", resultI.getValue("PAT_NAME", i));
                result.addData("CTZ_DESC", resultI.getValue("CTZ_DESC", i));
                result.addData("OUT_DATE", resultI.getValue("DS_DATE", i));
                result.addData("ID_NO", resultI.getValue("IDNO", i));
                result.addData("MR_NO", resultI.getValue("MR_NO", i));
                if ("Y".equals(resultI.getValue("CANCEL_FLG", i))) {
                    status_desc = "ȡ��סԺ";
                }
                else if ("2".equals(resultI.getValue("DS_DATE", i))) {
                    status_desc = "סԺ��";
                }
                else {
                    status_desc = "��Ժ";
                }
                result.addData("STATUS", status_desc);
                result.addData("DEPT_CODE", resultI.getValue("DEPT_CODE", i));
                result.addData("CTZ1_CODE", resultI.getValue("CTZ1_CODE", i));
                result.addData("BED_NO", resultI.getValue("BED_NO", i));
                result.addData("IPD_NO", resultI.getValue("IPD_NO", i));
                result.addData("BLOOD_RH_TYPE",
                               resultI.getValue("BLOOD_RH_TYPE", i));
                result.addData("BLOOD_TYPE", resultI.getValue("BLOOD_TYPE", i));
                result.addData("TESTBLD_DR", resultI.getValue("TESTBLD_DR", i));
            }
        }
        else {
            for (int i = 0; i < resultOE.getCount("ADM_TYPE"); i++) {
                result.addData("ADM_TYPE", resultOE.getValue("ADM_TYPE", i));
                result.addData("IN_DATE", resultOE.getValue("ADM_DATE", i));
                result.addData("CASE_NO", resultOE.getValue("CASE_NO", i));
                result.addData("USER_NAME", resultOE.getValue("USER_NAME", i));
                result.addData("DEPT_DESC",
                               resultOE.getValue("DEPT_CHN_DESC", i));
                result.addData("PAT_NAME", resultOE.getValue("PAT_NAME", i));
                result.addData("CTZ_DESC", resultOE.getValue("CTZ_DESC", i));
                result.addData("OUT_DATE", resultOE.getValue("ADM_DATE", i));
                result.addData("ID_NO", resultOE.getValue("IDNO", i));
                result.addData("MR_NO", resultOE.getValue("MR_NO", i));
                if (!"".equals(resultOE.getValue("REGCAN_DATE", i))) {
                    status_desc = "���˹�";
                }
                else if ("Y".equals(resultOE.getValue("SEE_DR_FLG", i))) {
                    status_desc = "�ѿ���";
                }
                else if ("T".equals(resultOE.getValue("SEE_DR_FLG", i))) {
                    status_desc = "����ݴ�";
                }
                else {
                    status_desc = "δ����";
                }
                result.addData("STATUS", status_desc);
                result.addData("DEPT_CODE", resultOE.getValue("DEPT_CODE", i));
                result.addData("CTZ1_CODE", resultOE.getValue("CTZ1_CODE", i));
                result.addData("BED_NO", "");
                result.addData("IPD_NO", "");
                result.addData("BLOOD_RH_TYPE",
                               resultOE.getValue("BLOOD_RH_TYPE", i));
                result.addData("BLOOD_TYPE", resultOE.getValue("BLOOD_TYPE", i));
                result.addData("TESTBLD_DR", resultOE.getValue("TESTBLD_DR", i));
            }
            for (int i = 0; i < resultI.getCount("ADM_TYPE"); i++) {
                result.addData("ADM_TYPE", resultI.getValue("ADM_TYPE", i));
                result.addData("IN_DATE", resultI.getValue("IN_DATE", i));
                result.addData("CASE_NO", resultI.getValue("CASE_NO", i));
                result.addData("USER_NAME", resultI.getValue("USER_NAME", i));
                result.addData("DEPT_DESC",
                               resultI.getValue("DEPT_CHN_DESC", i));
                result.addData("PAT_NAME", resultI.getValue("PAT_NAME", i));
                result.addData("CTZ_DESC", resultI.getValue("CTZ_DESC", i));
                result.addData("OUT_DATE", resultI.getValue("DS_DATE", i));
                result.addData("ID_NO", resultI.getValue("IDNO", i));
                result.addData("MR_NO", resultI.getValue("MR_NO", i));
                if ("Y".equals(resultI.getValue("CANCEL_FLG", i))) {
                    status_desc = "ȡ��סԺ";
                }
                else if ("2".equals(resultI.getValue("DS_DATE", i))) {
                    status_desc = "סԺ��";
                }
                else {
                    status_desc = "��Ժ";
                }
                result.addData("STATUS", status_desc);
                result.addData("DEPT_CODE", resultI.getValue("DEPT_CODE", i));
                result.addData("CTZ1_CODE", resultI.getValue("CTZ1_CODE", i));
                result.addData("BED_NO", resultI.getValue("BED_NO", i));
                result.addData("IPD_NO", resultI.getValue("IPD_NO", i));
                result.addData("BLOOD_RH_TYPE",
                               resultI.getValue("BLOOD_RH_TYPE", i));
                result.addData("BLOOD_TYPE", resultI.getValue("BLOOD_TYPE", i));
                result.addData("TESTBLD_DR", resultI.getValue("TESTBLD_DR", i));
            }
        }
        return result;
    }

    /**
     * ��Ѫ���뵥(����)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsertBMSApply(TParm parm, TConnection conn) {
        // ������Ѫ���뵥����
        TParm result = BMSApplyMTool.getInstance().onApplyInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // ������Ѫ���뵥ϸ��
        TParm parmD = parm.getParm("BMS_APPLYD");
        
        for (int i = 0; i < parmD.getCount("BLD_CODE"); i++) {
            TParm inparm = new TParm();
            inparm.setData("APPLY_NO", parm.getData("APPLY_NO"));
            inparm.setData("BLD_CODE", parmD.getData("BLD_CODE", i));
            inparm.setData("APPLY_QTY", parmD.getData("APPLY_QTY", i));
            inparm.setData("UNIT_CODE", parmD.getData("UNIT_CODE", i));
            
            //add by yangjj 20150323
            inparm.setData("APPLY_BLD", parmD.getData("APPLY_BLD", i));
            inparm.setData("APPLY_RH_TYPE", parmD.getData("APPLY_RH_TYPE", i));
            
            //add by yangjj 20150522
            inparm.setData("IRRADIATION", parmD.getData("IRRADIATION", i));
            
            inparm.setData("PRE_DATE", parm.getData("PRE_DATE"));
            inparm.setData("OPT_USER", parm.getData("OPT_USER"));
            inparm.setData("OPT_DATE", parm.getData("OPT_DATE"));
            inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
            result = BMSApplyDTool.getInstance().onApplyInsert(inparm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        //���²���Ѫ��.
        result = PatTool.getInstance().updatePatBldType(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }        
        return result;
    }

    /**
     * ��Ѫ���뵥(����)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateBMSApply(TParm parm, TConnection conn) {
        // ���±�Ѫ���뵥����
        TParm result = BMSApplyMTool.getInstance().onApplyUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // ɾ����Ѫ���뵥ϸ��
        result = BMSApplyDTool.getInstance().onApplyDelete(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // ������Ѫ���뵥ϸ��
        TParm parmD = parm.getParm("BMS_APPLYD");
        System.out.println("parmD:"+parmD);
        for (int i = 0; i < parmD.getCount("BLD_CODE"); i++) {
            TParm inparm = new TParm();
            inparm.setData("APPLY_NO", parm.getData("APPLY_NO"));
            inparm.setData("BLD_CODE", parmD.getData("BLD_CODE", i));
            inparm.setData("APPLY_QTY", parmD.getData("APPLY_QTY", i));
            inparm.setData("UNIT_CODE", parmD.getData("UNIT_CODE", i));
            
            //add by yangjj 20150323
            inparm.setData("APPLY_BLD", parmD.getData("APPLY_BLD", i));
            inparm.setData("APPLY_RH_TYPE", parmD.getData("APPLY_RH_TYPE", i));
            
            //add by yangjj 20150522
            inparm.setData("IRRADIATION", parmD.getData("IRRADIATION", i));
            
            inparm.setData("PRE_DATE", parm.getData("PRE_DATE"));
            inparm.setData("OPT_USER", parm.getData("OPT_USER"));
            inparm.setData("OPT_DATE", parm.getData("OPT_DATE"));
            inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
            System.out.println("inparm:"+inparm);
            result = BMSApplyDTool.getInstance().onApplyInsert(inparm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        //���²���Ѫ��.
        result = PatTool.getInstance().updatePatBldType(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }         
        return result;
    }

    /**
     * ��Ѫ���뵥(ɾ��)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteBMSApply(TParm parm, TConnection conn) {
        // ɾ����Ѫ���뵥����
        TParm result = BMSApplyMTool.getInstance().onApplyDelete(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // ɾ����Ѫ���뵥ϸ��
        result = BMSApplyDTool.getInstance().onApplyDelete(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��Ѫ����ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryBMSApply(TParm parm) {
        TParm result = new TParm();
        TParm resultM = BMSApplyMTool.getInstance().onApplyQuery(parm);
        result.setData("BMS_APPLYM", resultM.getData());
        TParm resultD = BMSApplyDTool.getInstance().onApplyQuery(parm);
        result.setData("BMS_APPLYD", resultD.getData());
        return result;
    }

    /**
     * ���±�Ѫ����Ϣ,���²���Ѫ��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdatePatCheckInfo(TParm parm, TConnection conn) {
        // ���±�Ѫ����Ϣ(��������)
        TParm result = BMSApplyMTool.getInstance().onUpdatePatCheckInfo(parm,
            conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // ���²���Ѫ��
        String mr_no = parm.getValue("MR_NO");
        String blood_type = parm.getValue("BLOOD_TYPE");
        String rh = parm.getValue("RH_FLG");
        String sql = "UPDATE SYS_PATINFO SET BLOOD_TYPE='" + blood_type +
            "' , BLOOD_RH_TYPE='" + rh + "' WHERE MR_NO = '" + mr_no + "'";
        result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
//        System.out.println("��Ѫ��"+parm.getData("BLOOD_TYPE"));
//        System.out.println("��Ѫ��"+parm.getData("BLOOD_TYPE_OLD"));
        if (parm.getData("BLOOD_TYPE") != null &&
            parm.getValue("BLOOD_TYPE").length() > 0 &&
            !parm.getValue("BLOOD_TYPE").equals(parm.getValue("BLOOD_TYPE_OLD"))) {
//            String oldBlood = "";
//            TNull tnull = new TNull(String.class);
//            if(parm.getData("BLOOD_TYPE_OLD")!=null)
//                oldBlood= parm.getValue("BLOOD_TYPE_OLD");
//            else
//                oldBlood = tnull;
            String patSql =
                "INSERT INTO SYS_PATLOG " +
                "            (MR_NO, OPT_DATE, MODI_ITEM, ITEM_OLD, ITEM_NEW, " +
                "            OPT_USER, OPT_TERM ) " +
                "     VALUES ('" + mr_no + "', TO_DATE('"+StringTool.getString(parm.getTimestamp("OPT_DATE"),"yyyyMMddHHmmss")+"','yyyyMMddHH24miss') , "+
                "             'Ѫ��', '" +parm.getValue("BLOOD_TYPE_OLD")+ "', '" + parm.getValue("BLOOD_TYPE") + "', '" +
                parm.getValue("OPT_USER") +
                "', '" + parm.getValue("OPT_TERM") + "')";
//            System.out.println("patSql"+patSql);
            result = new TParm(TJDODBTool.getInstance().update(patSql, conn));
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * ������ҳ�ӿ�
     * ���ݾ������,�����ź�סԺ�Ż�ò�����Ѫ��Ϣ(��ϸ��,ѪС��,Ѫ��,ȫѪ,����,��Ѫ��Ӧ)
     * @param parm TParm
     * @return TParm
     */
    public TParm getApplyInfo(TParm parm) {
        TParm result = new TParm();
        result.setData("RBC", 0);
        result.setData("PLATE", 0);
        result.setData("PLASMA", 0);
        result.setData("WHOLE_BLOOD", 0);
        result.setData("OTH_BLOOD", 0);
        result.setData("TRANS_REACTION", "3");
        // ��ϸ��,ѪС��,Ѫ��,ȫѪ,����
        TParm infoParm = BMSBloodTool.getInstance().getApplyInfo(parm);
        if (infoParm == null || infoParm.getCount() <= 0) {
            return result;
        }
        for (int i = 0; i < infoParm.getCount(); i++) {
            if ("01".equals(infoParm.getValue("FRONTPG_TYPE", i))) {
                result.setData("RBC", infoParm.getDouble("BLOOD_VOL", i));
            }
            else if ("02".equals(infoParm.getValue("FRONTPG_TYPE", i))) {
                result.setData("PLATE", infoParm.getDouble("BLOOD_VOL", i));
            }
            else if ("03".equals(infoParm.getValue("FRONTPG_TYPE", i))) {
                result.setData("PLASMA", infoParm.getDouble("BLOOD_VOL", i));
            }
            else if ("04".equals(infoParm.getValue("FRONTPG_TYPE", i))) {
                result.setData("WHOLE_BLOOD", infoParm.getDouble("BLOOD_VOL", i));
            }
            else if ("05".equals(infoParm.getValue("FRONTPG_TYPE", i))) {
                result.setData("OTH_BLOOD", infoParm.getDouble("BLOOD_VOL", i));
            }
        }
        // ��Ѫ��Ӧ
        TParm transParm = BMSSplrectTool.getInstance().onQueryTransReaction(
            parm);
        if (transParm == null || transParm.getCount() <= 0) {
            result.setData("TRANS_REACTION", "3");
        }
        else if (!"".equals(transParm.getValue("REACTION_CODE", 0))) {
            result.setData("TRANS_REACTION", "1");
        }
        else {
            result.setData("TRANS_REACTION", "2");
        }
        return result;
    }

    /**
     * ���±�Ѫ����Ϣ(������Ѫ)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateBloodCross(TParm parm, TConnection conn) {
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount("BLOOD_NO"); i++) {
            result = BMSBloodTool.getInstance().onUpdateBloodCross(parm.getRow(
                i), conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * ���±�Ѫ����Ϣ(ѪƷ����)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateBloodOut(TParm parm, TConnection conn) {
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount("BLOOD_NO"); i++) {
            // ���±�Ѫ����Ϣ
            result = BMSBloodTool.getInstance().onUpdateBloodOut(parm.getRow(
                i), conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
            // ���¿��
            result = BMSBldStockTool.getInstance().onUpdateOut(parm.getRow(
                i), conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * ����ѪƷ��������ѪƷ���
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onSaveBldSubcat(TParm parm, TConnection conn) {
        TParm result = new TParm();
        String[] tableM = (String[]) parm.getData("TABLE_M");
        result = new TParm(TJDODBTool.getInstance().update(tableM, conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        if (parm.existData("TABLE_D")) {
            result = new TParm(TJDODBTool.getInstance().update( (String[]) parm.
                getData("TABLE_D"), conn));
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * ����ѪƷ���
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateBldSubcat(TParm parm,TConnection conn){
    	TParm result = new TParm();

        result = new TParm(TJDODBTool.getInstance().update( (String[]) parm.
                getData("TABLE_D"), conn));
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        return result ;
    }

    /**
     * ɾ��ѪƷ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteBldSubcat(TParm parm, TConnection conn) {
        TParm result = new TParm();
        String[] tableM = (String[]) parm.getData("TABLE_M");
        result = new TParm(TJDODBTool.getInstance().update(tableM, conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        if (parm.existData("TABLE_D")) {
            result = new TParm(TJDODBTool.getInstance().update( (String[]) parm.
                getData("TABLE_D"), conn));
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        return result;
    }
}
