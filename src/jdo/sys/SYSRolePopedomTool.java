package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.javahis.util.JavaHisDebug;
/**
 *
 * <p>Title: �û�Ȩ�޹�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2008.9.26
 * @version 1.0
 */
public class SYSRolePopedomTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static SYSRolePopedomTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SYSRolePopedomTool
     */
    public static SYSRolePopedomTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSRolePopedomTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SYSRolePopedomTool() {
        setModuleName("sys\\SYSRolePopedom.x");
        onInit();
    }
    /**
     * �õ�Ȩ��
     * @param role String ��ɫ
     * @param groupCode String ��
     * @param code String ���
     * @return boolean true ��Ȩ�� false û��Ȩ��
     */
    public boolean getPopedom(String role,String groupCode,String code)
    {
        TParm parm = new TParm();
        parm.setData("ROLE_CODE",role);
        parm.setData("GROUP_CODE",groupCode);
        parm.setData("CODE",code);
        return getResultInt(query("getPopedom",parm),"COUNT") > 0;
    }
    /**
     * �õ�ϵͳ�б�
     * @param group String
     * @param roleCode String
     * @return TParm
     */
    public TParm getSystemList(String group,String roleCode)
    {
        TParm parm = DictionaryTool.getInstance().getGroupList(group);
        int count = parm.getCount();
        for(int i = 0;i < count;i++)
        {
            String id = parm.getValue("ID",i);
//            System.out.println("-----------------"+id);
            String value = getPopedom(roleCode,group,id)?"Y":"N";
            TParm childParm = getSystemList(id,roleCode);
            parm.addData("VALUE",value);
            parm.addData("CHILD",childParm.getData());
        }
        return parm;
    }
    /**
     * ��ѯ
     * @param roleCode String
     * @return TParm
     */
    public TParm queryRoleTree(String roleCode)
    {
        return getSystemList("SYS_SUBSYSTEM",roleCode);
    }
    /**
     * ����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onSave(TParm parm,TConnection connection)
    {
        TParm result = delete(parm.getParm("DELETE"),connection);
        if(result.getErrCode() < 0)
            return result;
        result = insert(parm.getParm("INSERT"),connection);
        if(result.getErrCode() < 0)
            return result;
        return result;
    }
    /**
     * ɾ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm delete(TParm parm,TConnection connection)
    {
        TParm result = new TParm();
        int count = parm.getCount();
        for(int i = 0;i < count;i++)
        {
            TParm inParm = new TParm();
            inParm.setRowData(-1, parm, i);
            result = update("delete",inParm,connection);
            if(result.getErrCode() < 0)
                return result;
        }
        return result;
    }

    /**
     * ɾ��ָ������ָ��Ȩ�޵�������Ϣ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteRolePopedom(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("deleteRolePopedom", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ�����������û���Ȩ��,ɾ���������е�ʹ���û�
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteUserRolePopedom(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("deleteUserRolePopedom", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }



    /**
     * ����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insert(TParm parm,TConnection connection)
    {
        TParm result = new TParm();
        int count = parm.getCount();
        for(int i = 0;i < count;i++)
        {
            TParm inParm = new TParm();
            inParm.setRowData(-1, parm, i);
            result = update("insert",inParm,connection);
            if(result.getErrCode() < 0)
                return result;
        }
        return result;
    }
    /**
     * �õ������б�
     * @param roleCode String ��ɫ���
     * @return TParm ID,NAME
     */
    public TParm getStructureList(String roleCode)
    {
        TParm parm = new TParm();
        parm.setData("ROLE_CODE",roleCode);
        return query("getStructureList",parm);
    }
    /**
     * �õ������б����Ȩ��
     * @param groupID String ����
     * @param roleCode String Ȩ�ޱ��
     * @return TParm
     */
    public TParm getStructureForRole(String groupID,String roleCode)
    {
        TParm parm = new TParm();
        parm.setData("GROUP_ID",groupID);
        parm.setData("ROLE_CODE",roleCode);
        return query("getStructureForRole",parm);
    }
    /**
     * �õ������б����Ȩ��(Treeר��)
     * @param roleCode String Ȩ�ޱ��
     * @return TParm
     */
    public TParm getStructureTreeForRole(String roleCode)
    {
        return getStructureTreeForRole("SYS_SUBSYSTEM",roleCode);
    }
    /**
     * �õ������б����Ȩ��(Treeר��)
     * @param groupID String ����
     * @param roleCode String Ȩ�ޱ��
     * @return TParm
     */
    public TParm getStructureTreeForRole(String groupID,String roleCode)
    {
        TParm parm = getStructureForRole(groupID,roleCode);
        int count = parm.getCount();
        for(int i = 0;i < count;i++)
        {
            String id = parm.getValue("ID",i);
            TParm childParm = getStructureTreeForRole(id,roleCode);
            parm.addData("CHILD",childParm.getData());
        }
        return parm;
    }
    public static void main(String args[])
    {
        JavaHisDebug.initClient();
        //System.out.println(SYSRolePopedomTool.getInstance().getStructureTreeForRole("ADMIN"));
        //System.out.println(SYSRolePopedomTool.getInstance().getStructureList("ADMIN"));
    }
}
