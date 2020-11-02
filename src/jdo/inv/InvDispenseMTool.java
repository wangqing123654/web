package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
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
public class InvDispenseMTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvDispenseMTool instanceObject;

    /**
     * ������
     */
    public InvDispenseMTool() {
        setModuleName("inv\\INVDispenseMModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvRequestMTool
     */
    public static InvDispenseMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvDispenseMTool();
        return instanceObject;
    }

    /**
     * ��ѯ���ⵥ����
     * @param parm TParm   
     * @return TParm
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryDispenseMOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    } 
    
    /**
     * ��ѯ��Ҫ���ĳ��ⵥ
     * @param parm TParm
     * @return TParm
     */ 
    public TParm onQueryOI(TParm parm) { 
        TParm result = this.query("queryDispenseMOI", parm);  
        System.out.println("��ѯ��Ҫ���ĳ��ⵥ ==="+result);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**  
     * ��ѯ��ⵥ����
     * @param parm TParm
     * @return TParm
     */ 
    public TParm onQueryIn(TParm parm) {
        TParm result = this.query("queryDispenseMIn", parm);
        System.out.println("��ѯ��ⵥ���� ==="+result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result; 
        }
        return result;
    }
    
    /**
     * ��ѯ���ⵥ����ϸ��  
     * @param parm TParm
     * @return TParm
     */  
    public TParm onQueryDetail(TParm parm) {
        TParm result = this.query("queryDispenseMOutDetail", parm); 
        System.out.println("onQueryDetail==="+result); 
        if (result.getErrCode() < 0) {  
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �������ⵥ����(������;)
     *
     * @param parm
     * @return
     */
    public TParm onInsertOut(TParm parm, TConnection conn) {
        TParm result = this.update("createDispenseOutM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �������ⵥ����(���⼴���)
     *
     * @param parm
     * @return
     */
    public TParm onInsertOutIn(TParm parm, TConnection conn) {
        TParm result = this.update("createDispenseOutInM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * ���³��ⵥ����(����״̬)
     *
     * @param parm
     * @return 
     */
    public TParm onUpdateDispenseM(TParm parm) {  
        TParm result = this.update("onUpdateDispenseM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * ���³��ⵥ����(�������뵥����״̬,conn)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm   
     */   
    public TParm onUpdateFinaFlg(TParm parm, TConnection conn) {
        TParm result = this.update("updateFinaFlg", parm, conn);
        if (result.getErrCode() < 0) {  
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result; 
        }
        return result;
    }
    
     
    
    /**
     * ɾ���������(ȫ��)
     * @param parm TParm
     * @param conn TConnection 
     * @return TParm
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("deleteDispenseinMAll", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
