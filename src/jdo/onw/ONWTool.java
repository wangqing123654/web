package jdo.onw;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ���ﻤʿվ����ӿ�</p>
 *
 * <p>Description: ���ﻤʿվ����ӿ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-2-5
 * @version 1.0
 */
public class ONWTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static ONWTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PositionTool
     */
    public static ONWTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ONWTool();
        return instanceObject;
    }
    public ONWTool() {
        onInit();
    }
    /**
     * �޸��ż���ҽ����ϸ���Ļ�ʿ��ע
     * @param parm TParm ���������CASE_NO;RX_NO;SEQ_NO;NS_NOTE
     * @return TParm
     */
    public TParm updateNS_NOTE(TParm parm,TConnection conn){
    	//Ƥ��ִ��ʱ��add by huangjw 20141031
    	
        String sql = "UPDATE OPD_ORDER SET NS_NOTE='"+parm.getValue("NS_NOTE").replaceAll("'","''")+"', "+
             " BATCH_NO='"+parm.getValue("BATCH_NO")+"' ,SKINTEST_FLG='"+parm.getValue("SKINTEST_FLG")+"',EXEC_DR_DESC='"+parm.getValue("EXEC_DR_DESC")+"'," +
             		"EXEC_DATE=TO_DATE('"+parm.getValue("EXEC_DATE").toString().substring(0,19)+"','yyyy/MM/dd HH24:MI:SS') "+
            "WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND RX_NO='"+parm.getValue("RX_NO")+"' AND SEQ_NO='"+parm.getValue("SEQ_NO")+"'";
        
//        System.out.println("2323233333333333����������������:::"+sql);
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().update(sql,conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
