package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ҽʦ��ֵͳ�Ʊ�</p>
 *
 * <p>Description: ҽʦ��ֵͳ�Ʊ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-13
 * @version 4.0
 */
public class MRORT002ScoreTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static MRORT002ScoreTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static MRORT002ScoreTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MRORT002ScoreTool();
        return instanceObject;
    }

    public MRORT002ScoreTool() {
        this.setModuleName("mro\\MRORT002_ScoreModule.x");//���ڽ�sql�ŵ�������ִ�У���module��ʱ�����ˡ�
        this.onInit();
    }
    
    /**
     * ��ѯ��Ժ����ͳ������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOUT(TParm parm){//add by wanglong
        String sql =
                "SELECT DISTINCT A.REGION_CODE,A.IN_DEPT DEPT_CODE,A.IN_STATION STATION_CODE,A.VS_DR_CODE DR_CODE,A.MR_NO,A.IPD_NO,A.PAT_NAME,"
                        + " NVL((SELECT SUM(C.DEDUCT_SCORE * C.DEDUCT_SCORECOUNT) AS DEDUCT_SCORE "
                        + "        FROM MRO_QLAYCONTROLM B, MRO_CHRTVETREC C "
                        + "       WHERE A.OUT_DATE IS NOT NULL "
                        + "         AND A.CASE_NO = B.CASE_NO(+) "
                        + "         AND B.CASE_NO = C.CASE_NO "
                        + "         AND B.EXAMINE_CODE = C.EXAMINE_CODE "
                        + "         AND B.QUERYSTATUS = '1' "// �������
                        + "         AND B.STATUS = '0' "// δͨ��
                        + "    GROUP BY A.MR_NO, A.IPD_NO), 0 ) AS SCORE "
                        + "  FROM MRO_RECORD A "
                        + " WHERE A.OUT_DATE IS NOT NULL "
                        + "   AND A.OUT_DATE BETWEEN TO_DATE( '#', 'YYYYMMDD') AND TO_DATE( '#' || '235959', 'YYYYMMDDHH24MISS') ";
        sql = sql.replaceFirst("#", parm.getValue("DATE_S"));
        sql = sql.replaceFirst("#", parm.getValue("DATE_E"));
        if (!StringUtil.isNullString(parm.getValue("REGION_CODE"))) {
            sql += " AND A.REGION_CODE='" + parm.getValue("REGION_CODE") + "' ";
        }
        if (!StringUtil.isNullString(parm.getValue("DEPT"))) {
            sql += " AND A.IN_DEPT='" + parm.getValue("DEPT") + "' ";
        }
        if (!StringUtil.isNullString(parm.getValue("STATION"))) {
            sql += " AND A.IN_STATION='" + parm.getValue("STATION") + "' ";
        }
        if (!StringUtil.isNullString(parm.getValue("VS_CODE"))) {
            sql += " AND A.VS_DR_CODE='" + parm.getValue("VS_CODE") + "' ";
        }
        sql += " ORDER BY A.REGION_CODE, A.IN_DEPT, A.IN_STATION, A.VS_DR_CODE ";
        // System.out.println("===========��ѯ��Ժ�����ܿ۷�===============" + sql);
	    TParm result=new TParm(TJDODBTool.getInstance().select(sql));
        // TParm result = this.query("selectOUT",parm);
        // �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
			return result;
		}
        return result;
    }
    
    /**
     * ��ѯ��Ժ����ͳ������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectIN(TParm parm){//add by wanglong
        String sql =
                "SELECT DISTINCT A.REGION_CODE,A.IN_DEPT DEPT_CODE,A.IN_STATION STATION_CODE,A.VS_DR_CODE DR_CODE,A.MR_NO,A.IPD_NO,A.PAT_NAME,"
                        + " NVL((SELECT SUM(C.DEDUCT_SCORE * C.DEDUCT_SCORECOUNT) AS DEDUCT_SCORE "
                        + "        FROM MRO_QLAYCONTROLM B, MRO_CHRTVETREC C "
                        + "       WHERE A.OUT_DATE IS NULL "
                        + "         AND A.CASE_NO = B.CASE_NO(+) "
                        + "         AND B.CASE_NO = C.CASE_NO "
                        + "         AND B.EXAMINE_CODE = C.EXAMINE_CODE "
                        + "         AND B.QUERYSTATUS = '1' "// �������
                        + "         AND B.STATUS = '0' "// δͨ��
                        + "    GROUP BY A.MR_NO, A.IPD_NO), 0 ) AS SCORE "
                        + "  FROM MRO_RECORD A "
                        + " WHERE A.IN_DATE IS NULL "
                        + "   AND A.IN_DATE BETWEEN TO_DATE( '#', 'YYYYMMDD') AND TO_DATE( '#' || '235959', 'YYYYMMDDHH24MISS') ";
        sql = sql.replaceFirst("#", parm.getValue("DATE_S"));
        sql = sql.replaceFirst("#", parm.getValue("DATE_E"));
        if (!StringUtil.isNullString(parm.getValue("REGION_CODE"))) {
            sql += " AND A.REGION_CODE='" + parm.getValue("REGION_CODE") + "' ";
        }
        if (!StringUtil.isNullString(parm.getValue("DEPT"))) {
            sql += " AND A.IN_DEPT='" + parm.getValue("DEPT") + "' ";
        }
        if (!StringUtil.isNullString(parm.getValue("STATION"))) {
            sql += " AND A.IN_STATION='" + parm.getValue("STATION") + "' ";
        }
        if (!StringUtil.isNullString(parm.getValue("VS_CODE"))) {
            sql += " AND A.VS_DR_CODE='" + parm.getValue("VS_CODE") + "' ";
        }
        sql += " ORDER BY A.REGION_CODE, A.IN_DEPT, A.IN_STATION, A.VS_DR_CODE ";
        // System.out.println("===========��ѯ��Ժ�����ܿ۷�===============" + sql);
	    TParm result=new TParm(TJDODBTool.getInstance().select(sql));
        // TParm result = this.query("selectIN",parm);
        // �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
			return result;
		}
        return result;
    }

}
