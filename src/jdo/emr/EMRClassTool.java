package jdo.emr;

import java.util.HashMap;
import java.util.Map;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
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
 * @author not attributable
 * @version 1.0
 */
public class EMRClassTool extends TJDOTool {
    //ʹ��������ǵ�����,ֻ�ܳ�ʼ��һ������
     private static EMRClassTool instance = null;
//�������Ȩ����Ϊ˽��,����ͨ�����췽��ʵ����,ֻ��ͨ����̬���������õ�����Ķ���,���Ҹö���ֻ�ᱻʵ����һ��
     private EMRClassTool() {

         //����Module�ļ�,�ļ���ʽ������˵��
         this.setModuleName("emr\\EMRClassModule.x");
         onInit();
     }

     /**
      * ��̬��������ֻ����һ��ʵ��
      * @return MyJDOTool
      */
     public static EMRClassTool getNewInstance() {
         if (instance == null) {
             instance = new EMRClassTool();
         }
         return instance;
     }
     /**
         *���ڵ��ѯ
      */
     public TParm Select(TParm parm) {
         if (parm == null) {
             err("ERR:" + parm);
         }
         TParm result = this.query("select", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * �ӽڵ��ѯ
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
      * Insert
      *
      *
      */
     public TParm Insert(TParm parm) {
         if (parm == null) {
             err("ERR:" + parm);
         }
         TParm result = this.update("insert", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * onUpdate
      *
      *
      */
     public TParm onUpdate(TParm parm) {
         if (parm == null) {
             err("ERR:" + parm);
         }
         TParm result = this.update("update", parm);
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
      * @param conn
      * @return
      */
     public TParm onSave(TParm parm, TConnection conn) {//wanglong add 20140430
         TParm result = new TParm();
         Map inMap = (HashMap) parm.getData("IN_MAP");
         String[] sql = (String[]) inMap.get("SQL");
         if (sql == null) {
             return result;
         }
         if (sql.length < 1) {
             return result;
         }
         for (String tempSql : sql) {
             result = new TParm(TJDODBTool.getInstance().update(tempSql, conn));
             if (result.getErrCode() != 0) {
                 return result;
             }
         }
         return result;
     }
     

}
