package jdo.ins;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: �Ǿӵ渶�걨������</p>
 *
 * <p>Description: �Ǿӵ渶�걨������</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2012.02.14
 * @version 1.0
 */
public class INSCJAdvanceTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static INSCJAdvanceTool instanceObject;
    /**
     * �õ�ʵ��
     * @return INSCJAdvanceTool
     */
    public static INSCJAdvanceTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSCJAdvanceTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public INSCJAdvanceTool() {
        onInit();
    }

    /**
     * ��ѯ������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm getINSIbsData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:��������ΪNULL");
            return result;
        }
        String sql =
            " SELECT 'N' AS FLG, I.ADM_SEQ, I.MR_NO, I.PAT_NAME, S.CHN_DESC AS SEX_DESC, C.CTZ_DESC," +
            "        TO_CHAR(I.IN_DATE,'yyyy/MM/dd') AS IN_DATE, TO_CHAR(I.DS_DATE,'yyyy/MM/dd') AS DS_DATE,"+
            "        I.CASE_NO, I.SPECIAL_SITU " +
            "   FROM INS_ADVANCE_PAYMENT I, SYS_CTZ C, SYS_PATINFO P, SYS_DICTIONARY S " +
            "  WHERE I.CTZ_TYPE = C.CTZ_CODE " +
            "    AND I.MR_NO = P.MR_NO " +
            "    AND P.SEX_CODE = S.ID(+) " +
            "    AND S.GROUP_ID = 'SYS_SEX' " +
            "    AND I.INS_STATUS = '2' " +
            "    AND I.IN_DATE BETWEEN TO_DATE ('" + parm.getData("S_DATE") + "000000"+"', 'YYYYMMDDhh24miss') " +
            "                      AND TO_DATE ('" + parm.getData("E_DATE") + "000000"+"', 'YYYYMMDDhh24miss') " ;
        //System.out.println("��ѯ������Ϣ" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * �õ��渶�����ϴ���ϸ
     * @param parm TParm
     * @return TParm
     */
    public TParm getAdvUpLoadData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:��������ΪNULL");
            return result;
        }
        String sql =
            " SELECT A.ADM_SEQ, A.ADM_SEQ, '91'," +
            "        TO_CHAR (A.CHARGE_DATE, 'YYYYMMDDHH24MISS'), A.SEQ_NO, '000551', " +
            "        A.NHI_ORDER_CODE, A.ORDER_DESC, A.OWN_RATE, D.JX, D.GG, A.PRICE, QTY, " +
            "        A.TOTAL_AMT, A.TOTAL_NHI_AMT, A.OWN_AMT, A.ADDPAY_AMT, A.OP_FLG, " +
            "        A.ADDPAY_FLG, A.NHI_ORD_CLASS_CODE, A.PHAADD_FLG, A.CARRY_FLG,D.PZWH " +
            "   FROM INS_IBS_UPLOAD A, INS_RULE D " +
            "  WHERE A.REGION_CODE = '" + parm.getData("") + "' " +
            "    AND A.ADM_SEQ = '" + parm.getData("") + "' " +
            "    AND A.QTY <> 0 " +
            "    AND A.NHI_ORDER_CODE = D.SFXMBM " +
            "    AND A.CHARGE_DATE BETWEEN D.KSSJ AND D.JSSJ " +
            "  ORDER BY A.ADM_SEQ " ;
       // System.out.println("�õ���������" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ����INS_ADVANCE_PAYMENT�������
     * @param parm TParm
     * @return TParm
     */
    public TParm upSpecialSitu(TParm parm) {
        TParm result = new TParm();
        String sql =
            " UPDATE INS_ADVANCE_PAYMENT " +
            "    SET SPECIAL_SITU = '" + parm.getData("") + "' " +
            "  WHERE ADM_SEQ = '" + parm.getData("") + "' ";
       // System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;

    }
    /**
     * ����INS_ADVANCE_PAYMENTҽ��״̬
     * @param parm TParm
     * @return TParm
     */
    public TParm upInsStatus(TParm parm) {
        TParm result = new TParm();
        String sql =
            " UPDATE INS_ADVANCE_PAYMENT " +
            "    SET INS_STATUS = '4' " +
            "  WHERE ADM_SEQ = '" + parm.getData("") + "' ";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;

    }
    /**
     * ����INS_ADVANCE_PAYMENTҽ��״̬,����
     * @param parm TParm
     * @return TParm
     */
    public TParm upInsStaDate(TParm parm) {
        TParm result = new TParm();
        String sql =
            " UPDATE INS_ADVANCE_PAYMENT " +
            "    SET INS_STATUS = '4',"+
            "        UPLOAD_DATE = '" + parm.getData("") + "' " +
            "  WHERE ADM_SEQ = '" + parm.getData("") + "' ";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;

    }
    /**
     * ����Ʊ��
     * @param parm TParm
     * @return TParm
     */
    public TParm upInsInvNo(TParm parm) {
        TParm result = new TParm();
        String sql =
            " UPDATE INS_IBS_UPLOAD " +
            "    SET INVNO = '" + parm.getData("") + "' "+
            "  WHERE ADM_SEQ = '" + parm.getData("") + "' ";
       // System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;

    }


}
