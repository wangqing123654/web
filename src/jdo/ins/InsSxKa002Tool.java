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
 * @author JiaoY
 * @version JavaHis 1.0
 */
public class InsSxKa002Tool extends TJDOTool {
    /**
     * ʵ��
     */
    public static InsSxKa002Tool instanceObject;
    /**
     * �õ�ʵ��
     * @return PositionTool
     */
    public static InsSxKa002Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new InsSxKa002Tool();
        return instanceObject;
    }

    /**
     * ������
     */
    public InsSxKa002Tool() {
        setModuleName("ins\\InsSxKa002Module.x");
        onInit();
    }

    /**
     * ��ѯINS_SX_KA002
     * @param parm TParm
     * @return TParm
     */
    public TParm selInSxka002(TParm parm, TConnection connection) {
        System.out.println("002 Tool ��ѯ=====��");

        TParm result = new TParm();
        result = query("query", parm);
//        System.out.println("result=" + result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
    /**
 * ��ѯINS_SX_KA002
 * @param parm TParm
 * @return TParm
 */
public TParm onQueryMaxSxka002(TParm parm, TConnection connection) {
    //System.out.println("002 Tool ��ѯ=====��");

    TParm result = new TParm();
    result = query("onQueryMaxSxka002", parm);
   // System.out.println("result=" + result);
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
        //System.out.println(" updata");
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
    public TParm onSave(TParm parm, TConnection connection) {
        //System.out.println("tool onsave");
        TParm result = new TParm();
        result = onDelete(parm.getParm(InsSxKa002List.DELETED));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        result = onUpdate(parm.getParm(InsSxKa002List.MODIFIED));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        result = onInsert(parm.getParm(InsSxKa002List.NEW));
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
        //System.out.println(" tool updata");
        int count = parm.getCount();
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
//        System.out.println("ɾ������");
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
//        System.out.println("��--------����--------------��");
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
      //  System.out.println("��--------����-------TOOL SX 002-------��");
      TParm result = new TParm();
      result = update("insert", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText()
              + result.getErrName());
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
//        System.out.println("��--------��ѯ--------------��");
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
//        System.out.println("��--------����--------------��");
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
     * ��ѯ���²��������
     * @return TParm
     */
    public TParm queryMaxDate(){
//        System.out.println("��--------��ѯ--------------��");
           TParm result = new TParm();
              result = query("onQueryMaxXaka002");
              if (result.getErrCode() < 0) {
                  err("ERR:" + result.getErrCode() + result.getErrText()
                      + result.getErrName());
                  return result;
              }
              return result;

    }


}
