package jdo.ins;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: ������ת�빤����</p>
 *
 * <p>Description: ������ת�빤����</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2012.02.13
 * @version 1.0
 */
public class INSSingleTransferTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static INSSingleTransferTool instanceObject;
    /**
     * �õ�ʵ��
     * @return INSSingleTransferTool
     */
    public static INSSingleTransferTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSSingleTransferTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public INSSingleTransferTool() {
        onInit();
    }

    /**
     * �õ���������ת������
     * @param parm TParm
     * @return TParm
     */
    public TParm getSingleTransData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:��������ΪNULL");
            return result;
        }
        
        String wsql  = "";
        if(parm.getValue("MR_NO").length()  != 0)
        	wsql  = " AND C.MR_NO  = '"+parm.getValue("MR_NO")+"' " ; 
        
        String sql =
            " SELECT CASE SUBSTR (C.CONFIRM_NO, 1, 2) WHEN 'KN' THEN ''  ELSE TO_CHAR (A.IN_DATE, 'yyyymmdd') END AS CONFIRM," +
            "        A.CASE_NO, C.CONFIRM_NO, C.PAT_NAME, D.CHN_DESC AS SEX_DESC," +
            "        B.CTZ_DESC, C.IDNO," +
            "        CASE IN_STATUS " +
            "        WHEN '0' " +
            "        THEN '�ʸ�ȷ����¼��' " +
            "        WHEN '1' " +
            "        THEN '�����ѽ���' " +
            "        WHEN '2' " +
            "        THEN '�������ϴ�' " +
            "        WHEN '3' " +
            "        THEN '���������' " +
            "        WHEN '4' " +
            "        THEN '������֧��' " +
            "        WHEN '5' " +
            "        THEN '����ȷ����' " +
            "        WHEN '6' " +
            "        THEN '�����ʸ�ȷ����ʧ��' " +
            "        WHEN '7' " +
            "        THEN '�ʸ�ȷ���������' ELSE '' END AS IN_STATUS," +
            "        A.MR_NO, C.SDISEASE_CODE, E.CHN_DESC AS SINGLEDISEASE_DESC " +
            "   FROM ADM_INP A,SYS_CTZ B,INS_ADM_CONFIRM C,SYS_DICTIONARY D,SYS_DICTIONARY E " +
            "  WHERE A.REGION_CODE = '" + parm.getData("REGION_CODE") + "' " +
            "    AND A.IN_DATE BETWEEN TO_DATE ('" + parm.getData("S_DATE")+"000000"+
            "', 'YYYYMMDDhh24miss') " +
            "                  AND TO_DATE ('" + parm.getData("E_DATE")+"235959"+
            "', 'YYYYMMDDhh24miss') " +
            "    AND B.CTZ_CODE = A.CTZ1_CODE " +
            "    AND B.NHI_CTZ_FLG = 'Y' " +
            "    AND C.CASE_NO = A.CASE_NO " +
            "    AND (C.IN_STATUS IN ('0', '1', '7') OR C.IN_STATUS IS NULL) " +
            "    AND C.SEX_CODE = D.ID(+) " +
            "    AND D.GROUP_ID = 'SYS_SEX' " +
            "    AND C.SDISEASE_CODE = E.ID(+) " +
            "    AND E.GROUP_ID = 'SIN_DISEASE' " +
            "    AND C.SDISEASE_CODE IS NOT NULL " +
            "    AND SUBSTR (B.CTZ_CODE, 0, 1) = '" + parm.getData("CARD_TYPE") +
            "' " + wsql ;
        //System.out.println("�õ���������ת������" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �õ�������δת������
     * @param parm TParm
     * @return TParm
     */
    public TParm getSingleNoTransData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:��������ΪNULL");
            return result;
        }
        
        String wsql  = "";
        if(parm.getValue("MR_NO").length()  != 0)
        	wsql  = " AND C.MR_NO  = '"+parm.getValue("MR_NO")+"' " ; 
        
        String sql =
            " SELECT CASE SUBSTR (C.CONFIRM_NO, 1, 2) WHEN 'KN' THEN ''  ELSE TO_CHAR (A.IN_DATE, 'yyyymmdd') END AS CONFIRM," +
            "        A.CASE_NO, C.CONFIRM_NO, C.PAT_NAME, D.CHN_DESC AS SEX_DESC," +
            "        B.CTZ_DESC, C.IDNO," +
            "        CASE IN_STATUS " +
            "        WHEN '0' " +
            "        THEN '�ʸ�ȷ����¼��' " +
            "        WHEN '1' " +
            "        THEN '�����ѽ���' " +
            "        WHEN '2' " +
            "        THEN '�������ϴ�' " +
            "        WHEN '3' " +
            "        THEN '���������' " +
            "        WHEN '4' " +
            "        THEN '������֧��' " +
            "        WHEN '5' " +
            "        THEN '����ȷ����' " +
            "        WHEN '6' " +
            "        THEN '�����ʸ�ȷ����ʧ��' " +
            "        WHEN '7' " +
            "        THEN '�ʸ�ȷ���������' ELSE '' END AS IN_STATUS," +
            "        A.MR_NO, '','' " +
            "   FROM ADM_INP A,SYS_CTZ B,INS_ADM_CONFIRM C,SYS_DICTIONARY D " +
            "  WHERE A.REGION_CODE = '" + parm.getData("REGION_CODE") + "' " +
            "    AND A.IN_DATE BETWEEN TO_DATE ('" + parm.getData("S_DATE")+"000000"+
            "', 'YYYYMMDDhh24miss') " +
            "                  AND TO_DATE ('" + parm.getData("E_DATE")+"235959"+
            "', 'YYYYMMDDhh24miss') " +
            "    AND B.CTZ_CODE = A.CTZ1_CODE " +
            "    AND B.NHI_CTZ_FLG = 'Y' " +
            "    AND C.CASE_NO = A.CASE_NO " +
            "    AND (C.IN_STATUS IN ('0', '1', '7') OR C.IN_STATUS IS NULL) " +
            "    AND C.SEX_CODE = D.ID(+) " +
            "    AND D.GROUP_ID = 'SYS_SEX' " +
            "    AND C.SDISEASE_CODE IS NULL " +
            "    AND SUBSTR (B.CTZ_CODE, 0, 1) = '" + parm.getData("CARD_TYPE") + "' "  + wsql ;
        //System.out.println("�õ���������ת������" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �����ʸ�ȷ����״̬
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpINSIbsUpload(TParm parm) {
        TParm result = new TParm();
        String setSql = "";
        String diseaseCode  = ""+ parm.getValue("SDISEASE_CODE");
        
        if (diseaseCode.length() != 0)
            setSql = "    SET SDISEASE_CODE = '" + parm.getData("SDISEASE_CODE") + "' ";
        else
            setSql = "    SET SDISEASE_CODE = NULL ";
        
        String sql =
            " UPDATE INS_ADM_CONFIRM " +
            setSql +
            "  WHERE CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "' ";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;

    }

}

