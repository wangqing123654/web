package jdo.inp;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
*
* <p>Title: ��������</p>
*
* <p>Description: �������뱨��</p>
*
* <p>Copyright: Copyright (c) caoyong 20139011</p>
*
* <p>Company: JavaHis</p>
*
* @author caoyong
* @version 1.0
*/
public class ConsApplicationTool extends TJDOTool{
	
	public static ConsApplicationTool instanceObject;
	
	public static ConsApplicationTool getInstance(){
		if(instanceObject==null){
			instanceObject=new ConsApplicationTool();
			
		}
		return instanceObject;
	}
	
	public ConsApplicationTool() {
		setModuleName("inp\\INPConsApplicationModule.x");
		onInit();
	}
	/**
	 * ���빫����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertBoard(TParm parm) {
		TParm result = new TParm();
		result = update("inserBoard", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ������յ�
	 * 
	 * @param parm
	 * @return
	 */
	
	public TParm insertPostRCV(TParm parm) {
		TParm result = new TParm();
		result = update("inserPostRCV", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ��ѯ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdata(TParm parm) {
		TParm result = new TParm();
		result = query("selectdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ��ѯ�����������ֵ��ҽ��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdacc(TParm parm) {
		TParm result = new TParm();
		result = query("selectdatacc", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ��ѯ�����������ָ��ҽ��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdass(TParm parm) {
		TParm result = new TParm();
		result = query("selectdatass", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ��ѯ����һ���������еĻ��������¼
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdataall(TParm parm) {
		TParm result = new TParm();
		result = query("selectall", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
		
		
	}
	
	/**
     * ɾ��
     * @param admType String
     * @param clinicTypeCode String
     * @param orderCode String
     * @return TParm
     */
    public TParm deletedata(TParm parm) {
       
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        
        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �����ʼ�
     */
    
    public TParm updateEMail(TParm parm){
        TParm result = update("updateEMail",parm);
        // �жϴ���ֵ
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
	 * ��ѯ���Ͷ���Ϣ���ֻ�����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdMess(TParm parm) {
		TParm result = new TParm();
		result = query("selectdMess", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ��ѯ�����Ƿ������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectdCons(TParm parm) {
		TParm result = new TParm();
		result = query("selectdCons", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	

}
