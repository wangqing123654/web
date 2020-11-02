package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ҽ������</p>
 *
 * <p>Description: ��Ƕʽҽ������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class InsSxKa004Tool extends TJDOTool{
    /**
    * ʵ��
    */
   public static InsSxKa004Tool instanceObject;
   /**
    * �õ�ʵ��
    * @return PositionTool
    */
   public static InsSxKa004Tool getInstance() {
       if (instanceObject == null)
           instanceObject = new InsSxKa004Tool();
       return instanceObject;
   }

   /**
    * ������
    */
   public InsSxKa004Tool() {
       setModuleName("ins\\InsSxKa004Module.x");
       onInit();
   }

   /**
    * ��ѯINS_SX_KA004
    * @param parm TParm
    * @return TParm
    */
   public TParm selInSxka004(TParm parm,TConnection connection) {
      // System.out.println("004 Tool ��ѯ=====��");

       TParm result = new TParm();
       result = query("query", parm);
//       System.out.println("result=" + result);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;

   }
   /**
* ��ѯsys_fee��ҽ��û�е�ҩƷ
* @param parm TParm
* @return TParm
*/
public TParm query_Ins_Sys(TParm parm){
  //System.out.println("��--------��ѯ--Sx004------------��");
  TParm result = new TParm();
     result = query("query_sys_ins", parm);
     if (result.getErrCode() < 0) {
         err("ERR:" + result.getErrCode() + result.getErrText()
             + result.getErrName());
         return result;
     }
     return result;

}
 /**
   * ����sys_fee�д���ҽ���в����ڵ�ҩƷ
   * @param parm TParm
   * @param conn TConnection
   * @return TParm
   */
  public TParm importOrder(TParm parm,TConnection conn){
      //System.out.println("��--------����-----Sx04---------��");
      TParm result = new TParm();
      result = update("importOrder", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText()
              + result.getErrName());
          return result;
      }
      return result;


  }

  /**
   * ��ѯINS_SX_KA004
   * @param parm TParm
   * @return TParm
   */
  public TParm onQueryMaxSxka004(TParm parm, TConnection connection) {
    //  System.out.println("004 Tool ��ѯ=====��");

      TParm result = new TParm();
      result = query("onQueryMaxSxka004", parm);
//      System.out.println("result=" + result);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;

  }




   /**
    * ����ҽ��ҩƷ
    * @param nhiCompany String
    * @return TParm
    */
   public TParm updatedata(TParm parm) {
//       System.out.println(" updata");
       TParm result = update("update", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }

   /**
    * ����
    * @param parm TParm
    * @return TParm
    */
   public TParm onSave(TParm parm,TConnection connection) {
      // System.out.println(" 004 tool onsave");
//       System.out.println("parm="+parm);
       TParm result = new TParm();
       result = onUpdate(parm.getParm(InsSxKa004List.MODIFIED));
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       result = onDelete(parm.getParm(InsSxKa004List.DELETED));
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       result = onInsert(parm.getParm(InsSxKa004List.NEW));
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }

       return result;

   }

   /**
    * ����
    * @param parm
    * @return result
    */
   public TParm onUpdate(TParm parm) {
      // System.out.println(" 004tool updata");
       int count = parm.getCount();
       //System.out.println("count="+count);
       TParm result = new TParm();
       for (int i = 0; i < count; i++) {
           TParm inParm = new TParm();
           inParm.setRowData( -1, parm, i);
           result = this.updatedata(inParm);
           if (result.getErrCode() < 0)
               return result;
       }
       return result;
   }
   /**
 * ɾ��
 * @param parm
 * @return result
 */
public TParm onDelete(TParm parm) {
    int count = parm.getCount();
    TParm result = new TParm();
    for (int i = 0; i < count; i++) {
        TParm inParm = new TParm();
        inParm.setRowData( -1, parm, i);
        result = this.deletedata(inParm);
        if (result.getErrCode() < 0)
            return result;
    }

    return result;
}

/**
 * ɾ������
 * @param parm
 * @return
 */
public TParm deletedata(TParm parm) {
//    System.out.println("ɾ������");
    TParm result = new TParm();
    result = update("delete", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}

/**
 * ����
 * @param parm
 * @return result
 */
public TParm onInsert(TParm parm) {
    int count = parm.getCount();
    TParm result = new TParm();
    for (int i = 0; i < count; i++) {
        TParm inParm = new TParm();
        inParm.setRowData( -1, parm, i);
        result = this.insertdata(inParm);
        if (result.getErrCode() < 0)
            return result;
    }
    return result;
}

/**
 * ����
 * @param parm TParm
 * @return TParm
 */
public TParm insertdata(TParm parm) {
   // System.out.println("��--------����--------------��");
    TParm result = new TParm();
    result = update("insert", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
/**
 * ����
 * @param parm TParm
 * @return TParm
 */
public TParm insertdata(TParm parm,TConnection conn) {
    //System.out.println("��--------����--------------��");
    TParm result = new TParm();
    result = update("insert", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}


}
