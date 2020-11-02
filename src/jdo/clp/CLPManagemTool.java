package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

import java.util.Map;

import jdo.sys.SystemTool;

import com.dongyang.db.TConnection;

/**
 * <p>Title: �ٴ�·��׼��׼��</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPManagemTool extends TJDOTool {
    public CLPManagemTool() {
        setModuleName("clp\\CLPManagemModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    public static CLPManagemTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static CLPManagemTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPManagemTool();
        return instanceObject;
    }

    /**
     * ��ѯʱ����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDurationData(TParm parm) {
//        System.out.println("ʱ����Ϣ��ѯ����");
        TParm result = this.query("selectDurationData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����ʱ����Ϣ
     * @return TParm
     */
    public TParm savaDurationData(TParm parm,TConnection conn){
//        System.out.println("����ʱ����Ϣ��ѯ����");
        TParm result = this.update("saveDurationData", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * չ��ʱ��
     * @return TParm
     */
    public TParm openDuraction(TParm parm, TConnection conn) {
//        System.out.println("չ��ʱ�̷���");
        TParm result = this.update("openDuraction", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ɾ��ʱ����Ϣ
     * @return TParm
     */
    public TParm delDurationData(TParm parm) {
//        System.out.println("ɾ��ʱ����Ϣ��ѯ����");
        TParm result = this.update("delDurationData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ׼����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm) {
//        System.out.println("���ݲ�ѯ����");
        TParm result = this.query("selectData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ���·����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOverData(TParm parm) {
//        System.out.println("���ݲ�ѯ����");
        TParm result = this.query("selectOverData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ����·����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selecCanceltData(TParm parm) {
//        System.out.println("���ݲ�ѯ����");
        TParm result = this.query("selectCancelData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���ٴ�·�������ƶ�����ʷ��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm managermToHistory(TParm parm,TConnection conn) {
//        System.out.println("�����ƶ�����");
        TParm result = this.update("moveManagermData", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * �õ�סԺ������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm getPatientInfo(TParm parm) {
//        System.out.println("���ݲ�ѯ����--��ѯ������Ϣ");
        TParm result = this.query("selectPatientData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
    
    public TParm insertManagem(TParm parm){
//       System.out.println("���ݲ��뷽��--�����ٴ�·��");
       TParm result = this.update("insertPatientManagem",parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
    }
    /**
     * �����ٴ�·��AMD_INP
     * @return TParm
     */
    public TParm insertAMDINPIntoCLNCPathCode(TParm parm,TConnection conn){
//        System.out.println("���ݲ��뷽��--����AMD_INP");
        TParm result = this.update("insertAMDINPIntoCLNCPathCode", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
    public TParm insertManagemHistory(TParm parm,TConnection conn) {
        //system.out.println("���ݲ��뷽��--�����ٴ�·����ʷ��");
        TParm result = this.update("insertManagemHistory", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * �����ٴ�·��(������ʽ)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertManagem(TParm parm,TConnection conn) {
        //system.out.println("���ݲ��뷽��--�����ٴ�·��");
        TParm result = this.update("insertPatientManagem", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ɾ������
     * @param ACIRecordNo String
     * @return TParm
     */
    public TParm deleteData(TParm parm) {
        TParm result = update("deleteData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ������
     * @param ACIRecordNo String
     * @return TParm
     */
    public TParm deleteData(TParm parm,TConnection conn) {
        TParm result = update("deleteData", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    public TParm updateManagem(TParm parm,TConnection conn){
        //system.out.println("���ݲ��뷽��--�༭�ٴ�·��");
        StringBuffer sqlbf = new StringBuffer();
        String caseNo=parm.getValue("CASE_NO");
        String clncPathCode=parm.getValue("CLNCPATH_CODE");
        sqlbf.append("UPDATE CLP_MANAGEM SET ");
        //������������begin
        String startDttm=parm.getValue("START_DTTM");
        //system.out.println("����"+startDttm);
        if(checkNullAndEmpty(startDttm)){
            sqlbf.append(" START_DTTM = TO_DATE('"+startDttm+"','YYYYMMDD') ,");
        }
        String deleteDttm = parm.getValue("DELETE_DTTM");
        //system.out.println("����"+deleteDttm);
        if(checkNullAndEmpty(deleteDttm)){
            sqlbf.append(" DELETE_DTTM = TO_DATE('"+deleteDttm+"','YYYYMMDD') ,");
        }
        String endDttm = parm.getValue("END_DTTM");
        String outissu=parm.getValue("OUTISSUE_CODE");
        //system.out.println("���:"+endDttm);
        if(checkNullAndEmpty(endDttm)){
            sqlbf.append(" END_DTTM = TO_DATE('"+endDttm+"','YYYYMMDD') ,");
            sqlbf.append(" OUTISSUE_CODE='"+outissu+"' , ");
        }
        sqlbf.append(" STATUS = '"+parm.getValue("STATUS")+"' ");
        //������������end
        sqlbf.append(" WHERE CASE_NO = '"+caseNo+"' ");
        sqlbf.append("AND CLNCPATH_CODE = '"+clncPathCode+"'");
        //system.out.println("ִ�е�sql���:"+sqlbf.toString());
        Map mapresult=TJDODBTool.getInstance().update(sqlbf.toString(),conn);
        TParm result =new TParm(mapresult);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
       return result;
    }
    public TParm moveDeleteAndEndManagemIntoHistory(TParm parm,TConnection conn){
        TParm result = update("moveDeleteAndEndManagemIntoHistory", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }


    /**
     * ����ԭ�ٴ�·����Ϣ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateChangedManagem(TParm parm , TConnection conn){
        //system.out.println("------------------------���±���ٴ�·����Ϣ");
        TParm result = update("updateChangeManagem", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����Ƿ�Ϊ�ջ�մ�
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr){
        if(checkstr==null){
            return false;
        }
        if("".equals(checkstr)){
            return false;
        }
        return true;
    }
    /**
     * �����ٴ�·����Ĭ��ʱ����Ϣ����
     * @param clncPathCode String
     * @return TParm
     */
    public TParm insertDefaultDurationConfig(TParm inparm,TConnection conn){
        String caseNo=inparm.getValue("CASE_NO");
        String clncPathCode=inparm.getValue("CLNCPATH_CODE");
        String regionCode=inparm.getValue("REGION_CODE");
        String optUser=inparm.getValue("OPT_USER");
        String optDate=inparm.getValue("OPT_DATE");
        String optTerm=inparm.getValue("OPT_TERM");
        //��Ժʱ��
        String inDate="";
        TParm result = new TParm();
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("DELETE FROM CLP_THRPYSCHDM_REAL WHERE ");
        sqlbf.append(" CASE_NO = '"+caseNo+"'");
        sqlbf.append(" AND CLNCPATH_CODE ='"+clncPathCode+"'");
        Map mapresult=TJDODBTool.getInstance().update(sqlbf.toString(),conn);
        result=new TParm(mapresult);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //������Ժʱ��
        sqlbf.delete(0,sqlbf.length());
        sqlbf.append(" SELECT TO_CHAR(IN_DATE,'YYYYMMDDHH24MISS') AS IN_DATE FROM ADM_INP WHERE CASE_NO ='"+caseNo+"'");
        mapresult = TJDODBTool.getInstance().select(sqlbf.toString());
        result = new TParm(mapresult);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //==========pangben 2012-6-12 ����޸�sql���
        String inDateTemp="";
        if(result.getCount()>0){
            inDate = result.getValue("IN_DATE", 0);
            inDateTemp=result.getValue("IN_DATE", 0);
        }
        int seq=0;
//        sqlbf.delete(0,sqlbf.length());
//        sqlbf.append("SELECT CASE  (COUNT(MAX(SEQ)+1)) WHEN 0 THEN '0' ELSE TO_CHAR(MAX(SEQ)) END AS SEQ FROM CLP_THRPYSCHDM_REAL WHERE  CASE_NO ='"+caseNo+"' AND CLNCPATH_CODE='"+clncPathCode+"'");
//        mapresult = TJDODBTool.getInstance().select(sqlbf.toString());
//        result = new TParm(mapresult);
//        seq=result.getInt("SEQ",0);
        sqlbf.delete(0,sqlbf.length());
        //�õ���׼����
        sqlbf.append(" SELECT CLNCPATH_CODE,SCHD_CODE,");
        sqlbf.append(caseNo + " AS CASE_NO, ");
        sqlbf.append("'" + regionCode + "'" + " AS REGION_CODE, ");
        sqlbf.append("SCHD_DAY,SUSTAINED_DAYS,");
        sqlbf.append("'" + optUser + "'" + " AS OPT_USER,");
        sqlbf.append("TO_CHAR(TO_DATE(" + optDate + ",'YYYYMMDD'),'YYYYMMDDHH24MISS')" + " AS OPT_DATE ,");
        sqlbf.append("'" + optTerm + "'" + " AS OPT_TERM , ");
        inDate=inDate.substring(0,8)+"000000";
        inDateTemp=inDate.substring(0,8)+"235959";
        sqlbf.append("TO_CHAR((TO_DATE(" + inDate +
                     ",'YYYYMMDDHH24MISS')+SUSTAINED_DAYS-1),'YYYYMMDDHH24MISS')" +
                     " AS START_DATE ,");    
        sqlbf.append("TO_CHAR((TO_DATE(" + inDateTemp +
                     ",'YYYYMMDDHH24MISS')+SUSTAINED_DAYS-2+SCHD_DAY),'YYYYMMDDHH24MISS')" +
                     " AS END_DATE ");//=======pangben 2012-6-12 �� SUSTAINED_DAYS-1 ��SUSTAINED_DAYS-2
        sqlbf.append(" FROM CLP_THRPYSCHDM ");
        sqlbf.append(" WHERE REGION_CODE='" + regionCode + "'");
        sqlbf.append(" AND CLNCPATH_CODE='" + clncPathCode + "'");
        sqlbf.append(" ORDER BY SEQ ");
        //system.out.println("sqlbf:::"+sqlbf);
        mapresult = TJDODBTool.getInstance().select(sqlbf.toString());
        result = new TParm(mapresult);
        for(int i=0;i<result.getCount();i++){
            //����Ĭ������
            sqlbf.delete(0, sqlbf.length());
            sqlbf.append("INSERT INTO CLP_THRPYSCHDM_REAL VALUES(");
            sqlbf.append("'"+result.getValue("CLNCPATH_CODE",i)+"',");
            sqlbf.append("'"+result.getValue("SCHD_CODE",i)+"',");
            sqlbf.append("'"+result.getValue("CASE_NO",i)+"',");
            sqlbf.append(""+seq+",");
            sqlbf.append("'"+result.getValue("REGION_CODE",i)+"',");
            sqlbf.append("'"+result.getValue("SCHD_DAY",i)+"',");
            sqlbf.append("'"+result.getValue("SUSTAINED_DAYS",i)+"',");
            sqlbf.append("'"+result.getValue("OPT_USER",i)+"',");
            sqlbf.append("TO_DATE('"+result.getValue("OPT_DATE",i)+"','YYYYMMDDHH24MISS'),");
            sqlbf.append("'"+result.getValue("OPT_TERM",i)+"',");
             sqlbf.append("TO_DATE('"+result.getValue("START_DATE",i)+"','YYYYMMDDHH24MISS'),");
              sqlbf.append("TO_DATE('"+result.getValue("END_DATE",i)+"','YYYYMMDDHH24MISS')");
            sqlbf.append(")");
            mapresult = TJDODBTool.getInstance().update(sqlbf.toString(),conn);
            TParm insertResult = new TParm(mapresult);
            if (insertResult.getErrCode() < 0) {
                err("ERR:" + insertResult.getErrCode() + insertResult.getErrText() +
                    insertResult.getErrName());
                return insertResult;
            }
            seq++;
        }
        return result;
    }

    /**
     * ��¡����
     * @param parm TParm
     * @return TParm
     */
    private TParm cloneTParm(TParm from) {
        TParm returnTParm = new TParm();
        for (int i = 0; i < from.getNames().length; i++) {
            returnTParm.setData(from.getNames()[i],
                                from.getValue(from.getNames()[i]));
        }
        return returnTParm;
    }
    /**
     * ����·��ʱ�̷���
    * @Title: updateIbsOrddSchdCode
    * @Description: TODO(������һ�仰�����������������)
    * @author Dangzhang
    * @return
    * @throws
     */
	public TParm updateIbsOrddSchdCode(TParm parm, TConnection conn) {
		String sql = "UPDATE IBS_ORDD SET CLNCPATH_CODE= '"
				+ parm.getValue("NEW_CLNCPATH_CODE") + "' ,SCHD_CODE='"
				+ parm.getValue("NEW_SCHD_CODE") + "' WHERE CASE_NO='"
				+ parm.getValue("CASE_NO") + "' AND CLNCPATH_CODE= '"
				+ parm.getValue("CLNCPATH_CODE") + "' AND SCHD_CODE";
		if (parm.getValue("SCHD_CODE").length()>0 ) {
			sql+="='"+parm.getValue("SCHD_CODE") + "'";
		}else{
			sql+=" IS NULL ";
		}
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		return result;
	}
	/**
	 * ��ͬʱ�̴���ֱ���滻
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm  updateIbsOrddSameSchdCode(TParm parm, TConnection conn) {
		String sql = "UPDATE IBS_ORDD SET CLNCPATH_CODE= '"
			+ parm.getValue("CLNCPATH_CODE") + "'  WHERE CASE_NO='"
			+ parm.getValue("CASE_NO") + "' AND SCHD_CODE IN ("+parm.getValue("sameSchdCode")+")";
			TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
			return result;
	}
	/**
	 * ��ȫƥ��·��ֱ���޸�
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateIBsOrddClnPathCode(TParm parm, TConnection conn){
		String sql = "UPDATE IBS_ORDD SET CLNCPATH_CODE= '"+ parm.getValue("CLNCPATH_CODE") + "' WHERE CASE_NO='"
			+ parm.getValue("CASE_NO") + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		return result;
	}
	/**
	 * 
	* @Title: queryClpManagem
	* @Description: TODO(��ѯ·��·����������)
	* @author pangben 2015-8-13
	* @param parm
	* @return
	* @throws
	 */
	public TParm queryClpManagem(TParm parm){
		TParm result = query("queryClpManagem", parm);
	    return result;
	}
}
