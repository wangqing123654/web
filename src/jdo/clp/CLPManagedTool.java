package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import java.util.Map;
import com.dongyang.jdo.TJDODBTool;
import java.util.List;
import com.dongyang.db.TConnection;

/**
 * <p>Title: �ٴ�·��չ����׼</p>
 *
 * <p>Description: �ٴ�·��չ����׼</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPManagedTool extends TJDOTool {
    /**
     * ��ʼ��
     */
    public CLPManagedTool() {
        setModuleName("clp\\CLPManagedModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    public static CLPManagedTool instanceObject;

    /**
     * �õ�ʵ��
     * @return CLPManagedTool
     */
    public static CLPManagedTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPManagedTool();
        return instanceObject;
    }

    /**
     * ��ѯ������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectStation(TParm parm) {
        TParm result = this.query("selectStation", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectPatient(TParm parm) {
    	String sql="SELECT 'N' AS STATUS,A.DEPT_CODE,A.MR_NO, A.CASE_NO,A.IPD_NO,A.CLNCPATH_CODE,A.STATION_CODE," +
    			"TO_CHAR(A.IN_DATE,'YYYYMMDDHH24MISS') ,B.PAT_NAME,A.BED_NO ,A.REGION_CODE,C.VERSION,'' OPEN_UP "+
				   "FROM ADM_INP A,SYS_PATINFO B,CLP_MANAGEM C  "+
				   "WHERE A.CASE_NO=C.CASE_NO AND A.CLNCPATH_CODE=C.CLNCPATH_CODE "+
				   "AND A.MR_NO=B.MR_NO AND A.REGION_CODE='"+parm.getValue("REGION_CODE")+"' "+
				   "AND A.CANCEL_FLG ='N' "+
				   "AND A.CLNCPATH_CODE IS NOT NULL ";
    	if (parm.getValue("AMD_TYPE").equals("1")) {
    		sql+=" AND A.DS_DATE IS NULL AND A.IN_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')";
		}else{
			sql+=" AND A.DS_DATE IS NOT NULL AND A.DS_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')";
		}
    	if (null!=parm.getValue("MR_NO") &&parm.getValue("MR_NO").length()>0) {
    		sql+=" AND A.MR_NO='"+parm.getValue("MR_NO")+"'";
		}
    	if (null!=parm.getValue("BED_NO") &&parm.getValue("BED_NO").length()>0) {
    		sql+=" AND A.BED_NO='"+parm.getValue("BED_NO")+"'";
		}
    	if (null!=parm.getValue("CLNCPATH_CODE") &&parm.getValue("CLNCPATH_CODE").length()>0) {
    		sql+=" AND A.CLNCPATH_CODE='"+parm.getValue("CLNCPATH_CODE")+"'";
		}
    	if (null!=parm.getValue("DEPT_CODE") &&parm.getValue("DEPT_CODE").length()>0) {
    		sql+=" AND A.DEPT_CODE='"+parm.getValue("DEPT_CODE")+"'";
		}
    	if (null!=parm.getValue("STATION_CODE") &&parm.getValue("STATION_CODE").length()>0) {
    		sql+=" AND A.STATION_CODE='"+parm.getValue("STATION_CODE")+"'";
		}
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �õ�������ҽ����Ϣ
     * @return TParm
     */
    public TParm selectPatientOrderInfo(TParm parm) {
        TParm result = this.query("selectPatientOrderCodeList", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ݲ�����ѯ������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectPatientByStationCodeList(TParm parm) {
        TParm result;
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(" SELECT A.MR_NO, A.CASE_NO,A.CLNCPATH_CODE,TO_CHAR(A.IN_DATE,'YYYYMMDDHH24MISS') ");
        sqlbf.append(",(SELECT B.PAT_NAME FROM SYS_PATINFO B WHERE B.MR_NO = A.MR_NO ),A.BED_NO,A.REGION_CODE ");
        sqlbf.append(" FROM ADM_INP A ");
        sqlbf.append(" WHERE A.REGION_CODE=  '" + parm.getData("REGION_CODE") +
                     "' ");
        sqlbf.append(" AND A.DS_DATE IS NULL ");
        sqlbf.append(" AND A.CANCEL_FLG ='N' ");
        sqlbf.append(" AND A.CLNCPATH_CODE IS NOT NULL ");
        List stationList = (List) parm.getData("StationList");
        if (stationList.size() > 0) {
            StringBuffer stationbfSql = new StringBuffer();
            sqlbf.append(" AND STATION_CODE IN (");
            for (Object stationCode : stationList) {
                String stationCodeStr = (String) stationCode;
                stationbfSql.append("'" + stationCodeStr + "',");
            }
            String stationsql = stationbfSql.toString().substring(0,
                    (stationbfSql.toString().length() - 1));
            sqlbf.append(stationsql);
            sqlbf.append("  ) ");
        }
        Map mapresult = TJDODBTool.getInstance().select(sqlbf.toString());
        result = new TParm(mapresult);
        return result;
    }

    /**
     * ɾ���ٴ�·����չ����ʷ����
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteCLPManaged(TParm parm, TConnection conn) {
        TParm result = this.update("deleteManaged", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public TParm deleteCLPBill(TParm parm, TConnection conn) {
        TParm result = this.update("deleteCLPBill", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����case_no �� clp_pack ����clpManaged��
     * @param parm TParm
     * @return TParm
     */
    public TParm inserttCLPManagedWithCLPPack(TParm parm, TConnection conn) {
        TParm result = this.update("insertCLPManagedWithCLPPack", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��cplPack�и���case_no ���ҳ���Ӧ���˵���Ϣ׼������clp_managed
     * @param parm TParm
     * @return TParm
     */
    public TParm selectManagedInfoFromCLPPack(TParm parm) {
        TParm result = this.query("selectCLPManaedFromCLPPack", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��cplPack�и���case_no ���ҳ���Ӧ���˵���Ϣ׼������clp_managed
     * @param parm TParm
     * @return TParm
     */
    public TParm selectManagedInfoFromCLPPackWithVersion(TParm parm) {
        TParm result = this.query("selectCLPManaedFromCLPPackWithVersion", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ��IBS_ORDD�в鴦��Ӧ���˵���Ϣ׼������clp_managed
     * @param parm TParm
     * @return TParm
     */
    public TParm selectManagedInfoFromIBSOrdd(TParm parm) {
        TParm result = this.query("selectCLPManaedFromIBSOrdd", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��IBS_ORDD�в鴦��һ�β���û�л�õ�ҽ����Ӧ���˵���Ϣ׼������clp_managed
     * @param parm TParm
     * @return TParm
     * =======pangben 2012-7-6
     */
    public TParm selectCLPManaedFromIBSOrddTwo(TParm parm) {
        TParm result = this.query("selectCLPManaedFromIBSOrddTwo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ���ibs_ordd �еĶ�Ӧ������clp_managed���Ƿ����
     * @param case_no String
     * @return boolean
     * mediUnit ��ҩ��λ
     * QTY ��ҩ����
     */
    public boolean checkIBSOrddExistInCLPManaged(String case_no,
                                                 String clncPathCode,
                                                 String schdCode,
                                                 String orderCode,
                                                 String mediUnit, String QTY) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", case_no);
        parm.setData("CLNCPATH_CODE", clncPathCode);
        parm.setData("SCHD_CODE", schdCode);
        parm.setData("ORDER_CODE", orderCode);
        parm.setData("MEDI_UNIT", mediUnit); //�ж��������뿪ҩ��λ
        parm.setData("QTY", QTY); //�ж��������뿪ҩ����
        TParm result = this.query("checkIBSOrddExistInCLPManaged", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        if (result.getInt("TOTALCOUNT", 0) > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ���� clpManaged from IBSOrdd
     * @param parm TParm
     * @return TParm
     */
    public TParm insertCLPManagedWithIBSOrdd(TParm parm, TConnection conn) {
        TParm result = this.update("insertCLPManagedWithIBSOrdd", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���� clpManaged  from IBSOrdd
     * @param parm TParm
     * @return TParm
     */
    public TParm updateCLPManagedWithIBSOrdd(TParm parm, TConnection conn) {
        TParm result = this.update("updateCLPManagedWithIBSOrdd", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���� clpManaged  from IBSOrdd
     * @param parm TParm
     * @return TParm
     * ==========pangben 2012-7-6 ����ҽ��û��ϸ�� ����ʾ���� ����
     */
    public TParm updateCLPManagedWithIBSOrddOrderSet(TParm parm, TConnection conn) {
        TParm result = this.update("updateCLPManagedWithIBSOrddOrderSet", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ���ҳ���Ҫ���µ�managed�����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm getUpdateDataInCLPManagedWithPatienInfo(TParm parm) {
        TParm result = this.query("getUpdateDataInCLPManagedWithPatienInfo",
                                  parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ݲ�����Ϣɾ��չ����Ϣ - ·����ԭ
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteCLPManagedWithPatientInfo(TParm parm, TConnection conn) {
        TParm result = this.update("deleteCLPManagedWithPatientInfo", parm,
                                   conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ݲ�����Ϣ����չ����Ϣ - ·����ԭ
     * @param parm TParm
     * @return TParm
     */
    public TParm updateCLPManagedWithPatientInfo(TParm parm, TConnection conn) {
        TParm result = this.update("updateCLPManagedWithPatientInfo", parm,
                                   conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����managed�еķ�����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectCLPManagedWithCondition(TParm parm, TConnection conn) {
        TParm result = this.query("selectCLPManagedWithCondition", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����order_code ���Ҷ�Ӧ�ķ���
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOwnPrice(TParm parm, TConnection conn) {
        TParm result = this.query("selectOwnPrice", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����CLPBill
     * @param parm TParm
     * @return TParm
     * ====================pangben 2012-5-24 �޸�
     */
    public TParm saveCLPBill(TParm parm, TConnection conn,double [] sum,String []sqlName,String []sqlTotName,String schdType,String schdCode) {
        String sqlTotTemp="";
        double sumTemp=0.00;
        String sqlNameTemp="";
        //System.out.println("����CLPBill:::::::"+parm);
        //============pangben 2012-6-1 start 
    	if ("1".equals(schdType)) {
    		sqlTotTemp=sqlTotName[0];
    		sumTemp=sum[0];
    		sqlNameTemp=sqlName[0];
		}else{
			sqlTotTemp=sqlTotName[1];
			sumTemp=sum[1];
			sqlNameTemp=sqlName[1];
		}
    	 //============pangben 2012-6-1 stop
    	StringBuffer sqlbf = new StringBuffer();
    	//��ÿ�ʼʱ����㵱ǰ����
    	TParm startDateParm=new TParm(TJDODBTool.getInstance().select("SELECT SCHD_DAY FROM CLP_THRPYSCHDM_REAL WHERE CASE_NO='" 
    			+ parm.getValue("CASE_NO") + "' AND CLNCPATH_CODE='"+parm.getValue("CLNCPATH_CODE")+"' AND SCHD_CODE='"+schdCode+"'"));
    	if (startDateParm.getErrCode()<0) {
			return startDateParm;
		}
        sqlbf.append("INSERT INTO CLP_BILL (CASE_NO,CLNCPATH_CODE,SCHD_TYPE,SCHD_CODE,START_DAY,REGION_CODE," +
        		sqlNameTemp + "OPT_USER,OPT_DATE,OPT_TERM");
        sqlbf.append(",TOT)VALUES(");
        sqlbf.append("'" + parm.getValue("CASE_NO") + "',");
        sqlbf.append("'" + parm.getValue("CLNCPATH_CODE") + "',");
        sqlbf.append("'" + parm.getValue("SCHD_TYPE") + "',");
        sqlbf.append("'" + schdCode + "',");
        sqlbf.append(startDateParm.getInt("SCHD_DAY",0)+",");
        sqlbf.append("'" + parm.getValue("REGION_CODE") + "',");
        sqlbf.append("" + sqlTotTemp+" ");
        sqlbf.append("'" + parm.getValue("OPT_USER") + "',");
        sqlbf.append("TO_DATE('" + parm.getValue("OPT_DATE") + "','YYYYMMDD'),");
        sqlbf.append("'" + parm.getValue("OPT_TERM") + "',");
        sqlbf.append(sumTemp);
        sqlbf.append(")");
        TParm result = new TParm(TJDODBTool.getInstance().update(sqlbf.toString(), conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
      //============pangben 2012-5-24 start 
//        } else {
//            conn.commit();
//        }
//        //�����ܼ�
//        result = updateTot(parm, conn);
//        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//                result.getErrName());
//        } else {
//            conn.commit();
//        }
      //============pangben 2012-5-24 stop 
        return result;
    }

    private TParm updateTot(TParm parm, TConnection conn) {
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("SELECT * FROM CLP_BILL WHERE CASE_NO='" +
                     parm.getValue("CASE_NO") + "' AND ");
        sqlbf.append(" CLNCPATH_CODE = '" + parm.getValue("CLNCPATH_CODE") +
                     "' AND ");
        sqlbf.append(" SCHD_TYPE='" + parm.getValue("SCHD_TYPE") + "' AND ");
        sqlbf.append(" SCHD_CODE='" + parm.getValue("SCHD_CODE") + "' ");
        Map mapresult = TJDODBTool.getInstance().select(sqlbf.toString());
        TParm result = new TParm(mapresult);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        double tot = 0;
        if (result.getCount() > 0) {
            TParm rowParm = result.getRow(0);
            String[] columns = rowParm.getNames();
            for (String colstr : columns) {
                if (colstr.toLowerCase().indexOf("rexp") >= 0) {
                    tot += rowParm.getDouble(colstr);
                }
            }
        }
        sqlbf.delete(0, sqlbf.length());
        sqlbf.append("UPDATE CLP_BILL SET TOT=" + tot + " WHERE  CASE_NO='" +
                     parm.getValue("CASE_NO") + "' AND ");
        sqlbf.append(" CLNCPATH_CODE = '" + parm.getValue("CLNCPATH_CODE") +
                     "' AND ");
        sqlbf.append(" SCHD_TYPE='" + parm.getValue("SCHD_TYPE") + "' AND ");
        sqlbf.append(" SCHD_CODE='" + parm.getValue("SCHD_CODE") + "' ");
        mapresult = TJDODBTool.getInstance().update(sqlbf.toString(), conn);
        result = new TParm(mapresult);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        } else {
            conn.commit();
        }
        return result;

    }

    /**
     * ����CLPBill
     * @param parm TParm
     * @return TParm
     */
    public TParm updateCLPBill(TParm parm, TConnection conn,double []sum, String []sqlName,String type,String schdCode) {
        StringBuffer sqlbf = new StringBuffer();
        double totAmt=0.00;
        String sqlNameTemp="";
        //============pangben 2012-6-1 start
        if("1".equals(type)){
        	totAmt=sum[0];
        	sqlNameTemp=sqlName[0];
        }else{
        	totAmt=sum[1];
        	sqlNameTemp=sqlName[1];
        }
      //============pangben 2012-6-1 stop
        sqlbf.append("UPDATE CLP_BILL SET TOT="+totAmt+sqlNameTemp +" WHERE  CASE_NO='" + parm.getValue("CASE_NO") +
                     "' AND ");
        sqlbf.append(" CLNCPATH_CODE = '" + parm.getValue("CLNCPATH_CODE") +
                     "' AND ");
        sqlbf.append(" SCHD_TYPE='" + parm.getValue("SCHD_TYPE") + "' AND ");
        sqlbf.append(" SCHD_CODE='" + schdCode + "' ");
        //system.out.println("updateCLPBill::SQL::::"+sqlbf.toString());
        TParm result = new TParm(TJDODBTool.getInstance().update(sqlbf.toString(),conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //============pangben 2012-5-24 start 
        //�����ܼ�
       // result = updateTot(parm, conn);
//        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//                result.getErrName());
//        } else {
//            conn.commit();
//        }
      //============pangben 2012-5-24 stop 
        return result;
    }

    /**
     * CLPBill ����Ƿ����
     * @param parm TParm
     * @return TParm
     */
    public TParm checkCLPBillExist(TParm parm) {
        TParm result = this.query("checkCLPBillExist", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �õ�Ҫ���µ���
     * @param parm TParm
     * @return TParm
     * 
     */
    public TParm getCLPBillColumName(TParm parm) {
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("SELECT CASE ");
        for (int i = 1; i <= 9; i++) {
            sqlbf.append(" WHEN OPD_CHARGE_CODE=(SELECT  CHARGE0" + i +
                         " FROM BIL_RECPPARM WHERE ROWNUM <2 ) THEN  'REXP_0" +
                         i + "'");
        }
        for (int i = 10; i <= 30; i++) {
            sqlbf.append(" WHEN OPD_CHARGE_CODE=(SELECT  CHARGE" + i +
                         " FROM BIL_RECPPARM WHERE ROWNUM <2 ) THEN  'REXP_" +
                         i + "'");
        }
        sqlbf.append(" END AS COLUMNNAME FROM  ");
        sqlbf.append(" (SELECT OPD_CHARGE_CODE FROM SYS_CHARGE_HOSP WHERE ");
        sqlbf.append(
                " CHARGE_HOSP_CODE IN(SELECT CHARGE_HOSP_CODE FROM SYS_FEE WHERE ORDER_CODE='" +
                parm.getValue("ORDER_CODE") + "') )A ");
        Map mapresult = TJDODBTool.getInstance().select(sqlbf.toString());
        TParm result = new TParm(mapresult);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �ж�����order��orderERType�����Ƿ�ʵ�ʶ�Ӧ��orderCode ����ͬһ�ٴ�·����Ŀ��QTY ��ͬ��UNIT��ͬ��
     * @return TParm
     */
    public boolean checkOrderIsValid(String orderCode1, String orderCode2) {
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(" SELECT COUNT(*) AS TOTAL FROM ");
        sqlbf.append(
                "(SELECT ORDTYPE_CODE,CLP_UNIT,CLP_QTY FROM CLP_ORDERTYPE WHERE ORDER_CODE='" +
                orderCode1 + "')A, ");
        sqlbf.append(
                "(SELECT ORDTYPE_CODE,CLP_UNIT,CLP_QTY FROM CLP_ORDERTYPE WHERE ORDER_CODE='" +
                orderCode2 + "')B ");
        sqlbf.append(" WHERE A.ORDTYPE_CODE=B.ORDTYPE_CODE  ");
        sqlbf.append(
                " AND (A.CLP_UNIT=B.CLP_UNIT OR A.CLP_UNIT IS NULL AND B.CLP_UNIT IS NULL ) ");
        sqlbf.append(
                " AND (A.CLP_QTY=B.CLP_QTY OR A.CLP_QTY IS NULL AND B.CLP_QTY IS NULL ) ");
        Map mapresult = TJDODBTool.getInstance().select(sqlbf.toString());
        TParm result = new TParm(mapresult);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        if (result.getRow(0).getInt("TOTAL") > 0) {
            return true;
        } else {
            return false;
        }
    }


}
