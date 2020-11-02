package jdo.sum;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.TypeTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ���������µ���Tool</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS</p>
 *
 * <p>Company:  </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class SUMNewArrivalTool
    extends TJDOTool {

    /**
     * ʵ��
     */
    public static SUMNewArrivalTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static SUMNewArrivalTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SUMNewArrivalTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SUMNewArrivalTool() {
        setModuleName("sum\\SUMNewArrivalModule.x");
        onInit();
    }

    /**
     * ����OEI��CASE_NO��ѯ�ò��˵ļ�¼--EXAMINE_DATE,USER_ID
     * @param regMethod String
     * @return TParm
     */
    public TParm selectExmDateUser(TParm date) {

        TParm result = query("selectNewArrExmUser", date);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����OEI��CASE_NO,���� ��ѯĳһ�������
     * @param regMethod String
     * @return TParm
     */
    public TParm selectOneDateDtl(TParm date) {

        TParm result = query("selectOneDayDtl", date);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����OEI��CASE_NO,���� ��ѯĳһ�������
     * @param regMethod String
     * @return TParm
     */
    public TParm selectOneDateMst(TParm date) {

        TParm result = query("selectOneDayMst", date);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ����OEI��CASE_NO������ ��ѯ��������
     * @param regMethod String
     * @return TParm
     */
    public TParm selectdataMst(TParm date) {

        TParm result = query("selectNewArrivalSign", date);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;
    }

    /**
     * ����OEI��CASE_NO������ ��ѯ�������
     * @param regMethod String
     * @return TParm
     */
    public TParm selectdataDtl(TParm date) {

        TParm result = query("selectNewArrivalSignDtl", date);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;
    }

    /**
     * ����һ��������Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertNewArrival(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("insertNewArrival", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * ����һ��������Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateNewArrival(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("updateNewArrival", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }


    /**
     * ����һ��ϸ����Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertNewArrivalDtl(TParm parm, TConnection connection) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm oneParm = new TParm();
            oneParm = (TParm) parm.getParm(i + "PARM");
            result = update("insertNewArrivalDtl", oneParm, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;

    }

    /**
     * ����һ��ϸ����Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateNewArrivalDtl(TParm parm, TConnection connection) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm oneParm = new TParm();
            oneParm = (TParm) parm.getParm(i + "PARM");
            result = update("updateNewArrivalDtl", oneParm, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;

    }



    /**
     * ����Ҫ����������Ƿ�Ӧ�����ڣ���������
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm checkIsExist(TParm parm) {
        TParm result = new TParm();
        result = query("checkIsExist", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
     /**
      * ��������������
      * TParm
      * @param parm
      * @return
      */
    public TParm getNewBornWeight(TParm parm){
    	String weight="";
    	TParm reParm=new TParm();
    	TParm result = new TParm();
        result = query("getNewBornWeight", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        weight=result.getInt("BORNWEIGHT", 0)>0?TypeTool.getString(result.getInt("BORNWEIGHT", 0)):"";
        reParm.setData("NB_WEIGHT",weight);
        //ͬһ����ŵ���С���ڵ��ֶ�
        return reParm;
    }
    /**
     * ��������Ժ����
     * TParm
     * @param parm
     * @return
     */
   public TParm getNewAdmWeight(TParm parm){
	   String weight="";
	   TParm reParm=new TParm();
   	   TParm result = new TParm();
       result = query("getNewAdmWeight", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       for(int i=0;i<result.getCount();i++){
    	  if(result.getInt("WEIGHT", i)>0) {
    		  weight=TypeTool.getString(result.getInt("WEIGHT", i));//g
    		  break;
    	  }
       }
       reParm.setData("NB_ADM_WEIGHT", weight);
     //ͬһ����ŵ���С������Сʱ����ֶ�
       return reParm;
   }
   
   /**
    * ������ҳ ��������Ժ����
    * @param parm
    * @return
    */
   public TParm getFirstDayWeight(TParm parm) {
	   String weight="";
	   TParm reParm=new TParm();
   	   TParm result = new TParm();
       result = query("getFirstDayWeight", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       for(int i=0;i < result.getCount(); i++){
    	  if(result.getInt("WEIGHT", i)>0) {
    		  weight=TypeTool.getString(result.getInt("WEIGHT", i));//g
    		  break;
    	  }
       }
       reParm.setData("NB_ADM_WEIGHT", weight);
       return reParm;
   }

}
