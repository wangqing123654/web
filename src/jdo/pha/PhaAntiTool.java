package jdo.pha;

import jdo.udd.UddChnCheckTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>
* Title: ����ҩƷ���Ʊ�
* </p>
*
* <p>
* Description:סԺ����ҩƷ����
* </p>
*
* <p>
* Copyright: Copyright (c) pangben 2013
* </p>
*
* <p>
* Company:bluecore
* </p>
*
* @author pangben 2013-9-4
* @version 4.0
*/
public class PhaAntiTool extends TJDOTool {
	 /**
     * ʵ��
     */
    public static PhaAntiTool instanceObject;
    /**
     * �õ�ʵ��
     * @return UddChnCheckTool
     */
    public static PhaAntiTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new PhaAntiTool();
        }
        return instanceObject;
    }

    /**
     * ������
     */
    public PhaAntiTool() {
        setModuleName("pha\\phaAntiModule.x");
        onInit();
    }
    /**
     * �������
     * @param parm
     * @param connection
     * @return
     */
    public TParm insertPhaAnti(TParm parm, TConnection connection){
    	TParm result = update("insertPhaAnti", parm, connection);
    	return result;
    }
    /**
     * ��ѯ����
     * @param parm
     * @return
     */
    public TParm queryPhaAnti(TParm parm){
    	TParm result = query("queryPhaAnti", parm);
    	return result;
    }
    /**
     * ������ѯ���� ,סԺҽ��վ�����Ӳ���ʹ��
     * @param parm
     * @return
     */
    public TParm queryPhaAntiStatus(TParm parm){
    	TParm result = query("queryPhaAntiStatus", parm);
    	return result;
    }
    /**
     * �޸�ҽ��ʹ��ע��
     * @param parm
     * @param connection
     * @return
     */
    public TParm updatePhaAntiUseFlg(TParm parm, TConnection connection){
    	TParm result = update("updatePhaAntiUseFlg", parm, connection);
    	return result;
    }
    /**
     * �޸Ļ���ע��
     * @param parm
     * @param connection
     * @return
     * ===========pangben 2014-2-25
     */
    public TParm updatePhaAntiApproveFlg(TParm parm, TConnection connection){
    	TParm result = update("updatePhaAntiApproveFlg", parm, connection);
    	return result;
    }
    /**
     * �޸�ҽ��ʹ��ע�Ǻ��������
     * @param parm
     * @param connection
     * @return
     */
    public TParm updatePhaAnti(TParm parm, TConnection connection){
    	TParm result = update("updatePhaAntiCheckUseFlg", parm, connection);
    	return result;
    }
}
