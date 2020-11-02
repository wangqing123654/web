package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ҽ�������㵵</p>
 *
 * <p>Description: ҽ�������㵵</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author pangben 20111107
 * @version 1.0
 */
public class INSOpdTXTool extends TJDOTool{
    /**
   * ʵ��
   */
  public static INSOpdTXTool instanceObject;
  /**
   * �õ�ʵ��
   * @return INSOpdApproveTool
   */
  public static INSOpdTXTool getInstance() {
      if (instanceObject == null)
          instanceObject = new INSOpdTXTool();
      return instanceObject;
  }

  /**
   * ������
   */
  public INSOpdTXTool() {
      setModuleName("ins\\INSOpdTXModule.x");
      onInit();
  }

  /**
   * �����������
   * @param parm TParm
   * @return TParm
   */
  public TParm insertINSOpd(TParm parm, TConnection conn) {
      TParm result = update("insertINSOpd", parm, conn);
      return result;
  }
  /**
   * INSAMT_FLG���˱�־Ϊ3
   * @param parm
   * @param conn
   * @return
   */
  public TParm updateINSOpd(TParm parm, TConnection conn){
	  TParm result = update("updateINSOpd", parm, conn);
      return result;
	  
  }
  /**
   * ɾ������
   * @param parm
   * @param conn
   * @return
   */
  public TParm deleteINSOpd(TParm parm, TConnection conn) {
  	TParm result = update("deleteINSOpd", parm, conn);
      return result;
  }
  /**
   * ��ѯ���SEQ_NO
   * @param parm
   * @return
   */
  public TParm selectMAXSeqNo(TParm parm){
	  TParm result = query("selectMAXSeqNo", parm);
      return result;
  }
  
}
