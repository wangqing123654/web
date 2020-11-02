package com.javahis.report.hrm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.util.IReport;
import com.javahis.util.StringUtil;
/**
 * <p> Title: ��������������׼���� </p>
 * 
 * <p> Description: ��������������׼����  </p>
 * 
 * <p> Copyright: Bluecore 20130730 </p>
 * 
 * <p> Company: Bluecore </p>
 * 
 * @author wanglong
 * @version 1.0
 */
public class HRMReportSheetReportTool implements IReport {

    /**
     * ׼��������
     * @param parm
     * @return
     */
    public TParm getReportParm(TParm parm) {
        String mrNo = parm.getValue("MR_NO");
        String caseNo = parm.getValue("CASE_NO");
        TParm result=new TParm();
        if(StringUtil.isNullString(mrNo)||StringUtil.isNullString(caseNo)){
            //out.println("mrNo,caseNo is null");
            return null;
        }
        TParm patParm=getPatParm(caseNo);//��hrm_patadm��ȡ��Ա��Ϣ
        if(patParm==null){
            //out.println(":patParm is null");
            return null;
        }
        //=====================modify by wanglong 20130204
        result.setData("COMPANY_DESC", patParm.getValue("COMPANY_DESC", 0));
        result.setData("COMPANY_DESC", "TEXT", patParm.getValue("COMPANY_DESC", 0));
        result.setData("CONTRACT_DESC", patParm.getValue("CONTRACT_DESC", 0));
        result.setData("CONTRACT_DESC", "TEXT", patParm.getValue("CONTRACT_DESC", 0));
        result.setData("PACKAGE_DESC", patParm.getValue("PACKAGE_DESC", 0));
        result.setData("PACKAGE_DESC", "TEXT", patParm.getValue("PACKAGE_DESC", 0));
        result.setData("SEQ_NO", patParm.getValue("SEQ_NO", 0));
        result.setData("SEQ_NO", "TEXT", patParm.getValue("SEQ_NO", 0));
        result.setData("MR_NO", mrNo);
        result.setData("PAT_NAME", "TEXT", patParm.getValue("PAT_NAME", 0));
        result.setData("SEX_CODE", "TEXT", patParm.getValue("SEX_DESC", 0));
        result.setData("BIRTH_DATE",
                       StringTool.getString(patParm.getTimestamp("BIRTHDAY", 0), "yyyy/MM/dd"));
        result.setData("BILL_FLG", "TEXT", patParm.getValue("BILL_FLG", 0).equals("1") ? "��" : "��");// ����״̬
        result.setData("MARRIAGE_DESC", "TEXT", patParm.getValue("MARRIAGE_DESC", 0));// ����״̬
        result.setData("TEL", "TEXT", patParm.getValue("TEL", 0));// �绰add by wanglong 20130412
        result.setData("PAT_DEPT", patParm.getValue("PAT_DEPT", 0));// ����add by wanglong 20130225
        if (!patParm.getValue("PAT_DEPT", 0).trim().equals("")) {// add by wanglong 20130225
            result.setData("DEPT_TITLE", "TEXT", "����:");
            result.setData("PAT_DEPT", "TEXT", patParm.getValue("PAT_DEPT", 0));
            result.setData("DEPT_TITLE", "����:");
            result.setData("PAT_DEPT", patParm.getValue("PAT_DEPT", 0));
        }
        result.setData("STAFF_NO", patParm.getValue("STAFF_NO", 0));// ����add by wanglong 20130225
        if (!patParm.getValue("STAFF_NO", 0).trim().equals("")) {// add by wanglong 20130225
            result.setData("STAFF_TITLE", "TEXT", "Ա����:");
            result.setData("STAFF_NO", "TEXT", patParm.getValue("STAFF_NO", 0));
            result.setData("STAFF_TITLE", "Ա����:");
            result.setData("STAFF_NO", patParm.getValue("STAFF_NO", 0));
        }
        String age =
                StringTool.CountAgeByTimestamp(patParm.getTimestamp("BIRTHDAY", 0), SystemTool
                        .getInstance().getDate())[0];
        result.setData("AGE", "TEXT", age);// ����
        if (!patParm.getValue("ID_NO", 0).matches("\\d{15}|\\d{18}|\\d{14}[xX]|\\d{17}[xX]")//add by wanglong 20130530
                || !StringTool.isId(patParm.getValue("ID_NO", 0))) {
            result.setData("ID_NO", "TEXT",
                           StringTool.getString(patParm.getTimestamp("BIRTHDAY", 0), "yyyy/MM/dd"));// �������ʾ��������
        } else {
            result.setData("ID_NO", "TEXT", patParm.getValue("ID_NO", 0));// ���֤��
        }
        //=====================modify end
        result.setData("MR_CODE", "TEXT", mrNo);
        String introUser = patParm.getValue("INTRO_USER", 0);
        String reportlist = patParm.getValue("REPORTLIST", 0);
        String introUserName =
                StringUtil.getDesc("SYS_OPERATOR", "USER_NAME", "USER_ID='" + introUser + "'");
        String reportlistName = this.getReportlist(reportlist);
        String packageCode = patParm.getValue("PACKAGE_CODE", 0);
        if (reportlistName == null) {
            reportlistName = "";
        }
        result.setData("INTRO_USER", introUserName);
        result.setData("REPORTLIST", "���˳��" + reportlistName);
        TParm deptParm = getDeptTParm(caseNo, packageCode);
        if (deptParm != null) {
            if (deptParm.getErrCode() != 0) {
                return deptParm;
            }
            if (deptParm.getData("IN") != null) {
                result.setData("IN_TABLE", ((TParm) deptParm.getData("IN")).getData());// �ڿ�
            }
            if (deptParm.getData("OUT_WOMAN") != null) {
                result.setData("OUT_WOMAN_TABLE", ((TParm) deptParm.getData("OUT_WOMAN")).getData());// ��ơ����ƹ���
            }
            if (deptParm.getData("EYE") != null) {
                result.setData("EYE_TABLE", ((TParm) deptParm.getData("EYE")).getData());// �ۿƢ��ۿƢ���
            }
            if (deptParm.getData("FACE") != null) {
                result.setData("FACE_TABLE", ((TParm) deptParm.getData("FACE")).getData());// ��ٿƢ���ٿƢ���
            }
        }
        TParm orderParm = this.getReportSheet(caseNo, packageCode);
        if (orderParm != null) {
            if (orderParm.getErrCode() != 0) {
                return null;
            }
            result.setData("ORDER_TABLE", orderParm.getData());
        }
        return result;
    }
    
