package com.javahis.ui.main;

import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * @author whaosoft
 *
 */
public class SYSLoginAuthTool {

	/**
	 * �õ������б����Ȩ��(Treeר��)
	 *
	 * @return userId
	 * @param roleCode
	 *            String Ȩ�ޱ��
	 * @return TParm
	 */
    public TParm getStructureTreeForRoleAndUserAuth(String userId,String roleCode){

    	TParm authData = this.getUserAuth(userId);

        return new TParm(getStructureTreeForRole("SYS_SUBSYSTEM",roleCode,authData));
    }

    /**
     * �õ������б����Ȩ��(Treeר��)
     * @param groupID String ����
     * @param roleCode String Ȩ�ޱ��
     * @param authData TParm
     * @return Map
     */
    private Map getStructureTreeForRole(String groupID,String roleCode,TParm authData){

        TParm parm = new TParm(getStructureForRole(groupID,roleCode));
     	this.doProcessAuth(groupID,parm, authData);

        int count = parm.getCount();
        for(int i = 0;i < count;i++) {

            String id = parm.getValue("ID",i);
            parm.addData("CHILD",getStructureTreeForRole(id,roleCode,authData));
        }
        return parm.getData();
    }

    /**
     *
     * @param groupID
     * @param parm
     * @param authData
     */
    private void doProcessAuth(String groupID,TParm parm, TParm authData) {

		int count = authData.getCount();

		for (int i = 0; i < count; i++) {

            String group = authData.getValue("GROUP_ID", i);
            if( group.equals(groupID) ){
            	parm.addRowData(authData, i);
            }
		}
	}

    /**
	 * �õ������б����Ȩ��
	 *
	 * @param groupID
	 *            String ����
	 * @param roleCode
	 *            String Ȩ�ޱ��
	 * @return Map
	 */
    private Map getStructureForRole(String groupID,String roleCode){

        String sql =
            "SELECT GROUP_ID,ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,TYPE,PARENT_ID,STATE,DATA" +
            "     FROM SYS_DICTIONARY,SYS_ROLE_POPEDOM" +
            "    WHERE GROUP_ID='" + groupID + "'" +
            "      AND GROUP_ID=SYS_ROLE_POPEDOM.GROUP_CODE" +
            "      AND ID=SYS_ROLE_POPEDOM.CODE" +
            "      AND SYS_ROLE_POPEDOM.ROLE_CODE='" + roleCode + "'" +
            " ORDER BY SEQ";

        return this.getDBTool().select(sql);
    }

    /**
     *
     * @param userId
     * @return
     */
    private TParm getUserAuth(String userId){

    	String sql =
        " SELECT GROUP_ID,ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,TYPE,PARENT_ID,STATE,DATA " +
        " FROM SYS_DICTIONARY D " +
        " INNER JOIN SYS_USER_AUTH U ON D.ID = U.AUTH_CODE AND D.GROUP_ID = U.GROUP_CODE" +
        " AND U.USER_ID = '"+userId+"'"+
        " ORDER BY SEQ,GROUP_ID";

        return  new TParm( this.getDBTool().select(sql) );
    }


    /**
     *
     * @return
     */
    private TJDODBTool getDBTool(){
    	return TJDODBTool.getInstance();
    }

}
