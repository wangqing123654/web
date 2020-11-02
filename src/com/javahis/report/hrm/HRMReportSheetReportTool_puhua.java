package com.javahis.report.hrm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jdo.sys.PatTool;
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
public class HRMReportSheetReportTool_puhua implements IReport {

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
        result.setData("PAT_NAME", patParm.getValue("PAT_NAME", 0));// ����
        result.setData("SEX_CODE", patParm.getValue("SEX_CODE", 0));// �Ա�
        result.setData("BIRTH_DATE", StringTool.getString(patParm.getTimestamp("BIRTHDAY", 0), "yyyy/MM/dd"));// ��������
        result.setData("PACKAGE_DESC", patParm.getValue("PACKAGE_DESC", 0));// �ײ�
        result.setData("BILL_FLG", patParm.getValue("BILL_FLG", 0).equals("Y") ? "��" : "��");// �շ�״̬
        String introUser = patParm.getValue("INTRO_USER", 0);
        String introUserName = StringUtil.getDesc("SYS_OPERATOR", "USER_NAME", "USER_ID='" + introUser + "'");
        result.setData("INTRO_USER", introUserName);// ������Ա
        String age = StringTool.CountAgeByTimestamp(patParm.getTimestamp("BIRTHDAY", 0), SystemTool.getInstance().getDate())[0];
        result.setData("AGE", age);// ����
        result.setData("IDNO", patParm.getValue("ID_NO", 0));// ֤����
        // ==============================��sys_patinfoȡ��Ա��������Ϣ add by wanglong 20130407
        TParm patInfo = PatTool.getInstance().getInfoForMrno(mrNo);
        if (patInfo.getErrCode() != 0) {
            // out.println(":patInfo is null");
            return null;
        }
        String nationCode = patInfo.getValue("NATION_CODE", 0);
        String nationDesc = StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC", "GROUP_ID = 'SYS_NATION' AND ID = '" + nationCode + "'");
        result.setData("NATION_DESC", nationDesc);// ����
        String speciesCode = patInfo.getValue("SPECIES_CODE", 0);
        String speciesDesc = StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC", "GROUP_ID = 'SYS_SPECIES' AND ID = '" + speciesCode + "'");
        result.setData("SPECIES_DESC", speciesDesc);// ����
        result.setData("COMPANY_DESC", patInfo.getValue("COMPANY_DESC", 0)); // ������λ
        result.setData("ADDRESS", patInfo.getValue("ADDRESS", 0)); // ͨѶ��ַ
        result.setData("E_MAIL", patInfo.getValue("E_MAIL", 0)); // �����ʼ�
        String telHome = patInfo.getValue("TEL_HOME", 0);
        String cellPhone = patInfo.getValue("CELL_PHONE", 0);
        result.setData("TEL", StringUtil.isNullString(cellPhone) ? telHome : cellPhone); // ��ϵ��ʽ
        String occCode = patInfo.getValue("OCC_CODE", 0);
        String occDesc = StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC", "GROUP_ID = 'SYS_OCCUPATION' AND ID = '" + occCode + "'");
        result.setData("OCC_DESC", occDesc);// ְҵ
        // =============================end
        result.setData("MR_NO", mrNo);
        result.setData("MR_CODE", "TEXT", mrNo);
        String packageCode = patParm.getValue("PACKAGE_CODE", 0);
        String reportlist = patParm.getValue("REPORTLIST", 0);
        String reportlistName = getReportlist(reportlist);
        if (reportlistName == null) {
            reportlistName = "";
        }
        result.setData("REPORTLIST", "���˳��" + reportlistName);
        //=============add by wanglong 20130620
        String isExistWomanSql =
                "SELECT * FROM HRM_ORDER WHERE CASE_NO='" + caseNo + "' AND DEPT_ATTRIBUTE='02'";
        TParm checkParm = new TParm(TJDODBTool.getInstance().select(isExistWomanSql));
        if (checkParm.getErrCode() < 0) {
            return null;
        }
        if (checkParm.getCount() > 0) {
            TParm womanParm = new TParm();
            womanParm.addData("COL1", "");
            womanParm.addData("COL2", "����:");
            womanParm.addData("COL3", "����:");
            womanParm.addData("COL4", "����:");
            womanParm.setData("TABLE_VALUE", 0, "HLine=Y");
            womanParm.addData("COL1", "");
            womanParm.addData("COL2", "����:");
            womanParm.addData("COL3", "����:");
            womanParm.addData("COL4", "��Ƭ��:");
            womanParm.setData("TABLE_VALUE", 1, "HLine=Y");
            womanParm.setCount(womanParm.getCount("COL1"));
            womanParm.addData("SYSTEM", "COLUMNS", "COL1");
            womanParm.addData("SYSTEM", "COLUMNS", "COL2");
            womanParm.addData("SYSTEM", "COLUMNS", "COL3");
            womanParm.addData("SYSTEM", "COLUMNS", "COL4");
            result.setData("WOMAN_TABLE", womanParm.getData());
            result.setData("LINE", "TEXT", "����������");
            result.setData("WOMAN", "����");
            result.setData("WOMAN", "TEXT", "����");
        }
        //=============add end
        TParm orderParm = this.getReportSheet(caseNo, packageCode);
        if (orderParm != null) {
            if (orderParm.getErrCode() != 0) {
                return null;
            }
        }
        if (orderParm.getData() != null) {
            orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            orderParm.addData("SYSTEM", "COLUMNS", "EXEC_DR");
            orderParm.addData("SYSTEM", "COLUMNS", "COL1");
            orderParm.addData("SYSTEM", "COLUMNS", "COL2");
            result.setData("ORDER_TABLE", orderParm.getData());
        }
        TParm deptParm = getDeptTParm(caseNo, packageCode);
        if (deptParm != null) {
            if (deptParm.getErrCode() != 0) {
                return deptParm;
            }
            if (deptParm.getData() != null) {
                result.setData("DEPT_TABLE", deptParm.getData());
            }
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
                        + "A.MARRIAGE_CODE,D.MARRIAGE_DESC,F.PAT_DEPT,A.REPORTLIST,A.INTRO_USER,A.BILL_FLG "
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
        String sql =
                "SELECT A.ORDER_DESC, '' EXEC_DR, '' COL1, '' COL2, A.ORDER_CODE, A.DR_NOTE "
                        + " FROM HRM_ORDER A  WHERE A.CASE_NO='#' AND A.SETMAIN_FLG = 'Y' "
                        + "  AND A.RPTTYPE_CODE IS NOT NULL AND ( A.CAT1_TYPE='LIS' OR A.CAT1_TYPE='RIS' ) "
                        + " ORDER BY A.ORDER_CODE";
        sql=sql.replaceFirst("#", caseNo);
        // System.out.println("xueyf sqlORDER======="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        // System.out.println("result===="+result);
        if (result.getErrCode() != 0) {
            // System.out.println("result.getErrCode="+result.getErrText());
            return null;
        }
        int count = result.getCount();
        // System.out.println("result.getcount="+result.getCount());
        Map map = new LinkedHashMap();
        for (int i = 0; i < count; i++) {
            String code = result.getValue("ORDER_CODE", i).substring(0, 3);
            result.addData("TABLE_VALUE", "HLine=Y;#0.Bold=N");
            String desc = getCateDesc(code);
            if (!map.containsValue(desc)) {
                map.put(i, desc);
            }
        }
        // System.out.println("map==="+map);
        Iterator it = map.keySet().iterator();
        List list = new ArrayList();
        while (it.hasNext()) {
            list.add(it.next());
        }
        // System.out.println("list====="+list);
        for (int i = list.size() - 1; i > -1; i--) {
            int row = StringTool.getInt(list.get(i) + "");
            // // System.out.println("row==="+row);
            result.insertData("ORDER_DESC", row, map.get(list.get(i)));
            result.insertData("EXEC_DR", row, "");
            result.insertData("COL1", row, "");
            result.insertData("COL2", row, "");
            result.insertData("TABLE_VALUE", row, "HLine=N;#0.Bold=Y;#0.FontSize=12");
            result.setCount(result.getCount("ORDER_DESC"));
        }
        // System.out.println("RRRR"+result);
        return result;
    }
    
