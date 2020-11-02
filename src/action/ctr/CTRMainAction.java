package action.ctr;

import com.dongyang.action.TAction;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import jdo.ctr.CTRMainTool;
import jdo.ctr.CTRTool;
/**
 *
 * <p>Title: ҽ��ܿ�</p>
 *
 * <p>Description: ҽ��ܿ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author shibl
 * @version 1.0
 */
public class CTRMainAction
    extends TAction {
    /**
     * ɾ������ҽ��
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteCTRMaincode(TParm parm) {
        TParm mresult = new TParm();
        TConnection conn = getConnection();
        mresult = CTRTool.getInstance().onMDelete(parm, conn);
       if (mresult.getErrCode() < 0) {
            conn.close();
            return mresult;
        }
        conn.commit();
        conn.close();
        return mresult;
    }
    /**
   * ɾ��ϸ��ҽ��
   * @param parm TParm
   * @return TParm
   */
   public TParm deleteCTRDetailcode(TParm parm) {
      TParm result = new TParm();
      TConnection conn = getConnection();
      result = CTRTool.getInstance().onDDelete(parm, conn);
     if (result.getErrCode() < 0) {
          conn.close();
          return result;
      }
      conn.commit();
      conn.close();
      return result;
  }
  /**
  * ��������ҽ��
  * @param parm TParm
  * @return TParm
  */
  public TParm InsertCTRMaincode(TParm parm) {
     TParm result = new TParm();
     TConnection conn = getConnection();
     result = CTRTool.getInstance().onMInsert(parm, conn);
    if (result.getErrCode() < 0) {
         conn.close();
         return result;
     }
     conn.commit();
     conn.close();
     return result;
 }

}
