package action.mro;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import jdo.mro.MROQlayControlMTool;
import jdo.mro.MROQlayControlDTool;
import jdo.mro.MROChrtvetrecTool;
import com.dongyang.data.TNull;
import java.sql.Timestamp;
import jdo.emr.GetWordValue;
import jdo.sys.MailVO;
import jdo.sys.MailUtil;
import jdo.sys.SYSPublishBoardTool;
import jdo.mro.MRORecordTool;

/**
 * <p>Title: �Զ��ʿ�</p>
 *
 * <p>Description: �Զ��ʿ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2011.5.3
 * @version 1.0
 */
public class MROQlayControlAction extends TAction {

    /**
     * ��ѯ����ʹ���Զ��ʿص������Ŀ
     */
    private String MRO_CHRTVETSTD_CHECK_SQL =
        " SELECT A.METHOD_CODE, A.METHOD_DESC, C.METHOD_TYPE_CODE, "
        + " C.METHOD_TYPE_DESC, B.EXAMINE_CODE, B.TYPE_CODE, B.EXAMINE_DESC, "
        + " B.ENNAME, B.DESCRIPTION, B.SCORE, B.URG_FLG, B.SPCFY_DEPT, "
        + " B.METHOD_PARM, B.CHECK_RANGE, B.CHECK_FLG, B.CHECK_SQL "
        + "FROM MRO_METHOD A, MRO_CHRTVETSTD B, MRO_METHOD_TYPE C "
        + " WHERE A.METHOD_CODE = B.METHOD_CODE "
        + " AND A.METHOD_TYPE_CODE = C.METHOD_TYPE_CODE "
        + " AND B.CHECK_FLG = 'Y' " //ɸѡ�Զ��ʿ�����Զ��ʿ���Ŀʱ����SQL���н����
        + " ORDER BY B.EXAMINE_CODE, B.TYPE_CODE ";

    /**
     * ��ѯ��Ӧ�Ĳ����б�
     */
    private String ADM_INP_PAT_QUERY = "SELECT A.CASE_NO, A.MR_NO, A.IPD_NO, "
                                     + " A.VS_DR_CODE , A.IN_DATE, A.DS_DATE "
                                     + " FROM ADM_INP A, MRO_RECORD B "
                                     + " WHERE A.CASE_NO =  B.CASE_NO "
                                     //+ " AND B.MRO_CHAT_FLG <> '2' "
                                     + " AND # ";

    /**
     * ��ѯSQL
     */
    private String QUERY_SQL = "SELECT # FROM # WHERE CASE_NO = '#'";

    /**
     * ��ѯ���Ӳ���SQL
     */
    private String QUERY_EMR_SQL =
        "SELECT # FROM EMR_FILE_INDEX WHERE CASE_NO = '#' AND CLASS_CODE LIKE '#%' ";

    /**
     * ��ѯ�ʿؼ�¼����
     */
    private String MRO_QLAYCONTROLM =
        "SELECT STATUS,CASE_NO,EXAMINE_CODE,QUERYSTATUS FROM MRO_QLAYCONTROLM ";

    /**
     * ��ѯ����δ���Ĺ�������Ϣ
     * ��ѯ������δͨ���Ѳ�״̬������
     */
    private String QUERY_BOARD_MESSAGE =
        "SELECT B.USER_NAME, B.E_MAIL, A.CASE_NO, A.EXAMINE_CODE, "
        + " A.EXAMINE_DATE, D.MR_NO, D.PAT_NAME, C.EXAMINE_DESC, "
        + " C.SCORE, B.ROLE_ID, B.USER_ID, C.URG_FLG "
        + " FROM MRO_CHRTVETREC A, SYS_OPERATOR B, "
        + " MRO_CHRTVETSTD C, SYS_PATINFO D,MRO_QLAYCONTROLM E "
        + " WHERE A.VS_CODE = B.USER_ID(+) "
        + " AND A.EXAMINE_CODE = C.EXAMINE_CODE "
        + " AND A.EXAMINE_CODE = E.EXAMINE_CODE "
        + " AND A.CASE_NO = E.CASE_NO "
        + " AND A.MR_NO = D.MR_NO "
        + " AND E.STATUS='0' AND E.QUERYSTATUS='1' "
        + " AND A.BOARD_STATUS = 'N'";

    /**
     * ��ѯ����δ���ʼ�
     * ��ѯ������δͨ���Ѳ�״̬������
     */
    private String QUERY_SEND_MESSAGE =
        "SELECT B.USER_NAME, B.E_MAIL, A.CASE_NO, A.EXAMINE_CODE, "
        + " A.EXAMINE_DATE, D.MR_NO, D.PAT_NAME, C.EXAMINE_DESC, "
        + " C.SCORE, B.ROLE_ID, B.USER_ID, C.URG_FLG "
        + " FROM MRO_CHRTVETREC A, SYS_OPERATOR B, "
        + " MRO_CHRTVETSTD C, SYS_PATINFO D,MRO_QLAYCONTROLM E "
        + " WHERE A.VS_CODE = B.USER_ID(+) "
        + " AND A.EXAMINE_CODE = C.EXAMINE_CODE "
        + " AND A.EXAMINE_CODE = E.EXAMINE_CODE "
        + " AND A.CASE_NO = E.CASE_NO "
        + " AND A.MR_NO = D.MR_NO "
        + " AND E.STATUS='0' AND E.QUERYSTATUS='1' "
        + " AND A.EMAIL_STATUS = 'N'";

    private TParm methodParm;//�õ����е��ʿط���
    private TParm parmValueM;//������Ϣ�б�鿴�Ƿ�������ʿ�
    private StringBuffer personName=new StringBuffer();//�Ѿ��ύ�Ĳ�����ʾ��������
    private TParm qlayMParm;//MRO_QLAYCONTROLM ���е���������
    private TParm checkParm;//��ѯ�Ƿ������õĵ��Ӳ�������ȱʧ��˵�checkParm
    
    public MROQlayControlAction() {
        // 1.��ѯ�Ƿ������õĵ��Ӳ�������ȱʧ���
        checkParm = new TParm(TJDODBTool.getInstance().select(MRO_CHRTVETSTD_CHECK_SQL));

    }
    
