package jdo.odi;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OdiMainTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static OdiMainTool instanceObject;
    /**
     * �õ�ʵ��
     * @return RuleTool
     */
    public static OdiMainTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OdiMainTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public OdiMainTool() {
        setModuleName("odi\\ODIMAINTOOL.x");
        onInit();
    }
    /**
     * ��ѯ��������������
     * @param parm TParm
     * @return TParm
     */
    public TParm queryInPatInfo(TParm parm) {
        TParm result = new TParm();
        result = query("selectdataInDate", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ����������Ϣ����
     * @param parm TParm
     * @return TParm
     */
    public TParm qeuryOutPatInfo(TParm parm){
        TParm result = new TParm();
        result = query("selectdataOutDate", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ�����������
     * @param mrNo String
     * @return String
     */
    public TParm queryPatCaseNo(TParm parm){
        TParm result = query("selectPatCaseNo",parm);
        if(result.getErrCode()<0){
            err("ERR:"+result.getErrCode()+result.getErrText()+result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��ʿ���ע��
     * @return boolean
     */
    public boolean queryNsCheckFlg(){
        TParm result = query("queryNsCheckFlg");
        if(result.getErrCode()<0){
            err("ERR:"+result.getErrCode()+result.getErrText()+result.getErrName());
            return false;
        }
        if(result.getData("NS_CHECK_FLG",0).equals("N")){
            return false;
        }
        return true;
    }
    /**
     * �õ�סԺϵͳ���ò���
     * @return boolean
     */
    public Object getOdiSysParmData(String key){
        TParm result = query("queryNsCheckFlg");
        if(result.getErrCode()<0){
            err("ERR:"+result.getErrCode()+result.getErrText()+result.getErrName());
            return "";
        }
        return result.getData(key,0);
    }
    /**
     * �õ�������ҩ����
     * @param key String
     * @return Object
     */
    public Object getOpdSysParmData(String key){
        TParm result = query("queryChnSysParm");
        if(result.getErrCode()<0){
            err("ERR:"+result.getErrCode()+result.getErrText()+result.getErrName());
            return "";
        }
        return result.getData(key,0);
    }
    /**
     * ��ѯPHA_BASE���������
     * @param parm TParm
     * @return TParm
     */
    public TParm queryPhaBase(TParm parm){
        TParm result = query("queryPhaBase",parm);
        if(result.getErrCode()<0){
            err("ERR:"+result.getErrCode()+result.getErrText()+result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯSYS_FEE�����ñ��
     * @param parm TParm
     * @return TParm
     */
    public TParm querySysFee(TParm parm){
        TParm result = query("querySysFee",parm);
        if(result.getErrCode()<0){
            err("ERR:"+result.getErrCode()+result.getErrText()+result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �ж�EMR�ļ��Ƿ���ڣ�����Ϊ�ļ���FILE_NAME��CASE_NO ,�ļ�·��FILE_PATH
     * @return TParm
     */
    public TParm checkEmrFileExist(TParm parm){
        TParm result = query("checkEmrFileExist",parm);
        if(result.getErrCode()<0){
            err("ERR:"+result.getErrCode()+result.getErrText()+result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����EMR�ļ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm saveEmrFile(TParm parm,TConnection con){
        TParm result = new TParm();
        result = this.update("updateEmrFile",parm,con);
        if(result.getErrCode()<0)
            return result;
        result = this.update("insertEmrFile",parm,con);
        return result;
    }
    /**
     * �����½�����
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm saveNewEmrFile(TParm parm,TConnection con){
        TParm result = new TParm();
        result = this.update("insertEmrFile",parm,con);
        return result;
    }
    /**
     * �޸Ĵ�λ��
     * @param parm TParm
     * @return TParm
     */
    public TParm modifBedNoUD(TParm parm,TConnection con){
        TParm result = new TParm();
        result = this.update("modifBedNoUD",parm,con);
        return result;
    }
    /**
     * ����EMR�ļ�״̬
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm updateEmrFile(TParm parm,TConnection con){
        TParm result = new TParm();
        result = this.update("updateEmrFile",parm,con);
        return result;
    }
    /**
     * ����EMR�ļ�״̬
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm writeEmrFile(TParm parm,TConnection con){
    	TParm result = new TParm();
    	result = this.update("writeEmrFile",parm,con);
    	return result;
    }
    /**
     * ����EMRPDF״̬
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm writePDFEmrFile(TParm parm,TConnection con){
        TParm result = new TParm();
        result = this.update("writePDFEmrFile",parm,con);
        return result;
    }
    
    /**
     * ͨ���ļ�������״̬
     * @param parm
     * @param con
     * @return
     */
    public TParm updateEmrFileByFile(TParm parm,TConnection con){
        TParm result = new TParm();
        result = this.update("updateEmrFileByFile",parm,con);
        return result;
    }
    /**
     * ����������ҽ��
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm onSaveOdiOrder(TParm parm,TConnection con){
        TParm result = new TParm();
        result = this.update("insertOdiOrder",parm,con);
        return result;
    } 

    /**
     * ����ODI_ORDER����δͣ��ҽ����ͣ��ʱ�����ڵ�ǰʱ��ҽ���Ĳ����ʹ���
     * 
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm modifyOdiOrderStationAndBed(TParm parm, TConnection con) {// wanglong add 20140728
        TParm result = new TParm();
        result = this.update("modifyOdiOrderStationAndBed", parm, con);
        return result;
    }

    /**
     * ����ODI_DSPNMδִ��ҽ���Ĳ����ʹ���
     * 
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm modifyOdiDspnmStationAndBed(TParm parm, TConnection con) {// wanglong add 20140728
        TParm result = new TParm();
        result = this.update("modifyOdiDspnmStationAndBed", parm, con);
        return result;
    }
}
