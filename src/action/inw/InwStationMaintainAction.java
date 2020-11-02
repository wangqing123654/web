package action.inw;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import jdo.inw.InwStationMaintainTool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: סԺ��ʿվ����ά��Action</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class InwStationMaintainAction
    extends TAction{
    public InwStationMaintainAction() {
    }

    /**
     * չ�����
     * @param parm
     * @return TParm
     */
    public TParm onSave(TParm parm) {
       TParm result = new TParm();
       //����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
       TConnection connection = getConnection();

       //���ó��ڴ��õ�չ������
       result = InwStationMaintainTool.getInstance().unfold(parm, connection);
       if (result.getErrCode() < 0) {
           System.out.println(result.getErrText());
           connection.rollback();
           connection.close();
           return result;
       }

       //��������������ⲿ�ӿ�Ҳ������InwStationMaintainTool�е���
       //---------------

       connection.commit();
       connection.close();
       return result;
   }

}
