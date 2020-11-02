package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title: �ٴ�·��������ԭ���ֵ�</p>
 *
 * <p>Description: �ٴ�·��������ԭ���ֵ�</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author pangben 20150811
 * @version 1.0
 */
public class CLPCauseHistoryTool extends TJDOTool {
	 /**
     * ʵ��
     */
    private static CLPCauseHistoryTool instanceObject;
    /**
     * ��ʼ�������
     * @return ClpchkTypeTool
     */
    public static CLPCauseHistoryTool getInstance() {
        if (null == instanceObject)
            instanceObject = new CLPCauseHistoryTool();
        return instanceObject;
    }
    /**
    * ���췽��
    */
   public CLPCauseHistoryTool() {
       this.setModuleName("clp\\CLPCauseHistoryModule.x");
       onInit();
   }
   /**
    * 
   * @Title: insertClpCauseHistory
   * @Description: TODO(��Ӳ�����ԭ��)
   * @author pangben
   * @param parm
   * @param conn
   * @return
   * @throws
    */
   public TParm insertClpCauseHistory(TParm parm,TConnection conn){
	   TParm result = this.update("insertClpCauseHistory", parm,conn);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 
   * @Title: queryCauseHistory
   * @Description: TODO(��ѯ����)
   * @author pangben
   * @param parm
   * @return
   * @throws
    */
   public TParm queryCauseHistory(TParm parm){
	   TParm result = this.query("queryCauseHistory", parm);
       return result;
   }
   /**
    * 
   * @Title: queryMaxSeqNo
   * @Description: TODO(��ѯ������)
   * @author pangben
   * @param parm
   * @return
   * @throws
    */
   public TParm queryMaxSeqNo(TParm parm){
	   TParm result = this.query("queryMaxSeqNo", parm);
       return result;
   }
}
