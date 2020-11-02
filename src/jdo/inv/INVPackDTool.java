package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title: �����������ϸ��</p>
 *
 * <p>Description: �����������ϸ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 2009-4-30
 * @version 1.0
 */
public class INVPackDTool extends TJDOTool {
    /**
  * ʵ��
  */
 public static INVPackDTool instanceObject;
 /**
  * �õ�ʵ��
  * @return INVVerifyinTool
  */
 public static INVPackDTool getInstance() {
     if (instanceObject == null)
         instanceObject = new INVPackDTool();
     return instanceObject;
 }

 /**
  * ������
  */
 public INVPackDTool() {
     setModuleName("inv\\INVPackDModule.x");
     onInit();
 }
 /**
  * ������������ϸ
  * @param packCode String
  * @return TParm
  */
 public TParm getPackDetial(String packCode){
    if(packCode==null||packCode.length()==0)
        return null;
    TParm parm=new TParm();
    parm.setData("PACK_CODE",packCode);
    TParm result = query("getPackD", parm);
     if (result.getErrCode() < 0)
         err(result.getErrCode() + " " + result.getErrText());
     return result;
}
 /**
   * ����������ϸ��ʹ������
   * @param packCode String
   * @return TParm
   */
  public TParm getPackType(String packCode,String invCode){
     if(packCode==null||packCode.length()==0||invCode==null||invCode.length()==0)
         return null;
     TParm parm=new TParm();
     parm.setData("PACK_CODE",packCode);
     parm.setData("INV_CODE",invCode);
     TParm result = query("getPackType", parm);
      if (result.getErrCode() < 0)
          err(result.getErrCode() + " " + result.getErrText());
      return result;
 }


}
