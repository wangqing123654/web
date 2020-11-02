package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title: �������ֵ�����</p>
 *
 * <p>Description: �������ֵ�����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 2009-06-01
 * @version 1.0
 */
public class INVPackMTool  extends TJDOTool {
    /**
  * ʵ��
  */
 public static INVPackMTool instanceObject;
 /**
  * �õ�ʵ��
  * @return INVVerifyinTool
  */
 public static INVPackMTool getInstance() {
     if (instanceObject == null)
         instanceObject = new INVPackMTool();
     return instanceObject;
 }

 /**
  * ������
  */
 public INVPackMTool() {
     setModuleName("inv\\INVPackMModule.x");
     onInit();
 }

 /**
  * �õ�����������GYSUsed
  * @param packCode String
  * @return TParm
  */
 public TParm getPackDesc(String packCode) {
     TParm result = new TParm();
     if(packCode==null||packCode.length()<=0)
         return result.newErrParm(-1,"��������Ϊ��");
     result.setData("IPACK_CODE", packCode);
     result = query("getPackDesc", result);
     if (result.getErrCode() < 0)
         err(result.getErrCode() + " " + result.getErrText());

     return result;
 }
}
