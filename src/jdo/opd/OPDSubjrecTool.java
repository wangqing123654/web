package jdo.opd;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
*
* <p>Title: ҽ��tool
*
* <p>Description: ҽ��tool</p>
*
* <p>Copyright: Copyright (c) Liu dongyang 2008</p>
*
* <p>Company: javahis
*
* @author ehui 20080921
* @version 1.0
*/
public class OPDSubjrecTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static OPDSubjrecTool instanceObject;
    /**
     * �õ�ʵ��
     * @return OPDSubjrecTool
     */
    public static OPDSubjrecTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new OPDSubjrecTool();
        }
        return instanceObject;
    }

    /**
     * ������
     */
    public OPDSubjrecTool() {
        setModuleName("opd\\OPDSubjrecModule.x");
        onInit();
    }
    /**
     * ����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onSave(TParm parm, TConnection connection)
    {
        TParm result = new TParm();
        String saveType = parm.getValue("SAVE_TYPE");
        //System.out.println("saveType=" + saveType);
        if("NO".equalsIgnoreCase(saveType)){
        	//System.out.println("no============");
        	return result;
        }
            
        if("INSERT".equalsIgnoreCase(saveType)){
        	//System.out.println("insert============");
        	return insert(parm,connection);
        }
            
        if("UPDATE".equalsIgnoreCase(saveType)){
        	//System.out.println("update=============");
            return update(parm,connection);}
        return result;
    }
    /**
     * ��ѯ����
     * @param caseNo String �����
     * @return TParm
     */
    public TParm query(String caseNo,String admType)
    {
        TParm parm = new TParm();
        parm.setData("CASE_NO",caseNo);
        parm.setData("ADM_TYPE",admType);
        TParm result=query("selectdata",parm);
        
        if (result.getErrCode() < 0) {
        	//System.out.println(result.getErrText());
            err(result.getErrCode() + " " + result.getErrText());
            
            return result;
        }
        //System.out.println("subjrecresult"+result);
        return result;
        
    }
    /**
     * ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm insert(TParm parm) {
        TParm result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
        	//System.out.println(result.getErrText());
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ��������(��̨ʹ��)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insert(TParm parm, TConnection connection) {
    	//System.out.println("in opdsubjrectool "+parm);
        TParm result = update("insertdata", parm, connection);
        if (result.getErrCode() < 0) {
        	//System.out.println(result.getErrText());
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ���淽��
     * @param parm TParm
     * @return TParm
     */
    public TParm update(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
        	//System.out.println(result.getErrText());
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ���淽��(��̨ʹ��)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm update(TParm parm, TConnection connection) {
        TParm result = update("updatedata", parm, connection);
        if (result.getErrCode() < 0) {
        	//System.out.println(result.getErrText());
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
}
