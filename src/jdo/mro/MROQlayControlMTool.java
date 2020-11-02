package jdo.mro;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.javahis.util.StringUtil;
import java.sql.Timestamp;

/**
 * <p>Title: �ʿؼ�¼����Tool</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2011.5.5
 * @version 1.0
 */
public class MROQlayControlMTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static MROQlayControlMTool instanceObject;

    /**
     * �õ�ʵ��
     * @return MROQlayControlMTool
     */
    public static MROQlayControlMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROQlayControlMTool();
        return instanceObject;
    }

    public MROQlayControlMTool() {
        this.setModuleName("mro\\MROQlayControlMModule.x");
        this.onInit();
    }

    /**
     * ��ѯ
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("update", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ��
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDelete(TParm parm,TConnection conn){//add by wanglong 20130819
        TParm result = update("delete",parm,conn);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * ��ѯ�ʿسɼ�
     * @param parm TParm
     * @return TParm
     */
	public TParm onQueryQlayControlScore(TParm parm) {
		TParm result = this.query("queryQlayControlScore", parm);
		Timestamp date = SystemTool.getInstance().getDate();
		String type = parm.getValue("TYPE");// ��Ժ����Ժ
		for (int i = 0; i < result.getCount("CASE_NO"); i++) {
			// 1.��������
			result.setData("AGE", i,// modify by wanglong 20121127
					StringUtil.showAge(result.getTimestamp("BIRTH_DATE", i), date));
			result.setData("STATUSTYPE", i, type);
		}
		return result;
	}
    
    /**
     * ����������ѯ��Ժ��Ժ������Ϣ
     * @param parm TParm
     * @return TParm
     */
	public TParm queryQlayControlSUM(TParm parm) {//modify by wanglong 20130819
        String sql="SELECT A.MR_NO, A.CASE_NO, A.IPD_NO, " +
        		"          A.TYPERESULT,A.SUMSCODE " +
        		"     FROM MRO_RECORD A,ADM_INP B " +
        		"    WHERE A.MR_NO = B.MR_NO " +
        		"      AND A.CASE_NO IN (#)" +
        		" ORDER BY A.CASE_NO";
        if (parm.getValue("CASE_NO").length() > 0) {
            sql = sql.replaceFirst("#", parm.getValue("CASE_NO"));
        } else {
            sql = sql.replaceFirst("#", parm.getValue("CASE_LIST"));
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		TParm result = this.query("queryQlayControlSUM", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
    /**
     * ���ɳ���1000Ԫ�ص�CASE_NO IN()���
     * @param name
     * @param Nos
     * @return �������ơ� CASE_NO IN (X1,X2,X3) OR CASE_NO IN (Y1,Y2,Y3)��
     */
    public static String getInStatement(String name, TParm Nos) {//add by wanglong 20130819
        if (Nos.getCount("CASE_NO") < 1) {
            return " 1=1 ";
        }
        StringBuffer inStr = new StringBuffer();
        inStr.append(name + " IN ('");
        for (int i = 0; i < Nos.getCount("CASE_NO") ; i++) {
            inStr.append(Nos.getValue("CASE_NO", i));
            if ((i + 1) != Nos.getCount("CASE_NO")) {
                if ((i + 1) % 999 != 0) {
                    inStr.append("','");
                } else if (((i + 1) % 999 == 0)) {
                    inStr.append("') OR " + name + " IN ('");
                }
            }
        }
        inStr.append("')");
        return inStr.toString();
    }
    
}
