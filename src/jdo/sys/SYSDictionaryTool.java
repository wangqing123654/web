package jdo.sys;

import java.util.HashMap;
import java.util.Map;

import com.dongyang.jdo.TJDOTool;
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
public class SYSDictionaryTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    private static SYSDictionaryTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PatTool
     */
    public static SYSDictionaryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSDictionaryTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SYSDictionaryTool() {
        setModuleName("sys\\SYSDictionaryModule.x");
        onInit();
    }

    /**
     * ��ʼ�����棬��ѯ���е�����
     * @return TParm
     */
    public TParm select(TParm parm) {
        TParm result = query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm insert(TParm parm) {
        TParm result = new TParm();
        result = update("insert", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insert(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm update(TParm parm) {
        TParm result = new TParm();
        result = update("update", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm update(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("update", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
    * �޸�Ĭ��ע��
    * ======pangben 2011-12-30
    * @param parm TParm
    * @param conn TConnection
    * @return TParm
    */
   public TParm updateFlg(TParm parm, TConnection conn) {
       TParm result = new TParm();
       result = update("updateFlg", parm, conn);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }

    /**
     *
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm delete(TParm parm) {
        TParm result = new TParm();
        result = update("delete", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     *
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm delete(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("delete", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     *
     * @return TParm
     */
    public TParm onUpdatePopedom(TParm parm, TConnection conn) {
        // ���ݼ��
        if (parm == null)
            return null;
        TParm result = new TParm();

        Object parmM = parm.getData("PARM_M");
        if (parmM != null) {
            TParm inParm = parm.getParm("PARM_M");
            result = this.update(inParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }

        Object parmD = parm.getData("PARM_D");
        if (parmD != null) {
            TParm inParm = parm.getParm("PARM_D");
            result = this.insert(inParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * ɾ��Ŀ¼����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeletePath(TParm parm, TConnection conn) {
        // ���ݼ��
        if (parm == null)
            return null;
        TParm result = new TParm();
        return result;
    }

    /**
     * ɾ������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeletePrg(TParm parm, TConnection conn) {
        // ���ݼ��
        if (parm == null)
            return null;
        TParm result = new TParm();
        // ɾ���ҽӳ���
        Object prgParm = parm.getData("PRG_PARM");
        if (prgParm != null) {
            TParm inParm = parm.getParm("PRG_PARM");
            result = this.delete(inParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }

        // ɾ����������Ȩ��
        Object popedomParm = parm.getData("POPEDOM_PARM");
        if (popedomParm != null) {
            TParm inParm = parm.getParm("POPEDOM_PARM");
            for (int i = 0; i < inParm.getCount(); i++) {
                result = this.delete(inParm.getRow(i), conn);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText()
                        + result.getErrName());
                    return result;
                }
            }
        }

        // ɾ�����������û���Ȩ��,ɾ���������е�ʹ���û�
        Object roleParm = parm.getData("ROLE_PARM");
        if (roleParm != null) {
            TParm inParm = parm.getParm("ROLE_PARM");
            result = SYSRolePopedomTool.getInstance().deleteUserRolePopedom(
                inParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * ɾ��Ȩ��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeletePopedom(TParm parm, TConnection conn) {
        // ���ݼ��
        if (parm == null)
            return null;
        TParm result = new TParm();
        // ɾ������Ȩ��
        Object popedomParm = parm.getData("POPEDOM_PARM");
        if (popedomParm != null) {
            TParm inParm = parm.getParm("POPEDOM_PARM");
            result = this.delete(inParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }

        // ɾ���û�Ȩ��
        Object roleParm = parm.getData("ROLE_PARM");
        if (roleParm != null) {
            TParm inParm = parm.getParm("ROLE_PARM");
            result = SYSRolePopedomTool.getInstance().deleteRolePopedom(inParm,
                conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        return result;
    }
    //2017/1/19 wuxy  
    /**
    * ��ȡ�Ա�
    */
    public Map getSexMap() {
    	TParm parm =  getSexParm();
    	Map map = new HashMap();
    	for(int i=0 ; i < parm.getCount(); i ++) {
    		map.put(parm.getData("ID", i), parm.getData("NAME", i));
    	}
    	return map;
    }
    
    /**
     * 
     * @return
     */
    public TParm getSexParm(){
    	TParm parm = new TParm();
    	parm.setData("GROUP_ID", "SYS_SEX");
    	parm = this.query("getListAll", parm);
    	return parm;
    }
    /**
     * ��ȡȨ��
     * @return
     */
    public TParm getRoleParm(){
    	TParm parm = new TParm();
    	parm.setData("GROUP_ID","ROLE");
    	parm = this.query("getListAll",parm);
    	return parm;
    }
    public Map getRoleMap(){
    	TParm parm = getRoleParm();
    	Map map = new HashMap();
    	for(int i=0 ; i < parm.getCount(); i ++) {
    		map.put(parm.getData("ID", i), parm.getData("NAME", i));
    	}
    	return map;
    }
    
    //2017/1/19 wuxy
}
