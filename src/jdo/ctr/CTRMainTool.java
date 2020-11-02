package jdo.ctr;
import com.dongyang.data.*;
import com.dongyang.jdo.*;
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
* @author
* @version 1.0
*/
 public class CTRMainTool
    extends TJDOTool {

//ʹ��������ǵ�����,ֻ�ܳ�ʼ��һ������
private static CTRMainTool instance = null;
private CTRMainTool() {

    //����Module�ļ�,�ļ���ʽ������˵��
    this.setModuleName("ctr\\CTRMainModule.x");
    onInit();
}

/**
 *
 * @return
 */
public static CTRMainTool getNewInstance() {
    if (instance == null) {
        instance = new CTRMainTool();
    }
    return instance;
}
//������ѯ
public TParm onMQuery(TParm parm) {
    TParm result = this.query("Mquery", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
//ϸ�������ѯ
public TParm onDQuery(TParm parm) {
    TParm result = this.query("Dquery", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
//ϸ���FU��ѯ
public TParm onDDQuery(TParm parm) {
    TParm result = this.query("DDquery", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
/**��������
 * onInsert
 *
 * @param parm TParm
 * @return TParm
 */
public TParm onMInsert(TParm parm,TConnection conn) {
    if (parm == null) {
        err("ERR:" + parm);
    }
    TParm result = this.update("Minsert", parm,conn);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
/**ϸ���������
 * onInsert
 *
 * @param parm TParm
 * @return TParm
 */
public TParm onDInsert(TParm parm) {
    if (parm == null) {
        err("ERR:" + parm);
    }
    TParm result = this.update("Dinsert", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
/**��������
 * onInsert
 *
 * @param parm TParm
 * @return TParm
 */
public TParm onMUpdate(TParm parm) {
    if (parm == null) {
        err("ERR:" + parm);
    }
    TParm result = this.update("Mupdate", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
/**ϸ���������
 * onInsert
 *
 * @param parm TParm
 * @return TParm
 */
public TParm onDUpdate(TParm parm) {
    if (parm == null) {
        err("ERR:" + parm);
    }
    TParm result = this.update("Dupdate", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
/**�����ɾ��
 * onInsert
 *
 * @param parm TParm
 * @return TParm
 */
public TParm onMDelete(TParm parm,TConnection conn) {
    if (parm == null) {
        err("ERR:" + parm);
    }
    TParm result = this.update("Mdelete", parm,conn);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
/**ϸ������ɾ��
 * onInsert
 *
 * @param parm TParm
 * @return TParm
 */
public TParm onDDelete(TParm parm,TConnection conn) {
    if (parm == null) {
        err("ERR:" + parm);
    }
    TParm result = this.update("Ddelete", parm,conn);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
}

