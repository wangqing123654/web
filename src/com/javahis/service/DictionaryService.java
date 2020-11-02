package com.javahis.service;

import com.dongyang.Service.Server;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import java.util.ArrayList;
import java.util.Vector;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.TMessage;
import com.dongyang.action.TAction;
import com.dongyang.db.TConnection;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_AppServer;
public class DictionaryService {
    /**
     * ��ʼ��
     */
    public DictionaryService()
    {
        Server.autoInit(this);
//        TSocket socket = new TSocket("127.0.0.1",8080,"web");
//        TIOM_AppServer.SOCKET = socket;
    }
    /**
     * �õ��汾
     * @return String
     */
    public String getVersion(){
        return "JavaHis DictionaryService version is 1.0.1";
    }
    /**
     * �õ���������
     * @param code String
     * @param password String
     * @param tableName String
     * @return String[]
     */
    public String[] getData(String code,String password,String tableName)
    {
        String result = checkAuthority(code,password,tableName,"read");
        if(result.length() > 0)
            return new String[]{result};
        return TJDODBTool.getInstance().selectList("select ID,CHN_DESC,ENG_DESC from SYS_DICTIONARY where GROUP_ID='" + tableName + "' Order By ID");
    }
    /**
     * �õ�������Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getDeptInf(String code,String password)
    {
        String result = checkAuthority(code,password,"DEPT_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        return TJDODBTool.getInstance().selectList("select DEPT_CODE,DEPT_CHN_DESC,DEPT_ENG_DESC,DEPT_ABS_DESC from SYS_DEPT Order By DEPT_CODE");
    }
    /**
     * �õ�����
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getStation(String code,String password){
        String result = checkAuthority(code,password,"DEPT_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        return TJDODBTool.getInstance().selectList("SELECT STATION_CODE,STATION_DESC,PY1,PY2,ENG_DESC,DEPT_CODE,ORG_CODE FROM SYS_STATION ORDER BY STATION_CODE");
    }
    /**
     * ͬ����ɫ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getReulInf(String code,String password){
        String result = checkAuthority(code,password,"ROLE_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        return TJDODBTool.getInstance().selectList("SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='ROLE' Order By ID");
    }
    /**
     * �õ�������Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getDeptInfCode(String code,String password,String deptcode)
    {
        String result = checkAuthority(code,password,"DEPT_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        return TJDODBTool.getInstance().selectList("select DEPT_CODE,DEPT_CHN_DESC,DEPT_ENG_DESC,DEPT_ABS_DESC from SYS_DEPT WHERE DEPT_CODE='"+deptcode+"' Order By DEPT_CODE");
    }

    /**
     * �û���Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getOperatorInf(String code,String password)
    {
        String result = checkAuthority(code,password,"OPERATOR_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        return TJDODBTool.getInstance().selectList("SELECT A.USER_ID,A.USER_NAME,A.SEX_CODE,A.E_MAIL,A.ROLE_ID,B.DEPT_CODE from SYS_OPERATOR A,SYS_OPERATOR_DEPT B WHERE A.USER_ID=B.USER_ID AND B.MAIN_FLG='Y' Order By A.SEQ");
    }
    /**
     * �õ�ҩƷ���������
     * @return int[]
     */
    private int[] getPhaRule()
    {
        TParm parm = new TParm(TJDODBTool.getInstance().select("SELECT CLASSIFY1,CLASSIFY2,CLASSIFY3,CLASSIFY4,CLASSIFY5 FROM SYS_RULE WHERE  RULE_TYPE='PHA_RULE'"));
        return new int[]{parm.getInt("CLASSIFY1",0),
            parm.getInt("CLASSIFY2",0),
            parm.getInt("CLASSIFY3",0),
            parm.getInt("CLASSIFY4",0),
            parm.getInt("CLASSIFY5",0)};
    }
    /**
     * LISҽ����Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getLisOrder(String code,String password){
        String result = checkAuthority(code,password,"PHA_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        return TJDODBTool.getInstance().selectList("SELECT ORDER_CODE,ORDER_DESC from SYS_FEE WHERE CAT1_TYPE='LIS' AND ORDERSET_FLG='Y' Order By ORDER_CODE");
    }
    /**
     * ҩƷ�����ѯ
     * @param code String
     * @param password String
     * @param name String
     * @return String[]
     */
    public String[] getPhaClassify(String code,String password,String name)
    {
        String result = checkAuthority(code,password,"PHA_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        String sql =
            "SELECT CATEGORY_CODE,CATEGORY_CHN_DESC,CATEGORY_ENG_DESC FROM SYS_CATEGORY" +
            " WHERE RULE_TYPE='PHA_RULE' ";
        if(name == null || name.length() == 0)
            sql += "AND LENGTH(CATEGORY_CODE)=2";
        else
        {
            int[] pule = getPhaRule();
            int l = name.length();
            int count = 0;
            for(int i = 0;i < pule.length;i++)
            {
                count += pule[i];
                if(count > l)
                    break;
            }
            if(l >= count)
                return new String[]{};
            sql += "AND LENGTH(CATEGORY_CODE)=" + count+
                " AND CATEGORY_CODE LIKE '" + name + "%' ORDER BY CATEGORY_CODE";
        }
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * ҩƷ��ѯ
     * @param code String
     * @param password String
     * @param classify String
     * @return String[]
     */
    public String[] getPhaInf(String code,String password,String classify)
    {
        String result = checkAuthority(code,password,"PHA_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        if(classify == null || classify.length() == 0)
            return new String[]{};
        String sql=
            "SELECT A.ORDER_CODE,A.ORDER_DESC,A.TRADE_ENG_DESC,A.GOODS_DESC,A.ALIAS_DESC,"+
            "       A.SPECIFICATION,A.OWN_PRICE,B.UNIT_CHN_DESC,A.PY1,C.MAN_CHN_DESC"+
            "  FROM SYS_FEE A, SYS_UNIT B,SYS_MANUFACTURER C"+
            " WHERE A.ORDER_CODE LIKE '" + classify + "%' AND A.UNIT_CODE = B.UNIT_CODE(+) AND A.MAN_CODE=C.MAN_CODE(+)";
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * �õ�ҩƷ��Ϣ
     * @param code String
     * @param password String
     * @param orderCode String
     * @return String[]
     */
    public String[] getPhaOrder(String code,String password,String ordercode){
        String result = checkAuthority(code,password,"PHA_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        if(ordercode == null || ordercode.length() == 0)
            return new String[]{};
        String sql=
            "SELECT A.ORDER_CODE,A.ORDER_DESC,A.TRADE_ENG_DESC,A.GOODS_DESC,A.ALIAS_DESC,"+
            "       A.SPECIFICATION,A.OWN_PRICE,B.UNIT_CHN_DESC,A.PY1,C.MAN_CHN_DESC"+
            "  FROM SYS_FEE A, SYS_UNIT B,SYS_MANUFACTURER C"+
            " WHERE A.ORDER_CODE LIKE '" + ordercode + "%' AND A.UNIT_CODE = B.UNIT_CODE(+) AND A.MAN_CODE=C.MAN_CODE(+)";
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * סԺ��ҩ���õ�HISϵͳҩƷ�ֵ�(ͬ����)
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getODIPhaOrderInfo(String code,String password){
        String result = checkAuthority(code,password,"PHA_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        String sql="SELECT A.ORDER_CODE,A.ORDER_DESC,A.GOODS_DESC,A.TRADE_ENG_DESC,A.SPECIFICATION,B.UNIT_CHN_DESC,D.DOSE_CHN_DESC,A.PY1,'' AS MATERIAL_CHN_DESC,E.MAN_CHN_DESC "+
            " FROM SYS_FEE A,SYS_UNIT B,PHA_BASE C,PHA_DOSE D,SYS_MANUFACTURER E"+
            " WHERE A.CAT1_TYPE='PHA' AND A.ACTIVE_FLG='Y' AND A.UNIT_CODE=B.UNIT_CODE AND A.ORDER_CODE=C.ORDER_CODE(+) AND C.DOSE_CODE=D.DOSE_CODE(+)"+
            " AND A.MAN_CODE=E.MAN_CODE(+) ORDER BY A.ORDER_CODE";
        return TJDODBTool.getInstance().selectList(sql);
    }

    /**
     * סԺ��ҩ���õ�HISϵͳҩƷ�ֵ䵥ҽ����ѯ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getODIPhaOrderInfoItem(String code,String password,String ordercode){
        String result = checkAuthority(code,password,"PHA_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        if(ordercode == null || ordercode.length() == 0)
            return new String[]{"����ȱ�٣�ҩƷ���룡"};
        String sql="SELECT A.ORDER_CODE,A.ORDER_DESC,A.GOODS_DESC,A.TRADE_ENG_DESC,A.SPECIFICATION,B.UNIT_CHN_DESC,D.DOSE_CHN_DESC,A.PY1,'' AS MATERIAL_CHN_DESC,E.MAN_CHN_DESC "+
            " FROM SYS_FEE A,SYS_UNIT B,PHA_BASE C,PHA_DOSE D,SYS_MANUFACTURER E"+
            " WHERE A.ORDER_CODE='"+ordercode+"' AND A.CAT1_TYPE='PHA' AND A.ACTIVE_FLG='Y' AND A.UNIT_CODE=B.UNIT_CODE AND A.ORDER_CODE=C.ORDER_CODE(+) AND C.DOSE_CODE=D.DOSE_CODE(+)"+
            " AND A.MAN_CODE=E.MAN_CODE(+)";
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * ҩƷ��λ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getIndMaterialloc(String code,String password){
        String result = checkAuthority(code,password,"PHA_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        String sql="SELECT ORG_CODE,MATERIAL_LOC_CODE,MATERIAL_CHN_DESC,MATERIAL_ENG_DESC,PY1,DESCRIPTION FROM IND_MATERIALLOC ORDER BY ORG_CODE";
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * ҩƷ��λ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getIndMateriallocOrder(String code,String password,String orgcode,String ordercode){
        String result = checkAuthority(code,password,"PHA_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        if(orgcode == null || orgcode.length() == 0)
            return new String[]{"����ȱ�٣�ҩ�����룡(ͬ�����ұ���;��ҩ������ͨ��HIS������ҩ����ҩ������õ�)"};
        if(ordercode == null || ordercode.length() == 0)
            return new String[]{"����ȱ�٣�ҩƷ���룡"};
        String sql="SELECT A.MATERIAL_LOC_CODE,B.MATERIAL_CHN_DESC FROM IND_STOCKM A,IND_MATERIALLOC B WHERE A.ORG_CODE='"+orgcode+"' AND ORDER_CODE='"+ordercode+"' AND A.MATERIAL_LOC_CODE=B.MATERIAL_LOC_CODE(+)";
        return TJDODBTool.getInstance().selectList(sql);
    }

    /**
     * �õ���Һϵͳҽ����Ϣ
     * @param code String
     * @param password String
     * @param ordercode String
     * @return String
     */
    public String[] getPhaEsyOrder(String code,String password,String ordercode){
        String result = checkAuthority(code, password, "PHA_INF", "read");
        if (result.length() > 0)
            return new String[] {
                result};
        if (ordercode == null || ordercode.length() == 0)
            return new String[] {};
        String sql= "SELECT B.ORDER_DESC,B.ALIAS_DESC,A.MEDI_QTY,C.UNIT_CHN_DESC,D.UNIT_CHN_DESC,B.SPECIFICATION,B.PY1,B.ALIAS_PYCODE,A.ORDER_CODE,TO_CHAR(A.OPT_DATE,'YYYYMMDDHH24MISS') FROM PHA_BASE A,SYS_FEE B,SYS_UNIT C,SYS_UNIT D WHERE A.ORDER_CODE=B.ORDER_CODE AND A.ORDER_CODE='"+ordercode+"' AND A.MEDI_UNIT=C.UNIT_CODE AND A.DOSAGE_UNIT=D.UNIT_CODE";
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * �Ա��ֵ�ͬ��
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getSexInf(String code,String password){
        String result = checkAuthority(code, password, "SYS_SEX", "read");
        if (result.length() > 0)
            return new String[] {
                result};
        String sql= "SELECT CHN_DESC,ID,TO_CHAR(OPT_DATE,'YYYYMMDDHH24MISS') FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX' ORDER BY ID";
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * Ѫ��
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getBloodType(String code,String password){
        String result = checkAuthority(code, password, "SYS_BLOOD", "read");
        if (result.length() > 0)
            return new String[] {
                result};
        String sql= "SELECT CHN_DESC,ID,TO_CHAR(OPT_DATE,'YYYYMMDDHH24MISS') FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_BLOOD' ORDER BY ID";
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * ��ݣ��������
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getCtzInf(String code,String password){
        String result = checkAuthority(code, password, "SYS_CTZ", "read");
        if (result.length() > 0)
            return new String[] {
                result};
        String sql= "SELECT CTZ_DESC,CTZ_CODE,TO_CHAR(OPT_DATE,'YYYYMMDDHH24MISS') FROM SYS_CTZ ORDER BY CTZ_CODE";
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * �õ�������Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getDeptInfSY(String code,String password)
    {
        String result = checkAuthority(code,password,"DEPT_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        return TJDODBTool.getInstance().selectList("select DEPT_CODE,DEPT_CHN_DESC,TO_CHAR(OPT_DATE,'YYYYMMDDHH24MISS') from SYS_DEPT Order By DEPT_CODE");
    }

    /**
     * �õ�Ƶ��
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getPhaFreqInfSY(String code, String password) {
        String result = checkAuthority(code, password, "PHAFREQ_INF", "read");
        if (result.length() > 0)
            return new String[] {
                result};
        String sql =
            "SELECT FREQ_CHN_DESC,'��' AS FREQ_UNIT,CASE WHEN CYCLE=0 THEN 7 ELSE CYCLE END CYCLE,FREQ_TIMES,FREQ_CODE,TO_CHAR(OPT_DATE,'YYYYMMDDHH24MISS'),FREQ_UNIT_48 FROM SYS_PHAFREQ ORDER BY SEQ";
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * �õ���ҩ��ʽ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getRouteInf(String code, String password) {
        String result = checkAuthority(code, password, "ROUTE_INF", "read");
        if (result.length() > 0)
            return new String[] {
                result};
        String sql =
            "SELECT ROUTE_CHN_DESC,ROUTE_CODE,TO_CHAR(OPT_DATE,'YYYYMMDDHH24MISS') FROM SYS_PHAROUTE ORDER BY SEQ";
        return TJDODBTool.getInstance().selectList(sql);
    }

    /**
     * ͬ�����
     * @param code String
     * @param password String
     * @param admtype String
     * @param startdate String
     * @param enddate String
     * @return String[]
     */
    public String[] getRegWorkList(String code,String password,String admtype,String startdate,String enddate){
        String result = checkAuthority(code,password,"WORK_LIST","read");
        if(result.length() > 0)
            return new String[]{result};
        if(admtype == null || admtype.length() == 0||startdate==null||startdate.length()==0||enddate==null||enddate.length()==0)
            return new String[]{};
        String sql=
            "SELECT A.ADM_DATE,B.DEPT_CHN_DESC,D.CLINICROOM_DESC,C.USER_NAME,E.SESSION_DESC "+
            " FROM REG_SCHDAY A,SYS_DEPT B,SYS_OPERATOR C,REG_CLINICROOM D,REG_SESSION E "+
            " WHERE A.ADM_TYPE='"+admtype.toUpperCase()+"' "+
            " AND  A.CLINICROOM_NO=D.CLINICROOM_NO "+
            " AND A.DEPT_CODE=B.DEPT_CODE "+
            " AND DR_CODE=C.USER_ID "+
            " AND E.ADM_TYPE='"+admtype.toUpperCase()+"' "+
            " AND A.SESSION_CODE=E.SESSION_CODE "+
            " AND TO_DATE(ADM_DATE,'YYYYMMDDHH24MISS') BETWEEN TO_DATE('"+startdate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+enddate+"','YYYYMMDDHH24MISS')";
        System.out.println("sql"+sql);
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * �õ�ҩƷ��Ϣ(ƴ����)
     * @param code String
     * @param password String
     * @param py1 String
     * @param startrow int
     * @param endrow int
     * @return String[]
     */
    public String[] getPhaOrderPY1(String code,String password,String py1,int startrow,int endrow){
        String result = checkAuthority(code,password,"PHA_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        if(py1 == null || py1.length() == 0)
            return new String[]{};
        if(startrow==0)
            startrow=0;
        if(endrow==0)
            endrow=100000;
        String sql=
            "SELECT ORDER_CODE,ORDER_DESC,TRADE_ENG_DESC,GOODS_DESC,ALIAS_DESC,"+
            "       SPECIFICATION,OWN_PRICE,UNIT_CHN_DESC"+
            "  FROM SYS_FEE, SYS_UNIT"+
            " WHERE SYS_FEE.PY1 LIKE '%"+py1.toUpperCase()+"%' AND SYS_FEE.UNIT_CODE = SYS_UNIT.UNIT_CODE ORDER BY ORDER_CODE";

        return selectList(sql,startrow,endrow,"ORDER_CODE");
    }
    /**
     * �ֶβ�ѯ
     * @param sql String
     * @param startrow int
     * @param endrow int
     * @return String[]
     */
    public String[] selectList(String sql,int startrow,int endrow,String columnName)
    {
        TParm parm = new TParm(TJDODBTool.getInstance().select("",sql,true,startrow,endrow));
        if(parm.getErrCode() < 0)
            return new String[]{"ERR:" + parm.getErrCode() + " " + parm.getErrText()};
        ArrayList list = new ArrayList();
        int count = parm.getCount(columnName);
        Vector columns = (Vector)parm.getData("SYSTEM","COLUMNS");
        for(int i = 0;i < count;i++)
        {
            StringBuffer s = new StringBuffer();
            for(int j = 0;j < columns.size();j++)
            {
                String name = (String)columns.get(j);
                if(j > 0)
                    s.append(";");
                s.append("" + parm.getData(name,i));
            }
            list.add(s.toString());
        }
        return (String[])list.toArray(new String[]{});
    }
    /**
     * �ֶβ�ѯ
     * @param sql String
     * @param startrow int
     * @param endrow int
     * @return String[]
     */
    public String[] selectList(TParm parm,String columnName)
    {
        ArrayList list = new ArrayList();
        int count = parm.getCount(columnName);
        Vector columns = (Vector)parm.getData("SYSTEM","COLUMNS");
        for(int i = 0;i < count;i++)
        {
            StringBuffer s = new StringBuffer();
            for(int j = 0;j < columns.size();j++)
            {
                String name = (String)columns.get(j);
                if(j > 0)
                    s.append(";");
                s.append("" + parm.getData(name,i));
            }
            list.add(s.toString());
        }
        return (String[])list.toArray(new String[]{});
    }

    /**
     * �õ�Ƶ��
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getPhaFreqInf(String code, String password) {
         String result = checkAuthority(code,password,"PHAFREQ_INF","read");
         if(result.length() > 0)
            return new String[]{result};
        String sql = "SELECT FREQ_CODE,FREQ_CHN_DESC,FREQ_ENG_DESC FROM SYS_PHAFREQ ORDER BY SEQ";
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * �����Ϣ
     * @param code String
     * @param password String
     * @param classify String
     * @param type String
     * @return String[]
     */
    public String[] getDiagnosisInf(String code,String password,String classify,String type)
    {
        String result = checkAuthority(code,password,"DIAGNOSIS_INF","read");
        if(result.length() > 0)
            return new String[]{result};
//        if(classify == null || classify.length() == 0)
//            return new String[]{};
        String sql=
            "SELECT ICD_CODE,ICD_CHN_DESC,ICD_ENG_DESC,ICD_TYPE"+
            "  FROM SYS_DIAGNOSIS";
//            " WHERE ICD_CODE LIKE '" + classify + "%'";
        if(classify != null && classify.length() > 0){
            sql+=" WHERE ICD_CODE LIKE '" + classify + "%'";
        }
        if(type != null && type.length() > 0){
            if(sql.contains("WHERE")){
                sql += " AND ICD_TYPE='" + type + "'";
            }else{
                sql += " WHERE ICD_TYPE='" + type + "'";
            }
        }
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * �����ֵ�
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getSysOperationICD(String code,String password){
        String result = checkAuthority(code,password,"PHA_INF","read");
       if(result.length() > 0)
           return new String[]{result};
       String sql = "SELECT OPERATION_ICD,OPT_CHN_DESC,OPT_ENG_DESC FROM SYS_PHAFREQ ORDER BY SEQ";
       return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * ע����Ϣ
     * @param code String ���
     * @param chnDesc String ��������
     * @param engDesc String Ӣ������
     * @param contactsName String ��ϵ��
     * @param tel String �绰
     * @param email String mail
     * @param password String ����
     * @return String Success
     * ��������
     * http://zj:8080/axis2/services/DictionaryService/regist?code=LIS&chnDesc=����&engDesc=LIS&contactsName=test&tel=123&email=aa@qq.com&password=123
     */
    public String regist(String code,String chnDesc,String engDesc,String contactsName,String tel,String email,String password)
    {
        if(code == null || code.length() == 0)
            return "ERR:code is null";
        if(chnDesc == null || chnDesc.length() == 0)
            return "ERR:chnDesc is null";
        if(engDesc == null || engDesc.length() == 0)
            return "ERR:engDesc is null";
        if(contactsName == null || contactsName.length() == 0)
            return "ERR:contactsName is null";
        if(password == null || password.length() == 0)
            return "ERR:password is null";
        String sql = "SELECT * FROM SYS_IO_INF WHERE IO_CODE='" + code + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if(parm.getCount() > 0)
            return "ERR:" + code + " Exist";
        sql = "INSERT INTO SYS_IO_INF VALUES('" + code + "','" + chnDesc + "','" + engDesc + "',"+
            "'" + contactsName + "','" + tel + "','" + email + "','" + password + "',"+
            "'NEW','SYS_SERVICE',SYSDATE,'','','')";
        parm = new TParm(TJDODBTool.getInstance().update(sql));
        if(parm.getErrCode() != 0)
            return "ERR:" + parm.getErrText();
        return "Success";
    }
    /**
     * ��ȡע��״̬
     * @param code String
     * @return String
     * No Find û���ҵ�
     * NEW ����,��ȷ��
     * ENABLED ��Ч��
     * DISABLED ��Ч��
     */
    public String getRegistStatus(String code)
    {
        if(code == null || code.length() == 0)
            return "ERR:code is null";
        String sql = "SELECT * FROM SYS_IO_INF WHERE IO_CODE='" + code + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if(parm.getCount() <= 0)
            return "Err:No Find " + code;
        return parm.getValue("STATUS",0);
    }
    /**
     * �޸�����
     * @param code String
     * @param oldPassword String
     * @param newPassword String
     * @return String
     */
    public String modifyPassword(String code,String oldPassword,String newPassword)
    {
        if(code == null || code.length() == 0)
            return "ERR:code is null";
        if(oldPassword == null || oldPassword.length() == 0)
            return "ERR:oldPassword is null";
        if(newPassword == null || newPassword.length() == 0)
            return "ERR:newPassword is null";
        String sql = "SELECT * FROM SYS_IO_INF WHERE IO_CODE='" + code + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if(parm.getCount() <= 0)
            return "Err:No Find " + code;
        if(!oldPassword.equals(parm.getValue("PASSWORD",0)))
            return "Err:old password is not fitful";
        sql = "update SYS_IO_INF set PASSWORD='" + newPassword + "'";
        parm = new TParm(TJDODBTool.getInstance().update(sql));
        if(parm.getErrCode() != 0)
            return "ERR:" + parm.getErrText();
        return "Success";
    }
    /**
     * �õ������ֵ���Ϣ
     * @return String[]
     */
    public String[] getShareTable()
    {
        return TJDODBTool.getInstance().selectList("SELECT TABLE_NAME,TABLE_DESC,READ,WRITE,LISTEN FROM SYS_IO_SHARETABLE");
    }
    /**
     * ���Գ�������
     * @param code String
     * @param password String
     * @param flg boolean
     * @return String
     */
    private String checkCode(String code,String password,boolean flg)
    {
        if(code == null || code.length() == 0)
            return "ERR:code is null";
        if(password == null || password.length() == 0)
            return "ERR:password is null";
        String sql = "SELECT * FROM SYS_IO_INF WHERE IO_CODE='" + code + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if(parm.getCount() <= 0)
            return "Err:No Find " + code;
        if(!password.equals(parm.getValue("PASSWORD",0)))
            return "Err:password err";
        if(flg)
            if(!"ENABLED".equals(parm.getValue("STATUS",0)))
                return "Err:" + code + " is DISABLED";
        return "";
    }
    /**
     * ���Ա���
     * @param tableName String
     * @param action String
     * @return String
     */
    private String checkTable(String tableName,String action)
    {
        if(tableName == null || tableName.length() == 0)
            return "ERR:tableName is null";
        if(action == null || action.length() == 0)
            return "ERR:action is null (read or write or listen)";
        String sql = "SELECT READ,WRITE,LISTEN FROM SYS_IO_SHARETABLE WHERE TABLE_NAME='" + tableName.toUpperCase() + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if(parm.getErrCode() != 0)
            return "Err:No Find " + tableName;
        if(action.equalsIgnoreCase("read"))
            if (!"Y".equals(parm.getValue("READ", 0)))
                return "Err:not allowed read";
            else
                return "";
        if(action.equalsIgnoreCase("write"))
            if (!"Y".equals(parm.getValue("WRITE", 0)))
                return "Err:not allowed write";
            else
                return "";
        if(action.equalsIgnoreCase("listen"))
            if (!"Y".equals(parm.getValue("LISTEN", 0)))
                return "Err:not allowed listen";
            else
                return "";
        return "ERR:action is invalid (read or write or listen)";
    }
    /**
     * ע�����Ϣ
     * @param code String ���̱��
     * @param password String ����
     * @param tableName String ����
     * @param action String ����
     * @return String
     * ����
     * http://zj:8080/axis2/services/DictionaryService/registTable?code=LIS&password=aaa&tableName=PHA_INF&action=listen
     */
    public String registTable(String code,String password,String tableName,String action)
    {
        String result = checkCode(code,password,false);
        if(result.length() > 0)
            return result;
        result = checkTable(tableName,action);
        if(result.length() > 0)
            return result;
        tableName = tableName.toUpperCase();
        String sql = "SELECT * FROM SYS_IO_TABLE WHERE IO_CODE='" + code + "' AND TABLE_NAME='" + tableName + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if(parm.getCount() <= 0)
        {
            String s="";
            if(action.equalsIgnoreCase("read"))
                s = "'1','N','N'";
            else if(action.equalsIgnoreCase("write"))
                s = "'N','1','N'";
            else if(action.equalsIgnoreCase("listen"))
                s = "'N','N','1'";
            sql = "INSERT INTO SYS_IO_TABLE VALUES('" + code + "','" + tableName + "'," + s + ",'SYS_SERVICE',SYSDATE,'')";
        }else
        {
            String s="";
            if(action.equalsIgnoreCase("read"))
                s = "READ='1'";
            else if(action.equalsIgnoreCase("write"))
                s = "WRITE='1'";
            else if(action.equalsIgnoreCase("listen"))
                s = "LISTEN='1'";
            sql = "update SYS_IO_TABLE set " + s;

        }
        parm = new TParm(TJDODBTool.getInstance().update(sql));
        if(parm.getErrCode() != 0)
            return "ERR:" + parm.getErrText();
        return "Success";
    }
    /**
     * �õ���ע����Ϣ
     * @param code String
     * @param password String
     * @param tableName String
     * @return String[]
     */
    public String[] getRegistTableInf(String code,String password,String tableName)
    {
        String result = checkCode(code,password,false);
        if(result.length() > 0)
            return new String[]{result};
        String sql = "SELECT TABLE_NAME,READ,WRITE,LISTEN FROM SYS_IO_TABLE WHERE IO_CODE='" + code + "'";
        if(tableName != null && tableName.length() > 0)
            sql += " AND TABLE_NAME='" + tableName.toUpperCase() + "'";
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * ����Ȩ��
     * @param code String
     * @param password String
     * @param tableName String
     * @param action String
     * @return String
     */
    private String checkAuthority(String code,String password,String tableName,String action)
    {
        String result = checkCode(code,password,true);
        if(result.length() > 0)
            return result;
        String sql = "SELECT TABLE_NAME,READ,WRITE,LISTEN FROM SYS_IO_TABLE WHERE IO_CODE='" + code + "'" +
            " AND TABLE_NAME='" + tableName.toUpperCase() + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if(parm.getErrCode() != 0)
            return "Err:No Authority";
        if(action.equalsIgnoreCase("read"))
            if (!"Y".equals(parm.getValue("READ", 0)))
                return "Err:not allowed read";
            else
                return "";
        if(action.equalsIgnoreCase("write"))
            if (!"Y".equals(parm.getValue("WRITE", 0)))
                return "Err:not allowed write";
            else
                return "";
        if(action.equalsIgnoreCase("listen"))
            if (!"Y".equals(parm.getValue("LISTEN", 0)))
                return "Err:not allowed listen";
            else
                return "";
        return "ERR:action is invalid (read or write or listen)";
    }
    /**
     * �õ��б仯�ı�
     * @param code String
     * @param password String
     * @param status String
     * @return String[]
     */
    public String[] getModifyTable(String code,String password,String status)
    {
        String result = checkCode(code,password,true);
        if(result.length() > 0)
            return new String[]{result};
        return TJDODBTool.getInstance().selectList("select distinct TABLE_NAME from SYS_IO_LOG where IO_CODE='" + code + "' AND STATUS='" + status + "'");
    }
    /**
     * �õ��仯��Ϣ
     * @param code String
     * @param password String
     * @param status String
     * @param tableName String
     * @return String[]
     */
    public String[] getModifyInf(String code,String password,String status,String tableName)
    {
        String result = checkCode(code,password,true);
        if(result.length() > 0)
            return new String[]{result};
        String sql = "select TABLE_NAME,SEQ,ACTION,IO_DATA from SYS_IO_LOG where IO_CODE='" + code + "' AND STATUS='" + status + "'";
        if(tableName != null && tableName.length() > 0)
            sql += " AND TABLE_NAME='" + tableName + "'";
        sql += " order by TABLE_NAME,SEQ";
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * ȡ����Ϣ
     * @param code String
     * @param password String
     * @param tableName String
     * @param index String
     * @return String
     */
    public String fetchInf(String code,String password,String tableName,String index)
    {
        String result = checkCode(code,password,true);
        if(result.length() > 0)
            return result;
        if(tableName == null || tableName.length() == 0)
            return "ERR:tableName is null";
        if(index == null || index.length() == 0)
            return "ERR:index is null";
        try{
            Integer.parseInt(index);
        }catch(Exception e)
        {
            return "ERR:index type err(int)";
        }
        String sql = "select STATUS from SYS_IO_LOG"+
            " where IO_CODE='" + code + "' AND TABLE_NAME='" + tableName + "' AND SEQ=" + index;
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if(parm.getErrCode() != 0)
            return "ERR:no find";
        String status = parm.getValue("STATUS",0);
        if(!"NEW".equals(status))
            return "ERR:status is " + status;
        sql = "update SYS_IO_LOG set STATUS='FETCH'"+
            " where IO_CODE='" + code + "' AND TABLE_NAME='" + tableName + "' AND SEQ=" + index;
        parm = new TParm(TJDODBTool.getInstance().update(sql));
        if(parm.getErrCode() != 0)
            return "ERR:" + parm.getErrText();
        return "Success";
    }
    /**
     * ȷ��
     * @param code String
     * @param password String
     * @param tableName String
     * @param index String
     * @return String
     */
    public String confirmedInf(String code,String password,String tableName,String index)
    {
        String result = checkCode(code,password,true);
        if(result.length() > 0)
            return result;
        if(tableName == null || tableName.length() == 0)
            return "ERR:tableName is null";
        if(index == null || index.length() == 0)
            return "ERR:index is null";
        try{
            Integer.parseInt(index);
        }catch(Exception e)
        {
            return "ERR:index type err(int)";
        }
        String sql = "select STATUS from SYS_IO_LOG"+
            " where IO_CODE='" + code + "' AND TABLE_NAME='" + tableName + "' AND SEQ=" + index;
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if(parm.getErrCode() != 0)
            return "ERR:no find";
        String status = parm.getValue("STATUS",0);
        if("CONFIRMED".equals(status))
            return "ERR:status is " + status;
        sql = "update SYS_IO_LOG set STATUS='CONFIRMED'"+
            " where IO_CODE='" + code + "' AND TABLE_NAME='" + tableName + "' AND SEQ=" + index;
        parm = new TParm(TJDODBTool.getInstance().update(sql));
        if(parm.getErrCode() != 0)
            return "ERR:" + parm.getErrText();
        return "Success";
    }
    /**
     * ɾ��ͬ����Ϣ
     * @param code String
     * @param password String
     * @param tableName String
     * @param index String
     * @return String
     */
    public String deleteInf(String code,String password,String tableName,String index)
    {
        String result = checkCode(code,password,true);
        if(result.length() > 0)
            return result;
        String sql = "delete SYS_IO_LOG"+
            " where IO_CODE='" + code + "'";
        if(tableName != null && tableName.length() > 0)
            sql += " AND TABLE_NAME='" + tableName + "'";
        if(index != null && index.length() > 0)
        {
            try{
                Integer.parseInt(index);
            }catch(Exception e)
            {
                return "ERR:index type err(int)";
            }
            sql += " AND SEQ=" + index;
        }
        TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
        if(parm.getErrCode() != 0)
            return "ERR:" + parm.getErrText();
        return "Success";
    }
    /**
     * �û���Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getOperatorInfSY(String code,String password)
    {
        String result = checkAuthority(code,password,"OPERATOR_INF","read");
        if(result.length() > 0)
            return new String[]{result};
        TParm action = new TParm(TJDODBTool.getInstance().select("SELECT A.USER_ID,A.USER_NAME,A.USER_PASSWORD,B.DEPT_CODE,A.USER_ID AS ID,TO_CHAR(A.OPT_DATE,'YYYYMMDDHH24MISS'),C.POS_CHN_DESC FROM SYS_OPERATOR A,SYS_OPERATOR_DEPT B,SYS_POSITION C WHERE A.USER_ID=B.USER_ID AND B.MAIN_FLG='Y' AND A.POS_CODE=C.POS_CODE Order By A.SEQ"));
        int rowConut = action.getCount();
        TJDOTool decryptTool = new TJDOTool();
        for(int i=0;i<rowConut;i++){
            String pass = action.getValue("USER_PASSWORD",i);
            action.setData("USER_PASSWORD",i,decryptTool.decrypt(pass));
        }
        return this.selectList(action,"USER_ID");
    }
    /**
     * Ƥ�Ի�д�ż�ҽ����ע
     * @param code String
     * @param password String
     * @param caseNo String
     * @param rxNo String
     * @param seqNo String
     * @return String[]
     */
    public String[] readOpdOrderPS(String code,String password,String caseno,String rxno,String seqno,String value){
        if(caseno == null || caseno.length() == 0)
            return new String[]{"ERR:caseno is null"};
        if(rxno == null || rxno.length() == 0)
            return new String[]{"ERR:rxno is null"};;
        if(seqno == null || seqno.length() == 0)
            return new String[]{"ERR:seqno is null"};
        String result = checkAuthority(code,password,"OPD_ORDERINF","write");
        if(result.length() > 0)
            return new String[]{result};
        String sql = "UPDATE OPD_ORDER SET NS_NOTE='"+value+"' WHERE CASE_NO='"+caseno+"' AND RX_NO='"+rxno+"' AND SEQ_NO='"+seqno+"'";
        TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
        if(parm.getErrCode() != 0)
            return new String[]{"N"};
        return new String[]{"Y"};
    }
    /**
     * �õ��ż�ס��
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getAdmType(String code,String password){
        String result = checkAuthority(code, password, "SYS_ADMTYPE", "read");
        if (result.length() > 0)
            return new String[] {
                result};
        String sql= "SELECT CHN_DESC,ID,TO_CHAR(OPT_DATE,'YYYYMMDDHH24MISS') FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_ADMTYPE' ORDER BY ID";
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * �õ�ҽʦ�Ű�
     * @param code String
     * @param password String
     * @param admDate String ���� 20100921
     * @return String[]
     * ʱ��,�ż�ס��,����,����,ҽʦ
     */
    public String[] getRegSchDay(String code,String password,String admDate)
    {
        String result = checkAuthority(code, password, "REG_SCHDAY", "read");
        if (result.length() > 0)
            return new String[] {
                result};
        String sql="SELECT SESSION_CODE,ADM_TYPE,DEPT_CODE,CLINICROOM_NO,DR_CODE FROM "+
            "REG_SCHDAY WHERE ADM_DATE='" + admDate + "'";
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * �õ�������
     * @param code String
     * @param password String
     * @return String
     */
    public String[] getRegionJD(String code,String password){
        String result = checkAuthority(code, password, "PHL_HISREGION", "read");
        if (result.length() > 0)
            return new String[] {
                result};
        String sql= "SELECT REGION_CODE,REGION_DESC FROM PHL_REGION ORDER BY REGION_CODE";
        return TJDODBTool.getInstance().selectList(sql);
    }
    /**
     * �õ��ż�ҽ���Ƿ��˷�
     * @param code String
     * @param password String
     * @param caseno String
     * @param rxno String
     * @param seqno String
     * @return String[]
     */
    public String[] getHisCancelOrder(String code,String password,String caseno,String rxno,String seqno){
        String result = checkAuthority(code, password, "OPD_ORDERINF", "read");
        if (result.length() > 0)
            return new String[] {
                result};
        String sql= "SELECT BILL_FLG FROM OPD_ORDER CASE_NO='"+caseno+"' AND RX_NO='"+rxno+"' AND SEQ_NO='"+seqno+"'";
        TParm action = new TParm(TJDODBTool.getInstance().select(sql));
        if(action.getCount()<=0){
            return new String[]{"Y"};
        }else{
            if(action.getBoolean("BILL_FLG",0)){
                return new String[]{"N"};
            }else{
                return new String[]{"Y"};
            }
        }
    }
    /**
     * �õ�����������Ϣ�ż�ҽ����Ϣ
     * @param code String
     * @param password String
     * @param rxno String
     * @return String[]
     */
    public String[] getPatInfAndOrder(String code,String password,String rxno){
        //100113000062
        String result = checkAuthority(code, password, "OPERATOR_INF", "read");
        if (result.length() > 0)
            return new String[] {
                result};
        //ҽ����Ϣ
        TParm orderParm = new TParm(TJDODBTool.getInstance().select("SELECT * FROM OPD_ORDER WHERE RX_NO='"+rxno+"'"));
        if(orderParm.getCount()<=0){
            return new String[]{"N"};
        }
        String mrNo = orderParm.getValue("MR_NO",0);
        String caseNo =orderParm.getValue("CASE_NO",0);
        String admType = orderParm.getValue("ADM_TYPE",0);
        String deptCode = orderParm.getValue("DEPT_CODE",0);
        String drugStr = "";
        //����������Ϣ
        TParm patInfParm = new TParm(TJDODBTool.getInstance().select("SELECT C.PAT_NAME,C.MR_NO,C.SEX_CODE,C.CTZ1_CODE,TO_CHAR(C.BIRTH_DATE,'YYYYMMDDHH24MISS'),C.IDNO,C.TEL_HOME,C.ADDRESS,C.BLOOD_TYPE,C.MR_NO AS VCBINGAN,TO_CHAR(A.ADM_DATE,'YYYYMMDDHH24MISS') AS ADM_DATE,A.CASE_NO,TO_CHAR(A.REG_DATE,'YYYYMMDDHH24MISS') AS REG_DATE,0 AS COUNT,B.PHL_REGION_CODE FROM REG_PATADM A,REG_CLINICROOM B,SYS_PATINFO C WHERE A.CLINICROOM_NO=B.CLINICROOM_NO AND A.MR_NO=C.MR_NO AND  CASE_NO='"+caseNo+"'"));
        if (patInfParm.getCount() <= 0) {
            return new String[] {
                "N"};
        }
        //����ʷ
        TParm drugParm = new TParm(TJDODBTool.getInstance().select("SELECT A.DRUG_TYPE,B.CHN_DESC,A.DRUGORINGRD_CODE FROM OPD_DRUGALLERGY A,SYS_DICTIONARY B WHERE A.DRUG_TYPE=B.ID AND MR_NO='"+mrNo+"' AND B.GROUP_ID='SYS_ALLERGY'"));
        if(drugParm.getCount()<=0){
            drugStr = "";
        }else{
            int rowCount = drugParm.getCount("DRUG_TYPE");
            for(int i=0;i<rowCount;i++){
                TParm temp = drugParm.getRow(i);
                if("A".equals(temp.getValue("DRUG_TYPE"))){
                    drugStr += temp.getValue("CHN_DESC")+":"+getDictionary("PHA_INGREDIENT",temp.getValue("DRUGORINGRD_CODE"));
                }
                if("C".equals(temp.getValue("DRUG_TYPE"))){
                    drugStr += temp.getValue("CHN_DESC")+":"+getDictionary("SYS_ALLERGYTYPE",temp.getValue("DRUGORINGRD_CODE"));
                }
            }
        }
        TParm tem = patInfParm.getRow(0);
        String[] patRes = new String[]{tem.getValue("PAT_NAME")+";"+tem.getValue("MR_NO")+";"+tem.getValue("SEX_CODE")+";"+tem.getValue("CTZ1_CODE")+";"+tem.getValue("BIRTH_DATE")+";"+tem.getValue("IDNO")+";"+tem.getValue("TEL_HOME")+";"+tem.getValue("ADDRESS")+";"+drugStr+";"+mrNo+";"+tem.getValue("BLOOD_TYPE")+";"+deptCode+";"+tem.getValue("ADM_DATE")+";"+caseNo+";"+tem.getValue("REG_DATE")+";"+tem.getValue("COUNT")+";"+tem.getValue("PHL_REGION_CODE")+";"+admType};
//        String[] orderOE = TJDODBTool.getInstance().selectList("SELECT 'ORDER_INF' AS ORDER_INF,A.CASE_NO,A.RX_NO,A.LINK_NO,A.SEQ_NO,A.ORDER_CODE,A.DISPENSE_QTY,A.DISPENSE_UNIT,A.MEDI_QTY,A.ROUTE_CODE,A.FREQ_CODE,NVL(B.PS_FLG,'N') AS PS_FLG,A.DR_CODE,TO_CHAR(A.ORDER_DATE,'YYYYMMDDHH24MISS') AS ORDER_DATE,A.TAKE_DAYS,CASE_NO||RX_NO||SEQ_NO AS ROWIDNUM,TO_CHAR(A.OPT_DATE,'YYYYMMDDHH24MISS') AS OPT_DATE,0 AS NINTIMES FROM OPD_ORDER A,SYS_PHAROUTE B WHERE A.ROUTE_CODE=B.ROUTE_CODE AND A.RX_NO='"+rxno+"' AND A.DOSE_TYPE IN ('I','F')");
//        patRes = copyArray(patRes,orderOE);
        return patRes;
    }
    /**
     * �õ���Һҽ����Ϣ
     * @param code String
     * @param password String
     * @param rxno String
     * @return String[]
     */
    public String[] getOrderSY(String code,String password,String rxno){
        String result = checkAuthority(code, password, "OPD_ORDERINF", "read");
        if (result.length() > 0)
            return new String[] {
                result};
        String[] orderOE = TJDODBTool.getInstance().selectList("SELECT A.CASE_NO,A.RX_NO,A.LINK_NO,A.SEQ_NO,A.ORDER_CODE,A.DISPENSE_QTY,C.UNIT_CHN_DESC,A.MEDI_QTY,A.ROUTE_CODE,A.FREQ_CODE,NVL(B.PS_FLG,'N') AS PS_FLG,A.DR_CODE,TO_CHAR(A.ORDER_DATE,'YYYYMMDDHH24MISS') AS ORDER_DATE,A.TAKE_DAYS,CASE_NO||RX_NO||SEQ_NO AS ROWIDNUM,TO_CHAR(A.OPT_DATE,'YYYYMMDDHH24MISS') AS OPT_DATE,0 AS NINTIMES FROM OPD_ORDER A,SYS_PHAROUTE B,SYS_UNIT C WHERE A.ROUTE_CODE=B.ROUTE_CODE AND A.RX_NO='"+rxno+"' AND A.DOSE_TYPE IN ('I','F') AND A.DISPENSE_UNIT=C.UNIT_CODE ");
        return orderOE;
    }
    /**
     * �����õ�����������Ϣ�ż�ҽ����Ϣ
     * @param code String
     * @param password String
     * @param rxno String
     * @return String[]
     */
    public String[] getPatInfAndOrderTest(String code,String password,String rxno){
        if(!"ESY".equals(code)){
           return new String[]{"ERR:"+code+" System no Find!"};
       }
       if(!"123".equals(password)){
           return new String[]{"ERR: Password is err!"};
       }
       String[] str = new String[]{"��һ;000000000315;1;99;;12010219830519323X;;;;000000000340;;10101;20100406;100113000008;20100406;0;002;O"};
       return str;
    }
    /**
     * ���ҽ����Ϣ
     * @param code String
     * @param password String
     * @param rxno String
     * @return String[]
     */
    public String[] getOrderSYTest(String code,String password,String rxno){
        if(!"ESY".equals(code)){
           return new String[]{"ERR:"+code+" System no Find!"};
       }
       if(!"123".equals(password)){
           return new String[]{"ERR: Password is err!"};
       }
       String[] str = new String[]{ "100113000008;100113000062;1;1;1AAAA001;1.0;֧;1.0;IVD;BID;N;D001;20100113;1;1001130000081001130000622;20100113;0","100113000008;100113000062;1;2;1AAAA002;1.0;֧;1.0;IVD;BID;N;D001;20100113;1;1001130000081001130000621;20100113;0"};
       return str;

    }
    /**
     * �����õ���Һϵͳҽ����Ϣ
     * @param code String
     * @param password String
     * @param ordercode String
     * @return String[]
     */
    public String[] getPhaEsyOrderTest(String code,String password,String ordercode){
        if(!"ESY".equals(code)){
            return new String[]{"ERR:"+code+" System no Find!"};
        }
        if(!"123".equals(password)){
            return new String[]{"ERR: Password is err!"};
        }
        String[] str = new String[]{"��������;;0.0;g;g;;FMXN;;1VA00004;20100315"};
        return str;
    }
    /**
     * �Ա����
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getSexInfTest(String code,String password){
        if(!"ESY".equals(code)){
           return new String[]{"ERR:"+code+" System no Find!"};
       }
       if(!"123".equals(password)){
           return new String[]{"ERR: Password is err!"};
       }
       String[] str = new String[]{"��;1;20090101","Ů;2;20090101"};
       return str;
    }
    /**
     * Ѫ�Ͳ���
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getBloodTypeTest(String code,String password){
        if(!"ESY".equals(code)){
            return new String[]{"ERR:"+code+" System no Find!"};
        }
        if(!"123".equals(password)){
            return new String[]{"ERR: Password is err!"};
        }
        String[] str = new String[]{"A;A;20090101","AB;AB;20090101","B;B;20090101","O;O;20090101"};
        return str;
    }
    /**
     * ��ݣ�������𣩲���
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getCtzInfTest(String code,String password){
        if(!"ESY".equals(code)){
            return new String[]{"ERR:"+code+" System no Find!"};
        }
        if(!"123".equals(password)){
            return new String[]{"ERR: Password is err!"};
        }
        String[] str = new String[]{"���;01;20100406","���ʹ��;02;20100406","�Է�;99;20100406"};
        return str;
    }
    /**
     * �õ�������Ϣ����
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getDeptInfSYTest(String code,String password){
        if(!"ESY".equals(code)){
           return new String[]{"ERR:"+code+" System no Find!"};
       }
       if(!"123".equals(password)){
           return new String[]{"ERR: Password is err!"};
       }
       String[] str = new String[]{"C10401;������Ϣ����;20100322","C20101;��Ѫ���ڿ�;20100330","C20102;��Ѫ�����;20100308"};
       return str;
    }

    /**
     * �õ�Ƶ��
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getPhaFreqInfSYTest(String code, String password) {
        if (!"ESY".equals(code)) {
            return new String[] {
                "ERR:" + code + " System no Find!"};
        }
        if (!"123".equals(password)) {
            return new String[] {
                "ERR: Password is err!"};
        }
        String[] str = new String[] {
            "����һСʱ;��;1;24;AW1;20051222",
            "3��/��;��;1;3;TIDA;20090412",
            "����/��;��;1;2;BID;20091020",
            "һ��1��;��;7;1;W1D7;20081218",
            "һ��1��;��;7;1;W1D6;20081218"};
        return str;
    }
    /**
     * �õ���ҩ��ʽ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getRouteInfTest(String code, String password) {
        if (!"ESY".equals(code)) {
           return new String[] {
               "ERR:" + code + " System no Find!"};
       }
       if (!"123".equals(password)) {
           return new String[] {
               "ERR: Password is err!"};
       }
       String[] str = new String[] {
           "�ζ�;AD;20090101",
           "����;ZZ;20090101",
           "���;AS;20090902",
           "Ƥ��ע��;ID;20090101",
           "Ƥ��ע��;IH;20090101"};
       return str;
    }

    /**
     * �û���Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getOperatorInfSYTest(String code, String password) {
        if (!"ESY".equals(code)) {
           return new String[] {
               "ERR:" + code + " System no Find!"};
       }
       if (!"123".equals(password)) {
           return new String[] {
               "ERR: Password is err!"};
       }
       String[] str = new String[] {
           "0707001;������;1;C30501;0707001;20100407",
           "0710001;��鰮;1;C20803;0710001;20100407",
           "0801001;���;1;C30301;0801001;20100406",
           "0805001;�޾�ȫ;1;C10401;0805001;20100326",
           "0809001;����;1;C20101;0809001;20100407"};
       return str;
    }
    /**
     * �õ��ż�ҽ���Ƿ��˷�
     * @param code String
     * @param password String
     * @param caseno String
     * @param rxno String
     * @param seqno String
     * @return String[]
     */
    public String[] getHisCancelOrderTest(String code,String password,String caseno,String rxno,String seqno){
        if (!"ESY".equals(code)) {
           return new String[] {
               "ERR:" + code + " System no Find!"};
       }
       if (!"123".equals(password)) {
           return new String[] {
               "ERR: Password is err!"};
       }
       String[] str = new String[] {
           "Y"};
       return str;
    }
    /**
     * Ƥ�Ի�д�ż�ҽ����ע
     * @param code String
     * @param password String
     * @param caseNo String
     * @param rxNo String
     * @param seqNo String
     * @return String[]
     */
    public String[] readOpdOrderPSTest(String code,String password,String caseno,String rxno,String seqno,String value){
        if (!"ESY".equals(code)) {
            return new String[] {
                "ERR:" + code + " System no Find!"};
        }
        if (!"123".equals(password)) {
            return new String[] {
                "ERR: Password is err!"};
        }
        return new String[]{"Y"};
    }
    /**
     * �õ��ż�ס�����
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getAdmTypeTest(String code,String password){
        if (!"ESY".equals(code)) {
            return new String[] {
                "ERR:" + code + " System no Find!"};
        }
        if (!"123".equals(password)) {
            return new String[] {
                "ERR: Password is err!"};
        }
        return new String[]{"����;E;20090617","�������;H;20091015","סԺ;I;20090617","����;O;20090617"};

    }
    /**
     * �õ�������
     * @param code String
     * @param password String
     * @return String
     */
    public String[] getRegionJDTest(String code, String password) {
        if (!"ESY".equals(code)) {
            return new String[] {
                "ERR:" + code + " System no Find!"};
        }
        if (!"123".equals(password)) {
            return new String[] {
                "ERR: Password is err!"};
        }
        return new String[]{"001;���˾�����","002;��ͯ������"};
    }
    /**
   * ���鿽������
   * @param sql1 String[]
   * @param sql2 String[]
   * @return String[]
   */
  public static String[] copyArray(String sql1[], String sql2[]) {
      if (sql1.length == 0)
          return sql2;
      if (sql2.length == 0)
          return sql1;
      String data[] = new String[sql1.length + sql2.length];
      System.arraycopy(sql1, 0, data, 0, sql1.length);
      System.arraycopy(sql2, 0, data, sql1.length, sql2.length);
      return data;
  }

//    public static void main(String[] args) {
//        JavaHisDebug.initClient();
//        DictionaryService d = new DictionaryService();
//        String[] strarry = d.getPatInfAndOrder("ESY","123","100113000062");
//        for(String temp:strarry){
//            System.out.println("temp"+temp);
//        }
//    }
    /**
     * �õ��ֵ���Ϣ
     * @param groupId String
     * @param id String
     * @return String
     */
    public String getDictionary(String groupId,String id){
        String result="";
        TParm parm = new TParm(TJDODBTool.getInstance().select("SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"+groupId+"' AND ID='"+id+"'"));
        result = parm.getValue("CHN_DESC",0);
        return result;
    }
    /**
     * ���Կ���
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] testServiceDept(String code,String password){
        if(!"LIS".equals(code)){
            return new String[]{"ERR:"+code+" System no Find!"};
        }
        if(!"123".equals(password)){
            return new String[]{"ERR: Password is err!"};
        }
        String[] str = new String[]{"10101;�����ڿ�;HXNK;�����ڿ�","10102;�����ڿ�;;�����ڿ�"};
        return str;
    }
    /**
     * ������Ա
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] testServiceUser(String code,String password){
        if(!"LIS".equals(code)){
            return new String[]{"ERR:"+code+" System no Find!"};
        }
        if(!"123".equals(password)){
            return new String[]{"ERR: Password is err!"};
        }
        String[] str = new String[]{"hwb;С��;1;haiwenbobo@163.com","ehui;���;1;1@11.com"};
        return str;
    }

    /**
     * ���没����Ϣ
     * @param code String
     * @param password String
     * @param name String ����
     * @param birthday String ��������
     * @param sex String �Ա�
     * @param SID String ���֤��
     * @param tel String �绰
     * @param address String ��ַ
     * @return String
     */
    public String savePat(String code,String password,String name,String birthday,String sex,String SID,String tel,String address)
    {
        String result = checkAuthority(code, password, "PAT_INF", "WRITE");
        if (result.length() > 0)
            return result;
        if(name == null || name.length() == 0)
            return "Err:��������Ϊ��";
        if(birthday == null || birthday.length() == 0)
            return "Err:�������ڲ���Ϊ��";
        if(sex == null || sex.length() == 0)
            return "Err:�Ա𲻿�Ϊ��";
        if(SID == null || SID.length() == 0)
            return "Err:���֤�Ų���Ϊ��";
        TParm parmPatInf = new TParm(TJDODBTool.getInstance().select(
            " SELECT MR_NO "+
            " FROM SYS_PATINFO "+
            " WHERE IDNO='"+SID+"'"));
        if(parmPatInf.getErrCode() < 0)
            return "Err:" + parmPatInf;
        if(parmPatInf.getCount() > 0)
            return "Err:�Ѵ��ڴ˲�����Ϣ�޷���������";
        TParm defCtzParm = getDefCtz();
        if(defCtzParm.getErrCode() < 0)
            return "Err:" + defCtzParm;
        if(defCtzParm.getCount() <= 0)
            return "Err:δȡ�ò���Ĭ�����";
        TJDOTool tJDOTool = new TJDOTool();
        tJDOTool.setModuleName("sys\\SYSSystemModule.x");
        String mrNo = tJDOTool.getResultString(tJDOTool.call("getMrNo"),"MR_NO");
        TParm parm = new TParm(TJDODBTool.getInstance().update(" INSERT INTO SYS_PATINFO ("+
                                                               "       PAT_NAME,PY1,FOREIGNER_FLG,BIRTH_DATE,SEX_CODE,"+
                                                               "       IDNO,TEL_HOME,ADDRESS,CTZ1_CODE,MR_NO,"+
                                                               "       OPT_USER,OPT_DATE,OPT_TERM)"+
                                                               " VALUES('"+name+"','"+TMessage.getPy(name)+"','N',TO_DATE('"+birthday+"','YYYYMMDDHH24MISS'),'"+sex+"',"+
                                                               "       '"+SID+"','"+tel+"','"+address+"','"+defCtzParm.getValue("CTZ_CODE",0)+"','"+mrNo+"',"+
                                                               "       'web',SYSDATE,'web')"));
        if(parm.getErrCode() < 0)
            return "Err:" + parm;
        return mrNo;
    }

    /**
     * ȡ�ò���Ĭ�����
     * @return TParm
     */
    private TParm getDefCtz(){
        TParm defCtzParm = new TParm(TJDODBTool.getInstance().select(
                " SELECT CTZ_CODE "+
                " FROM SYS_CTZ "+
                " WHERE DEF_CTZ_FLG = 'Y'"));
        return defCtzParm;
    }

    /**
     * �������֤�ŵõ����߱��
     * @param code String
     * @param password String
     * @param SID String ���֤��
     * @param name String ����
     * @return String
     */
    public String[] getPatForSID(String code,String password,String SID,String name)
    {
        String result = checkAuthority(code, password, "PAT_INF", "READ");
        if (result.length() > 0)
            return new String[]{result};
        TParm parm = new TParm(TJDODBTool.getInstance().select(" SELECT MR_NO "+
                                                               " FROM SYS_PATINFO "+
                                                               " WHERE IDNO='"+SID+"' "+
                                                               " AND PAT_NAME LIKE '%"+name+"%'"));
        if(parm.getErrCode() < 0)
            return new String[]{"Err:" + parm};
        if(parm.getCount() <= 0)
            return new String[]{""};
        String[] returnString = new String[parm.getCount()];
        for(int i = 0;i < parm.getCount();i++){
            returnString[i] = parm.getValue("MR_NO",i);
        }
        return returnString;
    }

    /**
     * ԤԼ�Һ�
     * @param code String
     * @param password String
     * @param mrNo String ������
     * @param date String �Һ�����
     * @param sessionCode String ʱ��
     * @param admType String �ż���
     * @param deptCode String ����
     * @param clinicRoomNo String ���
     * @param drCode String ҽ������
     * @param regionCode String ����
     * @param ctz1Code String �������
     * @param serviceLevel String ����ȼ�
     * @return String
     */
    public String regAppt(String code,String password,String mrNo,String date,String sessionCode,
                          String admType,String deptCode,String clinicRoomNo,String drCode,String regionCode,
                          String ctz1Code,String serviceLevel)
    {
        String result = checkAuthority(code, password, "REG_INF", "WRITE");
        if (result.length() > 0)
            return result;
        if(mrNo == null || mrNo.length() == 0)
            return "Err:�����Ų���Ϊ��";
        if(date == null || date.length() == 0)
            return "Err:ԤԼ�Һ����ڲ���Ϊ��";
        if(sessionCode == null || sessionCode.length() == 0)
            return "Err:�Һ�ʱ�β���Ϊ��";
        if(admType == null || admType.length() == 0)
            return "Err:�ż��𲻿�Ϊ��";
        if(deptCode == null || deptCode.length() == 0)
            return "Err:�Ʊ𲻿�Ϊ��";
        if(clinicRoomNo == null || clinicRoomNo.length() == 0)
            return "Err:��䲻��Ϊ��";
        if(drCode == null || drCode.length() == 0)
            return "Err:ҽ������Ϊ��";
        if(regionCode == null || regionCode.length() == 0)
            return "Err:���򲻿�Ϊ��";
        //ȡ�������Ϣ
        TParm parmClinicRoom = getClinicRoomInf(clinicRoomNo);
        if(parmClinicRoom.getErrCode() < 0)
            return "Err:" + parmClinicRoom;
        if(parmClinicRoom.getCount() <= 0 )
            return "Err:δȡ�������Ϣ";
        //ȡ�ð����Ϣ
        TParm parmSchDay = getREGSchdayInfo(clinicRoomNo,regionCode,
                                            admType,date,sessionCode);
        if(parmSchDay.getErrCode() < 0 )
            return "Err:" + parmSchDay;
        if(parmSchDay.getCount() <= 0 )
            return "Err:δȡ�ð����Ϣ";
        //ȡ�����ݿ�����
        TAction tAction = new TAction();
        TConnection conn = tAction.getConnection();
        if(conn == null)
            return "Err:δȡ�����ݿ�����";
        //ȡ��������
        String queNo = getQueNo(parmSchDay,clinicRoomNo,
                                admType,date,sessionCode,
                                regionCode,conn);
        if(queNo.length() == 0){
            conn.rollback();
            conn.close();
            return "Err:���������";
        }
        //ȷ���������
        if(ctz1Code == null || ctz1Code.length() == 0){
            TParm defCtzParm = getDefCtz();
            if (defCtzParm.getErrCode() < 0){
                conn.rollback();
                conn.close();
                return "Err:" + defCtzParm;
            }
            if (defCtzParm.getCount() <= 0){
                conn.rollback();
                conn.close();
                return "Err:δȡ�ò���Ĭ�����";
            }
            ctz1Code = defCtzParm.getValue("CTZ_CODE", 0);
        }
        //ȷ������ȼ���Ϣ
        if(serviceLevel == null || serviceLevel.length() == 0)
            serviceLevel = "1";
        //ȡ���������˳���
        String caseNo = getNo("ALL", "REG","CASE_NO", "CASE_NO");
        if(caseNo.length() == 0)
            return "Err:δȡ������˳���";
        TParm parm = insertRegPatadm(mrNo,date,sessionCode,
                                     admType,deptCode,clinicRoomNo,
                                     drCode,regionCode,ctz1Code,
                                     serviceLevel,queNo,caseNo,
                                     parmClinicRoom,parmSchDay,conn);
        if(parm.getErrCode() < 0 ){
            conn.rollback();
            conn.close();
            return "Err:" + parm;
        }
        conn.commit();
        conn.close();
        return caseNo;
    }

    /**
     * ȡ�þ������
     * @param parmSchDay TParm
     * @param clinicRoomNo String
     * @param admType String
     * @param date String
     * @param sessionCode String
     * @param regionCode String
     * @param conn TConnection
     * @return String
     */
    private String getQueNo(TParm parmSchDay,String clinicRoomNo,
                            String admType,String date,String sessionCode,
                            String regionCode,TConnection conn){
        if(parmSchDay.getValue("VIP_FLG",0).equals("Y")){
            TParm parmQueNo =getVIPQueNo(clinicRoomNo,admType,
                                     date,sessionCode);
            if(parmQueNo.getErrCode() < 0)
                return "";
            if(parmQueNo.getCount() <= 0)
                return "";
            if(parmQueNo.getValue("QUE_NO",0) == null ||
               parmQueNo.getValue("QUE_NO",0).length() == 0 ||
               parmQueNo.getValue("QUE_NO",0).equalsIgnoreCase("null"))
                return "";
            TParm parm = updateVIPQueNo(clinicRoomNo, admType,
                                        date,sessionCode,
                                        parmQueNo.getValue("QUE_NO",0),"Y",conn);
            if(parm.getErrCode() < 0 )
                return "";
            return parmQueNo.getValue("QUE_NO",0);
        }
        else{
            TParm parm = updateQueNo(clinicRoomNo, admType,
                                     date,sessionCode,
                                     regionCode,conn);
            if(parm.getErrCode() < 0 )
                return "";
            return parmSchDay.getValue("QUE_NO",0);
        }
    }

    /**
     * ȡ�������Ϣ
     * @param clinicRoomNo String
     * @return TParm
     */
    private TParm getClinicRoomInf(String clinicRoomNo){
        TParm parmClinicRoom = new TParm(TJDODBTool.getInstance().select(
                " SELECT CLINICAREA_CODE "+
                " FROM REG_CLINICROOM "+
                " WHERE CLINICROOM_NO = '"+clinicRoomNo+"'"));
        return parmClinicRoom;
    }

    /**
     * ȡ�ð����Ϣ
     * @param clinicRoomNo String
     * @param regionCode String
     * @param admType String
     * @param date String
     * @param sessionCode String
     * @return TParm
     */
    private TParm getREGSchdayInfo(String clinicRoomNo,String regionCode,
                               String admType,String date,String sessionCode){
        TParm parmSchDay = new TParm(TJDODBTool.getInstance().select(
                " SELECT VIP_FLG,CLINICTYPE_CODE,QUE_NO "+
                " FROM REG_SCHDAY "+
                " WHERE CLINICROOM_NO = '"+clinicRoomNo+"'"+
                " AND   REGION_CODE = '"+regionCode+"'"+
                " AND   ADM_TYPE = '"+admType+"'"+
                " AND   ADM_DATE = '"+date+"'"+
                " AND   SESSION_CODE = '"+sessionCode+"'"+
                " AND   QUE_NO != MAX_QUE"));
        return parmSchDay;
    }

    /**
     * ȡ��VIP�������
     * @param clinicRoomNo String
     * @param admType String
     * @param date String
     * @param sessionCode String
     * @return TParm
     */
    private TParm getVIPQueNo(String clinicRoomNo,String admType,
                              String date,String sessionCode){
        TParm parmQueNo = new TParm(TJDODBTool.getInstance().select(
                " SELECT MIN(QUE_NO) QUE_NO "+
                " FROM REG_CLINICQUE "+
                " WHERE CLINICROOM_NO = '"+clinicRoomNo+"'"+
                " AND   ADM_TYPE = '"+admType+"'"+
                " AND   ADM_DATE = '"+date+"'"+
                " AND   SESSION_CODE = '"+sessionCode+"'"+
                " AND   QUE_STATUS = 'N'"));
        return parmQueNo;
    }

    /**
     * ����VIP����˳���
     * @param clinicRoomNo String
     * @param admType String
     * @param date String
     * @param sessionCode String
     * @param queNo String
     * @param conn TConnection
     * @return TParm
     */
    private TParm updateVIPQueNo(String clinicRoomNo, String admType,
                                 String date,String sessionCode,
                                 String queNo,String queStatus,TConnection conn){
        TParm parm = updateDBTable(
                " UPDATE REG_CLINICQUE"+
                " SET QUE_STATUS = '"+queStatus+"'"+
                " WHERE CLINICROOM_NO = '"+clinicRoomNo+"'"+
                " AND   ADM_TYPE = '"+admType+"'"+
                " AND   ADM_DATE = '"+date+"'"+
                " AND   SESSION_CODE = '"+sessionCode+"'"+
                " AND   QUE_NO = '"+queNo+"'",conn);
        return parm;
    }

    /**
     * ���¾������
     * @param clinicRoomNo String
     * @param admType String
     * @param date String
     * @param sessionCode String
     * @param regionCode String
     * @param conn TConnection
     * @return TParm
     */
    private TParm updateQueNo(String clinicRoomNo, String admType,
                                 String date,String sessionCode,
                                 String regionCode,TConnection conn){
        TParm parm = updateDBTable(
                " UPDATE REG_SCHDAY"+
                " SET QUE_NO = QUE_NO + 1"+
                " WHERE CLINICROOM_NO = '"+clinicRoomNo+"'"+
                " AND   REGION_CODE = '"+regionCode+"'"+
                " AND   ADM_TYPE = '"+admType+"'"+
                " AND   ADM_DATE = '"+date+"'"+
                " AND   SESSION_CODE = '"+sessionCode+"'"+
                " AND   CLINICROOM_NO = '"+clinicRoomNo+"'",conn);
        return parm;
    }

    /**
     * д�Һ�����
     * @param mrNo String
     * @param date String
     * @param sessionCode String
     * @param admType String
     * @param deptCode String
     * @param clinicRoomNo String
     * @param drCode String
     * @param regionCode String
     * @param ctz1Code String
     * @param serviceLevel String
     * @param queNo String
     * @param caseNo String
     * @param parmClinicRoom TParm
     * @param parmSchDay TParm
     * @param conn TConnection
     * @return TParm
     */
    private TParm insertRegPatadm(String mrNo,String date,String sessionCode,
                                  String admType,String deptCode,String clinicRoomNo,
                                  String drCode,String regionCode,String ctz1Code,
                                  String serviceLevel,String queNo,String caseNo,
                                  TParm parmClinicRoom,TParm parmSchDay,TConnection conn){
        TParm parm = updateDBTable(
                " INSERT INTO REG_PATADM ("+
                "       REALDEPT_CODE,OPT_TERM,DEPT_CODE,APPT_CODE,QUE_NO,"+
                "       REGMETHOD_CODE,ADM_STATUS,DR_CODE,HEAT_FLG,OPT_USER,"+
                "       MR_NO,CLINICROOM_NO,ADM_DATE,SESSION_CODE,VISIT_CODE,"+
                "       CLINICAREA_CODE,VIP_FLG,CLINICTYPE_CODE,REGION_CODE,REPORT_STATUS,"+
                "       CASE_NO,ADM_REGION,ARRIVE_FLG,CTZ1_CODE,SERVICE_LEVEL,"+
                "       ADM_TYPE,REG_DATE,REALDR_CODE,OPT_DATE)"+
                " VALUES('"+deptCode+"','web','"+deptCode+"','0','"+queNo+"',"+
                "       'N','1','"+drCode+"','N','web',"+
                "       '"+mrNo+"','"+clinicRoomNo+"',TO_DATE('"+date+"','YYYYMMDD'),'"+sessionCode+"','0',"+
                "       '"+parmClinicRoom.getValue("CLINICAREA_CODE",0)+"','"+parmSchDay.getValue("VIP_FLG",0)+"','"+parmSchDay.getValue("CLINICTYPE_CODE",0)+"','"+regionCode+"','1',"+
                "       '"+caseNo+"','"+regionCode+"','N','"+ctz1Code+"','"+serviceLevel+"',"+
                "       '"+admType+"',SYSDATE,'"+serviceLevel+"',SYSDATE)",conn);
        return parm;
    }

    /**
     * ȡ��ԭ��ȡ��CASE_NO
     * @param regionCode String
     * @param systemCode String
     * @param operation String
     * @param section String
     * @return String
     */
    private String getNo(String regionCode,String systemCode,
                         String operation,String section){
        TParm parm = new TParm();
        parm.setData("REGION_CODE",regionCode);
        parm.setData("SYSTEM_CODE",systemCode);
        parm.setData("OPERATION",operation);
        parm.setData("SECTION",section);
        TJDOTool tJDOTool = new TJDOTool();
        tJDOTool.setModuleName("sys\\SYSSystemModule.x");
        return tJDOTool.getResultString(tJDOTool.call("getNo",parm),"NO");
    }

    /**
     * �������ݿ������
     * @param SQL String
     * @param conn TConnection
     * @return TParm
     */
    private TParm updateDBTable(String SQL,TConnection conn){
//        TSocket socket = TIOM_AppServer.SOCKET;
//        TIOM_AppServer.SOCKET = null;
//        TParm parm = new TParm(TJDODBTool.getInstance().update(SQL,conn));
//        TIOM_AppServer.SOCKET = socket;
        TParm parm = new TParm(TJDODBTool.getInstance().update(SQL));
        return parm;
    }

    /**
     * ԤԼ�˺�
     * @param code String
     * @param password String
     * @param caseNo String
     * @return String
     */
    public String regUnAppt(String code,String password,String caseNo)
    {
        String result = checkAuthority(code, password, "REG_INF", "WRITE");
        if (result.length() > 0)
            return result;
        if(caseNo == null || caseNo.length() == 0)
            return "Err:����˳��Ų���Ϊ��";
        TParm parmRegInf = new TParm(TJDODBTool.getInstance().select(
                " SELECT ARRIVE_FLG,VIP_FLG,CLINICROOM_NO,ADM_TYPE,"+
                "        TO_CHAR(ADM_DATE,'YYYYMMDD') ADM_DATE,SESSION_CODE,"+
                "        QUE_NO"+
                " FROM REG_PATADM "+
                " WHERE CASE_NO = '"+caseNo+"'"));
        if(parmRegInf.getErrCode() < 0)
            return "Err" + parmRegInf;
        if(parmRegInf.getCount() <= 0)
            return "Err:δ�ҵ�����ԤԼ��Ϣ";
        if(parmRegInf.getValue("ARRIVE_FLG",0).equals("Y"))
            return "Err:�����Ѿ�����,�뵽�շѹ�̨�����˹�ҵ��";
        TAction tAction = new TAction();
        TConnection conn = tAction.getConnection();
        if(conn == null)
            return "Err:δȡ������";
        TParm parm = updateDBTable(
                " UPDATE REG_PATADM"+
                " SET REGCAN_USER = 'web',"+
                "     REGCAN_DATE = SYSDATE"+
                " WHERE CASE_NO = '"+caseNo+"'",conn);
        if(parm.getErrCode() < 0){
            conn.rollback();
            conn.close();
            return "Err" + parm;
        }
        TParm parmRegParm = new TParm(TJDODBTool.getInstance().select(
            " SELECT QUEREUSE_FLG "+
            " FROM REG_SYSPARM"));
        if(parmRegParm.getErrCode() < 0){
            conn.rollback();
            conn.close();
            return "Err" + parmRegParm;
        }
        if(parmRegParm.getCount() < 0){
            conn.rollback();
            conn.close();
            return "Err:δȡ�ùҺŲ�������Ϣ";
        }
        if(parmRegParm.getValue("QUEREUSE_FLG",0).equals("Y") &&
           parmRegInf.getValue("VIP_FLG",0).equals("Y")){
            TParm parmQue = updateVIPQueNo(parmRegInf.getValue("CLINICROOM_NO",0),
                                           parmRegInf.getValue("ADM_TYPE",0),
                                           parmRegInf.getValue("ADM_DATE",0),
                                           parmRegInf.getValue("SESSION_CODE",0),
                                           parmRegInf.getValue("QUE_NO",0),"N",conn);
            if(parmQue.getErrCode() < 0){
                conn.rollback();
                conn.close();
                return "Err" + parmQue;
            }
        }
        conn.commit();
        conn.close();
        return "Y";
    }

    /**
     * ȡ�ò�����ǰ����ԤԼδ������Ϣ
     * @param code String
     * @param password String
     * @param mrNo String
     * @return String[]
     */
    public String[] getPatAppRegInfo(String code,String password,String mrNo)
    {
        String result = checkAuthority(code, password, "REG_INF", "READ");
        if (result.length() > 0)
            return new String[]{result};
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            " SELECT CASE_NO||';'||TO_CHAR(ADM_DATE,'YYYYMMDD')||';'||SESSION_CODE||';'||DEPT_CODE||';'||QUE_NO INFO"+
            " FROM   REG_PATADM"+
            " WHERE  MR_NO = '"+mrNo+"'"+
            " AND    APPT_CODE = '0'"+
            " AND    ARRIVE_FLG = 'N'"+
            " AND    REGCAN_USER IS NULL"));
        if(parm.getErrCode() < 0)
            return new String[]{"Err:" + parm};
        if(parm.getCount() <= 0)
            return new String[]{""};
        String[] appRegInfo = new String[parm.getCount()];
        for(int i = 0;i < parm.getCount();i++){
            appRegInfo[i] = parm.getValue("INFO",i);
        }
        return appRegInfo;
    }

    /**
     * ȡ��ʱ����Ϣ
     * @param code String
     * @param password String
     * @param admType String
     * @return String[]
     */
    public String[] getSessionInf(String code,String password,String admType)
    {
        String result = checkAuthority(code, password, "SESSION_INF", "READ");
        if (result.length() > 0)
            return new String[]{result};
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            " SELECT SESSION_CODE||';'||SESSION_DESC INFO"+
            " FROM   REG_SESSION"+
            " WHERE  ADM_TYPE = '"+admType+"'"));
        if(parm.getErrCode() < 0)
            return new String[]{"Err:" + parm};
        if(parm.getCount() <= 0)
            return new String[]{""};
        String[] sessionInfo = new String[parm.getCount()];
        for(int i = 0;i < parm.getCount();i++){
            sessionInfo[i] = parm.getValue("INFO",i);
        }
        return sessionInfo;
    }

    /**
     * ȡ�������Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getClinicRoom(String code,String password)
    {
        String result = checkAuthority(code, password, "CLINICROOM_INF", "READ");
        if (result.length() > 0)
            return new String[]{result};
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            " SELECT CLINICROOM_NO||';'||CLINICROOM_DESC||';'||CLINICAREA_CODE INFO"+
            " FROM   REG_CLINICROOM"));
        if(parm.getErrCode() < 0)
            return new String[]{"Err:" + parm};
        if(parm.getCount() <= 0)
            return new String[]{""};
        String[] clinicRoomInfo = new String[parm.getCount()];
        for(int i = 0;i < parm.getCount();i++){
            clinicRoomInfo[i] = parm.getValue("INFO",i);
        }
        return clinicRoomInfo;
    }

    /**
     * ȡ��������Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getClinicArea(String code,String password)
    {
        String result = checkAuthority(code, password, "CLINICAREA_INF", "READ");
        if (result.length() > 0)
            return new String[]{result};
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            " SELECT CLINICAREA_CODE||';'||CLINIC_DESC||';'||REGION_CODE INFO"+
            " FROM   REG_CLINICAREA"));
        if(parm.getErrCode() < 0)
            return new String[]{"Err:" + parm};
        if(parm.getCount() <= 0)
            return new String[]{""};
        String[] clinicArea = new String[parm.getCount()];
        for(int i = 0;i < parm.getCount();i++){
            clinicArea[i] = parm.getValue("INFO",i);
        }
        return clinicArea;
    }

    /**
     * ȡ�ÿ��ҷ�����Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getDeptClassRule(String code,String password)
    {
        String result = checkAuthority(code, password, "DEPTCLASS_INF", "READ");
        if (result.length() > 0)
            return new String[]{result};
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            " SELECT CATEGORY_CODE||';'||CATEGORY_CHN_DESC INFO"+
            " FROM   SYS_CATEGORY"+
            " WHERE  RULE_TYPE = 'SYS_DEPT'"));
        if(parm.getErrCode() < 0)
            return new String[]{"Err:" + parm};
        if(parm.getCount() <= 0)
            return new String[]{""};
        String[] deptClassRule = new String[parm.getCount()];
        for(int i = 0;i < parm.getCount();i++){
            deptClassRule[i] = parm.getValue("INFO",i);
        }
        return deptClassRule;
    }
}