    /**
     * ���ݸ���CASE_NO��ѯPAT���ݣ��ڵ�������ʾ��
     * @param caseNo
     * @return
     */
    private TParm getPatParm(String caseNo) {
        TParm parm = new TParm();
        if (StringUtil.isNullString(caseNo)) {
            return null;
        }
        //�������и�������
        String sql =//modify by wanglong 20130225
                "SELECT A.COMPANY_CODE,E.COMPANY_DESC,A.CONTRACT_CODE,F.CONTRACT_DESC,A.PACKAGE_CODE,B.PACKAGE_DESC,"
                        + "F.SEQ_NO,F.STAFF_NO,A.PAT_NAME,A.BIRTHDAY,A.SEX_CODE,C.CHN_DESC SEX_DESC,A.ID_NO,"
                        + "A.MARRIAGE_CODE,D.MARRIAGE_DESC,F.PAT_DEPT,A.REPORTLIST,A.INTRO_USER,A.BILL_FLG,F.TEL "//modify by wanglong 20130412
                        + " FROM HRM_PATADM A,HRM_PACKAGEM B,SYS_DICTIONARY C,"
                        + "      (SELECT ID,CHN_DESC AS MARRIAGE_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_MARRIAGE') D,"
                        + "      HRM_COMPANY E, HRM_CONTRACTD F "
                        + "WHERE A.CASE_NO='#' "
                        + "  AND A.PACKAGE_CODE=B.PACKAGE_CODE(+) "
                        + "  AND A.MARRIAGE_CODE=D.ID(+) "
                        + "  AND A.SEX_CODE=C.ID "
                        + "  AND C.GROUP_ID='SYS_SEX' "
                        + "  AND A.COMPANY_CODE = E.COMPANY_CODE "
                        + "  AND A.CONTRACT_CODE = F.CONTRACT_CODE "
                        + "  AND A.MR_NO = F.MR_NO";
        sql = sql.replaceFirst("#", caseNo);
        // System.out.println("getPatParm.sql="+sql);
        parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm.getErrCode() != 0) {
            // System.out.println("getPatParm.errText="+parm.getErrText());
            return null;
        }
        int count = parm.getCount();
        if (count <= 0) {
            // System.out.println("getPatParm.count <=0");
            return null;
        }
        return parm;
    }

    /**
     * ���ݸ���REPORTLIST���õ��ö�������
     * @param reportList
     * @return
     */
    public String getReportlist(String reportList) {
        StringBuffer sb = new StringBuffer();
        if (StringUtil.isNullString(reportList)) {
            return null;
        }
        String sql = "SELECT ID||'.'||CHN_DESC NAME FROM SYS_DICTIONARY WHERE GROUP_ID='#' ORDER BY SEQ".replaceFirst("#", reportList);
        // System.out.println("getReportlist=="+sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm.getErrCode() != 0 || parm.getCount() <= 0) {
            return null;
        }
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            sb.append(parm.getValue("NAME", i)).append("  ");
        }
        return sb.toString();
    }
    
    /**
     * 
     * @param caseNo
     * @return
     */
    private TParm getDeptTParm(String caseNo, String packageCode) {
        TParm result = new TParm();// modify by wanglong 20130131
        if (StringUtil.isNullString(caseNo)) {
            return null;
        }
        //�������зֿ�TABLE������
//        private static final String GET_DEPT_TABLE_TPARM="SELECT B.CHN_DESC ,'' OPT_USER,A.DR_NOTE " +
//                "FROM HRM_ORDER A, SYS_DICTIONARY B,HRM_PACKAGED C  " +
//                "WHERE A.CASE_NO='#' " +
//                "AND A.DEPT_ATTRIBUTE IS NOT NULL " +
//                "AND A.DEPT_ATTRIBUTE = B.ID(+) " +
//                "AND B.GROUP_ID='SYS_DEPT_ATTRIBUTE' " +
//                "AND A.SETMAIN_FLG='Y' " +
//                "AND A.ORDER_CODE=C.ORDER_CODE(+) " +
//                "AND C.PACKAGE_CODE='#' " +
//                "ORDER BY C.CHECK_SEQ ";
//        String sqlN =   "SELECT   B.CHN_DESC, '' OPT_USER, A.DR_NOTE "+
//                " FROM   HRM_ORDER A, SYS_DICTIONARY B "+
//                " WHERE       A.CASE_NO = '"+caseNo+"'"+
//                " AND A.DEPT_ATTRIBUTE IS NOT NULL "+
//                " AND A.DEPT_ATTRIBUTE = B.ID(+) "+
//                " AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE' "+
//                " AND A.SETMAIN_FLG = 'Y' ORDER BY A.SEQ_NO";
        String sql =// modify by wanglong 20130207
                "SELECT DISTINCT DEPT_ATTRIBUTE, B.CHN_DESC "
                        + "  FROM HRM_ORDER A, SYS_DICTIONARY B "
                        + " WHERE A.CASE_NO = '" + caseNo
                        + "'  AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE' "
                        + "   AND A.DEPT_ATTRIBUTE = B.ID " 
                        + " UNION "
                        + "SELECT B.ORDER_CODE DEPT_ATTRIBUTE, B.ORDER_DESC CHN_DESC "
                        + "  FROM HRM_ORDER A, SYS_FEE B "
                        + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                        + "   AND A.SETMAIN_FLG = 'Y' " 
                        + "   AND A.CASE_NO = '" + caseNo + "'";
//        String sql = // modify by wanglong 20130131
//                "SELECT DISTINCT DEPT_ATTRIBUTE, B.CHN_DESC "
//                        + "  FROM HRM_ORDER A, SYS_DICTIONARY B " 
//                        + " WHERE A.CASE_NO = '"
//                        + caseNo
//                        + "'  AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE' "
//                        + "   AND A.DEPT_ATTRIBUTE = B.ID "
//                        + "ORDER BY A.DEPT_ATTRIBUTE";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            return null;
        }
        int count = result.getCount();
        if (count <= 0) {
            result.setErrCode(-1);
            result.setErrText("���뿪���ܼ����͵�ҽ��");
            return null;
        }
        //====================================modify by wanglong 20130131
        Map content = new HashMap();
        for (int i = 0; i < count; i++) {
            content.put(result.getValue("DEPT_ATTRIBUTE", i), result.getValue("CHN_DESC", i));
        }
        TParm parm = new TParm();
        if (content.containsKey("03")) {// �ڿ�
            TParm inParm = new TParm();// �ڿ�
            inParm.addData("COL1", " �� ��");
            inParm.addData("COL2", "Ѫѹ:            mmHg");
            inParm.setData("TABLE_VALUE", 0, "HLine=N;#0.Bold=Y;#0.FontSize=11;#1.Bold=Y");
            inParm.addData("COL1", "����:");
            inParm.addData("COL2", "���ࣺ");
            inParm.setData("TABLE_VALUE", 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y");
            inParm.setCount(inParm.getCount("COL1"));
            inParm.addData("SYSTEM", "COLUMNS", "COL1");
            inParm.addData("SYSTEM", "COLUMNS", "COL2");
            parm.setData("IN", inParm);
        }
        TParm outOrWomanParm = new TParm();// ��ƻ򸾿�
        if (content.containsKey("05")) {// ���
            outOrWomanParm.addData("COL1", " �� ��");
            outOrWomanParm.addData("COL2", "���(m):        ����(kg):");
            outOrWomanParm.addData("COL3", "     ����ָ��(kg/�O):");
            outOrWomanParm.setData("TABLE_VALUE", outOrWomanParm.getCount("COL1") - 1, "HLine=N;#0.Bold=Y;#0.FontSize=11;#1.Bold=Y;#2.Bold=Y");
            outOrWomanParm.addData("COL1", "Ƥ��:");
            outOrWomanParm.addData("COL2", "�ز�:");
            outOrWomanParm.addData("COL3", "��������֫:");
            outOrWomanParm.setData("TABLE_VALUE", outOrWomanParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
            outOrWomanParm.addData("COL1", "��ǳ�ܰͽ�:");
            outOrWomanParm.addData("COL2", "��״��:");
            outOrWomanParm.addData("COL3", "");
            outOrWomanParm.setData("TABLE_VALUE", outOrWomanParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
        }
        if (content.containsKey("02")) {// ����
            // if (content.containsKey("U0201001")) {// *���Ƽ��ģ�� add by wanglong 20130225
            outOrWomanParm.addData("COL1", " �� ��");
            // } else {
            // outOrWomanParm.addData("COL1", " �� ��TCT");
            // }
            outOrWomanParm.addData("COL2", "");
            outOrWomanParm.addData("COL3", "");
            outOrWomanParm.setData("TABLE_VALUE", outOrWomanParm.getCount("COL1") - 1, "HLine=N;#0.Bold=Y;#0.FontSize=11;#1.Bold=Y;#2.Bold=Y");
            outOrWomanParm.addData("COL1", "����:");
            outOrWomanParm.addData("COL2", "����:");//modify by wanglong 20130521
            outOrWomanParm.addData("COL3", "����:");
            outOrWomanParm.setData("TABLE_VALUE", outOrWomanParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
            String typeName = "";// add by wanglong 20130521
            if (content.containsKey("Y1009011") && content.containsKey("Y1010039")) {//modify by wanglong 20130909
                typeName = "TCT��HPV";
            } else if (content.containsKey("Y1009011")) {// *TCT
                typeName = "TCT";
            } else if (content.containsKey("Y1010039")) {// *HPV
                typeName = "HPV";
            } else {
                typeName = "��Ƭ";
            }
            outOrWomanParm.addData("COL1", "�������ࣺ" + typeName);// modify by wanglong 20130521
            outOrWomanParm.addData("COL2", "�����:");
            outOrWomanParm.addData("COL3", "");
            outOrWomanParm.setData("TABLE_VALUE", outOrWomanParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
        }
        outOrWomanParm.setCount(outOrWomanParm.getCount("COL1"));
        outOrWomanParm.addData("SYSTEM", "COLUMNS", "COL1");
        outOrWomanParm.addData("SYSTEM", "COLUMNS", "COL2");
        outOrWomanParm.addData("SYSTEM", "COLUMNS", "COL3");
        parm.setData("OUT_WOMAN", outOrWomanParm);
        TParm eyeParm = new TParm();// �ۿ�
        if (content.containsKey("Y0409002")) {// �ۿƢ�
            eyeParm.addData("COL1", " �� �Ƣ�");
            eyeParm.addData("COL2", "");
            eyeParm.setData("TABLE_VALUE", 0, "HLine=N;#0.Bold=Y;#0.FontSize=11;#1.Bold=Y");
            eyeParm.addData("COL1", "�۵�:");
            eyeParm.addData("COL2", "��϶�Ƽ��:");
            eyeParm.setData("TABLE_VALUE", 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y");
        }
        if (content.containsKey("Y1009007")) {// �ۿƢ�
            eyeParm.addData("COL1", " �� �Ƣ�");
            eyeParm.addData("COL2", "");
            eyeParm.setData("TABLE_VALUE", eyeParm.getCount("COL1") - 1, "HLine=N;#0.Bold=Y;#0.FontSize=11;#1.Bold=Y");
            eyeParm.addData("COL1", "����:  ��          ��");
            eyeParm.addData("COL2", "ɫ�����:                       �۵�:");
            eyeParm.setData("TABLE_VALUE", eyeParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y");
            eyeParm.addData("COL1", "����:  ��          ��");
            eyeParm.addData("COL2", "��϶�Ƽ��:");
            eyeParm.setData("TABLE_VALUE", eyeParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y");
        }
        eyeParm.setCount(eyeParm.getCount("COL1"));
        eyeParm.addData("SYSTEM", "COLUMNS", "COL1");
        eyeParm.addData("SYSTEM", "COLUMNS", "COL2");
        parm.setData("EYE", eyeParm);
        TParm faceParm = new TParm();// ��ٿ�
        if (content.containsKey("Y1009008")) {// ��ٿƢ�
            faceParm.addData("COL1", " ���Ǻ��");
            faceParm.addData("COL2", "");
            faceParm.addData("COL3", "");
            faceParm.setData("TABLE_VALUE", 0, "HLine=N;#0.Bold=Y;#0.FontSize=11;#1.Bold=Y;#2.Bold=Y");
            faceParm.addData("COL1", "�����:");
            faceParm.addData("COL2", "��Ĥ:");
            faceParm.addData("COL3", "��:");
            faceParm.setData("TABLE_VALUE", 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
            faceParm.addData("COL1", "�ʺ�:");
            faceParm.addData("COL2", "��:");
            faceParm.addData("COL3", "");
            faceParm.setData("TABLE_VALUE", 2, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
        }
        if (content.containsKey("U0101007")) {// ��ٿƢ�
            faceParm.addData("COL1", " ���Ǻ��");
            faceParm.addData("COL2", "");
            faceParm.addData("COL3", "");
            faceParm.setData("TABLE_VALUE", faceParm.getCount("COL1") - 1, "HLine=N;#0.Bold=Y;#0.FontSize=11;#1.Bold=Y;#2.Bold=Y");
            faceParm.addData("COL1", "����:  ��          ��");
            faceParm.addData("COL2", "�����:");
            faceParm.addData("COL3", "��Ĥ:");
            faceParm.setData("TABLE_VALUE", faceParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
            faceParm.addData("COL1", "��:");
            faceParm.addData("COL2", "�ʺ�:");
            faceParm.addData("COL3", "��:");
            faceParm.setData("TABLE_VALUE", faceParm.getCount("COL1") - 1, "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y");
        }
        faceParm.setCount(faceParm.getCount("COL1"));
        faceParm.addData("SYSTEM", "COLUMNS", "COL1");
        faceParm.addData("SYSTEM", "COLUMNS", "COL2");
        faceParm.addData("SYSTEM", "COLUMNS", "COL3");
        parm.setData("FACE", faceParm);
        //====================================modify by wanglong 20130131
        return parm;
    }
    
    /**
     * ����CASE_NOȡ�õ���������
     * @param caseNo
     * @return
     */
    public TParm getReportSheet(String caseNo, String packageCode) {
        TParm result = new TParm();
        if (StringUtil.isNullString(caseNo)) {
            // System.out.println("caseNo is not null");
            return null;
        }
        //ȡ�õ��������� xueyf add(����ţ�����ҽ������������Ϊ��,ֻ��LIS,RIS��)
//        String sql =
//                ("SELECT A.ORDER_DESC,'' EXEC_DR,'' COL1,'' COL2, A.ORDER_CODE, A.DR_NOTE "
//                        + " FROM HRM_ORDER A " 
//                        + "WHERE A.CASE_NO = '#' "
//                        + "  AND A.SETMAIN_FLG = 'Y' " 
//                        + "  AND A.RPTTYPE_CODE IS NOT NULL "
//                        + "  AND (A.CAT1_TYPE='LIS' OR A.CAT1_TYPE='RIS') "
//                        + "ORDER BY A.ORDER_CODE").replaceFirst("#",caseNo);
        String sql = // modify by wanglong 20130131
                "SELECT A.ORDER_CODE, A.ORDER_DESC, A.GOODS_DESC, A.EXEC_DEPT_CODE, B.CATEGORY_CODE, B.CATEGORY_CHN_DESC "//modify by wanglong 20130308
                        + " FROM HRM_ORDER A, SYS_CATEGORY B "
                        + "WHERE A.CASE_NO = '"
                        + caseNo
                        + "' AND A.EXEC_DEPT_CODE <> '020301' " // 020301 ��������
                        + "  AND A.EXEC_DEPT_CODE IS NOT NULL "
                        + "  AND A.SETMAIN_FLG = 'Y' "
                        + "  AND SUBSTR( A.ORDER_CODE, 0, 3) = B.CATEGORY_CODE "
                        + "  AND B.RULE_TYPE='EXM_RULE' "
                        + "  AND A.ORDER_CODE NOT IN ('Y1010039') " //����ʾ*HPV add by wanglong 20130521
                        + "ORDER BY A.ORDER_CODE DESC";
        result=new TParm(TJDODBTool.getInstance().select(sql));
        // System.out.println("result===="+result);
        if (result.getErrCode() != 0) {
            // System.out.println("result.getErrCode="+result.getErrText());
            return null;
        }
        int count=result.getCount();
         if (count < 1) {
                return null;
            }
         //=================================modify by wanglong 20130131
        final int COL_NUM = 4;
        TParm items = new TParm();
        String[] depts = new String[count];
        for (int i = 0; i < count; i++) {
            items.addData(result.getValue("CATEGORY_CHN_DESC", i), result.getValue("ORDER_DESC", i));
            depts[i] = result.getValue("CATEGORY_CHN_DESC", i);
        }
        int lisCount=items.getCount("�����");
        for (int i = 0; i < lisCount; i++) {
            if (items.getValue("�����", i).matches(".*��.*")) {
                items.addData("�����", items.getValue("�����", i));
                items.removeRow(i, "�����");
            }
        }
        for (int i = 0; i < lisCount; i++) {
            if (items.getValue("�����", i).matches(".*����.*")) {
                items.addData("�����", items.getValue("�����", i));
                items.removeRow(i, "�����");
            }
        }
        for (int i = 0; i < lisCount; i++) {
            if (items.getValue("�����", i).matches(".*��.*")) {
                items.addData("�����", items.getValue("�����", i));
                items.removeRow(i, "�����");
            }
        }
        int uscCount=items.getCount("������");
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("������", i).matches(".*��.*")) {
                items.addData("������", items.getValue("������", i));
                items.removeRow(i, "������");
            }
        }
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("������", i).matches(".*��.*")) {
                items.addData("������", items.getValue("������", i));
                items.removeRow(i, "������");
            }
        }
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("������", i).matches(".*��.*")) {
                items.addData("������", items.getValue("������", i));
                items.removeRow(i, "������");
            }
        }
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("������", i).matches(".*��.*")) {
                items.addData("������", items.getValue("������", i));
                items.removeRow(i, "������");
            }
        }
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("������", i).matches(".*��.*")) {
                items.addData("������", items.getValue("������", i));
                items.removeRow(i, "������");
            }
        }
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("������", i).matches(".*����.*")) {
                items.addData("������", items.getValue("������", i));
                items.removeRow(i, "������");
            }
        }
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("������", i).matches(".*��.*")) {
                items.addData("������", items.getValue("������", i));
                items.removeRow(i, "������");
            }
        }
        for (int i = 0; i < uscCount; i++) {
            if (items.getValue("������", i).matches(".*֫.*")) {
                items.addData("������", items.getValue("������", i));
                items.removeRow(i, "������");
            }
        }
        depts = arrayUnique(depts);
        TParm orderParm = new TParm();
        for (int i = 0; i < depts.length; i++) {
            orderParm.addData("COL1", " " + depts[i]);
            for (int j = 2; j <= COL_NUM; j++) {
                orderParm.addData("COL" + j, "");
            }
            orderParm.setData("TABLE_VALUE", orderParm.getCount("COL1") - 1,
                              "HLine=N;#0.Bold=Y;#0.FontSize=11");
            int deptOrders = ceil(items.getCount(depts[i]), COL_NUM) * COL_NUM;
            for (int k = 0; k < deptOrders; k++) {
                orderParm.addData("COL" + (k % COL_NUM + 1), items.getValue(depts[i], k));
                orderParm.setData("TABLE_VALUE", orderParm.getCount("COL1") - 1,
                                  "HLine=Y;#0.Bold=Y;#0.FontSize=10;#1.Bold=Y;#2.Bold=Y;#3.Bold=Y");
            }
        }
        orderParm.setCount(orderParm.getCount("COL1"));
        orderParm.addData("SYSTEM", "COLUMNS", "COL1");
        orderParm.addData("SYSTEM", "COLUMNS", "COL2");
        orderParm.addData("SYSTEM", "COLUMNS", "COL3");
        orderParm.addData("SYSTEM", "COLUMNS", "COL4");
        return orderParm;
        //=========================modify by wanglong 20130131
    }
    
    /**
     * ȥ���������ظ��ļ�¼    add by wanglong 20130131
     * @param a
     * @return
     */
    public String[] arrayUnique(String[] a) {
        List<String> list = new LinkedList<String>();
        for (int i = 0; i < a.length; i++) {
            if (!list.contains(a[i])) {
                list.add(a[i]);
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
    
    /**
     * ��������ȡ��    add by wanglong 20130131
     * @return
     */
    public int ceil(int a, int b) {
        return (double) a / (double) b > a / b ? a / b + 1 : a / b;
    }
}