    /**
     * 
     * @param caseNo
     * @return
     */
    private TParm getDeptTParm(String caseNo, String packageCode) {
        if (StringUtil.isNullString(caseNo)) {
            return null;
        }
        String sqlN =
                "SELECT B.CHN_DESC, '' OPT_USER, A.DR_NOTE FROM HRM_ORDER A, SYS_DICTIONARY B "
                        + " WHERE A.CASE_NO = '" + caseNo + "'"
                        + "   AND A.DEPT_ATTRIBUTE IS NOT NULL "
                        + "   AND A.DEPT_ATTRIBUTE = B.ID(+) "
                        + "   AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE' "
                        + "   AND A.SETMAIN_FLG = 'Y' ORDER BY A.SEQ_NO";
        // System.out.println("getDeptTParm.sql=="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlN));
        if (result.getErrCode() != 0) {
            // System.out.println("getDeptTParm.errText="+result.getErrText());
            return null;
        }
        int count = result.getCount();
        // System.out.println("getDeptTParm.result="+result);
        if (count <= 0) {
            // System.out.println("getDeptTParm.count<=0");
            result.setErrCode(-1);
            result.setErrText("���뿪���ܼ����͵�ҽ��");
            return null;
        }
        // if(count%2!=0){
        // result.addData("CHN_DESC","");
        // result.addData("OPT_USER","");
        // result.setCount(result.getCount("CHN_DESC"));
        // }
        count = result.getCount();
        TParm parm = new TParm();
        for (int i = 0; i < count; i++) {
            // if(i%2==0){
            parm.addData("DEPT1", result.getData("CHN_DESC", i));
            parm.addData("OPT_USER1", result.getData("OPT_USER", i));
            parm.addData("DR_NOTE", result.getData("DR_NOTE", i));
            // parm.addData("TABLE_VALUE","HLine=Y");
            // }else{
            // parm.addData("DEPT2", result.getData("CHN_DESC",i));
            // parm.addData("OPT_USER2", result.getData("OPT_USER",i));
            // }
        }
        parm.addData("SYSTEM", "COLUMNS", "DEPT1");
        parm.addData("SYSTEM", "COLUMNS", "OPT_USER1");
        parm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
        // parm.addData("SYSTEM","COLUMNS", "DEPT2");
        // parm.addData("SYSTEM","COLUMNS", "OPT_USER2");
        parm.setCount(parm.getCount("DEPT1"));
        return parm;
    }
    
    /**
     * ���ݸ��������ҽ����������
     * @param code
     * @return
     */
    private String getCateDesc(String code) {
        String desc = "";
        if (StringUtil.isNullString(code)) {
            return desc;
        }
        //��������ҽ����������
        String descSql = "SELECT CATEGORY_CHN_DESC FROM SYS_CATEGORY WHERE RULE_TYPE='EXM_RULE' AND CATEGORY_CODE='#'".replaceFirst("#", code);
        TParm descParm = new TParm(TJDODBTool.getInstance().select(descSql));
        if (descParm.getErrCode() != 0) {
            // System.out.println("getReportSheet.descSql.errText="+descParm.getErrText());
            // System.out.println("getReportSheet.descSql"+descSql);
            return desc;
        }
        desc = descParm.getValue("CATEGORY_CHN_DESC", 0);
        return desc;
    }
    
}