    /**
     * �Զ��ʿ�:ִ�����е��ʿط����Զ�ָ��ͳ��ҽ������
     * @param parm TParm
     * @return TParm
     * ===========pangben 20110801
     */
    public TParm onQlayControlMethod(TParm parm) {
        TConnection conn = getConnection();
        qlayMParm = new TParm(TJDODBTool.getInstance().select(
                MRO_QLAYCONTROLM + " WHERE CASE_NO IN (" + parm.getValue("CASE_NO") + ")"));//MRO_QLAYCONTROLM ���е����м������
        //�鿴���в�����Ϣ���ж��Ƿ��Ѿ�����ʿ�
        TParm parmTEMP = parm.getParm("parmTEMP");
        parmTEMP.setData("CASE_LIST", parm.getValue("CASE_NO"));
        parmValueM = MROQlayControlMTool.getInstance().queryQlayControlSUM(parmTEMP);//��MRO_RECORD�в�ѯ������TYPERESULT��SUMSCODE
        //����������ѯ��Ժ��Ժ������Ϣ
        methodParm = new TParm(TJDODBTool.getInstance().select(
                "SELECT METHOD_CODE, METHOD_DESC FROM MRO_METHOD ORDER BY METHOD_CODE, SEQ"));
        TParm result = null;
        for (int i = 0; i < methodParm.getCount(); i++) {//ѭ��ÿ���ʿ�����
            String method_code = methodParm.getValue("METHOD_CODE", i);
            result = onQlayControlMethodbyCode(method_code, parm, conn);//ִ���Զ�ָ�ط���
        }
        if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
            conn.rollback();//add by wanglong 20130819
            conn.close();
            result.setErrText("�Զ��ʿش���"+result.getErrText());
            return result;
        }
        conn.commit();
        conn.close();
        //=================����Ϊͳ��ÿ���������ܷ�=======================
        //��ѯ����ִ���Զ��ʿغ��˹��ʿصĻ����ܷ���
        conn=getConnection();
        TParm caseList = new TParm();// add by wanglong 20130819
        caseList.setData("CASE_LIST", parm.getValue("CASE_NO"));
        //�����ѯ��ǰ���л��ߵ�MR_NO,IPD_NO,CASE_NO
        TParm SumScode = MROChrtvetrecTool.getInstance().selectSumScode(caseList);//modify by wanglong 20130819
        //�����ѯ��ǰ���л��ߵ��ܷ֣�������ڼ�˼�¼������ʾ��ֵ������������ʾ�ܷ�Ϊ100��
        TParm SumScode1 = MROChrtvetrecTool.getInstance().selectSumScode1(caseList);
        TParm updateScode = null;
        for (int i = 0; i < parmValueM.getCount(); i++) {
            //TYPERESULT:Y ����ɵ�״̬�������޸� N δ��ɵ�״̬�ۼƷ����޸��ܷ֣��ڲ�����˵�MRO_CHRTVETREC �����ۼƷ�����
            if (parmValueM.getValue("TYPERESULT", i).equals("0")) {
                for (int j = 0; j < SumScode.getCount(); j++) {
                    //������ͬ�Ĳ���
                    if (SumScode.getValue("CASE_NO", j).equals(parmValueM.getValue("CASE_NO", i))
                     && SumScode.getValue("MR_NO", j).equals(parmValueM.getValue("MR_NO", i))) {
                        TParm parmRecore = new TParm();
                        parmRecore.setData("MR_NO", SumScode.getValue("MR_NO", j));
                        parmRecore.setData("CASE_NO", SumScode.getValue("CASE_NO", j));
                        parmRecore.setData("IPD_NO", SumScode.getValue("IPD_NO", j));
                        boolean isDeductScore = false;//�Ƿ��п۷�
                        // ���������ѯ������ �Ѳ� δͨ���������ۼ�
                        for (int z = 0; z < SumScode1.getCount(); z++) {
                            if (SumScode.getValue("CASE_NO", j).equals(SumScode1.getValue("CASE_NO", z))
                             && SumScode.getValue("MR_NO", j).equals(SumScode1.getValue("MR_NO", z))) {
                                parmRecore.setData("SUMSCODE", SumScode1.getDouble("SCODE", z));
                                isDeductScore = true;//�п۷�
                                break;
                            }
                        }
                        if(!isDeductScore) 
                            parmRecore.setData("SUMSCODE", 100);//û�п۷���ܷ�Ϊ100
                        updateScode = MRORecordTool.getInstance().updateSCODE(parmRecore, conn); //MRO_RECORD���޸��ܷ�
                        if (updateScode.getErrCode() < 0 || !"".equals(updateScode.getErrText())) {
                            conn.close();
                            result.setErrText("�޸ķ���ʱ����");
                            return result;
                        }
                        break;
                    }
                }
            }
        }
        conn.commit();
        conn.close();
        result.setData("personName",personName);//��ʾ����ʿصĲ����Ĳ�����
        return result;
    }
    
    /**
     * �Զ��ʿأ�ִ��ÿһ���ʿط���
     * @param method_code String
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * ===========pangben 20110801
     *
     */
    public TParm onQlayControlMethodbyCode(String method_code,TParm parm,TConnection conn){
        //������Ϣ
        TParm result = new TParm();
        // 1.ȡ���ʿط�������Ϣ
        String sql = "SELECT A.METHOD_TYPE_CODE, B.METHOD_TYPE_DESC, "
                     + " B.METHOD_TYPE_ENG_DESC, B.EMR_CHECK_NULL, B.EMR_CHECK_TIME, "
                     + " B.TABLE_CHECK_NULL, B.TABLE_CHECK_TIME, B.TIME_VALUE "
                     + " FROM MRO_METHOD A, MRO_METHOD_TYPE B "
                     + " WHERE A.METHOD_TYPE_CODE = B.METHOD_TYPE_CODE "
                     + " AND A.METHOD_CODE = '" + method_code + "'";
        TParm methodTypeParm = new TParm(TJDODBTool.getInstance().select(sql));//��ѯ�Զ��ʿص����ͣ��ܵ���˵��Ŀǰ��Ϊ4��
        // 2.���ݲ�ͬ����ִ���Զ��ʿ�
        TParm inparm = new TParm();
        inparm.setData("METHOD_CODE", method_code);
        inparm.setData("METHOD_TYPE_CODE", methodTypeParm.getValue("METHOD_TYPE_CODE", 0));
        inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
        inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        // �ж��Ƿ�Ϊָ���������Զ��ʿ�(���ָ�������ָ����Ա����ָ��)
        if (parm.existData("CASE_NO")) {
            inparm.setData("CASE_NO", parm.getValue("CASE_NO"));
        }
        if ("Y".equals(methodTypeParm.getValue("EMR_CHECK_NULL", 0))) {
            inparm.setData("EMR_CHECK_NULL", "Y");
            result = getEmrCheckNull(inparm, conn);// 2.1.���Ӳ�������ȱʧ���
        } else if ("Y".equals(methodTypeParm.getValue("TABLE_CHECK_NULL", 0))) {
            inparm.setData("TABLE_CHECK_NULL", "Y");
            result = getTableCheckNull(inparm, conn);// 2.2.���ݿ������ȱʧ���
        } else if ("Y".equals(methodTypeParm.getValue("EMR_CHECK_TIME", 0))) {
            inparm.setData("EMR_CHECK_TIME", "Y");
            inparm.setData("TIME_VALUE", methodTypeParm.getDouble("TIME_VALUE", 0));
            result = getEmrCheckTime(inparm, conn);// 2.3.���Ӳ���ʱЧ���
        } else if ("Y".equals(methodTypeParm.getValue("TABLE_CHECK_TIME", 0))) {
            inparm.setData("TABLE_CHECK_TIME", "Y");
            inparm.setData("TIME_VALUE", methodTypeParm.getDouble("TIME_VALUE", 0));
            result = getTableCheckTime(inparm, conn);// 2.4.���ݿ��ʱЧ���
        }
        return result;
    }
    
    /**
     * ���Ӳ�������ȱʧ���
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    private TParm getEmrCheckNull(TParm parm, TConnection conn) {
        TParm result = new TParm();
//        // 1.��ѯ�Ƿ������õĵ��Ӳ�������ȱʧ���
//        String sql = MRO_CHRTVETSTD_CHECK_SQL.replaceFirst("#", parm.getValue("METHOD_CODE"));
//        TParm checkParm = new TParm(TJDODBTool.getInstance().select(sql));
//        if (checkParm == null || checkParm.getCount() < 0) {
//              result.setErrText("û�����õĵ��Ӳ�������ȱʧ��ˣ�");
//              return result;
//        }
        String where = "";
      //============����Ϊ�Զ��ʿصĵ��ò���===============
        String method_parm = "";// ȡ�÷�������
        String file_name = "";// ���Ӳ���ģ����
        String tag_name = "";// �ֶ�����
        String query_condition = null;// ��ѡ����1����EMR_FILE_INDEX����ɸѡ�����ļ���where������ǰ���ؼ�and��
        String query_condition1 = null;// ��ѡ����2���ֶ����ƶ�Ӧ�ĺ�����
        String check_sql = "";// ���SQL
        
        // ��ѯ�����б�
        String CASE_NO = "";
        String MR_NO = "";
        String EXAMINE_DATE = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss");
        String STATUS = "0"; //ͨ��״̬��0:δͨ��   1:ͨ��
        for (int i = 0; i < checkParm.getCount(); i++) {
            query_condition = "";
            query_condition1 = "";
            if (checkParm.getValue("METHOD_CODE",
                i).equals(parm.getValue("METHOD_CODE"))) {
                // 2.���ݲ�ͬ�ļ����Ŀ��ѯ��Ӧ�Ĳ����б�
                if ("1".equals(checkParm.getValue("CHECK_RANGE", i))) {
                    where = " A.IN_DATE IS NOT NULL AND (A.DS_DATE IS NULL OR A.DS_DATE > SYSDATE) ";
                } else {
                    where = " A.DS_DATE IS NOT NULL AND A.DS_DATE <= SYSDATE ";
                }
                // ���SQL�� �����Ϊ�գ�ȡ����ֵ�����Ϊ�գ�ȡ��Ĭ��ֵ
                String sql = null;
                check_sql = checkParm.getValue("CHECK_SQL", i);//���SQL
                if ("".equals(check_sql)) {
                    sql = ADM_INP_PAT_QUERY.replaceFirst("#", where);
                } else {
                    sql = check_sql;
                }
                //ָ���������Զ��ʿ�(���ָ�������ָ����Ա����ָ��)
                if (parm.existData("CASE_NO")) {
                    //�ɴ˿��Կ��������SQL������������,������CASE_NO�ֶ�
                    sql += " AND A.CASE_NO IN (" + parm.getValue("CASE_NO") + ")";
                }
                TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));// ��ѯ������Ϣ
                method_parm = checkParm.getValue("METHOD_PARM", i);//���ò���
                String[] tag = method_parm.split(";");
                try {
                    file_name = tag[0];
                    tag_name = tag[1];
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    TParm errParm = new TParm();
                    errParm.setErr(-1, "�Զ��ʿز������ô���");
                    return errParm;
                }
                if (tag.length > 2) {
                    query_condition = tag[2];
                }
                if (tag.length > 3)
                    query_condition1 = tag[3]; //���ĸ������õ��������
                // 3.ѭ�������б�
                for (int j = 0; j < patParm.getCount(); j++) {
                    //��������б�
                    CASE_NO = patParm.getValue("CASE_NO", j);
                    MR_NO = patParm.getValue("MR_NO", j);
                    //�Ѿ���ɵ��ʿز����������ڲ���=======pangben modify 20110801
                    if (isCreate(MR_NO, CASE_NO)) {
                        if (!personName.toString().contains(MR_NO))
                            personName.append(MR_NO + ",\n");
                        continue;
                    }
                    //����Ƿ�ͨ����������
                    TParm tempStatus = tempStatusShow(CASE_NO, checkParm.getValue("EXAMINE_CODE", i));
                    if (null != tempStatus.getValue("status") &&
                        "1".equals(tempStatus.getValue("status"))) {// 4.2 ���ͨ��
                        continue;
                    }
                    boolean M_FLG = tempStatus.getBoolean("M_FLG");//��¼�½����Ǹ������ݣ�true�½���false���£�
                    // 5.��˵��Ӳ�������ȱʧ
                    sql = QUERY_EMR_SQL.replaceFirst("#", " FILE_PATH , FILE_NAME ")
                                       .replaceFirst("#", CASE_NO)
                                       .replaceFirst("#", file_name);
                    if (!"".equals(query_condition)) {
                        sql = sql + " AND " + query_condition;
                    }
                    TParm queryParm = new TParm(TJDODBTool.getInstance().select(sql));//���ҵ��Ӳ����ļ��Ĵ����Ϣ
                    if (queryParm.getErrCode() < 0) {
                        result.setErrText("��ѯ���Ӳ����ļ�����");
                        return result;
                    }
                    if (queryParm == null || queryParm.getCount("FILE_PATH") <= 0) {
                        STATUS = "0";// ���δͨ��
                    } else {
                        TParm tagParm = new TParm();
                        //===============ѭ�����������е����������һ����������ͨ����˽����ز�ͨ��
                        for(int index=0;index<queryParm.getCount();index++){
                            tagParm.setData("FILE_PATH", queryParm.getValue("FILE_PATH", index));
                            tagParm.setData("FILE_NAME", queryParm.getValue("FILE_NAME", index));
                            //�жϵ��Ӳ���TAGֵ�Ƿ����
                            STATUS = getEMRTag(tagParm, tag_name, query_condition1); //===pangben modify 20110901 ��ӵ���������
                            if("0".equals(STATUS)){
                                break;
                            }
                        }
                    }
                    //�����Զ��ʿؼ����Ϣ��۷���Ϣ���ֱ����MRO_QLAYCONTROLM��֮�����MRO_CHRTVETREC��
                    result = onInsertQlayTParm(patParm.getRow(j), checkParm.getRow(i),parm,
                                               EXAMINE_DATE,STATUS, M_FLG,result, conn);
                }
            }
        }
        return result;
    }

    /**
     * ���ݿ������ȱʧ���
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    private TParm getTableCheckNull(TParm parm, TConnection conn) {
        TParm result = new TParm();
//        // 1.��ѯ�Ƿ������õ����ݿ������ȱʧ���
//        String sql = MRO_CHRTVETSTD_CHECK_SQL.replaceFirst("#", parm.getValue("METHOD_CODE"));
//        TParm checkParm = new TParm(TJDODBTool.getInstance().select(sql));
//        if (checkParm == null || checkParm.getCount() < 0) {
//            result.setErrText("û�����õ����ݿ������ȱʧ��ˣ�");
//            return result;
//        }
        String where = "";
        //============����Ϊ�Զ��ʿصĵ��ò���===============
        String method_parm = "";// ȡ�÷�������
        String table_name = "";// ������
        String tag_name = "";// �ֶ�����
        String query_condition = "";// ��ѡ������ɸѡ�ֶε�where������ǰ���ؼ�and��
        String check_sql = "";// ���SQL
        
        // ��ѯ�����б�
        String CASE_NO = "";
        String MR_NO = "";
        String EXAMINE_DATE = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss");
        String STATUS = "0";
        for (int i = 0; i < checkParm.getCount(); i++) {
            // 2.���ݲ�ͬ�ļ����Ŀ��ѯ��Ӧ�Ĳ����б�
            query_condition = "";
            if (checkParm.getValue("METHOD_CODE",
                i).equals(parm.getValue("METHOD_CODE"))) {
                if ("1".equals(checkParm.getValue("CHECK_RANGE", i))) {
                    where = " A.IN_DATE IS NOT NULL AND (A.DS_DATE IS NULL OR A.DS_DATE > SYSDATE) ";
                } else {
                    where = " A.DS_DATE IS NOT NULL AND A.DS_DATE <= SYSDATE ";
                }
                // ���SQL�� �����Ϊ�գ�ȡ����ֵ�����Ϊ�գ�ȡ��Ĭ��ֵ
                String sql = null;
                check_sql = checkParm.getValue("CHECK_SQL", i);//���SQL
                if ("".equals(check_sql)) {
                    sql = ADM_INP_PAT_QUERY.replaceFirst("#", where);
                } else {
                    sql = check_sql;
                }
                //ָ���������Զ��ʿ�(���ָ�������ָ����Ա����ָ��)
                if (parm.existData("CASE_NO")) {
                    sql += " AND A.CASE_NO IN (" + parm.getValue("CASE_NO") + ")";
                }
                TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
                method_parm = checkParm.getValue("METHOD_PARM", i);//���ò���
                String[] tag = method_parm.split(";");
                try {
                    table_name = tag[0];
                    tag_name = tag[1];
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    TParm errParm = new TParm();
                    errParm.setErr(-1, "�Զ��ʿز������ô���");
                    return errParm;
                }
                if (tag.length > 2) {
                    query_condition = tag[2];
                }
                // 3.ѭ�������б�
                for (int j = 0; j < patParm.getCount(); j++) {
                    //��������б�
                    CASE_NO = patParm.getValue("CASE_NO", j);
                    MR_NO = patParm.getValue("MR_NO", j);
                    //�Ѿ���ɵ��ʿز����������ڲ���=======pangben modify 20110801
                    if (isCreate(MR_NO, CASE_NO)) {
                        if (!personName.toString().contains(MR_NO)) personName.append(MR_NO + ",\n");
                        continue;
                    }
                    //����Ƿ�ͨ����������
                    TParm tempStatus = tempStatusShow(CASE_NO, checkParm.getValue("EXAMINE_CODE", i));
                    if (null != tempStatus.getValue("status") &&
                        "1".equals(tempStatus.getValue("status"))) {// 4.2 ���ͨ��
                        continue;
                    }
                    boolean M_FLG = tempStatus.getBoolean("M_FLG");//��¼�½����Ǹ������ݣ�true�½���false���£�
                    // 5.������ݿ������ȱʧ
                    sql = QUERY_SQL.replaceFirst("#", tag_name)
                                   .replaceFirst("#", table_name)
                                   .replaceFirst("#", CASE_NO);
                    if (!"".equals(query_condition)) {
                        sql = sql + " AND " + query_condition;
                    }
                    TParm queryParm = new TParm(TJDODBTool.getInstance().select(sql));
                    if (queryParm.getErrCode() < 0) {
                        result.setErrText("��ѯ������");
                        return result;
                    }
                    if (queryParm == null || queryParm.getCount(tag_name) <= 0) {
                        STATUS = "0";// ���δͨ��
                    } else if ("".equals(queryParm.getValue(tag_name, 0))) {
                        STATUS = "0";// ���δͨ��
                    } else {
                        STATUS = "1";// ���ͨ��
                    }
                    //�����Զ��ʿؼ����Ϣ��۷���Ϣ���ֱ����MRO_QLAYCONTROLM��֮�����MRO_CHRTVETREC��
                    result = onInsertQlayTParm(patParm.getRow(j), checkParm.getRow(i),parm, 
                                               EXAMINE_DATE,STATUS, M_FLG,result, conn);
                }
            }
        }
        return result;
    }

    /**
     * ���Ӳ���ʱЧ���
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    private TParm getEmrCheckTime(TParm parm, TConnection conn) {
        TParm result = new TParm();
//        // 1.��ѯ�Ƿ������õĵ��Ӳ���ʱЧ���
//        String sql = MRO_CHRTVETSTD_CHECK_SQL.replaceFirst("#", parm.getValue("METHOD_CODE"));
//        TParm checkParm = new TParm(TJDODBTool.getInstance().select(sql));
//        if (checkParm == null || checkParm.getCount() < 0) {
//            result.setErrText("û�����õĵ��Ӳ���ʱЧ��ˣ�");
//            return result;
//        }

        String where = "";
      //============����Ϊ�Զ��ʿصĵ��ò���===============
        double TIME_VALUE = parm.getDouble("TIME_VALUE");// ʱЧ
        String method_parm = "";// ȡ�÷�������
        String file_name = "";// ���Ӳ���ģ����
        String tag_name = "";// �ֶ�����
        String query_condition = "";//��ѡ����1����EMR_FILE_INDEX����ɸѡ�����ļ���where������ǰ���ؼ�and��
        String query_condition1="";//��ѡ����2������Ŀǰ��������ֵ��û�д���ʱ��ʱЧ֮�����ʿسɹ���
        String check_sql = "";// ���SQL
        
        // ��ѯ�����б�
        String CASE_NO = "";
        String MR_NO = "";
        String EXAMINE_DATE = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss");
        String STATUS = "0";
        String IN_DS_DATE = "";// ��Ժʱ�仹�ǳ�Ժʱ��
        // ��ǰ����(ͨ��������ʱ�䣺��ȥ�������Ƶ�ʱ��)
        Timestamp date = StringTool.rollDate(SystemTool.getInstance().getDate(), (long) ( -1 * TIME_VALUE / 24));
        String SYS_DATE = StringTool.getString(date, "yyyyMMdd");
        for (int i = 0; i < checkParm.getCount(); i++) {
            query_condition = "";
            query_condition1 = "";
            if (checkParm.getValue("METHOD_CODE",
                i).equals(parm.getValue("METHOD_CODE"))) {
                // 2.���ݲ�ͬ�ļ����Ŀ��ѯ��Ӧ�Ĳ����б�
                if ("1".equals(checkParm.getValue("CHECK_RANGE", i))) {
                    where = " A.IN_DATE IS NOT NULL AND (A.DS_DATE IS NULL OR A.DS_DATE > SYSDATE) ";
                    IN_DS_DATE = "IN_DATE";
                } else {
                    where = " A.DS_DATE IS NOT NULL AND A.DS_DATE <= SYSDATE ";
                    IN_DS_DATE = "DS_DATE";
                }
                // ���SQL�� �����Ϊ�գ�ȡ����ֵ�����Ϊ�գ�ȡ��Ĭ��ֵ
                String sql = null;
                check_sql = checkParm.getValue("CHECK_SQL", i);//���SQL
                if ("".equals(check_sql)) {
                    sql = ADM_INP_PAT_QUERY.replaceFirst("#", where);
                } else {
                    sql = check_sql;
                    if (check_sql.indexOf("<START_DATE>") > 0) {
                        sql = sql.replaceFirst("<START_DATE>", SYS_DATE + "000000");
                    }
                    IN_DS_DATE = "CHECK_DATE";
                }
                //ָ���������Զ��ʿ�(���ָ�������ָ����Ա����ָ��)
                if (parm.existData("CASE_NO")) {
                    sql += " AND A.CASE_NO IN (" + parm.getValue("CASE_NO") + ")";
                }
                TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
                method_parm = checkParm.getValue("METHOD_PARM", i);//���ò���
                String[] tag = method_parm.split(";");
                try {
                    file_name = tag[0];
                    tag_name = tag[1];
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    TParm errParm = new TParm();
                    errParm.setErr(-1, "�Զ��ʿز������ô���");
                    return errParm;
                }
                if (tag.length > 2) {
                    query_condition = tag[2];
                }
                if (tag.length > 3) {
                    query_condition1 = tag[3]; //�������ƱȽϵ�ʱ��
                }
                // 3.ѭ�������б�
                for (int j = 0; j < patParm.getCount(); j++) {
                    
                    //��������б�
                    CASE_NO = patParm.getValue("CASE_NO", j);
                    MR_NO = patParm.getValue("MR_NO", j);
                    //�Ѿ���ɵ��ʿز����������ڲ���
                    if (isCreate(MR_NO, CASE_NO)) {
                        if (!personName.toString().contains(MR_NO))
                            personName.append(MR_NO + ",\n");
                        continue;
                    }
                    //����Ƿ�ͨ����������
                    TParm tempStatus = tempStatusShow(CASE_NO, checkParm.getValue("EXAMINE_CODE", i));
                    if (null != tempStatus.getValue("status") &&
                        "1".equals(tempStatus.getValue("status"))) {// 4.2 ���ͨ��
                        continue;
                    }
                    boolean M_FLG = tempStatus.getBoolean("M_FLG");//��¼�½����Ǹ������ݣ�true�½���false���£�
                    // 5.��˵��Ӳ���ʱЧ
                    sql = QUERY_EMR_SQL.replaceFirst("#", tag_name).
                          replaceFirst("#", CASE_NO).
                          replaceFirst("#", file_name);
                    if (!"".equals(query_condition)) {
                        sql = sql + " AND " + query_condition;
                    }
                    TParm queryParm = new TParm(TJDODBTool.getInstance().select(sql));
                    if (queryParm.getErrCode() < 0) {
                        result.setErrText("��ѯ������");
                        return result;
                    }
                    if (queryParm == null || queryParm.getCount(tag_name) <= 0) {
                        //����סԺ��ʱ��С��ʱЧ������ڼ���Ҫ������Ŀ���ܻ�û������������²�Ӧ�ÿ۷�
                        if (M_FLG == true) {// add by wanglong 20130909
                            if ("1".equals(checkParm.getValue("CHECK_RANGE", i))) {
                                Timestamp offset = rollHour(patParm.getTimestamp("IN_DATE", j), TIME_VALUE);
                                Timestamp now = SystemTool.getInstance().getDate();
                                if (offset.getTime() > now.getTime()) {
                                    continue;
                                }
                            } else {
                                Timestamp offset = rollHour(patParm.getTimestamp("DS_DATE", j), TIME_VALUE);
                                Timestamp now = SystemTool.getInstance().getDate();
                                if (offset.getTime() > now.getTime()) {
                                    continue;
                                }
                            }
                        }
                        STATUS = "0";// ���δͨ��
                    } else if ("".equals(queryParm.getValue(tag_name, 0))) {
                        STATUS = "0";// ���δͨ��
                    } else {
                        for (int index = 0; index < queryParm.getCount(); index++) {
                            Timestamp START_DATE = patParm.getTimestamp(IN_DS_DATE, j);// ��ʼʱ��
                            Timestamp END_DATE = queryParm.getTimestamp(tag_name, index);// ����ʱ��
                            START_DATE = rollHour(START_DATE, TIME_VALUE);// �����µĿ�ʼʱ��
                            //===============pangben 20110829 ��Ӵ��� ��ѯ�������ĸ����� ��ǰһ����ҵĲ���
                            if (!"".equals(query_condition1.trim())) {
                                if (START_DATE.getTime() <= END_DATE.getTime()) {
                                    STATUS = "1";// ���ͨ��
                                } else {
                                    STATUS = "0";// ���δͨ��
                                }
                                //===============pangben 20110829 stop
                            } else if ("".equals(query_condition1.trim())) {
                                if (START_DATE.getTime() >= END_DATE.getTime()) {
                                    STATUS = "1";// ���ͨ��
                                } else {
                                    STATUS = "0";// ���δͨ��
                                }
                            }
                            if("1".equals(STATUS)) break;//���ͨ���ͽ���
                        }
                    }
                    //�����Զ��ʿؼ����Ϣ��۷���Ϣ���ֱ����MRO_QLAYCONTROLM��֮�����MRO_CHRTVETREC��
                    result = onInsertQlayTParm(patParm.getRow(j), checkParm.getRow(i),parm,
                                               EXAMINE_DATE,STATUS, M_FLG,result, conn);
                }
            }
        }
        return result;
    }
    

    
    /**
     * ���ݿ��ʱЧ���
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    private TParm getTableCheckTime(TParm parm, TConnection conn) {
        TParm result = new TParm();
//        // 1.��ѯ�Ƿ������õ����ݿ��ʱЧ���
//        String sql = MRO_CHRTVETSTD_CHECK_SQL.replaceFirst("#", parm.getValue("METHOD_CODE"));
//        TParm checkParm = new TParm(TJDODBTool.getInstance().select(sql));
//        if (checkParm == null || checkParm.getCount() < 0) {
//            result.setErrText("û�����õ����ݿ��ʱЧ��ˣ�");
//            return result;
//        }

        String where = "";
        //============����Ϊ�Զ��ʿصĵ��ò���===============
        double TIME_VALUE = parm.getDouble("TIME_VALUE");// ʱЧ
        String method_parm = "";// ȡ�÷�������
        String table_name = "";// ������
        String tag_name = "";// �ֶ�����
        String query_condition = "";// ��ѡ������ɸѡ�ֶε�where������ǰ���ؼ�and��
        String query_condition1="";// ��ѡ����2������Ŀǰ��������ֵ��û�д���ʱ��ʱЧ֮�����ʿسɹ���
        String check_sql = "";// ���SQL
        
        // ��ѯ�����б�
        String CASE_NO = "";
        String MR_NO = "";
        String EXAMINE_DATE = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss");
        String STATUS = "0";
        String IN_DS_DATE = "";// ��Ժʱ�仹�ǳ�Ժʱ��
        // ��ǰ����(ͨ��������ʱ�䣺��ȥ�������Ƶ�ʱ��)
        Timestamp date = StringTool.rollDate(SystemTool.getInstance().getDate(), (long) ( -1 * TIME_VALUE / 24));
        String SYS_DATE = StringTool.getString(date, "yyyyMMdd");
        for (int i = 0; i < checkParm.getCount(); i++) {
            query_condition = "";
            query_condition1 = "";
            if (checkParm.getValue("METHOD_CODE",
                i).equals(parm.getValue("METHOD_CODE"))) {
                // 2.���ݲ�ͬ�ļ����Ŀ��ѯ��Ӧ�Ĳ����б�
                if ("1".equals(checkParm.getValue("CHECK_RANGE", i))) {
                    where = " A.IN_DATE IS NOT NULL AND (A.DS_DATE IS NULL OR A.DS_DATE > SYSDATE) ";
                    IN_DS_DATE = "IN_DATE";
                } else {
                    where = " A.DS_DATE IS NOT NULL AND A.DS_DATE <= SYSDATE ";
                    IN_DS_DATE = "DS_DATE";
                }
                // ���SQL�� �����Ϊ�գ�ȡ����ֵ�����Ϊ�գ�ȡ��Ĭ��ֵ
                check_sql = checkParm.getValue("CHECK_SQL", i);//���SQL
                String sql = null;
                if ("".equals(check_sql)) {
                    sql = ADM_INP_PAT_QUERY.replaceFirst("#", where);
                } else {
                    sql = check_sql;
                    if (check_sql.indexOf("<START_DATE>") > 0) {
                        sql = sql.replaceFirst("<START_DATE>", SYS_DATE + "000000");
                    }
                    IN_DS_DATE = "CHECK_DATE";
                }
                //ָ���������Զ��ʿ�(���ָ�������ָ����Ա����ָ��)
                if (parm.existData("CASE_NO")) {
                    sql += " AND A.CASE_NO IN (" + parm.getValue("CASE_NO") + ")";
                }
                TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
                method_parm = checkParm.getValue("METHOD_PARM", i);//���ò���
                String[] tag = method_parm.split(";");
                try {
                    table_name = tag[0];
                    tag_name = tag[1];
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    TParm errParm = new TParm();
                    errParm.setErr(-1, "�Զ��ʿز������ô���");
                    return errParm;
                }
                if (tag.length > 2) {
                    query_condition = tag[2];
                }
                if (tag.length > 3) {
                    query_condition1 = tag[3]; //�������ƱȽϵ�ʱ��
                }
                // 3.ѭ�������б�
                for (int j = 0; j < patParm.getCount(); j++) {
                    //��������б�
                    CASE_NO = patParm.getValue("CASE_NO", j);
                    MR_NO = patParm.getValue("MR_NO", j);
                    //�Ѿ���ɵ��ʿز����������ڲ���=======pangben modify 20110801
                    if (isCreate(MR_NO, CASE_NO)) {
                        if (!personName.toString().contains(MR_NO))
                            personName.append(MR_NO + ",\n");
                        continue;
                    }
                    //����Ƿ�ͨ����������
                    TParm tempStatus = tempStatusShow(CASE_NO, checkParm.getValue("EXAMINE_CODE", i));
                    if (null != tempStatus.getValue("status") &&
                        "1".equals(tempStatus.getValue("status"))) {// 4.2 ���ͨ��
                        continue;
                    }
                    boolean M_FLG = tempStatus.getBoolean("M_FLG");//��¼�½����Ǹ������ݣ�true�½���false���£�
                    // 5.������ݿ��ʱЧ
                    sql = QUERY_SQL.replaceFirst("#", tag_name)
                                   .replaceFirst("#", table_name)
                                   .replaceFirst("#", CASE_NO);
                    if (!"".equals(query_condition)) {
                        sql = sql + " AND " + query_condition;
                    }
                    TParm queryParm = new TParm(TJDODBTool.getInstance().select(sql));
                    if (queryParm.getErrCode() < 0) {
                        result.setErrText("��ѯ������");
                        return result;
                    }
                    if (queryParm == null || queryParm.getCount(tag_name) <= 0) {
                        //����סԺ��ʱ��С��ʱЧ������ڼ���Ҫ������Ŀ���ܻ�û������������²�Ӧ�ÿ۷�
                        if (M_FLG == true) {// add by wanglong 20130909
                            if ("1".equals(checkParm.getValue("CHECK_RANGE", i))) {
                                Timestamp offset = rollHour(patParm.getTimestamp("IN_DATE", j), TIME_VALUE);
                                Timestamp now = SystemTool.getInstance().getDate();
                                if (offset.getTime() > now.getTime()) {
                                    continue;
                                }
                            } else {
                                Timestamp offset = rollHour(patParm.getTimestamp("DS_DATE", j), TIME_VALUE);
                                Timestamp now = SystemTool.getInstance().getDate();
                                if (offset.getTime() > now.getTime()) {
                                    continue;
                                }
                            }
                        }
                        STATUS = "0";// ���δͨ��
                    } else if ("".equals(queryParm.getValue(tag_name, 0))) {
                        STATUS = "0";// ���δͨ��
                    } else {
                        //�������ò�����sql��䷵�صļ���
                        for (int index = 0; index < queryParm.getCount(); index++) {
                            Timestamp START_DATE = patParm.getTimestamp(IN_DS_DATE, j);// ��ʼʱ��
                            Timestamp END_DATE = queryParm.getTimestamp(tag_name, index);// ����ʱ��
                            START_DATE = rollHour(START_DATE, TIME_VALUE);// �����µĿ�ʼʱ��
                            //===============pangben 20110829 ��Ӵ��� ��ѯ�������ĸ����� ��ǰһ����ҵĲ���
                            if (query_condition1.trim().length() > 0) {
                                if (START_DATE.getTime() <= END_DATE.getTime()) {
                                    STATUS = "1";// ���ͨ��
                                } else {
                                    STATUS = "0";// ���δͨ��
                                }
                                //===============pangben 20110829 stop
                            } else {
                                if (START_DATE.getTime() >= END_DATE.getTime()) {
                                    STATUS = "1";// ���ͨ��
                                } else {
                                    STATUS = "0";// ���δͨ��
                                }
                            }
                            if("1".equals(STATUS)){
                                break; //���ͨ���ͽ�������
                            }
                        }
                    }
                    //�����Զ��ʿؼ����Ϣ��۷���Ϣ���ֱ����MRO_QLAYCONTROLM��֮�����MRO_CHRTVETREC��
                    result = onInsertQlayTParm(patParm.getRow(j), checkParm.getRow(i),parm,
                                               EXAMINE_DATE,STATUS, M_FLG,result, conn);
                }
            }
        }
        return result;
    }

    /**
     * �жϴ˲����Ƿ��Ѿ��ύ�ʿ�
     * @param mr_no String
     * @param case_no String
     * @return boolean
     */
    private boolean isCreate(String mr_no,String case_no){// �鿴MRO_RECORD���TYPERESULT�����Ƿ������
        boolean isTrue=false;
        for (int i = 0; i < parmValueM.getCount(); i++) {
            if (mr_no.equals(parmValueM.getValue("MR_NO", i)) &&
                case_no.equals(parmValueM.getValue("CASE_NO", i))) {
                isTrue="1".equals(parmValueM.getValue("TYPERESULT", i)) ? true : false;
                break;
            }
        }
        return isTrue;
    }
    
    /**
     * ���͹�����
     * @param parm TParm
     * @return TParm
     */
    public TParm onBoardMessage(TParm parm) {
        TParm result = new TParm();
        TParm caseParm = parm.getParm("CASE_NO");
        String caseSql = MROQlayControlMTool.getInstance().getInStatement("A.CASE_NO", caseParm);//add by wanglong 20130813
        // 1.��ѯδ���͵Ĺ�������Ϣ
        TParm sendMessage = new TParm(TJDODBTool.getInstance().select(QUERY_BOARD_MESSAGE + " AND (" + caseSql + ")"));//modify by wanglong 20130819
        if (sendMessage == null || sendMessage.getCount() <= 0) {
            result.setErrText("û��δ���͵���Ϣ��");
        }
        // 2.���͹�������Ϣ���Ҹ��¹�����״̬
        TParm resultMessage = sendBoard(sendMessage);
        String message = resultMessage.getValue("MESSAGE");
        TConnection conn = getConnection();
        for (int i = 0; i < resultMessage.getCount("CASE_NO"); i++) {
            TParm inparm = new TParm();
            inparm.setData("MESSAGE_NO", getMessageNo());
            inparm.setData("POST_TYPE", "P");
            inparm.setData("POST_GROUP", resultMessage.getValue("ROLE_ID", i));
            inparm.setData("USER_ID", resultMessage.getValue("USER_ID", i));
            inparm.setData("READ_FLG", "N");
            inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
            inparm.setData("OPT_DATE", SystemTool.getInstance().getDate());
            inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
            inparm.setData("POST_SUBJECT", resultMessage.getValue("TITLE", i));
            inparm.setData("URG_FLG", resultMessage.getValue("URG_FLG", i));
            inparm.setData("POST_INFOMATION", resultMessage.getValue("BODY", i));
            inparm.setData("RESPONSE_NO", 0);
            inparm.setData("POST_ID", parm.getValue("OPT_USER"));
            inparm.setData("POST_TIME", SystemTool.getInstance().getDate());
            inparm.setData("CASE_NO", resultMessage.getValue("CASE_NO", i));
            inparm.setData("EXAMINE_CODE", resultMessage.getValue("EXAMINE_CODE", i));
            inparm.setData("EXAMINE_DATE", resultMessage.getValue("EXAMINE_DATE", i));
            inparm.setData("BOARD_STATUS", "Y");
            result = SYSPublishBoardTool.getInstance().insertPostRCV(inparm, conn);
            if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
                conn.close();
                return result;
            }
            result = SYSPublishBoardTool.getInstance().insertBoard(inparm, conn);
            if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
                conn.close();
                return result;
            }
            result = MROChrtvetrecTool.getInstance().updateBoard(inparm, conn);
            if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
                conn.close();
                return result;
            }
        }
        conn.commit();
        conn.close();
        result.setData("MESSAGE", message);
        return result;
    }

    /**
     * �����ʼ�
     * @param parm TParm
     * @return TParm
     */
    public TParm onSendMessage(TParm parm) {
        TParm result = new TParm();
        TParm caseParm = parm.getParm("CASE_NO");
        String caseSql = MROQlayControlMTool.getInstance().getInStatement("A.CASE_NO", caseParm);//add by wanglong 20130813
        // 1.��ѯδ���͵��ʼ�
        TParm sendMessage = new TParm(TJDODBTool.getInstance().select(QUERY_SEND_MESSAGE + " AND (" + caseSql + ")"));//modify by wanglong 20130819
        if (sendMessage == null || sendMessage.getCount() <= 0) {
            result.setErrText("û��δ���͵��ʼ���");
        }
        // 1.�����ʼ�
        TParm resultMessage = sendEMail(sendMessage);
        String message = resultMessage.getValue("MESSAGE");
        // 2.�����ʼ�״̬
        TConnection conn = getConnection();
        for (int i = 0; i < resultMessage.getCount("CASE_NO"); i++) {
            TParm inparm = new TParm();
            inparm.setData("CASE_NO", resultMessage.getValue("CASE_NO", i));
            inparm.setData("EXAMINE_CODE", resultMessage.getValue("EXAMINE_CODE", i));
            inparm.setData("EXAMINE_DATE", resultMessage.getValue("EXAMINE_DATE", i));
            inparm.setData("EMAIL_STATUS", "Y");
            inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
            inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
            result = MROChrtvetrecTool.getInstance().updateEMail(inparm, conn);
            if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
                conn.close();
                return result;
            }
        }
        conn.commit();
        conn.close();
        result.setData("MESSAGE", message);
        return result;
    }

    /**
     * ������˼�¼
     * @param parmM TParm
     * @param parmC TParm
     * @param M_FLG boolean
     * @param STATUS String
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsertQlay(TParm parmM, TParm parmC, boolean M_FLG,
                              String STATUS, TConnection conn) {
        TParm result = new TParm();
        // 6.1 ��¼�ʿؼ�¼����
        if (M_FLG) {
            result = MROQlayControlMTool.getInstance().onInsert(parmM, conn);// �����ʿؼ�¼����
        } else {
            result = MROQlayControlMTool.getInstance().onUpdate(parmM, conn);// �����ʿؼ�¼����
        }
        if (result.getErrCode() < 0) {
            result.setErrText("�ʿؼ�¼������¼����");
            return result;
        }

//      6.2 ��¼�ʿؼ�¼��ϸ
//      TParm parmD = parmM;

//      result = MROQlayControlDTool.getInstance().onInsert(parmD, conn);
//      if (result.getErrCode() < 0) {
//          result.setErrText("�ʿؼ�¼��ϸ��¼����");
//          return result;
//      }
//      6.3 ��¼������˵�:��˲�ͨ������δ�۹��ֵļ�¼�۷�
        TParm inParm = new TParm();
        inParm.setData("CASE_NO", parmC.getValue("CASE_NO"));
        inParm.setData("EXAMINE_CODE", parmC.getValue("EXAMINE_CODE"));
        TParm queryParm = MROChrtvetrecTool.getInstance().selectChrData(inParm);//�ڿ۷ֱ��в�ѯ�۷����
        if (queryParm == null || queryParm.getCount("CASE_NO") <= 0) {
            STATUS = "0";//���δͨ��
        }
        else {
            STATUS = "1";//���ͨ��
        }
        if (!"1".equals(STATUS)) {
            result = MROChrtvetrecTool.getInstance().insertdata(parmC, conn);
            if (result.getErrCode() < 0) {
                result.setErrText("��¼������˵���¼����");
                return result;
            }
        }
        return result;
    }

    /**
     * ����ƫ��Сʱ
     * @param t Timestamp
     * @param hour double
     * @return Timestamp
     */
    public Timestamp rollHour(Timestamp t, double hour) {
        return new Timestamp(t.getTime() + (long) hour * 60 * 60 * 1000);
    }

    /**
     * �жϵ��Ӳ���TAGֵ�Ƿ����
     * @param parm TParm
     * @param tag String ����
     * @param great String ����
     * @return String
     */
    public String getEMRTag(TParm parm, String tag,String macroName) {
        TParm result = new TParm();
        result = GetWordValue.getInstance().getWordValueByName(parm, tag, macroName);
        //System.out.println("RESULT::::"+result);
        if (result == null || result.getErrCode() < 0) {
            return "0";
        }
        //===========pangben modify 20110710 start
        int count =0;
        //���macroName �����ƴ������ݽ�ͨ�������Ʋ�������
        if(null!=macroName && macroName.trim().length()>0)
            count=result.getCount(macroName);
        else
            count = result.getCount(tag);
        //System.out.println("count::::::"+count);
        if( count <= 0)
            return "0";
        for (int i = 0; i < count; i++) {
            if (null != macroName && macroName.trim().length() > 0){
                if (result.getValue(macroName + "_VALUE", i) == null ||
                    result.getValue(macroName + "_VALUE", i).length() == 0) {
                    return "0";
                }
            } else{
                if (result.getValue(tag + "_VALUE", i) == null ||
                    result.getValue(tag + "_VALUE", i).length() == 0) {
                    return "0";
                }
            }
        }
        //===========pangben modify 20110710 stop
        return "1";
    }

    /**
     * ���͹�������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm sendBoard(TParm parm) {
        String message = "";
        boolean flg = true;
        TParm result = new TParm();
        String title = "";
        String body = "";
        String dr_name = "";
        for (int i = 0; i < parm.getCount("E_MAIL"); i++) {
            title = "�Զ��ʿؼ��";
            dr_name = parm.getValue("USER_NAME", i);
            body = dr_name + "ҽ���� �����ţ�" + parm.getValue("MR_NO", i) + "(" +
                parm.getValue("PAT_NAME", i) + ") " +
                parm.getValue("EXAMINE_DESC", i) + "�� ��" +
                parm.getDouble("SCORE", i) + "��";
            result.addData("CASE_NO", parm.getValue("CASE_NO", i));
            result.addData("EXAMINE_CODE", parm.getValue("EXAMINE_CODE", i));
            result.addData("EXAMINE_DATE", parm.getValue("EXAMINE_DATE", i));
            result.addData("ROLE_ID", parm.getValue("ROLE_ID", i));
            result.addData("USER_ID", parm.getValue("USER_ID", i));
            result.addData("TITLE", title);
            result.addData("BODY", body);
            result.addData("URG_FLG", parm.getValue("URG_FLG", i));
            message += dr_name + (flg ? "��������Ϣ���ͳɹ�" : "��������Ϣ����ʧ��") + "\n";
        }
        result.setData("MESSAGE", message);
        return result;
    }


    /**
     * �����ʼ����ҷ���
     * @param parm TParm
     * @return TParm
     */
    public TParm sendEMail(TParm parm) {
        String message = "";
        boolean flg = true;
        TParm result = new TParm();
        String e_mail = "";
        String title = "";
        String body = "";
        String dr_name = "";
        for (int i = 0; i < parm.getCount("E_MAIL"); i++) {
            e_mail = parm.getValue("E_MAIL", i);
            title = "�Զ��ʿؼ��";
            dr_name = parm.getValue("USER_NAME", i);
            body = dr_name + "ҽ���� �����ţ�" + parm.getValue("MR_NO", i) + "(" +
                parm.getValue("PAT_NAME", i) + ") " +
                parm.getValue("EXAMINE_DESC", i) + "�� ��" +
                parm.getDouble("SCORE", i) + "��";

            flg = sendEMail(e_mail, title, body);
            if (flg) {
                result.addData("CASE_NO", parm.getValue("CASE_NO", i));
                result.addData("EXAMINE_CODE", parm.getValue("EXAMINE_CODE", i));
                result.addData("EXAMINE_DATE", parm.getValue("EXAMINE_DATE", i));
                result.addData("ROLE_ID", parm.getValue("ROLE_ID", i));
                result.addData("USER_ID", parm.getValue("USER_ID", i));
                result.addData("TITLE", title);
                result.addData("BODY", body);
                result.addData("URG_FLG", parm.getValue("URG_FLG", i));
            }
            message += dr_name + (flg ? "�ʼ����ͳɹ�" : "�ʼ�����ʧ��") + "\n";
        }

        result.setData("MESSAGE", message);
        return result;
    }

    /**
     * �����ʼ����ҷ���
     * @param email_address String
     * @param title String
     * @param body String
     * @return TParm
     */
    public boolean sendEMail(String email_address, String title, String body) {
        MailVO mail = new MailVO();
        mail.getToAddress().add(email_address);
        mail.setSubject(title);
        mail.setContent(body);
        TParm MailSendResult = MailUtil.getInstance().sendMail(mail);
        //System.out.println("MailSendResult----"+MailSendResult);
        if (MailSendResult.getErrCode() < 0) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * ȡ����ˮ��
     * @return String
     */
    private synchronized String getMessageNo() {
        String messageNo = "";
        messageNo = SystemTool.getInstance().getNo("ALL", "PUB", "MESSAGE_NO", "MESSAGE_NO");
        return messageNo;
    }

    /**
     * ����Ƿ�ͨ����������
     * @return boolean
     */
    private TParm tempStatusShow(String CASE_NO, String EXAMINE_CODE) {
        // 4.��ѯ�жϸü���Ƿ��Ѿ�ͨ��: ͨ����continue;ûͨ������м��
        TParm parm = new TParm();
        String status = null;
        String queryStatus=null;//�Ѳ�δ��״̬
        // �ж������Ƿ��������
        boolean M_FLG = true;
        if (null==qlayMParm || qlayMParm.getCount("STATUS") <= 0) {
            M_FLG = true;// 4.1 ��һ�ν��м��
        } else {
            //����MRO_QLAYCONTROLM ���������Ƿ����statusֵ
            for (int i = 0; i < qlayMParm.getCount(); i++) {
                if (CASE_NO.equals(qlayMParm.getValue("CASE_NO", i)) &&
                    EXAMINE_CODE.equals(qlayMParm.getValue("EXAMINE_CODE", i))) {
                    status = qlayMParm.getValue("STATUS", i);
                    queryStatus=qlayMParm.getValue("QUERYSTATUS", i);
                    break;
                }
            }
            if ((queryStatus == null) && (status == null)) {
                M_FLG = true;// �״ν��м��  modify by wanglong 20121029
            } else
            //��ִ�в���������1.�Ѿ�ͨ������Ŀ 2.δ������Ŀ
           // if ((null!=status && "1".equals(status)) || (null!=queryStatus && "1".equals(queryStatus))) {
            if ((null != status && "1".equals(status)) ) {//modify by wanglong 20121029
                parm.setData("status", "1");// 4.2 ���ͨ��
            } else {
                M_FLG = false;// 4.3 ���ûͨ����һ�ν��м��
            }
        }
        parm.setData("M_FLG",M_FLG);
        return parm;
    }
    
    /**
     * ִ���Զ�ָ�ط����Ĳ���
     * @param patParm TParm
     * @param parm TParm ����parm
     * @param tmpParm TParm ����parm
     * @param EXAMINE_DATE String ʱ��
     * @param STATUS String �Ƿ�۷�״̬
     * @param M_FLG Boolean ���״̬
     * @param result TParm ����ֵ
     * @param conn TConnection
     * @return TParm
     */
    private TParm onInsertQlayTParm(TParm patParm,TParm parm,TParm tmpParm,
                                    String EXAMINE_DATE, String STATUS,
                                    Boolean M_FLG, TParm result,
                                    TConnection conn) {

        // 6.������˼�¼
        TParm parmM = new TParm();
        parmM.setData("CASE_NO", patParm.getValue("CASE_NO"));
        parmM.setData("EXAMINE_CODE", parm.getValue("EXAMINE_CODE"));
        parmM.setData("EXAMINE_DATE", EXAMINE_DATE);
        parmM.setData("MR_NO", patParm.getValue("MR_NO"));
        parmM.setData("IPD_NO",patParm.getValue("IPD_NO") );
        parmM.setData("STATUS", STATUS);//�Ƿ�ͨ�����
        parmM.setData("QUERYSTATUS", "1"); //=============pangben modify 20110801 ��ǰ״̬��1.�Ѳ顢0.δ��
        parmM.setData("CHECK_RANGE", parm.getValue("CHECK_RANGE"));
        parmM.setData("CHECK_USER",tmpParm.getValue("OPT_USER"));
        parmM.setData("CHECK_DATE", "");
        parmM.setData("OPT_USER", tmpParm.getValue("OPT_USER"));
        parmM.setData("OPT_DATE", "");
        parmM.setData("OPT_TERM", tmpParm.getValue("OPT_TERM"));
        TParm parmC = new TParm();
        parmC.setData("CASE_NO", patParm.getValue("CASE_NO"));
        parmC.setData("EXAMINE_CODE",parm.getValue("EXAMINE_CODE"));
        parmC.setData("EXAMINE_DATE", EXAMINE_DATE.substring(0, 8));
        parmC.setData("IPD_NO", patParm.getValue("IPD_NO"));
        parmC.setData("MR_NO", patParm.getValue("MR_NO"));
        parmC.setData("VS_CODE",patParm.getValue("VS_DR_CODE") );
        parmC.setData("DEDUCT_SCORE", parm.getDouble("SCORE"));
        parmC.setData("DEDUCT_NOTE", "");
        parmC.setData("URG_FLG",null==parm.getValue("URG_FLG")?"N":parm.getValue("URG_FLG") );
        parmC.setData("REPLY_DTTM", new TNull(Timestamp.class));
        parmC.setData("REPLY_DR_CODE", "");
        parmC.setData("REPLY_REMK", "");
        parmC.setData("OPT_USER",tmpParm.getValue("OPT_USER"));
        parmC.setData("OPT_DATE", "");
        parmC.setData("OPT_TERM",tmpParm.getValue("OPT_TERM"));
        parmC.setData("DEDUCT_SCORECOUNT", 1); //========pangben modify 20110801  ���ÿ�����
        // ������˼�¼
        result = onInsertQlay(parmM, parmC, M_FLG, STATUS, conn);
        if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
            result.setErrText("������ݴ���");
            conn.rollback();//add by wanglong 20130819
            conn.close();
            return result;
        }
        conn.commit();//add by wanglong 20130819
        return result;
    }
    
    /**
     * ���Ĳ������ʿص÷ֺ����״̬Ϊ������ɡ�
     * @param parm TParm��������������״̬��
     * @return TParm
     */
    public TParm saveScoreAndState(TParm parm) {//add by wanglong 20121129
        TConnection conn = getConnection();
        TParm result = new TParm();
        TParm parms = new TParm();
        for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
            parms = parm.getRow(i);
            result = MRORecordTool.getInstance().updateTYPERESULT(parms, conn);
            conn.commit();
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    
    /**
     * �����ʿط���
     * @param parm
     * @return
     */
    public TParm updateScore(TParm parm) {//add by wanglong 20130819
        TConnection conn = getConnection();
        TParm result = new TParm();
        TParm parms = new TParm();
        for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
            parms = parm.getRow(i);
            TParm SumScode1 = MROChrtvetrecTool.getInstance().selectSumScode1(parms);//��ѯ����
            if (SumScode1.getErrCode() < 0) {
                err("ERR:" + SumScode1.getErrCode() + SumScode1.getErrText()
                    + SumScode1.getErrName());
                conn.close();
                return SumScode1;
            }
            TParm parmRecore= parm.getRow(i);
            parmRecore.setData("SUMSCODE", SumScode1.getDouble("SCODE", 0));
            result = MRORecordTool.getInstance().updateSCODE(parmRecore, conn); //MRO_RECORD���޸��ܷ�
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                conn.close();
                return result;
            }
            conn.commit();
        }
        conn.commit();
        conn.close();
        return result;
    }
}
