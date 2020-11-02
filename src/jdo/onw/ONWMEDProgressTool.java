package jdo.onw;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: ҽ������</p>
 *
 * <p>Description: ҽ������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-26
 * @version 1.0
 */
public class ONWMEDProgressTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static ONWMEDProgressTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PositionTool
     */
    public static ONWMEDProgressTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ONWMEDProgressTool();
        return instanceObject;
    }
    public ONWMEDProgressTool() {
        setModuleName("onw\\ONWMEDProgressModule.x");
        onInit();
    }
    /**
     * ��ѯ��Ϣ
     */
    public TParm selectData(TParm parm){
        TParm result = this.query("selectData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    public TParm selectData(String RPTTYPE_CODE,String DEPT_CODE,String DATE,String admType){
        String where1="";
        String where2="";
        String where3="";
        String admCode = "";
        //RPTTYPE_CODE�ֶ�ǰ��λ��ʾϵͳ���� ���ǰ̨���������Ϊ3λ����С��3λ ��ʾĬ���ԡ�ϵͳ���ơ�Ϊ��λ��ѯ
        if(RPTTYPE_CODE.length()<3){
            where1 = "SUBSTR(RPTTYPE_CODE,0,3) AS RPTTYPE_CODE";
        }else{//��ǰ̨�����������3λʱ �ԡ������Ŀ��Ϊ��λ��ѯ
            where1 = "RPTTYPE_CODE";
        }
        if(admType.length()>0){
            admCode = " AND A.ADM_TYPE = '"+admType+"' ";
        }
        if(DEPT_CODE.length()>0){
            where2 = "AND A.EXEC_DEPT_CODE='"+DEPT_CODE+"'";
        }
        if(RPTTYPE_CODE.length()>3){
            where3 = "AND A.RPTTYPE_CODE='"+RPTTYPE_CODE+"'";
        }else if(RPTTYPE_CODE.length()>0&&RPTTYPE_CODE.length()<=3){
            where3 = "AND SUBSTR(A.RPTTYPE_CODE,0,3)='"+RPTTYPE_CODE+"'";
        }
        String sql = "SELECT A.RPTTYPE_CODE, B.DEPT_CHN_DESC, " +
            "COUNT (CASE WHEN A.STATUS IS NOT NULL THEN 1 ELSE NULL END ) AS ORDER_NUM, " + //����״̬�����ݿ�������
            "COUNT (CASE WHEN A.STATUS = '0' OR A.STATUS = '1' OR A.STATUS = '2' THEN 1 ELSE NULL END) AS WAIT_NUM " +//״̬С�ڵ���2��Ϊ�ȴ��е�����
            "FROM (SELECT "+where1+",EXEC_DEPT_CODE,STATUS,ORDER_DATE,ADM_TYPE FROM MED_APPLY) A, SYS_DEPT B " +
            "WHERE A.EXEC_DEPT_CODE = B.DEPT_CODE " +
            where2 +
            where3 +
            admCode +
            "AND A.ORDER_DATE BETWEEN TO_DATE('"+DATE+"','YYYYMMDD') AND TO_DATE('"+DATE+"'||'235959','YYYYMMDDHH24MISS') " +
            "GROUP BY A.RPTTYPE_CODE, A.EXEC_DEPT_CODE, B.DEPT_CHN_DESC ";
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
//       System.out.println("ҽ������  sql is ����"+sql);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
