package action.clp;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.clp.PackTool;
import jdo.clp.BscInfoTool;

import java.io.Serializable;
import java.util.Map;
import com.dongyang.jdo.TJDODBTool;
import jdo.clp.ThrpyschdmTool;

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
public class CLPBscInfoAction  extends TAction {
    public CLPBscInfoAction() {
    }
    /**
     * ɾ���ٴ�·�����
     * @param parm
     * @return
     */
    public TParm deleteBscInfo(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        // ɾ���ٴ�·�����
        result = BscInfoTool.getInstance().delete(parm, connection);
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        //����ʷ���еĵ�ǰʹ�õİ汾�ŵ��������ݽ���ʱ���޸ĳɵ�ǰʱ�䣬����״̬��ΪN==liling
        result = BscInfoTool.getInstance(). updateBscHis(parm, connection);
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        //ɾ�����
        result=this.deleteICD(parm.getValue("CLNCPATH_CODE"),connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        // ɾ������������ʱ��
        result = new TParm(TJDODBTool.getInstance().update(
                " DELETE FROM CLP_THRPYSCHDM WHERE REGION_CODE = '" +
                parm.getValue("REGION_CODE") + "' AND CLNCPATH_CODE = '" +
                parm.getValue("CLNCPATH_CODE") + "'", connection));
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        // ɾ������������������ϸ
        result = new TParm(TJDODBTool.getInstance().update(
                " DELETE FROM CLP_PACK WHERE REGION_CODE = '" +
                parm.getValue("REGION_CODE") + "' AND CLNCPATH_CODE = '" +
                parm.getValue("CLNCPATH_CODE") + "'", connection));
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
      //ɾ���ٴ�·����ʱ��ҽ����ʷ��ɾ����ͨ���޸Ľ���ʱ��ذ汾==liling
       /* result = new TParm(TJDODBTool.getInstance().update(
                " DELETE FROM CLP_PACK_HISTORY WHERE REGION_CODE = '" +
                parm.getValue("REGION_CODE") + "' AND CLNCPATH_CODE = '" +
                parm.getValue("CLNCPATH_CODE") + "'", connection));*/
        result = new TParm(TJDODBTool.getInstance().update(
                " UPDATE CLP_PACK_HISTORY SET END_DATE= '" +
                parm.getValue("END_DATE") + "' WHERE REGION_CODE = '" +
                parm.getValue("REGION_CODE") + "' AND CLNCPATH_CODE = '" +
                parm.getValue("CLNCPATH_CODE") + "' AND VERSION ='"+ parm.getValue("VERSION") + "' ", connection));
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * ɾ������ʱ��
     * @param parm
     * @return
     */
    public TParm deleteThrpyschdm(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        // ɾ������ʱ��
        result = ThrpyschdmTool.getInstance().delete(parm, connection);
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        // ɾ������������������ϸ
        result = new TParm(TJDODBTool.getInstance().update(
                " DELETE FROM CLP_PACK WHERE REGION_CODE = '" +
                parm.getValue("REGION_CODE") + "' AND CLNCPATH_CODE = '" +
                parm.getValue("CLNCPATH_CODE") + "' AND SCHD_CODE = '" +
                parm.getValue("SCHD_CODE") + "'", connection));
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        //ɾ���ٴ�·����ʱ��ҽ����ʷ��ɾ����ͨ���޸Ľ���ʱ��ذ汾==liling
        /*
        result = new TParm(TJDODBTool.getInstance().update(
                " DELETE FROM CLP_PACK_HISTORY WHERE REGION_CODE = '" +
                parm.getValue("REGION_CODE") + "' AND CLNCPATH_CODE = '" +
                parm.getValue("CLNCPATH_CODE") + "' AND SCHD_CODE = '" +
                parm.getValue("SCHD_CODE") + "'", connection));*/
        result = new TParm(TJDODBTool.getInstance().update(
        		" UPDATE CLP_PACK_HISTORY SET END_DATE= '" +
                parm.getValue("END_DATE") + "'  WHERE REGION_CODE = '" +
                parm.getValue("REGION_CODE") + "' AND CLNCPATH_CODE = '" +
                parm.getValue("CLNCPATH_CODE") + "' AND SCHD_CODE = '" +
                parm.getValue("SCHD_CODE") + "' AND VERSION ='"+ parm.getValue("VERSION") + "' ", connection));
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * �����ٴ�·��
     * @param parm TParm
     * @return TParm
     */
    public TParm insertBSCInfo(TParm saveParm) {
        TParm basinfoParm = new TParm((Map) saveParm.getData("bscinfoParm"));
        TParm icdParm = new TParm((Map) saveParm.getData("icdParm"));
        TParm optParm = new TParm((Map) saveParm.getData("optParm"));
        Map basicInfo = (Map) saveParm.getData("basicInfo");
        TParm result = new TParm();
        TConnection connection = getConnection();
        //��׼����
        result = BscInfoTool.getInstance().insert(basinfoParm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        result = insertICD(basicInfo, icdParm, basinfoParm, optParm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        //��׼��ʷ��
        result = BscInfoTool.getInstance().insertHis(basinfoParm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
   
    /**
     * �������
     * @param 
     * @param connection TConnection
     * @return TParm
     */
    private TParm insertICD(Map basicInfo, TParm icdParm, TParm basinfoParm,
                            TParm optParm, TConnection connection) {
        TParm result = new TParm();
        //diagnose
        StringBuffer sqlbf = new StringBuffer("");
        sqlbf.delete(0, sqlbf.length());
        sqlbf.append("SELECT CASE  (COUNT(MAX(SEQ))) WHEN 0 THEN '1' ELSE TO_CHAR((MAX(SEQ))) END AS  SEQ FROM CLP_CLNCPATHERDIAGICD GROUP BY SEQ ");
        result = new TParm(TJDODBTool.getInstance().select(sqlbf.toString()));
        int seq = result.getInt("SEQ", 0);
        for (int i = 0; i < icdParm.getCount("diagnose_icd_begin"); i++) {
            if ("".equals(icdParm.getValue("diagnose_icd_type_begin", i))) {
                continue;
            }
            sqlbf.delete(0, sqlbf.length());
            sqlbf.append("INSERT INTO CLP_CLNCPATHERDIAGICD VALUES( ");
            sqlbf.append("'" + basinfoParm.getValue("CLNCPATH_CODE") + "',");
            sqlbf.append("'" + icdParm.getValue("diagnose_icd_type_begin", i) +
                         "',");
            sqlbf.append("'" + icdParm.getValue("diagnose_icd_begin", i) + "',");
            sqlbf.append("'" + icdParm.getValue("diagnose_icd_type_end", i) +
                         "',");
            sqlbf.append("'" + icdParm.getValue("diagnose_icd_end", i) + "',");
            sqlbf.append((++seq) + ",");
            sqlbf.append("TO_DATE('" + basicInfo.get("OPT_DATE") +
                         "','YYYYMMDD'),");
            sqlbf.append("'" + basicInfo.get("OPT_TERM") + "',");
            sqlbf.append("'" + basicInfo.get("OPT_USER") + "'");
            sqlbf.append(")");
            result = new TParm(TJDODBTool.getInstance().update(sqlbf.toString(),
                    connection));
            if (result.getErrCode() < 0) {
            	connection.rollback();
                connection.close();
                return result;
            }
        }
        //opt diagnose
        sqlbf.delete(0, sqlbf.length());
        sqlbf.append("SELECT CASE  (COUNT(MAX(SEQ))) WHEN 0 THEN '1' ELSE TO_CHAR((MAX(SEQ))) END AS  SEQ FROM CLP_CLNCPATHEROPTICD GROUP BY SEQ");
        result = new TParm(TJDODBTool.getInstance().select(sqlbf.toString()));
        seq = result.getInt("SEQ", 0);
        for (int i = 0; i < optParm.getCount("operator_diagnose_icd_begin"); i++) {
            if ("".equals(optParm.getValue("operator_diagnose_icd_begin", i))) {
                continue;
            }
            sqlbf.delete(0, sqlbf.length());
            sqlbf.append("INSERT INTO CLP_CLNCPATHEROPTICD VALUES( ");
            sqlbf.append("'" + basinfoParm.getValue("CLNCPATH_CODE") + "',");
            sqlbf.append("'" +
                         optParm.getValue("operator_diagnose_icd_begin", i) +
                         "',");
            sqlbf.append("'" + optParm.getValue("operator_diagnose_icd_end", i) +
                         "',");
            sqlbf.append((++seq) + ",");
            sqlbf.append("TO_DATE('" + basicInfo.get("OPT_DATE") +
                         "','YYYYMMDD'),");
            sqlbf.append("'" + basicInfo.get("OPT_TERM") + "',");
            sqlbf.append("'" + basicInfo.get("OPT_USER") + "'");
            sqlbf.append(")");
            result = new TParm(TJDODBTool.getInstance().update(sqlbf.toString(),
                    connection));
            if (result.getErrCode() < 0) {
            	connection.rollback();
                connection.close();
                return result;
            }
        }
        return result;
    }
    /**
     * �����ٴ�·��
     * @param parm TParm
     * @return TParm
     */
    public TParm updateBSCInfo(TParm saveParm) {
        TParm basinfoParm = new TParm((Map) saveParm.getData("bscinfoParm"));
        TParm icdParm = new TParm((Map) saveParm.getData("icdParm"));
        TParm optParm = new TParm((Map) saveParm.getData("optParm"));
        Map basicInfo = (Map) saveParm.getData("basicInfo");
        TParm result = new TParm();
        TConnection connection = getConnection();
        //��׼����
        result = BscInfoTool.getInstance().update(basinfoParm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        result = this.deleteICD(basinfoParm.getValue("CLNCPATH_CODE"),
                                connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        result = insertICD(basicInfo, icdParm, basinfoParm, optParm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * ɾ�����
     * @param clncPathCode String
     * @param connection TConnection
     * @return TParm
     */
    private TParm deleteICD(String clncPathCode, TConnection connection) {
        TParm result = new TParm();
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("DELETE FROM CLP_CLNCPATHERDIAGICD WHERE CLNCPATH_CODE='" +
                     clncPathCode + "'");
        result = new TParm(TJDODBTool.getInstance().update(sqlbf.toString(),
                connection));
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        sqlbf.delete(0, sqlbf.length());
        sqlbf.append("DELETE FROM CLP_CLNCPATHEROPTICD WHERE CLNCPATH_CODE='" +
                     clncPathCode + "'");
        result = new TParm(TJDODBTool.getInstance().update(sqlbf.toString(),
                connection));
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        return result;
    }
    /**
     * �޶�
     * liling 20140909 add
     */
   public TParm onRevise(TParm parm){
	   TParm result = new TParm();
       TConnection connection = getConnection();
     //1����·��ʹ��״̬Ϊ�޶���
       result = new TParm(TJDODBTool.getInstance().update(
    		   "UPDATE CLP_BSCINFO SET CLP_STATUS ='1'  WHERE CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"' ",connection));
       if (result.getErrCode() < 0) {
          	connection.rollback();
              connection.close();
              return result;
          }
     //����ǰʹ�õ��ٴ�·��ҽ�� ��ӵ���ʷ��,��ʼʱ��Ϊ��ǰʱ�䣬����ʱ��Ϊ99991231235959
       Map tableMap = (Map) parm.getData("CLP_PACK");
       TParm clpPack =  new TParm(tableMap);
//       TParm clpPack=(TParm) parm.getData("CLP_PACK");// java.util.HashMap cannot be cast to com.dongyang.data.TParm
       for(int i=0;i<clpPack.getCount();i++){
    	   result =  BscInfoTool.getInstance().insertPackHistory(clpPack.getRow(i),connection);
    	   if (result.getErrCode() < 0) {
           	connection.rollback();
            connection.close();
            return result;
           }
		}
     //3�޸���ʷ���е�ǰʹ�õ��ٴ�·��ҽ������ʱ��
       result = new TParm(TJDODBTool.getInstance().update(
    		   "UPDATE CLP_PACK_HISTORY  SET END_DATE ='"+parm.getData("END_DATE")+"'  " +
    		   		"WHERE VERSION='"+parm.getData("VERSION") +"' AND CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"'  ",connection));
       if (result.getErrCode() < 0) {
       	connection.rollback();
           connection.close();
           return result;
       }
       connection.commit();
       connection.close();
       return result;
   }
   /**
    * ����
    * liling 20140909 add
    */
   public TParm onRelease(TParm parm){
	   TParm result = new TParm();
       TConnection connection = getConnection();
     //ɾ����ǰ����ʹ�õ�ҽ������
       result = new TParm(TJDODBTool.getInstance().update(
    		   "DELETE FROM CLP_PACK WHERE CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"' AND VERSION='"+parm.getData("VERSION") +"' ",connection));
       if (result.getErrCode() < 0) {
          	connection.rollback();
              connection.close();
              return result;
          }
     //���Ѿ��޶�����ʷ��ҽ���滻����ʹ�õ��ٴ�·����Ŀҽ�� ����ʷ���а汾����=0��ҽ����ӵ�CLP_ PACK��
       Map tableMap = (Map) parm.getData("CLP_PACK");
       TParm clpPack =  new TParm(tableMap);
//       TParm clpPack=(TParm) parm.getData("CLP_PACK");// java.util.HashMap cannot be cast to com.dongyang.data.TParm
       for(int i=0;i<clpPack.getCount();i++){
    	   clpPack.setData("VERSION", i,parm.getData("VERSION_NEW"));
    	   result =  BscInfoTool.getInstance().insertPack(clpPack.getRow(i),connection);
    	   if (result.getErrCode() < 0) {
           	connection.rollback();
               connection.close();
               return result;
           }
		}
     //ͬʱͬ����ǰ�汾����(CLP_PACK��ǰ�汾����+1)parm.getData("VERSION_NEW")
       result = new TParm(TJDODBTool.getInstance().update(
    		   "UPDATE CLP_PACK_HISTORY SET VERSION='"+parm.getData("VERSION_NEW")+"' WHERE CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"' AND VERSION='0' ",connection));
       if (result.getErrCode() < 0) {
          	connection.rollback();
              connection.close();
              return result;
          }
     //ͬʱͬ����ǰ�汾����(CLP_MANAGEM��ǰ�汾����+1)parm.getData("VERSION_NEW")
       result = new TParm(TJDODBTool.getInstance().update(
    		   "UPDATE CLP_MANAGEM SET VERSION='"+parm.getData("VERSION_NEW")+"' WHERE CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"'  ",connection));
       if (result.getErrCode() < 0) {
          	connection.rollback();
              connection.close();
              return result;
          }
       result = new TParm(TJDODBTool.getInstance().update(
    		   "UPDATE CLP_BSCINFO SET VERSION='"+parm.getData("VERSION_NEW")+"',CLP_STATUS='2' WHERE CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"'",connection));
       if (result.getErrCode() < 0) {
          	connection.rollback();
              connection.close();
              return result;
          }
       result = new TParm(TJDODBTool.getInstance().update(
    		   "UPDATE CLP_BSCINFO_HISTORY SET VERSION='"+parm.getData("VERSION_NEW")+"' WHERE CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"' ",connection));
       if (result.getErrCode() < 0) {
          	connection.rollback();
              connection.close();
              return result;
          }
          connection.commit();
          connection.close();
          return result;
      }
   /**
    * ����
    * liling 20140910 add
    * �ٴ������Ժ󣬴���һ����ʷ���ݣ���ʼʱ��Ϊ��ǰʱ�䣬����ʱ��Ϊ99991231235959
	* CLP_BSCINFO_HISTORY ����ǰʹ�õ��ٴ�·���Ľ���ʱ���޸�Ϊ��ǰʱ�䣬�ٴ���һ���µ����ݣ��汾���벻��
	* UPDATE CLP_BSCINFO_HISTORY  SET END_DATE =XXX WHERE �汾��=XXX AND XXX
    */
   public TParm onUse(TParm parm) {
       TParm result = new TParm();
       TConnection connection = getConnection();
     //��׼��ʷ��liling 20140827 add
       result = BscInfoTool.getInstance().insertHis(parm, connection);
       if (result.getErrCode() < 0) {
           connection.rollback();
           connection.close();
           return result;
       }
       //�ٴ�·������ ����ACTIVE_FLGΪY
       TParm useParm = new TParm((Map) parm.getData("USE_PARM"));
       result = BscInfoTool.getInstance().onUse(useParm,connection);
       if (result.getErrCode() < 0) {
       	connection.rollback();
           connection.close();
           return result;
       }
       connection.commit();
       connection.close();
       return result;
   }
   /**
    * ͣ��
    * liling 20140910 add
    * ���ù�ѡȥ����
    */
   public TParm onStop(TParm parm) {
       TParm result = new TParm();
       TConnection connection = getConnection();
       //�ٴ�·������ ����ACTIVE_FLGΪN
       TParm useParm = new TParm((Map) parm.getData("USE_PARM"));
       result = BscInfoTool.getInstance().onUse(useParm,connection);
       if (result.getErrCode() < 0) {
          	connection.rollback();
              connection.close();
              return result;
          }
       //��ʷ���и��ݰ汾�Ų�ѯ���ݣ�����ʱ���޸�Ϊ��ǰʱ�䣬  ����ACTIVE_FLGΪN����
       result = BscInfoTool.getInstance(). updateBscHis(parm, connection);
//       result = new TParm(TJDODBTool.getInstance().update(
//    		   "UPDATE CLP_BSCINFO_HISTORY  SET END_DATE ='"+parm.getData("END_DATE")
//    		   +"',ACTIVE_FLG ='N'  WHERE CLNCPATH_CODE='"+parm.getData("CLNCPATH_CODE")+"'  AND VERSION='"+parm.getData("VERSION")
//    		   +"'  AND START_DATE='"+parm.getData("START_DATE")+"' ",connection));
       if (result.getErrCode() < 0) {
       	connection.rollback();
           connection.close();
           return result;
       }
       connection.commit();
       connection.close();
       return result;
   }
    /**
     * ҽ���ײ� ������� ��������� ���ݱ��浽��ʷ��
     * liling 20140909 add
     */
    public TParm savePackHis(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
//        System.out.println("action parm++"+parm.getData());
        result = PackTool.getInstance().savePackHis(parm, connection);
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * �����ٴ�·����ʷ��
     * @param parm TParm
     * @return TParm
     * liling 20140829 add
     */
    public TParm insertBSCInfoHis(TParm parm) {
    	 TParm result = new TParm();
    	  TConnection connection = getConnection();
    	 //��׼��ʷ��liling 20140827 add
         result = BscInfoTool.getInstance().insertHis(parm, connection);
         if (result.getErrCode() < 0) {
             connection.rollback();
             connection.close();
             return result;
         }
         connection.commit();
         connection.close();
         return result;
    }
    /**
     * ҽ���ײ� ɾ������  ����ʷ��ɾ������
     * liling 20140909 add
     */
    public TParm deletePackHis(TParm parm) {
    	TParm result = new TParm();
        TConnection connection = getConnection();
//        System.out.println("action delete++"+parm.getData());
        result = BscInfoTool.getInstance().deletePackHis(parm, connection);
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * ҽ���ײ� �ؼ�������Ŀ ����ƻ�
     * @param table TTable �б�
     * @param parm TParm ���б�����
    
    public TParm savePack(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        // ���ñ�־ �汾
        Map bscInfoMap = (Map) parm.getData("CLP_BSCINFO");
        TParm bscInfoParm = new TParm(bscInfoMap);
        String activeFlg = bscInfoParm.getValue("ACTIVE_FLG");
        // ����״̬ΪYʱ����ҽ���ײͲ�����ʷ�����µ�ǰҽ���ײͣ��汾�ż�1������ǰ�ٴ�·���汾�ż�1�ҽ���
        if ("Y".equals(activeFlg)) {
            // 2011-07-22 del��ҽ���ײͲ�����ʷ�� 2011-07-22 del
            // ɾ��ʱ��clppack ��Ӧ���ٴ�·����������Ϣȫ��������ʷ�� 2011-07-22 luhai �޸�
            result = PackTool.getInstance().insertPackHistoryBatch(parm,
                    connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            // ���µ�ǰҽ���ײͣ��汾�ż�1��
            int version = Integer.parseInt(bscInfoParm.getValue("VERSION")) + 1;
            parm.setData("VERSION", version);
            //ɾ��ʱ�����༭��ʱ�̺�orderType ��Ӧ������ɾ���������Ĳ��������Ǻ���ͳһ���°汾��
            //��ֻ֤Ҫ��׼���ݱ䶯���ͽ�һ����������Ϣ������ʷ���У�����չ��ʱ����
            result = PackTool.getInstance().deletePackBatch(parm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            //����������Ҳ�ǽ��༭ʱ�̺�orderFlg��Ӧ�����ݲ��룬������orderFlg�����в���
            result = PackTool.getInstance().insertPackBatch(parm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            // �汾ͬ������
            result = PackTool.getInstance().upgradeVersionForPack(parm,
                    connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            // ��ǰ�ٴ�·���汾�ż�1�ҽ���
            parm.setData("ACTIVE_FLG", "Y");
            result = BscInfoTool.getInstance().use(parm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        // ����״̬ΪNʱ�����µ�ǰҽ���ײͣ��汾�Ų��䣩
        if ("N".equals(activeFlg)) {
            // ���µ�ǰҽ���ײͣ��汾�Ų��䣩
            int version = Integer.parseInt(bscInfoParm.getValue("VERSION"));
            parm.setData("VERSION", version);
            result = PackTool.getInstance().deletePackBatch(parm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            result = PackTool.getInstance().insertPackBatch(parm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    } 
     public TParm deletePack(TParm sendParm) {
        TParm parm = new TParm((Map) sendParm.getData("saveMap"));
        // ���ñ�־ �汾
        Map bscInfoMap = (Map) sendParm.getData("CLP_BSCINFO");
        TParm bscInfoParm = new TParm(bscInfoMap);
        TParm result = new TParm();
        TConnection connection = getConnection();
        String activeFlg = bscInfoParm.getValue("ACTIVE_FLG");
        // ����״̬ΪY
        if ("Y".equals(activeFlg)) {
            //2011-07-22 ����begin
            // ɾ��ʱ��clppack ��Ӧ���ٴ�·����������Ϣȫ��������ʷ�� 2011-07-22 luhai �޸�
            result = PackTool.getInstance().insertPackHistoryBatch(sendParm,
                    connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            // ���µ�ǰҽ���ײͣ��汾�ż�1��
            int version = Integer.parseInt(bscInfoParm.getValue("VERSION")) + 1;
            sendParm.setData("VERSION", version);
            // ɾ��ҽ���ײ�
            result = PackTool.getInstance().delete(parm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            // �汾ͬ������
            result = PackTool.getInstance().upgradeVersionForPack(sendParm,
                    connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
            // ��ǰ�ٴ�·���汾�ż�1�ҽ���
            sendParm.setData("ACTIVE_FLG", "Y");
            result = BscInfoTool.getInstance().use(sendParm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        } else {
            // ɾ��ҽ���ײ�
            result = PackTool.getInstance().delete(parm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        //2011-07-22 ����end
        connection.commit();
        connection.close();
        return result;
    }

    */
}
